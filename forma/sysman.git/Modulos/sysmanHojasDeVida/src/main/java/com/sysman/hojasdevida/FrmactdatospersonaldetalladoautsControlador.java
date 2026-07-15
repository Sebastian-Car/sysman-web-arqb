/*-
 * FrmactdatospersonaldetalladoautsControlador.java
 *
 * 1.0
 *
 * 25/01/2018
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
import com.sysman.hojasdevida.enums.FrmactdatospersonaldetalladoautsControladorUrlEnum;
import com.sysman.hojasdevida.enums.frmactdatospersonalsControladorEnum;
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

/**
 * Permite la comparación de los datos actuales, las solicitudes de cambio y los aprobados.
 *
 * @version 1.0, 25/01/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactdatospersonaldetalladoautsControlador
                extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String idEmpleado;

    private String consecutivo;

    private String nombreEmpleado;
    private String sucursal;
    private String cedula;
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
     * listado de pasises
     */
    private List<Registro> listacmbPaisHab;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * listado de documentos
     */
    private RegistroDataModel listanit;
    /**
     * listado de departamentos
     */
    private List<Registro> listacmbDptoHab;
    /**
     * listado de ciudades
     */
    private List<Registro> listacmbCiudadHab;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmactdatospersonaldetalladoautsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmactdatospersonaldetalladoautsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = 1642;
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
        cargarListanit();
        cargarListacmbPaisHab();
        cargarListacmbDptoHab();
        cargarListacmbCiudadHab();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        enumBase = GenericUrlEnum.AUT_PERSONAL;
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
     * Carga la lista de paises
     *
     */
    public void cargarListacmbPaisHab()
    {
        try
        {
            listacmbPaisHab = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatospersonaldetalladoautsControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista de empleados
     *
     */
    public void cargarListanit()
    {
        // listanit = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN,
        // ":FR1642_nuevo:TBCB5491", "SELECT " +
        // " PERSONAL.NUMERO_DCTO, " +
        // " ((PERSONAL.NOMBRES || ' ' || PERSONAL.APELLIDO1 || ' ' || PERSONAL.APELLIDO2) ) NOMBRE, "
        // +
        // " PERSONAL.EMAIL_PERSONAL, " +
        // " PERSONAL.EMAIL_CORPORATIVO, " +
        // " PERSONAL.ID_DE_EMPLEADO, " +
        // " PERSONAL.EMAIL_PERSONAL, " +
        // " PERSONAL.DIRECCION, " +
        // " PERSONAL.PAIS_HAB, " +
        // " PERSONAL.DEPARTAMENTO_HAB, " +
        // " PERSONAL.CIUDAD_HAB, " +
        // " PERSONAL.PANTALON, " +
        // " PERSONAL.CAMISA, " +
        // " PERSONAL.CALZADO, " +
        // " PERSONAL.CHAQUETA, " +
        // " PERSONAL.TELEFONOS, " +
        // " PERSONAL.COMPANIA, " +
        // " PERSONAL.NUMERO_DCTOCAUSANTE, " +
        // " PERSONAL.APELLIDO1, " +
        // " 0 DDD, " +
        // " PERSONAL.ID_DE_CARGO, " +
        // " PERSONAL.ESCALAFON, " +
        // " PERSONAL.ID_DE_TIPO, " +
        // " PERSONAL.ESTADO_ACTUAL, " +
        // " PERSONAL.NOMBRES, " +
        // " PERSONAL.APELLIDO1 " +
        // " FROM " +
        // " PERSONAL " +
        // " WHERE " +
        // " (" +
        // " (Not(PERSONAL.EMAIL_PERSONAL) IS NULL) " +
        // " AND " +
        // " ((PERSONAL.COMPANIA) = '" + compania
        // + "') " +
        // " AND " +
        // " ((PERSONAL.ESTADO_ACTUAL) = 1) " +
        // " AND " +
        // " (Not((PERSONAL.NOMBRES || ' ' || PERSONAL.APELLIDO1 || ' ' || PERSONAL.APELLIDO2)) IS NULL)"
        // +
        // " ) " +
        // " ",
        // true, "NUMERO_DCTO");
    }

    /**
     *
     * Carga la lista de departamentos
     *
     */
    public void cargarListacmbDptoHab()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(frmactdatospersonalsControladorEnum.PAIS.getValue(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get("PAIS_HAB_A"), "").toString());
            listacmbDptoHab = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmactdatospersonaldetalladoautsControladorUrlEnum.URL003
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
     * Carga la lista de ciudades
     *
     */
    public void cargarListacmbCiudadHab()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(frmactdatospersonalsControladorEnum.PAIS.getValue(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get("PAIS_HAB_A"), "").toString());
            param.put(frmactdatospersonalsControladorEnum.DEPARTAMENTO
                            .getValue(),
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get("DEPARTAMENTO_HAB_A"),
                                            "").toString());
            listacmbCiudadHab = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmactdatospersonaldetalladoautsControladorUrlEnum.URL002
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        if ("T".equals(registro.getCampos().get("ESTADO")))
        {
            JsfUtil.agregarMensajeAlerta(
                            "Por favor escoja un estado de solicitud diferente.");
            return;
        }
        agregarRegistroNuevo(false);
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
            llaveActual.put("KEY_ID_DE_EMPLEADO", idEmpleado);
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
         * FR1642-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim Estado As Boolean On Error Resume Next If Forms!FRM_ACTDATOSPERSONAL!TIPOS = "S" And Forms!FRM_ACTDATOSPERSONAL!ACTUALIZADO = 0
         * Then Estado = False Me!Imprimir.Enabled = True ElseIf Forms!FRM_ACTDATOSPERSONAL!TIPOS = "A" Then Estado = True ElseIf Forms!FRM_ACTDATOSPERSONAL!TIPOS = "T" And Not
         * (Forms!Identificacion!N_Solicitud = 0) And Forms!FRM_ACTDATOSPERSONAL!ACTUALIZADO = 0 Then Estado = False Me.Estado.Locked = Estado Me!Imprimir.Enabled = True End If If
         * Forms!Identificacion!N_Solicitud = 0 Then 'NUEVO Estado = False Forms!Identificacion!N_Solicitud = 0 Me.Undo Me.Requery Me!Imprimir.Enabled = True Me!btn_Ruta.Enabled = True End If If Not
         * Forms!FRM_ACTDATOSPERSONAL!ACTUALIZADO = 0 Then Estado = True Me.Estado.Locked = Estado End If Me.txtEmailPersonal.Locked = Estado Me.txtDireccion.Locked = Estado Me.txtTelefono.Locked =
         * Estado Me.cmbPaisHab.Locked = Estado Me.cmbDptoHab.Locked = Estado Me.cmbCiudadHab.Locked = Estado Me.txtTallaPantalon.Locked = Estado Me.txtTallaCamisa.Locked = Estado
         * Me.txtTallaCalzado.Locked = Estado Me.txtTallaChaqueta.Locked = Estado Me.txtRuta.Locked = Estado End Sub
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
        if (ACCION_INSERTAR.equals(accion))
        {
            // registro.getCampos().put("COMPANIA", compania);
            registro.getCampos().put("CONSECUTIVO", consecutivo);
            registro.getCampos().put("ID_DE_EMPLEADO", idEmpleado);
            registro.getCampos().put("NUMERO_DCTO", cedula);
            registro.getCampos().put("NOMBREEMPLEADO", nombreEmpleado);
            registro.getCampos().put("ESTADO", "T");
            registro.getCampos().put("ENVIADO", false);
            registro.getCampos().put("SUCURSAL", sucursal);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbPaisHab()
    {
        registro.getCampos().put("DEPARTAMENTO_HAB_A", "");
        registro.getCampos().put("CIUDAD_HAB_A", "");
        cargarListacmbDptoHab();
    }

    public void cambiarcmbDptoHab()
    {
        registro.getCampos().put("CIUDAD_HAB_A", "");
        cargarListacmbCiudadHab();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return variable que define si se continua con el proceso de insercion.
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
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return variable que define si se deben continuar
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
     * @return variable que define si se debe continuar con el proceso de actualización.
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1642-ANTES_ACTUALIZAR Private Sub Form_BeforeUpdate(Cancel As Integer) Dim Estado As Boolean Estado = True If Me!Consecutivo = 0 Then Me!Consecutivo = GenConsecutivo("aut_personal",
         * " COMPANIA = '" & Getcompany() & "' AND ID_DE_EMPLEADO = '" & GetUserId() & "'", "CONSECUTIVO") If Not Me!Enviado = 0 Then Me.txtEmailPersonal.Locked = Estado Me.txtDireccion.Locked =
         * Estado Me.txtTelefono.Locked = Estado Me.cmbPaisHab.Locked = Estado Me.cmbDptoHab.Locked = Estado Me.cmbCiudadHab.Locked = Estado Me.txtTallaPantalon.Locked = Estado
         * Me.txtTallaCamisa.Locked = Estado Me.txtTallaCalzado.Locked = Estado Me.txtTallaChaqueta.Locked = Estado 'Me.DESTINO.Locked = Estado End If End If End Sub
         */
        // </CODIGO_DESARROLLADO>

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos().remove("APELLIDO1_A");
        registro.getCampos().remove("APELLIDO2_A");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
        registro.getCampos().remove("APELLIDO1");
        registro.getCampos().remove("NOMBREEMPLEADO");

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return variable que define si debe continuar el proceso.
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
     * @return variable que controla si se continua con la eliminacion
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
     * @return variable que define si continua el proceso
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
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void listacmbPaisHab_D()
    {

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable paisActual
     *
     * @return paisActual
     */
    public String getPaisActual()
    {
        return paisActual;
    }

    /**
     * Asigna la variable paisActual
     *
     * @param paisActual
     * Variable a asignar en paisActual
     */
    public void setPaisActual(String paisActual)
    {
        this.paisActual = paisActual;
    }

    /**
     * Retorna la variable departamentoActual
     *
     * @return departamentoActual
     */
    public String getDepartamentoActual()
    {
        return departamentoActual;
    }

    /**
     * Asigna la variable departamentoActual
     *
     * @param departamentoActual
     * Variable a asignar en departamentoActual
     */
    public void setDepartamentoActual(String departamentoActual)
    {
        this.departamentoActual = departamentoActual;
    }

    /**
     * Retorna la variable ciudadActual
     *
     * @return ciudadActual
     */
    public String getCiudadActual()
    {
        return ciudadActual;
    }

    /**
     * Asigna la variable ciudadActual
     *
     * @param ciudadActual
     * Variable a asignar en ciudadActual
     */
    public void setCiudadActual(String ciudadActual)
    {
        this.ciudadActual = ciudadActual;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbPaisHab
     *
     * @return listacmbPaisHab
     */
    public List<Registro> getListacmbPaisHab()
    {
        return listacmbPaisHab;
    }

    /**
     * Asigna la lista listacmbPaisHab
     *
     * @param listacmbPaisHab
     * Variable a asignar en listacmbPaisHab
     */
    public void setListacmbPaisHab(List<Registro> listacmbPaisHab)
    {
        this.listacmbPaisHab = listacmbPaisHab;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listanit
     *
     * @return listanit
     */
    public RegistroDataModel getListanit()
    {
        return listanit;
    }

    /**
     * Asigna la lista listanit
     *
     * @param listanit
     * Variable a asignar en listanit
     */
    public void setListanit(RegistroDataModel listanit)
    {
        this.listanit = listanit;
    }

    /**
     * Retorna la lista listacmbDptoHab
     *
     * @return listacmbDptoHab
     */
    public List<Registro> getListacmbDptoHab()
    {
        return listacmbDptoHab;
    }

    /**
     * Asigna la lista listacmbDptoHab
     *
     * @param listacmbDptoHab
     * Variable a asignar en listacmbDptoHab
     */
    public void setListacmbDptoHab(List<Registro> listacmbDptoHab)
    {
        this.listacmbDptoHab = listacmbDptoHab;
    }

    /**
     * Retorna la lista listacmbCiudadHab
     *
     * @return listacmbCiudadHab
     */
    public List<Registro> getListacmbCiudadHab()
    {
        return listacmbCiudadHab;
    }

    /**
     * Asigna la lista listacmbCiudadHab
     *
     * @param listacmbCiudadHab
     * Variable a asignar en listacmbCiudadHab
     */
    public void setListacmbCiudadHab(List<Registro> listacmbCiudadHab)
    {
        this.listacmbCiudadHab = listacmbCiudadHab;
    }

    public String getIdEmpleado()
    {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado)
    {
        this.idEmpleado = idEmpleado;
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

    public boolean isBloqEstado()
    {
        return bloqEstado;
    }

    public void setBloqEstado(boolean bloqEstado)
    {
        this.bloqEstado = bloqEstado;
    }

    public boolean isBloqImprimir()
    {
        return bloqImprimir;
    }

    public void setBloqImprimir(boolean bloqImprimir)
    {
        this.bloqImprimir = bloqImprimir;
    }

    public boolean isBloqCampos()
    {
        return bloqCampos;
    }

    public void setBloqCampos(boolean bloqCampos)
    {
        this.bloqCampos = bloqCampos;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
