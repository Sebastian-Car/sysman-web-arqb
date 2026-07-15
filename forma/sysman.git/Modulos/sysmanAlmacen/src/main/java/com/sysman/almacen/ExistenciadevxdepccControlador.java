package com.sysman.almacen;

import com.sysman.almacen.enums.ExistenciadevxdepccControladorEnum;
import com.sysman.almacen.enums.ExistenciadevxdepccControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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

import java.io.IOException;
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
 * @author NGOMEZ
 * @version 1, 28/01/2016
 * 
 * @version 2, 27/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */
@ManagedBean
@ViewScoped
public class ExistenciadevxdepccControlador extends BeanBaseModal {

    private final String consCodigo;
    private final String consCodigoElemento;
    private String compania;

    private String elementoDesde;
    private String elementoHasta;
    private String dependenciaDesde;
    private String dependenciaHasta;
    private String lblElementoDesde;
    private String lblElementoHasta;
    private String lblDependenciaDesde;
    private String lblDependenciaHasta;
    private boolean grupo;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Creates a new instance of ExistenciadevxdepccControlador
     */
    public ExistenciadevxdepccControlador() {
        super();
        consCodigo = "CODIGO";
        consCodigoElemento = "CODIGOELEMENTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.EXISTENCIADEVXDEPCC_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ExistenciadevxdepccControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbElementoDesde();
        cargarListacmbElementoHasta();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        grupo = true;
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExistenciadevxdepccControladorUrlEnum.URL3238
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoElemento);
    }

    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExistenciadevxdepccControladorUrlEnum.URL4055
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ExistenciadevxdepccControladorEnum.PARAM0.getValue(),
                        elementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoElemento);
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExistenciadevxdepccControladorUrlEnum.URL4991
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExistenciadevxdepccControladorUrlEnum.URL5683
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ExistenciadevxdepccControladorEnum.PARAM1.getValue(),
                        dependenciaDesde);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = registroAux.getCampos().get(consCodigoElemento)
                        .toString();
        lblElementoDesde = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
        elementoHasta = null;
        lblElementoHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = registroAux.getCampos().get(consCodigoElemento)
                        .toString();
        lblElementoHasta = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaDesde = registroAux.getCampos().get(consCodigo).toString();
        lblDependenciaDesde = registroAux.getCampos().get("NOMBRE").toString();
        dependenciaHasta = null;
        lblDependenciaHasta = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaHasta = registroAux.getCampos().get(consCodigo).toString();
        lblDependenciaHasta = registroAux.getCampos().get("NOMBRE").toString();
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

    public String getDependenciaDesde() {
        return dependenciaDesde;
    }

    public void setDependenciaDesde(String dependenciaDesde) {
        this.dependenciaDesde = dependenciaDesde;
    }

    public String getDependenciaHasta() {
        return dependenciaHasta;
    }

    public void setDependenciaHasta(String dependenciaHasta) {
        this.dependenciaHasta = dependenciaHasta;
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

    public String getLblDependenciaDesde() {
        return lblDependenciaDesde;
    }

    public void setLblDependenciaDesde(String lblDependenciaDesde) {
        this.lblDependenciaDesde = lblDependenciaDesde;
    }

    public String getLblDependenciaHasta() {
        return lblDependenciaHasta;
    }

    public void setLblDependenciaHasta(String lblDependenciaHasta) {
        this.lblDependenciaHasta = lblDependenciaHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public boolean isGrupo() {
        return grupo;
    }

    public void setGrupo(boolean grupo) {
        this.grupo = grupo;
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            String reporte = "000490ExistenciaDevXDepCC";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);
            reemplazar.put("dependenciaDesde", dependenciaDesde);
            reemplazar.put("dependenciaHasta", dependenciaHasta);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}
