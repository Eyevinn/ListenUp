package se.eyevinn.listenup.processor.listenertypes;

import com.mattiasselin.linewriter.ILineWriter;
import com.mattiasselin.linewriter.LineWriter;

import se.eyevinn.listenup.processor.model.EventModel;
import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.ReferenceFactory;

/**
 * 
 * @author Mattias Selin
 *
 */
public class BaseGenerator implements IClassGenerator {

	@Override
	public void generateClass(ReferenceFactory rf, IImportContext importContext, String className,
			ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef) {
		body.println("public abstract class "+className+" implements "+importContext.getName(listenerRef)+" {");
		body.indent();
		for(EventModel eventModel : listenerModel.getEvents()) {
			LineWriter methodBody = new LineWriter();
			methodBody.println("// Unhandled");
			eventModel.override(importContext, methodBody).writeTo(body);
		}
		body.unindent();
		body.println("}");
	}

}
