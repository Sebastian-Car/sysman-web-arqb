/*-
 * FacturacionConjuntaControlador.java
 *
 * 1.0
 * 
 * 28/10/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.impl.EjbServiciosPublicosCero;
import com.sysman.serviciospublicos.enums.FacturacionConjuntaControladorEnum;
import com.sysman.serviciospublicos.enums.FacturacionConjuntaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
 * Esta clase es el controlador para el formulario Facturacion
 * Conjunta y Conenios en Access "FrmFactConjunta", el cual es llamado
 * desde Facturacion\Informes\Generales\Facturacion Conjunta y
 * Convenio
 *
 * @version 1.0, 28/10/2016
 * @author amonroy
 * 
 * @author eamaya
 * @version 2, 18/05/2017 Proceso de Refactoring y Manejo de EJBs
 */
@ManagedBean
@ViewScoped

public class FacturacionConjuntaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que se definepara el reemplazo de la palabra
     * CODIGORUTA
     */
    private final String strCodigo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar si se ha seleccionado el check
     * de totalizado
     */
    private boolean totalizado;
    /**
     * Atributo que almacena el codigo de ruta desde el cual se desea
     * generar el informe
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el codigo de ruta final que se desea ver
     * en el informe
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el anio seleccionado en el formulario
     */
    private String anio;
    /**
     * Atributo que almacena el periodo del ciclo seleccionado
     */
    private String periodo;
    /**
     * Atributo que almacena el ciclo seleccionado en el formulario
     */
    private String ciclo;
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
    private List<Registro> listaCmbCiclo;
    /**
     * Listado de registros para el comboBox de anio
     */
    private List<Registro> listaCmbAnio;
    /**
     * Listado de registros para el comboBox de periodo
     */
    private List<Registro> listaCmbPeriodo;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de codigo de ruta inicial
     */
    private RegistroDataModelImpl listaCmbCodigoInicial;
    /**
     * Listado de registros para el comboBox de codigo de ruta final
     */
    private RegistroDataModelImpl listaCmbCodigoFinal;

    @EJB

    private EjbServiciosPublicosCero ejbServicioPublico;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FacturacionConjuntaControlador
     */
    public FacturacionConjuntaControlador() {
        super();
        compania = SessionUtil.getCompania();
        strCodigo = "CODIGORUTA";
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURACION_CONJUNTA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            periodo = "01";
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
        cargarListaCmbCiclo();
        cargarListaCmbAnio();
        cargarListaCmbPeriodo();
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
     * Carga la lista listaCmbCiclo
     *
     */
    public void cargarListaCmbCiclo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCmbCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturacionConjuntaControladorUrlEnum.URL7503
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
     * Carga la lista del combo grande listaCmbCodigoInicial
     *
     */
    public void cargarListaCmbCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionConjuntaControladorUrlEnum.URL6132
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FacturacionConjuntaControladorEnum.PARAM0.getValue(),
                        ciclo);

        listaCmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * 
     * Carga la lista del combo grande listaCmbCodigoFinal
     *
     */
    public void cargarListaCmbCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionConjuntaControladorUrlEnum.URL6940
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(FacturacionConjuntaControladorEnum.PARAM0.getValue(),
                        ciclo);

        param.put(FacturacionConjuntaControladorEnum.PARAM1.getValue(),
                        codigoInicial);

        listaCmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * 
     * Carga la lista listaCmbAnio
     */
    public void cargarListaCmbAnio() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));

            listaCmbAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturacionConjuntaControladorUrlEnum.URL8108
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
     * Carga la lista listaCmbPeriodo
     */
    public void cargarListaCmbPeriodo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaCmbPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturacionConjuntaControladorUrlEnum.URL8695
                                                                            .getValue())
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
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista
     * Realiza el llamado al m�todo que genera los informes definiendo
     * el formato de Excel
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
     * Realiza el llamado al m�todo que genera los informes definiendo
     * el formato de PDF
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
     * Metodo ejecutado al cambiar el control CmbAnio
     * 
     */
    public void cambiarCmbAnio() {
        // <CODIGO_DESARROLLADO>
        cargarListaCmbPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Asigna los valores por omisi�n a los combos de codigo inicial y
     * final de acuerdo al ciclo seleccionado. Llama el metodo para
     * cargar la lista de codigo de ruta inicial
     */
    public void cambiarCmbCiclo() {
        if (("T").equalsIgnoreCase(ciclo)) {
            codigoInicial = "0000000000000000";
            codigoFinal = "9999999999999999";
        }
        else {
            codigoInicial = "";
            codigoFinal = "";
        }

        cargarListaCmbCodigoInicial();
    }

    public void cambiarCheckAgrupado() {
        // Al seleccionar el check de totalizar se ocultan algunas
        // etiquetas y combos, eso se esta controlando desde la forma
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCodigoInicial. Lama el metodo para cargar la lista de
     * codigo de ruta final
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(strCodigo).toString(), "");
        cargarListaCmbCodigoFinal();
        codigoFinal = "";
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(strCodigo).toString(), "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y env�a los
     * par�metros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
       
        try {
            String reporte = totalizado
                ? "001187InfFactConjuntaAgrup"
                : "001184InfFactConjunta";

            String condicionCiclo = !("T").equalsIgnoreCase(ciclo)
                ? "CICLO = " + ciclo + " AND"
                : "";
            String nombrePeriodo = ejbServicioPublico.asignarNombrePeriodo(
                            compania, Integer.parseInt(anio), periodo, null);

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("anio", anio);
            reemplazar.put("periodo", periodo);
            if (("001184InfFactConjunta").equalsIgnoreCase(reporte)) {
                reemplazar.put("codigoInicial", codigoInicial);
                reemplazar.put("codigoFinal", codigoFinal);
            }

            parametros.put("PR_ANIO", anio);
            parametros.put("PR_PERIODO", periodo);
            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

 
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException | NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
      
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable totalizado
     * 
     * @return totalizado
     */
    public boolean getTotalizado() {
        return totalizado;
    }

    /**
     * Asigna la variable totalizado
     * 
     * @param totalizado
     * Variable a asignar en totalizado
     */
    public void setTotalizado(boolean totalizado) {
        this.totalizado = totalizado;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

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
     * Retorna la lista listaCmbCiclo
     * 
     * @return listaCmbCiclo
     */
    public List<Registro> getListaCmbCiclo() {
        return listaCmbCiclo;
    }

    /**
     * Asigna la lista listaCmbCiclo
     * 
     * @param listaCmbCiclo
     * Variable a asignar en listaCmbCiclo
     */
    public void setListaCmbCiclo(List<Registro> listaCmbCiclo) {
        this.listaCmbCiclo = listaCmbCiclo;
    }

    /**
     * Retorna la lista listaCmbAnio
     * 
     * @return listaCmbAnio
     */
    public List<Registro> getListaCmbAnio() {
        return listaCmbAnio;
    }

    /**
     * Asigna la lista listaCmbAnio
     * 
     * @param listaCmbAnio
     * Variable a asignar en listaCmbAnio
     */
    public void setListaCmbAnio(List<Registro> listaCmbAnio) {
        this.listaCmbAnio = listaCmbAnio;
    }

    /**
     * Retorna la lista listaCmbPeriodo
     * 
     * @return listaCmbPeriodo
     */
    public List<Registro> getListaCmbPeriodo() {
        return listaCmbPeriodo;
    }

    /**
     * Asigna la lista listaCmbPeriodo
     * 
     * @param listaCmbPeriodo
     * Variable a asignar en listaCmbPeriodo
     */
    public void setListaCmbPeriodo(List<Registro> listaCmbPeriodo) {
        this.listaCmbPeriodo = listaCmbPeriodo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCmbCodigoInicial
     * 
     * @return listaCmbCodigoInicial
     */
    public RegistroDataModelImpl getListaCmbCodigoInicial() {
        return listaCmbCodigoInicial;
    }

    /**
     * Asigna la lista listaCmbCodigoInicial
     * 
     * @param listaCmbCodigoInicial
     * Variable a asignar en listaCmbCodigoInicial
     */
    public void setListaCmbCodigoInicial(
        RegistroDataModelImpl listaCmbCodigoInicial) {
        this.listaCmbCodigoInicial = listaCmbCodigoInicial;
    }

    /**
     * Retorna la lista listaCmbCodigoFinal
     * 
     * @return listaCmbCodigoFinal
     */
    public RegistroDataModelImpl getListaCmbCodigoFinal() {
        return listaCmbCodigoFinal;
    }

    /**
     * Asigna la lista listaCmbCodigoFinal
     * 
     * @param listaCmbCodigoFinal
     * Variable a asignar en listaCmbCodigoFinal
     */
    public void setListaCmbCodigoFinal(
        RegistroDataModelImpl listaCmbCodigoFinal) {
        this.listaCmbCodigoFinal = listaCmbCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
