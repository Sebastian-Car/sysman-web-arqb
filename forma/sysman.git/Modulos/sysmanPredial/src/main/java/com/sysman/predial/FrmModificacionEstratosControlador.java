/*-
 * FrmModificacionEstratosControlador.java
 *
 * 1.0
 * 
 * 14/02/2017
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
import com.sysman.predial.enums.FrmModificacionEstratosControladorEnum;
import com.sysman.predial.enums.FrmModificacionEstratosControladorUrlEnum;
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
 *
 * @version 1.0, 14/02/2017
 * @author jsforero
 * 
 * @author asana
 * @version 2.0 13/06/2017 Se implementa enum en formulario, se ajusta
 * Conexion.
 * 
 * @modifier amonroy
 * @version 3.0, 05/07/2017 Se realiza el Proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped
public class FrmModificacionEstratosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena la palabra CODIGO, definida por el
     * numero de veces que es llamada dentro del controlador
     */
    private final String cCodigo;
    /**
     */
    private String predioInicial;
    /**
     */
    private String predioFinal;
    /**
     */
    private Date fechaInicial;
    /**
     */
    private Date fechaFinal;

    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listacmbpredioInicial;
    /**
     */
    private RegistroDataModelImpl listacmbpredioFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmModificacionEstratosControlador
     */
    public FrmModificacionEstratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_MODIFICACION_ESTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = fechaFinal = new Date();
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbpredioInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmb_predioInicial
     *
     */
    public void cargarListacmbpredioInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmModificacionEstratosControladorUrlEnum.URL4455
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listacmbpredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listacmb_predioFinal
     *
     */
    public void cargarListacmbpredioFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmModificacionEstratosControladorUrlEnum.URL5277
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmModificacionEstratosControladorEnum.MICODIGO.getValue(),
                        predioInicial);

        listacmbpredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btn_Imprimir en la vista
     *
     *
     */

    public void oprimirbtnExcel() {
        oprimirbtnImprimir(FORMATOS.EXCEL97);
    }

    public void oprimirbtnPdf() {
        oprimirbtnImprimir(FORMATOS.PDF);

    }

    public void oprimirbtnImprimir(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = "001409INFMODIFICACIONESESTRATOS";

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("predioInicial", predioInicial);
            reemplazar.put("predioFinal", predioFinal);
            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FORMS_IDENTIFICACION_CODMUNICIPIO",
                            SessionUtil.getCompaniaIngreso().getCodigoCiudad());
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmb_predioInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbpredioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        predioFinal = "";
        cargarListacmbpredioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmb_predioFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbpredioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable predioInicial
     * 
     * @return predioInicial
     */
    public String getPredioInicial() {
        return predioInicial;
    }

    /**
     * Asigna la variable predioInicial
     * 
     * @param predioInicial
     * Variable a asignar en predioInicial
     */
    public void setPredioInicial(String predioInicial) {
        this.predioInicial = predioInicial;
    }

    /**
     * Retorna la variable predioFinal
     * 
     * @return predioFinal
     */
    public String getPredioFinal() {
        return predioFinal;
    }

    /**
     * Asigna la variable predioFinal
     * 
     * @param predioFinal
     * Variable a asignar en predioFinal
     */
    public void setPredioFinal(String predioFinal) {
        this.predioFinal = predioFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmb_predioInicial
     * 
     * @return listacmb_predioInicial
     */
    public RegistroDataModelImpl getListacmbpredioInicial() {
        return listacmbpredioInicial;
    }

    /**
     * Asigna la lista listacmb_predioInicial
     * 
     * @param listacmb_predioInicial
     * Variable a asignar en listacmb_predioInicial
     */
    public void setListacmbpredioInicial(
        RegistroDataModelImpl listacmbpredioInicial) {
        this.listacmbpredioInicial = listacmbpredioInicial;
    }

    /**
     * Retorna la lista listacmb_predioFinal
     * 
     * @return listacmb_predioFinal
     */
    public RegistroDataModelImpl getListacmbpredioFinal() {
        return listacmbpredioFinal;
    }

    /**
     * Asigna la lista listacmb_predioFinal
     * 
     * @param listacmb_predioFinal
     * Variable a asignar en listacmb_predioFinal
     */
    public void setListacmbpredioFinal(
        RegistroDataModelImpl listacmbpredioFinal) {
        this.listacmbpredioFinal = listacmbpredioFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
