package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
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
 * @version 2, 03/08/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Reemplazo del numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class CodigorupsControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que aloja el valor de la cadena
     * <code>CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Creates a new instance of CodigorupsControlador
     */
    public CodigorupsControlador() {
        super();

        cCodigo = GeneralParameterEnum.CODIGO.getName();

        try {
            // 180
            numFormulario = GeneralCodigoFormaEnum.CODIGORUPS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(CodigorupsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CODIGO_RUP;

        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        // <CODIGO_DESARROLLADO>
        buscarUrls();
        // </CODIGO_DESARROLLADO>
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
        if (validarCampos()) {
            return false;
        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica el valor del campo: <code>Codigo</code>.
     * 
     * @return <code>true</code>: Cuando el valor esta vacio.
     */
    private boolean validarCampos() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), cCodigo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3360"));
            return true;
        }

        return false;
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
        if (validarCampos()) {
            return false;
        }

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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
