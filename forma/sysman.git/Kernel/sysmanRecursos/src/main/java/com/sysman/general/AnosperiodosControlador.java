package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ybecerra
 * @version 1, 04/03/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 03/04/2017
 * 
 * @author asana
 * @version 5, 12/06/2017 Redireccion de formulario.
 */
@ManagedBean
@ViewScoped
public class AnosperiodosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private boolean iniciaContabilidad;

    /**
     * Creates a new instance of AnosperiodosControlador
     */
    public AnosperiodosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ANOSPERIODOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AnosperiodosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.ANO;
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

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        iniciaContabilidad = SessionUtil.getModulo().equals("1");
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
        registro.getCampos().remove("NOMBREESTADO");

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

        registro.getCampos().remove("NOMBREESTADO");
        registro.getCampos().remove("NOMBREMODELO");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
    }


    public void cerrarFormulario() {

        if (SessionUtil.getMenuActual().equals("6012501")) {
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        else {
            SessionUtil.redireccionar("/diabloqueo.sysman");
        }
    }

    public void ejecutarrcCerrar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @return the iniciaContabilidad
     */
    public boolean isIniciaContabilidad() {
        return iniciaContabilidad;
    }

    /**
     * @param iniciaContabilidad
     * the iniciaContabilidad to set
     */
    public void setIniciaContabilidad(boolean iniciaContabilidad) {
        this.iniciaContabilidad = iniciaContabilidad;
    }

}
