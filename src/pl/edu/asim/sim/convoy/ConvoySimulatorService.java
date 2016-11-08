package pl.edu.asim.sim.convoy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

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
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingArea;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.wpan.PathLoss;
import pl.edu.asim.service.wpan.WirelessChannel;
import pl.edu.asim.sim.ASimSVGManager;
import pl.edu.asim.sim.ASimSimulatorManager;
import pl.edu.asim.sim.deployment.BroadcastMessage;

import com.vividsolutions.jts.geom.GeometryFactory;

public class ConvoySimulatorService implements SimulatorService {

	private ASimSimulatorManager manager;
	private ASimSVGManager svgManager;

	@Override
	public SimulatorService newInstance() {
		return new ConvoySimulatorService();
	}

	public ASimSimulatorManager getManager() {
		return manager;
	}

	@Override
	public void setManager(ASimSimulatorInterface manager) {
		this.manager = (ASimSimulatorManager) manager;
		this.svgManager = this.manager.getSvgManager();
	}

	@Override
	public void simulation(ASimDO simulator, String resultsDirectory) {
		TLTree tree = manager.getTree();
		long calculationTime = System.currentTimeMillis();
		System.out.println("Start: simulation");
		double dt = 1;
		long time = 100;

		try {

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
			area.setPolygon(new JTSUtils().getAsJTSPolygon(area,
					new GeometryFactory()));

			PathLoss pl = new PathLoss();
			// inicjalizacja modelu propagacji radiowej
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
						o.setSvgManager(svgManager);
						o.read(obstacle);
						if (o.isActive() && o.getPoints().size() > 1)
							area.getObstacles().add(o);
					}
				}
			}

			// inicjalizacja pojazdów
			for (ASimDO d : simulator.getChildren()) {
				if (d.getType().equals("VEHICLES")) {
					for (ASimDO entity : d.getChildren()) {
						Vehicle o = new Vehicle();
						o.setSvgManager(manager.getSvgManager());
						o.read(entity);
						if (o.isActive()) {
							area.addNode(o);
						}
					}
				}
			}

			// uruchomienie symulacji
			// przygotowanie TLProcess
			ArrayList<TLProcess> services = new ArrayList<TLProcess>();

			for (Entity e : area.getNodesList()) {
				Vehicle entity = (Vehicle) e;
				VehicleService service = new VehicleService();
				service.setEntity(entity);
				service.setJts(new JTSUtils());
				service.setGeometryFactory(new GeometryFactory());
				service.setPl(pl);
				service.setDt(dt);
				service.setWlChannel(wlChannel);
				service.setCellSize(area.getCellSize());
				service.setAreaHeight(area.getHeight());
				service.setAreaWidth(area.getWidth());
				services.add(service);
				wlChannel.register(
						entity.getId(),
						entity.getOrientationPoint().getAsVector3D()
								.add(entity.getSvgZeroPoint().getAsVector3D()),
						entity.getCard());
			}

			wlChannel.calculateConnections();
			int f = 0;
			double k = 0;
			while (k <= time) {
				if (f == 10)
					f = 0;
				// zapis do pliku png
				if (f == 0) {
					manager.getSvgManager().exportToImage(
							resultsDirectory + "/svg_model/"
									+ simulator.getName() + "_" + k + ".png");
					String result = "\t k=" + k;
					for (Entity e : area.getNodesList()) {
						Vehicle ent = (Vehicle) e;
						if (ent.getPoints().size() > 1) {
							Vector3D v = e
									.getSvgZeroPoint()
									.getAsVector3D()
									.add(e.getOrientationPoint()
											.getAsVector3D());
							BigDecimal v1 = new BigDecimal(v.getX()).setScale(
									2, RoundingMode.HALF_UP);
							BigDecimal v2 = new BigDecimal(v.getY()).setScale(
									2, RoundingMode.HALF_UP);
							result = result + "\t " + v1 + "," + v2;
						}
					}
					manager.getLogger().info(result);
				}

				// przygotowanie aktualnej listy przeszkód
				ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
				for (Entity e : area.getNodesList()) {
					Vehicle ent = (Vehicle) e;
					if (ent.getPoints().size() > 1) {
						obstacles.add(ent.getAsObstacle());
					}
				}

				// przygotowanie aktualnej listy procesów do obsługi
				for (TLProcess process : services) {
					VehicleService service = (VehicleService) process;
					service.getObstacles().clear();
					service.getObstacles().addAll(obstacles);
					service.getObstacles().addAll(area.getObstacles());
					service.getGoals().clear();
					for (Goal g : service.getEntity().getGoals()) {
						if (g.isActive())
							service.getGoals().add(g);
					}
					for (Entity e2 : area.getNodesList()) {
						Vehicle ent = (Vehicle) e2;
						if (!ent.getId().equals(service.getEntity().getId())) {
							if (ent.isActive())
								service.getGoals().add(ent.getAsGoal());
						}
					}

				}

				// uruchomienie obsługi procesów
				Double energy = new Double("0");
				tree.setProcesses(services);
				try {
					tree.start();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (TLProcess process : services) {
					VehicleService service = (VehicleService) process;
					ArrayList<BroadcastMessage> messages = wlChannel
							.read(service.getEntity().getId());
					service.getInBuffer().clear();
					service.getInBuffer().addAll(messages);
				}

				// aktualizacja położenia węzłów mobilnych
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
					manager.getSvgManager().updatePathForEntity(entity, k, dt);
				}

				// aktualizacja topologii sieci
				manager.getSvgManager().updatePath(area.getNodesList(), k, dt);

				// zapis aktualnych wyników
				if (f == 0)
					manager.results(k, area.getNodesList(), 300.0, energy);
				f++;
				System.out.println("------------------------------ k=" + k);
				k = k + dt;
			}
			manager.getSvgManager().exportToImage(
					resultsDirectory + "/svg_model/" + simulator.getName()
							+ "_" + time + ".png");
			manager.getSvgManager().save(
					resultsDirectory + "/svg_model/" + simulator.getName());
			System.out.println("Stop, simulation time="
					+ (System.currentTimeMillis() - calculationTime));
			ASimPlatform.commandLine();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ASimSVGManager getSvgManager() {
		return svgManager;
	}

	public void setSvgManager(ASimSVGManager svgManager) {
		this.svgManager = svgManager;
	}
}
