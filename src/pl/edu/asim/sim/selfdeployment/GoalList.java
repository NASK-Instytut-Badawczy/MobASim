package pl.edu.asim.sim.selfdeployment;

import java.util.ArrayList;

import pl.edu.asim.service.mobility.Goal;

public class GoalList {

	private ArrayList<Goal> influenceList;
	private Double qualityThreshold;

	public GoalList(){
		influenceList = new ArrayList<Goal>();
	}
	
	public ArrayList<Goal> getInfluenceList() {
		return influenceList;
	}
	public void setInfluenceList(ArrayList<Goal> influenceList) {
		this.influenceList = influenceList;
	}

	public Double getQualityThreshold() {
		return qualityThreshold;
	}

	public void setQualityThreshold(Double qualityThreshold) {
		this.qualityThreshold = qualityThreshold;
	}
	
}
