package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

public class AddElementAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AddElementAction(String name) {
		super(name);
	}

	public AddElementAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		getModeler().saveNode(getModeler().getSelectedNode());
		getModeler().addNewNode(getModeler().getSelectedNode(), type);
		return getDec();
	}

}
