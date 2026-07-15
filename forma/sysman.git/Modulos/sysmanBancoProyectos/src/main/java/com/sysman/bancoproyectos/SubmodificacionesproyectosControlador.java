
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.SubmodificacionesproyectosControladorEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 17/09/2015
 * 
 * @version 2, 28/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Manejo de EJBs.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class SubmodificacionesproyectosControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CONSECUTIVO</code>
     */
    private final String cConsecutivo = GeneralParameterEnum.CONSECUTIVO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>NOMBRETIPO</code>
     */
    private final String cNombreTipo = SubmodificacionesproyectosControladorEnum.NOMBRETIPO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROYECTO</code>
     */
    private final String cProyecto = GeneralParameterEnum.PROYECTO
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPO</code>
     */
    private final String cTipo = SubmodificacionesproyectosControladorEnum.TIPO
                    .getValue();

    private String codigoProyecto;
    private String accion;
    private boolean muestraRegistro;
    private String menuActual;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of SubmodificacionesproyectosControlador
     */
    public SubmodificacionesproyectosControlador() {
        super();

        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();

        try {
            // 211
            numFormulario = GeneralCodigoFormaEnum.SUBMODIFICACIONESPROYECTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            menuActual = menuActual == null ? "NULL" : menuActual;

            switch (menuActual) {
            case "52020102":
            case "52020402":
                muestraRegistro = false;
                break;
            case "52020101":
                muestraRegistro = true;
                break;
            default:
                SessionUtil.redireccionarMenu();
                break;
            }

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                codigoProyecto = parametrosEntrada.get("codigoProyecto")
                                .toString();

                accion = parametrosEntrada.get("accion").toString();
            }
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();

            Logger.getLogger(SubmodificacionesproyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_PROYECTOS_MODIFICACIONES;
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        abrirFormulario();

        if (!cargado && ("v").equals(accion)) {
            muestraRegistro = false;
        }
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cProyecto, codigoProyecto);
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public boolean isMuestraRegistro() {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro) {
        this.muestraRegistro = muestraRegistro;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void cambiarTIPO() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), cTipo)) {
            registro.getCampos().remove(cConsecutivo);
        }
        else {
            generarConsecutivo();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTIPOC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put(cProyecto, codigoProyecto);

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(cNombreTipo);
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cConsecutivo);
        registro.getCampos().remove(cProyecto);
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Asigna el consecutivo del mayor valor del atributo
     * <code>CONSECUTIVO</code> de la tabla
     * <code>BP_PROYECTOS_MODIFICACIONES</code>
     */
    private void generarConsecutivo() {
        String condicion = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND PROYECTO = ''", codigoProyecto,
                        "'' AND TIPO = ''",
                        registro.getCampos().get(cTipo).toString(), "''");

        try {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            enumBase.getTable(), condicion,
                            cConsecutivo, "1");

            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException ex) {
            Logger.getLogger(SubmodificacionesproyectosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);

            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }
}
