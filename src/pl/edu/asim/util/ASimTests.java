package pl.edu.asim.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;

public class ASimTests {

	public void test(String command) {

		try {
			String temp = command.replaceAll("test ", "");
			int sp = temp.indexOf(" ");
			String nString = temp.substring(0, sp);
			String temp2 = temp.substring(sp + 1);
			sp = temp2.indexOf(" ");
			String rString = temp2.substring(0, sp);
			String zString = temp2.substring(sp + 1);

			int n = new BigInteger(nString).intValue();
			double z = new BigDecimal(zString).doubleValue();
			;
			double r = new BigDecimal(rString).doubleValue();

			double r_c = 2 * z * Math.sqrt((Math.log(n)) / (n * Math.PI));

			// System.out.println("r_c " + r_c);

			BigDecimal range = new BigDecimal(r);
			BigDecimal fullArea = new BigDecimal(z * z);
			BigDecimal BSArea = (range.pow(2))
					.multiply(new BigDecimal(Math.PI)).multiply(
							new BigDecimal(0.61));

			System.out.println("Powierzchnia kola: " + BSArea.doubleValue());

			BigDecimal TempArea = new BigDecimal(1).subtract(BSArea.divide(
					fullArea, BigDecimal.ROUND_HALF_UP));

			BigDecimal area = fullArea.subtract(fullArea.multiply(TempArea
					.pow(n - 1)));
			BigDecimal oneArea = area.divide(new BigDecimal(n - 1),
					BigDecimal.ROUND_HALF_UP);

			System.out.println("Pokrycie=" + area.doubleValue()
					+ " pokrycie jednostkowe=" + oneArea.doubleValue());

			System.out.println("Pokrycie2="
					+ fullArea.subtract(BSArea).doubleValue()
					+ " pokrycie jednostkowe="
					+ fullArea
							.subtract(BSArea)
							.divide(new BigDecimal(n - 1),
									BigDecimal.ROUND_HALF_UP).doubleValue());

			double l1 = Math.log(0.001);
			double l2 = Math.log(1 - BSArea.divide(fullArea,
					BigDecimal.ROUND_HALF_UP).doubleValue());
			BigDecimal count = new BigDecimal(1).add(new BigDecimal(l1 / l2));

			// BigDecimal count2 = new BigDecimal(1)
			// .add(new BigDecimal(l1 / l2));

			System.out.println("Count " + count.doubleValue());
			System.out.println("Percent "
					+ area.divide(fullArea, BigDecimal.ROUND_HALF_UP)
							.doubleValue());

			System.out
					.println("--------------------- Prawdopodobienstwo, ze SB jest izolowana");
			double PC = BSArea.doubleValue() / (z * z);
			double PI = 1 - PC;
			// double P1_w1 = Math.pow(PI, n - 1);
			// System.out.println("ze wzoru w ksiazce Kumar");
			// System.out.println("P1_w1 =" + P1_w1);
			double P1_w3 = ArithmeticUtils.binomialCoefficient(n - 1, n - 1)
					* Math.pow(PI, n - 1) * Math.pow(PC, n - 1 - n + 1);

			System.out.println("ze wzoru bernoulliego");
			System.out.println("P1_w3 =" + P1_w3);

			System.out
					.println("--------------------- Prawdopodobienstwo, ze jeden inny niz SB jest izolowany");

			PC = (Math.PI * r * r * (0.61)) / (z * z);
			PI = 1 - PC;

			// double PC2 = (Math.PI * r * r * 0.273) / (z * z);
			// double PI2 = 1 - PC2;

			double PC2 = (Math.PI * r * r * 0.25) / (z * z);
			double PI2 = 1 - PC2;

			double PC3 = (Math.PI * r * r) / (z * z);
			double PI3 = 1 - PC3;

			double P1_w6 = PI * Math.pow(PI2, n - 2);
			double P1_w7 = PI * Math.pow(PI3, n - 2);

			l1 = Math.log(0.001 / PI);
			l2 = Math.log(PI2);

			System.out.println("n = " + (2 + l1 / l2));

			System.out.println("ze wzoru bernoulliego");
			System.out.println(" max=" + P1_w6 + " min=" + P1_w7);

			for (int k = n - 1; k >= 0; k--) {

				// BigDecimal networkArea = BSArea.add(oneArea
				// .multiply(new BigDecimal(n - 1 - k)));
				// BigDecimal networkArea = BSArea
				// .add(BSArea.multiply(new BigDecimal(
				// (n - 1 - k) * 0.3)));

				// PC = ((Math.PI * r * r * (0.612)) + (Math.PI * r
				// * r * (n - 1 - k) * (0.273)))
				// / (z * z);
				// PI = 1 - PC;
				//
				// P1_w3 = ArithmeticUtils.binomialCoefficient(n -
				// 1,
				// k)
				// * Math.pow(PI, k)
				// * Math.pow(PC, n - 1 - k);

				// System.out.println(k
				// + " izolowane, ze wzoru bernoulliego "
				// + (0.612));
				// System.out.println("P1_w1 =" + P1_w3);

			}

			System.out
					.println("--------------------- Prawdopodobienstwo, ze conajmniej jeden jest izolowany");
			double P1_w1 = P1_w3 * n;
			System.out.println("P1_w1 =" + P1_w1);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void test3(String command) {

		try {

			String temp = command.replaceAll("test3 ", "");
			int sp = temp.indexOf(" ");
			String nString = temp.substring(0, sp);
			String temp2 = temp.substring(sp + 1);
			sp = temp2.indexOf(" ");
			String rString = temp2.substring(0, sp);
			String zString = temp2.substring(sp + 1);

			int n = new BigInteger(nString).intValue();
			double z = new BigDecimal(zString).doubleValue();
			;
			double r = new BigDecimal(rString).doubleValue();

			RandomTools rt = new RandomTools();

			rt.test2(r, z, n);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void test2(String command) {
		try {

			String temp = command.replaceAll("test2 ", "");
			int sp = temp.indexOf(" ");
			String nString = temp.substring(0, sp);
			String temp2 = temp.substring(sp + 1);
			sp = temp2.indexOf(" ");
			String rString = temp2.substring(0, sp);
			String zString = temp2.substring(sp + 1);

			int n = new BigInteger(nString).intValue();
			double z = new BigDecimal(zString).doubleValue();
			;
			double r = new BigDecimal(rString).doubleValue();

			RandomTools rt = new RandomTools();

			rt.test(r, z, n);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test4(String command) {
		try {

			String temp = command.replaceAll("test4 ", "");
			int K = new BigInteger(temp).intValue();

			double li = 0;
			double mi = 0;
			for (int l = 0; l < K; l++) {
				li = li + (K - l) / Math.pow(l + 1, 3);
			}
			for (int l = 0; l < K; l++) {
				mi = mi + (K - l) / Math.pow(l + 1, 2);
			}
			System.out.println(li / mi);
			System.out.println(mi / li);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void testNewton() {

		Vector3D centralPoint = new Vector3D(6, 5, 0);
		Vector3D point2 = new Vector3D(9, 8, 0);
		Vector3D point3 = new Vector3D(9, 1, 0);
		Vector3D point4 = new Vector3D(1, 3, 0);

		Double d2 = 2.0;
		Double d3 = 2.0;
		Double d4 = 2.0;

		Double sqNorm = 0.0;

		try {
			ArrayRealVector E = new ArrayRealVector(12);
			E.setEntry(0, 10.0);
			E.setEntry(3, 10.0);
			E.setEntry(6, 10.0);
			E.setEntry(9, 10.0);

			ArrayRealVector D = new ArrayRealVector(12);
			D.setEntry(0, 0);
			D.setEntry(3, 2.0);
			D.setEntry(6, 2.0);
			D.setEntry(9, 2.0);

			ArrayRealVector X = new ArrayRealVector(12);
			X.setSubVector(0, centralPoint.toArray());
			sqNorm = sqNorm + centralPoint.getNormSq();
			X.setSubVector(3, point2.toArray());
			sqNorm = sqNorm + point2.getNormSq();
			X.setSubVector(6, point3.toArray());
			sqNorm = sqNorm + point3.getNormSq();
			X.setSubVector(9, point4.toArray());
			sqNorm = sqNorm + point4.getNormSq();

			System.out.println("Wektor zmiennych X=" + X.toString()
					+ " wymiar=" + X.getDimension());
			System.out.println("Norma X=" + X.getNorm());
			System.out.println("Kwadrat normy X=" + sqNorm);

			Double energy = 0.0;
			for (int i = 3; i < X.getDimension(); i = i + 3) {
				System.out.println(X.getSubVector(0, 3) + " "
						+ X.getSubVector(i, 3));

				RealVector cooperativeNode = X.getSubVector(i, 3);
				double distance = X.getSubVector(0, 3).getDistance(
						cooperativeNode);

				System.out.println("d=" + distance);
				energy = energy + E.getEntry(0) * E.getEntry(i)
						* (D.getEntry(i) / distance - 1)
						* (D.getEntry(i) / distance - 1);
			}

			double hNorm = 1.0;
			while (hNorm > 0.00001) {
				// for (int a = 0; a < 10; a++) {

				ArrayRealVector F = new ArrayRealVector(12);
				energy = 0.0;

				for (int i = 3; i < X.getDimension(); i = i + 3) {
					RealVector cooperativeNode = X.getSubVector(i, 3);
					double distance = X.getSubVector(0, 3).getDistance(
							cooperativeNode);

					energy = energy + E.getEntry(0) * E.getEntry(i)
							* (D.getEntry(i) / distance - 1)
							* (D.getEntry(i) / distance - 1);

					double ft = 2 * ((D.getEntry(i) * D.getEntry(i))
							/ (distance * distance * distance) - D.getEntry(i)
							/ (distance * distance));

					F.setEntry(0, F.getEntry(0) + ft
							* (X.getEntry(0) - cooperativeNode.getEntry(0))
							/ distance);
					F.setEntry(1, F.getEntry(1) + ft
							* (X.getEntry(1) - cooperativeNode.getEntry(1))
							/ distance);
					F.setEntry(2, F.getEntry(2) + ft
							* (X.getEntry(2) - cooperativeNode.getEntry(2))
							/ distance);

					F.setEntry(i + 0,
							ft * (cooperativeNode.getEntry(0) - X.getEntry(0))
									/ distance);
					F.setEntry(i + 1,
							ft * (cooperativeNode.getEntry(1) - X.getEntry(1))
									/ distance);
					F.setEntry(i + 2,
							ft * (cooperativeNode.getEntry(2) - X.getEntry(2))
									/ distance);
				}
				double fNorm = F.getNorm();
				System.out.println("F=" + F + " " + fNorm);

				ArrayRealVector h = (ArrayRealVector) F.mapMultiply(energy
						/ (F.getNorm() * F.getNorm()));

				hNorm = h.getNorm();
				System.out.println("h=" + h + " " + h.getNorm());
				X = X.add(h);
				System.out.println("X=" + X);
				System.out.println("Energia=" + energy);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	ArrayRealVector getNewtonH() {
		return null;
	}

	public Vector3D getForceWithH(Double d, Vector3D myPoint,
			Vector3D influencePoint, Double mass) {
		Vector3D diff = influencePoint.subtract(myPoint);
		double distance = diff.getNorm();
		double step = (d - distance) * mass / distance;
		return diff.scalarMultiply(step);
	}

	public Vector3D getForce(Double d, Vector3D myPoint,
			Vector3D influencePoint, Double mass) {
		Vector3D diff = influencePoint.subtract(myPoint);
		double distance = diff.getNorm();

		if (distance == 0)
			return new Vector3D(0, 0, 0);

		Vector3D one = diff.scalarMultiply((mass) / distance);
		double a = (-2.0 * d * d) / (distance * distance * distance);
		double b = (-2.0 * d) / (distance * distance);
		return one.scalarMultiply(a - b);
	}

	public Double getH(Double d, Vector3D myPoint, Vector3D influencePoint,
			Double mass) {
		Vector3D diff = influencePoint.subtract(myPoint);
		double distance = diff.getNorm();

		return (distance * distance * distance / (2 * d));
	}

	public double getEnergy(Double d, Vector3D myPoint,
			Vector3D influencePoint, Double mass) {
		Vector3D diff = influencePoint.subtract(myPoint);
		double distance = diff.getNorm();
		double energy = (d / distance) - 1;
		energy = energy * energy;
		return energy;
	}

	public void testFastD() {

		Vector3D centralPoint = new Vector3D(6, 5, 0);
		Vector3D point2 = new Vector3D(9, 8, 0);
		Vector3D point3 = new Vector3D(9, 1, 0);
		Vector3D point4 = new Vector3D(1, 3, 0);

		Double d2 = 2.0;
		Double d3 = 2.0;
		Double d4 = 2.0;

		Double m2 = 10.0;
		Double m3 = 10.0;
		Double m4 = 10.0;
		Double ms = m2 + m3 + m4;

		// Vector3D s2 = getForceWithH(d2, centralPoint, point2, (1.0-(m2/ms)));
		// Vector3D s3 = getForceWithH(d3, centralPoint, point3, (1.0-(m3/ms)));
		// Vector3D s4 = getForceWithH(d4, centralPoint, point4, (1.0-(m4/ms)));

		Vector3D result = centralPoint.scalarMultiply(1.0);

		for (int i = 0; i < 10; i++) {

			Double energy = getEnergy(d2, result, point2, (1.0))
					+ getEnergy(d3, result, point3, (1.0))
					+ getEnergy(d4, result, point4, (1.0));
			System.out.println("Energy = " + energy);
			Double distance2 = result.subtract(point2).getNorm();
			System.out.println("Distance2 = " + distance2);
			Double distance3 = result.subtract(point3).getNorm();
			System.out.println("Distance3 = "+distance3);
			Double distance4 = result.subtract(point4).getNorm();
			System.out.println("Distance4 = "+distance4);

			Vector3D s2 = getForceWithH(d2, result, point2, (1.0));
			// System.out.println("Force2 ="+s2);
			Vector3D s3 = getForceWithH(d3, result, point3, (1.0));
			// System.out.println("Force2 ="+s3);
			Vector3D s4 = getForceWithH(d4, result, point4, (1.0));
			// System.out.println("Force2 ="+s4);

			Double H = (distance2-d2)/s2.getNorm()
					+ (distance3-d3)/s3.getNorm()
					+ (distance4-d4)/s4.getNorm();
			
			Double H2 = (distance2-d2)
					+ (distance3-d3)
					+ (distance4-d4);
			H2 = H2 / (s2.getNorm()+s3.getNorm()+s4.getNorm());
			
			Vector3D sumForce = s2.add(s3).add(s4);
			//H2 = H2/sumForce.getNorm(); 
			
			System.out.println("Force =" + sumForce + " H=" + H+" H2="+H2);

			result = result.subtract(sumForce);

			System.out.println("FD result = " + result);

			Double nEnergy = getEnergy(d2, result, point2, (1.0))
					+ getEnergy(d3, result, point3, (1.0))
					+ getEnergy(d4, result, point4, (1.0));
			System.out.println("--------------- nEnergy = " + nEnergy);
		}

	}
}
