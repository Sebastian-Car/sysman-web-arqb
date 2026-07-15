/*-
 * FamiliaresControlador.java
 *
 * 1.0
 *
 * 27/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FamiliaresControladorEnum;
import com.sysman.general.enums.FamiliaresControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 23/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 *
 * @version 2.0, 20/04/2018
 * @author eamaya se agrega la insercion a la tabla FAMILIARES con la
 * SUCURSAL_EMPLEADO y modifican dss para que tomar la informacion de
 * la tabla NAT_DATOS_PERSONALES en vez de PERSONAL
 * 
 */

@ManagedBean
@ViewScoped
public class FamiliaresControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * parametros de entrada del formulario principal
     * natdatospersonales
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    /**
     * codigo empleado
     */
    private String numeroDcto;

    /**
     * sucursal empleado
     */
    private String sucursal;

    /**
     * estado del empleado
     */
    private String estado;

    /**
     * valida si se carga el empleado del formulario principal
     */
    private boolean cargarEmpleado;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * listado con los tipos de documentos de identidad
     */
    private List<Registro> listaDctoIdentidad;
    /**
     * listado de parentescos posibles con el empleado
     */
    private List<Registro> listaParentesco;
    /**
     * listado de bancos
     */
    private RegistroDataModelImpl listaBanco1;

    /**
     * lista de identificaciones
     */
    private RegistroDataModelImpl listaIdentificacion;

    /**
     * lista para el combo de empleados
     */
    private RegistroDataModelImpl listacmbempleado;

    /**
     * variable que almacena el nombre del fondo de salud
     */
    private String formato;

    /**
     * valor de la edad calculado a partir de la fecha de nacimiento
     */
    private String edad;

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
     * Crea una nueva instancia de FamiliaresControlador
     */
    @SuppressWarnings("unchecked")
    public FamiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FAMILIARES_CONTROLADOR
                            .getCodigo();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                numeroDcto = SysmanFunciones
                                .nvl(parametrosEntrada.get("numeroDcto"), "")
                                .toString();
                sucursal = SysmanFunciones
                                .nvl(parametrosEntrada.get("sucursal"), "")
                                .toString();
                estado = "1";
                cargarEmpleado = false;
            }
            else {
                estado = "0";
                cargarEmpleado = true;
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarlistaDctoIdentidad();
        cargarListaParentesco();
        cargarListaBanco1();
        cargarListaIdentificacion();
        cargarListacmbempleado();
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
        enumBase = GenericUrlEnum.FAMILIARES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ESTADO.getName(),
                        estado);
        parametrosListado.put(FamiliaresControladorEnum.DCTOEMPLEADO.getValue(),
                        numeroDcto);
        parametrosListado.put(
                        FamiliaresControladorEnum.SUCURSAL_EMPLEADO.getValue(),
                        sucursal);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaDcto_Identidad
     *
     */
    public void cargarlistaDctoIdentidad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaDctoIdentidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FamiliaresControladorUrlEnum.URL3320
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaParentesco
     *
     */
    public void cargarListaParentesco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaParentesco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FamiliaresControladorUrlEnum.URL3321
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaBanco1
     *
     */
    public void cargarListaBanco1() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FamiliaresControladorUrlEnum.URL3322
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaBanco1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FamiliaresControladorEnum.FONDO_SALUD.getValue());
    }

    public void cargarListaIdentificacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FamiliaresControladorUrlEnum.URL3323
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaIdentificacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FamiliaresControladorEnum.NIT.getValue());
    }

    public void cargarListacmbempleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FamiliaresControladorUrlEnum.URL3324
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listacmbempleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    public void seleccionarFilacmbempleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FamiliaresControladorEnum.DCTO_EMPLEADO.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));

        registro.getCampos().put("NOMBRES",
                        registroAux.getCampos().get("NOMBRES"));

        registro.getCampos().put(
                        FamiliaresControladorEnum.SUCURSAL_EMPLEADO.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

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
        if (ACCION_INSERTAR.equals(accion)) {
            edad = "0";
            registro.getCampos().put(
                            FamiliaresControladorEnum.FECHANCTO.getValue(),
                            new Date());
        }
        cambiarFechaNcto();

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
        registro.getCampos()
                        .remove(FamiliaresControladorEnum.POLIZA.getValue());

        registro.getCampos().remove("NOMBRES");
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
        if ("1".equals(estado)) {
            registro.getCampos().put(
                            FamiliaresControladorEnum.DCTO_EMPLEADO.getValue(),
                            numeroDcto);
            registro.getCampos().put(FamiliaresControladorEnum.SUCURSAL_EMPLEADO
                            .getValue(), sucursal);
        }
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(FamiliaresControladorEnum.SUCURSAL_EMPLEADO
                                            .getValue());
            registro.getCampos().remove(
                            FamiliaresControladorEnum.FONDO_SALUD.getValue());

            registro.getCampos().remove("NOMBRES");
        }
        if (SysmanConstantes.CONS_TERCERO.equals(numeroDcto)) {
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            SysmanConstantes.CONS_SUCURSAL);
        }
        else {
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            SysmanConstantes.CONS_SUCURSAL_DEFAULT);
        }
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
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     *
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if ("1".equals(estado)) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", ridDatosPersonales);
            parametros.put("idEmpleado", numeroDcto);

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
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

    public void seleccionarFilaIdentificacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FamiliaresControladorEnum.IDENTIFICACION.getValue(),
                        registroAux.getCampos()
                                        .get(FamiliaresControladorEnum.NIT
                                                        .getValue()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    public void seleccionarFilaBanco1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FamiliaresControladorEnum.FONDO_SALUD.getValue(),
                        registroAux.getCampos()
                                        .get(FamiliaresControladorEnum.FONDO_SALUD
                                                        .getValue()));
        formato = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FamiliaresControladorEnum.NOMBRE_FONDO_SALUD
                                                        .getValue()),
                                        "")
                        .toString();

    }

    public void cambiarFechaNcto() {
        if (registro.getCampos().get(
                        FamiliaresControladorEnum.FECHANCTO
                                        .getValue()) != null) {
            edad = SysmanFunciones.calcularEdad(
                            (Date) registro.getCampos()
                                            .get(FamiliaresControladorEnum.FECHANCTO
                                                            .getValue()));
        }

    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDcto_Identidad
     *
     * @return listaDcto_Identidad
     */
    public List<Registro> getListaDctoIdentidad() {
        return listaDctoIdentidad;
    }

    /**
     * Asigna la lista listaDcto_Identidad
     *
     * @param listaDctoIdentidad
     * Variable a asignar en listaDcto_Identidad
     */
    public void setListaDctoIdentidad(List<Registro> listaDctoIdentidad) {
        this.listaDctoIdentidad = listaDctoIdentidad;
    }

    /**
     * Retorna la lista listaParentesco
     *
     * @return listaParentesco
     */
    public List<Registro> getListaParentesco() {
        return listaParentesco;
    }

    /**
     * Asigna la lista listaParentesco
     *
     * @param listaParentesco
     * Variable a asignar en listaParentesco
     */
    public void setListaParentesco(List<Registro> listaParentesco) {
        this.listaParentesco = listaParentesco;
    }

    /**
     * Retorna la lista listaBanco1
     *
     * @return listaBanco1
     */
    public RegistroDataModelImpl getListaBanco1() {
        return listaBanco1;
    }

    /**
     * Asigna la lista listaBanco1
     *
     * @param listaBanco1
     * Variable a asignar en listaBanco1
     */
    public void setListaBanco1(RegistroDataModelImpl listaBanco1) {
        this.listaBanco1 = listaBanco1;
    }

    public RegistroDataModelImpl getListaIdentificacion() {
        return listaIdentificacion;
    }

    public void setListaIdentificacion(
        RegistroDataModelImpl listaIdentificacion) {
        this.listaIdentificacion = listaIdentificacion;
    }

    public RegistroDataModelImpl getListacmbempleado() {
        return listacmbempleado;
    }

    public void setListacmbempleado(RegistroDataModelImpl listacmbempleado) {
        this.listacmbempleado = listacmbempleado;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public boolean isCargarEmpleado() {
        return cargarEmpleado;
    }

    public void setCargarEmpleado(boolean cargarEmpleado) {
        this.cargarEmpleado = cargarEmpleado;
    }

    public String getNumeroDcto() {
        return numeroDcto;
    }

    public void setNumeroDcto(String numeroDcto) {
        this.numeroDcto = numeroDcto;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
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
