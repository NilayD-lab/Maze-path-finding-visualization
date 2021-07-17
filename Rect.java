import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import java.awt.*;
import java.beans.EventHandler;
import java.net.URI;
import javafx.scene.layout.BorderStroke;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import org.w3c.dom.Text;

public class Rect extends Rectangle{
    private int g;
    private int h;
    private Rect parent;
    private int num;
    private int xpos;
    private int ypos;
    private boolean isOn;
    private Color onColor;
    private int length;
    private Color offColor;
    private Scene scene;
    public Rect(Color f, Color e, int n, Scene scene, int x, int y, int len){
        super(x, y, len, len);
        g=Integer.MAX_VALUE;
        h=Integer.MAX_VALUE;
        onColor = f;
        super.setFill(e);
        super.setStroke(Color.BLACK);
        super.setStrokeWidth(1);
        xpos=x;
        ypos=y;
        num=n;
        isOn=false;
        length=len;
        offColor = e;
        this.scene = scene;
    }
    public Rect(){

    }
    public void setToNothing(){
        super.setOnMouseClicked(e-> System.out.print(""));
    }
    public void setOn(boolean x){
        isOn = x;
    }
    public void setColor(){
        super.setOnMouseClicked(e-> {
            if (isOn) {
                super.setFill(offColor);
                isOn=false;
            }
            else {
                super.setFill(onColor);
                isOn=true;
            }
        });

    }

    @Override
    public String toString() {
        return getR() + " && " + getC();
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    public int getR(){
        return (ypos-25)/length;
    }
    public int getC(){
        return xpos/length;
    }

    public void setOffColor(Color offColor) {
        this.offColor = offColor;
    }

    public void setOnColor(Color onColor) {
        this.onColor = onColor;
    }

    public Color getOnColor() {
        return onColor;
    }
    public Color getOffColor(){
        return offColor;
    }
    public int getXpos() {
        return xpos;
    }

    public int getYpos() {
        return ypos;
    }
    public void setParent(Rect rect){
        parent = new Rect(rect.getOffColor(), rect.getOnColor(), rect.getNum(), rect.getScene(), rect.getXpos(), rect.getYpos(),length );
    }

    public Rect getTheParent() {
        return parent;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }
}