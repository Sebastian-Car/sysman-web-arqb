/*-
 * Frmestadocobro.java
 *
 * 1.0
 *
 * 19/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyacï¿½. All rights reserved.
 *
 * Formulario para la generacion del informe "Usuarios por estados de cobro"
 *
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmestadocobroEnum;
import com.sysman.serviciospublicos.enums.FrmestadocobroUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
 * Formulario para la generacion del informe
 * "Usuarios por estados de cobro" para un ciclo y estados
 * seleccionados
 *
 * @author jlozano
 * @version 1, 19/09/2016 17:26:09 -- Modificado por jlozano
 * @author jcrodriguez
 * @version 2, 25/05/2016 =>Depuracion del controlador,Refactoring y
 * creacion de DSS
 */
@ManagedBean
@ViewScoped
public class Frmestadocobro extends BeanBaseModal {
    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * Ciclo seleccionado para el reporte
     */
    private String ciclo;
    /**
     * variable que alamcena la lista de ciclos
     */
    private RegistroDataModelImpl listaTxtCiclo;
    /**
     * variable que almacena la lista de cobros
     */
    private RegistroDataModelImpl listaCobros;
    /**
     * variable que almacena el reporte a descargar
     */
    private StreamedContent archivoDescarga;
    /**
     * EJB
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of Frmestadocobro
     */
    public Frmestadocobro() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMESTADOCOBRO.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(Frmestadocobro.class.getName()).log(Level.SEVERE,
                            null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaTxtCiclo();
        cargarListaCobros();
        abrirFormulario();
    }

    /**
     * heredado del bean padre
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean padre
    }

    /**
     * Metodo que carga la lista de los ciclos disponibles para
     * seleccionar
     */
    public void cargarListaTxtCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadocobroUrlEnum.URL3468
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTxtCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * Metodo que carga la lista de los tipos de cobro disponibles
     * para seleccionar
     */
    public void cargarListaCobros() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestadocobroUrlEnum.URL4244
                                                        .getValue());
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCobros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmestadocobroEnum.SP_ESTADOSCOBRO
                                                            .getValue()),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que se ejecuta al presionar el boton Impresora. Invoca
     * el metodo generarReporte y envia como parametro de formato
     * ReportesBean.FORMATOS.PDF
     */
    public void oprimirImpresora() {
        archivoDescarga = null;
        if ("".equals(SysmanFunciones.nvl(ciclo.trim(), ""))) { // Valida
            // que
            // se haya
            // selecionado
            // un ciclo
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            FrmestadocobroEnum.TB_TB1626.getValue()));
            return;
        }
        if (listaCobros.getSeleccionados().isEmpty()) { // Valida que
            // se haya
            // selecionado
            // uno o mas
            // estado de
            // cobro
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            FrmestadocobroEnum.TB_TB1627.getValue()));
            return;
        }
        generarReporte(ReportesBean.FORMATOS.PDF);
    }

    /**
     * Metodo que se ejecuta al presionar el boton Excel. Invoca el
     * metodo generarReporte y envia como parametro de formato
     * ReportesBean.FORMATOS.EXCEL
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ("".equals(SysmanFunciones.nvl(ciclo.trim(), ""))) { // Valida
            // que se
            // haya
            // seleccio
            // un ciclo
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            FrmestadocobroEnum.TB_TB1626.getValue()));
            return;
        }
        if (listaCobros.getSeleccionados().isEmpty()) { // Valida que
            // se halla
            // selecionado
            // uno o mas
            // estados de
            // cobro
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            FrmestadocobroEnum.TB_TB1627.getValue()));
            return;
        }

        generarReporte(ReportesBean.FORMATOS.EXCEL);
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * Metodo que genera el reporte "Usuarios poe estados de cobro"
     * para el ciclo y estados de cobro seleccionados
     *
     * @param formato
     * Formato en el que se genera el reporte
     */
    private void generarReporte(ReportesBean.FORMATOS formato) {
        StringBuilder varCobro = new StringBuilder("");
        StringBuilder strCobro = new StringBuilder("");
        String strInforme;
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        archivoDescarga = null;

        for (Registro varitm : listaCobros.getSeleccionados()) {
            varCobro.append("'"
                + varitm.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                                .toString()
                + "',");
            strCobro.append(varitm.getCampos()
                            .get(GeneralParameterEnum.DESCRIPCION.getName())
                            .toString()
                + ", ");
        }

        String varCobro1 = varCobro.substring(0, varCobro.length() - 1);
        String strCobro1 = strCobro.substring(0, strCobro.length() - 2);

        strInforme = getParametro(idioma.getString("TB_TB3178"), false);

        if (" ".equals(SysmanFunciones.nvlStr(strInforme, " "))) {
            strInforme = FrmestadocobroEnum.INF_ESTADOCOBRO.getValue();
        }

        reemplazos.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(),
                        compania);
        reemplazos.put(GeneralParameterEnum.CICLO.getName().toLowerCase(),
                        ciclo);
        reemplazos.put(FrmestadocobroEnum.ESTADOS.getValue().toLowerCase(),
                        varCobro1);

        parametros.put(FrmestadocobroEnum.PR_CICLO.getValue(), ciclo);
        parametros.put(FrmestadocobroEnum.PR_LISTADO.getValue(), strCobro1);

        try {
            if (FrmestadocobroEnum.INF_ESTADOCOBRO.getValue()
                            .equals(strInforme)) {

                Reporteador.resuelveConsulta(
                                FrmestadocobroEnum.REPORTE001086.getValue(),
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(
                                FrmestadocobroEnum.REPORTE001086.getValue(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                formato);

            }

            if (FrmestadocobroEnum.INF_ESTADOCOBROING.getValue()
                            .equals(strInforme)) {
                Reporteador.resuelveConsulta(
                                FrmestadocobroEnum.REPORTE001087.getValue(),
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                FrmestadocobroEnum.REPORTE001087.getValue(),
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama al seleccionar un registro de un combo
     * grande
     * 
     * @param event
     */
    public void seleccionarFilaTxtCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString();
    }

    public void seleccionarFilaCobros(SelectEvent event) {
        // heredado del bean base
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaTxtCiclo() {
        return listaTxtCiclo;
    }

    public void setListaTxtCiclo(RegistroDataModelImpl listaTxtCiclo) {
        this.listaTxtCiclo = listaTxtCiclo;
    }

    public RegistroDataModelImpl getListaCobros() {
        return listaCobros;
    }

    public void setListaCobros(RegistroDataModelImpl listaCobros) {
        this.listaCobros = listaCobros;
    }
}
