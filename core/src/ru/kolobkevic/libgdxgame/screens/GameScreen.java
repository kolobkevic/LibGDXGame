package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.kolobkevic.libgdxgame.MyAtlasAnimation;
import ru.kolobkevic.libgdxgame.MyInputProcessor;
import ru.kolobkevic.libgdxgame.PhysX;
import ru.kolobkevic.libgdxgame.enums.Actions;

import java.util.HashMap;

public class GameScreen implements Screen {
    private Actions actions;
    private Game game;
    private SpriteBatch batch;
    private HashMap<Actions, MyAtlasAnimation> manAssets;
    private Music music;
    private Sound sound;
    private MyInputProcessor myInputProcessor;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;
    private TiledMap baseMap;
    private OrthogonalTiledMapRenderer mapRenderer;

    public GameScreen(Game game) {
        this.game = game;

        baseMap = new TmxMapLoader().load("map/BaseMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(baseMap);

        physX = new PhysX();

        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        def.type = BodyDef.BodyType.StaticBody;
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.restitution = 1;

        MapLayer environment = baseMap.getLayers().get("ObjectsStatic");
        Array<RectangleMapObject> rectArray = environment.getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < rectArray.size; i++) {
            float x = rectArray.get(i).getRectangle().x;
            float y = rectArray.get(i).getRectangle().y;
            float w = rectArray.get(i).getRectangle().width / 2;
            float h = rectArray.get(i).getRectangle().height / 2;
            def.position.set(x, y);
            shape.setAsBox(w, h);
            physX.getWorld().createBody(def).createFixture(fdef).setUserData("Kubik");
        }

        def.type = BodyDef.BodyType.DynamicBody;
        def.gravityScale = 1;
        environment = baseMap.getLayers().get("ObjectsDynamic");
        rectArray = environment.getObjects().getByType(RectangleMapObject.class);
        for (int i = 0; i < rectArray.size; i++) {
            float x = rectArray.get(i).getRectangle().x;
            float y = rectArray.get(i).getRectangle().y;
            float w = rectArray.get(i).getRectangle().width / 2;
            float h = rectArray.get(i).getRectangle().height / 2;
            def.position.set(x, y);
            shape.setAsBox(w, h);
            fdef.density = 1;
            fdef.friction = 0;
            fdef.restitution = 1;
            physX.getWorld().createBody(def).createFixture(fdef).setUserData("Kubik");
        }

        environment = baseMap.getLayers().get("Hero");
        RectangleMapObject hero = (RectangleMapObject) environment.getObjects().get("Hero");
        float x = hero.getRectangle().x;
        float y = hero.getRectangle().y;
        float w = hero.getRectangle().width / 2;
        float h = hero.getRectangle().height / 2;

        def.position.set(x, y);
        shape.setAsBox(w, h);
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.restitution = 1;

        body = physX.getWorld().createBody(def);
        body.createFixture(fdef).setUserData("Kubik");

        shape.dispose();

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("Juhani-Junkala-Title-Screen.mp3"));
        music.setVolume(0.1f);
        music.setLooping(true);
        music.play();
        System.out.println(music.isPlaying());

        sound = Gdx.audio.newSound(Gdx.files.internal("Car_accelerating.mp3"));

        batch = new SpriteBatch();

        manAssets = new HashMap<>();
        manAssets.put(Actions.STAND, new MyAtlasAnimation("atlas/myAtlas.atlas", "stand", 10, Animation.PlayMode.LOOP));
        manAssets.put(Actions.RUN, new MyAtlasAnimation("atlas/myAtlas.atlas", "run", 10, Animation.PlayMode.LOOP));
        actions = Actions.STAND;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.zoom = 2;
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        manAssets.get(actions).setTime(Gdx.graphics.getDeltaTime());
        body.applyForceToCenter(myInputProcessor.getOutForce(), true);

        if (body.getLinearVelocity().len() < 0.6f) {
            actions = Actions.STAND;
        } else if (Math.abs(body.getLinearVelocity().x) > 0.6f) {
            actions = Actions.RUN;
        }
        manAssets.get(actions).setTime(Gdx.graphics.getDeltaTime());
        if (!manAssets.get(actions).draw().isFlipX() & body.getLinearVelocity().x < -0.6f) {
            manAssets.get(actions).draw().flip(true, false);
        }
        if (manAssets.get(actions).draw().isFlipX() & body.getLinearVelocity().x > 0.6f) {
            manAssets.get(actions).draw().flip(true, false);
        }

        float x = body.getPosition().x - 2.5f/camera.zoom;
        float y = body.getPosition().y - 2.5f/camera.zoom;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(manAssets.get(actions).draw(), x, y);
        batch.end();

        Gdx.graphics.setTitle(String.valueOf(body.getLinearVelocity()));
        physX.step();
        physX.debugDraw(camera);
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
    }
}
