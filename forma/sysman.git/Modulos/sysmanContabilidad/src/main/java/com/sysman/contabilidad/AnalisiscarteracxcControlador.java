package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorEnum;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorUrlEnum;
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
 * @author NGOMEZ
 * @version 1, 05/05/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class AnalisiscarteracxcControlador extends BeanBaseModal {
    private final String compania;
    private boolean porVencimiento;
    private boolean porTercero;
    private String terceroInicial;
    private String terceroFinal;
    private Date fechaCorte;
    private String nombreTerceroInicial;
    private String nombreTerceroFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private String nombreCuentaInicial;
    private String nombreCuentaFinal;
    private int ano;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listacuentaInicial;
    private RegistroDataModelImpl listacuentaFinal;
    private Boolean terceroVisible;
    private Boolean formatoEspecial;
    private Boolean consolidado;


    /**
     * Creates a new instance of AnalisiscarteracxcControlador
     */
    public AnalisiscarteracxcControlador() {
        super();
        compania = SessionUtil.getCompania();
        fechaCorte = new Date();
        ano = SysmanFunciones.ano(fechaCorte);
        try {
            numFormulario = GeneralCodigoFormaEnum.ANALISISCARTERACXC_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AnalisiscarteracxcControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTerceroInicial();
        cargarListacuentaInicial(); 
        cargarListacuentaFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        terceroVisible = false;
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisiscarteracxcControladorUrlEnum.URL2759
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisiscarteracxcControladorUrlEnum.URL3332
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(AnalisiscarteracxcControladorEnum.TERCEROINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }
    
    public void cargarListacuentaInicial(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		AnalisiscarteracxcControladorUrlEnum.URL16209
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
    	
    	listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
    	}
    	 

    	public void cargarListacuentaFinal(){
    	
    		UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                    		AnalisiscarteracxcControladorUrlEnum.URL16207
                                                    .getValue());
    		
    		Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(AnalisiscarteracxcControladorEnum.PARAM0.getValue(),
                            cuentaInicial);
		    		
		    listacuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                urlBean.getUrlConteo().getUrl(), param, true,
	                GeneralParameterEnum.CODIGO.getName());
    	}
    

    public void oprimirImprimir() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        genInforme(ReportesBean.FORMATOS.EXCEL);
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        // Antes de generar el informe se ejecuta la rutina:
        // RevisarAfectacionesCarteraAFechaXTERCEROCXC
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaCorte",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("porVencimiento", porVencimiento
                ? "COMPROBANTE_CNT.FECHA_VCN_DOC" : "DETALLE_COMPROBANTE_CNT.FECHA");
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_ANALISISCARTERACXC_TERCEROINICIAL",
                            nombreTerceroInicial);
            parametros.put("PR_FORMS_ANALISISCARTERACXC_TERCEROFINAL",
                            nombreTerceroFinal);
            parametros.put("PR_FORMS_ANALISISCARTERACXC_FECHACORTE",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            parametros.put("PR_CON_TOTAL_TERCERO", porVencimiento
                && porTercero ? true : false);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_EXCEL", formato.equals(FORMATOS.EXCEL)?false:true);
             
            String reporte = consolidado ? "002678AnalisisCarteraCXC_Consolidado" : "000748AnalisisCarteraCXCFechavcntoxtercero";
            if(formatoEspecial) {
            	
            	if(porTercero) {
            		reporte = "800640ANALISISCARTERAXTERCERO";
            	}
            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(Reporteador.resuelveConsulta(reporte, 
            			Integer.parseInt(SessionUtil.getModulo()),
            			reemplazar).toString(), 
        				ConectorPool.ESQUEMA_SYSMAN,
        				FORMATOS.EXCEL,reporte);
            }else {

            		Reporteador.resuelveConsulta(reporte,
            				Integer.parseInt(SessionUtil.getModulo()),
            				reemplazar, parametros);

            		archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
            				ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (OutOfMemoryError | JRException
                        | IOException | ParseException | SysmanException | NumberFormatException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTerceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        terceroFinal = null;
        nombreTerceroFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTerceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }
    
    public void seleccionarFilacuentaInicial(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	cuentaInicial= SysmanFunciones
                .nvl(registroAux.getCampos().get("CODIGO"), "").toString();
    	nombreCuentaInicial = SysmanFunciones
                .nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
    	cargarListacuentaFinal();       
    	}
    
    public void seleccionarFilacuentaFinal(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	cuentaFinal= SysmanFunciones
                .nvl(registroAux.getCampos().get("CODIGO"), "").toString();
    	nombreCuentaFinal = SysmanFunciones
                .nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
    	        
    	}

    public void cambiarFechaVcnto() {
        porTercero = false;
        terceroVisible = !terceroVisible;
    }

    public boolean getPorVencimiento() {
        return porVencimiento;
    }

    public void setPorVencimiento(boolean porVencimiento) {
        this.porVencimiento = porVencimiento;
    }

    public boolean getPorTercero() {
        return porTercero;
    }

    public void setPorTercero(boolean porTercero) {
        this.porTercero = porTercero;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }
    
    public String getCuentaInicial() {
        return cuentaInicial;
    }
    
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }
    
    public String getCuentaFinal() {
        return cuentaFinal;
    }
    
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }
    
    public Date getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public String getCompania() {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreTerceroInicial() {
        return nombreTerceroInicial;
    }

    public void setNombreTerceroInicial(String nombreTerceroInicial) {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }

    public String getNombreTerceroFinal() {
        return nombreTerceroFinal;
    }

    public void setNombreTerceroFinal(String nombreTerceroFinal) {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }
    
    public String getNombreCuentaInicial() {
        return nombreCuentaInicial;
    }
    
    public String getNombreCuentaFinal() {
        return nombreCuentaFinal;
    }
    
    public void setNombreCuentaFinal(String nombreCuentaFinal) {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }
    
    public void setNombreCuentaInicial(String nombreCuentaInicial) {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }
    
    public RegistroDataModelImpl getListacuentaInicial() {
        return listacuentaInicial;
    }
    
    public void setListacuentaInicial(RegistroDataModelImpl listacuentaInicial) {
        this.listacuentaInicial = listacuentaInicial;
    }
    
    public RegistroDataModelImpl getListacuentaFinal() {
        return listacuentaFinal;
    }
    
    public void setListacuentaFinal(RegistroDataModelImpl listacuentaFinal) {
        this.listacuentaFinal = listacuentaFinal;
    }

    public Boolean getTerceroVisible() {
        return terceroVisible;
    }

    public void setTerceroVisible(Boolean terceroVisible) {
        this.terceroVisible = terceroVisible;
    }

	/**
	 * @return the formatoEspecial
	 */
	public Boolean getFormatoEspecial() {
		return formatoEspecial;
	}

	/**
	 * @param formatoEspecial the formatoEspecial to set
	 */
	public void setFormatoEspecial(Boolean formatoEspecial) {
		this.formatoEspecial = formatoEspecial;
	}

	/**
	 * @return the consolidado
	 */
	public Boolean getConsolidado() {
		return consolidado;
	}

	/**
	 * @param consolidado the consolidado to set
	 */
	public void setConsolidado(Boolean consolidado) {
		this.consolidado = consolidado;
	}

}
