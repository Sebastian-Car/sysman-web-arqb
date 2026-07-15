package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.kernel.templates.annotations.Refactoring;
import com.sysman.mantenimientoactivos.enums.PeriodoControladorEnum;
import com.sysman.mantenimientoactivos.enums.PeriodoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

/**
 *
 * @author ngomez
 * @version 1, 25/09/2015
 *
 * @author lcortes
 * @version 2, Refactorizacion del codigo y revision de observaciones
 * de la herramienta SonarLint.
 */
@ManagedBean
@ViewScoped
@Refactoring
public class PeriodoControlador extends BeanBaseModal {

    private final String compania;
    private String mes;
    private String tipo;
    private String anio;
    private List<Registro> listaTipo;
    private List<Registro> listaAno;

    /**
     * Creates a new instance of PeriodoControlador
     */
    public PeriodoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PeriodoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        cargarListaTipo();
        abrirFormulario();
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoControladorUrlEnum.URL2047
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PeriodoControladorEnum.CLASECONTABLE.getValue(), "M");

        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodoControladorUrlEnum.URL2370
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(anio) < 1900) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2489"));
        }
        else {
            Map<String, Object> parametros = new HashMap<>();
            StringBuilder nombreTipo = new StringBuilder();
            nombreTipo.append(tipo).append(" - ").append(service.buscarEnLista(
                            tipo, "CODIGO", "NOMBRE", listaTipo));
            parametros.put("pmes", mes);
            parametros.put("panio", anio);
            parametros.put("tipo", tipo);
            parametros.put("tipoNombre", nombreTipo);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.PROGRAMACIONMANTENIMIENTOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            RequestContext.getCurrentInstance().closeDialog(direccionador);

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Date fechaActual;
        fechaActual = new Date();
        anio = String.valueOf(SysmanFunciones.getParteFecha(fechaActual,
                        Calendar.YEAR));
        mes = String.valueOf(SysmanFunciones.getParteFecha(fechaActual,
                        Calendar.MONTH)
            + 1);
        tipo = "AUT";
        // </CODIGO_DESARROLLADO>
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public String getCompania() {
        return compania;
    }

}
