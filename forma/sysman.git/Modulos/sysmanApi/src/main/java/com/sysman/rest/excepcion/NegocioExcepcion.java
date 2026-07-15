/**
 * 
 */
package com.sysman.rest.excepcion;

/**
 * Manejo de escpciones de negocio
 * 
 * @author Andrés Velásquez
 *
 */
public class NegocioExcepcion extends Exception{

	/**
	 * Propiedad para objeto serializable serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NegocioExcepcion.class);

	
	/**
	 * Constructor por parámetro
	 * @param message Message
	 * @param cause Cause
	 * @param enableSuppression Enable Suppression
	 * @param writableStackTrace Writable Stack Trace
	 */
	public NegocioExcepcion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		LOG.error("Error en <<ejecutar>> ->> mensaje ->> {} / causa ->> {} ", cause.getMessage(), cause.getCause());
	}

	/**
	  * Constructor por parámetro
	 * @param message Message
	 * @param cause Cause
	 */
	public NegocioExcepcion(String message, Throwable cause) {
		super(message, cause);
		LOG.error("Error en <<ejecutar>> ->> mensaje ->> {} / causa ->> {} ", cause.getMessage(), cause.getCause());
	}

	/**
	 * Constructor por parámetro
	 * @param message Message
	 * @param cause Cause
	 */
	public NegocioExcepcion(String message) {
		super(message);
		LOG.error("Error en <<ejecutar>> ->> mensaje ->> {} ", message);
	}

	/**
	 * Constructor por parámetro
	 * @param cause Cause
	 */
	public NegocioExcepcion(Throwable cause) {
		super(cause);
		LOG.error("Error en <<ejecutar>> ->> mensaje ->> {} / causa ->> {} ", cause.getMessage(), cause.getCause());
	}

	
}
