package pl.edu.asim.proc;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class TLBuffer {

	LinkedBlockingQueue<TLEvent> buffer;

	public TLBuffer() {
		buffer = new LinkedBlockingQueue<TLEvent>();
	}

	public LinkedBlockingQueue<TLEvent> getEvents() {
		return buffer;
	}

	public void remove(TLEvent e) {
		buffer.remove(e);
	}

	public void add(TLEvent e) {
		buffer.add(e);
	}

	public void add(Collection<TLEvent> m) {
		if (m != null)
			for (TLEvent mess : m) {
				buffer.add(mess);
			}
	}

	public TLEvent pop() {
		return buffer.poll();
	}

	public TLEvent top() {
		return buffer.peek();
	}

	public int size() {
		return buffer.size();
	}
}
