package com.sysman.precontractual;

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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmriesgosproysControladorEnum;
import com.sysman.precontractual.enums.FrmriesgosproysControladorUrlEnum;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author apineda
 * @version 1, 01/04/2016
 * 
 * @version 2, 29/08/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Ajustes al redireccionar para que utilice el numero del
 * formulario.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 * 
 * @version 3, 01/08/2018, <strong>mvenegas</strong>: Se cambio el
 * formulario de Continuo a datos para poder implentar nuevos campos
 * de texto (22 nuevos campos).
 */

@ManagedBean
@ViewScoped
public class FrmriesgosproysControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario esta interactuando
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CEDULA</code>
     */
    private final String cCedula;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_ESTUDIO</code>
     */
    private final String cCodEstudio;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_T_RIESGO</code>
     */
    private final String cCodTRiesgo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_RIESGO</code>
     */
    private final String cCodRiesgo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMRESPONSABLE</code>
     */
    private final String cNomResponsable;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>RESP_RECIBIDO</code>
     */
    private final String cRespRecibido;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DETALLE</code>
     */
    private final String cDetalle;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL_RESP_RECIBIDO</code>
     */
    private final String cSucursalRespRecibido;

    private int valoracionAntes;

    private int valoracionDespues;

    private long consecutivoRiesgo;

    private RegistroDataModelImpl listaTxtRespRecibido;
    private RegistroDataModelImpl listaTxtRespRecibidoE;
    private String auxiliar;
    private List<Registro> listacmbTRiesgo;
    private List<Registro> listaCodRiesgo;
    private final Map<String, Object> parametrosEntrada;
    private String codEstudio;
    private String tipoDia;
    private Map<String, Object> ridEstudio;
    private String vigenciaPeriodo;
    private int indice;
    private String codRiesgo;
    private String nombreRiesgo;
    private String nombreRespRec;
    private String sucursal;
    private boolean esCreador;
    private String manejaRiesgoDigitado;
    private boolean bloquearCodRiesgo;

    @EJB
    private EjbSysmanUtil ejbSysmanUtil;

    /**
     * listado que almacena los impactos
     */
    private RegistroDataModelImpl listacmbImpacto;
    /**
     * listado que almacena las fuentes
     */
    private RegistroDataModelImpl listacmbFuente;
    /**
     * listado que almacena las etapas
     */
    private RegistroDataModelImpl listacmbEtapa;
    /**
     * listado que almacena los tipos
     */
    private RegistroDataModelImpl listacmbTipo;
    /**
     * listado que almacena las probabilidades
     */
    private RegistroDataModelImpl listacmbProbabilidad;
    /**
     * listado que almacena las probabilidades despues de ocurrido el
     * evento
     */
    private RegistroDataModelImpl listacmbProbabilidadDespues;
    /**
     * listado que almacena los impactos despues de ocurrido el evento
     */
    private RegistroDataModelImpl listacmbImpactoDespues;

    /**
     * Creates a new instance of FrmriesgosproysControlador
     */
    @SuppressWarnings("unchecked")
    public FrmriesgosproysControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        parametrosEntrada = SessionUtil.getFlash();

        cCedula = FrmriesgosproysControladorEnum.CEDULA.getValue();
        cCodEstudio = GeneralParameterEnum.COD_ESTUDIO.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cCodTRiesgo = FrmriesgosproysControladorEnum.COD_T_RIESGO.getValue();
        cCodRiesgo = FrmriesgosproysControladorEnum.COD_RIESGO.getValue();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cRespRecibido = FrmriesgosproysControladorEnum.RESP_RECIBIDO.getValue();
        cDetalle = FrmriesgosproysControladorEnum.DETALLE.getValue();

        cSucursalRespRecibido = FrmriesgosproysControladorEnum.SUCURSAL_RESP_RECIBIDO
                        .getValue();

        cNomResponsable = FrmriesgosproysControladorEnum.NOMRESPONSABLE
                        .getValue();
        valoracionDespues = 0;

        try {
            // 603
            numFormulario = GeneralCodigoFormaEnum.FRMRIESGOSPROYS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            if (parametrosEntrada != null) {
                ridEstudio = (HashMap<String, Object>) parametrosEntrada
                                .get("ridEstudio");

                codEstudio = (String) parametrosEntrada.get("codEstudio");
                vigenciaPeriodo = (String) parametrosEntrada
                                .get("vigenciaPeriodo");

                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get("esCreador").toString());

                //parametrosEntrada.put("rid", ridEstudio);
                //parametrosEntrada.remove("ridEstudio");
            }

            SessionUtil.cleanFlash();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmriesgosproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_RIES_ESTPR;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void cargarRegistro() {
        // Auto-generated method stub

        if (accion.equals(ACCION_MODIFICAR) ||
            accion.equals(ACCION_VER)) {
            cargarListaCodRiesgo();
        }

    }

    @Override
    public void iniciarListasSubNulo() {
        // Auto-generated method stub

    }

    @Override
    public void iniciarListasSub() {
        // Auto-generated method stub

    }

    @Override
    public void iniciarListas() {
        cargarListaTxtRespRecibido();
        cargarListaTxtRespRecibidoE();
        cargarListacmbImpacto();
        cargarListacmbFuente();
        cargarListacmbEtapa();
        cargarListacmbTipo();
        cargarListacmbProbabilidad();
        cargarListacmbProbabilidadDespues();
        cargarListacmbImpactoDespues();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbTRiesgo();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cCodEstudio, codEstudio);

    }

    public void cargarListacmbTRiesgo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listacmbTRiesgo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmriesgosproysControladorUrlEnum.URL5496
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodRiesgo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodTRiesgo,
                        registro.getCampos().get(cCodTRiesgo));

        try {
            listaCodRiesgo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmriesgosproysControladorUrlEnum.URL5935
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTxtRespRecibido() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL6466
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaTxtRespRecibido = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);
    }

    public void cargarListaTxtRespRecibidoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL6466
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaTxtRespRecibidoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);
    }

    public void cambiarcmbTRiesgo() {
        // <CODIGO_DESARROLLADO>
        if ("NO".equals(manejaRiesgoDigitado)) {
            cargarListaCodRiesgo();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodRiesgo() {
        // <CODIGO_DESARROLLADO>
        codRiesgo = (String) registro.getCampos().get(cCodRiesgo);

        if (verificarAsociacionRiesgo()) {
            registro.getCampos().put(cCodRiesgo, "");
        }

        registro.getCampos().put("FECHA_RECIBIDO", new Date()); //
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodRiesgoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        codRiesgo = (String) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cCodRiesgo);

        if (verificarAsociacionRiesgo()) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCodRiesgo, "");
        }

        sucursal = (String) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cSucursalRespRecibido);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CpDescripcion
     * 
     */
    public void cambiarCpDescripcion() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("FECHA_RECIBIDO", new Date());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica si el riesgo ya se encuentra asociado en otro estudio.
     * 
     * @return <code>true</code>: El riesgo esta asociado en otro
     * estudio.<br>
     * <code>false</code>: El riesgo no esta asociado en algun
     * estudio.
     */
    private boolean verificarAsociacionRiesgo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodEstudio, codEstudio);
        param.put(cCodRiesgo, codRiesgo);

        Registro auxReg = null;

        try {
            auxReg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmriesgosproysControladorUrlEnum.URL7236
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (auxReg != null
            && !"0".equals(auxReg.getCampos().get("CANT").toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2208"));
            return true;
        }

        return false;
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listacmbImpacto
     *
     */
    public void cargarListacmbImpacto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0005
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbImpacto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmriesgosproysControladorEnum.IMPACTO.getValue());
    }

    /**
     * 
     * Carga la lista listacmbFuente
     *
     * 
     */
    public void cargarListacmbFuente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmriesgosproysControladorEnum.FUENTE.getValue());

    }

    /**
     * 
     * Carga la lista listacmbEtapa
     *
     * 
     */
    public void cargarListacmbEtapa() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmriesgosproysControladorEnum.ETAPA.getValue());

    }

    /**
     * 
     * Carga la lista listacmbTipo
     *
     * 
     */
    public void cargarListacmbTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "TIPO");

    }

    /**
     * 
     * Carga la lista listacmbProbabilidad
     *
     * 
     */
    public void cargarListacmbProbabilidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbProbabilidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmriesgosproysControladorEnum.PROBABILIDAD.getValue());

    }

    /**
     * 
     * Carga la lista listacmbProbabilidadDespues
     *
     * 
     */
    public void cargarListacmbProbabilidadDespues() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbProbabilidadDespues = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmriesgosproysControladorEnum.PROBABILIDAD.getValue());
    }

    /**
     * 
     * Carga la lista listacmbImpactoDespues
     *
     * 
     */
    public void cargarListacmbImpactoDespues() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0005
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listacmbImpactoDespues = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmriesgosproysControladorEnum.IMPACTO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbImpacto
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbImpacto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.IMPACTO.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.IMPACTO
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_IMPACTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_IMPACTO
                                                        .getValue()));

        calcularValoracionYCategoria(
                        FrmriesgosproysControladorEnum.ANTES.getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbFuente
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.FUENTE.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.FUENTE
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_FUENTE.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_FUENTE
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEtapa
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEtapa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.ETAPA.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.ETAPA
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_ETAPA.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_ETAPA
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbTipo
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("TIPO", registroAux.getCampos().get("TIPO"));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_TIPO.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_TIPO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbProbabilidad
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbProbabilidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.PROBABILIDAD.getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.PROBABILIDAD
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD
                                                        .getValue()));

        calcularValoracionYCategoria(
                        FrmriesgosproysControladorEnum.ANTES.getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbProbabilidadDespues
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbProbabilidadDespues(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.PROBABILIDAD_DESPUES
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.PROBABILIDAD
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD_DESPUES
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD
                                                        .getValue()));

        calcularValoracionYCategoria(
                        FrmriesgosproysControladorEnum.DESPUES.getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbImpactoDespues
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbImpactoDespues(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.IMPACTO_DESPUES
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.IMPACTO
                                                        .getValue()));
        registro.getCampos().put(
                        FrmriesgosproysControladorEnum.NOMBRE_IMPACTO_DESPUES
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmriesgosproysControladorEnum.NOMBRE_IMPACTO
                                                        .getValue()));
        calcularValoracionYCategoria(
                        FrmriesgosproysControladorEnum.DESPUES.getValue());
    }

    public void seleccionarFilaTxtRespRecibido(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cRespRecibido,
                        registroAux.getCampos().get(cCedula));

        registro.getCampos().put(cNomResponsable,
                        registroAux.getCampos().get(cNombre));

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("SUCURSAL"), "")
                        .toString();
    }

    public void calcularValoracionYCategoria(String estado) {

        if (FrmriesgosproysControladorEnum.ANTES.getValue().equals(estado)) {

            if (registro.getCampos()
                            .get(FrmriesgosproysControladorEnum.IMPACTO
                                            .getValue())
                            .toString() != null
                && registro.getCampos()
                                .get(FrmriesgosproysControladorEnum.PROBABILIDAD
                                                .getValue())
                                .toString() != null) {

                valoracionAntes = Integer.parseInt(registro.getCampos()
                                .get(FrmriesgosproysControladorEnum.IMPACTO
                                                .getValue())
                                .toString())
                    + Integer.parseInt(registro.getCampos().get(
                                    FrmriesgosproysControladorEnum.PROBABILIDAD
                                                    .getValue())
                                    .toString());

                registro.getCampos().put(
                                FrmriesgosproysControladorEnum.VALORACION
                                                .getValue(),
                                String.valueOf(valoracionAntes));

                Map<String, Object> param = new TreeMap<>();
                param.put(FrmriesgosproysControladorEnum.VALORACION.getValue(),
                                String.valueOf(valoracionAntes));

                try {
                    Registro nombreCategoria = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    FrmriesgosproysControladorUrlEnum.URL0006
                                                                                    .getValue())
                                                    .getUrl(),
                                                    param));

                    registro.getCampos().put(
                                    FrmriesgosproysControladorEnum.CATEGORIA
                                                    .getValue(),
                                    nombreCategoria.getCampos().get(
                                                    FrmriesgosproysControladorEnum.CATEGORIA
                                                                    .getValue())
                                                    .toString());
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }

        }
        else {

            if (registro.getCampos()
                            .get(FrmriesgosproysControladorEnum.IMPACTO_DESPUES
                                            .getValue())
                            .toString() != null
                && registro.getCampos().get(
                                FrmriesgosproysControladorEnum.PROBABILIDAD_DESPUES
                                                .getValue())
                                .toString() != null) {

                valoracionDespues = Integer
                                .parseInt(registro.getCampos().get(
                                                FrmriesgosproysControladorEnum.IMPACTO_DESPUES
                                                                .getValue())
                                                .toString())
                    + Integer.parseInt(registro.getCampos().get(
                                    FrmriesgosproysControladorEnum.PROBABILIDAD_DESPUES
                                                    .getValue())
                                    .toString());

                if (valoracionDespues > valoracionAntes) {
                    registro.getCampos().put("VALORACION_DESPUES",
                                    null);
                    registro.getCampos().put(
                                    FrmriesgosproysControladorEnum.IMPACTO_DESPUES
                                                    .getValue(),
                                    null);
                    registro.getCampos().put(
                                    FrmriesgosproysControladorEnum.CATEGORIA_DESPUES
                                                    .getValue(),
                                    null);

                    registro.getCampos().put(
                                    FrmriesgosproysControladorEnum.NOMBRE_IMPACTO_DESPUES
                                                    .getValue(),
                                    null);

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4194"));
                }

                else {
                    registro.getCampos().put("VALORACION_DESPUES",
                                    String.valueOf(valoracionDespues));

                    Map<String, Object> param = new TreeMap<>();
                    param.put(FrmriesgosproysControladorEnum.VALORACION
                                    .getValue(),
                                    String.valueOf(valoracionDespues));

                    try {
                        Registro nombreCategoria = RegistroConverter.toRegistro(
                                        requestManager.get(UrlServiceUtil
                                                        .getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmriesgosproysControladorUrlEnum.URL0006
                                                                                        .getValue())
                                                        .getUrl(),
                                                        param));

                        registro.getCampos().put(
                                        FrmriesgosproysControladorEnum.CATEGORIA_DESPUES
                                                        .getValue(),
                                        nombreCategoria.getCampos().get(
                                                        FrmriesgosproysControladorEnum.CATEGORIA
                                                                        .getValue())
                                                        .toString());
                    }

                    catch (SystemException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }

            }

        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            manejaRiesgoDigitado = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA RIESGO DIGITADO", modulo, new Date(),
                            false);

            manejaRiesgoDigitado = manejaRiesgoDigitado == null ? "NO"
                : manejaRiesgoDigitado;

            bloquearCodRiesgo = "SI".equals(manejaRiesgoDigitado);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        insertarRiesgoDefault();

        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodEstudio, codEstudio);

        if ("SI".equals(manejaRiesgoDigitado)) {
            registro.getCampos().put(cDetalle, consecutivoRiesgo);
            registro.getCampos().put(cCodRiesgo, consecutivoRiesgo);
        }
        else {
            registro.getCampos().put(cDetalle, codRiesgo);
        }

        registro.getCampos().put(cSucursalRespRecibido, sucursal);

        registro.getCampos().put("TIPO_OPCION_FUENTE", "1");
        registro.getCampos().put("TIPO_OPCION_ETAPA", "2");
        registro.getCampos().put("TIPO_OPCION_IMPACTO", "5");
        registro.getCampos().put("TIPO_OPCION_IMPACTO_DES", "5");
        registro.getCampos().put("TIPO_OPCION_PROBABILIDAD", "4");
        registro.getCampos().put("TIPO_OPCION_PROBABILIDAD_DES", "4");
        registro.getCampos().put("TIPO_OPCION_TIPO", "3");

        registro.getCampos().remove(
                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD
                                        .getValue());
        registro.getCampos()
                        .remove(FrmriesgosproysControladorEnum.NOMBRE_IMPACTO
                                        .getValue());
        registro.getCampos().remove(FrmriesgosproysControladorEnum.NOMBRE_FUENTE
                        .getValue());
        registro.getCampos().remove(
                        FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD_DESPUES
                                        .getValue());
        registro.getCampos().remove(
                        FrmriesgosproysControladorEnum.NOMBRE_IMPACTO_DESPUES
                                        .getValue());
        registro.getCampos().remove(
                        FrmriesgosproysControladorEnum.NOMBRE_TIPO.getValue());
        registro.getCampos().remove(
                        FrmriesgosproysControladorEnum.NOMBRE_ETAPA.getValue());

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
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cNomResponsable);

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cCodEstudio);

            registro.getCampos().put("TIPO_OPCION_FUENTE", "1");
            registro.getCampos().put("TIPO_OPCION_ETAPA", "2");
            registro.getCampos().put("TIPO_OPCION_IMPACTO", "5");
            registro.getCampos().put("TIPO_OPCION_IMPACTO_DES", "5");
            registro.getCampos().put("TIPO_OPCION_PROBABILIDAD", "4");
            registro.getCampos().put("TIPO_OPCION_PROBABILIDAD_DES", "4");
            registro.getCampos().put("TIPO_OPCION_TIPO", "3");

            if ("SI".equals(manejaRiesgoDigitado)) {
                registro.getCampos().put(cDetalle,
                                registro.getCampos().get(cCodRiesgo));
            }
            else {
                registro.getCampos().put(cDetalle, codRiesgo);
            }

            registro.getCampos().remove(
                            FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD
                                            .getValue());
            registro.getCampos().remove(
                            FrmriesgosproysControladorEnum.NOMBRE_IMPACTO
                                            .getValue());
            registro.getCampos()
                            .remove(FrmriesgosproysControladorEnum.NOMBRE_FUENTE
                                            .getValue());
            registro.getCampos().remove(
                            FrmriesgosproysControladorEnum.NOMBRE_PROBABILIDAD_DESPUES
                                            .getValue());
            registro.getCampos().remove(
                            FrmriesgosproysControladorEnum.NOMBRE_IMPACTO_DESPUES
                                            .getValue());
            registro.getCampos()
                            .remove(FrmriesgosproysControladorEnum.NOMBRE_TIPO
                                            .getValue());
            registro.getCampos()
                            .remove(FrmriesgosproysControladorEnum.NOMBRE_ETAPA
                                            .getValue());

        }
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

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    public void ejecutarrcCerrar() {
     // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridEstPrevios", ridEstudio);
        parametros.put("codEstudio", codEstudio);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void insertarRiesgoDefault() {

        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmriesgosproysControladorUrlEnum.URL0007
                                                        .getValue());

        try {
            consecutivoRiesgo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "ES_RIESGO",
                            SysmanFunciones.concatenar(
                                            " COMPANIA = ''",
                                            compania,
                                            "'' AND COD_T_RIESGO = ''",
                                            registro.getCampos()
                                                            .get(cCodTRiesgo)
                                                            .toString(),
                                            "'' "),
                            cCodRiesgo);

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            fields.put(cCodRiesgo, consecutivoRiesgo);

            fields.put(cCodTRiesgo,
                            registro.getCampos().get(cCodTRiesgo));
            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public List<Registro> getListacmbTRiesgo() {
        return listacmbTRiesgo;
    }

    public void setListacmbTRiesgo(List<Registro> listacmbTRiesgo) {
        this.listacmbTRiesgo = listacmbTRiesgo;
    }

    public RegistroDataModelImpl getListaTxtRespRecibido() {
        return listaTxtRespRecibido;
    }

    public void setListaTxtRespRecibido(
        RegistroDataModelImpl listaTxtRespRecibido) {
        this.listaTxtRespRecibido = listaTxtRespRecibido;
    }

    public RegistroDataModelImpl getListaTxtRespRecibidoE() {
        return listaTxtRespRecibidoE;
    }

    public void setListaTxtRespRecibidoE(
        RegistroDataModelImpl listaTxtRespRecibidoE) {
        this.listaTxtRespRecibidoE = listaTxtRespRecibidoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getCodEstudio() {
        return codEstudio;
    }

    public void setCodEstudio(String codEstudio) {
        this.codEstudio = codEstudio;
    }

    public String getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(String tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getVigenciaPeriodo() {
        return vigenciaPeriodo;
    }

    public void setVigenciaPeriodo(String vigenciaPeriodo) {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }

    public String getCodRiesgo() {
        return codRiesgo;
    }

    public void setCodRiesgo(String codRiesgo) {
        this.codRiesgo = codRiesgo;
    }

    public String getNombreRiesgo() {
        return nombreRiesgo;
    }

    public void setNombreRiesgo(String nombreRiesgo) {
        this.nombreRiesgo = nombreRiesgo;
    }

    public List<Registro> getListaCodRiesgo() {
        return listaCodRiesgo;
    }

    public void setListaCodRiesgo(List<Registro> listaCodRiesgo) {
        this.listaCodRiesgo = listaCodRiesgo;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getNombreRespRec() {
        return nombreRespRec;
    }

    public void setNombreRespRec(String nombreRespRec) {
        this.nombreRespRec = nombreRespRec;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listacmbImpacto
     * 
     * @return listacmbImpacto
     */
    public RegistroDataModelImpl getListacmbImpacto() {
        return listacmbImpacto;
    }

    /**
     * Asigna la lista listacmbImpacto
     * 
     * @param listacmbImpacto
     * Variable a asignar en listacmbImpacto
     */
    public void setListacmbImpacto(RegistroDataModelImpl listacmbImpacto) {
        this.listacmbImpacto = listacmbImpacto;
    }

    /**
     * Retorna la lista listacmbFuente
     * 
     * @return listacmbFuente
     */
    public RegistroDataModelImpl getListacmbFuente() {
        return listacmbFuente;
    }

    /**
     * Asigna la lista listacmbFuente
     * 
     * @param listacmbFuente
     * Variable a asignar en listacmbFuente
     */
    public void setListacmbFuente(RegistroDataModelImpl listacmbFuente) {
        this.listacmbFuente = listacmbFuente;
    }

    /**
     * Retorna la lista listacmbEtapa
     * 
     * @return listacmbEtapa
     */
    public RegistroDataModelImpl getListacmbEtapa() {
        return listacmbEtapa;
    }

    /**
     * Asigna la lista listacmbEtapa
     * 
     * @param listacmbEtapa
     * Variable a asignar en listacmbEtapa
     */
    public void setListacmbEtapa(RegistroDataModelImpl listacmbEtapa) {
        this.listacmbEtapa = listacmbEtapa;
    }

    /**
     * Retorna la lista listacmbTipo
     * 
     * @return listacmbTipo
     */
    public RegistroDataModelImpl getListacmbTipo() {
        return listacmbTipo;
    }

    /**
     * Asigna la lista listacmbTipo
     * 
     * @param listacmbTipo
     * Variable a asignar en listacmbTipo
     */
    public void setListacmbTipo(RegistroDataModelImpl listacmbTipo) {
        this.listacmbTipo = listacmbTipo;
    }

    /**
     * Retorna la lista listacmbProbabilidad
     * 
     * @return listacmbProbabilidad
     */
    public RegistroDataModelImpl getListacmbProbabilidad() {
        return listacmbProbabilidad;
    }

    /**
     * Asigna la lista listacmbProbabilidad
     * 
     * @param listacmbProbabilidad
     * Variable a asignar en listacmbProbabilidad
     */
    public void setListacmbProbabilidad(
        RegistroDataModelImpl listacmbProbabilidad) {
        this.listacmbProbabilidad = listacmbProbabilidad;
    }

    /**
     * Retorna la lista listacmbProbabilidadDespues
     * 
     * @return listacmbProbabilidadDespues
     */
    public RegistroDataModelImpl getListacmbProbabilidadDespues() {
        return listacmbProbabilidadDespues;
    }

    /**
     * Asigna la lista listacmbProbabilidadDespues
     * 
     * @param listacmbProbabilidadDespues
     * Variable a asignar en listacmbProbabilidadDespues
     */
    public void setListacmbProbabilidadDespues(
        RegistroDataModelImpl listacmbProbabilidadDespues) {
        this.listacmbProbabilidadDespues = listacmbProbabilidadDespues;
    }

    /**
     * Retorna la lista listacmbImpactoDespues
     * 
     * @return listacmbImpactoDespues
     */
    public RegistroDataModelImpl getListacmbImpactoDespues() {
        return listacmbImpactoDespues;
    }

    /**
     * Asigna la lista listacmbImpactoDespues
     * 
     * @param listacmbImpactoDespues
     * Variable a asignar en listacmbImpactoDespues
     */
    public void setListacmbImpactoDespues(
        RegistroDataModelImpl listacmbImpactoDespues) {
        this.listacmbImpactoDespues = listacmbImpactoDespues;
    }

    /**
     * @return the bloquearCodRiesgo
     */
    public boolean isBloquearCodRiesgo() {
        return bloquearCodRiesgo;
    }

    /**
     * @param bloquearCodRiesgo
     * the bloquearCodRiesgo to set
     */
    public void setBloquearCodRiesgo(boolean bloquearCodRiesgo) {
        this.bloquearCodRiesgo = bloquearCodRiesgo;
    }
}
