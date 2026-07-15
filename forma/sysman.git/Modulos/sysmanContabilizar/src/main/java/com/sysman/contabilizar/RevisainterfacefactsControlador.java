/*-
 * RevisainterfacefactsControlador.java
 *
 * 1.0
 * 
 * 09/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilizar.ejb.EjbContabilizarFacGenRemote;
import com.sysman.contabilizar.enums.RevisainterfacefactsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite Revisar Facturacion por a�o ver conceptos
 * sin configuracion e importar a�o anterior.
 *
 * @version 1.0, 09/01/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class RevisainterfacefactsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
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
    /**
     */
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista que almacena codigo de consultar cuentas
     */
    private RegistroDataModelImpl listaconcepto;
    /**
     * Lista que almacena codigo de consultar cuentas desde la grilla
     */
    private RegistroDataModelImpl listaconceptoE;
    /**
     * Lista que almacena los conceptos
     */
    private RegistroDataModelImpl listaTexto63;
    /**
     * Lista que almacena los conceptos de la grilla
     */
    private RegistroDataModelImpl listaTexto63E;
    /**
     * Lista que almacena los a�os
     */
    private List<Registro> listaano;
    /**
     * Lista que almacena los a�os de la grilla
     */
    private List<Registro> listaanoE;

    /**
     * Lista que almacena centro de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista que almacena centro de costo de la grilla
     */
    private RegistroDataModelImpl listaCentroCostoE;
    /**
     * Lista que almacena Debito Base Causacion
     */
    private RegistroDataModelImpl listaDebitoBaseC;
    /**
     * Lista que almacena Debito Base Causacion de la grilla
     */
    private RegistroDataModelImpl listaDebitoBaseCE;
    /**
     * Lista que almacena Credito Base Causacion
     */
    private RegistroDataModelImpl listaCreditoBaseC;
    /**
     * Lista que almacena Credito Base Causacion de la grilla
     */
    private RegistroDataModelImpl listaCreditoBaseCE;
    /**
     * Lista que almacena Debito Iva Causacion
     */
    private RegistroDataModelImpl listaDebitoIvaC;
    /**
     * Lista que almacena Debito Iva Causacion de la grilla
     */
    private RegistroDataModelImpl listaDebitoIvaCE;
    /**
     * Lista que almacena Credito Iva Causacion
     */
    private RegistroDataModelImpl listaCreditoIvaC;
    /**
     * Lista que almacena Credito Iva Causacion de la grilla
     */
    private RegistroDataModelImpl listaCreditoIvaCE;
    /**
     * Lista que almacena Debito Retefuente Causacion
     */
    private RegistroDataModelImpl listaDebitoRtfteC;
    /**
     * Lista que almacena Debito Retefuente Causacion de la grilla
     */
    private RegistroDataModelImpl listaDebitoRtfteCE;
    /**
     * Lista que almacena Credito Retefuente Causacion
     */
    private RegistroDataModelImpl listaCreditoRtfteC;
    /**
     * Lista que almacena Credito Retefuente Causacion de la grilla
     */
    private RegistroDataModelImpl listaCreditoRtfteCE;
    /**
     * Lista que almacena Debito Base Recaudo
     */
    private RegistroDataModelImpl listaDebitoBaseR;
    /**
     * Lista que almacena Debito Base Recaudo de la grilla
     */
    private RegistroDataModelImpl listaDebitoBaseRE;
    /**
     * Lista que almacena Credito Base Recaudo
     */
    private RegistroDataModelImpl listaCreditoBaseR;
    /**
     * Lista que almacena Credito Base Recaudo de la grilla
     */
    private RegistroDataModelImpl listaCreditoBaseRE;
    /**
     * Lista que almacena Debito Iva Recaudo
     */
    private RegistroDataModelImpl listaDebitoIvaR;
    /**
     * Lista que almacena Debito Iva Recaudo de la grilla
     */
    private RegistroDataModelImpl listaDebitoIvaRE;
    /**
     * Lista que almacena Debito Retefuente Recaudo
     */
    private RegistroDataModelImpl listaDebitoRtfteR;
    /**
     * Lista que almacena Debito Retefuente Recaudo de la grilla
     */
    private RegistroDataModelImpl listaDebitoRtfteRE;
    /**
     * Lista que almacena Lista Credito Iva Recaudo
     */
    private RegistroDataModelImpl listaCreditoIvaR;
    /**
     * Lista que almacena Lista Credito Iva Recaudo de la grilla
     */
    private RegistroDataModelImpl listaCreditoIvaRE;
    /**
     * Lista que almacena Credito Retefuente Recaudo
     */
    private RegistroDataModelImpl listaCreditoRtfteR;
    /**
     * Lista que almacena Credito Retefuente Recaudo de la grilla
     */
    private RegistroDataModelImpl listaCreditoRtfteRE;
    /**
     * Lista que almacena Cuenta Base Vigencia Anterior
     */
    private RegistroDataModelImpl listaCuentaBaseVa;
    /**
     * Lista que almacena Cuenta Base Vigencia Anterior de la grilla
     */
    private RegistroDataModelImpl listaCuentaBaseVaE;
    /**
     * Lista que almacena Cuenta Iva Vigencia Anterior
     */
    private RegistroDataModelImpl listaCuentaIvaVa;
    /**
     * Lista que almacena Cuenta Iva Vigencia Anterior de la grilla
     */
    private RegistroDataModelImpl listaCuentaIvaVaE;
    /**
     * Lista que almacena Cuenta Retefuente Vigecia Anterior
     */
    private RegistroDataModelImpl listaCuentaRtftVa;
    /**
     * Lista que almacena Cuenta Retefuente Vigecia Anterior de la
     * grilla
     */
    private RegistroDataModelImpl listaCuentaRtftVaE;
    /**
     * Lista que almacena Valor Corriente Refinanciacion
     */
    private RegistroDataModelImpl listaCuentaRefinan;
    /**
     * Lista que almacena Valor Corriente Refinanciacion de la grilla
     */
    private RegistroDataModelImpl listaCuentaRefinanE;
    /**
     * Lista que almacena Valor No Corriente Refinanciacion
     */
    private RegistroDataModelImpl listaCuentaRefinanVa;
    /**
     * Lista que almacena Valor No Corriente Refinanciacion de la
     * grilla
     */
    private RegistroDataModelImpl listaCuentaRefinanVaE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private String ano;
    private String codigo;
    private String concepto;
    @EJB
    private EjbContabilizarFacGenRemote ejbContabilizarFacGen;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RevisainterfacefactsControlador
     */
    public RevisainterfacefactsControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = Integer.toString(SysmanFunciones.ano(new Date()));
        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REVISAINTERFACEFACTS_CONTROLADOR
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
        enumBase = GenericUrlEnum.CUENTAS_CONCEPTOS_FACT_CNT;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaconcepto();
        cargarListaconceptoE();
        cargarListaTexto63();
        cargarListaTexto63E();
        cargarListaano();
        cargarListaanoE();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaDebitoBaseC();
        cargarListaDebitoBaseCE();
        cargarListaCreditoBaseC();
        cargarListaCreditoBaseCE();
        cargarListaDebitoIvaC();
        cargarListaDebitoIvaCE();
        cargarListaCreditoIvaC();
        cargarListaCreditoIvaCE();
        cargarListaDebitoRtfteC();
        cargarListaDebitoRtfteCE();
        cargarListaCreditoRtfteC();
        cargarListaCreditoRtfteCE();
        cargarListaDebitoBaseR();
        cargarListaDebitoBaseRE();
        cargarListaCreditoBaseR();
        cargarListaCreditoBaseRE();
        cargarListaDebitoIvaR();
        cargarListaDebitoIvaRE();
        cargarListaDebitoRtfteR();
        cargarListaDebitoRtfteRE();
        cargarListaCreditoIvaR();
        cargarListaCreditoIvaRE();
        cargarListaCreditoRtfteR();
        cargarListaCreditoRtfteRE();
        cargarListaCuentaBaseVa();
        cargarListaCuentaBaseVaE();
        cargarListaCuentaIvaVa();
        cargarListaCuentaIvaVaE();
        cargarListaCuentaRtftVa();
        cargarListaCuentaRtftVaE();
        cargarListaCuentaRefinan();
        cargarListaCuentaRefinanE();
        cargarListaCuentaRefinanVa();
        cargarListaCuentaRefinanVaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisainterfacefactsControladorUrlEnum.URL7171
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCostoE() {
        listaCentroCostoE = listaCentroCosto;

    }

    /**
     * 
     * Carga la lista listaDebitoBaseC
     *
     */
    public void cargarListaDebitoBaseC() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisainterfacefactsControladorUrlEnum.URL6969
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaDebitoBaseC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDebitoBaseC
     *
     */
    public void cargarListaDebitoBaseCE() {
        listaDebitoBaseCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoBaseC
     *
     */
    public void cargarListaCreditoBaseC() {
        listaCreditoBaseC = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoBaseC
     *
     */
    public void cargarListaCreditoBaseCE() {
        listaCreditoBaseCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoIvaC
     *
     */
    public void cargarListaDebitoIvaC() {
        listaDebitoIvaC = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoIvaC
     *
     */
    public void cargarListaDebitoIvaCE() {
        listaDebitoIvaCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoIvaC
     *
     */
    public void cargarListaCreditoIvaC() {
        listaCreditoIvaC = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoIvaC
     *
     */
    public void cargarListaCreditoIvaCE() {
        listaCreditoIvaCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoRtfteC
     *
     */
    public void cargarListaDebitoRtfteC() {
        listaDebitoRtfteC = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoRtfteC
     *
     */
    public void cargarListaDebitoRtfteCE() {
        listaDebitoRtfteCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoRtfteC
     *
     */
    public void cargarListaCreditoRtfteC() {
        listaCreditoRtfteC = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoRtfteC
     *
     */
    public void cargarListaCreditoRtfteCE() {
        listaCreditoRtfteCE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoBaseR
     *
     */
    public void cargarListaDebitoBaseR() {
        listaDebitoBaseR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoBaseR
     *
     */
    public void cargarListaDebitoBaseRE() {
        listaDebitoBaseRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoBaseR
     *
     */
    public void cargarListaCreditoBaseR() {
        listaCreditoBaseR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoBaseR
     *
     */
    public void cargarListaCreditoBaseRE() {
        listaCreditoBaseRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoIvaR
     *
     */
    public void cargarListaDebitoIvaR() {
        listaDebitoIvaR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoIvaR
     *
     */
    public void cargarListaDebitoIvaRE() {
        listaDebitoIvaRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoRtfteR
     *
     */
    public void cargarListaDebitoRtfteR() {
        listaDebitoRtfteR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaDebitoRtfteR
     *
     */
    public void cargarListaDebitoRtfteRE() {
        listaDebitoRtfteRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoIvaR
     *
     */
    public void cargarListaCreditoIvaR() {
        listaCreditoIvaR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoIvaR
     *
     */
    public void cargarListaCreditoIvaRE() {
        listaCreditoIvaRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoRtfteR
     *
     */
    public void cargarListaCreditoRtfteR() {
        listaCreditoRtfteR = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCreditoRtfteR
     *
     */
    public void cargarListaCreditoRtfteRE() {
        listaCreditoRtfteRE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaBaseVa
     *
     */
    public void cargarListaCuentaBaseVa() {
        listaCuentaBaseVa = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaBaseVa
     *
     */
    public void cargarListaCuentaBaseVaE() {
        listaCuentaBaseVaE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaIvaVa
     *
     */
    public void cargarListaCuentaIvaVa() {
        listaCuentaIvaVa = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaIvaVa
     *
     */
    public void cargarListaCuentaIvaVaE() {
        listaCuentaIvaVaE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRtftVa
     *
     */
    public void cargarListaCuentaRtftVa() {
        listaCuentaRtftVa = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRtftVa
     *
     */
    public void cargarListaCuentaRtftVaE() {
        listaCuentaRtftVaE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRefinan
     *
     */
    public void cargarListaCuentaRefinan() {
        listaCuentaRefinan = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRefinan
     *
     */
    public void cargarListaCuentaRefinanE() {
        listaCuentaRefinanE = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRefinanVa
     *
     */
    public void cargarListaCuentaRefinanVa() {
        listaCuentaRefinanVa = listaDebitoBaseC;
    }

    /**
     * 
     * Carga la lista listaCuentaRefinanVa
     *
     */
    public void cargarListaCuentaRefinanVaE() {
        listaCuentaRefinanVaE = listaDebitoBaseC;
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        parametrosListado.put(GeneralParameterEnum.CONCEPTO.getName(),
                        codigo);
    }

    /**
     * 
     * Carga la lista listaconcepto
     *
     */

    public void cargarListaconcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisainterfacefactsControladorUrlEnum.URL6922
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaconcepto
     *
     */
    public void cargarListaconceptoE() {

        listaconceptoE = listaconcepto;
    }

    /**
     * 
     * Carga la lista listaTexto63
     *
     */
    public void cargarListaTexto63() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RevisainterfacefactsControladorUrlEnum.URL8446
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaTexto63 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTexto63
     *
     */
    public void cargarListaTexto63E() {

        listaTexto63E = listaTexto63;
    }

    /**
     * 
     * Carga la lista listaano
     *
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisainterfacefactsControladorUrlEnum.URL10250
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
     * Carga la lista listaano
     *
     */
    public void cargarListaanoE() {

        listaanoE = listaano;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton ConceptoSin en la vista
     *
     *
     */
    public void oprimirConceptoSin() {
        String cadena;

        try {
            cadena = ejbContabilizarFacGen.enviarConceptosSinConfiguracion(
                            compania,
                            Integer.parseInt(ano));

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(cadena),
                            "CONCEPTOS_SIN_CONFIGURAR.TXT");

        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CopiarAno en la vista
     *
     *
     */
    public void oprimirCopiarAno() {
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.USUARIO.getName(), usuario);

            Parameter parameter = new Parameter();

            parameter.setFields(param);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisainterfacefactsControladorUrlEnum.URL9874
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ano
     * 
     * 
     */
    public void cambiarano() {
        reasignarOrigen();
        concepto = "";
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        codigo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();
        reasignarOrigen();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaconcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaconceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        codigo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto63
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto63(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        concepto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTexto63
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTexto63E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        concepto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaano
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaano(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaano
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaanoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName());
    }

    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CENTRO_COSTO", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoBaseC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoBaseC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_BASE_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoBaseC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoBaseCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_BASE_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoBaseC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoBaseC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_BASE_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoBaseC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoBaseCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_BASE_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoIvaC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoIvaC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_IVA_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoIvaC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoIvaCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_IVA_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoIvaC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoIvaC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_IVA_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoIvaC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoIvaCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_IVA_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRtfteC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoRtfteC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_RTFTE_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRtfteC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoRtfteCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_RTFTE_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRtfteC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoRtfteC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_RTFTE_C",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRtfteC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoRtfteCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_RTFTE_C", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoBaseR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoBaseR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_BASE_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoBaseR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoBaseRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_BASE_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoBaseR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoBaseR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_BASE_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoBaseR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoBaseRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_BASE_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoIvaR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoIvaR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_IVA_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoIvaR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoIvaRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_IVA_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRtfteR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoRtfteR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEBITO_RTFTE_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoRtfteR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoRtfteRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("DEBITO_RTFTE_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoIvaR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoIvaR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_IVA_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoIvaR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoIvaRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_IVA_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRtfteR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoRtfteR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CREDITO_RTFTE_R",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoRtfteR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoRtfteRE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CREDITO_RTFTE_R", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaBaseVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaBaseVa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_BASE_VA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaBaseVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaBaseVaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CUENTA_BASE_VA", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaIvaVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaIvaVa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_IVA_VA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaIvaVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaIvaVaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CUENTA_IVA_VA", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRtftVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRtftVa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTA_RTFT_VA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRtftVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRtftVaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CUENTA_RTFT_VA", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRefinan
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRefinan(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTAREFINAN",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRefinan
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRefinanE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CUENTAREFINAN", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRefinanVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRefinanVa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTAREFINAN_VA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRefinanVa
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRefinanVaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registro.getCampos().put("CUENTAREFINAN_VA", SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString());
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1490-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Maximize End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
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
     * Retorna la lista listaconcepto
     * 
     * @return listaconcepto
     */
    public RegistroDataModelImpl getListaconcepto() {
        return listaconcepto;
    }

    /**
     * Asigna la lista listaconcepto
     * 
     * @param listaconcepto
     * Variable a asignar en listaconcepto
     */
    public void setListaconcepto(RegistroDataModelImpl listaconcepto) {
        this.listaconcepto = listaconcepto;
    }

    /**
     * Retorna la lista listaconcepto
     * 
     * @return listaconcepto
     */
    public RegistroDataModelImpl getListaconceptoE() {
        return listaconceptoE;
    }

    /**
     * Asigna la lista listaconcepto
     * 
     * @param listaconcepto
     * Variable a asignar en listaconcepto
     */
    public void setListaconceptoE(RegistroDataModelImpl listaconceptoE) {
        this.listaconceptoE = listaconceptoE;
    }

    /**
     * Retorna la lista listaTexto63
     * 
     * @return listaTexto63
     */
    public RegistroDataModelImpl getListaTexto63() {
        return listaTexto63;
    }

    /**
     * Asigna la lista listaTexto63
     * 
     * @param listaTexto63
     * Variable a asignar en listaTexto63
     */
    public void setListaTexto63(RegistroDataModelImpl listaTexto63) {
        this.listaTexto63 = listaTexto63;
    }

    /**
     * Retorna la lista listaTexto63
     * 
     * @return listaTexto63
     */
    public RegistroDataModelImpl getListaTexto63E() {
        return listaTexto63E;
    }

    /**
     * Asigna la lista listaTexto63
     * 
     * @param listaTexto63
     * Variable a asignar en listaTexto63
     */
    public void setListaTexto63E(RegistroDataModelImpl listaTexto63E) {
        this.listaTexto63E = listaTexto63E;
    }

    /**
     * Retorna la lista listaano
     * 
     * @return listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaano
     * 
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    /**
     * Retorna la lista listaano
     * 
     * @return listaano
     */
    public List<Registro> getListaanoE() {
        return listaanoE;
    }

    /**
     * Asigna la lista listaano
     * 
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaanoE(List<Registro> listaanoE) {
        this.listaanoE = listaanoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getCompania() {
        return compania;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getano() {
        return ano;
    }

    public void setano(String ano) {
        this.ano = ano;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        return false;
    }

    @Override
    public boolean insertarDespues() {
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaCentroCosto
     * 
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCostoE() {
        return listaCentroCostoE;
    }

    /**
     * Asigna la lista listaCentroCosto
     * 
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    /**
     * Retorna la lista listaDebitoBaseC
     * 
     * @return listaDebitoBaseC
     */
    public RegistroDataModelImpl getListaDebitoBaseC() {
        return listaDebitoBaseC;
    }

    /**
     * Asigna la lista listaDebitoBaseC
     * 
     * @param listaDebitoBaseC
     * Variable a asignar en listaDebitoBaseC
     */
    public void setListaDebitoBaseC(RegistroDataModelImpl listaDebitoBaseC) {
        this.listaDebitoBaseC = listaDebitoBaseC;
    }

    /**
     * Retorna la lista listaDebitoBaseC
     * 
     * @return listaDebitoBaseC
     */
    public RegistroDataModelImpl getListaDebitoBaseCE() {
        return listaDebitoBaseCE;
    }

    /**
     * Asigna la lista listaDebitoBaseC
     * 
     * @param listaDebitoBaseC
     * Variable a asignar en listaDebitoBaseC
     */
    public void setListaDebitoBaseCE(RegistroDataModelImpl listaDebitoBaseCE) {
        this.listaDebitoBaseCE = listaDebitoBaseCE;
    }

    /**
     * Retorna la lista listaCreditoBaseC
     * 
     * @return listaCreditoBaseC
     */
    public RegistroDataModelImpl getListaCreditoBaseC() {
        return listaCreditoBaseC;
    }

    /**
     * Asigna la lista listaCreditoBaseC
     * 
     * @param listaCreditoBaseC
     * Variable a asignar en listaCreditoBaseC
     */
    public void setListaCreditoBaseC(RegistroDataModelImpl listaCreditoBaseC) {
        this.listaCreditoBaseC = listaCreditoBaseC;
    }

    /**
     * Retorna la lista listaCreditoBaseC
     * 
     * @return listaCreditoBaseC
     */
    public RegistroDataModelImpl getListaCreditoBaseCE() {
        return listaCreditoBaseCE;
    }

    /**
     * Asigna la lista listaCreditoBaseC
     * 
     * @param listaCreditoBaseC
     * Variable a asignar en listaCreditoBaseC
     */
    public void setListaCreditoBaseCE(
        RegistroDataModelImpl listaCreditoBaseCE) {
        this.listaCreditoBaseCE = listaCreditoBaseCE;
    }

    /**
     * Retorna la lista listaDebitoIvaC
     * 
     * @return listaDebitoIvaC
     */
    public RegistroDataModelImpl getListaDebitoIvaC() {
        return listaDebitoIvaC;
    }

    /**
     * Asigna la lista listaDebitoIvaC
     * 
     * @param listaDebitoIvaC
     * Variable a asignar en listaDebitoIvaC
     */
    public void setListaDebitoIvaC(RegistroDataModelImpl listaDebitoIvaC) {
        this.listaDebitoIvaC = listaDebitoIvaC;
    }

    /**
     * Retorna la lista listaDebitoIvaC
     * 
     * @return listaDebitoIvaC
     */
    public RegistroDataModelImpl getListaDebitoIvaCE() {
        return listaDebitoIvaCE;
    }

    /**
     * Asigna la lista listaDebitoIvaC
     * 
     * @param listaDebitoIvaC
     * Variable a asignar en listaDebitoIvaC
     */
    public void setListaDebitoIvaCE(RegistroDataModelImpl listaDebitoIvaCE) {
        this.listaDebitoIvaCE = listaDebitoIvaCE;
    }

    /**
     * Retorna la lista listaCreditoIvaC
     * 
     * @return listaCreditoIvaC
     */
    public RegistroDataModelImpl getListaCreditoIvaC() {
        return listaCreditoIvaC;
    }

    /**
     * Asigna la lista listaCreditoIvaC
     * 
     * @param listaCreditoIvaC
     * Variable a asignar en listaCreditoIvaC
     */
    public void setListaCreditoIvaC(RegistroDataModelImpl listaCreditoIvaC) {
        this.listaCreditoIvaC = listaCreditoIvaC;
    }

    /**
     * Retorna la lista listaCreditoIvaC
     * 
     * @return listaCreditoIvaC
     */
    public RegistroDataModelImpl getListaCreditoIvaCE() {
        return listaCreditoIvaCE;
    }

    /**
     * Asigna la lista listaCreditoIvaC
     * 
     * @param listaCreditoIvaC
     * Variable a asignar en listaCreditoIvaC
     */
    public void setListaCreditoIvaCE(RegistroDataModelImpl listaCreditoIvaCE) {
        this.listaCreditoIvaCE = listaCreditoIvaCE;
    }

    /**
     * Retorna la lista listaDebitoRtfteC
     * 
     * @return listaDebitoRtfteC
     */
    public RegistroDataModelImpl getListaDebitoRtfteC() {
        return listaDebitoRtfteC;
    }

    /**
     * Asigna la lista listaDebitoRtfteC
     * 
     * @param listaDebitoRtfteC
     * Variable a asignar en listaDebitoRtfteC
     */
    public void setListaDebitoRtfteC(RegistroDataModelImpl listaDebitoRtfteC) {
        this.listaDebitoRtfteC = listaDebitoRtfteC;
    }

    /**
     * Retorna la lista listaDebitoRtfteC
     * 
     * @return listaDebitoRtfteC
     */
    public RegistroDataModelImpl getListaDebitoRtfteCE() {
        return listaDebitoRtfteCE;
    }

    /**
     * Asigna la lista listaDebitoRtfteC
     * 
     * @param listaDebitoRtfteC
     * Variable a asignar en listaDebitoRtfteC
     */
    public void setListaDebitoRtfteCE(
        RegistroDataModelImpl listaDebitoRtfteCE) {
        this.listaDebitoRtfteCE = listaDebitoRtfteCE;
    }

    /**
     * Retorna la lista listaCreditoRtfteC
     * 
     * @return listaCreditoRtfteC
     */
    public RegistroDataModelImpl getListaCreditoRtfteC() {
        return listaCreditoRtfteC;
    }

    /**
     * Asigna la lista listaCreditoRtfteC
     * 
     * @param listaCreditoRtfteC
     * Variable a asignar en listaCreditoRtfteC
     */
    public void setListaCreditoRtfteC(
        RegistroDataModelImpl listaCreditoRtfteC) {
        this.listaCreditoRtfteC = listaCreditoRtfteC;
    }

    /**
     * Retorna la lista listaCreditoRtfteC
     * 
     * @return listaCreditoRtfteC
     */
    public RegistroDataModelImpl getListaCreditoRtfteCE() {
        return listaCreditoRtfteCE;
    }

    /**
     * Asigna la lista listaCreditoRtfteC
     * 
     * @param listaCreditoRtfteC
     * Variable a asignar en listaCreditoRtfteC
     */
    public void setListaCreditoRtfteCE(
        RegistroDataModelImpl listaCreditoRtfteCE) {
        this.listaCreditoRtfteCE = listaCreditoRtfteCE;
    }

    /**
     * Retorna la lista listaDebitoBaseR
     * 
     * @return listaDebitoBaseR
     */
    public RegistroDataModelImpl getListaDebitoBaseR() {
        return listaDebitoBaseR;
    }

    /**
     * Asigna la lista listaDebitoBaseR
     * 
     * @param listaDebitoBaseR
     * Variable a asignar en listaDebitoBaseR
     */
    public void setListaDebitoBaseR(RegistroDataModelImpl listaDebitoBaseR) {
        this.listaDebitoBaseR = listaDebitoBaseR;
    }

    /**
     * Retorna la lista listaDebitoBaseR
     * 
     * @return listaDebitoBaseR
     */
    public RegistroDataModelImpl getListaDebitoBaseRE() {
        return listaDebitoBaseRE;
    }

    /**
     * Asigna la lista listaDebitoBaseR
     * 
     * @param listaDebitoBaseR
     * Variable a asignar en listaDebitoBaseR
     */
    public void setListaDebitoBaseRE(RegistroDataModelImpl listaDebitoBaseRE) {
        this.listaDebitoBaseRE = listaDebitoBaseRE;
    }

    /**
     * Retorna la lista listaCreditoBaseR
     * 
     * @return listaCreditoBaseR
     */
    public RegistroDataModelImpl getListaCreditoBaseR() {
        return listaCreditoBaseR;
    }

    /**
     * Asigna la lista listaCreditoBaseR
     * 
     * @param listaCreditoBaseR
     * Variable a asignar en listaCreditoBaseR
     */
    public void setListaCreditoBaseR(RegistroDataModelImpl listaCreditoBaseR) {
        this.listaCreditoBaseR = listaCreditoBaseR;
    }

    /**
     * Retorna la lista listaCreditoBaseR
     * 
     * @return listaCreditoBaseR
     */
    public RegistroDataModelImpl getListaCreditoBaseRE() {
        return listaCreditoBaseRE;
    }

    /**
     * Asigna la lista listaCreditoBaseR
     * 
     * @param listaCreditoBaseR
     * Variable a asignar en listaCreditoBaseR
     */
    public void setListaCreditoBaseRE(
        RegistroDataModelImpl listaCreditoBaseRE) {
        this.listaCreditoBaseRE = listaCreditoBaseRE;
    }

    /**
     * Retorna la lista listaDebitoIvaR
     * 
     * @return listaDebitoIvaR
     */
    public RegistroDataModelImpl getListaDebitoIvaR() {
        return listaDebitoIvaR;
    }

    /**
     * Asigna la lista listaDebitoIvaR
     * 
     * @param listaDebitoIvaR
     * Variable a asignar en listaDebitoIvaR
     */
    public void setListaDebitoIvaR(RegistroDataModelImpl listaDebitoIvaR) {
        this.listaDebitoIvaR = listaDebitoIvaR;
    }

    /**
     * Retorna la lista listaDebitoIvaR
     * 
     * @return listaDebitoIvaR
     */
    public RegistroDataModelImpl getListaDebitoIvaRE() {
        return listaDebitoIvaRE;
    }

    /**
     * Asigna la lista listaDebitoIvaR
     * 
     * @param listaDebitoIvaR
     * Variable a asignar en listaDebitoIvaR
     */
    public void setListaDebitoIvaRE(RegistroDataModelImpl listaDebitoIvaRE) {
        this.listaDebitoIvaRE = listaDebitoIvaRE;
    }

    /**
     * Retorna la lista listaDebitoRtfteR
     * 
     * @return listaDebitoRtfteR
     */
    public RegistroDataModelImpl getListaDebitoRtfteR() {
        return listaDebitoRtfteR;
    }

    /**
     * Asigna la lista listaDebitoRtfteR
     * 
     * @param listaDebitoRtfteR
     * Variable a asignar en listaDebitoRtfteR
     */
    public void setListaDebitoRtfteR(RegistroDataModelImpl listaDebitoRtfteR) {
        this.listaDebitoRtfteR = listaDebitoRtfteR;
    }

    /**
     * Retorna la lista listaDebitoRtfteR
     * 
     * @return listaDebitoRtfteR
     */
    public RegistroDataModelImpl getListaDebitoRtfteRE() {
        return listaDebitoRtfteRE;
    }

    /**
     * Asigna la lista listaDebitoRtfteR
     * 
     * @param listaDebitoRtfteR
     * Variable a asignar en listaDebitoRtfteR
     */
    public void setListaDebitoRtfteRE(
        RegistroDataModelImpl listaDebitoRtfteRE) {
        this.listaDebitoRtfteRE = listaDebitoRtfteRE;
    }

    /**
     * Retorna la lista listaCreditoIvaR
     * 
     * @return listaCreditoIvaR
     */
    public RegistroDataModelImpl getListaCreditoIvaR() {
        return listaCreditoIvaR;
    }

    /**
     * Asigna la lista listaCreditoIvaR
     * 
     * @param listaCreditoIvaR
     * Variable a asignar en listaCreditoIvaR
     */
    public void setListaCreditoIvaR(RegistroDataModelImpl listaCreditoIvaR) {
        this.listaCreditoIvaR = listaCreditoIvaR;
    }

    /**
     * Retorna la lista listaCreditoIvaR
     * 
     * @return listaCreditoIvaR
     */
    public RegistroDataModelImpl getListaCreditoIvaRE() {
        return listaCreditoIvaRE;
    }

    /**
     * Asigna la lista listaCreditoIvaR
     * 
     * @param listaCreditoIvaR
     * Variable a asignar en listaCreditoIvaR
     */
    public void setListaCreditoIvaRE(RegistroDataModelImpl listaCreditoIvaRE) {
        this.listaCreditoIvaRE = listaCreditoIvaRE;
    }

    /**
     * Retorna la lista listaCreditoRtfteR
     * 
     * @return listaCreditoRtfteR
     */
    public RegistroDataModelImpl getListaCreditoRtfteR() {
        return listaCreditoRtfteR;
    }

    /**
     * Asigna la lista listaCreditoRtfteR
     * 
     * @param listaCreditoRtfteR
     * Variable a asignar en listaCreditoRtfteR
     */
    public void setListaCreditoRtfteR(
        RegistroDataModelImpl listaCreditoRtfteR) {
        this.listaCreditoRtfteR = listaCreditoRtfteR;
    }

    /**
     * Retorna la lista listaCreditoRtfteR
     * 
     * @return listaCreditoRtfteR
     */
    public RegistroDataModelImpl getListaCreditoRtfteRE() {
        return listaCreditoRtfteRE;
    }

    /**
     * Asigna la lista listaCreditoRtfteR
     * 
     * @param listaCreditoRtfteR
     * Variable a asignar en listaCreditoRtfteR
     */
    public void setListaCreditoRtfteRE(
        RegistroDataModelImpl listaCreditoRtfteRE) {
        this.listaCreditoRtfteRE = listaCreditoRtfteRE;
    }

    /**
     * Retorna la lista listaCuentaBaseVa
     * 
     * @return listaCuentaBaseVa
     */
    public RegistroDataModelImpl getListaCuentaBaseVa() {
        return listaCuentaBaseVa;
    }

    /**
     * Asigna la lista listaCuentaBaseVa
     * 
     * @param listaCuentaBaseVa
     * Variable a asignar en listaCuentaBaseVa
     */
    public void setListaCuentaBaseVa(RegistroDataModelImpl listaCuentaBaseVa) {
        this.listaCuentaBaseVa = listaCuentaBaseVa;
    }

    /**
     * Retorna la lista listaCuentaBaseVa
     * 
     * @return listaCuentaBaseVa
     */
    public RegistroDataModelImpl getListaCuentaBaseVaE() {
        return listaCuentaBaseVaE;
    }

    /**
     * Asigna la lista listaCuentaBaseVa
     * 
     * @param listaCuentaBaseVa
     * Variable a asignar en listaCuentaBaseVa
     */
    public void setListaCuentaBaseVaE(
        RegistroDataModelImpl listaCuentaBaseVaE) {
        this.listaCuentaBaseVaE = listaCuentaBaseVaE;
    }

    /**
     * Retorna la lista listaCuentaIvaVa
     * 
     * @return listaCuentaIvaVa
     */
    public RegistroDataModelImpl getListaCuentaIvaVa() {
        return listaCuentaIvaVa;
    }

    /**
     * Asigna la lista listaCuentaIvaVa
     * 
     * @param listaCuentaIvaVa
     * Variable a asignar en listaCuentaIvaVa
     */
    public void setListaCuentaIvaVa(RegistroDataModelImpl listaCuentaIvaVa) {
        this.listaCuentaIvaVa = listaCuentaIvaVa;
    }

    /**
     * Retorna la lista listaCuentaIvaVa
     * 
     * @return listaCuentaIvaVa
     */
    public RegistroDataModelImpl getListaCuentaIvaVaE() {
        return listaCuentaIvaVaE;
    }

    /**
     * Asigna la lista listaCuentaIvaVa
     * 
     * @param listaCuentaIvaVa
     * Variable a asignar en listaCuentaIvaVa
     */
    public void setListaCuentaIvaVaE(RegistroDataModelImpl listaCuentaIvaVaE) {
        this.listaCuentaIvaVaE = listaCuentaIvaVaE;
    }

    /**
     * Retorna la lista listaCuentaRtftVa
     * 
     * @return listaCuentaRtftVa
     */
    public RegistroDataModelImpl getListaCuentaRtftVa() {
        return listaCuentaRtftVa;
    }

    /**
     * Asigna la lista listaCuentaRtftVa
     * 
     * @param listaCuentaRtftVa
     * Variable a asignar en listaCuentaRtftVa
     */
    public void setListaCuentaRtftVa(RegistroDataModelImpl listaCuentaRtftVa) {
        this.listaCuentaRtftVa = listaCuentaRtftVa;
    }

    /**
     * Retorna la lista listaCuentaRtftVa
     * 
     * @return listaCuentaRtftVa
     */
    public RegistroDataModelImpl getListaCuentaRtftVaE() {
        return listaCuentaRtftVaE;
    }

    /**
     * Asigna la lista listaCuentaRtftVa
     * 
     * @param listaCuentaRtftVa
     * Variable a asignar en listaCuentaRtftVa
     */
    public void setListaCuentaRtftVaE(
        RegistroDataModelImpl listaCuentaRtftVaE) {
        this.listaCuentaRtftVaE = listaCuentaRtftVaE;
    }

    /**
     * Retorna la lista listaCuentaRefinan
     * 
     * @return listaCuentaRefinan
     */
    public RegistroDataModelImpl getListaCuentaRefinan() {
        return listaCuentaRefinan;
    }

    /**
     * Asigna la lista listaCuentaRefinan
     * 
     * @param listaCuentaRefinan
     * Variable a asignar en listaCuentaRefinan
     */
    public void setListaCuentaRefinan(
        RegistroDataModelImpl listaCuentaRefinan) {
        this.listaCuentaRefinan = listaCuentaRefinan;
    }

    /**
     * Retorna la lista listaCuentaRefinan
     * 
     * @return listaCuentaRefinan
     */
    public RegistroDataModelImpl getListaCuentaRefinanE() {
        return listaCuentaRefinanE;
    }

    /**
     * Asigna la lista listaCuentaRefinan
     * 
     * @param listaCuentaRefinan
     * Variable a asignar en listaCuentaRefinan
     */
    public void setListaCuentaRefinanE(
        RegistroDataModelImpl listaCuentaRefinanE) {
        this.listaCuentaRefinanE = listaCuentaRefinanE;
    }

    /**
     * Retorna la lista listaCuentaRefinanVa
     * 
     * @return listaCuentaRefinanVa
     */
    public RegistroDataModelImpl getListaCuentaRefinanVa() {
        return listaCuentaRefinanVa;
    }

    /**
     * Asigna la lista listaCuentaRefinanVa
     * 
     * @param listaCuentaRefinanVa
     * Variable a asignar en listaCuentaRefinanVa
     */
    public void setListaCuentaRefinanVa(
        RegistroDataModelImpl listaCuentaRefinanVa) {
        this.listaCuentaRefinanVa = listaCuentaRefinanVa;
    }

    /**
     * Retorna la lista listaCuentaRefinanVa
     * 
     * @return listaCuentaRefinanVa
     */
    public RegistroDataModelImpl getListaCuentaRefinanVaE() {
        return listaCuentaRefinanVaE;
    }

    /**
     * Asigna la lista listaCuentaRefinanVa
     * 
     * @param listaCuentaRefinanVa
     * Variable a asignar en listaCuentaRefinanVa
     */
    public void setListaCuentaRefinanVaE(
        RegistroDataModelImpl listaCuentaRefinanVaE) {
        this.listaCuentaRefinanVaE = listaCuentaRefinanVaE;
    }
}
