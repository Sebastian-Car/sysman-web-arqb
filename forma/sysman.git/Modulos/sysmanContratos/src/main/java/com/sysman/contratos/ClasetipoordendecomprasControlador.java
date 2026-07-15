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
 * @author dsuesca
 * @version 1, 15/09/2015
 * 
 * @author eamaya
 * @version 2.0, 03/08/2017 , Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped
public class ClasetipoordendecomprasControlador
                extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of ClasetipoordendecomprasControlador
     */
    public ClasetipoordendecomprasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.CLASETIPOORDENDECOMPRAS_CONTROLADOR
                        .getCodigo();
        SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ClasetipoordendecomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CLASETIPOORDENDECOMPRA;
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

}
