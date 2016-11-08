package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;

public class ColorField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	String firstValue = "";
	JTextField valueField;

	JButton undoButton;
	JButton openButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ChangeListener cListener;
	Color color;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	public ColorField() {
		super(new BorderLayout());
		valueField = new JTextField("none");
		valueField.setEditable(false);
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
	}

	@Override
	public void showField() {
		openButton = (JButton) attribute.getModeler().getContext()
				.getBean("OPEN_button");
		openButton.addActionListener(this);
		undoButton = (JButton) attribute.getModeler().getContext()
				.getBean("UNDO_button");
		undoButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(openButton);
		buttonPanel.add(undoButton);

		this.removeAll();
		this.add(valueField, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}

	@Override
	public void hideField() {
		openButton = null;
		undoButton = null;
		buttonPanel = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == undoButton) {
			setText(firstValue);
		} else if (e.getSource() == openButton) {
			Color fillColor = JColorChooser.showDialog(this,
					"Choose Fill Color", valueField.getBackground());
			valueField.setBackground(fillColor);
			if (fillColor == null)
				valueField.setText("none");
			else {
				valueField.setText("rgb(" + fillColor.getRed() + ","
						+ fillColor.getGreen() + "," + fillColor.getBlue()
						+ ")");
			}
			ChangeEvent ce = new ChangeEvent(valueField);
			if (cListener != null)
				cListener.stateChanged(ce);
		}
	}

	@Override
	public String getText() {
		if (valueField.getText().equals("none"))
			return "none";
		return valueField.getText();
	}

	public Color getColorFromString(String rgb) {
		try {
			if (rgb == null || rgb.equals("") || rgb.equals("none"))
				return null;
			String s1 = rgb.substring(4);
			int index = s1.indexOf(",");
			String r = s1.substring(0, index);
			s1 = s1.substring(index + 1);
			index = s1.indexOf(",");
			String g = s1.substring(0, index);
			s1 = s1.substring(index + 1);
			String b = s1.substring(0, s1.length() - 1);

			Color c = new Color(new BigInteger(r).intValue(),
					new BigInteger(g).intValue(), new BigInteger(b).intValue());
			return c;
		} catch (Exception e) {
			;
		}
		return color;
	}

	@Override
	public void setText(String s) {
		if (s != null && !s.equals("")) {
			color = getColorFromString(s);
			if (color == null) {
				value = "none";
				valueField.setText("none");
			} else {
				value = s;
				valueField.setText(s);
				valueField.setBackground(getColorFromString(s));
			}
		}
	}

	@Override
	public Document getDocument() {
		return valueField.getDocument();
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

	public void setChangeListener(ChangeListener cListener) {
		this.cListener = cListener;
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
