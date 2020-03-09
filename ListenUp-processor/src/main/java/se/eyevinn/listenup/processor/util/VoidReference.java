package se.eyevinn.listenup.processor.util;

import se.eyevinn.listenup.processor.util.IImportContext.IImportContextRegistry;

/**
 * 
 * @author Mattias Selin
 *
 */
public class VoidReference implements ITypeReference {
	public static final VoidReference INSTANCE = new VoidReference(); 
	
	private VoidReference() {
	}

	@Override
	public void addImports(IImportContextRegistry registry) {
	}

	@Override
	public String resolveAlias(ImportContext importContext) {
		return "void";
	}
}
