package de.jaschastarke.maven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers this Class to archiv all its doccomments into the META-INF/doccomments.properties for runtime-access
 */
@Retention(RetentionPolicy.SOURCE) 
@Target(ElementType.TYPE)
public @interface ArchiveDocComments {
}
