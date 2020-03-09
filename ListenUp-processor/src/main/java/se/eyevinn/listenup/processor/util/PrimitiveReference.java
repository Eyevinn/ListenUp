package se.eyevinn.listenup.processor.util;

import se.eyevinn.listenup.processor.util.IImportContext.IImportContextRegistry;

/**
 * 
 * @author Mattias Selin
 *
 */
public class PrimitiveReference implements ITypeReference {
	public static final PrimitiveReference BOOLEAN = new PrimitiveReference("boolean");
	public static final PrimitiveReference BYTE = new PrimitiveReference("byte");
	public static final PrimitiveReference CHAR = new PrimitiveReference("char");
	public static final PrimitiveReference DOUBLE = new PrimitiveReference("double");
	public static final PrimitiveReference FLOAT = new PrimitiveReference("float");
	public static final PrimitiveReference INT = new PrimitiveReference("int");
	public static final PrimitiveReference LONG = new PrimitiveReference("long");
	public static final PrimitiveReference SHORT = new PrimitiveReference("short");
	
	private final String primitiveName;
	
	private PrimitiveReference(String primitiveName) {
		this.primitiveName = primitiveName;
	}

	@Override
	public void addImports(IImportContextRegistry registry) {
	}

	@Override
	public String resolveAlias(ImportContext importContext) {
		return primitiveName;
	}
}
