package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Controlador para formulario que permite ejecutar el proceso de
 * actualización de la vida &uacute;til.
 * 
 * @author jrodrigueza
 * @version 1, 17/02/2016
 * 
 * @author jcrodriguez
 * @version 2, 25/04/2017 Depuracion del controlador, creacion de
 * funcion en pl, se eliminaron algunos metodos del controlador
 * 
 * @author lcortes
 * @version 3, 12/06/2017 Se reemplaza el valor del atributo numero de
 * formulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class FrmActualizaVidaUtilR extends BeanBaseModal {
    /**
     * variable que almacena la compa&ntilde;ia
     */
    private final String compania;
    /**
     * variable que almacena el predio
     */
    private static final String PREDIOS = "predio";
    /**
     * variable que almacena la via
     */
    private static final String VIAS = "vias";
    /**
     * Implementacion del EJB de almacen para hacer el llamado a las
     * funciones que se invocan dentro del controlador y se encuentran
     * almacenadas en el paquete PCK_ALMACEN_COM4
     */
    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    /**
     * Creates a new instance of FrmActualizaVidaUtilR
     */
    public FrmActualizaVidaUtilR() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTUALIZA_VIDA_UTIL_R
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmActualizaVidaUtilR.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    /**
     * metodo que se llama al oprimir el boton de aceptar
     */
    public void oprimirACTUALIZAR() {
        actualizarPredioVia(true);
        actualizarPredioVia(false);
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1992"));
    }

    /**
     * metodo que contiene la logica para actualizar predios y vias
     * 
     * @param isPredio
     * Define si es un Predio o una Via.
     */
    private void actualizarPredioVia(boolean isPredio) {
        String usuario = SessionUtil.getUser().getCodigo();
        try {
            int rta = ejbAlmacenCuatro.actualizarVidaUtilR(compania, isPredio,
                            usuario);
            if (rta > 0) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3093")
                                .replace("s$predioVias$s",
                                                String.valueOf(rta))
                                .replace("s$nombre$s",
                                                isPredio ? PREDIOS : VIAS));
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3094")
                                .replace("s$predio$s",
                                                isPredio ? PREDIOS : VIAS));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB1991") + e.getMessage());
        }
    }

    /**
     * metodo que se llama al oprimir el boton de cancelar
     */
    public void oprimirCANCELAR() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    /**
     * metodo get y set
     */
    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}
