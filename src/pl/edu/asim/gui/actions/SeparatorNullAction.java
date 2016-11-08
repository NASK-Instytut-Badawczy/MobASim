package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

public class SeparatorNullAction extends ModelerAction{

	/**
	 * 
	 */
	
	String name = "separator";
	
	private static final long serialVersionUID = 1L;

	public SeparatorNullAction() {
		super("separator");
	}

	public int operation(ActionEvent e) {
        return getDec();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
	}

}
