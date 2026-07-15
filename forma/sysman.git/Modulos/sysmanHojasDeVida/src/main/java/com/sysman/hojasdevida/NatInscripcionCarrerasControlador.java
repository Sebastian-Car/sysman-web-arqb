/*-
 * NatInscripcionCarrerasControlador.java
 *
 * 1.0
 * 
 * 5/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.NatInscripcionCarrerasControladorEnum;
import com.sysman.hojasdevida.enums.NatInscripcionCarrerasControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * Formulario que muestra el proceso de prueba del personal
 *
 * @version 1.0, 05/02/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class NatInscripcionCarrerasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el modulo en el que
     * ingreso el usuario
     */

    private final String modulo;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que indica que boton se pulso para redireccionar el
     * formulario de nombramiento o inscripcion
     */

    private String opcionBoton;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NatInscripcionCarrerasControlador
     */
    public NatInscripcionCarrerasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_INSCRIPCIONES_CARRERAS_CONTROLADOR
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
        tabla = GenericUrlEnum.NAT_DATOS_PERSONALES.getTable();
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

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatInscripcionCarrerasControladorUrlEnum.URL9999
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Nombramiento
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirNombramiento(Registro reg, int indice) {

        opcionBoton = "1";

        Map<String, Object> param = new TreeMap<>();

        param.put(NatInscripcionCarrerasControladorEnum.PARAM0.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName()));

        param.put(NatInscripcionCarrerasControladorEnum.PARAM1.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        param.put("opcionBoton", opcionBoton);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_SUBNOMBRAMIENTO_CAR_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);

    }

    /**
     * Metodo ejecutado al oprimir el boton IngresoEvaluacion
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirIngresoEvaluacion(Registro reg, int indice) {

        Map<String, Object> param = new TreeMap<>();

        param.put(NatInscripcionCarrerasControladorEnum.PARAM0.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName()));

        param.put(NatInscripcionCarrerasControladorEnum.PARAM1.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_SUB_EVALUACION_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    /**
     * Metodo ejecutado al oprimir el boton InscripcionCarra
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirInscripcionCarrera(Registro reg, int indice) {
        opcionBoton = "2";

        Map<String, Object> param = new TreeMap<>();

        param.put(NatInscripcionCarrerasControladorEnum.PARAM0.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName()));

        param.put(NatInscripcionCarrerasControladorEnum.PARAM1.getValue(),
                        reg.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        param.put("opcionBoton", opcionBoton);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_SUBNOMBRAMIENTO_CAR_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
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
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
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
