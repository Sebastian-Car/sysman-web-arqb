/*-
 * NatsubcajasControlador.java
 *
 * 1.0
 * 
 * 19/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.hojasdevida.enums.NatsubcajasControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar los datos de las cajas de compesacion
 * en el modulo de hojas de vida
 *
 * @version 1.0, 16/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class NatsubcajasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Variable encargada de almacenar el numero de documento el
     * usuario al que se le va a registrar la caja de compesacion
     */
    private String numeroDocu;
    /**
     * Variable encargada de almacenar la sucursal el usuario al que
     * se le va a registrar la caja de compesacion
     */
    private String sucursal;

    /**
     * Variable encargada de almacenar la caja de compensacion
     * seleccionada por el usuario en la interfaz grafica
     * 
     */
    private String cajaComp;
    /**
     * Constante encargada de almacenar el String CAJA DE COMPENSACION
     */
    private final String cajaCompCons;
    /**
     * Constante encargada de almacenar el String CAJA DE COMPENSACION
     */
    private final String nomEntidadCons;
    /**
     * Constante encargada de almacenar el String NAT_SEGURIDAD_SOCIAL
     */
    private final String nombreTablaCons;
    private String idDeEmpleado;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar los datos de las cajas de
     * compesacion registradas en el sistema
     */
    private RegistroDataModelImpl listaEntidad;
    private Map<String, Object> ridPrincial;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatsubcajasControlador
     */
    public NatsubcajasControlador() {
        super();
        nombreTablaCons = "NAT_SEGURIDAD_SOCIAL";
        compania = SessionUtil.getCompania();
        registro = new Registro();
        Map<String, Object> parametros = SessionUtil.getFlash();
        cajaCompCons = "CAJA DE COMPENSACION";
        nomEntidadCons = "NOM_ENTIDAD";

        if (parametros != null) {

            numeroDocu = parametros.get("numeroDocu").toString();
            sucursal = parametros.get("sucursal").toString();
            idDeEmpleado = parametros.get("codigoPersona").toString();
            ridPrincial = (Map<String, Object>) parametros.get("rid");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.NATSUBCAJAS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEntidad();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        tabla = nombreTablaCons;
        buscarLlave();
        asignarOrigenDatos();

        abrirFormulario();
        cargarListaEntidad();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_DP_NUMEDOCU", numeroDocu);
        parametrosListado.put("KEY_SUCURSAL", sucursal);
        parametrosListado.put("REGIMEN", cajaCompCons);

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatsubcajasControladorUrlEnum.URL001.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubcajasControladorUrlEnum.URL002
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatsubcajasControladorUrlEnum.URL003.getValue());

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        NatsubcajasControladorUrlEnum.URL006.getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubcajasControladorUrlEnum.URL007
                                                        .getValue());
    }

    // URL006 listado

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaEntidad
     *
     */
    public void cargarListaEntidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubcajasControladorUrlEnum.URL004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEntidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CAJA_COMPENSACION");

    }

    // </METODOS_CARGAR_LISTA>
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
        registro.getCampos().put(nomEntidadCons,
                        registroAux.getCampos().get("NOMBRE_CAJA"));

        cajaComp = retornarString(registroAux, "CAJA_COMPENSACION");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("DP_NUMEDOCU", numeroDocu);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idDeEmpleado);

        registro.getCampos().put("SS_REGIMEN", cajaCompCons);

        registro.getCampos().put("SS_ENTIDAD", cajaComp);

        registro.getCampos().remove(nomEntidadCons);

        registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());

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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("DP_NUMEDOCU", numeroDocu);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        registro.getCampos().put("SS_REGIMEN", cajaCompCons);

        registro.getCampos().put("SS_ENTIDAD", cajaComp);

        registro.getCampos().remove(nomEntidadCons);

        if (css != null) {
            registro.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registro.getCampos().put(
                            GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridPrincial);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

}
