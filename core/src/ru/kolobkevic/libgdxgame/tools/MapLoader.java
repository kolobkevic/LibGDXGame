package ru.kolobkevic.libgdxgame.tools;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import static ru.kolobkevic.libgdxgame.Constants.MAP_NAME;


public class MapLoader implements Disposable {
    private static final String MAP_WALL = "wall";
    private static final String MAP_PLAYER = "player";
    private final World mWorld;
    private final TiledMap mMap;

    public MapLoader(World world) {
        this.mWorld = world;
        mMap = new TmxMapLoader().load(MAP_NAME);
        final Array<RectangleMapObject> walls = mMap.getLayers().get(MAP_WALL).getObjects().
                getByType(RectangleMapObject.class);

        for (RectangleMapObject rObject : walls) {
            Rectangle rectangle = rObject.getRectangle();
            ShapeFactory.createRectangle(
                    new Vector2(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2),
                    new Vector2(rectangle.width / 2, rectangle.height / 2),
                    BodyDef.BodyType.StaticBody, mWorld, 1.0f);
        }
    }

    public Body getPlayer() {
        final Rectangle player = mMap.getLayers().get(MAP_PLAYER).getObjects().getByType(RectangleMapObject.class).
                get(0).getRectangle();
        return ShapeFactory.createRectangle(new Vector2(player.x + player.width / 2, player.y + player.height / 2),
                new Vector2(player.width / 2, player.height / 2),
                BodyDef.BodyType.DynamicBody, mWorld, 0.4f);
    }

    @Override
    public void dispose() {
        mMap.dispose();
    }
}
