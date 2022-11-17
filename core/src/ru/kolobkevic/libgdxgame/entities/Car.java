package ru.kolobkevic.libgdxgame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;
import ru.kolobkevic.libgdxgame.enums.Drive;
import ru.kolobkevic.libgdxgame.enums.Turn;
import ru.kolobkevic.libgdxgame.tools.BodyHolder;
import ru.kolobkevic.libgdxgame.tools.MapLoader;

import static ru.kolobkevic.libgdxgame.constants.Constants.*;

public class Car extends BodyHolder {
    private static final int UPPER_LEFT = 0;
    private static final int UPPER_RIGHT = 1;
    private static final int DOWN_LEFT = 2;
    private static final int DOWN_RIGHT = 3;

    private final Array<Wheel> mWheels = new Array<>();
    private final Array<Wheel> mFrontWheels = new Array<>();

    private float mCurrentWheelAngle = 0;
    private float mCurrentMaxSpeed;
    private final float mRegularMaxSpeed;
    private float mAcceleration;
    private Drive driveDirection;
    private Turn turnDirection;

    public Car(final float maxSpeed, final float drift, final float acceleration, final MapLoader mapLoader,
               int wheelDrive, World world) {
        super(mapLoader.getPlayer());
        this.mRegularMaxSpeed = maxSpeed;
        this.mAcceleration = acceleration;
        this.mDrift = drift;
        getBody().setLinearDamping(LINEAR_DAMPING);
        getBody().getFixtureList().get(0).setRestitution(RESTITUTION);
        createWheels(world, wheelDrive);
    }

    private void createWheels(World world, int wheelDrive) {
        for (int i = 0; i < 4; i++) {
            float xOffset = 0;
            float yOffset = 0;

            switch (i) {
                case UPPER_LEFT:
                    xOffset = -64;
                    yOffset = 80;
                    break;
                case UPPER_RIGHT:
                    xOffset = 64;
                    yOffset = 80;
                    break;
                case DOWN_LEFT:
                    xOffset = -64;
                    yOffset = -80;
                    break;
                case DOWN_RIGHT:
                    xOffset = 64;
                    yOffset = -80;
                    break;
            }

            boolean powered = (wheelDrive == DRIVE_4WD) || (wheelDrive == DRIVE_2WD && i < 2);

            Wheel wheel = new Wheel(
                    new Vector2(getBody().getPosition().x * PPM + xOffset, getBody().getPosition().y * PPM + yOffset),
                    WHEEL_SIZE, BodyDef.BodyType.DynamicBody, world, WHEELS_DENSITY, i, this, powered);
            if (i < 2) {
                RevoluteJointDef jointDef = new RevoluteJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter());
                jointDef.enableMotor = false;
                world.createJoint(jointDef);
            } else {
                PrismaticJointDef jointDef = new PrismaticJointDef();
                jointDef.initialize(getBody(), wheel.getBody(), wheel.getBody().getWorldCenter(), new Vector2(1, 0));
                jointDef.enableLimit = true;
                jointDef.lowerTranslation = jointDef.upperTranslation = 0;
                world.createJoint(jointDef);
            }

            mWheels.add(wheel);
            if (i < 2) {
                mFrontWheels.add(wheel);
            }
            wheel.setDrift(mDrift);
        }
    }

    public void setDriveDirection(Drive driveDirection) {
        this.driveDirection = driveDirection;
    }

    public void setTurnDirection(Turn turnDirection) {
        this.turnDirection = turnDirection;
    }

    private void processInput() {
        Vector2 baseVector = new Vector2(0, 0);
        if (turnDirection == Turn.LEFT) {
            if (mCurrentWheelAngle < 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.min(mCurrentWheelAngle += WHEEL_TURN_INCREMENT, MAX_WHEEL_ANGLE);
        } else if (turnDirection == Turn.RIGHT) {
            if (mCurrentWheelAngle > 0) {
                mCurrentWheelAngle = 0;
            }
            mCurrentWheelAngle = Math.max(mCurrentWheelAngle -= WHEEL_TURN_INCREMENT, -MAX_WHEEL_ANGLE);
        } else {
            mCurrentWheelAngle = 0;
        }

        for (Wheel wheel : mFrontWheels) {
            wheel.setAngle(mCurrentWheelAngle);
        }

        if (driveDirection == Drive.FORWARD) {
            baseVector.set(0, mAcceleration);
        } else if (driveDirection == Drive.BACKWARD) {
            if (direction() == Drive.BACKWARD) {
                baseVector.set(0, -mAcceleration * REVERSE_POWER);
            } else if (direction() == Drive.FORWARD) {
                baseVector.set(0, -mAcceleration * BREAK_POWER);
            } else {
                baseVector.set(0, -mAcceleration);
            }
        }
        mCurrentMaxSpeed = mRegularMaxSpeed;

        if (getBody().getLinearVelocity().len() < mCurrentMaxSpeed) {
            for (Wheel wheel : mWheels) {
                if(wheel.isPowered()) {
                    wheel.getBody().applyForceToCenter(wheel.getBody().getWorldVector(baseVector), true);
                }
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        processInput();
        for (Wheel wheel : mWheels) {
            wheel.update(delta);
        }
    }
}