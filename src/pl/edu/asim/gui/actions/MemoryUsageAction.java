package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import org.apache.batik.util.gui.MemoryMonitor;


public class MemoryUsageAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MemoryUsageAction(String name) {
		super(name);
	}

	public int operation(ActionEvent e) {
		MemoryMonitor mem = new MemoryMonitor();
        mem.setVisible(true);
        return getDec();
	}

}
