/*-
 * EvaluacionFormatosAntiguosControlador.java
 *
 * 1.0
 * 
 * 02/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar las evaluaciones del formato antiguo
 * antes del 2000
 *
 * @version 1.0, 02/03/2017
 * @author jguerrero
 */
@ManagedBean
@ViewScoped
public class EvaluacionFormatosAntiguosControlador extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el String DP_NUMEDOCU
     * 
     */
    private final String dpNumeroDocuCons;
    /**
     * Constante a nivel de clase que almacena el String SUCURSAL
     * 
     */
    private final String sucursalCons;
    /**
     * Constante a nivel de clase que almacena el String EV_FORMATO
     * 
     */

    private final String evFormatoCons;
    /**
     * Constante a nivel de clase que almacena el String
     * EV_FECHHASTAEVAL
     * 
     */

    private final String evFechHastaEvalCons;
    /**
     * Constante a nivel de clase que almacena el String
     * EV_FECHDESDEEVAL
     * 
     */

    private final String evfechDesdeEvalCons;
    /**
     * Constante a nivel de clase que almacena el String
     * NAT_COMITE_CALIFICACION_EV
     * 
     */

    private final String comitecalificacionCons;
    /**
     * Constante a nivel de clase que almacena el String NUMERO_DCTO
     * 
     */

    private final String numeroDocumentoCons;
    /**
     * Constante a nivel de clase que almacena el String
     * EV_PUNTOBJEDESE
     * 
     */

    private final String evPuntoEjeDesCons;
    /**
     * Constante a nivel de clase que almacena el String EV_SATISF_R
     * 
     */

    private final String evSatisRegCons;
    /**
     * Constante a nivel de clase que almacena el String EV_SATISF_B
     * 
     */

    private final String evSatisBueCons;
    /**
     * Constante a nivel de clase que almacena el String EV_SATISF_EX
     * 
     */

    private final String evSatisExCons;
    /**
     * Constante a nivel de clase que almacena el String
     * NOMBRECOMPLETO
     * 
     */

    private final String nombreCompletoCons;
    /**
     * Constante a nivel de clase que almacena el String CARGO
     * 
     */

    private final String cargoCons;
    /**
     * Constante a nivel de clase que almacena el String
     * CODIGO_PERSONA
     * 
     */

    private final String codigoPersonaCons;
    /**
     * Constante a nivel de clase que almacena el String CODIGO
     * 
     */

    private final String codigoCons;
    /**
     * Constante a nivel de clase que almacena el String COMPANIA
     * 
     */

    private final String companiaCons;
    /**
     * Constante a nivel de clase que almacena el String NOMBRE
     * 
     */

    private final String nombreCons;
    /**
     * Constante a nivel de clase que almacena el String NO_CARGON
     * 
     */

    private final String noCargoNomCons;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena temporalmente lo seleccionado del check
     * insatisfactoria en el formulario
     */
    private boolean checkInSatisfactoria;
    /**
     * Atributo que almacena temporalmente lo digitado en el campo
     * numeroDias en el formulario
     */
    private String numeroDias;
    /**
     * Atributo que almacena temporalmente lo seleccinado en el combo
     * formato.
     */
    private String tipoFormato;
    /**
     * Atributo que almacena temporalmente lo seleccinado en la fecha
     * inciial de evaluacion
     */
    private String fechaInicialEv;
    /**
     * Atributo que almacena temporalmente el numero de documento de
     * la persona seleccionada
     */
    private String dpNumeroDocu;
    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */
    private String sucursal;
    /**
     * Atributo sirve para hacer visible o no el subformulario
     * dependiente el combo del formato
     */
    private boolean visibleSubFormulario;
    /**
     * Atributo sirve para hacer visible o no los campos del nivel
     * ejecutivo dependiente el combo del formato
     */
    private boolean visiblePestanaEjecu;
    /**
     * Atributo sirve para hacer visible o no los campos del nivel
     * profesiona dependiente el combo del formato
     */
    private boolean visiblePestanaProfe;
    /**
     * Atributo sirve para hacer visible o no los campos de la
     * admin/opera dependiente el combo del formato
     */
    private boolean visiblePestanaAdmin;
    /**
     * Atributo sirve para hacer visible o no los campos del evaludaro
     * dependiente el combo del formato
     */
    private boolean visibleCamposEvalua;
    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */
    private String documentoPersona;
    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */
    private String codigoPersona;
    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */

    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */
    private String nombreCompleto;

    /**
     * Atributo que almacena temporalmente la sucursal de la persona
     * seleccionada
     */
    private String cargo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista encargada de almacenar los datos de la clase como
     * respuesta de la llamda a la base datos
     */
    private List<Registro> listaevClaseval;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar los datos de la persona como
     * respuesta de la llamda a la base datos
     */
    private RegistroDataModel listaNumeroDcto;
    /**
     * Lista encargada de almacenar los datos de la persona como
     * respuesta de la llamda a la base datos
     */
    private RegistroDataModel listaNumeroDctoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Lista encargada de almacenar los datos de la persona evauadora
     * como respuesta de la llamda a la base datos
     */
    private RegistroDataModel listaevNumedocueval;
    /**
     * Lista encargada de almacenar los datos de la persona a evaluar
     * como respuesta de la llamda a la base datos
     */
    private RegistroDataModel listaDatosPersonales;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista encargada de almacenar los datos del comite evaluador
     * como respuesta de la llamda a la base datos
     */
    private List<Registro> listaSubformulariocomitecalificacion;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    private String sucursalEdicion;
    private boolean nombreBloqueado;
    private final String diasNoEvaluados;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * EvaluacionFormatosAntiguosControlador
     */
    public EvaluacionFormatosAntiguosControlador() {
        super();
        compania = SessionUtil.getCompania();

        dpNumeroDocuCons = "DP_NUMEDOCU";
        sucursalCons = "SUCURSAL";
        evFormatoCons = "EV_FORMATO";
        evFechHastaEvalCons = "EV_FECHHASTAEVAL";
        evfechDesdeEvalCons = "EV_FECHDESDEEVAL";
        comitecalificacionCons = "NAT_COMITE_CALIFICACION_EV";

        numeroDocumentoCons = "NUMERO_DCTO";
        evPuntoEjeDesCons = "EV_PUNTOBJEDESE";
        evSatisRegCons = "EV_SATISF_R";
        evSatisBueCons = "EV_SATISF_B";
        evSatisExCons = "EV_SATISF_EX";

        nombreCompletoCons = "NOMBRECOMPLETO";
        cargoCons = "CARGO";
        codigoPersonaCons = "CODIGO_PERSONA";
        codigoCons = "CODIGO";
        companiaCons = "COMPANIA";
        nombreCons = "NOMBRE";
        noCargoNomCons = "NO_CARGON";
        diasNoEvaluados = "DIASNOEVALUADOS";

        visiblePestanaEjecu = true;
        visibleCamposEvalua = true;

        try {
            numFormulario = 1318;
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaNumeroDcto();
        cargarListaNumeroDctoE();
        cargarListaevNumedocueval();
        cargarListaDatosPersonales();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaevClaseval();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubformulariocomitecalificacion();
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
        listaSubformulariocomitecalificacion = null;
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
        tabla = "NAT_EVALUACION_ANTIGUA";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = " SELECT " +
            " NAT_EVALUACION_ANTIGUA.COMPANIA ," +
            " NAT_EVALUACION_ANTIGUA.DP_NUMEDOCU ," +
            " NAT_EVALUACION_ANTIGUA.SUCURSAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_FORMATO ," +
            " NAT_EVALUACION_ANTIGUA.EV_FECHDESDEEVAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_FECHHASTAEVAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_TIPODOCUEVAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_NUMEDOCUEVAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_SUCURSAL ," +
            " NAT_EVALUACION_ANTIGUA.EV_NOMBEVAL ," +
            " NAT_EVALUACION_ANTIGUA.CARGOEVALUADOR ," +
            " NAT_EVALUACION_ANTIGUA.EV_MOTICONC ," +
            " NAT_EVALUACION_ANTIGUA.DIASNOEVALUADOS ," +
            " NAT_EVALUACION_ANTIGUA.EV_PUNTOBJEDESE ," +
            " NAT_EVALUACION_ANTIGUA.EV_FECHDILI ," +
            " NAT_EVALUACION_ANTIGUA.EV_CLASEVAL ," +
            " NAT_EVALUACION_ANTIGUA.CONSECUTIVO ," +
            " NAT_EVALUACION_ANTIGUA.EV_CODIGOPERSONA ," +
            " NAT_EVALUACION_ANTIGUA.EV_FECHANOTI ," +
            " NAT_EVALUACION_ANTIGUA.EV_GRADO_EVALUADOR ," +
            " NAT_EVALUACION_ANTIGUA.NUM_PERIODOPRUEBA ," +
            " NAT_EVALUACION_ANTIGUA.NUM_EV_PERIODICA ," +
            " NAT_EVALUACION_ANTIGUA.EV_SATISF_EX ," +
            " NAT_EVALUACION_ANTIGUA.EV_SATISF_B ," +
            " NAT_EVALUACION_ANTIGUA.EV_SATISF_R ," +
            " NAT_EVALUACION_ANTIGUA.OBSERVACIONES ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_DIRECCION ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_PROGRAMACION ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_COMUNICACION ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_ACTITUDTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_EVAL_CONTROL ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_CALIDADTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_PLANEACIONTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_CAPACIDADJUICIO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_CALIDADMATERIALES ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_ACTITUDTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_COORDSUPERVISION ," +
            " NAT_EVALUACION_ANTIGUA.EV_NE_CONOFUNCIONES ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_CONOFUNCIONES ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_RESPONSABILIDAD ," +
            " NAT_EVALUACION_ANTIGUA.EV_NP_RELAINTERPERSONALES ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_CALIDADTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_CANTIDADTRABAJO ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_RESPONSABILIDAD ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_INICIATIVA ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_RELAINTER ," +
            " NAT_EVALUACION_ANTIGUA.EV_NAO_COLABORACION ," +
            " NAT_EVALUACION_ANTIGUA.EV_REC_REPOS ," +
            " NAT_EVALUACION_ANTIGUA.EV_REC_APEL ," +
            " NAT_EVALUACION_ANTIGUA.CREATED_BY ," +
            " NAT_EVALUACION_ANTIGUA.DATE_CREATED ," +
            " NAT_EVALUACION_ANTIGUA.MODIFIED_BY ," +
            " NAT_EVALUACION_ANTIGUA.DATE_MODIFIED," +
            "  (NAT_DATOS_PERSONALES.APELLIDO1||' '|| NAT_DATOS_PERSONALES.APELLIDO2||' '||NAT_DATOS_PERSONALES.NOMBRES) NOMBRE ,"
            + " NAT_DATOS_PERSONALES.CODIGO CODIGO_PERSONA, "
            + " NAT_DATOS_PERSONALES.NUMERO_DCTO DOCUMENTO_PERSONA"
            +
            " FROM NAT_EVALUACION_ANTIGUA " +
            " INNER JOIN NAT_DATOS_PERSONALES " +
            " ON NAT_EVALUACION_ANTIGUA.COMPANIA     = NAT_DATOS_PERSONALES.COMPANIA"
            +
            " AND NAT_EVALUACION_ANTIGUA.DP_NUMEDOCU = NAT_DATOS_PERSONALES.NUMERO_DCTO"
            +
            " AND NAT_EVALUACION_ANTIGUA.SUCURSAL    = NAT_DATOS_PERSONALES.SUCURSAL "
            +

            " AND NAT_EVALUACION_ANTIGUA.COMPANIA='" + compania + "'";

    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "SELECT NAT_EVALUACION_ANTIGUA.COMPANIA ,  " +
            "   NAT_EVALUACION_ANTIGUA.EV_FECHDESDEEVAL, " +
            "   NAT_EVALUACION_ANTIGUA.SUCURSAL ," +
            "   NAT_EVALUACION_ANTIGUA.EV_FORMATO ," +
            "   NAT_EVALUACION_ANTIGUA.EV_FECHHASTAEVAL ," +
            "   NAT_DATOS_PERSONALES.CODIGO," +
            "   NAT_EVALUACION_ANTIGUA.DP_NUMEDOCU ," +
            "   (NAT_DATOS_PERSONALES.APELLIDO1||' '|| NAT_DATOS_PERSONALES.APELLIDO2||' '||NAT_DATOS_PERSONALES.NOMBRES) NOMBRE,"
            +
            "   TO_CHAR(NAT_EVALUACION_ANTIGUA.EV_FECHDESDEEVAL,'DD/MM/YYYY') FECHADESDE  "
            +
            " FROM NAT_EVALUACION_ANTIGUA " +
            " INNER JOIN NAT_DATOS_PERSONALES " +
            " ON NAT_EVALUACION_ANTIGUA.COMPANIA     = NAT_DATOS_PERSONALES.COMPANIA"
            +
            " AND NAT_EVALUACION_ANTIGUA.DP_NUMEDOCU = NAT_DATOS_PERSONALES.NUMERO_DCTO"
            +
            " AND NAT_EVALUACION_ANTIGUA.SUCURSAL    = NAT_DATOS_PERSONALES.SUCURSAL "
            +
            " WHERE  NAT_EVALUACION_ANTIGUA.COMPANIA='" + compania + "'";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    /**
     * 
     * Carga la lista listaSubformulariocomitecalificacion
     *
     */
    public void cargarListaSubformulariocomitecalificacion() {
        try {

            listaSubformulariocomitecalificacion = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "  SELECT "
                                + "  NAT_COMITE_CALIFICACION_EV.COMPANIA,"
                                + "  NAT_COMITE_CALIFICACION_EV.SUCURSAL,"
                                + "  NAT_COMITE_CALIFICACION_EV.ID," +
                                "   NAT_COMITE_CALIFICACION_EV.NUM_DCTO," +
                                "   NAT_COMITE_CALIFICACION_EV.CODIGO," +
                                "   NAT_COMITE_CALIFICACION_EV.FECHA_DESDE," +
                                "   NAT_COMITE_CALIFICACION_EV.FECHA_HASTA," +
                                "   NAT_COMITE_CALIFICACION_EV.FORMATO," +
                                "   NAT_COMITE_CALIFICACION_EV.CARGO," +
                                "   NAT_COMITE_CALIFICACION_EV.DOCUMENTO_ID," +
                                "   NAT_COMITE_CALIFICACION_EV.CODIGO_EV," +
                                "   ( NAT_DATOS_PERSONALES.APELLIDO1||' '|| NAT_DATOS_PERSONALES.APELLIDO2||' '||NAT_DATOS_PERSONALES.NOMBRES)  NOMBRECOMPLETO"
                                +
                                " FROM NAT_COMITE_CALIFICACION_EV" +
                                " LEFT JOIN NAT_DATOS_PERSONALES " +
                                " ON  NAT_COMITE_CALIFICACION_EV.COMPANIA= NAT_DATOS_PERSONALES.COMPANIA"
                                +
                                " AND NAT_COMITE_CALIFICACION_EV.DOCUMENTO_ID=NAT_DATOS_PERSONALES.NUMERO_DCTO"
                                +
                                " AND NAT_COMITE_CALIFICACION_EV.SUCURSAL=NAT_DATOS_PERSONALES.SUCURSAL"
                                + " WHERE NAT_COMITE_CALIFICACION_EV.COMPANIA='"
                                + compania
                                + "' AND NAT_COMITE_CALIFICACION_EV.NUM_DCTO='"
                                + registro.getCampos().get(dpNumeroDocuCons)
                                + "' AND NAT_COMITE_CALIFICACION_EV.SUCURSAL='"
                                + registro.getCampos().get(sucursalCons)
                                + "' AND NAT_COMITE_CALIFICACION_EV.FORMATO='"
                                + registro.getCampos().get(evFormatoCons)
                                + "' AND NAT_COMITE_CALIFICACION_EV.FECHA_DESDE="
                                + SysmanFunciones.formatearFecha((Date) registro
                                                .getCampos()
                                                .get(evfechDesdeEvalCons))
                                + " AND NAT_COMITE_CALIFICACION_EV.FECHA_HASTA="
                                + SysmanFunciones.formatearFecha((Date) registro
                                                .getCampos()
                                                .get(evFechHastaEvalCons)),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            comitecalificacionCons));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaevClaseval
     *
     */
    public void cargarListaevClaseval() {
        listaevClaseval = service.getListado(conectorPool, "SELECT " +
            "     CE_CODIGO, " +
            "     CE_NOMBRE" +
            " " +
            " FROM " +
            "     NAT_TIPO_EVALUACION " +
            " WHERE " +
            "     CE_NOMBRE IS NOT NULL " +
            " ORDER BY " +
            "     CE_NOMBRE");
    }

    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDcto() {
        listaNumeroDcto = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1318_nuevo:TBCB4332",
                        "SELECT   NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            "   ( APELLIDO1|| ' '|| APELLIDO2 || ' '|| NOMBRES) NOMBRE,"
                            +
                            "     NAT_NOMBRAMIENTO.NO_CARGON," +
                            " Nat_Datos_Personales.CODIGO" +
                            "   FROM NAT_DATOS_PERSONALES" +
                            " LEFT JOIN NAT_NOMBRAMIENTO" +
                            " ON NAT_DATOS_PERSONALES.COMPANIA     =NAT_NOMBRAMIENTO.COMPANIA"
                            +
                            "   AND NAT_DATOS_PERSONALES.NUMERO_DCTO = NAT_NOMBRAMIENTO.DP_NUMEDOCU"
                            +
                            " AND NAT_DATOS_PERSONALES.SUCURSAL    =NAT_NOMBRAMIENTO.SUCURSAL"
                            +
                            " AND NAT_DATOS_PERSONALES.CODIGO      = NAT_NOMBRAMIENTO.NB_CODIGOPERSONA"
                            +
                            " GROUP BY NAT_DATOS_PERSONALES.CODIGO," +
                            "   NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            "   ( APELLIDO1|| ' '|| APELLIDO2|| ' '|| NOMBRES),"
                            +
                            "   NAT_NOMBRAMIENTO.NO_CARGON",
                        true, numeroDocumentoCons);
    }

    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDctoE() {
        listaNumeroDctoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1318_nuevo:TBCB4332", "SELECT " +
                            "     NAT_DATOS_PERSONALES.CODIGO, " +
                            "     NAT_DATOS_PERSONALES.NUMERO_DCTO, " +
                            "     ( APELLIDO1|| ' '|| APELLIDO2|| ' '|| NOMBRES) NOMBRE, "
                            +
                            "     NOMBRAMIENTO.NO_CARGON " +
                            " FROM " +
                            "     NAT_DATOS_PERSONALES " +
                            "         LEFT JOIN NAT_NOMBRAMIENTO NOMBRAMIENTO "
                            +
                            "         ON NAT_DATOS_PERSONALES.CODIGO = NOMBRAMIENTO.NB_CODIGOPERSONA "
                            +
                            " GROUP BY " +
                            "     NAT_DATOS_PERSONALES.CODIGO, " +
                            "     NAT_DATOS_PERSONALES.NUMERO_DCTO, " +
                            "     ( APELLIDO1|| ' '|| APELLIDO2|| ' '|| NOMBRES), "
                            +
                            "     NOMBRAMIENTO.NO_CARGON " +
                            " ",
                        true, numeroDocumentoCons);
    }

    /**
     * 
     * Carga la lista listaevNumedocueval
     *
     */
    public void cargarListaevNumedocueval() {
        listaevNumedocueval = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FR1318_nuevo:TBCB4331",
                        "SELECT NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            "   (NAT_DATOS_PERSONALES.APELLIDO1" +
                            "     ||' '" +
                            "   || NAT_DATOS_PERSONALES.APELLIDO2" +
                            "     ||' '" +
                            "   ||NAT_DATOS_PERSONALES.NOMBRES) NOMBRE," +
                            "   NAT_NOMBRAMIENTO.NO_CARGON," +
                            "   NAT_NOMBRAMIENTO.NO_GRADOCARGO" +
                            " FROM NAT_DATOS_PERSONALES" +
                            " INNER JOIN NAT_NOMBRAMIENTO" +
                            " ON NAT_DATOS_PERSONALES.COMPANIA     =NAT_NOMBRAMIENTO.COMPANIA"
                            +
                            " AND NAT_DATOS_PERSONALES.NUMERO_DCTO = NAT_NOMBRAMIENTO.DP_NUMEDOCU"
                            +
                            " AND NAT_DATOS_PERSONALES.SUCURSAL    =NAT_NOMBRAMIENTO.SUCURSAL"
                            +
                            " GROUP BY NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            "   (NAT_DATOS_PERSONALES.APELLIDO1" +
                            "   ||' '" +
                            "   || NAT_DATOS_PERSONALES.APELLIDO2" +
                            "   ||' '" +
                            "   ||NAT_DATOS_PERSONALES.NOMBRES)," +
                            "   NAT_NOMBRAMIENTO.NO_CARGON," +
                            "   NAT_NOMBRAMIENTO.NO_GRADOCARGO",
                        true, numeroDocumentoCons);
    }

    /**
     * 
     * Carga la lista listaDatosPersonales
     *
     */
    public void cargarListaDatosPersonales() {
        listaDatosPersonales = new RegistroDataModel(
                        ConectorPool.ESQUEMA_SYSMAN, ":FR1318_nuevo:TBCB4333",
                        "SELECT NAT_DATOS_PERSONALES.CODIGO," +
                            "   NAT_DATOS_PERSONALES.NUMERO_DCTO," +
                            " ( NAT_DATOS_PERSONALES.APELLIDO1||' '|| NAT_DATOS_PERSONALES.APELLIDO2||' '||NAT_DATOS_PERSONALES.NOMBRES)  NOMBRE,"
                            + " NAT_DATOS_PERSONALES.SUCURSAL"
                            +
                            " FROM NAT_DATOS_PERSONALES" +
                            " LEFT JOIN NAT_NOMBRAMIENTO" +
                            " ON NAT_DATOS_PERSONALES.COMPANIA     = NAT_NOMBRAMIENTO.COMPANIA"
                            +
                            " AND NAT_DATOS_PERSONALES.NUMERO_DCTO = NAT_NOMBRAMIENTO.DP_NUMEDOCU"
                            +
                            " AND NAT_DATOS_PERSONALES.SUCURSAL    = NAT_NOMBRAMIENTO.SUCURSAL"
                            +
                            " WHERE NAT_DATOS_PERSONALES.COMPANIA = '"
                            + compania + "'",
                        true, nombreCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control evFormato Y es el
     * encargado de gestionar la visibilidad de los campos de los
     * niveles y del subformulario
     * 
     */
    public void cambiarevFormato() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(evPuntoEjeDesCons, null);

        cargarRegistroFormato();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarInsatisfactoria() {
        // <CODIGO_DESARROLLADO>
        if (checkInSatisfactoria) {
            registro.getCampos().put(evSatisBueCons, false);
            registro.getCampos().put(evSatisRegCons, false);
            registro.getCampos().put(evSatisExCons, false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CaliExcele es el
     * encargado de que solo el check de Excelente pueda ser
     * seleccionado
     * 
     * 
     */
    public void cambiarCaliExcele() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get(evSatisExCons)) {
            registro.getCampos().put(evSatisBueCons, false);
            registro.getCampos().put(evSatisRegCons, false);
            checkInSatisfactoria = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CaliBuen es el encargado
     * de que solo el check de Bueno pueda ser seleccionado
     */
    public void cambiarCaliBuen() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get(evSatisBueCons)) {
            registro.getCampos().put(evSatisExCons, false);
            registro.getCampos().put(evSatisRegCons, false);
            checkInSatisfactoria = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CaliRegulo y es el
     * encargado de que solo el check de reguar pueda ser seleccionado
     * 
     * 
     */
    public void cambiarCaliRegulo() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get(evSatisRegCons)) {
            registro.getCampos().put(evSatisBueCons, false);
            registro.getCampos().put(evSatisExCons, false);
            checkInSatisfactoria = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control documento
     * 
     * 
     */
    public void cambiardocumento() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control evRecRepos es el
     * encargado de que solo el check de reposicion pueda ser
     * seleccionado
     * 
     */
    public void cambiarevRecRepos() {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get("EV_REC_REPOS")) {
            registro.getCampos().put("EV_REC_APEL", false);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control evRecApel es el
     * encargado de que solo el check de apelacion pueda ser
     * seleccionado
     * 
     * 
     */
    public void cambiarevRecApel() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get("EV_REC_APEL")) {
            registro.getCampos().put("EV_REC_REPOS", false);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DiasNoEvaluados y
     * ademas, es el encargado de calcular los dias evaluados
     * 
     * 
     */
    public void cambiarDiasNoEvaluados() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        diasNoEvaluados)) {
            int diasEvaluados = Integer.parseInt(registro.getCampos()
                            .get(diasNoEvaluados).toString());

            int resultado = 365 - diasEvaluados;
            numeroDias = String.valueOf(resultado);

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarnivelEjecuDireccion() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelEjecuProgramacion
     * Se encarga de calcular el puntaje del nivel ejecutivo
     * 
     */
    public void cambiarnivelEjecuProgramacion() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelEjecuCoordSupervision
     * 
     * 
     */
    public void cambiarnivelEjecuCoordSupervision() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelEjecuConoFunciones
     * 
     * 
     */
    public void cambiarnivelEjecuConoFunciones() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelEjecuComunicacion
     * 
     * 
     */
    public void cambiarnivelEjecuComunicacion() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelEjecuActitudTrabajo
     * 
     * 
     */
    public void cambiarnivelEjecuActitudTrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelEjecuEvalControl
     * 
     */
    public void cambiarnivelEjecuEvalControl() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelEjecutivo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalCalidadtrabajo
     * 
     * 
     */
    public void cambiarnivelProfesionalCalidadtrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalPlaneacionTrabajo
     * 
     * 
     */
    public void cambiarnivelProfesionalPlaneacionTrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalConoFunciones
     * 
     */
    public void cambiarnivelProfesionalConoFunciones() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalCapacidadJucio
     * 
     * 
     */
    public void cambiarnivelProfesionalCapacidadJucio() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalResponsabilidad
     * 
     * 
     */
    public void cambiarnivelProfesionalResponsabilidad() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelProfesionalActitudTrabajo
     * 
     * 
     */
    public void cambiarnivelProfesionalActitudTrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelProfesional();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeCalidadTrabajo
     * 
     * 
     */
    public void cambiarnivelAdminOpeCalidadTrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeCantidadTrabajo
     * 
     * 
     */
    public void cambiarnivelAdminOpeCantidadTrabajo() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeCalidadMateriales
     * 
     * 
     */
    public void cambiarnivelAdminOpeCalidadMateriales() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeResponsabilidad
     * 
     * 
     */
    public void cambiarnivelAdminOpeResponsabilidad() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelAdminOpeIniciativa
     * 
     * 
     */
    public void cambiarnivelAdminOpeIniciativa() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control nivelAdminOpeRelaInter
     * 
     * 
     */
    public void cambiarnivelAdminOpeRelaInter() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeColaboracion
     * 
     * 
     */
    public void cambiarnivelAdminOpeColaboracion() {
        // <CODIGO_DESARROLLADO>
        calcularPuntajeNivelAdminOpera();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control
     * nivelAdminOpeColaboracion Es el encargado de actualizar los
     * campos de numero de documento nombre y nombre cargo en el
     * subformario luego de ser seleccionado
     * 
     * 
     */

    public void cambiarNumeroDctoC(int rowNum) {

        listaSubformulariocomitecalificacion.get(rowNum).getCampos()
                        .put("CODIGO_EV", auxiliar);
        listaSubformulariocomitecalificacion.get(rowNum).getCampos()
                        .put("DOCUMENTO_ID", auxiliar);
        listaSubformulariocomitecalificacion.get(rowNum).getCampos()
                        .put(nombreCompletoCons, nombreCompleto);
        listaSubformulariocomitecalificacion.get(rowNum).getCampos()
                        .put(cargoCons, cargo);

        listaSubformulariocomitecalificacion.get(rowNum).getCampos()
                        .put(sucursalCons, sucursalEdicion);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroDcto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilaNumeroDcto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("DOCUMENTO_ID",
                        registroAux.getCampos().get(numeroDocumentoCons));

        registroSub.getCampos().put("CODIGO_EV",
                        registroAux.getCampos().get(numeroDocumentoCons));

        registroSub.getCampos().put(cargoCons,
                        registroAux.getCampos().get(noCargoNomCons));

        registroSub.getCampos().put(nombreCompletoCons,
                        registroAux.getCampos().get(nombreCons));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroDcto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDctoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(numeroDocumentoCons);
        nombreCompleto = registroAux.getCampos().get(nombreCons)
                        .toString();

        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        cargoCons)) {
            cargo = registroAux.getCampos().get(cargoCons).toString();
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaevNumedocueval
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaevNumedocueval(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EV_NUMEDOCUEVAL",
                        registroAux.getCampos().get(numeroDocumentoCons));

        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        nombreCons)) {

            String nombreEvaluador = registroAux.getCampos().get(nombreCons)
                            .toString();

            registro.getCampos().put("EV_NOMBEVAL", nombreEvaluador);

        }
        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        noCargoNomCons)) {
            String cargoEvaluador = registroAux.getCampos().get(noCargoNomCons)
                            .toString();
            registro.getCampos().put("CARGOEVALUADOR", cargoEvaluador);
        }
        if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        "NO_GRADOCARGO")) {
            String gradoEvaluador = registroAux.getCampos().get("NO_GRADOCARGO")
                            .toString();
            registro.getCampos().put("EV_GRADO_EVALUADOR", gradoEvaluador);

        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDatosPersonales
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDatosPersonales(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons));

        if ((registroAux.getCampos().get(numeroDocumentoCons) != null)
            && (registroAux.getCampos().get(sucursalCons) != null)) {

            dpNumeroDocu = registroAux.getCampos().get(numeroDocumentoCons)
                            .toString();
            registro.getCampos().put("DOCUMENTO_PERSONA",
                            registroAux.getCampos().get(numeroDocumentoCons));
            registro.getCampos().put(codigoPersonaCons,
                            registroAux.getCampos().get(codigoCons));

            sucursal = registroAux.getCampos().get(sucursalCons)
                            .toString();
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario
     * Subformulariocomitecalificacion
     * 
     */
    public void agregarRegistroSubSubformulariocomitecalificacion() {
        try {
            int conteo;
            String condicion = "COMPANIA='" + compania + "'";
            Long consecutivoComite = Acciones.genConsecutivo(
                            ConectorPool.ESQUEMA_SYSMAN,
                            comitecalificacionCons, condicion, "ID", "1");
            registroSub.getCampos().remove(nombreCompletoCons);

            registroSub.getCampos().put(companiaCons,
                            registro.getCampos().get(companiaCons));
            registroSub.getCampos().put("NUM_DCTO",
                            registro.getCampos().get(dpNumeroDocuCons));
            registroSub.getCampos().put(sucursalCons,
                            registro.getCampos().get(sucursalCons));
            registroSub.getCampos().put("FORMATO",
                            registro.getCampos().get(evFormatoCons));
            registroSub.getCampos().put("FECHA_DESDE",
                            registro.getCampos().get(evfechDesdeEvalCons));
            registroSub.getCampos().put("FECHA_HASTA",
                            registro.getCampos().get(evFechHastaEvalCons));
            registroSub.getCampos().put(codigoCons,
                            registro.getCampos().get(codigoPersonaCons));
            registroSub.getCampos().put("ID", consecutivoComite);

            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            comitecalificacionCons,
                            registroSub.getCampos());
            cargarListaSubformulariocomitecalificacion();
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario
     * Subformulariocomitecalificacion
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubformulariocomitecalificacion(
        RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            int conteo;

            reg.getCampos().remove(nombreCompletoCons);

            reg.getCampos().put(companiaCons,
                            registro.getCampos().get(companiaCons));
            reg.getCampos().put("NUM_DCTO",
                            registro.getCampos().get(dpNumeroDocuCons));
            reg.getCampos().put(sucursalCons,
                            registro.getCampos().get(sucursalCons));
            reg.getCampos().put("FORMATO",
                            registro.getCampos().get(evFormatoCons));
            reg.getCampos().put("FECHA_DESDE",
                            registro.getCampos().get(evfechDesdeEvalCons));
            reg.getCampos().put("FECHA_HASTA",
                            registro.getCampos().get(evFechHastaEvalCons));
            reg.getCampos().put(codigoCons,
                            registro.getCampos().get(codigoPersonaCons));

            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            comitecalificacionCons, reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubformulariocomitecalificacion();
        }
    }

    /**
     * Metodo de eliminacion del formulario
     * Subformulariocomitecalificacion
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubformulariocomitecalificacion(Registro reg) {
        try {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            comitecalificacionCons, reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaSubformulariocomitecalificacion();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario
     * Subformulariocomitecalificacion
     *
     */
    public void cancelarEdicionSubformulariocomitecalificacion() {
        cargarListaSubformulariocomitecalificacion();
    }

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
        // Metodo heredado de la clase BeanBase

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>

        if (css != null) {
            cargarRegistroFormato();
            nombreBloqueado = true;

            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            diasNoEvaluados)) {
                int resultado = 365 - Integer.parseInt(registro.getCampos()
                                .get(diasNoEvaluados).toString());
                numeroDias = String.valueOf(resultado);
            }

            if ("A3".equals(registro.getCampos().get(evFormatoCons)
                            .toString())) {
                visibleSubFormulario = true;
            }
            else {
                visibleSubFormulario = false;
            }
        }
        else {
            nombreBloqueado = false;
        }

        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(companiaCons, compania);
        registro.getCampos().remove(nombreCons);
        registro.getCampos().remove("DOCUMENTO_PERSONA");
        registro.getCampos().remove(codigoPersonaCons);

        registro.getCampos().put(dpNumeroDocuCons, dpNumeroDocu);
        registro.getCampos().put(sucursalCons, sucursal);

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
        // Metodo heredado de la clase BeanBase
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // Metodo heredado de la clase BeanBase
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable checkInSatisfactoria
     * 
     * @return checkInSatisfactoria
     */

    /**
     * Retorna la variable numeroDias
     * 
     * @return numeroDias
     */
    public String getNumeroDias() {
        return numeroDias;
    }

    public boolean isCheckInSatisfactoria() {
        return checkInSatisfactoria;
    }

    public void setCheckInSatisfactoria(boolean checkInSatisfactoria) {
        this.checkInSatisfactoria = checkInSatisfactoria;
    }

    /**
     * Asigna la variable numeroDias
     * 
     * @param numeroDias
     * Variable a asignar en numeroDias
     */
    public void setNumeroDias(String numeroDias) {
        this.numeroDias = numeroDias;
    }

    /**
     * Retorna la variable tipoFormato
     * 
     * @return tipoFormato
     */
    public String getTipoFormato() {
        return tipoFormato;
    }

    /**
     * Asigna la variable tipoFormato
     * 
     * @param tipoFormato
     * Variable a asignar en tipoFormato
     */
    public void setTipoFormato(String tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    /**
     * Retorna la variable fechaInicialEv
     * 
     * @return fechaInicialEv
     */
    public String getFechaInicialEv() {
        return fechaInicialEv;
    }

    /**
     * Asigna la variable fechaInicialEv
     * 
     * @param fechaInicialEv
     * Variable a asignar en fechaInicialEv
     */
    public void setFechaInicialEv(String fechaInicialEv) {
        this.fechaInicialEv = fechaInicialEv;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaevClaseval
     * 
     * @return listaevClaseval
     */
    public List<Registro> getListaevClaseval() {
        return listaevClaseval;
    }

    /**
     * Asigna la lista listaevClaseval
     * 
     * @param listaevClaseval
     * Variable a asignar en listaevClaseval
     */
    public void setListaevClaseval(List<Registro> listaevClaseval) {
        this.listaevClaseval = listaevClaseval;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNumeroDcto
     * 
     * @return listaNumeroDcto
     */
    public RegistroDataModel getListaNumeroDcto() {
        return listaNumeroDcto;
    }

    /**
     * Asigna la lista listaNumeroDcto
     * 
     * @param listaNumeroDcto
     * Variable a asignar en listaNumeroDcto
     */
    public void setListaNumeroDcto(RegistroDataModel listaNumeroDcto) {
        this.listaNumeroDcto = listaNumeroDcto;
    }

    /**
     * Retorna la lista listaNumeroDcto
     * 
     * @return listaNumeroDcto
     */
    public RegistroDataModel getListaNumeroDctoE() {
        return listaNumeroDctoE;
    }

    /**
     * Asigna la lista listaNumeroDcto
     * 
     * @param listaNumeroDcto
     * Variable a asignar en listaNumeroDcto
     */
    public void setListaNumeroDctoE(RegistroDataModel listaNumeroDctoE) {
        this.listaNumeroDctoE = listaNumeroDctoE;
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
     * Retorna la lista listaevNumedocueval
     * 
     * @return listaevNumedocueval
     */
    public RegistroDataModel getListaevNumedocueval() {
        return listaevNumedocueval;
    }

    /**
     * Asigna la lista listaevNumedocueval
     * 
     * @param listaevNumedocueval
     * Variable a asignar en listaevNumedocueval
     */
    public void setListaevNumedocueval(RegistroDataModel listaevNumedocueval) {
        this.listaevNumedocueval = listaevNumedocueval;
    }

    /**
     * Retorna la lista listaDatosPersonales
     * 
     * @return listaDatosPersonales
     */
    public RegistroDataModel getListaDatosPersonales() {
        return listaDatosPersonales;
    }

    /**
     * Asigna la lista listaDatosPersonales
     * 
     * @param listaDatosPersonales
     * Variable a asignar en listaDatosPersonales
     */
    public void setListaDatosPersonales(
        RegistroDataModel listaDatosPersonales) {
        this.listaDatosPersonales = listaDatosPersonales;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubformulariocomitecalificacion
     * 
     * @return listaSubformulariocomitecalificacion
     */
    public List<Registro> getListaSubformulariocomitecalificacion() {
        return listaSubformulariocomitecalificacion;
    }

    /**
     * Asigna la lista listaSubformulariocomitecalificacion
     * 
     * @param listaSubformulariocomitecalificacion
     * Variable a asignar en listaSubformulariocomitecalificacion
     */
    public void setListaSubformulariocomitecalificacion(
        List<Registro> listaSubformulariocomitecalificacion) {
        this.listaSubformulariocomitecalificacion = listaSubformulariocomitecalificacion;
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

    public boolean isVisibleSubFormulario() {
        return visibleSubFormulario;
    }

    public void setVisibleSubFormulario(boolean visibleSubFormulario) {
        this.visibleSubFormulario = visibleSubFormulario;
    }

    public boolean isVisiblePestanaEjecu() {
        return visiblePestanaEjecu;
    }

    public void setVisiblePestanaEjecu(boolean visiblePestanaEjecu) {
        this.visiblePestanaEjecu = visiblePestanaEjecu;
    }

    public boolean isVisiblePestanaProfe() {
        return visiblePestanaProfe;
    }

    public void setVisiblePestanaProfe(boolean visiblePestanaProfe) {
        this.visiblePestanaProfe = visiblePestanaProfe;
    }

    public boolean isVisiblePestanaAdmin() {
        return visiblePestanaAdmin;
    }

    public void setVisiblePestanaAdmin(boolean visiblePestanaAdmin) {
        this.visiblePestanaAdmin = visiblePestanaAdmin;
    }

    public boolean isVisibleCamposEvalua() {
        return visibleCamposEvalua;
    }

    public void setVisibleCamposEvalua(boolean visibleCamposEvalua) {
        this.visibleCamposEvalua = visibleCamposEvalua;
    }

    public String getDocumentoPersona() {
        return documentoPersona;
    }

    public void setDocumentoPersona(String documentoPersona) {
        this.documentoPersona = documentoPersona;
    }

    public String getCodigoPersona() {
        return codigoPersona;
    }

    public void setCodigoPersona(String codigoPersona) {
        this.codigoPersona = codigoPersona;
    }

    public boolean isNombreBloqueado() {
        return nombreBloqueado;
    }

    public void setNombreBloqueado(boolean nombreBloqueado) {
        this.nombreBloqueado = nombreBloqueado;
    }

    private void calcularPuntajeNivelEjecutivo() {
        double resultado;

        double direccion = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "EV_NE_DIRECCION"), 0)
                                        .toString());
        double programacion = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NE_PROGRAMACION"),
                                        0).toString());
        double cooSupervision = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NE_COORDSUPERVISION"),
                                        0).toString());
        double conoFuncion = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NE_CONOFUNCIONES"),
                                        0).toString());
        double comunicacion = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NE_COMUNICACION"),
                                        0).toString());
        double actitudTrabajo = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NE_ACTITUDTRABAJO"),
                                        0).toString());
        double control = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NE_EVAL_CONTROL"),
                                        0).toString());

        resultado = direccion + programacion + cooSupervision + conoFuncion
            + comunicacion + actitudTrabajo + control;

        registro.getCampos().put(evPuntoEjeDesCons, resultado);

    }

    private void calcularPuntajeNivelProfesional() {
        double resultado;

        double calidad = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "EV_NP_CALIDADTRABAJO"),
                                                        0)
                                        .toString());
        double planeacion = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NP_PLANEACIONTRABAJO"),
                                        0).toString());
        double conocimienoFun = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NP_CONOFUNCIONES"),
                                        0).toString());
        double juicio = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NP_CAPACIDADJUICIO"),
                                        0).toString());
        double resposabilidad = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NP_RESPONSABILIDAD"),
                                        0).toString());
        double actitudTrabajo = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NP_ACTITUDTRABAJO"),
                                        0).toString());

        resultado = calidad + planeacion + conocimienoFun + juicio
            + resposabilidad + actitudTrabajo;

        registro.getCampos().put(evPuntoEjeDesCons, resultado);

    }

    private void calcularPuntajeNivelAdminOpera() {
        double resultado;

        double calidad = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "EV_NAO_CALIDADTRABAJO"),
                                                        0)
                                        .toString());
        double cantidad = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NAO_CANTIDADTRABAJO"),
                                        0).toString());
        double caliMate = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NAO_CALIDADMATERIALES"),
                                        0).toString());
        double responsabilidad = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NAO_RESPONSABILIDAD"),
                                        0).toString());
        double iniciativa = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get("EV_NAO_INICIATIVA"),
                                        0).toString());
        double rela = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NAO_RELAINTER"),
                                        0).toString());

        double colarboracion = Double
                        .parseDouble(SysmanFunciones.nvl(registro.getCampos()
                                        .get("EV_NAO_COLABORACION"),
                                        0).toString());

        resultado = calidad + cantidad + caliMate + responsabilidad + iniciativa
            + rela + colarboracion;

        registro.getCampos().put(evPuntoEjeDesCons, resultado);

    }

    private void cargarRegistroFormato() {
        if ("A1".equals(registro.getCampos().get(evFormatoCons))) {
            visiblePestanaEjecu = true;
            visiblePestanaAdmin = false;
            visiblePestanaProfe = false;
            visibleCamposEvalua = true;

        }
        if ("A2".equals(registro.getCampos().get(evFormatoCons))) {
            visiblePestanaEjecu = false;
            visiblePestanaAdmin = false;
            visiblePestanaProfe = true;
            visibleCamposEvalua = true;
        }

        if ("A3".equals(registro.getCampos().get(evFormatoCons))) {
            visibleSubFormulario = true;
            visiblePestanaEjecu = false;
            visiblePestanaAdmin = true;
            visiblePestanaProfe = false;
            visibleCamposEvalua = false;

        }
        else {
            visibleSubFormulario = false;
        }
    }

    // </SET_GET_ADICIONALES>
}
