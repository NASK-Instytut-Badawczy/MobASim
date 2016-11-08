package pl.edu.asim.gui.fields.svg;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pl.edu.asim.gui.Modeler;

import pl.edu.asim.gui.fields.AttributeInterface;
import pl.edu.asim.gui.fields.ColorField;
import pl.edu.asim.gui.fields.DoubleField;
import pl.edu.asim.gui.fields.FileField;
import pl.edu.asim.gui.fields.ListField;
import pl.edu.asim.gui.fields.TextField;

public class SVGConstant {

	public static String TYPE_0 = "desc";
	public static String TYPE_0t = "title";
	public static String TYPE_1 = "image";
	public static String TYPE_2 = "rect";
	public static String TYPE_3 = "text";
	public static String TYPE_4 = "circle";
	public static String TYPE_5 = "ellipse";
	public static String TYPE_6 = "line";
	public static String TYPE_7 = "polyline";
	public static String TYPE_8 = "polygon";
	public static String TYPE_9 = "path";
	// public static String TYPE_10 = "use";
	public static String TYPE_11 = "point";
	public static String TYPE_12 = "g";
	public static String TYPE_12a = "svg";

	public static String A_TYPE_0 = "xlink:href";
	public static String A_TYPE_1 = "r";
	public static String A_TYPE_2 = "x";
	public static String A_TYPE_3 = "y";
	public static String A_TYPE_4 = "cx";
	public static String A_TYPE_5 = "cy";
	public static String A_TYPE_6 = "rx";
	public static String A_TYPE_7 = "ry";
	public static String A_TYPE_8 = "width";
	public static String A_TYPE_9 = "height";
	public static String A_TYPE_10 = "fill";
	public static String A_TYPE_11 = "stroke";
	public static String A_TYPE_12 = "points";
	public static String A_TYPE_13 = "x1";
	public static String A_TYPE_14 = "y1";
	public static String A_TYPE_15 = "x2";
	public static String A_TYPE_16 = "y2";
	public static String A_TYPE_17 = "stroke-width";
	public static String A_TYPE_18 = "font-family";
	public static String A_TYPE_19 = "font-size";
	public static String A_TYPE_20 = "text";
	public static String A_TYPE_21 = "dx";
	public static String A_TYPE_22 = "dy";
	public static String A_TYPE_23 = "transform";
	public static String A_TYPE_24 = "opacity";
	public static String A_TYPE_25 = "stroke-dasharray";
	public static String A_TYPE_26 = "d";
	public static String A_TYPE_27 = "xmlns:xlink";
	// public static String A_TYPE_28 = " xlink:href";
	public static String A_TYPE_29 = "scale";
	public static String A_TYPE_31 = "marker-start";
	public static String A_TYPE_32 = "marker-end";
	public static String A_TYPE_33 = "marker-mid";
	public static String A_TYPE_34 = "visibility";
	public static String A_TYPE_35 = "stroke-linecap";
	public static String A_TYPE_36 = "id";

	/*
	 * public static JLabel getLabel(String s) { JLabel l = new JLabel(s,
	 * JLabel.RIGHT); l.setPreferredSize(new Dimension(80, 25)); return l; }
	 */
	public static AttributeInterface getField(String s) {
		AttributeInterface result = null;
		if (s.equals(A_TYPE_0)) {
			result = new FileField();
			((FileField) result).setExtensions("*.gif,*.png,*.jpg, *.svg ...");
			result.setText("../data/images/info.png");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_10) || s.equals(A_TYPE_11)) {
			result = new ColorField();
			result.setText("none");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_12) || s.equals(A_TYPE_20)
				|| s.equals(A_TYPE_23)) {
			result = new TextField();
			result.setText("");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_36)) {
			result = new TextField();
			result.setText("");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_18)) {
			result = new TextField();
			result.setText("Verdana");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_24) || s.equals(A_TYPE_29)) {
			result = new DoubleField();
			result.setText("1.00");
			((DoubleField) result).setMin(0);
			((DoubleField) result).setMax(1);
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_25)) {
			result = new TextField();
			result.setText("0,0");
			// result.addModelerActionListener(element);
		} else if (s.equals(A_TYPE_31) || s.equals(A_TYPE_32)
				|| s.equals(A_TYPE_33)) {

			result = new ListField();
			((ListField) result).addRow("url(#Triangle-y)", "Triangle-y");
			((ListField) result).addRow("url(#Circle-y)", "Circle-y");
			((ListField) result).addRow("none", "none");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_34)) {
			result = new ListField();
			((ListField) result).addRow("visible", "visible");
			((ListField) result).addRow("hidden", "hidden");
			((ListField) result).addRow("inherit", "inherit");
			((ListField) result).setText("inherit");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_35)) {
			result = new ListField();
			((ListField) result).addRow("butt", "butt");
			((ListField) result).addRow("round", "round");
			((ListField) result).addRow("square", "square");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_27)) {
			result = new TextField();
			result.setText("");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} else if (s.equals(A_TYPE_26)) {
			result = new TextField();
			result.setText("M0,0 ");
			// result.addModelerActionListener(element);
			//result.getDocument().addDocumentListener(element);
		} 
