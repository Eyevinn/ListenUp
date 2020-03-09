package se.eyevinn.listenup.processor.util;

import se.eyevinn.listenup.processor.util.IImportContext.IImportContextRegistry;

/**
 * 
 * @author Mattias Selin
 *
 */
public class ClassReference implements ITypeReference {
	private final Qname qname;

	public ClassReference(Class<?> clazz) {
		this(new Qname(clazz));
	}
	
	public ClassReference(Qname qname) {
		this.qname = qname;
	}
	
	public ClassReference(String qname) {
		this(new Qname(qname));
	}

	@Override
	public void addImports(IImportContextRegistry registry) {
		registry.addImport(qname);
	}


	@Override
	public String resolveAlias(ImportContext importContext) {
		return importContext.getAlias(qname);
	}
}
