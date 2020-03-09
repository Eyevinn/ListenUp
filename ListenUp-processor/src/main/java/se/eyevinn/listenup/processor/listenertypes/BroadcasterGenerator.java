package se.eyevinn.listenup.processor.listenertypes;

import java.util.ArrayList;
import java.util.List;

import com.mattiasselin.linewriter.ILineSource;
import com.mattiasselin.linewriter.ILineWriter;

import se.eyevinn.listenup.event.IEventSource;
import se.eyevinn.listenup.processor.model.AbstractMethodModel;
import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.model.ParameterModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.LocalVariableNamePicker;
import se.eyevinn.listenup.processor.util.ReferenceFactory;
import se.eyevinn.listenup.processor.util.StringUtil;
import se.eyevinn.listenup.processor.util.StringUtil.IStringJoiner;

/**
 * 
 * @author Mattias Selin
 *
 */
public class BroadcasterGenerator implements IClassGenerator {

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		ITypeReference listenerListType = rf.g(List.class, listenerRef);
		ITypeReference listenerEventSource = rf.g(IEventSource.class, listenerRef);
		
		body.println("public final class "+className+" implements "+importContext.getName(listenerRef)+", "+importContext.getName(listenerEventSource)+" {");
			body.indent();
			body.println("private final "+importContext.getName(listenerListType)+" listeners = new "+importContext.getName(rf.g(ArrayList.class))+"();");
			body.println();
			AbstractMethodModel addListenerMethod = new AbstractMethodModel(rf._boolean(),"addListener", new ParameterModel(listenerRef, "listener"));
			AbstractMethodModel removeListenerMethod = new AbstractMethodModel(rf._boolean(),"removeListener", new ParameterModel(listenerRef, "listener"));
			
			addListenerMethod.override(importContext, new ILineSource() {
				@Override
				public void writeTo(ILineWriter methodBody) {
					methodBody.println("return this.listeners.add(listener);");
				}
			}).writeTo(body);
			
			removeListenerMethod.override(importContext, new ILineSource() {
				@Override
				public void writeTo(ILineWriter methodBody) {
					methodBody.println("return this.listeners.remove(listener);");
				}
			}).writeTo(body);
			
			
			for(EventModel eventModel : listenerModel.getEvents()) {
				IStringJoiner arguments = StringUtil.joiner(", ");
				for(ParameterModel parameterModel : eventModel.parameters) {
					arguments.add(parameterModel.name);
				}
				body.println();
				eventModel.override(importContext, new ILineSource() {
					@Override
					public void writeTo(ILineWriter lineWriter) {
						LocalVariableNamePicker namePicker = new LocalVariableNamePicker();
						eventModel.registerParameterNames(namePicker);
						String listenerName = namePicker.getSafeName("listener", true);
						lineWriter.println("for("+importContext.getName(listenerRef)+" "+listenerName+" : this.listeners) {");
							lineWriter.indent();
							lineWriter.println(listenerName+"."+eventModel.name+"("+arguments+");");
							lineWriter.unindent();
						lineWriter.println("}");
					}
				}).writeTo(body);
			}
			
			body.unindent();
		body.println("}");
	}

}
