/*-
 * NatsubpensionsControlador.java
 *
 * 1.0
 * 
 * 24/01/2018
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
import com.sysman.hojasdevida.enums.NatSubArpsControladorEnum;
import com.sysman.hojasdevida.enums.NatsubpensionsControladorEnum;
import com.sysman.hojasdevida.enums.NatsubpensionsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Este controlador se necarga de gestionar el formulario
 * Natsubpension perteneciente al formulario de datos basicos de hojas
 * de vida
 *
 * @version 1.0, 19/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class NatsubpensionsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Combo grande para mostrar las entidades
     */
    private RegistroDataModelImpl listassEntidad;
    /**
     * Combo grande para modificar las entidades
     */
    private RegistroDataModelImpl listassEntidadE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Atributo que contiene el valor del documento del empleado el
     * cual se asigna por parametro
     */
    private String documento;
    /**
     * Atributo que contiene el valor de la sucursal del empleado el
     * cual se asigna por parametro
     */
    private String sucursal;
    /**
     * Atributo que contiene el valor del codigo del empleado el cual
     * se asigna por parametro
     */
    private String codigo;
    /**
     * Atributo que contiene los diversos parametros de entrada para
     * el filtro de la informacion
     */
    /**
     * Atributo que contiene los diversos parametros de entrada para
     * el filtro de la informacion
     */
    private Map<String, Object> parametrosEntrada;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    /**
     * Atributo que especifica la accion de actualizacion
     */
    private boolean modificar;

    /**
     * Atributo que contiene el id de la entidad seleccionada
     */
    private String entidad;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NatsubpensionsControlador
     */
    public NatsubpensionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUB_PENSION_CONTROLADOR
                            .getCodigo();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                documento = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                codigo = parametrosEntrada.get("codigo").toString();
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
        tabla = NatsubpensionsControladorEnum.NAT_SEGURIDAD_SOCIAL.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListassEntidad();
        cargarListassEntidadE();
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
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);
        parametrosListado.put(NatSubArpsControladorEnum.PARAM0.getValue(),
                        "PENSION");
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubpensionsControladorUrlEnum.URL100
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatsubpensionsControladorUrlEnum.URL101.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubpensionsControladorUrlEnum.URL102
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubpensionsControladorUrlEnum.URL103
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listass_entidad
     *
     */
    public void cargarListassEntidad() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubpensionsControladorUrlEnum.URL104
                                                        .getValue());

        listassEntidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        NatsubpensionsControladorEnum.ID_DEL_FONDO.getValue());
    }

    /**
     * 
     * Carga la lista listass_entidad
     *
     */
    public void cargarListassEntidadE() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubpensionsControladorUrlEnum.URL104
                                                        .getValue());

        listassEntidadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        NatsubpensionsControladorEnum.ID_DEL_FONDO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listass_entidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilassEntidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        NatsubpensionsControladorEnum.SS_ENTIDAD.getValue(),
                        registroAux.getCampos()
                                        .get(NatsubpensionsControladorEnum.ID_DEL_FONDO
                                                        .getValue()));

        registro.getCampos().put(NatsubpensionsControladorEnum.NOMBRE_DEL_FONDO
                        .getValue(),
                        SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(NatsubpensionsControladorEnum.NOMBRE_DEL_FONDO
                                                        .getValue()),
                                        "")
                                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listass_entidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilassEntidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(NatsubpensionsControladorEnum.NOMBRE_DEL_FONDO
                                                        .getValue()),
                                        "")
                        .toString();

        entidad = SysmanFunciones.nvl(registroAux.getCampos().get(
                        NatsubpensionsControladorEnum.ID_DEL_FONDO
                                        .getValue()),
                        "").toString();

        modificar = true;

    }

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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        codigo);

        registro.getCampos().put(
                        NatsubpensionsControladorEnum.SS_REGIMEN.getValue(),
                        "PENSION");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (modificar) {
            registro.getCampos().put("SS_ENTIDAD", entidad);
            modificar = false;
        }
        registro.getCampos()
                        .remove(NatsubpensionsControladorEnum.NOMBRE_DEL_FONDO
                                        .getValue());
        Date fechaVin = (Date) registro.getCampos().get(
                        NatsubpensionsControladorEnum.SS_FECHVINC
                                        .getValue());
        Date fechaRad = (Date) registro.getCampos()
                        .get(NatsubpensionsControladorEnum.SS_FECHARADICACION
                                        .getValue());

        if (fechaRad != null && fechaRad.before(fechaVin)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3878"));
            return false;
        }

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
        registro.getCampos();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", documento);
        parametros.put("sucursal", sucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());

        registro.getCampos()
                        .remove(NatsubpensionsControladorEnum.NOMBRE_DEL_FONDO
                                        .getValue());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listass_entidad
     * 
     * @return listass_entidad
     */
    public RegistroDataModelImpl getListassEntidad() {
        return listassEntidad;
    }

    /**
     * Asigna la lista listass_entidad
     * 
     * @param listass_entidad
     * Variable a asignar en listass_entidad
     */
    public void setListassEntidad(RegistroDataModelImpl listassEntidad) {
        this.listassEntidad = listassEntidad;
    }

    /**
     * Retorna la lista listass_entidad
     * 
     * @return listass_entidad
     */
    public RegistroDataModelImpl getListassEntidadE() {
        return listassEntidadE;
    }

    /**
     * Asigna la lista listass_entidad
     * 
     * @param listasEntidad
     * Variable a asignar en listass_entidad
     */
    public void setListassEntidadE(RegistroDataModelImpl listassEntidadE) {
        this.listassEntidadE = listassEntidadE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
