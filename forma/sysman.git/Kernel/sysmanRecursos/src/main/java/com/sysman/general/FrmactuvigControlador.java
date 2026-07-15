package com.sysman.general;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectosCeroGeneralRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmactuvigControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author lcortes
 * @version 1, 19/08/2015
 * 
 * @author ybecerra
 * @version 2, 13/09/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class FrmactuvigControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el año seleccionado del combo Año inicial
     */
    private String anoIni;

    /**
     * Atributo que almacena el año seleccionado del combo Año final
     */
    private String anoFin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String opcion;
    private String titulo;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el combo Año Inicial
     */
    private List<Registro> listaAnoIni;

    /**
     * Listado de registros para el combo Año Final
     */
    private List<Registro> listaAnoFin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbBancoProyectosCeroGeneralRemote ejbBancoProyectoCero;

    /**
     * Crea una nueva instancia de FrmactuvigControlador
     */
    public FrmactuvigControlador() {

        super();
        compania = SessionUtil.getCompania();

        try {
            // 128
            numFormulario = GeneralCodigoFormaEnum.FRMACTUVIG_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmactuvigControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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

    /**
     * Metodo ejecutado que se visualiza el formulario modal, en este
     * se deben leer los parametros del formulario
     */
    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        cargarListaAnoIni();

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoIni
     */
    public void cargarListaAnoIni() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            if ("NivCum".equals(opcion)) {
                titulo = idioma.getString("TB_TB3552");

                listaAnoIni = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmactuvigControladorUrlEnum.URL2395
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

            }
            else if ("NivPlan".equals(opcion)) {
                titulo = idioma.getString("TB_TB3553");
                listaAnoIni = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmactuvigControladorUrlEnum.URL2971
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaAnoFin
     */
    public void cargarListaAnoFin() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoIni);
            if ("NivCum".equals(opcion)) {

                listaAnoFin = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmactuvigControladorUrlEnum.URL3552
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

            }
            else if ("NivPlan".equals(opcion)) {
                listaAnoFin = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmactuvigControladorUrlEnum.URL4746
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Ejec en la vista
     *
     */
    public void oprimirEjec() {
        // <CODIGO_DESARROLLADO>
        if (anoIni == null || anoFin == null || anoIni.isEmpty()
            || anoFin.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2294"));
            return;
        }
        try {
            BigDecimal num = ejbBancoProyectoCero.insertarVigencias(compania,
                            Integer.valueOf(anoIni), Integer.valueOf(anoFin),
                            opcion, SessionUtil.getUser().getCodigo());

            String[] dato = new String[3];
            dato[0] = anoFin;
            dato[1] = String.valueOf(num);
            dato[2] = anoIni;
            RequestContext.getCurrentInstance().closeDialog(dato);
        }

        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BTCancelar en la vista
     *
     */
    public void oprimirBTCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAnoIni() {
        cargarListaAnoFin();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anoIni
     * 
     * @return anoIni
     */
    public String getAnoIni() {
        return anoIni;
    }

    /**
     * Asigna la variable anoIni
     * 
     * @param anoIni
     * Variable a asignar en anoIni
     */
    public void setAnoIni(String anoIni) {
        this.anoIni = anoIni;
    }

    /**
     * Retorna la variable anoFin
     * 
     * @return anoFin
     */
    public String getAnoFin() {
        return anoFin;
    }

    /**
     * Asigna la variable anoFin
     * 
     * @param anoFin
     * Variable a asignar en anoFin
     */
    public void setAnoFin(String anoFin) {
        this.anoFin = anoFin;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion() {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable titulo
     * 
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna la variable titulo
     * 
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaAnoIni
     * 
     * @return listaAnoIni
     */
    public List<Registro> getListaAnoIni() {
        return listaAnoIni;
    }

    /**
     * Asigna la lista listaAnoIni
     * 
     * @param listaAnoIni
     * Variable a asignar en listaAnoIni
     */
    public void setListaAnoIni(List<Registro> listaAnoIni) {
        this.listaAnoIni = listaAnoIni;
    }

    /**
     * Retorna la lista listaAnoFin
     * 
     * @return listaAnoFin
     */
    public List<Registro> getListaAnoFin() {
        return listaAnoFin;
    }

    /**
     * Asigna la lista listaAnoFin
     * 
     * @param listaAnoFin
     * Variable a asignar en listaAnoFin
     */
    public void setListaAnoFin(List<Registro> listaAnoFin) {
        this.listaAnoFin = listaAnoFin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}