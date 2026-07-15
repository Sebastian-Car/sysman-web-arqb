/*-
 * FrejecucioninversionControlador.java
 *
 * 1.0
 * 
 * 05/03/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrejecucioninversionControladorEnum;
import com.sysman.presupuesto.enums.FrejecucioninversionControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 05/03/2021
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  FrejecucioninversionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
	 * Constante a nivel de clase que almacena el codigo del usuario
	 * que inicio sesion
	 */
	private final String cod;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * constante que almacena el codigo
	 */
	private String anio;
	/**
	 * variable de tipo cadena anio
	 */
	private String mesInicial;
	/**
	 * variable de tipo cadena mesInicial
	 */
	private String mesFinal;
	/**
	 * variable de tipo cadena mesFinal
	 */
	private String cuentaInicial;
	/**
	 * variable de tipo cadena cuentaInicial
	 */
	private String cuentaFinal;
	/**
	 * variable de tipo cadena cuentaFinal
	 */
	private String centroInicial;
	/**
	 * variable de tipo cadena centroInicial
	 */
	private String centroFinal;
	/**
	 * variable de tipo cadena centroFinal
	 */
	private String fuenteInicial;
	/**
	 * variable de tipo cadena fuenteInicial
	 */
	private String fuenteFinal;
	/**
	 * variable de tipo cadena fuenteFinal
	 */
	private String referenciaInicial;
	/**
	 * variable de tipo cadena referenciaInicial
	 */
	private String referenciafinal;
	/**
	 * variable de tipo cadena referenciaFinal
	 */
	private String auxiliarInicial;
	/**
	 * variable de tipo cadena auxiliarInicial
	 */
	private String auxiliarFinal;
	/**
	 * variable de tipo cadena auxiliarFinal
	 */
	private String nmes1;
	/**
	 * variable de tipo cadea nmes1
	 */
	private String nmes2;
	/**
	 * variable de tipo cadena nmes2
	 */
	private String observaciones;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	private List<Registro> listaAno;
	/**
	 * variable que almacena la listaAno
	 */
	private List<Registro> listamesInicial;
	/**
	 * variable que almacena la lista mesInicial
	 */
	private List<Registro> listamesFinal;
	/**
	 * variable que almacena la lista mesFinal
	 */
	private RegistroDataModelImpl listacuentaInicial;
	/**
	 * variable que almacena la listaCuentaInicial
	 */
	private RegistroDataModelImpl listacuentaFinal;
	/**
	 * variable que almacena la lista cuenaFinal
	 */
	private RegistroDataModelImpl listacentroInicial;
	/**
	 * variable que almacena la lista centroInicial
	 */
	private RegistroDataModelImpl listacentroFinal;
	/**
	 * variable que almacena la lista centroFinal
	 */
	private RegistroDataModelImpl listafuenteInicial;
	/**
	 * variable que almacena la fuenteInicial
	 */
	private RegistroDataModelImpl listafuenteFinal;
	/**
	 * variable que almacena la fuenteFinal
	 */
	private RegistroDataModelImpl listareferenciaInicial;
	/**
	 * variable que almacena la referenciaInicial
	 */
	private RegistroDataModelImpl listareferenciaFinal;
	/**
	 * variable que almacena la referenciaFinal
	 */
	private RegistroDataModelImpl listaauxiliarInicial;
	/**
	 * variable que almacena el auxiliarInicial
	 */
	private RegistroDataModelImpl listaauxiliarFinal;
	/**
	 * variable que almacena el auixilairFinal
	 */
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de FrejecucioninversionControlador
	 */
	public FrejecucioninversionControlador() {
		super();
		compania = SessionUtil.getCompania();
		cod = GeneralParameterEnum.CODIGO.getName();
		try {
			//2249;
			numFormulario = GeneralCodigoFormaEnum.FREJECUCIONINVERSIONCONTROLADOR
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex)
		{
			Logger.getLogger(FrejecucioninversionControlador.class.getName())
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
		//<CARGAR_LISTA>
		cargarListaAno();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		cargarListamesInicial();
		mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
		cargarListamesFinal();
		mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
		                                                   .parseInt(mesInicial)];
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
		                                                   .parseInt(mesInicial)
		                                                   + 1];
		InicializarCombos();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacuentaInicial(); 
		cargarListacentroInicial();
		cargarListafuenteInicial(); 
		cargarListareferenciaInicial();
		cargarListaauxiliarInicial();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	public void InicializarCombos()
	{
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		auxiliarInicial=SysmanConstantes.DEFECTOINICIAL_STRING;
		auxiliarFinal=SysmanConstantes.DEFECTOFINAL_STRING;
		referenciaInicial=SysmanConstantes.DEFECTOINICIAL_STRING;
		referenciafinal=SysmanConstantes.DEFECTOFINAL_STRING;
		
	}
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try
		{
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrejecucioninversionControladorUrlEnum.URL5600
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			Logger.getLogger(FrejecucioninversionControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listamesInicial
	 *
	 */
	public void cargarListamesInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try
		{
			listamesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrejecucioninversionControladorUrlEnum.URL4686
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			Logger.getLogger(FrejecucioninversionControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listamesFinal
	 *
	 */
	public void cargarListamesFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

		try
		{
			listamesFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrejecucioninversionControladorUrlEnum.URL5111
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			Logger.getLogger(FrejecucioninversionControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacuentaInicial
	 *
	 */
	public void cargarListacuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL5941
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID");
	}
	/**
	 * 
	 * Carga la lista listacuentaFinal
	 *
	 */
	public void cargarListacuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL6882
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(FrejecucioninversionControladorEnum.PARAM0.getValue(),
				cuentaInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID");
	}

	/**
	 * 
	 * Carga la lista listacentroInicial
	 *
	 */
	public void cargarListacentroInicial(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL7993
						.getValue());

		listacentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listacentroFinal
	 *
	 */
	public void cargarListacentroFinal(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL8700
						.getValue());

		listacentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listafuenteInicial
	 *
	 */
	public void cargarListafuenteInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL8701
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listafuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listafuenteFinal
	 *
	 */
	public void cargarListafuenteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL10036
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrejecucioninversionControladorEnum.PARAM1.getValue(),
				String.valueOf(fuenteInicial));
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listafuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listareferenciaInicial
	 *
	 */
	public void cargarListareferenciaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL10037
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listareferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listareferenciaFinal
	 *
	 */
	public void cargarListareferenciaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL10038
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrejecucioninversionControladorEnum.PARAM2.getValue(),
				String.valueOf(referenciaInicial));
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listareferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listaaxiliarInicial
	 *
	 */
	public void cargarListaauxiliarInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL6589
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaauxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	/**
	 * 
	 * Carga la lista listaauxiliarFinal
	 *
	 */
	public void cargarListaauxiliarFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrejecucioninversionControladorUrlEnum.URL6590
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrejecucioninversionControladorEnum.PARAM3.getValue(),
				String.valueOf(auxiliarInicial));
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaauxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cod);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTPdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirBTPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generaReporte(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirBTExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		 generaReporte(FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo invocado al ejecutar el comando remoto limpiarObservaciones
	 * en la vista
	 *
	 *
	 */
	public void ejecutarlimpiarObservaciones() {
		//<CODIGO_DESARROLLADO>
		observaciones = "";
	}
	private void generaReporte(FORMATOS formato)
    {
        
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal",auxiliarFinal);
            reemplazar.put("referenciaInicial",referenciaInicial);
            reemplazar.put("referenciafinal",referenciafinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesInicial-1", Integer.parseInt(mesInicial) - 1);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anio);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_OBSERVACIONES", observaciones);

            String reporte = Reporteador.resuelveConsulta(
                    "800424EjecucionGastosInversion",
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(reporte,
            		ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                    "800424EjecucionGastosInversion");
            
            
        }
        catch (JRException | IOException | SysmanException
                        | SQLException | DRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control mesInicial
	 * 
	 * 
	 */
	public void cambiarmesInicial() {
		//<CODIGO_DESARROLLADO>
		nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
		                                                   .parseInt(mesInicial)];
		cargarListamesFinal();
	}
	/**
	 * Metodo ejecutado al cambiar el control mesFinal
	 * 
	 * 
	 */
	public void cambiarmesFinal() {
		//<CODIGO_DESARROLLADO>
		nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
		                                                   .parseInt(mesFinal)];
	}
	public void cambiarAno()
	{
		// <CODIGO_DESARROLLADO>
		cargarListamesInicial();
		cargarListacuentaInicial();
		cargarListacentroInicial();
		cargarListafuenteInicial();
		cargarListaauxiliarInicial();
		cargarListareferenciaFinal();
		InicializarCombos();
		// </CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get("ID").toString();
		cargarListacuentaFinal();
		cuentaFinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get("ID").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacentroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroInicial = registroAux.getCampos().get(cod).toString();
		cargarListacentroFinal();
		centroFinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacentroFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroFinal = registroAux.getCampos().get(cod).toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listafuenteInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = registroAux.getCampos().get(cod).toString();
		cargarListafuenteFinal();
		fuenteFinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listafuenteFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilafuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = registroAux.getCampos().get(cod).toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listareferenciaInicial
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilareferenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaInicial = registroAux.getCampos().get(cod).toString();
		cargarListareferenciaFinal();
		referenciafinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listareferenciaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilareferenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciafinal = registroAux.getCampos().get(cod).toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaaxiliarInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial = registroAux.getCampos().get(cod).toString();
		cargarListaauxiliarFinal();
		auxiliarFinal = null;
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaauxiliarFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal = registroAux.getCampos().get(cod).toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return  mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}
	/**
	 * Asigna la variable  mesInicial
	 * 
	 * @param  mesInicial
	 * Variable a asignar en  mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}
	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return  mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}
	/**
	 * Asigna la variable  mesFinal
	 * 
	 * @param  mesFinal
	 * Variable a asignar en  mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}
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
	 * Retorna la variable centroInicial
	 * 
	 * @return  centroInicial
	 */
	public String getCentroInicial() {
		return centroInicial;
	}
	/**
	 * Asigna la variable  centroInicial
	 * 
	 * @param  centroInicial
	 * Variable a asignar en  centroInicial
	 */
	public void setCentroInicial(String centroInicial) {
		this.centroInicial = centroInicial;
	}
	/**
	 * Retorna la variable centroFinal
	 * 
	 * @return  centroFinal
	 */
	public String getCentroFinal() {
		return centroFinal;
	}
	/**
	 * Asigna la variable  centroFinal
	 * 
	 * @param  centroFinal
	 * Variable a asignar en  centroFinal
	 */
	public void setCentroFinal(String centroFinal) {
		this.centroFinal = centroFinal;
	}
	/**
	 * Retorna la variable fuenteInicial
	 * 
	 * @return  fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}
	/**
	 * Asigna la variable  fuenteInicial
	 * 
	 * @param  fuenteInicial
	 * Variable a asignar en  fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}
	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return  fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}
	/**
	 * Asigna la variable  fuenteFinal
	 * 
	 * @param  fuenteFinal
	 * Variable a asignar en  fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}
	/**
	 * Retorna la variable referenciaInicial
	 * 
	 * @return  referenciaInicial
	 */
	public String getReferenciaInicial() {
		return referenciaInicial;
	}
	/**
	 * Asigna la variable  referenciaInicial
	 * 
	 * @param  referenciaInicial
	 * Variable a asignar en  referenciaInicial
	 */
	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}
	/**
	 * Retorna la variable referenciafinal
	 * 
	 * @return  referenciafinal
	 */
	public String getReferenciafinal() {
		return referenciafinal;
	}
	/**
	 * Asigna la variable  referenciafinal
	 * 
	 * @param  referenciafinal
	 * Variable a asignar en  referenciafinal
	 */
	public void setReferenciafinal(String referenciafinal) {
		this.referenciafinal = referenciafinal;
	}
	/**
	 * Retorna la variable auxiliarInicial
	 * 
	 * @return  auxiliarInicial
	 */
	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}
	/**
	 * Asigna la variable  auxiliarInicial
	 * 
	 * @param  auxiliarInicial
	 * Variable a asignar en  auxiliarInicial
	 */
	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}
	/**
	 * Retorna la variable auxiliarFinal
	 * 
	 * @return  auxiliarFinal
	 */
	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}
	/**
	 * Asigna la variable  auxiliarFinal
	 * 
	 * @param  auxiliarFinal
	 * Variable a asignar en  auxiliarFinal
	 */
	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}
	/**
	 * Retorna la variable nmes1
	 * 
	 * @return  nmes1
	 */
	public String getNmes1() {
		return nmes1;
	}
	/**
	 * Asigna la variable  nmes1
	 * 
	 * @param  nmes1
	 * Variable a asignar en  nmes1
	 */
	public void setNmes1(String nmes1) {
		this.nmes1 = nmes1;
	}
	/**
	 * Retorna la variable nmes2
	 * 
	 * @return  nmes2
	 */
	public String getNmes2() {
		return nmes2;
	}
	/**
	 * Asigna la variable  nmes2
	 * 
	 * @param  nmes2
	 * Variable a asignar en  nmes2
	 */
	public void setNmes2(String nmes2) {
		this.nmes2 = nmes2;
	}
	/**
	 * Retorna la variable observaciones
	 * 
	 * @return  observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}
	/**
	 * Asigna la variable  observaciones
	 * 
	 * @param  observaciones
	 * Variable a asignar en  observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}
	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listamesInicial
	 * 
	 * @return listamesInicial
	 */
	public List<Registro> getListamesInicial() {
		return listamesInicial;
	}
	/**
	 * Asigna la lista listamesInicial
	 * 
	 * @param listamesInicial
	 * Variable a asignar en  listamesInicial
	 */
	public void setListamesInicial(List<Registro> listamesInicial) {
		this.listamesInicial = listamesInicial;
	}
	/**
	 * Retorna la lista listamesFinal
	 * 
	 * @return listamesFinal
	 */
	public List<Registro> getListamesFinal() {
		return listamesFinal;
	}
	/**
	 * Asigna la lista listamesFinal
	 * 
	 * @param listamesFinal
	 * Variable a asignar en  listamesFinal
	 */
	public void setListamesFinal(List<Registro> listamesFinal) {
		this.listamesFinal = listamesFinal;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacuentaInicial
	 * 
	 * @return listacuentaInicial
	 */
	public RegistroDataModelImpl getListacuentaInicial() {
		return listacuentaInicial;
	}
	/**
	 * Asigna la lista listacuentaInicial
	 * 
	 * @param listacuentaInicial
	 * Variable a asignar en  listacuentaInicial
	 */
	public void setListacuentaInicial(RegistroDataModelImpl listacuentaInicial) {
		this.listacuentaInicial = listacuentaInicial;
	}
	/**
	 * Retorna la lista listacuentaFinal
	 * 
	 * @return listacuentaFinal
	 */
	public RegistroDataModelImpl getListacuentaFinal() {
		return listacuentaFinal;
	}
	/**
	 * Asigna la lista listacuentaFinal
	 * 
	 * @param listacuentaFinal
	 * Variable a asignar en  listacuentaFinal
	 */
	public void setListacuentaFinal(RegistroDataModelImpl listacuentaFinal) {
		this.listacuentaFinal = listacuentaFinal;
	}
	/**
	 * Retorna la lista listacentroInicial
	 * 
	 * @return listacentroInicial
	 */
	public RegistroDataModelImpl getListacentroInicial() {
		return listacentroInicial;
	}
	/**
	 * Asigna la lista listacentroInicial
	 * 
	 * @param listacentroInicial
	 * Variable a asignar en  listacentroInicial
	 */
	public void setListacentroInicial(RegistroDataModelImpl listacentroInicial) {
		this.listacentroInicial = listacentroInicial;
	}
	/**
	 * Retorna la lista listacentroFinal
	 * 
	 * @return listacentroFinal
	 */
	public RegistroDataModelImpl getListacentroFinal() {
		return listacentroFinal;
	}
	/**
	 * Asigna la lista listacentroFinal
	 * 
	 * @param listacentroFinal
	 * Variable a asignar en  listacentroFinal
	 */
	public void setListacentroFinal(RegistroDataModelImpl listacentroFinal) {
		this.listacentroFinal = listacentroFinal;
	}
	/**
	 * Retorna la lista listafuenteInicial
	 * 
	 * @return listafuenteInicial
	 */
	public RegistroDataModelImpl getListafuenteInicial() {
		return listafuenteInicial;
	}
	/**
	 * Asigna la lista listafuenteInicial
	 * 
	 * @param listafuenteInicial
	 * Variable a asignar en  listafuenteInicial
	 */
	public void setListafuenteInicial(RegistroDataModelImpl listafuenteInicial) {
		this.listafuenteInicial = listafuenteInicial;
	}
	/**
	 * Retorna la lista listafuenteFinal
	 * 
	 * @return listafuenteFinal
	 */
	public RegistroDataModelImpl getListafuenteFinal() {
		return listafuenteFinal;
	}
	/**
	 * Asigna la lista listafuenteFinal
	 * 
	 * @param listafuenteFinal
	 * Variable a asignar en  listafuenteFinal
	 */
	public void setListafuenteFinal(RegistroDataModelImpl listafuenteFinal) {
		this.listafuenteFinal = listafuenteFinal;
	}
	/**
	 * Retorna la lista listareferenciaInicial
	 * 
	 * @return listareferenciaInicial
	 */
	public RegistroDataModelImpl getListareferenciaInicial() {
		return listareferenciaInicial;
	}
	/**
	 * Asigna la lista listareferenciaInicial
	 * 
	 * @param listareferenciaInicial
	 * Variable a asignar en  listareferenciaInicial
	 */
	public void setListareferenciaInicial(RegistroDataModelImpl listareferenciaInicial) {
		this.listareferenciaInicial = listareferenciaInicial;
	}
	/**
	 * Retorna la lista listareferenciaFinal
	 * 
	 * @return listareferenciaFinal
	 */
	public RegistroDataModelImpl getListareferenciaFinal() {
		return listareferenciaFinal;
	}
	/**
	 * Asigna la lista listareferenciaFinal
	 * 
	 * @param listareferenciaFinal
	 * Variable a asignar en  listareferenciaFinal
	 */
	public void setListareferenciaFinal(RegistroDataModelImpl listareferenciaFinal) {
		this.listareferenciaFinal = listareferenciaFinal;
	}
	/**
	 * Retorna la lista listaaxiliarInicial
	 * 
	 * @return listaaxiliarInicial
	 */
	public RegistroDataModelImpl getListaauxiliarInicial() {
		return listaauxiliarInicial;
	}
	/**
	 * Asigna la lista listaaxiliarInicial
	 * 
	 * @param listaaxiliarInicial
	 * Variable a asignar en  listaaxiliarInicial
	 */
	public void setListaaxiliarInicial(RegistroDataModelImpl listaauxiliarInicial) {
		this.listaauxiliarInicial = listaauxiliarInicial;
	}
	/**
	 * Retorna la lista listaauxiliarFinal
	 * 
	 * @return listaauxiliarFinal
	 */
	public RegistroDataModelImpl getListaauxiliarFinal() {
		return listaauxiliarFinal;
	}
	/**
	 * Asigna la lista listaauxiliarFinal
	 * 
	 * @param listaauxiliarFinal
	 * Variable a asignar en  listaauxiliarFinal
	 */
	public void setListaauxiliarFinal(RegistroDataModelImpl listaauxiliarFinal) {
		this.listaauxiliarFinal = listaauxiliarFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
