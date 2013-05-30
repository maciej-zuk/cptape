package com.taigh.ape;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

public class Assets {
	public static SpriteBatch batch;
	public static BitmapFont font;
	public static ShaderProgram shaderDummy;
	public static ShaderProgram shaderCircle;
	public static Sprite sprtPlants[];
	public static Sprite playerShipSprite;
	public static Sprite engineSmoke1;
	public static Sprite engineSmoke2;
	public static Sprite planetIcon;
	public static Sprite terraPlanetIcon;
	public static Sprite shieldFrame;
	public static Sprite shieldFill;
	public static Texture texSprites1;
	public static Texture texFont;
	public static ShapeRenderer shapeRenderer;
	public static SimpleFX fx;

	public static OrthographicCamera camera;
	public static PerspectiveCamera cameraPersp;
	public static Circle viewCircle = new Circle();

	public static Vector3 cameraTarget;
	public static float dt;
	public static Ship playerShip;
	public static Random random;
	public static PlanetsSystem system;
	public static Texture planetCircle;
	public static Sprite planetSprite;
	public static Sprite boxSprite;

	public static void updateCams() {
		cameraTarget.x = playerShip.getPosition().x;
		cameraTarget.y = playerShip.getPosition().y;
		float transX = 10 * dt * (cameraTarget.x - camera.position.x) / 5.0f;
		float transY = 10 * dt * (cameraTarget.y - camera.position.y) / 5.0f;
		camera.translate(transX, transY);
		camera.update();
		cameraPersp.position.x = camera.position.x;
		cameraPersp.position.y = camera.position.y;
		cameraPersp.position.z = camera.zoom * 1000;
		cameraPersp.lookAt(cameraPersp.position.x, cameraPersp.position.y, 0);
		cameraPersp.update();

		float r = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 1.5f * camera.zoom;

		viewCircle.set(cameraTarget.x, cameraTarget.y, r);
	}

	public static void dispose() {
		batch.dispose();
		font.dispose();
		shaderDummy.dispose();
		shaderCircle.dispose();
		texSprites1.dispose();
		texFont.dispose();
		shapeRenderer.dispose();
		system.dispose();
    	planetCircle.dispose();
	}

	public static void loadAssets() {
		texSprites1 = new Texture(Gdx.files.internal("data/sprites1.png"), true);
		texSprites1.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		texFont = new Texture(Gdx.files.internal("data/font.png"), true);
		texFont.setFilter(TextureFilter.MipMap, TextureFilter.Linear);
		Pixmap planetCirclePix = new Pixmap(1, 1, Format.RGBA8888);
		planetCircle = new Texture(planetCirclePix, true);
		planetCircle.setFilter(TextureFilter.MipMap, TextureFilter.Linear);

		planetCirclePix.dispose();
		planetSprite = new Sprite(planetCircle);
		sprtPlants = new Sprite[10];
		sprtPlants[0] = new Sprite(texSprites1, 5, 41, 102, 162);
		sprtPlants[0].setOrigin(48, 0);
		sprtPlants[1] = new Sprite(texSprites1, 117, 40, 146, 163);
		sprtPlants[1].setOrigin(74, 0);
		sprtPlants[2] = new Sprite(texSprites1, 271, 39, 75, 165);
		sprtPlants[2].setOrigin(36, 0);
		sprtPlants[3] = new Sprite(texSprites1, 353, 38, 161, 165);
		sprtPlants[3].setOrigin(81, 0);
		sprtPlants[4] = new Sprite(texSprites1, 522, 40, 102, 164);
		sprtPlants[4].setOrigin(43, 0);
		sprtPlants[5] = new Sprite(texSprites1, 633, 40, 169, 164);
		sprtPlants[5].setOrigin(84, 0);
		sprtPlants[6] = new Sprite(texSprites1, 811, 39, 100, 164);
		sprtPlants[6].setOrigin(53, 0);
		sprtPlants[7] = new Sprite(texSprites1, 919, 3, 88, 81);
		sprtPlants[7].setOrigin(46, 0);
		sprtPlants[8] = new Sprite(texSprites1, 919, 84, 88, 61);
		sprtPlants[8].setOrigin(46, 0);
		sprtPlants[9] = new Sprite(texSprites1, 919, 143, 88, 61);
		sprtPlants[9].setOrigin(46, 0);

		planetIcon = new Sprite(texSprites1, 274, 229, 107, 107);
		terraPlanetIcon = new Sprite(texSprites1, 405, 217, 132, 132);
		playerShipSprite = new Sprite(texSprites1, 6, 222, 222, 291);
		engineSmoke1 = new Sprite(texSprites1, 15, 523, 90, 90);
		engineSmoke2 = new Sprite(texSprites1, 124, 523, 90, 90);
		shieldFrame = new Sprite(texSprites1, 671, 255, 320, 196);
		shieldFrame.setOrigin(0, 0);
		shieldFill = new Sprite(texSprites1, 671, 461, 320, 196);
		shieldFill.setOrigin(0, 0);
		boxSprite = new Sprite(texSprites1, 550, 229, 109, 109);
		boxSprite.setOrigin(0, 0);

		playerShip = new Ship();

		batch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), new TextureRegion(texFont), false);
		font.setOwnsTexture(true);
		font.setScale(0.33f);
		font.setUseIntegerPositions(false);

		ShaderProgram.pedantic = false;
		shaderDummy = new ShaderProgram(Gdx.files.internal("shaders/dummy.vs"), Gdx.files.internal("shaders/dummy.fs"));
		shaderCircle = new ShaderProgram(Gdx.files.internal("shaders/dummy.vs"),
				Gdx.files.internal("shaders/circle.fs"));
		shaderDummy.begin();
		Gdx.app.log("shaderDummy", shaderDummy.getLog());
		shaderDummy.end();

		shaderCircle.begin();
		Gdx.app.log("shaderCircle", shaderCircle.getLog());
		shaderCircle.end();

		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		cameraTarget = new Vector3();
		cameraPersp = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraPersp.far = 10000;

		random = new Random();
		system = new PlanetsSystem();
		Assets.updateCams();
		fx = new SimpleFX();
	}

}
