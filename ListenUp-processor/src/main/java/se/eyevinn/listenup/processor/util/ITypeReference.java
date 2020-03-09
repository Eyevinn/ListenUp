package se.eyevinn.listenup.processor.util;

/**
 * 
 * @author Mattias Selin
 *
 */
public interface ITypeReference {
	void addImports(IImportContext.IImportContextRegistry registry);
	/**
	 * Returns the alias for this type
	 * 
	 * @param importContext
	 * @return
	 */
	String resolveAlias(ImportContext importContext);
}
