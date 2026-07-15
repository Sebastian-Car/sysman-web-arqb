package com.sysman.general;

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
 * @author dmaldonado
 * @version 1, 14/08/2015
 * 
 * @author jlramirez
 * @version 2, 04/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 *
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario
 */
@ManagedBean
@ViewScoped
public class FrmasociacionesmunicipalesControlador
                extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of FrmasociacionesmunicipalesControlador
     */
    public FrmasociacionesmunicipalesControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMASOCIACIONESMUNICIPALES_CONTROLADOR
                        .getCodigo();

        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmasociacionesmunicipalesControlador.class
                            .getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ASOCIACIONESMUNICIPALES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
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
    public void removerCombos() {
        // NO SE IMPLEMENTA

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
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
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }
}
