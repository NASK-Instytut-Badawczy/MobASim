package pl.edu.econet;

import java.util.ArrayList;

import javax.naming.CompositeName;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.Relationship;

import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class ConstraintsMath {

	public static ArrayList<LinearConstraint> getUpbo(int count, double up) {
		ArrayList<LinearConstraint> result = new ArrayList<LinearConstraint>();
		for (int i = 0; i < count; i++) {
			result.add(getUpbo(count, i, up));
		}
		return result;
	}

	public static ArrayList<LinearConstraint> getLowbo(int count, double low) {
		ArrayList<LinearConstraint> result = new ArrayList<LinearConstraint>();
		for (int i = 0; i < count; i++) {
			result.add(getLowbo(count, i, low));
		}
		return result;
	}

	public static LinearConstraint getUpbo(int count, int index, double up) {
		RealVector coefficients = new ArrayRealVector();
		for (int i = 0; i < count; i++) {
			if (i == index)
				coefficients = coefficients.append(1.0);
			else
				coefficients = coefficients.append(0.0);
		}

		return new LinearConstraint(coefficients, Relationship.LEQ, up);
	}

	public static LinearConstraint getLowbo(int count, int index, double low) {
		RealVector coefficients = new ArrayRealVector();
		for (int i = 0; i < count; i++) {
			if (i == index)
				coefficients = coefficients.append(1.0);
			else
				coefficients = coefficients.append(0.0);
		}

		return new LinearConstraint(coefficients, Relationship.GEQ, low);
	}

	public static LinearConstraint generateConstraint1(String linkId,
			String pEasId, String nEasId, Network network) {

		RealVector coefficients = new ArrayRealVector();

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

		return new LinearConstraint(coefficients, Relationship.LEQ, 0.0);
	}

	public static LinearConstraint generateConstraint1_bidir(String linkId,
			String easId, String nLinkId, Network network) {

		RealVector coefficients = new ArrayRealVector();

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

		return new LinearConstraint(coefficients, Relationship.EQ, 0.0);
	}

	public static LinearConstraint generateConstraint2(int d, String routerId,
			String cardId, CompositeName card, Network network) {

		RealVector coefficients = new ArrayRealVector();

		/* u_de */
		int i = 0;
		for (Demand dM : network.getDemands()) {
			i++;
			if (d == i) {
				for (Link l : network.getLinks()) {
					coefficients = coefficients
							.append(l.getA(routerId, cardId));
				}
			} else {
				for (Link l : network.getLinks()) {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				coefficients = coefficients.append(0.0);
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				try {
					CompositeName cN = new CompositeName(r.getId() + "/"
							+ c.getId());
					if (cN.toString().equals(card.toString()))
						coefficients = coefficients.append(-1.0);
					else
						coefficients = coefficients.append(0.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(0.0);
		}

		return new LinearConstraint(coefficients, Relationship.LEQ, 0.0);
	}

	public static LinearConstraint generateConstraint2(String linkId,
			String easId, CompositeName card, Network network) {

		RealVector coefficients = new ArrayRealVector();

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
					CompositeName cN = new CompositeName(r.getId() + "/"
							+ c.getId());
					if (cN.toString().equals(card.toString())) {
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

		return new LinearConstraint(coefficients, Relationship.LEQ, 0.0);
	}

	public static LinearConstraint generateConstraint4(CompositeName card,
			Network network) {

		RealVector coefficients = new ArrayRealVector();

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
					CompositeName cN = new CompositeName(r.getId() + "/"
							+ c.getId());
					if (cN.toString().equals(card.toString()))
						coefficients = coefficients.append(1.0);
					else
						coefficients = coefficients.append(0.0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			if (card.get(0).equals(r.getId()))
				coefficients = coefficients.append(-1.0);
			else
				coefficients = coefficients.append(0.0);
		}

		return new LinearConstraint(coefficients, Relationship.LEQ, 0.0);
	}

	public static LinearConstraint generateConstraintK(String d, String router,
			Network network, double st) {

		RealVector coefficients = new ArrayRealVector();

		/* u_de */
		for (Demand dM : network.getDemands()) {
			if (d.equals(dM.getId())) {
				for (Link l : network.getLinks()) {
					int re = l.getA(router).intValue()
							- l.getB(router).intValue();

					coefficients = coefficients.append(re);
				}
			} else {
				for (Link l : network.getLinks()) {
					coefficients = coefficients.append(0.0);
				}
			}
		}

		for (Link l : network.getLinks()) {
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

		return new LinearConstraint(coefficients, Relationship.EQ, st);
	}

	public static LinearConstraint generateConstraint8(String e, Network network) {

		RealVector coefficients = new ArrayRealVector();

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

		return new LinearConstraint(coefficients, Relationship.LEQ, 0.0);
	}

}
