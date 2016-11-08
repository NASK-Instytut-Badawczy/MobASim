package pl.edu.asim.sim.deployment;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.util.Matrix;
import pl.edu.asim.util.RandomTools;

import com.vividsolutions.jts.geom.GeometryFactory;

public class SelfOrganizeDeployment extends Goal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6729019000656144244L;
	JTSUtils jts;
	GeometryFactory geometryFactory;
	private int degree;
	private boolean e_degree;

	public boolean isE_degree() {
		return e_degree;
	}

	public void setE_degree(boolean e_degree) {
		this.e_degree = e_degree;
	}

	private Matrix teamMatrix;

	public JTSUtils getJts() {
		return jts;
	}

	public void setJts(JTSUtils jts) {
		this.jts = jts;
	}

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	public void setGeometryFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
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
			} else if (p.getCode().equals("degree")) {
				this.setDegree(new BigInteger(p.getValue()).intValue());
			} else if (p.getCode().equals("e_degree")) {
				this.setE_degree(new Boolean(p.getValue()));
			} else if (p.getCode().equals("svg_x")) {
				svg_x = p.getValue();
			} else if (p.getCode().equals("svg_y")) {
				svg_y = p.getValue();
			} else if (p.getCode().equals("accuracy_x")) {
				accuracy_x = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("accuracy_y")) {
				accuracy_y = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("accuracy_z")) {
				accuracy_z = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("svg_width")) {
				width = new BigDecimal(p.getValue()).doubleValue();
			} else if (p.getCode().equals("svg_height")) {
				height = new BigDecimal(p.getValue()).doubleValue();
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

	@Override
	public Goal getAsGoal() {
		SelfOrganizeDeployment o = new SelfOrganizeDeployment();
		o.setId(id);
		o.setName(name);
		o.setActive(isActive());
		o.setRandom_location(false);
		o.setGrid_location(this.isGrid_location());
		o.setGrid_obstacles(this.isGrid_obstacles());
		o.setSvgManager(this.getSvgManager());
		o.setWidth(width);
		o.setHeight(height);
		o.setOrientationPoint(this.getOrientationPoint());
		o.setStart(this.getStart());
		o.setStop(this.getStop());
		o.setStage(getStage());
		o.setStage_end(this.getStage_end());
		o.setDegree(degree);
		o.setE_degree(e_degree);
		o.setAccuracy_x(accuracy_x);
		o.setAccuracy_y(accuracy_y);
		o.setAccuracy_z(accuracy_z);
		o.setSvgZeroPoint(new OperatingAreaPoint(0, 0));
		o.setTeamMatrix(teamMatrix);
		return o;
	}

	public Goal getASGoal(String nodeName) {
		for (int i = 0; i < teamMatrix.getRowCount(); i++) {
			String id = teamMatrix.getAsString(i, 0);
			if (nodeName.equals(id)) {
				Goal o = new Goal();
				o.setId(id);
				o.setName(name);
				o.setActive(isActive());
				o.setRandom_location(false);
				o.setGrid_location(this.isGrid_location());
				o.setGrid_obstacles(this.isGrid_obstacles());
				o.setSvgManager(this.getSvgManager());
				o.setWidth(width);
				o.setHeight(height);
				o.setOrientationPoint(this.getOrientationPoint());
				o.setStart(this.getStart());
				o.setStop(this.getStop());
				o.setSvgZeroPoint(new OperatingAreaPoint(0, 0));
				o.setStage(getStage());
				o.setStage_end(getStage_end());
				o.setAccuracy_x(accuracy_x);
				o.setAccuracy_y(accuracy_y);
				o.setAccuracy_z(accuracy_z);

				String distance = teamMatrix.getAsString(i, 1);
				if (!distance.equals("WPAN") && distance != null)
					o.setD(teamMatrix.getAsDouble(i, 1));
				String w = teamMatrix.getAsString(i, 2);
				if (w != null)
					o.setE(teamMatrix.getAsDouble(i, 2));
				String range = teamMatrix.getAsString(i, 3);
				if (range != null)
					o.setRange(teamMatrix.getAsDouble(i, 3));
				return o;
			}
		}
		return null;
	}

	public Matrix getTeamMatrix() {
		return teamMatrix;
	}

	public void setTeamMatrix(Matrix teamMatrix) {
		this.teamMatrix = teamMatrix;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

}
