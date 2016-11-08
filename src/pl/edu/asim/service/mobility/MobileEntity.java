package pl.edu.asim.service.mobility;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.service.optim.LocalTaskCRRI;
import pl.edu.asim.service.wpan.Card;
import pl.edu.asim.sim.deployment.SelfOrganizeDeployment;
import pl.edu.asim.util.Matrix;

import com.vividsolutions.jts.geom.GeometryFactory;

public class MobileEntity extends Goal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132497736026570186L;
	private Double buffer;
	private List<Goal> goals = new ArrayList<Goal>();
	private String side = JTSUtils.LEFT;
	private Double sensingRange;
	private Integer ring;
	private Double maxVelocity;
	private Double minVelocity;
	private Double rangeOfView;
	private Double maxBrakingDistance;
	private Matrix eventsMatrix;

	private Double defaultMaxVelocity;
	private Double dmvReturn = 1.0;
	private int stage = 1;

	// private Geometry coverage;

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
			if (d.getType().equals("EVENTS")) {
				for (ASimPO p : d.getProperties())
					if (p.getCode().equals("eventsMatrix")) {
						setEventsMatrix(new Matrix(p.getValue()));
					}
			}
			if (d.getType().equals("GOALS")) {
				for (ASimDO entity : d.getChildren()) {
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
						this.getGoals().add(rd);
					} else {
						Goal o = new Goal();
						o.setSvgManager(getSvgManager());
						o.read(entity);
						this.getGoals().add(o);
					}
				}
			}
		}

		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("name")) {
				this.setName(p.getValue());
			} else if (p.getCode().equals("id")) {
				this.setId(p.getValue());
			} else if (p.getCode().equals("enabled")) {
				this.setActive(new Boolean(p.getValue()));
			} else if (p.getCode().equals("svg_x")) {
				svg_x = p.getValue();
			} else if (p.getCode().equals("svg_y")) {
				svg_y = p.getValue();
			} else if (p.getCode().equals("c_x")) {
				c_x = p.getValue();
			} else if (p.getCode().equals("c_y")) {
				c_y = p.getValue();
			} else if (p.getCode().equals("active_start")) {
				this.setStart(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("active_stop")) {
				this.setStop(new BigInteger(p.getValue()).intValue());
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
				this.setDefaultMaxVelocity(new Double(p.getValue()));
			} else if (p.getCode().equals("v_min")) {
				this.setMinVelocity(new Double(p.getValue()));
			} else if (p.getCode().equals("range_of_view")) {
				this.setRangeOfView(new Double(p.getValue()));
			} else if (p.getCode().equals("braking_distance")) {
				this.setMaxBrakingDistance(new Double(p.getValue()));
			} else if (p.getCode().equals("buffer")) {
				this.setBuffer(new Double(p.getValue()));
			} else if (p.getCode().equals("precision")) {
				this.setPrecision(new Double(p.getValue()));
			} else if (p.getCode().equals("side")) {
				this.setSide(p.getValue());
			} else if (p.getCode().equals("sensing_range")) {
				this.setSensingRange(new Double(p.getValue()));
			} else if (p.getCode().equals("ring")) {
				this.setRing(new Integer(p.getValue()));
			} else if (p.getCode().equals("pt")) {
				getCard().setPt(new Double(p.getValue()));
			} else if (p.getCode().equals("ps")) {
				getCard().setPs(new Double(p.getValue()));
			} else if (p.getCode().equals("pcca")) {
				getCard().setPcca(new Double(p.getValue()));
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
			// this.setPolygon(manager.getJts().getAsJTSPolygon(this.getPoints(),
			// manager.getGeometryFactory()));
			calculateBonds();
			// calculateRadius();
		}

		// for (ASimDO d : data.getChildren()) {
		// if (d.getType().equals("GOALS")) {
		// for (ASimDO entity : d.getChildren()) {
		// Goal o = new Goal();
		// o.setSvgManager(getSvgManager());
		// o.read(entity);
		// this.getGoals().add(o);
		// }
		// }
		// }
		setConnections(new HashMap<String, Entity>());

	}

	public void setParameter(String code, String value) {
		if (code.equals("name")) {
			this.setName(value);
		} else if (code.equals("id")) {
			this.setId(value);
		} else if (code.equals("enabled")) {
			this.setActive(new Boolean(value));
		} else if (code.equals("active_start")) {
			this.setStart(new BigInteger(value).intValue());
		} else if (code.equals("active_stop")) {
			this.setStop(new BigInteger(value).intValue());
		} else if (code.equals("d")) {
			this.setD(new Double(value));
		} else if (code.equals("e")) {
			this.setE(new Double(value));
		} else if (code.equals("range")) {
			if (value != null && !value.equals("") && !value.equals("0"))
				this.setRange(new Double(value));
		} else if (code.equals("v_max")) {
			this.setMaxVelocity(new Double(value));
			this.setDefaultMaxVelocity(new Double(value));
		} else if (code.equals("v_min")) {
			this.setMinVelocity(new Double(value));
		} else if (code.equals("range_of_view")) {
			this.setRangeOfView(new Double(value));
		} else if (code.equals("braking_distance")) {
			this.setMaxBrakingDistance(new Double(value));
		} else if (code.equals("buffer")) {
			this.setBuffer(new Double(value));
		} else if (code.equals("precision")) {
			this.setPrecision(new Double(value));
		} else if (code.equals("side")) {
			this.setSide(value);
		} else if (code.equals("sensing_range")) {
			this.setSensingRange(new Double(value));
		} else if (code.equals("ring")) {
			this.setRing(new Integer(value));
		}

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
		o.setD(d);
		o.setE(e);
		o.setStart(this.getStart());
		o.setStop(this.getStop());
		o.setOrientationPoint(orientationPoint.clone());
		o.setRange(range);
		return o;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public LocalTaskCRRI getAsOptimTask(double dt, double velocity) {
		LocalTaskCRRI task = new LocalTaskCRRI(orientationPoint.getAsVector3D()
				.add(svgZeroPoint.getAsVector3D()));
		task.setVelocity(velocity);
		task.setDt(dt);
		task.setName(name);
		return task;
	}

	public LocalTaskCRRI getAsOptimTask(double dt) {
		LocalTaskCRRI task = new LocalTaskCRRI(orientationPoint.getAsVector3D()
				.add(svgZeroPoint.getAsVector3D()));
		task.setVelocity(maxVelocity);
		task.setDt(dt);
		return task;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public void reverseSide() {
		if (this.side.equals(JTSUtils.LEFT))
			setSide(JTSUtils.RIGHT);
		else if (this.side.equals(JTSUtils.RIGHT))
			setSide(JTSUtils.LEFT);
	}

	public void setBuffer(Double buffer) {
		this.buffer = buffer;
	}

	public Double getBuffer() {
		return buffer;
	}

	public Double getSensingRange() {
		return sensingRange;
	}

	public void setSensingRange(Double sensingRange) {
		this.sensingRange = sensingRange;
	}

	public Integer getRing() {
		return ring;
	}

	public void setRing(Integer ring) {
		this.ring = ring;
	}

	public Double getMinVelocity() {
		return minVelocity;
	}

	public void setMinVelocity(Double minVelocity) {
		this.minVelocity = minVelocity;
	}

	public Double getRangeOfView() {
		return rangeOfView;
	}

	public void setRangeOfView(Double rangeOfView) {
		this.rangeOfView = rangeOfView;
	}

	public Double getMaxBrakingDistance() {
		return maxBrakingDistance;
	}

	public void setMaxBrakingDistance(Double maxBrakingDistance) {
		this.maxBrakingDistance = maxBrakingDistance;
	}

	public void setDefaultMaxVelocity(Double defaultMaxVelocity) {
		this.defaultMaxVelocity = defaultMaxVelocity;
	}

	public Double getDefaultMaxVelocity() {
		return defaultMaxVelocity;
	}

	public void setDmvReturn(Double dmvReturn) {
		this.dmvReturn = dmvReturn;
	}

	public Double getDmvReturn() {
		return dmvReturn;
	}

	public Matrix getEventsMatrix() {
		return eventsMatrix;
	}

	public void setEventsMatrix(Matrix eventsMatrix) {
		this.eventsMatrix = eventsMatrix;
	}

	@Override
	public int getStage() {
		return stage;
	}

	@Override
	public void setStage(int stage) {
		this.stage = stage;
	}

}
