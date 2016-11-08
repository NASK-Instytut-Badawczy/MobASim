package pl.edu.econet.energy;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;

public class EAS {

	double power = 0;
	double minPower = 0;
	double bandwidth = 0;
	double minBandwidth = 0;
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public double getMinBandwidth() {
		return minBandwidth;
	}

	public void setMinBandwidth(double minBandwidth) {
		this.minBandwidth = minBandwidth;
	}

	public double getMinPower() {
		return minPower;
	}

	public void setMinPower(double minPower) {
		this.minPower = minPower;
	}

	public double getPowerDifference() {
		return this.power - this.minPower;
	}

	public double getBandwidthDifference() {
		return this.bandwidth - this.minBandwidth;
	}

	public ASimDO getAsData() {
		ASimDO result = new ASimDO();
		result.setName(this.getId());

		ASimPO p = new ASimPO();
		p.setFather(result);
		p.setCode("power");
		p.setValue("" + this.getPower());

		ASimPO mp = new ASimPO();
		mp.setFather(result);
		mp.setCode("minPower");
		mp.setValue("" + this.minPower);

		ASimPO b = new ASimPO();
		b.setFather(result);
		b.setCode("bandwidth");
		b.setValue("" + this.getBandwidth());

		ASimPO mb = new ASimPO();
		mb.setFather(result);
		mb.setCode("minBandwidth");
		mb.setValue("" + this.minBandwidth);

		result.getProperties().add(p);
		result.getProperties().add(mp);
		result.getProperties().add(b);
		result.getProperties().add(mb);
		result.setType("EAS");
		return result;
	}

	public void setData(ASimDO data) {
		this.setId(data.getName());
		for (ASimPO p : data.getProperties()) {
			if (p.getValue() == null || p.getValue().equals(""))
				continue;
			if (p.getCode().equals("power"))
				this.setPower(new Double(p.getValue()));
			if (p.getCode().equals("minPower"))
				this.setMinPower(new Double(p.getValue()));
			if (p.getCode().equals("bandwidth"))
				this.setBandwidth(new Double(p.getValue()));
			if (p.getCode().equals("minBandwidth"))
				this.setMinBandwidth(new Double(p.getValue()));
		}
	}

}
