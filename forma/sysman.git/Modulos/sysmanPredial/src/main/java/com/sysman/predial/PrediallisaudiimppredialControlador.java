/*-
 * PrediallisaudiimppredialControlador.java
 *
 * 1.0
 *
 * 16/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PrediallisaudiimppredialControladorEnum;
import com.sysman.predial.enums.PrediallisaudiimppredialControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * @version 1.0, 16/02/2017
 * @author jcrodriguez
 *
 * @version 2, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class PrediallisaudiimppredialControlador extends BeanBaseModal {
    /**
     * modulo (predial => 60) en el que se encuentra la vista
     */
    private final String modulo;
    /**
     * variable compañia
     */
    private final String compania;
    /**
     * variable que almacena el estado del check usuario
     */
    private boolean usuario;
    /**
     * variable que almacena el estado del check predio
     */
    private boolean predio;
    /**
     * variable que almacena el usaurio seleccionado
     */
    private String usuarioR;
    /**
     * variable que almacena el predio seleccionado
     */
    private String predioR;
    /**
     * atributo que guarda la fecha inicial
     */
    private Date fechaInicial;
    /**
     * atributo que guarda la fecha final
     */
    private Date fechaFinal;
    /**
     * lista de predios
     */
    private RegistroDataModelImpl listaCmbPredio;
    /**
     * lista de usuario
     */
    private RegistroDataModelImpl listaCmbUsuario;
    /**
     * variable que almacena la descarga del reporte
     */
    private StreamedContent archivoDescarga;

    public PrediallisaudiimppredialControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALLISAUDIIMPPREDIAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            fechaInicial = fechaFinal = new Date();
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

        abrirFormulario();
        cargarListaCmbPredio();
        cargarListaCmbUsuario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // herado del bean base
    }

    /**
     * Este metodo es invocado al inicio para poder visualizar un
     * listado de predios
     */
    public void cargarListaCmbPredio() {
        // 367141
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisaudiimppredialControladorUrlEnum.URL4469
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PrediallisaudiimppredialControladorEnum.FECHAINICIAL
                        .getValue(), convertirfechaDesde(fechaInicial));
        param.put(PrediallisaudiimppredialControladorEnum.FECHAFINAL.getValue(),
                        convertirfechaDesde(fechaFinal));

        listaCmbPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo que es invocado cuando se selecciona el check o cuando
     * inicia el modal para cargar la lista de usuarios
     */
    public void cargarListaCmbUsuario() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallisaudiimppredialControladorUrlEnum.URL5802
                                                        .getValue());
        listaCmbUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    private void mensajeCombobox() {
        if (usuario && SysmanFunciones.validarVariableVacio(usuarioR)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2863"));
        }
        else if (predio && SysmanFunciones.validarVariableVacio(predioR)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2862"));
        }
    }

    /**
     * Metodo invocado cuando se presiona el boton pdf
     */
    public void oprimirPdf() {
        archivoDescarga = null;
        if (!validarVacios()) {
            generaInforme(ReportesBean.FORMATOS.PDF);
        }
        else {
            mensajeCombobox();
        }
    }

    /**
     * metodo que valida un chech y un combobox
     *
     * @return
     */
    private boolean validacionCombobox() {
        return (usuario && SysmanFunciones.validarVariableVacio(usuarioR))
            || (predio && SysmanFunciones.validarVariableVacio(predioR));
    }

    /**
     * metodo que valda los campos obligatorios
     *
     * @return
     */
    private boolean validarVacios() {
        return SysmanFunciones.validarVariableVacio(calcularFecha(fechaInicial))
            || SysmanFunciones.validarVariableVacio(calcularFecha(fechaFinal))
            || validacionCombobox();
    }

    /**
     * metodo que formatea la fecha
     *
     * @param fecha
     * @return
     */
    public String calcularFecha(Date fecha) {
        return SysmanFunciones.formatearFecha(fecha);
    }

    private String convertirfechaDesde(Date fecha) {
        try {
            return SysmanFunciones.convertirAFechaCadena(fecha);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * Metodo invocado cuando se presiona en el boton pdf y excel
     *
     * @param formato
     */

    private void generaInforme(FORMATOS formato) {
        try {
            String informe = "001414PREDIALLISAUDIIMPPREDIAL";

            StringBuilder condicion = new StringBuilder();

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            if (usuario && !"".equals(usuarioR)) {
                condicion.append(" UPPER(IP_AUDITORIA.MODOPE)= '")
                                .append(usuarioR.toUpperCase())
                                .append("' AND ");
            }
            if (predio && !"".equals(predioR)) {
                condicion.append(
                                " IP_AUDITORIA.MODCOD= '").append(predioR)
                                .append("' AND ");
            }

            condicion.append(" TRUNC(IP_AUDITORIA.MODFEC) BETWEEN TO_DATE('")
                            .append(convertirfechaDesde(fechaInicial))
                            .append("','DD/MM/YYYY') AND TO_DATE('")
                            .append(convertirfechaDesde(fechaFinal)
                                + "','DD/MM/YYYY') ");
            reemplazar.put("condicion", condicion.toString());

            parametros.put("PR_FECHAINI", calcularFecha(fechaInicial));
            parametros.put("PR_FECHAFIN", calcularFecha(fechaFinal));
            parametros.put("PR_FECHAC", SysmanFunciones.concatenar("DESDE ",
                            convertirfechaDesde(fechaInicial), " y ",
                            convertirfechaDesde(fechaFinal)));

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo invocado cuando se presiona el boton de excel
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        if (!validarVacios()) {
            generaInforme(ReportesBean.FORMATOS.EXCEL);
        }
        else {
            mensajeCombobox();
        }
    }

    /**
     * metodo invocado cuando se presiona el check usuario
     */
    public void cambiarChkUsuario() {
        if (usuario) {
            predio = false;
            predioR = "";
            cargarListaCmbUsuario();
        }
    }

    /**
     * metodo invocado cuando se presiona el check predio
     */
    public void cambiarChkPredio() {
        if (predio) {
            usuario = false;
            usuarioR = "";
            cargarListaCmbPredio();
        }
    }

    /**
     * metodo invocado cuando se selecciona un predio
     *
     * @param event
     */
    public void seleccionarFilaCmbPredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioR = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
    }

    /**
     * metodo invocado cuando se cambia la fecha inicial
     */
    public void cambiarfechaIni() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        if (predio) {
            cargarListaCmbPredio();
        }
    }

    /**
     * metodo invocado cuando se cambia la fecha final
     */
    public void cambiarfechaFin() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        if (predio) {
            cargarListaCmbPredio();
        }
    }

    /**
     * metodo invocado cuando se selecciona un usuario
     *
     * @param event
     */
    public void seleccionarFilaCmbUsuario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        usuarioR = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * Retorna la variable usuarioR
     *
     * @return usuarioR
     */
    public String getUsuarioR() {
        return usuarioR;
    }

    /**
     * Asigna la variable usuarioR
     *
     * @param usuarioR
     * Variable a asignar en usuarioR
     */
    public void setUsuarioR(String usuarioR) {
        this.usuarioR = usuarioR;
    }

    /**
     * Retorna la variable predioR
     *
     * @return predioR
     */
    public String getPredioR() {
        return predioR;
    }

    /**
     * Asigna la variable predioR
     *
     * @param predioR
     * Variable a asignar en predioR
     */
    public void setPredioR(String predioR) {
        this.predioR = predioR;
    }
    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */

    /**
     * Retorna la lista listaCmbUsuario
     *
     * @return listaCmbUsuario
     */
    public RegistroDataModelImpl getListaCmbUsuario() {
        return listaCmbUsuario;
    }

    public RegistroDataModelImpl getListaCmbPredio() {
        return listaCmbPredio;
    }

    public void setListaCmbPredio(RegistroDataModelImpl listaCmbPredio) {
        this.listaCmbPredio = listaCmbPredio;
    }

    /**
     * Asigna la lista listaCmbUsuario
     *
     * @param listaCmbUsuario
     * Variable a asignar en listaCmbUsuario
     */
    public void setListaCmbUsuario(RegistroDataModelImpl listaCmbUsuario) {
        this.listaCmbUsuario = listaCmbUsuario;
    }

    public boolean isUsuario() {
        return usuario;
    }

    public void setUsuario(boolean usuario) {
        this.usuario = usuario;
    }

    public boolean isPredio() {
        return predio;
    }

    public void setPredio(boolean predio) {
        this.predio = predio;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCompania() {
        return compania;
    }

}
