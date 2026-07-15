package com.sysman.util.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.ws.BindingProvider;

import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.enums.APIAutoServicioEnum;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosFacturasRequest;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosFacturasResponse;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosFacturasWS;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosFacturasWSService;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosNominasRequest;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosNominasResponse;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosNominasWS;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosNominasWSService;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.EntregaFacturaRequest;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.FacturaResult;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.IdentificadorFactura;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.IdentificadorNomina;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosFactura;

/**
 * Cliente SOAP para la integración con el proveedor de facturación y nomina electrónica Invoway.
 * Centraliza las operaciones de envío y consulta de documentos electrónicos
 * (facturas, documentos soporte, notas de ajuste y nóminas) a través del Web Service
 * de Invoway.
 *
 * <p>Las credenciales de autenticación (usuario y contraseña) se inyectan por cada
 * petición desde los parámetros de configuración de la compañía en BD.</p>
 */

public class ApiInvoway {
	protected ResourceBundle idioma;

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(APIFrida.class);

	public ApiInvoway() {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}
	
    /**
     * Envía un documento electrónico (factura, documento soporte, nota de ajuste, nota debito o crédito) al Web Service
     * de Invoway mediante el método {@code entregaFactura}.
     *
     * @param url      URL del Web Service SOAP de Invoway, obtenida del parámetro
     *                 {@code "URL SERVICIO SOAP"} de la compañía.
     * @param xml      Objeto {@link Documento} construido por {@link InvowayDocumentoBuilder},
     *                 que representa el documento a enviar.
     * @param pass     Contraseña de autenticación del Web Service, obtenida del parámetro
     *                 {@code CLAVE_FACT_ELECTRONICA_EXTERNA}.
     * @param user     Usuario de autenticación del Web Service, obtenido del parámetro
     *                 {@code USUARIO_FACT_ELECTRONICA_EXTERNA}.
     * @return         Mensaje de respuesta retornado por Invoway. En caso de error de
     *                 conexión, retorna el mensaje configurado en el archivo de idioma.
     */
	public String postEnvioFactura(String url, Documento xml, String pass, String user) throws MalformedURLException, IOException, SysmanException {
		String msg = "";
		
		try {
			// Crear el servicio y el port
			URL urlFin = new URL(url);
            ConsultaEstadosFacturasWS servicio = new ConsultaEstadosFacturasWS(urlFin);
            EntregaFacturaRequest factura = new EntregaFacturaRequest();
            factura.getDocumento().add(xml);
            ConsultaEstadosFacturasWSService port = servicio.getConsultaEstadosFacturasWSPort();
            
            // Configurar las credenciales de autenticación
            Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
            requestContext.put(BindingProvider.USERNAME_PROPERTY, user); //"sysman-int"
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, pass); //"XoXwEL90P9y7"
            
            // se envia la factura
            FacturaResult respuesta = port.entregaFactura(factura);
                        
        	return respuesta.getMensajeRespuesta();
        } catch (Exception e) {
        	msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
        	return msg;
        }

	}
	
	/**
     * Consulta el estado de un documento electrónico previamente enviado a Invoway.
     * Retorna información como el UUID (CUDE) y el estado de procesamiento ante la DIAN.
     *
     * @param url            URL del Web Service SOAP de Invoway.
     * @param numeroFactura  Prefijo y Consecutivo DIAN del documento a consultar. ejemplo(NC3001 nota crédito)
     * @param anio           Año del documento.
     * @param tipoDoc        Tipo de documento: {@code "DE"} para documento soporte,
     *                       {@code "FA"} para factura electrónica, {@code "NA"} para nota de ajuste.
     * @param nitEntidad     NIT de la compañía emisora (sin dígito de verificación).
     * @param pass           Contraseña de autenticación del Web Service.
     * @param user           Usuario de autenticación del Web Service.
     * @return               Lista de {@link InfoEstadosFactura} con el estado del documento.
     *                       Retorna {@code null} si ocurre un error en la comunicación.
     */
	public List<InfoEstadosFactura> consultarFactura(String url, String numeroFactura, String anio, String tipoDoc, String nitEntidad, String pass, String user) throws MalformedURLException, IOException, SysmanException {
			
		try {
			// Crear el servicio y el port
			URL urlFin = new URL(url);
            ConsultaEstadosFacturasWS servicio = new ConsultaEstadosFacturasWS(urlFin);
            
            ConsultaEstadosFacturasRequest peticion = new ConsultaEstadosFacturasRequest();
            IdentificadorFactura factConsulta = new IdentificadorFactura();
            factConsulta.setIdFiscalEmisor(nitEntidad);
            factConsulta.setAnyo(anio);
            factConsulta.setNumeroDocumento(numeroFactura);
            factConsulta.setTipoDocumento(tipoDoc);
            peticion.getIdentificadoresFacturas().add(factConsulta);
            
            ConsultaEstadosFacturasWSService port = servicio.getConsultaEstadosFacturasWSPort();
            
            // Configurar las credenciales de autenticación
            Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
            requestContext.put(BindingProvider.USERNAME_PROPERTY, user); //"sysman-int"
            requestContext.put(BindingProvider.PASSWORD_PROPERTY, pass); //"XoXwEL90P9y7"
            
            // se envia la factura
            ConsultaEstadosFacturasResponse respuesta = port.getEstadosFacturas(peticion);
                        
        	return respuesta.getInfoEstadosFacturas();
        } catch (Exception e) {
        	return null;
        }

	}
	
	/**
	 * Consulta el estado de una nómina electrónica en el servicio web de Invoway
	 * utilizando los criterios de búsqueda suministrados.
	 * <p>
	 * El método construye una petición SOAP con la información de identificación
	 * de la nómina y realiza la autenticación contra el servicio web mediante
	 * usuario y contraseña.
	 * </p>
	 *
	 * <p>
	 * Los tipos de nómina soportados son:
	 * <ul>
	 *     <li>{@code N}: Nómina electrónica base.</li>
	 *     <li>{@code R}: Nómina de ajuste de reemplazo.</li>
	 *     <li>{@code E}: Nómina de ajuste de eliminación.</li>
	 * </ul>
	 * </p>
	 *
	 * @param url URL del servicio web de consulta de estados de nómina.
	 * @param anyo año de la nómina electrónica a consultar.
	 * @param mes mes de la nómina electrónica a consultar.
	 * @param numero número consecutivo de la nómina electrónica.
	 * @param nitEmisor NIT del emisor de la nómina electrónica.
	 * @param nitTrabajador NIT o identificación del trabajador asociado a la nómina.
	 *                       Actualmente no se envía en la petición.
	 * @param tipoNomina tipo de nómina electrónica a consultar.
	 * @param trackId identificador único de seguimiento de la nómina.
	 *                Actualmente no se envía en la petición.
	 * @param pass contraseña de autenticación del servicio web.
	 * @param user usuario de autenticación del servicio web.
	 *
	 * @return objeto {@link ConsultaEstadosNominasResponse} con la información
	 *         retornada por el servicio web, o {@code null} si ocurre un error
	 *         durante la ejecución de la consulta.
	 */
	public ConsultaEstadosNominasResponse consultarNomina(
	        String url,
	        String anyo,
	        String mes,
	        String numero,
	        String nitEmisor,
	        String nitTrabajador,
	        String tipoNomina,
	        String trackId,
	        String pass,
	        String user) {
 
	    try {
 
	        URL urlFin = new URL(url);
	        ConsultaEstadosNominasWS servicio = new ConsultaEstadosNominasWS(urlFin);
 
	        ConsultaEstadosNominasRequest peticion = new ConsultaEstadosNominasRequest();
 
	        IdentificadorNomina identificador = new IdentificadorNomina();
	        identificador.setAnyo(anyo);
	        identificador.setMes(mes);
	        identificador.setNumero(numero);
	        identificador.setIdFiscalEmisor(nitEmisor);
//	        identificador.setIdFiscalTrabajador(nitTrabajador);
	        if(tipoNomina!=null) {
	        	identificador.setTipoNomina(tipoNomina);
	        }
//	        identificador.setTrackID(trackId);
 
	        peticion.getIdentificadoresNominas().add(identificador);
 
	        ConsultaEstadosNominasWSService port =
	                servicio.getConsultaEstadosNominasWSPort();
 
	        Map<String, Object> requestContext =
	                ((BindingProvider) port).getRequestContext();
 
	        requestContext.put(BindingProvider.USERNAME_PROPERTY, user);
	        requestContext.put(BindingProvider.PASSWORD_PROPERTY, pass);
 
	        ConsultaEstadosNominasResponse respuesta =
	                port.getEstadosNominas(peticion);
 
	        return respuesta;
 
	    } catch (Exception e) {
			LOG.error(e.toString());
			e.printStackTrace();
        	return null;
		}
	}
	
}