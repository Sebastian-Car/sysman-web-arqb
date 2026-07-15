/*-
 * FrmcompromisoslaboralesControlador.java
 *
 * 1.0
 * 
 * 16/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmaccionesdemejorasControladorEnum;
import com.sysman.hojasdevida.enums.FrmcompromisoslaboralesControladorEnum;
import com.sysman.hojasdevida.enums.FrmcompromisoslaboralesControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que genera los compromisos laborales y los registra en
 * la grilla del mismo por consecutivo competencia version y
 * dependencia .
 *
 * @version 1.0, 16/02/2018
 * @author jromero
 * 
 * Se agrego los campos semestrec1, semestrec2, totalc para la
 * calificación del compromiso se creo los cambiarSemestreC,
 * cambiarSemestreC2 y se creo el método sumatoriaSemestreC
 * 
 * @version 2.0, 31/05/2018
 * @author lbotia
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcompromisoslaboralesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private final String consCedulaEvaluado;
    private final String consClase;
    private final String consSucursalEvaluado;
    private final String consTipo;
    private final String consNumeroManual;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena numero evaluacion de la vista
     */
    private String numeroEvaluacion;
    /**
     * Atributo que almacena claseEvaluacion de la vista
     */
    private String claseEvaluacion;
    /**
     * Atributo que almacena tipoEvaluacion de la vista
     */
    private String tipoEvaluacion;
    /**
     * Atributo que almacena cedulaEvaluado de la vista
     */
    private String cedulaEvaluado;
    /**
     * Atributo que almacena sucursalEvaluado de la vista
     */
    private String sucursalEvaluado;
    /**
     * Atributo que almacena cedulaEvaluador de la vista
     */
    private String cedulaEvaluador;
    /**
     * Atributo que almacena sucursalEvaluador de la vista
     */
    private String sucursalEvaluador;
    /**
     * Atributo que almacena codigo del evaluado
     */
    private String codigoEvaluado;
    /**
     * Atributo que almacena la dependencia del empleado
     */
    private String dependencia;

    /**
     * Atributo que almacena el anio de la evaluacion
     */
    private String anio;

    private String manual;

    private String version;

    private boolean manejaCantidad;

    private double pesoAnterior;
    
    private int  cantidadMinima;
    
    private int cantidadMaxima;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * 
     */
    private List<Registro> listaSemestreC;
    /**
     * 
     */
    private List<Registro> listaSemestre2C;
    /**
     * Lista que almacena lista dependencia de la vista
     */
    private List<Registro> listacmbDependencia;
    /**
     * Atributo que almacena lista codigo meta de la vista
     */
    private RegistroDataModelImpl listacmbCodigoMeta;
    /**
     * Atributo que almacena lista version de la vista
     */
    private RegistroDataModelImpl listaVersion;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private long consecutivo;
    /* MAP que me recibe los datos de formulario padre */
    /* MAP que me recibe los datos de formulario padre */
    /*
     * variables que me almacenan los datos seleccionados en el combo
     * competencia
     */
    private RegistroDataModelImpl listaCompetencia;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private Object total;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmcompromisoslaboralesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmcompromisoslaboralesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCedulaEvaluado= "CEDULA_EVALUADO";
        consClase= "CLASE_EVALUACION";
        consTipo = "TIPO_EVALUACION";
        consSucursalEvaluado="SUCURSAL_EVALUADO";
        consNumeroManual="NUMERO_MANUAL";
        Map<String, Object> parametroRecibidos = SessionUtil.getFlash();
        if (parametroRecibidos != null) {
            ridDatosPersonales = (Map<String, Object>) parametroRecibidos
                            .get("rid");
            numeroEvaluacion = ridDatosPersonales.get("KEY_NUMERO_EVALUACION")
                            .toString();
            claseEvaluacion = ridDatosPersonales.get("KEY_CLASE_EVALUACION")
                            .toString();
            tipoEvaluacion = ridDatosPersonales.get("KEY_TIPO_EVALUACION")
                            .toString();
            cedulaEvaluado = ridDatosPersonales.get("KEY_CEDULA_EVALUADO")
                            .toString();
            sucursalEvaluado = ridDatosPersonales.get("KEY_SUCURSAL_EVALUADO")
                            .toString();
            cedulaEvaluador = ridDatosPersonales.get("KEY_CEDULA_EVALUADOR")
                            .toString();
            sucursalEvaluador = ridDatosPersonales.get("KEY_SUCURSAL_EVALUADOR")
                            .toString();
            dependencia = parametroRecibidos
                            .get(GeneralParameterEnum.DEPENDENCIA.getName()
                                            .toLowerCase())
                            .toString();
            codigoEvaluado = parametroRecibidos
                            .get("CODIGO_EVALUADO")
                            .toString();
            anio = parametroRecibidos
                            .get(GeneralParameterEnum.ANO.getName()
                                            .toLowerCase())
                            .toString();

        }
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_COMPROMISOS_LABORALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbDependencia();
        cargarListaCompetencia();
        cargarListacmbCodigoMeta();
        cargarListaSemestreC();
        cargarListaSemestre2C();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        enumBase = GenericUrlEnum.EV_COMPROMISOS_LABORALES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(consClase,
                        claseEvaluacion);
        parametrosListado.put(consTipo,
                        tipoEvaluacion);
        parametrosListado.put(consCedulaEvaluado,
                        cedulaEvaluado);
        parametrosListado.put(consSucursalEvaluado,
                        sucursalEvaluado);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);

    }

    public void cargarListaSemestreC() {

        Map<String, Object> param = new TreeMap<>();

        try {
            listaSemestreC = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSemestre2C() {

        Map<String, Object> param = new TreeMap<>();

        try {
            setListaSemestre2C(RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param)));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbDependencia
     *
     */
    public void cargarListacmbDependencia() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            listacmbDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL5044
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
     * Carga la lista listacmbCodigoMeta
     *
     */
    public void cargarListacmbCodigoMeta() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcompromisoslaboralesControladorUrlEnum.URL5637
                                                        .getValue());

        listacmbCodigoMeta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmcompromisoslaboralesControladorEnum.CODIGO_META
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaCompetencia
     *
     */
    public void cargarListaCompetencia() {
        try {
            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param2.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            codigoEvaluado);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL5743
                                                                            .getValue())
                                            .getUrl(), param2));

            if (reg.getCampos().get(consNumeroManual) == null
                || reg.getCampos().get("VERSION_MANUAL") == null) {
                JsfUtil.agregarMensajeAlertaDialogo(
                                idioma.getString("TB_TB4164"));
            }
            else {
                manual = reg.getCampos().get(consNumeroManual).toString();
                version = reg.getCampos().get("VERSION_MANUAL").toString();
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(consNumeroManual, manual);
            param.put("VERSION", version);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcompromisoslaboralesControladorUrlEnum.URL7080
                                                            .getValue());

            listaCompetencia = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            "EV_COMPETENCIAS"));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        dependencia = registroAux.getCampos().get("CODIGO").toString();
        registro.getCampos()
                        .put(FrmcompromisoslaboralesControladorEnum.CODIGO_META
                                        .getValue(), null);
        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get("NOMBRE"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoMeta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoMeta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_META",
                        registroAux.getCampos().get("CODIGO_META"));
        registro.getCampos().put("NOMBREMETA",
                        registroAux.getCampos().get("META"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCompetencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCompetencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("ID_COMPETENCIA",
                        registroAux.getCampos().get("CONSECUTIVO"));
        registro.getCampos().put("VERSION",
                        version);
        registro.getCampos().put(consNumeroManual,
                        manual);
        registro.getCampos().put("CONSECUTIVO_COMPETENCIA",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("DESCRIPCION",
                        registroAux.getCampos().get("DESCRIPCION"));
        registro.getCampos()
                        .put(FrmcompromisoslaboralesControladorEnum.TIPO_COMPETENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmcompromisoslaboralesControladorEnum.TIPO_COMPETENCIA
                                                                        .getValue()));

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
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
        try {
            manejaCantidad = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPROMISOS",
                            SessionUtil.getModulo(), new Date(), true));
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            numeroEvaluacion);
            param.put(GeneralParameterEnum.CLASE.getName(),
                            claseEvaluacion);
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL4217
                                                                            .getValue())
                                            .getUrl(), param));
            cantidadMinima = Integer.parseInt(reg.getCampos().get("MIN_COMPROMISOS").toString());
            cantidadMaxima = Integer.parseInt(reg.getCampos().get("MAX_COMPROMISOS").toString());
        }
        catch (SystemException e) {
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
        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            dependencia);
            registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                            service.buscarEnLista(dependencia,
                                            GeneralParameterEnum.CODIGO
                                                            .getName(),
                                            GeneralParameterEnum.NOMBRE
                                                            .getName(),
                                            listacmbDependencia));
        }else {
            pesoAnterior = Double.parseDouble(
                            registro.getCampos().get("PESO_PORCENTUAL").toString());
        }
        
       

        // </CODIGO_DESARROLLADO>
    }

    public boolean validarCompromisos(int opcion) {
        boolean estado = true;
        try {
            if (manejaCantidad) {
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put(consClase,
                                claseEvaluacion);
                param.put(consTipo,
                                tipoEvaluacion);
                param.put(consCedulaEvaluado,
                                cedulaEvaluado);
                param.put(consSucursalEvaluado,
                                sucursalEvaluado);
                param.put(GeneralParameterEnum.ANO.getName(),
                                anio);
                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmcompromisoslaboralesControladorUrlEnum.URL8574
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) >= cantidadMaxima
                    && opcion == 1) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4166").replace("#cantidad#", String.valueOf(cantidadMaxima)));
                    estado = false;
                }
                else if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) < cantidadMinima
                    && opcion == 2) {
                    estado = false;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;
    }

    public boolean validarPorcentaje() {
        double totalPor = 0;
        double peso = Double.parseDouble(
                        SysmanFunciones.nvl(registro.getCampos().get("PESO_PORCENTUAL"), 0).toString());
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(consClase,
                            claseEvaluacion);
            param.put(consTipo,
                            tipoEvaluacion);
            param.put(consCedulaEvaluado,
                            cedulaEvaluado);
            param.put(consSucursalEvaluado,
                            sucursalEvaluado);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcompromisoslaboralesControladorUrlEnum.URL1872
                                                                            .getValue())
                                            .getUrl(), param));

            totalPor = Double.parseDouble(
                            SysmanFunciones.nvl(reg.getCampos().get("PESO"), 0).toString());
            totalPor = accion.equals(ACCION_INSERTAR) ? totalPor
                : totalPor - pesoAnterior;

            if ((totalPor + peso) > 100) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4167")
                                .replace("#cantidad#", String
                                                .valueOf(100 - (totalPor))));
                return false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean insertarAntes() {

        if (validarCompromisos(1)) {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.NUMERO_EVALUACION
                                            .getValue(),
                            numeroEvaluacion);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.CLASE_EVALUACION
                                            .getValue(),
                            claseEvaluacion);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.TIPO_EVALUACION
                                            .getValue(),
                            tipoEvaluacion);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.CEDULA_EVALUADO
                                            .getValue(),
                            cedulaEvaluado);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.SUCURSAL_EVALUADO
                                            .getValue(),
                            sucursalEvaluado);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.CEDULA_EVALUADOR
                                            .getValue(),
                            cedulaEvaluador);

            registro.getCampos().put(
                            FrmcompromisoslaboralesControladorEnum.SUCURSAL_EVALUADOR
                                            .getValue(),
                            sucursalEvaluador);

            registro.getCampos().put(
                            GeneralParameterEnum.ANO.getName(),
                            anio);

            registro.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.NOMBREMETA
                                            .getValue());
            try {
                consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(

                                GenericUrlEnum.EV_COMPROMISOS_LABORALES
                                                .getTable(),
                                SysmanFunciones.concatenar("COMPANIA=''",
                                                compania,
                                                "'' AND ANO =", anio,
                                                " AND CEDULA_EVALUADO =''",
                                                cedulaEvaluado,
                                                "'' AND CEDULA_EVALUADOR= ''",
                                                cedulaEvaluador,
                                                "'' AND  SUCURSAL_EVALUADO = ''",
                                                sucursalEvaluado,
                                                "'' AND SUCURSAL_EVALUADOR =''",
                                                sucursalEvaluador, "''"),
                                FrmaccionesdemejorasControladorEnum.CONSECUTIVO
                                                .getValue(),
                                "1");
                registro.getCampos().put(
                                GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivo);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            return true;
        }
        else {
            return false;
        }

        // </CODIGO_DESARROLLADO>

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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.NUMERO_EVALUACION
                                            .getValue());
            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.CLASE_EVALUACION
                                            .getValue());

            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.TIPO_EVALUACION
                                            .getValue());

            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());

            registro.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.NOMBREMETA
                                            .getValue());
            registro.getCampos()
                            .remove(FrmcompromisoslaboralesControladorEnum.CONSECUTIVO_COMPETENCIA
                                            .getValue());

        }
        // </CODIGO_DESARROLLADO>
        return validarPorcentaje();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>

        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if (!validarCompromisos(2)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB4165").replace("#cantidad#", String.valueOf(cantidadMinima)));
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("evaluacion", numeroEvaluacion);
        parametros.put("tipo", tipoEvaluacion);
        parametros.put("claseEvaluacion", claseEvaluacion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaSemestreC
     * 
     * @return listaSemestreC
     */
    public List<Registro> getListaSemestreC() {
        return listaSemestreC;
    }

    /**
     * Asigna la lista listaSemestreC
     * 
     * @param listaSemestreC
     * Variable a asignar en listaSemestreC
     */
    public void setListaSemestreC(List<Registro> listaSemestreC) {
        this.listaSemestreC = listaSemestreC;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listacmbCodigoMeta
     * 
     * @return listacmbCodigoMeta
     */
    public RegistroDataModelImpl getListacmbCodigoMeta() {
        return listacmbCodigoMeta;
    }

    public List<Registro> getListacmbDependencia() {
        return listacmbDependencia;
    }

    public void setListacmbDependencia(List<Registro> listacmbDependencia) {
        this.listacmbDependencia = listacmbDependencia;
    }

    /**
     * Asigna la lista listacmbCodigoMeta
     * 
     * @param listacmbCodigoMeta
     * Variable a asignar en listacmbCodigoMeta
     */
    public void setListacmbCodigoMeta(
        RegistroDataModelImpl listacmbCodigoMeta) {
        this.listacmbCodigoMeta = listacmbCodigoMeta;
    }

    /**
     * Retorna la lista listaVersion
     * 
     * @return listaVersion
     */
    public RegistroDataModelImpl getListaVersion() {
        return listaVersion;
    }

    /**
     * Asigna la lista listaVersion
     * 
     * @param listaVersion
     * Variable a asignar en listaVersion
     */
    public void setListaVersion(RegistroDataModelImpl listaVersion) {
        this.listaVersion = listaVersion;
    }

    /**
     * Retorna la lista listaCompetencia
     * 
     * @return listaCompetencia
     */
    public RegistroDataModelImpl getListaCompetencia() {
        return listaCompetencia;
    }

    /**
     * Asigna la lista listaCompetencia
     * 
     * @param listaCompetencia
     * Variable a asignar en listaCompetencia
     */
    public void setListaCompetencia(RegistroDataModelImpl listaCompetencia) {
        this.listaCompetencia = listaCompetencia;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }

    /**
     * @return the listaSemestre2C
     */
    public List<Registro> getListaSemestre2C() {
        return listaSemestre2C;
    }

    /**
     * @param listaSemestre2C
     * the listaSemestre2C to set
     */
    public void setListaSemestre2C(List<Registro> listaSemestre2C) {
        this.listaSemestre2C = listaSemestre2C;
    }

}
