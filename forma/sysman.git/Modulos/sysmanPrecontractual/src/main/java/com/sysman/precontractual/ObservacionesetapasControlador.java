package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.ObservacionesetapasControladorEnum;
import com.sysman.precontractual.enums.ObservacionesetapasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 29/12/2015
 * 
 * @modifier amonroy
 * @version 2, 01/09/2017 Proceso de Refactoring, Revision de buenas
 * practicas sugeridas por la herramienta SonarLint e implementación
 * de EJBs para consulta de parametros.
 * @version 3, 13/09/2017 Redireccionamiento a formulario
 * "Transacciones"(419)
 */
@ManagedBean
@ViewScoped
public class ObservacionesetapasControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE en el Controlador, almacena el texto
     * NOMBRE
     */
    private final String cNombre;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo SUCURSAL en el Controlador, almacena el texto
     * SUCURSAL el cual es un campo del registro principal
     */
    private final String cSucursal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NIT en el Controlador, almacena el texto NIT
     */
    private final String cNit;

    private boolean varVolver;
    private RegistroDataModelImpl listaTercero;
    private String tipoContrato;
    private String consecutivoTransaccion;
    private String consecutivoDetalle;
    private String idEtapa;
    private String nombreEtapa;
    private String titulo;
    private String observador;
    private String nombreObservador;
    private String sucursal;
    private boolean modificar;
    private String estadoEtapa;
    /**
     * Implementacion del EJB de EjbSysmanUtilRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Estructura que almacena los parametros que han sido enviados
     * desde el formulario "Transaccion"(419)
     */
    private Map<String, Object> parametrosEntrada;

    /**
     * Crea una nueva instancia de ObservacionesetapasControlador
     */
    public ObservacionesetapasControlador() {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cSucursal = GeneralParameterEnum.SUCURSAL.getName();
        cNit = ObservacionesetapasControladorEnum.NIT.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.OBSERVACIONESETAPAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                tipoContrato = parametrosEntrada.get("tipoContrato").toString();
                consecutivoTransaccion = parametrosEntrada
                                .get("consecutivoTransaccion").toString();
                consecutivoDetalle = parametrosEntrada.get("consecutivoDetalle")
                                .toString();
                idEtapa = parametrosEntrada.get("idEtapa").toString();
                nombreEtapa = parametrosEntrada.get("nombreEtapa").toString();
                titulo = idioma.getString("TB_TB3502")
                                .replace("s$idEtapa$s", idEtapa)
                                .replace("s$nombreEtapa$s",
                                                nombreEtapa.toUpperCase());
                estadoEtapa = parametrosEntrada.get("estadoEtapa").toString();
                modificar = "A".equals(estadoEtapa);
                registro = new Registro(new HashMap<String, Object>());
            }

        }
        catch (SysmanException ex) {
            Logger.getLogger(ObservacionesetapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.OBSERVACIONES_ETA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(ObservacionesetapasControladorEnum.TIPO_CONTRATO
                        .getValue(), tipoContrato);
        parametrosListado.put(ObservacionesetapasControladorEnum.CONSECUTIVOTX
                        .getValue(), consecutivoTransaccion);
        parametrosListado.put(ObservacionesetapasControladorEnum.CONSECUTIVODET
                        .getValue(), consecutivoDetalle);
    }

    @Override
    public void iniciarListas() {
        cargarListaTercero();
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * Redirecciona al formulario Transaccion(419)
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ObservacionesetapasControladorUrlEnum.URL5109
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNit);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * Permite pbtener el nombre del tercero seleccionado y la
     * sucursal a la cual pertenece
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        observador = SysmanFunciones.nvl(registroAux.getCampos().get(cNit), "")
                        .toString();
        registro.getCampos().put(cNit, observador);
        nombreObservador = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        registro.getCampos().put(cNombre, nombreObservador);
        sucursal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cSucursal), "")
                        .toString();
        registro.getCampos().put(cSucursal, sucursal);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css != null) {
            observador = registro.getCampos().get(cNit).toString();
            nombreObservador = registro.getCampos().get(cNombre).toString();
            sucursal = registro.getCampos().get(cSucursal).toString();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * Adiciona los campos necesarios para realizar la insercion de un
     * registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos()
                            .put(ObservacionesetapasControladorEnum.TIPOCONTRATO
                                            .getValue(), tipoContrato);
            registro.getCampos()
                            .put(ObservacionesetapasControladorEnum.TRANSACCION
                                            .getValue(),
                                            consecutivoTransaccion);
            registro.getCampos()
                            .put(ObservacionesetapasControladorEnum.CONSECUTIVODETALLE
                                            .getValue(), consecutivoDetalle);
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "OBSERVACIONES_ETA",
                            SysmanFunciones.concatenar(
                                            "     COMPANIA           = ''",
                                            compania, "'' ",
                                            " AND TIPOCONTRATO       = ''",
                                            tipoContrato, "'' ",
                                            " AND TRANSACCION        = ",
                                            consecutivoTransaccion, " ",
                                            " AND CONSECUTIVODETALLE = ",
                                            consecutivoDetalle, " "),
                            "ID",
                            "1");
            registro.getCampos().put(
                            ObservacionesetapasControladorEnum.ID.getValue(),
                            consecutivo);
            registro.getCampos()
                            .put(ObservacionesetapasControladorEnum.OBSERVADOR
                                            .getValue(), observador);
            registro.getCampos().put(cSucursal, sucursal);
            registro.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registro.getCampos().remove(cNit);
            registro.getCampos().remove(cNombre);
            registro.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());

        }
        catch (SystemException ex) {
            Logger.getLogger(ObservacionesetapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

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
     * Remueve los campos que no son necesarios en la insercion o
     * actualizacion de un registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNit);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * Adiciona los campos que han sido removidos en el metodo
     * actualizarAntes() y son necesarios para su visualizacion en el
     * formulario
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cNit, observador);
        registro.getCampos().put(cNombre, nombreObservador);
        // </CODIGO_DESARROLLADO>
        return true;
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getConsecutivoTransaccion() {
        return consecutivoTransaccion;
    }

    public void setConsecutivoTransaccion(String consecutivoTransaccion) {
        this.consecutivoTransaccion = consecutivoTransaccion;
    }

    public String getConsecutivoDetalle() {
        return consecutivoDetalle;
    }

    public void setConsecutivoDetalle(String consecutivoDetalle) {
        this.consecutivoDetalle = consecutivoDetalle;
    }

    public String getIdEtapa() {
        return idEtapa;
    }

    public void setIdEtapa(String idEtapa) {
        this.idEtapa = idEtapa;
    }

    public String getNombreEtapa() {
        return nombreEtapa;
    }

    public void setNombreEtapa(String nombreEtapa) {
        this.nombreEtapa = nombreEtapa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isVarVolver() {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    public boolean isModificar() {
        return modificar;
    }

    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

}
