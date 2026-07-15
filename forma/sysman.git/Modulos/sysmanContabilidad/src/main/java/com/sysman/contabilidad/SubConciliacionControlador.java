package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.enums.SubConciliacionControladorEnum;
import com.sysman.contabilidad.enums.SubConciliacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite visualizar los movimientos del banco en el periodo seleccionado, con el fin de iniciar el proceso de conciliacion.
 *
 * @author jrodrigueza
 * @version 1, 14/04/2016
 * 
 * @version 2, 17/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos, en el origen de grilla.
 * 
 * @version 3, 28/04/2017
 * @author jrodrigueza Ajuste de origen de datos y lista de documentos para filtrar por auxiliares en caso de que la cuenta maneje cada uno de estos.
 */
@ManagedBean
@ViewScoped
public class SubConciliacionControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consTipoCpte;
    private final String consFechaConcilia;
    private final String consComprobante;
    // <DECLARAR_ATRIBUTOS>
    private String auxiliar;
    private String clasesContables;
    private String codCuenta;
    private String cuenta;
    private String documento;
    private String nombreMes;
    private String ultimoDia;
    private String dialogoMensaje;
    private boolean concilVisible;
    private int ano;
    private int mes;
    /**
     * Indica si los campos Pagado Banco, Fecha Conciliacion y el combo Documento deben bloquearse.
     */
    private boolean periodoCerrado;
    /**
     * Indica si los campos Sin Identificar y Fecha Identificacion no deben bloquearse.
     */
    private boolean reportaInformes;
    /**
     * Indica si se debe mostrar o no, el campo Fecha de Consignacion.
     */
    private boolean verFechaConsignacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTipoComprobante;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaDocumento;
    private RegistroDataModel listaDocumentoE;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModel listaTerceroE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbContabilidadSeisRemote ejbContabilidadSeis;
    private Object tercero;
    private Object sucursal;
    private Object centroCosto;
    private Object auxiliarConc;
    private Object fuenteRecurso;
    private Object referencia;

    /**
     * Crea una nueva instancia de SubConciliacionControlador
     */
    public SubConciliacionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        consTipoCpte = "TIPO_CPTE";
        consFechaConcilia = "FECHA_CONCILIA";
        consComprobante = "COMPROBANTE";
        concilVisible = false;
        dialogoMensaje = idioma.getString("TB_TB4172");
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUB_CONCILIACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                ano = (int) parametros.get("ano");
                mes = (int) parametros.get("mes");
                codCuenta = parametros.get("codCuenta").toString();
                cuenta = codCuenta + " " + parametros.get("nombreCuenta");
                tercero = parametros.get("tercero");
                sucursal = parametros.get("sucursal");
                centroCosto = parametros.get("centroCosto");
                auxiliarConc = parametros.get("auxiliar");
                fuenteRecurso = parametros.get("fuenteRecurso");
                referencia = parametros.get("referencia");
            }
            nombreMes = SysmanFunciones.initCap(new SimpleDateFormat("MMMM", new Locale("es")).format(ultimoDiaPeriodo()));

            ultimoDia = new SimpleDateFormat("dd/MM/yyyy")
                            .format(ultimoDiaPeriodo());
        }
        catch (Exception ex)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        clasesContables = getClasesContables();
        tabla = SubConciliacionControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaTipoComprobante();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDocumento();
        cargarListaTercero();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        SubConciliacionControladorUrlEnum.URL15084.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubConciliacionControladorUrlEnum.URL9050
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(SubConciliacionControladorEnum.PARAM12.getValue(),
                        ano);
        parametrosListado.put(SubConciliacionControladorEnum.PARAM0.getValue(),
                        mes);
        parametrosListado.put(GeneralParameterEnum.CUENTA.getName(), codCuenta);
        parametrosListado.put(SubConciliacionControladorEnum.PARAM1.getValue(),
                        ultimoDia);
        parametrosListado.put(SubConciliacionControladorEnum.PARAM2.getValue(),
                        clasesContables);
        parametrosListado.put(SubConciliacionControladorEnum.PARAM11.getValue(),
                        tercero);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCosto);
        parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
                        auxiliarConc);
        parametrosListado.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        fuenteRecurso);
        parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(),
                        referencia);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaTipoComprobante = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubConciliacionControladorUrlEnum.URL6076
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDocumento
     *
     */
    public void cargarListaDocumento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubConciliacionControladorUrlEnum.URL6368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubConciliacionControladorEnum.PARAM1.getValue(), ultimoDia);
        param.put(GeneralParameterEnum.CUENTA.getName(), codCuenta);
        param.put(SubConciliacionControladorEnum.PARAM2.getValue(),
                        clasesContables);
        param.put(SubConciliacionControladorEnum.PARAM0.getValue(), mes);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroCosto);
        param.put(GeneralParameterEnum.AUXILIAR.getName(), auxiliarConc);
        param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(), fuenteRecurso);
        param.put(GeneralParameterEnum.REFERENCIA.getName(), referencia);

        listaDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consComprobante);
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubConciliacionControladorUrlEnum.URL8222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton indicadorEstConcil en la vista
     *
     *
     */
    public void oprimirindicadorEstConcil() {
    	
        if (periodoCerrado) {
            JsfUtil.agregarMensajeError("Proceso no permitido. El período de conciliación está cerrado");
            return; // No ejecutar si el periodo está cerrado o la conciliación no está permitida
        }
        
        concilVisible = true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BorrarSeleccion en la vista
     *
     *
     */
    public void oprimirBorrarSeleccion()
    {
        // <CODIGO_DESARROLLADO>
    	
    	 if (periodoCerrado) {
    		   JsfUtil.agregarMensajeError("Proceso no permitido. El período de conciliación está cerrado");
    	        return; // No ejecutar si el periodo está cerrado o la conciliación no está permitida
    	    }
    	    
        try
        {
            ejbContabilidadSeis.actualizarEstadoC(compania, ano, mes, codCuenta,
                            ultimoDiaPeriodo(),
                            SessionUtil.getUser().getCodigo(),
                            getClasesContables(), false);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo dgConfirmacion en la vista
     *
     *
     */
    public void aceptardgConfirmacion()
    {
        concilVisible = false;

        try
        {
            ejbContabilidadSeis.actualizarEstadoC(compania, ano, mes, codCuenta,
                            ultimoDiaPeriodo(),
                            SessionUtil.getUser().getCodigo(),
                            getClasesContables(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Trae la fecha de conciliacion y el usuario conciliador cuando se activa el indicador de pagadoBanco.
     *
     * @param rowNum
     */
    public void cambiarPagadoBancoC(int rowNum)
    {
        /*
         * Para el cambio en una fila selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA",
         * "hola ")
         */
        // PagadoBanco_AfterUpdate
        boolean pagadoBanco = (boolean) listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get("PAGADOBANCO");
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        consFechaConcilia,
                        pagadoBanco ? ultimoDiaPeriodo() : null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "CONCILIADOR",
                        pagadoBanco ? SessionUtil.getUser().getCodigo()
                                        : null);
    }

    /**
     * Verifica el estado de la conciliacion del mes seleccionado en el periodo.
     *
     * @param mes
     * @return <code>true</code> si el mes esta cerrado.
     */
    private boolean esMesCerrado(int mes)
    {
        Registro reg = null;
        boolean salida = true;
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(SubConciliacionControladorEnum.PARAM0.getValue(), mes);
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubConciliacionControladorUrlEnum.URL9545
                                                                            .getValue())
                                            .getUrl(), param));
            salida = "C".equals(reg.getCampos().get("ESTADO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return salida;
    }

    /**
     * Auditoria para establecer cambios en las fechas de las conciliaciones.
     */
    private void insertaAuditoria()
    {
        Map<String, Object> campos = new HashMap<>();

        campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        campos.put(GeneralParameterEnum.CONSECUTIVO.getName(), genConsecutivo(
                        "AUDITORIA_CONCILIACION", "CONSECUTIVO", null));
        campos.put(GeneralParameterEnum.CUENTA.getName(), codCuenta);
        campos.put(SubConciliacionControladorEnum.PARAM4.getValue(), mes);
        campos.put(SubConciliacionControladorEnum.PARAM5.getValue(), ano);
        campos.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                        registro.getCampos().get(consTipoCpte));
        campos.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(consComprobante));
        campos.put(SubConciliacionControladorEnum.PARAM6.getValue(),
                        registro.getCampos().get(consFechaConcilia));
        campos.put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        campos.put(GeneralParameterEnum.DATE_CREATED.getName(),
                        Calendar.getInstance().getTime());
        campos.put(GeneralParameterEnum.ANO.getName(),
                        Calendar.getInstance().get(Calendar.YEAR));
        campos.put(GeneralParameterEnum.TERCERO.getName(),
                        SysmanConstantes.CONS_TERCERO);
        campos.put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanConstantes.CONS_SUCURSAL);
        campos.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        SysmanConstantes.CONS_CENTRO);
        campos.put(GeneralParameterEnum.AUXILIAR.getName(),
                        SysmanConstantes.CONS_AUXILIAR);
        campos.put(GeneralParameterEnum.REFERENCIA.getName(),
                        SysmanConstantes.CONS_REFERENCIA);
        campos.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        SysmanConstantes.CONS_FUENTE);
        Parameter parameter = new Parameter();
        parameter.setFields(campos);
        try
        {
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubConciliacionControladorUrlEnum.URL15847
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Genera el consecutivo segun los parametros ingresados.
     *
     * @param tabla
     * nombre de la tabla
     * @param campo
     * nombre del campo
     * @param condicion
     * @return entero que representa el consecutivo.
     */
    private long genConsecutivo(String tabla, String campo, String condicion)
    {
        try
        {
            return ejbSysmanUtilRemote.generarConsecutivoConValorInicial(
                            tabla, condicion == null
                                            ? "COMPANIA=''" + compania + "''"
                                            : condicion,
                            campo, "1");
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return -1;
        }
    }

    /**
     * Trae las clases contables para conciliacion bancaria.
     *
     * @return cadena con las clases separadas por coma.
     */
    private String getClasesContables()
    {
        String clase = "";
        try
        {
            clase = SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "CLASES CONTABLES EN CONCILIACION BANCARIA",
                                            SessionUtil.getModulo(), new Date(),
                                            false),
                            "").toString();
            if (clase.isEmpty())
            {
                insertarParametro();
            }
            clase = SysmanFunciones.separarCaracteres(clase, ",");
            // clase = "'" + clase + "'";
        }
        catch (NamingException | SQLException | SystemException e)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return clase;
    }

    /**
     * Insercion del parametro <i>CLASES CONTABLES EN CONCILIACION BANCARIA</i> en caso de que el no exista en la base de datos.
     *
     * @throws SQLException
     * @throws NamingException
     */
    private void insertarParametro() throws SQLException, NamingException
    {
        try
        {
            Map<String, Object> campos = new HashMap<>();
            campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            campos.put(GeneralParameterEnum.NOMBRE.getName(),
                            "CLASES CONTABLES EN CONCILIACION BANCARIA");
            Calendar calendar = Calendar.getInstance();
            calendar.set(2000, 1, 1);
            campos.put(SubConciliacionControladorEnum.PARAM7.getValue(),
                            calendar.getTime());
            campos.put(GeneralParameterEnum.VALOR.getName(), "SEBGDALI");
            campos.put(GeneralParameterEnum.DESCRIPCION.getName(),
                            "PARAMETRO PARA FILTRAR TIPOS DE COMPROBANTES QUE APARECERAN EN CONCILIACION BANCARIA");
            campos.put(SubConciliacionControladorEnum.PARAM8.getValue(), "1");
            campos.put(SubConciliacionControladorEnum.PARAM9.getValue(), -1);
            campos.put(SubConciliacionControladorEnum.PARAM10.getValue(), 0);
            campos.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            campos.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            Calendar.getInstance().getTime());
            Parameter parameter = new Parameter();
            parameter.setFields(campos);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubConciliacionControladorUrlEnum.URL20100
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Trae el ultimo dia del periodo de conciliacion seleccionado.
     *
     * @return fecha que representa el ultimo dia segun el mes y anio
     */
    private Date ultimoDiaPeriodo()
    {
        Date fechaU = null;
        try
        {
            Date fec = SysmanFunciones.convertirAFecha("01/" + mes + "/" + ano);
            fechaU = SysmanFunciones.ultimoDiaDate(fec);

        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return fechaU;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocumento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        documento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NRO_DOCUMENTO"), " ")
                        .toString();
        String comprobante = SysmanFunciones
                        .nvl(registroAux.getCampos().get(consTipoCpte), " ")
                        .toString()
                        + " "
                        + SysmanFunciones.nvl(registroAux.getCampos().get(consComprobante),
                                        " ").toString();

        try
        {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(SubConciliacionControladorEnum.PARAM1.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            ultimoDiaPeriodo()));
            parametros.put(SubConciliacionControladorEnum.PARAM3.getValue(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(GeneralParameterEnum.ANO.getName(),
                            registroAux.getCampos().get("ANO"));
            parametros.put(GeneralParameterEnum.TIPO_CPTE.getName(),
                            registroAux.getCampos().get(consTipoCpte));
            parametros.put(GeneralParameterEnum.COMPROBANTE.getName(),
                            registroAux.getCampos().get(consComprobante));
            parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            registroAux.getCampos().get("CONSECUTIVO"));
            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubConciliacionControladorUrlEnum.URL9045
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB608") + " " + comprobante);
        }
        catch (SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDocumentoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NRO_DOCUMENTO"), " ")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        periodoCerrado = esMesCerrado(mes);
        if (periodoCerrado)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB609") + " " + nombreMes + " "
                                            + idioma.getString("TB_TB610"));
        }
        try
        {
            reportaInformes = "SI".equals(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "REPORTA INFORMES AL MEN",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
            verFechaConsignacion = "SI".equals(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FECHA DE CONSIGNACIï¿½N EN CONCILIACION BANCARIA",
                                            SessionUtil.getModulo(), new Date(),
                                            true));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubConciliacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        if (reportaInformes
                        && (registro.getCampos().get(consFechaConcilia) != null)
                        && (registro.getCampos().get("FECHA_IDENTIFICACION") != null
                                        && ((Date) registro.getCampos().get(consFechaConcilia))
                                                        .before((Date) registro.getCampos().get(
                                                                        "FECHA_IDENTIFICACION"))))
        {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB611"));
            return false;

        }

        registro.getCampos().remove(GeneralParameterEnum.IMPRESO.getName());
        registro.getCampos().remove(
                        SubConciliacionControladorEnum.PARAM13.getValue());
        insertaAuditoria();
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     * 
     * @return true
     */

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable documento
     * 
     * @return documento
     */
    public String getDocumento()
    {
        return documento;
    }

    /**
     * Asigna la variable documento
     * 
     * @param documento
     * Variable a asignar en documento
     */
    public void setDocumento(String documento)
    {
        this.documento = documento;
    }

    /**
     * Retorna la variable cuenta
     * 
     * @return cuenta
     */
    public String getCuenta()
    {
        return cuenta;
    }

    /**
     * Asigna la variable cuenta
     * 
     * @param cuenta
     * Variable a asignar en cuenta
     */
    public void setCuenta(String cuenta)
    {
        this.cuenta = cuenta;
    }

    /**
     * Retorna la variable nombreMes
     * 
     * @return nombreMes
     */
    public String getNombreMes()
    {
        return nombreMes;
    }

    /**
     * Asigna la variable nombreMes
     * 
     * @param nombreMes
     * Variable a asignar en nombreMes
     */
    public void setNombreMes(String nombreMes)
    {
        this.nombreMes = nombreMes;
    }

    public boolean isPeriodoCerrado()
    {
        return periodoCerrado;
    }

    public void setPeriodoCerrado(boolean periodoCerrado)
    {
        this.periodoCerrado = periodoCerrado;
    }

    public boolean isReportaInformes()
    {
        return reportaInformes;
    }

    public void setReportaInformes(boolean reportaInformes)
    {
        this.reportaInformes = reportaInformes;
    }

    public boolean isVerFechaConsignacion()
    {
        return verFechaConsignacion;
    }

    public void setVerFechaConsignacion(boolean verFechaConsignacion)
    {
        this.verFechaConsignacion = verFechaConsignacion;
    }

    public String getDialogoMensaje()
    {
        return dialogoMensaje;
    }

    public void setDialogoMensaje(String dialogoMensaje)
    {
        this.dialogoMensaje = dialogoMensaje;
    }

    public boolean isConcilVisible()
    {
        return concilVisible;
    }

    public void setConcilVisible(boolean concilVisible)
    {
        this.concilVisible = concilVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoComprobante
     * 
     * @return listaTipoComprobante
     */
    public List<Registro> getListaTipoComprobante()
    {
        return listaTipoComprobante;
    }

    /**
     * Asigna la lista listaTipoComprobante
     * 
     * @param listaTipoComprobante
     * Variable a asignar en listaTipoComprobante
     */
    public void setListaTipoComprobante(List<Registro> listaTipoComprobante)
    {
        this.listaTipoComprobante = listaTipoComprobante;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDocumento
     * 
     * @return listaDocumento
     */
    public RegistroDataModelImpl getListaDocumento()
    {
        return listaDocumento;
    }

    /**
     * Asigna la lista listaDocumento
     * 
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumento(RegistroDataModelImpl listaDocumento)
    {
        this.listaDocumento = listaDocumento;
    }

    /**
     * Retorna la lista listaDocumento
     * 
     * @return listaDocumento
     */
    public RegistroDataModel getListaDocumentoE()
    {
        return listaDocumentoE;
    }

    /**
     * Asigna la lista listaDocumento
     * 
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumentoE(RegistroDataModel listaDocumentoE)
    {
        this.listaDocumentoE = listaDocumentoE;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero()
    {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero)
    {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModel getListaTerceroE()
    {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModel listaTerceroE)
    {
        this.listaTerceroE = listaTerceroE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
