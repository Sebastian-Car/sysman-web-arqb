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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenAportesAutoControladorEnum;
import com.sysman.nomina.enums.ResumenAportesAutoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 08/10/2015
 *
 * @author eamaya
 * @version 2.0, 25/10/2017, Proceso de Refactoring DSS, cambio de
 * numero de formulario y correcciones SonarQube
 * 
 * @author obarragan
 * @version 3, 10/06/2019 - Se agrego opcion de imprimir header con
 * imagenes adicionales.
 *
 */
@ManagedBean
@ViewScoped

public class ResumenAportesAutoControlador extends BeanBaseModal {

    private final String mSeleccionarInforme;
    private final String parametroTitulo;
    private final String parametroNombreEmpresa;
    private final String parametroIdProceso;
    private final String parametroPeriodo;
    private final String parametroVisCentro;
    private final String parametroRangoMes;
    private String reporteResAutoPension;
    private String reporteResAutoSalud;
    private String nit;

    private String compania;
    private String modulo;
    private String ano;
    private String mes;
    private String periodo;
    private String idProceso;
    private String aportes;
    private String cmbSalud;
    private String cmbPension;
    private String cmbRiesgos;
    private String resumenes;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private List<Registro> listaProceso;
    private String formatoReportes;
    private boolean verAcumulado;
    private boolean ckAcumulado;
    private boolean ckCentroCosto;
    private String cmbParafiscales;
    private String headerEspecial;
    private String sticker;

