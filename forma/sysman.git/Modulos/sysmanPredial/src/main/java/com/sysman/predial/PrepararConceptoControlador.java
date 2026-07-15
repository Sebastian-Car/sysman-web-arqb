package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
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
 * @author dmaldonado
 * @version 1, 24/05/2016 08:19:34 -- Modificado por dmaldonado
 * 
 * @version 2, 13/07/2017
 * @author jreina se realizaron los cambios de refactoring.
 * 
 */

@ManagedBean
@ViewScoped
public class PrepararConceptoControlador extends BeanBaseModal {
    
    private final String compania;
  
    private String anoInicial;
    private String anoFinal; 
    
    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;

    /**
     * Creates a new instance of PrepararConceptoControlador
     */
    public PrepararConceptoControlador() {
        super();
        compania=SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREPARAR_CONCEPTO_CONTROLADOR.getCodigo();
            validarPermisos();       
        }
        catch (Exception ex) {
            Logger.getLogger(PrepararConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {     
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        int ano = SysmanFunciones.ano(new Date());
        anoInicial = String.valueOf(ano);
        anoFinal = String.valueOf(ano + 1);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        if (Integer.valueOf(anoFinal) < Integer.valueOf(anoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB723"));
            return;
        }
        try {
            int res= ejbPredialSiete.prepararConceptoAnio(compania,
                            Integer.parseInt(anoFinal),
                            Integer.parseInt(anoInicial),
                            SessionUtil.getUser().getCodigo());
            
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB979")
                            .replace("#numero#", String.valueOf(res)));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }
    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }
    
}
