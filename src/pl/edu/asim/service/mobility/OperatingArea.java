package pl.edu.asim.service.mobility;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Polygon;

public class OperatingArea {

	Double width;
	Double height;
	Double cellSize;
	Double scale = 1.0;

	private List<Obstacle> obstacles = new ArrayList<Obstacle>();
	private List<Team> teams = new ArrayList<Team>();
	private List<Goal> goals = new ArrayList<Goal>();
	private Team defaultTeam = new Team();

	private Polygon polygon;

	public OperatingArea() {
		teams.add(defaultTeam);
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getCellSize() {
		return cellSize;
	}

	public void setCellSize(Double cellSize) {
		this.cellSize = cellSize;
	}

	public void setObstacles(List<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Team getDefaultTeam() {
		return defaultTeam;
	}

	public void setDefaultTeam(Team defaultTeam) {
		this.defaultTeam = defaultTeam;
	}

	public void addNode(Entity node) {
		defaultTeam.addNode(node);
	}

	public List<Entity> getNodesList() {
		List<Entity> result = new ArrayList<Entity>();
		for (Team team : teams) {
			result.addAll(team.getNodes());
		}
		// Collections.sort(result);
		return result;
	}
}
