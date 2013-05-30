package com.taigh.ape;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameGestureListener implements GestureListener {

	public interface InputReceiver {
		public Rectangle getRectangle();

		public Circle getCircle();

		public void onTouchDown();

		public void onTouchUp();

		public boolean receivesInput();
	}

	private final List<InputReceiver> receivers = new LinkedList<InputReceiver>();
	public final Circle touchCircle = new Circle(0, 0, 1);
	private final Vector3 touchPos = new Vector3();
	private float lastDistance;

	public GameGestureListener() {
		lastDistance = 0;

	}

	public void registerReceiver(InputReceiver receiver) {
		receivers.add(receiver);
	}

	public void unregisterReceiver(InputReceiver receiver) {
		receivers.remove(receiver);
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		touchPos.x = x;
		touchPos.y = y;
		Assets.camera.unproject(touchPos);
		touchCircle.set(touchPos.x - 20, touchPos.y - 20, 80);
		InputReceiver minReceiver = null;
		float minArea = 1000000;
		for (InputReceiver receiver : receivers) {
			Circle rCircle = receiver.getCircle();
			Rectangle rRect = receiver.getRectangle();
			if (rRect != null) {
				if (receiver.receivesInput() && Intersector.overlaps(touchCircle, rRect)) {
					float thisArea = rRect.width * rRect.height;
					if (minReceiver != null) {
						if (minArea > thisArea) {
							minReceiver = receiver;
							minArea = thisArea;
						}
					} else {
						minReceiver = receiver;
						minArea = thisArea;
					}
				}
			} else if (rCircle != null) {
				if (receiver.receivesInput() && Intersector.overlaps(touchCircle, rCircle)) {
					float thisArea = (float) (Math.PI * rCircle.radius * rCircle.radius);
					if (minReceiver != null) {
						if (minArea > thisArea) {
							minReceiver = receiver;
							minArea = thisArea;
						}
					} else {
						minReceiver = receiver;
						minArea = thisArea;
					}
				}
			}
		}
		if (minReceiver != null) {
			minReceiver.onTouchDown();
			minReceiver.onTouchUp();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// Assets.cameraTarget.add(-deltaX * Assets.camera.zoom, deltaY *
		// Assets.camera.zoom, 0.0f);
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (distance - lastDistance < 0)
			Assets.camera.zoom *= 1.01f;
		else
			Assets.camera.zoom *= 0.99f;
		if (Assets.camera.zoom > 10f)
			Assets.camera.zoom = 10f;
		if (Assets.camera.zoom < 0.3f)
			Assets.camera.zoom = 0.3f;
		lastDistance = distance;
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
}
