package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceVerificacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author otorres
 * @version 1, 26/04/2016
 * @modified jguerrero
 * @version 2. 06/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * @author asana
 * @version 3. Se realiza Redirección de formulario y conexion.
 */
@ManagedBean
@ViewScoped

public class BalanceVerificacionControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String mostrarCodigoInicial;
    private String mostrarCodigoFinal;
    private String arribaBotones;
    private String codigoInicial;
    private String codigoFinal;
    private String anio;
    private int mes;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    private final String codigoCons;

    /**
     * Creates a new instance of BalanceVerificacionControlador
     */
    public BalanceVerificacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_VERIFICACION_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BalanceVerificacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        arribaBotones = "130px";
        mostrarCodigoInicial = "block";
        mostrarCodigoFinal = "block";
        codigoInicial = "0";
        codigoFinal = "9999999999999999";

        mes = SysmanFunciones.mes(new Date());
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaAnoTrabajo();
        cargarListaCodigoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR662-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceVerificacionControladorUrlEnum.URL3194
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
                                        BalanceVerificacionControladorUrlEnum.URL3728
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceVerificacionControladorUrlEnum.URL4685
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGOINICIAL", codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

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
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
            
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            try {
            reemplazar.put("ano", anio);
            reemplazar.put("mesActual", mes);
            reemplazar.put("mesAnterior", mes - 1);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            parametros.put("PR_BALANCE_VERIFICACION",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase()
                                + " DE "
                                + anio);
            Reporteador.resuelveConsulta("000692BalanceVerificacion",
                            Integer.parseInt(modulo), reemplazar, parametros);
            
                archivoDescarga = JsfUtil.exportarStreamed(
                                "000692BalanceVerificacion", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
    

    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
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

    /**
     * @return the mostrarCodigoInicial
     */
    public String getMostrarCodigoInicial() {
        return mostrarCodigoInicial;
    }

    /**
     * @param mostrarCodigoInicial
     * the mostrarCodigoInicial to set
     */
    public void setMostrarCodigoInicial(String mostrarCodigoInicial) {
        this.mostrarCodigoInicial = mostrarCodigoInicial;
    }

    /**
     * @return the mostrarCodigoFinal
     */
    public String getMostrarCodigoFinal() {
        return mostrarCodigoFinal;
    }

    /**
     * @param mostrarCodigoFinal
     * the mostrarCodigoFinal to set
     */
    public void setMostrarCodigoFinal(String mostrarCodigoFinal) {
        this.mostrarCodigoFinal = mostrarCodigoFinal;
    }

    /**
     * @return the arribaBotones
     */
    public String getArribaBotones() {
        return arribaBotones;
    }

    /**
     * @param arribaBotones
     * the arribaBotones to set
     */
    public void setArribaBotones(String arribaBotones) {
        this.arribaBotones = arribaBotones;
    }
}
