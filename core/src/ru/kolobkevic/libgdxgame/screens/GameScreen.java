package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.kolobkevic.libgdxgame.entities.Car;
import ru.kolobkevic.libgdxgame.enums.Drive;
import ru.kolobkevic.libgdxgame.enums.Turn;
import ru.kolobkevic.libgdxgame.tools.MapLoader;

import static ru.kolobkevic.libgdxgame.constants.Constants.*;

public class GameScreen extends AbstractScreen {
    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mBdr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Car mPlayer;
    private final MapLoader mMapLoader;

    public GameScreen() {
        title = "game";
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mBdr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(RESOLUTION.x / PPM, RESOLUTION.y / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = new Car(35.0f, 0.7f, 80.0f, mMapLoader, DRIVE_2WD, mWorld);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        update(delta);
        draw();
    }


    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mPlayer.setDriveDirection(Drive.FORWARD);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mPlayer.setDriveDirection(Drive.BACKWARD);
        } else {
            mPlayer.setDriveDirection(Drive.NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mPlayer.setTurnDirection(Turn.LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mPlayer.setTurnDirection(Turn.RIGHT);
        } else {
            mPlayer.setTurnDirection(Turn.NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            mCamera.zoom -= CAMERA_ZOOM;
        } else if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            mCamera.zoom += CAMERA_ZOOM;
        }
    }

    private void draw() {
        mBatch.setProjectionMatrix(mCamera.combined);
        mBdr.render(mWorld, mCamera.combined);
    }

    private void update(final float delta) {
        mPlayer.update(delta);
        mCamera.position.set(mPlayer.getBody().getPosition(), 0);
        mCamera.update();
        mWorld.step(delta, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mBatch.dispose();
        mWorld.dispose();
        mBdr.dispose();
        mMapLoader.dispose();
    }
}