/*-
 * MovimientosEntreFechasControlador.java
 *
 * 1.0
 * 
 * 08/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.MovimientosporsemanasControladorEnum;
import com.sysman.presupuesto.enums.MovimientosporsemanasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario en el cual se permite generar el informe de movimeintos por semanas
 *
 * @version 1.0, 08/10/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  MovimientosEntreFechasControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
	 * Constante a nivel de clase que aloja el nombre de la compania
	 * con la que esta interactuando el usuario
	 */
	private final String cAnio;
	private  int anio;
	private String cuentaInicial;
	private final String modulo;
	/**
	 * constante que almaneca el modulo
	 */
	/**
	 * Esta variable se encarga de la cuenta Inicial
	 */
	private String cuentaFinal;
	/**
	 * Esta variable se encarga de la cuenta Final
	 * 
	 */
	private Date fechaInicial;
	/**
	 * Esta variable se encarga de almacenar la fecha inicial
	 * 
	 */
	private Date fechaFinal;
	/**
	 *Esta variable se encarga de almacenar la fecha inicial
	 *
	 */
	private StreamedContent archivoDescarga;
	/**
	 * variable de  descarga
	 */
	private final String codigoCons = "CODIGO";
	/**
	 * Constante a nivel de clase que aloja el codigo
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * Listado de registros para el combo de cuentaInicial
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	/**
	 * Listado de registros para el combo de cuentaInicial
	 */
	@EJB
	private EjbPresupuestoCeroRemote ejbPresupuesto;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de MovimientosEntreFechasControlador
	 */
	public MovimientosEntreFechasControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		fechaFinal = new Date();
		fechaInicial = new Date();
		cAnio = GeneralParameterEnum.ANO.getName();

		try {
			//2201
			numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_ENTRE_FECHAS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(LisdispabiertascuentasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial(); 
		abrirFormulario();

	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientosporsemanasControladorUrlEnum.URL3766
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cAnio, SysmanFunciones.ano(fechaInicial));

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientosporsemanasControladorUrlEnum.URL4700
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(MovimientosporsemanasControladorEnum.PARAM3.getValue(),
				cuentaInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cAnio, SysmanFunciones.ano(fechaInicial));

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir
	 * en la vista
	 *
	 */
	public void oprimirImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(ReportesBean.FORMATOS.PDF);

	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 */
	public void oprimirExcel() {
		archivoDescarga = null;
		generarInforme(ReportesBean.FORMATOS.EXCEL97);
	}



	public void generarInforme(ReportesBean.FORMATOS formato) {
		String reporte = "002159MOVIMIENTOSSEMANASIDI";

		anio =SysmanFunciones.ano(fechaInicial);

		int mes =SysmanFunciones.mes(fechaInicial);

		Date fechaPar = SysmanFunciones.sumarRestarMesesFecha(fechaInicial, -1);
		String primeroDeAno = ("01/01/" + anio);

		try {

			String fechaIni = SysmanFunciones
					.convertirAFechaCadena(fechaInicial,"dd/MM/yyyy");
			String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal,"dd/MM/yyyy");

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaPar", SysmanFunciones
					.convertirAFechaCadena(fechaPar, "dd/MM/yyyy")); 
			reemplazar.put("primeroDeAno", primeroDeAno);
			reemplazar.put("fechaInicial", SysmanFunciones
					.convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
			reemplazar.put("fechaFinal", SysmanFunciones
					.convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
			reemplazar.put("cuentaInicial",cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("anio",anio);
			reemplazar.put("mes",mes);

			//parametros 
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_FECHAINICIAL", fechaIni);
			parametros.put("PR_FECHAFINAL", fechaFin);
			parametros.put("PR_CUENTAINICIAL", cuentaInicial);
			parametros.put("PR_CUENTAFINAL", cuentaFinal);
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

			Reporteador.resuelveConsulta("002159MOVIMIENTOSSEMANASIDI",
					Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					"002159MOVIMIENTOSSEMANASIDI", parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
					.replace("s$reporte$s", reporte),
					ex.getMessage()));
			logger.error(ex.getMessage(), ex);
		}
		catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * 
	 */
	public void cambiarFechaInicial() {
		//<CODIGO_DESARROLLADO>
		cuentaInicial = null;
		cargarListaCuentaInicial();

	}
	public void cambiarFechaFinal() {
		//<CODIGO_DESARROLLADO>

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial= registroAux.getCampos().get("CODIGO").toString();

		cargarListaCuentaFinal();

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
