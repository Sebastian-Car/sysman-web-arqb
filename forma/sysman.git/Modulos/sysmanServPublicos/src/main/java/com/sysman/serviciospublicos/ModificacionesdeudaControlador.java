/*-
 * ModificacionesdeudaControlador.java
 *
 * 1.0
 *
 * 25/10/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ModificacionesdeudaControladorEnum;
import com.sysman.serviciospublicos.enums.ModificacionesdeudaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del informe Frmmofificaciones, en el cual se genera los
 * informes para deudas de modificaciones
 *
 * @version 1.0, 25/10/2016
 * @author NGOMEZ
 * 
 * @version 2.0, 09/06/2017, <strong>pespitia</strong>:<br>
 * Refactoring.<br>
 * Manejo de EJBs.<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class ModificacionesdeudaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que representa el tipo del informe que se va a generar
     */
    private String tipoReporte;
    /**
     * Variable que representa el ciclo en que se va a generar el
     * informe. Si toma T equivale a Todos.
     */
    private String ciclo;
    /**
     * Variable que representa la anio inicial para generar el informe
     */
    private String anioInicial;
    /**
     * Variable que representa la periodo inicial para generar el
     * informe
     */
    private String periodoInicial;
    /**
     * Variable que representa la anio final para generar el informe
     */
    private String anioFinal;
    /**
     * Variable que representa la periodo final para generar el
     * informe
     */
    private String periodoFinal;
    /**
     * Variable que representa el codigo inicial para generar el
     * informe
     */
    private String codigoInicial;
    /**
     * Variable que representa la codigo final para generar el informe
     */
    private String codigoFinal;
    /**
     * Variable que representa la fecha inicial para generar el
     * informe
     */
    private Date fechaInicial;
    /**
     * Variable que representa la fecha final para generar el informe
     */
    private Date fechaFinal;
    /**
     * Variable que representa la valor inicial para generar el
     * informe
     */
    private String valorInicial;
    /**
     * Variable que representa la valor final para generar el informe
     */
    private String valorFinal;
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
     * Lista del combo ciclo
     */
    private List<Registro> listaciclo;
    /**
     * Lista del combo anio inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista del combo periodo inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista del combo anio final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista del combo periodo final
     */
    private List<Registro> listaPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista del combo codigo inicial
     */
    private RegistroDataModelImpl listacodigoInicial;
    /**
     * Lista del combo codigo final
     */
    private RegistroDataModelImpl listacodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Constante que represnta el nombre del campo CODIGORUTA
     */
    private final String codigoRutaCons;

    /**
     * Crea una nueva instancia de ModificacionesdeudaControlador
     */
    public ModificacionesdeudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoRutaCons = "CODIGORUTA";
        try {
            // 1152
            numFormulario = GeneralCodigoFormaEnum.MODIFICACIONESDEUDA_CONTROLADOR
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
        ciclo = "T";
        fechaInicial = fechaFinal = new Date();
        valorInicial = "0";
        valorFinal = "9999999999";

        cargarListaciclo();
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        cargarListacodigoInicial();
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
     * Carga la lista listaciclo
     *
     */
    public void cargarListaciclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaciclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesdeudaControladorUrlEnum.URL6973
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
                                                            ModificacionesdeudaControladorUrlEnum.URL7583
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
                                                            ModificacionesdeudaControladorUrlEnum.URL8600
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
                                                            ModificacionesdeudaControladorUrlEnum.URL8036
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioFinal);
        param.put(ModificacionesdeudaControladorEnum.MESINI.getValue(),
                        anioInicial.equals(anioFinal) ? periodoInicial : "");

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ModificacionesdeudaControladorUrlEnum.URL0001
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
     * Carga la lista listacodigoInicial
     *
     */
    public void cargarListacodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesdeudaControladorUrlEnum.URL9576
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listacodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    /**
     *
     * Carga la lista listacodigoFinal
     *
     */
    public void cargarListacodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificacionesdeudaControladorUrlEnum.URL10444
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        param.put(ModificacionesdeudaControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listacodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el valor del control PeriodoInicial
     */
    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = "";

        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ciclo
     *
     *
     */
    public void cambiarciclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = codigoFinal = null;

        if (ciclo == null) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3214"));
            ciclo = "T";
        }

        cargarListacodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     *
     *
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoInicial = periodoFinal = "";

        anioFinal = anioInicial;

        cargarListaAnoFinal();
        cargarListaPeriodoInicial();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     *
     *
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = "";

        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoRutaCons), "")
                        .toString();

        codigoFinal = null;
        cargarListacodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoRutaCons), "")
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoReporte
     *
     * @return tipoReporte
     */
    public String getTipoReporte() {
        return tipoReporte;
    }

    /**
     * Asigna la variable tipoReporte
     *
     * @param tipoReporte
     * Variable a asignar en tipoReporte
     */
    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
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
    public String getcodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setcodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
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
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable valorInicial
     *
     * @return valorInicial
     */
    public String getValorInicial() {
        return valorInicial;
    }

    /**
     * Asigna la variable valorInicial
     *
     * @param valorInicial
     * Variable a asignar en valorInicial
     */
    public void setValorInicial(String valorInicial) {
        this.valorInicial = valorInicial;
    }

    /**
     * Retorna la variable valorFinal
     *
     * @return valorFinal
     */
    public String getValorFinal() {
        return valorFinal;
    }

    /**
     * Asigna la variable valorFinal
     *
     * @param valorFinal
     * Variable a asignar en valorFinal
     */
    public void setValorFinal(String valorFinal) {
        this.valorFinal = valorFinal;
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
     * Retorna la lista listaciclo
     *
     * @return listaciclo
     */
    public List<Registro> getListaciclo() {
        return listaciclo;
    }

    /**
     * Asigna la lista listaciclo
     *
     * @param listaciclo
     * Variable a asignar en listaciclo
     */
    public void setListaciclo(List<Registro> listaciclo) {
        this.listaciclo = listaciclo;
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
    /**
     * Retorna la lista listacodigoInicial
     *
     * @return listacodigoInicial
     */
    public RegistroDataModelImpl getListacodigoInicial() {
        return listacodigoInicial;
    }

    /**
     * Asigna la lista listacodigoInicial
     *
     * @param listacodigoInicial
     * Variable a asignar en listacodigoInicial
     */
    public void setListacodigoInicial(
        RegistroDataModelImpl listacodigoInicial) {
        this.listacodigoInicial = listacodigoInicial;
    }

    /**
     * Retorna la lista listacodigoFinal
     *
     * @return listacodigoFinal
     */
    public RegistroDataModelImpl getListacodigoFinal() {
        return listacodigoFinal;
    }

    /**
     * Asigna la lista listacodigoFinal
     *
     * @param listacodigoFinal
     * Variable a asignar en listacodigoFinal
     */
    public void setListacodigoFinal(RegistroDataModelImpl listacodigoFinal) {
        this.listacodigoFinal = listacodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Proceso en que se genera el reporte
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInforme(ReportesBean.FORMATOS formato) {
        try {
            String reporte;

            switch (tipoReporte) {
            case "1":
                reporte = "001168ModifDeudaCasosEspConceptos";
                break;
            case "2":
                reporte = "001170ModifDeudaCasosEspUsuarios";
                break;
            case "3":
                reporte = "001169ModifDeudaCasosEspUsuarioConceptos";
                break;
            default:
                reporte = "001167ModifDeudaCasosEspComparar";
                break;
            }

            String fechaInicialAux = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFinalAux = SysmanFunciones
                            .convertirAFechaCadena(fechaFinal);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", "T".equals(ciclo) ? ""
                : " AND SP_MODIFICACIONESDEUDA.CICLO=" + ciclo + " ");
            reemplazar.put("fechaInicial", fechaInicialAux);
            reemplazar.put("fechaFinal", fechaFinalAux);
            reemplazar.put("valorInicial", valorInicial);
            reemplazar.put("valorFinal", valorFinal);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("periodoInicial", anioInicial + periodoInicial);
            reemplazar.put("periodoFinal", anioFinal + periodoFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_CICLO",
                            "T".equals(ciclo) ? "Todos" : ciclo);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_TXTRANGOINICIAL",
                            fechaInicialAux);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_TXTRANGOFINAL",
                            fechaFinalAux);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_ANOINICIAL",
                            anioInicial);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_PERIODOINICIAL",
                            periodoInicial);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_ANOFINAL", anioFinal);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_PERIODOFINAL",
                            periodoFinal);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_TXTVALORINICIAL",
                            formatearValor(valorInicial));
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_TXTVALORFINAL",
                            formatearValor(valorFinal));
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_CODIGOINICIAL",
                            codigoInicial);
            parametros.put("PR_FORMS_MODIFICACIONESDEUDA_CODIGOFINAL",
                            codigoFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (ParseException | OutOfMemoryError | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String formatearValor(String valor) {
        String rta = new java.text.DecimalFormat("#,##0.00").format(
                        Double.parseDouble(valor));
        rta = rta.replace(",", "*").replace(".", ",").replace("*", ".");
        return rta;
    }
}
