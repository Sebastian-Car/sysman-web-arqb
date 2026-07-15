package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenConceptosControladorEnum;
import com.sysman.nomina.enums.ResumenConceptosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 24/08/2015
 * 
 * @author eamaya
 * @version 2.0, 26/10/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class ResumenConceptosControlador extends BeanBaseModal {

    private final String compania;
    private String modulo;
    private String ano1;
    private String mes1;
    private String periodo1;
    private boolean ckTercero;
    private boolean ckDetDescuento;
    private boolean ckResumenDescuentos;
    private boolean visibleResumenDesc;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private final String anioSession = (String) SessionUtil
                    .getSessionVar("anioNomina");
    private final String mesSession = (String) SessionUtil
                    .getSessionVar("mesNomina");
    private final String periodoSession = (String) SessionUtil
                    .getSessionVar("periodoNomina");

    private final String proceso = (String) SessionUtil
                    .getSessionVar("procesoNomina");
    private StreamedContent archivoDescarga;
    
    @EJB
    //EjbSysmanUtil ejbSysmanUtl;
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ResumenConceptosControlador
     */
    public ResumenConceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_CONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenConceptosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        ano1 = anioSession;
        mes1 = mesSession;
        periodo1 = periodoSession;

        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        abrirFormulario();
        
        String visibleResumenDescuentos = "";
        
        try 
		{
			visibleResumenDescuentos = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"VISIBLE INDICADOR RESUMEN DE DESCUENTOS", modulo,
				    new Date(),false),
				    "NO");
		} 
		catch (SystemException e) 
		{
			e.printStackTrace();
		}
		
    	if(visibleResumenDescuentos.equals("SI")) 
    	{
    		visibleResumenDesc = true;
    	}
    	else
    	{
    		visibleResumenDesc = false;
    	}

    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenConceptosControladorUrlEnum.URL2846
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ResumenConceptosControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenConceptosControladorUrlEnum.URL3599
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ResumenConceptosControladorEnum.PROCESO.getValue(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);
        param.put(GeneralParameterEnum.MES.getName(),
                        mes1);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenConceptosControladorUrlEnum.URL4753
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        getInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        getInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }
    
    private String consultarParametro(String nombre, boolean mayus)
            throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                new Date(), mayus);
    }


    public void getInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            if (ckTercero) {
                String reporte = "000398AcumVarios";
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("proceso", proceso);
                reemplazar.put("ano", ano1);
                reemplazar.put("mes", mes1);
                reemplazar.put("periodo", periodo1);

                Map<String, Object> parametros = new HashMap<>();
                // MANEJO DE PARAMETROS DEL REPORTE
                parametros.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso().getNombre());
                parametros.put("PR_PERIODO1", periodo1);
                parametros.put("PR_ANO1", ano1);
                parametros.put("PR_MES1",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mes1)]);
                Reporteador.resuelveConsulta("001868AcumVarios",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed(reporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }else if(ckDetDescuento) {
            		HashMap<String, Object> reemplazar = new HashMap<>();
                    reemplazar.put("proceso", proceso);
                    reemplazar.put("ano", ano1);
                    reemplazar.put("mes", mes1);
                    reemplazar.put("periodo", periodo1);

                    Map<String, Object> parametros = new HashMap<>();
                    // MANEJO DE PARAMETROS DEL REPORTE
                    parametros.put("PR_NOMBREEMPRESA",
                                    SessionUtil.getCompaniaIngreso().getNombre());
                    parametros.put("PR_PERIODO1", periodo1);
                    parametros.put("PR_ANO1", ano1);
                    parametros.put("PR_MES1",
                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                    .parseInt(mes1)]);
    				String[] informe = new String[2];
    				informe[0] = "002296DetalleDescuentosNomina";
    				informe[1] = "002297RelacionDescuentosNomina";
    				Reporteador.resuelveConsulta(informe[0], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
    						parametros);

    				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

    				salidas[0] = JsfUtil.serializarReporte(informe[0], parametros, ConectorPool.ESQUEMA_SYSMAN,
    						formato);

    				Reporteador.resuelveConsulta(informe[1], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
    						parametros);

    				salidas[1] = JsfUtil.serializarReporte(informe[1], parametros, ConectorPool.ESQUEMA_SYSMAN,
    						formato);
    				String[] nombresArchivos = new String[2];

    				if (ReportesBean.FORMATOS.PDF.equals(formato)) {
    					nombresArchivos[0] = "002296DetalleDescuentosNomina.pdf";
    					nombresArchivos[1] = "002297RelacionDescuentosNomina.pdf";
    				}else {
    					nombresArchivos[0] = "002296DetalleDescuentosNomina.xlsx";
    					nombresArchivos[1] = "002297RelacionDescuentosNomina.xlsx";    					
    				}
    				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);
            	}else if(ckResumenDescuentos) {
            		HashMap<String, Object> reemplazar = new HashMap<>();
	                reemplazar.put("ano1", ano1);
	                reemplazar.put("mes1", mes1);
	                reemplazar.put("periodo1", periodo1);
	                
	                Map<String, Object> parametros = new HashMap<>();
	                String nitcuenta = consultarParametro(
	                        "NIT CUENTA", false);
	                
	                String nitsindicato = consultarParametro(
	                        "NIT SINDICATO", false);
	                
	                String nitsintra = consultarParametro(
	                        "NIT SINTRAGOBERNAR", false);
	                
	                parametros.put("PR_NOMBREEMPRESA",
	                                SessionUtil.getCompaniaIngreso().getNombre());
	                parametros.put("PR_PERIODO", periodo1);
	                parametros.put("PR_ANO", ano1);
	                parametros.put("PR_NOMBREMES",
	                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
	                                                .parseInt(mes1)]);
	                
	                parametros.put("PR_NIT_CUENTA", nitcuenta);
	                parametros.put("PR_NIT_SINDICATO", nitsindicato);
	                
	                parametros.put("PR_NIT_SINTRAGOBERNAR", nitsintra);
	                
	                Reporteador.resuelveConsulta("002223ResumenDescuentosNari",
	                		Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

	                archivoDescarga = JsfUtil.exportarStreamed(
            		         "002223ResumenDescuentosNari", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        		}else {
	                HashMap<String, Object> reemplazar = new HashMap<>();
	                reemplazar.put("ano1", ano1);
	                reemplazar.put("mes1", mes1);
	                reemplazar.put("periodo1", periodo1);
	                Map<String, Object> parametros = new HashMap<>();
	                // MANEJO DE PARAMETROS DEL REPORTE
	                parametros.put("PR_NOMBREEMPRESA",
	                                SessionUtil.getCompaniaIngreso().getNombre());
	                parametros.put("PR_PERIODO", periodo1);
	                parametros.put("PR_ANO", ano1);
	                parametros.put("PR_NOMBREMES",
	                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
	                                                .parseInt(mes1)]);
	               
	                String nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	     			        "FORMATO RESUMEN CONCEPTOS", modulo,
	     			        new Date(),false),
	     			        "000176ResumenDescuentos");
	                
	                Reporteador.resuelveConsulta(nombreReporte,
	                                Integer.parseInt(SessionUtil.getModulo()),
	                                reemplazar, parametros);
	
	                archivoDescarga = JsfUtil.exportarStreamed(
	                		         nombreReporte, parametros,
	                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (JRException | IOException | SysmanException | SystemException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo1() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the ckTercero
     */
    public boolean isCkTercero() {
        return ckTercero;
    }

    /**
     * @param ckTercero
     * the ckTercero to set
     */
    public void setCkTercero(boolean ckTercero) {
        this.ckTercero = ckTercero;
    }

	public boolean isCkDetDescuento() {
		return ckDetDescuento;
	}

	public void setCkDetDescuento(boolean ckDetDescuento) {
		this.ckDetDescuento = ckDetDescuento;
	}
	
	public boolean isCkResumenDescuentos() {
		return ckResumenDescuentos;
	}
	
	public void setCkResumenDescuentos(boolean ckResumenDescuentos) {
		this.ckResumenDescuentos = ckResumenDescuentos;
	}
	
	public boolean isVisibleResumenDesc() {
		return visibleResumenDesc;
	}
	
	public void setVisibleResumenDesc(boolean visibleResumenDesc) {
		this.visibleResumenDesc = visibleResumenDesc;
	}

}
