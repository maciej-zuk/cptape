package com.taigh.ape;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.taigh.ape.Planet.state;

public class GuideUi {

	static void draw() {
		float ringSize = Assets.camera.zoom * Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.22f;
		Assets.planetIcon.setScale(Assets.camera.zoom / 2);
		Assets.terraPlanetIcon.setScale(Assets.camera.zoom / 2);
		Assets.font.setScale(Assets.camera.zoom / 2);
		for (Planet planet : Assets.system.planets) {
			float dx, dy;
			float distx = planet.getPositionX() - Assets.camera.position.x;
			float disty = planet.getPositionY() - Assets.camera.position.y;
			float dist = distx * distx + disty * disty;
			Sprite icon = Assets.planetIcon;
			if (planet.getCurrentState() != state.empty)
				icon = Assets.terraPlanetIcon;
			float ph = (float) Math.atan2(disty, distx);
			float opacity = Math.min(1, 5000000f / dist);
			dx = (float) (Math.cos(ph) * (2 * ringSize - ringSize * opacity));
			dy = (float) (Math.sin(ph) * (2 * ringSize - ringSize * opacity));
			icon.setPosition(Assets.camera.position.x - icon.getWidth() / 2 + dx,
					Assets.camera.position.y - icon.getHeight() / 2 + dy);
			icon.setColor(planet.getPlanetColor().r, planet.getPlanetColor().g, planet.getPlanetColor().b, opacity);
			icon.setScale(Assets.camera.zoom * planet.getRadius() / 1500);
			icon.draw(Assets.batch);
			if (planet.getCurrentState() == state.terraforming || planet.getCurrentState() == state.terraformingPaused) {
				String str = planet.getProgress() + "%";
				Assets.font.setScale(Assets.camera.zoom * 0.4f);
				TextBounds bnds = Assets.font.getBounds(str);
				Assets.font.setColor(1, 1, 1, opacity);
				Assets.font.draw(Assets.batch, str, Assets.camera.position.x + dx - bnds.width / 2,
						Assets.camera.position.y + dy + bnds.height / 2);
			}
		}
	}
}
