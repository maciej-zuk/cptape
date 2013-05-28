package com.taigh.ape;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Capitan Ape";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 600;
        new LwjglApplication(new Game(), cfg);
    }
}
