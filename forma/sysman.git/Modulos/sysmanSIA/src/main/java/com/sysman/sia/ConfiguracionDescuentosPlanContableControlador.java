/*-
 * ConfiguracionDescuentosPlanContableControlador.java
 *
 * 1.0
 * 
 * 29/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sia.ejb.EjbSiaCeroRemote;
import com.sysman.sia.enums.ConfiguracionDescuentosPlanContableControladorEnum;
import com.sysman.sia.enums.ConfiguracionDescuentosPlanContableControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 29/11/2018
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class ConfiguracionDescuentosPlanContableControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     */
    private int anio;
    private String cuentaContable;
    private String nombreCuenta;
    private String tipoDescuento;
    private int indiceSubdescuentosplanc;
    private String Codigo;
    private boolean registraConf;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listaAnio;
    /**
     */
    private List<Registro> listaCodigoCuenta;
    /**
     */
    private RegistroDataModelImpl listaCuenta;
    // </DECLARAR_LISTAS>
    @EJB
    private EjbSiaCeroRemote ejbSiaCeroRemote;

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     */
    private RegistroDataModelImpl listaSubdescuentosplanc;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * ConfiguracionDescuentosPlanContableControlador
     */
    public ConfiguracionDescuentosPlanContableControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_DESCUENTOS_PLAN_CONTABLE_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            anio = SysmanFunciones.ano(new Date());

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
        cargarListaSubdescuentosplanc();
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
        listaSubdescuentosplanc = null;
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
        tabla = "PLAN_CONTABLE";
        cargarListaAnio();
        iniciarListasSub();
        cargarListaCuenta();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    /**
     * 
     * Carga la lista listaSubdescuentosplanc
     *
     */
    public void cargarListaSubdescuentosplanc() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionDescuentosPlanContableControladorUrlEnum.URL6081
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaSubdescuentosplanc = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConfiguracionDescuentosPlanContableControladorUrlEnum.URL7294
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
     * Carga la lista listaCodigoCuenta
     *
     */
    public void cargarListaCodigoCuenta() {

    }

    /**
     * 
     * Carga la lista listaCuenta
     *
     */
    public void cargarListaCuenta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConfiguracionDescuentosPlanContableControladorUrlEnum.URL14120
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuenta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuenta(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();

        cuentaContable = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        nombreCuenta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        iniciarListasSub();
        cargarListaCuenta();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control configurarAnio
     * 
     * 
     */
    public void cambiarconfigurarAnio() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * configurarAnio en la vista
     *
     *
     */
    public void aceptarconfigurarAnio() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbSiaCeroRemote.configurarCuentasDesc(compania,
                            anio,
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
            registraConf = false;
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * configurarAnio en la vista
     *
     *
     */
    public void cancelarconfigurarAnio() {
        // <CODIGO_DESARROLLADO>
        registraConf = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnActualiza en la vista
     *
     *
     */
    public void oprimirBtnActualiza() {

        // <CODIGO_DESARROLLADO>

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionDescuentosPlanContableControladorUrlEnum.URL12866
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CUENTA.getName(), cuentaContable);
            param.put(ConfiguracionDescuentosPlanContableControladorEnum.TIPODESCUENTO
                            .getValue(), tipoDescuento);
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnPrepararSiguiente en la
     * vista
     *
     *
     */
    public void oprimirBtnPrepararSiguiente() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        registraConf = true;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Subdescuentosplanc
     * 
     */
    public void agregarRegistroSubSubdescuentosplanc() {
        try {
            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            "PLAN_CONTABLE", registroSub.getCampos());
            listaSubdescuentosplanc.load();
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subdescuentosplanc
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubdescuentosplanc(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        try {

            Object Tipo = reg.getCampos().put(
                            ConfiguracionDescuentosPlanContableControladorEnum.TIPODESCUENTO
                                            .getValue(),
                            tipoDescuento);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConfiguracionDescuentosPlanContableControladorUrlEnum.URL12866
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CUENTA.getName(), Codigo);

            param.put(ConfiguracionDescuentosPlanContableControladorEnum.TIPODESCUENTO
                            .getValue(),
                            Tipo);

            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
            listaSubdescuentosplanc.load();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    /**
     * Metodo de eliminacion del formulario Subdescuentosplanc
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubdescuentosplanc(Registro reg) {
        try {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            "PLAN_CONTABLE", reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            listaSubdescuentosplanc.load();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void activarEdicionSubdescuentosplanc(Registro registro) {
        indiceSubdescuentosplanc = listaSubdescuentosplanc.getRowIndex();

        Codigo = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName())
                        .toString();

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subdescuentosplanc
     *
     */
    public void cancelarEdicionSubdescuentosplanc() {

    }

    public void cancelarEdicion(RowEditEvent event) {

        // </CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio
     * the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

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
     * Retorna la lista listaCodigoCuenta
     * 
     * @return listaCodigoCuenta
     */
    public List<Registro> getListaCodigoCuenta() {
        return listaCodigoCuenta;
    }

    /**
     * Asigna la lista listaCodigoCuenta
     * 
     * @param listaCodigoCuenta
     * Variable a asignar en listaCodigoCuenta
     */
    public void setListaCodigoCuenta(List<Registro> listaCodigoCuenta) {
        this.listaCodigoCuenta = listaCodigoCuenta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    /**
     * @return the listaCuenta
     */
    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    /**
     * @return the cuentaContable
     */
    public String getCuentaContable() {
        return cuentaContable;
    }

    /**
     * @param cuentaContable
     * the cuentaContable to set
     */
    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }

    /**
     * @param listaCuenta
     * the listaCuenta to set
     */
    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
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
     * @return the listaSubdescuentosplanc
     */
    public RegistroDataModelImpl getListaSubdescuentosplanc() {
        return listaSubdescuentosplanc;
    }

    /**
     * @param listaSubdescuentosplanc
     * the listaSubdescuentosplanc to set
     */
    public void setListaSubdescuentosplanc(
        RegistroDataModelImpl listaSubdescuentosplanc) {
        this.listaSubdescuentosplanc = listaSubdescuentosplanc;
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
     * @return the nombreCuenta
     */
    public String getNombreCuenta() {
        return nombreCuenta;
    }

    /**
     * @param nombreCuenta
     * the nombreCuenta to set
     */
    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    /**
     * @return the tipoDescuento
     */
    public String getTipoDescuento() {
        return tipoDescuento;
    }

    /**
     * @param tipoDescuento
     * the tipoDescuento to set
     */
    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    /**
     * @return the indiceSubdescuentosplanc
     */
    public int getIndiceSubdescuentosplanc() {
        return indiceSubdescuentosplanc;
    }

    /**
     * @param indiceSubdescuentosplanc
     * the indiceSubdescuentosplanc to set
     */
    public void setIndiceSubdescuentosplanc(int indiceSubdescuentosplanc) {
        this.indiceSubdescuentosplanc = indiceSubdescuentosplanc;
    }

    /**
     * @return the registraConf
     */
    public boolean isRegistraConf() {
        return registraConf;
    }

    /**
     * @param registraConf
     * the registraConf to set
     */
    public void setRegistraConf(boolean registraConf) {
        this.registraConf = registraConf;
    }

    // </SET_GET_ADICIONALES>
}
