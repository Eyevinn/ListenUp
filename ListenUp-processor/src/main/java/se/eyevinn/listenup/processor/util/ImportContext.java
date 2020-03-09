package se.eyevinn.listenup.processor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Mattias Selin
 *
 */
public class ImportContext implements IImportContext {
	private final String packageName;
	private final ImportContextRegistry registry = new ImportContextRegistry();
	
	public ImportContext(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getName(ITypeReference reference) {
		reference.addImports(registry);
		return reference.resolveAlias(this);
	}

	private class ImportContextRegistry implements IImportContextRegistry {
		private Map<String, Qname> typeAliases = new HashMap<>();
		private List<Qname> samePackage = new ArrayList<Qname>();

		@Override
		public boolean addImport(Qname qname) {
			if(Objects.equals(packageName, qname.packageName)) {
				if(!samePackage.contains(qname)) {
					samePackage.add(qname);
				}
				return false;
			}
			if(typeAliases.containsKey(qname.typeName)) {
				return false;
			}
			typeAliases.put(qname.typeName, qname);
			return true;
		}
	}

	@Override
	public void getImports(IImporter importer) {
		for(Qname qname : registry.typeAliases.values()) {
			importer.addImport(qname);
		}
	}

	@Override
	public String getAlias(Qname qname) {
		if(registry.samePackage.contains(qname)) {
			return qname.typeName;
		}
		if(registry.typeAliases.containsKey(qname.typeName) && qname.equals(registry.typeAliases.get(qname.typeName))) {
			return qname.typeName;
		} else {
			return qname.toString();
		}
	}
}
