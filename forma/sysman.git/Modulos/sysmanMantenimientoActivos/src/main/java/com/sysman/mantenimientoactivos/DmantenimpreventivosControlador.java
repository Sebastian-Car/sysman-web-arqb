package com.sysman.mantenimientoactivos;

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
import com.sysman.mantenimientoactivos.ejb.EjbMantenimientoActivosCeroRemote;
import com.sysman.mantenimientoactivos.enums.DmantenimpreventivosControladorEnum;
import com.sysman.mantenimientoactivos.enums.DmantenimpreventivosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ngomez
 * @version 1, 17/11/2015
 * 
 * @author eamaya
 * @version 2.0, 18/08/2107; Proceso de Refactoring DSS, cambio de
 * numero de formulario por enum y Manejo de EJBs
 * 
 * @author jrodrigueza
 * @version 3.0, 29/08/2017 Adici&oacute;n de campo Kilometraje y
 * cambio de lista sencilla que carga las placas por combo grande.
 * 
 * @author asana
 * @version 4.0 17/10/2018, Para el tipo de comprobante SOL, se permite
 * agregar elementos que contiene una estación solo si parámetro esta en SI.
 */
@ManagedBean
@ViewScoped

public class DmantenimpreventivosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual esta trabajando el usuario,
     */
    private final String modulo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private final String cantidad;
    private final String codElemento;
    private final String fechaFin;
    private final String fechaFinal;
    private final String fechaIni;
    private final String fechaInicial;
    private final String nitTaller;
    private final String nomElemento;
    private final String nomTarea;
    private final String sucursal;
    private final String sucursalRes;
    private final String tipoMant;
    private final String valorTotal;
    private final String valorUnitario;
    private int auxiliarFila;

    private boolean camposBloqueoAut;
    private boolean visibleComponente;
    private boolean camposBloqueoEje;
    private Map<String, Object> rid;
    private String ano;
    private String tipo2;
    private String numero;
    private String pmes;
    private String panio;
    private String tipo;
    private String tipoNombre;
    private String totalMantenimiento;
    private String registroauxNombreElemento;
    private String registroauxSucursal;
    private String registroauxRes;
    private String registroauxSucursalResponsable;
    private String nombreComponente;
    private boolean camposVisible;
    private boolean camposVisibleRes;
    private boolean bloqueaDetalle;
    private boolean aprobado;

    /**
     * Indicador para cargar el campo "Kilometraje"
     * <code>(CP45117)</code>
     */
    private boolean cargarKilometraje;
    /**
     * Lista de elementos.
     */
    private RegistroDataModelImpl listaElemento;
    /**
     * Lista de elementos.
     */
    private RegistroDataModelImpl listaElementoE;
    /**
     * Lista de placas asociadas al elemento seleccionado.
     */
    private RegistroDataModelImpl listaPlaca;
    /**
     * Lista de placas asociadas al elemento seleccionado.
     */
    private RegistroDataModelImpl listaPlacaE;
    /**
     * Lista de talleres.
     */
    private RegistroDataModelImpl listaNitTaller;
    /**
     * Lista de talleres.
     */
    private RegistroDataModelImpl listaNitTallerE;
    /**
     * Lista de Responsables.
     */
    private RegistroDataModelImpl listaResponsable;
    /**
     * Lista de Responsables.
     */
    private RegistroDataModelImpl listaResponsableE;
    /**
     * Lista de Tareas de mantenimiento.
     */
    private List<Registro> listaTareaMantenimiento;

    private RegistroDataModelImpl listaComponente;
    
    private RegistroDataModelImpl listaComponenteE;
    /**
     * EJB para implementar las funcionalidades del paquete
     * PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbMantenimientoActivosCeroRemote ejbMantenimientoActivos;

    /**
     * Creates a new instance of DmantenimpreventivosControlador
     */
    public DmantenimpreventivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cantidad = "CANTIDAD";
        codElemento = "CODIGOELEMENTO";
        nombreComponente = "COMPONENTE";
        fechaFin = "FECHAFIN";
        fechaFinal = "FECHAFINAL";
        fechaIni = "FECHAINI";
        fechaInicial = "FECHAINICIAL";
        nitTaller = "NIT_TALLER";
        nomElemento = "NOMBRELEMENTO";
        nomTarea = "NOMTAREA";
        sucursal = "SUCURSAL";
        sucursalRes = "SUCURSAL_RESPONSABLE";
        tipoMant = "TIPOMANT";
        valorTotal = "VALORTOTAL";
        valorUnitario = "VALORUNITARIO";
        try {
            //343
            numFormulario = GeneralCodigoFormaEnum.DMANTENIMPREVENTIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                ano = extraerString(parametrosEntrada.get("ano"));
                tipo2 = extraerString(parametrosEntrada.get("tipo2"));
                numero = extraerString(parametrosEntrada.get("numero"));
                pmes = extraerString(parametrosEntrada.get("pmes"));
                panio = extraerString(parametrosEntrada.get("panio"));
                tipo = extraerString(parametrosEntrada.get("tipo"));
                tipoNombre = extraerString(parametrosEntrada.get("tipoNombre"));
                totalMantenimiento = new java.text.DecimalFormat("$ #,##0.00")
                                .format(Double.parseDouble(parametrosEntrada
                                                .get("totalMantenimiento")
                                                .toString()));
                camposVisible = Boolean
                                .parseBoolean(extraerString(parametrosEntrada
                                                .get("camposVisible")));
                camposVisibleRes = Boolean
                                .parseBoolean(extraerString(parametrosEntrada
                                                .get("camposVisibleRes")));
                bloqueaDetalle = Boolean
                                .parseBoolean(extraerString(parametrosEntrada
                                                .get("bloqueaDetalle")));
                aprobado = Boolean.parseBoolean(extraerString(
                                parametrosEntrada.get("aprobado")));
            }

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
        enumBase = GenericUrlEnum.D_MANTENIMIENTO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaTareaMantenimiento();
        cargarListaPlaca();
        cargarListaPlacaE();
        cargarListaNitTaller();
        cargarListaNitTallerE();
        cargarListaResponsable();
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
        if (!bloqueaDetalle || aprobado) {
            permisos[0] = false;
            permisos[1] = false;
            permisos[2] = false;
            permisos[3] = true;
            permisos[4] = true;
        }
        else {
            permisos[0] = true;
            permisos[1] = true;
            permisos[2] = true;
            permisos[3] = true;
            permisos[4] = true;
        }
        if ("EJE".equals(tipo) || "AUT".equals(tipo)) {
            permisos[0] = false;
            permisos[1] = false;
        }
        if ("AUT".equals(tipo) || "EJE".equals(tipo)) {
            camposBloqueoAut = true;
        }

        if ("EJE".equals(tipo)) {
            generarTotal();
        }
        
        registro.getCampos().put(nombreComponente, false);
        
        cargarListaElemento();
        cargarListaElementoE();
        
        if ("SOL".equals(tipo)) {
            
           try {
                visibleComponente = "SI".equals(ejbSysmanUtil
                                .consultarParametro(compania,
                                                "MANEJA ESTACIONES EN MANTENIMIENTO ACTIVOS",
                                                modulo, new Date(), true));
            }
           catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
        } else {
            visibleComponente = false;
        }

        try {
            cargarKilometraje = "SI".equals(ejbSysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA KILOMETRAJE MANTENIMIENTO ACTIVOS",
                                            modulo, new Date(), true))
                                                ? true : false;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put("TIPO", tipo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
    }

    /**
     * 
     * Carga la lista listaTareaMantenimiento
     */
    public void cargarListaTareaMantenimiento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTareaMantenimiento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DmantenimpreventivosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaElemento
     */
    public void cargarListaElemento() {
        
        UrlBean urlBean;
        
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), nombreComponente) || !(boolean) registro.getCampos().get(nombreComponente) ) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DmantenimpreventivosControladorUrlEnum.URL15241
                                                            .getValue());
        } else {
           
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DmantenimpreventivosControladorUrlEnum.URL002
                                            .getValue());
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        
        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codElemento);
        registro.getCampos().put(codElemento, null);
    }

    /**
     * Carga la lista listaElemento
     */
    public void cargarListaElementoE() {
        
        UrlBean urlBean;
        
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), nombreComponente) || !(boolean) registro.getCampos().get(nombreComponente) ) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DmantenimpreventivosControladorUrlEnum.URL002
                                                            .getValue());
        } else {
           
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DmantenimpreventivosControladorUrlEnum.URL15241
                                            .getValue());
            
        }
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codElemento);
        
       registro.getCampos().put(codElemento, null);
    }

    /**
     * Carga la lista listaPlaca
     */
    public void cargarListaPlaca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL16224
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        registro.getCampos().get(codElemento));
        listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }
    
    public void cargarListaComponente() {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL002
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        
        listaComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CONSECUTIVO.getName());
    }

    /**
     * Carga la lista listaPlaca
     */
    public void cargarListaPlacaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL16224
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        registro.getCampos().get(codElemento));
        listaPlacaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    /**
     * Carga la lista listaNitTaller
     */
    public void cargarListaNitTaller() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL17486
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaNitTaller = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * Carga la lista listaNitTaller
     */
    public void cargarListaNitTallerE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL17486
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaNitTallerE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    /**
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL18416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(DmantenimpreventivosControladorEnum.TALLER.getValue(),
                        registro.getCampos().get(nitTaller));

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DmantenimpreventivosControladorUrlEnum.URL18416
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(DmantenimpreventivosControladorEnum.TALLER.getValue(),
                        registroauxRes);
        listaResponsableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * Metodo ejecutado al cambiar el control NitTaller
     */
    public void cambiarNitTaller() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Placa en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPlaca() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Placa en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPlacaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ("AUT".equals(tipo) || "EJE".equals(tipo)) {
            camposBloqueoAut = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad
     */
    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(valorUnitario) != null) {
            registro.getCampos().put(valorTotal, Integer.parseInt(
                            registro.getCampos().get(cantidad).toString())
                * Integer.parseInt(registro.getCampos().get(valorUnitario)
                                .toString()));
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarComponente() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(codElemento, null);
        registro.getCampos().put(GeneralParameterEnum.PLACA.getName(), null);
        registro.getCampos().put(nomElemento, null);
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaPlaca();
        cargarListaPlacaE();
        // </CODIGO_DESARROLLADO>
        
    }

    /**
     * Metodo ejecutado al cambiar el control ValorUnitario
     * 
     */
    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(cantidad) != null) {
            registro.getCampos().put(valorTotal, Integer.parseInt(
                            registro.getCampos().get(cantidad).toString())
                * Integer.parseInt(registro.getCampos().get(valorUnitario)
                                .toString()));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Elemento en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarElementoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(codElemento, listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(codElemento));

        auxiliarFila = rowNum;

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nomElemento, registroauxNombreElemento);
        cargarListaPlacaE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NitTaller en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNitTallerC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(sucursal, registroauxSucursal);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarResposnableC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        sucursalRes, registroauxSucursalResponsable);

        if ("EJE".equals(tipo)) {
            camposBloqueoEje = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarResponsableC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCantidadC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(valorUnitario) != null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(valorTotal, Integer
                                            .parseInt(listaInicial
                                                            .getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(cantidad)
                                                            .toString())
                                * Integer.parseInt(
                                                listaInicial.getDatasource()
                                                                .get(rowNum
                                                                    % 10)
                                                                .getCampos()
                                                                .get(valorUnitario)
                                                                .toString()));
        }
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
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cantidad) != null) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(valorTotal, Integer
                                            .parseInt(listaInicial
                                                            .getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(cantidad)
                                                            .toString())
                                * Integer.parseInt(
                                                listaInicial.getDatasource()
                                                                .get(rowNum
                                                                    % 10)
                                                                .getCampos()
                                                                .get(valorUnitario)
                                                                .toString()));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElemento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codElemento,
                        registroAux.getCampos().get(codElemento));
        registro.getCampos().put(nomElemento,
                        registroAux.getCampos().get("NOMBRECORTO"));
        
            registro.getCampos().put(GeneralParameterEnum.PLACA.getName(),
                            null);
            cargarListaPlaca();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaElemento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codElemento), "")
                        .toString();

        listaInicial.getDatasource().get(auxiliarFila % 10).getCampos()
                        .put(GeneralParameterEnum.PLACA.getName(), "");

        registroauxNombreElemento = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRECORTO"), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlaca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.PLACA.getName(),
                        registroAux.getCampos().get("SERIE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaPlaca
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlacaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos().get("SERIE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNitTaller
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitTaller(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nitTaller,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(sucursal,
                        registroAux.getCampos().get(sucursal));
        if ("EJE".equals(tipo)) {
            registro.getCampos().put("RESPONSABLE", null);
        }
        cargarListaResponsable();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNitTaller
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitTallerE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        registroauxRes = auxiliar;

        listaInicial.getDatasource().get(auxiliarFila % 10).getCampos()
                        .put(GeneralParameterEnum.RESPONSABLE.getName(),
                                        SysmanConstantes.CONS_TERCERO);

        registroauxSucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(sucursal), "")
                        .toString();

        cargarListaResponsableE();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RESPONSABLE",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(sucursalRes,
                        registroAux.getCampos().get(sucursal));
    }
    

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        registroauxSucursalResponsable = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(sucursal), "")
                        .toString();

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        
        if ((boolean) registro.getCampos().get(nombreComponente)) {
            
            try {
                ejbMantenimientoActivos.mantEstacion(compania, Integer.parseInt(ano), tipo2, Long.parseLong(numero), 
                                registro.getCampos().get("TIPO_MANTENIMIENTO").toString(), Integer.parseInt(registro.getCampos().get("CODIGOELEMENTO").toString()), 
                                registro.getCampos().get("DESCRIPCION").toString(), registro.getCampos().get("TAREAMANTENIMIENTO").toString(), SessionUtil.getUser().toString());
                cargarListaElemento();
                cargarListaElementoE();
                
                return false;
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
                return false;
            }
        }
        else {
            
        if (validarVacio()) {
            
            try {
            long aux;
                    aux = ejbSysmanUtil.generarSiguienteConsecutivo(
                                    "D_MANTENIMIENTO",
                                    " D_MANTENIMIENTO.COMPANIA ='''||"
                                        + "'" + compania + "'" + " ||'''"
                                        + " AND D_MANTENIMIENTO.TIPO_CPTE = '''||" + "'"
                                        + tipo + "'" + "||'''"
                                        + " AND D_MANTENIMIENTO.COMPROBANTE = '''||"
                                        + ""
                                        + numero + "" + "||'''",
                                    "CONSECUTIVO");
                    registro.getCampos().put("CONSECUTIVO", aux);
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().remove(fechaFin);
            registro.getCampos().remove(fechaIni);
            registro.getCampos().put("ANO", ano);
            registro.getCampos().put("TIPO_CPTE", tipo2);
            registro.getCampos().put("COMPROBANTE", numero);
            registro.getCampos().remove(nomTarea);
            registro.getCampos().remove(tipoMant);
    
            if ((registro.getCampos().get(fechaFinal) != null)
                && (registro.getCampos().get(fechaInicial) != null)
                && (((Date) registro.getCampos().get(fechaFinal)).before(
                                (Date) registro.getCampos().get(fechaInicial)))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1874"));
                return false;
            } else {
                
                registro.getCampos().put(cantidad, 1);
                return true;
            }
    
        } else {
            return false;
        }
        // </CODIGO_DESARROLLADO>
    }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        generarTotal();
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
        if (!verificarResponsable()) {
            return false;
        }

        registro.getCampos().remove("RNUM");
        registro.getCampos().remove("RID");
        registro.getCampos().remove(fechaFin);
        registro.getCampos().remove(fechaIni);
        registro.getCampos().remove(nomTarea);
        registro.getCampos().remove(tipoMant);
        registro.getCampos().remove(nombreComponente);

        registro.getCampos().put(sucursalRes, registroauxSucursalResponsable);

        if ((registro.getCampos().get(fechaFinal) != null)
            && (registro.getCampos().get(fechaInicial) != null)) {
            if (((Date) registro.getCampos().get(fechaFinal)).before(
                            (Date) registro.getCampos().get(fechaInicial))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1874"));
                return false;
            }
            else {
                if (registro.getCampos().get(GeneralParameterEnum.PLACA.getName()) == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2481"));
                    return false;
                }
            }
        }
        else {
            if (registro.getCampos().get("PLACA") == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2481"));
                return false;
            }
        }
        registro.getCampos().put(cantidad, 1);
        return true;
        // </CODIGO_DESARROLLADO>

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
        generarTotal();
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return
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
     * @return
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        generarTotal();
        return true;
        // </CODIGO_DESARROLLADO>
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
        registroauxRes = SysmanFunciones
                        .nvl(registro.getCampos().get(nitTaller), "")
                        .toString();
        registroauxNombreElemento = SysmanFunciones.nvl(registro.getCampos()
                        .get(nomElemento), "").toString();
        registroauxSucursal = SysmanFunciones
                        .nvl(registro.getCampos().get(sucursal), "").toString();
        registroauxSucursalResponsable = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(sucursalRes), "")
                        .toString();
    }

    private void generarTotal() {

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(), ano);

            param.put(DmantenimpreventivosControladorEnum.TIPO.getValue(),
                            tipo);

            param.put(GeneralParameterEnum.NUMERO.getName(), numero);

            List<Registro> auxLista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DmantenimpreventivosControladorUrlEnum.URL32164
                                                                            .getValue())
                                            .getUrl(), param));

            String cvalorTotal = SysmanFunciones.nvl(
                            auxLista.get(0).getCampos().get(valorTotal), "0")
                            .toString();

            totalMantenimiento = new java.text.DecimalFormat("$ #,##0.00")
                            .format(Double.parseDouble(SysmanFunciones.nvl(
                                            auxLista.get(0).getCampos().get(
                                                            valorTotal),
                                            "0")
                                            .toString()));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            parametros.put(DmantenimpreventivosControladorEnum.VALORTOTAL
                            .getValue(), cvalorTotal);

            parametros.put(GeneralParameterEnum.ANO.getName(), ano);

            parametros.put(DmantenimpreventivosControladorEnum.TIPO.getValue(),
                            tipo);

            parametros.put(GeneralParameterEnum.NUMERO.getName(), numero);

            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DmantenimpreventivosControladorUrlEnum.URL19235
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

        }
        catch (SystemException ex) {
            Logger.getLogger(ProgramacionmantenimientosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    private boolean verificarResponsable() {

        if ("AUT".equals(tipo)
            && !("").equals(registro.getCampos().get(nitTaller))) {
            try {
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                param.put(DmantenimpreventivosControladorEnum.NIT.getValue(),
                                SysmanConstantes.CONS_TERCERO);

                param.put(GeneralParameterEnum.SUCURSAL.getName(),
                                SysmanConstantes.CONS_SUCURSAL);

                param.put(DmantenimpreventivosControladorEnum.TALLER.getValue(),
                                registro.getCampos().get(nitTaller));

                param.put(DmantenimpreventivosControladorEnum.SUCURSALTALLER
                                .getValue(),
                                registro.getCampos().get(sucursal));

                List<Registro> auxLista = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                DmantenimpreventivosControladorUrlEnum.URL15935
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                String aux = SysmanFunciones.nvl(
                                auxLista.get(0).getCampos().get("CUENTA"), "")
                                .toString();

                if ("0".equals(aux)) {

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);

                    parametros.put(DmantenimpreventivosControladorEnum.NIT
                                    .getValue(),
                                    SysmanConstantes.CONS_TERCERO);

                    parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
                                    SysmanConstantes.CONS_SUCURSAL);

                    parametros.put(GeneralParameterEnum.NOMBRE.getName(),
                                    "VARIOS");

                    parametros.put(DmantenimpreventivosControladorEnum.TALLER
                                    .getValue(),
                                    registro.getCampos().get(nitTaller));

                    parametros.put(DmantenimpreventivosControladorEnum.SUCURSAL_TALLER
                                    .getValue(),
                                    registro.getCampos().get(sucursal));

                    parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());

                    parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());

                    Parameter parameter = new Parameter();
                    parameter.setFields(parametros);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    DmantenimpreventivosControladorUrlEnum.URL17950
                                                                    .getValue());

                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(),
                                    parameter);

                }
            }
            catch (SystemException ex) {
                Logger.getLogger(ProgramacionmantenimientosControlador.class
                                .getName()).log(Level.SEVERE, null, ex);

                JsfUtil.agregarMensajeError(ex.getMessage());
                return false;
            }

        }
        return true;

    }
    
    public boolean validarVacio() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "PLACA")) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("rid", rid);
        parametrosEntrada.put("tipo", tipo);
        parametrosEntrada.put("panio", panio);
        parametrosEntrada.put("pmes", pmes);
        parametrosEntrada.put("tipoNombre", tipoNombre);
        parametrosEntrada.put("rid", rid);
        direccionador.setParametros(parametrosEntrada);
        String numForm = Integer
                        .toString(GeneralCodigoFormaEnum.PROGRAMACIONMANTENIMIENTOS_CONTROLADOR
                                        .getCodigo());
        direccionador.setNumForm(numForm);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(fechaFin);
        registro.getCampos().remove(fechaIni);
        registro.getCampos().remove(nomTarea);
        registro.getCampos().remove(tipoMant);
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA

    }

    /**
     * Retorna la lista listaTareaMantenimiento
     * 
     * @return listaTareaMantenimiento
     */
    public List<Registro> getListaTareaMantenimiento() {
        return listaTareaMantenimiento;
    }

    /**
     * Asigna la lista listaTareaMantenimiento
     * 
     * @param listaTareaMantenimiento
     * Variable a asignar en listaTareaMantenimiento
     */
    public void setListaTareaMantenimiento(
        List<Registro> listaTAREAMANTENIMIENTO) {
        this.listaTareaMantenimiento = listaTAREAMANTENIMIENTO;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipo2() {
        return tipo2;
    }

    public void setTipo2(String tipo2) {
        this.tipo2 = tipo2;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPmes() {
        return pmes;
    }

    public void setPmes(String pmes) {
        this.pmes = pmes;
    }

    public String getPanio() {
        return panio;
    }

    public void setPanio(String panio) {
        this.panio = panio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }

    /**
     * Retorna la lista listaElemento
     * 
     * @return listaElemento
     */
    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    /**
     * Asigna la lista listaElemento
     * 
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElemento(
        RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    /**
     * Asigna la lista listaElemento
     * 
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    /**
     * Asigna la lista listaElemento
     * 
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElementoE(
        RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    /**
     * Retorna la lista listaPlaca
     * 
     * @return listaPlaca
     */
    public RegistroDataModelImpl getListaPlaca() {
        return listaPlaca;
    }

    /**
     * Asigna la lista listaPlaca
     * 
     * @param listaPlaca
     * Variable a asignar en listaPlaca
     */
    public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
        this.listaPlaca = listaPlaca;
    }

    /**
     * Retorna la lista listaPlaca
     * 
     * @return listaPlaca
     */
    public RegistroDataModelImpl getListaPlacaE() {
        return listaPlacaE;
    }

    /**
     * Asigna la lista listaPlaca
     * 
     * @param listaPlaca
     * Variable a asignar en listaPlaca
     */
    public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
        this.listaPlacaE = listaPlacaE;
    }

    /**
     * Retorna la lista listaNitTaller
     * 
     * @return listaNitTaller
     */
    public RegistroDataModelImpl getListaNitTallerE() {
        return listaNitTallerE;
    }

    /**
     * Retorna la lista listaNitTaller
     * 
     * @return listaNitTaller
     */
    public RegistroDataModelImpl getListaNitTaller() {
        return listaNitTaller;
    }

    /**
     * Asigna la lista listaNitTaller
     * 
     * @param listaNitTaller
     * Variable a asignar en listaNitTaller
     */
    public void setListaNitTaller(RegistroDataModelImpl listaNitTaller) {
        this.listaNitTaller = listaNitTaller;
    }

    /**
     * Asigna la lista listaNitTaller
     * 
     * @param listaNitTaller
     * Variable a asignar en listaNitTaller
     */
    public void setListaNitTallerE(RegistroDataModelImpl listaNitTallerE) {
        this.listaNitTallerE = listaNitTallerE;
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

    public String getTotalMantenimiento() {
        return totalMantenimiento;
    }

    public void setTotalMantenimiento(String totalMantenimiento) {
        this.totalMantenimiento = totalMantenimiento;
    }

    public String getRegistroauxNombreElemento() {
        return registroauxNombreElemento;
    }

    public void setRegistroauxNombreElemento(String registroauxNombreElemento) {
        this.registroauxNombreElemento = registroauxNombreElemento;
    }

    public String getRegistroauxSucursal() {
        return registroauxSucursal;
    }

    public void setRegistroauxSucursal(String registroauxSucursal) {
        this.registroauxSucursal = registroauxSucursal;
    }

    public String getRegistroauxRes() {
        return registroauxRes;
    }

    public void setRegistroauxRes(String registroauxRes) {
        this.registroauxRes = registroauxRes;
    }

    public String getRegistroauxSucursalResponsable() {
        return registroauxSucursalResponsable;
    }

    public void setRegistroauxSucursalResponsable(
        String registroauxSucursalResponsable) {
        this.registroauxSucursalResponsable = registroauxSucursalResponsable;
    }

    public boolean isCamposVisible() {
        return camposVisible;
    }

    public void setCamposVisible(boolean camposVisible) {
        this.camposVisible = camposVisible;
    }

    public boolean isCamposVisibleRes() {
        return camposVisibleRes;
    }

    public void setCamposVisibleRes(boolean camposVisibleRes) {
        this.camposVisibleRes = camposVisibleRes;
    }

    public boolean isBloqueaDetalle() {
        return bloqueaDetalle;
    }

    public void setBloqueaDetalle(boolean bloqueaDetalle) {
        this.bloqueaDetalle = bloqueaDetalle;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public boolean isCamposBloqueoAut() {
        return camposBloqueoAut;
    }

    public void setCamposBloqueoAut(boolean camposBloqueoAut) {
        this.camposBloqueoAut = camposBloqueoAut;
    }

    public boolean isCamposBloqueoEje() {
        return camposBloqueoEje;
    }

    public void setCamposBloqueoEje(boolean camposBloqueoEje) {
        this.camposBloqueoEje = camposBloqueoEje;
    }

    public boolean isCargarKilometraje() {
        return cargarKilometraje;
    }

    public void setCargarKilometraje(boolean cargarKilometraje) {
        this.cargarKilometraje = cargarKilometraje;
    }

    public RegistroDataModelImpl getListaComponente() {
        return listaComponente;
    }

    public void setListaComponente(RegistroDataModelImpl listaComponente) {
        this.listaComponente = listaComponente;
    }

    
    public boolean isVisibleComponente() {
        return visibleComponente;
    }

    public void setVisibleComponente(boolean visibleComponente) {
        this.visibleComponente = visibleComponente;
    }

    
    public RegistroDataModelImpl getListaComponenteE() {
        return listaComponenteE;
    }

    public void setListaComponenteE(RegistroDataModelImpl listaComponenteE) {
        this.listaComponenteE = listaComponenteE;
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
}
