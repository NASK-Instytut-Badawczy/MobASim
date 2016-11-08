package pl.edu.asim.gui.fields.svg;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import pl.edu.asim.gui.Modeler;
import pl.edu.asim.gui.actions.FieldUpdateAction;
import pl.edu.asim.gui.fields.AttributeInterface;
import pl.edu.asim.gui.fields.ListField;
import pl.edu.asim.gui.fields.ModelerActionListener;

public class SVGElement extends JPanel implements ActionListener,
		ChangeListener, ModelerActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;

	org.w3c.dom.Element sourceElement;

	// svgconst musi aktualizowac mape
	// zmianu w dokumencie bez przeladowywawania bezposrednio

	public org.w3c.dom.Element getSourceElement() {
		return sourceElement;
	}

	public void setSourceElement(org.w3c.dom.Element sourceElement) {
		this.sourceElement = sourceElement;
	}

	public ListField elementType;
	public JLabel elementTypeLabel;

	// public HashMap<String, AttributeInterface> attributeMap;
	// public HashMap<String, JLabel> labelMap;

	public List<SVGAttribute> attributes;
	public HashMap<String, SVGAttribute> attributesMap;

	String svgNS;
	// DOMImplementation impl;

	DefaultSVGEditorField editor;
	public Integer index;

	public int maxWidth;
	public int maxHeight;

	public SVGRotate rotate;

	// public boolean nodeReloaded = true;
	// public boolean visible = false;

	// String gmlNS = "http://www.opengis.net/gml";

	Modeler modeler;
	Dimension textFieldDimension;
	Dimension labelDimension;

	public SVGElement(int i, String n, String svgNs, DOMImplementation impl,
			DefaultSVGEditorField dsef) {
		super(new FlowLayout());
		name = n;
		editor = dsef;
		modeler = dsef.manager.getModeler();
		this.index = new Integer(i);
		this.svgNS = svgNs;
		this.setPreferredSize(new Dimension(520, 200));
		this.setName(n);
		maxWidth = new BigDecimal(dsef.getElement().getAttribute("width"))
				.intValue();
		maxHeight = new BigDecimal(dsef.getElement().getAttribute("height"))
				.intValue();
		this.textFieldDimension = (Dimension) modeler.getContext().getBean(
				"textFieldDimension");
		this.labelDimension = (Dimension) modeler.getContext().getBean(
				"labelDimension");
		// attributeMap = SVGConstant.getAttributeMap(SVGConstant.TYPE_0, this,
		// modeler);
		// labelMap = SVGConstant.getLabelMap(SVGConstant.TYPE_0);
		attributes = new ArrayList<SVGAttribute>();
		attributesMap = new HashMap<String, SVGAttribute>();

		elementType = new ListField();
		// elementType.setModeler(modeler);
		elementType.addRow(SVGConstant.TYPE_0, SVGConstant.TYPE_0);
		elementType.addRow(SVGConstant.TYPE_0t, SVGConstant.TYPE_0t);
		elementType.addRow(SVGConstant.TYPE_1, SVGConstant.TYPE_1);
		elementType.addRow(SVGConstant.TYPE_2, SVGConstant.TYPE_2);
		elementType.addRow(SVGConstant.TYPE_3, SVGConstant.TYPE_3);
		elementType.addRow(SVGConstant.TYPE_4, SVGConstant.TYPE_4);
		elementType.addRow(SVGConstant.TYPE_5, SVGConstant.TYPE_5);
		elementType.addRow(SVGConstant.TYPE_6, SVGConstant.TYPE_6);
		elementType.addRow(SVGConstant.TYPE_7, SVGConstant.TYPE_7);
		elementType.addRow(SVGConstant.TYPE_8, SVGConstant.TYPE_8);
		elementType.addRow(SVGConstant.TYPE_9, SVGConstant.TYPE_9);
		// elementType.addRow(SVGConstant.TYPE_10, SVGConstant.TYPE_10);
		elementType.addRow(SVGConstant.TYPE_12, SVGConstant.TYPE_12);
		elementType.addRow(SVGConstant.TYPE_12a, SVGConstant.TYPE_12a);
		elementType.setText(SVGConstant.TYPE_0);
		elementType.addModelerActionListener(this);
		// elementType.addActionListener(this);
		// elementType.setA
		// elementType.getDocument().addDocumentListener(this);

		elementType.setName(name);

		((JPanel) elementType).setPreferredSize(new Dimension(400, 25));
		elementTypeLabel = new JLabel("SVG element", JLabel.RIGHT);
		elementTypeLabel.setPreferredSize(new Dimension(80, 25));

		// select(SVGConstant.TYPE_0);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// name = attributeMap.get(SVGConstant.A_TYPE_36).getText();
		// if (e != null && !e.getSource().equals(elementType.getComponent()))
		// editor.reloadNode(this, ((Component)e.getSource()).getName());
		// else {
		// System.out.println("state");
		// String type = elementType.getText();
		// select(type);
		// editor.reloadNodeWithType(this);
		// }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("action");
		/*
		 * // String type = elementType.getText(); // if (nodeReloaded &&
		 * e.getSource() != null && //
		 * e.getSource().equals(elementType.getComponent())) { // select(type);
		 * // } // name = attributeMap.get(SVGConstant.A_TYPE_36).getText();
		 * //if (nodeReloaded) // && //(!type.equals(SVGConstant.TYPE_10) ||
		 * (!attributeMap.get(SVGConstant.A_TYPE_28).getText().equals("") && //
		 * !attributeMap.get(SVGConstant.A_TYPE_28).getText().equals("#")))) //
		 * { if (e.getSource() != null &&
		 * e.getSource().equals(elementType.getComponent())) {
		 * editor.reloadNodeWithType(this); // } else if
		 * (type.equals(SVGConstant.TYPE_10) && // e.getSource() != null && //
		 * e.
		 * getSource().equals(attributeMap.get(SVGConstant.A_TYPE_28).getComponent
		 * ())) { // // org.w3c.dom.Element use = //
		 * editor.getSymbolMap().get(attributeMap
		 * .get(SVGConstant.A_TYPE_28).getText().substring(1)); // if (use !=
		 * null) { // String height = use.getAttribute("height"); //
		 * attributeMap.get("height").setText(height); // String width =
		 * use.getAttribute("width"); //
		 * attributeMap.get("width").setText(width); // String name =
		 * use.getAttribute("name"); // attributeMap.get("id").setText(name); //
		 * editor.reloadNodeWithType(this); // }
		 * 
		 * // } else if (type.equals(SVGConstant.TYPE_9) && // e.getSource() !=
		 * null && // e.getActionCommand().equals("RELOAD_PATH")) { //
		 * reloadPath(); // editor.reloadNode(this,
		 * ((Component)e.getSource()).getName()); //// if
		 * (((Component)e.getSource()).getName().equals(SVGConstant.A_TYPE_30))
		 * //// editor.reloadNode(this, SVGConstant.A_TYPE_26); // //
		 * editor.reloadNodeWithType(index); } else if (e.getSource() != null) {
		 * editor.reloadNode(this, ((Component)e.getSource()).getName()); } else
		 * { editor.reloadNodeWithType(this); } // }
		 */}

	@Override
	public void setName(String n) {
		name = n;
		super.setName(n);
	}

	/*
	 * public int getXcenter() { int width = new
	 * BigDecimal((attributeMap.get(SVGConstant.A_TYPE_8) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_8).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_1) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_1).getText() : "0")).intValue(); int
	 * x = new BigDecimal((attributeMap.get(SVGConstant.A_TYPE_2) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_2).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_4) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_4).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_13) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_13).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_21) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_21).getText() : "0")))).intValue();
	 * return (x + (width / 2)); }
	 * 
	 * public int getYcenter() { int height = new
	 * BigDecimal((attributeMap.get(SVGConstant.A_TYPE_9) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_9).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_1) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_1).getText() : "0")).intValue(); int
	 * y = new BigDecimal((attributeMap.get(SVGConstant.A_TYPE_3) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_3).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_5) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_5).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_14) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_14).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_22) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_22).getText() : "0")))).intValue();
	 * return (y + (height / 2)); }
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * public void reloadPath() {
	 * 
	 * String connection = attributeMap.get(SVGConstant.A_TYPE_30).getText();
	 * int i = connection.indexOf(":"); if (i == -1) return; nodeReloaded =
	 * false; String from = connection.substring(0, i); String to =
	 * connection.substring(i + 1); org.w3c.dom.Element fE =
	 * editor.getInternalPoints().get(from); org.w3c.dom.Element tE =
	 * editor.getInternalPoints().get(to);
	 * 
	 * String startPoint = (fE != null) ? fE.getAttribute("newD").substring(1) :
	 * "0,0"; //newD String endPoint = (tE != null) ?
	 * tE.getAttribute("newD").substring(1) : "0,0"; //newD i =
	 * startPoint.indexOf(","); BigDecimal xS = new
	 * BigDecimal(startPoint.substring(0, i)); BigDecimal yS = new
	 * BigDecimal(startPoint.substring(i + 1)); i = endPoint.indexOf(",");
	 * BigDecimal xE = new BigDecimal(endPoint.substring(0, i)); BigDecimal yE =
	 * new BigDecimal(endPoint.substring(i + 1));
	 * 
	 * String path = attributeMap.get("d").getText(); i = path.indexOf(" "); if
	 * (i < 0) { path = "M" + xS.floatValue() + "," + yS.floatValue() + " L" +
	 * xE.floatValue() + "," + yE.floatValue(); } else { path =
	 * path.substring(i); i = path.lastIndexOf(" "); if (i >= 0) path =
	 * path.substring(0, i); path = path.replaceAll("  ", " "); path = "M" +
	 * xS.floatValue() + "," + yS.floatValue() + path + " L" + xE.floatValue() +
	 * "," + yE.floatValue(); } attributeMap.get("d").setText(path);
	 * nodeReloaded = true; }
	 */
	public synchronized org.w3c.dom.Node getElement() {

		return sourceElement;
		/*
		 * org.w3c.dom.Element element; String type = elementType.getText();
		 * //nodeReloaded = false;
		 * 
		 * if (type.equals(SVGConstant.TYPE_11)) { element =
		 * editor.manager.getSvgDocument().createElementNS(gmlNS,
		 * elementType.getText()); } else element =
		 * editor.manager.getSvgDocument().createElementNS(svgNS,
		 * elementType.getText()); ArrayList<String> typeList =
		 * SVGConstant.getAttributeList(elementType.getText()); try { for
		 * (Iterator<String> it = typeList.iterator(); it.hasNext(); ) { String
		 * t = it.next(); if (attributeMap.get(t).getText().equals(""))
		 * continue; if (t.equals(SVGConstant.A_TYPE_0)) { File f = new
		 * File(attributeMap.get(t).getText()); if (f.exists())
		 * element.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href",
		 * (new File(attributeMap.get(t).getText())).toURI().toString()); else
		 * element.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href",
		 * (attributeMap.get(t).getText())); // } else if
		 * (t.equals(SVGConstant.A_TYPE_28)) { //
		 * element.setAttributeNS("http://www.w3.org/1999/xlink", //
		 * SVGConstant.A_TYPE_0, // attributeMap.get(t).getText()); } else if
		 * (t.equals("text") && type.equals("g")) { try { String s =
		 * "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
		 * attributeMap.get(t).getText() + "</svg>"; org.w3c.dom.Document d =
		 * editor.manager.getFactory().createDocument(null, new
		 * StringReader(s));
		 * element.appendChild(editor.manager.getSvgDocument().
		 * importNode(d.getDocumentElement().getFirstChild(), true)); } catch
		 * (Exception ex) { ex.printStackTrace(); }
		 * 
		 * } else if (t.equals("text") || t.equals("desc") || t.equals("title"))
		 * { element.appendChild(editor.manager.getSvgDocument().createTextNode(
		 * attributeMap.get(t).getText())); } else element.setAttributeNS(null,
		 * t, attributeMap.get(t).getText()); } } catch (Exception e) {
		 * e.printStackTrace(); } element.setAttributeNS(null, "id", name);
		 * nodeReloaded = true; return element;
		 */}

	/*
	 * public synchronized void getElement(org.w3c.dom.Element element) {
	 * 
	 * nodeReloaded = false;
	 * 
	 * ArrayList<String> typeList =
	 * SVGConstant.getAttributeList(elementType.getText()); try { for
	 * (Iterator<String> it = typeList.iterator(); it.hasNext(); ) { String t =
	 * it.next(); if (t.equals(SVGConstant.A_TYPE_0)) { File f = new
	 * File(attributeMap.get(t).getText()); if (f.exists())
	 * element.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", (new
	 * File(attributeMap.get(t).getText())).toURI().toString()); else
	 * element.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href",
	 * (attributeMap.get(t).getText())); // } else if
	 * (t.equals(SVGConstant.A_TYPE_28)) { //
	 * element.setAttributeNS("http://www.w3.org/1999/xlink", //
	 * SVGConstant.A_TYPE_0, // attributeMap.get(t).getText()); } else if
	 * (t.equals("text") && element.getNodeName().equals("g")) { try { String s
	 * = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
	 * attributeMap.get(t).getText() + "</svg>"; org.w3c.dom.Document d =
	 * editor.manager.getFactory().createDocument(null, new StringReader(s));
	 * element
	 * .appendChild(editor.manager.getSvgDocument().importNode(d.getDocumentElement
	 * ().getFirstChild(), true)); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * } else if (t.equals("text") || t.equals("desc") || t.equals("title")) {
	 * element
	 * .appendChild(editor.manager.getSvgDocument().createTextNode(attributeMap
	 * .get(t).getText())); } else element.setAttributeNS(null, t,
	 * attributeMap.get(t).getText()); } } catch (Exception e) {
	 * e.printStackTrace(); } element.setAttributeNS(null, "id", name);
	 * nodeReloaded = true; }
	 */

	// public void

	public void setElement(org.w3c.dom.Element en) {
		setSourceElement(en);

		NamedNodeMap amap = sourceElement.getAttributes();
		String type = sourceElement.getNodeName();
		elementType.setText(type);
		this.prepareAttributesMap(type);
		int size = amap.getLength();

		for (int i = 0; i < size; i++) {
			Node n = amap.item(i);
			String name = n.getNodeName();

			AttributeInterface ai = SVGConstant.getField(name);
			if (!attributesMap.keySet().contains(name) || ai == null)
				continue;
			ai.getComponent().setName(name);

			SVGAttribute attribute = new SVGAttribute();
			attribute.setName(name);
			attribute.setSourcePropertyData(n);
			attribute.setField(ai);
			attribute.setModeler(modeler);
			attribute.getField().setPreferredSize(textFieldDimension);
			attributesMap.put(name, attribute);
			if (name.equals("id")) {
				this.name = n.getNodeValue();
				attribute.getField().addModelerActionListener(this);
			}
		}

		if (type.equals("text") || type.equals("desc") || type.equals("title")) {
			int childIndex = sourceElement.getChildNodes().getLength();
			for (int ii = 0; ii < childIndex; ii++) {
				org.w3c.dom.Node child_a = sourceElement.getChildNodes().item(
						ii);
				String name_a = child_a.getNodeName();
				if (name_a.equals("#text") || name_a.equals("#desc")
						|| name_a.equals("#title")) {
					AttributeInterface ai = SVGConstant.getField("text");

					ai.getComponent().setName("text");

					SVGAttribute attribute = new SVGAttribute();
					attribute.setName("text");
					attribute.setSourcePropertyData(sourceElement);
					attribute.setTextAttribute(true);
					attribute.setField(ai);
					attribute.setModeler(modeler);
					attribute.getField().setPreferredSize(textFieldDimension);
					attributesMap.put("text", attribute);
				}
			}
		}
	}

	public void newElement(String type) {
		if (!type.equals(SVGConstant.TYPE_1))
			setSourceElement(editor.manager.getSvgDocument()
					.createElement(type));
		else
			setSourceElement(editor.manager.getSvgDocument().createElementNS(
					"http://www.w3.org/1999/xlink", type));
		sourceElement.setAttributeNS(null, "id", name);
	}

	public void replaceElement(String type) {

		this.removeAll();
		if (sourceElement != null) {
			String previousType = sourceElement.getNodeName();
			if (previousType.equals(type))
				return;
		}

		org.w3c.dom.Element nE = null;
		if (!type.equals(SVGConstant.TYPE_1))
			nE = editor.manager.getSvgDocument().createElement(type);
		else
			nE = editor.manager.getSvgDocument().createElementNS(
					"http://www.w3.org/1999/xlink", type);
		nE.setAttributeNS(null, "id", name);
		org.w3c.dom.Element imp = (org.w3c.dom.Element) editor.manager
				.getSvgDocument().importNode(nE, true);
		editor.getElement().replaceChild(imp, sourceElement);
		setSourceElement(imp);
		// setSourceElement((org.w3c.dom.Element)
		// editor.manager.getSvgDocument().importNode(sourceElement, true));
		prepareAttributesMap(type);
		this.showField();
//		editor.manager.reloadNode();
	}

	public void prepareAttributesMap(String type) {
		List<String> a = SVGConstant.getAttributeList(type);
		HashMap<String, SVGAttribute> newAttributes = new HashMap<String, SVGAttribute>();

		for (Iterator<String> it = a.iterator(); it.hasNext();) {
			String n = it.next();
			newAttributes.put(n, attributesMap.get(n));
		}
		attributesMap.clear();
		attributesMap = newAttributes;
	}

	public void select(String type) {
		this.removeAll();
		if (sourceElement != null) {
			String previousType = sourceElement.getNodeName();
			if (previousType.equals(type))
				return;
		}
		prepareAttributesMap(type);
		newElement(type);
		this.showField();
	}

	public void showField() {
		this.removeAll();
		rotate = new SVGRotate(this);
		elementType.showField();

		this.add(elementTypeLabel);
		this.add(elementType);

		JSeparator s = new JSeparator(JSeparator.HORIZONTAL);
		s.setPreferredSize(new Dimension(540, 2));
		this.add(s);

		for (Iterator<String> it = attributesMap.keySet().iterator(); it
				.hasNext();) {
			String n = it.next();
			SVGAttribute t = attributesMap.get(n);
			if (t == null) {
				AttributeInterface ai = SVGConstant.getField(n);
				if (ai == null)
					continue;
				ai.getComponent().setName(n);
				SVGAttribute attribute = new SVGAttribute();
				attribute.setName(n);

				if (n.equals("text")) {
					// org.w3c.dom.Text t1 = (org.w3c.dom.Text)
					// editor.manager.getSvgDocument().importNode(editor.manager.getSvgDocument().createTextNode(""),false);
					// if(t1.getData()==null || t1.getData().equals(""))
					// t1.setData(ai.getText());
					// attribute.setTextNode(t1);
					// sourceElement.appendChild(t1);
					sourceElement.setTextContent("");
					// org.w3c.dom.Text t1 = (org.w3c.dom.Text)
					// sourceElement.getFirstChild();
					attribute.setSourcePropertyData(sourceElement);
					attribute.setTextAttribute(true);
					attribute.setField(ai);
					attribute.setModeler(modeler);
					attribute.getField().setPreferredSize(textFieldDimension);
					attributesMap.put(n, attribute);
					t = attribute;
				} else {
					org.w3c.dom.Attr t1 = sourceElement.getAttributeNodeNS(
							SVGConstant.getNS(n), n);
					if (t1 == null)
						t1 = editor.manager.getSvgDocument().createAttributeNS(
								SVGConstant.getNS(n), n);
					if (t1.getValue() == null || t1.getValue().equals(""))
						t1.setValue(ai.getText());
					attribute.setSourcePropertyData(t1);
					sourceElement.setAttributeNodeNS(t1);
					attribute.setField(ai);
					attribute.setModeler(modeler);
					attribute.getField().setPreferredSize(textFieldDimension);
					attributesMap.put(n, attribute);
					t = attribute;
				}
				sourceElement.setAttributeNode((org.w3c.dom.Attr) attribute
						.getSourcePropertyData());
			}

			t.getField().showField();
			JLabel label = t.getLabel();
			// label.setPreferredSize(labelDimension);
			this.add(label);
			JPanel field = (JPanel) t.getField();
			field.setPreferredSize(new Dimension(400, 25));
			this.add(field);
		}
		this.setPreferredSize(new Dimension(510,
				10 + 30 * (attributes.size() + 1)));
	}

	/*
	 * public void hideField() { rotate = null; elementType.hideField(); for
	 * (Iterator<SVGAttribute> it = attributes.iterator(); it .hasNext();) {
	 * SVGAttribute t = it.next(); t.getField().hideField(); } this.removeAll();
	 * }
	 */
	public void setIndex(int i) {
		index = new Integer(i);
	}

	public Integer getIndex() {
		return index;
	}

	/*
	 * public void moveElement(int x, int y) { // nodeReloaded = false; if
	 * (elementType.getText().equals(SVGConstant.TYPE_7) ||
	 * elementType.getText().equals(SVGConstant.TYPE_8) ||
	 * elementType.getText().equals(SVGConstant.TYPE_9)) { String transform =
	 * attributeMap.get(SVGConstant.A_TYPE_23).getText(); int translateIndex =
	 * transform.indexOf("translate("); if (translateIndex == -1) {
	 * attributeMap.
	 * get(SVGConstant.A_TYPE_23).setText(attributeMap.get(SVGConstant
	 * .A_TYPE_23).getText() + ",translate(" + (-x) + "," + (-y) + ")"); } else
	 * { String translateBefore = transform.substring(0, translateIndex + 10);
	 * String translate = transform.substring(translateIndex + 10); int pr =
	 * translate.indexOf(","); String value1 = translate.substring(0, pr);
	 * translate = translate.substring(pr + 1);
	 * 
	 * pr = translate.indexOf(")"); String value2 = translate.substring(0, pr);
	 * translate = translate.substring(pr);
	 * 
	 * String result = translateBefore + (new BigDecimal(value1).intValue() - x)
	 * + "," + (new BigDecimal(value2).intValue() - y) + translate;
	 * attributeMap.get(SVGConstant.A_TYPE_23).setText(result); } } else {
	 * 
	 * if (attributeMap.get(SVGConstant.A_TYPE_2) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_2).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_2).getText()).intValue() -
	 * x)); if (attributeMap.get(SVGConstant.A_TYPE_3) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_3).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_3).getText()).intValue() -
	 * y)); if (attributeMap.get(SVGConstant.A_TYPE_4) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_4).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_4).getText()).intValue() -
	 * x)); if (attributeMap.get(SVGConstant.A_TYPE_5) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_5).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_5).getText()).intValue() -
	 * y)); if (attributeMap.get(SVGConstant.A_TYPE_13) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_13).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_13).getText()).intValue()
	 * - x)); if (attributeMap.get(SVGConstant.A_TYPE_14) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_14).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_14).getText()).intValue()
	 * - y)); if (attributeMap.get(SVGConstant.A_TYPE_15) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_15).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_15).getText()).intValue()
	 * - x)); if (attributeMap.get(SVGConstant.A_TYPE_16) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_16).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_16).getText()).intValue()
	 * - y)); } updateRotate(getXcenter(), getYcenter());
	 * editor.reloadNodeWithType(this); // nodeReloaded = true; }
	 * 
	 * public void scaleElement(int x, int y) { // nodeReloaded = false;
	 * 
	 * if (elementType.getText().equals(SVGConstant.TYPE_7) ||
	 * elementType.getText().equals(SVGConstant.TYPE_8) ||
	 * elementType.getText().equals(SVGConstant.TYPE_9) //||
	 * //elementType.getText().equals(SVGConstant.TYPE_10) ) { String transform
	 * = attributeMap.get(SVGConstant.A_TYPE_23).getText(); int translateIndex =
	 * transform.indexOf("scale("); if (translateIndex == -1) {
	 * attributeMap.get(
	 * SVGConstant.A_TYPE_23).setText(attributeMap.get(SVGConstant
	 * .A_TYPE_23).getText() + ",scale(" + (1) + "," + (1) + ")"); } else {
	 * String translateBefore = transform.substring(0, translateIndex + 6);
	 * String translate = transform.substring(translateIndex + 6); int pr =
	 * translate.indexOf(","); int tmp = translate.indexOf(")");
	 * 
	 * String value1 = "1"; String value2 = "1";
	 * 
	 * if (pr == -1 || tmp < pr) {
	 * 
	 * value1 = translate.substring(0, tmp); translate =
	 * translate.substring(tmp);
	 * 
	 * } else {
	 * 
	 * value1 = translate.substring(0, pr); translate = translate.substring(pr +
	 * 1);
	 * 
	 * pr = translate.indexOf(")"); value2 = translate.substring(0, pr);
	 * translate = translate.substring(pr);
	 * 
	 * } BigDecimal b1 = new BigDecimal(new BigDecimal(value1).doubleValue() -
	 * (double)x / 10); BigDecimal b2 = new BigDecimal(new
	 * BigDecimal(value2).doubleValue() - (double)y / 10);
	 * 
	 * String result = translateBefore + b1.doubleValue() + "," +
	 * b2.doubleValue() + translate;
	 * attributeMap.get(SVGConstant.A_TYPE_23).setText(result); } } else {
	 * 
	 * if (attributeMap.get(SVGConstant.A_TYPE_8) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_8).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_8).getText()).intValue() -
	 * x)); if (attributeMap.get(SVGConstant.A_TYPE_9) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_9).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_9).getText()).intValue() -
	 * y)); if (attributeMap.get(SVGConstant.A_TYPE_1) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_1).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_1).getText()).intValue() -
	 * (y))); if (attributeMap.get(SVGConstant.A_TYPE_19) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_19).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_19).getText()).intValue()
	 * - x)); if (attributeMap.get(SVGConstant.A_TYPE_15) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_15).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_15).getText()).intValue()
	 * - x)); if (attributeMap.get(SVGConstant.A_TYPE_16) != null)
	 * attributeMap.get(SVGConstant.A_TYPE_16).setText("" + (new
	 * BigDecimal(attributeMap.get(SVGConstant.A_TYPE_16).getText()).intValue()
	 * - y)); } updateRotate(getXcenter(), getYcenter()); //nodeReloaded = true;
	 * editor.reloadNodeWithType(this); // if
	 * (elementType.getText().equals(SVGConstant.TYPE_10)) { //
	 * editor.reloadPaths(index); // } //editor.reloadNode(index);
	 * //nodeReloaded = true; }
	 */
	/*
	 * public void updateRotate(double x, double y) { if
	 * (attributeMap.get(SVGConstant.A_TYPE_23) == null) return; String
	 * transform = attributeMap.get(SVGConstant.A_TYPE_23).getText(); int
	 * translateIndex = transform.indexOf("rotate("); if (translateIndex == -1)
	 * { return; } else { String translateBefore = transform.substring(0,
	 * translateIndex + 7); String translate =
	 * transform.substring(translateIndex + 7); int pr = translate.indexOf(",");
	 * String angle = translate.substring(0, pr); pr = translate.indexOf(")");
	 * String translateAfter = translate.substring(pr); String result =
	 * translateBefore + angle + "," + x + "," + y + translateAfter;
	 * attributeMap.get(SVGConstant.A_TYPE_23).setText(result); } }
	 * 
	 * public void setRotate(double theta, double x, double y) { //nodeReloaded
	 * = false; if (attributeMap.get(SVGConstant.A_TYPE_23) == null) return; //
	 * nodeReloaded = true; String transform =
	 * attributeMap.get(SVGConstant.A_TYPE_23).getText(); int translateIndex =
	 * transform.indexOf("rotate("); if (translateIndex == -1) { if
	 * (attributeMap.get(SVGConstant.A_TYPE_23) != null)
	 * attributeMap.get(SVGConstant
	 * .A_TYPE_23).setText(attributeMap.get(SVGConstant.A_TYPE_23).getText() +
	 * ",rotate(" + theta + "," + x + "," + y + ")"); } else { String
	 * translateBefore = transform.substring(0, translateIndex + 7); String
	 * translate = transform.substring(translateIndex + 7); int pr =
	 * translate.indexOf(")"); String translateAfter = translate.substring(pr);
	 * BigDecimal b1 = new BigDecimal(theta); String result = translateBefore +
	 * b1.doubleValue() + "," + x + "," + y + translateAfter;
	 * attributeMap.get(SVGConstant.A_TYPE_23).setText(result); } //nodeReloaded
	 * = true; editor.reloadNodeWithType(this);
	 * 
	 * // if (elementType.getText().equals(SVGConstant.TYPE_10)) { //
	 * editor.reloadPaths(index); // } //editor.reloadNode(index);
	 * //nodeReloaded = true; }
	 */
	/*
	 * public Geometry geAsGeometry() { GeometryFactory factory = new
	 * GeometryFactory(); WKTReader reader = new WKTReader(factory);
	 * 
	 * String points = (attributeMap.get(SVGConstant.A_TYPE_12) != null) ?
	 * (attributeMap.get(SVGConstant.A_TYPE_12)).getText() : ""; points =
	 * points.trim(); if (points.equals("")) { points =
	 * (attributeMap.get(SVGConstant.A_TYPE_26) != null) ?
	 * (attributeMap.get(SVGConstant.A_TYPE_26)).getText() : ""; points =
	 * points.trim(); points = points.replaceAll("M", "").replaceAll("L",
	 * "").replaceAll("m", "").replaceAll("l", "").replaceAll(",",
	 * "a").replaceAll(" ", ",").replaceAll("a", " "); } if (!points.equals(""))
	 * { try { if (!points.contains(",")) { Point g =
	 * (Point)reader.read("POINT (" + points + ")"); return g;
	 * 
	 * } else {
	 * 
	 * 
	 * LineString g = (LineString)reader.read("LINESTRING (" + points + ")"); if
	 * (g.isClosed()) return reader.read("POLYGON ((" + points + "))"); else
	 * return g; } } catch (Exception e) { System.out.println("LINESTRING (" +
	 * points + ")"); e.printStackTrace(); } }
	 * 
	 * double width = new Double((attributeMap.get(SVGConstant.A_TYPE_8) !=
	 * null) ? attributeMap.get(SVGConstant.A_TYPE_8).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_1) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_1).getText() : "0")); double x = new
	 * Double((attributeMap.get(SVGConstant.A_TYPE_2) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_2).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_4) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_4).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_13) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_13).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_21) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_21).getText() : "0"))));
	 * 
	 * double height = new Double((attributeMap.get(SVGConstant.A_TYPE_9) !=
	 * null) ? attributeMap.get(SVGConstant.A_TYPE_9).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_1) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_1).getText() : "0")); double y = new
	 * Double((attributeMap.get(SVGConstant.A_TYPE_3) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_3).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_5) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_5).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_14) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_14).getText() :
	 * ((attributeMap.get(SVGConstant.A_TYPE_22) != null) ?
	 * attributeMap.get(SVGConstant.A_TYPE_22).getText() : "0"))));
	 * 
	 * if (width > 0 || height > 0) { points = x + " " + y + "," + (x + width) +
	 * " " + y + "," + (x + width) + " " + (y + height) + "," + (x) + " " + (y +
	 * height) + "," + x + " " + y;
	 * 
	 * try { return reader.read("POLYGON ((" + points + "))"); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 * 
	 * return null; }
	 */
	@Override
	public void modelerAction(FieldUpdateAction action) {
		if (action.getField() == elementType) {
			// this.hideField();
			replaceElement(elementType.getText());
			// select(elementType.getText());
			// this.showField();
		} else if (action.getField().getComponent().getName().equals("id")) {
			name = action.getField().getText();
			editor.elementPane.setTitleAt(index, name);
		}
		this.repaint();
	}

}
