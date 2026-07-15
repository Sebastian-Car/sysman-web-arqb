package com.sysman.almacen;

import com.sysman.almacen.enums.InformeinterfazControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 23/10/2015
 * 
 * @modifier amonroy
 * @version 2, 3/05/2017 Proceso de Refactoring y Revision de buenas
 * practicas sugeridas por la herramienta SonarLint
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class InformeinterfazControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cCodigoElemento;
    private String anio;
    private String elementoDesde;
    private String elementoHasta;
    private String lblElementoDesde;
    private String lblElementoHasta;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoFuente;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;

    /**
     * Creates a new instance of InformeinterfazControlador
     */
    public InformeinterfazControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigoElemento = GeneralParameterEnum.CODIGOELEMENTO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORMEINTERFAZ_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InformeinterfazControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaAnoFuente();
        cargarListaCmbElementoDesde();
        cargarListaCmbElementoHasta();
        abrirFormulario();
    }

    public void cargarListaAnoFuente() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoFuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeinterfazControladorUrlEnum.URL2774
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeinterfazControladorUrlEnum.URL3456
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaCmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeinterfazControladorUrlEnum.URL4299
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), elementoDesde);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void oprimircmdPdf() {
        if ((elementoDesde != null) && (elementoHasta != null)
            && (anio != null)) {
            generarReporteInterfaz(ReportesBean.FORMATOS.PDF);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void oprimircmdExcel() {
        if ((elementoDesde != null) && (elementoHasta != null)
            && (anio != null)) {
            generarReporteInterfaz(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = registroAux.getCampos().get(cCodigoElemento).toString();
        lblElementoDesde = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
        elementoHasta = null;
        lblElementoHasta = null;
        cargarListaCmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = registroAux.getCampos().get(cCodigoElemento).toString();
        lblElementoHasta = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
    }

    private void generarReporteInterfaz(FORMATOS formatos) {
        String reporte = "000346Interfaz";

        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("anio", anio);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            String elementos = idioma.getString("TB_TB3498");
            elementos = elementos.replace("s$elementoDesde$s", elementoDesde);
            elementos = elementos.replace("s$elementoHasta$s", elementoHasta);

            parametros.put("PR_ANIO", SysmanFunciones.concatenar(
                            idioma.getString("TG_ANIO4"), " ", anio));
            parametros.put("PR_ELEMENTO", elementos);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getLblElementoDesde() {
        return lblElementoDesde;
    }

    public void setLblElementoDesde(String lblElementoDesde) {
        this.lblElementoDesde = lblElementoDesde;
    }

    public String getLblElementoHasta() {
        return lblElementoHasta;
    }

    public void setLblElementoHasta(String lblElementoHasta) {
        this.lblElementoHasta = lblElementoHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoFuente() {
        return listaAnoFuente;
    }

    public void setListaAnoFuente(List<Registro> listaAnoFuente) {
        this.listaAnoFuente = listaAnoFuente;
    }

    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listaCmbElementoDesde) {
        this.listaCmbElementoDesde = listaCmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListaCmbElementoHasta(
        RegistroDataModelImpl listaCmbElementoHasta) {
        this.listaCmbElementoHasta = listaCmbElementoHasta;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}
