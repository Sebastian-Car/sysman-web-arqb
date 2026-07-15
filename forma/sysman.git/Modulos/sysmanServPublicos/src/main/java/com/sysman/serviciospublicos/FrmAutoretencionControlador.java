/*-
 * FrmAutoretencionControlador.java
 *
 * 1.0
 *
 * 10/11/2016
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrmAutoretencionControladorEnum;
import com.sysman.serviciospublicos.enums.FrmAutoretencionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador y gestor de eventos de la forma frmautoretencion.
 *
 * @version 1.0, 10/11/2016
 * @author Pablo Espitia
 *
 * -- Modificado por lcortes 24/05/2017. Refactorizacion de codigo de
 * la lista ciclo para utilizar dss. Reemplazo de llamados a la clase
 * Acciones.
 */
@ManagedBean
@ViewScoped
public class FrmAutoretencionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que contiene el valor asignado en el formulario al
     * combo ciclo.
     */
    private String ciclo;

    /**
     * Atributo que contiene el valor ingresado al campo retencion en
     * el formulario
     */
    private String retencion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /** Lista que contiene los items del combo ciclo. */
    private List<Registro> listaCiclo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmAutoretencionControlador
     */
    public FrmAutoretencionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_AUTORETENCION_CONTROLADOR.getCodigo();
            validarPermisos();
            retencion = "2.50";
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
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la los items del combo ciclo. */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAutoretencionControladorUrlEnum.URL4967
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Pdf en la vista, encargado
     * de gestionar los eventos del mismo.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista,
     * encargado de gestionar los eventos del mismo.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     *
     * @param formato
     * Extension o tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {

        String reporte = "001238LAutoretenciones";
        try {     

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();        

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("condicionCiclo", filtrarCiclo());
            reemplazar.put("condicionUso", filtrarUsos());
            reemplazar.put("retencion", retencion);

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_CICLO", "T".equals(ciclo) ? "TODOS" : ciclo);
            parametros.put("PR_RETENCION", retencion);
            parametros.put("PR_PERIODO", recuperarPeriodo());

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }

        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(FrmAutoretencionControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            Logger.getLogger(
                            FrmAutoretencionControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e) {   
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Recupera el nombre del periodo del ciclo seleccionado en el
     * formulario.
     *
     * @return El nombre del periodo.
     */
    public String recuperarPeriodo() {
        String periodo = null;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmAutoretencionControladorEnum.PARAM0.getValue(), ciclo);
        try {
            /* Consulta para recuperar el periodo del ciclo. */
            /* Recupera el registro que contiene el periodo. */
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAutoretencionControladorUrlEnum.URL4283
                                                            .getValue())
                                            .getUrl(), param));

            /* Nombre del periodo. */
            periodo = registro == null ? null
                : registro.getCampos().get("NOMBREPERIODO").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return periodo == null ? "TODOS" : periodo;

    }

    /**
     * Realiza la condicion para filtrar por usos.
     *
     * @return La condicion para filtrar por usos.
     */
    public String filtrarUsos() {
        String value = "NO";
        try {
            value = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
                            compania, "USOS EN QUE APLICA AUTORETENCION",
                            SessionUtil.getModulo(), new Date(), true), "NO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return "NO".equals(value) ? " "
            : " AND USO IN(" + SysmanFunciones.colocarComillas(value) + ")";
    }

    /**
     * Metodo que retorna la condicion para filtrar por ciclo.
     *
     * @return La condicion para filtrar por ciclo.
     */
    public String filtrarCiclo() {
        return "T".equals(ciclo) ? " " : " AND U.CICLO = " + ciclo;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable retencion
     *
     * @return retencion
     */
    public String getRetencion() {
        return retencion;
    }

    /**
     * Asigna la variable retencion
     *
     * @param retencion
     * Variable a asignar en retencion
     */
    public void setRetencion(String retencion) {
        this.retencion = retencion;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
}
