/*-
 * FrmCambiarFondoControlador.java
 *
 * 1.0
 * 
 * 09/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmCambiarFondoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 09/06/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class FrmCambiarFondoControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	private final String proceso;
	private final String anio;
	private final String mes;
	private final String periodo;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;
	private String nombrePension;
	private String nombreSalud;
	private String nombreRiesgos;
	private String nombrePensionVol;
	private String nombreMedicina;
	// <DECLARAR_ATRIBUTOS>
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
	private RegistroDataModelImpl listaFondoPension;
	private RegistroDataModelImpl listaFondoPensionE;
	private RegistroDataModelImpl listaFondoSalud;
	private RegistroDataModelImpl listaFondoSaludE;
	private RegistroDataModelImpl listaFondoRiesgos;
	private RegistroDataModelImpl listaFondoRiesgosE;
	private RegistroDataModelImpl listaFondoPensionVol;
	private RegistroDataModelImpl listaFondoPensionVolE;
	private RegistroDataModelImpl listaFondoMedicinaPre;
	private RegistroDataModelImpl listaFondoMedicinaPreE;

	private String claseFondo;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCambiarFondoControlador
	 */
	public FrmCambiarFondoControlador() {
		super();

		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = (String) SessionUtil.getSessionVar("procesoNomina");
		anio = (String) SessionUtil.getSessionVar("anioNomina");
		mes = (String) SessionUtil.getSessionVar("mesNomina");
		periodo = (String) SessionUtil.getSessionVar("periodoNomina");

		try {
			  Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			  if (parametrosEntrada != null) {
	                claseFondo = (String) parametrosEntrada.get("claseFondo");
			  }

			// 2411
			numFormulario = GeneralCodigoFormaEnum.FRM_CAMBIAR_FONDO_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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
		tabla = "PERSONAL_HISTORICO";
		reasignarOrigen();
		buscarLlave();
		registro = new Registro();
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaFondoPensionE();
		cargarListaFondoSaludE();
		cargarListaFondoRiesgosE();
		cargarListaFondoPensionVolE();
		cargarListaFondoMedicinaPreE();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor de la
	 * consulta del formulario. Tambien carga la lista del formulario por primera
	 * vez
	 */
	@Override
	public void reasignarOrigen() {

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
		parametrosListado.put(GeneralParameterEnum.MES.getName(), mes);
		parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);
		parametrosListado.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL1033004.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL1033006.getValue());
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaFondoPension
	 *
	 */
	public void cargarListaFondoPension() {
	}

	/**
	 * 
	 * Carga la lista listaFondoPension
	 *
	 */
	public void cargarListaFondoPensionE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL617001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASEFONDO", "AFP");

		listaFondoPensionE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_FONDO");

	}

	/**
	 * 
	 * Carga la lista listaFondoSalud
	 *
	 */
	public void cargarListaFondoSalud() {
	}

	/**
	 * 
	 * Carga la lista listaFondoSalud
	 *
	 */
	public void cargarListaFondoSaludE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL617001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASEFONDO", "EPS");

		listaFondoSaludE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_FONDO");

	}

	/**
	 * 
	 * Carga la lista listaFondoRiesgos
	 *
	 */
	public void cargarListaFondoRiesgos() {
	}

	/**
	 * 
	 * Carga la lista listaFondoRiesgos
	 *
	 */
	public void cargarListaFondoRiesgosE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL617001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASEFONDO", "ARL");

		listaFondoRiesgosE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_FONDO");

	}

	/**
	 * 
	 * Carga la lista listaFondoPensionVol
	 *
	 */
	public void cargarListaFondoPensionVol() {
	}

	/**
	 * 
	 * Carga la lista listaFondoPensionVol
	 *
	 */
	public void cargarListaFondoPensionVolE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL617001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASEFONDO", "APV");

		listaFondoPensionVolE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_FONDO");

	}

	/**
	 * 
	 * Carga la lista listaFondoMedicinaPre
	 *
	 */
	public void cargarListaFondoMedicinaPre() {
	}

	/**
	 * 
	 * Carga la lista listaFondoMedicinaPre
	 *
	 */
	public void cargarListaFondoMedicinaPreE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFondoControladorUrlEnum.URL617001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CLASEFONDO", "FMP");

		listaFondoMedicinaPreE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_FONDO");

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Informe en la vista
	 *
	 *
	 */
	public void oprimirInforme() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	
	private void generaReporte(FORMATOS formato) {
        try {
        	
            String reporte = "800576InformeCambioFondos";
            String codigoFondo = "";
            String nombreFondo = "";
            
            switch(claseFondo) {
            case "AFP":
            	codigoFondo = "ID_DEL_FONDO";
            	nombreFondo = "NOMBRE_FONDOPENSION";
              break;
            case "EPS":
            	codigoFondo = "FONDO_SALUD";
            	nombreFondo = "NOMBRE_FONDOSALUD";
              break;
            case "ARL":
            	codigoFondo = "FONDO_RIESGOS";
            	nombreFondo = "NOMBRE_FONDORIESGOS";
              break;
            case "APV":
            	codigoFondo = "FONDO_PENSION_VOL";
            	nombreFondo = "NOMBRE_FONDOPENSION_VOL";
              break;
            case "FMP":
            	codigoFondo = "MEDICINA_PREPAGADA";
            	nombreFondo = "NOMBRE_MEDICINAPREPAGADA";
                break;
          }


            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);
            reemplazar.put("proceso", proceso);
            reemplazar.put("codigoFondo",codigoFondo);
            reemplazar.put("nombreFondo",nombreFondo);
            


            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();

            String sql= Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, "800576InformeCambioFondos");
        
        }
        catch (JRException | IOException | NumberFormatException  | DRException | SQLException| com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control FondoPension en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFondoPensionC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("ID_DEL_FONDO", claseFondo + auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_FONDOPENSION", nombrePension);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control FondoSalud en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFondoSaludC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FONDO_SALUD", claseFondo + auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_FONDOSALUD", nombreSalud);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control FondoRiesgos en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFondoRiesgosC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FONDO_RIESGOS", claseFondo + auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_FONDORIESGOS", nombreRiesgos);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control FondoPensionVol en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFondoPensionVolC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FONDO_PENSION_VOL", claseFondo + auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_FONDOPENSION_VOL", nombrePensionVol);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control FondoMedicinaPre en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarFondoMedicinaPreC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("MEDICINA_PREPAGADA", claseFondo + auxiliar);

		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("NOMBRE_MEDICINAPREPAGADA", nombreMedicina);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoPension
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoPension(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("ID_DEL_FONDO", registroAux.getCampos().get("ID_DE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoPension
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoPensionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_FONDO"));
		nombrePension = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoSalud
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoSalud(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FONDO_SALUD", registroAux.getCampos().get("ID_DE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoSalud
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoSaludE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_FONDO"));
		nombreSalud = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoRiesgos
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoRiesgos(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FONDO_RIESGOS", registroAux.getCampos().get("ID_DE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoRiesgos
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoRiesgosE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_FONDO"));
		nombreRiesgos = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoPensionVol
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoPensionVol(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FONDO_PENSION_VOL", registroAux.getCampos().get("ID_DE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoPensionVol
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoPensionVolE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_FONDO"));
		nombrePensionVol = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoMedicinaPre
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoMedicinaPre(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("MEDICINA_PREPAGADA", registroAux.getCampos().get("ID_DE_FONDO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFondoMedicinaPre
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFondoMedicinaPreE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_FONDO"));
		nombreMedicina = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_FONDO"));
	}

	// </METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
		registro.getCampos().remove(GeneralParameterEnum.ID_DE_PROCESO.getName());
		registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
		registro.getCampos().remove(GeneralParameterEnum.MES.getName());
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.NOMBRECOMPLETO.getName());
	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaFondoPension
	 * 
	 * @return listaFondoPension
	 */
	public RegistroDataModelImpl getListaFondoPension() {
		return listaFondoPension;
	}

	/**
	 * Asigna la lista listaFondoPension
	 * 
	 * @param listaFondoPension Variable a asignar en listaFondoPension
	 */
	public void setListaFondoPension(RegistroDataModelImpl listaFondoPension) {
		this.listaFondoPension = listaFondoPension;
	}

	/**
	 * Retorna la lista listaFondoPension
	 * 
	 * @return listaFondoPension
	 */
	public RegistroDataModelImpl getListaFondoPensionE() {
		return listaFondoPensionE;
	}

	/**
	 * Asigna la lista listaFondoPension
	 * 
	 * @param listaFondoPension Variable a asignar en listaFondoPension
	 */
	public void setListaFondoPensionE(RegistroDataModelImpl listaFondoPensionE) {
		this.listaFondoPensionE = listaFondoPensionE;
	}

	/**
	 * Retorna la lista listaFondoSalud
	 * 
	 * @return listaFondoSalud
	 */
	public RegistroDataModelImpl getListaFondoSalud() {
		return listaFondoSalud;
	}

	/**
	 * Asigna la lista listaFondoSalud
	 * 
	 * @param listaFondoSalud Variable a asignar en listaFondoSalud
	 */
	public void setListaFondoSalud(RegistroDataModelImpl listaFondoSalud) {
		this.listaFondoSalud = listaFondoSalud;
	}

	/**
	 * Retorna la lista listaFondoSalud
	 * 
	 * @return listaFondoSalud
	 */
	public RegistroDataModelImpl getListaFondoSaludE() {
		return listaFondoSaludE;
	}

	/**
	 * Asigna la lista listaFondoSalud
	 * 
	 * @param listaFondoSalud Variable a asignar en listaFondoSalud
	 */
	public void setListaFondoSaludE(RegistroDataModelImpl listaFondoSaludE) {
		this.listaFondoSaludE = listaFondoSaludE;
	}

	/**
	 * Retorna la lista listaFondoRiesgos
	 * 
	 * @return listaFondoRiesgos
	 */
	public RegistroDataModelImpl getListaFondoRiesgos() {
		return listaFondoRiesgos;
	}

	/**
	 * Asigna la lista listaFondoRiesgos
	 * 
	 * @param listaFondoRiesgos Variable a asignar en listaFondoRiesgos
	 */
	public void setListaFondoRiesgos(RegistroDataModelImpl listaFondoRiesgos) {
		this.listaFondoRiesgos = listaFondoRiesgos;
	}

	/**
	 * Retorna la lista listaFondoRiesgos
	 * 
	 * @return listaFondoRiesgos
	 */
	public RegistroDataModelImpl getListaFondoRiesgosE() {
		return listaFondoRiesgosE;
	}

	/**
	 * Asigna la lista listaFondoRiesgos
	 * 
	 * @param listaFondoRiesgos Variable a asignar en listaFondoRiesgos
	 */
	public void setListaFondoRiesgosE(RegistroDataModelImpl listaFondoRiesgosE) {
		this.listaFondoRiesgosE = listaFondoRiesgosE;
	}

	/**
	 * Retorna la lista listaFondoPensionVol
	 * 
	 * @return listaFondoPensionVol
	 */
	public RegistroDataModelImpl getListaFondoPensionVol() {
		return listaFondoPensionVol;
	}

	/**
	 * Asigna la lista listaFondoPensionVol
	 * 
	 * @param listaFondoPensionVol Variable a asignar en listaFondoPensionVol
	 */
	public void setListaFondoPensionVol(RegistroDataModelImpl listaFondoPensionVol) {
		this.listaFondoPensionVol = listaFondoPensionVol;
	}

	/**
	 * Retorna la lista listaFondoPensionVol
	 * 
	 * @return listaFondoPensionVol
	 */
	public RegistroDataModelImpl getListaFondoPensionVolE() {
		return listaFondoPensionVolE;
	}

	/**
	 * Asigna la lista listaFondoPensionVol
	 * 
	 * @param listaFondoPensionVol Variable a asignar en listaFondoPensionVol
	 */
	public void setListaFondoPensionVolE(RegistroDataModelImpl listaFondoPensionVolE) {
		this.listaFondoPensionVolE = listaFondoPensionVolE;
	}

	/**
	 * Retorna la lista listaFondoMedicinaPre
	 * 
	 * @return listaFondoMedicinaPre
	 */
	public RegistroDataModelImpl getListaFondoMedicinaPre() {
		return listaFondoMedicinaPre;
	}

	/**
	 * Asigna la lista listaFondoMedicinaPre
	 * 
	 * @param listaFondoMedicinaPre Variable a asignar en listaFondoMedicinaPre
	 */
	public void setListaFondoMedicinaPre(RegistroDataModelImpl listaFondoMedicinaPre) {
		this.listaFondoMedicinaPre = listaFondoMedicinaPre;
	}

	/**
	 * Retorna la lista listaFondoMedicinaPre
	 * 
	 * @return listaFondoMedicinaPre
	 */
	public RegistroDataModelImpl getListaFondoMedicinaPreE() {
		return listaFondoMedicinaPreE;
	}

	/**
	 * Asigna la lista listaFondoMedicinaPre
	 * 
	 * @param listaFondoMedicinaPre Variable a asignar en listaFondoMedicinaPre
	 */
	public void setListaFondoMedicinaPreE(RegistroDataModelImpl listaFondoMedicinaPreE) {
		this.listaFondoMedicinaPreE = listaFondoMedicinaPreE;
	}

	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * @return the nombrePension
	 */
	public String getNombrePension() {
		return nombrePension;
	}

	/**
	 * @param nombrePension the nombrePension to set
	 */
	public void setNombrePension(String nombrePension) {
		this.nombrePension = nombrePension;
	}

	/**
	 * @return the nombreSalud
	 */
	public String getNombreSalud() {
		return nombreSalud;
	}

	/**
	 * @param nombreSalud the nombreSalud to set
	 */
	public void setNombreSalud(String nombreSalud) {
		this.nombreSalud = nombreSalud;
	}

	/**
	 * @return the nombreRiesgos
	 */
	public String getNombreRiesgos() {
		return nombreRiesgos;
	}

	/**
	 * @param nombreRiesgos the nombreRiesgos to set
	 */
	public void setNombreRiesgos(String nombreRiesgos) {
		this.nombreRiesgos = nombreRiesgos;
	}

	/**
	 * @return the nombrePensionVol
	 */
	public String getNombrePensionVol() {
		return nombrePensionVol;
	}

	/**
	 * @param nombrePensionVol the nombrePensionVol to set
	 */
	public void setNombrePensionVol(String nombrePensionVol) {
		this.nombrePensionVol = nombrePensionVol;
	}

	/**
	 * @return the nombreMedicina
	 */
	public String getNombreMedicina() {
		return nombreMedicina;
	}

	/**
	 * @param nombreMedicina the nombreMedicina to set
	 */
	public void setNombreMedicina(String nombreMedicina) {
		this.nombreMedicina = nombreMedicina;
	}

	/**
	 * @return the indice
	 */
	public int getIndice() {
		return indice;
	}

	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return the claseFondo
	 */
	public String getClaseFondo() {
		return claseFondo;
	}

	/**
	 * @param claseFondo the claseFondo to set
	 */
	public void setClaseFondo(String claseFondo) {
		this.claseFondo = claseFondo;
	}

	/**
	 * @return the modulo
	 */
	public String getModulo() {
		return modulo;
	}

	/**
	 * @return the proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * @return the periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}
