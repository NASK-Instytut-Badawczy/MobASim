package pl.edu.asim.sim.deployment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pl.edu.asim.service.mobility.Goal;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.Obstacle;
import pl.edu.asim.service.mobility.OperatingAreaPoint;

import com.vividsolutions.jts.geom.GeometryFactory;

public class TriangularGridDeployment extends Goal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4651788014546242228L;

	double h;
	JTSUtils jts;
	GeometryFactory geometryFactory;

	public double getH() {
		h = Math.sqrt(3) * this.getD() / 2;
		return h;
	}

	public int getMaxRingCount() {
		return new BigDecimal(height / getH()).intValue();
	}

	public ArrayList<Goal> getGoalList(List<Obstacle> obstacles) {

		ArrayList<OperatingAreaPoint> pointList = new ArrayList<OperatingAreaPoint>();
		ArrayList<Goal> goalList = new ArrayList<Goal>();

		int maxRingCount = getMaxRingCount();
		// System.out.println(maxRingCount);
		double a = this.getD();
		double start_x = getSvgZeroPoint().getX()
				+ getOrientationPoint().getX();
		double start_y = getSvgZeroPoint().getY()
				+ getOrientationPoint().getY();
		for (int i = 1; i <= maxRingCount; i++) {
			start_x = start_x - (a);
			// pierwszy punkt - nie zapisywany

			for (int p = 1; p <= i; p++) {
				start_x = start_x + 0.5 * a;
				start_y = start_y - h;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// pierwszy punkt
			}
			for (int p = 1; p <= i; p++) {
				start_x = start_x + a;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// drugi punkt
			}
			for (int p = 1; p <= i; p++) {
				start_x = start_x + 0.5 * a;
				start_y = start_y + h;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// trzeci punkt
			}
			for (int p = 1; p <= i; p++) {
				start_x = start_x - 0.5 * a;
				start_y = start_y + h;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// czwarty punkt
			}
			for (int p = 1; p <= i; p++) {
				start_x = start_x - a;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// piąty punkt
			}
			for (int p = 1; p <= i; p++) {
				start_x = start_x - 0.5 * a;
				start_y = start_y - h;
				if (!outside(start_x, start_y))
					pointList.add(new OperatingAreaPoint(start_x, start_y));
				// szósty punkt
			}
		}

		for (OperatingAreaPoint point : pointList) {

			if (isGrid_obstacles() && isInObstacle(obstacles, point))
				continue;

			Goal goal = new Goal();
			goal.setName(getName());
			goal.setRange(getRange());
			goal.setE(getE());
			goal.setStart(getStart());
			goal.setStop(getStop());
			goal.setActive(true);
			goal.setSvgZeroPoint(new OperatingAreaPoint(0, 0));
			goal.setOrientationPoint(point);
			goal.setStage(getStage());
			goal.setStage_end(getStage_end());
			goal.setAccuracy_x(accuracy_x);
			goal.setAccuracy_y(accuracy_y);
			goal.setAccuracy_z(accuracy_z);
			goal.setD(1);

			goalList.add(goal);
		}

		return goalList;
	}

	private boolean isInObstacle(List<Obstacle> obstacles,
			OperatingAreaPoint point) {

		for (Obstacle o : obstacles) {
			if (!o.isActive())
				continue;
			if (jts.getAsJTSPolygon(o, geometryFactory).buffer(2)
					.contains(point.getAsJTSPoint(geometryFactory))) {
				return true;
			}
		}
		return false;
	}

	private boolean outside(double x, double y) {
		boolean result = (x < this.getSvgZeroPoint().getX()
				|| y < this.getSvgZeroPoint().getY()
				|| x > this.getSvgZeroPoint().getX() + width || y > this
				.getSvgZeroPoint().getY() + height);
		return result;
	}

	public JTSUtils getJts() {
		return jts;
	}

	public void setJts(JTSUtils jts) {
		this.jts = jts;
	}

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	public void setGeometryFactory(GeometryFactory factory) {
		this.geometryFactory = factory;
	}

	public void setH(double h) {
		this.h = h;
	}

}
