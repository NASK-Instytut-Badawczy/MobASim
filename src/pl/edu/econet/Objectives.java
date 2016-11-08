package pl.edu.econet;

import java.util.ArrayList;

import lpsolve.LpSolve;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;

import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class Objectives {

	/**
	 * Funkcja celu dla modelu zwykłego
	 * 
	 * @param network
	 * @param solver
	 * @return
	 */
	public static double[] getObjective(Network network) {
		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);
		
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				coefficients = coefficients.append(eas.getPower());
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(c.getEcoAdvantage());
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(r.getEcoAdvantage());
		}
		return coefficients.toArray();
	}

	/**
	 * Funkcja celu dla modelu przyrostowego
	 * 
	 * @param network
	 * @return
	 */
	public static double[] getObjectiveD_step(Network network) {
		RealVector coefficients = new ArrayRealVector();
		coefficients = coefficients.append(0.0);

		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				coefficients = coefficients.append(eas.getPowerDifference());
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(c.getEcoAdvantage());
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(r.getEcoAdvantage());
		}
		return coefficients.toArray();
	}

	
	
	public static LinearObjectiveFunction getObjective_step(Network network) {
		RealVector coefficients = new ArrayRealVector();
		
		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				coefficients = coefficients.append(0.0);
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				coefficients = coefficients.append(eas.getPowerDifference());
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				coefficients = coefficients.append(c.getEcoAdvantage());
			}
		}

		for (Router r : network.getRouters()) {
			coefficients = coefficients.append(r.getEcoAdvantage());
		}
		return new LinearObjectiveFunction(coefficients, 0.0);
	}


	public static Double getSolution(Network network, PointValuePair pv) {

		double[] point = pv.getPoint();
		double result = 0;
		int i = 0;

		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				i++;
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				result = result + point[i] * eas.getPowerDifference();
				i++;
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				result = result + point[i] * c.getEcoAdvantage();
				i++;
			}
		}

		for (Router r : network.getRouters()) {
			result = result + point[i] * r.getEcoAdvantage();
			i++;
		}
		return result;
	}

	/**
	 * Lista nazw kolumn
	 * 
	 * @param network
	 * @return
	 */
	public static ArrayList<String> getColumnNames(Network network) {
		ArrayList<String> result = new ArrayList<String>();

		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				result.add("u_" + d.getId() + "_" + l.getId());
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				result.add("y_" + l.getId() + "_" + eas.getId());
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				result.add("x_" + c.getName());
			}
		}

		for (Router r : network.getRouters()) {
			result.add("z_" + r.getId());
		}
		return result;
	}

}
