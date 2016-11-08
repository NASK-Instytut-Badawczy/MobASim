package pl.edu.asim.service.optim;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class PotentialSource {
	Double mass;
	Double range = null;
	Double precision = null;
	Vector3D influenceLocation;

	Vector3D virtualInfluenceLocation;

	Vector3D nearInfluenceLocation;
	Vector3D farInfluenceLocation;

	boolean neutral = false;
	boolean cooperative = false;
	boolean inside = false;

	public Vector3D getInfluenceLocation() {
		return influenceLocation;
	}

	public void setInfluenceLocation(Vector3D influenceLocation) {
		this.influenceLocation = influenceLocation;
	}

	Double d;
	Double d_min;
	Double accuracy_X = 0.0;
	Double accuracy_Y = 0.0;
	Double accuracy_Z = 0.0;
	Double velocity = 0.0;

	public Double getAccuracy_X() {
		return accuracy_X;
	}

	public void setAccuracy_X(Double accuracy_X) {
		this.accuracy_X = accuracy_X;
	}

	public Double getAccuracy_Y() {
		return accuracy_Y;
	}

	public void setAccuracy_Y(Double accuracy_Y) {
		this.accuracy_Y = accuracy_Y;
	}

	public Double getAccuracy_Z() {
		return accuracy_Z;
	}

	public void setAccuracy_Z(Double accuracy_Z) {
		this.accuracy_Z = accuracy_Z;
	}

	public PotentialSource(double mass, double d, double range,
			double precision, Vector3D influenceLocation) {
		this.setMass(mass);
		this.setRange(range);
		this.setPrecision(precision);
		this.influenceLocation = influenceLocation;
		this.setD(d);
	}

	public Vector3D getForce(Vector3D location) {

		if (neutral && inside) {
			return new Vector3D(0, 0, 0);
		}

		double distance = 0;
		Vector3D diff = new Vector3D(0, 0, 0);

		if (virtualInfluenceLocation == null) {
			diff = influenceLocation.subtract(location);
			distance = diff.getNorm();
		} else {
			diff = virtualInfluenceLocation.subtract(location);
			distance = diff.getNorm();
		}

		if (range != null && range < distance)
			return new Vector3D(0, 0, 0);
		if (precision != null && (d - precision) <= distance
				&& (d + precision) >= distance)
			return new Vector3D(0, 0, 0);
		if (distance == 0)
			return new Vector3D(0, 0, 0);

		Vector3D one = diff.scalarMultiply(((-2) * mass) / distance);
		double a = (d * d) / (distance * distance * distance);
		double b = (d) / (distance * distance);
		return one.scalarMultiply(a - b);
	}

	public double getEnergy(Vector3D location) {

		if (neutral && inside) {
			return 0.0;
		}

		double distance = 0;
		Vector3D diff = new Vector3D(0, 0, 0);

		if (virtualInfluenceLocation == null) {
			diff = influenceLocation.subtract(location);
			distance = diff.getNorm();
		} else {
			diff = virtualInfluenceLocation.subtract(location);
			distance = diff.getNorm();
		}

		if (range != null && range < distance)
			return 0.0;
		if (precision != null && (d - precision) <= distance
				&& (d + precision) >= distance)
			return 0.0;
		if (distance == 0)
			return 0.0;

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

	public Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Double velocity) {
		this.velocity = velocity;
	}

	public Double getDistance(Vector3D location) {
		if (neutral)
			return influenceLocation.subtract(location).getNorm();
		else if (cooperative)
			return nearInfluenceLocation.subtract(location).getNorm();
		else
			return farInfluenceLocation.subtract(location).getNorm();
	}

	public Double getDistance_old(Vector3D location) {
		Vector3D diff = influenceLocation.subtract(location);

		if (virtualInfluenceLocation != null)
			diff = virtualInfluenceLocation.subtract(location);
		return diff.getNorm();
	}

	public void calculateVirtualInfluenceLocation(Vector3D location) {

		Vector3D[] vectors = new Vector3D[2];

		double x = location.getX();
		double y = location.getY();

		double x1 = influenceLocation.getX() - accuracy_X;
		double x2 = influenceLocation.getX() + accuracy_X;

		double y1 = influenceLocation.getY() - accuracy_Y;
		double y2 = influenceLocation.getY() + accuracy_Y;

		this.inside = (x >= x1 && x <= x2 && y >= y1 && y <= y2);

		// System.out.println("x = " + x); System.out.println("y = " + y);
		// System.out.println("x1 = " + x1); System.out.println("x2 = " + x2);
		// System.out.println("y1 = " + y1); System.out.println("y2 = " + y2);

		double x_min = x1;
		double x_max = x2;
		double y_min = y1;
		double y_max = y2;

		double dx1 = (x1 - x) * (x1 - x);
		double dx2 = (x2 - x) * (x2 - x);

		double dy1 = (y1 - y) * (y1 - y);
		double dy2 = (y2 - y) * (y2 - y);

		if (dx1 > dx2) {
			x_min = x2;
			x_max = x1;
		}

		if (dy1 > dy2) {
			y_min = y2;
			y_max = y1;
		}

		vectors[0] = new Vector3D(x_min, y_min, 0.0);
		vectors[1] = new Vector3D(x_max, y_max, 0.0);

		nearInfluenceLocation = vectors[0];
		farInfluenceLocation = vectors[1];

		// System.out.println("inside="+inside+" neutral="+neutral+" cooperative="+cooperative);

		if (neutral) {
			virtualInfluenceLocation = influenceLocation;
			// } else if (inside && cooperative) {
			// virtualInfluenceLocation = influenceLocation;
			// } else if (inside && !cooperative) {
			// virtualInfluenceLocation = vectors[1];
		} else if (!cooperative) {
			virtualInfluenceLocation = vectors[1];
		} else
			virtualInfluenceLocation = vectors[0];

		// if(neutral) {
		// virtualInfluenceLocation = influenceLocation;
		// } else if (inside || !cooperative)
		// virtualInfluenceLocation = vectors[1];
		// else
		// virtualInfluenceLocation = vectors[0];
	}

	public boolean isCooperative() {
		return cooperative;
	}

	public void setCooperative(boolean cooperative) {
		this.cooperative = cooperative;
	}

	public boolean isNeutral() {
		return neutral;
	}

	public void setNeutral(boolean neutral) {
		this.neutral = neutral;
	}

	public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

}
