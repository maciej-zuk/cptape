package com.taigh.ape;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SimpleFX {

	class Boom {
		float posX;
		float posY;
		float alfa;
		float random;
		public float life;

		public Boom(float posX, float posY) {
			this.posX = posX;
			this.posY = posY;
			life = Assets.random.nextFloat() * 5;
			alfa = Assets.random.nextFloat();
			random = Assets.random.nextFloat() - 0.5f;
		}

		void draw() {
			Assets.engineSmoke1.setRotation(random * life * 360 / 2);
			Assets.engineSmoke1.setScale(5 - life);
			Assets.engineSmoke1.setColor(1, 1, 1, alfa * life / 5);
			Assets.engineSmoke1.setPosition(posX, posY);
			Assets.engineSmoke1.draw(Assets.batch);
			life -= Assets.dt * 10;
		}
	}

	List<Boom> booms = new LinkedList<SimpleFX.Boom>();
	List<Boom> boomsDelete = new LinkedList<SimpleFX.Boom>();

	static class FloatingBox {
        interface BoxReachCallback {
            void reached();
        }

        float x;
		float y;
		float vx;
		float vy;
		float size;
		ObservableCircle target;
		boolean enabled = false;
        BoxReachCallback cb;

		void draw() {
			if (!enabled)
				return;
			float nvx = target.getPositionX() - x;
			float nvy = target.getPositionY() - y;
			float dist = (float) Math.hypot(nvx, nvy);
			if (dist < 40) {
				cb.reached();
				enabled = false;
				return;
			}
			nvx /= dist;
			nvy /= dist;
			if (dist < target.getRadius()) {
				size -= 0.005;
				if (size < 0) {
					enabled = false;
					cb.reached();
					return;
				}
			} else if (size < 0.5f)
				size += 0.005;
			vx += 10 * nvx * Assets.dt;
			vy += 10 * nvy * Assets.dt;
			float vel = (float) Math.hypot(vx, vy);
			vx /= vel;
			vy /= vel;
			if (vel > 30)
				vel = 30;
			vx *= vel;
			vy *= vel;
			x += 10 * vx * Assets.dt;
			y += 10 * vy * Assets.dt;
			Assets.boxSprite.setPosition(x, y);
			Assets.boxSprite.setScale(size);
			Assets.boxSprite.draw(Assets.batch);
		}

		public void shoot(ObservableCircle source, ObservableCircle target, BoxReachCallback cb) {
			vx = vy = 0;
			x = source.getPositionX() - Assets.boxSprite.getWidth() / 2;
			y = source.getPositionY();
			size = 0.3f;
			enabled = true;
			this.cb = cb;
			this.target = target;
		}
	}

	FloatingBox boxes[];

	public SimpleFX() {
		boxes = new FloatingBox[60];
		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new FloatingBox();
		}
	}

	void addBoom(float x, float y) {
		booms.add(new Boom(x, y));
	}

	public boolean shootBox(ObservableCircle source, ObservableCircle target, FloatingBox.BoxReachCallback cb) {
		for (FloatingBox box : boxes) {
			if (!box.enabled) {
				box.shoot(source, target, cb);
				return true;
			}
		}
		return false;
	}

	void draw() {
		for (Boom boom : booms) {
			boom.draw();
			if (boom.life < 0)
				boomsDelete.add(boom);
		}
		for (Boom boom : boomsDelete) {
			booms.remove(boom);
		}
		boomsDelete.clear();
		for (FloatingBox box : boxes) {
			box.draw();
		}
	}

    static void drawGuide() {
        float ringSize = Assets.camera.zoom * Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.22f;
        Assets.planetIcon.setScale(Assets.camera.zoom / 2);
        Assets.terraPlanetIcon.setScale(Assets.camera.zoom / 2);
        Assets.font.setScale(Assets.camera.zoom / 2);
        for (Planet planet : Assets.system.planets) {
            float dx, dy;
            float distx = planet.getPositionX() - Assets.camera.position.x;
            float disty = planet.getPositionY() - Assets.camera.position.y;
            float dist = distx * distx + disty * disty;
            Sprite icon = Assets.planetIcon;
            if (planet.getCurrentState() != Planet.state.empty)
                icon = Assets.terraPlanetIcon;
            float ph = (float) Math.atan2(disty, distx);
            float opacity = Math.min(1, 5000000f / dist);
            dx = (float) (Math.cos(ph) * (2 * ringSize - ringSize * opacity));
            dy = (float) (Math.sin(ph) * (2 * ringSize - ringSize * opacity));
            icon.setPosition(Assets.camera.position.x - icon.getWidth() / 2 + dx,
                    Assets.camera.position.y - icon.getHeight() / 2 + dy);
            icon.setColor(planet.getPlanetColor().r, planet.getPlanetColor().g, planet.getPlanetColor().b, opacity);
            icon.setScale(Assets.camera.zoom * planet.getRadius() / 1500);
            icon.draw(Assets.batch);
            if (planet.getCurrentState() == Planet.state.terraforming || planet.getCurrentState() == Planet.state.terraformingPaused) {
                String str = planet.getProgress() + "%";
                Assets.font.setScale(Assets.camera.zoom * 0.4f);
                BitmapFont.TextBounds bnds = Assets.font.getBounds(str);
                Assets.font.setColor(1, 1, 1, opacity);
                Assets.font.draw(Assets.batch, str, Assets.camera.position.x + dx - bnds.width / 2,
                        Assets.camera.position.y + dy + bnds.height / 2);
            }
        }
    }
    static void drawShields(float shieldLevel) {
        Assets.shieldFrame.setScale(Assets.camera.zoom);
        Assets.shieldFrame.setColor(1, 1, 1, 0.5f);
        Assets.shieldFill.setScale(Assets.camera.zoom * shieldLevel, Assets.camera.zoom);
        Assets.shieldFill.setColor(1, 1, 1, 0.75f);

        float offX = -Gdx.graphics.getWidth() / 2 + 10;
        float offY = Gdx.graphics.getHeight() / 2 - Assets.shieldFrame.getHeight() - 10;
        Assets.shieldFrame.setPosition(Assets.camera.position.x + Assets.camera.zoom * offX, Assets.camera.position.y
                + Assets.camera.zoom * offY);
        Assets.shieldFill.setPosition(Assets.shieldFrame.getX(), Assets.shieldFrame.getY());
        Assets.shieldFill.setRegionWidth((int) (320 * shieldLevel));
        Assets.shieldFill.draw(Assets.batch);
        Assets.shieldFrame.draw(Assets.batch);
    }
    static void drawBoxes(int wCnt) {
        Assets.boxSprite.setScale(Assets.camera.zoom / 2);
        Assets.boxSprite.setPosition(Assets.camera.position.x + Assets.camera.zoom
                * (Gdx.graphics.getWidth() - Assets.boxSprite.getWidth() - 20) / 2, Assets.camera.position.y
                + Assets.camera.zoom * (Gdx.graphics.getHeight() - Assets.boxSprite.getHeight() - 20) / 2);
        Assets.boxSprite.draw(Assets.batch);
        float w = Assets.font.getBounds("" + wCnt).width;
        Assets.font.setColor(1, 1, 1, 1);
        Assets.font.draw(Assets.batch, "" + wCnt, Assets.boxSprite.getX() - w, Assets.boxSprite.getY()
                + Assets.camera.zoom * (Assets.boxSprite.getHeight() / 3));
    }
}
