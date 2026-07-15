/*-
 * SolicitudesAutsControlador.java
 *
 * 1.0
 *
 * 03/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SolicitudesAutsControladorEnum;
import com.sysman.general.enums.SolicitudesAutsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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
 * Esta clase es el controlador para el formulario Solicitudes en Access "FRMSOLICITUDES", el cual es llamado desde Hojas de Vida\Autoservicio\Gesti�n Autoservicio\Solicitudes
 *
 *
 * @version 1.0, 03/02/2018
 * @author amonroy
 *
 * @version 2.0, 05/04/2018
 * @author sdaza / ajustar dise�o del formulario teniendo en cuenta si el tipo es una solicitud o consulta; y si requiere o no periodo
 */
@ManagedBean
@ViewScoped
public class SolicitudesAutsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el identificador del empleado que ha ingresado al sistema
     */
    private String idEmpleado;
    /**
     * Atributo que indica si se esta accediendo al formulario mediante la opcion "Por Tramitar" o "Mis Solicitudes"
     */
    private int porTramitar;
    /**
     * Esta variable permite ver o no el boton de historico
     */
    private boolean verBotonHistorico;
    /**
     * Esta variable tiene
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SolicitudesAutsControlador
     */
    public SolicitudesAutsControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.SOLICITUDES_AUTS_CONTROLADOR
                            .getCodigo();// 1690
            validarPermisos();
            // <INI_ADICIONAL>
            porTramitar = 0;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
                    String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.AUT_SOLICITUDES;

        buscarLlave();
        idEmpleado = obtenerIdEmpleado();
        if ("".equals(idEmpleado)) {
            SessionUtil.agregarMensajeErrorMenu(
                            "Usted no tiene un identificador como empleado de la entidad. No pude registrar solicitudes");

            SessionUtil.redireccionarMenu();
        }
        reasignarOrigen();
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
    public void reasignarOrigen() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        SessionUtil.getUser().getCedula());
        parametrosListado.put(
                        SolicitudesAutsControladorEnum.PORTRAMITAR.getValue(),
                        porTramitar);
        parametrosListado.put(
                        SolicitudesAutsControladorEnum.DESTINATARIO.getValue(),
                        SessionUtil.getUser().getCedula());
        parametrosListado.put(
                        SolicitudesAutsControladorEnum.IDEMPLEADO.getValue(),
                        idEmpleado);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Nuevo en la vista
     *
     * Realiza la redireccion al formulario "SolicitudesAutDetallado(1693)" y realiza el envio de los parametros
     *
     */
    public void oprimirNuevo() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("numeroSolicitud", 0);
        parametros.put("estadoSolicitud", "S");
        parametros.put("bloqueaPeriodo", true);
        parametros.put("llavesSolicitud", null);
        parametros.put("porTramitar", porTramitar);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SOLICITUDESAUT_DETALLADOS_CONTROLADOR
                                        .getCodigo()));// 1693
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton PorTramitar en la vista
     *
     * Asigna valor al indicador "PorTramitar" y recarga la grilla que permite la visualizacion de las solicitudes
     *
     */
    public void oprimirPorTramitar() {
        // <CODIGO_DESARROLLADO>
        porTramitar = 1;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton MisSolicitudes en la vista
     *
     * Asigna valor al indicador "PorTramitar" y recarga la grilla que permite la visualizacion de las solicitudes
     *
     */
    public void oprimirMisSolicitudes() {
        // <CODIGO_DESARROLLADO>
        porTramitar = 0;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnHistorico en la vista
     *
     *
     */
    public void oprimirbtnHistorico() {
        // <CODIGO_DESARROLLADO>
        porTramitar = 3;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Ver
     *
     * Realiza la redireccin al formulario "SolicitudesSutDetallado(1693)" para visualizar o editar ua solicitud registrada
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirVer(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("numeroSolicitud", Integer.parseInt(
                        reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO
                                        .getName()).toString()));
        parametros.put("estadoSolicitud", retornarString(reg,
                        SolicitudesAutsControladorEnum.DESTINOO.getValue()));
        /*
         * En el tipo de solicitud se configura si el tipo requeire o no periodo de trabajo,
         *
         * si se requiere el atributo bloqueaPeriodo debe tomar el valor de false para habilitar
         *
         * los campos. Si no es requerido debera tomar el valor de true para que se inhabiliten los campos
         */
        parametros.put("bloqueaPeriodo", !((boolean) reg.getCampos().get("REQUIERE_PERIODO")));
        parametros.put("tipoSolicitud", reg.getCampos().get("TIPO_SOLICITUD"));
        parametros.put("reporte", SysmanFunciones.validarCampoVacio(reg.getCampos(), "REPORTE") ? null
                        : reg.getCampos().get("REPORTE").toString());
        parametros.put("plantilla", SysmanFunciones.validarCampoVacio(reg.getCampos(), "CODIGO_PLANTILLA") ? null
                        : reg.getCampos().get("CODIGO_PLANTILLA").toString());
        parametros.put("porTramitar", porTramitar);
        parametros.put("claseSolicitud", registro.getCampos().get("NOMBRESOLICITUD"));
        parametros.put("llavesSolicitud", reg.getLlave());

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SOLICITUDESAUT_DETALLADOS_CONTROLADOR
                                        .getCodigo()));// 1693
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if (obtenerParametro("USUARIO_GESTOR_TALENTO_HUMANO", "NO").equals(SessionUtil.getUser().getCodigo())) {
            verBotonHistorico = true;
        }
        else {
            verBotonHistorico = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return Si el proceso previo a la insercion fue exitoso
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
     * @return Si el proceso de insercion fue exitoso
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
     * @return Si el proceso previo a la actualizacion fue exitoso
     *
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     * @return Si el proceso de actualizacion fue exitoso
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
     * @return Si el proceso previo a la eliminacion fue exitoso
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!"0".equals(registro.getCampos().get("SENOTIFICA"))) {
            JsfUtil.agregarMensajeInformativo(
                            "No es posible eliminar esta solicitud porque ya fue enviada al Jefe Directo");
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     * @return Si el proceso de eliminacion fue exitoso
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo dentro del registro que tambien ha sido ingresado por parametro
     *
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();
    }

    /**
     * Obtiene el Codigo que identifica a la persona que ha ingresado al sistema realizando la consulta en la informacion de Persoanl
     */
    private String obtenerIdEmpleado() {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(SolicitudesAutsControladorEnum.CEDULA.getValue(),
                            SessionUtil.getUser().getCedula());
            params.put(GeneralParameterEnum.SUCURSAL.getName(),
                            SessionUtil.getUser().getSucursal());

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudesAutsControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), params));
            if (rs != null) {
                return retornarString(rs,
                                GeneralParameterEnum.ID_DE_EMPLEADO.getName());
            }
            else {
                return "";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";

    }

    public int getPorTramitar() {
        return porTramitar;
    }

    public void setPorTramitar(int porTramitar) {
        this.porTramitar = porTramitar;
    }

    public boolean isVerBotonHistorico() {
        return verBotonHistorico;
    }

    public void setVerBotonHistorico(boolean verBotonHistorico) {
        this.verBotonHistorico = verBotonHistorico;
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
