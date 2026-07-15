package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.ParametrosdeentradasControladorEnum;
import com.sysman.nomina.enums.ParametrosdeentradasControladorUrlEnum;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jacelas
 * @version 1, 23/11/2015
 * 
 * @version 1.1, 17/03/2017, <strong>pespitia</strong> <br>
 * Se aplicaron las recomendaciones de SonarLint en el controlador.
 * 
 * @version 2, 18/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos y en el
 * subformulario.
 */

@ManagedBean
@ViewScoped
public class ParametrosdeentradasControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que aloja el codigo de la compa�ia
     * con la que el usuario esta interactuando
     */
    private final String compania;

    /** Constante a nivel de clase que aloja la cadena CIUDAD */
    private final String cCiudad;

    /** Constante a nivel de clase que aloja la cadena CODIGO */
    private final String cCodigo;

    /** Constante a nivel de clase que aloja la cadena COMPANIA */
    private final String cCompania;

    /** Constante a nivel de clase que aloja la cadena DEPARTAMENTO */
    private final String cDepartamento;

    /**
     * Constante a nivel de clase que aloja la cadena ID_DE_CONCEPTO
     */
    private final String cIdConcepto;

    /**
     * Constante a nivel de clase que aloja la cadena
     * MSM_TRANS_INTERRUMPIDA
     */
    private final String cMsmTrans;

    /** Constante a nivel de clase que aloja la cadena PATRONALES */
    private final String cPatronales;

    /** Constante a nivel de clase que aloja la cadena RAZONSOCIAL */
    private final String cRazonSocial;

    /**
     * Constante a nivel de clase que aloja la cadena TOTAL_APORTE_AFP
     */
    private final String cTotalAporte;

    private final String consAporteEps;

    private String codPais;
    private String codDep;
    private Registro registroSub;
    private int indicePatronales;
    private List<Registro> listaDpto;
    private List<Registro> listaCiudad;
    private List<Registro> listaSubDepartamento;
    private List<Registro> listaSubCiudad;
    private List<Registro> listaDepartamento;
    private List<Registro> listaComboPais;
    private List<Registro> listaSubPais;

    private RegistroDataModelImpl listaConceptoINDICADORVACACIONES;
    private RegistroDataModelImpl listaConceptoDIASLABORADOS;
    private RegistroDataModelImpl listaConceptoDIASDISFRUTE;
    private RegistroDataModelImpl listaConceptoDIASPAGARVACACIONES;
    private RegistroDataModelImpl listaConceptoVACACIONESDINERO;
    private RegistroDataModelImpl listaConceptoDIASENCARGO;
    private RegistroDataModelImpl listaConceptoDIASREEMPLAZO;
    private RegistroDataModelImpl listaConceptoPERIODOVACACIONES;
    private RegistroDataModelImpl listaConceptoPORCGASTOS;
    private RegistroDataModelImpl listaConceptoSALARIOENCARGO;
    private RegistroDataModelImpl listaConceptoSALARIOREEMPLAZO;
    private RegistroDataModelImpl listaConceptoDEDUCIBLE;
    private RegistroDataModelImpl listaConceptoDIASHABILES;
    private RegistroDataModelImpl listaConceptoIBL;
    private RegistroDataModelImpl listaConceptoLABSIGPERIODO;
    private RegistroDataModelImpl listaCompania;
    private List<Registro> listaPatronales;
    private List<Registro> listaCiabas;
    private List<Registro> listaTipoAportante;

    private String tituloTexto;
    private String companiaBase;

    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public ParametrosdeentradasControlador() {
        super();
        compania = SessionUtil.getCompania();

        cCiudad = "CIUDAD";
        cCodigo = "CODIGO";
        cCompania = "COMPANIA";
        cDepartamento = "DEPARTAMENTO";
        cIdConcepto = "ID_DE_CONCEPTO";
        cMsmTrans = "MSM_TRANS_INTERRUMPIDA";
        cPatronales = "PATRONALES";
        cRazonSocial = "RAZONSOCIAL";
        cTotalAporte = "TOTAL_APORTE_AFP";
        consAporteEps = "PAR_APORTE_EPS";

        try {
            numFormulario = GeneralCodigoFormaEnum.PARAMETROSDEENTRADAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(ParametrosdeentradasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaCiabas();

    }

    @Override
    public void iniciarListasSub() {
        try {
            cargarListaConceptoINDICADORVACACIONES();
            cargarListaComboPais();
            cargarListaDpto();
            cargarListaCiudad();
            cargarListaPatronales();
            cargarListaSubPais();
            cargarListaSubDepartamento();
            cargarListaSubCiudad();
            cargarListaTipoAportante();
            tituloTexto = SysmanFunciones.concatenar(
                            registro.getCampos().get(cCompania).toString(),
                            " - ",
                            registro.getCampos().get("NIT").toString(), " - ",
                            registro.getCampos().get(cRazonSocial).toString());
            companiaBase = registro.getCampos().get(cCompania).toString();
            registro.getCampos().put(consAporteEps,
                            ejbSysmanUtil.consultarParametro(compania,
                                            "PORCENTAJE SALUD DECRETO 1122",
                                            SessionUtil.getModulo(), new Date(),
                                            false));
        }
        catch (SystemException ex) {
            Logger.getLogger(ParametrosdeentradasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void iniciarListasSubNulo() {
        listaPatronales = null;

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PARAMETROS_DE_ENTRADA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaTipoAportante() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos().get(cCompania).toString());
        try {
            listaTipoAportante = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL20298
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPatronales() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos().get(cCompania));
        param.put(ParametrosdeentradasControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get("NIT"));

        try {
            listaPatronales = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL21911
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            cPatronales));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDpto() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(ParametrosdeentradasControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get("PAIS"));

            listaDpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL10917
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCiudad() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                            registro.getCampos().get(cDepartamento));
            param.put(ParametrosdeentradasControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get("PAIS"));

            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL10095
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaSubDepartamento() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(ParametrosdeentradasControladorEnum.PARAM1.getValue(),
                            codPais);

            listaSubDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL10917
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            cDepartamento));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSubPais() {
        try {
            listaSubPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL12394
                                                                            .getValue())
                                            .getUrl(), null),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "PAISES"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaComboPais() {
        try {
            listaComboPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL12394
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCompania() {
        UrlBean urlBean;
        if ("m".equals(accion) || "v".equals(accion)) {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            ParametrosdeentradasControladorUrlEnum.URL13125
                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            ParametrosdeentradasControladorUrlEnum.URL14200
                                            .getValue());
        }

        listaCompania = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, cCodigo);
    }

    public void cargarListaSubCiudad() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(ParametrosdeentradasControladorEnum.PARAM1.getValue(),
                            codPais);
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), codDep);

            listaSubCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL10095
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            cCiudad));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConceptoINDICADORVACACIONES() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParametrosdeentradasControladorUrlEnum.URL16289
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        registro.getCampos().get(cCompania));

        listaConceptoINDICADORVACACIONES = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param,
                        true, cIdConcepto);

        listaConceptoDIASLABORADOS = listaConceptoINDICADORVACACIONES;
        listaConceptoDIASDISFRUTE = listaConceptoINDICADORVACACIONES;
        listaConceptoDIASPAGARVACACIONES = listaConceptoINDICADORVACACIONES;
        listaConceptoVACACIONESDINERO = listaConceptoINDICADORVACACIONES;
        listaConceptoDIASENCARGO = listaConceptoINDICADORVACACIONES;
        listaConceptoDIASREEMPLAZO = listaConceptoINDICADORVACACIONES;
        listaConceptoPERIODOVACACIONES = listaConceptoINDICADORVACACIONES;
        listaConceptoPORCGASTOS = listaConceptoINDICADORVACACIONES;
        listaConceptoSALARIOENCARGO = listaConceptoINDICADORVACACIONES;
        listaConceptoSALARIOREEMPLAZO = listaConceptoINDICADORVACACIONES;
        listaConceptoDEDUCIBLE = listaConceptoINDICADORVACACIONES;
        listaConceptoDIASHABILES = listaConceptoINDICADORVACACIONES;
        listaConceptoIBL = listaConceptoINDICADORVACACIONES;
        listaConceptoLABSIGPERIODO = listaConceptoINDICADORVACACIONES;

    }

    public void seleccionarFilaConceptoINDICADORVACACIONES(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("INDICADORVACACIONES",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASLABORADOS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASLABORADOS",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASDISFRUTE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASDISFRUTE",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASPAGARVACACIONES(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASPAGARVACACIONES",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoVACACIONESDINERO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("VACACIONESDINERO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASENCARGO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASENCARGO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASREEMPLAZO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASREEMPLAZO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoPERIODOVACACIONES(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PERIODOVACACIONES",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoPORCGASTOS(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PORCGASTOS",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoSALARIOENCARGO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SALARIOENCARGO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoSALARIOREEMPLAZO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SALARIOREEMPLAZO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDEDUCIBLE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEDUCIBLE",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoDIASHABILES(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DIASHABILES",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoIBL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("IBL",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void seleccionarFilaConceptoLABSIGPERIODO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("LABSIGPERIODO",
                        registroAux.getCampos().get(cIdConcepto));
    }

    public void cargarListaCiabas() {
        try {
            listaCiabas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ParametrosdeentradasControladorUrlEnum.URL20297
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubPatronales() {
        try {
            registroSub.getCampos().put("NIT", registro.getCampos().get("NIT"));
            registroSub.getCampos().put(cCompania,
                            registro.getCampos().get(cCompania));
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());
            registroSub.getCampos().remove("NOMBRE_CIUDAD");
            registroSub.getCampos().remove("NOMBRE_DEPARTAMENTO");
            registroSub.getCampos().remove("NOMBRE_PAIS");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PATRONALES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaPatronales();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(cMsmTrans)
                                + ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubPatronales(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove("NOMBRE_DEPARTAMENTO");
            reg.getCampos().remove("NOMBRE_CIUDAD");
            reg.getCampos().remove("NOMBRE_PAIS");
            reg.getCampos().remove("DEP");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(ParametrosdeentradasControladorEnum.PARAM0
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PATRONALES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(cMsmTrans)
                                + ex.getMessage());
        }
        finally {
            cargarListaPatronales();
        }
    }

    public void eliminarRegSubPatronales(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PATRONALES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaPatronales();
        }
        catch (SystemException ex) {
            Logger.getLogger(ParametrosdeentradasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionPatronales() {
        cargarListaPatronales();
    }

    public void oprimirCrear() {
        try {
            // <CODIGO_DESARROLLADO>
            ejbNominaDos.crearDatos(
                            registro.getCampos().get(cCompania).toString(),
                            companiaBase,
                            registro.getCampos().get(cRazonSocial).toString(),
                            registro.getCampos().get(cRazonSocial).toString(),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2632")
                            .replace("#$razonsocial$#", registro.getCampos()
                                            .get(cRazonSocial).toString()));
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(cMsmTrans)
                                + ex.getMessage());
        }
    }

    public void oprimirSALARIOSMINIMOS(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDpto() {
        cargarListaCiudad();
    }

    public void cambiarComboPais() {
        cargarListaDpto();
        cargarListaCiudad();
    }

    public void cambiarSubPais() {
        codPais = registroSub.getCampos().get("PAIS").toString();
        codDep = null;
        cargarListaSubDepartamento();
        cargarListaSubCiudad();
    }

    public void cambiarSubDepartamento() {
        codDep = registroSub.getCampos().get(cDepartamento).toString();
        cargarListaSubCiudad();
    }

    public void cambiarPorcEmpleadoAfp() {
        calcularTotalAporteAPF();
    }

    public void cambiarPorcPatronAfp() {
        calcularTotalAporteAPF();
    }

    public void cambiarCompania() {
        companiaBase = registro.getCampos().get(cCompania).toString();
    }

    public void activarEdicionPatronales(Registro reg) {
        indicePatronales = listaPatronales.indexOf(reg);
    }

    public void calcularTotalAporteAPF() {

        if (registro.getCampos().get("PORC_PATRON_AFP") != null
            && registro.getCampos().get("PORC_EMPLEADO_AFP") != null) {
            Double var1 = Double.parseDouble(registro.getCampos()
                            .get("PORC_PATRON_AFP").toString());
            Double var2 = Double.parseDouble(registro.getCampos()
                            .get("PORC_EMPLEADO_AFP").toString());
            registro.getCampos().put("PORC_TOTAL_AFP",
                            String.valueOf(var1 + var2));
        }
        else {
            registro.getCampos().put("PORC_TOTAL_AFP", "");
        }

    }

    public void cambiarPorcCaja() {
        calcularParafiscalTotalAportesAFP();
    }

    public void cambiarPorcICBF() {
        calcularParafiscalTotalAportesAFP();
    }

    public void cambiarPorcSENA() {
        calcularParafiscalTotalAportesAFP();
    }

    public void cambiarPorcESAP() {
        calcularParafiscalTotalAportesAFP();
    }

    public void cambiarPorcInst() {
        calcularParafiscalTotalAportesAFP();
    }

    public void cambiarPorcPatronEps() {
        calcularAporteEps();
    }

    public void cambiarPorcEmpleadoEps() {
        calcularAporteEps();
    }

    public void cambiarSubDepartamentoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        codPais = listaPatronales.get(rowNum).getCampos().get("PAIS")
                        .toString();
        codDep = listaPatronales.get(rowNum).getCampos()
                        .get(cDepartamento).toString();
        cargarListaSubCiudad();
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarSubPaisC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        codPais = listaPatronales.get(rowNum).getCampos().get("PAIS")
                        .toString();
        codDep = listaPatronales.get(rowNum).getCampos()
                        .get(cDepartamento).toString();
        cargarListaSubDepartamento();
        cargarListaSubCiudad();

        // </CODIGO_DESARROLLADO>
    }

    public void calcularParafiscalTotalAportesAFP() {
        boolean key = SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "PORC_CAJA")
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "PORC_ICBF")
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "PORC_SENA");

        boolean key1 = SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "PORC_ESAP")
            || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "PORC_INST");

        /* Si los campos tienen valor diferente a nulo */
        if (!key && !key1) {

            Double var1 = Double.parseDouble(
                            registro.getCampos().get("PORC_CAJA").toString());
            Double var2 = Double.parseDouble(
                            registro.getCampos().get("PORC_ICBF").toString());
            Double var3 = Double.parseDouble(
                            registro.getCampos().get("PORC_SENA").toString());
            Double var4 = Double.parseDouble(
                            registro.getCampos().get("PORC_ESAP").toString());
            Double var5 = Double.parseDouble(
                            registro.getCampos().get("PORC_INST").toString());

            registro.getCampos().put(cTotalAporte,
                            String.valueOf(var1 + var2 + var3 + var4 + var5));
        }
        else {
            registro.getCampos().put(cTotalAporte, "");
        }
    }

    public void calcularAporteEps() {
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "PORC_PATRON_EPS")
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "PORC_EMPLEADO_EPS")) {
            Double var1 = Double.parseDouble(registro.getCampos()
                            .get("PORC_PATRON_EPS").toString());
            Double var2 = Double.parseDouble(registro.getCampos()
                            .get("PORC_EMPLEADO_EPS").toString());
            registro.getCampos().put(consAporteEps,
                            String.valueOf(var1 + var2));

        }
        else {
            registro.getCampos().put(consAporteEps, "");
        }
    }

    public void seleccionarFilaCompania(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCompania,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put("NIT",
                        registroAux.getCampos().get("NITCOMPANIA"));
        registro.getCampos().put(cRazonSocial,
                        registroAux.getCampos().get("NOMBRE"));
        registro.getCampos().put("TELEFONO",
                        registroAux.getCampos().get("TELEFONO"));
        registro.getCampos().put("FAX", registroAux.getCampos().get("FAX"));
        registro.getCampos().put("DIRECCION",
                        registroAux.getCampos().get("DIRECCION"));
        registro.getCampos().put("PAIS", registroAux.getCampos().get("PAIS"));
        registro.getCampos().put(cDepartamento,
                        registroAux.getCampos().get(cDepartamento));
        registro.getCampos().put(cCiudad,
                        registroAux.getCampos().get(cCiudad));
        companiaBase = registro.getCampos().get(cCompania).toString();
        cargarListaComboPais();
        cargarListaDpto();
        cargarListaCiudad();
        cargarListaConceptoINDICADORVACACIONES();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarListaCompania();
        cargarListaDpto();
        cargarListaCiudad();
        cargarListaComboPais();
        cargarListaSubPais();
        cargarListaSubDepartamento();
        cargarListaSubCiudad();
        cargarListaTipoAportante();
        registro.getCampos().put("CUPOSALUD", "0");
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        try {
            // <CODIGO_DESARROLLADO>
            registro.getCampos().remove(cTotalAporte);

            ejbSysmanUtil.anexarDatosEnTabla("CONCEPTOS", "COMPANIA",
                            registro.getCampos().get(cCompania).toString(),
                            companiaBase, "");

            // </CODIGO_DESARROLLADO>
            return true;
        }
        catch (SystemException ex) {
            Logger.getLogger(ParametrosdeentradasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
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
        registro.getCampos().remove(cTotalAporte);
        registro.getCampos().remove(consAporteEps);
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
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

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public RegistroDataModelImpl getListaConceptoINDICADORVACACIONES() {
        return listaConceptoINDICADORVACACIONES;
    }

    public void setListaConceptoINDICADORVACACIONES(
        RegistroDataModelImpl listaConceptoINDICADORVACACIONES) {
        this.listaConceptoINDICADORVACACIONES = listaConceptoINDICADORVACACIONES;
    }

    public RegistroDataModelImpl getListaConceptoDIASLABORADOS() {
        return listaConceptoDIASLABORADOS;
    }

    public void setListaConceptoDIASLABORADOS(
        RegistroDataModelImpl listaConceptoDIASLABORADOS) {
        this.listaConceptoDIASLABORADOS = listaConceptoDIASLABORADOS;
    }

    public RegistroDataModelImpl getListaConceptoDIASDISFRUTE() {
        return listaConceptoDIASDISFRUTE;
    }

    public void setListaConceptoDIASDISFRUTE(
        RegistroDataModelImpl listaConceptoDIASDISFRUTE) {
        this.listaConceptoDIASDISFRUTE = listaConceptoDIASDISFRUTE;
    }

    public RegistroDataModelImpl getListaConceptoDIASPAGARVACACIONES() {
        return listaConceptoDIASPAGARVACACIONES;
    }

    public void setListaConceptoDIASPAGARVACACIONES(
        RegistroDataModelImpl listaConceptoDIASPAGARVACACIONES) {
        this.listaConceptoDIASPAGARVACACIONES = listaConceptoDIASPAGARVACACIONES;
    }

    public RegistroDataModelImpl getListaConceptoVACACIONESDINERO() {
        return listaConceptoVACACIONESDINERO;
    }

    public void setListaConceptoVACACIONESDINERO(
        RegistroDataModelImpl listaConceptoVACACIONESDINERO) {
        this.listaConceptoVACACIONESDINERO = listaConceptoVACACIONESDINERO;
    }

    public RegistroDataModelImpl getListaConceptoDIASENCARGO() {
        return listaConceptoDIASENCARGO;
    }

    public void setListaConceptoDIASENCARGO(
        RegistroDataModelImpl listaConceptoDIASENCARGO) {
        this.listaConceptoDIASENCARGO = listaConceptoDIASENCARGO;
    }

    public RegistroDataModelImpl getListaConceptoDIASREEMPLAZO() {
        return listaConceptoDIASREEMPLAZO;
    }

    public void setListaConceptoDIASREEMPLAZO(
        RegistroDataModelImpl listaConceptoDIASREEMPLAZO) {
        this.listaConceptoDIASREEMPLAZO = listaConceptoDIASREEMPLAZO;
    }

    public RegistroDataModelImpl getListaConceptoPERIODOVACACIONES() {
        return listaConceptoPERIODOVACACIONES;
    }

    public void setListaConceptoPERIODOVACACIONES(
        RegistroDataModelImpl listaConceptoPERIODOVACACIONES) {
        this.listaConceptoPERIODOVACACIONES = listaConceptoPERIODOVACACIONES;
    }

    public RegistroDataModelImpl getListaConceptoPORCGASTOS() {
        return listaConceptoPORCGASTOS;
    }

    public void setListaConceptoPORCGASTOS(
        RegistroDataModelImpl listaConceptoPORCGASTOS) {
        this.listaConceptoPORCGASTOS = listaConceptoPORCGASTOS;
    }

    public RegistroDataModelImpl getListaConceptoSALARIOENCARGO() {
        return listaConceptoSALARIOENCARGO;
    }

    public void setListaConceptoSALARIOENCARGO(
        RegistroDataModelImpl listaConceptoSALARIOENCARGO) {
        this.listaConceptoSALARIOENCARGO = listaConceptoSALARIOENCARGO;
    }

    public RegistroDataModelImpl getListaConceptoSALARIOREEMPLAZO() {
        return listaConceptoSALARIOREEMPLAZO;
    }

    public void setListaConceptoSALARIOREEMPLAZO(
        RegistroDataModelImpl listaConceptoSALARIOREEMPLAZO) {
        this.listaConceptoSALARIOREEMPLAZO = listaConceptoSALARIOREEMPLAZO;
    }

    public RegistroDataModelImpl getListaConceptoDEDUCIBLE() {
        return listaConceptoDEDUCIBLE;
    }

    public void setListaConceptoDEDUCIBLE(
        RegistroDataModelImpl listaConceptoDEDUCIBLE) {
        this.listaConceptoDEDUCIBLE = listaConceptoDEDUCIBLE;
    }

    public RegistroDataModelImpl getListaConceptoDIASHABILES() {
        return listaConceptoDIASHABILES;
    }

    public void setListaConceptoDIASHABILES(
        RegistroDataModelImpl listaConceptoDIASHABILES) {
        this.listaConceptoDIASHABILES = listaConceptoDIASHABILES;
    }

    public RegistroDataModelImpl getListaConceptoIBL() {
        return listaConceptoIBL;
    }

    public void setListaConceptoIBL(RegistroDataModelImpl listaConceptoIBL) {
        this.listaConceptoIBL = listaConceptoIBL;
    }

    public RegistroDataModelImpl getListaConceptoLABSIGPERIODO() {
        return listaConceptoLABSIGPERIODO;
    }

    public void setListaConceptoLABSIGPERIODO(
        RegistroDataModelImpl listaConceptoLABSIGPERIODO) {
        this.listaConceptoLABSIGPERIODO = listaConceptoLABSIGPERIODO;
    }

    public RegistroDataModelImpl getListaCompania() {
        return listaCompania;
    }

    public void setListaCompania(RegistroDataModelImpl listaCompania) {
        this.listaCompania = listaCompania;
    }

    public List<Registro> getListaComboPais() {
        return listaComboPais;
    }

    public void setListaComboPais(List<Registro> listaComboPais) {
        this.listaComboPais = listaComboPais;
    }

    public List<Registro> getListaCiabas() {
        return listaCiabas;
    }

    public void setListaCiabas(List<Registro> listaCiabas) {
        this.listaCiabas = listaCiabas;
    }

    public String getCompaniaBase() {
        return companiaBase;
    }

    public void setCompaniaBase(String companiaBase) {
        this.companiaBase = companiaBase;
    }

    public int getIndicePatronales() {
        return indicePatronales;
    }

    public void setIndicePatronales(int indicePatronales) {
        this.indicePatronales = indicePatronales;
    }

    public List<Registro> getListaDpto() {
        return listaDpto;
    }

    public void setListaDpto(List<Registro> listaDpto) {
        this.listaDpto = listaDpto;
    }

    public List<Registro> getListaSubDepartamento() {
        return listaSubDepartamento;
    }

    public void setListaSubDepartamento(List<Registro> listaSubDepartamento) {
        this.listaSubDepartamento = listaSubDepartamento;
    }

    public List<Registro> getListaSubCiudad() {
        return listaSubCiudad;
    }

    public void setListaSubCiudad(List<Registro> listaSubCiudad) {
        this.listaSubCiudad = listaSubCiudad;
    }

    public List<Registro> getListaSubPais() {
        return listaSubPais;
    }

    public void setListaSubPais(List<Registro> listasubPais) {
        this.listaSubPais = listasubPais;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaPatronales() {
        return listaPatronales;
    }

    public void setListaPatronales(List<Registro> listaPatronales) {
        this.listaPatronales = listaPatronales;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getTituloTexto() {
        return tituloTexto;
    }

    public void setTituloTexto(String tituloTexto) {
        this.tituloTexto = tituloTexto;
    }

    /**
     * @return the listaTipoAportante
     */
    public List<Registro> getListaTipoAportante() {
        return listaTipoAportante;
    }

    /**
     * @param listaTipoAportante
     * the listaTipoAportante to set
     */
    public void setListaTipoAportante(List<Registro> listaTipoAportante) {
        this.listaTipoAportante = listaTipoAportante;
    }
}
