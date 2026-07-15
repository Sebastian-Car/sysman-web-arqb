package com.sysman.mantenimientoactivos;

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
import com.sysman.mantenimientoactivos.enums.ItareasdemantenimientoControladorEnum;
import com.sysman.mantenimientoactivos.enums.ItareasdemantenimientoControladorUrlEnum;
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
 * @author ngomez
 * @version 1, 17/09/2015
 *
 * @author lcortes
 * @version 2, 16/08/2017. Refactorizacion de codigo a las consultas
 * de las listas para usar dss y revision de observaciones herramienta
 * SonarLint.
 */
@ManagedBean
@ViewScoped
public class ItareasdemantenimientoControlador extends BeanBaseModal {

    private final String compania;
    private final String cCodigo;
    private final String cCodigoElemento;
    private final String cNombre;
    private final String parametroSubtitulo;
    private final String cTexto;
    private final String cDesde;
    private final String cHasta;

    private String codigoTareaDesde;
    private String codigoTareaHasta;
    private String codigoActivoDesde;
    private String codigoActivoHasta;
    private String codigoTallerDesde;
    private String codigoTallerHasta;
    private String tareaDesde;
    private String tareaHasta;
    private String activoDesde;
    private String activoHasta;
    private String tallerDesde;
    private String tallerHasta;
    private String tipoInforme;
    private String subtitulo;
    private Date desde;
    private Date hasta;
    private RegistroDataModelImpl listaCmbTareaDesde;
    private RegistroDataModelImpl listaCmbTareaHasta;
    private RegistroDataModelImpl listaCmbActivoDesde;
    private RegistroDataModelImpl listaCmbActivoHasta;
    private RegistroDataModelImpl listaCmbTallerDesde;
    private RegistroDataModelImpl listaCmbTallerHasta;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ItareasdemantenimientoControlador
     */
    public ItareasdemantenimientoControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        cCodigoElemento = "CODIGOELEMENTO";
        parametroSubtitulo = "PR_SUBTITULO";
        cNombre = "NOMBRE";
        cTexto = "TB_TB3467";
        cDesde = "s$desde$s";
        cHasta = "s$hasta$s";
        try {
            numFormulario = GeneralCodigoFormaEnum.ITAREASDEMANTENIMIENTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ItareasdemantenimientoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListaCmbTareaDesde();
        cargarListaCmbTareaHasta();
        cargarListaCmbActivoDesde();
        cargarListaCmbActivoHasta();
        cargarListaCmbTallerDesde();
        cargarListaCmbTallerHasta();
        abrirFormulario();
    }

