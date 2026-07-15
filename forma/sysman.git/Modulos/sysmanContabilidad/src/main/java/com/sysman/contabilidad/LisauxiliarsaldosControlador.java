package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisauxiliarsaldosControladorEnum;
import com.sysman.contabilidad.enums.LisauxiliarsaldosControladorUrlEnum;
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

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
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
import com.sysman.util.SysmanFunciones;
/**
 *
 * @author NGOMEZ
 * @version 1, 26/04/2016
 * @modifed jsforero
 * @version 2. 07/04/2017 Se realizo el refactory. Ademas se hicieron las respectivas Correcciones del sonar.
 * @version 3. 21/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 *
 * -- Modificado por lcortes 23/05/2017. Se ajustan las consultas de las listas cuenta inicial y final para que solo muestren las cuentas hijas y se realizo correccion de SonarLint. Se cambia el
 * nombre de los metodos onRowSelect por selecionarFila.
 *
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class LisauxiliarsaldosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private final String msmInterConst;
    private final String tdiConst;
    private final String falseConst;
    private final String codigoConst;
    private final String filtroConst;
    private final String filtroTerConst;

    private String condicionReferencias;
    private String agruparPorId;
    private String porNit;
    private String porNombre;
    private String saltoPagina;
    private String filtroTercero;
    private String conFirmas;
    private String tamano;
    private boolean formatoEspecial;
    private String tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String referenciaInicial;
    private String referenciaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreTerceroInicial;
    private String nombreTerceroFinal;
    private int anio;
    private boolean terceroVisible;
    private boolean tamanoVisible;
    private boolean  funcionarioVisible ;
    private boolean formatoSinCierre;
    
    

	private String columnaFuncionario;
	private boolean manejaEsp;
	private boolean especial;
 

	private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaCmbCentroCInicial;
    private RegistroDataModelImpl listaCmbCentroCFinal;
    private RegistroDataModelImpl listareferenciaInicial;
    private RegistroDataModelImpl listaReferenciafinal;

    private String descripcionCompleta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    
    
    private String sinSubTotales;

    /**
     * Creates a new instance of LisauxiliarsaldosControlador
     */
    public LisauxiliarsaldosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        msmInterConst = "MSM_TRANS_INTERRUMPIDA";
        tdiConst = "TG_ID";
        falseConst = "false";
        codigoConst = "CODIGO";
        filtroConst = "filtrosTercero";
        filtroTerConst = "filtrosTer";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISAUXILIARSALDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LisauxiliarsaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        cargarListareferenciaInicial();
        cargarListaReferenciafinal();

    }

    @Override
    public void abrirFormulario()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cargarListaCuentaInicial();
        filtroTercero = "true";
        porNit = "true";
        porNombre = falseConst;
        agruparPorId = "true";
        columnaFuncionario = "false";
        terceroVisible = true;
        
        
        
        try
        {
           funcionarioVisible = "SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
     				"MANEJA NOMBRE FUNCIONARIO EN PLANO CONTABILIZAR", "6", new Date(), true), "NO"));

            tamanoVisible = "NO"
                            .equals(SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "FORMATO CALIDAD",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));
            
            manejaEsp = "SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
     				"MANEJA INFORME ESPECIAL AUXILIAR SALDO",SessionUtil.getModulo(),new Date(),true),"NO"));
        }
        catch (SystemException e)
        {
            Logger.getLogger(LisauxiliarsaldosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL4567
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaTipoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL5040
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.TIPOINICIAL.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaCuentaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL5573
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL5972
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(LisauxiliarsaldosControladorEnum.CODIGOINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaTerceroInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL6436
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL6904
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.TERCEROINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaCmbCentroCInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL7434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaCmbCentroCInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListareferenciaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL1717
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listareferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaReferenciafinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL1718
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.REFERENCIAINICIAL.getValue(),
                        referenciaInicial);

        listaReferenciafinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaCmbCentroCFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxiliarsaldosControladorUrlEnum.URL7965
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.CENTROINICIAL.getValue(),
                        centroInicial);

        listaCmbCentroCFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void oprimirImprimir()
    {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        genInformeExcel(ReportesBean.FORMATOS.EXCEL);
    }
    
    public void oprimircsv() 
    {
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar = asignarReemplazosConsulta();
			
			archivoDescarga = null;
			
			String strSql = "";
			
			String consulta = formatoEspecial?"800510LisAuxiliarSaldosPorIdoficio":"8000574LisAuxiliarSaldos";
			
			if(formatoSinCierre) {
				consulta = formatoEspecial?"800725LisAuxiliarSaldosPorIdoficio_SINCIERRE":"800726LisAuxiliarSaldos_SINCIERRE";
			}
			
				 strSql = Reporteador.resuelveConsulta(consulta,
						Integer.parseInt(SessionUtil.getModulo()), reemplazar);

			try {
				
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.CSV);
				
			} catch (JRException | IOException | SQLException | DRException | SysmanException e) {
				JsfUtil.agregarMensajeError(
						idioma.getString(msmInterConst)
						+ e.getMessage());
				Logger.getLogger(LisauxiliarsaldosControlador.class.getName())
				.log(Level.SEVERE, null, e);
			}finally {
			}
         
    }



    public void genInforme(ReportesBean.FORMATOS formato)
    {

        if (SysmanFunciones.getParteFecha(fechaInicial,
                        Calendar.YEAR) != SysmanFunciones.getParteFecha(
                                        fechaFinal, Calendar.YEAR))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB510"));
            return;
        }

        archivoDescarga = null;
        try
        {
        	
        	 
        	String consultareporte = "";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar = asignarReemplazosConsulta();
            Map<String, Object> parametros = new HashMap<>();
            descripcionCompleta = ejbSysmanUtil.consultarParametro(compania,
                            "DESCRIPCION COMPLETA AUXILIAR CONTABLE", modulo,
                            new Date(), true);
            sinSubTotales = ejbSysmanUtil.consultarParametro(compania,
                            "AUXILIAR CON SALDOS SIN SUBTOTALES", modulo,
                            new Date(), true);
            parametros = asignarParametrosReporte();
            String reporte;
            if (!tamanoVisible)
            {
                reporte = "000680LisAuxiliarSaldosCOS";
                consultareporte = reporte;
            }
            else
            {
                if (sinSubTotales.equals("SI"))
                {
                    reporte = "true".equals(tamano)
                        ? "000682LisAuxiliarSaldosPorIdoficio"
                        : "000676LisAuxiliarSaldosinSubtotales";
                    
                    consultareporte = reporte;
                    if("true".equals(tamano) && formatoSinCierre) {
                    	consultareporte = "002849LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                    }
                    if(!"true".equals(tamano) && formatoSinCierre) {
                    	consultareporte = "002850LisAuxiliarSaldosinSubtotales_SINCIERRE";
                    }
                    
                }
                else
                {
                    reporte = "002115LISAUXILIARSALDOSSINREF";
                    consultareporte = reporte;
                }
            }

            // String baseAuxiliar = Reporteador.resuelveConsulta(
            // "800044BaseAuxiliares",
            // Integer.parseInt(SessionUtil.getModulo()),
            // reemplazar);
            // reemplazar.put("baseAuxiliar", baseAuxiliar);

            if (funcionarioVisible &&  "true".equals(columnaFuncionario) ) {
            	reporte = "002730LISAUXILIARSALDOSSINREF";
            	consultareporte = reporte;
            	if(formatoSinCierre) {
                	consultareporte = "002852LISAUXILIARSALDOSSINREF_SINCIERRE";
                }
            	Reporteador.resuelveConsulta(consultareporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
            }else {
            	
            	reporte = "000682LisAuxiliarSaldosPorIdoficio";
            	consultareporte = reporte;
            	if(formatoSinCierre) {
                	consultareporte = "002849LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                }
            	
	            Reporteador.resuelveConsulta(consultareporte,
	                            Integer.parseInt(SessionUtil.getModulo()),
	                            reemplazar, parametros);
            }
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msmInterConst)
                                + ex.getMessage());
            Logger.getLogger(LisauxiliarsaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void genInformeExcel(ReportesBean.FORMATOS formato)
    {
        if (SysmanFunciones.getParteFecha(fechaInicial,
                        Calendar.YEAR) != SysmanFunciones.getParteFecha(
                                        fechaFinal, Calendar.YEAR))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB510"));
            return;
        }
        String consultareporte = "";
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar = asignarReemplazosConsulta();
            Map<String, Object> parametros = new HashMap<>();
            descripcionCompleta = ejbSysmanUtil.consultarParametro(compania,
                            "DESCRIPCION COMPLETA AUXILIAR CONTABLE", modulo,
                            new Date(), true);
            sinSubTotales = ejbSysmanUtil.consultarParametro(compania,
                            "AUXILIAR CON SALDOS SIN SUBTOTALES", modulo,
                            new Date(), true);
            parametros = asignarParametrosReporte();
            String reporte;
            if (!tamanoVisible)
            {
                reporte = "000680LisAuxiliarSaldosCOS";
                consultareporte = reporte;
                
            }
            else
            {
                if (sinSubTotales.equals("SI"))
                {
                    reporte = "true".equals(tamano)
                        ? "000682LisAuxiliarSaldosPorIdoficio"
                        : especial?"002895LisAuxiliarSaldosinSubtotales":"000676LisAuxiliarSaldosinSubtotales";
                    consultareporte = reporte;
                    
                    if(!"true".equals(tamano) && formatoSinCierre) {
                    	consultareporte = "002849LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                    }
                    
                    if(!"true".equals(tamano) && formatoSinCierre) {
                    	consultareporte = "002850LisAuxiliarSaldosinSubtotales_SINCIERRE";
                    }
                    
                    
                }
                else
                {
                    reporte = "002115LISAUXILIARSALDOSSINREF";
                    consultareporte = reporte;
                }
            }

            // String baseAuxiliar = Reporteador.resuelveConsulta(
            // "800044BaseAuxiliares",
            // Integer.parseInt(SessionUtil.getModulo()),
            // reemplazar);
            // reemplazar.put("baseAuxiliar", baseAuxiliar);
            String indicador = ejbSysmanUtil.consultarParametro(compania, "EXCEL PLANO AUXILIARES", "-1", new Date(), false);
            if (indicador.equals("SI"))
            {
               
                if(formatoEspecial) {
                	consultareporte = "800510LisAuxiliarSaldosPorIdoficio";
                	if(formatoSinCierre) {
                    	consultareporte = "800725LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                    }
                	
                	 String strSql = Reporteador.resuelveConsulta(consultareporte,
                             Integer.parseInt(SessionUtil.getModulo()), reemplazar);
                	 
                	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.EXCEL, reporte);
                } else {
                	
                	consultareporte = "000682LisAuxiliarSaldosPorIdoficio";
                	if(formatoSinCierre) {
                    	consultareporte = "002849LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                    }
                	
                	 Reporteador.resuelveConsulta(consultareporte,
                             Integer.parseInt(SessionUtil.getModulo()),
                             reemplazar, parametros);
                	archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
                }
            }
            else
            {
            	 if (funcionarioVisible &&  "true".equals(columnaFuncionario) ) {
                 	reporte = "002730LISAUXILIARSALDOSSINREF";
                 	consultareporte = reporte;
                 	if(formatoSinCierre) {
                    	consultareporte = "002852LISAUXILIARSALDOSSINREF_SINCIERRE";
                    }
                 	Reporteador.resuelveConsulta(consultareporte,
                             Integer.parseInt(SessionUtil.getModulo()),
                             reemplazar, parametros);
                 }else {
                	 
                	 consultareporte = "000682LisAuxiliarSaldosPorIdoficio";
                  	if(formatoSinCierre) {
                     	consultareporte = "002849LisAuxiliarSaldosPorIdoficio_SINCIERRE";
                     }	 
                 
                Reporteador.resuelveConsulta(consultareporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                 }
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

        }
        catch (JRException | IOException | SysmanException
                        | SystemException | SQLException | DRException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msmInterConst)
                                + ex.getMessage());
            Logger.getLogger(LisauxiliarsaldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private Map<String, Object> asignarParametrosReporte()
    {
        Map<String, Object> parametros = new HashMap<>();
        try
        {
            parametros.put("PR_CONFIRMAS",
                            "true".equals(conFirmas) ? true : false);
            parametros.put("PR_PAGINAS",
                            "true".equals(saltoPagina) ? true : false);
            parametros.put("PR_FORMS_LISAUXILIARSALDOS_FECHAINICIAL",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                            .getParteFecha(fechaInicial,
                                                            Calendar.MONTH)
                                + 1].toUpperCase()
                                + " "
                                + SysmanFunciones.getParteFecha(fechaInicial,
                                                Calendar.DAY_OF_MONTH)
                                + " A "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                                .getParteFecha(fechaFinal,
                                                                Calendar.MONTH)
                                    + 1].toUpperCase()
                                + " "
                                + SysmanFunciones.getParteFecha(fechaFinal,
                                                Calendar.DAY_OF_MONTH)
                                + " DE "
                                + anio);
            parametros.put("PR_FORMS_LISAUXILIARSALDOS_FECHAFINAL", "");
            parametros.put("PR_FORMS_LISAUXILIARSALDOS_CUENTAINICIAL",
                            cuentaInicial);
            parametros.put("PR_FORMS_LISAUXILIARSALDOS_CUENTAFINAL",
                            cuentaFinal);
            Date fechaAct = new Date();
            parametros.put("PR_NOMBRE_TESORERO",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE TESORERO",
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            ""));

            parametros.put("PR_CARGO_TESORERO",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO TESORERO",
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            ""));
            parametros.put("PR_DESCRIPCION", descripcionCompleta);
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecial);
            parametros.put("PR_USERNAME", SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametros;
    }

    private HashMap<String, Object> asignarReemplazosConsulta()
    {
        HashMap<String, Object> reemplazos = new HashMap<>();
        try
        {
            reemplazos.put("referenciaInicial", referenciaInicial);
            reemplazos.put("referenciafinal", referenciaFinal);
            reemplazos.put("id_codigo", "true".equals(agruparPorId)
                ? idioma.getString(tdiConst)
                : idioma.getString("TG_CODIGO2"));
            reemplazos.put("id_cuenta", "true".equals(agruparPorId)
                ? idioma.getString(tdiConst)
                : idioma.getString("TG_CUENTA"));
            reemplazos.put("agrupa", "true".equals(agruparPorId)
                ? idioma.getString(tdiConst)
                : idioma.getString("TG_CODIGO2"));
            reemplazos.put("anio", anio);
            reemplazos.put("tipoInicial", tipoInicial);
            reemplazos.put("tipoFinal", tipoFinal);
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);
            reemplazos.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("centroInicial", centroInicial);
            reemplazos.put("centroFinal", centroFinal);
            reemplazos.put("mesAnterior", SysmanFunciones
                            .getParteFecha(fechaInicial, Calendar.MONTH));
            reemplazos.put("mes", SysmanFunciones.getParteFecha(fechaInicial,
                            Calendar.MONTH)

                + 1);

            reemplazos.put("filtrosCentro",
                            " AND DETALLE_COMPROBANTE_CNT.CENTRO_COSTO BETWEEN '"
                                + centroInicial + "' AND  '" + centroFinal
                                + "' ");
            reemplazos.put("es_id", "true".equals(agruparPorId) ? "1" : "0");
            if ("true".equals(filtroTercero))
            {
                if ("true".equals(porNit))
                {
                    reemplazos.put(filtroConst,
                                    " AND DETALLE_COMPROBANTE_CNT.TERCERO BETWEEN '"
                                        + terceroInicial + "' AND  '"
                                        + terceroFinal + "' ");
                    reemplazos.put(filtroTerConst,
                                    "WHERE TERCERO BETWEEN '" + terceroInicial
                                        + "' AND   '" + terceroFinal + "' ");
                }
                else
                {
                    reemplazos.put("filtrosTercero",
                                    " AND TERCERO_DET.NOMBRE BETWEEN '"
                                        + SysmanFunciones.nvl(nombreTerceroInicial, SysmanConstantes.DEFECTOINICIAL_STRING) + "' AND '"
                                        + SysmanFunciones.nvl(nombreTerceroFinal, SysmanConstantes.DEFECTOFINAL_STRING) + "' ");
                    reemplazos.put(filtroTerConst,
                                    "WHERE NOMBRETERCERO BETWEEN '"
                                        + SysmanFunciones.nvl(nombreTerceroInicial, SysmanConstantes.DEFECTOINICIAL_STRING) + "' AND '"
                                        + SysmanFunciones.nvl(nombreTerceroFinal, SysmanConstantes.DEFECTOFINAL_STRING) + "' ");
                }
            }
            else
            {
                reemplazos.put(filtroConst, "");
                reemplazos.put(filtroTerConst, "");
            }
            if (referenciaInicial.isEmpty() && referenciaFinal.isEmpty())
            {

                condicionReferencias = " ";
            }
            else
            {
                condicionReferencias = "AND DETALLE_COMPROBANTE_CNT.REFERENCIA BETWEEN '"
                    + referenciaInicial + "' AND '" + referenciaFinal + "' ";

            }
            reemplazos.put("condicionReferencias", condicionReferencias);

        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reemplazos;
    }

    public void cambiarfiltroTercero()
    {
    	 if("false".equals(filtroTercero)) {
         	columnaFuncionario =  "false";	
         }   
        
        if("true".equals(columnaFuncionario)) {
     	   saltoPagina ="false";
     	   filtroTercero =  "true";
     	   porNombre =  "false";
     	   agruparPorId =  "false";
     	   porNit =  "true";
     	   terceroVisible = true;
        }else {
        	terceroVisible = !terceroVisible;
        }
           
    }
    
    public void cambiarColumnaFuncionario()
    {
       if("true".equals(columnaFuncionario)) {
    	   saltoPagina ="false";
    	   filtroTercero =  "true";
    	   porNombre =  "false";
    	   agruparPorId =  "false";
    	   porNit =  "true";
    	   terceroVisible =  true;
       } 	   
    }
    
    public void cambiarsaltoPagina() {
    	if("true".equals(saltoPagina)) {
          	columnaFuncionario =  "false";	
         }  
    	 if("true".equals(columnaFuncionario)) {
      	   saltoPagina ="false";
      	   filtroTercero =  "true";
      	   porNombre =  "false";
      	   agruparPorId =  "false";
      	   porNit =  "true";
      	   terceroVisible =  true;
         }
    	 
     	
    }
    
    public void cambiaragruparPorId() {
    	if("true".equals(agruparPorId)) {
         	columnaFuncionario =  "false";	
         }  
    	if("true".equals(columnaFuncionario)) {
      	   saltoPagina ="false";
      	   filtroTercero =  "true";
      	   porNombre =  "false";
      	   agruparPorId =  "false";
      	   porNit =  "true";
      	   terceroVisible =  true;
         }
    	  
    	
    }
    

    public void cambiarPornit()
    {
    	if("false".equals(porNit)) {
         	columnaFuncionario =  "false";	
         }
        porNombre = "true".equals(porNit) ? falseConst : "true";
        
        if("true".equals(columnaFuncionario)) {
     	   saltoPagina ="false";
     	   filtroTercero =  "true";
     	   porNombre =  "false";
     	   agruparPorId =  "false";
     	   porNit =  "true";
     	   terceroVisible =  true;
        }
        
    }

    public void cambiarPorNombre()
    {
    	if("true".equals(porNombre)) {
         	columnaFuncionario =  "false";	
        }
        porNit = "true".equals(porNombre) ? falseConst : "true";
        if("true".equals(columnaFuncionario)) {
     	   saltoPagina ="false";
     	   filtroTercero =  "true";
     	   porNombre =  "false";
     	   agruparPorId =  "false";
     	   porNit =  "true";
     	  terceroVisible =  true;
        }
        
    }

    public void cambiarfechainicial()
    {
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cargarListaCuentaInicial();
        
        
        
    }

    public void seleccionarFilaTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux == null ? ""
            : registroAux.getCampos().get(codigoConst).toString();
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = (String) registroAux.getCampos().get(codigoConst);
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux == null ? ""
            : registroAux.getCampos().get(codigoConst).toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux == null ? ""
            : registroAux.getCampos().get(codigoConst).toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux == null ? ""
            : registroAux.getCampos().get("NIT").toString();
        nombreTerceroInicial = registroAux == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreTerceroFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux == null ? ""
            : registroAux.getCampos().get("NIT").toString();
        nombreTerceroFinal = registroAux == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();
    }

    public void seleccionarFilaCmbCentroCInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux == null ? ""
            : registroAux.getCampos().get(codigoConst).toString();
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCmbCentroCFinal();
    }

    public void seleccionarFilareferenciaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos().get("CODIGO").toString();
        cargarListaReferenciafinal();
    }

    public void seleccionarFilaReferenciafinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        setReferenciaFinal(registroAux.getCampos().get("CODIGO").toString());
    }

    public void seleccionarFilaCmbCentroCFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux == null ? ""
            : registroAux.getCampos().get(codigoConst).toString();
    }

    public String getAgruparPorId()
    {
        return agruparPorId;
    }

    public void setAgruparPorId(String agruparPorId)
    {
        this.agruparPorId = agruparPorId;
    }

    public String getPorNit()
    {
        return porNit;
    }

    public void setPorNit(String porNit)
    {
        this.porNit = porNit;
    }

    public String getPorNombre()
    {
        return porNombre;
    }

    public void setPorNombre(String porNombre)
    {
        this.porNombre = porNombre;
    }

    public String getSaltoPagina()
    {
        return saltoPagina;
    }

    public void setSaltoPagina(String saltoPagina)
    {
        this.saltoPagina = saltoPagina;
    }

    public String getFiltroTercero()
    {
        return filtroTercero;
    }

    public void setFiltroTercero(String filtroTercero)
    {
        this.filtroTercero = filtroTercero;
    }

    public String getConFirmas()
    {
        return conFirmas;
    }

    public void setConFirmas(String conFirmas)
    {
        this.conFirmas = conFirmas;
    }

    public String getTamano()
    {
        return tamano;
    }

    public void setTamano(String tamano)
    {
        this.tamano = tamano;
    }

    public String getTipoInicial()
    {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial)
    {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal()
    {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal)
    {
        this.tipoFinal = tipoFinal;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public String getTerceroInicial()
    {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial)
    {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal()
    {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal)
    {
        this.terceroFinal = terceroFinal;
    }

    public String getCentroInicial()
    {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial)
    {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal()
    {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal)
    {
        this.centroFinal = centroFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public int getAnio()
    {
        return anio;
    }

    public void setAnio(int anio)
    {
        this.anio = anio;
    }

    public String getCompania()
    {
        return compania;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreTerceroInicial()
    {
        return nombreTerceroInicial;
    }

    public void setNombreTerceroInicial(String nombreTerceroInicial)
    {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }

    public String getNombreTerceroFinal()
    {
        return nombreTerceroFinal;
    }

    public void setNombreTerceroFinal(String nombreTerceroFinal)
    {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTipoInicial()
    {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial()
    {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial)
    {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal()
    {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal)
    {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public RegistroDataModelImpl getListaCmbCentroCInicial()
    {
        return listaCmbCentroCInicial;
    }

    public void setListaCmbCentroCInicial(
        RegistroDataModelImpl listaCmbCentroCInicial)
    {
        this.listaCmbCentroCInicial = listaCmbCentroCInicial;
    }

    public RegistroDataModelImpl getListaCmbCentroCFinal()
    {
        return listaCmbCentroCFinal;
    }

    public void setListaCmbCentroCFinal(
        RegistroDataModelImpl listaCmbCentroCFinal)
    {
        this.listaCmbCentroCFinal = listaCmbCentroCFinal;
    }

    public boolean isTerceroVisible()
    {
        return terceroVisible;
    }

    public void setTerceroVisible(boolean terceroVisible)
    {
        this.terceroVisible = terceroVisible;
    }

    public boolean isTamanoVisible()
    {
        return tamanoVisible;
    }

    public void setTamanoVisible(boolean tamanoVisible)
    {
        this.tamanoVisible = tamanoVisible;
    }

    public String getModulo()
    {
        return modulo;
    }

    public RegistroDataModelImpl getListareferenciaInicial()
    {
        return listareferenciaInicial;
    }

    public void setListareferenciaInicial(
        RegistroDataModelImpl listareferenciaInicial)
    {
        this.listareferenciaInicial = listareferenciaInicial;
    }

    public RegistroDataModelImpl getListaReferenciafinal()
    {
        return listaReferenciafinal;
    }

    public void setListaReferenciafinal(
        RegistroDataModelImpl listaReferenciafinal)
    {
        this.listaReferenciafinal = listaReferenciafinal;
    }

    public String getReferenciaFinal()
    {
        return referenciaFinal;
    }

    public void setReferenciaFinal(String referenciaFinal)
    {
        this.referenciaFinal = referenciaFinal;
    }

    public String getReferenciaInicial()
    {
        return referenciaInicial;
    }

    public void setReferenciaInicial(String referenciaInicial)
    {
        this.referenciaInicial = referenciaInicial;
    }

    public String getCondicionReferencias()
    {
        return condicionReferencias;
    }

    public void setCondicionReferencias(String condicionReferencias)
    {
        this.condicionReferencias = condicionReferencias;
    }

    /**
     * @return the formatoEspecial
     */
    public boolean isFormatoEspecial()
    {
        return formatoEspecial;
    }

    /**
     * @param formatoEspecial
     * the formatoEspecial to set
     */
    public void setFormatoEspecial(boolean formatoEspecial)
    {
        this.formatoEspecial = formatoEspecial;
    }
    
    
    public String getColumnaFuncionario() {
		return columnaFuncionario;
	}

	public void setColumnaFuncionario(String columnaFuncionario) {
		this.columnaFuncionario = columnaFuncionario;
	}
	
	public boolean isFuncionarioVisible() {
		return funcionarioVisible;
	}

	public void setFuncionarioVisible(boolean funcionarioVisible) {
		this.funcionarioVisible = funcionarioVisible;
	}
	
    public boolean isFormatoSinCierre() {
        return formatoSinCierre;
    }

    public void setFormatoSinCierre(boolean formatoSinCierre) {
        this.formatoSinCierre = formatoSinCierre;
    }
    
    public boolean isManejaEsp()
    {
        return manejaEsp;
    }
    
    public void setManejaEsp(boolean manejaEsp)
    {
        this.manejaEsp = manejaEsp;
    }
    
    public boolean isEspecial()
    {
        return especial;
    }
    
    public void setEspecial(boolean especial)
    {
        this.especial = especial;
    }

}
