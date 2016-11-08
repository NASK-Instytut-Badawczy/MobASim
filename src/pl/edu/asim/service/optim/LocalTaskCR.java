package pl.edu.asim.service.optim;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 * Metoda gradientów sprzężonych z maksymalnym zasięgiem
 * 
 * @author andrzejs
 * 
 */
public class LocalTaskCR implements Task_Interface {

	Integer index;
	Vector3D location;
	Vector3D startLocation;
	Vector3D lambdas;
	ArrayList<PotentialSource> influencePoints = new ArrayList<PotentialSource>();

	ArrayRealVector gradient;
	ArrayRealVector dk;

	Vector3D gradient_old;
	Vector3D dk_old;

	int loop = 0;
	int loop_old = 0;

	double tau = 1.0;
	int n;
	double maxShift = 10;

	Double xConstraint;
	Double yConstraint;
	Double zConstraint;
	Double maxRange;

	public LocalTaskCR() {
	}

	public LocalTaskCR(Vector3D location) {
		this.setLocation(location);
		this.setStartLocation(new Vector3D(location.getX(), location.getY(),
				location.getZ()));
		// gradient = new ArrayRealVector(6);
	}

	/* gradient */

	public Vector3D getForce() {
		Vector3D result = new Vector3D(0, 0, 0);
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			result = result.add(ps.getForce(location));
		}
		return result;
	}

	public Vector3D getForceFromConstraints() {
		double x = (xConstraint != null) ? lambdas.getX() : 0;
		double y = (yConstraint != null) ? lambdas.getY() : 0;
		double z = (zConstraint != null) ? lambdas.getZ() : 0;
		Vector3D result = new Vector3D(x, y, z);
		return result;
	}

	// public Vector3D getForceFromRangeConstraint() {
	// if (maxRange == null) return new Vector3D(0,0,0);
	// double dx = (location.getX() - startLocation.getX());
	// double dy = (location.getY() - startLocation.getY());
	// double dz = (location.getZ() - startLocation.getZ());
	// Vector3D result = new Vector3D(2*lambdaR*dx, 2*lambdaR*dy, 2*lambdaR*dz);
	// return result;
	// }

	public Vector3D getLambdasGradient() {
		double lx = (xConstraint != null) ? -(location.getX() - xConstraint)
				: 0;
		double ly = (yConstraint != null) ? -(location.getY() - yConstraint)
				: 0;
		double lz = (zConstraint != null) ? -(location.getZ() - zConstraint)
				: 0;

		Vector3D result = new Vector3D(lx, ly, lz);
		return result;
	}

	// public double getLambdaRGradient() {
	// if (maxRange == null) return 0.0;
	// double dx = (location.getX() - startLocation.getX());
	// double dy = (location.getY() - startLocation.getY());
	// double dz = (location.getZ() - startLocation.getZ());
	// return -(dx*dx + dy*dy + dz*dz - maxRange*maxRange);
	// }

	// public Vector3D getAdditionalForce(ArrayList<PotentialSource> addPoints)
	// {
	// Vector3D result = new Vector3D(0, 0, 0);
	// for (Iterator<PotentialSource> it = addPoints.iterator(); it.hasNext();
	// ) {
	// PotentialSource ps = it.next();
	// result = result.add(ps.getForce(location));
	// }
	// return result;
	// }

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

	public void calculateTau() {
		double sum = 0;
		for (int i = 0; i < influencePoints.size(); i++) {
			PotentialSource ps = influencePoints.get(i);
			sum = sum + ps.getMass();
		}
		tau = (1000 * influencePoints.size()) / (sum);
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

	// public void p12() {
	//
	// init();
	// int i = 0;
	// boolean run = true;
	//
	// while (run) {
	// run = //tick();
	// tick2();
	// if (i > 500)
	// run = false;
	// // System.out.println(i + " Global " + location.toString() +
	// // " Energy " +
	// getEnergy()+" "+tau+" "+gradient.toString()+" "+dk.toString());
	// System.out.println(i + " Global " + location.toString() +
	// " Energy " + getEnergy() + " " + tau + " " +
	// gradient_old.toString() + " " +
	// dk_old.toString());
	// i++;
	// }
	// }

	@Override
	public void init() {
		// System.out.println(" maxRange=" + maxRange + " location=" + location
		// + " " + influencePoints.get(0).getInfluenceLocation()
		// + " Energy=" + getEnergy());
		gradient = new ArrayRealVector(7);
		gradient_old = new Vector3D(0, 0, 0);
		lambdas = new Vector3D(1, 1, 1);
		n = 3;
		if (xConstraint != null)
			n++;
		if (yConstraint != null)
			n++;
		if (zConstraint != null)
			n++;
		if (maxRange != null)
			n++;
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
			if (i > 300)
				run = false;
			System.out.println(i + " Location=" + location.toString()
					+ " Energy=" + getEnergy() + " tau=" + tau + " distance="
					+ Vector3D.distance(startLocation, location));
			// System.out.println(i + " Global " + location.toString() +
			// " Energy " +
			// getEnergy()+" "+tau+" "+gradient_old.toString()+" "+dk_old.toString());
			i++;
		}
	}

	public boolean tick() {

		Vector3D force = getForce();
		force = force.add(this.getForceFromConstraints());
		System.out.println("force=" + force);
		if (maxRange != null) {
			Vector3D predShift = force.scalarMultiply(tau);
			Vector3D predLocation = location.add(predShift);

			Vector3D r = predLocation.subtract(startLocation);
			if (r.getNorm() > maxRange) {
				System.out.println("PredShift=" + predShift);
				System.out.println("PredLocation=" + predLocation);
				System.out.println("Distance=" + r.getNorm());
				System.out.println("r=" + r.toString());
				// zmniejszamy r;
				r = r.normalize().scalarMultiply(maxRange);
				Vector3D newShift = r.add(startLocation).subtract(location);
				System.out.println("Preparing scale r=" + r.toString() + " "
						+ r.getNorm() + " " + newShift.toString());
				force = newShift.scalarMultiply(1 / tau);
			}
		}

		Vector3D newLambdas = this.getLambdasGradient();
		ArrayRealVector newGr = OptimUtil.toArrayRealVector(force, newLambdas);

		if (gradient.getNorm() != 0
				&& gradient.unitVector().add(newGr.unitVector()).getNorm() < 0.1)
			tau = tau / 2;

		if (true) {
			// if (loop == 0 || loop > (n*n) - 1) {
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
		if (gradient.getNorm() < 0.0001)
			return false;
		else {
			// Vector3D shift = OptimUtil.toVector3D((ArrayRealVector)
			// dk.mapMultiplyToSelf(tau));
			// double s = shift.getNorm();
			// if (s > maxShift) {
			// shift = shift.scalarMultiply(maxShift / s);
			// }
			ArrayRealVector shift = (ArrayRealVector) dk.mapMultiply(tau);
			double s = OptimUtil.toVector3D(shift).getNorm();
			if (s > maxShift) {
				shift = (ArrayRealVector) shift.mapMultiply(maxShift / s);
			}

			location = location.add(OptimUtil.toVector3D(shift));
			lambdas = lambdas.add(OptimUtil.toVector3D((OptimUtil
					.toLambdasVector(shift))));
			return true;
		}
	}

	@Override
	public void test() {
		System.out
				.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out
				.println("----------------------------------------------------------");
	}

}
