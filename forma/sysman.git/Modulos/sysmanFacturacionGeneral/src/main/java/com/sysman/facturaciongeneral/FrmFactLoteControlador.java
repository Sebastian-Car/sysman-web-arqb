/*-
 * FrmFactLoteControlador.java
 *
 * 1.0
 *
 * 23/12/2020
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmFactLoteControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmFactLoteControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmRangoProduccionDianUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParamItem;
import com.sysman.util.rest.ParamItems;
import com.sysman.util.rest.ParametroCuerpoEnvioFactura;
import com.sysman.util.rest.ParametroDeleteEnvioFactura;
import com.sysman.util.rest.ParametrosCargos;
import com.sysman.util.rest.ParametrosDescuentos;
import com.sysman.util.rest.ParametrosEnvioFactura;
import com.sysman.util.rest.ParametrosEnvioFacturaFiltros;
import com.sysman.util.rest.ParametrosImpuestos;
import com.sysman.util.rest.ParametrosItems;
import com.sysman.util.rest.ParametrosItemsImpuestos;
import com.sysman.util.rest.ParametrosTercero;
import com.sysman.util.rest.ParametrosTerceroLote;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaNotasReporte;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosFactura;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Proveedor;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Cliente;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago.CondicionPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DatosTotales;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DocumentosReferenciados;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos.Impuesto;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas.Linea;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones.Retencion;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.TotalesCop;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DocumentosReferenciados.DocumentoReferenciado;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
import sysman.util.consumo.enums.EnumParametros;



/**
 * Formulario que envia las facturas a FRIDA
 *
 * @version 1.0, 23/12/2020
 * @author eamaya
 *
 * @version 1.1, 24/08/2021
 * @author gfigueredo Se aï¿½ade un dia a la fecha final, para asegurar que en la base de datos se busque por el rango de fecha adecuadas.
 * @see #oprimirEnviarFacturas()
 * @see #oprimirEnviarNotas()
 *
 * @version 1.2, 06/10/2021
 * @author gfigueredo Se consulta información de total, subtotal e iva.
 * @see #exportarNotas(String, String, String, String, String, String, String, String, String)
 *
 */
