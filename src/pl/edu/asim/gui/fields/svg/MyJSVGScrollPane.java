package pl.edu.asim.gui.fields.svg;

import java.awt.geom.Rectangle2D;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;

public class MyJSVGScrollPane extends JSVGScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3064828550499857117L;

	public MyJSVGScrollPane(JSVGCanvas arg0) {
		super(arg0);
	}
	
	public Rectangle2D getRect() {
		return super.getViewBoxRect();
	}

	public int getVerticalValue(){
		return vertical.getValue();
	}

	public int getHorizontalValue(){
		return horizontal.getValue();
	}

}
