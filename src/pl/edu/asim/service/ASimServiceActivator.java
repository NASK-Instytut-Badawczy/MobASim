package pl.edu.asim.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import pl.edu.asim.interfaces.ASimServiceInterface;

public class ASimServiceActivator implements BundleActivator{
	public void start(BundleContext ctx) {
		ctx.registerService(ASimServiceInterface.class.getName(),
			new ASimServiceManager(), null);
	}

	public void stop(BundleContext ctx) {
	}
}
