/*-
 * FrmConfiguracionInterfazControlador.java
 *
 * 1.0
 * 
 * 31/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.auditoriacuentasmedicas.ejb.EjbAuditoriaCuentasMedicasCeroLocal;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmConfiguracionInterfazControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite configurar la interfaz contable para RIPS
 *
 * @version 1.0, 31/10/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmConfiguracionInterfazControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion
     */
    private final String usuario;

    private int ano;

    private int desdeAno;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;

    /**
     * Lista que carga los anios desde
     */
    private List<Registro> listaDesdeAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los auxiliares
     */
    private RegistroDataModelImpl listaAuxiliar;
    /**
     * Lista que carga los auxiliares en la grilla del subformulario
     */
    private RegistroDataModelImpl listaAuxiliarE;

    /**
     * 
     * Lista que carga el centro de costo
     */

    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista que carga los centro de costo en la grilla del
     * subformulario
     */
    private RegistroDataModelImpl listaCentroCostoE;

    /**
     * Lista que carga los terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista que carga los terceros en la grilla del subformulario
     */
    private RegistroDataModelImpl listaTerceroE;
    /**
     * Lista que carga las referencias
     */
    private RegistroDataModelImpl listaReferencia;
    /**
     * Lista que carga las referencias en la grilla del subformulario
     */
    private RegistroDataModelImpl listaReferenciaE;
    /**
     * Lista que carga las fuentes de recurso
     */
    private RegistroDataModelImpl listaFuenteRecurso;
    /**
     * Lista que carga las fuentes de recurso en la grilla del
     * subformulario
     */
    private RegistroDataModelImpl listaFuenteRecursoE;
    /**
     * Lista que carga las cuentas debito
     */
    private RegistroDataModelImpl listaCuentaDebito;
    /**
     * Lista que carga las cuentas debito en la grilla del
     * subformulario
     */
    private RegistroDataModelImpl listaCuentaDebitoE;
    /**
     * Lista que carga las cuentas credito
     */
    private RegistroDataModelImpl listaCuentaCredito;
    /**
     * Lista que carga las cuentas credito en la grilla del
     * subformulario
     */
    private RegistroDataModelImpl listaCuentaCreditoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Lista que carga los tipos de comprobante
     */
    private RegistroDataModelImpl listaTipoComprobante;

    /**
     * Variable que almacena el anio seleccionado en la grilla
     */
    private String anio;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga el subformulario
     */
    private List<Registro> listaSubconfiguracioninterfaz;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    /**
     * Variable auxiliar que almacena la sucursal del tercero
     * seleccionado en la grilla
     */
    private String auxSucursal;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAuditoriaCuentasMedicasCeroLocal ejbAuditoriaCuentasMedicasCero;
    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de FrmConfiguracionInterfazControlador
     */
    public FrmConfiguracionInterfazControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        usuario = SessionUtil.getUser().getCodigo();
        ano = SysmanFunciones.ano(new Date()) + 1;
        desdeAno = SysmanFunciones.ano(new Date());
        try {
            // 2125
            numFormulario = GeneralCodigoFormaEnum.FRM_CONFIGURACION_INTERFAZ
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
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
        cargarListaAuxiliar();
        cargarListaAuxiliarE();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaTercero();
        cargarListaTerceroE();
        cargarListaReferencia();
        cargarListaReferenciaE();
        cargarListaFuenteRecurso();
        cargarListaFuenteRecursoE();
        cargarListaCuentaDebito();
        cargarListaCuentaDebitoE();
        cargarListaCuentaCredito();
        cargarListaCuentaCreditoE();
        cargarListaTipoComprobante();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        cargarListaAnio();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubconfiguracioninterfaz();
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
        listaSubconfiguracioninterfaz = null;
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
        enumBase = GenericUrlEnum.CM_CAUSACION_AUTOMATICA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * 
     * Carga la lista listaSubconfiguracioninterfaz
     *
     */
    public void cargarListaSubconfiguracioninterfaz() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

            listaSubconfiguracioninterfaz = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.CM_DET_CAUSACION_AUTOMATICA
                                                                                            .getGridKey())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            GenericUrlEnum.CM_DET_CAUSACION_AUTOMATICA
                                                                            .getTable()));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmConfiguracionInterfazControladorUrlEnum.URL9744
                                                                            .getValue())
                                            .getUrl(),
                            param));

            listaDesdeAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmConfiguracionInterfazControladorUrlEnum.URL9744
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
                                        FrmConfiguracionInterfazControladorUrlEnum.URL10182
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANIO.getName(),
                        String.valueOf(anio));

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliar
     *
     */
    public void cargarListaAuxiliarE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL10795
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANIO.getName(),
                        anio);

        listaAuxiliarE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL11414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

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
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL12368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL13314
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTerceroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL13984
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL14660
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL15276
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaReferenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
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
                                        FrmConfiguracionInterfazControladorUrlEnum.URL15898
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecursoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL16414
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaFuenteRecursoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL16928
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaDebito
     *
     */
    public void cargarListaCuentaDebitoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL17507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaDebitoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCredito() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL18091
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCuentaCredito
     *
     */
    public void cargarListaCuentaCreditoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL18703
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaCreditoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmConfiguracionInterfazControladorUrlEnum.URL29726
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        cargarListaAuxiliar();
        cargarListaCentroCosto();
        cargarListaTercero();
        cargarListaReferencia();
        cargarListaFuenteRecurso();
        cargarListaCuentaDebito();
        cargarListaCuentaCredito();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tercero en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroC(int rowNum) {
        listaSubconfiguracioninterfaz.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        auxSucursal);
    }

    /**
     * Metodo ejecutado al cambiar el control Anio en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarAnioC(int rowNum) {

        cargarListaAuxiliarE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put("AUXILIAR",
                        "");

        cargarListaCentroCostoE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put(
                        "CENTRO_COSTO",
                        "");

        cargarListaReferenciaE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put("REFERENCIA",
                        "");

        cargarListaFuenteRecursoE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put(
                        "FUENTE_RECURSO",
                        "");

        cargarListaCuentaDebitoE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put(
                        "CUENTA_DEBITO",
                        "");

        cargarListaCuentaCreditoE();
        listaSubconfiguracioninterfaz.get(rowNum).getCampos().put(
                        "CUENTA_CREDITO",
                        " ");

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
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
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CENTRO_COSTO",
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
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("TERCERO",
                        registroAux.getCampos().get("NIT"));

        registroSub.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        auxSucursal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecursoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CUENTA_DEBITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaDebitoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CUENTA_CREDITO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaCreditoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_COMPROBANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    /**
     * Genera las variables para un nuevo año asignado
     */
    public void oprimirGenerar() {
        try {
            ejbAuditoriaCuentasMedicasCero.generarProximoAnio(
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString(),
                            ano, desdeAno, usuario);
            JsfUtil.agregarMensajeInformativo("Año preparado exitosamente");
        }
        catch (SystemException e) {

            JsfUtil.agregarMensajeError(e.getMessage());

        }
        finally {
            cargarListaSubconfiguracioninterfaz();
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subconfiguracioninterfaz
     * 
     */
    public void agregarRegistroSubSubconfiguracioninterfaz() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put("CODIGO_TRANSACCION",
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            usuario);

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CM_DET_CAUSACION_AUTOMATICA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubconfiguracioninterfaz();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subconfiguracioninterfaz
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubconfiguracioninterfaz(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().remove("CODIGO_TRANSACCION");

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CM_DET_CAUSACION_AUTOMATICA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubconfiguracioninterfaz();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subconfiguracioninterfaz
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubconfiguracioninterfaz(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CM_DET_CAUSACION_AUTOMATICA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaSubconfiguracioninterfaz();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subconfiguracioninterfaz
     *
     */
    public void cancelarEdicionSubconfiguracioninterfaz() {
        cargarListaSubconfiguracioninterfaz();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     */
    @Override
    public boolean actualizarAntes() {

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
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
     * 
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    /**
     * @return the listaDesdeAno
     */
    public List<Registro> getListaDesdeAno() {
        return listaDesdeAno;
    }

    /**
     * @param listaDesdeAno
     * the listaDesdeAno to set
     */
    public void setListaDesdeAno(List<Registro> listaDesdeAno) {
        this.listaDesdeAno = listaDesdeAno;
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
     * Retorna la lista listaAuxiliar
     * 
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliarE() {
        return listaAuxiliarE;
    }

    /**
     * Asigna la lista listaAuxiliar
     * 
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
        this.listaAuxiliarE = listaAuxiliarE;
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
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
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
     * Retorna la lista listaReferencia
     * 
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferenciaE() {
        return listaReferenciaE;
    }

    /**
     * Asigna la lista listaReferencia
     * 
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
        this.listaReferenciaE = listaReferenciaE;
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
     * Retorna la lista listaFuenteRecurso
     * 
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecursoE() {
        return listaFuenteRecursoE;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     * 
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecursoE(
        RegistroDataModelImpl listaFuenteRecursoE) {
        this.listaFuenteRecursoE = listaFuenteRecursoE;
    }

    /**
     * Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebito() {
        return listaCuentaDebito;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebito(RegistroDataModelImpl listaCuentaDebito) {
        this.listaCuentaDebito = listaCuentaDebito;
    }

    /**
     * Retorna la lista listaCuentaDebito
     * 
     * @return listaCuentaDebito
     */
    public RegistroDataModelImpl getListaCuentaDebitoE() {
        return listaCuentaDebitoE;
    }

    /**
     * Asigna la lista listaCuentaDebito
     * 
     * @param listaCuentaDebito
     * Variable a asignar en listaCuentaDebito
     */
    public void setListaCuentaDebitoE(
        RegistroDataModelImpl listaCuentaDebitoE) {
        this.listaCuentaDebitoE = listaCuentaDebitoE;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCredito() {
        return listaCuentaCredito;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCredito(
        RegistroDataModelImpl listaCuentaCredito) {
        this.listaCuentaCredito = listaCuentaCredito;
    }

    /**
     * Retorna la lista listaCuentaCredito
     * 
     * @return listaCuentaCredito
     */
    public RegistroDataModelImpl getListaCuentaCreditoE() {
        return listaCuentaCreditoE;
    }

    /**
     * Asigna la lista listaCuentaCredito
     * 
     * @param listaCuentaCredito
     * Variable a asignar en listaCuentaCredito
     */
    public void setListaCuentaCreditoE(
        RegistroDataModelImpl listaCuentaCreditoE) {
        this.listaCuentaCreditoE = listaCuentaCreditoE;
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

    /**
     * Retorna la lista listaTipoComprobante
     * 
     * @return listaTipoComprobante
     */
    public RegistroDataModelImpl getListaTipoComprobante() {
        return listaTipoComprobante;
    }

    /**
     * Asigna la lista listaTipoComprobante
     * 
     * @param listaTipoComprobante
     * Variable a asignar en listaTipoComprobante
     */
    public void setListaTipoComprobante(
        RegistroDataModelImpl listaTipoComprobante) {
        this.listaTipoComprobante = listaTipoComprobante;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubconfiguracioninterfaz
     * 
     * @return listaSubconfiguracioninterfaz
     */
    public List<Registro> getListaSubconfiguracioninterfaz() {
        return listaSubconfiguracioninterfaz;
    }

    /**
     * Asigna la lista listaSubconfiguracioninterfaz
     * 
     * @param listaSubconfiguracioninterfaz
     * Variable a asignar en listaSubconfiguracioninterfaz
     */
    public void setListaSubconfiguracioninterfaz(
        List<Registro> listaSubconfiguracioninterfaz) {
        this.listaSubconfiguracioninterfaz = listaSubconfiguracioninterfaz;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano
     * the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * @return the desdeAno
     */
    public int getDesdeAno() {
        return desdeAno;
    }

    /**
     * @param desdeAno
     * the desdeAno to set
     */
    public void setDesdeAno(int desdeAno) {
        this.desdeAno = desdeAno;
    }

    // </SET_GET_ADICIONALES>
}
