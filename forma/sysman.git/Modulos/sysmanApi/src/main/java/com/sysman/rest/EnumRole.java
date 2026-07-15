package com.sysman.rest;

/**
 * Enumerado para los roles del patrón comando
 *
 * @author Andrés Velásquez
 */
public enum EnumRole {
    /**
     * Procesador de ejecuciones
     */
    PROCESSOR,
    /**
     * Componente procesador de ejecuciones
     */
    COMPONENTE_PROCESSOR,
    /**
     * Sucesor procesador concreto de ejecuciones
     */
    COMPONENTE_PROCESSOR_SUCCESSOR,
    /**
     * Cliente
     */
    CLIENT,
    /**
     * Compuesto de Procesadores
     */
    COMPOSITE_PROCESSOR,
    /**
     * Invocador del Procesador
     */
    INVOKER,
    /**
     * Recibidor de resultado de la ejecución
     */
    RECEIVER
}