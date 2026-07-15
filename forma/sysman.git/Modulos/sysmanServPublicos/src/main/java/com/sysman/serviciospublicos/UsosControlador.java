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
 * @author jguerrero
 * @version 1, 05/08/2016
 * 
 * @version 2.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * <code>ConectorPool.ESQUEMA_SYSMAN</code>.
 * 
 * @version 3, 20/06/2017
 * @author jreina se realizaron los cambios de refactoring.
 */

@ManagedBean
@ViewScoped
public class UsosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean actaSusp;
    private String tituloUsos;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of UsosControlador
     */
    public UsosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo=SessionUtil.getModulo();
        try {
            // 1036
            numFormulario = GeneralCodigoFormaEnum.USOS_CONTROLADOR.getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(UsosControlador.class.getName()).log(Level.SEVERE,
                            null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase=GenericUrlEnum.SP_USOS;
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
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
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "DEFINIR USOS CON ACTAS DE SUSPENSION",
                                            modulo, new Date(),
                                            true),
                                            "NO"))) {
                actaSusp = true;
            }
            else {
                actaSusp = false;
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO",
                                            modulo, new Date(), true),
                                            "NO")))

            {
                tituloUsos = "USOS DE " + SysmanFunciones
                                .nvl(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO",
                                                modulo, new Date(), true), "");
            }
            else {
                tituloUsos = idioma.getString("TT_FR1036");
            }

        }
        catch (SystemException e) {
            Logger.getLogger(UsosControlador.class.getName()).log(Level.SEVERE,
                            null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }
    // <SET_GET_ATRIBUTOS>

    public boolean isActaSusp() {
        return actaSusp;
    }

    public String getTituloUsos() {
        return tituloUsos;
    }

    public void setTituloUsos(String tituloUsos) {
        this.tituloUsos = tituloUsos;
    }

    public void setActaSusp(boolean actaSusp) {
        this.actaSusp = actaSusp;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
