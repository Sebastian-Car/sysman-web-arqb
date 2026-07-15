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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.AcumvariosControladorEnum;
import com.sysman.nomina.enums.AcumvariosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 30/11/2015
 *
 * @author spina
 * @version 2, 05/09/2017 - se refactoriza para dss y depuracion sonar
 */
@ManagedBean  
@ViewScoped
public class AcumvariosControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloNomina;
    private boolean detallado;
    private boolean ckTercero;
    private String anioUno;
    private String anioDos;
    private String mesUno;
    private String mesDos;
    private String periodoUno;
    private String periodoDos;
    private String proceso;
    private String conceptoInicial;
    private String conceptoFinal;
    private String empleadoInicial;
    private String empleadoFinal;
    private String lblConceptoInicial;
    private String lblConceptoFinal;
    private String lblEmpleadoInicial;
    private String lblempleadoFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaConceptoI;
    private RegistroDataModelImpl listaConceptoF;
    private RegistroDataModelImpl listaEmpleadoI;
    private RegistroDataModelImpl listaEmpleadoF;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;    
    
    private String headerEspecial;

    /**
     * Creates a new instance of AcumvariosControlador
     */
    public AcumvariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACUMVARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AcumvariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        proceso = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                        .toString();
        anioUno = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        anioDos = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        mesUno = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        mesDos = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        periodoUno = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        periodoDos = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        conceptoInicial = "000";
        conceptoFinal = "999";
        empleadoInicial = "00000";
        empleadoFinal = "99999";
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaConceptoI();
        cargarListaEmpleadoI();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumvariosControladorUrlEnum.URL2160
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {
        listaAno2 = listaAno1;
    }

    public void cargarListaMes1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumvariosControladorUrlEnum.URL2161
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {
        listaMes2 = listaMes1;
    }

    public void cargarListaPeriodo1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioUno);
        param.put(GeneralParameterEnum.MES.getName(), mesUno);
        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumvariosControladorUrlEnum.URL2162
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioDos);
        param.put(GeneralParameterEnum.MES.getName(), mesDos);
        try {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumvariosControladorUrlEnum.URL2162
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumvariosControladorUrlEnum.URL2163
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaConceptoI() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumvariosControladorUrlEnum.URL2164
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConceptoI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        AcumvariosControladorEnum.ID_DE_CONCEPTO.getValue());
    }

    public void cargarListaConceptoF() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumvariosControladorUrlEnum.URL2165
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), conceptoInicial);

        listaConceptoF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        AcumvariosControladorEnum.ID_DE_CONCEPTO.getValue());
    }

    public void cargarListaEmpleadoI() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumvariosControladorUrlEnum.URL2166
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleadoI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        AcumvariosControladorEnum.ID_DE_EMPLEADO.getValue());
    }

    public void cargarListaEmpleadoF() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumvariosControladorUrlEnum.URL2167
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        empleadoInicial);

        listaEmpleadoF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        AcumvariosControladorEnum.ID_DE_EMPLEADO.getValue());
    }

    private boolean validarCamposNulos(String campo1, String campo2) {
        return campo1 == null || campo2 == null;
    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (validarCamposNulos(anioUno, anioDos)) {
            retorno = false;
        }
        if (proceso == null
            || validarCamposNulos(periodoUno, periodoDos)) {
            retorno = false;
        }
        if (validarCamposNulos(empleadoInicial, empleadoFinal)) {
            retorno = false;
        }
        if (validarCamposNulos(mesUno, mesDos)) {
            retorno = false;
        }
        if (validarCamposNulos(conceptoInicial, conceptoFinal)) {
            retorno = false;
        }
        return retorno;
    }

    public void oprimircmdPdf() {
        archivoDescarga = null;
        if (validarCampos()) {
            String perinicial = anioUno
                + (mesUno.length() == 1 ? "0" + mesUno : mesUno)
                + (periodoUno.length() == 1 ? "0" + periodoUno : periodoUno);
            String perfinal = anioDos
                + (mesDos.length() == 1 ? "0" + mesDos : mesDos)
                + (periodoDos.length() == 1 ? "0" + periodoDos : periodoDos);
            if (perinicial.compareTo(perfinal) > 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2509"));
                return;
            }

            if (detallado) {
                generarReporteAcumVariosDetallado(ReportesBean.FORMATOS.PDF);
            }
            else {
                generarReporteAcumVarios(ReportesBean.FORMATOS.PDF);
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }

    }

    public void oprimircmbExcel() {
        archivoDescarga = null;
        if (validarCampos()) {
            String perinicial = anioUno
                + (mesUno.length() == 1 ? "0" + mesUno : mesUno)
                + (periodoUno.length() == 1 ? "0" + periodoUno : periodoUno);
            String perfinal = anioDos
                + (mesDos.length() == 1 ? "0" + mesDos : mesDos)
                + (periodoDos.length() == 1 ? "0" + periodoDos : periodoDos);
            if (perinicial.compareTo(perfinal) > 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2509"));
                return;
            }

            if (detallado) {
                generarReporteAcumVariosDetallado(
                                ReportesBean.FORMATOS.EXCEL97);
            }
            else {
                generarReporteAcumVarios(ReportesBean.FORMATOS.EXCEL97);
            }
        }
        else {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
        }
    }

    public void seleccionarFilaConceptoI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(AcumvariosControladorEnum.ID_DE_CONCEPTO
                                                        .getValue()),
                                        "")
                        .toString();
        lblConceptoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(AcumvariosControladorEnum.NOMBRE_CONCEPTO
                                        .getValue()),
                        "").toString();
        cargarListaConceptoF();
    }

    public void seleccionarFilaConceptoF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(AcumvariosControladorEnum.ID_DE_CONCEPTO
                                                        .getValue()),
                                        "")
                        .toString();
        lblConceptoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(AcumvariosControladorEnum.NOMBRE_CONCEPTO
                                        .getValue()),
                        "").toString();
    }

    public void seleccionarFilaEmpleadoI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(AcumvariosControladorEnum.ID_DE_EMPLEADO
                                        .getValue()),
                        "").toString();
        lblEmpleadoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(AcumvariosControladorEnum.NOMCOMPLETO
                                        .getValue()),
                        "").toString();
        cargarListaEmpleadoF();
    }

    public void seleccionarFilaEmpleadoF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(AcumvariosControladorEnum.ID_DE_EMPLEADO
                                        .getValue()),
                        "").toString();
        lblempleadoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName()),
                        "").toString();
    }

    public void cambiarAno1() {
        mesUno = null;
        periodoUno = null;
        cargarListaMes1();
    }

    public void cambiarAno2() {
        mesDos = null;
        periodoDos = null;
        cargarListaMes2();
    }

    public void cambiarMes1() {
        periodoUno = null;
        cargarListaPeriodo1();
    }

    public void cambiarMes2() {
        periodoDos = null;
        cargarListaPeriodo2();
    }

    public void generarReporteAcumVarios(ReportesBean.FORMATOS formato) {

        try {
        	
			headerEspecial = ejbSysmanUtil.consultarParametro(compania,
				        "FORMATOS ESPECIALES BUCARAMANGA", moduloNomina,
				        new Date(),
				        true);
            
            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();        	        	
        	
            String reporte = "001869AcumVariosxHoja";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proceso", proceso);
            reemplazar.put("anioUno", anioUno);
            reemplazar.put("mesUno", mesUno);
            reemplazar.put("periodoUno", periodoUno);
            reemplazar.put("anioDos", anioDos);
            reemplazar.put("mesDos", mesDos);
            reemplazar.put("periodoDos", periodoDos);
            reemplazar.put("empleadoIni", empleadoInicial);
            reemplazar.put("empleadoFin", empleadoFinal);
            reemplazar.put("conceptoIni", conceptoInicial);
            reemplazar.put("conceptoFin", conceptoFinal);

            String strSql = Reporteador.resuelveConsulta("000398AcumVarios",
                            Integer.parseInt(moduloNomina), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_PERIODO1", periodoUno);
            parametros.put("PR_MES1",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesUno)]);
            parametros.put("PR_ANO1", anioUno);
            parametros.put("PR_PERIODO2", periodoDos);
            parametros.put("PR_MES2",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesDos)]);
            parametros.put("PR_ANO2", anioDos);
            
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException |SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(AcumvariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    private void generarReporteAcumVariosDetallado(FORMATOS formato) {
        try {
            String reporte = "000399AcumVariosDetallado";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proceso", proceso);
            reemplazar.put("anioUno", anioUno);
            reemplazar.put("mesUno", mesUno);
            reemplazar.put("periodoUno", periodoUno);
            reemplazar.put("anioDos", anioDos);
            reemplazar.put("mesDos", mesDos);
            reemplazar.put("periodoDos", periodoDos);
            reemplazar.put("empleadoIni", empleadoInicial);
            reemplazar.put("empleadoFin", empleadoFinal);
            reemplazar.put("conceptoIni", conceptoInicial);
            reemplazar.put("conceptoFin", conceptoFinal);

            String strSql = Reporteador.resuelveConsulta(
                            reporte,
                            Integer.parseInt(moduloNomina), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_PERIODO1", periodoUno);
            parametros.put("PR_MES1",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesUno)]);
            parametros.put("PR_ANO1", anioUno);
            parametros.put("PR_PERIODO2", periodoDos);
            parametros.put("PR_MES2",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mesDos)]);
            parametros.put("PR_ANO2", anioDos);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(AcumvariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public boolean getDetallado() {
        return detallado;
    }

    public void setDetallado(boolean detallado) {
        this.detallado = detallado;
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

    public String getAnioUno() {
        return anioUno;
    }

    public void setAnioUno(String anioUno) {
        this.anioUno = anioUno;
    }

    public String getAnioDos() {
        return anioDos;
    }

    public void setAnioDos(String anioDos) {
        this.anioDos = anioDos;
    }

    public String getMesUno() {
        return mesUno;
    }

    public void setMesUno(String mesUno) {
        this.mesUno = mesUno;
    }

    public String getMesDos() {
        return mesDos;
    }

    public void setMesDos(String mesDos) {
        this.mesDos = mesDos;
    }

    public String getPeriodoUno() {
        return periodoUno;
    }

    public void setPeriodoUno(String periodoUno) {
        this.periodoUno = periodoUno;
    }

    public String getPeriodoDos() {
        return periodoDos;
    }

    public void setPeriodoDos(String periodoDos) {
        this.periodoDos = periodoDos;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getConceptoInicial() {
        return conceptoInicial;
    }

    public void setConceptoInicial(String conceptoInicial) {
        this.conceptoInicial = conceptoInicial;
    }

    public String getConceptoFinal() {
        return conceptoFinal;
    }

    public void setConceptoFinal(String conceptoFinal) {
        this.conceptoFinal = conceptoFinal;
    }

    public String getEmpleadoInicial() {
        return empleadoInicial;
    }

    public void setEmpleadoInicial(String empleadoInicial) {
        this.empleadoInicial = empleadoInicial;
    }

    public String getEmpleadoFinal() {
        return empleadoFinal;
    }

    public void setEmpleadoFinal(String empleadoFinal) {
        this.empleadoFinal = empleadoFinal;
    }

    public String getLblConceptoInicial() {
        return lblConceptoInicial;
    }

    public void setLblConceptoInicial(String lblConceptoInicial) {
        this.lblConceptoInicial = lblConceptoInicial;
    }

    public String getLblConceptoFinal() {
        return lblConceptoFinal;
    }

    public void setLblConceptoFinal(String lblConceptoFinal) {
        this.lblConceptoFinal = lblConceptoFinal;
    }

    public String getLblEmpleadoInicial() {
        return lblEmpleadoInicial;
    }

    public void setLblEmpleadoInicial(String lblEmpleadoInicial) {
        this.lblEmpleadoInicial = lblEmpleadoInicial;
    }

    public String getLblempleadoFinal() {
        return lblempleadoFinal;
    }

    public void setLblempleadoFinal(String lblempleadoFinal) {
        this.lblempleadoFinal = lblempleadoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public RegistroDataModelImpl getListaConceptoI() {
        return listaConceptoI;
    }

    public void setListaConceptoI(RegistroDataModelImpl listaConceptoI) {
        this.listaConceptoI = listaConceptoI;
    }

    public RegistroDataModelImpl getListaConceptoF() {
        return listaConceptoF;
    }

    public void setListaConceptoF(RegistroDataModelImpl listaConceptoF) {
        this.listaConceptoF = listaConceptoF;
    }

    public RegistroDataModelImpl getListaEmpleadoI() {
        return listaEmpleadoI;
    }

    public void setListaEmpleadoI(RegistroDataModelImpl listaEmpleadoI) {
        this.listaEmpleadoI = listaEmpleadoI;
    }

    public RegistroDataModelImpl getListaEmpleadoF() {
        return listaEmpleadoF;
    }

    public void setListaEmpleadoF(RegistroDataModelImpl listaEmpleadoF) {
        this.listaEmpleadoF = listaEmpleadoF;
    }

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}
    
}
