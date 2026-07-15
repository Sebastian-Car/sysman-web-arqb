package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.enums.PrepararTarifasControladorEnum;
import com.sysman.predial.enums.PrepararTarifasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario
 * 
 * @version 3, 14/07/2017
 * @author jreina se realizaron los cambios de refactoring.
 * 
 */

@ManagedBean
@ViewScoped
public class PrepararTarifasControlador extends BeanBaseModal {
    
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String anoFinal;
    private String anoInicial;
    private String incremento;
    private boolean dialogoVisible;
    private String desDialogo;
    
    @EJB
    private EjbPredialSeisRemote ejbPredialSeis;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of PrepararTarifasControlador
     */
    public PrepararTarifasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREPARAR_TARIFAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PrepararTarifasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        int ano = SysmanFunciones
                        .ano(new Date());
        anoInicial = String.valueOf(ano);
        anoFinal = String.valueOf(ano + 1);
        incremento = "0";
        dialogoVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (faltanCamposObligatorios()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB737"));
            return;
        }

        if (Integer.valueOf(anoFinal) < Integer.valueOf(anoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB738"));
            return;
        }
        
        StringBuilder sql = new StringBuilder("SELECT NUMERO FROM ANO WHERE ANO.COMPANIA =  '");
        sql.append(compania).append("' AND NUMERO = ").append(anoFinal);
        Long rsAnio;
        try {
            rsAnio = service.getConteoConsulta(sql.toString());
            
            if (rsAnio > 0) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(PrepararTarifasControladorEnum.PARAM0.getValue(),anoFinal);
                
                List<Registro> rs =  RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PrepararTarifasControladorUrlEnum.URL7080
                                                                                .getValue())
                                                .getUrl(), param));
                if (rs.isEmpty()) {
                    dialogoVisible = true;
                    desDialogo = idioma.getString("TB_TB2841")
                                    .replace("#ano#",
                                                    String.valueOf(anoFinal));

                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB984")
                                    .replace("#anoFinal#",
                                                    String.valueOf(anoFinal)));
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB982")
                                .replace("#anoFinal#", String.valueOf(anoFinal)));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

       

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Validacion de campos obligatorios.
     * 
     * @return Verdadero si hacen falta campos por diligenciar.
     */
    private boolean faltanCamposObligatorios() {
        return SysmanFunciones.validarVariableVacio(anoFinal)
            || SysmanFunciones.validarVariableVacio(anoInicial)
            || SysmanFunciones.validarVariableVacio(incremento);
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    public void cancelardecision() {
        dialogoVisible = false;
    }

    public void aceptardecision() {
        try {
            Long res = ejbPredialSeis.reemplazarTarifas(compania,
                            Integer.parseInt(anoFinal),
                            Integer.parseInt(anoInicial),
                            SessionUtil.getUser().getCodigo(),
                            new BigDecimal(incremento));
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB981")
                            .replace("#res#", String.valueOf(res)));
        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(PrepararTarifasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        dialogoVisible = false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getIncremento() {
        return incremento;
    }

    public void setIncremento(String incremento) {
        this.incremento = incremento;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public String getDesDialogo() {
        return desDialogo;
    }

    public void setDesDialogo(String desDialogo) {
        this.desDialogo = desDialogo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
