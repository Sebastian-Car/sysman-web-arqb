/*-
 * DistribuirAuxControlador.java
 *
 * 1.0
 * 
 * 20/08/2024
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
import java.text.DecimalFormat;
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
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.EjbNominaDiezRemote;
import com.sysman.nomina.enums.DistribuirAuxControladorUrlEnum;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @version 1.0, 20/08/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class DistribuirAuxControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private Double sumHorasP;
	private Double sumMinutosP;
	private Double sumTiempoP;
	private Double sumPorcP;
	private double sumTiempoD;
	private double sumHorasD;
	private double sumMinutosD;
	private String nombreProceso;
	private String nombrePeriodo;
	private int horasTrabM;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaIdEmpleadoPer;
	private RegistroDataModelImpl listaIdEmpleadoPerE;
	private RegistroDataModelImpl listaCentroCostoPer;
	private RegistroDataModelImpl listaCentroCostoPerE;
	private RegistroDataModelImpl listaAuxiliarPer;
	private RegistroDataModelImpl listaAuxiliarPerE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	private RegistroDataModelImpl listaIdEmpleadoDia;
	private RegistroDataModelImpl listaIdEmpleadoDiaE;
	private RegistroDataModelImpl listaCentroCostoDia;
	private RegistroDataModelImpl listaCentroCostoDiaE;
	private RegistroDataModelImpl listaAuxiliarDia;
	private RegistroDataModelImpl listaAuxiliarDiaE;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	private RegistroDataModelImpl listaFrmdistpersonalaux;
	private RegistroDataModelImpl listaFrmdistpersonaldiarioaux;

	private StreamedContent archivoDescarga;
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario FrmDistPersonalAux
	 */
	private Registro registroSubFrmDistPersonalAux;
	/**
	 * Atributo de referencia para el subformulario FrmDistPersonalDiarioAux
	 */
	private Registro registroSubFrmDistPersonalDiarioAux;
	private String proceso;
	private String anio;
	private String mes;
	private String nombreMes;
	private String periodo;
	private String nombreProcesos;
	private String nombrePeriodos;
	
	/**
     * Variable encargada de mostrar o no el dialogo de confirmacion.
     */
    private boolean dialogoVisible;

	@EJB
	private EjbNominaDiezRemote ejbNominaDiez;

	@EJB
	private EjbNominaCeroGeneralRemote ejbNominaCero;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtilRemote;
	private boolean respuesta = true;

	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de DistribuirAuxControlador
	 */
	public DistribuirAuxControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = 2481;
			validarPermisos();
			//<INI_ADICIONAL>
			registroSubFrmDistPersonalAux = new Registro(new HashMap<String, Object>());
			registroSubFrmDistPersonalDiarioAux = new Registro(new HashMap<String, Object>());

			proceso = SessionUtil.getSessionVar("procesoNomina").toString();
			nombreProcesos = SysmanFunciones.toString(SessionUtil.getSessionVar("nombreProcesoNomina"));
			anio = SessionUtil.getSessionVar("anioNomina").toString();
			mes = SessionUtil.getSessionVar("mesNomina").toString();
			nombreMes = SessionUtil.getSessionVar("nombreMesNomina").toString();
			periodo = SessionUtil.getSessionVar("periodoNomina").toString();
			nombrePeriodos = SysmanFunciones.toString(SessionUtil.getSessionVar("nombrePeriodoNomina"));
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
		cargarListaIdEmpleadoPer();
		cargarListaIdEmpleadoPerE();
		cargarListaCentroCostoPer();
		cargarListaCentroCostoPerE();
		cargarListaAuxiliarPer();
		cargarListaAuxiliarPerE();
		cargarListaIdEmpleadoDia();
		cargarListaIdEmpleadoDiaE();
		cargarListaCentroCostoDia();
		cargarListaCentroCostoDiaE();
		cargarListaAuxiliarDia();
		cargarListaAuxiliarDiaE();
		abrirFormulario();
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
		// <CARGAR_LISTAS_SUBFORM>
		cargarListaFrmdistpersonalaux();
		cargarListaFrmdistpersonaldiarioaux();
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaFrmdistpersonalaux = null;
		listaFrmdistpersonaldiarioaux = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		tabla = "COMPANIA";
		buscarLlave();
		asignarOrigenDatos();
		iniciarListas();
		iniciarListasSub();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";
	}

	/**
	 * 
	 * Carga la lista listaFrmdistpersonalaux
	 *
	 */
	public void cargarListaFrmdistpersonalaux() {

        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(GenericUrlEnum.DISTPERSONAL.getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
            param.put(GeneralParameterEnum.ANIO.getName(), anio);
            param.put(GeneralParameterEnum.MES.getName(), mes);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            
            String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                    GenericUrlEnum.DISTPERSONAL.getTable());
            
            listaFrmdistpersonalaux = new RegistroDataModelImpl(
                    urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(),
                    param, rowKey);
            
        } catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

	/**
	 * 
	 * Carga la lista listaFrmdistpersonaldiarioaux
	 *
	 */
	public void cargarListaFrmdistpersonaldiarioaux() {

		try {

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.DISTPERSONALDIARIO.getGridKey());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

			String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
					GenericUrlEnum.DISTPERSONALDIARIO.getTable());

			listaFrmdistpersonaldiarioaux = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(),
					param, rowKey);

		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaIdEmpleadoPer
	 *
	 */
	public void cargarListaIdEmpleadoPer() {

		String urlEnumId = DistribuirAuxControladorUrlEnum.URL0001.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaIdEmpleadoPer = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_DE_EMPLEADO");

	}

	/**
	 * 
	 * Carga la lista listaIdEmpleadoPer
	 *
	 */
	public void cargarListaIdEmpleadoPerE() {

		listaIdEmpleadoPerE = listaIdEmpleadoPer;
	}

	/**
	 * 
	 * Carga la lista listaCentroCostoPer
	 *
	 */
	public void cargarListaCentroCostoPer() {

		String urlEnumId = DistribuirAuxControladorUrlEnum.URL0002.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCentroCostoPer = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaCentroCostoPer
	 *
	 */
	public void cargarListaCentroCostoPerE() {
		listaCentroCostoPerE = listaCentroCostoPer;
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarPer
	 *
	 */
	public void cargarListaAuxiliarPer() {

		String urlEnumId = DistribuirAuxControladorUrlEnum.URL0003.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		listaAuxiliarPer = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarPer
	 *
	 */
	public void cargarListaAuxiliarPerE() {
		listaAuxiliarPerE = listaAuxiliarPer;
	}

	/**
	 * 
	 * Carga la lista listaIdEmpleadoDia
	 *
	 */
	public void cargarListaIdEmpleadoDia() {
		listaIdEmpleadoDia = listaIdEmpleadoPer;
	}

	/**
	 * 
	 * Carga la lista listaIdEmpleadoDia
	 *
	 */
	public void cargarListaIdEmpleadoDiaE() {
		listaIdEmpleadoDiaE = listaIdEmpleadoPer;
	}

	/**
	 * 
	 * Carga la lista listaCentroCostoDia
	 *
	 */
	public void cargarListaCentroCostoDia() {
		listaCentroCostoDia = listaCentroCostoPer;
	}

	/**
	 * 
	 * Carga la lista listaCentroCostoDia
	 *
	 */
	public void cargarListaCentroCostoDiaE() {
		listaCentroCostoDiaE = listaCentroCostoPer;
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarDia
	 *
	 */
	public void cargarListaAuxiliarDia() {
		listaAuxiliarDia = listaAuxiliarPer;
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarDia
	 *
	 */
	public void cargarListaAuxiliarDiaE() {
		listaAuxiliarDiaE = listaAuxiliarPer;
	}

	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control HorasT
	 * 
	 * 
	 */
	public void cambiarHorasT() {
		// <CODIGO_DESARROLLADO>

		int horasT = Integer
				.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("ID_DE_EMPLEADO"));
		if (horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM);
			registroSubFrmDistPersonalAux.getCampos().put("HORAST", horasTrabM);
		}
		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			double totalH = distribuir(idEmpleado, 1) + horasT;
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + totalH);
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		registroSubFrmDistPersonalAux.getCampos().put("TIEMPOTRABAJADO", horasT);
		registroSubFrmDistPersonalAux.getCampos().put("PORCENTAJE", (horasT * 100 / horasTrabM));

		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control Porcentaje
	 * 
	 * 
	 */
	public void cambiarPorcentaje() {
		// <CODIGO_DESARROLLADO>		
		double porcentaje = Double.parseDouble(SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("PORCENTAJE")));		
		String idEmpleado = SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("ID_DE_EMPLEADO"));
		if(porcentaje > 100)
		{
			JsfUtil.agregarMensajeError("El Porcentaje máximo al mes deben ser: 100% ");
		}		
		if (distribuir(idEmpleado, 2) + porcentaje > 100) {
			double totalP = distribuir(idEmpleado, 2) + porcentaje;
			JsfUtil.agregarMensajeError("El Porcentaje máximo al mes deben ser: 100% el porcentaje total del empleado suma: " + totalP);
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		int horasT = (int) (porcentaje * horasTrabM / 100);
		registroSubFrmDistPersonalAux.getCampos().put("TIEMPOTRABAJADO", horasT);
		registroSubFrmDistPersonalAux.getCampos().put("HORAST", horasT);

		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control MinutosT
	 * 
	 * 
	 */
	public void cambiarMinutosT() {
		// <CODIGO_DESARROLLADO>
		int horasT = Integer
				.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("HORAST")));
		double minutosT = Double
				.parseDouble(SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("MINUTOST")));

		double minutosComoHoras = minutosT / 60;
		double tiempoTrabajado = horasT + minutosComoHoras;
		double porcentaje = SysmanFunciones.redondear(tiempoTrabajado * 100 / horasTrabM, 2);

		registroSubFrmDistPersonalAux.getCampos().put("TIEMPOTRABAJADO", tiempoTrabajado);
		registroSubFrmDistPersonalAux.getCampos().put("PORCENTAJE", porcentaje);
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control HorasD
	 * 
	 * 
	 */
	public void cambiarHorasD() {
		//<CODIGO_DESARROLLADO>
		int horasT = Integer
				.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("ID_DE_EMPLEADO"));
		if (horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("HORASD", horasTrabM);
		}
		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			double totalH = distribuir(idEmpleado, 1) + horasT;
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + totalH);
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		registroSubFrmDistPersonalDiarioAux.getCampos().put("TIEMPOTRABAJADO", horasT);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control MinutosD
	 * 
	 * 
	 */
	public void cambiarMinutosD() {
		//<CODIGO_DESARROLLADO>
		int horasT = Integer.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("HORAST")));	
		double minutosT = Double.parseDouble(SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("MINUTOST")));

		double minutosComoHoras = minutosT / 60;
		double tiempoTrabajado = horasT + minutosComoHoras;
		registroSubFrmDistPersonalDiarioAux.getCampos().put("TIEMPOTRABAJADO", tiempoTrabajado);
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control HorasT en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarHorasTC(int rowNum) {
		// <CODIGO_DESARROLLADO>

		int horasT = Integer.parseInt(SysmanFunciones
				.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones
				.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("ID_DE_EMPLEADO"));
		if (horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM);
			listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("HORAST", horasTrabM);
		}
		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + distribuir(idEmpleado, 1));
			respuesta = false;
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("TIEMPOTRABAJADO", horasT);
		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("PORCENTAJE",
				(horasT * 100 / horasTrabM));

		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control Porcentaje en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarPorcentajeC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		double porcentaje = Double.parseDouble(SysmanFunciones.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("PORCENTAJE")));
		String idEmpleado = SysmanFunciones
				.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("ID_DE_EMPLEADO"));
		if(porcentaje > 100)
		{
			JsfUtil.agregarMensajeError("El Porcentaje máximo al mes deben ser: 100% ");
		}		
		if (distribuir(idEmpleado, 2) + porcentaje > 100) {
			double totalP = distribuir(idEmpleado, 2) + porcentaje;
			JsfUtil.agregarMensajeError("El Porcentaje máximo al mes deben ser: 100% el porcentaje total del empleado suma: " + totalP);
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		int horasT = (int) (porcentaje * horasTrabM / 100);
		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("TIEMPOTRABAJADO", horasT);
		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("PORCENTAJE",horasT);
		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control MinutosT en la fila seleccionada
	 * dentro de la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarMinutosTC(int rowNum) {
		// <CODIGO_DESARROLLADO>

		int horasT = Integer.parseInt(SysmanFunciones
				.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("HORAST")));
		double minutosT = Double.parseDouble(SysmanFunciones
				.toString(listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().get("MINUTOST")));

		double minutosComoHoras = minutosT / 60;
		double tiempoTrabajado = horasT + minutosComoHoras;
		double porcentaje = SysmanFunciones.redondear(tiempoTrabajado * 100 / horasTrabM, 2);

		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("TIEMPOTRABAJADO", tiempoTrabajado);
		listaFrmdistpersonalaux.getDatasource().get(rowNum % 10).getCampos().put("PORCENTAJE", porcentaje);

		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control HorasD en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarHorasDC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		int horasT = Integer.parseInt(SysmanFunciones
				.toString(listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones
				.toString(listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().get("ID_DE_EMPLEADO"));
		if (horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM);
			listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().put("HORAST", horasTrabM);
		}
		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + distribuir(idEmpleado, 1));
			respuesta = false;
		}
		if (distribuir(idEmpleado, 2) > 100) {
			JsfUtil.agregarMensajeError(
					"El Porcentaje máximo al mes deben ser: 100% y los valores suman: " + distribuir(idEmpleado, 2));
		}
		listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().put("TIEMPOTRABAJADO", horasT);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control MinutosD en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarMinutosDC(int rowNum) {

		//<CODIGO_DESARROLLADO>
		int horasT = Integer.parseInt(SysmanFunciones.toString(listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().get("HORAST")));	
		double minutosT = Double.parseDouble(SysmanFunciones.toString(listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().get("MINUTOST")));

		double minutosComoHoras = minutosT / 60;
		double tiempoTrabajado = horasT + minutosComoHoras;

		listaFrmdistpersonaldiarioaux.getDatasource().get(rowNum % 10).getCampos().put("TIEMPOTRABAJADO", tiempoTrabajado);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>

	public boolean validarHoras() {
		boolean rta = true;

		int horasT = Integer
				.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones.toString(registroSubFrmDistPersonalAux.getCampos().get("ID_DE_EMPLEADO"));

		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			double totalH = distribuir(idEmpleado, 1) + horasT;
			rta = false;
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + totalH);
		}

		return rta;
	}	
	
	public boolean validarHorasD() {
		boolean rta = true;

		int horasT = Integer
				.parseInt(SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("HORAST")));
		String idEmpleado = SysmanFunciones.toString(registroSubFrmDistPersonalDiarioAux.getCampos().get("ID_DE_EMPLEADO"));

		if (distribuir(idEmpleado, 1) + horasT > horasTrabM) {
			double totalH = distribuir(idEmpleado, 1) + horasT;
			rta = false;
			JsfUtil.agregarMensajeError("El Número máximo de horas laboradas al mes deben ser: " + horasTrabM
					+ " y los horas suman: " + totalH);
		}

		return rta;
	}	
	
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaIdEmpleadoPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdEmpleadoPer(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalAux.getCampos().put("ID_DE_EMPLEADO", registroAux.getCampos().get("ID_DE_EMPLEADO"));
		registroSubFrmDistPersonalAux.getCampos().put("NOMBRE_EMPLEADO", registroAux.getCampos().get("NOMBRECOMPLETO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaIdEmpleadoPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdEmpleadoPerE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoPer(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalAux.getCampos().put("ID_CENTRO_DE_COSTO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoPerE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarPer(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalAux.getCampos().put("ID_AUXILIAR", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarPer
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarPerE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaIdEmpleadoDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdEmpleadoDia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_DE_EMPLEADO",
				registroAux.getCampos().get("ID_DE_EMPLEADO"));
		registroSubFrmDistPersonalDiarioAux.getCampos().put("NOMBRE_EMPLEADO", registroAux.getCampos().get("NOMBRECOMPLETO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaIdEmpleadoDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaIdEmpleadoDiaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoDia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_CENTRO_DE_COSTO",
				registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroCostoDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoDiaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarDia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_AUXILIAR", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarDia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarDiaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Copiar en la vista
	 *
	 *
	 */
	public void oprimirCopiar() {
		// <CODIGO_DESARROLLADO>
		String[] campos = { "compania" };
		String[] valores = { compania };
		SessionUtil.cargarModal(String.valueOf(GeneralCodigoFormaEnum.FRM_PREPARAR_DIST_CONTROLADOR.getCodigo()),
				modulo, campos, valores);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Calcular en la vista
	 *
	 *
	 */
	public void oprimirCalcular() {
		// <CODIGO_DESARROLLADO>
		String[] campos = { "compania" };
		String[] valores = { compania };
		SessionUtil.cargarModal(String.valueOf(GeneralCodigoFormaEnum.FRM_CALCULO_DISTRIBUCION_CONTROLADOR.getCodigo()),
				modulo, campos, valores);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton MigrarIni en la vista
	 *
	 *
	 */
	public void oprimirMigrarIni() {
		// <CODIGO_DESARROLLADO>
		try {
			ejbNominaDiez.distribuirDatos(compania, proceso, anio, mes, periodo, SessionUtil.getUser().getCodigo());
			cargarListaFrmdistpersonalaux();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton RevDifDistribucion en la vista
	 *
	 *
	 */
	public void oprimirRevDifDistribucion() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String reporte = "800643RevisarDiferenciasDistribucion";
		generarReporte(reporte);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnSubirDatos en la vista
	 *
	 *
	 */
	public void oprimirBtnSubirDatos() {
		// <CODIGO_DESARROLLADO>

		try {
			String msg = ejbNominaDiez.ajustarDecimalesDist(compania, mes, periodo, anio, proceso, anio);
			JsfUtil.agregarMensajeInformativo(msg);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ActualizaPorcentajes en la vista
	 *
	 *
	 */
	public void oprimirActualizaPorcentajes() {
		// <CODIGO_DESARROLLADO>
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("COMPANIA", compania);
			param.put("PROCESO", proceso);
			param.put("ANIO", anio);
			param.put("MES", mes);
			param.put("PERIODO", periodo);
			Parameter parameter = new Parameter();
			parameter.setFields(param);

			String urlEnumId = DistribuirAuxControladorUrlEnum.URL0004.getValue();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton RetiradosCondistribucion en la vista
	 *
	 *
	 */
	public void oprimirRetiradosCondistribucion() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String reporte = "800642RetiradosConDistribucion";
		generarReporte(reporte);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConDif100PorCiento en la vista
	 *
	 *
	 */
	public void oprimirConDif100PorCiento() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String reporte = "800641Consultar100PorCiento";
		generarReporte(reporte);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton AjustarDecimales en la vista
	 *
	 *
	 */
	public void oprimirAjustarDecimales() {
		// <CODIGO_DESARROLLADO>
		try {
			boolean periodoActivo = ejbNominaCero
					.validarPeriodoActivoNomina(compania,
							Integer.parseInt(proceso),
							Integer.parseInt(anio),
							Integer.parseInt(mes),
							Integer.parseInt(periodo));

			if(periodoActivo) {
				String msg = ejbNominaDiez.ajustarDecimalesDist(compania, mes, periodo, anio, proceso, anio);
				JsfUtil.agregarMensajeInformativo(msg);
			}


		}
		catch ( NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cerrar en la vista
	 *
	 *
	 */
	public void oprimirCerrar() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Generar en la vista
	 *
	 *
	 */
	public void oprimirGenerar() {
		// <CODIGO_DESARROLLADO>
		dialogoVisible = true;
		// </CODIGO_DESARROLLADO>
	}
	
	/**
    *
    * Metodo ejecutado al oprimir el boton Aceptar del dialogo
    * GENERAR REGISTROS en la vista
    *
    *
    */
   public void aceptarGenerarRegistros()
   {
       // <CODIGO_DESARROLLADO>
       dialogoVisible = false;
       try {
			ejbNominaDiez.generarDistMensual(compania, proceso, anio, mes, periodo, anio);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			cargarListaFrmdistpersonalaux();
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
       // </CODIGO_DESARROLLADO>
   }
   
   /**
   *
   * Metodo ejecutado al oprimir el boton Aceptar del dialogo
   * GENERAR REGISTROS en la vista
   *
   */
  public void cancelarGenerarRegistros()
  {
      dialogoVisible = false;
  }

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CambiarPeriodo en la vista
	 *
	 *
	 */
	public void oprimirCambiarPeriodo() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void generarReporte(String reporte) {
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("anio", anio);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);
			reemplazar.put("proceso", proceso);

			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar);

			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
					reporte);

		} catch (JRException | IOException | NumberFormatException | DRException | SQLException
				| com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarSumaTotales() {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

			Registro rsSuma;

			rsSuma = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DistribuirAuxControladorUrlEnum.URL0005.getValue())
							.getUrl(),
							param));


			if (rsSuma != null)
			{

				sumMinutosP = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("MINUTOST")), "0")));

				sumHorasP = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("HORAST")), "0")));

				sumTiempoP = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("TIEMPOTRABAJADO")), "0")));

				sumPorcP = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("PORCENTAJE")), "0")));
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarSumaTotalesDiario() {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

			Registro rsSuma;

			rsSuma = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DistribuirAuxControladorUrlEnum.URL0007.getValue())
							.getUrl(),
							param));


			if (rsSuma != null)
			{

				sumMinutosD = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("MINUTOST")), "0")));

				sumHorasD = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("HORAST")), "0")));

				sumTiempoD = Double.parseDouble(
						SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rsSuma.getCampos().get("TIEMPOTRABAJADO")), "0")));

			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}


	public double distribuir(String idEmpleado,int opcion) {
		double distribuir = 0;
		try {

			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
			param.put("CODEMPLEADO", idEmpleado);

			Registro rs;

			rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									DistribuirAuxControladorUrlEnum.URL0006.getValue())
							.getUrl(),
							param));


			if (rs != null)
			{
				if(opcion == 1) {

					distribuir = Double.parseDouble(
							SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rs.getCampos().get("THORAST")), "0")));
				} else if(opcion == 2) {


					distribuir = Double.parseDouble(
							SysmanFunciones.toString(SysmanFunciones.nvl(SysmanFunciones.toString(rs.getCampos().get("TPORCENTAJE")), "0")));
				}
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return distribuir;
	}

	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Frmdistpersonalaux
	 * 
	 */
	public void agregarRegistroSubFrmdistpersonalaux() {
		try {
			if(validarHoras()) {
			registroSubFrmDistPersonalAux.getCampos().put("COMPANIA", compania);
			registroSubFrmDistPersonalAux.getCampos().put("ID_DE_PROCESO", proceso);
			registroSubFrmDistPersonalAux.getCampos().put("ANO", anio);
			registroSubFrmDistPersonalAux.getCampos().put("MES", mes);
			registroSubFrmDistPersonalAux.getCampos().put("PERIODO", periodo);
			registroSubFrmDistPersonalAux.getCampos().put("CREATED_BY", SessionUtil.getUser().getCodigo());
			registroSubFrmDistPersonalAux.getCampos().put("DATE_CREATED", new Date());

			registroSubFrmDistPersonalAux.getCampos().remove("NOMBRE_EMPLEADO");

			String urlEnumId = GenericUrlEnum.DISTPERSONAL.getCreateKey();

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubFrmDistPersonalAux.getCampos());
			cargarListaFrmdistpersonalaux();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
			}

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSubFrmDistPersonalAux = new Registro(new HashMap<String, Object>());
			registroSubFrmDistPersonalAux.getCampos().put("ID_AUXILIAR", SysmanConstantes.CONS_AUXILIAR);
			registroSubFrmDistPersonalAux.getCampos().put("ID_CENTRO_DE_COSTO", SysmanConstantes.CONS_CENTRO);
			registroSubFrmDistPersonalAux.getCampos().put("HORAST", 0);
			registroSubFrmDistPersonalAux.getCampos().put("MINUTOST", 0);
			registroSubFrmDistPersonalAux.getCampos().put("TIEMPOTRABAJADO", 0);
			registroSubFrmDistPersonalAux.getCampos().put("PORCENTAJE", 0);
		}
	}

	/**
	 * Metodo de edicion del formulario Frmdistpersonalaux
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubFrmdistpersonalaux(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
			reg.getCampos().put("DATE_MODIFIED", new Date());
			reg.getCampos().remove("NOMBRE_EMPLEADO");

			String urlEnumId = GenericUrlEnum.DISTPERSONAL.getUpdateKey();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaFrmdistpersonalaux();
		}
	}

	/**
	 * Metodo de eliminacion del formulario Frmdistpersonalaux
	 * 
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubFrmdistpersonalaux(Registro reg) {
		try {

			String urlEnumId = GenericUrlEnum.DISTPERSONAL.getDeleteKey();
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));

			cargarListaFrmdistpersonalaux();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Frmdistpersonalaux
	 *
	 */
	public void cancelarEdicionFrmdistpersonalaux() {
		cargarListaFrmdistpersonalaux();
	}

	/**
	 * Metodo de insercion del formulario Frmdistpersonaldiarioaux
	 * 
	 */
	public void agregarRegistroSubFrmdistpersonaldiarioaux() {

		try {
			if(validarHorasD()) {
				registroSubFrmDistPersonalDiarioAux.getCampos().put("COMPANIA", compania);
				registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_DE_PROCESO", proceso);
				registroSubFrmDistPersonalDiarioAux.getCampos().put("ANO", anio);
				registroSubFrmDistPersonalDiarioAux.getCampos().put("MES", mes);
				registroSubFrmDistPersonalDiarioAux.getCampos().put("PERIODO", periodo);
				registroSubFrmDistPersonalDiarioAux.getCampos().put("CREATED_BY", SessionUtil.getUser().getCodigo());
				registroSubFrmDistPersonalDiarioAux.getCampos().put("DATE_CREATED", new Date());
	
				registroSubFrmDistPersonalDiarioAux.getCampos().remove("NOMBRE_EMPLEADO");
	
				String urlEnumId = GenericUrlEnum.DISTPERSONALDIARIO.getCreateKey();
	
				UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
				requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSubFrmDistPersonalDiarioAux.getCampos());
				cargarListaFrmdistpersonaldiarioaux();
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
			}
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarSumaTotalesDiario();
			registroSubFrmDistPersonalDiarioAux = new Registro(new HashMap<String, Object>());
			registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_AUXILIAR", SysmanConstantes.CONS_AUXILIAR);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_CENTRO_DE_COSTO", SysmanConstantes.CONS_CENTRO);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("MINUTOST", 0);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("HORAST", 0);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("TIEMPOTRABAJADO", 0);
			registroSubFrmDistPersonalDiarioAux.getCampos().put("FECHA", new Date());
		}
	}

	/**
	 * Metodo de edicion del formulario Frmdistpersonaldiarioaux
	 * 
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubFrmdistpersonaldiarioaux(RowEditEvent event) {

		Registro reg = (Registro) event.getObject();
		try {
			reg.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
			reg.getCampos().put("DATE_MODIFIED", new Date());
			reg.getCampos().remove("NOMBRE_EMPLEADO");

			String urlEnumId = GenericUrlEnum.DISTPERSONALDIARIO.getUpdateKey();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaFrmdistpersonaldiarioaux();
			cargarSumaTotalesDiario();
		}

	}

	/**
	 * Metodo de eliminacion del formulario Frmdistpersonaldiarioaux
	 * 
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubFrmdistpersonaldiarioaux(Registro reg) {
		try {

			String urlEnumId = GenericUrlEnum.DISTPERSONALDIARIO.getDeleteKey();
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));

			cargarListaFrmdistpersonaldiarioaux();
			cargarSumaTotalesDiario();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Frmdistpersonaldiarioaux
	 *
	 */
	public void cancelarEdicionFrmdistpersonaldiarioaux() {
		cargarListaFrmdistpersonaldiarioaux();
	}

	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		nombrePeriodo = "Proceso: " + nombreProcesos;
		nombreProceso = "Periodo: " + nombrePeriodos + " de " + nombreMes + " de " + anio;

		cargarSumaTotales();
		cargarSumaTotalesDiario();

		registroSubFrmDistPersonalAux.getCampos().put("ID_AUXILIAR", SysmanConstantes.CONS_AUXILIAR);
		registroSubFrmDistPersonalAux.getCampos().put("ID_CENTRO_DE_COSTO", SysmanConstantes.CONS_CENTRO);
		registroSubFrmDistPersonalAux.getCampos().put("HORAST", 0);
		registroSubFrmDistPersonalAux.getCampos().put("MINUTOST", 0);
		registroSubFrmDistPersonalAux.getCampos().put("TIEMPOTRABAJADO", 0);
		registroSubFrmDistPersonalAux.getCampos().put("PORCENTAJE", 0);

		registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_AUXILIAR", SysmanConstantes.CONS_AUXILIAR);
		registroSubFrmDistPersonalDiarioAux.getCampos().put("ID_CENTRO_DE_COSTO", SysmanConstantes.CONS_CENTRO);
		registroSubFrmDistPersonalDiarioAux.getCampos().put("MINUTOST", 0);
		registroSubFrmDistPersonalDiarioAux.getCampos().put("HORAST", 0);
		registroSubFrmDistPersonalDiarioAux.getCampos().put("TIEMPOTRABAJADO", 0);
		registroSubFrmDistPersonalDiarioAux.getCampos().put("FECHA", new Date());
		try 
		{
			horasTrabM = Integer.parseInt(SysmanFunciones.toString(SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania, "HORAS TRABAJADAS MENSUALES", modulo, new Date(), false), "240")));
		}
	    catch (SystemException e)
	    {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }

		// </CODIGO_DESARROLLADO>
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
	 * 
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

	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable sumHorasP
	 * 
	 * @return sumHorasP
	 */
	public Double getSumHorasP() {
		return sumHorasP;
	}

	/**
	 * Asigna la variable sumHorasP
	 * 
	 * @param sumHorasP Variable a asignar en sumHorasP
	 */
	public void setSumHorasP(Double sumHorasP) {
		this.sumHorasP = sumHorasP;
	}

	/**
	 * Retorna la variable sumMinutosP
	 * 
	 * @return sumMinutosP
	 */
	public Double getSumMinutosP() {
		return sumMinutosP;
	}

	/**
	 * Asigna la variable sumMinutosP
	 * 
	 * @param sumMinutosP Variable a asignar en sumMinutosP
	 */
	public void setSumMinutosP(Double sumMinutosP) {
		this.sumMinutosP = sumMinutosP;
	}

	/**
	 * Retorna la variable sumTiempoP
	 * 
	 * @return sumTiempoP
	 */
	public Double getSumTiempoP() {
		return sumTiempoP;
	}

	/**
	 * Asigna la variable sumTiempoP
	 * 
	 * @param sumTiempoP Variable a asignar en sumTiempoP
	 */
	public void setSumTiempoP(Double sumTiempoP) {
		this.sumTiempoP = sumTiempoP;
	}

	/**
	 * Retorna la variable sumPorcP
	 * 
	 * @return sumPorcP
	 */
	public Double getSumPorcP() {
		return sumPorcP;
	}

	/**
	 * Asigna la variable sumPorcP
	 * 
	 * @param sumPorcP Variable a asignar en sumPorcP
	 */
	public void setSumPorcP(Double sumPorcP) {
		this.sumPorcP = sumPorcP;
	}

	/**
	 * Retorna la variable sumTiempoD
	 * 
	 * @return sumTiempoD
	 */
	public double getSumTiempoD() {
		return sumTiempoD;
	}

	/**
	 * Asigna la variable sumTiempoD
	 * 
	 * @param sumTiempoD Variable a asignar en sumTiempoD
	 */
	public void setSumTiempoD(double sumTiempoD) {
		this.sumTiempoD = sumTiempoD;
	}

	/**
	 * Retorna la variable sumHorasD
	 * 
	 * @return sumHorasD
	 */
	public double getSumHorasD() {
		return sumHorasD;
	}

	/**
	 * Asigna la variable sumHorasD
	 * 
	 * @param sumHorasD Variable a asignar en sumHorasD
	 */
	public void setSumHorasD(double sumHorasD) {
		this.sumHorasD = sumHorasD;
	}

	/**
	 * Retorna la variable sumMinutosD
	 * 
	 * @return sumMinutosD
	 */
	public double getSumMinutosD() {
		return sumMinutosD;
	}

	/**
	 * Asigna la variable sumMinutosD
	 * 
	 * @param sumMinutosD Variable a asignar en sumMinutosD
	 */
	public void setSumMinutosD(double sumMinutosD) {
		this.sumMinutosD = sumMinutosD;
	}

	/**
	 * Retorna la variable nombreProceso
	 * 
	 * @return nombreProceso
	 */
	public String getNombreProceso() {
		return nombreProceso;
	}

	/**
	 * Asigna la variable nombreProceso
	 * 
	 * @param nombreProceso Variable a asignar en nombreProceso
	 */
	public void setNombreProceso(String nombreProceso) {
		this.nombreProceso = nombreProceso;
	}

	/**
	 * Retorna la variable nombrePeriodo
	 * 
	 * @return nombrePeriodo
	 */
	public String getNombrePeriodo() {
		return nombrePeriodo;
	}

	/**
	 * Asigna la variable nombrePeriodo
	 * 
	 * @param nombrePeriodo Variable a asignar en nombrePeriodo
	 */
	public void setNombrePeriodo(String nombrePeriodo) {
		this.nombrePeriodo = nombrePeriodo;
	}

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaIdEmpleadoPer
	 * 
	 * @return listaIdEmpleadoPer
	 */
	public RegistroDataModelImpl getListaIdEmpleadoPer() {
		return listaIdEmpleadoPer;
	}

	/**
	 * Asigna la lista listaIdEmpleadoPer
	 * 
	 * @param listaIdEmpleadoPer Variable a asignar en listaIdEmpleadoPer
	 */
	public void setListaIdEmpleadoPer(RegistroDataModelImpl listaIdEmpleadoPer) {
		this.listaIdEmpleadoPer = listaIdEmpleadoPer;
	}

	/**
	 * Retorna la lista listaIdEmpleadoPer
	 * 
	 * @return listaIdEmpleadoPer
	 */
	public RegistroDataModelImpl getListaIdEmpleadoPerE() {
		return listaIdEmpleadoPerE;
	}

	/**
	 * Asigna la lista listaIdEmpleadoPer
	 * 
	 * @param listaIdEmpleadoPer Variable a asignar en listaIdEmpleadoPer
	 */
	public void setListaIdEmpleadoPerE(RegistroDataModelImpl listaIdEmpleadoPerE) {
		this.listaIdEmpleadoPerE = listaIdEmpleadoPerE;
	}

	/**
	 * Retorna la lista listaCentroCostoPer
	 * 
	 * @return listaCentroCostoPer
	 */
	public RegistroDataModelImpl getListaCentroCostoPer() {
		return listaCentroCostoPer;
	}

	/**
	 * Asigna la lista listaCentroCostoPer
	 * 
	 * @param listaCentroCostoPer Variable a asignar en listaCentroCostoPer
	 */
	public void setListaCentroCostoPer(RegistroDataModelImpl listaCentroCostoPer) {
		this.listaCentroCostoPer = listaCentroCostoPer;
	}

	/**
	 * Retorna la lista listaCentroCostoPer
	 * 
	 * @return listaCentroCostoPer
	 */
	public RegistroDataModelImpl getListaCentroCostoPerE() {
		return listaCentroCostoPerE;
	}

	/**
	 * Asigna la lista listaCentroCostoPer
	 * 
	 * @param listaCentroCostoPer Variable a asignar en listaCentroCostoPer
	 */
	public void setListaCentroCostoPerE(RegistroDataModelImpl listaCentroCostoPerE) {
		this.listaCentroCostoPerE = listaCentroCostoPerE;
	}

	/**
	 * Retorna la lista listaAuxiliarPer
	 * 
	 * @return listaAuxiliarPer
	 */
	public RegistroDataModelImpl getListaAuxiliarPer() {
		return listaAuxiliarPer;
	}

	/**
	 * Asigna la lista listaAuxiliarPer
	 * 
	 * @param listaAuxiliarPer Variable a asignar en listaAuxiliarPer
	 */
	public void setListaAuxiliarPer(RegistroDataModelImpl listaAuxiliarPer) {
		this.listaAuxiliarPer = listaAuxiliarPer;
	}

	/**
	 * Retorna la lista listaAuxiliarPer
	 * 
	 * @return listaAuxiliarPer
	 */
	public RegistroDataModelImpl getListaAuxiliarPerE() {
		return listaAuxiliarPerE;
	}

	/**
	 * Asigna la lista listaAuxiliarPer
	 * 
	 * @param listaAuxiliarPer Variable a asignar en listaAuxiliarPer
	 */
	public void setListaAuxiliarPerE(RegistroDataModelImpl listaAuxiliarPerE) {
		this.listaAuxiliarPerE = listaAuxiliarPerE;
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
	 * Retorna la lista listaIdEmpleadoDia
	 * 
	 * @return listaIdEmpleadoDia
	 */
	public RegistroDataModelImpl getListaIdEmpleadoDia() {
		return listaIdEmpleadoDia;
	}

	/**
	 * Asigna la lista listaIdEmpleadoDia
	 * 
	 * @param listaIdEmpleadoDia Variable a asignar en listaIdEmpleadoDia
	 */
	public void setListaIdEmpleadoDia(RegistroDataModelImpl listaIdEmpleadoDia) {
		this.listaIdEmpleadoDia = listaIdEmpleadoDia;
	}

	/**
	 * Retorna la lista listaIdEmpleadoDia
	 * 
	 * @return listaIdEmpleadoDia
	 */
	public RegistroDataModelImpl getListaIdEmpleadoDiaE() {
		return listaIdEmpleadoDiaE;
	}

	/**
	 * Asigna la lista listaIdEmpleadoDia
	 * 
	 * @param listaIdEmpleadoDia Variable a asignar en listaIdEmpleadoDia
	 */
	public void setListaIdEmpleadoDiaE(RegistroDataModelImpl listaIdEmpleadoDiaE) {
		this.listaIdEmpleadoDiaE = listaIdEmpleadoDiaE;
	}

	/**
	 * Retorna la lista listaCentroCostoDia
	 * 
	 * @return listaCentroCostoDia
	 */
	public RegistroDataModelImpl getListaCentroCostoDia() {
		return listaCentroCostoDia;
	}

	/**
	 * Asigna la lista listaCentroCostoDia
	 * 
	 * @param listaCentroCostoDia Variable a asignar en listaCentroCostoDia
	 */
	public void setListaCentroCostoDia(RegistroDataModelImpl listaCentroCostoDia) {
		this.listaCentroCostoDia = listaCentroCostoDia;
	}

	/**
	 * Retorna la lista listaCentroCostoDia
	 * 
	 * @return listaCentroCostoDia
	 */
	public RegistroDataModelImpl getListaCentroCostoDiaE() {
		return listaCentroCostoDiaE;
	}

	/**
	 * Asigna la lista listaCentroCostoDia
	 * 
	 * @param listaCentroCostoDia Variable a asignar en listaCentroCostoDia
	 */
	public void setListaCentroCostoDiaE(RegistroDataModelImpl listaCentroCostoDiaE) {
		this.listaCentroCostoDiaE = listaCentroCostoDiaE;
	}

	/**
	 * Retorna la lista listaAuxiliarDia
	 * 
	 * @return listaAuxiliarDia
	 */
	public RegistroDataModelImpl getListaAuxiliarDia() {
		return listaAuxiliarDia;
	}

	/**
	 * Asigna la lista listaAuxiliarDia
	 * 
	 * @param listaAuxiliarDia Variable a asignar en listaAuxiliarDia
	 */
	public void setListaAuxiliarDia(RegistroDataModelImpl listaAuxiliarDia) {
		this.listaAuxiliarDia = listaAuxiliarDia;
	}

	/**
	 * Retorna la lista listaAuxiliarDia
	 * 
	 * @return listaAuxiliarDia
	 */
	public RegistroDataModelImpl getListaAuxiliarDiaE() {
		return listaAuxiliarDiaE;
	}

	/**
	 * Asigna la lista listaAuxiliarDia
	 * 
	 * @param listaAuxiliarDia Variable a asignar en listaAuxiliarDia
	 */
	public void setListaAuxiliarDiaE(RegistroDataModelImpl listaAuxiliarDiaE) {
		this.listaAuxiliarDiaE = listaAuxiliarDiaE;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaFrmdistpersonalaux
	 * 
	 * @return listaFrmdistpersonalaux
	 */
	public RegistroDataModelImpl getListaFrmdistpersonalaux() {
		return listaFrmdistpersonalaux;
	}

	/**
	 * Asigna la lista listaFrmdistpersonalaux
	 * 
	 * @param listaFrmdistpersonalaux Variable a asignar en listaFrmdistpersonalaux
	 */
	public void setListaFrmdistpersonalaux(RegistroDataModelImpl listaFrmdistpersonalaux) {
		this.listaFrmdistpersonalaux = listaFrmdistpersonalaux;
	}

	/**
	 * Retorna la lista listaFrmdistpersonaldiarioaux
	 * 
	 * @return listaFrmdistpersonaldiarioaux
	 */
	public RegistroDataModelImpl getListaFrmdistpersonaldiarioaux() {
		return listaFrmdistpersonaldiarioaux;
	}

	/**
	 * Asigna la lista listaFrmdistpersonaldiarioaux
	 * 
	 * @param listaFrmdistpersonaldiarioaux Variable a asignar en
	 *                                      listaFrmdistpersonaldiarioaux
	 */
	public void setListaFrmdistpersonaldiarioaux(RegistroDataModelImpl listaFrmdistpersonaldiarioaux) {
		this.listaFrmdistpersonaldiarioaux = listaFrmdistpersonaldiarioaux;
	}

	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	/**
	 * Retorna el objeto registroSubFrmDistPersonalAux
	 * 
	 * @return registroSubFrmDistPersonalAux
	 */
	public Registro getRegistroSubFrmDistPersonalAux() {
		return registroSubFrmDistPersonalAux;
	}

	/**
	 * Asigna el objeto registroSubFrmDistPersonalAux
	 * 
	 * @param registroSubFrmDistPersonalAux Variable a asignar en
	 *                                      registroSubFrmDistPersonalAux
	 */
	public void setRegistroSubFrmDistPersonalAux(Registro registroSubFrmDistPersonalAux) {
		this.registroSubFrmDistPersonalAux = registroSubFrmDistPersonalAux;
	}

	/**
	 * Retorna el objeto registroSubFrmDistPersonalDiarioAux
	 * 
	 * @return registroSubFrmDistPersonalDiarioAux
	 */
	public Registro getRegistroSubFrmDistPersonalDiarioAux() {
		return registroSubFrmDistPersonalDiarioAux;
	}

	/**
	 * Asigna el objeto registroSubFrmDistPersonalDiarioAux
	 * 
	 * @param registroSubFrmDistPersonalDiarioAux Variable a asignar en
	 *                                            registroSubFrmDistPersonalDiarioAux
	 */
	public void setRegistroSubFrmDistPersonalDiarioAux(Registro registroSubFrmDistPersonalDiarioAux) {
		this.registroSubFrmDistPersonalDiarioAux = registroSubFrmDistPersonalDiarioAux;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ADICIONALES>

	public int getHorasTrabM() {
		return horasTrabM;
	}

	public void setHorasTrabM(int horasTrabM) {
		this.horasTrabM = horasTrabM;
	}

	public boolean isDialogoVisible() {
		return dialogoVisible;
	}

	public void setDialogoVisible(boolean dialogoVisible) {
		this.dialogoVisible = dialogoVisible;
	}
}
