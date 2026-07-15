package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.FrminformesProyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrminformesProyectosControladorUrlEnum;
import com.sysman.bancoproyectos.reportes.BancoProyectosReportes;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 06/10/2015
 * 
 * @version 2, 18/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @version 2.1, 04/10/2017
 * @author jreina se modifico la generacion del reporte 000379RptFormatoCDC-15.
 * 
 */

@ManagedBean
@ViewScoped
public class FrminformesProyectosControlador extends BeanBaseModal {

    private final String compania;
    private final String consPlanDesarrollo;
    private String proyectoInicial;
    private String proyectoFin;
    private String vigenciaInicial;
    private String informe;
    private String dependencia;
    private String estadoActual;
    private String enviado;
    private String barrioUbicacion;
    private String numSolicitud;
    private String vigenciaFin;
    private String nombreInforme;
    private String codDependencia;
    private boolean cuadroVisible;
    private boolean preparaDatos;
    private final String ciudad;
    private final String departamento;
    private final String pais;
    private final int modulo;
    private final String consLetra;
    private ContenedorArchivo contArchivoSelector;
    private List<Registro> listaVIGENCIAINICIAL;
    private RegistroDataModelImpl listaDependencia;
    private List<Registro> listaBarrioUbicacion;
    private List<Registro> listaVIGENCIAFINAL;
    private List<Registro> listaInforme;
    private RegistroDataModelImpl listaProyectoinicial;
    private RegistroDataModelImpl listaProyectofinal;
    private RegistroDataModelImpl listaNumSolicitud;
    private StreamedContent archivoDescarga;
    private static final String TB_TB2474 = "TB_TB2474";
    private static final String TB_TB2475 = "TB_TB2475";
    private static final String TB_TB2478 = "TB_TB2478";
    private static final String TB_TB809 = "TB_TB809";
    private static final String TB_TB810 = "TB_TB810";
    private static final String TB_TB2477 = "TB_TB2477";
    private static final String CODIGO = "CODIGO";
    private static final String NOMBRE = "NOMBRE";
    private static final String VIGENCIA = "vigencia";
    private static final String PROYECTOINICIALS = "proyectoInicial";
    private static final String PROYECTOFINS = "proyectoFin";
    private static final String ESTADOACTUALS = "estadoActual";
    private static final String TODAS = "TODAS";
    private static final String DEPENDENCIAS = "dependencia";
    private static final String PR_STRSQL = "PR_STRSQL";
    private static final String PR_INFORME = "PR_INFORME";
    private static final String PR_GETUSER = "PR_GETUSER";
    private static final String DIGITOS = "DIGITOS";
    private static final String RUBROPRESUPUESTAL = "RUBROPRESUPUESTAL";
    private static final String PR_VIGENCIA = "PR_VIGENCIA";
    private static final String ENVIADOS = "enviado";
    private static final String VIGENCIA_META = "VIGENCIA_META";
    private static final String VIGENCIAINICIALS = "vigenciaInicial";
    private static final String PR_VIGENCIAINICIAL = "PR_VIGENCIAINICIAL";
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbBancoProyectoTresRemote ejbBancoProyectoTres;

