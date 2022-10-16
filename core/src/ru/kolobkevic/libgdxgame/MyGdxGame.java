package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	MyAnimation animation;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("GT Car 4.png");
		animation = new MyAnimation("GT Car 4.png", 4,8, 60, Animation.PlayMode.LOOP);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		animation.setTime(Gdx.graphics.getDeltaTime());
		float x = Gdx.input.getX() + animation.draw().getRegionWidth()/2f;
		float y = Gdx.graphics.getHeight() - (Gdx.input.getY() + animation.draw().getRegionHeight()/2f);
		batch.begin();
		batch.draw(animation.draw(), x, y);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		animation.dispose();
	}
}
