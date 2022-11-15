package ru.kolobkevic.libgdxgame.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static ru.kolobkevic.libgdxgame.Constants.PPM;

public class ShapeFactory {
    private ShapeFactory() {
    }

    public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type,
                                       final World world, float density) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position.x / PPM, position.y / PPM);
        bodyDef.type = type;
        final Body body = world.createBody(bodyDef);

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / PPM, size.y / PPM);
        final FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = density;

        body.createFixture(fDef);
        shape.dispose();

        return body;
    }
}
