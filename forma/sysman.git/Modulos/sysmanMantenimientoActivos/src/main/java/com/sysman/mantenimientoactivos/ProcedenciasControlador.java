package com.sysman.mantenimientoactivos;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;

/**
 *
 * @author ngomez
 * @version 1, 16/09/2015
 * 
 * @author eamaya
 * @version 2.0, 16/08/2017, Proceso de Refactoring DSS y cambio de
 * numero de fomulario por enum
 * 
 */
@ManagedBean
@ViewScoped
public class ProcedenciasControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of ProcedenciasControlador
     */
    public ProcedenciasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.PROCEDENCIAS_CONTROLADOR
                        .getCodigo();

        try {
            validarPermisos();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PROCEDENCIA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
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
        // Metodo heredado de la clase BeanBase

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
        // Metodo heredado de la clase BeanBase

    }
}
