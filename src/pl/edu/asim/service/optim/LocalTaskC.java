package pl.edu.asim.service.optim;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 * Metoda gradientu sprzężonego
 * 
 * @author andrzejs
 * 
 */
public class LocalTaskC implements Task_Interface {

	Integer index;
	Vector3D location;
	Vector3D lambdas;
	ArrayList<PotentialSource> influencePoints = new ArrayList<PotentialSource>();

	ArrayRealVector gradient;
	ArrayRealVector dk;

	Vector3D gradient_old;
	Vector3D dk_old;
	Vector3D location_old;

	int loop = 0;
	int loop_old = 0;

	double tau = 1.0;
	int n = 3;
	double maxShift = 10;

	Double xConstraint;
	Double yConstraint;
	Double zConstraint;

	public LocalTaskC() {
	}

	public LocalTaskC(Vector3D location) {
		this.setLocation(location);
		gradient = new ArrayRealVector(6);
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
		tau = (15 * influencePoints.size()) / (sum);
	}

	public boolean tick() {

		Vector3D force = getForce();
		force = force.add(this.getForceFromConstraints());

		Vector3D newLambdas = this.getLambdasGradient();
		ArrayRealVector newGr = OptimUtil.toArrayRealVector(force, newLambdas);

		if (gradient.getNorm() != 0
				&& gradient.unitVector().add(newGr.unitVector()).getNorm() < 0.1)
			tau = tau / 2;

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

	public boolean tick2() {

		Vector3D newGr = getForce();

		if (gradient_old.getNorm() != 0
				&& gradient_old.normalize().add(newGr.normalize()).getNorm() < 0.1)
			tau = tau / 2;

		if (loop_old == 0 || loop_old > (n * 4) - 1) {
			dk_old = newGr; // .scalarMultiply(-1);
			gradient_old = newGr;
			loop_old = 0;
		} else {
			double beta = Vector3D.dotProduct(newGr,
					newGr.subtract(gradient_old))
					/ gradient_old.getNormSq();
			dk_old = newGr.add(dk_old.scalarMultiply(beta));
			gradient_old = newGr;
		}
		loop_old++;

		// System.out.println(Vector3D.dotProduct(newGr,newGr));
		if (gradient_old.getNorm() < 0.0001)
			return false;
		else {
			Vector3D shift = dk_old.scalarMultiply(tau);
			double s = shift.getNorm();
			if (s > maxShift) {
				shift = shift.scalarMultiply(maxShift / s);
			}
			location = location.add(shift);
			return true;
		}
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

	public void p1() {

		init();
		int i = 0;
		boolean run = true;

		while (run) {
			run = // tick();
			tick();
			if (i > 200)
				run = false;
			System.out.println(i + " Global " + location.toString()
					+ " Energy " + getEnergy() + " " + tau + " "
					+ gradient.toString() + " " + dk.toString());
			// System.out.println(i + " Global " + location.toString() +
			// " Energy " +
			// getEnergy()+" "+tau+" "+gradient_old.toString()+" "+dk_old.toString());
			i++;
		}
	}

	public void p12() {

		init();
		int i = 0;
		boolean run = true;

		while (run) {
			run = // tick();
			tick2();
			if (i > 200)
				run = false;
			// System.out.println(i + " Global " + location.toString() +
			// " Energy " +
			// getEnergy()+" "+tau+" "+gradient.toString()+" "+dk.toString());
			System.out.println(i + " Global " + location.toString()
					+ " Energy " + getEnergy() + " " + tau + " "
					+ gradient_old.toString() + " " + dk_old.toString());
			i++;
		}
	}

	@Override
	public void test() {
		// System.out
		// .println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// location = new Vector3D(0, 0, 0);
		// location_old = new Vector3D(0, 0, 0);
		//
		// this.xConstraint = 76.0;
		// this.zConstraint = 5.0;
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
		// System.out
		// .println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// location = new Vector3D(0, 0, 0);
		// location_old = new Vector3D(0, 0, 0);
		//
		// // this.xConstraint = 50.0;
		//
		// influencePoints = new ArrayList<PotentialSource>();
		// influencePoints.add(ps1);
		// influencePoints.add(ps2);
		// // influencePoints.add(ps3);
		// // p12();
		// System.out
		// .println("----------------------------------------------------------");
	}

	@Override
	public void init() {
		gradient = new ArrayRealVector(6);
		gradient_old = new Vector3D(0, 0, 0);
		lambdas = new Vector3D(1, 1, 1);
		n = 3;
		if (xConstraint != null)
			n++;
		if (yConstraint != null)
			n++;
		if (zConstraint != null)
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
}
