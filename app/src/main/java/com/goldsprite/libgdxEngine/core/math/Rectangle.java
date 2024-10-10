package com.goldsprite.libgdxEngine.core.math;
import com.goldsprite.geometryDash.MainActivity;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;

public class Rectangle {
    public Vector2 position;
    public float width, height;
    public Vector2 velocity;// 单位像素每秒 px/s，使用时乘上delta时间步长
    public int face=-1;
    public Vector2 perTrans = new Vector2(0, 0);

    public Rectangle(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.velocity = new Vector2(0, 0);
    }
    public Rectangle(Vector2 xy, float width, float height) {
        this.position = new Vector2(xy.x, xy.y);
        this.width = width;
        this.height = height;
        this.velocity = new Vector2(0, 0);
    }

    public float leftX() {
        return position.x;
    }
    public float bottomY() {
        return position.y;
    }
    public float rightX() {
        return position.x + width;
    }
    public float topY() {
        return position.y + height;
    }

    public void move(float deltaTime) {
        position = position.add(velocity.multiply(deltaTime));
        //velocity = velocity.multiply(0);
    }

    public boolean intersects(Rectangle other) {
        return intersects(this, other);
    }
    public static boolean intersects(Rectangle rect, Rectangle other) {
        return rect.position.x <= other.position.x + other.width &&
            rect.position.x + rect.width >= other.position.x &&
            rect.position.y <= other.position.y + other.height &&
            rect.position.y + rect.height >= other.position.y;
    }

    public boolean intersectsLine(Line other) {
        return intersectsLine(this, other);
    }
    //适用于对线的AABB快速检测
    public static boolean intersectsLine(Rectangle rect, Line other) {
        return rect.leftX() <= other.maxX() &&
            rect.rightX() >= other.minX() &&
            rect.bottomY() <= other.maxY() &&
            rect.topY() >= other.minY();
    }


    public int resolveCollision(float delta, Rectangle other) {
        face = -1;
        //预计下帧位置
        Vector2 deltaVel = velocity.multiply(delta);
        Vector2 afterPos = position.add(deltaVel);
        //AABB快速碰撞
        if (!intersects(new Rectangle(afterPos, width, height), other)) {
            return face;
        }

        //计算斜率
        float m = deltaVel.y / deltaVel.x;
        Vector2 finalVel=null;
        Vector2 velBack = null;//回退量
        float backLength;
        Vector2 tranVel=null;//切向量
        float depX = deltaVel.x > 0
            ?other.position.x - (afterPos.x + width)
            : other.position.x + other.width - afterPos.x;
        float depY = deltaVel.y > 0
            ?other.position.y - (afterPos.y + height)
            : other.position.y + other.height - afterPos.y;
        velBack = new Vector2(depY / m, depY);
        backLength = velBack.magnitude();

        //竖直线斜率无限大，水平线斜率0(计算xy无限大)
        if (!Float.isInfinite(backLength) && intersects(new Rectangle(afterPos.add(velBack), width, height), other)) {
            face = deltaVel.y > 0 ?0: 2;
            tranVel = new Vector2(-velBack.x, -velBack.y * 0);
        } else {
            velBack = new Vector2(depX, m * depX);
            backLength = velBack.magnitude();
            if (!Float.isInfinite(backLength) && intersects(new Rectangle(afterPos.add(velBack), width, height), other)) {
                face = deltaVel.x > 0 ?1: 3;
                tranVel = new Vector2(-velBack.x * 0, -velBack.y);
            }
        }

        finalVel = deltaVel.add(velBack).add(tranVel);
        velocity = finalVel.devideBy(delta);

        return face;
    }


