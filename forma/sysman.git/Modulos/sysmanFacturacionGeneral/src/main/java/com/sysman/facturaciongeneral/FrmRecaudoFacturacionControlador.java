/*-
 * FrmRecaudoFacturacionControlador.java
 *
 * 1.0
 * 
 * 14/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmRecaudoFacturacionControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmRecaudoFacturacionControladorUrlEnum;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
 * Se realiza migración de controlador
 *
 * @version 1.0, 14/11/2017
 * @author asana
 */
@ManagedBean
@ViewScoped
public class FrmRecaudoFacturacionControlador extends BeanBaseModal {
    private final String compania;
    private RegistroDataModelImpl listabancoInicial;
    private RegistroDataModelImpl listabancoFinal;
    private List<Registro> listaconcepInicial;
    private List<Registro> listaconcepFinal;
    private RegistroDataModelImpl listanitInicial;
    private RegistroDataModelImpl listanitFinal;
    private String tipoCobro;
    private String ano;
    private String nitInicial;
    private String nitFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String bancoInicial;
    private String bancoFinal;
    private String concepInicial;
    private String concepFinal;
    private StreamedContent archivoDescarga;
    private String nombreN;
    private String bancoN;

    /**
     * Crea una nueva instancia de FrmRecaudoFacturacionControlador
     */
    public FrmRecaudoFacturacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        fechaInicial = new Date();
        fechaFinal = new Date();
        nombreN = "NOMBRE";
        bancoN = "BANCO_PAGO";
        nitInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        nitFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        bancoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        bancoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_RECAUDO_FACTURACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        cargarListanitInicial();
        cargarListanitFinal();
        cargarListabancoInicial();
        cargarListabancoFinal();
        cargarListaconcepInicial();
        cargarListaconcepFinal();
    }

    public void cargarListabancoInicial() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRecaudoFacturacionControladorUrlEnum.URL0001
                                                        .getValue());

        listabancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListabancoFinal() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("CODIGOINICIAL", bancoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRecaudoFacturacionControladorUrlEnum.URL0002
                                                        .getValue());

        listabancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaconcepInicial() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmRecaudoFacturacionControladorEnum.PARAM3.getValue(), ano);
        param.put(FrmRecaudoFacturacionControladorEnum.PARAM1.getValue(),
                        tipoCobro);

        try {
            listaconcepInicial = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmRecaudoFacturacionControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        if (listaconcepInicial == null) {
            listaconcepInicial = new ArrayList<>();
        }

        Registro reg = new Registro();
        reg.getCampos().put("NOMBRE", SysmanConstantes.DEFECTOINICIAL_STRING);
        reg.getCampos().put("CODIGO", SysmanConstantes.DEFECTOINICIAL_STRING);
        
        listaconcepInicial.add(reg);
        concepInicial = SysmanConstantes.DEFECTOINICIAL_STRING;

    }

    public void cargarListaconcepFinal() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmRecaudoFacturacionControladorEnum.PARAM3.getValue(), ano);
        param.put(FrmRecaudoFacturacionControladorEnum.PARAM1.getValue(),
                        tipoCobro);

        try {
            listaconcepFinal = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmRecaudoFacturacionControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        if (listaconcepFinal == null) {
        	listaconcepFinal = new ArrayList<>();
        }

        Registro reg = new Registro();
        reg.getCampos().put("NOMBRE", SysmanConstantes.DEFECTOFINAL_STRING);
        reg.getCampos().put("CODIGO", SysmanConstantes.DEFECTOFINAL_STRING);
        
        listaconcepFinal.add(reg);
        concepFinal = SysmanConstantes.DEFECTOFINAL_STRING;

    }

    public void cargarListanitInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRecaudoFacturacionControladorUrlEnum.URL0005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listanitInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListanitFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRecaudoFacturacionControladorUrlEnum.URL0006
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmRecaudoFacturacionControladorEnum.PARAM6.getValue(),
                        nitInicial);

        listanitFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_TIPOCOBRO", tipoCobro);
            parametros.put("PR_FECHAEXPEDICION",
                            SysmanFunciones.convertirAFechaCadena(new Date()));

            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("tipocobro", tipoCobro);
            reemplazos.put("fechainicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechafinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("compania", compania);
            reemplazos.put("terceroinicial", nitInicial);
            reemplazos.put("tercerofinal", nitFinal);
            reemplazos.put("conceptoinicial", concepInicial);
            reemplazos.put("conceptofinal", concepFinal);
            reemplazos.put("bancoinicial", bancoInicial);
            reemplazos.put("bancofinal", bancoFinal);

            Reporteador.resuelveConsulta("001504INFLISRECSTD01",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001504INFLISRECSTD01",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }

        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            Constantes.MSM_INFORME_NO_EXISTE, " ",
                            e.getMessage()));
            Logger.getLogger(FrmRecaudoFacturacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }

        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilabancoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListabancoFinal();
    }

    public void seleccionarFilabancoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListabancoFinal();
    }

    public void seleccionarFilanitInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitInicial = registroAux.getCampos().get("NIT").toString();
        terceroInicial = registroAux.getCampos().get(nombreN).toString();
        cargarListanitFinal();
    }

    public void seleccionarFilanitFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitFinal = registroAux.getCampos().get("NIT").toString();
        terceroFinal = registroAux.getCampos().get(nombreN).toString();

    }

    public void seleccionarFilaconcepInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concepInicial = registroAux.getCampos().get(nombreN).toString();
        cargarListaconcepFinal();
    }

    public void seleccionarFilaconcepFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        concepFinal = registroAux.getCampos().get(nombreN).toString();
        cargarListanitFinal();
    }

    public void oprimircmdPrevia() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdOficial() {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListabancoInicial() {
        return listabancoInicial;
    }

    public void setListabancoInicial(RegistroDataModelImpl listabancoInicial) {
        this.listabancoInicial = listabancoInicial;
    }

    public RegistroDataModelImpl getListabancoFinal() {
        return listabancoFinal;
    }

    public void setListabancoFinal(RegistroDataModelImpl listabancoFinal) {
        this.listabancoFinal = listabancoFinal;
    }

    public List<Registro> getListaconcepInicial() {
        return listaconcepInicial;
    }

    public void setListaconcepInicial(List<Registro> listaconcepInicial) {
        this.listaconcepInicial = listaconcepInicial;
    }

    public List<Registro> getListaconcepFinal() {
        return listaconcepFinal;
    }

    public void setListaconcepFinal(List<Registro> listaconcepFinal) {
        this.listaconcepFinal = listaconcepFinal;
    }

    public RegistroDataModelImpl getListanitInicial() {
        return listanitInicial;
    }

    public void setListanitInicial(RegistroDataModelImpl listanitInicial) {
        this.listanitInicial = listanitInicial;
    }

    public RegistroDataModelImpl getListanitFinal() {
        return listanitFinal;
    }

    public void setListanitFinal(RegistroDataModelImpl listanitFinal) {
        this.listanitFinal = listanitFinal;
    }

    public String getBancoInicial() {
        return bancoInicial;
    }

    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
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

    public String getNitInicial() {
        return nitInicial;
    }

    public void setNitInicial(String nitInicial) {
        this.nitInicial = nitInicial;
    }

    public String getNitFinal() {
        return nitFinal;
    }

    public void setNitFinal(String nitFinal) {
        this.nitFinal = nitFinal;
    }

    public String getBancoFinal() {
        return bancoFinal;
    }

    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
    }

    public String getConcepInicial() {
        return concepInicial;
    }

    public void setConcepInicial(String concepInicial) {
        this.concepInicial = concepInicial;
    }

    public String getConcepFinal() {
        return concepFinal;
    }

    public void setConcepFinal(String concepFinal) {
        this.concepFinal = concepFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
