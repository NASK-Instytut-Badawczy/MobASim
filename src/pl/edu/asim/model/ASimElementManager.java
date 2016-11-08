package pl.edu.asim.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;

public class ASimElementManager {

	private static final String PERSISTENCE_UNIT_NAME = "eclipseLink";
	static ASimElementManager instance;
	private static EntityManagerFactory factory;
	private final EntityManager em;
	private Logger logger;

	ASimElementManager() {
		try {
			logger = org.apache.log4j.Logger.getLogger("pl.edu.asim");

		} catch (Exception e) {
			e.printStackTrace();
		}

		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();

		logger.info("pl.edu.asim.model: Start");
	}

	public static ASimElementManager getInstance() {
		if (instance == null)
			instance = new ASimElementManager();
		return instance;
	}

	public List<ASimDO> getElementListByTypeAndName(String type, String name) {
		Query q = em
				.createQuery("select e from ASimDO e where e.type = :type and e.name = :name");
		q.setParameter("type", type);
		q.setParameter("name", name);

		@SuppressWarnings("unchecked")
		List<ASimDO> resultList = q.getResultList();
		return resultList;
	}

	public List<ASimDO> getElementListByType(String type) {
		Query q = em.createQuery("select e from ASimDO e where e.type = :type");
		q.setParameter("type", type);

		@SuppressWarnings("unchecked")
		List<ASimDO> resultList = q.getResultList();
		return resultList;
	}

	public List<ASimDO> getElementListByFather(ASimDO father) {
		Query q = em
				.createQuery("select e from ASimDO e where e.father = :father");
		q.setParameter("father", father);

		@SuppressWarnings("unchecked")
		List<ASimDO> resultList = q.getResultList();
		return resultList;
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public void close() {
		em.close();
		factory.close();
	}
}
