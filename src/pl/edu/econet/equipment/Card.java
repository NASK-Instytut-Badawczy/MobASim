package pl.edu.econet.equipment;

import pl.edu.asim.model.ASimDO;
import pl.edu.econet.Network;
import pl.edu.econet.energy.EAS;

public class Card {

	String id;
	Router router;
	EAS idle;
	EAS busy;
	boolean isActive = false;

	public Card() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return router.getId() + "/" + id;
	}

	public Router getRouter() {
		return router;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public EAS getIdle() {
		return idle;
	}

	public void setIdle(EAS idle) {
		this.idle = idle;
	}

	public EAS getBusy() {
		return busy;
	}

	public void setBusy(EAS busy) {
		this.busy = busy;
	}

	public double getEcoAdvantage() {
		return (busy.getPower() > idle.getPower()) ? busy.getPower()
				- idle.getPower() : 0.0;
	}

	/* g */
	public boolean belongsToRouter(String routerId) {
		return router.getId().equals(routerId);
	}

	public Integer getG(String routerId) {
		return (belongsToRouter(routerId)) ? 1 : 0;
	}

	public void init(Network network) {

	}

	public ASimDO getAsData() {
		ASimDO result = new ASimDO();
		result.setName(this.getId());

		ASimDO busy = this.busy.getAsData();
		busy.setFather(result);
		busy.setType("BUSY");
		busy.setName("EAS: busy");
		result.getChildren().add(busy);

		ASimDO idle = this.idle.getAsData();
		idle.setFather(result);
		idle.setType("IDLE");
		idle.setName("EAS: idle");
		result.getChildren().add(idle);

		// ASimDO ports = new ASimDO();
		// ports.setFather(result);
		// ports.setType("PORTS");
		// ports.setName("Ports");
		// result.getChildren().add(ports);

		result.setType("CARD");
		return result;
	}

	public void setData(ASimDO data) {
		this.setId(data.getName());
		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("IDLE")) {
				EAS eas = new EAS();
				eas.setData(d);
				this.setIdle(eas);
			} else if (d.getType().equals("BUSY")) {
				EAS eas = new EAS();
				eas.setData(d);
				this.setBusy(eas);
			}
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
