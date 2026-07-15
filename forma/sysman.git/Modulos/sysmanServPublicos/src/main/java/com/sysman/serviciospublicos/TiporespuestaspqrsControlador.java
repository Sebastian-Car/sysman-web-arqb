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
import com.sysman.serviciospublicos.enums.TiporespuestaspqrsControladorEnum;
import com.sysman.util.SysmanFunciones;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jlozano
 * @version 1, 05/08/2016 09:46:43 -- Modificado por jlozano
 * 
 * @author jreina
 * @version 2, 13/06/2017 Cambio c�digo formulario y actualizaci�n de
 * ConnectorPool
 * 
 * @modified jguerrero
 * @version 3. 20/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class TiporespuestaspqrsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String tipo;

    private static final String CODIGO = GeneralParameterEnum.CODIGO.getName();

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of TiporespuestaspqrsControlador
     */
    public TiporespuestaspqrsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.TIPORESPUESTASPQRS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(TiporespuestaspqrsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        tabla = TiporespuestaspqrsControladorEnum.PARAM0.getValue();
        enumBase = GenericUrlEnum.SP_TIPORESPUESTA_PQR;

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
        parametrosListado.put(
                        TiporespuestaspqrsControladorEnum.PARAM2.getValue(),
                        tipo);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartipoPQR() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        generarConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(tipo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1063"));
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        TiporespuestaspqrsControladorEnum.PARAM2.getValue(),
                        tipo);
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
        String auxCodigo = SysmanFunciones
                        .nvl(registro.getCampos().get(CODIGO), "").toString();
        String auxDescripcion = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        if ("".equals(auxCodigo.trim())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1061"));
            auxRetorno = false;
        }
        if ("".equals(auxDescripcion.trim())) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1062"));
            auxRetorno = false;
        }
        // </CODIGO_DESARROLLADO>
        return auxRetorno;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        generarConsecutivo();

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
        // heredado del bean base

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        generarConsecutivo();
    }

    // <SET_GET_ATRIBUTOS>
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    private void generarConsecutivo() {
        try {

            String criterio = "COMPANIA=''" + compania + "'' AND TIPO=''" + tipo
                + "''";

            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            TiporespuestaspqrsControladorEnum.PARAM0.getValue(),
                            criterio, CODIGO, "1");

            registro.getCampos().put(CODIGO, consecutivo);
        }
        catch (SystemException e) {

            Logger.getLogger(TiporespuestaspqrsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
