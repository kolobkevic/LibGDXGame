package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.physics.box2d.*;
import ru.kolobkevic.libgdxgame.screens.GameScreen;

public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
//        if (a.getUserData() != null && b.getUserData() != null) {
            if (a.getUserData().equals("coins") && b.getUserData().equals("coins")) {
                GameScreen.bodyToDelete.add(b.getBody());
            }
            if (b.getUserData().equals("coins") && a.getUserData().equals("coins")) {
                GameScreen.bodyToDelete.add(a.getBody());
            }
//        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