    @EJB
    // EjbSysmanUtil ejbSysmanUtl;
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ResumenAportesAutoControlador
     */
    public ResumenAportesAutoControlador() {
        super();
        mSeleccionarInforme = "TB_TB2647";
        parametroTitulo = "PR_TITULO";
        parametroNombreEmpresa = "PR_NOMBREEMPRESA";
        parametroIdProceso = "idProceso";
        parametroPeriodo = "periodo";
        parametroVisCentro = "PR_VISCENTRO";
        parametroRangoMes = "rangoMes";
        reporteResAutoPension = "001922ResumenAutoPension"; // FORMATO
                                                            // APORTES
                                                            // AUTO
                                                            // PENSION
        reporteResAutoSalud = "000274ResumenAutoSalud"; // FORMATO
                                                        // APORTES
                                                        // AUTO SALUD
        
        
        
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_APORTES_AUTO_CONTROLADOR
                            .getCodigo();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            nit = SessionUtil.getCompaniaIngreso().getNit();
            idProceso = (String) SessionUtil.getSessionVar("procesoNomina");
            ano = (String) SessionUtil.getSessionVar("anioNomina");
            mes = (String) SessionUtil.getSessionVar("mesNomina");
            periodo = (String) SessionUtil.getSessionVar("periodoNomina");
            verAcumulado = true;
            formatoReportes = nit;

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenAportesAutoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListaProceso();
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();

        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        aportes = "1";
        cmbSalud = "1";

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenAportesAutoControladorUrlEnum.URL5555
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        idProceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenAportesAutoControladorUrlEnum.URL4631
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(ResumenAportesAutoControladorEnum.ID_PROCESO.getValue(),
                        idProceso);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenAportesAutoControladorUrlEnum.URL5072
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(ResumenAportesAutoControladorEnum.PROCESO.getValue(),
                        idProceso);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenAportesAutoControladorUrlEnum.URL6666
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void generarReportesSalud(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(cmbSalud)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mSeleccionarInforme));
            return;
        }

        String nombreReporte = null;
        String nombreConsulta = null;
        String reporteResdtoSaludTotal = null;

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String titulo = SysmanFunciones
                            .concatenar(service.buscarEnLista(mes, "MES",
                                            GeneralParameterEnum.NOMBRE
                                                            .getName(),
                                            listaMes), " de ", ano);
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            
            reporteResdtoSaludTotal = SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                                    "FORMATO RESUMEN APORTES AUTOLIQUIDACION SALUD",
                                    modulo,
                                    new Date(), false),
                    "000271ResumenDtoSaludTotal");
            
            if (cmbSalud.equals("1")) {
            	if (ckCentroCosto == true) {
            		nombreReporte = "002455ResumDtoSaludTotalCenCos";
                    nombreConsulta = "002455ResumDtoSaludTotalCenCos";
                    
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);

                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    
                    if (ckAcumulado == true) {
                    	reemplazar.put("filtro", "");
                    } else {
                    	reemplazar.put("filtro", "AND V_ACUMULADOS.PERIODO="+periodo+"");
                    }
            	} else {
	                if (cmbSalud.equals("1") && ckAcumulado == true) {
	                    nombreReporte = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "RESUMEN APORTES AUTOLIQUIDACION SALUD IDI",
	                                                    modulo,
	                                                    new Date(), false),
	                                    reporteResdtoSaludTotal);
	                    nombreConsulta = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "RESUMEN APORTES AUTOLIQUIDACION SALUD IDI",
	                                                    modulo,
	                                                    new Date(), false),
	                                    "001914ResumenDtoSaludTotal");
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	
	                }
	                else {
	                    // MANEJO DE PARAMETROS DEL REPORTE
	                    nombreReporte = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "RESUMEN APORTES AUTOLIQUIDACION SALUD IDI",
	                                                    modulo,
	                                                    new Date(), false),
	                                    reporteResdtoSaludTotal);
	                    nombreConsulta = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "RESUMEN APORTES AUTOLIQUIDACION SALUD IDI",
	                                                    modulo,
	                                                    new Date(), false),
	                                    reporteResdtoSaludTotal);
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    reemplazar.put(parametroPeriodo, periodo);
	                }
            	}
            }
            else if (cmbSalud.equals("2")) {
                if (cmbSalud.equals("2") && ckAcumulado == true) {
                    nombreReporte = "000272ResDtoSaludTotalGrCtble";
                    nombreConsulta = "001915ResDtoSaludTotalGrCtble";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);

                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE

                    nombreReporte = "000272ResDtoSaludTotalGrCtble";
                    nombreConsulta = "000272ResDtoSaludTotalGrCtble";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbSalud.equals("3")) {
                if ("3".equals(cmbSalud) && ckAcumulado == true) {
                    nombreReporte = "000273ResDtoSalTotalGrCtbleCCto";
                    nombreConsulta = "001916ResDtoSalTotalGrCtbleCCto";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    // cargarParametros(parametros);

                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);

                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000273ResDtoSalTotalGrCtbleCCto";
                    nombreConsulta = "000273ResDtoSalTotalGrCtbleCCto";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    // cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbSalud.equals("4")) {
                reporteResAutoSalud = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FORMATO APORTES AUTO SALUD",
                                                modulo, new Date(), false),
                                "000274ResumenAutoSalud");
                if ("4".equals(cmbSalud) && ckAcumulado == true) {
                    nombreReporte = reporteResAutoSalud;
                    nombreConsulta = "001917ResumenAutoSalud";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    parametros.put(parametroVisCentro, 1);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put(parametroRangoMes, mes);
                    reemplazar.put(parametroPeriodo, periodo);
                    reemplazar.put(compania, compania);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = reporteResAutoSalud;
                    nombreConsulta = reporteResAutoSalud;
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    parametros.put(parametroVisCentro, 1);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);
                    reemplazar.put(compania, compania);
                }
            }
            else if (cmbSalud.equals("5")) {
                if ("5".equals(cmbSalud) && ckAcumulado == true) {
                    nombreReporte = "000275ResumenDtoSaludTotalEMP";
                    nombreConsulta = "001918ResumenDtoSaludTotalEMP";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);

                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000275ResumenDtoSaludTotalEMP";
                    nombreConsulta = "000275ResumenDtoSaludTotalEMP";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbSalud.equals("6")) {
                if ("6".equals(cmbSalud) && ckAcumulado == true) {
                    nombreReporte = "000276ResumenDtoSalud";
                    nombreConsulta = "001929ResumenDtoSalud";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000276ResumenDtoSalud";
                    nombreConsulta = "000276ResumenDtoSalud";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            cargarParametros(parametros);
            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que cargar los parametros a visulizar en los informes
     *
     * @param param
     * @throws SystemException
     */
    public void cargarParametros(Map<String, Object> param)
                    throws SystemException {
        param.put("PR_NOMBRE DEL GERENTE",
                        retornarValorParametro("NOMBRE DEL GERENTE"));
        param.put("PR_NOMBRE_DEL_GERENTE",
                        retornarValorParametro("NOMBRE DEL GERENTE"));
        param.put("PR_CARGO_DEL_GERENTE",
                        retornarValorParametro("CARGO DEL GERENTE"));
        param.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                        retornarValorParametro(
                                        "NOMBRE DEL CARGO TESORERO PAGADOR"));
        param.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                        retornarValorParametro(
                                        "CARGO DEL TESORERO PAGADOR"));
        param.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                        retornarValorParametro(
                                        "CARGO DE QUIEN AUTORIZA NOMINA"));
        param.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                        retornarValorParametro(
                                        "NOMBRE DE QUIEN AUTORIZA NOMINA"));

        param.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                        retornarValorParametro(
                                        "NOMBRE DE QUIEN REVISA NOMINA"));
        param.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                        retornarValorParametro(
                                        "CARGO DE QUIEN REVISA NOMINA"));

        param.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                        retornarValorParametro(
                                        "NOMBRE JEFE RECURSOS HUMANOS"));
        param.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                        retornarValorParametro(
                                        "CARGO JEFE RECURSOS HUMANOS"));

        param.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", retornarValorParametro(
                        "NOMBRE JEFE DESARROLLO HUMANO"));
        param.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", retornarValorParametro(
                        "CARGO JEFE DESARROLLO HUMANO"));
        param.put("PR_NOMBRE_JEFE_NOMINA", retornarValorParametro(
                        "NOMBRE JEFE NOMINA"));
        param.put("PR_CARGO_RESPONSABLE_DE_NOMINA", retornarValorParametro(
                        "CARGO RESPONSABLE DE NOMINA"));

        param.put("PR_MOSTAR",
                        "900.334.265-3".equals(formatoReportes) ? false
                            : true);
        param.put("PR_MOSTRAR",
                        "900.334.265-3".equals(formatoReportes) ? false
                            : true);

        param.put("PR_HEADER_ESPECIAL",
                        headerEspecial.equals("SI") ? true : false);

        param.put("PR_IMAGEN_ESPECIAL", sticker);

        param.put("PR_NOMBRE_DE_QUIEN_ELABORA_RESUMEN_SEGURIDAD_SOCIAL",
                        retornarValorParametro(
                                        "NOMBRE DE QUIEN ELABORA RESUMEN SEGURIDAD SOCIAL"));
        param.put("PR_CARGO_DE_QUIEN_ELABORA_RESUMEN_SEGURIDAD_SOCIAL",
                        retornarValorParametro(
                                        "CARGO DE QUIEN ELABORA RESUMEN SEGURIDAD SOCIAL"));
        param.put("PR_CARGO_DEL_JEFE_DE_NOMINA",
                retornarValorParametro(
                                "CARGO DEL JEFE DE NOMINA"));
        // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
        param.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
        // FIN IMPLEMENTACION MARCA_BLANCA
        
        //7750292 - ljdiaz - cuarta firma
        String mostrarCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "ACTIVAR CUARTA FIRMA", modulo,
					new Date(), false), "NO");
		if("SI".equals(mostrarCuartaFirma)) {
			String nombreCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE CUARTA FIRMA", modulo,
					new Date(), false), "NO");
			String cargoCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "CARGO CUARTA FIRMA", modulo,
					new Date(), false), "NO");
			
			param.put("PR_MOSTRAR_CUARTA_FIRMA", mostrarCuartaFirma);
			param.put("PR_NOMBRE_CUARTA_FIRMA", nombreCuartaFirma);
			param.put("PR_CARGO_CUARTA_FIRMA", cargoCuartaFirma);
		}
    }

    public void generarReportesPension(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(cmbPension)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mSeleccionarInforme));
            return;
        }
        String nombreReporte = null;
        String nombreConsulta = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String titulo = SysmanFunciones
                            .concatenar(service.buscarEnLista(mes, "MES",
                                            GeneralParameterEnum.NOMBRE
                                                            .getName(),
                                            listaMes), " de ", ano);
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            // El parametro FORMATO APORTES AUTO PENSION TOTAL se creo
            // con el fin de configurar el informe
            // de entidad de Gobernacion de Nariño
            // 002222ResumenAutoPensiontotalGoberN
            if (cmbPension.equals("1")) {
            	if (ckCentroCosto == true) {
            		if ((SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,"FORMATO APORTES AUTO PENSION TOTAL",modulo,
            				new Date(), false),"002456ResumAutoPensionTotCenCos")).equals("002222ResumenAutoPensiontotalGoberN")) {
            			nombreReporte = "002458ResumAutoPensionTotCenCosGoberN";
                        nombreConsulta = "002458ResumAutoPensionTotCenCosGoberN";
            		} else {
	            		nombreReporte = "002456ResumAutoPensionTotCenCos";
	                    nombreConsulta = "002456ResumAutoPensionTotCenCos";
            		}
            		
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    
                    if (ckAcumulado == true) {
                    	reemplazar.put("filtro", "");
                    } else {
                    	reemplazar.put("filtro", "AND V_ACUMULADOS.PERIODO="+periodo+"");
                    }
            	} else {
	                if ("1".equals(cmbPension) && ckAcumulado == true) {
	                    nombreReporte = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "FORMATO APORTES AUTO PENSION TOTAL",
	                                                    modulo,
	                                                    new Date(), false),
	                                    "000278ResumenAutoPensiontotal");
	                    nombreConsulta = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "FORMATO APORTES AUTO PENSION TOTAL",
	                                                    modulo,
	                                                    new Date(), false),
	                                    "001919ResumenAutoPensiontotal");
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	                    // cargarParametros(parametros);
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    reemplazar.put("filtro", ""); //JM CC 4272
	                }
	                else {
	                    // MANEJO DE PARAMETROS DEL REPORTE
	                    nombreReporte = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "FORMATO APORTES AUTO PENSION TOTAL",
	                                                    modulo,
	                                                    new Date(), false),
	                                    "000278ResumenAutoPensiontotal");
	                    nombreConsulta = SysmanFunciones.nvlStr(
	                                    ejbSysmanUtil.consultarParametro(compania,
	                                                    "FORMATO APORTES AUTO PENSION TOTAL",
	                                                    modulo,
	                                                    new Date(), false),
	                                    "001919ResumenAutoPensiontotal");
	                    // nombreReporte = "reporteResAutoPensionTotal";
	                    // nombreConsulta = "reporteResAutoPensionTotal";
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	                    // cargarParametros(parametros);
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    reemplazar.put(parametroPeriodo, periodo);
	                    reemplazar.put("filtro", "AND V_ACUMULADOS.PERIODO="+periodo+""); // JM CC 4272
	                }
            	}
            }
            else if (cmbPension.equals("2")) {
                if ("2".equals(cmbPension) && ckAcumulado == true) {
                    nombreReporte = "000281ResAutoPensionTotGrContable";
                    nombreConsulta = "001920ResAutoPensionTotGrContable";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000281ResAutoPensionTotGrContable";
                    nombreConsulta = "000281ResAutoPensionTotGrContable";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbPension.equals("3")) {
                if ("3".equals(cmbPension) && ckAcumulado == true) {
                    nombreReporte = "000283ResAutoPensionTotGrCtbleCC";
                    nombreConsulta = "001921ResAutoPensionTotGrCtbleCC";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    // cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000283ResAutoPensionTotGrCtbleCC";
                    nombreConsulta = "000283ResAutoPensionTotGrCtbleCC";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    // cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbPension.equals("4")) {
                reporteResAutoPension = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "FORMATO APORTES AUTO PENSION",
                                                modulo, new Date(), false),
                                "000284RESUMENAUTOPENSIONIDI");
                if ("4".equals(cmbPension) && ckAcumulado == true) {
                    nombreReporte = reporteResAutoPension;
                    nombreConsulta = "001922ResumenAutoPension";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    parametros.put(parametroVisCentro, 1);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put(parametroRangoMes, mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = reporteResAutoPension;
                    nombreConsulta = reporteResAutoPension;
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    parametros.put(parametroVisCentro, 1);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put(parametroRangoMes, mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbPension.equals("5")) {
                if ("5".equals(cmbPension) && ckAcumulado == true) {
                    nombreReporte = "000286ResumenDtoPension";
                    nombreConsulta = "001923ResumenDtoPension";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000286ResumenDtoPension";
                    nombreConsulta = "000286ResumenDtoPension";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            cargarParametros(parametros);
            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarReportesRiesgo(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(cmbRiesgos)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mSeleccionarInforme));
            return;
        }

        String nombreReporte = null;
        String nombreConsulta = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String titulo = SysmanFunciones.concatenar(service.buscarEnLista(
                            mes, "MES", GeneralParameterEnum.NOMBRE.getName(),
                            listaMes), " de ", ano);
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            if (cmbRiesgos.equals("1")) {
            	if (ckCentroCosto == true) {
            		nombreReporte = "002457PlanillaResumAutoRiesCenCos";
                    nombreConsulta = "002457PlanillaResumAutoRiesCenCos";
                    
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    
                    cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    
                    if (ckAcumulado == true) {
                    	reemplazar.put("filtro", "");
                    } else {
                    	reemplazar.put("filtro", "AND V_ACUMULADOS.PERIODO="+periodo+"");
                    }
            	} else {
	                if ("1".equals(cmbRiesgos) && ckAcumulado == true) {
	                    nombreReporte = "001701PlanillaresumenAutoRies";
	                    nombreConsulta = "001924PlanillaresumenAutoRies";
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	                    cargarParametros(parametros);
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                }
	                else {
	                    // MANEJO DE PARAMETROS DEL REPORTE
	                    nombreReporte = "001701PlanillaresumenAutoRies";
	                    nombreConsulta = "001701PlanillaresumenAutoRies";
	                    parametros.put(parametroTitulo, titulo);
	                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
	                    cargarParametros(parametros);
	                    reemplazar.put(parametroIdProceso, idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    reemplazar.put(parametroPeriodo, periodo);
	                }
            	}
            }
            else if (cmbRiesgos.equals("2")) {
                if ("2".equals(cmbRiesgos) && ckAcumulado == true) {
                    nombreReporte = "000301PlanillaResAutoRiesgoRubroyCentroC";
                    nombreConsulta = "001925PlanillaResAutoRiesgoRubroyCentroC";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000301PlanillaResAutoRiesgoRubroyCentroC";
                    nombreConsulta = "000301PlanillaResAutoRiesgoRubroyCentroC";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    cargarParametros(parametros);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }
            else if (cmbRiesgos.equals("3")) {
                if ("3".equals(cmbRiesgos) && ckAcumulado == true) {
                    nombreReporte = "000303PlanillaresumenAutoRiesgoGrupoContable";
                    nombreConsulta = "001926PlanillaresumenAutoRiesgoGrupoContable";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000303PlanillaresumenAutoRiesgoGrupoContable";
                    nombreConsulta = "000303PlanillaresumenAutoRiesgoGrupoContable";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                // FIN IMPLEMENTACION MARCA_BLANCA
            }
            else if (cmbRiesgos.equals("4")) {
                if ("4".equals(cmbRiesgos) && ckAcumulado == true) {
                    nombreReporte = "000310PlanillaResumenAutoRiesgoCentroCosto";
                    nombreConsulta = "001927PlanillaResumenAutoRiesgoCentroCosto";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);

                }
                else {
                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000310PlanillaResumenAutoRiesgoCentroCosto";
                    nombreConsulta = "000310PlanillaResumenAutoRiesgoCentroCosto";
                    parametros.put(parametroTitulo, titulo);
                    parametros.put(parametroNombreEmpresa, nombreEmpresa);
                    reemplazar.put(parametroIdProceso, idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put(parametroPeriodo, periodo);

                }
            }else if (cmbRiesgos.equals("5")) {
            	
            	 nombreReporte = "000291PlanillaResumenAutoRiesgo";
                 nombreConsulta = "000291PlanillaResumenAutoRiesgo";
                 parametros.put(parametroTitulo, titulo);
                 parametros.put(parametroNombreEmpresa, nombreEmpresa);
                 reemplazar.put(parametroIdProceso, idProceso);
                 reemplazar.put("ano", ano);
                 reemplazar.put("mes", mes);
                 reemplazar.put(parametroPeriodo, periodo);
            }

            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    public void generarReportesResumenes(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(resumenes)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mSeleccionarInforme));
            return;
        }
        String nombreReporte = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String titulo = "RETROACTIVO 05";
            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();

            if ("1".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = reporteResAutoSalud;
                parametros.put(parametroTitulo, titulo);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                parametros.put(parametroVisCentro, 1);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);
                reemplazar.put(parametroRangoMes, " BETWEEN 1 AND  " + mes);
                reemplazar.put(parametroPeriodo, "5");

            }
            if ("2".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = reporteResAutoSalud;
                parametros.put(parametroTitulo, titulo + " - AGRUPADO");
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                parametros.put(parametroVisCentro, 0);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);
                reemplazar.put(parametroRangoMes, " BETWEEN 1 AND  " + mes);
                reemplazar.put(parametroPeriodo, "5");

            }
            if ("3".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = "000316ResumenDtoSaludTotalANUAL";
                parametros.put(parametroTitulo, ano);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);

            }
            if ("4".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = reporteResAutoPension;
                parametros.put(parametroTitulo, titulo);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                parametros.put(parametroVisCentro, 1);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);
                reemplazar.put(parametroRangoMes, "BETWEEN 1 AND  " + mes);
                reemplazar.put(parametroPeriodo, "5");

            }
            if ("5".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = reporteResAutoPension;
                parametros.put(parametroTitulo, titulo);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                parametros.put(parametroVisCentro, 0);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);
                reemplazar.put(parametroRangoMes, "BETWEEN 1 AND  " + mes);
                reemplazar.put(parametroPeriodo, "5");

            }
            if ("6".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = "000330ResumenAutoPensiontotalAnual";
                parametros.put(parametroTitulo, ano);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);

            }
            if ("7".equals(resumenes)) {
                // MANEJO DE PARAMETROS DEL REPORTE
                nombreReporte = "000315PlanillaResumenAutoRiesgoAnual";
                parametros.put(parametroTitulo, ano);
                parametros.put(parametroNombreEmpresa, nombreEmpresa);
                reemplazar.put(parametroIdProceso, idProceso);
                reemplazar.put("ano", ano);

            }
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarReportesParafiscales(ReportesBean.FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(cmbParafiscales)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(mSeleccionarInforme));
            return;
        }
        String nombreReporte = null;
        String nombreConsulta = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            
                        
            if (cmbParafiscales.equals("1")) {
            	if (ckCentroCosto == true) {
            		nombreReporte = "002459ListadoParafiscalesMensualCenCos";
                    nombreConsulta = "002459ListadoParafiscalesMensualCenCos";
                    
                    reemplazar.put("idProceso", idProceso);
                    reemplazar.put("ano", ano);
                    reemplazar.put("mes", mes);
                    
                    if (ckAcumulado == true) {
                    	reemplazar.put("filtro", "");
                    } else {
                    	reemplazar.put("filtro", "AND HIS.PERIODO="+periodo+"");
                    }
                    
                    cargarParametros(parametros);
                    
                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_ANO1", ano);
                    parametros.put("PR_NOMBREEMPRESA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getNombre());
                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_MES1",
                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                    .parseInt(mes)]);
                    String usuario = SysmanFunciones.concatenar(
                                    SessionUtil.getUser().getNombre1(), " ",
                                    SessionUtil.getUser().getApellido1(), " ",
                                    SessionUtil.getUser().getApellido2());
                    
                    parametros.put("PR_GETUSER", usuario);
            	}
            	else {
	                if ("1".equals(cmbParafiscales) && ckAcumulado == true) {
	                    nombreReporte = "001702ListadoParafiscalesMensual";
	                    nombreConsulta = "001932ListadoParafiscalesMensual";
	                    reemplazar.put("idProceso", idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    cargarParametros(parametros);
	                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_ANO1", ano);
	                    parametros.put("PR_NOMBREEMPRESA",
	                                    SessionUtil.getCompaniaIngreso()
	                                                    .getNombre());
	                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_MES1",
	                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
	                                                    .parseInt(mes)]);
	                    String usuario = SysmanFunciones.concatenar(
	                                    SessionUtil.getUser().getNombre1(), " ",
	                                    SessionUtil.getUser().getApellido1(), " ",
	                                    SessionUtil.getUser().getApellido2());
	                    parametros.put("PR_GETUSER", usuario);
	                }
	                else {
	                    // MANEJO DE PARAMETROS DEL REPORTE
	                    nombreReporte = "001702ListadoParafiscalesMensual";
	                    nombreConsulta = "001702ListadoParafiscalesMensual";
	                    reemplazar.put("idProceso", idProceso);
	                    reemplazar.put("ano", ano);
	                    reemplazar.put("mes", mes);
	                    cargarParametros(parametros);
	                    reemplazar.put("idPeriodo", periodo);
	                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_ANO1", ano);
	                    parametros.put("PR_NOMBREEMPRESA",
	                                    SessionUtil.getCompaniaIngreso()
	                                                    .getNombre());
	                    parametros.put("PR_FORMS_INFORME_COMPENSACIONF_MES1",
	                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
	                                                    .parseInt(mes)]);
	                    String usuario = SysmanFunciones.concatenar(
	                                    SessionUtil.getUser().getNombre1(), " ",
	                                    SessionUtil.getUser().getApellido1(), " ",
	                                    SessionUtil.getUser().getApellido2());
	                    parametros.put("PR_GETUSER", usuario);
	                }
            	}
            }
            else if (cmbParafiscales.equals("2")) {
                if ("2".equals(cmbParafiscales) && ckAcumulado == true) {
                    nombreReporte = "000452ListadoParafiscalesCentrosdeCostoSTR";
                    nombreConsulta = "001931ListadoParafiscalesCentrosdeCostoSTR";
                    reemplazar.put("proceso", idProceso);
                    reemplazar.put("anio", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put("periodo", periodo);
                    cargarParametros(parametros);
                    parametros.put("PR_NOMBREEMPRESA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getNombre());
                    parametros.put("PR_NOMBREMES",
                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                    .parseInt(mes)]);
                    parametros.put("PR_ANIO", ano);
                    parametros.put("PR_GETUSER",
                                    SessionUtil.getUser().getNombre1());

                }
                else {

                    // MANEJO DE PARAMETROS DEL REPORTE
                    nombreReporte = "000452ListadoParafiscalesCentrosdeCostoSTR";
                    nombreConsulta = "000452ListadoParafiscalesCentrosdeCostoSTR";
                    reemplazar.put("proceso", idProceso);
                    reemplazar.put("anio", ano);
                    reemplazar.put("mes", mes);
                    reemplazar.put("periodo", periodo);
                    cargarParametros(parametros);
                    parametros.put("PR_NOMBREEMPRESA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getNombre());
                    parametros.put("PR_NOMBREMES",
                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                    .parseInt(mes)]);
                    parametros.put("PR_ANIO", ano);
                    parametros.put("PR_GETUSER",
                                    SessionUtil.getUser().getNombre1());
                    parametros.put("PR_HEADER_ESPECIAL",
                                    headerEspecial.equals("SI") ? true : false);
                    parametros.put("PR_IMAGEN_ESPECIAL", sticker);

                }
            }
            else if ("3".equals(cmbParafiscales)) {
                nombreReporte = "002910ListadoParafiscalesGrupoContable";
                nombreConsulta = "002910ListadoParafiscalesGrupoContable";
                reemplazar.put("proceso", idProceso);
                reemplazar.put("anio", ano);
                reemplazar.put("mes", mes);
                if (ckAcumulado == true) {
                	reemplazar.put("filtro", "");
                } else {
                	reemplazar.put("filtro", "AND HISTORICOS.PERIODO="+periodo+"");
                }
                cargarParametros(parametros);
                parametros.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso()
                                                .getNombre());
                parametros.put("PR_NOMBREMES",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mes)]);
                parametros.put("PR_ANIO", ano);
                parametros.put("PR_GETUSER",
                                SessionUtil.getUser().getNombre1());            
            }  
            else if ("4".equals(cmbParafiscales)) {
                nombreReporte = "002947ListadoParafiscalesGrupoContableyCC";
                nombreConsulta = "002947ListadoParafiscalesGrupoContableyCC";
                reemplazar.put("proceso", idProceso);
                reemplazar.put("anio", ano);
                reemplazar.put("mes", mes);
                if (ckAcumulado == true) {
                	reemplazar.put("filtro", "");
                } else {
                	reemplazar.put("filtro", "AND HISTORICOS.PERIODO="+periodo+"");
                }
                cargarParametros(parametros);
                parametros.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso()
                                                .getNombre());
                parametros.put("PR_NOMBREMES",
                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mes)]);
                parametros.put("PR_ANIO", ano);
                parametros.put("PR_GETUSER",
                                SessionUtil.getUser().getNombre1());            
            }

            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void invocarReportes(FORMATOS formato) {
        if (validarVacios() || validarVaciosDos()) {
            return;
        }

        if ("1".equals(aportes)) {
            generarReportesSalud(formato);
        }

        if ("2".equals(aportes)) {
            generarReportesPension(formato);
        }

        if ("3".equals(aportes)) {
            generarReportesRiesgo(formato);
        }
        if ("4".equals(aportes)) {
            generarReportesParafiscales(formato);
        }
        if ("5".equals(aportes)) {
            generarReportesResumenes(formato);
        }

    }

    public boolean validarVacios() {
        if ((idProceso == null) || ("".equals(idProceso))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2548"));
            return true;
        }
        if ((ano == null) || ("".equals(ano))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB250"));
            return true;
        }

        return false;
    }

    public boolean validarVaciosDos() {
        if ((mes == null) || ("".equals(mes))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB730"));
            return true;
        }

        if ((periodo == null) || ("".equals(periodo))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB670"));
            return true;
        }
        return false;
    }

    public void oprimirPreliminar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        invocarReportes(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdExcel() {
        archivoDescarga = null;
        // <CODIGO_DESARROLLADO>
        invocarReportes(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano = null;
        mes = null;
        periodo = null;
        cargarListaAno();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAportes() {
        // </CODIGO_DESARROLLADO>
        if ((aportes == null) || (aportes == "")) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2649"));
            aportes = "1";
            cmbSalud = "1";
        }
        cmbPension = "1";
        cmbRiesgos = "1";
        resumenes = "1";
        cmbParafiscales = "1";
        verAcumulado = true;
        if ("5".equals(aportes)) {
            verAcumulado = false;
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2650"));
        }
        // </CODIGO_DESARROLLADO>

    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getAportes() {
        return aportes;
    }

    public void setAportes(String aportes) {
        this.aportes = aportes;
    }

    public String getCmbSalud() {
        return cmbSalud;
    }

    public void setCmbSalud(String cmbSalud) {
        this.cmbSalud = cmbSalud;
    }

    public String getCmbPension() {
        return cmbPension;
    }

    public void setCmbPension(String cmbPension) {
        this.cmbPension = cmbPension;
    }

    public String getCmbRiesgos() {
        return cmbRiesgos;
    }

    public void setCmbRiesgos(String cmbRiesgos) {
        this.cmbRiesgos = cmbRiesgos;
    }

    public String getResumenes() {
        return resumenes;
    }

    public void setResumenes(String resumenes) {
        this.resumenes = resumenes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    private String retornarValorParametro(String parametro) {
        String rta = "";
        try {
            rta = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            parametro,
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "");
            headerEspecial = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATOS ESPECIALES BUCARAMANGA", modulo,
                            new Date(),
                            true);

            sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;

    }

    /**
     * @return the formatoReportes
     */
    public String getFormatoReportes() {
        return formatoReportes;
    }

    /**
     * @param formatoReportes
     * the formatoReportes to set
     */
    public void setFormatoReportes(String formatoReportes) {
        this.formatoReportes = formatoReportes;
    }

    /**
     * @return the verAcumulado
     */
    public boolean isVerAcumulado() {
        return verAcumulado;
    }

    /**
     * @param verAcumulado
     * the verAcumulado to set
     */
    public void setVerAcumulado(boolean verAcumulado) {
        this.verAcumulado = verAcumulado;
    }

    /**
     * @return the ckAcumulado
     */
    public boolean isCkAcumulado() {
        return ckAcumulado;
    }

    /**
     * @param ckAcumulado
     * the ckAcumulado to set
     */
    public void setCkAcumulado(boolean ckAcumulado) {
        this.ckAcumulado = ckAcumulado;
    }
    
    /**
     * @return the ckCentroCosto
     */
    public boolean isCkCentroCosto() {
        return ckCentroCosto;
    }

    /**
     * @param ckCentroCosto
     * the ckCentroCosto to set
     */
    public void setCkCentroCosto(boolean ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    /**
     * @return the cmbParafiscales
     */
    public String getCmbParafiscales() {
        return cmbParafiscales;
    }

    /**
     * @param cmbParafiscales
     * the cmbParafiscales to set
     */
    public void setCmbParafiscales(String cmbParafiscales) {
        this.cmbParafiscales = cmbParafiscales;
    }

    public String getHeaderEspecial() {
        return headerEspecial;
    }

    public void setHeaderEspecial(String headerEspecial) {
        this.headerEspecial = headerEspecial;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
