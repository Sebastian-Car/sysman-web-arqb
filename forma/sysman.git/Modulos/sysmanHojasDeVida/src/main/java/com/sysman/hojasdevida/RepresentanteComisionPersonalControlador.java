/*-
 * RepresentanteComisionPersonalControlador.java
 *
 * 1.0
 * 
 * 15/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.RepresentanteComisionPersonalControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera reporte de comite comision de personal para seguridad salud en el trabajo, convivencia laboral y capacitacion bienestar
 *
 * @version 1.0, 15/03/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class RepresentanteComisionPersonalControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena empleado desde la vista
     */
    private String empleado;
    /**
     * Atributo que almacena clase desde la vista
     */
    private String clase;
    /**
     * Atributo que almacena comite desde la vista
     */
    private String comite;
    /**
     * Atributo que almacena tipo representante desde la vista
     */
    private String tiporepresentante;
    /**
     * Atributo que almcena funciones desde la vista
     */
    private String funciones;
    /**
     * Atributo que almacena nombre empleado desde la vista
     */
    private String nombreempleado;
    /**
     * Atributo que almacena nombre empleado desde la vista
     */
    private String codigopersona;
    /**
     * Atributo que almacena fecha de vinculacion desde la vista
     */
    private Date fechavinculacion;
    /**
     * Atributo que almacena fecha desvinculacion desde la vista
     */
    private Date fechadesvinculacion;
    /**
     * Atributo que almacena nombrecargo desde la vista
     */
    private String nombrecargo;
    /**
     * Atributo que almacena nombreclase desde la vista
     */
    private String nombreclase;
    /**
     * Atributo que almacena cargo desde la vista
     */
    private String cargo;
    /**
     * Atributo que almcena cargoem desde la vista
     */
    private String cargoem;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena empleado
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * Lista que Almacena Clase integrante
     */
    private RegistroDataModelImpl listaClaseIntegrante;
    /**
     * Lista que almacena cargo comite
     */
    private RegistroDataModelImpl listaCargoComite;

    private String modulo;
    private Object cargocomite;
    private Object nombreTitulo;
    private Object nombreEncabezado;
    private String codigoComite;
    private String sucursal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de RepresentanteComisionPersonalControlador
     */
    public RepresentanteComisionPersonalControlador() {
        super();
        compania = SessionUtil.getCompania();
        setModulo(SessionUtil.getModulo());
        try {
            numFormulario = GeneralCodigoFormaEnum.REPRESENTANTE_COMISION_PERSONAL_CONTROLADOR
                            .getCodigo();
            if ("2104020301".equals(SessionUtil.getMenuActual())) {
                setNombreTitulo(idioma.getString("TB_TB3994"));
                setNombreEncabezado("Capacitación y bienestar");
                codigoComite = "1";
            }
            else if ("2104020302".equals(SessionUtil.getMenuActual())) {
                setNombreTitulo(idioma.getString("TB_TB3995"));
                setNombreEncabezado("Seguridad y salud en el trabajo");
                codigoComite = "2";
            }
            else if ("2104020303".equals(SessionUtil.getMenuActual())) {
                setNombreTitulo(idioma.getString("TB_TB3996"));
                setNombreEncabezado("Convivencia laboral");
                codigoComite = "3";
            }
            validarPermisos();
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
        cargarListaEmpleado();
        cargarListaClaseIntegrante();
        cargarListaCargoComite();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        enumBase = GenericUrlEnum.NAT_REPRESENTANTE;
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
        parametrosListado.put("TIPOCOMITE",
                        codigoComite);
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la consulta correspondiente de la grilla del formulario, se hace la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     * 
     */
    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RepresentanteComisionPersonalControladorUrlEnum.URL7613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.PERSONAL
                                                            .getTable()));
        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaClaseIntegrante
     *
     */
    public void cargarListaClaseIntegrante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RepresentanteComisionPersonalControladorUrlEnum.URL8603
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_COMITE", codigoComite);

        listaClaseIntegrante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCargoComite
     *
     */
    public void cargarListaCargoComite() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RepresentanteComisionPersonalControladorUrlEnum.URL9717
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CODIGO_COMITE", codigoComite);

        listaCargoComite = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        generarReporte(ReportesBean.FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        generarReporte(ReportesBean.FORMATOS.EXCEL);
    }

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;

        try {
            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("cargocomite",
                            registro.getCampos().get("CARGOCOMITE").toString());
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta("001691COMITECOMISIONDEPERSONAL",
                            Integer.parseInt(modulo), reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001691COMITECOMISIONDEPERSONAL",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    private boolean validarCamposVacios() {
        if (SysmanFunciones.validarVariableVacio(comite)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB165"));
            return false;
        }
        return true;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        registroAux.getCampos().get("NUMERO_DCTO").toString());

        empleado = registroAux.getCampos().get("NUMERO_DCTO").toString();

        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get("NOMBRE").toString());

        registro.getCampos().put("NOMBRE_DEL_CARGO", registroAux.getCampos()
                        .get("NOMBRE_DEL_CARGO").toString());
        registro.getCampos().put("ID_DE_CARGO",
                        registroAux.getCampos().get("ID_DE_CARGO").toString());
        sucursal = registroAux.getCampos().get("SUCURSAL").toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaClaseIntegrante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseIntegrante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASEINTEGRANTE", registroAux.getCampos()
                        .get("CODIGO").toString());

        registro.getCampos().put("NOMBRECLASE", registroAux.getCampos()
                        .get("NOMBRE").toString());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargoComite
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoComite(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CARGOCOMITE",
                        registroAux.getCampos().get("CODIGO").toString());
        registro.getCampos().put("NOMBRECARGO",
                        registroAux.getCampos().get("NOMBRE").toString());

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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1685-AL_ABRIR Private Sub Form_Open(Cancel As Integer) ','DoCmd.Maximize End Sub
         */
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
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put("TIPOCOMITE", codigoComite);
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove("TIPOCOMITE");
        }
        registro.getCampos().remove("NOMBRE_DEL_CARGO");
        registro.getCampos().remove("ID_DE_CARGO");
        registro.getCampos().remove("NOMBRECLASE");
        registro.getCampos().remove("NOMBRECARGO");
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("R_CODIGO_PERSONA");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable empleado
     * 
     * @return empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * Asigna la variable empleado
     * 
     * @param empleado
     * Variable a asignar en empleado
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * Retorna la variable clase
     * 
     * @return clase
     */
    public String getClase() {
        return clase;
    }

    /**
     * Asigna la variable clase
     * 
     * @param clase
     * Variable a asignar en clase
     */
    public void setClase(String clase) {
        this.clase = clase;
    }

    /**
     * Retorna la variable comite
     * 
     * @return comite
     */
    public String getComite() {
        return comite;
    }

    /**
     * Asigna la variable comite
     * 
     * @param comite
     * Variable a asignar en comite
     */
    public void setComite(String comite) {
        this.comite = comite;
    }

    /**
     * Retorna la variable tiporepresentante
     * 
     * @return tiporepresentante
     */
    public String getTiporepresentante() {
        return tiporepresentante;
    }

    /**
     * Asigna la variable tiporepresentante
     * 
     * @param tiporepresentante
     * Variable a asignar en tiporepresentante
     */
    public void setTiporepresentante(String tiporepresentante) {
        this.tiporepresentante = tiporepresentante;
    }

    /**
     * Retorna la variable funciones
     * 
     * @return funciones
     */
    public String getFunciones() {
        return funciones;
    }

    /**
     * Asigna la variable funciones
     * 
     * @param funciones
     * Variable a asignar en funciones
     */
    public void setFunciones(String funciones) {
        this.funciones = funciones;
    }

    /**
     * Retorna la variable nombreempleado
     * 
     * @return nombreempleado
     */
    public String getNombreempleado() {
        return nombreempleado;
    }

    /**
     * Asigna la variable nombreempleado
     * 
     * @param nombreempleado
     * Variable a asignar en nombreempleado
     */
    public void setNombreempleado(String nombreempleado) {
        this.nombreempleado = nombreempleado;
    }

    /**
     * Retorna la variable codigopersona
     * 
     * @return codigopersona
     */
    public String getCodigopersona() {
        return codigopersona;
    }

    /**
     * Asigna la variable codigopersona
     * 
     * @param codigopersona
     * Variable a asignar en codigopersona
     */
    public void setCodigopersona(String codigopersona) {
        this.codigopersona = codigopersona;
    }

    /**
     * Retorna la variable fechavinculacion
     * 
     * @return fechavinculacion
     */
    public Date getFechavinculacion() {
        return fechavinculacion;
    }

    /**
     * Asigna la variable fechavinculacion
     * 
     * @param fechavinculacion
     * Variable a asignar en fechavinculacion
     */
    public void setFechavinculacion(Date fechavinculacion) {
        this.fechavinculacion = fechavinculacion;
    }

    /**
     * Retorna la variable fechadesvinculacion
     * 
     * @return fechadesvinculacion
     */
    public Date getFechadesvinculacion() {
        return fechadesvinculacion;
    }

    /**
     * Asigna la variable fechadesvinculacion
     * 
     * @param fechadesvinculacion
     * Variable a asignar en fechadesvinculacion
     */
    public void setFechadesvinculacion(Date fechadesvinculacion) {
        this.fechadesvinculacion = fechadesvinculacion;
    }

    /**
     * Retorna la variable nombrecargo
     * 
     * @return nombrecargo
     */
    public String getNombrecargo() {
        return nombrecargo;
    }

    /**
     * Asigna la variable nombrecargo
     * 
     * @param nombrecargo
     * Variable a asignar en nombrecargo
     */
    public void setNombrecargo(String nombrecargo) {
        this.nombrecargo = nombrecargo;
    }

    /**
     * Retorna la variable nombreclase
     * 
     * @return nombreclase
     */
    public String getNombreclase() {
        return nombreclase;
    }

    /**
     * Asigna la variable nombreclase
     * 
     * @param nombreclase
     * Variable a asignar en nombreclase
     */
    public void setNombreclase(String nombreclase) {
        this.nombreclase = nombreclase;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Retorna la variable cargoem
     * 
     * @return cargoem
     */
    public String getCargoem() {
        return cargoem;
    }

    /**
     * Asigna la variable cargoem
     * 
     * @param cargoem
     * Variable a asignar en cargoem
     */
    public void setCargoem(String cargoem) {
        this.cargoem = cargoem;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpleado
     * 
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    /**
     * Asigna la lista listaEmpleado
     * 
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    /**
     * Retorna la lista listaClaseIntegrante
     * 
     * @return listaClaseIntegrante
     */
    public RegistroDataModelImpl getListaClaseIntegrante() {
        return listaClaseIntegrante;
    }

    /**
     * Asigna la lista listaClaseIntegrante
     * 
     * @param listaClaseIntegrante
     * Variable a asignar en listaClaseIntegrante
     */
    public void setListaClaseIntegrante(
        RegistroDataModelImpl listaClaseIntegrante) {
        this.listaClaseIntegrante = listaClaseIntegrante;
    }

    /**
     * Retorna la lista listaCargoComite
     * 
     * @return listaCargoComite
     */
    public RegistroDataModelImpl getListaCargoComite() {
        return listaCargoComite;
    }

    /**
     * Asigna la lista listaCargoComite
     * 
     * @param listaCargoComite
     * Variable a asignar en listaCargoComite
     */
    public void setListaCargoComite(RegistroDataModelImpl listaCargoComite) {
        this.listaCargoComite = listaCargoComite;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Object getCargocomite() {
        return cargocomite;
    }

    public void setCargocomite(Object cargocomite) {
        this.cargocomite = cargocomite;
    }

    public Object getNombreTitulo() {
        return nombreTitulo;
    }

    public void setNombreTitulo(Object nombreTitulo) {
        this.nombreTitulo = nombreTitulo;
    }

    public Object getNombreEncabezado() {
        return nombreEncabezado;
    }

    public void setNombreEncabezado(Object nombreEncabezado) {
        this.nombreEncabezado = nombreEncabezado;
    }
}
