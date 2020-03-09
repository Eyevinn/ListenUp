package se.eyevinn.listenup.processor.listenertypes;

import java.util.ArrayList;
import java.util.List;

import com.mattiasselin.linewriter.ILineSource;
import com.mattiasselin.linewriter.ILineWriter;

import se.eyevinn.listenup.event.IEvent;
import se.eyevinn.listenup.annotation.options.BufferOptions;
import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.model.ParameterModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.ReferenceFactory;
import se.eyevinn.listenup.processor.util.StringUtil;
import se.eyevinn.listenup.processor.util.StringUtil.IStringJoiner;

/**
 * 
 * @author Mattias Selin
 *
 */
public class BufferGenerator implements IClassGenerator {
	private final Options options;
	private final ITypeReference eventFactory;
	
	public BufferGenerator(BufferOptions bufferOptions, ITypeReference eventFactory) {
		this.options = new Options(bufferOptions);
		this.eventFactory = eventFactory;
	}

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		ITypeReference typedEvent = rf.g(IEvent.class, listenerRef);
		ITypeReference eventList = rf.g(List.class, typedEvent);
		
		body.println("public class "+className+" implements "+importContext.getName(listenerRef)+" {");
			body.indent();
			body.println("private final "+importContext.getName(eventList)+" buffer = new "+importContext.getName(rf.g(ArrayList.class))+"();");
			body.println();
			body.println("public void "+options.releaseBufferMethodName+"("+importContext.getName(listenerRef)+" target) {");
				body.indent();
				body.println(importContext.getName(eventList)+" harvested;");
				body.println("synchronized(buffer) {");
					body.indent();
					body.println("harvested = new "+importContext.getName(rf.g(ArrayList.class))+"(buffer);");
					body.println("buffer.clear();");
					body.unindent();
				body.println("}");
				body.println("for("+importContext.getName(typedEvent)+" event : harvested) {");
					body.indent();
					body.println("event.send(target);");
					body.unindent();
				body.println("}");
				body.unindent();
			body.println("}");
			body.println();
			for(EventModel eventModel : listenerModel.getEvents()) {
				eventModel.override(importContext, new ILineSource() {
					@Override
					public void writeTo(ILineWriter methodBody) {
						IStringJoiner arguments = StringUtil.joiner(", ");
						for(ParameterModel parameterModel : eventModel.parameters) {
							arguments.add(parameterModel.name);
						}
						methodBody.println("synchronized(this.buffer) {");
							methodBody.indent();
							methodBody.println("this.buffer.add("+importContext.getName(eventFactory)+"."+eventModel.name+"("+arguments+"));");
							methodBody.unindent();
						methodBody.println("}");
					}
				}).writeTo(body);
			}
			body.unindent();
		body.println("}");
	}
	
	private static class Options {
		private final String releaseBufferMethodName;

		public Options(BufferOptions bufferOptions) {
			this.releaseBufferMethodName = bufferOptions != null ? bufferOptions.releaseBufferMethodName() : "releaseBuffer";
		}
	}
}
