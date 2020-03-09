package se.eyevinn.listenup.event;

/**
 * 
 * @author Mattias Selin
 *
 * @param <L>
 */
public interface IEventSource<L> {
	boolean addListener(L listener);
	boolean removeListener(L listener);
}
