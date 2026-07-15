/*-
 * ManteniminetoscuipoControlador.java
 *
 * 1.0
 * 
 * 29/06/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.ManteniminetoscuipoControladorEnumUrl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 29/06/2022
 * @author ljdiaz
 */
@ManagedBean
@ViewScoped
public class ManteniminetoscuipoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String PROCESO;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private int ano;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaano;

    @EJB
    private EjbPresupuestoTresGeneralRemote ejbPresupuestoTres;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private String modulo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ManteniminetoscuipoControlador
     */
    public ManteniminetoscuipoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.MANTENIMIENTOS_CUIPO
                            .getCodigo();
            // numFormulario = 2357;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        cargarListaano();
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
     * 
     * Carga la lista listaano
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ManteniminetoscuipoControladorEnumUrl.URL23574001
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
     * Metodo ejecutado al oprimir el boton Ejecutar en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirEjecutar() {

        String actualizaSituacionFondos;
        try {
            actualizaSituacionFondos = ejbSysmanUtil.consultarParametro(
                            compania,
                            "ACTUALIZA SSF DESDE FUENTE", modulo, new Date(),
                            true);

            if (!SysmanFunciones
                            .validarVariableVacio(actualizaSituacionFondos)) {
                // ejbPresupuestoTres
                
                    System.out.println("parametro en si : "
                        + actualizaSituacionFondos);
                    if (("1").equals(PROCESO)) {// el proceso es
                        // ACTUALIZAR
                        // SITUACION FONDOS
                        System.out.println("proceso " + PROCESO
                            + " -> ACTUALIZAR SITUACION FONDOS ");
                        ejbPresupuestoTres.actualizaSituacionFondos(compania,
                                        ano);

                        JsfUtil.agregarMensajeInformativo(
                                        idioma.getString(
                                                        "MSM_PROCESO_EJECUTADO"));
                    }
                
            }

            if (PROCESO.equals("2")) {

                ejbPresupuestoTres.actualizarFuenteDetalle(compania, ano,
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));
            }
            if (PROCESO.equals("3")) {

                ejbPresupuestoTres.actualizaClasificadorCadenaPptal(compania,
                                ano,
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));
            }
            if (PROCESO.equals("4")) {

                ejbPresupuestoTres.actualizarClasificadoresPptal(compania, ano,
                                SessionUtil.getUser().getCodigo());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));
            }
        }
        catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
     * Retorna la variable PROCESO
     * 
     * @return PROCESO
     */
    public String getPROCESO() {
        return PROCESO;
    }

    /**
     * Asigna la variable PROCESO
     * 
     * @param PROCESO
     * Variable a asignar en PROCESO
     */
    public void setPROCESO(String PROCESO) {
        this.PROCESO = PROCESO;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaano
     * 
     * @return listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaano
     * 
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
