package pl.edu.econet.topology;

import java.util.ArrayList;
import java.util.List;

import javax.naming.CompositeName;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.util.Matrix;
import pl.edu.econet.Network;
import pl.edu.econet.energy.EAS;

public class Link implements Comparable<Link> {

	Network network;

	String source;
	String sourceRouter;

	String sink;
	String sinkRouter;

	String id;

	List<EAS> levels;
	Link returnLink;
	String returnLinkName;

	EAS eas;

	public Link() {
		levels = new ArrayList<EAS>();
	}

	public List<EAS> getLevels() {
		return levels;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLevels(List<EAS> levels) {
		this.levels = levels;
	}

	public String getSource() {
		return source.toString();
	}

	public void setSource(String source) {
		this.source = source;
		try {
			CompositeName port = new CompositeName(source);
			sourceRouter = port.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSink() {
		return sink;
	}

	public void setSink(String sink) {
		this.sink = sink;
		try {
			CompositeName port = new CompositeName(sink);
			sinkRouter = port.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* a */
	public boolean isOutgoingFromRouter(String routerId) {
		return sourceRouter.equals(routerId);
	}

	public Integer getA(String routerId) {
		return (isOutgoingFromRouter(routerId)) ? 1 : 0;
	}

	public boolean isOutgoingFromCard(String routerId, String cardId) {
		return (routerId + "/" + cardId).equals(source);
	}

	public Integer getA(String routerId, String cardId) {
		return ((routerId + "/" + cardId).equals(source)) ? 1 : 0;
	}

	/* b */
	public boolean isIncomingToRouter(String routerId) {
		return sinkRouter.equals(routerId);
	}

	public boolean isIncomingToCard(String routerId, String cardId) {
		return (routerId + "/" + cardId).equals(sink);
	}

	public Integer getB(String routerId) {
		return (isIncomingToRouter(routerId)) ? 1 : 0;
	}

	public Integer getB(String routerId, String cardId) {
		return ((routerId + "/" + cardId).equals(sink)) ? 1 : 0;
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public Link getReturnLink() {
		return returnLink;
	}

	public void setReturnLink(Link returnLink) {
		this.returnLink = returnLink;
	}

	public ASimDO getAsData() {
		ASimDO result = new ASimDO();
		result.setName(this.getId());

		ASimPO source = new ASimPO();
		source.setFather(result);
		source.setCode("source");
		source.setValue("" + this.getSource());

		ASimPO sink = new ASimPO();
		sink.setFather(result);
		sink.setCode("sink");
		sink.setValue("" + this.getSink());

		ASimPO easMatrix = new ASimPO();
		easMatrix.setFather(result);
		easMatrix.setCode("easMatrix");
		Matrix n_matrix = Matrix.zeros(this.getLevels().size(), 3);
		int i = 0;
		for (EAS eas : this.getLevels()) {
			n_matrix.setAsString(eas.getId(), i, 0);
			n_matrix.setAsString("" + eas.getPower(), i, 1);
			n_matrix.setAsString("" + eas.getBandwidth(), i, 2);
			i++;
		}
		easMatrix.setValue("" + n_matrix.exportToCSV());

		if (this.getReturnLink() != null) {
			ASimPO returnLink = new ASimPO();
			returnLink.setFather(result);
			returnLink.setCode("returnLink");
			returnLink.setValue("" + this.getReturnLink().getId());
			result.getProperties().add(returnLink);
		}
		result.getProperties().add(source);
		result.getProperties().add(sink);
		result.getProperties().add(easMatrix);
		result.setType("LINK");

		return result;
	}

	public void setData(ASimDO data) {
		this.setId(data.getName());
		for (ASimPO p : data.getProperties()) {
			if (p.getValue() == null || p.getValue().equals(""))
				continue;

			if (p.getCode().equals("source"))
				this.setSource(p.getValue());
			if (p.getCode().equals("sink"))
				this.setSink(p.getValue());
			if (p.getCode().equals("returnLink"))
				this.setReturnLinkName(p.getValue());
			if (p.getCode().equals("easMatrix")) {
				Matrix n_matrix = new Matrix(p.getValue());

				double pPower = 0.0;
				double pBandwidth = 0.0;
				for (int i = 1; i <= n_matrix.getRowCount(); i++) {
					EAS eas = new EAS();
					eas.setId("" + n_matrix.getAsInteger(i - 1, 0));
					eas.setPower(n_matrix.getAsDouble(i - 1, 1));
					eas.setBandwidth(n_matrix.getAsDouble(i - 1, 2));
					eas.setMinPower(pPower);
					eas.setMinBandwidth(pBandwidth);
					pPower = eas.getPower();
					pBandwidth = eas.getBandwidth();
					this.getLevels().add(eas);
				}
			}
		}
		// for (ASimDO d : data.getChildren()) {
		// if (d.getType().equals("LEVELS")) {
		// for (ASimDO r : d.getChildren()) {
		// EAS eas = new EAS();
		// eas.setData(r);
		// this.getLevels().add(eas);
		// }
		// }
		// }
	}

	public void updateData(ASimDO data) {
		this.setId(data.getName());
		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("LEVELS")) {
				for (ASimDO r : d.getChildren()) {
					EAS eas = new EAS();
					eas.setData(r);
					this.getLevels().add(eas);
				}
			}
		}
		if (this.getLevels().size() == 0)
			return;
		EAS[] easList = new EAS[this.getLevels().size()];
		for (EAS eas : this.getLevels()) {
			easList[new Integer(eas.getId()).intValue() - 1] = eas;
		}

		for (ASimPO p : data.getProperties()) {
			if (p.getValue() == null || p.getValue().equals(""))
				continue;

			if (p.getCode().equals("easMatrix")) {
				Matrix n_matrix = Matrix.zeros(this.getLevels().size(), 3);
				int i = 0;
				for (EAS eas : easList) {
					n_matrix.setAsString(eas.getId(), i, 0);
					n_matrix.setAsString("" + eas.getPower(), i, 1);
					n_matrix.setAsString("" + eas.getBandwidth(), i, 2);
					i++;
				}
				p.setValue("" + n_matrix.exportToCSV());
			}
		}
	}

	public EAS getEas() {
		return eas;
	}

	public void setEas(EAS eas) {
		this.eas = eas;
	}

	public String getReturnLinkName() {
		return returnLinkName;
	}

	public void setReturnLinkName(String returnLinkName) {
		this.returnLinkName = returnLinkName;
	}

	@Override
	public int compareTo(Link o) {

		int result = this.getSource().compareTo(o.getSource());
		if (result == 0)
			result = this.getSink().compareTo(o.getSink());

		// TODO Auto-generated method stub
		return result;
	}

	public String getSourceRouter() {
		return sourceRouter;
	}

	public void setSourceRouter(String sourceRouter) {
		this.sourceRouter = sourceRouter;
	}

	public String getSinkRouter() {
		return sinkRouter;
	}

	public void setSinkRouter(String sinkRouter) {
		this.sinkRouter = sinkRouter;
	}

}
