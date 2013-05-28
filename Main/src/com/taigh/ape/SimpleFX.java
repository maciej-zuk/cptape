package com.taigh.ape;

import java.util.LinkedList;
import java.util.List;

import com.taigh.ape.Ship.reachCallback;

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

	class FloatingBox {

		float x;
		float y;
		float vx;
		float vy;
		float size;
		ObservableCircle target;
		boolean enabled = false;
		reachCallback cb;

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

		public void shoot(ObservableCircle source, ObservableCircle target, reachCallback cb) {
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

	public boolean shootBox(ObservableCircle source, ObservableCircle target, reachCallback cb) {
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

}
