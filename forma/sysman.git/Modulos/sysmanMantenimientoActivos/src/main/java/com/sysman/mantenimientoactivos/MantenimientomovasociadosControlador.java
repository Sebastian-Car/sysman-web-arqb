package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.MantenimientomovasociadosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author vmolano
 * @version 1, 15/11/2016
 *
 * @author lcortes
 * @version 2, 16,17/08/2017. Se realiza el proceso de refactorizacion
 * del codigo y reemplazo de llamados a la clase Acciones.
 */
@ManagedBean
@ViewScoped
public class MantenimientomovasociadosControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String cNumero;

    private RegistroDataModelImpl listaMovimiento;
    private RegistroDataModelImpl listaMovimientoE;
    private String auxiliar;
    private String anoActual;
    private String tipoActual;
    private String numeroActual;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of MantenimientomovasociadosControlador
     */
    public MantenimientomovasociadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNumero = "NUMERO";
        try {
            numFormulario = GeneralCodigoFormaEnum.MANTENIMIENTOMOVASOCIADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anoActual = (String) parametrosEntrada.get("ano");
                tipoActual = (String) parametrosEntrada.get("tipo");
                numeroActual = (String) parametrosEntrada
                                .get("numMantenimiento");
            }

        }
        catch (Exception ex) {
            Logger.getLogger(MantenimientomovasociadosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.MANTENIMIENTO_MOV_ASOCIADO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaMovimiento();
        cargarListaMovimientoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoActual);
        parametrosListado.put("TIPOCOMPROBANTE", tipoActual);
        parametrosListado.put("NUM_MANTENIMIENTO", numeroActual);

    }

    public void seleccionarFilaMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOMOVIMIENTO",
                        registroAux.getCampos().get("TIPOMOVIMIENTO"));
        registro.getCampos().put("NUM_MOVIMIENTO",
                        registroAux.getCampos().get(cNumero));
    }

    public void seleccionarFilaMovimientoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cNumero);
    }

    public RegistroDataModelImpl getListaMovimiento() {
        return listaMovimiento;
    }

    public void setListaMovimiento(RegistroDataModelImpl listaMovimiento) {
        this.listaMovimiento = listaMovimiento;
    }

    public RegistroDataModelImpl getListaMovimientoE() {
        return listaMovimientoE;
    }

    public void setListaMovimientoE(RegistroDataModelImpl listaMovimientoE) {
        this.listaMovimientoE = listaMovimientoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cargarListaMovimiento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MantenimientomovasociadosControladorUrlEnum.URL4826
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    public void cargarListaMovimientoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MantenimientomovasociadosControladorUrlEnum.URL6438
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaMovimientoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("TIPOCOMPROBANTE", tipoActual);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anoActual);
        registro.getCampos().put("NUM_MANTENIMIENTO", numeroActual);
        StringBuilder condicion = new StringBuilder();
        condicion.append(" COMPANIA = ''").append(compania)
                        .append("'' AND ANO = ").append(anoActual)
                        .append(" AND TIPOCOMPROBANTE = ''").append(tipoActual)
                        .append("'' AND NUM_MANTENIMIENTO = ")
                        .append(numeroActual);

        try {
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            GenericUrlEnum.MANTENIMIENTO_MOV_ASOCIADO
                                                            .getTable(),
                                            condicion.toString(), "CONSECUTIVO",
                                            "1"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarListaMovimiento();
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
        cargarListaMovimiento();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }
}
