package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;

public class DirectoryField extends JPanel implements ActionListener,
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
	ArrayList<ModelerActionListener> modelerActionListenerList;

	public DirectoryField() {
		super(new BorderLayout());
		valueField = new JTextField("");
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
	}

	public void setChangeListener(ChangeListener cListener) {
		this.cListener = cListener;
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
			Dialog dialog = new Dialog(value);
			dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = dialog.showOpenDialog(DirectoryField.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = dialog.getSelectedFile();
				valueField.setText(file.getPath());
				if (cListener != null)
					cListener.stateChanged(null);
			} else {
				;
			}
		}
	}

	@Override
	public String getText() {
		return valueField.getText();
	}

	@Override
	public void setText(String s) {
		if (s != null) {
			File f = new File(s);
			value = f.getPath();
		}
		firstValue = value;
		valueField.setText(value);

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

	class Dialog extends JFileChooser {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Dialog(String v) {
			super(v);
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
