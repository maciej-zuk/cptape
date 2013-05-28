package com.taigh.ape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Disposable;

public class PlanetsSystem implements Disposable {

	public Planet[] planets;

	public PlanetsSystem() {

	}

	public static int hsv2rgb(float h, float s, float v) {
		float r = 0, g = 0, b = 0;
		if (v < 0.00001)
			return 0;
		else {
			h *= 6;
			float i = (float) Math.floor(h);
			float f = h - i;
			float p = v * (1 - s);
			float q = v * (1 - (s * f));
			float t = v * (1 - (s * (1 - f)));
			if (i == 0) {
				r = v;
				g = t;
				b = p;
			} else if (i == 1) {
				r = q;
				g = v;
				b = p;
			} else if (i == 2) {
				r = p;
				g = v;
				b = t;
			} else if (i == 3) {
				r = p;
				g = q;
				b = v;
			} else if (i == 4) {
				r = t;
				g = p;
				b = v;
			} else if (i == 5) {
				r = v;
				g = p;
				b = q;
			}
			return (((int) (r * 255)) << 24) + (((int) (g * 255)) << 16) + (((int) (b * 255) << 8)) + 255;
		}
	}

	public void populate(int N, long seed) {
		Assets.random.setSeed(seed);
		planets = new Planet[N];
		for (int i = 0; i < N; i++) {
			Color color = new Color();
			Color.rgba8888ToColor(color, hsv2rgb(Assets.random.nextFloat(), 0.6f, 0.8f));

			planets[i] = new Planet(color, (float) (800 + Assets.random.nextGaussian() * 300));

			float sizeLimit = 20000;
			boolean overlaps = false;
			do {
				float posX = (float) (Assets.random.nextGaussian() * sizeLimit);
				float posY = (float) (Assets.random.nextGaussian() * sizeLimit);
				planets[i].setPosition(posX, posY);

				sizeLimit += 1000;
				for (int k = 0; k < i; k++) {
					float cpr = planets[i].circle.radius;
					planets[i].circle.radius = cpr * 2;
					overlaps = Intersector.overlaps(planets[i].circle, planets[k].circle);
					planets[i].circle.radius = cpr;
					if (overlaps)
						break;
				}
			} while (overlaps);
		}
	}

	public void draw() {
		Assets.batch.begin();
		for (Planet planet : planets) {
			planet.tick();
			if (Intersector.overlaps(planet.getCircle(), Assets.viewCircle)) {
				planet.rotate(-1 * (planet.getId() & 1) * Assets.dt);
				planet.draw();
			}
		}
		Assets.batch.end();
	}

	@Override
	public void dispose() {
		for (Planet planet : planets) {
			planet.dispose();
		}
	}
}
