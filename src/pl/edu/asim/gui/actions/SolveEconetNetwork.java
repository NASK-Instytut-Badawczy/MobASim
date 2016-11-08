package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import pl.edu.asim.interfaces.ASimSimulatorInterface;

public class SolveEconetNetwork extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130111333562399056L;
	ServiceReference<ASimSimulatorInterface> ref;
	ASimSimulatorInterface manager;
	ImageIcon icon;
	String taskType;

	public SolveEconetNetwork(String name) {
		super(name);
	}

	public SolveEconetNetwork(String name, ImageIcon icon) {
		super(name, icon);
	}

	public SolveEconetNetwork(String name, ImageIcon icon, String taskType) {
		super(name, icon);
		this.taskType = taskType;
	}

	@Override
	public int operation(ActionEvent e) {

		try {

			ref = this
					.getModeler()
					.getBundleContext()
					.getServiceReference(ASimSimulatorInterface.class);
			manager = null;
			try {
				Bundle b = ref.getBundle();
				b.update();
			} catch (BundleException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ref = null;
			while (ref == null) {
				ref = this
						.getModeler()
						.getBundleContext()
						.getServiceReference(
								ASimSimulatorInterface.class);
			}
			ASimSimulatorInterface asimSimulator = null;

			while (asimSimulator == null) {
				asimSimulator = (ASimSimulatorInterface) this.getModeler()
						.getBundleContext().getService(ref);
			}
			// asimSimulator.setResultsDirectory(modeler.getWorkspace());
			asimSimulator.optimization(getModeler().getSelectedNode()
					.getSourceElementData(), modeler.getWorkspace(), taskType);
			return 0;

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return 0;
	}

}
