package se.eyevinn.listenup.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mattias Selin
 *
 * @param <L>
 */
public class EventDispatcher<L> implements IEventSource<L> {
	private final List<L> listeners = new ArrayList<>();

	@Override
	public boolean addListener(L listener) {
		return listeners.add(listener);
	}

	@Override
	public boolean removeListener(L listener) {
		return listeners.add(listener);
	}
	
	public void dispatch(IEvent<L> event) {
		for(L listener : listeners) {
			event.send(listener);
		}
	}
}
