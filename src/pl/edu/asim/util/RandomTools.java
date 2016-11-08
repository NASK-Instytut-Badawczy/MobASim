package pl.edu.asim.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.random.RandomDataGenerator;

public class RandomTools {

	public static RandomTools INSTANCE = new RandomTools();
	public static Random randomJDK = new Random();

	String name;
	// private double mean;
	// private double stdDev;
	// private double variance;
	// private long seed;
	RandomDataGenerator random;

	public RandomTools() {
		random = new RandomDataGenerator();
	}

	public static double sampleNormal(double mean, double variance) {
		if (INSTANCE == null)
			INSTANCE = new RandomTools();
		return INSTANCE.random.nextGaussian(mean, Math.sqrt(variance));
	}

	public static double sampleUniform(double lower, double upper) {
		if (INSTANCE == null)
			INSTANCE = new RandomTools();
		return INSTANCE.random.nextUniform(lower, upper);
	}

	public static Vector3D getRandomPoint(double width, double height) {
		double x = (randomJDK.nextDouble() * width);
		double y = (randomJDK.nextDouble() * height);
		double z = (0.0);
		Vector3D result = new Vector3D(x, y, z);
		return result;
	}

	public static String getRandomString(int lenght) {
		return INSTANCE.random.nextSecureHexString(lenght);
	}

	public void test2(double r, double z, double n) {

		System.out.println(n + " " + r + " " + z);
		double is = 0;
		double co = 0;
		double avgDistance = 0;

		ArrayList<Integer> counter = new ArrayList<Integer>();
		int a = 0;
		for (a = 0; a < 100000; a++) {

			ArrayList<Vector3D> result = new ArrayList<Vector3D>();
			ArrayList<ArrayList<Integer>> conn = new ArrayList<ArrayList<Integer>>();

			Vector3D v = new Vector3D(60.5, 85.5, 0.0);
			result.add(v);
			ArrayList<Integer> c = new ArrayList<Integer>();
			conn.add(c);
			counter.add(new Integer(0));
			int i = 0;
			for (i = 1; i < n; i++) {
				v = new Vector3D(random.nextUniform(0.0, z),
						random.nextUniform(0.0, z), 0.0);
				result.add(v);
				// System.out.println(i + " " + v);
				c = new ArrayList<Integer>();
				conn.add(c);
				counter.add(new Integer(0));
			}

			double temp_avgDistance = 0;
			i = 0;

			v = result.get(i);
			c = conn.get(i);
			int dim = 0;
			double temp_avgDistance2 = 0;
			int ii = 0;
			for (ii = 0; ii < n; ii++) {

				if (i == ii)
					continue;
				Vector3D vv = result.get(ii);
				double distance = v.distance(vv);
				if (distance <= r) {
					// System.out.println("distance " + i + " " + ii + " "
					// + distance);
					c.add(ii);
				}
				dim++;
				temp_avgDistance2 = temp_avgDistance2 + distance;
			}

			if (dim > 0) {
				avgDistance = avgDistance + temp_avgDistance2 / dim;
			}

			HashSet<Integer> ask = new HashSet<Integer>();
			ask.add(0);
			HashSet<Integer> connected = isConnected(0, conn, ask);
			if (n - connected.size() == 0)
				co++;
			else {
				is++;
			}
			int t = (int) n - connected.size();
			Integer t2 = counter.get(t);
			t2++;
			counter.set(t, t2);
		}
		avgDistance = avgDistance / 100000;

		System.out.println("co=" + co + " is=" + is + " percent="
				+ (co / (co + is)) + " avgDistance=" + avgDistance);
		for (int t = 0; t < n; t++) {
			System.out.print(" "
					+ ((double) counter.get(new Integer(t)) / 100000) + " ");
		}
	}

