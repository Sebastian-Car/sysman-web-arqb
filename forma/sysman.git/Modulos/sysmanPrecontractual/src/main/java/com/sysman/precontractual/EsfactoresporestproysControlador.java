package com.sysman.precontractual;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EsfactoresporestproysControladorEnum;
import com.sysman.precontractual.enums.EsfactoresporestproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author vmolano
 * @version 1, 06/07/2016
 * 
 * @version 2, 24/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazo el numero del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Reemplazo de creacion de conexiones por el esquema:
 * <code>ConectorPool.ESQUEMA_SYSMAN</code>.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class EsfactoresporestproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOESTUDIO</code>
     */
    private final String cCodigoEstudio;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOMODALIDAD</code>
     */
    private final String cCodigoModalidad;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOCRITERIO</code>
     */
    private final String cCodigoCriterio;

    private String codigoEstudio;
    private String codigoModalidad;
    private String codigoCriterio;
    private int totalPuntaje;
    private String titulo;
    private Map<String, Object> ridEstPrevio;
    private String vigenciaPeriodo;
    /*
     * permite verificar si viene con restriccion de operaciones de
     * crear, editar o insertar desde el formulario invocado
     */
    private boolean vobo;

    /**
     * Atributo que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EsfactoresporestproysControlador
     */
    public EsfactoresporestproysControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        cCodigoEstudio = EsfactoresporestproysControladorEnum.CODIGOESTUDIO
                        .getValue();

        cCodigoModalidad = EsfactoresporestproysControladorEnum.CODIGOMODALIDAD
                        .getValue();

        cCodigoCriterio = EsfactoresporestproysControladorEnum.CODIGOCRITERIO
                        .getValue();

        try {
            // 722
            numFormulario = GeneralCodigoFormaEnum.ESFACTORESPORESTPROYS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            Map<String, Object> parametros = SessionUtil.getFlash();
            

            if (parametros != null) {
                titulo = (String) parametros.get("titulo");
                codigoEstudio = (String) parametros.get("codigoEstudio");
                codigoModalidad = (String) parametros.get("codigoModalidad");
                codigoCriterio = (String) parametros.get("codigoCriterio");
                vigenciaPeriodo = (String) parametros.get("vigenciaPeriodo");
                
                vobo = Boolean.parseBoolean(
                                parametros.get("vobo").toString());
                ridEstPrevio = (Map<String, Object>) parametros.get("ridEstPrevios");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(EsfactoresporestproysControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.ES_DFACTORES;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cCodigoEstudio, codigoEstudio);
        parametrosListado.put(cCodigoModalidad, codigoModalidad);
        parametrosListado.put(cCodigoCriterio, codigoCriterio);
    }

    public int getTotalPuntaje() {
        return totalPuntaje;
    }

    public void setTotalPuntaje(int totalPuntaje) {
        this.totalPuntaje = totalPuntaje;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        totalPuntaje = obtenerTotalPuntaje();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        String criterio = SysmanFunciones.concatenar(
                        "      COMPANIA = ''", compania,
                        "'' AND CODIGOESTUDIO = ''", codigoEstudio,
                        "'' AND CODIGOMODALIDAD = ''", codigoModalidad,
                        "'' AND CODIGOCRITERIO = ''", codigoCriterio, "''");

        String inicial = SysmanFunciones.concatenar(codigoCriterio, "01");

        try {
            String cod = SysmanFunciones.padl(Long.toString(
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            GenericUrlEnum.ES_DFACTORES
                                                            .getTable(),
                                            criterio,
                                            "CODIGO_SUBFACTOR", inicial)),
                            codigoCriterio.length() + 2, "0");

            registro.getCampos().put("CODIGO_SUBFACTOR", cod);
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodigoEstudio, codigoEstudio);
        registro.getCampos().put(cCodigoModalidad, codigoModalidad);
        registro.getCampos().put(cCodigoCriterio, codigoCriterio);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the vobo
     */
    public boolean isVobo() {
        return vobo;
    }

    /**
     * @param vobo
     * the vobo to set
     */
    public void setVobo(boolean vobo) {
        this.vobo = vobo;
    }

    public int obtenerTotalPuntaje() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cCodigoEstudio, codigoEstudio);
        param.put(cCodigoModalidad, codigoModalidad);
        param.put(cCodigoCriterio, codigoCriterio);

        Registro rTotal = null;

        try {
            rTotal = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EsfactoresporestproysControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        int total = 0;

        if (rTotal != null) {
            total = Integer.parseInt(
                            rTotal.getCampos().get("TOTAL").toString());
        }

        return total;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        totalPuntaje = obtenerTotalPuntaje();

        return true;
        // </CODIGO_DESARROLLADO>
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
        totalPuntaje = obtenerTotalPuntaje();
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
        totalPuntaje = obtenerTotalPuntaje();
        // </CODIGO_DESARROLLADO>

        return true;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodigoEstudio);
        registro.getCampos().remove(cCodigoModalidad);
        registro.getCampos().remove(cCodigoCriterio);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario. Inicializa los eventos de precionar el boton
     * cerrar.
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("txtCodEstudio", codigoEstudio);
        parametros.put("tipoContratacion", codigoModalidad);
        parametros.put("visualizar", vobo);
        parametros.put("ridEstPrevios", ridEstPrevio);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ESCRITERIOS_FAC_PROY_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }
}
