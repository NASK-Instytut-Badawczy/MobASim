package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import pl.edu.asim.interfaces.ASimSimulatorInterface;
import pl.edu.asim.sim.ASimSimulatorManager;

public class RunSimulatorAction extends ModelerAction implements
		ServiceListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3110536757042855218L;
	ServiceReference<ASimSimulatorInterface> ref;
	ASimSimulatorInterface manager;

	public RunSimulatorAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {

		ref = this.getModeler().getBundleContext()
				.getServiceReference(ASimSimulatorInterface.class);
		// ref.getBundle().getBundleContext().removeServiceListener(this);
		// ref.getBundle().getBundleContext().addServiceListener(this);
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
			ref = this.getModeler().getBundleContext()
					.getServiceReference(ASimSimulatorInterface.class);
		}
		ASimSimulatorInterface asimSimulator = null;

		while (asimSimulator == null) {
			asimSimulator = this.getModeler().getBundleContext()
					.getService(ref);
		}
		// asimSimulator.setResultsDirectory(modeler.getWorkspace());

		asimSimulator.simulation(this.getModeler().getSelectedNode()
				.getSourceElementData(), this.getModeler().getWorkspace());

		return 0;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		System.out.println("service changed " + event.getType() + " "
				+ event.getServiceReference());
		if (event.getType() == ServiceEvent.REGISTERED
				&& event.getServiceReference().getBundle().getLocation()
						.contains("asim.sim.jar")) {
			@SuppressWarnings("unchecked")
			ServiceReference<Object> sr = (ServiceReference<Object>) event
					.getServiceReference();
			Object o = event.getServiceReference().getBundle()
					.getBundleContext().getService(sr);
			System.out.println(o);
			if (o instanceof ASimSimulatorManager) {
				manager = (ASimSimulatorManager) o;
			}
			// if (o instanceof OsgiBundleXmlApplicationContext) {
			// manager.setApplicationContext((OsgiBundleXmlApplicationContext)
			// o);
			// manager.start();
			// System.out.println("Simulator menager started.");
			// ASimPlatform.commandLine();
			// }
		}
		// ASimSimulatorInterface asimSimulator = (ASimSimulatorInterface) this
		// .getModeler().getBundleContext().getService(ref);
		if (manager != null) {
			System.out.println("symulator start");
			// manager.setResultsDirectory(modeler.getWorkspace());

			manager.simulation(this.getModeler().getSelectedNode()
					.getSourceElementData(), this.getModeler().getWorkspace());
		}
	}
}