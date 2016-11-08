package pl.edu.asim.service.optim;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 * Metoda gradientów sprzężonych z maksymalnym zasięgiem i ograniczeniami na x,
 * y lub z.
 * 
 * @author andrzejs
 * 
 */
public class LocalTaskCRR implements Task_Interface {

	Integer index;
	Vector3D location;
	Vector3D startLocation;

	ArrayList<PotentialSource> influencePoints = new ArrayList<PotentialSource>();
	// private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

	ArrayRealVector gradient;
	ArrayRealVector dk;

	int loop = 0;

	double tau = 1.0;
	int n;
	double maxShift = 1;

	Double xConstraint;
	Double yConstraint;
	Double zConstraint;
	Double maxRange;

	public LocalTaskCRR() {
	}

	public LocalTaskCRR(Vector3D location) {
		this.setLocation(location);
		this.setStartLocation(new Vector3D(location.getX(), location.getY(),
				location.getZ()));
		gradient = new ArrayRealVector(6);
	}

	/* gradient */

	public Vector3D getForce() {
		Vector3D result = new Vector3D(0, 0, 0);
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			Vector3D f = ps.getForce(location);
			// System.out.println(f.toString());
			result = result.add(f);
		}
		return result;
	}

	public Double getFullMass() {
		Double result = 0.0;
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			result = result + ps.getMass();
		}
		return result;
	}

	public double getEnergy() {
		double result = 0;
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			result = result + ps.getEnergy(location);
		}
		return result;
	}

	public double getEnergy(Vector3D location) {
		double result = 0;
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			result = result + ps.getEnergy(location);
		}
		return result;
	}

	public void calculateTau() {
		double sum = 0;
		for (int i = 0; i < influencePoints.size(); i++) {
			PotentialSource ps = influencePoints.get(i);
			sum = sum + ps.getMass();
		}
		tau = (500 * influencePoints.size()) / (sum);
	}

	public Vector3D getLocation() {
		return location;
	}

	public void setLocation(Vector3D location) {
		this.location = location;
	}

	public ArrayList<PotentialSource> getInfluencePoints() {
		return influencePoints;
	}

	public void setInfluencePoints(ArrayList<PotentialSource> influencePoints) {
		this.influencePoints = influencePoints;
	}

	@Override
	public void init() {
		gradient = new ArrayRealVector(7);
		// gradient_old = new Vector3D(0, 0, 0);
		// lambdas = new Vector3D(1, 1, 1);
		n = 3;
		// if (xConstraint != null)
		// n++;
		// if (yConstraint != null)
		// n++;
		// if (zConstraint != null)
		// n++;
		// if (maxRange != null)
		// n++;
		calculateTau();
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public double getXConstraint() {
		return xConstraint;
	}

	public void setXConstraint(double xConstraint) {
		this.xConstraint = xConstraint;
	}

	public double getYConstraint() {
		return yConstraint;
	}

	public void setYConstraint(double yConstraint) {
		this.yConstraint = yConstraint;
	}

	public double getZConstraint() {
		return zConstraint;
	}

	public void setZConstraint(double zConstraint) {
		this.zConstraint = zConstraint;
	}

	public Vector3D getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Vector3D startLocation) {
		this.startLocation = startLocation;
	}

	public Double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}

	public void p1() {

		init();
		int i = 0;
		boolean run = true;

		while (run) {
			run = // tick();
			tick();
			if (i > 3000)
				run = false;
			// System.out.println(i + " Global " + location.toString()
			// + " Energy " + getEnergy() + " " + tau + " distance="
			// + Vector3D.distance(startLocation, location));
			// System.out.println(i + " Global " + location.toString() +
			// " Energy " +
			// getEnergy()+" "+tau+" "+gradient_old.toString()+" "+dk_old.toString());
			i++;
		}
	}

	public void p2() {

		init();
		double S = maxRange.doubleValue() / 100;

		for (int i = 0; i < 100; i++) {
			Vector3D force = getForce();
			double norm = force.getNorm();
			if (norm > 0) {
				force = force.scalarMultiply(S / norm);
				location = location.add(force);
			}
		}
	}

	public boolean tick() {

		Vector3D force = getForce();

		// if (this.xConstraint != null) {
		// double diff = this.xConstraint - this.location.getX();
		// Vector3D predShift = force.scalarMultiply(tau);
		// predShift = new Vector3D(diff, predShift.getY(), predShift.getZ());
		// force = predShift.scalarMultiply(1 / tau);
		// }
		// if (this.yConstraint != null) {
		// double diff = this.yConstraint - this.location.getY();
		// Vector3D predShift = force.scalarMultiply(tau);
		// predShift = new Vector3D(predShift.getX(), diff, predShift.getZ());
		// force = predShift.scalarMultiply(1 / tau);
		// }
		// if (this.zConstraint != null) {
		// double diff = this.zConstraint - this.location.getZ();
		// Vector3D predShift = force.scalarMultiply(tau);
		// predShift = new Vector3D(predShift.getX(), predShift.getY(), diff);
		// force = predShift.scalarMultiply(1 / tau);
		// }

		if (maxRange != null) {
			Vector3D predShift = force.scalarMultiply(tau);
			Vector3D predLocation = location.add(predShift);

			Vector3D r = predLocation.subtract(startLocation);
			if (r.getNorm() > maxRange) {
				// zmniejszamy r;
				r = r.normalize().scalarMultiply(maxRange);
				Vector3D newShift = r.add(startLocation).subtract(location);
				force = newShift.scalarMultiply(1 / tau);
			}
		}

		ArrayRealVector newGr = OptimUtil.toArrayRealVector(force);

		// if (gradient.getNorm() != 0 && newGr.getNorm() != 0)
		// System.out.println("!!!!!! " + gradient.getNorm() + " "
		// + newGr.getNorm() + " " + gradient.add(newGr).getNorm());
		// if (gradient.getNorm() != 0 && newGr.getNorm() != 0
		// && gradient.add(newGr).getNorm() < 0.1) {
		// tau = tau / 2;
		// }

		// if (true) {
		if (loop == 0 || loop > (n * n) - 1) {
			dk = newGr;
			gradient = newGr;
			loop = 0;
		} else {
			double beta = newGr.dotProduct(newGr.subtract(gradient))
					/ (gradient.getNorm() * gradient.getNorm());

			dk = newGr.add(dk.mapMultiply(beta));
			gradient = newGr;
		}
		loop++;
		if (gradient.getNorm() < 0.000001)
			return false;
		else {
			ArrayRealVector shift = (ArrayRealVector) dk.mapMultiply(tau);
			double s = OptimUtil.toVector3D(shift).getNorm();
			if (s > maxShift) {
				shift = (ArrayRealVector) shift.mapMultiply(maxShift / s);
			}

			location = location.add(OptimUtil.toVector3D(shift));
			return true;
		}
	}

	@Override
	public void test() {
		// System.out
		// .println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// location = new Vector3D(0, 0, 0);
		// setStartLocation(new Vector3D(0, 0, 0));
		// // location_old = new Vector3D(0, 0, 0);
		// // this.maxRange = 10.0;
		// // this.xConstraint = 0.0;
		// // this.zConstraint = 0.0;
		//
		// influencePoints = new ArrayList<PotentialSource>();
		// PotentialSource ps1 = new PotentialSource(100, 30, 1000, new
		// Vector3D(
		// 100, 0, 0));
		// PotentialSource ps2 = new PotentialSource(100, 50, 1000, new
		// Vector3D(
		// 0, 100, 0));
		// PotentialSource ps3 = new PotentialSource(100, 30, 1000, new
		// Vector3D(
		// 100, 100, 0));
		// influencePoints.add(ps1);
		// influencePoints.add(ps2);
		// // influencePoints.add(ps3);
		// p1();
		// System.out
		// .println("----------------------------------------------------------");
	}

	// public ArrayList<Obstacle> getObstacles() {
	// return obstacles;
	// }
	//
	// public void setObstacles(ArrayList<Obstacle> obstacles) {
	// this.obstacles = obstacles;
	// }

}
