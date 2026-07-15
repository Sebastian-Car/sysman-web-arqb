package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TipoadjudicacionsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ybecerra
 * @version 1, 11/09/2015
 * 
 * @version 2.0, 12/06//2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class TipoadjudicacionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String cNombre;
    private final String cInfAuditoria;

    private List<Registro> listaCodigoPrecontrato;

    /**
     * Creates a new instance of TipoadjudicacionsControlador
     */
    public TipoadjudicacionsControlador() {
        super();
        // 173
        numFormulario = GeneralCodigoFormaEnum.TIPOADJUDICACIONS_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        cNombre = "NOMBRE";
        cInfAuditoria = "INFAUDITORIA";
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(TipoadjudicacionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.TIPO_ADJUDICACIONES;

        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        reasignarOrigen();
        cargarListaCodigoPrecontrato();
        abrirFormulario();
    }

    public List<Registro> getListaCodigoPrecontrato() {
        return listaCodigoPrecontrato;
    }

    public void setListaCodigoPrecontrato(
        List<Registro> listaCodigoPrecontrato) {
        this.listaCodigoPrecontrato = listaCodigoPrecontrato;
    }

    public void cargarListaCodigoPrecontrato() {

        try {
            listaCodigoPrecontrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipoadjudicacionsControladorUrlEnum.URL2172
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cInfAuditoria);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cInfAuditoria);
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cInfAuditoria);
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }
}
