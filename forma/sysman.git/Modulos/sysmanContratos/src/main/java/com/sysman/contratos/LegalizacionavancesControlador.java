package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.LegalizacionavancesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 13/11/2015
 * 
 * @author asana
 * @version 2, 10/08/2017 Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class LegalizacionavancesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Creates a new instance of LegalizacionavancesControlador
     */
    public LegalizacionavancesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LEGALIZACIONAVANCES_CONTROLADOR
                            .getCodigo();
            validarPermisos();

        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            logger.error(ex.getMessage(), ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "ORDENDECOMPRA";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        LegalizacionavancesControladorUrlEnum.URL5760
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LegalizacionavancesControladorUrlEnum.URL5358
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {

        if (Double.parseDouble(
                        registro.getCampos().get("VALORFINAL").toString()) > 0
            && !(registro.getCampos().get("FECHAFINALIZACION") == null ? ""
                : registro.getCampos().get("FECHAFINALIZACION")).toString()
                                .isEmpty()) {
            registro.getCampos().put("ESTADO", "T");
        }
        if (Double.parseDouble(registro.getCampos().get("VALORFINAL")
                        .toString()) > Double.parseDouble(
                                        registro.getCampos().get("VALORTOTAL")
                                                        .toString())) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2136"));
            return false;
        }

        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("CLASEORDEN");
        registro.getCampos().remove("SUCURSAL");
        registro.getCampos().remove("FECHA");
        registro.getCampos().remove("NUMERO");
        registro.getCampos().remove("TERCERO");
        registro.getCampos().remove("DEPENDENCIA");
        registro.getCampos().remove("VALORTOTAL");
        registro.getCampos().remove("COMPANIA");

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    @Override
    public void removerCombos() {
        // Metodo heredado
    }
}
