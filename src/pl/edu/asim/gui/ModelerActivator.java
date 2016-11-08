package pl.edu.asim.gui;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import pl.edu.asim.interfaces.ASimModelerInterface;

public class ModelerActivator implements BundleActivator {
	private static BundleContext m_context = null;

	@Override
	public void start(BundleContext context) {
		try {
			Modeler.instance = new Modeler();
			Modeler.instance.setBundleContext(context);
			m_context = context;
			m_context.registerService(ASimModelerInterface.class.getName(),
					Modeler.instance, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext context) {

		if (Modeler.instance.call) {
			Modeler.instance.saveNode(Modeler.instance.getSelectedNode());
			Modeler.instance.getFrame().dispose();
		}
		Modeler.instance = null;
		m_context = null;
	}

	public static Bundle[] getBundles() {
		if (m_context != null) {
			return m_context.getBundles();
		} else {
			return m_context.getBundles();
		}
	}
}