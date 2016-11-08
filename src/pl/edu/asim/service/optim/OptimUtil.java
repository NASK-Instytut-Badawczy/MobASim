package pl.edu.asim.service.optim;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;


public class OptimUtil {
    public OptimUtil() {
    }

    public static ArrayRealVector toArrayRealVector(Vector3D v) {
        ArrayRealVector result = new ArrayRealVector(3);
        result.setEntry(0, v.getX());
        result.setEntry(1, v.getY());
        result.setEntry(2, v.getZ());
        return result;
    }

    public static ArrayRealVector toArrayRealVector(Vector3D v, Vector3D l) {
        ArrayRealVector result = new ArrayRealVector(6);
        result.setEntry(0, v.getX());
        result.setEntry(1, v.getY());
        result.setEntry(2, v.getZ());
        result.setEntry(3, l.getX());
        result.setEntry(4, l.getY());
        result.setEntry(5, l.getZ());
        return result;
    }

    public static ArrayRealVector toArrayRealVector(Vector3D v, ArrayRealVector lambdas) {
        ArrayRealVector result = new ArrayRealVector(3+lambdas.getDimension());
        result.setEntry(0, v.getX());
        result.setEntry(1, v.getY());
        result.setEntry(2, v.getZ());
        for (int i = 0; i < lambdas.getDimension(); i++) {
            result.setEntry(i+3, lambdas.getEntry(i));        
        }
        return result;
    }

    public static Vector3D toVector3D(ArrayRealVector v) {
        return new Vector3D(v.getEntry(0), v.getEntry(1), v.getEntry(2));
    }

    public static ArrayRealVector toLambdasVector(ArrayRealVector v) {
        if (v.getDimension() <= 3)
            return new ArrayRealVector(0);
        ArrayRealVector result = new ArrayRealVector(v.getDimension() - 3);
        for (int i = 3; i < v.getDimension(); i++) {
            result.setEntry(i - 3, v.getEntry(i));
        }
        return result;
    }
}
