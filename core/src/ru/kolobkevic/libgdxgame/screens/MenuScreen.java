package ru.kolobkevic.libgdxgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends AbstractScreen {

    private final Texture background, sign;
    private final SpriteBatch spriteBatch;
    private final Rectangle rectangle;
    int x, y;

    public MenuScreen() {
        background = new Texture("backgrounds/City.png");
        sign = new Texture("start.png");
        x = Gdx.graphics.getWidth() / 2 - sign.getWidth() / 2;
        y = Gdx.graphics.getHeight() / 2 - sign.getHeight() / 2;
        rectangle = new Rectangle(x, y, sign.getWidth(), sign.getHeight());
        spriteBatch = new SpriteBatch();
        title = "menu";
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.draw(sign, x, y);
        spriteBatch.end();
        if (Gdx.input.isTouched()) {
            if (rectangle.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                dispose();
                isReady= true;
            }
        }
    }

    @Override
    public void resize(int width, int height) {

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
        this.background.dispose();
        this.sign.dispose();
        this.spriteBatch.dispose();
    }
}
