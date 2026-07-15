/*-
 * FinanciablesdedeudasControlador.java
 *
 * 1.0
 *
 * 30/01/2017
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FinanciablesdedeudasControladorEnum;
import com.sysman.serviciospublicos.enums.FinanciablesdedeudasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase que controla la vista del formulario de Financiables de Deuda
 * Facturacion \ Novedades \ Financiables de Deuda
 *
 * @version 1.0, 30/01/2017
 * @author ybecerra
 * @version 2, 23/05/2017 jrodriguezr Se refactoriza el c�digo SQL
 * de las listas para utilizar DSS. Tambi�n los llamados a
 * funciones, procedimientos y m�todos de la clase Acciones a
 * llamados a EJB. Textos al archivo properties.
 * @version 2.1, 22/09/2017 amonroy Se adiciona el envio del campo
 * consecutivo al realizar el llamado a la funcion
 * PCK_SERVICIOS_PUBLICOS_COM3.FC_ACTUALIZAFINANCIABLEDEUDA
 *
 */
@ManagedBean
@ViewScoped

public class FinanciablesdedeudasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo
     * ingresado en la aplicacion
     */
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "SYSDATE"
     */
    /**
     * Constante definida para almacenar la cadena "CONSECUTIVO"
     */
    private final String cConsecutivo;
    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String cNumero;
    /**
     * Constante definida para almacenar la cadena "VALORCUOTA"
     */
    private final String cValorCuota;
    /**
     * Constante definida para almacenar la cadena "NOCUOTAS"
     */
    private final String cNoCuotas;
    /**
     * Constante definida para almacenar la cadena "VRAFINANCIAR"
     */
    private final String cVraFinanciar;
    /**
     * Constante definida para almacenar la cadena "ABONOINICIAL"
     */
    private final String cAbonoInicial;
    /**
     * Constante definida para almacenar la cadena "PERIODOSNOCOBRO"
     */
    private final String cPeriodosCobro;
    /**
     * Constante definida para almacenar la cadena "CODIGORUTA"
     */
    private final String cCodigoRuta;
    /**
     * Constante definida para almacenar la cadena "NOMBREUSUARIO"
     */
    private final String cNombreUsuario;
    /**
     * Constante definida para almacenar la cadena "USUARIO"
     */
    private final String cUsuario;
    /**
     * Constante definida para almacenar la cadena "CONSECUTIVOMANUAL"
     */
    private final String cConsecutivoManual;
    /**
     * Constante definida para almacenar la cadena "INDANULADO"
     */
    private final String cIndAnulado;
    /**
     * Constante definida para almacenar la cadena "PERIODO"
     */
    private final String cPeriodo;
    /**
     * Constante definida para almacenar la cadena "CICLO"
     */
    private final String cCiclo;
    /**
     * Constante definida para almacenar la cadena "FINANCIADO"
     */
    private final String cFinanciado;
    /**
     * Almacena la llave del financiable, se envia al formulario
     * factura , para cuando se le de clic en el boton volver del
     * formulario retorne al registro que abrio el formulario factura
     */
    private Map<String, Object> ridFinanciable;
    /**
     * Atributo que permite identificar el BANCOPERPROCESO del usuario
     */
    private String bancoPer;
    /**
     * Atributo que permite identificar la fecha de la plantilla
     * seleccionada.
     */
    private String fechaPlantilla;
    /**
     * Atributo que permite identificar el Nit del usuario
     * seleccionado
     */
    private String nitUsuario;
    /**
     * Atributo que permite identificar el Nombre del usuario
     * seleccionado
     */
    private String nombreUsuario;

    /**
     * Atributo que permite validar si el abono Inicial esta en 0 o no
     */
    private boolean sinAbonoIn;
    /**
     * Atributo que almacena el valor del Abono Inicial
     */
    private double valAbonoIn;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar el numero de ciclo
     * seleccionado
     */
    private String ciclo;
    /**
     * Atributo que permite identificar el codigo de la plantilla
     * seleccionada
     */
    private String formato;
    /**
     * Atributo que permite identificar el estrado del usuario
     * seleccionado
     */
    private String estrato;
    /**
     * Atributo que permite identificar el uso del usuario
     * seleccionado
     */
    private String uso;
    /**
     *
     */
    private boolean manejaTercero;
    /**
     * Atributo que permite validar si cargan o no los campos de
     * Consecutivo Manual
     */
    private boolean cargarConsecutivoManual;
    /**
     * Atributo que permite validar si cargan o no los campos de
     * Maneja Datos Tercero
     */
    private boolean cargarDatosTercero;
    /**
     * Atributo que permite validar si cargan o no el combo del
     * formato y los botones de imprimir
     */
    private boolean cargarFormato;
    /**
     * Atributo que permite validar si carga o no el boton de
     * detallado
     */
    private boolean cargarDetallado;
    /**
     * Atributo que permite validar si cargan o no los campos de Fecha
     * anulacion y usuario anulo
     */
    private boolean cargarAnular;
    /**
     * Atributo que permite hacer visible o no el Dialogo de Anular
     */
    private boolean cargarDialogoAnular;
    /**
     * Atributo que bloquea los campos Fecha Anulacion y Uusario anulo
     * si el check Anular Financiable esta seleccionado
     */
    private boolean bloqueadoAnular;
    /**
     * Atributo que permite hacer visible o no el Dialogo Abono
     */
    private boolean cargarDialogoAbono;
    /**
     * Atributo que permite desbloquear el combo formato y los botones
     * imprimir
     */
    private boolean bloqueadoFactura = true;
    /**
     * Atributo que permite desbloquear el check de tercero despues de
     * insertar un registro
     */
    private boolean bloqueadoTercero;
    /**
     * Atributo que permite activar o inactivar el boton Ver/Editar
     */
    private boolean activarTercero;
    /**
     * Atributo que almacena el consecutivo asignado al crear un
     * financiable de deuda
     */
    private Long consecutivo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena la lista del combo Codigo
     * Ruta
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /**
     * Atributo de la clase que almacena la lista del combo Ciclo
     */

    private RegistroDataModelImpl listaCiclo;
    /**
     * Atributo de la clase que almacena la lista del combo informes
     * formateados
     */
    private RegistroDataModelImpl listaFormateado;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    @EJB
    private EjbServiciosPublicosCuatroRemote ejbServiciosPublicosCuatro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FinanciablesdedeudasControlador
     */
    public FinanciablesdedeudasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        cValorCuota = "VALORCUOTA";
        cNoCuotas = "NOCUOTAS";
        cVraFinanciar = "VRAFINANCIAR";
        cAbonoInicial = "ABONOINICIAL";
        cPeriodosCobro = "PERIODOSNOCOBRO";
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        cNombreUsuario = "NOMBREUSUARIO";
        cUsuario = GeneralParameterEnum.USUARIO.getName();
        cConsecutivoManual = "CONSECUTIVOMANUAL";
        cIndAnulado = "INDANULADO";
        cPeriodo = GeneralParameterEnum.PERIODO.getName();
        cCiclo = GeneralParameterEnum.CICLO.getName();
        cFinanciado = "FINANCIADO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FINANCIABLESDEDEUDAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridFinanciable = (Map<String, Object>) parametrosEntrada
                                .get("ridFinanciable");
            }
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

        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaFormateado();
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
        enumBase = GenericUrlEnum.SP_FINANCIABLESDEDEUDA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesdedeudasControladorUrlEnum.URL12319
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    /**
     *
     * Carga la lista listaFormateado
     *
     */
    public void cargarListaFormateado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesdedeudasControladorUrlEnum.URL13081
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FinanciablesdedeudasControladorEnum.TIPO.getValue(), "25");
        listaFormateado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCodigoRuta
     */
    public void cargarListaCodigoRuta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesdedeudasControladorUrlEnum.URL13521
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo que se ejecuta al cambiar el abono Inicial
     */
    public void cambiarAbonoInicial() {
        double abonoInicial = Double.parseDouble(
                        registro.getCampos().get(cAbonoInicial).toString());
        double vraFinanciar = Double.parseDouble(
                        registro.getCampos().get(cVraFinanciar).toString());
        int numCuotas = Integer.parseInt(
                        registro.getCampos().get(cNoCuotas).toString());
        if (abonoInicial < vraFinanciar) {
            if (numCuotas != 0) {
                double valCuota = (vraFinanciar - abonoInicial) / numCuotas;
                int valorCuota = (int) SysmanFunciones.redondear(valCuota, 0);
                registro.getCampos().put(cValorCuota, valorCuota);
            }
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2781"));
            registro.getCampos().put(cAbonoInicial, "");
            return;
        }
    }

    /**
     * Metodo que se ejecuta al cambiar el Numero de Cuotas
     */
    public void cambiarNoCuotas() {
        double abonoInicial = Double.parseDouble(
                        registro.getCampos().get(cAbonoInicial).toString());
        double vraFinanciar = Double.parseDouble(
                        registro.getCampos().get(cVraFinanciar).toString());
        int numCuotas = Integer.parseInt(
                        registro.getCampos().get(cNoCuotas).toString());
        if (numCuotas > 0) {

            double valCuota = (vraFinanciar - abonoInicial) / numCuotas;
            int valorCuota = (int) SysmanFunciones.redondear(valCuota, 0);
            registro.getCampos().put(cValorCuota, valorCuota);
        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2782"));

            registro.getCampos().put(cNoCuotas, "1");
            double valCuota = (vraFinanciar - abonoInicial) / 1;
            int valorCuota = (int) SysmanFunciones.redondear(valCuota, 0);
            registro.getCampos().put(cValorCuota, valorCuota);
            return;
        }
    }

    /**
     * Metodo que se ejecuta al cambiar Periodos de no cobro
     * financiables
     */
    public void cambiarPerNoCobroFinan() {
        int noCobro = Integer.parseInt(
                        registro.getCampos().get(cPeriodosCobro).toString());
        if (noCobro < 1) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2783"));
            registro.getCampos().put(cPeriodosCobro, 1);
            return;
        }
    }

    /**
     * Metodo que se ejecuta al seleccionar o deseleccionar el check
     * Anular
     */
    public void cambiarchkAnular() {

        if (!SysmanFunciones.validarVariableVacio(bancoPer)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2790"));
            registro.getCampos().put(cIndAnulado, false);
            return;
        }
        if ((boolean) registro.getCampos().get(cIndAnulado)) {
            cargarAnular = true;
            cargarDialogoAnular = true; // Dialogo
                                        // aceptardialogoAnular
        }
        else {
            cargarDialogoAnular = false;
            cargarAnular = false;
        }

    }

    /**
     * Metodo que se ejecuta al seleccionar o deseleccionar el check
     * de maneja tercero
     */
    public void cambiarmanejaTercero() {
        if (manejaTercero) {
            activarTercero = false;
        }
        else {
            activarTercero = true;
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cUsuario,
                        registroAux.getCampos().get(cCodigoRuta));
        registro.getCampos().put(cNombreUsuario,
                        registroAux.getCampos().get("NOMBRE"));
        registro.getCampos().put(cVraFinanciar,
                        registroAux.getCampos().get("TOTFACTURAPERACTUAL"));
        nitUsuario = registroAux.getCampos().get("NIT").toString();
        nombreUsuario = registroAux.getCampos().get("NOMBRE").toString();
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), registroAux
                        .getCampos().get(GeneralParameterEnum.ANO.getName()));
        registro.getCampos().put(cPeriodo,
                        registroAux.getCampos().get(cPeriodo));

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        registroAux.getCampos().get(cCodigoRuta));
        Registro rsFinanciables = null;
        try {
            rsFinanciables = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesdedeudasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rsFinanciables != null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2784"));
            registro.getCampos().put(cUsuario, null);
            registro.getCampos().put(cNombreUsuario, null);
            registro.getCampos().put(cVraFinanciar, 0);
            return;
        }
        bancoPer = SysmanFunciones.nvl(
                        registroAux.getCampos().get("BANCOPERPROCESO"), "")
                        .toString();
        if (!SysmanFunciones.validarVariableVacio(bancoPer)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2785"));
            registro.getCampos().put(cUsuario, null);
            registro.getCampos().put(cNombreUsuario, null);
            registro.getCampos().put(cVraFinanciar, 0);
            return;
        }
        estrato = registroAux.getCampos().get("ESTRATO").toString();
        uso = registroAux.getCampos().get("USO").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCiclo,
                        registroAux.getCampos().get(cNumero));
        ciclo = registroAux.getCampos().get(cNumero).toString();
        cargarListaCodigoRuta();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormateado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormateado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        formato = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        fechaPlantilla = registroAux.getCampos().get(
                        GeneralParameterEnum.FECHA.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { cCiclo.toLowerCase(), "ano", cPeriodo.toLowerCase(),
                            "codigoInicial", "codigoFinal",
                            "marca", "marcaIni", "noReciboInicial",
                            "nombreInicial", "nombreFinal" };
        String[] valores = { registro.getCampos().get(cCiclo).toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.ANO
                                                             .getName())
                                             .toString(),
                             registro.getCampos().get(cPeriodo).toString(),
                             registro.getCampos().get(cUsuario).toString(),
                             registro.getCampos().get(cUsuario).toString(),
                             "4", "4", "0",
                             registro.getCampos().get(cNombreUsuario)
                                             .toString(),
                             registro.getCampos().get(cNombreUsuario)
                                             .toString() };
        SessionUtil.cargarModalDatosFlashCerrar("1076", modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton VerFactura en la vista
     *
     */
    public void oprimirVerFactura() {
        // <CODIGO_DESARROLLADO>
        cargarConsultaFacturacion();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton AcuerdoDePago en la vista
     *
     */
    public void oprimirAcuerdoDePago() {
        // <CODIGO_DESARROLLADO>
        if (formato.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2876"));
            return;
        }
        Date fechaActual = new Date();
        try {
            String strNombreDocumento = idioma.getString("TB_TB2817");
            strNombreDocumento = strNombreDocumento.replace("s$CodigoRuta$s",
                            registro.getCampos()
                                            .get(cUsuario).toString());
            strNombreDocumento = strNombreDocumento + " " + SysmanFunciones
                            .convertirAFechaCadena(fechaActual).replace("/", "")
                + "_"
                + SysmanFunciones.convertirAHoraCadena(fechaActual).replace(":",
                                "");

            String[] camposW = { "codigoPlantilla", "nombreDocDescarga",
                                 "fechaPlantilla" };
            String[] valoresW = { formato, strNombreDocumento, SysmanFunciones
                            .formatearFecha(SysmanFunciones
                                            .convertirAFecha(fechaPlantilla)) };
            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$modulo$s", "" + modulo + "");
            variablesConsultaW.put("s$nombreUsuario$s",
                            "'" + nombreUsuario + "'");
            variablesConsultaW.put("s$nitUsuario$s", "" + nitUsuario + "");
            variablesConsultaW.put("s$consecutivo$s",
                            "" + registro.getCampos().get(cConsecutivo) + "");
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash("281", SessionUtil.getModulo(),
                            camposW,
                            valoresW);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton boton_detallado en la
     * vista
     *
     */
    public void oprimirbotonDetallado() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "ciclo", "codigoRuta", "ano", "periodo",
                            "marcaDetalleFact" };
        String[] valores = { registro.getCampos().get(cCiclo).toString(),
                             registro.getCampos().get(cUsuario).toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.ANO
                                                             .getName())
                                             .toString(),
                             registro.getCampos().get(cPeriodo).toString(),
                             "2" };
        SessionUtil.cargarModalDatosFlash("1093", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton DatosTercero en la vista
     *
     */
    public void oprimirDatosTercero() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "ridFinanciableDeuda" };
        Object[] valores = { registro.getLlave() };
        SessionUtil.cargarModalDatosFlashCerrar("1291", modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que se ejecuta en el abrir Formulario, valida si los
     * parametros vienen nulos o no, si vienen nulos ejecuta un
     * mensaje de alerta
     */
    public void mensajesInicioModal() {
        try {
            String consutivoPago = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA CONSECUTIVO MANUAL EN ACUERDOS DE PAGO",
                            modulo,
                            new Date(), true);
            if (consutivoPago == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2776"));
                return;
            }
            String permiteTerceros = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE ACUERDOS DE PAGO A TERCEROS",
                            modulo,
                            new Date(), true);
            if (permiteTerceros == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2779"));
                return;
            }
            String verDetallado = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE VER DETALLADO ANTES DEL FINANCIABLE",
                            modulo,
                            new Date(), true);
            if (verDetallado == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2780"));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que devuelve verdadero si el valor del parametro es "si"
     * y falso si no es otro valor
     *
     * @param nombreParametro
     * @return true o false
     */
    private boolean valorParametro(String nombreParametro) {
        boolean respuesta = false;
        try {
            respuesta = "SI".equalsIgnoreCase(
                            ejbSysmanUtil.consultarParametro(compania,
                                            nombreParametro,
                                            modulo, new Date(), true));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;
    }

    /**
     * Metodo que retorna verdadero si la funcion
     * FC_ACTUALIZAFINANCIABLEDEUDA ejecuto correctamente
     *
     * @param accion
     * @return
     */
    public boolean actualizaFinanciable(String accion) {
        boolean rta = false;
        valAbonoIn = Double.parseDouble(
                        registro.getCampos().get(cAbonoInicial).toString());
        int nroCuotas = Integer.parseInt(
                        registro.getCampos().get(cNoCuotas).toString());
        try {
            if (ACCION_INSERTAR.equals(accion)) {
                if (valAbonoIn < 1) {
                    return numeroCuotas(nroCuotas);
                }
                else {
                    rta = ejbServiciosPublicosTres.actualizaFinanciabledeDeuda(
                                    compania,
                                    Integer.parseInt(registro.getCampos()
                                                    .get(cCiclo).toString()),
                                    registro.getCampos().get(cUsuario)
                                                    .toString(),
                                    Integer.parseInt(registro.getCampos()
                                                    .get(GeneralParameterEnum.ANO
                                                                    .getName())
                                                    .toString()),
                                    registro.getCampos().get(cPeriodo)
                                                    .toString(),
                                    BigDecimal.valueOf(valAbonoIn),
                                    new BigDecimal(registro.getCampos()
                                                    .get(cVraFinanciar)
                                                    .toString()),
                                    nroCuotas,
                                    SessionUtil.getUser().getCodigo(),
                                    sinAbonoIn,
                                    Integer.parseInt(registro.getCampos()
                                                    .get(cPeriodosCobro)
                                                    .toString()),
                                    consecutivo);
                }
                bloqueadoFactura = false;
                if (rta) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2802"));
                    registro.getCampos().put(cFinanciado, true);
                    return true;
                }
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Metodo llamado en actualizaFinanciable
     *
     * @param numeroCuotas
     * @return true o false
     */
    private boolean numeroCuotas(int numeroCuotas) {
        if (numeroCuotas <= 1) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2804"));
            return false;
        }
        else {
            cargarDialogoAbono = true; // aceptardialogoAbono
            return false;
        }
    }

    /**
     * Metodo que se ejecuta cuando al dalre clic al check anular
     * financiable, en el dialogo que aparece se da clic al boton si
     */
    public void aceptardialogoAnular() {
        try {
            ejbServiciosPublicosTres.anularFinanciabledeDeuda(compania,
                            Integer.parseInt(registro.getCampos().get(cCiclo)
                                            .toString()),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.USUARIO
                                                            .getName())
                                            .toString(),
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString()),
                            registro.getCampos().get(cPeriodo).toString(),
                            new BigDecimal(registro.getCampos()
                                            .get(cVraFinanciar).toString()));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(cIndAnulado, true);
        cargarAnular = true;
        registro.getCampos().put("FECHAANULACION",
                        new Date());
        registro.getCampos().put("USUARIOANULO",
                        SessionUtil.getUser().getCodigo());
        cargarDialogoAnular = false;
        bloqueadoAnular = true;
        agregarRegistroNuevo(false);
    }

    /**
     * Metodo que se ejecuta cuando al dalre clic al check anular
     * financiable, en el dialogo que aparece se da clic al boton no
     */
    public void cancelardialogoAnular() {
        registro.getCampos().put(cIndAnulado, false);
        cargarAnular = false;
        cargarDialogoAnular = false;
    }

    /**
     * Metodo que se ejecuta cuando al guardar el registro el valor
     * del abono inicial esta en 0, en el dialogo que aparece se da
     * clic al boton si
     */
    public void aceptardialogoAbono() {
        boolean rta = false;
        sinAbonoIn = true;
        int nroCuotas = Integer.parseInt(
                        registro.getCampos().get(cNoCuotas).toString());
        double vrFina = Double.parseDouble(
                        registro.getCampos().get(cVraFinanciar).toString());
        double valorAbono = vrFina / nroCuotas;
        valAbonoIn = valorAbono;
        try {
            rta = ejbServiciosPublicosTres.actualizaFinanciabledeDeuda(
                            compania,
                            Integer.parseInt(registro.getCampos().get(cCiclo)
                                            .toString()),
                            registro.getCampos().get(cUsuario).toString(),
                            Integer.parseInt(registro.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString()),
                            registro.getCampos().get(cPeriodo).toString(),
                            BigDecimal.valueOf(valAbonoIn),
                            new BigDecimal(registro.getCampos()
                                            .get(cVraFinanciar).toString()),
                            nroCuotas, SessionUtil.getUser().getCodigo(),
                            sinAbonoIn,
                            Integer.parseInt(registro.getCampos()
                                            .get(cPeriodosCobro).toString()),
                            consecutivo);
            if (rta) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2802"));
                registro.getCampos().put(cFinanciado, true);
            }
            agregarRegistroNuevo(true);
            bloqueadoFactura = false;
        }
        catch (NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarDialogoAbono = false;
        cargarRegistro(rid, ACCION_MODIFICAR);
        bloqueadoFactura = false;
        bloqueadoAnular = false;
        bloqueadoTercero = false;
    }

    /**
     * Metodo que se ejecuta cuando al guardar el registro el valor
     * del abono inicial esta en 0, en el dialogo que aparece se da
     * clic al boton no
     */
    public void cancelardialogoAbono() {
        cargarDialogoAbono = false;
    }

    /**
     * Metodo que se ejecuta cuando se da clic al boton Consultar
     * Facturacion
     */
    public void cargarConsultaFacturacion() {
        try {
            boolean bloqueado = ejbServiciosPublicosCuatro.estarBloqueado(
                            compania,
                            Integer.parseInt(registro.getCampos().get(cCiclo)
                                            .toString()));
            if (bloqueado) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3156"));
            }
            else {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(),
                                registro.getCampos().get(cCiclo));
                param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                registro.getCampos().get(cUsuario));

                Registro registroUsuario = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FinanciablesdedeudasControladorUrlEnum.URL0002
                                                                                .getValue())
                                                .getUrl(), param));

                Map<String, Object> parametrosForm = new HashMap<>();
                registroUsuario.getCampos().put("KEY_COMPANIA", compania);
                registroUsuario.getCampos().put("KEY_CICLO",
                                registroUsuario.getCampos().get(cCiclo));
                registroUsuario.getCampos().put("KEY_CODIGORUTA",
                                registroUsuario.getCampos()
                                                .get(GeneralParameterEnum.CODIGORUTA
                                                                .getName()));
                registroUsuario.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());
                registroUsuario.getCampos()
                                .remove(GeneralParameterEnum.CICLO.getName());
                registroUsuario.getCampos()
                                .remove(GeneralParameterEnum.CODIGORUTA
                                                .getName());
                parametrosForm.put("ridUsuario", registroUsuario.getCampos());
                parametrosForm.put("ciclo",
                                registro.getCampos().get(cCiclo).toString());
                parametrosForm.put("ano",
                                registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString());
                parametrosForm.put("periodo",
                                registro.getCampos().get(cPeriodo).toString());
                parametrosForm.put("ridFinanciable", registro.getLlave());

                Direccionador direccionador = new Direccionador();
                direccionador.setParametros(parametrosForm);
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FACTURA_CONTROLADOR
                                                .getCodigo()));

                SessionUtil.redireccionarForma(direccionador, modulo);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        mensajesInicioModal();
        cargarConsecutivoManual = valorParametro(
                        "MANEJA CONSECUTIVO MANUAL EN ACUERDOS DE PAGO");
        cargarDatosTercero = valorParametro(
                        "PERMITE ACUERDOS DE PAGO A TERCEROS");
        try {
            cargarFormato = ejbServiciosPublicosDos
                            .validarManejoCartas(
                                            compania,
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNit());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarDetallado = valorParametro(
                        "PERMITE VER DETALLADO ANTES DEL FINANCIABLE");

        // </CODIGO_DESARROLLADO>
        if ((ridFinanciable != null) && !ridFinanciable.isEmpty()) {
            cargarRegistro(ridFinanciable, ACCION_MODIFICAR);
        }
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            new Date());
            registro.getCampos().put(cNoCuotas, 1);
            registro.getCampos().put(cAbonoInicial, 0);
            registro.getCampos().put(cValorCuota, 0);
            registro.getCampos().put(cPeriodosCobro, 1);
            registro.getCampos().put("USUARIOSISTEMA",
                            SessionUtil.getUser().getCodigo());
            bloqueadoAnular = true;
            cargarAnular = false;
            bloqueadoFactura = true;
            bloqueadoTercero = true;
            activarTercero = true;
        }
        else if (ACCION_MODIFICAR.equals(accion)
            && ((boolean) registro.getCampos().get(cIndAnulado))) {
            bloqueadoAnular = true;
            cargarAnular = true;
            bloqueadoFactura = false;
            bloqueadoTercero = false;
            activarTercero = true;
        }
        else {
            bloqueadoAnular = false;
            cargarAnular = false;
            bloqueadoFactura = false;
            bloqueadoTercero = false;
            activarTercero = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SP_FINANCIABLESDEDEUDA",
                            " SP_FINANCIABLESDEDEUDA.COMPANIA = ''"
                                + compania + "''",
                            cConsecutivo, "1");
            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().remove(cNombreUsuario);
        registro.getCampos().put("ESTRATO", estrato);
        registro.getCampos().put("USO", uso);
        registro.getCampos().put("USUARIOSISTEMA",
                        SessionUtil.getUser().getCodigo());
        // </CODIGO_DESARROLLADO>
        return actualizaFinanciable(accion);
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cFinanciado, true);
        bloqueadoFactura = false;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos()
                            .remove(cNombreUsuario);
        }
        Registro rsConsecutivo = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos().get(cConsecutivoManual));
            rsConsecutivo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesdedeudasControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rsConsecutivo != null) {
            String msj = idioma.getString("TB_TB2786");
            msj = msj.replace("s$consecutivoManual$s", registro.getCampos()
                            .get(cConsecutivoManual).toString());
            JsfUtil.agregarMensajeError(msj);
            registro.getCampos().put(cConsecutivoManual, "");
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return true
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isCargarConsecutivoManual() {
        return cargarConsecutivoManual;
    }

    public void setCargarConsecutivoManual(boolean cargarConsecutivoManual) {
        this.cargarConsecutivoManual = cargarConsecutivoManual;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public boolean isManejaTercero() {
        return manejaTercero;
    }

    public void setManejaTercero(boolean manejaTercero) {
        this.manejaTercero = manejaTercero;
    }

    public boolean isCargarDatosTercero() {
        return cargarDatosTercero;
    }

    public void setCargarDatosTercero(boolean cargarDatosTercero) {
        this.cargarDatosTercero = cargarDatosTercero;
    }

    public boolean isCargarFormato() {
        return cargarFormato;
    }

    public void setCargarFormato(boolean cargarFormato) {
        this.cargarFormato = cargarFormato;
    }

    public boolean isCargarDetallado() {
        return cargarDetallado;
    }

    public void setCargarDetallado(boolean cargarDetallado) {
        this.cargarDetallado = cargarDetallado;
    }

    public boolean isCargarAnular() {
        return cargarAnular;
    }

    public void setCargarAnular(boolean cargarAnular) {
        this.cargarAnular = cargarAnular;
    }

    public boolean isCargarDialogoAnular() {
        return cargarDialogoAnular;
    }

    public void setCargarDialogoAnular(boolean cargarDialogoAnular) {
        this.cargarDialogoAnular = cargarDialogoAnular;
    }

    public boolean isBloqueadoAnular() {
        return bloqueadoAnular;
    }

    public void setBloqueadoAnular(boolean bloqueadoAnular) {
        this.bloqueadoAnular = bloqueadoAnular;
    }

    public boolean isCargarDialogoAbono() {
        return cargarDialogoAbono;
    }

    public void setCargarDialogoAbono(boolean cargarDialogoAbono) {
        this.cargarDialogoAbono = cargarDialogoAbono;
    }

    public boolean isBloqueadoFactura() {
        return bloqueadoFactura;
    }

    public void setBloqueadoFactura(boolean bloqueadoFactura) {
        this.bloqueadoFactura = bloqueadoFactura;
    }

    public boolean isBloqueadoTercero() {
        return bloqueadoTercero;
    }

    public void setBloqueadoTercero(boolean bloqueadoTercero) {
        this.bloqueadoTercero = bloqueadoTercero;
    }

    public boolean isActivarTercero() {
        return activarTercero;
    }

    public void setActivarTercero(boolean activarTercero) {
        this.activarTercero = activarTercero;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoRuta
     *
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     *
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaFormateado
     *
     * @return listaFormateado
     */
    public RegistroDataModelImpl getListaFormateado() {
        return listaFormateado;
    }

    /**
     * Asigna la lista listaFormateado
     *
     * @param listaFormateado
     * Variable a asignar en listaFormateado
     */
    public void setListaFormateado(RegistroDataModelImpl listaFormateado) {
        this.listaFormateado = listaFormateado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
