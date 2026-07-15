/*-
 * FrminfooperacionControlador.java
 *
 * 1.0
 *
 * 02 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyacá. All rights
 * reserved.
 *
 * Formulario que permite realizar el registro de operaciones.
 *
 */

package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FrminfooperacionControladorEnum;
import com.sysman.serviciospublicos.enums.FrminfooperacionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite realizar el registro de las diferentes
 * operaciones y novedades del registro de operaciones en el modulo de
 * servicios publicos.
 *
 * @author lcortes
 * @version 2, 02/09/2016 11:11:06 -- Modificado por lcortes
 * 
 * @version 3.0, 30/05/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Reemplazar el getLlave por el getLlaveServicio del CacheUtil.<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class FrminfooperacionControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante que identifica el codigo de la compania.
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el que se abre el formulario.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * esta interactuando con el formulario.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CREATED_BY</code>
     */
    private final String cCreatedBy;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.DATE_CREATED</code>
     */
    private final String cDateCreated;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * <code>FrminfooperacionControladorEnum.CAMPO</code>
     */
    private final String cCampo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * {@code GeneralParameterEnum.CONSECUTIVO}
     */
    private final String cConsecutivo;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code FrminfooperacionControladorEnum.ESTADO_OPE}
     */
    private final String cEstadoOpe;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * <code>FrminfooperacionControladorEnum.USUARIO</code>
     */
    private final String cUsuario;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.AFORADOR</code>
     */
    private final String cAforador;

    /**
     * Constante a nivel de clase que aloja el valor
     * <code>FrminfooperacionControladorEnum.TOTALFINANCIABLE</code>
     */
    private final String cTotalFinanciable;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * {@code GeneralParameterEnum.FECHA}
     */
    private final String cFecha;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * {@code GeneralParameterEnum.NOMBRE}
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja el valor del enumerado
     * {@code FrminfooperacionControladorEnum.TIPO_OPERACION }
     */
    private final String cTipoOperacion;

    /**
     * Constante que identifica el nombre de la tabla
     * SP_INFO_TIPO_OPERACION
     */
    private final String subInfoOperacion;
    /**
     * Constante que identifica el nombre de la tabla SP_FINANCIABLES
     */
    private final String subFinanciable;

    /**
     * Constante que identifica el nombre del campo CODIGORUTA
     */
    private final String campoCodRuta;
    /**
     * Constante que identifica el nombre del campo ESTADO
     */
    private final String campoEstado;

    /**
     * Constante que identifica el nombre del campo CONCEPTO
     */
    private final String campoConcepto;

    /**
     * Constante que identifica el nombre del campo LECTURA
     */
    private final String campoLectura;
    /**
     * Constante que identifica el nombre del campo VALORCUOTA
     */
    private final String campoValCuota;
    /**
     * Constante que identifica el nombre del campo CONCEPTO
     */
    private final String campoNomConcepto;
    /**
     * Constante que identifica el nombre del campo PERIODO
     */
    private final String campoPeriodo;
    /**
     * Constante que identifica el nombre del campo VALOR
     */
    private final String campoValor;
    /**
     * Constante que identifica el nombre del campo BLOQUEADO
     */
    private final String campoBloqueado;
    /**
     * Constante que identifica el nombre del campo NROCUOTA
     */
    private final String campoNroCuota;
    /**
     * Constante que identifica el nombre del campo BLOQUEADOHASTAANO
     */
    private final String campoBloqHastaAnio;
    /**
     * Constante que identifica el nombre del campo
     * BLOQUEADOHASTAPERIODO
     */
    private final String campoBloqHastaPeriodo;
    /**
     * Constante que identifica el nombre del campo BANCOPERPROCESO
     */
    private final String campoBancoPerProc;
    /**
     * Constante que identifica el nombre del campo SALDOFINANCIABLE
     */
    private final String campoSaldoFinan;
    /**
     * Constante que identifica el nombre del campo MONTOFINANCIAR
     */
    private final String campoMontoFinanciar;
    /**
     * Constante que identifica el nombre del campo FORMATO
     */
    private final String campoFormato;
    /**
     * Constante que identifica el nombre del campo FECHAACTA
     */
    private final String campoFechaActa;
    /**
     * Constante que identifica el nombre del campo NOMBREACTA
     */
    private final String campoNomActa;
    /**
     * Constante que identifica el nombre del campo NUMEROCUOTAS
     */
    private final String campoNumCuotas;
    /**
     * Constante que identifica el nombre del campo FECHAEJECUCION
     */
    private final String campoFechaEjec;
    /**
     * Constante que identifica el nombre del campo HORAEJECUCION
     */
    private final String campoHoraEjec;

    /**
     * Constante que identifica el mensaje que contiene la propiedad
     * TB_TB1632
     */
    private final String mensAnioSubFin;
    /**
     * Constante que identifica el mensaje que contiene la propiedad
     * TB_TB1884
     */
    private final String mensFormato;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que controla el valor seleccionado en el check:
     * <code>Bloq.</code>. Aplica en la pestania: <code>Novedad</code>
     */
    private boolean bloqueado;

    /**
     * Atributo que controla la visibilidad del dialogo:
     * <code>mensajeFinan</code>
     */
    private boolean verMensajeFinan;

    /**
     * Atributo que contiene la <code>FACTURA</code> del codigo de
     * ruta seleccionado
     */
    private String facturaFin;

    /**
     * Variable que identifica el codigo de ruta al que se realizara
     * el registro de operaciones.
     */
    private String codigoRuta;
    /**
     * Variable que identifica el ciclo al cual pertenece el codigo de
     * ruta.
     */
    private String ciclo;
    /**
     * Variable que almacena el anio para el cual esta habilitado el
     * registro de operaciones.
     */
    private String anio;

    /**
     * Atributo que controla el codigo del aforador seleccionado en el
     * combo Aforador
     */
    private String aforador;

    /**
     * Atributo que contiene el valor asignado al campo lectura de las
     * pestania Operacion
     */
    private String lectura;

    /**
     * Variable que almacena el periodo para el cual esta habilitado
     * el registro de operaciones.
     */
    private String periodo;
    /**
     * Variable para identificar el nombre del usuario al que
     * corresponde el codigo de ruta.
     */
    private String nombre;
    /**
     * Variable para identificar la direccion del usuario al que
     * corresponde el codigo de ruta.
     */
    private String dirTecnica;

    /**
     * Atributo que contiene el valor asignado en el campo descripcion
     * de la pestaña operacion
     */
    private String descripcion;

    /**
     * Variable para identificar el codigo interno del usuario al que
     * corresponde el codigo de ruta.
     */
    private String codigoInterno;

    /**
     * Variable que gestiona el tipo o tipos de modelo de plantilla
     */
    private String tipoPlantilla;

    /**
     * Atributo que controla el valor ingresado en el campo tipo
     * operacion
     */
    private String tipoOperacion;

    /**
     * Atributo que contiene los conceptos que se deben mostrar en la
     * lista de conceptos de la pestaña: <code>Novedad</code>
     */
    private StringBuilder codigosConcepto;

    /**
     * Atributo que contiene el valor: <code>PERIODOSNOCOBROFIN</code>
     * asociado al codigo de ruta seleccionado
     */
    private String perNoCobroFin;

    /**
     * Atributo que contiene el valor asignado en el campo Estado
     * Operacion
     */
    private String estadoOperacion;

    /**
     * Atributo que contiene el valor asignado al campo estado en la
     * pestaña operacion
     */
    private String estado;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de años.
     */
    private List<Registro> listacmbAno;
    /**
     * Lista de registros de Periodos
     */
    private List<Registro> listacmbPeriodo;
    /**
     * Lista de registros de Periodos
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de Medidor
     */
    private RegistroDataModelImpl listaMedidor;
    /**
     * Lista de registros de Medidor en la grilla de edicion
     */
    private RegistroDataModelImpl listaMedidorE;
    /**
     * Lista de registros de la lista de formatos permitidos para el
     * ciclo
     */
    private RegistroDataModelImpl listaFormateado;
    /**
     * Lista de registros de la lista de formatos en la grilla de
     * edicion
     */
    private RegistroDataModelImpl listaFormateadoE;

    /** Lista asociada al combo de aforadores */
    private RegistroDataModelImpl listaAforador;

    /** Lista asociada al combo de aforadores. */
    private RegistroDataModelImpl listaAforadorE;

    /**
     * Variable usada como auxiliar para subformularios y en esta se
     * alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;
    /**
     * Lista de registros de Conceptos.
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Lista de registros de Conceptos en la grilla de edicion.
     */
    private RegistroDataModelImpl listaConceptoE;
    /**
     * Lista de registros de Codigos de Ruta.
     */
    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que contiene los registros de la taba
     * SP_INFO_TIPO_OPERACION
     */
    private RegistroDataModel listaSubinfooperacion;
    /**
     * Lista que contiene los registros de la taba SP_FINANCIABLES
     */
    private RegistroDataModelImpl listaSubfinanciables;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que indica si el valor del parametro:
     * <code>ESTADO OPERACION Y DESCRIPCION EN OPERACIONES</code> esta
     * definido en <code>SI</code>.
     */
    private boolean parEstadoOperacion;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Variable que contiene los campos que componen la llave primaria
     * de la tabla SP_INFO_TIPO_OPERACION. Atributo de referencia para
     * el subformulario
     */
    private Registro registroSubSubInfoOperacion;
    /**
     * Variable que contiene los campos que componen la llave primaria
     * de la tabla SP_FINANCIABLES. Atributo de referencia para el
     * subformulario
     */
    private Registro registroSubSubFinanciables;

    /**
     * Atributo que almacena el registro que esta siendo siendo
     * editado por el usuario en la grilla del subformulario:
     * <code>Registro de Operaciones</code>>
     */
    private Registro regSubInfoOperacion;

    /**
     * Variable creada para guardar el registro de la grilla antes de
     * ser editado el registro seleccionado en el subformulario
     * SubInfoOperacion
     */
    private Registro regSubInfoOpeAntEd;

    /**
     * Atributo que contiene el <code>ESTADO</code> del codigo de ruta
     * seleccionado
     */
    private String estadoUsuario;

    /**
     * Atributo que contiene el <code>BANCOPERPROCESO</code> asociado
     * al codigo de ruta seleccionado
     */
    private String bancoPerProcesoUsuario;

    /**
     * Atributo que contiene el <code>TOTFACTURAPERACTUAL</code>
     * asociado al codigo de ruta seleccionado
     */
    private String totFacturaPerActualUsuario;

    /**
     * Atributo que contiene el <code>DEUDA</code> asociado al codigo
     * de ruta seleccionado
     */
    private String deudaUsuario;

    /**
     * Atributo que almacena el numero de <code>PERIODOSATRASO</code>
     * del codigo de ruta seleccionado
     */
    private String periodosAtrasoUsuario;

    /**
     * Atributo que controla el mensaje a mostrar en el dialogo
     * <code>DialogoPregunta</code>
     */
    private String mensajeDPreg;

    /**
     * Variable del contenido del mensaje que muestra el dialogo
     * ValTotFactura.
     */
    private String mensValTotal;

    /**
     * Variable del contenido del mensaje que muestra el dialogo
     * DG115.
     */
    private String mensFinanciable;
    /**
     * Variable que permite almacenar el valor del campo CONSECUTIVO
     * de la Operacion seleccionada en el subformulario
     * SubInfoOPeracion al realizar la edicion.
     */
    private String consecutivo;

    /**
     * Variable que permite identificar el anio inicial para el
     * SubFormulario Financiables
     */
    private String anioIniSF;

    /**
     * Variable que permite identificar el periodo inicial para el
     * SubFormulario Financiables
     */
    private String periodoIniSF;

    /**
     * Variable que permite identificar el saldo financiable para el
     * SubFormulario Financiables
     */
    private String saldoFinanciable;

    /**
     * Variable que permite identificar el numero de cuotas de para el
     * SubFormulario Financiables
     */
    private String numeroCuotas;
    /**
     * Variable que permite identificar el numero de la cuota del
     * SubFormulario Financiables
     */
    private String nroCuota;
    /**
     * Variable que permite identificar el valor de la cuota para el
     * SubFormulario Financiables
     */
    private String valorCuota;

    /**
     * Variable que permite identificar el valor del monto financiable
     * para el SubFormulario Financiables
     */
    private String montoFinanciar;
    /**
     * Variable que permite identificar periodo seleccionado en el
     * registro de una novedad en el SubFormulario Financiables
     */
    private String bloqHasPeriodo;
    /**
     * Variable que permite identificar el anio seleccionado en el
     * registro para el SubFormulario Financiables
     */
    private String bloqHasAnio;
    /**
     * Variable que permite mostrar la suma del Monto Financiar en el
     * pie de pagina del subformulario SubFinanciable.
     */
    private String sumMontoFin;
    /**
     * Variable que permite mostrar la suma Saldo Financiable en el
     * pie de pagina del subformulario SubFinanciable.
     */
    private String sumSaldoFin;
    /**
     * Variable que permite mostrar la suma Valor Cuota en el pie de
     * pagina del subformulario SubFinanciable.
     */
    private String sumValorCuota;

    /**
     * Variable que se envia como parametro de la consulta de la
     * plantilla a imprimir.
     */
    private String claseOperacion;
    /**
     * Variable que identifica el campo BANCOPERPROCESO para el
     * SubFormulario Financiables
     */
    String banPerProceso;

    /**
     * Variable que identifica el campo FACTURA para el SubFormulario
     * Financiables
     */
    String fact;
    /**
     * Variable que permite asignar el titulo del campo fecha en la
     * grilla del subformulario SubInfoOperacion
     */
    private String etiquetaFecha;

    /**
     * Variable que permite hacer visible el campo con la alerta de
     * que el suscriptor tiene registrado un fraude.
     */
    private boolean conFraude;
    /**
     * Variable que permite hacer visible el diálogo ValTotFactura.
     */
    private boolean mostrarMensVal;
    /**
     * Variable que permite hacer visible el diálogo DG114
     */
    private boolean mostrarMensDeuda;
    /**
     * Variable que permite hacer visible el diálogo DG115.
     */
    private boolean mostrarMensFinan;

    /**
     * Atributo que controla la visibilidad del dialogo:
     * <code>DialogoPregunta</code>
     */
    private boolean verDPreg;

    /**
     * Variable que permite hacer visible el diálogo DG124.
     */
    private boolean verMenRecFact;

    /**
     * Variable para determinar si el subformulario SubInfoOperacion
     * es editable dependiendo el valor del parámetro ESTADO OPERACION
     * Y DESCRIPCION EN OPERACIONES.
     */
    private boolean editarRegSubInfoOp;

    /**
     * Atributo de control que gestiona la visibilidad de los campos
     * fecha y hora de ejecucion en la grilla
     */
    private boolean verFechaHoraEjecucion;

    /**
     * Atributo de control que permite gestionar la visibilidad del
     * campo <code>Usuario Creador</code>
     */
    private boolean verUsuario;

    /**
     * Variable que permite determinar si el campo estado operacion es
     * visible en la grilla del subformulario SubInfoOperacion.
     */
    private boolean verEstadoOpe;

    /**
     * Variable que permite determinar si el campo hora creacion es
     * visible en la grilla del subformulario SubInfoOperacion.
     */
    private boolean verHoraCreacion;

    /**
     * Atributo que controla el bloqueo de los campos fecha y hora de
     * ejecucion en la grilla de la pestaña Operacion.
     */
    private boolean bloquearFechaHoraE;

    /**
     * Variable que permite almacenar el valor del indice de la fila
     * del subformulario SubInfoOperacion que va a ser editada.
     */
    private int indiceSubinfooperacion;
    /**
     * Variable que permite almacenar el valor del indice de la fila
     * del subformulario SubFinanciables que va a ser editada.
     */
    private int indiceSubfinanciables;

    // </DECLARAR_ADICIONALES>

    // <DECLARAR_EJBs>
    /**
     * Instancia que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Instancia que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SERVICIOS_PUBLICOS_COM2</code>
     */
    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    /**
     * Instancia que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SERVICIOS_PUBLICOS_COM3</code>
     */
    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    /**
     * Instancia que permite utilizar las funciones y procedimientos
     * del paquete: <code>PCK_SERVICIOS_PUBLICOS_COM7</code>
     */
    @EJB
    private EjbServiciosPublicosSieteRemote ejbServiciosPublicosSiete;

    // </DECLARAR_EJBs>
    /**
     * Crea una nueva instancia de FrminfooperacionControlador
     */
    public FrminfooperacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        cCreatedBy = GeneralParameterEnum.CREATED_BY.getName();
        cDateCreated = GeneralParameterEnum.DATE_CREATED.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCampo = FrminfooperacionControladorEnum.CAMPO.getValue();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        cEstadoOpe = FrminfooperacionControladorEnum.ESTADO_OPE.getValue();
        cUsuario = FrminfooperacionControladorEnum.USUARIO.getValue();
        cAforador = GeneralParameterEnum.AFORADOR.getName();
        cFecha = GeneralParameterEnum.FECHA.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();

        cTotalFinanciable = FrminfooperacionControladorEnum.TOTALFINANCIABLE
                        .getValue();

        cTipoOperacion = FrminfooperacionControladorEnum.TIPO_OPERACION
                        .getValue();

        subInfoOperacion = "SP_INFO_TIPO_OPERACION";
        subFinanciable = "SP_FINANCIABLES";
        campoCodRuta = "CODIGORUTA";
        campoEstado = "ESTADO";
        campoConcepto = "CONCEPTO";
        campoLectura = "LECTURA";
        campoValCuota = "VALORCUOTA";
        campoNomConcepto = "NOMCONCEPTO";
        campoPeriodo = "PERIODO";
        campoValor = "VALOR";
        campoBloqueado = "BLOQUEADO";
        campoNroCuota = "NROCUOTA";
        campoBloqHastaAnio = "BLOQUEADOHASTAANO";
        campoBloqHastaPeriodo = "BLOQUEADOHASTAPERIODO";
        campoBancoPerProc = "BANCOPERPROCESO";
        campoSaldoFinan = "SALDOFINANCIABLE";
        campoMontoFinanciar = "MONTOFINANCIAR";
        campoFormato = "FORMATO";
        campoFechaActa = "FECHAACTA";
        campoNomActa = "NOMBREACTA";
        campoNumCuotas = "NUMEROCUOTAS";
        campoFechaEjec = "FECHAEJECUCION";
        campoHoraEjec = "HORAEJECUCION";
        mensAnioSubFin = "TB_TB1632";
        mensFormato = "TB_TB1884";

        try {
            // 1066
            numFormulario = GeneralCodigoFormaEnum.FRMINFOOPERACION_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            registro = new Registro(new HashMap<String, Object>());
            registroSubSubInfoOperacion = new Registro(
                            new HashMap<String, Object>());
            registroSubSubFinanciables = new Registro(
                            new HashMap<String, Object>());
            regSubInfoOperacion = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubinfooperacion();
        cargarListaSubfinanciables();
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubinfooperacion = null;
        listaSubfinanciables = null;
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
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo para cargar los registros del sub formulario
     * subinfooperacion, pestana Operacion del formulario.
     */
    public void cargarListaSubinfooperacion() {
        List<Registro> listaFormato = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(FrminfooperacionControladorEnum.MODULO.getValue(), modulo);
        param.put(FrminfooperacionControladorEnum.TIPO.getValue(),
                        tipoPlantilla);

        try {
            listaFormato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL42914
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (listaFormato == null) {
            return;
        }

        StringBuilder nombreActa = new StringBuilder(" CASE");

        // for (Registro registro : listaFormato) {
        // nombreActa.append(" WHEN FORMATO = "
        // + listaFormato.get(registro.getIndice()).getCampos()
        // .get(cCodigo)
        // + " AND FECHAACTA = "
        // + SysmanFunciones.formatearFecha((Date) listaFormato
        // .get(registro.getIndice()).getCampos()
        // .get(cFecha))
        // + " THEN '"
        // + listaFormato.get(registro.getIndice()).getCampos()
        // .get(cNombre)
        // + "' ");
        // }

        for (Registro registro : listaFormato) {
            nombreActa.append(" WHEN FORMATO = '");
            nombreActa.append(registro.getCampos().get(cCodigo).toString());
            nombreActa.append("' AND FECHAACTA = ");
            nombreActa.append(SysmanFunciones.formatearFecha(
                            (Date) registro.getCampos().get(cFecha)));

            nombreActa.append(" THEN '");
            nombreActa.append(registro.getCampos().get(cNombre).toString());
            nombreActa.append("' ");

            /*-     String formato = registro.getCampos().get(cCodigo).toString();
            
            try {
                nombreActa.append(formato
                    + SysmanFunciones.convertirAFechaCadena(
                                    (Date) registro.getCampos().get(cFecha)));
            
                nombreActa.append(","
                    + registro.getCampos().get(cNombre).toString());
            
                nombreActa.append(",");
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }*/
        }

        // nombreActa.deleteCharAt(nombreActa.length() - 1);
        nombreActa.append(" END NOMBREACTA");

        try {
            listaSubinfooperacion = new RegistroDataModel(
                            ConectorPool.ESQUEMA_SYSMAN,
                            ":FR1066_nuevo:TS47:tablePL2052",
                            "SELECT SPI.COMPANIA, " +
                                "     SPI.CICLO, " +
                                "     SPI.CODIGORUTA, " +
                                "     SPI.CONSECUTIVO, " +
                                "     SPI.FECHA, " +
                                "     SPI.TIPO_OPERACION," +
                                "     CASE SPI.TIPO_OPERACION WHEN '001' THEN 'SUSPENSION'"
                                +
                                "                             WHEN '002' THEN 'CORTE' "
                                +
                                "                             WHEN '003' THEN 'RECONEXION'"
                                +
                                "                             WHEN '004' THEN 'REINSTALACION'"
                                +
                                "     END NOM_TIPOOPERACION," +
                                "     SPI.ESTADO, " +
                                "     CASE SPI.ESTADO WHEN 'A' THEN 'Activo'"
                                +
                                "                     WHEN 'C' THEN 'Cortado' "
                                +
                                "                     WHEN 'S' THEN 'Suspendido'"
                                +
                                "                     WHEN 'R' THEN 'Retirado'"
                                +
                                "     END NOM_ESTADOETAPA," +
                                "     SPI.AFORADOR," +
                                "     TERCERO.NOMBRE NOM_AFORADOR, " +
                                "     SPI.DESCRIPCION, " +
                                "     SPI.LECTURA, " +
                                "     SPI.MEDIDOR, " +
                                "     SPI.SELLO_RETIRADO, " +
                                "     SPI.SELLO_INSTALADO, " +
                                "     SPI.FECHAEJECUCION, " +
                                "     SPI.HORACREACION, " +
                                "     SPI.HORAEJECUCION, " +
                                "     SPI.DATE_CREATED, " +
                                "     SPI.CREATED_BY, " +
                                "     SPI.DATE_MODIFIED, " +
                                "     SPI.MODIFIED_BY, " +
                                "     SPI.ESTADO_OPE, " +
                                "     CASE SPI.ESTADO_OPE WHEN 'E' THEN 'Ejecutada'"
                                +
                                "                         WHEN 'N' THEN 'No Ejecutada'"
                                +
                                "     END NOM_ESTADO_OPE," +
                                "     SPI.FORMATO, " +
                                "     SPI.FECHAACTA, " +
                                "     SPI.TIPOACTA, " +
                                "     SPI.USUARIO,        " +
                                "     " + nombreActa +
                                " FROM SP_INFO_TIPO_OPERACION SPI" +
                                "     INNER JOIN SP_AFORADORES AFORADORES  "
                                +
                                "       ON SPI.COMPANIA = AFORADORES.COMPANIA "
                                +
                                "       AND SPI.AFORADOR = AFORADORES.CODIGO"
                                +
                                "     INNER JOIN TERCERO " +
                                "       ON AFORADORES.COMPANIA = TERCERO.COMPANIA"
                                +
                                "       AND AFORADORES.NIT      = TERCERO.NIT "
                                +
                                "       AND AFORADORES.SUCURSAL = TERCERO.SUCURSAL "
                                +
                                " WHERE SPI.COMPANIA = '" + compania + "'" +
                                "   AND SPI.CICLO = " + ciclo +
                                "   AND SPI.CODIGORUTA = '" + codigoRuta
                                + "' "
                                +
                                " ORDER BY CONSECUTIVO ",
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            subInfoOperacion));
        }
        catch (SysmanException e) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo para cargar los registros del sub formulario
     * subfinanciables, pestana Novedad del formulario.
     */
    public void cargarListaSubfinanciables() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminfooperacionControladorUrlEnum.URL0001
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(FrminfooperacionControladorEnum.MI_ANIO.getValue(), anio);
            param.put(FrminfooperacionControladorEnum.MI_PERIODO.getValue(),
                            periodo);

            listaSubfinanciables = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            subFinanciable));

        }
        catch (SysmanException e) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que permite cargar la lista de los codigos y nombres de
     * los aforadores.
     */
    public void cargarListaAforador() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL31413
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista: <code>listaAforadorE</code> asociada al combo:
     * <code>Aforador</code> al crear un nuevo registro de operacion.
     */
    public void cargarListaAforadorE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL31413
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAforadorE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Metodo que carga el listado de anios en los cuales se puede
     * registrar una novedad.
     */
    public void cargarListacmbAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL33171
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que carga la lista de periodos en los cuales se puede
     * registrar la novedad, dependiendo el anio seleccionado.
     */
    public void cargarListacmbPeriodo() {
        String mes = anio.equals(SysmanFunciones
                        .nvl(registroSubSubFinanciables.getCampos()
                                        .get(campoBloqHastaAnio), "0")
                        .toString()) ? periodo : "01";

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listacmbPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL30892
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite cargar el listado de los ciclos registrados.
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL40032
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite listar los medidores disponibles para el
     * ciclo y codigo de ruta seleccionados previamente.
     */
    public void cargarListaMedidor() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL27177
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

        listaMedidor = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    /**
     * Metodo que permite listar los medidores disponibles para el
     * ciclo y codigo de ruta seleccionados previamente.
     */
    public void cargarListaMedidorE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL27177
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

        listaMedidorE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    /**
     * Metodo que lista los modelos de plantillas
     */
    public void cargarListaFormateado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL41509
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(FrminfooperacionControladorEnum.MODULO.getValue(), modulo);
        param.put(FrminfooperacionControladorEnum.MI_TIPOS.getValue(),
                        tipoPlantilla);

        listaFormateado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Metodo que lista los modelos de plantillas en la grilla del
     * formulario.
     */
    public void cargarListaFormateadoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL41509
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(FrminfooperacionControladorEnum.MODULO.getValue(), modulo);
        param.put(FrminfooperacionControladorEnum.MI_TIPOS.getValue(),
                        tipoPlantilla);

        listaFormateadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Metodo que permite listar los conceptos
     */
    public void cargarListaConcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL29800
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(),
                        codigosConcepto.toString());

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     * Metodo que permite listar los conceptos en la grilla del
     * formulario.
     */
    public void cargarListaConceptoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL29800
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(),
                        codigosConcepto.toString());

        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Metodo que permite listar los codigos de ruta correspondientes
     * al ciclo seleccionado.
     */
    public void cargarListaCodigoRuta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfooperacionControladorUrlEnum.URL32035
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrminfooperacionControladorEnum.MI_CICLO.getValue(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo que se ejecuta cuando se selecciona un ciclo de la lista
     * Ciclo
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoRuta = null;
        codigoInterno = null;
        nombre = null;
        dirTecnica = null;
        conFraude = false;
        listaSubinfooperacion = null;
        listaSubfinanciables = null;

        cargarListaCodigoRuta();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al seleccionar un tipo de operacion en el
     * subformulario SubInfoOperacion
     */
    public void cambiarTipoOperacion() {
        // <CODIGO_DESARROLLADO>
        tipoOperacion = SysmanFunciones.nvl(registroSubSubInfoOperacion
                        .getCampos().get(cTipoOperacion), "").toString();

        listaFormateado = null;
        registroSubSubInfoOperacion.getCampos().put(campoFormato, "");

        validarCondicionFormato(registroSubSubInfoOperacion);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    /**
     * Metodo que permite asignar la condicion para cargar la lista de
     * formatos validos para el tipo de operacion seleccionado.
     *
     * @param reg:
     * registros del subformulario SubInfoOperacion
     */
    private void validarCondicionFormato(Registro reg) {
        listaFormateado = listaFormateadoE = null;

        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        cTipoOperacion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1881"));

            return;
        }

        String tipoOperacion = reg.getCampos().get(cTipoOperacion)
                        .toString();

        // Corte y suspension
        if ("001,002".contains(tipoOperacion)) {
            tipoPlantilla = "28";
            claseOperacion = "";
        }
        else { // Reconexion y reinstalacion
            tipoPlantilla = "30";
            claseOperacion = ("003").equals(tipoOperacion) ? "01" : "02";
        }

        cargarListaFormateado();
        cargarListaFormateadoE();
    }

    /**
     * Metodo ejecutado al cambiar el control Estado
     */
    public void cambiarEstado() {
        // <CODIGO_DESARROLLADO>
        estado = SysmanFunciones.nvl(registroSubSubInfoOperacion
                        .getCampos().get("ESTADO"), "").toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EstadoOperacion
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarEstadoOperacion() {
        // <CODIGO_DESARROLLADO>
        estadoOperacion = SysmanFunciones.nvl(registroSubSubInfoOperacion
                        .getCampos().get("ESTADO_OPE"), "").toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Descripcion
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarDescripcion() {
        // <CODIGO_DESARROLLADO>
        descripcion = SysmanFunciones.nvl(registroSubSubInfoOperacion
                        .getCampos().get("DESCRIPCION"), "").toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Lectura
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarLectura() {
        // <CODIGO_DESARROLLADO>
        lectura = SysmanFunciones
                        .nvl(registroSubSubInfoOperacion.getCampos()
                                        .get("LECTURA"), "")
                        .toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbAno en el
     * subformulario SubFinanciables
     */
    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        bloqHasAnio = SysmanFunciones
                        .nvl(registroSubSubFinanciables.getCampos()
                                        .get(campoBloqHastaAnio), "0")
                        .toString();

        if (Integer.parseInt(bloqHasAnio) < Integer.parseInt(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensAnioSubFin));

            listacmbPeriodo = null;
        }
        else {
            registroSubSubFinanciables.getCampos().put(campoBloqHastaPeriodo,
                            "");

            cargarListacmbPeriodo();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbPeriodo en el
     * subformulario SubFinanciables
     */
    public void cambiarcmbPeriodo() {
        bloqHasPeriodo = (String) registroSubSubFinanciables.getCampos()
                        .get(campoBloqHastaPeriodo);
    }

    /**
     * Metodo ejecutado al cambiar el control MontoFinanciar
     */
    public void cambiarMontoFinanciar() {
        // <CODIGO_DESARROLLADO>
        if (!("0").equals(registroSubSubFinanciables.getCampos()
                        .get(campoMontoFinanciar))) {
            calcularValorCuota();

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que permite calcular el valor de las cuotas del
     * financiable
     */
    private void calcularValorCuota() {
        if ((registroSubSubFinanciables.getCampos()
                        .get(campoMontoFinanciar) != null)
            || !("").equals(registroSubSubFinanciables.getCampos()
                            .get(campoMontoFinanciar))) {
            Double valCuota = Double
                            .parseDouble(registroSubSubFinanciables.getCampos()
                                            .get(campoMontoFinanciar)
                                            .toString())
                / Double.parseDouble(registroSubSubFinanciables.getCampos()
                                .get(campoNumCuotas).toString());

            registroSubSubFinanciables.getCampos().put(campoSaldoFinan,
                            registroSubSubFinanciables.getCampos()
                                            .get(campoMontoFinanciar));

            BigDecimal valCta = BigDecimal.valueOf(valCuota);
            registroSubSubFinanciables.getCampos().put(campoValCuota,
                            valCta);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1882"));
        }
    }

    /**
     * Metodo ejecutado al cambiar el control NumeroCuotas
     *
     */
    public void cambiarNumeroCuotas() {
        // <CODIGO_DESARROLLADO>

        if ((registroSubSubFinanciables.getCampos()
                        .get(campoNumCuotas) != null)
            || !("").equals(registroSubSubFinanciables.getCampos()
                            .get(campoNumCuotas))) {
            if (!("0").equals(registroSubSubFinanciables.getCampos()
                            .get(campoNumCuotas).toString())) {
                calcularValorCuota();
            }
        }
        else {
            registroSubSubFinanciables.getCampos().put(campoValCuota, "");
            registroSubSubFinanciables.getCampos().put(campoNumCuotas, "");
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1883"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoOperacion en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubInfoOperacion
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoOperacionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaFormateadoE = null;

        tipoOperacion = SysmanFunciones
                        .nvl(listaSubinfooperacion.getDatasource().get(rowNum)
                                        .getCampos().get(cTipoOperacion), "")
                        .toString();

        listaSubinfooperacion.getDatasource().get(rowNum).getCampos()
                        .put(campoFormato, "");

        validarCondicionFormato(
                        listaSubinfooperacion.getDatasource().get(rowNum));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Estado en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubInfoOperacion
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEstadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        consecutivo = listaSubinfooperacion.getDatasource().get(rowNum)
                        .getCampos().get(cConsecutivo).toString();

        estado = listaSubinfooperacion.getDatasource().get(rowNum)
                        .getCampos()
                        .get(campoEstado).toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EstadoOperacion en la
     * fila seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEstadoOperacionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        estadoOperacion = SysmanFunciones
                        .nvl(listaSubinfooperacion.getDatasource()
                                        .get(rowNum)
                                        .getCampos()
                                        .get("ESTADO_OPE"),
                                        "")
                        .toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Descripcion en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDescripcionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea!
        descripcion = SysmanFunciones.nvl(
                        listaSubinfooperacion.getDatasource()
                                        .get(rowNum)
                                        .getCampos()
                                        .get("DESCRIPCION"),
                        "").toString();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Lectura en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarLecturaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        lectura = SysmanFunciones
                        .nvl(listaSubinfooperacion.getDatasource().get(rowNum)
                                        .getCampos().get("LECTURA"), "")
                        .toString();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Formateado en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubInfoOperacion
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFormateadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(listaSubinfooperacion
                        .getDatasource().get(rowNum % 10).getCampos(),
                        campoFormato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensFormato));
            return;
        }

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            listaSubinfooperacion.getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos().get(campoFormato)
                                            .toString());

            /*-    Date date = (Date) listaSubinfooperacion
                            .getDatasource()
                            .get(rowNum % 10)
                            .getCampos()
                            .get(campoFechaActa);
            
            // Revisar casting a fecha //verificar el getRegistroUnico
            param.put("FECHAET",
                            SysmanFunciones.convertirAFechaCadena(date));*/

            listaSubinfooperacion.getDatasource().get(rowNum % 10).getCampos()
                            .put(campoFechaActa,
                                            listaFormateadoE.getRegistroUnico(
                                                            param).getCampos()
                                                            .get(cFecha));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Concepto en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubFinanciables
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbAno en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubFinanciables
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbAnoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        if (Integer.parseInt(listacmbAno.get(rowNum).getCampos().get("ANO")
                        .toString()) < Integer.parseInt(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensAnioSubFin));

            listacmbPeriodo = null;
        }
        else {
            registroSubSubFinanciables.getCampos().put(campoBloqHastaPeriodo,
                            "");
            cargarListacmbPeriodo();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbPeriodo en la fila
     * seleccionada dentro de la grilla del sub formulario
     * SubFinanciables
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbPeriodoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        if ((Integer.parseInt(listacmbAno.get(rowNum).getCampos().get("ANO")
                        .toString()) == Integer.parseInt(anio))
            && (Integer.parseInt(listacmbPeriodo.get(rowNum).getCampos()
                            .get("MES").toString()) < Integer
                                            .parseInt(periodo))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1633"));
        }
        else if (Integer.parseInt(listacmbAno.get(rowNum).getCampos().get("ANO")
                        .toString()) < Integer.parseInt(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensAnioSubFin));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaAforador</code>.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAforador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        aforador = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        registroSubSubInfoOperacion.getCampos().put(cAforador, aforador);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaAforadorE</code>.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAforadorE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        aforador = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        /* revisar esta variable para que sirve */
        /**
         * @TODO
         */
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedidor del sub formulario SubInfoOperacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMedidor(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSubSubInfoOperacion.getCampos()
                        .put("MEDIDOR", SysmanFunciones
                                        .nvl(registroAux.getCampos().get(
                                                        cConsecutivo), "")
                                        .toString());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMedidor dentro de la grilla del sub formulario
     * SubInfoOperacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMedidorE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cConsecutivo), "")
                        .toString();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormateado del sub formulario SubInfoOperacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormateado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSubSubInfoOperacion.getCampos().put(campoFormato,
                        registroAux.getCampos().get(cCodigo));

        registroSubSubInfoOperacion.getCampos().put(campoFechaActa,
                        registroAux.getCampos().get(cFecha));

        registroSubSubInfoOperacion.getCampos().put("TIPOACTA",
                        registroAux.getCampos().get("TIPO"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFormateado dentro de la grilla del sub formulario
     * SubInfoOperacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormateadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    public boolean validarVacios(Registro registroAux) {
        if ((registroAux.getCampos().get(cCodigo) == null)
            || ("").equals(registroAux.getCampos().get(cCodigo))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1885"));
            return false;
        }

        return true;
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto del sub formulario SubFinanciable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSubSubFinanciables.getCampos().put(campoConcepto,
                        registroAux.getCampos().get(cCodigo));

        registroSubSubFinanciables.getCampos().put(campoMontoFinanciar,
                        registroAux.getCampos().get(campoValor));

        registroSubSubFinanciables.getCampos().put(campoSaldoFinan,
                        registroAux.getCampos().get(campoValor));

        registroSubSubFinanciables.getCampos().put(campoValCuota,
                        registroAux.getCampos().get(campoValor));

        registroSubSubFinanciables.getCampos().put(campoNomConcepto,
                        registroAux.getCampos().get(cNombre));

        String anioPer = "";

        try {
            anioPer = ejbServiciosPublicosDos.actualizarConceptoAntes(compania,
                            Integer.parseInt(ciclo), codigoRuta,
                            Integer.parseInt(anio), periodo,
                            (int) registroAux.getCampos().get(cCodigo));
        }
        catch (NumberFormatException | SystemException e) {
            registroSubSubFinanciables.getCampos().put(campoConcepto, "");
            registroSubSubFinanciables.getCampos().put(campoMontoFinanciar, "");
            registroSubSubFinanciables.getCampos().put(campoSaldoFinan, "");
            registroSubSubFinanciables.getCampos().put(campoValCuota, "");

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!SysmanFunciones.validarVariableVacio(anioPer)) {
            String[] valores = anioPer.split(",");

            registroSubSubFinanciables.getCampos().put("ANO", valores[0]);
            registroSubSubFinanciables.getCampos().put("PERIODO", valores[1]);
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto dentro de la grilla del sub formulario
     * SubFinanciable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = ((BigDecimal) registroAux.getCampos().get(cCodigo))
                        .toString();

        if ("12".equals(auxiliar)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1592"));
            return;
        }

        registroSubSubFinanciables.getCampos().put(campoConcepto, auxiliar);
        registroSubSubFinanciables.getCampos().put(campoMontoFinanciar,
                        registroAux.getCampos().get(campoValor));
        registroSubSubFinanciables.getCampos().put(campoSaldoFinan,
                        registroAux.getCampos().get(campoValor));
        registroSubSubFinanciables.getCampos().put(campoValCuota,
                        registroAux.getCampos().get(campoValor));
        registroSubSubFinanciables.getCampos().put(campoNomConcepto,
                        registroAux.getCampos().get(cNombre));
    }

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

        codigoRuta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoCodRuta), "")
                        .toString();

        /* Valida que el codigo de ruta sea diferente de nulo */
        if (SysmanFunciones.validarVariableVacio(codigoRuta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1886"));

            codigoInterno = "";
            nombre = "";
            dirTecnica = "";
            listaSubinfooperacion = null;
            listaSubfinanciables = null;

            return;
        }

        codigoInterno = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOINTERNO"), "")
                        .toString();

        nombre = SysmanFunciones.nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        dirTecnica = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DIRTECNICA"), "")
                        .toString();

        anio = SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "")
                        .toString();

        periodo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoPeriodo), "")
                        .toString();

        estadoUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoEstado), "")
                        .toString();

        // estado = estadoUsuario;

        bancoPerProcesoUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoBancoPerProc), "")
                        .toString();

        totFacturaPerActualUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TOTFACTURAPERACTUAL"),
                                        "0")
                        .toString();

        deudaUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DEUDA"), "")
                        .toString();

        periodosAtrasoUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODOSATRASO"), "")
                        .toString();

        // CP OBLIGATORIO
        facturaFin = registroAux.getCampos().get("FACTURA").toString();

        // CP OBLIGATORIO
        perNoCobroFin = registroAux.getCampos().get("PERIODOSNOCOBROFIN")
                        .toString();

        conFraude = false;

        /* Mostrar las plantillas de tipo 28 y 30 al iniciar */
        tipoPlantilla = "28,30";

        // Pestaña Novedad
        asignarCondConceptos();

        // Pestaña Operacion
        cargarListaSubinfooperacion();

        cargarListaAforador();
        cargarListaAforadorE();
        cargarListaMedidor();
        cargarListaMedidorE();
        cargarListaConcepto();
        cargarListaConceptoE();
        cargarListacmbAno();
        cargarListacmbPeriodo();
        cargarListaSubfinanciables();
        cargarRegistrosSub();
        abrirSubInfoOperacion();
        consultarFraudes();
        calcularTotales();
        cargarListaFormateadoE();

        if (!listaSubinfooperacion.getDatasource().isEmpty()) {
            asignarNombreFormato();
        }

        try {
            parEstadoOperacion = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil
                                            .consultarParametro(
                                                            compania,
                                                            "ESTADO OPERACION Y DESCRIPCION EN OPERACIONES",
                                                            modulo,
                                                            new Date(),
                                                            true),
                                            "NO"));
        }
        catch (SystemException e) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite mostrar el nombre del formato
     * correspondiente a cada elemento de la lista de la grilla del
     * subformulario SubInfoOperacion
     */
    private void asignarNombreFormato() {
        for (int i = 0; i < listaSubinfooperacion.getDatasource().size(); i++) {
            validarCondicionFormato(
                            listaSubinfooperacion.getDatasource().get(i));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            listaSubinfooperacion.getDatasource().get(i)
                                            .getCampos().get(campoFormato));

            Date fecha = (Timestamp) listaSubinfooperacion.getDatasource()
                            .get(i)
                            .getCampos().get(campoFechaActa);

            Registro auxReg = null;

            GregorianCalendar calendar2 = new GregorianCalendar();
            calendar2.setTime(fecha);

            try {
                param.put(GeneralParameterEnum.FECHA.getName(),
                                calendar2.getTime());

                auxReg = RegistroConverter.toRegistro(requestManager.get(
                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrminfooperacionControladorUrlEnum.URL0009
                                                                                .getValue())
                                                .getUrl(),
                                param));
            }
            catch (SystemException e1) {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }

            listaSubinfooperacion.getDatasource().get(i).getCampos()
                            .put(campoNomActa, auxReg == null ? ""
                                : auxReg.getCampos().get("NOMBRE")
                                                .toString());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Imprimir de la pestaña
     * <code>Operacion</code>
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirImprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), campoFormato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1887"));
            return;
        }

        validarCondicionFormato(reg);

        if (registrarActaOperacion(indice)) {
            String documentoGen = reg.getCampos().get(campoFormato).toString();

            String nombreDoc = (String) reg.getCampos().get(campoNomActa);

            String fechaPlantilla = SysmanFunciones.formatearFecha(
                            (Date) reg.getCampos().get(campoFechaActa));

            String[] campos = { "codigoPlantilla", "nombreDocDescarga",
                                "fechaPlantilla" };

            String[] valores = { documentoGen, nombreDoc, fechaPlantilla };

            HashMap<String, String> reemplazar = new HashMap<>();
            reemplazar.put("s$compania$s", "'" + compania + "'");
            reemplazar.put("s$codigoInicial$s", "'" + codigoRuta + "'");
            reemplazar.put("s$codigoFinal$s", "'" + codigoRuta + "'");
            reemplazar.put("s$ciclo$s", ciclo);

            // creadas para prueba
            reemplazar.put("s$perAtrasoInicial$s", "0");
            reemplazar.put("s$perAtrasoFinal$s", "99999");
            reemplazar.put("s$condEstado$s", "'" + estadoUsuario + "'");
            reemplazar.put("s$actaSuspencion$s", "'SI'");
            reemplazar.put("s$notaInvalidar$s", "' '");
            reemplazar.put("s$deudaInicial$s", "0");
            reemplazar.put("s$deudaFinal$s", "99999999999999999999");
            reemplazar.put("s$periodosAtraso$s", "0");
            reemplazar.put("s$tituloActa$s", "'pae'");

            reemplazar.put("s$codigoRuta$s", "'" + codigoRuta + "'");
            reemplazar.put("s$anio$s", anio);
            reemplazar.put("s$periodo$s", "'" + periodo + "'");
            reemplazar.put("s$claseOperacion$s", "'" + claseOperacion + "'");

            /* Reemplazos de la consulta asociada a la plantilla */
            SessionUtil.setSessionVar("variablesConsultaWord", reemplazar);

            SessionUtil.cargarModalDatosFlash(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos, valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que verifica si el usuario tiene acta asociada, en caso
     * contrario crea un registro de un acta.
     * 
     * @param indice
     * Numero del registro desde el cual se presiono el boton
     * <code>Imprimir</code>
     * @return <code>true</code>: El tipo de operacion es de
     * suspension o corte. El usuario tiene un acta o se crea un
     * registro de acta de operacion.
     */
    private boolean registrarActaOperacion(int indice) {
        boolean respuesta = false;

        String tipoOp = (String) listaSubinfooperacion.getDatasource()
                        .get(indice).getCampos()
                        .get(cTipoOperacion);

        Date fechaEj = new Date(((Date) listaSubinfooperacion
                        .getDatasource().get(indice)
                        .getCampos()
                        .get(campoFechaEjec)).getTime());

        Date horaEje = new Date(((Date) listaSubinfooperacion
                        .getDatasource().get(indice)
                        .getCampos()
                        .get(campoHoraEjec)).getTime());

        String aforador = (String) listaSubinfooperacion.getDatasource()
                        .get(indice).getCampos()
                        .get(cAforador);

        String descr = (String) listaSubinfooperacion.getDatasource()
                        .get(indice).getCampos()
                        .get("DESCRIPCION");

        // Tipo de operacion de suspension o corte
        if (("001").equals(tipoOp) || ("002").equals(tipoOp)) {
            /*-  ejbServiciosPublicosDos.registrarActa(compania, "0", ciclo, codigoRuta, codigoRuta, peratrasoini, peratrasofin, deudaini, deudafin, descr, conabonos, abonos, chapetas, pqr, fechaemision, superint, descr)*/
            return true;
        }

        try {
            /*-Verifica si el acta existe: false, en caso contrario la registra: true*/
            boolean key = ejbServiciosPublicosDos.registrarActaOperacion(
                            compania, tipoOp,
                            Integer.parseInt(ciclo), codigoRuta, codigoInterno,
                            Integer.parseInt(anio), periodo, fechaEj,
                            horaEje,
                            aforador, descr, periodosAtrasoUsuario, usuario,
                            claseOperacion);

            if (!key) {
                String acta = "01".equals(claseOperacion) ? "Reconexión"
                    : "Reinstalación";

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1888")
                                .replace("#ACTA#", acta));
            }

            respuesta = true;
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del subformulario Subinfooperacion
     */
    public void agregarRegistroSubSubinfooperacion() {
        if (validarCamposObligatoriosRegOperacion()) {
            return;
        }

        if (SysmanFunciones.validarCampoVacio(
                        registroSubSubInfoOperacion.getCampos(),
                        campoFormato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensFormato));
            return;
        }

        actualizarEstado();

        registroSubSubInfoOperacion.getCampos().remove("NOM_TIPOOPERACION");
        registroSubSubInfoOperacion.getCampos().remove("NOM_ESTADOETAPA");
        registroSubSubInfoOperacion.getCampos().remove("NOM_ESTADO_OPE");
        registroSubSubInfoOperacion.getCampos().put("COMPANIA", compania);
        registroSubSubInfoOperacion.getCampos().put("CICLO", ciclo);
        registroSubSubInfoOperacion.getCampos().put(campoCodRuta, codigoRuta);

        registroSubSubInfoOperacion.getCampos().put("ANO", anio);
        registroSubSubInfoOperacion.getCampos().put(campoPeriodo, periodo);

        registroSubSubInfoOperacion.getCampos().put("CREATED_BY", usuario);
        registroSubSubInfoOperacion.getCampos().put(cDateCreated, new Date());

        try {
            String criterio = SysmanFunciones.concatenar(
                            "     COMPANIA   = ''", compania, "'' ",
                            " AND CICLO      =  ", ciclo,
                            " AND CODIGORUTA = ''", codigoRuta, "'' ");

            long miConsecutivo = ejbSysmanUtil
                            .generarConsecutivoConValorInicial(
                                            "SP_INFO_TIPO_OPERACION",
                                            criterio,
                                            cConsecutivo, "1");

            registroSubSubInfoOperacion.getCampos().put(cConsecutivo,
                            miConsecutivo);

            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            "SP_INFO_TIPO_OPERACION",
                            registroSubSubInfoOperacion.getCampos());
            listaSubinfooperacion.load();

            if (conteo > 0) {
                auditarOperaciones(1, Long.toString(miConsecutivo));

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (SystemException | SQLException | NamingException
                        | IllegalAccessException | InstantiationException
                        | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSubSubInfoOperacion = new Registro(
                            new HashMap<String, Object>());

            cargarRegistrosSub();
            abrirSubInfoOperacion();
            asignarNombreFormato();
            calcularTotales();
        }
    }

    /**
     * Metodo de edicion del subformulario Subinfooperacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubinfooperacion(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        registroSubSubInfoOperacion = new Registro(reg.getCampos());
        registroSubSubInfoOperacion.setLlave(new HashMap<>(reg.getLlave()));

        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), campoFormato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensFormato));

            cargarListaSubinfooperacion();
            asignarNombreFormato();

            return;
        }

        if (validarCamposObligatoriosRegOperacion()) {
            return;
        }

        /* Al cambiar el valor del combo estado */
        if (!SysmanFunciones.validarVariableVacio(estado)) {
            actualizarEstado();
        }

        /* Detiene el proceso principal si el dialogo se habilita */
        if (mostrarMensVal) {
            regSubInfoOperacion = new Registro(reg.getCampos());
            regSubInfoOperacion.setLlave(reg.getLlave());
            return;
        }

        actualizarRegistroOperacion(reg);
    }

    private void actualizarRegistroOperacion(Registro reg) {
        reg.getCampos().remove("NOM_TIPOOPERACION");
        reg.getCampos().remove("NOM_ESTADOETAPA");
        reg.getCampos().remove("NOM_AFORADOR");
        reg.getCampos().remove("NOM_ESTADO_OPE");
        reg.getCampos().remove("RNUM");
        reg.getCampos().remove(campoNomActa);
        reg.getCampos().put("MODIFIED_BY", usuario);
        reg.getCampos().put("DATE_MODIFIED", new Date());

        try {
            int conteo;
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            subInfoOperacion, reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0) {
                compararRegistros(regSubInfoOpeAntEd, reg);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            listaSubinfooperacion.load();
            cargarRegistrosSub();
            abrirSubInfoOperacion();
            asignarNombreFormato();
        }
    }

    /**
     * Metodo de eliminacion del subformulario SubInfoOperacion
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubinfooperacion(Registro reg) {
        try {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            subInfoOperacion, reg.getLlave());
            if (conteo > 0) {
                auditarOperaciones(2, reg.getCampos().get(cConsecutivo)
                                .toString());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            listaSubinfooperacion.load();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo de insercion del formulario Subfinanciables
     */
    public void agregarRegistroSubSubfinanciables() {
        cargarValoresSubFinanciables();

        adicionarFinanciableSigPeriodo();

        /* Si DialogoPregunta debe ser visible */
        if (verDPreg) {
            return;
        }

        try {
            Date fechaCreacion = (Date) registroSubSubFinanciables.getCampos()
                            .get(cDateCreated);

            ejbServiciosPublicosDos.verificarAdicionFinanciable(compania,
                            Integer.parseInt(ciclo), codigoRuta, fechaCreacion);

            if (!bancoPerProcesoUsuario.isEmpty()) {
                if (anio.equals(anioIniSF)) {
                    String anioPer = ejbServiciosPublicosDos
                                    .agregarFinanciablePerSig(
                                                    compania,
                                                    Integer.parseInt(anioIniSF),
                                                    periodoIniSF);

                    String[] valores = anioPer.split(",");

                    registroSubSubFinanciables.getCampos().put("ANO",
                                    valores[0]);

                    registroSubSubFinanciables.getCampos().put(campoPeriodo,
                                    valores[1]);
                }

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1532")
                                .replace("#USUARIO#", codigoRuta));
            }

            if (!"0".equals(perNoCobroFin)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3327")
                                .replace("#PERIODOSNOCOBROFIN#",
                                                perNoCobroFin));
            }

            /* Si el check esta marcado, no calcule fact */
            if (bloqueado) {
                insertarSubFinanciable();
                return;
            }

            verMensajeFinan = !"0".equals(facturaFin);

            if (!verMensajeFinan) {
                insertarSubFinanciable();

                ejbServiciosPublicosSiete.calcularFacturacion(compania,
                                Integer.parseInt(ciclo), codigoRuta, codigoRuta,
                                false, false, usuario);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Verifica si el <code>FIMM</code> tiene como valor:
     * <code>P</code>, para mostrar un dialogo al usuario.
     */
    private void adicionarFinanciableSigPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

        Registro regAux = null;

        try {
            regAux = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL0008
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (regAux != null && "P".equals(SysmanFunciones
                        .nvl(regAux.getCampos().get("FIMM"), "").toString())) {
            mensajeDPreg = idioma.getString("TB_TB3323");
            verDPreg = true;
        }
    }

    /**
     * Metodo que ejecuta la insercion del registro seleccionado en la
     * grilla del subformulario Subfinanciables.
     */
    private void insertarSubFinanciable() {
        registroSubSubFinanciables.getCampos().put("COMPANIA",
                        compania);

        registroSubSubFinanciables.getCampos().put("CICLO", ciclo);

        registroSubSubFinanciables.getCampos().put(campoCodRuta,
                        codigoRuta);

        registroSubSubFinanciables.getCampos().put("ANO", anio);

        registroSubSubFinanciables.getCampos().put(campoPeriodo,
                        periodo);

        registroSubSubFinanciables.getCampos().put(campoBloqHastaAnio,
                        bloqHasAnio);

        registroSubSubFinanciables.getCampos().put(campoBloqHastaPeriodo,
                        bloqHasPeriodo);

        registroSubSubFinanciables.getCampos().put(cCreatedBy, usuario);
        registroSubSubFinanciables.getCampos().put(cDateCreated, new Date());

        registroSubSubFinanciables.getCampos().remove(campoNomConcepto);

        actualizarDespuesCheckBloq();

        try {
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminfooperacionControladorUrlEnum.URL0002
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            registroSubSubFinanciables.getCampos());

            listaSubfinanciables.load();
            ejecutarPRAuditoriaGeneral();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubFinanciables = new Registro(
                            new HashMap<String, Object>());

            cargarListaSubfinanciables();
            cargarRegistrosSub();
            calcularTotales();
        }

    }

    /**
     * Metodo de edicion del subformulario Subfinanciables. <br>
     * Access no cuenta con opcion de edicion en el subformulario de
     * la pestaña novedad.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubfinanciables(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de eliminacion del subformulario Subfinanciables
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubfinanciables(Registro reg) {
        int concepto = (int) reg.getCampos().get(campoConcepto);

        saldoFinanciable = reg.getCampos().get(campoSaldoFinan).toString();

        montoFinanciar = reg.getCampos().get("MONTOFINANCIAR").toString();

        BigDecimal nCuotas = new BigDecimal(
                        reg.getCampos().get(campoNumCuotas).toString());

        int miCuota = (int) reg.getCampos().get(campoNroCuota);

        valorCuota = reg.getCampos().get(campoValCuota).toString();

        try {
            String codMensaje = ejbServiciosPublicosDos.eliminarFinanciable(
                            compania,
                            Integer.parseInt(ciclo), Integer.parseInt(anio),
                            periodo, codigoRuta, concepto, usuario,
                            new BigDecimal(montoFinanciar),
                            new BigDecimal(sumMontoFin),
                            new BigDecimal(saldoFinanciable),
                            nCuotas, miCuota,
                            new BigDecimal(valorCuota), bancoPerProcesoUsuario,
                            codigoInterno);

            if ("-1".equals(codMensaje)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1531"));
            }
            else {
                if (12 == concepto) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1714"));
                }

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrminfooperacionControladorUrlEnum.URL0003
                                                                .getValue());

                int conteo = requestManager.delete(urlDelete.getUrl(),
                                reg.getLlave());

                if (conteo > 0) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_ELIMINADO"));

                    eliminarDespuesSubFinanciable();
                }

                listaSubfinanciables.load();
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que se ejecuta despues de eliminar un registro de la
     * pestaña <code>Novedad</code>. Realiza el calculo de facturacion
     * cuando el registro esta desbloqueado.
     * 
     * @throws SystemException
     */
    private void eliminarDespuesSubFinanciable() throws SystemException {
        if (!bloqueado) {
            ejbServiciosPublicosSiete.calcularFacturacion(compania,
                            Integer.parseInt(ciclo), codigoRuta, codigoRuta,
                            false, false, usuario);
        }
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo para cargar los valores del registro seleccionado en la
     * edicion del sub formulario subinfooperacion, pestana Operacion
     *
     * @param reg:
     * equivale al registro que contiene los campos almacenados del
     * registro seleccionado.
     */
    public void activarEdicionSubinfooperacion(Registro reg) {
        indiceSubinfooperacion = reg.getIndice();
        regSubInfoOpeAntEd = new Registro(new HashMap<>(reg.getCampos()));
        validarCondicionFormato(reg);
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subinfooperacion
     */
    public void cancelarEdicionSubinfooperacion() {
        // <CODIGO_DESARROLLADO>
        cargarListaSubinfooperacion();
        validarCondicionFormato(regSubInfoOpeAntEd);
        asignarNombreFormato();
        // <CODIGO_DESARROLLADO>
    }

    /**
     * Metodo para cargar los valores del registro seleccionado en la
     * edicion del sub formulario subfinanciables, pestana Novedad
     *
     * @param reg:
     * equivale al registro que contiene los campos almacenados del
     * registro seleccionado.
     */
    public void activarEdicionSubfinanciables(Registro reg) {
        indiceSubfinanciables = reg.getIndice();

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subfinanciables
     **/
    public void cancelarEdicionSubfinanciables() {
        cargarListaSubfinanciables();
    }

    /**
     * Metodo que permite hacer visibles o no algunos campos del
     * subformulario Subinfooperacion dependiendo el valor de los
     * parametros de la base de datos evaluados.
     */
    private void abrirSubInfoOperacion() {
        etiquetaFecha = "Fecha";

        try {
            /*-Controla la visibilidad del campo fecha y hora de ejecucion en la pestaña novedad*/
            verFechaHoraEjecucion = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil
                                            .consultarParametro(
                                                            compania,
                                                            "MANEJA FECHA DE EJECUCION EN OPERACIONES",
                                                            modulo,
                                                            new Date(),
                                                            true),
                                            "NO"));

            /*-Controla la edicion del subformulario de la pestaña Operacion*/
            editarRegSubInfoOp = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE MODIFICACIONES EN OPERACIONES",
                                            modulo, new Date(), true), "NO"));

            /*-Controla la visibilidad del campo usuario en la pestaña Operacion*/
            verUsuario = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "GESTION DE USUARIOS EN REGISTRO DE OPERACIONES",
                                            modulo, new Date(), true), "NO"));

            /*-Controla la visiblidad del campo estado de operacion en la pestaña operacion*/
            verEstadoOpe = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "OPERACIONES EJECUTADAS Y NO EJECUTADAS",
                                            modulo, new Date(), true), "NO"));

            boolean parManOrdTraWeb = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA ORDENES DE TRABAJO WEB",
                                            modulo, new Date(), true), "NO"));

            if (parManOrdTraWeb) {
                registroSubSubInfoOperacion.getCampos().put(campoFechaEjec,
                                null);

                registroSubSubInfoOperacion.getCampos().put(campoHoraEjec,
                                null);

                etiquetaFecha = "Fecha Creación";
                verHoraCreacion = true;
                bloquearFechaHoraE = false;
            }
            else {
                bloquearFechaHoraE = true;
                verHoraCreacion = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que determina los conceptos por los que se va a
     * condicionar la consulta de la lista de conceptos en el
     * subformulario SubFinanciables
     */
    private void asignarCondConceptos() {
        codigosConcepto = new StringBuilder();

        try {
            /* Determina si se debe incluir el concepto 32 */
            boolean parPerFinConc32 = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE FINANCIAR CONCEPTO 32",
                                            modulo, new Date(), true), "NO"));

            /*-Determina si se debe incluir los conceptos del parametro: CONCEPTOS FINANCIABLES REGISTRO DE OPERACIONES*/
            boolean parGestConFin = "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "GESTIONA CONCEPTOS FINANCIABLES EN OPERACIONES",
                                            modulo, new Date(), true), "NO"));

            codigosConcepto.append(parPerFinConc32 ? "32,247,47" : "247,47");

            if (parGestConFin) {
                String conceptos = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "CONCEPTOS FINANCIABLES REGISTRO DE OPERACIONES",
                                                modulo, new Date(), true), "");

                if (!conceptos.isEmpty()) {
                    codigosConcepto.append("," + conceptos);
                }
            }
        }
        catch (SystemException e) {
            Logger.getLogger(FrminfooperacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite verificar si el suscriptor tiene fraudes
     * registrados en el sub formulario SubInfoOperacion.
     */
    private void consultarFraudes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.ESTADO.getName(), "A");

        Registro reg = null;

        try {
            reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null
            && !("0").equals(reg.getCampos().get("CANT").toString())) {
            conFraude = true;
        }
    }

    /**
     * @TODO d
     * @return
     */
    private boolean validarCamposObligatoriosRegOperacion() {
        String valorPar = null;

        try {
            valorPar = recuperarValorPar(
                            "CAMPOS OBLIGATORIOS REG. OPERACIONES");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        valorPar = validarParametro("CAMPOS OBLIGATORIOS REG. OPERACIONES",
                        valorPar) ? SysmanFunciones.nvlStr(valorPar, "NO")
                            : "NO";

        if ("NO".equals(valorPar)) {
            /* Aforador obligatorio */
            if (SysmanFunciones.validarVariableVacio(aforador)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3300")
                                .replace(cCampo, "aforador"));
                return true;
            }
        }
        else {
            if ("E".equals(estadoOperacion)
                || "001002".contains(tipoOperacion)
                || ("E".equals(estadoOperacion)
                    && "003004".contains(tipoOperacion))) {
                if (SysmanFunciones.validarVariableVacio(estado)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3300")
                                    .replace(cCampo, "estado"));
                    return true;
                }
                if (SysmanFunciones.validarVariableVacio(aforador)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3300")
                                    .replace(cCampo, "aforador"));
                    return true;
                }
                if (SysmanFunciones.validarVariableVacio(descripcion)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3300")
                                    .replace(cCampo, "descripcion"));
                    return true;
                }
                if (SysmanFunciones.validarVariableVacio(lectura)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3300")
                                    .replace(cCampo, "lectura"));
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3301")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    /**
     * Desencadena el proceso y las correspondientes validaciones para
     * actualizar el estado de un usuario.
     */
    private void actualizarEstado() {
        /* Verifica si se cambio el estado del registro */
        if (!estadoUsuario.equals(estado)) {
            validarEstadoNoActivo();

            /* Detiene el proceso si el dialogo se habilita */
            if (mostrarMensVal) {
                return;
            }

            ejecutarPRActualizarEstado();
        }
    }

    /** Hace el llamado al procedimiento que actualiza el estado */
    private void ejecutarPRActualizarEstado() {
        StringBuilder codAuditoria = new StringBuilder();
        codAuditoria.append(consecutivo);
        codAuditoria.append("^");
        codAuditoria.append(compania);
        codAuditoria.append("^");
        codAuditoria.append(ciclo);
        codAuditoria.append("^");
        codAuditoria.append(codigoRuta);

        try {
            ejbServiciosPublicosTres.actualizarEstado(compania,
                            Integer.parseInt(ciclo), codigoRuta,
                            estado, usuario,
                            codAuditoria.toString());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Validaciones para cuando el estado no esta activo. */
    private void validarEstadoNoActivo() {
        if (!"A".equals(estado) && bancoPerProcesoUsuario.isEmpty()) {
            /* Valida facturado */
            if (!"0".equals(totFacturaPerActualUsuario)) {
                mensValTotal = idioma.getString("TB_TB1439")
                                .replace(cUsuario, codigoRuta)
                                .replace("#VALFACTURA#",
                                                totFacturaPerActualUsuario)
                                .replace("#NPERIODOS#", periodosAtrasoUsuario);

                mostrarMensVal = true;
            }

            /* Valida deuda */
            if (!"0".equals(deudaUsuario)) {
                mensValTotal = idioma.getString("TB_TB1440")
                                .replace(cUsuario, codigoRuta)
                                .replace("#DEUDA#", deudaUsuario)
                                .replace("#NPERIODOS#", periodosAtrasoUsuario);

                mostrarMensVal = true;
            }

            /* Valida financiables */
            validarFinanciables();
        }
    }

    /**
     * Verifica los financiables del usuario. Cuando el usuario tiene
     * financiables muestra una ventana emergente.
     */
    private void validarFinanciables() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

        Registro rAux = null;

        try {
            rAux = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL0006
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rAux != null && !SysmanFunciones.validarCampoVacio(rAux.getCampos(),
                        cTotalFinanciable)) {
            DecimalFormat formatoPesos = new DecimalFormat("$ #,##0.00");

            double totalFinanciable = Double.parseDouble(rAux.getCampos()
                            .get(cTotalFinanciable).toString());

            mensValTotal = mensFinanciable = idioma.getString("TB_TB1441")
                            .replace(cUsuario, codigoRuta)
                            .replace("#SALDOFINANCIABLE#", formatoPesos
                                            .format(totalFinanciable));

            mostrarMensVal = true;
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * ValTotFactura en la vista.
     */
    public void aceptarValTotFactura() {
        ejecutarPRActualizarEstado();

        regSubInfoOperacion.getCampos().put(cTipoOperacion,
                        regSubInfoOpeAntEd.getCampos().get(cTipoOperacion));

        regSubInfoOperacion.getCampos().put("ESTADO", estado);

        regSubInfoOperacion.getCampos().put(cEstadoOpe,
                        regSubInfoOpeAntEd.getCampos().get(cEstadoOpe));

        actualizarRegistroOperacion(regSubInfoOperacion);

        mostrarMensVal = false;
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * ValTotFactura en la vista.
     */
    public void cancelarValTotFactura() {
        // <CODIGO_DESARROLLADO>
        mostrarMensVal = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que asigna los valores iniciales al registro de cada uno
     * de los subformularios SubInfoOperacion y SubFinanciables
     */
    private void cargarRegistrosSub() {
        registroSubSubInfoOperacion.getCampos().put("USUARIO", usuario);
        registroSubSubInfoOperacion.getCampos().put(cFecha, new Date());
        registroSubSubInfoOperacion.getCampos().put(campoFechaEjec, new Date());
        registroSubSubInfoOperacion.getCampos().put("HORACREACION", new Date());
        registroSubSubInfoOperacion.getCampos().put(campoHoraEjec, new Date());
        registroSubSubInfoOperacion.getCampos().put("ANO", anio);
        registroSubSubInfoOperacion.getCampos().put(campoLectura, "0");

        registroSubSubFinanciables.getCampos().put(cDateCreated, new Date());
        registroSubSubFinanciables.getCampos().put("ANO", anio);
        registroSubSubFinanciables.getCampos().put("ANOINICIAL", anio);
        registroSubSubFinanciables.getCampos().put(campoPeriodo, periodo);
        registroSubSubFinanciables.getCampos().put("PERIODOINICIAL", periodo);
        registroSubSubFinanciables.getCampos().put(campoNumCuotas, "1");
        registroSubSubFinanciables.getCampos().put(campoMontoFinanciar, "0");
        registroSubSubFinanciables.getCampos().put(campoSaldoFinan, "0");
        registroSubSubFinanciables.getCampos().put(campoValCuota, "0");
    }

    /**
     * Metodo que asigna los valores del registro del subformulario
     * SubFinanciables
     */
    private void cargarValoresSubFinanciables() {
        anioIniSF = (String) registroSubSubFinanciables.getCampos()
                        .get("ANOINICIAL");

        periodoIniSF = (String) registroSubSubFinanciables.getCampos()
                        .get("PERIODOINICIAL");

        montoFinanciar = (String) registroSubSubFinanciables.getCampos()
                        .get(campoMontoFinanciar);

        saldoFinanciable = SysmanFunciones.nvl(registroSubSubFinanciables
                        .getCampos().get(campoSaldoFinan), "").toString();

        numeroCuotas = (String) registroSubSubFinanciables.getCampos()
                        .get(campoNumCuotas);

        nroCuota = (String) registroSubSubFinanciables.getCampos()
                        .get(campoNroCuota);

        valorCuota = registroSubSubFinanciables.getCampos()
                        .get(campoValCuota) instanceof BigDecimal
                            ? ((BigDecimal) registroSubSubFinanciables
                                            .getCampos()
                                            .get(campoValCuota)).toString()
                            : String.valueOf(registroSubSubFinanciables
                                            .getCampos().get(campoValCuota));

        bloqueado = (Boolean) registroSubSubFinanciables.getCampos()
                        .get(campoBloqueado);
    }

    /**
     * Metodo para calcular los valores totales del monto financiar,
     * saldo financiable y valor cuota de los registros de la grilla
     * del subformulario SubFinanciables.
     */
    private void calcularTotales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

        Registro reg = null;

        try {
            reg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminfooperacionControladorUrlEnum.URL0007
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null) {
            sumMontoFin = reg.getCampos().get("SUMMONTOFINANCIAR").toString();
            sumSaldoFin = reg.getCampos().get("SUMSALDOFINAN").toString();
            sumValorCuota = reg.getCampos().get("SUMVALORCUOTA").toString();
        }
    }

    /**
     * Metodo que adiciona los campos a ingresar cuando el indicador
     * de bloqueado esta marcado. Aplica para la pestaña
     * <code>Novedad</code>.
     */
    private void actualizarDespuesCheckBloq() {
        if (bloqueado) {
            registroSubSubFinanciables.getCampos().put("USUARIOBLOQUEO",
                            usuario);

            registroSubSubFinanciables.getCampos().put("HORABLOQUEO",
                            recuperarHoraActualEstandar());

            registroSubSubFinanciables.getCampos().put("FECHABLOQUEADO",
                            new Date());
        }
    }

    /**
     * @return La hora actual estandar.
     */
    private Date recuperarHoraActualEstandar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(1899, 12, 30);

        return calendar.getTime();
    }

    /** Metodo que ejecuta el proceso de auditoria general */
    private void ejecutarPRAuditoriaGeneral() {
        String[] cadenas = { "Monto: ", montoFinanciar, "; Saldo: ",
                             saldoFinanciable, "; Cuotas: ", numeroCuotas,
                             "; Cuota: ", nroCuota, "; Valor Cuota: ",
                             valorCuota };

        try {
            ejbServiciosPublicosTres.auditoriaGeneral(compania, usuario,
                            "FINANCIABLES", "Creación", Integer.parseInt(anio),
                            periodo, codigoInterno,
                            SysmanFunciones.concatenar(cadenas));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite comparar los registros de los campos antes
     * de editar y los campos modificados para ejecutar el
     * procedimiento AUDITARREGCOMPARAR
     *
     * @param regAntes
     * registro de la grilla del subformulario SubInfoOperacion antes
     * de ser modificado
     * @param regDespues
     * registro de la grilla del subformulario SubInfoOperacion con
     * los campos modificados
     */
    private void compararRegistros(Registro regAntes, Registro regDespues) {
        StringBuilder resultado = new StringBuilder();
        int contador = 0;

        Iterator<Entry<String, Object>> it = regDespues.getCampos().entrySet()
                        .iterator();

        while (it.hasNext()) {
            Entry<String, Object> e = it.next();
            StringBuilder campoAnt = new StringBuilder();
            StringBuilder campoNue = new StringBuilder();
            String campoA;
            String campoN;

            extraerCampos(campoAnt, campoNue, regAntes, regDespues, e);

            campoA = campoAnt.toString();
            campoN = campoNue.toString();

            if (!campoN.equals(campoA)) {
                campoN = campoN == "" ? "nulo" : campoN;
                resultado.append(
                                e.getKey() + "," + campoA + "," + campoN
                                    + ";");
                contador++;
            }
        }

        auditarRegComparar(regDespues.getCampos().get(cConsecutivo)
                        .toString(), resultado, contador);
    }

    public void extraerCampos(StringBuilder campoAnt, StringBuilder campoNue,
        Registro regAntes, Registro regDespues, Entry<String, Object> e) {

        try {
            campoAnt.append((regAntes.getCampos().get(e.getKey()) != null)
                && (regAntes.getCampos().get(e.getKey()) instanceof Date)
                    ? SysmanFunciones.convertirAFechaCadena(
                                    (Date) regAntes.getCampos().get(e.getKey()),
                                    "dd/MM/yyyy HH:mm:ss")
                    : regAntes.getCampos().get(e.getKey()) == null
                        ? ""
                        : regAntes.getCampos().get(e.getKey()).toString());

            campoNue.append((e.getValue() != null)
                && (e.getValue() instanceof Date)
                    ? SysmanFunciones.convertirAFechaCadena((Date) e.getValue(),
                                    "dd/MM/yyyy HH:mm:ss")
                    : regDespues.getCampos().get(e.getKey()) == null
                        ? ""
                        : regDespues.getCampos().get(e.getKey()).toString());

        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    /**
     * Metodo que ejecuta el procedimiento PR_AUDITARREGCOMPARAR
     *
     * @param consecutivo
     * Consecutivo de la operacion
     * @param camposMod
     * Nombre y valores de los campos modificados en la edicion
     * @param contador
     * Numero de veces que se debe recorrer la cadena camposMod
     */
    private void auditarRegComparar(String consecutivo, StringBuilder camposMod,
        int contador) {
        if (contador != 0) {
            StringBuilder strCampo = new StringBuilder();
            strCampo.append(consecutivo);
            strCampo.append("^");
            strCampo.append(compania);
            strCampo.append("^");
            strCampo.append(ciclo);
            strCampo.append("^");
            strCampo.append(codigoRuta);

            try {
                ejbServiciosPublicosTres.auditarRegistroComparar(compania,
                                "OPERACIONES", strCampo.toString(),
                                camposMod.toString(), usuario,
                                Integer.parseInt(ciclo), codigoRuta,
                                Integer.parseInt(anio), periodo, contador);
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Audita los procesos de creacion y eliminacion del
     * subformulario: <code>Registro de Operaciones</code>
     * 
     * @param tipoOperacion
     * 1: Crear<br>
     * 2: Eliminar.
     * @param consecutivo
     * Codigo del consecutivo en la operacion.
     * 
     */
    private void auditarOperaciones(int tipoOperacion, String consecutivo) {
        /*- Concatenacion del consecutivo, compania, ciclo y codigo de ruta.*/
        String[] cadenas = { consecutivo, "^", compania, "^",
                             ciclo, "^", codigoRuta };

        try {
            ejbServiciosPublicosTres.auditarModif(compania, "OPERACIONES",
                            tipoOperacion, SysmanFunciones.concatenar(cadenas),
                            usuario);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo:
     * <code>DialogoPregunta</code>
     */
    public void aceptarDialogoPregunta() {
        // <CODIGO_DESARROLLADO>
        verDPreg = false;

        try {
            String anioPer = ejbServiciosPublicosDos.agregarFinanciablePerSig(
                            compania, Integer.parseInt(anioIniSF),
                            periodoIniSF);

            String[] valores = anioPer.split(",");

            registroSubSubFinanciables.getCampos().put("ANO", valores[0]);

            registroSubSubFinanciables.getCampos().put(campoPeriodo,
                            valores[1]);

            Date fechaCreacion = (Date) registroSubSubFinanciables.getCampos()
                            .get(cDateCreated);

            ejbServiciosPublicosDos.verificarAdicionFinanciable(compania,
                            Integer.parseInt(ciclo), codigoRuta, fechaCreacion);

            if (!bancoPerProcesoUsuario.isEmpty()) {
                if (valores[0].equals(anioIniSF)) {
                    anioPer = ejbServiciosPublicosDos.agregarFinanciablePerSig(
                                    compania, Integer.parseInt(anioIniSF),
                                    periodoIniSF);

                    valores = anioPer.split(",");

                    registroSubSubFinanciables.getCampos().put("ANO",
                                    valores[0]);

                    registroSubSubFinanciables.getCampos().put(campoPeriodo,
                                    valores[1]);
                }

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1532")
                                .replace("#USUARIO#", codigoRuta));
            }

            if (!"0".equals(perNoCobroFin)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3327")
                                .replace("#PERIODOSNOCOBROFIN#",
                                                perNoCobroFin));
            }

            /* Si el check esta marcado, no calcule fact */
            if (bloqueado) {
                insertarSubFinanciable();
                return;
            }

            verMensajeFinan = !"0".equals(facturaFin);

            if (!verMensajeFinan) {
                insertarSubFinanciable();

                ejbServiciosPublicosSiete.calcularFacturacion(compania,
                                Integer.parseInt(ciclo), codigoRuta, codigoRuta,
                                false, false, usuario);
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo:
     * <code>DialogoPregunta</code>
     */
    public void cancelarDialogoPregunta() {
        // <CODIGO_DESARROLLADO>
        verDPreg = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * mensajeFinan en la vista.
     */
    public void aceptarmensajeFinan() {
        // <CODIGO_DESARROLLADO>
        verMensajeFinan = false;

        try {
            insertarSubFinanciable();

            ejbServiciosPublicosSiete.calcularFacturacion(compania,
                            Integer.parseInt(ciclo), codigoRuta, codigoRuta,
                            false, false, usuario);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * mensajeFinan en la vista.
     */
    public void cancelarmensajeFinan() {
        // <CODIGO_DESARROLLADO>
        verMensajeFinan = false;
        registroSubSubFinanciables.getCampos().put(campoBloqueado, "-1");
        registroSubSubFinanciables.getCampos().put("USUARIOBLOQUEO", usuario);
        registroSubSubFinanciables.getCampos().put("HORABLOQUEO", new Date());
        registroSubSubFinanciables.getCampos().put("FECHABLOQUEADO",
                        new Date());
        bloqueado = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * RecalcularFact en la vista
     */
    public void aceptarRecalcularFact() {
        // <CODIGO_DESARROLLADO>
        verMenRecFact = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * RecalcularFact en la vista
     */
    public void cancelarRecalcularFact() {
        // <CODIGO_DESARROLLADO>
        verMenRecFact = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        conFraude = false;
        cargarListaCiclo();
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     **/
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    /*-
    private void registrarActa() {
        ejbServiciosPublicosDos.registrarActa(compania, "0",
                        Integer.parseInt(ciclo), codigoRuta, codigoRuta,
                        0,
                        99999,
                        BigDecimal.ZERO,
                        new BigDecimal("999999999999999999.99"), estado,
                        false, Integer.parseInt(abonos),
                        Integer.parseInt(cbChapetas), Integer.parseInt(pqr),
                        SysmanFunciones.convertirAFechaCadena(
                                                        fechaEmision),
                        intendencia,
                        usuario);
    }*/

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoRuta
     *
     * @return codigoRuta
     */
    public String getCodigoRuta() {
        return codigoRuta;
    }

    /**
     * Asigna la variable codigoRuta
     *
     * @param codigoRuta
     * Variable a asignar en codigoRuta
     */
    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
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
     * Retorna la variable nombre
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     *
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable dirTecnica
     *
     * @return dirTecnica
     */
    public String getDirTecnica() {
        return dirTecnica;
    }

    /**
     * Asigna la variable dirTecnica
     *
     * @param dirTecnica
     * Variable a asignar en dirTecnica
     */
    public void setDirTecnica(String dirTecnica) {
        this.dirTecnica = dirTecnica;
    }

    /**
     * Retorna la variable codigoInterno
     *
     * @return codigoInterno
     */
    public String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Asigna la variable codigoInterno
     *
     * @param codigoInterno
     * Variable a asignar en codigoInterno
     */
    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Retorna la variable sumMontoFin
     *
     * @return sumMontoFin
     */
    public String getSumMontoFin() {
        return sumMontoFin;
    }

    /**
     * Asigna la variable sumMontoFin
     *
     * @param sumMontoFin
     */
    public void setSumMontoFin(String sumMontoFin) {
        this.sumMontoFin = sumMontoFin;
    }

    /**
     * Retorna la variable sumSaldoFin
     *
     * @return sumSaldoFin
     */
    public String getSumSaldoFin() {
        return sumSaldoFin;
    }

    /**
     * Asigna la variable sumSaldoFin
     *
     * @param sumSaldoFin
     */
    public void setSumSaldoFin(String sumSaldoFin) {
        this.sumSaldoFin = sumSaldoFin;
    }

    /**
     * Retorna la variable sumValorCuota
     *
     * @return sumValorCuota
     */
    public String getSumValorCuota() {
        return sumValorCuota;
    }

    /**
     * Asigna la variable sumValorCuota
     *
     * @param sumValorCuota
     */
    public void setSumValorCuota(String sumValorCuota) {
        this.sumValorCuota = sumValorCuota;
    }

    /**
     * Retorna la variable etiquetaFecha
     *
     * @return etiquetaFecha
     */
    public String getEtiquetaFecha() {
        return etiquetaFecha;
    }

    /**
     * Asigna la variable etiquetaFecha
     *
     * @param etiquetaFecha
     */
    public void setEtiquetaFecha(String etiquetaFecha) {
        this.etiquetaFecha = etiquetaFecha;
    }

    /**
     * Retorna la variable conFraude
     *
     * @return conFraude
     */
    public boolean isConFraude() {
        return conFraude;
    }

    /**
     * Asigna la variable conFraude
     *
     * @param conFraude
     */
    public void setConFraude(boolean conFraude) {
        this.conFraude = conFraude;
    }

    /**
     * Retorna la variable mensValTotal
     *
     * @return mensValTotal
     */
    public String getMensValTotal() {
        return mensValTotal;
    }

    /**
     * Asigna la variable mensValTotal
     *
     * @param mensValTotal
     */
    public void setMensValTotal(String mensValTotal) {
        this.mensValTotal = mensValTotal;
    }

    /**
     * Retorna la variable mensFinanciable
     *
     * @return mensFinanciable
     */
    public String getMensFinanciable() {
        return mensFinanciable;
    }

    /**
     * Asigna la variable mensFinanciable
     *
     * @param mensFinanciable
     */
    public void setMensFinanciable(String mensFinanciable) {
        this.mensFinanciable = mensFinanciable;
    }

    /**
     * Retorna la variable mostrarMensVal
     *
     * @return mostrarMensVal
     */
    public boolean isMostrarMensVal() {
        return mostrarMensVal;
    }

    /**
     * Asigna la variable mostrarMensVal
     *
     * @param mostrarMensVal
     */
    public void setMostrarMensVal(boolean mostrarMensVal) {
        this.mostrarMensVal = mostrarMensVal;
    }

    /**
     * Retorna la variable mostrarMensDeuda
     *
     * @return mostrarMensDeuda
     */
    public boolean isMostrarMensDeuda() {
        return mostrarMensDeuda;
    }

    /**
     * Asigna la variable mostrarMensDeuda
     *
     * @param mostrarMensDeuda
     */
    public void setMostrarMensDeuda(boolean mostrarMensDeuda) {
        this.mostrarMensDeuda = mostrarMensDeuda;
    }

    /**
     * Retorna la variable mostrarMensFinan
     *
     * @return mostrarMensFinan
     */
    public boolean isMostrarMensFinan() {
        return mostrarMensFinan;
    }

    /**
     * Asigna la variable mostrarMensFinan
     *
     * @param mostrarMensFinan
     */
    public void setMostrarMensFinan(boolean mostrarMensFinan) {
        this.mostrarMensFinan = mostrarMensFinan;
    }

    public boolean isVerDPreg() {
        return verDPreg;
    }

    public void setVerDPreg(boolean verDPreg) {
        this.verDPreg = verDPreg;
    }

    /**
     * Retorna la variable verMensajeFinan
     *
     * @return verMensajeFinan
     */
    public boolean isVerMensajeFinan() {
        return verMensajeFinan;
    }

    /**
     * Asigna la variable verMensajeFinan
     *
     * @param verMensajeFinan
     */
    public void setVerMensajeFinan(boolean verMensajeFinan) {
        this.verMensajeFinan = verMensajeFinan;
    }

    /**
     * Retorna la variable verMenRecFact
     *
     * @return verMenRecFact
     */
    public boolean isVerMenRecFact() {
        return verMenRecFact;
    }

    /**
     * Asigna la variable verMenRecFact
     *
     * @param verMenRecFact
     */
    public void setVerMenRecFact(boolean verMenRecFact) {
        this.verMenRecFact = verMenRecFact;
    }

    /**
     * Retorna la variable editarRegSubInfoOp
     *
     * @return editarRegSubInfoOp
     */
    public boolean isEditarRegSubInfoOp() {
        return editarRegSubInfoOp;
    }

    /**
     * Asigna la variable editarRegSubInfoOp
     *
     * @param editarRegSubInfoOp
     */
    public void setEditarRegSubInfoOp(boolean editarRegSubInfoOp) {
        this.editarRegSubInfoOp = editarRegSubInfoOp;
    }

    /**
     * Retorna la variable indiceSubinfooperacion
     *
     * @return indiceSubinfooperacion
     */
    public int getIndiceSubinfooperacion() {
        return indiceSubinfooperacion;
    }

    /**
     * Asigna la variable indiceSubinfooperacion
     *
     * @param indiceSubinfooperacion
     */
    public void setIndiceSubinfooperacion(int indiceSubinfooperacion) {
        this.indiceSubinfooperacion = indiceSubinfooperacion;
    }

    /**
     * Retorna la variable indiceSubfinanciables
     *
     * @return indiceSubfinanciables
     */
    public int getIndiceSubfinanciables() {
        return indiceSubfinanciables;
    }

    /**
     * Asigna la variable indiceSubfinanciables
     *
     * @param indiceSubfinanciables
     */
    public void setIndiceSubfinanciables(int indiceSubfinanciables) {
        this.indiceSubfinanciables = indiceSubfinanciables;
    }

    public boolean isVerFechaHoraEjecucion() {
        return verFechaHoraEjecucion;
    }

    public void setVerFechaHoraEjecucion(boolean verFechaHoraEjecucion) {
        this.verFechaHoraEjecucion = verFechaHoraEjecucion;
    }

    /**
     * Retorna la variable verUsuario
     *
     * @return verUsuario
     */
    public boolean isVerUsuario() {
        return verUsuario;
    }

    /**
     * Asigna la variable verUsuario
     *
     * @param verUsuario
     */
    public void setVerUsuario(boolean verUsuario) {
        this.verUsuario = verUsuario;
    }

    /**
     * Retorna la variable verEstadoOpe
     *
     * @return verEstadoOpe
     */
    public boolean isVerEstadoOpe() {
        return verEstadoOpe;
    }

    /**
     * Asigna la variable verEstadoOpe
     *
     * @param verEstadoOpe
     */
    public void setVerEstadoOpe(boolean verEstadoOpe) {
        this.verEstadoOpe = verEstadoOpe;
    }

    /**
     * Retorna la variable verHoraCreacion
     *
     * @return verHoraCreacion
     */
    public boolean isVerHoraCreacion() {
        return verHoraCreacion;
    }

    /**
     * Asigna la variable verHoraCreacion
     *
     * @param verHoraCreacion
     */
    public void setVerHoraCreacion(boolean verHoraCreacion) {
        this.verHoraCreacion = verHoraCreacion;
    }

    public boolean isBloquearFechaHoraE() {
        return bloquearFechaHoraE;
    }

    public void setBloquearFechaHoraE(boolean bloquearFechaHoraE) {
        this.bloquearFechaHoraE = bloquearFechaHoraE;
    }

    public String getMensajeDPreg() {
        return mensajeDPreg;
    }

    public void setMensajeDPreg(String mensajeDPreg) {
        this.mensajeDPreg = mensajeDPreg;
    }

    public String getUsuario() {
        return usuario;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listacmbAno
     *
     * @return listacmbAno
     */
    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    /**
     * Asigna la lista listacmbAno
     *
     * @param listacmbAno
     * Variable a asignar en listacmbAno
     */
    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }

    /**
     * Retorna la lista listacmbPeriodo
     *
     * @return listacmbPeriodo
     */
    public List<Registro> getListacmbPeriodo() {
        return listacmbPeriodo;
    }

    /**
     * Asigna la lista listacmbPeriodo
     *
     * @param listacmbPeriodo
     * Variable a asignar en listacmbPeriodo
     */
    public void setListacmbPeriodo(List<Registro> listacmbPeriodo) {
        this.listacmbPeriodo = listacmbPeriodo;
    }

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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaMedidor
     *
     * @return listaMedidor
     */
    public RegistroDataModelImpl getListaMedidor() {
        return listaMedidor;
    }

    public RegistroDataModelImpl getListaAforador() {
        return listaAforador;
    }

    public void setListaAforador(RegistroDataModelImpl listaAforador) {
        this.listaAforador = listaAforador;
    }

    public RegistroDataModelImpl getListaAforadorE() {
        return listaAforadorE;
    }

    public void setListaAforadorE(RegistroDataModelImpl listaAforadorE) {
        this.listaAforadorE = listaAforadorE;
    }

    /**
     * Asigna la lista listaMedidor
     *
     * @param listaMedidor
     * Variable a asignar en listaMedidor
     */
    public void setListaMedidor(RegistroDataModelImpl listaMedidor) {
        this.listaMedidor = listaMedidor;
    }

    /**
     * Retorna la lista listaMedidorE
     *
     * @return listaMedidorE
     */
    public RegistroDataModelImpl getListaMedidorE() {
        return listaMedidorE;
    }

    /**
     * Asigna la lista listaMedidorE
     *
     * @param listaMedidorE
     * Variable a asignar en listaMedidorE
     */
    public void setListaMedidorE(RegistroDataModelImpl listaMedidorE) {
        this.listaMedidorE = listaMedidorE;
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

    /**
     * Retorna la lista listaFormateadoE
     *
     * @return listaFormateadoE
     */
    public RegistroDataModelImpl getListaFormateadoE() {
        return listaFormateadoE;
    }

    /**
     * Asigna la lista listaFormateadoE
     *
     * @param listaFormateadoE
     * Variable a asignar en listaFormateadoE
     */
    public void setListaFormateadoE(RegistroDataModelImpl listaFormateadoE) {
        this.listaFormateadoE = listaFormateadoE;
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

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
    }

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

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    /**
     * Retorna la lista listaSubinfooperacion
     *
     * @return listaSubinfooperacion
     */
    public RegistroDataModel getListaSubinfooperacion() {
        return listaSubinfooperacion;
    }

    /**
     * Asigna la lista listaSubinfooperacion
     *
     * @param listaSubinfooperacion
     * Variable a asignar en listaSubinfooperacion
     */
    public void setListaSubinfooperacion(
        RegistroDataModel listaSubinfooperacion) {
        this.listaSubinfooperacion = listaSubinfooperacion;
    }

    /**
     * Retorna la lista listaSubfinanciables
     *
     * @return listaSubfinanciables
     */
    public RegistroDataModelImpl getListaSubfinanciables() {
        return listaSubfinanciables;
    }

    /**
     * Asigna la lista listaSubfinanciables
     *
     * @param listaSubfinanciables
     * Variable a asignar en listaSubfinanciables
     */
    public void setListaSubfinanciables(
        RegistroDataModelImpl listaSubfinanciables) {
        this.listaSubfinanciables = listaSubfinanciables;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    public boolean isParEstadoOperacion() {
        return parEstadoOperacion;
    }

    public void setParEstadoOperacion(boolean parEstadoOperacion) {
        this.parEstadoOperacion = parEstadoOperacion;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSubInfoOperacion
     *
     * @return registroSubSubInfoOperacion
     */
    public Registro getRegistroSubSubInfoOperacion() {
        return registroSubSubInfoOperacion;
    }

    /**
     * Asigna el objeto registroSubSubInfoOperacion
     *
     * @param registroSubSubInfoOperacion
     * Variable a asignar en registroSubSubInfoOperacion
     */
    public void setRegistroSubSubInfoOperacion(
        Registro registroSubSubInfoOperacion) {
        this.registroSubSubInfoOperacion = registroSubSubInfoOperacion;
    }

    /**
     * Retorna el objeto registroSubSubFinanciables
     *
     * @return registroSubSubFinanciables
     */
    public Registro getRegistroSubSubFinanciables() {
        return registroSubSubFinanciables;
    }

    /**
     * Asigna el objeto registroSubSubFinanciables
     *
     * @param registroSubSubFinanciables
     * Variable a asignar en registroSubSubFinanciables
     */
    public void setRegistroSubSubFinanciables(
        Registro registroSubSubFinanciables) {
        this.registroSubSubFinanciables = registroSubSubFinanciables;
    }
    // </SET_GET_ADICIONALES>
}
