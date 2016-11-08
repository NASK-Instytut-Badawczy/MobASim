package pl.edu.asim.gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pl.edu.asim.gui.fields.AttributeInterface;
import pl.edu.asim.model.ASimPO;

public class Attribute implements DocumentListener {

	private String name;
	private String title = "";
	Dimension labelDimension = new Dimension(170, 25);

	private AttributeInterface field;
	private String fieldName;
	private String defaultValue = "";
	private ASimPO sourcePropertyData;

	private Modeler modeler;
	private GuiNode father;
	private String description;

	private boolean blocked = false;

	public GuiNode getFather() {
		return father;
	}

	public void setFather(GuiNode father) {
		this.father = father;
	}

	public ASimPO getSourcePropertyData() {
		return sourcePropertyData;
	}

	public void setSourcePropertyData(ASimPO sourcePropertyData) {
		this.sourcePropertyData = sourcePropertyData;
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

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
	}

	public Modeler getModeler() {
		return modeler;
	}

	public JPanel show() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		JLabel label = new JLabel(title);
		label.setPreferredSize(labelDimension);
		label.setBounds(0, 0, labelDimension.width, labelDimension.height);
		label.setToolTipText(name);
		panel.add(label);

		if (modeler.getContext().containsBean(this.fieldName)) {
			field = (AttributeInterface) modeler.getContext().getBean(
					this.fieldName);
			if (field != null) {
				field.setAttribute(this);
				field.getDocument().addDocumentListener(this);
				field.setText(sourcePropertyData.getValue());
				field.setBounds(labelDimension.width + 2, 0,
						field.getPreferredSize().width,
						field.getPreferredSize().height);
				field.getComponent().setEnabled(!blocked);
				field.setDescription(description);
				field.showField();
				panel.add((Component) field);
			}
		}
		panel.setPreferredSize(new Dimension(2 + labelDimension.width
				+ field.getPreferredSize().width,
				field.getPreferredSize().height));
		return panel;
	}

	public void hide() {
		field.hideField();
		field = null;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (field != null && sourcePropertyData != null)
			sourcePropertyData.setValue(field.getText());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (field != null && sourcePropertyData != null)
			sourcePropertyData.setValue(field.getText());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (field != null && sourcePropertyData != null)
			sourcePropertyData.setValue(field.getText());
	}

	public Dimension getLabelDimension() {
		return labelDimension;
	}

	public void setLabelDimension(Dimension labelDimension) {
		this.labelDimension = labelDimension;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
