/*-
 * ExamenMedicosControlador.java
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
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.ExamenMedicosControladorEnum;
import com.sysman.hojasdevida.enums.ExamenMedicosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario
 * "Examen Medico del Personal" en Access "NAT_EXAMEN_MEDICO", el cual
 * es llamado desde Hojas De Vida / Seguridad Y Salud En El Trabajo /
 * Procesos / Historia Clínica Ocupacional / Examen Médico
 *
 * @version 1.0, 29/12/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ExamenMedicosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NUMERO_DCTO en el formulario, almacena el
     * texto NUMERO_DCTO el cual es un campo del registro
     */
    private final String cNumeroDcto;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE_ENFERMEDAD en el formulario, almacena
     * el texto NOMBRE_ENFERMEDAD el cual es un campo del registro
     */
    private final String cNombreEnfermedad;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo SUCURSAL en el formulario, almacena el texto
     * SUCURSAL el cual es un campo del registro
     */
    private final String cSucursal;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el nombre de la persona seleccionada en
     * el combo "Empleado"
     */
    private String nombreCompleto;
    /**
     * Atributo que almacena la sucursal de la persona seleccionada en
     * el combo "Empleado"
     */
    private String sucursal;
    /**
     * Atributo que almacena el codigo de la persona seleccionada en
     * el combo "Empleado"
     */
    private String idEmpleado;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo de Tipo Examen
     */
    private List<Registro> listaTipoExamen;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de Enfermedad
     */
    private RegistroDataModelImpl listaNombreEnfermedad;
    /**
     * Listado de registros para el combo de empleado
     */
    private RegistroDataModelImpl listaNumeroDcto;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ExamenMedicosControlador
     */
    public ExamenMedicosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNumeroDcto = GeneralParameterEnum.NUMERO_DCTO.getName();
        cNombreEnfermedad = ExamenMedicosControladorEnum.NOMBRE_ENFERMEDAD
                        .getValue();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.EXAMEN_MEDICOS_CONTROLADOR
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
        cargarListaNombreEnfermedad();
        cargarListaNumeroDcto();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoExamen();
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
        enumBase = GenericUrlEnum.NAT_ENFERMEDAD_PERSONAL;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * Realiza el llamado del metodo buscarUrls() el cual asigna las
     * URLs de las operaciones basicas del formulario a los DSS
     * creados
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
     * Carga la lista listaTipoExamen
     *
     */
    public void cargarListaTipoExamen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTipoExamen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExamenMedicosControladorUrlEnum.URL8407
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
     * Carga la lista listaNombreEnfermedad
     *
     */
    public void cargarListaNombreEnfermedad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExamenMedicosControladorUrlEnum.URL8944
                                                        .getValue());
        listaNombreEnfermedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, ExamenMedicosControladorEnum.CODIGOEN.getValue());
    }

    /**
     * 
     * Carga la lista listaNumeroDcto
     *
     */
    public void cargarListaNumeroDcto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ExamenMedicosControladorUrlEnum.URL9544
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDcto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumeroDcto);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaExamen
     * 
     * Valida que la fecha ingresada no sea superior a la fecha actual
     * 
     */
    public void cambiarFechaExamen() {
        // <CODIGO_DESARROLLADO>
        String strFecha = ExamenMedicosControladorEnum.FECHA_APARICION
                        .getValue();
        if (SysmanFunciones
                        .truncarFecha((Date) registro.getCampos()
                                        .get(strFecha))
                        .after(SysmanFunciones.truncarFecha(new Date()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3897"));
            registro.getCampos().put(strFecha, null);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreEnfermedad
     *
     * El campo visible en el combo es NOMBRE_ENFERMEDAD, sin embargo
     * en la tabla se registra el Codigo de la enfermedad CODIGOEN,
     * por tal razon se asignan tanto el nombre como el codigo de la
     * enfermedad seleccionada en el combo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreEnfermedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cNombreEnfermedad,
                        retornarString(registroAux, cNombreEnfermedad));
        registro.getCampos().put("ID_ENF_PER",
                        retornarString(registroAux,
                                        ExamenMedicosControladorEnum.CODIGOEN
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumeroDcto
     *
     * Asigna los valores asociados al empleado seleccionado que son
     * necesarios para realizar un registro nuevo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroDcto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cNumeroDcto,
                        retornarString(registroAux, cNumeroDcto));
        nombreCompleto = retornarString(registroAux,
                        ExamenMedicosControladorEnum.NOMBRES.getValue());
        sucursal = retornarString(registroAux, cSucursal);
        idEmpleado = retornarString(registroAux,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
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
     * Actualiza el valor del atributo "nombreCompleto" dependiendo la
     * operacion CRUD que se este realizando
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        try {
            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametros.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            retornarString(registro, cNumeroDcto));
            parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
                            retornarString(registro, cSucursal));

            Registro aux = new Registro(listaNumeroDcto
                            .getRegistroUnico(parametros).getCampos());

            nombreCompleto = css != null ? retornarString(aux,
                            ExamenMedicosControladorEnum.NOMBRES.getValue())
                : null;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            // Se remueven los campos que no son necesarios en la
            // insercion del registro
            registro.getCampos().remove(cNombreEnfermedad);
            registro.getCampos()
                            .remove(ExamenMedicosControladorEnum.NOMBRETIPOEXAMEN
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());
            // Se adicionana los campos llave del registro
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(cSucursal, sucursal);
            registro.getCampos().put(
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            idEmpleado);
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "NAT_ENFERMEDAD_PERSONAL",
                            "COMPANIA = ''" + compania
                                + "'' AND NUMERO_DCTO = ''"
                                + retornarString(registro, cNumeroDcto)
                                + "'' ",
                            "CONSECUTIVO",
                            "1");
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombreEnfermedad);
        registro.getCampos()
                        .remove(ExamenMedicosControladorEnum.NOMBRETIPOEXAMEN
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return Si el proceso de actualizacion fue exitoso
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreCompleto
     * 
     * @return nombreCompleto
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Asigna la variable nombreCompleto
     * 
     * @param nombreCompleto
     * Variable a asignar en nombreCompleto
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoExamen
     * 
     * @return listaTipoExamen
     */
    public List<Registro> getListaTipoExamen() {
        return listaTipoExamen;
    }

    /**
     * Asigna la lista listaTipoExamen
     * 
     * @param listaTipoExamen
     * Variable a asignar en listaTipoExamen
     */
    public void setListaTipoExamen(List<Registro> listaTipoExamen) {
        this.listaTipoExamen = listaTipoExamen;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaNombreEnfermedad
     * 
     * @return listaNombreEnfermedad
     */
    public RegistroDataModelImpl getListaNombreEnfermedad() {
        return listaNombreEnfermedad;
    }

    /**
     * Asigna la lista listaNombreEnfermedad
     * 
     * @param listaNombreEnfermedad
     * Variable a asignar en listaNombreEnfermedad
     */
    public void setListaNombreEnfermedad(
        RegistroDataModelImpl listaNombreEnfermedad) {
        this.listaNombreEnfermedad = listaNombreEnfermedad;
    }

    /**
     * Retorna la lista listaNumeroDcto
     * 
     * @return listaNumeroDcto
     */
    public RegistroDataModelImpl getListaNumeroDcto() {
        return listaNumeroDcto;
    }

    /**
     * Asigna la lista listaNumeroDcto
     * 
     * @param listaNumeroDcto
     * Variable a asignar en listaNumeroDcto
     */
    public void setListaNumeroDcto(RegistroDataModelImpl listaNumeroDcto) {
        this.listaNumeroDcto = listaNumeroDcto;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
