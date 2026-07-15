/*-
 * NatAccidentesTrabajosControlador.java
 *
 * 1.0
 * 
 * 29/12/2017
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
import com.sysman.hojasdevida.enums.NatAccidentesTrabajosControladorEnum;
import com.sysman.hojasdevida.enums.NatAccidentesTrabajosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite administrar los accidentes de trabajo del personal.
 *
 * @version 1.0, 29/12/2017
 * @author eamaya
 * 
 * @version 2, 20/01/2018
 * @modifier amonroy, Se realizan ajustes en los DSS a los que hacia referencia el Controlador para la ejecucion de las operaciones CRUD, Se realizan ajustes de forma para reemplazo de etiquetas, se
 * adiciona el metodo ejecutarrcCerrar para realizar el redireccionamiento, Ajustes de SonarLint
 */
@ManagedBean
@ViewScoped
public class NatAccidentesTrabajosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>NOMBRE</b>
     */
    private final String cNombre;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>TIPO_TRANSACCION</b>
     */
    private final String cTipoTransaccion;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>NUMERO_DCTO</b>
     */
    private final String cNumeroDocumento;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>NOMBRE_RIESGO</b>
     */
    private final String cNombreRiesgo;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>NOMBRE_CONCEPTO</b>
     */
    private final String cNombreConcepto;
    /**
     * Atributo que almacena el Titulo de la grilla para el formulario
     */
    private String tituloFrmPrincipal;
    /**
     * Constante definida por el numero de veces que se realiza el llamado a la palabra <b>SUCURSAL</b>
     */
    private final String cSucursal;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el nombre del empleado
     */
    private String nombreEmpleado;

    /**
     * Atributo que almacena el nombre del factor de riesgo
     */
    private String nombreFactorRiesgo;
    /**
     * Atributo que almacena el nombre del concepto de riesgo
     */
    private String nombreConceptoRiesgo;
    /**
     * Atributo que almacena los campos y valores que son llave para el registro que se esta trabajando en el formulario "FrmtransaccionessstsControlador (1563)"
     */
    private Map<String, Object> ridSstTransacciones;
    /**
     * Atributo que almacena titulo que se va visualizar en el formulario
     */
    private String titulo;
    /**
     * Valor que se recibe por parametro, almacena la Clase Transaccion que es un campo de la llave principal para la tabla NAT_SEGURIDAD_INDUSTRIAL
     */
    private String claseTransaccion;
    /**
     * Valor que se recibe por parametro, almacena el codigo del empleado con el que se esta trabajando
     */
    private String codEmpleado;
    /**
     * Atributo que almacena el valor del indicador del agente recibido por parametro
     */
    private boolean indicadorAgente;
    /**
     * Valor que se recibe por parametro, almacena el Numero de Documento del empleado con el que se esta trabajando
     */
    private String nroDocEmpleado;
    /**
     * Valor que se recibe por parametro, almacena la sucursa a la que se encuentra asociado del empleado con el que se esta trabajando
     */
    private String sucursalEmpleado;
    /**
     * Valor que se recibe por parametro, almacena el Tipo de Transaccion que es un campo de la llave principal para la tabla NAT_SEGURIDAD_INDUSTRIAL
     */
    private String tipoTransaccion;
    /**
     * Atributo que almacena el nombre de la transaccion que se esta realizando
     */
    private String nombreTransaccion;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido seleccionado posee empleados a cargo
     */
    private boolean responsable;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido seleccionado es Comite
     */
    private boolean comite;
    /**
     * Atributo que almacena el valor que se visualizara en la etiqueta <code>LB40486</code>
     */
    private String etiquetaTipoTransaccion;
    /**
     * Atributo que almacena el valor que se visualizara en la etiqueta <code>LB40487</code>
     */
    private String etiquetaCausaTransaccion;
    /**
     * Atributo que almacena el nombre de la Transaccion en la que se esta trabajando
     */
    private String nombreClaseTransaccion;
    /**
     * Indicador para referenciar la visibilidad de los objetos <code>LB44477, CP54361</code>
     */
    private boolean conIncapacidad;
    /**
     * Indicador para referenciar la visibilidad de los objetos <code>LB44476, CP54360</code>
     */
    private boolean conTestigo;

    /**
     * Indicador para referenciar la visibilidad de los objetos <code>LB45138, CP55291 ,LB45139, CP55292 </code>
     */
    private boolean conRemision;
    /**
     * Indicador para referenciar la visibilidad de los objetos <code>LB45137, CK1566  </code>
     */
    private boolean visibleRemision;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los registros del factor de riesgo
     */
    private RegistroDataModelImpl listaFactorRiesgo;
    /**
     * Lista que carga los registros del concepto de riesgo
     */
    private RegistroDataModelImpl listaConceptoRiegos;
    /**
     * Lista que carga los registros de los empleados
     */
    private RegistroDataModelImpl listaCodigo;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatAccidentesTrabajosControlador
     */
    @SuppressWarnings("unchecked")
    public NatAccidentesTrabajosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cTipoTransaccion = "TIPO_TRANSACCION";
        cNumeroDocumento = GeneralParameterEnum.NUMERO_DCTO.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cNombreRiesgo = "NOMBRE_RIESGO";
        cNombreConcepto = "NOMBRE_CONCEPTO";

        try {
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                titulo = (String) parametros.get("titulo");
                claseTransaccion = (String) parametros.get("claseTransaccion");
                tipoTransaccion = (String) parametros.get("tipoTransaccion");
                codEmpleado = (String) parametros.get("codEmpleado");
                nombreEmpleado = (String) parametros.get("nombreEmpleado");
                nroDocEmpleado = (String) parametros.get("nroDocEmpleado");
                sucursalEmpleado = (String) parametros.get("sucursalEmpleado");

                nombreTransaccion = (String) parametros
                                .get("nombreTransaccion");
                responsable = (boolean) parametros.get("responsable");
                comite = (boolean) parametros.get("comite");
                indicadorAgente = (boolean) parametros.get("agente");
                ridSstTransacciones = (Map<String, Object>) parametros
                                .get("rid");

                // Nombre Etiquetas
                nombreClaseTransaccion = (String) parametros
                                .get("nombreClaseTransaccion");
                etiquetaTipoTransaccion = idioma.getString("TB_TB3928").replace(
                                "s$nombreClase$s",
                                obtenerNombre(nombreClaseTransaccion));
                etiquetaCausaTransaccion = idioma.getString("TB_TB3929")
                                .replace("s$nombreClase$s", obtenerNombre(
                                                nombreClaseTransaccion));
            }

            numFormulario = GeneralCodigoFormaEnum.NAT_ACCIDENTES_TRABAJOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            tituloFrmPrincipal = nombreTransaccion.toUpperCase();
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
        cargarListaFactorRiesgo();
        cargarListaCodigo();
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
        enumBase = GenericUrlEnum.NAT_SEGURIDAD_INDUSTRIAL;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(cNumeroDocumento, nroDocEmpleado);
        parametrosListado.put(cSucursal, sucursalEmpleado);
        parametrosListado.put(cTipoTransaccion, tipoTransaccion);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFactorRiesgo
     *
     */
    public void cargarListaFactorRiesgo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatAccidentesTrabajosControladorUrlEnum.URL6289
                                                        .getValue());

        listaFactorRiesgo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, NatAccidentesTrabajosControladorEnum.ID_RIESGO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaConceptoRiegos
     *
     */
    public void cargarListaConceptoRiegos() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatAccidentesTrabajosControladorUrlEnum.URL6784
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(NatAccidentesTrabajosControladorEnum.ID_RIESGO.getValue(),
                        registro.getCampos()
                                        .get(NatAccidentesTrabajosControladorEnum.FACTOR_RIESGO
                                                        .getValue()));

        listaConceptoRiegos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NatAccidentesTrabajosControladorEnum.ID_CONCEPTO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaCodigo
     *
     */
    public void cargarListaCodigo() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatAccidentesTrabajosControladorUrlEnum.URL7111
                                                        .getValue());

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Incapacidad
     * 
     * Actualiza el valor del atributo conIncapacidad
     * 
     */
    public void cambiarIncapacidad() {
        // <CODIGO_DESARROLLADO>
        conIncapacidad = (boolean) registro.getCampos().get("IND_INCAPACIDAD");
        if (!conIncapacidad) {
            registro.getCampos().put("DIAS_INCAPACIDAD", 0);
            registro.getCampos().put("DIAGNOSTICO", null);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Testigos
     * 
     * Actualiza el valor del atributo conTestigo
     * 
     */
    public void cambiarTestigos() {
        // <CODIGO_DESARROLLADO>
        conTestigo = (boolean) registro.getCampos().get("IND_TESTIGOS");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Remision
     * 
     */
    public void cambiarRemision() {
        conRemision = (boolean) registro.getCampos().get("IND_REMISION");
        if (!conRemision) {
            registro.getCampos().put("FECHA_REMISION", null);
            registro.getCampos().put("OBSERVACION_REMISION", null);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFactorRiesgo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFactorRiesgo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(NatAccidentesTrabajosControladorEnum.FACTOR_RIESGO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(NatAccidentesTrabajosControladorEnum.ID_RIESGO
                                                                        .getValue()));

        nombreFactorRiesgo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(cNombreRiesgo, nombreFactorRiesgo);
        registro.getCampos().put("CONCEPTO_RIESGO", "");
        registro.getCampos().put(cNombreConcepto, "");

        cargarListaConceptoRiegos();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaConceptoRiegos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoRiegos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO_RIESGO",
                        registroAux.getCampos()
                                        .get(NatAccidentesTrabajosControladorEnum.ID_CONCEPTO
                                                        .getValue()));

        nombreConceptoRiesgo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        registro.getCampos().put(cNombreConcepto, nombreConceptoRiesgo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()));

        nombreEmpleado = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();

        registro.getCampos().put(cNumeroDocumento,
                        registroAux.getCampos().get(cNumeroDocumento));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Testigos en la vista
     *
     */
    public void oprimirTestigos() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { registro.getLlave() };
        SessionUtil.cargarModalDatosFlash(Integer
                        .toString(GeneralCodigoFormaEnum.TESTIGOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
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
        visibleRemision = "E".equals(claseTransaccion);

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
        if (registro != null && !registro.getCampos().isEmpty()) {
            conIncapacidad = (boolean) registro.getCampos()
                            .get("IND_INCAPACIDAD");
            conTestigo = (boolean) registro.getCampos().get("IND_TESTIGOS");

            conRemision = (boolean) registro.getCampos().get("IND_REMISION");
        }

        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        codEmpleado);
        registro.getCampos().put(cNumeroDocumento, nroDocEmpleado);
        registro.getCampos().put(cSucursal, sucursalEmpleado);
        registro.getCampos().put(cTipoTransaccion, tipoTransaccion);
        registro.getCampos()
                        .put(NatAccidentesTrabajosControladorEnum.CLASE_TRANSACCION
                                        .getValue(), claseTransaccion);
        registro.getCampos().put(cNombre, nombreEmpleado);
        if (css == null) {
            generarConsecutivo();
            conIncapacidad = conTestigo = conRemision = false;
        }
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
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cNombreRiesgo);
        registro.getCampos().remove(cNombreConcepto);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Realiza el llamado a la funcion PCK_SYSMAN_UTL.FC_GENCONSECUTIVO, para obetener el valor a asignar en el campo CONSECUTIVO y asignarlo al registro
     */
    private void generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "NAT_SEGURIDAD_INDUSTRIAL", "COMPANIA =" + compania,
                            GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos().remove(cNombre);
            registro.getCampos().remove(cNombreRiesgo);
            registro.getCampos().remove(cNombreConcepto);
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos().remove(cTipoTransaccion);
            registro.getCampos().remove("CLASE_TRANSACCION");
            registro.getCampos()
                            .remove(GeneralParameterEnum.ID_DE_EMPLEADO
                                            .getName());
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * Realiza el envio de parametros para la redirccion al formulario FrmtransaccionessstsControlador (1563)
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipoTransaccion", tipoTransaccion);
        parametros.put("nombreTransaccion", nombreTransaccion);
        parametros.put("responsable", responsable);
        parametros.put("comite", comite);
        parametros.put("claseTransaccion", claseTransaccion);
        parametros.put("rid", ridSstTransacciones);
        parametros.put("agente", indicadorAgente);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FRMTRANSACCIONESSSTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Obtiene la primera palabra de un texto y la retorna con mayuscula inicial
     * 
     * @param cadena
     * Texto a evaluar
     * @return Primera palabra con mayuscula inicial
     */
    private String obtenerNombre(String cadena) {
        String[] palabras = cadena.split(" ");
        return initCap(palabras[0]);
    }

    /**
     * Retorna una cadena cuya letra inicial es en mayuscula
     * 
     * @param cadena
     * Cadena de Texto a evaluar
     * @return Palabra con mayuscula inicial
     */
    private String initCap(String cadena) {
        return cadena.substring(0, 1).toUpperCase()
            + cadena.toLowerCase().substring(1, cadena.length());
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreEmpleado
     * 
     * @return nombreEmpleado
     */
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    /**
     * Asigna la variable nombreEmpleado
     * 
     * @param nombreEmpleado
     * Variable a asignar en nombreEmpleado
     */
    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     * Retorna la variable nombreFactorRiesgo
     * 
     * @return nombreFactorRiesgo
     */
    public String getNombreFactorRiesgo() {
        return nombreFactorRiesgo;
    }

    /**
     * Asigna la variable nombreFactorRiesgo
     * 
     * @param nombreFactorRiesgo
     * Variable a asignar en nombreFactorRiesgo
     */
    public void setNombreFactorRiesgo(String nombreFactorRiesgo) {
        this.nombreFactorRiesgo = nombreFactorRiesgo;
    }

    /**
     * Retorna la variable nombreConceptoRiesgo
     * 
     * @return nombreConceptoRiesgo
     */
    public String getNombreConceptoRiesgo() {
        return nombreConceptoRiesgo;
    }

    /**
     * Asigna la variable nombreConceptoRiesgo
     * 
     * @param nombreConceptoRiesgo
     * Variable a asignar en nombreConceptoRiesgo
     */
    public void setNombreConceptoRiesgo(String nombreConceptoRiesgo) {
        this.nombreConceptoRiesgo = nombreConceptoRiesgo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEtiquetaTipoTransaccion() {
        return etiquetaTipoTransaccion;
    }

    public void setEtiquetaTipoTransaccion(String etiquetaTipoTransaccion) {
        this.etiquetaTipoTransaccion = etiquetaTipoTransaccion;
    }

    public String getEtiquetaCausaTransaccion() {
        return etiquetaCausaTransaccion;
    }

    public void setEtiquetaCausaTransaccion(String etiquetaCausaTransaccion) {
        this.etiquetaCausaTransaccion = etiquetaCausaTransaccion;
    }

    public boolean isConIncapacidad() {
        return conIncapacidad;
    }

    public void setConIncapacidad(boolean conIncapacidad) {
        this.conIncapacidad = conIncapacidad;
    }

    public boolean isConTestigo() {
        return conTestigo;
    }

    public void setConTestigo(boolean conTestigo) {
        this.conTestigo = conTestigo;
    }

    public boolean isConRemision() {
        return conRemision;
    }

    public void setConRemision(boolean conRemision) {
        this.conRemision = conRemision;
    }

    public boolean isVisibleRemision() {
        return visibleRemision;
    }

    public void setVisibleRemision(boolean visibleRemision) {
        this.visibleRemision = visibleRemision;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFactorRiesgo
     * 
     * @return listaFactorRiesgo
     */
    public RegistroDataModelImpl getListaFactorRiesgo() {
        return listaFactorRiesgo;
    }

    /**
     * Asigna la lista listaFactorRiesgo
     * 
     * @param listaFactorRiesgo
     * Variable a asignar en listaFactorRiesgo
     */
    public void setListaFactorRiesgo(RegistroDataModelImpl listaFactorRiesgo) {
        this.listaFactorRiesgo = listaFactorRiesgo;
    }

    /**
     * Retorna la lista listaConceptoRiegos
     * 
     * @return listaConceptoRiegos
     */
    public RegistroDataModelImpl getListaConceptoRiegos() {
        return listaConceptoRiegos;
    }

    /**
     * Asigna la lista listaConceptoRiegos
     * 
     * @param listaConceptoRiegos
     * Variable a asignar en listaConceptoRiegos
     */
    public void setListaConceptoRiegos(
        RegistroDataModelImpl listaConceptoRiegos) {
        this.listaConceptoRiegos = listaConceptoRiegos;
    }

    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public String getTituloFrmPrincipal() {
        return tituloFrmPrincipal;
    }

    public void setTituloFrmPrincipal(String tituloFrmPrincipal) {
        this.tituloFrmPrincipal = tituloFrmPrincipal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
