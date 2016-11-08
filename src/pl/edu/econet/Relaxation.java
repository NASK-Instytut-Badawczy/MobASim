package pl.edu.econet;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import pl.edu.asim.ASimPlatform;
import pl.edu.econet.demand.Demand;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.equipment.Card;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class Relaxation {

	/**
	 * @param args
	 */

	LpSolve solver;

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

	public Relaxation(Network network, String name) {
		this.network = network;
		this.name = name;
		String dir = "results/" + name + "/";
		File resDir = new File(dir);
		resDir.mkdir();
		long time = System.currentTimeMillis();

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
				solver.setLowbo(i, 0);
				solver.setUpbo(i, 1);
			}
			this.setColumnName(network, solver);
			solver.setObjFn(Objectives.getObjectiveD_step(network));

			int i = 0;

			// for (Link l : network.getLinks()) {
			// EAS prev = null;
			// for (EAS eas : l.getLevels()) {
			// i++;
			// if (prev != null)
			// solver.addConstraint(Constraints
			// .generateConstraint1_step(l.getId(),
			// prev.getId(), eas.getId(), network),
			// LpSolve.LE, 0);
			//
			// prev = eas;
			// }
			// }
			//
			// i = de;
			//
			// for (Link l : network.getLinks()) {
			// if (l.getReturnLink() != null)
			// for (EAS eas : l.getLevels()) {
			// i++;
			// solver.addConstraint(Constraints
			// .generateConstraint1D_bidir(l.getId(),
			// eas.getId(), l.getReturnLink().getId(),
			// network), LpSolve.EQ, 0);
			// }
			// solver.addConstraint(Constraints.generateConstraint8_step(
			// l.getId(), network), LpSolve.LE, 0);
			// }

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
			for (Router r : network.getRouters()) {
				for (Card c : r.getCards()) {
					for (Link l : network.getLinks()) {
						if (l.getReturnLink() != null) {
							EAS eas = l.getLevels().get(0);
							if (l.getA(r.getId(), c.getId()) > 0
									|| l.getB(r.getId(), c.getId()) > 0)
								solver.addConstraint(
										Constraints.generateConstraint2(
												l.getId(), eas.getId(),
												r.getId(), c.getId(), network),
										LpSolve.LE, 0);
						}
					}
				}
			}

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

			// i = 0;
			// for (Demand d : network.getDemands()) {
			// i++;
			// for (Router r : network.getRouters()) {
			// if (r.getId().equals(d.getSource()))
			// solver.addConstraint(Constraints.generateConstraintK(i,
			// r.getId(), network, de, ek, this.c, this.r),
			// LpSolve.EQ, 1);
			// else if (r.getId().equals(d.getSink()))
			// solver.addConstraint(Constraints.generateConstraintK(i,
			// r.getId(), network, de, ek, this.c, this.r),
			// LpSolve.EQ, -1);
			// else
			// solver.addConstraint(Constraints.generateConstraintK(i,
			// r.getId(), network, de, ek, this.c, this.r),
			// LpSolve.EQ, 0);
			// for (Card c : r.getCards()) {
			// solver.addConstraint(Constraints.generateConstraint2(i,
			// r.getId(), c.getId(), network, de, ek, this.c,
			// this.r), LpSolve.LE, 0);
			// }
			// }
			// }

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

			Integer iteration = 0;
			iteration = Aproximations.upYMin(solver, iteration, name, de, ek,
					network);
			if (iteration == -1)
				return;
			iteration = Aproximations.upU(solver, iteration, name, de);
			if (iteration == -1)
				return;

			while (!Aproximations.testResults(solver)) {
				iteration = Aproximations.upYMin(solver, iteration, name, de,
						ek, network);
				if (iteration == -1)
					return;
				iteration = Aproximations.upU(solver, iteration, name, de);
				if (iteration == -1)
					return;
			}

			time = System.currentTimeMillis() - time;
			System.out.println("time:" + (time) + " solution="
					+ solver.getObjective() + " ek=" + ek + " de=" + de
					+ " constraints=" + solver.getNrows());

			double[] rTable = solver.getPtrVariables();

			HashMap<String, Demand> dMap = new HashMap<String, Demand>();
			HashMap<String, Link> lMap = new HashMap<String, Link>();
			HashMap<String, Router> rMap = new HashMap<String, Router>();
			HashMap<String, Card> cMap = new HashMap<String, Card>();
			for (Router r : network.getRouters()) {
				rMap.put(r.getId(), r);
				for (Card c : r.getCards()) {
					cMap.put(r.getId() + "/" + c.getId(), c);
				}
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
			int cardCount = 0;
			double cardPower = 0;
			for (i = de + ek; i < de + ek + c; i++) {
				String colName = solver.getColName(i + 1);
				String cardName = colName.substring(1 + colName.indexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015) {
					cardCount++;
					cMap.get(cardName).setActive(true);
					cardPower = cardPower
							+ cMap.get(cardName).getBusy().getPower();
				} else
					cMap.get(cardName).setActive(false);
			}
			int routerCount = 0;
			double routerPower = 0;
			for (i = de + ek + c; i < de + ek + c + r; i++) {
				String colName = solver.getColName(i + 1);
				String routerName = colName.substring(1 + colName.indexOf("_"));
				double value = rTable[i];
				if (value > 1.0e-015) {
					routerCount++;
					rMap.get(routerName).setActive(true);
					routerPower = routerPower
							+ rMap.get(routerName).getBusy().getPower();
				} else
					rMap.get(routerName).setActive(false);
			}

			FileWriter fstream;
			fstream = new FileWriter("results/tex/" + name + "_relaxation.tex",
					false);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("\\documentclass[12pt]{article}");
			out.newLine();
			out.write("\\input{preambula_pakiety.tex}");
			out.newLine();
			out.write("\\input{preambula_ustawienia.tex}");
			out.newLine();
			out.write("\\begin{document}");
			out.newLine();

			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("results/tex/obrazki/" + name
						+ ".png"));
				double width = img.getWidth();
				double height = img.getHeight();
				img.flush();
				double pr = 15 / width;

				width = 15;
				height = pr * height;

				out.write("\\begin{figure}[!htb]" + "\\centering"
						+ "\\includegraphics[width=" + width + "cm, height="
						+ height + "cm]{obrazki/" + name + ".png}"
						+ "\\caption" + "{" + name.replaceAll("_", "\\\\_")
						+ "}" + "\\end{figure}");
				out.newLine();
				out.newLine();

			} catch (IOException e) {
				System.out.println("Brak pliku: results/tex/obrazki/" + name
						+ ".png");
				e.printStackTrace();
			}

			out.write("\\begin{table}[!htb] \\caption{MIP problem description.} \\begin{tabular}{|l|l|} \\hline");
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
			out.write("Calculation time & \\color{OliveGreen} " + time
					+ "ms \\\\");
			out.newLine();
			out.write("Calculation method & MIP relaxation \\\\");
			out.newLine();
			out.write("Number of variables: & " + solver.getNcolumns()
					+ " \\\\");
			out.newLine();
			out.write("Number of constraints: & " + solver.getNrows() + " \\\\");
			out.newLine();
			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();

			out.write("\\begin{table}[!htb] \\caption{Power costs and corresponding throughputs.} \\begin{tabular}{|l|l|} \\hline");
			out.newLine();
			HashSet<String> le = new HashSet<String>();

			double totalLinkPower = 0;
			double totalRouterPower = 0;
			double totalCardPower = 0;
			int totalRouterCounter = 0;
			int totalCardCounter = 0;
			int totalLinkCount = 0;

			double linkPower = 0;
			int linkCount = 0;

			for (Link l : network.getLinks()) {
				totalLinkPower = totalLinkPower
						+ l.getLevels().get(l.getLevels().size() - 1)
								.getPower();
				totalLinkCount++;
				for (EAS eas : l.getLevels()) {
					if (!le.contains(eas.getId())) {
						out.write("EAS of link $k$=" + eas.getId()
								+ " & throughput = " + eas.getBandwidth()
								+ network.gettUnit() + ", power = "
								+ eas.getPower() + network.getpUnit() + " \\\\");
						out.newLine();
						le.add(eas.getId());
					}
				}
			}

			for (Router r : network.getRouters()) {
				out.write("Router " + r.getId() + " & " + "power = "
						+ r.getBusy().getPower() + network.getpUnit()
						+ " (active state) \\\\");
				out.newLine();
				totalRouterCounter++;
				totalRouterPower = totalRouterPower + r.getBusy().getPower();
				for (Card c : r.getCards()) {
					totalCardCounter++;
					if (c.getBusy().getPower() > 0) {
						out.write("Card " + r.getId() + "/" + c.getId() + " & "
								+ "power = " + c.getBusy().getPower()
								+ network.getpUnit() + " (active state)  \\\\");
						out.newLine();
						totalCardPower = totalCardPower
								+ c.getBusy().getPower();
					}
				}
			}
			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
			out.write("\\begin{table}[!htb] \\caption{Energy states of links.} \\begin{tabular}{|l|l|l|l|} \\hline");
			out.newLine();
			out.write("Link (router/card) & Optimal energy state ($k$) & Throughput & Power \\\\\\hline");
			out.newLine();
			for (Link l : network.getLinks()) {
				if (l.getEas() != null) {
					out.write(l.getSource() + " $\\rightarrow$ " + l.getSink()
							+ " & \\color{OliveGreen} " + l.getEas().getId()
							+ " & \\color{OliveGreen} "
							+ l.getEas().getBandwidth() + network.gettUnit()
							+ " &  \\color{OliveGreen}" + l.getEas().getPower()
							+ network.getpUnit() + " \\\\");
					linkPower = linkPower + l.getEas().getPower();
					linkCount++;
					out.newLine();
				}
			}
			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
			out.write("\\begin{table}[!htb]  \\caption{Routers, cards and links used for data transmission. Reduction of energy consumption.} \\begin{tabular}{|l|l|} \\hline");
			out.newLine();
			out.write("Routers/cards/links & " + routerCount + "/" + cardCount
					+ "/" + linkCount + "\\\\\\hline");
			out.newLine();
			out.write("Power consumption & " + solver.getObjective()
					+ network.getpUnit() + "\\\\\\hline");
			out.newLine();
			out.write("Power reduction & "
					+ (totalRouterPower + totalCardPower + totalLinkPower - solver
							.getObjective()) + network.getpUnit()
					+ "\\\\\\hline");
			out.newLine();
			out.write("\\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();

			out.write("\\begin{table}[!htb] \\caption{Routers, cards and links used for data transmission. Optimized versus total energy consumption.} \\begin{tabular}{|l|l|l|l|l|} \\hline");
			out.newLine();
			out.write(" & Routers & Cards & Links & Sum \\\\\\hline");
			out.newLine();
			out.write("Optimized Counter & \\color{OliveGreen} " + routerCount
					+ " & \\color{OliveGreen} " + cardCount
					+ " & \\color{OliveGreen} " + linkCount + " & \\\\");
			out.newLine();
			out.write("Optimized Power & \\color{OliveGreen} " + routerPower
					+ network.getpUnit() + " & \\color{OliveGreen} "
					+ cardPower + network.getpUnit()
					+ " & \\color{OliveGreen} " + linkPower
					+ network.getpUnit() + " & \\color{OliveGreen} "
					+ solver.getObjective() + network.getpUnit()
					+ "\\\\\\hline");
			out.newLine();
			out.write("Total counter &  " + totalRouterCounter + " &  "
					+ totalCardCounter + " &  " + network.getLinks().size()
					+ " & \\\\");
			out.newLine();
			out.write("Total power &  " + totalRouterPower + network.getpUnit()
					+ " &  " + totalCardPower + network.getpUnit() + " &  "
					+ totalLinkPower + network.getpUnit() + " &  "
					+ (totalRouterPower + totalCardPower + totalLinkPower)
					+ network.getpUnit() + "\\\\");
			out.newLine();
			out.write("\\hline \\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();

			out.write("\\begin{table}[!htb] \\caption{MPLS routing} \\begin{tabular}{|l|l|l|p{10cm}|} \\hline");
			out.newLine();
			out.write("Source & Sink & Vol. & Realization of demands: routing \\\\\\hline");
			out.newLine();
			int line = 0;
			for (Demand d : network.getDemands()) {
				if(line == 25) {
					line=0;
					out.write("\\end{tabular} \\end{table}");
					out.newLine();
					out.write("\\begin{table}[!htb] \\caption{MPLS routing} \\begin{tabular}{|l|l|l|p{10cm}|} \\hline");
					out.newLine();
					out.write("Source & Sink & Vol. & Realization of demands: \\color{OliveGreen} Routing \\\\\\hline");
					out.newLine();					
				}
				out.write(d.getSource() + " & "
						+ d.getSink() + " & " + d.getVolume()
						+ network.gettUnit()
						+ " & \\tiny "
						+ d.getPath().getAsSortedString(d.getSource())
						+ " \\\\\\hline");
				out.newLine();
				line++;
			}
			out.write("\\end{tabular} \\end{table}");
			out.newLine();
			out.newLine();
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
