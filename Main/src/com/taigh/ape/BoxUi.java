package com.taigh.ape;

import com.badlogic.gdx.Gdx;

public class BoxUi {

	static void draw(int wCnt) {
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
