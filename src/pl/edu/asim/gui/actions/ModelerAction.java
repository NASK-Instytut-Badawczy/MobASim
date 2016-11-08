package pl.edu.asim.gui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import pl.edu.asim.gui.Modeler;

public abstract class ModelerAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Modeler modeler;
	String question;
	protected int dec;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Modeler getModeler() {
		return modeler;
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getDec() {
		return dec;
	}

	public void setDec(int dec) {
		this.dec = dec;
	}

	public ModelerAction(String name) {
		super(name);
		setName(name);
	}

	public ModelerAction(String name, ImageIcon icon) {
		super(name, icon);
		setName(name);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		dec = (question != null && !question.equals("")) ? showConfirmDialog(question)
				: 0;

		if (dec == 2)
			return;

		if (dec == 1)
			return;

		modeler.getFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		operation(e);
		modeler.getFrame().setCursor(Cursor.getDefaultCursor());
	}

	public abstract int operation(ActionEvent e);

	protected int showConfirmDialog(String question) {
		if (question == null || question.equals(""))
			return 0;
		int n = JOptionPane.showConfirmDialog(modeler.getSelectedNode()
				.getPanel(), question, "", JOptionPane.YES_NO_CANCEL_OPTION);
		return n;
	}

}
