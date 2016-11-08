package pl.edu.econet.equipment;

import java.util.ArrayList;
import java.util.List;

import pl.edu.asim.model.ASimDO;
import pl.edu.econet.Network;
import pl.edu.econet.energy.EAS;

public class Router implements Comparable<Router> {

	String id;
	List<Card> cards;
	EAS idle;
	EAS busy;
	boolean isActive = false;

	public Router() {
		cards = new ArrayList<Card>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
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

	public void init(Network network) {
		for (Card c : cards) {
			c.setRouter(this);
			c.init(network);
		}
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

		ASimDO cards = new ASimDO();
		cards.setFather(result);
		cards.setType("CARDS");
		cards.setName("Cards");
		result.getChildren().add(cards);
		int i = 0;
		for (Card c : this.getCards()) {
			ASimDO ld = c.getAsData();
			ld.setFather(cards);
			ld.setOrder(i);
			cards.getChildren().add(ld);
			i++;
		}

		result.setType("ROUTER");
		return result;
	}

	public void setData(ASimDO data) {
		this.setId(data.getName());
		for (ASimDO d : data.getChildren()) {
			if (d.getType().equals("CARDS")) {
				for (ASimDO r : d.getChildren()) {
					Card card = new Card();
					card.setData(r);
					this.getCards().add(card);
				}
			} else if (d.getType().equals("IDLE")) {
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

	@Override
	public int compareTo(Router o) {
		if (this.getBusy().getPower() < o.getBusy().getPower())
			return 1;
		if (this.getBusy().getPower() > o.getBusy().getPower())
			return -1;

		return o.getId().compareTo(this.getId());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
