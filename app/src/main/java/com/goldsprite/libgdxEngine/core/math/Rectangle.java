package com.goldsprite.libgdxEngine.core.math;
import com.goldsprite.geometryDash.MainActivity;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Rectangle {
    public Vector2 position;
    public float width, height;
    public Vector2 velocity;// 单位像素每秒 px/s，使用时乘上delta时间步长
    public int face=-1;
    public Vector2 perTrans = new Vector2(0, 0);
    public static float collDeathZone = 60;//直径
    public static float halfCollDeathZone(){
        return collDeathZone/2f;
    }

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
        MainActivity.setDebugTxt("");
        MainActivity.addDebugTxt("recPos: "+position);
        MainActivity.addDebugTxt("collDeachZonePercent: "+(MainActivity.collDeathZone*100)+"%, halfCollDeachZone: "+halfCollDeathZone());
        try {
            //预计下帧位置
            Vector2 deltaVel = velocity.multiply(delta);
            float deltaVelMagnitude = deltaVel.magnitude();
            float k = deltaVel.y / deltaVel.x;
            Rectangle afterRec = new Rectangle(position.add(deltaVel), width, height);

            ArrayList<CollInfo> collInfoX = new ArrayList<>();
            ArrayList<CollInfo> collInfoY = new ArrayList<>();
            Vector2 collEdge = Vector2.zero();
            int collInfoIndex=0;
            Vector2 minFinalVel=deltaVel;
            float minFinalVelMagnitude=deltaVelMagnitude;
            Vector2 tranVel=new Vector2();//切向量
            Vector2 finalEdgeVel = new Vector2();
            Vector2 finalVel=new Vector2(deltaVel);
            CollInfo collInfo = new CollInfo();
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
                boolean inLineX = false, inLineY=false, inRectX=false, inRectY=false;
                Vector2 lineNormal = line.normal();
                boolean backOutof=false;
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
                    oppsiteNormal = deltaVel.y > 0 ? lineNormal.equals(Vector2.down): lineNormal.equals(Vector2.up);//排除同法向边，即平行边，选择垂直边判断
                    inLineX = !(backRec.leftX() + halfCollDeathZone() >= line.maxX() || backRec.rightX() - halfCollDeathZone() <= line.minX());//排除贴边后在线外无碰撞, 这里不写=会造成卡边
                    inRectY = (deltaVel.y > 0 && rec.topY()-halfCollDeathZone() <= line.minY()) || (deltaVel.y < 0 && rec.bottomY()+halfCollDeathZone() >= line.maxY());//(已放弃，无法应对单步速度过快情况，会产生无接触穿透)排除背后边，这里使用保守判断/绝大部分陷入也吃判断(会造成回退过多倒退)，激进型会造成过线一点点就不阻挡了
                    oppsite = Math.signum(deltaVel.y) != Math.signum(velBack.y);//排除距离不够
                    backOutof = backLength > deltaVelMagnitude;//回退超出原速度，排除类似往右却退到背后右墙情况，
                    if (oppsiteNormal && (inRectY || !backOutof) && inLineX && oppsite) {
                        collInfoIndex++;
                        collEdge.y = deltaVel.y > 0 ? 1 : -1;
                        collInfo = new CollInfo();
                        collInfoY.add(collInfo);
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
                    oppsiteNormal = deltaVel.x > 0 ? lineNormal.equals(Vector2.left): lineNormal.equals(Vector2.right);//排除同法向边，即平行边，选择垂直边判断
                    inLineY = !(line.maxY() < backRec.bottomY() + halfCollDeathZone() || line.minY() > backRec.topY() - halfCollDeathZone());
                    inRectX = (deltaVel.x > 0 && rec.rightX()-halfCollDeathZone() <= line.minX()) || (deltaVel.x < 0 && rec.leftX()+halfCollDeathZone() >= line.maxX());
                    oppsite = Math.signum(deltaVel.x) != Math.signum(velBack.x);
                    backOutof = backLength > deltaVelMagnitude;
                    if (oppsiteNormal && (inRectX || !backOutof) && inLineY && oppsite) {
                        collInfoIndex++;
                        collEdge.x = deltaVel.x > 0 ? 1 : -1;
                        collInfo = new CollInfo();
                        collInfoX.add(collInfo);
                        collInfo.collEdge = deltaVel.x > 0 ?Vector2.right : Vector2.left;
                        collInfo.edgeVel = deltaVel.add(velBack);
                        collInfo.trangleVel = new Vector2(-velBack.x * 0, -velBack.y);
                    }
                }
                
                 MainActivity.addDebugTxt(
                 "检测边索引: " + (i)+", 边朝向:"+faceString(line.normal())
                 + "\n过检定次数: " + collInfoIndex
                 + "\n角色速度: " + deltaVel
                 + "\n判定方向: " + faceString(collInfo.collEdge)
                 + "\n回退向量: " + velBack
                 + "\n阻挡后速度: " + deltaVel.add(velBack)
                 + "\n条件: oppsiteNormal:"+oppsiteNormal+", backOutof:"+backOutof+", inLineX:" + inLineX + ",inRectY:" + inRectY + ",inLineY:" + inLineY + ",inRectX:" + inRectX
                 + "\n"
                 );
                 
            }

            //检测到碰撞的情况
            if (!collEdge.isZero()) {
                //同轴多条线碰撞选出最近那条
                CollInfo nearCollInfoY=collEdge.y != 0 ? collInfoY.get(0): null;
                if (collInfoY.size() > 1) {
                    Iterator ite = collInfoY.iterator();
                    nearCollInfoY = (CollInfo)ite.next();
                    while (ite.hasNext()) {
                        CollInfo c = (CollInfo)ite.next();
                        if (c.edgeVel.magnitude() < nearCollInfoY.edgeVel.magnitude()) {
                            nearCollInfoY = c;
                        }
                    }
                }
                CollInfo nearCollInfoX=collEdge.x != 0 ? collInfoX.get(0) : null;
                if (collInfoX.size() > 1) {
                    Iterator ite = collInfoX.iterator();
                    nearCollInfoX = (CollInfo)ite.next();
                    while (ite.hasNext()) {
                        CollInfo c = (CollInfo)ite.next();
                        if (c.edgeVel.magnitude() < nearCollInfoX.edgeVel.magnitude()) {
                            nearCollInfoX = c;
                        }
                    }
                }

                //根据最近碰撞轴线，判断碰撞为单轴还是双轴，计算出最终速度
                if (collEdge.x * collEdge.y == 0) {
                    collInfo = collEdge.x == 0 ?nearCollInfoY : nearCollInfoX;
                    finalVel = collInfo.edgeVel.add(collInfo.trangleVel);
                } else {
                    collInfo = nearCollInfoY;
                    collInfo.edgeVel.x = nearCollInfoX.edgeVel.x;
                    finalVel = collInfo.edgeVel;
                }
            }

            MainActivity.addDebugTxt(
                "\n完成检测，结果如下: "
                + "\n初始速度方向: " + faceString(deltaVel.normalize())
                + "\n最终判定方向: " + faceString(collEdge)
                + "\n过检定次数: " + collInfoIndex
                + "\n角色速度: " + deltaVel
                + "\n阻挡后速度: " + finalVel
                + "\n"
            );
            finalVel = finalVel.subtract(finalVel.normalize().multiply(0.05f));//为了解决浮点数精度不精导致的略微嵌入问题
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
