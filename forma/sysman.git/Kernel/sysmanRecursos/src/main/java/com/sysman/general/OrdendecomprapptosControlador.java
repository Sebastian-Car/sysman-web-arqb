package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/11/2015
 *
 * -- Modificado por lcortes 05/04/2017 12:20. Ajustes refactorización
 * del código.
 */
@ManagedBean
@ViewScoped
public class OrdendecomprapptosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String numeroOrden;
    private String claseOrden;
    private String tipoPPTO;

    private static final String CTETIPOPPTO = "tipoPPTO";

    /**
     * Creates a new instance of OrdendecomprapptosControlador
     */
    public OrdendecomprapptosControlador() {
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.ORDENDECOMPRAPPTOS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                claseOrden = parametrosEntrada.get("claseF").toString();
                numeroOrden = parametrosEntrada.get("numeroOrden").toString();
                tipoPPTO = parametrosEntrada.get(CTETIPOPPTO).toString();
                parametrosEntrada.remove(CTETIPOPPTO);
            }
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(OrdendecomprapptosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ORDENDECOMPRAPPTO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);
        parametrosListado.put("TIPOPPTO", tipoPPTO);
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getTipoPPTO() {
        return tipoPPTO;
    }

    public void setTipoPPTO(String tipoPPTO) {
        this.tipoPPTO = tipoPPTO;
    }

    public void oprimirBTDetalle(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String[] campos = { CTETIPOPPTO, "numeroPPTO", "numeroOrden",
                            "claseOrden" };
        String[] valores = { String.valueOf(reg.getCampos().get("TIPOPPTO")),
                             String.valueOf(reg.getCampos().get("NUMEROPPTO")),
                             numeroOrden, claseOrden };
        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRDETALLECOMPROBANTESPRESUPUESTALES_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    public void cerrarFormulario() {

        int formularioRedirecciona = 0;
        if ("9020312".equals(SessionUtil.getMenuActual())) {
            formularioRedirecciona = GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                            .getCodigo();
        }
        else if ("90202".equals(SessionUtil.getMenuActual())
            || "10020201".equals(SessionUtil.getMenuActual())) {
            formularioRedirecciona = GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR
                            .getCodigo();
        }

        Map<String, Object> parametros = SessionUtil.getFlash();
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(formularioRedirecciona));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    public void ejecutarrcCerrar() {
        cerrarFormulario();
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    @Override
    public void removerCombos() {
        // Metodo heredado
    }

}
