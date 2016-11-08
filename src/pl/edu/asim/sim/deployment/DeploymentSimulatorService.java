package pl.edu.asim.sim.deployment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import pl.edu.asim.ASimPlatform;
import pl.edu.asim.interfaces.ASimSimulatorInterface;
import pl.edu.asim.interfaces.SimulatorService;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.proc.TLProcess;
import pl.edu.asim.proc.TLTree;
import pl.edu.asim.service.mobility.Entity;
import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.MobileEntity;
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingArea;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.mobility.Team;
import pl.edu.asim.service.wpan.PathLoss;
import pl.edu.asim.service.wpan.WirelessChannel;
import pl.edu.asim.sim.ASimSVGManager;
import pl.edu.asim.sim.ASimSimulatorManager;
import pl.edu.asim.util.Matrix;
import pl.edu.asim.util.RandomTools;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class DeploymentSimulatorService implements SimulatorService {

	private ASimSimulatorManager manager;
	private ASimSVGManager svgManager;
	private JTSUtils jts;
	private GeometryFactory geometryFactory;

	@Override
	public SimulatorService newInstance() {
		// TODO Auto-generated method stub
		return new DeploymentSimulatorService();
	}

	public ASimSimulatorManager getManager() {
		return manager;
	}

	@Override
	public void setManager(ASimSimulatorInterface manager) {
		this.manager = (ASimSimulatorManager) manager;
		this.svgManager = this.manager.getSvgManager();
		jts = new JTSUtils();
		geometryFactory = new GeometryFactory();
	}

	@Override
	public void simulation(ASimDO simulator, String resultsDirectory) {
		String hs = RandomTools.getRandomString(4);
		TLTree tree = manager.getTree();
		System.out.println("Start simulation");
		double dt = 1;
		long time = 100;
		long calculationTime = System.currentTimeMillis();
		String bs_name = "Stacja bazowa";
		ArrayList<OperatingAreaPoint> roiPoints = new ArrayList<OperatingAreaPoint>();
		Polygon roiPolygon = null;

		try {
			String dir = resultsDirectory + "/" + simulator.getName() + "/";
			File resDir = new File(dir);
			resDir.mkdir();
			FileWriter fstream;
			fstream = new FileWriter(resultsDirectory + "/"
					+ simulator.getName() + "/table.tex", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("============================= Simulation: " + hs);
			out.newLine();

			// inicjalizacja obszaru roboczego
			OperatingArea area = new OperatingArea();
			for (ASimPO p : simulator.getProperties()) {
				if (p.getCode().equals("cell_size")) {
					area.setCellSize(new Double(p.getValue()));
					manager.getSvgManager().setCellSize(
							area.getCellSize().intValue());
				} else if (p.getCode().equals("svg_width")) {
					area.setWidth(new Double(p.getValue()));
				} else if (p.getCode().equals("svg_height")) {
					area.setHeight(new Double(p.getValue()));
				} else if (p.getCode().equals("dt")) {
					dt = new Double(p.getValue());
				} else if (p.getCode().equals("time")) {
					time = new Integer(p.getValue());
				} else if (p.getCode().equals("scale")) {
					area.setScale(new Double(p.getValue()));
				}
			}
			area.setPolygon(jts.getAsJTSPolygon(area, geometryFactory));

			// inicjalizacja modelu propagacji radiowej
			PathLoss pl = new PathLoss();
			for (ASimDO d : simulator.getChildren()) {
				if (d.getType().equals("PATHLOSS")) {
					for (ASimPO p : d.getProperties()) {
						if (p.getCode().equals("matrix_n")) {
							pl.setNMatrix(p.getValue());
						} else if (p.getCode().equals("matrix_s")) {
							pl.setSMatrix(p.getValue());
						} else if (p.getCode().equals("defaultN")) {
							pl.setDefaultN(new Double(p.getValue()));
						} else if (p.getCode().equals("defaultS")) {
							pl.setDefaultS(new Double(p.getValue()));
						}
					}
				} else if (d.getType().equals("ROI")) {
					org.w3c.dom.Document svgDocument = getSvgManager()
							.getSVGDocument(d, false);
					org.w3c.dom.NodeList nodeList = svgDocument
							.getElementsByTagName("polygon");

					for (int i = 0; i < nodeList.getLength(); i++) {
						org.w3c.dom.Node node = nodeList.item(i);
						String points = ((org.w3c.dom.Element) node)
								.getAttribute("points");
						String[] subpoints = points.split(" ");
						for (String point : subpoints) {
							String[] coordinate = point.split(",");
							OperatingAreaPoint oap = new OperatingAreaPoint(
									new Double(coordinate[0]), new Double(
											coordinate[1]));
							roiPoints.add(oap);
						}
					}
					if (roiPoints.size() > 0)
						roiPolygon = jts.getAsJTSPolygon(roiPoints,
								geometryFactory);

					for (ASimPO p : d.getProperties()) {
						if (p.getCode().equals("base_station_name")) {
							if (p.getValue() != null
									&& !p.getValue().equals(""))
								bs_name = p.getValue();
						}
					}
				}
			}
			WirelessChannel<BroadcastMessage> wlChannel = new WirelessChannel<BroadcastMessage>(
					pl, area.getCellSize());
			wlChannel.setScale(area.getScale());

			// inicjalizacja przeszkód
			for (ASimDO d : simulator.getChildren()) {
				if (d.getType().equals("OBSTACLES")) {
					for (ASimDO obstacle : d.getChildren()) {
						Obstacle o = new Obstacle();
						o.setSvgManager(getSvgManager());
						o.read(obstacle);
						if (o.isActive() && o.getPoints().size() > 1)
							area.getObstacles().add(o);
					}
				}
			}
			MobileEntity sb = null;

			// inicjalizacja węzłów sieci
			for (ASimDO d : simulator.getChildren()) {
				if (d.getType().equals("NODES")) {
					for (ASimDO entity : d.getChildren()) {
						if (entity.getType().equals("NODE")) {
							MobileEntity o = new MobileEntity();
							o.setSvgManager(getSvgManager());
							o.read(entity);
							if (o.isActive()) {
								area.addNode(o);
							} else
								continue;
							if (entity.getName().equals(bs_name)) {
								sb = o;
							}
						} else if (entity.getType().equals("TEAM")) {
							Team o = new Team();
							o.setSvgManager(getSvgManager());
							o.read(entity);
							if (o.isActive()) {
								area.getTeams().add(o);
							} else
								continue;
							for (Entity e : o.getNodes()) {
								if (e.getName().equals(bs_name)) {
									sb = (MobileEntity) e;
								}
							}
						}
					}
				}
			}

			// cele globalne
			for (ASimDO d : simulator.getChildren()) {
				if (d.getType().equals("GOALS")) {
					for (ASimDO entity : d.getChildren()) {
						if (entity.getType().equals("GOAL")) {
							Goal o = new Goal();
							o.setSvgManager(getSvgManager());
							o.read(entity);
							area.getGoals().add(o);
						}
						if (entity.getType().equals("TRIANGULAR_GRID")) {
							TriangularGridDeployment o = new TriangularGridDeployment();
							o.setSvgManager(getSvgManager());
							o.setGeometryFactory(new GeometryFactory());
							o.setJts(new JTSUtils());
							o.read(entity);
							area.getGoals().add(o);
						}
						if (entity.getType().equals("RANDOM_GRID")) {
							RandomDeployment rd = new RandomDeployment();
							rd.setSvgManager(getSvgManager());
							rd.setGeometryFactory(new GeometryFactory());
							rd.setJts(new JTSUtils());
							rd.read(entity);
							area.getGoals().add(rd);
						}
						if (entity.getType().equals("SELFORGANIZE_GRID")) {
							SelfOrganizeDeployment rd = new SelfOrganizeDeployment();
							rd.setSvgManager(getSvgManager());
							rd.setGeometryFactory(new GeometryFactory());
							rd.setJts(new JTSUtils());
							rd.read(entity);
							for (ASimPO p : entity.getProperties()) {
								if (p.getCode().equals("teamMatrix")) {
									rd.setTeamMatrix(new Matrix(p.getValue()));
								}
							}
							area.getGoals().add(rd);
						}
					}
				}
			}

			// cele globalne
			for (Goal g : area.getGoals()) {
				if (g.isActive() && g instanceof TriangularGridDeployment) {

					TriangularGridDeployment grid = (TriangularGridDeployment) g;
					ArrayList<Goal> goalList = grid.getGoalList(area
							.getObstacles());

					int iPoint = 0;
					for (Entity entity : area.getNodesList()) {
						if (goalList.size() <= iPoint)
							break;
						if (!entity.getName().equals(bs_name)) {
							Goal goal = goalList.get(iPoint);
							((MobileEntity) entity).getGoals().add(goal);
							iPoint++;
						}
					}
				} else if (g.isActive() && g instanceof RandomDeployment) {
					for (Entity entity : area.getNodesList()) {
						((MobileEntity) entity).getGoals().add(
								((RandomDeployment) g).getAsGoal(
										area.getObstacles(),
										((MobileEntity) entity).getBuffer()));
					}
				} else if (g.isActive()) {
					for (Entity entity : area.getNodesList()) {
						((MobileEntity) entity).getGoals().add(g.getAsGoal());
					}
				}
			}

			// uruchomienie symulacji
			// przygotowanie TLProcess

			ArrayList<TLProcess> services = new ArrayList<TLProcess>();

			for (Entity e : area.getNodesList()) {
				MobileEntity entity = (MobileEntity) e;
				DeploymentEntityService service = new DeploymentEntityService();
				service.setEntity(entity);
				service.setJts(new JTSUtils());
				service.setGeometryFactory(new GeometryFactory());
				service.setWlChannel(wlChannel);
				service.setDt(dt);
				service.setCellSize(area.getCellSize());
				service.setAreaHeight(area.getHeight());
				service.setAreaWidth(area.getWidth());
				manager.getLogger();
				Logger logger = Logger.getLogger(entity.getName());
				logger.addAppender(new FileAppender(
						new org.apache.log4j.PatternLayout(), "./log/"
								+ entity.getName() + ".txt"));
				service.setLogger(logger);

				services.add(service);

				wlChannel.register(
						entity.getId(),
						entity.getOrientationPoint().getAsVector3D()
								.add(entity.getSvgZeroPoint().getAsVector3D()),
						entity.getCard());
			}
			wlChannel.calculateConnections();

			double fullArea = (roiPolygon == null) ? area.getPolygon()
					.getArea() : roiPolygon.getArea();

			// ////////////////////////////////////////////////////////////////////////

			int f = 0;
			double k = 0;
			while (k <= time) {
				if (f == 20)
					f = 0;

				// przygotowanie aktualnej listy przeszkód
				ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
				for (Entity ent : area.getNodesList()) {
					if (ent.getPoints().size() > 1) {
						obstacles.add(ent.getAsObstacle());
					}
				}

				// przygotowanie aktualnej listy procesów do obsługi

				for (TLProcess process : services) {

					DeploymentEntityService service = (DeploymentEntityService) process;

					service.setTimestamp(k);
					service.updateParameters();

					MobileEntity entity = service.getEntity();
					// obstacles
					service.getObstacles().clear();
					service.getObstacles().addAll(obstacles);
					service.getObstacles().addAll(area.getObstacles());
					// goals
					service.getGoals().clear();
					for (Goal g : entity.getGoals()) {
						if (g.isActive() && g.getStart() <= k
								&& g.getStop() >= k) {
							service.getGoals().add(g);
						}
					}
				}

				// uruchomienie obsługi procesów
				tree.setProcesses(services);
				try {
					tree.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				wlChannel.calculateConnections();

				double averageND = 0.0;
				for (TLProcess process : services) {
					DeploymentEntityService service = (DeploymentEntityService) process;
					ArrayList<BroadcastMessage> messages = wlChannel
							.read(service.getEntity().getId());
					service.getInBuffer().clear();
					service.getInBuffer().addAll(messages);
					averageND = averageND + messages.size();
				}
				if (services.size() > 0)
					averageND = new BigDecimal(averageND / services.size())
							.setScale(2, BigDecimal.ROUND_HALF_DOWN)
							.doubleValue();

				double pathLenght = 0;
				Double energy = new Double("0");
				HashMap<String, Vector3D> entities = new HashMap<String, Vector3D>();
				HashMap<String, Double> ranges = new HashMap<String, Double>();
				HashMap<String, Double> sensingRanges = new HashMap<String, Double>();
				for (Entity entity : area.getNodesList()) {

					String points = "";
					for (int p = 1; p < entity.getPoints().size(); p++) {
						OperatingAreaPoint oap = entity.getPoints().get(p);
						points = points + oap.getX() + "," + oap.getY() + " ";
					}

					manager.getSvgManager().move(entity.getId(),
							entity.getSvgZeroPoint().getX(),
							entity.getSvgZeroPoint().getY(), k, dt, points,
							entity.getOldPointsString());

					pathLenght = pathLenght + entity.getPathLenght();
					energy = energy + entity.getAvgEnergy();
					entities.put(
							entity.getId(),
							entity.getOrientationPoint()
									.getAsVector3D()
									.add(entity.getSvgZeroPoint()
											.getAsVector3D()));
					ranges.put(entity.getId(), area.getScale()
							* entity.getCard().getOptimalWirelessDistance());
					sensingRanges.put(entity.getId(),
							((MobileEntity) entity).getSensingRange());

				}

				ResultsService rs = new ResultsService();
				rs.setManager(manager);
				rs.setJts(new JTSUtils());
				rs.setScale(area.getScale());
				rs.setGeometryFactory(new GeometryFactory());
				rs.setEntities(entities);
				rs.setOut(out);
				rs.setAverageND(averageND);
				rs.setRanges(ranges);
				rs.setSensingRanges(sensingRanges);
				rs.setTimestamp(k);
				rs.setPathLenght(pathLenght);
				rs.setEnergy(energy);
				if (sb != null)
					rs.setBsId(sb.getId());
				rs.setFullArea(fullArea);
				rs.setArea((roiPolygon == null) ? area.getPolygon()
						: roiPolygon);
				rs.setResultsDirectory(resultsDirectory);
				rs.setSimulatorName(simulator.getName());
				rs.setWlChannel(wlChannel);

				// zapis aktualnych wyników
				if (f == 0) {
					rs.setSave(true);
					// rs.setSaveSVG(true);
				}
				f++;

				try {
					rs.service();
				} catch (Exception e) {
					e.printStackTrace();
				}
				k = k + dt;
				wlChannel.refresh(k);
			}
			manager.getSvgManager().exportToImage(
					resultsDirectory + "/" + simulator.getName() + "/" + time
							+ "_" + hs + ".png");
			manager.getSvgManager().save(
					resultsDirectory + "/svg_model/" + simulator.getName());

			manager.getLogger().info(
					"Stop, simulation time="
							+ (System.currentTimeMillis() - calculationTime)
							+ " " + simulator.getName() + " " + hs);
			System.out
					.println("Stop, simulation time="
							+ (System.currentTimeMillis() - calculationTime
									+ " " + hs));
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ASimPlatform.commandLine();

	}

	public double getAngle(int p, int pp) {
		double a = 360 / (p * 6);
		return (a * pp);
	}

	public ASimSVGManager getSvgManager() {
		return svgManager;
	}

	public void setSvgManager(ASimSVGManager svgManager) {
		this.svgManager = svgManager;
	}

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	public void setGeometryFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
	}

	public JTSUtils getJts() {
		return jts;
	}

	public void setJts(JTSUtils jts) {
		this.jts = jts;
	}

}
