/*-
 * LisejecpptalingresosfldisControlador.java
 *
 * 1.0
 * 
 * 21/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.IOException;
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
import com.sysman.presupuesto.enums.LisejecpptalingresosflControladorEnum;
import com.sysman.presupuesto.enums.LisejecpptalingresosflControladorUrlEnum;
import com.sysman.presupuesto.enums.LisejecpptalingresosfldisControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * 
 * @version 1.0, 21/10/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  LisejecpptalingresosfldisControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Constante a nivel de clase que aloja el nombre de la compania
	 * con la que esta interactuando el usuario
	 */
	private final String strCodigo;
	/*
	 * Constante que almacenara la cadena "CODIGO"
	 */
	private String cuentaInicial;
	/**
	 * Esta variable se encarga de almacenar la cuenta inicial
	 */
	private String cuentaFinal;
	/**
	 * Esta variable se encarga de almacenar la cuenta Final
	 */
	private String fuenteInicial;
	/**
	 * Esta variable se encarga de almacenar la fuente inicial
	 */
	private String fuenteFinal;
	/**
	 * Esta variable se encarga de almacenar la fuente Final
	 */
	private String mesInicial;
	/**
	 * Esta variable se encarga de almacenar el mes inicial
	 */
	private String mesFinal;
	/**
	 * Esta variable se encarga de almacenar el mes final
	 */
	private String anio;
	/*
	 * Esta variable se encarga de almacenar el anio
	 */
	private String firmaEjecucion1;
    private String firmaEjecucion2;
    private String firmaEjecucion3;
    private String cargoEjecucion1;
    private String cargoEjecucion2;
    private String cargoEjecucion3;
	private StreamedContent archivoDescarga;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * Listado de registros para la lista mesInicial
	 */
	private List<Registro> listaMesInicial;
	/**
	 * Listado de registros para la lista mesFinal
	 */
	private List<Registro> listaMesFinal;
	/**
	 * Listado de registros para la listaAno
	 */
	private List<Registro> listaAno;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Listado de registros para la listaCuentaInicial
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * Listado de registros para la listaCuentaFinal
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	/**
	 * Listado de registros para la listaFuenteInicial
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 *Listado de registros para la listaFenteFinal
	 */
	private RegistroDataModelImpl listaFuenteFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de LisejecpptalingresosfldisControlador
	 */
	@EJB

    private EjbSysmanUtilRemote ejbParametro;
	
	public LisejecpptalingresosfldisControlador() {
		super();
		compania = SessionUtil.getCompania();
		strCodigo = GeneralParameterEnum.CODIGO.getName();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
		mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		try {
			numFormulario = GeneralCodigoFormaEnum.LISEJECPPTAL_INGRESOSFLDIS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(LisejecpptalingresosflControlador.class.getName())
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
		cargarListaMesInicial();
		cargarListaMesFinal();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial(); 
		cargarListaCuentaFinal(); 
		cargarListaFuenteInicial();
		cargarListaFuenteFinal();
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2204-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial(){
		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			param.put(GeneralParameterEnum.ANO.getName(),
					anio);

			listaMesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									LisejecpptalingresosfldisControladorUrlEnum.URL9268
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaMesFinal
	 * 
	 */
	public void cargarListaMesFinal(){
		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			param.put(GeneralParameterEnum.ANO.getName(),
					anio);

			listaMesFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									LisejecpptalingresosfldisControladorUrlEnum.URL9268
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaAno
	 */
	public void cargarListaAno(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);

			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									LisejecpptalingresosfldisControladorUrlEnum.URL7664
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalingresosflControladorUrlEnum.URL9889
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);
		param.put(LisejecpptalingresosflControladorEnum.PARAM1.getValue(),
				"C");

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID");
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalingresosflControladorUrlEnum.URL10828
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(LisejecpptalingresosflControladorEnum.PARAM3.getValue(),
				cuentaInicial);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);
		param.put(LisejecpptalingresosflControladorEnum.PARAM1.getValue(),
				"C");

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID");

	}
	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalingresosfldisControladorUrlEnum.URL11906
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);

		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}
	/**
	 * 
	 * Carga la lista listaFuenteFinal
	 *
	 */
	public void cargarListaFuenteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisejecpptalingresosfldisControladorUrlEnum.URL12612
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(LisejecpptalingresosflControladorEnum.PARAM8.getValue(),
				fuenteInicial);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);

		listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 */
	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		getInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		getInforme(FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	public void cargarParametros() {
        try {
           firmaEjecucion1 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 1",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            firmaEjecucion2 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 2",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            firmaEjecucion3 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "FIRMA EJECUCION 3",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            cargoEjecucion1 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 1",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
            cargoEjecucion2 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 2",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();

            cargoEjecucion3 = SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "CARGO EJECUCION 3",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                                            "")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	public void getInforme(FORMATOS formato) {
		String reporte = "002216LisEjecPptalIngresosFLIDI";
		
        try {
        	
        	
            HashMap<String, Object> reemplazar = new HashMap<>();

            cargarParametros();
            reemplazar.put("anio", anio);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            

            parametros.put("PR_NOMBREDEMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesInicial)].toUpperCase());
            parametros.put("PR_ANO", anio);
            parametros.put("PR_CARGO_EJECUCION_3", cargoEjecucion3);
            parametros.put("PR_CARGO_EJECUCION_1", cargoEjecucion1);
            parametros.put("PR_FIRMA_EJECUCION_2", firmaEjecucion2);
            parametros.put("PR_CARGO_EJECUCION_2", cargoEjecucion2);
            parametros.put("PR_FIRMA_EJECUCION_3", firmaEjecucion3);
            parametros.put("PR_FIRMA_EJECUCION_1", firmaEjecucion1);
            
            Reporteador.resuelveConsulta(reporte,
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazar, parametros);
            
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }


	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		cargarListaMesInicial();
		cargarListaMesInicial();
		cargarListaFuenteInicial();
		cargarListaFuenteFinal();
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get("ID"), " ").toString();
		cuentaFinal = null;
		cargarListaCuentaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get("ID"), " ").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();
		fuenteFinal = null;
		cargarListaFuenteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();
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
	 * Retorna la lista listaMesInicial
	 * 
	 * @return listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}
	/**
	 * Asigna la lista listaMesInicial
	 * 
	 * @param listaMesInicial
	 * Variable a asignar en  listaMesInicial
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}
	/**
	 * Retorna la lista listaMesFinal
	 * 
	 * @return listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}
	/**
	 * Asigna la lista listaMesFinal
	 * 
	 * @param listaMesFinal
	 * Variable a asignar en  listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}
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
	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}
	/**
	 * Asigna la lista listaFuenteInicial
	 * 
	 * @param listaFuenteInicial
	 * Variable a asignar en  listaFuenteInicial
	 */
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}
	/**
	 * Retorna la lista listaFuenteFinal
	 * 
	 * @return listaFuenteFinal
	 */
	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}
	/**
	 * Asigna la lista listaFuenteFinal
	 * 
	 * @param listaFuenteFinal
	 * Variable a asignar en  listaFuenteFinal
	 */
	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
