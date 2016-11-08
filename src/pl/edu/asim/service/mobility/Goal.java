package pl.edu.asim.service.mobility;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.service.optim.PotentialSource;
import pl.edu.asim.util.RandomTools;

public class Goal extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 218229600577887684L;
	protected Double e;
	protected Double d;
	protected Double width = 0.0;
	protected Double height = 0.0;
	protected Double range;
	protected Double precision = 0.0;
	protected Double accuracy_x = 0.0;
	protected Double accuracy_y = 0.0;
	protected Double accuracy_z = 0.0;

	private boolean active = true;
	private boolean random_location = false;
	private boolean grid_location = false;
	private boolean grid_obstacles = false;
	private int start;
	private int stop;
	private int stage;
	private int stage_end;

	private boolean neutral = false;
	private boolean cooperative = false;

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public Double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getE() {
		return e;
	}

	public void setE(double e) {
		this.e = e;
	}

	@Override
	public void read(ASimDO data) {
		String svg_x = "0";
		String svg_y = "0";
		String c_x = "0";
		String c_y = "0";
		this.setName(data.getName());
		this.setId("" + data.getId());
		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("name")) {
				this.setName(p.getValue());
			} else if (p.getCode().equals("id")) {
				this.setId(p.getValue());
			} else if (p.getCode().equals("active")) {
				this.setActive(new Boolean(p.getValue()));
			} else if (p.getCode().equals("random_location")) {
				this.setRandom_location(new Boolean(p.getValue()));
			} else if (p.getCode().equals("grid_location")) {
				this.setGrid_location(new Boolean(p.getValue()));
			} else if (p.getCode().equals("grid_obstacles")) {
				this.setGrid_obstacles(new Boolean(p.getValue()));
			} else if (p.getCode().equals("active_start")) {
				this.setStart(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("active_stop")) {
				this.setStop(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("stage")) {
				this.setStage(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("stage_end")) {
				this.setStage_end(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("svg_x")) {
				svg_x = p.getValue();
			} else if (p.getCode().equals("svg_y")) {
				svg_y = p.getValue();
			} else if (p.getCode().equals("svg_width")) {
				width = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("svg_height")) {
				height = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("accuracy_x")) {
				accuracy_x = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("accuracy_y")) {
				accuracy_y = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("accuracy_z")) {
				accuracy_z = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("c_x")) {
				c_x = p.getValue();
			} else if (p.getCode().equals("c_y")) {
				c_y = p.getValue();
			} else if (p.getCode().equals("d")) {
				this.setD(new Double(p.getValue()));
			} else if (p.getCode().equals("goal_type")) {
				String gt = "" + p.getValue();
				if (gt != null && gt.equals("neutral")) {
					neutral = true;
				} else if (gt != null && gt.equals("cooperative"))
					cooperative = true;
			} else if (p.getCode().equals("e")) {
				this.setE(new Double(p.getValue()));
			} else if (p.getCode().equals("range")) {
				if (p.getValue() != null && !p.getValue().equals("")
						&& !p.getValue().equals("0"))
					this.setRange(new Double(p.getValue()));
			} else if (p.getCode().equals("precision")) {
				if (p.getValue() != null && !p.getValue().equals("")
						&& !p.getValue().equals("0"))
					this.setPrecision(new Double(p.getValue()));
			}
		}

		if (this.isRandom_location()) {
			Vector3D v = RandomTools.getRandomPoint(width, height);
			orientationPoint = new OperatingAreaPoint(v);
		} else {
			orientationPoint = new OperatingAreaPoint(new Double(c_x),
					new Double(c_y));
		}

		svgZeroPoint = new OperatingAreaPoint(new Double(svg_x), new Double(
				svg_y));

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
	}

	public PotentialSource getAsPotentialSource() {
		PotentialSource ps = new PotentialSource(this.e, this.d, range,
				precision, orientationPoint.getAsVector3D().add(
						svgZeroPoint.getAsVector3D()));
		ps.setAccuracy_X(accuracy_x);
		ps.setAccuracy_Y(accuracy_y);
		ps.setAccuracy_Z(accuracy_z);
		ps.setNeutral(neutral);
		ps.setCooperative(cooperative);
		return ps;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setRandom_location(boolean random_location) {
		this.random_location = random_location;
	}

	public boolean isRandom_location() {
		return random_location;
	}

	public boolean isGrid_location() {
		return grid_location;
	}

	public void setGrid_location(boolean grid_location) {
		this.grid_location = grid_location;
	}

	public boolean isGrid_obstacles() {
		return grid_obstacles;
	}

	public void setGrid_obstacles(boolean grid_obstacles) {
		this.grid_obstacles = grid_obstacles;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}

	public Goal getAsGoal() {
		Goal o = new Goal();
		o.setId(id);
		o.setName(name);
		o.setActive(active);
		o.setRandom_location(random_location);
		o.setGrid_location(grid_location);
		o.setGrid_obstacles(grid_obstacles);
		o.setSvgManager(this.getSvgManager());
		o.setWidth(width);
		o.setHeight(height);
		o.setStage(stage);
		o.setStage_end(stage_end);

		if (this.isRandom_location()) {
			Vector3D v = RandomTools.getRandomPoint(width, height);
			o.setOrientationPoint(new OperatingAreaPoint(v));
		} else {
			o.setOrientationPoint(orientationPoint.clone());
		}

		o.setSvgManager(getSvgManager());
		for (OperatingAreaPoint point : points) {
			o.getPoints().add(point.clone());
		}
		if (d != null)
			o.setD(d);
		if (e != null)
			o.setE(e);
		o.setStart(this.getStart());
		o.setStop(this.getStop());
		o.setAccuracy_x(accuracy_x);
		o.setAccuracy_y(accuracy_y);
		o.setAccuracy_z(accuracy_z);
		o.setSvgZeroPoint(svgZeroPoint.clone());
		o.setCooperative(cooperative);
		o.setNeutral(neutral);
		if (range != null)
			o.setRange(range);
		if (precision != null)
			o.setPrecision(precision);
		return o;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	@Override
	public String toString() {
		String result = "Goal="
				+ (this.getOrientationPoint().getAsVector3D()
						.add(this.getSvgZeroPoint().getAsVector3D())
						+ " e="
						+ this.getE()
						+ " range="
						+ this.getRange()
						+ " d=" + this.getD());
		return result;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public int getStage_end() {
		return stage_end;
	}

	public void setStage_end(int stage_end) {
		this.stage_end = stage_end;
	}

	public Double getAccuracy_x() {
		return accuracy_x;
	}

	public void setAccuracy_x(Double accuracy_x) {
		this.accuracy_x = accuracy_x;
	}

	public Double getAccuracy_y() {
		return accuracy_y;
	}

	public void setAccuracy_y(Double accuracy_y) {
		this.accuracy_y = accuracy_y;
	}

	public Double getAccuracy_z() {
		return accuracy_z;
	}

	public void setAccuracy_z(Double accuracy_z) {
		this.accuracy_z = accuracy_z;
	}

	public boolean isNeutral() {
		return neutral;
	}

	public void setNeutral(boolean neutral) {
		this.neutral = neutral;
	}

	public boolean isCooperative() {
		return cooperative;
	}

	public void setCooperative(boolean cooperative) {
		this.cooperative = cooperative;
	}

	@Override
	public Double getPrecision() {
		return precision;
	}

	@Override
	public void setPrecision(Double precision) {
		this.precision = precision;
	}

}
