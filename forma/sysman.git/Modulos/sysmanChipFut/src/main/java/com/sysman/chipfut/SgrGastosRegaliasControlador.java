/*-
 * SgrGastosRegaliasControlador.java
 *
 * 1.0
 * 
 * 31/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.SgrGastosRegaliasControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanConstantes;

import java.util.Date;
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

/**
 * Controlador de la forma sgrgastosregalias asociado al formulario
 * "SGR GASTOS REGALIAS" del modulo de ChipFUT.
 *
 * @version 1.0, 31/03/2017
 * @author yrojas
 */
@ManagedBean
@ViewScoped
public class SgrGastosRegaliasControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * la cual esta ubicada la opcion de menu seleccionada por el
     * usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String modulo;

    /**
     * Variable de control del ejb correspondiente del paquete de
     * SysmanUtil.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable de tipo boolean que determina si el check que permite
     * seleccionar meses se muestra o no, dependiendo de un parametro.
     */
    private boolean visibleMes;

    /**
     * Variable de tipo boolean del check de "En miles de pesos" de la
     * forma.
     */
    private boolean pesos;

    /**
     * Variable de tipo boolean del check de "Mensual" de la forma.
     */
    private boolean porMes;

    /**
     * Atributo asociado al valor del trimestre que se obtiene del
     * combo trimestre en el formulario.
     */
    private String trimestre;

    /**
     * Atributo asociado al valor del anio que se obtiene del combo de
     * ano en el formulario.
     */
    private String anio;

    /**
     * Atributo asociado al valor del mes que se obtiene del combo de
     * mes en el formulario.
     */
    private String mes;

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
     * Lista que contiene la informacion de los detalles del combo
     * ano.
     */
    private List<Registro> listaAnoTrabajo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de SgrGastosRegaliasControlador
     */
    public SgrGastosRegaliasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1395
            numFormulario = GeneralCodigoFormaEnum.SGR_GASTOS_REGALIAS_CONTROLADOR
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
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
        porMes = false;
        mes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[0];

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        String parComboPorMes;
        try {
            parComboPorMes = ejbSysmanUtil.consultarParametro(compania,
                            "MOSTRAR COMBO POR MES EN INFORMES CGR-FUT",
                            modulo, new Date(), true);
            visibleMes = ("SI").equals(parComboPorMes);
        }
        catch (SystemException e) {
            Logger.getLogger(SgrGastosRegaliasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnoTrabajo
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SgrGastosRegaliasControladorUrlEnum.URL6082
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
     * Metodo ejecutado al oprimir el boton Generar en la vista
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control PorMes
     */
    public void cambiarPorMes() {
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
     * Retorna la variable visibleMes
     * 
     * @return visibleMes
     */
    public boolean isVisibleMes() {
        return visibleMes;
    }

    /**
     * Asigna la variable visibleMes
     * 
     * @param visibleMes
     * Variable a asignar en visibleMes
     */
    public void setVisibleMes(boolean visibleMes) {
        this.visibleMes = visibleMes;
    }

    /**
     * Retorna la variable pesos
     * 
     * @return pesos
     */
    public boolean getPesos() {
        return pesos;
    }

    /**
     * Asigna la variable pesos
     * 
     * @param pesos
     * Variable a asignar en pesos
     */
    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    /**
     * Retorna la variable porMes
     * 
     * @return porMes
     */
    public boolean getPorMes() {
        return porMes;
    }

    /**
     * Asigna la variable porMes
     * 
     * @param porMes
     * Variable a asignar en porMes
     */
    public void setPorMes(boolean porMes) {
        this.porMes = porMes;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public String getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
