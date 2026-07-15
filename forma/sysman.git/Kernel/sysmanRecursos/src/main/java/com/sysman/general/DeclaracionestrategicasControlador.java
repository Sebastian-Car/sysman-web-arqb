package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DeclaracionestrategicasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 12/02/2016
 *
 * @author jlramirez
 * @version 2, 03/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 *
 * -- Modificado por lcortes 11,12/05/2017. Se agrega validacion de la
 * fecha para que corresponda al anio seleccionado y que tenga formato
 * valido.
 */
@ManagedBean
@ViewScoped
public class DeclaracionestrategicasControlador
                extends BeanBaseContinuoAcmeImpl {

    private final String cFechaActualizacion;
    private String compania;
    private List<Registro> listaAnio;
    int anio;

    /**
     * Creates a new instance of DeclaracionestrategicasControlador
     */
    public DeclaracionestrategicasControlador() {
        super();
        cFechaActualizacion = "FECHA_ACTUALIZACION";
        try {
            numFormulario = GeneralCodigoFormaEnum.DECLARACIONESTRATEGICAS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                compania = (String) parametrosEntrada.get("companiap");
            }
        }
        catch (SysmanException ex) {
            Logger.getLogger(DeclaracionestrategicasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.DECLARACIONESESTRATEGICAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        asignarValoresRegistro();
        cargarListaAnio();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    public String getCompaniap() {
        return compania;
    }

    public void setCompaniap(String companiap) {
        this.compania = companiap;
    }

    public void cargarListaAnio() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DeclaracionestrategicasControladorUrlEnum.URL3006
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(DeclaracionestrategicasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control Anio
     *
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        anio = Integer.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cFechaActualizacion)) {
            validarAniosFechas();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control fecha
     *
     */
    public void cambiarfecha() {
        // <CODIGO_DESARROLLADO>
        validarAniosFechas();

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarAniosFechas() {
        Date fecha = (Date) registro.getCampos()
                        .get(cFechaActualizacion);
        if (SysmanFunciones.ano(fecha) != anio) {
            registro.getCampos().put(cFechaActualizacion, null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3154"));
            return false;
        }
        else if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cFechaActualizacion)) {
            try {
                if (!SysmanFunciones.validarFecha(
                                SysmanFunciones.convertirAFechaCadena(fecha))) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3155"));
                    return false;
                }
            }
            catch (ParseException e) {
                Logger.getLogger(DeclaracionestrategicasControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return true;
    }

    @Override
    public void abrirFormulario() {
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
        registro.getCampos().put("COMPANIA", compania);
        validarAniosFechas();
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
        validarAniosFechas();
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

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    @Override
    public void asignarValoresRegistro() {
        // NO SE IMPLEMENTA
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }
}
