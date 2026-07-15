package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.TipocomprobppsControladorEnum;
import com.sysman.presupuesto.enums.TipocomprobppsControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 17/06/2016
 *
 *
 * @author ybecerra
 * @version 2, 21/04/2017 Refactoring
 */
@ManagedBean
@ViewScoped

public class TipocomprobppsControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String codigocons;
    private final String consecutivoTcpCons;
    private final String tipoComprobanteCons;
    private boolean bloqueadoCodigo; 
    private RegistroDataModelImpl listaClase;
    private RegistroDataModelImpl listaSubconsecutivotcp;
    private Registro registroSub;

    public TipocomprobppsControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigocons = GeneralParameterEnum.CODIGO.getName();
        consecutivoTcpCons = TipocomprobppsControladorEnum.PARAM1.getValue();
        tipoComprobanteCons = TipocomprobppsControladorEnum.PARAM0.getValue();
        try {
            numFormulario = 921;
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(TipocomprobppsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {    
        cargarListaClase();   
    }

    @Override
    public void iniciarListasSub() { 
        cargarListaSubconsecutivotcp();
    }

    @Override
    public void iniciarListasSubNulo() {    
        listaSubconsecutivotcp = null;

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_COMPROBPP;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaSubconsecutivotcp() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTCP
                                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                            registro.getCampos().get(codigocons));

            listaSubconsecutivotcp = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            consecutivoTcpCons));
        }
        catch (SysmanException e) {
       
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaClase() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TipocomprobppsControladorUrlEnum.URL5704
                                                        .getValue());
        listaClase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, codigocons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaClase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASE",
                        registroAux.getCampos().get(codigocons));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubconsecutivotcp() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(tipoComprobanteCons,
                            registro.getCampos().get(codigocons));
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTCP
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            listaSubconsecutivotcp.load();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubconsecutivotcp(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getLlave().put(TipocomprobppsControladorEnum.PARAM2.getValue(),
                            registro.getCampos().get(codigocons));
            reg.getCampos().put("MODIFIED_BY",
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put("DATE_MODIFIED", new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTCP
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            listaSubconsecutivotcp.load();
        }
    }

    public void eliminarRegSubSubconsecutivotcp(Registro reg) {
        try {

            reg.getLlave().put(TipocomprobppsControladorEnum.PARAM2.getValue(),
                            registro.getCampos().get(codigocons));
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSECUTIVOTCP
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            listaSubconsecutivotcp.load();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cancelarEdicionSubconsecutivotcp() {
        cargarListaSubconsecutivotcp();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR921-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 3, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            bloqueadoCodigo = true;
        }
        else {
            bloqueadoCodigo = false;
        }
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

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

    // <SET_GET_ATRIBUTOS>

    public boolean isBloqueadoCodigo() {
        return bloqueadoCodigo;
    }

    public void setBloqueadoCodigo(boolean bloqueadoCodigo) {
        this.bloqueadoCodigo = bloqueadoCodigo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaClase() {
        return listaClase;
    }

    public void setListaClase(RegistroDataModelImpl listaClase) {
        this.listaClase = listaClase;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub() {
        return registroSub;
    }

    public RegistroDataModelImpl getListaSubconsecutivotcp() {
        return listaSubconsecutivotcp;
    }

    public void setListaSubconsecutivotcp(
        RegistroDataModelImpl listaSubconsecutivotcp) {
        this.listaSubconsecutivotcp = listaSubconsecutivotcp;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
}
