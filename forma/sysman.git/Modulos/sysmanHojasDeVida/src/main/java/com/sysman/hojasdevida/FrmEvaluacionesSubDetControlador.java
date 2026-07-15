/*-
 * FrmEvaluacionesSubDetControlador.java
 *
 * 1.0
 * 
 * 06/02/2018
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
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmEvaluacionesSubDetControladorEnum;
import com.sysman.hojasdevida.enums.FrmEvaluacionesSubDetControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que gestiona los criterios secundarios de la evaluacion.
 *
 * @version 1.0, 06/02/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmEvaluacionesSubDetControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    // <DECLARAR_ATRIBUTOS>

    private String evaluacion;
    private String cedulaEvaluado;
    private String cedulaEvaluador;
    private String clase;
    private String tipo;
    private String criterio;
    private String cerrar;
    private boolean escompromiso;
    
    private String criterioPadre;
    private String nombre;
    private String descripcion;
    

    /**
     * Atributo que almacena el criterio inicial
     */
    private String criterioInicial;

    /**
     * Atributo que administra la edicion del campo puntaje si el
     * indicador escompromiso esta activo
     */
    private boolean editarPuntaje;

    private Map<String, Object> ridDatos;
    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    /**
     * Crea una nueva instancia de FrmEvaluacionesSubDetControlador
     */
    @SuppressWarnings("unchecked")
    public FrmEvaluacionesSubDetControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDET_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                cedulaEvaluado = (String) parametrosEntrada
                                .get("cedulaEvaluado");
                cerrar = (String) parametrosEntrada
                                .get("cerrar");
                cedulaEvaluador = (String) parametrosEntrada
                                .get("cedulaEvaluador");
                tipo = (String) parametrosEntrada.get("tipo");
                criterio = (String) parametrosEntrada.get("criterio");
                criterioPadre= criterio;
                nombre =  (String) parametrosEntrada.get("nombre");
                descripcion =  (String) parametrosEntrada.get("descripcion");
                evaluacion = (String) parametrosEntrada.get("evaluacion");
                clase = (String) SessionUtil.getSessionVar(
                                ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                                .getValue());

                escompromiso = !(boolean) SysmanFunciones.nvl(parametrosEntrada
                                .get("escomprometido"), false);
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
        tabla = FrmEvaluacionesSubDetControladorEnum.TABLA.getValue();
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
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmEvaluacionesSubDetControladorUrlEnum.URL15084
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesSubDetControladorUrlEnum.URL45768
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(FrmEvaluacionesSubDetControladorEnum.EVALUACION
                                        .getValue(), evaluacion);
        parametrosListado
                        .put(FrmEvaluacionesSubDetControladorEnum.CEDULA_EVALUADO
                                        .getValue(), cedulaEvaluado);
        parametrosListado
                        .put(FrmEvaluacionesSubDetControladorEnum.CEDULA_EVALUADOR
                                        .getValue(), cedulaEvaluador);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);
        parametrosListado.put(FrmEvaluacionesSubDetControladorEnum.TIPO
                        .getValue(), tipo);
        parametrosListado
                        .put(FrmEvaluacionesSubDetControladorEnum.CRITERIO
                                        .getValue(), criterio);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
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
        // METODO_NO_IMPLEMENTADO
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

        if (!validarPuntaje()) {
            return false;
        }

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("NOMBRECRITERIO");
        registro.getCampos().remove("IND_TEXTO");
        registro.getCampos().remove("EVALUACION");
        registro.getCampos().remove("CLASE_EVALUACION");
        registro.getCampos().remove("CRITERIO_EVALUADO");
        registro.getCampos().remove("CEDULA_EVALUADO");
        registro.getCampos().remove("CEDULA_EVALUADOR");
        registro.getCampos().remove("SUCURSAL_EVALUADO");
        registro.getCampos().remove("SUCURSAL_EVALUADOR");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean validarPuntaje() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), criterioInicial);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);

        int puntaje = Integer.parseInt(
                        registro.getCampos().get("PUNTAJE").toString());

        try {
            Registro regpuntaje = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEvaluacionesSubDetControladorUrlEnum.URL9999
                                                                            .getValue())
                                            .getUrl(), param));

            if (puntaje > Integer.parseInt(
                            regpuntaje.getCampos()
                                            .get("PUNTAJE")
                                            .toString())) {

                JsfUtil.agregarMensajeAlerta(
                                "Error. El criterio sobrepasa el puntaje configurado para el criteroip que es de "+regpuntaje.getCampos()
                                .get("PUNTAJE")
                                .toString());

                return false;
            }
            else if (puntaje == 0) {
                JsfUtil.agregarMensajeAlerta(
                                "Error. El criterio no puede tener puntaje cero");
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        try {

            ejbHojasDeVidaCero.actualizarPuntaje(compania,
                            new BigInteger(evaluacion),
                            Integer.parseInt(clase),
                            tipo,
                            criterio,
                            registro.getCampos().get("KEY_CRITERIO_EVALUADO")
                                            .toString(),
                            cedulaEvaluado,
                            cedulaEvaluador,
                            registro.getCampos().get("KEY_SUCURSAL_EVALUADO")
                                            .toString(),
                            registro.getCampos().get("KEY_SUCURSAL_EVALUADOR")
                                            .toString(),
                            !escompromiso,
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        parametros.put("cerrar", cerrar);
        parametros.put("cedulaEvaluado", cedulaEvaluado);
        parametros.put("cedulaEvaluador", cedulaEvaluador);
        parametros.put("tipo", tipo);
        parametros.put("evaluacion", evaluacion);
        parametros.put("criterio", criterio);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
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
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        criterioInicial = registro.getCampos().get("CRITERIO_EVALUADO")
                        .toString();

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

    public boolean isEditarPuntaje() {
        return editarPuntaje;
    }

    public void setEditarPuntaje(boolean editarPuntaje) {
        this.editarPuntaje = editarPuntaje;
    }

    public boolean isEscompromiso() {
        return escompromiso;
    }

    public void setEscompromiso(boolean escompromiso) {
        this.escompromiso = escompromiso;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getCriterioPadre() {
        return criterioPadre;
    }

    public void setCriterioPadre(String criterioPadre) {
        this.criterioPadre = criterioPadre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
