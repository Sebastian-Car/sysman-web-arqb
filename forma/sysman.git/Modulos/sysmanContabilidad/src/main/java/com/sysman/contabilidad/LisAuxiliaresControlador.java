package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisAuxiliaresControladorEnum;
import com.sysman.contabilidad.enums.LisAuxiliaresControladorUrlEnum;
import com.sysman.contabilidad.enums.LisauxiliarsaldosControladorEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

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
 * @author otorres
 * @version 1, 04/04/2016
 * @modified 2,spina 07/04/2017 se refactoriza para DSS y depuracion
 * sonar
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a getSysdate a new Date().
 * @version 4, /08/2018 jgomezp Se a�aden campos y metodos de la
 * actualizacion access SysmanCT2018.06.07.
 */
@ManagedBean
@ViewScoped
public class LisAuxiliaresControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;

    private String tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private int formatoInforme;
    private Date fechaInicial;
    private Date fechaFinal;
    private String anio;
    private String reporte;
    private String consulta;
    private boolean formatoEspecialExcel;
    private boolean formatoSinCierre;

    private StreamedContent archivoDescarga;

    private String descripcionCompleta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaCentroCostoInicial;
    private RegistroDataModelImpl listaCentroCostoFinal;

    /**
     * Creates a new instance of LisAuxiliaresControlador
     */
    public LisAuxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        fechaInicial = new Date();
        fechaFinal = new Date();
        formatoInforme = 1;
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_AUXILIARES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaTipoInicial();
        cargarListaCuentaInicial();
        cargarListaTerceroInicial();
        cargarListaCentroCostoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL3488
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                         false,GeneralParameterEnum.CODIGO.getName());
        

        try {
        	listaTipoInicial.load(0,1);
        	
            Object data =listaTipoInicial.getDatasource();

            if (data != null) {

                Registro registroAux =
                    (Registro) ((List<?>) data).get(0);

                tipoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
                cargarListaTipoFinal();
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }
    }
    


    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL4272
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisAuxiliaresControladorEnum.TIPOINICIAL.getValue(),
                        tipoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL5222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        false, GeneralParameterEnum.CODIGO.getName());
        try {
        	listaCuentaInicial.load(0,1);
        	
            Object data =listaCuentaInicial.getDatasource();

            if (data != null) {

                Registro registroAux =
                    (Registro) ((List<?>) data).get(0);

                cuentaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
                cargarListaCuentaFinal();
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL5971
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(LisAuxiliaresControladorEnum.CODIGOINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
   
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                         LisAuxiliaresControladorUrlEnum.URL6436
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, false, "NIT");
        
        try {
        	listaTerceroInicial.load(0,1);
        	
            Object data =listaTerceroInicial.getDatasource();

            if (data != null) {

                Registro registroAux =
                    (Registro) ((List<?>) data).get(0);

                terceroInicial = SysmanFunciones.nvl(
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.NIT.getName()), "")
                        .toString();
                cargarListaTerceroFinal();
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL6904
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put("TERCEROINICIAL", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaCentroCostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL7434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, false,
                        GeneralParameterEnum.CODIGO.getName());
        try {
        	listaCentroCostoInicial.load(0,1);
        	
            Object data =listaCentroCostoInicial.getDatasource();

            if (data != null) {

                Registro registroAux =
                    (Registro) ((List<?>) data).get(0);

                centroCostoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
                cargarListaCentroCostoFinal();
            }

        } catch (Exception e) {

            logger.error(e.getMessage(), e);
        }
    }

    public void cargarListaCentroCostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisAuxiliaresControladorUrlEnum.URL7965
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.CENTROINICIAL.getValue(),
                        centroCostoInicial);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInformeExcel(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public String consultarInform()  {
        // reporte="000604I2LisAuxiliares"
    	try 
    	{    		
    		String reporteTercero = ejbSysmanUtil.consultarParametro(compania, "INFORME LISTADO AUXILIARES PERSONALIZADO", "1", new Date(), false);
        
    		if (formatoInforme == 1) 
    		{
	        	reporte = reporteTercero.equals("SI")
	                    ? "002439LisAuxiliares_COR"
	                        : "001871LisAuxiliares"; // Estandar 
	            String indicador=ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
	            if (indicador.equals("SI")) {
	            	consulta="800511LisAuxiliares";
	            	if(formatoSinCierre) {
	            		consulta="002847LisAuxiliares_SINCIERRE";
	            	}
				}else {
					consulta="000604I2LisAuxiliares";
					
					if(formatoSinCierre) {
						consulta="002848I2LisAuxiliares_SINCIERRE";
	            	}
				}
            
    		}
        else if (formatoInforme == 2) {
        	reporte = reporteTercero.equals("SI")
                    ? "002440LisAuxiliaresCO_COR"
                        : "001872LisAuxiliaresCO"; // CO
            String indicador=ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", 								"-1", new Date(), false);
            if (indicador.equals("SI")) {
            	consulta="800511LisAuxiliares";
            	if(formatoSinCierre) {
					consulta="002847LisAuxiliares_SINCIERRE";
            	}
			}else {
				consulta="000604I2LisAuxiliares";
				if(formatoSinCierre) {
					consulta="002848I2LisAuxiliares_SINCIERRE";
            	}
			}
        }
        else if (formatoInforme == 3) {
        	reporte = reporteTercero.equals("SI")
                    ? "002441LisAuxiliaresUPC_COR"
                        : "001873LisAuxiliaresUPC"; // UPC
            String indicador=ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", 								"-1", new Date(), false);
            if (indicador.equals("SI")) {
            	consulta="800511LisAuxiliares";
            	if(formatoSinCierre) {
					consulta="002847LisAuxiliares_SINCIERRE";
            	}
			}else {
				consulta="000604I2LisAuxiliares";
				if(formatoSinCierre) {
					consulta="002848I2LisAuxiliares_SINCIERRE";
            	}
			}
        }
        else if (formatoInforme == 4) {
        	 try {
				reporte = ejbSysmanUtil.consultarParametro(compania,
				         "FORMATO LISTA AUXILIAR CONTABLE",
				         modulo, new Date(),false);
				consulta=reporte;
				
				if(formatoSinCierre) {
					consulta = ejbSysmanUtil.consultarParametro(compania,
					         "FORMATO LISTA AUXILIAR CONTABLE SIN CIERRE",
					         modulo, new Date(),false);
            	}
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    	 }
        catch ( SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                + e.getMessage());
        }
        return reporte;
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (fechaInicial.before(fechaFinal)
            || (fechaInicial.equals(fechaFinal))) {
            try {
                consultarInform();
                Map<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                
               

                descripcionCompleta = ejbSysmanUtil.consultarParametro(compania,
                                "DESCRIPCION COMPLETA AUXILIAR CONTABLE",
                                modulo, new Date(), true);

                reemplazar.put("compania", compania);
                reemplazar.put("cuentaInicial", cuentaInicial);
                reemplazar.put("cuentaFinal", cuentaFinal);
                reemplazar.put("fechaInicial",
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.formatearFecha(fechaFinal));
                reemplazar.put("comprobanteInicial", tipoInicial);
                reemplazar.put("comprobanteFinal", tipoFinal);
                reemplazar.put("centroCostoInicial", centroCostoInicial);
                reemplazar.put("centroCostoFinal", centroCostoFinal);
                reemplazar.put("terceroInicial", terceroInicial);
                reemplazar.put("terceroFinal", terceroFinal);

                parametros.put("PR_TITULO_CUENTAS", "ENTRE CUENTAS "
                    + cuentaInicial + " Y " + cuentaFinal);
                parametros.put("PR_TITULO_FECHAS",
                                "AUXILIAR CONTABLE ENTRE FECHAS "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial)
                                    + " Y "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal));
                parametros.put("PR_DESCRIPCION", descripcionCompleta);

                parametros.put("PR_FORMATO_ESPECIAL_EXCEL",
                                formatoEspecialExcel);
                
                parametros.put("PR_USERNAME", 
        				SessionUtil.getUser().getCodigo());

                Reporteador.resuelveConsulta(consulta,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException
                            | ParseException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                    + e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB49"));
        }
    }
    
    public void generaInformeExcel(ReportesBean.FORMATOS formato) {
        if (fechaInicial.before(fechaFinal)
            || (fechaInicial.equals(fechaFinal))) {
            try {
                consultarInform();
                Map<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                
               

                descripcionCompleta = ejbSysmanUtil.consultarParametro(compania,
                                "DESCRIPCION COMPLETA AUXILIAR CONTABLE",
                                modulo, new Date(), true);

                reemplazar.put("compania", compania);
                reemplazar.put("cuentaInicial", cuentaInicial);
                reemplazar.put("cuentaFinal", cuentaFinal);
                reemplazar.put("fechaInicial",
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.formatearFecha(fechaFinal));
                reemplazar.put("comprobanteInicial", tipoInicial);
                reemplazar.put("comprobanteFinal", tipoFinal);
                reemplazar.put("centroCostoInicial", centroCostoInicial);
                reemplazar.put("centroCostoFinal", centroCostoFinal);
                reemplazar.put("terceroInicial", terceroInicial);
                reemplazar.put("terceroFinal", terceroFinal);

                parametros.put("PR_TITULO_CUENTAS", "ENTRE CUENTAS "
                    + cuentaInicial + " Y " + cuentaFinal);
                parametros.put("PR_TITULO_FECHAS",
                                "AUXILIAR CONTABLE ENTRE FECHAS "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial)
                                    + " Y "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal));
                parametros.put("PR_DESCRIPCION", descripcionCompleta);

                parametros.put("PR_FORMATO_ESPECIAL_EXCEL",
                                formatoEspecialExcel);
                
                parametros.put("PR_USERNAME", 
        				SessionUtil.getUser().getCodigo());
                String indicador=ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
                if (indicador.equals("SI")) {
                	String strSql = Reporteador.resuelveConsulta(consulta,
                    		Integer.parseInt(SessionUtil.getModulo()), reemplazar);
                    

                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.EXCEL, reporte);
				}else {
					Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);
					archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
				}

                
                 
                
                
            }
            catch (JRException | IOException | SysmanException
                            | ParseException | SystemException | SQLException | DRException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                    + e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB49"));
        }
    }
    
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }

    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCentroCostoFinal();
    }

    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
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

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    public int getFormatoInforme() {
        return formatoInforme;
    }

    public void setFormatoInforme(int formatoInforme) {
        this.formatoInforme = formatoInforme;
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

    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;

        int it = KeyEvent.VK_CONTROL + KeyEvent.VK_0;
    }

    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }
    
    public boolean isFormatoSinCierre() {
        return formatoSinCierre;
    }

    public void setFormatoSinCierre(boolean formatoSinCierre) {
        this.formatoSinCierre = formatoSinCierre;
    }
    
    
    
}
