/*-
 * LfinanciablesdeudaControlador.java
 *
 * 1.0
 * 
 * 01/11/2011
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LfinanciablesdeudaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del LfinanciablesdeudaControlador
 *
 * @version 1.0, 01/11/2011
 * @author cperez
 * @modified jguerrero
 * @version 2. 06/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class LfinanciablesdeudaControlador extends BeanBaseModal {
    private String filtroPor;
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
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
     * Obtiene el a�o Inicial de la consulta
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
    private String nombreCodigoIncial;
    /**
     * Obtiene el nombre de la persona del codigo final de la consulta
     */
    private String nombreCodigoFinal;
    /**
     * Variable para generar mensaje de error
     */
    private String mensajeError;
    /**
     * Generada automaticamente
     */
    private String nombre;

    /**
     * Nombre codigo Ruta
     */
    private String codigoRu;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Necesario para obtener mandar la lista del a�o Inicial
     */
    private List<Registro> listaAnoInicial;
    /**
     * Necesario para obtener mandar la lista del periodo Inicial
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Necesario para obtener y mandar la lista del a�o final
     */
    private List<Registro> listaAnoFinal;
    /**
     * Necesario para obtener y mandar la lista del Periodo Final
     */
    private List<Registro> listaPeriodoFinal;
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
    private static final String CODIGOINICIALCON = "CODIGOINICIAL";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LfinanciablesdeudaControlador
     */
    public LfinanciablesdeudaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LFINANCIABLESDEUDA_CONTROLADOR
                            .getCodigo();
            codigoRu = GeneralParameterEnum.CODIGORUTA.getName();
            mensajeError = "MSM_INFORME_NO_EXISTE";
            nombre = "NOMBRE";
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
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
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaCiclo();
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

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesdeudaControladorUrlEnum.URL6835
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

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesdeudaControladorUrlEnum.URL7439
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
     */
    public void cargarListaAnoFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesdeudaControladorUrlEnum.URL8100
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

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("MESINICIAL", periodoInicial);

        try {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LfinanciablesdeudaControladorUrlEnum.URL8695
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

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LfinanciablesdeudaControladorUrlEnum.URL9338
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRu);

        // 213083 COMPANIA CICLO

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LfinanciablesdeudaControladorUrlEnum.URL10337
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(CODIGOINICIALCON, codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRu);

        // 213085 CODIGOINICIAL COMPANIA CICLO
    }

    /**
     * 
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LfinanciablesdeudaControladorUrlEnum.URL11448
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try {

            String valorParametro;
            valorParametro = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO DE FINANCIABLES DE DEUDA",
                            SessionUtil.getModulo(),
                            new Date(), true);
            if ("FINANCIABLESDEUDA".equals(valorParametro)) {
                genInforme(FORMATOS.EXCEL, "001202FinanciablesDeuda");
            }
            if ("FINANCIABLESDEUDAYOP".equals(valorParametro)) {
                genInforme(FORMATOS.EXCEL, "001203FinanciablesDeudaYop");

            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(mensajeError));
            }
        }
        catch (SystemException e) {
            Logger.getLogger(LfinanciablesdeudaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            String valorParametro;

            valorParametro = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO DE FINANCIABLES DE DEUDA",
                            SessionUtil.getModulo(),
                            new Date(), true);

            if ("FINANCIABLESDEUDA".equals(valorParametro)) {
                genInforme(FORMATOS.PDF, "001202FinanciablesDeuda");
            }
            if ("FINANCIABLESDEUDAYOP".equals(valorParametro)) {
                genInforme(FORMATOS.PDF, "001203FinanciablesDeudaYop");

            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(mensajeError));
            }
        }
        catch (SystemException e) {
            Logger.getLogger(LfinanciablesdeudaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoInicial = null;
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     * 
     * 
     */
    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CodigoInicial en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */

    public void cambiarCodigoInicialC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // s
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoRu).toString();
        nombreCodigoIncial = retorno(registroAux, nombre);

        codigoFinal = null;
        nombreCodigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoRu).toString();
        nombreCodigoFinal = retorno(registroAux, nombre);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {

        try {
            Registro registroAux = (Registro) event.getObject();
            ciclo = registroAux.getCampos().get("NUMERO").toString();
            codigoInicial = SysmanFunciones
                            .nvl(registroAux.getCampos().get(CODIGOINICIALCON),
                                            "")
                            .toString();
            codigoFinal = SysmanFunciones
                            .nvl(registroAux.getCampos().get("CODIGOFINAL"), "")
                            .toString();

            if ("0".equals(codigoInicial) || "".equals(codigoInicial)) {
                nombreCodigoIncial = "";
                nombreCodigoFinal = "";
            }
            else {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                param.put(CODIGOINICIALCON, codigoInicial);

                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                LfinanciablesdeudaControladorUrlEnum.URL8101
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                nombreCodigoIncial = reg != null
                    ? reg.getCampos().get("NOMBRE").toString() : "";

                Map<String, Object> paramCodFinal = new TreeMap<>();
                paramCodFinal.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramCodFinal.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                paramCodFinal.put(CODIGOINICIALCON, codigoFinal);

                Registro regCodFinal = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                LfinanciablesdeudaControladorUrlEnum.URL8101
                                                                                                .getValue())
                                                                .getUrl(),
                                                paramCodFinal));

                nombreCodigoFinal = regCodFinal != null
                    ? regCodFinal.getCampos().get(nombre).toString() : "";
            }
            cargarListaCodigoInicial();
            cargarListaCodigoFinal();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /*
     * Metodo para generar el informe ya sea de pdf o excel
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        try {
            archivoDescarga = null;
            HashMap<String, Object> reemplazar = new HashMap<>();
            String condicionCicloQryUltimoPago;
            String condicionCicloQrFinal;
            String condicionCiclo;
            String condicionFiltroPor;
            if ("T".equals(ciclo)) {
                condicionCicloQryUltimoPago = "";
                condicionCicloQrFinal = "";
                condicionCiclo = "";

            }
            else {
                condicionCicloQryUltimoPago = "AND SP_HISTORIA.CICLO ='" + ciclo
                    + "'";
                condicionCicloQrFinal = "AND SP_FINANCIABLES.CICLO = '" + ciclo
                    + "'";
                condicionCiclo = "AND SP_FINANCIABLESDEDEUDA.CICLO = '" + ciclo
                    + "'";
            }
            if ("3".equals(filtroPor)) {
                condicionFiltroPor = "";
            }
            else if ("2".equals(filtroPor)) {
                condicionFiltroPor = "AND TO_NUMBER(NVL(SP_FINANCIABLESDEDEUDA.INDANULADO,0)) <> 0";
            }
            else {
                condicionFiltroPor = "AND TO_NUMBER(NVL(SP_FINANCIABLESDEDEUDA.INDANULADO,0)) IN (0)";
            }
            String periodoInicialCompleto;
            String periodoFinalCompleto;
            String nombreCompania;
            periodoInicialCompleto = anoInicial + periodoInicial;
            periodoFinalCompleto = anoFinal + periodoFinal;
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("compania", compania);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("periodoInicialCompleto", periodoInicialCompleto);
            reemplazar.put("periodoFinalCompleto", periodoFinalCompleto);
            reemplazar.put("condicionCicloQryUltimoPago",
                            condicionCicloQryUltimoPago);
            reemplazar.put("condicionCicloQrFinal", condicionCicloQrFinal);
            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("condicionFiltroPor", condicionFiltroPor);

            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String retorno(Registro registro, String campo) {

        return SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)
            ? "" : registro.getCampos().get(campo).toString();
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
     * Retorna la variable filtroPor
     * 
     * @return filtroPor
     */
    public String getFiltroPor() {
        return filtroPor;
    }

    /**
     * Asigna la variable filtroPor
     * 
     * @param filtroPor
     * Variable a asignar en filtroPor
     */
    public void setFiltroPor(String filtroPor) {
        this.filtroPor = filtroPor;
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
     * Retorna la variable nombreCodigoIncial
     * 
     * @return nombreCodigoIncial
     */
    public String getNombreCodigoIncial() {
        return nombreCodigoIncial;
    }

    /**
     * Asigna la variable nombreCodigoIncial
     * 
     * @param nombreCodigoIncial
     * Variable a asignar en nombreCodigoIncial
     */
    public void setNombreCodigoIncial(String nombreCodigoIncial) {
        this.nombreCodigoIncial = nombreCodigoIncial;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

}
