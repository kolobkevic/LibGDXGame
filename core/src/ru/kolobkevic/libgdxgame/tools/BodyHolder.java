package ru.kolobkevic.libgdxgame.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import ru.kolobkevic.libgdxgame.enums.Drive;

public abstract class BodyHolder {
    private static final float DRIFT_OFFSET = 1f;
    private final Body mBody;
    private final int mId;
    protected Vector2 mForwardSpeed;
    protected Vector2 mLateralSpeed;
    protected float mDrift = 1;

    public BodyHolder(final Body mBody) {
        this.mBody = mBody;
        mId = -1;
    }

    public BodyHolder(final Vector2 position, final Vector2 size, final BodyDef.BodyType type,
                      final World world, float density, final boolean sensor, final int id) {
        mBody = ShapeFactory.createRectangle(position, size, type, world, density, sensor);
        this.mId = id;
    }

    public void setDrift(final float drift) {
        this.mDrift = drift;
    }

    public Body getBody() {
        return mBody;
    }

    public void update(final float delta) {
        if (mDrift < 1) {
            mForwardSpeed = getForwardVelocity();
            mLateralSpeed = getLateralVelocity();
            if ((mLateralSpeed.len() < DRIFT_OFFSET) && (mId > 1)) {
                stopDrift();
            } else {
                handleDrift();
            }
        }
    }

    private void handleDrift() {
        Vector2 forwardSpeed = getForwardVelocity();
        Vector2 lateralSpeed = getLateralVelocity();
        mBody.setLinearVelocity(forwardSpeed.x + lateralSpeed.x * mDrift,
                forwardSpeed.y + lateralSpeed.y * mDrift);
    }

    private Vector2 getForwardVelocity() {
        final Vector2 currentNormal = mBody.getWorldVector(new Vector2(0, 1));
        final float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return new Vector2().mulAdd(currentNormal, dotProduct);
    }

    private Vector2 getLateralVelocity() {
        final Vector2 currentNormal = mBody.getWorldVector(new Vector2(1, 0));
        final float dotProduct = currentNormal.dot(mBody.getLinearVelocity());
        return new Vector2().mulAdd(currentNormal, dotProduct);
    }

    public void stopDrift() {
        mBody.setLinearVelocity(mForwardSpeed);
    }

    public Drive direction() {
        final float tolerance = 0.2f;
        if (getLocalVelocity().y < -tolerance) {
            return Drive.BACKWARD;
        } else if (getLocalVelocity().y > tolerance) {
            return Drive.FORWARD;
        } else {
            return Drive.NONE;
        }
    }

    private Vector2 getLocalVelocity() {
        return mBody.getLocalVector(mBody.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
    }
}
