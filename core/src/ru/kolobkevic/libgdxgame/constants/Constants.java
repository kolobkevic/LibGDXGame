package ru.kolobkevic.libgdxgame.constants;

import com.badlogic.gdx.math.Vector2;

public class Constants {
    public Constants() {
    }
    public static final String MAP_NAME = "map/BaseMap.tmx";
    public static final Vector2 GRAVITY = new Vector2(0, 0);
    public static final Vector2 WHEEL_SIZE = new Vector2(16,32);
    public static final Vector2 RESOLUTION = new Vector2(640, 480);
    public static final int DRIVE_2WD = 0;
    public static final int DRIVE_4WD = 1;
    public static final float CAMERA_ZOOM = 0.3f;
    public static final float DEFAULT_ZOOM = 6f;
    public static final float PPM = 100f;
    public static final float MAX_WHEEL_ANGLE = 20.0f;
    public static final float WHEEL_TURN_INCREMENT = 1.0f;
    public static final float LINEAR_DAMPING = 0.5f;
    public static final float RESTITUTION = 0.2f;
    public static final float WHEELS_DENSITY = 0.4f;
    public static final float BREAK_POWER = 1.3f;
    public static final float REVERSE_POWER = 0.5f;
}
