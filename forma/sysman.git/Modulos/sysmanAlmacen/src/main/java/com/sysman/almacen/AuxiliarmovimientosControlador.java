package com.sysman.almacen;

import com.sysman.almacen.enums.AuxiliarmovimientosControladorEnum;
import com.sysman.almacen.enums.AuxiliarmovimientosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 24/11/2015
 *
 * -- Modificado por lcortes 26/04/2017. Refactorizacion de codigo.
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class AuxiliarmovimientosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    /** Constante a nivel de clase que aloja el valor CODIGO */
    private final String codigo;

    private String tipoInicial;
    private String tipoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String reporte;
    private boolean excelPlano;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of AuxiliarmovimientosControlador
     */
    public AuxiliarmovimientosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigo = "CODIGO";
        try {
            // 363
            numFormulario = GeneralCodigoFormaEnum.AUXILIARMOVIMIENTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AuxiliarmovimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaTipoInicial();
        cargarListaTipoFinal();
        abrirFormulario();
    }

    public void cargarListaTipoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarmovimientosControladorUrlEnum.URL2613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListaTipoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarmovimientosControladorUrlEnum.URL3383
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxiliarmovimientosControladorEnum.PARAM0.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
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

    public void genInforme(ReportesBean.FORMATOS formato) {
    	archivoDescarga = null;
    	String reporteAux = "";
    	try {

    		String valorParametro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
    				compania,"MANEJA INFORME RELACION MOVIMIENTOS ESPECIAL",SessionUtil.getModulo(),new Date(),true ),"NO");

    		boolean manInforme = "SI".equals(valorParametro);
    		
    		boolean excelNombreMov = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
    				compania,"GENERA INFORME AUXILIAR DE MOVIMIENTO POR TIPO DE MOVIMIENTO CON COLUMNA NOMBREMOVIMIENTO",
    				SessionUtil.getModulo(),new Date(),true ),"NO"));

    		if (manInforme) {
    			reporteAux = "002786AuxiliarMovimientosTipoE";
    		}else {
    			reporteAux = "000395AuxiliarMovimientosTipo";
    		}

    		HashMap<String, Object> reemplazar = new HashMap<>();


    		String desdeAux = SysmanFunciones
    				.convertirAFechaCadena(fechaInicial);

    		String hastaAux = SysmanFunciones.convertirAFechaCadena(fechaFinal);

    		reemplazar.put("tipoInicial", tipoInicial);
    		reemplazar.put("tipoFinal", tipoFinal);
    		reemplazar.put("desde", desdeAux);
    		reemplazar.put("hasta", hastaAux);
    		// MANEJO DE PARAMETROS DE REEMPLAZO
    		Map<String, Object> parametros = new HashMap<>();
    		// MANEJO DE PARAMETROS DEL REPORTE
    		String strSql = Reporteador
    				.resuelveConsulta(reporteAux,
    						Integer.parseInt(
    								SessionUtil.getModulo()),
    						reemplazar);
    		parametros.put("PR_STRSQL", strSql);
    		parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_TIPOINICIAL",
    				tipoInicial);
    		parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_TIPOFINAL", tipoFinal);
    		parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_FECHAINICIAL",
    				desdeAux);
    		parametros.put("PR_FORMS_AUXILIARMOVIMIENTOS_FECHAFINAL", hastaAux);


    		if(excelPlano == true) 
    		{
    			if (manInforme) {
    				reporteAux = "800709AuxiliarMovimientosTipoE";
    			}else {
    				reporteAux = "800414AuxiliarMovimientosTipo";
    			}

    			if (excelNombreMov) {
    				reporteAux = "800727AuxiliarMovimientosTipo";
    			}
    			
    			String datosExcel = Reporteador.resuelveConsulta(reporteAux, 
    					Integer.parseInt(modulo),
    					reemplazar);

    			try {
    				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, 
    						ConectorPool.ESQUEMA_SYSMAN,
    						FORMATOS.EXCEL,reporteAux);
    				return;
    			} catch (SQLException | DRException e) {
    				// TODO Auto-generated catch block
    				((Throwable) e).printStackTrace();
    			}

    		}


    		archivoDescarga = JsfUtil.exportarStreamed(
    				reporteAux, parametros,
    				ConectorPool.ESQUEMA_SYSMAN, formato);
    	}
    	catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = (String) registroAux.getCampos().get(codigo);
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = (String) registroAux.getCampos().get(codigo);
    }

    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }
    /**
     * @return the formatoEspecialExcel
     */
    public boolean isexcelPlano() {
        return excelPlano;
    }

    /**
     * @param formatoEspecialExcel
     * the formatoEspecialExcel to set
     */
    public void setexcelPlano(boolean excelPlano) {
        this.excelPlano = excelPlano;
    }
    @Override
    public void abrirFormulario() {
        // Codigo
        fechaInicial = new Date();
        fechaFinal = new Date();
    }
}
