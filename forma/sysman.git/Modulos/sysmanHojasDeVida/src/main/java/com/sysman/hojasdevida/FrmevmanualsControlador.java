/*-
 * FrmevmanualsControlador.java
 *
 * 1.0
 *
 * 15/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmevmanualsControladorEnum;
import com.sysman.hojasdevida.enums.FrmevmanualsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Clase que permite registrar los manuales en el Manual De Funciones
 *
 * @version 1.0, 15/01/2018
 * @author asana Se realiza migración.
 *
 * @version 2, 23/02/2018, <strong>pespitia</strong>: Se ajusto el
 * combo cargo (5357) y cargo jefe inmediato (CB5354) y los campos
 * asociados.
 */
@ManagedBean
@ViewScoped
public class FrmevmanualsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String modeloPlantilla;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * lista dependencia
     */
    private RegistroDataModelImpl listacmbDependencia;
    /**
     * variable que almacena lista de cargos de Jefe Inmediato
     */
    private RegistroDataModelImpl listacmbCargoJI;
    /**
     * variable que almacena Lista de cargos
     */
    private RegistroDataModelImpl listacmbCargo;

    private RegistroDataModelImpl listaplantilla;
    /**
     * variable que almacena la dependencia seleccionada en el combo
     */
    private String dependencia;
    /**
     * variable que almacena cargoJI seleccionada en el combo
     */
    private String cargoJI;
    /**
     * variable que almacena escalafonJI seleccionada en el combo
     */
    private String escalafonJI;
    /**
     * variable que almacena categoriaJI seleccionada en el combo
     */
    private String categoriaJI;
    /**
     * variable que almacena cargo seleccionada en el combo
     */
    private String cargo;
    /**
     * variable que almacena escalafon seleccionada en el combo
     */
    private String escalafon;
    /**
     * variable que almacena categoria seleccionada en el combo
     */
    private String categoria;

    /**
     * Atributo que almacena el nombre de la categoria segun cargo de
     * jefe inmediato seleccionado
     */
    private String nombreCategoriaJi;
    /**
     * Atributo que almacena el nombre de la categoria segun cargo
     * seleccionado
     */
    private String nombreCategoria;

    private Date fecha;

    private String strNombreDocumento;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmevmanualsControlador
     */
    @SuppressWarnings("unchecked")
    public FrmevmanualsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVMANUAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                parametrosEntrada.remove("rid");
            }
            // <INI_ADICIONAL>
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
        enumBase = GenericUrlEnum.EV_MANUAL;
        registro = new Registro();

        buscarLlave();
        asignarOrigenDatos();
        cargarListacmbDependencia();
        cargarListacmbCargoJI();
        cargarListacmbCargo();
        cargarListaPlantilla();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista: listacmbDependencia que se encuentren activas
     */
    public void cargarListacmbDependencia() {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevmanualsControladorUrlEnum.URL7048
                                                        .getValue());

        listacmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista: listacmbCargoJI asociada al combo cargo jefe
     * inmediato (CB5354).
     */
    public void cargarListacmbCargoJI() {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevmanualsControladorUrlEnum.URL7043
                                                        .getValue());

        listacmbCargoJI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros, true,
                        FrmevmanualsControladorEnum.ID_DE_CARGO.getValue());
    }

    /**
     * Carga la lista: listacmbCargo asociada al combo cargo (CB5357).
     */
    public void cargarListacmbCargo() {
        HashMap<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevmanualsControladorUrlEnum.URL7047
                                                        .getValue());

        listacmbCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros, true,
                        FrmevmanualsControladorEnum.ID_DE_CARGO.getValue());
    }

    public void cargarListaPlantilla() {

        HashMap<String, Object> param = new HashMap<>();

        param.put(FrmevmanualsControladorEnum.TIPO.getValue(), "52");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmevmanualsControladorUrlEnum.URL389
                                                        .getValue());
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void seleccionarFilaplantilla(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            modeloPlantilla = registroAux.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString();
            fecha = SysmanFunciones.convertirAFecha(
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName())
                                            .toString());
            strNombreDocumento = registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())
                            .toString();
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarcmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilacmbCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.CARGO.getName(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.ID_DE_CARGO
                                                        .getValue()));

        registro.getCampos().put(
                        FrmevmanualsControladorEnum.NOMBRECARGO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBRE_DEL_CARGO
                                                        .getValue()));

        registro.getCampos().put(
                        FrmevmanualsControladorEnum.ESCALAFON.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.ESCALAFON
                                                        .getValue()));

        registro.getCampos().put(
                        FrmevmanualsControladorEnum.NOMBREESCALAFON.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBREESCALAFON
                                                        .getValue()));

        registro.getCampos().put(
                        FrmevmanualsControladorEnum.CATEGORIA.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.ID_DE_CATEGORIA
                                                        .getValue()));

        nombreCategoria = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmevmanualsControladorEnum.NOMBRECATEGORIA
                                        .getValue()),
                        "").toString();

        cargo = SysmanFunciones.nvl(registroAux.getCampos().get(
                        FrmevmanualsControladorEnum.ID_DE_CARGO.getValue()), "")
                        .toString();
    }

    public void cambiarcmbCargoJI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cargoJI = registroAux.getCampos().get(
                        FrmevmanualsControladorEnum.NOMBRE_DEL_CARGO.getValue())
                        .toString();
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
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
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            nombreCategoria = nombreCategoriaJi = "";

            dependencia = "";

            registro.getCampos().put(
                            FrmevmanualsControladorEnum.FECHA_INICIAL
                                            .getValue(),
                            new Date());

        }
        else {
            dependencia = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                            .getName()),
                                            "")
                            .toString();

            cargarListacmbCargoJI();

            // Recuperar categoria cargo jefe
            nombreCategoriaJi = recuperarNomCategoria(SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            FrmevmanualsControladorEnum.CATEGORIA_JEFE_INMEDIATO
                                                            .getValue()),
                                            "")
                            .toString());

            // Recuperar categoria cargo
            nombreCategoria = recuperarNomCategoria(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(FrmevmanualsControladorEnum.CATEGORIA
                                                            .getValue()),
                                            "")
                            .toString());
        }

        cargarListacmbCargo();
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtfunciones() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMEVFUNCIONES_CONTROLADOR
                                        .getCodigo()));
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("rid", css);
        parametrosEntrada.put(GeneralParameterEnum.SIGLA.getName(), registro
                        .getCampos().get(GeneralParameterEnum.SIGLA.getName()));
        parametrosEntrada.put(
                        FrmevmanualsControladorEnum.NOMBRE_MANUAL.getValue(),
                        registro.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBRE_MANUAL
                                                        .getValue()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
    }

    public void oprimirbtrequisitos() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EVREQUISITOS_CONTROLADOR
                                        .getCodigo()));
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("rid", css);
        parametrosEntrada.put(GeneralParameterEnum.SIGLA.getName(), registro
                        .getCampos().get(GeneralParameterEnum.SIGLA.getName()));
        parametrosEntrada.put(
                        FrmevmanualsControladorEnum.NOMBRE_MANUAL.getValue(),
                        registro.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBRE_MANUAL
                                                        .getValue()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
    }

    public void oprimirbtcompetencias() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_EV_COMPETENCIAS_CONTROLADOR
                                        .getCodigo()));
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put("rid", css);
        parametrosEntrada.put(GeneralParameterEnum.SIGLA.getName(), registro
                        .getCampos().get(GeneralParameterEnum.SIGLA.getName()));
        parametrosEntrada.put(
                        FrmevmanualsControladorEnum.NOMBRE_MANUAL.getValue(),
                        registro.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBRE_MANUAL
                                                        .getValue()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.NOMBREESCALAFON.getValue());
        registro.getCampos()
                        .remove(FrmevmanualsControladorEnum.NOMBREESCALAFONJI
                                        .getValue());
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.NOMBRECARGO.getValue());
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.NOMBRECARGOJI.getValue());
        registro.getCampos()
                        .remove(FrmevmanualsControladorEnum.NOMBREDEPENDENCIA
                                        .getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
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

    public void seleccionarFilacmbDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        registro.getCampos().put(FrmevmanualsControladorEnum.NOMBREDEPENDENCIA
                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBREDEPENDENCIA
                                                        .getValue()));

        registro.getCampos().remove(GeneralParameterEnum.CARGO.getName());
        limpiarCamposCargos();
        cargarListacmbCargo();
    }

    /**
     * Metodo que gestiona los eventos de seleccionar una fila del
     * combo cargo jefe inmediato (CB5354).
     *
     * @param event
     * -> Objecto que encapsula el registro asociado a la fila.
     */
    public void seleccionarFilacmbCargoJI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cargoJI = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.ID_DE_CARGO
                                                        .getValue()),
                                        "")
                        .toString();

        registro.getCampos()
                        .put(FrmevmanualsControladorEnum.CARGO_JEFE_INMEDIATO
                                        .getValue(), cargoJI);

        registro.getCampos().put(
                        FrmevmanualsControladorEnum.NOMBRECARGOJI.getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBRE_DEL_CARGO
                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevmanualsControladorEnum.ESCALAFON_JEFE_INMEDIATO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmevmanualsControladorEnum.ESCALAFON
                                                                        .getValue()));
        registro.getCampos().put(
                        FrmevmanualsControladorEnum.NOMBREESCALAFONJI
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrmevmanualsControladorEnum.NOMBREESCALAFON
                                                        .getValue()));

        registro.getCampos()
                        .put(FrmevmanualsControladorEnum.CATEGORIA_JEFE_INMEDIATO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        FrmevmanualsControladorEnum.ID_DE_CATEGORIA
                                                                        .getValue()));

        nombreCategoriaJi = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmevmanualsControladorEnum.NOMBRECATEGORIA
                                        .getValue()),
                        "").toString();
    }

    /**
     * Util para recuperar el nombre asociado a una categoria mediante
     * su codigo.
     *
     * @param categoria
     * --> Codigo de la categoria.
     * @return El nombre de la categoria.
     */
    private String recuperarNomCategoria(String categoria) {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmevmanualsControladorEnum.CATEGORIA.getValue(), categoria);

        Registro auxReg = null;

        try {
            auxReg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmevmanualsControladorUrlEnum.URL383
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (auxReg != null) {
                return SysmanFunciones
                                .nvl(auxReg.getCampos()
                                                .get(FrmevmanualsControladorEnum.NOMBRE_CATEGORIA
                                                                .getValue()),
                                                "")
                                .toString();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return "";
    }

    /** Limpia los campos asociados al combo cargos. */
    private void limpiarCamposCargos() {
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.ESCALAFON.getValue());
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.CATEGORIA.getValue());
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.NOMBREESCALAFON.getValue());
        registro.getCampos().remove(
                        FrmevmanualsControladorEnum.NOMBRECARGO.getValue());
        nombreCategoria = "";
    }

    public void oprimirImprimirWord() {
        String[] camposP = new String[3];
        String[] valores = new String[3];

        camposP[0] = "codigoPlantilla";
        camposP[1] = "fechaPlantilla";
        camposP[2] = "nombreDocDescarga";

        valores[0] = modeloPlantilla;
        valores[1] = SysmanFunciones.formatearFecha(fecha);
        valores[2] = strNombreDocumento;

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s",
                        SysmanFunciones.concatenar("'", compania, "'"));
        variablesConsultaW.put("s$numero$s",
                        registro.getCampos()
                                        .get(FrmevmanualsControladorEnum.NUMERO_MANUAL
                                                        .getValue())
                                        .toString());
        variablesConsultaW.put("s$version$s",
                        registro.getCampos()
                                        .get(FrmevmanualsControladorEnum.VERSION
                                                        .getValue())
                                        .toString());

        // variables por parametro para documento word
        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);
        String numForm = String
                        .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlash(numForm,
                        SessionUtil.getModulo(),
                        camposP, valores);

    }

    /**
     * Retorna la lista listacmbDependencia
     *
     * @return listacmbDependencia
     */
    public RegistroDataModelImpl getListacmbDependencia() {
        return listacmbDependencia;
    }

    /**
     * Asigna la lista listacmbDependencia
     *
     * @param listacmbDependencia
     * Variable a asignar en listacmbDependencia
     */
    public void setListacmbDependencia(
        RegistroDataModelImpl listacmbDependencia) {
        this.listacmbDependencia = listacmbDependencia;
    }

    /**
     * Retorna la lista listacmbCargoJI
     *
     * @return listacmbCargoJI
     */
    public RegistroDataModelImpl getListacmbCargoJI() {
        return listacmbCargoJI;
    }

    /**
     * Asigna la lista listacmbCargoJI
     *
     * @param listacmbCargoJI
     * Variable a asignar en listacmbCargoJI
     */
    public void setListacmbCargoJI(RegistroDataModelImpl listacmbCargoJI) {
        this.listacmbCargoJI = listacmbCargoJI;
    }

    /**
     * Retorna la lista listacmbCargo
     *
     * @return listacmbCargo
     */
    public RegistroDataModelImpl getListacmbCargo() {
        return listacmbCargo;
    }

    /**
     * Asigna la lista listacmbCargo
     *
     * @param listacmbCargo
     * Variable a asignar en listacmbCargo
     */
    public void setListacmbCargo(RegistroDataModelImpl listacmbCargo) {
        this.listacmbCargo = listacmbCargo;
    }

    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getCargoJI() {
        return cargoJI;
    }

    public void setCargoJI(String cargoJI) {
        this.cargoJI = cargoJI;
    }

    public String getEscalafonJI() {
        return escalafonJI;
    }

    public void setEscalafonJI(String escalafonJI) {
        this.escalafonJI = escalafonJI;
    }

    public String getCategoriaJI() {
        return categoriaJI;
    }

    public void setCategoriaJI(String categoriaJI) {
        this.categoriaJI = categoriaJI;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }

    /**
     * Retorna la variable categoria
     *
     * @return categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Asigna la variable categoria
     *
     * @param categoria
     * Variable a asignar en categoria
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }

    /**
     * Retorna la variable nombreCategoriaJi
     *
     * @return nombreCategoriaJi
     */
    public String getNombreCategoriaJi() {
        return nombreCategoriaJi;
    }

    /**
     * Asigna la variable nombreCategoriaJi
     *
     * @param nombreCategoriaJi
     * Variable a asignar en nombreCategoriaJi
     */
    public void setNombreCategoriaJi(String nombreCategoriaJi) {
        this.nombreCategoriaJi = nombreCategoriaJi;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getStrNombreDocumento() {
        return strNombreDocumento;
    }

    public void setStrNombreDocumento(String strNombreDocumento) {
        this.strNombreDocumento = strNombreDocumento;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
