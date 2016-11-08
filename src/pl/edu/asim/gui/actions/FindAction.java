package pl.edu.asim.gui.actions;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import pl.edu.asim.gui.GuiNode;

public class FindAction extends ModelerAction {

	// String findLabelText = " Element ID = ";
	String findLabelText;

	public String getFindLabelText() {
		return findLabelText;
	}

	public void setFindLabelText(String findLabelText) {
		this.findLabelText = findLabelText;
	}

	JToolBar find;

	public JToolBar getFind() {
		return find;
	}

	public void setFind(JToolBar find) {
		this.find = find;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField findField;

	public JTextField getFindField() {
		return findField;
	}

	public void setFindField(JTextField findField) {
		this.findField = findField;
	}

	public FindAction(String name, String findLabelText) {
		super(name);
		this.setFindLabelText(findLabelText);

		find = new JToolBar();
		find.setName("Find");
		find.setFloatable(false);
		find.setRollover(true);

		String fontName = find.getFont().getName();
		Font f = new Font(fontName, Font.ITALIC, 11);

		JLabel findLabel = new JLabel(findLabelText);
		findLabel.setHorizontalAlignment(JLabel.RIGHT);
		find.add(findLabel);
		findLabel.setFont(f);

		findField = new JTextField("");
		find.add(findField);

		JButton findButton = new JButton(this);
		find.add(findButton);
		find.setOrientation(JToolBar.HORIZONTAL);
		findButton.setFont(f);
	}

	@Override
	public int operation(ActionEvent e) {
		getModeler().getFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String id = findField.getText();
		try {
			GuiNode n = getModeler().getNodeMap().get(
					new Integer(id).longValue());
			if (n != null) {
				getModeler().selectNode(n);
			}
		} catch (Exception ex) {
			;
		}
		getModeler().getFrame().setCursor(Cursor.getDefaultCursor());
		return getDec();
	}

}
