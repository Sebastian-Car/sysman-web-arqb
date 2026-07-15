package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FrmfraudesexternosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmfraudesexternosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
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

import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para administrar los fraudes no registrados
 *
 * @author ybecerra
 * @version 1, 08/09/2016
 * 
 * @version 2, 30/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos, en el origen de grilla, de datos y en los subformulario.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */

@ManagedBean
@ViewScoped
public class FrmfraudesexternosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida que almacena el codigo del modulo de la aplicacion por la que se ingresa
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena "CONSECUTIVO", se llama en los metodos cargarListaCartaPregunta, cargarListaFraudesCarta, cambiarSubClase,oprimirbtSolicitud,
     * agregarRegistroSubCartaPregunta, agregarRegistroSubFraudesCarta,insertarAntes, EliminarAntes
     */
    private final String consecutivo;

    /**
     * Constante definida para almacenar la cadena "SP_RESPUESTA_MODELO_TIPO", se llama en los metodos cargarListaCartaPregunta,agregarResgistroSubCartaPregunta, editarRegSubCartaPregunta,
     * eliminarRegSubCartaPregunta
     */
    private final String cartaPregunta;

    /**
     * Constante definida para almacenar la cadena "SP_FRAUDES_CARTA", se llama en los metodos cargarListaFraudesCarta, agregarRegistroSubFraudesCarta, editarRegSubFraudesCarta,
     * eliminarRegSubFraudesCarta, oprimirPlantilla
     */
    private final String fraudesCarta;

    /**
     * Constante definida para almacenar la cadena "SUBCLASE", se llama en los metodos cargarListaFraudesCarta, cambiarSubClase,oprimirPlantilla llama en los metodos
     */
    private final String subClase;

    /**
     * Constante definida para almacenar la cadena "FECHA", se llama en los metodos cargarListaFraudesCarta, oprimirPlantilla,agregarRegistroSubFraudesCarta, editarRegSubFraudesCarta
     */
    private final String fec;

    /**
     * Constante definida para almacenar la cadena "CODIGORUTA", se llama en los metodos cambiarSubClase, oprimirPlantilla,insertarAntes
     */
    private final String codigoRuta;

    /**
     * Constante definida para almacenar la cadena "ESTADO" , se llama en los metodos cambiarEstado, actualizarReg, cargarRegistro
     */
    private final String estado;

    /**
     * Constante definida para almacenar la cadena "FRAUDE", se llama en los metodos oprimirPlantilla, agregarRegistroSubFraudesCarta
     */
    private final String fraude;

    /**
     * Constante definida para almacenar la cadena "CLASE", se llama en los metodos oprimirPlantilla, agregarRegistroSubCartaPregunta, agregarRegistroSubFraudesCarta
     */
    private final String clase;
    /**
     * Constante definida para almacenar la cadena "COMPANIA" , se llama en los metodos agregarRegistroSubCartaPregunta, agregarRegistroSubFraudesCarta, insertarAntes
     */
    private final String com;

    /**
     * Constante definida que almacena la cadena "PREGUNTA", se llama en los metodos cargarListaSubcartapregunta, editarRegSubsubcartapregunta
     */
    private final String pregunta;
    /**
     * Constante definida que almacena la cadena "FRM_FRAUDES_EXTERNOS" se llama en los metodos auditarModif,compararRegistros,auditarRegComparar
     */
    private final String frmFraudesExternos;

    /**
     * Constante definida que almacena la cadena "FRM_FRAUDES_CARTA_EXTERNOS", se llama en los metodos auditarModif,compararRegistros,auditarRegComparar
     */
    private final String frmFraudesCartaExternos;
    /**
     * Constante definida que almacena la cadena "PERIODO", se llama en los metodos auditarRegComparar,cargarRegistro,
     */
    private final String per;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo definido que tiene por defecto el numero "1", se llama en los metodos inicilizar, reasignarOrigenGrilla,cambiarSubClase,oprimirPlantilla, insertarAntes en
     */
    private String ciclo;

    /**
     * Atributo definido que tiene por defecto el codigo "9999991", se llama en los metodos auditarModif
     */
    private String codRuta;

    /**
     * Atributo definido para validar si los campos CONSECUTIVO y METROSCOBRAR se hacen visibles o no
     */
    private boolean conmetVisible;
    
    
    private boolean visibleEstado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de Registros de los Barrios
     */
    private List<Registro> listaBarrio;
    /**
     * Lista de Registros de los tipos
     */
    private List<Registro> listaTipo;
    /**
     * Lista de Registros de las Preguntas
     */
    private List<Registro> listaPregunta;
    /**
     * Lista de Registros de los anos de cobro
     */
    private List<Registro> listaAnoCobro;
    /**
     * Lista de Registros de los Periodos de cobro
     */
    private List<Registro> listaPeriodoCobro;
    /**
     * Lista de Registro de los Periodos
     */
    private List<Registro> listaPeriodo;
    /**
     * Lista de Registros de las subClases
     */
    private List<Registro> listaSubClase;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de Registros que contiene la tabla SP_RESPUESTA_MODELO_TIPO
     */
    private List<Registro> listaCartapregunta;
    /**
     * Lista de Registros que contiene la tabla SP_FRAUDES_CARTA
     */
    private List<Registro> listaFraudescarta;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que permite almacenar el valor del indice de la fila del subFormulario fraudesCarta que va hacer editado
     */
    private int indiceFraudescarta;
    /**
     * Variable creada para guardar el registro de la grilla antes de ser editado el registro seleccionado en el subFormulario fraudesCarta
     */
    private Registro registroCartaExterno;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subFormulario cartaPregunta
     */
    private Registro registroSubcartaPregunta;
    /**
     * Atributo de referencia para el subFormulario fraudesCarta
     */
    private Registro registroSubfraudesCarta;

    /**
     * Este atributo almacena el registro seleccionado en el subFraudesCarta
     */
    private Registro registroCarta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTresRemote;

    // </DECLARAR_ADICIONALES>
    public FrmfraudesexternosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        com = "COMPANIA";
        consecutivo = "CONSECUTIVO";
        cartaPregunta = "SP_RESPUESTA_MODELO_TIPO";
        fraudesCarta = "SP_FRAUDES_CARTA";
        subClase = "SUBCLASE";
        fec = "FECHA";
        codigoRuta = "CODIGORUTA";
        estado = "ESTADO";
        fraude = "FRAUDE";
        clase = "CLASE";
        pregunta = "PREGUNTA";
        frmFraudesExternos = "FRM_FRAUDES_EXTERNOS";
        frmFraudesCartaExternos = "FRM_FRAUDES_CARTA_EXTERNOS";
        per = "PERIODO";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMFRAUDESEXTERNOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubcartaPregunta = new Registro(
                            new HashMap<String, Object>());
            registroSubfraudesCarta = new Registro(
                            new HashMap<String, Object>());
            registroCartaExterno = new Registro(
                            new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaBarrio();
        cargarListaSubClase();
        cargarListaTipo();
        cargarListaPregunta();
        cargarListaAnoCobro();
        cargarListaPeriodoCobro();

        cargarListaPeriodo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaCartapregunta();
        cargarListaFraudescarta();

        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaCartapregunta = null;
        listaFraudescarta = null;

        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = FrmfraudesexternosControladorEnum.TABLA.getValue();
        buscarLlave();
        ciclo = "1";
        codRuta = "9999991";
        visibleEstado=false;
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos()
    {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmfraudesexternosControladorUrlEnum.URL41734.getValue());

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmfraudesexternosControladorUrlEnum.URL41735.getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmfraudesexternosControladorUrlEnum.URL41736.getValue());

        urlEliminacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmfraudesexternosControladorUrlEnum.URL41737.getValue());

        urlActualizacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmfraudesexternosControladorUrlEnum.URL41738.getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);

    }

    /**
     *
     * Carga la lista listaSubcartapregunta
     *
     * Metodo para cargar los registros del subFormulario subCartaPregunta
     */
    public void cargarListaCartapregunta()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(consecutivo));
            param.put(GeneralParameterEnum.CLASE.getName(), "32");

            listaCartapregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL14528
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            cartaPregunta));

            for (Registro registroAux : listaCartapregunta)
            {
                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param2.put(GeneralParameterEnum.CODIGO.getName(), registroAux.getCampos().get("CODIGO"));
                param2.put(GeneralParameterEnum.CLASE.getName(), "32");

                Registro rsPregunta = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmfraudesexternosControladorUrlEnum.URL14529
                                                                                .getValue())
                                                .getUrl(), param2),
                                CacheUtil.getLlaveServicio(UrlServiceCache.SYSMANIRISST,
                                                "SP_MODELO_TIPO_PREGUNTA"));

                registroAux.getCampos().put(pregunta,
                                rsPregunta.getCampos().get(pregunta));

            }
        }
        catch (SystemException | SysmanException e)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaSubfraudescarta
     *
     * Metodo para cargar los registro del subFormulario subFraudesCarta
     */
    public void cargarListaFraudescarta()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmfraudesexternosControladorEnum.PARAM3.getValue(), registro.getCampos().get(consecutivo));

            listaFraudescarta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL17385
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            fraudesCarta));

            for (Registro registroAux : listaFraudescarta)
            {
                Date fechaGeneracion = (Date) registroAux.getCampos().get(fec);

                Map<String, Object> param2 = new TreeMap<>();
                param2.put(FrmfraudesexternosControladorEnum.PARAM0.getValue(), "32");
                param2.put(GeneralParameterEnum.CODIGO.getName(), registroAux.getCampos().get(subClase));
                param2.put(GeneralParameterEnum.FECHA.getName(), fechaGeneracion);

                Registro rsNombre = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmfraudesexternosControladorUrlEnum.URL17386
                                                                                .getValue())
                                                .getUrl(), param2),
                                CacheUtil.getLlaveServicio(urlConexionCache,
                                                fraudesCarta));

                registroAux.getCampos().put("NOMBRECLASE",
                                rsNombre.getCampos().get("NOMBRE"));

            }

        }
        catch (SystemException | SysmanException e)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que permite cargar el listado de los barrios registrados en la tabla BARRIOS
     */
    public void cargarListaBarrio()
    {
        try
        {
            
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CIUDAD.getName(),
                            SessionUtil.getCompaniaIngreso().getCodigoCiudad());
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), SessionUtil
                            .getCompaniaIngreso().getCodigoDepartamento());
            param.put(FrmfraudesexternosControladorEnum.PARAM4.getValue(),
                            SessionUtil.getCompaniaIngreso().getCodigoPais());

            listaBarrio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL19175
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
     * Metodo que permite cargar el listado de los tipos de fraudes registrados en la tabla SP_FRAUDES_TIPO
     */
    public void cargarListaTipo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL19677
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
     * Metodo que permite cargar la lista de los codigos y preguntas de los fraudes
     */
    public void cargarListaPregunta()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASE.getName(), "32");
            listaPregunta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL20203
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
     * Metodo que permite cargar el listado de anos mayores o iguales al ano actual registrados en la tabla SP_PERIODO
     */
    public void cargarListaAnoCobro()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAnoCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL20754
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
     * Metodo que permite cargar el listado de los nombres de los meses registrados en la tabla SP_PERIODO dependiendo el ano
     */
    public void cargarListaPeriodoCobro()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
            listaPeriodoCobro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL21246
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
     * Metodo que permite cargar el listado de los nombres concatenado con el ano de los periodos registrados en la tabla SP_PERIODO
     */
    public void cargarListaPeriodo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL22161
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
     * Metodo que permite cargar el listado de las plantillas registradas en la tabla MODELO_PLANTILLA
     */
    public void cargarListaSubClase()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(FrmfraudesexternosControladorEnum.PARAM0.getValue(), "32");
            listaSubClase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL23529
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
     * Metodo ejecutado al seleccionar un registro del combo carta del subFormulario subFraudesCarta
     */
    public void cambiarSubClase()
    {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(consecutivo));
            param.put(FrmfraudesexternosControladorEnum.PARAM1.getValue(), registroSubfraudesCarta.getCampos().get(subClase));
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), registro.getCampos().get(codigoRuta));
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL23529
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs != null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1318"));
            registroSubfraudesCarta.getCampos().put(subClase, null);

            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al momento de cambiar el registro del combo estado del formulario fraudes
     */
    public void cambiarEstado()
    {

        if ("C".equals(registro.getCampos().get(estado)))
        {

            if ((registro.getCampos().get("DECISION") == " ")
                || (registro.getCampos().get("DECISION") == null))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1300"));
                registro.getCampos().put(estado, "A");
                return;
            }

            registro.getCampos().put("FECHACIERRE", new Date());

            agregarRegistroNuevo(false);

        }

        cargarRegistro();

    }

    // </METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al oprimir en el boton imprimir del subFormulario fraudesCarta
     */
    public void retornarFormularioPlantilla()
    {
        cargarListaFraudescarta();
    }

    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>

    /**
     * Metodo ejecutado al oprimir el boton impresa del subFormulario fraudesCarta
     */
    public void oprimirPlantilla()
    {
        Date fechaActual = new Date();
        try
        {
            Date fechaGeneracion = (Date) registroCarta.getCampos().get(fec);

            Map<String, Object> param = new TreeMap<>();
            param.put(FrmfraudesexternosControladorEnum.PARAM0.getValue(), "32");
            param.put(GeneralParameterEnum.CODIGO.getName(), registroCarta.getCampos().get(subClase));
            param.put(FrmfraudesexternosControladorEnum.PARAM1.getValue(), registroSubfraudesCarta.getCampos().get(subClase));
            param.put(FrmfraudesexternosControladorEnum.PARAM2.getValue(), fechaGeneracion);
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL23530
                                                                            .getValue())
                                            .getUrl(), param));

            Date fecha = (Date) rs.getCampos().get(fec);
            String codigoPlantilla = registroCarta.getCampos().get(subClase)
                            .toString();

            String strNombreDocumento = idioma.getString("TB_TB1349");
            strNombreDocumento = strNombreDocumento.replace("s$CodigoRuta$s",
                            (CharSequence) registro.getCampos()
                                            .get(codigoRuta));

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
                            "" + registroCarta.getCampos().get(fraude) + "");
            variablesConsultaW.put("s$ciclo$s", "'" + ciclo + "'");
            variablesConsultaW.put("s$codigoRuta$s",
                            "'" + registro.getCampos().get(codigoRuta) + "'");
            variablesConsultaW.put("s$clase$s",
                            "" + registroCarta.getCampos().get(subClase) + "");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR.getCodigo()),
                            SessionUtil.getModulo(),
                            campos,
                            valores);

            if (!(boolean) registroCarta.getCampos().get("IMPRESA"))
            {

                String criterio = "COMPANIA = ''" + compania
                    + "'' AND CLASE = ''" + registroCarta.getCampos().get(clase)
                    + "'' AND SUBCLASE = "
                    + registroCarta.getCampos().get(subClase) + "  ";

                long consecutivoA = ejbSysmanUtilRemote
                                .generarConsecutivoConValorInicial(
                                                "SP_FRAUDES_CARTA", criterio,
                                                consecutivo, "1");

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivoA);
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parametros.put(FrmfraudesexternosControladorEnum.PARAM3.getValue(), registroCarta.getCampos().get(fraude));
                parametros.put(FrmfraudesexternosControladorEnum.PARAM1.getValue(), registroCarta.getCampos().get(subClase));
                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(FrmfraudesexternosControladorUrlEnum.URL29678
                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);

            }
            cargarListaFraudescarta();
            compararCartas(registroCarta);
            // </CODIGO_DESARROLLADO>
        }
        catch (ParseException | SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado al oprimir el boton imprimir del subFormulario fraudesCarta
     *
     * @param reg
     * Registro en el cual esta ubicado el boton oprimido dentro de la grilla del subFormulario fraudesCarta
     * @param indice
     * Indice en el cual esta ubicado el boton oprimido dentro de la grilla del subFormulario fraudesCarta
     */
    public void oprimirImprime(Registro reg, int indice)
    {

        registroCarta = reg;

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Solicitud de Servicio del formulario
     */
    public void oprimirbtSolicitud()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codigoFraude", registro.getCampos().get(consecutivo)
                        .toString());
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.SOLICITUDSERVICIOS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del subFormulario cartaPregunta
     */
    public void agregarRegistroSubCartapregunta()
    {
        try
        {
            registroSubcartaPregunta.getCampos().put(com, compania);
            registroSubcartaPregunta.getCampos().put("CONSECUTIVOCLASE", registro.getCampos().get(consecutivo));
            registroSubcartaPregunta.getCampos().put(clase, "32");
            registroSubcartaPregunta.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
            registroSubcartaPregunta.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO.getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubcartaPregunta.getCampos());
            cargarListaCartapregunta();

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubcartaPregunta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo que ejecuta la actualizacion del registro seleccionado en la grilla del subFormulario cartaPregunta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubCartapregunta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove("PREGUNTA");
            reg.getCampos().remove(com);
            reg.getCampos().remove("CLASE");
            reg.getCampos().remove("CODIGO");
            reg.getCampos().remove("CONSECUTIVOCLASE");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO.getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_MODIFICADO));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaCartapregunta();
        }
    }

    /**
     * Metodo que ejecuta la eliminacion del registro seleccionado dentro de la grilla del subFormulario cartaPregunta
     *
     * @param reg
     * registro seleccionado en subFormulario
     */
    public void eliminarRegSubCartapregunta(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_RESPUESTA_MODELO_TIPO.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_ELIMINADO));
            cargarListaCartapregunta();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado en la grilla del subFormulario cartaPregunta
     */
    public void cancelarEdicionCartapregunta()
    {
        cargarListaCartapregunta();

    }

    /**
     * Metodo de insercion del subFormulario fraudesCarta
     */
    public void agregarRegistroSubFraudescarta()
    {
        try
        {
            registroSubfraudesCarta.getCampos().put(com, compania);
            registroSubfraudesCarta.getCampos().put(clase, "32");
            registroSubfraudesCarta.getCampos().put(fec, new Date());
            registroSubfraudesCarta.getCampos().put(fraude, registro.getCampos().get(consecutivo));
            registroSubfraudesCarta.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
            registroSubfraudesCarta.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_FRAUDES_CARTA.getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubfraudesCarta.getCampos());

            cargarListaFraudescarta();
            auditarModif(registroSubfraudesCarta.getCampos().get(fraude)
                            .toString(),
                            "1",
                            frmFraudesCartaExternos);

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubfraudesCarta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo que ejecuta la actualizacion del registro seleccionado en la grilla del subFormulario fraudesCarta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFraudescarta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            reg.getCampos().remove("NOMBRECLASE");
            reg.getCampos().remove(com);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_FRAUDES_CARTA.getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(),
                            reg.getLlave());
            compararRegistros(registroCartaExterno, reg,
                            frmFraudesCartaExternos);
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_MODIFICADO));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaFraudescarta();
        }
    }

    /**
     * Metodo ejecutado para la eliminacion del registro seleccionado en la grilla del subFormulario fraudesCarta
     *
     * @param reg
     * Registro seleccionado en el subFormulario
     */
    public void eliminarRegSubFraudescarta(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.SP_FRAUDES_CARTA.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            auditarModif(reg.getCampos().get(fraude).toString(), "2",
                            frmFraudesCartaExternos);

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaFraudescarta();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado en la grilla del subFormulario fraudesCarta
     */
    public void cancelarEdicionFraudescarta()
    {
        cargarListaFraudescarta();

    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que se ejecuta al momento de cambiar el estado, si el estado esta cerrado solo permitira visualizar la informacion.
     */
    public void actualizarReg()
    {

        if ("C".equals(registro.getCampos().get(estado)))
        {
            accion = "v";

        }
        else if ("A".equals(registro.getCampos().get(estado)))
        {
            if ("i".equals(accion))
            {
                accion = "i";
            }
            else
            {
                accion = "m";

            }

        }

    }

    /**
     * Metodo que permite ejecutar el procedimiento PR_AUDITARMODIF
     *
     * @param consecutivo
     * Valor del consecutivo del Fraude Externo insertado
     * @param tipoMod
     * Tipo de modificación que se esta realizando. 1 para Insercion, 2 para Eliminacion.
     * @param formulario
     * nombre del formulario a auditar
     */
    private void auditarModif(String consecutivo, String tipoMod,
        String formulario)
    {
        try
        {
            ejbServiciosPublicosTresRemote.auditarModif(compania, formulario, Integer.parseInt(tipoMod), consecutivo + "^" + compania + "^"
                + ciclo + "^" + codRuta, SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que permite comparar los registros de los campos antes de editar y los campos modificados para ejecutar el procedimiento AUDITARREGCOMPARAR
     *
     * @param regAntes
     * registro del fraude Externo o registro de la grilla del subformulario listaSubfraudescarta antes de ser modificado
     * @param regDespues
     * registro del fraude Externo o registro de la grilla del subformulario listaSubfraudescarta con los campos modificados
     * @param formulario
     * nombre del formulario a comparar
     */
    private void compararRegistros(Registro regAntes, Registro regDespues, String formulario)
    {
        StringBuilder resultado = new StringBuilder();
        int contador = 0;
        Iterator<Entry<String, Object>> it = regDespues.getCampos().entrySet()
                        .iterator();
        while (it.hasNext())
        {
            Entry<String, Object> e = it.next();
            StringBuilder campoAnt = new StringBuilder();
            StringBuilder campoNue = new StringBuilder();
            try
            {
                campoAnt.append((regAntes.getCampos()
                                .get(e.getKey()) != null)
                    && (regAntes.getCampos().get(e.getKey()) instanceof Date)
                        ? SysmanFunciones
                                        .convertirAFechaCadena(
                                                        (Date) regAntes.getCampos()
                                                                        .get(e.getKey()),
                                                        "dd/MM/yyyy HH:mm:ss")
                        : regAntes.getCampos().get(e.getKey()) == null
                            ? ""
                            : regAntes.getCampos().get(e.getKey()).toString());
                campoNue.append((e.getValue() != null)
                    && (e.getValue() instanceof Date)
                        ? SysmanFunciones.convertirAFechaCadena(
                                        (Date) e.getValue(),
                                        "dd/MM/yyyy HH:mm:ss")
                        : regDespues.getCampos().get(e.getKey()) == null
                            ? ""
                            : regDespues.getCampos().get(e.getKey()).toString());

                contador = compararCadenas(contador, campoNue, campoAnt, resultado, e);

            }
            catch (ParseException e1)
            {
                logger.error(e1.getMessage(), e1);
                JsfUtil.agregarMensajeError(e1.getMessage());
            }
        }

        if (frmFraudesExternos.equals(formulario))
        {
            auditarRegComparar(regDespues.getCampos().get(consecutivo)
                            .toString(), resultado, contador, formulario);
        }
        else
        {
            auditarRegComparar(regDespues.getCampos().get(fraude)
                            .toString(), resultado, contador, formulario);
        }

    }

    public int compararCadenas(int aux, StringBuilder campoNue, StringBuilder campoAnt,
        StringBuilder resultado, Entry<String, Object> e)
    {
        int contador = aux;
        if (!campoNue.equals(campoAnt))
        {
            campoNue.append("".equals(campoNue.toString()) ? "nulo" : campoNue);
            resultado.append(
                            e.getKey() + "," + campoAnt + "," + campoNue
                                + ";");
            contador++;
        }
        return contador;
    }

    /**
     * Metodo que ejecuta el procedimiento PR_AUDITARREGCOMPARAR
     *
     * @param consecutivo
     * Consecutivo del fraude Externo
     * @param camposMod
     * Nombre y valores de los campos modificados en la edicion
     * @param contador
     * Numero de veces que se debe recorrer la cadena camposMod
     * @param formulario
     * Nombre del formulario modificado
     */
    private void auditarRegComparar(String consecutivo, StringBuilder camposMod,
        int contador, String formulario)
    {
        if (contador != 0)
        {
            try
            {
                ejbServiciosPublicosTresRemote.auditarRegistroComparar(compania,
                                formulario,
                                consecutivo + "^" + compania + "^" + ciclo + "^" + registro.getCampos().get(codigoRuta).toString(),
                                camposMod.toString(),
                                SessionUtil.getUser().getCodigo(),
                                Integer.parseInt(ciclo),
                                codRuta,
                                Integer.parseInt(registro.getCampos().get("ANO").toString()),
                                registro.getCampos().get(per).toString(),
                                contador);
            }
            catch (NumberFormatException | SystemException ex)
            {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * Metodo para los valores del registro seleccionado en la edicion del subFormulario FraudesCarta
     *
     * @param reg
     * registro que contiene los campos almacenados del registro seleccionado
     */
    public void activarEdicionFraudescarta(Registro reg)
    {

        indiceFraudescarta = reg.getIndice();
        registroCartaExterno = new Registro(new HashMap<>(reg.getCampos()));
    }

    /**
     * Metodo ejecutado al oprimir el boton imprimir del subFormulario subFraudesCarta
     *
     * @param reg
     * Registro en el cual esta ubicado el boton oprimido dentro de la grilla, antes de ser actualizado
     */
    public void compararCartas(Registro reg)
    {
        Registro resFraude = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmfraudesexternosControladorEnum.PARAM3.getValue(), reg.getCampos().get(fraude));
        param.put(FrmfraudesexternosControladorEnum.PARAM1.getValue(), reg.getCampos().get(subClase));
        try
        {
            resFraude = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL41790
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        compararRegistros(reg, resFraude,
                        frmFraudesCartaExternos);
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        try
        {

            String parametro = ejbSysmanUtilRemote.consultarParametro(compania,
                            "CONSECUTIVO Y METROS EN FRAUDES", modulo,
                            new Date(), false);
            parametro = SysmanFunciones.nvlStr(parametro, "NO");

            if ("SI".equals(parametro))
            {
                conmetVisible = true;
            }
            else
            {
                conmetVisible = false;

            }
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>

        if ("i".equals(accion))
        {
            registro.getCampos().put(estado, "A");
            registro.getCampos().put("CUOTASDECOBRO", 1);
            if (listaPeriodo.get(0) != null)
            {
                registro.getCampos().put(per,
                                listaPeriodo.get(0).getCampos().get("MES"));
            }
            visibleEstado=true;
        }else{
            visibleEstado=false;
        }
        actualizarReg();

        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion al registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(com, compania);
        registro.getCampos().put("CICLO", ciclo);
        registro.getCampos().put(codigoRuta, "9999991");
        registro.getCampos().put("ANO", SysmanFunciones
                        .ano(new Date()));
        registro.getCampos().put("EXTERNO", "-1");

        try
        {
            long consecutivoF = ejbSysmanUtilRemote.generarSiguienteConsecutivo(
                            "SP_FRAUDES", "COMPANIA= " + compania,
                            consecutivo);
            registro.getCampos().put(consecutivo, consecutivoF);
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FrmfraudesexternosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion al registro
     *
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>

        auditarModif(registro.getCampos().get(consecutivo).toString(), "1",
                        frmFraudesExternos);
        /*
         * FR1084-DESPUES_INSERTAR Private Sub Form_AfterInsert() AuditarModif Me, 1, Me!Compania & "^" & Me!Ciclo & "^" & Me!CodigoRuta & "^" & Me!CONSECUTIVO btSolicitud.Enabled = True End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if ("m".equals(accion))
        {
            registro.getCampos().remove("PER");
            registro.getCampos().remove(com);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        if ("m".equals(accion))
        {
            Registro regAnterior = new Registro(registroIni);

            compararRegistros(regAnterior, registro, frmFraudesExternos);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "32");
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(consecutivo));
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmfraudesexternosControladorUrlEnum.URL33465
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null)
        {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1301"));
            return true;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        auditarModif(registro.getCampos().get(consecutivo).toString(), "2",
                        frmFraudesExternos);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna el objeto conmetVisible
     *
     * @return conmetVisible
     */
    public boolean isConmetVisible()
    {
        return conmetVisible;
    }

    /**
     * Asigna el objeto conmetVisible
     *
     * @param conmetVisible
     */
    public void setConmetVisible(boolean conmetVisible)
    {
        this.conmetVisible = conmetVisible;
    }
    
    

    public boolean isVisibleEstado() {
        return visibleEstado;
    }

    public void setVisibleEstado(boolean visibleEstado) {
        this.visibleEstado = visibleEstado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaBarrio
     *
     * @return listaBarrio
     */
    public List<Registro> getListaBarrio()
    {
        return listaBarrio;
    }

    /**
     * Asigna la lista listaBarrio
     *
     * @param listaBarrio
     */
    public void setListaBarrio(List<Registro> listaBarrio)
    {
        this.listaBarrio = listaBarrio;
    }

    /**
     * Retorna la lista listaTipo
     *
     * @return listaTipo
     */
    public List<Registro> getListaTipo()
    {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     *
     * @param listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaPregunta
     *
     * @return listaPregunta
     */
    public List<Registro> getListaPregunta()
    {
        return listaPregunta;
    }

    /**
     * Asigna la lista listaPregunta
     *
     * @param listaPregunta
     */
    public void setListaPregunta(List<Registro> listaPregunta)
    {
        this.listaPregunta = listaPregunta;
    }

    /**
     * Retorna la lista listaAnoCobro
     *
     * @return listaAnoCobro
     */
    public List<Registro> getListaAnoCobro()
    {
        return listaAnoCobro;
    }

    /**
     * Asigna la lista listaAnoCobro
     *
     * @param listaAnoCobro
     */
    public void setListaAnoCobro(List<Registro> listaAnoCobro)
    {
        this.listaAnoCobro = listaAnoCobro;
    }

    /**
     * Retorna la lista listaPeriodoCobro
     *
     * @return listaPeriodoCobro
     */
    public List<Registro> getListaPeriodoCobro()
    {
        return listaPeriodoCobro;
    }

    /**
     * Asigna la lista listaPeriodoCobro
     *
     * @param listaPeriodoCobro
     */
    public void setListaPeriodoCobro(List<Registro> listaPeriodoCobro)
    {
        this.listaPeriodoCobro = listaPeriodoCobro;
    }

    /**
     * Retorna la lista listaPeriodo
     *
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     *
     * @param listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaSubClase
     *
     * @return listaSubClase
     */
    public List<Registro> getListaSubClase()
    {
        return listaSubClase;
    }

    /**
     * Asigna la lista listaSubClase
     *
     * @param listaSubClase
     */
    public void setListaSubClase(List<Registro> listaSubClase)
    {
        this.listaSubClase = listaSubClase;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaCartaPregunta
     *
     * @return listaCartaPregunta
     */
    public List<Registro> getListaCartapregunta()
    {
        return listaCartapregunta;
    }

    /**
     * Asigna la lista listaCartaPregunta
     *
     * @param listaCartapregunta
     */
    public void setListaCartapregunta(
        List<Registro> listaCartapregunta)
    {
        this.listaCartapregunta = listaCartapregunta;
    }

    /**
     * Retorna la lista listaFraudescarta
     *
     * @return listaFraudescarta
     */
    public List<Registro> getListaFraudescarta()
    {
        return listaFraudescarta;
    }

    /**
     * Asigna la lista listaFraudescarta
     *
     * @param listaFraudescarta
     */
    public void setListaFraudescarta(
        List<Registro> listaFraudescarta)
    {
        this.listaFraudescarta = listaFraudescarta;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    /**
     * Retorna el objeto indiceFraudescarta
     *
     * @return indiceFraudescarta
     */
    public int getIndiceFraudescarta()
    {
        return indiceFraudescarta;
    }

    /**
     * Asigna el objeto indiceFraudescarta
     *
     * @param indiceFraudescarta
     */
    public void setIndiceFraudescarta(int indiceFraudescarta)
    {
        this.indiceFraudescarta = indiceFraudescarta;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubcartaPregunta
     *
     * @return registroSubcartaPregunta
     */
    public Registro getRegistroSubcartaPregunta()
    {
        return registroSubcartaPregunta;
    }

    /**
     * Asigna el objeto registroSubcartaPregunta
     *
     * @param registroSubcartaPregunta
     */
    public void setRegistroSubcartaPregunta(
        Registro registroSubcartaPregunta)
    {
        this.registroSubcartaPregunta = registroSubcartaPregunta;
    }

    /**
     * Retorna el objeto registroSubfraudesCarta
     *
     * @return registroSubfraudesCarta
     */
    public Registro getRegistroSubfraudesCarta()
    {
        return registroSubfraudesCarta;
    }

    /**
     * Asigna el objeto registroSubfraudesCarta
     *
     * @param registroSubfraudesCarta
     */
    public void setRegistroSubfraudesCarta(
        Registro registroSubfraudesCarta)
    {
        this.registroSubfraudesCarta = registroSubfraudesCarta;
    }

    /**
     * Retorna el objeto registroCarta
     *
     * @return registroCarta
     */
    public Registro getRegistroCarta()
    {
        return registroCarta;
    }

    /**
     * Asigna el objeto registroCarta
     *
     * @param registroCarta
     */
    public void setRegistroCarta(Registro registroCarta)
    {
        this.registroCarta = registroCarta;
    }

    // </SET_GET_ADICIONALES>
}
