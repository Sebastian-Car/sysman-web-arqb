/*-
 * FrmconceptosadicalmacensControlador.java
 *
 * 1.0
 * 
 * 06/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmconceptosadicalmacensControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmconceptosadicalmacensControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmcreeconfiguracionsControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para ingresar las cuentas de derecho de conexion
 *
 * @version 1.0, 06/12/2017
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class FrmconceptosadicalmacensControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ano recibido por parametro
     */
    private String ano;
    /**
     * Atributo que almacena el tipo de cobro recibido por parametro
     */
    private String tipoCobro;
    /**
     * Atributo que almacena el codigo recibido por parametro
     */
    private String codigo;
    /**
     * Map recibida por parametro que trae la llave del registro por
     * el cual se carga este formulario
     */
    Map<String, Object> ridConcepto;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros del combo cuenta debito acometida actual
     */
    private RegistroDataModelImpl listacuentaDebitoAcometida;
    /**
     * Lista de registros del combo cuenta credito acometida actual
     */
    private RegistroDataModelImpl listaCuentaCreditoAcometida;
    /**
     * Lista de registros del combo cuenta debito actual aiu
     */
    private RegistroDataModelImpl listaCuentaDebitoAiu;
    /**
     * Lista de registros del combo cuenta credito actual aiu
     */
    private RegistroDataModelImpl listaCuentaCreditoAiu;
    /**
     * Lista de registros del combo cuenta debito actual
     * administracion e imprevistos
     */
    private RegistroDataModelImpl listaCuentaDebitoAdmon;
    /**
     * Lista de registros del combo cuenta credito actual
     * administracion e imprevistos
     */
    private RegistroDataModelImpl listaCuentaCreditoAdmon;
    /**
     * Lista de registros del combo cuenta debito iva actual de iva de
     * utilidad
     */
    private RegistroDataModelImpl listaCuentaDebitoIva;
    /**
     * Lista de registros del combo cuenta credito iva actual de iva
     * de utilidad
     */
    private RegistroDataModelImpl listaCuentaCreditoIva;
    /**
     * Lista de registros del combo cuenta debito anterior de
     * acometida del servicio
     */
    private RegistroDataModelImpl listaCuentaDebitoAcometidaAv;
    /**
     * Lista de registros del combo cuenta credito anterior de
     * acometida anterior
     */
    private RegistroDataModelImpl listaCuentaCreditoAcometidaAV;
    /**
     * Lista de registros del combo cuenta debito anterior de aiu
     */
    private RegistroDataModelImpl listaCuentaDebitoAiuAv;
    /**
     * Lista de registros del combo cuenta credito anterior de aiu
     */
    private RegistroDataModelImpl listaCuentaCreditoAiuAv;
    /**
     * Lista de registros del combo cuenta debito anterior de
     * administracion e imprevistos
     */
    private RegistroDataModelImpl listaCuentaDebitoAdmonAv;
    /**
     * Lista de registros del combo cuenta credito anterior de
     * administracion e imprevistos
     */
    private RegistroDataModelImpl listaCuentaCreditoAdmonAv;
    /**
     * Lista de registros del combo cuenta debito anterior del iva de
     * la utilidad
     */
    private RegistroDataModelImpl listaCuentaDebitoIvaAv;
    /**
     * Lista de registros del combo cuenta credito anterior del iva de
     * la utilidad
     */
    private RegistroDataModelImpl listaCuentaCreditoIvaAv;
    /**
     * Lista de registros del combo cuenta debito actual de utilidad
     * aiu
     */
    private RegistroDataModelImpl listaCuentaDebitoUtilidad;
    /**
     * Lista de registros del combo cuenta credito actual de utilidad
     * aiu
     */
    private RegistroDataModelImpl listaCuentaCreditoUtilidad;
    /**
     * Lista de registros del combo cuenta debito anterior de la
     * utilidad aiu
     */
    private RegistroDataModelImpl listaCuentaDebitoUtilidadAv;
    /**
     * Lista de registros del combo cuenta credito anterior de
     * utilidad aiu
     */
    private RegistroDataModelImpl listaCuentaCreditoUtilidadAv;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmconceptosadicalmacensControlador
     */
    @SuppressWarnings("unchecked")
    public FrmconceptosadicalmacensControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1491
            numFormulario = GeneralCodigoFormaEnum.FRM_CONCEPTOSADICALMACEN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridConcepto = (Map<String, Object>) parametrosEntrada
                                .get("ridConcepto");
                ano = ridConcepto.get(
                                FrmconceptosadicalmacensControladorEnum.KEY_ANO
                                                .getValue())
                                .toString();
                tipoCobro = ridConcepto
                                .get(FrmconceptosadicalmacensControladorEnum.KEY_TIPOCOBRO
                                                .getValue())
                                .toString();
                codigo = ridConcepto
                                .get(FrmconceptosadicalmacensControladorEnum.KEY_CODIGO
                                                .getValue())
                                .toString();
            }
            // </INI_ADICIONAL>
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
        cargarListacuentaDebitoAcometida();
        cargarListaCuentaCreditoAcometida();
        cargarListaCuentaDebitoAiu();
        cargarListaCuentaCreditoAiu();
        cargarListaCuentaDebitoAdmon();
        cargarListaCuentaCreditoAdmon();
        cargarListaCuentaDebitoIva();
        cargarListaCuentaCreditoIva();
        cargarListaCuentaDebitoAcometidaAv();
        cargarListaCuentaCreditoAcometidaAV();
        cargarListaCuentaDebitoAiuAv();
        cargarListaCuentaCreditoAiuAv();
        cargarListaCuentaDebitoAdmonAv();
        cargarListaCuentaCreditoAdmonAv();
        cargarListaCuentaDebitoIvaAv();
        cargarListaCuentaCreditoIvaAv();
        cargarListaCuentaDebitoUtilidad();
        cargarListaCuentaCreditoUtilidad();
        cargarListaCuentaDebitoUtilidadAv();
        cargarListaCuentaCreditoUtilidadAv();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
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
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL12243
                                                        .getValue());

        parametrosListado
                        .put(FrmconceptosadicalmacensControladorEnum.KEY_COMPANIA
                                        .getValue(), compania);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_ANO
                        .getValue(), ano);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_TIPOCOBRO
                        .getValue(), tipoCobro);
        parametrosListado.put(FrmcreeconfiguracionsControladorEnum.KEY_CODIGO
                        .getValue(), codigo);
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL14304
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacuentaDebitoAcometida
     */
    public void cargarListacuentaDebitoAcometida() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listacuentaDebitoAcometida = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAcometida
     */
    public void cargarListaCuentaCreditoAcometida() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAcometida = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoAiu
     */
    public void cargarListaCuentaDebitoAiu() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoAiu = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAiu
     */
    public void cargarListaCuentaCreditoAiu() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAiu = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoAdmon
     */
    public void cargarListaCuentaDebitoAdmon() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoAdmon = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAdmon
     */
    public void cargarListaCuentaCreditoAdmon() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAdmon = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIva
     */
    public void cargarListaCuentaDebitoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoIva = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIva
     */
    public void cargarListaCuentaCreditoIva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoIva = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaDebitoAcometidaAv
     */
    public void cargarListaCuentaDebitoAcometidaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoAcometidaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAcometidaAV
     */
    public void cargarListaCuentaCreditoAcometidaAV() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAcometidaAV = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoAiuAv
     */
    public void cargarListaCuentaDebitoAiuAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoAiuAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAiuAv
     */
    public void cargarListaCuentaCreditoAiuAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAiuAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoAdmonAv
     */
    public void cargarListaCuentaDebitoAdmonAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoAdmonAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoAdmonAv
     */
    public void cargarListaCuentaCreditoAdmonAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoAdmonAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoIvaAv
     */
    public void cargarListaCuentaDebitoIvaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoIvaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoIvaAv
     */
    public void cargarListaCuentaCreditoIvaAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoIvaAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoUtilidad
     */
    public void cargarListaCuentaDebitoUtilidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoUtilidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoUtilidad
     */
    public void cargarListaCuentaCreditoUtilidad() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoUtilidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaDebitoUtilidadAv
     */
    public void cargarListaCuentaDebitoUtilidadAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaDebitoUtilidadAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * 
     * Carga la lista listaCuentaCreditoUtilidadAv
     */
    public void cargarListaCuentaCreditoUtilidadAv() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptosadicalmacensControladorUrlEnum.URL8159
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaCreditoUtilidadAv = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacuentaDebitoAcometida
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacuentaDebitoAcometida(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBACOMETIDA",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAcometida
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAcometida(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDACOMETIDA",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoAiu
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoAiu(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBAIU",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAiu
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAiu(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDAIU",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoAdmon
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoAdmon(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBADMONIMP",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAdmon
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAdmon(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDADMONIMP",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTADEBIVAUTILIDAD",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTACREDIVAUTILIDAD",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoAcometidaAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoAcometidaAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBACOMETIDA_AV",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAcometidaAV
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAcometidaAV(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDACOMETIDA_AV",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoAiuAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoAiuAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBAIU_AV",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAiuAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAiuAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDAIU_AV",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebitoAdmonAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoAdmonAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTADEBADMONIMP_AV",
                        registroAux.getCampos().get("ID"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCreditoAdmonAv
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoAdmonAv(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTACREDADMONIMP_AV",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTADEBIVAUTILIDAD_AV",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTACREDIVAUTILIDAD_AV",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTADEBUTILIDADAIU",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTACREDUTILIDADAIU",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTADEBUTILIDADAIU_AV",
                        registroAux.getCampos().get("ID"));
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
        registro.getCampos().put("CTACREDUTILIDADAIU_AV",
                        registroAux.getCampos().get("ID"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que valida los campos del formulario llamado en el
     * metodo actualizar Antes
     * 
     * @param campo
     * @param mensaje
     * @return verdadero o falso
     */
    public boolean validarCampos(String campo, String mensaje) {
        if (SysmanFunciones.nvl(registro.getCampos().get(campo),
                        "").toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString(mensaje));
            return true;
        }
        return false;
    }

    /**
     * validad cuentas de utilidad aiu
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentasUtilidadAiu() {
        if (!"0".equals(registro.getCampos().get("FORMULAR_UTILIDADAIU")
                        .toString())) {
            if (validarCampos("CTADEBUTILIDADAIU", "TB_TB3841")
                || validarCampos("CTACREDUTILIDADAIU", "TB_TB3842")) {
                return true;
            }
            if (validarCampos("CTADEBUTILIDADAIU_AV", "TB_TB3843")
                || validarCampos("CTACREDUTILIDADAIU_AV", "TB_TB3844")) {
                return true;
            }
        }

        return false;

    }

    /**
     * validad cuentas de utilidad iva
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentasUtilidadIva() {
        if (!"0".equals(registro.getCampos().get("FORMULAR_IVAUTILIDAD")
                        .toString())) {
            if (validarCampos("CTADEBIVAUTILIDAD", "TB_TB3845")
                || validarCampos("CTACREDIVAUTILIDAD", "TB_TB3846")) {
                return true;
            }
            if (validarCampos("CTADEBIVAUTILIDAD_AV", "TB_TB3847")
                || validarCampos("CTACREDIVAUTILIDAD_AV", "TB_TB3848")) {
                return true;
            }
        }
        return false;

    }

    /**
     * validad cuentas de acometida
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentasAcometida() {
        if (!"0".equals(registro.getCampos().get("FORMULAR_ACOMETIDA")
                        .toString())) {

            if (validarCampos("CTADEBACOMETIDA", "TB_TB3828")
                || validarCampos("CTACREDACOMETIDA", "TB_TB3830")) {
                return true;
            }
            if (validarCampos("CTADEBACOMETIDA_AV", "TB_TB3831")
                || validarCampos("CTACREDACOMETIDA_AV", "TB_TB3831")) {
                return true;
            }

        }
        return false;
    }

    /**
     * validad cuentas de Aiu
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentasAiu() {
        if (!"0".equals(registro.getCampos().get("FORMULAR_AIU")
                        .toString())) {

            if (validarCampos("CTADEBAIU", "TB_TB3833")
                || validarCampos("CTACREDAIU", "TB_TB3834")) {
                return true;
            }
            if (validarCampos("CTADEBAIU_AV", "TB_TB3835")
                || validarCampos("CTACREDAIU_AV", "TB_TB3836")) {
                return true;
            }

        }

        return false;
    }

    /**
     * validad cuentas de Administración e Imprevistos
     * 
     * @return verdadero o falso
     */
    public boolean validarCuentasAdmon() {
        if (!"0".equals(registro.getCampos().get("FORMULAR_ADMONIMP")
                        .toString())) {
            if (validarCampos("CTADEBADMONIMP", "TB_TB3837")
                || validarCampos("CTACREDADMONIMP", "TB_TB3838")) {
                return true;
            }
            if (validarCampos("CTADEBADMONIMP_AV", "TB_TB3839")
                || validarCampos("CTACREDADMONIMP_AV", "TB_TB3840")) {
                return true;
            }
        }

        return false;
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
        precargarRegistro();
        cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "ridConcepto" };
        Object[] valores = { ridConcepto };

        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SFCONCEPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
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

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos()
                        .remove(FrmconceptosadicalmacensControladorEnum.TIPOCOBRO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

        if (validarCuentasAcometida() || validarCuentasAiu()
            || validarCuentasAdmon()) {

            return false;

        }

        if (validarCuentasUtilidadAiu() || validarCuentasUtilidadIva()) {
            return false;
        }

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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacuentaDebitoAcometida
     * 
     * @return listacuentaDebitoAcometida
     */
    public RegistroDataModelImpl getListacuentaDebitoAcometida() {
        return listacuentaDebitoAcometida;
    }

    /**
     * Asigna la lista listacuentaDebitoAcometida
     * 
     * @param listacuentaDebitoAcometida
     * Variable a asignar en listacuentaDebitoAcometida
     */
    public void setListacuentaDebitoAcometida(
        RegistroDataModelImpl listacuentaDebitoAcometida) {
        this.listacuentaDebitoAcometida = listacuentaDebitoAcometida;
    }

    /**
     * Retorna la lista listaCuentaCreditoAcometida
     * 
     * @return listaCuentaCreditoAcometida
     */
    public RegistroDataModelImpl getListaCuentaCreditoAcometida() {
        return listaCuentaCreditoAcometida;
    }

    /**
     * Asigna la lista listaCuentaCreditoAcometida
     * 
     * @param listaCuentaCreditoAcometida
     * Variable a asignar en listaCuentaCreditoAcometida
     */
    public void setListaCuentaCreditoAcometida(
        RegistroDataModelImpl listaCuentaCreditoAcometida) {
        this.listaCuentaCreditoAcometida = listaCuentaCreditoAcometida;
    }

    /**
     * Retorna la lista listaCuentaDebitoAiu
     * 
     * @return listaCuentaDebitoAiu
     */
    public RegistroDataModelImpl getListaCuentaDebitoAiu() {
        return listaCuentaDebitoAiu;
    }

    /**
     * Asigna la lista listaCuentaDebitoAiu
     * 
     * @param listaCuentaDebitoAiu
     * Variable a asignar en listaCuentaDebitoAiu
     */
    public void setListaCuentaDebitoAiu(
        RegistroDataModelImpl listaCuentaDebitoAiu) {
        this.listaCuentaDebitoAiu = listaCuentaDebitoAiu;
    }

    /**
     * Retorna la lista listaCuentaCreditoAiu
     * 
     * @return listaCuentaCreditoAiu
     */
    public RegistroDataModelImpl getListaCuentaCreditoAiu() {
        return listaCuentaCreditoAiu;
    }

    /**
     * Asigna la lista listaCuentaCreditoAiu
     * 
     * @param listaCuentaCreditoAiu
     * Variable a asignar en listaCuentaCreditoAiu
     */
    public void setListaCuentaCreditoAiu(
        RegistroDataModelImpl listaCuentaCreditoAiu) {
        this.listaCuentaCreditoAiu = listaCuentaCreditoAiu;
    }

    /**
     * Retorna la lista listaCuentaDebitoAdmon
     * 
     * @return listaCuentaDebitoAdmon
     */
    public RegistroDataModelImpl getListaCuentaDebitoAdmon() {
        return listaCuentaDebitoAdmon;
    }

    /**
     * Asigna la lista listaCuentaDebitoAdmon
     * 
     * @param listaCuentaDebitoAdmon
     * Variable a asignar en listaCuentaDebitoAdmon
     */
    public void setListaCuentaDebitoAdmon(
        RegistroDataModelImpl listaCuentaDebitoAdmon) {
        this.listaCuentaDebitoAdmon = listaCuentaDebitoAdmon;
    }

    /**
     * Retorna la lista listaCuentaCreditoAdmon
     * 
     * @return listaCuentaCreditoAdmon
     */
    public RegistroDataModelImpl getListaCuentaCreditoAdmon() {
        return listaCuentaCreditoAdmon;
    }

    /**
     * Asigna la lista listaCuentaCreditoAdmon
     * 
     * @param listaCuentaCreditoAdmon
     * Variable a asignar en listaCuentaCreditoAdmon
     */
    public void setListaCuentaCreditoAdmon(
        RegistroDataModelImpl listaCuentaCreditoAdmon) {
        this.listaCuentaCreditoAdmon = listaCuentaCreditoAdmon;
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
     * Retorna la lista listaCuentaDebitoAcometidaAv
     * 
     * @return listaCuentaDebitoAcometidaAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoAcometidaAv() {
        return listaCuentaDebitoAcometidaAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoAcometidaAv
     * 
     * @param listaCuentaDebitoAcometidaAv
     * Variable a asignar en listaCuentaDebitoAcometidaAv
     */
    public void setListaCuentaDebitoAcometidaAv(
        RegistroDataModelImpl listaCuentaDebitoAcometidaAv) {
        this.listaCuentaDebitoAcometidaAv = listaCuentaDebitoAcometidaAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoAcometidaAV
     * 
     * @return listaCuentaCreditoAcometidaAV
     */
    public RegistroDataModelImpl getListaCuentaCreditoAcometidaAV() {
        return listaCuentaCreditoAcometidaAV;
    }

    /**
     * Asigna la lista listaCuentaCreditoAcometidaAV
     * 
     * @param listaCuentaCreditoAcometidaAV
     * Variable a asignar en listaCuentaCreditoAcometidaAV
     */
    public void setListaCuentaCreditoAcometidaAV(
        RegistroDataModelImpl listaCuentaCreditoAcometidaAV) {
        this.listaCuentaCreditoAcometidaAV = listaCuentaCreditoAcometidaAV;
    }

    /**
     * Retorna la lista listaCuentaDebitoAiuAv
     * 
     * @return listaCuentaDebitoAiuAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoAiuAv() {
        return listaCuentaDebitoAiuAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoAiuAv
     * 
     * @param listaCuentaDebitoAiuAv
     * Variable a asignar en listaCuentaDebitoAiuAv
     */
    public void setListaCuentaDebitoAiuAv(
        RegistroDataModelImpl listaCuentaDebitoAiuAv) {
        this.listaCuentaDebitoAiuAv = listaCuentaDebitoAiuAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoAiuAv
     * 
     * @return listaCuentaCreditoAiuAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoAiuAv() {
        return listaCuentaCreditoAiuAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoAiuAv
     * 
     * @param listaCuentaCreditoAiuAv
     * Variable a asignar en listaCuentaCreditoAiuAv
     */
    public void setListaCuentaCreditoAiuAv(
        RegistroDataModelImpl listaCuentaCreditoAiuAv) {
        this.listaCuentaCreditoAiuAv = listaCuentaCreditoAiuAv;
    }

    /**
     * Retorna la lista listaCuentaDebitoAdmonAv
     * 
     * @return listaCuentaDebitoAdmonAv
     */
    public RegistroDataModelImpl getListaCuentaDebitoAdmonAv() {
        return listaCuentaDebitoAdmonAv;
    }

    /**
     * Asigna la lista listaCuentaDebitoAdmonAv
     * 
     * @param listaCuentaDebitoAdmonAv
     * Variable a asignar en listaCuentaDebitoAdmonAv
     */
    public void setListaCuentaDebitoAdmonAv(
        RegistroDataModelImpl listaCuentaDebitoAdmonAv) {
        this.listaCuentaDebitoAdmonAv = listaCuentaDebitoAdmonAv;
    }

    /**
     * Retorna la lista listaCuentaCreditoAdmonAv
     * 
     * @return listaCuentaCreditoAdmonAv
     */
    public RegistroDataModelImpl getListaCuentaCreditoAdmonAv() {
        return listaCuentaCreditoAdmonAv;
    }

    /**
     * Asigna la lista listaCuentaCreditoAdmonAv
     * 
     * @param listaCuentaCreditoAdmonAv
     * Variable a asignar en listaCuentaCreditoAdmonAv
     */
    public void setListaCuentaCreditoAdmonAv(
        RegistroDataModelImpl listaCuentaCreditoAdmonAv) {
        this.listaCuentaCreditoAdmonAv = listaCuentaCreditoAdmonAv;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
