/*-
 * LAuditPlanosControlador.java
 *
 * 1.0
 *
 * 30/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LAuditPlanosControladorUrlEnum;
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
 *
 * @version 1.0, 30/01/2017
 * @author jsforero
 * @version 2, 12/05/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss. Tambien los llamados a funciones,
 * procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class LAuditPlanosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // archivo que se descarga cuando se termina el proceso
    private StreamedContent archivoDescarga;

    // Fecha inicial que limita la generacion del informe
    private Date fechaInicial;
    // Fecha Final que limita la generacion del informe
    private Date fechaFinal;

    private String tipoProceso;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listacmbTipoInicial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LAuditPlanosControlador
     */
    public LAuditPlanosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.L_AUDIT_PLANOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
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
        fechaInicial = new Date();
        fechaFinal = new Date();
        cargarListacmbTipoInicial();

        abrirFormulario();

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbTipoInicial
     */
    public void cargarListacmbTipoInicial() {
        // 230001
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LAuditPlanosControladorUrlEnum.URL3965
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // Metodo que se encarga de enviar el formato y el informe a
    // generar
    public void oprimirpdf() {
        genInforme(FORMATOS.PDF, "001389LauditProceso");
    }

    public void oprimirExcel() {
        genInforme(FORMATOS.EXCEL97, "001389LauditProceso");
    }

    // Metodo que se encarga de generar el reporte
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        archivoDescarga = null;

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .formatearFecha(fechaFinal));
            reemplazar.put("tipoProceso", tipoProceso);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LAUDITPLANOS_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMS_LAUDITPLANOS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMS_LAUDITPLANOS_CMBTIPOINICIAL", tipoProceso);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // metodo que controla la seleccion de el combo tipoInicial
    public void seleccionarFilacmbTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoProceso = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    public void cambiarFechaFinal() {
        /* Evento validarFechas */
    }

    public void cambiarFechaInicial() {
        /* Evento validarFechas */
    }

    /**
     * Retorna la lista listacmbTipoInicial
     *
     * @return listacmbTipoInicial
     */
    public RegistroDataModelImpl getListacmbTipoInicial() {
        return listacmbTipoInicial;
    }

    /**
     * Asigna la lista listacmbTipoInicial
     *
     * @param listacmbTipoInicial
     * Variable a asignar en listacmbTipoInicial
     */
    public void setListacmbTipoInicial(
        RegistroDataModelImpl listacmbTipoInicial) {
        this.listacmbTipoInicial = listacmbTipoInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getTipoProceso() {
        return tipoProceso;
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

    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}
