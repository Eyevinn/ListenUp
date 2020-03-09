package se.eyevinn.listenup.processor.model;

import java.util.List;

import se.eyevinn.listenup.processor.util.VoidReference;

/**
 * 
 * @author Mattias Selin
 *
 */
public class EventModel extends AbstractMethodModel {
	
	public EventModel(String name, List<ParameterModel> parameters) {
		super(VoidReference.INSTANCE, name, parameters);
	}
}
