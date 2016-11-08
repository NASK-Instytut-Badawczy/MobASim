package pl.edu.asim.service.mobility;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;

public class Obstacle extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2590479636864711548L;

	private boolean active = true;

	@Override
	public void read(ASimDO data) {
		String svg_x = "0";
		String svg_y = "0";
		this.setName(data.getName());
		this.setId("" + data.getId());

		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("name")) {
				this.setName(p.getValue());
			} else if (p.getCode().equals("id")) {
				this.setId(p.getValue());
			} else if (p.getCode().equals("obstacle_active")) {
				this.setActive(new Boolean(p.getValue()));
			} else if (p.getCode().equals("svg_x")) {
				svg_x = p.getValue();
			} else if (p.getCode().equals("svg_y")) {
				svg_y = p.getValue();
			} else if (p.getCode().equals("svg_visibility")) {
				if (p.getValue().equals("hidden")) {
					this.setActive(false);
				} else {
					this.setActive(true);
				}
			}
		}
		svgZeroPoint = new OperatingAreaPoint(new Double(svg_x), new Double(
				svg_y));

		org.w3c.dom.Document svgDocument = getSvgManager().getSVGDocument(data,
				false);
		org.w3c.dom.NodeList nodeList = svgDocument
				.getElementsByTagName("polygon");

		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node node = nodeList.item(i);
			String points = ((org.w3c.dom.Element) node).getAttribute("points");
			String[] subpoints = points.split(" ");
			for (String point : subpoints) {
				String[] coordinate = point.split(",");
				OperatingAreaPoint oap = new OperatingAreaPoint(new Double(
						coordinate[0]), new Double(coordinate[1]));
				this.getPoints().add(oap);
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
