package pl.edu.econet.demand;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.econet.topology.Path;

public class Demand implements Comparable<Demand> {
	String source;
	String sink;
	Double volume;
	double w;
	String id;
	Path path;

	public Demand() {
		w = Math.random();
		path = new Path();
		path.setDemand(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSink() {
		return sink;
	}

	public void setSink(String sink) {
		this.sink = sink;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getX() {
		return volume;
	}

	public String getS() {
		return source;
	}

	public String getT() {
		return sink;
	}

	public ASimDO getAsData() {
		ASimDO result = new ASimDO();
		result.setName(this.getId());

		ASimPO source = new ASimPO();
		source.setFather(result);
		source.setCode("source");
		source.setValue("" + this.getSource());

		ASimPO sink = new ASimPO();
		sink.setFather(result);
		sink.setCode("sink");
		sink.setValue("" + this.getSink());

		ASimPO volume = new ASimPO();
		volume.setFather(result);
		volume.setCode("volume");
		volume.setValue("" + this.getVolume());

		result.getProperties().add(source);
		result.getProperties().add(sink);
		result.getProperties().add(volume);
		result.setType("DEMAND");
		return result;
	}

	public void setData(ASimDO data) {
		this.setId(data.getName());
		for (ASimPO p : data.getProperties()) {
			if (p.getValue() == null || p.getValue().equals(""))
				continue;

			if (p.getCode().equals("source"))
				this.setSource(p.getValue());
			if (p.getCode().equals("sink"))
				this.setSink(p.getValue());
			if (p.getCode().equals("volume"))
				this.setVolume(new Double(p.getValue()));
		}
	}

	@Override
	public int compareTo(Demand o) {
		if (this.getVolume() < o.getVolume())
			return -1;
		if (this.getVolume() > o.getVolume())
			return 1;

		if (w > o.w)
			return 1;
		if (w < o.w)
			return -1;

		// int result = this.getSource().compareTo(o.getSource());
		// if (result == 0)
		// result = this.getSink().compareTo(o.getSink());

		// TODO Auto-generated method stub
		return 0;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
