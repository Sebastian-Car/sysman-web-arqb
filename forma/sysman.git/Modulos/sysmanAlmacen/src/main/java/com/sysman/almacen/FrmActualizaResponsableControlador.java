package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Controlador para formulario que permite ejecutar el proceso de
 * actualización de responsables.
 * 
 * @author jrodrigueza
 * @version 1, 16/02/2016
 * 
 * @author jcrodriguez
 * @version 2, 25/04/2017 Depuracion del controlador, creacion de
 * funcion en pl, eliminacion de metodos
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */
@ManagedBean
@ViewScoped
public class FrmActualizaResponsableControlador extends BeanBaseModal {
    /**
     * variable que almacena la compa&ntilde;ia
     */
    private final String compania;
    /**
     * variable que almacena el predio
     */
    private static final String PREDIOS = "predios";
    /**
     * variable que almacena las vias
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
     * Creates a new instance of FrmActualizaResponsableControlador
     */
    public FrmActualizaResponsableControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTUALIZA_RESPONSABLE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
     * metodo que se llama al oprimir el boton de actualizar
     * 
     * @param ac
     */
    public void oprimirACTUALIZAR() {
        actualizarResponsable(true);
        actualizarResponsable(false);
    }

    /**
     * Ejecuta el procedimiento para actualizar el responsable de los
     * <b>Predios</b> o las <b>Vias</b>.
     * 
     * @param esPredio
     * Define si es un Predio o una Via.
     */
    private void actualizarResponsable(boolean esPredio) {
        String usuario = SessionUtil.getUser().getCodigo();
        try {
            int rta = ejbAlmacenCuatro.actualizarResponsablePredioVias(compania,
                            esPredio, usuario);
            mostrarMensaje(rta, esPredio);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB1997") + e.getMessage());
        }
    }

    /**
     * metodo que muestra los mensajes al usuario
     * 
     * @param aux
     * @param esPredio
     */
    private void mostrarMensaje(int aux, boolean esPredio) {
        if (aux == 0) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3091")
                            .replace("s$predio$s", esPredio ? PREDIOS : VIAS));
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3090")
                            .replace("s$registro$s", String.valueOf(aux)));
        }
    }

    /**
     * metodo que se llama al oprimir el boton de cancelar
     * 
     * @param ac
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
}
