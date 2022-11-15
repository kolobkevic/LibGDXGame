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
        mViewport = new FitViewport(640 / PPM, 480 / PPM, mCamera);
        mMapLoader = new MapLoader(mWorld);
        mPlayer = mMapLoader.getPlayer();
        mPlayer.setLinearDamping(0.5f);
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
        handleDrift();
        draw();
    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mPlayer.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * DRIFT,
                forwardSpeed.y + lateralSpeed.y * DRIFT);
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0, 0);
        if (turnDirection == Turn.LEFT) {
            mPlayer.setAngularVelocity(-TURN_SPEED);
        } else if (turnDirection == Turn.RIGHT) {
            mPlayer.setAngularVelocity(TURN_SPEED);
        } else if (turnDirection == Turn.NONE && mPlayer.getAngularVelocity() != 0) {
            mPlayer.setAngularVelocity(0f);
        }

        if (driveDirection == Drive.FORWARD) {
            baseVector.set(0, DRIVE_SPEED);
        } else if (driveDirection == Drive.BACKWARD) {
            baseVector.set(0, -DRIVE_SPEED);
        }

        if (!baseVector.isZero() && mPlayer.getLinearVelocity().len() < SLIDE_SPEED) {
            mPlayer.applyForceToCenter(mPlayer.getWorldVector(baseVector), true);
        }
    }

    private Vector2 getForwardVelocity() {
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(0, 1));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return new Vector2().mulAdd(currentNormal, dotProduct);
    }

    private Vector2 getLateralVelocity() {
        Vector2 currentNormal = mPlayer.getWorldVector(new Vector2(1, 0));
        float dotProduct = currentNormal.dot(mPlayer.getLinearVelocity());
        return new Vector2().mulAdd(currentNormal, dotProduct);
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