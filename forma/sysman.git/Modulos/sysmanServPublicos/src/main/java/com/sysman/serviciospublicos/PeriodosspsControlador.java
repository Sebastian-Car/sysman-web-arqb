package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.PeriodosspsControladorEnum;
import com.sysman.serviciospublicos.enums.PeriodosspsControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author amonroy
 * @version 1, 02/08/2016
 * @modified jguerrero
 * @version 2. 14/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class PeriodosspsControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private static final String MESCONS = GeneralParameterEnum.MES.getName();

    private List<Registro> listaAno;

    /**
     * Creates a new instance of PeriodosspsControlador
     */
    public PeriodosspsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODOSSPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodosspsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.SP_PERIODO;
        tabla = PeriodosspsControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();

        registro = new Registro();
        cargarListaAno();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosspsControladorUrlEnum.URL4104
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

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

        if (!validaciones()) {
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validaciones() {
        boolean rta = true;
        if (Integer.parseInt(
                        registro.getCampos().get(MESCONS).toString()) > 12) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3213"));
            rta = false;
        }
        StringBuilder mes = new StringBuilder();

        if ((registro.getCampos().get(MESCONS).toString()).length() == 1) {
            mes.append(0).append(registro.getCampos().get(MESCONS));
            registro.getCampos().put(MESCONS, mes.toString());
        }
        return rta;

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
        if (!validaciones()) {
            return false;
        }
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

    public void cerrarFormulario() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

}
