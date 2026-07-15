package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 19/10/2015
 * 
 * @author eamaya
 * @version 2, 04/05/2017 Proceso de Refactoring
 * 
 * @author asana
 * @version 3, 12/06/2017 Se implementa enum en formulario.
 */
@ManagedBean
@ViewScoped

public class LocalizacionsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String rid;

    /**
     * Creates a new instance of LocalizacionsControlador
     */
    public LocalizacionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LOCALIZACIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(InvdevolinicialControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.LOCALIZACIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        actualizarAntes();
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/menu.sysman");
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

}
