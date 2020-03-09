package se.eyevinn.listenup.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.mattiasselin.linewriter.ILineWriter;
import com.mattiasselin.linewriter.LineWriter;
import com.mattiasselin.linewriter.WriterLineWriter;

import se.eyevinn.listenup.annotation.Listener;
import se.eyevinn.listenup.annotation.ListenerType;
import se.eyevinn.listenup.annotation.options.BufferOptions;
import se.eyevinn.listenup.annotation.options.DispatcherOptions;
import se.eyevinn.listenup.annotation.options.LoggerOptions;
import se.eyevinn.listenup.processor.listenertypes.BaseGenerator;
import se.eyevinn.listenup.processor.listenertypes.BroadcasterGenerator;
import se.eyevinn.listenup.processor.listenertypes.BufferGenerator;
import se.eyevinn.listenup.processor.listenertypes.DispatcherGenerator;
import se.eyevinn.listenup.processor.listenertypes.EventFactoryGenerator;
import se.eyevinn.listenup.processor.listenertypes.IClassGenerator;
import se.eyevinn.listenup.processor.listenertypes.LoggerGenerator;
import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.model.ParameterModel;
import se.eyevinn.listenup.processor.util.ClassReference;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.ImportContext;
import se.eyevinn.listenup.processor.util.Qname;
import se.eyevinn.listenup.processor.util.ReferenceFactory;
import se.eyevinn.listenup.processor.util.IImportContext.IImporter;


/**
 * 
 * @author Mattias Selin
 *
 */
