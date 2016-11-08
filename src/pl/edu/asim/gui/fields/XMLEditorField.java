package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.text.Document;

import org.apache.batik.util.gui.xmleditor.XMLTextEditor;

import pl.edu.asim.gui.Attribute;

public class XMLEditorField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	String firstValue = "";
	XMLTextEditor valueField;

	JButton undoButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	public XMLEditorField() {
		super(new BorderLayout());
		valueField = new XMLTextEditor();
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
	}

	@Override
	public void showField() {
		undoButton = (JButton) attribute.getModeler().getContext()
				.getBean("UNDO_button");
		undoButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(undoButton);

		this.removeAll();
		this.add(new JScrollPane(valueField), BorderLayout.CENTER);
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
		return valueField.getText();
	}

	@Override
	public void setText(String s) {
		value = s;
		firstValue = s;
		valueField.setText(s);

		// ActionListener[] al = valueField.getActionListeners();
		// for (int i = 0; i < al.length; i++) {
		// ActionListener a = al[i];
		// ActionEvent ae = new ActionEvent(valueField, 0, "");
		// a.actionPerformed(ae);
		// }
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
		// valueField.addActionListener(l);
	}

	public void setActionCommand(String s) {
		// valueField.setActionCommand(s);
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
