package com.sysman.rest;

import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.impl.ProcesaWorflowPqr;
import com.sysman.rest.logica.Pqr;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.rest.RespuestaApi;

/**
 * Invocador encargado de realizar los procesos para registrar y consultar PQR
 * @author Maria fernanda Ochoa Paipilla
 *
 */


@Path("/pqr")
@Ejecutor(tipo = EnumRole.INVOKER)
public class InvockerPqr {
    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(InvockerPqr.class);
    

    /**
     * Procesador de actualizaciones de datos personales.
     */
    @Inject
    private ProcesaWorflowPqr procesador;
    /**
     * 
     */
    @Inject
    private Pqr paramPqr;
    
  
    /**
     * Util para traducir los mensajes de errores
     */
    private ResourceBundle idioma;

    @GET
    @Path("/prueba")
    @Produces("text/plain")
    public Response doGet() {
        return Response.ok("method doGet invoked").build();
    }

    /**
     * Ejecuta la petici&oacute;n POST para crear una consulta o
     * solicitud de autoservicio.
     *
     * @param token
     * Credenciales de autorizaci&oacute;n. Token asociado con a la
     * consulta o solicitud de autoservicio.
     * @param contexto
     * Parametros especificados en el cuerpo de la petici&oacute;n.
     * @return respuesta generada al llevar a cabo la petici&oacute;n
     * POST.
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response crearSolicitudAutoservicio(Pqr contexto) {
        idioma = ResourceBundle
                        .getBundle(SysmanConstantes.RUTA_IDIOMA);
       

        
        RespuestaApi respuestaApi = new RespuestaApi();
        try {
                procesador.setContexto(contexto);
                procesador.procesar();
                respuestaApi.setCuerpo(procesador.getResultado());
        }
        catch (NegocioExcepcion e) {
            LOG.error("Error en <<doPost>> ->> mensaje ->> {} / causa ->> {}",
                            e.getMessage(),
                            e.getCause());
            respuestaApi.setCodigo(5);
            respuestaApi.setMensaje(e.getMessage());
            return Response.ok().entity(respuestaApi).build();
        }
        return Response.ok().entity(respuestaApi).build();
    }

   

//    @GET
//    @Path("/validapagoidsn")
//    @Produces("application/json")
//    public Response consultarPqr(
//        @QueryParam("numRadicado") long numRadicado,@QueryParam("cedula") String cedula
//       ) {
//        RespuestaApi respuestaApi = new RespuestaApi();
//
//        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
//       try {            
//    	   
//          }
//                    
//        catch (NegocioExcepcion e) {
//            respuestaApi.setCodigo(5);
//            respuestaApi.setMensaje(e.getMessage());
//            return Response.ok().entity(respuestaApi).build();
//
//        }
//
//        return Response.ok().entity(respuestaApi).build();
//
//    }

  
}