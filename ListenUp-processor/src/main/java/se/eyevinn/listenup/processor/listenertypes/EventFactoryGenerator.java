package se.eyevinn.listenup.processor.listenertypes;

import com.mattiasselin.linewriter.ILineWriter;
import com.mattiasselin.linewriter.LineWriter;

import se.eyevinn.listenup.event.IEvent;
import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.model.ParameterModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.ReferenceFactory;
import se.eyevinn.listenup.processor.util.StringUtil;

/**
 * 
 * @author Mattias Selin
 *
 */
public class EventFactoryGenerator implements IClassGenerator {

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		
		ITypeReference typedEventRef = rf.g(IEvent.class, listenerRef);
		
		body.println("public class "+className+" {");
		body.indent();
		for(EventModel eventModel : listenerModel.getEvents()) {
			StringUtil.IStringJoiner parameters = StringUtil.joiner(", ");
			StringUtil.IStringJoiner arguments = StringUtil.joiner(", ");
			for(ParameterModel parameterModel : eventModel.parameters) {
				parameters.add(importContext.getName(parameterModel.type)+" "+parameterModel.name);
				arguments.add(parameterModel.name);
			}
			body.println("public static "+importContext.getName(typedEventRef)+" "+eventModel.name+"("+parameters+") {");
			body.indent();
			body.println("return new Event_"+eventModel.name+"("+arguments+");");
			body.unindent();
			body.println("}");
			body.println();
		}
		
		body.println();
		
		for(EventModel eventModel : listenerModel.getEvents()) {
			StringUtil.IStringJoiner parameters = StringUtil.joiner(", ");
			StringUtil.IStringJoiner arguments = StringUtil.joiner(", ");
			LineWriter privateFields = new LineWriter();
			LineWriter constructorAssignments = new LineWriter();
			
			for(ParameterModel parameterModel : eventModel.parameters) {
				String typeAndName = importContext.getName(parameterModel.type)+" "+parameterModel.name;
				parameters.add(typeAndName);
				arguments.add("this."+parameterModel.name);
				privateFields.println("private final "+typeAndName+";");
				constructorAssignments.println("this."+parameterModel.name+" = "+parameterModel.name+";");
			}
			
			String eventTypeName = "Event_"+eventModel.name;
			
			body.println();
			body.println("private static class "+eventTypeName+" implements "+importContext.getName(typedEventRef)+" {");
			body.indent();
			privateFields.writeTo(body);
			
			body.println("public "+eventTypeName+"("+parameters+") {");
			body.indent();
			constructorAssignments.writeTo(body);
			body.unindent();
			body.println("}");
			
			
			body.println("@Override");
			body.println("public void send("+importContext.getName(listenerRef)+" listener) {");
			body.indent();
			body.println("listener."+eventModel.name+"("+arguments+");");
			body.unindent();
			body.println("}");
			
			body.unindent();
			body.println("}");
		}
		
		body.unindent();
		body.println("}");
	}

}
