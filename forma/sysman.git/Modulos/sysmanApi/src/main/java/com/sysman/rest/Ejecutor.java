package com.sysman.rest;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para comando Ejecutor
 *
 * @author Andrés Velásquez
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, TYPE })
public @interface Ejecutor {

    /**
     * tipo de roles para el patrón
     * 
     * @return Roles
     */
    EnumRole[] tipo();
}
