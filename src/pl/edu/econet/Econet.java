package pl.edu.econet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import pl.edu.asim.ASimPlatform;
import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class Econet {

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

	public Econet(Network network, String name1) {
		this.network = network;
		this.name = name1;
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
			for (int i = 1; i <= de; i++) {
				solver.setBinary(i, true);
			}
			for (int i = de + 1; i <= de + ek; i++) {
				solver.setBinary(i, true);
			}
			for (int i = de + 1; i <= columnCount; i++) {
				solver.setBinary(i, true);
			}
			this.setColumnName(network, solver);
			solver.setObjFn(Objectives.getObjective(network));

			int i = 0;

			/* tylko jeden stan energetyczny może zostać wykorzystany */
			for (Link l : network.getLinks()) {
				i++;
				solver.addConstraint(
						Constraints.generateConstraint1D(i, network),
						LpSolve.LE, 1);
				solver.addConstraint(
						Constraints.generateConstraint8D(l.getId(), network),
						LpSolve.LE, 0);
			}

			/* wymuszenie dwukierunkowości łącz */
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

			/* wykorzystanie rutera */
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

			solver.setOutputfile("results/" + name + "/BB.txt");
			int state = solver.solve();
			solver.printSolution(1);
			solver.writeLp("results/" + name + "/model_MIP.txt");
			time = System.currentTimeMillis() - time;
			System.out.println("time:" + (time) + " solution="
					+ solver.getObjective() + " ek=" + ek + " de=" + de
					+ " constraints=" + solver.getNrows());

			double[] rTable = solver.getPtrVariables();

			HashMap<String, Demand> dMap = new HashMap<String, Demand>();
			HashMap<String, Link> lMap = new HashMap<String, Link>();
			HashMap<String, Router> rMap = new HashMap<String, Router>();
			for (Router r : network.getRouters()) {
				rMap.put(r.getId(), r);
			}
			for (Link l : network.getLinks()) {
				lMap.put(l.getId(), l);
			}
			for (Demand d : network.getDemands()) {
				d.getPath().getLinks().clear();
				dMap.put(d.getId(), d);
			}
			for (i = 0; i < de; i++) {
				String colName = solver.getColName(i + 1);
				String demandName = colName.substring(1 + colName.indexOf("_"),
						colName.lastIndexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015) {
					Demand d = dMap.get(demandName);
					String vI = colName.substring(1 + colName.lastIndexOf("_"));
					Link l = lMap.get(vI);
					d.getPath().getLinks().add(l);
					// d.getRouters().add(
					// l.getSource() + " $\\rightarrow$ " + l.getSink());
				}
			}
			for (i = de; i < de + ek; i++) {
				String colName = solver.getColName(i + 1);
				String linkName = colName.substring(1 + colName.indexOf("_"),
						colName.lastIndexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015) {
					Link l = lMap.get(linkName);
					String easId = colName.substring(1 + colName
							.lastIndexOf("_"));
					for (EAS eas : l.getLevels()) {
						if (easId.equals(eas.getId())) {
							l.setEas(eas);
						}
					}
				}
			}
			String cardList = "";
			int cardCount = 0;
			for (i = de + ek; i < de + ek + c; i++) {
				String colName = solver.getColName(i + 1);
				String cardName = colName.substring(1 + colName.indexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015)
					cardList = cardList + cardName + ", ";
				cardCount = cardCount + (int) value;
			}
			int routerCount = 0;
			String routerList = "";
			for (i = de + ek + c; i < de + ek + c + r; i++) {
				String colName = solver.getColName(i + 1);
				String routerName = colName.substring(1 + colName.indexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015)
					routerList = routerList + routerName + ", ";
				routerCount = routerCount + (int) value;
			}

			FileWriter fstream;
			fstream = new FileWriter("results/tex/" + name + ".tex", false);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("\\documentclass[12pt]{article}");
			out.write("\\input{preambula_pakiety.tex}");
			out.write("\\input{preambula_ustawienia.tex}");
			out.write("\\begin{document}");

			out.write("\\begin{figure}[!htb]" + "\\centering"
					+ "\\includegraphics[width=8cm, height=4cm]{obrazki/"
					+ name + ".png}" + "\\caption" + "{"
					+ name.replaceAll("_", "\\\\_") + "}" + "\\end{figure}");
			out.newLine();
			out.newLine();

			out.write("\\begin{table}[!htb] \\begin{tabular}{|l|l|} \\hline");
			out.newLine();
			out.write("Network name: & " + name.replaceAll("_", "\\\\_")
					+ " \\\\");
			out.newLine();
			out.write("Number of routers: & " + network.getRouters().size()
					+ " \\\\");
			out.newLine();
			out.write("Number of links: & " + network.getLinks().size()
					+ " \\\\");
			out.newLine();
			out.write("Number of demands: & " + network.getDemands().size()
					+ " \\\\");
			out.newLine();
			out.write("Overbooking factor: & " + network.getOverbooking()
					+ " \\\\");
			out.newLine();
			for (EAS eas : network.getLinks().get(0).getLevels()) {
				out.write("EAS of link & Id=" + eas.getId() + " Bandwidth="
						+ eas.getBandwidth() + " Power=" + eas.getPower()
						+ " \\\\");
				out.newLine();
			}
			for (Router r : network.getRouters()) {
				out.write("Router " + r.getId() + " busy & " + " Power="
						+ r.getBusy().getPower() + " \\\\");
				out.newLine();
				for (Card c : r.getCards()) {
					out.write("Card " + r.getId() + "/" + c.getId()
							+ " busy & " + " Power=" + c.getBusy().getPower()
							+ " \\\\");
					out.newLine();
				}
			}
			out.write("Calculation time & \\color{OliveGreen} " + time
					+ "ms \\\\");
			out.newLine();
			out.write("Number of variables: & " + solver.getNcolumns()
					+ " \\\\");
			out.newLine();
			out.write("Number of constraints: & " + solver.getNrows() + " \\\\");
			out.newLine();

			double routerPower = routerCount
					* network.getRouters().get(0).getBusy().getPower();
			double cardPower = cardCount
					* network.getRouters().get(0).getCards().get(0).getBusy()
							.getPower();

			double totalRouterPower = 0;
			double totalCardPower = 0;
			int totalRouterCounter = 0;
			int totalCardCounter = 0;

			for (Router router : network.getRouters()) {
				totalRouterPower = totalRouterPower
						+ router.getBusy().getPower();
				totalRouterCounter++;
				for (Card card : router.getCards()) {
					totalCardPower = totalCardPower + card.getBusy().getPower();
					totalCardCounter++;
				}
			}

			double totalLinkPower = network.getLinks().get(0).getLevels()
					.get(network.getLinks().get(0).getLevels().size() - 1)
					.getPower()
					* network.getLinks().size();
			double linkPower = 0.0;
			int linkCount = 0;

			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
			out.write("\\begin{table}[!htb] \\begin{tabular}{|l|l|l|l|p{8cm}|} \\hline");
			out.newLine();
			out.write("Demand & Source & Sink & Volume & Realization of demand: used links \\\\\\hline");
			out.newLine();
			for (Demand d : network.getDemands()) {
				// String rString = "";
				// for (String r : d.getRouters()) {
				// rString = rString + r + ", ";
				// }
				out.write(d.getId() + " & " + d.getSource() + " & "
						+ d.getSink() + " & " + d.getVolume()
						+ " & \\color{OliveGreen} "
						+ d.getPath().getAsSortedString(d.getSource())
						+ " \\\\\\hline");
				out.newLine();
			}
			out.write("\\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
			out.write("\\begin{table}[!htb] \\begin{tabular}{|l|l|l|l|} \\hline");
			out.newLine();
			out.write("Link & EAS id & Bandwidth & Power \\\\\\hline");
			out.newLine();
			for (Link l : network.getLinks()) {
				if (l.getEas() != null) {
					out.write(l.getSource() + " $\\rightarrow$ " + l.getSink()
							+ " & \\color{OliveGreen} " + l.getEas().getId()
							+ " & \\color{OliveGreen} "
							+ l.getEas().getBandwidth()
							+ " &  \\color{OliveGreen}" + l.getEas().getPower()
							+ " \\\\");
					linkPower = linkPower + l.getEas().getPower();
					linkCount++;
					out.newLine();
				}
				// else
				// out.write(l.getSource() + " $\\rightarrow$ " + l.getSink()
				// + " &   & \\color{OliveGreen}" + 0.0
				// + " & \\color{OliveGreen}" + 0.0 + " \\\\");

			}
			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
			out.write("\\begin{table}[!htb] \\begin{tabular}{|l|l|l|l|l|} \\hline");
			out.newLine();
			out.write(" & Routers & Cards & Links & Sum \\\\\\hline");
			out.newLine();
			out.write("Optimized Counter & \\color{OliveGreen} " + routerCount
					+ " & \\color{OliveGreen} " + cardCount
					+ " & \\color{OliveGreen} " + linkCount + " & \\\\");
			out.newLine();
			out.write("Optimized Power & \\color{OliveGreen} " + routerPower
					+ " & \\color{OliveGreen} " + cardPower
					+ " & \\color{OliveGreen} " + linkPower
					+ " & \\color{OliveGreen} " + solver.getObjective()
					+ "\\\\\\hline");
			out.newLine();
			out.write("Total counter &  " + totalRouterCounter + " &  "
					+ totalCardCounter + " &  " + network.getLinks().size()
					+ " & \\\\");
			out.newLine();
			out.write("Total power &  " + totalRouterPower + " &  "
					+ totalCardPower + " &  " + totalLinkPower + " &  "
					+ (totalRouterPower + totalCardPower + totalLinkPower)
					+ "\\\\");
			out.newLine();
			out.write("\\hline \\end{tabular} \\end{table}");
			out.write("\\end{document}");
			out.newLine();
			out.flush();
			out.close();
			ASimPlatform.commandLine();

		} catch (LpSolveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
