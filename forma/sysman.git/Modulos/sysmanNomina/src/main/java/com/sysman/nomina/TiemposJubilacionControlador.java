/*-
 * TiemposJubilacionControlador.java
 *
 * 1.0
 * 
 * 10/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.TiemposJubilacionControladorEnum;
import com.sysman.nomina.enums.TiemposJubilacionControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar el informe de jubilacion del personal
 *
 * @version 1.0, 10/12/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class TiemposJubilacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor seleccionado en el cuadro de
     * Opciones del informe
     */
    private String genero;
    /**
     * Atributo que almacena el valor de la edad inicial seleccionada
     * en el formulario
     */
    private String edadInicial;
    /**
     * Atributo que almacena el valor de la edad final seleccionada en
     * el formulario
     */
    private String edadFinal;
    /**
     * Atributo que almacena el valor de la fecha seleccionada en el
     * formulario
     */
    private Date fechaCorte;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la edad
     */
    private List<Registro> listaEdadInicial;
    /**
     * Lista de registros de la edad
     */
    private List<Registro> listaEdadFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TiemposJubilacionControlador
     */
    public TiemposJubilacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2006
            numFormulario = GeneralCodigoFormaEnum.TIEMPOS_JUBILICACION_CONTROLADOR
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
        cargarListaEdadInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        fechaCorte = new Date();
        genero = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaEdadInicial
     *
     */
    public void cargarListaEdadInicial() {

        Map<String, Object> param = new TreeMap<>();

        try {
            listaEdadInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiemposJubilacionControladorUrlEnum.URL146
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaEdadFinal
     *
     */
    public void cargarListaEdadFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(TiemposJubilacionControladorEnum.EDADINICIAL.getValue(),
                        edadInicial);

        try {
            listaEdadFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiemposJubilacionControladorUrlEnum.URL182
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Metodo que se ejecuta en el llamado del evento de los botones
     * 
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {
        String informe = "001973InformeTiempoJubilacion";
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("fecha", SysmanFunciones.formatearFecha(fechaCorte));
        reemplazos.put("genero", "1".equals(genero) ? "M" : "F");
        reemplazos.put("edadInicial", edadInicial);
        reemplazos.put("edadFinal", edadFinal);
        Map<String, Object> parametro = new HashMap<>();
        try {
            parametro.put("PR_FORMS_FECHA",
                            SysmanFunciones.convertirAFechaCadena(fechaCorte));
            parametro.put("PR_GENERO",
                            "1".equals(genero) ? "Hombres" : "Mujeres");
            parametro.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametro);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe, parametro,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | SysmanException | IOException | JRException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control EdadInicial
     * 
     * 
     */
    public void cambiarEdadInicial() {
        // <CODIGO_DESARROLLADO>
        edadFinal = null;
        cargarListaEdadFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable genero
     * 
     * @return genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * Asigna la variable genero
     * 
     * @param genero
     * Variable a asignar en genero
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * Retorna la variable edadInicial
     * 
     * @return edadInicial
     */
    public String getEdadInicial() {
        return edadInicial;
    }

    /**
     * Asigna la variable edadInicial
     * 
     * @param edadInicial
     * Variable a asignar en edadInicial
     */
    public void setEdadInicial(String edadInicial) {
        this.edadInicial = edadInicial;
    }

    /**
     * Retorna la variable edadFinal
     * 
     * @return edadFinal
     */
    public String getEdadFinal() {
        return edadFinal;
    }

    /**
     * Asigna la variable edadFinal
     * 
     * @param edadFinal
     * Variable a asignar en edadFinal
     */
    public void setEdadFinal(String edadFinal) {
        this.edadFinal = edadFinal;
    }

    /**
     * Retorna la variable fechaCorte
     * 
     * @return fechaCorte
     */
    public Date getFechaCorte() {
        return fechaCorte;
    }

    /**
     * Asigna la variable fechaCorte
     * 
     * @param fechaCorte
     * Variable a asignar en fechaCorte
     */
    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaEdadInicial
     * 
     * @return listaEdadInicial
     */
    public List<Registro> getListaEdadInicial() {
        return listaEdadInicial;
    }

    /**
     * Asigna la lista listaEdadInicial
     * 
     * @param listaEdadInicial
     * Variable a asignar en listaEdadInicial
     */
    public void setListaEdadInicial(List<Registro> listaEdadInicial) {
        this.listaEdadInicial = listaEdadInicial;
    }

    /**
     * Retorna la lista listaEdadFinal
     * 
     * @return listaEdadFinal
     */
    public List<Registro> getListaEdadFinal() {
        return listaEdadFinal;
    }

    /**
     * Asigna la lista listaEdadFinal
     * 
     * @param listaEdadFinal
     * Variable a asignar en listaEdadFinal
     */
    public void setListaEdadFinal(List<Registro> listaEdadFinal) {
        this.listaEdadFinal = listaEdadFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
