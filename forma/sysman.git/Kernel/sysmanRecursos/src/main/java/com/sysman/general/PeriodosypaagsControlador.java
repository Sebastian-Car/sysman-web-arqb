package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.PeriodosypaagsControladorEnum;
import com.sysman.general.enums.PeriodosypaagsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dsuesca
 * @version 1, 16/09/2015
 *
 * Refactoring
 * @author ybecerra
 * @version 2, 05/04/2017
 */
@ManagedBean
@ViewScoped

public class PeriodosypaagsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;

    private final String strEstado;
    private final String strNumero;
    private final String strCompania;
    // <DECLARAR_ATRIBUTOS>
    private String estado;
    private String ano;
    private String nuevoAnio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private boolean esContabilidad;
    private boolean esPresupuesto;
    private boolean esAlmacen;

    @EJB
    private EjbGeneralesRemote ejbGenerales;

    /**
     * Creates a new instance of PeriodosypaagsControlador
     */
    public PeriodosypaagsControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strEstado = "ESTADO";
        strNumero = "NUMERO";
        strCompania = "COMPANIA";

        esContabilidad = "1".equals(modulo) ? true : false;
        esPresupuesto = "3".equals(modulo) ? true : false;
        esAlmacen = "10".equals(modulo) ? true : false;

        try {
            numFormulario = GeneralCodigoFormaEnum.PERIODOSYPAAGS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.MES;
        buscarLlave();
        cargarListaanio();
        Date sysdate = new Date();
        ano = String.valueOf(SysmanFunciones.ano(sysdate));
        estado = service.buscarEnLista(ano, strNumero, strEstado, listaanio);
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosypaagsControladorUrlEnum.URL7258
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirActualizar() {
        try {
            // <CODIGO_DESARROLLADO>
            ejbGenerales.calcularPaag(compania, Integer.parseInt(ano));

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(PeriodosypaagsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void retornarFormularioAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosypaagsControladorUrlEnum.URL7258
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        Date sysdate = new Date();
        ano = String.valueOf(SysmanFunciones.ano(sysdate));

        estado = service.buscarEnLista(ano, strNumero, strEstado, listaanio);
        cargarForma();
        reasignarOrigen();
    }

    public void cambiaranio() {
        // cambiar a�o
        cargado = false;
        estado = service.buscarEnLista(ano, strNumero, strEstado, listaanio);
        ano = (ano == null) ? "" : ano;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarestado() {
        // Cambiar estado
        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ANO_ESTADO
                                                            .getUpdateKey());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(PeriodosypaagsControladorEnum.PARAM0.getValue(),
                            compania);
            fields.put(PeriodosypaagsControladorEnum.PARAM1.getValue(), ano);
            fields.put(GeneralParameterEnum.ESTADO.getName(), estado);
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
            cargarListaanio();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB875"));
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(PeriodosypaagsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Metodo ejecutado al cambiar el control estado en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarestadoC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String mayorEnLista() {
        return listaanio.get(0).getCampos().get(strNumero)
                        .toString();
    }

    public void aceptarNuevoAnio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR197-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name Me.numero.Requery DoCmd.Restore
         * End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(strCompania, compania);

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove("PAAG_ANUAL");
        registro.getCampos().remove("PAAG_ACUMULADO");
        registro.getCampos().remove("ESTADOCONCILIACIONLB");
        registro.getCampos().remove("ESTADOALMACENLB");
        registro.getCampos().remove("ESTADOPRESUPUESTOLB");
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAno() {
        SessionUtil.cargarModalDatos("557", modulo);
    }

    @Override
    public void removerCombos() {
        // Actualmente no se requiere remover combos.

    }

    @Override
    public void asignarValoresRegistro() {
        // Actualmente no se requiere asignar valores.
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isEsContabilidad() {
        return esContabilidad;
    }

    public void setEsContabilidad(boolean esContabilidad) {
        this.esContabilidad = esContabilidad;
    }

    public boolean isEsPresupuesto() {
        return esPresupuesto;
    }

    public void setEsPresupuesto(boolean esPresupuesto) {
        this.esPresupuesto = esPresupuesto;
    }

    public boolean isEsAlmacen() {
        return esAlmacen;
    }

    public void setEsAlmacen(boolean esAlmacen) {
        this.esAlmacen = esAlmacen;
    }

    /**
     * Retorna la variable nuevoAnio
     *
     * @return nuevoAnio
     */
    public String getNuevoAnio() {
        return nuevoAnio;
    }

    /**
     * Asigna la variable nuevoAnio
     *
     * @param nuevoAnio
     * Variable a asignar en nuevoAnio
     */
    public void setNuevoAnio(String nuevoAnio) {
        this.nuevoAnio = nuevoAnio;
    }

    /**
     * Retorna la variable estado
     *
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna la variable estado
     *
     * @param estado
     * Variable a asignar en estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaanio
     *
     * @return listaanio
     */
    public List<Registro> getListaanio() {
        return listaanio;
    }

    /**
     * Asigna la lista listaanio
     *
     * @param listaanio
     * Variable a asignar en listaanio
     */
    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }
}
