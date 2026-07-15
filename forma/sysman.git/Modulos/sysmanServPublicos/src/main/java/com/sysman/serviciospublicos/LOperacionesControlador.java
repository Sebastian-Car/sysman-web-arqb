/*-
 * LOperacionesControlador.java
 *
 * 1.0
 *
 * 28/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LOperacionesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario LOperaciones, que se utiliza cuando el
 * parametro INFORME DE OPERACIONES SIMPLIFICADO esta definido con
 * valor NO.
 *
 * @version 1.0, 28/10/2016
 * @author Pablo Espitia
 *
 * -- Modificado por lcortes 07,08,09/06/2017. Refactorizacion de
 * codigo de las listas para utilizar dss. Reemplazo de llamados a la
 * clase Acciones y revision de envio de reemplazos de condiciones a
 * las consultas al generar los reportes.
 */
@ManagedBean
@ViewScoped
public class LOperacionesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /** Constante a nivel de clase que almacena un valor duplicado. */
    private final String todos;

    /**
     * Constante a nivel de clase que almacena el nombre del campo
     * CODIGORUTA
     */
    private final String cCodigoRuta;
    /**
     * Atributo que contiene la opcion seleccionada del control
     * InformePor.
     */
    private String opcionInforme;

    /**
     * Atributo que contiene el ciclo seleccionado en el formulario.
     */
    private String ciclo;

    /**
     * Atributo que contiene la condicion para filtrar por ciclo.
     */
    private String condicionCiclo;

    /**
     * Atributo que contiene el valor del a�o inicial seleccionado.
     */
    private String anioInicial;

    /**
     * Atributo que contiene el valor del periodo inicial
     * seleccionado.
     */
    private String periodoInicial;

    /**
     * Atributo que contiene el valor del a�o final seleccionado.
     */
    private String anioFinal;

    /**
     * Atributo que contiene el valor del periodo final seleccionado.
     */
    private String periodoFinal;

    /**
     * Atributo que contiene el valor del usuario seleccionado.
     */
    private String usuario;

    /** Atributo que contiene el valor del aforador seleccionado. */
    private String aforador;

    /**
     * Atributo que contiene el valor de la operacion seleccionada.
     */
    private String operacion;

    /**
     * Atributo que contiene la fecha inicial ingresada en el
     * formulario.
     */
    private Date fechaInicial;

    /**
     * Atributo que contiene la fecha final ingresada en el
     * formulario.
     */
    private Date fechaFinal;

    /**
     * Atributo que contiene la hora inicial ingresada en el
     * formulario.
     */
    private Date horaInicial;

    /**
     * Atributo que contiene la hora final ingresada en el formulario.
     */
    private Date horaFinal;

    /**
     * Atributo que controla la visibilidad de los controles
     * FechaInicial y FechaFinal..
     */
    private boolean visibleFechas;

    /**
     * Atributo que controla la visibilidad del control Usuario.
     */
    private boolean visibleUsuario;

    /**
     * Atributo que controla la visibilidad del control TipoOperacion.
     */
    private boolean visibleTipoOperacion;

    /**
     * Atributo que controla la visibilidad del control Aforador.
     */
    private boolean visibleAforador;

    /**
     * Atributo que gestiona la visibilidad de los filtros por hora.
     */
    private boolean visibleHoras;

    /**
     * Atributo que almacena el nombre del item seleccionado en el
     * control Usuario.
     */
    private String nombreItemUsuario;

    /**
     * Atributo que almacena el nombre del item seleccionado en el
     * control Aforador.
     */
    private String nombreItemAforador;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista de elementos del control ciclo. */
    private List<Registro> listaCiclo;

    /** Lista de elementos del control AnoInicial. */
    private List<Registro> listaAnoInicial;

    /** Lista de elementos del control PeriodoInicial. */
    private List<Registro> listaPeriodoInicial;

    /** Lista de elementos del control AnoFinal. */
    private List<Registro> listaAnoFinal;

    /** Lista de elementos del control PeriodoFinal. */
    private List<Registro> listaPeriodoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista de elementos del control usuario. */
    private RegistroDataModelImpl listaUsuario;

    /** Lista de elementos del control Aforador. */
    private RegistroDataModelImpl listaAforador;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de LOperacionesControlador
     */
    public LOperacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        todos = "TODOS";
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.L_OPERACIONES_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            opcionInforme = "1";
            nombreItemAforador = nombreItemUsuario = usuario = ciclo = todos;
            aforador = operacion = "000";

            /*
             * Se asigna el a�o actual como valor por defecto para
             * anioInicial y anioFinal.
             */
            anioFinal = anioInicial = String.valueOf(SysmanFunciones
                            .ano(SysmanFunciones.hoy().getTime()));

            periodoInicial = "01";
            periodoFinal = "01";

            cambiarCiclo();
            cargarListaAforador();

            visibleUsuario = true;

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar() {
        /*
         * Verifica si se hacen visibles los filtros por fecha y hora
         * en el formulario.
         */
        try {
            visibleFechas = visibleHoras = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA FECHA DE EJECUCION EN OPERACIONES",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoInicial();
        cargarListaPeriodoFinal();
        cargarListaAforador();  
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1163-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 74, Me.Name If
         * Nz(ParFacturacion(Getcompany(),
         * "MANEJA FECHA DE EJECUCION EN OPERACIONES"), "NO") = "SI"
         * Then Me!lbFechaInicial.visible = True
         * Me!TxtFechaInicial.visible = True Me!lbFechaFinal.visible =
         * True Me!TxtFechaFinal.visible = True
         * Me!lbHoraInicial.visible = True Me!txtHoraInicial.visible =
         * True Me!lbHoraFinal.visible = True Me!txtHoraFinal.visible
         * = True End If Call opcion_AfterUpdate Call
         * Ciclo_AfterUpdate End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista del control ciclo. */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LOperacionesControladorUrlEnum.URL9672
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del control AnoFinal */
    public void cargarListaAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LOperacionesControladorUrlEnum.URL10157
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del control PeriodoInicial */
    public void cargarListaPeriodoInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LOperacionesControladorUrlEnum.URL10549
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /** Carga la lista del control AnoFinal */
    public void cargarListaAnoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LOperacionesControladorUrlEnum.URL10999
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga la lista del control periodo final. */
    public void cargarListaPeriodoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
        param.put(GeneralParameterEnum.MES.getName(), periodoInicial);

        try {
            if (anioFinal.equals(anioInicial)) {
                listaPeriodoFinal = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LOperacionesControladorUrlEnum.URL11450
                                                                .getValue())
                                                .getUrl(),
                                                param));
            }
            else {
                listaPeriodoFinal = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LOperacionesControladorUrlEnum.URL11451
                                                                .getValue())
                                                .getUrl(),
                                                param));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /** Carga la lista del control Aforador. */
    public void cargarListaAforador() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LOperacionesControladorUrlEnum.URL11895
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /** Carga la lista del control Usuario. */
    public void cargarListaUsuario() {

        if (todos.equals(ciclo)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LOperacionesControladorUrlEnum.URL12406
                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigoRuta);
        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            LOperacionesControladorUrlEnum.URL12407
                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            listaUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, cCodigoRuta);
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista. Gestiona
     * el evento de presionar el boton PDF.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        /* Valida la fecha y la hora. */
        if (validarFecha() && validarHora()) {
            generarReporte(FORMATOS.PDF);
            // </CODIGO_DESARROLLADO>
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista.
     * Gestiona el evento de presionar el boton EXCEL.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        /* Valida la fecha y la hora. */
        if (validarFecha() && validarHora()) {
            generarReporte(FORMATOS.EXCEL97);
            // </CODIGO_DESARROLLADO>
        }
    }

    /**
     * Genera un reporte con un determinado formato.
     *
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {

        String reporte = seleccionarReporte();
        try {


            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();     

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("condicionCiclo", filtrarCiclo());
            reemplazar.put("peranoInicial", anioInicial + periodoInicial);
            reemplazar.put("peranoFinal", anioFinal + periodoFinal);
            reemplazar.put("informePor", filtrarOpcionInforme());
            reemplazar.put("condicionFecha", filtrarFecha(visibleFechas));
            reemplazar.put("condicionHora", filtrarHora(visibleHoras));

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_ANOINICIAL", anioInicial);
            parametros.put("PR_ANOFINAL", anioFinal);
            parametros.put("PR_PERIODOINICIAL", periodoInicial);
            parametros.put("PR_PERIODOFINAL", periodoFinal);
            parametros.put("PR_OPERACION", verNombreTipoOperacion());
            parametros.put("PR_VISIBLEHORA", visibleHoras);
            parametros.put("PR_USUARIO", nombreItemUsuario);
            parametros.put("PR_AFORADOR", nombreItemAforador);

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(LOperacionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            Logger.getLogger(LOperacionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {         
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Verifica que la fecha inicial y final no sean nulas, de lo
     * contrario envia un mensaje de alerta. Siempre y cuando las
     * fechas sean visibles.
     *
     * @return true si la fecha no es nula y es visible.
     */
    public boolean validarFecha() {
        boolean key = visibleFechas
                        ? (fechaInicial != null) && (fechaFinal != null)
                            : true;
                        if (!key) {
                            JsfUtil.agregarMensajeAlerta(
                                            idioma.getString("TB_TB1790"));
                        }
                        return key;
    }

    /**
     * Verifica que la hora inicial y final no sean nulas, de lo
     * contrario envia un mensaje de alerta. Siempre y cuando las
     * horas sean visibles.
     *
     * @return true si la hora no es nula y es visible.
     */
    public boolean validarHora() {
        boolean key = visibleHoras
                        ? (horaInicial != null) && (horaFinal != null)
                            : true;
                        if (!key) {
                            JsfUtil.agregarMensajeAlerta(
                                            idioma.getString("TB_TB1791"));
                        }
                        return key;
    }

    /**
     * Recuper el nombre del tipo de operacion seleccionado en el
     * formulario.
     *
     * @return El nombre de asignado al tipo de operacion.
     */
    public String verNombreTipoOperacion() {
        String op;
        switch (operacion) {
        case "000":
            op = "TODAS";
            break;
        case "001":
            op = "SUSPENSION";
            break;
        case "002":
            op = "CORTE";
            break;
        case "003":
            op = "RECONEXION";
            break;
        default:
            op = "REINSTALACION";
            break;
        }
        return op;
    }

    /**
     * Selecciona el reporte que se debe generar y la consulta
     * asociada.
     *
     * @return Nombre de la consulta y reporte.
     */
    public String seleccionarReporte() {
        String reporte;
        switch (opcionInforme) {
        case "2":
            reporte = "001189RegistroOperacionesOperario";
            break;
        case "3":
            reporte = "001191RegistroOperacionesOperacion";
            break;
        default:
            reporte = "001185RegistroOperacionesUsuario";
            break;
        }
        return reporte;
    }

    /**
     * Formatea la fecha inicial e inicial y genera la condicion para
     * filtrar por fecha.
     *
     * @param key
     * Parametro que indica si se debe filtrar por fecha.
     * @return La condicion para filtrar por fecha.
     */
    public String filtrarFecha(boolean key) {
        if ((fechaInicial != null) && (fechaFinal != null) && key) {
            String fInicial = SysmanFunciones.formatearFecha(fechaInicial);
            String fFinal = SysmanFunciones.formatearFecha(fechaFinal);
            return " AND ITO.FECHAEJECUCION BETWEEN " + fInicial + " AND "
            + fFinal;
        }
        return " ";
    }

    /**
     * Formatea la hora inicial y final y genera la condicon para
     * filtrar por hora.
     *
     * @param key
     * Parametro que indica si se debe filtrar por hora.
     * @return La condicion para filtrar por hora.
     */
    public String filtrarHora(boolean key) {
        if ((horaInicial != null) && (horaFinal != null) && key) {
            try {
                return " AND TO_CHAR(ITO.HORAEJECUCION, 'HH24:MI:SS') BETWEEN '"
                                + SysmanFunciones.convertirAHoraCadena(horaInicial)
                                + "' AND '"
                                + SysmanFunciones.convertirAHoraCadena(horaFinal) + "' ";
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return " ";
    }

    /**
     * Retorna la condici�n para filtrar por ciclo, si el el ciclo
     * tiene valor asignado diferente de TODOS.
     *
     * @return La condicion que permite filtrar por ciclo.
     */
    public String filtrarCiclo() {
        return todos.equals(ciclo) ? " " : " AND U.CICLO = " + ciclo;
    }

    /**
     * Retorna la condicion para filtrar por usuario, tipo de
     * operacion o aforador, respecto al valor del atributo
     * opcionInforme.
     *
     * @return
     */
    public String filtrarOpcionInforme() {
        String condicion;

        switch (opcionInforme) {
        case "1":
            condicion = todos.equals(usuario) ? " "
                : " AND U.CODIGORUTA='" + usuario + "'";
            break;
        case "2":
            condicion = "000".equals(aforador) ? " "
                : " AND ITO.AFORADOR='" + aforador + "'";
            break;
        default:
            condicion = "000".equals(operacion) ? " "
                : " AND ITO.TIPO_OPERACION= '" + operacion + "'";
            break;
        }

        return condicion;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /** Gestiona los eventos de actualizar el control ciclo. */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        /* Filtrar por ciclo cuando sea diferente de todos. */
        condicionCiclo = todos.equals(ciclo) ? " " : " AND U.CICLO =" + ciclo;
        cargarListaUsuario();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoInicial. Controla los
     * eventos del control AnoInicial.
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal. Controla los
     * eventos del control AnoFinal.
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodoInicial() {
        cargarListaPeriodoFinal();
    }

    /**
     * Metodo ejecutado al cambiar el control Opcion. Gestiona los
     * eventos al actualizar el control.
     *
     */
    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>
        switch (opcionInforme) {
        case "2": // Se selecciono la opcion Aforador.
            visibleFechas = visibleAforador = true;
            visibleUsuario = visibleTipoOperacion = false;
            break;
        case "3": // Se selecciono la opcion TipoOperacion.
            visibleTipoOperacion = true;
            visibleFechas = visibleUsuario = visibleAforador = false;
            break;
        default: // Opcion Usuario.
            visibleFechas = visibleTipoOperacion = visibleAforador = false;
            visibleUsuario = true;
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuario. Gestiona los eventos del combo Usuario.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaUsuario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuario = registroAux.getCampos().get(cCodigoRuta).toString();

        /* Recupera el nombre del item seleccionado. */
        nombreItemUsuario = registroAux.getCampos().get("NOMBRE").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAforador. Gestiona los eventos del combo Aforador.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaAforador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        aforador = registroAux.getCampos().get("CODIGO").toString();

        /* Recupera el nombre del item seleccionado. */
        nombreItemAforador = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable opcionInforme
     *
     * @return opcionInforme
     */
    public String getOpcionInforme() {
        return opcionInforme;
    }

    public boolean isVisibleHoras() {
        return visibleHoras;
    }

    public void setVisibleHoras(boolean visibleHoras) {
        this.visibleHoras = visibleHoras;
    }

    public boolean isVisibleFechas() {
        return visibleFechas;
    }

    public void setVisibleFechas(boolean visibleFechas) {
        this.visibleFechas = visibleFechas;
    }

    public boolean isVisibleUsuario() {
        return visibleUsuario;
    }

    public void setVisibleUsuario(boolean visibleUsuario) {
        this.visibleUsuario = visibleUsuario;
    }

    public boolean isVisibleTipoOperacion() {
        return visibleTipoOperacion;
    }

    public void setVisibleTipoOperacion(boolean visibleTipoOperacion) {
        this.visibleTipoOperacion = visibleTipoOperacion;
    }

    public boolean isVisibleAforador() {
        return visibleAforador;
    }

    public void setVisibleAforador(boolean visibleAforador) {
        this.visibleAforador = visibleAforador;
    }

    public String getCondicionCiclo() {
        return condicionCiclo;
    }

    public void setCondicionCiclo(String condicionCiclo) {
        this.condicionCiclo = condicionCiclo;
    }

    public Date getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(Date horaInicial) {
        this.horaInicial = horaInicial;
    }

    public Date getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(Date horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Asigna la variable opcionInforme
     *
     * @param opcionInforme
     * Variable a asignar en opcionInforme
     */
    public void setOpcionInforme(String opcionInforme) {
        this.opcionInforme = opcionInforme;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anioInicial
     *
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     *
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anioFinal
     *
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     *
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable usuario
     *
     * @return usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Asigna la variable usuario
     *
     * @param usuario
     * Variable a asignar en usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna la variable aforador
     *
     * @return aforador
     */
    public String getAforador() {
        return aforador;
    }

    /**
     * Asigna la variable aforador
     *
     * @param aforador
     * Variable a asignar en aforador
     */
    public void setAforador(String aforador) {
        this.aforador = aforador;
    }

    /**
     * Retorna la variable operacion
     *
     * @return operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * Asigna la variable operacion
     *
     * @param operacion
     * Variable a asignar en operacion
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getNombreItemAforador() {
        return nombreItemAforador;
    }

    public void setNombreItemAforador(String nombreItemAforador) {
        this.nombreItemAforador = nombreItemAforador;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaUsuario
     *
     * @return listaUsuario
     */
    public RegistroDataModelImpl getListaUsuario() {
        return listaUsuario;
    }

    /**
     * Asigna la lista listaUsuario
     *
     * @param listaUsuario
     * Variable a asignar en listaUsuario
     */
    public void setListaUsuario(RegistroDataModelImpl listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    /**
     * Retorna la lista listaAforador
     *
     * @return listaAforador
     */
    public RegistroDataModelImpl getListaAforador() {
        return listaAforador;
    }

    /**
     * Asigna la lista listaAforador
     *
     * @param listaAforador
     * Variable a asignar en listaAforador
     */
    public void setListaAforador(RegistroDataModelImpl listaAforador) {
        this.listaAforador = listaAforador;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
