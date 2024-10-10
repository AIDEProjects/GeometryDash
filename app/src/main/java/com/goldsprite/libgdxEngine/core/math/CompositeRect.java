package com.goldsprite.libgdxEngine.core.math;

public class CompositeRect {
    public float[] vertices;

    public Vector2 position;

    public float width, height;

    public CompositeRect(float[] vertices, float x, float y, float width, float height) {
        this.vertices = vertices;
        position = new Vector2(x, y);
        this.width = width;
        this.height = height;
    }

    public Line getEdge(int index) {
        Line line = 
            new Line(
            vertices[(index * 2 + 0) % vertices.length], 
            vertices[(index * 2 + 1) % vertices.length], 
            vertices[(index * 2 + 2) % vertices.length], 
            vertices[(index * 2 + 3) % vertices.length]);
        line.multiply(new Vector2(width, height));
        line.move(position);
        return line;
    }

    public Rectangle getBoundingBox() {
        float minX=vertices[0], minY=vertices[1];
        for (int i=2;i < vertices.length;i += 2) {
            minX = Math.min(minX, vertices[i]);
            minY = Math.min(minY, vertices[i + 1]);
        }
        return new Rectangle(position.x + minX, position.y + minY, width, height);
    }

}
