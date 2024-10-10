package com.goldsprite.libgdxEngine.core.math;

public class Line
{
    public Vector2 start;
    public Vector2 end;

    public Line(float startX, float startY, float endX, float endY){
        this.start = new Vector2(startX, startY);
        this.end = new Vector2(endX, endY);
    }

    public float minX(){
        return Math.min(start.x, end.x);
    }
    public float minY(){
        return Math.min(start.y, end.y);
    }
    public float maxX(){
        return Math.max(start.x, end.x);
    }
    public float maxY(){
        return Math.max(start.y, end.y);
    }

    public Line multiply(Vector2 scl){
        start.x *= scl.x;
        start.y *= scl.y;
        end.x *= scl.x;
        end.y *= scl.y;
        return this;
    }
    public Line move(Vector2 trans){
        start = start.add(trans);
        end = end.add(trans);
        return this;
    }

    public float vecX(){
        return end.x - start.x;
    }
    public float vecY(){
        return end.y - start.y;
    }
    public float k(){
        return vecY()/vecX();
    }
    public float b(){
        float y = start.y;
        float x = start.x;
        float b = y - k() * x;
        return b;
    }
    
    @Override
    public String toString() {
        return "{Line(" + start.x + ", " + start.y + ", " + end.x + ", " + end.y + ")}";
    }
}
