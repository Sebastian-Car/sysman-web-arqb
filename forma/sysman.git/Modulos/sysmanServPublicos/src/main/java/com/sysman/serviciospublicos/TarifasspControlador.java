/*-
 * TarifasspControlador.java
 *
 * 1.0
 *
 * 17/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoRemote;
import com.sysman.serviciospublicos.enums.TarifasspControladorEnum;
import com.sysman.serviciospublicos.enums.TarifasspControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

/**
 * Controlador para la vista del formulario tarifassp
 *
 * @version 1.0, 17/01/2017
 * @author mzanguna
 * 
 * @author eamaya
 * @version 2.0, 16/06/2017 Proceso de Refactoring y manejo de Ejbs
 * 
 * @modifier amonroy
 * @version 3.0, 21/09/2017 Se adiciona el parametro "desdeSuscriptor"
 * para diferenciar si el formulario ha sido redireccionado desde el
 * formulario "Factura" y se define el comportamiento al oprimir el
 * boton "Volver"
 * 
 */
@ManagedBean
@ViewScoped

public class TarifasspControlador extends BeanBaseDatosAcmeImpl {
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por la cantidad de veces que se llama el
     * texto "periodo" en el controlador
     */
    private final String cPeriodo;
    /**
     * Almacena el codigo del modulo en el que se esta trabajando
     */
    private String modulo;
    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    /**
     * Variable para almacenar el uso de la tarifa actual.
     */
    private String usoActual;
    private int anoActual;
    private String periodoActual;

    /**
     * Variables para totalizar las tarifas del aseo 720
     */
    private double totalTAseo720 = 0.0;
    private double totalSubAseo720 = 0.0;
    private double totalSobreAseo720 = 0.0;
    private double totalTDesAseo720 = 0.0;
    private double totalDesSubAseo720 = 0.0;
    private double totalDesSobreAseo720 = 0.0;

    /**
     * Variables para control de los permisos de guardar y actualizar
     */
    private boolean muestraNuevo = true;
    private boolean muestraActualiza = true;
    private boolean muestraEliminar = true;

    /**
     * Atributos para visualizar controles de la forma
     */
    private boolean pagAnalisisVisible;
    private boolean btInteresRecargoVisible;
    private boolean visible668;

    /**
     * Variables para control de parámetros
     */
    private boolean parSubContrEnFactura;
    private boolean parCobrarAseoPeso;
    private boolean parRes351;
    private boolean parDescMetrajeAcu;
    private boolean parTarifasPropias;
    private boolean parTarifa668;
    private boolean parHogarTarifa;
    private boolean parSubSobreDesh;
    private boolean parAseoDeshComponentes;
    private boolean parSubSobreTasaAmb;
    private boolean parSegundaFechaRecargo;
    private boolean parManeja720;
    private boolean parTarifaManual720;
    private boolean parRes688;

    /**
     * Variables para calculo de progresividad de la 720 Este proceso
     * solo se realiza una vez en el periodo configurado
     */
    private boolean parAplicaPgr720;
    private String parPeriodoConfPgr720;

    private String parPeriodoInicio720;
    private String parClasificacion668;

    /**
     * Variable que permite hacer visible las categorias de aseo por
     * metraje
     */
    private boolean aseoPorMetrajeVisible;
    /**
     * Indica si el formulario ha sido redireccionado desde
     * "FacturaControlador"
     */
    private boolean desdeSuscriptor;
    /**
     * Almacena los valores enviados por parametro al redireccionar a
     * este formulario
     */
    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listas de uso y estrato
     */
    private List<Registro> listaUso;
    /**
     * Lista que carga los estratos correspondientes a la compañia
     * actual
     */
    private List<Registro> listaEstrato;

