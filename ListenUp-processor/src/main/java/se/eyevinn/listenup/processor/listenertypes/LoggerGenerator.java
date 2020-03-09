package se.eyevinn.listenup.processor.listenertypes;

import com.mattiasselin.linewriter.ILineSource;
import com.mattiasselin.linewriter.ILineWriter;

import se.eyevinn.listenup.annotation.options.LoggerOptions;
import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.model.ParameterModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.LocalVariableNamePicker;
import se.eyevinn.listenup.processor.util.ReferenceFactory;

/**
 * 
 * @author Mattias Selin
 *
 */
public class LoggerGenerator implements IClassGenerator {
	private final Options options;

	public LoggerGenerator(LoggerOptions loggerOptions) {
		this.options  = new Options(loggerOptions);
	}

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		ITypeReference stringType = rf.c(String.class);
		
		body.println("public class "+className+" implements "+importContext.getName(listenerRef)+" {");
			body.indent();
			body.println();
			body.println("/**");
			body.println(" * Override this to handle conversation to String.");
			body.println(" */");
			body.println("protected "+importContext.getName(stringType)+" "+options.toStringMethodName+"("+importContext.getName(rf.c(Object.class))+" object) {");
				body.indent();
				body.println("return "+importContext.getName(stringType)+".valueOf(object);");
				body.unindent();
			body.println("}");
			body.println();
			body.println("/**");
			body.println(" * Override this to handle logging.");
			body.println(" */");
			body.println("protected void "+options.logMethodName+"("+importContext.getName(stringType)+" message) {");
				body.indent();
				body.println(importContext.getName(rf.c(System.class))+".out.println(message);");
				body.unindent();
			body.println("}");
			
			for(EventModel eventModel : listenerModel.getEvents()) {
				body.println();
				eventModel.override(importContext, new ILineSource() {
					public void writeTo(ILineWriter methodBody) {
						LocalVariableNamePicker namePicker = new LocalVariableNamePicker();
						eventModel.registerParameterNames(namePicker);
						
						String messageLocalVariable = namePicker.getSafeName("message", true);
						
						methodBody.println(importContext.getName(rf.c(StringBuilder.class))+" "+messageLocalVariable+" = new "+importContext.getName(rf.c(StringBuilder.class))+"(\""+eventModel.name+"(\");");
						boolean first = true;
						for(ParameterModel parameterModel : eventModel.parameters) {
							if(first) {
								first = false;
							} else {
								methodBody.println(messageLocalVariable+".append(\", \");");										
							}
							methodBody.println(messageLocalVariable+".append("+options.toStringMethodName+"("+parameterModel.name+"));");									
						}
						methodBody.println(messageLocalVariable+".append(\")\");");
						methodBody.println(options.logMethodName+"("+messageLocalVariable+".toString());");
					}
				}).writeTo(body);
			}
			body.unindent();
		body.println("}");
	}

	private static class Options {
		private final String logMethodName;
		private final String toStringMethodName;
		
		public Options(LoggerOptions loggerOptions) {
			this.logMethodName = loggerOptions != null ? loggerOptions.logMethodName() : "_internal_log";
			this.toStringMethodName = loggerOptions != null ? loggerOptions.toStringMethodName() : "_internal_toString";
		}
	}
}
