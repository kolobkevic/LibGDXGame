package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.Game;
import ru.kolobkevic.libgdxgame.screens.AbstractScreen;
import ru.kolobkevic.libgdxgame.screens.GameScreen;
import ru.kolobkevic.libgdxgame.screens.MenuScreen;

public class Main extends Game {
    AbstractScreen currentScreen;
    @Override
    public void create() {
        currentScreen = new MenuScreen();
        setScreen(currentScreen);
    }

    @Override
    public void render() {
        super.render();
        if(currentScreen.isReady){
            if (currentScreen.title.equals("menu")){
                setScreen(new GameScreen());
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}