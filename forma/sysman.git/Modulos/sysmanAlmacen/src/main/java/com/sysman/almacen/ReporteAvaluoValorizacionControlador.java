/*-
 * ReporteAvaluoValorizacionControlador.java
 *
 * 1.0
 * 
 * 26/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.ReporteAvaluoValorizacionControladorEnum;
import com.sysman.almacen.enums.ReporteAvaluoValorizacionControladorUrlEnum;
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
 * Esta clase es el controlador para el formulario
 * "Reporte de Valorizaciones por Devolutivo" en Access
 * "RAvaluoValorizacion", el cual es llamado desde
 * Almacen\Informes\Especiales\Informe de Avaluos y Valorización
 *
 * 
 * @version 1.0, 26/01/2017
 * @author amonroy
 * 
 * @version 2, 10/05/2017, pespitia:<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class ReporteAvaluoValorizacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo que identifica al modulo
     * Almacen
     */
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGOELEMENTO en el formulario, almacena el
     * nombre del enumerado:
     * <code>GeneralParameterEnum.CODIGOELEMENTO</code>.
     */
    private final String strCodigoElemento;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del check detallado en el
     * formulario
     */
    private boolean detallado;
    /**
     * Atributo que almacena el codigo del elemento inicial
     * seleccionado en el formulario
     */
    private String elementoInicial;
    /**
     * Atributo que almacena el codigo del elemento final seleccionado
     * en el formulario
     */
    private String elementoFinal;
    /**
     * Atributo que almacena el nombre asociado al codigo del elemento
     * inicial
     */
    private String nombreInicial;
    /**
     * Atributo que almacena el nombre asociado al codigo del elemento
     * final
     */
    private String nombreFinal;
    /**
     * Atributo que almacena el valor ingresado en el campo placa
     * inicial
     */
    private String placaInicial;
    /**
     * Atributo que almacena el valor ingresado en el campo placa
     * final
     */
    private String placaFinal;
    /**
     * Atributo que almacena el valor seleccionado en la fecha final
     */
    private Date fechaFin;
    /**
     * Atributo que almacena el valor seleccionado en la fecha inicial
     */
    private Date fechaInicial;
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
     * Listado de registros para el comboBox de elemento inicial
     */
    private RegistroDataModelImpl listaElementoInicial;
    /**
     * Listado de registros para el comboBox de elemento final
     */
    private RegistroDataModelImpl listaElementoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ReporteAvaluoValorizacionControlador
     */
    public ReporteAvaluoValorizacionControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigoElemento = GeneralParameterEnum.CODIGOELEMENTO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_AVALUO_VALORIZACION_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            fechaInicial = new Date();
            fechaFin = new Date();
            elementoInicial = "2";
            elementoFinal = "99999999";
            placaInicial = "1";
            placaFinal = "99999999999";
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
        cargarListaElementoInicial();
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
        /*
         * FR1271-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 10, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaElementoInicial
     *
     */
    public void cargarListaElementoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteAvaluoValorizacionControladorUrlEnum.URL6594
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoElemento);
    }

    /**
     * 
     * Carga la lista listaElementoFinal
     *
     */
    public void cargarListaElementoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteAvaluoValorizacionControladorUrlEnum.URL7630
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ReporteAvaluoValorizacionControladorEnum.ELEMENTOINICIAL
                        .getValue(), elementoInicial);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoElemento);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     * Hace el llamado al metodo "generarInforme" indicando el formato
     * con el que se desea generar el informe
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoInicial
     *
     * Actualiza los campos nombreInicial, elementoFinal y nombreFinal
     * , adicionalmente carga la lista de elemento final
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigoElemento), "")
                        .toString();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

        elementoFinal = nombreFinal = null;

        cargarListaElementoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElementoFinal
     *
     * Actualiza el nombre del elemento final de acuerdo al codigo
     * final seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        elementoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOELEMENTO"), "")
                        .toString();

        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */

    public void generarInforme(FORMATOS formato) {
        try {
            String nombreReporte = detallado
                ? "001373ValorizacionesDetallado"
                : "001379ValorizacionesGrupo";

            String adicionSelect = detallado ? ""
                : ",SUBSTR(VALORIZACIONDEVOLUTIVO.ELEMENTO,1,3) GRUPO";
            // HashMap reemplazar: Envio de reemplazos para la
            // consulta almacenada en la BD
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("elementoInicial", elementoInicial);
            reemplazar.put("elementoFinal", elementoFinal);
            reemplazar.put("placaInicial", placaInicial);
            reemplazar.put("placaFinal", placaFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFin",
                            SysmanFunciones.formatearFecha(fechaFin));
            reemplazar.put("adicionSelect", adicionSelect);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFIN", SysmanFunciones
                            .convertirAFechaCadena(fechaFin));
            parametros.put("PR_ELEMENTOINICIAL", elementoInicial);
            parametros.put("PR_ELEMENTOFINAL", elementoFinal);
            parametros.put("PR_PLACAINICIAL", placaInicial);
            parametros.put("PR_PLACAFINAL", placaFinal);

            Reporteador.resuelveConsulta("001373ValorizacionesDetallado",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable detallado
     * 
     * @return detallado
     */
    public boolean getDetallado() {
        return detallado;
    }

    /**
     * Asigna la variable detallado
     * 
     * @param detallado
     * Variable a asignar en detallado
     */
    public void setDetallado(boolean detallado) {
        this.detallado = detallado;
    }

    /**
     * Retorna la variable elementoInicial
     * 
     * @return elementoInicial
     */
    public String getElementoInicial() {
        return elementoInicial;
    }

    /**
     * Asigna la variable elementoInicial
     * 
     * @param elementoInicial
     * Variable a asignar en elementoInicial
     */
    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }

    /**
     * Retorna la variable elementoFinal
     * 
     * @return elementoFinal
     */
    public String getElementoFinal() {
        return elementoFinal;
    }

    /**
     * Asigna la variable elementoFinal
     * 
     * @param elementoFinal
     * Variable a asignar en elementoFinal
     */
    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial
     * Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal
     * Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    /**
     * Retorna la variable placaInicial
     * 
     * @return placaInicial
     */
    public String getPlacaInicial() {
        return placaInicial;
    }

    /**
     * Asigna la variable placaInicial
     * 
     * @param placaInicial
     * Variable a asignar en placaInicial
     */
    public void setPlacaInicial(String placaInicial) {
        this.placaInicial = placaInicial;
    }

    /**
     * Retorna la variable placaFinal
     * 
     * @return placaFinal
     */
    public String getPlacaFinal() {
        return placaFinal;
    }

    /**
     * Asigna la variable placaFinal
     * 
     * @param placaFinal
     * Variable a asignar en placaFinal
     */
    public void setPlacaFinal(String placaFinal) {
        this.placaFinal = placaFinal;
    }

    /**
     * Retorna la variable fechaFin
     * 
     * @return fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Asigna la variable fechaFin
     * 
     * @param fechaFin
     * Variable a asignar en fechaFin
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaElementoInicial
     * 
     * @return listaElementoInicial
     */
    public RegistroDataModelImpl getListaElementoInicial() {
        return listaElementoInicial;
    }

    /**
     * Asigna la lista listaElementoInicial
     * 
     * @param listaElementoInicial
     * Variable a asignar en listaElementoInicial
     */
    public void setListaElementoInicial(
        RegistroDataModelImpl listaElementoInicial) {
        this.listaElementoInicial = listaElementoInicial;
    }

    /**
     * Retorna la lista listaElementoFinal
     * 
     * @return listaElementoFinal
     */
    public RegistroDataModelImpl getListaElementoFinal() {
        return listaElementoFinal;
    }

    /**
     * Asigna la lista listaElementoFinal
     * 
     * @param listaElementoFinal
     * Variable a asignar en listaElementoFinal
     */
    public void setListaElementoFinal(
        RegistroDataModelImpl listaElementoFinal) {
        this.listaElementoFinal = listaElementoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
