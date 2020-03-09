package se.eyevinn.listenup.processor.listenertypes;

import com.mattiasselin.linewriter.ILineWriter;

import se.eyevinn.listenup.processor.model.ListenerModel;
import se.eyevinn.listenup.processor.util.IImportContext;
import se.eyevinn.listenup.processor.util.ITypeReference;
import se.eyevinn.listenup.processor.util.ReferenceFactory;

/**
 * 
 * @author Mattias Selin
 *
 */
public interface IClassGenerator {
	void generateClass(ReferenceFactory rf, IImportContext importContext, String className, ListenerModel listenerModel, ILineWriter body, ITypeReference listenerRef);
}
