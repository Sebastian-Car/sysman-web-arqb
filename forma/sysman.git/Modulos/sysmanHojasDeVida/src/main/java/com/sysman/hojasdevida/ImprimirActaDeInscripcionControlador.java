/*-
 * ImprimirActaDeInscripcionControlador.java
 *
 * 1.0
 * 
 * 26/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ImprimirActaDeInscripcionControladorEnum;
import com.sysman.hojasdevida.enums.ImprimirActaDeInscripcionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Clase que permite gestionar el formulario Imprimir Acta de
 * Inscripcion.
 *
 * @version 1.0, 26/01/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class ImprimirActaDeInscripcionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();
    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * desde la cual se inicio sesion.
     */
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el codigo de la convocatoria
     * seleccionado.
     */
    private String convocatoria;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Variable que recibe el código del reporte a generar.
     */
    private String reporte;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Declaracion de lista de Convocatorias.
     */
    private RegistroDataModelImpl listaCmbConvocatoria;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ImprimirActaDeInscripcionControlador
     */
    public ImprimirActaDeInscripcionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_ACTADEINSCRIPCION_CONTROLADOR
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbConvocatoria();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
         * FR1652-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbConvocatoria
     *
     */
    public void cargarListaCmbConvocatoria() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirActaDeInscripcionControladorUrlEnum.URL230
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaCmbConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        ImprimirActaDeInscripcionControladorEnum.NRO_CONVOCATORIA
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarPDF en la vista
     *
     *
     */
    public void oprimirGenerarPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarExcel en la vista
     *
     *
     */
    public void oprimirGenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(ReportesBean.FORMATOS formato) {
        // Creacion arreglos
        HashMap<String, Object> reemplazar = new HashMap<>();
        HashMap<String, Object> parametros = new HashMap<>();
        // Codigo del reporte
        reporte = ImprimirActaDeInscripcionControladorEnum.REPORTE.getValue();
        archivoDescarga = null;
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("compania", compania);
        reemplazar.put("convocatoria",
                        convocatoria);
        // </REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);
            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
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
     * listaCmbConvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        convocatoria = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(ImprimirActaDeInscripcionControladorEnum.NRO_CONVOCATORIA
                                                        .getValue()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable convocatoria
     * 
     * @return convocatoria
     */
    public String getConvocatoria() {
        return convocatoria;
    }

    /**
     * Asigna la variable convocatoria
     * 
     * @param convocatoria
     * Variable a asignar en convocatoria
     */
    public void setConvocatoria(String convocatoria) {
        this.convocatoria = convocatoria;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the reporte
     */
    public String getReporte() {
        return reporte;
    }

    /**
     * @param reporte
     * the reporte to set
     */
    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the listaCmbConvocatoria
     */
    public RegistroDataModelImpl getListaCmbConvocatoria() {
        return listaCmbConvocatoria;
    }

    /**
     * @param listaCmbConvocatoria
     * the listaCmbConvocatoria to set
     */
    public void setListaCmbConvocatoria(
        RegistroDataModelImpl listaCmbConvocatoria) {
        this.listaCmbConvocatoria = listaCmbConvocatoria;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @return the modulo
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * @return the nombreCompania
     */
    public String getNombreCompania() {
        return nombreCompania;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
