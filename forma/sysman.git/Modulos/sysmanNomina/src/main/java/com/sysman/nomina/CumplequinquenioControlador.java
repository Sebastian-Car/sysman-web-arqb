package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.CumplequinquenioControladorEnum;
import com.sysman.nomina.enums.CumplequinquenioControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 16/12/2015
 * @modified jguerrero
 * @version 2. 05/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class CumplequinquenioControlador extends BeanBaseModal {

    private final String modulo;
    private String mes;
    private StreamedContent archivoDescarga;
    private List<Registro> listaMes1;

    /**
     * Creates a new instance of CumplequinquenioControlador
     */
    public CumplequinquenioControlador() {
        super();
        modulo = SessionUtil.getModulo();
        numFormulario = GeneralCodigoFormaEnum.CUMPLEQUINQUENIO_CONTROLADOR
                        .getCodigo();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CumplequinquenioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        mes = (String) SessionUtil
                        .getSessionVar(CumplequinquenioControladorEnum.MES_NOMINA_LOWER
                                        .getValue());
        cargarListaMes1();
        abrirFormulario();
    }

    public void cargarListaMes1() {

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CumplequinquenioControladorUrlEnum.URL2163
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporte(FORMATOS formatos) {
        try {
            String mes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mes)];

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(CumplequinquenioControladorEnum.MES_LOWER.getValue(),
                            mes);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = CumplequinquenioControladorEnum.REPORTE438
                            .getValue();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put(CumplequinquenioControladorEnum.PR_STRSQL.getValue(),
                            strSql);
            parametros.put(CumplequinquenioControladorEnum.PR_MES1.getValue(),
                            mes1);
            parametros.put(CumplequinquenioControladorEnum.PR_NOMBREEMPRESA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}