@ManagedBean
@ViewScoped
public class FrmFactLoteControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String nitCompania;

    private String usuario;

    private final String tipoCobro;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena la fecha de inicio
     */
    private Date fechaInicio;
    /**
     * Variable que almacena la fecha fin
     */
    private Date fechaFin;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();
    private double totalFactura = 0;

    /**
     * Crea una nueva instancia de FrmFactLoteControlador
     */
    public FrmFactLoteControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        usuario = SessionUtil.getUser().getCodigo();
        fechaInicio = new Date();
        fechaFin = new Date();

        tipoCobro = SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue()).toString();
        try
        {
            // 2227
            numFormulario = GeneralCodigoFormaEnum.FRM_FACTLOTE_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        if (nitCompania.contains("-"))
        {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton EnviarFacturas en la vista
     *
     *
     */
    public void oprimirEnviarFacturas()
    {
        archivoDescarga = null;
        String url;
        String log;
        log = "|---------------     ENVIO FACTURAS ---------------|";
        // Variable para el 
        String facturadorExterno = "";
        try
        {

            url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

            /*
             * Tar 1000108430 Se aï¿½ade un dia a la fecha final, debido a que al realizar la consulta en la bd esta toma la fecha desde las 0 horas, por lo cual al enviarse la misma fecha no trae
             * registros. Ej: '01/01/2021' - '01/01/2021', no trae registros debido a que la busqueda se realiza desde las 0 horas 0 minutos y 0 segundos ('01/01/2021 00:00:00' - '01/01/2021
             * 00:00:00') mientras que al sumar un dia, se va a realizar la busqueda de la siguiente manera ('01/01/2021 00:00:00' - '02/01/2021 00:00:00')
             */

            Calendar c = Calendar.getInstance();
            c.setTime(fechaFin);
            c.add(Calendar.DATE, 1);
            fechaFin = c.getTime();

            Map<String, Object> param = new TreeMap<>();
            
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/YYYY");
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("FECHAINICIAL", formatoFecha.format(fechaInicio));
            param.put("FECHAFINAL", formatoFecha.format(fechaFin));
            param.put("TIPOCOBRO", tipoCobro);

            
            // se consulta el parametro que define si se usa Facturador externo o FRIDA
            try {
            	facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
            			FrmFactLoteControladorEnum.MANEJA_FACTURACION_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"NO");
            }catch (Exception e) {
            	facturadorExterno = "NO";
    		}
            if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) { 
            	List<Registro> listaFactSysman = RegistroConverter
                        .toListRegistro(
                                        requestManager
                                                        .getList(
                                                                        UrlServiceUtil.getInstance()
                                                                                        .getUrlServiceByUrlByEnumID(
                                                                                                        FrmFactLoteControladorUrlEnum.URL3578
                                                                                                                        .getValue())
                                                                                        .getUrl(), param));
	            if (!listaFactSysman.isEmpty())
	            {
	
	                for (Registro reg : listaFactSysman)
	                {
	
	                    String respuesta;
	                    APIFrida apiFrida = new APIFrida();
	
	                    respuesta = apiFrida.cargarTercero(nitCompania, reg.getCampos().get("NUMTERCERO").toString(), url);
	
	                    Gson gson = new Gson();
	                    RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);
	
	                    if (respuestaApi.getCodigo() != 0)
	                    {
	
	                        log = log + "\n" + crearTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
	                    }
	                }
	
	                for (Registro reg : listaFactSysman)
	                {
	                    if (reg.getCampos().get("CODIGOPRODUCTO") != null)
	                    {
	                        validarProducto(url, reg.getCampos().get("CODIGOPRODUCTO").toString());
	
	                    }
	
	                    String prefijo = SysmanFunciones.nvl(reg.getCampos().get("PREFIJO"), "").toString();
	
	                    String numeroFactura = SysmanFunciones.nvl(reg.getCampos().get("NUMEROFACTURA"), "").toString();
	
	                    boolean facExiste = false;
	
	                    String tipoFormato;
	                    if ("NC".equals(prefijo))
	                    {
	                        tipoFormato = "02";
	                    }
	                    else
	                    {
	                        tipoFormato = "ND".equals(prefijo) ? "03" : "01";
	                    }
	
	                    String respuesta;
	                    APIFrida api = new APIFrida();
	
	                    respuesta = api.cargarEnvioFacatura(nitCompania, url);
	
	                    Gson gson = new Gson();
	                    RespuestaEnvioFactura respuestaApi = gson.fromJson(respuesta, RespuestaEnvioFactura.class);
	
	                    for (int i = 0; i < respuestaApi.getCuerpo().size(); i++)
	                    {
	                        facExiste = false;
	                        List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);
	
	                        if (numeroFactura.equals(
	                                        new DecimalFormat("#.####################################").format(datos.get(0)))
	                            && prefijo.equals(datos.get(1)))
	                        {
	                            facExiste = true;
	                        }
	
	                        // Borrar Factura
	                        if (facExiste)
	                        {
	                            Registro rs = RegistroConverter
	                                            .toRegistro(requestManager.get(
	                                                            UrlServiceUtil.getInstance()
	                                                                            .getUrlServiceByUrlByEnumID(
	                                                                                            FrmRangoProduccionDianUrlEnum.URL9457
	                                                                                                            .getValue())
	                                                                            .getUrl(),
	                                                            null));
	
	                            if (rs != null)
	                            {
	
	                                File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());
	
	                                String nombreCertificado = archivo.getName();
	
	                                byte[] archivoBytes = Files.readAllBytes(archivo.toPath());
	
	                                String certificado = Base64.getEncoder().encodeToString(archivoBytes);
	
	                                String passCertificado = Base64.getEncoder()
	                                                .encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());
	
	                                ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();
	
	                                paramDelete.setTipoFormato(tipoFormato);
	                                paramDelete.setNumFormato(numeroFactura);
	                                paramDelete.setPrefijo(prefijo);
	                                paramDelete.setCertificado(certificado);
	                                paramDelete.setNombreCertificado(nombreCertificado);
	                                paramDelete.setPassCertificado(passCertificado);
	                                paramDelete.setNumDocumentoContribuyente(nitCompania);
	
	                                Gson gson2 = new Gson();
	                                String json = gson2.toJson(paramDelete, ParametroDeleteEnvioFactura.class);
	
	                                APIFrida apiFrida = new APIFrida();
	
	                                log = log + "\n" + apiFrida.deleteEnvioFactura(url, json);
	                            }
	                        }
	
	                    }
	
	                    log = log + "\n" + exportarFacturas(url, numeroFactura,
	                                    SysmanFunciones.nvl(reg.getCampos().get("TIPOCOBRO"), "").toString());
	
	                }
	
	            }
	            c.add(Calendar.DATE, -1);
	            fechaFin = c.getTime();
	
	            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
	            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarFacturas.txt");
	
	            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
            }else {
            	try {
            		List<Registro> listaFactSysman = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            FrmFactLoteControladorUrlEnum.URL661078
                                                                                                                            .getValue())
                                                                                            .getUrl(), param));
            		if (!listaFactSysman.isEmpty())
    	            {    	
    	                for (Registro reg : listaFactSysman)
    	                {
							String resultado = exportarFacturadorExterno(retornarString(reg, "NUMEROFACTURA"), tipoCobro);
            				log += "\n" + resultado;
    	                }	
	    	    		ByteArrayInputStream streamTexto;
	    			
	    				streamTexto = JsfUtil
	    				        .serializarPlano(log);
	    				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
	    	                    "LogEnviarDian.txt");
	    	    		JsfUtil.agregarMensajeInformativo(
	    	                    idioma.getString("MSM_PROCESO_EJECUTADO"));
    	                
    	            }else {
    	            	log = log + "No se encontraron facturas";
    	            }
    			} catch (JRException | IOException | JAXBException e) {
					log += "\nERROR: " + e.getMessage();
    				logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
    			}     
            }
        }
        catch (SystemException | IOException | SysmanException | JRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    /**
     * este metodo permite obtener el valor de un campo del registro
     * @param reg
     * @param campo
     * @return
     */
    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
    /**
     * este metodo permite hacer la creacion de xml y llenado del mismo, para ser enviado al web services de invoway proveedor tecnologico
     * @param numeroFactura
     * @param tipoCobro
     * @return
     * @throws JAXBException
     */
    private String exportarFacturadorExterno(String numeroFactura, String tipoCobro) throws JAXBException {
    	Documento documento = new Documento();
		String respuesta = "";
		try {
			// se consulta la url configurada en el parametro
			String url = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					FrmFactLoteControladorEnum.URL_SERVICIO_SOAP.getValue(), "69", new Date(), false),"");
			if(url == null) {
				JsfUtil.agregarMensajeError("Por favor configure la url para el envio.");
	            return "Por favor configure la url para el envio.";
			}
			
	        Map<String, Object> param = new TreeMap<>();
	        
	        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	                   
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaInicio));
	        param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaFin));
	        param.put(FacturacionconceptosControladorEnum.NUMEROFACTURA.getValue(),
	                        numeroFactura);
	        param.put("TIPOCOBRO", tipoCobro);

        
			Registro rs2 = RegistroConverter
			                    .toRegistro(requestManager.get(
			                                    UrlServiceUtil.getInstance()
			                                                    .getUrlServiceByUrlByEnumID(
			                                                                    FrmFactLoteControladorUrlEnum.URL661077
			                                                                                    .getValue())
			                                                    .getUrl(),
			                                    param));
			
			if(rs2 == null) 
			{
				respuesta = "No se encontró información para la factura: " + numeroFactura;
			}
			else
			{
				//Datos basico fijos
				documento.setNumeroDocumento(SysmanFunciones
						.nvl(rs2.getCampos().get("PREFIJO"), "")
						.toString()+numeroFactura);
				documento.setTipoDocumento("FA");
		        documento.setSubtipoDocumento("01");
		        documento.setTipoOperacion("10");
		        documento.setFechaDocumento(SysmanFunciones
												.nvl(rs2.getCampos().get("FECHAFACTURA"), "")
														.toString());
				documento.setDivisa(SysmanFunciones
                        .nvl(rs2.getCampos().get("TIPO_MONEDA"), "")
                        .toString().equals("602")?"COP":"");
				documento.setDireccionFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DIRECCIONCOMPANIA"), "")
                        .toString());
				documento.setDistritoFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOCOMPANIA"), "")
                        .toString());
		        documento.setCiudadFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOCOMPANIA"), "")
                        .toString()+SysmanFunciones
                        .nvl(rs2.getCampos().get("CIUDADCOMPANIA"), "")
                        .toString());
		        documento.setPaisFactura("CO");
		        //datos de proveedor
		        Proveedor p = new Proveedor(); 
		        p.setIdProveedor(nitCompania+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString());
		        documento.setProveedor(p);
		        // Fin Proveedor
		        String tipoPersona = SysmanFunciones
                        .nvl(rs2.getCampos().get("NATURALEZATERCERO"), "")
                        .toString();
		        
		        // se crean datos del cliente
		        Cliente c = new Cliente();
		        c.setTipoPersonaCliente(tipoPersona.equals("N")?"1":"2");
		        c.setDireccionCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DIRECCIONTERCERO"), "")
                        .toString());
		        c.setCodigoPostalCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("CODIGOPOSTALTERCERO"), "")
                        .toString());
		        c.setTelefonoCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("TELEFONOS"), "")
                        .toString());
		        c.setEmailCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("EMAILTECERO"), "")
                        .toString());
		        c.setPaisCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("PAISTERCERO"), "")
                        .toString());
		        c.setIdCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("NUMTERCERO"), "").toString()+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());
				c.setTipoDocumentoIdCliente("31");
				c.setDistritoCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOTERCERO"), "")
                        .toString());
		        c.setCiudadCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOTERCERO"), "")
                        .toString() + SysmanFunciones 
                        .nvl(rs2.getCampos().get("CIUDADTERCERO"), "")
                        .toString());
		        c.setRazonSocialCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setNombreCliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setApellido1Cliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setApellido2Cliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setRegimenCliente("49");
		        
		        documento.setCliente(c);
		        // fin cliente
		        // gestios de impuestos
		        BigDecimal totalImpuestos = new BigDecimal("0");
		        Impuestos impuestosFin = new Impuestos();
		        Registro impuestosGeneralesIva = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5768
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesIva != null)


		        {		        	
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesIva.getCampos().get("SUMADEVALORIMPUESTO").toString(), 0).toString()) > 0) {
			        	Impuesto impuesto = new Impuesto();
			        	impuesto.setBaseImpuesto(new BigDecimal(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString()));
			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesIva.getCampos().get(
                                        "PORCIVA"),
                        "0").toString()));
			        	double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString())*Double.parseDouble(impuestosGeneralesIva.getCampos().get("PORCIVA").toString())/100),2);
			        	impuesto.setValorImpuesto(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));
			        	impuesto.setCodImpuesto("01");
			        	impuestosFin.getImpuesto().add(impuesto);
			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
		        	}
		        }
		        Registro impuestosGeneralesInc = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5773
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesInc != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesInc.getCampos().get("SUMADEIMPUESTO_INC").toString(), 0).toString()) > 0) {
			        	Impuesto impuesto = new Impuesto();
			        	impuesto.setBaseImpuesto(new BigDecimal(impuestosGeneralesInc.getCampos().get("SUMADEBASEIMPUESTOINC").toString()));
			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesInc.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()));
			        	double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesInc.getCampos().get("SUMADEBASEIMPUESTOINC").toString())*Double.parseDouble(impuestosGeneralesInc.getCampos().get("PORCENTAJE").toString())/100),2);
			        	impuesto.setValorImpuesto(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));
						impuesto.setCodImpuesto("04");
			        	impuestosFin.getImpuesto().add(impuesto);
			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
		        	}			        
		        }
		        // gestion retenciones
		        BigDecimal totalRetenciones = new BigDecimal("0");
		        Retenciones retencionesFin = new Retenciones();
		        Registro impuestosGeneralesReteIva = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5771
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesReteIva != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteIva.getCampos().get("SUMADERETEIVA").toString(), 0).toString()) > 0) {
			        	Retencion retencion = new Retencion();

			        	double baseRetencion = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString())*Double.parseDouble(impuestosGeneralesIva.getCampos().get("PORCIVA").toString())/100),2);
		        		retencion.setBaseRetencion(new BigDecimal(baseRetencion).setScale(2, RoundingMode.HALF_UP));
		        		retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteIva.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()));
						double valorRetencion = SysmanFunciones.redondear(baseRetencion*Double.parseDouble(impuestosGeneralesReteIva.getCampos().get("PORCENTAJE").toString())/100,2);
			        	retencion.setValorRetencion(new BigDecimal(valorRetencion).setScale(2, RoundingMode.HALF_UP));
			        	retencion.setCodRetencion("05");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}			        
		        }
		        Registro impuestosGeneralesReteFuente = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5769
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
										
		        if (impuestosGeneralesReteFuente != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteFuente.getCampos().get("SUMADEVALOR_RETEFUENTE").toString(), 0).toString()) > 0) {
			        	Retencion retencion = new Retencion();
			        	retencion.setBaseRetencion(new BigDecimal(impuestosGeneralesReteFuente.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE").toString()));
			        	retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteFuente.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()).setScale(2));
						double valorRetencion = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesReteFuente.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE").toString())*Double.parseDouble(impuestosGeneralesReteFuente.getCampos().get("PORCENTAJE").toString())/100),2);
			        	retencion.setValorRetencion(new BigDecimal(valorRetencion).setScale(2, RoundingMode.HALF_UP));
			        	retencion.setCodRetencion("06");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}			        
		        }       
		        // se agregan los productos 
		        List<Registro> listaProductos = RegistroConverter
		        .toListRegistro(
		                requestManager.getList(
		                                UrlServiceUtil
		                                                .getInstance()
		                                                .getUrlServiceByUrlByEnumID(
		                                                                FrmFactLoteControladorUrlEnum.URL2974
		                                                                                .getValue())
		                                                .getUrl(),
		                                param));
		        Lineas lineas = new Lineas();
	        	int cont = 1;
	        	double sumProdPorFactura = 0;
				double sumDescuentosFactura = 0;
	        	double sumsubTotalFactura = 0;
		        if(!listaProductos.isEmpty()) {
		        	
			        for(Registro reg: listaProductos) {
			        	//representa los items o productos de la factura
			        	Linea linea = new Linea();
				        linea.setNumLinea(cont);
				        linea.setIdEstandarReferencia("00"+cont);
				        linea.setDescripcionItem(SysmanFunciones
                                .nvl(reg.getCampos().get(
                                        "DESCRIPCIONPRODUCTO"),
                                        "")
                        .toString());
				        linea.setUnidadMedida("NIU");
				        linea.setUnidadesLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString(), 0).toString()));
				        linea.setPrecioUnidad(new BigDecimal(SysmanFunciones
                                .nvl(reg.getCampos()
                                        .get("VALORUNITARIO"),
                                        "0")
                        .toString()));

				        double subTotalLinea = Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALORUNITARIO").toString(), 0).toString())*Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString(), 0).toString());
				        linea.setSubtotalLinea(new BigDecimal(subTotalLinea).setScale(2, RoundingMode.HALF_UP));
				        
						//descuentos
				        if(Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString()) > 0) {
				        	Double porcentajeDescuenteo = new Double("0");
					        porcentajeDescuenteo = (Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString())*100)/Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("TOTALITEM").toString(), 0).toString());
					        linea.setPorcDescuentoLinea(new BigDecimal(porcentajeDescuenteo).setScale(2, RoundingMode.HALF_UP));
					        linea.setDescuentoLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString()));
							sumDescuentosFactura = sumDescuentosFactura + linea.getDescuentoLinea().doubleValue();
					    }else {
					    	Double porcentajeDescuenteo = new Double("0");
					        linea.setPorcDescuentoLinea(new BigDecimal(porcentajeDescuenteo));
					        linea.setDescuentoLinea(new BigDecimal("0"));
					    }

				        double totalLinea = subTotalLinea - Double.parseDouble(reg.getCampos().get("VALOR_DESCUENTO").toString());
				        linea.setTotalLinea(new BigDecimal(totalLinea).setScale(2, RoundingMode.HALF_UP));
				        
						// se suman los totales de los producctos
				        sumProdPorFactura = sumProdPorFactura + linea.getTotalLinea().doubleValue();
				        //se agrega impuesto principal IVA en caso de tener el impuesto
				        if(Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("BASEIMPUESTOIVA").toString(), 0).toString()) > 0) {
					        linea.setPorcImpuestoLinea(new BigDecimal(reg.getCampos().get("PORCIVA").toString()).setScale(2));
					        double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(reg.getCampos().get("BASEIMPUESTOIVA").toString())*Double.parseDouble(reg.getCampos().get("PORCIVA").toString())/100),2);
					        linea.setValorImpuestoLinea(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));
					        linea.setCodImpuestoLinea("01");
				        }
						// se suman los subtotales de los productos
				        sumsubTotalFactura = sumsubTotalFactura + linea.getSubtotalLinea().doubleValue();				        
				        lineas.getLinea().add(linea);
				        cont++; 
			        }			        
		        }	      
		        documento.setLineas(lineas);		        
		         // Se agregan los datos totales
		        DatosTotales totalesFact = new DatosTotales();
		        double totalBase = sumsubTotalFactura - sumDescuentosFactura;
		        double totalDoc = totalBase + totalImpuestos.doubleValue();
		        totalesFact.setAPagar(new BigDecimal(totalDoc).setScale(2, RoundingMode.HALF_UP));
				totalesFact.setSubtotal(new BigDecimal(sumsubTotalFactura).setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalBase(new BigDecimal(totalBase).setScale(2, RoundingMode.HALF_UP));        
		        totalesFact.setTotalImpuestos(totalImpuestos);
		        totalesFact.setTotalGastos(new BigDecimal(0));
		        totalesFact.setTotalRetenciones(totalRetenciones);
		        totalesFact.setTotalDocumento(new BigDecimal(totalDoc).setScale(2, RoundingMode.HALF_UP));
		        
		        // total factura

		        BigDecimal totalFactFin = totalesFact.getTotalBase();
			    totalFactFin = totalFactFin.add(totalImpuestos);

			    totalesFact.setTotalDocumento(totalFactFin);
			    totalesFact.setAPagar(totalFactFin);
			    documento.setImpuestos(impuestosFin);


				documento.setRetenciones(retencionesFin);		        
		        documento.setDatosTotales(totalesFact);
		        
		        CondicionPago formaPago = new CondicionPago();
		        formaPago.setMedioPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("MEDIOPAGO"), "")
                        .toString());
		        formaPago.setFormaPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("TIPO_PAGO"), "")
                        .toString());
		        formaPago.setFechaPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("FECHA_VENCIMIENTO"), "")
                        .toString());  
		        CondicionesPago condicionesFormaPago = new CondicionesPago();
		        condicionesFormaPago.getCondicionPago().add(formaPago);
		        documento.setCondicionesPago(condicionesFormaPago);
		        ApiInvoway apiInvoway = new ApiInvoway();
		        
		        // se consulta los parametros para traer el usuario y la contraseña del ws invoway
		        String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		FrmFactLoteControladorEnum.USUARIO_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");
		        
		        String pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		FrmFactLoteControladorEnum.CLAVE_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");

		        
		        respuesta = apiInvoway.postEnvioFactura(url, documento, pass, user)+" .NrO : "+documento.getNumeroDocumento();

			}  
		        
		} catch (SystemException | IOException | com.sysman.util.SysmanException e1) {
			logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());

            return "ERROR en factura " + numeroFactura + ": " + e1.getMessage();
		}
		
		return respuesta;
	}
    /**
     * verifica descuentos de la factura
     * @param parametrosConsulta
     * @param nConceptos
     * @param sumProdPorFactura
     * @throws NumberFormatException
     * @throws SystemException
     */
    private void verificarDescuentos(Map<String, Object> parametrosConsulta, Double nConceptos, Double sumProdPorFactura) throws NumberFormatException, SystemException {
    	int digRedDian = Integer.parseInt(SysmanFunciones
                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                "SF NUMERO DIGITOS REDONDEO DIAN",
                                SessionUtil.getModulo(), new Date(),
                                false), "0").toString());
    	double descuento = 0;
    	double redondeodescuento = 0;
    	Registro rs4 = RegistroConverter
                .toRegistro(requestManager.get(
                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmFactLoteControladorUrlEnum.URL4587
                                                                                .getValue())
                                                .getUrl(),
                                                parametrosConsulta));

		if (rs4 != null)
		{
			if (digRedDian == 0)
		    {
		        totalFactura = SysmanFunciones.redondear(
		                        SysmanFunciones.nvlDbl((SysmanFunciones
		                                        .nvlDbl(rs4.getCampos()
		                                                        .get(
		                                                                        GeneralParameterEnum.TOTAL
		                                                                                        .getName()),
		                                                        0)
		                            -
		                            (SysmanFunciones.nvlDbl(rs4
		                                            .getCampos()
		                                            .get("DESCUENTO_FACTURA"),
		                                            0)
		                                / nConceptos)
		                            - descuento), 0),
		                        2);
		    }
		    else if (digRedDian % 10 == 0)
		    {
		        totalFactura = (SysmanFunciones.nvlDbl(SysmanFunciones
		                        .nvlDbl(rs4.getCampos()
		                                        .get(
		                                                        GeneralParameterEnum.TOTAL
		                                                                        .getName()),
		                                        0)
		            - (SysmanFunciones.nvlDbl(rs4
		                            .getCampos()
		                            .get("DESCUENTO_FACTURA"),
		                            0)
		                / nConceptos)
		            - descuento, 0) / digRedDian + 0.501) * digRedDian;
		
		    }
		    else
		    {
		        totalFactura = SysmanFunciones.nvlDbl((SysmanFunciones
		                        .nvlDbl(rs4.getCampos().get(
		                                        GeneralParameterEnum.TOTAL
		                                                        .getName()),
		                                        0)
		            -
		            (SysmanFunciones.nvlDbl(rs4.getCampos()
		                            .get("DESCUENTO_FACTURA"), 0)
		                / nConceptos)
		            - descuento), 0) + digRedDian;
		    }
		
		    if (sumProdPorFactura < totalFactura &&
		        (totalFactura - sumProdPorFactura) > 1)
		    {
		
		        ParametrosCargos paramCargos = new ParametrosCargos();
		
		        paramCargos.setValor((int) SysmanFunciones
		                        .redondear(totalFactura
		                            - sumProdPorFactura, 2));
		
//		        listaParamCargos.add(paramCargos);
		
		    }
		    else if (sumProdPorFactura > totalFactura &&
		        (sumProdPorFactura - totalFactura) > 1)
		    {
		
		        redondeodescuento = SysmanFunciones.redondear(
		                        sumProdPorFactura
		                            - totalFactura,
		                        2);
		
		        ParametrosDescuentos paramDescuento = new ParametrosDescuentos();
		
		        paramDescuento.setTipo("05");
		
		        paramDescuento.setValor(
		                        (int) redondeodescuento);
		
//		        listaParamDescuentos.add(paramDescuento);
		
		    }
		}
    }
    private String exportarFacturas(String url, String numeroFactura, String tipoFactura)
    {
        String respuesta = null;
        String datosFacturaBancos;
        String strObservaciones;
        double valorIVAConcepto = 0;
        double sumProdPorFactura = 0;
        double descuento = 0;
        double totalFactura = 0;
        double redondeodescuento = 0;
        int digRedDian;

        int nConceptos = 0;

        ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

        ParametroCuerpoEnvioFactura paramCuerpoFactura = new ParametroCuerpoEnvioFactura();

        Map<String, Object> param = new TreeMap<>();
        /**
         * @author ldiaz en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros pues el systema requiere que este entre 1 y 12
         */

        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), formatFecha.format(fechaInicio));
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaFin));
        param.put("NUMEROFACTURA", numeroFactura);
        param.put("TIPOCOBRO", tipoFactura);

        try
        {

            digRedDian = Integer.parseInt(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "SF NUMERO DIGITOS REDONDEO DIAN", SessionUtil.getModulo(), new Date(), false), "0").toString());
            Registro rs2 = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmFactLoteControladorUrlEnum.URL3564.getValue()).getUrl(),
                            param));

            if (rs2 != null)
            {

                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.CODIGO.getName(),
                                rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

                Registro rs3 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL9512
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param2));

                paramFactura.setCreatedBy(rs3.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());

                paramFactura.setNumerocontribuyente(nitCompania);

                if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                                "SF SELECCIONAR CUENTA DE RECAUDO PARA FACTURACION ELECTRONICA", SessionUtil.getModulo(),
                                new Date(), false)))
                {

                    Map<String, Object> params = new TreeMap<>();

                    params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                    params.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
                    params.put(GeneralParameterEnum.CUENTA.getName(),
                                    SysmanFunciones.nvl(rs2.getCampos().get("CUENTA_RECUADO"), ""));

                    rs3 = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmFactLoteControladorUrlEnum.URL3651.getValue())
                                                                    .getUrl(),
                                                    param2));

                    if (rs3 != null)
                    {

                        datosFacturaBancos = SysmanFunciones.nvl(rs3.getCampos().get("LEYENDA"), "").toString();
                    }
                    else
                    {
                        datosFacturaBancos = "";
                    }

                }
                else
                {

                    datosFacturaBancos = ejbSysmanUtil.consultarParametro(compania, "DATOS FACTURA BANCOS",
                                    SessionUtil.getModulo(), new Date(), false);

                }

                strObservaciones = SysmanFunciones
                                .nvl(rs2.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()), "").toString();

                paramCuerpoFactura.setNumTercero(SysmanFunciones.nvl(rs2.getCampos().get("NUMTERCERO"), "").toString());

                paramCuerpoFactura
                                .setFechafactura(SysmanFunciones.nvl(rs2.getCampos().get("FECHAFACTURA"), "").toString());

                paramCuerpoFactura.setFechaVencimiento(
                                SysmanFunciones.nvl(rs2.getCampos().get("FECHA_VENCIMIENTO"), "").toString());

                paramCuerpoFactura.setNumerofactura(Integer.parseInt(numeroFactura));

                paramCuerpoFactura
                                .setTelefonoCliente(SysmanFunciones.nvl(rs2.getCampos().get("TELEFONOS"), "").toString());

                paramCuerpoFactura.setTipoPago(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_PAGO"), "").toString());

                paramCuerpoFactura.setMedioPago(SysmanFunciones.nvl(rs2.getCampos().get("MEDIOPAGO"), "").toString());

                paramCuerpoFactura
                                .setTipoMoneda(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_MONEDA"), "").toString());

                paramCuerpoFactura.setPrefijo(SysmanFunciones.nvl(rs2.getCampos().get("PREFIJO"), "").toString());

                paramCuerpoFactura
                                .setTipoOperacion(SysmanFunciones.nvl(rs2.getCampos().get("TIPOFACDIAN"), "").toString());

                paramCuerpoFactura.setObservacionesFactura(strObservaciones + "<br/>" + datosFacturaBancos);

                paramCuerpoFactura.setReviso(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                                "SF NOMBRE REVISO EN FACTURA", SessionUtil.getModulo(), new Date(), false), "").toString());

                paramCuerpoFactura.setDatosSoftware(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                                "DATOS SOFTWARE FACTURA ELECTRONICA", SessionUtil.getModulo(), new Date(), false), "")
                                .toString());
                
                //7742153_FACGENERAL prefijo+contrato
                paramCuerpoFactura.setPrefijoOrden(SysmanFunciones
                        .nvl(rs2.getCampos().get("PREFIJOORDEN"), "")
                        .toString());
                
                paramCuerpoFactura.setNumeroPrefijoOrden(SysmanFunciones
                        .nvl(rs2.getCampos().get("NUMEROPREFIJOORDEN"), "")
                        .toString());
                
                if (!"0".equals(rs2.getCampos().get("DIFERIDA").toString()))
                {

                    paramCuerpoFactura
                                    .setNumCuotas(SysmanFunciones.nvl(rs2.getCampos().get("CUOTAS_DIFERIDAS"), "0").toString());

                }

                // Productos

                List<ParametrosItems> listaParamItems = new ArrayList<>();
                int contador = 0;
                Map<String, Object> param3 = new TreeMap<>();

                param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param3.put(GeneralParameterEnum.FECHAINICIAL.getName(), formatFecha.format(fechaInicio));
                param3.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaFin));
                param3.put("NUMEROFACTURA", numeroFactura);
                param3.put("TIPOCOBRO", tipoFactura);

                List<Registro> listaProductos = RegistroConverter
                                .toListRegistro(
                                                requestManager.getList(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL2974
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param3));

                if (!listaProductos.isEmpty())
                {

                    for (Registro reg3 : listaProductos)
                    {

                        valorIVAConcepto = SysmanFunciones.nvlDbl(reg3.getCampos().get("BASEIMPUESTOIVA"), 0)
                            * (SysmanFunciones.nvlDbl(reg3.getCampos().get("PORCIVA"), 0) / 100);

                        sumProdPorFactura = sumProdPorFactura
                            + SysmanFunciones.nvlDbl(reg3.getCampos().get("VALORUNITARIO"), 0)

                                * SysmanFunciones.nvlDbl(reg3.getCampos().get("CANTIDAD"), 0)
                            - SysmanFunciones.nvlDbl(reg3.getCampos().get("VALOR_DESCUENTO"), 0) + valorIVAConcepto;

                        ParametrosItems paramItems = new ParametrosItems();

                        paramItems.setCodigoproducto(
                                        SysmanFunciones.nvl(reg3.getCampos().get("CODIGOPRODUCTO"), "").toString());
                        /**
                         * @author ljdiaz
                         * @descrpcion: se cambia el tipo de dato que recibe la cantidad de el producto, para que esta reciba cantidades decimales y entenera.
                         */
                        paramItems.setCantidad(
                                        Double.parseDouble(reg3.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString()));

                        paramItems.setDescripcionproducto(
                                        SysmanFunciones.nvl(reg3.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

                        paramItems.setValorunitario(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("VALORUNITARIO"), "0").toString()));

                        paramItems.setTipoDescuento("05");

                        paramItems.setDescuentoItem(
                                        SysmanFunciones.nvl(reg3.getCampos().get("VALOR_DESCUENTO"), "0").toString());

                        paramItems.setTotalitem(Double
                                        .parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("TOTALITEM"), "0").toString()));

                        List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();

                        ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
                        paramItemImpuestos.setTipo("01");

                        paramItemImpuestos.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOIVA"), "0").toString()));

                        paramItemImpuestos.setPorcentaje(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOIVA"), "0").toString()) == 0 ? 0
                                            : Double.parseDouble(
                                                            SysmanFunciones.nvl(reg3.getCampos().get("PORCIVA"), "0").toString()));

                        paramItemImpuestos.setValor(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("VALORIMPUESTO"), "0").toString()));

                        ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
                        paramItemImpuestos2.setTipo("06");

                        paramItemImpuestos2.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()));

                        paramItemImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
                                        .nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()) == 0 ? 0
                                            : Double.parseDouble(SysmanFunciones
                                                            .nvl(reg3.getCampos().get("PORCENTAJERETEFUENTEX"), "0").toString()));

                        paramItemImpuestos2.setValor(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("VALOR_RETEFUENTE"), "0").toString()));

                        ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
                        paramItemImpuestos3.setTipo("03");

                        paramItemImpuestos3.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()));

                        paramItemImpuestos3.setPorcentaje(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()) == 0 ? 0
                                            : Double.parseDouble(SysmanFunciones
                                                            .nvl(reg3.getCampos().get("PORCENTAJEICAX"), "0").toString()));

                        paramItemImpuestos3.setValor(Double
                                        .parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("VALOR_ICA"), "0").toString()));

                        ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
                        paramItemImpuestos4.setTipo("05");

                        paramItemImpuestos4.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()));

                        paramItemImpuestos4.setPorcentaje(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()) == 0
                                            ? 0
                                            : Double.parseDouble(SysmanFunciones
                                                            .nvl(reg3.getCampos().get("PORCENTAJERETEIVADX"), "0").toString()));

                        paramItemImpuestos4.setValor(Double
                                        .parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEIVA"), "0").toString()));

                        ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
                        paramItemImpuestos5.setTipo("07");

                        paramItemImpuestos5.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()));

                        paramItemImpuestos5.setPorcentaje(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()) == 0
                                            ? 0
                                            : Double.parseDouble(SysmanFunciones
                                                            .nvl(reg3.getCampos().get("PORCENTAJERETEICAX"), "0").toString()));

                        paramItemImpuestos5.setValor(Double
                                        .parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEICA"), "0").toString()));

                        ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
                        paramItemImpuestos6.setTipo("02");

                        paramItemImpuestos6.setBase(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()));

                        paramItemImpuestos6.setPorcentaje(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()) == 0 ? 0
                                            : Integer.parseInt(SysmanFunciones
                                                            .nvl(reg3.getCampos().get("PORCENTAJEINCX"), "0").toString()));

                        paramItemImpuestos6.setValor(Double.parseDouble(
                                        SysmanFunciones.nvl(reg3.getCampos().get("IMPUESTO_INC"), "0").toString()));

                        if (paramItemImpuestos.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos);
                        }
                        if (paramItemImpuestos2.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos2);
                        }
                        if (paramItemImpuestos3.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos3);
                        }
                        if (paramItemImpuestos4.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos4);
                        }
                        if (paramItemImpuestos5.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos5);
                        }
                        if (paramItemImpuestos6.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos6);
                        }

                        paramItems.setImpuestos(listaParamItemImpuestos);

                        listaParamItems.add(contador, paramItems);

                        contador++;

                        nConceptos++;
                    }

                }
                // agregar lista items
                paramCuerpoFactura.setItems(listaParamItems);
                Map<String, Object> param4 = new TreeMap<>();

                param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param4.put(GeneralParameterEnum.FECHAINICIAL.getName(), formatFecha.format(fechaInicio));
                param4.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaFin));
                param4.put("NUMEROFACTURA", numeroFactura);
                param4.put("TIPOCOBRO", tipoFactura);

                /* Registro rs4 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL4987
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                if (rs4 != null)
                {

                    descuento = SysmanFunciones.nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0);
                } */ //COMENTADO POR  JM CC 2693 (OSEA WDF porque el descuento es el total de la factura??? quien me explica??)

                List<ParametrosCargos> listaParamCargos = new ArrayList<>();
                List<ParametrosDescuentos> listaParamDescuentos = new ArrayList<>();

                Registro rs4 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL4587
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                if (rs4 != null)
                {

                    if (digRedDian == 0)
                    {
                        totalFactura = SysmanFunciones.redondear(SysmanFunciones.nvlDbl(SysmanFunciones
                                        .nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0)
                            - SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0) / nConceptos
                            - descuento, 0), 2);
                    }
                    else if (digRedDian % 10 == 0)
                    {
                        totalFactura = (SysmanFunciones.nvlDbl(SysmanFunciones
                                        .nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0)
                            - SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0) / nConceptos
                            - descuento, 0) / digRedDian + 0.501) * digRedDian;

                    }
                    else
                    {
                        totalFactura = SysmanFunciones.nvlDbl(SysmanFunciones
                                        .nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0)
                            - SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0) / nConceptos
                            - descuento, 0) + digRedDian;
                    }

                    if (sumProdPorFactura < totalFactura && totalFactura - sumProdPorFactura > 1)
                    {

                        ParametrosCargos paramCargos = new ParametrosCargos();

                        paramCargos.setValor((int) SysmanFunciones.redondear(totalFactura - sumProdPorFactura, 2));

                        listaParamCargos.add(paramCargos);
                        
                        

                    }
                    else if (sumProdPorFactura > totalFactura && sumProdPorFactura - totalFactura > 1)
                    {

                        redondeodescuento = SysmanFunciones.redondear(sumProdPorFactura - totalFactura, 2);

                        ParametrosDescuentos paramDescuento = new ParametrosDescuentos();

                        paramDescuento.setTipo("05");

                        paramDescuento.setValor((int) redondeodescuento);

                        listaParamDescuentos.add(paramDescuento);

                    }else if ((sumProdPorFactura - totalFactura) == 0 && SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) > 0)// JM CC 2693
                    { 
                    	

			                        	ParametrosDescuentos paramDescuento = new ParametrosDescuentos();
			                        	
			                        	paramDescuento.setTipo("05");
			
			                            paramDescuento.setValor((int) SysmanFunciones.redondear(SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0), 2));
			
			                            listaParamDescuentos.add(paramDescuento);
			                    
                    }
                }
                paramCuerpoFactura.setCargos(listaParamCargos);
                paramCuerpoFactura.setDescuentos(listaParamDescuentos);

                rs4 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL7452
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                if (rs4 != null)
                {

                    paramCuerpoFactura.setSubtotalfactura(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("SUBTOTALFACTURA"), "0").toString()));

                    paramCuerpoFactura.setValorfactura(totalFactura);

                    paramCuerpoFactura.setReteFuente(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("RETE"), "0").toString()));

                    paramCuerpoFactura.setReteIva(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("RETE_IVA"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableIva(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableRete(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableIca(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEICA"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableReteiva(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableIca(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEICA"), "0").toString()));

                    paramCuerpoFactura.setTotalBaseGravableInc(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEINC"), "0").toString()));
                    
                    paramCuerpoFactura.setValorIvaFactura(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));

                    paramCuerpoFactura.setValorIcaFactura(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("ICA"), "0").toString()));

                    paramCuerpoFactura.setNumeroConceptos(nConceptos);

                    paramCuerpoFactura.setReteIca(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("RETE_ICA"), "0").toString()));

                    paramCuerpoFactura.setValorIncFactura(
                                    Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("INC"), "0").toString()));

                    paramCuerpoFactura.setDescuentoItems(Double
                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTOPARCIAL"), "0").toString()));

                    paramCuerpoFactura
                                    .setDescuentoFactura(SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0)

                                        / nConceptos + redondeodescuento);

                    // correcion de toma de campo descripcion que no sobrepase los 250 caracteres
                    String descFact = SysmanFunciones
                            .nvl(rs4.getCampos().get(
                                    "OBSERVACIONES"), "0").toString();
                    
                    paramCuerpoFactura.setDescripcion(descFact);
                    
                    if(paramCuerpoFactura.getValorIvaFactura() == 0 && 
                    		paramCuerpoFactura.getValorIcaFactura() == 0 &&
                    			paramCuerpoFactura.getValorIncFactura() == 0 &&
                    				paramCuerpoFactura.getReteIva() == 0 &&
                    					paramCuerpoFactura.getReteFuente() == 0 &&
                    						paramCuerpoFactura.getReteIca() == 0) {
                    	paramCuerpoFactura.setTotalBaseImponible(Double.parseDouble(
                                "0"));
                    }else {
                    	if(nConceptos > 1 ) {
                    		paramCuerpoFactura.setTotalBaseImponible(paramCuerpoFactura
                                    .getTotalBaseGravableIva() + paramCuerpoFactura
                                    .getTotalBaseGravableIca() + paramCuerpoFactura
                                    .getTotalBaseGravableInc()- SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) ); //JM CC 2693
                    	}else {
                    		paramCuerpoFactura.setTotalBaseImponible(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "BASEIMPONIBLE"), "0")
                                                    .toString())- SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) ); //JM CC 2693
                    	}
                    }
                   
                }
                //7741966_FACGENERAL mrosero
                if(paramCuerpoFactura.getDescripcion().equals("0")) {
                	Registro rsDes = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmFactLoteControladorUrlEnum.URL661076
                                                                                            .getValue())
                                                            .getUrl(),
                                            param4));
                	if(rsDes != null) {
                		String descFact = SysmanFunciones
                                .nvl(rsDes.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()),"0").toString();
                        
                		paramCuerpoFactura.setDescripcion(descFact);
                	}
                }

                List<ParametrosImpuestos> listaParamImpuestos = new ArrayList<>();

                // impuestos
                // PORCENTAJEIVAX
                Registro rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5768
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos.setTipo("01");
                    paramImpuestos.setBase(0);
                    paramImpuestos.setPorcentaje(0);
                    paramImpuestos.setValor(0);

                    // listaParamImpuestos.add(0, paramImpuestos);

                }
                else
                {
                    paramImpuestos.setTipo("01");
                    paramImpuestos.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOIVA"), "0").toString()));

                    paramImpuestos.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("PORCIVA"), "0").toString()));

                    paramImpuestos.setValor(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALORIMPUESTO"), "0").toString()));

                    if (paramImpuestos.getBase() != 0)
                    {
                        listaParamImpuestos.add(0, paramImpuestos);
                    }
                }

                // PORCENTAJERETEFUENTEX

                rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5769
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos2 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos2.setTipo("06");
                    paramImpuestos2.setBase(0);
                    paramImpuestos2.setPorcentaje(0);
                    paramImpuestos2.setValor(0);

                    // listaParamImpuestos.add(1, paramImpuestos2);

                }
                else
                {
                    paramImpuestos2.setTipo("06");
                    paramImpuestos2.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE"), "0").toString()));

                    paramImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
                                    .nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

                    paramImpuestos2.setValor(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_RETEFUENTE"), "0").toString()));

                    if (paramImpuestos2.getBase() != 0)
                    {
                        listaParamImpuestos.add(1, paramImpuestos2);
                    }
                }

                // PORCENTAJEICAX

                rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5770
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos3 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos3.setTipo("03");
                    paramImpuestos3.setBase(0);
                    paramImpuestos3.setPorcentaje(0);
                    paramImpuestos3.setValor(0);

                    // listaParamImpuestos.add(2, paramImpuestos3);

                }
                else
                {
                    paramImpuestos3.setTipo("03");
                    paramImpuestos3.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOICA"), "0").toString()));

                    paramImpuestos3.setPorcentaje(Double.parseDouble(SysmanFunciones
                                    .nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

                    paramImpuestos3.setValor(Double
                                    .parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_ICA"), "0").toString()));

                    if (paramImpuestos3.getBase() != 0)
                    {
                        listaParamImpuestos.add(2, paramImpuestos3);
                    }
                }

                // PORCENTAJERETEIVADX
                rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5771
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos4 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos4.setTipo("05");
                    paramImpuestos4.setBase(0);
                    paramImpuestos4.setPorcentaje(0);
                    paramImpuestos4.setValor(0);

                    // listaParamImpuestos.add(3, paramImpuestos4);

                }
                else
                {
                    paramImpuestos4.setTipo("05");
                    paramImpuestos4.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEIVA"), "0").toString()));

                    paramImpuestos4.setPorcentaje(Double.parseDouble(SysmanFunciones
                                    .nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

                    paramImpuestos4.setValor(Double
                                    .parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADERETEIVA"), "0").toString()));

                    if (paramImpuestos4.getBase() != 0)
                    {
                        listaParamImpuestos.add(3, paramImpuestos4);
                    }
                }

                // PORCENTAJERETEICAX

                rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5772
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos5 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos5.setTipo("07");
                    paramImpuestos5.setBase(0);
                    paramImpuestos5.setPorcentaje(0);
                    paramImpuestos5.setValor(0);

                    // listaParamImpuestos.add(4, paramImpuestos5);

                }
                else
                {
                    paramImpuestos5.setTipo("07");
                    paramImpuestos5.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEICA"), "0").toString()));

                    paramImpuestos5.setPorcentaje(Double.parseDouble(SysmanFunciones
                                    .nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

                    paramImpuestos5.setValor(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

                    if (paramImpuestos5.getBase() != 0)
                    {
                        listaParamImpuestos.add(4, paramImpuestos5);
                    }
                }
                // PORCENTAJEINCX

                rs5 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL5773
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param4));

                ParametrosImpuestos paramImpuestos6 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos6.setTipo("02");
                    paramImpuestos6.setBase(0);
                    paramImpuestos6.setPorcentaje(0);
                    paramImpuestos6.setValor(0);

                    // listaParamImpuestos.add(5, paramImpuestos6);

                }
                else
                {
                    paramImpuestos6.setTipo("02");
                    paramImpuestos6.setBase(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOINC"), "0").toString()));

                    paramImpuestos6.setPorcentaje(Double.parseDouble(SysmanFunciones
                                    .nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

                    paramImpuestos6.setValor(Double.parseDouble(
                                    SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

                    if (paramImpuestos6.getBase() != 0)
                    {
                        listaParamImpuestos.add(5, paramImpuestos6);
                    }
                }

                paramCuerpoFactura.setImpuestos(listaParamImpuestos);
            }

            List<ParametroCuerpoEnvioFactura> listaCuerpoFactura = new ArrayList<>();
            listaCuerpoFactura.add(paramCuerpoFactura);

            paramFactura.setFacturas(listaCuerpoFactura);

            APIFrida api2 = new APIFrida();

            Gson gson2 = new Gson();
            String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);
            respuesta = api2.postEnvioFactura(url, json);

            RespuestaApi respuestaApi = gson2.fromJson(respuesta, RespuestaApi.class);

            if (respuestaApi.getCodigo() != 0)
            {
                respuesta = respuestaApi.getMensaje().toString();
            }

            return respuesta + "\n" + json;

        }
        catch (SystemException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;

    }

    private void validarProducto(String url, String codigoProducto)
    {

        String respuesta;
        APIFrida api = new APIFrida();
        Gson gson = new Gson();

        try
        {
            respuesta = api.cargarItem(url, codigoProducto);

            RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

            if (respuestaApi.getCodigo() != 0)
            {
                crearProducto(url, codigoProducto);
            }
        }
        catch (IOException | SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void crearProducto(String url, String codigoProducto)
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));

        param.put(GeneralParameterEnum.CODIGO.getName(), codigoProducto);

        try
        {
            Registro rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmFactLoteControladorUrlEnum.URL2486.getValue()).getUrl(),
                            param));

            if (rs != null)
            {
                ParamItem params = new ParamItem();
                ParamItems paramsItems = new ParamItems();

                List<ParamItem> listaParams = new ArrayList<>();

                params.setCodigoProducto(codigoProducto);
                params.setCreatedBy(usuario);
                params.setDescripcionProducto(
                                SysmanFunciones.nvl(rs.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

                params.setUnidadMedida(SysmanFunciones.nvl(rs.getCampos().get("UNIDADMEDIDA"), "").toString());

                params.setValorItem(SysmanFunciones.nvl(rs.getCampos().get("VALORITEM"), "").toString());

                listaParams.add(params);

                paramsItems.setItems(listaParams);

                Gson gson = new Gson();
                String json = gson.toJson(paramsItems, ParamItems.class);
                APIFrida apiFrida = new APIFrida();

                apiFrida.postItem(url, json);

            }
        }
        catch (SystemException | IOException | SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String crearTecero(String tercero, String url) throws SysmanException
    {
        String respuesta = null;
        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NIT.getName(), tercero);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmFactLoteControladorUrlEnum.URL7354
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            params));

            ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();

			ParametrosTercero param = new ParametrosTercero();
			//7742397_FACGENERAL
			if (rs.getCampos().get(GeneralParameterEnum.PAIS.getName()).equals("CO")) {
				
				param.setCodigomunicipio(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());

				param.setCiudad(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

				param.setCodigodepartamento(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());

				param.setDepartamento(SysmanFunciones
						.nvl(rs.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

			} else {
				param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());

				param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());

				param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());

				param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());			

			}

            param.setCorreoelectronico(SysmanFunciones.nvl(rs.getCampos().get("CORREOELECTRONICO"), "").toString());

            param.setDireccion(
                            SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "").toString());

            param.setDireccionfiscal(SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONFISCAL"), "").toString());

            param.setCodigopostal(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTAL"), "").toString());

            param.setNumerodocumento(SysmanFunciones.nvl(rs.getCampos().get("NUMERODOCUMENTO"), "").toString());

            param.setTelefono(SysmanFunciones.nvl(rs.getCampos().get("TELEFONO"), "").toString());
            
            param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "")).replace("&", "").replaceAll("\\s+", " ").trim());

			param.setTipoidentificacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOIDENTIFICACION"), "").toString());

            param.setDigitoverificacion(SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACION"), "").toString());
            
			param.setPais(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());             

            param.setTipoorganizacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOORGANIZACION"), "").toString());

            param.setTiporegimen(SysmanFunciones.nvl(rs.getCampos().get("TIPOREGIMEN"), "").toString());

            // Iteracion sobre obligaciones fisacles del tercero

            Map<String, Object> param2 = new TreeMap<>();

            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.NIT.getName(), tercero);

            List<Registro> listaObligaciones = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmFactLoteControladorUrlEnum.URL2054
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param2));

            if (!listaObligaciones.isEmpty())
            {
                String responsabilidadesFiscales = "";

                for (Registro reg : listaObligaciones)
                {
                    responsabilidadesFiscales = responsabilidadesFiscales + ","
                        + reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
                }

                param.setResponsabilidadesfiscales(
                                responsabilidadesFiscales.substring(1, responsabilidadesFiscales.length()));
            }

            paramTercero.setContribuyente(nitCompania);

            List<ParametrosTercero> listaParam = new ArrayList<>();

            listaParam.add(param);

            paramTercero.setTerceros(listaParam);

            Gson gson = new Gson();
            String json = gson.toJson(paramTercero, ParametrosTerceroLote.class);
            APIFrida apiFrida = new APIFrida();

            respuesta = apiFrida.postTercero(url, json);

        }
        catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton EnviarNotas en la vista
     *
     *
     */
    public void oprimirEnviarNotas()
    {
        archivoDescarga = null;
        String url;
        String log;

        try
        {

            log = "|---------------     ENVIO NOTAS FRIDA      ---------------|";

            url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

            /*
             * Tar 1000108430 Se aï¿½ade un dia a la fecha final, debido a que al realizar la consulta en la bd esta toma la fecha desde las 0 horas, por lo cual al enviarse la misma fecha no trae
             * registros. Ej: '01/01/2021' - '01/01/2021', no trae registros debido a que la busqueda se realiza desde las 0 horas 0 minutos y 0 segundos ('01/01/2021 00:00:00' - '01/01/2021
             * 00:00:00') mientras que al sumar un dia, se va a realizar la busqueda de la siguiente manera ('01/01/2021 00:00:00' - '02/01/2021 00:00:00')
             */

            Calendar c = Calendar.getInstance();
            c.setTime(fechaFin);
            c.add(Calendar.DATE, 1);
            fechaFin = c.getTime();

            if (SysmanFunciones.validarVariableVacio(url))
            {
                JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else
            {
                String respuesta;
                APIFrida api = new APIFrida();
                
                SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy/MM/dd");
                respuesta = api.cargarFormatoConsultarReporte(url, nitCompania, "10", "", "181",
                                formatFecha.format(fechaInicio),
                                formatFecha.format(fechaFin), "0", "1", "1");

                Gson gson = new Gson();
                RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);

                String comprobantes = "";

                for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas())
                {

                    comprobantes = comprobantes + "," + respuestaNotasReporte.getNumFormato();

                }
                // se consulta el parametro que define si se usa Facturador externo o FRIDA
                String facturadorExterno = "";
                try {
                	facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                			FrmFactLoteControladorEnum.MANEJA_FACTURACION_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"NO");
                }catch (Exception e) {
                	facturadorExterno = "NO";
        		}
                Map<String, Object> param = new TreeMap<>();

                comprobantes = SysmanFunciones.nvlStr(comprobantes, SysmanConstantes.CONS_FUENTE);
                
                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                param.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobantes.substring(1, comprobantes.length()));
                param.put("FECHAINICIAL", formatFecha.format(fechaInicio));
                param.put("FECHAFINAL", formatFecha.format(fechaFin));

                List<Registro> listaComprobantes = RegistroConverter
                                .toListRegistro(
                                                requestManager.getList(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL8921
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param));

                if (!listaComprobantes.isEmpty())
                {

                    for (Registro reg : listaComprobantes)
                    {
                    	// se valida si el tercero de la factura es el mismo tercero de la nota
                    	if(reg.getCampos().get("TERCERO") != null 
                    			&& reg.getCampos().get("TERCERODETALLE") != null
                    			&& !reg.getCampos().get("TERCERO").toString().trim().equals(reg.getCampos().get("TERCERODETALLE").toString().trim())) {
                    		log = log + "\n El tercero de la factura no corresponde al tercero de la nota, revisar contabilidad Factura N° "+reg.getCampos().get("NUMERO_FACTURA").toString()+" Numero nota: "+reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString()+"\n";
                    		continue;
                    	}                    	
                   		// se valida si se usa facturacion electronica externa o FRIDA           
                        if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
                        	log = log + "\n"
	                            + exportarNotas(
	                                            reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString(),
	                                            reg.getCampos().get("NUMERO_FACTURA").toString(),
	                                            reg.getCampos().get("TIPO_NOTA").toString(),
	                                            reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString(),
	                                            reg.getCampos().get("CODIGOPRODUCTO").toString(),
	
	                                            reg.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString(),
	
	                                            reg.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString(),
	                                            reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString(),
	
	                                            url,
	                                            reg.getCampos().get(GeneralParameterEnum.CODIGO_NOTA.getName()).toString(),
	                                            reg.getCampos().get("TIPO_CPTE_AFECT").toString(),
	                                            reg.getCampos().get("FECHA_EXPEDICION").toString());
                        }else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
                        	log = log + "|---------------     ENVIO NOTAS INVOWAY      ---------------|";
                        	log = log + "\n" + exportarNotasExterno(log, reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString(),
                                    reg.getCampos().get("NUMERO_FACTURA").toString(),
                                    reg.getCampos().get("TIPO_NOTA").toString(),
                                    reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString(),
                                    reg.getCampos().get("CODIGOPRODUCTO").toString(),

                                    reg.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString(),

                                    reg.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString(),
                                    reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString(),

                                    reg.getCampos().get(GeneralParameterEnum.CODIGO_NOTA.getName()).toString());
                        }
                    }

                }

            }
            c.add(Calendar.DATE, -1);
            fechaFin = c.getTime();

            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarNotas.txt");

            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException | IOException | SysmanException | JRException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    /*
     * se crea metodo que permite la comunicacion con invoway para el envio de las notas
     */
    private String exportarNotasExterno(String log, String comprobante, String numeroFactura, String tipoNota, String fecha,
            String codigoProducto, String descripcion, String valor, String fechaIExpedicion, String codigoNota) {
    	
    	try
        {	
    		ApiInvoway apiInvoway = new ApiInvoway();
	    	
	    	 // se consulta los parametros para traer el usuario y la contraseña del ws invoway
	        String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		FrmFactLoteControladorEnum.USUARIO_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");
	        
	        String pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		FrmFactLoteControladorEnum.CLAVE_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");
	        
	        // se consulta la url configurada en el parametro
			String url = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					FrmFactLoteControladorEnum.URL_SERVICIO_SOAP.getValue(), "69", new Date(), false),"");
			if(url == null || url.equals("NO")) {
				JsfUtil.agregarMensajeError("Por favor configure la url para el envio.");
	            return "Por favor configure la url para el envio.";
			}
			
	        int idFactura;
	        // 91 nota credito y 92 nota debito
	        String subTipoNNota = "CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC")) ? "91" : "92";
	        
	        String tipoFinNota = "CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC")) ? "NC" : "ND";
	        
	        ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();
	
	        Map<String, Object> param = new TreeMap<>();
	
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	
	        param.put(GeneralParameterEnum.FACTURA.getName(), numeroFactura);
        
        
            Registro rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmFactLoteControladorUrlEnum.URL39116.getValue()).getUrl(),
                            param));

            if (rs != null)
            {
               Documento documentoNota = new Documento();
               documentoNota.setNumeroDocumento(SysmanFunciones
						.nvl(tipoFinNota, "")
						.toString()+comprobante);
               documentoNota.setTipoDocumento(tipoFinNota);
               documentoNota.setSubtipoDocumento(subTipoNNota);
               documentoNota.setTipoOperacion("CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC")) ? "20" : "30");
               
               documentoNota.setFechaDocumento(SysmanFunciones
						.nvl(rs.getCampos().get("DATE_CREATED"), "")
								.toString());
               
               documentoNota.setDivisa(SysmanFunciones
						.nvl(rs.getCampos().get("TIPO_MONEDA"), "")
						.toString().equals("602")?"COP":"");
               documentoNota.setDireccionFactura(SysmanFunciones
						.nvl(rs.getCampos().get("DIRECCIONCOMPANIA"), "")
						.toString());
               documentoNota.setDistritoFactura(SysmanFunciones
						.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "")
						.toString());
               documentoNota.setCiudadFactura(SysmanFunciones
						.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "")
						.toString()+SysmanFunciones
						.nvl(rs.getCampos().get("CIUDADCOMPANIA"), "")
						.toString());
               documentoNota.setPaisFactura("CO");
               //datos de proveedor
		       Proveedor p = new Proveedor(); 
		       if(!SysmanFunciones
	                  .nvl(rs.getCampos().get("NITCOMPANIA"), "").equals("") && SysmanFunciones
	                  .nvl(rs.getCampos().get("NITCOMPANIA"), "").toString().contains("-")) {
		    	   p.setIdProveedor(SysmanFunciones
	                  .nvl(rs.getCampos().get("NITCOMPANIA"), "").toString());
		       }else {
		    	   p.setIdProveedor(nitCompania+"-"+SysmanFunciones
	                  .nvl(rs.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString());
		       }
		       documentoNota.setProveedor(p);
		       // fin datos proveedor
		       String tipoPersona = SysmanFunciones
                       .nvl(rs.getCampos().get("NATURALEZATERCERO"), "")
                       .toString();
		       // se crean datos del cliente
		       Cliente c = new Cliente();
		       c.setTipoPersonaCliente(tipoPersona.equals("N")?"1":"2");
		       c.setDireccionCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("DIRECCIONTERCERO"), "")
                   .toString());
		       c.setCodigoPostalCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("CODIGOPOSTALTERCERO"), "")
                   .toString());
		       c.setTelefonoCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("TELEFONOS"), "")
                   .toString());
		       c.setEmailCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("EMAILTERCERO"), "")
                   .toString());
		       c.setPaisCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("PAISTERCERO"), "")
                   .toString());
		       c.setIdCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("NUMTERCERO"), "").toString()+"-"+SysmanFunciones
                   .nvl(rs.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());
		       c.setTipoDocumentoIdCliente("31");
		       c.setDistritoCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("DEPTOTERCERO"), "")
                   .toString());
		       c.setCiudadCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("DEPTOTERCERO"), "")
                   .toString() + SysmanFunciones 
                   .nvl(rs.getCampos().get("CIUDADTERCERO"), "")
                   .toString());
		       c.setPaisCliente("CO");
		       c.setRazonSocialCliente(SysmanFunciones
                   .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                   .toString());
		       c.setNombreCliente(tipoPersona.equals("N")?SysmanFunciones
                   .nvl(rs.getCampos().get("NOMBRETERCERO"), "")
                   .toString():SysmanFunciones
                   .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                   .toString());
		       c.setApellido1Cliente(tipoPersona.equals("N")?SysmanFunciones
                   .nvl(rs.getCampos().get("APELLIDO1CLIENTE"), "")
                   .toString():SysmanFunciones
                   .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                   .toString());
		       c.setApellido2Cliente(tipoPersona.equals("N")?SysmanFunciones
                   .nvl(rs.getCampos().get("APELLIDO2CLIENTE"), "")
                   .toString():SysmanFunciones
                   .nvl(rs.getCampos().get("RAZONSOCIAL"), "")
                   .toString());
		       c.setRegimenCliente("49");
		       // Iteracion sobre obligaciones fisacles del tercero

	            Map<String, Object> param2 = new TreeMap<>();

	            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	            param2.put(GeneralParameterEnum.NIT.getName(), SysmanFunciones
	                    .nvl(rs.getCampos().get("NUMTERCERO"), "").toString());

	            List<Registro> listaObligaciones = RegistroConverter
	                            .toListRegistro(
	                                            requestManager.getList(
	                                                            UrlServiceUtil
	                                                                            .getInstance()
	                                                                            .getUrlServiceByUrlByEnumID(
	                                                                            		FrmFactLoteControladorUrlEnum.URL2054
	                                                                                                            .getValue())
	                                                                            .getUrl(),
	                                                            param2));
	            if (!listaObligaciones.isEmpty())
	            {
	            	for (Registro reg : listaObligaciones)
	                {	            
	            		c.setResponsabilidadesRutCliente(reg.getCampos().get("CODIGO").toString());
	                }
	            }
		        
		       documentoNota.setCliente(c);
		       // fin cliente
		       // factura de referencia
		       // se onteiene el aï¿½os apartir de la fecha de la factura
		       String anio = String.valueOf(SysmanFunciones.ano(new Date()));
		       List<InfoEstadosFactura> respuestaConsulta = apiInvoway.consultarFactura(url, SysmanFunciones
	                   .nvl(rs.getCampos().get("PREFIJO"), "")
	                   .toString()+numeroFactura, anio, "FA" , nitCompania, pass, user);
		       if(respuestaConsulta.isEmpty()) {
		    	   return idioma.getString("MSM_ERROR_FACTURA_NO_ENVIADA_PREVIAMENTE")+" Nro: "+SysmanFunciones
		                   .nvl(rs.getCampos().get("PREFIJO"), "")
		                   .toString()+numeroFactura;
		       }
		       DocumentosReferenciados documentosReferenciados = new DocumentosReferenciados();
		       DocumentoReferenciado facturaAfectada = new DocumentoReferenciado();
		       facturaAfectada.setFechaDocumentoRef((SysmanFunciones
						.nvl(rs.getCampos().get("FECHAFACTURA"), "")
						.toString()));
		       facturaAfectada.setNumDocumentoRef(SysmanFunciones
	                   .nvl(rs.getCampos().get("PREFIJO"), "")
	                   .toString()+numeroFactura);
		       facturaAfectada.setUuidDocumentoRef(respuestaConsulta.get(0).getUUID());
		       documentosReferenciados.getDocumentoReferenciado().add(facturaAfectada);
		       documentoNota.setDocumentosReferenciados(documentosReferenciados);
		       
		       
		       //items o lineas
		       Lineas lineas = new Lineas();
		       int cont = 1;
               double sumProdPorFactura = 0;
               
        	   documentoNota.setMotivoRect(codigoNota);
		       
		       List<Registro> listaDetalleComprobantes = new ArrayList<>();
		       param = new TreeMap<>();
               SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
               param.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);
               param.put("FECHAINICIAL", formatFecha.format(fechaInicio));
               param.put("FECHAFINAL", formatFecha.format(fechaFin));
               Impuestos impuestosGnrl = new Impuestos();
               Retenciones retencionesGnrl = new Retenciones();
               BigDecimal totalRetenciones = new BigDecimal("0");
               BigDecimal totalImpuestos = new BigDecimal("0");
               BigDecimal totalBaseImpuestos = new BigDecimal("0");
               BigDecimal totalDescuentos = new BigDecimal("0");
               if(codigoNota.equals("1") || codigoNota.equals("3")) {
            	   	 listaDetalleComprobantes = RegistroConverter
	                          .toListRegistro(
	                                          requestManager.getList(
	                                                          UrlServiceUtil.getInstance()
	                                                                          .getUrlServiceByUrlByEnumID(
	                                                                                          FrmFactLoteControladorUrlEnum.URL1848009
	                                                                                                          .getValue())
	                                                                          .getUrl(),
	                                                          param));
	              	
            	   	Linea linea = new Linea();
            	   	cont = 1;
	          		for(Registro rs4: listaDetalleComprobantes) {
	          			
	          			linea.setNumLinea(cont);
	          			linea.setIdEstandarReferencia("001");
	          			linea.setDescripcionItem(descripcion);
	          			linea.setUnidadMedida("NIU");
	          			linea.setUnidadesLinea(new BigDecimal("1"));
	          			linea.setPorcDescuentoLinea(new BigDecimal("0").setScale(2));
	          			linea.setDescuentoLinea(new BigDecimal("0").setScale(2));
	     			   
	          			linea.setTotalLinea(new BigDecimal(valor));
				        // impuestsos generales de la factura
	          			Impuesto impuestoGnrl = new Impuesto();
	                	// se settean dentro del array impuestos en el detalle   
	              		String valorTemp =  SysmanFunciones.nvl(rs4.getCampos().get(GeneralParameterEnum.VALOR.getName()), "0").toString();
		                    
	              		if(valorTemp.contains(","))
		                    	valorTemp = valorTemp.replace(",", ".");
		                    
	              		if(rs4.getCampos().get(GeneralParameterEnum.CLASECUENTA.getName()).toString().equals("A")) {
	              			impuestoGnrl.setCodImpuesto("01");
	              			impuestoGnrl.setPorcImpuesto(new BigDecimal("19").setScale(2));
			                
		                    impuestoGnrl.setValorImpuesto(new BigDecimal(SysmanFunciones.nvlStr(valorTemp, "0")));
			               
	          			}else {
	          				impuestoGnrl.setCodImpuesto("01");
	          				impuestoGnrl.setValorImpuesto(new BigDecimal(SysmanFunciones.nvlStr(valorTemp, "0")));
	          			}
	              		impuestosGnrl.getImpuesto().add(impuestoGnrl);
	              		totalImpuestos = totalImpuestos.add(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get(GeneralParameterEnum.VALOR.getName()), "0").toString()));
	              		lineas.getLinea().add(linea);
	              		cont++;
	              	}
	            }else {
	                /**
	                 * @autor Luis Jacobo Diaz Se setea los valores de los parametros para el detalle de la nota esto como correccion del desarrollo del ticket 7701326, correccion que se basa en agregar
	                 * los valores correctos al detalle de la nota a enviar.
	                 */
	                String fechaFExpedicion;
	                Date fechaIExpedicionC;
	                Date fechaFExpedicionC;
	                Map<String, Object> param4 = new TreeMap<>();
	
	                fechaFExpedicion = fechaIExpedicion;
	                fechaIExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
	                Calendar clndr = Calendar.getInstance();
	                clndr.setTime(fechaIExpedicionC);
	                clndr.add(Calendar.DATE, -1);
	                fechaIExpedicionC = clndr.getTime();
	
	                fechaFExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
	                Calendar c1 = Calendar.getInstance();
	                c1.setTime(fechaFExpedicionC);
	                fechaFExpedicionC = c1.getTime();
	                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	                param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	                param4.put("FECHAINICIAL", formatFecha.format(fechaIExpedicionC));
	                param4.put("FECHAFINAL", formatFecha.format(fechaFExpedicionC));
	                param4.put("NUMEROFACTURA", numeroFactura);
	                param4.put("TIPOCOBRO", tipoCobro);
	                
	                // IMPUESTOS GENERALES NOTA DESDE FACTURA
	                Registro rs4 = RegistroConverter
	                                .toRegistro(
	                                                requestManager.get(
	                                                                UrlServiceUtil.getInstance()
	                                                                                .getUrlServiceByUrlByEnumID(
	                                                                                                FrmFactLoteControladorUrlEnum.URL7452
	                                                                                                                .getValue())
	                                                                                .getUrl(),
	                                                                param4));
	                // logica en el caso que el impuesto sea IVA.
	                if (!rs4.getCampos().get("BASEGRAVABLEIVA").toString().equals("0") && !rs4.getCampos().get("IVA").toString().equals("0")){
	                	// se settean dentro del array impuestos en el detalle
	                    Impuesto paramItemImpuestos = new Impuesto();
	                    paramItemImpuestos.setCodImpuesto("01");
	                    paramItemImpuestos.setBaseImpuesto(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()).setScale(2));
	                    paramItemImpuestos.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVAX"), 0).toString()));
	                    paramItemImpuestos.setValorImpuesto(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()).setScale(2));
	                    impuestosGnrl.getImpuesto().add(paramItemImpuestos);
	                    totalImpuestos = totalImpuestos.add(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()).setScale(2));
	                    totalBaseImpuestos = totalBaseImpuestos.add(paramItemImpuestos.getBaseImpuesto());
	                }
	                // logica en el caso que el impuesto sea RETE-IVA.
	                if (!rs4.getCampos().get("BASEGRAVABLERETEIVA").toString().equals("0") && !rs4.getCampos().get("RETE_IVA").toString().equals("0")){
	                	// se settean dentro del array impuestos en el detalle
	                    Retencion paramItemImpuestos = new Retencion();
	                    paramItemImpuestos.setCodRetencion("05");
	                    paramItemImpuestos.setBaseRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), 0).toString()).setScale(2));
	                    paramItemImpuestos.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEIVADX"), 0).toString()));
	                    paramItemImpuestos.setValorRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("RETE_IVA"), "0").toString()).setScale(2));
	                    retencionesGnrl.getRetencion().add(paramItemImpuestos);
	                    totalRetenciones = totalRetenciones.add(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("RETE_IVA"), "0").toString()).setScale(2));
	                }
	                // VALIDAR LOS DESCUENTOS SI SON PARCIALES O TOTALES
	                BigDecimal desct = new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0).toString());
	                BigDecimal desctFact = new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0).toString());
	                if(desctFact.compareTo(new BigDecimal("0")) > 0) {
	                	totalDescuentos = new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0).toString());
	                }else if(desct.compareTo(new BigDecimal("0")) > 0) {
	                	totalDescuentos = new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0).toString());
	                }
	                // logica en el caso que el impuesto sea RETEFUENTE.
	                if (!rs4.getCampos().get("BASEGRAVABLERETE").toString().equals("0") && !rs4.getCampos().get("RETE").toString().equals("0")){
	                	// se settean dentro del array impuestos en el detalle
	                	Retencion paramItemImpuestos = new Retencion();
	                    paramItemImpuestos.setCodRetencion("06");
	                    paramItemImpuestos.setBaseRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), 0).toString()).setScale(2));
	                    paramItemImpuestos.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEFUENTEX"), 0).toString()));
	                    paramItemImpuestos.setValorRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("RETE"), "0").toString()).setScale(2));
	                    retencionesGnrl.getRetencion().add(paramItemImpuestos);
	                    totalRetenciones = totalRetenciones.add(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("RETE"), "0").toString()).setScale(2));
	                }
                	// logica en el caso que el impuesto sea INC.
	                if (!rs4.getCampos().get("BASEGRAVABLEINC").toString().equals("0") && !rs4.getCampos().get("INC").toString().equals("0")){
	                	// se settean dentro del array impuestos en el detalle
	                	Retencion paramItemImpuestos = new Retencion();
	                    paramItemImpuestos.setCodRetencion("04");
	                    paramItemImpuestos.setBaseRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEINC"), 0).toString()).setScale(2));
	                    paramItemImpuestos.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEFUENTEX"), 0).toString()));
	                    paramItemImpuestos.setValorRetencion(new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("INC"), "0").toString()).setScale(2));
	                    retencionesGnrl.getRetencion().add(paramItemImpuestos);
	                }
	               
        
                    // CREACION DE LINEAS DEBEN SER UNA POR CADA CONCEPTO DE LA FACTURA
	                
	                List<Registro> listaFactSysman = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil
                                                                            .getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                            		FrmFactLoteControladorUrlEnum.URL661064
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param4));

		            if (!listaFactSysman.isEmpty())
		            {
		            	cont = 1;
		                for (Registro reg : listaFactSysman)
		                {
			                   Linea linea = new Linea();
			     		       linea.setNumLinea(cont);
			     		       linea.setIdEstandarReferencia("001");
			     		       linea.setDescripcionItem(descripcion);
			     		       linea.setUnidadMedida("NIU");
			     		       linea.setUnidadesLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("CANTIDAD").toString(), "0")));
			     		       linea.setPorcDescuentoLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("PORCENTAJEDESCUENTO").toString(),"0")).setScale(2));
			     			   linea.setDescuentoLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(),"0")).setScale(2));
			     			   linea.setPrecioUnidad(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("VALORUNITARIO").toString(), "0")).setScale(2).subtract(linea.getDescuentoLinea()));
			     		       linea.setSubtotalLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("SUBTOTAL").toString(), "0")).setScale(2).subtract(linea.getDescuentoLinea()));
			     		       
			     			   linea.setTotalLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("SUBTOTAL").toString(),"0")).setScale(2).subtract(linea.getDescuentoLinea()));
			     			   // se suman los totales de los producctos
			    		       sumProdPorFactura = sumProdPorFactura + linea.getTotalLinea().doubleValue();
			     		       
			     		       // se valida el iva
			     		       if(new BigDecimal(
			     		    		   SysmanFunciones.nvl(
			     		    				   reg.getCampos().get("VALORIMPUESTO").toString(), "0"))
			     		    		   .compareTo(BigDecimal.ZERO) > 0) {
			     		    	   linea.setCodImpuestoLinea("01");
			     		    	   linea.setValorImpuestoLinea(new BigDecimal(
			     		    		   SysmanFunciones.nvl(
			     		    				   reg.getCampos().get("VALORIMPUESTO").toString(), "0")));
			     		    	   linea.setPorcImpuestoLinea(new BigDecimal(
			     		    		   SysmanFunciones.nvl(
			     		    				   reg.getCampos().get("PORCENTAJEIVAX").toString(), "0")));
			     		       }
			     		       // 	se agrega la liena a la lista de lineas
			                   lineas.getLinea().add(linea);
			     		       cont++;
			                }
		            }
	                documentoNota.setMotivoRect(codigoNota);
	                
	            }
                
                documentoNota.setLineas(lineas);
                    
		        // Se agregan los datos totales
		        DatosTotales totalesFact = new DatosTotales();
		        
		        totalesFact.setSubtotal(new BigDecimal(sumProdPorFactura).setScale(2));
		        totalesFact.setTotalBase(new BigDecimal(sumProdPorFactura).setScale(2));

		        totalesFact.setTotalImpuestos(totalImpuestos.setScale(2));
		        totalesFact.setTotalGastos(new BigDecimal(0).setScale(2));
		        totalesFact.setTotalRetenciones(totalRetenciones.setScale(2));		        
		        // total factura
		        BigDecimal totalFactFin = totalesFact.getTotalBase();
		        totalFactFin = totalFactFin.add(totalImpuestos);
		        totalesFact.setDescuentoFinal(totalDescuentos.setScale(2));
		        if(totalDescuentos.compareTo(BigDecimal.ZERO) > 0) {
			        BigDecimal porcentajeDscto = totalDescuentos
			        	    .multiply(new BigDecimal("100"))
			        	    .divide(totalFactFin, 2, RoundingMode.HALF_UP);
			        totalesFact.setPorcDescuentoFinal(porcentajeDscto);
			        totalesFact.setTotalDocumento(totalFactFin.setScale(2));
		        }
			    totalesFact.setAPagar(totalFactFin.subtract(totalDescuentos).setScale(2));
		        if(!impuestosGnrl.getImpuesto().isEmpty())
		        	documentoNota.setImpuestos(impuestosGnrl);
		        if(!retencionesGnrl.getRetencion().isEmpty())
		        	documentoNota.setRetenciones(retencionesGnrl);
		        
		        documentoNota.setDatosTotales(totalesFact);
		        
		        CondicionPago formaPago = new CondicionPago();
		        formaPago.setMedioPago(SysmanFunciones
                       .nvl(rs.getCampos().get("MEDIOPAGO"), "")
                       .toString());
		        formaPago.setFormaPago(SysmanFunciones
                       .nvl(rs.getCampos().get("TIPO_PAGO"), "")
                       .toString());
		        formaPago.setFechaPago(SysmanFunciones
                        .nvl(rs.getCampos().get("FECHAFACTURA"), "")
                        .toString());
		        CondicionesPago condicionesFormaPago = new CondicionesPago();
		        condicionesFormaPago.getCondicionPago().add(formaPago);
		        documentoNota.setCondicionesPago(condicionesFormaPago);
		        
		        // ESTAS LINEAS SE DEJAN COMENTADAS PUES SON PARA USO DE DESARROLLO AL MOMENTO DE GENERAR EL XML PREVIO AL ENVIO.
