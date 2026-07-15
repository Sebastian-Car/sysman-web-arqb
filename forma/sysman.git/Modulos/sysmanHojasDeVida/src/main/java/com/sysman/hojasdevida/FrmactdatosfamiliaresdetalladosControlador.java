/*-
 * FrmactdatosfamiliaresdetalladosControlador.java
 *
 * 1.0
 *
 * 26/03/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmactdatosfamiliaresdetalladosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/03/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class FrmactdatosfamiliaresdetalladosControlador
                extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    // <DECLARAR_ATRIBUTOS>
    private final String compania;
    private String idEmpleado;

    private String consecutivo;

    private String nombreEmpleado;
    private String sucursal;
    private String cedula;

    private String sucursalEmpleado;
    private String identificacion;
    /**
     * Variable que almacena el nombre del pais actualmente registrado par el usuario.
     */
    private String paisActual;
    /**
     * Variable que almacena el nombre del departamento actualmente registrado para el usuario.
     */
    private String departamentoActual;
    /**
     * Variable que almacena el nombre de la ciudad regi
     */
    private String ciudadActual;
    private boolean actualizado;

    /**
     * Variable que almacena el nombre de la ciudad regi
     */
    private String tipoS;
    private boolean bloqImprimir;
    private boolean bloqEstado;
    private boolean bloqCampos;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listacmbTipoDocumento;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listacmbParentesco;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadrocombinado1071;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadrocombinado1072;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadrocombinado1237;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCuadrocombinado1238;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listacmbNIdentificacion;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCuadrocombinado1066;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCuadrocombinado1232;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmactdatosfamiliaresdetalladosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmactdatosfamiliaresdetalladosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 1653;
            validarPermisos();
            registro = new Registro();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                accion = parametrosEntrada.get("ACCION").toString();
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                idEmpleado = SysmanFunciones
                                .nvl(parametrosEntrada.get("ID_DE_EMPLEADO"),
                                                "")
                                .toString();
                consecutivo = SysmanFunciones
                                .nvl(parametrosEntrada.get("CONSECUTIVO"), "")
                                .toString();
                nombreEmpleado = SysmanFunciones.nvl(
                                parametrosEntrada.get("NOMBREEMPLEADO"), "")
                                .toString();

                sucursal = parametrosEntrada.get("SUCURSAL").toString();

                cedula = SysmanFunciones
                                .nvl(parametrosEntrada.get("CEDULA"), "")
                                .toString();

                sucursalEmpleado = SysmanFunciones
                                .nvl(parametrosEntrada.get("SUCURSAL"),
                                                "")
                                .toString();

                identificacion = SysmanFunciones
                                .nvl(parametrosEntrada.get("IDENTIFICACION"),
                                                "")
                                .toString();

                actualizado = (boolean) parametrosEntrada.get("ACTUALIZADO");

                tipoS = SysmanFunciones
                                .nvl(parametrosEntrada.get("TIPO"), "")
                                .toString();

            }
            else
            {
                throw new SysmanException(idioma.getString("TB_TB440"));
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbNIdentificacion();
        cargarListaCuadrocombinado1066();
        cargarListaCuadrocombinado1232();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbTipoDocumento();
        cargarListacmbParentesco();
        cargarListaCuadrocombinado1071();
        cargarListaCuadrocombinado1072();
        cargarListaCuadrocombinado1237();
        cargarListaCuadrocombinado1238();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.AUT_FAMILIARES;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbTipoDocumento
     */
    public void cargarListacmbTipoDocumento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listacmbTipoDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatosfamiliaresdetalladosControladorUrlEnum.URL4130
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listacmbParentesco
     *
     */
    public void cargarListacmbParentesco()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listacmbParentesco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatosfamiliaresdetalladosControladorUrlEnum.URL4131
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaCuadrocombinado1071
     *
     */
    public void cargarListaCuadrocombinado1071()
    {
        // listaCuadrocombinado1071 = service.getListado(conectorPool,
        // "SELECT DISTINCT " +
        // " DOCUMENTOS.DCTO_IDENTIDAD, " +
        // " DOCUMENTOS.DESCRIPCION, " +
        // " DOCUMENTOS.COMPANIA " +
        // " FROM " +
        // " DOCUMENTOS " +
        // " WHERE " +
        // " (((DOCUMENTOS.COMPANIA) = '" + compania
        // + "')) " +
        // " ");
    }

    /**
     *
     * Carga la lista listaCuadrocombinado1072
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadrocombinado1072()
    {
        // listaCuadrocombinado1072 = service.getListado(conectorPool,
        // "SELECT DISTINCT " +
        // " PARENTESCO.PARENTESCO, " +
        // " PARENTESCO.DESCRIPCION, " +
        // " PARENTESCO.COMPANIA " +
        // " FROM " +
        // " PARENTESCO " +
        // " WHERE " +
        // " (((PARENTESCO.COMPANIA) = '" + compania
        // + "')) " +
        // " ");
    }

    /**
     *
     * Carga la lista listaCuadrocombinado1237
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadrocombinado1237()
    {
        // listaCuadrocombinado1237 = service.getListado(conectorPool,
        // "SELECT DISTINCT " +
        // " DOCUMENTOS.DCTO_IDENTIDAD, " +
        // " DOCUMENTOS.DESCRIPCION, " +
        // " DOCUMENTOS.COMPANIA " +
        // " FROM " +
        // " DOCUMENTOS " +
        // " WHERE " +
        // " (((DOCUMENTOS.COMPANIA) = '" + compania
        // + "')) " +
        // " ");
    }

    /**
     *
     * Carga la lista listaCuadrocombinado1238
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadrocombinado1238()
    {
        // listaCuadrocombinado1238 = service.getListado(conectorPool,
        // "SELECT DISTINCT " +
        // " PARENTESCO.PARENTESCO, " +
        // " PARENTESCO.DESCRIPCION, " +
        // " PARENTESCO.COMPANIA " +
        // " FROM " +
        // " PARENTESCO " +
        // " WHERE " +
        // " (((PARENTESCO.COMPANIA) = '" + compania
        // + "')) " +
        // " ");
    }

    /**
     *
     * Carga la lista listacmbNIdentificacion
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacmbNIdentificacion()
    {
        // listacmbNIdentificacion = new RegistroDataModel(
        // ConectorPool.ESQUEMA_SYSMAN, ":FR1653_nuevo:TBCB5531",
        // "SELECT " +
        // " IDENTIFICACION, " +
        // " DCTO_IDENTIDAD, " +
        // " PARENTESCO, " +
        // " NOMBRE, " +
        // " APELLIDO1, " +
        // " APELLIDO2, " +
        // " FECHANCTO, " +
        // " SEXO, " +
        // " EDAD, " +
        // " TELEFONO, " +
        // " PORCENTAJE, " +
        // " SALUD, " +
        // " POLIZA, " +
        // " OCUPACION, " +
        // " DIRECCION, " +
        // " OBSERVACIONES, " +
        // " ESTADO_ACTUAL " +
        // " FROM " +
        // " FAMILIARES " +
        // " WHERE " +
        // " (" +
        // " ((FAMILIARES.COMPANIA) = '" + compania
        // + "') " +
        // " AND " +
        // " ((FAMILIARES.ID_DE_EMPLEADO) = GETUSERID())"
        // +
        // " ) " +
        // " ",
        // true, "IDENTIFICACION");
    }

    /**
     *
     * Carga la lista listaCuadrocombinado1066
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadrocombinado1066()
    {
        // listaCuadrocombinado1066 = new RegistroDataModel(
        // ConectorPool.ESQUEMA_SYSMAN, ":FR1653_nuevo:TBCB5536",
        // "SELECT " +
        // " IDENTIFICACION, " +
        // " DCTO_IDENTIDAD, " +
        // " PARENTESCO, " +
        // " NOMBRE, " +
        // " APELLIDO1, " +
        // " APELLIDO2, " +
        // " FECHANCTO, " +
        // " SEXO, " +
        // " EDAD, " +
        // " TELEFONO, " +
        // " PORCENTAJE, " +
        // " SALUD, " +
        // " POLIZA, " +
        // " OCUPACION, " +
        // " DIRECCION, " +
        // " OBSERVACIONES, " +
        // " ESTADO_ACTUAL " +
        // " FROM " +
        // " FAMILIARES " +
        // " WHERE " +
        // " (" +
        // " ((FAMILIARES.COMPANIA) = '" + compania
        // + "') " +
        // " AND " +
        // " ((FAMILIARES.ID_DE_EMPLEADO) = GETUSERID())"
        // +
        // " ) " +
        // " ",
        // true, "IDENTIFICACION");
    }

    /**
     *
     * Carga la lista listaCuadrocombinado1232
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCuadrocombinado1232()
    {
        // listaCuadrocombinado1232 = new RegistroDataModel(
        // ConectorPool.ESQUEMA_SYSMAN, ":FR1653_nuevo:TBCB5541",
        // "SELECT " +
        // " IDENTIFICACION, " +
        // " DCTO_IDENTIDAD, " +
        // " PARENTESCO, " +
        // " NOMBRE, " +
        // " APELLIDO1, " +
        // " APELLIDO2, " +
        // " FECHANCTO, " +
        // " SEXO, " +
        // " EDAD, " +
        // " TELEFONO, " +
        // " PORCENTAJE, " +
        // " SALUD, " +
        // " POLIZA, " +
        // " OCUPACION, " +
        // " DIRECCION, " +
        // " OBSERVACIONES, " +
        // " ESTADO_ACTUAL " +
        // " FROM " +
        // " FAMILIARES " +
        // " WHERE " +
        // " (" +
        // " ((FAMILIARES.COMPANIA) = '" + compania
        // + "') " +
        // " AND " +
        // " ((FAMILIARES.ID_DE_EMPLEADO) = GETUSERID())"
        // +
        // " ) " +
        // " ",
        // true, "IDENTIFICACION");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listacmbNIdentificacion
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbNIdentificacion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("N_IDENTIFICACION",
                        registroAux.getCampos().get("IDENTIFICACION"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuadrocombinado1066
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadrocombinado1066(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("N_IDENTIFICACION",
                        registroAux.getCampos().get("IDENTIFICACION"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCuadrocombinado1232
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadrocombinado1232(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("IDENTIFICACION",
                        registroAux.getCampos().get("IDENTIFICACION"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton imprimir en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirimprimir()
    {
        // <CODIGO_DESARROLLADO>
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> llaveActual = new HashMap<>();
        if (ACCION_MODIFICAR.equals(accion))
        {
            llaveActual.put("KEY_COMPANIA", compania);
            llaveActual.put("KEY_CONSECUTIVO", consecutivo);
            llaveActual.put("KEY_DCTO_EMPLEADO", cedula);
            llaveActual.put("KEY_SUCURSAL_EMPLEADO", sucursalEmpleado);
            llaveActual.put("KEY_IDENTIFICACION", identificacion);
            llaveActual.put("KEY_SUCURSAL", sucursal);
            cargarRegistro(llaveActual, accion);
        }
        else
        {
            cargarRegistroNuevo();
        }

        iniciarListas();

        boolean estado = false;
        if ("S".equals(tipoS) && !actualizado)
        {
            estado = false;
            bloqImprimir = false;
        }
        else if ("A".equals(tipoS))
        {
            estado = true;
        }
        else if ("T".equals(tipoS) && !ACCION_INSERTAR.equals(accion)
            && !actualizado)
        {
            estado = false;
            bloqEstado = estado;
            bloqImprimir = false;
        }

        if (ACCION_INSERTAR.equals(accion))
        {
            estado = false;
            bloqImprimir = false;
            // Me!btn_Ruta.Enabled = True
        }

        if (actualizado)
        {
            estado = true;
            bloqEstado = true;
        }

        bloqCampos = estado;
        /*
         * FR1653-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim Estado As Boolean On Error Resume Next Me.cmbNIdentificacion.RowSource =
         * " SELECT IDENTIFICACION, DCTO_IDENTIDAD,  PARENTESCO, NOMBRE,  APELLIDO1,  APELLIDO2, FECHANCTO, SEXO,  EDAD, TELEFONO,  PORCENTAJE,  SALUD,  POLIZA, OCUPACION, DIRECCION, OBSERVACIONES, ESTADO_ACTUAL  "
         * & _ " FROM FAMILIARES " & _ " WHERE FAMILIARES.COMPANIA IN('" & Getcompany() & "') AND FAMILIARES.ID_DE_EMPLEADO IN('" & GetUserId() & "') " If Forms!PERSONAL_FAMILIAR!TIPOS = "S" And
         * Forms!PERSONAL_FAMILIAR!ACTUALIZADO = 0 Then Estado = False Me!Imprimir.Enabled = True ElseIf Forms!PERSONAL_FAMILIAR!TIPOS = "A" Then Estado = True ElseIf Forms!PERSONAL_FAMILIAR!TIPOS =
         * "T" And Not (Forms!Identificacion!N_Solicitud = 0) And Forms!PERSONAL_FAMILIAR!ACTUALIZADO = 0 Then Estado = False Me.Estado.Locked = Estado Me!Imprimir.Enabled = True End If If
         * Forms!Identificacion!N_Solicitud = 0 Then 'NUEVO Estado = False Forms!Identificacion!N_Solicitud = 0 Me.Undo Me.Requery Me!Imprimir.Enabled = True 'Me!btn_Ruta.Enabled = True End If If Not
         * Forms!PERSONAL_FAMILIAR!ACTUALIZADO = 0 Then Estado = True Me.Estado.Locked = Estado End If Me!cmbTipoDocumento.Locked = Estado 'Me!cmbNIdentificacion.Locked = Estado
         * Me!cmbParentesco.Locked = Estado Me!txtNombre.Locked = Estado Me!txtApellidoUno.Locked = Estado Me!txtApellidoDos.Locked = Estado Me!txtFECHAnacimiento.Locked = Estado Me!cmbSexo.Locked =
         * Estado Me!txtEdad.Locked = Estado Me!txtTelefono.Locked = Estado Me!txtPorcentaje.Locked = Estado Me!chkSalud.Locked = Estado Me!chkPoliza.Locked = Estado Me!txtOcupacion.Locked = Estado
         * Me!txtDireccion.Locked = Estado Me!cmbEstadoActual.Locked = Estado Me!txtObservaciones.Locked = Estado 'Me!txtRuta.Locked = Estado End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbTipoDocumento
     *
     * @return listacmbTipoDocumento
     */
    public List<Registro> getListacmbTipoDocumento()
    {
        return listacmbTipoDocumento;
    }

    /**
     * Asigna la lista listacmbTipoDocumento
     *
     * @param listacmbTipoDocumento
     * Variable a asignar en listacmbTipoDocumento
     */
    public void setListacmbTipoDocumento(List<Registro> listacmbTipoDocumento)
    {
        this.listacmbTipoDocumento = listacmbTipoDocumento;
    }

    /**
     * Retorna la lista listacmbParentesco
     *
     * @return listacmbParentesco
     */
    public List<Registro> getListacmbParentesco()
    {
        return listacmbParentesco;
    }

    /**
     * Asigna la lista listacmbParentesco
     *
     * @param listacmbParentesco
     * Variable a asignar en listacmbParentesco
     */
    public void setListacmbParentesco(List<Registro> listacmbParentesco)
    {
        this.listacmbParentesco = listacmbParentesco;
    }

    /**
     * Retorna la lista listaCuadrocombinado1071
     *
     * @return listaCuadrocombinado1071
     */
    public List<Registro> getListaCuadrocombinado1071()
    {
        return listaCuadrocombinado1071;
    }

    /**
     * Asigna la lista listaCuadrocombinado1071
     *
     * @param listaCuadrocombinado1071
     * Variable a asignar en listaCuadrocombinado1071
     */
    public void setListaCuadrocombinado1071(
        List<Registro> listaCuadrocombinado1071)
    {
        this.listaCuadrocombinado1071 = listaCuadrocombinado1071;
    }

    /**
     * Retorna la lista listaCuadrocombinado1072
     *
     * @return listaCuadrocombinado1072
     */
    public List<Registro> getListaCuadrocombinado1072()
    {
        return listaCuadrocombinado1072;
    }

    /**
     * Asigna la lista listaCuadrocombinado1072
     *
     * @param listaCuadrocombinado1072
     * Variable a asignar en listaCuadrocombinado1072
     */
    public void setListaCuadrocombinado1072(
        List<Registro> listaCuadrocombinado1072)
    {
        this.listaCuadrocombinado1072 = listaCuadrocombinado1072;
    }

    /**
     * Retorna la lista listaCuadrocombinado1237
     *
     * @return listaCuadrocombinado1237
     */
    public List<Registro> getListaCuadrocombinado1237()
    {
        return listaCuadrocombinado1237;
    }

    /**
     * Asigna la lista listaCuadrocombinado1237
     *
     * @param listaCuadrocombinado1237
     * Variable a asignar en listaCuadrocombinado1237
     */
    public void setListaCuadrocombinado1237(
        List<Registro> listaCuadrocombinado1237)
    {
        this.listaCuadrocombinado1237 = listaCuadrocombinado1237;
    }

    /**
     * Retorna la lista listaCuadrocombinado1238
     *
     * @return listaCuadrocombinado1238
     */
    public List<Registro> getListaCuadrocombinado1238()
    {
        return listaCuadrocombinado1238;
    }

    /**
     * Asigna la lista listaCuadrocombinado1238
     *
     * @param listaCuadrocombinado1238
     * Variable a asignar en listaCuadrocombinado1238
     */
    public void setListaCuadrocombinado1238(
        List<Registro> listaCuadrocombinado1238)
    {
        this.listaCuadrocombinado1238 = listaCuadrocombinado1238;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbNIdentificacion
     *
     * @return listacmbNIdentificacion
     */
    public RegistroDataModel getListacmbNIdentificacion()
    {
        return listacmbNIdentificacion;
    }

    /**
     * Asigna la lista listacmbNIdentificacion
     *
     * @param listacmbNIdentificacion
     * Variable a asignar en listacmbNIdentificacion
     */
    public void setListacmbNIdentificacion(
        RegistroDataModel listacmbNIdentificacion)
    {
        this.listacmbNIdentificacion = listacmbNIdentificacion;
    }

    /**
     * Retorna la lista listaCuadrocombinado1066
     *
     * @return listaCuadrocombinado1066
     */
    public RegistroDataModel getListaCuadrocombinado1066()
    {
        return listaCuadrocombinado1066;
    }

    /**
     * Asigna la lista listaCuadrocombinado1066
     *
     * @param listaCuadrocombinado1066
     * Variable a asignar en listaCuadrocombinado1066
     */
    public void setListaCuadrocombinado1066(
        RegistroDataModel listaCuadrocombinado1066)
    {
        this.listaCuadrocombinado1066 = listaCuadrocombinado1066;
    }

    /**
     * Retorna la lista listaCuadrocombinado1232
     *
     * @return listaCuadrocombinado1232
     */
    public RegistroDataModel getListaCuadrocombinado1232()
    {
        return listaCuadrocombinado1232;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCedula()
    {
        return cedula;
    }

    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public String getSucursal()
    {
        return sucursal;
    }

    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    public boolean isActualizado()
    {
        return actualizado;
    }

    public void setActualizado(boolean actualizado)
    {
        this.actualizado = actualizado;
    }

    public String getTipoS()
    {
        return tipoS;
    }

    public void setTipoS(String tipoS)
    {
        this.tipoS = tipoS;
    }

    public String getIdEmpleado()
    {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado)
    {
        this.idEmpleado = idEmpleado;
    }

    public String getPaisActual()
    {
        return paisActual;
    }

    public void setPaisActual(String paisActual)
    {
        this.paisActual = paisActual;
    }

    public String getDepartamentoActual()
    {
        return departamentoActual;
    }

    public void setDepartamentoActual(String departamentoActual)
    {
        this.departamentoActual = departamentoActual;
    }

    public String getCiudadActual()
    {
        return ciudadActual;
    }

    public void setCiudadActual(String ciudadActual)
    {
        this.ciudadActual = ciudadActual;
    }

    public boolean isBloqImprimir()
    {
        return bloqImprimir;
    }

    public void setBloqImprimir(boolean bloqImprimir)
    {
        this.bloqImprimir = bloqImprimir;
    }

    public boolean isBloqEstado()
    {
        return bloqEstado;
    }

    public void setBloqEstado(boolean bloqEstado)
    {
        this.bloqEstado = bloqEstado;
    }

    public boolean isBloqCampos()
    {
        return bloqCampos;
    }

    public void setBloqCampos(boolean bloqCampos)
    {
        this.bloqCampos = bloqCampos;
    }

    public String getSucursalEmpleado()
    {
        return sucursalEmpleado;
    }

    public void setSucursalEmpleado(String sucursalEmpleado)
    {
        this.sucursalEmpleado = sucursalEmpleado;
    }

    public String getIdentificacion()
    {
        return identificacion;
    }

    public void setIdentificacion(String identificacion)
    {
        this.identificacion = identificacion;
    }

    /**
     * Asigna la lista listaCuadrocombinado1232
     *
     * @param listaCuadrocombinado1232
     * Variable a asignar en listaCuadrocombinado1232
     */
    public void setListaCuadrocombinado1232(
        RegistroDataModel listaCuadrocombinado1232)
    {
        this.listaCuadrocombinado1232 = listaCuadrocombinado1232;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
