package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 11/12/2015
 *
 * @author jrodrigueza
 * @version 2, 05/04/2017 Proceso de Refactoring y traslado de lógica
 * de negocio a PL/SQL.
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class AuxordendesuministrosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String menuActual;
    private String numeroOrden;
    private String claseOrden;
    private String codRequisicion;
    private String codDetalle;
    private String rid;
    private String descripcion;
    private double porcDescGlobal;
    private double porcIVAGlobal;
    /**
     * Codigo de la opcion de menu CENTRALIZACION DE REQUISICIONES.
     */
    private static final String OPC_CENTR_REQUICIONES = "400204";
    /**
     * Codigo de la opcion de menu MODIFICACIONES A CONTRATOS.
     */
    private static final String OPC_MODIF_CONTRATOS = "9020312";
    @EJB
    private EjbGeneralesRemote ejbGenerales;
	private Map<String, Object> parametroswf;
	private String modulo;

    /**
     * Creates a new instance of AuxordendesuministrosControlador
     */
    public AuxordendesuministrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
        	
            numFormulario = GeneralCodigoFormaEnum.AUXORDENDESUMINISTROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if(parametroswf != null) {
            	menuActual = SysmanFunciones.nvl(parametroswf.get("menu"),"").toString();
        	} else {
        		menuActual = SessionUtil.getMenuActual();
        	}
            if (parametros != null) {
                switch (menuActual) {
                case "90202":
                case "10020201":
                    cargarFlashPContrato(parametros);
                    break;
                case OPC_MODIF_CONTRATOS:
                    claseOrden = parametros.get("claseOrden").toString();
                    numeroOrden = parametros.get("numeroOrden").toString();
                    break;
                case OPC_CENTR_REQUICIONES:
                    codRequisicion = parametros.get("codRequisicion")
                                    .toString();
                    codDetalle = parametros.get("codDetalle").toString();
                    break;
                default:
                    break;
                }
            }
        }
        catch (SysmanException | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Carga de parametros flash cuando el formulario es invocado
     * desde PContratos.
     *
     * @param parametros
     * Parametros Flash.
     */
    private void cargarFlashPContrato(Map<String, Object> parametros) {
        claseOrden = parametros.get("claseOrden").toString();
        numeroOrden = parametros.get("numeroOrden").toString();
        porcDescGlobal = Double.parseDouble(
                        parametros.get("porcDescGlobal") == null
                            ? "2"
                            : parametros.get("porcDescGlobal")
                                            .toString());
        porcIVAGlobal = Double.parseDouble(
                        parametros.get("porcIVAGlobal") == null
                            ? "2"
                            : parametros.get("porcIVAGlobal")
                                            .toString());
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ORDENDESUMINISTRO_REQUISICION;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("MENU", SessionUtil.getMenuActual());
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * Opcion para abrir el formulario desde el formulario
         * PRequisicion.
         */
        if (SessionUtil.getMenuActual()
                        .equals(OPC_CENTR_REQUICIONES)) {
            reasignarOrigen();
        }
        else {
            abrirFormularioOtraOpcion();
        }
        // </CODIGO_DESARROLLADO>
    }

    private void abrirFormularioOtraOpcion() {
        try {
            ejbGenerales.validarOrdenesDeSuministroVacias(compania);
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        reasignarOrigen();
    }

    public void cerrarFormulario() {
        menuActual = SessionUtil.getMenuActual();
        switch (menuActual) {
        case "90202":
        case "10020201":
        case OPC_MODIF_CONTRATOS:
            registrarDetalleOrdenDeCompra();
            break;
        case OPC_CENTR_REQUICIONES:
            registrarDetalleRequisicion();
            break;
        default:
            break;
        }
    }

    /**
     * Ejecuta el procedimiento para registrar el detalle de la orden
     * de compra según las ordenes de suministro seleccionadas y con
     * cantidad por entregar.
     */
    private void registrarDetalleOrdenDeCompra() {
        boolean esModificacionContratos = menuActual.equals(OPC_MODIF_CONTRATOS)
            ? true : false;
        BigDecimal porcentajeDescuentoGlobal = BigDecimal.ZERO;
        BigDecimal porcentajeIvaGlobal = BigDecimal.ZERO;
        if (!esModificacionContratos) {
            porcentajeDescuentoGlobal = BigDecimal.valueOf(porcDescGlobal);
            porcentajeIvaGlobal = BigDecimal.valueOf(porcIVAGlobal);
        }
        int orden = Integer.parseInt(numeroOrden);
        String usuario = SessionUtil.getUser().getCodigo();
        try {
            ejbGenerales.registrarDetalleOrdenDeCompra(compania, orden,
                            claseOrden,
                            porcentajeDescuentoGlobal, porcentajeIvaGlobal,
                            esModificacionContratos, usuario);
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Ejecuta el procedimiento para crear el detalle de la
     * requisición según las ordenes de suministro seleccionadas y con
     * cantidad por entregar. Aplica solo para el módulo de
     * Planeación.
     */
    private void registrarDetalleRequisicion() {
        String usuario = SessionUtil.getUser().getCodigo();
        BigInteger requisicion = new BigInteger(codRequisicion);
        BigInteger detalle = new BigInteger(codDetalle);
        try {
            ejbGenerales.registrarDetalleRequisicion(compania, requisicion,
                            detalle, usuario);
            RequestContext.getCurrentInstance().closeDialog(null);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.VACIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getCodRequisicion() {
        return codRequisicion;
    }

    public void setCodRequisicion(String codRequisicion) {
        this.codRequisicion = codRequisicion;
    }

    public String getCodDetalle() {
        return codDetalle;
    }

    public void setCodDetalle(String codDetalle) {
        this.codDetalle = codDetalle;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}