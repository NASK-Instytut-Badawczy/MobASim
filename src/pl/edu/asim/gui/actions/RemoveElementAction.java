package pl.edu.asim.gui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

public class RemoveElementAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RemoveElementAction(String name) {
		super(name);
	}

	public RemoveElementAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		getModeler().deleteNode(getModeler().getSelectedNode());
		return getDec();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String q = getQuestion() + " ["
				+ getModeler().getSelectedNode().getName() + "]";
		dec = showConfirmDialog(q);

		if (dec == 2)
			return;

		if (dec == 1)
			return;

		getModeler().getFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		operation(e);
		getModeler().getFrame().setCursor(Cursor.getDefaultCursor());
	}

}
