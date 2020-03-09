package se.eyevinn.listenup.processor.model;

import se.eyevinn.listenup.processor.util.ITypeReference;

/**
 * 
 * @author Mattias Selin
 *
 */
public class ParameterModel {
	public final ITypeReference type;
	public final String name;
	
	public ParameterModel(ITypeReference type, String name) {
		this.type = type;
		this.name = name;
	}
}
