package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import pl.edu.asim.gui.Modeler;

public class ListDialog extends JDialog implements ActionListener,
		DocumentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ListDialog dialog;
	private static String value = "";
	public static int valueIndex = -1;
	public static boolean set = false;
	private JList<Object> list;
	private JScrollPane listScroller;
	int max;

	private JTextField findField;
	Modeler modeler;

	public static String showDialog(Component frameComp,
			Component locationComp, String title, Object[] possibleValues,
			String initialValue, String longValue, Modeler modeler) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		set = false;
		dialog = new ListDialog(frame, locationComp, title, possibleValues,
				initialValue, longValue, modeler);
		dialog.setVisible(true);
		return value;
	}

	private void setValue(String newValue) {
		value = newValue;
		list.setSelectedValue(value, true);
		valueIndex = list.getSelectedIndex();
	}

	private ListDialog(Frame frame, Component locationComp, String title,
			Object[] data, String initialValue, String longValue,
			Modeler modeler) {
		super(frame, title, true);
		this.modeler = modeler;

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setIcon((ImageIcon) modeler.getContext().getBean(
				"CANCEL_Icon"));

		final JButton clearButton = new JButton("Clear");
		clearButton.setActionCommand("Clear");
		clearButton.addActionListener(this);
		clearButton.setIcon((ImageIcon) modeler.getContext().getBean(
				"CLEAR_Icon"));

		final JButton setButton = new JButton("Set");
		setButton.setActionCommand("Set");
		setButton.addActionListener(this);
		setButton.setIcon((ImageIcon) modeler.getContext().getBean("OK_Icon"));
		getRootPane().setDefaultButton(setButton);

		findField = new JTextField();
		findField.getDocument().addDocumentListener(this);

		list = new JList<Object>(data) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getScrollableUnitIncrement(Rectangle visibleRect,
					int orientation, int direction) {
				int row;
				if (orientation == SwingConstants.VERTICAL && direction < 0
						&& (row = getFirstVisibleIndex()) != -1) {
					Rectangle r = getCellBounds(row, row);
					if ((r.y == visibleRect.y) && (row != 0)) {
						Point loc = r.getLocation();
						loc.y--;
						int prevIndex = locationToIndex(loc);
						Rectangle prevR = getCellBounds(prevIndex, prevIndex);

						if (prevR == null || prevR.y >= r.y) {
							return 0;
						}
						return prevR.height;
					}
				}
				return super.getScrollableUnitIncrement(visibleRect,
						orientation, direction);
			}

			public String getToolTipText(MouseEvent e) {
				// String tip = null;
				java.awt.Point p = e.getPoint();
				int index = locationToIndex(p);
				// System.out.println(index);
				// if (index > this.getMaxSelectionIndex())
				// return "";
				// if (index < this.getMinSelectionIndex())
				// return "";
				// if (index >= this.getModel().getSize())
				// return "";
				if (this.getModel().getSize() == 0)
					return "";
				return "" + this.getModel().getElementAt(index);
			}

		};

		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		if (longValue != null) {
			list.setPrototypeCellValue(longValue);
		}
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(-1);
		list.setAutoscrolls(true);
		list.setCellRenderer(new MyCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					setButton.doClick();
				}
			}
		});
		list.setAutoscrolls(true);
		// list.
		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(550, 150));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);
		listScroller.setAutoscrolls(true);
		max = listScroller.getVerticalScrollBar().getMaximum();

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(findField);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(clearButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(setButton);

		Container contentPane = getContentPane();
		contentPane.add(listPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		setValue(initialValue);
		pack();
		setLocationRelativeTo(locationComp);
	}

	public void changedUpdate(DocumentEvent event) {
		lookup(event);
	}

	public void removeUpdate(DocumentEvent event) {
		lookup(event);
	}

	public void insertUpdate(DocumentEvent event) {
		lookup(event);
	}

	private void lookup(DocumentEvent event) {
		try {
			int lenght = event.getDocument().getLength();
			String s = event.getDocument().getText(0, lenght);
			int size = list.getModel().getSize();

			String tmp1 = "";
			int index = -1;
			for (int i = 0; i < size; i++) {
				String o = "" + list.getModel().getElementAt(i);
				if (startsWithIgnoreCase(o, s)) {
					if (tmp1.equals("")) {
						tmp1 = o;
						index = i;
					} else if (tmp1.length() > o.length()) {
						tmp1 = o;
						index = i;
					}
				}
			}
			if (index != -1)
				list.setSelectedIndex(index);
			listScroller.getVerticalScrollBar().setValue(
					(int) (max / size) * index);

		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	private boolean startsWithIgnoreCase(String str1, String str2) {
		return str1.toUpperCase().startsWith(str2.toUpperCase());
	}

	public void actionPerformed(ActionEvent e) {
		ListDialog.set = false;
		if ("Set".equals(e.getActionCommand())) {
			ListDialog.value = ((String) list.getSelectedValue());
			ListDialog.valueIndex = list.getSelectedIndex();
			ListDialog.set = true;
		} else if ("Clear".equals(e.getActionCommand())) {
			ListDialog.value = "";
			ListDialog.valueIndex = -1;
			ListDialog.set = true;
		}
		ListDialog.dialog.setVisible(false);
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value,
		// value to display
				int index, // cell index
				boolean isSelected, // is the cell selected
				boolean cellHasFocus) // the list and the cell have the focus
		{
			String s = value.toString();
			setText(s);
			// setIcon((s.length() > 10) ? longIcon : shortIcon);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			return this;
		}
	}

}
