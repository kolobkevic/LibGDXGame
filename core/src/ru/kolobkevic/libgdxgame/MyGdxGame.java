package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture img;
    private MyAtlasAnimation stand, run, tmpAnim;
    private Music music;
    private Sound sound;
    private MyInputProcessor myInputProcessor;
    private float x, y;
    private int look;
    private OrthographicCamera camera;
    private PhysX physX;
    private Body body;

    @Override
    public void create() {
        physX = new PhysX();
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        def.gravityScale = 1;
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(200, 10);
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.restitution = 1;

        physX.getWorld().createBody(def).createFixture(fdef).setUserData("Kubik");

        def.type = BodyDef.BodyType.DynamicBody;
        def.gravityScale = 5;
        for (int i = 0; i < 50; i++) {
            def.position.set(MathUtils.random(-100, 100), MathUtils.random(100, 200));

            shape.setAsBox(10, 10);
            fdef.shape = shape;
            fdef.density = 1;
            fdef.friction = 0;
            fdef.restitution = 1;

            physX.getWorld().createBody(def).createFixture(fdef).setUserData("Kubik");
        }

        def.position.set(MathUtils.random(-100, 100), MathUtils.random(100, 200));
        shape.setAsBox(10, 10);
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.restitution = 1;

        body = physX.getWorld().createBody(def);
        body.createFixture(fdef).setUserData("Kubik");

        shape.dispose();

        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

//        music = Gdx.audio.newMusic(Gdx.files.internal("Juhani Junkala Title Screen.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("Juhani-Junkala-Title-Screen.mp3"));
        music.setVolume(1);
        music.setLooping(true);
        music.play();
        System.out.println(music.isPlaying());

        sound = Gdx.audio.newSound(Gdx.files.internal("Car_accelerating.wav"));

        batch = new SpriteBatch();
        run = new MyAtlasAnimation("atlas/myAtlas.atlas", "run", 10, Animation.PlayMode.LOOP);
        stand = new MyAtlasAnimation("atlas/myAtlas.atlas", "stand", 10, Animation.PlayMode.LOOP);
        tmpAnim = stand;

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1);

        camera.position.x = body.getPosition().x;
        camera.position.y = body.getPosition().y;
        camera.zoom = 1;
        camera.update();

        tmpAnim = stand;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            sound.stop();
            sound.play(1);
        }
        if (myInputProcessor.getOutString().contains("A")) {
            look = -1;
            tmpAnim = run;
        }
        if (myInputProcessor.getOutString().contains("D")) {
            look = 1;
            tmpAnim = run;
        }
        if (myInputProcessor.getOutString().contains("W")) {
            y++;
        }
        if (myInputProcessor.getOutString().contains("Space")) {
            x = Gdx.graphics.getWidth() / 2.0f;
            y = Gdx.graphics.getHeight() / 2.0f;
        } else if (myInputProcessor.getOutString().contains("S")) {
            y--;
        }

        if (look == 1 & tmpAnim == run) {
            x++;
        }
        if (look == -1 & tmpAnim == run) {
            x--;
        }

        tmpAnim.setTime(Gdx.graphics.getDeltaTime());

        if (!tmpAnim.draw().isFlipX() & look == -1) {
            tmpAnim.draw().flip(true, false);
        }
        if (tmpAnim.draw().isFlipX() & look == 1) {
            tmpAnim.draw().flip(true, false);
        }

        float x = body.getPosition().x - 2.5f/camera.zoom;
        float y = body.getPosition().y - 2.5f/camera.zoom;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(tmpAnim.draw(), x, y);
        batch.end();

        physX.step();
        physX.debugDraw(camera);
    }

    @Override
    public void dispose() {
        sound.dispose();
        music.dispose();
        batch.dispose();
        img.dispose();
        run.dispose();
        stand.dispose();
        tmpAnim.dispose();
        physX.dispose();

    }
}
