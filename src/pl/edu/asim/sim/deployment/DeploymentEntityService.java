package pl.edu.asim.sim.deployment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.interfaces.Service;
import pl.edu.asim.proc.TLProcess;
import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.MobileEntity;
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.optim.BondTask;
import pl.edu.asim.service.optim.LocalTaskCRRI;
import pl.edu.asim.service.optim.PotentialSource;
import pl.edu.asim.service.wpan.WirelessChannel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class DeploymentEntityService extends TLProcess implements Service {

	protected MobileEntity entity;
	protected ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	protected ArrayList<Goal> goals = new ArrayList<Goal>();
	protected WirelessChannel<BroadcastMessage> wlChannel;
	protected Double cellSize;
	protected Double areaWidth;
	protected Double areaHeight;
	protected boolean adap = true;

	double velocity = 0;
	private GeometryFactory geometryFactory;
	private JTSUtils jts;

	ArrayList<Goal> explorationList = new ArrayList<Goal>();

	ArrayList<BroadcastMessage> inBuffer = new ArrayList<BroadcastMessage>();

	public ArrayList<BroadcastMessage> messageFilter(double distance,
			Vector3D location) {
		ArrayList<BroadcastMessage> result = new ArrayList<BroadcastMessage>();
		for (BroadcastMessage m : inBuffer) {
			double d = m.getLocation().distance(location);
			if (d < distance) {
				result.add(m);
			}
		}
		return result;
	}

	public Coordinate collisionFree(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, double multiply) {

		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation.getAsJTSCoordinate();

		LineSegment lso = new LineSegment(lsc[0], lsc[1]);
		// double lenght = lso.getLength();
		LineSegment eS = new LineSegment(new Coordinate(entity.getBuffer(),
				entity.getBuffer()), new Coordinate(entity.getBuffer(),
				areaHeight - entity.getBuffer()));
		LineSegment wS = new LineSegment(new Coordinate(areaWidth
				- entity.getBuffer(), entity.getBuffer()),
				new Coordinate(areaWidth - entity.getBuffer(), areaHeight
						- entity.getBuffer()));
		LineSegment nS = new LineSegment(new Coordinate(entity.getBuffer(),
				entity.getBuffer()), new Coordinate(areaWidth
				- entity.getBuffer(), entity.getBuffer()));
		LineSegment sS = new LineSegment(new Coordinate(entity.getBuffer(),
				areaHeight - entity.getBuffer()), new Coordinate(areaWidth
				- entity.getBuffer(), areaHeight - entity.getBuffer()));

		ArrayList<LineSegment> obstacleString = new ArrayList<LineSegment>();
		ArrayList<Coordinate> intersectionPoints = new ArrayList<Coordinate>();

		for (Obstacle o : getObstacles()) {
			if (o.getId().equals(getEntity().getId()))
				continue;
			Polygon p = getJts().getAsJTSPolygon(o, getGeometryFactory());
			Geometry ge = p.buffer(getEntity().getBuffer(), 1,
					BufferOp.CAP_SQUARE);
			if (!(ge instanceof Polygon))
				continue;
			p = (Polygon) ge;

			LineString lineS = getJts().getExteriorRing(p);

			for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
				Point p1 = lineS.getPointN(i);
				Point p2 = lineS.getPointN(i + 1);

				LineSegment lSegment = new LineSegment(p1.getCoordinate(),
						p2.getCoordinate());
				Coordinate inscCoordinate = lso.intersection(lSegment);
				Coordinate closCoordinate = lSegment.closestPoint(lso.p0);

				if (inscCoordinate != null) {
					obstacleString.add(lSegment);
					intersectionPoints.add(closCoordinate);
				}
			}
		}

		Coordinate wCoordinate = lso.intersection(wS);
		Coordinate eCoordinate = lso.intersection(eS);
		Coordinate nCoordinate = lso.intersection(nS);
		Coordinate sCoordinate = lso.intersection(sS);

		if (wCoordinate != null) {
			obstacleString.add(wS);
			intersectionPoints.add(wCoordinate);
		}
		if (eCoordinate != null) {
			obstacleString.add(eS);
			intersectionPoints.add(eCoordinate);
		}
		if (nCoordinate != null) {
			obstacleString.add(nS);
			intersectionPoints.add(nCoordinate);
		}
		if (sCoordinate != null) {
			obstacleString.add(sS);
			intersectionPoints.add(sCoordinate);
		}

		double dS = -1;
		int iS = -1;
		int i = 0;
		for (Coordinate point : intersectionPoints) {
			double d = point.distance(oldLocation.getAsJTSCoordinate());
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
			i++;
		}

		if (iS > -1) {
			return intersectionPoints.get(iS);
		}
		return null;
	}

	@Override
	public Integer call() throws Exception {

		long time = System.currentTimeMillis();
		Vector3D motion = null;
		double v = 0;
		try {
			OperatingAreaPoint oldLocation = new OperatingAreaPoint(entity
					.getSvgZeroPoint().getX()
					+ entity.getOrientationPoint().getX(), entity
					.getSvgZeroPoint().getY()
					+ entity.getOrientationPoint().getY());

			LocalTaskCRRI task = entity.getAsOptimTask(getDt(),
					entity.getMaxVelocity());
			task.setPrecision(entity.getPrecision());
			task.setLogger(this.getLogger());

			// getLogger().info("optim task localisation " +
			// task.getLocation());
			double ws = 0;
			for (Goal g : goals) {
				// if (g.getStage() != 0
				// && (g.getStage() > entity.getStage() || g
				// .getStage_end() < entity.getStage())) {
				// continue;
				// }
				if ((g.getStage() > entity.getStage() || g.getStage_end() < entity
						.getStage())) {
					continue;
				}
				if (g instanceof SelfOrganizeDeployment) {

					Goal myGoal = ((SelfOrganizeDeployment) g).getASGoal(entity
							.getName());
					if (myGoal == null)
						continue;
					if (((SelfOrganizeDeployment) g).isE_degree())
						task.setMyMass(myGoal.getE() * inBuffer.size());
					else
						task.setMyMass(myGoal.getE());

					double a = Math.sqrt(3) * entity.getSensingRange();
					double b = entity.getCard().getOptimalWirelessDistance();
					double d = new BigDecimal((a > 0 && a < b) ? a : b)
							.setScale(2, BigDecimal.ROUND_HALF_DOWN)
							.doubleValue();

					// System.out.println("D="+d);
					/* filtrowanie wiadomości */
					ArrayList<BroadcastMessage> messages = messageFilter(
							1.25 * d, oldLocation.getAsVector3D());
					if (messages.size() < 3)
						messages = inBuffer;

					for (BroadcastMessage m : messages) {
						Goal o = ((SelfOrganizeDeployment) g).getASGoal(m
								.getName());
						if (o != null) {
							o.setOrientationPoint(new OperatingAreaPoint(m
									.getLocation()));
							if (((SelfOrganizeDeployment) g).isE_degree()) {
								o.setE(o.getE() * m.getDegree());
							}

							if (o.getD() == null) {
								o.setD(d);
							}

							PotentialSource ps = o.getAsPotentialSource();
							ps.setVelocity(m.getVelocity());
							ps.setAccuracy_X(ps.getAccuracy_X()
									+ m.getShift().getX()
									* entity.getPrecision());
							ps.setAccuracy_Y(ps.getAccuracy_Y()
									+ m.getShift().getY()
									* entity.getPrecision());
							ps.setCooperative(true);

							task.getInfluencePoints().add(ps);
							ws = ws + o.getE();
						}
					}
				} else {
					task.getInfluencePoints().add(g.getAsPotentialSource());
					ws = ws + g.getE();
				}
			}
			for (Goal g : explorationList) {
				task.getInfluencePoints().add(g.getAsPotentialSource());
			}

			for (Iterator<PotentialSource> it = task.getInfluencePoints()
					.iterator(); it.hasNext();) {
				PotentialSource ps = it.next();
				ps.calculateVirtualInfluenceLocation(oldLocation
						.getAsVector3D());
			}

			OperatingAreaPoint newLocation = null;

			task.setLocation(oldLocation.getAsVector3D());
			task.calculateWithInterval();

			newLocation = new OperatingAreaPoint(task.getLocation());
			motion = new Vector3D(newLocation.getX() - oldLocation.getX(),
					newLocation.getY() - oldLocation.getY(), 0);
			v = motion.getNorm() / getDt();

			boolean collisionFree = false;
			while (!collisionFree) {
				task.setLocation(oldLocation.getAsVector3D());
				task.calculateWithInterval();

				newLocation = new OperatingAreaPoint(task.getLocation());

				motion = new Vector3D(newLocation.getX() - oldLocation.getX(),
						newLocation.getY() - oldLocation.getY(), 0);
				v = motion.getNorm() / getDt();

				Coordinate intersection = collisionFree(oldLocation,
						newLocation, 1);
				if (entity.getSide().equals(JTSUtils.ADAP)) {

					if (intersection == null) {
						collisionFree = true;
					} else {
						Goal intersectionPoint = new Goal();
						intersectionPoint.setActive(true);
						intersectionPoint.setRange(entity.getMaxVelocity()
								* getDt() * 0.9);
						intersectionPoint.setD(entity.getMaxVelocity()
								* getDt());
						intersectionPoint.setE(2 * ws);
						intersectionPoint
								.setSvgZeroPoint(new OperatingAreaPoint(0, 0));
						intersectionPoint
								.setOrientationPoint(new OperatingAreaPoint(
										intersection.x, intersection.y));

						task.getInfluencePoints().add(
								intersectionPoint.getAsPotentialSource());
						// if (explorationList.size() > 10)
						// explorationList.remove(0);
						explorationList.add(intersectionPoint);
						collisionFree = false;
					}
				} else {
					collisionFree = true;
					newLocation = getJts().collisionFree(oldLocation,
							newLocation, getGeometryFactory(), 1, areaWidth,
							areaHeight, this.getEntity(), this.getObstacles());
				}
			}

			motion = new Vector3D(newLocation.getX() - oldLocation.getX(),
					newLocation.getY() - oldLocation.getY(), 0);
			v = motion.getNorm() / getDt();

			if (v < entity.getMinVelocity()) {
				newLocation = oldLocation;
				motion = new Vector3D(0, 0, 0);
				v = 0;
			}

			// Zwalnianie przed przeszkodami

			if (v > 0) {
				double freeDistance = getJts().getFreeDistance(entity.getId(),
						obstacles, getGeometryFactory(), oldLocation,
						newLocation, entity.getRangeOfView());
				if (freeDistance < entity.getMaxBrakingDistance()) {
					double v2 = Math.sqrt((entity.getMaxVelocity()
							* entity.getMaxVelocity() * freeDistance)
							/ entity.getMaxBrakingDistance());
					if (v2 < v) {
						motion = motion.scalarMultiply(v2 / v);
						v = v2;
						newLocation = new OperatingAreaPoint(oldLocation.getX()
								+ motion.getX(), oldLocation.getY()
								+ motion.getY());
					}
				}
			}

			motion = new Vector3D(newLocation.getX() - oldLocation.getX(),
					newLocation.getY() - oldLocation.getY(), 0);
			v = motion.getNorm() / getDt();

			// if (v > 0) {
			// int s = entity.getBlocked(3, entity.getPrecision());
			// if (s > 0) {
			// entity.setMinVelocity(entity.getMinVelocity() * (2));
			// entity.setMaxVelocity(entity.getMaxVelocity() * (0.9));

			// Vector3D noise = RandomTools.getRandomPoint(
			// entity.getPrecision(), entity.getPrecision());
			// newLocation.setX(newLocation.getX()
			// + (noise.getX() - entity.getPrecision() / 2));
			// newLocation.setY(newLocation.getY()
			// + (noise.getY() - entity.getPrecision() / 2));
			// }
			// }

			entity.setAvgEnergy(task.getEnergy(newLocation.getAsVector3D()));
			if (entity.getAvgEnergy() < 0.001) {
				entity.setStage(entity.getStage() + 1);
			}

			ArrayList<Vector3D> influencePoints = new ArrayList<Vector3D>();
			influencePoints.add(newLocation.getAsVector3D());
			for (int p = 1; p < entity.getPoints().size(); p++) {
				Vector3D point = entity.getPoints().get(p).getAsVector3D()
						.add(entity.getSvgZeroPoint().getAsVector3D());
				influencePoints.add(point);
			}

			ArrayList<BondTask> bTasks = new ArrayList<BondTask>();
			for (int p = 1; p < entity.getPoints().size(); p++) {
				BondTask bt = new BondTask(p, influencePoints, entity
						.getBonds().get(p));
				bTasks.add(bt);
			}

			for (int i = 0; i < 500; i++) { // wyznaczenie nowych położeń
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

			// przesuniecie węzla mobilnego

			entity.setSvgZeroPoint(new OperatingAreaPoint(influencePoints
					.get(0).subtract(
							entity.getOrientationPoint().getAsVector3D())));

			// System.out.println(entity.getName() + " "
			// + entity.getSvgZeroPoint().toString() + " "
			// + entity.getCard().getOptimalWirelessDistance() + " "
			// + entity.getCard().getMaxCcaWirelessDistance());

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

			String points = "";
			for (int p = 1; p < entity.getPoints().size(); p++) {
				OperatingAreaPoint oap = entity.getPoints().get(p);
				points = points + oap.getX() + "," + oap.getY() + " ";
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
			velocity = v;
			data.setVelocity(velocity);
			this.getWlChannel().send(entity.getId(), null, data);

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setCalculationTime(new BigInteger(""
				+ (System.currentTimeMillis() - time)));
		return new Integer(0);
	}

	public void updateParameters() {

		for (int i = 0; i < entity.getEventsMatrix().getRowCount(); i++) {
			double step = entity.getEventsMatrix().getAsDouble(i, 0);
			if (step == this.getTimestamp()) {

				String code = entity.getEventsMatrix().getAsString(i, 1);
				String value = entity.getEventsMatrix().getAsString(i, 2);
				entity.setParameter(code, value);
			}
		}
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

	public MobileEntity getEntity() {
		return entity;
	}

	public void setEntity(MobileEntity entity) {
		this.entity = entity;
	}

	public ArrayList<Goal> getGoals() {
		return goals;
	}

	public void setGoals(ArrayList<Goal> goals) {
		this.goals = goals;
	}

	public Double getCellSize() {
		return cellSize;
	}

	public void setCellSize(Double cellSize) {
		this.cellSize = cellSize;
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

	public boolean isAdap() {
		return adap;
	}

	public void setAdap(boolean adap) {
		this.adap = adap;
	}

	public ArrayList<Goal> getExplorationList() {
		return explorationList;
	}

	public void setExplorationList(ArrayList<Goal> explorationList) {
		this.explorationList = explorationList;
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
