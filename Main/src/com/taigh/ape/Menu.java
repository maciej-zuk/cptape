package com.taigh.ape;

import com.badlogic.gdx.utils.Disposable;

public interface Menu extends Disposable {
	public abstract class MenuCallback {
		public abstract boolean clicked();
	}

	public int addEntry(String value, MenuCallback callback);

	public void delEntry(int id);

	public void draw();

	public int addEntry(ObservableString value, MenuCallback callback);

}
