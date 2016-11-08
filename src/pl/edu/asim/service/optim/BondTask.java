package pl.edu.asim.service.optim;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Metoda najszybszego spadku
 * 
 * @author andrzejs
 * 
 */

public class BondTask implements Task_Interface {

	Integer index;
	Vector3D location;
	ArrayList<Vector3D> influencePoints;
	ArrayList<Double> bonds;

	// ArrayList<PotentialSource> influencePoints = new
	// ArrayList<PotentialSource>();

	Vector3D gradient;
	Vector3D dk;
	int loop = 0;

	double tau = 0.02;
	int n = 3;
	double maxShift = 10;

	public BondTask(Integer index, ArrayList<Vector3D> influencePoints,
			ArrayList<Double> bonds) {
		this.bonds = bonds;
		this.influencePoints = influencePoints;
		this.index = index;
		location = influencePoints.get(index);
		gradient = new Vector3D(0, 0, 0);
	}

	public BondTask(Vector3D location) {
		this.setLocation(location);
		gradient = new Vector3D(0, 0, 0);
	}

	/* gradient */

	public Vector3D getForce() {
		Vector3D result = new Vector3D(0, 0, 0);
		for (int i = 0; i < influencePoints.size(); i++) {
			Vector3D diff = influencePoints.get(i).subtract(location);
			double distance = diff.getNorm();
			if (distance - bonds.get(i) == 0)
				continue;
			result = result.add(diff.scalarMultiply((distance - bonds.get(i))
					/ distance));
		}
		// System.out.println(index+" force ="+result.getNorm());
		return result;
	}

	// public Vector3D getForce() {
	// Vector3D result = new Vector3D(0, 0, 0);
	// for (int i = 0; i < influencePoints.size(); i++) {
	// result = result.add(getForce(location, influencePoints.get(i),
	// bonds.get(i)));
	// }
	// // System.out.println(index+" force ="+result.getNorm());
	// return result;
	// }

	public Vector3D getForce(Vector3D location, Vector3D influenceLocation,
			double d) {
		Vector3D diff = influenceLocation.subtract(location);
		double distance = diff.getNorm();
		if (distance == 0)
			return new Vector3D(0, 0, 0);
		Vector3D pre = diff.scalarMultiply(100);
		double a = d / distance - 1;
		double n = distance * distance * distance;
		return pre.scalarMultiply((-2) * d * a / n);
	}

	// public Vector3D getAdditionalForce(ArrayList<PotentialSource> addPoints)
	// {
	// Vector3D result = new Vector3D(0, 0, 0);
	// for (Iterator<PotentialSource> it = addPoints.iterator(); it.hasNext();)
	// {
	// PotentialSource ps = it.next();
	// result = result.add(ps.getForce(location));
	// }
	// return result;
	// }
	//
	// public Double getFullMass() {
	// Double result = 0.0;
	// for (Iterator<PotentialSource> it = influencePoints.iterator(); it
	// .hasNext();) {
	// PotentialSource ps = it.next();
	// result = result + ps.getMass();
	// }
	// return result;
	// }
	//
	// public double getEnergy() {
	// double result = 0;
	// for (Iterator<PotentialSource> it = influencePoints.iterator(); it
	// .hasNext();) {
	// PotentialSource ps = it.next();
	// result = result + ps.getEnergy(location);
	// }
	// return result;
	// }

	// public void calculateTau() {
	// double sum = 0;
	// for (int i = 0; i < influencePoints.size(); i++) {
	// PotentialSource ps = influencePoints.get(i);
	// sum = sum + ps.getMass();
	// }
	// tau = (15 * influencePoints.size()) / (sum);
	// }

	public boolean tick() {

		Vector3D newGr = getForce();

		if (gradient.getNorm() != 0
				&& gradient.normalize().add(newGr.normalize()).getNorm() < 0.1)
			tau = tau / 2;

		if (loop == 0 || loop > (n * 2) - 1) {
			dk = newGr; // .scalarMultiply(-1);
			gradient = newGr;
			loop = 0;
		} else {
			double beta = Vector3D.dotProduct(newGr, newGr.subtract(gradient))
					/ gradient.getNormSq();
			dk = newGr.add(dk.scalarMultiply(beta));
			gradient = newGr;
		}
		// System.out.println("Force="+newGr+" dk ="+dk+" loop="+loop+" tau="+tau);
		loop++;

		// System.out.println(Vector3D.dotProduct(newGr,newGr));
		if (gradient.getNorm() < 0.0001)
			return false;
		else {
			Vector3D shift = dk.scalarMultiply(tau);
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

	public ArrayList<Vector3D> getInfluencePoints() {
		return influencePoints;
	}

	public void setInfluencePoints(ArrayList<Vector3D> influencePoints) {
		this.influencePoints = influencePoints;
	}

	public void p1() {

		init();
		int i = 0;
		boolean run = true;

		while (run) {
			run = tick();
			if (i > 500)
				run = false;
			System.out.println(i + " Global " + location.toString());
			i++;
		}
	}

	@Override
	public void test() {
		System.out
				.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		// location = new Vector3D(0, 0, 0);
		// influencePoints = new ArrayList<Vector3D>();
		// PotentialSource ps1 = new PotentialSource(100, 30, 1000, new
		// Vector3D(
		// 100, 0, 0));
		// PotentialSource ps2 = new PotentialSource(100, 30, 1000, new
		// Vector3D(
		// 0, 100, 0));
		// PotentialSource ps3 = new PotentialSource(100, 30, 1000, new
		// Vector3D(
		// 100, 100, 0));
		// influencePoints.add(new Vector3D(
		// 100, 0, 0));
		// influencePoints.add(new Vector3D(
		// 0, 100, 0));
		// influencePoints.add(ps3);
		// p1();
		System.out
				.println("----------------------------------------------------------");
	}

	@Override
	public void init() {
		gradient = new Vector3D(0, 0, 0);
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
