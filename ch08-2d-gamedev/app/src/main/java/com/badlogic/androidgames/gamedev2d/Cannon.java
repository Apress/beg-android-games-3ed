package com.badlogic.androidgames.gamedev2d;

import com.badlogic.androidgames.framework.GameObject;

public class Cannon extends GameObject {
    public float angle;
    
    public Cannon(float x, float y, float width, float height) {
        super(x, y, width, height);
        angle = 0;
    }
}
