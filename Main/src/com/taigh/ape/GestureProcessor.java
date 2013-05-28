package com.taigh.ape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;

public class GestureProcessor extends GestureDetector {

	public class TouchState {
		Vector3 position = new Vector3();
		Vector3 positionProjected = new Vector3();
		boolean touched;
		boolean touchedAndMoved;
	};

	public TouchState touchState;

	public GestureProcessor(GestureListener listener) {
		super(listener);
		touchState = new TouchState();
	}

	public GestureProcessor(float halfTapSquareSize, float tapCountInterval, float longPressDuration,
			float maxFlingDelay, GestureListener listener) {
		super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
	}

	@Override
	public boolean scrolled(int amount) {
		if ((Assets.camera.zoom > 0.3f && amount < 0) || (Assets.camera.zoom < 10 && amount > 0))
			Assets.camera.zoom *= 1 + amount / 15.0f;
		return super.scrolled(amount);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		touchState.position.x = x;
		touchState.position.y = y;
		update();
		touchState.touched = true;
		touchState.touchedAndMoved = false;
		return super.touchDown(x, y, pointer, button);
	}

	@Override
	public boolean touchDragged(float x, float y, int pointer) {
		touchState.position.x = x;
		touchState.position.y = y;
		if (touchState.touched && !Gdx.input.isTouched(1))
			touchState.touchedAndMoved = true;
		update();
		return super.touchDragged(x, y, pointer);
	}

	@Override
	public boolean touchUp(float x, float y, int pointer, int button) {
		boolean ret = super.touchUp(x, y, pointer, button);
		touchState.touched = false;
		touchState.touchedAndMoved = false;
		return ret;
	}

	public void update() {
		touchState.positionProjected.x = touchState.position.x;
		touchState.positionProjected.y = touchState.position.y;
		Assets.camera.unproject(touchState.positionProjected);
	}
}
