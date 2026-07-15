package com.sysman.rest;

import static com.sysman.rest.EnumRole.PROCESSOR;

import com.sysman.rest.excepcion.NegocioExcepcion;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Patrón comando géneral. Procesador de Servicios para el comando
 * ejecutado Crea un grupo de subprocesos que instancia nuevos hilos
 * como sea necesario, pero volverá a utilizar los hilos previamente
 * construidos cuando están disponibles.
 *
 * @see java.util.concurrent.ExecutorService
 * @author Andrés Velásquez
 * @param <C>
 * Tipo de Contexto esperada al ejecutarse el comando
 * @param <P>
 * Tipo de Respuesta esperada al ejecutarse el comando
 */
@Ejecutor(tipo = PROCESSOR)
public abstract class Procesador<C, P> implements Serializable, Runnable {
    /**
     * Constante que representa la instancia del Log
     */
    protected static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(Procesador.class);
    /**
     * Propiedad para el objeto serializable
     */
    private static final long serialVersionUID = 1L;
    /**
     * Pripiedad que retorna el resultado de la ejecución del comando
     */
    protected P resultado;
    /**
     * Contexto de procesamiento
     */
    protected C contexto;
    /**
     * Es Ejecutado comando
     */
    protected boolean esEjecutado = false;
    /**
     * Es Valido comando
     */
    protected boolean esValido = false;
    /**
     * Objeto Worker
     */
    protected Runnable worker;
    /**
     * Este pool mejora el rendimiento de los programas que ejecutan
     * muchas tareas asíncronas de corta duración. Las llamadas a
     * ejecutar reutilizará hilos previamente construidos si está
     * disponible. Si no hay ningún tema existente está disponible, un
     * nuevo hilo se creará y se añade al pool. Los Hilos que no han
     * sido utilizados durante sesenta segundos (60 seg) se terminan y
     * se eliminan de la memoria caché. De este modo, un pool que
     * permanece inactivo durante el tiempo suficiente no consumirá
     * ningún recurso.
     */
    @SuppressWarnings("unused")
    private final ExecutorService ejecutor = Executors.newCachedThreadPool();

       
    /**
     * Preprocesa el contexto y propiedades requeridas en la ejecución
     * del procesador previamente a la ejecución.
     * @throws NegocioExcepcion 
     *
     */
    protected abstract void preProcesar() throws NegocioExcepcion;
    
    /**
     * Pos procesa el contexto y propiedades requeridas en la
     * ejecución del procesador posterior a la ejecución.
     * 
     * @return Tipo de objeto requerido en el pos proceso
     *
     */
    protected abstract void posProcesar();

    /**
     * Pos procesa el contexto y propiedades requeridas en la
     * ejecución del procesador posterior a la ejecución.
     * 
     * @return Tipo de objeto requerido en el pos proceso
     * @throws com.sysman.rest.excepcion.NegocioExcepcion
     *
     */
    protected abstract void ejecutar() throws NegocioExcepcion;

    /**
     * Metodo ejecutor del Comando
     *
     * @throws NegocioExcepcion
     * Excepción de Negocio
     */
    public void procesar() throws NegocioExcepcion {
        LOG.info("Entro a: <<ejecutar>>");
        preProcesar();
        try {
            if (esValido) {
                ejecutar();
                esEjecutado = true;
                posProcesar();
            }
        } catch (Exception e) {
            throw new NegocioExcepcion(e.getMessage());
        }
        LOG.info("Finalizando metodo <<ejecutar>> .... ");
    }

    /**
     * Retorna si el comando es ejecutado
     *
     * @return es Ejecutado
     */
    public Boolean esComandoEjecutado() {
        return esEjecutado;
    }

    /**
     * Ejecutable o Worker
     *
     * @return Objeto de tipo Runnable
     */
    public abstract Runnable getEjecutable();

    /**
     * Retorna resultado esperado posejecución
     * 
     * @return Objeto de tipo P
     */
    public abstract P getResultado();

    /**
     * Modifca el contexto
     *
     * @param contexto
     * Contexto
     */
    public void setContexto(C contexto) {
        this.contexto = contexto;
    }

}