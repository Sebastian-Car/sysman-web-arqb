
package com.sysman.bancoproyectos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author lcortes
 * @version 1, 17/09/2015
 * 
 * @author asana
 * @version 2, 22/09/2017, 29/09/2017 se realiza refactoring de
 * controlador.
 */
@ManagedBean
@ViewScoped
public class FrmtiponovedadtecnicasControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private boolean muestraRegistro;
    private String menuActual;

    /**
     * Creates a new instance of FrmtiponovedadtecnicasControlador
     */
    public FrmtiponovedadtecnicasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMTIPONOVEDADTECNICAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            menuActual = SessionUtil.getMenuActual();

            switch (menuActual) {
            case "52020102":
            case "52020402":
                muestraRegistro = false;
                break;
            case "52020101":
            case "520112":
                muestraRegistro = true;
                break;
            default:
                SessionUtil.redireccionarMenu();
                break;
            }

        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmtipofuenterecursosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BPTIPOSNOVEDADTECNICA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public boolean isMuestraRegistro() {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro) {
        this.muestraRegistro = muestraRegistro;
    }

    @Override
    public void abrirFormulario() {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("TIPOLB");
        registro.getCampos().remove("COMPANIA");
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        // NO ESTA IMPLEMENTADO
    }

}