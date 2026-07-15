/*-
 * auxContablePorAuxiliaresControlador.java
 *
 * 1.0
 * 
 * 16/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisAuxiliaresControladorEnum;
import com.sysman.contabilidad.enums.LisauxiliarsaldosControladorEnum;
import com.sysman.contabilidad.enums.auxContablePorAuxiliaresControladorUrlEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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
 *
 * @version 1.0, 16/11/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class auxContablePorAuxiliaresControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private int formatoInformes;
    private String tipoComprobanteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoComprobanteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String fuenteRecuersoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String fuenteRecuersoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private boolean formatoEspecial;
    private boolean indReferenciado;
    private boolean formatoEspecialExcel = false; //JM 04/12/2024
    private String anio;
    private String reporte;
    private Date fechaInicial;
    private Date fechaFinal;
    
    /**
     * Variable para almacenar el valor del check Con saldo cero
     */
    private boolean saldoCero;
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

    private RegistroDataModelImpl listaTipoComprobanteInicial;
    private RegistroDataModelImpl listaTipoComprobanteFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaCentroCostoInicial;
    private RegistroDataModelImpl listaCentroCostoFinal;
    private RegistroDataModelImpl listaFuenteRecuersoInicial;
    private RegistroDataModelImpl listaFuenteRecuersoFinal;
    private RegistroDataModelImpl listaReferenciaInicial;
    private RegistroDataModelImpl listaReferenciaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de auxContablePorAuxiliaresControlador
     */
    public auxContablePorAuxiliaresControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        fechaInicial = new Date();
        fechaFinal = new Date();
        formatoInformes = 1;

        try {
            // 1985
            numFormulario = GeneralCodigoFormaEnum.AUX_CONTABLE_POR_AUXILIARES_CONTROLADOR
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
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListaTipoComprobanteInicial();
        cargarListaCuentaInicial();
        cargarListaTerceroInicial();
        cargarListaCentroCostoInicial();
        cargarListaFuenteRecuersoInicial();
        cargarListaReferenciaInicial();
        abrirFormulario();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // Metodo heredado
        formatoEspecial = SessionUtil.getCompaniaIngreso().getNit()
                        .equals("800091594-4");
        indReferenciado = true;

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoComprobanteInicial
     *
     */
    public void cargarListaTipoComprobanteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL3488
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoComprobanteInicial = new RegistroDataModelImpl(
                        urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                        param, true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoComprobanteFinal
     *
     */
    public void cargarListaTipoComprobanteFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL15003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisAuxiliaresControladorEnum.TIPOINICIAL.getValue(),
                        tipoComprobanteInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTipoComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL5222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL6436
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL6904
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put("TERCEROINICIAL", terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    public void cargarListaCentroCostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL7434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCentroCostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL7965
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(LisauxiliarsaldosControladorEnum.CENTROINICIAL.getValue(),
                        centroCostoInicial);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL5971
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisAuxiliaresControladorEnum.CODIGOINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteRecuersoInicial
     *
     */
    public void cargarListaFuenteRecuersoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL34043
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put("ANIO", anio);

        listaFuenteRecuersoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteRecuersoFinal
     *
     */
    public void cargarListaFuenteRecuersoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL34045
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", anio);
        param.put(LisAuxiliaresControladorEnum.CODIGOINICIAL.getValue(),
                        fuenteRecuersoInicial);
        listaFuenteRecuersoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL13028
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     */
    public void cargarListaReferenciaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        auxContablePorAuxiliaresControladorUrlEnum.URL13030
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("REFERENCIAINICIAL", referenciaInicial);
        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton generaPDF en la vista
     *
     *
     */
    public void oprimirgeneraPDF() {

        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton generaExcel en la vista
     *
     *
     */
    public void oprimirgeneraExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        cargarListaFuenteRecuersoInicial();

    }
    
    public void cambiarFormatoInforme() {
    	if (formatoInformes == 5) {
            indReferenciado = false;
        }
    	else {
    		indReferenciado = true;
    	}

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobanteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void consultarInform() {
        if (formatoInformes == 1) {
            reporte = "001958AuxiliaresporAux";
        }
        else if (formatoInformes == 2) {
            reporte = "001960AuxiliaresporAuxUPC";

        }
        else if (formatoInformes == 3) {
            reporte = "001959AuxiliaresporAuxCO";

        }
        else if (formatoInformes == 4) {

            reporte = "002005AuxiliaresporAuxCAQ";
        }
        else if (formatoInformes == 5) {
            if (formatoEspecialExcel && saldoCero) {
                reporte = "800717AuxiliarSaldoCeroRefEspecial_SINCHI";
            }
            else if (formatoEspecialExcel) {
                reporte = "002674AUXILIARESPORAUXREFERENCIADOESPECIAL";
            }
            else {
                reporte = "002629AUXILIARESPORAUXREFERENCIADO";
            }
        }
    }


    

    private void generarReporte(FORMATOS formato) {
        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();
        if (fechaInicial.before(fechaFinal)
            || (fechaInicial.equals(fechaFinal))) {

            consultarInform();

            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("mesAnterior", SysmanFunciones
                            .getParteFecha(fechaInicial, Calendar.MONTH));
            reemplazar.put("comprobanteInicial", tipoComprobanteInicial);
            reemplazar.put("comprobanteFinal", tipoComprobanteFinal);
            reemplazar.put("centroCostoInicial", centroCostoInicial);
            reemplazar.put("centroCostoFinal", centroCostoFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);
            parametros.put("PR_TITULO_CUENTAS", "ENTRE CUENTAS " + cuentaInicial
                + " Y " + cuentaFinal);
            try {
                parametros.put("PR_TITULO_FECHAS",
                                "AUXILIAR CONTABLE ENTRE FECHAS "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaInicial)
                                    + " Y "
                                    + SysmanFunciones.convertirAFechaCadena(
                                                    fechaFinal));
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            if(formatoInformes == 5)
            {
            	reemplazar.put("anoTrabajo", SysmanFunciones
                        .getParteFecha(fechaInicial, Calendar.YEAR));
            	Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo), reemplazar,
                        parametros);
            	
            }
            else {
	            Reporteador.resuelveConsulta("001957AuxiliaresporAuxiliar",
	                            Integer.parseInt(modulo), reemplazar,
	                            parametros);
            }

            try {
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4256"));
        }
    }

    public void seleccionarFilaTipoComprobanteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComprobanteInicial = registroAux.getCampos().get("CODIGO")
                        .toString();
        cargarListaTipoComprobanteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobanteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobanteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComprobanteFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("CODIGO").toString();
        cargarListaCuentaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecuersoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
    }

    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaCentroCostoFinal();
    }

    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    public void seleccionarFilaFuenteRecuersoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteRecuersoInicial = registroAux.getCampos().get("CODIGO")
                        .toString();
        cargarListaFuenteRecuersoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecuersoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecuersoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteRecuersoFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos().get("CODIGO").toString();
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = registroAux.getCampos().get("CODIGO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoComprobanteInicial
     * 
     * @return tipoComprobanteInicial
     */
    public String getTipoComprobanteInicial() {
        return tipoComprobanteInicial;
    }

    /**
     * Asigna la variable tipoComprobanteInicial
     * 
     * @param tipoComprobanteInicial
     * Variable a asignar en tipoComprobanteInicial
     */
    public void setTipoComprobanteInicial(String tipoComprobanteInicial) {
        this.tipoComprobanteInicial = tipoComprobanteInicial;
    }

    /**
     * Retorna la variable tipoComprobanteFinal
     * 
     * @return tipoComprobanteFinal
     */
    public String getTipoComprobanteFinal() {
        return tipoComprobanteFinal;
    }

    /**
     * Asigna la variable tipoComprobanteFinal
     * 
     * @param tipoComprobanteFinal
     * Variable a asignar en tipoComprobanteFinal
     */
    public void setTipoComprobanteFinal(String tipoComprobanteFinal) {
        this.tipoComprobanteFinal = tipoComprobanteFinal;
    }

    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial() {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal() {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable fuenteRecuersoInicial
     * 
     * @return fuenteRecuersoInicial
     */
    public String getFuenteRecuersoInicial() {
        return fuenteRecuersoInicial;
    }

    /**
     * Asigna la variable fuenteRecuersoInicial
     * 
     * @param fuenteRecuersoInicial
     * Variable a asignar en fuenteRecuersoInicial
     */
    public void setFuenteRecuersoInicial(String fuenteRecuersoInicial) {
        this.fuenteRecuersoInicial = fuenteRecuersoInicial;
    }

    /**
     * Retorna la variable fuenteRecuersoFinal
     * 
     * @return fuenteRecuersoFinal
     */
    public String getFuenteRecuersoFinal() {
        return fuenteRecuersoFinal;
    }

    /**
     * Asigna la variable fuenteRecuersoFinal
     * 
     * @param fuenteRecuersoFinal
     * Variable a asignar en fuenteRecuersoFinal
     */
    public void setFuenteRecuersoFinal(String fuenteRecuersoFinal) {
        this.fuenteRecuersoFinal = fuenteRecuersoFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     * 
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     * 
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     * 
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     * 
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
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
     * Retorna la lista listaTipoComprobanteInicial
     * 
     * @return listaTipoComprobanteInicial
     */

    // </SET_GET_LISTAS_COMBO_GRANDE>
    public int getFormatoInformes() {
        return formatoInformes;
    }

    public void setFormatoInformes(int formatoInformes) {
        this.formatoInformes = formatoInformes;
    }

    public RegistroDataModelImpl getListaTipoComprobanteInicial() {
        return listaTipoComprobanteInicial;
    }

    public void setListaTipoComprobanteInicial(
        RegistroDataModelImpl listaTipoComprobanteInicial) {
        this.listaTipoComprobanteInicial = listaTipoComprobanteInicial;
    }

    public RegistroDataModelImpl getListaTipoComprobanteFinal() {
        return listaTipoComprobanteFinal;
    }

    public void setListaTipoComprobanteFinal(
        RegistroDataModelImpl listaTipoComprobanteFinal) {
        this.listaTipoComprobanteFinal = listaTipoComprobanteFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaFuenteRecuersoInicial() {
        return listaFuenteRecuersoInicial;
    }

    public void setListaFuenteRecuersoInicial(
        RegistroDataModelImpl listaFuenteRecuersoInicial) {
        this.listaFuenteRecuersoInicial = listaFuenteRecuersoInicial;
    }

    public RegistroDataModelImpl getListaFuenteRecuersoFinal() {
        return listaFuenteRecuersoFinal;
    }

    public void setListaFuenteRecuersoFinal(
        RegistroDataModelImpl listaFuenteRecuersoFinal) {
        this.listaFuenteRecuersoFinal = listaFuenteRecuersoFinal;
    }

    public RegistroDataModelImpl getListaReferenciaInicial() {
        return listaReferenciaInicial;
    }

    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial) {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
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
     * @return the formatoEspecial
     */
    public boolean isFormatoEspecial() {
        return formatoEspecial;
    }

    /**
     * @param formatoEspecial
     * the formatoEspecial to set
     */
    public void setFormatoEspecial(boolean formatoEspecial) {
        this.formatoEspecial = formatoEspecial;
    }

	public boolean isIndReferenciado() {
		return indReferenciado;
	}

	public void setIndReferenciado(boolean indReferenciado) {
		this.indReferenciado = indReferenciado;
	}
	
    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }
	/**
	 * @return the saldoCero
	 */
	public boolean isSaldoCero() {
		return saldoCero;
	}

	/**
	 * @param saldoCero the saldoCero to set
	 */
	public void setSaldoCero(boolean saldoCero) {
		this.saldoCero = saldoCero;
	}
    
    

}
