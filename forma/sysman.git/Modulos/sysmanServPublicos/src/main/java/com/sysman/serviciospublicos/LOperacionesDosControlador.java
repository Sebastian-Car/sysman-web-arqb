/*-
 * LOperacionesDosControlador.java
 *
 * 1.0
 *
 * 24/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LOperacionesDosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario OperacionesDos.
 *
 * @version 1.0, 24/10/2016
 * @author Pablo A. Espitia Cuca.
 *
 * @version 2, 07/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 *
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del formulario y actualizaci�n de ConnectorPool
 *
 */

@ManagedBean
@ViewScoped
public class LOperacionesDosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Controla si la casilla de Reconexiones Automaticas esta seleccionada.
     */
    private boolean ckEstado;

    /** Contiene el ciclo seleccionado en el formulario. */
    private String ciclo;

    /**
     * Contiene el tipo de operacion seleccionado en el formulario.
     */
    private String tipoOperacion;

    /** Contiene el estado de la operacion ingresado. */
    private String estadoOperacion;

    /** Contiene la fecha inicial ingresada. */
    private Date fechaInicial;

    /** Contiene la fecha final ingresada. */
    private Date fechaFinal;

    /** Controla el bloqueo de la fecha inicial y final */
    private boolean visibleFecha;

    /** Contiene la hora inicial ingresada. */
    private Date horaInicial;

    /** Contiene la fecha final ingresada. */
    private Date horaFinal;

    /**
     * Controla si se debe filtrar por fecha y hora en el formulario.
     */
    private boolean condicionFechaHora;

    /**
     * Controla la visibilidad del combo Estado Operacion y la etiqueta asociada en el formulario. El valor true hace visible los componentes.
     */
    private boolean visibleEstadoOperacion;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Contiene la lista de items del combo ciclo. */
    private List<Registro> listaCiclo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LOperacionesDosControlador
     */
    public LOperacionesDosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.L_OPERACIONES_DOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>

            visibleFecha = (ckEstado ? false : true)
                && (condicionFechaHora ? false : true);

            /* Valores por defecto de los campos del formualrio. */
            ciclo = "T";
            tipoOperacion = "000";
            estadoOperacion = "T";

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
        cargarListaCiclo();
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
        /*
         * Verifica que el valor del parametro <OPERACIONES EJECUTADAS Y NO EJECUTADAS> este en SI.
         */
        try
        {
            visibleEstadoOperacion = "SI".equals(
                            SysmanFunciones.nvlStr(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            "OPERACIONES EJECUTADAS Y NO EJECUTADAS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        /*
         * Parametro que controla si se debe filtrar por fecha y hora.
         */
        condicionFechaHora = verificarParametro(
                        "MANEJA FECHA DE EJECUCION EN OPERACIONES");
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista de items del combo CICLO. */
    public void cargarListaCiclo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LOperacionesDosControladorUrlEnum.URL6946
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /** Gestiona los eventos del boton PDF. */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /** Gestiona los eventos del boton EXCEL. */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     *
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato)
    {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        /*
         * Parametro que establece cual de 2 reportes se debe generar.
         */
        boolean isReporte = verificarParametro(
                        "VER DESCRIPCION INFORME DE OPERACIONES");

        String reporte = "";
        String condicionFechaEjecucion = " ";
        String condicionFechaPagoPerProceso = "";
        String condicionFecha = "";
        String condicionHora = " ";
        String condicionEstado = " ";
        String condicionCiclo = filtrarCiclo();
        String condicionTipoOperacion = filtrarTipoOperacion();
        try
        {
            /* Selecciona el reporte .jasper */
            if ("N".equals(estadoOperacion) && isReporte)
            {
                reporte = "001163RegistroOperacionesOperacionDesc";
            }
            else
            {
                reporte = "001173RegistroOperacionesOperacionDos";
            }

            String[] aux = condicionarFechas();
            condicionFechaEjecucion = condicionFechaHora ? aux[0] : " ";
            condicionFechaPagoPerProceso = aux[1];
            condicionFecha = aux[2];

            condicionHora = filtrarHora();
            condicionEstado = filtrarEstadoOperacion();

            // <REEMPLAZAR VARIABLES EN CONSULTA>

            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("condicionTipoOperacion", condicionTipoOperacion);
            reemplazar.put("condicionHora", condicionHora);
            reemplazar.put("condicionEstado", condicionEstado);
            reemplazar.put("condicionFechaEjecucion", condicionFechaEjecucion);
            reemplazar.put("condicionFechaPagoPerProceso",
                            condicionFechaPagoPerProceso);
            reemplazar.put("condicionFecha", condicionFecha);

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_TIPO_OPERACION", tipoOperacion);
            parametros.put("PR_FECHAINICIAL", convertirFechaInicial());
            parametros.put("PR_FECHAFINAL", convertirFechaFinal());
            // </ENVIAR PARAMETROS AL REPORTE>

            if (validarFechaNula() && validarHoraNula())
            {
                Reporteador.resuelveConsulta(ckEstado
                    ? "001173RegistroOperacionesOperacionDos"
                    : "001163RegistroOperacionesOperacionDesc",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte,
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }

        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Valida que la fecha inicial y final no tengan valores nulos cuando: <br>
     * (1) El parametro MANEJA FECHA DE EJECUCION EN OPERACIONES tenga valor definido en SI.
     *
     * @return false si la fecha es nula.
     */
    public boolean validarFechaNula()
    {
        if ((fechaInicial != null) && (fechaFinal != null))
        {
            return true;
        }
        else
        {
            if (!ckEstado && condicionFechaHora)
            {
                return true;
            }

            if (!visibleFecha)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1787"));
                return false;
            }

            return true;
        }
    }

    /**
     * Valida que la hora inicial y final no tengan valores nulos cuando: <br>
     * (1) El indicador ckEstado no este marcado <br>
     * (2) El parametro MANEJA FECHA DE EJECUCION EN OPERACIONES tenga valor SI.
     *
     * @return false si la hora es nula.
     */
    public boolean validarHoraNula()
    {
        // no validar la hora
        if (ckEstado || !condicionFechaHora)
        {
            return true;
        }

        if ((horaInicial != null) && (horaFinal != null))
        {
            if (horaInicial.after(horaFinal))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3238"));
                return false;
            }
            return true;
        }

        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1788"));

        return false;
    }

    /**
     * Verifica si el parametro tiene el valor asignado en SI, de lo contrario retorna false.
     *
     * @param parametro
     * El parametro que se quiere evaluar.
     * @return Valor de verdad.
     */
    public boolean verificarParametro(String parametro)
    {
        boolean valor = false;

        try
        {
            valor = "SI".equals(
                            SysmanFunciones.nvlStr(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            parametro,
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "NO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valor;
    }

    /**
     * Convierte la fecha inicial en una cadena.
     *
     * @return La cadena de la fecha inicial.
     */
    public String convertirFechaInicial()
    {
        String fecha = " ";

        if (fechaInicial != null)
        {
            try
            {
                fecha = SysmanFunciones.convertirAFechaCadena(fechaInicial);
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return fecha;
    }

    /**
     * Convierte la fecha final en una cadena.
     *
     * @return La cadena de la fecha final.
     */
    public String convertirFechaFinal()
    {
        String fecha = " ";

        if (fechaFinal != null)
        {
            try
            {
                fecha = SysmanFunciones.convertirAFechaCadena(fechaFinal);
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return fecha;
    }

    /**
     * Para cada una de las fechas utilizadas en las consulta realiza la condicion correpondiente.
     *
     * @return Array de fechas formateadas.
     */
    public String[] condicionarFechas()
    {
        String[] fechas = { " ", " ", " " };

        if ((fechaInicial != null) && (fechaFinal != null))
        {

            /*
             * Resultado de formatear la fecha inicial y final.
             */
            String fInicial = SysmanFunciones.formatearFecha(fechaInicial);
            String fFinal = SysmanFunciones.formatearFecha(fechaFinal);

            /* Filtrar por fecha de ejecucion. */
            fechas[0] = " AND ITO.FECHAEJECUCION BETWEEN "
                + fInicial
                + " AND  " + fFinal;

            /* Filtrar por fecha de pago. */
            fechas[1] = " AND FECHAPAGOPERPROCESO BETWEEN "
                + fInicial + " AND " + fFinal;

            /* Filtrar por fecha de abono. */
            fechas[2] = " AND A.FECHA BETWEEN " + fInicial + " AND "
                + fFinal;
        }

        return fechas;
    }

    /**
     * Genera la condicion para filtrar por el ciclo. Filtra por el ciclo cuando el valor sea diferente de TODOS.
     *
     * @return La condicion del ciclo.
     */
    public String filtrarCiclo()
    {
        return "T".equals(ciclo) ? " "
            : " AND U.CICLO =" + ciclo;
    }

    /**
     * Evalua que la horaInicial y la horaFinal no tengan valor nulo. Genera la condicion para filtrar las horas que esten entre la horaInicial y la horaFinal.
     *
     * @return La condicion para filtrar por hora.
     */
    public String filtrarHora()
    {
        String condicionHora = " ";
        if ((horaInicial != null) && (horaFinal != null))
        {
            try
            {
                /*
                 * Filtrar las operaciones que esten entre la hora inicial y final.
                 */
                condicionHora = " AND TO_CHAR(ITO.HORAEJECUCION,'HH24:MI:SS') BETWEEN '"
                    + SysmanFunciones.convertirAHoraCadena(horaInicial)
                    + "' AND '"
                    + SysmanFunciones.convertirAHoraCadena(horaFinal)
                    + "'";
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return condicionHora;
    }

    /**
     * Filtra por tipoOperacion cuando el valor sea diferente de TODAS.
     *
     * @return La condicion para filtrar por tipo de operacion.
     */
    public String filtrarTipoOperacion()
    {
        return "000".equals(tipoOperacion)
            ? " " : " AND ITO.TIPO_OPERACION = '" + tipoOperacion + "' ";
    }

    /**
     * Genera la condicion para filtrar por el Estado de Operacion.
     *
     * @return La condicion para filtrar por ESTADO_OPE.
     */
    public String filtrarEstadoOperacion()
    {
        String condicionEstado = "";

        if (visibleEstadoOperacion)
        {
            switch (estadoOperacion)
            {
            case "S":
                condicionEstado = " AND ITO.ESTADO_OPE IS NULL ";
                break;
            case "T":
                condicionEstado = " ";
                break;
            default:
                condicionEstado = " AND ITO.ESTADO_OPE = "
                    + SysmanFunciones.colocarComillas(estadoOperacion);
                break;
            }
        }
        return condicionEstado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /** Gestiona los eventos de la casilla CheckEstado. */
    public void cambiarCheckEstado()
    {
        // <CODIGO_DESARROLLADO>
        visibleFecha = (ckEstado ? false : true)
            && (condicionFechaHora ? false : true);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    public boolean isCkEstado()
    {
        return ckEstado;
    }

    public void setCkEstado(boolean ckEstado)
    {
        this.ckEstado = ckEstado;
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
     * Retorna la variable operacion
     *
     * @return operacion
     */
    public String getTipoOperacion()
    {
        return tipoOperacion;
    }

    /**
     * Asigna la variable operacion
     *
     * @param operacion
     * Variable a asignar en operacion
     */
    public void setTipoOperacion(String operacion)
    {
        this.tipoOperacion = operacion;
    }

    /**
     * Retorna la variable estadoOperacion
     *
     * @return estadoOperacion
     */
    public String getEstadoOperacion()
    {
        return estadoOperacion;
    }

    /**
     * Asigna la variable estadoOperacion
     *
     * @param estadoOperacion
     * Variable a asignar en estadoOperacion
     */
    public void setEstadoOperacion(String estadoOperacion)
    {
        this.estadoOperacion = estadoOperacion;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public boolean isCondicionFechaHora()
    {
        return condicionFechaHora;
    }

    public void setCondicionFechaHora(boolean condicionFechaHora)
    {
        this.condicionFechaHora = condicionFechaHora;
    }

    /**
     * Retorna la variable horaInicial
     *
     * @return horaInicial
     */
    public Date getHoraInicial()
    {
        return horaInicial;
    }

    /**
     * Asigna la variable horaInicial
     *
     * @param horaInicial
     * Variable a asignar en horaInicial
     */
    public void setHoraInicial(Date horaInicial)
    {
        this.horaInicial = horaInicial;
    }

    /**
     * Retorna la variable horaFinal
     *
     * @return horaFinal
     */
    public Date getHoraFinal()
    {
        return horaFinal;
    }

    /**
     * Asigna la variable horaFinal
     *
     * @param horaFinal
     * Variable a asignar en horaFinal
     */
    public void setHoraFinal(Date horaFinal)
    {
        this.horaFinal = horaFinal;
    }

    public boolean isVisibleFecha()
    {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha)
    {
        this.visibleFecha = visibleFecha;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public boolean isVisibleEstadoOperacion()
    {
        return visibleEstadoOperacion;
    }

    public void setVisibleEstadoOperacion(boolean visibleEstado)
    {
        this.visibleEstadoOperacion = visibleEstado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
