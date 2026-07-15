/*
* Refactoring
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.templates.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase de la annotation usada en los proyectos para indicar si  el código puede ser procesado 
 * en la refactorizaación usando los patrones de busqueda respectivos.
 */  
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention( RetentionPolicy.CLASS)
public @interface Refactoring {
	String pattern() default "";
	String value() default "";
} 
