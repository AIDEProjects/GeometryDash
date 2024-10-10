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
        return rect.leftX() <= other.rightX() &&
            rect.rightX() >= other.leftX() &&
            rect.bottomY() <= other.topY() &&
            rect.topY() >= other.bottomY();
    }

    public int resolveCollisionWithCompositeRect(float delta, CompositeRect other) {
        int tempFace = -1;
        int face;
        int collEdge=0;
        try {
            //预计下帧位置
            Vector2 deltaVel = velocity.multiply(delta);
            float deltaVelMagnitude = deltaVel.magnitude();
            float k = deltaVel.y / deltaVel.x;
            Rectangle afterRec = new Rectangle(position.add(deltaVel), width, height);
            
            
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
                    finalVel = deltaVel.add(velBack).add(tranVel);
                    float finalVelMagnitude = finalVel.magnitude();
                    boolean lower = finalVelMagnitude <= minFinalVelMagnitude;
                    minFinalVel = lower ? finalVel : minFinalVel;
                    tempFace = lower ?face: tempFace;
                    collEdge = lower ? i : collEdge;
                    minFinalVelMagnitude = minFinalVel.magnitude();
                    MainActivity.addDebugTxt("collEdge: "+i+", face: "+faceString(face)+", planVel: "+deltaVel+", realVel: "+finalVel);
                }
            }
            MainActivity.addDebugTxt("realCollEdge: "+collEdge+", finalFace: "+faceString(tempFace)+", planVel: "+deltaVel+", realVel: "+minFinalVel);
            velocity = minFinalVel.devideBy(delta);//还原为每秒速度 vel/s

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            MainActivity.addDebugTxt(sw.toString());
        }
        return tempFace;
    }

    public String faceString(int face){
        String str="";
        switch(face){
            case 0: 
                str="上";
                break;
            case 1: 
                str="左";
                break;
            case 2: 
                str="下";
                break;
            case 3: 
                str="右";
                break;
        }
        return str+"方";
    }

    @Override
    public String toString() {
        return "{Rect(" + position.x + ", " + position.y + ", " + width + ", " + height + ")}";
    }


}
