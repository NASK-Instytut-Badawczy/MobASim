package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;

public class IntegerField extends JPanel implements ActionListener,
		AttributeInterface, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	String firstValue = "";
	JSpinner valueField;
	JTextField duplicate;

	JButton undoButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	SpinnerNumberModel model;

	public IntegerField() {
		super(new BorderLayout());
		model = new SpinnerNumberModel();

		model.setValue(new Integer("0"));
		model.setStepSize(new Integer("1"));
		valueField = new JSpinner(model);
		duplicate = new JTextField(valueField.getValue() + "");
		valueField.addChangeListener(this);
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
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
			setText(firstValue);
		}
	}

	@Override
	public String getText() {
		return "" + valueField.getValue();
	}

	@Override
	public void setText(String s) {
		try {
			if (s == null || s.equals(""))
				return;
			if (model.getMinimum() != null
					&& ((Integer) model.getMinimum()).compareTo(new Integer(s)) == 1) {
				value = "" + model.getMinimum();
				valueField.setValue(new Integer(value));
				firstValue = value;
			} else if (model.getMaximum() != null
					&& ((Integer) model.getMaximum()).compareTo(new Integer(s)) == -1) {
				value = "" + model.getMaximum();
				valueField.setValue(new Integer(value));
				firstValue = value;
			} else {
				value = s;
				valueField.setValue(new Integer(value));
				firstValue = value;
			}
		} catch (Exception e) {
			;
		}
	}

	@Override
	public Document getDocument() {
		return duplicate.getDocument();
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
		duplicate.addActionListener(l);
	}

	public void setActionCommand(String s) {
		duplicate.setActionCommand(s);
	}

	public void setStepSize(BigDecimal b) {
		((SpinnerNumberModel) valueField.getModel()).setStepSize(b);
	}

	public void addChangeListener(ChangeListener cl) {
		valueField.addChangeListener(cl);
	}

	@Override
	public String getToolTipText() {
		return getText();
	}

	@Override
	public Component getComponent() {
		return valueField;
	}

	public void setMin(int min) {
		try {
			model.setMinimum(new Integer("" + min));
			valueField.setModel(model);
			valueField.commitEdit();
			valueField.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMax(int max) {
		try {
			model.setMaximum(new Integer("" + max));
			valueField.setModel(model);
			valueField.commitEdit();
			valueField.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean tick = true;

	@Override
	public void stateChanged(ChangeEvent e) {
		try {
			if (tick)
				duplicate.getDocument().insertString(0, "1", null);
			else
				duplicate.getDocument().remove(0, 1);
		} catch (Exception er) {
			er.printStackTrace();
		}
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
