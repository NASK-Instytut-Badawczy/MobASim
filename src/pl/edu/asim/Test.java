package pl.edu.asim;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Test {
	Double mass;
	Double range = null;
	Vector3D influenceLocation;
	Double d;
	Double d_min;
	Double accuracy = 0.0;

	public Test(double mass, double d, double range, Vector3D influenceLocation) {
		this.setMass(mass);
		this.setRange(range);
		this.setInfluenceLocation(influenceLocation);
		this.setD(d);
	}

	public Vector3D getForce(Vector3D location) {
		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();

		if (range != null && range < distance)
			return new Vector3D(0, 0, 0);
		if (distance == 0)
			return new Vector3D(0, 0, 0);

		Vector3D one = diff.scalarMultiply((mass) / distance);
		double a = (-2.0 * d * d) / (distance * distance * distance);
		double b = (-2.0 * d) / (distance * distance);
		return one.scalarMultiply(a - b);
	}

	public Vector3D[] getForceWithInterval(Vector3D location) {
		Vector3D[] result = new Vector3D[2];
		result[0] = new Vector3D(0, 0, 0);
		result[1] = new Vector3D(0, 0, 0);

		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();

		if (range != null && range < distance)
			return result;
		if (distance == 0)
			return result;

		double distance_min = (distance - accuracy > 0) ? distance - accuracy
				: 0.001;
		double distance_max = (distance + accuracy > 0) ? distance + accuracy
				: 0.001;

		Vector3D one = diff.scalarMultiply(((-2) * mass) / distance);

		double a1 = (d * d)
				/ ((distance_max) * (distance_max) * (distance_max));

		double b1 = (d) / ((distance_min) * (distance_min));

		double a2 = (d * d)
				/ ((distance_min) * (distance_min) * (distance_min));
		double b2 = (d) / (distance_max * distance_max);

		result[0] = one.scalarMultiply(a1 - b1);
		result[1] = one.scalarMultiply(a2 - b2);

		return result;
	}

	public Vector3D[] getForceWithInterval2(Vector3D location) {
		Vector3D[] result = new Vector3D[2];
		result[0] = new Vector3D(0, 0, 0);
		result[1] = new Vector3D(0, 0, 0);

		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();

		if (d_min == null || d_min > d)
			d_min = d;

		if (range != null && range < distance)
			return result;
		if (distance == 0)
			return result;

		Vector3D one = diff.scalarMultiply((mass) / distance);
		double a1 = (-2.0 * d_min * d_min) / (distance * distance * distance);
		double a2 = (-2.0 * d * d) / (distance * distance * distance);
		double b1 = (-2.0 * d) / (distance * distance);
		double b2 = (-2.0 * d_min) / (distance * distance);
		double r1 = a1 - b1;
		double r2 = a2 - b2;
		result[0] = one.scalarMultiply(Math.min(r1, r2));
		result[1] = one.scalarMultiply(Math.max(r1, r2));

		return result;
	}

	public Vector3D getUnitVector(Vector3D location) {
		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();
		if (distance == 0)
			return diff;
		if (distance > d) {
			return diff.normalize();
		} else if (distance < d) {
			diff.normalize().negate();
		}
		return new Vector3D(0, 0, 0);
	}

	public double getEnergy(Vector3D location) {
		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();
		if (distance == 0)
			return 0;
		if (range != null && range < distance)
			return 0;

		double a = d / distance - 1;
		double n = 1;
		return (mass) * (n * a * a);
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double pointMass) {
		this.mass = pointMass;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public Vector3D getInfluenceLocation() {
		return influenceLocation;
	}

	public void setInfluenceLocation(Vector3D influenceLocation) {
		this.influenceLocation = influenceLocation;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public Double getD_min() {
		return d_min;
	}

	public void setD_min(Double d_min) {
		this.d_min = d_min;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	private double min(double a, double b, double c, double d) {
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	private double max(double a, double b, double c, double d) {
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	public static void test() {
		Test t = new Test(1, 10, 1000, new Vector3D(1.0, 1.0, 0.0));
		t.setAccuracy(1.0);

		Double[] d1 = t.getDistance(new Vector3D(4.0, 2.0, 0.0));
		Vector3D[] v1 = t.getDistanceVector(new Vector3D(4.0, 2.0, 0.0));
		System.out.println("d1_min=" + d1[0]+" "+v1[0].getNorm());
		System.out.println("d1_max=" + d1[1]+" "+v1[1].getNorm());
		System.out.println("---------------------------------");

		Double[] d2 = t.getDistance(new Vector3D(-4.0, 0.5, 0.0));
		Vector3D[] v2 = t.getDistanceVector(new Vector3D(-4.0, 0.5, 0.0));
		System.out.println("d2_min=" + d2[0]+" "+v2[0].getNorm());
		System.out.println("d2_max=" + d2[1]+" "+v2[1].getNorm());
		System.out.println("---------------------------------");

		Double[] d3 = t.getDistance(new Vector3D(0.5, 0.5, 0.0));
		Vector3D[] v3 = t.getDistanceVector(new Vector3D(0.5, 0.5, 0.0));
		System.out.println("d3_min=" + d3[0]+" "+v3[0].getNorm());
		System.out.println("d3_max=" + d3[1]+" "+v3[1].getNorm());;
		System.out.println("---------------------------------");

	}

	public Double[] getDistance(Vector3D location) {
		Double[] result = new Double[2];

		double x = location.getX();
		double y = location.getY();

		double x1 = influenceLocation.getX() - accuracy;
		double x2 = influenceLocation.getX() + accuracy;

		double y1 = influenceLocation.getY() - accuracy;
		double y2 = influenceLocation.getY() + accuracy;

		// if (x >= x1 && x <= x2) {
		// if (Math.abs(x - x1) < Math.abs(x - x2))
		// x1 = x;
		// else
		// x2 = x;
		// } else if (y >= y1 && y <= y2) {
		// if (Math.abs(y - y1) < Math.abs(y - y2))
		// y1 = y;
		// else
		// y2 = y;
		// }

		System.out.println("x = " + x);
		System.out.println("y = " + y);
		System.out.println("x1 = " + x1);
		System.out.println("x2 = " + x2);
		System.out.println("y1 = " + y1);
		System.out.println("y2 = " + y2);

		double dx1 = (x1 - x) * (x1 - x);
		double dx2 = (x2 - x) * (x2 - x);

		double dy1 = (y1 - y) * (y1 - y);
		double dy2 = (y2 - y) * (y2 - y);

		double dx_min = Math.min(dx1, dx2);
		double dx_max = Math.max(dx1, dx2);

		double dy_min = Math.min(dy1, dy2);
		double dy_max = Math.max(dy1, dy2);

		result[0] = Math.sqrt(dx_min + dy_min);
		result[1] = Math.sqrt(dx_max + dy_max);

		// double result1 = (x1 * x1) - (2 * x2 * x) + (x * x) + (y1 * y1)
		// - (2 * y2 * y) + (y * y);
		// double result2 = (x2 * x2) - (2 * x1 * x) + (x * x) + (y2 * y2)
		// - (2 * y1 * y) + (y * y);

		// System.out.println("result1 = " + result1);
		// System.out.println("result2 = " + result2);

		// double result3 = (x1 * x1) - (2 * x2 * x) + (x * x) + (y2 * y2)
		// - (2 * y2 * y) + (y * y);
		// double result4 = (x2 * x2) - (2 * x1 * x) + (x * x) + (y1 * y1)
		// - (2 * y1 * y) + (y * y);

		// double result1 = (x1 * x1) - (2 * x1 * x) + (x * x) + (y1 * y1)
		// - (2 * y1 * y) + (y * y);
		// double result2 = (x2 * x2) - (2 * x2 * x) + (x * x) + (y2 * y2)
		// - (2 * y2 * y) + (y * y);

		// double result3 = (x1 * x1) - (2 * x2 * x) + (x * x) + (y2 * y2)
		// - (2 * y2 * y) + (y * y);
		// double result4 = (x2 * x2) - (2 * x1 * x) + (x * x) + (y1 * y1)
		// - (2 * y1 * y) + (y * y);

		// result[0] = Math.sqrt(Math.min(result1, result2));
		// result[1] = Math.sqrt(Math.max(result1, result2));
		// result[0] = Math.sqrt(min(result1, result2, result3, result4));
		// result[1] = Math.sqrt(max(result1, result2, result3, result4));
		return result;
	}

	public Vector3D[] getDistanceVector(Vector3D location) {
		Vector3D[] vectors = new Vector3D[2];

		double x = location.getX();
		double y = location.getY();

		double x1 = influenceLocation.getX() - accuracy;
		double x2 = influenceLocation.getX() + accuracy;

		double y1 = influenceLocation.getY() - accuracy;
		double y2 = influenceLocation.getY() + accuracy;

		double x_min = x1;
		double x_max = x2;
		double y_min = y1;
		double y_max = y2;
		
		// if (x >= x1 && x <= x2) {
		// if (Math.abs(x - x1) < Math.abs(x - x2))
		// x1 = x;
		// else
		// x2 = x;
		// } else if (y >= y1 && y <= y2) {
		// if (Math.abs(y - y1) < Math.abs(y - y2))
		// y1 = y;
		// else
		// y2 = y;
		// }

		double dx1 = (x1 - x) * (x1 - x);
		double dx2 = (x2 - x) * (x2 - x);

		double dy1 = (y1 - y) * (y1 - y);
		double dy2 = (y2 - y) * (y2 - y);

		if(dx1>dx2) {
			x_min = x2;
			x_max = x1;			
		}
		
		if(dy1>dy2) {
			y_min = y2;
			y_max = y1;			
		}

		vectors[0] = new Vector3D(x_min-x,y_min-y,0.0);
		vectors[1] = new Vector3D(x_max-x,y_max-y,0.0);
		
		return vectors;
	}

}
