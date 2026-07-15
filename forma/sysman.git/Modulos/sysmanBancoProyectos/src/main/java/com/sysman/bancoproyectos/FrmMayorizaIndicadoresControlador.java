package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
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
 *
 * @author dsuesca
 * @version 1, 04/09/2015
 * 
 * @author eamaya
 * @version 2.0, 20/09/2017 Manejo de EJBs y cambio de numero de
 * formulario por enum
 * 
 */
@ManagedBean
@ViewScoped
public class FrmMayorizaIndicadoresControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String usuario;

    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;

    /**
     * Creates a new instance of FrmMayorizaIndicadoresControlador
     */
    public FrmMayorizaIndicadoresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        numFormulario = GeneralCodigoFormaEnum.FRM_MAYORIZA_INDICADORES_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmMayorizaIndicadoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        abrirFormulario();

    }

    public void oprimirAceptar() {
        try {
            // <CODIGO_DESARROLLADO>

            // Ejecutar la funcion
            ejbBancoProyectoCinco
                            .actualizarPlanIndicativo(
                                            compania, Integer.parseInt(modulo),
                                            Long.parseLong("0"),
                                            Long.parseLong("999999999999999"),
                                            "",
                                            usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));

            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2430"));
            Logger.getLogger(FrmMayorizaIndicadoresControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

}
