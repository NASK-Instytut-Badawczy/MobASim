package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;
import pl.edu.asim.gui.actions.FieldUpdateAction;

public class CheckboxField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
* 
*/
	private static final long serialVersionUID = 1L;
	boolean firstValue;
	String value = "true";
	JCheckBox valueField;
	JTextField valueField2;

	JButton undoButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	public CheckboxField() {

		super(new BorderLayout());
		valueField = new javax.swing.JCheckBox();
		valueField.setSelected(true);
		valueField2 = new JTextField("");
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
		valueField.addActionListener(this);
	}

	@Override
	public void showField() {
		undoButton = (JButton) attribute.getModeler().getContext()
				.getBean("UNDO_button");
		undoButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(undoButton);

		this.removeAll();
		this.add(valueField, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}

	@Override
	public void hideField() {
		undoButton = null;
		buttonPanel = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == undoButton) {
			setText("" + firstValue);
		} else {
			update();
		}
	}

	public void update() {
		value = "" + valueField.isSelected();
		// try {
		// valueField2.getDocument().remove(0, 1);
		// } catch (BadLocationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		valueField2.setText("" + valueField.isSelected());

		FieldUpdateAction action = new FieldUpdateAction();
		action.setActionCommand("update");
		action.setField(this);
		for (Iterator<ModelerActionListener> it = this.modelerActionListenerList
				.iterator(); it.hasNext();) {
			ModelerActionListener mal = it.next();
			mal.modelerAction(action);
		}
	}

	@Override
	public String getText() {
		return value;
	}

	@Override
	public void setText(String s) {
		value = s;
		firstValue = Boolean.getBoolean(s);
		valueField.setSelected(new Boolean(s));
		valueField2.setText(s);
	}

	@Override
	public Document getDocument() {
		return valueField2.getDocument();
	}

	@Override
	public void setBorder(Border b) {
		if (valueField != null)
			valueField.setBorder(b);
		else
			super.setBorder(b);
	}

	@Override
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public void addActionListener(ActionListener l) {
		valueField.addActionListener(l);
	}

	public void setActionCommand(String s) {
		valueField.setActionCommand(s);
	}

	@Override
	public String getToolTipText() {
		return getText();
	}

	@Override
	public Component getComponent() {
		return valueField;
	}

	@Override
	public void addModelerActionListener(ModelerActionListener listener) {
		modelerActionListenerList.add(listener);
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

}
