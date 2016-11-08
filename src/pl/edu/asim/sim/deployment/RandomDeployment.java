package pl.edu.asim.sim.deployment;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.util.RandomTools;

import com.vividsolutions.jts.geom.GeometryFactory;

public class RandomDeployment extends Goal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4651788014546242228L;
	JTSUtils jts;
	GeometryFactory geometryFactory;

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

	private boolean random_location = true;

	@Override
	public boolean isRandom_location() {
		return random_location;
	}

	@Override
	public void setRandom_location(boolean random_location) {
		this.random_location = random_location;
	}

	public boolean isInObstacle(List<Obstacle> obstacles,
			OperatingAreaPoint point, double buffer) {

		for (Obstacle o : obstacles) {
			if (!o.isActive())
				continue;
			if (jts.getAsJTSPolygon(o, geometryFactory).buffer(buffer)
					.contains(point.getAsJTSPoint(geometryFactory))) {
				System.out.println("inObstacle " + point);
				return true;
			}
		}
		System.out.println("outObstacle " + point);
		return false;
	}

	public Goal getAsGoal(List<Obstacle> obstacles, double buffer) {
		Goal o = new Goal();
		o.setId(id);
		o.setName(name);
		o.setActive(isActive());
		o.setRandom_location(random_location);
		o.setGrid_location(this.isGrid_location());
		o.setGrid_obstacles(this.isGrid_obstacles());
		o.setSvgManager(this.getSvgManager());
		o.setWidth(width);
		o.setHeight(height);

		Vector3D v = RandomTools.getRandomPoint(width, height);
		OperatingAreaPoint p = new OperatingAreaPoint(v.getX()
				+ this.getSvgZeroPoint().getX(), v.getY()
				+ this.getSvgZeroPoint().getY());
		if (this.isGrid_obstacles())
			while (isInObstacle(obstacles, p, buffer)) {
				v = RandomTools.getRandomPoint(width, height);
				p = new OperatingAreaPoint(v.getX()
						+ this.getSvgZeroPoint().getX(), v.getY()
						+ this.getSvgZeroPoint().getY());
			}
		o.setOrientationPoint(p);
		o.setSvgManager(getSvgManager());
		o.setD(d);
		o.setE(e);
		o.setAccuracy_x(accuracy_x);
		o.setAccuracy_y(accuracy_y);
		o.setAccuracy_z(accuracy_z);
		o.setStage(getStage());
		o.setStage_end(getStage_end());
		o.setStart(this.getStart());
		o.setStop(this.getStop());
		o.setSvgZeroPoint(new OperatingAreaPoint(0, 0));
		o.setRange(range);
		return o;
	}

}
