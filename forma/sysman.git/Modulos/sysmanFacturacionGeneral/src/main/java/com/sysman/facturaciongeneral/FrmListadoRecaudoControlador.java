/*-
 * FrmListadoRecaudoControlador.java
 *
 * 1.0
 * 
 * 08/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.facturaciongeneral.enums.FrmListadoRecaudoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
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
 * Clase encargada de generar y exportar el reporte que lista los
 * recaudos
 *
 * @version 1.0, 08/11/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped

public class FrmListadoRecaudoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo Banco Inicial en la interfaz grafica de usuario
     */
    private String bancoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo Banco Final en la interfaz grafica de usuario
     */
    private String bancoFinal;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo Concepto Inicial en la interfaz grafica de usuario
     */
    private String conceptoInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del combo Concepto Final en la interfaz grafica de usuario
     */
    private String conceptoFinal;

    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del campo fecha inicial en la interfaz grafica de usuario
     */
    private Date fechaInicial;
    /**
     * Variable encargada de almacenar temporalmente lo seleccionado
     * del campo fecha Final en la interfaz grafica de usuario
     */
    private Date fechaFinal;
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
     * Lista encargada de almacenar termporalmente los datos de
     * respuesta a la llamda a la base de datos a la tabla
     * SF_CONCEPTOS.
     */
    private RegistroDataModelImpl listaConceptoIni;
    /**
     * Lista encargada de almacenar termporalmente los datos de
     * respuesta a la llamda a la base de datos a la tabla
     * SF_CONCEPTOS.
     */
    private RegistroDataModelImpl listaConceptoFin;

    /**
     * Lista encargada de almacenar termporalmente los datos de
     * respuesta a la llamda a la base de datos a la tabla
     * SF_TIPO_COBRO.
     */
    private List<Registro> listaTipooFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar termporalmente los datos de
     * respuesta a la llamda a la base de datos a la vista
     * V_PLAN_CONTABLE.
     */
    private RegistroDataModelImpl listaBancoIni;
    /**
     * Lista encargada de almacenar termporalmente los datos de
     * respuesta a la llamda a la base de datos a la vista
     * V_PLAN_CONTABLE.
     */
    private RegistroDataModelImpl listaBancoFin;
    /**
     * Variable encargada de almacenar temporalemte las variables de
     * session del modulo de facturacion general
     */
    private String ano;
    /**
     * Variable encargada de almacenar temporalemte las variables de
     * session del modulo de facturacion general
     */
    private String tipoCobro;
    /**
     * Variable encargada de almacenar temporalemte las variables de
     * session del modulo de facturacion general
     */
    private String nombreTipoCobro;
    /**
     * Constante encargada de almacenar el String CODIGO
     */
    private final String codigoCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmListadoRecaudoControlador
     */
    public FrmListadoRecaudoControlador() {
        super();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        nombreTipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                        .getValue());

        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMLISTADORECAUDO_CONTROLADOR
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
        cargarListaConceptoIni();
        cargarListaConceptoFin();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoIni();
        cargarListaBancoFin();
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
     * Carga la lista listaConceptoIni
     *
     * Metodo encargado de hacer el llamado a la base de datos por
     * medio de Dss.
     */

    public void cargarListaConceptoIni() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoRecaudoControladorUrlEnum.URL7176
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("TIPOCOBRO", tipoCobro);

        listaConceptoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 663005 ANO,TIPOCOBRO
    }

    /**
     * 
     * Carga la lista listaConceptoFin
     *
     * Metodo encargado de hacer el llamado a la base de datos por
     * medio de Dss.
     */
    public void cargarListaConceptoFin() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoRecaudoControladorUrlEnum.URL7916
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put("TIPOCOBRO", tipoCobro);
        param.put("CODIGOINICIAL", conceptoInicial);

        listaConceptoFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 663007 CODIGOINICIAL
    }

    /**
     * 
     * Carga la lista listaBancoIni
     *
     * Metodo encargado de hacer el llamado a la base de datos por
     * medio de Dss.
     */
    public void cargarListaBancoIni() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoRecaudoControladorUrlEnum.URL8718
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaBancoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 29123 COMPANIA ANO
    }

    /**
     * 
     * Carga la lista listaBancoFin Metodo encargado de hacer el
     * llamado a la base de datos pormedio de Dss.
     */
    public void cargarListaBancoFin() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmListadoRecaudoControladorUrlEnum.URL9802
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put("CODIGOINICIAL", bancoInicial);

        listaBancoFin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 29125 CODIGOINICIAL
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Metodo encargado de hacer el llamado a la base de datos
     * pormedio de Dss.
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        genInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     * Metodo encargado de hacer el llamado a la base de datos
     * pormedio de Dss.
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoIni
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = retornarString(registroAux, codigoCons);
        bancoFinal = null;
        cargarListaBancoFin();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = retornarString(registroAux, codigoCons);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoIni
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoIni(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoInicial = retornarString(registroAux, codigoCons);
        conceptoFinal = null;
        cargarListaConceptoFin();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoFin(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoFinal = retornarString(registroAux, codigoCons);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable bancoInicial
     * 
     * @return bancoInicial
     */
    public String getBancoInicial() {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     * 
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial) {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     * 
     * @return bancoFinal
     */
    public String getBancoFinal() {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     * 
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal) {
        this.bancoFinal = bancoFinal;
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
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */

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
     * Retorna la lista listaConceptoIni
     * 
     * @return listaConceptoIni
     */

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

    public RegistroDataModelImpl getListaConceptoIni() {
        return listaConceptoIni;
    }

    public void setListaConceptoIni(RegistroDataModelImpl listaConceptoIni) {
        this.listaConceptoIni = listaConceptoIni;
    }

    public RegistroDataModelImpl getListaConceptoFin() {
        return listaConceptoFin;
    }

    public void setListaConceptoFin(RegistroDataModelImpl listaConceptoFin) {
        this.listaConceptoFin = listaConceptoFin;
    }

    /**
     * Retorna la lista listaTipooFinal
     * 
     * @return listaTipooFinal
     */
    public List<Registro> getListaTipooFinal() {
        return listaTipooFinal;
    }

    /**
     * Asigna la lista listaTipooFinal
     * 
     * @param listaTipooFinal
     * Variable a asignar en listaTipooFinal
     */
    public void setListaTipooFinal(List<Registro> listaTipooFinal) {
        this.listaTipooFinal = listaTipooFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBancoIni
     * 
     * @return listaBancoIni
     */
    public RegistroDataModelImpl getListaBancoIni() {
        return listaBancoIni;
    }

    /**
     * Asigna la lista listaBancoIni
     * 
     * @param listaBancoIni
     * Variable a asignar en listaBancoIni
     */
    public void setListaBancoIni(RegistroDataModelImpl listaBancoIni) {
        this.listaBancoIni = listaBancoIni;
    }

    /**
     * Retorna la lista listaBancoFin
     * 
     * @return listaBancoFin
     */
    public RegistroDataModelImpl getListaBancoFin() {
        return listaBancoFin;
    }

    /**
     * Asigna la lista listaBancoFin
     * 
     * @param listaBancoFin
     * Variable a asignar en listaBancoFin
     */
    public void setListaBancoFin(RegistroDataModelImpl listaBancoFin) {
        this.listaBancoFin = listaBancoFin;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void genInforme(ReportesBean.FORMATOS formato) {

        try {

            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);
            reemplazar.put("tipocobro", tipoCobro);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBRETIPOCOBRO", nombreTipoCobro);
            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_BANCOINICIAL", bancoInicial);
            parametros.put("PR_BANCOFINAL", bancoFinal);

            Reporteador.resuelveConsulta("001485INFLISFACRECAUDO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001485INFLISFACRECAUDO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
