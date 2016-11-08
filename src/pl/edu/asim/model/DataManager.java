package pl.edu.asim.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;

import org.apache.log4j.Logger;

public class DataManager {

	private static final String PERSISTENCE_UNIT_NAME = "eclipseLink";
	static DataManager instance;
	private static EntityManagerFactory factory;
	private Logger logger;
	private JAXBContext jaxbContext;

	DataManager() {
		try {
			logger = org.apache.log4j.Logger.getLogger("pl.edu.asim");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			factory = Persistence
					.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();
			em.getTransaction().commit();
			em.clear();
			em.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ASim: Brak polaczenia z baza danych!");
		}
		try {
			setJaxbContext(JAXBContext.newInstance(ASimDO.class, ASimPO.class));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ASim: JAXB error!");
		}

		logger.info("pl.edu.asim.model: Start");
	}

	public static synchronized DataManager getInstance() {
		if (instance == null)
			instance = new DataManager();
		return instance;
	}

	public synchronized EntityManager getEntityManager() {
		EntityManager em = factory.createEntityManager();
		return em;
	}

	public void close() {
		factory.close();
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}

	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

}
