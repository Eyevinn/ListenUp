package se.eyevinn.listenup.processor.listenertypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mattiasselin.linewriter.ILineSource;
import com.mattiasselin.linewriter.ILineWriter;

import se.eyevinn.listenup.event.IEventSource;
import se.eyevinn.listenup.annotation.options.DispatcherOptions;
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
public class DispatcherGenerator implements IClassGenerator {
	private final Options options;
	
	public DispatcherGenerator(DispatcherOptions dispatcherOptions) {
		this.options = new Options(dispatcherOptions);
	}
	
	private String getFireEventName(EventModel eventModel) {
		String eventName = eventModel.name;
		if(options.stripOnPrefix) {
			if(eventName.toLowerCase(Locale.ENGLISH).startsWith("on")) {
				eventName = eventName.substring(2);
			}
		}
		return options.fireEventPrefix+StringUtil.capitalize(eventName);
	}

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		ITypeReference listenerListType = rf.g(List.class, listenerRef);
		ITypeReference listenerEventSource = rf.g(IEventSource.class, listenerRef);
		
		body.println("public final class "+className+" implements "+importContext.getName(listenerEventSource)+" {");
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
				IStringJoiner parameters = StringUtil.joiner(", ");
				for(ParameterModel parameterModel : eventModel.parameters) {
					arguments.add(parameterModel.name);
					parameters.add(importContext.getName(parameterModel.type)+" "+parameterModel.name);
				}
				LocalVariableNamePicker namePicker = new LocalVariableNamePicker();
				eventModel.registerParameterNames(namePicker);
				
				body.println();
				body.println("public void "+getFireEventName(eventModel)+" ("+parameters+") {");
					body.indent();
					String listenerLoopName = namePicker.getSafeName("listener", true);
					body.println("for("+importContext.getName(listenerRef)+" "+listenerLoopName+" : this.listeners) {");
						body.indent();
						body.println(listenerLoopName+"."+eventModel.name+"("+arguments+");");
						body.unindent();
					body.println("}");
					body.unindent();
				body.println("}");
			}
			
			body.unindent();
		body.println("}");
	}

	private static class Options {
		private final String fireEventPrefix;
		private final boolean stripOnPrefix;

		public Options(DispatcherOptions dispatcherOptions) {
			this.fireEventPrefix = dispatcherOptions != null ? dispatcherOptions.fireEventPrefix() : "fire";
			this.stripOnPrefix = dispatcherOptions != null ? dispatcherOptions.stripOnPrefix() : false;
		}
	}
}
