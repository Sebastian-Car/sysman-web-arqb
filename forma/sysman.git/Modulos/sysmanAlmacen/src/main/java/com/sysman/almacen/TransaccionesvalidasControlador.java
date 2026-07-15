package com.sysman.almacen;

import com.sysman.almacen.enums.TransaccionesvalidasControladorEnum;
import com.sysman.almacen.enums.TransaccionesvalidasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

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
 * @version 1, 14/01/2016
 *
 *
 * -- Modificado por lcortes 12,15/05/2017. Refactorizacion de codigo
 * de las listas para utilizar dss's.
 */
@ManagedBean
@ViewScoped
public class TransaccionesvalidasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private List<Registro> listaCodigo;
    private List<Registro> listaClaseBodega;
    private List<Registro> listaConcepto;

    /**
     * Creates a new instance of TransaccionesvalidasControlador
     */
    public TransaccionesvalidasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.TRANSACCIONESVALIDAS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(TransaccionesvalidasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = TransaccionesvalidasControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCodigo();
        cargarListaClaseBodega();
        cargarListaConcepto();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        TransaccionesvalidasControladorUrlEnum.URL0001
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public List<Registro> getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(List<Registro> listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public List<Registro> getListaClaseBodega() {
        return listaClaseBodega;
    }

    public void setListaClaseBodega(List<Registro> listaClaseBodega) {
        this.listaClaseBodega = listaClaseBodega;
    }

    public List<Registro> getListaConcepto() {
        return listaConcepto;
    }

    public void setListaConcepto(
        List<Registro> listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    public void cargarListaCodigo() {
        try {
            listaCodigo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionesvalidasControladorUrlEnum.URL4882
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TransaccionesvalidasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaClaseBodega() {

        try {
            listaClaseBodega = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionesvalidasControladorUrlEnum.URL4882
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TransaccionesvalidasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConcepto() {
        try {
            listaConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionesvalidasControladorUrlEnum.URL4447
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TransaccionesvalidasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
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
        registro.getCampos().remove("RNUM");
        registro.getCampos().remove("RID");
        registro.getCampos().remove("ORIGEN");
        registro.getCampos().remove("DESTINO");
        registro.getCampos().remove("NOMBRECONCEPTO");
        registro.getCampos().remove("NOMBRETIPOELEMENTO");
        registro.getCampos().remove("NOMBRECLASE");
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
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
