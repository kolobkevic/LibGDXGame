package ru.kolobkevic.libgdxgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.jetbrains.annotations.NotNull;

public class PhysX {
    private final World world;
    public final static float PPM = 100;
    private final Box2DDebugRenderer debugRenderer;

    public World getWorld() {
        return world;
    }

    public PhysX() {
        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    public void debugDraw(@NotNull OrthographicCamera camera) {
        debugRenderer.render(world, camera.combined);
    }

    public void step() {
        world.step(1 / 60f, 3, 3);
    }

    public void dispose() {
        this.world.dispose();
        this.debugRenderer.dispose();
    }

    public Body addObject(@NotNull RectangleMapObject rectangleMapObject) {
        Rectangle rectangle = rectangleMapObject.getRectangle();
        String type = (String) rectangleMapObject.getProperties().get("BodyType");
        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        if (type.equals("StaticBody")) {
            def.type = BodyDef.BodyType.StaticBody;
        }
        if (type.equals("DynamicBody")) {
            def.type = BodyDef.BodyType.DynamicBody;
        }

        def.position.set((rectangle.x + rectangle.width / 2) / PPM, (rectangle.y + rectangle.height / 2) / PPM);
        def.gravityScale = (float) rectangleMapObject.getProperties().get("gravityScale");
        shape.setAsBox(rectangle.getWidth() / 2 / PPM, rectangle.getHeight() / 2 / PPM);
        fdef.shape = shape;
        fdef.friction = (float) rectangleMapObject.getProperties().get("friction");
        fdef.density = 1;
        fdef.restitution = (float) rectangleMapObject.getProperties().get("restitution");

        String name = "";
        if (rectangleMapObject.getName() != null) {
            name = rectangleMapObject.getName();
        }
        Body body;
        body = world.createBody(def);
        body.createFixture(fdef);
        body.setUserData("body");
        body.createFixture(fdef).setUserData(name);
        shape.dispose();
        return body;
    }
}
