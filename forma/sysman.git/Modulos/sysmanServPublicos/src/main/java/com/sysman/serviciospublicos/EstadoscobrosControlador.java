package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
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

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jlozano
 * @version 1, 05/08/2016 11:51:07 -- Modificado por jlozano
 * 
 * @author eamaya
 * @version 2, 18/05/2017 Proceso de Refactoring Y Manejo de EJBs
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 */
@ManagedBean
@ViewScoped
public class EstadoscobrosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean codEquiVisible;
    private boolean pertAVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of EstadoscobrosControlador
     */
    public EstadoscobrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1033
            numFormulario = GeneralCodigoFormaEnum.ESTADOSCOBROS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(EstadoscobrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_ESTADOSCOBRO;

        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            String par = SysmanFunciones.nvlStr(
                            ejbParametro.consultarParametro(compania,
                                            "MANEJA TERMINAL DOLPHIN 7600",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "NO");
            if ("SI".equals(par)) {
                codEquiVisible = true;
            }
            else {
                codEquiVisible = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(EstadoscobrosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        String nitCompania = SysmanFunciones
                        .extraerNIT(SessionUtil.getCompaniaIngreso().getNit());
        switch (nitCompania) {
        case "844000755": // YOPAL
        case "832000776": // FUNZA
        case "832001512": // MADRID
        case "899999714": // CHIA
        case "890680053": // FUSAGASUGA
            pertAVisible = true;
            break;
        default:
            pertAVisible = false;
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        boolean auxRetorno = true;
        String auxCodigo = (String) SysmanFunciones
                        .nvl(registro.getCampos().get("CODIGO"), "");
        if (auxCodigo.trim().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1061"));
            auxRetorno = false;
        }
        registro.getCampos().remove("NOMBREPERTENECE");
        // </CODIGO_DESARROLLADO>
        return auxRetorno;
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

        registro.getCampos().remove("NOMBREPERTENECE");

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isCodEquiVisible() {
        return codEquiVisible;
    }

    public void setCodEquiVisible(boolean codEquiVisible) {
        this.codEquiVisible = codEquiVisible;
    }

    public boolean isPertAVisible() {
        return pertAVisible;
    }

    public void setPertAVisible(boolean pertAVisible) {
        this.pertAVisible = pertAVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
