/*-
 * FuncionesEspecificasControlador.java
 *
 * 1.0
 * 
 * 27/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario que registra el
 * manual de funciones especificas en Access "HvFuncionesEspecificas"
 * y "FuncionesEspecificas", el cual es llamado desde Hojas de
 * Vida\Aplicaciones\Carrera Administrativa\Archivos\Manual de
 * funciones especificas
 *
 * 
 * @version 1.0, 27/02/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class FuncionesEspecificasControlador extends BeanBaseContinuoAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NUMERO_DCTO en el formulario, almacena el
     * texto NUMERO_DCTO el cual es un campo del registro
     */
    private final String cNumeroDcto;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de cedula seleccionado en el
     * formulario
     */
    private String cedula;
    /**
     * Atributo que almacena el codigo del cargo seleccionado en el
     * formulario
     */
    private String cargo;
    /**
     * Atributo que almacena la informaci�n realcionada con el cargo,
     * incluye el jefe inmediato y la naturaleza
     */
    private String denominacion;
    /**
     * Atributo que almacena el nombre del numero de cedula
     * seleccionado en el formulario
     */
    private String nombre;
    /**
     * Atributo que almacena el grado asociado al cargo seleccionado
     * en el formulario
     */
    private String grado;
    /**
     * Atributo que almacena la fecha de creacion del cargo
     * seleccionado en el formulario
     */
    private Date fechaCreacion;
    /**
     * Atributo que almacena la sucursal a la que pertenece el numero
     * de cedula seleccionado
     */
    private String sucursal;
    /**
     * Atributo que almacena el identificador del numero de cedula
     * seleccionado en el formulario
     */
    private String idEmpleado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de cedula
     */
    private RegistroDataModel listaCedula;
    /**
     * Listado de registros para el comboBox de cedula que se
     * despliega al editar un registro
     */
    private RegistroDataModel listaCedulaE;
    /**
     * Listado de registros para el comboBox de cargo
     */
    private RegistroDataModel listaCargo;
    /**
     * Listado de registros para el comboBox de cargo que se despliega
     * al editar un registro
     */
    private RegistroDataModel listaCargoE;
    /**
     * Listado de registros para el comboBox de Area o dependencias
     */
    private RegistroDataModel listaArea;
    /**
     * Listado de registros para el comboBox de Area o dependencias
     * que se despliega al editar un registro
     */
    private RegistroDataModel listaAreaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FuncionesEspecificasControlador
     */
    public FuncionesEspecificasControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        cNumeroDcto = "NUMERO_DCTO";
        try {
            numFormulario = 1313;
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
        try {
            tabla = "NAT_FUNCIONESESPECIFICAS";
            reasignarOrigen();
            buscarLlave();
            conectorPool.conectar(nombreConexion);
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListaCedula();
            cargarListaCedulaE();
            cargarListaCargo();
            cargarListaCargoE();
            cargarListaArea();
            cargarListaAreaE();
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (NamingException | SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        origenDatos = "SELECT NAT_FUNCIONESESPECIFICAS.COMPANIA," +
            "     NAT_FUNCIONESESPECIFICAS.NUMERO_DCTO," +
            "     NAT_FUNCIONESESPECIFICAS.SUCURSAL," +
            "     NAT_FUNCIONESESPECIFICAS.ID_DE_EMPLEADO," +
            "     NAT_FUNCIONESESPECIFICAS.FECHACREACION," +
            "     NAT_FUNCIONESESPECIFICAS.CODIGO," +
            "     NAT_FUNCIONESESPECIFICAS.GRADO," +
            "     NAT_FUNCIONESESPECIFICAS.IDFUNCION," +
            "     NAT_FUNCIONESESPECIFICAS.FECHAMODIFICACIONFUNCIONES," +
            "     NAT_FUNCIONESESPECIFICAS.AREA, " +
            "     DEPENDENCIA.NOMBRE NOMBREAREA, " +
            "     NAT_FUNCIONESESPECIFICAS.FUNCION" +
            " FROM NAT_FUNCIONESESPECIFICAS" +
            "   LEFT JOIN DEPENDENCIA" +
            "      ON DEPENDENCIA.COMPANIA = NAT_FUNCIONESESPECIFICAS.COMPANIA"
            +
            "     AND DEPENDENCIA.CODIGO   = NAT_FUNCIONESESPECIFICAS.AREA " +
            " WHERE  NAT_FUNCIONESESPECIFICAS.COMPANIA    = '" + compania + "' "
            +
            "     AND NAT_FUNCIONESESPECIFICAS.NUMERO_DCTO   = '" + cedula
            + "' " +
            "     AND NAT_FUNCIONESESPECIFICAS.FECHACREACION = "
            + SysmanFunciones.formatearFecha(fechaCreacion) + " " +
            "     AND NAT_FUNCIONESESPECIFICAS.CODIGO        = '" + cargo + "' "
            +
            "     AND NAT_FUNCIONESESPECIFICAS.GRADO         = '" + grado
            + "' ";

        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCedula
     * 
     */
    public void cargarListaCedula() {
        listaCedula = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4316", "SELECT DISTINCT  " +
                            "     NAT_DATOS_PERSONALES.NUMERO_DCTO, " +
                            "     (APELLIDO1 || ' ' || APELLIDO2 || ' ' || NOMBRES ) NOMBRECOMPLETO, "
                            +
                            "     SUCURSAL," +
                            "     CODIGO " +
                            " FROM " +
                            "     NAT_DATOS_PERSONALES",
                        true, cNumeroDcto);
    }

    /**
     * 
     * Carga la lista listaCedula
     *
     */
    public void cargarListaCedulaE() {
        listaCedulaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4316", "SELECT DISTINCT   " +
                            "     NAT_DATOS_PERSONALES.NUMERO_DCTO, " +
                            "     (APELLIDO1 || ' ' || APELLIDO2 || ' ' || NOMBRES ) NOMBRECOMPLETO "
                            +
                            "     SUCURSAL," +
                            "     CODIGO " +
                            " FROM " +
                            "     NAT_DATOS_PERSONALES",
                        true, cNumeroDcto);
    }

    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {
        listaCargo = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4317", "SELECT DISTINCT " +
                            "   HVCARGOS.CODIGO," +
                            "   HVCARGOS.GRADO," +
                            "   HVCARGOS.DENOMINACION," +
                            "   HVCARGOS.JEFEINMEDIATO," +
                            "   HVCARGOS.NATURALEZA," +
                            "   HVCARGOS.FECHACREACION" +
                            " FROM NAT_HVCARGOS HVCARGOS",
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargoE() {
        listaCargoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4317", "SELECT DISTINCT " +
                            "   HVCARGOS.CODIGO," +
                            "   HVCARGOS.GRADO," +
                            "   HVCARGOS.DENOMINACION," +
                            "   HVCARGOS.JEFEINMEDIATO," +
                            "   HVCARGOS.NATURALEZA," +
                            "   HVCARGOS.FECHACREACION" +
                            " FROM NAT_HVCARGOS HVCARGOS",
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaArea
     *
     */
    public void cargarListaArea() {
        listaArea = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4319", "SELECT DEPENDENCIA.CODIGO, " +
                            "             DEPENDENCIA.NOMBRE " +
                            " FROM DEPENDENCIA",
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaArea
     *
     */
    public void cargarListaAreaE() {
        listaAreaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
                        ":FRFR1313:TBCB4319", "SELECT DEPENDENCIA.CODIGO, " +
                            "             DEPENDENCIA.NOMBRE " +
                            " FROM DEPENDENCIA",
                        true, cCodigo);
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
     * listaCedula
     *
     * Al seleccionar un numero de documento se obtiene adicionalmente
     * el nombre, el id que identifica a ese colaborador y la sucursal
     * a la cual esta asociado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedula(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cedula = registroAux.getCampos().get(cNumeroDcto).toString();
        nombre = registroAux.getCampos().get("NOMBRECOMPLETO").toString();
        // Pendiente por definir si el CODIGO que se trae de la tabla
        // NAT_DATOS_PERSONALES es el mismo ID_DE_EMPLEADO de la tabla
        // PERSONAL
        idEmpleado = registroAux.getCampos().get(cCodigo).toString();
        sucursal = registroAux.getCampos().get("SUCURSAL").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCedula
     *
     * Se ejecuta cuando se realiza la actualizacion de un registo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCedulaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cNumeroDcto);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     * Mediante la seleccion del cargo se obtienen los valores de
     * grado, fecha de creacion del cargo, su jefe inmediato y su
     * naturaleza, Se realiza la validaci�n de campos vacios
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cargo = registroAux.getCampos().get(cCodigo).toString();
        grado = denominacion = null;
        grado = registroAux.getCampos().get("GRADO").toString();
        fechaCreacion = SysmanFunciones.truncarFecha(
                        (Date) registroAux.getCampos()
                                        .get("FECHACREACION"));
        try {
            String auxDenominacion = registroAux.getCampos()
                            .get("DENOMINACION") != null
                                ? registroAux.getCampos().get("DENOMINACION")
                                                .toString()
                                : " ";

            String fecha = SysmanFunciones
                            .convertirAFechaCadena(fechaCreacion) != null
                                ? SysmanFunciones.convertirAFechaCadena(
                                                fechaCreacion)
                                : " ";
            String depende = registroAux.getCampos()
                            .get("JEFEINMEDIATO") != null
                                ? registroAux.getCampos().get("JEFEINMEDIATO")
                                                .toString()
                                : " ";
            String naturaleza = registroAux.getCampos()
                            .get("NATURALEZA") != null
                                ? registroAux.getCampos().get("NATURALEZA")
                                                .toString()
                                : "";
            denominacion = auxDenominacion
                + "(" + fecha + "). \n"
                + "Depende de: " + depende + "\n"
                + "Naturaleza: " + naturaleza + " ";
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().put("FECHAMODIFICACIONFUNCIONES", new Date());
        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     * Se ejecuta cuando se realiza la actualizacion de un registo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaArea
     *
     * Adiciona al registro el identificador del area aeleccionada
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaArea(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AREA", registroAux.getCampos().get(cCodigo));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaArea
     *
     * Se ejecuta cuando se realiza la actualizacion de un registo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAreaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigo);
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
     * 
     * Recarga los valores de la grilla
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * Se adiciona en el registro los valores necesarios para realizar
     * la insercion en la tabla NAT_FUNCIONESESPECIFICAS
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        try {
            int idFuncion = (int) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
                            "UN_TABLA => 'NAT_FUNCIONESESPECIFICAS'," +
                                " UN_CRITERIO => 'COMPANIA = ''" + compania
                                + "'''," +
                                " UN_CAMPO    => 'IDFUNCION'," +
                                " UN_INICIAL  => '1'",
                            Types.INTEGER);
            registro.getCampos().put("IDFUNCION", idFuncion);

        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(cNumeroDcto, cedula);
        registro.getCampos().put(cCodigo, cargo);
        registro.getCampos().put("GRADO", grado);
        registro.getCampos().put("FECHACREACION", fechaCreacion);
        registro.getCampos().put("FECHAMODIFICACIONFUNCIONES", new Date());
        registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
        registro.getCampos().put("SUCURSAL", sucursal);
        registro.getCampos().put("CREATED_BY",
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put("DATE_CREATED", new Date());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove("NOMBREAREA");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        //
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cedula
     * 
     * @return cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Asigna la variable cedula
     * 
     * @param cedula
     * Variable a asignar en cedula
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
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
     * Retorna la variable denominacion
     * 
     * @return denominacion
     */
    public String getDenominacion() {
        return denominacion;
    }

    /**
     * Asigna la variable denominacion
     * 
     * @param denominacion
     * Variable a asignar en denominacion
     */
    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable grado
     * 
     * @return grado
     */
    public String getGrado() {
        return grado;
    }

    /**
     * Asigna la variable grado
     * 
     * @param grado
     * Variable a asignar en grado
     */
    public void setGrado(String grado) {
        this.grado = grado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Retorna la variable sucursal
     * 
     * @return sucursal
     */
    public String getSucursal() {
        return sucursal;
    }

    /**
     * Asigna la variable sucursal
     * 
     * @param sucursal
     * Variable a asignar en sucursal
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * Retorna la variable idEmpleado
     * 
     * @return idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * Asigna la variable idEmpleado
     * 
     * @param idEmpleado
     * Variable a asignar en idEmpleado
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModel getListaCedula() {
        return listaCedula;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedula(RegistroDataModel listaCedula) {
        this.listaCedula = listaCedula;
    }

    /**
     * Retorna la lista listaCedula
     * 
     * @return listaCedula
     */
    public RegistroDataModel getListaCedulaE() {
        return listaCedulaE;
    }

    /**
     * Asigna la lista listaCedula
     * 
     * @param listaCedula
     * Variable a asignar en listaCedula
     */
    public void setListaCedulaE(RegistroDataModel listaCedulaE) {
        this.listaCedulaE = listaCedulaE;
    }

    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModel getListaCargo() {
        return listaCargo;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargo(RegistroDataModel listaCargo) {
        this.listaCargo = listaCargo;
    }

    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModel getListaCargoE() {
        return listaCargoE;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargoE(RegistroDataModel listaCargoE) {
        this.listaCargoE = listaCargoE;
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

    /**
     * Retorna la lista listaArea
     * 
     * @return listaArea
     */
    public RegistroDataModel getListaArea() {
        return listaArea;
    }

    /**
     * Asigna la lista listaArea
     * 
     * @param listaArea
     * Variable a asignar en listaArea
     */
    public void setListaArea(RegistroDataModel listaArea) {
        this.listaArea = listaArea;
    }

    /**
     * Retorna la lista listaArea
     * 
     * @return listaArea
     */
    public RegistroDataModel getListaAreaE() {
        return listaAreaE;
    }

    /**
     * Asigna la lista listaArea
     * 
     * @param listaArea
     * Variable a asignar en listaArea
     */
    public void setListaAreaE(RegistroDataModel listaAreaE) {
        this.listaAreaE = listaAreaE;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
