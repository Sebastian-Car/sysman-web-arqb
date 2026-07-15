/*-
 * PlandecompraselemsControlador.java
 *
 * 1.0
 * 
 * 08/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.ejb.EjbPlaneacionCeroRemote;
import com.sysman.planeacion.enums.PlandecompraselemsControladorEnum;
import com.sysman.planeacion.enums.PlandecompraselemsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite crear, modificar o eliminar los planes de
 * adquisiciones seg&uacute;n la dependencia.
 * 
 * @author dmaldonado
 * @version 1.0, 30/12/2015
 *
 * @author jrodrigueza
 * @version 2.0, 08/09/2017 Proceso de Refactoring.
 */
@ManagedBean
@ViewScoped
public class PlandecompraselemsControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase para almacenar el c&oacute;digo del
     * m&oacute;dulo actual
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador de aprobado.
     */
    private boolean aprobado;
    /**
     * Porcentaje de ejecuci&oacute;n.
     */
    private double porcentajeEjecucion;
    /**
     * Valor ejecutado.
     */
    private double valorEjecutado;
    /**
     * Nombre sub proyecto.
     */
    private String nombreSubProyecto;
    /**
     * Nombre de la dependencia.
     */
    private String nombreDependencia;
    /**
     * Nombre del responsable.
     */
    private String nombreResponsable;
    /**
     * Permite hacer visible el di&aacute;logo ModAprobado.
     */
    private boolean visibleModAprobado;
    /**
     * Permite habilitar/deshabilitar el campo ESTADO_VF.
     */
    private boolean bloqueadoVF;
    /**
     * Permite mostrar/ocultar el campo ESTADO_VF.
     */
    private String visibleVigencias;
    /**
     * Indica si maneja SubProyectos en el plan de compras.
     */
    private boolean manejaSubProyectos;
    /**
     * Indica si el plan de adquisiciones tiene detalles. Permite
     * bloquear/desbloquear el a&ntilde;o y el c&oacute;digo del rubro
     * presupuestal.
     */
    private boolean tieneDetalles;
    /**
     * Constante para definir la naturaleza d&eacute;bito.
     */
    private static final String NATURALEZA_DEBITO = "D";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de a&ntilde;os.
     */
    private RegistroDataModelImpl listaAno;
    /**
     * Lista de fuentes.
     */
    private RegistroDataModelImpl listaFuente;
    /**
     * Lista de responsables.
     */
    private RegistroDataModelImpl listaResponsable;
    /**
     * Lista de dependencias.
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Lista de c&oacute;digos.
     */
    private RegistroDataModelImpl listaCodigo;
    /**
     * Lista subformulario de proyectos.
     */
    private RegistroDataModelImpl listaSubProyecto;
    /**
	 * Lista de referencias vinculadas al codigo/rubro.
	 */
	private RegistroDataModelImpl listaReferencia;

	/**
	 * Lista de referencias vinculadas al codigo/rubro.
	 */
	private RegistroDataModelImpl listaAuxiliar;

	/**
	 * Lista de referencias vinculadas al codigo/rubro.
	 */
	private RegistroDataModelImpl listaCentroCosto;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Indicador DesdeSub.
     */
    private boolean desdeSub;
    /**
     * Valor programado de detalle del Plan de Compras.
     */
    private Double programadoDPC;
    /**
     * Contenido decargable para los reportes.
     */
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de EjbSysmanUtilRemote para hacer el
     * llamado a las funciones y procedimientos que se invocan dentro
     * del Controlador y se encuentran almacenadas en el paquete
     * PCK_SYSMAN_UTIL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Para el bloqueo de componentes cuando se est&aacute; creando un
     * registro.
     */
    private boolean registroNuevo;
    /**
     * Implementacion del EJB de EjbPlaneacionCeroRemote para hacer el
     * llamado a las funciones y procedimientos que se invocan dentro
     * del Controlador y se encuentran almacenadas en el paquete
     * PCK_PLANEACION
     */
    @EJB
    private EjbPlaneacionCeroRemote ejbPlaneacionCero;

    private String codigoRubro = "";
	private BigInteger codigoFuente;
	private BigInteger codigoReferencia;
	private BigInteger codigoAuxiliar;
	private BigInteger codigoCentroCosto;
	
	private boolean manAuxFuente = false;
	private boolean manAuxRef = false;
	private boolean manCCosto = false;
	private boolean manAuxGen = false;
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlandecompraselemsControlador
     */
    public PlandecompraselemsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        desdeSub = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.PLANDECOMPRASELEMS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            // <INI_ADICIONAL>
            HashMap<String, Object> parametrosEntrada = (HashMap<String, Object>) SessionUtil
                            .getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("ridPC");
                programadoDPC = Double.valueOf(parametrosEntrada
                                .get("programadoPC").toString());
                desdeSub = true;
                String accionDetalle = extraerString(
                                parametrosEntrada.get("accion"));
                accion = accionDetalle != null ? accionDetalle : accion;
            }
            else {
                programadoDPC = 0.0;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaAno();
        cargarListaResponsable();
        cargarListaDependencia();
        cargarListaCodigo();
        cargarListaFuente();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaSubProyecto();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PLAN_DE_COMPRAS;
        buscarLlave();
        asignarOrigenDatos();
        setRegistroNuevo(true);
        setNombreDependencia(null);
        setAprobado(false);
        setBloqueadoVF(true);
        setVisibleVigencias("none");
        setManejaSubProyectos("SI".equals(getParametro(
                        "MANEJA SUBPROYECTOS EN PLAN DE COMPRAS", "NO")));
        registro.getCampos()
                        .put(PlandecompraselemsControladorEnum.VLRPROGRAMADO
                                        .getValue(), 0.0);
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaSubProyecto
     */
    public void cargarListaSubProyecto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlandecompraselemsControladorUrlEnum.URL8405
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaSubProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        PlandecompraselemsControladorEnum.CODIGOSUBPROYECTO
                                        .getValue());
    }

    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlandecompraselemsControladorUrlEnum.URL8943
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaAno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * Carga la lista listaFuente
     */
    public void cargarListaFuente() {
        String urlEnumId = PlandecompraselemsControladorUrlEnum.URL34064
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.COD_RUBRO.getName(),
				codigoRubro);
        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		// Cuando es edicion
        if(registro.getCampos().get("FUENTE_DE_RECURSOS")!=null) {
			BigInteger valorFuente = new BigInteger(registro.getCampos().get("FUENTE_DE_RECURSOS").toString());
		
			if(valorFuente != null) {
				codigoFuente = valorFuente;
			}
        }
    }

    /**
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable() {
        String urlEnumId = PlandecompraselemsControladorUrlEnum.URL10308
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));
        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.RESPONSABLE.getName());
    }

    /**
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia() {
        String urlEnumId = PlandecompraselemsControladorUrlEnum.URL11422
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigo() {
        String urlEnumId = PlandecompraselemsControladorUrlEnum.URL12104
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlandecompraselemsControladorEnum.ANIO.getValue(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.NATURALEZA.getName(), NATURALEZA_DEBITO);
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
		// Cuando es edicion
		String valorRubro = SysmanFunciones.toString(registro.getCampos().get("CODIGO"));

		if(valorRubro != null) {
			codigoRubro = valorRubro;
			validarAuxiliares();
		}
    }
    
    /**
	 * Carga la lista listaReferencia
	 */
	public void cargarListaReferencia() {
		String urlEnumId = PlandecompraselemsControladorUrlEnum.URL13045
				.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				registro.getCampos().get("ANO"));

		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				codigoFuente);

		param.put(GeneralParameterEnum.COD_RUBRO.getName(),
				codigoRubro);

		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
		// Cuando es edicion
		if(registro.getCampos().get("REFERENCIA")!=null) {
			BigInteger valorActual = new BigInteger(registro.getCampos().get("REFERENCIA").toString());
		
			if(valorActual != null) {
				codigoReferencia = valorActual;
			}
		}
	}
	
	/**
	 * Carga la lista ListaCentroCosto
	 */
	public void cargarListaCentroCosto() {
		String urlEnumId = PlandecompraselemsControladorUrlEnum.URL20078
				.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				registro.getCampos().get("ANO"));

		param.put(GeneralParameterEnum.COD_RUBRO.getName(),
				codigoRubro);

		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				codigoFuente);

		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				codigoReferencia);

		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
		// Cuando es edicion
		if(registro.getCampos().get("CENTRO_COSTO")!=null) {	
			BigInteger valorActual = new BigInteger(registro.getCampos().get("CENTRO_COSTO").toString());

			if(valorActual != null) {
				codigoCentroCosto = valorActual;
			}
		}
	}


	/**
	 * Carga la lista ListaAuxiliares
	 */
	public void cargarListaAuxiliares() {
		String urlEnumId = PlandecompraselemsControladorUrlEnum.URL23056
				.getValue();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(urlEnumId);

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				registro.getCampos().get("ANO"));

		param.put(GeneralParameterEnum.COD_RUBRO.getName(),
				codigoRubro);

		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				codigoFuente);

		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				codigoReferencia); 

		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				codigoCentroCosto);

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control RequiereVigencia
     * 
     */
    public void cambiarRequiereVigencia() {
        // <CODIGO_DESARROLLADO>
        setBloqueadoVF(!((boolean) registro.getCampos()
                        .get("REQUIERE_VIGENCIA_FUTURA")));
        setVisibleVigencias(((boolean) registro.getCampos()
                        .get("REQUIERE_VIGENCIA_FUTURA")) ? "block" : "none");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Aprobado
     */
    public void cambiarAprobado() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            aprobado = !aprobado;
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3547"));
        }
        if (ACCION_MODIFICAR.equals(accion) && aprobado) {
            setVisibleModAprobado(true);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el codigo.
     */
    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>
        calcularValorEjecutado();
        cargarValoresPresupuestoP();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * modificarAprobado en la vista
     */
    public void aceptarmodificarAprobado() {
        // <CODIGO_DESARROLLADO>
        setVisibleModAprobado(false);

        HashMap<String, Object> parametro = new HashMap<>();
        parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametro.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get("ANO"));
        parametro.put(PlandecompraselemsControladorEnum.PLANAPROBADO.getValue(),
                        -1);
        parametro.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        parametro.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        Parameter parameter = new Parameter();
        parameter.setFields(parametro);
        String urlEnumId = PlandecompraselemsControladorUrlEnum.URL4269
                        .getValue();
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * modificarAprobado en la vista
     *
     */
    public void cancelarmodificarAprobado() {
        // <CODIGO_DESARROLLADO>
        setVisibleModAprobado(false);
        aprobado = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaAno
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if ("".equals(registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3548"));
            return;
        }
        registro.getCampos().put("ANO", registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        aprobado = (boolean) registroAux.getCampos()
                        .get(PlandecompraselemsControladorEnum.PLANAPROBADO
                                        .getValue());
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), null);
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), null);
        cargarListaCodigo();
        cargarListaFuente();
        calcularValorEjecutado();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(PlandecompraselemsControladorEnum.FUENTE_DE_RECURSOS
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
        codigoFuente = (BigInteger) registroAux.getCampos().get(
				GeneralParameterEnum.CODIGO.getName());
        
        cargarValoresPresupuestoP();
        cargarListaReferencia();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaSubProyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSubProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SUBPROYECTO",
                        registroAux.getCampos().get("CODIGOSUBPROYECTO"));
        setNombreSubProyecto(extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName())));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        setNombreResponsable(extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName())));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        null);
        setNombreResponsable(null);
        cargarListaResponsable();
        setNombreDependencia(extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName())));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        codigoRubro = SysmanFunciones.toString(registroAux.getCampos().get(
				GeneralParameterEnum.CODIGO.getName()));
        calcularValorEjecutado();
        cargarValoresPresupuestoP();
        validarAuxiliares();
		cargarListaFuente();

    }
    
    
	public void seleccionarFilaReferencia(SelectEvent event) {
			
			Registro registroAux = (Registro) event.getObject();
			
			registro.getCampos().put("REFERENCIA", registroAux.getCampos().get("CODIGO"));
			
			codigoReferencia =  (BigInteger) registroAux.getCampos().get(
					GeneralParameterEnum.CODIGO.getName());
	
			cargarListaCentroCosto();
			cargarValoresPresupuestoP();
	}
	
	
	/**
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * ListaCentroCosto
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vistima
	 */
	public void seleccionarFilaCentroCosto(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CENTRO_COSTO", registroAux.getCampos().get("CODIGO"));

		codigoCentroCosto = new BigInteger(registroAux.getCampos().get(
		                GeneralParameterEnum.CODIGO.getName()).toString());
		cargarListaAuxiliares();
		cargarValoresPresupuestoP();
	}
	

	/**
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * ListaAuxiliar
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vistima
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		
		registro.getCampos().put("AUXILIAR", registroAux.getCampos().get("CODIGO"));

		codigoAuxiliar = (BigInteger) registroAux.getCampos().get(
				GeneralParameterEnum.CODIGO.getName());
		
		cargarValoresPresupuestoP();
	}

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     */
    public void oprimirImprimir(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            agregarRegistroNuevo(false);
        }
        String[] campos = { "codigoPlan", "anoPlan", "dependencia" };
        String[] valores = { extraerString(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())),
                             extraerString(registro.getCampos()
                                             .get(GeneralParameterEnum.ANO
                                                             .getName())),
                             extraerString(registro.getCampos()
                                             .get(GeneralParameterEnum.DEPENDENCIA
                                                             .getName())) };
        String form = Integer
                        .toString(GeneralCodigoFormaEnum.PAR_IMP_PCCONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(form, modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerDetallePC en la vista
     */
    public void oprimirVerDetallePC(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion) && !aprobado) {
            agregarRegistroNuevo(false);
        }
        String[] campos = { "dependenciaPC", "anoPC", "nombreDependenciaPC",
                            "ridPC", "codigoPC", "programadoPC", "asignadoPC",
                            "aprobadoPC", "accion", "responsablePC",
                            "sucursalPC", "fuenteR", "referencia", "centroC", "auxiliar" };

        Object dependencia = registro.getCampos().get("DEPENDENCIA");
        Object ano = registro.getCampos().get("ANO");
        Object codigo = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        Object vlrProgramado = registro.getCampos().get("VLRPROGRAMADO");
        Object vlrAsignado = registro.getCampos()
                        .get(PlandecompraselemsControladorEnum.VLRASIGNADO
                                        .getValue());
        Object responsable = registro.getCampos()
                        .get(GeneralParameterEnum.RESPONSABLE.getName());
        Object sucursal = registro.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName());
        Object fuenteR = registro.getCampos()
                		.get(PlandecompraselemsControladorEnum.FUENTE_DE_RECURSOS.getValue());
        Object referencia = registro.getCampos()
        				.get(GeneralParameterEnum.REFERENCIA.getName());
        Object centroC = registro.getCampos()
						.get(GeneralParameterEnum.CENTRO_COSTO.getName());
        Object auxiliar = registro.getCampos()
						.get(GeneralParameterEnum.AUXILIAR.getName());
        
        Object[] valores = { dependencia, ano, nombreDependencia, css, codigo,
                             vlrProgramado, vlrAsignado, aprobado, accion,
                             responsable, sucursal, fuenteR, referencia, centroC, auxiliar };

        String numeroFormulario = Integer
                        .toString(GeneralCodigoFormaEnum.DPLANCOMPRASELEMS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.redireccionarPorFormulario(modulo, numeroFormulario,
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        evaluarRegistro();
        setRegistroNuevo(ACCION_INSERTAR.equals(accion));
        precargarRegistro();
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos()
                            .put(PlandecompraselemsControladorEnum.VLRPROGRAMADO
                                            .getValue(), 0);
            inicializarValores();
            cargarListaCodigo();
            cargarListaResponsable();
            setNombreDependencia(null);
            setNombreResponsable(null);
            setNombreSubProyecto(null);
        }
        if (!ACCION_INSERTAR.equals(accion)) {
            setNombreDependencia(traerNombreDependencia());
            cargarListaResponsable();
            setNombreResponsable(traerNombreResponsable());
            setNombreSubProyecto(traerNombreSubProyecto());
            setAprobado(traerPlanAprobado());
            cambiarRequiereVigencia();
            calcularPorcentajeEjecucion();
        }
        if (ACCION_MODIFICAR.equals(accion)) {
            setTieneDetalles(tieneDetallesPlanCompras());
            actualizarValorProgramado();
            cargarListaCodigo();
            cargarListaFuente();
			cargarListaReferencia();
			cargarListaCentroCosto();
			cargarListaAuxiliares();
        }
        else {
            setTieneDetalles(false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evalua el registro para evitar que se pueda modificar si el
     * a&ntilde;o est&aacute; aprobado, al navegar entre registros.
     */
    private void evaluarRegistro() {
        Map<String, Object> campos = registro.getCampos();
        if (!campos.isEmpty()) {
            aprobado = (boolean) campos
                            .get(PlandecompraselemsControladorEnum.PLANAPROBADO
                                            .getValue());
            accion = aprobado ? ACCION_VER : accion;
        }
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .remove(PlandecompraselemsControladorEnum.PLANAPROBADO
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // Campos que se deben remover al actualizar
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        }
        // Campos que se deben remover al actualizar y/o insertar
        registro.getCampos()
                        .remove(PlandecompraselemsControladorEnum.PLANAPROBADO
                                        .getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return
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
     * @return
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable aprobado
     * 
     * @return aprobado
     */
    public boolean getAprobado() {
        return aprobado;
    }

    /**
     * Asigna la variable aprobado
     * 
     * @param aprobado
     * Variable a asignar en aprobado
     */
    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    /**
     * Retorna la variable porcentajeEjecucion
     * 
     * @return porcentajeEjecucion
     */
    public double getPorcentajeEjecucion() {
        return porcentajeEjecucion;
    }

    /**
     * Asigna la variable porcentajeEjecucion
     * 
     * @param porcentajeEjecucion
     * Variable a asignar en porcentajeEjecucion
     */
    public void setPorcentajeEjecucion(double porcentajeEjecucion) {
        this.porcentajeEjecucion = porcentajeEjecucion;
    }

    /**
     * Retorna la variable valorEjecutado
     * 
     * @return valorEjecutado
     */
    public double getValorEjecutado() {
        return valorEjecutado;
    }

    /**
     * Asigna la variable valorEjecutado
     * 
     * @param valorEjecutado
     * Variable a asignar en valorEjecutado
     */
    public void setValorEjecutado(double valorEjecutado) {
        this.valorEjecutado = valorEjecutado;
    }

    /**
     * Retorna la variable nombreSubProyecto
     * 
     * @return nombreSubProyecto
     */
    public String getNombreSubProyecto() {
        return nombreSubProyecto;
    }

    /**
     * Asigna la variable nombreSubProyecto
     * 
     * @param nombreSubProyecto
     * Variable a asignar en nombreSubProyecto
     */
    public void setNombreSubProyecto(String nombreSubProyecto) {
        this.nombreSubProyecto = nombreSubProyecto;
    }

    /**
     * Retorna la variable nombreDependencia
     * 
     * @return nombreDependencia
     */
    public String getNombreDependencia() {
        return nombreDependencia;
    }

    /**
     * Asigna la variable nombreDependencia
     * 
     * @param nombreDependencia
     * Variable a asignar en nombreDependencia
     */
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    /**
     * Retorna la variable nombreResponsable
     * 
     * @return nombreResponsable
     */
    public String getNombreResponsable() {
        return nombreResponsable;
    }

    /**
     * Asigna la variable nombreResponsable
     * 
     * @param nombreResponsable
     * Variable a asignar en nombreResponsable
     */
    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    /**
     * @return the visibleModAprobado
     */
    public boolean isVisibleModAprobado() {
        return visibleModAprobado;
    }

    /**
     * Permite mostrar/ocultar modal de confirmaci&oacute;n para el
     * indicador Aprobado.
     * 
     * @param visibleModAprobado
     * the visibleModAprobado to set
     */
    public void setVisibleModAprobado(boolean visibleModAprobado) {
        this.visibleModAprobado = visibleModAprobado;
    }

    /**
     * @return the bloqueadoVF
     */
    public boolean isBloqueadoVF() {
        return bloqueadoVF;
    }

    /**
     * @param bloqueadoVF
     * the bloqueadoVF to set
     */
    public void setBloqueadoVF(boolean bloqueadoVF) {
        this.bloqueadoVF = bloqueadoVF;
    }

    /**
     * @return the tieneDetalles
     */
    public boolean isTieneDetalles() {
        return tieneDetalles;
    }

    /**
     * @param tieneDetalles
     * the tieneDetalles to set
     */
    public void setTieneDetalles(boolean tieneDetalles) {
        this.tieneDetalles = tieneDetalles;
    }

    /**
     * @return the visibleVigencias
     */
    public String getVisibleVigencias() {
        return visibleVigencias;
    }

    /**
     * @param visibleVigencias
     * the visibleVigencias to set
     */
    public void setVisibleVigencias(String visibleVigencias) {
        this.visibleVigencias = visibleVigencias;
    }

    /**
     * @return the manejaSubProyectos
     */
    public boolean isManejaSubProyectos() {
        return manejaSubProyectos;
    }

    /**
     * @param manejaSubProyectos
     * the manejaSubProyectos to set
     */
    public void setManejaSubProyectos(boolean manejaSubProyectos) {
        this.manejaSubProyectos = manejaSubProyectos;
    }
    
    /**
	 * @return the codigoRubro
	 */
	public String getCodigoRubro() {
		return codigoRubro;
	}

	/**
	 * @param codigoRubro the codigoRubro to set
	 */
	public void setCodigoRubro(String codigoRubro) {
		this.codigoRubro = codigoRubro;
	}

	/**
	 * @return the codigoFuente
	 */
	public BigInteger getCodigoFuente() {
		return codigoFuente;
	}

	/**
	 * @param codigoFuente the codigoFuente to set
	 */
	public void setCodigoFuente(BigInteger codigoFuente) {
		this.codigoFuente = codigoFuente;
	}

	/**
	 * @return the codigoReferencia
	 */
	public BigInteger getCodigoReferencia() {
		return codigoReferencia;
	}

	/**
	 * @param codigoReferencia the codigoReferencia to set
	 */
	public void setCodigoReferencia(BigInteger codigoReferencia) {
		this.codigoReferencia = codigoReferencia;
	}

	/**
	 * @return the codigoAuxiliar
	 */
	public BigInteger getCodigoAuxiliar() {
		return codigoAuxiliar;
	}

	/**
	 * @param codigoAuxiliar the codigoAuxiliar to set
	 */
	public void setCodigoAuxiliar(BigInteger codigoAuxiliar) {
		this.codigoAuxiliar = codigoAuxiliar;
	}

	/**
	 * @return the codigoCentroCosto
	 */
	public BigInteger getCodigoCentroCosto() {
		return codigoCentroCosto;
	}

	/**
	 * @param codigoCentroCosto the codigoCentroCosto to set
	 */
	public void setCodigoCentroCosto(BigInteger codigoCentroCosto) {
		this.codigoCentroCosto = codigoCentroCosto;
	}
    

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

	/**
     * @return the listaAno
     */
    public RegistroDataModelImpl getListaAno() {
        return listaAno;
    }

    /**
     * @param listaAno
     * the listaAno to set
     */
    public void setListaAno(RegistroDataModelImpl listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * @return the listaFuente
     */
    public RegistroDataModelImpl getListaFuente() {
        return listaFuente;
    }

    /**
     * @param listaFuente
     * the listaFuente to set
     */
    public void setListaFuente(RegistroDataModelImpl listaFuente) {
        this.listaFuente = listaFuente;
    }

    /**
     * @return the listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    /**
     * @param listaResponsable
     * the listaResponsable to set
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    /**
     * @return the listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * @param listaDependencia
     * the listaDependencia to set
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaSubProyecto
     * 
     * @return listaSubProyecto
     */
    public RegistroDataModelImpl getListaSubProyecto() {
        return listaSubProyecto;
    }

    /**
     * Asigna la lista listaSubProyecto
     * 
     * @param listaSubProyecto
     * Variable a asignar en listaSubProyecto
     */
    public void setListaSubProyecto(RegistroDataModelImpl listaSubProyecto) {
        this.listaSubProyecto = listaSubProyecto;
    }
    
    /**
	 * @return the listaReferencia
	 */
	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}

	/**
	 * @param listaReferencia the listaReferencia to set
	 */
	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	/**
	 * @return the listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	/**
	 * @param listaAuxiliar the listaAuxiliar to set
	 */
	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	/**
	 * @return the listaCentroCosto
	 */
	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}

	/**
	 * @param listaCentroCosto the listaCentroCosto to set
	 */
	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>

    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_ADICIONALES>

    
	/**
     * @return the listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    /**
	 * @return the manAuxFuente
	 */
	public boolean isManAuxFuente() {
		return manAuxFuente;
	}

	/**
	 * @param manAuxFuente the manAuxFuente to set
	 */
	public void setManAuxFuente(boolean manAuxFuente) {
		this.manAuxFuente = manAuxFuente;
	}

	/**
	 * @return the manAuxRef
	 */
	public boolean isManAuxRef() {
		return manAuxRef;
	}

	/**
	 * @param manAuxRef the manAuxRef to set
	 */
	public void setManAuxRef(boolean manAuxRef) {
		this.manAuxRef = manAuxRef;
	}

	/**
	 * @return the manCCosto
	 */
	public boolean isManCCosto() {
		return manCCosto;
	}

	/**
	 * @param manCCosto the manCCosto to set
	 */
	public void setManCCosto(boolean manCCosto) {
		this.manCCosto = manCCosto;
	}

	/**
	 * @return the manAuxGen
	 */
	public boolean isManAuxGen() {
		return manAuxGen;
	}

	/**
	 * @param manAuxGen the manAuxGen to set
	 */
	public void setManAuxGen(boolean manAuxGen) {
		this.manAuxGen = manAuxGen;
	}

	/**
     * @param listaCodigo
     * the listaCodigo to set
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the registroNuevo
     */
    public boolean isRegistroNuevo() {
        return registroNuevo;
    }

    /**
     * @param registroNuevo
     * the registroNuevo to set
     */
    public void setRegistroNuevo(boolean registroNuevo) {
        this.registroNuevo = registroNuevo;
    }
    // </SET_GET_ADICIONALES>

    // <METODOS_ADICIONALES>

    /**
     * Trae el nombre de la dependencia asociado al registro cargado.
     * 
     * @return nombre de dependencia seleccionada.
     */
    private String traerNombreDependencia() {
        String origenControl = GeneralParameterEnum.DEPENDENCIA.getName();
        String nombreFiltro = GeneralParameterEnum.CODIGO.getName();
        String nombreCampo = GeneralParameterEnum.NOMBRE.getName();
        return traerCampoListaGrande(listaDependencia, origenControl,
                        nombreFiltro, nombreCampo);
    }

    /**
     * Trae el nombre del responsable asociado al registro cargado.
     * 
     * @return nombre del responsable seleccionado.
     */
    private String traerNombreResponsable() {
        String origenControl = GeneralParameterEnum.RESPONSABLE.getName();
        String nombreFiltro = GeneralParameterEnum.RESPONSABLE.getName();
        String nombreCampo = GeneralParameterEnum.NOMBRE.getName();
        return traerCampoListaGrande(listaResponsable, origenControl,
                        nombreFiltro, nombreCampo);
    }

    /**
     * Trae el nombre del proyecto asociado al registro cargado.
     * 
     * @return nombre del proyecto seleccionado.
     */
    private String traerNombreSubProyecto() {
        String origenControl = PlandecompraselemsControladorEnum.SUBPROYECTO
                        .getValue();
        String nombreFiltro = PlandecompraselemsControladorEnum.CODIGOSUBPROYECTO
                        .getValue();
        String nombreCampo = GeneralParameterEnum.NOMBRE.getName();
        return traerCampoListaGrande(listaSubProyecto, origenControl,
                        nombreFiltro, nombreCampo);
    }

    /**
     * Trae el valor de una columna definida en una consulta de un
     * combo grande cargado previamente.
     * 
     * @param listaGrande
     * lista tipo combo grande.
     * @param origenControl
     * nombre del campo en el origen de datos
     * @param nombreFiltro
     * nombre del campo en la consulta
     * @param nombreCampo
     * nombre de la columna a la que se quiere traer el valor
     * @return valor del campo ingresado por par&aacute;metro.
     */
    private String traerCampoListaGrande(RegistroDataModelImpl listaGrande,
        String origenControl, String nombreFiltro, String nombreCampo) {
        String nombre = null;
        Map<String, Object> params = new HashMap<>();
        Object proyecto = registro.getCampos().get(origenControl);
        params.put(nombreFiltro, proyecto);
        try {
            Registro reg = listaGrande.getRegistroUnico(params);
            if (reg != null) {
                Map<String, Object> campos = reg.getCampos();
                if (!campos.isEmpty()) {
                    nombre = extraerString(campos.get(nombreCampo));
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return nombre;
    }

    /**
     * Verifica si para el a&ntilde;o seleccionado est&aacute;
     * aprobado el plan de compras.
     * 
     * @return valor del indicador PLANAPROBADO
     */
    private boolean traerPlanAprobado() {
        boolean planAprobado = false;
        Map<String, Object> params = new HashMap<>();
        Object ano = registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName());
        params.put(GeneralParameterEnum.NUMERO.getName(), ano);
        try {
            Map<String, Object> campos = listaAno.getRegistroUnico(params)
                            .getCampos();
            if (!campos.isEmpty()) {
                planAprobado = (boolean) campos
                                .get(PlandecompraselemsControladorEnum.PLANAPROBADO
                                                .getValue());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return planAprobado;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Extrae el BigDecimal que representa el objeto.
     * 
     * @param object
     * Un Objeto
     * @return objeto como BigDecimal
     */
    private BigDecimal extraerDecimal(Object object) {
        if (object == null) {
            return new BigDecimal(0);
        }
        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        else {
            return new BigDecimal(extraerString(object));
        }
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Calcula y graba el valor de VALORTOTALCOMPRADO.
     */
    private void calcularValorEjecutado() {
        BigDecimal valor = new BigDecimal(0);
        int anio = Integer.parseInt(
                        extraerString(registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName())));
        String codigo = extraerString(registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        try {
            valor = ejbPlaneacionCero.calcularValorEjecutado(compania, anio,
                            codigo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        valorEjecutado = valor.doubleValue();
    }

    /**
     * Carga de valores presupuestales.
     */
    private void cargarValoresPresupuestoP() {
        BigDecimal valorAdicion;
        BigDecimal valorReduccion;
        BigDecimal valorTraslado;
        BigDecimal valorApropiado;
        BigDecimal apropiacionInicial = new BigDecimal(0);
        BigDecimal apropiacionDefinitiva = new BigDecimal(0);

        String reporte = "800122V_RESUMENPPTO_P";
        Map<String, Object> reemplazos = new HashMap<>();
        Object anio = registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName());
        reemplazos.put("anio", anio);
        String mesApropiacion = getParametro(
                        "NUMERO DE MES APROPIACION PLAN DE COMPRAS", "0");
        reemplazos.put("mes", mesApropiacion);
        Object rubro = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        reemplazos.put("rubro", rubro == null ? "" : rubro);
        Object fuente = registro.getCampos()
                        .get(PlandecompraselemsControladorEnum.FUENTE_DE_RECURSOS
                                        .getValue());
        Object referencia = registro.getCampos()
                		.get(GeneralParameterEnum.REFERENCIA
                                		.getName());
        Object centroC = registro.getCampos()
                		.get(GeneralParameterEnum.CENTRO_COSTO
                                		.getName());
        Object auxiliar = registro.getCampos()
                		.get(GeneralParameterEnum.AUXILIAR
                                		.getName());
        reemplazos.put("fuente", fuente == null ? "" : fuente);
        reemplazos.put("referencia", referencia == null ? "" : referencia);
        reemplazos.put("centroC", centroC == null ? "" : centroC);
        reemplazos.put("auxiliar", auxiliar == null ? "" : auxiliar);
        String consulta = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo), reemplazos);
        Registro regPlanCompra = service.getRegistro(
                        ConectorPool.ESQUEMA_SYSMAN,
                        consulta);
        if (regPlanCompra != null) {
            Map<String, Object> planCompra = regPlanCompra.getCampos();
            valorApropiado = extraerDecimal(
                            planCompra.get(PlandecompraselemsControladorEnum.APROPIADO
                                            .getValue()));
            valorAdicion = extraerDecimal(planCompra
                            .get(PlandecompraselemsControladorEnum.ADICION
                                            .getValue()));
            valorReduccion = extraerDecimal(planCompra
                            .get(PlandecompraselemsControladorEnum.REDUCCION
                                            .getValue()));
            valorTraslado = extraerDecimal(planCompra
                            .get(PlandecompraselemsControladorEnum.TRASLADO
                                            .getValue()));
            apropiacionDefinitiva = extraerDecimal(
                            planCompra.get(PlandecompraselemsControladorEnum.APRDEFINITIVA
                                            .getValue()));
            apropiacionInicial = apropiacionInicial.add(valorApropiado);

        }
        else {
            valorAdicion = new BigDecimal(0);
            valorReduccion = new BigDecimal(0);
            valorTraslado = new BigDecimal(0);
        }
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRADICION
                        .getValue(), valorAdicion);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRREDUCCION
                        .getValue(), valorReduccion);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRTRASLADO
                        .getValue(), valorTraslado);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRAPRINICIAL
                        .getValue(), apropiacionInicial);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRASIGNADO
                        .getValue(), apropiacionDefinitiva);
        calcularApropiacionProgramado();
    }

    /**
     * Inicializa los valores num&eacute;ricos en cero.
     */
    private void inicializarValores() {
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRADICION
                        .getValue(), 0);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRREDUCCION
                        .getValue(), 0);

        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRTRASLADO
                        .getValue(), 0);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRAPRINICIAL
                        .getValue(), 0);

        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRASIGNADO
                        .getValue(), 0);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRDIFERENCIA
                        .getValue(), 0);
    }

    /**
     * Calcula el valor del campo VLRDIFERENCIA.
     */
    private void calcularApropiacionProgramado() {
        BigDecimal asignado = extraerDecimal(registro.getCampos()
                        .get(PlandecompraselemsControladorEnum.VLRASIGNADO
                                        .getValue()));
        BigDecimal programado = extraerDecimal(registro.getCampos()
                        .get(PlandecompraselemsControladorEnum.VLRPROGRAMADO
                                        .getValue()));
        BigDecimal diferencia = asignado.subtract(programado);
        registro.getCampos().put(PlandecompraselemsControladorEnum.VLRDIFERENCIA
                        .getValue(), diferencia);
    }

    /**
     * Calcula el porcentaje de ejecuci&oacute;n.
     */
    private void calcularPorcentajeEjecucion() {
        BigDecimal asignado = extraerDecimal(registro.getCampos()
                        .get(PlandecompraselemsControladorEnum.VLRASIGNADO
                                        .getValue()));
        if (asignado.compareTo(BigDecimal.ZERO) == 0) {
            porcentajeEjecucion = 0;
        }
        else {
            BigDecimal ejecutado = extraerDecimal(registro.getCampos()
                            .get(PlandecompraselemsControladorEnum.VLREJECUTADO
                                            .getValue()));
            porcentajeEjecucion = (ejecutado.doubleValue() * 100)
                / asignado.doubleValue();
        }
    }

    /**
     * Actualiza el valor progaramado del rubro, en caso de que
     * aplique.
     */
    private void actualizarValorProgramado() {
        if (desdeSub) {
            Double programado = extraerDecimal(registro.getCampos()
                            .get(PlandecompraselemsControladorEnum.VLRPROGRAMADO
                                            .getValue())).doubleValue();
            if (programado.compareTo(programadoDPC) != 0) {
                HashMap<String, Object> parametro = new HashMap<>();
                parametro.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametro.put(GeneralParameterEnum.ANO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName()));
                parametro.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));
                parametro.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                parametro.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parametro.put(GeneralParameterEnum.VALOR.getName(),
                                programadoDPC);
                Parameter parameter = new Parameter();
                parameter.setFields(parametro);
                String urlEnumId = PlandecompraselemsControladorUrlEnum.URL133380
                                .getValue();
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(urlEnumId);
                try {
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), parameter);
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
                registro.getCampos()
                                .put(PlandecompraselemsControladorEnum.VLRPROGRAMADO
                                                .getValue(), programadoDPC);

                calcularApropiacionProgramado();
            }
        }
        desdeSub = false;
    }

    /**
     * Verifica si el plan de adquisiciones tiene detalles.
     * 
     * @return verdadero si el plan de adquisiciones tiene detalles.
     */
    private boolean tieneDetallesPlanCompras() {
        boolean rta = false;
        Map<String, Object> campos = registro.getCampos();
        int anio = Integer.parseInt(extraerString(
                        campos.get(GeneralParameterEnum.ANO.getName())));
        String rubro = extraerString(
                        campos.get(GeneralParameterEnum.CODIGO.getName()));
        String dependencia = extraerString(
                        campos.get(GeneralParameterEnum.DEPENDENCIA.getName()));
        try {
            rta = ejbPlaneacionCero.tieneDetallesPlanCompras(compania, anio,
                            rubro, dependencia);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }
    
    /**
	 * Metodo que valida los auxliares que maneja determinado rubro
	 */
	public void validarAuxiliares() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				registro.getCampos().get("ANO"));

		param.put(GeneralParameterEnum.CUENTA.getName(),
				codigoRubro);

		Registro regAux;
		
		try {
			regAux = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PlandecompraselemsControladorUrlEnum.URL45069.getValue())
							.getUrl(),
							param));
			
			manAuxFuente = (boolean) regAux.getCampos().get(
					GeneralParameterEnum.MAN_AUX_FUE.getName());
			
			manAuxRef = (boolean) regAux.getCampos().get(
					GeneralParameterEnum.MAN_AUX_REF.getName());

			manCCosto = (boolean) regAux.getCampos().get(
					GeneralParameterEnum.MAN_CEN_CTO.getName());
			
			manAuxGen = (boolean) regAux.getCampos().get(
					GeneralParameterEnum.MAN_AUX_GEN.getName());

			if(!manAuxGen) {
	        	registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), SysmanConstantes.CONS_AUXILIAR);
			}
	        if(!manAuxFuente) {
	        	registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(), SysmanConstantes.CONS_FUENTE);
			}
	        if(!manAuxRef) {
	        	registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
			}
	        if(!manCCosto) {
	        	registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
    // </METODOS_ADICIONALES>

}
