package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author amonroy
 * @version 1, 01/08/2016
 * @modifier amonroy
 * @version 2, 16/05/2017 Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class BancosspsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    /**
     * Crea una nueva intancia de BancosspsControlador
     */
    public BancosspsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.BANCOSSPS_CONTROLADOR.getCodigo();
            validarPermisos();        
        }
        catch (Exception ex) {
            Logger.getLogger(BancosspsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {   
        enumBase = GenericUrlEnum.SP_BANCOS;
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
        /*
         * FR1018-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
