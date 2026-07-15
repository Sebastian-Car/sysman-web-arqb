/*-
 * FlujoDeCajaControlador.java
 *
 * 1.0
 *
 * 23/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.FlujoDeCajaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

/**
 * Clase que permite generar el estado financiero del flujo de caja.
 *
 * @version 1, 23/03/2017 15:45:16 -- Modificado por jrodriguezr
 * @author jrodriguezr
 */
@ManagedBean
@ViewScoped
public class FlujoDeCajaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente a mes Trabajo
     */
    private String mesTrabajo;
    /**
     * Valor del atributo correspondiente a mes a comparar
     */
    private String mesComparar;
    /**
     * Valor del atributo correspondiente a anio trabajo
     */
    private String anioTrabajo;
    /**
     * Valor del atributo correspondiente a anio a comparar
     */
    private String anioComparar;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos plantillaExcel y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoplantillaExcel;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de objetos pertenecientes al combo mes trabajo
     */
    private List<Registro> listaMesTrabajo;
    /**
     * Lista de objetos pertenecientes al combo mes a comparar
     */
    private List<Registro> listaMesComparar;
    /**
     * Lista de objetos pertenecientes al combo anio trabajo
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Lista de objetos pertenecientes al combo anio a comparar
     */
    private List<Registro> listaAnoComparar;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FlujoDeCajaControlador
     */
    public FlujoDeCajaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1373
            numFormulario = GeneralCodigoFormaEnum.FLUJO_DE_CAJA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            contArchivoplantillaExcel = new ContenedorArchivo();
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
        anioTrabajo = String.valueOf(SysmanFunciones.ano(new Date()) - 1);
        anioComparar = String.valueOf(SysmanFunciones.ano(new Date()));
        mesTrabajo = String.valueOf(SysmanFunciones.mes(new Date()) - 1);
        mesComparar = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListaMesTrabajo();
        cargarListaMesComparar();
        cargarListaAnoTrabajo();
        cargarListaAnoComparar();
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaMesTrabajo
     *
     */
    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioTrabajo);

        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FlujoDeCajaControladorUrlEnum.URL5302
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaMesComparar
     *
     */
    public void cargarListaMesComparar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioTrabajo);

        try {
            listaMesComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FlujoDeCajaControladorUrlEnum.URL5785
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FlujoDeCajaControladorUrlEnum.URL6268
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAnoComparar
     *
     */
    public void cargarListaAnoComparar() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FlujoDeCajaControladorUrlEnum.URL6684
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
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoComparar() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable mesTrabajo
     *
     * @return mesTrabajo
     */
    public String getMesTrabajo() {
        return mesTrabajo;
    }

    /**
     * Asigna la variable mesTrabajo
     *
     * @param mesTrabajo
     * Variable a asignar en mesTrabajo
     */
    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    /**
     * Retorna la variable mesComparar
     *
     * @return mesComparar
     */
    public String getMesComparar() {
        return mesComparar;
    }

    /**
     * Asigna la variable mesComparar
     *
     * @param mesComparar
     * Variable a asignar en mesComparar
     */
    public void setMesComparar(String mesComparar) {
        this.mesComparar = mesComparar;
    }

    /**
     * Retorna la variable anioTrabajo
     *
     * @return anioTrabajo
     */
    public String getAnioTrabajo() {
        return anioTrabajo;
    }

    /**
     * Asigna la variable anioTrabajo
     *
     * @param anioTrabajo
     * Variable a asignar en anioTrabajo
     */
    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    /**
     * Retorna la variable anioComparar
     *
     * @return anioComparar
     */
    public String getAnioComparar() {
        return anioComparar;
    }

    /**
     * Asigna la variable anioComparar
     *
     * @param anioComparar
     * Variable a asignar en anioComparar
     */
    public void setAnioComparar(String anioComparar) {
        this.anioComparar = anioComparar;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoplantillaExcel
     *
     * @return contArchivoplantillaExcel
     */
    public ContenedorArchivo getContArchivoplantillaExcel() {
        return contArchivoplantillaExcel;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaMesTrabajo
     *
     * @return listaMesTrabajo
     */
    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    /**
     * Asigna la lista listaMesTrabajo
     *
     * @param listaMesTrabajo
     * Variable a asignar en listaMesTrabajo
     */
    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    /**
     * Retorna la lista listaMesComparar
     *
     * @return listaMesComparar
     */
    public List<Registro> getListaMesComparar() {
        return listaMesComparar;
    }

    /**
     * Asigna la lista listaMesComparar
     *
     * @param listaMesComparar
     * Variable a asignar en listaMesComparar
     */
    public void setListaMesComparar(List<Registro> listaMesComparar) {
        this.listaMesComparar = listaMesComparar;
    }

    /**
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaAnoComparar
     *
     * @return listaAnoComparar
     */
    public List<Registro> getListaAnoComparar() {
        return listaAnoComparar;
    }

    /**
     * Asigna la lista listaAnoComparar
     *
     * @param listaAnoComparar
     * Variable a asignar en listaAnoComparar
     */
    public void setListaAnoComparar(List<Registro> listaAnoComparar) {
        this.listaAnoComparar = listaAnoComparar;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
