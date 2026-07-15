/*-
 * InformeTiempoServicioControlador.java
 *
 * 1.0
 * 
 * 23/01/2018
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InformeTiempoServicioControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que permite obtener una serie de reportes, atravez de la
 * seleccion de unos atributos
 *
 * @version 1.0, 23/01/2018
 * @author jcaceres
 */
@ManagedBean
@ViewScoped
public class InformeTiempoServicioControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que contiene el valor asignado a la dependencia en la
     * forma del formulario.
     */
    private String dependencia;
    /**
     * Atributo que contiene el valor asignado al codigo del empleado
     * en la forma del formulario.
     */
    private String idEmpleado;
    /**
     * Atributo que contiene el valor asignado al centro de costos en
     * la forma del formulario.
     */
    private String centroCosto;
    /**
     * Atributo que contiene el valor asignado al momento de
     * seleccionar la opcion de pendencia
     * 
     */
    private boolean dependenciaVisible;
    /**
     * Atributo que contiene el valor asignado al momento de
     * seleccionar la opcion empleado
     * 
     */
    private boolean empleadoVisible;
    /**
     * Atributo que contiene el valor asignado al momento de
     * seleccionar la opcion centro de costo
     * 
     */
    private boolean centroVisible;
    /**
     * Atributo que contiene el valor asignado al momento de
     * seleccionar la opcion todos los empleados
     * 
     */
    private boolean todosEmpleados;
    /**
     * variable que toma el valor de la opcion seleccionada para
     * visualizar las opciones de un informe
     */
    private String opcion;
    /**
     * Atributo que contiene el valor asignado al nombre del empleado
     */
    private String nombreEmpleado;
    /**
     * Atributo que contiene el valor asignado al nombre de la
     * dependencia
     */
    private String nombreDependencia;
    /**
     * Atributo que contiene el valor asignado al nombre del centtro
     * de costo
     */
    private String centroCostoNombre;
    /**
     * Atributo que contiene el valor asignado a la fecha en la forma
     * del formulario.
     */
    private Date fecha;

    private String condicion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /** Lista que contiene los detalles de la opcion dependencia. */
    private RegistroDataModelImpl listadependencia;

    /** Lista que contiene los detalles de la opcion empleado. */
    private RegistroDataModelImpl listaidEmpleado;
    /**
     * Lista que contiene los detalles de la opcion centro de costo.
     */
    private RegistroDataModelImpl listaidCentroDeCosto;

    /**
     * Crea una nueva instancia de InformeTiempoServicioControlador
     */

    public InformeTiempoServicioControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMETIEMPOSERVICO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            empleadoVisible = false;
            dependenciaVisible = false;
            centroVisible = false;
            todosEmpleados = false;

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

        cargarListadependencia();
        cargarListaidEmpleado();
        cargarListaidCentroDeCosto();
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
        opcion = "1";
        empleadoVisible = true;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listadependencia
     *
     */
    public void cargarListadependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeTiempoServicioControladorUrlEnum.URL28521
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listadependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.DEPENDENCIA.getName());

    }

    /**
     * 
     * Carga la lista listaidEmpleado
     *
     */

    public void cargarListaidEmpleado()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeTiempoServicioControladorUrlEnum.URL28520
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaidEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());

    }

    /**
     * 
     * Carga la lista listaidCentroDeCosto
     *
     */
    public void cargarListaidCentroDeCosto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeTiempoServicioControladorUrlEnum.URL28522
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaidCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_CENTRO_DE_COSTO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Preliminar en la vista
     *
     *
     */
    public void oprimirPreliminar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando20()
    {
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL);
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Extension o tipo de documento a generar.
     */

    public void getInforme(FORMATOS formato)
    {
        if (validarOpcion() || validarOpcionDos())
        {

            try
            {

                Map<String, Object> reemplazos = new HashMap<>();
                Map<String, Object> parametro = new HashMap<>();
                reemplazos.put("compania", compania);
                reemplazos.put("fecha", SysmanFunciones.formatearFecha(fecha));
                reemplazos.put("condicion", condicion);
                parametro.put("PR_FORMS_FECHA",
                                SysmanFunciones.convertirAFechaCadena(fecha));
                parametro.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso().getNombre());
                parametro.put("PR_FECHA",
                                SysmanFunciones.formatearFecha(fecha));

                Reporteador.resuelveConsulta("001667InformeTiempoServicio",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametro);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001667InformeTiempoServicio", parametro,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException
                            | ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public boolean validarOpcion()
    {
        if (("1").equals(opcion) && validarCampo2())
        {
            condicion = SysmanFunciones.concatenar(
                            " AND PERSONAL.ID_DE_EMPLEADO = ", idEmpleado);
            return true;
        }

        else

        {
            return false;
        }

    }

    public boolean validarOpcionDos()
    {
        if ("4".equals(opcion) && validarCampo())
        {

            condicion = SysmanFunciones.concatenar(
                            " AND PERSONAL.DEPENDENCIA = '", dependencia, "'");
            return true;
        }
        if ("3".equals(opcion) && validarCampo())
        {

            condicion = SysmanFunciones.concatenar(
                            " AND PERSONAL.ID_CENTRO_DE_COSTO = '", centroCosto,
                            "'");
            return true;
        }
        if ("2".equals(opcion))
        {
            condicion = "";
            return true;
        }
        else
        {
            return false;
        }

    }

    // metodos de validacion de datos vacios teniendo en cuenta la
    // opcion chequeada en el formulario
    public boolean validarCampo()
    {

        if (("3").equals(opcion)

            && SysmanFunciones.validarVariableVacio(centroCosto))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2774"));
            return false;
        }

        else if (("4").equals(opcion)

            && SysmanFunciones.validarVariableVacio(dependencia))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2424"));
            return false;
        }

        else
        {
            return true;
        }
    }

    public boolean validarCampo2()
    {
        if (("1").equals(opcion)

            && SysmanFunciones.validarVariableVacio(idEmpleado))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            return false;
        }
        else
        {
            return true;
        }

    }

    /**
     * 
     * Metodo ejecutado para visualizar la opcion que tiene el usuario
     * segun la eleccion que tome
     *
     *
     */
    public void cambiarOpcion()
    {

        if ("1".equals(opcion))
        {

            empleadoVisible = true;
            dependenciaVisible = false;
            centroVisible = false;
            todosEmpleados = false;
            dependencia = "";
            centroCosto = "";
            idEmpleado = "";

        }
        else if ("2".equals(opcion))
        {
            todosEmpleados = true;
            dependenciaVisible = false;
            empleadoVisible = false;
            centroVisible = false;
            dependencia = "";
            centroCosto = "";
            idEmpleado = "0";

        }
        else if ("3".equals(opcion))
        {
            centroVisible = true;
            dependenciaVisible = false;
            empleadoVisible = false;
            todosEmpleados = false;
            dependencia = "";
            centroCosto = "";
            idEmpleado = "0";
        }
        else if ("4".equals(opcion))
        {

            dependenciaVisible = true;
            empleadoVisible = false;
            centroVisible = false;
            todosEmpleados = false;
            dependencia = "";
            centroCosto = "";
            idEmpleado = "0";

        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroAux.getCampos();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DEPENDENCIA"), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaidEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registroAux.getCampos();

        idEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
        nombreEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaidCentroDeCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilaidCentroDeCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        centroCosto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_CENTRO_DE_COSTO"),
                                        " ")
                        .toString();
        centroCostoNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

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
    /**
     * Retorna la lista listadependencia
     * 
     * @return listadependencia
     */

    public RegistroDataModelImpl getListadependencia()
    {
        return listadependencia;
    }

    /**
     * Asigna la lista listadependencia
     * 
     * @param listadependencia
     * Variable a asignar en listadependencia
     */
    public void setListadependencia(RegistroDataModelImpl listadependencia)
    {
        this.listadependencia = listadependencia;
    }

    /**
     * Retorna la lista listaidEmpleado
     * 
     * @return listaidEmpleado
     */
    public RegistroDataModelImpl getListaidEmpleado()
    {
        return listaidEmpleado;
    }

    /**
     * Asigna la lista listaidEmpleado
     * 
     * @param listaidEmpleado
     * Variable a asignar en listaidEmpleado
     */
    public void setListaidEmpleado(RegistroDataModelImpl listaidEmpleado)
    {
        this.listaidEmpleado = listaidEmpleado;
    }

    /**
     * Retorna la lista listaidCentroDeCosto
     * 
     * @return listaidCentroDeCosto
     */
    public RegistroDataModelImpl getListaidCentroDeCosto()
    {
        return listaidCentroDeCosto;
    }

    /**
     * Asigna la lista listaidCentroDeCosto
     * 
     * @param listaidCentroDeCosto
     * Variable a asignar en listaidCentroDeCosto
     */
    public void setListaidCentroDeCosto(
        RegistroDataModelImpl listaidCentroDeCosto)
    {
        this.listaidCentroDeCosto = listaidCentroDeCosto;
    }

    /**
     * @return the dependencia
     */
    public String getDependencia()
    {
        return dependencia;
    }

    /**
     * @param dependencia
     * the dependencia to set
     */
    public void setDependencia(String dependencia)
    {
        this.dependencia = dependencia;
    }

    /**
     * @return the idEmpleado
     */
    public String getIdEmpleado()
    {
        return idEmpleado;
    }

    /**
     * @param idEmpleado
     * the idEmpleado to set
     */
    public void setIdEmpleado(String idEmpleado)
    {
        this.idEmpleado = idEmpleado;
    }

    /**
     * @return the centroCosto
     */
    public String getCentroCosto()
    {
        return centroCosto;
    }

    /**
     * @param centroCosto
     * the centroCosto to set
     */
    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    public boolean isDependenciaVisible()
    {
        return dependenciaVisible;
    }

    /**
     * @param dependenciaVisible
     * the dependenciaVisible to set
     */
    public void setDependenciaVisible(boolean dependenciaVisible)
    {
        this.dependenciaVisible = dependenciaVisible;
    }

    /**
     * @return the empleadoVisible
     */
    public boolean isEmpleadoVisible()
    {
        return empleadoVisible;
    }

    /**
     * @param empleadoVisible
     * the empleadoVisible to set
     */
    public void setEmpleadoVisible(boolean empleadoVisible)
    {
        this.empleadoVisible = empleadoVisible;
    }

    /**
     * @return the centroVisible
     */
    public boolean isCentroVisible()
    {
        return centroVisible;
    }

    /**
     * @param centroVisible
     * the centroVisible to set
     */
    public void setCentroVisible(boolean centroVisible)
    {
        this.centroVisible = centroVisible;
    }

    /**
     * @return the todosEmpleados
     */
    public boolean isTodosEmpleados()
    {
        return todosEmpleados;
    }

    /**
     * @param todosEmpleados
     * the todosEmpleados to set
     */
    public void setTodosEmpleados(boolean todosEmpleados)
    {
        this.todosEmpleados = todosEmpleados;
    }

    /**
     * @return the opcion
     */
    public String getOpcion()
    {
        return opcion;
    }

    /**
     * @param opcion
     * the opcion to set
     */
    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    /**
     * @return the nombreEmpleado
     */
    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    /**
     * @param nombreEmpleado
     * the nombreEmpleado to set
     */
    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    /**
     * @return the nombreDependencia
     */
    public String getNombreDependencia()
    {
        return nombreDependencia;
    }

    /**
     * @param nombreDependencia
     * the nombreDependencia to set
     */
    public void setNombreDependencia(String nombreDependencia)
    {
        this.nombreDependencia = nombreDependencia;
    }

    /**
     * @return the centroCostoNombre
     */
    public String getCentroCostoNombre()
    {
        return centroCostoNombre;
    }

    /**
     * @param centroCostoNombre
     * the centroCostoNombre to set
     */
    public void setCentroCostoNombre(String centroCostoNombre)
    {
        this.centroCostoNombre = centroCostoNombre;
    }

    /**
     * @return the fecha
     */
    public Date getFecha()
    {
        return fecha;
    }

    /**
     * @param fecha
     * the fecha to set
     */
    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}
