package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;


public class ModelerExitAction extends ModelerAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModelerExitAction(String name) {
		super(name);
	}

	public int operation(ActionEvent e) {
		getModeler().close();
		return getDec();
	}

}
