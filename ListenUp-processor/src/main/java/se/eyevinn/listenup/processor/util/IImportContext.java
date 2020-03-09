package se.eyevinn.listenup.processor.util;

/**
 * The ImportContext is responsible for keeping track of which
 * imports are added and the qualified names for {@link ITypeReference}s.
 * 
 * @author Mattias Selin
 *
 */
public interface IImportContext {
	
	String getAlias(Qname qname);
	
	/**
	 * Imports the type referenced if there are no name-collisions and 
	 * returns the type alias for the type.
	 * @param reference
	 * @return The type alias.
	 */
	String getName(ITypeReference reference);
	
	void getImports(IImporter importer);
	
	interface IImportContextRegistry {
		/**
		 * Tries to add the import. Returns true if added.
		 * 
		 * @param qname
		 * @return
		 */
		boolean addImport(Qname qname);
	}
	
	interface IImporter {
		void addImport(Qname qname);
	}
}
