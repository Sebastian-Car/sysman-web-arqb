/*-
 * RetirosMasivosControlador.java
 *
 * 1.0
 * 
 * 11/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.enums.RetirosMasivosControladorUrlEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Formulario que permite realizar el retiro masivo de empleados
 * filtrado por clasificación.
 *
 * @version 1.0, 11/01/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class RetirosMasivosControlador extends BeanBaseModal
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
     * variable que almacena listaidTipo de empleados
     */
    private List<Registro> listatipo;
    /**
     * variable que almacena listaEstadoActual de empleados
     */
    private List<Registro> listaEstadoActual;

    /**
     * variable que almacena tipo seleccionada en Modal
     */

    private String tipo;

    /**
     * variable que almacena estadoActual seleccionada en Modal
     */

    private String estadoActual;

    /**
     * variable que almacena fecharetiro indicada en Modal
     */

    private Date fechaRetiro;

    /**
     * variable que almacena fechaterminacion indicada en Modal
     */

    private Date fechaTerminacion;

    /**
     * variable que almacena el mensaje a mostrar en el dialogo de
     * confirmación
     */
    private String mensaje;

    private boolean dialogoVisible;

    @EJB
    private EjbNominaSieteRemote ejbNominaSiete;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RetirosMasivosControlador
     */
    public RetirosMasivosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RETIROSMASIVOS_CONTROLADOR.getCodigo();
            validarPermisos();
            fechaRetiro = new Date();
            fechaTerminacion = new Date();
            mensaje = idioma.getString("TB_TB3916");

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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaTipo();
        cargarListaEstadoActual();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaidTipo
     *
     * Método que trae los registros de los tipos de empleados
     */
    public void cargarListaTipo()
    {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listatipo = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetirosMasivosControladorUrlEnum.URL4256.getValue())
                                            .getUrl(),
                            parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEstadoActual()
    {
        try
        {
            listaEstadoActual = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(RetirosMasivosControladorUrlEnum.URL5121.getValue())
                                            .getUrl(),
                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnActualizar en la vista
     *
     * Metodo que permite generar el procedimiento de retiro de los
     * empleados según selección campos
     *
     */
    public void oprimirBtnActualizar()
    {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarproceso()
    {
        try
        {

            if (Integer.parseInt(ejbNominaSiete.retiroMasivo(compania, tipo, fechaRetiro, fechaTerminacion, Long.parseLong(estadoActual),
                            SessionUtil.getUser().toString())) > 0)
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3917"));
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3918"));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        dialogoVisible = false;
    }

    public boolean validaVariables()
    {
        boolean respuesta;
        if (((tipo == null) || (tipo == "")) || ((estadoActual == null) || (estadoActual == "")))
        {

            respuesta = false;
        }
        else
        {
            respuesta = true;
        }
        return respuesta;
    }

    public boolean validarCampos()
    {
        if (((fechaRetiro == null) || (fechaTerminacion == null)) || !validaVariables())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        else
        {
            return true;
        }
    }

    public List<Registro> getListatipo()
    {
        return listatipo;
    }

    public void setListatipo(List<Registro> listatipo)
    {
        this.listatipo = listatipo;
    }

    public List<Registro> getListaEstadoActual()
    {
        return listaEstadoActual;
    }

    public void setListaEstadoActual(List<Registro> listaEstadoActual)
    {
        this.listaEstadoActual = listaEstadoActual;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getEstadoActual()
    {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual)
    {
        this.estadoActual = estadoActual;
    }

    public Date getFechaRetiro()
    {
        return fechaRetiro;
    }

    public void setFechaRetiro(Date fechaRetiro)
    {
        this.fechaRetiro = fechaRetiro;
    }

    public Date getFechaTerminacion()
    {
        return fechaTerminacion;
    }

    public void setFechaTerminacion(Date fechaTerminacion)
    {
        this.fechaTerminacion = fechaTerminacion;
    }

    public String getMensaje()
    {
        return mensaje;
    }

    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
