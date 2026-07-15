package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmestprevioproysControladorEnum;
import com.sysman.precontractual.enums.FrmestproyControladorEnum;
import com.sysman.precontractual.enums.FrmestproyControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 31/10/2016
 * @modified jguerrero
 * @version 2. 30/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class FrmestproyControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    /** Constante a nivel de clase que aloja el valor de ACTIVIDAD */
    private final String cActividad;

    /** Constante a nivel de clase que aloja el valor de CANTIDAD */
    private final String cCantidad;

    /**
     * Constante a nivel de clase que aloja el valor de CANTIDAD_EJE
     */
    private final String cCantidadEje;

    /** Constante a nivel de clase que aloja el valor de COD_PROY */
    private final String cCodProy;

    /** Constante a nivel de clase que aloja el valor de COMPONENTE */
    private final String cComponente;

    /** Constante a nivel de clase que aloja el valor de COSTOTOTAL */
    private final String cCostoTotal;

    /**
     * Constante a nivel de clase que aloja el valor de COSTOUNITARIO
     */
    private final String cCostoUnitario;

    /**
     * Constante a nivel de clase que aloja el valor de
     * DESCRIPCION_META
     */
    private final String cDescripcionMeta;

    /** Constante a nivel de clase que aloja el valor de ID_PLAN */
    private final String cIdPlan;

    /**
     * Constante a nivel de clase que aloja el valor de
     * NOMBREACTIVIDAD
     */
    private final String cNombreActividad;

    /**
     * Constante a nivel de clase que aloja el valor de
     * NOMBRECOMPONENTE
     */
    private final String cNombreComponente;

    /**
     * Constante a nivel de clase que aloja el valor de NOMBREPROYECTO
     */
    private final String cNombreProyecto;

    /** Constante a nivel de clase que aloja el valor de NOMBRETIPO */
    private final String cNombreTipo;

    /**
     * Constante a nivel de clase que aloja el valor de PORCEJECUTADO
     */
    private final String cPorcEjecutado;

    /**
     * Constante a nivel de clase que aloja el valor de T_COMPONENTE
     */
    private final String cTComponente;

    /**
     * Constante a nivel de clase que aloja el valor de VALOREJECUTADO
     */
    private final String cValorEjecutado;

    /**
     * Constante a nivel de clase que aloja el valor de
     * VALORPROGRAMADO
     */
    private final String cValorProgramado;

    /**
     * Constante a nivel de clase que aloja el valor de
     * VALOR_SOLICITADO_ACTIV
     */
    private final String cValorSolicitado;

    /** Constante a nivel de clase que aloja el valor de VIGENCIA */
    private final String cVigencia;

    /** Constante a nivel de clase que aloja el valor de esCreador */
    private final String cEsCreador;

    private final String codEstudioCons;
    private final String nombreCons;
    private final String cantidadMetaCons;
    private final String codIndicadorCons;

    private Registro registroSub;
    private List<Registro> listaSubmetas;
    private RegistroDataModelImpl listaCbCodigoMeta;
    private RegistroDataModelImpl listaCbCodigoMetaE;
    private String auxiliar;

    private RegistroDataModelImpl listaCmbActividad;
    private RegistroDataModelImpl listaCmbProyecto;
    private RegistroDataModelImpl listaCmbComponente;
    private RegistroDataModelImpl listaCmbTipoComponente;
    private String txtCodEstudio;
    private HashMap<String, Object> ridEP;
    private HashMap<String, Object> ridMetas;
    private String vigenciaPeriodo;

    /**
     * Atributo que controla la visibilidad de los controles de
     * creacion, actualizacion y eliminacion en la forma
     */
    private boolean vobo;
    private String dependencia;
    private Boolean esCreador;
    private boolean bloqueadoEditar;
    private boolean mostrarBoton;
    private boolean mostrarSubformulario;

    private String tipoMeta;
    private String nombre;
    private double cantidad;

    private boolean metas;

    private int indiceSubmetas;

    /**
     * Variable que almacena el codigo del formulario desde el que se
     * accede a este controlador
     */
    private int frmOrigen;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @SuppressWarnings("unchecked")
    public FrmestproyControlador() {
        super();
        compania = SessionUtil.getCompania();

        cActividad = GeneralParameterEnum.ACTIVIDAD.getName();
        cCantidad = GeneralParameterEnum.CANTIDAD.getName();
        cCantidadEje = FrmestproyControladorEnum.CANTIDAD_EJE.getValue();
        cCodProy = FrmestproyControladorEnum.COD_PROY.getValue();
        cComponente = FrmestproyControladorEnum.COMPONENTE.getValue();
        cCostoTotal = FrmestproyControladorEnum.COSTOTOTAL.getValue();
        cCostoUnitario = FrmestproyControladorEnum.COSTOUNITARIO.getValue();
        cDescripcionMeta = FrmestproyControladorEnum.DESCRIPCION_META
                        .getValue();

        cIdPlan = GeneralParameterEnum.ID_PLAN.getName();
        cNombreActividad = FrmestproyControladorEnum.NOMBREACTIVIDAD.getValue();
        cNombreComponente = FrmestproyControladorEnum.NOMBRECOMPONENTE
                        .getValue();
        cNombreProyecto = FrmestproyControladorEnum.NOMBREPROYECTO.getValue();
        cNombreTipo = FrmestproyControladorEnum.NOMBRETIPO.getValue();
        cPorcEjecutado = FrmestproyControladorEnum.PORCEJECUTADO.getValue();
        cTComponente = FrmestproyControladorEnum.T_COMPONENTE.getValue();
        cValorEjecutado = FrmestproyControladorEnum.VALOREJECUTADO.getValue();
        cValorProgramado = FrmestproyControladorEnum.VALORPROGRAMADO.getValue();
        cValorSolicitado = FrmestproyControladorEnum.VALOR_SOLICITADO_ACTIV
                        .getValue();
        cVigencia = GeneralParameterEnum.VIGENCIA.getName();
        cEsCreador = FrmestproyControladorEnum.ESCREADORLOWER.getValue();
        indiceSubmetas = -1;
        codEstudioCons = GeneralParameterEnum.COD_ESTUDIO.getName();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        cantidadMetaCons = FrmestproyControladorEnum.CANTIDAD_META.getValue();
        codIndicadorCons = FrmestproyControladorEnum.COD_INDICADOR.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMESTPROY_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ridEP = (HashMap<String, Object>) parametrosEntrada.get(
                                FrmestproyControladorEnum.RIDLOWER.getValue());

                frmOrigen = Integer.parseInt(SysmanFunciones
                                .nvl(parametrosEntrada.get(
                                                FrmestprevioproysControladorEnum.PR_FRM_ORIGEN
                                                                .getValue()),
                                                "0")
                                .toString());

                ridMetas = (HashMap<String, Object>) parametrosEntrada
                                .get(FrmestproyControladorEnum.RIDMETASLOWER
                                                .getValue());
                txtCodEstudio = (String) parametrosEntrada
                                .get(FrmestproyControladorEnum.TXT_COD_ESTUDIOLOWER
                                                .getValue());
                vigenciaPeriodo = (String) parametrosEntrada
                                .get(FrmestproyControladorEnum.VIGENCIA_PERIODOLOWER
                                                .getValue());
                vobo = Boolean.parseBoolean(
                                parametrosEntrada
                                                .get(FrmestproyControladorEnum.VOBOLOWER
                                                                .getValue())
                                                .toString());
                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get(cEsCreador).toString());
                dependencia = (String) parametrosEntrada
                                .get(FrmestproyControladorEnum.DEPENDENCIALOWER
                                                .getValue());
                metas = (boolean) SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get(FrmestproyControladorEnum.METASLOWER
                                                                .getValue()),
                                                false);
            }

            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(FrmestproyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaCmbProyecto();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubmetas();
        cargarListaCbCodigoMetaE();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubmetas = null;
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_EST_PROY;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        txtCodEstudio);

    }

    public void cargarListaSubmetas() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                            txtCodEstudio);

            param.put(FrmestproyControladorEnum.TIPO_COMPONENTE.getValue(),
                            registro.getCampos().get(cTComponente));
            param.put(cComponente, registro.getCampos().get(cComponente));
            param.put(GeneralParameterEnum.ACTIVIDAD.getName(),
                            registro.getCampos().get(cActividad));
            param.put(GeneralParameterEnum.ACTIVIDAD.getName(),
                            registro.getCampos().get(cActividad));
            param.put(cCodProy, registro.getCampos().get(cCodProy));

            listaSubmetas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.ES_METAS_PI
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            FrmestproyControladorEnum.ES_METAS_PI
                                                            .getValue()));

        }
        catch (Exception e) {
            Logger.getLogger(FrmestproyControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCbCodigoMeta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL4029
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cCodProy, registro.getCampos().get(cCodProy));
        param.put(FrmestproyControladorEnum.TIPOMETA.getValue(), tipoMeta);

        listaCbCodigoMeta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codIndicadorCons);

    }

    public void cargarListaCbCodigoMetaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL4029
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cCodProy, registro.getCampos().get(cCodProy));
        param.put(FrmestproyControladorEnum.TIPOMETA.getValue(), tipoMeta);

        listaCbCodigoMetaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codIndicadorCons);

    }

    public void cargarListaCmbActividad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL4419
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cCodProy, registro.getCampos().get(cCodProy));
        param.put(cComponente, registro.getCampos().get(cComponente));
        param.put(FrmestproyControladorEnum.TIPO_COMPONENTE.getValue(),
                        registro.getCampos().get(cTComponente));
        param.put(cVigencia, vigenciaPeriodo);

        listaCmbActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cActividad);

    }

    public void cargarListaCmbProyecto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL5592
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaCmbProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.PROYECTO.getName());

    }

    public void cargarListaCmbComponente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL5593
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cCodProy, registro.getCampos().get(cCodProy));
        param.put(cVigencia, vigenciaPeriodo);
        param.put(FrmestproyControladorEnum.TIPO_COMPROBANTE.getValue(),
                        registro.getCampos().get(cTComponente));

        listaCmbComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cComponente);

    }

    public void cargarListaCmbTipoComponente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproyControladorUrlEnum.URL5594
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cCodProy, registro.getCampos().get(cCodProy));
        param.put(cVigencia, vigenciaPeriodo);

        listaCmbTipoComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmestproyControladorEnum.TIPOCOMPONENTE.getValue());

    }

    public void agregarRegistroSubSubmetas() {
        try {
            registroSub.getCampos().remove(cDescripcionMeta);
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(cCodProy,
                            registro.getCampos().get(cCodProy));
            registroSub.getCampos().put(codEstudioCons, txtCodEstudio);
            registroSub.getCampos().put(cTComponente,
                            registro.getCampos().get(cTComponente));
            registroSub.getCampos().put(cComponente,
                            registro.getCampos().get(cComponente));
            registroSub.getCampos().put(cActividad,
                            registro.getCampos().get(cActividad));

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_METAS_PI
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubmetas();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmestproyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubmetas(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {
            reg.getCampos().remove(cDescripcionMeta);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_METAS_PI
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmestproyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubmetas();
        }
    }

    public void eliminarRegSubSubmetas(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ES_METAS_PI
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubmetas();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmestproyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubmetas() {
        cargarListaSubmetas();
    }

    public void oprimirCmdMetas() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> ridMet = registro
                        .getLlave();

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(FrmestproyControladorEnum.TCOMPONENTELOWER.getValue(),
                        retornoString(registro, cTComponente));
        parametros.put(FrmestproyControladorEnum.CODPROYLOWER.getValue(),
                        retornoString(registro, cCodProy));
        parametros.put(FrmestproyControladorEnum.COMPONENTELOWER.getValue(),
                        retornoString(registro, cComponente));
        parametros.put(FrmestproyControladorEnum.ACTIVIDADLOWER.getValue(),
                        retornoString(registro, cActividad));
        parametros.put(FrmestproyControladorEnum.COD_ESTUDIOLOWER.getValue(),
                        txtCodEstudio);
        parametros.put(FrmestproyControladorEnum.RIDLOWER.getValue(), ridEP);
        parametros.put(FrmestproyControladorEnum.RIDMETASLOWER.getValue(),
                        ridMet);
        parametros.put(FrmestproyControladorEnum.VOBOLOWER.getValue(), vobo);
        parametros.put(cEsCreador, esCreador);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESMETAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    public void cambiarCbTipoMeta() {
        // <CODIGO_DESARROLLADO>

        tipoMeta = retornoString(registroSub,
                        FrmestproyControladorEnum.TIPO_META.getValue());
        registroSub.getCampos().put(cIdPlan, "");
        registroSub.getCampos().put(cDescripcionMeta, "");
        registroSub.getCampos().put(cCantidad, "");
        cargarListaCbCodigoMeta();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCANTIDAD() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCOSTOUNITARIO() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCbTipoMetaC(int rowNum) {

        tipoMeta = retornoString(listaSubmetas.get(rowNum),
                        FrmestproyControladorEnum.TIPO_META.getValue());
        listaSubmetas.get(rowNum).getCampos().put(cIdPlan,
                        retornoString(listaSubmetas.get(rowNum), cIdPlan));
        listaSubmetas.get(rowNum).getCampos().put(cDescripcionMeta,
                        retornoString(listaSubmetas.get(rowNum), nombreCons));
        listaSubmetas.get(rowNum).getCampos().put(cCantidad, retornoString(
                        listaSubmetas.get(rowNum), cantidadMetaCons));
        cargarListaCbCodigoMetaE();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCbCodigoMetaC(int rowNum) {

        listaSubmetas.get(rowNum).getCampos().put(cDescripcionMeta, nombre);
        listaSubmetas.get(rowNum).getCampos().put(cCantidad, cantidad);

    }

    public void calcularTotal() {
        int cantidads = Integer.parseInt(SysmanFunciones.nvl(
                        registro.getCampos().get(cCantidad), "0").toString());
        double costoUnitario = Double.parseDouble(
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(cCostoUnitario), "0").toString());
        double costoTotal = cantidads * costoUnitario;
        registro.getCampos().put(cCostoTotal, costoTotal);
    }

    public void seleccionarFilaCbCodigoMeta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(cIdPlan,
                        retornoString(registroAux, cIdPlan));
        registroSub.getCampos().put(codIndicadorCons,
                        retornoString(registroAux, codIndicadorCons));
        registroSub.getCampos().put(cDescripcionMeta,
                        retornoString(registroAux, nombreCons));
        registroSub.getCampos().put(cCantidad,
                        Double.valueOf(retornoString(registroAux,
                                        cantidadMetaCons)));
        registroSub.getCampos().put(
                        FrmestproyControladorEnum.VIGENCIA_INICIAL.getValue(),
                        retornoString(registroAux,
                                        FrmestproyControladorEnum.VIGENCIA_INICIAL
                                                        .getValue()));

    }

    public void seleccionarFilaCbCodigoMetaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornoString(registroAux, codIndicadorCons);
        nombre = retornoString(registroAux, nombreCons);
        cantidad = Double.valueOf(retornoString(registroAux, cantidadMetaCons));

    }

    public void seleccionarFilaCmbActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cActividad,
                        registroAux.getCampos().get(cActividad));
        registro.getCampos().put(cNombreActividad,
                        registroAux.getCampos().get(cNombreActividad));
        registro.getCampos().put(cCantidad,
                        registroAux.getCampos().get(cCantidad));
        registro.getCampos().put(cCostoUnitario,
                        registroAux.getCampos().get(cCostoUnitario));
        registro.getCampos().put(cCostoTotal,
                        registroAux.getCampos().get(cCostoTotal));
        registro.getCampos().put(cValorProgramado,
                        registroAux.getCampos().get(cValorProgramado));
        registro.getCampos().put(cValorEjecutado,
                        registroAux.getCampos().get(cValorEjecutado));
        registro.getCampos().put(cVigencia,
                        registroAux.getCampos().get(cVigencia));
        registro.getCampos().put(cPorcEjecutado,
                        registroAux.getCampos().get(cPorcEjecutado));
        registro.getCampos().put(cValorSolicitado,
                        registroAux.getCampos().get(cValorSolicitado));
        registro.getCampos().put(cCantidadEje,
                        registroAux.getCampos().get(cCantidadEje));
    }

    public void seleccionarFilaCmbProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCodProy,
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.PROYECTO
                                                        .getName()));

        registro.getCampos().put(cNombreProyecto,
                        registroAux.getCampos().get(cNombreProyecto));

        registro.getCampos().put(cTComponente, "");
        registro.getCampos().put(cNombreTipo, "");
        registro.getCampos().put(cComponente, "");
        registro.getCampos().put(cNombreComponente, "");

        blanquearCampos();
        cargarListaCmbTipoComponente();
        cargarListaCmbComponente();
        cargarListaCmbActividad();
    }

    public void seleccionarFilaCmbComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cComponente,
                        registroAux.getCampos().get(cComponente));
        registro.getCampos().put(cNombreComponente, registroAux.getCampos()
                        .get(cNombreComponente));
        blanquearCampos();
        cargarListaCmbActividad();
    }

    public void seleccionarFilaCmbTipoComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cTComponente,
                        registroAux.getCampos()
                                        .get(FrmestproyControladorEnum.TIPOCOMPONENTE
                                                        .getValue()));
        registro.getCampos().put(cNombreTipo,
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
        registro.getCampos().put(cComponente, "");
        registro.getCampos().put(cNombreComponente, "");
        blanquearCampos();
        cargarListaCmbComponente();
        cargarListaCmbActividad();
    }

    public void blanquearCampos() {
        registro.getCampos().put(cActividad, "");
        registro.getCampos().put(cNombreActividad, "");
        registro.getCampos().put(cCantidad, "");
        registro.getCampos().put(cCostoUnitario, "");
        registro.getCampos().put(cCostoTotal, "");
        registro.getCampos().put(cValorProgramado, "");
        registro.getCampos().put(cValorEjecutado, "");
        registro.getCampos().put(cVigencia, "");
        registro.getCampos().put(cPorcEjecutado, "");
        registro.getCampos().put(cValorSolicitado, "");
        registro.getCampos().put(cCantidadEje, "");
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (metas) {
            cargarRegistro(ridMetas, "m");
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        String manejaPlanAccion = null;
        mostrarBoton = false;
        mostrarSubformulario = false;
        precargarRegistro();

        try {

            if (css != null) {
                bloqueadoEditar = true;
            }
            else {
                bloqueadoEditar = false;
            }

            manejaPlanAccion = ejbSysmanUtil.consultarParametro(compania,
                            FrmestproyControladorEnum.MANEJA_PLAN_DE_ACCION
                                            .getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

            if (manejaPlanAccion == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2202"));
                return;
            }
            if ("SI".equals(manejaPlanAccion)) {
                mostrarSubformulario = true;
                mostrarBoton = false;
            }
            else {
                mostrarBoton = true;
                mostrarSubformulario = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(FrmestproyControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(cNombreProyecto);
        registro.getCampos().remove(cNombreTipo);
        registro.getCampos().remove(cNombreComponente);
        registro.getCampos().remove(cNombreActividad);

        registro.getCampos().put(codEstudioCons, txtCodEstudio);

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
        registro.getCampos().remove(cNombreProyecto);
        registro.getCampos().remove(cNombreTipo);
        registro.getCampos().remove(cNombreComponente);
        registro.getCampos().remove(cNombreActividad);
        registro.getCampos().put(codEstudioCons, txtCodEstudio);
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

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(frmOrigen == GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                        .getCodigo() ? "ridEstPrevios" : "rid", ridEP);

        parametros.put(FrmestproyControladorEnum.TXT_COD_ESTUDIOLOWER
                        .getValue(), txtCodEstudio);
        parametros.put(FrmestproyControladorEnum.VIGENCIA_PERIODOLOWER
                        .getValue(), vigenciaPeriodo);
        parametros.put(cEsCreador, esCreador);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    public List<Registro> getListaSubmetas() {
        return listaSubmetas;
    }

    public void setListaSubmetas(List<Registro> listaSubmetas) {
        this.listaSubmetas = listaSubmetas;
    }

    public RegistroDataModelImpl getListaCbCodigoMeta() {
        return listaCbCodigoMeta;
    }

    public void setListaCbCodigoMeta(RegistroDataModelImpl listaCbCodigoMeta) {
        this.listaCbCodigoMeta = listaCbCodigoMeta;
    }

    public RegistroDataModelImpl getListaCbCodigoMetaE() {
        return listaCbCodigoMetaE;
    }

    public void setListaCbCodigoMetaE(
        RegistroDataModelImpl listaCbCodigoMetaE) {
        this.listaCbCodigoMetaE = listaCbCodigoMetaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaCmbActividad() {
        return listaCmbActividad;
    }

    public void setListaCmbActividad(RegistroDataModelImpl listaCmbActividad) {
        this.listaCmbActividad = listaCmbActividad;
    }

    public RegistroDataModelImpl getListaCmbProyecto() {
        return listaCmbProyecto;
    }

    public void setListaCmbProyecto(RegistroDataModelImpl listaCmbProyecto) {
        this.listaCmbProyecto = listaCmbProyecto;
    }

    public RegistroDataModelImpl getListaCmbComponente() {
        return listaCmbComponente;
    }

    public void setListaCmbComponente(
        RegistroDataModelImpl listaCmbComponente) {
        this.listaCmbComponente = listaCmbComponente;
    }

    public RegistroDataModelImpl getListaCmbTipoComponente() {
        return listaCmbTipoComponente;
    }

    public void setListaCmbTipoComponente(
        RegistroDataModelImpl listaCmbTipoComponente) {
        this.listaCmbTipoComponente = listaCmbTipoComponente;
    }

    public boolean isBloqueadoEditar() {
        return bloqueadoEditar;
    }

    public void setBloqueadoEditar(boolean bloqueadoEditar) {
        this.bloqueadoEditar = bloqueadoEditar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isMostrarBoton() {
        return mostrarBoton;
    }

    public void setMostrarBoton(boolean mostrarBoton) {
        this.mostrarBoton = mostrarBoton;
    }

    public boolean isMostrarSubformulario() {
        return mostrarSubformulario;
    }

    public void setMostrarSubformulario(boolean mostrarSubformulario) {
        this.mostrarSubformulario = mostrarSubformulario;
    }

    public Boolean getEsCreador() {
        return esCreador;
    }

    public void setEsCreador(Boolean esCreador) {
        this.esCreador = esCreador;
    }

    public boolean isVobo() {
        return vobo;
    }

    public void setVobo(boolean vobo) {
        this.vobo = vobo;
    }

    public int getIndiceSubmetas() {
        return indiceSubmetas;
    }

    public void setIndiceSubmetas(int indiceSubmetas) {
        this.indiceSubmetas = indiceSubmetas;
    }

    private String retornoString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    public void activarEdicionSubmetas(Registro r) {
        indiceSubmetas = listaSubmetas.indexOf(r);

    }

}