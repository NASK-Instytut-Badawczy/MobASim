package pl.edu.econet;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.naming.CompositeName;

import lpsolve.LpSolve;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.SimplexSolver;

import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.energy.EASRow;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class RelaxationMath {

	/**
	 * @param args
	 */

	Network network;

	String name;

	int de = 0;

	int ek = 0;

	int c = 0;

	int r = 0;

	/** Liczba zmiennych */
	int columnCount = 0;

	/** Liczba wezlow algorytmu B&B */
	int bBNodeCount = 0;

	/** Wartosc funkcji celu dla najlepszego znalezionego rozwiazania */
	BigDecimal z_best = null;

	/** Wartosci zmiennych dla najlepszego znalezionego rozwiazania */
	double[] x_best = null;

	ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
	NonNegativeConstraint nonNegativeConstraint = new NonNegativeConstraint(
			true);
	LinearObjectiveFunction objectiveFunction;

	public RelaxationMath(Network network, String name) {
		this.network = network;
		this.name = name;
		String dir = "results/" + name + "/";
		File resDir = new File(dir);
		resDir.mkdir();

		r = network.getRouters().size();
		for (Router r : network.getRouters()) {
			r.init(network);
			c = c + r.getCards().size();
		}
		for (Link l : network.getLinks()) {
			l.setNetwork(network);
			ek = ek + l.getLevels().size();
		}
		de = network.getDemands().size() * network.getLinks().size();

		columnCount = de + ek + c + r;

		try {

			constraints.addAll(ConstraintsMath.getLowbo(columnCount, 0));
			constraints.addAll(ConstraintsMath.getUpbo(columnCount, 1));

			objectiveFunction = Objectives.getObjective_step(network);

			int i = 0;

			for (Link l : network.getLinks()) {
				EAS prev = null;
				if (l.getReturnLink() != null)
					for (EAS eas : l.getLevels()) {
						i++;
						if (prev == null) {

						} else {
							constraints
									.add(ConstraintsMath.generateConstraint1(
											l.getId(), prev.getId(),
											eas.getId(), network));
						}
						prev = eas;
					}
			}

			for (Link l : network.getLinks()) {
				EAS prev = null;
				if (l.getReturnLink() != null)
					for (EAS eas : l.getLevels()) {
						i++;
						constraints.add(ConstraintsMath
								.generateConstraint1_bidir(l.getId(),
										eas.getId(), l.getReturnLink().getId(),
										network));
						prev = eas;
					}
			}

			i = 0;
			for (Demand d : network.getDemands()) {
				i++;
				for (Router r : network.getRouters()) {
					for (Card c : r.getCards()) {
						try {
							CompositeName cN = new CompositeName(r.getId()
									+ "/" + c.getId());
							constraints.add(ConstraintsMath
									.generateConstraint2(i, r.getId(),
											c.getId(), cN, network));
							// solver.strAddConstraint(generateConstraint3(i,
							// cN),
							// LpSolve.LE, 0);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			for (Router r : network.getRouters()) {
				for (Card c : r.getCards()) {
					for (Link l : network.getLinks()) {
						if (l.getReturnLink() != null) {
							EAS eas = l.getLevels().get(0);
							try {
								CompositeName cN = new CompositeName(r.getId()
										+ "/" + c.getId());
								if (l.getA(r.getId(), c.getId()) > 0
										|| l.getB(r.getId(), c.getId()) > 0)
									constraints.add(ConstraintsMath
											.generateConstraint2(l.getId(),
													eas.getId(), cN, network));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			int index = de + ek + c;
			for (Router r : network.getRouters()) {
				boolean find = false;
				for (Demand d : network.getDemands()) {
					if (r.getId().equals(d.getSource())
							|| r.getId().equals(d.getSink())) {
						find = true;
						constraints.add(ConstraintsMath.getLowbo(columnCount,
								index, 1));
						// solver.setLowbo(index + 1, 1);
					}
				}
				index++;
				if (!find)
					for (Card c : r.getCards()) {
						try {
							CompositeName cN = new CompositeName(r.getId()
									+ "/" + c.getId());
							constraints.add(ConstraintsMath
									.generateConstraint4(cN, network));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}

			for (Demand d : network.getDemands()) {
				for (Router r : network.getRouters()) {
					if (r.getId().equals(d.getSource()))
						constraints.add(ConstraintsMath.generateConstraintK(
								d.getId(), r.getId(), network, 1.0));
					else if (r.getId().equals(d.getSink()))
						constraints.add(ConstraintsMath.generateConstraintK(
								d.getId(), r.getId(), network, -1.0));
					else
						constraints.add(ConstraintsMath.generateConstraintK(
								d.getId(), r.getId(), network, 0));
				}
			}

			for (Link l : network.getLinks()) {
				constraints.add(ConstraintsMath.generateConstraint8(l.getId(),
						network));
			}

			long time = System.currentTimeMillis();

			// Integer iteration = 0;

			SimplexSolver simplex = new SimplexSolver();
			PointValuePair result = simplex.optimize(new MaxIter(100000000),
					this.objectiveFunction,
					new LinearConstraintSet(constraints));

			// iteration = upYMin(solver, iteration, de, ek,
			// network);
			// iteration = Aproximations.upU(solver, iteration, bean, de);
			// iteration = Aproximations.upYMin(solver, iteration, bean, de, ek,
			// network);

			System.out.println("time:" + (System.currentTimeMillis() - time)
					+ " solution=" + Objectives.getSolution(network, result));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int upYMin(LpSolve solver, int iteration, int de, int ek,
			Network network) {
		try {
			double rTable[] = null;

			boolean find = true;
			while (find) {
				iteration++;
				solver.setOutputfile("results/" + name + "/r" + iteration
						+ ".txt");
				int wynik = solver.solve();
				solver.printSolution(1);
				solver.writeLp("results/" + name + "/model_r" + iteration
						+ ".txt");
				System.out
						.println("iteration +++++++++++++++++++++++++++++++++ "
								+ iteration + " " + wynik);
				if (wynik > 0) {
					System.out.println("!!! Brak rozwiazania ");
					break;
				}

				find = false;
				rTable = solver.getPtrVariables();

				String columnName = "";
				int column = -1;
				double max = 0;
				int level = -1;
				EASRow row2 = null;
				int index2 = de;
				for (Link link : network.getLinks()) {
					EASRow row = new EASRow();
					row.setEasList(link.getLevels());
					for (EAS eas : link.getLevels()) {
						row.getVariables().put(eas.getId(), rTable[index2]);
						row.getIndexes().put(eas.getId(), index2);
						index2++;
					}
					if (row.getUnallocation() > max) {
						max = row.getUnallocation();
						column = row.getIndexes().get(
								row.getFirstNonIntegerId()) + 1;
						columnName = "y_" + link.getId() + "_"
								+ row.getFirstNonIntegerId();
						row2 = row;
					}
				}

				if (column > 0) {
					find = true;
					System.out.println("add " + columnName + " " + max
							+ " level=" + row2.getFirstNonIntegerIndex());
					solver.setLowbo(column, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iteration;
	}

	// public String generateAdditionalConstraint(String name) {
	// String result = "";
	// try {
	// for (int i = 1; i <= columnCount; i++) {
	// String n = solver.getColName(i);
	// if (n.equals(name)) {
	// if (result.equals(""))
	// result = result + "1";
	// else
	// result = result + " 1";
	// } else {
	// if (result.equals(""))
	// result = result + "0";
	// else
	// result = result + " 0";
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return result;
	// }

}
