package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ExtractoporterceroControladorUrlEnum;
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
 * @author acaceres
 * @version 1, 05/05/2016
 * @modified jguerrero
 * @version 2. 06/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class ExtractoporterceroControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private String terceroInicial;
    private String terceroFinal;
    private int anoTrabajo;
    private int mesTrabajo;

    private String nombreTerceroInicial;
    private String nombreTerceroFinal;
    private String nomCodigoInicial;
    private String nomCodigoFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    private final String codigoCons;
    private final String nombreCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ExtractoporterceroControlador
     */
    public ExtractoporterceroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        nombreCons = "NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.EXTRACTOPORTERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ExtractoporterceroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        anoTrabajo = SysmanFunciones
                        .ano(new Date());
        mesTrabajo = SysmanFunciones
                        .mes(new Date());
        codigoInicial = "0";
        codigoFinal = "9999999999999999";
        terceroInicial = "0";
        terceroFinal = "9999999999999999";

        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExtractoporterceroControladorUrlEnum.URL3927
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaMesTrabajo
     *
     */
    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExtractoporterceroControladorUrlEnum.URL4409
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExtractoporterceroControladorUrlEnum.URL5790
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExtractoporterceroControladorUrlEnum.URL6690
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExtractoporterceroControladorUrlEnum.URL7712
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExtractoporterceroControladorUrlEnum.URL8423
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("TERCEROINICIAL", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
            int mesAnterior = mesTrabajo - 1;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("mesAnterior", mesAnterior);
            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("anoTrabajo", anoTrabajo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            String cargoTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO TESORERO", modulo,
                            new Date(), true);

            String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesTrabajo];
            String entre = "EXTRACTO DEL MES DE " + nombreMes.toUpperCase()
                + " DE " + anoTrabajo + "";
            parametros.put("PR_CARGO_TESORERO", cargoTesorero);
            parametros.put("PR_ENTRE", entre);
            Reporteador.resuelveConsulta("000747ExtractoPorTercero",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000747ExtractoPorTercero", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        nomCodigoInicial = null;
        nomCodigoFinal = null;
        terceroInicial = null;
        terceroFinal = null;
        nombreTerceroInicial = null;
        nombreTerceroFinal = null;
        cargarListaCodigoInicial();
        cargarListaTerceroInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        nomCodigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
        cargarListaCodigoFinal();
        codigoFinal = null;
        nomCodigoFinal = null;
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        nomCodigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTerceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
        cargarListaTerceroFinal();
        terceroFinal = null;
        nombreTerceroFinal = null;
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        nombreTerceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
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

    public int getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(int anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public int getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(int mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreTerceroInicial() {
        return nombreTerceroInicial;
    }

    public void setNombreTerceroInicial(String nombreTerceroInicial) {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }

    public String getNombreTerceroFinal() {
        return nombreTerceroFinal;
    }

    public void setNombreTerceroFinal(String nombreTerceroFinal) {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaMesTrabajo
     *
     * @return listaMesTrabajo
     */
    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    /**
     * Asigna la lista listaMesTrabajo
     *
     * @param listaMesTrabajo
     * Variable a asignar en listaMesTrabajo
     */
    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
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

    public String getNomCodigoInicial() {
        return nomCodigoInicial;
    }

    public void setNomCodigoInicial(String nomCodigoInicial) {
        this.nomCodigoInicial = nomCodigoInicial;
    }

    public String getNomCodigoFinal() {
        return nomCodigoFinal;
    }

    public void setNomCodigoFinal(String nomCodigoFinal) {
        this.nomCodigoFinal = nomCodigoFinal;
    }

}
