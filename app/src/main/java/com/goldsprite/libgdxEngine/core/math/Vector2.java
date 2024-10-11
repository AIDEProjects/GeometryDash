package com.goldsprite.libgdxEngine.core.math;

public class Vector2 {
    public float x, y;

    public Vector2(){
        this(0, 0);
    }
    public Vector2(Vector2 vec){
        this(vec.x, vec.y);
    }
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    

    public Vector2 multiply(float mul) {
        return new Vector2(x * mul, y * mul);
    }

    public Vector2 devideBy(float dev) {
        return new Vector2(x / dev, y / dev);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        float mag = magnitude();
        return new Vector2(x / mag, y / mag);
    }

    public static float dotProduct(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }
    
    
    public static Vector2 up = new Vector2(0, 1);
    public static Vector2 left = new Vector2(-1, 0);
    public static Vector2 down = new Vector2(0, -1);
    public static Vector2 right = new Vector2(1, 0);

    public boolean equals(Vector2 vec) {
        return vec.x==this.x && vec.y ==y;
    }

    @Override
    public String toString() {
        return "{Vector2("+x+", "+y+")}";
    }


}
