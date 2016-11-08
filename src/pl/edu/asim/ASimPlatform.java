package pl.edu.asim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import pl.edu.asim.interfaces.ASimModelerInterface;
import pl.edu.asim.interfaces.ASimSimulatorInterface;
import pl.edu.asim.model.DataManager;
import pl.edu.asim.proc.TLTree;
import pl.edu.asim.util.ASimTests;
import pl.edu.asim.util.RandomTools;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;

public class ASimPlatform {

	// ExecutorService executor;
	// ArrayList<Future<String>> outs;
	// ArrayList<String> names;
	TLTree tree;

	private static Bundle modeler_bundle = null;
	private static Bundle simulator_bundle = null;
	private static Felix m_felix = null;
	private static ASimModelerInterface modeler;

	Properties properties;

	Logger logger;

	public ASimPlatform() {

		try {
			logger = org.apache.log4j.Logger.getRootLogger();
			logger.info("ASim Platform: Start");

		} catch (Exception e) {
			e.printStackTrace();
		}

		DataManager.getInstance();
		properties = loadProperties();
		tree = new TLTree(new Integer("" + properties.get("asim.threadPool")));
		startFelix();

	}

	public Properties loadProperties() {
		Properties properties = new Properties();
		InputStream is;
		try {
			File f = new File("./conf/asim.properties");
			is = new FileInputStream(f);
			properties.load(is);
			return properties;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void startFelix() {
		Properties properties = new Properties();
		if (m_felix == null && properties != null)
			try {
				properties.put(FelixConstants.FRAMEWORK_BOOTDELEGATION,
						"*,javax.*,javax.naming.*");
				properties.put(FelixConstants.FRAMEWORK_BUNDLE_PARENT, "app");
				properties.setProperty(FelixConstants.FRAMEWORK_STORAGE_CLEAN,
						FelixConstants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
				m_felix = new Felix(properties);
				m_felix.start();

				this.loadAutoDeployBundles();
				this.startBundles();

				simulator_bundle = m_felix.getBundleContext().installBundle(
						"file:bundles/pl.edu.asim.sim.jar");
				simulator_bundle.start();

				modeler_bundle = m_felix.getBundleContext().installBundle(
						"file:bundles/pl.edu.asim.gui.jar");
				modeler_bundle.start();

				m_felix.getBundleContext().registerService(
						this.getClass().getName(), this, null);

				m_felix.getBundleContext().registerService(
						tree.getClass().getName(), tree, null);

			} catch (Exception ex) {
				System.err.println("Could not create framework: " + ex);
				ex.printStackTrace();
			}
	}

	public void runGUI() {
		try {
			modeler_bundle.update();
			ServiceReference<ASimModelerInterface> ref = modeler_bundle
					.getBundleContext().getServiceReference(
							ASimModelerInterface.class);
			modeler = m_felix.getBundleContext().getService(ref);
			modeler.call();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void commandLine() {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					System.out));
			bw.write("ASim: ");
			bw.flush();
		} catch (Exception e) {
			;
		}
	}

	public static void main(String[] args) {
		ASimPlatform aSimPlatform = new ASimPlatform();
		String command = "";
		boolean start = false;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			for (int i = 0; i < args.length; i++) {
				command = command + " " + args[i];
				start = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// command = command.toLowerCase().trim();
		command = command.trim();
		System.out.println("");

		while (!command.equals("exit") && !command.equals("Exit")) {
			try {
				System.out.print("ASim: ");
				if (!start) {
					command = br.readLine().trim();
					// command = br.readLine().toLowerCase().trim();
				}
				start = false;
				if (command.equals("modeler")) {
					System.gc();
					try {
						aSimPlatform.runGUI();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (command.startsWith("simulator -start")) {
					// String r = "";
					// try {
					// r = command.substring(17).trim();
					// aSimPlatform.runFederation(r);
					// } catch (Exception ex) {
					// ex.printStackTrace();
					// System.out.println("Wrong parameters ... " + r);
					// }
					// } else if (command.equals("applications list")) {
					// System.out.println(aSimPlatform.getAppsList());
					// } else if (command.startsWith("application stop")) {
					// String r = "";
					// try {
					// r = command.substring(17).trim();
					// Integer toRemove = new Integer(command.substring(17));
					// aSimPlatform.removeApps(toRemove.intValue());
					// } catch (Exception ex) {
					// System.out.println("Wrong Lp number... " + r);
					// }
				} else if (command.equals("update simulator")) {
					try {
						simulator_bundle.update();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (command.equals("update modeler")) {
					try {
						modeler_bundle.update();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (command.equals("gamma")) {
					try {

						NormalDistribution normal = new NormalDistribution();
						System.out.println(normal
								.inverseCumulativeProbability(0.01));
						System.out.println(normal
								.inverseCumulativeProbability(0.99));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (command.startsWith("solve")) {
					try {

						ServiceReference<ASimSimulatorInterface> ref = null;

						while (ref == null) {
							ref = simulator_bundle.getBundleContext()
									.getServiceReference(
											ASimSimulatorInterface.class);
						}
						ASimSimulatorInterface simulator = null;
						while (simulator == null) {
							simulator = simulator_bundle.getBundleContext()
									.getService(ref);
						}
						String temp = command.replaceAll("solve ", "");
						int sp = temp.indexOf(" ");
						String taskType = temp.substring(0, sp);
						String bean = temp.substring(sp + 1);

						simulator.econetTask(bean, taskType);

						// System.out.println(asimService);

						// ServiceReference[] srt = simulator_bundle
						// .getRegisteredServices();
						// ServiceReference[] srt2 = m_felix
						// .getRegisteredServices();
						// for (int i = 0; i < srt.length; i++) {
						// System.out.println(srt[i]);
						// }
						// for (int i = 0; i < srt2.length; i++) {
						// System.out.println(srt2[i]);
						// }
						//
						// ServiceReference ref = service_bundle
						// .getBundleContext().getServiceReference(
						// ASimServiceInterface.class.getName());
						//
						// ASimServiceInterface asimService =
						// (ASimServiceInterface) service_bundle
						// .getBundleContext().getService(ref);
						// System.gc();
						// asimService.test();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (command.equals("threads")) {
					int nbThreads = Thread.getAllStackTraces().keySet().size();

					ThreadGroup group;
					ThreadGroup parent;

					group = Thread.currentThread().getThreadGroup();
					while ((parent = group.getParent()) != null) {
						group = parent;
					}
					group.activeCount();

					System.out.println("Liczba wątków " + nbThreads + " "
							+ Thread.activeCount() + " " + group.activeCount());

				} else if (command.startsWith("opt")) {
					ASimTests test = new ASimTests();
					test.testFastD();
				} else if (command.startsWith("test4")) {
					try {

						String temp = command.replaceAll("test4 ", "");
						int K = new BigInteger(temp).intValue();

						double li = 0;
						double mi = 0;
						for (int l = 0; l < K; l++) {
							li = li + (K - l) / Math.pow(l + 1, 3);
						}
						for (int l = 0; l < K; l++) {
							mi = mi + (K - l) / Math.pow(l + 1, 2);
						}
						System.out.println(li / mi);
						System.out.println(mi / li);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.startsWith("test2")) {
					try {

						String temp = command.replaceAll("test2 ", "");
						int sp = temp.indexOf(" ");
						String nString = temp.substring(0, sp);
						String temp2 = temp.substring(sp + 1);
						sp = temp2.indexOf(" ");
						String rString = temp2.substring(0, sp);
						String zString = temp2.substring(sp + 1);

						int n = new BigInteger(nString).intValue();
						double z = new BigDecimal(zString).doubleValue();
						;
						double r = new BigDecimal(rString).doubleValue();

						RandomTools rt = new RandomTools();

						rt.test(r, z, n);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.startsWith("test3")) {

					try {

						String temp = command.replaceAll("test3 ", "");
						int sp = temp.indexOf(" ");
						String nString = temp.substring(0, sp);
						String temp2 = temp.substring(sp + 1);
						sp = temp2.indexOf(" ");
						String rString = temp2.substring(0, sp);
						String zString = temp2.substring(sp + 1);

						int n = new BigInteger(nString).intValue();
						double z = new BigDecimal(zString).doubleValue();
						;
						double r = new BigDecimal(rString).doubleValue();

						RandomTools rt = new RandomTools();

						rt.test2(r, z, n);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.startsWith("test")) {

					try {
						String temp = command.replaceAll("test ", "");
						int sp = temp.indexOf(" ");
						String nString = temp.substring(0, sp);
						String temp2 = temp.substring(sp + 1);
						sp = temp2.indexOf(" ");
						String rString = temp2.substring(0, sp);
						String zString = temp2.substring(sp + 1);

						int n = new BigInteger(nString).intValue();
						double z = new BigDecimal(zString).doubleValue();
						;
						double r = new BigDecimal(rString).doubleValue();

						double r_c = 2 * z
								* Math.sqrt((Math.log(n)) / (n * Math.PI));

						// System.out.println("r_c " + r_c);

						BigDecimal range = new BigDecimal(r);
						BigDecimal fullArea = new BigDecimal(z * z);
						BigDecimal BSArea = (range.pow(2)).multiply(
								new BigDecimal(Math.PI)).multiply(
								new BigDecimal(0.61));

						System.out.println("Powierzchnia kola: "
								+ BSArea.doubleValue());

						BigDecimal TempArea = new BigDecimal(1).subtract(BSArea
								.divide(fullArea, BigDecimal.ROUND_HALF_UP));

						BigDecimal area = fullArea.subtract(fullArea
								.multiply(TempArea.pow(n - 1)));
						BigDecimal oneArea = area.divide(new BigDecimal(n - 1),
								BigDecimal.ROUND_HALF_UP);

						System.out.println("Pokrycie=" + area.doubleValue()
								+ " pokrycie jednostkowe="
								+ oneArea.doubleValue());

						System.out.println("Pokrycie2="
								+ fullArea.subtract(BSArea).doubleValue()
								+ " pokrycie jednostkowe="
								+ fullArea
										.subtract(BSArea)
										.divide(new BigDecimal(n - 1),
												BigDecimal.ROUND_HALF_UP)
										.doubleValue());

						double l1 = Math.log(0.001);
						double l2 = Math.log(1 - BSArea.divide(fullArea,
								BigDecimal.ROUND_HALF_UP).doubleValue());
						BigDecimal count = new BigDecimal(1)
								.add(new BigDecimal(l1 / l2));

						// BigDecimal count2 = new BigDecimal(1)
						// .add(new BigDecimal(l1 / l2));

						System.out.println("Count " + count.doubleValue());
						System.out
								.println("Percent "
										+ area.divide(fullArea,
												BigDecimal.ROUND_HALF_UP)
												.doubleValue());

						System.out
								.println("--------------------- Prawdopodobienstwo, ze SB jest izolowana");
						double PC = BSArea.doubleValue() / (z * z);
						double PI = 1 - PC;
						// double P1_w1 = Math.pow(PI, n - 1);
						// System.out.println("ze wzoru w ksiazce Kumar");
						// System.out.println("P1_w1 =" + P1_w1);
						double P1_w3 = ArithmeticUtils.binomialCoefficient(
								n - 1, n - 1)
								* Math.pow(PI, n - 1)
								* Math.pow(PC, n - 1 - n + 1);

						System.out.println("ze wzoru bernoulliego");
						System.out.println("P1_w3 =" + P1_w3);

						System.out
								.println("--------------------- Prawdopodobienstwo, ze jeden inny niz SB jest izolowany");

						PC = (Math.PI * r * r * (0.61)) / (z * z);
						PI = 1 - PC;

						// double PC2 = (Math.PI * r * r * 0.273) / (z * z);
						// double PI2 = 1 - PC2;

						double PC2 = (Math.PI * r * r * 0.25) / (z * z);
						double PI2 = 1 - PC2;

						double PC3 = (Math.PI * r * r) / (z * z);
						double PI3 = 1 - PC3;

						double P1_w6 = PI * Math.pow(PI2, n - 2);
						double P1_w7 = PI * Math.pow(PI3, n - 2);

						l1 = Math.log(0.001 / PI);
						l2 = Math.log(PI2);

						System.out.println("n = " + (2 + l1 / l2));

						System.out.println("ze wzoru bernoulliego");
						System.out.println(" max=" + P1_w6 + " min=" + P1_w7);

						for (int k = n - 1; k >= 0; k--) {

							// BigDecimal networkArea = BSArea.add(oneArea
							// .multiply(new BigDecimal(n - 1 - k)));
							// BigDecimal networkArea = BSArea
							// .add(BSArea.multiply(new BigDecimal(
							// (n - 1 - k) * 0.3)));

							// PC = ((Math.PI * r * r * (0.612)) + (Math.PI * r
							// * r * (n - 1 - k) * (0.273)))
							// / (z * z);
							// PI = 1 - PC;
							//
							// P1_w3 = ArithmeticUtils.binomialCoefficient(n -
							// 1,
							// k)
							// * Math.pow(PI, k)
							// * Math.pow(PC, n - 1 - k);

							// System.out.println(k
							// + " izolowane, ze wzoru bernoulliego "
							// + (0.612));
							// System.out.println("P1_w1 =" + P1_w3);

						}

						System.out
								.println("--------------------- Prawdopodobienstwo, ze conajmniej jeden jest izolowany");
						double P1_w1 = P1_w3 * n;
						System.out.println("P1_w1 =" + P1_w1);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.equals("exit")) {
					aSimPlatform.exit();
				} else if (command.equals("")) {

				} else {
					System.out.println("Command not recognized");
				}
			} catch (Exception ioe) {
				System.out.println("IO error trying to read command!");
				System.exit(1);
			}
		}

	}

	public long getPathCount(int n, int p) {
		long result = 0;
		result = ArithmeticUtils.factorial(n - 2)
				/ ArithmeticUtils.factorial(n - p - 1);
		return result;
	}

	// public ArrayList<Future<String>> getOuts() {
	// return outs;
	// }
	//
	// public void setOuts(ArrayList<Future<String>> outs) {
	// this.outs = outs;
	// }
	//
	// public ArrayList<String> getNames() {
	// return names;
	// }
	//
	// public void setNames(ArrayList<String> names) {
	// this.names = names;
	// }

	public void loadAutoDeployBundles() throws Exception {
		File dir = new File("autodeploy");
		String[] children = dir.list();
		if (children == null) {
		} else {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				if (filename.endsWith(".jar")) {
					m_felix.getBundleContext().installBundle(
							"file:autodeploy/" + filename);
				}
			}
		}
	}

	public void startBundles() throws Exception {
		Bundle[] bundles = m_felix.getBundleContext().getBundles();
		if (bundles != null) {
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];
				if (bundle.getHeaders().get("Fragment-Host") == null)
					bundle.start();
			}
		}
	}

	private void exit() {
		try {
			logger.info(" ------------------------------------------------ ASim: Close");
			if (modeler != null)
				modeler.close();
			m_felix.stop();
			m_felix.waitForStop(0);
			DataManager.getInstance().close();
			logger.info(" ------------------------------------------------ ASim: Stop");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}

// co=46526.0 is=53474.0 percent=0.46526
// 0.46526 0.14895 0.08435 0.05324 0.0369 0.02715 0.01991 0.01628 0.0141
// 0.01222 0.01139 0.01024 0.00911 0.00923 0.00883 0.0098 0.00985 0.01149
// 0.01201 0.01231 0.01043 0.00695
