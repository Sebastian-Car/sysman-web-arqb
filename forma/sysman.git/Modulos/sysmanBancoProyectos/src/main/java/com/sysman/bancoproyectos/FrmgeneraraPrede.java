package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.FrmgeneraraPredeEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author esarmiento
 * @version 1, 21/09/2015
 * @modified jguerrero
 * @version 2. 14/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped
public class FrmgeneraraPrede extends BeanBaseModal {

    private final String compania;

    private String vigencia;
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbBancoProyectoTresRemote ejbBancoProyTres;

    /**
     * Creates a new instance of FrmgeneraraPrede
     */
    public FrmgeneraraPrede() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMGENERARA_PREDE.getCodigo();

        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmgeneraraPrede.class.getName()).log(Level.SEVERE,
                            null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        try {
            vigencia = ejbSysmanUtil.consultarParametro(compania,
                            FrmgeneraraPredeEnum.VIGENCIA_GUBERNAMENTAL_ACTUAL
                                            .getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        abrirFormulario();
    }

    public void oprimirAceptar() {
        try {
            // <CODIGO_DESARROLLADO>
            if (SysmanFunciones.validarVariableVacio(vigencia)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3564"));
                return;
            }

            ejbBancoProyTres.generarPredecesorIndicativo(
                            compania, Integer.parseInt(vigencia),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2312"));

            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA),
                            ex.getMessage()));
            Logger.getLogger(FrmgeneraraPrede.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }
}
