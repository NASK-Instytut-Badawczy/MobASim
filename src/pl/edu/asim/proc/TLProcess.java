package pl.edu.asim.proc;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import pl.edu.asim.model.ASimDO;

public abstract class TLProcess implements Callable<Integer>,
		Comparable<TLProcess> {

	public static Integer SUCCESS = 0;
	public static Integer FAIL = 1;

	public static Integer SERVICE = 0;
	public static Integer SYNCHRO = 1;

	ASimDO parameters;

	private double timestamp;
	private double dt;
	private BigInteger calculationTime;

	private Integer ID;
	private TLTree tree;
	private TLCard card;
	private TLBuffer queue;
	private final Integer stage = SERVICE;

	private Logger logger;

	public TLCard getCard() {
		return card;
	}

	public void setCard(TLCard card) {
		this.card = card;
	}

	public TLTree getTree() {
		return tree;
	}

	public void setTree(TLTree tree) {
		this.tree = tree;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public abstract Integer service() throws Exception;

	public Integer synchronization() throws Exception {
		return SUCCESS;
	}

	@Override
	public Integer call() throws Exception {
		if (stage == SERVICE)
			return service();
		else if (stage == SYNCHRO)
			return synchronization();
		return FAIL;
	}

	public TLBuffer getQueue() {
		return queue;
	}

	public void setQueue(TLBuffer queue) {
		this.queue = queue;
	}

	@Override
	public int compareTo(TLProcess o) {
		if (calculationTime != null && o.getCalculationTime() != null)
			return o.getCalculationTime().compareTo(calculationTime);
		else
			return 1;
	}

	public BigInteger getCalculationTime() {
		return calculationTime;
	}

	public void setCalculationTime(BigInteger calculationTime) {
		this.calculationTime = calculationTime;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	public double getDt() {
		return dt;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
