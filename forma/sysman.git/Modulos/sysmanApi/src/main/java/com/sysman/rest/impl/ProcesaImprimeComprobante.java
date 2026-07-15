package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.enums.InvocadorEnum;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.Comprobante;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.RespuestaApi;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Named;

/**
 * Procesador de solicitudes/impresión de comprobante contable.
 * 
 * @version 1.0, 14/03/2019
 * @author jgomez
 */
@Named ("procesaImprimeComprobante")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesaImprimeComprobante
                extends Procesador<Comprobante, RespuestaApi> {

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * constante para validar el nit de la entidad con la base de
     * datos
     */
    private static final String CODIGO_URL_EXISTE_COM = "72039"; 
    /**
     * Para implementar hilos con la clase <code>Runnable</code>,
     */
    /**
     * Util para traducir los mensajes de errores
     */
    private ResourceBundle idioma;
    @Override
    public void run() {
        // Sin implementar
    }

    /**
     * Acciones que se ejecutan antes de procesar una petición.
     * @throws NegocioExcepcion 
     */
    @Override
    protected void preProcesar() throws NegocioExcepcion {
        if (contexto != null) {
            
            RequestManager rq = new RequestManager();
            Map<String, Object> par = new HashMap<>();
            par.put(GeneralParameterEnum.COMPANIA.getName(),contexto.getCompania());
            par.put(GeneralParameterEnum.ANO.getName(), contexto.getAno());
            par.put(GeneralParameterEnum.TIPO.getName(), contexto.getTipo());
            par.put(GeneralParameterEnum.NUMERO.getName(), contexto.getNumero());

            Parameter parExiste;
            try {
                parExiste = rq.get(UrlServiceUtil
                                .getUrlBeanById(CODIGO_URL_EXISTE_COM)
                                .getUrl(),
                                par);
            }
            catch (SystemException e) {
                String mensaje = idioma.getString(
                                InvocadorEnum.MSG_INVOCADOR_URL_ERRADA.getValue());
                String causa = mensaje;
                LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}",
                                mensaje, causa);
                NegocioExcepcion error = new NegocioExcepcion(mensaje);
                error.initCause(new Exception(causa));
                throw error;
            }

            if (parExiste != null) {
                int cantidad = Integer.parseInt(parExiste.getFields()
                                .get(GeneralParameterEnum.CUENTA.getName())
                                .toString());
                           
                if (cantidad==0) {
                    String mensaje = "El comprobante no Existe";
                    String causa = mensaje;
                    LOG.error("Error en <<validarToken>> ->> mensaje ->> {} / causa ->> {}",
                                    mensaje, causa);
                    NegocioExcepcion error = new NegocioExcepcion(mensaje);
                    error.initCause(new Exception(causa));
                    throw error;
                };                
            }            
            esValido = true;
        }
    }

    /**
     * Acciones que se ejecutan despues de procesar una petición.
     */
    @Override
    protected void posProcesar() {
        LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                        resultado);
    }

    /**
     * Acciones que ejecuta el procesador o comando concreto.
     */
    @Override
    public void ejecutar() throws NegocioExcepcion {
        GestionAutoservicio autoservicio = new GestionAutoservicio();
        resultado = autoservicio.generarComprobanteContable(contexto);
    }

    /**
     * Obtiene el ejecutable o worker.
     */
    @Override
    public Runnable getEjecutable() {
        return this;
    }

    /**
     * Retorna resultado esperado posejecución
     */
    @Override
    public RespuestaApi getResultado() {
        return resultado;
    }

}
