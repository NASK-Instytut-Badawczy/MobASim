package pl.edu.asim.sim;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import pl.edu.asim.interfaces.ASimSimulatorInterface;
import pl.edu.asim.interfaces.SimulatorService;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.proc.TLTree;
import pl.edu.asim.service.mobility.Entity;
import pl.edu.econet.BB_step;
import pl.edu.econet.Econet;
import pl.edu.econet.Network;
import pl.edu.econet.Relaxation;
import pl.edu.econet.topology.Link;

public class ASimSimulatorManager implements ASimSimulatorInterface {

	Bundle bundle;
	ApplicationContext applicationContext;
	ASimSVGManager svgManager;
	Logger logger;

	// GeometryFactory geometryFactory;

	private org.springframework.oxm.castor.CastorMarshaller marshaller;
	private org.springframework.oxm.castor.CastorMarshaller unmarshaller;

	Map<String, SimulatorService> services;

	// public GeometryFactory getGeometryFactory() {
	// return geometryFactory;
	// }
	//
	// public void setGeometryFactory(GeometryFactory geometryFactory) {
	// this.geometryFactory = geometryFactory;
	// }

	// public JTSUtils getJts() {
	// return jts;
	// }
	//
	// public void setJts(JTSUtils jts) {
	// this.jts = jts;
	// }
	//
	// JTSUtils jts;

	public ASimSimulatorManager() {

		try {

			logger = org.apache.log4j.Logger.getLogger("pl.edu.asim");

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("pl.edu.asim.service: Start");

		// geometryFactory = new GeometryFactory();
		// jts = new JTSUtils();

	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public void start() {
		try {
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
	}

	@Override
	public void test() {
		System.out.println("Service test");
	}

	@Override
	public void simulation(ASimDO simulator, String resultsDirectory) {

		SimulatorService service = services.get(simulator.getType());
		if (service != null) {
			svgManager = new ASimSVGManager(this, resultsDirectory
					+ "/svg_model/" + simulator.getName() + ".svg", simulator);
			service.setManager(this);
			service.simulation(simulator, resultsDirectory);

		} else
			System.out.println("Service error: service for "
					+ simulator.getType() + " not found");
	}

	public ASimSVGManager getSvgManager() {
		return svgManager;
	}

	public void setSvgManager(ASimSVGManager svgManager) {
		this.svgManager = svgManager;
	}

	public void results(double k, List<Entity> entities, double range,
			double energy) {
		int linkCount = 0;
		double linkwidth = 0;
		double nCounts = 0;

		for (Entity e1 : entities) {
			int nCount = 0;
			Vector3D v1 = e1.getOrientationPoint().getAsVector3D()
					.add(e1.getSvgZeroPoint().getAsVector3D());
			for (Entity e2 : entities) {
				Vector3D v2 = e2.getOrientationPoint().getAsVector3D()
						.add(e2.getSvgZeroPoint().getAsVector3D());
				double distance = v1.distance(v2);
				if (distance <= range && distance > 0) {
					linkCount++;
					nCount++;
					linkwidth = linkwidth + distance;
				}
			}
			nCounts = nCounts + nCount;
		}
		if (linkCount == 0)
			return;
		double e = new BigDecimal(energy).setScale(2,
				BigDecimal.ROUND_HALF_DOWN).doubleValue();
		double averageLL = new BigDecimal((linkwidth / linkCount) / 10)
				.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		double averageND = new BigDecimal(nCounts / entities.size()).setScale(
				2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		// logger.info(k + " & " + averageND + " & " + averageLL + " & " + e
		// + " \\\\");
	}

	public void results(double k, List<Entity> entities, double range,
			double energy, double percent, double percent2, int izCount) {
		int linkCount = 0;
		double linkwidth = 0;
		double nCounts = 0;
		double pathLenght = 0;

		for (Entity e1 : entities) {
			int nCount = 0;
			Vector3D v1 = e1.getOrientationPoint().getAsVector3D()
					.add(e1.getSvgZeroPoint().getAsVector3D());
			for (Entity e2 : entities) {
				Vector3D v2 = e2.getOrientationPoint().getAsVector3D()
						.add(e2.getSvgZeroPoint().getAsVector3D());
				double distance = v1.distance(v2);
				if (distance <= range && distance > 0) {
					linkCount++;
					nCount++;
					linkwidth = linkwidth + distance;
				}
			}
			nCounts = nCounts + nCount;
			pathLenght = pathLenght + e1.getPathLenght();
		}
		if (linkCount == 0)
			return;

		pathLenght = new BigDecimal(pathLenght).setScale(2,
				BigDecimal.ROUND_HALF_DOWN).doubleValue();
		double e = new BigDecimal(energy).setScale(2,
				BigDecimal.ROUND_HALF_DOWN).doubleValue();
		double averageLL = new BigDecimal((linkwidth / linkCount) / 10)
				.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		double averageND = new BigDecimal(nCounts / entities.size()).setScale(
				2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		logger.info(" \t " + k + " \t " + averageND + " \t " + averageLL
				+ " \t " + percent + " \t " + izCount + " \t " + percent2
				+ " \t " + pathLenght);
	}

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = (org.springframework.oxm.castor.CastorMarshaller) marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = (org.springframework.oxm.castor.CastorMarshaller) unmarshaller;
	}

	// @Override
	// public void convertFromObjectToXML(Object object, String filepath)
	// throws IOException {
	//
	// FileOutputStream os = null;
	// try {
	// os = new FileOutputStream(filepath);
	// getMarshaller().marshal(object, new StreamResult(os));
	// os.flush();
	// os.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// @Override
	// public Object convertFromXMLToObject(String xmlfile) throws IOException {
	//
	// FileInputStream is = null;
	// try {
	// is = new FileInputStream(xmlfile);
	// return getUnmarshaller().unmarshal(new StreamSource(is));
	// } finally {
	// if (is != null) {
	// is.close();
	// }
	// }
	// }

	@Override
	public void econetTask(String bean, String taskType) {

		while (this.getApplicationContext() == null) {
			try {
				Thread t = new Thread();
				t.start();
				t.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Network n = (Network) this.getApplicationContext().getBean(bean);

		if (taskType.equals("-r")) {
			new Relaxation(n, bean);
		} else if (taskType.equals("-f")) {
			new Econet(n, bean);
		} else if (taskType.equals("-fs")) {
			new BB_step(n, bean);
		}
	}

	public static ApplicationContext getLocalFileApplicationContext() {
		return new FileSystemXmlApplicationContext(
				new String[] { "econet/*.xml" });
	}

	@Override
	public void optimization(ASimDO n, String resultsDirecory, String taskType) {
		Network network = new Network();
		network.setData(n);
		for (Link l : network.getLinks()) {
			if (l.getReturnLinkName() != null) {
				for (Link l2 : network.getLinks()) {
					if (l.getReturnLinkName().equals(l2.getId())) {
						l.setReturnLink(l2);
					}
				}
			}
		}
		if (taskType.equals("-r")) {
			new Relaxation(network, n.getName());
		} else if (taskType.equals("-f")) {
			new Econet(network, n.getName());
		} else if (taskType.equals("-fs")) {
			new BB_step(network, n.getName());
		}
	}

	public Map<String, SimulatorService> getServices() {
		return services;
	}

	public void setServices(Map<String, SimulatorService> services) {
		this.services = services;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public TLTree getTree() {
		ServiceReference<TLTree> ref = getBundle().getBundleContext()
				.getServiceReference(TLTree.class);
		TLTree tree = getBundle().getBundleContext().getService(ref);
		return tree;
	}

}
