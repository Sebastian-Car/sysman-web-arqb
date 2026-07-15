package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.BoletinDiarioCajaControladorEnum;
import com.sysman.contabilidad.enums.BoletinDiarioCajaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 *
 * @author esarmiento
 * @version 1, 15/04/2016
 * @modified spina 06/04/2017 - se refactorizo para DSS y depuracion
 * sonar, se modifico la consulta donde se filtra por el to_char de la
 * fecha por un trunc para obtener los registros
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class BoletinDiarioCajaControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private boolean tercero;
    private boolean mensual;
    private String cuentaCaja;
    private Date fecha;
    private boolean visibleMensual;
    private String boletinCajaMensual;
    private String naturalezaCuenta;
    private String nombreCuenta;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCuentaCaja;

    @EJB
    private EjbContabilidadCeroRemote ejbContabilidadCero;

    @EJB
    private EjbContabilidadUnoRemote ejbContabilidadUno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BoletinDiarioCajaControlador
     */
    public BoletinDiarioCajaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.BOLETIN_DIARIO_CAJA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BoletinDiarioCajaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        fecha = new Date();
        visibleMensual = false;
        try {
            boletinCajaMensual = ejbSysmanUtil.consultarParametro(compania,
                            "BOLETIN DE CAJA MENSUAL, CAJA Y BANCOS",
                            modulo, new Date(), true);
            if ("SI".equals(boletinCajaMensual)) {
                visibleMensual = true;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(BoletinDiarioCajaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaCuentaCaja();
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    public void cargarListaCuentaCaja() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BoletinDiarioCajaControladorUrlEnum.URL4067
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            param.put(BoletinDiarioCajaControladorEnum.FECHA.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fecha));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        param.put(BoletinDiarioCajaControladorEnum.CLASECUENTA.getValue(),
                        "SI".equals(boletinCajaMensual) ? "J,B" : "J");

        listaCuentaCaja = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            String nombreReporte;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fecha", "'"
                + SysmanFunciones.convertirAFechaCadena(fecha) + "'");
            String fechaEncabezado = mensual
                ? "MES DE: "
                    + SysmanFunciones.convertirAFechaCadena(fecha, "MMMM")
                                    .toUpperCase()
                : SysmanFunciones
                                .convertirAFechaCadena(fecha,
                                                "EEEEE, dd MMMMM yyyy")
                                .toUpperCase();
            if (tercero) {
                parametros.put("PR_FECHALARGA", fechaEncabezado);
                parametros.put("PR_FECHA_ORIGEN",
                                SysmanFunciones.convertirAFechaCadena(fecha));
                reemplazar.put("portercero", -1);
                nombreReporte = "000630LisBoletinDiarioCajacontercero";
            }
            else {
                parametros.put("PR_ENCABEZADO", fechaEncabezado);
                reemplazar.put("portercero", 0);
                nombreReporte = "000635LisBoletinDiarioCaja";
            }

            parametros.put("PR_CUENTA_CAJA", cuentaCaja + " " + nombreCuenta);
            parametros.put("PR_CODIGO_FORMATO", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO FORMATO BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_FIRMA1", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA 1 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_FIRMA2", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA 2 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_FIRMA3", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA 3 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_CARGO1", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO 1 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_CARGO2", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO 2 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));
            parametros.put("PR_CARGO3", SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO 3 EN BOLETIN DIARIO DE CAJA",
                                            modulo, new Date(), true), ""));

            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("mensual", mensual ? -1 : 0);
            reemplazar.put("cuenta", "'" + cuentaCaja + "'");

            Reporteador.resuelveConsulta("000635LisBoletinDiarioCaja",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFecha() {
        // <CODIGO_DESARROLLADO>
        cuentaCaja = null;
        cargarListaCuentaCaja();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaCaja(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaCaja = registroAux.getCampos().get("CODIGO") == null ? ""
            : registroAux.getCampos().get("CODIGO").toString();
        setNaturalezaCuenta(
                        registroAux.getCampos().get("NATURALEZA") == null ? ""
                            : registroAux.getCampos().get("NATURALEZA")
                                            .toString());
        nombreCuenta = registroAux.getCampos().get("NOMBRE") == null ? ""
            : registroAux.getCampos().get("NOMBRE").toString();
    }

    public boolean isTercero() {
        return tercero;
    }

    public void setTercero(boolean tercero) {
        this.tercero = tercero;
    }

    public String getCuentaCaja() {
        return cuentaCaja;
    }

    public void setCuentaCaja(String cuentaCaja) {
        this.cuentaCaja = cuentaCaja;
    }

    public Date getFecha() {
        return fecha;
    }

    public boolean isMensual() {
        return mensual;
    }

    public void setMensual(boolean mensual) {
        this.mensual = mensual;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCuentaCaja() {
        return listaCuentaCaja;
    }

    public void setListaCuentaCaja(RegistroDataModelImpl listaCuentaCaja) {
        this.listaCuentaCaja = listaCuentaCaja;
    }

    public boolean isVisibleMensual() {
        return visibleMensual;
    }

    public void setVisibleMensual(boolean visibleMensual) {
        this.visibleMensual = visibleMensual;
    }

    public String getNaturalezaCuenta() {
        return naturalezaCuenta;
    }

    public void setNaturalezaCuenta(String naturalezaCuenta) {
        this.naturalezaCuenta = naturalezaCuenta;
    }

}
