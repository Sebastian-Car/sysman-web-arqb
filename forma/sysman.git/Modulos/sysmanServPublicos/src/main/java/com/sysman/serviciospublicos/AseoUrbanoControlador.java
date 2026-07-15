/*-
 * AseoUrbanoControlador.java
 *
 * 1.0
 *
 * 18/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.AseoUrbanoControladorEnum;
import com.sysman.serviciospublicos.enums.AseoUrbanoControladorUrlEnum;

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

/**
 * Clase encargada de la gestion de la vista del formulario de aseo
 * urbano.
 *
 * @version 1.0, 18/11/2016
 * @author vmolano
 *
 * @version 2.0, 15/05/2017, pespitia: <br>
 * Refactoring<br>
 * Manejo de EJBs. <br>
 * Se reemplazo el texto quemado por etiquetas del archivo properties.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class AseoUrbanoControlador extends BeanBaseDatosAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que almacena el valor del enumerado
     * <code>AseoUrbanoControladorEnum.VALORASEO</code>
     */
    private final String cValorAseo;

    /**
     * Constante a nivel de clase que almacena el valor del enumerado
     * <code>GeneralParameterEnum.DATE_MODIFIED</code>
     */
    private final String cDateModified;

    /**
     * Constante a nivel de clase que almacena el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGORUTA</code>
     */
    private final String cCodigoRuta;

    /**
     * Constante a nivel de clase que aloja la tabla del enumerado
     * <code>GenericUrlEnum.SP_HISTORIA_EXTERNA_DESACTIVA</code>
     */
    private final String spHistoriaExternaDesactiva;

    /**
     * Constante a nivel de clase que aloja la tabla del enumerado
     * <code>GenericUrlEnum.SP_HISTORIA_EXTERNA</code>
     */
    private final String spHistoriaExterna;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que recibe por flash el c�digo de ruta del suscriptor
     * actual.
     */
    private String codigoRuta;

    /**
     * Variable que recibe por flash el banco de pago del suscriptor.
     */
    private String bancoPerProceso;

    /**
     * Variable que recibe por flash el a�o del suscriptor actual.
     */
    private String anoActual;

    /**
     * Variable que recibe por flash el periodo del suscriptor actual.
     */
    private String periodoActual;

    /**
     * Variable que recibe por flash el ciclo del suscriptor actual.
     */
    private String cicloActual;

    /**
     * Variable que controla la aparici�n del dialogo que pide el
     * valor para modificar el aseo urbano.
     */
    private boolean verNuevoValor;

    /**
     * Variable que almacena el valor digitado por el usuario como
     * nuevo valor del aseo urbano.
     */
    private double nuevoValor;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Listado de servicios de aseo urbano que se han desactivado de
     * los diferentes periodos.
     */
    private List<Registro> listaSecundario238;
    /**
     * Listado de servicios de aseo urbano activos para los diferentes
     * periodos.
     */
    private List<Registro> listaSuburbanosactivos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario Secundario238
     */
    private Registro registroSubSecundario238;
    /**
     * Atributo de referencia para el subformulario subUrbanosActivos
     */
    private Registro registroSubsubUrbanosActivos;

    // </DECLARAR_ADICIONALES>

    // <DECLARAR EJBs>
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    // </DECLARAR EJBs>
    /**
     * Crea una nueva instancia de AseoUrbanoControlador
     */
    public AseoUrbanoControlador() {
        super();

        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        cValorAseo = AseoUrbanoControladorEnum.VALORASEO.getValue();
        cDateModified = GeneralParameterEnum.DATE_MODIFIED.getName();
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();

        spHistoriaExternaDesactiva = GenericUrlEnum.SP_HISTORIA_EXTERNA_DESACTIVA
                        .getTable();

        spHistoriaExterna = GenericUrlEnum.SP_HISTORIA_EXTERNA.getTable();

        try {
            numFormulario = GeneralCodigoFormaEnum.ASEO_URBANO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codigoRuta = (String) parametrosEntrada.get("codigoRuta");
                bancoPerProceso = (String) parametrosEntrada
                                .get("bancoPerProceso");
                anoActual = (String) parametrosEntrada.get("ano");
                periodoActual = (String) parametrosEntrada.get("periodo");
                cicloActual = (String) parametrosEntrada.get("ciclo");
            }
            else {
                SessionUtil.redireccionarMenu();
            }

            registroSubSecundario238 = new Registro(
                            new HashMap<String, Object>());
            registroSubsubUrbanosActivos = new Registro(
                            new HashMap<String, Object>());
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
        cargarListaSecundario238();
        cargarListaSuburbanosactivos();
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
        listaSecundario238 = null;
        listaSuburbanosactivos = null;
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
        tabla = "";

        iniciarListasSub();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }

    /**
     *
     * Carga la lista listaSecundario238
     *
     * Carga el listado de aseo urbano que ha sido desactivado.
     */
    public void cargarListaSecundario238() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(cCodigoRuta, codigoRuta);

            listaSecundario238 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_HISTORIA_EXTERNA_DESACTIVA
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spHistoriaExternaDesactiva));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaSuburbanosactivos
     *
     * Listado de aseo urbano activo para los diferentes periodos.
     */
    public void cargarListaSuburbanosactivos() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(cCodigoRuta, codigoRuta);

            listaSuburbanosactivos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AseoUrbanoControladorUrlEnum.URL9911
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spHistoriaExterna));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>

    /**
     *
     * Metodo ejecutado al oprimir el boton btCerrar en la vista
     *
     * Si el valor del aseo tuvo cambios, se envia dicho valor para
     * ser actualizado en el formulario principal.
     *
     */
    public void oprimirbtCerrar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance()
                        .closeDialog(nuevoValor > 0 ? nuevoValor : null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dgNuevoValor en la vista
     *
     * Se recibe el nuevo valor de aseo y se actualiza el registro
     * correspondiente.
     *
     */
    public void aceptardgNuevoValor() {
        // <CODIGO_DESARROLLADO>

        if (nuevoValor > 0) {
            try {
                HashMap<String, Object> parSet = new HashMap<>();
                parSet.put(cValorAseo, nuevoValor);
                parSet.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
                parSet.put(cDateModified, new Date());
                parSet.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parSet.put(GeneralParameterEnum.CICLO.getName(), cicloActual);
                parSet.put(cCodigoRuta, codigoRuta);
                parSet.put(GeneralParameterEnum.ANO.getName(), anoActual);
                parSet.put(GeneralParameterEnum.PERIODO.getName(),
                                periodoActual);

                Parameter parametro = new Parameter();
                parametro.setFields(parSet);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                AseoUrbanoControladorUrlEnum.URL23167
                                                                .getValue());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parametro);

                cargarListaSuburbanosactivos();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            finally {
                verNuevoValor = false;
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3162"));
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton cmbAseoUrbano en la vista
     *
     * SP_HISTORIA_EXTERNA_DESACTIVA.
     *
     */
    public void oprimircmbAseoUrbano() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbServiciosPublicosCero.desactivarServicioAseoUrbano(compania,
                            Integer.parseInt(cicloActual), codigoRuta,
                            Integer.parseInt(anoActual), periodoActual,
                            usuario);

            cargarListaSecundario238();
            cargarListaSuburbanosactivos();

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdActivarAseo en la vista
     *
     *
     */
    public void oprimircmdActivarAseo() {
        // <CODIGO_DESARROLLADO>

        if ("".equals(bancoPerProceso)) {
            try {
                ejbServiciosPublicosCero.activarServicioAseoUrbano(compania,
                                Integer.parseInt(cicloActual), codigoRuta,
                                Integer.parseInt(anoActual), periodoActual,
                                usuario);

                cargarListaSecundario238();
                cargarListaSuburbanosactivos();

                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3163"));
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Comando246 en la vista
     *
     *
     *
     */
    public void oprimirComando246() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), cicloActual);
        param.put(GeneralParameterEnum.PERIODO.getName(), periodoActual);
        param.put(GeneralParameterEnum.ANO.getName(), anoActual);
        param.put(cCodigoRuta, codigoRuta);

        Registro rAseoU = null;

        try {
            rAseoU = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AseoUrbanoControladorUrlEnum.URL7759
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rAseoU != null) {
            if (Integer.parseInt(rAseoU.getCampos()
                            .get("VALORASEOANT").toString()) > 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3164"));
                verNuevoValor = false;
            }
            else {
                verNuevoValor = true;
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3165"));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Secundario238
     */
    public void agregarRegistroSubSecundario238() {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    /**
     * Metodo de edicion del formulario Secundario238
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSecundario238(RowEditEvent event) {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Secundario238
     */
    public void cancelarEdicionSecundario238() {
        cargarListaSecundario238();
        cargarListaSuburbanosactivos();
    }

    /**
     * Metodo de insercion del formulario Suburbanosactivos
     */
    public void agregarRegistroSubSuburbanosactivos() {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    /**
     * Metodo de edicion del formulario Suburbanosactivos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSuburbanosactivos(RowEditEvent event) {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Suburbanosactivos
     */
    public void cancelarEdicionSuburbanosactivos() {
        cargarListaSuburbanosactivos();
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
        /*
         * FR1213-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Me!cmbAseoUrbano.SetFocus DoCmd.Maximize End Sub
         */
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
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario() {

        RequestContext.getCurrentInstance()
                        .closeDialog(nuevoValor > 0 ? nuevoValor : null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSecundario238
     *
     * @return listaSecundario238
     */
    public List<Registro> getListaSecundario238() {
        return listaSecundario238;
    }

    /**
     * Asigna la lista listaSecundario238
     *
     * @param listaSecundario238
     * Variable a asignar en listaSecundario238
     */
    public void setListaSecundario238(List<Registro> listaSecundario238) {
        this.listaSecundario238 = listaSecundario238;
    }

    /**
     * Retorna la lista listaSuburbanosactivos
     *
     * @return listaSuburbanosactivos
     */
    public List<Registro> getListaSuburbanosactivos() {
        return listaSuburbanosactivos;
    }

    /**
     * Asigna la lista listaSuburbanosactivos
     *
     * @param listaSuburbanosactivos
     * Variable a asignar en listaSuburbanosactivos
     */
    public void setListaSuburbanosactivos(
        List<Registro> listaSuburbanosactivos) {
        this.listaSuburbanosactivos = listaSuburbanosactivos;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSecundario238
     *
     * @return registroSubSecundario238
     */
    public Registro getRegistroSubSecundario238() {
        return registroSubSecundario238;
    }

    /**
     * Asigna el objeto registroSubSecundario238
     *
     * @param registroSubSecundario238
     * Variable a asignar en registroSubSecundario238
     */
    public void setRegistroSubSecundario238(Registro registroSubSecundario238) {
        this.registroSubSecundario238 = registroSubSecundario238;
    }

    /**
     * Retorna el objeto registroSubsubUrbanosActivos
     *
     * @return registroSubsubUrbanosActivos
     */
    public Registro getRegistroSubsubUrbanosActivos() {
        return registroSubsubUrbanosActivos;
    }

    /**
     * Asigna el objeto registroSubsubUrbanosActivos
     *
     * @param registroSubsubUrbanosActivos
     * Variable a asignar en registroSubsubUrbanosActivos
     */
    public void setRegistroSubsubUrbanosActivos(
        Registro registroSubsubUrbanosActivos) {
        this.registroSubsubUrbanosActivos = registroSubsubUrbanosActivos;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getBancoPerProceso() {
        return bancoPerProceso;
    }

    public void setBancoPerProceso(String bancoPerProceso) {
        this.bancoPerProceso = bancoPerProceso;
    }

    public String getAnoActual() {
        return anoActual;
    }

    public void setAnoActual(String anoActual) {
        this.anoActual = anoActual;
    }

    public String getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(String periodoActual) {
        this.periodoActual = periodoActual;
    }

    public String getCicloActual() {
        return cicloActual;
    }

    public void setCicloActual(String cicloActual) {
        this.cicloActual = cicloActual;
    }

    public boolean isVerNuevoValor() {
        return verNuevoValor;
    }

    public void setVerNuevoValor(boolean verNuevoValor) {
        this.verNuevoValor = verNuevoValor;
    }

    public double getNuevoValor() {
        return nuevoValor;
    }

    public void setNuevoValor(double nuevoValor) {
        this.nuevoValor = nuevoValor;
    }

    // </SET_GET_ADICIONALES>
}
