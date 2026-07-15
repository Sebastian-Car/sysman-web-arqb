/*-
 * LFinanDeudaControlador.java
 *
 * 1.0
 * 
 * 04/01/2017
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
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LFinanDeudaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario
 * "Modificaciones por Finaciacion" en Access "LFinanDeuda", el cual
 * es llamado desde Facturacion\Informes\Facturaci�n y
 * cartera\Reporte de deudas diferidas
 *
 * 
 * @author amonroy
 * @version 1.0, 04/01/2017
 * @version 2.0, 15/06/2017 Proceso de Refactoring a los listados del
 * Controlador
 */
@ManagedBean
@ViewScoped
public class LFinanDeudaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo que identifica al modulo de
     * servicios publicos
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo seleccionado en el formulario
     */
    private String ciclo;
    /**
     * Atributo que almacena el anio inicial seleccionado en el
     * formulario
     */
    private String anioInicial;
    /**
     * Atributo que almacena el periodo inicial seleccionado en el
     * formulario
     */
    private String periodoInicial;
    /**
     * Atributo que almacena el anio final seleccionado en el
     * formulario
     */
    private String anioFinal;
    /**
     * Atributo que almacena el periodo final seleccionado en el
     * formulario
     */
    private String periodoFinal;
    /**
     * Atributo que almacena el nombre del informe a generar de
     * acuerdo a la opci�n seleccionada en e
     */
    private String informe;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Listado de registros para el comboBox de anio inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Listado de registros para el comboBox de periodo inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Listado de registros para el comboBox de anio final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Listado de registros para el comboBox de periodo final
     */
    private List<Registro> listaPeriodoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LFinanDeudaControlador
     * 
     * Asigna valores iniciales a los atributos del formulario
     */
    public LFinanDeudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.L_FINAN_DEUDA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            String mesActual = String.valueOf(SysmanFunciones.mes(new Date()));
            anioInicial = anioFinal = String
                            .valueOf(SysmanFunciones.ano(new Date()));
            periodoInicial = periodoFinal = mesActual.length() == 1
                ? "0" + mesActual
                : mesActual;
            ciclo = "T";
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
        cargarListaCiclo();
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanDeudaControladorUrlEnum.URL6289
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnoInicial
     *
     */
    public void cargarListaAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanDeudaControladorUrlEnum.URL6852
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodoInicial
     *
     */
    public void cargarListaPeriodoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanDeudaControladorUrlEnum.URL7298
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaAnoFinal
     *
     */
    public void cargarListaAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LFinanDeudaControladorUrlEnum.URL7896
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodoFinal
     *
     */
    public void cargarListaPeriodoFinal() {
        Map<String, Object> param = new TreeMap<>();
        String urlListaPeriodoFinal;
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if (anioInicial != anioFinal) {
            param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
            urlListaPeriodoFinal = LFinanDeudaControladorUrlEnum.URL7298
                            .getValue();
        }
        else {
            param.put(GeneralParameterEnum.ANO.getName(),
                            anioFinal);
            param.put(GeneralParameterEnum.MES.getName(), periodoInicial);
            urlListaPeriodoFinal = LFinanDeudaControladorUrlEnum.URL8572
                            .getValue();
        }

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            urlListaPeriodoFinal)
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista Hace
     * el llamado al metodo "generarInforme" indicando el formato con
     * el que se desea generar
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
     * con el que se desea generar
     *
     *
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
    /**
     * Actualiza el valor del anio final al seleccionar el anio
     * inicial, adicionalmente carga la lista del periodo inicial y
     * del anio final
     */
    public void cambiarAnoInicial() {
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        anioFinal = anioInicial;
    }

    /**
     * Realiza la carga del periodo final al seleccionar un anio final
     * diferente
     */
    public void cambiarAnoFinal() {
        cargarListaPeriodoFinal();
    }

    /**
     * Actualiza el valor del periodo final al seleccionar el periodo
     * inicial, adicionalmente carga la lista del periodo final
     */
    public void cambiarPeriodoInicial() {
        cargarListaPeriodoFinal();
        periodoFinal = periodoInicial;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Permite definir el reporte que se va a generar dependiendo de
     * la opcion seleccionada en el informe
     * 
     * @return nombre del informe
     */
    public String obtenerNombreInforme() {
        String nombre = "";
        switch (informe) {
        case "1":
            nombre = "001354LFinanDeuda";
            break;
        case "2":
            nombre = "001355LFinanDeudaConcepto";
            break;
        case "3":
            nombre = "001357LFinanDeudaUsuarioConcepto";
            break;
        default:
            break;
        }
        return nombre;
    }

    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y env�a
     * los par�metros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String reporte = obtenerNombreInforme();
        // Valor para el reemplazo filtro de los informes
        String filtro = "T".equals(ciclo) ? " "
            : " AND DEUDA.CICLO = " + ciclo + " ";

        try {
            // HashMap reemplazar es para que reemplace en la
            // consulta almacenada en la tabla CONSULTAS
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("filtro", filtro);
            reemplazar.put("anioInicial", anioInicial);
            reemplazar.put("periodoInicial", periodoInicial);
            reemplazar.put("anioFinal", anioFinal);
            reemplazar.put("periodoFinal", periodoFinal);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_ANIOINICIAL", anioInicial);
            parametros.put("PR_PERIODOINICIAL", periodoInicial);
            parametros.put("PR_ANIOFINAL", anioFinal);
            parametros.put("PR_PERIODOFINAL", periodoFinal);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable informe
     * 
     * @return informe
     */
    public String getInforme() {
        return informe;
    }

    /**
     * Asigna la variable informe
     * 
     * @param informe
     * Variable a asignar en informe
     */
    public void setInforme(String informe) {
        this.informe = informe;
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
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     * 
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     * 
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     * 
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     * 
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     * 
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     * 
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
