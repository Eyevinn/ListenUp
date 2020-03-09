package se.eyevinn.listenup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Mattias Selin
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Listener {
	 ListenerType[] generate() default {ListenerType.ALL};
	 ListenerType[] dontGenerate() default {ListenerType.NONE};
}
