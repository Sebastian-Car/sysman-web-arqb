package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.LecturasControladorEnum;
import com.sysman.serviciospublicos.enums.LecturasControladorUrlEnum;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador para el formulario Lectura que permite registrar las
 * lecturas tomadas por los aforadores.
 *
 * @version 1.0, 14/09/2016
 * @author jrodriguezr
 * 
 * @version 2.0, 06/06/2017 Proceso de refactoring.
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class LecturasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante para el campo COMPANIA
     */
    private final String cCompania;
    /**
     * Constante para el campo CICLO
     */
    private final String cCiclo;
    /**
     * Constante para el campo PERIODO
     */
    private final String cPeriodo;
    /**
     * Constante para el campo AFORADOR
     */
    private final String cAforador;
    /**
     * Constante para el campo FECHALECTURAAFORO
     */
    private final String cFechaLecturaAforo;
    /**
     * Constante para la tabla SP_USUARIO_PROBLEMA
     */
    private final String cSpUsuarioProblema;
    /**
     * Constante para el campo CODIGO
     */
    private final String cCodigo;
    /**
     * Constante para el campo CODIGORUTA
     */
    private final String cCodigoRuta;
    /**
     * Constante para el campo LECTURAAFORO
     */
    private final String cLecturaAforo;
    /**
     * Constante para el campo LECTURA
     */
    private final String cLectura;
    /**
     * Constante para el campo NOMBRE
     */
    private final String cNombre;
    /**
     * Constante para el campo SOLUCION
     */
    private final String cSolucion;
    /**
     * Constante para el campo CLASE
     */
    private final String cClase;
    /**
     * Indice subformulario de "Registro de Problemas" que se
     * actualiza cada vez que se activa un registro y toma el valor
     * del indice del registro que se activo.
     */
    private int indiceUsuarioproblemaprueba;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Usuario seleccionado en el combo <b>C&oacute;digo Inicial</b>
     */
    private String codigoInicial;
    /**
     * Anio asociado al ciclo seleccionado.
     */
    private String anio;
    /**
     * Periodos asociado al ciclo seleccionado.
     */
    private String periodo;
    /**
     * Nombre del aforador seleccionado.
     */
    private String nombreAforador;
    /**
     * Nombre del usuario seleccionado.
     */
    private String nombreCodigoIni;
    /**
     * Permite hacer visible/oculto el boton cambiar.
     */
    private boolean visibleBotonCambiar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de problemas para su registro en el subformulario.
     */
    private RegistroDataModelImpl listaProblema;
    /**
     * Listado de problemas para su registro en el subformulario.
     */
    private RegistroDataModelImpl listaProblemaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Listado de aforadores.
     */
    private RegistroDataModelImpl listaAforador;
    /**
     * Listado de usuarios.
     */
    private RegistroDataModelImpl listaCodigoInicialRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de problemas registrados en el subformulario.
     */
    private List<Registro> listaUsuarioproblemaprueba;
    /**
     * Listado hist&oacute;rico de los problemas de aforo del periodo
     * anterior.
     */
    private List<Registro> listaUsuarioproblemasaforo;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     * UsuarioProblemaPrueba
     */
    private Registro registroSubUsuarioProblemaPrueba;
    /**
     * Atributo de referencia para el subformulario
     * UsuarioProblemasaforo
     */
    private Registro registroSubUsuarioProblemasaforo;
    private String ciclo;
    private boolean codigoRutaVisible;
    private boolean txtUsuarioVisible;
    private boolean lecturaAnteriorVisible;
    private boolean lecturaaforoVisible;
    private boolean cambiarVisible;
    private boolean chkDeshabitadoVisible;
    private boolean chkChapetasVisible;
    private boolean chkFijosVisible;
    private boolean usuarioProblemaPruebaVisible;
    private boolean fechaEnabled;
    private boolean anoEnabled;
    private boolean cicloEnabled;
    private boolean periodoEnabled;
    private boolean aforadorEnabled;
    private boolean txtfechaEnabled;
    private boolean codigoinicialrutaEnabled;
    private boolean problemaAforoVisible;
    private boolean aceptarVisible;

    private String siguiente;
    private String perSigte;
    private String anioSigte;
    private String lecturaActual;
    private String codigoRuta;
    private String nombreProblema;
    private String solucionProblema;
    /**
     * Clase de problema AFR para filtrar el listado de problemas a
     * registrar.
     */
    private static final String CLASE_PROBLEMA_AFORO = "AFR";
    /**
     * Implementacion del EJB de SysmanUtil para hacer llamado a las
     * funciones que se invocan en el bean y se encuentran almacenadas
     * en el paqueta PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * C&oacute;digo del problema asociado al registro seleccionado
     * del subformulario de "Registro de Problemas".
     */
    private String idProblemaActual;
    /**
     * Implementacion del EJB de SysmanUtil para hacer llamado a las
     * funciones que se invocan en el bean y se encuentran almacenadas
     * en el paqueta PCK_SERVICIOS_PUBLICOS.
     */
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicos;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de LecturasControlador
     */
    public LecturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cCiclo = GeneralParameterEnum.CICLO.getName();
        cPeriodo = GeneralParameterEnum.PERIODO.getName();
        cAforador = GeneralParameterEnum.AFORADOR.getName();
        cFechaLecturaAforo = LecturasControladorEnum.PARAM4.getValue();
        cSpUsuarioProblema = LecturasControladorEnum.PARAM5.getValue();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();
        cLecturaAforo = LecturasControladorEnum.PARAM6.getValue();
        cLectura = LecturasControladorEnum.PARAM7.getValue();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cSolucion = LecturasControladorEnum.PARAM9.getValue();
        cClase = GeneralParameterEnum.CLASE.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.LECTURAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            ciclo = extraerString(parametrosEntrada.get(cCiclo.toLowerCase()));
            periodo = extraerString(
                            parametrosEntrada.get(cPeriodo.toLowerCase()));
            anio = extraerString(parametrosEntrada.get("anio"));
            registroSubUsuarioProblemaPrueba = new Registro(
                            new HashMap<String, Object>());
            registroSubUsuarioProblemasaforo = new Registro(
                            new HashMap<String, Object>());
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
        cargarListaProblema();
        cargarListaProblemaE();
        cargarListaAforador();
        cargarListaCodigoInicialRuta();
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
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaUsuarioproblemaprueba = null;
        listaUsuarioproblemasaforo = null;
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
        enumBase = GenericUrlEnum.SP_USUARIO;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();

        Map<String, Object> key = new HashMap<>();
        key.put("KEY_" + cCompania, compania);
        key.put("KEY_" + cCiclo, ciclo);
        key.put("KEY_" + cCodigoRuta, traerRutaInicial());
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        LecturasControladorUrlEnum.URL44488.getValue());
        Registro regSpUsuarios = null;
        try {
            regSpUsuarios = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), key));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        /*
         * Verifica si hay registros con los datos seleccionados para
         * cargar el primer registro.
         */
        if (regSpUsuarios == null) {
            SessionUtil.agregarMensajeErrorMenu(idioma.getString("TB_TB3204"));
            SessionUtil.redireccionarMenu();
            return;
        }
        else {
            cargarRegistro(key, ACCION_MODIFICAR);
        }
        cargarListaAforador();
        cargarListaCodigoInicialRuta();
        cargarListaProblema();
        cargarListaProblemaE();
        nombreAforador = traerNombreAforador();
        registro.getCampos().put(cFechaLecturaAforo, new Date());
        prepararAnoPeriodo();
        aceptarVisible = true;
    }

    /**
     * Trae el primer c&oacute;digo de ruta de la tabla SP_USUARIOS.
     * 
     * @return c&oacute;digo de ruta inicial.
     */
    private String traerRutaInicial() {
        String rutaInicial = null;
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        LecturasControladorUrlEnum.URL365115.getValue());
        Map<String, Object> param = new HashMap<>();
        param.put(cCompania, compania);
        param.put(cCiclo, ciclo);
        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
            if (reg != null) {
                rutaInicial = extraerString(reg.getCampos().get(cCodigoRuta));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rutaInicial;
    }

    /**
     * Ejecuta la funcion prepararAnoPeriodo con el fin de traer el
     * a&ntilde;o y el periodo siguiente.
     */
    private void prepararAnoPeriodo() {
        String frecuencia = null;
        int ano = Integer.parseInt(anio);
        String retornoAno = "0";
        String retornoPeriodo = "1";
        try {
            anioSigte = ejbServiciosPublicos.prepararAnoPeriodoSiguiente(
                            compania, ano, periodo, retornoAno, frecuencia);
            perSigte = ejbServiciosPublicos.prepararAnoPeriodoSiguiente(
                            compania, ano, periodo, retornoPeriodo, frecuencia);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Trae el nombre del aforador seleccionado.
     * 
     * @return nombre aforador
     */
    private String traerNombreAforador() {
        String nombre = null;
        Map<String, Object> params = new HashMap<>();
        params.put("CODIGO", registro.getCampos().get(cAforador));
        try {
            nombre = extraerString(listaAforador.getRegistroUnico(params)
                            .getCampos().get(cNombre));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return nombre;
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario.
     */
    @Override
    public void asignarOrigenDatos() {
        String urlEnumId = LecturasControladorUrlEnum.URL44488.getValue();
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        urlEnumId = LecturasControladorUrlEnum.URL44688.getValue();
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
    }

    /**
     * Carga la lista listaUsuarioproblemaprueba.
     */
    public void cargarListaUsuarioproblemaprueba() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        registro.getCampos().get(cPeriodo));
        param.put(GeneralParameterEnum.CICLO.getName(),
                        registro.getCampos().get(cCiclo));
        param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
        param.put(GeneralParameterEnum.CLASE.getName(), CLASE_PROBLEMA_AFORO);
        try {
            String urlEnumId = LecturasControladorUrlEnum.URL43965.getValue();
            String url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
            List<Parameter> parameters = requestManager.getList(url, param);
            String[] llaveRegistro = CacheUtil.getLlaveServicio(
                            urlConexionCache, cSpUsuarioProblema);
            listaUsuarioproblemaprueba = RegistroConverter
                            .toListRegistro(parameters, llaveRegistro);
        }
        catch (SystemException | SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Carga la lista listaUsuarioproblemasaforo
     */
    public void cargarListaUsuarioProblemasaforo() {
        String nombreParametro = "CARGAR PROBLEMA AFORO PERIODO ANTERIOR";
        String cargarProblema = getParametro(nombreParametro, null);
        if ((cargarProblema != null) && "SI".equals(cargarProblema)) {
            try {
                int ano = Integer.parseInt(anio);
                String retornoAno = "0";
                String retornoPeriodo = "1";
                String frecuencia = null;
                String perAnt = ejbServiciosPublicos.prepararAnoPeriodoAnterior(
                                compania, ano, periodo, retornoPeriodo,
                                frecuencia);
                String anoAnt = ejbServiciosPublicos.prepararAnoPeriodoAnterior(
                                compania, ano, periodo, retornoAno, frecuencia);
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(),
                                registro.getCampos().get(cCiclo));
                param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                codigoRuta);
                param.put(GeneralParameterEnum.PERIODO.getName(), perAnt);
                param.put(GeneralParameterEnum.ANO.getName(), anoAnt);
                param.put(GeneralParameterEnum.CLASE.getName(),
                                CLASE_PROBLEMA_AFORO);
                String urlEnumId = LecturasControladorUrlEnum.URL53967
                                .getValue();
                String url = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
                List<Parameter> parameters = requestManager.getList(url, param);
                String[] llaveRegistro = CacheUtil.getLlaveServicio(
                                urlConexionCache, cSpUsuarioProblema);
                listaUsuarioproblemasaforo = RegistroConverter
                                .toListRegistro(parameters, llaveRegistro);
            }
            catch (SystemException | SysmanException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaProblema del subformulario de Registro de
     * Problemas.
     */
    public void cargarListaProblema() {
        String urlEnumId = LecturasControladorUrlEnum.URL52461.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LecturasControladorEnum.PARAM0.getValue(),
                        CLASE_PROBLEMA_AFORO);
        listaProblema = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista listaProblema del subformulario de Registro de
     * Problemas.
     */
    public void cargarListaProblemaE() {
        String urlEnumId = LecturasControladorUrlEnum.URL52461.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LecturasControladorEnum.PARAM0.getValue(), "AFR");
        listaProblemaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista listaAforador
     */
    public void cargarListaAforador() {
        String urlEnumId = LecturasControladorUrlEnum.URL54263.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista listaCodigoInicialRuta
     */
    public void cargarListaCodigoInicialRuta() {
        String urlEnumId = LecturasControladorUrlEnum.URL54463.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(GeneralParameterEnum.ESTADO.getName(), "R");
        listaCodigoInicialRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control LecturaAforo
     */
    public void cambiarLecturaAforo() {
        // <CODIGO_DESARROLLADO>
        String msg = msg();
        if (msg != null) {
            JsfUtil.agregarMensajeAlerta(msg);
        }
        // </CODIGO_DESARROLLADO>
    }

    private String msg() {
        String msg = null;
        String lecturaAnterior = extraerString(
                        registro.getCampos().get(cLectura));
        String lecturaAforo = extraerString(
                        registro.getCampos().get(cLecturaAforo));
        if ("0".equals(lecturaAforo) && !"0".equals(lecturaAnterior)) {
            msg = idioma.getString("TB_TB1569");
        }
        else if (SysmanFunciones.redondear(
                        Math.log10(Double
                                        .parseDouble(extraerString(
                                                        registro.getCampos()
                                                                        .get(cLecturaAforo)))
                            + 0.5),
                        0) > Double.parseDouble(registro.getCampos()
                                        .get("NUMERODIGITOS").toString())) {
            msg = idioma.getString("TB_TB1570");
        }
        else if ("1".equals(registro.getCampos().get("USO"))
            && ((Double.parseDouble(extraerString(
                            registro.getCampos().get(cLecturaAforo)))
                - Double.parseDouble(registro.getCampos().get(cLectura)
                                .toString())) > 200)) {
            msg = idioma.getString("TB_TB1571");
        }
        else if (!"1".equals(registro.getCampos().get("USO"))
            && ((Double.parseDouble(extraerString(
                            registro.getCampos().get(cLecturaAforo)))
                - Double.parseDouble(extraerString(
                                registro.getCampos().get(cLectura)))) > 500)) {
            msg = idioma.getString("TB_TB1572");
        }
        else if (Double.parseDouble(extraerString(
                        registro.getCampos().get(cLecturaAforo))) < Double
                                        .parseDouble(registro.getCampos()
                                                        .get(cLectura)
                                                        .toString())) {
            msg = idioma.getString("TB_TB1573");
        }
        else if ((Double.parseDouble(
                        extraerString(registro.getCampos().get(cLecturaAforo)))
            - Double.parseDouble(extraerString(
                            registro.getCampos().get(cLectura)))) > 300) {
            msg = idioma.getString("TB_TB1574");
        }
        return msg;
    }

    /**
     * Metodo ejecutado al cambiar el control Problema en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProblemaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaUsuarioproblemaprueba.get(rowNum).getCampos().put("PROBLEMA",
                        auxiliar);
        listaUsuarioproblemaprueba.get(rowNum).getCampos().put(cNombre,
                        nombreProblema);
        listaUsuarioproblemaprueba.get(rowNum).getCampos().put(cSolucion,
                        solucionProblema);
        listaUsuarioproblemaprueba.get(rowNum).getCampos().put(cClase,
                        CLASE_PROBLEMA_AFORO);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblema
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblema(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubUsuarioProblemaPrueba.getCampos().put("PROBLEMA",
                        registroAux.getCampos().get(cCodigo));
        registroSubUsuarioProblemaPrueba.getCampos().put(cNombre,
                        registroAux.getCampos().get(cNombre));
        registroSubUsuarioProblemaPrueba.getCampos().put(cSolucion,
                        registroAux.getCampos().get(cSolucion));
        registroSubUsuarioProblemaPrueba.getCampos().put(cClase,
                        CLASE_PROBLEMA_AFORO);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProblema
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProblemaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos().get(cCodigo));
        nombreProblema = extraerString(registroAux.getCampos().get(cNombre));
        solucionProblema = extraerString(
                        registroAux.getCampos().get(cSolucion));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAforador
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAforador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cAforador,
                        registroAux.getCampos().get(cCodigo));
        nombreAforador = extraerString(
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicialRuta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicialRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = extraerString(registroAux.getCampos().get(cCodigoRuta));
        nombreCodigoIni = extraerString(registroAux.getCampos().get(cNombre));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar en la vista.
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        String aforador = extraerString(registro.getCampos().get(cAforador));
        lecturaActual = extraerString(registro.getCampos().get(cLecturaAforo));
        Date fecha = extraerFecha(registro.getCampos().get(cFechaLecturaAforo));
        if (fecha != null) {
            if (aforador == null) {
                return;
            }
            if (!SysmanFunciones.validarVariableVacio(codigoInicial)) {
                activarDetalle(1, true, 1);
                cambiarVisible = true;
                aceptarVisible = false;
                activarDetalle(2, false, 2);
                txtfechaEnabled = true;
                String nombreParametro = "ACTIVA DESHABITADOS DESDE PROBLEMAS AFORO";
                String activaCheck = getParametro(nombreParametro, "NO");
                chkDeshabitadoVisible = "SI".equals(activaCheck) ? true : false;
            }
            codigoRuta = codigoInicial;

            // Carga de los datos de la ruta seleccionada.
            Map<String, Object> key = new HashMap<>();
            key.put("KEY_" + cCompania, compania);
            key.put("KEY_" + cCiclo, ciclo);
            key.put("KEY_" + cCodigoRuta, codigoRuta);
            cargarRegistro(key, BeanBaseDatosAcme.ACCION_INSERTAR);

            cargarListaUsuarioproblemaprueba();
            registro.getCampos().put(cAforador, aforador);
            registro.getCampos().put(cFechaLecturaAforo, fecha);
            nombreAforador = traerNombreAforador();
            registro.getCampos().put(cNombre, nombreCodigoIni);
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1576"));
        }
        cargarListaUsuarioProblemasaforo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cambiar en la vista
     */
    public void oprimirCambiar() {
        // <CODIGO_DESARROLLADO>
        activarDetalle(2, true, 2);
        aceptarVisible = true;
        activarDetalle(1, false, 1);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Problemas en la vista
     */
    public void oprimirProblemas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Usuarioproblemaprueba
     */
    public void agregarRegistroSubUsuarioproblemaprueba() {
        try {
            registroSubUsuarioProblemaPrueba.getCampos().remove(cNombre);
            registroSubUsuarioProblemaPrueba.getCampos().remove(cSolucion);
            registroSubUsuarioProblemaPrueba.getCampos().put(cCompania,
                            compania);
            registroSubUsuarioProblemaPrueba.getCampos().put("ANO", anio);
            registroSubUsuarioProblemaPrueba.getCampos().put(cPeriodo, periodo);
            registroSubUsuarioProblemaPrueba.getCampos().put(cCiclo, ciclo);
            registroSubUsuarioProblemaPrueba.getCampos().put(cCodigoRuta,
                            codigoRuta);
            registroSubUsuarioProblemaPrueba.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubUsuarioProblemaPrueba.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            String urlEnumId = LecturasControladorUrlEnum.URL88966.getValue();
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubUsuarioProblemaPrueba.getCampos());
            cargarListaUsuarioproblemaprueba();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubUsuarioProblemaPrueba = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Usuarioproblemaprueba
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubUsuarioproblemaprueba(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            Map<String, Object> fields = css;
            fields.put(cCodigoRuta, reg.getCampos().get(cCodigoRuta));
            fields.put(GeneralParameterEnum.CLASE.getName(),
                            CLASE_PROBLEMA_AFORO);
            fields.put(LecturasControladorEnum.PARAM1.getValue(), reg
                            .getCampos()
                            .get(LecturasControladorEnum.PARAM1.getValue()));
            fields.put(LecturasControladorEnum.PARAM2.getValue(),
                            idProblemaActual);
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter params = new Parameter();
            params.setFields(fields);
            String urlEnumId = LecturasControladorUrlEnum.URL92063.getValue();
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            params);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaUsuarioproblemaprueba();
        }
    }

    /**
     * Metodo de eliminacion del formulario Usuarioproblemaprueba
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubUsuarioproblemaprueba(Registro reg) {
        try {
            String urlEnumId = LecturasControladorUrlEnum.URL94766.getValue();
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaUsuarioproblemaprueba();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Usuarioproblemaprueba
     */
    public void cancelarEdicionUsuarioproblemaprueba() {
        cargarListaUsuarioproblemaprueba();
        cargarListaUsuarioProblemasaforo();
    }

    /**
     * Metodo de insercion del formulario Usuarioproblemasaforo
     */
    public void agregarRegistroSubUsuarioproblemasaforo() {
        /*
         * No aplica. El subformulario UsuarioProblemasAforo es de
         * solo lectura.
         */
    }

    /**
     * Metodo de edicion del formulario Usuarioproblemasaforo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubUsuarioproblemasaforo(RowEditEvent event) {
        /*
         * No aplica. El subformulario UsuarioProblemasAforo es de
         * solo lectura.
         */
    }

    /**
     * Metodo de eliminacion del formulario Usuarioproblemasaforo
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubUsuarioProblemasaforo(Registro reg) {
        /*
         * No aplica. El subformulario UsuarioProblemasAforo es de
         * solo lectura.
         */
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Usuarioproblemasaforo
     */
    public void cancelarEdicionUsuarioproblemasaforo() {
        cargarListaUsuarioProblemasaforo();
    }

    /**
     * Evento que se llama en el momento de activar un registro del
     * subformulario.
     * 
     * @param registro
     */
    public void activarEdicionUsuarioproblemaprueba(Registro registro) {
        setIndiceUsuarioproblemaprueba(listaUsuarioproblemaprueba
                        .indexOf(registro));
        idProblemaActual = extraerString(registro.getCampos()
                        .get(LecturasControladorEnum.PARAM1.getValue()));
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
        // <CODIGO_DESARROLLADO>
        activarDetalle(1, false, 1);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Gestiona la visualizaci&oacute;n de los subformularios y
     * componentes de los datos asociados al c&oacute;digo de ruta
     * seleccionado.
     * 
     * @param opcion
     * @param valor
     * @param propiedad
     */
    private void activarDetalle(int opcion, boolean valor, int propiedad) {
        if ((opcion == 1) && (propiedad == 1)) {
            codigoRutaVisible = valor;
            txtUsuarioVisible = valor;
            lecturaAnteriorVisible = valor;
            lecturaaforoVisible = valor;
            visibleBotonCambiar = valor;
            chkDeshabitadoVisible = valor;
            chkChapetasVisible = valor;
            chkFijosVisible = valor;
            usuarioProblemaPruebaVisible = valor;
            String nombreParametro = "CARGAR PROBLEMA AFORO PERIODO ANTERIOR";
            String cargarProblema = getParametro(nombreParametro, "NO");
            problemaAforoVisible = "SI".equals(cargarProblema) && valor ? true
                : false;
        }
        if ((opcion == 2) && (propiedad == 2)) {
            anoEnabled = !valor;
            fechaEnabled = !valor;
            cicloEnabled = !valor;
            periodoEnabled = !valor;
            aforadorEnabled = !valor;
            codigoinicialrutaEnabled = !valor;
            txtfechaEnabled = !valor;
        }
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Verdadero si permite completar la accion
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Verdadero si permite completar la accion
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
     * @return Verdadero si permite completar la accion
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        codigoRuta = extraerString(registro.getCampos().get(cCodigoRuta));
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodigoRuta);
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove("ANOACTUAL");
        registro.getCampos().remove(cCiclo);
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(cPeriodo);
        registro.getCampos().remove("SIGUIENTE");
        registro.getCampos().remove("USO");
        registro.getCampos().remove(GeneralParameterEnum.ESTADO.getName());
        registro.getCampos().remove("NUMERODIGITOS");
        registro.getCampos().remove("LECTURA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return Verdadero si permite completar la accion
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        nombreAforador = traerNombreAforador();
        String nombreUsuario = traerNombreUsuario();
        registro.getCampos().put(cNombre, nombreUsuario);
        registro.getCampos().put(cCodigoRuta, codigoRuta);
        cargarRegistro(getCss(), ACCION_MODIFICAR);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Trae el nombre de usuario seleccionado en la lista de
     * c&oacute;digos iniciales de ruta.
     * 
     * @return nombre del usuario de servicios p&uacute;blicos.
     */
    private String traerNombreUsuario() {
        Map<String, Object> map = new HashMap<>();
        map.put("CODIGORUTA", codigoRuta);
        String usuario = null;
        try {
            Registro reg = listaCodigoInicialRuta.getRegistroUnico(map);
            usuario = extraerString(reg.getCampos().get(cNombre));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return usuario;
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
     * Extrae la fecha del objeto, solo si es diferente de nulo.
     * 
     * @param obj
     * Un objeto
     * @return fecha contenido en el objeto
     */
    private Date extraerFecha(Object object) {
        return object != null ? (Date) object : null;
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
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return Verdadero si permite completar la accion
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
     * @return Verdadero si permite completar la accion
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable nombreAforador
     * 
     * @return nombreAforador
     */
    public String getNombreAforador() {
        return nombreAforador;
    }

    /**
     * Asigna la variable nombreAforador
     * 
     * @param nombreAforador
     * Variable a asignar en nombreAforador
     */
    public void setNombreAforador(String nombreAforador) {
        this.nombreAforador = nombreAforador;
    }

    /**
     * Retorna la variable nombreCodigoIni
     * 
     * @return nombreCodigoIni
     */
    public String getNombreCodigoIni() {
        return nombreCodigoIni;
    }

    /**
     * Asigna la variable nombreCodigoIni
     * 
     * @param nombreCodigoIni
     * Variable a asignar en nombreCodigoIni
     */
    public void setNombreCodigoIni(String nombreCodigoIni) {
        this.nombreCodigoIni = nombreCodigoIni;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anioSigte
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
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
     * Retorna la variable perSigte
     * 
     * @return perSigte
     */
    public String getPerSigte() {
        return perSigte;
    }

    /**
     * Asigna la variable perSigte
     * 
     * @param perSigte
     * Variable a asignar en perSigte
     */
    public void setPerSigte(String perSigte) {
        this.perSigte = perSigte;
    }

    /**
     * Retorna la variable anioSigte
     * 
     * @return anioSigte
     */
    public String getAnioSigte() {
        return anioSigte;
    }

    /**
     * Asigna la variable anioSigte
     * 
     * @param anioSigte
     * Variable a asignar en anioSigte
     */
    public void setAnioSigte(String anioSigte) {
        this.anioSigte = anioSigte;
    }

    /**
     * Retorna la variable lecturaActual
     * 
     * @return lecturaActual
     */
    public String getLecturaActual() {
        return lecturaActual;
    }

    /**
     * Asigna la variable lecturaActual
     * 
     * @param lecturaActual
     * Variable a asignar en lecturaActual
     */
    public void setLecturaActual(String lecturaActual) {
        this.lecturaActual = lecturaActual;
    }

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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaProblema
     * 
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblema() {
        return listaProblema;
    }

    /**
     * Asigna la lista listaProblema
     * 
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblema(RegistroDataModelImpl listaProblema) {
        this.listaProblema = listaProblema;
    }

    /**
     * Retorna la lista listaProblema
     * 
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblemaE() {
        return listaProblemaE;
    }

    /**
     * Asigna la lista listaProblema
     * 
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblemaE(RegistroDataModelImpl listaProblemaE) {
        this.listaProblemaE = listaProblemaE;
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

    /**
     * Retorna la lista listaCodigoInicialRuta
     * 
     * @return listaCodigoInicialRuta
     */
    public RegistroDataModelImpl getListaCodigoInicialRuta() {
        return listaCodigoInicialRuta;
    }

    /**
     * Asigna la lista listaCodigoInicialRuta
     * 
     * @param listaCodigoInicialRuta
     * Variable a asignar en listaCodigoInicialRuta
     */
    public void setListaCodigoInicialRuta(
        RegistroDataModelImpl listaCodigoInicialRuta) {
        this.listaCodigoInicialRuta = listaCodigoInicialRuta;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaUsuarioproblemaprueba
     * 
     * @return listaUsuarioproblemaprueba
     */
    public List<Registro> getListaUsuarioproblemaprueba() {
        return listaUsuarioproblemaprueba;
    }

    /**
     * Asigna la lista listaUsuarioproblemaprueba
     * 
     * @param listaUsuarioproblemaprueba
     * Variable a asignar en listaUsuarioproblemaprueba
     */
    public void setListaUsuarioproblemaprueba(
        List<Registro> listaUsuarioproblemaprueba) {
        this.listaUsuarioproblemaprueba = listaUsuarioproblemaprueba;
    }

    /**
     * Retorna la lista listaUsuarioproblemasaforo
     * 
     * @return listaUsuarioproblemasaforo
     */
    public List<Registro> getListaUsuarioproblemasaforo() {
        return listaUsuarioproblemasaforo;
    }

    /**
     * Asigna la lista listaUsuarioproblemasaforo
     * 
     * @param listaUsuarioproblemasaforo
     * Variable a asignar en listaUsuarioproblemasaforo
     */
    public void setListaUsuarioproblemasaforo(
        List<Registro> listaUsuarioproblemasaforo) {
        this.listaUsuarioproblemasaforo = listaUsuarioproblemasaforo;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubUsuarioProblemaPrueba
     * 
     * @return registroSubUsuarioProblemaPrueba
     */
    public Registro getregistroSubUsuarioProblemaPrueba() {
        return registroSubUsuarioProblemaPrueba;
    }

    /**
     * Retorna el objeto registroSubUsuarioProblemasaforo
     * 
     * @return registroSubUsuarioProblemasaforo
     */
    public void setregistroSubUsuarioProblemaPrueba(
        Registro registroSubUsuarioProblemaPrueba) {
        this.registroSubUsuarioProblemaPrueba = registroSubUsuarioProblemaPrueba;
    }

    /**
     * Asigna el objeto registroSubUsuarioProblemasaforo
     * 
     * @param registroSubUsuarioProblemasaforo
     * Variable a asignar en registroSubUsuarioProblemasaforo
     */
    public Registro getRegistroSubUsuarioProblemasaforo() {
        return registroSubUsuarioProblemasaforo;
    }

    /**
     * Asigna el objeto registroSubUsuarioProblemasaforo
     * 
     * @param registroSubUsuarioProblemasaforo
     * Variable a asignar en registroSubUsuarioProblemasaforo
     */
    public void setRegistroSubUsuarioProblemasaforo(
        Registro registroSubUsuarioProblemasaforo) {
        this.registroSubUsuarioProblemasaforo = registroSubUsuarioProblemasaforo;
    }

    /**
     * Retorna la variable visibleBotonCambiar
     * 
     * @return visibleBotonCambiar
     */
    public boolean isVisibleBotonCambiar() {
        return visibleBotonCambiar;
    }

    /**
     * Asigna la variable visibleBotonCambiar
     * 
     * @param visibleBotonCambiar
     * Variable a asignar en visibleBotonCambiar
     */
    public void setVisibleBotonCambiar(boolean visibleBotonCambiar) {
        this.visibleBotonCambiar = visibleBotonCambiar;
    }

    /**
     * Retorna la variable codigoRutaVisible
     * 
     * @return codigoRutaVisible
     */
    public boolean isCodigoRutaVisible() {
        return codigoRutaVisible;
    }

    /**
     * Asigna la variable codigoRutaVisible
     * 
     * @param visibleBotonCambiar
     * Variable a asignar en codigoRutaVisible
     */
    public void setCodigoRutaVisible(boolean codigoRutaVisible) {
        this.codigoRutaVisible = codigoRutaVisible;
    }

    /**
     * Retorna la variable txtUsuarioVisible
     * 
     * @return txtUsuarioVisible
     */
    public boolean isTxtUsuarioVisible() {
        return txtUsuarioVisible;
    }

    /**
     * Asigna la variable txtUsuarioVisible
     * 
     * @param txtUsuarioVisible
     * Variable a asignar en txtUsuarioVisible
     */
    public void setTxtUsuarioVisible(boolean txtUsuarioVisible) {
        this.txtUsuarioVisible = txtUsuarioVisible;
    }

    /**
     * Retorna la variable lecturaAnteriorVisible
     * 
     * @return lecturaAnteriorVisible
     */
    public boolean isLecturaAnteriorVisible() {
        return lecturaAnteriorVisible;
    }

    /**
     * Asigna la variable lecturaAnteriorVisible
     * 
     * @param lecturaAnteriorVisible
     * Variable a asignar en lecturaAnteriorVisible
     */
    public void setLecturaAnteriorVisible(boolean lecturaAnteriorVisible) {
        this.lecturaAnteriorVisible = lecturaAnteriorVisible;
    }

    /**
     * Retorna la variable lecturaaforoVisible
     * 
     * @return lecturaaforoVisible
     */
    public boolean isLecturaaforoVisible() {
        return lecturaaforoVisible;
    }

    /**
     * Asigna la variable lecturaaforoVisible
     * 
     * @param lecturaaforoVisible
     * Variable a asignar en lecturaaforoVisible
     */
    public void setLecturaaforoVisible(boolean lecturaaforoVisible) {
        this.lecturaaforoVisible = lecturaaforoVisible;
    }

    /**
     * Retorna la variable cambiarVisible
     * 
     * @return cambiarVisible
     */
    public boolean isCambiarVisible() {
        return cambiarVisible;
    }

    /**
     * Asigna la variable cambiarVisible
     * 
     * @param cambiarVisible
     * Variable a asignar en cambiarVisible
     */
    public void setCambiarVisible(boolean cambiarVisible) {
        this.cambiarVisible = cambiarVisible;
    }

    /**
     * Retorna la variable chkDeshabitadoVisible
     * 
     * @return chkDeshabitadoVisible
     */
    public boolean isChkDeshabitadoVisible() {
        return chkDeshabitadoVisible;
    }

    /**
     * Asigna la variable chkDeshabitadoVisible
     * 
     * @param chkDeshabitadoVisible
     * Variable a asignar en chkDeshabitadoVisible
     */
    public void setChkDeshabitadoVisible(boolean chkDeshabitadoVisible) {
        this.chkDeshabitadoVisible = chkDeshabitadoVisible;
    }

    /**
     * Retorna la variable chkChapetasVisible
     * 
     * @return chkChapetasVisible
     */
    public boolean isChkChapetasVisible() {
        return chkChapetasVisible;
    }

    /**
     * Asigna la variable chkChapetasVisible
     * 
     * @param chkChapetasVisible
     * Variable a asignar en chkChapetasVisible
     */
    public void setChkChapetasVisible(boolean chkChapetasVisible) {
        this.chkChapetasVisible = chkChapetasVisible;
    }

    /**
     * Retorna la variable chkFijosVisible
     * 
     * @return chkFijosVisible
     */
    public boolean isChkFijosVisible() {
        return chkFijosVisible;
    }

    /**
     * Asigna la variable chkFijosVisible
     * 
     * @param chkFijosVisible
     * Variable a asignar en chkFijosVisible
     */
    public void setChkFijosVisible(boolean chkFijosVisible) {
        this.chkFijosVisible = chkFijosVisible;
    }

    /**
     * Retorna la variable usuarioProblemaPruebaVisible
     * 
     * @return usuarioProblemaPruebaVisible
     */
    public boolean isusuarioProblemaPruebaVisible() {
        return usuarioProblemaPruebaVisible;
    }

    /**
     * Asigna la variable usuarioProblemaPruebaVisible
     * 
     * @param usuarioProblemaPruebaVisible
     * Variable a asignar en usuarioProblemaPruebaVisible
     */
    public void setusuarioProblemaPruebaVisible(
        boolean usuarioProblemaPruebaVisible) {
        this.usuarioProblemaPruebaVisible = usuarioProblemaPruebaVisible;
    }

    /**
     * Retorna la variable fechaEnabled
     * 
     * @return fechaEnabled
     */
    public boolean isFechaEnabled() {
        return fechaEnabled;
    }

    /**
     * Asigna la variable fechaEnabled
     * 
     * @param fechaEnabled
     * Variable a asignar en fechaEnabled
     */
    public void setFechaEnabled(boolean fechaEnabled) {
        this.fechaEnabled = fechaEnabled;
    }

    /**
     * Retorna la variable anoEnabled
     * 
     * @return anoEnabled
     */
    public boolean isAnoEnabled() {
        return anoEnabled;
    }

    /**
     * Asigna la variable anoEnabled
     * 
     * @param anoEnabled
     * Variable a asignar en anoEnabled
     */
    public void setAnoEnabled(boolean anoEnabled) {
        this.anoEnabled = anoEnabled;
    }

    /**
     * Retorna la variable anoEnabled
     * 
     * @return cicloEnabled
     */
    public boolean isCicloEnabled() {
        return cicloEnabled;
    }

    /**
     * Asigna la variable cicloEnabled
     * 
     * @param cicloEnabled
     * Variable a asignar en cicloEnabled
     */
    public void setCicloEnabled(boolean cicloEnabled) {
        this.cicloEnabled = cicloEnabled;
    }

    /**
     * Retorna la variable periodoEnabled
     * 
     * @return periodoEnabled
     */
    public boolean isPeriodoEnabled() {
        return periodoEnabled;
    }

    /**
     * Asigna la variable periodoEnabled
     * 
     * @param periodoEnabled
     * Variable a asignar en periodoEnabled
     */
    public void setPeriodoEnabled(boolean periodoEnabled) {
        this.periodoEnabled = periodoEnabled;
    }

    /**
     * Retorna la variable aforadorEnabled
     * 
     * @return aforadorEnabled
     */
    public boolean isAforadorEnabled() {
        return aforadorEnabled;
    }

    /**
     * Asigna la variable aforadorEnabled
     * 
     * @param aforadorEnabled
     * Variable a asignar en aforadorEnabled
     */
    public void setAforadorEnabled(boolean aforadorEnabled) {
        this.aforadorEnabled = aforadorEnabled;
    }

    /**
     * Retorna la variable txtfechaEnabled
     * 
     * @return txtfechaEnabled
     */
    public boolean isTxtfechaEnabled() {
        return txtfechaEnabled;
    }

    /**
     * Asigna la variable txtfechaEnabled
     * 
     * @param txtfechaEnabled
     * Variable a asignar en txtfechaEnabled
     */
    public void setTxtfechaEnabled(boolean txtfechaEnabled) {
        this.txtfechaEnabled = txtfechaEnabled;
    }

    /**
     * Retorna la variable codigoinicialrutaEnabled
     * 
     * @return codigoinicialrutaEnabled
     */
    public boolean isCodigoinicialrutaEnabled() {
        return codigoinicialrutaEnabled;
    }

    /**
     * Asigna la variable codigoinicialrutaEnabled
     * 
     * @param codigoinicialrutaEnabled
     * Variable a asignar en codigoinicialrutaEnabled
     */
    public void setCodigoinicialrutaEnabled(boolean codigoinicialrutaEnabled) {
        this.codigoinicialrutaEnabled = codigoinicialrutaEnabled;
    }

    /**
     * Retorna la variable problemaAforoVisible
     * 
     * @return problemaAforoVisible
     */
    public boolean isProblemaAforoVisible() {
        return problemaAforoVisible;
    }

    /**
     * Asigna la variable problemaAforoVisible
     * 
     * @param problemaAforoVisible
     * Variable a asignar en problemaAforoVisible
     */
    public void setProblemaAforoVisible(boolean problemaAforoVisible) {
        this.problemaAforoVisible = problemaAforoVisible;
    }

    /**
     * Retorna la variable aceptarVisible
     * 
     * @return aceptarVisible
     */
    public boolean isAceptarVisible() {
        return aceptarVisible;
    }

    /**
     * Asigna la variable aceptarVisible
     * 
     * @param aceptarVisible
     * Variable a asignar en aceptarVisible
     */
    public void setAceptarVisible(boolean aceptarVisible) {
        this.aceptarVisible = aceptarVisible;
    }

    // </SET_GET_ADICIONALES>

    public void mensajesInicioModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Retorna la variable indiceUsuarioproblemaprueba
     * 
     * @return indiceUsuarioproblemaprueba
     */
    public int getIndiceUsuarioproblemaprueba() {
        return indiceUsuarioproblemaprueba;
    }

    /**
     * Asigna la variable indiceUsuarioproblemaprueba
     * 
     * @param indiceUsuarioproblemaprueba
     * Variable a asignar en indiceUsuarioproblemaprueba
     */
    public void setIndiceUsuarioproblemaprueba(
        int indiceUsuarioproblemaprueba) {
        this.indiceUsuarioproblemaprueba = indiceUsuarioproblemaprueba;
    }
}
