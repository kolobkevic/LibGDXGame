package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {
    private String outString = "";

    public String getOutString() {
        return outString;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!outString.contains(Input.Keys.toString(keycode))){
            outString += Input.Keys.toString(keycode);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (outString.contains(Input.Keys.toString(keycode))){
            outString = outString.replace(Input.Keys.toString(keycode), "");
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