    @EJB
    private EjbServiciosPublicosCincoRemote ejbSPCinco;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de TarifasspControlador
     */
    public TarifasspControlador() {
        super();
        compania = SessionUtil.getCompania();
        cPeriodo = "periodo";
        try {
            modulo = SessionUtil.getModulo();
            numFormulario = GeneralCodigoFormaEnum.TARIFASSP_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                validarCondicionesIniciales();
            }
            else {
                SessionUtil.redireccionarMenu();
            }

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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaUso();
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
        enumBase = GenericUrlEnum.SP_TARIFAS;
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoActual);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(),
                        periodoActual);

    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     * Este metodo se ejecutara al oprimir el boton de volver cuando
     * ha sido redireccionado desde el formulario "Facturas", su
     * ejecucion depende del valor del atributo "varVolver"
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        if (desdeSuscriptor) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ciclo", parametrosEntrada.get("cicloSuscriptor"));
            parametros.put("ano", parametrosEntrada.get("anoSuscriptor"));
            parametros.put(cPeriodo,
                            parametrosEntrada.get("periodoSuscriptor"));
            parametros.put("rid", parametrosEntrada.get("ridSuscriptor"));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FACTURA_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaUso con datos correspondientes a la
     * compañia actual
     *
     */
    public void cargarListaUso() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasspControladorUrlEnum.URL7777
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
     * Carga la lista listaEstrato con los datos de la compaña actual
     */
    public void cargarListaEstrato() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(TarifasspControladorEnum.PARAM0.getValue(),
                            usoActual);

            listaEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasspControladorUrlEnum.URL8712
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiarUso() {
        // <CODIGO_DESARROLLADO>
        usoActual = registro.getCampos().get("USO").toString();
        cargarListaEstrato();
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado al cambiar el control Verificatarifas
     *
     * Metodo para hacer visible el cobro del aseo por metraje
     *
     */
    public void cambiarVerificatarifas() {
        // <CODIGO_DESARROLLADO>

    }

    // </CODIGO_DESARROLLADO>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton BtInteresRecargo en la
     * vista
     *
     * Boton para abrir formulario modal con los datos de intereses de
     * recargo
     *
     */
    public void oprimirBtInteresRecargo() {
        // <CODIGO_DESARROLLADO>

        String estratoActual = (String) registro.getCampos().get("ESTRATO");
        String[] campos = { "anio", cPeriodo, "uso", "estrato" };
        Object[] valores = { anoActual, periodoActual, usoActual,
                             estratoActual };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRMTARIFASINTRECARGO_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos, valores);

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtnCalcular en la vista
     *
     * Boton para calcular las tarifas de aseo 720
     *
     */
    public void oprimirBtnCalcular() {
        // <CODIGO_DESARROLLADO>
        boolean respuesta = false;
        try {
            if (parAplicaPgr720) {

                respuesta = ejbSPCinco.calcularProgresividad(compania,
                                anoActual, periodoActual, false,
                                null, null);

            }
            else {
                respuesta = ejbSPCinco.calcularTarifas720(compania, anoActual,
                                periodoActual);

            }

            cargarRegistro(registro.getLlave(), "m");
            if (respuesta) {

                JsfUtil.agregarMensajeInformativo(idioma
                                .getString(Constantes.MSM_PROCESO_EJECUTADO));

            }
            else {
                JsfUtil.agregarMensajeError(idioma
                                .getString(Constantes.MSM_TRANS_INTERRUMPIDA));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        cargarParametros();
        if (parManeja720 && ((anoActual + "," + periodoActual)
                        .compareTo(parPeriodoInicio720) < 0)) {
            parManeja720 = false;
        }
        parAseoDeshComponentes = parManeja720 ? false : parAseoDeshComponentes;

        if (parAplicaPgr720 && ((anoActual + "," + periodoActual)
                        .compareTo(parPeriodoConfPgr720) < 0)) {
            parAplicaPgr720 = false;
        }

        if (desdeSuscriptor) {
            cargarRegistro(rid, ACCION_VER);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evalua si el formulario ha sido redireccionado desde el
     * formulario de "Factura" y realiza la asignacion de atributos
     * con relacion a los valores recibidos por parametro
     */
    private void validarCondicionesIniciales() {
        int ultimoAno;
        String ultimoPeriodo;
        desdeSuscriptor = (boolean) SysmanFunciones.nvl(
                        parametrosEntrada.get("desdeSuscriptor"),
                        false);

        if (desdeSuscriptor) {
            rid = new HashMap<String, Object>();
            rid.put(TarifasspControladorEnum.KEY_COMPANIA.getValue(), compania);
            rid.put(TarifasspControladorEnum.KEY_ANO.getValue(),
                            parametrosEntrada.get("anoSuscriptor")
                                            .toString());
            rid.put(TarifasspControladorEnum.KEY_PERIODO.getValue(),
                            parametrosEntrada.get("periodoSuscriptor")
                                            .toString());
            rid.put(TarifasspControladorEnum.KEY_USO.getValue(),
                            parametrosEntrada.get("uso").toString());
            rid.put(TarifasspControladorEnum.KEY_ESTRATO.getValue(),
                            parametrosEntrada.get("estrato")
                                            .toString());
            varVolver = true;
        }
        else {
            anoActual = (int) parametrosEntrada.get("anio");
            periodoActual = (String) parametrosEntrada.get(cPeriodo);

            ultimoAno = (int) parametrosEntrada.get("ultimoAnio");
            ultimoPeriodo = (String) parametrosEntrada
                            .get("ultimoPeriodo");

            if ((anoActual != ultimoAno)
                || (!periodoActual.equals(ultimoPeriodo))) {
                muestraNuevo = false;
                muestraActualiza = false;
                muestraEliminar = false;
            }
            varVolver = false;
        }
    }

    public void cargarParametros() {
        try {
            parSubContrEnFactura = parSiNo("PR_MANEJA_PORCE_SUB_CONT_FACTURA");
            parCobrarAseoPeso = parSiNo("PR_COBRAR_ASEO_PESO");
            parRes351 = parSiNo("PR_MANEJA351");
            parDescMetrajeAcu = parSiNo("PR_DESC_METRAJE_ACU");
            parTarifasPropias = parSiNo("PR_TARIFAS_PROPIAS");
            parTarifa668 = parSiNo("PR_TARIFA668");

            parClasificacion668 = parStrDefault("PR_CLASIFICACION668", "");

            if (!parClasificacion668.isEmpty()) {
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("RESIDENCIAL", "1");
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("COMERCIAL", "2");
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("INDUSTRIAL", "3");
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("OFICIAL", "4");
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("ESPECIAL", "5");
                parClasificacion668 = parClasificacion668.toUpperCase()
                                .replace("TEMPORAL", "6");
            }

            pagAnalisisVisible = parSiNo("PR_ANALISIS_FISICOQUIMICO");
            btInteresRecargoVisible = parSiNo("PR_MANEJA_INTERES_RECARGO");
            parHogarTarifa = parSiNo("PR_HOGAR_COMUNITARIO_TARIFAS");
            parAseoDeshComponentes = parSiNo("PR_ASEODESH_COMPONENTE");
            parSubSobreDesh = parAseoDeshComponentes ? false
                : parSiNo("PR_SUB_SOBRE_DESHABITADOS");
            parSubSobreTasaAmb = parSiNo("PR_SUBSOBRE_TASA_AMB");
            parSegundaFechaRecargo = parSiNo(
                            "PR_RECARGO_SEGUNDAFECHA_USOESTRATO");

            parManeja720 = parSiNo("PR_MANEJA_720");
            parPeriodoInicio720 = parStrDefault("PR_PERIODO_INICIO_720", "0");

            parTarifaManual720 = parSiNo("PR_TARIFA_MANUAL720") ? false : true;
            parAplicaPgr720 = parSiNo("PR_PROGRESIVIDAD720");
            parPeriodoConfPgr720 = parStrDefault("PR_PERIODO_CONF_PGR720", "");
            parRes688 = parSiNo("PR_RES688");

        }

        catch (NamingException | SQLException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean parSiNo(String parametro)
                    throws NamingException, SQLException {

        String parFinal = parStrDefault(parametro, "NO");

        return "SI".equals(parFinal) ? true : false;
    }

    private String parStrDefault(String parametro, String defecto)
                    throws NamingException, SQLException {
        return SysmanFunciones.nvlStr(parStr(parametro), defecto);
    }

    private String parStr(String parametro) {

        String valor = null;
        try {

            valor = ejbSysmanUtil.consultarParametro(compania,
                            parametros.getString(parametro), modulo, new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valor;
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        String catSuperServicio = SysmanFunciones.nvl(registro.getCampos()
                        .get("USOSUPERSERVICIOS"), "").toString();
        precargarRegistro();
        usoActual = SysmanFunciones
                        .nvl(registro.getCampos().get("USO"), "").toString();
        cargarListaEstrato();
        subTotales720();
        aseoPorMetrajeVisible = false;
        if (parTarifa668 && (parClasificacion668.contains(catSuperServicio))) {
            visible668 = true;
        }
        else {
            visible668 = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cargar los registros para Subtotalizar las
     * tarifas de aseo 720
     */
    public void subTotales720() {
        totalTAseo720 = Double.parseDouble(SysmanFunciones.nvl(
                        registro.getCampos().get("CCS_720"), "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("CBLS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("CLUS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("CRT_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("CDF_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("CTL_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("VBA_720"),
                            "0").toString());

        totalSubAseo720 = Double.parseDouble(SysmanFunciones.nvl(
                        registro.getCampos().get("SUBCCS_720"), "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBCBLS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBCLUS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBCRT_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBCDF_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBCTL_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SUBVBA_720"),
                            "0").toString());

        totalSobreAseo720 = Double.parseDouble(SysmanFunciones.nvl(
                        registro.getCampos().get("SOBRECCS_720"),
                        "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBRECBLS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBRECLUS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBRECRT_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBRECDF_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBRECTL_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("SOBREVBA_720"),
                            "0").toString());

        totalTDesAseo720 = Double.parseDouble(SysmanFunciones.nvl(
                        registro.getCampos().get("DES_CCS_720"),
                        "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_CBLS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_CLUS_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_CRT_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_CDF_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_CTL_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_VBA_720"),
                            "0").toString());

        totalDesSubAseo720 = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("DES_SUBCCS_720"),
                                        "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SUBCBLS_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SUBCLUS_720"),
                                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_SUBCRT_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_SUBCDF_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_SUBCTL_720"),
                            "0").toString())
            +
            Double.parseDouble(SysmanFunciones.nvl(
                            registro.getCampos().get("DES_SUBVBA_720"),
                            "0").toString());

        totalDesSobreAseo720 = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("DES_SOBRECCS_720"),
                                        "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBRECBLS_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBRECLUS_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBRECRT_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBRECDF_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBRECTL_720"),
                                            "0").toString())
            +
            Double.parseDouble(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DES_SOBREVBA_720"),
                                            "0").toString());

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * Remueve los datos que no se insertan a la tabla sp_tarifas
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anoActual);
        registro.getCampos().put("PERIODO", periodoActual);
        registro.getCampos().remove("NOMBREUSO");
        registro.getCampos().remove("ESTRATONOMBRE");
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     *
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        if ("m".equals(accion)) {
            registro.getCampos().remove("COMPANIA");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * Recarga los subtotales de las tarifas 720
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        subTotales720();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaUso
     *
     * @return listaUso
     */
    public List<Registro> getListaUso() {
        return listaUso;
    }

    /**
     * Asigna la lista listaUso
     *
     * @param listaUso
     * Variable a asignar en listaUso
     */
    public void setListaUso(List<Registro> listaUso) {
        this.listaUso = listaUso;
    }

    /**
     * Retorna la lista listaEstrato
     *
     * @return listaEstrato
     */
    public List<Registro> getListaEstrato() {
        return listaEstrato;
    }

    /**
     * Asigna la lista listaEstrato
     *
     * @param listaEstrato
     * Variable a asignar en listaEstrato
     */
    public void setListaEstrato(List<Registro> listaEstrato) {
        this.listaEstrato = listaEstrato;
    }

    public String getUsoActual() {
        return usoActual;
    }

    public void setUsoActual(String usoActual) {
        this.usoActual = usoActual;
    }

    public Double getTotalTAseo720() {
        return totalTAseo720;
    }

    public Boolean getMuestraNuevo() {
        return muestraNuevo;
    }

    public void setMuestraNuevo(Boolean muestraNuevo) {
        this.muestraNuevo = muestraNuevo;
    }

    public Boolean getMuestraActualiza() {
        return muestraActualiza;
    }

    public void setMuestraActualiza(Boolean muestraActualiza) {
        this.muestraActualiza = muestraActualiza;
    }

    public Double getTotalSubAseo720() {
        return totalSubAseo720;
    }

    public Double getTotalSobreAseo720() {
        return totalSobreAseo720;
    }

    public Double getTotalTDesAseo720() {
        return totalTDesAseo720;
    }

    public Double getTotalDesSubAseo720() {
        return totalDesSubAseo720;
    }

    public Double getTotalDesSobreAseo720() {
        return totalDesSobreAseo720;
    }

    public boolean isPagAnalisisVisible() {
        return pagAnalisisVisible;
    }

    public void setPagAnalisisVisible(boolean pagAnalisisVisible) {
        this.pagAnalisisVisible = pagAnalisisVisible;
    }

    public String getModulo() {
        return modulo;
    }

    public boolean isBtInteresRecargoVisible() {
        return btInteresRecargoVisible;
    }

    public void setBtInteresRecargoVisible(boolean btInteresRecargoVisible) {
        this.btInteresRecargoVisible = btInteresRecargoVisible;
    }

    public boolean isParSubContrEnFactura() {
        return parSubContrEnFactura;
    }

    public void setParSubContrEnFactura(boolean parSubContrEnFactura) {
        this.parSubContrEnFactura = parSubContrEnFactura;
    }

    public boolean isParCobrarAseoPeso() {
        return parCobrarAseoPeso;
    }

    public boolean isAseoPorMetrajeVisible() {
        return aseoPorMetrajeVisible;
    }

    public void setAseoPorMetrajeVisible(boolean aseoPorMetrajeVisible) {
        this.aseoPorMetrajeVisible = aseoPorMetrajeVisible;
    }

    public boolean isParRes351() {
        return parRes351;
    }

    public boolean isParDescMetrajeAcu() {
        return parDescMetrajeAcu;
    }

    public boolean isParTarifasPropias() {
        return parTarifasPropias;
    }

    public boolean isMuestraEliminar() {
        return muestraEliminar;
    }

    public boolean isvisible668() {
        return visible668;
    }

    public boolean isparTarifa668() {
        return parTarifa668;
    }

    /**
     * Retorna la variable varVolver
     * 
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    public String getparClasificacion668() {
        return parClasificacion668;
    }

    public boolean isParHogarTarifa() {
        return parHogarTarifa;
    }

    public boolean isParSubSobreDesh() {
        return parSubSobreDesh;
    }

    public boolean isParAseoDeshComponentes() {
        return parAseoDeshComponentes;
    }

    public boolean isParSubSobreTasaAmb() {
        return parSubSobreTasaAmb;
    }

    public boolean isParSegundaFechaRecargo() {
        return parSegundaFechaRecargo;
    }

    public boolean isParManeja720() {
        return parManeja720;
    }

    public String getParPeriodoInicio720() {
        return parPeriodoInicio720;
    }

    public boolean isParTarifaManual720() {
        return parTarifaManual720;
    }

    public boolean isParAplicaPgr720() {
        return parAplicaPgr720;
    }

    public String getParPeriodoConfPgr720() {
        return parPeriodoConfPgr720;
    }

    public boolean isParRes688() {
        return parRes688;
    }

}
