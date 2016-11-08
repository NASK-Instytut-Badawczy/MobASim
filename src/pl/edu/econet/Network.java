package pl.edu.econet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.util.Matrix;
import pl.edu.econet.demand.Demand;
import pl.edu.econet.equipment.Router;
import pl.edu.econet.topology.Link;

public class Network {

	List<Router> routers;
	List<Link> links;
	List<Demand> demands;
	double overbooking;

	String tUnit;
	String pUnit;

	public Network() {
		routers = new ArrayList<Router>();
		links = new ArrayList<Link>();
		demands = new ArrayList<Demand>();
	}

	public List<Router> getRouters() {
		return routers;
	}

	public void setRouters(List<Router> routers) {
		this.routers = routers;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Demand> getDemands() {
		return demands;
	}

	public void setDemands(List<Demand> demands) {
		this.demands = demands;
	}

	public double getOverbooking() {
		return overbooking;
	}

	public void setOverbooking(double overbooking) {
		this.overbooking = overbooking;
	}

	public void setData(ASimDO data) {
		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("overbooking"))
				this.setOverbooking(new Double(p.getValue()));
			if (p.getCode().equals("tUnit"))
				this.settUnit(p.getValue());
			if (p.getCode().equals("pUnit"))
				this.setpUnit(p.getValue());
		}
		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("ROUTERS")) {
				for (ASimDO r : d.getChildren()) {
					Router router = new Router();
					router.setData(r);
					this.getRouters().add(router);
				}
			} else if (d.getType().equals("LINKS")) {
				Boolean bidirectional = false;
				for (ASimPO p : d.getProperties()) {
					if (p.getValue() == null || p.getValue().equals(""))
						continue;
					if (p.getCode().equals("bidirectional")) {
						bidirectional = new Boolean(p.getValue());
					}
				}

				for (ASimDO l : d.getChildren()) {
					Link link = new Link();
					link.setData(l);
					this.getLinks().add(link);
					if (bidirectional) {
						Link returnLink = new Link();
						returnLink.setId(link.getReturnLinkName());
						returnLink.getLevels().addAll(link.getLevels());
						returnLink.setSink(link.getSource());
						returnLink.setSource(link.getSink());
						returnLink.setReturnLinkName(link.getId());
						this.getLinks().add(returnLink);
					}
				}
			} else if (d.getType().equals("DEMANDS")) {
				for (ASimDO de : d.getChildren()) {
					if (de.getType().equals("DEMAND")) {
						Boolean active = false;
						for (ASimPO p : de.getProperties()) {
							if (p.getValue() == null || p.getValue().equals(""))
								continue;
							if (p.getCode().equals("active")) {
								active = new Boolean(p.getValue());
							}
						}
						if (active) {
							Demand demand = new Demand();
							demand.setData(de);
							this.getDemands().add(demand);
						}
					}
					if (de.getType().equals("DEMAND_MATRIX")) {
						Matrix n_matrix = null;
						Boolean active = false;
						for (ASimPO p : de.getProperties()) {
							if (p.getValue() == null || p.getValue().equals(""))
								continue;
							if (p.getCode().equals("active")) {
								active = new Boolean(p.getValue());
							} else if (p.getCode().equals("demandMatrix")) {
								n_matrix = new Matrix(p.getValue());
							}
						}
						if (active) {
							for (int r = 1; r < n_matrix.getRowCount(); r++) {
								for (int c = 1; c < n_matrix.getColumnCount(); c++) {
									Double value = n_matrix.getAsDouble(r, c);
									if (value > 0) {
										String id = de.getName()
												+ n_matrix.getAsString(r, 0)
												+ n_matrix.getAsString(0, c);
										Demand demand = new Demand();
										demand.setSource(n_matrix.getAsString(
												r, 0));
										demand.setSink(n_matrix.getAsString(0,
												c));
										demand.setVolume(value);
										demand.setId(id);
										this.getDemands().add(demand);
									}
								}
							}
						}
					}
				}
				Collections.sort(this.demands);
				Collections.sort(this.routers);
				Collections.sort(this.links);
				// for (Demand demand : demands) {
				// System.out.println(demand.getVolume() + " "
				// + demand.getSource() + " " + demand.getSink());
				// }
			}
		}
	}

	public ASimDO getAsData() {
		ASimDO result = new ASimDO();
		ASimPO overbooking = new ASimPO();
		overbooking.setFather(result);
		overbooking.setCode("overbooking");
		overbooking.setValue("" + this.getOverbooking());
		result.getProperties().add(overbooking);
		result.setType("WIRED_NETWORK");

		ASimPO tUnit = new ASimPO();
		tUnit.setFather(result);
		tUnit.setCode("tUnit");
		tUnit.setValue(this.gettUnit());
		result.getProperties().add(tUnit);

		ASimPO pUnit = new ASimPO();
		pUnit.setFather(result);
		pUnit.setCode("pUnit");
		pUnit.setValue(this.getpUnit());
		result.getProperties().add(pUnit);

		int i = 0;
		ASimDO routers = new ASimDO();
		routers.setName("Routers");
		routers.setFather(result);
		routers.setType("ROUTERS");
		result.getChildren().add(routers);
		for (Router r : this.getRouters()) {
			ASimDO ld = r.getAsData();
			ld.setFather(routers);
			ld.setOrder(i);
			routers.getChildren().add(ld);
			i++;
		}
		i = 0;
		ASimDO links = new ASimDO();
		links.setFather(result);
		links.setType("LINKS");
		links.setName("Links");
		result.getChildren().add(links);
		for (Link l : this.getLinks()) {
			ASimDO ld = l.getAsData();
			ld.setFather(links);
			ld.setOrder(i);
			links.getChildren().add(ld);
			i++;
		}
		i = 0;
		ASimDO demands = new ASimDO();
		demands.setFather(result);
		demands.setType("DEMANDS");
		demands.setName("Demands");
		result.getChildren().add(demands);
		for (Demand d : this.getDemands()) {
			ASimDO dd = d.getAsData();
			dd.setFather(demands);
			dd.setOrder(i);
			demands.getChildren().add(dd);
			i++;
		}
		return result;
	}

	public String gettUnit() {
		return tUnit;
	}

	public void settUnit(String tUnit) {
		this.tUnit = tUnit;
	}

	public String getpUnit() {
		return pUnit;
	}

	public void setpUnit(String pUnit) {
		this.pUnit = pUnit;
	}
}
