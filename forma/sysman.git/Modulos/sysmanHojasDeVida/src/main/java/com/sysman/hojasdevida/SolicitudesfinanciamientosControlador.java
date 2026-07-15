/*-
 * SolicitudesfinanciamientosControlador.java
 *
 * 1.0
 *
 * 02/02/2018
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.SolicitudesfinanciamientosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Permite gestionar los registos de los formularios Solicitudes de financiamientos y Solicitudes academicas
 *
 * @version 1.0, 02/02/2018
 * @author spina
 * 
 * @version 2.0, 02/05/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped
public class SolicitudesfinanciamientosControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Consecutivo generado o ingresado por el usuario
     */
    private long consecutivo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de aspirantes
     */
    private RegistroDataModelImpl listadpnumedocu;
    /**
     * Verdadero si la solicitud es anticipada
     */
    private String soliAnticipada;

    /**
     * Se usa para los objetos tipo marco de la forma ya que requiren un origen de control
     */
    private String marco;

    /**
     * Se usa para mostrar el total de las calificaciones
     */
    private double puntajeTotal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de SolicitudesfinanciamientosControlador
     */
    public SolicitudesfinanciamientosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SOLICITUDESFINANCIAMIENTOS_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListadpnumedocu();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.NAT_SOLICITUD_FINANCIAMIENTO_A;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listadpnumedocu
     *
     */
    public void cargarListadpnumedocu() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudesfinanciamientosControladorUrlEnum.URL4130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listadpnumedocu = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    public void seleccionarFiladpnumedocu(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NOMBRES
                        .getName(), SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NOMBRES
                                                                        .getName()),
                                                        "")
                                        .toString());
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()),
                                        "").toString());
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        SysmanFunciones.nvl(
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.CODIGO
                                                                        .getName()),
                                        "").toString());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanFunciones.nvl(
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.SUCURSAL
                                                                        .getName()),
                                        "").toString());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Comando127 en la vista
     *
     *
     */
    public void oprimirComando127() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando129 en la vista
     *
     *
     */
    public void oprimirComando129() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        if (css != null) {
            parametros.put("rid", css);
            parametros.put("idempleado",
                            registro.getCampos().get("ID_DE_EMPLEADO")
                                            .toString());
            parametros.put("numeroDcto",
                            registro.getCampos().get("DP_NUMEDOCU").toString());
            parametros.put("sucursal",
                            registro.getCampos().get("SUCURSAL").toString());
            parametros.put("numero",
                            registro.getCampos().get("CONSECUTIVO").toString());
        }
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FINANEDUCACIONS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando130 en la vista
     *
     *
     */
    public void oprimirComando130() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmbtCoti en la vista
     *
     *
     */
    public void oprimirCmbtCoti() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando128 en la vista
     *
     *
     */
    public void oprimirComando128() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCalifFunSecretaria() {
        actualizarTotal();
    }

    public void cambiarCalifNotas() {
        actualizarTotal();
    }

    public void cambiarCalifAntiguedad() {
        actualizarTotal();
    }

    public void cambiarCalifFunCargo() {
        actualizarTotal();
    }

    public void actualizarTotal() {
        double califFunSecretaria = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("CALIFFUNSECRETARIA"),
                                        0);
        double califnotas = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("CALIFNOTAS"), 0);
        double califAntiguedad = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("CALIFANTIGUEDAD"), 0);
        double califFunCargo = SysmanFunciones
                        .nvlDbl(registro.getCampos().get("CALIFFUNCARGO"), 0);
        puntajeTotal = califFunSecretaria + califnotas + califAntiguedad
            + califFunCargo;

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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
        actualizarTotal();
        // </CODIGO_DESARROLLADO>
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        registro.getCampos().remove("DIASANTICIPACION");
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "NAT_SOLICITUD_FINANCIAMIENTO_A",
                            "DP_NUMEDOCU = ''"
                                + registro.getCampos().get("DP_NUMEDOCU")
                                + "'' AND FECHASOLICITUD = "
                                + SysmanFunciones.formatearFecha((Date) registro
                                                .getCampos()
                                                .get("FECHASOLICITUD"))
                                                .replace("'", "''")
                                + "",
                            "CONSECUTIVO", "1");

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listadpnumedocu
     *
     * @return listadpnumedocu
     */
    public RegistroDataModelImpl getListadpnumedocu() {
        return listadpnumedocu;
    }

    /**
     * Asigna la lista listadpnumedocu
     *
     * @param listadpnumedocu
     * Variable a asignar en listadpnumedocu
     */
    public void setListadpnumedocu(RegistroDataModelImpl listadpnumedocu) {
        this.listadpnumedocu = listadpnumedocu;
    }

    public String getSoliAnticipada() {
        return soliAnticipada;
    }

    public void setSoliAnticipada(String soliAnticipada) {
        this.soliAnticipada = soliAnticipada;
    }

    public String getMarco() {
        return marco;
    }

    public void setMarco(String marco) {
        this.marco = marco;
    }

    public double getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(double puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
