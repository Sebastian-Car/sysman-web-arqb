package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.VerificarafectacionControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 13/01/2016
 * 
 * @version 2.0, 10/05/2017, pespitia: <br>
 * Refactoring.<br>
 * Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class VerificarafectacionControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.NUMERO</code>
     */
    private final String strNumero;

    private String tipoDeMovimiento;
    private String numero;
    private String nombreMovimiento;
    private String numeroDescripcion;
    private RegistroDataModelImpl listaTipoDeMovimiento;
    private RegistroDataModelImpl listaNumero;

    private StreamedContent archivoDescarga;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCero;

    /**
     * Creates a new instance of VerificarafectacionControlador
     */
    public VerificarafectacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strNumero = GeneralParameterEnum.NUMERO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.VERIFICARAFECTACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoDeMovimiento();
        cargarListaNumero();
        abrirFormulario();
    }

    public void cargarListaTipoDeMovimiento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VerificarafectacionControladorUrlEnum.URL2828
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "E");

        listaTipoDeMovimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VerificarafectacionControladorUrlEnum.URL3763
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                        tipoDeMovimiento);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strNumero);
    }

    public void oprimirComando13() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(tipoDeMovimiento)
            || SysmanFunciones.validarVariableVacio(numero)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1937"));
            return;
        }
        archivoDescarga = null;
        try {
            ejbAlmacenCero.revisarAfectacionMovimiento(compania,
                            tipoDeMovimiento, Long.parseLong(numero));
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(VerificarafectacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // <REEMPLAZAR VARIABLES EN CONSULTA>
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("tipoMovimiento", "'" + tipoDeMovimiento + "'");
        reemplazar.put("movimiento", numero);
        // <REEMPLAZAR VARIABLES EN CONSULTA>

        String consulta1 = Reporteador.resuelveConsulta(
                        "800112DetalleAfectacionMov", Integer.parseInt(modulo),
                        reemplazar);

        String consulta2 = Reporteador.resuelveConsulta(
                        "800113SaldoAfectacionMov", Integer.parseInt(modulo),
                        reemplazar);

        String[] consultas = { consulta1, consulta2 };
        String[] nombres = { "Detalle_Afectacion_movimiento",
                             "Saldo_Afectacion_movimiento" };

        try {
            archivoDescarga = JsfUtil.exportarComprimidoHojaDatosStreamed(
                            nombres, consultas, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CODIGO_DESARROLLADO>
    }

    public void oprimirComando14() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTipoDeMovimiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoDeMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreMovimiento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        numero = numeroDescripcion = null;
        cargarListaNumero();
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        numero = SysmanFunciones.nvl(registroAux.getCampos().get(strNumero), "")
                        .toString();

        numeroDescripcion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESCRIPCION"), "")
                        .toString();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTipoDeMovimiento() {
        return tipoDeMovimiento;
    }

    public void setTipoDeMovimiento(String tipoDeMovimiento) {
        this.tipoDeMovimiento = tipoDeMovimiento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreMovimiento() {
        return nombreMovimiento;
    }

    public void setNombreMovimiento(String nombreMovimiento) {
        this.nombreMovimiento = nombreMovimiento;
    }

    public String getNumeroDescripcion() {
        return numeroDescripcion;
    }

    public void setNumeroDescripcion(String numeroDescripcion) {
        this.numeroDescripcion = numeroDescripcion;
    }

    public RegistroDataModelImpl getListaTipoDeMovimiento() {
        return listaTipoDeMovimiento;
    }

    public void setListaTipoDeMovimiento(
        RegistroDataModelImpl listaTipoDeMovimiento) {
        this.listaTipoDeMovimiento = listaTipoDeMovimiento;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public String getModulo() {
        return modulo;
    }

}
