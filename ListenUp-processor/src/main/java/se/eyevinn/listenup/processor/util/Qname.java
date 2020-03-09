package se.eyevinn.listenup.processor.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Mattias Selin
 *
 */
public class Qname {
	public final String packageName;
	public final String typeName;
	
	public Qname(Class<?> clazz) {
		this(clazz.getName());
	}
	
	public Qname(String qname) {
		this(parse(qname));
	}
	
	private Qname(Qname copy) {
		this(copy.packageName, copy.typeName);
	}
	
	public Qname(String packageName, String typeName) {
		this.packageName = packageName;
		this.typeName = typeName;
	}
	
	@Override
	public String toString() {
		return (packageName != null ? (packageName+".") : "")+typeName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Qname) {
			Qname qname = (Qname) obj;
			return Objects.equals(this.typeName, qname.typeName) && Objects.equals(this.packageName, qname.packageName);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(packageName, typeName);
	}
	
	private static Qname parse(String qname) {
		if(qname.contains(".")) {
			int split = qname.lastIndexOf(".");
			return new Qname(qname.substring(0, split), qname.substring(split+1));
		} else {
			return new Qname(null, qname);
		}
	}
	
	public static void main(String[] args) {
		ImportContext importContext = new ImportContext("aaa");
		importContext.getName(new ClassReference("java.awt.List"));
//		String fully = importContext.getName(new GenericTypeReference(new ClassReference(List.class), new GenericTypeReference(new ClassReference(Map.class), new ClassReference(String.class), new ClassReference("cool.Matte"))));
		ReferenceFactory rf = new ReferenceFactory();
		String fully = importContext.getName(rf.g(List.class, rf.g(Map.class, rf.c(String.class), rf.c("aaa.Matte"))));
		
		importContext.getImports(new IImportContext.IImporter() {
			@Override
			public void addImport(Qname qname) {
				System.out.println("import "+qname+";");
			}
		});
		System.out.println();
		System.out.println(fully);
	}
}
