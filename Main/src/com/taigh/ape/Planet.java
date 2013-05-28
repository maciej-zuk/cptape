package com.taigh.ape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.taigh.ape.GameInputProcessor.InputReceiver;
import com.taigh.ape.Menu.MenuCallback;
import com.taigh.ape.Ship.reachCallback;

public class Planet implements InputReceiver, Disposable, ObservableCircle {

	enum state {
		empty, terraformed, terraforming, terraformingPaused, terraformingStart
	}

	class BoxReachesPlanet implements reachCallback {
		int sent = 0;

		@Override
		public void reached() {
			sent--;
			switch (currentState) {
			case empty:
				break;
			case terraformed:
				break;
			case terraformingPaused:
				break;
			case terraformingStart:
				currentState = state.terraforming;
			case terraforming:
				terraformedCount += 20;
				if (terraformedCount > terraformLimit * 3) {
					terraformedCount = terraformLimit * 3;
					menu.clearMenu();
					menu.addEntry(new ObservableString() {

						@Override
						public String getString() {
							return "trade (" + ((int) Math.ceil(Math.min(3f * terraformLimit / 20, wares))) + " pkgs)";
						}
					}, new MenuCallback() {
						@Override
						public boolean clicked() {
							final int amount = (int) Math.ceil(Math.min(3f * terraformLimit / 20, wares));
							wares -= amount;
							if (amount < 1)
								return false;
							Assets.fx.shootBox(Planet.this, Assets.playerShip, new reachCallback() {
								@Override
								public void reached() {
									Assets.playerShip.boxesOnBoard += amount;

								}
							});
							return false;
						}
					});
					menu.addEntry("repair tools", new MenuCallback() {
						@Override
						public boolean clicked() {
							if (Assets.playerShip.boxesOnBoard >= 40) {
								Assets.playerShip.boxesOnBoard -= 40;
								Assets.fx.shootBox(Planet.this, Assets.playerShip, new reachCallback() {
									@Override
									public void reached() {
										Assets.playerShip.health = 1;
									}
								});
							}
							return false;
						}
					});
					currentState = state.terraformed;
				} else {
					if (sent < 1) {
						if (Assets.playerShip.boxesOnBoard > 0) {
							Assets.playerShip.boxesOnBoard--;
							Assets.fx.shootBox(Assets.playerShip, Planet.this, this);
							sent++;
						} else {
							currentState = state.terraformingPaused;
							menu.clearMenu();
							menu.addEntry("continue terraformation", terraformCallback);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	BoxReachesPlanet cb;

	private class TerraformUnit {
		float alfa;
		int id;
		float random;
		float rotation;
		float scale;

		public TerraformUnit(float rotation, int id, float scale, float random, float alfa) {
			super();
			this.rotation = rotation;
			this.id = id;
			this.scale = scale;
			this.random = random;
			this.alfa = alfa;
		}
	}

	private state currentState = state.empty;

	public state getCurrentState() {
		return currentState;
	}

	PlanetMenu menu;

	private Color planetColor;
	private float posX, posY, radius;
	private float rotation;
	private int id;
	private float wares;

	public int getId() {
		return id;
	}

	public final Circle circle = new Circle();;

	private MenuCallback terraformCallback;

	private float terraformedCount = 0;

	private int terraformLimit;

	private TerraformUnit units[];

	public Planet(Color planetColor, float planetSize) {
		Game.giListener.registerReceiver(this);

		this.planetColor = planetColor;
		radius = planetSize;

		menu = new PlanetMenu(0.25f * planetSize, 0.75f * planetSize);

		radius = planetSize;

		posX = 0;
		posY = 0;
		id = Assets.random.nextInt();
		circle.set(posX, posY, radius);

		rotation = 0;
		cb = new BoxReachesPlanet();
		terraformLimit = (int) (planetSize / 10);

		// terraform units
		units = new TerraformUnit[terraformLimit * 3];
		for (int i = 0; i < 2 * terraformLimit; i++) {
			float scale = 0.4f + Assets.random.nextFloat();
			int id = 7 + Assets.random.nextInt(3);
			units[i] = new TerraformUnit(360 * Assets.random.nextFloat(), id, scale, Assets.random.nextFloat(), 0.f);
		}
		for (int i = 2 * terraformLimit; i < 3 * terraformLimit; i++) {
			float scale = 0.4f + Assets.random.nextFloat();
			int id = Assets.random.nextInt(10);
			units[i] = new TerraformUnit(360 * Assets.random.nextFloat(), id, scale, Assets.random.nextFloat(), 0.f);
		}

		// menu
		terraformCallback = new MenuCallback() {
			@Override
			public boolean clicked() {
				currentState = state.terraformingStart;
				menu.addEntry("cancel terraformation", new MenuCallback() {

					@Override
					public boolean clicked() {
						currentState = state.terraformingPaused;
						menu.addEntry("continue terraformation", terraformCallback);
						return true;
					}
				});
				return true;
			}
		};
		menu.addEntry("terraform (" + (int) Math.ceil(3f * terraformLimit / 20) + "pkgs)", terraformCallback);

	}

	@Override
	public void dispose() {
		Game.giListener.unregisterReceiver(this);
		menu.dispose();
	}

	public int getProgress() {
		return (int) (100 * terraformedCount / (terraformLimit * 3));
	}

	public int getWares() {
		return (int) wares;
	}

	public void draw() {

		// FSM
		switch (currentState) {
		case terraformingStart:
			currentState = state.terraforming;
			if (cb.sent < 1) {
				if (Assets.playerShip.boxesOnBoard > 0) {
					Assets.playerShip.boxesOnBoard--;
					Assets.fx.shootBox(Assets.playerShip, this, cb);
					cb.sent++;
				} else {
					currentState = state.terraformingPaused;
					menu.clearMenu();
					menu.addEntry("continue terraformation", terraformCallback);
				}
			}
		default:
			break;
		}

		for (int i = 0; i < (int) terraformedCount; i++) {
			float scale = radius * units[i].scale / 512.0f;
			Sprite plant = Assets.sprtPlants[units[i].id];
			float originShift = 0.95f * radius / scale;
			plant.setColor(planetColor.r, planetColor.g, planetColor.b, units[i].alfa);
			plant.setOrigin(plant.getOriginX(), -originShift);
			plant.setRotation(units[i].rotation + rotation * (0.5f + units[i].random));
			plant.setPosition(posX - plant.getOriginX(), posY - plant.getOriginY());
			plant.setScale(scale);
			plant.draw(Assets.batch);
			if (units[i].alfa < 0.5f)
				units[i].alfa += 0.001f;
		}
		Assets.planetSprite.setColor(planetColor);
		Assets.planetSprite.setScale(2 * radius);
		Assets.planetSprite.setPosition(posX, posY);
		Assets.batch.setShader(Assets.shaderCircle);
		Assets.planetSprite.draw(Assets.batch);
		Assets.batch.setShader(null);
		menu.draw();
	}

	public void tick() {
		if (currentState == state.terraformed && wares < 3f * terraformLimit / 20)
			wares += Assets.dt / 4;
	}

	public Color getPlanetColor() {
		return planetColor;
	}

	public float getPlanetSize() {
		return radius;
	}

	public float getRotation() {
		return rotation;
	}

	@Override
	public void onTouchDown() {

	}

	@Override
	public void onTouchUp() {
		menu.toggleVisibility();

	}

	public void rotate(float rotation) {
		this.rotation += rotation;
	}

	public void setColor(Color planetColor) {
		this.planetColor = planetColor;
	}

	public void setRadius(float planetSize) {
		radius = planetSize;
		circle.set(posX, posY, radius);
	}

	public void setPosition(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
		menu.setPos(posX, posY);
		circle.set(posX, posY, radius);
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public boolean receivesInput() {
		return true;
	}

	@Override
	public Rectangle getRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Circle getCircle() {
		return circle;
	}

	@Override
	public float getPositionX() {
		return posX;
	}

	@Override
	public float getPositionY() {
		return posY;
	}

	@Override
	public float getRadius() {
		return circle.radius;
	}

}
