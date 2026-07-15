/*-
 * NatSubArpsControlador.java
 *
 * 1.0
 * 
 * 20/12/2017
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
import com.sysman.hojasdevida.enums.NatSubArpsControladorEnum;
import com.sysman.hojasdevida.enums.NatSubArpsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite administrar los riesgos profesionales del
 * empleado.
 *
 * @version 1.0, 18/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */

@ManagedBean
@ViewScoped
public class NatSubArpsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena la cadena FONDO DE
     * CESANTIAS SALUD
     */
    private final String consFondoCesantias;
    /**
     * Constante a nivel de clase que almacena la cadena SALUD
     */
    private final String consSalud;

    /**
     * Constante a nivel de clase que almacena la cadena NOMBRE_FONDO
     */
    private final String consNombre;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que contiene los detalles del combo de entidades. */
    private RegistroDataModelImpl listaEntidad;

    /** Lista que contiene los detalles del combo de entidades. */
    private RegistroDataModelImpl listaEntidadE;
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
     * Atributo que contiene el valor del id del empleado el cual se
     * asigna por parametro
     */
    private String idEmpleado;

    /**
     * Atributo que contiene el tipo de seguridad social
     */
    private String seguridad;

    /**
     * Atributo que contiene la cadena a asignar en el titulo del
     * formulario.
     */
    private String titulo;

    /**
     * Atributo que contiene el id de la entidad seleccionada
     */
    private String entidad;
    /**
     * Atributo que define si se visualiza o no la novedad.
     */
    private boolean cargarNovedad;

    private boolean modificar;
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

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de NatSubArpsControlador
     */
    @SuppressWarnings("unchecked")
    public NatSubArpsControlador() {
        super();
        compania = SessionUtil.getCompania();
        consFondoCesantias = "FONDO DE CESANTIAS";
        consSalud = "SALUD";
        consNombre = "NOMBRE_FONDO";
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUBARPS_CONTROLADOR
                            .getCodigo();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                documento = parametrosEntrada.get("numeroDcto").toString();
                sucursal = parametrosEntrada.get("sucursal").toString();
                idEmpleado = parametrosEntrada.get("codigo").toString();
                seguridad = parametrosEntrada.get("seguridad").toString();
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
        enumBase = GenericUrlEnum.NAT_SEGURIDAD_SOCIAL;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEntidad();
        cargarListaEntidadE();
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
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        seguridad);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        parametrosListado.put(NatSubArpsControladorEnum.PARAM0.getValue(),
                        "0".equals(seguridad) ? "RIESGOS"
                            : "1".equals(seguridad) ? consSalud
                                : consFondoCesantias);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaEntidad
     *
     */
    public void cargarListaEntidad() {

        String servicio = "0".equals(seguridad)
            ? NatSubArpsControladorUrlEnum.URL6154.getValue()
            : "1".equals(seguridad)
                ? NatSubArpsControladorUrlEnum.URL5412.getValue()
                : NatSubArpsControladorUrlEnum.URL7851.getValue();
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(servicio);
        listaEntidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        NatSubArpsControladorEnum.FONDO.getValue());
    }

    /**
     * 
     * Carga la lista listaEntidad
     *
     */
    public void cargarListaEntidadE() {
        String servicio = "0".equals(seguridad)
            ? NatSubArpsControladorUrlEnum.URL6154.getValue()
            : "1".equals(seguridad)
                ? NatSubArpsControladorUrlEnum.URL5412.getValue()
                : NatSubArpsControladorUrlEnum.URL7851.getValue();
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(servicio);
        listaEntidadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        NatSubArpsControladorEnum.FONDO.getValue());
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
     * listaEntidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEntidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consNombre,
                        registroAux.getCampos()
                                        .get(consNombre));
        entidad = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(NatSubArpsControladorEnum.FONDO
                                        .getValue()),
                        "").toString();
        registro.getCampos().put("SS_ENTIDAD", entidad);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEntidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEntidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                        consNombre), "").toString();
        entidad = registroAux.getCampos().get(NatSubArpsControladorEnum.FONDO
                        .getValue()).toString();
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
        cargarNovedad = "1".equals(seguridad) || "2".equals(seguridad);
        titulo = "0".equals(seguridad) ? "RIESGOS PROFESIONALES"
            : "1".equals(seguridad) ? consSalud : consFondoCesantias;
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        documento);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idEmpleado);
        registro.getCampos().put("SS_REGIMEN", "0".equals(seguridad)
            ? "RIESGOS"
            : "1".equals(seguridad)
                ? consSalud
                : consFondoCesantias);

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
        if (modificar) {
            registro.getCampos().put("SS_ENTIDAD", entidad);
            modificar = false;
        }
        registro.getCampos().remove(consNombre);
        Date fechaVin = (Date) registro.getCampos().get("SS_FECHVINC");
        Date fechaRad = (Date) registro.getCampos().get("SS_FECHARADICACION");
        if (fechaVin != null && fechaRad != null && fechaRad.before(fechaVin)) {
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
     * 
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
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(consNombre);
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
    /**
     * Retorna la lista listaEntidad
     * 
     * @return listaEntidad
     */
    public RegistroDataModelImpl getListaEntidad() {
        return listaEntidad;
    }

    /**
     * Asigna la lista listaEntidad
     * 
     * @param listaEntidad
     * Variable a asignar en listaEntidad
     */
    public void setListaEntidad(RegistroDataModelImpl listaEntidad) {
        this.listaEntidad = listaEntidad;
    }

    /**
     * Retorna la lista listaEntidad
     * 
     * @return listaEntidad
     */
    public RegistroDataModelImpl getListaEntidadE() {
        return listaEntidadE;
    }

    /**
     * Asigna la lista listaEntidad
     * 
     * @param listaEntidad
     * Variable a asignar en listaEntidad
     */
    public void setListaEntidadE(RegistroDataModelImpl listaEntidadE) {
        this.listaEntidadE = listaEntidadE;
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

    public boolean isCargarNovedad() {
        return cargarNovedad;
    }

    public void setCargarNovedad(boolean cargarNovedad) {
        this.cargarNovedad = cargarNovedad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
