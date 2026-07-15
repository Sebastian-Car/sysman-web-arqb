package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author dsuesca
 * @version 1, 31/08/2015
 * 
 * @author eamaya
 * @version 2.0 20/09/2017, Manejo de EJBs y cambio de numero de
 * formulario por enum
 */
@ManagedBean
@ViewScoped
public class FrmMayorizaAvanceControlador extends BeanBaseModal {

    private String compania;
    private String modulo;
    private String usuario;

    @EJB
    private EjbSysmanUtilRemote ejbSysmaUtl;

    @EJB
    private EjbBancoProyectoCincoRemote ejbBancosCinco;

    /**
     * Creates a new instance of FrmMayorizaAvanceControlador
     */
    public FrmMayorizaAvanceControlador() {
        super();
        try {
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            usuario = SessionUtil.getUser().getCodigo();
            numFormulario = GeneralCodigoFormaEnum.FRM_MAYORIZA_AVANCE_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmMayorizaAvanceControlador.class.getName())
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
            String valorParametro = ejbSysmaUtl.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo, new Date(),
                            false);

            BigDecimal respuesta = ejbBancosCinco.mayorizarAvance(compania,
                            Integer.parseInt(valorParametro), "0",
                            "999999999999999",
                            new BigDecimal(1), usuario);

            if ("1".equals(respuesta.toString())) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));
            }
        }
        catch (NullPointerException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB2410"), " ",
                            ex.getMessage()));
            Logger.getLogger(FrmMayorizaAvanceControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {

        // Metodo que se hereda del bean base
    }

}
