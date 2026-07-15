package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmestprevioproysControladorEnum;
import com.sysman.precontractual.enums.FrmestproypastosControladorEnum;
import com.sysman.precontractual.enums.FrmestproypastosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 17/02/2016
 *
 * @version 2 -- sdaza 01/06/2016 en la consulta de origen de datos
 * agregar en las subconsultas el campo COMPANIA
 *
 * eliminar codigo de access que no se requiere modificar nombre de
 * atributos y metodos
 *
 * debido a que se migra la forma y se presenta conflictos eliminar
 * alertas de sonar
 * 
 * @version 3, 29/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el número del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Se ajusto el redireccionar para que incluya el numero del
 * formulario.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmestproypastosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo en el
     * cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ACTIVIDAD</code>
     */
    private final String cActividad;

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
     * <code>COD_PROY</code>
     */
    private final String cCodProy = FrmestproypastosControladorEnum.COD_PROY
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPONENTENOMBRE</code>
     */
    private final String cComponenteNombre = FrmestproypastosControladorEnum.COMPONENTENOMBRE
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CANTIDAD_PLAN</code>.
     */
    private final String cCantidadPlan = FrmestproypastosControladorEnum.CANTIDAD_PLAN
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOVEDAD</code>
     */
    private final String cNovedad;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRE_FUENTE</code>
     */
    private final String cNombreFuente;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBREPROYECTO</code>
     */
    private final String cNombreProyecto = FrmestproypastosControladorEnum.NOMBREPROYECTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROYECTO</code>
     */
    private final String cProyecto;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DESCRIPCIONNUEVA</code>
     */
    private final String cDescripcionNueva = FrmestproypastosControladorEnum.DESCRIPCIONNUEVA
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>SECTORNUEVO</code>
     */
    private final String cSectorNuevo = FrmestproypastosControladorEnum.SECTORNUEVO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOT</code>
     */
    private final String cTipoT;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>T_COMPONENTE</code>
     */
    private final String cTComponente;

    private String nombreAuxiliar;
    private String nombreFuenteRecursos;
    private String descMetaProducto;
    private String nombreComponente;
    private String codigoAuxiliar;
    private RegistroDataModelImpl listaSolicitud;
    private RegistroDataModelImpl listaSolicitudE;
    private RegistroDataModelImpl listaActividad;
    private RegistroDataModelImpl listaActividadE;

    /**
     * Lista que contiene los items del combo Sector al insertar un
     * registro
     */
    private RegistroDataModelImpl listaSector;

    /**
     * Lista que contiene los items del combo Sector al modificar un
     * registro
     */
    private RegistroDataModelImpl listaSectorE;

    private String sector;
    private int indice;
    private String tComponente;
    private String cmbTipoComponente;
    private String auxiliar;
    private String vigenciaPeriodo;
    private String txtCodEstudio;

    /**
     * Atributo que gestiona la visibilidad de los botones de creacion
     * y eliminacion
     */
    private boolean vobo;

    private HashMap<String, Object> ridEP;

    /**
     * Variable que almacena el codigo del proyecto del estudio previo
     */
    private String codigoProyecto;

    /**
     * Variable que almacena el codigo de la solicitud del estudio
     * previo
     */
    private String codigoSolicitud;

    private String componente;
    private Boolean esCreador;

    /**
     * Variable que almacena el codigo del formulario desde el que se
     * accede a este controlador
     */
    private int frmOrigen;

    /**
     * Creates a new instance of FrmestproypastosControlador
     */
    @SuppressWarnings("unchecked")
    public FrmestproypastosControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cActividad = GeneralParameterEnum.ACTIVIDAD.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cAno = GeneralParameterEnum.ANO.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCodEstudio = GeneralParameterEnum.COD_ESTUDIO.getName();
        cNovedad = GeneralParameterEnum.NOVEDAD.getName();
        cProyecto = GeneralParameterEnum.PROYECTO.getName();
        cTipoT = FrmestproypastosControladorEnum.TIPOT.getValue();
        cTComponente = FrmestproypastosControladorEnum.T_COMPONENTE.getValue();

        cNombreFuente = FrmestproypastosControladorEnum.NOMBRE_FUENTE
                        .getValue();

        try {
            // 520
            numFormulario = GeneralCodigoFormaEnum.FRMESTPROYPASTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ridEP = (HashMap<String, Object>) parametrosEntrada.get("rid");

                frmOrigen = Integer.parseInt(SysmanFunciones
                                .nvl(parametrosEntrada.get(
                                                FrmestprevioproysControladorEnum.PR_FRM_ORIGEN
                                                                .getValue()),
                                                "0")
                                .toString());

                txtCodEstudio = (String) parametrosEntrada.get("txtCodEstudio");

                vigenciaPeriodo = (String) parametrosEntrada
                                .get("vigenciaPeriodo");

                vobo = Boolean.parseBoolean(
                                parametrosEntrada.get("voBo").toString());

                esCreador = Boolean.parseBoolean(
                                parametrosEntrada.get("esCreador").toString());
            }
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.ES_EST_PROY.getTable();
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();

        cargarListaSolicitud();
        cargarListaSector();

        abrirFormulario();
    }

    public void cargarListaSolicitud() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproypastosControladorUrlEnum.URL5784
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTipoT, "SCD");
        param.put(cAno, vigenciaPeriodo);

        listaSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaSolicitudE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaActividad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproypastosControladorUrlEnum.URL7155
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cTipoT, "SCD");
        param.put(cNovedad, codigoSolicitud);
        param.put(cAno, vigenciaPeriodo);

        listaActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "COD");
    }

    public void cargarListaActividadE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista <code>listaSector</code> asociada al combo
     * Sector en la ventana nuevo registro.
     */
    public void cargarListaSector() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestproypastosControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * Carga la lista <code>listaSectorE</code> asociada al combo
     * Sector en la grilla.
     */
    public void cargarListaSectorE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoSolicitud = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        registro.getCampos().put("SOLICITUD_SCD_BP", codigoSolicitud);

        registro.getCampos().remove(cActividad);
        registro.getCampos().remove(cProyecto);
        registro.getCampos().remove(cNombreProyecto);
        registro.getCampos().remove(cNombreFuente);
        registro.getCampos().remove(cDescripcionNueva);
        registro.getCampos().remove(cComponenteNombre);
        registro.getCampos().remove(cTComponente);
        registro.getCampos().remove("CANTIDAD_PRODUCTO_C");
        registro.getCampos().remove(cCantidadPlan);
        registro.getCampos().remove("VALOR");

        cmbTipoComponente = "";

        cargarListaActividad();
    }

    public void seleccionarFilaSolicitudE(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cActividad,
                        registroAux.getCampos().get(cActividad));

        codigoProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cProyecto), "")
                        .toString();

        registro.getCampos().put(cProyecto, codigoProyecto);

        registro.getCampos().put(cSectorNuevo,
                        registroAux.getCampos().get("SECTOR"));

        registro.getCampos().put("DESTINORECURSOS",
                        registroAux.getCampos().get("SECTOR"));

        registro.getCampos().put("FUENTE_RECURSOS",
                        registroAux.getCampos().get("FUENTERECURSOS"));

        cmbTipoComponente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "")
                        .toString();

        tComponente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "")
                        .toString();

        registro.getCampos().put(cNombreProyecto,
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put(cNombreFuente,
                        registroAux.getCampos().get(cNombreFuente));

        registro.getCampos().put(cDescripcionNueva,
                        registroAux.getCampos().get("DESCRIPCION"));

        registro.getCampos().put(cComponenteNombre,
                        registroAux.getCampos().get("NOMBRECOMPONENTE"));

        registro.getCampos().put("META_PROD",
                        registroAux.getCampos().get("ID_META_PRODUCTO"));

        componente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("COMPONENTE"), "")
                        .toString();

        registro.getCampos().put("CANTIDAD_PRODUCTO_C",
                        registroAux.getCampos().get("CANTIDAD"));

        registro.getCampos().put(cCantidadPlan,
                        registroAux.getCampos().get(cCantidadPlan));

        registro.getCampos().put("VALOR",
                        registroAux.getCampos().get("VALORAPROBADO"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila en el combo Sector de
     * la ventana nuevo registro.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSector(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put("DESTINORECURSOS",
                        registroAux.getCampos().get(cCodigo));

        registro.getCampos().put(cSectorNuevo,
                        registroAux.getCampos().get("DESCRIPCION"));
    }

    /**
     * Metodo ejecutado al seleccionar una fila en el combo Sector de
     * la grilla.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSectorE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(cCodigo);
    }

    public void cambiarSolicitudC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarActividadC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaActividadE(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion(Registro registro) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cTComponente, tComponente);
        registro.getCampos().put(cCodEstudio, txtCodEstudio);
        registro.getCampos().put(cCodProy, codigoProyecto);

        registro.getCampos().remove(cProyecto);
        registro.getCampos().remove(cNombreFuente);
        registro.getCampos().remove(cNombreProyecto);
        registro.getCampos().remove(cDescripcionNueva);
        registro.getCampos().remove(cComponenteNombre);
        registro.getCampos().remove(cSectorNuevo);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPONENTE", componente);
        // </CODIGO_DESARROLLADO>
        return true;
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getUrlBeanById(
                        FrmestproypastosControladorUrlEnum.URL4328.getValue());

        urlCreacion = UrlServiceUtil.getUrlBeanById(
                        FrmestproypastosControladorUrlEnum.URL5487.getValue());

        urlEliminacion = UrlServiceUtil.getUrlBeanById(
                        FrmestproypastosControladorUrlEnum.URL6470.getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cCodEstudio, txtCodEstudio);
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();
        param.put(frmOrigen == GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                        .getCodigo() ? "ridEstPrevios" : "rid", ridEP);

        param.put("txtCodEstudio", txtCodEstudio);
        param.put("vigenciaPeriodo", vigenciaPeriodo);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));

        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        cmbTipoComponente = "";
        // </CODIGO_DESARROLLADO>

    }

    public RegistroDataModelImpl getListaSector() {
        return listaSector;
    }

    public void setListaSector(RegistroDataModelImpl listaSector) {
        this.listaSector = listaSector;
    }

    public RegistroDataModelImpl getListaSectorE() {
        return listaSectorE;
    }

    public void setListaSectorE(RegistroDataModelImpl listaSectorE) {
        this.listaSectorE = listaSectorE;
    }

    public RegistroDataModelImpl getListaSolicitud() {
        return listaSolicitud;
    }

    public void setListaSolicitud(RegistroDataModelImpl listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
    }

    public RegistroDataModelImpl getListaSolicitudE() {
        return listaSolicitudE;
    }

    public void setListaSolicitudE(RegistroDataModelImpl listaSolicitudE) {
        this.listaSolicitudE = listaSolicitudE;
    }

    public RegistroDataModelImpl getListaActividad() {
        return listaActividad;
    }

    public void setListaACTIVIDAD(RegistroDataModelImpl listaActividad) {
        this.listaActividad = listaActividad;
    }

    public RegistroDataModelImpl getListaActividadE() {
        return listaActividadE;
    }

    public void setListaActividadE(RegistroDataModelImpl listaActividadE) {
        this.listaActividadE = listaActividadE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNombreAuxiliar() {
        return nombreAuxiliar;
    }

    public void setNombreAuxiliar(String nombreAuxiliar) {
        this.nombreAuxiliar = nombreAuxiliar;
    }

    public String getNombreFuenteRecursos() {
        return nombreFuenteRecursos;
    }

    public void setNombreFuenteRecursos(String nombreFuenteRecursos) {
        this.nombreFuenteRecursos = nombreFuenteRecursos;
    }

    public String getDescMetaProducto() {
        return descMetaProducto;
    }

    public void setDescMetaProducto(String descMetaProducto) {
        this.descMetaProducto = descMetaProducto;
    }

    public String getNombreComponente() {
        return nombreComponente;
    }

    public void setNombreComponente(String nombreComponente) {
        this.nombreComponente = nombreComponente;
    }

    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    public String getTxtCodEstudio() {
        return txtCodEstudio;
    }

    public void setTxtCodEstudio(String txtCodEstudio) {
        this.txtCodEstudio = txtCodEstudio;
    }

    public String getVigenciaPeriodo() {
        return vigenciaPeriodo;
    }

    public void setVigenciaPeriodo(String vigenciaPeriodo) {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCmbTipoComponente() {
        return cmbTipoComponente;
    }

    public void setCmbTipoComponente(String cmbTipoComponente) {
        this.cmbTipoComponente = cmbTipoComponente;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String gettComponente() {
        return tComponente;
    }

    public void settComponente(String tComponente) {
        this.tComponente = tComponente;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public Boolean getEsCreador() {
        return esCreador;
    }

    public void setEsCreador(Boolean esCreador) {
        this.esCreador = esCreador;
    }

    public boolean isVobo() {
        return vobo;
    }

    public void setVobo(boolean vobo) {
        this.vobo = vobo;
    }
}
