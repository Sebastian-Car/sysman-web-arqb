/*-
 * SfconceptosControlador.java
 *
 * 1.0
 * 
 * 08/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralRemote;
import com.sysman.general.enums.SfconceptosControladorEnum;
import com.sysman.general.enums.SfconceptosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar los conceptos
 *
 * @version 1.0, 08/11/2017
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class SfconceptosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en la aplicaicon
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Codigo del tipo de cobro seleccionado al ingresar al modulo
     */
    private String tipoCobro;
    /**
     * "Cuentas para manejo de la utilidad"
     */
    private boolean manejaUtilidad;
    /**
     * variable que valida la visibilidad de la etiqueta y campo
     * "Nivel"
     */
    private boolean nivelVisible;
    /**
     * variable que valida la visibilidad del boton "Configurar
     * cuentas para derechos de conexion"
     */
    private boolean derechoConexion;
    /**
     * variable que valida la visibilidad de la etiqueta y combo
     * "Agrupar como"
     */
    private boolean agruparVisible;
    /**
     * variable que valida la visibilidad del boton "Configurar
     * calculo de cree"
     */
    private boolean calculoCree;

    /**
     * variable que valida la visibilidad del boton "Configurar
     * cuentas con tercero"
     */
    private boolean terceroVisible;
    /**
     * variable que valida la visibilidad de las etiquetas, combos y
     * campo de los descuentos
     */
    private boolean bloquearPorcentaje;
    
    
    /**
     * variable que valida la visibilidad de las etiquetas, combos y
     * campo de los descuentos
     */
     private boolean bloquearPorcentajeReteIva;
    
     /**
     * Identificador MANEJA_RECAUDOTERCEROS de la tabla SF_TIPOCOBRO,
     * indicador "Maneja Recaudo a Favor de Terceros"
     */
    private boolean recaudoTercero;
    /**
     * Valor del campo CLASE_CUENTASRECAUDO de la tabla SF_TIPOCOBRO
     */
    private String cuentasTercero;
    /**
     * Variable que valida si el campo de formula es bloqueado o no y
     * si el campo y la etiqueta Redondear valor a se hace visible o
     * no
     */
    private boolean aplicaFormula;
    /**
     * Variable que valida si los campos de Porcentaje Ica y cuentas
     * debito y credito son bloqueadas o no
     */
    private boolean aplicaIca;

    /**
     * Variable que valida si los campos de Porcentaje Iva y cuentas
     * debito y credito son bloqueadas o no
     */
    private boolean aplicaIva;

    /**
     * Variable que valida si los campos de Porcentaje retefuente y
     * cuentas debito y credito son bloqueadas o no
     */
    private boolean aplicaReteFuente;

    /**
     * Variable que valida si los campos de Impoconsumo y cuentas
     * debito y credito son bloqueadas o no
     */
    private boolean aplicaImpoConsumo;
    
    /**
     * Variable que valida si los campos de AutoRenta y cuentas
     * debito y credito son bloqueadas o no
     */    
    private boolean aplicaAutoRenta;
    
    /**
     * Variable que valida si los campos de AutoICA y cuentas
     * debito y credito son bloqueadas o no
     */ 
    private boolean aplicaAutoICA;

    /**
     * Variable que valida si la etiqueta y el check "�nico C�lculo
     * Base Gravable", se hace visible o no
     */
    private boolean baseGravableVisible;
    /**
     * Variable que valida si el combo ano se bloquea o no
     */
    private boolean bloquearAno;

    /**
     * Variable que valida si los botones son activo o no
     */
    private boolean configurar;
    /**
     * variable que valida si el boton configurar conceptos
     * dependientes se hace visible
     */
    private boolean conceptoDependiente;
    
    /**
     * variable que valida si el los campos de laas cuentas publico o privado se hacen visibles
     */
    private boolean tipoEntidad;
    
    /**
     * Variable que almacena el nombre del Indicador Descuento o No causar
     */
    private String nombreIndicador;
    
	private boolean visibleSigec;
	
	private boolean permiteAjusteDecimales;
	
    private String cargueAnio;
    /**
     * Variable que realiza el proceso de cargue del año actual
     */

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros del combo ano
     */
    private List<Registro> listaAno;
    /**
     * Lista de registros del combo tipo de concepto
     */
    private List<Registro> listaClaseConcepto;
    private List<Registro> listaTiposConcepto;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros del combo auxiliar
     */
    private RegistroDataModelImpl listaAuxiliar;
    /**
     * Lista de registros del combo de cuenta debito base
     */
    private RegistroDataModelImpl listaCuentaDebitoBase;
    /**
     * Lista de registros del combo cuenta credito base
     */
    private RegistroDataModelImpl listaCuentaCreditoBase;
    
    /**
     * 
     * Lista de registros del combo cuenta credito base
     */
    private RegistroDataModelImpl listaCuentaDebitoReteIvaAct;
    /**
     * 
     * Lista de registros del combo cuenta credito base
     */
    private RegistroDataModelImpl listaCuentaCreditoReteIvaAct;
    
    /**
     * 
     * Lista de registros del combo cuenta credito base
     */
    private RegistroDataModelImpl listaCuentaDebitoIvaAntes;
    
    /**
     * 
     * Lista de registros del combo cuenta credito base
     */
    private RegistroDataModelImpl listaCuentaCreditoIvaAntes;
    
    
    /**
     * Lista de registros del combo centro de costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista de registros del combo cuenta debito iva
     */
    private RegistroDataModelImpl listaCuentaDebitoIva;
    /**
     * Lista de registros del combo cuenta credito iva
     */
    private RegistroDataModelImpl listaCuentaCreditoIva;
    /**
     * Lista de registros del combo cuenta debito rete
     */
    private RegistroDataModelImpl listaCuentaDebitoRete;
    /**
     * Lista de registros del combo cuenta credito rete
     */
    private RegistroDataModelImpl listaCuentaCreditoRete;
    /**
     * Lista de registros del combo cuenta debito descuento
     */
    private RegistroDataModelImpl listaCuentaDebitoDescuento;
    /**
     * Lista de registros del combo cuenta credito descuento
     */
    private RegistroDataModelImpl listaCuentaCreditoDescuento;
    /**
     * Lista de registros del combo cuenta debito ica
     */
    private RegistroDataModelImpl listaCuentaDebitoIca;
    /**
     * Lista de registros del combo cuenta credito ica
     */
    private RegistroDataModelImpl listaCuentaCreditoIca;
    /**
     * Lista de registros del combo cuenta debito base av
     */
    private RegistroDataModelImpl listaCuentaDebitoBaseAv;
    /**
     * Lista de registros del combo cuenta credito base av
     */
    private RegistroDataModelImpl listaCuentaCreditoBaseAv;
    /**
     * Lista de registros del combo cuenta debito iva av
     */
    private RegistroDataModelImpl listaCuentaDebitoIvaAv;
    /**
     * Lista de registros del combo cuenta credito iva av
     */
    private RegistroDataModelImpl listaCuentaCreditoIvaAv;
    /**
     * Lista de registros del combo cuenta debito rete av
     */
    private RegistroDataModelImpl listaCuentaDebitoReteAv;
    /**
     * Lista de registros del combo cuenta credito rete av
     */
    private RegistroDataModelImpl listaCuentaCreditoReteAv;
    /**
     * Lista de registros del combo cuenta debito descuento av
     */
    private RegistroDataModelImpl listaCuentaDebitoDescuentoAv;
    /**
     * Lista de registros del combo cuenta credito descuento av
     */
    private RegistroDataModelImpl listaCuentaCreditoDescuentoAv;
    /**
     * Lista de registros del combo cuenta debito ica av
     */
    private RegistroDataModelImpl listaCuentaDebitoIcaAv;
    /**
     * Lista de registros del combo cuenta credito ica av
     */
    private RegistroDataModelImpl listaCuentaCreditoIcaAv;
    /**
     * Lista de registros del combo unidad
     */
    private RegistroDataModelImpl listaUnidad;
    /**
     * Lista de registros del combo cuenta debito utilidad
     */
    private RegistroDataModelImpl listaCuentaDebitoUtilidad;
    /**
     * Lista de registros del combo cuenta credito utilidad
     */
    private RegistroDataModelImpl listaCuentaCreditoUtilidad;
    /**
     * Lista de registros del combo cuenta debito utilidad av
     */
    private RegistroDataModelImpl listaCuentaDebitoUtilidadAv;
    /**
     * Lista de registros del combo cuenta credito utilidad av
     */
    private RegistroDataModelImpl listaCuentaCreditoUtilidadAv;
    /**
     * Lista de registros del combo cuenta recaudo
     */
    private RegistroDataModelImpl listaCuentaRecaudo;

    /**
     * Lista que carga las referencias
     */
    private RegistroDataModelImpl listaReferencia;

    /**
     * Lista que carga las fuentes de recurso
     */
    private RegistroDataModelImpl listaFuenteRecurso;

    /**
     * Lista que carga las cuentas debito de Impoconsumo
     */
    private RegistroDataModelImpl listaCuentaDebitoImpoConsumo;
    /**
     * Lista que carga las cuentas credito de Impoconsumo
     */
    private RegistroDataModelImpl listaCuentaCreditoImpoConsumo;
        
    /**
     * Lista de registros del combo cuenta debito Auto renta vigencia actual
     */
    private RegistroDataModelImpl listaDebitoAutoRentaVAct;
    
    /**
     * Lista de registros del combo cuenta credito AutoRenta vigencia actual
     */
    private RegistroDataModelImpl listaCreditoAutoRentaVAct;    
    
    /**
     * Lista de registros del combo cuenta debito AutoRenta vigencia actual
     */
    private RegistroDataModelImpl listaDebitoAutoRentaVAnt; 
    
    /**
     * Lista de registros del combo cuenta credito AutoRenta vigencia anterior
     */
    private RegistroDataModelImpl listaCreditoAutoRentaVAnt;
    
    /**
     * Lista de registros del combo cuenta debito AutoICA vigencia actual
     */
    private RegistroDataModelImpl listaDebitoAutoICAVAct;
    
    /**
     * Lista de registros del combo cuenta credito AutoICA vigencia actual
     */
    private RegistroDataModelImpl listaCreditoAutoICAVAct;
    
    /**
     * Lista de registros del combo cuenta debito AutoICA vigencia anterior
     */
    private RegistroDataModelImpl listaDebitoAutoICAVAnt;
    
    /**
     * Lista de registros del combo cuenta credito AutoICA vigencia anterior
     */
    private RegistroDataModelImpl listaCreditoAutoICAVAnt;
    
    /**
     * Lista de registros del combo CCPET
     */
    private RegistroDataModelImpl listaCCPET;
    
    /**
     * Lista de registros del combo FuenteCuipo
     */
    private RegistroDataModelImpl listaFuenteCuipo;
    
    /**
     * Lista de registros del combo UnidadEjecutora
     */
    private RegistroDataModelImpl listaUnidadEjecutora;
    
    /**
     *  Lista de registros del combo cuenta debito Entidad Publica
     */
 private RegistroDataModelImpl listaCuentaDebitoPublico;
    /**
     * Lista de registros del combo cuenta Credito Entidad Publica
     */
 private RegistroDataModelImpl listaCuentaCreditoPublico;
    /**
     * Lista de registros del combo cuenta debito Entidad Privada
     */
 private RegistroDataModelImpl listaCuentaDebitoPrivado;
    /**
     * Lista de registros del combo cuenta Credito Entidad Privada
     */
 private RegistroDataModelImpl listaCuentaCreditoPrivado;

    /**
     * Atributo que almacena el anio seleccionado antes de ingresar al
     * modulo de facturacion general
     */
    private String anio;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Map recibida por parametro que trae la llave del registro por
     * el cual se carga este formulario
     */
    Map<String, Object> ridConcepto;
    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbFacturacionGeneralRemote ejbFactGeneralCero;
    
    private boolean tiposConceptos;

    /**
     * Crea una nueva instancia de SfconceptosControlador
     */
    @SuppressWarnings("unchecked")
    public SfconceptosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = Integer.toString(SysmanConstantes.MODULO_FACTURACION_GENERAL);

        try {
            // 1437
            numFormulario = GeneralCodigoFormaEnum.SFCONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridConcepto = (Map<String, Object>) parametrosEntrada
                                .get(SfconceptosControladorEnum.RIDCONCEPTO
                                                .getValue());

                anio = parametrosEntrada.get(
                                ConstantesFacturacionGenEnum.ANIO.getValue())
                                .toString();

                tipoCobro = parametrosEntrada.get(
                                ConstantesFacturacionGenEnum.TIPOCOBRO
                                                .getValue())
                                .toString();
                recaudoTercero = (boolean) parametrosEntrada
                                .get(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                                .getValue());
                cuentasTercero = SysmanFunciones.nvlStr(parametrosEntrada
                                .get(ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                                .getValue())
                                .toString(), "");
            }
            else {
                anio = SessionUtil.getSessionVar(
                                ConstantesFacturacionGenEnum.ANIO.getValue())
                                .toString();

                tipoCobro = SessionUtil.getSessionVar(
                                ConstantesFacturacionGenEnum.TIPOCOBRO
                                                .getValue())
                                .toString();
                recaudoTercero = (boolean) SessionUtil
                                .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                                .getValue());
                cuentasTercero = SysmanFunciones.nvlStr(SessionUtil
                                .getSessionVar(ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                                .getValue())
                                .toString(), "");
            }  
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListaUnidad();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaClaseConcepto();
        cargarListaTiposConcepto();
        // </CARGAR_LISTA>

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaAuxiliar();
        cargarListaCentroCosto();
        cargarListaCuentaDebitoBase();
        cargarListaCuentaCreditoBase();
        cargarListaCuentaDebitoIva();
        cargarListaCuentaCreditoIva();
        cargarListaCuentaDebitoRete();
        cargarListaCuentaCreditoRete();
        cargarListaCuentaDebitoDescuento();
        
        cargarListaCuentaDebitoReteIvaAct();
        cargarListaCuentaCreditoReteIvaAct();
        cargarListaCuentaDebitoIvaAntes();
        cargarListaCuentaCreditoIvaAntes();
        
        cargarListaCuentaCreditoDescuento();
        cargarListaCuentaDebitoIca();
        cargarListaCuentaCreditoIca();
        cargarListaCuentaDebitoBaseAv();
        cargarListaCuentaCreditoBaseAv();
        cargarListaCuentaDebitoIvaAv();
        cargarListaCuentaCreditoIvaAv();
        cargarListaCuentaDebitoReteAv();
        cargarListaCuentaCreditoReteAv();
        cargarListaCuentaDebitoDescuentoAv();
        cargarListaCuentaCreditoDescuentoAv();
        cargarListaCuentaDebitoIcaAv();
        cargarListaCuentaCreditoIcaAv();
        cargarListaCuentaDebitoUtilidad();
        cargarListaCuentaCreditoUtilidad();
        cargarListaCuentaDebitoUtilidadAv();
        cargarListaCuentaCreditoUtilidadAv();
        cargarListaCuentaRecaudo();
        cargarListaReferencia();
        cargarListaFuenteRecurso();
        cargarListaCuentaDebitoImpoConsumo();
        cargarListaCuentaCreditoImpoConsumo();
        cargarListaDebitoAutoRentaVAct();
        cargarListaCreditoAutoRentaVAct();
        cargarListaDebitoAutoRentaVAnt();
        cargarListaCreditoAutoRentaVAnt();        
        cargarListaDebitoAutoICAVAct();
        cargarListaCreditoAutoICAVAct();
        cargarListaDebitoAutoICAVAnt();
        cargarListaCreditoAutoICAVAnt();
        
        cargarListaCCPET();
        cargarListaFuenteCuipo();
        cargarListaUnidadEjecutora();
        
        cargarListaCuentaDebitoPublico(); 
        cargarListaCuentaCreditoPublico();
        cargarListaCuentaDebitoPrivado(); 
        cargarListaCuentaCreditoPrivado();
        
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.SF_CONCEPTOS;

        buscarLlave();
        asignarOrigenDatos();
        cargueAnio = String.valueOf(LocalDate.now().getYear());
        iniciarListasSub();
        

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(SfconceptosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        anio);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfconceptosControladorUrlEnum.URL9670
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaClaseConcepto
     */
    public void cargarListaClaseConcepto() {
        try {
            listaClaseConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfconceptosControladorUrlEnum.URL11419
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Carga la lista listaTiposConcepto
     *
     */
    public void cargarListaTiposConcepto(){
    	
    	 Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.VALOR.getName(), tiposConceptos);
         try {
        	 listaTiposConcepto = RegistroConverter.toListRegistro(
                             requestManager.getList(UrlServiceUtil.getInstance()
                                             .getUrlServiceByUrlByEnumID(
                                                             SfconceptosControladorUrlEnum.URL195001
                                                                             .getValue())
                                             .getUrl(), param));
         }
         catch (SystemException e) {
             JsfUtil.agregarMensajeError(e.getMessage());
             logger.error(e.getMessage(), e);

         }
         
    }

    /**
     * 
     * Carga la lista listaAuxiliar
     *
     */
    public void cargarListaAuxiliar() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL10092
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);  
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoBase
     */
    public void cargarListaCuentaDebitoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
     
        listaCuentaDebitoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoBase
     */
    public void cargarListaCuentaCreditoBase() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    
    /**
     * 
     * Carga la lista listaCuentaDebito Entidad Publica
     */
    public void cargarListaCuentaDebitoPublico() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
         
        listaCuentaDebitoPublico = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCuentaCredito Entidad Publica
     */
    public void cargarListaCuentaCreditoPublico() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoPublico = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista listaCuentaDebito Entidad Privada
     */
    public void cargarListaCuentaDebitoPrivado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoPrivado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCuentaCredito Entidad Privada
     */
    public void cargarListaCuentaCreditoPrivado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoPrivado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    /**
     * 
     * Carga la lista listaCentroCosto
     * 
     */
    public void cargarListaCentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL10742
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);  
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIva
     */
    public void cargarListaCuentaDebitoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoIva = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIva
     */
    public void cargarListaCuentaCreditoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoIva = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoRete
     */
    public void cargarListaCuentaDebitoRete() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoRete = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoRete
     */
    public void cargarListaCuentaCreditoRete() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoRete = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoDescuento
     */
    public void cargarListaCuentaDebitoDescuento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoDescuento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista cargarListaCuentaDebitoReteIvaAct
     */
    public void cargarListaCuentaDebitoReteIvaAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoReteIvaAct = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    
    /**
     * 
     * Carga la lista cargarListaCuentaDebitoReteIvaAct
     */
    public void cargarListaCuentaDebitoIvaAntes() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoIvaAntes = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    
    /**
     * 
     * Carga la lista cargarListaCuentaDebitoReteIvaAct
     */
    public void cargarListaCuentaCreditoIvaAntes() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoIvaAntes = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    
    
    
    /**
     * 
     * Carga la lista cargarListaCuentaDebitoReteIvaAct
     */
    public void cargarListaCuentaCreditoReteIvaAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoReteIvaAct = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    
    

    /**
     * 
     * Carga la lista listaCuentaCreditoDescuento
     */
    public void cargarListaCuentaCreditoDescuento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaCreditoDescuento = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIca
     */
    public void cargarListaCuentaDebitoIca() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCuentaDebitoIca = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIca
     */
    public void cargarListaCuentaCreditoIca() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoIca = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista ListaCreditoAutoRentaVAct
     */
    public void cargarListaCreditoAutoRentaVAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCreditoAutoRentaVAct = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaDebitoAutoRentaVAnt
     */
    public void cargarListaDebitoAutoRentaVAnt() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaDebitoAutoRentaVAnt = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista listaDebitoAutoRentaVAct
     */
    public void cargarListaDebitoAutoRentaVAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaDebitoAutoRentaVAct = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCreditoAutoRentaVAnt
     */
    public void cargarListaCreditoAutoRentaVAnt() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaCreditoAutoRentaVAnt = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista listaDebitoAutoICAVAct
     */
    public void cargarListaDebitoAutoICAVAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
        
        listaDebitoAutoICAVAct = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista listaCreditoAutoRentaVAnt
     */
    public void cargarListaCreditoAutoICAVAct() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCreditoAutoICAVAct = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }
    
    /**
     * 
     * Carga la lista listaDebitoAutoICAVAnt
     */
    public void cargarListaDebitoAutoICAVAnt() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaDebitoAutoICAVAnt = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCreditoAutoRentaVAnt
     */
    public void cargarListaCreditoAutoICAVAnt() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCreditoAutoICAVAnt = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoBaseAv
     */
    public void cargarListaCuentaDebitoBaseAv() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoBaseAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoBaseAv
     */
    public void cargarListaCuentaCreditoBaseAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoBaseAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIvaAv
     */
    public void cargarListaCuentaDebitoIvaAv() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoIvaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIvaAv
     */
    public void cargarListaCuentaCreditoIvaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoIvaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoReteAv
     */
    public void cargarListaCuentaDebitoReteAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoReteAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoReteAv
     */
    public void cargarListaCuentaCreditoReteAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoReteAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoDescuentoAv
     */
    public void cargarListaCuentaDebitoDescuentoAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoDescuentoAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoDescuentoAv
     */
    public void cargarListaCuentaCreditoDescuentoAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoDescuentoAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIcaAv
     */
    public void cargarListaCuentaDebitoIcaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoIcaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIcaAv
     */
    public void cargarListaCuentaCreditoIcaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoIcaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaUnidad
     */
    public void cargarListaUnidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL47769
                                                        .getValue());
        listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.UNIDAD.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoUtilidad
     */
    public void cargarListaCuentaDebitoUtilidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoUtilidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoUtilidad
     */
    public void cargarListaCuentaCreditoUtilidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoUtilidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoUtilidadAv
     */
    public void cargarListaCuentaDebitoUtilidadAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoUtilidadAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoUtilidadAv
     */
    public void cargarListaCuentaCreditoUtilidadAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoUtilidadAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());

    }

    /**
     * 
     * Carga la lista listaCuentaRecaudo
     */
    public void cargarListaCuentaRecaudo() {

        try {
            String manejaRecaudo = ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA RECAUDO A FAVOR DE TERCEROS", modulo,
                            new Date(), true);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SfconceptosControladorUrlEnum.URL55481
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

            if ("SI".equals(manejaRecaudo) && recaudoTercero) {

                param.put(SfconceptosControladorEnum.CLASECUENTA.getValue(),
                                cuentasTercero);
            }
            else {

                String claseCuentas = ejbSysmanUtil.consultarParametro(compania,
                                "SF MANEJAR CLASE CUENTAS PARA RECAUDOS",
                                modulo, new Date(), true);

                param.put(SfconceptosControladorEnum.CLASECUENTA.getValue(),
                                claseCuentas);
            }

            listaCuentaRecaudo = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL55784
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);
 
        listaReferencia = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecurso() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL58412
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaFuenteRecurso = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoImpoConsumo
     *
     */
    public void cargarListaCuentaDebitoImpoConsumo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaDebitoImpoConsumo = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoImpoConsumo
     *
     */
    public void cargarListaCuentaCreditoImpoConsumo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfconceptosControladorUrlEnum.URL11903
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cargueAnio);

        listaCuentaCreditoImpoConsumo = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, SfconceptosControladorEnum.ID.getValue());
    }
    
    //7716154 mperez
    
    /**
	 * 
	 * Carga la lista listaTipoClasificador Unificada MPEREZ
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo)
	{
		RegistroDataModelImpl listaTipo = null;
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);

		urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SfconceptosControladorUrlEnum.URL2252
								.getValue());
			
		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
		 
		return listaTipo;
	}
    /**
     * 
     * Carga la lista listaCCPET
     *
     */
    public void cargarListaCCPET() {

       listaCCPET = cargarListaTipoClasificador("006");
    }
    
    /**
     * 
     * Carga la lista listaFuenteCuipo
     *
     */
    public void cargarListaFuenteCuipo() {

        listaFuenteCuipo = cargarListaTipoClasificador("009");
    }
    
    /**
     * 
     * Carga la lista listaUnidadEjecutora
     *
     */
    public void cargarListaUnidadEjecutora() {

        listaUnidadEjecutora = cargarListaTipoClasificador("008");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        validarVisibilidadCampos();
        cargarListaAuxiliar();
        cargarListaCentroCosto();
        cargarListaCuentaDebitoBase();
        cargarListaCuentaCreditoBase();
        cargarListaCuentaDebitoIva();
        cargarListaCuentaCreditoIva();
        cargarListaCuentaDebitoRete();
        cargarListaCuentaCreditoRete();
        cargarListaCuentaDebitoDescuento();
        
        cargarListaCuentaDebitoReteIvaAct();
        cargarListaCuentaCreditoReteIvaAct();
        cargarListaCuentaDebitoIvaAntes();
        cargarListaCuentaCreditoIvaAntes();
        
        cargarListaCuentaCreditoDescuento();
        cargarListaCuentaDebitoIca();
        cargarListaCuentaCreditoIca();
        cargarListaCuentaDebitoBaseAv();
        cargarListaCuentaCreditoBaseAv();
        cargarListaCuentaDebitoIvaAv();
        cargarListaCuentaCreditoIvaAv();
        cargarListaCuentaDebitoReteAv();
        cargarListaCuentaCreditoReteAv();
        cargarListaCuentaDebitoDescuentoAv();
        cargarListaCuentaCreditoDescuentoAv();
        cargarListaCuentaDebitoIcaAv();
        cargarListaCuentaCreditoIcaAv();
        cargarListaCuentaDebitoUtilidad();
        cargarListaCuentaCreditoUtilidad();
        cargarListaCuentaDebitoUtilidadAv();
        cargarListaCuentaCreditoUtilidadAv();
        cargarListaCuentaDebitoImpoConsumo();
        cargarListaCuentaCreditoImpoConsumo();
        cargarListaCuentaDebitoPublico(); 
        cargarListaCuentaCreditoPublico();
        cargarListaCuentaDebitoPrivado(); 
        cargarListaCuentaCreditoPrivado();
        cargarListaCuentaRecaudo();
        cargarListaReferencia();
        cargarListaFuenteRecurso();

    }

    /**
     * Metodo ejecutado al cambiar el control CantidadPorDefecto
     * 
     */
    public void cambiarCantidadPorDefecto() {
        // <CODIGO_DESARROLLADO>
    	String cantidadPorDefecto = SysmanFunciones.nvl(registro.getCampos()
                .get(SfconceptosControladorEnum.CANTIDAD_PORDEFECTO
                        .getValue()),
        "").toString();
    	
        if (cantidadPorDefecto.isEmpty() || cantidadPorDefecto.equals("0")) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.CANTIDAD_PORDEFECTO
                                            .getValue(), "1");

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Aplica Descuento
     * 
     */
    public void cambiarAplicaDescuento() {

        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICADESCUENTO
                                        .getValue())) {
            bloquearPorcentaje = false;
        }
        else {
            bloquearPorcentaje = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control PorcentajeDescuento
     * 
     */
    public void cambiarPorcentajeDescuento() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJEDESCUENTO
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEDESCUENTO
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control cambiarPorcentajeReteIva
     * 
     */
    public void cambiarPorcentajeReteIva() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get("PORCENTAJERETEIVA"),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put("PORCENTAJERETEIVA", 0);
        }
    }
    
    /**
     * Metodo ejecutado al cambiar el control AplicaFormula
     * 
     */
    public void cambiarAplicaFormula() {
        if ((boolean) registro.getCampos().get(
                        SfconceptosControladorEnum.APLICAFORMULA.getValue())) {
            aplicaFormula = true;
        }
        else {
            aplicaFormula = false;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Formula
     * 
     */
    public void cambiarFormula() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.FORMULA
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.FORMULA
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Aplica Ica
     * 
     */
    public void cambiarAplicaIca() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAICA.getValue())) {
            aplicaIca = false;
        }
        else {
            aplicaIca = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Porcentaje Ica
     * 
     */
    public void cambiarPorcentajeIca() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJEICA
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEICA
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Aplica Iva
     * 
     */
    public void cambiarAplicaIva() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAIVA.getValue())) {

            aplicaIva = false;
        }
        else {
            aplicaIva = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control AplicaImpoconsumo
     * 
     * 
     */
    public void cambiarAplicaImpoconsumo() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAIMPOCONSUMO
                                        .getValue())) {

            aplicaImpoConsumo = false;
        }
        else {
            aplicaImpoConsumo = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Porcentaje Iva
     * 
     */
    public void cambiarPorcentajeIva() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJEIVA
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEIVA
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Aplica Porcentaje
     * ReteFuente
     * 
     */
    public void cambiarAplicaRetefuente() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICARETEFUENTE
                                        .getValue())) {
            aplicaReteFuente = false;
        }
        else {
            aplicaReteFuente = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Porcentaje ReteFuente
     * 
     */
    public void cambiarPorcentajeRetefuente() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJERETEFUENTE
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJERETEFUENTE
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Porcentaje Utilidad
     * 
     */
    public void cambiarPorcentajeUtilidad() {

        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCETAJE_UTILIDAD
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCETAJE_UTILIDAD
                                            .getValue(), 0);
        }

        try {
            String conceptoUtilidad = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "SF REDONDEAR VALOR DEL CONCEPTO CON UTILIDAD",
                                            modulo,
                                            new Date(), true), "");
            int digitoEn;

            if ("CIENES".equals(conceptoUtilidad)) {
                digitoEn = -2;
            }
            else if ("MILES".equals(conceptoUtilidad)) {
                digitoEn = -3;
            }
            else {
                digitoEn = 2;
            }

            String utilidadConceptos = ejbSysmanUtil.consultarParametro(
                            compania,
                            "SF USAR PORC UTILIDAD PARA CONCEPTOS DE ALMACEN",
                            modulo, new Date(), true);
            if (utilidadConceptos == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3783"));
                return;
            }
            else if ("NO".equals(utilidadConceptos)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3784"));
                return;
            }

            if ("0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.PORCETAJE_UTILIDAD
                                            .getValue())
                            .toString())) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3785"));

            }

            double valorCompra = Double.parseDouble(registro.getCampos()
                            .get("VALOR_COMPRA").toString());
            int pocentajeUtilidad = Integer.parseInt(registro.getCampos()
                            .get(SfconceptosControladorEnum.PORCETAJE_UTILIDAD
                                            .getValue())
                            .toString());

            double valorBase = SysmanFunciones.redondear(
                            valorCompra + (valorCompra * pocentajeUtilidad),
                            digitoEn);

            registro.getCampos().put("VALOR_BASE", valorBase);

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }
    
    /**
     * Metodo ejecutado al cambiar el control Aplica AutoRenta
     * 
     */
    public void cambiarAplicaAutoRenta() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAAUTORENTA
                                        .getValue())) {
            aplicaAutoRenta = false;
        }
        else {
        	aplicaAutoRenta = true;
        }
    }
    /**
     * Metodo ejecutado al cambiar el control cambiarAplicaReteIva
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarAplicaReteIva() {
    	//<CODIGO_DESARROLLADO>
    	 if ((boolean) registro.getCampos()
		                 .get("APLICARETEIVA")) {
    		 bloquearPorcentajeReteIva = false;
		 }
		 else {
			 bloquearPorcentajeReteIva = true;
		 }
    	 
    	//</CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Aplica AutoICA
     * 
     */
    public void cambiarAplicaAutoICA() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAAUTOICA
                                        .getValue())) {
            aplicaAutoICA = false;
        }
        else {
        	aplicaAutoICA = true;
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Porcentaje AutoRenta
     * 
     */
    public void cambiarPorcentajeAutoRenta() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJEAUTORENTA
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEAUTORENTA
                                            .getValue(), 0);
        }
    }
    
    /**
     * Metodo ejecutado al cambiar el control Porcentaje AutoICA
     * 
     */
    public void cambiarPorcentajeAutoICA() {
        if (SysmanFunciones.nvl(registro.getCampos()
                        .get(SfconceptosControladorEnum.PORCENTAJEAUTOICA
                                        .getValue()),
                        "").toString().isEmpty()) {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEAUTOICA
                                            .getValue(), 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Valor compra
     * 
     */
    public void cambiarValorCompra() {
        // <CODIGO_DESARROLLADO>
        cambiarPorcentajeUtilidad();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedBase
     * 
     * 
     */
    public void cambiarFactorRedBase() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        SfconceptosControladorEnum.FACTOR_RED_BASE.getValue())
            || "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.FACTOR_RED_BASE
                                            .getValue()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4098")
                            .replace("s$variable$s", "El valor Base "));
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_BASE
                            .getValue(), 1);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedIva
     * 
     * 
     */
    public void cambiarFactorRedIva() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        SfconceptosControladorEnum.FACTOR_RED_IVA.getValue())
            || "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.FACTOR_RED_IVA
                                            .getValue()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4098")
                            .replace("s$variable$s", "El IVA "));
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_IVA
                            .getValue(), 1);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedBaseTotal
     * 
     * 
     */
    public void cambiarFactorRedBaseTotal() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        SfconceptosControladorEnum.FACTOR_RED_BASETOTAL
                                        .getValue())
            || "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.FACTOR_RED_BASETOTAL
                                            .getValue()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4098")
                            .replace("s$variable$s",
                                            "El valor de la base total "));
            registro.getCampos()
                            .put(SfconceptosControladorEnum.FACTOR_RED_BASETOTAL
                                            .getValue(), 1);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedIca
     * 
     * 
     */
    public void cambiarFactorRedIca() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        SfconceptosControladorEnum.FACTOR_RED_ICA.getValue())
            || "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.FACTOR_RED_ICA
                                            .getValue()))) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB4098").replace(
                                            "s$variable$s",
                                            "El valor del Impuesto Industria y Comercio "));
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_ICA
                            .getValue(), 1);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedDescuento
     * 
     * 
     */
    public void cambiarFactorRedDescuento() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "FACTOR_RED_DESCUENTO")
            || "0".equals(registro.getCampos().get("FACTOR_RED_DESCUENTO"))) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB4098").replace(
                                            "s$variable$s", "El descuento "));
            registro.getCampos().put("FACTOR_RED_DESCUENTO", 1);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FactorRedRete
     * 
     * 
     */
    public void cambiarFactorRedRete() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "FACTOR_RED_RETE")
            || "0".equals(registro.getCampos().get("FACTOR_RED_RETE"))) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB4098").replace(
                                            "s$variable$s",
                                            idioma.getString("TB_TB4099")));
            registro.getCampos().put("FACTOR_RED_RETE", 1);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ConcDep
     * 
     */
    public void retornarFormularioConcDep(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() != null) {
            cargarRegistro(css, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        SfconceptosControladorEnum.NOMBREAUXILIAR.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOBASE",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOBASE",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoPublico
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaDebitoPublico(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTADEBITOPUBLICO", registroAux.getCampos().get("ID"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditoPublico
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditoPublico(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTACREDITOPUBLICO", registroAux.getCampos().get("ID"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaDebitoPrivado
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaDebitoPrivado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTADEBITOPRIVADO", registroAux.getCampos().get("ID"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditoPrivado
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditoPrivado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTACREDITOPRIVADO", registroAux.getCampos().get("ID"));
	}
    
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(
                        SfconceptosControladorEnum.NOMBRECENTRO.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIva
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOIVA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIva
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOIVA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoRete
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoRete(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITORETE",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoRete
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoRete(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITORETE",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoDescuento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoDescuento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITODESCUENTO",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * seleccionarFilaCuentaDebitoReteIvaAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoReteIvaAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBITORETEIVAACTUAL",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * seleccionarFilaCuentaCreditoReteIvaAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoReteIvaAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDITORETEIVAACTUAL",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * seleccionarFilaCuentaDebitoIvaAntes
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIvaAntes(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBITORETEIVAANTES",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * seleccionarFilaCuentaCreditoIvaAntes
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIvaAntes(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDITORETEIVAANTES",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoDescuento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoDescuento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITODESCUENTO",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIca
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOICA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIca
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOICA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoBaseAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoBaseAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOBASE_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoBaseAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoBaseAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOBASE_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIvaAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIvaAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOIVA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIvaAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIvaAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOIVA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoReteAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoReteAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITORETE_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoReteAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoReteAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITORETE_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoDescuentoAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoDescuentoAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITODESCUENTO_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoDescuentoAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoDescuentoAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITODESCUENTO_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoIcaAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoIcaAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOICA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoIcaAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoIcaAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOICA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUnidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUnidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.UNIDAD.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.UNIDAD.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoUtilidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoUtilidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOUTILIDAD",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoUtilidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoUtilidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOUTILIDAD",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoUtilidadAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoUtilidadAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOUTILIDAD_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoRentaVAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoAutoRentaVAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOAUTORENTA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoRentaVAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoAutoRentaVAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOAUTORENTA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoAutoRentaVAnt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoAutoRentaVAnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOAUTORENTA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoRentaVAnt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoAutoRentaVAnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOAUTORENTA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoICAVAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoAutoICAVAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOAUTOICA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoICAVAct
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoAutoICAVAct(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOAUTOICA",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDebitoAutoICAVAnt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDebitoAutoICAVAnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOAUTOICA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCreditoAutoRentaVAnt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCreditoAutoICAVAnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOAUTOICA_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }


    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoUtilidadAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoUtilidadAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOUTILIDAD_AV",
                        registroAux.getCampos()
                                        .get(SfconceptosControladorEnum.ID
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaRecaudo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaRecaudo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        SfconceptosControladorEnum.CUENTA_RECAUDO.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put("NOMBREREFERENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put("NOMBREFUENTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoImpoConsumo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoImpoConsumo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBIMPOCONSUMO",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoImpoConsumo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoImpoConsumo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDIMPOCONSUMO",
                        registroAux.getCampos().get("CODIGO"));
    }
    
    public void seleccionarFilaCCPET(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_CCPET",
                        registroAux.getCampos().get("CODIGO"));
        
        registro.getCampos().put("NOMBRECCPET",
                registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()));
        
    }
    
    public void seleccionarFilaFuenteCuipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_CUIPO",
                        registroAux.getCampos().get("CODIGO"));
        
        registro.getCampos().put("NOMBREFUENTECUIPO",
                registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()));
    }
    
    public void seleccionarFilaUnidadEjecutora(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOUNIDADEJE",
                        registroAux.getCampos().get("CODIGO"));
        
        registro.getCampos().put("NOMBREUNIDADEJE",
                registroAux.getCampos().get(
                                GeneralParameterEnum.NOMBRE.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Configurar conceptos
     * dependientes en la vista
     *
     */
    public void oprimirConcDep() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "anio", "codigo" };
        Object[] valores = { registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.CODIGO
                                                             .getName())
                                             .toString() };
        SessionUtil.cargarModalDatosFlash(Integer
                        .toString(GeneralCodigoFormaEnum.CONCEPTOS_DEPENDIENTES_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton DerConexion en la vista
     *
     */
    public void oprimirDerConexion() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { SfconceptosControladorEnum.RIDCONCEPTO.getValue() };
        Object[] valores = { registro.getLlave() };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_CONCEPTOSADICALMACEN_CONTROLADOR
                                                        .getCodigo()),
                        campos,
                        valores, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CtaTercero en la vista
     *
     */
    public void oprimirCtaTercero() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { SfconceptosControladorEnum.RIDCONCEPTO.getValue() };
        Object[] valores = { registro.getLlave() };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_CTAAUXTERCERO_CONTROLADOR
                                                        .getCodigo()),
                        campos,
                        valores, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cree en la vista
     *
     */
    public void oprimirCree() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { SfconceptosControladorEnum.RIDCONCEPTO.getValue() };
        Object[] valores = { registro.getLlave() };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_CREE_CONFIGURACION_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Met�do que valida la visibilidad de los campos de "Cuentas para
     * manejo de la utilidad", se llama en el cargarRegistro y
     * cambiarAno
     */
    public void validarVisibilidadCampos() {

        try {
            manejaUtilidad = ejbFactGeneralCero.validarManejaInventario(
                            compania,
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            tipoCobro);
        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * M�todo que valida visibilidad de campos segun valor de
     * par�metros se llama en el cargarRegistro
     */
    public void validarCamposParametros() {
        try {       	
                    
        	 String cuentasTipoEntidad = SysmanFunciones
                     .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                     "SF MANEJA TARIFA POR TERCERO",
                                     modulo,
                                     new Date(), true), "NO");

             tipoEntidad = "SI".equals(cuentasTipoEntidad) ? true : false;
             
            String conceptoPrincipal = ejbSysmanUtil.consultarParametro(
                            compania, "SF CONCEPTO PRINCIPAL PRODESARROLLO",
                            modulo, new Date(), true);
            nivelVisible = !SysmanFunciones
                            .validarVariableVacio(conceptoPrincipal) ? true
                                : false;

            String calcularDerechos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "SF CALCULAR DERECHOS DE CONEXION",
                                            modulo,
                                            new Date(), true), "NO");

            derechoConexion = "SI".equals(calcularDerechos) ? true : false;

            String agruparNombre = ejbSysmanUtil.consultarParametro(compania,
                            "SF AGRUPAR POR NOMBRE DE CONCEPTO", modulo,
                            new Date(), true);

            agruparVisible = "SI".equals(agruparNombre);

            String manejaCalculo = ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA CALCULO CREE", modulo, new Date(), true);

            calculoCree = "SI".equals(manejaCalculo);

            String manejaDpendendiente = ejbSysmanUtil.consultarParametro(
                            compania,
                            "SF MANEJA CONCEPTOS DEPENDIENTES", modulo,
                            new Date(), true);

            conceptoDependiente = "SI".equals(manejaDpendendiente);
            String cuentaTercero = ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA CONFIGURACION DE CTA POR TERCERO",
                            modulo, new Date(), true);

            terceroVisible = "SI".equals(cuentaTercero);
            
			visibleSigec = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"SF MANEJA FACTURACION DE ESTAMPILLA ELECTRONICA", SessionUtil.getModulo(), new Date(), true),
					"NO")) ? true : false;
			
			permiteAjusteDecimales = "SI".equals(SysmanFunciones
 					.nvl(ejbSysmanUtil.consultarParametro(compania, "SF PERMITE AJUSTE DE DECIMALES EN FACTURACION GENERAL",
 							SessionUtil.getModulo(), new Date(), true), "NO"));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SfconceptosControladorUrlEnum.URL44206
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            String obligaGravable = ejbSysmanUtil.consultarParametro(compania,
                            "SF OBLIGA A BASE GRAVABLE", modulo, new Date(),
                            true);
            String baseGravable = ejbSysmanUtil.consultarParametro(compania,
                            "SF BASE GRAVABLE CALCULADA", modulo, new Date(),
                            true);

            if ("SI".equals(obligaGravable) && "SI".equals(baseGravable)) {
                baseGravableVisible = true;
            }
            else {
                baseGravableVisible = false;
            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que agrega valores por defecto a los campos, se llama en
     * el cargarRegistro
     */
    public void cargarValoresDefecto() {
        try {
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEDESCUENTO
                                            .getValue(), 0);
            
            if (SysmanFunciones.nvl(registro.getCampos()
                    .get("PORCENTAJERETEIVA"),
                    "").toString().isEmpty()) {
                registro.getCampos().put("PORCENTAJERETEIVA", 0);
            }
            
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEIVA
                                            .getValue(), 0);
            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJERETEFUENTE
                                            .getValue(), 0);

            registro.getCampos()
                            .put(SfconceptosControladorEnum.PORCENTAJEICA
                                            .getValue(), 0);

            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
            registro.getCampos().put("VALOR_BASE", 1);
            registro.getCampos().put("CLASE_CONCEPTO", "C");
            registro.getCampos().put("CENTRO_COSTO",
                            SysmanConstantes.CONS_CENTRO);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            SysmanConstantes.CONS_CENTRO);

            Registro rsNomCentro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfconceptosControladorUrlEnum.URL2227
                                                                            .getValue())
                                            .getUrl(), param));

            registro.getCampos().put(
                            SfconceptosControladorEnum.NOMBRECENTRO
                                            .getValue(),
                            rsNomCentro.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));

            registro.getCampos().put("AUXILIAR",
                            SysmanConstantes.CONS_AUXILIAR);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            SysmanConstantes.CONS_AUXILIAR);

            Registro rsNomAuxiliar = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfconceptosControladorUrlEnum.URL2251
                                                                            .getValue())
                                            .getUrl(), param));

            registro.getCampos().put(
                            SfconceptosControladorEnum.NOMBREAUXILIAR
                                            .getValue(),
                            rsNomAuxiliar.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));
            registro.getCampos()
                            .put(SfconceptosControladorEnum.CANTIDAD_PORDEFECTO
                                            .getValue(), "1");
            registro.getCampos().put("PORCETAJE_UTILIDAD", 0);
            registro.getCampos().put("VALOR_COMPRA", 0);
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_BASE
                            .getValue(), 1);
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_ICA
                            .getValue(), 1);
            registro.getCampos().put(SfconceptosControladorEnum.FACTOR_RED_IVA
                            .getValue(), 1);
            registro.getCampos()
                            .put(SfconceptosControladorEnum.FACTOR_RED_BASETOTAL
                                            .getValue(), 1);
            registro.getCampos().put("FACTOR_RED_DESCUENTO", 1);
            registro.getCampos().put("FACTOR_RED_RETE", 1);
            registro.getCampos().put(
                            SfconceptosControladorEnum.APLICAFORMULA.getValue(),
                            false);
            registro.getCampos().put(
                            SfconceptosControladorEnum.APLICAIVA.getValue(),
                            false);
            registro.getCampos().put(
                            SfconceptosControladorEnum.APLICARETEFUENTE
                                            .getValue(),
                            false);
            registro.getCampos().put(SfconceptosControladorEnum.APLICADESCUENTO
                            .getValue(), false);
            registro.getCampos().put(
                            SfconceptosControladorEnum.APLICAICA.getValue(),
                            false);
            registro.getCampos().put(
                            SfconceptosControladorEnum.APLICAIMPOCONSUMO
                                            .getValue(),
                            false);
            registro.getCampos().put("ESTADO", true);
            registro.getCampos().put("FORMULA", "0");
            registro.getCampos().put("TIPODECONCEPTO", "C");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * Metodo que valida campos antes de insercion y actualizacion
     * 
     * @return verdadero o false
     */
    public boolean validarCampoActualizar() {

        if (!validarCuentas()) {
            return false;
        }

        if (!validarAplicaConceptos()) {
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida las cuentas
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentas() {
       if (manejaUtilidad && (SysmanFunciones
                        .validarVariableVacio(registro.getCampos()
                                        .get("CUENTADEBITOUTILIDAD").toString())
            || SysmanFunciones.validarVariableVacio(registro.getCampos()
                            .get("CUENTACREDITOUTILIDAD").toString()))
            && "C".equals(registro.getCampos().get("TIPODECONCEPTO")
                            .toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3789"));
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida los checks del formulario
     * 
     * @return verdadero o falso
     */
    public boolean validarAplicaConceptos() {

        if ((boolean) registro.getCampos().get(
                        SfconceptosControladorEnum.APLICAFORMULA.getValue())
            && SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(registro
                            .getCampos()
                            .get(SfconceptosControladorEnum.DIGITOS_REDONDEO
                                            .getValue()),
                            "")
                            .toString())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3790"));
            return false;
        }

        if (!validarAplicaIva() || !validarAplicaReteFuente()
            || !validarAplicaDescuento()) {
            return false;
        }

        if (!validarAplicaIca()) {
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida los campos de Aplica Iva
     * 
     * @return verdadero o falso
     */
    public boolean validarAplicaIva() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAIVA.getValue())
            && (SysmanFunciones
                            .validarVariableVacio(registro.getCampos()
                                            .get("CUENTADEBITOIVA").toString())
                || SysmanFunciones.validarVariableVacio(registro.getCampos()
                                .get("CUENTACREDITOIVA").toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3791"));
            return false;
        }

        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAIVA.getValue())
            && "0".equals(
                            registro.getCampos()
                                            .get(SfconceptosControladorEnum.PORCENTAJEIVA
                                                            .getValue())
                                            .toString())) {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3792"));
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida los campos de Aplica ReteFuente
     * 
     * @return verdadero o falso
     */
    public boolean validarAplicaReteFuente() {
        if ((boolean) registro.getCampos().get(
                        SfconceptosControladorEnum.APLICARETEFUENTE.getValue())
            && (SysmanFunciones
                            .validarVariableVacio(registro.getCampos()
                                            .get("CUENTADEBITORETE").toString())
                || SysmanFunciones.validarVariableVacio(registro.getCampos()
                                .get("CUENTACREDITORETE").toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3793"));
            return false;
        }
        if ((boolean) registro.getCampos().get(
                        SfconceptosControladorEnum.APLICARETEFUENTE.getValue())
            && "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.PORCENTAJERETEFUENTE
                                            .getValue())
                            .toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3794"));
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida los campos de Aplica Descuento
     * 
     * @return verdadero o falso
     */
    public boolean validarAplicaDescuento() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICADESCUENTO
                                        .getValue())
            && (SysmanFunciones
                            .validarVariableVacio(registro.getCampos()
                                            .get("CUENTADEBITODESCUENTO")
                                            .toString())
                || SysmanFunciones.validarVariableVacio(registro.getCampos()
                                .get("CUENTACREDITODESCUENTO").toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3795"));
            return false;

        }

        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICADESCUENTO
                                        .getValue())
            && "0".equals(registro.getCampos()
                            .get(SfconceptosControladorEnum.PORCENTAJEDESCUENTO
                                            .getValue())
                            .toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3796"));
            return false;
        }
        
        if ((boolean) registro.getCampos()
		                .get("APLICARETEIVA")
		    && "0".equals(registro.getCampos()
		                    .get("PORCENTAJERETEIVA")
		                    .toString())) {
		    JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4451"));
		    return false;
		}
        
        
        return true;
    }

    /**
     * Metodo que valida los campos de Aplica Iva
     * 
     * @return verdadero o falso
     */
    public boolean validarAplicaIca() {
        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAICA.getValue())
            && (SysmanFunciones
                            .validarVariableVacio(registro.getCampos()
                                            .get("CUENTADEBITOICA")
                                            .toString())
                || SysmanFunciones.validarVariableVacio(registro.getCampos()
                                .get("CUENTACREDITOICA").toString()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3797"));
            return false;

        }

        if ((boolean) registro.getCampos()
                        .get(SfconceptosControladorEnum.APLICAICA.getValue())
            && "0".equals(
                            registro.getCampos()
                                            .get(SfconceptosControladorEnum.PORCENTAJEICA
                                                            .getValue())
                                            .toString())) {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3798"));
            return false;
        }

        if (!validarParametroConcepto()) {
            return false;
        }
        return true;
    }

    /**
     * Metodo que valida el parametro SF MANEJA CONCEPTO CON CUENTA DE
     * RECAUDO
     * 
     * @return
     */
    public boolean validarParametroConcepto() {
        try {
            String manejaConcepto = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "SF MANEJA CONCEPTO CON CUENTA DE RECAUDO",
                                            modulo, new Date(), true), "NO");

            if ("SI".equals(manejaConcepto)
                && SysmanFunciones.validarVariableVacio(registro.getCampos()
                                .get(SfconceptosControladorEnum.CUENTA_RECAUDO
                                                .getValue())
                                .toString())) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3799"));
                return false;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return true;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SfconceptosControladorUrlEnum.URL31616
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(SfconceptosControladorEnum.TIPOCOBRO.getValue(),
                            tipoCobro);
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
            
            String manejaRecaudo = ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA CONCEPTO CON CUENTA DE RECAUDO", modulo,
                            new Date(), true);

            if ("SI".equals(manejaRecaudo)) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                Registro regCuentaRecuado = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SfconceptosControladorUrlEnum.URL19055
                                                                                .getValue())
                                                .getUrl(), param));

                if (!"0".equals(regCuentaRecuado.getCampos().get("CANT")
                                .toString())) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3773"));
                }

            }
            
            String manejaInterazImp = ejbSysmanUtil.consultarParametro(compania,
			                    "MANEJA INTERFAZ EXTERNA DE IMPUESTOS", modulo,
			                    new Date(), true);
            if("SI".equals(manejaInterazImp))
            {
            	nombreIndicador = "No Causar:";
            }
            else
            {
            	nombreIndicador = "Descuento:";
            }
            
            tiposConceptos = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
                    "TIPOS CONCEPTOS ARRENDAMIENTOS", modulo,
                    new Date(), true));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((ridConcepto != null) && !ridConcepto.isEmpty()) {
            cargarRegistro(ridConcepto, ACCION_MODIFICAR);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        manejaUtilidad = false;
        if (ACCION_INSERTAR.equals(accion)) {
            cargarValoresDefecto();

            bloquearAno = true;
            configurar = true;
        }
        else if (ACCION_MODIFICAR.equals(accion)) {
            validarVisibilidadCampos();
            bloquearAno = true;
            configurar = false;
        }

        validarCamposParametros();
        cambiarAplicaFormula();
        cambiarCantidadPorDefecto();
        cambiarAplicaIva();
        cambiarAplicaRetefuente();
        cambiarAplicaDescuento();
        cambiarAplicaReteIva();
        cambiarAplicaIca();
        cambiarAplicaImpoconsumo();
        cambiarAno();
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        SfconceptosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        registro.getCampos().put(
                        GeneralParameterEnum.ANO.getName(),
                        anio);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        if (!validarCampoActualizar()) {
            return false;
        }

        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos().remove(
                            SfconceptosControladorEnum.TIPOCOBRO.getValue());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            SfconceptosControladorEnum.DIGITOS_REDONDEO
                                            .getValue())) {
                registro.getCampos()
                                .put(SfconceptosControladorEnum.DIGITOS_REDONDEO
                                                .getValue(), 0);
            }

        }

        registro.getCampos().remove(
                        SfconceptosControladorEnum.NOMBRECENTRO.getValue());
        registro.getCampos().remove(
                        SfconceptosControladorEnum.NOMBREAUXILIAR.getValue());
        registro.getCampos().remove("NOMBREREFERENCIA");
        registro.getCampos().remove("NOMBREFUENTE");
        registro.getCampos().remove("NOMBRECCPET");
        registro.getCampos().remove("NOMBREFUENTECUIPO");
        registro.getCampos().remove("NOMBREUNIDADEJE");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable manejaUtilidad
     * 
     * @return manejaUtilidad
     */
    public boolean isManejaUtilidad() {
        return manejaUtilidad;
    }

    /**
     * Asigna la variable facturaInicial
     * 
     * @param manejaUtilidad
     * Variable a asignar en manejaUtilidad
     */
    public void setManejaUtilidad(boolean manejaUtilidad) {
        this.manejaUtilidad = manejaUtilidad;
    }

    /**
     * Retorna la variable nivelVisible
     * 
     * @return nivelVisible
     */
    public boolean isNivelVisible() {
        return nivelVisible;
    }

    /**
     * Asigna la variable nivelVisible
     * 
     * @param nivelVisible
     * Variable a asignar en nivelVisible
     */
    public void setNivelVisible(boolean nivelVisible) {
        this.nivelVisible = nivelVisible;
    }

    /**
     * Retorna la variable derechoConexion
     * 
     * @return derechoConexion
     */
    public boolean isDerechoConexion() {
        return derechoConexion;
    }

    /**
     * Asigna la variable derechoConexion
     * 
     * @param derechoConexion
     * Variable a asignar en derechoConexion
     */
    public void setDerechoConexion(boolean derechoConexion) {
        this.derechoConexion = derechoConexion;
    }

    /**
     * Retorna la variable agruparVisible
     * 
     * @return agruparVisible
     */
    public boolean isAgruparVisible() {
        return agruparVisible;
    }

    /**
     * Asigna la variable agruparVisible
     * 
     * @param agruparVisible
     * Variable a asignar en agruparVisible
     */
    public void setAgruparVisible(boolean agruparVisible) {
        this.agruparVisible = agruparVisible;
    }

    /**
     * Retorna la variable calculoCree
     * 
     * @return calculoCree
     */
    public boolean isCalculoCree() {
        return calculoCree;
    }

    /**
     * Asigna la variable calculoCree
     * 
     * @param calculoCree
     * Variable a asignar en calculoCree
     */
    public void setCalculoCree(boolean calculoCree) {
        this.calculoCree = calculoCree;
    }

    /**
     * Retorna la variable terceroVisible
     * 
     * @return terceroVisible
     */
    public boolean isTerceroVisible() {
        return terceroVisible;
    }

    /**
     * Asigna la variable terceroVisible
     * 
     * @param terceroVisible
     * Variable a asignar en terceroVisible
     */
    public void setTerceroVisible(boolean terceroVisible) {
        this.terceroVisible = terceroVisible;
    }

    /**
     * Retorna la variable bloquearPorcentaje
     * 
     * @return bloquearPorcentaje
     */

    public boolean isBloquearPorcentaje() {
        return bloquearPorcentaje;
    }

    /**
     * Asigna la variable bloquearPorcentaje
     * 
     * @param bloquearPorcentaje
     * Variable a asignar en bloquearPorcentaje
     */
    public void setBloquearPorcentaje(boolean bloquearPorcentaje) {
        this.bloquearPorcentaje = bloquearPorcentaje;
    }

    /**
     * Retorna la variable aplicaFormula
     * 
     * @return aplicaFormula
     */

    public boolean isAplicaFormula() {
        return aplicaFormula;
    }

    /**
     * Asigna la variable aplicaFormula
     * 
     * @param aplicaFormula
     * Variable a asignar en aplicaFormula
     */
    public void setAplicaFormula(boolean aplicaFormula) {
        this.aplicaFormula = aplicaFormula;
    }

    /**
     * Retorna la variable aplicaIca
     * 
     * @return aplicaIca
     */
    public boolean isAplicaIca() {
        return aplicaIca;
    }

    /**
     * Asigna la variable aplicaIca
     * 
     * @param aplicaIca
     * Variable a asignar en aplicaIca
     */
    public void setAplicaIca(boolean aplicaIca) {
        this.aplicaIca = aplicaIca;
    }

    /**
     * Retorna la variable aplicaIva
     * 
     * @return aplicaIva
     */
    public boolean isAplicaIva() {
        return aplicaIva;
    }

    /**
     * Asigna la variable aplicaIva
     * 
     * @param aplicaIva
     * Variable a asignar en aplicaIva
     */
    public void setAplicaIva(boolean aplicaIva) {
        this.aplicaIva = aplicaIva;
    }

    public boolean isAplicaImpoConsumo() {
        return aplicaImpoConsumo;
    }

    public void setAplicaImpoConsumo(boolean aplicaImpoConsumo) {
        this.aplicaImpoConsumo = aplicaImpoConsumo;
    }

    /**
     * Retorna la variable aplicaReteFuente
     * 
     * @return aplicaReteFuente
     */
    public boolean isAplicaReteFuente() {
        return aplicaReteFuente;
    }

    /**
     * Asigna la variable aplicaReteFuente
     * 
     * @param aplicaReteFuente
     * Variable a asignar en aplicaReteFuente
     */
    public void setAplicaReteFuente(boolean aplicaReteFuente) {
        this.aplicaReteFuente = aplicaReteFuente;
    }

    /**
     * Retorna la variable baseGravableVisible
     * 
     * @return baseGravableVisible
     */
    public boolean isBaseGravableVisible() {
        return baseGravableVisible;
    }

    /**
     * Asigna la variable baseGravableVisible
     * 
     * @param baseGravableVisible
     * Variable a asignar en baseGravableVisible
     */
    public void setBaseGravableVisible(boolean baseGravableVisible) {
        this.baseGravableVisible = baseGravableVisible;
    }

    /**
     * Retorna la variable bloquearAno
     * 
     * @return bloquearAno
     */
    public boolean isBloquearAno() {
        return bloquearAno;
    }

    /**
     * Asigna la variable bloquearAno
     * 
     * @param bloquearAno
     * Variable a asignar en bloquearAno
     */
    public void setBloquearAno(boolean bloquearAno) {
        this.bloquearAno = bloquearAno;
    }

    /**
     * Retorna la variable configurar
     * 
     * @return configurar
     */
    public boolean isConfigurar() {
        return configurar;
    }

    /**
     * Asigna la variable configurar
     * 
     * @param configurar
     * Variable a asignar en configurar
     */
    public void setConfigurar(boolean configurar) {
        this.configurar = configurar;
    }

    /**
     * Retorna la variable conceptoDependiente
     * 
     * @return conceptoDependiente
     */
    public boolean isConceptoDependiente() {
        return conceptoDependiente;
    }

    /**
     * Asigna la variable conceptoDependiente
     * 
     * @param conceptoDependiente
     * Variable a asignar en conceptoDependiente
     */
    public void setConceptoDependiente(boolean conceptoDependiente) {
        this.conceptoDependiente = conceptoDependiente;
    }

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaClaseConcepto
     * 
     * @return listaClaseConcepto
     */
    public List<Registro> getListaClaseConcepto() {
        return listaClaseConcepto;
    }

    /**
     * Asigna la lista listaClaseConcepto
     * 
     * @param listaClaseConcepto
     * Variable a asignar en listaClaseConcepto
     */
    public void setListaClaseConcepto(List<Registro> listaClaseConcepto) {
        this.listaClaseConcepto = listaClaseConcepto;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaAuxiliar
     * 
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     * 
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * Retorna la lista listaCuentaDebitoBase
     * 
     * @return listaCuentaDebitoBase
     */
    public RegistroDataModelImpl getListaCuentaDebitoBase() {
        return listaCuentaDebitoBase;
    }

    /**
     * Asigna la lista listaCuentaDebitoBase
     * 
     * @param listaCuentaDebitoBase
     * Variable a asignar en listaCuentaDebitoBase
     */
    public void setListaCuentaDebitoBase(
        RegistroDataModelImpl listaCuentaDebitoBase) {
        this.listaCuentaDebitoBase = listaCuentaDebitoBase;
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
     * Retorna la lista listaCuentaCreditoBase
     * 
     * @return listaCuentaCreditoBase
     */
    public RegistroDataModelImpl getListaCuentaCreditoBase() {
        return listaCuentaCreditoBase;
    }

    /**
     * Asigna la lista listaCuentaCreditoBase
     * 
     * @param listaCuentaCreditoBase
     * Variable a asignar en listaCuentaCreditoBase
     */
    public void setListaCuentaCreditoBase(
        RegistroDataModelImpl listaCuentaCreditoBase) {
        this.listaCuentaCreditoBase = listaCuentaCreditoBase;
    }

    /**
     * Retorna la lista listaCuentaDebitoIva
     * 
     * @return listaCuentaDebitoIva
     */
    public RegistroDataModelImpl getListaCuentaDebitoIva() {
        return listaCuentaDebitoIva;
    }

    /**
     * Asigna la lista listaCuentaDebitoIva
     * 
     * @param listaCuentaDebitoIva
     * Variable a asignar en listaCuentaDebitoIva
     */
    public void setListaCuentaDebitoIva(
        RegistroDataModelImpl listaCuentaDebitoIva) {
        this.listaCuentaDebitoIva = listaCuentaDebitoIva;
    }

    /**
     * Retorna la lista listaCuentaCreditoIva
     * 
     * @return listaCuentaCreditoIva
     */
    public RegistroDataModelImpl getListaCuentaCreditoIva() {
        return listaCuentaCreditoIva;
    }

    /**
     * Asigna la lista listaCuentaCreditoIva
     * 
     * @param listaCuentaCreditoIva
     * Variable a asignar en listaCuentaCreditoIva
     */
    public void setListaCuentaCreditoIva(
        RegistroDataModelImpl listaCuentaCreditoIva) {
        this.listaCuentaCreditoIva = listaCuentaCreditoIva;
    }

    /**
     * Retorna la lista listaCuentaDebitoRete
     * 
     * @return listaCuentaDebitoRete
     */
    public RegistroDataModelImpl getListaCuentaDebitoRete() {
        return listaCuentaDebitoRete;
    }

    /**
     * Asigna la lista listaCuentaDebitoRete
     * 
     * @param listaCuentaDebitoRete
     * Variable a asignar en listaCuentaDebitoRete
     */
    public void setListaCuentaDebitoRete(
        RegistroDataModelImpl listaCuentaDebitoRete) {
        this.listaCuentaDebitoRete = listaCuentaDebitoRete;
    }

    /**
     * Retorna la lista listaCuentaCreditoRete
     * 
     * @return listaCuentaCreditoRete
     */
    public RegistroDataModelImpl getListaCuentaCreditoRete() {
        return listaCuentaCreditoRete;
    }

    /**
     * Asigna la lista listaCuentaCreditoRete
     * 
     * @param listaCuentaCreditoRete
     * Variable a asignar en listaCuentaCreditoRete
     */
    public void setListaCuentaCreditoRete(
        RegistroDataModelImpl listaCuentaCreditoRete) {
        this.listaCuentaCreditoRete = listaCuentaCreditoRete;
    }

    /**
     * Retorna la lista listaCuentaDebitoDescuento
     * 
     * @return listaCuentaDebitoDescuento
     */
    public RegistroDataModelImpl getListaCuentaDebitoDescuento() {
        return listaCuentaDebitoDescuento;
    }

    /**
     * Asigna la lista listaCuentaDebitoDescuento
     * 
     * @param listaCuentaDebitoDescuento
     * Variable a asignar en listaCuentaDebitoDescuento
     */
    public void setListaCuentaDebitoDescuento(
        RegistroDataModelImpl listaCuentaDebitoDescuento) {
        this.listaCuentaDebitoDescuento = listaCuentaDebitoDescuento;
    }

    /**
     * Retorna la lista listaCuentaCreditoDescuento
     * 
     * @return listaCuentaCreditoDescuento
     */
    public RegistroDataModelImpl getListaCuentaCreditoDescuento() {
        return listaCuentaCreditoDescuento;
    }

    /**
     * Asigna la lista listaCuentaCreditoDescuento
     * 
     * @param listaCuentaCreditoDescuento
     * Variable a asignar en listaCuentaCreditoDescuento
     */
    public void setListaCuentaCreditoDescuento(
        RegistroDataModelImpl listaCuentaCreditoDescuento) {
        this.listaCuentaCreditoDescuento = listaCuentaCreditoDescuento;
    }

    /**
     * Retorna la lista listaCuentaDebitoIca
     * 
     * @return listaCuentaDebitoIca
     */
    public RegistroDataModelImpl getListaCuentaDebitoIca() {
        return listaCuentaDebitoIca;
    }

    /**
     * Asigna la lista listaCuentaDebitoIca
     * 
     * @param listaCuentaDebitoIca
     * Variable a asignar en listaCuentaDebitoIca
     */
    public void setListaCuentaDebitoIca(
        RegistroDataModelImpl listaCuentaDebitoIca) {
        this.listaCuentaDebitoIca = listaCuentaDebitoIca;
    }

    /**
     * Retorna la lista listaCuentaCreditoIca
     * 
     * @return listaCuentaCreditoIca
     */
    public RegistroDataModelImpl getListaCuentaCreditoIca() {
        return listaCuentaCreditoIca;
    }

    /**
     * Asigna la lista listaCuentaCreditoIca
     * 
     * @param listaCuentaCreditoIca
     * Variable a asignar en listaCuentaCreditoIca
     */
    public void setListaCuentaCreditoIca(
        RegistroDataModelImpl listaCuentaCreditoIca) {
        this.listaCuentaCreditoIca = listaCuentaCreditoIca;
    }

    /**
     * Retorna la lista listaCuentaDebitoBaseAv
     * 
     * @return listaCuentaDebitoBaseAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoBaseAv() {
        return listaCuentaDebitoBaseAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoBaseAv
     * 
     * @param listaCuentaDebitoBaseAv
     * Variable a asignar en listaCuentaDebitoBaseAv
     */
    public void setListaCuentaDebitoBaseAv(
        RegistroDataModelImpl listaCuentaDebitoBaseAv) {
        this.listaCuentaDebitoBaseAv = listaCuentaDebitoBaseAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoBaseAv
     * 
     * @return listaCuentaCreditoBaseAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoBaseAv() {
        return listaCuentaCreditoBaseAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoBaseAv
     * 
     * @param listaCuentaCreditoBaseAv
     * Variable a asignar en listaCuentaCreditoBaseAv
     */
    public void setListaCuentaCreditoBaseAv(
        RegistroDataModelImpl listaCuentaCreditoBaseAv) {
        this.listaCuentaCreditoBaseAv = listaCuentaCreditoBaseAv;
    }

    /**
     * Retorna la lista listaCuentaDebitoIvaAv
     * 
     * @return listaCuentaDebitoIvaAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoIvaAv() {
        return listaCuentaDebitoIvaAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoIvaAv
     * 
     * @param listaCuentaDebitoIvaAv
     * Variable a asignar en listaCuentaDebitoIvaAv
     */
    public void setListaCuentaDebitoIvaAv(
        RegistroDataModelImpl listaCuentaDebitoIvaAv) {
        this.listaCuentaDebitoIvaAv = listaCuentaDebitoIvaAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoIvaAv
     * 
     * @return listaCuentaCreditoIvaAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoIvaAv() {
        return listaCuentaCreditoIvaAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoIvaAv
     * 
     * @param listaCuentaCreditoIvaAv
     * Variable a asignar en listaCuentaCreditoIvaAv
     */
    public void setListaCuentaCreditoIvaAv(
        RegistroDataModelImpl listaCuentaCreditoIvaAv) {
        this.listaCuentaCreditoIvaAv = listaCuentaCreditoIvaAv;
    }

    /**
     * Retorna la lista listaCuentaDebitoReteAv
     * 
     * @return listaCuentaDebitoReteAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoReteAv() {
        return listaCuentaDebitoReteAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoReteAv
     * 
     * @param listaCuentaDebitoReteAv
     * Variable a asignar en listaCuentaDebitoReteAv
     */
    public void setListaCuentaDebitoReteAv(
        RegistroDataModelImpl listaCuentaDebitoReteAv) {
        this.listaCuentaDebitoReteAv = listaCuentaDebitoReteAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoReteAv
     * 
     * @return listaCuentaCreditoReteAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoReteAv() {
        return listaCuentaCreditoReteAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoReteAv
     * 
     * @param listaCuentaCreditoReteAv
     * Variable a asignar en listaCuentaCreditoReteAv
     */
    public void setListaCuentaCreditoReteAv(
        RegistroDataModelImpl listaCuentaCreditoReteAv) {
        this.listaCuentaCreditoReteAv = listaCuentaCreditoReteAv;
    }

    /**
     * Retorna la lista listaCuentaDebitoDescuentoAv
     * 
     * @return listaCuentaDebitoDescuentoAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoDescuentoAv() {
        return listaCuentaDebitoDescuentoAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoDescuentoAv
     * 
     * @param listaCuentaDebitoDescuentoAv
     * Variable a asignar en listaCuentaDebitoDescuentoAv
     */
    public void setListaCuentaDebitoDescuentoAv(
        RegistroDataModelImpl listaCuentaDebitoDescuentoAv) {
        this.listaCuentaDebitoDescuentoAv = listaCuentaDebitoDescuentoAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoDescuentoAv
     * 
     * @return listaCuentaCreditoDescuentoAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoDescuentoAv() {
        return listaCuentaCreditoDescuentoAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoDescuentoAv
     * 
     * @param listaCuentaCreditoDescuentoAv
     * Variable a asignar en listaCuentaCreditoDescuentoAv
     */
    public void setListaCuentaCreditoDescuentoAv(
        RegistroDataModelImpl listaCuentaCreditoDescuentoAv) {
        this.listaCuentaCreditoDescuentoAv = listaCuentaCreditoDescuentoAv;
    }

    /**
     * Retorna la lista listaCuentaDebitoIcaAv
     * 
     * @return listaCuentaDebitoIcaAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoIcaAv() {
        return listaCuentaDebitoIcaAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoIcaAv
     * 
     * @param listaCuentaDebitoIcaAv
     * Variable a asignar en listaCuentaDebitoIcaAv
     */
    public void setListaCuentaDebitoIcaAv(
        RegistroDataModelImpl listaCuentaDebitoIcaAv) {
        this.listaCuentaDebitoIcaAv = listaCuentaDebitoIcaAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoIcaAv
     * 
     * @return listaCuentaCreditoIcaAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoIcaAv() {
        return listaCuentaCreditoIcaAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoIcaAv
     * 
     * @param listaCuentaCreditoIcaAv
     * Variable a asignar en listaCuentaCreditoIcaAv
     */
    public void setListaCuentaCreditoIcaAv(
        RegistroDataModelImpl listaCuentaCreditoIcaAv) {
        this.listaCuentaCreditoIcaAv = listaCuentaCreditoIcaAv;
    }

    /**
     * Retorna la lista listaUnidad
     * 
     * @return listaUnidad
     */
    public RegistroDataModelImpl getListaUnidad() {
        return listaUnidad;
    }

    /**
     * Asigna la lista listaUnidad
     * 
     * @param listaUnidad
     * Variable a asignar en listaUnidad
     */
    public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    /**
     * Retorna la lista listaCuentaDebitoUtilidad
     * 
     * @return listaCuentaDebitoUtilidad
     */
    public RegistroDataModelImpl getListaCuentaDebitoUtilidad() {
        return listaCuentaDebitoUtilidad;
    }

    /**
     * Asigna la lista listaCuentaDebitoUtilidad
     * 
     * @param listaCuentaDebitoUtilidad
     * Variable a asignar en listaCuentaDebitoUtilidad
     */
    public void setListaCuentaDebitoUtilidad(
        RegistroDataModelImpl listaCuentaDebitoUtilidad) {
        this.listaCuentaDebitoUtilidad = listaCuentaDebitoUtilidad;
    }

    /**
     * Retorna la lista listaCuentaCreditoUtilidad
     * 
     * @return listaCuentaCreditoUtilidad
     */
    public RegistroDataModelImpl getListaCuentaCreditoUtilidad() {
        return listaCuentaCreditoUtilidad;
    }

    /**
     * Asigna la lista listaCuentaCreditoUtilidad
     * 
     * @param listaCuentaCreditoUtilidad
     * Variable a asignar en listaCuentaCreditoUtilidad
     */
    public void setListaCuentaCreditoUtilidad(
        RegistroDataModelImpl listaCuentaCreditoUtilidad) {
        this.listaCuentaCreditoUtilidad = listaCuentaCreditoUtilidad;
    }

    /**
     * Retorna la lista listaCuentaDebitoUtilidadAv
     * 
     * @return listaCuentaDebitoUtilidadAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoUtilidadAv() {
        return listaCuentaDebitoUtilidadAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoUtilidadAv
     * 
     * @param listaCuentaDebitoUtilidadAv
     * Variable a asignar en listaCuentaDebitoUtilidadAv
     */
    public void setListaCuentaDebitoUtilidadAv(
        RegistroDataModelImpl listaCuentaDebitoUtilidadAv) {
        this.listaCuentaDebitoUtilidadAv = listaCuentaDebitoUtilidadAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoUtilidadAv
     * 
     * @return listaCuentaCreditoUtilidadAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoUtilidadAv() {
        return listaCuentaCreditoUtilidadAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoUtilidadAv
     * 
     * @param listaCuentaCreditoUtilidadAv
     * Variable a asignar en listaCuentaCreditoUtilidadAv
     */
    public void setListaCuentaCreditoUtilidadAv(
        RegistroDataModelImpl listaCuentaCreditoUtilidadAv) {
        this.listaCuentaCreditoUtilidadAv = listaCuentaCreditoUtilidadAv;
    }

    /**
     * Retorna la lista listaCuentaRecaudo
     * 
     * @return listaCuentaRecaudo
     */
    public RegistroDataModelImpl getListaCuentaRecaudo() {
        return listaCuentaRecaudo;
    }

    /**
     * Asigna la lista listaCuentaRecaudo
     * 
     * @param listaCuentaRecaudo
     * Variable a asignar en listaCuentaRecaudo
     */
    public void setListaCuentaRecaudo(
        RegistroDataModelImpl listaCuentaRecaudo) {
        this.listaCuentaRecaudo = listaCuentaRecaudo;
    }

    /**
     * Retorna la lista listaReferencia
     * 
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    /**
     * Asigna la lista listaReferencia
     * 
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     * 
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     * 
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    /**
     * Retorna la lista listaCuentaDebitoImpoConsumo
     * 
     * @return listaCuentaDebitoImpoConsumo
     */
    public RegistroDataModelImpl getListaCuentaDebitoImpoConsumo() {
        return listaCuentaDebitoImpoConsumo;
    }

    /**
     * Asigna la lista listaCuentaDebitoImpoConsumo
     * 
     * @param listaCuentaDebitoImpoConsumo
     * Variable a asignar en listaCuentaDebitoImpoConsumo
     */
    public void setListaCuentaDebitoImpoConsumo(
        RegistroDataModelImpl listaCuentaDebitoImpoConsumo) {
        this.listaCuentaDebitoImpoConsumo = listaCuentaDebitoImpoConsumo;
    }

    /**
     * Retorna la lista listaCuentaCreditoImpoConsumo
     * 
     * @return listaCuentaCreditoImpoConsumo
     */
    public RegistroDataModelImpl getListaCuentaCreditoImpoConsumo() {
        return listaCuentaCreditoImpoConsumo;
    }

    /**
     * Asigna la lista listaCuentaCreditoImpoConsumo
     * 
     * @param listaCuentaCreditoImpoConsumo
     * Variable a asignar en listaCuentaCreditoImpoConsumo
     */
    public void setListaCuentaCreditoImpoConsumo(
        RegistroDataModelImpl listaCuentaCreditoImpoConsumo) {
        this.listaCuentaCreditoImpoConsumo = listaCuentaCreditoImpoConsumo;
    }
		
	/**
	 * @return the aplicaAutoRenta
	 */
	public boolean isAplicaAutoRenta() {
		return aplicaAutoRenta;
	}

	/**
	 * @param aplicaAutoRenta the aplicaAutoRenta to set
	 */
	public void setAplicaAutoRenta(boolean aplicaAutoRenta) {
		this.aplicaAutoRenta = aplicaAutoRenta;
	}

	/**
	 * @return the listaCreditoAutoRentaVAnt
	 */
	public RegistroDataModelImpl getListaCreditoAutoRentaVAnt() {
		return listaCreditoAutoRentaVAnt;
	}

	/**
	 * @param listaCreditoAutoRentaVAnt the listaCreditoAutoRentaVAnt to set
	 */
	public void setListaCreditoAutoRentaVAnt(RegistroDataModelImpl listaCreditoAutoRentaVAnt) {
		this.listaCreditoAutoRentaVAnt = listaCreditoAutoRentaVAnt;
	}

	/**
	 * @return the listaDebitoAutoRentaVAct
	 */
	public RegistroDataModelImpl getListaDebitoAutoRentaVAct() {
		return listaDebitoAutoRentaVAct;
	}

	/**
	 * @param listaDebitoAutoRentaVAct the listaDebitoAutoRentaVAct to set
	 */
	public void setListaDebitoAutoRentaVAct(RegistroDataModelImpl listaDebitoAutoRentaVAct) {
		this.listaDebitoAutoRentaVAct = listaDebitoAutoRentaVAct;
	}

	/**
	 * @return the listaCreditoAutoRentaVAct
	 */
	public RegistroDataModelImpl getListaCreditoAutoRentaVAct() {
		return listaCreditoAutoRentaVAct;
	}

	/**
	 * @param listaCreditoAutoRentaVAct the listaCreditoAutoRentaVAct to set
	 */
	public void setListaCreditoAutoRentaVAct(RegistroDataModelImpl listaCreditoAutoRentaVAct) {
		this.listaCreditoAutoRentaVAct = listaCreditoAutoRentaVAct;
	}

	/**
	 * @return the listaDebitoAutoRentaVAnt
	 */
	public RegistroDataModelImpl getListaDebitoAutoRentaVAnt() {
		return listaDebitoAutoRentaVAnt;
	}

	/**
	 * @param listaDebitoAutoRentaVAnt the listaDebitoAutoRentaVAnt to set
	 */
	public void setListaDebitoAutoRentaVAnt(RegistroDataModelImpl listaDebitoAutoRentaVAnt) {
		this.listaDebitoAutoRentaVAnt = listaDebitoAutoRentaVAnt;
	}

	/**
	 * @return the aplicaAutoICA
	 */
	public boolean isAplicaAutoICA() {
		return aplicaAutoICA;
	}

	/**
	 * @param aplicaAutoICA the aplicaAutoICA to set
	 */
	public void setAplicaAutoICA(boolean aplicaAutoICA) {
		this.aplicaAutoICA = aplicaAutoICA;
	}

	/**
	 * @return the listaDebitoAutoICAVAct
	 */
	public RegistroDataModelImpl getListaDebitoAutoICAVAct() {
		return listaDebitoAutoICAVAct;
	}

	/**
	 * @param listaDebitoAutoICAVAct the listaDebitoAutoICAVAct to set
	 */
	public void setListaDebitoAutoICAVAct(RegistroDataModelImpl listaDebitoAutoICAVAct) {
		this.listaDebitoAutoICAVAct = listaDebitoAutoICAVAct;
	}

	/**
	 * @return the listaCreditoAutoICAVAct
	 */
	public RegistroDataModelImpl getListaCreditoAutoICAVAct() {
		return listaCreditoAutoICAVAct;
	}

	/**
	 * @param listaCreditoAutoICAVAct the listaCreditoAutoICAVAct to set
	 */
	public void setListaCreditoAutoICAVAct(RegistroDataModelImpl listaCreditoAutoICAVAct) {
		this.listaCreditoAutoICAVAct = listaCreditoAutoICAVAct;
	}

	/**
	 * @return the listaDebitoAutoICAVAnt
	 */
	public RegistroDataModelImpl getListaDebitoAutoICAVAnt() {
		return listaDebitoAutoICAVAnt;
	}

	/**
	 * @param listaDebitoAutoICAVAnt the listaDebitoAutoICAVAnt to set
	 */
	public void setListaDebitoAutoICAVAnt(RegistroDataModelImpl listaDebitoAutoICAVAnt) {
		this.listaDebitoAutoICAVAnt = listaDebitoAutoICAVAnt;
	}

	/**
	 * @return the listaCreditoAutoICAVAnt
	 */
	public RegistroDataModelImpl getListaCreditoAutoICAVAnt() {
		return listaCreditoAutoICAVAnt;
	}

	/**
	 * @param listaCreditoAutoICAVAnt the listaCreditoAutoICAVAnt to set
	 */
	public void setListaCreditoAutoICAVAnt(RegistroDataModelImpl listaCreditoAutoICAVAnt) {
		this.listaCreditoAutoICAVAnt = listaCreditoAutoICAVAnt;
	}

	/**
	 * @return the listaCCPET
	 */
	public RegistroDataModelImpl getListaCCPET() {
		return listaCCPET;
	}

	/**
	 * @param listaCCPET the listaCCPET to set
	 */
	public void setListaCCPET(RegistroDataModelImpl listaCCPET) {
		this.listaCCPET = listaCCPET;
	}

	/**
	 * @return the listaFuenteCuipo
	 */
	public RegistroDataModelImpl getListaFuenteCuipo() {
		return listaFuenteCuipo;
	}

	/**
	 * @param listaFuenteCuipo the listaFuenteCuipo to set
	 */
	public void setListaFuenteCuipo(RegistroDataModelImpl listaFuenteCuipo) {
		this.listaFuenteCuipo = listaFuenteCuipo;
	}

	/**
	 * @return the listaUnidadEjecutora
	 */
	public RegistroDataModelImpl getListaUnidadEjecutora() {
		return listaUnidadEjecutora;
	}

	/**
	 * @param listaUnidadEjecutora the listaUnidadEjecutora to set
	 */
	public void setListaUnidadEjecutora(RegistroDataModelImpl listaUnidadEjecutora) {
		this.listaUnidadEjecutora = listaUnidadEjecutora;
	}
	
	//***************
    /**
     * Retorna la lista listaCuentaDebitoPublico
     * 
     * @return listaCuentaDebitoPublico
     */
    public RegistroDataModelImpl getListaCuentaDebitoPublico() {
        return listaCuentaDebitoPublico;
    }
    /**
     * Asigna la lista listaCuentaDebitoPublico
     * 
     * @param listaCuentaDebitoPublico
     * Variable a asignar en  listaCuentaDebitoPublico
     */
    public void setListaCuentaDebitoPublico(RegistroDataModelImpl listaCuentaDebitoPublico) {
        this.listaCuentaDebitoPublico = listaCuentaDebitoPublico;
    }
    /**
     * Retorna la lista listaCuentaCreditoPublico
     * 
     * @return listaCuentaCreditoPublico
     */
    public RegistroDataModelImpl getListaCuentaCreditoPublico() {
        return listaCuentaCreditoPublico;
    }
    /**
     * Asigna la lista listaCuentaCreditoPublico
     * 
     * @param listaCuentaCreditoPublico
     * Variable a asignar en  listaCuentaCreditoPublico
     */
    public void setListaCuentaCreditoPublico(RegistroDataModelImpl listaCuentaCreditoPublico) {
        this.listaCuentaCreditoPublico = listaCuentaCreditoPublico;
    }	
	
	/**
     * Retorna la lista listaCuentaDebitoPrivado
     * 
     * @return listaCuentaDebitoPrivado
     */
    public RegistroDataModelImpl getListaCuentaDebitoPrivado() {
        return listaCuentaDebitoPrivado;
    }
    /**
     * Asigna la lista listaCuentaDebitoPrivado
     * 
     * @param listaCuentaDebitoPrivado
     * Variable a asignar en  listaCuentaDebitoPrivado
     */
    public void setListaCuentaDebitoPrivado(RegistroDataModelImpl listaCuentaDebitoPrivado) {
        this.listaCuentaDebitoPrivado = listaCuentaDebitoPrivado;
    }
    /**
     * Retorna la lista listaCuentaCreditoPrivado
     * 
     * @return listaCuentaCreditoPrivado
     */
    public RegistroDataModelImpl getListaCuentaCreditoPrivado() {
        return listaCuentaCreditoPrivado;
    }
    /**
     * Asigna la lista listaCuentaCreditoPrivado
     * 
     * @param listaCuentaCreditoPrivado
     * Variable a asignar en  listaCuentaCreditoPrivado
     */
    public void setListaCuentaCreditoPrivado(RegistroDataModelImpl listaCuentaCreditoPrivado) {
        this.listaCuentaCreditoPrivado = listaCuentaCreditoPrivado;
    }

	/**
	 * @return the nombreIndicador
	 */
	public String getNombreIndicador() {
		return nombreIndicador;
	}

	/**
	 * @param nombreIndicador the nombreIndicador to set
	 */
	public void setNombreIndicador(String nombreIndicador) {
		this.nombreIndicador = nombreIndicador;
	}

	
	public boolean isTipoEntidad() {
		return tipoEntidad;
	}

	public void setTipoEntidad(boolean tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public boolean isBloquearPorcentajeReteIva() {
		return bloquearPorcentajeReteIva;
	}

	public void setBloquearPorcentajeReteIva(boolean bloquearPorcentajeReteIva) {
		this.bloquearPorcentajeReteIva = bloquearPorcentajeReteIva;
	}

	public RegistroDataModelImpl getListaCuentaDebitoReteIvaAct() {
		return listaCuentaDebitoReteIvaAct;
	}

	public void setListaCuentaDebitoReteIvaAct(RegistroDataModelImpl listaCuentaDebitoReteIvaAct) {
		this.listaCuentaDebitoReteIvaAct = listaCuentaDebitoReteIvaAct;
	}

	public RegistroDataModelImpl getListaCuentaCreditoReteIvaAct() {
		return listaCuentaCreditoReteIvaAct;
	}

	public void setListaCuentaCreditoReteIvaAct(RegistroDataModelImpl listaCuentaCreditoReteIvaAct) {
		this.listaCuentaCreditoReteIvaAct = listaCuentaCreditoReteIvaAct;
	}

	public RegistroDataModelImpl getListaCuentaDebitoIvaAntes() {
		return listaCuentaDebitoIvaAntes;
	}

	public void setListaCuentaDebitoIvaAntes(RegistroDataModelImpl listaCuentaDebitoIvaAntes) {
		this.listaCuentaDebitoIvaAntes = listaCuentaDebitoIvaAntes;
	}

	public RegistroDataModelImpl getListaCuentaCreditoIvaAntes() {
		return listaCuentaCreditoIvaAntes;
	}

	public void setListaCuentaCreditoIvaAntes(RegistroDataModelImpl listaCuentaCreditoIvaAntes) {
		this.listaCuentaCreditoIvaAntes = listaCuentaCreditoIvaAntes;
	}

	public boolean isVisibleSigec() {
		return visibleSigec;
	}

	public void setVisibleSigec(boolean visibleSigec) {
		this.visibleSigec = visibleSigec;
	}

	/**
	 * @return the listaTiposConcepto
	 */
	public List<Registro> getListaTiposConcepto() {
		return listaTiposConcepto;
	}

	/**
	 * @param listaTiposConcepto the listaTiposConcepto to set
	 */
	public void setListaTiposConcepto(List<Registro> listaTiposConcepto) {
		this.listaTiposConcepto = listaTiposConcepto;
	}

	public boolean isPermiteAjusteDecimales() {
		return permiteAjusteDecimales;
	}

	public void setPermiteAjusteDecimales(boolean permiteAjusteDecimales) {
		this.permiteAjusteDecimales = permiteAjusteDecimales;
	}
	
		
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
