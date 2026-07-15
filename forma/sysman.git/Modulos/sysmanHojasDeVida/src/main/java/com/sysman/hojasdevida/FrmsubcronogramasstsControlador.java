/*-
 * FrmsubcronogramasstsControlador.java
 *
 * 1.0
 * 
 * 02/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 02/01/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class FrmsubcronogramasstsControlador extends BeanBaseContinuoAcme
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaCmbRaci;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listacodActividad;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listacodActividadE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbIdEmpleado;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbIdEmpleadoE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbIdNit;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbIdNitE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbSucursal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaCmbSucursalE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmsubcronogramasstsControlador
     */
    public FrmsubcronogramasstsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 1557;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
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
    public void inicializar()
    {
        try
        {
            enumBase = GenericUrlEnum.SST_D_CRONOGRAMA;
            reasignarOrigen();
            buscarLlave();
            conectorPool.conectar(nombreConexion);
            registro = new Registro();
            // <CARGAR_LISTA>
            cargarListaCmbRaci();
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListacodActividad();
            cargarListacodActividadE();
            cargarListaCmbIdEmpleado();
            cargarListaCmbIdEmpleadoE();
            cargarListaCmbIdNit();
            cargarListaCmbIdNitE();
            cargarListaCmbSucursal();
            cargarListaCmbSucursalE();
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (NamingException | SQLException ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                conectorPool.getConection().close();
            }
            catch (SQLException ex)
            {
                // TODO Auto-generated catch block
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
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // parametrosListado.put("NUMERO", numero);
        // parametrosListado.put("ACTIVIDAD", actividad);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCmbRaci
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbRaci()
    {
        listaCmbRaci = service.getListado(conectorPool, "SELECT " +
            "     SST_TIPORACI.CODIGO, " +
            "     SST_TIPORACI.NOMBRE " +
            " FROM " +
            "     SST_TIPORACI " +
            " ORDER BY " +
            "     SST_TIPORACI.CODIGO " +
            " ");
    }

    /**
     * 
     * Carga la lista listacodActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacodActividad()
    {
        listacodActividad = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5176", "SELECT " +
            "     SST_TIPO_ACTIVIDAD.CODIGO, " +
            "     SST_TIPO_ACTIVIDAD.NOMBRE, " +
            "     SST_TIPO_ACTIVIDAD.TIPO_TRANSACCION " +
            " FROM " +
            "     SST_TIPO_ACTIVIDAD " +
            " WHERE " +
            "     (((SST_TIPO_ACTIVIDAD.COMPANIA) = '" + compania + "')) " +
            " ORDER BY " +
            "     SST_TIPO_ACTIVIDAD.CODIGO " +
            " ", true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listacodActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListacodActividadE()
    {
        listacodActividadE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5176", "SELECT " +
            "     SST_TIPO_ACTIVIDAD.CODIGO, " +
            "     SST_TIPO_ACTIVIDAD.NOMBRE, " +
            "     SST_TIPO_ACTIVIDAD.TIPO_TRANSACCION " +
            " FROM " +
            "     SST_TIPO_ACTIVIDAD " +
            " WHERE " +
            "     (((SST_TIPO_ACTIVIDAD.COMPANIA) = '" + compania + "')) " +
            " ORDER BY " +
            "     SST_TIPO_ACTIVIDAD.CODIGO " +
            " ", true, "CODIGO");
    }

    /**
     * 
     * Carga la lista listaCmbIdEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbIdEmpleado()
    {
        listaCmbIdEmpleado = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5177", "SELECT " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.SUCURSAL, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "ID_DE_EMPLEADO");
    }

    /**
     * 
     * Carga la lista listaCmbIdEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbIdEmpleadoE()
    {
        listaCmbIdEmpleadoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5177", "SELECT " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.SUCURSAL, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "ID_DE_EMPLEADO");
    }

    /**
     * 
     * Carga la lista listaCmbIdNit
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbIdNit()
    {
        listaCmbIdNit = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5178", "SELECT " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     PERSONAL.SUCURSAL, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "NUMERO_DCTO");
    }

    /**
     * 
     * Carga la lista listaCmbIdNit
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbIdNitE()
    {
        listaCmbIdNitE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5178", "SELECT " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     PERSONAL.SUCURSAL, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "NUMERO_DCTO");
    }

    /**
     * 
     * Carga la lista listaCmbSucursal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbSucursal()
    {
        listaCmbSucursal = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5179", "SELECT " +
            "     PERSONAL.SUCURSAL, " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "SUCURSAL");
    }

    /**
     * 
     * Carga la lista listaCmbSucursal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCmbSucursalE()
    {
        listaCmbSucursalE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FRFR1557:TBCB5179", "SELECT " +
            "     PERSONAL.SUCURSAL, " +
            "     PERSONAL.NUMERO_DCTO, " +
            "     PERSONAL.ID_DE_EMPLEADO, " +
            "     (NOMBRES || '  ' || APELLIDO1 || '  ' || APELLIDO2 ) NOMBRES1 " +
            " FROM " +
            "     PERSONAL " +
            " ", true, "SUCURSAL");
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
     * listacodActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodActividad(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_ACTIVIDAD", registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodActividad
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodActividadE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_EMPLEADO", registroAux.getCampos().get("ID_DE_EMPLEADO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdEmpleado
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdEmpleadoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("ID_DE_EMPLEADO");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdNit
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdNit(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CEDULA", registroAux.getCampos().get("NUMERO_DCTO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbIdNit
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbIdNitE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NUMERO_DCTO");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbSucursal
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbSucursal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("SUCURSAL", registroAux.getCampos().get("SUCURSAL"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbSucursal
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbSucursalE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("SUCURSAL");
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1557-ANTES_ACTUALIZAR Private Sub
         * Form_BeforeUpdate(Cancel As Integer) Dim strsql As String
         * Dim rs As DAO.Recordset Dim db As DAO.Database Dim
         * Consecutivo As Double Set db = CurrentDb() ' 'On Error
         * Resume Next ' ' ' ' If IsNull(Me!txtAno) Or
         * IsNull(Me!TxtDescripcion) Or IsNull(Me!TxtFecha) _ ' Or
         * IsNull(Me!CmbTercero) Or IsNull(Me!CmbTipoCpptal) Or
         * IsNull(Me!CbmNComPptal) Or _ ' IsNull(Me!CbmTComCnt) Or
         * IsNull(Me!CbmNComCnt) Or IsNull(Me!TxtVlrLegalizado) Then '
         * MsgBox "Faltan datos por ingresar", vbInformation,
         * "Sysman Software" ' Cancel = True ' Exit Sub ' Else ' ' If
         * Me.NewRecord Then ' STRSQL =
         * " SELECT TOP 1 sst_d_transacciones.CONSECUTIVO AS consec" &
         * _ ' " From sst_d_transacciones" & _ '
         * " WHERE sst_d_transacciones.[Compania] = '" & Getcompany()
         * & "'" & _ ' " AND TIPO_TRANSACCION = " & Me.Txt_Trans2 & ""
         * & _ ' " AND CONSECUTIVO =" & Me.TxtNumero & "" & _ '
         * " ORDER BY sst_d_transacciones.CONSECUTIVO DESC" ' ' Set RS
         * = DB.OpenRecordset(STRSQL) ' ' If Not RS.EOF Then '
         * Consecutivo = Nz(RS!consec, 0) + 1 ' Else ' Consecutivo = 1
         * ' End If ' Me!TxtCONSECUTIVO = Consecutivo ' End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1557-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * Dim db As DAO.Database Set db = CurrentDb() 'If Not
         * Me.NewRecord Then ' If Me!txtAno <> Anio Or
         * Me!TxtDescripcion <> Descripcion2 Or Me!TxtFecha <>
         * Fechaingreso2 _ ' Or Me!CmbTercero <> TERCERO2 Or
         * Me!CmbTipoCpptal <> Tipo_Com_Pptal2 Or Me!CbmNComPptal <>
         * N_Com_Pptal2 _ ' Or Me!CbmTComCnt <> Tipo_Com_Cnt2 Or
         * Me!CbmNComCnt <> N_Com_Cnt2 Or Me!TxtVlrLegalizado <>
         * ValorLegalizado2 Then ' ' DB.Execute
         * " UPDATE  LEGALIZACION_VIATICOS SET MODIFIED_BY = '" &
         * GetUser() & "',DATE_MODIFIED=#" & Date &
         * "# WHERE COMPANIA = '" & Getcompany() &
         * "'  AND  NUMERO  = " &
         * Forms!FRM_LEGALIZACION_VIATICOS!Numero & " AND ANO = " &
         * Forms!FRM_LEGALIZACION_VIATICOS!Ano & " " ' ' End If 'End
         * If 'GuardarRegistro 'Me.Refresh End Sub
         */
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
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

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCmbRaci
     * 
     * @return listaCmbRaci
     */
    public List<Registro> getListaCmbRaci()
    {
        return listaCmbRaci;
    }

    /**
     * Asigna la lista listaCmbRaci
     * 
     * @param listaCmbRaci
     * Variable a asignar en listaCmbRaci
     */
    public void setListaCmbRaci(List<Registro> listaCmbRaci)
    {
        this.listaCmbRaci = listaCmbRaci;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodActividad
     * 
     * @return listacodActividad
     */
    public RegistroDataModel getListacodActividad()
    {
        return listacodActividad;
    }

    /**
     * Asigna la lista listacodActividad
     * 
     * @param listacodActividad
     * Variable a asignar en listacodActividad
     */
    public void setListacodActividad(RegistroDataModel listacodActividad)
    {
        this.listacodActividad = listacodActividad;
    }

    /**
     * Retorna la lista listacodActividad
     * 
     * @return listacodActividad
     */
    public RegistroDataModel getListacodActividadE()
    {
        return listacodActividadE;
    }

    /**
     * Asigna la lista listacodActividad
     * 
     * @param listacodActividad
     * Variable a asignar en listacodActividad
     */
    public void setListacodActividadE(RegistroDataModel listacodActividadE)
    {
        this.listacodActividadE = listacodActividadE;
    }

    /**
     * Retorna la lista listaCmbIdEmpleado
     * 
     * @return listaCmbIdEmpleado
     */
    public RegistroDataModel getListaCmbIdEmpleado()
    {
        return listaCmbIdEmpleado;
    }

    /**
     * Asigna la lista listaCmbIdEmpleado
     * 
     * @param listaCmbIdEmpleado
     * Variable a asignar en listaCmbIdEmpleado
     */
    public void setListaCmbIdEmpleado(RegistroDataModel listaCmbIdEmpleado)
    {
        this.listaCmbIdEmpleado = listaCmbIdEmpleado;
    }

    /**
     * Retorna la lista listaCmbIdEmpleado
     * 
     * @return listaCmbIdEmpleado
     */
    public RegistroDataModel getListaCmbIdEmpleadoE()
    {
        return listaCmbIdEmpleadoE;
    }

    /**
     * Asigna la lista listaCmbIdEmpleado
     * 
     * @param listaCmbIdEmpleado
     * Variable a asignar en listaCmbIdEmpleado
     */
    public void setListaCmbIdEmpleadoE(RegistroDataModel listaCmbIdEmpleadoE)
    {
        this.listaCmbIdEmpleadoE = listaCmbIdEmpleadoE;
    }

    /**
     * Retorna la lista listaCmbIdNit
     * 
     * @return listaCmbIdNit
     */
    public RegistroDataModel getListaCmbIdNit()
    {
        return listaCmbIdNit;
    }

    /**
     * Asigna la lista listaCmbIdNit
     * 
     * @param listaCmbIdNit
     * Variable a asignar en listaCmbIdNit
     */
    public void setListaCmbIdNit(RegistroDataModel listaCmbIdNit)
    {
        this.listaCmbIdNit = listaCmbIdNit;
    }

    /**
     * Retorna la lista listaCmbIdNit
     * 
     * @return listaCmbIdNit
     */
    public RegistroDataModel getListaCmbIdNitE()
    {
        return listaCmbIdNitE;
    }

    /**
     * Asigna la lista listaCmbIdNit
     * 
     * @param listaCmbIdNit
     * Variable a asignar en listaCmbIdNit
     */
    public void setListaCmbIdNitE(RegistroDataModel listaCmbIdNitE)
    {
        this.listaCmbIdNitE = listaCmbIdNitE;
    }

    /**
     * Retorna la lista listaCmbSucursal
     * 
     * @return listaCmbSucursal
     */
    public RegistroDataModel getListaCmbSucursal()
    {
        return listaCmbSucursal;
    }

    /**
     * Asigna la lista listaCmbSucursal
     * 
     * @param listaCmbSucursal
     * Variable a asignar en listaCmbSucursal
     */
    public void setListaCmbSucursal(RegistroDataModel listaCmbSucursal)
    {
        this.listaCmbSucursal = listaCmbSucursal;
    }

    /**
     * Retorna la lista listaCmbSucursal
     * 
     * @return listaCmbSucursal
     */
    public RegistroDataModel getListaCmbSucursalE()
    {
        return listaCmbSucursalE;
    }

    /**
     * Asigna la lista listaCmbSucursal
     * 
     * @param listaCmbSucursal
     * Variable a asignar en listaCmbSucursal
     */
    public void setListaCmbSucursalE(RegistroDataModel listaCmbSucursalE)
    {
        this.listaCmbSucursalE = listaCmbSucursalE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
