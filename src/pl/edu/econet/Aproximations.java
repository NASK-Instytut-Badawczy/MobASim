package pl.edu.econet;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import pl.edu.econet.energy.EAS;
import pl.edu.econet.energy.EASRow;
import pl.edu.econet.topology.Link;

public class Aproximations {

	public static boolean testResults(LpSolve solver) {
		boolean result = true;
		try {
			double[] rTable = solver.getPtrVariables();
			for (int i = 0; i < rTable.length; i++) {
				if (rTable[i] > 1.0e-015 && rTable[i] < (1.0 - 1.0e-015))
					result = false;
			}
		} catch (LpSolveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static int upU(LpSolve solver, int iteration, String bean, int de) {
		try {
			boolean find = true;
			while (find) {
				iteration++;
				solver.setOutputfile("results/" + bean + "/r" + iteration
						+ ".txt");
				int wynik = solver.solve();
				solver.printSolution(1);
				solver.writeLp("results/" + bean + "/model_r" + iteration
						+ ".txt");
				// System.out
				// .println("iteration +++++++++++++++++++++++++++++++++ "
				// + iteration + " " + wynik);
				if (wynik > 0) {
					System.out.println("!!! Brak rozwiazania ");
					return -1;
				}

				find = false;
				double[] rTable = solver.getPtrVariables();

				String columnName = "";
				int column = -1;
				double max = -1;
				double min = -1;
				String columnNameMin = "";
				int columnMin = -1;

				for (int index = 0; index < de; index++) {
					if (rTable[index] > 1.0e-012 && rTable[index] < 0.999999) {
						String name = solver.getColName(index + 1);
						String demandName = name.substring(
								1 + name.indexOf("_"), name.lastIndexOf("_"));
						String vI = name.substring(1 + name.lastIndexOf("_"));
						if (rTable[index] >= 0.5
								&& (max == -1 || max < rTable[index])) {
							max = rTable[index];
							columnName = demandName;
							column = index + 1;
							find = true;
						}
						if (rTable[index] < 0.5
								&& (min == -1 || min > rTable[index])) {
							min = rTable[index];
							columnNameMin = demandName;
							columnMin = index + 1;
						}
					}
				}

				if (!columnName.equals("")) {
					find = true;
					// System.out.println("add " + columnName + " " + max);
					solver.setLowbo(column, 1);
				}
				if (!columnNameMin.equals("")) {
					find = true;
					// System.out.println("add " + columnNameMin + " " + min);
					solver.setUpbo(columnMin, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iteration;
	}

	public static int upYMin(LpSolve solver, int iteration, String bean,
			int de, int ek, Network network) {
		try {
			double rTable[] = null;

			boolean find = true;
			while (find) {
				iteration++;
				solver.setOutputfile("results/" + bean + "/r" + iteration
						+ ".txt");
				int wynik = solver.solve();
				solver.printSolution(1);
				solver.writeLp("results/" + bean + "/model_r" + iteration
						+ ".txt");
				// System.out
				// .println("iteration +++++++++++++++++++++++++++++++++ "
				// + iteration + " " + wynik);
				if (wynik > 0) {
					System.out.println("!!! Brak rozwiazania ");
					return -1;
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
					// System.out.println("add " + columnName + " " + max
					// + " level=" + row2.getFirstNonIntegerIndex());
					solver.setLowbo(column, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iteration;
	}

	public static int upMin(LpSolve solver, int iteration, String bean, int de,
			int ek, Network network) {
		try {
			boolean find = true;
			long time = System.currentTimeMillis();
			while (find) {
				iteration++;
				solver.setOutputfile("results/" + bean + "/r" + iteration
						+ ".txt");
				int wynik = solver.solve();
				solver.printSolution(1);
				solver.writeLp("results/" + bean + "/model_r" + iteration
						+ ".txt");
				System.out
						.println("iteration +++++++++++++++++++++++++++++++++ "
								+ iteration + " " + wynik);
				if (wynik > 0) {
					System.out.println("!!! Brak rozwiazania ");
					return -1;
				}

				find = false;
				double rTable[] = solver.getPtrVariables();

				String columnName = "";
				int column = 0;
				double max = -1;
				int level = -1;

				for (int index = de; index < de + ek; index++) {
					if (rTable[index] > 1.0e-015 && rTable[index] < 0.999999) {
						String name = solver.getColName(index + 1);
						String linkName = name.substring(1 + name.indexOf("_"),
								name.lastIndexOf("_"));
						String vI = name.substring(1 + name.lastIndexOf("_"));
						for (Link link : network.getLinks()) {
							if (link.getId().equals(linkName)) {
								int tmpLevel = 0;
								for (EAS eas : link.getLevels()) {
									if (eas.getId().equals(vI)
											&& (level == -1
													|| (level == tmpLevel && rTable[index] < max) || level > tmpLevel)) {
										columnName = name;
										column = index + 1;
										max = rTable[index];
										level = tmpLevel;
									}
									tmpLevel++;
								}
							}
						}
					}
				}

				if (!columnName.equals("")) {
					find = true;
					System.out.println("add " + columnName + " " + max
							+ " level=" + level);
					solver.setLowbo(column, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iteration;
	}

	public static int lowYMax(LpSolve solver, int iteration, String bean,
			int de, int ek, Network network) {
		try {
			double rTable[] = null;

			boolean find = true;
			while (find) {
				iteration++;
				solver.setOutputfile("results/" + bean + "/r" + iteration
						+ ".txt");
				int wynik = solver.solve();
				solver.printSolution(1);
				solver.writeLp("results/" + bean + "/model_r" + iteration
						+ ".txt");
				System.out
						.println("iteration +++++++++++++++++++++++++++++++++ "
								+ iteration + " " + wynik);
				if (wynik > 0) {
					System.out.println("!!! Brak rozwiazania ");
					return -1;
				}

				find = false;
				rTable = solver.getPtrVariables();

				String columnNameFirst = "";
				String columnNameLast = "";
				int columnFirst = -1;
				int columnLast = -1;

				double max = 0;
				double min = 0;
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
					if (row.getUnallocation() > 0
							&& (min == 0 || row.getUnallocation() < min)) {
						min = row.getUnallocation();
						columnLast = row.getIndexes().get(
								row.getLastNonIntegerId()) + 1;
						columnNameLast = "y_" + link.getId() + "_"
								+ row.getLastNonIntegerId();
						columnFirst = row.getIndexes().get(
								row.getFirstNonIntegerId()) + 1;
						columnNameFirst = "y_" + link.getId() + "_"
								+ row.getFirstNonIntegerId();
						row2 = row;
					}
					// if (row.getUnallocation() > max) {
					// max = row.getUnallocation();
					// columnLast = row.getIndexes().get(
					// row.getLastNonIntegerId()) + 1;
					// columnNameLast = "y_" + link.getId() + "_"
					// + row.getLastNonIntegerId();
					// columnFirst = row.getIndexes().get(
					// row.getFirstNonIntegerId()) + 1;
					// columnNameFirst = "y_" + link.getId() + "_"
					// + row.getFirstNonIntegerId();
					// row2 = row;
					// }
				}

				if (columnLast > 0) {
					find = true;
					// if(max>1) {
					// solver.setUpbo(columnLast, 0);
					// System.out.println("add last " + columnNameLast + " " +
					// max
					// + " level=" + row2.getLastNonIntegerIndex());
					// } else {
					solver.setLowbo(columnFirst, 1);
					System.out.println("add first " + columnNameFirst + " "
							+ max + " level=" + row2.getFirstNonIntegerIndex());
					// }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iteration;
	}

}
