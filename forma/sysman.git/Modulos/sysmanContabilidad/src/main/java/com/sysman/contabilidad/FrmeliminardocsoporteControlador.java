/*-
 * FrmeliminardocsoporteControlador.java
 *
 * 1.0
 * 
 * 15/11/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.beanbase.BeanBaseContinuoNAcme;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.contabilidad.enums.FrmeliminardocsoporteControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmestadodocsoporteControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametroDeleteEnvioFactura;
import com.sysman.util.rest.RespuestaEnvioFactura;

import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModel;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/11/2022
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmeliminardocsoporteControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * 
	 */
	private String nitCompania;
	/**
	 * 
	 */
	private String usuario;
	/**
	 * 
	 */
	private String url;
	/**
	 * 
	 */
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * 
	 */
	private List<Registro> listaTiposDocSoporte;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmeliminardocsoporteControlador
	 */
	public FrmeliminardocsoporteControlador() {
		super();
		compania = SessionUtil.getCompania();
		if (SessionUtil.getCompaniaIngreso().getNit().toString().contains("-")) {
			nitCompania = SessionUtil.getCompaniaIngreso().getNit().toString().split("-")[0];
		} else {
			nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		}
        usuario = SessionUtil.getUser().getCodigo();
		try {
			// 2375
			numFormulario = GeneralCodigoFormaEnum.ELIMINARODOCSOPORTEDIAN.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.TEMP_BORRADO_FACTURAS;

        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
	}

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Eliminar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirEliminar(Registro reg, int indice) {
		// <CODIGO_DESARROLLADO>
		 String tipoFormato = "";

	        tipoFormato = "NC".equals(reg.getCampos().get("PREFIJO").toString())
	            ? "02"
	            : "ND".equals(reg.getCampos().get("PREFIJO").toString()) ? "03"
	                : "NA".equals(reg.getCampos().get("PREFIJO").toString()) ? "06":"05";

	        try {

	            Registro rs;

	            rs = RegistroConverter
	                            .toRegistro(requestManager.get(
	                                            UrlServiceUtil.getInstance()
	                                                            .getUrlServiceByUrlByEnumID(
	                                                            		FrmestadodocsoporteControladorUrlEnum.URL9457
	                                                                                            .getValue())
	                                                            .getUrl(),
	                                            null));

	            if (rs != null) {

	                File archivo = new File(
	                                rs.getCampos().get(
	                                                "RUTA_CERTIFICADO")
	                                                .toString());

	                String nombreCertificado = archivo
	                                .getName();

	                byte[] archivoBytes = Files
	                                .readAllBytes(archivo
	                                                .toPath());

	                String certificado = Base64.getEncoder()
	                                .encodeToString(archivoBytes);

	                String passCertificado = Base64.getEncoder()
	                                .encodeToString(rs
	                                                .getCampos()
	                                                .get("CONTRA_CERTIFICADO")
	                                                .toString()
	                                                .getBytes());

	                ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();

	                paramDelete.setTipoFormato(tipoFormato);
	                paramDelete.setNumFormato(
	                                reg.getCampos().get("NUM_FACTURA").toString());
	                paramDelete.setPrefijo(
	                                reg.getCampos().get("PREFIJO").toString());
	                paramDelete.setCertificado(certificado);
	                paramDelete.setNombreCertificado(
	                                nombreCertificado);
	                paramDelete.setPassCertificado(
	                                passCertificado);
	                paramDelete.setNumDocumentoContribuyente(
	                                nitCompania);

	                Gson gson2 = new Gson();
	                String json = gson2.toJson(paramDelete,
	                                ParametroDeleteEnvioFactura.class);

	                APIFrida apiFrida = new APIFrida();

	                apiFrida.deleteEnvioFactura(url, json);

	                JsfUtil.agregarMensajeInformativo(
	                                idioma.getString("MSM_REGISTRO_ELIMINADO"));

	                insertarDatos();

	            }

	        }
	        catch (SystemException | IOException | SysmanException e) {

	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
		// </CODIGO_DESARROLLADO>
	}

	
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
	/*
	 * 
	 */
	private void insertarDatos() {
        borrarDocumentos();

        try {
            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {

                String respuesta;
                APIFrida api = new APIFrida();

                respuesta = api.cargarEnvioFacatura(nitCompania, url);

                Gson gson = new Gson();
                RespuestaEnvioFactura respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaEnvioFactura.class);

                for (int i = 0; i < respuestaApi.getCuerpo().size(); i++) {

                    List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);
                    
                    for (Registro reg : listaTiposDocSoporte) {
                    	String prefijo = reg.getCampos().get("CODIGO").toString().equals("NPR") ? "NA":reg.getCampos().get("CODIGO").toString().equals("CNA")?"NA":"";
						if (reg.getCampos().get("CODIGO").toString().equals(datos.get(1).toString()) || prefijo.equals(datos.get(1).toString())) {
							insertarDocumentos(datos);
							break;
						}
					}  
                }
                reasignarOrigen();
            }

        }
        catch (SystemException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	/**
	 * 
	 */
	private void consultarTiposDocSoporte() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaTiposDocSoporte = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmestadodocsoporteControladorUrlEnum.URL1895017.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/*
	 * 
	 */
	private void borrarDocumentos() {
        Map<String, Object> params = new TreeMap<>();

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmeliminardocsoporteControladorUrlEnum.URL2564
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
	/*
	 * 
	 */
	 private void insertarDocumentos(List<Object> datos) {
		 	
	        Map<String, Object> params = new TreeMap<>();

	        String urlEnumId = FrmeliminardocsoporteControladorUrlEnum.URL4589.getValue();

	        String descEstado = "";

	        switch (datos.get(3).toString()) {
	        case "000":
	            descEstado = "PERSISTIDA";
	            break;
	        case "7200001":
	            descEstado = "RECIBIDA";
	            break;
	        case "7200003":
	            descEstado = "EN PROCESO DE VALIDACIÓN";
	            break;
	        case "7200004":
	            descEstado = "FALLIDA (Documento no cumple 1 o más validaciones de la DIAN)";
	            break;
	        default:
	            descEstado = "";
	            break;
	        }

	        try {

	            params.put("NUM_FACTURA", new DecimalFormat(
	                            "#.####################################")
	                                            .format(datos.get(0)));
	            params.put("PREFIJO", datos.get(1));
	            params.put("ID_FACTURA", new DecimalFormat(
	                            "#.####################################")
	                                            .format(datos.get(2)));
	            params.put("COD_ESTADO", datos.get(3));
	            params.put("DESCRIPCION_ESTADO", descEstado);

	            params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
	            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
	                            new Date());

	            UrlBean urlCreate = UrlServiceUtil.getInstance()
	                            .getUrlServiceByUrlByEnumID(urlEnumId);

	            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
	                            params);
	        }
	        catch (SystemException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }

	    }
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		consultarTiposDocSoporte();
		insertarDatos();
		// </CODIGO_DESARROLLADO>
	}
//<SET_GET_ATRIBUTOS>
//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>


	@Override
	public boolean insertarAntes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertarDespues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean actualizarAntes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean actualizarDespues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eliminarAntes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eliminarDespues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removerCombos() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reasignarOrigen() {
		// TODO Auto-generated method stub
		buscarUrls();
	}
}
