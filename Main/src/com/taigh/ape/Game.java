package com.taigh.ape;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class Game implements ApplicationListener {
	public static GameGestureListener gestureListener;
	public static GameGestureDetector gestureDetector;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		gestureListener = new GameGestureListener();

		Assets.loadAssets();
		Assets.camera.setToOrtho(false, w, h);

		gestureDetector = new GameGestureDetector(gestureListener);
		Gdx.input.setInputProcessor(gestureDetector);
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);

		Assets.system.populate(100, 0);
		float transX = (Assets.cameraTarget.x - Assets.camera.position.x);
		float transY = (Assets.cameraTarget.y - Assets.camera.position.y);
		Assets.camera.translate(transX, transY);

	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void render() {
		gestureDetector.update();
		Assets.dt = Gdx.graphics.getDeltaTime();
		Assets.updateCams();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));


		if (gestureDetector.touchState.touchedAndMoved) {
			float dx = gestureDetector.touchState.positionProjected.x - Assets.playerShip.getPosition().x;
			float dy = gestureDetector.touchState.positionProjected.y - Assets.playerShip.getPosition().y;
			if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
				Assets.playerShip.getVelocity().add(dx / 50.0f, dy / 50.0f);
				Assets.playerShip.smokeSupply = 20;
			}

		}

		Assets.system.draw();

		Assets.shapeRenderer.setProjectionMatrix(Assets.camera.combined);
		Assets.batch.setProjectionMatrix(Assets.camera.combined);

		Assets.playerShip.applyGravitation(Assets.system);

		Assets.batch.begin();
		Assets.playerShip.draw();
		Assets.fx.draw();
        SimpleFX.drawGuide();
        SimpleFX.drawShields(Assets.playerShip.health);
        SimpleFX.drawBoxes(Assets.playerShip.boxesOnBoard);

		Assets.font.setScale(Assets.camera.zoom * 0.4f);
		Assets.font.setColor(Color.WHITE);
		Assets.font.draw(Assets.batch, "fps: " + Gdx.graphics.getFramesPerSecond(), Assets.camera.position.x
				- Assets.camera.zoom * (Gdx.graphics.getWidth() / 2 - 5), Assets.camera.position.y - Assets.camera.zoom
				* (Gdx.graphics.getHeight() / 2 - 20));
		Assets.batch.end();

	}

	@Override
	public void resize(int width, int height) {
		Assets.camera.setToOrtho(false, width, height);
		Assets.cameraPersp.viewportHeight = height;
		Assets.cameraPersp.viewportWidth = width;
		float transX = (Assets.cameraTarget.x - Assets.camera.position.x);
		float transY = (Assets.cameraTarget.y - Assets.camera.position.y);
		Assets.camera.translate(transX, transY);
	}

	@Override
	public void resume() {
	}
}
