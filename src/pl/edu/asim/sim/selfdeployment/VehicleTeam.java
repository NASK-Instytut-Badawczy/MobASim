package pl.edu.asim.sim.selfdeployment;

import java.util.ArrayList;

import pl.edu.asim.service.mobility.Goal;

public class VehicleTeam extends Goal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3784908508982599030L;

	private ArrayList<String> teamMembers;

	private Double positionAccuracy; // liczone

	private Double defaultDistance;

	public GoalList getAsGoalList(String vehicleName) {
		return null;
	}

	public ArrayList<String> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(ArrayList<String> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public Double getPositionAccuracy() {
		return positionAccuracy;
	}

	public void setPositionAccuracy(Double positionAccuracy) {
		this.positionAccuracy = positionAccuracy;
	}

}
