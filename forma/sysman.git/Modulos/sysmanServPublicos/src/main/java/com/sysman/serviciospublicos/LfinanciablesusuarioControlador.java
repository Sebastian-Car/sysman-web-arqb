/*-
 * LfinanciablesusuarioControlador.java
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LfinanciablesusuarioControladorEnum;
import com.sysman.serviciospublicos.enums.LfinanciablesusuarioControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario Lfinanciablesusuario
 *
 * @version 1.0, 24/10/2016
 * @author cperez
 * 
 * @version 2, 06/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */

@ManagedBean
@ViewScoped
public class LfinanciablesusuarioControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consNombre;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el codigo Inicial de la consulta
     */
    private String codigoInicial;
    /**
     * Obtiene el codigol Final de la consulta
     */
    private String codigoFinal;
    /**
     * Obtiene el ciclo de la consulta
     */
    private String ciclo;
    /**
     * Obtiene el concepto Inicial de la consulta
     */
    private String conceptoInicial;
    /**
     * Obtiene el concepto Final de la consulta
     */
    private String conceptoFinal;
    /**
     * Obtiene el año Inicial de la consulta
     */
    private String anoInicial;
    /**
     * Obtiene el periodo Inicial de la consulta
     */
    private String periodoInicial;
    /**
     * Obtiene el ano final de la consulta
     */
    private String anoFinal;
    /**
     * Obtiene el periodo final de la consulta
     */
    private String periodoFinal;
    /**
     * Obtiene el nombre de la persona del codigo Inicial de la
     * consulta
     */
    private String nombreCodigoInicial;
    /**
     * Obtiene el nombre de la persona del codigo final de la consulta
     */
    private String nombreCodigoFinal;

    /**
     * Nombre codigo Ruta
     */
    private String codigoRu;
    /**
     * Nombre codigo
     */
    private String codigoConstante;
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
     * Necesario para obtener mandar la lista del año Inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Necesario para obtener mandar la lista del periodo Inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Necesario para obtener y mandar la lista del año final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Necesario para obtener y mandar la lista del Periodo Final
     */
    private List<Registro> listaPeriodoFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Necesario para obtener y mandar la lista del Codigo Inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Necesario para obtener y mandar la lista del Codigo Final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * Necesario para obtener y mandar la lista del Ciclo
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Necesario para obtener y mandar la lista del Concepto Inicial
     */
    private RegistroDataModelImpl listacmbConceptoInicial;
    /**
     * Necesario para obtener y mandar la lista del Concepto Final
     */
    private RegistroDataModelImpl listaCmbConceptoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LfinanciablesusuarioControlador
     */
    public LfinanciablesusuarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoRu = "CODIGORUTA";
        codigoConstante = "CODIGO";
        consNombre="NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.LFINANCIABLESUSUARIO_CONTROLADOR.getCodigo();
            validarPermisos();    
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaAnoFinal();
        cargarListaPeriodoInicial();

        cargarListaPeriodoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();

        cargarListacmbConceptoInicial();
        cargarListaCmbConceptoFinal();
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
     * Carga la lista listaAnoInicial
     */
    public void cargarListaAnoInicial() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesusuarioControladorUrlEnum.URL6878
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
     */
    public void cargarListaPeriodoInicial() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anoInicial);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesusuarioControladorUrlEnum.URL7483
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
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anoInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesusuarioControladorUrlEnum.URL8144
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
     */
    public void cargarListaPeriodoFinal() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.MES.getName(),periodoInicial);
        param.put(GeneralParameterEnum.ANO.getName(),anoInicial);

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesusuarioControladorUrlEnum.URL8740
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
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LfinanciablesusuarioControladorUrlEnum.URL9398.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoRu);
    }

    /**
     *
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LfinanciablesusuarioControladorUrlEnum.URL10356.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
        param.put(LfinanciablesusuarioControladorEnum.PARAM0.getValue(),codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoRu);
    }

    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LfinanciablesusuarioControladorUrlEnum.URL11251.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NUMERO");
    }

    /**
     *
     * Carga la lista listacmbConceptoInicial
     */
    public void cargarListacmbConceptoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LfinanciablesusuarioControladorUrlEnum.URL12268.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listacmbConceptoInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoConstante);
    }

    /**
     *
     * Carga la lista listaCmbConceptoFinal
     */
    public void cargarListaCmbConceptoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LfinanciablesusuarioControladorUrlEnum.URL12924.getValue());  
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(LfinanciablesusuarioControladorEnum.PARAM0.getValue(),conceptoInicial);

        listaCmbConceptoFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, codigoConstante);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF, "001160FinanciablesCOS");


        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL, "001160FinanciablesCOS");

        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {


        try {
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            String periodoInicialCompleto;
            String periodoFinalCompleto;
            periodoInicialCompleto = anoInicial + periodoInicial;
            periodoFinalCompleto = anoFinal + periodoFinal;
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("compania", compania);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);
            reemplazar.put("periodoInicialCompleto", periodoInicialCompleto);
            reemplazar.put("periodoFinalCompleto", periodoFinalCompleto);
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE")+" "+ex.getMessage()+" "+reporte);
            Logger.getLogger(LfinanciablesusuarioControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        } 
        catch ( JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
                                            + ex.getMessage());
            Logger.getLogger(LfinanciablesusuarioControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {      
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }      

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     * 
     * 
     */
    public void cambiarAnoInicial() {
        //<CODIGO_DESARROLLADO>
        cargarListaAnoFinal();
        anoFinal="";
        cargarListaPeriodoInicial();
        periodoInicial="";
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     * 
     * 
     */
    public void cambiarAnoFinal() {
        //<CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        periodoFinal="";
        //</CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     * 
     * 
     */
    public void cambiarPeriodoInicial() {
        //<CODIGO_DESARROLLADO>
        periodoFinal="";
        cargarListaPeriodoFinal();
        //</CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial objeto que encapsula la accion proveniente
     * de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRu)
                        .toString();
        nombreCodigoInicial = registroAux.getCampos().get(consNombre).toString();
        codigoFinal="";
        nombreCodigoFinal="";
        cargarListaCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal objeto que encapsula la accion proveniente de
     * la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRu).toString();
        nombreCodigoFinal = registroAux.getCampos().get(consNombre).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOINICIAL"), "")
                        .toString();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOFINAL"), "")
                        .toString();
        if ("0".equals(codigoInicial) || "".equals(codigoInicial)) {
            nombreCodigoInicial = "";
            nombreCodigoFinal = "";
        }
        else {
            cargarListaCodigoInicial();
            cargarListaCodigoFinal();

            try {
                Map<String,Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
                param.put(GeneralParameterEnum.CICLO.getName(),ciclo);
                param.put(GeneralParameterEnum.CODIGO.getName(),codigoInicial);

                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                LfinanciablesusuarioControladorUrlEnum.URL6880
                                                                .getValue())
                                                .getUrl(), param));


                nombreCodigoInicial = reg != null
                                ? (String) reg.getCampos().get(consNombre) : "";

                                param.put(GeneralParameterEnum.CODIGO.getName(),codigoFinal);


                                reg = RegistroConverter.toRegistro(
                                                requestManager.get(UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                LfinanciablesusuarioControladorUrlEnum.URL6880
                                                                                .getValue())
                                                                .getUrl(), param));
                                nombreCodigoFinal = reg != null
                                                ? (String) reg.getCampos().get(consNombre) : "";
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        anoInicial=anoFinal=periodoInicial=periodoFinal=conceptoInicial=conceptoFinal="";
        
        
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbConceptoInicial objeto que encapsula la accion
     * proveniente de la vista
     */
    public void seleccionarFilacmbConceptoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInicial = registroAux.getCampos().get(codigoConstante)
                        .toString();
        cargarListaCmbConceptoFinal();
        conceptoFinal="";
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbConceptoFinal objeto que encapsula la accion
     * proveniente de la vista
     */
    public void seleccionarFilaCmbConceptoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFinal = registroAux.getCampos().get(codigoConstante).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable conceptoInicial
     *
     * @return conceptoInicial
     */
    public String getConceptoInicial() {
        return conceptoInicial;
    }

    /**
     * Asigna la variable conceptoInicial
     *
     * @param conceptoInicial
     * Variable a asignar en conceptoInicial
     */
    public void setConceptoInicial(String conceptoInicial) {
        this.conceptoInicial = conceptoInicial;
    }

    /**
     * Retorna la variable conceptoFinal
     *
     * @return conceptoFinal
     */
    public String getConceptoFinal() {
        return conceptoFinal;
    }

    /**
     * Asigna la variable conceptoFinal
     *
     * @param conceptoFinal
     * Variable a asignar en conceptoFinal
     */
    public void setConceptoFinal(String conceptoFinal) {
        this.conceptoFinal = conceptoFinal;
    }

    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
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
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
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
     * Retorna la variable nombreCodigoInicial
     *
     * @return nombreCodigoInicial
     */
    public String getNombreCodigoInicial() {
        return nombreCodigoInicial;
    }

    /**
     * Asigna la variable nombreCodigoInicial
     *
     * @param nombreCodigoInicial
     * Variable a asignar en nombreCodigoInicial
     */
    public void setNombreCodigoInicial(String nombreCodigoInicial) {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    /**
     * Retorna la variable nombreCodigoFinal
     *
     * @return nombreCodigoFinal
     */
    public String getNombreCodigoFinal() {
        return nombreCodigoFinal;
    }

    /**
     * Asigna la variable nombreCodigoFinal
     *
     * @param nombreCodigoFinal
     * Variable a asignar en nombreCodigoFinal
     */
    public void setNombreCodigoFinal(String nombreCodigoFinal) {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }


    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }



    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }


    public RegistroDataModelImpl getListacmbConceptoInicial() {
        return listacmbConceptoInicial;
    }

    public void setListacmbConceptoInicial(
        RegistroDataModelImpl listacmbConceptoInicial) {
        this.listacmbConceptoInicial = listacmbConceptoInicial;
    }

    public RegistroDataModelImpl getListaCmbConceptoFinal() {
        return listaCmbConceptoFinal;
    }

    public void setListaCmbConceptoFinal(
        RegistroDataModelImpl listaCmbConceptoFinal) {
        this.listaCmbConceptoFinal = listaCmbConceptoFinal;
    }

}
