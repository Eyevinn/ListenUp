package se.eyevinn.listenup.event;

/**
 * 
 * @author Mattias Selin
 *
 * @param <L>
 */
public interface IEvent<L> {
	void send(L listener);
}
