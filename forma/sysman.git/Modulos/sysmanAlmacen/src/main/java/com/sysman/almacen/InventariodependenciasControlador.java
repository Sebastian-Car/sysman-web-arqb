package com.sysman.almacen;

import com.sysman.almacen.enums.InventariodependenciasControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 13/11/2015
 *
 * @version 2, 02/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @version 4, 14/08/2018
 * @author jgomezp se realizo cambio para ingresar a otro formulario.
 * 
 */

@ManagedBean
@ViewScoped
public class InventariodependenciasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String dependencia;
    private String dependenciaFinal;
    private Date fechaDesde;
    private Date fechaHasta;
    private String nombreDependencia;
    private String nombreDependenciaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaFinal;
    private final String codigoCons;
    private boolean visible;

    /**
     * Creates a new instance of InventariodependenciasControlador
     */
    public InventariodependenciasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        if ("10040310".equals(SessionUtil.getMenuActual())) {
        	visible = false;
        } else {
        	visible = true;
        }
        
        try {
            numFormulario = GeneralCodigoFormaEnum.INVENTARIODEPENDENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InventariodependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaDependencia();
        cargarListaDependenciaFinal();
        fechaDesde = new Date();
        fechaHasta = new Date();
        abrirFormulario();
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventariodependenciasControladorUrlEnum.URL2953
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoCons);
    }

    public void cargarListaDependenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InventariodependenciasControladorUrlEnum.URL3521
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoCons);
    }

    public void oprimirPresentar() throws ParseException {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() throws ParseException {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (("899999717-1").equals(compania)) {
            excel(ReportesBean.FORMATOS.EXCEL97);
        }
        else {
            generarInforme(ReportesBean.FORMATOS.EXCEL97);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos().get(codigoCons).toString();
        nombreDependencia = registroAux.getCampos().get("NOMBRE").toString();
        dependenciaFinal= null;
        nombreDependenciaFinal=null;
        cargarListaDependenciaFinal();
    }

    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFinal = registroAux.getCampos().get(codigoCons).toString();
        nombreDependenciaFinal = registroAux.getCampos().get("NOMBRE")
                        .toString();
    }

    public void generarInforme(ReportesBean.FORMATOS formato) throws ParseException {

        String parReporte = "";
        if ("10040310".equals(SessionUtil.getMenuActual())) {
        	parReporte="001855DevolutivosPorDependencia";
        } else {
        	parReporte = "000383InventarioFisicoporDependencia";
        }
        HashMap<String, Object> remplazar = new HashMap<>();

        remplazar.put("dependencia", "'" + dependencia + "'");
        remplazar.put("dependenciafinal", "'" + dependenciaFinal + "'");
        remplazar.put("fechainicial",
                        SysmanFunciones.formatearFecha(fechaDesde));
        remplazar.put("fechafinal",
                        SysmanFunciones.formatearFecha(fechaHasta));

        String strsql = Reporteador.resuelveConsulta(parReporte,
                        Integer.parseInt(modulo), remplazar);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_STRSQL", strsql);
        parametros.put("PR_FECHA", SysmanFunciones.convertirAFechaCadena(fechaDesde));
        parametros.put("PR_AHORA", SysmanFunciones.convertirAFechaCadena(fechaHasta));

        try {
            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void excel(ReportesBean.FORMATOS formato) {
        HashMap<String, Object> remplazar = new HashMap<>();
        remplazar.put("dependencia", "'" + dependencia + "'");
        remplazar.put("dependenciaFinal", "'" + dependenciaFinal + "'");
        remplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaDesde));
        remplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaHasta));
        String strsql = Reporteador.resuelveConsulta(
                        "900004InventarioFisico", Integer.parseInt(modulo),
                        remplazar);

        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strsql,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // HEREDADO DEL BEAN BASE
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getDependenciaFinal() {
        return dependenciaFinal;
    }

    public void setDependenciaFinal(String dependenciaFinal) {
        this.dependenciaFinal = dependenciaFinal;
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

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getNombreDependenciaFinal() {
        return nombreDependenciaFinal;
    }

    public void setNombreDependenciaFinal(String nombreDependenciaFinal) {
        this.nombreDependenciaFinal = nombreDependenciaFinal;
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

    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
    

}