package pl.edu.asim.proc;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.edu.asim.model.ASimDO;

public class TLTree {

	List<TLProcess> processes;

	public ExecutorService executor;
	public CompletionService<Integer> ecs;

	public TLTree(int threadsPool) {
		if (threadsPool > 0)
			executor = Executors.newFixedThreadPool(threadsPool);
		else
			executor = Executors.newCachedThreadPool();
		// Runtime.getRuntime().availableProcessors();
		ecs = new ExecutorCompletionService<Integer>(executor);
	}

	public List<TLProcess> getProcesses() {
		return processes;
	}

	public void setProcesses(List<TLProcess> processes) {
		this.processes = processes;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public CompletionService<Integer> getEcs() {
		return ecs;
	}

	public void setEcs(CompletionService<Integer> ecs) {
		this.ecs = ecs;
	}

	public void generateCards(int fanIn) {

		for (int i = 0; i < processes.size(); i++) {
			TLCard card = new TLCard(fanIn);
			processes.get(i).setCard(card);
		}
	}

	public void init(ASimDO simulator) {

	}

	public HashMap<TLProcess, Integer> start() throws Exception {

		HashMap<TLProcess, Integer> result = new HashMap<TLProcess, Integer>();
		long time = System.currentTimeMillis();
		java.util.Collections.sort(processes);
		for (TLProcess process : processes) {
			ecs.submit(process);
		}
		for (TLProcess process : processes) {
			Integer r = ecs.take().get();
			result.put(process, r);
		}
		time = System.currentTimeMillis() - time;
		return result;
	}

}
