/*-
 * FrmEvaluacionesSubDetPrincipalControlador.java
 *
 * 1.0
 * 
 * 31/01/2018
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
import com.sysman.hojasdevida.enums.FrmEvaluacionesSubDetPrincipalControladorEnum;
import com.sysman.hojasdevida.enums.FrmEvaluacionesSubDetPrincipalControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que gestiona los criterios principales de la evaluacion.
 *
 * @version 1.0, 31/01/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmEvaluacionesSubDetPrincipalControlador
                extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consCedulaEvaluado;
    private final String consCedulaEvaluador;
    private final String consEvaluacion;

    // <DECLARAR_ATRIBUTOS>

    private String evaluacion;
    private String cedulaEvaluado;
    private String cedulaEvaluador;
    private String clase;
    private String cerrar;
    private String tipo;

    private String sucursalEvaluado;
    private String sucursalEvaluador;
    private String cargoEvaluado;
    private String cargoEvaluador;
    private String escalafonEvaluado;
    private String escalafonEvaluador;
    private boolean visibleAdmin;

    private int cantidadMinima;
    private int cantidadMaxima;
    private boolean manejaCantidad;

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
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmEvaluacionesSubDetPrincipalControlador
     */
    @SuppressWarnings("unchecked")
    public FrmEvaluacionesSubDetPrincipalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        consCedulaEvaluado = "cedulaEvaluado";
        consCedulaEvaluador = "cedulaEvaluador";
        consEvaluacion = "evaluacion";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
                cerrar = (String) parametrosEntrada.get("cerrar");
                cedulaEvaluado = (String) parametrosEntrada
                                .get(consCedulaEvaluado);
                cedulaEvaluador = (String) parametrosEntrada
                                .get(consCedulaEvaluador);
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
                evaluacion = (String) parametrosEntrada.get(consEvaluacion);
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
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = FrmEvaluacionesSubDetPrincipalControladorEnum.TABLA.getValue();
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
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL15084
                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL58413
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(FrmEvaluacionesSubDetPrincipalControladorEnum.EVALUACION
                                        .getValue(), evaluacion);
        parametrosListado
                        .put(FrmEvaluacionesSubDetPrincipalControladorEnum.CEDULA_EVALUADO
                                        .getValue(), cedulaEvaluado);
        parametrosListado
                        .put(FrmEvaluacionesSubDetPrincipalControladorEnum.CEDULA_EVALUADOR
                                        .getValue(), cedulaEvaluador);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);
        parametrosListado.put(FrmEvaluacionesSubDetPrincipalControladorEnum.TIPO
                        .getValue(), tipo);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton cmdAceptar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimircmdAceptar(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>

        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        parametros.put("cerrar", cerrar);
        parametros.put(consCedulaEvaluado, cedulaEvaluado);
        parametros.put(consCedulaEvaluador, cedulaEvaluador);
        parametros.put("tipo", tipo);
        parametros.put(consEvaluacion, evaluacion);
        parametros.put("criterio",
                        reg.getCampos().get("CRITERIO_EVALUADO").toString());
        parametros.put("nombre",
                        reg.getCampos().get("NOMBRECRITERIO").toString());
        parametros.put("descripcion",
                        SysmanFunciones.nvl(reg.getCampos().get("TEXTO"), "")
                                        .toString());
        parametros.put("escomprometido", reg.getCampos().get("ESCOMPROMISO"));

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDET_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    public void oprimirAgregar()
    {
        String[] campos = { "clase", "evaluacion", "cedulaEvaluado",
                            "cedulaEvaluador", "tipo", "sucursalEvaluado",
                            "sucursalEvaluador", "cargoEvaluado",
                            "cargoEvaluador", "escalafonEvaluado",
                            "escalafonEvaluador" };
        String[] valores = { clase, evaluacion, cedulaEvaluado, cedulaEvaluador,
                             tipo, sucursalEvaluado, sucursalEvaluador,
                             cargoEvaluado, cargoEvaluador, escalafonEvaluado,
                             escalafonEvaluador };
        SessionUtil.cargarModalDatosFlash("1890", SessionUtil.getModulo(),
                        campos, valores);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Borrar en la vista
     *
     *
     */
    public void oprimirBorrar()
    {
        // <CODIGO_DESARROLLADO>
        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL275.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmEvaluacionesSubDetPrincipalControladorEnum.CEDULA_EVALUADO
                        .getValue(), cedulaEvaluado);
        try {
            requestManager.delete(urlDelete.getUrl(), param);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        
        }
        reasignarOrigen();

      
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarCompromisos(int opcion)
    {
        boolean estado = true;
        try {
            if (manejaCantidad) {
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put(consEvaluacion.toUpperCase(),
                                evaluacion);
                param.put(GeneralParameterEnum.CLASE.getName(),
                                clase);
                param.put(GeneralParameterEnum.TIPO.getName(),
                                tipo);
                param.put("CEDULA_EVALUADO",
                                cedulaEvaluado);
                param.put("CEDULA_EVALUADOR",
                                cedulaEvaluador);

                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL8574
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) >= cantidadMaxima
                    && opcion == 1) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4166")
                                    .replace("#cantidad#", String
                                                    .valueOf(cantidadMaxima)));
                    estado = false;
                }
                else if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) < cantidadMinima
                    && opcion == 2) {
                    estado = false;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA ADMINISTRACION CRITERIOS",
                            SessionUtil.getModulo(), new Date(), true))
                && "21100203".equals(SessionUtil.getMenuActual())) {
                visibleAdmin = true;
            }
            else {
                visibleAdmin = false;
            }

            manejaCantidad = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPETENCIAS",
                            SessionUtil.getModulo(), new Date(), true));

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            evaluacion);
            param.put(GeneralParameterEnum.CLASE.getName(),
                            clase);
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL4217
                                                                            .getValue())
                                            .getUrl(), param));
            cantidadMinima = Integer.parseInt(
                            reg.getCampos().get("MIN_COMPETENCIAS").toString());
            cantidadMaxima = Integer.parseInt(
                            reg.getCampos().get("MAX_COMPETENCIAS").toString());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues()
    {
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
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getLlave().put("CRITERIO",
                        registro.getLlave().get("KEY_CRITERIO_EVALUADO"));
        registro.getLlave().remove("KEY_CRITERIO_EVALUADO");
        registro.getLlave().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getLlave().put(
                        FrmEvaluacionesSubDetPrincipalControladorEnum.EVALUACION
                                        .getValue(),
                        evaluacion);
        registro.getLlave().put(
                        FrmEvaluacionesSubDetPrincipalControladorEnum.CEDULA_EVALUADO
                                        .getValue(),
                        cedulaEvaluado);
        registro.getLlave().put(
                        FrmEvaluacionesSubDetPrincipalControladorEnum.CEDULA_EVALUADOR
                                        .getValue(),
                        cedulaEvaluador);
        registro.getLlave().put(GeneralParameterEnum.CLASE.getName(), clase);
        registro.getLlave()
                        .put(FrmEvaluacionesSubDetPrincipalControladorEnum.TIPO
                                        .getValue(), tipo);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarCompromisos(2) && visibleAdmin) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB4189"));
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatos);
        parametros.put(consCedulaEvaluado, cedulaEvaluado);
        parametros.put(consCedulaEvaluador, cedulaEvaluador);
        parametros.put("tipo", tipo);
        parametros.put(consEvaluacion, evaluacion);

        if ("1".equals(cerrar)) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                            .getCodigo()));

            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        else {
            SessionUtil.redireccionar("/menu.sysman");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // METODO NO IMPLEMENTADO
    }

    public void retornarFormularioAgregar()
    {
        // METODO NO IMPLEMENTADO
    }
    // <SET_GET_ATRIBUTOS>

    public boolean isVisibleAdmin()
    {
        return visibleAdmin;
    }

    public void setVisibleAdmin(boolean visibleAdmin)
    {
        this.visibleAdmin = visibleAdmin;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
