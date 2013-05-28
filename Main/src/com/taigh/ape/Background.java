package com.taigh.ape;

import java.util.Comparator;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Disposable;

public class Background implements Disposable {
	DecalBatch decalBatch;
	static int BG_ELEMS = 50;
	Decal clouds[] = new Decal[BG_ELEMS];
	float xoff[] = new float[BG_ELEMS];
	float yoff[] = new float[BG_ELEMS];

	public Background() {
		decalBatch = new DecalBatch(new CameraGroupStrategy(Assets.cameraPersp, new Comparator<Decal>() {
			@Override
			public int compare(Decal o1, Decal o2) {
				float dist1 = o1.getPosition().z;
				float dist2 = o2.getPosition().z;
				return (int) Math.signum(dist1 - dist2);
			}
		}));
		decalBatch.initialize(clouds.length);
		// decalBatch = new DecalBatch(new SimpleOrthoGroupStrategy());
		for (int i = 0; i < clouds.length; i++) {
			clouds[i] = Decal.newDecal(Assets.engineSmoke1);
			clouds[i].lookAt(Assets.cameraPersp.direction, Assets.cameraPersp.up);
			clouds[i].setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			clouds[i].setColor(1f, 1f, 1f, 0.1f);
			clouds[i].setDimensions(800, 800);
			clouds[i].setScale(4 * (float) (Math.random() + 1f));
			clouds[i].rotateZ((float) Math.random() * 360);
			xoff[i] = (float) (Math.random() - 0.5) * 20000f;
			yoff[i] = (float) (Math.random() - 0.5) * 10000f;
			clouds[i].setPosition(0, 0, (float) (Math.random()) * 990);
		}
	}

	public void draw() {
		for (int i = 0; i < clouds.length; i++) {
			float x = xoff[i];
			while (x - Assets.cameraPersp.position.x > 10000f) {
				x -= 20000f;
			}
			while (x - Assets.cameraPersp.position.x < -10000f) {
				x += 20000f;
			}
			float y = yoff[i];
			while (y - Assets.cameraPersp.position.y > 5000f) {
				y -= 10000f;
			}
			while (y - Assets.cameraPersp.position.y < -5000f) {
				y += 10000f;
			}
			clouds[i].setPosition(x, y, clouds[i].getZ());
			decalBatch.add(clouds[i]);
		}
		decalBatch.flush();
	}

	@Override
	public void dispose() {
		decalBatch.dispose();
	}
}
