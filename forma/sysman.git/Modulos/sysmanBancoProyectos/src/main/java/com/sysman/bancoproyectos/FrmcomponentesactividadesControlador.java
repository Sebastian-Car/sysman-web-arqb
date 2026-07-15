package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.enums.FrmcomponentesControladorUrlEnum;
import com.sysman.bancoproyectos.enums.FrmcomponentesactividadesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmcomponentesactividadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.Date;
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
 *
 * @author dmaldonado
 * @version 1, 03/09/2015
 * 
 * @author ybecerra
 * @version 2, 19/09/2017, proceso de Refactoring
 */

@ManagedBean
@ViewScoped

public class FrmcomponentesactividadesControlador
                extends BeanBaseContinuoAcmeImpl
{

    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida para almacenar el codigo del modulo seleccionado en la aplicacion
     */
    private final String moduloBancos;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se activa la edicion de un registro. Toma el valor del indice dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Constante a nivel de clase que aloja la cadena: <code>ridComponente</code>
     */
    private final String cRidComponente = FrmcomponentesactividadesControladorEnum.RIDCOMPONENTE
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    private final String cVigencia;
    private final String cCantidad;
    private final String cCostoUnitario;
    private final String cCostoTotal;
    private final String cValorEjecutado;
    private final String cValorProgramado;
    private final String cPorcEjectudo;
    private final String cCantidadEje;
    private final String cValorSolicitadoActividad;
    private final String cValorDisminuido;
    private final String cNombre;
    private final String cCodigo;
    private final String msgErrValor;
    private String totalProyecto;
    private String ejecutadoProyecto;
    private String programadoProyecto;
    private String bpimProyecto;
    private String totalComponente;
    private String ejecutadoComponente;
    private String asignadoComponente;
    private String nombreProyecto;
    private String nombreComponente;
    private String periodicidad;
    private String sumaValorProgramado;
    private String sumaCostoTotal;
    private String sumaValorEjecutado;
    private String codigoComponente;
    private String codigoProyecto;
    private String tipoComponente;
    private String vigenciaComponente;
    private boolean actividadBorrable;
    private boolean bloqueadoCantidad;
    private boolean creaActividad;
    private String auxiliar;
    private String anoIni;
    private String anoFin;
    private boolean muestraRegistro;
    private String menuActual;
    private boolean botonesInactivos;
    private String codigoActBP;
    private String unitarioComponente;
    private String cantidadComponente;
    private String accion;
    private boolean actualizando;
    Registro registroAuxEdicion;
    private String codigoProy;
    private Registro valTotal;
    private String costoTotal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private HashMap<String, Object> ridProyecto;
    private HashMap<String, Object> ridComponente;
    private HashMap<String, Object> rid;
    private Map<String, Object> parametrosEntrada;
    private boolean permiteAjustarComponentes;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaActividad;
    private RegistroDataModelImpl listaActividadE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Crea una nueva instancia de FrmcomponentesactividadesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmcomponentesactividadesControlador()
    {

        super();
        compania = SessionUtil.getCompania();
        moduloBancos = SessionUtil.getModulo();
        cVigencia = GeneralParameterEnum.VIGENCIA.getName();
        cCantidad = GeneralParameterEnum.CANTIDAD.getName();
        cCostoUnitario = FrmcomponentesactividadesControladorEnum.COSTOUNITARIO
                        .getValue();
        cCostoTotal = FrmcomponentesactividadesControladorEnum.COSTOTOTAL
                        .getValue();
        cValorEjecutado = FrmcomponentesactividadesControladorEnum.VALOREJECUTADO
                        .getValue();
        cValorProgramado = FrmcomponentesactividadesControladorEnum.VALORPROGRAMADO
                        .getValue();
        cPorcEjectudo = FrmcomponentesactividadesControladorEnum.PORCEJECUTADO
                        .getValue();
        cCantidadEje = FrmcomponentesactividadesControladorEnum.CANTIDAD_EJE
                        .getValue();
        cValorSolicitadoActividad = FrmcomponentesactividadesControladorEnum.VALOR_SOLICITADO_ACTIVIDAD
                        .getValue();

        cValorDisminuido = FrmcomponentesactividadesControladorEnum.VALOR_DISMINUIDO.getValue();

        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        msgErrValor = "TB_TB2329";

        try
        {
            // 162
            numFormulario = GeneralCodigoFormaEnum.FRMCOMPONENTESACTIVIDADES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
            switch (menuActual)
            {
            case "52020102":
            case "52020402":
                muestraRegistro = false;
                botonesInactivos = true;
                break;
            case "52020101":
                muestraRegistro = true;
                botonesInactivos = false;
                break;
            case "NULL":
                SessionUtil.redireccionarMenu();
                break;

            default:

                break;

            }

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                codigoProy = (String) parametrosEntrada.get("codigoProy");
                ridComponente = (HashMap<String, Object>) parametrosEntrada
                                .get(cRidComponente);

                ridProyecto = (HashMap<String, Object>) parametrosEntrada
                                .get("ridProyecto");
                anoIni = (String) parametrosEntrada.get("anoIni");
                anoFin = (String) parametrosEntrada.get("anoFin");

                accion = (String) parametrosEntrada.get("accion");

                parametrosEntrada.put("rid", ridComponente);
                parametrosEntrada.remove(cRidComponente);
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
            }
            SessionUtil.cleanFlash();
            // </INI_ADICIONAL>
        }
        catch (SysmanException ex)
        {
            logger.error(ex.getMessage(), ex);
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
        enumBase = GenericUrlEnum.COMPONENTES_ACTIVIDADES;
        buscarLlave();
        valoresProyecto(ridProyecto);
        valoresComponente(ridComponente);
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        try {
            permiteAjustarComponentes = "SI".equals(ejbSysmanUtilRemote.consultarParametro(compania, 
                            "PERMITE AJUSTAR COMPONENTES Y ACTIVIDADES PROGRAMADAS", SessionUtil.getModulo(), new Date(), false));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        registro.getCampos().put(cVigencia, vigenciaComponente);
        registro.getCampos().put(cCantidad, 0);
        registro.getCampos().put(cCostoUnitario, 0);
        registro.getCampos().put(cCostoTotal, 0);
        registro.getCampos().put(cValorEjecutado, 0);
        registro.getCampos().put(cValorProgramado, 0);
        registro.getCampos().put(cPorcEjectudo, 0);
        registro.getCampos().put(cCantidadEje, 0);
        registro.getCampos().put(cValorSolicitadoActividad, 0);
        registro.getCampos().put(cValorDisminuido, 0);
        cargarListaActividad();
        cargarListaActividadE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        if ("v".equals(accion))
        {
            muestraRegistro = false;
        }
        if (ridComponente == null)
        {

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRMCOMPONENTES_CONTROLADOR
                                            .getCodigo()));

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
            return;
        }

        abrirFormulario();
        valoresComponente(ridComponente);
        registro.getCampos().put(cVigencia, vigenciaComponente);
        
        rid = ridComponente;
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                                        .getValue(), codigoComponente);
        parametrosListado.put(
                        FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                                        .getValue(),
                        codigoProyecto);

        parametrosListado.put(
                        FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                                        .getValue(),
                        tipoComponente);

        cargarValores();

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaACTIVIDAD
     */
    public void cargarListaActividad()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcomponentesactividadesControladorUrlEnum.URL10583
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaACTIVIDAD
     */
    public void cargarListaActividadE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcomponentesactividadesControladorUrlEnum.URL10583
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaActividadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Actividades en la vista
     *
     */
    public void oprimirActividades()
    {
        // 130
        Map<String, Object> param = new HashMap<>();
        param.put("codigoProy", codigoProy);
        param.put(cRidComponente, ridComponente);
        param.put("ridProyecto", ridProyecto);
        param.put("anoIni", anoIni);
        param.put("anoFin", anoFin);
        param.put("accion", accion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRMACTIVIDADES_CONTROLADOR
                                        .getCodigo()));

        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador, moduloBancos);
    }
    
    public void oprimirProgramacionProyectos() {
    	//CC 811
        String[] campos = { "anoIni", "numeroProyecto", "nombreProyecto" };
        Object[] valores = { anoIni, codigoProy, nombreProyecto };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(
                         GeneralCodigoFormaEnum.FRMCREARPROGAPROYECTOS_CONTROLADOR
                                         .getCodigo()),
                        moduloBancos, campos, valores);

    }
    

    /**
     * 
     * Metodo ejecutado al oprimir el boton CrearActividad en la vista
     *
     */
    public void oprimirCrearActividad()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (accion != null && "v".equals(accion))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2338"));
                return;

            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                            .getValue(), tipoComponente);

            List<Registro> listaRS;

            listaRS = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL12234
                                                                            .getValue())
                                            .getUrl(), param));

            if (!listaRS.isEmpty())
            {
                codigoActBP = listaRS.get(0).getCampos().get(cCodigo)
                                .toString();
                creaActividad = true;
            }
            else
            {
                aceptarDialogoCreaActividades();
                reasignarOrigen();
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdCopiarActividades en la vista
     *
     */
    public void oprimircmdCopiarActividades()
    {
        try
        {
            if (accion != null && "v".equals(accion))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2338"));
                return;

            }

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcomponentesactividadesControladorUrlEnum.URL418
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                            .getValue(), codigoProyecto);
            fields.put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                            .getValue(), codigoComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                            .getValue(), tipoComponente);
            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(FrmcomponentesactividadesControladorEnum.NOMBRECOMPONENTE
                            .getValue(), nombreComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.VIGENCIACOMPONENTE
                            .getValue(), vigenciaComponente);

            int aux = requestManager.saveCount(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            fields);

            // <CODIGO_DESARROLLADO>

            if (aux == 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2336"));
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2335")
                                .replace("#$aux#$", String.valueOf(aux)));
            }

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control COSTOUNITARIO
     * 
     */
    public void cambiarCostoUnitario()
    {
        // <CODIGO_DESARROLLADO>
        Object uni = registro.getCampos().get(cCostoUnitario);
        if (uni == null || "".equals(uni))
        {
            registro.getCampos().put(cCostoUnitario, 0);
        }

        double cantidad = Double.parseDouble(
                        registro.getCampos().get(cCantidad).toString());
        double unitario = Double.parseDouble(
                        registro.getCampos().get(cCostoUnitario).toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99)
        {
            JsfUtil.agregarMensajeError(idioma.getString(msgErrValor));
            registro.getCampos().put(cCostoUnitario, 0);
            registro.getCampos().put(cCostoTotal, 0);
        }
        else
        {
            registro.getCampos().put(cCostoTotal, cantidad * unitario);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CANTIDAD
     * 
     */
    public void cambiarCantidad()
    {
        // <CODIGO_DESARROLLADO>
        Object cant = registro.getCampos().get(cCantidad);
        if (cant == null || "".equals(cant))
        {
            registro.getCampos().put(cCantidad, 0);
        }
        double cantidad = Double.parseDouble(
                        registro.getCampos().get(cCantidad).toString());
        double unitario = Double.parseDouble(
                        registro.getCampos().get(cCostoUnitario).toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99)
        {
            JsfUtil.agregarMensajeError(idioma.getString(msgErrValor));
            registro.getCampos().put(cCantidad, 0);
            registro.getCampos().put(cCostoTotal, 0);
        }
        else
        {
            registro.getCampos().put(cCostoTotal, cantidad * unitario);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DialogoCreaActividades
     * 
     */
    public void cambiarDialogoCreaActividades()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Actividades
     * 
     */
    public void retornarFormularioActividades(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void retornarFormularioProgramacionProyectos(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo DialogoCreaActividades en la vista
     *
     */
    public void aceptarDialogoCreaActividades()
    {
        creaActividad = false;
        codigoActBP = null;

        boolean creaAct;
        try
        {
            creaAct = ejbBancoProyectoCero.crearActividades(compania,
                            codigoComponente, codigoProyecto, tipoComponente,
                            SessionUtil.getUser().getCodigo());

            if (creaAct)
            {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2343"));
            }

            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2341"));
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2340"));
            }

            reasignarOrigen();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2340"));
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo DialogoCreaActividades en la vista
     *
     */
    public void cancelarDialogoCreaActividades()
    {
        creaActividad = false;
        insertarComponenteRelacionado(codigoActBP);
        codigoActBP = null;

    }

    /**
     * Metodo ejecutado al cambiar el control COSTOUNITARIO en la fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCostoUnitarioC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        Object uni = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cCostoUnitario);
        if (uni == null || "".equals(uni))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoUnitario, 0);
        }

        double cantidad = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(cCantidad)
                        .toString());
        double unitario = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(cCostoUnitario)
                        .toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99)
        {
            JsfUtil.agregarMensajeError(idioma.getString(msgErrValor));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoUnitario, 0);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoTotal, 0);
        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoTotal, cantidad * unitario);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CANTIDAD en la fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCantidadC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        Object cant = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(cCantidad);
        if (cant == null || "".equals(cant))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCantidad, 0);
        }
        double cantidad = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(cCantidad)
                        .toString());
        double unitario = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(cCostoUnitario)
                        .toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99)
        {
            JsfUtil.agregarMensajeError(idioma.getString(msgErrValor));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCantidad, 0);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoTotal, 0);
        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(cCostoTotal, cantidad * unitario);
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaACTIVIDAD
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividad(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ACTIVIDAD.getName(),
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put("NOMBREACTIVIDAD",
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaACTIVIDAD
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * @param actividad
     */
    public void insertarComponenteRelacionado(String actividad)
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), actividad);

        Registro regAux;
        try
        {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL738
                                                                            .getValue())
                                            .getUrl(), param));

            String nombreActividad = regAux.getCampos().get(cNombre) == null
                            ? ""
                            : regAux.getCampos().get(cNombre).toString();

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcomponentesactividadesControladorUrlEnum.URL751
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                            .getValue(), codigoProyecto);
            fields.put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                            .getValue(), codigoComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                            .getValue(), tipoComponente);
            fields.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);
            fields.put(FrmcomponentesactividadesControladorEnum.NOMBREACTIVIDAD
                            .getValue(), nombreActividad);
            fields.put(FrmcomponentesactividadesControladorEnum.UNITARIOCOMPONENTE
                            .getValue(), unitarioComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.CANTIDADCOMPONENTE
                            .getValue(), cantidadComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.TOTALCOMPONENTE
                            .getValue(), totalComponente);
            fields.put(FrmcomponentesactividadesControladorEnum.VIGENCIACOMPONENTE
                            .getValue(), vigenciaComponente);
            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * @param rowId
     */
    public void valoresProyecto(Map<String, Object> rowId)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        rowId.get(FrmcomponentesactividadesControladorEnum.KEY_CODIGO
                                        .getValue()));

        Registro proyecto;
        try
        {
            proyecto = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL16291
                                                                            .getValue())
                                            .getUrl(), param));

            nombreProyecto = SysmanFunciones.nvl(proyecto.getCampos()
                            .get("NOMBREPROYECTO"), "").toString();
            bpimProyecto = SysmanFunciones
                            .nvl(proyecto.getCampos().get("CODIGOBPIM"), "")
                            .toString();
            ejecutadoProyecto = SysmanFunciones.nvl(proyecto.getCampos()
                            .get(cValorEjecutado), "").toString();
            programadoProyecto = SysmanFunciones.nvl(proyecto.getCampos()
                            .get(cValorProgramado), "").toString();
            periodicidad = SysmanFunciones
                            .nvl(proyecto.getCampos().get("PERIODICIDAD"), "")
                            .toString();
            totalProyecto = SysmanFunciones
                            .nvl(proyecto.getCampos().get("VALORTOTAL"), "")
                            .toString();
            codigoProyecto = SysmanFunciones
                            .nvl(proyecto.getCampos().get(cCodigo), "")
                            .toString();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * @param rowId
     */
    public void valoresComponente(Map<String, Object> rowId)
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                            .getValue(), rowId.get("KEY_CODIGOPROYECTO"));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            rowId.get("KEY_CODIGO"));
            param.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                            .getValue(), rowId.get("KEY_TIPOCOMPONENTE"));

            Registro componente;

            componente = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL11376
                                                                            .getValue())
                                            .getUrl(), param));

            nombreComponente = (componente.getCampos()
                            .get("NOMBRECOMPONENTE") == null ? ""
                                            : componente.getCampos()
                                                            .get("NOMBRECOMPONENTE"))
                                                                            .toString();
            totalComponente = componente.getCampos().get("VALORTOTAL")
                            .toString();
            ejecutadoComponente = componente.getCampos()
                            .get(cValorEjecutado).toString();
            codigoComponente = componente.getCampos().get(cCodigo).toString();
            tipoComponente = componente.getCampos()
                            .get("TIPOCOMPONENTE").toString();
            vigenciaComponente = componente.getCampos().get(cVigencia)
                            .toString();
            asignadoComponente = sumaCostoTotal == null ? "0" : sumaCostoTotal;

            unitarioComponente = String.valueOf(
                            componente.getCampos().get("VALORUNITARIO")) == null
                                            ? ""
                                            : String.valueOf(componente.getCampos()
                                                            .get("VALORUNITARIO"));
            cantidadComponente = String
                            .valueOf(componente.getCampos()
                                            .get(cCantidad)) == null
                                                            ? ""
                                                            : String.valueOf(componente
                                                                            .getCampos()
                                                                            .get(cCantidad));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * @param valor
     * @return
     */
    private void cargarValores()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                        .getValue(),
                        codigoComponente);
        param.put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                        .getValue(),
                        codigoProyecto);
        param.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                        .getValue(),
                        tipoComponente);

        try
        {
            valTotal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL32744
                                                                            .getValue())
                                            .getUrl(), param));

            if (valTotal != null)
            {
                DecimalFormat dblDF = new DecimalFormat("#,###.00");
                sumaCostoTotal = dblDF.format(valTotal.getCampos()
                                .get("COSTOTOTAL"));
                sumaValorProgramado = dblDF.format(valTotal.getCampos()
                                .get("VALORPROGRAMADO"));
                costoTotal = valTotal.getCampos()
                                .get("VALORPROGRAMADO").toString();
                sumaValorEjecutado = dblDF.format(valTotal.getCampos()
                                .get("VALOREJECUTADO"));

            }
            else
            {
                sumaCostoTotal = "0.0";
                sumaValorProgramado = "0.0";
                sumaValorEjecutado = "0.0";

            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        cargarValores();

        // </CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
        cambiarCantidad();
        cambiarCostoUnitario();
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                                        .getValue(), codigoProyecto);
        registro.getCampos()
                        .put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                                        .getValue(), codigoComponente);
        registro.getCampos()
                        .put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                                        .getValue(), tipoComponente);
        registro.getCampos().put("PRIORIDAD", 5);
        registro.getCampos().put(cVigencia, vigenciaComponente);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cVigencia, vigenciaComponente);
        registro.getCampos().put(cCantidad, 0);
        registro.getCampos().put(cCostoUnitario, 0);
        registro.getCampos().put(cCostoTotal, 0);
        registro.getCampos().put(cValorEjecutado, 0);
        registro.getCampos().put(cValorProgramado, 0);
        registro.getCampos().put(cPorcEjectudo, 0);
        registro.getCampos().put(cCantidadEje, 0);
        registro.getCampos().put(cValorSolicitadoActividad, 0);
        registro.getCampos().put(cValorDisminuido, 0);
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (actualizando)
        {
            cambiarCantidadC(indice);
            cambiarCostoUnitarioC(indice);
            
        }

        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        cargarValores();

        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("CODIGO", codigoProyecto);
        parametros.put("COMPONENTE", codigoComponente);
        parametros.put("VALORPROGRAMADO", costoTotal);
        parametros.put(GeneralParameterEnum.USUARIO.getName(),
                        SessionUtil.getUser().getCodigo());
        Parameter parameter = new Parameter();
        parameter.setFields(parametros);
        // 206017

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcomponentesControladorUrlEnum.URL0001
                                                        .getValue());
        try
        {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                            .getValue(), codigoProyecto);
            param.put(FrmcomponentesactividadesControladorEnum.COMPONENTE
                            .getValue(), codigoComponente);
            param.put(FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                            .getValue(), tipoComponente);
            param.put(FrmcomponentesactividadesControladorEnum.VIGENCIACOMPONENTE
                            .getValue(), vigenciaComponente);
            Registro programacion;

            programacion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesactividadesControladorUrlEnum.URL40381
                                                                            .getValue())
                                            .getUrl(), param));

            actividadBorrable = programacion == null;
            if (!actividadBorrable)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2331"));
                return false;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(FrmcomponentesactividadesControladorEnum.CODIGOPROYECTO
                                        .getValue());
        registro.getCampos().remove(
                        FrmcomponentesactividadesControladorEnum.COMPONENTE
                                        .getValue());
        registro.getCampos().remove(
                        FrmcomponentesactividadesControladorEnum.TIPOCOMPONENTE
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.ACTIVIDAD.getName());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        registroAuxEdicion = registro;
        indice = listaInicial.getRowIndex();
        actualizando = true;
        bloqueadoCantidad =  !permiteAjustarComponentes &&
                        Double.parseDouble(registro.getCampos()
                        .get(cValorProgramado).toString()) > 0 ;
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     */
    public void ejecutarrcCerrar()
    {

        Direccionador direccionador = new Direccionador();
        SessionUtil.setFlash(parametrosEntrada);
        direccionador.setParametros(parametrosEntrada);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCOMPONENTES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cVigencia, vigenciaComponente);
        registro.getCampos().put(cCantidad, 0);
        registro.getCampos().put(cCostoUnitario, 0);
        registro.getCampos().put(cCostoTotal, 0);
        registro.getCampos().put(cValorEjecutado, 0);
        registro.getCampos().put(cValorProgramado, 0);
        registro.getCampos().put(cPorcEjectudo, 0);
        registro.getCampos().put(cCantidadEje, 0);
        registro.getCampos().put(cValorSolicitadoActividad, 0);
        registro.getCampos().put(cValorDisminuido, 0);
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable totalProyecto
     * 
     * @return totalProyecto
     */
    public String getTotalProyecto()
    {
        return totalProyecto;
    }

    /**
     * Asigna la variable totalProyecto
     * 
     * @param totalProyecto
     * Variable a asignar en totalProyecto
     */
    public void setTotalProyecto(String totalProyecto)
    {
        this.totalProyecto = totalProyecto;
    }

    /**
     * Retorna la variable ejecutadoProyecto
     * 
     * @return ejecutadoProyecto
     */
    public String getEjecutadoProyecto()
    {
        return ejecutadoProyecto;
    }

    /**
     * Asigna la variable ejecutadoProyecto
     * 
     * @param ejecutadoProyecto
     * Variable a asignar en ejecutadoProyecto
     */
    public void setEjecutadoProyecto(String ejecutadoProyecto)
    {
        this.ejecutadoProyecto = ejecutadoProyecto;
    }

    /**
     * Retorna la variable programadoProyecto
     * 
     * @return programadoProyecto
     */
    public String getProgramadoProyecto()
    {
        return programadoProyecto;
    }

    /**
     * Asigna la variable programadoProyecto
     * 
     * @param programadoProyecto
     * Variable a asignar en programadoProyecto
     */
    public void setProgramadoProyecto(String programadoProyecto)
    {
        this.programadoProyecto = programadoProyecto;
    }

    /**
     * Retorna la variable bpimProyecto
     * 
     * @return bpimProyecto
     */
    public String getBpimProyecto()
    {
        return bpimProyecto;
    }

    /**
     * Asigna la variable bpimProyecto
     * 
     * @param bpimProyecto
     * Variable a asignar en bpimProyecto
     */
    public void setBpimProyecto(String bpimProyecto)
    {
        this.bpimProyecto = bpimProyecto;
    }

    /**
     * Retorna la variable totalComponente
     * 
     * @return totalComponente
     */
    public String getTotalComponente()
    {
        return totalComponente;
    }

    /**
     * Asigna la variable totalComponente
     * 
     * @param totalComponente
     * Variable a asignar en totalComponente
     */
    public void setTotalComponente(String totalComponente)
    {
        this.totalComponente = totalComponente;
    }

    /**
     * Retorna la variable ejecutadoComponente
     * 
     * @return ejecutadoComponente
     */
    public String getEjecutadoComponente()
    {
        return ejecutadoComponente;
    }

    /**
     * Asigna la variable ejecutadoComponente
     * 
     * @param ejecutadoComponente
     * Variable a asignar en ejecutadoComponente
     */
    public void setEjecutadoComponente(String ejecutadoComponente)
    {
        this.ejecutadoComponente = ejecutadoComponente;
    }

    /**
     * Retorna la variable nombreProyecto
     * 
     * @return nombreProyecto
     */
    public String getNombreProyecto()
    {
        return nombreProyecto;
    }

    /**
     * Asigna la variable nombreProyecto
     * 
     * @param nombreProyecto
     * Variable a asignar en nombreProyecto
     */
    public void setNombreProyecto(String nombreProyecto)
    {
        this.nombreProyecto = nombreProyecto;
    }

    /**
     * Retorna la variable nombreComponente
     * 
     * @return nombreComponente
     */
    public String getNombreComponente()
    {
        return nombreComponente;
    }

    /**
     * Asigna la variable nombreComponente
     * 
     * @param nombreComponente
     * Variable a asignar en nombreComponente
     */
    public void setNombreComponente(String nombreComponente)
    {
        this.nombreComponente = nombreComponente;
    }

    /**
     * Retorna la variable periodicidad
     * 
     * @return periodicidad
     */
    public String getPeriodicidad()
    {
        return periodicidad;
    }

    /**
     * Asigna la variable periodicidad
     * 
     * @param periodicidad
     * Variable a asignar en periodicidad
     */
    public void setPeriodicidad(String periodicidad)
    {
        this.periodicidad = periodicidad;
    }

    /**
     * Retorna la variable asignadoComponente
     * 
     * @return asignadoComponente
     */
    public String getAsignadoComponente()
    {
        return asignadoComponente;
    }

    /**
     * Asigna la variable asignadoComponente
     * 
     * @param asignadoComponente
     * Variable a asignar en asignadoComponente
     */
    public void setAsignadoComponente(String asignadoComponente)
    {
        this.asignadoComponente = asignadoComponente;
    }

    /**
     * Retorna la variable anoIni
     * 
     * @return anoIni
     */
    public String getAnoIni()
    {
        return anoIni;
    }

    /**
     * Asigna la variable anoIni
     * 
     * @param anoIni
     * Variable a asignar en anoIni
     */
    public void setAnoIni(String anoIni)
    {
        this.anoIni = anoIni;
    }

    /**
     * Retorna la variable codigoProy
     * 
     * @return codigoProy
     */

    public String getCodigoProy()
    {
        return codigoProy;
    }

    /**
     * Asigna la variable codigoProy
     * 
     * @param codigoProy
     * Variable a asignar en codigoProy
     */
    public void setCodigoProy(String codigoProy)
    {
        this.codigoProy = codigoProy;
    }

    /**
     * Retorna la variable anoFin
     * 
     * @return anoFin
     */
    public String getAnoFin()
    {
        return anoFin;
    }

    /**
     * Asigna la variable anoFin
     * 
     * @param anoFin
     * Variable a asignar en anoFin
     */
    public void setAnoFin(String anoFin)
    {
        this.anoFin = anoFin;
    }

    /**
     * Retorna la variable bloqueadoCantidad
     * 
     * @return bloqueadoCantidad
     */
    public boolean isBloqueadoCantidad()
    {
        return bloqueadoCantidad;
    }

    /**
     * Asigna la variable bloqueadoCantidad
     * 
     * @param bloqueadoCantidad
     * Variable a asignar en bloqueadoCantidad
     */
    public void setBloqueadoCantidad(boolean bloqueadoCantidad)
    {
        this.bloqueadoCantidad = bloqueadoCantidad;
    }

    /**
     * Retorna la variable actividadBorrable
     * 
     * @return actividadBorrable
     */
    public boolean isActividadBorrable()
    {
        return actividadBorrable;
    }

    /**
     * Asigna la variable actividadBorrable
     * 
     * @param actividadBorrable
     * Variable a asignar en actividadBorrable
     */
    public void setActividadBorrable(boolean actividadBorrable)
    {
        this.actividadBorrable = actividadBorrable;
    }

    /**
     * Retorna la variable vigenciaComponente
     * 
     * @return vigenciaComponente
     */
    public String getVigenciaComponente()
    {
        return vigenciaComponente;
    }

    /**
     * Asigna la variable vigenciaComponente
     * 
     * @param vigenciaComponente
     * Variable a asignar en vigenciaComponente
     */
    public void setVigenciaComponente(String vigenciaComponente)
    {
        this.vigenciaComponente = vigenciaComponente;
    }

    /**
     * Retorna la variable tipoComponente
     * 
     * @return tipoComponente
     */
    public String getTipoComponente()
    {
        return tipoComponente;
    }

    /**
     * Asigna la variable tipoComponente
     * 
     * @param tipoComponente
     * Variable a asignar en tipoComponente
     */
    public void setTipoComponente(String tipoComponente)
    {
        this.tipoComponente = tipoComponente;
    }

    /**
     * Retorna la variable codigoComponente
     * 
     * @return codigoComponente
     */
    public String getCodigoComponente()
    {
        return codigoComponente;
    }

    /**
     * Asigna la variable codigoComponente
     * 
     * @param codigoComponente
     * Variable a asignar en codigoComponente
     */
    public void setCodigoComponente(String codigoComponente)
    {
        this.codigoComponente = codigoComponente;
    }

    /**
     * Retorna la variable codigoProyecto
     * 
     * @return codigoProyecto
     */
    public String getCodigoProyecto()
    {
        return codigoProyecto;
    }

    /**
     * Asigna la variable codigoProyecto
     * 
     * @param codigoProyecto
     * Variable a asignar en codigoProyecto
     */
    public void setCodigoProyecto(String codigoProyecto)
    {
        this.codigoProyecto = codigoProyecto;
    }

    /**
     * Retorna la variable sumaValorProgramado
     * 
     * @return sumaValorProgramado
     */
    public String getSumaValorProgramado()
    {
        return sumaValorProgramado;
    }

    /**
     * Asigna la variable sumaValorProgramado
     * 
     * @param sumaValorProgramado
     * Variable a asignar en sumaValorProgramado
     */
    public void setSumaValorProgramado(String sumaValorProgramado)
    {
        this.sumaValorProgramado = sumaValorProgramado;
    }

    /**
     * Retorna la variable sumaCostoTotal
     * 
     * @return sumaCostoTotal
     */
    public String getSumaCostoTotal()
    {
        return sumaCostoTotal;
    }

    /**
     * Asigna la variable sumaValorProgramado
     * 
     * @param sumaCostoTotal
     * Variable a asignar en sumaCostoTotal
     */
    public void setSumaCostoTotal(String sumaCostoTotal)
    {
        this.sumaCostoTotal = sumaCostoTotal;
    }

    /**
     * Retorna la variable sumaValorEjecutado
     * 
     * @return sumaValorEjecutado
     */
    public String getSumaValorEjecutado()
    {
        return sumaValorEjecutado;
    }

    /**
     * Asigna la variable sumaValorEjecutado
     * 
     * @param sumaValorEjecutado
     * Variable a asignar en sumaValorEjecutado
     */
    public void setSumaValorEjecutado(String sumaValorEjecutado)
    {
        this.sumaValorEjecutado = sumaValorEjecutado;
    }

    
    public boolean getPermiteAjustarComponentes() {
        return permiteAjustarComponentes;
    }

    public void setPermiteAjustarComponentes(boolean permiteAjustarComponentes) {
        this.permiteAjustarComponentes = permiteAjustarComponentes;
    }

    /**
     * Retorna la variable creaActividad
     * 
     * @return creaActividad
     */
    public boolean isCreaActividad()
    {
        return creaActividad;
    }

    /**
     * Asigna la variable creaActividad
     * 
     * @param creaActividad
     * Variable a asignar en creaActividad
     */
    public void setCreaActividad(boolean creaActividad)
    {
        this.creaActividad = creaActividad;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }

    /**
     * Asigna la variable indice
     * 
     * @param indice
     * Variable a asignar en indice
     */
    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    /**
     * Retorna la variable muestraRegistro
     * 
     * @return muestraRegistro
     */
    public boolean isMuestraRegistro()
    {
        return muestraRegistro;
    }

    /**
     * Asigna la variable muestraRegistro
     * 
     * @param muestraRegistro
     * Variable a asignar en muestraRegistro
     */
    public void setMuestraRegistro(boolean muestraRegistro)
    {
        this.muestraRegistro = muestraRegistro;
    }

    /**
     * Retorna la variable botonesInactivos
     * 
     * @return botonesInactivos
     */
    public boolean isBotonesInactivos()
    {
        return botonesInactivos;
    }

    /**
     * Asigna la variable botonesInactivos
     * 
     * @param botonesInactivos
     * Variable a asignar en botonesInactivos
     */
    public void setBotonesInactivos(boolean botonesInactivos)
    {
        this.botonesInactivos = botonesInactivos;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public Map<String, Object> getRidProyecto()
    {
        return ridProyecto;
    }

    public void setRidProyecto(Map<String, Object> ridProyecto)
    {
        this.ridProyecto = (HashMap<String, Object>) ridProyecto;
    }

    public Map<String, Object> getRidComponente()
    {
        return ridComponente;
    }

    public void setRidComponente(Map<String, Object> ridComponente)
    {
        this.ridComponente = (HashMap<String, Object>) ridComponente;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = (HashMap<String, Object>) rid;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaActividad
     * 
     * @return listaActividad
     */
    public RegistroDataModelImpl getListaActividad()
    {
        return listaActividad;
    }

    /**
     * Asigna la lista listaActividad
     * 
     * @param listaActividad
     * Variable a asignar en listaActividad
     */
    public void setListaActividad(RegistroDataModelImpl listaActividad)
    {
        this.listaActividad = listaActividad;
    }

    /**
     * Retorna la lista listaActividad
     * 
     * @return listaActividad
     */
    public RegistroDataModelImpl getListaActividadE()
    {
        return listaActividadE;
    }

    /**
     * Asigna la lista listaActividad
     * 
     * @param listaActividad
     * Variable a asignar en listaActividad
     */
    public void setListaActividadE(RegistroDataModelImpl listaActividadE)
    {
        this.listaActividadE = listaActividadE;
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