/*-
 * FrmclasecartapregsControlador.java
 *
 * 1.0
 *
 * 28/12/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite realizar los registros de las preguntas para
 * las plantillas registradas o de acuerdo al tipo
 *
 * @version 1.0, 28/12/2016
 * @author ybecerra
 */
/**
 *
 */
@ManagedBean
@ViewScoped
public class FrmclasecartapregsControlador extends BeanBaseDatosAcme
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacena el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "Preguntas Tipo",
     * se visualiza en el subFormulario tipoPregunta
     */
    private final String consPreguntasTipo;

    /**
     * Constante definida para almacenar la cadena
     * "SP_MODELO_TIPO_PREGUNTA"
     */
    private final String tablaActualizar;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo seleccionado en el combo clase
     * del formulario
     */
    private String clase;
    /**
     * Atributo que valida que titulo se hace visible de acuerdo a la
     * clase seleccionada
     */
    private String tituloPreguntasTipo;

    /**
     * Atributo que valida si la pesta�a Pregunta se hace visible o no
     */
    private boolean visiblePreguntas;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de Registros de las clases
     */
    private List<Registro> listaClase;
    String msj;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista de Registros de las Preguntas de las Plantillas
     */
    private RegistroDataModel listaCartapregunta;
    /**
     * Lista de Registro de las Preguntas de los Tipos
     */
    private RegistroDataModel listaTipopregunta;

    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario cartaPregunta
     */
    private Registro registroSubcartaPregunta;
    /**
     * Atributo de referencia para el subformulario tipoPregunta
     */
    private Registro registroSubtipoPregunta;

    /**
     * Variable definida para concatenar registros a la consulta de la
     * listaClase
     */
    String newClase;
    /**
     * Variable definida para cambiar filtro de la listaCLase de
     * acuerdo a los registros que se irian concatenado
     */
    String filtroClase;

    /**
     * Variable definida para cambiar condicion de la listaClase
     */
    String condicionClase;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmclasecartapregsControlador
     */
    public FrmclasecartapregsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consPreguntasTipo = "Preguntas Tipo";
        tablaActualizar = "SP_MODELO_TIPO_PREGUNTA";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMCLASECARTAPREGS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubcartaPregunta = new Registro(
                            new HashMap<String, Object>());
            registroSubtipoPregunta = new Registro(
                            new HashMap<String, Object>());
            nombreConexion = ConectorPool.ESQUEMA_SYSMANK;
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
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
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaCartapregunta = null;
        listaTipopregunta = null;
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
    public void inicializar()
    {
        tabla = "";
        abrirFormulario();
        tituloPreguntasTipo = consPreguntasTipo;

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     *
     *
     */
    @Override
    public void reasignarOrigenGrilla()
    {
        origenGrilla = "";
        if (listaInicial != null)
        {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null)
        {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    /**
     *
     * Carga la lista listaCartapregunta
     *
     * Metodo para cargar los registros del subFormulario
     * cartaPregunta
     *
     */
    public void cargarListaCartapregunta()
    {
        try
        {
            listaCartapregunta = new RegistroDataModel(
                            ConectorPool.ESQUEMA_SYSMANK,
                            ":FR1067_nuevo:TS54:tablePL2398",
                            "SELECT SP_MODELO_TIPO_PREGUNTA.COMPANIA," +
                                "       SP_MODELO_TIPO_PREGUNTA.CLASE," +
                                // "
                                // SP_MODELO_TIPO_PREGUNTA.SUBCLASE,"
                                // +
                                "       SP_MODELO_TIPO_PREGUNTA.CODIGO," +
                                "       SP_MODELO_TIPO_PREGUNTA.PREGUNTA" +
                                // "
                                // SP_MODELO_TIPO_PREGUNTA.CONSECUTIVO"
                                // +
                                " FROM SP_MODELO_TIPO_PREGUNTA" +
                                " WHERE SP_MODELO_TIPO_PREGUNTA.COMPANIA = '001'"
                                +
                                "   AND SP_MODELO_TIPO_PREGUNTA.CLASE = '32'",
                            // " AND
                            // SP_MODELO_TIPO_PREGUNTA.SUBCLASE =
                            // '1'",
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            tablaActualizar));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaTipopregunta
     *
     * Metodo para cargar los registros del subFormulario tipoPregunta
     *
     */
    public void cargarListaTipopregunta()
    {
        try
        {
            listaTipopregunta = new RegistroDataModel(
                            ConectorPool.ESQUEMA_SYSMANK,
                            ":FR1067_nuevo:TS54:tablePL2399",
                            "SELECT SP_MODELO_TIPO_PREGUNTA.COMPANIA," +
                                "       SP_MODELO_TIPO_PREGUNTA.CLASE," +
                                "       SP_MODELO_TIPO_PREGUNTA.CODIGO," +
                                "       SP_MODELO_TIPO_PREGUNTA.PREGUNTA," +
                                "       SP_MODELO_TIPO_PREGUNTA.DATE_CREATED," +
                                "       SP_MODELO_TIPO_PREGUNTA.CREATED_BY," +
                                "       SP_MODELO_TIPO_PREGUNTA.DATE_MODIFIED,"
                                +
                                "       SP_MODELO_TIPO_PREGUNTA.MODIFIED_BY" +
                                " FROM SP_MODELO_TIPO_PREGUNTA" +
                                " WHERE SP_MODELO_TIPO_PREGUNTA.COMPANIA = '"
                                + compania + "'"
                                +
                                "   AND SP_MODELO_TIPO_PREGUNTA.CLASE = '"
                                + clase + "'",
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            tablaActualizar));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaClase
     *
     */
    public void cargarListaClase()
    {
        listaClase = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        "SELECT CODIGO , NOMBRE "
                            + " FROM (SELECT ROWNUM+24 CODIGO , " +
                            "        DECODE(ROWNUM+24,  25,'Acuerdos'," +
                            "                           26,'Cobro Coactivo'," +
                            "                           27,'Cobro Persuasivo',"
                            +
                            "                           28,'Corte y Suspensi�n',"
                            +
                            "                           29,'Paz y Salvo' "
                            + " " + newClase + ") NOMBRE"
                            +
                            " FROM  DUAL" +
                            " CONNECT BY ROWNUM+24 BETWEEN 24 AND "
                            + filtroClase + ") " + condicionClase + "");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarClase()
    {
        cargarListaCartapregunta();
        cargarListaTipopregunta();

        tituloPreguntasTipo = consPreguntasTipo + " "
            + service.buscarEnLista(clase, "CODIGO", "NOMBRE", listaClase);
        if ("31".equals(clase) || "32".equals(clase))
        {
            visiblePreguntas = true;

        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Datos
     *
     *
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDatos(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Cartapregunta
     *
     */
    public void agregarRegistroSubCartapregunta()
    {
        try
        {
            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            tablaActualizar,
                            registroSubcartaPregunta.getCampos());
            cargarListaCartapregunta();
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
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
            registroSubcartaPregunta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Cartapregunta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubCartapregunta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            tablaActualizar, reg.getCampos(),
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
            cargarListaCartapregunta();
        }
    }

    /**
     * Metodo de eliminacion del formulario Cartapregunta
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubCartapregunta(Registro reg)
    {
        try
        {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            tablaActualizar, reg.getLlave());
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaCartapregunta();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Cartapregunta
     *
     */
    public void cancelarEdicionCartapregunta()
    {
        cargarListaCartapregunta();
        cargarListaTipopregunta();
    }

    /**
     * Metodo de insercion del formulario Tipopregunta
     *
     */
    public void agregarRegistroSubTipopregunta()
    {
        try
        {

            int consecutivo = (int) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMANK,
                            "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
                            "'SP_MODELO_TIPO_PREGUNTA' , 'COMPANIA=''"
                                + compania
                                + "'' AND CLASE = ''" + clase + "''' \n" +
                                "                                           , 'CODIGO'",
                            Types.INTEGER);
            registroSubtipoPregunta.getCampos().put("COMPANIA", compania);
            registroSubtipoPregunta.getCampos().put("CLASE", clase);
            registroSubtipoPregunta.getCampos().put("CODIGO", consecutivo);

            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMANK,
                            tablaActualizar,
                            registroSubtipoPregunta.getCampos());
            cargarListaTipopregunta();
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
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
            registroSubtipoPregunta = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Tipopregunta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubTipopregunta(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            reg.getCampos().remove("RNUM");
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMANK,
                            tablaActualizar, reg.getCampos(),
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
            cargarListaTipopregunta();
        }
    }

    /**
     * Metodo de eliminacion del formulario Tipopregunta
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubTipopregunta(Registro reg)
    {
        try
        {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMANK,
                            tablaActualizar, reg.getLlave());
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaTipopregunta();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Tipopregunta
     *
     */
    public void cancelarEdicionTipopregunta()
    {
        cargarListaTipopregunta();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Metodo ejecutado al abrir el formulario, valida si lo que
     * devuelve la funcion FC_AUTORIZACION_DESVIACION es verdadero o
     * falso
     *
     * @return Verdadero o falso
     */
    public boolean autorizacionDesviacion()
    {

        boolean autorizacionD = false;
        try
        {

            int autorizacionDesviacion = (int) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_DESVIACION",
                            "'" + compania + "', '"
                                + SessionUtil.getCompaniaIngreso().getNit()
                                + "'",
                            Types.INTEGER);

            if (autorizacionDesviacion == 0)
            {

                autorizacionD = false;

            }
            else
            {
                autorizacionD = true;
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        return autorizacionD;

    }

    public boolean autorizacionFraudes()
    {
        boolean autorizacionF = false;
        try
        {

            int autorizacionFraudes = (int) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_AUTORIZACION_FRAUDES",
                            "'" + compania + "', '"
                                + SessionUtil.getCompaniaIngreso().getNit()
                                + "'",
                            Types.INTEGER);

            if (autorizacionFraudes == 0)
            {

                autorizacionF = false;

            }
            else
            {
                autorizacionF = true;
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        return autorizacionF;

    }

    public void mensajesInicioModal()
    {
        if ("SI".equals(msj))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2715"));
        }
        return;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        newClase = "";
        filtroClase = "29";
        condicionClase = "";
        visiblePreguntas = false;

        String manejaReconexion;
        try
        {
            manejaReconexion = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "MANEJA ACTAS DE RECONEXION Y REINSTALACION",
                            modulo,
                            "SYSDATE");
            if (manejaReconexion == null)
            {
                msj = "SI";
                mensajesInicioModal();
                return;

            }
            else if ("SI".equals(manejaReconexion))
            {

                newClase = ", 30 ,'Reconexiones y Reinstalaciones' ";
                filtroClase = "30";
            }
            else
            {

                condicionClase = "WHERE CODIGO NOT IN(30 ";

            }

            if (autorizacionDesviacion())
            {
                newClase = newClase + ",31,'Desviaci�n'";
                filtroClase = "31";
            }
            else
            {
                if (condicionClase.isEmpty())
                {
                    condicionClase = "WHERE CODIGO NOT IN(31";
                }
                else
                {
                    condicionClase = condicionClase + ",31";
                }
            }

            if (autorizacionFraudes())
            {
                newClase = newClase + ",32,'Fraude'";
                filtroClase = "32";
            }
            else
            {
                if (condicionClase.isEmpty())
                {
                    condicionClase = "WHERE CODIGO NOT IN(32";
                }
                else
                {
                    condicionClase = condicionClase + ",32";
                }
            }

            if (!condicionClase.isEmpty())
            {
                condicionClase = condicionClase + ")";
            }
            newClase = newClase + ",33,'PQR', 34 , 'Solicitud de Servicio'";
            filtroClase = "34";

            cargarListaClase();
        }
        catch (NamingException | SQLException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put("COMPANIA", compania);
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
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

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna el atributo clase
     *
     * @return clase
     */
    public String getClase()
    {
        return clase;
    }

    /**
     * Asigna el atributo clase
     *
     * @param clase
     */
    public void setClase(String clase)
    {
        this.clase = clase;
    }

    /**
     * Retorna el atributo tituloPreguntasTipo
     *
     * @return tituloPreguntasTipo
     */
    public String getTituloPreguntasTipo()
    {
        return tituloPreguntasTipo;
    }

    /**
     * Asigna el atributo tituloPreguntasTipo
     *
     * @param tituloPreguntasTipo
     */
    public void setTituloPreguntasTipo(String tituloPreguntasTipo)
    {
        this.tituloPreguntasTipo = tituloPreguntasTipo;
    }

    /**
     * Retorna el atributo visiblePreguntas
     *
     * @return visiblePreguntas
     */
    public boolean isVisiblePreguntas()
    {
        return visiblePreguntas;
    }

    /**
     * Asigna el atributo visiblePreguntas
     *
     * @param visiblePreguntas
     */
    public void setVisiblePreguntas(boolean visiblePreguntas)
    {
        this.visiblePreguntas = visiblePreguntas;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCLASE
     *
     * @return listaCLASE
     */
    public List<Registro> getListaClase()
    {
        return listaClase;
    }

    /**
     * Asigna la lista listaCLASE
     *
     * @param listaCLASE
     * Variable a asignar en listaCLASE
     */
    public void setListaClase(List<Registro> listaClase)
    {
        this.listaClase = listaClase;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaCartapregunta
     *
     * @return listaCartapregunta
     */
    public RegistroDataModel getListaCartapregunta()
    {
        return listaCartapregunta;
    }

    /**
     * Asigna la lista listaCartapregunta
     *
     * @param listaCartapregunta
     * Variable a asignar en listaCartapregunta
     */
    public void setListaCartapregunta(RegistroDataModel listaCartapregunta)
    {
        this.listaCartapregunta = listaCartapregunta;
    }

    /**
     * Retorna la lista listaTipopregunta
     *
     * @return listaTipopregunta
     */
    public RegistroDataModel getListaTipopregunta()
    {
        return listaTipopregunta;
    }

    /**
     * Asigna la lista listaTipopregunta
     *
     * @param listaTipopregunta
     * Variable a asignar en listaTipopregunta
     */
    public void setListaTipopregunta(RegistroDataModel listaTipopregunta)
    {
        this.listaTipopregunta = listaTipopregunta;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubcartaPregunta
     *
     * @return registroSubcartaPregunta
     */
    public Registro getRegistroSubcartaPregunta()
    {
        return registroSubcartaPregunta;
    }

    /**
     * Asigna el objeto registroSubcartaPregunta
     *
     * @param registroSubcartaPregunta
     * Variable a asignar en registroSubcartaPregunta
     */
    public void setRegistroSubcartaPregunta(Registro registroSubcartaPregunta)
    {
        this.registroSubcartaPregunta = registroSubcartaPregunta;
    }

    /**
     * Retorna el objeto registroSubtipoPregunta
     *
     * @return registroSubtipoPregunta
     */
    public Registro getRegistroSubtipoPregunta()
    {
        return registroSubtipoPregunta;
    }

    /**
     * Asigna el objeto registroSubtipoPregunta
     *
     * @param registroSubtipoPregunta
     * Variable a asignar en registroSubtipoPregunta
     */
    public void setRegistroSubtipoPregunta(Registro registroSubtipoPregunta)
    {
        this.registroSubtipoPregunta = registroSubtipoPregunta;
    }
    // </SET_GET_ADICIONALES>
}
