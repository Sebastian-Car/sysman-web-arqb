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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CesantiasPeriodoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr-modificado depuracion con sonar jcrodriguez
 * @version 1, 08/09/2015
 * 
 * @version 1.1, 24/03/2017, pespitia <br>
 * Se pasa la consulta del reporte a una consulta del esquema
 * SYSMANIRIS.
 * 
 * @version 2, 23/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class CesantiasPeriodoControlador extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo que
     * esta siendo usado por el usuario
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * con la que esta interactuando el usuario
     */
    private final String nombreCompania;

    private String proceso;
    private String anioUno;
    private String mesUno;
    private String periodoUno;
    private String anioDos;
    private String mesDos;
    private String periodoDos;
    private List<Registro> listaProceso;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of CesantiasPeriodoControlador
     */
    public CesantiasPeriodoControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();

        try {
            numFormulario = GeneralCodigoFormaEnum.CESANTIAS_PERIODO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CesantiasPeriodoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        anioUno = (String) SessionUtil.getSessionVar("anioNomina");
        anioDos = (String) SessionUtil.getSessionVar("anioNomina");
        mesUno = (String) SessionUtil.getSessionVar("mesNomina");
        mesDos = (String) SessionUtil.getSessionVar("mesNomina");
        periodoUno = (String) SessionUtil.getSessionVar("periodoNomina");
        periodoDos = (String) SessionUtil.getSessionVar("periodoNomina");
        cargarListaProceso();
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();
    }

    public void cargarListaProceso() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL3926
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno1() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL4644
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),anioUno);
            
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL5201
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
            param.put(GeneralParameterEnum.ANO.getName(),anioUno);
            param.put(GeneralParameterEnum.MES.getName(),mesUno);
            
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL3978
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
            param.put(GeneralParameterEnum.ANO.getName(),anioUno);
            
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL6814
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),anioDos);
            
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL5201
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
            param.put(GeneralParameterEnum.ANO.getName(),anioDos);
            param.put(GeneralParameterEnum.MES.getName(),mesDos);
            
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CesantiasPeriodoControladorUrlEnum.URL3978
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPresentar() {
        archivoDescarga=null;
        if (validarRango()) {
            generarReporte(FORMATOS.PDF);
        }
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga=null;
        if (validarRango()) {
            generarReporte(FORMATOS.EXCEL97);
        }
    }

    private boolean validarRango() {
        String perInicial = "" + anioUno + "" + concatenar(mesUno) + "" + concatenar(periodoUno) + "";
        String perFinal = "" + anioDos + "" + concatenar(mesDos) + "" + concatenar(periodoDos) + "";

        if (perInicial.compareTo(perFinal) > 0) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2509"));
            return false;
        }
        return true;
    }
    
    public String concatenar(String var){
        return var.length()==1?"0"+var:var;
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     * @author pespitia
     */
    private void generarReporte(FORMATOS formato) {

        try {
            
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = "000215ListadoCesantias";

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("idProceso", proceso);
            reemplazar.put("anioInicial", anioUno);
            reemplazar.put("anioFinal", anioDos);
            reemplazar.put("mesInicial", mesUno);
            reemplazar.put("mesFinal", mesDos);
            reemplazar.put("perInicial", periodoUno);
            reemplazar.put("perFinal", periodoDos);

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mesUno = null;
        periodoUno = null;
        cargarListaMes1();
        cargarListaAno2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodoUno = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodoDos = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        mesDos = null;
        periodoDos = null;
        cargarListaMes2();
        // </CODIGO_DESARROLLADO>
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getAnioUno() {
        return anioUno;
    }

    public void setAnioUno(String anioUno) {
        this.anioUno = anioUno;
    }

    public String getMesUno() {
        return mesUno;
    }

    public void setMesUno(String mesUno) {
        this.mesUno = mesUno;
    }

    public String getPeriodoUno() {
        return periodoUno;
    }

    public void setPeriodoUno(String periodoUno) {
        this.periodoUno = periodoUno;
    }

    public String getAnioDos() {
        return anioDos;
    }

    public void setAnioDos(String anioDos) {
        this.anioDos = anioDos;
    }

    public String getMesDos() {
        return mesDos;
    }

    public void setMesDos(String mesDos) {
        this.mesDos = mesDos;
    }

    public String getPeriodoDos() {
        return periodoDos;
    }

    public void setPeriodoDos(String periodoDos) {
        this.periodoDos = periodoDos;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
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

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
