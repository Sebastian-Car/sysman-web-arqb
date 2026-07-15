package com.sysman.almacen;

import com.sysman.almacen.enums.RequisicionesPendientesControladorUrlEnum;
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
import com.sysman.jsfutil.ReportesBean;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
 * @author jacelas
 * @version 1, 18/11/2015
 * 
 * @version 2, 08/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class RequisicionesPendientesControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String codigoCons;
    private final String codigoElementoCons;
    private final String fechaFinalCons;
    private final String fechaInicialCons;
    private String opcion;
    private String elementoInicio;
    private String elementoFin;
    private String dependenciaInicio;
    private String dependenciaFin;
    private Date fechaInicio;
    private Date fechaFin;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;
    private RegistroDataModelImpl listaDependenciadesde;
    private RegistroDataModelImpl listaDependenciahasta;
    

    /**
     * Creates a new instance of RequisicionesPendientesControlador
     */
    public RequisicionesPendientesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        codigoCons = "CODIGO";
        codigoElementoCons = "CODIGOELEMENTO";
        fechaFinalCons = "fecha_final";
        fechaInicialCons = "fecha_inicial";
        try {

            numFormulario = GeneralCodigoFormaEnum.REQUISICIONES_PENDIENTES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RequisicionesPendientesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        opcion = "1";
        fechaInicio = new Date();
        fechaFin = new Date();
        cargarListacmbElementoDesde();
        cargarListadependenciadesde();
        cargarListadependenciahasta();
        abrirFormulario();
        cargarListacmbElementoHasta();
    }

    public void cargarListacmbElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RequisicionesPendientesControladorUrlEnum.URL3516.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoElementoCons);
    }

    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RequisicionesPendientesControladorUrlEnum.URL4250.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(),elementoInicio);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "CODIGOELEMENTO");
    }

    public void cargarListadependenciadesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RequisicionesPendientesControladorUrlEnum.URL5228.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaDependenciadesde = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void cargarListadependenciahasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RequisicionesPendientesControladorUrlEnum.URL5931.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),dependenciaInicio);
        
        listaDependenciahasta = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoCons);
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>

        // 000386OrdSumpend
        HashMap<String, Object> variables = new HashMap<>();
        try {
            if ("2".equals(opcion)) {
            	
            	
            	
            	variables.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                variables.put(fechaInicialCons, SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
                variables.put(fechaFinalCons, SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
                variables.put("dependencia_inicio", dependenciaInicio);
                variables.put("dependencia_fin", dependenciaFin);
                variables.put("elemento_inicio", elementoInicio);
                variables.put("elemento_fin", elementoFin);
        
                
                Map<String, Object> parametros = new HashMap<>();

                String sql = Reporteador.resuelveConsulta("000387requisicionesPenxDep", Integer.valueOf(modulo), variables);
                parametros.put("PR_STRSQL", sql);
                parametros.put("PR_FORMS_REQUISICIONESFECHADEPENDENCIA_DESDE",dependenciaInicio);
                parametros.put("PR_FORMS_REQUISICIONESFECHADEPENDENCIA_HASTA",dependenciaFin);
                
                archivoDescarga = JsfUtil.exportarStreamed("000387requisicionesPenxDep",
                        parametros, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.PDF );
            }
            else {
                variables.put(fechaInicialCons, SysmanFunciones
                                .formatearFecha(fechaInicio));
                variables.put(fechaFinalCons, SysmanFunciones
                                .formatearFecha(fechaFin));

                genInforme(ReportesBean.FORMATOS.PDF, "000386OrdSumpend",
                                variables);

            }

        }
        catch (JRException | IOException | ParseException  | SysmanException ex) {
            Logger.getLogger(RequisicionesPendientesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                    idioma.getString("MSM_TRANS_INTERRUMPIDA")
                        + ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>

        HashMap<String, Object> variables = new HashMap<>();
        try {
            if ("2".equals(opcion)) {
                variables.put(fechaInicialCons, SysmanFunciones
                                .formatearFecha(fechaInicio));
                variables.put(fechaFinalCons, SysmanFunciones
                                .formatearFecha(fechaFin));
                variables.put("dependencia_inicio", dependenciaInicio);
                variables.put("dependencia_fin", dependenciaFin);
                variables.put("elemento_inicio", elementoInicio);
                variables.put("elemento_fin", elementoFin);


                genInforme(ReportesBean.FORMATOS.EXCEL97,
                                "000387requisicionesPenxDep", variables);

            }
            else {
                variables.put(fechaInicialCons, SysmanFunciones
                                .formatearFecha(fechaInicio));
                variables.put(fechaFinalCons, SysmanFunciones
                                .formatearFecha(fechaFin));

                genInforme(ReportesBean.FORMATOS.EXCEL97, "000386OrdSumpend",
                                variables);

            }

        }
        catch (JRException | IOException | ParseException ex) {
            Logger.getLogger(RequisicionesPendientesControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        // </CODIGO_DESARROLLADO>
    }

    private void genInforme(ReportesBean.FORMATOS formato, String nombreReporte,
        HashMap<String, Object> variables)
                        throws JRException, IOException, ParseException {
        // <CODIGO_DESARROLLADO>   
        archivoDescarga = null;
        try {

            HashMap <String, Object> parametros = new HashMap<>();

            String sql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.valueOf(modulo), variables);          

            parametros.put("PR_STRSQL", sql);

            SimpleDateFormat formateador = new SimpleDateFormat(
                            " dd 'de' MMMM  'de' yyyy", new Locale("es", "ES"));

            parametros.put("PR_FORMS_SUM_PEND_FECINICIAL",
                            formateador.format(fechaInicio));
            parametros.put("PR_FORMS_SUM_PEND_FECFINAL",
                            formateador.format(fechaFin));
            parametros.put("PR_FORMS_REQUISICIONESFECHADEPENDENCIA_DESDE",
                            formateador.format(fechaInicio));
            parametros.put("PR_FORMS_REQUISICIONESFECHADEPENDENCIA_HASTA",
                            formateador.format(fechaFin));

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros,ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+nombreReporte);
            Logger.getLogger(RequisicionesPendientesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }        
        catch (SysmanException e) {       
            Logger.getLogger(RequisicionesPendientesControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
         
     
    }

    public void cambiarMarco38() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicio = registroAux.getCampos()
                        .get(codigoElementoCons).toString();
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFin = registroAux.getCampos().get(codigoElementoCons).toString();
    }

    public void seleccionarFilaDependenciadesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaInicio = registroAux.getCampos().get(codigoCons).toString();
        cargarListadependenciahasta();
    }

    public void seleccionarFilaDependenciahasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependenciaFin = registroAux.getCampos().get(codigoCons).toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getElementoInicio() {
        return elementoInicio;
    }

    public void setElementoInicio(String elementoInicio) {
        this.elementoInicio = elementoInicio;
    }

    public String getElementoFin() {
        return elementoFin;
    }

    public void setElementoFin(String elementoFin) {
        this.elementoFin = elementoFin;
    }

    public String getDependenciaInicio() {
        return dependenciaInicio;
    }

    public void setDependenciaInicio(String dependenciaInicio) {
        this.dependenciaInicio = dependenciaInicio;
    }

    public String getDependenciaFin() {
        return dependenciaFin;
    }

    public void setDependenciaFin(String dependenciaFin) {
        this.dependenciaFin = dependenciaFin;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listaCmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListaCmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listaCmbElementoHasta = listacmbElementoHasta;
    }

    public RegistroDataModelImpl getListaDependenciadesde() {
        return listaDependenciadesde;
    }

    public void setListaDependenciadesde(
        RegistroDataModelImpl listadependenciadesde) {
        this.listaDependenciadesde = listadependenciadesde;
    }
    
    

    public RegistroDataModelImpl getListaDependenciahasta() {
        return listaDependenciahasta;
    }

    public void setListaDependenciahasta(
        RegistroDataModelImpl listadependenciahasta) {
        this.listaDependenciahasta = listadependenciahasta;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }
}
