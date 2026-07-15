package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmsoporteproysControladorEnum;
import com.sysman.precontractual.enums.FrmsoporteproysControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 01/04/2016
 * 
 * @version 2, 30/08/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de conexiones por el esquema
 * {@code ConectorPool.ESQUEMA_SYSMAN }.
 * <li>Refactoring de sentencias SQL.
 * <li>Se ajusto el redireccionar para que incluya el numero del
 * formulario.
 */
@ManagedBean
@ViewScoped
public class FrmsoporteproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_T_ASPTEC</code>
     */
    private final String cCodTAsptec;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_ESTUDIO</code>
     */
    private final String cCodEstudio;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_ASPTEC</code>
     */
    private final String cCodAsptec;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CEDULA</code>
     */
    private final String cCedula;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ENTIDAD</code>
     */
    private final String cEntidad;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE</code>
     */
    private final String cNombre;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRETERCERO</code>
     */
    private final String cNombreTercero;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBREESTADO</code>
     */
    private final String cNombreEstado;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRENOVEDAD</code>
     */
    private final String cNombreNovedad;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NIT</code>
     */
    private final String cNit;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>RESPONSABLE</code>
     */
    private final String cResponsable;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL</code>
     */
    private final String cSucursal;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SUCURSAL_RESPONSABLE</code>
     */
    private final String cSucursalResponsable;

    /**
     * Atributo auxliar el cual es asignado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String aspecto;
    private HashMap<String, Object> rid;
    private String codEstudio;
    private String codAspecto;
    private RegistroDataModelImpl listaCmbTAspecto;
    private RegistroDataModelImpl listaCmbTAspectoE;
    private RegistroDataModelImpl listaDescripcion;
    private RegistroDataModelImpl listaDescripcionE;
    private RegistroDataModelImpl listaResponsableEntrega;
    private RegistroDataModelImpl listaResponsableEntregaE;

    /**
     * Lista que contiene los items del combo Entidad al ingresar un
     * nuevo registro.
     */
    private RegistroDataModelImpl listaEntidad;

    /**
     * Lista que contiene los items del combo Entidad al editar un
     * registro.
     */
    private RegistroDataModelImpl listaEntidadE;

    private String auxiliar;
    private String nombreAspecto;
    private boolean esCreador;

    private String vigenciaPeriodo;

    /**
     * Variable que almacena la sucursal de la entidad seleccionada en
     * el combo Entidad
     */
    private String sucursal;

    /**
     * Variable que almacena la sucursal del responsable seleccionado
     * en el combo Responsable
     */
    private String sucursalResponsable;

    /**
     * Creates a new instance of FrmsoporteproysControlador
     */
    public FrmsoporteproysControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCodTAsptec = FrmsoporteproysControladorEnum.COD_T_ASPTEC.getValue();
        cCodEstudio = GeneralParameterEnum.COD_ESTUDIO.getName();
        cCedula = FrmsoporteproysControladorEnum.CEDULA.getValue();
        cCodAsptec = FrmsoporteproysControladorEnum.COD_ASPTEC.getValue();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cEntidad = GeneralParameterEnum.ENTIDAD.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNombreNovedad = FrmsoporteproysControladorEnum.NOMBRENOVEDAD
                        .getValue();

        cNombreTercero = FrmsoporteproysControladorEnum.NOMBRETERCERO
                        .getValue();

        cNombreEstado = FrmsoporteproysControladorEnum.NOMBREESTADO.getValue();
        cNit = FrmsoporteproysControladorEnum.NIT.getValue();
        cResponsable = GeneralParameterEnum.RESPONSABLE.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cSucursalResponsable = GeneralParameterEnum.SUCURSAL_RESPONSABLE
                        .getName();

        try {
            // 605
            numFormulario = GeneralCodigoFormaEnum.FRMSOPORTEPROYS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                codEstudio = (String) parametrosEntrada.get("codEstudio");
                vigenciaPeriodo = parametrosEntrada.get("vigenciaPeriodo")
                                .toString();
                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get("esCreador").toString());
            }
        }
        catch (Exception ex) {
            Logger.getLogger(FrmsoporteproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_ASP_ESTPR;
        registro = new Registro();

        aspecto = "";

        buscarLlave();
        reasignarOrigen();
        cargarListaCmbTAspecto();
        cargarListaCmbTAspectoE();
        cargarListaResponsableEntrega();
        cargarListaResponsableEntregaE();
        cargarListaEntidad();
        cargarListaEntidadE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cCodEstudio, codEstudio);
        parametrosListado.put(cCodTAsptec, aspecto);
    }

    public void cargarListaCmbTAspecto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL6511
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCmbTAspecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodTAsptec);
    }

    public void cargarListaCmbTAspectoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL6511
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaCmbTAspectoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodTAsptec);
    }

    public void cargarListaDescripcion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL7648
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodTAsptec, aspecto);

        listaDescripcion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodAsptec);
    }

    public void cargarListaDescripcionE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL7648
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodTAsptec, aspecto);

        listaDescripcionE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodAsptec);
    }

    public void cargarListaResponsableEntrega() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL8992
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaResponsableEntrega = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);
    }

    public void cargarListaResponsableEntregaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL8992
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaResponsableEntregaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCedula);
    }

    /**
     * Carga la lista: {@code listaEntidad} asociada al combo Entidad.
     */
    public void cargarListaEntidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaEntidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    /**
     * Carga la lista: {@code listaEntidadE} asociada al combo
     * Entidad.
     */
    public void cargarListaEntidadE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmsoporteproysControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaEntidadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    public void cambiarDescripcionC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodAsptec, codAspecto);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbTAspecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        aspecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodTAsptec), "")
                        .toString();

        nombreAspecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();

        reasignarOrigen();
        cargarListaDescripcion();
        cargarListaDescripcionE();
    }

    public void seleccionarFilaDescripcion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cNombreNovedad,
                        registroAux.getCampos().get(cCodAsptec));

        registro.getCampos().put(cCodAsptec,
                        registroAux.getCampos().get(cCodAsptec));

        registro.getCampos().put(cNombre,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaDescripcionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codAspecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodAsptec), "")
                        .toString();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
    }

    public void seleccionarFilaResponsableEntrega(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cResponsable,
                        registroAux.getCampos().get(cCedula));

        registro.getCampos().put(cSucursalResponsable,
                        registroAux.getCampos().get(cSucursal));
    }

    public void seleccionarFilaResponsableEntregaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCedula), "")
                        .toString();

        sucursalResponsable = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaEntidad} asociada al combo Entidad.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEntidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cEntidad, registroAux.getCampos().get(cNit));

        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * {@code listaEntidadE} asociada al combo Entidad.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEntidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cNit), "")
                        .toString();

        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();
    }

    public String getNombreAspecto() {
        return nombreAspecto;
    }

    public void setNombreAspecto(String nombreAspecto) {
        this.nombreAspecto = nombreAspecto;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodEstudio, codEstudio);
        registro.getCampos().put(cCodTAsptec, aspecto);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombre);
        registro.getCampos().remove(cNombreTercero);
        registro.getCampos().remove(cNombreEstado);
        registro.getCampos().remove("NOMBREENTIDAD");

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodEstudio);
        registro.getCampos().remove(cCodTAsptec);

        registro.getCampos().put(cSucursalResponsable, sucursalResponsable);
        registro.getCampos().put(cSucursal, sucursal);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario.
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        sucursalResponsable = SysmanFunciones
                        .nvl(registro.getCampos().get(cSucursalResponsable), "")
                        .toString();

        sucursal = SysmanFunciones.nvl(registro.getCampos().get(cSucursal), "")
                        .toString();
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();
        param.put("ridEstPrevios", rid);
        param.put("vigenciaPeriodo", vigenciaPeriodo);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaCmbTAspecto() {
        return listaCmbTAspecto;
    }

    public void setListaCmbTAspecto(RegistroDataModelImpl listaCmbTAspecto) {
        this.listaCmbTAspecto = listaCmbTAspecto;
    }

    public RegistroDataModelImpl getListaCmbTAspectoE() {
        return listaCmbTAspectoE;
    }

    public void setListaCmbTAspectoE(RegistroDataModelImpl listaCmbTAspectoE) {
        this.listaCmbTAspectoE = listaCmbTAspectoE;
    }

    public RegistroDataModelImpl getListaDescripcion() {
        return listaDescripcion;
    }

    public void setListaDescripcion(RegistroDataModelImpl listaDescripcion) {
        this.listaDescripcion = listaDescripcion;
    }

    public RegistroDataModelImpl getListaDescripcionE() {
        return listaDescripcionE;
    }

    public void setListaDescripcionE(RegistroDataModelImpl listaDescripcionE) {
        this.listaDescripcionE = listaDescripcionE;
    }

    public RegistroDataModelImpl getListaResponsableEntrega() {
        return listaResponsableEntrega;
    }

    public void setListaResponsableEntrega(
        RegistroDataModelImpl listaResponsableEntrega) {
        this.listaResponsableEntrega = listaResponsableEntrega;
    }

    public RegistroDataModelImpl getListaResponsableEntregaE() {
        return listaResponsableEntregaE;
    }

    public void setListaResponsableEntregaE(
        RegistroDataModelImpl listaResponsableEntregaE) {
        this.listaResponsableEntregaE = listaResponsableEntregaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAspecto() {
        return aspecto;
    }

    public void setAspecto(String aspecto) {
        this.aspecto = aspecto;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public RegistroDataModelImpl getListaEntidad() {
        return listaEntidad;
    }

    public void setListaEntidad(RegistroDataModelImpl listaEntidad) {
        this.listaEntidad = listaEntidad;
    }

    public RegistroDataModelImpl getListaEntidadE() {
        return listaEntidadE;
    }

    public void setListaEntidadE(RegistroDataModelImpl listaEntidadE) {
        this.listaEntidadE = listaEntidadE;
    }
}
