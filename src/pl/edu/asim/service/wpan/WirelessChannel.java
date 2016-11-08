package pl.edu.asim.service.wpan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class WirelessChannel<E> {

	LinkedTransferQueue<Frame<E>> queue;
	double timestamp = 0;
	ArrayList<String> registeredNodes;
	HashMap<String, Vector3D> locations;
	HashMap<String, Card> cards;
	PathLoss pl;
	ArrayList<String> connections;
	double probability = 0.99;
	double probability2 = 0.9999;
	private NormalDistribution standarizedNormalDistribution;
	private final double frequency = 2400;
	BigDecimal constantLoss = null;
	double cellsize;
	String wirelessPath = "";
	int linkCount = 0;
	double linkwidth = 0;
	double nCounts = 0;
	double scale = 1;

	public WirelessChannel(PathLoss pl, double cellsize) {
		this.pl = pl;
		this.cellsize = cellsize;
		queue = new LinkedTransferQueue<Frame<E>>();
		registeredNodes = new ArrayList<String>();
		locations = new HashMap<String, Vector3D>();
		cards = new HashMap<String, Card>();
		connections = new ArrayList<String>();
		setStandarizedNormalDistribution(new NormalDistribution());
	}

	public synchronized void refresh(double timestamp) {
		for (Frame<E> f : queue) {
			if (f.getTimestamp() < timestamp)
				queue.remove(f);
		}
		registeredNodes.clear();
		connections.clear();
		cards.clear();
		locations.clear();
		this.timestamp = timestamp;
		wirelessPath = "";
		linkCount = 0;
		linkwidth = 0;
		nCounts = 0;
	}

	public synchronized boolean register(String id, Vector3D location, Card c) {
		if (c.getFrequency() != frequency)
			return false;
		registeredNodes.add(id);
		locations.put(id, location);
		cards.put(id, c);
		// calculateConnections(id, c, location);
		return true;
	}

	public boolean send(String idFrom, String idTo, E data) {
		if (!registeredNodes.contains(idFrom))
			return false;
		Frame<E> f = new Frame<E>();
		f.setData(data);
		f.setIdFrom(idFrom);
		f.setIdTo(idTo);
		f.setTimestamp(timestamp);
		queue.add(f);
		return true;
	}

	public ArrayList<E> read(String idTo) {
		ArrayList<E> result = new ArrayList<E>();
		if (!registeredNodes.contains(idTo))
			return result;
		List<String> connections = getConnections(idTo);

		for (Frame<E> f : queue) {
			if (f.getIdTo() == null || f.getIdTo().equals(idTo)) {
				if (connections.contains(f.getIdFrom()))
					result.add(f.getData());
			}
		}
		return result;
	}

	public NormalDistribution getStandarizedNormalDistribution() {
		return standarizedNormalDistribution;
	}

	public void setStandarizedNormalDistribution(
			NormalDistribution standarizedNormalDistribution) {
		this.standarizedNormalDistribution = standarizedNormalDistribution;
	}

	private BigDecimal getPropagationRangeWithProbability(double nFactor,
			double sFactor, double probability, double pt, double ps) {
		double r = Math
				.pow(10,
						(((-this.standarizedNormalDistribution
								.inverseCumulativeProbability(probability) * sFactor)
								+ pt - ps - getConstantLoss().doubleValue()) / (10 * nFactor)));
		return new BigDecimal("" + r);
	}

	private BigDecimal getConstantLoss() {
		if (constantLoss == null) {
			constantLoss = new BigDecimal(
					""
							+ (20 * Math
									.log10(((4 * Math.PI * frequency * 1000000) / 299792458))));
		}
		return constantLoss;
	}

	public void calculateConnections() {
		for (String nid : cards.keySet()) {
			Card c = cards.get(nid);
			Vector3D l = locations.get(nid);
			calculateConnections(nid, c, l);
		}
	}

	private void calculateConnections(String id, Card c, Vector3D l) {
		int column = (int) (l.getX() / cellsize);
		int row = (int) (l.getY() / cellsize);
		double N = pl.getN(column, row);
		double S = pl.getS(column, row);
		c.setOptimalWirelessDistance(0);
		for (String nid : cards.keySet()) {
			if (nid.equals(id))
				continue;
			Card nc = cards.get(nid);
			Vector3D nl = locations.get(nid);
			int ncolumn = (int) (nl.getX() / cellsize);
			int nrow = (int) (nl.getY() / cellsize);
			double nN = pl.getN(ncolumn, nrow);
			double nS = pl.getS(ncolumn, nrow);

			double distance = nl.distance(l);
			double prange1 = scale
					* getPropagationRangeWithProbability(N, S, probability,
							nc.getPt(), c.getPs()).doubleValue();
			double prange2 = scale
					* getPropagationRangeWithProbability(nN, nS, probability2,
							c.getPt(), nc.getPs()).doubleValue();
			// System.out.println(prange1+" "+prange2+" "+distance);
			// if (prange1 >= distance && prange2 >= distance) {
			if (prange1 >= distance) {
				connections.add(nid + "," + id);
				// connections.add(id + "," + nid);
				linkCount++;
				linkwidth = linkwidth + distance;
				nCounts++;
				wirelessPath = wirelessPath
						+ " M"
						+ new BigDecimal(nl.getX()).setScale(2,
								RoundingMode.HALF_DOWN)
						+ ","
						+ new BigDecimal(nl.getY()).setScale(2,
								RoundingMode.HALF_DOWN);
				wirelessPath = wirelessPath
						+ " L"
						+ new BigDecimal(l.getX()).setScale(2,
								RoundingMode.HALF_DOWN)
						+ ","
						+ new BigDecimal(l.getY()).setScale(2,
								RoundingMode.HALF_DOWN);
			}

			// if (nc.getOptimalWirelessDistance() == 0
			// || nc.getOptimalWirelessDistance() > prange1)
			// nc.setOptimalWirelessDistance(prange1);
			if (c.getOptimalWirelessDistance() == 0
					|| c.getOptimalWirelessDistance() > prange2)
				c.setOptimalWirelessDistance(prange2);
		}
	}

	public List<String> getConnections(String e1) {
		ArrayList<String> result = new ArrayList<String>();
		for (String s : connections) {
			if (s.startsWith(e1 + ",")) {
				String n = s.replaceAll(e1 + ",", "");
				result.add(n);
			}
		}
		return result;
	}

	public boolean isConnected(String e1, String e2, HashSet<String> ask) {
		List<String> c = getConnections(e1);
		if (c.contains(e2))
			return true;
		else {
			for (String e3 : c) {
				if (ask.contains(e3))
					continue;
				ask.add(e3);
				if (isConnected(e3, e2, ask))
					return true;
			}
		}
		return false;
	}

	public ArrayList<String> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<String> connections) {
		this.connections = connections;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String getWirelessPath() {
		return wirelessPath;
	}

	public void setWirelessPath(String wirelessPath) {
		this.wirelessPath = wirelessPath;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public double getLinkwidth() {
		return linkwidth;
	}

	public void setLinkwidth(double linkwidth) {
		this.linkwidth = linkwidth;
	}

	public double getnCounts() {
		return nCounts;
	}

	public void setnCounts(double nCounts) {
		this.nCounts = nCounts;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