//		        JAXBContext jaxbContext = JAXBContext.newInstance(Documento.class);
//	            Marshaller marshaller = jaxbContext.createMarshaller();
//	            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//		        		        
//	            marshaller.marshal(documentoNota, System.out);
	            
		        String respuesta = apiInvoway.postEnvioFactura(url, documentoNota, pass, user); 
	            
	            log = log + "\n" + respuesta + " Nota Nro : " + documentoNota.getNumeroDocumento();
            }
        }
        catch (SystemException | ParseException | IOException | SysmanException e)
        {
            log = log + " " + e.getMessage();
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return log;
    }
    private String exportarNotas(String comprobante, String numeroFactura, String tipoNota, String fecha,
        String codigoProducto, String descripcion, String valor, String fechaIExpedicion, String url, String codigoNota, String tipoCpteAfect, String fechaExpFactura)
    {

        String log = "";

        int idFactura = 0;
        

        String tipoNNota = ("CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC")) || "CNF".equals(SysmanFunciones.nvl(tipoNota, "CNC"))) ? "02" : "03";

        //mrosero CC_619
        String tipoOperacion;
        if("CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC"))) {
        	tipoOperacion = "20";
        }else if ("CND".equals(tipoNota)) {
        	tipoOperacion = "30";
        }else {
        	tipoOperacion = "22";
        }
        //mrosero CC_619
        ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.FACTURA.getName(), numeroFactura);
        
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        param.put("FECHAINICIAL", formatFecha.format(fechaInicio));
        
        param.put("FECHAFINAL", formatFecha.format(fechaFin));
        
        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCpteAfect);
        try
        {
            Registro rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmFactLoteControladorUrlEnum.URL2154.getValue()).getUrl(),
                            param));

            if (rs != null)
            {

                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.CODIGO.getName(),
                                rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

                Registro rs2 = RegistroConverter
                                .toRegistro(
                                                requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL9512
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param2));

                paramFactura.setCreatedBy(rs2.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());

                paramFactura.setNumerocontribuyente(nitCompania);
                
                String respuesta;
                APIFrida api = new APIFrida();                
              //MROSERO CC618 11/04/2025
				if (!tipoOperacion.equals("22")) {
					respuesta = api.cargarEnvioFacatura(nitCompania, numeroFactura,
							SysmanFunciones.nvl(rs.getCampos().get("PREFIJO"), "").toString(), url);

					Gson gson = new Gson();
					ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(respuesta,
							ParametrosEnvioFacturaFiltros.class);

					idFactura = respuestaApi.getCuerpo().getId();
				} 
                respuesta="";

                ParametroCuerpoEnvioFactura paramCuerpo = new ParametroCuerpoEnvioFactura();

                paramCuerpo.setTipoDeFactura(tipoNNota);
                paramCuerpo.setIdFactura(idFactura);
                paramCuerpo.setNumTercero(rs.getCampos().get("NUMTERCERO").toString());
                paramCuerpo.setFechafactura(fecha);

                paramCuerpo.setFechaVencimiento(fecha);

                paramCuerpo.setNumerofactura(Integer.parseInt(comprobante));

                paramCuerpo.setTelefonoCliente(rs.getCampos().get("TELEFONOS").toString());

                paramCuerpo.setTipoPago(rs.getCampos().get("TIPO_PAGO").toString());

                paramCuerpo.setObservacionesFactura("");

                paramCuerpo.setMedioPago(rs.getCampos().get("MEDIOPAGO").toString());

                paramCuerpo.setTipoMoneda(rs.getCampos().get("TIPO_MONEDA").toString());

                paramCuerpo.setPrefijo(("CNC".equals(SysmanFunciones.nvl(tipoNota, "CNC")) || "CNF".equals(SysmanFunciones.nvl(tipoNota, "CNC"))) ? "NC" : "ND");

              //mrosero CC_619
                paramCuerpo.setTipoOperacion(tipoOperacion);
                
                paramCuerpo.setPorcentajeIva(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));

                ParametrosItems paramItems = new ParametrosItems();

                paramItems.setCodigoproducto(codigoProducto);

                paramItems.setCantidad(1);

                paramItems.setDescripcionproducto(descripcion);

                paramItems.setValorunitario(Double.parseDouble(valor));

                paramItems.setTipoDescuento("05");

                paramItems.setDescuentoItem("0");

                paramItems.setTotalitem(Double.parseDouble(valor));
                
                // se traslada su declaracion
                List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();
                List<ParametrosImpuestos> listaParamImpuestosGnrl = new ArrayList<>();
                ParametrosImpuestos paramImpuesGnrl = new ParametrosImpuestos();
                ParametrosItems paramItemsSinImpesto = new ParametrosItems();
                ParametrosImpuestos impuestoReteIca = new ParametrosImpuestos();
                ParametrosImpuestos impuestoReteFuente = new ParametrosImpuestos();
                ParametrosImpuestos impuestoReteIva = new ParametrosImpuestos();
                List<ParametrosItems> listaParamItems = new ArrayList<>();
                List<Registro> listaDetalleComprobantes = new ArrayList<>();

                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                param.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);
                param.put("FECHAINICIAL", formatFecha.format(fechaInicio));
                param.put("FECHAFINAL", formatFecha.format(fechaFin));
                String valorTemp = "0";
                String valorTempIva = "0";
                /*
                 * ticket 7741625 se toma el valor de la nota de acuerdo al codigo de la nota, para el tipo 1 parcial, se toma el valor del comprobante no de la factura
                 */ 
                if(codigoNota.equals("1") || codigoNota.equals("3")) {
                	 param.remove("FACTURA");
                	 param.remove("COMPANIA");
                	 listaDetalleComprobantes = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmFactLoteControladorUrlEnum.URL1848009
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
                	Double valorBaseImpuestos = new Double("0");
                	Double valorIvaImpuesto = new Double("0");
                	Double porcentajeIva= new Double("0");
                	ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
                	for(Registro rs4: listaDetalleComprobantes) {
		                	// se settean dentro del array impuestos en el detalle   
                		valorTemp =  SysmanFunciones.nvl(rs4.getCampos().get(GeneralParameterEnum.VALOR.getName()), "0").toString();
                		valorTempIva = SysmanFunciones.nvl(rs4.getCampos().get("VALOR_IVA"), "0").toString();
	                    
                		if(valorTemp.contains(","))
	                    	valorTemp = valorTemp.replace(",", ".");
                		
                		if(valorTempIva.contains(","))
                			valorTempIva = valorTempIva.replace(",", ".");
	                    
                		if(rs4.getCampos().get(GeneralParameterEnum.CLASECUENTA.getName()).toString().equals("A")) {
            				paramItemImpuestos.setTipo("01");
		                    paramItemImpuestos.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVA"), 0).toString()));
		                    
		                    valorIvaImpuesto = Double.parseDouble(SysmanFunciones.nvl(valorTempIva, "0").toString()); //JM MOD CC 2145 
		                    paramItemImpuestos.setValor(valorIvaImpuesto);
		                    
		                    porcentajeIva = Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVA"), 0).toString());
		                    paramItemImpuestos.setPorcentaje(porcentajeIva);
		                    
		                    valorBaseImpuestos = Double
                                    .parseDouble(SysmanFunciones.nvl(valorTemp, "0").toString());
            				paramItemImpuestos.setBase(valorBaseImpuestos);
		                    
            				
		               
            			}else {
            				paramItemImpuestos.setTipo("01");
            				valorBaseImpuestos = Double
                                    .parseDouble(SysmanFunciones.nvl(valorTemp, "0").toString());
            				paramItemImpuestos.setBase(valorBaseImpuestos);
            				
            				valorIvaImpuesto = Double.parseDouble(SysmanFunciones.nvl(valorTempIva, "0").toString());
		                    paramItemImpuestos.setValor(valorIvaImpuesto);
		                    
		                    porcentajeIva = Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVA"), 0).toString());
		                    paramItemImpuestos.setPorcentaje(porcentajeIva);

            			}
                		
                	}
                	
                	//mrosero CC1108 CND = 03, CNC Y CNF = 02
                	//mrosero CC_2154 se adiciona if para validar si el inpuesto es 0 no sea agregado al json
					/* if (valorBaseImpuestos>0 && valorIvaImpuesto > 0) {
						if (!valorTemp.equals("1") && !tipoNNota.equals("03")) {
							listaParamItemImpuestos.add(paramItemImpuestos);
						}

					} */ //comentado por JM CC 2145 abajo esta tambien y lo lleva 2 veces 
					// se settean los impuestos al array de impuestos de la factura en general

					paramImpuesGnrl.setBase(valorBaseImpuestos);
					paramImpuesGnrl.setTipo("01");
					paramImpuesGnrl.setValor(valorIvaImpuesto);
					paramImpuesGnrl.setPorcentaje(porcentajeIva);

					// mrosero CC1108
					if (valorBaseImpuestos>0 && valorIvaImpuesto > 0) {
						if (!valorTemp.equals("1") && (tipoNNota.equals("03") || tipoNNota.equals("01"))) {
							listaParamItemImpuestos.add(paramItemImpuestos);
						}          	
                    } 
		            paramCuerpo.setImpuestos(listaParamImpuestosGnrl);
		                    
		            paramCuerpo.setSubtotalfactura(valorBaseImpuestos);
		                
		            paramCuerpo.setValorfactura(valorBaseImpuestos + valorIvaImpuesto);
		                
	                paramCuerpo.setReteFuente(0);
	                paramCuerpo.setReteIva(0);
	                		
	                paramCuerpo.setTotalBaseGravableIca(0);
	                paramCuerpo.setTotalBaseGravableInc(0);
	                //mrosero CC1108
	                paramCuerpo.setTotalBaseGravableIva((valorTemp.equals("1") && tipoNNota.equals("03")) ? 0 : valorBaseImpuestos);
	                paramCuerpo.setTotalBaseGravableRete(0);
	                paramCuerpo.setTotalBaseGravableIca(0);
	                paramCuerpo.setTotalBaseGravableReteiva(0);
	                if(paramCuerpo.getValorIvaFactura() == 0 && 
	                		paramCuerpo.getValorIcaFactura() == 0 &&
	                				paramCuerpo.getValorIncFactura() == 0 &&
	                						paramCuerpo.getReteIva() == 0 &&
	                								paramCuerpo.getReteFuente() == 0 &&
	                										paramCuerpo.getReteIca() == 0) {
	                	paramCuerpo.setTotalBaseImponible(Double.parseDouble(
                                "0"));
                    }else {
                    	paramCuerpo.setTotalBaseImponible(Double.parseDouble(
                                    SysmanFunciones.nvl(valorBaseImpuestos, "0")
                                                    .toString()));
                    }
	
	                paramCuerpo.setValorIvaFactura(valorIvaImpuesto);
                	
//                	// se settean en el general del detalle.
                    paramItems.setValorunitario(valorBaseImpuestos);
                    paramItems.setTotalitem(valorBaseImpuestos + valorIvaImpuesto);
                    
                	// mrosero CC1108
    				if (valorBaseImpuestos>0 && valorIvaImpuesto > 0) {
    					if (!valorTemp.equals("1") && (tipoNNota.equals("03") || tipoNNota.equals("01"))) {
    						paramCuerpo.setTotalBaseImponible(valorBaseImpuestos); //JM CC 2145
    					}          	
                    } 
    				
                }else {
	                /**
	                 * @autor Luis Jacobo Diaz Se setea los valores de los parametros para el detalle de la nota esto como correccion del desarrollo del ticket 7701326, correccion que se basa en agregar
	                 * los valores correctos al detalle de la nota a enviar.
	                 */
	                String fechaFExpedicion;
	                Date fechaIExpedicionC;
	                Date fechaFExpedicionC;
	                Map<String, Object> param4 = new TreeMap<>();
	
	                fechaFExpedicion = fechaExpFactura;
	                fechaIExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
	                Calendar c = Calendar.getInstance();
	                c.setTime(fechaIExpedicionC);
	                c.add(Calendar.DATE, -1);
	                fechaIExpedicionC = c.getTime();
	
	                fechaFExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
	                Calendar c1 = Calendar.getInstance();
	                c1.setTime(fechaFExpedicionC);
	                fechaFExpedicionC = c1.getTime();
	                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	                param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	                param4.put("FECHAINICIAL", formatFecha.format(fechaIExpedicionC));
	                param4.put("FECHAFINAL", formatFecha.format(fechaFExpedicionC));
	                param4.put("NUMEROFACTURA", numeroFactura);
	                param4.put("TIPOCOBRO", tipoCobro);
	
	                Registro rs4 = RegistroConverter
	                                .toRegistro(
	                                                requestManager.get(
	                                                                UrlServiceUtil.getInstance()
	                                                                                .getUrlServiceByUrlByEnumID(
	                                                                                                FrmFactLoteControladorUrlEnum.URL7452
	                                                                                                                .getValue())
	                                                                                .getUrl(),
	                                                                param4));
	
	                
	                // logica en el caso que el impuesto sea IVA.
	                if (rs4.getCampos().get("IVA") != null && rs4.getCampos().get("BASEGRAVABLEIVA") != null && !rs4.getCampos().get("BASEGRAVABLEIVA").toString().equals("0") && !rs4.getCampos().get("IVA").toString().equals("0"))
	                {                    
	                    
	                	// se settean dentro del array impuestos en el detalle
	                    ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
	                    paramItemImpuestos.setTipo("01");
	                    paramItemImpuestos.setBase(Double
	                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
	                    paramItemImpuestos.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVAX"), 0).toString()));
	                    paramItemImpuestos.setValor(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
	                    listaParamItemImpuestos.add(paramItemImpuestos);
	                    paramCuerpo.setPorcentajeIva(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVAX"), 0).toString()));
	                    // se settean en el general del detalle.
	                    paramItems.setValorunitario(Double
	                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
	                    Double totalItemDetalle = Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString())
	                        + Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString());
	                	paramItems.setTotalitem(totalItemDetalle);
	                    
	                    // se settean los impuestos al array de impuestos de la factura en general
	                	
	                	//--VALO_IVA
	                    paramImpuesGnrl.setBase(Double
	                                    .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
	                    paramImpuesGnrl.setTipo("01");
	                    paramImpuesGnrl.setValor(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
	                    paramImpuesGnrl.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJEIVAX"), 0).toString()));
	                    listaParamImpuestosGnrl.add(paramImpuesGnrl);
	                    paramCuerpo.setImpuestos(listaParamImpuestosGnrl);
	                    
	                    //--VALO_ICA
	                    impuestoReteIca.setBase(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEICA"), "0").toString()));
	                    impuestoReteIca.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEICAX"), "0").toString()));
	                    impuestoReteIca.setTipo("03");
	                    impuestoReteIca.setValor(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("ICA"), "0").toString()));
	                    listaParamImpuestosGnrl.add(impuestoReteIca);
	                    
	                   //--VALOR-RETE-FUENTE
	                    impuestoReteFuente.setBase(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), "0").toString()));                 
	                    impuestoReteFuente.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEFUENTEX"), "0").toString()));
	                    impuestoReteFuente.setTipo("06");
	                    impuestoReteFuente.setValor(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("RETE"), "0").toString()));
	                    listaParamImpuestosGnrl.add(impuestoReteFuente);
	                    
	                    //--VALOR_RETEIVA
	                    impuestoReteIva.setBase(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), "0").toString()));
	                    impuestoReteIva.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("PORCENTAJERETEIVADX"), "0").toString()));
	                    impuestoReteIva.setTipo("05");
	                    impuestoReteIva.setValor(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("RETE_IVA"), "0").toString()));
	                    listaParamImpuestosGnrl.add(impuestoReteIva);

	                    paramCuerpo.setImpuestos(listaParamImpuestosGnrl);

	                    
	                    
	                    // inicio desarrollo solucion 7724470 ljdiaz (27/12/2022)
	                    // se agrega un segundo item que contiene el valor de la diferencia entre el valor final y el iva, para completar la nota
	                    double totalItemAjuste = Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString())
	                        - (paramImpuesGnrl.getBase() + paramImpuesGnrl.getValor());
	                    // 7735445 se modifica el valor de validación del ajuste de cero a 1 para evitar que se adicione en las notas
	                    // item de descuentos por diferencias menores o iguales a uno
	                    if (totalItemAjuste > 1)
	                    {
	                        // se foramtea el valor para que traiga solo dos decimales
	                        DecimalFormat df = new DecimalFormat("#");
	
	                        paramItemsSinImpesto.setCodigoproducto(codigoProducto);
	
	                        paramItemsSinImpesto.setCantidad(1);
	
	                        paramItemsSinImpesto.setDescripcionproducto(descripcion);
	                        
	                        paramItemsSinImpesto.setValorunitario(Double.parseDouble(df.format(totalItemAjuste)));
		
		                    paramItemsSinImpesto.setTotalitem(Double.parseDouble(df.format(totalItemAjuste)));
	                        
	                        paramItemsSinImpesto.setTipoDescuento("05");
	
	                        paramItemsSinImpesto.setDescuentoItem("0");
	
	                        List<ParametrosItemsImpuestos> listaTempSinImpuesto = new ArrayList<>();
	                        paramItemsSinImpesto.setImpuestos(listaTempSinImpuesto);
	
	                        listaParamItems.add(paramItemsSinImpesto);
	                        // fin desarrollo soluicion 7724470 ljdiaz (27/12/2022)
	                    }
	                }
	
	                // fin desarrollo solucion 7701326 ljdiaz
	                
	                
                            
                	/*
                     * gfigueredo Inicio Ajuste Ticket 7701326 ï¿½ 1000109226 Facturacion Tocancipa Se aï¿½ade consulta para traer los datos de subtotal, total e iva.
                     */
	                paramCuerpo.setSubtotalfactura(Double
	                                .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("SUBTOTALFACTURA"), "0").toString()));
	
	                
	                paramCuerpo.setValorfactura(
	                                Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString()));
                
	                paramCuerpo.setReteFuente(0);
	                paramCuerpo.setReteIva(0);
	                // paramCuerpo.setTotalBaseGravableIca(0);
	                // paramCuerpo.setTotalBaseGravableInc(0);
	                // paramCuerpo.setTotalBaseGravableIva(0);
	                // paramCuerpo.setTotalBaseGravableRete(0);
	                // paramCuerpo.setTotalBaseGravableReteica(0);
	                // paramCuerpo.setTotalBaseGravableReteiva(0);
	                // paramCuerpo.setTotalBaseImponible(0);
	                
	
	                paramCuerpo.setTotalBaseGravableIca(Double
	                                .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEICA"), "0").toString()));
	                paramCuerpo.setTotalBaseGravableInc(Double
	                                .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEINC"), "0").toString()));
	                paramCuerpo.setTotalBaseGravableIva(Double
	                                .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
	                paramCuerpo.setTotalBaseGravableRete(Double
	                                .parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), "0").toString()));
	                paramCuerpo.setTotalBaseGravableIca(Double.parseDouble(
	                                SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEICA"), "0").toString()));
	                paramCuerpo.setTotalBaseGravableReteiva(Double.parseDouble(
	                                SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), "0").toString()));
	                
	
	                paramCuerpo.setValorIvaFactura(
	                                Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
	                if(paramCuerpo.getValorIvaFactura() == 0 && 
	                		paramCuerpo.getValorIcaFactura() == 0 &&
	                				paramCuerpo.getValorIncFactura() == 0 &&
	                						paramCuerpo.getReteIva() == 0 &&
	                								paramCuerpo.getReteFuente() == 0 &&
	                										paramCuerpo.getReteIca() == 0) {
	                	paramCuerpo.setTotalBaseImponible(Double.parseDouble(
                                "0"));
                    }else {
                    	paramCuerpo.setTotalBaseImponible(paramCuerpo
                                    .getTotalBaseGravableIva() + paramCuerpo
                                    .getTotalBaseGravableIca() + paramCuerpo
                                    .getTotalBaseGravableInc());
                    }
                }
                //FIN 7741625
                ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
                paramItemImpuestos2.setTipo("06");

                paramItemImpuestos2.setBase(0);

                paramItemImpuestos2.setPorcentaje(0);

                paramItemImpuestos2.setValor(0);

                ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
                paramItemImpuestos3.setTipo("03");

                paramItemImpuestos3.setBase(0);

                paramItemImpuestos3.setPorcentaje(0);

                paramItemImpuestos3.setValor(0);

                ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
                paramItemImpuestos4.setTipo("05");

                paramItemImpuestos4.setBase(0);

                paramItemImpuestos4.setPorcentaje(0);

                paramItemImpuestos4.setValor(0);

                ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
                paramItemImpuestos5.setTipo("07");

                paramItemImpuestos5.setBase(0);

                paramItemImpuestos5.setPorcentaje(0);

                paramItemImpuestos5.setValor(0);

                ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
                paramItemImpuestos6.setTipo("02");

                paramItemImpuestos6.setBase(0);

                paramItemImpuestos6.setPorcentaje(0);

                paramItemImpuestos6.setValor(0);

                if (paramItemImpuestos2.getBase() != 0.0)
                {
                    listaParamItemImpuestos.add(paramItemImpuestos2);
                }
                if (paramItemImpuestos3.getBase() != 0.0)
                {
                    listaParamItemImpuestos.add(paramItemImpuestos3);
                }
                if (paramItemImpuestos4.getBase() != 0.0)
                {
                    listaParamItemImpuestos.add(paramItemImpuestos4);
                }
                if (paramItemImpuestos5.getBase() != 0.0)
                {
                    listaParamItemImpuestos.add(paramItemImpuestos5);
                }
                if (paramItemImpuestos6.getBase() != 0.0)
                {
                    listaParamItemImpuestos.add(paramItemImpuestos6);
                }

                paramItems.setImpuestos(listaParamItemImpuestos);

                listaParamItems.add(paramItems);

                paramCuerpo.setItems(listaParamItems);
                /*
                 * gfigueredo Fin Ajuste Ticket 7701326 — 1000109226 Facturacion Tocancipa.
                 */
                paramCuerpo.setValorIcaFactura(0);
                paramCuerpo.setNumeroConceptos(listaParamItems.size());
                paramCuerpo.setReteIca(0);
                paramCuerpo.setValorIncFactura(0);
                paramCuerpo.setDescuentoItems(0);
                
                paramCuerpo.setDescuentoFactura(0);
                paramCuerpo.setDescripcion(descripcion);

                List<ParametroCuerpoEnvioFactura> listaParamCuerpo = new ArrayList<>();

                listaParamCuerpo.add(paramCuerpo);

                paramFactura.setFacturas(listaParamCuerpo);

                APIFrida api2 = new APIFrida();

                Gson gson2 = new Gson();

                String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);
                if (json.toString().contains(FrmFactLoteControladorEnum.IMPUESTOIVA.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.IMPUESTOIVA.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.VALORIMPUESTOIVA.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.VALORIMPUESTOIVA.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.IMPUESTOICA.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.IMPUESTOICA.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.VALORIMPUESTOICA.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.VALORIMPUESTOICA.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.IMPUESTOIMPOCONSUMO.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.IMPUESTOIMPOCONSUMO.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue(), " ");
                }
                if (json.toString().contains(FrmFactLoteControladorEnum.BASEGRAVAVLEDETALLE.getValue()))
                {
                    json = json.toString().replace(FrmFactLoteControladorEnum.BASEGRAVAVLEDETALLE.getValue(), " ");
                }

                log = api2.postEnvioFactura(url, json);
//MROSERO CC618 11/04/2025
				if (!respuesta.equals("")) {
					RespuestaApi respuestaApi2 = gson2.fromJson(respuesta, RespuestaApi.class);
					if (respuestaApi2.getCodigo() != 0) {
						log = respuestaApi2.getMensaje().toString();
					}
				}

                return log;

            }

        }
        catch (SystemException | IOException | SysmanException | ParseException e)
        {
            log = log + " " + e.getMessage();
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return log;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable fechaInicio
     *
     * @return fechaInicio
     */
    public Date getFechaInicio()
    {
        return fechaInicio;
    }

    /**
     * Asigna la variable fechaInicio
     *
     * @param fechaInicio
     * Variable a asignar en fechaInicio
     */
    public void setFechaInicio(Date fechaInicio)
    {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Retorna la variable fechaFin
     *
     * @return fechaFin
     */
    public Date getFechaFin()
    {
        return fechaFin;
    }

    /**
     * Asigna la variable fechaFin
     *
     * @param fechaFin
     * Variable a asignar en fechaFin
     */
    public void setFechaFin(Date fechaFin)
    {
        this.fechaFin = fechaFin;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
