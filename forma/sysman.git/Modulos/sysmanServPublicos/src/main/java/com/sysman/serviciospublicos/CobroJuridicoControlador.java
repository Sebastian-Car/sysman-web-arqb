/*-
 * CobroJuridicoControlador.java
 *
 * 1.0
 * 
 * 17/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.CobroJuridicoControladorEnum;
import com.sysman.serviciospublicos.enums.CobroJuridicoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;

/**
 * Clase que genera cartas de cobro juridico en serie
 *
 * @author jguerrero
 * @version 1.0, 17/03/2017
 * @modifier amonroy
 * @version 2, 17/05/2017 Proceso de Refactoring e implementaci�n de
 * EJBs para hacer el llamado a la funcion
 * PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARCOACTIVO
 * @version 3, 01/06/2017 Se modifica la funcion FC_REGISTRARCOACTIVO
 * y se realizan ajustes en el metodo oprimirmodeloPlantilla() para
 * recibir de forma adecuada el retorno de la funcion
 *
 * @author ybecerra
 * @version 4, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class CobroJuridicoControlador extends BeanBaseDatosAcme
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almancena temporalmentelo seleccionado el check de
     * media carta del formulario
     */
    private String checkMedia;

    /*
     * Constante encargada de almacenar el String CODIGORUTA
     */
    private final String codigoRutacons;

    /**
     * Variable que almancena temporalmentelo seleccionado el check de
     * consecutivo automatico del formulario
     */
    private String checkVerificacion;
    /**
     * Variable que almancena temporalmentelo seleccionado codigo ruta
     * inicial del combo del formulario
     */
    private String codigoRutaInicial;
    /**
     * Variable que almancena temporalmentelo seleccionado codigo ruta
     * final del combo del formulario
     */
    private String codigoRutaFinal;
    /**
     * Variable que almancena temporalmentelo seleccionado del ciclo
     * del combo del formulario
     */
    private String ciclo;
    /**
     * Variable que sirve para ocultar o aparecer el campo de
     * consecutivo automatico
     */
    private boolean visibleAutomatico;
    /**
     * Variable que almancena temporalmente lo seleccionado del combo
     * formato en el formulario
     */
    private String formato;
    /**
     * Variable que almancena temporalmentelo la fecha del formulario
     */
    private Date fechaEmicion;
    /**
     * Variable que almancena temporalmente lo digitado de periodo
     * atraso del formulario
     */
    private String atraso;
    /**
     * Variable que almancena temporalmente lo digitado de la deuda
     * inicialdel formulario
     */
    private String deuda;
    /**
     * Variable que almancena temporalmente lo digitado de la deuda
     * final del formulario
     */
    private String deudaFinal;
    /**
     * Variable que almancena temporalmente lo digitado de periodo
     * atraso superiro del formulario
     */
    private String atrasoSuperior;
    /**
     * Variable que almancena temporalmente lo digitado consecutivo
     * formulario
     */
    private String consecutivo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que se encarga de almacenar temporalmente lo seleccionado
     * del combo ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Lista que se encarga de almacenar temporalmente lo seleccionado
     * del combo formato
     */
    private List<Registro> listaFormateado;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que se encarga de almacenar temporalmente lo seleccionado
     * del combo codigo Ruta inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista que se encarga de almacenar temporalmente lo seleccionado
     * del combo codigo Ruta FInal
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Implementacion del EJB del paquete 2 de Servicios Publicos para
     * hacer el llamado a la funcion FC_REGISTRARCOACTIVO
     */
    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CobroJuridicoControlador
     */
    public CobroJuridicoControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        checkVerificacion = "true";
        fechaEmicion = new Date();
        atraso = "1";
        atrasoSuperior = "999";
        deuda = "1";
        deudaFinal = "9999999999";
        codigoRutacons = GeneralParameterEnum.CODIGORUTA.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.COBRO_JURIDICO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaFormateado();
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

        asignarOrigenDatos();
        reasignarOrigenGrilla();
        cargarListaCiclo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaFormateado();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar en la listaciclo la respuesta
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CobroJuridicoControladorUrlEnum.URL8448
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
     * Carga la lista listaFormateado
     *
     */
    public void cargarListaFormateado()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CLASE.getName(), "26");
        try
        {
            listaFormateado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CobroJuridicoControladorUrlEnum.URL8867
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
     * Carga la lista listaCodigoInicial
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar en la listaCodigoInicial la respuesa
     */
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroJuridicoControladorUrlEnum.URL9420
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutacons);
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar en la listacodigoFinal la respuesta
     */
    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CobroJuridicoControladorUrlEnum.URL10536
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(CobroJuridicoControladorEnum.CODIGORUTAINI.getValue(),
                        codigoRutaInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutacons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     * 
     */
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>
        codigoRutaInicial = null;
        codigoRutaFinal = null;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaDeEmision
     * 
     * 
     */
    public void cambiarFechaDeEmision()
    {
        // <CODIGO_DESARROLLADO>
        if (fechaEmicion == null)
        {
            fechaEmicion = new Date();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TxtAtraso
     * 
     */
    public void cambiarTxtAtraso()
    {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(atraso))
        {
            atraso = "1";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TxtDeuda
     * 
     * 
     */
    public void cambiarTxtDeuda()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(deuda))
        {
            deuda = "1";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TxtDeudaFinal
     * 
     * 
     */
    public void cambiarTxtDeudaFinal()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(deudaFinal))
        {
            deudaFinal = "9999999999";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TxtAtrasoSuperior
     * 
     */
    public void cambiarTxtAtrasoSuperior()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(atrasoSuperior))
        {
            atrasoSuperior = "999";
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Verificacion56
     * 
     * 
     */
    public void cambiarVerificacion56()
    {
        // <CODIGO_DESARROLLADO>
        if ("true".equals(checkVerificacion))
        {
            visibleAutomatico = false;
        }
        else
        {
            visibleAutomatico = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        codigoRutacons))
        {
            codigoRutaFinal = "0";
        }
        else
        {

            codigoRutaInicial = registroAux.getCampos().get(codigoRutacons)
                            .toString();
        }

        codigoRutaFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        codigoRutacons))
        {
            codigoRutaFinal = "zzz";
        }
        else
        {
            codigoRutaFinal = registroAux.getCampos().get(codigoRutacons)
                            .toString();

        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton modeloPlantilla en la
     * vista
     *
     *
     */
    public void oprimirmodeloPlantilla()
    {
        // <CODIGO_DESARROLLADO>

        validarCamposvacios();

        if (!validarConsecutivoVacio())
        {
            return;
        }

        try
        {
            String idCobroInicial;
            String idCobroFinal;
            String registroCoactivo = ejbServiciosPublicosDos
                            .registrarCoactivo(
                                            compania,
                                            consecutivo,
                                            Integer.parseInt(ciclo),
                                            codigoRutaInicial,
                                            codigoRutaFinal,
                                            atraso,
                                            atrasoSuperior,
                                            new BigDecimal(deuda),
                                            new BigDecimal(deudaFinal),
                                            SessionUtil.getUser().getCodigo());

            if (registroCoactivo.length() == 1)
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB1750"));
                return;
            }
            else
            {
                String[] rangoIdCobro = registroCoactivo.split("-");
                idCobroInicial = rangoIdCobro[0];
                idCobroFinal = rangoIdCobro[1];
            }

            Map<String, Object> params = new TreeMap<>();
            params.put(CobroJuridicoControladorEnum.TIPO.getValue(), "26");
            params.put(GeneralParameterEnum.CODIGO.getName(), formato);
            params.put(CobroJuridicoControladorEnum.FECHAGENERACION.getValue(),
                            fechaEmicion);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CobroJuridicoControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));

            Date fecha = (Date) rs.getCampos().get("FECHA");

            String strNombreDocumento = "COBRO COACTIVO " + " "
                + SysmanFunciones
                                .convertirAFechaCadena(fechaEmicion)
                                .replace("/", "")
                + "_"
                + SysmanFunciones.convertirAHoraCadena(fechaEmicion).replace(
                                ":",
                                "");

            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = formato;
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");

            variablesConsultaW.put("s$ciclo$s", "'" + ciclo + "'");
            variablesConsultaW.put("s$codigoRutaInicial$s",
                            "'" + codigoRutaInicial + "'");

            variablesConsultaW.put("s$codigoRutaFinal$s",
                            "'" + codigoRutaFinal + "'");

            variablesConsultaW.put("s$periodoAtrasoini$s",
                            "'" + atraso + "'");

            variablesConsultaW.put("s$periodoAtrasoFin$s",
                            "'" + atrasoSuperior + "'");

            variablesConsultaW.put("s$deuda$s",
                            deuda);

            variablesConsultaW.put("s$deudaFin$s",
                            deudaFinal);

            variablesConsultaW.put("s$idCobroInicial$s",
                            "'" + idCobroInicial + "'");

            variablesConsultaW.put("s$idCobroFinal$s",
                            "'" + idCobroFinal + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            campos,
                            valores);
        }
        catch (ParseException ex)
        {

            Logger.getLogger(CobroJuridicoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
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
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
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
     * Retorna la variable checkMedia
     * 
     * @return checkMedia
     */
    public String getCheckMedia()
    {
        return checkMedia;
    }

    /**
     * Asigna la variable checkMedia
     * 
     * @param checkMedia
     * Variable a asignar en checkMedia
     */
    public void setCheckMedia(String checkMedia)
    {
        this.checkMedia = checkMedia;
    }

    /**
     * Retorna la variable checkVerificacion
     * 
     * @return checkVerificacion
     */
    public String getCheckVerificacion()
    {
        return checkVerificacion;
    }

    /**
     * Asigna la variable checkVerificacion
     * 
     * @param checkVerificacion
     * Variable a asignar en checkVerificacion
     */
    public void setCheckVerificacion(String checkVerificacion)
    {
        this.checkVerificacion = checkVerificacion;
    }

    /**
     * Retorna la variable codigoRutaInicial
     * 
     * @return codigoRutaInicial
     */
    public String getCodigoRutaInicial()
    {
        return codigoRutaInicial;
    }

    /**
     * Asigna la variable codigoRutaInicial
     * 
     * @param codigoRutaInicial
     * Variable a asignar en codigoRutaInicial
     */
    public void setCodigoRutaInicial(String codigoRutaInicial)
    {
        this.codigoRutaInicial = codigoRutaInicial;
    }

    /**
     * Retorna la variable codigoRutaFinal
     * 
     * @return codigoRutaFinal
     */
    public String getCodigoRutaFinal()
    {
        return codigoRutaFinal;
    }

    /**
     * Asigna la variable codigoRutaFinal
     * 
     * @param codigoRutaFinal
     * Variable a asignar en codigoRutaFinal
     */
    public void setCodigoRutaFinal(String codigoRutaFinal)
    {
        this.codigoRutaFinal = codigoRutaFinal;
    }

    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato()
    {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato)
    {
        this.formato = formato;
    }

    public Date getFechaEmicion()
    {
        return fechaEmicion;
    }

    public void setFechaEmicion(Date fechaEmicion)
    {
        this.fechaEmicion = fechaEmicion;
    }

    /**
     * Retorna la variable atraso
     * 
     * @return atraso
     */
    public String getAtraso()
    {
        return atraso;
    }

    /**
     * Asigna la variable atraso
     * 
     * @param atraso
     * Variable a asignar en atraso
     */
    public void setAtraso(String atraso)
    {
        this.atraso = atraso;
    }

    /**
     * Retorna la variable deuda
     * 
     * @return deuda
     */
    public String getDeuda()
    {
        return deuda;
    }

    /**
     * Asigna la variable deuda
     * 
     * @param deuda
     * Variable a asignar en deuda
     */
    public void setDeuda(String deuda)
    {
        this.deuda = deuda;
    }

    /**
     * Retorna la variable deudaFinal
     * 
     * @return deudaFinal
     */
    public String getDeudaFinal()
    {
        return deudaFinal;
    }

    /**
     * Asigna la variable deudaFinal
     * 
     * @param deudaFinal
     * Variable a asignar en deudaFinal
     */
    public void setDeudaFinal(String deudaFinal)
    {
        this.deudaFinal = deudaFinal;
    }

    /**
     * Retorna la variable atrasoSuperior
     * 
     * @return atrasoSuperior
     */
    public String getAtrasoSuperior()
    {
        return atrasoSuperior;
    }

    /**
     * Asigna la variable atrasoSuperior
     * 
     * @param atrasoSuperior
     * Variable a asignar en atrasoSuperior
     */
    public void setAtrasoSuperior(String atrasoSuperior)
    {
        this.atrasoSuperior = atrasoSuperior;
    }

    /**
     * Retorna la variable consecutivo
     * 
     * @return consecutivo
     */
    public String getConsecutivo()
    {
        return consecutivo;
    }

    /**
     * Asigna la variable consecutivo
     * 
     * @param consecutivo
     * Variable a asignar en consecutivo
     */
    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaFormateado
     * 
     * @return listaFormateado
     */
    public List<Registro> getListaFormateado()
    {
        return listaFormateado;
    }

    /**
     * Asigna la lista listaFormateado
     * 
     * @param listaFormateado
     * Variable a asignar en listaFormateado
     */
    public void setListaFormateado(List<Registro> listaFormateado)
    {
        this.listaFormateado = listaFormateado;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>

    public boolean isVisibleAutomatico()
    {
        return visibleAutomatico;
    }

    public void setVisibleAutomatico(boolean visibleAutomatico)
    {
        this.visibleAutomatico = visibleAutomatico;
    }

    private boolean validarConsecutivoVacio()
    {
        boolean respuesta = true;
        if ("false".equals(checkVerificacion)
            && SysmanFunciones.validarVariableVacio(consecutivo))
        {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2799"));
            respuesta = false;

        }
        return respuesta;

    }

    private void validarCamposvacios()
    {
        if (SysmanFunciones.validarVariableVacio(ciclo))
        {
            ciclo = "1";
            cargarListaCodigoInicial();
        }
        if (SysmanFunciones.validarVariableVacio(codigoRutaInicial))
        {
            codigoRutaInicial = "0";
        }

        if (SysmanFunciones.validarVariableVacio(codigoRutaFinal))
        {
            codigoRutaFinal = "zzz";
        }
    }

}
