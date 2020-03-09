package se.eyevinn.listenup.processor.model;

import java.util.ArrayList;
import java.util.List;

import se.eyevinn.listenup.annotation.ListenerType;

/**
 * 
 * @author Mattias Selin
 *
 */
public class ListenerModel {
	private final String name;
	private final String qname;
	private final List<EventModel> events = new ArrayList<EventModel>();
	private final ListenerType[] listenerTypes;
	private final ListenerType[] excludedListenerTypes;
	
	public ListenerModel(String name, String qname, ListenerType[] listenerTypes, ListenerType[] excludedListenerTypes) {
		this.name = name;
		this.qname = qname;
		this.listenerTypes = listenerTypes;
		this.excludedListenerTypes = excludedListenerTypes;
	}

	public String getName() {
		return name;
	}
	
	public String getQname() {
		return qname;
	}
	
	public List<EventModel> getEvents() {
		return events;
	}
	
	public void addEvent(EventModel event) {
		events.add(event);
	}
	
	public boolean shouldGenerate(ListenerType listenerType) {
		if(listenerTypes == null) {
			return false;
		}
		for(ListenerType requested : excludedListenerTypes) {
			if(requested == ListenerType.ALL || requested == listenerType) {
				return false;
			}
		}
		for(ListenerType requested : listenerTypes) {
			if(requested == ListenerType.ALL || requested == listenerType) {
				return true;
			}
		}
		return false;
	}
}
