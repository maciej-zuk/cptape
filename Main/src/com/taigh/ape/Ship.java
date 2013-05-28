package com.taigh.ape;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Ship implements ObservableCircle {

	Vector2 position;
	Vector2 velocity;
	float maxSpeed = 30000000.0f;
	float smokeSupply;
	private boolean canReset;
	public float health;
	public int boxesOnBoard;

	private class SmokeTrail {
		Vector2 pos;
		Vector2 vel;
		float tick;
		float scale;
		float maxTick;
		Sprite mySprite;

		public SmokeTrail() {
			pos = new Vector2();
			vel = new Vector2();
			smokeSupply = 0;
			reset();
			tick = 40;
		}

		void reset() {
			float angle = (float) (Math.random() - 0.5) * 80f;
			float speed = 10f + (float) (150f * Math.random());
			if (Math.random() > 0.5)
				mySprite = Assets.engineSmoke1;
			else
				mySprite = Assets.engineSmoke2;
			maxTick = 5f + 60f * (float) Math.random();
			pos.x = position.x;
			pos.y = position.y;
			vel.x = 0;
			vel.y = 1;
			vel.rotate(velocity.angle() + angle + 90);
			vel.nor();
			vel.scl(speed);
			tick = 0;
			scale = (float) (0.25 + Math.random() * 0.25f);
		}

		void draw() {
			tick++;
			if (tick > maxTick && canReset)
				reset();
			pos.x += vel.x * Assets.dt;
			pos.y += vel.y * Assets.dt;

			mySprite.setRotation(vel.angle() + 90);
			mySprite.setScale(scale * tick / 10.0f);
			mySprite.setColor(1f, 1f, 1f, 1f / tick);
			mySprite.setPosition(pos.x - mySprite.getWidth() / 2, pos.y - mySprite.getHeight() / 2);
			mySprite.draw(Assets.batch);
		}
	}

	interface reachCallback {
		void reached();
	}

	SmokeTrail trail[];

	public Ship() {
		position = new Vector2(14000, 14000);
		velocity = new Vector2();
		trail = new SmokeTrail[50];
		for (int i = 0; i < trail.length; i++) {
			trail[i] = new SmokeTrail();
		}
		health = 1;
		boxesOnBoard = 10;
	}

	public void draw() {
		float vel = velocity.len();
		// if (vel > maxSpeed) {
		// velocity.nor();
		// velocity.scl(maxSpeed);
		// }
		position.add(velocity.x * Assets.dt, velocity.y * Assets.dt);
		velocity.scl(1 - (vel * vel) / maxSpeed);

		Assets.playerShipSprite.setScale(0.25f);
		Assets.playerShipSprite.setPosition(position.x - Assets.playerShipSprite.getWidth() / 2, position.y
				- Assets.playerShipSprite.getHeight() / 2);
		if (vel > 10)
			Assets.playerShipSprite.setRotation(velocity.angle() - 90);
		if (smokeSupply < 20) {
			canReset = false;
		} else {
			canReset = true;
		}

		if (smokeSupply > 1) {
			for (int i = 0; i < trail.length; i++) {
				trail[i].draw();
			}
			smokeSupply -= 0.1;
		}

		Assets.playerShipSprite.draw(Assets.batch);

	}

	public void applyGravitation(PlanetsSystem system) {
		applyGravitation(system.planets);
	}

	public void applyGravitation(Planet[] planets) {
		float vx = 0, vy = 0;
		for (Planet planet : planets) {
			if (!Intersector.overlaps(planet.getCircle(), Assets.viewCircle))
				continue;
			float dx = planet.getPositionX() - position.x;
			float dy = planet.getPositionY() - position.y;
			float len = (float) Math.hypot(dx, dy);
			if (len < planet.getCircle().radius + 50) {
				health -= 0.1;
				dx = dx / len;
				dy = dy / len;
				float dot = Math.abs(velocity.x * dx + velocity.y * dy);
				float velocity1 = (float) Math.hypot(velocity.x, velocity.y);
				velocity.x = velocity.x - 2 * dot * dx;
				velocity.y = velocity.y - 2 * dot * dy;
				float velocity2 = (float) Math.hypot(velocity.x, velocity.y);
				velocity.scl(velocity1 / velocity2);
				for (int i = 0; i < 10; i++)
					Assets.fx.addBoom((float) (position.x + Assets.random.nextGaussian() * 50),
							(float) (position.y + Assets.random.nextGaussian() * 50));
				return;
			} else {
				float clen = planet.getPlanetSize() + 200;
				float slen = 1 / (len * len * len);
				dx *= slen;
				dy *= slen;
				if (len < clen - 5) {
					vx -= 30000 * dx * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
					vy -= 30000 * dy * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
				} else if (len > clen - 5 && len < clen + 50) {
					float sign = 1 - 2 * (planet.getId() & 1);
					vx -= velocity.x;
					vy -= velocity.y;
					vx += -sign * 30000 * dy * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
					vy += sign * 30000 * dx * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
				} else if (len > clen + 50) {
					vx += 30000 * dx * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
					vy += 30000 * dy * planet.getPlanetSize() * planet.getPlanetSize() * Assets.dt;
				}
			}
		}
		velocity.x += vx * Assets.dt;
		velocity.y += vy * Assets.dt;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	@Override
	public float getPositionX() {
		return position.x;
	}

	@Override
	public float getPositionY() {
		return position.y;
	}

	@Override
	public float getRadius() {
		return Assets.playerShipSprite.getHeight();
	}

}
