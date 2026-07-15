/*-
 * FrmEstNomInvowayControlador.java
 *
 * 1.0
 * 
 * 03/03/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.datatype.XMLGregorianCalendar;

import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmNominaDianControladorEnum;
import com.sysman.nomina.enums.FrmNominaDianControladorUrlEnum;
import com.sysman.nomina.enums.ResumentotalcuneControladorEnum;
import com.sysman.nomina.enums.ResumentotalcuneControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.ConsultaEstadosNominasResponse;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosNomina;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/03/2026
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmEstNomInvowayControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	private String tipoNomina;

	private Date fechaInicio;

	private Date fechaFin;
	
	private boolean bloqueoBotonesExportar;
	
	private boolean bloqueoFecha;

	private boolean bloqueoConsecutivo;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaNominaAjuste;
	
	private RegistroDataModelImpl listaNominaBase;
	
	private RegistroDataModelImpl listaNominaEliminacion;
	private String nitEmpleador;
	private String modulo;
	private String url;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String usuario;
	private String pagInicio;
	private String pagTamanio;
	private boolean ignorarPaginado;
	private String usuarioInvoway;
	private String passwordInvoway;
	private String ano;
	private String mes;
	
	private static final String USUARIO_NOM_ELECTRONICA_EXTERNA = "USUARIO NOMINA ELECTRONICA EXTERNA";
	private static final String CLAVE_NOM_ELECTRONICA_EXTERNA = "CONTRASEÑA NOMINA ELECTRONICA EXTERNA";
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmEstNomInvowayControlador
	 */
	public FrmEstNomInvowayControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitEmpleador = SessionUtil.getCompaniaIngreso().getNit();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();

		fechaFin = new Date();
		fechaInicio = new Date();
		ignorarPaginado = true;		
		listaNominaAjuste = new RegistroDataModelImpl();
		listaNominaBase = new RegistroDataModelImpl();
		listaNominaEliminacion = new RegistroDataModelImpl();
		try {
			numFormulario = 2571;
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
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaNominaAjuste();
		cargarListaNominaBase();
		cargarListaNominaEliminacion();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {

	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		 
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		abrirFormulario();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";
	}



//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaNominaAjuste
	 *
	 */
	public void cargarListaNominaAjuste() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902008.getValue());

		try {
			listaNominaAjuste = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaNominaBase
	 *
	 */
	public void cargarListaNominaBase() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902006.getValue());

		try {
			listaNominaBase = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
					false, CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaNominaEliminacion
	 *
	 */
	public void cargarListaNominaEliminacion() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902010.getValue());

		try {
			listaNominaEliminacion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control TipoNomina
	 * 
	 * 
	 */
	public void cambiarTipoNomina() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNominaAjuste
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNominaAjuste(SelectEvent event) {
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNominaBase
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNominaBase(SelectEvent event) {
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNominaEliminacion
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNominaEliminacion(SelectEvent event) {
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoBase en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirConsultarEstadoBase() {
		// <CODIGO_DESARROLLADO>
		consultarEstadoDeNomina();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoAjuste en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirConsultarEstadoAjuste() {
		// <CODIGO_DESARROLLADO>
		consultarEstadoDeNomina();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoEliminacion en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirConsultarEstadoEliminacion() {
		// <CODIGO_DESARROLLADO>
		consultarEstadoDeNomina();
		// </CODIGO_DESARROLLADO>
	}
	
	
	private void consultarEstadoDeNomina() {

		borrarDatosConsultarNomina();
		String numero;
		String fecha;
		String nie045;

		for (Registro r : listaNominaBase.getSeleccionados()) {

			numero = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
			tipoNomina = r.getCampos().get(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue()).toString();
			fecha =  r.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString();
			nie045 = r.getCampos().get(GeneralParameterEnum.NUMERO_DOCUMENTO.getName()).toString();


			//T_NE_ACEPTADODIAN
			if (!"Aceptado por DIAN".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {

				if (consultarEstadoDocDian(numero,tipoNomina,fecha,nie045)) {

					String[] campos = { GeneralParameterEnum.NUMERO_DOCUMENTO.getName() };

					String[] valores = { nie045 };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_NOMINA_CONTROLADOR.getCodigo()),
							modulo, campos, valores);

				}

			} else {
				JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
						+ " no se ha legalizado frente a la DIAN");
			}

		}

	}
	
	private void borrarDatosConsultarNomina() {

		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1903001.getValue());

			requestManager.delete(urlDelete.getUrl(), null);


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private boolean consultarEstadoDocDian(String numero ,String codigoTipoNomina , String fechaGenNie008,String numeroDocumentoNie045) {
		return true;

	}
	
	

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Buscar en la vista
	 *
	 *
	 */
	public void oprimirBuscar() {
	archivoDescarga = null;
		pagInicio = "0";
		pagTamanio = "5";

	    try {
	        ano = SysmanFunciones.toString(SysmanFunciones.ano(fechaInicio));
	        mes = String.format("%02d", SysmanFunciones.mes(fechaInicio));

	        borrarDatosNominas();

	        Map<String, Object> paramListado = new HashMap<>();
	        paramListado.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(), compania);
	        paramListado.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(), ano);
	        paramListado.put(ResumentotalcuneControladorEnum.UN_MES.getValue(), mes);

	        List<Registro> rs = RegistroConverter.toListRegistro(
	                requestManager.getList(
	                        UrlServiceUtil.getInstance()
	                                .getUrlServiceByUrlByEnumID(
	                                        ResumentotalcuneControladorUrlEnum.URL1881008.getValue())
	                                .getUrl(),
	                        paramListado
	                )
	        );

	        if (rs == null || rs.isEmpty()) {
	            JsfUtil.agregarMensajeError("No hay datos");
	            return;
	        }

	        String tipoConsulta = "LOTE".equals(tipoNomina) ? "TODAS" : tipoNomina;

	        for (Registro rs1 : rs) {

	            String numeroNomina = SysmanFunciones.toString(
	                    SysmanFunciones.nvl(rs1.getCampos().get("NUM_NOMINAS"), "")
	            );

	            consultarNominas(tipoConsulta, numeroNomina);
	        }

	    } catch (SystemException e) {
	        logger.error("Error en oprimirBuscar", e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}
	
	
	/**
	 * Consulta las nóminas electrónicas en Invoway según el tipo y número de nómina
	 * suministrados.
	 * <p>
	 * El método realiza las siguientes operaciones:
	 * <ul>
	 *     <li>Consulta la URL de conexión configurada para Invoway.</li>
	 *     <li>Obtiene las credenciales de autenticación del servicio externo.</li>
	 *     <li>Determina el tipo de nómina a consultar según el parámetro recibido.</li>
	 *     <li>Consume el servicio de consulta de estados de nómina electrónica.</li>
	 *     <li>Inserta las nóminas encontradas en la base de datos.</li>
	 *     <li>Recarga las listas de nómina base, ajuste y eliminación.</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * Los valores permitidos para el parámetro {@code tipoNomina} son:
	 * <ul>
	 *     <li>{@code TODAS}: consulta todas las nóminas.</li>
	 *     <li>{@code T_NE_BASE}: consulta nóminas tipo base.</li>
	 *     <li>{@code T_NE_AJUSTE_RE}: consulta nóminas de ajuste de reemplazo.</li>
	 *     <li>{@code T_NE_AJUSTE_EL}: consulta nóminas de ajuste de eliminación.</li>
	 * </ul>
	 * </p>
	 *
	 * @param tipoNomina tipo de nómina electrónica a consultar.
	 * @param numeroNomina número específico de nómina a consultar. Puede ser
	 *                     {@code null} para consultar todas las nóminas del período.
	 *
	 */
	private void consultarNominas(String tipoNomina, String numeroNomina) {

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL INVOWAY", "-1", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL INVOWAY");
			} else {


					usuarioInvoway = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
			        		USUARIO_NOM_ELECTRONICA_EXTERNA, "-1", new Date(), false),"");
			        
					passwordInvoway = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
			        		CLAVE_NOM_ELECTRONICA_EXTERNA, "-1", new Date(), false),"");

							ApiInvoway api = new ApiInvoway();
							
							String tipoNominaParametro = "";

							switch (tipoNomina) {
							    case "TODAS":
							    	tipoNominaParametro = null;
							        break;
							    case "T_NE_BASE":
							    	tipoNominaParametro = "N";
							        break;
							    case "T_NE_AJUSTE_RE":
							    	tipoNominaParametro = "R";
							        break;
							    case "T_NE_AJUSTE_EL":
							    	tipoNominaParametro = "E";
							    default:
							    	tipoNominaParametro = null;
							        break;
							}
								
							
							String nitEmisor = SessionUtil.getCompaniaIngreso().getNit();

							ConsultaEstadosNominasResponse resp =
							        api.consultarNomina(
							            url,
							            ano,
							            mes,
							            numeroNomina,
							            nitEmisor,
							            null,//nitTrabajador
							            tipoNominaParametro,
							            null,//trackId
							            passwordInvoway,
							            usuarioInvoway
							        );
							

							if(resp.getInfoEstadosNominas() == null || resp.getInfoEstadosNominas().isEmpty()) {

							    JsfUtil.agregarMensajeAlerta("No se encontraron datos");

							}else{

							    for (InfoEstadosNomina nomina : resp.getInfoEstadosNominas()) {

							        insertarNomina(nomina);

							    }

							}

				cargarListaNominaBase();
				cargarListaNominaAjuste();
				cargarListaNominaEliminacion();

			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	private void insertarNomina(InfoEstadosNomina nomina) {


		try {

			String urlEnumIdExiste = FrmNominaDianControladorUrlEnum.URL1902013.getValue();
			
			HashMap<String, Object> paramExiste = new HashMap<>();
			
			paramExiste.put(GeneralParameterEnum.NUMERO.getName(), nomina.getIdentificadorNomina().getNumero());
			
			Registro rsExiste = RegistroConverter.toRegistro(
                      requestManager.get(UrlServiceUtil.getInstance()
                                      .getUrlServiceByUrlByEnumID(urlEnumIdExiste)
                                      .getUrl(), paramExiste));
		
			XMLGregorianCalendar xmlFecha = nomina.getFechaEstadoDIAN();
			
			String fecha = xmlFecha.toGregorianCalendar()
					.toZonedDateTime()
					.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			
			String tipoNominaParametro = "";

			switch (nomina.getIdentificadorNomina().getTipoNomina()) {
			    case "N":
			    	tipoNominaParametro = "T_NE_BASE";
			        break;
			    case "R":
			    	tipoNominaParametro = "T_NE_AJUSTE_RE";
			        break;
			    default :
			    	tipoNominaParametro = "T_NE_BASE";
			    	break;
			    	
			}
			
			HashMap<String, Object> params = new HashMap<>();
			
			params.put(GeneralParameterEnum.NUMERO_DOCUMENTO.getName(), 0);
			params.put(GeneralParameterEnum.ESTADO.getName(), nomina.getEstadoDIAN());
			params.put(FrmNominaDianControladorEnum.CUNE.getValue(), nomina.getCUNE());
			params.put(GeneralParameterEnum.NUMERO.getName(), nomina.getIdentificadorNomina().getNumero());
			params.put(GeneralParameterEnum.FECHA.getName(), fecha);
			params.put(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue(), tipoNominaParametro);
			params.put(GeneralParameterEnum.OBSERVACIONES.getName(), nomina.getObservacionesEstadoDIAN());
			params.put(FrmNominaDianControladorEnum.CODIGO_TRABAJADOR.getValue(), nomina.getCodigoInternoTrabajador());
			
			if (rsExiste.getCampos().get("TOTAL").toString().equals("0")) {
				
				String urlEnumIdInsert = FrmNominaDianControladorUrlEnum.URL1902002.getValue();
				UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumIdInsert);
				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
				
			}else {
				 Parameter parameter = new Parameter();
	                parameter.setFields(params);
	                
	                String urlEnumIdUpdate = FrmNominaDianControladorUrlEnum.URL1902012.getValue();
	                UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumIdUpdate);

	                int respuesta = requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
				
			}
		} catch (SystemException   e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosEmitidos en la vista
	 *
	 *
	 */
	public void oprimirExportarDocumentosEmitidos() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosNoEmitidos en la vista
	 *
	 *
	 */
	public void oprimirExportarDocumentosNoEmitidos() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		if (nitEmpleador.contains("-")) {
			int fin = nitEmpleador.indexOf("-");
			nitEmpleador = nitEmpleador.substring(0, fin);
		}
		borrarDatosNominas();
		tipoNomina = "T_NE_BASE";
	}

	
	private void borrarDatosNominas() {
		// TODO Auto-generated method stub
		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902001.getValue());

			requestManager.delete(urlDelete.getUrl(), null);


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	
	
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoNomina
	 * 
	 * @return tipoNomina
	 */
	public String getTipoNomina() {
		return tipoNomina;
	}

	/**
	 * Asigna la variable tipoNomina
	 * 
	 * @param tipoNomina Variable a asignar en tipoNomina
	 */
	public void setTipoNomina(String tipoNomina) {
		this.tipoNomina = tipoNomina;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public RegistroDataModelImpl getListaNominaAjuste() {
		return listaNominaAjuste;
	}

	public void setListaNominaAjuste(RegistroDataModelImpl listaNominaAjuste) {
		this.listaNominaAjuste = listaNominaAjuste;
	}

	public RegistroDataModelImpl getListaNominaBase() {
		return listaNominaBase;
	}

	public void setListaNominaBase(RegistroDataModelImpl listaNominaBase) {
		this.listaNominaBase = listaNominaBase;
	}

	public RegistroDataModelImpl getListaNominaEliminacion() {
		return listaNominaEliminacion;
	}

	public void setListaNominaEliminacion(RegistroDataModelImpl listaNominaEliminacion) {
		this.listaNominaEliminacion = listaNominaEliminacion;
	}

	public String getNitEmpleador() {
		return nitEmpleador;
	}

	public void setNitEmpleador(String nitEmpleador) {
		this.nitEmpleador = nitEmpleador;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPagInicio() {
		return pagInicio;
	}

	public void setPagInicio(String pagInicio) {
		this.pagInicio = pagInicio;
	}

	public String getPagTamanio() {
		return pagTamanio;
	}

	public void setPagTamanio(String pagTamanio) {
		this.pagTamanio = pagTamanio;
	}

	public boolean isIgnorarPaginado() {
		return ignorarPaginado;
	}

	public void setIgnorarPaginado(boolean ignorarPaginado) {
		this.ignorarPaginado = ignorarPaginado;
	}

	public String getCompania() {
		return compania;
	}

	public boolean isBloqueoBotonesExportar() {
		return bloqueoBotonesExportar;
	}

	public void setBloqueoBotonesExportar(boolean bloqueoBotonesExportar) {
		this.bloqueoBotonesExportar = bloqueoBotonesExportar;
	}

	public boolean isBloqueoFecha() {
		return bloqueoFecha;
	}

	public void setBloqueoFecha(boolean bloqueoFecha) {
		this.bloqueoFecha = bloqueoFecha;
	}

	public boolean isBloqueoConsecutivo() {
		return bloqueoConsecutivo;
	}

	public void setBloqueoConsecutivo(boolean bloqueoConsecutivo) {
		this.bloqueoConsecutivo = bloqueoConsecutivo;
	}

	
	
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>
}
