/*-
 * RptNombramientoControlador.java
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
import com.sysman.hojasdevida.enums.RptNombramientoControladorEnum;
import com.sysman.hojasdevida.enums.RptNombramientoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * Esta clase es el controlador para el formulario Nombramientos en
 * Access "RptNombramiento", el cual es llamado desde
 * Suip\Informes\Informes básicos\Nombramientos
 *
 * 
 * @version 1.0, 13/12/2017
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class RptNombramientoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NUMERO_DCTO en el formulario
     */
    private final String cNumeroDcto;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el NIT del empleado inicial seleccionado
     * en el formulario
     */
    private String empleadoInicial;
    /**
     * Atributo que almacena el NIT del empleado final seleccionado en
     * el formulario
     */
    private String empleadoFinal;
    /**
     * Atributo que almacena el nombre del empleado inicial
     * seleccionado en el formulario
     */
    private String nombreEmpleadoInicial;
    /**
     * Atributo que almacena el nombre del empleado final seleccionado
     * en el formulario
     */
    private String nombreEmpleadoFinal;
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
     * Listado de registros para el comboBox de Empleado Inicial
     */
    private RegistroDataModelImpl listaEmpleadoInicial;
    /**
     * Listado de registros para el comboBox de Empleado Final
     */
    private RegistroDataModelImpl listaEmpleadoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptNombramientoControlador
     */
    public RptNombramientoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cNumeroDcto = GeneralParameterEnum.NUMERO_DCTO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPT_NOMBRAMIENTO_CONTROLADOR
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
        cargarListaEmpleadoInicial();
        cargarListaEmpleadoFinal();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaEmpleadoInicial
     *
     */
    public void cargarListaEmpleadoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptNombramientoControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleadoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cNumeroDcto);
    }

    /**
     * 
     * Carga la lista listaEmpleadoFinal
     *
     */
    public void cargarListaEmpleadoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptNombramientoControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RptNombramientoControladorEnum.EMPLEADOINICIAL.getValue(),
                        empleadoInicial);

        listaEmpleadoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cNumeroDcto);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista
     *
     *
     */
    public void oprimirBtnPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     *
     *
     */
    public void oprimirBtnExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleadoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleadoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoInicial = retornarString(registroAux, cNumeroDcto);
        nombreEmpleadoInicial = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        empleadoFinal = nombreEmpleadoFinal = null;
        cargarListaEmpleadoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleadoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleadoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoFinal = retornarString(registroAux, cNumeroDcto);
        nombreEmpleadoFinal = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato) {

        try {
            String informe = "001556RhvNombramientos";
            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("empleadoInicial", empleadoInicial);
            reemplazar.put("empleadoFinal", empleadoFinal);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable empleadoInicial
     * 
     * @return empleadoInicial
     */
    public String getEmpleadoInicial() {
        return empleadoInicial;
    }

    /**
     * Asigna la variable empleadoInicial
     * 
     * @param empleadoInicial
     * Variable a asignar en empleadoInicial
     */
    public void setEmpleadoInicial(String empleadoInicial) {
        this.empleadoInicial = empleadoInicial;
    }

    /**
     * Retorna la variable empleadoFinal
     * 
     * @return empleadoFinal
     */
    public String getEmpleadoFinal() {
        return empleadoFinal;
    }

    /**
     * Asigna la variable empleadoFinal
     * 
     * @param empleadoFinal
     * Variable a asignar en empleadoFinal
     */
    public void setEmpleadoFinal(String empleadoFinal) {
        this.empleadoFinal = empleadoFinal;
    }

    /**
     * Retorna la variable nombreEmpleadoInicial
     * 
     * @return nombreEmpleadoInicial
     */
    public String getNombreEmpleadoInicial() {
        return nombreEmpleadoInicial;
    }

    /**
     * Asigna la variable nombreEmpleadoInicial
     * 
     * @param nombreEmpleadoInicial
     * Variable a asignar en nombreEmpleadoInicial
     */
    public void setNombreEmpleadoInicial(String nombreEmpleadoInicial) {
        this.nombreEmpleadoInicial = nombreEmpleadoInicial;
    }

    /**
     * Retorna la variable nombreEmpleadoFinal
     * 
     * @return nombreEmpleadoFinal
     */
    public String getNombreEmpleadoFinal() {
        return nombreEmpleadoFinal;
    }

    /**
     * Asigna la variable nombreEmpleadoFinal
     * 
     * @param nombreEmpleadoFinal
     * Variable a asignar en nombreEmpleadoFinal
     */
    public void setNombreEmpleadoFinal(String nombreEmpleadoFinal) {
        this.nombreEmpleadoFinal = nombreEmpleadoFinal;
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
     * Retorna la lista listaEmpleadoInicial
     * 
     * @return listaEmpleadoInicial
     */
    public RegistroDataModelImpl getListaEmpleadoInicial() {
        return listaEmpleadoInicial;
    }

    /**
     * Asigna la lista listaEmpleadoInicial
     * 
     * @param listaEmpleadoInicial
     * Variable a asignar en listaEmpleadoInicial
     */
    public void setListaEmpleadoInicial(
        RegistroDataModelImpl listaEmpleadoInicial) {
        this.listaEmpleadoInicial = listaEmpleadoInicial;
    }

    /**
     * Retorna la lista listaEmpleadoFinal
     * 
     * @return listaEmpleadoFinal
     */
    public RegistroDataModelImpl getListaEmpleadoFinal() {
        return listaEmpleadoFinal;
    }

    /**
     * Asigna la lista listaEmpleadoFinal
     * 
     * @param listaEmpleadoFinal
     * Variable a asignar en listaEmpleadoFinal
     */
    public void setListaEmpleadoFinal(
        RegistroDataModelImpl listaEmpleadoFinal) {
        this.listaEmpleadoFinal = listaEmpleadoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
