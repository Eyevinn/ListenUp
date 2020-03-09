package se.eyevinn.listenup.annotation.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Mattias Selin
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface LoggerOptions {
	String logMethodName() default "_internal_log";
	String toStringMethodName() default "_internal_toString";
}
