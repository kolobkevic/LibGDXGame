package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    @Override
    public void create() {
        myInputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(myInputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("Juhani Junkala Title Screen.wav"));
        music.setVolume(1);
        music.setLooping(true);
        music.play();
        System.out.println(music.isPlaying());

        sound = Gdx.audio.newSound(Gdx.files.internal("Car_accelerating.wav"));

        batch = new SpriteBatch();
        run = new MyAtlasAnimation("atlas/myAtlas.atlas", "run", 10, Animation.PlayMode.LOOP);
        stand = new MyAtlasAnimation("atlas/myAtlas.atlas", "stand", 10, Animation.PlayMode.LOOP);
        tmpAnim = stand;
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1);

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

        if (!tmpAnim.draw().isFlipX() & look == -1) {
            tmpAnim.draw().flip(true, false);
        }
        if (tmpAnim.draw().isFlipX() & look == 1) {
            tmpAnim.draw().flip(true, false);
        }

        tmpAnim.setTime(Gdx.graphics.getDeltaTime());
        batch.begin();
        batch.draw(tmpAnim.draw(), x, y);
        batch.end();
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
    }
}
