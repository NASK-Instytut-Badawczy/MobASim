package pl.edu.asim.sim;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

public class ASimSimulatorActivator implements BundleActivator, ServiceListener {

	ASimSimulatorManager manager;
	BundleContext context;

	@Override
	public void start(BundleContext ctx) {
		context = ctx;
		ctx.addServiceListener(this);
		// ctx.registerService(ASimSimulatorInterface.class.getName(),
		// new ASimSimulatorManager(), null);
		// System.out.println("new manager " + manager);
	}

	@Override
	public void stop(BundleContext ctx) {
		manager = null;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED
				&& event.getServiceReference().getBundle().getLocation()
						.contains("asim.sim.jar")) {
			ServiceReference sr = event.getServiceReference();
			Object o = event.getServiceReference().getBundle()
					.getBundleContext().getService(sr);
			if (o instanceof ASimSimulatorManager) {
				manager = (ASimSimulatorManager) o;
				manager.setBundle(event.getServiceReference().getBundle());
			}
			if (o instanceof OsgiBundleXmlApplicationContext) {
				manager.setApplicationContext((OsgiBundleXmlApplicationContext) o);
				manager.start();
			}
		}
	}

}
