package pl.edu.asim.service.mobility;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class OperatingAreaPoint {

	private double x;
	private double y;

	private int xIndex;
	private int yIndex;

	public OperatingAreaPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public OperatingAreaPoint(Vector3D v) {
		this.x = v.getX();
		this.y = v.getY();
	}

	public void setxIndex(int xIndex) {
		this.xIndex = xIndex;
	}

	public int getxIndex() {
		return xIndex;
	}

	public void setyIndex(int yIndex) {
		this.yIndex = yIndex;
	}

	public int getyIndex() {
		return yIndex;
	}

	public boolean isInCell(int xIndex, int yIndex) {
		return (this.xIndex == xIndex) && (this.yIndex == yIndex);
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getY() {
		return y;
	}

	public Point getAsJTSPoint(GeometryFactory factory) {
		return factory.createPoint(new Coordinate(x, y));
	}

	public Geometry getAsJTSBuffer(GeometryFactory factory, double distance) {
		return factory.createPoint(new Coordinate(x, y)).buffer(distance, 16,
				BufferOp.CAP_ROUND);
	}

	public Coordinate getAsJTSCoordinate() {
		return new Coordinate(x, y);
	}

	public Vector3D getAsVector3D() {
		return new Vector3D(x, y, 0);
	}

	public String toWKT() {
		return "POINT (" + x + " " + y + ")";
	}

	@Override
	public String toString() {
		return "(" + x + " " + y + ")";
	}

	@Override
	public OperatingAreaPoint clone() {
		return new OperatingAreaPoint(new Double(x), new Double(y));
	}

}
