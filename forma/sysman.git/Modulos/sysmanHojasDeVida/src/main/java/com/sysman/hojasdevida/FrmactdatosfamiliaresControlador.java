/*-
 * FrmactdatosfamiliaresControlador.java
 *
 * 1.0
 *
 * 31/01/2018
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
import com.sysman.general.PlantillaswordsControlador;
import com.sysman.hojasdevida.enums.FrmactdatosfamiliaresControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 31/01/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactdatosfamiliaresControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String consecutivo;
    // private String idEmpleado;
    private String nombreEmpleado;

    private String cedula;

    private String sucursalEmpleado;
    private String identificacion;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String familiar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listacmbNIdentificacion;
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
    private List<Registro> listacmbFamiliar;

    private boolean bloquearArchivos;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */

    private List<Registro> listaFrmarchivos;

    private String sucursal;

    private String observaciones;

    private boolean enviado;
    private boolean estadoInterfaz;
    private boolean bloqueaGuardar;
    private boolean actualizado;

    private String tipoS;

    private int tipoArchivo;

    private String ruta;

    private UploadedFile archivoCarganuevoArchivo;

    private RegistroDataModelImpl listacmbTipoArchivo;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    @EJB
    EjbSysmanUtil ejbSysmanUtil;

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
     * Crea una nueva instancia de FrmactdatosfamiliaresControlador
     */
    public FrmactdatosfamiliaresControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 1647;
            validarPermisos();
            registro = new Registro();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {

                accion = parametrosEntrada.get("ACCION").toString();
                if (ACCION_MODIFICAR.equals(accion))
                {
                    consecutivo = parametrosEntrada.get("CONSECUTIVO")
                                    .toString();
                    // actualizado = (boolean) parametrosEntrada
                    // .get("ACTUALIZADO");
                    // enviado = (boolean) parametrosEntrada.get("ENVIADO");
                    // tipoS = parametrosEntrada.get("TIPO").toString();

                }

                // idEmpleado = SysmanFunciones.nvl(
                // parametrosEntrada.get("ID_DE_EMPLEADO"), "")
                // .toString();

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

                validarCampos();
            }
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void validarCampos()
    {

        if ("S".equals(tipoS))
        {
            estadoInterfaz = false;
            bloqueaGuardar = false;
            if (enviado)
            {
                estadoInterfaz = true;
                // Desactivar boton imprimir
                bloqueaGuardar = true;

                // Desactivar pestaña archivos
                bloquearArchivos = false;
            }
        }
        else if ("A".equals(tipoS))
        {
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;
        }
        else if ("T".equals(tipoS) && consecutivo != null)
        {

            if (enviado)
            {
                // desbloquear ESTADO
            }
            else
            {
                // bloquear ESTADO
            }
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;
        }

        if (!actualizado && consecutivo != null)
        {
            estadoInterfaz = true;
            // desactivar boton imprimir
            bloqueaGuardar = true;

            // desactivar pestaña archivos
            bloquearArchivos = false;

            if (!enviado)
            {
                estadoInterfaz = false;
                // Activar boton imprimir
                bloqueaGuardar = false;

                // Activar pestaña archivos
                bloquearArchivos = true;
            }
        }

        if (consecutivo == null && !actualizado)
        { // Es nuevo
            estadoInterfaz = false;
            // Activar boton imprimir
            bloqueaGuardar = false;

            // Activar pestaña archivos
            bloquearArchivos = true;
        }

        if (consecutivo == null)
        {
            bloquearArchivos = false;
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacmbNIdentificacion();
        cargarListacmbTipoDocumento();
        cargarListacmbParentesco();
        cargarListacmbFamiliar();
        cargarListacmbTipoArchivo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmarchivos();
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
     *
     * Carga la lista listaFrmarchivos
     *
     */
    public void cargarListaFrmarchivos()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("AUTORIZACION", consecutivo);
        // param.put("IDEMPLEADO", idEmpleado);

        try
        {
            listaFrmarchivos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "989001")
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "AUTPERSONAL_DOCUMENTO"));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * Carga la lista listacmbNIdentificacion
     *
     */
    public void cargarListacmbNIdentificacion()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactdatosfamiliaresControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("DCTO_EMPLEADO", SessionUtil.getUser().getCedula());
        listacmbNIdentificacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "IDENTIFICACION");
    }

    /**
     *
     * Carga la lista listacmbTipoDocumento
     *
     */
    public void cargarListacmbTipoDocumento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try
        {
            listacmbTipoDocumento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatosfamiliaresControladorUrlEnum.URL004
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
                                                            FrmactdatosfamiliaresControladorUrlEnum.URL003
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
     * Carga la lista listacmbFamiliar
     *
     */
    public void cargarListacmbFamiliar()
    {
        // listacmbFamiliar = service.getListado(conectorPool, "SELECT " +
        // " IDENTIFICACION," +
        // " NOMBRE || ' ' || APELLIDO1 || ' ' || APELLIDO2 NOMBRES" +
        // " FROM " +
        // " FAMILIARES " +
        // " WHERE " +
        // " (" +
        // " ((FAMILIARES.COMPANIA) = '" + compania + "') " +
        // " AND " +
        // " ((FAMILIARES.ID_DE_EMPLEADO) = GETUSERID())" +
        // " )" +
        // " UNION " +
        // " SELECT " +
        // " 'N' IDENTIFICACION, " +
        // " 'Nuevo' NUEVO" +
        // " FROM " +
        // " COMPANIA " +
        // " WHERE 1=1");
    }

    public void seleccionarFilacmbNIdentificacion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("IDENTIFICACION",
                        registroAux.getCampos().get("IDENTIFICACION"));
        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));

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
     * Metodo ejecutado al oprimir el boton btn_Ruta en la vista
     *
     *
     */
    public void oprimirbtn_Ruta()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        // agregarRegistroNuevo(false);
        // accion = ACCION_MODIFICAR;
        // bloquearArchivos = true;
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
        bloquearArchivos = true;
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

        /*
         * FR1647-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim Estado As Boolean On Error Resume Next Me.cmbNIdentificacion.RowSource =
         * " SELECT IDENTIFICACION, DCTO_IDENTIDAD,  PARENTESCO, NOMBRE,  APELLIDO1,  APELLIDO2, FECHANCTO, SEXO,  EDAD, TELEFONO,  PORCENTAJE,  SALUD,  POLIZA, OCUPACION, DIRECCION, OBSERVACIONES, ESTADO_ACTUAL  "
         * & _ " FROM FAMILIARES " & _ " WHERE FAMILIARES.COMPANIA IN('" & Getcompany() & "') AND FAMILIARES.ID_DE_EMPLEADO IN('" & GetUserId() & "') " If Forms!PERSONAL_FAMILIAR!TIPOS = "S" Then
         * Estado = False Me!Imprimir.Enabled = True If Not Me!Enviado = 0 Then Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False End If ElseIf Forms!PERSONAL_FAMILIAR!TIPOS = "A"
         * Then Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False ElseIf Forms!PERSONAL_FAMILIAR!TIPOS = "T" And Not (Forms!Identificacion!N_Solicitud = 0) Then Estado = False If
         * Not Me!Enviado = 0 Then Me.Estado.Locked = Estado Else Me.Estado.Locked = True End If Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False End If If
         * Forms!PERSONAL_FAMILIAR!ACTUALIZADO = 0 And Not (Forms!Identificacion!N_Solicitud = 0) Then Estado = True Me!Imprimir.Enabled = False Me!btn_Ruta.Enabled = False If Me!Enviado = 0 Then
         * Estado = False Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled = True End If End If If Forms!Identificacion!N_Solicitud = 0 And Forms!PERSONAL_FAMILIAR!ACTUALIZADO = 0 Then 'NUEVO Estado =
         * False Forms!Identificacion!N_Solicitud = 0 Me.Undo Me.Requery Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled = True Me!cmbFamiliar.visible = True Me!Etiqueta644.visible = True
         * Me.cmbNIdentificacion.RowSource =
         * " SELECT IDENTIFICACION, DCTO_IDENTIDAD,  PARENTESCO, NOMBRE,  APELLIDO1,  APELLIDO2, FECHANCTO, SEXO,  EDAD, TELEFONO,  PORCENTAJE,  SALUD,  POLIZA, OCUPACION, DIRECCION, OBSERVACIONES, ESTADO_ACTUAL FROM FAMILIARES WHERE 0=1"
         * End If Me!cmbTipoDocumento.Locked = Estado Me!cmbNIdentificacion.Locked = Estado Me!cmbParentesco.Locked = Estado Me!txtNombre.Locked = Estado Me!txtApellidoUno.Locked = Estado
         * Me!txtApellidoDos.Locked = Estado Me!txtFECHAnacimiento.Locked = Estado Me!cmbSexo.Locked = Estado Me!txtEdad.Locked = Estado Me!txtTelefono.Locked = Estado Me!txtPorcentaje.Locked = Estado
         * Me!chkSalud.Locked = Estado Me!chkPoliza.Locked = Estado Me!txtOcupacion.Locked = Estado Me!txtDireccion.Locked = Estado Me!cmbEstadoActual.Locked = Estado Me!txtObservaciones.Locked =
         * Estado Me!txtRuta.Locked = Estado End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        registro.getCampos().put("NOMBREEMPLEADO", nombreEmpleado);
        if (ACCION_INSERTAR.equals(accion))
        {
            try
            {
                String criterio = "COMPANIA = ''" + compania
                    + "'' AND DCTO_EMPLEADO = ''" + cedula + "''"
                    + " AND SUCURSAL_EMPLEADO = ''" + sucursalEmpleado + "''";

                consecutivo = String.valueOf(ejbSysmanUtil
                                .generarConsecutivoConValorInicial(
                                                enumBase.getTable(),
                                                criterio, "CONSECUTIVO", "1"));

                registro.getCampos().put("CONSECUTIVO", consecutivo);
                registro.getCampos().put("DCTO_EMPLEADO", cedula);
                // registro.getCampos().put("NUMERO_DCTO", cedula);
                registro.getCampos().put("ESTADO", "T");
                // llaveActual.put("SUCURSAL_EMPLEADO", "001");
                // llaveActual.put("KEY_IDENTIFICACION", "1026271017");
                // llaveActual.put("KEY_SUCURSAL", sucursal);
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro TODO DOCUMENTACION ADICIONAL
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ENVIADO", false);
        registro.getCampos().put("SUCURSAL_EMPLEADO", sucursalEmpleado);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro TODO DOCUMENTACION ADICIONAL
     *
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
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBREEMPLEADO");
        /*
         * FR1647-ANTES_ACTUALIZAR Private Sub Form_BeforeUpdate(Cancel As Integer) Dim Estado As Boolean Dim db As DAO.Database Dim strSql As String Set db = CurrentDb Estado = True If Me!Consecutivo
         * = 0 Then Me!Consecutivo = GenConsecutivo("AUT_FAMILIARES", " COMPANIA = '" & Getcompany() & "'  AND   ID_DE_EMPLEADO       = '" & GetUserId() & "' ", "CONSECUTIVO") strSql =
         * "   UPDATE AUT_FAMILIARES  " & _ "   SET DCTO_IDENTIDAD_A = DCTO_IDENTIDAD,  " If Me!cmbTipoSolicitud = "N" Then strSql = strSql & _ " N_IDENTIFICACION = IDENTIFICACION, " End If strSql =
         * strSql & _ "       PARENTESCO_A = PARENTESCO, " & _ "       NOMBRE_A = NOMBRE, " & _ "       APELLIDO1_A = APELLIDO1, " & _ "       APELLIDO2_A = APELLIDO2, " & _
         * "       FECHANCTO_A = FECHANCTO, " & _ "       SEXO_A = SEXO, " & _ "       EDAD_A = EDAD, " & _ "       TELEFONO_A = TELEFONO, " & _ "       PORCENTAJE_A = PORCENTAJE, " & _
         * "       SALUD_A = SALUD, " & _ "       POLIZA_A = POLIZA, " & _ "       OCUPACION_A = OCUPACION, " & _ "       DIRECCION_A = DIRECCION, " & _ "       ESTADO_ACTUAL_A = ESTADO_ACTUAL, " & _
         * "       OBSERVACIONES_A = OBSERVACIONES " & _ "  WHERE COMPANIA = '" & Me!txtCompania & "' " & _ "    AND CONSECUTIVO = " & Me!Consecutivo db.Execute strSql If Not Me!Enviado = 0 Then
         * Me!cmbTipoDocumento.Locked = Estado Me!cmbNIdentificacion.Locked = Estado Me!cmbParentesco.Locked = Estado Me!txtNombre.Locked = Estado Me!txtApellidoUno.Locked = Estado
         * Me!txtApellidoDos.Locked = Estado Me!txtFECHAnacimiento.Locked = Estado Me!cmbSexo.Locked = Estado Me!txtEdad.Locked = Estado Me!txtTelefono.Locked = Estado Me!txtPorcentaje.Locked = Estado
         * Me!chkSalud.Locked = Estado Me!chkPoliza.Locked = Estado Me!txtOcupacion.Locked = Estado Me!txtDireccion.Locked = Estado Me!cmbEstadoActual.Locked = Estado Me!txtObservaciones.Locked =
         * Estado Me!txtRuta.Locked = Estado End If End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
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
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     *
     * Carga la lista listacmbTipoArchivo
     *
     */
    public void cargarListacmbTipoArchivo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactdatosfamiliaresControladorUrlEnum.URL005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbTipoArchivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listacmbTipoArchivo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoArchivo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoArchivo = (int) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo de insercion del formulario Frmarchivos
     *
     */
    public void agregarRegistroSubFrmarchivos()
    {
        try
        {

            File archivoTemp = new File(ruta);

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("CODIGO_DOCUMENTO", tipoArchivo);
            registroSub.getCampos().put("AUTORIZACION", consecutivo);
            // registroSub.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
            registroSub.getCampos().put("OBSERVACION", observaciones);
            registroSub.getCampos().put("NOMBRE_ARCHIVO",
                            archivoTemp.getName());
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());

            Parameter params = new Parameter();
            params.setFields(registroSub.getCampos());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmactdatosfamiliaresControladorUrlEnum.URL006
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo de edicion del formulario Frmarchivos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmarchivos(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            "AUTPERSONAL_DOCUMENTO", reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaFrmarchivos();
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Frmarchivos
     *
     */
    public void cancelarEdicionFrmarchivos()
    {
        cargarListaFrmarchivos();
    }

    /**
     * Metodo ejecutado al oprimir el boton Comando50
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirComando50(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        String nombreArchivo = String
                        .valueOf(reg.getCampos().get("NOMBRE_ARCHIVO"));

        String rutaDes = JsfUtil.generarRuta(SessionUtil.getModulo(),
                        SessionUtil.getUser().getCedula(),
                        SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES,
                        nombreArchivo);

        File anexo = new File(rutaDes);
        try (InputStream fis = new FileInputStream(anexo))
        {

            byte[] vec = new byte[(int) anexo.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), anexo.getName());
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta("El archivo a descargar no existe");
        }
        catch (JRException | IOException ex)
        {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Comando2
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirComando2(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        eliminarRegSubFrmarchivos(reg);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de eliminacion del formulario Frmarchivos
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmarchivos(Registro reg)
    {

        try
        {
            Map<String, Object> llaveAct = new HashMap<>();

            llaveAct.put("KEY_COMPANIA", compania);
            llaveAct.put("KEY_CODIGO_DOCUMENTO", String
                            .valueOf(reg.getCampos().get("CODIGO_DOCUMENTO")));
            llaveAct.put("KEY_AUTORIZACION", consecutivo);
            // llaveAct.put("KEY_ID_DE_EMPLEADO", idEmpleado);

            Parameter params = new Parameter();
            params.setFields(registroSub.getCampos());

            UrlBean urlEliminacion = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmactdatosfamiliaresControladorUrlEnum.URL007
                                                            .getValue());

            requestManager.delete(urlEliminacion.getUrl(), llaveAct);
            cargarListaFrmarchivos();
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton btAdjuntar en la vista
     */
    public void oprimirbtAdjuntar()
    {
        // <CODIGO_DESARROLLADO>

        if (consecutivo != null)
        {

            if (tipoArchivo != 0)
            {

                Map<String, Object> llaveAct = new HashMap<>();

                llaveAct.put("KEY_COMPANIA", compania);
                llaveAct.put("KEY_CODIGO_DOCUMENTO", tipoArchivo);
                llaveAct.put("KEY_AUTORIZACION", consecutivo);
                // llaveAct.put("KEY_ID_DE_EMPLEADO", idEmpleado);

                String archivo = archivoCarganuevoArchivo.getFileName();

                if (!SysmanFunciones.validarVariableVacio(archivo))
                {
                    String extension = FilenameUtils.getExtension(archivo);

                    ruta = JsfUtil.generarNombreArchivo(SessionUtil.getModulo(),
                                    llaveAct,
                                    SysmanConstantes.RUTA_DOCUMENTOS_PERSONALES,
                                    extension,
                                    SessionUtil.getUser().getCedula());

                    try
                    {
                        JsfUtil.upload(archivoCarganuevoArchivo
                                        .getInputstream(),
                                        ruta);
                        agregarRegistroSubFrmarchivos();
                        cargarListaFrmarchivos();
                    }
                    catch (IOException e)
                    {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
                else
                {
                    JsfUtil.agregarMensajeError(
                                    "No se ha seleccionado ningún archivo");
                }
            }
            else
            {
                JsfUtil.agregarMensajeError(
                                "No se ha seleccionado el tipo de archivo");
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(
                            "Primero debe guardar el registro principal en la pestaña de Información");
        }

        // </CODIGO_DESARROLLADO>
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
    /**
     * Retorna la variable familiar
     *
     * @return familiar
     */
    public String getFamiliar()
    {
        return familiar;
    }

    /**
     * Asigna la variable familiar
     *
     * @param familiar
     * Variable a asignar en familiar
     */
    public void setFamiliar(String familiar)
    {
        this.familiar = familiar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbNIdentificacion
     *
     * @return listacmbNIdentificacion
     */
    public RegistroDataModelImpl getListacmbNIdentificacion()
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
        RegistroDataModelImpl listacmbNIdentificacion)
    {
        this.listacmbNIdentificacion = listacmbNIdentificacion;
    }

    public List<Registro> getListacmbTipoDocumento()
    {
        return listacmbTipoDocumento;
    }

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
     * Retorna la lista listacmbFamiliar
     *
     * @return listacmbFamiliar
     */
    public List<Registro> getListacmbFamiliar()
    {
        return listacmbFamiliar;
    }

    /**
     * Asigna la lista listacmbFamiliar
     *
     * @param listacmbFamiliar
     * Variable a asignar en listacmbFamiliar
     */
    public void setListacmbFamiliar(List<Registro> listacmbFamiliar)
    {
        this.listacmbFamiliar = listacmbFamiliar;
    }

    public boolean isBloquearArchivos()
    {
        return bloquearArchivos;
    }

    public void setBloquearArchivos(boolean bloquearArchivos)
    {
        this.bloquearArchivos = bloquearArchivos;
    }

    public List<Registro> getListaFrmarchivos()
    {
        return listaFrmarchivos;
    }

    public void setListaFrmarchivos(List<Registro> listaFrmarchivos)
    {
        this.listaFrmarchivos = listaFrmarchivos;
    }

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }

    public int getTipoArchivo()
    {
        return tipoArchivo;
    }

    public void setTipoArchivo(int tipoArchivo)
    {
        this.tipoArchivo = tipoArchivo;
    }

    public RegistroDataModelImpl getListacmbTipoArchivo()
    {
        return listacmbTipoArchivo;
    }

    public void setListacmbTipoArchivo(
        RegistroDataModelImpl listacmbTipoArchivo)
    {
        this.listacmbTipoArchivo = listacmbTipoArchivo;
    }

    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    // public String getIdEmpleado()
    // {
    // return idEmpleado;
    // }
    //
    // public void setIdEmpleado(String idEmpleado)
    // {
    // this.idEmpleado = idEmpleado;
    // }

    public String getRuta()
    {
        return ruta;
    }

    public void setRuta(String ruta)
    {
        this.ruta = ruta;
    }

    public UploadedFile getArchivoCarganuevoArchivo()
    {
        return archivoCarganuevoArchivo;
    }

    public void setArchivoCarganuevoArchivo(
        UploadedFile archivoCarganuevoArchivo)
    {
        this.archivoCarganuevoArchivo = archivoCarganuevoArchivo;
    }

    public String getSucursal()
    {
        return sucursal;
    }

    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    public String getCedula()
    {
        return cedula;
    }

    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    public boolean isEnviado()
    {
        return enviado;
    }

    public void setEnviado(boolean enviado)
    {
        this.enviado = enviado;
    }

    public boolean isEstadoInterfaz()
    {
        return estadoInterfaz;
    }

    public void setEstadoInterfaz(boolean estadoInterfaz)
    {
        this.estadoInterfaz = estadoInterfaz;
    }

    public boolean isBloqueaGuardar()
    {
        return bloqueaGuardar;
    }

    public void setBloqueaGuardar(boolean bloqueaGuardar)
    {
        this.bloqueaGuardar = bloqueaGuardar;
    }

    public String getTipoS()
    {
        return tipoS;
    }

    public void setTipoS(String tipoS)
    {
        this.tipoS = tipoS;
    }

    public boolean isActualizado()
    {
        return actualizado;
    }

    public void setActualizado(boolean actualizado)
    {
        this.actualizado = actualizado;
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
