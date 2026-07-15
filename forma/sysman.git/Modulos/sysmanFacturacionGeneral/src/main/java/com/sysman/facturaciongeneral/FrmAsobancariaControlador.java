/*-
 * FrmAsobancaria.java
 *
 * 1.0
 * 
 * 16/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.ibm.icu.util.Calendar;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.facturaciongeneral.enums.FrmAsobancariaControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmAsobancariaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Se realizar migración de sde access
 *
 * @version 1.0, 16/11/2017
 * @author asana
 */
@ManagedBean
@ViewScoped
public class FrmAsobancariaControlador extends BeanBaseModal {

    private final String compania;
    private String modulo;
    private String banco;
    private String codigoBanco;
    private String ebanco;
    private String ubicacionArchivo;
    private Date efecha;
    private String txtBanco;
    private String eTxtBanco;
    private String anioCobro;
    private RegistroDataModelImpl listabanco;
    private RegistroDataModelImpl listaebanco;
    private BufferedReader brArchivo;
    private String ruta;
    private long longitudArchivo;
    private String lineaArchivo;
    private int linea;
    private String archivoErrores;
    private StreamedContent archivoDescarga;
    private boolean sistema;
    private boolean recaudo;
    private String filtroFecha;
    private String informe;
    private String reporte;
    @EJB
    private EjbFacturacionGeneralDosRemote ejbFacturacionGeneralDosRemote;

    // private E

