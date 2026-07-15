package com.sysman.contabilidad;

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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author apineda
 * @version 1, 14/04/2016
 * @version 2, 17/04/2017 mzanguna Refactorizaci�n, ajustes sonar.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class TipopagosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    private boolean verClasePago;

    /**
     * Creates a new instance of TipopagosControlador
     */
    public TipopagosControlador() {
        super();
        compania = SessionUtil.getCompania();

        if (SessionUtil.getMenuActual().equals("10128")) {
            verClasePago = true;
        }
        else {
            verClasePago = false;
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPOPAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(TipopagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPOPAGO;
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
         * FR622-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name DoCmd.Restore Me.Requery End Sub
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        registro.getCampos().remove("NOMBRE_CLASE");

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.sysman.beanbase.BeanBaseContinuoAcme#asignarValoresRegistro
     * ()
     */
    @Override
    public void asignarValoresRegistro() {
        // Metodo para asignar valores al registro

    }

    public boolean isVerClasePago() {
        return verClasePago;
    }

    public void setVerClasePago(boolean verClasePago) {
        this.verClasePago = verClasePago;
    }

}
