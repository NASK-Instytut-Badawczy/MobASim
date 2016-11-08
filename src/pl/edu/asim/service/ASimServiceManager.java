package pl.edu.asim.service;

import org.apache.log4j.Logger;

import pl.edu.asim.interfaces.ASimServiceInterface;

public class ASimServiceManager implements ASimServiceInterface {

	Logger logger;

	public ASimServiceManager() {
		try {

			logger = org.apache.log4j.Logger.getLogger("pl.edu.asim");

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("pl.edu.asim.service: Start");
	}

	@Override
	public void test() {
		System.out.println("Service test");
	}
}
