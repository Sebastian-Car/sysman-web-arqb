package com.sysman.planeacion;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.ListaplancomprasControladorEnum;
import com.sysman.planeacion.enums.ListaplancomprasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 17/12/2015
 * 
 * @author eamaya
 * @version 2.0, 07/09/2017, Proceso de Refactoring DSS, cambio de
 * numero de formulario por enum,cambio metodos OnRow y correcciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class ListaplancomprasControlador extends BeanBaseModal {

    private String compania = SessionUtil.getCompania();
    private String modulo = SessionUtil.getModulo();
    private Registro registro;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private List<Registro> listacmbAno;
    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private String cmbano;
    private String cpElementoDesde;
    private String cpElementoHasta;
    private StreamedContent archivoDescarga;

    public ListaplancomprasControlador() {
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTAPLANCOMPRAS_CONTROLADOR
                            .getCodigo();

            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ListaplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void cargarForma() {
        cargarListacmbElementoDesde();
        cargarListacmbAno();
        abrirFormulario();
    }

    public void cargarListacmbAno() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListaplancomprasControladorUrlEnum.URL3015
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaplancomprasControladorUrlEnum.URL3564
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        cmbano);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaplancomprasControladorUrlEnum.URL4447
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        cmbano);
        param.put(ListaplancomprasControladorEnum.CODIGOINI.getValue(),
                        cmbElementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        cpElementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cargarListacmbElementoHasta();

    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        cpElementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void obtenerReporte(FORMATOS formatos) {
        archivoDescarga = null;

        String nombreCor = SessionUtil.getCompaniaIngreso().getNombre();
        if ("CORPORACION AUTONOMA REGIONAL DE BOYACA".equals(nombreCor)) {
            nombreCor = "PLAN DE ADQUISICION DE BIENES Y SERVICIOS";
        }
        else {
            nombreCor = "PLAN DE COMPRAS ACUMULADO";
        }

        try {
            Map<String, Object> reemplazar = new TreeMap<>();
            reemplazar.put("cmbano", cmbano);
            reemplazar.put("cmbElementoDesde", cmbElementoDesde);
            reemplazar.put("cmbElementoHasta", cmbElementoHasta);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new TreeMap<>();
            String reporte = "000439IPlanComprasRubro";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_LISTAPLANCOMPRAS_ETIQUETA21_CAPTION",
                            nombreCor);
            parametros.put("PR_FORMS_LISTAPLANCOMPRAS_CMBANO", cmbano);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (JRException | IOException ex) {
            Logger.getLogger(ListaplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarcmbElementoDesde() {
        // <CODIGO_DESARROLLADO>
        cmbElementoHasta = null;
        cargarListacmbElementoHasta();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        cmbElementoDesde = null;
        cmbElementoHasta = null;
        cpElementoDesde = "";
        cpElementoHasta = "";
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }

    public String getCmbElementoDesde() {
        return cmbElementoDesde;
    }

    public void setCmbElementoDesde(String cmbElementoDesde) {
        this.cmbElementoDesde = cmbElementoDesde;
    }

    public String getCmbElementoHasta() {
        return cmbElementoHasta;
    }

    public void setCmbElementoHasta(String cmbElementoHasta) {
        this.cmbElementoHasta = cmbElementoHasta;
    }

    public String getCmbano() {
        return cmbano;
    }

    public void setCmbano(String cmbano) {
        this.cmbano = cmbano;
    }

    public String getCpElementoDesde() {
        return cpElementoDesde;
    }

    public void setCpElementoDesde(String cpElementoDesde) {
        this.cpElementoDesde = cpElementoDesde;
    }

    public String getCpElementoHasta() {
        return cpElementoHasta;
    }

    public void setCpElementoHasta(String cpElementoHasta) {
        this.cpElementoHasta = cpElementoHasta;
    }
}
