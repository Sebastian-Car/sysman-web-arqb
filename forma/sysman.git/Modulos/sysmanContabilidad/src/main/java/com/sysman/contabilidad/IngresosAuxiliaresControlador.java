/*-
 * IngresosAuxiliaresControlador.java
 *
 * 1.0
 * 
 * 15/04/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.IngresosAuxiliaresControladorEnum;
import com.sysman.contabilidad.enums.IngresosAuxiliaresControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/04/2021
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class IngresosAuxiliaresControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    /**
     * Variable que almacena el valor del Ck Centro Costo
     */
    private boolean indCentroCosto;
    /**
     * Variable que almacena el valor del Ck auxiliar
     */
    private boolean indAuxiliar;
    /**
     * Variable que almacena el valor del Ck referencia
     */
    private boolean indReferencia;
    /**
     * Variable que almacena el valor del Ck Fuente
     */
    private boolean indFuenteRecursos;
    /**
     * Variable que almacena el codigo del Centro Costo
     */
    private String centroCostoInicial;
    /**
     * Variable que almacena el codigo del Centro Costo
     */
    private String centroCostoFinal;
    /**
     * Variable que almacena el codigo del auxiliar
     */
    private String auxiliarInicial;
    /**
     * Variable que almacena el codigo del auxiliar
     */
    private String auxiliarFinal;
    /**
     * Variable que almacena el codigo de la referencia
     */
    private String referenciaInicial;
    /**
     * Variable que almacena el codigo de la referencia
     */
    private String referenciaFinal;
    /**
     * Variable que almacena el codigo de la fuente
     */
    private String fuenteInicial;
    /**
     * Variable que almacena el codigo de la fuente
     */
    private String fuenteFinal;
    /**
     * Variable que almacena el codigo de la cuenta
     */
    private String cuentaInicial;
    /**
     * Variable que almacena el codigo de la cuenta
     */
    private String cuentaFinal;
    /**
     * Variable que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * Variable que almacena la fecha final
     */
    private Date fechaFinal;
    /**
     * Variable que almacena el nombre del centro de costo
     */
    private String nombreCentroInicial;
    /**
     * Variable que almacena el nomnbre del centro de costo
     */
    private String nombreCentroFinal;
    /**
     * Variable que almacena el nombre del auxiliar
     */
    private String nombreAuxiliarInicial;
    /**
     * Variable que almacena el nombre del auxiliar
     */
    private String nombreAuxiliarFinal;
    /**
     * Variable que almacena el nombre de la referencia
     */
    private String nombreReferenciaInicial;
    /**
     * Variable que almacena el nombre de la referencia
     */
    private String nombreReferenciaFinal;
    /**
     * Variable que almacena el nombre de la fuente
     */
    private String nombreFuenteInicial;
    /**
     * Variable que almacena el nombre de la fuente
     */
    private String nombreFuenteFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista que almacena el centro de costo
     */
    private RegistroDataModelImpl listaCentroInicial;
    /**
     * Lista que almacena el centro de costo
     */
    private RegistroDataModelImpl listaCentroFinal;
    /**
     * Lista que almacena el auxiliar
     */
    private RegistroDataModelImpl listaAuxiliarInicial;
    /**
     * Lista que almacena el auxiliar
     */
    private RegistroDataModelImpl listaAuxiliarFinal;
    /**
     * Lista que almacena la referencia
     */
    private RegistroDataModelImpl listaReferenciaInicial;
    /**
     * Lista que almacena la referencia
     */
    private RegistroDataModelImpl listaReferenciaFinal;
    /**
     * Lista que almacena la fuente
     */
    private RegistroDataModelImpl listaFuenteInicial;
    /**
     * Lista que almacena la fuente
     */
    private RegistroDataModelImpl listaFuenteFinal;
    /**
     * Lista que almacena la cuenta 
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que almacena la cuenta
     */
    private RegistroDataModelImpl listaCuentaFinal;
    /**
     * Crea una nueva instancia de IngresosAuxiliaresControlador
     */
	public IngresosAuxiliaresControlador() 
	{
		super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.INGRESOS_AUXILIARES.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            IngresosAuxiliaresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
	@Override
	public void iniciarListas() {}
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
	@Override
	public void iniciarListasSub() {}
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
	@Override
	public void iniciarListasSubNulo() {}
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
    	abrirFormulario();
   	 	cargarListaCentroInicial(); 
   	 	cargarListaCentroFinal(); 
   	 	cargarListaAuxiliarInicial(); 
   	 	cargarListaAuxiliarFinal(); 
   	 	cargarListaReferenciaInicial(); 
   	 	cargarListaReferenciaFinal(); 
   	 	cargarListaFuenteInicial(); 
   	 	cargarListaFuenteFinal(); 
   	 	cargarListaCuentaInicial(); 
   	 	cargarListaCuentaFinal();
	}
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() 
    {
    	origenDatos="";	
    }
    //<METODOS_CARGAR_LISTA>	
    /**
     * 
     * Carga la lista listaCentroInicial
     *
     */
    public void cargarListaCentroInicial()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL20013
	                                                    .getValue());
	    
	    listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaCentroFinal
     *
     */
    public void cargarListaCentroFinal()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(),  SysmanFunciones.ano(fechaInicial));
	    param.put(IngresosAuxiliaresControladorEnum.CENTRO_COSTO.getValue(), centroCostoInicial);

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL20015
	                                                    .getValue());
	    
	    listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial()
    {
		 Map<String, Object> param = new TreeMap<>();
	     param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	     param.put(IngresosAuxiliaresControladorEnum.ANIO.getValue(), SysmanFunciones.ano(fechaInicial));

	     UrlBean urlBean = UrlServiceUtil.getInstance()
	                     .getUrlServiceByUrlByEnumID(
	                                     IngresosAuxiliaresControladorUrlEnum.URL23006
	                                                     .getValue());
	     
	     listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                     urlBean.getUrlConteo().getUrl(), param,
	                     true, GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     */
    public void cargarListaAuxiliarFinal()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(IngresosAuxiliaresControladorEnum.ANIO.getValue(), SysmanFunciones.ano(fechaInicial));
	    param.put(IngresosAuxiliaresControladorEnum.CODIGOFINAL.getValue(), auxiliarInicial);

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL23008
	                                                    .getValue());
	    
	    listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
	    }
    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL13001
	                                                    .getValue());
	    
	    listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
	    param.put(IngresosAuxiliaresControladorEnum.REFERENCIAINICIAL.getValue(), referenciaInicial);

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL13035
	                                                    .getValue());
	    listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     */
    public void cargarListaFuenteInicial()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
	
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL34001
	                                                    .getValue());
	    
	    listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal()
    {
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
	    param.put(IngresosAuxiliaresControladorEnum.FUENTEINICIAL.getValue(), fuenteInicial);

	    UrlBean urlBean = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                                    IngresosAuxiliaresControladorUrlEnum.URL34003
	                                                    .getValue());
	    
	    listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                    urlBean.getUrlConteo().getUrl(), param,
	                    true, GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {
		UrlBean urlBean = UrlServiceUtil.getInstance()
	            .getUrlServiceByUrlByEnumID(
	                            IngresosAuxiliaresControladorUrlEnum.URL45002
	                                            .getValue());
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
		
		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(), param,
		            true, GeneralParameterEnum.CODIGO.getName());
    }	
    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {
		if (cuentaInicial != null) {
	        UrlBean urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(
	                                        IngresosAuxiliaresControladorUrlEnum.URL45004
	                                                        .getValue());
	        
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
	
	        param.put("CUENTAINICIAL", cuentaInicial);
	
	        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param,
	                        true, GeneralParameterEnum.CODIGO.getName());
	    }
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     * 
     */
	public void cambiarFechaInicial() 
	{
		cargarListaCentroInicial();
		cargarListaCentroFinal();
		cargarListaAuxiliarInicial();
		cargarListaAuxiliarFinal();
		cargarListaReferenciaInicial();
		cargarListaReferenciaFinal();
		cargarListaFuenteInicial();
		cargarListaFuenteFinal();
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
	    }
    /**
     * Metodo ejecutado al cambiar el control IndCentroCosto
     * 
     * 
     */
	public void cambiarIndCentroCosto() 
	{
		centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	    centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	    nombreCentroInicial = null;
	    nombreCentroFinal = null;
    }
    /**
     * Metodo ejecutado al cambiar el control IndAuxiliar
     * 
     */
	public void cambiarIndAuxiliar() 
	{
		auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	    auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	    nombreAuxiliarInicial = null;
	    nombreAuxiliarFinal = null;
    }
    /**
     * Metodo ejecutado al cambiar el control IndReferencia
     * 
     */
	public void cambiarIndReferencia() 
	{
		referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	    referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	    nombreReferenciaInicial = null;
	    nombreReferenciaFinal = null;
    }
    /**
     * Metodo ejecutado al cambiar el control IndFuenteRecursos
     * 
     */
	public void cambiarIndFuenteRecursos() 
	{
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	    fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	    nombreFuenteInicial = null;
	    nombreFuenteFinal = null;
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCentroInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    centroCostoInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreCentroInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	    centroCostoFinal = null;
	    nombreCentroFinal = null;
	    cargarListaCentroFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCentroFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    centroCostoFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreCentroFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaAuxiliarInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    auxiliarInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreAuxiliarInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	    auxiliarFinal = null;
	    nombreAuxiliarFinal = null;
	    cargarListaAuxiliarFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaAuxiliarFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    auxiliarFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreAuxiliarFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaReferenciaInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    referenciaInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreReferenciaInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	    referenciaFinal = null;
	    nombreReferenciaFinal = null;
	    cargarListaReferenciaFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaReferenciaFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    referenciaFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreReferenciaFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaFuenteInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    fuenteInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreFuenteInicial = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	    fuenteFinal = null;
	    nombreFuenteFinal = null;
	    cargarListaFuenteFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaFuenteFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    fuenteFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.CODIGO.getName()),
	                    "").toString();
	    nombreFuenteFinal = SysmanFunciones.nvl(
	                    registroAux.getCampos().get(
	                                    GeneralParameterEnum.NOMBRE.getName()),
	                    "").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCuentaInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    cuentaInicial = SysmanFunciones
	                    .nvl(registroAux.getCampos().get("CODIGO"), "")
	                    .toString();
	    cargarListaCuentaFinal();
	    cuentaFinal = null;
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCuentaFinal(SelectEvent event) 
	{
		 Registro registroAux = (Registro) event.getObject();
	     cuentaFinal = SysmanFunciones
	                     .nvl(registroAux.getCampos().get("CODIGO"), "")
	                     .toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf
     * en la vista
     *
     */
	public void oprimirPdf() 
	{
         archivoDescarga=null; 
         generaReporte(FORMATOS.PDF);
    }

	private void generaReporte(FORMATOS formato) 
	{
	    try {
	        HashMap<String, Object> reemplazar = new HashMap<>();
	        String reporte = "002251RelacionIngresosAuxiliares";
	
	        String fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
	        String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);
	
	        reemplazar.put("fechaInicial", fechaIni);
	        reemplazar.put("fechaFinal", fechaFin);
	        reemplazar.put("cuentaInicial", cuentaInicial);
	        reemplazar.put("cuentaFinal", cuentaFinal);
	        
	        reemplazar.put("condFuenteRec", indFuenteRecursos
	                ? "AND DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO BETWEEN '" + fuenteInicial
	                    + "'  AND '" + fuenteFinal + "'"
	                : "");
	        
	        reemplazar.put("condCentroCosto", indCentroCosto
	                ? "AND DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO BETWEEN '" + centroCostoInicial
	                    + "'  AND '" + centroCostoFinal + "'"
	                : "");
	        
	        reemplazar.put("condReferencia", indReferencia
	                ? "AND DETALLE_COMPROBANTE_PPTAL.REFERENCIA BETWEEN '" + referenciaInicial
	                    + "'  AND '" + referenciaFinal + "'"
	                : "");
	        
	        reemplazar.put("condAuxiliar", indAuxiliar
	                ? "AND DETALLE_COMPROBANTE_PPTAL.AUXILIAR BETWEEN '" + auxiliarInicial
	                    + "'  AND '" + auxiliarFinal + "'"
	                : "");
	
	        // MANEJO DE PARAMETROS DEL REPORTE
	        Map<String, Object> parametros = new HashMap<>();
	        Reporteador.resuelveConsulta("002251RelacionIngresosAuxiliares",
	                        Integer.parseInt(SessionUtil.getModulo()),
	                        reemplazar,
	                        parametros);
	        
	        parametros.put("PR_NOMBRECOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getNombre());
	
	        parametros.put("PR_FECHAINICIAL", fechaIni);
	        parametros.put("PR_FECHAFINAL", fechaFin);
	        parametros.put("PR_CUENTAINICIAL", cuentaInicial);
	        parametros.put("PR_CUENTAFINAL", cuentaFinal);
	        parametros.put("PR_VISIBLE_FUENTE", indFuenteRecursos);
	        parametros.put("PR_VISIBLE_CENTRO", indCentroCosto);
	        parametros.put("PR_VISIBLE_REFERENCIA", indReferencia);
	        parametros.put("PR_VISIBLE_AUXILIAR", indAuxiliar);
	
	        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
	                        ConectorPool.ESQUEMA_SYSMAN, formato);
	    }
	    catch (SysmanException | ParseException | JRException | IOException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}  
	}
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     */
	public void oprimirExcel() 
	{
         archivoDescarga=null;   
         generaReporte(FORMATOS.EXCEL);
    }
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
	  @Override
	  public void abrirFormulario()
	  {
		  fechaInicial = fechaFinal = new Date();
		  cuentaInicial = "1";
		  cuentaFinal = "999999999999999999";
		  centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	      centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	      auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	      auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	      referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	      referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	      fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	      fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    }
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() 
    {
        precargarRegistro();
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes()
    {
		 registro.getCampos().put("COMPANIA", compania);
		return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues()
    {
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
		return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarDespues()
    {
		return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarAntes()
    {
		return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues()
    {
		return true;
    }
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable indCentroCosto
     * 
     * @return  indCentroCosto
     */
    public boolean getIndCentroCosto() 
    {
        return indCentroCosto;
    }
    /**
     * Asigna la variable  indCentroCosto
     * 
     * @param  indCentroCosto
     * Variable a asignar en  indCentroCosto
     */
    public void setIndCentroCosto(boolean indCentroCosto) 
    {
        this.indCentroCosto = indCentroCosto;
    }
    /**
     * Retorna la variable indAuxiliar
     * 
     * @return  indAuxiliar
     */
    public boolean getIndAuxiliar() 
    {
        return indAuxiliar;
    }
    /**
     * Asigna la variable  indAuxiliar
     * 
     * @param  indAuxiliar
     * Variable a asignar en  indAuxiliar
     */
    public void setIndAuxiliar(boolean indAuxiliar) 
    {
        this.indAuxiliar = indAuxiliar;
    }
    /**
     * Retorna la variable indReferencia
     * 
     * @return  indReferencia
     */
	public boolean getIndReferencia() 
	{
        return indReferencia;
    }
    /**
     * Asigna la variable  indReferencia
     * 
     * @param  indReferencia
     * Variable a asignar en  indReferencia
     */
    public void setIndReferencia(boolean indReferencia) 
    {
        this.indReferencia = indReferencia;
    }
    /**
     * Retorna la variable indFuenteRecursos
     * 
     * @return  indFuenteRecursos
     */
    public boolean getIndFuenteRecursos() 
    {
        return indFuenteRecursos;
    }
    /**
     * Asigna la variable  indFuenteRecursos
     * 
     * @param  indFuenteRecursos
     * Variable a asignar en  indFuenteRecursos
     */
    public void setIndFuenteRecursos(boolean indFuenteRecursos) 
    {
        this.indFuenteRecursos = indFuenteRecursos;
    }
    /**
     * Retorna la variable centroCostoInicial
     * 
     * @return  centroCostoInicial
     */
    public String getCentroCostoInicial() 
    {
        return centroCostoInicial;
    }
    /**
     * Asigna la variable  centroCostoInicial
     * 
     * @param  centroCostoInicial
     * Variable a asignar en  centroCostoInicial
     */
    public void setCentroCostoInicial(String centroCostoInicial) 
    {
        this.centroCostoInicial = centroCostoInicial;
    }
    /**
     * Retorna la variable centroCostoFinal
     * 
     * @return  centroCostoFinal
     */
    public String getCentroCostoFinal() 
    {
        return centroCostoFinal;
    }
    /**
     * Asigna la variable  centroCostoFinal
     * 
     * @param  centroCostoFinal
     * Variable a asignar en  centroCostoFinal
     */
    public void setCentroCostoFinal(String centroCostoFinal) 
    {
        this.centroCostoFinal = centroCostoFinal;
    }
    /**
     * Retorna la variable auxiliarInicial
     * 
     * @return  auxiliarInicial
     */
    public String getAuxiliarInicial() 
    {
        return auxiliarInicial;
    }
    /**
     * Asigna la variable  auxiliarInicial
     * 
     * @param  auxiliarInicial
     * Variable a asignar en  auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) 
    {
        this.auxiliarInicial = auxiliarInicial;
    }
    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return  auxiliarFinal
     */
    public String getAuxiliarFinal() 
    {
        return auxiliarFinal;
    }
    /**
     * Asigna la variable  auxiliarFinal
     * 
     * @param  auxiliarFinal
     * Variable a asignar en  auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) 
    {
        this.auxiliarFinal = auxiliarFinal;
    }
    /**
     * Retorna la variable referenciaInicial
     * 
     * @return  referenciaInicial
     */
    public String getReferenciaInicial() 
    {
        return referenciaInicial;
    }
    /**
     * Asigna la variable  referenciaInicial
     * 
     * @param  referenciaInicial
     * Variable a asignar en  referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) 
    {
        this.referenciaInicial = referenciaInicial;
    }
    /**
     * Retorna la variable referenciaFinal
     * 
     * @return  referenciaFinal
     */
    public String getReferenciaFinal() 
    {
        return referenciaFinal;
    }
    /**
     * Asigna la variable  referenciaFinal
     * 
     * @param  referenciaFinal
     * Variable a asignar en  referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) 
    {
        this.referenciaFinal = referenciaFinal;
    }
    /**
     * Retorna la variable fuenteInicial
     * 
     * @return  fuenteInicial
     */
    public String getFuenteInicial() 
    {
        return fuenteInicial;
    }
    /**
     * Asigna la variable  fuenteInicial
     * 
     * @param  fuenteInicial
     * Variable a asignar en  fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) 
    {
        this.fuenteInicial = fuenteInicial;
    }
    /**
     * Retorna la variable fuenteFinal
     * 
     * @return  fuenteFinal
     */
    public String getFuenteFinal() 
    {
        return fuenteFinal;
    }
    /**
     * Asigna la variable  fuenteFinal
     * 
     * @param  fuenteFinal
     * Variable a asignar en  fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) 
    {
        this.fuenteFinal = fuenteFinal;
    }
    /**
     * Retorna la variable cuentaInicial
     * 
     * @return  cuentaInicial
     */
    public String getCuentaInicial() 
    {
        return cuentaInicial;
    }
    /**
     * Asigna la variable  cuentaInicial
     * 
     * @param  cuentaInicial
     * Variable a asignar en  cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) 
    {
        this.cuentaInicial = cuentaInicial;
    }
    /**
     * Retorna la variable cuentaFinal
     * 
     * @return  cuentaFinal
     */
    public String getCuentaFinal() 
    {
        return cuentaFinal;
    }
    /**
     * Asigna la variable  cuentaFinal
     * 
     * @param  cuentaFinal
     * Variable a asignar en  cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) 
    {
        this.cuentaFinal = cuentaFinal;
    }
    /**
     * Retorna la variable fechaInicial
     * 
     * @return  fechaInicial
     */
    public Date getFechaInicial() 
    {
        return fechaInicial;
    }
    /**
     * Asigna la variable  fechaInicial
     * 
     * @param  fechaInicial
     * Variable a asignar en  fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) 
    {
        this.fechaInicial = fechaInicial;
    }
    /**
     * Retorna la variable fechaFinal
     * 
     * @return  fechaFinal
     */
    public Date getFechaFinal() 
    {
        return fechaFinal;
    }
    /**
     * Asigna la variable  fechaFinal
     * 
     * @param  fechaFinal
     * Variable a asignar en  fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) 
    {
    	this.fechaFinal = fechaFinal;
    }
    /**
     * Retorna la variable nombreCentroInicial
     * 
     * @return  nombreCentroInicial
     */
    public String getNombreCentroInicial() 
    {
        return nombreCentroInicial;
    }
    /**
     * Asigna la variable  nombreCentroInicial
     * 
     * @param  nombreCentroInicial
     * Variable a asignar en  nombreCentroInicial
     */
    public void setNombreCentroInicial(String nombreCentroInicial) 
    {
        this.nombreCentroInicial = nombreCentroInicial;
    }
    /**
     * Retorna la variable nombreCentroFinal
     * 
     * @return  nombreCentroFinal
     */
    public String getNombreCentroFinal() 
    {
        return nombreCentroFinal;
    }
    /**
     * Asigna la variable  nombreCentroFinal
     * 
     * @param  nombreCentroFinal
     * Variable a asignar en  nombreCentroFinal
     */
    public void setNombreCentroFinal(String nombreCentroFinal) 
    {
        this.nombreCentroFinal = nombreCentroFinal;
    }
    /**
     * Retorna la variable nombreAuxiliarInicial
     * 
     * @return  nombreAuxiliarInicial
     */
    public String getNombreAuxiliarInicial() 
    {
        return nombreAuxiliarInicial;
    }
    /**
     * Asigna la variable  nombreAuxiliarInicial
     * 
     * @param  nombreAuxiliarInicial
     * Variable a asignar en  nombreAuxiliarInicial
     */
    public void setNombreAuxiliarInicial(String nombreAuxiliarInicial) 
    {
        this.nombreAuxiliarInicial = nombreAuxiliarInicial;
    }
    /**
     * Retorna la variable nombreAuxiliarFinal
     * 
     * @return  nombreAuxiliarFinal
     */
    public String getNombreAuxiliarFinal() 
    {
        return nombreAuxiliarFinal;
    }
    /**
     * Asigna la variable  nombreAuxiliarFinal
     * 
     * @param  nombreAuxiliarFinal
     * Variable a asignar en  nombreAuxiliarFinal
     */
    public void setNombreAuxiliarFinal(String nombreAuxiliarFinal) 
    {
        this.nombreAuxiliarFinal = nombreAuxiliarFinal;
    }
    /**
     * Retorna la variable nombreReferenciaInicial
     * 
     * @return  nombreReferenciaInicial
     */
    public String getNombreReferenciaInicial() 
    {
        return nombreReferenciaInicial;
    }
    /**
     * Asigna la variable  nombreReferenciaInicial
     * 
     * @param  nombreReferenciaInicial
     * Variable a asignar en  nombreReferenciaInicial
     */
    public void setNombreReferenciaInicial(String nombreReferenciaInicial) 
    {
        this.nombreReferenciaInicial = nombreReferenciaInicial;
    }
    /**
     * Retorna la variable nombreReferenciaFinal
     * 
     * @return  nombreReferenciaFinal
     */
    public String getNombreReferenciaFinal() 
    {
        return nombreReferenciaFinal;
    }
    /**
     * Asigna la variable  nombreReferenciaFinal
     * 
     * @param  nombreReferenciaFinal
     * Variable a asignar en  nombreReferenciaFinal
     */
    public void setNombreReferenciaFinal(String nombreReferenciaFinal) 
    {
        this.nombreReferenciaFinal = nombreReferenciaFinal;
    }
    /**
     * Retorna la variable nombreFuenteInicial
     * 
     * @return  nombreFuenteInicial
     */
    public String getNombreFuenteInicial() 
    {
        return nombreFuenteInicial;
    }
    /**
     * Asigna la variable  nombreFuenteInicial
     * 
     * @param  nombreFuenteInicial
     * Variable a asignar en  nombreFuenteInicial
     */
    public void setNombreFuenteInicial(String nombreFuenteInicial) 
    {
        this.nombreFuenteInicial = nombreFuenteInicial;
    }
    /**
     * Retorna la variable nombreFuenteFinal
     * 
     * @return  nombreFuenteFinal
     */
    public String getNombreFuenteFinal() 
    {
        return nombreFuenteFinal;
    }
    /**
     * Asigna la variable  nombreFuenteFinal
     * 
     * @param  nombreFuenteFinal
     * Variable a asignar en  nombreFuenteFinal
     */
    public void setNombreFuenteFinal(String nombreFuenteFinal) 
    {
        this.nombreFuenteFinal = nombreFuenteFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() 
    {
        return archivoDescarga;
    }
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaCentroInicial
     * 
     * @return listaCentroInicial
     */
    public RegistroDataModelImpl getListaCentroInicial() 
    {
        return listaCentroInicial;
    }
    /**
     * Asigna la lista listaCentroInicial
     * 
     * @param listaCentroInicial
     * Variable a asignar en  listaCentroInicial
     */
    public void setListaCentroInicial(RegistroDataModelImpl listaCentroInicial) 
    {
        this.listaCentroInicial = listaCentroInicial;
    }
    /**
     * Retorna la lista listaCentroFinal
     * 
     * @return listaCentroFinal
     */
    public RegistroDataModelImpl getListaCentroFinal() 
    {
        return listaCentroFinal;
    }
    /**
     * Asigna la lista listaCentroFinal
     * 
     * @param listaCentroFinal
     * Variable a asignar en  listaCentroFinal
     */
    public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) 
    {
        this.listaCentroFinal = listaCentroFinal;
    }
    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() 
    {
        return listaAuxiliarInicial;
    }
    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en  listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) 
    {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }
    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() 
    {
        return listaAuxiliarFinal;
    }
    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en  listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) 
    {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }
    /**
     * Retorna la lista listaReferenciaInicial
     * 
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial() 
    {
        return listaReferenciaInicial;
    }
    /**
     * Asigna la lista listaReferenciaInicial
     * 
     * @param listaReferenciaInicial
     * Variable a asignar en  listaReferenciaInicial
     */
    public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial)
    {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }
    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() 
    {
        return listaReferenciaFinal;
    }
    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en  listaReferenciaFinal
     */
    public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) 
    {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }
    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() 
    {
        return listaFuenteInicial;
    }
    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en  listaFuenteInicial
     */
    public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) 
    {
        this.listaFuenteInicial = listaFuenteInicial;
    }
    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() 
    {
        return listaFuenteFinal;
    }
    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en  listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) 
    {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial() 
    {
        return listaCuentaInicial;
    }
    /**
     * Asigna la lista listaCuentaInicial
     * 
     * @param listaCuentaInicial
     * Variable a asignar en  listaCuentaInicial
     */
    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) 
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }
    /**
     * Retorna la lista listaCuentaFinal
     * 
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal() 
    {
        return listaCuentaFinal;
    }
    /**
     * Asigna la lista listaCuentaFinal
     * 
     * @param listaCuentaFinal
     * Variable a asignar en  listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) 
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}
