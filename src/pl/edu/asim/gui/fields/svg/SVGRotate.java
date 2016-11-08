package pl.edu.asim.gui.fields.svg;

import java.awt.event.MouseEvent;

import java.math.BigDecimal;


public class SVGRotate {

    protected boolean used;
    protected double initialRotation;
//    protected int width;
//    protected int height;
    protected int x;
    protected int y;

    protected int firstx;
    protected int firsty;

    double angle=0;
    int degreeAngle=0;
    SVGElement element;

    public SVGRotate (SVGElement element){
      this.element = element;
      used = false;
    }
    
    public void mousePressed(MouseEvent e) {
        
//        String value = "";
//        used = false;
//        String transform = element.attributeMap.get(SVGConstant.A_TYPE_23).getText();
//        int translateIndex = transform.indexOf("rotate(");
//        if(translateIndex!=-1) {
//          String translate = transform.substring(translateIndex+7);
//          int pr = translate.indexOf(",");  
//          if(pr==-1) pr = translate.indexOf(")");
//          value = translate.substring(0,pr);
//        }
//        x = element.getXcenter();    
//        y = element.getYcenter();    
//        double dx = e.getX() - x;
//        double dy = e.getY() - y;
//        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
//        initialRotation = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);
//        if(!value.equals("")) initialRotation = initialRotation - Math.toRadians(new Double(value).doubleValue());
    }


    public void mouseReleased(MouseEvent e) {
    //    finished = true;
    }

    public void mouseExited(MouseEvent e) {
    //    finished = true;
    }

    public void mouseDragged(MouseEvent e) {
        rotateTransform(e.getX(), e.getY());
    }

    public void keyPressed(int x) {
        
//        //System.out.println("keyPressed "+x);
//        String value = "";
//        int angle;
//        String transform = element.attributeMap.get(SVGConstant.A_TYPE_23).getText();
//        int translateIndex = transform.indexOf("rotate(");
//        if(translateIndex!=-1) {
//          String translate = transform.substring(translateIndex+7);
//          int pr = translate.indexOf(",");  
//          value = translate.substring(0,pr);
//        }
//        //System.out.println("value="+value);
//        if(!value.equals("")) angle = new BigDecimal(value).intValue();
//        else angle = 0;
//        angle = angle + x;
//        element.setRotate(angle,element.getXcenter(),element.getYcenter());
//        //System.out.println(angle);
    }


/*    public void updateRotate() {
        String value = "";
        String transform = element.attributeMap.get(SVGConstant.A_TYPE_23).getText();
        int translateIndex = transform.indexOf("rotate(");
        if(translateIndex!=-1) {
          String translate = transform.substring(translateIndex+7);
          int pr = translate.indexOf(",");  
          value = translate.substring(0,pr);
        }
        x = element.getXcenter();           System.out.println("dx "+dx+" dy+"+dy);
 
        y = element.getYcenter();
        
        if(!value.equals("")) 
        element.updateRotate(Math.toDegrees(new Double(value).doubleValue()),this.x,this.y);
       // else
       // element.updateRotate(0.0,this.x,this.y);
    }
*/    
    protected void rotateTransform(int x, int y) {
//        double dx = x - this.x;
//        double dy = y - this.y;
//        used = true;
//        //System.out.println("dx "+dx+" dy+"+dy);
//        double cos = -dy / Math.sqrt(dx * dx + dy * dy);
//        //System.out.println("cos "+cos);
//        double newAngle = (dx > 0) ? Math.acos(cos) : -Math.acos(cos);
//        newAngle -= initialRotation;
//        int newDegreeAngle = new Double(Math.toDegrees(newAngle)).intValue();
//        if(newDegreeAngle!=degreeAngle) {
//            angle = newAngle;
//            degreeAngle = newDegreeAngle;
//            element.setRotate(degreeAngle,this.x,this.y);
//        }
    }
}
