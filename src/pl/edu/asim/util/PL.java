package pl.edu.asim.util;

import java.math.BigDecimal;

public class PL {

	double frequency = 2400;
	BigDecimal constantLoss = null;

	/* moc nadajnika */
	private double pt;
	/* wrażliwość odbiornika */
	private double ps;
	private double pcca;

	private double optimalWirelessDistance;
	private double maxCcaWirelessDistance;

	/* strata mocy w odległości referencyjnej 1m */
	public BigDecimal getConstantLoss() {

		if (constantLoss == null) {
			constantLoss = new BigDecimal(
					""
							+ (20 * Math
									.log10(((4 * Math.PI * frequency * 1000000) / 299792458))));
		}
		return constantLoss;
	}

	/* strata mocy */
	public BigDecimal getPathLoss(double distance, double nFactor) {
		if (distance <= 1)
			return constantLoss;
		else
			return constantLoss.add(new BigDecimal(""
					+ (10 * nFactor * Math.log10(distance))));
	}

	/* strata mocy */
	public BigDecimal getPathLoss(double distance, double nFactor,
			double sFactor) {
		BigDecimal result = new BigDecimal("0");
		if (distance <= 1) {
			result = result.add(constantLoss);
		} else {
			result = result.add(constantLoss.add(new BigDecimal(""
					+ (10 * nFactor * Math.log10(distance)))));
		}
		if (sFactor > 0) {
			BigDecimal shadowing = new BigDecimal(""
					+ RandomTools.sampleNormal(0.0, sFactor));
			result = result.add(shadowing);
		}
		return result;
	}

	/* maksymalny zasięg transmisji */
	public BigDecimal getPropagationRange(double pt, double ps, double nFactor) {
		double r = Math.pow(10,
				((pt - ps - getConstantLoss().doubleValue()) / (10 * nFactor)));
		return new BigDecimal("" + r);
	}

	/* maksymalny zasięg transmisji */
	public BigDecimal getPropagationRange(double nFactor) {
		double r = Math.pow(10,
				((pt - ps - getConstantLoss().doubleValue()) / (10 * nFactor)));
		return new BigDecimal("" + r);
	}

	/* maksymalny zasięg transmisji */
	public BigDecimal getPropagationRange(double nFactor, double sFactor) {
		double r = Math
				.pow(10, (((-2.3 * sFactor) + pt - ps - getConstantLoss()
						.doubleValue()) / (10 * nFactor)));
		return new BigDecimal("" + r);
	}

	public BigDecimal getMaxCcaPropagationRange(double nFactor, double sFactor) {
		double r = Math
				.pow(10, (((-4 * sFactor) + pt - pcca - getConstantLoss()
						.doubleValue()) / (10 * nFactor)));
		return new BigDecimal("" + r);
	}

	public double getPt() {
		return pt;
	}

	public void setPt(double pt) {
		this.pt = pt;
	}

	public double getPs() {
		return ps;
	}

	public void setPs(double ps) {
		this.ps = ps;
	}

	public double getOptimalWirelessDistance() {
		return optimalWirelessDistance;
	}

	public void setOptimalWirelessDistance(double optimalWirelessDistance) {
		this.optimalWirelessDistance = optimalWirelessDistance;
	}

	public double getPcca() {
		return pcca;
	}

	public void setPcca(double pcca) {
		this.pcca = pcca;
	}

	public double getMaxCcaWirelessDistance() {
		return maxCcaWirelessDistance;
	}

	public void setMaxCcaWirelessDistance(double maxCcaWirelessDistance) {
		this.maxCcaWirelessDistance = maxCcaWirelessDistance;
	}
}
