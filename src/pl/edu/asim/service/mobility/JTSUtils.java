package pl.edu.asim.service.mobility;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.sim.convoy.VehicleService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class JTSUtils {

	public static String LEFT = "left";
	public static String RIGHT = "right";
	public static String DIRECT = "direct";
	public static String ADAP = "adaptive";

	public ArrayList<OperatingAreaPoint> movePoints(
			ArrayList<OperatingAreaPoint> points, OperatingAreaPoint shift) {
		ArrayList<OperatingAreaPoint> result = new ArrayList<OperatingAreaPoint>();
		for (OperatingAreaPoint p : points) {
			result.add(new OperatingAreaPoint(p.getAsVector3D().add(
					shift.getAsVector3D())));
		}
		return result;
	}

	public LineString getAsJTSLineString(ArrayList<OperatingAreaPoint> points,
			GeometryFactory factory) {
		Coordinate[] c = new Coordinate[points.size()];
		int i = 0;
		for (OperatingAreaPoint point : points) {
			c[i] = point.getAsJTSCoordinate();
			i++;
		}
		return factory.createLineString(c);
	}

	public LinearRing getAsJTSLinearRing(ArrayList<OperatingAreaPoint> points,
			GeometryFactory factory) {
		Coordinate[] c = new Coordinate[points.size() + 1];
		int i = 0;
		for (OperatingAreaPoint point : points) {
			c[i] = point.getAsJTSCoordinate();
			i++;
		}
		if (points.size() > 0)
			c[i] = points.get(0).getAsJTSCoordinate();
		return factory.createLinearRing(c);
	}

	public Polygon getAsJTSPolygon(ArrayList<OperatingAreaPoint> points,
			GeometryFactory factory) {
		return factory.createPolygon(getAsJTSLinearRing(points, factory), null);
	}

	public Polygon getAsJTSPolygon(OperatingArea area, GeometryFactory factory) {
		ArrayList<OperatingAreaPoint> points = new ArrayList<OperatingAreaPoint>();
		OperatingAreaPoint p1 = new OperatingAreaPoint(0, 0);
		OperatingAreaPoint p2 = new OperatingAreaPoint(0, area.getHeight());
		OperatingAreaPoint p3 = new OperatingAreaPoint(area.getWidth(),
				area.getHeight());
		OperatingAreaPoint p4 = new OperatingAreaPoint(area.getWidth(), 0);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		return factory.createPolygon(getAsJTSLinearRing(points, factory), null);
	}

	public Polygon getAsJTSPolygon(Obstacle o, GeometryFactory factory) {
		ArrayList<OperatingAreaPoint> points = movePoints(o.getPoints(),
				o.getSvgZeroPoint());
		return factory.createPolygon(getAsJTSLinearRing(points, factory), null);
	}

	public boolean intersects(ArrayList<OperatingAreaPoint> points, Geometry g,
			GeometryFactory factory) {
		if (points == null || g == null)
			return false;
		for (OperatingAreaPoint p : points) {
			if (g.intersects(p.getAsJTSPoint(factory)))
				return true;
		}
		return false;
	}

	public LineString getExteriorRing(Polygon p) {

		LineString lineS = (p).getExteriorRing();
		lineS.normalize();

		Point center = lineS.getCentroid();
		Point p1 = lineS.getPointN(0);
		Point p2 = lineS.getPointN(1);
		if (p1.distance(p2) < 0.001)
			p2 = lineS.getPointN(2);

		LineSegment ls1 = new LineSegment(center.getCoordinate(),
				p1.getCoordinate());
		LineSegment ls2 = new LineSegment(center.getCoordinate(),
				p2.getCoordinate());

		double angle1 = Math.toDegrees(ls1.angle());
		double angle2 = Math.toDegrees(ls2.angle()) - angle1;
		if (angle2 <= -180)
			angle2 = angle2 + 360;
		if (angle2 > 180)
			angle2 = angle2 - 360;

		if (angle2 < 0) { // RIGHT
			lineS = lineS.reverse();
		} else { // LEFT
			;
		}

		return lineS;
	}

	public int getEdgeIndex(LineString lineS, Coordinate referencePoint) {

		double dS = -1;
		int iS = -1;

		for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
			Point p1 = lineS.getPointN(i);
			Point p2 = lineS.getPointN(i + 1);
			LineSegment tSegment = new LineSegment(p1.getCoordinate(),
					p2.getCoordinate());
			double d = tSegment.distance(referencePoint);
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
		}
		return iS;
	}

	// public OperatingAreaPoint raLR(String id, ArrayList<Obstacle> obstacles,
	// OperatingAreaPoint oldLocation, OperatingAreaPoint newLocation,
	// GeometryFactory factory, boolean left, double multiply) {
	// Coordinate[] lsc = new Coordinate[2];
	// lsc[0] = oldLocation.getAsJTSCoordinate();
	// lsc[1] = newLocation.getAsJTSCoordinate();
	// LineString ls = factory.createLineString(lsc);
	// double lenght = ls.getLength();
	//
	// ArrayList<LineString> obstacleString = new ArrayList<LineString>();
	// ArrayList<Point> intersectionPoints = new ArrayList<Point>();
	//
	// for (Obstacle o : obstacles) {
	// if (o.getId().equals(id))
	// continue;
	// Polygon p = getAsJTSPolygon(o, factory);
	//
	// LineString lineS = getExteriorRing(p);
	//
	// for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
	// Point p1 = lineS.getPointN(i);
	// Point p2 = lineS.getPointN(i + 1);
	//
	// Coordinate[] lsc2 = new Coordinate[2];
	// lsc2[0] = p1.getCoordinate();
	// lsc2[1] = p2.getCoordinate();
	//
	// LineString ls2 = factory.createLineString(lsc2);
	// Geometry g = ls.intersection(ls2);
	// if (!g.isEmpty()) {
	// obstacleString.add(ls2);
	// if (g instanceof Point)
	// intersectionPoints.add((Point) g);
	// }
	// }
	// }
	// double dS = -1;
	// int iS = -1;
	// int i = 0;
	// for (Point point : intersectionPoints) {
	// double d = point.distance(oldLocation.getAsJTSPoint(factory));
	// if (dS == -1 || d < dS) {
	// dS = d;
	// iS = i;
	// }
	// i++;
	// }
	// if (iS > -1) {
	// LineString result = obstacleString.get(iS);
	//
	// Vector3D vector = (left) ? new Vector3D(result.getPointN(1)
	// .getCoordinate().x - result.getPointN(0).getCoordinate().x,
	// result.getPointN(1).getCoordinate().y
	// - result.getPointN(0).getCoordinate().y, 0)
	// .normalize() : new Vector3D(result.getPointN(0)
	// .getCoordinate().x - result.getPointN(1).getCoordinate().x,
	// result.getPointN(0).getCoordinate().y
	// - result.getPointN(1).getCoordinate().y, 0)
	// .normalize();
	//
	// vector = vector.scalarMultiply(lenght * multiply);
	// return new OperatingAreaPoint(oldLocation.getX() + vector.getX(),
	// oldLocation.getY() + vector.getY());
	//
	// } else
	// return newLocation;
	//
	// }

	public double getFreeDistance(String id, ArrayList<Obstacle> obstacles,
			GeometryFactory factory, OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, double viewRange) {

		Vector3D vector = new Vector3D(newLocation.getX() - oldLocation.getX(),
				newLocation.getY() - oldLocation.getY(), 0);
		vector = vector.scalarMultiply(viewRange / vector.getNorm());

		OperatingAreaPoint newLocation2 = new OperatingAreaPoint(
				oldLocation.getX() + vector.getX(), oldLocation.getY()
						+ vector.getY());
		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation2.getAsJTSCoordinate();
		LineString ls = factory.createLineString(lsc);

		double lenght = ls.getLength();
		// System.out.println(lenght+" "+vector.getNorm()+" "+ls.toString()+" "+vector);

		for (Obstacle o : obstacles) {
			if (o.getId().equals(id))
				continue;
			Polygon p = getAsJTSPolygon(o, factory);
			if (p.intersects(ls)) {
				double l = p.distance(oldLocation.getAsJTSPoint(factory));
				if (l > 0 && l < lenght) {
					lenght = l;
				}
			}
		}
		return lenght;
	}

	public OperatingAreaPoint raADAP(String id, ArrayList<Obstacle> obstacles,
			OperatingAreaPoint oldLocation, OperatingAreaPoint newLocation,
			GeometryFactory factory, String passingSide, double multiply,
			double buffer, double areaWidth, double areaHeight) {
		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation.getAsJTSCoordinate();

		LineSegment lso = new LineSegment(lsc[0], lsc[1]);
		double lenght = lso.getLength();
		LineSegment eS = new LineSegment(new Coordinate(0, 0), new Coordinate(
				0, areaHeight));
		LineSegment wS = new LineSegment(new Coordinate(areaWidth, 0),
				new Coordinate(areaWidth, areaHeight));
		LineSegment nS = new LineSegment(new Coordinate(0, 0), new Coordinate(
				areaWidth, 0));
		LineSegment sS = new LineSegment(new Coordinate(0, areaHeight),
				new Coordinate(areaWidth, areaHeight));

		ArrayList<LineSegment> obstacleString = new ArrayList<LineSegment>();
		ArrayList<Coordinate> intersectionPoints = new ArrayList<Coordinate>();

		for (Obstacle o : obstacles) {
			if (o.getId().equals(id))
				continue;
			Polygon p = getAsJTSPolygon(o, factory);
			Geometry ge = p.buffer(buffer, 1, BufferOp.CAP_SQUARE);
			p = (Polygon) ge;

			LineString lineS = getExteriorRing(p);

			for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
				Point p1 = lineS.getPointN(i);
				Point p2 = lineS.getPointN(i + 1);

				LineSegment lSegment = new LineSegment(p1.getCoordinate(),
						p2.getCoordinate());
				Coordinate inscCoordinate = lso.intersection(lSegment);

				if (inscCoordinate != null) {
					obstacleString.add(lSegment);
					intersectionPoints.add(inscCoordinate);
				}
			}
		}

		Coordinate wCoordinate = lso.intersection(wS);
		Coordinate eCoordinate = lso.intersection(eS);
		Coordinate nCoordinate = lso.intersection(nS);
		Coordinate sCoordinate = lso.intersection(sS);

		if (wCoordinate != null) {
			obstacleString.add(wS);
			intersectionPoints.add(wCoordinate);
		}
		if (eCoordinate != null) {
			obstacleString.add(eS);
			intersectionPoints.add(eCoordinate);
		}
		if (nCoordinate != null) {
			obstacleString.add(nS);
			intersectionPoints.add(nCoordinate);
		}
		if (sCoordinate != null) {
			obstacleString.add(sS);
			intersectionPoints.add(sCoordinate);
		}

		double dS = -1;
		int iS = -1;
		int i = 0;
		for (Coordinate point : intersectionPoints) {
			double d = point.distance(oldLocation.getAsJTSCoordinate());
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
			i++;
		}
		if (iS > -1) {
			LineSegment result = obstacleString.get(iS);

			Vector3D vectorL = new Vector3D(result.p1.x - result.p0.x,
					result.p1.y - result.p0.y, 0).normalize().scalarMultiply(
					lenght * multiply);
			Vector3D vectorR = new Vector3D(result.p0.x - result.p1.x,
					result.p0.y - result.p1.y, 0).normalize().scalarMultiply(
					lenght * multiply);

			OperatingAreaPoint opL = new OperatingAreaPoint(oldLocation.getX()
					+ vectorL.getX(), oldLocation.getY() + vectorL.getY());

			OperatingAreaPoint opR = new OperatingAreaPoint(oldLocation.getX()
					+ vectorR.getX(), oldLocation.getY() + vectorR.getY());

			double dL = opL.getAsVector3D().distance(
					newLocation.getAsVector3D());
			double dR = opR.getAsVector3D().distance(
					newLocation.getAsVector3D());

			if (passingSide.equals(JTSUtils.LEFT))
				return opL;
			if (passingSide.equals(JTSUtils.RIGHT))
				return opR;

			return (dL < dR) ? opL : opR;
		} else
			return newLocation;
	}

	public OperatingAreaPoint raADAP2(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, GeometryFactory factory,
			double multiply, double areaWidth, double areaHeight,
			MobileEntity entity, ArrayList<Obstacle> obstacles, boolean adap) {
		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation.getAsJTSCoordinate();
		LineString ls = factory.createLineString(lsc);
		double lenght = ls.getLength();

		ArrayList<LineString> obstacleString = new ArrayList<LineString>();
		ArrayList<Point> intersectionPoints = new ArrayList<Point>();

		for (Obstacle o : obstacles) {
			if (o.getId().equals(entity.getId()))
				continue;
			Polygon p = getAsJTSPolygon(o, factory);
			Geometry ge = p.buffer(entity.getBuffer(), 1, BufferOp.CAP_SQUARE);
			p = (Polygon) ge;

			LineString lineS = getExteriorRing(p);

			for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
				Point p1 = lineS.getPointN(i);
				Point p2 = lineS.getPointN(i + 1);

				Coordinate[] lsc2 = new Coordinate[2];
				lsc2[0] = p1.getCoordinate();
				lsc2[1] = p2.getCoordinate();

				LineString ls2 = factory.createLineString(lsc2);
				Geometry g = ls.intersection(ls2);
				if (!g.isEmpty()) {
					obstacleString.add(ls2);
					if (g instanceof Point)
						intersectionPoints.add((Point) g);
				}
			}
		}
		double dS = -1;
		int iS = -1;
		int i = 0;
		for (Point point : intersectionPoints) {
			double d = point.distance(oldLocation.getAsJTSPoint(factory));
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
			i++;
		}
		if (iS > -1) {
			LineString result = obstacleString.get(iS);

			Vector3D vectorL = new Vector3D(
					result.getPointN(1).getCoordinate().x
							- result.getPointN(0).getCoordinate().x, result
							.getPointN(1).getCoordinate().y
							- result.getPointN(0).getCoordinate().y, 0)
					.normalize().scalarMultiply(lenght * multiply);
			Vector3D vectorR = new Vector3D(
					result.getPointN(0).getCoordinate().x
							- result.getPointN(1).getCoordinate().x, result
							.getPointN(0).getCoordinate().y
							- result.getPointN(1).getCoordinate().y, 0)
					.normalize().scalarMultiply(lenght * multiply);

			LineSegment lineSegment = new LineSegment(result.getPointN(0)
					.getCoordinate(), result.getPointN(1).getCoordinate());
			Coordinate closestPoint = lineSegment.closestPoint(lsc[1]);
			OperatingAreaPoint opD = new OperatingAreaPoint(closestPoint.x,
					closestPoint.y);
			double d0 = oldLocation.getAsVector3D().distance(
					opD.getAsVector3D());
			// System.out.println(d0);

			if (opD.getX() > areaWidth - entity.getBuffer()) {
				opD.setX(areaWidth - entity.getBuffer());
			}
			if (opD.getX() < 0 + entity.getBuffer()) {
				opD.setX(0 + entity.getBuffer());
			}
			if (opD.getY() > areaHeight - entity.getBuffer()) {
				opD.setY(areaHeight - entity.getBuffer());
			}
			if (opD.getY() < 0 + entity.getBuffer()) {
				opD.setY(0 + entity.getBuffer());
			}

			OperatingAreaPoint opL = new OperatingAreaPoint(oldLocation.getX()
					+ vectorL.getX(), oldLocation.getY() + vectorL.getY());

			boolean leftOut = false;
			if (opL.getX() > areaWidth - entity.getBuffer()) {
				opL.setX(areaWidth - entity.getBuffer());
				leftOut = true;
			}
			if (opL.getX() < 0 + entity.getBuffer()) {
				opL.setX(0 + entity.getBuffer());
				leftOut = true;
			}
			if (opL.getY() > areaHeight - entity.getBuffer()) {
				opL.setY(areaHeight - entity.getBuffer());
				leftOut = true;
			}
			if (opL.getY() < 0 + entity.getBuffer()) {
				opL.setY(0 + entity.getBuffer());
				leftOut = true;
			}

			OperatingAreaPoint opR = new OperatingAreaPoint(oldLocation.getX()
					+ vectorR.getX(), oldLocation.getY() + vectorR.getY());

			boolean rightOut = false;
			if (opR.getX() > areaWidth - entity.getBuffer()) {
				opR.setX(areaWidth - entity.getBuffer());
				rightOut = true;
			}
			if (opR.getX() < 0 + entity.getBuffer()) {
				opR.setX(0 + entity.getBuffer());
				rightOut = true;
			}
			if (opR.getY() > areaHeight - entity.getBuffer()) {
				opR.setY(areaHeight - entity.getBuffer());
				rightOut = true;
			}
			if (opR.getY() < 0 + entity.getBuffer()) {
				opR.setY(0 + entity.getBuffer());
				rightOut = true;
			}

			double dL = opL.getAsVector3D().distance(
					newLocation.getAsVector3D());
			double dR = opR.getAsVector3D().distance(
					newLocation.getAsVector3D());

			// if (adap) {
			// if (leftOut)
			// entity.setSide(JTSUtils.RIGHT);
			// if (rightOut)
			// entity.setSide(JTSUtils.LEFT);
			// }

			if (entity.getSide().equals(JTSUtils.LEFT)) {
				return opL;
			}
			if (entity.getSide().equals(JTSUtils.RIGHT)) {
				return opR;
			}
			if (entity.getSide().equals(JTSUtils.DIRECT)) {
				return opD;
			}

			// if (entity.getSide().equals(JTSUtils.LEFT) &&
			// !leftOut) {
			// return opL;
			// }
			// if (entity.getSide().equals(JTSUtils.RIGHT)
			// && !rightOut) {
			// return opR;
			// }

			// if (adap) {
			// if (dL < dR) {
			// entity.setSide(JTSUtils.LEFT);
			// } else {
			// entity.setSide(JTSUtils.RIGHT);
			// }
			// }

			return opD;
			// return (dL < dR) ? opL : opR;
		} else
			return newLocation;

	}

	public OperatingAreaPoint collisionFree(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, GeometryFactory factory,
			double multiply, double areaWidth, double areaHeight,
			MobileEntity entity, ArrayList<Obstacle> obstacles) {

		boolean ra = true;
		OperatingAreaPoint newLocation1 = newLocation;
		OperatingAreaPoint newLocation2 = newLocation;
		while (ra) {
			newLocation1 = raADAP3(oldLocation, newLocation2, factory,
					multiply, areaWidth, areaHeight, entity, obstacles);
			// if (entity.getId().equals("80"))
			// System.out.println("80 " + oldLocation + " " + newLocation2
			// + " " + newLocation1);

			if (newLocation1.getX() != newLocation2.getX()
					|| newLocation1.getY() != newLocation2.getY()) {
				newLocation2 = newLocation1;
				multiply = multiply * 0.8;
			} else {
				ra = false;
			}
		}
		return newLocation1;
	}

	public OperatingAreaPoint collisionFree(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, GeometryFactory factory,
			double multiply, double areaWidth, double areaHeight,
			VehicleService service, boolean adap) {

		boolean ra = true;
		OperatingAreaPoint newLocation1 = newLocation;
		OperatingAreaPoint newLocation2 = newLocation;
		while (ra) {
			newLocation1 = raADAP3(oldLocation, newLocation2, factory,
					multiply, areaWidth, areaHeight, service, adap);
			// if (entity.getId().equals("80"))
			// System.out.println("80 " + oldLocation + " " + newLocation2
			// + " " + newLocation1);

			if (newLocation1.getX() != newLocation2.getX()
					|| newLocation1.getY() != newLocation2.getY()) {
				newLocation2 = newLocation1;
				multiply = multiply * 0.8;
			} else {
				ra = false;
			}
		}
		return newLocation1;
	}

	public OperatingAreaPoint raADAP3(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, GeometryFactory factory,
			double multiply, double areaWidth, double areaHeight,
			MobileEntity entity, ArrayList<Obstacle> obstacles) {

		double buffer = entity.getBuffer();
		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation.getAsJTSCoordinate();

		LineSegment lso = new LineSegment(lsc[0], lsc[1]);
		double lenght = lso.getLength();
		LineSegment eS = new LineSegment(new Coordinate(buffer, buffer),
				new Coordinate(buffer, areaHeight - buffer));
		LineSegment wS = new LineSegment(new Coordinate(areaWidth - buffer,
				buffer),
				new Coordinate(areaWidth - buffer, areaHeight - buffer));
		LineSegment nS = new LineSegment(new Coordinate(buffer, buffer),
				new Coordinate(areaWidth - buffer, buffer));
		LineSegment sS = new LineSegment(new Coordinate(buffer, areaHeight
				- buffer), new Coordinate(areaWidth - buffer, areaHeight
				- buffer));

		ArrayList<LineSegment> obstacleString = new ArrayList<LineSegment>();
		ArrayList<Coordinate> intersectionPoints = new ArrayList<Coordinate>();

		for (Obstacle o : obstacles) {
			if (o.getId().equals(entity.getId()))
				continue;
			Polygon p = getAsJTSPolygon(o, factory);
			Geometry ge = p.buffer(entity.getBuffer(), 1, BufferOp.CAP_SQUARE);
			if(!(ge instanceof Polygon)) continue;
			p = (Polygon) ge;

			LineString lineS = getExteriorRing(p);

			for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
				Point p1 = lineS.getPointN(i);
				Point p2 = lineS.getPointN(i + 1);

				LineSegment lSegment = new LineSegment(p1.getCoordinate(),
						p2.getCoordinate());
				Coordinate[] tc = { lSegment.p0, lSegment.p1 };

				LineString ls_2 = factory.createLineString(lsc);
				LineString ls_1 = factory.createLineString(tc);

				Coordinate inscCoordinate = lso.intersection(lSegment);
				if (inscCoordinate != null) {
					Geometry g = ls_1.intersection(ls_2);

					if (g instanceof Point) {
						obstacleString.add(lSegment);
						intersectionPoints.add(inscCoordinate);
					}
				}
			}
		}

		Coordinate wCoordinate = lso.intersection(wS);
		Coordinate eCoordinate = lso.intersection(eS);
		Coordinate nCoordinate = lso.intersection(nS);
		Coordinate sCoordinate = lso.intersection(sS);

		// if (entity.getId().equals("80"))
		// System.out.println("wCoordinate " + wCoordinate);

		if (wCoordinate != null) {
			obstacleString.add(wS);
			intersectionPoints.add(wCoordinate);
		}
		if (eCoordinate != null) {
			obstacleString.add(eS);
			intersectionPoints.add(eCoordinate);
		}
		if (nCoordinate != null) {
			obstacleString.add(nS);
			intersectionPoints.add(nCoordinate);
		}
		if (sCoordinate != null) {
			obstacleString.add(sS);
			intersectionPoints.add(sCoordinate);
		}

		double dS = -1;
		int iS = -1;
		int i = 0;
		for (Coordinate point : intersectionPoints) {
			double d = point.distance(oldLocation.getAsJTSCoordinate());
			// if (entity.getId().equals("80"))
			// System.out.println("distance " + d + " for point " + point);
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
			i++;
		}
		if (iS > -1) {
			LineSegment result = obstacleString.get(iS);
			Coordinate point = intersectionPoints.get(iS);

			Vector3D vectorL = new Vector3D(result.p1.x - result.p0.x,
					result.p1.y - result.p0.y, 0).normalize().scalarMultiply(
					lenght * multiply);
			Vector3D vectorR = new Vector3D(result.p0.x - result.p1.x,
					result.p0.y - result.p1.y, 0).normalize().scalarMultiply(
					lenght * multiply);

			// Coordinate closestPoint = result.closestPoint(lsc[1]);
			// OperatingAreaPoint opD = new OperatingAreaPoint(closestPoint.x,
			// closestPoint.y);

			OperatingAreaPoint opL = new OperatingAreaPoint(oldLocation.getX()
					+ vectorL.getX(), oldLocation.getY() + vectorL.getY());

			OperatingAreaPoint opR = new OperatingAreaPoint(oldLocation.getX()
					+ vectorR.getX(), oldLocation.getY() + vectorR.getY());

			// double dD = newLocation.getAsVector3D().distance(
			// opD.getAsVector3D());
			double dL = opL.getAsVector3D().distance(
					newLocation.getAsVector3D());
			double dR = opR.getAsVector3D().distance(
					newLocation.getAsVector3D());

			if (entity.getSide().equals(JTSUtils.LEFT)) {
				return opL;
			} else if (entity.getSide().equals(JTSUtils.RIGHT)) {
				return opR;
			}
			return (dL < dR) ? opL : opR;
		} else
			return newLocation;
	}

	public OperatingAreaPoint raADAP3(OperatingAreaPoint oldLocation,
			OperatingAreaPoint newLocation, GeometryFactory factory,
			double multiply, double areaWidth, double areaHeight,
			VehicleService service, boolean adap) {

		double buffer = service.getEntity().getBuffer();
		Coordinate[] lsc = new Coordinate[2];
		lsc[0] = oldLocation.getAsJTSCoordinate();
		lsc[1] = newLocation.getAsJTSCoordinate();

		LineSegment lso = new LineSegment(lsc[0], lsc[1]);
		double lenght = lso.getLength();
		LineSegment eS = new LineSegment(new Coordinate(buffer, buffer),
				new Coordinate(buffer, areaHeight - buffer));
		LineSegment wS = new LineSegment(new Coordinate(areaWidth - buffer,
				buffer),
				new Coordinate(areaWidth - buffer, areaHeight - buffer));
		LineSegment nS = new LineSegment(new Coordinate(buffer, buffer),
				new Coordinate(areaWidth - buffer, buffer));
		LineSegment sS = new LineSegment(new Coordinate(buffer, areaHeight
				- buffer), new Coordinate(areaWidth - buffer, areaHeight
				- buffer));

		ArrayList<LineSegment> obstacleString = new ArrayList<LineSegment>();
		ArrayList<Coordinate> intersectionPoints = new ArrayList<Coordinate>();

		for (Obstacle o : service.getObstacles()) {
			if (o.getId().equals(service.getEntity().getId()))
				continue;
			Polygon p = getAsJTSPolygon(o, factory);
			Geometry ge = p.buffer(service.getEntity().getBuffer(), 1,
					BufferOp.CAP_SQUARE);
			p = (Polygon) ge;

			LineString lineS = getExteriorRing(p);

			for (int i = 0; i < lineS.getNumPoints() - 1; i++) {
				Point p1 = lineS.getPointN(i);
				Point p2 = lineS.getPointN(i + 1);

				LineSegment lSegment = new LineSegment(p1.getCoordinate(),
						p2.getCoordinate());
				Coordinate[] tc = { lSegment.p0, lSegment.p1 };

				LineString ls_2 = factory.createLineString(lsc);
				LineString ls_1 = factory.createLineString(tc);

				Coordinate inscCoordinate = lso.intersection(lSegment);
				if (inscCoordinate != null) {
					Geometry g = ls_1.intersection(ls_2);

					if (g instanceof Point) {
						obstacleString.add(lSegment);
						intersectionPoints.add(inscCoordinate);
					}
				}
			}
		}

		Coordinate wCoordinate = lso.intersection(wS);
		Coordinate eCoordinate = lso.intersection(eS);
		Coordinate nCoordinate = lso.intersection(nS);
		Coordinate sCoordinate = lso.intersection(sS);

		// if (service.getEntity().getId().equals("80"))
		// System.out.println("wCoordinate " + wCoordinate);

		if (wCoordinate != null) {
			obstacleString.add(wS);
			intersectionPoints.add(wCoordinate);
		}
		if (eCoordinate != null) {
			obstacleString.add(eS);
			intersectionPoints.add(eCoordinate);
		}
		if (nCoordinate != null) {
			obstacleString.add(nS);
			intersectionPoints.add(nCoordinate);
		}
		if (sCoordinate != null) {
			obstacleString.add(sS);
			intersectionPoints.add(sCoordinate);
		}

		double dS = -1;
		int iS = -1;
		int i = 0;
		for (Coordinate point : intersectionPoints) {
			double d = point.distance(oldLocation.getAsJTSCoordinate());
			// if (service.getEntity().getId().equals("80"))
			// System.out.println("distance " + d + " for point " + point);
			if (dS == -1 || d < dS) {
				dS = d;
				iS = i;
			}
			i++;
		}
		if (iS > -1) {
			LineSegment result = obstacleString.get(iS);
			Coordinate point = intersectionPoints.get(iS);

			Vector3D vectorL = new Vector3D(result.p1.x - result.p0.x,
					result.p1.y - result.p0.y, 0).normalize().scalarMultiply(
					lenght * multiply);
			Vector3D vectorR = new Vector3D(result.p0.x - result.p1.x,
					result.p0.y - result.p1.y, 0).normalize().scalarMultiply(
					lenght * multiply);

			// Coordinate closestPoint = result.closestPoint(lsc[1]);
			// OperatingAreaPoint opD = new OperatingAreaPoint(closestPoint.x,
			// closestPoint.y);

			OperatingAreaPoint opL = new OperatingAreaPoint(oldLocation.getX()
					+ vectorL.getX(), oldLocation.getY() + vectorL.getY());

			OperatingAreaPoint opR = new OperatingAreaPoint(oldLocation.getX()
					+ vectorR.getX(), oldLocation.getY() + vectorR.getY());

			// double dD = newLocation.getAsVector3D().distance(
			// opD.getAsVector3D());
			double dL = opL.getAsVector3D().distance(
					newLocation.getAsVector3D());
			double dR = opR.getAsVector3D().distance(
					newLocation.getAsVector3D());

			if (service.getEntity().getSide().equals(JTSUtils.LEFT)) {
				return opL;
			} else if (service.getEntity().getSide().equals(JTSUtils.RIGHT)) {
				return opR;
			}
			return (dL < dR) ? opL : opR;
		} else
			return newLocation;
	}

}
