package pl.edu.asim.gui.fields.svg;

import java.awt.Dimension;
import java.io.File;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.edu.asim.gui.Modeler;
import pl.edu.asim.gui.fields.AttributeInterface;

public class SVGAttribute implements DocumentListener {

	private String name;
	private String ns = "";
	private boolean textAttribute = false;
	private AttributeInterface field;
	private org.w3c.dom.Node sourcePropertyData;
	private Modeler modeler;

	public boolean isTextAttribute() {
		return textAttribute;
	}

	public void setTextAttribute(boolean textAttribute) {
		this.textAttribute = textAttribute;
	}

	public String getNs() {
		return ns;
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		update();
	}

	public void update() {
		if (this.textAttribute) {
			sourcePropertyData.setTextContent(field.getText());
		} else if (name.equals(SVGConstant.A_TYPE_0)) {
			File f = new File(field.getText());
			if (f.exists())
				sourcePropertyData.setNodeValue(f.toURI().toString());
			// sourcePropertyData.setAttributeNS("http://www.w3.org/1999/xlink",
			// "xlink:href",
			// f.toURI().toString());
			else
				sourcePropertyData.setNodeValue(field.getText());
			// sourcePropertyData.setAttributeNS("http://www.w3.org/1999/xlink",
			// "xlink:href",
			// field.getText());
		} else
			sourcePropertyData.setNodeValue(field.getText());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttributeInterface getField() {
		return field;
	}

	public void setField(AttributeInterface field) {
		this.field = field;
		if (sourcePropertyData != null) {
			if (this.textAttribute) {
				field.setText(sourcePropertyData.getTextContent());
			} else if (name.equals(SVGConstant.A_TYPE_0)) {
				try {
					URI u = new URI(this.sourcePropertyData.getNodeValue());
					field.setText(u.getPath());
				} catch (Exception ex) {
					;
				}
			} else {
				field.setText(sourcePropertyData.getNodeValue());
			}
		}
		this.field.getDocument().addDocumentListener(this);
		this.field.getComponent().setName(name);
	}

	public org.w3c.dom.Node getSourcePropertyData() {
		return sourcePropertyData;
	}

	public void setSourcePropertyData(org.w3c.dom.Node sourcePropertyData) {
		this.sourcePropertyData = sourcePropertyData;
		if (field != null) {
			if (this.textAttribute) {
				field.setText(sourcePropertyData.getTextContent());
			} else if (name.equals(SVGConstant.A_TYPE_0)) {
				try {
					URI u = new URI(this.sourcePropertyData.getNodeValue());
					field.setText(u.getPath());
				} catch (Exception ex) {
					;
				}
			} else {
				field.setText(sourcePropertyData.getNodeValue());
			}
		}
	}

	public Modeler getModeler() {
		return modeler;
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
		// this.field.setModeler(modeler);
	}

	public JLabel getLabel() {
		JLabel l = new JLabel(name, JLabel.RIGHT);
		l.setPreferredSize(new Dimension(80, 25));
		return l;
	}

}
