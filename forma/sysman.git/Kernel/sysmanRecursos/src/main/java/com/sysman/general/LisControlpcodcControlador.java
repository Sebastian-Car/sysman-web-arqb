package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.LisControlpcodcControladorEnum;
import com.sysman.general.enums.LisControlpcodcControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 12/11/2015
 * @modified jsforero
 * @version 2. 05/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 */
@ManagedBean
@ViewScoped
public class LisControlpcodcControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String elementoInicial;
    private String elementoFinal;
    private String nombreElemInicial;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreElemFinal;
    private RegistroDataModelImpl listaCmbElementoDesde;
    private RegistroDataModelImpl listaCmbElementoHasta;


    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of LisControlpcodcControlador
     */
    public LisControlpcodcControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_CONTROLPCODC_CONTROLADOR.getCodigo();
            validarPermisos();
        } catch (SysmanException ex) {
            Logger.getLogger(LisControlpcodcControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde() {
        elementoFinal = LisControlpcodcControladorEnum.PARAM3.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisControlpcodcControladorUrlEnum.URL3623.getValue()); 	
        Map<String,Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.name(),compania);

        listaCmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(),param, true, LisControlpcodcControladorEnum.PARAM1.getValue());

    }

    public void cargarListacmbElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        LisControlpcodcControladorUrlEnum.URL13825.
                        getValue()); 	
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(),compania);
        param.put(LisControlpcodcControladorEnum.PARAM0.getValue(),elementoInicial);

        listaCmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(),
                        param, true, LisControlpcodcControladorEnum.PARAM1.getValue());
    }

    public void oprimirPresentar() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        //</CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = registroAux.getCampos().get(LisControlpcodcControladorEnum.PARAM1.getValue()).toString();
        nombreElemInicial =  registroAux.getCampos().get(LisControlpcodcControladorEnum.PARAM2.getValue()).toString();
        if (!elementoFinal.equals(LisControlpcodcControladorEnum.PARAM3.getValue())) {
            elementoFinal = null;
            nombreElemFinal = null;
        }
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilaCmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal =  registroAux.getCampos().get(LisControlpcodcControladorEnum.PARAM1.getValue()).toString();
        nombreElemFinal = registroAux.getCampos().get(LisControlpcodcControladorEnum.PARAM2.getValue()).toString();
    }

    public void cambiarFechaFinal() {
        //<CODIGO_DESARROLLADO>
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeError("La Fecha Final debe ser mayor a la Fecha Inicial.");
            fechaFinal = null;
        }
        //</CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        String reporte="000377InfControlPCmODC";

        try {
            archivoDescarga = null;
            HashMap<String,Object> parametros = new HashMap<>();
            HashMap<String,Object> reemplazar = new HashMap<>();
            String strSql;
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));
            strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_SIGLACOMPANIA", SessionUtil.getCompaniaIngreso().getSigla());   
            parametros.put("PR_ENCABEZADO", SysmanFunciones.convertirAFechaCadena(fechaInicial) + " a "
                            + SysmanFunciones.convertirAFechaCadena(fechaFinal));  
            parametros.put("PR_STRSQL", strSql);			

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,ConectorPool.ESQUEMA_SYSMAN , formato);
        }
        catch (JRException | IOException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()+" "+reporte);
        }  catch (ParseException e1) {           
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        //<CODIGO_DESARROLLADO>
        /*
         FR336-AL_ABRIR
         Private Sub Form_Open(Cancel As Integer)
         formularioAbrir 1, Me.Name
         DoCmd.Restore
         End Sub
         */
        //</CODIGO_DESARROLLADO>
    }
    @Override
    public FormContinuoService getService() {
        return service;
    }
    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public String getElementoInicial() {
        return elementoInicial;
    }

    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    public String getElementoFinal() {
        return elementoFinal;
    }

    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    public String getNombreElemInicial() {
        return nombreElemInicial;
    }

    public void setNombreElemInicial(String nombreElemInicial) {
        this.nombreElemInicial = nombreElemInicial;
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

    public String getNombreElemFinal() {
        return nombreElemFinal;
    }

    public void setNombreElemFinal(String nombreElemFinal) {
        this.nombreElemFinal = nombreElemFinal;
    }


    public RegistroDataModelImpl getListaCmbElementoDesde() {
        return listaCmbElementoDesde;
    }

    public void setListaCmbElementoDesde(RegistroDataModelImpl listaCmbElementoDesde) {
        this.listaCmbElementoDesde = listaCmbElementoDesde;
    }

    public RegistroDataModelImpl getListaCmbElementoHasta() {
        return listaCmbElementoHasta;
    }

    public void setListaCmbElementoHasta(RegistroDataModelImpl listaCmbElementoHasta) {
        this.listaCmbElementoHasta = listaCmbElementoHasta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}
