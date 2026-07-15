/*-
 * FrmelegiblesControlador.java
 *
 * 1.0
 *
 * 30/01/2018
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
import com.sysman.hojasdevida.enums.FrmelegiblesControladorEnum;
import com.sysman.hojasdevida.enums.FrmelegiblesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
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
 * Modal encargado de generar reporte de los elegibles
 *
 * @version 1.0, 30/01/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class FrmelegiblesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacena el codigo de la convocatoria seleccionada
     */
    private String noConvocatoria;
    /**
     * Obtiene la fecha de la convocatoria seleccionada
     */
    private String fecha;
    /**
     * Obtiene el nombre del cargo de la convocatoria seleccionada
     */
    private String nombreCargo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * listado de las convocatorias de la compania
     */
    private RegistroDataModelImpl listaNroConvocatoria;

    /**
     * archivo que genera los informes
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmelegiblesControlador
     */
    public FrmelegiblesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMELEGIBLES_CONTROLADOR
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
        cargarListaNroConvocatoria();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaNroConvocatoria
     *
     */
    public void cargarListaNroConvocatoria() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmelegiblesControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaNroConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmelegiblesControladorEnum.NRO_CONVOCATORIA
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdPreview en la vista
     *
     */
    public void oprimirCmdPreview() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdPrint en la vista
     *
     */
    public void oprimirCmdPrint() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        reemplazar.put("convocatoria", noConvocatoria);
        String reporte = "001676RptImprimirElegibles";
        Reporteador.resuelveConsulta(reporte, Integer.valueOf(modulo),
                        reemplazar, parametros);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdConsultaElegibles en la
     * vista
     *
     */
    public void oprimircmdConsultaElegibles() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "convocatoria", "cargo" };
        Object[] valores = { noConvocatoria, nombreCargo };
        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRMELEGIBLESUB_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNroConvocatoria
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNroConvocatoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        noConvocatoria = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmelegiblesControladorEnum.NRO_CONVOCATORIA
                                                        .getValue()),
                                        "")
                        .toString();

        fecha = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmelegiblesControladorEnum.FECHA_CONVOCATORIA
                                                        .getValue()),
                                        "")
                        .toString();

        nombreCargo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmelegiblesControladorEnum.NOMBRE_DEL_CARGO
                                                        .getValue()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable noConvocatoria
     *
     * @return noConvocatoria
     */
    public String getNoConvocatoria() {
        return noConvocatoria;
    }

    /**
     * Asigna la variable noConvocatoria
     *
     * @param noConvocatoria
     * Variable a asignar en noConvocatoria
     */
    public void setNoConvocatoria(String noConvocatoria) {
        this.noConvocatoria = noConvocatoria;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNroConvocatoria
     *
     * @return listaNroConvocatoria
     */
    public RegistroDataModelImpl getListaNroConvocatoria() {
        return listaNroConvocatoria;
    }

    /**
     * Asigna la lista listaNroConvocatoria
     *
     * @param listaNroConvocatoria
     * Variable a asignar en listaNroConvocatoria
     */
    public void setListaNroConvocatoria(
        RegistroDataModelImpl listaNroConvocatoria) {
        this.listaNroConvocatoria = listaNroConvocatoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getModulo() {
        return modulo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
