package ru.kolobkevic.libgdxgame.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import ru.kolobkevic.libgdxgame.tools.BodyHolder;

public class Wheel extends BodyHolder {
    private final Car mCar;
    private final boolean mPowered;

    public Wheel(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world,
                 float density, final int id, final Car car, boolean powered) {
        super(position, size, type, world, density, true, id);
        this.mCar = car;
        this.mPowered = powered;
    }

    public void setAngle(final float angle) {
        getBody().setTransform(getBody().getPosition(), mCar.getBody().getAngle() + angle * MathUtils.degreesToRadians);
    }

    public boolean isPowered() {
        return mPowered;
    }
}
