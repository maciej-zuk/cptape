package com.taigh.ape;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.taigh.ape.GameGestureListener.InputReceiver;

public class PlanetMenu implements Menu {

	private class Entry implements InputReceiver {
		public MenuCallback callback;
		public Rectangle rectangle = new Rectangle();
		public ObservableString text;

		@Override
		public Rectangle getRectangle() {
			return rectangle;
		}

		@Override
		public void onTouchDown() {
			if (callback != null && currentState == state.shown) {
				if (callback.clicked())
					delEntry(entries.indexOf(this));
			}
			currentState = state.hid;
			color.a = 0;
		}

		@Override
		public void onTouchUp() {

		}

		@Override
		public boolean receivesInput() {
			return currentState == state.shown;
		}

		@Override
		public Circle getCircle() {
			return null;
		}

	}

	private enum state {
		hid, hiding, showing, shown
	}

	private final Color color = new Color(1, 1, 1, 1);
	state currentState = state.hid;
	List<Entry> entries = new ArrayList<PlanetMenu.Entry>();

	private final float innerRadius, outerRadius;
	private float autohideCounter;

	private final Vector3 menuPosition = new Vector3();

	public PlanetMenu(float innerRadius, float outerRadius) {
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
	}

	@Override
	public int addEntry(ObservableString value, MenuCallback callback) {
		Entry entry = new Entry();
		entry.text = value;
		entry.callback = callback;
		Assets.font.setScale(0.75f);
		entries.add(entry);
		Game.gestureListener.registerReceiver(entry);
		prepare();
		return entries.indexOf(entry);
	}

	@Override
	public int addEntry(String value, MenuCallback callback) {
		final String text = value;
		return addEntry(new ObservableString() {

			@Override
			public String getString() {
				return text;
			}
		}, callback);
	}

	public void clearMenu() {
		for (Entry entry : entries) {
			Game.gestureListener.unregisterReceiver(entry);
		}
		entries.clear();
	}

	@Override
	public void delEntry(int id) {
		if (id >= 0 && id < entries.size()) {
			Entry entry = entries.get(id);
			Game.gestureListener.unregisterReceiver(entry);
			entries.remove(id);
			prepare();
		}
	}

	@Override
	public void dispose() {
		clearMenu();
	}

	@Override
	public void draw() {
		switch (currentState) {
		case hiding:
			color.a -= 0.05;
			if (color.a < 0.05)
				currentState = state.hid;
			break;
		case showing:
			color.a += 0.1;
			if (color.a > 0.75)
				currentState = state.shown;
			break;
		case hid:
			return;
		case shown:
			autohideCounter -= 0.1;
			if (autohideCounter < 0)
				currentState = state.hiding;
			break;
		default:
			break;
		}
		Assets.font.setColor(color);
		Assets.font.setScale(0.75f);
		for (Entry entry : entries) {
			Assets.font.draw(Assets.batch, entry.text.getString(), entry.rectangle.x, entry.rectangle.y
					+ entry.rectangle.height);
		}
	}

	private void prepare() {
		double eSize = entries.size() - 1;
		double idx = 0;
		if (entries.size() == 1) {
			Entry entry = entries.get(0);
			TextBounds bounds = Assets.font.getBounds(entry.text.getString());
			entry.rectangle.width = bounds.width;
			entry.rectangle.height = bounds.height;
			entry.rectangle.x = menuPosition.x - entry.rectangle.width / 2;
			entry.rectangle.y = menuPosition.y - entry.rectangle.height / 2;
		} else {
			for (Entry entry : entries) {
				TextBounds bounds = Assets.font.getBounds(entry.text.getString());
				entry.rectangle.width = bounds.width;
				entry.rectangle.height = bounds.height;
				entry.rectangle.x = menuPosition.x - bounds.width / 2;
				entry.rectangle.y = (float) (menuPosition.y - (outerRadius / 2) + idx * (outerRadius) / eSize);
				idx += 1;

			}
		}
	}

	public void setPos(float posX, float posY) {
		menuPosition.x = posX;
		menuPosition.y = posY;
		prepare();
	}

	public void show() {
		color.a = 0;
		currentState = state.showing;
		autohideCounter = 30;
	}

	public void hide() {
		currentState = state.hiding;
		autohideCounter = 30;
	}

	public void toggleVisibility() {
		switch (currentState) {
		case hid:
			show();
			break;
		case shown:
			hide();
		default:
			break;
		}
	}

}
