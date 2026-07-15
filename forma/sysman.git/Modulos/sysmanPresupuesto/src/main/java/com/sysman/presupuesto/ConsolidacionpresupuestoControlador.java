/*-
 * ConsolidacionpresupuestoControlador.java
 *
 * 1.0
 *
 * 21/03/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.ConsolidacionpresupuestoControladorEnum;
import com.sysman.presupuesto.enums.ConsolidacionpresupuestoControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que ejecuta el proceso de consolidacion de companias
 *
 * @version 1.0, 21/03/2018
 * @author spina
 */
@ManagedBean
@ViewScoped
public class ConsolidacionpresupuestoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * codigo de la compania seleccionada
     */
    private String companiaSeleccion;
    /**
     * listado de anios para seleccionar el que se va a consolidar
     */
    private List<Registro> listaAno;
    /**
     * nombre de la compania seleccionada
     */
    private String nombreCompania;
    /**
     * anio seleccionado en el combo ano a consolidar
     */
    private String anio;
    /**
     * valor del nivel del codigo de rubro a consolidar ingresado en el campo nivel
     */
    private String nivel;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * variable que muestra el mensaje de dialogo preguntando si desea actualiar el indicador de permiteconsolidar en las companias de la tablas consolidadas
     *
     */
    private boolean dgActPermiteConsolVisible;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * combo con el listado de companias a consolidar
     */
    private RegistroDataModelImpl listaCompania;

    @EJB
    private EjbPresupuestoTresRemote ejbPresupuestoTres;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConsolidacionpresupuestoControlador
     */
    public ConsolidacionpresupuestoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CONSOLIDACIONPRESUPUESTO_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCompania();
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        dgActPermiteConsolVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCompania
     *
     */
    public void cargarListaCompania()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsolidacionpresupuestoControladorUrlEnum.URL001
                                                        .getValue());
        listaCompania = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsolidacionpresupuestoControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void seleccionarFilaCompania(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        companiaSeleccion = SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreCompania = SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.NOMBRE.getName()), "").toString();

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            dgActPermiteConsolVisible = false;
            // Totaliza el plan pptal de todas las companias que la consolidan
            int rta = Integer.parseInt(String.valueOf(
                            ejbPresupuestoTres.consolidarCompaniasPptales(
                                            companiaSeleccion,
                                            Integer.parseInt(anio),
                                            SessionUtil.getUser().getCodigo(),
                                            Integer.parseInt(nivel))));

            if (rta <= 0)
            {
                dgActPermiteConsolVisible = true;
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            // Totalizar saldos pptales para la compañnia a consolidar

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptaractpermiteconsolidar()
    {
        Registro reg = new Registro();
        reg.getCampos().put(
                        ConsolidacionpresupuestoControladorEnum.PERMITECONSOLIDAR
                                        .getValue(),
                        -1);
        reg.getLlave().put("CASE WHEN COMPANIA IN (SELECT NITCOMPANIA " +
            "          FROM CONSOLIDADA " +
            "          WHERE COMPANIACON = '" + companiaSeleccion
            + "') THEN -1 ELSE 0 END", -1);
        reg.getLlave().put(GeneralParameterEnum.ANO.getName(), anio);
        reg.getLlave().put(
                        ConsolidacionpresupuestoControladorEnum.PERMITECONSOLIDAR
                                        .getValue(),
                        0);
        reg.getLlave().put("CASE WHEN LENGTH(CODIGO) <= " + nivel
            + " THEN -1 ELSE 0 END", -1);
        try
        {
            int conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            ConsolidacionpresupuestoControladorEnum.PLAN_PRESUPUESTAL
                                            .getValue(),
                            reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB4056"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelaractpermiteconsolidar()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
    /**
     * Retorna la lista listaCompania
     *
     * @return listaCompania
     */
    public RegistroDataModelImpl getListaCompania()
    {
        return listaCompania;
    }

    /**
     * Asigna la lista listaCompania
     *
     * @param listaCompania
     * Variable a asignar en listaCompania
     */
    public void setListaCompania(RegistroDataModelImpl listaCompania)
    {
        this.listaCompania = listaCompania;
    }

    public String getCompaniaSeleccion()
    {
        return companiaSeleccion;
    }

    public void setCompaniaSeleccion(String companiaSeleccion)
    {
        this.companiaSeleccion = companiaSeleccion;
    }

    public String getNombreCompania()
    {
        return nombreCompania;
    }

    public void setNombreCompania(String nombreCompania)
    {
        this.nombreCompania = nombreCompania;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public String getCompania()
    {
        return compania;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public boolean isDgActPermiteConsolVisible()
    {
        return dgActPermiteConsolVisible;
    }

    public void setDgActPermiteConsolVisible(boolean dgActPermiteConsolVisible)
    {
        this.dgActPermiteConsolVisible = dgActPermiteConsolVisible;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
