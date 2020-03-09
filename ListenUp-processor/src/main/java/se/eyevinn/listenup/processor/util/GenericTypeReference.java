package se.eyevinn.listenup.processor.util;

import se.eyevinn.listenup.processor.util.IImportContext.IImportContextRegistry;
import se.eyevinn.listenup.processor.util.StringUtil.IStringJoiner;

/**
 * 
 * @author Mattias Selin
 *
 */
public class GenericTypeReference implements ITypeReference {
	private final ITypeReference genericType;
	private final ITypeReference[] typeParameters;
	
	public GenericTypeReference(ITypeReference genericType, ITypeReference ... typeParameters) {
		this.genericType = genericType;
		this.typeParameters = typeParameters;
	}

	@Override
	public void addImports(IImportContextRegistry registry) {
		genericType.addImports(registry);
		for(ITypeReference typeParameter : typeParameters) {
			typeParameter.addImports(registry);
		}
	}

	@Override
	public String resolveAlias(ImportContext importContext) {
		String genericTypeAlias = genericType.resolveAlias(importContext);
		IStringJoiner joiner = StringUtil.joiner(",");
		for(ITypeReference typeParameter : typeParameters) {
			joiner.add(typeParameter.resolveAlias(importContext));
		}
		return genericTypeAlias+"<"+joiner+">";
//		return "GEN("+genericTypeAlias+")<"+joiner+">";
//		GEN(Integer>>)<GEN(Integer>)<String,Integer>>
	}
}
