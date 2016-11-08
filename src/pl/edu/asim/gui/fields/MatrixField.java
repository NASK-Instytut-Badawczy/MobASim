package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;
import pl.edu.asim.gui.actions.FieldUpdateAction;
import pl.edu.asim.util.Matrix;

public class MatrixField extends JPanel implements ActionListener,
		DocumentListener, AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4205803068029550367L;
	String type = "NULL";
	String typeName = "";
	String value = "";
	JTextArea valueField;
	String firstValue = "";

	JButton undoButton;
	JPanel buttonPanel;
	ArrayList<ModelerActionListener> modelerActionListenerList;

	JButton setButton;
	JButton showButton;
	JButton guiButton;
	JTextField xField;
	JTextField yField;
	JTextField xyValueField;
	JPanel matrixPanel;

	Matrix matrix;
	private Attribute attribute;
	private String description;

	public MatrixField() {
		super(new BorderLayout());
		valueField = new JTextArea("");
		valueField.getDocument().addDocumentListener(this);
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

		matrixPanel = new JPanel();
		matrixPanel.add(new JLabel("r="));

		Dimension pD = this.getPreferredSize();
		double width = pD.getWidth();
		xField = new JTextField("");
		xField.setPreferredSize(new Dimension(25, 25));
		matrixPanel.add(xField);
		matrixPanel.add(new JLabel("c="));
		yField = new JTextField("");
		yField.setPreferredSize(new Dimension(25, 25));
		matrixPanel.add(yField);
		matrixPanel.add(new JLabel("value="));
		xyValueField = new JTextField("");
		xyValueField.setPreferredSize(new Dimension((int) width - 280, 25));
		matrixPanel.add(xyValueField);
		setButton = new JButton(new SetAction());
		showButton = new JButton(new ShowAction());
		setButton.setPreferredSize(new Dimension(60, 25));
		showButton.setPreferredSize(new Dimension(60, 25));
		matrixPanel.add(setButton);
		matrixPanel.add(showButton);

		this.removeAll();
		if (description != null) {
			JLabel descL = new JLabel(description);
			descL.setForeground(Color.GRAY);
			descL.setToolTipText(description);
			this.add(descL, BorderLayout.PAGE_START);
		}
		this.add(new JScrollPane(valueField), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
		this.add(matrixPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void hideField() {
		undoButton = null;
		buttonPanel = null;
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
	public String getText() {
		return valueField.getText();
	}

	@Override
	public void setText(String s) {
		value = s;
		firstValue = s;
		try {
			if (value == null || value.equals("")) {
				matrix = new Matrix();
			} else {
				value = value.trim();
				matrix = new Matrix(value);
			}
			value = matrix.exportToCSV();
		} catch (Exception e) {
			value = s;
		}
		valueField.setText(s);
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

	public void initType(String type, String typeName) {
		this.type = type;
		this.typeName = typeName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == undoButton) {
			setText(firstValue);
		}
	}

	public String getValue() {
		return valueField.getText();
	}

	public void setTempText(String s) {
	}

	public void setMatrixValue(int x, int y, String value) {
		try {
			matrix.setAsString(value, x, y);
			valueField.setText(matrix.exportToCSV());
			this.value = getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ShowAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ShowAction() {
			super(" Get ");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				matrix.read(valueField.getText());
				int x = new BigDecimal(xField.getText()).intValue();
				int y = new BigDecimal(yField.getText()).intValue();
				String value = matrix.getAsString(x, y);
				xyValueField.setText(value);
			} catch (ArrayIndexOutOfBoundsException ex) {
				xyValueField.setText("");
			} catch (Exception e) {
				;
			}
		}
	}

	class SetAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SetAction() {
			super(" Set ");
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			int x = 0;
			int y = 0;
			try {
				matrix.read(valueField.getText());
				x = new BigInteger(xField.getText()).intValue();
				y = new BigInteger(yField.getText()).intValue();
				String value = xyValueField.getText();
				setMatrixValue(x, y, value);
			} catch (Exception e) {
				;
			}
		}
	}

	int tab;

	public void setTab(int i) {
		tab = i;
	}

	int rowIndex;

	public void setRowIndex(int i) {
		rowIndex = i;
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

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

}
