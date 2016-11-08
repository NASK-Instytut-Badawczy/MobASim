package pl.edu.econet;

import java.io.File;
import java.math.BigDecimal;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class BB_step {

	/**
	 * @param args
	 */

	LpSolve solver;

	Network network;

	int de = 0;

	int ek = 0;

	int c = 0;

	int r = 0;

	String name;

	/** Liczba zmiennych */
	int columnCount = 0;

	/** Liczba wezlow algorytmu B&B */
	int bBNodeCount = 0;

	/** Wartosc funkcji celu dla najlepszego znalezionego rozwiazania */
	BigDecimal z_best = null;

	/** Wartosci zmiennych dla najlepszego znalezionego rozwiazania */
	double[] x_best = null;

	public BB_step(Network network, String name) {
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
			solver = LpSolve.makeLp(0, columnCount);
			for (int i = 1; i <= columnCount; i++) {
				solver.setBinary(i, true);
			}
			this.setColumnName(network, solver);
			solver.setObjFn(Objectives.getObjectiveD_step(network));

			int i = 0;

			/*
			 * ograniczenie dla modelu przyrostowego, posortowanie rosnące
			 * wykorzystania stanów energetycznych
			 */
			for (Link l : network.getLinks()) {
				EAS prev = null;
				for (EAS eas : l.getLevels()) {
					i++;
					if (prev != null)
						solver.addConstraint(Constraints
								.generateConstraint1_step(l.getId(),
										prev.getId(), eas.getId(), network),
								LpSolve.LE, 0);

					prev = eas;
				}
				solver.addConstraint(Constraints.generateConstraint8_step(
						l.getId(), network), LpSolve.LE, 0);

			}

			/* wymuszenie dwukierunkowści łącz */
			for (Link l : network.getLinks()) {
				if (l.getReturnLink() != null)
					for (EAS eas : l.getLevels()) {
						i++;
						solver.addConstraint(Constraints
								.generateConstraint1D_bidir(l.getId(),
										eas.getId(), l.getReturnLink().getId(),
										network), LpSolve.EQ, 0);
					}
			}

			/* wykorzystanie routera */
			int index = de + ek + c;
			for (Router r : network.getRouters()) {
				boolean find = false;
				for (Demand d : network.getDemands()) {
					if ((r.getId().equals(d.getSource()) && d.getVolume() > 0)
							|| (r.getId().equals(d.getSink()) && d.getVolume() > 0)) {
						find = true;
						solver.setLowbo(index + 1, 1);
					}
				}
				index++;
				if (!find)
					for (Card c : r.getCards()) {
						solver.addConstraint(
								Constraints.generateConstraint4D(r.getId(),
										c.getId(), network), LpSolve.LE, 0);
					}
			}
			/* prawo przepływu */
			i = 0;
			for (Demand d : network.getDemands()) {
				i++;
				for (Router r : network.getRouters()) {
					if (r.getId().equals(d.getSource()))
						solver.addConstraint(Constraints.generateConstraintK(i,
								r.getId(), network, de, ek, this.c, this.r),
								LpSolve.EQ, 1);
					else if (r.getId().equals(d.getSink()))
						solver.addConstraint(Constraints.generateConstraintK(i,
								r.getId(), network, de, ek, this.c, this.r),
								LpSolve.EQ, -1);
					else
						solver.addConstraint(Constraints.generateConstraintK(i,
								r.getId(), network, de, ek, this.c, this.r),
								LpSolve.EQ, 0);
					for (Card c : r.getCards()) {
						solver.addConstraint(Constraints.generateConstraint2(i,
								r.getId(), c.getId(), network, de, ek, this.c,
								this.r), LpSolve.LE, 0);
						solver.addConstraint(Constraints.generateConstraint3(i,
								r.getId(), c.getId(), network, de, ek, this.c,
								this.r), LpSolve.LE, 0);
					}
				}
			}

			long time = System.currentTimeMillis();
			solver.setBbRule(LpSolve.BRANCH_AUTOMATIC);

			solver.setOutputfile("results/" + name + "/BB_step.txt");
			int state = solver.solve();
			solver.printSolution(1);
			solver.writeLp("results/" + name + "/model_MIP_step.txt");
			time = System.currentTimeMillis() - time;
			System.out.println("time:" + (time) + " solution="
					+ solver.getObjective() + " ek=" + ek
					+ " de=" + de+" constraints="+solver.getNrows());

		} catch (LpSolveException e) {
			e.printStackTrace();
		}
		solver.deleteLp();
	}

	public void setColumnName(Network network, LpSolve solver) {
		int i = 0;

		for (Demand d : network.getDemands())
			for (Link l : network.getLinks()) {
				i++;
				try {
					solver.setColName(i, "u_" + d.getId() + "_" + l.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		for (Link l : network.getLinks()) {
			for (EAS eas : l.getLevels()) {
				i++;
				try {
					solver.setColName(i, "y_" + l.getId() + "_" + eas.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			for (Card c : r.getCards()) {
				i++;
				try {
					solver.setColName(i, "x_" + c.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Router r : network.getRouters()) {
			i++;
			try {
				solver.setColName(i, "z_" + r.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