@SupportedAnnotationTypes({"se.eyevinn.listenup.annotation.Listener"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ListenerProcessor extends AbstractProcessor {
	
	public ListenerProcessor() { //TODO test without standard constructor. should work.
	}
	
	private void log(String message) {
		processingEnv.getMessager().printMessage(Kind.NOTE, message);
	}
	
	//TODO:
	// 1. Package of generated files should be related to package of source to avoid name clashes

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for(Element element : roundEnv.getElementsAnnotatedWith(Listener.class)) {
			ElementKind elementKind = element.getKind();
			if(elementKind == ElementKind.INTERFACE) {
				TypeElement listenerElement = ((TypeElement) element);
				Name listenerName = listenerElement.getSimpleName();
				Listener listenerAnnotation = listenerElement.getAnnotation(Listener.class);
				ListenerModel listenerModel = new ListenerModel(listenerName.toString(), listenerElement.getQualifiedName().toString(), listenerAnnotation.generate(), listenerAnnotation.dontGenerate());
				
				for(Element listenerEnclosed : listenerElement.getEnclosedElements()) {
					if(listenerEnclosed.getKind() == ElementKind.METHOD) {
						ExecutableElement methodElement = (ExecutableElement) listenerEnclosed;
						List<ParameterModel> parameters = new ArrayList<>();
						for(VariableElement variableElement : methodElement.getParameters()) {
							TypeMirror type = variableElement.asType();
							parameters.add(new ParameterModel(convertToReference(new ReferenceFactory(), type), variableElement.getSimpleName().toString()));
						}
						listenerModel.addEvent(new EventModel(listenerEnclosed.getSimpleName().toString(), parameters));
					}
				}
				
				generateFiles(listenerModel, listenerElement);
			}
		}
		return true;
	}
	
	private static ITypeReference convertToReference(ReferenceFactory rf, TypeMirror type) {
		switch (type.getKind()) {
			case ERROR:
				throw new RuntimeException("Could not resolve type");
			case BOOLEAN:
				return rf._boolean();
			case BYTE:
				return rf._byte();
			case CHAR:
				return rf._char();
			case DOUBLE:
				return rf._double();
			case FLOAT:
				return rf._float();
			case INT:
				return rf._int();
			case LONG:
				return rf._long();
			case SHORT:
				return rf._short();
			case DECLARED: {
				DeclaredType declaredType = (DeclaredType) type;
				String declaredTypeNestedQname = declaredType.toString();
				return rf.parse(declaredTypeNestedQname);
			}
		
			default:
				throw new RuntimeException("Could not handle kind "+type.getKind().name());
		}
	}

	private void generateFiles(ListenerModel listenerModel, Element listenerElement, Element ... elements) {
		String className = listenerModel.getName()+"_Events";
		//TODO base package on package of annotated listener
		BasicInfo baseData = new BasicInfo("listenup.generated.event", listenerModel, listenerElement, elements);
		ITypeReference eventFactory = new ClassReference(new Qname(baseData.packageName, className));
		
		generate(baseData, className, new EventFactoryGenerator());
		
		
		if(listenerModel.shouldGenerate(ListenerType.BASE)) {
			generateBaseListener(baseData);
		}
		if(listenerModel.shouldGenerate(ListenerType.BROADCASTER)) {
			generateBroadcaster(baseData);
		}
		if(listenerModel.shouldGenerate(ListenerType.LOGGER)) {
			generateLogger(baseData);
		}
		if(listenerModel.shouldGenerate(ListenerType.BUFFER)) {
			generateBuffer(baseData, eventFactory);
		}
		if(listenerModel.shouldGenerate(ListenerType.DISPATCHER)) {
			generateDispatcher(baseData);
		}
	}

	private void generateBaseListener(BasicInfo baseData) {
		String listenerName = baseData.listenerModel.getName();
		if(listenerName.startsWith("I")) {
			listenerName = listenerName.substring(1);
		}
		listenerName = "Base"+listenerName;
		generate(baseData, listenerName, new BaseGenerator());
	}
	
	private void generateBroadcaster(BasicInfo baseData) {
		String listenerName = baseData.listenerModel.getName();
		if(listenerName.startsWith("I")) {
			listenerName = listenerName.substring(1);
		}
		listenerName = listenerName+"EventBroadcaster";
		generate(baseData, listenerName, new BroadcasterGenerator());
	}
	
	private void generateLogger(BasicInfo baseData) {
		String listenerName = baseData.listenerModel.getName();
		if(listenerName.startsWith("I")) {
			listenerName = listenerName.substring(1);
		}
		listenerName = "Logging"+listenerName;
		generate(baseData, listenerName, new LoggerGenerator(baseData.getAnnotation(LoggerOptions.class)));
	}
	
	private void generateBuffer(BasicInfo baseData, ITypeReference eventFactory) {
		String listenerName = baseData.listenerModel.getName();
		if(listenerName.startsWith("I")) {
			listenerName = listenerName.substring(1);
		}
		listenerName = "Buffering"+listenerName;
		generate(baseData, listenerName, new BufferGenerator(baseData.getAnnotation(BufferOptions.class), eventFactory));
	}
	
	private void generateDispatcher(BasicInfo baseData) {
		String listenerName = baseData.listenerModel.getName();
		if(listenerName.startsWith("I")) {
			listenerName = listenerName.substring(1);
		}
		listenerName = listenerName+"EventDispatcher";
		generate(baseData, listenerName, new DispatcherGenerator(baseData.getAnnotation(DispatcherOptions.class)));
	}
	
	private void generate(BasicInfo baseData, String className, ICodeGen codeGen) {
		try {
			JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(baseData.packageName+"."+className, baseData.elements);
			try(PrintWriter pw = new PrintWriter(javaFileObject.openWriter(), true)) {
				codeGen.generateFile(pw, baseData.packageName, className);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void generate(BasicInfo baseData, String className, final IClassGenerator classGen) {
		generate(baseData, className, new ICodeGen() {
			@Override
			public void generateFile(PrintWriter pw, String packageName, String className) {
				ReferenceFactory rf = new ReferenceFactory();
				IImportContext importContext = new ImportContext(packageName);
				
				ILineWriter output = new WriterLineWriter(pw, true);
				output.println("package "+packageName+";");
				output.println();
				
				ITypeReference listenerRef = rf.c(baseData.listenerModel.getQname());
				
				LineWriter body = new LineWriter();
				
				classGen.generateClass(rf, importContext, className, baseData.listenerModel, body, listenerRef);
				
				importContext.getImports(new IImporter() {
					@Override
					public void addImport(Qname qname) {
						output.println("import "+qname+";");
					}
				});
				output.println();
				output.println();
				
				body.writeTo(output);
			}
		});
	}
	
	private static class BasicInfo {
		public final String packageName;
		public final ListenerModel listenerModel;
		public final Element[] elements;
		private final Element listenerElement;
		
		public BasicInfo(String packageName, ListenerModel listenerModel, Element listenerElement, Element ... elements) {
			this.packageName = packageName;
			this.listenerModel = listenerModel;
			this.elements = new Element[elements.length+1];
			this.elements[0] = listenerElement;
			System.arraycopy(elements, 0, this.elements, 1, elements.length);
			this.listenerElement = listenerElement;
		}
		
		public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
			return listenerElement.getAnnotation(annotationType);
		}
	}
	
	private interface ICodeGen {
		void generateFile(PrintWriter pw, String packageName, String className);
	}
}