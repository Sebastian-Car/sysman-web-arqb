package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.ControlPagosControladorEnum;
import com.sysman.contratos.enums.ControlPagosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 09/11/2015
 * 
 * @author asana
 * @version 2, 08/08/2017, Se realiza refactoring
 */
@ManagedBean
@ViewScoped
public class ControlPagosControlador extends BeanBaseModal
{

    private final String compania;
    private final String moduloContratos;

    /**
     * Constante que almacenara el valor de la cadena "NUMERO"
     */
    private final String numeroC;

    /**
     * Constante que almacenara la cadena "9999999999999999"
     */
    private final String constanteNueveC;

    /**
     * Constante que almacenara la cadena "90313"
     */
    private final String constanteNumMenuC;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private RegistroDataModelImpl listaTipoContratoFinal;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private StreamedContent archivoDescarga;
    private final String menuActual;
    private String nombreContratoInicial;
    private String nombreContratoFinal;
    private String tituloModal;
    private String tituloForm;
    private String labelInicial;
    private String labelFinal;

    /**
     * Creates a new instance of ControlPagosControlador
     */
    public ControlPagosControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.CONTROL_PAGOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        numeroC = "NUMERO";
        constanteNueveC = "9999999999999999";
        constanteNumMenuC = "90313";
        menuActual = SessionUtil.getMenuActual();
        tituloModal = menuActual.equals(constanteNumMenuC) ? "Control de Pagos"
            : "Control de Pagos por Contrato";
        tituloForm = menuActual.equals(constanteNumMenuC) ? "CONTROL DE PAGOS"
            : "CONTROL DE PAGOS POR CONTRATO";
        labelInicial = menuActual.equals(constanteNumMenuC)
            ? "Tipo de contrato inicial:"
            : "Número de contrato inicial:";
        labelFinal = menuActual.equals(constanteNumMenuC)
            ? "Tipo de contrato final:"
            : "Número de contrato final:";
        fechaInicial = new Date();
        fechaFinal = new Date();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ControlPagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTipoContratoInicial();
        abrirFormulario();
    }

    public String getTituloModal()
    {
        return tituloModal;
    }

    public void setTituloModal(String tituloModal)
    {
        this.tituloModal = tituloModal;
    }

    public String getTituloForm()
    {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm)
    {
        this.tituloForm = tituloForm;
    }

    public String getLabelInicial()
    {
        return labelInicial;
    }

    public void setLabelInicial(String labelInicial)
    {
        this.labelInicial = labelInicial;
    }

    public String getLabelFinal()
    {
        return labelFinal;
    }

    public void setLabelFinal(String labelFinal)
    {
        this.labelFinal = labelFinal;
    }

    public void cargarListaTipoContratoInicial()
    {
        if (constanteNumMenuC.equals(menuActual))
        {

            tipoContratoFinal = "";
            nombreContratoFinal = "";
            cargarListaTipoOrdenCompra();
        }
        else if ("90314".equals(menuActual))
        {

            tipoContratoFinal = constanteNueveC;
            nombreContratoFinal = constanteNueveC;
            cargarListaOrdencompra();

        }

    }

    public void cargarListaTipoContratoFinal()
    {

        if (constanteNumMenuC.equals(menuActual))
        {
            cargarListaNumeroConIni();
        }
        else if ("90314".equals(menuActual))
        {

            cargarLisTipocontratoFinal();

        }
    }

    public void cargarLisTipocontratoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControlPagosControladorUrlEnum.URL5289
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ControlPagosControladorEnum.PARAM3.getValue(),
                        tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroC);

    }

    public void cargarListaNumeroConIni()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControlPagosControladorUrlEnum.URL6611
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ControlPagosControladorEnum.PARAM4.getValue(),
                        nombreContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroC);

    }

    public void cargarListaOrdencompra()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControlPagosControladorUrlEnum.URL7595
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroC);
    }

    public void cargarListaTipoOrdenCompra()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ControlPagosControladorUrlEnum.URL8430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, numeroC);
    }

    public void oprimircmdExcel()
    {
        // <CODIGO_DESARROLLADO>
        if (cambiarFechaFinal())
        {
            archivoDescarga = null;
            generaInforme(ReportesBean.FORMATOS.EXCEL97);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdPresentar()
    {
        // <CODIGO_DESARROLLADO>
        if (cambiarFechaFinal())
        {
            archivoDescarga = null;
            generaInforme(ReportesBean.FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = registroAux.getCampos().get(numeroC).toString();
        nombreContratoInicial = registroAux.getCampos().get("NOMBRE")
                        .toString();

        if (!"ZZZZZZZZZZZZZZZZ".equals(SysmanFunciones.nvlStr(tipoContratoFinal, ""))
            && !SysmanFunciones.nvlStr(tipoContratoFinal, "").equals(constanteNueveC))
        {
            tipoContratoFinal = null;
        }
        cargarListaTipoContratoFinal();

    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = registroAux.getCampos().get(numeroC).toString();
        nombreContratoFinal = registroAux.getCampos().get("NOMBRE").toString();

    }

    public boolean cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        if (fechaInicial.after(fechaFinal))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB276"));
            fechaFinal = null;
            return false;
        }
        else
        {
            return true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;
            reemplazar.put("tablaFecha", menuActual.equals(constanteNumMenuC)
                ? "ORDENDECOMPRA" : "C_CONTROLDEPAGOS");
            reemplazar.put("campoContrato", menuActual.equals(constanteNumMenuC)
                ? "TIPOORDENDECOMPRA.NOMBRE" : "ORDENDECOMPRA.NUMERO");
            reemplazar.put("tipoContratoInicial",
                            menuActual.equals(constanteNumMenuC)
                                ? nombreContratoInicial : tipoContratoInicial);
            reemplazar.put("tipoContratoFinal",
                            menuActual.equals(constanteNumMenuC)
                                ? nombreContratoFinal : tipoContratoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            strSql = Reporteador.resuelveConsulta("000350ControlDePagos",
                            Integer.parseInt(moduloContratos), reemplazar);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENCABEZADO", SysmanFunciones.concatenar(
                            "Control de pagos a contratos desde ",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial),
                            " a ",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal)));
            parametros.put("PR_STRSQL", strSql);
            archivoDescarga = JsfUtil.exportarStreamed("000350ControlDePagos",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException
                        | ParseException | SysmanException ex)
        {
            Logger.getLogger(ControlPagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public String getTipoContratoInicial()
    {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial)
    {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal()
    {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal)
    {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public RegistroDataModelImpl getListaTipoContratoFinal()
    {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        RegistroDataModelImpl listaTipoContratoFinal)
    {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public RegistroDataModelImpl getListaTipoContratoInicial()
    {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        RegistroDataModelImpl listaTipoContratoInicial)
    {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreContratoInicial()
    {
        return nombreContratoInicial;
    }

    public void setNombreContratoInicial(String nombreContratoInicial)
    {
        this.nombreContratoInicial = nombreContratoInicial;
    }

    public String getNombreContratoFinal()
    {
        return nombreContratoFinal;
    }

    public void setNombreContratoFinal(String nombreContratoFinal)
    {
        this.nombreContratoFinal = nombreContratoFinal;
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo que se hereda desde el bean base
    }
}
