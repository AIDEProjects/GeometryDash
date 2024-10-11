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
    public float collDeathZone = 20;

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
        return !(
            rect.leftX() > other.rightX() ||
            rect.rightX() < other.leftX() ||
            rect.bottomY() > other.topY() ||
            rect.topY() < other.bottomY()
            );
    }

    //复合矩形边列表必须符合逆时针绘制顺序，以便法向判定无误
    public int resolveCollisionWithCompositeRect(float delta, CompositeRect other) {
        try {
            MainActivity.setDebugTxt("");
            //预计下帧位置
            Vector2 deltaVel = velocity.multiply(delta);
            float deltaVelMagnitude = deltaVel.magnitude();
            float k = deltaVel.y / deltaVel.x;
            Rectangle afterRec = new Rectangle(position.add(deltaVel), width, height);

            Vector2 collEdge = Vector2.zero();
            CollInfo[] collInfos = new CollInfo[2];
            CollInfo collInfo = new CollInfo();
            int collInfoIndex=0;
            Vector2 minFinalVel=deltaVel;
            float minFinalVelMagnitude=deltaVelMagnitude;
            Vector2 tranVel=new Vector2();//切向量
            Vector2 finalEdgeVel = new Vector2();
            Vector2 finalVel=new Vector2(deltaVel);
            for (int i=0;i < other.vertices.length / 2f;i++) {
                Line line = other.getEdge(i);

                Rectangle rec = this;
                Rectangle backRec;
                Rectangle lineRec;
                Vector2 velBack =new Vector2();//回退量
                float backLength;
                float backMagnitude;
                String detectEdge="";
                boolean inLine, oppsite;
                Vector2 indexVel;
                boolean oppsiteNormal=false;
                collInfo = new CollInfo();
                boolean inLineX = false, inLineY=false, inRectX=false, inRectY=false;
                Vector2 lineNormal = line.normal();

                //纵轴碰撞
                if (deltaVel.y != 0 && line.k() == 0) {
                    float depY = deltaVel.y > 0
                        ?line.minY() - afterRec.topY()
                        : line.maxY() - afterRec.bottomY();
                    float mappingX = depY / k;
                    velBack = new Vector2(mappingX, depY);
                    backLength = velBack.magnitude();
                    backMagnitude = deltaVel.add(velBack).magnitude();
                    backRec = new Rectangle(afterRec.position.add(velBack), width, height);
                    oppsiteNormal = deltaVel.y>0? lineNormal.equals(Vector2.down): lineNormal.equals(Vector2.up);//排除同法向边，即平行边，选择垂直边判断
                    inLineX = !(backRec.leftX()+collDeathZone >= line.maxX() || backRec.rightX()-collDeathZone <= line.minX());//排除贴边后在线外无碰撞, 这里不写=会造成卡边
                    inRectY = !((deltaVel.y > 0 && line.minY() <= rec.bottomY()) || deltaVel.y < 0 && line.maxY() >= rec.topY());//排除背后边，这里使用保守判断/绝大部分陷入也吃判断
                    oppsite = Math.signum(deltaVel.y) != Math.signum(velBack.y);//排除距离不够
                    if (oppsiteNormal && inRectY && inLineX && oppsite) {
                        collInfo = new CollInfo();
                        collInfos[collInfoIndex++] = collInfo;
                        collInfo.collEdge = deltaVel.y > 0 ?Vector2.up : Vector2.down;
                        collInfo.edgeVel = deltaVel.add(velBack);
                        collInfo.trangleVel = new Vector2(-velBack.x, -velBack.y * 0);
                    }
                }
                /*
                if (deltaVel.x != 0 && deltaVel.y != 0 && i == 4 && collInfoIndex == 0) {
                    System.out.println("");
                }*/
                //横轴碰撞
                if (deltaVel.x != 0 && Float.isInfinite(line.k())) {
                    float depX = deltaVel.x > 0
                        ?line.minX() - afterRec.rightX()
                        : line.maxX() - afterRec.leftX();
                    float mappingY = k * depX;
                    velBack = new Vector2(depX, mappingY);
                    backLength = velBack.magnitude();
                    backMagnitude = deltaVel.add(velBack).magnitude();
                    backRec = new Rectangle(afterRec.position.add(velBack), width, height);
                    oppsiteNormal = deltaVel.x>0? lineNormal.equals(Vector2.left): lineNormal.equals(Vector2.right);//排除同法向边，即平行边，选择垂直边判断
                    inLineY = !(backRec.bottomY()+collDeathZone >= line.maxY() || backRec.topY()-collDeathZone <= line.minY());
                    inRectX = !((deltaVel.x > 0 && line.maxX() <= rec.leftX()) || deltaVel.x < 0 && line.minX() >= rec.rightX());
                    oppsite = Math.signum(deltaVel.x) != Math.signum(velBack.x);
                    if (oppsiteNormal && inRectX && inLineY && oppsite) {
                        collInfo = new CollInfo();
                        collInfos[collInfoIndex++] = collInfo;
                        collInfo.collEdge = deltaVel.x > 0 ?Vector2.right : Vector2.left;
                        collInfo.edgeVel = deltaVel.add(velBack);
                        collInfo.trangleVel = new Vector2(-velBack.x * 0, -velBack.y);
                    }
                }
                
                /*
                MainActivity.addDebugTxt(
                    "检测边索引: " + (i)
                    + "\n过检定次数: " + collInfoIndex
                    + "\n角色速度: " + deltaVel
                    + "\n判定方向: " + faceString(collInfo.collEdge)
                    + "\n回退向量: " + velBack
                    + "\n阻挡后速度: " + deltaVel.add(velBack)
                    + "\n两条件: inLineX:" + inLineX + ",inRectY:" + inRectY + ",inLineY:" + inLineY + ",inRectX:" + inRectX
                    + "\n"
                );*/

            }

            if (collInfoIndex == 1) {
                finalVel = collInfos[0].edgeVel.add(collInfos[0].trangleVel);
                collInfo.collEdge = collInfos[0].collEdge;
            } else if (collInfoIndex == 2) {
                collInfo.collEdge = collInfos[0].collEdge.add(collInfos[1].collEdge);
                if (collInfos[0].collEdge.x != 0) {
                    finalVel.x = collInfos[0].edgeVel.x;
                }
                if (collInfos[0].collEdge.y != 0) {
                    finalVel.y = collInfos[0].edgeVel.y;
                }
                if (collInfos[1].collEdge.x != 0) {
                    finalVel.x = collInfos[1].edgeVel.x;
                }
                if (collInfos[1].collEdge.y != 0) {
                    finalVel.y = collInfos[1].edgeVel.y;
                }
                
                if(finalVel.y !=0){
                    System.out.println("");
                }
            }

            MainActivity.addDebugTxt(
                "\n完成检测，结果如下: "
                + "\n最终判定方向: " + faceString(collInfo.collEdge)
                + "\n过检定次数: " + collInfoIndex
                + "\n角色速度: " + deltaVel
                + "\n阻挡后速度: " + finalVel
                + "\n"
            );
            //MainActivity.addDebugTxt("realCollEdge: "+collEdge+", finalFace: "+faceString(tempFace)+", planVel: "+deltaVel+", realVel: "+minFinalVel);
            velocity = finalVel.devideBy(delta);//还原为每秒速度 vel/s

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            MainActivity.addDebugTxt(sw.toString());
        }
        return -1;
    }

    class CollInfo {
        Vector2 collEdge = Vector2.zero();
        Vector2 edgeVel=Vector2.zero();
        Vector2 trangleVel=Vector2.zero();
    }

    public String faceString(int face) {
        switch (face) {
            case 0: 
                return "上";
            case 1: 
                return "左";
            case 2: 
                return "下";
            case 3: 
                return "右";
            default: 
                return "无";
        }
    }
    public String faceString(Vector2 collEdge) {
        String str = (collEdge.x == 0 ?"": collEdge.x > 0 ?"右": "左") + (collEdge.y == 0 ?"": collEdge.y > 0 ?"上": "下");
        return str;
    }

    @Override
    public String toString() {
        return "{Rect(" + position.x + ", " + position.y + ", " + width + ", " + height + ")}";
    }


}
