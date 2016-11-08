package pl.edu.asim.sim.deployment;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class BroadcastMessage {

	private String name;
	private String id;
	private Vector3D location;
	private double timestamp;
	private double velocity;
	private Vector3D shift;
	private double pt;
	private int degree;
	private int hoop;

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vector3D getLocation() {
		return location;
	}

	public void setLocation(Vector3D location) {
		this.location = location;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getPt() {
		return pt;
	}

	public void setPt(double pt) {
		this.pt = pt;
	}

	public Vector3D getShift() {
		return shift;
	}

	public void setShift(Vector3D shift) {
		this.shift = shift;
	}

	public int getHoop() {
		return hoop;
	}

	public void setHoop(int hoop) {
		this.hoop = hoop;
	}

}
