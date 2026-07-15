package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author lcortes
 * @version 1, 28/03/2016
 *
 * @author lcortes
 * @version 2, 01/09/2017. Se realiza refactorizacion de codigo para
 * usar dss, revision de observaciones de la herramienta SonarLint y
 * reemplazo del llamado a la clase Acciones por el ejb respectivo.
 */
@ManagedBean
@ViewScoped
public class ObservacionesControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private boolean varVolver;
    private String tituloEtapa;
    private String tituloProponente;
    private String tipoContrato;
    private String consecutivo;
    private String transaccion;
    private String proponente;
    private String nombreProponente;
    private String sucursal;
    private String idEtapa;
    private String nombreEtapa;
    private String estadoEtapa;
    private String estadoProponente;
    private boolean modificar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public ObservacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            tipoContrato = (String) parametrosEntrada.get("tipoContrato");
            transaccion = (String) parametrosEntrada
                            .get("consecutivoTransaccion");
            consecutivo = (String) parametrosEntrada.get("consecutivoDetalle");
            proponente = (String) parametrosEntrada.get("proponente");
            nombreProponente = (String) parametrosEntrada
                            .get("nombreProponente");
            sucursal = (String) parametrosEntrada.get("sucursal");
            idEtapa = (String) parametrosEntrada.get("idEtapa");
            nombreEtapa = (String) parametrosEntrada.get("nombreEtapa");
            modificar = Boolean.valueOf(
                            (String) parametrosEntrada.get("modificar"));
            tituloEtapa = "ETAPA " + idEtapa + "- " + nombreEtapa.toUpperCase();
            tituloProponente = "PROPONENTE: " + proponente + " - "
                + nombreProponente;
            estadoEtapa = (String) parametrosEntrada.get("estadoEtapa");
            estadoProponente = (String) parametrosEntrada
                            .get("estadoProponente");
            estadoEtapa = estadoEtapa == null ? "NEE" : estadoEtapa; // NEE
                                                                     // no
                                                                     // tiene
                                                                     // ninguna
                                                                     // equivalencia
                                                                     // para
                                                                     // el
                                                                     // estado
            estadoProponente = estadoProponente == null ? "NEP"
                : estadoProponente; // NEP no tiene ninguna
                                    // equivalencia para el estado
            if (modificar) {
                if (("A").equals(estadoEtapa)) {
                    modificar = true;
                }
                else {
                    modificar = false;
                }
                if (!("RE").equals(estadoProponente)) {
                    modificar = true;
                }
                else {
                    modificar = false;
                }
            }
            numFormulario = GeneralCodigoFormaEnum.OBSERVACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ObservacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.OBSERVACIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("PROPONENTE", proponente);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosListado.put("TRANSACCION", transaccion);
        parametrosListado.put("CONSECUTIVO", consecutivo);
    }

    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        try {
            // <CODIGO_DESARROLLADO>
            StringBuilder condicion = new StringBuilder();
            condicion.append(" COMPANIA = ''").append(compania)
                            .append("'' AND TIPOCONTRATO = ''")
                            .append(tipoContrato)
                            .append("''  AND TRANSACCION = ''")
                            .append(transaccion)
                            .append("'' AND CONSECUTIVODETALLE = ''")
                            .append(consecutivo).append("''");
            registro.getCampos().put("COMPANIA", compania);
            registro.getCampos().put("ID",
                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                            "OBSERVACIONES",
                                            condicion.toString(), "ID", "1"));
            registro.getCampos().put("TIPOCONTRATO", tipoContrato);
            registro.getCampos().put("TRANSACCION", transaccion);
            registro.getCampos().put("CONSECUTIVODETALLE", consecutivo);
            registro.getCampos().put("PROPONENTE", proponente);
            registro.getCampos().put("SUCURSAL", sucursal);
            registro.getCampos().remove("NIT");
            registro.getCampos().remove("NOMBRE");
            registro.getCampos().remove("SUCURSAL_T");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
        // </CODIGO_DESARROLLADO>
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
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public boolean isVarVolver() {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    public String getTituloEtapa() {
        return tituloEtapa;
    }

    public void setTituloEtapa(String tituloEtapa) {
        this.tituloEtapa = tituloEtapa;
    }

    public String getTituloProponente() {
        return tituloProponente;
    }

    public void setTituloProponente(String tituloProponente) {
        this.tituloProponente = tituloProponente;
    }

    public boolean isModificar() {
        return modificar;
    }

    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

}
