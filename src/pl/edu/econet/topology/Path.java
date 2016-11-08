package pl.edu.econet.topology;

import java.util.ArrayList;
import java.util.HashSet;

import pl.edu.econet.demand.Demand;

public class Path {

	Demand demand;
	ArrayList<Link> links = new ArrayList<Link>();

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public Link getLinkWithSource(String source) {
		for (Link link : links) {
			if (link.getSourceRouter().equals(source))
				return link;
		}
		return null;
	}

	public Link getLinkWithSink(String sink) {
		for (Link link : links) {
			if (link.getSinkRouter().equals(sink))
				return link;
		}
		return null;
	}

	public String getAsSortedString(String fromSource) {

		HashSet<String> hs = new HashSet<String>();
		String result2 = "";
		boolean p = false;
		for (Link link : links) {
			result2 = result2 + link.getSourceRouter() + " $\\rightarrow$ "
					+ link.getSinkRouter() + ",";
			if (hs.contains(link.getSourceRouter())) {
				p = true;
			}
			hs.add(link.getSourceRouter());
		}
		if (p)
			return result2;

		Link link = getLinkWithSource(fromSource);
		if (link == null)
			return "";
		String result = link.getSource();
		while (link != null) {
			result = result + " $\\rightarrow$ " + link.getSink();
			link = getLinkWithSource(link.getSinkRouter());
		}
		return result;
	}

	public Demand getDemand() {
		return demand;
	}

	public void setDemand(Demand demand) {
		this.demand = demand;
	}

}
