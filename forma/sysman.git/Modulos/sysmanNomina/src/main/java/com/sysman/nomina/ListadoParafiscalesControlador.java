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
import com.sysman.nomina.enums.ListadoParafiscalesControladorEnum;
import com.sysman.nomina.enums.ListadoParafiscalesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 03/08/2015
 * @modified spina 23/03/2017 Depuracion sonar - se eliminan
 * parametros innecesarios
 * 
 * @author eamaya
 * @version 2.0,10/10/2017 Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class ListadoParafiscalesControlador extends BeanBaseModal {

    private String compania;
    private String mes;
    private String anio;
    private String procesoNomina;
    private String ano1;
    private String mes1;
    private String proceso;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaProceso;
    private StreamedContent archivoDescarga;
    private String nombreCompania;
    private String modulo;
	private String periodo;
    
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
    

    /**
     * Creates a new instance of ListadoParafiscalesControlador
     */
    public ListadoParafiscalesControlador() {
        super();

        try {
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            mes = SessionUtil.getSessionVar("mesNomina").toString();
            anio = SessionUtil.getSessionVar("anioNomina").toString();
            procesoNomina = SessionUtil.getSessionVar("procesoNomina")
                            .toString();
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            numFormulario = GeneralCodigoFormaEnum.LISTADO_PARAFISCALES_CONTROLADOR
                            .getCodigo();
            periodo = SessionUtil.getSessionVar("periodoNomina").toString();
            
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ListadoParafiscalesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaProceso();
        abrirFormulario();
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        procesoNomina);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoParafiscalesControladorUrlEnum.URL3196
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
        param.put(ListadoParafiscalesControladorEnum.PROCESO.getValue(),
                        procesoNomina);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoParafiscalesControladorUrlEnum.URL3846
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

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoParafiscalesControladorUrlEnum.URL4649
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ListadoParafiscalesControladorUrlEnum.URL5150
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        mes1 = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        cargarListaMes1();
        mes1 = null;
    }

    public void oprimirPreliminar() {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel() {
        genInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    private void genInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put("periodo", periodo);
            reemplazar.put("proceso", procesoNomina);
            
            String reporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
			        "FORMATO LISTADO DE PARAFISCALES", modulo,
			        new Date(),true),
			        "000146ListadoParafiscalesSTR"); //LISTADO_PARAFISCALES_STR
            
            String strSql = Reporteador.resuelveConsulta(
                            reporte, Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
            
            String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE NOMINA", modulo, new Date(), false);
            String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);
            
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    @Override
    public void abrirFormulario() {
        proceso = "0";
        ano1 = anio;
        cargarListaMes1();
        mes1 = mes;
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

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCompania() {
        return nombreCompania;
    }

}
