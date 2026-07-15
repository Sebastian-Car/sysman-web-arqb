package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
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
import com.sysman.precontractual.enums.EscriteriosFacProyControladorEnum;
import com.sysman.precontractual.enums.EscriteriosFacProyControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 20/05/2016
 *
 * @version 2, 02/06/2017 sdaza- mejorar consultas, verificar
 * validacion de permisos crear, editar o eliminar segun valor enviado
 * de formulario que invoca
 * 
 * @author eamaya
 * @version 3.0, 23/08/2017. Proceso de Refactoring DSS , cambio de
 * numero de fomulario por enum y ajuste a los redireccionamientos
 * 
 */
@ManagedBean
@ViewScoped

public class EscriteriosFacProyControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;

    private final String modulo;

    /** Constante a nivel de clase que aloja el valor ES_ESTPREVIO */
    private final String cEsEstprevio;

    /**
     * Constante a nivel de clase que aloja el valor ES_CRITERIOSFAC
     */
    private final String cEsCriterios;

    /** Constante a nivel de clase que aloja el valor COMPANIA */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja el valor CODIGOCRITERIO
     */
    private final String cCodigoCriterio;

    private Registro registroSubSubEsCriteriosFacProy;
    private Registro registroSubEsSubevaluacionProy;
    private List<Registro> listaSubescriteriosfacproy;
    private List<Registro> listaEssubevaluacionproy;

    private String codigoEstudio;
    private String tipoContratacion;
    private int totalPuntaje;
    private Map<String, Object> misParametros;
    private Map<String, Object> ridEstPrevios;
    private String vigenciaPeriodo;

    /**
     * Indicador que gestiona la visibilidad de los controles para
     * insertar, editar y eliminar
     */
    private boolean visualizar;

    public EscriteriosFacProyControlador() {
        super();
        compania = SessionUtil.getCompania();

        modulo = SessionUtil.getModulo();

        cEsEstprevio = "ES_ESTPREVIO";
        cEsCriterios = "ES_CRITERIOSFAC";
        cCompania = "COMPANIA";
        cCodigoCriterio = "CODIGOCRITERIO";

        try {
            numFormulario = GeneralCodigoFormaEnum.ESCRITERIOS_FAC_PROY_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            misParametros = SessionUtil.getFlash();

            if (misParametros != null) {
                codigoEstudio = (String) misParametros.get("txtCodEstudio");
                tipoContratacion = (String) misParametros
                                .get("tipoContratacion");
                visualizar = Boolean.parseBoolean(
                                misParametros.get("visualizar").toString());
                ridEstPrevios = (Map<String, Object>) misParametros.get("ridEstPrevios");
                vigenciaPeriodo = (String) misParametros.get("vigenciaPeriodo");
            }

            registroSubSubEsCriteriosFacProy = new Registro(
                            new HashMap<String, Object>());
            registroSubEsSubevaluacionProy = new Registro(
                            new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(EscriteriosFacProyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void abrirFormulario() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(EscriteriosFacProyControladorEnum.TIPO.getValue(),
                        tipoContratacion);

        List<Registro> lCriterios;
        try {
            lCriterios = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            EscriteriosFacProyControladorUrlEnum.URL10077
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (lCriterios != null) {
                agregarCriterio(lCriterios);
            }
            iniciarListasSub();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void agregarCriterio(List<Registro> lCriterios) {
        for (Registro criterio : lCriterios) {
            try {

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                criterio.getCampos().get(cCompania));
                param.put(EscriteriosFacProyControladorEnum.CODIGOESTUDIO
                                .getValue(), codigoEstudio);

                param.put(EscriteriosFacProyControladorEnum.TIPO.getValue(),
                                tipoContratacion);

                param.put(EscriteriosFacProyControladorEnum.CRITERIO.getValue(),
                                criterio.getCampos().get(cCodigoCriterio));
                Registro rCriterio = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                EscriteriosFacProyControladorUrlEnum.URL15446
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (rCriterio == null) {
                    Map<String, Object> insert = new TreeMap<>();
                    insert.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    insert.put("CODIGOESTUDIO", codigoEstudio);
                    insert.put("CODIGOMODALIDAD", tipoContratacion);
                    insert.put(cCodigoCriterio, criterio.getCampos()
                                    .get(cCodigoCriterio));
                    insert.put("PUNTAJE", "0");

                    insert.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());

                    insert.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    EscriteriosFacProyControladorUrlEnum.URL11385
                                                                    .getValue());
                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(), insert);
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    public void setTotalPuntaje(int totalPuntaje) {
        this.totalPuntaje = totalPuntaje;
    }

    public void ejecutarrcCerrar() {

        HashMap<String, Object> parametros = new HashMap<>();
                
        parametros.put("ridEstPrevios", ridEstPrevios);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(misParametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubescriteriosfacproy();
        cargarListaEssubevaluacionproy();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubescriteriosfacproy = null;
        listaEssubevaluacionproy = null;
    }

    @PostConstruct
    public void inicializar() {
        tabla = "";

        asignarOrigenDatos();

        abrirFormulario();

    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    public void cargarListaSubescriteriosfacproy() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(EscriteriosFacProyControladorEnum.CODIGOESTUDIO
                            .getValue(),
                            codigoEstudio);
            param.put(EscriteriosFacProyControladorEnum.TIPO.getValue(),
                            tipoContratacion);

            listaSubescriteriosfacproy = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscriteriosFacProyControladorUrlEnum.URL4860
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            cEsCriterios));

        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(EscriteriosFacProyControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        totalPuntaje = obtenerTotalPuntaje();
    }

    public void cargarListaEssubevaluacionproy() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(EscriteriosFacProyControladorEnum.CODIGOESTUDIO
                            .getValue(),
                            codigoEstudio);

            listaEssubevaluacionproy = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EscriteriosFacProyControladorUrlEnum.URL6976
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            cEsEstprevio));

        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(EscriteriosFacProyControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubSubescriteriosfacproy() {
        // METODO_NO_IMPLEMENTADO
    }

    public void editarRegSubSubescriteriosfacproy(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscriteriosFacProyControladorUrlEnum.URL8348
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscriteriosFacProyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubescriteriosfacproy();
        }
    }

    public void eliminarRegSubEsCriteriosFacProy(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    public void cancelarEdicionSubescriteriosfacproy() {
        cargarListaSubescriteriosfacproy();
        cargarListaEssubevaluacionproy();
    }

    public void agregarRegistroSubEssubevaluacionproy() {
        // METODO_NO_IMPLEMENTADO
    }

    public void editarRegSubEssubevaluacionproy(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EscriteriosFacProyControladorUrlEnum.URL7106
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(EscriteriosFacProyControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaEssubevaluacionproy();
        }
    }

    public void eliminarRegSubEsSubevaluacionProy(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    public void cancelarEdicionEssubevaluacionproy() {
        cargarListaEssubevaluacionproy();
    }

    public void oprimirCmbModNo(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codigoEstudio", codigoEstudio);
        parametros.put("codigoModalidad", tipoContratacion);
        parametros.put("codigoCriterio",
                        reg.getCampos().get(cCodigoCriterio).toString());
        parametros.put("titulo", reg.getCampos().get("NOMBRE").toString());
        parametros.put("vobo", misParametros.get("visualizar").toString());
        parametros.put("ridEstPrevios", ridEstPrevios);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ESFACTORESPORESTPROYS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    public int obtenerTotalPuntaje() {
        int total = 0;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(EscriteriosFacProyControladorEnum.CODIGOESTUDIO.getValue(),
                        codigoEstudio);

        param.put(EscriteriosFacProyControladorEnum.TIPO.getValue(),
                        tipoContratacion);

        Registro rTotal;
        try {
            rTotal = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            EscriteriosFacProyControladorUrlEnum.URL10152
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rTotal != null) {
                total = Integer.parseInt(SysmanFunciones.nvl(
                                rTotal.getCampos().get("TOTAL"), "0")
                                .toString());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return total;
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);

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

    public List<Registro> getListaSubescriteriosfacproy() {
        return listaSubescriteriosfacproy;
    }

    public void setListaSubescriteriosfacproy(
        List<Registro> listaSubescriteriosfacproy) {
        this.listaSubescriteriosfacproy = listaSubescriteriosfacproy;
    }

    public List<Registro> getListaEssubevaluacionproy() {
        return listaEssubevaluacionproy;
    }

    public void setListaEssubevaluacionproy(
        List<Registro> listaEssubevaluacionproy) {
        this.listaEssubevaluacionproy = listaEssubevaluacionproy;
    }

    public Registro getRegistroSubSubEsCriteriosFacProy() {
        return registroSubSubEsCriteriosFacProy;
    }

    public void setRegistroSubSubEsCriteriosFacProy(
        Registro registroSubSubEsCriteriosFacProy) {
        this.registroSubSubEsCriteriosFacProy = registroSubSubEsCriteriosFacProy;
    }

    public Registro getRegistroSubEsSubevaluacionProy() {
        return registroSubEsSubevaluacionProy;
    }

    public void setRegistroSubEsSubevaluacionProy(
        Registro registroSubEsSubevaluacionProy) {
        this.registroSubEsSubevaluacionProy = registroSubEsSubevaluacionProy;
    }

    public String getCodigoEstudio() {
        return codigoEstudio;
    }

    public void setCodigoEstudio(String codigoEstudio) {
        this.codigoEstudio = codigoEstudio;
    }

    public String getTipoContratacion() {
        return tipoContratacion;
    }

    public void setTipoContratacion(String tipoContratacion) {
        this.tipoContratacion = tipoContratacion;
    }

    public int getTotalPuntaje() {
        return totalPuntaje;
    }

    public boolean isVisualizar() {
        return visualizar;
    }

    public void setVisualizar(boolean visualizar) {
        this.visualizar = visualizar;
    }

}