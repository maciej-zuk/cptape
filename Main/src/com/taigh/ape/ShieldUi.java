package com.taigh.ape;

import com.badlogic.gdx.Gdx;

public class ShieldUi {

	static void draw(float shieldLevel) {
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
}