    /**
     * Creates a new instance of FrminformesProyectosControlador
     */
    public FrminformesProyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consPlanDesarrollo="PLAN DE DESARROLLO";
        ciudad = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
        contArchivoSelector = new ContenedorArchivo();
        departamento = SessionUtil.getCompaniaIngreso().getCodigoDepartamento();
        pais = SessionUtil.getCompaniaIngreso().getCodigoPais();
        modulo = Integer.parseInt(SessionUtil.getModulo());
        consLetra="Arial";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMINFORMES_PROYECTOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaVIGENCIAINICIAL();
        cargarListaBarrioUbicacion();
        cargarListaVIGENCIAFINAL();
        cargarListaProyectoinicial();
        cargarListaProyectofinal();
        cargarListaNumSolicitud();
        cargarListaDependencia();
        cargarListaInforme();
        abrirFormulario();
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesProyectosControladorUrlEnum.URL6553
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaVIGENCIAINICIAL() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaVIGENCIAINICIAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL7040
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaBarrioUbicacion() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(FrminformesProyectosControladorEnum.PARAM1.getValue(),pais);
            param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),departamento);
            param.put(GeneralParameterEnum.CIUDAD.getName(),ciudad);
            
            listaBarrioUbicacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL7711
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaVIGENCIAFINAL() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            if(!"TODAS".equals(vigenciaInicial)){
                param.put(GeneralParameterEnum.NUMERO.getName(),vigenciaInicial);                 
            }
            listaVIGENCIAFINAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL8597
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProyectoinicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesProyectosControladorUrlEnum.URL9541
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaProyectofinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesProyectosControladorUrlEnum.URL10118
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), proyectoInicial);

        listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaNumSolicitud() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesProyectosControladorUrlEnum.URL10663
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), proyectoInicial);

        listaNumSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NOVEDAD");

    }

    public void cargarListaInforme() {
        if ("891855200-9".equals(SessionUtil.getCompaniaIngreso().getNit())
                      && "891855200".equals(SessionUtil.getCompaniaIngreso().getNit())) {
                      listaInforme.remove(8);
         }
    }

    public void oprimirexcel() {
        generarInforme(FORMATOS.EXCEL97);
    }

    private boolean mostrarMensaje(String var, String idiomas) {
        if (SysmanFunciones.validarVariableVacio(var)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(idiomas));
            return true;
        }
        return false;
    }

    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;
        cuerpoInforme(formato);
        pieInforme(formato);

    }

    private void descargarInfomeOpcion() {
        if (mostrarMensaje(proyectoInicial, "TB_TB2423")) {
            return;
        }
        if (mostrarMensaje(vigenciaInicial, TB_TB2474)) {
            return;
        }
        
            archivoDescarga = BancoProyectosReportes.generarInformeFES(
                            proyectoInicial, vigenciaInicial, modulo,
                            service, ejbBancoProyectoTres, logger);
            if (archivoDescarga == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2476"));
            }

        

    }

    private void cuerpoInforme(FORMATOS formato) {
        if ("1".equals(informe)) {
            informeProyectosyComponentes(formato);
        }
        else if ("2".equals(informe)) {

            generarInformeRgInformacion(formato);
        }
        else if ("3".equals(informe)) {
            generarInformeRecursosRegalias(formato);
        }
        else if ("4".equals(informe)) {
            generarInformeProyectosEstadoEspecial(formato);
        }
        else if ("5".equals(informe)) {
            generarInformeGestionFinanciacion(formato);
        }
        else if ("7".equals(informe)) {
            descargarInfomeOpcion();

        }

    }

    private void pieInforme(FORMATOS formato) {
        if ("8".equals(informe)) {

            generarInformePlanAccion(formato);
        }
        else if ("9".equals(informe)) {
            generarInformePorLocalizacion(formato);
        }
        else if ("10".equals(informe)) {
            generarInformeRegistroPresupuestal(formato);
        }
        else if ("11".equals(informe)) {
            generarInformeFormatoCDC15();
        }
        else if ("12".equals(informe)) {
            generarInformeRelacionRegistroPresupuestal(formato);
        }
        else if ("13".equals(informe)) {
            generarCertificadoProyecto();
        }
        else if ("14".equals(informe)) {
            generarInformeConceptoViabilidad(formato);
        }
        else if ("15".equals(informe)) {
            generarInformeCertificadoRegistro(formato);
        }
        else if ("16".equals(informe)) {
            generarInformeProyectosEjecuicion(formato);
        }else if("17".equals(informe)) {
        	generarInformeProgramacionyEjecucion(formato);
        }
    }

    private void generarInformeProgramacionyEjecucion(FORMATOS formato) {
		// lvega CC_2521
		String vigencia = "";
		String filtroAno = "";

		try {
			if (!vigenciaInicial.equals(TODAS)) {
				vigencia = "AND CA.VIGENCIA =  " + vigenciaInicial;
				filtroAno = "AND DETALLE_COMPROBANTE_PPTAL.ANO = " + vigenciaInicial;
			}

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("proyectoInicial", proyectoInicial);
			reemplazar.put("proyectoFinal", proyectoFin);
			reemplazar.put("vigencia", vigencia);
			reemplazar.put("filtroAno", filtroAno);

			boolean columnasAdicionales = "SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,
							"CAMPOS ADICIONALES EN INFORME BANCO PROYECTOS",
							SessionUtil.getModulo(), new Date(),true), "NO"));
			
			String nombreReporte = columnasAdicionales ? "002967ProyectoPorProgramacionEjecucionConAdicionales" : "002854ProyectoPorProgramacionEjecucion";

			String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo, reemplazar);

			Map<String, Object> parametros = new HashMap<>();

			parametros.put(PR_STRSQL, strsql);
			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
			
		} catch (JRException | IOException | SysmanException | SystemException ex) {
			JsfUtil.agregarMensajeError(idioma.getString(MSM_TRANS_INTERRUMPIDA) + ex.getMessage());
			Logger.getLogger(FrminformesProyectosControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		        
	}

	public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void informeProyectosyComponentes(FORMATOS formatos) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, TB_TB2474)) {
            return;
        }

        if (mostrarMensaje(estadoActual, TB_TB2477)
            || mostrarMensaje(enviado, TB_TB2478)
            || mostrarMensaje(codDependencia, TB_TB2475)) {
            return;
        }
        cuerpoProyectosyComponentes(formatos);
    }

    private void cuerpoProyectosyComponentes(FORMATOS formatos) {
        Map<String, Object> parametros = new HashMap<>();
        try {
            String nombreSub1 = "000265InfDetalleValorPerioProg";
            HashMap<String, Object> reemplazar1 = new HashMap<>();
            String sqlSub1 = Reporteador.resuelveConsulta(nombreSub1, modulo,
                            reemplazar1);
            parametros.put("PR_STRSQL_INFDETALLEVALORPERIOPROG", sqlSub1);

            String nombreSub2 = "000264InfDetalleProyecto";
            HashMap<String, Object> reemplazar2 = new HashMap<>();
            reemplazar2.put(VIGENCIA, vigenciaInicial);
            reemplazar2.put(PROYECTOINICIALS, proyectoInicial);
            reemplazar2.put(PROYECTOFINS, proyectoFin);
            String sqlSub2 = Reporteador.resuelveConsulta(nombreSub2, modulo,
                            reemplazar2);
            parametros.put("PR_STRSQL_INFDETALLEPROYECTO", sqlSub2);

            String nombreReporte = "000263RptInfoFisicoFinanciera";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(VIGENCIA, vigenciaInicial);
            reemplazar.put(PROYECTOINICIALS, proyectoInicial);
            reemplazar.put(PROYECTOFINS, proyectoFin);
            reemplazar.put(ESTADOACTUALS, estadoActual);
            reemplazar.put("entidad", codDependencia);

            switch (enviado) {
            case "1":
                reemplazar.put(ENVIADOS, "AND PROYECTOS.ENVIADONACION <> 0");
                break;
            case "2":
                reemplazar.put(ENVIADOS, "AND PROYECTOS.ENVIADODPTO <> 0");
                break;
            case "3":
                reemplazar.put(ENVIADOS,
                                "AND PROYECTOS.ENVIADOOTRAENTIDAD <> 0");
                break;
            default:
                reemplazar.put(ENVIADOS, "");
                break;
            }

            String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);
            parametros.put(PR_STRSQL, strsql);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeRgInformacion(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, TB_TB2474)) {
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        try {
            String nombreSub = "000319SubSolCDP";
            HashMap<String, Object> reemplazarSub = new HashMap<>();
            reemplazarSub.put(VIGENCIA, vigenciaInicial);
            String sqlSub = Reporteador.resuelveConsulta(nombreSub, modulo,
                            reemplazarSub);

            String nombreReporte = "000318RptRgInformacion";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(PROYECTOINICIALS, proyectoInicial);
            reemplazar.put(PROYECTOFINS, proyectoFin);

            if (!vigenciaInicial.equals(TODAS)) {
                reemplazar.put("vig",
                                "AND PROYECTOS.VIGENCIAINICIO <> 0 AND PROYECTOS.VIGENCIAFIN <> 0");
            }
            else {
                reemplazar.put("vig", "");
            }

            String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);
            parametros.put("PR_STRSQL_SUB_SOLCDP", sqlSub);
            parametros.put(PR_STRSQL, strsql);
            archivoDescarga = JsfUtil.exportarStreamed("000318RptRgInformacion",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeProyectosEstadoEspecial(FORMATOS formato) {
        if (mostrarMensaje(vigenciaInicial, TB_TB2474)
                        || mostrarMensaje(estadoActual, TB_TB2477)
                        || mostrarMensaje(enviado, TB_TB2478)
                        || mostrarMensaje(codDependencia, TB_TB2475)) {
            return;
        }
        try {
            String nombreReporte = "000329rptproyectossecretariasESTADOESPECIAL";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(VIGENCIA, vigenciaInicial);
            reemplazar.put(DEPENDENCIAS, codDependencia);
            reemplazar.put(ESTADOACTUALS, estadoActual);
            reemplazar.put(ENVIADOS, enviado);
            String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(PR_INFORME,
                            "CONTROL DE REGISTRO DE PROYECTOS DE INVERSIÓN SOCIAL");
            parametros.put(PR_VIGENCIAINICIAL, vigenciaInicial);
            parametros.put(PR_STRSQL, strsql);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeRecursosRegalias(FORMATOS formato) {

        if (mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, TB_TB2474)
            || mostrarMensaje(proyectoInicial, TB_TB809)) {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        String nombreReporte = "000322InfInfSolCertDisPresupuestal";

        reemplazar.put(PROYECTOINICIALS, proyectoInicial);
        reemplazar.put(PROYECTOFINS, proyectoFin);
        reemplazar.put(VIGENCIA, vigenciaInicial);

        String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PR_STRSQL, strSql);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeGestionFinanciacion(FORMATOS formato) {
        archivoDescarga = null;
        if (mostrarMensaje(estadoActual, TB_TB2477)
                        || mostrarMensaje(vigenciaInicial, TB_TB2474)
                        || mostrarMensaje(enviado, TB_TB2478)) {
            return;
        }
        Map<String, Object> parametros = new HashMap<>();
        try {
            String nombreReporte = "000333rptproyectossecretariasESTADOESPECIALenviados";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(VIGENCIA, vigenciaInicial);
            reemplazar.put(DEPENDENCIAS, codDependencia);
            reemplazar.put(ESTADOACTUALS, estadoActual);
            reemplazar.put(ENVIADOS, enviado);
            reemplazar.put(PROYECTOINICIALS, proyectoInicial);
            reemplazar.put(PROYECTOFINS, proyectoFin);

            String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);

            parametros.put("PR_FINANCIADORA", obtenerDependencia());
            parametros.put(PR_STRSQL, strsql);
            parametros.put(PR_INFORME,
                            "LISTADO DE PROYECTOS ENVIADOS A GESTIÓN Y FINANCIACIÓN");
            parametros.put(PR_VIGENCIAINICIAL, vigenciaInicial);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private String obtenerDependencia() {
        switch (enviado) {
        case "1":
            return "NACIÓN";
        case "2":
            return "DEPARTAMENTO";
        case "3":
            return "MUNICIPIO";
        case "X":
            return "OTROS";
        default:
            return "";
        }
    }


    private void generarInformePlanAccion(FORMATOS formatos) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
                        || mostrarMensaje(proyectoFin, TB_TB810)
                        || mostrarMensaje(vigenciaInicial, TB_TB2474)
                        || mostrarMensaje(codDependencia, TB_TB2475)) {
            return;
        }

        if (vigenciaInicial.equals(TODAS)) {
            vigenciaInicial = null;
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB250"));
            return;
        }
        Map<String, Object> parametros = new HashMap<>();

        try {
            String nombreSub = "000335subrptComponentesProyectoplanAccion";
            HashMap<String, Object> reemplazarSub = new HashMap<>();
            String sqlSub = Reporteador.resuelveConsulta(nombreSub, modulo,
                            reemplazarSub);

            String nombreReporte = "000334RptPlandeAccion";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put(DEPENDENCIAS, codDependencia);
            reemplazar.put(PROYECTOINICIALS, proyectoInicial);
            reemplazar.put(PROYECTOFINS, proyectoFin);
            reemplazar.put(VIGENCIA, vigenciaInicial);
            String strsql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);

            parametros.put(PR_INFORME, "PLAN DE ACCIÓN");
            parametros.put(PR_STRSQL, strsql);
            parametros.put(PR_VIGENCIAINICIAL, vigenciaInicial);
            parametros.put("PR_SUBSTRSQL", sqlSub);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformePorLocalizacion(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, TB_TB2474)
            || mostrarMensaje(estadoActual, TB_TB2477)) {
            return;
        }

        if (mostrarMensaje(enviado, TB_TB2478)
            || mostrarMensaje(codDependencia, TB_TB2475)
            || mostrarMensaje(barrioUbicacion, "TB_TB2480")) {
            return;
        }
        cuerpoInformePorLocalizacion(formato);

    }

    private void cuerpoInformePorLocalizacion(FORMATOS formato) {
        String nombreReporte = "000337InfActividadeslocalizacion";
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(VIGENCIAINICIALS, vigenciaInicial);
        reemplazar.put("barrioUbicacion", barrioUbicacion);
        reemplazar.put(ESTADOACTUALS, estadoActual);
        reemplazar.put(PROYECTOINICIALS, proyectoInicial);
        reemplazar.put(PROYECTOFINS, proyectoFin);
        reemplazar.put(VIGENCIAINICIALS, vigenciaInicial);
        reemplazar.put(ENVIADOS, enviado);
        reemplazar.put(DEPENDENCIAS, codDependencia);

        String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PR_GETUSER, SessionUtil.getUser().getCodigo());
        parametros.put(PR_STRSQL, strSql);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeRegistroPresupuestal(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, TB_TB2474)) {
            return;
        }

        String nombreSubReporte = "000339SubSolRES";
        String nombreReporte = "000338RptRgInformacionRes";
        HashMap<String, Object> reemplazarSub = new HashMap<>();
        String strSqlSub = Reporteador.resuelveConsulta(nombreSubReporte,
                        modulo, reemplazarSub);

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(PROYECTOINICIALS, proyectoInicial);
        reemplazar.put(PROYECTOFINS, proyectoFin);
        reemplazar.put(VIGENCIAINICIALS, vigenciaInicial);

        String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(PR_VIGENCIA, vigenciaInicial);
        parametros.put(PR_GETUSER, SessionUtil.getUser().getCodigo());
        parametros.put("PR_STRSQL_SUB_SOLCDP", strSqlSub);
        parametros.put(PR_STRSQL, strSql);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeConceptoViabilidad(FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(proyectoInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2462"));
            return;
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        String nombreReporte = null;
        try {
            nombreReporte = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CONCEPTO DE VIABILIDAD",
                            String.valueOf(modulo), new Date(), false);
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (nombreReporte == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2287"));
            return;
        }
        try {
            reemplazar.put("proyecto", proyectoInicial);
            String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(PR_STRSQL, strSql);
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeRelacionRegistroPresupuestal(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)) {
            return;
        }
        if (mostrarMensaje(proyectoFin, TB_TB810)) {
            return;
        }
        if (mostrarMensaje(vigenciaInicial, TB_TB2474)) { 
            return;
        }

        String nombreSubReporte = "000341SubSolCDP12";
        String nombreReporte = "000340InfActividadesRegistro";

        String strSqlSub = Reporteador.resuelveConsulta(nombreSubReporte,
                        modulo, new HashMap<String, Object>());

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(PROYECTOINICIALS, proyectoInicial);
        reemplazar.put(PROYECTOFINS, proyectoFin);
        reemplazar.put(VIGENCIAINICIALS, vigenciaInicial);
        String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_STRSQL_SUB", strSqlSub);
        parametros.put(PR_STRSQL, strSql);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }
    
    
    public String consultarParametro(String nombre) throws SystemException{
        return ejbSysmanUtil.consultarParametro(compania, nombre, String.valueOf(modulo), new Date(), false);
    }

    private void generarInformeCertificadoRegistro(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)) {
            return;
        }
        if (mostrarMensaje(proyectoFin, TB_TB810)) {
            return;
        }

        try {
            String nombreJefeBanco = consultarParametro("JEFE BANCO PROYECTOS"); 
            String cargoJefeBanco = consultarParametro("CARGO JEFE BANCO PROYECTOS");
            String planDeDesarrollo = consultarParametro(consPlanDesarrollo); 
            String tituloReporte = "LA OFICINA ASESORA DE PLANEACIÓN MUNICIPAL";
            String nombreSubreporte = "000153Subinforme BPPROYECTOSRUBROS";
            String nombreReporte = "000152rptCertificadoregistroproyecto";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("condicionProyecto", " BETWEEN '" + proyectoInicial
                + "' AND '" + proyectoFin + "' ");

            String strSqlSub = Reporteador.resuelveConsulta(nombreSubreporte,
                            modulo, new HashMap<String, Object>());
            String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                            reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("GETUSER", SessionUtil.getUser().getCodigo());
            parametros.put("PR_JEFE_BANCO_PROYECTOS", nombreJefeBanco);
            parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS", cargoJefeBanco);
            parametros.put("PR_TITULO_INFORME_CERTIFICADO_DE_REGISTRO",
                            tituloReporte);
            parametros.put(PR_STRSQL, strSql);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_PLAN_DE_DESARROLLO", planDeDesarrollo);
            parametros.put("PR_STRSQL_SUBINFORME", strSqlSub);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }catch (JRException | IOException
                        | SysmanException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    private void generarInformeProyectosEjecuicion(FORMATOS formato) {
        if (mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)
            || mostrarMensaje(vigenciaInicial, "TB_TB2483")
            || mostrarMensaje(vigenciaFin, "TB_TB2484")) {
            return;
        }

        if (mostrarMensaje(codDependencia, TB_TB2475)) {
            return;
        }

        cuerpoInformeProyectosEjecuicion(formato);

    }

    private boolean negarVariable(String var1, String var2, String idiomas) {
        if (var1.equals(TODAS) && !var2.equals(TODAS)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(idiomas));
            return true;
        }
        return false;
    }

    private void cuerpoInformeProyectosEjecuicion(FORMATOS formato) {
        if (negarVariable(vigenciaInicial, vigenciaFin, "TB_TB2485")
            || negarVariable(vigenciaFin, vigenciaInicial, "TB_TB2485")) {
            return;
        }
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(PROYECTOINICIALS, proyectoInicial);
        reemplazar.put(PROYECTOFINS, proyectoFin);
        reemplazar.put(DEPENDENCIAS, codDependencia);
        reemplazar.put("vigenciaInicio", vigenciaInicial);
        reemplazar.put("vigenciaFin", vigenciaFin);

        String nombreReporte = "000356rptProyectosEnEjecucion";

        String strSql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);

        Map<String, Object> parametros = new HashMap<>();

        try {
            if (vigenciaInicial.equals(TODAS)) {
                parametros.put(PR_VIGENCIA, "TODAS LAS VIGENCIAS");
            }
            else {
                parametros.put(PR_VIGENCIA, "Entre la vigencia "
                    + vigenciaInicial + " y la vigencia " + vigenciaFin);
            }
            parametros.put("PR_PROYECTOFINAL", proyectoFin);
            parametros.put("PR_PROYECTOINICIAL", proyectoInicial);
            parametros.put(PR_GETUSER, SessionUtil.getUser().getCodigo());
            parametros.put(PR_STRSQL, strSql);
            if ("X".equals(codDependencia)) {
                parametros.put("PR_DEPENDENCIA", "TODAS LAS DEPENDENCIAS");
            }
            else {
                parametros.put("PR_DEPENDENCIA", "DEPENDENCIA " + dependencia);
            }
            parametros.put(PR_INFORME, "PROYECTOS EN EJECUCIÓN");

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarInformeFormatoCDC15() {
        String alcaldeMunicipal;
        String secretarioDespacho;
        String planDesarrollo;
        if (mostrarMensaje(vigenciaInicial, TB_TB2474)
            || mostrarMensaje(proyectoInicial, TB_TB809)
            || mostrarMensaje(proyectoFin, TB_TB810)) {
            return;
        }
        if (contArchivoSelector.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }
        try (FileInputStream file = new FileInputStream(
                        contArchivoSelector.getArchivo())) {

            Workbook workbook = new HSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            
            Font font2 = workbook.createFont();
            font2.setFontName(consLetra);
            font2.setFontHeightInPoints((short) 11);
            font2.setBold(false);
            
            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_CENTER);
            style2.setFont(font2);
            style2.setBorderBottom((short) 1);
            style2.setBorderLeft((short) 1);
            style2.setBorderTop((short) 1);
            style2.setBorderRight((short) 1);
            
            alcaldeMunicipal = consultarParametro("ALCALDE MUNICIPAL");
            secretarioDespacho = consultarParametro(
                            "SECRETARIO DESPACHO FROMATO BPPIM");
            planDesarrollo = consultarParametro(consPlanDesarrollo);
 
            String datos= ejbBancoProyectoTres.prepararDatos(compania, vigenciaInicial,
                            proyectoInicial, proyectoFin);
            
            String [] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
            String [] columna=null;
            boolean estado=true;
            
            Row fila;
            Cell celda;
            fila = sheet.getRow(4);
            celda = fila.createCell(1);
            celda.setCellValue(planDesarrollo);
            if(!SysmanFunciones.validarVariableVacio(datos)){
                for (int i = 0; i < registro.length; i++) {
                    columna =  registro[i].split(SysmanConstantes.SEPARADOR_COL);
                    Row r = sheet.createRow(12 + i);
                    
                    for (int j = 0; j < 13; j++) {   
                        Cell cell2 = r.createCell(j);
                        cell2.setCellValue(columna[j]);
                        cell2.setCellStyle(style2);
                        if(estado && columna.length > 14){
                            fila = sheet.getRow(4);
                            celda = fila.createCell(12);
                            celda.setCellValue(columna[14]);
                            
                            fila = sheet.getRow(5);
                            celda = fila.createCell(1);
                            celda.setCellValue(columna[15]);
                            
                            fila = sheet.getRow(6);
                            celda = fila.createCell(1);
                            celda.setCellValue(columna[16]);
                            
                            fila = sheet.getRow(7);
                            celda = fila.createCell(1);
                            celda.setCellValue(columna[17]);
                            estado=false;
                        }
                    }
                }
            }
    
            fila = sheet.createRow(sheet.getLastRowNum()+3);
            celda = fila.createCell(0);
            celda.setCellValue(alcaldeMunicipal);
            
            celda = fila.createCell(5);
            celda.setCellValue(secretarioDespacho);
            
            fila = sheet.createRow(sheet.getLastRowNum()+1);
            celda = fila.createCell(0);
            celda.setCellValue(idioma.getString("TB_TB3677"));

            celda = fila.createCell(5);
            celda.setCellValue(idioma.getString("TB_TB3678"));
            
            
            fila = sheet.createRow(sheet.getLastRowNum()+2);
            celda = fila.createCell(1);
            celda.setCellValue(SessionUtil.getUser().getCodigo());
            
            celda = fila.createCell(0);
            celda.setCellValue(idioma.getString("TG_ELABORO2"));
            
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
 
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            "000379RptFormatoCDC-15.xls");
            workbook.close();
        }
        catch (JRException | IOException | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
    }


    private String crearDominio(List<Registro> lista) {
        StringBuilder dominio = new StringBuilder("");
        for (int i = 0; i < lista.size(); i++) {
            if (i == 0) {
                dominio.append(lista.get(i).getCampos().get(VIGENCIA_META)
                                .toString());
            }
            else {
                dominio.append(", "
                    + lista.get(i).getCampos().get(VIGENCIA_META).toString());
            }
        }
        return dominio.toString();
    }

    private void generarCertificadoProyecto() {
        if (mostrarMensaje(proyectoInicial, TB_TB809) || (numSolicitud == null)
                        || mostrarMensaje(vigenciaInicial, TB_TB2474)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2486"));
            return;
        }

        if (contArchivoSelector.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }
        try (FileInputStream file = new FileInputStream(
                        contArchivoSelector.getArchivo())) {

            Workbook workbook = new HSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Cell celda;
            Row fila;
            CellStyle style;
            CellStyle styleBold;
            CellStyle styleNum;

            style = workbook.createCellStyle();
            style.setBorderBottom(CellStyle.BORDER_HAIR);
            style.setBottomBorderColor(
                            IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderLeft(CellStyle.BORDER_HAIR);
            style.setLeftBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderRight(CellStyle.BORDER_HAIR);
            style.setRightBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderTop(CellStyle.BORDER_HAIR);
            style.setTopBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 8);
            font.setFontName(consLetra);
            style.setFont(font);
            styleNum = style;
            styleNum.setDataFormat(
                            workbook.createDataFormat().getFormat("#,##0.00"));
            styleBold = workbook.createCellStyle();
            Font fontBold = workbook.createFont();
            fontBold.setFontHeightInPoints((short) 8);
            fontBold.setFontName(consLetra);
            fontBold.setBold(true);
            styleBold.setFont(fontBold);

            List<Registro> aux;
            Registro regAux;

            String planDesarrollo = SysmanFunciones
                            .nvl(consultarParametro(consPlanDesarrollo),
                                            "")
                            .toString();
            
            celda = sheet.getRow(7).getCell(1);
            celda.setCellValue(planDesarrollo);

            String nomResponsableAsoc = SessionUtil.getUser()
                            .getResponsableAso().getNombre();

            if (nomResponsableAsoc != null) {
                celda = sheet.getRow(35).getCell(8);
                celda.setCellValue("Proyecto y revisión: "
                    + SysmanFunciones.nvl(nomResponsableAsoc, " ")
                    + " - Profesional Universitario");
            }
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), proyectoInicial);

            regAux = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrminformesProyectosControladorUrlEnum.URL4852
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            celda = sheet.getRow(12).getCell(1);
            celda.setCellValue(regAux.getCampos().get("NOMBREPROYECTO")
                            .toString().toUpperCase());
            String strCodigoBPIM = regAux.getCampos().get("CODIGOBPIM")
                            .toString();
            celda = sheet.getRow(14).getCell(1);
            celda.setCellValue(strCodigoBPIM);

            String viini = regAux.getCampos().get("VIGENCIAINICIO").toString();
            
            param.remove(GeneralParameterEnum.CODIGO.getName());
            List<Registro> listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL74184
                                                                            .getValue())
                                            .getUrl(), param));
            

            String dominio = listaVigencia != null ? crearDominio(listaVigencia)
                : "' '";

            String vigenciaAux = vigenciaInicial.equals(TODAS) ? dominio
                : vigenciaInicial;

            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.NOVEDAD.getName(), numSolicitud);
            param2.put(GeneralParameterEnum.PROYECTO.getName(),
                            proyectoInicial);
            param2.put(GeneralParameterEnum.VIGENCIA.getName(), vigenciaAux);

            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL85158
                                                                            .getValue())
                                            .getUrl(), param2));


            int j = 25;
            StringBuilder strmetprod = new StringBuilder("");
            StringBuilder stridprod = new StringBuilder("");

            for (Registro aux1 : aux) {
                sheet.shiftRows(j, sheet.getLastRowNum(), 1);
                fila = sheet.createRow(j);
                celda = fila.createCell(0);
                celda.setCellValue(aux1.getCampos().get(VIGENCIA_META)
                                .toString());
                celda.setCellStyle(style);
                celda = fila.createCell(1);
                celda.setCellValue(aux1.getCampos().get("NOMBRE_MEDIDA")
                                .toString().toUpperCase());
                celda.setCellStyle(style);
                celda = fila.createCell(2);
                celda.setCellValue(Double.parseDouble(aux1.getCampos()
                                .get("CANTIDAD_AUX").toString()));
                celda.setCellStyle(styleNum);
                celda = fila.createCell(3);
                celda.setCellValue(Double.parseDouble(aux1.getCampos()
                                .get("CANTIDAD_EJECUTADA1").toString()));
                celda.setCellStyle(styleNum);
                celda = fila.createCell(4);
                celda.setCellValue(
                                aux1.getCampos().get("COMPONENTE").toString());
                celda.setCellStyle(style);

                strmetprod.append(SysmanFunciones.nvl(
                                aux1.getCampos().get("NOMBREMETAPRODUCTO"), "")
                    + " ");
                stridprod.append(SysmanFunciones.nvl(
                                aux1.getCampos().get("ID_META_PRODUCTO"), ""));

                j++;
            }

            int k = j;

            param2.remove(GeneralParameterEnum.NOVEDAD.getName());
            param2.remove(GeneralParameterEnum.PROYECTO.getName());
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL90263
                                                                            .getValue())
                                            .getUrl(), param2));

            if (!"".equals(stridprod.toString()) && (aux.size() >= 5)) {
                String strNivel = stridprod.substring(0, Integer.parseInt(aux
                                .get(4).getCampos().get(DIGITOS).toString()));
                celda = sheet.getRow(18).getCell(0);
                celda.setCellValue(descripcionNivel(strNivel, viini, viini));
                strNivel = stridprod.substring(0, Integer.parseInt(aux.get(2)
                                .getCampos().get(DIGITOS).toString()));
                celda = sheet.getRow(10).getCell(1);
                celda.setCellValue(descripcionNivel(strNivel, viini, viini));
                strNivel = stridprod.substring(0, Integer.parseInt(aux.get(1)
                                .getCampos().get(DIGITOS).toString()));
                celda = sheet.getRow(9).getCell(1);
                celda.setCellValue(descripcionNivel(strNivel, viini, viini));
                strNivel = stridprod.substring(0, Integer.parseInt(aux.get(0)
                                .getCampos().get(DIGITOS).toString()));
                celda = sheet.getRow(8).getCell(1);
                celda.setCellValue(descripcionNivel(strNivel, viini, viini));
            }

            celda = sheet.getRow(22).getCell(0);
            celda.setCellValue(strmetprod.toString());
            
            Map<String, Object> param3 = new TreeMap<>();
            param3.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param3.put(GeneralParameterEnum.CODIGO.getName(),
                            numSolicitud);

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL83647
                                                                            .getValue())
                                            .getUrl(), param3));

            String strFechaValidez;

            if (regAux != null) {
                celda = sheet.getRow(0).getCell(0);
                celda.setCellValue("CERTIFICADO DE VIABILIDAD No. "
                    + SysmanFunciones.nvl(
                                    regAux.getCampos().get("NUMERORADICADO"),
                                    regAux.getCampos().get(CODIGO))
                    + " DE " + strCodigoBPIM.substring(0, 4));
                strFechaValidez = SysmanFunciones
                                .nvl(regAux.getCampos().get("FECHA_VALIDEZ"),
                                                "Sin fecha")
                                .toString();
            }
            else {
                strFechaValidez = "Sin fecha";
            }
            
            Map<String,Object> param4 = new TreeMap<>();
            param4.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param4.put(GeneralParameterEnum.NOVEDAD.getName(),numSolicitud);
            param4.put(GeneralParameterEnum.PROYECTO.getName(),proyectoInicial);


            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL93694
                                                                            .getValue())
                                            .getUrl(), param4));

            StringBuilder strrubros = new StringBuilder("");
            StringBuilder strrubrosAux = new StringBuilder("");

            for (Registro aux1 : aux) {
                if (aux1.getCampos().get(RUBROPRESUPUESTAL) != null) {
                    strrubros.append(aux1.getCampos().get(RUBROPRESUPUESTAL)
                        + ", ");
                    strrubrosAux.append(aux1.getCampos().get(RUBROPRESUPUESTAL)
                        + "','");
                }
            }

            if (!aux.isEmpty()) {
                strrubros.append(
                                strrubros.substring(0, strrubros.length() - 2));
                strrubrosAux.append("'"
                    + strrubrosAux.substring(0, strrubrosAux.length() - 2));
            }

            celda = sheet.getRow(8).getCell(7);
            celda.setCellValue("RUBRO " + strrubros.toString());


            Map<String, Object> param5 = new TreeMap<>();
            param5.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param5.put(GeneralParameterEnum.VIGENCIA.getName(),
                            vigenciaAux);
            
            param5.put(GeneralParameterEnum.RUBRO.getName(),
                            "".equals(strrubrosAux.toString()) ? "''"
                                : strrubrosAux.toString());
            
            
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL7145
                                                                            .getValue())
                                            .getUrl(), param5));

            StringBuilder strCodPptal = new StringBuilder("");

            for (Registro aux1 : aux) {
                strCodPptal.append(aux1.getCampos().get(CODIGO) + "','");
            }

            if (!aux.isEmpty()) {
                strCodPptal.append("'"
                    + strCodPptal.substring(0, strCodPptal.length() - 2));
            }

            Map<String,Object> param6 = new TreeMap<>();
            param6.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param6.put(GeneralParameterEnum.VIGENCIA.getName(),vigenciaAux);
            param6.put(GeneralParameterEnum.NOVEDAD.getName(),numSolicitud);
            param6.put(GeneralParameterEnum.CODIGO.getName(),"".equals(strCodPptal.toString()) ? "''"
                : strCodPptal.toString());


            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL96497
                                                                            .getValue())
                                            .getUrl(), param));


            int i = 10;
            j = 8;

            for (Registro aux1 : aux) {
                celda = sheet.getRow(i).getCell(j);
                celda.setCellValue(SysmanFunciones
                                .nvl(aux1.getCampos().get("NOMBREPLAN"), "..")
                                .toString());
                celda = sheet.getRow(i).getCell(j + 1);
                celda.setCellValue(Double.parseDouble(SysmanFunciones
                                .nvl(aux1.getCampos().get("APROPIACIONINICIAL"),
                                                "0")
                                .toString()));
                celda = sheet.getRow(i).getCell(j + 2);
                celda.setCellValue(Double.parseDouble(SysmanFunciones
                                .nvl(aux1.getCampos().get(
                                                "VALORSOLICITADO_AUX"), "0")
                                .toString()));
                celda = sheet.getRow(i).getCell(j + 3);
                celda.setCellValue(Double.parseDouble(SysmanFunciones
                                .nvl(aux1.getCampos().get("TOTALDIS1"), "0")
                                .toString()));
                i++;
            }
            
            Map<String, Object> param7 = new TreeMap<>();
            param7.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param7.put(GeneralParameterEnum.NOVEDAD.getName(),
                            numSolicitud);

            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL101866
                                                                            .getValue())
                                            .getUrl(), param7));

            j = k + 4;
            int id = 1;

            for (Registro aux1 : aux) {
                sheet.shiftRows(j, sheet.getLastRowNum(), 1);
                CellRangeAddress reg = CellRangeAddress
                                .valueOf("A" + (j + 1) + ":K" + (j + 1));
                sheet.addMergedRegion(reg);
                RegionUtil.setBorderBottom(CellStyle.BORDER_HAIR, reg, sheet,
                                workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_HAIR, reg, sheet,
                                workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_HAIR, reg, sheet,
                                workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_HAIR, reg, sheet,
                                workbook);

                fila = sheet.getRow(j);
                celda = fila.createCell(0);
                celda.setCellValue(id + ". "
                    + SysmanFunciones.nvl(aux1.getCampos().get(NOMBRE), " ")
                                    .toString());
                celda.setCellStyle(style);
                celda = fila.createCell(11);
                celda.setCellValue(Double.parseDouble(SysmanFunciones
                                .nvl(aux1.getCampos().get("VALORSOLICITADO"),
                                                "0")
                                .toString()));
                celda.setCellStyle(styleNum);
                j++;
                id++;
            }

            fila = sheet.createRow(j + 4);
            celda = fila.createCell(0);
            celda.setCellStyle(styleBold);
            celda.setCellValue(idioma.getString("TB_TB3573").replace("#fecha#", strFechaValidez));

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);
            fileOut.close();
            file.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(fileOut.toByteArray()),
                            "Archivo Salida.xls");
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2079"));
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (IOException | JRException | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
            Logger.getLogger(FrminformesProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public String descripcionNivel(String strNivel, String vigenciaIni,
        String vigenciaFin) {
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(FrminformesProyectosControladorEnum.PARAM0.getValue(), strNivel);
        param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),vigenciaIni);
        param.put(FrminformesProyectosControladorEnum.PARAM2.getValue(),vigenciaFin);
        String descripcion="";
        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesProyectosControladorUrlEnum.URL101866
                                                                            .getValue())
                                            .getUrl(), param));
            if (regAux != null) {
                descripcion = SysmanFunciones
                                .nvl(regAux.getCampos().get("DESCRIPCION"), "")
                                .toString();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return descripcion;
    }

    public int getModulo() {
        return modulo;
    }

    public void seleccionarFilaProyectoinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "").toString();
        numSolicitud = null;
        cargarListaNumSolicitud();
        cargarListaProyectofinal();
    }

    public void seleccionarFilaProyectofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectoFin = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "").toString();
    }

    public void seleccionarFilaNumSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numSolicitud = registroAux.getCampos().get("NOVEDAD").toString();
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones.nvl(registroAux.getCampos().get(NOMBRE), "").toString();
        codDependencia = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "").toString();
    }

    public void cambiarCUADRO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarCUADRO() {
        // <CODIGO_DESARROLLADO>
        preparaDatos = true;
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCUADRO() {
        // <CODIGO_DESARROLLADO>
        preparaDatos = false;
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public String getProyectoInicial() {
        return proyectoInicial;
    }

    public void setProyectoInicial(String proyectoInicial) {
        this.proyectoInicial = proyectoInicial;
    }

    public String getProyectoFin() {
        return proyectoFin;
    }

    public void setProyectoFin(String proyectoFin) {
        this.proyectoFin = proyectoFin;
    }

    public String getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(String vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }

    public String getBarrioUbicacion() {
        return barrioUbicacion;
    }

    public void setBarrioUbicacion(String barrioUbicacion) {
        this.barrioUbicacion = barrioUbicacion;
    }

    public String getNumSolicitud() {
        return numSolicitud;
    }

    public void setNumSolicitud(String numSolicitud) {
        this.numSolicitud = numSolicitud;
    }

    public String getVigenciaFin() {
        return vigenciaFin;
    }

    public void setVigenciaFin(String vigenciaFin) {
        this.vigenciaFin = vigenciaFin;
    }

    public String getNombreInforme() {
        return nombreInforme;
    }

    public void setNombreInforme(String nombreInforme) {
        this.nombreInforme = nombreInforme;
    }

    public List<Registro> getListaVIGENCIAINICIAL() {
        return listaVIGENCIAINICIAL;
    }

    public void setListaVIGENCIAINICIAL(List<Registro> listaVIGENCIAINICIAL) {
        this.listaVIGENCIAINICIAL = listaVIGENCIAINICIAL;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public List<Registro> getListaBarrioUbicacion() {
        return listaBarrioUbicacion;
    }

    public void setListaBarrioUbicacion(List<Registro> listaBarrioUbicacion) {
        this.listaBarrioUbicacion = listaBarrioUbicacion;
    }

    public List<Registro> getListaVIGENCIAFINAL() {
        return listaVIGENCIAFINAL;
    }

    public void setListaVIGENCIAFINAL(List<Registro> listaVIGENCIAFINAL) {
        this.listaVIGENCIAFINAL = listaVIGENCIAFINAL;
    }

    public RegistroDataModelImpl getListaProyectoinicial() {
        return listaProyectoinicial;
    }

    public void setListaProyectoinicial(
        RegistroDataModelImpl listaProyectoinicial) {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    public RegistroDataModelImpl getListaProyectofinal() {
        return listaProyectofinal;
    }

    public void setListaProyectofinal(RegistroDataModelImpl listaProyectofinal) {
        this.listaProyectofinal = listaProyectofinal;
    }

    public RegistroDataModelImpl getListaNumSolicitud() {
        return listaNumSolicitud;
    }

    public void setListaNumSolicitud(RegistroDataModelImpl listaNumSolicitud) {
        this.listaNumSolicitud = listaNumSolicitud;
    }

    public String getCodDependencia() {
        return codDependencia;
    }

    public void setCodDependencia(String codDependencia) {
        this.codDependencia = codDependencia;
    }

    public boolean isPreparaDatos() {
        return preparaDatos;
    }

    public void setPreparaDatos(boolean preparaDatos) {
        this.preparaDatos = preparaDatos;
    }

    public void cambiarInforme() {
        vigenciaInicial = TODAS;
        if ("8".equals(informe)) {
            vigenciaInicial = null;
            return;
        }
        if ("14".equals(informe) ||  "13".equals(informe)) {
            proyectoInicial = null;
        }
    }
    
    /**
     * Metodo ejecutado al cambiar el control VIGENCIAINICIAL
     * 
     */
    public void cambiarVIGENCIAINICIAL() {
        //<CODIGO_DESARROLLADO>
        cargarListaVIGENCIAFINAL();
        //</CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaInforme() {
        return listaInforme;
    }

    public void setListaInforme(List<Registro> listaInforme) {
        this.listaInforme = listaInforme;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public ContenedorArchivo getContArchivoSelector() {
        return contArchivoSelector;
    }

    public void setContArchivoSelector(ContenedorArchivo contArchivoSelector) {
        this.contArchivoSelector = contArchivoSelector;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        informe = "1";
        proyectoInicial = "00000000";
        proyectoFin = "9999999999";
        vigenciaInicial = TODAS;
        estadoActual = "X";
        enviado = "X";
        codDependencia = "X";
        dependencia = TODAS;
        barrioUbicacion = "X";
        // </CODIGO_DESARROLLADO>
    }
}
