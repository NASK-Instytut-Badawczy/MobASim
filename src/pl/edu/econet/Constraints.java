package pl.edu.econet;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class Constraints {

	/**
	 * Ograniczenie wymuszające wykorzystanie tylko jednego stanu energetycznego
	 * na łączu,
	 * 
	 * @param e
	 *            - numer łacza
	 * @param network
	 * @return
	 */
	public static double[] generateConstraint1D(int e, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		int i = 0;
		for (Link l : network.getLinks()) {
			i++;
			for (EAS eas : l.getLevels()) {
				if (i == e)
					coefficients = coefficients.append(1.0);
				else
					coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();

	}

	/**
	 * Ograniczenie wymuszające jednoczesne wykorzystanie w tym samym stanie
	 * dwóch łącz tworzących łącze dwukierunkowe
	 * 
	 * @param linkId
	 *            - identyfikator łącza
	 * @param easId
	 *            - identyfikator stanu energetycznego
	 * @param nLinkId
	 *            - identyfikator łącza powrotnego
	 * @param network
	 *            - sieć
	 * @return
	 */

	public static double[] generateConstraint1D_bidir(String linkId,
			String easId, String nLinkId, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			if (l.getId().equals(linkId)) {
				for (EAS eas : l.getLevels()) {
					if (eas.getId().equals(easId))
						coefficients = coefficients.append(1.0);
					else
						coefficients = coefficients.append(0.0);
				}
			} else if (l.getId().equals(nLinkId)) {
				for (EAS eas : l.getLevels()) {
					if (eas.getId().equals(easId))
						coefficients = coefficients.append(-1.0);
					else
						coefficients = coefficients.append(0.0);
				}
			} else {
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();
	}

	/**
	 * Ograniczenie w modelu przyrostowym, posortowanie rosnące wykorzystania
	 * stanów energetycznych.
	 * 
	 * @param linkId
	 *            - identyfikator łącza
	 * @param pEasId
	 *            - identyfikator stanu enegetycznego
	 * @param nEasId
	 *            - identyfikator następnego stanu energetycznego
	 * @param network
	 *            - sieć
	 * @return
	 */
	public static double[] generateConstraint1_step(String linkId,
			String pEasId, String nEasId, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			if (l.getId().equals(linkId)) {
				for (EAS eas : l.getLevels()) {
					if (eas.getId().equals(pEasId))
						coefficients = coefficients.append(-1.0);
					else if (eas.getId().equals(nEasId))
						coefficients = coefficients.append(1.0);
					else
						coefficients = coefficients.append(0.0);
				}
			} else {
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();

	}

	/**
	 * Ograniczenie 2: Ustalenie zmiennej x - wykorzystania karty
	 * 
	 * @param d
	 *            - numer zmiennej
	 * @param card
	 *            - nazwa karty
	 * @param network
	 *            - sieć
	 * @return
	 */

	public static double[] generateConstraint2(int d, String routerId,
			String cardId, Network network, int de, int ek, int ci, int ri) {

		RealVector coefficients = generateEmptyRow(de + ek + ci + ri + 1);

		/* u_de */

		int i = (d - 1) * network.getLinks().size() + 1;

		for (Link l : network.getLinks()) {
			coefficients.setEntry(i, l.getA(routerId, cardId));
			i++;
		}

		/* ek */

		/* c */
		i = de + ek + 1;

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				try {
					String cN = r.getId() + "/" + c.getId();
					String cN2 = routerId + "/" + cardId;
					if (cN.equals(cN2))
						coefficients.setEntry(i, -1.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}

		/* r */

		return coefficients.toArray();
	}

	public static RealVector generateEmptyRow(int size) {

		RealVector coefficients = new ArrayRealVector(size + 1);
		for (int i = 0; i <= size; i++)
			coefficients.setEntry(i, 0.0);
		return coefficients;
	}

	public static double[] generateConstraint2(String linkId, String easId,
			String routerId, String cardId, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				if (linkId.equals(l.getId()) && easId.equals(eas.getId())) {
					coefficients = coefficients.append(1.0);
				} else
					coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				try {
					String cN = r.getId() + "/" + c.getId();
					String cN2 = routerId + "/" + cardId;
					if (cN.equals(cN2)) {
						coefficients = coefficients.append(-1.0);
					} else
						coefficients = coefficients.append(0.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();

	}

	/**
	 * Ograniczenie 3: Ustalenie zmiennej x - wykorzystania karty
	 * 
	 * @param d
	 *            - numer zmiennej
	 * @param card
	 *            - nazwa karty
	 * @param network
	 *            - sieć
	 * @return
	 */

	public static double[] generateConstraint3(int d, String routerId,
			String cardId, Network network, int de, int ek, int ci, int ri) {

		RealVector coefficients = generateEmptyRow(de + ek + ci + ri + 1);

		/* u_de */

		int i = (d - 1) * network.getLinks().size() + 1;

		for (Link l : network.getLinks()) {
			coefficients.setEntry(i, l.getB(routerId, cardId));
			i++;
		}

		/* ek */

		/* c */
		i = de + ek + 1;

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				try {
					String cN = r.getId() + "/" + c.getId();
					String cN2 = routerId + "/" + cardId;
					if (cN.equals(cN2))
						coefficients.setEntry(i, -1.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}

		/* r */

		return coefficients.toArray();
	}

	/**
	 * Wykorzystanie routera
	 * 
	 * @param card
	 *            - nazwa karty
	 * @param network
	 *            - sieć
	 * @return
	 */
	public static double[] generateConstraint4D(String routerId, String cardId,
			Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				try {
					String cN = r.getId() + "/" + c.getId();
					String cN2 = routerId + "/" + cardId;
					if (cN.equals(cN2))
						coefficients = coefficients.append(1.0);
					else
						coefficients = coefficients.append(0.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			if (routerId.equals(r.getId()))
				coefficients = coefficients.append(-1.0);
			else
				coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();
	}

	/**
	 * Prawo przepływu
	 * 
	 * @param d
	 *            - identyfikator zapotrzebowania
	 * @param router
	 *            - nazwa routera
	 * @param network
	 *            - sieć
	 * @return
	 */
	public static double[] generateConstraintK(int d, String router,
			Network network, int de, int ek, int ci, int ri) {

		RealVector coefficients = generateEmptyRow(de + ek + ci + ri + 1);

		/* u_de */

		int i = (d - 1) * network.getLinks().size() + 1;

		/* u_de */
		for (Link l : network.getLinks()) {
			int re = l.getA(router).intValue() - l.getB(router).intValue();
			coefficients.setEntry(i, re);
			i++;
		}

		return coefficients.toArray();
	}

	/**
	 * Ograniczenie ustawiające wymagane pojemności łączy - wersja dla modelu
	 * przyrostowego
	 * 
	 * @param e
	 *            - identyfikator łącza
	 * @param network
	 *            - sieć
	 * @return
	 */
	public static double[] generateConstraint8_step(String e, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands()) {
			for (Link l : network.getLinks()) {
				if (l.getId().equals(e)) {
					coefficients = coefficients.append(network.getOverbooking()
							* d.getVolume());
				} else {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Link l : network.getLinks()) {
			if (l.getId().equals(e))
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(-eas
							.getBandwidthDifference());
				}
			else
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(0.0);
				}
		}

		for (Router ru : network.getRouters()) {
			for (Card c : ru.getCards()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router ru : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();
	}

	/**
	 * Ograniczenie ustawiające wymagane pojemności łączy
	 * 
	 * @param e
	 *            - identyfikator łącza
	 * @param network
	 *            - sieć
	 * @return
	 */

	public static double[] generateConstraint8D(String e, Network network) {

		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		/* u_de */
		for (Demand d : network.getDemands()) {
			for (Link l : network.getLinks()) {
				if (l.getId().equals(e)) {
					coefficients = coefficients.append(network.getOverbooking()
							* d.getVolume());
				} else {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Link l : network.getLinks()) {
			if (l.getId().equals(e))
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(-eas.getBandwidth());
				}
			else
				for (EAS eas : l.getLevels()) {
					coefficients = coefficients.append(0.0);
				}
		}

		for (Router ru : network.getRouters()) {
			for (Card c : ru.getCards()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router ru : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return coefficients.toArray();
	}

}
