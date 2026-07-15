/*-
 * FrmactdatospersonalsControlador.java
 *
 * 1.0
 *
 * 20/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.frmactdatospersonalsControladorEnum;
import com.sysman.hojasdevida.enums.frmactdatospersonalsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Usuario;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Permite la actualización de información personal, familiar y de tercero
 *
 * @version 1.0, 20/01/2018
 * @author vmolano
 */
@ManagedBean
@ViewScoped
public class FrmactdatospersonalsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String cedula;
    private String sucursal;
    private String idEmpleado;
    private String nombreEmpleado;
    private int porTramitar;
    private String titulo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmactdatospersonalsControlador
     */
    public FrmactdatospersonalsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTDATOSPERSONALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Usuario usuario = SessionUtil.getUser();
            cedula = usuario.getCedula();
            sucursal = usuario.getSucursal();

            // <INI_ADICIONAL>

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(frmactdatospersonalsControladorEnum.CEDULA.getValue(),
                            cedula);
            param.put(frmactdatospersonalsControladorEnum.SUCURSAL.getValue(),
                            sucursal);

            List<Registro> rsIdEmpleado = peticionSelect(param,
                            frmactdatospersonalsControladorUrlEnum.URL002
                                            .getValue());

            if (rsIdEmpleado.size() > 1)
            {
                JsfUtil.agregarMensajeAlerta(
                                "El usuario tiene más de un registro en personal");
            }
            else
            {
                idEmpleado = String.valueOf(rsIdEmpleado.get(0).getCampos()
                                .get(frmactdatospersonalsControladorEnum.ID_DE_EMPLEADO
                                                .getValue()));
                nombreEmpleado = String.valueOf(rsIdEmpleado.get(0).getCampos()
                                .get(frmactdatospersonalsControladorEnum.NOMBREEMPLEADO
                                                .getValue()));
            }

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public List<Registro> peticionSelect(
        Map<String, Object> parametrosConsulta, String url)
    {
        List<Registro> rsConsulta = null;
        try
        {
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(url);
            rsConsulta = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            urlReg.getUrl(),
                                            parametrosConsulta));

        }
        catch (SystemException e)
        {
            Logger.getLogger(FrmactdatospersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return rsConsulta;
    }

    private GenericUrlEnum validarMenu(String actual)
    {
        GenericUrlEnum res = null;
        if (actual.equals(
                        frmactdatospersonalsControladorEnum.MENU_PERSONALES
                                        .getValue()))
        {
            res = GenericUrlEnum.AUT_PERSONAL;

        }
        else if (actual.equals(
                        frmactdatospersonalsControladorEnum.MENU_FAMILIARES
                                        .getValue()))
        {
            res = GenericUrlEnum.AUT_FAMILIARES;
        }
        else if (actual.equals(
                        frmactdatospersonalsControladorEnum.MENU_EXTERNOS
                                        .getValue()))
        {
            res = GenericUrlEnum.AUT_TERCERO;
        }

        return res;

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = validarMenu(SessionUtil.getMenuActual());
        buscarLlave();
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
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if (enumBase == GenericUrlEnum.AUT_PERSONAL)
        {
            titulo = "ACTUALIZACIÓN DE DATOS PERSONALES";
            parametrosListado
                            .put(frmactdatospersonalsControladorEnum.NUMDOCUMENTO
                                            .getValue(), cedula);
            parametrosListado.put(frmactdatospersonalsControladorEnum.IDEMPLEADO
                            .getValue(), idEmpleado);

        }
        else if (enumBase == GenericUrlEnum.AUT_FAMILIARES)
        {
            titulo = "ACTUALIZACIÓN DE DATOS FAMILIARES";
            parametrosListado.put(
                            frmactdatospersonalsControladorEnum.NUMDOCUMENTO
                                            .getValue(),
                            cedula);
        }
        else if (enumBase == GenericUrlEnum.AUT_TERCERO)
        {
            titulo = "ACTUALIZACIÓN DE DATOS DE TERCEROS";
            parametrosListado.put(
                            frmactdatospersonalsControladorEnum.NIT.getValue(),
                            cedula);
        }

        parametrosListado.put(frmactdatospersonalsControladorEnum.PORTRAMITAR
                        .getValue(), porTramitar);
        parametrosListado.put(frmactdatospersonalsControladorEnum.DESTINATARIO
                        .getValue(), cedula);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton nuevo y que redirecciona al formulario correspondiente segun la opción de menú.
     *
     */
    public void oprimirComando156()
    {
        // <CODIGO_DESARROLLADO>

        String frmNuevo = getFormularioSegunOpcion(false);

        String[] campos = { frmactdatospersonalsControladorEnum.ACCION
                        .getValue(),
                            frmactdatospersonalsControladorEnum.ID_DE_EMPLEADO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.NOMBREEMPLEADO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.SUCURSAL
                                            .getValue(),
                            "CEDULA" };
        String[] valores = { ACCION_INSERTAR, idEmpleado, nombreEmpleado,
                             sucursal, cedula };
        SessionUtil.cargarModalDatosFlash(frmNuevo,
                        SessionUtil.getModulo(), campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioVer()
    {
        reasignarOrigen();
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton PorTramitar y filtra los registros pendientes de tramite
     *
     */
    public void oprimirPorTramitar()
    {
        // <CODIGO_DESARROLLADO>
        porTramitar = 1;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton MisSolicitudes y filtra los registros de las solicitudes propias
     *
     */
    public void oprimirMisSolicitudes()
    {
        // <CODIGO_DESARROLLADO>
        porTramitar = 0;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton ver
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la grilla
     */
    public void oprimirVer(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        String consecutivo = String.valueOf(reg.getCampos()
                        .get(frmactdatospersonalsControladorEnum.CONSECUTIVO
                                        .getValue()));
        boolean actualizado = (boolean) reg.getCampos()
                        .get(frmactdatospersonalsControladorEnum.ACTUALIZADO
                                        .getValue());
        boolean enviado = (boolean) reg.getCampos().get(
                        frmactdatospersonalsControladorEnum.ENVIADO.getValue());

        String tipoS = String.valueOf(reg.getCampos()
                        .get(frmactdatospersonalsControladorEnum.DESTINO
                                        .getValue()));

        String[] campos = { frmactdatospersonalsControladorEnum.ACCION
                        .getValue(),
                            frmactdatospersonalsControladorEnum.CONSECUTIVO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.ID_DE_EMPLEADO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.ACTUALIZADO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.TIPO.getValue(),
                            frmactdatospersonalsControladorEnum.ENVIADO
                                            .getValue(),
                            "SUCURSAL_EMPLEADO",
                            frmactdatospersonalsControladorEnum.CEDULA
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.NOMBREEMPLEADO
                                            .getValue(),
                            frmactdatospersonalsControladorEnum.SUCURSAL
                                            .getValue(),
                            "IDENTIFICACION" };
        Object[] valores = { ACCION_MODIFICAR, consecutivo, idEmpleado,
                             actualizado, tipoS, enviado,
                             reg.getCampos().get("SUCURSAL"),
                             cedula,
                             nombreEmpleado,
                             sucursal,
                             reg.getCampos().get("IDENTIFICACION") };

        String formulario = getFormularioSegunOpcion(validarCondiciones(reg));
        SessionUtil.cargarModalDatosFlash(formulario,
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public boolean validarCondiciones(Registro reg)
    {
        String consecutivo = reg.getCampos()
                        .get(frmactdatospersonalsControladorEnum.CONSECUTIVO
                                        .getValue())
                        .toString();
        String destino = (String) reg.getCampos().get(
                        frmactdatospersonalsControladorEnum.DESTINO.getValue());
        boolean enviado = (boolean) reg.getCampos().get(
                        frmactdatospersonalsControladorEnum.ENVIADO.getValue());
        boolean actualizado = (boolean) reg.getCampos()
                        .get(frmactdatospersonalsControladorEnum.ACTUALIZADO
                                        .getValue());
        String estado = (String) reg.getCampos().get(
                        frmactdatospersonalsControladorEnum.ESTADO.getValue());

        boolean destinoT = "T".equals(destino);
        boolean destinoS = "S".equals(destino);
        boolean estadoA = "A".equals(estado);
        boolean consecutivoCero = "0".equals(consecutivo);

        boolean c1 = destinoT && !(consecutivoCero) && enviado;
        boolean c2 = !actualizado && destinoT && enviado;
        boolean c3 = estadoA && destinoS;

        return c1 || c2 || c3;
    }

    public String getFormularioSegunOpcion(boolean condicion)
    {
        String formulario = "";

        if (enumBase == GenericUrlEnum.AUT_PERSONAL)
        {
            formulario = String
                            .valueOf(GeneralCodigoFormaEnum.FRM_ACTDATOSPERSONALES_DETALLADO_CONTROLADOR
                                            .getCodigo());
            if (condicion)
            {
                formulario = String
                                .valueOf(GeneralCodigoFormaEnum.FRMACTDATOSPERSONALDETALLADOAUTS_CONTROLADOR
                                                .getCodigo());
            }
        }
        else if (enumBase == GenericUrlEnum.AUT_FAMILIARES)
        {
            formulario = String
                            .valueOf(GeneralCodigoFormaEnum.FRMACTDATOSFAMILIARES_CONTROLADOR
                                            .getCodigo());
            if (condicion)
            {
                formulario = String
                                .valueOf(GeneralCodigoFormaEnum.FRMACTDATOSFAMILIARESDETALLADO_CONTROLADOR
                                                .getCodigo());
            }
        }
        else if (enumBase == GenericUrlEnum.AUT_TERCERO)
        {
            formulario = "1677";
            if (condicion)
            {
                formulario = "1678";
            }
        }

        return formulario;
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1629-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Dim strSql As String strSql = datosSql & _ "   AND   aut_personal.NUMERO_DCTO       = '" & GetUserNit() & "' " & vbCrLf & _
         * "   AND  aut_personal.ID_DE_EMPLEADO    = '" & GetUserId & "'" & vbCrLf Me.RecordSource = strSql If GetUserAutoriza() = GetUserNit() Then Me.PorTramitar.visible = True
         * Me.MisSolicitudes.visible = True Else Me.PorTramitar.visible = False Me.MisSolicitudes.visible = False End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
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
     *
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void retornarFormularioComando156()
    {
        listaInicial.load();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return true
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // no aplica
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // No aplica
    }
    // <SET_GET_ATRIBUTOS>

    public String getCompania()
    {
        return compania;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
