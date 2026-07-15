package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dmaldonado
 * @version 1, 29/04/2016
 * @modifier amonroy
 * @version 2, 7/04/2017 Revision de buenas practicas sugeridas por la
 * herramienta SonarLint y cambio en el llamado de la funcion
 * "FC_GENPACPPNALALGIRO_SINCOM" utilizando EJB
 */
@ManagedBean
@ViewScoped
public class PedirFechasPacPropControlador extends BeanBaseModal {
    private final String compania;

    private String nombreComprobante;
    private String numeroComprobante;
    private String titulo;
    private Date fechaInicial;
    private Date fechaFinal;
    private String ano;
    private Date fechaComprobante;
    private Object terceroComprobante;
    private Object sucursalComprobante;
    private Object tipoComprobante;
    private double totalGiro;

    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

    /**
     * Creates a new instance of PedirFechasPacPropControlador
     */
    public PedirFechasPacPropControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PEDIR_FECHAS_PAC_PROP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            numeroComprobante = parametrosEntrada.get("numeroComprobante")
                            .toString();
            nombreComprobante = parametrosEntrada.get("nombreComprobante")
                            .toString();
            ano = parametrosEntrada.get("ano").toString();
            tipoComprobante = parametrosEntrada.get("tipoComprobante");
            fechaComprobante = (Date) parametrosEntrada.get("fechaComprobante");
            totalGiro = (double) parametrosEntrada.get("totalGiro");
            fechaInicial = (Date) parametrosEntrada.get("fechaInicial");
            fechaFinal = (Date) parametrosEntrada.get("fechaFinal");
            terceroComprobante = parametrosEntrada.get("terceroComprobante");
            sucursalComprobante = parametrosEntrada.get("sucursalComprobante");
            titulo = SysmanFunciones.concatenar(nombreComprobante.toUpperCase(),
                            " - NO. - ", numeroComprobante);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            boolean respuesta = ejbContabilidadCuatro
                            .generarPacProporcionalAlGiroSinOrdenDePago(
                                            compania,
                                            Integer.parseInt(ano),
                                            tipoComprobante.toString(),
                                            new BigInteger(numeroComprobante),
                                            fechaComprobante,
                                            BigDecimal.valueOf(totalGiro),
                                            fechaInicial,
                                            fechaFinal,
                                            terceroComprobante.toString(),
                                            sucursalComprobante.toString());
            if (respuesta) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1059"));
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1457"));
            }
        }
        catch (NumberFormatException | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        // Hacer la navegabilidad de afectarRes
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public String getNombreComprobante() {
        return nombreComprobante;
    }

    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

}
