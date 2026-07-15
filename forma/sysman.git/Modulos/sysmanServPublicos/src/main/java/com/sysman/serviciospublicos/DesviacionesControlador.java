/*-
 * DesviacionesControlador.java
 *
 * 1.0
 *
 * 30/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.DesviacionesControladorEnum;
import com.sysman.serviciospublicos.enums.DesviacionesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para administrar las desviaciones registradas.
 *
 * @version 1.0, 30/09/2016
 * @author ybecerra
 * 
 * @author ybecerra
 * @version 2, 15/05/2017 Revision Sonar y Refactoring
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class DesviacionesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para hacer referencia al numero de modulo
     * ingresado en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Constante definida para almacenar la cadena "DESVIACION", se
     * llama en el metodo de oprimirPlantillas y en los agregar,
     * editar y eliminar de los subFormularios desviacionesCarta y
     * desviacionesHistotoria
     */
    private final String desviacion;

    /**
     * Constante definida para almacenar la cadena "CONSECUTIVO", se
     * llama mas en los cargarLista de los subformularios, en los
     * agregar de los subformularios, en el cambiarSubClase, en los
     * metodos oprimirPlantilla, oprimirDatos y en Totales
     */
    private final String consecutivo;

    /**
     * Constante definida para almacenar la cadena "SUBCLASE", se
     * llama en el cargar lista del subDesviacionesCFarta, en el
     * cambiarSubclase, en los metodos oprimirPlantilla y oprimirDatos
     */
    private final String subClase;

    /**
     * Constante definida para almacenar la cadena ESTADO, se llama en
     * los metodos cambiarCobrarPeriodo, cambiarEstado, decision,
     * cancelarDgMetros,actualizarReg
     */
    private final String estado;

    /**
     * Constante definida para almcenar el string "PERIODOCIERRE", se
     * llama en el metodo actualizaUsuario
     */
    private final String periodoCierre;

    /**
     * Constante definida para almacenar el string "COBROPERIODO", se
     * llama en los metodos cambiarEstado, decision, usuario y
     * aceptarDgMetros
     */
    private final String cobroPeriodo;

    /**
     * Constante definida para almacenar ls cadena "CODIGORUTA", se
     * llama en los metodos cambiarSubClase, usuario, usuarioFimm,
     * actualizaUsuario, aceptarDgMetros, oprimirPlantilla,
     * oprimirDatos
     */
    private final String codigoRuta;

    /**
     * Constante definida que almacena la cadena "NOMBREPLANTILLA" ,
     * se llama en en el cargar lista del subDesviacionCarta, en los
     * metodos agregar y editar de este y en el opirmirDatos
     */
    private final String nombrePlantilla;

    /**
     * Constante definida que almacena la cadena "FECHA", se llama en
     * el cargar lista del SubDesviacionesHistoria en los metodos
     * agregar de este subFormulario y oprimirPlantilla
     */
    private final String fec;

    /**
     * Constante definida que almacena la cadena "CLASE" , se llama en
     * los metodos agregar de los subformularios DesviacionesCarta,
     * CartaPregunta, oprimirPlantilla, oprimirDatos
     */
    private final String clase;
    /**
     * Almacena el valor del codigo de ciclo del Formulario PedirCiclo
     */
    private String ciclo;
    /**
     * atributo que toma el valor de la suma de los registros de la
     * columna pendiente del subDesviacionesHistoria
     */
    private int totalPendiente;

    /**
     * Atributo que toma el valor de la suma de los registros de la
     * columna Acueducto del subDesviacionesHistoria
     */
    private String totalAcueducto;

    /**
     * Atributo que toma el valor de la suma de los registros de la
     * columna Alcantarillado del subDesviacionHistoria
     */
    private String totalAlcantarillado;

    /**
     * Atributo que toma el valor de la suma de los registros de la
     * columna Metros Micro del subDesviacionHistorica
     */
    private int totalMetrosMicro;
    /**
     * Almacena el valor ingresado en la caja de texto del dialogo
     * DgMetros
     */
    private int metros;

    /**
     * Atributo que valida si el dialogo dgMetros se hace visible o no
     */
    private boolean dialogoMetros;

    /**
     * Atributo que valida si la columna Micro del
     * subDesviacionHistorica sea visible o no
     */
    private boolean porMicroVisible;

    /**
     * Atributo que valida si la columna Manual del
     * subDesviacionHistorica sea visible o no
     */
    private boolean consumoManualVisible;

    /**
     * Atributo que controla el ancho del subDesviacionHistorica
     */
    private String anchoSub;

    /**
     * Permite hacer visible o no, la etiqueta Cobrar en periodo
     * actual: y la casilla de verificacion cobraPeriodo en el
     * formulario principal
     */
    private boolean cargarPeriodoActual;

    /**
     * Este atributo determina el valor del ancho de la etiqueta
     * Consumo del subDesviacionHistorica
     */
    private String anchoConsumo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaSubClase;
    private List<Registro> listaPregunta;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaSubdesviacionescarta;
    private List<Registro> listaSubcartapregunta;
    private List<Registro> listaSubdesviacioneshistoria;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSubSubDesviacionesCarta;
    private Registro registroSubsubCartaPregunta;
    private Registro registroSubSubDesviacionesHistoria;
    /**
     * Este atributo almacena el registro seleccionado en el
     * subDesviacionesCarta
     */
    private Registro registroCarta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicos;
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;

    // </DECLARAR_ADICIONALES>
    public DesviacionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        subClase = DesviacionesControladorEnum.PARAM1.getValue();
        estado = GeneralParameterEnum.ESTADO.getName();
        cobroPeriodo = "COBROPERIODO";
        codigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        periodoCierre = DesviacionesControladorEnum.PARAM5.getValue();
        desviacion = DesviacionesControladorEnum.PARAM4.getValue();
        nombrePlantilla = DesviacionesControladorEnum.PARAM8.getValue();
        fec = GeneralParameterEnum.FECHA.getName();
        clase = GeneralParameterEnum.CLASE.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DESVIACIONES_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                ciclo = (String) parametrosEntrada.get("ciclo");

            }
            // <INI_ADICIONAL>
            registroSubSubDesviacionesCarta = new Registro(
                            new HashMap<String, Object>());
            registroSubsubCartaPregunta = new Registro(
                            new HashMap<String, Object>());
            registroSubSubDesviacionesHistoria = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaSubClase();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPregunta();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubdesviacionescarta();
        cargarListaSubcartapregunta();
        cargarListaSubdesviacioneshistoria();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubdesviacionescarta = null;
        listaSubcartapregunta = null;
        listaSubdesviacioneshistoria = null;
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
    public void inicializar()
    {
        enumBase = GenericUrlEnum.SP_DESVIACIONES;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
    }

    /**
     * Lista que contiene los registros del SubFormulario Desviaciones
     * Carta
     */
    public void cargarListaSubdesviacionescarta()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(DesviacionesControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(consecutivo));

            listaSubdesviacionescarta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_DESVIACIONES_CARTA
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.SP_DESVIACIONES_CARTA
                                                            .getTable()));

            for (Registro registroAux : listaSubdesviacionescarta)
            {
                param.clear();
                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registroAux.getCampos().get(subClase));
                param.put(GeneralParameterEnum.FECHA.getName(),
                                registroAux.getCampos()
                                                .get(fec));

                Registro rsNombre = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                DesviacionesControladorUrlEnum.URL12137
                                                                                .getValue())
                                                .getUrl(), param));
                if (rsNombre == null)
                {
                    registroAux.getCampos().put(nombrePlantilla, " ");
                }
                else
                {
                    registroAux.getCampos().put(nombrePlantilla,
                                    rsNombre.getCampos()
                                                    .get(GeneralParameterEnum.NOMBRE
                                                                    .getName()));
                }

            }
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Lista de registro del subFormulario Carta Pregunta
     */
    public void cargarListaSubcartapregunta()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos().get(consecutivo));
            param.put(GeneralParameterEnum.CLASE.name(),
                            DesviacionesControladorEnum.PARAM2.getValue());

            listaSubcartapregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getTable()));
            for (Registro registroAux : listaSubcartapregunta)
            {
                param.clear();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CLASE.name(),
                                DesviacionesControladorEnum.PARAM2.getValue());
                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registroAux.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));
                Registro rsPregunta = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                DesviacionesControladorUrlEnum.URL15423
                                                                                .getValue())
                                                .getUrl(), param));
                registroAux.getCampos().put(
                                DesviacionesControladorEnum.PARAM3.getValue(),
                                rsPregunta.getCampos()
                                                .get(DesviacionesControladorEnum.PARAM3
                                                                .getValue()));

            }

        }
        catch (SystemException | SysmanException e)
        {
            Logger.getLogger(FrmfraudesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaSubdesviacioneshistoria()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(desviacion,
                            registro.getCampos().get(consecutivo));

            listaSubdesviacioneshistoria = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_DESVIACIONES_HISTORIA
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.SP_DESVIACIONES_HISTORIA
                                                            .getTable()));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que carga la lista de registros del combo Pregunta del
     * subFormulario cartaPregunta
     */
    public void cargarListaPregunta()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        DesviacionesControladorEnum.PARAM2.getValue());

        try
        {
            listaPregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL17756
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodoq que carga los registros del combo carta del
     * subFormulario Desviaciones Carta
     */
    public void cargarListaSubClase()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CLASE.getName(),
                        DesviacionesControladorEnum.PARAM2.getValue());
        try
        {
            listaSubClase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL18455
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo que valida que al seleccionar un registro en el combo
     * carta si ya se encuentra registrado, no lo deja seleccionar
     */
    public void cambiarSubClase()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registro.getCampos().get(consecutivo));
            param.put(subClase, registroSubSubDesviacionesCarta.getCampos()
                            .get(subClase));
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigoRuta));
            Registro rsSubClase;

            rsSubClase = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL18179
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsSubClase != null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1729"));
                registroSubSubDesviacionesCarta.getCampos().put(subClase,
                                null);
                return;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al seleccionar la casilla de verificacion
     * Cobrar en periodo actual
     */
    public void cambiarcobrarPeriodo()
    {

        if ((boolean) registro.getCampos().get(cobroPeriodo))
        {

            registro.getCampos().put(estado, "C");
            cambiarestado();
        }

    }

    /**
     * Evento que se ejecuta al cambiar el registro del combo Estado
     * del formulario
     */
    public void cambiarestado()
    {

        if ("C".equals(registro.getCampos().get(estado)))
        {
            if (validarUsuario())
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1717"));
                registro.getCampos().put(cobroPeriodo, "0");
                registro.getCampos().put(estado, "A");

                return;
            }
            if (validarDecision())
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1300"));
                return;
            }

            switch (validarUsuarioFimm())
            {
            case "consulta":
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1721"));

                registro.getCampos().put(cobroPeriodo, "0");
                registro.getCampos().put(estado, "A");
                return;
            case "parametro":
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1720"));
                registro.getCampos().put(cobroPeriodo, "0");
                return;
            default:
                break;
            }

            dialogoMetros = true; // aceptarDgMetros

        }
        else
        {
            dialogoMetros = false; // cancelarDgMetros
        }

    }

    public void cambiardecision()
    {
        // Este evento no se ejecuta, debido a que no realiza ningun
        // procedimiento al momento de cambiar o editar la decision
    }

    /**
     * Metodo que se llama en el evento cambiarEstado, valida si el
     * campo desicion del formulario viene vacio o no
     */
    public boolean validarDecision()
    {
        if ((registro.getCampos().get("DECISION") == "")
            || (registro.getCampos().get("DECISION") == null))
        {

            registro.getCampos().put(cobroPeriodo, "0");
            registro.getCampos().put(estado, "A");
            return true;
        }
        return false;
    }

    /**
     * Metodo que se llama en el evento cambiarEstado, verifica si el
     * usuario ya realizo pago
     */
    public boolean validarUsuario()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(codigoRuta,
                            registro.getCampos().get(codigoRuta));
            if ((boolean) registro.getCampos().get(cobroPeriodo))
            {

                Registro rsUsuario;

                rsUsuario = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                DesviacionesControladorUrlEnum.URL43490
                                                                                .getValue())
                                                .getUrl(), param));

                if (rsUsuario.getCampos().get("BANCOPERPROCESO") != null)
                {
                    return true;
                }

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;

    }

    /**
     * Metodo que se llama en evento cambiarEstado, valida si el
     * usuario puede cerrar la desviacion
     */
    public String validarUsuarioFimm()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(codigoRuta,
                        registro.getCampos().get(codigoRuta));
        /* Variable que almacena el resultado que retorna el metodo */
        String rta = "";
        /*
         * Variable que almacena el valor del parametro FACTURACION EN
         * SITIO
         */
        String facturacionSitio;

        try
        {
            facturacionSitio = ejbSysmanUtl.consultarParametro(compania,
                            "FACTURACION EN SITIO", modulo, new Date(), true);

            /*
             * Variable que almacena el valor del parametro MANEJA
             * DESVIACION SIGNIFICATIVA EN SITIO
             */
            String desviacionSignificativa = ejbSysmanUtl.consultarParametro(
                            compania,
                            "MANEJA DESVIACION SIGNIFICATIVA EN SITIO", modulo,
                            new Date(), true);

            if (desviacionSignificativa == null)
            {

                rta = "parametro";

            }
            else if ("SI".equals(facturacionSitio)
                && "SI".equals(desviacionSignificativa))
            {

                /*
                 * Variable que contiene la consulta para evaluar si
                 * el registro que trae es igual a P
                 */
                Registro rsUsuarioFimm = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                DesviacionesControladorUrlEnum.URL48462
                                                                                .getValue())
                                                .getUrl(), param));

                if ("P".equals(rsUsuarioFimm.getCampos().get("FIMM")))
                {

                    rta = "consulta";
                }
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    /**
     * Metodo que se llama en el envento cambiarEstado, actualiza los
     * campos ANOCIERRE,PERIODOCIERRE,FECHACIERRE,USUARIOCIERRE de la
     * tabla SP_DESVIACIONES
     */
    public void actualizarUsuario()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(codigoRuta,
                            registro.getCampos().get(codigoRuta));

            Registro rsActualiza = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL51952
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsActualiza != null)
            {
                /*
                 * Variable que almacena el valor del ano de la
                 * consulta del rsActualiza
                 */
                String ano = rsActualiza.getCampos().get("ANO").toString();
                /*
                 * Variable que almacena el valor del periodo de la
                 * consulta del rsActualiza
                 */
                String periodo = rsActualiza.getCampos().get("PERIODO")
                                .toString();
                registro.getCampos().put("ANOCIERRE", ano);
                registro.getCampos().put(periodoCierre, periodo);
                registro.getCampos().put("FECHACIERRE",
                                new Date());
                registro.getCampos().put("USUARIOCIERRE",
                                SessionUtil.getUser().getCodigo());

                registro.getCampos().put(periodoCierre,
                                ejbServiciosPublicos.asignarNombrePeriodo(
                                                compania, Integer.valueOf(ano),
                                                periodo, ""));

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Este metodo se ejecuta al momento de darle clic en le boton
     * aceptar del dialogo visualizado en el formulario
     */
    public void aceptarDgMetros()
    {
        try
        {
            actualizarUsuario();
            registro.getCampos().put("METROSCOBRADOS", metros);

            ejbServiciosPublicosOcho.actualizarMetrosDesviacion(compania,
                            Integer.valueOf(ciclo),
                            registro.getCampos().get(codigoRuta).toString(),
                            metros,
                            (boolean) registro.getCampos().get(cobroPeriodo),
                            Integer.valueOf(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.PERIODO
                                                            .getName())
                                            .toString(),
                            SessionUtil.getUser().getCodigo());
            cargarRegistro();
            dialogoMetros = false;
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException | NumberFormatException e)

        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarDgMetros()
    {

        registro.getCampos().put(estado, "A");
        cargarRegistro();
        dialogoMetros = false;

    }

    public void retornarFormularioPlantilla()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaSubdesviacionescarta();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    public void abrirSubDesviacionHistorica()
    {

        try
        {

            anchoSub = "966px";
            anchoConsumo = "110px";
            String manejaConsumo = ejbSysmanUtl.consultarParametro(compania,
                            "MANEJA CONSUMO MANUAL", modulo, new Date(), true);

            if (manejaConsumo == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1723"));
                return;
            }
            else
            {
                if ("SI".equals(manejaConsumo))
                {
                    consumoManualVisible = true;
                }
                else
                {
                    consumoManualVisible = false;
                }
            }
            /*
             * Variable que almacena el resultado que retorna la
             * funcion
             * PCK_SERVICIOS_PUBLICOS.FC_AUTORIZACION_MICROMEDICION
             */

            boolean bolMicro = ejbServiciosPublicos.autorizarMicromedicion(
                            compania,
                            SessionUtil.getCompaniaIngreso().getNit());

            if (bolMicro)
            {

                porMicroVisible = true;
            }
            else
            {
                porMicroVisible = false;

            }

            if (consumoManualVisible && porMicroVisible)
            {
                anchoSub = "1182px";
                anchoConsumo = "325px";
            }
            else if (consumoManualVisible || porMicroVisible)
            {
                anchoSub = "1073px";
                anchoConsumo = "216px";
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Metodo que se ejecuta al darle clic al boton imprimir del
     * subDesviacionesCarta
     */

    /**
     * 
     */
    public void oprimirPlantilla()
    {
        // <CODIGO_DESARROLLADO>
        Date fechaGeneracion = (Date) registroCarta.getCampos().get(fec);
        Date fechaActual = new Date();
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(DesviacionesControladorEnum.PARAM6.getValue(),
                            DesviacionesControladorEnum.PARAM2.getValue());
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroCarta.getCampos().get(subClase));
            param.put(DesviacionesControladorEnum.PARAM7.getValue(),
                            fechaGeneracion);

            // Consulta que trae el registro de la ultima fecha de la
            // plantilla selecciona
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL51232
                                                                            .getValue())
                                            .getUrl(), param));

            Date fecha = (Date) rs.getCampos().get(fec);
            String codigoPlantilla = registroCarta.getCampos().get(subClase)
                            .toString();
            String strNombreDocumento = idioma.getString("TB_TB1728");
            strNombreDocumento = strNombreDocumento.replace("s$codigoRuta$s",
                            registro.getCampos()
                                            .get(codigoRuta).toString());

            strNombreDocumento = strNombreDocumento + " " + SysmanFunciones
                            .convertirAFechaCadena(fechaActual).replace("/", "")
                + "_"
                + SysmanFunciones.convertirAHoraCadena(fechaActual).replace(":",
                                "");

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = codigoPlantilla;
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = strNombreDocumento;
            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$consecutivo$s",
                            "" + registroCarta.getCampos().get(desviacion)
                                + "");
            variablesConsultaW.put("s$ciclo$s", "'" + ciclo + "'");
            variablesConsultaW.put("s$codigoRuta$s",
                            "" + registro.getCampos().get(codigoRuta) + "");
            variablesConsultaW.put("s$clase$s",
                            "" +
                                registroCarta.getCampos().get(subClase)
                                + "");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR.getCodigo()), SessionUtil.getModulo(),
                            campos,
                            valores);

            if (!(boolean) registroCarta.getCampos().get("IMPRESA"))
            {

                String criterio = " COMPANIA = ''" + compania
                    + "'' AND CLASE = ''"
                    + registroCarta.getCampos().get(clase)
                    + "'' AND SUBCLASE = "
                    + registroCarta.getCampos().get(subClase) + " ";

                long consecutivoD = ejbSysmanUtl
                                .generarConsecutivoConValorInicial(
                                                "SP_DESVIACIONES_CARTA",
                                                criterio,
                                                GeneralParameterEnum.CONSECUTIVO
                                                                .getName(),
                                                "1");

                param.clear();
                param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivoD);
                param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(desviacion,
                                registroCarta.getCampos().get(desviacion));
                param.put(subClase, registroCarta.getCampos().get(subClase));

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                DesviacionesControladorUrlEnum.URL30992
                                                                .getValue());

                Parameter parameter = new Parameter();
                parameter.setFields(param);
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
            }

        }
        catch (ParseException | SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprime(Registro reg, int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        registroCarta = reg;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirDatos(Registro reg, int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = new String[6];
        String[] valores = new String[6];
        campos[0] = "claseCarta";
        campos[1] = "subClase";
        campos[2] = "consecutivo";
        campos[3] = "codigoRuta";
        campos[4] = "suscriptor";
        campos[5] = "nombreClase";

        // Validacion con el codigo 31 valores[0] =
        // reg.getCampos().get(clase).toString();
        valores[0] = "D";
        valores[1] = reg.getCampos().get(subClase).toString();
        valores[2] = registro.getCampos().get(consecutivo).toString();
        valores[3] = registro.getCampos().get(codigoRuta).toString();
        valores[4] = registro.getCampos().get("SUSCRIPTOR").toString();
        valores[5] = reg.getCampos().get(nombrePlantilla).toString();

        SessionUtil.cargarModalDatosFlash(String.valueOf(GeneralCodigoFormaEnum.CARTAPREGUNTAPROPS_CONTROLADOR.getCodigo()), modulo,
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubdesviacionescarta()
    {
        try
        {
            registroSubSubDesviacionesCarta.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubDesviacionesCarta.getCampos().put(desviacion,
                            registro.getCampos().get(consecutivo));

            registroSubSubDesviacionesCarta.getCampos().put(
                            GeneralParameterEnum.CLASE.getName(),
                            DesviacionesControladorEnum.PARAM2.getValue());

            registroSubSubDesviacionesCarta.getCampos().put(
                            GeneralParameterEnum.FECHA.getName(),
                            new Date());
            registroSubSubDesviacionesCarta.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubDesviacionesCarta.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubDesviacionesCarta.getCampos()
                            .remove(nombrePlantilla);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_DESVIACIONES_CARTA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubDesviacionesCarta.getCampos());
            cargarListaSubdesviacionescarta();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubSubDesviacionesCarta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubdesviacionescarta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            reg.getCampos().remove(nombrePlantilla);
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(desviacion);
            reg.getCampos().remove(GeneralParameterEnum.CLASE.getName());
            reg.getCampos().remove(subClase);
            reg.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
            reg.getCampos().remove(GeneralParameterEnum.FECHA.getName());
            reg.getCampos().remove(nombrePlantilla);
            reg.getCampos().remove("FECHAIMPRESION");
            reg.getCampos().remove("IMPRESA");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_DESVIACIONES_CARTA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubdesviacionescarta();
        }
    }

    public void eliminarRegSubSubdesviacionescarta(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_DESVIACIONES_CARTA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaSubdesviacionescarta();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubdesviacionescarta()
    {
        cargarListaSubdesviacionescarta();
        cargarListaSubcartapregunta();
        cargarListaSubdesviacioneshistoria();
    }

    public void agregarRegistroSubSubcartapregunta()
    {
        try
        {

            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubsubCartaPregunta.getCampos().put(clase,
                            DesviacionesControladorEnum.PARAM2.getValue());
            registroSubsubCartaPregunta.getCampos().put("CONSECUTIVOCLASE",
                            registro.getCampos().get(consecutivo));
            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubsubCartaPregunta.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubsubCartaPregunta.getCampos());
            cargarListaSubcartapregunta();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubsubCartaPregunta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubcartapregunta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CLASE.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            reg.getCampos().remove("CONSECUTIVOCLASE");
            reg.getCampos().remove("PREGUNTA");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubcartapregunta();
        }
    }

    public void eliminarRegSubSubcartapregunta(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaSubcartapregunta();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubcartapregunta()
    {
        cargarListaSubcartapregunta();
        cargarListaSubdesviacioneshistoria();
    }

    public void agregarRegistroSubSubdesviacioneshistoria()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void editarRegSubSubdesviacioneshistoria(RowEditEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void eliminarRegSubSubdesviacioneshistoria(Registro reg)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEdicionSubdesviacioneshistoria()
    {
        cargarListaSubdesviacioneshistoria();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Metodo para calcular los totales del subFormulario,
     * subDesviacionesHistoria, se visualizan en la parte inferior del
     * subformulario
     */
    public void calculartotalesHistoria()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(desviacion, registro.getCampos().get(consecutivo));

            Registro rsTotal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesviacionesControladorUrlEnum.URL50568
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsTotal == null)
            {
                totalPendiente = 0;
                totalAcueducto = "0";
                totalAlcantarillado = "0";
                totalMetrosMicro = 0;
            }
            else
            {

                totalPendiente = Integer.valueOf(
                                rsTotal.getCampos().get("PENDIENTE")
                                                .toString());
                totalAcueducto = rsTotal.getCampos().get("PENDIENTEACU")
                                .toString();
                totalAlcantarillado = rsTotal.getCampos().get("PENDIENTEALC")
                                .toString();
                totalMetrosMicro = Integer.valueOf(
                                rsTotal.getCampos().get("METROSMICRO")
                                                .toString());
            }
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    /**
     * Metodo que se ejecuta al momento de cambiar el estado, si el
     * estado esta cerrado solo permitira visualizar la informacion.
     */
    public void actualizarReg()
    {

        if ("C".equals(registro.getCampos().get(estado)))
        {
            accion = "v";
            agregarRegistroNuevo(false);

        }
        else if ("A".equals(registro.getCampos().get(estado)))
        {
            accion = "m";
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        totalAcueducto = "0";
        totalAlcantarillado = "0";
        try
        {
            /*
             * Variable que almacena el valor del parametro
             * FACTURACION EN SITIO
             */
            String facturacionSitio = ejbSysmanUtl.consultarParametro(compania,
                            "FACTURACION EN SITIO", modulo, new Date(), true);

            if (facturacionSitio == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1693"));
                return;
            }

            /*
             * Variable que almacena el valor del parametro COBRAR
             * DESVIACION EN PERIODO ACTUAL
             */
            String cobrarDesviacion = ejbSysmanUtl.consultarParametro(compania,
                            "COBRAR DESVIACION EN PERIODO ACTUAL", modulo,
                            new Date(), true);

            if (cobrarDesviacion == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1694"));
                return;
            }

            if ("SI".equals(facturacionSitio)
                || "SI".equals(cobrarDesviacion))
            {

                cargarPeriodoActual = true;

            }
            else
            {
                cargarPeriodoActual = false;
            }

            abrirSubDesviacionHistorica();

        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    public List<Registro> getListaPregunta()
    {
        return listaPregunta;
    }

    public int getTotalPendiente()
    {
        return totalPendiente;
    }

    public String getTotalAcueducto()
    {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs)
                        .format(Double.parseDouble(totalAcueducto));
    }

    public void setTotalAcueducto(String totalAcueducto)
    {
        this.totalAcueducto = totalAcueducto;
    }

    public String getTotalAlcantarillado()
    {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs)
                        .format(Double.parseDouble(totalAlcantarillado));
    }

    public void setTotalAlcantarillado(String totalAlcantarillado)
    {
        this.totalAlcantarillado = totalAlcantarillado;
    }

    public int getTotalMetrosMicro()
    {
        return totalMetrosMicro;
    }

    public void setTotalMetrosMicro(int totalMetrosMicro)
    {
        this.totalMetrosMicro = totalMetrosMicro;
    }

    public String getAnchoConsumo()
    {
        return anchoConsumo;
    }

    public void setAnchoConsumo(String anchoConsumo)
    {
        this.anchoConsumo = anchoConsumo;
    }

    public boolean isCargarPeriodoActual()
    {
        return cargarPeriodoActual;
    }

    public void setCargarPeriodoActual(boolean cargarPeriodoActual)
    {
        this.cargarPeriodoActual = cargarPeriodoActual;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaSubClase()
    {
        return listaSubClase;
    }

    public void setListaSubClase(List<Registro> listaSubClase)
    {
        this.listaSubClase = listaSubClase;
    }

    public void setTotalPendiente(int totalPendiente)
    {
        this.totalPendiente = totalPendiente;
    }

    public void setListaPregunta(List<Registro> listaPregunta)
    {
        this.listaPregunta = listaPregunta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public int getMetros()
    {
        return metros;
    }

    public void setMetros(int metros)
    {
        this.metros = metros;
    }

    public boolean isDialogoMetros()
    {
        return dialogoMetros;
    }

    public void setDialogoMetros(boolean dialogoMetros)
    {
        this.dialogoMetros = dialogoMetros;
    }

    public boolean isPorMicroVisible()
    {
        return porMicroVisible;
    }

    public void setPorMicroVisible(boolean porMicroVisible)
    {
        this.porMicroVisible = porMicroVisible;
    }

    public boolean isConsumoManualVisible()
    {
        return consumoManualVisible;
    }

    public void setConsumoManualVisible(boolean consumoManualVisible)
    {
        this.consumoManualVisible = consumoManualVisible;
    }

    public String getAnchoSub()
    {
        return anchoSub;
    }

    public void setAnchoSub(String anchoSub)
    {
        this.anchoSub = anchoSub;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubdesviacionescarta()
    {
        return listaSubdesviacionescarta;
    }

    public void setListaSubdesviacionescarta(
        List<Registro> listaSubdesviacionescarta)
    {
        this.listaSubdesviacionescarta = listaSubdesviacionescarta;
    }

    public List<Registro> getListaSubcartapregunta()
    {
        return listaSubcartapregunta;
    }

    public void setListaSubcartapregunta(List<Registro> listaSubcartapregunta)
    {
        this.listaSubcartapregunta = listaSubcartapregunta;
    }

    public List<Registro> getListaSubdesviacioneshistoria()
    {
        return listaSubdesviacioneshistoria;
    }

    public void setListaSubdesviacioneshistoria(
        List<Registro> listaSubdesviacioneshistoria)
    {
        this.listaSubdesviacioneshistoria = listaSubdesviacioneshistoria;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSubSubDesviacionesCarta()
    {
        return registroSubSubDesviacionesCarta;
    }

    public void setRegistroSubSubDesviacionesCarta(
        Registro registroSubSubDesviacionesCarta)
    {
        this.registroSubSubDesviacionesCarta = registroSubSubDesviacionesCarta;
    }

    public Registro getRegistroSubsubCartaPregunta()
    {
        return registroSubsubCartaPregunta;
    }

    public void setRegistroSubsubCartaPregunta(
        Registro registroSubsubCartaPregunta)
    {
        this.registroSubsubCartaPregunta = registroSubsubCartaPregunta;
    }

    public Registro getRegistroSubSubDesviacionesHistoria()
    {
        return registroSubSubDesviacionesHistoria;
    }

    public void setRegistroSubSubDesviacionesHistoria(
        Registro registroSubSubDesviacionesHistoria)
    {
        this.registroSubSubDesviacionesHistoria = registroSubSubDesviacionesHistoria;
    }

    public Registro getRegistroCarta()
    {
        return registroCarta;
    }

    public void setRegistroCarta(Registro registroCarta)
    {
        this.registroCarta = registroCarta;
    }

    // </SET_GET_ADICIONALES>

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        actualizarReg();
        precargarRegistro();
        calculartotalesHistoria();

        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarAntes()
    {

        return false;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1127-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * AuditarModif Me, 1, Me!Compania & "^" & Me!Ciclo & "^" &
         * Me!CodigoRuta End Sub
         */
        return false;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("VENCIMIENTO");
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove("PER");
        registro.getCampos().remove("ANOCIERRE");
        registro.getCampos().remove("PERIODOCIERRE");
        registro.getCampos().remove("SUSCRIPTOR");
        registro.getCampos().remove("LECTURAANTERIOR");
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
        registro.getCampos().remove("LECTURAINICIAL");
        registro.getCampos().remove("USUARIOCIERRE");
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove("FECHACIERRE");
        registro.getCampos().remove("PROMEDIO");
        registro.getCampos().remove("FECHAAFORO");

        // </CODIGO_DESARROLLADO>
        return true;

    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1127-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate() If
         * Not BolNuevo Then AudRegistroComparar Me, Me!Compania & "^"
         * & Me!Ciclo & "^" & Me!CodigoRuta, Me!Compania, Me!Ciclo,
         * Me!CodigoRuta, Me!ANO, Me!PERIODO, False End If BolNuevo =
         * False AudRegistroCargar Me End Sub
         */

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1127-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As
         * Integer) AuditarModif Me, 2, Me!Compania & "^" & Me!Ciclo &
         * "^" & Me!CodigoRuta End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }
}
