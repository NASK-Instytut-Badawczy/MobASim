package pl.edu.asim.service.wpan;

public class Frame<E> {

	private String idFrom;
	private double timestamp;
	private String idTo;
	private E data;

	public void setData(E data) {
		this.data = data;
	}

	public E getData() {
		return data;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public String getIdFrom() {
		return idFrom;
	}

	public void setIdFrom(String idFrom) {
		this.idFrom = idFrom;
	}

	public String getIdTo() {
		return idTo;
	}

	public void setIdTo(String idTo) {
		this.idTo = idTo;
	}

}
