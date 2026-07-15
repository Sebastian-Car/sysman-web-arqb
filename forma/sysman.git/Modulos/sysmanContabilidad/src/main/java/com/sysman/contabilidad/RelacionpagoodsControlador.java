package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionpagoodsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @author lcortes
 * @version 1, 26/04/2016
 * @modified spina 07/04/2017 - se refactoriza dss y depuracion sonar
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author eamaya
 * @version 4.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class RelacionpagoodsControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String cuenta;
    private String anio;
    private String nombreCuenta;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaCuenta;

    private Object nroDocumento;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of RelacionpagoodsControlador
     */
    public RelacionpagoodsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONPAGOODS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelacionpagoodsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        abrirFormulario();
        cargarListaCuenta();
    }

    @Override
    public void abrirFormulario() {
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacionpagoodsControladorUrlEnum.URL2813
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionpagoodsControladorUrlEnum.URL3172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        cargarListaCuenta();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuenta = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreCuenta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {

        try {
            if (validarCampos()) {
                return;
            }
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // Reemplazos consulta del reporte
            reemplazar.put("anio", anio);
            reemplazar.put("cuenta", cuenta);
            reemplazar.put("nroDocumento", nroDocumento);
            // Par�metros del reporte
            parametros.put("PR_CARGO_RELACION_PAGO_1",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO RELACION PAGO 1",
                                                            modulo,
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_CARGO_RELACION_PAGO_2",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "CARGO RELACION PAGO 2",
                                                            modulo,
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_NOMBRE_RELACION_PAGO_1",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE RELACION PAGO 1",
                                                            modulo,
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_NOMBRE_RELACION_PAGO_2",
                            SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "NOMBRE RELACION PAGO 2",
                                                            modulo,
                                                            new Date(), true),
                                            " "));
            parametros.put("PR_FORMS_RELACIONPAGOODS_NOCHEQUE", nroDocumento);

            Reporteador.resuelveConsulta("000671RelacionPagoODS",
                            Integer.parseInt(
                                            modulo),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000671RelacionPagoODS",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCampos() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(cuenta)
            || (nroDocumento == null) || "".equals(nroDocumento)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB714"));
            return true;
        }
        return false;
    }

    public Object getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(Object nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }
}
