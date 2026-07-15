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
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.AcumuladosUnoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jgomez
 * @version 1, 29/07/2015
 * 
 * @version 2, 29/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class AcumuladosUnoControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloNomina;
    private final String procesoNomina;
    private String varAno1;
    private String varAno2;
    private String varMes1;
    private String varMes2;
    private String varPeriodo1;
    private String varPeriodo2;
    private String varProceso;
    private String cedula;
    private String nombreEmpleado;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private String tipo;
    private String idDeEmpleado;
    private StreamedContent archivoDescarga;
    private static final String CTENOMBRE = "NOMBRE";
    
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    
    
    
    

    /**
     * Creates a new instance of AcumuladosUnoControlador
     */
    public AcumuladosUnoControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        try {
            numFormulario = GeneralCodigoFormaEnum.ACUMULADOS_UNO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AcumuladosUnoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        varProceso = (String) SessionUtil.getSessionVar("procesoNomina");
        varAno1 = (String) SessionUtil.getSessionVar("anioNomina");
        varAno2 = (String) SessionUtil.getSessionVar("anioNomina");
        varMes1 = (String) SessionUtil.getSessionVar("mesNomina");
        varMes2 = (String) SessionUtil.getSessionVar("mesNomina");
        varPeriodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
        varPeriodo2 = (String) SessionUtil.getSessionVar("periodoNomina");
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        abrirFormulario();
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno1() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL3933
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),varProceso);
            
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL4509
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),varAno1);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),varProceso);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL5057
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),varAno2);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),varProceso);

        try {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL5057
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),varAno1);
            param.put(GeneralParameterEnum.MES.getName(),varMes1);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),varProceso);

            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL5973
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),varAno2);
            param.put(GeneralParameterEnum.MES.getName(),varMes2);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),varProceso);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL5973
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladosUnoControladorUrlEnum.URL8762
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPresentar() {
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirComando20() {
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton CnBaseLiq
     * en la vista
     *
     *
     */
public void oprimirCnBaseLiq() {
         //<CODIGO_DESARROLLADO>
         archivoDescarga=null;    
         generarInfCnBases(ReportesBean.FORMATOS.PDF);
        //</CODIGO_DESARROLLADO>
    }

    private boolean validarCampos() {
        boolean retorno = false;
        if (varAno1 == null || varAno2 == null) {
            retorno = true;
        }
        if (varMes1 == null || varMes2 == null) {
            retorno = true;
        }
        if (varPeriodo1 == null || varPeriodo2 == null || varProceso == null) {
            retorno = true;
        }
        return retorno;
    }

    private void genInforme(FORMATOS formato) {
        if (validarCampos()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2523"));
            return;
        }
        String perInicial = SysmanFunciones.concatenar(varAno1, SysmanFunciones.padl(varMes1, 2, "0")
            ,SysmanFunciones.padl(varPeriodo1, 2, "0"));
        String perFinal = SysmanFunciones.concatenar(varAno2, SysmanFunciones.padl(varMes2, 2, "0")
            ,SysmanFunciones.padl(varPeriodo2, 2, "0"));
        if (perInicial.compareTo(perFinal) > 0) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2509"));
            return;
        }
        try {
            String reporte = SysmanFunciones.nvl(ejbSysmanUtilRemote.consultarParametro(compania, "FORMATO INFORME ACUMULADOS", moduloNomina, new Date(), false), "000123AcumuladosUno").toString();
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("empleado", idDeEmpleado);
            reemplazar.put("perInicial", perInicial);
            reemplazar.put("perFinal", perFinal);
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(moduloNomina), reemplazar);
            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            String nombreMes1 = service.buscarEnLista(varMes1, "MES", CTENOMBRE,
                            listaMes1);
            String nombreMes2 = service.buscarEnLista(varMes2, "MES", CTENOMBRE,
                            listaMes2);
            String nombrePer1 = service.buscarEnLista(varPeriodo1, "PERIODO",
                            CTENOMBRE, listaPeriodo1);
            String nombrePer2 = service.buscarEnLista(varPeriodo2, "PERIODO",
                            CTENOMBRE, listaPeriodo2);

            String titulo = SysmanFunciones.concatenar("Entre ", nombreMes1,
                            " de ", varAno1, " Periodo ", nombrePer1, " y ",
                            nombreMes2, " de ", varAno2, " Periodo ",
                            nombrePer2);
            double salarioCategoria = ejbNominaUno.getSalarioBaseCategoria(
                            compania, Integer.parseInt(idDeEmpleado))
                            .doubleValue();
            double deducible = ejbNominaUno.getDeducible(compania,
                            Integer.parseInt(idDeEmpleado)).doubleValue();
            double deducibleSalud = ejbNominaCero.getDeducibleSalud(
                            compania, Integer.parseInt(idDeEmpleado),
                            Integer.parseInt(procesoNomina)).doubleValue();
            double porcReteFuente = ejbNominaCero.getPorcentateRetefuente(
                            compania, Integer.parseInt(idDeEmpleado),
                            Integer.parseInt(procesoNomina)).doubleValue();
            
            String nombreAutoriza = ejbSysmanUtilRemote.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA", moduloNomina, new Date(), false);
            String cargoAutoriza = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO DE QUIEN AUTORIZA NOMINA", moduloNomina, new Date(), false);
            String nombreRecursos = ejbSysmanUtilRemote.consultarParametro(compania, "NOMBRE DEL JEFE DE RECURSOS HUMANOS", moduloNomina, new Date(), false);
            String cargoRecursos = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS", moduloNomina, new Date(), false);
            String revisaNomina = ejbSysmanUtilRemote.consultarParametro(compania, "NOMBRE DE QUIEN REVISA NOMINA", moduloNomina, new Date(), false);
            String revisaNominaCargo = ejbSysmanUtilRemote.consultarParametro(compania, "CARGO DE QUIEN REVISA NOMINA", moduloNomina, new Date(), false);
            
            
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_PORCRTEFTE", porcReteFuente);
            parametros.put("PR_SALARIO_BASE_IBC", salarioCategoria);
            parametros.put("PR_DEDUCIBLE", deducible);
            parametros.put("PR_DEDUCIBLESALUD", deducibleSalud);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA", nombreAutoriza);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoAutoriza);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreRecursos);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoRecursos);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", revisaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", revisaNominaCargo);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (SystemException | JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            ex.getMessage()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void generarInfCnBases(FORMATOS formato) {
    	
    	 String perInicial = SysmanFunciones.concatenar(varAno1, SysmanFunciones.padl(varMes1, 2, "0")
    	            ,SysmanFunciones.padl(varPeriodo1, 2, "0"));
    	        String perFinal = SysmanFunciones.concatenar(varAno2, SysmanFunciones.padl(varMes2, 2, "0")
    	            ,SysmanFunciones.padl(varPeriodo2, 2, "0"));
    	        if (perInicial.compareTo(perFinal) > 0) {
    	            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2509"));
    	            return;
    	        }
    	 try {
    	String reporte = "002480ConceptoBaseLiquidacion";
    	String subAutoliq = "002481SubAutoliquidacion";
    	String subParafiscal = "002482SubParafiscal";
    	String subRetefuente = "002483SubRetefuente";
    	
    	Map<String, Object> parametros = new HashMap<>();
    	HashMap<String, Object> reemplazar = new HashMap<>();
    	reemplazar.put("empleado", idDeEmpleado);
        reemplazar.put("perInicial", perInicial);
        reemplazar.put("perFinal", perFinal);
        
    	String strSql = Reporteador.resuelveConsulta(reporte,
                Integer.parseInt(moduloNomina), reemplazar);
    	
    	reemplazar.put("consultaBase", strSql);
    	
    	String sqlAutoliq = Reporteador.resuelveConsulta(subAutoliq,
                Integer.parseInt(moduloNomina), reemplazar);
    	String sqlParafiscal = Reporteador.resuelveConsulta(subParafiscal,
                Integer.parseInt(moduloNomina), reemplazar);
    	String sqlRetefuente = Reporteador.resuelveConsulta(subRetefuente,
                Integer.parseInt(moduloNomina), reemplazar);
    	
    	
    	  parametros.put("PR_STRSQL", strSql);
    	  parametros.put("PR_SQL_REPORTE1", sqlAutoliq);
    	  parametros.put("PR_SQL_REPORTE2", sqlParafiscal);
    	  parametros.put("PR_SQL_REPORTE3", sqlRetefuente);
    	  
    	 
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
			          ConectorPool.ESQUEMA_SYSMAN, formato);
			
		} catch (JRException | IOException | SysmanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }

    public void cambiarAno1() {
        varMes1 = null;
        varPeriodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public void cambiarAno2() {
        varMes2 = null;
        varPeriodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    public void cambiarMes1() {
        varPeriodo1 = null;
        cargarListaPeriodo1();
    }

    public void cambiarMes2() {
        varPeriodo2 = null;
        cargarListaPeriodo2();
    }

    public void cambiarProceso() {
        varMes1 = null;
        varMes2 = null;
        varPeriodo1 = null;
        varPeriodo2 = null;
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getVarAno1() {
        return varAno1;
    }

    public void setVarAno1(String varAno1) {
        this.varAno1 = varAno1;
    }

    public String getVarAno2() {
        return varAno2;
    }

    public void setVarAno2(String varAno2) {
        this.varAno2 = varAno2;
    }

    public String getVarMes1() {
        return varMes1;
    }

    public void setVarMes1(String varMes1) {
        this.varMes1 = varMes1;
    }

    public String getVarMes2() {
        return varMes2;
    }

    public void setVarMes2(String varMes2) {
        this.varMes2 = varMes2;
    }

    public String getVarPeriodo1() {
        return varPeriodo1;
    }

    public void setVarPeriodo1(String varPeriodo1) {
        this.varPeriodo1 = varPeriodo1;
    }

    public String getVarPeriodo2() {
        return varPeriodo2;
    }

    public void setVarPeriodo2(String varPeriodo2) {
        this.varPeriodo2 = varPeriodo2;
    }

    public String getVarProceso() {
        return varProceso;
    }

    public void setVarProceso(String varProceso) {
        this.varProceso = varProceso;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdDeEmpleado() {
        return idDeEmpleado;
    }

    public void setIdDeEmpleado(String idDeEmpleado) {
        this.idDeEmpleado = idDeEmpleado;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
