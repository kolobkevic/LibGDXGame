package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.kolobkevic.libgdxgame.Hero;
import ru.kolobkevic.libgdxgame.MyAnimation;
import ru.kolobkevic.libgdxgame.MyInputProcessor;
import ru.kolobkevic.libgdxgame.PhysX;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private Music music;
    private Sound sound;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap baseMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int[] front, tL;
    private final Hero hero;
    private final MyAnimation coinAnim;

    public static List<Body> bodyToDelete;

    public GameScreen(Game game) {
        this.game = game;
        bodyToDelete = new ArrayList<>();

        coinAnim = new MyAnimation("MonedaD.png", 1, 5, 12f, Animation.PlayMode.LOOP);

        baseMap = new TmxMapLoader().load("map/BaseMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(baseMap);

        front = new int[1];
        front[0] = baseMap.getLayers().getIndex("front");
        tL = new int[1];
        tL[0] = baseMap.getLayers().getIndex("t0");

        physX = new PhysX();

        Array<RectangleMapObject> rectObjects = baseMap.getLayers().get("ObjectsStatic").getObjects().getByType(RectangleMapObject.class);
        rectObjects.addAll(baseMap.getLayers().get("ObjectsDynamic").getObjects().getByType(RectangleMapObject.class));
        for (int i = 0; i < rectObjects.size; i++) {
            physX.addObject(rectObjects.get(i));
        }

        body = physX.addObject((RectangleMapObject) baseMap.getLayers().get("hero").getObjects().get("hero"));
        body.setFixedRotation(true);
        hero = new Hero(body);

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("Juhani-Junkala-Title-Screen.mp3"));
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
        System.out.println(music.isPlaying());

        sound = Gdx.audio.newSound(Gdx.files.internal("Car_accelerating.mp3"));

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.zoom = 2f;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLUE);

        camera.position.x = body.getPosition().x * PhysX.PPM;
        camera.position.y = body.getPosition().y * PhysX.PPM;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(tL);

        hero.setTime(delta);
        body.applyForceToCenter(myInputProcessor.getOutForce(), true);
        hero.setFPS(body.getLinearVelocity(),true);

        Rectangle tmp = hero.getRect(camera);
        float bScale = 0.2f;
        ((PolygonShape) body.getFixtureList().get(0).getShape()).setAsBox(tmp.getWidth()/2, tmp.getHeight()/2);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(hero.getFrame(), tmp.x, tmp.y, tmp.width * PhysX.PPM/bScale, tmp.height * PhysX.PPM/bScale);


        Array<Body> bodies = physX.getBodies("coins");
        coinAnim.setTime(delta);
        TextureRegion tr = coinAnim.draw();
        float dScale = 0.3f;
        for(Body b: bodies){
            float cX = b.getPosition().x * PhysX.PPM - tr.getRegionWidth() / 2f / dScale;
            float cY = b.getPosition().y * PhysX.PPM - tr.getRegionHeight() / 2f / dScale;
            float cH = tr.getRegionHeight() / PhysX.PPM / dScale;
            float cW = tr.getRegionWidth() / PhysX.PPM / dScale;
            ((PolygonShape) b.getFixtureList().get(0).getShape()).setAsBox(cW/2, cH/2);
            batch.draw(tr, cX, cY, cW * PhysX.PPM, cH * PhysX.PPM);
        }
        batch.end();

        mapRenderer.render(front);

        for (Body b:bodyToDelete) {
            physX.deleteBody(b);
        }
        bodyToDelete.clear();
        physX.step();
//        physX.debugDraw(camera);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
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
        sound.dispose();
        music.dispose();
        batch.dispose();
        physX.dispose();
        baseMap.dispose();
        mapRenderer.dispose();
        coinAnim.dispose();
        hero.dispose();
    }
}
