/*-
 * DisresreoegrSchipControlador.java
 *
 * 1.0
 *
 * 02/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.DisresreoegrSchipControladorEnum;
import com.sysman.cgr.enums.DisresreoegrSchipControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma disresreoegrschip asociada al formulario Disponibilidades, registros, obligaciones y pagos . Que permite generar un archivo de excel
 *
 * @version 1.0, 02/03/2017
 * @author jlramirez
 *
 * @version 2.0 16/08/2017
 * @author jrodriguezr Se elimina la conexion y se ajusta el manejo de excepciones
 */
@ManagedBean
@ViewScoped
public class DisresreoegrSchipControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo de la cuenta inicial seleccionada en la combo de cuenta inicial
     */
    private String cuentaInicial;
    /**
     * Atributo que almacena el codigo de la cuenta final seleccionada en la combo de cuenta final
     */
    private String cuentaFinal;
    /**
     * Atributo que almacena el a�o seleccionado en la combo de a�o
     */
    private int anio;
    /**
     * Atributo que almacena el nombre de la cuenta inicial seleccionada en la combo de cuenta inicial
     */
    private String nombreCuentaIni;
    /**
     * Atributo que almacena el nombre de la cuenta final seleccionada en la combo de cuenta final
     */
    private String nombreCuentaFin;
    /**
     * Atributo que almacena la fecha inicial seleccionada
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final seleccionada
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena falso o verdadero, dependiendo de si el indicador de libro de registros esta seleccionado o no
     */
    private boolean registros;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene la informaci�n de los detalles del combo del a�o.
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informacion de los detalles del combo grande de cuenta inicial
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que contiene la informacion de los detalles del combo grande de cuenta final
     */
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of DisresreoegrSchipControlador
     */
    public DisresreoegrSchipControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DISRESREOEGR_SCHIP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
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
        anio = SysmanFunciones
                        .ano(new Date());
        fechaInicial = fechaFinal = new Date();
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        registros = true;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnio
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DisresreoegrSchipControladorUrlEnum.URL6241
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
     * Carga la lista listaCuentaInicial
     */
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DisresreoegrSchipControladorUrlEnum.URL6711
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaCuentaFinal
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DisresreoegrSchipControladorUrlEnum.URL7731
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(DisresreoegrSchipControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo que llama una funcion que genera la consulta que sera utilizada para generar el archivo Excel
     */
    private void obtenerReporte()
    {
        try
        {
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("ano", anio);
            reemplazos.put("fechainicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazos.put("fechafinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazos.put("cuentainicial", cuentaInicial);
            reemplazos.put("cuentafinal", cuentaFinal);
            String consulta = Reporteador.resuelveConsulta("800099DISRESREOEGR",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            long cantidad = service.getConteoConsulta(consulta);
            if (cantidad > 0)
            {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(
                                consulta,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL97, "800099DISRESREOEGR");
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }

        }
        catch (SQLException | IOException | JRException
                        | DRException | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     */
    public void cambiarAnio()
    {
        // <CODIGO_DESARROLLADO>
        fechaInicial = fechaFinal = null;
        cuentaInicial = cuentaFinal = null;
        nombreCuentaFin = nombreCuentaIni = null;
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.ano(fechaInicial) != anio)
        {
            fechaInicial = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3494"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        if (fechaInicial != null)
        {
            if (!SysmanFunciones.comparaFechas(fechaInicial, fechaFinal))
            {
                fechaFinal = null;
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB3591"));
            }
            else if (SysmanFunciones.ano(fechaFinal) != anio)
            {
                fechaFinal = null;
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3494"));
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCuentaIni = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cuentaFinal = null;
        nombreCuentaFin = null;
        cargarListaCuentaFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCuentaFin = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable registros
     *
     * @return registros
     */
    public boolean getRegistros()
    {
        return registros;
    }

    /**
     * Asigna la variable registros
     *
     * @param registros
     * Variable a asignar en registros
     */
    public void setRegistros(boolean registros)
    {
        this.registros = registros;
    }

    /**
     * Retorna la variable cuentaInicial
     *
     * @return cuentaInicial
     */
    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     *
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     *
     * @return cuentaFinal
     */
    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     *
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public int getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la variable nombreCuentaIni
     *
     * @return nombreCuentaIni
     */
    public String getNombreCuentaIni()
    {
        return nombreCuentaIni;
    }

    /**
     * Asigna la variable nombreCuentaIni
     *
     * @param nombreCuentaIni
     * Variable a asignar en nombreCuentaIni
     */
    public void setNombreCuentaIni(String nombreCuentaIni)
    {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    /**
     * Retorna la variable nombreCuentaFin
     *
     * @return nombreCuentaFin
     */
    public String getNombreCuentaFin()
    {
        return nombreCuentaFin;
    }

    /**
     * Asigna la variable nombreCuentaFin
     *
     * @param nombreCuentaFin
     * Variable a asignar en nombreCuentaFin
     */
    public void setNombreCuentaFin(String nombreCuentaFin)
    {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     *
     * @return listaAnio
     */
    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     *
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     *
     * @return listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    /**
     * Asigna la lista listaCuentaInicial
     *
     * @param listaCuentaInicial
     * Variable a asignar en listaCuentaInicial
     */
    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * Retorna la lista listaCuentaFinal
     *
     * @return listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    /**
     * Asigna la lista listaCuentaFinal
     *
     * @param listaCuentaFinal
     * Variable a asignar en listaCuentaFinal
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
