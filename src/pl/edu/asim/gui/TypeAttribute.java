package pl.edu.asim.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.edu.asim.gui.fields.AttributeInterface;

public class TypeAttribute implements DocumentListener {

	private String name;
	private String title = "";
	private int order;
	private boolean isRequired = false;
	private int tab = 0;
	Dimension labelDimension = new Dimension(170, 25);

	private AttributeInterface field;
	private String fieldName;
	private String defaultValue = "";

	private Attribute attribute;
	private GuiNode father;

	public GuiNode getFather() {
		return father;
	}

	public void setFather(GuiNode father) {
		this.father = father;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public AttributeInterface getField() {
		return field;
	}

	public void setField(AttributeInterface field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public JPanel show() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel label = new JLabel(title);
		label.setPreferredSize(labelDimension);
		label.setBounds(0, 0, labelDimension.width, labelDimension.height);
		label.setToolTipText(title);
		panel.add(label);

		field = (AttributeInterface) attribute.getModeler().getContext()
				.getBean(this.fieldName);
		if (field != null) {
			field.setAttribute(attribute);
			field.getDocument().addDocumentListener(this);
			field.setText("" + father.getSourceElementData().getType());
			field.setBounds(labelDimension.width + 2, 0,
					field.getPreferredSize().width,
					field.getPreferredSize().height);
			field.showField();
			field.getComponent().setEnabled(false);
			panel.add((Component) field);
		}
		panel.setPreferredSize(new Dimension(2 + labelDimension.width
				+ field.getPreferredSize().width, labelDimension.height));
		return panel;
	}

	public void hide() {
		field.hideField();
		field = null;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	public Dimension getLabelDimension() {
		return labelDimension;
	}

	public void setLabelDimension(Dimension labelDimension) {
		this.labelDimension = labelDimension;
	}

}
