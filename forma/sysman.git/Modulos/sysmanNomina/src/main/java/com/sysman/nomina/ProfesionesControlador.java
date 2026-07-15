package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cmanrique
 * 
 * 
 * @author eamaya
 * @version 2.0, 23/10/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 */
@ManagedBean
@ViewScoped

public class ProfesionesControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Creates a new instance of ProfesionesControlador
     */
    public ProfesionesControlador() {
        super();

        numFormulario = GeneralCodigoFormaEnum.PROFESIONES_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PROFESIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     */
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

        registro.getCampos().remove("TIPO_LB");
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
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }
}