    public FrmAsobancariaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ASOBANCARIA_CONTROLADOR
                            .getCodigo();
            anioCobro = (String) SessionUtil.getSessionVar(
                            ConstantesFacturacionGenEnum.ANIO.getValue());
            modulo = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        cargarListabanco();
        cargarListaebanco();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListabanco() {
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAsobancariaControladorEnum.PARAM0.getValue(), anioCobro);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsobancariaControladorUrlEnum.URL0001
                                                        .getValue());

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaebanco() {
        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAsobancariaControladorEnum.PARAM0.getValue(), anioCobro);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsobancariaControladorUrlEnum.URL0001
                                                        .getValue());

        listaebanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void cargarArchivolector(FileUploadEvent event) {
        InputStream is;
        ruta = event.getFile().getFileName();
        longitudArchivo = event.getFile().getSize();
        String formato = ruta.substring(ruta.length() - 4, ruta.length());
        if (!".txt".equals(formato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3813"));
        }
        try {
            is = event.getFile().getInputstream();
            InputStreamReader r = new InputStreamReader(is);
            brArchivo = new BufferedReader(r);
            ruta = event.getFile().getFileName();
            brArchivo = new BufferedReader(new InputStreamReader(
                            event.getFile().getInputstream()));

        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirSelecFile() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void validararchivo() {

        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("compania", compania);
        Calendar a = Calendar.getInstance();
        a.setTime(efecha);

        reemplazos.put("ano", a.get(Calendar.YEAR));

        Map<String, Object> parametros = new HashMap<>();
        Reporteador.resuelveConsulta("900024AlertaAsobancaria",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "900024ALERTAASOBANCARIA", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirregistrarPagos() {
        // <CODIGO_DESARROLLADO>
        linea = 0;
        validararchivo();

        if (validarVacios()) {
            StringBuilder textoArchivo = new StringBuilder();

            try {
                textoArchivo = archivoConSeparador(textoArchivo);
                String planoClob = Acciones
                                .getClobConcatenado(textoArchivo.toString());

                String ultimoCaracter = planoClob
                                .substring(planoClob.length() - 1);
                if (",".equals(ultimoCaracter)) {
                    planoClob = planoClob.substring(0, planoClob.length() - 1);
                }

                archivoErrores = ejbFacturacionGeneralDosRemote
                                .cargarArchivoAsobancaria(compania,
                                                banco,
                                                SessionUtil.getUser()
                                                                .toString(),
                                                planoClob);

                if ("-1".equals(archivoErrores)) {
                    archivoDescarga = null;

                    Map<String, Object> reemplazos = new HashMap<>();
                    reemplazos.put("s$compania$s", compania);

                    String consulta = Reporteador.resuelveConsulta(
                                    "800100ErrorPlanoAsobancaria",
                                    Integer.valueOf(SessionUtil.getModulo()),
                                    reemplazos);

                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                    consulta, ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.EXCEL);

                }
                else {
                    ejecutarejecutarAlertas();
                }
            }
            catch (JRException | IOException | SQLException
                            | DRException | SysmanException
                            | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto ejecutarAlertas en la vista
     *
     *
     */
    public void ejecutarejecutarAlertas() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo("Proceso ejecutado exitosamente..");
        // </CODIGO_DESARROLLADO>
    }

    public StringBuilder archivoConSeparador(StringBuilder textoArchivo) {

        try {
            while ((lineaArchivo = brArchivo.readLine()) != null) {
                textoArchivo.append(lineaArchivo);
                if (linea == (longitudArchivo / 162)) {
                    textoArchivo.append("@");
                }
                if (linea != (longitudArchivo / 162)) {
                    textoArchivo.append("@");
                }
                linea++;
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return textoArchivo;
    }

    public boolean validarVacios() {
        if (banco == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3815"));
            return false;
        }
        else if (brArchivo == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3814"));
            return false;
        }
        return true;
    }

    public void oprimirasobancariaInforme() {
        // <CODIGO_DESARROLLADO>
        validarCampos();
        archivoDescarga = null;
        informe = "000831SF_INFASOBANCARIALOG";
        reporte = "000831INFASOBANCARIALOG";
        getInforme(informe, reporte, codigoBanco);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirlog() {
        // <CODIGO_DESARROLLADO>
        validarCampos();
        archivoDescarga = null;
        informe = "000834SF_INFASOBANCARIADET";
        reporte = "000834INFASOBANCARIADET";
        getInforme(informe, reporte, codigoBanco);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdAsobDet() {
        // <CODIGO_DESARROLLADO>
        validarCampos();
        archivoDescarga = null;
        informe = "000833SF_INFASOBANCARIAERROR";
        reporte = "000833INFASOBANCARIAERROR";
        getInforme(informe, reporte, banco);

        // </CODIGO_DESARROLLADO>
    }

    public void validarCampos() {
        if ((ebanco == null) || (efecha == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3813"));
        }
    }

    public void cambiarrecaudo() {
        if (recaudo) {
            sistema = false;
        }
    }

    public void cambiarsistema() {
        if (sistema) {
            recaudo = false;
        }
    }

    public void getInforme(String consulta, String reporte, String banco) {
        try {

            Map<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazos.put("compania", compania);

            reemplazos.put("banco", banco);
            reemplazos.put("indicador", !recaudo ? "0" : "-1");
            reemplazos.put("fecha", SysmanFunciones.formatearFecha(efecha));

            Reporteador.resuelveConsulta(consulta, Integer.valueOf(modulo),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilabanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        banco = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        txtBanco = registroAux.getCampos().get("NOMBRE").toString();

    }

    public void seleccionarFilaebanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ebanco = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        eTxtBanco = registroAux.getCampos().get("NOMBRE").toString();
        codigoBanco = registroAux.getCampos().get("BANCO").toString();

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public String getBanco() {
        return banco;
    }

    /**
     * Asigna la variable banco
     * 
     * @param banco
     * Variable a asignar en banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Retorna la variable ubicacionArchivo
     * 
     * @return ubicacionArchivo
     */
    public String getUbicacionArchivo() {
        return ubicacionArchivo;
    }

    /**
     * Asigna la variable ubicacionArchivo
     * 
     * @param ubicacionArchivo
     * Variable a asignar en ubicacionArchivo
     */
    public void setUbicacionArchivo(String ubicacionArchivo) {
        this.ubicacionArchivo = ubicacionArchivo;
    }

    /**
     * Retorna la variable txtBanco
     * 
     * @return txtBanco
     */
    public String getTxtBanco() {
        return txtBanco;
    }

    public Date getEfecha() {
        return efecha;
    }

    public void setEfecha(Date efecha) {
        this.efecha = efecha;
    }

    /**
     * Asigna la variable txtBanco
     * 
     * @param txtBanco
     * Variable a asignar en txtBanco
     */
    public void setTxtBanco(String txtBanco) {
        this.txtBanco = txtBanco;
    }

    public String geteTxtBanco() {
        return eTxtBanco;
    }

    public void seteTxtBanco(String eTxtBanco) {
        this.eTxtBanco = eTxtBanco;
    }

    public RegistroDataModelImpl getListabanco() {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco) {
        this.listabanco = listabanco;
    }

    public RegistroDataModelImpl getListaebanco() {
        return listaebanco;
    }

    public void setListaebanco(RegistroDataModelImpl listaebanco) {
        this.listaebanco = listaebanco;
    }

    public String getEbanco() {
        return ebanco;
    }

    public void setEbanco(String ebanco) {
        this.ebanco = ebanco;
    }

    public BufferedReader getBrArchivo() {
        return brArchivo;
    }

    public void setBrArchivo(BufferedReader brArchivo) {
        this.brArchivo = brArchivo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getArchivoErrores() {
        return archivoErrores;
    }

    public void setArchivoErrores(String archivoErrores) {
        this.archivoErrores = archivoErrores;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getLineaArchivo() {
        return lineaArchivo;
    }

    public void setLineaArchivo(String lineaArchivo) {
        this.lineaArchivo = lineaArchivo;
    }

    public String getFiltroFecha() {
        return filtroFecha;
    }

    public void setFiltroFecha(String filtroFecha) {
        this.filtroFecha = filtroFecha;
    }

    public boolean isSistema() {
        return sistema;
    }

    public void setSistema(boolean sistema) {
        this.sistema = sistema;
    }

    public boolean isRecaudo() {
        return recaudo;
    }

    public void setRecaudo(boolean recaudo) {
        this.recaudo = recaudo;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listabanco
     * 
     * @return listabanco
     */

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