    public int resolveCollisionWithCompositeRect(float delta, CompositeRect other) {
        int tempFace = -1;
        try {
            //预计下帧位置
            Vector2 deltaVel = velocity.multiply(delta);
            float deltaVelMagnitude = deltaVel.magnitude();
            float k = deltaVel.y / deltaVel.x;
            Rectangle afterRec = new Rectangle(position.add(deltaVel), width, height);
            Vector2 afterPos = afterRec.position;
            //AABB快速碰撞
            Rectangle otherBox = other.getBoundingBox();
            if (!intersects(new Rectangle(afterPos, width, height), otherBox)) {
                return face;
            }

            Vector2 minFinalVel=deltaVel;
            float minFinalVelMagnitude=deltaVelMagnitude;
            for (int i=0;i < other.vertices.length / 2;i++) {
                face = -1;
                Line line = other.getEdge(i);
                
                Rectangle rec = this;
                Vector2 finalVel=new Vector2();
                Vector2 velBack =new Vector2();//回退量
                float backLength;
                Vector2 tranVel=new Vector2();//切向量
                float backMagnitude;
                //贴边y
                float depY = deltaVel.y > 0
                    ?line.minY() - afterRec.topY()
                    : line.maxY() - afterRec.bottomY();
                float mappingX = depY / k;
                velBack = new Vector2(mappingX, depY);
                backLength = velBack.magnitude();
                backMagnitude = deltaVel.add(velBack).magnitude();
                if(!Float.isInfinite(backLength) && backMagnitude <= deltaVelMagnitude
                   && ((deltaVel.y<0&&rec.bottomY()>=line.maxY()) || (deltaVel.y>0&&rec.topY()<=line.minY()))
                   && (!(line.minX()>=rightX() || line.maxX() <= leftX()))
                   ){
                    face = deltaVel.y > 0 ?0: 2;
                    tranVel = new Vector2(-velBack.x, -velBack.y * 0);
                }else{
                    //贴边x
                    float depX = deltaVel.x > 0
                        ?line.minX() - afterRec.rightX()
                        : line.maxX() - afterRec.leftX();
                    float mappingY = k * depX;
                    velBack = new Vector2(depX, mappingY);
                    backLength = velBack.magnitude();
                    backMagnitude = deltaVel.add(velBack).magnitude();
                    if(!Float.isInfinite(backLength) && backMagnitude <= deltaVelMagnitude
                       && ((deltaVel.x<0&&rec.leftX()>=line.maxX()) || (deltaVel.x>0&&rec.rightX()<=line.minX()))
                       && (!(line.minY()>=topY() || line.maxY() <= bottomY()))
                       ){
                        face = deltaVel.x > 0 ?3: 1;
                        tranVel = new Vector2(-velBack.x * 0, -velBack.y);
                    }
                }

                if (face != -1) {
                    //最终速度
                    MainActivity.addDebugTxt("coll: "+face+", velBack: "+velBack);
                    finalVel = deltaVel.add(velBack).add(tranVel);
                    float finalVelMagnitude = finalVel.magnitude();
                    boolean lower = finalVelMagnitude <= minFinalVelMagnitude;
                    minFinalVel = lower ? finalVel : minFinalVel;
                    tempFace = lower ?face: tempFace;
                    minFinalVelMagnitude = minFinalVel.magnitude();
                }
            }
            velocity = minFinalVel.devideBy(delta);//还原为每秒速度 vel/s

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            MainActivity.addDebugTxt(sw.toString());
        }
        return tempFace;
    }

    public static int collisionWithLine(Rectangle rec, float delta, Line line) {
        int face = -1;
        Vector2 deltaVel = rec.velocity.multiply(delta);
        float deltaVelMagnitude = deltaVel.magnitude();
        float k = deltaVel.y / deltaVel.x;
        Rectangle afterRec = new Rectangle(rec.position.add(deltaVel), rec.width, rec.height);

        Vector2 finalVel=null;
        Vector2 velBack = null;//回退量
        float backLength;
        Vector2 tranVel=null;//切向量
        float backMagnitude;
        //贴边y
        float depY = deltaVel.y > 0
            ?line.minY() - afterRec.topY()
            : line.maxY() - afterRec.bottomY();
        float mappingX = depY / k;
        velBack = new Vector2(mappingX, depY);
        backLength = velBack.magnitude();
        backMagnitude = deltaVel.add(velBack).magnitude();
        if (!Float.isInfinite(backLength) && backMagnitude <= deltaVelMagnitude) {
            face = deltaVel.y > 0 ?2: 0;
            tranVel = new Vector2(-velBack.x, -velBack.y * 0);
        } else {
            //贴边x
            float depX = deltaVel.x > 0
                ?line.minX() - afterRec.rightX()
                : line.maxX() - afterRec.leftX();
            float mappingY = k * depX;
            velBack = new Vector2(depX, mappingY);
            backLength = velBack.magnitude();
            backMagnitude = deltaVel.add(velBack).magnitude();
            if (!Float.isInfinite(backLength) && backMagnitude <= deltaVelMagnitude) {
                face = deltaVel.x > 0 ?3: 1;
                tranVel = new Vector2(-velBack.x * 0, -velBack.y);
            }
        }

        if (face != -1) {
            //最终速度
            finalVel = deltaVel.add(velBack).add(tranVel);
            //rec.velocity = finalVel.devideBy(delta);//还原为每秒速度 vel/s

        }

        return face;
    }

    @Override
    public String toString() {
        return "{Rect(" + position.x + ", " + position.y + ", " + width + ", " + height + ")}";
    }


}
