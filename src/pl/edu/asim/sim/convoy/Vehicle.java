package pl.edu.asim.sim.convoy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.service.mobility.Entity;
import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.optim.LocalTaskCRR;
import pl.edu.asim.service.optim.LocalTaskCRRI;
import pl.edu.asim.service.wpan.Card;
import pl.edu.asim.util.Matrix;

public class Vehicle extends Goal {

	public static String LEFT = "left";
	public static String RIGHT = "left";
	public static String DIRECT = "direct";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132497736026570186L;
	private Double maxVelocity;
	private Double minVelocity;
	private Double rangeOfView;
	private Double maxBrakingDistance;
	private Double buffer;
	private List<Goal> goals = new ArrayList<Goal>();
	private String side = LEFT;
	private Matrix convoyMatrix;

	public Double getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(Double maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public void calculateBonds() {
		bonds = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < points.size(); i++) {
			ArrayList<Double> bond = new ArrayList<Double>();
			for (int ii = 0; ii < points.size(); ii++) {
				bond.add(points.get(ii).getAsJTSCoordinate()
						.distance(points.get(i).getAsJTSCoordinate()));
			}
			bonds.add(bond);
		}
	}

	@Override
	public void read(ASimDO data) {
		String svg_x = "0";
		String svg_y = "0";
		String c_x = "0";
		String c_y = "0";
		this.setName(data.getName());
		this.setId("" + data.getId());

		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("WPAN_CARD")) {
				setCard(new Card());
				for (ASimPO p : d.getProperties()) {
					if (p.getCode().equals("pt")) {
						getCard().setPt(new Double(p.getValue()));
					} else if (p.getCode().equals("ps")) {
						getCard().setPs(new Double(p.getValue()));
					} else if (p.getCode().equals("pcca")) {
						getCard().setPcca(new Double(p.getValue()));
					}
				}
			}
		}

		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("name")) {
				this.setName(p.getValue());
			} else if (p.getCode().equals("id")) {
				this.setId(p.getValue());
			} else if (p.getCode().equals("convoyMatrix")) {
				setConvoyMatrix(new Matrix(p.getValue()));
			} else if (p.getCode().equals("active")) {
				this.setActive(new Boolean(p.getValue()));
			} else if (p.getCode().equals("svg_x")) {
				svg_x = p.getValue();
			} else if (p.getCode().equals("svg_y")) {
				svg_y = p.getValue();
			} else if (p.getCode().equals("c_x")) {
				c_x = p.getValue();
			} else if (p.getCode().equals("c_y")) {
				c_y = p.getValue();
			} else if (p.getCode().equals("d")) {
				this.setD(new Double(p.getValue()));
			} else if (p.getCode().equals("e")) {
				this.setE(new Double(p.getValue()));
			} else if (p.getCode().equals("range")) {
				if (p.getValue() != null && !p.getValue().equals("")
						&& !p.getValue().equals("0"))
					this.setRange(new Double(p.getValue()));
			} else if (p.getCode().equals("v_max")) {
				this.setMaxVelocity(new Double(p.getValue()));
			} else if (p.getCode().equals("v_min")) {
				this.setMinVelocity(new Double(p.getValue()));
			} else if (p.getCode().equals("range_of_view")) {
				this.setRangeOfView(new Double(p.getValue()));
			} else if (p.getCode().equals("braking_distance")) {
				this.setMaxBrakingDistance(new Double(p.getValue()));
			} else if (p.getCode().equals("buffer")) {
				this.setBuffer(new Double(p.getValue()));
			} else if (p.getCode().equals("side")) {
				this.setSide(p.getValue());
			}
		}
		svgZeroPoint = new OperatingAreaPoint(new Double(svg_x), new Double(
				svg_y));
		orientationPoint = new OperatingAreaPoint(new Double(c_x), new Double(
				c_y));
		this.getPoints().add(orientationPoint);

		org.w3c.dom.Document svgDocument = getSvgManager().getSVGDocument(data,
				false);
		org.w3c.dom.NodeList nodeList = svgDocument
				.getElementsByTagName("polygon");

		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node node = nodeList.item(i);
			String points = ((org.w3c.dom.Element) node).getAttribute("points");
			String[] subpoints = points.split(" ");
			for (String point : subpoints) {
				String[] coordinate = point.split(",");
				OperatingAreaPoint oap = new OperatingAreaPoint(new Double(
						coordinate[0]), new Double(coordinate[1]));
				this.getPoints().add(oap);
			}
		}

		if (this.getPoints().size() > 1) {
			calculateBonds();
		}

		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("GOALS")) {
				for (ASimDO entity : d.getChildren()) {
					Goal o = new Goal();
					o.setSvgManager(getSvgManager());
					o.read(entity);
					this.getGoals().add(o);
				}
			}
		}
		setConnections(new HashMap<String, Entity>());

	}

	@Override
	public Goal getAsGoal() {
		Goal o = new Goal();
		o.setId(id);
		o.setName(name);
		o.setSvgZeroPoint(svgZeroPoint.clone());
		o.setSvgManager(getSvgManager());
		for (OperatingAreaPoint point : points) {
			o.getPoints().add(point.clone());
		}
		// o.setD(d);
		// o.setE(e);
		o.setOrientationPoint(orientationPoint.clone());
		// o.setRange(range);
		return o;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public LocalTaskCRR getAsOptimTask(double dt) {
		LocalTaskCRR task = new LocalTaskCRR(orientationPoint.getAsVector3D()
				.add(svgZeroPoint.getAsVector3D()));
		task.setMaxRange(dt * maxVelocity);
		return task;
	}

	public LocalTaskCRRI getAsOptimTask(double dt, double velocity) {
		LocalTaskCRRI task = new LocalTaskCRRI(orientationPoint.getAsVector3D()
				.add(svgZeroPoint.getAsVector3D()));
		task.setVelocity(velocity);
		task.setDt(dt);
		task.setName(name);
		return task;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public Matrix getConvoyMatrix() {
		return convoyMatrix;
	}

	public void setConvoyMatrix(Matrix convoyMatrix) {
		this.convoyMatrix = convoyMatrix;
	}

	public void setBuffer(Double buffer) {
		this.buffer = buffer;
	}

	public Double getBuffer() {
		return buffer;
	}

	public void setMinVelocity(Double minVelocity) {
		this.minVelocity = minVelocity;
	}

	public Double getMinVelocity() {
		return minVelocity;
	}

	public void setRangeOfView(Double rangeOfView) {
		this.rangeOfView = rangeOfView;
	}

	public Double getRangeOfView() {
		return rangeOfView;
	}

	public void setMaxBrakingDistance(Double maxBrakingDistance) {
		this.maxBrakingDistance = maxBrakingDistance;
	}

	public Double getMaxBrakingDistance() {
		return maxBrakingDistance;
	}

}
