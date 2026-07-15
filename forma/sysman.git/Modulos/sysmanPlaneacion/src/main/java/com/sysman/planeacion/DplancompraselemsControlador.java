package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.planeacion.ejb.EjbPlaneacionCeroRemote;
import com.sysman.planeacion.enums.DplancompraselemsControladorEnum;
import com.sysman.planeacion.enums.DplancompraselemsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que lista los detalles de plan de adquisiciones por
 * dependencia.
 * 
 * @author dmaldonado
 * @version 1.0, 11/02/2016
 * 
 * @author jrodrigueza
 * @version 2.0, 19/09/2017 Proceso de Refactoring.
 */
@ManagedBean
@ViewScoped
public class DplancompraselemsControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo auxiliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Implementacion del EJB de EjbPlaneacionCeroRemote para hacer el
     * llamado a las funciones y procedimientos que se invocan dentro
     * del Controlador y se encuentran almacenadas en el paquete
     * PCK_PLANEACION
     */
    @EJB
    private EjbPlaneacionCeroRemote ejbPlaneacionCero;
    /**
     * Atributo que contiene las llaves de la tabla asociada al
     * formulario de Plan de Compras por dependencia.
     */
    private Map<String, Object> ridPC;
    /**
     * Indica si hay un registro activo, es decir, si se est&aacute;
     * editando un registro existente en el formulario continuo.
     */
    private boolean registroActivo;
    /**
     * Dependencia asociada al Plan de Compras.
     */
    private String dependenciaPC;
    /**
     * A&ntilde;o asociado al Plan de Compras.
     */
    private String anoPC;
    /**
     * Nombre de la dependencia asociada al Plan de Compras.
     */
    private String nombreDependenciaPC;
    /**
     * C&oacute;digo asociado al Plan de Compras.
     */
    private String codigoPC;
    /**
     * Valor programado para el Plan de Compras.
     */
    private double programadoPC;
    /**
     * Valor asignado al Plan de Compras.
     */
    private double asignadoPC;
    /**
     * Valor aprobado para el Plan de Compras.
     */
    private Boolean aprobadoPC;
    /**
     * Texto mostrado en el encabezado de la tabla de datos.
     */
    private String tituloSub;
    /**
     * Cadena con el valor programado por dependencia mostrado en el
     * pie de la tabla de datos.
     */
    private String txtTotalAComprar;
    /**
     * Valor total programado por dependencia.
     */
    private double totalAComprar;
    /**
     * Cantidad ingresada en el di&aacute;logo de nuevo registro.
     */
    private Object cantidad;
    /**
     * Cantidad ingresada en el formulario continuo.
     */
    private Object cantidadCont;
    /**
     * Valor unitario ingresado en el di&aacute;logo de nuevo
     * registro.
     */
    private Object unitario;
    /**
     * Valor unitario ingresado en el formulario continuo.
     */
    private Object unitarioCont;
    /**
     * Copia de la cantidad asociada al registro seleccionado del
     * formulario continuo.
     */
    private Double cantInicial;
    /**
     * Copia del valor unitario asociado al registro seleccionado del
     * formulario continuo.
     */
    private Double unitInicial;
    /**
     * Sucursal del responsable asociado al registro activo.
     */
    private String sucursalC;
    /**
     * Nombre del elemento seleccionado.
     */
    private Object nombreElemento;
    /**
     * Valor unitario del elemento seleccionado.
     */
    private Object valorUnitarioElem;
    /**
     * Identifica la accion con la que se cargo el registro en el
     * encabezado del plan de adquisiciones.
     */
    private String accionEncabezado;
    /**
     * C&oacute;digo del responsable asociado al plan de compras.
     */
    private String responsable;
    /**
     * Sucursal del responsable asociado al plan de compras.
     */
    private String sucursal;
    /**
     * Codigo de la fuente de recursos
     */
    private String fuenteR;
    /**
     * Codigo de la referencia
     */
    private String referencia;
    /**
     * Codigo del centro de costo
     */
    private String centroC;
    /**
     * Codigo del auxiliar
     */
    private String auxiliarPC;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>
    /**
     * Lista de tipos de contrataci&oacute;n.
     */
    private List<Registro> listaTipoContratacion;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de c&oacute;digos.
     */
    private RegistroDataModelImpl listaCodigo;
    /**
     * Lista de c&oacute;digos. Externo.
     */
    private RegistroDataModelImpl listaCodigoE;
    /**
     * Lista de responsables.
     */
    private RegistroDataModelImpl listaResponsable;
    /**
     * Lista de responsables. Externo.
     */
    private RegistroDataModelImpl listaResponsableE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de DplancompraselemsControlador
     */
    public DplancompraselemsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.DPLANCOMPRASELEMS_CONTROLADOR
                            .getCodigo();
            indicadorClonarPermisos = true;
            validarPermisos();
            // <INI_ADICIONAL>
            traerParametrosEntrada();
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
        enumBase = GenericUrlEnum.DETALLE_PLAN_COMPRAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        // <CARGAR_LISTA>
        cargarListaTipoContratacion();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaResponsable();
        cargarListaResponsableE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        cantInicial = 0.0;
        unitInicial = 0.0;
        calcularTotal();
        calcularProgramado();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoPC);
        parametrosListado.put(GeneralParameterEnum.RUBRO.getName(), codigoPC);
        parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependenciaPC);
        parametrosListado.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
               fuenteR);
        parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(),
                referencia);
        parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                centroC);
        parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
                auxiliarPC);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaTipoContratacion
     */
    public void cargarListaTipoContratacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL5995
                        .getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            listaTipoContratacion = RegistroConverter
                            .toListRegistro(requestManager.getList(url, param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigo() {
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL6472
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DplancompraselemsControladorEnum.CODIGOELEMENTO
                                        .getValue());
    }

    /**
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigoE() {
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL6472
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, DplancompraselemsControladorEnum.CODIGOELEMENTO
                                        .getValue());
    }

    /**
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable() {
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL8810
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependenciaPC);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        DplancompraselemsControladorEnum.CEDULA.getValue());
    }

    /**
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableE() {
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL8810
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependenciaPC);

        listaResponsableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        DplancompraselemsControladorEnum.CEDULA.getValue());
    }
    // </METODOS_CARGAR_LISTA>

    // <METODOS_BOTONES>
    /**
     * Metodo invocado al ejecutar el comando remoto focusCantidad en
     * la vista
     */
    public void ejecutarfocusCantidad() {
        // <CODIGO_DESARROLLADO>
        tomarValoresFocus();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo invocado al ejecutar el comando remoto focusVlrUnitario
     * en la vista
     */
    public void ejecutarfocusVlrUnitario() {
        // <CODIGO_DESARROLLADO>
        tomarValoresFocus();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_BOTONES>

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Valor_Unitario
     */
    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        boolean rta = validarProgramado(registro,
                        DplancompraselemsControladorEnum.VALOR_UNITARIO
                                        .getValue());
        if (rta) {
            calcularValorAComprar(registro);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Codigo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(DplancompraselemsControladorEnum.NOMBRELARGO
                                        .getValue(), nombreElemento);
        if (valorUnitarioElem == null
            || "".equals(extraerString(valorUnitarioElem))
            || "0".equals(extraerString(valorUnitarioElem))) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            DplancompraselemsControladorEnum.VALOR_UNITARIO
                                            .getValue(),
                            ultimoValorElemento(Integer.parseInt(anoPC),
                                            extraerString(listaInicial
                                                            .getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get("CODIGO"))));
            cambiarValorUnitarioC(rowNum);
            return;
        }
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                        .getValue(), valorUnitarioElem);
        cambiarValorUnitarioC(rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorUnitario en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorUnitarioC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        boolean rta = validarProgramado(
                        listaInicial.getDatasource().get(rowNum % 10),
                        DplancompraselemsControladorEnum.VALOR_UNITARIO
                                        .getValue());
        if (rta) {
            calcularValorAComprar(
                            listaInicial.getDatasource().get(rowNum % 10));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     * 
     */
    public void cambiarCantidadC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        boolean rta = validarProgramado(
                        listaInicial.getDatasource().get(rowNum % 10),
                        GeneralParameterEnum.CANTIDAD.getName());
        if (rta) {
            calcularValorAComprar(
                            listaInicial.getDatasource().get(rowNum % 10));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     * 
     */
    public void cambiarResponsableC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        sucursalC);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad en la fila
     * seleccionada dentro de la grilla
     */
    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        boolean rta = validarProgramado(registro,
                        GeneralParameterEnum.CANTIDAD.getName());
        if (rta) {
            calcularValorAComprar(registro);
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()));
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.NOMBRELARGO.getValue(),
                        registroAux.getCampos()
                                        .get(DplancompraselemsControladorEnum.NOMBRELARGO
                                                        .getValue()));
        tomarValoresFocus();
        registro.getCampos().put(DplancompraselemsControladorEnum.VALOR_UNITARIO
                        .getValue(),
                        registroAux.getCampos()
                                        .get(DplancompraselemsControladorEnum.MAXVALOR
                                                        .getValue()));
        cambiarValorUnitario();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGOELEMENTO.getName()));
        nombreElemento = registroAux.getCampos()
                        .get(DplancompraselemsControladorEnum.NOMBRELARGO
                                        .getValue());
        valorUnitarioElem = registroAux.getCampos().get(
                        DplancompraselemsControladorEnum.MAXVALOR.getValue());
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(DplancompraselemsControladorEnum.CEDULA
                                                        .getValue()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos()
                        .get(DplancompraselemsControladorEnum.CEDULA
                                        .getValue()));
        sucursalC = extraerString(registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()));
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
        registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.VALOR_UNITARIO
                        .getValue(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.VALORACOMPRAR
                        .getValue(), 0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.COMPRADO.getValue(),
                        0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.VALORTOTALCOMPRADO
                                        .getValue(), 0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.ENTRADAS.getValue(),
                        0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.SALIDAS.getValue(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.SALDO_CANTIDAD
                        .getValue(), 0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.SALDO_TOTALACOMPRAR
                                        .getValue(), 0);
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        responsable);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        // Permisos de adicion, eliminacion y edicion
        if (aprobadoPC || ACCION_VER.equals(accionEncabezado)) {
            permisos[0] = false;
            permisos[1] = false;
            permisos[2] = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
        registroActivo = false;
        cantInicial = 0.0;
        unitInicial = 0.0;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (aprobadoPC) {
            String mensaje = idioma
                            .getString(DplancompraselemsControladorEnum.TB_TB3615
                                            .getValue());
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ANO_PC
                                            .getValue(), anoPC);
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ACCION
                                            .getValue(), "insertar");
            JsfUtil.agregarMensajeError(mensaje);
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anoPC);
        registro.getCampos().put(GeneralParameterEnum.RUBRO.getName(),
                        codigoPC);
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependenciaPC);
        registro.getCampos().put("FUENTE_DE_RECURSOS", fuenteR);
        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), 
        				referencia);
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), 
        				centroC);
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), 
        				auxiliarPC);
        ///
        registro.getCampos().remove("NOMBRELARGO");
        registro.getCampos().remove("NOMBREMES");
        registro.getCampos().remove("MODALIDAD_CONTRATACION");
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        
        try {
        long consecutivo;
        String condicion = " COMPANIA = ''" + compania + "'' "
            + "AND ANO = ''" + anoPC + "'' "
            + "AND RUBRO = ''" + registro.getCampos().get("RUBRO") + "'' "
            + "AND DEPENDENCIA = ''" + dependenciaPC + "'' "
            + "AND FUENTE_DE_RECURSOS = ''" + fuenteR + "'' "
            + "AND REFERENCIA = ''" + referencia + "'' "
            + "AND CENTRO_COSTO = ''" +  centroC + "'' "
            + "AND AUXILIAR = ''" + auxiliarPC + "'' ";
            
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
			                GenericUrlEnum.DETALLE_PLAN_COMPRAS.getTable(),
			                condicion, "CONSECUTIVO", "1");
			
			registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), consecutivo);
			
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.VALOR_UNITARIO
                        .getValue(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.VALORACOMPRAR
                        .getValue(), 0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.COMPRADO.getValue(),
                        0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.VALORTOTALCOMPRADO
                                        .getValue(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.SALDO_CANTIDAD
                        .getValue(), 0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.SALDO_TOTALACOMPRAR
                                        .getValue(), 0);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (aprobadoPC) {
            String mensaje = idioma
                            .getString(DplancompraselemsControladorEnum.TB_TB3615
                                            .getValue());
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ANO_PC
                                            .getValue(), anoPC);
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ACCION
                                            .getValue(), "actualizar");
            JsfUtil.agregarMensajeError(mensaje);
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        cantInicial = 0.0;
        unitInicial = 0.0;
        calcularProgramado();
        calcularTotal();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if (aprobadoPC) {
            String mensaje = idioma
                            .getString(DplancompraselemsControladorEnum.TB_TB3615
                                            .getValue());
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ANO_PC
                                            .getValue(), anoPC);
            mensaje = mensaje
                            .replace(DplancompraselemsControladorEnum.VAR_ACCION
                                            .getValue(), "eliminar");
            JsfUtil.agregarMensajeError(mensaje);
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(DplancompraselemsControladorEnum.NOMBRELARGO
                        .getValue());
        registro.getCampos().remove(
                        DplancompraselemsControladorEnum.NOMBREMES.getValue());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.RUBRO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(GeneralParameterEnum.DEPENDENCIA.getName());
        registro.getCampos()
                        .remove(DplancompraselemsControladorEnum.MODALIDAD_CONTRATACION
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        registroActivo = true;
        cantInicial = Double.valueOf(
                        extraerString(registro.getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName())));
        unitInicial = Double.valueOf(extraerString(
                        registro.getCampos()
                                        .get(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                                        .getValue())));
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("ridPC", ridPC);
        parametrosEntrada.put("programadoPC", totalAComprar);
        parametrosEntrada.put("accion", accionEncabezado);
        direccionador.setParametros(parametrosEntrada);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLANDECOMPRASELEMS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.COMPRADO.getValue(),
                        0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.VALORTOTALCOMPRADO
                                        .getValue(), 0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.ENTRADAS.getValue(),
                        0);
        registro.getCampos().put(
                        DplancompraselemsControladorEnum.SALIDAS.getValue(), 0);
        registro.getCampos()
                        .put(DplancompraselemsControladorEnum.SALDO_TOTALACOMPRAR
                                        .getValue(), 0);
        registro.getCampos().put(DplancompraselemsControladorEnum.SALDO_CANTIDAD
                        .getValue(), 0);
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        responsable);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * @return the tituloSub
     */
    public String getTituloSub() {
        return tituloSub;
    }

    /**
     * @param tituloSub
     * the tituloSub to set
     */
    public void setTituloSub(String tituloSub) {
        this.tituloSub = tituloSub;
    }

    /**
     * @return the txtTotalAComprar
     */
    public String getTxtTotalAComprar() {
        return txtTotalAComprar;
    }

    /**
     * @param txtTotalAComprar
     * the txtTotalAComprar to set
     */
    public void setTxtTotalAComprar(String txtTotalAComprar) {
        this.txtTotalAComprar = txtTotalAComprar;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }
    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoContratacion
     * 
     * @return listaTipoContratacion
     */
    public List<Registro> getListaTipoContratacion() {
        return listaTipoContratacion;
    }

    /**
     * Asigna la lista listaTipoContratacion
     * 
     * @param listaTipoContratacion
     * Variable a asignar en listaTipoContratacion
     */
    public void setListaTipoContratacion(List<Registro> listaTipoContratacion) {
        this.listaTipoContratacion = listaTipoContratacion;
    }
    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigoE() {
        return listaCodigoE;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsableE() {
        return listaResponsableE;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE) {
        this.listaResponsableE = listaResponsableE;
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

    // <METODOS_ADICIONALES>
    /**
     * Trae los par&aacute;metros enviados desde el formulario de Plan
     * de Compras por Dependencia.
     * 
     * @see PlandecompraselemsControlador
     */
    private void traerParametrosEntrada() {
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            dependenciaPC = extraerString(
                            parametrosEntrada.get("dependenciaPC"));
            anoPC = extraerString(parametrosEntrada.get("anoPC"));
            nombreDependenciaPC = extraerString(
                            parametrosEntrada.get("nombreDependenciaPC"));
            ridPC = (Map<String, Object>) parametrosEntrada.get("ridPC");
            codigoPC = extraerString(parametrosEntrada.get("codigoPC"));
            programadoPC = Double.valueOf(extraerString(
                            parametrosEntrada.get("programadoPC")));
            asignadoPC = Double.valueOf(
                            extraerString(parametrosEntrada.get("asignadoPC")));
            aprobadoPC = Boolean.valueOf(extraerString(extraerString(
                            parametrosEntrada.get("aprobadoPC"))));
            accionEncabezado = extraerString(parametrosEntrada.get("accion"));
            responsable = extraerString(parametrosEntrada.get("responsablePC"));
            sucursal = extraerString(parametrosEntrada.get("sucursalPC"));
            fuenteR = extraerString(parametrosEntrada.get("fuenteR"));
            referencia = extraerString(parametrosEntrada.get("referencia"));
            centroC = extraerString(parametrosEntrada.get("centroC"));
            auxiliarPC = extraerString(parametrosEntrada.get("auxiliar"));
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            "Debe ingresar nuevamente al formulario. Ó_Ò");
            SessionUtil.redireccionarMenu();
        }
        tituloSub = "Detalle Plan de Compras de la dependencia ("
            + dependenciaPC
            + ") " + nombreDependenciaPC + " para el rubro " + codigoPC;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Extrae el BigDecimal que representa el objeto.
     * 
     * @param object
     * Un Objeto
     * @return objeto como BigDecimal
     */
    private BigDecimal extraerDecimal(Object object) {
        if (object == null) {
            return new BigDecimal(0);
        }
        if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        else {
            return new BigDecimal(extraerString(object));
        }
    }

    /**
     * Método que carga los valores del registro en el momento en que
     * se selecciona alguno de los campos de cantidad o de valor
     * unitario.
     */
    public void tomarValoresFocus() {
        if (indice > 0) {
            cantidadCont = listaInicial.getDatasource().get(indice).getCampos()
                            .get(GeneralParameterEnum.CANTIDAD.getName());
            unitarioCont = listaInicial.getDatasource().get(indice).getCampos()
                            .get(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                            .getValue());
            cantidad = 0;
            unitario = 0;
        }
        else {
            cantidadCont = 0;
            unitarioCont = 0;
            cantidad = registro.getCampos()
                            .get(GeneralParameterEnum.CANTIDAD.getName());
            unitario = registro.getCampos()
                            .get(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                            .getValue());
        }
    }

    /**
     * Trae el &uacute;ltimo valor del elemento en el detalle del Plan
     * de Compras.
     * 
     * @param ano
     * A&ntilde;o del plan de compras.
     * @param elemento
     * C&oacute;digo del elemento.
     * @return &Uacute;ltimo valor unitario asociado al elemento en el
     * plan de compras.
     */
    public double ultimoValorElemento(int ano, String elemento) {
        BigDecimal valorUnitario = BigDecimal.ZERO;
        try {
            valorUnitario = ejbPlaneacionCero.traerUltimoValorElemento(compania,
                            ano, elemento);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        if (valorUnitario == null) {
        	return 0;
        } else {
        	return valorUnitario.doubleValue();
        }
    }

    /**
     * Calcula el valor a comprar.
     * 
     * @param reg
     */
    public void calcularValorAComprar(Registro reg) {
        BigDecimal cantidadPC = extraerDecimal(reg.getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName()));
        BigDecimal valorUnitario = extraerDecimal(reg.getCampos()
                        .get(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                        .getValue()));
        BigDecimal valorAComprar = cantidadPC.multiply(valorUnitario);
        reg.getCampos().put(DplancompraselemsControladorEnum.VALORACOMPRAR
                        .getValue(), valorAComprar);
    }

    /**
     * Verifica que el valor programado no sobrepase el valor
     * asignado.
     * 
     * @param reg
     * Registro
     * @param key
     * Nombre del campo
     * @return falso si el valor programado sobrepasa al asignado.
     */
    public boolean validarProgramado(Registro reg, String key) {
        double programadoInicial = cantInicial * unitInicial;
        Object object = reg.getCampos()
                        .get(DplancompraselemsControladorEnum.VALOR_UNITARIO
                                        .getValue());
        double valorUnitario = extraerDecimal(object).doubleValue();
        object = reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName());
        double cant = extraerDecimal(object).doubleValue();
        double vlrProgramado = programadoPC - programadoInicial;

        if ((Math.round((valorUnitario * cant) + vlrProgramado) > asignadoPC)
            || (Math.round(totalAComprar) > asignadoPC)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3626"));
            if (GeneralParameterEnum.CANTIDAD.getName().equals(key)) {
                reg.getCampos().put(key,
                                registroActivo ? cantidadCont : cantidad);
            }
            else if (DplancompraselemsControladorEnum.VALOR_UNITARIO.getValue()
                            .equals(key)) {
                reg.getCampos().put(key,
                                registroActivo ? unitarioCont : unitario);
            }
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Calcula el valor programado en el plan de compras para el rubro
     * asociado.
     */
    public void calcularProgramado() {
        BigDecimal valorProgramado = BigDecimal.ZERO;
        try {
            valorProgramado = ejbPlaneacionCero.calcularValorProgramado(
                            compania, Integer.parseInt(anoPC), codigoPC,
                            fuenteR, referencia, centroC, auxiliarPC);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        programadoPC = valorProgramado.doubleValue();
    }

    /**
     * Calcula el valor programado por dependencia, que ser&aacute;
     * mostrado en el pie del formulario continuo.
     */
    public void calcularTotal() {
        String urlEnumId = DplancompraselemsControladorUrlEnum.URL33283
                        .getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependenciaPC);
        params.put(GeneralParameterEnum.RUBRO.getName(), codigoPC);
        params.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                fuenteR);
        params.put(GeneralParameterEnum.REFERENCIA.getName(),
                 referencia);
        params.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                 centroC);
        params.put(GeneralParameterEnum.AUXILIAR.getName(),
                 auxiliarPC);
        Parameter parameter = null;
        try {
            parameter = requestManager.get(url, params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        Registro detallePC = RegistroConverter.toRegistro(parameter);
        if (detallePC != null) {
            DecimalFormat dblDF = new DecimalFormat("##,###.00");
            totalAComprar = Double
                            .valueOf(detallePC.getCampos()
                                            .get(DplancompraselemsControladorEnum.SUMA
                                                            .getValue())
                                            .toString());
            txtTotalAComprar = dblDF.format(totalAComprar);
        }
        else {
            DecimalFormat dblDF = new DecimalFormat("##,###.00");
            totalAComprar = 0;
            txtTotalAComprar = dblDF.format(totalAComprar);
        }
    }
    // </METODOS_ADICIONALES>

}
