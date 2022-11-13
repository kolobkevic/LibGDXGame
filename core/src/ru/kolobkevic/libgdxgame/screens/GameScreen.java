package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.kolobkevic.libgdxgame.enums.Drive;
import ru.kolobkevic.libgdxgame.enums.Turn;
import ru.kolobkevic.libgdxgame.tools.MapLoader;

import static ru.kolobkevic.libgdxgame.Constants.*;

public class GameScreen extends AbstractScreen {
    private final SpriteBatch mBatch;
    private final World mWorld;
    private final Box2DDebugRenderer mBdr;
    private final OrthographicCamera mCamera;
    private final Viewport mViewport;
    private final Body mPlayer;
    private final MapLoader mMapLoader;
    private Drive driveDirection;
    private Turn turnDirection;


    public GameScreen() {
        title = "game";
        mBatch = new SpriteBatch();
        mWorld = new World(GRAVITY, true);
        mBdr = new Box2DDebugRenderer();
        mCamera = new OrthographicCamera();
        mCamera.zoom = DEFAULT_ZOOM;
        mViewport = new FitViewport(800 / PPM, 600 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = mMapLoader.getPlayer();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        processInput();
        System.out.println(mPlayer.getAngularVelocity());
        update(delta);
        draw();
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0,0);
        if (turnDirection == Turn.LEFT) {
            mPlayer.setAngularVelocity(-ANGULAR_VELOCITY);
        } else if (turnDirection == Turn.RIGHT) {
            mPlayer.setAngularVelocity(ANGULAR_VELOCITY);
        } else if (turnDirection == Turn.NONE && mPlayer.getAngularVelocity() != 0) {
            mPlayer.setAngularVelocity(0f);
        }

        if (driveDirection == Drive.FORWARD) {
            baseVector.set(0, 200f);
        } else if (driveDirection == Drive.BACKWARD) {
            baseVector.set(0, -200f);
        }

        if (!baseVector.isZero()){
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            driveDirection = Drive.FORWARD;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            driveDirection = Drive.BACKWARD;
        } else {
            driveDirection = Drive.NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            turnDirection = Turn.LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            turnDirection = Turn.RIGHT;
        } else {
            turnDirection = Turn.NONE;
        }
    }

    private void draw() {
        mBatch.setProjectionMatrix(mCamera.combined);
        mBdr.render(mWorld, mCamera.combined);

    }

    private void update(final float delta) {
        mCamera.position.set(mPlayer.getPosition(), 0);
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