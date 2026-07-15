package com.sysman.almacen;

import com.sysman.almacen.enums.LiscompraxproveedorControladorEnum;
import com.sysman.almacen.enums.LiscompraxproveedorControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 29/10/2015
 * 
 * @author eamaya
 * @version 2, 03/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 * 
 */
@ManagedBean
@ViewScoped
public class LiscompraxproveedorControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloAlmacen;
    private static final String FORMATOFECHA = "dd/MM/yyyy";
    private boolean entrada;
    private String proveedorInicial;
    private String proveedorFinal;
    private String proveeInicial;
    private String proveeFinal;
    private String reporte;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaProveedorInicial;
    private RegistroDataModelImpl listaProveedorFinal;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of LiscompraxproveedorControlador
     */
    public LiscompraxproveedorControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloAlmacen = SessionUtil.getModulo();
        try {
            // 321
            numFormulario = GeneralCodigoFormaEnum.LISCOMPRAXPROVEEDOR_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LiscompraxproveedorControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListaProveedorInicial();
        abrirFormulario();
    }

    public void cargarListaProveedorInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscompraxproveedorControladorUrlEnum.URL3144
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaProveedorInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListaProveedorFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LiscompraxproveedorControladorUrlEnum.URL3813
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LiscompraxproveedorControladorEnum.PARAM0.getValue(),
                        String.valueOf(proveedorInicial));

        listaProveedorFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void oprimircmdPdf() {
        
        archivoDescarga = null;
        
        if ((fechaInicial != null) && (fechaFinal != null)
            && (proveedorInicial != null) && (proveedorFinal != null)) {
            if (!validarFechas()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            if (!entrada) {
                generarReporteComprasPorProveedor(ReportesBean.FORMATOS.PDF);
            }
            else {
                generarReporteComprasxProveedorEntrada(
                                ReportesBean.FORMATOS.PDF);
            }
        }
        else {
            JsfUtil.agregarMensajeError("TI_MS_ERROR_VALIDACION");
        }
    }

    public void oprimircmbExcel() {
        
        archivoDescarga = null;
        
        if ((fechaInicial != null) && (fechaFinal != null)
            && (proveedorInicial != null) && (proveedorFinal != null)) {
            if (!validarFechas()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
                return;
            }
            if (!entrada) {
                generarReporteComprasPorProveedor(
                                ReportesBean.FORMATOS.EXCEL97);
            }
            else {
                generarReporteComprasxProveedorEntrada(
                                ReportesBean.FORMATOS.EXCEL97);
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void seleccionarFilaProveedorInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proveedorInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        proveeInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
        proveedorFinal = null;
        proveeFinal = null;
        cargarListaProveedorFinal();
    }

    public void seleccionarFilaProveedorFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proveedorFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        proveeFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    private void generarReporteComprasPorProveedor(FORMATOS formato) {
        
        if (!validarFechas()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
            fechaFinal = null;
            return;
        }

        reporte = "000360COMPRAsPORPROVEEDOR";

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        reemplazar.put("proveedorInicial", proveedorInicial);
        reemplazar.put("proveedorFinal", proveedorFinal);
        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(moduloAlmacen), reemplazar);

        Map<String, Object> parametros = new HashMap<>();

        try {
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_PERIODO",
                            "Periodo "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial, FORMATOFECHA)
                                + " Al "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal, FORMATOFECHA));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarReporteComprasxProveedorEntrada(FORMATOS formato) {
       
        if (!validarFechas()) {
            fechaFinal = null;
            return;
        }

        reporte = "000361COMPRASXPROVEEDORENTRADA";

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        reemplazar.put("proveedorInicial", proveedorInicial);
        reemplazar.put("proveedorFinal", proveedorFinal);
        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(moduloAlmacen), reemplazar);

        Map<String, Object> parametros = new HashMap<>();

        try {
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_PERIODO",
                            "Periodo "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial, FORMATOFECHA)
                                + " Al "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal, FORMATOFECHA));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFechas() {
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
            return false;
        }
        return true;
    }

    @Override
    public void abrirFormulario() {
        // Viene desde forma
    }

    public boolean getEntrada() {
        return entrada;
    }

    public void setEntrada(boolean entrada) {
        this.entrada = entrada;
    }

    public String getProveedorInicial() {
        return proveedorInicial;
    }

    public void setProveedorInicial(String proveedorInicial) {
        this.proveedorInicial = proveedorInicial;
    }

    public String getProveedorFinal() {
        return proveedorFinal;
    }

    public void setProveedorFinal(String proveedorFinal) {
        this.proveedorFinal = proveedorFinal;
    }

    public String getProveeInicial() {
        return proveeInicial;
    }

    public void setProveeInicial(String proveeInicial) {
        this.proveeInicial = proveeInicial;
    }

    public String getProveeFinal() {
        return proveeFinal;
    }

    public void setProveeFinal(String proveeFinal) {
        this.proveeFinal = proveeFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaProveedorInicial() {
        return listaProveedorInicial;
    }

    public void setListaProveedorInicial(
        RegistroDataModelImpl listaProveedorInicial) {
        this.listaProveedorInicial = listaProveedorInicial;
    }

    public RegistroDataModelImpl getListaProveedorFinal() {
        return listaProveedorFinal;
    }

    public void setListaProveedorFinal(
        RegistroDataModelImpl listaProveedorFinal) {
        this.listaProveedorFinal = listaProveedorFinal;
    }

}