    public void cargarListaCmbTareaDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL3763
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbTareaDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCmbTareaHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL4325
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ItareasdemantenimientoControladorEnum.COD_TAREADESDE
                        .getValue(), codigoTareaDesde);

        listaCmbTareaHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCmbActivoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL5100
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbActivoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaCmbActivoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL5887
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(),
                        String.valueOf(codigoActivoDesde));

        listaCmbActivoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoElemento);
    }

    public void cargarListaCmbTallerDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL6801
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbTallerDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaCmbTallerHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ItareasdemantenimientoControladorUrlEnum.URL7484
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoTallerDesde);

        listaCmbTallerHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    private void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        String strSql;
        String reporte = "000132IListaMantenimiento";
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("codigoActivoDesde", codigoActivoDesde);
        reemplazar.put("codigoActivoHasta", codigoActivoHasta);
        reemplazar.put("codigoTareaDesde", codigoTareaDesde);
        reemplazar.put("codigoTareaHasta", codigoTareaHasta);
        reemplazar.put("desde", SysmanFunciones.formatearFecha(desde));
        reemplazar.put("hasta", SysmanFunciones.formatearFecha(hasta));
        reemplazar.put("codigoTallerDesde", codigoTallerDesde);
        reemplazar.put("codigoTallerHasta", codigoTallerHasta);
        strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        parametros.put("PR_STRSQL", strSql);

        switch (tipoInforme) {
        case "1": // Por elemento
            informeElemento(parametros, formato);
            break;
        case "2": // Por fechas
            informeFechas(parametros, formato);
            break;
        case "3":// Por tareas
            informeTareas(parametros, formato);
            break;

        case "4": // Por encargado
            informeEncargado(parametros, formato);
            break;

        default:
            break;
        }
    }

    private void informeEncargado(Map<String, Object> parametros,
        FORMATOS formato) {
        subtitulo = idioma.getString(cTexto)
                        .replace(cDesde, tallerDesde)
                        .replace(cHasta, tallerHasta);
        parametros.put(parametroSubtitulo, subtitulo);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000158IListaMantenimientoT", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void informeTareas(Map<String, Object> parametros,
        FORMATOS formato) {
        subtitulo = idioma.getString(cTexto)
                        .replace(cDesde, tareaDesde)
                        .replace(cHasta, tareaHasta);
        parametros.put(parametroSubtitulo, subtitulo);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000137IListaMantenimientoM", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void informeElemento(Map<String, Object> parametros,
        FORMATOS formato) {
        subtitulo = idioma.getString(cTexto)
                        .replace(cDesde, activoDesde)
                        .replace(cHasta, activoHasta);
        parametros.put(parametroSubtitulo, subtitulo);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000132IListaMantenimiento", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void informeFechas(Map<String, Object> parametros,
        FORMATOS formato) {
        try {
            parametros.put("PR_DESDE",
                            SysmanFunciones.convertirAFechaCadena(desde,
                                            "dd/MM/yyyy"));

            parametros.put("PR_HASTA",
                            SysmanFunciones.convertirAFechaCadena(hasta,
                                            "dd/MM/yyyy"));

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000122IListaMantenimientoF", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdImpresora() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbTareaDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoTareaDesde = registroAux.getCampos().get(cCodigo).toString();
        tareaDesde = registroAux.getCampos().get(cNombre).toString();
        codigoTareaHasta = tareaHasta = null;
        cargarListaCmbTareaHasta();
    }

    public void seleccionarFilaCmbTareaHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoTareaHasta = registroAux.getCampos().get(cCodigo).toString();
        tareaHasta = registroAux.getCampos().get(cNombre).toString();
    }

    public void seleccionarFilaCmbActivoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoActivoDesde = registroAux.getCampos()
                        .get(cCodigoElemento).toString();
        activoDesde = registroAux.getCampos().get("NOMBRECORTO").toString();
        codigoActivoHasta = activoHasta = null;
        cargarListaCmbActivoHasta();
    }

    public void seleccionarFilaCmbActivoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoActivoHasta = registroAux.getCampos()
                        .get(cCodigoElemento).toString();
        activoHasta = registroAux.getCampos().get("NOMBRECORTO").toString();
    }

    public void seleccionarFilaCmbTallerDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoTallerDesde = registroAux.getCampos().get("NIT").toString();
        tallerDesde = registroAux.getCampos().get(cNombre).toString();
        codigoTallerHasta = tallerHasta = null;
        cargarListaCmbTallerHasta();
    }

    public void seleccionarFilaCmbTallerHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoTallerHasta = registroAux.getCampos().get("NIT").toString();
        tallerHasta = registroAux.getCampos().get(cNombre).toString();
    }

    public String getCodigoTareaDesde() {
        return codigoTareaDesde;
    }

    public void setCodigoTareaDesde(String codigoTareaDesde) {
        this.codigoTareaDesde = codigoTareaDesde;
    }

    public String getCodigoTareaHasta() {
        return codigoTareaHasta;
    }

    public void setCodigoTareaHasta(String codigoTareaHasta) {
        this.codigoTareaHasta = codigoTareaHasta;
    }

    public String getCodigoActivoDesde() {
        return codigoActivoDesde;
    }

    public void setCodigoActivoDesde(String codigoActivoDesde) {
        this.codigoActivoDesde = codigoActivoDesde;
    }

    public String getCodigoActivoHasta() {
        return codigoActivoHasta;
    }

    public void setCodigoActivoHasta(String codigoActivoHasta) {
        this.codigoActivoHasta = codigoActivoHasta;
    }

    public String getCodigoTallerDesde() {
        return codigoTallerDesde;
    }

    public void setCodigoTallerDesde(String codigoTallerDesde) {
        this.codigoTallerDesde = codigoTallerDesde;
    }

    public String getCodigoTallerHasta() {
        return codigoTallerHasta;
    }

    public void setCodigoTallerHasta(String codigoTallerHasta) {
        this.codigoTallerHasta = codigoTallerHasta;
    }

    public String getTareaDesde() {
        return tareaDesde;
    }

    public void setTareaDesde(String tareaDesde) {
        this.tareaDesde = tareaDesde;
    }

    public String getTareaHasta() {
        return tareaHasta;
    }

    public void setTareaHasta(String tareaHasta) {
        this.tareaHasta = tareaHasta;
    }

    public String getActivoDesde() {
        return activoDesde;
    }

    public void setActivoDesde(String activoDesde) {
        this.activoDesde = activoDesde;
    }

    public String getActivoHasta() {
        return activoHasta;
    }

    public void setActivoHasta(String activoHasta) {
        this.activoHasta = activoHasta;
    }

    public String getTallerDesde() {
        return tallerDesde;
    }

    public void setTallerDesde(String tallerDesde) {
        this.tallerDesde = tallerDesde;
    }

    public String getTallerHasta() {
        return tallerHasta;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public void setTallerHasta(String tallerHasta) {
        this.tallerHasta = tallerHasta;
    }

    public RegistroDataModelImpl getListaCmbTareaDesde() {
        return listaCmbTareaDesde;
    }

    public void setListaCmbTareaDesde(
        RegistroDataModelImpl listaCmbTareaDesde) {
        this.listaCmbTareaDesde = listaCmbTareaDesde;
    }

    public RegistroDataModelImpl getListaCmbTareaHasta() {
        return listaCmbTareaHasta;
    }

    public void setListaCmbTareaHasta(
        RegistroDataModelImpl listaCmbTareaHasta) {
        this.listaCmbTareaHasta = listaCmbTareaHasta;
    }

    public RegistroDataModelImpl getListaCmbActivoDesde() {
        return listaCmbActivoDesde;
    }

    public void setListaCmbActivoDesde(
        RegistroDataModelImpl listaCmbActivoDesde) {
        this.listaCmbActivoDesde = listaCmbActivoDesde;
    }

    public RegistroDataModelImpl getListaCmbActivoHasta() {
        return listaCmbActivoHasta;
    }

    public void setListaCmbActivoHasta(
        RegistroDataModelImpl listaCmbActivoHasta) {
        this.listaCmbActivoHasta = listaCmbActivoHasta;
    }

    public RegistroDataModelImpl getListaCmbTallerDesde() {
        return listaCmbTallerDesde;
    }

    public void setListaCmbTallerDesde(
        RegistroDataModelImpl listaCmbTallerDesde) {
        this.listaCmbTallerDesde = listaCmbTallerDesde;
    }

    public RegistroDataModelImpl getListaCmbTallerHasta() {
        return listaCmbTallerHasta;
    }

    public void setListaCmbTallerHasta(
        RegistroDataModelImpl listaCmbTallerHasta) {
        this.listaCmbTallerHasta = listaCmbTallerHasta;
    }

    public String getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        tipoInforme = "3";
        // </CODIGO_DESARROLLADO>
    }
}
