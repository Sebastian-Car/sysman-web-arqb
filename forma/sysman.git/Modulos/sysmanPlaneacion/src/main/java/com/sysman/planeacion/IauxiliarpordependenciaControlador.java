package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.enums.IauxiliarpordependenciaControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 18/12/2015
 *
 * @author lcortes
 * @version 2, 07,08/09/2017. Refactorizacion de codigo y revision de
 * observaciones de la herramienta SonarLint.
 */
@ManagedBean
@ViewScoped
public class IauxiliarpordependenciaControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String dependencia;
    private String nomDependencia;
    private Date fechaDesde;
    private Date fechaHasta;
    private String nombreDependencia;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaDependencia;

    /**
     * Creates a new instance of IauxiliarpordependenciaControlador
     */
    public IauxiliarpordependenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.IAUXILIARPORDEPENDENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(IauxiliarpordependenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaDependencia();
        abrirFormulario();
    }

    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        IauxiliarpordependenciaControladorUrlEnum.URL2605
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirExcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirpresentar(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporte(FORMATOS formatos) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaDesde",
                            SysmanFunciones.convertirAFechaCadena(fechaDesde));
            reemplazar.put("fechaHasta",
                            SysmanFunciones.convertirAFechaCadena(fechaHasta));
            reemplazar.put("dependencia", dependencia);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> parametros = new HashMap<>();
            String reporte = "000444IAuxiliarpordependencia";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_IAUXILIARPORDEPENDENCIA_DEPENDENCIA_COLUMN(0)",
                            dependencia);
            parametros.put("PR_FORMS_IAUXILIARPORDEPENDENCIA_DESDE",
                            SysmanFunciones.convertirAFechaCadena(fechaDesde));
            parametros.put("PR_FORMS_IAUXILIARPORDEPENDENCIA_HASTA",
                            SysmanFunciones.convertirAFechaCadena(fechaHasta));
            parametros.put("PR_FORMS_IAUXILIARPORDEPENDENCIA_DEPENDENCIA_COLUMN(1)",
                            nombreDependencia);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreDependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        nomDependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public String getNomDependencia() {
        return nomDependencia;
    }

    public void setNomDependencia(String nomDependencia) {
        this.nomDependencia = nomDependencia;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaDesde = new Date();
        fechaHasta = new Date();
        // <CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
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
