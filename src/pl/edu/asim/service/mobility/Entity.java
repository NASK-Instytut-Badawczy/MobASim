package pl.edu.asim.service.mobility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.service.wpan.Card;
import pl.edu.asim.sim.ASimSVGManager;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public abstract class Entity implements java.lang.Comparable<Entity>, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1088840790046633128L;
	protected String name;
	protected String id;
	// private Geometry coverage;
	private Double avgEnergy = new Double("0");

	private HashMap<String, Entity> connections;
	private Card card;
	private ASimSVGManager svgManager;

	/* globalna lokalizacja obiektu */
	protected OperatingAreaPoint svgZeroPoint;
	protected ArrayList<OperatingAreaPoint> svgZeroPointHistory = new ArrayList<OperatingAreaPoint>();

	/* lokalna lokalizacja obiektu */
	protected ArrayList<OperatingAreaPoint> points = new ArrayList<OperatingAreaPoint>();
	protected OperatingAreaPoint orientationPoint;

	/* ograniczenia na odległość pomiędzy punktami brzegowymi */
	protected ArrayList<ArrayList<Double>> bonds;
	/* poprzednie położenie punktów brzegowych w formacie SVG */
	protected String oldPointsString;

	//protected ASimSimulatorManager manager;

	protected Double pathLenght = 0.0;
	
	private Double precision;

	public void setPoints(ArrayList<OperatingAreaPoint> points) {
		this.points = points;
	}

	public ArrayList<OperatingAreaPoint> getPoints() {
		return points;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OperatingAreaPoint getSvgZeroPoint() {
		return svgZeroPoint;
	}

	public void setSvgZeroPoint(OperatingAreaPoint svgZeroPoint) {
		if (this.svgZeroPoint != null) {
			double pl = this.svgZeroPoint.getAsVector3D().distance(
					svgZeroPoint.getAsVector3D());
			this.setPathLenght((this.getPathLenght() + pl));
			svgZeroPointHistory.add(new OperatingAreaPoint(this.svgZeroPoint
					.getAsVector3D()));

		}
		this.svgZeroPoint = svgZeroPoint;
	}

	public abstract void read(ASimDO data);

	public Point[] getBoundaryPointsAsTable(GeometryFactory factory) {
		if (points == null)
			return null;
		Point[] pList = new Point[points.size()];
		for (int i = 0; i < points.size(); i++) {
			pList[i] = points.get(i).getAsJTSPoint(factory);
		}
		return pList;
	}

	public MultiPoint getBoundaryPointsAsMultiPoint(GeometryFactory factory) {
		if (points == null)
			return null;
		Point[] pList = getBoundaryPointsAsTable(factory);
		return new MultiPoint(pList, factory);
	}

	@Override
	public String toString() {
		String result = "Entity points=";
		for (OperatingAreaPoint p : points) {
			result = result + " " + p.toString();
		}
		return result;
	}

	public OperatingAreaPoint getOrientationPoint() {
		return orientationPoint;
	}

	public void setOrientationPoint(OperatingAreaPoint orientationPoint) {
		this.orientationPoint = orientationPoint;
	}

	public ArrayList<ArrayList<Double>> getBonds() {
		return bonds;
	}

	public void setBonds(ArrayList<ArrayList<Double>> bonds) {
		this.bonds = bonds;
	}

	public String getOldPointsString() {
		return oldPointsString;
	}

	public void setOldPointsString(String oldPointsString) {
		this.oldPointsString = oldPointsString;
	}

	public Obstacle getAsObstacle() {
		Obstacle o = new Obstacle();
		o.setId(id);
		o.setName(name);
		o.setSvgZeroPoint(svgZeroPoint.clone());
		o.setSvgManager(getSvgManager());
		for (OperatingAreaPoint point : points) {
			o.getPoints().add(point.clone());
		}
		return o;
	}

	public ArrayList<OperatingAreaPoint> getSvgZeroPointHistory() {
		return svgZeroPointHistory;
	}

	public void setSvgZeroPointHistory(
			ArrayList<OperatingAreaPoint> svgZeroPointHistory) {
		this.svgZeroPointHistory = svgZeroPointHistory;
	}

	public HashMap<String, Entity> getConnections() {
		return connections;
	}

	public void setConnections(HashMap<String, Entity> connections) {
		this.connections = connections;
	}

	public boolean isDirectConnected(String name) {
		if (getConnections().get(name) != null)
			return true;
		else
			return false;
	}

	public boolean isConnected(String name, HashSet<String> ask) {
		if (isDirectConnected(name))
			return true;
		for (Entity entity : getConnections().values()) {
			if (ask.contains(entity.getName()))
				continue;
			ask.add(this.getName());
			if (entity.isConnected(name, ask))
				return true;
		}
		return false;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Double getPathLenght() {
		return pathLenght;
	}

	public void setPathLenght(Double pathLenght) {
		this.pathLenght = pathLenght;
	}

	public void setAvgEnergy(Double avgEnergy) {
		this.avgEnergy = avgEnergy;
	}

	public Double getAvgEnergy() {
		return avgEnergy;
	}

	public boolean isBlocked(int backStep, double threshold) {
		if (this.svgZeroPointHistory.size() < backStep)
			return false;

		ArrayList<Double> crossDistanceList = new ArrayList<Double>();
		ArrayList<Double> distanceList = new ArrayList<Double>();
		Vector3D vector = this.svgZeroPointHistory.get(
				this.svgZeroPointHistory.size() - 1).getAsVector3D();
		for (int i = this.svgZeroPointHistory.size() - 2; i >= this.svgZeroPointHistory
				.size() - backStep; i--) {
			Vector3D v = this.svgZeroPointHistory.get(i).getAsVector3D();
			Vector3D v2 = this.svgZeroPointHistory.get(i + 1).getAsVector3D();
			double crossDistance = v.distance(vector);
			crossDistanceList.add(crossDistance);
			distanceList.add(v.distance(v2));
		}
		for (int s = 2; s < backStep; s++) {
			int i = 0;
			try {
				double distance = 0.0;
				for (i = 1; i < crossDistanceList.size(); i = i + s) {
					double cd = crossDistanceList.get(i);
					distance = distance + distanceList.get(i);
					if (cd < threshold && distance > 0) {
						// oscillations = true;
						// System.out.println("oscylacje o kroku " + s + " dla "
						// + this.getName() + " droga=" + distance);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public int getBlocked(int backStep, double threshold) {
		if (this.svgZeroPointHistory.size() < backStep)
			return 0;

		ArrayList<Double> crossDistanceList = new ArrayList<Double>();
		ArrayList<Double> distanceList = new ArrayList<Double>();
		Vector3D vector = this.svgZeroPointHistory.get(
				this.svgZeroPointHistory.size() - 1).getAsVector3D();
		for (int i = this.svgZeroPointHistory.size() - 2; i >= this.svgZeroPointHistory
				.size() - backStep; i--) {
			Vector3D v = this.svgZeroPointHistory.get(i).getAsVector3D();
			Vector3D v2 = this.svgZeroPointHistory.get(i + 1).getAsVector3D();
			double crossDistance = v.distance(vector);
			crossDistanceList.add(crossDistance);
			distanceList.add(v.distance(v2));
		}
		for (int s = 2; s < backStep; s++) {
			int i = 0;
			try {
				double distance = 0.0;
				for (i = 1; i < crossDistanceList.size(); i = i + s) {
					double cd = crossDistanceList.get(i);
					distance = distance + distanceList.get(i);
					if (cd < threshold && distance > 0) {
						// oscillations = true;
						// System.out.println("oscylacje o kroku " + s + " dla "
						// + this.getName() + " droga=" + distance);
						return s;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public Double getPrecision() {
		return precision;
	}

	public int compareTo(Entity e) {
		return name.compareTo(e.getName());
	}

	public ASimSVGManager getSvgManager() {
		return svgManager;
	}

	public void setSvgManager(ASimSVGManager svgManager) {
		this.svgManager = svgManager;
	}
}
