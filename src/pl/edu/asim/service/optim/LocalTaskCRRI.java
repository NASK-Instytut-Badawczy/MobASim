package pl.edu.asim.service.optim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.Logger;

/**
 * Metoda gradientowa z uwzględnionym d w formie przedziału,
 * 
 * @author andrzejs
 * 
 */
public class LocalTaskCRRI implements Task_Interface {

	Integer index;
	Vector3D location;
	Vector3D startLocation;

	ArrayList<PotentialSource> influencePoints = new ArrayList<PotentialSource>();

	Double velocity;
	Double dt;
	Double myMass = 0.0;
	Double precision;

	String name;
	Logger logger;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public LocalTaskCRRI() {
	}

	public Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Double velocity) {
		this.velocity = velocity;
	}

	public Double getDt() {
		return dt;
	}

	public void setDt(Double dt) {
		this.dt = dt;
	}

	public Double getMyMass() {
		return myMass;
	}

	public void setMyMass(Double myMass) {
		this.myMass = myMass;
	}

	public LocalTaskCRRI(Vector3D location) {
		this.setLocation(location);
		this.setStartLocation(new Vector3D(location.getX(), location.getY(),
				location.getZ()));
	}

	/* gradient */

	public Vector3D getForce() {
		Vector3D result = new Vector3D(0, 0, 0);
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			Vector3D f = ps.getForce(location);
			result = result.add(f);
		}
		return result;
	}

	// public Vector3D[] getForceWithInterval() {
	// Vector3D[] result = new Vector3D[2];
	// result[0] = new Vector3D(0, 0, 0);
	// result[1] = new Vector3D(0, 0, 0);
	//
	// for (Iterator<PotentialSource> it = influencePoints.iterator(); it
	// .hasNext();) {
	// PotentialSource ps = it.next();
	// Vector3D[] f = ps.getForceWithInterval(location);
	// result[0] = result[0].add(f[0]);
	// result[1] = result[1].add(f[1]);
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

	public double getEnergy(Vector3D location) {
		double result = 0;
		for (Iterator<PotentialSource> it = influencePoints.iterator(); it
				.hasNext();) {
			PotentialSource ps = it.next();
			result = result + ps.getEnergy(location);
		}
		return result;
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
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Vector3D getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Vector3D startLocation) {
		this.startLocation = startLocation;
	}

	public void calculate() {

		init();
		double S = (velocity * dt) / 100.0;

		for (int i = 0; i < 100; i++) {
			Vector3D force = getForce();
			double norm = force.getNorm();
			if (norm > 0) {
				force = force.scalarMultiply(S / norm);
				location = location.add(force);
			}
		}
	}

	public void calculateWithInterval() {

		init();
		double sum = 0;
		double S = new BigDecimal(dt).divide(new BigDecimal(100.0))
				.doubleValue();
		double tr = 0.000001;
		BigDecimal norm = new BigDecimal(1);
		double path = new BigDecimal(velocity).multiply(new BigDecimal(dt))
				.doubleValue();
		Vector3D force = new Vector3D(0, 0, 0);

		while (norm.doubleValue() > tr && sum < path) {
			force = new Vector3D(0, 0, 0);
			for (Iterator<PotentialSource> it = influencePoints.iterator(); it
					.hasNext();) {
				PotentialSource ps = it.next();
				Vector3D f = ps.getForce(location);
				force = force.add(f);
			}
			norm = new BigDecimal(force.getNorm());
			if (norm.doubleValue() == 0)
				continue;
			BigDecimal a1 = new BigDecimal(S * velocity);
			force = force.scalarMultiply(a1.divide(norm,
					BigDecimal.ROUND_HALF_UP).doubleValue());
			sum = sum + a1.doubleValue();
			if (sum > path)
				break;

			location = location.add(force);
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
		// System.out
		// .println("----------------------------------------------------------");
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
