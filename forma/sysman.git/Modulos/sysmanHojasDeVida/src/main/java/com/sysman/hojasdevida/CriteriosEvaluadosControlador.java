/*-
 * CriteriosEvaluadosControlador.java
 *
 * 1.0
 * 
 * 13/08/2018
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.CriteriosEvaluadosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Permite agregar competencias comportamentales a la evaluacion de
 * desempeño..
 *
 * @version 1.0, 13/08/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class CriteriosEvaluadosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>

    private Map<String, Object> parametrosEntrada;

    private String evaluacion;
    private String cedulaEvaluado;
    private String cedulaEvaluador;
    private String clase;
    private String tipo;
    private String sucursalEvaluado;
    private String sucursalEvaluador;
    private String cargoEvaluado;
    private String cargoEvaluador;
    private String escalafonEvaluado;
    private String escalafonEvaluador;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CriteriosEvaluadosControlador
     */
    public CriteriosEvaluadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = 1890;
            if (parametrosEntrada != null) {

                cedulaEvaluado = (String) parametrosEntrada
                                .get("cedulaEvaluado");
                cedulaEvaluador = (String) parametrosEntrada
                                .get("cedulaEvaluador");

                sucursalEvaluador = (String) parametrosEntrada
                                .get("sucursalEvaluador");

                sucursalEvaluado = (String) parametrosEntrada
                                .get("sucursalEvaluado");

                cargoEvaluador = (String) parametrosEntrada
                                .get("cargoEvaluador");

                cargoEvaluado = (String) parametrosEntrada
                                .get("cargoEvaluado");

                escalafonEvaluado = (String) parametrosEntrada
                                .get("escalafonEvaluado");

                escalafonEvaluador = (String) parametrosEntrada
                                .get("escalafonEvaluador");

                tipo = (String) parametrosEntrada.get("tipo");
                evaluacion = (String) parametrosEntrada.get("evaluacion");
                clase = (String) SessionUtil.getSessionVar(
                                ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                                .getValue());
            }
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
        tabla = "EV_CRITERIOS_EVALUACION";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put("EVALUACION", evaluacion);
        parametrosListado.put("CEDULA_EVALUADO", cedulaEvaluado);
        parametrosListado.put("CEDULA_EVALUADOR", cedulaEvaluador);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        CriteriosEvaluadosControladorUrlEnum.URL6229
                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado al oprimir el boton Agregar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirAgregar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put("EVALUACION", evaluacion);
            parametros.put("CARGO_EVALUADOR", cargoEvaluador);
            parametros.put("CARGO_EVALUADO", cargoEvaluado);
            parametros.put(GeneralParameterEnum.TIPO.getName(), tipo);
            parametros.put(GeneralParameterEnum.CLASE.getName(), clase);
            parametros.put("CEDULA_EVALUADO", cedulaEvaluado);
            parametros.put("CEDULA_EVALUADOR", cedulaEvaluador);
            parametros.put("SUCURSAL_EVALUADO", sucursalEvaluado);
            parametros.put("SUCURSAL_EVALUADOR", sucursalEvaluador);
            parametros.put("ESCALAFON_EVALUADOR", escalafonEvaluador);
            parametros.put("ESCALAFON_EVALUADO", escalafonEvaluado);
            parametros.put("HORA", new Date());
            parametros.put(GeneralParameterEnum.USUARIO.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put("CRITERIO",
                            reg.getCampos().get("CRITERIO_EVALUADO"));
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlInsert = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CriteriosEvaluadosControladorUrlEnum.URL5784
                                                            .getValue());

            requestManager.save(urlInsert.getUrl(), urlInsert.getMetodo(),
                            parameter);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            listaInicial.load();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        // </CODIGO_DESARROLLADO>
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cedulaEvaluado",cedulaEvaluado);
        
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                                        .getCodigo()));
        direccionador.setRuta("/frmevaluacionessubdetprincipal.sysman");
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog("CICLO");
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
