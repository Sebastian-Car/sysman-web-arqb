package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Named;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.enums.PqrProcesadorUrlEnum;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ConsultaPqr;
import com.sysman.rest.logica.Pqr;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.ejb.impl.EjbWorkflowCero;

/**
 * Procesador que permite manejar PQR en modelo Worflow
 *
 * @version 1.0, 13/12/2019
 * @author mochoa
 */
@Named ("ProcesaWorflowPqr")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesaWorflowPqr extends Procesador<Pqr, RespuestaApi>
{
	@EJB
	private static EjbWorkflowCeroRemote wor=new EjbWorkflowCero() ;

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
   /**
    * Constante para evaluar el tipo tramite PQR
    */
    public static final String TIPO_TRAMITE="1";
    /**
     * Constante para evaluar el tipo tramite PQR
     */
     public static final String PROCESO_PQR="00000";
     /**
      * Constante para quemar el valor de la etapa 1 de pqr
      */
     public static final String NODO_ORIGEN="0000";
     /**
      *  Constante para quemar el valor de la etapa 1 de pqr
      */
     public static final String NODO_DESTINO="0000";
     /**
      * Constante para quemar usuario de workflow
      */
     public static final String USUARIO="USUARIO_WORKFLOW";
     
     private Registro rsPqr;
    

    /**
     * Abstraccion creada para emitir respuesta del procesador
     */
    RespuestaApi respuestaAPi = new RespuestaApi();
    
    protected RequestManager requestManager;

     
    /**
     * LOG Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProcesaWorflowPqr.class);

    /**
     * Lista los errores que se controlan del json
     */
    List<String> errores = new ArrayList<>();
    

    /**
     * Implementacion del EJB de workflow para llamar al procesoatramitar con el fin de PQR
     */
    @EJB
    private static EjbWorkflowCero worflowPqr = new EjbWorkflowCero();
    
    private ConsultaPqr respuestaPqr=new ConsultaPqr();
    
    
    /**
     * Para implementar hilos con la clase <code>Runnable</code>,
     */
    @Override
    public void run()
    {
        // Sin implementar
    }

    /**
     * Acciones que se ejecutan antes de procesar una petición.
     * @throws NegocioExcepcion 
     */
    @Override
    protected void preProcesar() throws NegocioExcepcion
    {
    	if(contexto.getTipoConsumo().equals("1")) {
    	

                if(contexto.getTipoTramite()!=TIPO_TRAMITE) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                                    .append("No pude hacer un tipo tramite diferente a PQR por este medio")                                    
                                    .toString());
                } if(contexto.getCedula().isEmpty()&&contexto.getCorreo().isEmpty()&&contexto.getDireccion().isEmpty()
                		&&contexto.getCompania().isEmpty()&&contexto.getDescripcion().isEmpty()&&contexto.getNombre().isEmpty()) {
                    errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                            .append("No ha completado los datos minimos para registrar una PQR")                                    
                            .toString());
                    
            }
    	}else {
    		if(contexto.getNumRadicado().isEmpty()) {
    			errores.add(new StringBuilder().append((errores.isEmpty() ? "" : "\n\r"))
                        .append("No puede consultar una PQR si no tiene un numero de radicado")                                    
                        .toString());
    		}
    	}
            if (errores.isEmpty() || errores == null) {
                
                esValido = true;
            } else {
                esValido=false;
                respuestaAPi.setCuerpo(errores);
                respuestaAPi.setCodigo(10006);                
               throw new NegocioExcepcion(errores.toString());
            }
        
    }

    /**
     * Acciones que se ejecutan despues de procesar una petición.
     */
    @Override
    protected void posProcesar()
    {
        LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                        resultado);
    }

    /**
     * Acciones que ejecuta el procesador o comando concreto.
     */
    @Override
    public void ejecutar() throws NegocioExcepcion
    {
    	BigInteger numeroRad;
        try {
        	BigInteger bigde=new BigInteger(contexto.getNumRadicado());
        	BigInteger bigIN=new BigInteger(TIPO_TRAMITE);
        	if(contexto.getTipoConsumo().equals("1")) {
        	numeroRad= worflowPqr.workflow_pqrs(contexto.getCompania(),
        			PROCESO_PQR,
        			TIPO_TRAMITE,
        			bigde,
        			bigIN,
        			NODO_ORIGEN,
        			NODO_DESTINO,
        			USUARIO,
        			true,
        			contexto.getNombre(),
        			contexto.getDireccion(),
        			contexto.getCorreo(),
        			contexto.getAnexo1(),
        			contexto.getAnexo2(),
        			contexto.getAnexo3(),
        			contexto.getDescripcion(), 
        			contexto.getTipoTramite());
            respuestaAPi.setCuerpo(numeroRad);
        	}else {
        		
        		 Map<String, Object> param = new HashMap<>();
                 param.put(GeneralParameterEnum.COMPANIA.getName(),
                                 contexto.getCompania());
                 param.put(GeneralParameterEnum.PROCESO.getName(),
                                 PROCESO_PQR);
                 param.put(GeneralParameterEnum.NUMERO.getName(),
                                 contexto.getNumRadicado());
                 param.put(GeneralParameterEnum.TRAMITE.getName(),
                                 TIPO_TRAMITE);
        		rsPqr = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil
                                        .getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		PqrProcesadorUrlEnum.URL1045
                                                                        .getValue())
                                        .getUrl(),
                                        param));
        		respuestaPqr.setEtapa(rsPqr.getCampos().get("ETAPA").toString());
        		respuestaPqr.setNumRadicado((String)rsPqr.getCampos().get("NUMERO").toString());
        		respuestaPqr.setDependencia((String)rsPqr.getCampos().get("DEPENDENCIA").toString());
        		
        		respuestaAPi.setCuerpo(respuestaPqr);
        		
        	}
            resultado = respuestaAPi;
            
        } catch (SystemException e) {
            String rta = e.getMessage();
            if (rta.contains("@#INI#@")) {
                    rta = rta.substring(e.getMessage().indexOf("@#INI#@"));
                    rta = rta.substring(rta.indexOf("Log:"), rta.indexOf("@#FIN#@"));
            }
            throw new NegocioExcepcion(rta);
           
        }		
    }

    /**
     * Obtiene el ejecutable o worker.
     */
    @Override
    public Runnable getEjecutable()
    {
        return this;
    }

    /**
     * Retorna resultado esperado posejecución
     */
    @Override
    public RespuestaApi getResultado()
    {
        return resultado;
    }

    
}
