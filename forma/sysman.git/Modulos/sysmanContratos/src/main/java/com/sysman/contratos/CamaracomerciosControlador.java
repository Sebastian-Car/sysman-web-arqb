package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dcastro
 * @version 1, 15/09/2015
 * 
 * @author asana
 * @version 2, 08/08/2017 Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class CamaracomerciosControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of CamaracomerciosControlador
     */
    public CamaracomerciosControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.CAMARACOMERCIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(CamaracomerciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CAMARA_COMERCIO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
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

    @Override
    public void removerCombos() {
        // Metodo heredado
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

}