	public void test(double r, double z, double n) {

		System.out.println(n + " " + r + " " + z);
		double is = 0;
		double co = 0;
		double avgDistance = 0;

		ArrayList<Integer> counter = new ArrayList<Integer>();
		int a = 0;
		for (a = 0; a < 100000; a++) {

			ArrayList<Vector3D> result = new ArrayList<Vector3D>();
			ArrayList<ArrayList<Integer>> conn = new ArrayList<ArrayList<Integer>>();

			Vector3D v = new Vector3D(60.5, 85.5, 0.0);
			result.add(v);
			ArrayList<Integer> c = new ArrayList<Integer>();
			conn.add(c);
			counter.add(new Integer(0));
			int i = 0;
			for (i = 1; i < n; i++) {
				v = new Vector3D(random.nextUniform(0.0, z),
						random.nextUniform(0.0, z), 0.0);
				result.add(v);
				// System.out.println(i + " " + v);
				c = new ArrayList<Integer>();
				conn.add(c);
				counter.add(new Integer(0));
			}

			double temp_avgDistance = 0;
			int tmp_dim = 0;
			i = 0;
			for (i = 0; i < n; i++) {
				v = result.get(i);
				c = conn.get(i);
				int dim = 0;
				double temp_avgDistance2 = 0;
				int ii = 0;
				for (ii = 0; ii < n; ii++) {

					if (i == ii)
						continue;
					Vector3D vv = result.get(ii);
					double distance = v.distance(vv);
					if (distance <= r) {
						// System.out.println("distance " + i + " " + ii + " "
						// + distance);
						dim++;
						c.add(ii);
						temp_avgDistance2 = temp_avgDistance2 + distance;
					}
				}
				if (dim > 0) {
					temp_avgDistance = temp_avgDistance + temp_avgDistance2
							/ dim;
					tmp_dim++;
				}
			}

			if (tmp_dim > 0) {
				avgDistance = avgDistance + temp_avgDistance / tmp_dim;
			}
			HashSet<Integer> ask = new HashSet<Integer>();
			ask.add(0);
			HashSet<Integer> connected = isConnected(0, conn, ask);
			if (n - connected.size() == 0)
				co++;
			else {
				is++;
			}
			int t = (int) n - connected.size();
			Integer t2 = counter.get(t);
			t2++;
			counter.set(t, t2);
		}
		avgDistance = avgDistance / 100000;

		System.out.println("co=" + co + " is=" + is + " percent="
				+ (co / (co + is)) + " avgDistance=" + avgDistance);
		for (int t = 0; t < n; t++) {
			System.out.print(t + " & "
					+ ((double) counter.get(new Integer(t)) / 100000)
					+ " \\\\ \n");
		}
	}

	public HashSet<Integer> isConnected(Integer e1,
			ArrayList<ArrayList<Integer>> connections, HashSet<Integer> ask) {
		List<Integer> c = connections.get(e1);
		// System.out.print("isConnected " + e1 + " size=" + c.size());

		for (Integer i : c) {
			if (ask.contains(i))
				;
			else {
				ask.add(i);
				// System.out.println(" add " + i + " ");
				ask.addAll(isConnected(i, connections, ask));
			}
		}
		return ask;
	}

	// public RandomTools(String name, double mean, double variance) {
	// this.name = name;
	// this.mean = mean;
	// this.variance = variance;
	// this.stdDev = Math.sqrt(variance);
	// seed = System.currentTimeMillis();
	// }

	// public double sampleNormal() {
	// double result = mean + (stdDev * mt.nextGaussian());
	// return result;
	// }
	//
	// public double sampleNormal(double mean, double variance) {
	// this.mean = mean;
	// this.variance = variance;
	// this.stdDev = Math.sqrt(variance);
	// double result = mean + (stdDev * mt.nextGaussian());
	// return result;
	// }
	//
	// public double sampleLogNormal() {
	// return Math.exp(mean + stdDev * sampleNormal());
	// }
	//
	// public double sampleLogNormal(double mean, double variance) {
	// this.mean = mean;
	// this.variance = variance;
	// this.stdDev = Math.sqrt(variance);
	// return Math.exp(mean + (stdDev * sampleNormal()));
	// }
}
