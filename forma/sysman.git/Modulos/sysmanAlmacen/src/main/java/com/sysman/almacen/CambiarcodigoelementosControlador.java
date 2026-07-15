/*-
 * CambiarcodigoelementosControlador.java
 *
 * 1.0
 * 
 * 05/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.enums.CambiarcodigoelementosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase muestra el formulario "1809" que se encuentra en la ruta "PANEL PRINCIPAL\ALMACÉN\UTILIDADES\PROCESOS/HERRAMIENTAS\CAMBIAR CÓDIGO A LOS ELEMENTOS DEL CATALOGO"
 *
 * @version 1, 08/06/2018 16:59:40 -- Modificado por mvenegas
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class CambiarcodigoelementosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */

    private StreamedContent archivoDescarga;

    /**
     * variable que almacena el modulo
     */
    private String modulo = SessionUtil.getModulo();

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Variable que almacena los registros del listado listaSERIE
     */
    private RegistroDataModelImpl listaSERIE;

    /**
     * Variable que almacena los registros del listado listaSERIE
     */
    private RegistroDataModelImpl listaSERIEE;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable que almacena los registros del listado listaELEMENTO
     */
    private RegistroDataModelImpl listaELEMENTO;
    /**
     * Variable que almacena los registros del listado listaELEMENTOE
     */
    private RegistroDataModelImpl listaELEMENTOE;
    /**
     * Variable que almacena los registros del listado listaNUEVOELEMENTO
     */
    private RegistroDataModelImpl listaNUEVOELEMENTO;
    /**
     * Variable que almacena los registros del listado listaNUEVOELEMENTOE
     */
    private RegistroDataModelImpl listaNUEVOELEMENTOE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Variable que almacena los registros del listado listaDcambiosdeplacasub
     */
    private RegistroDataModelImpl listaDcambiosdeplacasub;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    /**
     * Esta variable controla el lugar desde el cual se debe mostrar cada mesaje
     */

    private int origen;
    // </DECLARAR_ADICIONALES>
    /**
     * Esta variable bloquea los los campos elemento y serie del subformulario
     */
    private boolean bloquearLLavesSub;

    /**
     * 
     */
    private int indiceDcambiosdeplacasub;

    /**
     * Esta variable cambia el titulo del formularo segun la opcion de menu
     */
    private String tituloMayuscula;
    private String tituloMinuscula;

    /**
     * Estas variables permiten mostrar los mensajes que se encuentran en la parte superior del formulario en color rojo
     */
    private boolean mostrarDeplaca;
    private boolean mostrarDeCodigo;

    /**
     * Variables para almacenar textos quemdos
     */
    private String cCambio;
    private String cCodigoElemento;
    private String cCambioMenor;
    private String cSiguientePlaca;
    private String cSiguienteConsecutivo;

    /**
     * Esta variable me determina si lo que este modificando es una addion, reduccion o solicitud
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    /**
     * Esta variable permite bloquear el subformulario
     */
    private boolean bloquearSub;

    /**
     * Esta variable almacena la palabra PLACANUEVA
     */
    private String cPlacaNueva;

    /**
     * Esta variable almacena la palabra REALIZADO
     */
    private String cRealizado;

    /**
     * Crea una nueva instancia de CambiarcodigoelementosControlador
     */
    public CambiarcodigoelementosControlador() {
        super();
        compania = SessionUtil.getCompania();
        bloquearLLavesSub = true;
        indiceDcambiosdeplacasub = 0;
        origen = 0;
        disenarFormularioPorMenu();
        cCambio = "CAMBIO";
        cCodigoElemento = "CODIGOELEMENTO";
        cCambioMenor = "cambio";
        cSiguientePlaca = "SIGUIENTEPLACA";
        cSiguienteConsecutivo = "SIGUIENTECONSECUTIVO";
        bloquearSub = true;
        cPlacaNueva = "PLACANUEVA";
        cRealizado = "REALIZADO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIAR_CODIGO_ELEMENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaELEMENTO();
        cargarListaELEMENTOE();
        cargarListaNUEVOELEMENTO();
        cargarListaNUEVOELEMENTOE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    public void disenarFormularioPorMenu() {
        String menuActual = SysmanFunciones.toString(SessionUtil.getMenuActual());

        switch (menuActual) {
        case "10060318": // menu codigo
            tituloMayuscula = "CAMBIAR CODIGO DE ELEMENTO E INICIO DE ALMACÉN";
            tituloMinuscula = "Cambiar código de elemento e inicio de Almacén";
            mostrarDeCodigo = true;
            mostrarDeplaca = false;
            break;
        case "10060319": // menu placa
            tituloMayuscula = "CAMBIAR CÓDIGO DE ELEMENTO A UNA PLACA";
            tituloMinuscula = "Cambiar código de elemento a una placa";
            mostrarDeCodigo = false;
            mostrarDeplaca = true;
            break;
        default:

            break;
        }
    }

    public void activarEdicionDcambiosdeplacasub(Registro reg) {
        bloquearLLavesSub = true;
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaDcambiosdeplacasub();
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
        listaDcambiosdeplacasub = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CAMBIOSDEPLACA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * 
     * Carga la lista listaDcambiosdeplacasub
     *
     */
    public void cargarListaDcambiosdeplacasub() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(cCambio, registro.getCampos().get(cCambio));

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CAMBIOSDEPLACA
                                                            .getGridKey());

            listaDcambiosdeplacasub = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.D_CAMBIOSDEPLACA
                                                            .getTable()));

        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaSERIE
     *
     */
    public void cargarListaSERIE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        "141096");

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()));

        listaSERIE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());

    }

    /**
     * 
     * Carga la lista listaSERIE
     *
     * 
     */
    public void cargarListaSERIEE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        "141096");

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()));

        listaSERIEE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());

    }

    /**
     * 
     * Carga la lista listaELEMENTO
     *
     */
    public void cargarListaELEMENTO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarcodigoelementosControladorUrlEnum.URL001.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaELEMENTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    /**
     * 
     * Carga la lista listaELEMENTO
     *
     */
    public void cargarListaELEMENTOE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarcodigoelementosControladorUrlEnum.URL001.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaELEMENTOE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    /**
     * 
     * Carga la lista listaNUEVOELEMENTO
     *
     */
    public void cargarListaNUEVOELEMENTO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarcodigoelementosControladorUrlEnum.URL001.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNUEVOELEMENTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    /**
     * 
     * Carga la lista listaNUEVOELEMENTO
     *
     */
    public void cargarListaNUEVOELEMENTOE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiarcodigoelementosControladorUrlEnum.URL001.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNUEVOELEMENTOE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaELEMENTO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaELEMENTO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("ELEMENTO",
                        registroAux.getCampos().get(cCodigoElemento));

        registroSub.getCampos().put(GeneralParameterEnum.SERIE.getName(), null);
        cargarListaSERIE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaELEMENTO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaELEMENTOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigoElemento);
        registroSub.getCampos().put(GeneralParameterEnum.SERIE.getName(), null);
        cargarListaSERIEE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaSERIE
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSERIE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.SERIE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaSERIE
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSERIEE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .toString(registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNUEVOELEMENTO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNUEVOELEMENTO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("NUEVOELEMENTO",
                        registroAux.getCampos().get(cCodigoElemento));

        registroSub.getCampos().put("TIPO",
                        registroAux.getCampos().get("TIPO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaNUEVOELEMENTO
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNUEVOELEMENTOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigoElemento).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btn2 en la vista
     *
     *
     */
    public void oprimirbtn2() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbAlmacenCuatro.cambiarPlacas(compania, SysmanFunciones.toString(SessionUtil.getUser().getCodigo()),
                            Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cCambio))), 1);

            JsfUtil.agregarMensajeInformativo("Proceso Ejecutado Correctamente.");

            registro.getCampos().put(cRealizado, true);
            cargarRegistro();

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btn2 en la vista
     *
     *
     */
    public void oprimirbtnEjecutar() {
        // <CODIGO_DESARROLLADO>
        try {

            ejbAlmacenCuatro.cambiarPlacas(compania, SysmanFunciones.toString(SessionUtil.getUser().getCodigo()),
                            Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cCambio))), 1);
            JsfUtil.agregarMensajeInformativo("Proceso Ejecutado Correctamente.");

            registro.getCampos().put(cRealizado, true);
            cargarRegistro();

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btn1 en la vista
     *
     *
     */
    public void oprimirbtn1() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "opcion", cCambioMenor };

        Object[] valores = { SessionUtil.getMenuActual(),
                        registro.getCampos().get(cCambio) };

        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(1813),
                        SessionUtil.getModulo(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariobtn1(SelectEvent event) {
        if (event.getObject() != null) {
            cargarRegistro(css, ACCION_MODIFICAR);
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btn0 en la vista
     *
     *
     */
    public void oprimirbtn0() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbAlmacenCuatro.actualizarIdentificadoresPlaca(compania,
                            SessionUtil.getUser().getCodigo());

            /**
             * Proceso para exportar varios reportes.
             */
            String[] consultas = new String[2];

            consultas[0] = Reporteador.resuelveConsulta(
                            "000000PlacasInconsistentes",
                            Integer.valueOf(SessionUtil.getModulo()),
                            null);

            consultas[1] = Reporteador.resuelveConsulta(
                            "000000ConsecutivosInconsistentes",
                            Integer.valueOf(SessionUtil.getModulo()),
                            null);

            String[] nombresArchivos = new String[2];

            nombresArchivos[0] = "Placas Inconsistentes";
            nombresArchivos[1] = "Consecutivos Inconsistentes";

            archivoDescarga = JsfUtil.exportarComprimidoHojaDatosStreamed(
                            nombresArchivos, consultas,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);

            origen = 1;
            ejecutaralertas();

        }
        catch (JRException | IOException | SQLException | DRException
                        | SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnImprimir en la vista
     *
     *
     */
    public void oprimirbtnImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado para generar el informe
     *
     *
     */

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {
            String reporte = "001798CAMBIOSDEELEMENTO";

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put(cCambioMenor, registro.getCampos().get(cCambio));

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_COMPANIA", compania);
            parametros.put(cCambioMenor, registro.getCampos().get(cCambio));

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Dcambiosdeplacasub
     * 
     */
    public void agregarRegistroSubDcambiosdeplacasub() {
        try {

            /**
             * En este paso se valida que los datos ingresados en el subformulario de cambios si sean correctos
             */
            if (validarDatosDeCambio(registroSub)) {

                registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                registroSub.getCampos().put(cCambio,
                                registro.getCampos().get(cCambio));
                registroSub.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                registroSub.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                registroSub.getCampos().remove("TIPO");

                if (registroSub.getCampos().get(cPlacaNueva) == null || registroSub.getCampos().get("PLACANUEVA").toString().isEmpty()) {
                    registroSub.getCampos().put(cPlacaNueva, registroSub.getCampos().get("SERIE"));
                }

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.D_CAMBIOSDEPLACA
                                                                .getCreateKey());

                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                registroSub.getCampos());

                cargarListaDcambiosdeplacasub();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Dcambiosdeplacasub
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubDcambiosdeplacasub(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {
            /**
             * En este paso se valida que los datos ingresados en el subformulario de cambios si sean correctos
             */
            if (validarDatosDeCambio(reg)) {
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                reg.getCampos().remove("TIPO");

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.D_CAMBIOSDEPLACA
                                                                .getUpdateKey());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDcambiosdeplacasub();
        }
    }

    /**
     * Metodo de eliminacion del formulario Dcambiosdeplacasub
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubDcambiosdeplacasub(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CAMBIOSDEPLACA
                                                            .getDeleteKey());

            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaDcambiosdeplacasub();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ELEMENTO en la fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarELEMENTOC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto alertas en la vista
     *
     *
     */
    public void ejecutaralertas() {
        // <CODIGO_DESARROLLADO>
        if (origen == 1) {
            JsfUtil.agregarMensajeInformativo(
                            "Se ejecuto el proceso correctamente.");
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Dcambiosdeplacasub
     *
     */
    public void cancelarEdicionDcambiosdeplacasub() {
        cargarListaDcambiosdeplacasub();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
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
            registro.getCampos().put(cCambio, generarConsecutivo());
            registro.getCampos().put("FECHACAMBIO", new Date());
            registro.getCampos().put("USUARIO",
                            SysmanFunciones.toString(
                                            SysmanFunciones.nvl(
                                                            SessionUtil.getUser()
                                                                            .getNombre1(),
                                                            "")
                                                            + " " + SysmanFunciones.nvl(
                                                                            SessionUtil.getUser()
                                                                                            .getNombre2(),
                                                                            "")));
        }
        if ((boolean) SysmanFunciones.nvl(registro.getCampos().get(cRealizado),false)) {
            bloquearSub = false;
        }
        else {
            bloquearSub = true;
        }

        registro.getCampos().put(cSiguientePlaca, maximos()[0]);
        registro.getCampos().put(cSiguienteConsecutivo, maximos()[1]);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put("COMPANIA", compania);
            registro.getCampos().remove(cSiguientePlaca);
            registro.getCampos().remove(cSiguienteConsecutivo);
            registro.getCampos().remove("TEXTO_EXCEL");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo valida que los datos ingresados sean correctos
     * 
     * @return
     */
    public boolean validarDatosDeCambio(Registro registroAccion) {
        boolean salida = true;
        /**
         * VALIDACION Si no ingreso nada en el campo nueva serie o nueva placa
         */
        if (registroAccion.getCampos().get(cPlacaNueva).toString().equals("")
                        || registroAccion.getCampos().get(cPlacaNueva).toString().isEmpty()
                        || registroAccion.getCampos().get(cPlacaNueva).toString() == null) {
            /**
             * como no se ingreso nada en el campo SERIE NUEVA, entonces se debe garantizar que los 2 elementos sean del mismo tipo
             */

            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put("ELEMENTO_NUEVO", registroAccion.getCampos().get("NUEVOELEMENTO"));
            parametros.put("ELEMENTO_ANTIGUO", registroAccion.getCampos().get("ELEMENTO"));

            Registro validarTipoPlaca;
            try {
                validarTipoPlaca = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                "141118")
                                                .getUrl(), parametros));

                String respuesta = validarTipoPlaca.getCampos().get("CANTIDAD_PLACAS").toString();
                /**
                 * el numero 1, indica que los elementos antiguo y nuevo son del mismo tipo
                 */
                if (!respuesta.equals("1")) {
                    JsfUtil.agregarMensajeError(
                                    "No digito una placa nueva, por lo que el elemento ANTIGUO y NUEVO deben tener el mismo identificador.");
                    return false;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {

            /**
             * VALIDACION, Los rangos de placas son de 0 a 499999 y los consecutivos sean 500000 y X
             */

            if (registroAccion.getCampos().get(cPlacaNueva).toString() != null
                            && registroAccion.getCampos().get("TIPO").toString().equals("Placa")
                            && (Integer.parseInt(registroAccion.getCampos().get(cPlacaNueva).toString()) < 0
                                            || Integer.parseInt(registroAccion.getCampos().get(cPlacaNueva).toString()) >= 500000
                                            || Integer.parseInt(registroAccion.getCampos().get(cPlacaNueva).toString()) < Integer
                                                            .parseInt(registro.getCampos().get(cSiguientePlaca).toString()))) {
                JsfUtil.agregarMensajeError(
                                "La nueva placa debe estar entre " + registro.getCampos().get(cSiguientePlaca) + " y 499999");
                return false;
            }
            else if (registroAccion.getCampos().get(cPlacaNueva).toString() != null
                            && registroAccion.getCampos().get("TIPO").toString().equals("Consecutivo")
                            && (Integer.parseInt(registroAccion.getCampos().get(cPlacaNueva).toString()) < 500000
                                            || Integer.parseInt(registroAccion.getCampos().get(cPlacaNueva).toString()) < Integer
                                                            .parseInt(registro.getCampos().get(cSiguienteConsecutivo).toString()))) {
                JsfUtil.agregarMensajeError(
                                "La nueva serie debe ser mayor o igual a " + registro.getCampos().get(cSiguienteConsecutivo));
                return false;
            }

        }

        return salida;
    }

    public long generarConsecutivo() {

        long consecutivoGenerado = 0;
        try {
            consecutivoGenerado = ejbSysmanUtil
                            .generarSiguienteConsecutivo(
                                            "CAMBIOSDEPLACA",
                                            SysmanFunciones.concatenar(
                                                            "COMPANIA = ''"
                                                                            + compania,
                                                            "''"),
                                            cCambio);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivoGenerado;
    }

    /**
     * Este metodo valida la siguiente placa y consecutivo
     */
    public int[] maximos() {
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Registro acumPlaca;
        Registro acumConsecutivo;
        int[] valores = new int[2];
        valores[0] = 0;
        valores[1] = 0;
        try {
            acumPlaca = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "141116")
                                            .getUrl(), parametros));

            acumConsecutivo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "141117")
                                            .getUrl(), parametros));

            valores[0] = Integer.parseInt(SysmanFunciones.toString(acumPlaca
                            .getCampos()
                            .get("MAXIMA_PLACA")))
                            + 1;

            valores[1] = Integer
                            .parseInt(SysmanFunciones.toString(acumConsecutivo
                                            .getCampos()
                                            .get("MAXIMO_CONSECUTIVO")))
                            + 1;

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valores;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove(cSiguientePlaca);
            registro.getCampos().remove(cSiguienteConsecutivo);
            registro.getCampos().remove("USUARIO");
            registro.getCampos().remove("COMPANIA");
            registro.getCampos().remove("FECHACAMBIO");
            registro.getCampos().remove(cCambio);
            registro.getCampos().remove("TEXTO_EXCEL");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaSERIE
     * 
     * @return listaSERIE
     */
    public RegistroDataModelImpl getListaSERIE() {
        return listaSERIE;
    }

    /**
     * Asigna la lista listaSERIE
     * 
     * @param listaSERIE
     * Variable a asignar en listaSERIE
     */
    public void setListaSERIE(RegistroDataModelImpl listaSERIE) {
        this.listaSERIE = listaSERIE;
    }

    /**
     * Retorna la lista listaSERIE
     * 
     * @return listaSERIE
     */
    public RegistroDataModelImpl getListaSERIEE() {
        return listaSERIEE;
    }

    /**
     * Asigna la lista listaSERIE
     * 
     * @param listaSERIE
     * Variable a asignar en listaSERIE
     */
    public void setListaSERIEE(RegistroDataModelImpl listaSERIEE) {
        this.listaSERIEE = listaSERIEE;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaELEMENTO
     * 
     * @return listaELEMENTO
     */
    public RegistroDataModelImpl getListaELEMENTO() {
        return listaELEMENTO;
    }

    /**
     * Asigna la lista listaELEMENTO
     * 
     * @param listaELEMENTO
     * Variable a asignar en listaELEMENTO
     */
    public void setListaELEMENTO(RegistroDataModelImpl listaELEMENTO) {
        this.listaELEMENTO = listaELEMENTO;
    }

    /**
     * Retorna la lista listaELEMENTO
     * 
     * @return listaELEMENTO
     */
    public RegistroDataModelImpl getListaELEMENTOE() {
        return listaELEMENTOE;
    }

    /**
     * Asigna la lista listaELEMENTO
     * 
     * @param listaELEMENTO
     * Variable a asignar en listaELEMENTO
     */
    public void setListaELEMENTOE(RegistroDataModelImpl listaELEMENTOE) {
        this.listaELEMENTOE = listaELEMENTOE;
    }

    /**
     * Retorna la lista listaNUEVOELEMENTO
     * 
     * @return listaNUEVOELEMENTO
     */
    public RegistroDataModelImpl getListaNUEVOELEMENTO() {
        return listaNUEVOELEMENTO;
    }

    /**
     * Asigna la lista listaNUEVOELEMENTO
     * 
     * @param listaNUEVOELEMENTO
     * Variable a asignar en listaNUEVOELEMENTO
     */
    public void setListaNUEVOELEMENTO(RegistroDataModelImpl listaNUEVOELEMENTO) {
        this.listaNUEVOELEMENTO = listaNUEVOELEMENTO;
    }

    /**
     * Retorna la lista listaNUEVOELEMENTO
     * 
     * @return listaNUEVOELEMENTO
     */
    public RegistroDataModelImpl getListaNUEVOELEMENTOE() {
        return listaNUEVOELEMENTOE;
    }

    /**
     * Asigna la lista listaNUEVOELEMENTO
     * 
     * @param listaNUEVOELEMENTO
     * Variable a asignar en listaNUEVOELEMENTO
     */
    public void setListaNUEVOELEMENTOE(
                    RegistroDataModelImpl listaNUEVOELEMENTOE) {
        this.listaNUEVOELEMENTOE = listaNUEVOELEMENTOE;
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
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaDcambiosdeplacasub
     * 
     * @return listaDcambiosdeplacasub
     */
    public RegistroDataModelImpl getListaDcambiosdeplacasub() {
        return listaDcambiosdeplacasub;
    }

    /**
     * Asigna la lista listaDcambiosdeplacasub
     * 
     * @param listaDcambiosdeplacasub
     * Variable a asignar en listaDcambiosdeplacasub
     */
    public void setListaDcambiosdeplacasub(
                    RegistroDataModelImpl listaDcambiosdeplacasub) {
        this.listaDcambiosdeplacasub = listaDcambiosdeplacasub;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isBloquearLLavesSub() {
        return bloquearLLavesSub;
    }

    public void setBloquearLLavesSub(boolean bloquearLLavesSub) {
        this.bloquearLLavesSub = bloquearLLavesSub;
    }

    public int getIndiceDcambiosdeplacasub() {
        return indiceDcambiosdeplacasub;
    }

    public void setIndiceDcambiosdeplacasub(int indiceDcambiosdeplacasub) {
        this.indiceDcambiosdeplacasub = indiceDcambiosdeplacasub;
    }

    public String getTituloMayuscula() {
        return tituloMayuscula;
    }

    public void setTituloMayuscula(String tituloMayuscula) {
        this.tituloMayuscula = tituloMayuscula;
    }

    public boolean isMostrarDeplaca() {
        return mostrarDeplaca;
    }

    public void setMostrarDeplaca(boolean mostrarDeplaca) {
        this.mostrarDeplaca = mostrarDeplaca;
    }

    public boolean isMostrarDeCodigo() {
        return mostrarDeCodigo;
    }

    public void setMostrarDeCodigo(boolean mostrarDeCodigo) {
        this.mostrarDeCodigo = mostrarDeCodigo;
    }

    public String getTituloMinuscula() {
        return tituloMinuscula;
    }

    public void setTituloMinuscula(String tituloMinuscula) {
        this.tituloMinuscula = tituloMinuscula;
    }

    public boolean isBloquearSub() {
        return bloquearSub;
    }

    public void setBloquearSub(boolean bloquearSub) {
        this.bloquearSub = bloquearSub;
    }
    // </SET_GET_ADICIONALES>
}
