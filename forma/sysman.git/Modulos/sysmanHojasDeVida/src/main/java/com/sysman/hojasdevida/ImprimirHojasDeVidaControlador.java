/*-
 * ImprimirHojasDeVidaControlador.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.ImprimirHojasDeVidaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma <code>imprimirhojasdevida</code>. Fue
 * migrado del formulario <code>ImprimirHojasDeVida</code> de Access.
 *
 * @version 1.0, 13/12/2017
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class ImprimirHojasDeVidaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>.
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NUMERO</code>.
     */
    private final String cNumero = GeneralParameterEnum.NUMERO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NUMERO_DCTO</code>.
     */
    private final String cNumeroDcto = GeneralParameterEnum.NUMERO_DCTO
                    .getName();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que indica si el check consolidado
     * <code>(CK1375)</code> esta seleccionado.
     */
    private boolean ckConsolidado;

    /**
     * Atributo que indica si el check historial unico
     * <code>(CK1376)</code> esta seleccionado.
     */
    private boolean ckHistorial;

    /**
     * Atributo que controla el valor check estado actual,
     * <code>CK1377</code>.
     */
    private boolean ckEstado;

    /**
     * Atributo que indica si el check entre fechas
     * <code>(CK1378)</code> esta seleccionado.
     */
    private boolean ckFechas;

    /**
     * Atributo que indica si el check listado <code>(CK1379)</code>
     * esta seleccionado.
     */
    private boolean ckListado;

    /**
     * Atributo que indica si el check ubicacion <code>(CK1380)</code>
     * esta seleccionado.
     */
    private boolean ckUbicacion;

    /**
     * Atributo que almacena el numero del documento seleccionado en
     * el combo: <code>CB4985</code>.
     */
    private String empleadoInicial;

    /**
     * Atributo que almacena el numero del documento seleccionado en
     * el combo: <code>CB4986</code>.
     */
    private String empleadoFinal;

    /**
     * Atributo que contiene el informe seleccionado en el combo
     * informacion acerca de, <code>CB4987</code>.
     */
    private int informe;

    /**
     * Atributo que almacena la fecha inicial ingresada en el campo
     * <code>CP47866</code>.
     */
    private Date fechaInicial;

    /**
     * Atributo que almacena la fecha inicial ingresada en el campo
     * <code>CP47867</code>.
     */
    private Date fechaFinal;

    /**
     * Atributo que contiene el valor del estado seleccionado en el
     * combo <code>CB4990</code>
     */
    private int valorEstado;

    /**
     * Atributo que contiene el texto que se debe mostrar en la
     * etiqueta <code>LB39241</code>.
     */
    private String subTitulo;

    /**
     * Indicador que controla la visibilidad de los componentes que
     * permiten filtrar entre empleados.
     */
    private boolean verRangoEmpleados;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los item del combo empleado inicial
     * <code>(CB4985)</code>.
     */
    private RegistroDataModelImpl listaCarpetaInicial;

    /**
     * Lista que contiene los item del combo empleado final
     * <code>(CB4986)</code>.
     */
    private RegistroDataModelImpl listaCarpetaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_EJBs>
    /**
     * Instancia que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_HOJAS_DE_VIDA</code>.
     */
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    // </DECLARAR_EJBs>
    /**
     * Crea una nueva instancia de ImprimirHojasDeVidaControlador
     */
    public ImprimirHojasDeVidaControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1499
            numFormulario = GeneralCodigoFormaEnum.IMPRIMIR_HOJAS_DE_VIDA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCarpetaInicial();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        verRangoEmpleados = true;
        subTitulo = idioma.getString("TB_TB3856");
        valorEstado = 1;
        informe = 1;
        empleadoInicial = empleadoFinal = "";
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la lista asociada al combo <code>CB4985</code>. */
    public void cargarListaCarpetaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHojasDeVidaControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumeroDcto);
    }

    /** Carga la lista asociada al combo <code>CB4986</code>. */
    public void cargarListaCarpetaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImprimirHojasDeVidaControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cNumero, empleadoInicial);

        listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumeroDcto);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton PDF <code>BT2703</code> en
     * la vista.
     */
    public void oprimirBtPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            ejbHojasDeVidaCero.validarFiltrosImpresionHV(ckListado, ckFechas,
                            ckEstado, ckConsolidado, ckHistorial, fechaInicial,
                            fechaFinal, empleadoInicial, empleadoFinal,
                            Integer.toString(informe),
                            Integer.toString(valorEstado));

            generarReporte(FORMATOS.PDF);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel <code>BT2704</code>
     * en la vista.
     */
    public void oprimirBtExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {
            ejbHojasDeVidaCero.validarFiltrosImpresionHV(ckListado, ckFechas,
                            ckEstado, ckConsolidado, ckHistorial, fechaInicial,
                            fechaFinal, empleadoInicial, empleadoFinal,
                            Integer.toString(informe),
                            Integer.toString(valorEstado));

            generarReporte(FORMATOS.EXCEL97);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el check entre fechas,
     * <code>1378</code>.
     */
    public void cambiarCkFechas() {
        // <CODIGO_DESARROLLADO>
        if (ckFechas) {
            verRangoEmpleados = false;
            subTitulo = idioma.getString("TB_TB3857");
            ckHistorial = ckConsolidado = ckEstado = false;
        }
        else {
            subTitulo = idioma.getString("TB_TB3856");
            verRangoEmpleados = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el check estado actual,
     * <code>CK1377</code>.
     */
    public void cambiarCkEstado() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el check consolidado,
     * <code>CK1375</code>.
     */
    public void cambiarCkConsolidado() {
        // <CODIGO_DESARROLLADO>
        informe = 0;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila del combo empleado
     * inicial, <code>CB4985</code>.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        empleadoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumeroDcto), "")
                        .toString();

        empleadoFinal = "";

        cargarListaCarpetaFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila del combo empleado
     * final, <code>CB4986</code>.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        empleadoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumeroDcto), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = seleccionarReporte();

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("empleadoInicial",
                        "'".concat(empleadoInicial).concat("'"));

        reemplazar.put("empleadoFinal",
                        "'".concat(empleadoFinal).concat("'"));

        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));

        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        // </REEMPLAZAR VARIABLES EN CONSULTA>

        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));

            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            /*-aqui reporte hace referencia al nombre del reporte*/
            archivoDescarga = JsfUtil.exportarStreamed(
                            seleccionarFormaReporte(reporte), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo utilizado para elegir el reporte a generar dependiendo
     * de la opcion seleccionada en el combo <code>CB4987</code>,
     * aplica solo cuando el check consolidado (<code>CK1375</code>)
     * esta marcado.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteConsolidado() {
        String reporte;

        switch (informe) {
        case 1:
            reporte = "001580ConsolidadoDatosPersonales";
            break;
        case 2:
            reporte = "001585ConsolidadoEducacionBasica";
            break;
        case 3:
            reporte = "001586ConsolidadoEducacionSuperior";
            break;
        case 4:
            reporte = "001587ConsolidadoPublicaciones";
            break;
        case 5:
            reporte = "001588ConsolidadoIdiomas";
            break;
        case 6:
            reporte = "001593ConsolidadoElaboral";
            break;
        case 7:
            reporte = "001594ConsolidadoRequisitosPosesion";
            break;
        case 8:
            reporte = "001595ConsolidadoNombramientos";
            break;
        case 9:
            reporte = "001596ConsolidadoEncargosTraslados";
            break;
        case 10:
            reporte = "001597ConsolidadoFunciones";
            break;
        case 11:
            reporte = "001598ConsolidadoRetiros";
            break;
        case 12:
            reporte = "001599ConsolidadoSanciones";
            break;
        default: // Opcion 13
            reporte = "001600ConsolidadoComisiones";
            break;
        }

        return reporte;
    }

    /**
     * Estructura que indica el reporte que se debe generar respecto a
     * la configuracion de las casillas de verificacion.
     * <blockquote> Observaciones:
     * <li>Se debe conservar el orden los condicionales.</blockquote>
     * 
     * @return El reporte a generar.
     */
    private String seleccionarReporte() {
        if (ckConsolidado) { // Consolidado
            return seleccionarReporteConsolidado();
        }

        if (ckFechas) { // Entre fechas
            return seleccionarReporteFechas();
        }

        if (ckHistorial) {
            return seleccionarReporteHistorial();
        }

        if (ckEstado) {
            return ckListado ? seleccionarReporteListadoEstado()
                : seleccionarReporteEstado();
        }

        return "001657HojasDeVida";
    }

    /**
     * Metodo utilizado para elegir el reporte a generar cuando el
     * check Entre Fechas (CK1378) esta seleccionado.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteFechas() {
        return ckListado ? "001603HojasDeVidaListadoFechas"
            : "001657HojasDeVidaEntreFechas";
    }

    /**
     * Metodo utilizado para elegir el reporte a generar cuando el
     * check Historial Unico (CK1376) esta seleccionado.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteHistorial() {
        return ckEstado ? seleccionarReporteEstadoHistorial()
            : (ckListado ? "001669HojasDeVidaListadoUnificada"
                : "001657HojasDeVidaUnificada");
    }

    /**
     * Metodo utilizado para elegir el reporte a generar dependiendo
     * de la opcion seleccionada en el combo Nombre
     * <code>CB4990</code>, aplica cuando las casillas Estado Actual
     * <code>(CK1375)</code> e Historial Unico <code>(CK1376)</code>
     * estan marcados.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteEstadoHistorial() {
        if (1 == valorEstado) {
            return ckListado ? "001605HojaDeVidaListadoUnificadaActivos"
                : "001657HojasDeVidaUnificadaActivos";
        }

        return ckListado ? "001606HojaDeVidaListadoUnificadaRetirados"
            : "001657HojasDeVidaUnificadaRetirados";
    }

    /**
     * Metodo utilizado para elegir el reporte a generar dependiendo
     * de la opcion seleccionada en el combo Nombre
     * <code>CB4990</code>, aplica cuando la casilla Estado Actual
     * <code>(CK1375)</code> es la unica marcada.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteEstado() {
        return 1 == valorEstado ? "001657HojasDeVidaActivos"
            : "001657HojasDeVidaRetirados";
    }

    /**
     * Metodo utilizado para elegir el reporte a generar dependiendo
     * de la opcion seleccionada en el combo Nombre
     * <code>CB4990</code>, aplica cuando las casillas Estado Actual
     * <code>(CK1375)</code> y Listado <code>(CK1379)</code> estan
     * marcadas.
     * 
     * @return El nombre del reporte a generar.
     */
    private String seleccionarReporteListadoEstado() {
        return 1 == valorEstado ? "001607HojasDeVidaListadoActivos"
            : "001608HojasDeVidaListadoRetirados";
    }

    /**
     * Determina el reporte en el cual se van a presentar los datos de
     * la consulta.
     * 
     * @param reporte
     * -> Nombre de la consulta.
     * @return El reporte de la consulta (jasper).
     */
    private String seleccionarFormaReporte(String reporte) {
        String forma;

        switch (reporte) {
        case "001657HojasDeVidaEntreFechas":
        case "001657HojasDeVidaUnificadaActivos":
        case "001657HojasDeVidaUnificadaRetirados":
        case "001657HojasDeVidaUnificada":
        case "001657HojasDeVidaActivos":
        case "001657HojasDeVidaRetirados":
            forma = "001657HojasDeVida";
            break;
        default:
            forma = reporte;
            break;
        }

        return forma;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isCkFechas() {
        return ckFechas;
    }

    public void setCkFechas(boolean ckFechas) {
        this.ckFechas = ckFechas;
    }

    public boolean isCkConsolidado() {
        return ckConsolidado;
    }

    public void setCkConsolidado(boolean ckConsolidado) {
        this.ckConsolidado = ckConsolidado;
    }

    public boolean isCkHistorial() {
        return ckHistorial;
    }

    public void setCkHistorial(boolean ckHistorial) {
        this.ckHistorial = ckHistorial;
    }

    public boolean isCkEstado() {
        return ckEstado;
    }

    public void setCkEstado(boolean ckEstado) {
        this.ckEstado = ckEstado;
    }

    public boolean isCkListado() {
        return ckListado;
    }

    public void setCkListado(boolean ckListado) {
        this.ckListado = ckListado;
    }

    public boolean isCkUbicacion() {
        return ckUbicacion;
    }

    public void setCkUbicacion(boolean ckUbicacion) {
        this.ckUbicacion = ckUbicacion;
    }

    public String getEmpleadoInicial() {
        return empleadoInicial;
    }

    public void setEmpleadoInicial(String empleadoInicial) {
        this.empleadoInicial = empleadoInicial;
    }

    public String getEmpleadoFinal() {
        return empleadoFinal;
    }

    public void setEmpleadoFinal(String empleadoFinal) {
        this.empleadoFinal = empleadoFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public boolean isVerRangoEmpleados() {
        return verRangoEmpleados;
    }

    public void setVerRangoEmpleados(boolean verRangoEmpleados) {
        this.verRangoEmpleados = verRangoEmpleados;
    }

    public int getValorEstado() {
        return valorEstado;
    }

    public void setValorEstado(int valorEstado) {
        this.valorEstado = valorEstado;
    }

    public int getInforme() {
        return informe;
    }

    public void setInforme(int informe) {
        this.informe = informe;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCarpetaInicial
     * 
     * @return listaCarpetaInicial
     */
    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     * 
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     * 
     * @return listaCarpetaFinal
     */
    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     * 
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
