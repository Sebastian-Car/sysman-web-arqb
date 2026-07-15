/*-
 * CompromisosAcordadosControlador.java
 *
 * 1.0
 * 
 * 11/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.CompromisosAcordadosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase que permite calificar cada uno de los compromisos
 *
 * @version 1.0, 11/07/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class CompromisosAcordadosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consCedulaEvaluado;
    private final String consClase;
    private final String consSucursalEvaluado;
    private final String consTipo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena numero evaluacion de la vista
     */
    private String numeroEvaluacion;
    /**
     * Atributo que almacena claseEvaluacion de la vista
     */
    private String claseEvaluacion;
    /**
     * Atributo que almacena tipoEvaluacion de la vista
     */
    private String tipoEvaluacion;
    /**
     * Atributo que almacena cedulaEvaluado de la vista
     */
    private String cedulaEvaluado;
    /**
     * Atributo que almacena sucursalEvaluado de la vista
     */
    private String sucursalEvaluado;
    /**
     * Atributo que almacena el periodo de la evaluacion
     */
    private String periodo;

    /**
     * Atributo que almacena el anio de la evaluacion
     */
    private String anio;

    private double pesoAnterior;

    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatos;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCalificacion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CompromisosAcordadosControlador
     */
    @SuppressWarnings("unchecked")
    public CompromisosAcordadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCedulaEvaluado = "CEDULA_EVALUADO";
        consClase = "CLASE_EVALUACION";
        consTipo = "TIPO_EVALUACION";
        consSucursalEvaluado = "SUCURSAL_EVALUADO";
        try {

            Map<String, Object> parametroEntrada = SessionUtil.getFlash();
            if (parametroEntrada != null) {
                ridDatos = (Map<String, Object>) parametroEntrada
                                .get("rid");
                numeroEvaluacion = ridDatos.get("KEY_NUMERO_EVALUACION")
                                .toString();
                claseEvaluacion = ridDatos.get("KEY_CLASE_EVALUACION")
                                .toString();
                tipoEvaluacion = ridDatos.get("KEY_TIPO_EVALUACION").toString();
                cedulaEvaluado = ridDatos.get("KEY_CEDULA_EVALUADO")
                                .toString();
                sucursalEvaluado = ridDatos.get("KEY_SUCURSAL_EVALUADO")
                                .toString();

                periodo = parametroEntrada
                                .get(GeneralParameterEnum.PERIODO.getName()
                                                .toLowerCase())
                                .toString();
                anio = parametroEntrada
                                .get(GeneralParameterEnum.ANO.getName()
                                                .toLowerCase())
                                .toString();

            }
            numFormulario = GeneralCodigoFormaEnum.COMPROMISOS_ACORDADOS_CONTROLADOR
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
        enumBase = GenericUrlEnum.EV_DETALLE_COMPACORDADOS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaCalificacion();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(consClase,
                        claseEvaluacion);
        parametrosListado.put(consTipo,
                        tipoEvaluacion);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        parametrosListado.put(consCedulaEvaluado,
                        cedulaEvaluado);

        parametrosListado.put(consSucursalEvaluado,
                        sucursalEvaluado);

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCalificacion
     *
     */
    public void cargarListaCalificacion() {
        try {
            listaCalificacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompromisosAcordadosControladorUrlEnum.URL6229
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public boolean validarPorcentaje() {
        double totalPor = 0;
        double peso = Double.parseDouble(
                        registro.getCampos().get("PESO_PORCENTUAL").toString());
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(consClase,
                            claseEvaluacion);
            param.put(consTipo,
                            tipoEvaluacion);
            param.put(consCedulaEvaluado,
                            cedulaEvaluado);
            param.put(consSucursalEvaluado,
                            sucursalEvaluado);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompromisosAcordadosControladorUrlEnum.URL1872
                                                                            .getValue())
                                            .getUrl(), param));

            totalPor = Double.parseDouble(
                            reg.getCampos().get("PESO").toString());
            totalPor = totalPor - pesoAnterior;

            if ((totalPor + peso) > 100) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4167")
                                .replace("#cantidad#", String
                                                .valueOf(100 - (totalPor))));
                return false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove("SUCURSAL_EVALUADOR");
        registro.getCampos().remove(consCedulaEvaluado);
        registro.getCampos().remove("NUMERO_EVALUACION");
        registro.getCampos().remove("META");
        registro.getCampos().remove(consSucursalEvaluado);
        registro.getCampos().remove(consClase);
        registro.getCampos().remove("COMPROMISO");
        registro.getCampos().remove("PERIODO");
        registro.getCampos().remove("CEDULA_EVALUADOR");
        registro.getCampos().remove(consTipo);
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        if (registro.getCampos().get("CALIFICACION") == null) {
            registro.getCampos().put("CALIFICACION", 0);
        }

        // </CODIGO_DESARROLLADO>
        return validarPorcentaje();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        pesoAnterior = Double.parseDouble(
                        registro.getCampos().get("PESO_PORCENTUAL").toString());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        parametros.put("evaluacion", numeroEvaluacion);
        parametros.put("tipo", tipoEvaluacion);
        parametros.put("claseEvaluacion", claseEvaluacion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }
    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCalificacion() {
        return listaCalificacion;
    }

    public void setListaCalificacion(List<Registro> listaCalificacion) {
        this.listaCalificacion = listaCalificacion;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
