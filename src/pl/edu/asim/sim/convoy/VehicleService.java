package pl.edu.asim.sim.convoy;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.interfaces.Service;
import pl.edu.asim.proc.TLProcess;
import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.optim.BondTask;
import pl.edu.asim.service.optim.LocalTaskCRRI;
import pl.edu.asim.service.optim.PotentialSource;
import pl.edu.asim.service.wpan.PathLoss;
import pl.edu.asim.service.wpan.WirelessChannel;
import pl.edu.asim.sim.deployment.BroadcastMessage;

import com.vividsolutions.jts.geom.GeometryFactory;

public class VehicleService extends TLProcess implements Service {

	private Vehicle entity;
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private ArrayList<Goal> goals = new ArrayList<Goal>();
	private Double avgEnergy = new Double("0");
	private PathLoss pl;
	private Double cellSize;
	private Double areaWidth;
	private Double areaHeight;
	private GeometryFactory geometryFactory;
	private JTSUtils jts;
	protected WirelessChannel<BroadcastMessage> wlChannel;
	ArrayList<BroadcastMessage> inBuffer = new ArrayList<BroadcastMessage>();

	public void setEntity(Vehicle entity) {
		this.entity = entity;
	}

	public Vehicle getEntity() {
		return entity;
	}

	@Override
	public Integer call() throws Exception {
		try {
			// LocalTaskCRR task = entity.getAsOptimTask(getDt());
			LocalTaskCRRI task = entity.getAsOptimTask(getDt(),
					entity.getMaxVelocity());
			task.setPrecision(entity.getPrecision());
			task.setLogger(this.getLogger());

			String goalList = "";
			for (Goal g : goals) {
				for (int i = 0; i < entity.getConvoyMatrix().getRowCount(); i++) {
					String gName = entity.getConvoyMatrix().getAsString(i, 0);
					if (gName.equals(g.getName())) {

						goalList = goalList + g.getName();
						String dString = entity.getConvoyMatrix().getAsString(
								i, 1);
						double d;
						if (dString.equals("WPAN")) {
							d = entity.getCard().getOptimalWirelessDistance();
						} else {
							d = (entity.getConvoyMatrix().getAsDouble(i, 1));
						}

						double mass = (entity.getConvoyMatrix().getAsDouble(i,
								2));
						double range = (entity.getConvoyMatrix().getAsDouble(i,
								3));
						Vector3D il = g.getOrientationPoint().getAsVector3D()
								.add(g.getSvgZeroPoint().getAsVector3D());
						PotentialSource ps = new PotentialSource(mass, d,
								range, 0.0, il);
						task.getInfluencePoints().add(ps);
					}
				}
			}

			OperatingAreaPoint oldLocation = new OperatingAreaPoint(entity
					.getSvgZeroPoint().getX()
					+ entity.getOrientationPoint().getX(), entity
					.getSvgZeroPoint().getY()
					+ entity.getOrientationPoint().getY());

			for (Iterator<PotentialSource> it = task.getInfluencePoints()
					.iterator(); it.hasNext();) {
				PotentialSource ps = it.next();
				ps.calculateVirtualInfluenceLocation(oldLocation
						.getAsVector3D());
			}

			task.setLocation(oldLocation.getAsVector3D());
			task.calculateWithInterval();

			OperatingAreaPoint newLocation = new OperatingAreaPoint(
					task.getLocation());

			newLocation = jts.collisionFree(oldLocation, newLocation,
					geometryFactory, 1, areaWidth, areaHeight, this, true);

			Vector3D vector = new Vector3D(newLocation.getX()
					- oldLocation.getX(), newLocation.getY()
					- oldLocation.getY(), 0);
			double v = vector.getNorm() / getDt();

			if (v > 0) {
				double freeDistance = jts.getFreeDistance(entity.getId(),
						obstacles, geometryFactory, oldLocation, newLocation,
						entity.getRangeOfView());
				if (freeDistance < entity.getMaxBrakingDistance()) {
					double v2 = (entity.getMaxVelocity() * freeDistance)
							/ entity.getMaxBrakingDistance()
							+ entity.getMinVelocity();
					if (v2 < v) {
						vector = vector.scalarMultiply(v2 / v);
						v = v2;
						newLocation = new OperatingAreaPoint(oldLocation.getX()
								+ vector.getX(), oldLocation.getY()
								+ vector.getY());
					}
				}
			}

			setAvgEnergy(task.getEnergy(newLocation.getAsVector3D()));

			ArrayList<Vector3D> influencePoints = new ArrayList<Vector3D>();
			influencePoints.add(newLocation.getAsVector3D());
			for (int p = 1; p < entity.getPoints().size(); p++) {
				Vector3D point = entity.getPoints().get(p).getAsVector3D()
						.add(entity.getSvgZeroPoint().getAsVector3D());
				influencePoints.add(point);
			}

			ArrayList<BondTask> bTasks = new ArrayList<BondTask>(); // pomijamy
																	// punkt
																	// orientacji
																	// -
																	// pierwszy
			for (int p = 1; p < entity.getPoints().size(); p++) {
				BondTask bt = new BondTask(p, influencePoints, entity
						.getBonds().get(p));
				bTasks.add(bt);
			}

			for (int i = 0; i < 3000; i++) { // wyznaczenie nowych położeń
				boolean stop = true;
				for (BondTask bt : bTasks) {
					boolean run = bt.tick();
					stop = stop && !(run);
				}
				for (BondTask bt : bTasks) {
					influencePoints.set(bt.getIndex(), bt.getLocation());
				}
				for (BondTask bt : bTasks) {
					bt.setInfluencePoints(influencePoints);
				}
				if (stop) {
					break;
				}
			}

			BondTask bt = new BondTask(0, influencePoints, entity.getBonds()
					.get(0));
			for (int i = 0; i < 500; i++) { // wyznaczenie nowych położeń
				boolean stop = true;

				boolean run = bt.tick();
				stop = stop && !(run);

				influencePoints.set(bt.getIndex(), bt.getLocation());

				bt.setInfluencePoints(influencePoints);

				if (stop) {
					break;
				}
			}

			// int column = (int) (newLocation.getX() / cellSize);
			// int row = (int) (newLocation.getY() / cellSize);
			// double N = pl.getN(column, row);
			// double S = pl.getS(column, row);
			// entity.getCard().setOptimalWirelessDistance(
			// entity.getCard().getPropagationRange(N, S, 0.99)
			// .doubleValue());
			// entity.getCard().setMaxCcaWirelessDistance(
			// entity.getCard().getMaxCcaPropagationRange(N, S)
			// // 10 * entity.getCard().getMaxCcaPropagationRange(N, S)
			// .doubleValue());

			// przesuniecie węzla mobilnego
			entity.setSvgZeroPoint(new OperatingAreaPoint(newLocation
					.getAsVector3D().subtract(
							entity.getOrientationPoint().getAsVector3D())));
			System.out.println(entity.getName() + " "
					+ entity.getSvgZeroPoint().toString() + " " + goalList
					+ " " + v);

			String oldPoints = "";
			for (int p = 1; p < entity.getPoints().size(); p++) {
				OperatingAreaPoint oap = entity.getPoints().get(p);
				oldPoints = oldPoints + oap.getX() + "," + oap.getY() + " ";
			}
			entity.setOldPointsString(oldPoints);

			for (int p = 1; p < influencePoints.size(); p++) {
				Vector3D point = influencePoints.get(p).subtract(
						entity.getSvgZeroPoint().getAsVector3D());
				entity.getPoints().set(p, new OperatingAreaPoint(point));
			}

			this.getWlChannel().register(
					entity.getId(),
					entity.getOrientationPoint().getAsVector3D()
							.add(entity.getSvgZeroPoint().getAsVector3D()),
					entity.getCard());
			BroadcastMessage data = new BroadcastMessage();
			data.setId(entity.getId());
			data.setLocation(entity.getOrientationPoint().getAsVector3D()
					.add(entity.getSvgZeroPoint().getAsVector3D()));
			data.setName(entity.getName());
			data.setTimestamp(this.getTimestamp());
			data.setPt(entity.getCard().getPt());
			data.setDegree(this.inBuffer.size());
			data.setShift(newLocation.getAsVector3D().subtract(
					oldLocation.getAsVector3D()));
			Vector3D motion = new Vector3D(newLocation.getX()
					- oldLocation.getX(), newLocation.getY()
					- oldLocation.getY(), 0);
			v = motion.getNorm() / getDt();
			data.setVelocity(v);
			this.getWlChannel().send(entity.getId(), null, data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Integer(0);
	}

	@Override
	public Integer service() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer synchronization() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Obstacle> getObstacles() {
		return obstacles;
	}

	public void setObstacles(ArrayList<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	public ArrayList<Goal> getGoals() {
		return goals;
	}

	public void setGoals(ArrayList<Goal> goals) {
		this.goals = goals;
	}

	// public ASimSimulatorManager getManager() {
	// return manager;
	// }
	//
	// public void setManager(ASimSimulatorManager manager) {
	// this.manager = manager;
	// }

	public Double getAvgEnergy() {
		return avgEnergy;
	}

	public void setAvgEnergy(Double avgEnergy) {
		this.avgEnergy = avgEnergy;
	}

	public void setPl(PathLoss pl) {
		this.pl = pl;
	}

	public PathLoss getPl() {
		return pl;
	}

	public void setCellSize(Double double1) {
		this.cellSize = double1;
	}

	public Double getCellSize() {
		return cellSize;
	}

	public Double getAreaWidth() {
		return areaWidth;
	}

	public void setAreaWidth(Double areaWidth) {
		this.areaWidth = areaWidth;
	}

	public Double getAreaHeight() {
		return areaHeight;
	}

	public void setAreaHeight(Double areaHeight) {
		this.areaHeight = areaHeight;
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

	public WirelessChannel<BroadcastMessage> getWlChannel() {
		return wlChannel;
	}

	public void setWlChannel(WirelessChannel<BroadcastMessage> wlChannel) {
		this.wlChannel = wlChannel;
	}

	public ArrayList<BroadcastMessage> getInBuffer() {
		return inBuffer;
	}

	public void setInBuffer(ArrayList<BroadcastMessage> inBuffer) {
		this.inBuffer = inBuffer;
	}

}
