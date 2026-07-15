package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.ejb.EjbContratosUnoGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanConstantes;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ybecerra
 * @version 1, 07/10/2015
 *
 * @version 2, 11/08/2017 jrodriguezr Se refactoriza el c�digo SQL de
 * las listas para utilizar DSS. Tambi�n los llamados a funciones,
 * procedimientos y m�todos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 * 
 * @author jcaceres
 * @version 2.5, 05/03/2018 se crea una validacion del formulario para
 * poder mostrar dos columnas mas en el, siempre y cuando coinida con
 * la ruta definida en la validación
 *
 */
@ManagedBean
@ViewScoped
public class SectoresControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * 
     * constante de clase que contiene el codigo del modulo desde el
     * cual es usuario inicio sesion.
     * 
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * la variable menu recibe el valor del menu actual, es decir el
     * codigo del menu por el cual es accedido el formulario
     */
    private String menu;
    /**
     * indicador que controla la visibilidad del campo SECTOR_PI
     */
    private boolean verSectorPi;

    /**
     * la variable campoVisible recibe el valor de falso o verdadero
     */
    private boolean campoVisible;
    /**
     * la variable verSectorAppui recibe el valor de falso o verdadero
     */
    private boolean verSectorAppui;
    @EJB
    private EjbContratosUnoGeneralRemote ejbContratosUno;

    /**
     * Creates a new instance of SectoresControlador
     */
    public SectoresControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.SECTORES_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        menu = SessionUtil.getMenuActual();

        try {
            validarPermisos();

        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SectoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SECTORES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        cargarForma();

        if (Integer.toString(SysmanConstantes.MODULO_BANCOPROY)
                        .equals(modulo)
            || Integer.toString(SysmanConstantes.MODULO_PLAN_DE_DESARROLLO)
                            .equals(modulo)) {
            verSectorPi = true;
        }

        /**
         * en este ciclo se compara el valor de la variable menu, co
         * el menu asignado por defecto en el IF en donde si
         * corresponde el valor haga visible columnas en el formulario
         */
        if ("670170".equals(menu)) {
            campoVisible = true;
        }
        
        if ("90103".equals(menu)) {
        	verSectorAppui = true;
        }

        if (listaInicial.isVacio()) {
            try {
                ejbContratosUno.insertarSectoresDefault(compania,
                                SessionUtil.getUser().getCodigo());
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
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
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        buscarUrls();
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Retorna la variable Menu
     * 
     * @return Menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * Asigna la variable Menu
     * 
     * @param Menu
     * Variable a asignar en Menu
     */

    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * Retorna la variable CampoVisible
     * 
     * @return CampoVisible
     */
    public boolean isCampoVisible() {
        return campoVisible;
    }

    /**
     * Asigna la variable CampoVisible
     * 
     * @param CampoVisible
     * Variable a asignar en CampoVisible
     */
    public void setCampoVisible(boolean campoVisible) {
        this.campoVisible = campoVisible;
    }

    /**
     * @return the verSectorPi
     */
    public boolean isVerSectorPi() {
        return verSectorPi;
    }

    /**
     * @param verSectorPi
     * the verSectorPi to set
     */
    public void setVerSectorPi(boolean verSectorPi) {
        this.verSectorPi = verSectorPi;
    }
    
    /**
     * @return the verSectorAppui
     */
    public boolean isVerSectorAppui() {
        return verSectorAppui;
    }

    /**
     * @param verSectorAppui
     * the verSectorAppui to set
     */
    public void setVerSectorAppui(boolean verSectorAppui) {
        this.verSectorAppui = verSectorAppui;
    }

}
