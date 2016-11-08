package pl.edu.asim.service.mobility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.sim.deployment.RandomDeployment;
import pl.edu.asim.sim.deployment.SelfOrganizeDeployment;
import pl.edu.asim.sim.deployment.TriangularGridDeployment;
import pl.edu.asim.util.Matrix;

import com.vividsolutions.jts.geom.GeometryFactory;

public class Team extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean active = true;

	private List<Entity> nodes = new ArrayList<Entity>();
	private List<Goal> goals = new ArrayList<Goal>();

	public List<Entity> getNodes() {
		return nodes;
	}

	public void setNodes(List<Entity> nodes) {
		this.nodes = nodes;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void addNode(Entity node) {
		nodes.add(node);
		Collections.sort(getNodes());
	}

	@Override
	public void read(ASimDO data) {
		this.setName(data.getName());
		this.setId("" + data.getId());

		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("NODES")) {
				for (ASimDO entity : d.getChildren()) {
					if (entity.getType().equals("NODE")) {
						MobileEntity o = new MobileEntity();
						o.setSvgManager(getSvgManager());
						o.read(entity);
						if (o.isActive()) {
							nodes.add(o);
						} else
							continue;
					}
				}
			}
		}

		for (ASimDO d : data.getChildren()) {

			if (d.getType().equals("GOALS")) {
				for (ASimDO entity : d.getChildren()) {
					if (entity.getType().equals("GOAL")) {
						Goal o = new Goal();
						o.setSvgManager(getSvgManager());
						o.read(entity);
						goals.add(o);
					}
					if (entity.getType().equals("TRIANGULAR_GRID")) {
						TriangularGridDeployment o = new TriangularGridDeployment();
						o.setSvgManager(getSvgManager());
						o.setGeometryFactory(new GeometryFactory());
						o.setJts(new JTSUtils());
						o.read(entity);
						goals.add(o);
					}
					if (entity.getType().equals("RANDOM_GRID")) {
						RandomDeployment rd = new RandomDeployment();
						rd.setSvgManager(getSvgManager());
						rd.setGeometryFactory(new GeometryFactory());
						rd.setJts(new JTSUtils());
						rd.read(entity);
						goals.add(rd);
					}
					if (entity.getType().equals("SELFORGANIZE_GRID")) {
						SelfOrganizeDeployment rd = new SelfOrganizeDeployment();
						rd.setSvgManager(getSvgManager());
						rd.setGeometryFactory(new GeometryFactory());
						rd.setJts(new JTSUtils());
						rd.read(entity);
						for (ASimPO p : entity.getProperties()) {
							if (p.getCode().equals("teamMatrix")) {
								rd.setTeamMatrix(new Matrix(p.getValue()));
							}
						}
						goals.add(rd);
					}
				}
			}
		}

		// cele globalne
		for (Goal g : goals) {
			if (g.isActive() && g instanceof TriangularGridDeployment) {

				// TriangularGridDeployment grid = (TriangularGridDeployment) g;
				// ArrayList<Goal> goalList = grid.getGoalList(null);
				//
				// int iPoint = 0;
				// for (Entity entity : nodes) {
				// if (goalList.size() <= iPoint)
				// break;
				// if (!entity.getName().equals(bs_name)) {
				// Goal goal = goalList.get(iPoint);
				// ((MobileEntity) entity).getGoals().add(goal);
				// iPoint++;
				// }
				// }
			} else if (g.isActive() && g instanceof RandomDeployment) {
				// for (Entity entity : nodes) {
				// ((MobileEntity) entity).getGoals().add(
				// ((RandomDeployment) g).getAsGoal(
				// area.getObstacles(),
				// ((MobileEntity) entity).getBuffer()));
				// }
			} else if (g.isActive()) {
				for (Entity entity : nodes) {
					((MobileEntity) entity).getGoals().add(g.getAsGoal());
				}
			}
		}

		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("name")) {
				this.setName(p.getValue());
			} else if (p.getCode().equals("id")) {
				this.setId(p.getValue());
			} else if (p.getCode().equals("enabled")) {
				this.setActive(new Boolean(p.getValue()));
			}
		}
	}

}
