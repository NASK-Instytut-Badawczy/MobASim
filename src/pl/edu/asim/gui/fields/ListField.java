package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;
import pl.edu.asim.gui.actions.FieldUpdateAction;

public class ListField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	public String idValue = "";
	String firstValue = "";
	JTextField valueField;

	JButton undoButton;
	JButton openButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ChangeListener cListener;
	String extensions;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	String title = "";
	ActionEvent actionEvent;

	public ActionEvent getActionEvent() {
		return actionEvent;
	}

	public void setActionEvent(ActionEvent actionEvent) {
		this.actionEvent = actionEvent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> values;

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public void addRow(String key, String value) {
		this.values.put(key, value);
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public ListField() {
		super(new BorderLayout());
		valueField = new JTextField("");
		valueField.setEnabled(false);
		values = new HashMap<String, String>();
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
			openAction();
		}
	}

	@Override
	public String getText() {
		return idValue;
	}

	@Override
	public void setText(String id) {
		idValue = id;
		firstValue = id;
		if (values != null && values.get(id) != null)
			value = values.get(id);
		else
			value = id;
		valueField.setText(value);
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

	public void openAction() {

		Set<String> keys = values.keySet();

		String[] valuesT = new String[keys.size()];
		String[] keysT = new String[keys.size()];

		int i = 0;
		String longValue = " ";
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			String val = values.get(key);
			keysT[i] = key;
			valuesT[i] = val;
			if (longValue.length() < val.length())
				longValue = val;
			i++;
		}

		sort(keysT, valuesT);
		String selectedName = ListDialog.showDialog(this, openButton, title,
				valuesT, value, longValue, attribute.getModeler());

		if (selectedName != null) {

			if (ListDialog.valueIndex >= 0) {
				idValue = keysT[ListDialog.valueIndex];

				if (values != null && values.get(idValue) != null)
					value = values.get(idValue);
				else
					value = idValue;
			} else {
				idValue = "";
				value = "";
			}
			valueField.setText(value);

			// if (actionEvent != null) {
			// ActionListener[] al = valueField.getActionListeners();
			// for (int ii = 0; ii < al.length; ii++) {
			// ActionListener a = al[ii];
			// a.actionPerformed(new ActionEvent(valueField, 0, "R"));
			// }
			// }
			update();
		}
	}

	public void sort(String[] keys, String[] values) {

		ArrayList<String> newk = new ArrayList<String>();
		ArrayList<String> newv = new ArrayList<String>();

		for (int i = 0; i < values.length; i++) {
			int insertPoint = newv.size();
			String s = values[i];

			while ((insertPoint > 0)
					&& s.compareTo(newv.get(insertPoint - 1)) < 0) {
				insertPoint--;
			}
			newv.add(insertPoint, s);
			newk.add(insertPoint, keys[i]);
		}
		for (int i = 0; i < values.length; i++) {
			values[i] = newv.get(i);
			keys[i] = newk.get(i);
		}
	}

	@Override
	public void addModelerActionListener(ModelerActionListener listener) {
		modelerActionListenerList.add(listener);
	}

	public void update() {
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
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}
}