//		else {
//			result = new DoubleField();
//			result.setText("0.00");
//			((DoubleField) result).setStepSize(new BigDecimal("1"));
//			// result.addModelerActionListener(element);
//			//result.getDocument().addDocumentListener(element);
//		}
//		((JPanel) result).setPreferredSize(new Dimension(400, 25));
//		result.setModeler(modeler);
		return result;
	}

	public static ArrayList<String> getAttributeList(String elementType) {
		ArrayList<String> result = new ArrayList<String>();

		if (elementType.equals(TYPE_0)) {
			result.add(A_TYPE_20);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_0t)) {
			result.add(A_TYPE_20);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_1)) {
			result.add(A_TYPE_0);
			result.add(A_TYPE_2);
			result.add(A_TYPE_3);
			result.add(A_TYPE_8);
			result.add(A_TYPE_9);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_2)) {
			result.add(A_TYPE_2);
			result.add(A_TYPE_3);
			result.add(A_TYPE_8);
			result.add(A_TYPE_9);
			result.add(A_TYPE_6);
			result.add(A_TYPE_7);
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_17);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_3)) {
			result.add(A_TYPE_20);
			result.add(A_TYPE_2);
			result.add(A_TYPE_3);
			result.add(A_TYPE_21);
			result.add(A_TYPE_22);
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_17);
			result.add(A_TYPE_18);
			result.add(A_TYPE_19);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_4)) {
			result.add(A_TYPE_1);
			result.add(A_TYPE_4);
			result.add(A_TYPE_5);
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_17);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_5)) {
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_17);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_4);
			result.add(A_TYPE_5);
			result.add(A_TYPE_6);
			result.add(A_TYPE_7);
			result.add(A_TYPE_25);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_6)) {
			result.add(A_TYPE_13);
			result.add(A_TYPE_14);
			result.add(A_TYPE_15);
			result.add(A_TYPE_16);
			result.add(A_TYPE_17);
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_31);
			result.add(A_TYPE_32);
			result.add(A_TYPE_33);
			result.add(A_TYPE_34);
			result.add(A_TYPE_35);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_7)) {
			result.add(A_TYPE_11);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_12);
			result.add(A_TYPE_17);
			result.add(A_TYPE_25);
			result.add(A_TYPE_31);
			result.add(A_TYPE_32);
			result.add(A_TYPE_33);
			result.add(A_TYPE_34);
			result.add(A_TYPE_35);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_8)) {
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_12);
			result.add(A_TYPE_17);
			result.add(A_TYPE_25);
			result.add(A_TYPE_31);
			result.add(A_TYPE_32);
			result.add(A_TYPE_33);
			result.add(A_TYPE_34);
			result.add(A_TYPE_35);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_9)) {
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_17);
			result.add(A_TYPE_26);
			result.add(A_TYPE_31);
			result.add(A_TYPE_32);
			result.add(A_TYPE_33);
			result.add(A_TYPE_34);
			result.add(A_TYPE_35);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_11)) {
			result.add(A_TYPE_2);
			result.add(A_TYPE_3);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_12)) {
			result.add(A_TYPE_20);
			result.add(A_TYPE_1);
			result.add(A_TYPE_10);
			result.add(A_TYPE_11);
			result.add(A_TYPE_17);
			result.add(A_TYPE_18);
			result.add(A_TYPE_19);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_25);
			result.add(A_TYPE_34);
			result.add(A_TYPE_35);
			result.add(A_TYPE_36);
		} else if (elementType.equals(TYPE_12a)) {
			result.add(A_TYPE_2);
			result.add(A_TYPE_3);
			result.add(A_TYPE_8);
			result.add(A_TYPE_9);
			result.add(A_TYPE_23);
			result.add(A_TYPE_24);
			result.add(A_TYPE_34);
			result.add(A_TYPE_36);
		}
		return result;
	}
	/*
	 * public static HashMap<String, JLabel> getLabelMap(String elementType) {
	 * HashMap<String, JLabel> labelMap = new HashMap<String, JLabel>();
	 * ArrayList<String> typeList = SVGConstant.getAttributeList(elementType);
	 * 
	 * for (Iterator<String> it = typeList.iterator(); it.hasNext(); ) { String
	 * t = (String)it.next(); labelMap.put(t, getLabel(t)); } return labelMap; }
	 */
	/*
	 * public static HashMap<String, AttributeInterface> getAttributeMap(String
	 * elementType, SVGElement element, Modeler modeler) { HashMap<String,
	 * AttributeInterface> attributeMap = new HashMap<String,
	 * AttributeInterface>(); ArrayList<String> typeList =
	 * SVGConstant.getAttributeList(elementType);
	 * 
	 * for (Iterator<String> it = typeList.iterator(); it.hasNext(); ) { String
	 * t = (String)it.next(); attributeMap.put(t, getField(t, element,
	 * modeler)); } return attributeMap; }
	 */
	
	public static String getNS(String attributeType){
		String result = "";
		if (attributeType.equals(A_TYPE_0)) {
			result = "http://www.w3.org/1999/xlink";
		}
		return result;
	}
}
