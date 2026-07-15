package com.sysman.almacen;

import com.sysman.almacen.enums.ProcontracadqControladorEnum;
import com.sysman.almacen.enums.ProcontracadqControladorUrlEnum;
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
import com.sysman.services.FormContinuoService;
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
 * @author ngomez
 * @version 1, 09/11/2015
 * 
 * @author ybecerra
 * @version 2, 15/05/2017 Revision Sonar y Refactoring
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class ProcontracadqControlador extends BeanBaseModal {

    private final String compania;
    private String desde;
    private String hasta;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaelementoDesde;
    private RegistroDataModelImpl listaelementoHasta;

    /**
     * Creates a new instance of ProcontracadqControlador
     */
    public ProcontracadqControlador() {
        super();

        // 327
        numFormulario = GeneralCodigoFormaEnum.PROCONTRACADQ_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ProcontracadqControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {

        cargarListaelementoDesde();
        abrirFormulario();
    }

    public void cargarListaelementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProcontracadqControladorUrlEnum.URL2346
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaelementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());

    }

    public void cargarListaelementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProcontracadqControladorUrlEnum.URL3413
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ProcontracadqControladorEnum.PARAM0.getValue(),
                        desde);

        listaelementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ELEMENTO.getName());

    }

    public void oprimircmdpantalla() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "000366LisContraAdqui";

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("desde", desde);
        reemplazar.put("hasta", hasta);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        parametros.put("PR_STRSQL", strSql);
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaelementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        desde = registroAux.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()) == null
                            ? "" : registroAux.getCampos()
                                            .get(GeneralParameterEnum.ELEMENTO
                                                            .getName())
                                            .toString();

        hasta = null;
        cargarListaelementoHasta();
    }

    public void seleccionarFilaelementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        hasta = registroAux.getCampos()
                        .get(GeneralParameterEnum.ELEMENTO.getName()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.ELEMENTO
                                                            .getName())
                                            .toString();
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaelementoDesde() {
        return listaelementoDesde;
    }

    public void setListaelementoDesde(
        RegistroDataModelImpl listaelementoDesde) {
        this.listaelementoDesde = listaelementoDesde;
    }

    public RegistroDataModelImpl getListaelementoHasta() {
        return listaelementoHasta;
    }

    public void setListaelementoHasta(
        RegistroDataModelImpl listaelementoHasta) {
        this.listaelementoHasta = listaelementoHasta;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

}