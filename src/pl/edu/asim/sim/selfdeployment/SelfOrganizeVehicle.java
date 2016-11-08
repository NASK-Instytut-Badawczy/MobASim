package pl.edu.asim.sim.selfdeployment;

import pl.edu.asim.service.mobility.MobileEntity;
import pl.edu.asim.util.Matrix;

public class SelfOrganizeVehicle extends MobileEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4397413393895120187L;

	private Matrix goalsMatrix;

	public Matrix getGoalsMatrix() {
		return goalsMatrix;
	}

	public void setGoalsMatrix(Matrix goalsMatrix) {
		this.goalsMatrix = goalsMatrix;
	}

}
