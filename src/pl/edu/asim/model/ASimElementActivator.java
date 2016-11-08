package pl.edu.asim.model;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ASimElementActivator implements BundleActivator{

	public void start(BundleContext ctx) {
		ASimElementManager.instance = new ASimElementManager();
//		ctx.registerService(ASimServiceInterface.class.getName(),
//			new ASimServiceManager(), null);
	}

	public void stop(BundleContext ctx) {
		ASimElementManager.instance.close();
		ASimElementManager.instance = null;
	}

}
