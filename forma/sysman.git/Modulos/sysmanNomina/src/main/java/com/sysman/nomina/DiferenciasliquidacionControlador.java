package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.DiferenciasliquidacionControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 30/11/2015
 * 
 * @version 2, 29/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class DiferenciasliquidacionControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String proceso;
    private String ano2;
    private String mes2;
    private String periodo2;
    private String opcion;
    private List<Registro> listaProceso;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    private StreamedContent archivoDescarga;
    private static final String CTECOMPANIA = "compania";
    private static final String CTEPERIODO2 = "periodo2";

    /**
     * Creates a new instance of DiferenciasliquidacionControlador
     */
    public DiferenciasliquidacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.DIFERENCIASLIQUIDACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DiferenciasliquidacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        ano2 = (String) SessionUtil.getSessionVar("anioNomina");
        mes2 = (String) SessionUtil.getSessionVar("mesNomina");
        periodo2 = (String) SessionUtil.getSessionVar("periodoNomina");

        cargarListaProceso();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();
    }

    public void cargarListaProceso() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiferenciasliquidacionControladorUrlEnum.URL3294
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiferenciasliquidacionControladorUrlEnum.URL4074
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano2);

            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiferenciasliquidacionControladorUrlEnum.URL4572
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano2);
            param.put(GeneralParameterEnum.MES.getName(), mes2);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DiferenciasliquidacionControladorUrlEnum.URL5412
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga=null;
        if (SysmanFunciones.validarVariableVacio(opcion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));
            return;
        }
        switch (opcion) {
        case "1":
            getConceptosNegativosMes();
            break;
        case "2":
            getDiferenciasNetoPeriodo();
            break;
        case "3":
            getDiferenciasParafiscalesCV();
            break;
        case "4":
            getDiferenciaAporteDescuentosSalud();
            break;
        case "5":
            getDiferenciaDescuentosSaludPension();
            break;
        default:
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano2 = null;
        mes2 = null;
        periodo2 = null;
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    private void validarCamposAnioMes() {
        if (SysmanFunciones.validarVariableVacio(ano2)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2319"));
        }
        if (SysmanFunciones.validarVariableVacio(mes2)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_DEBE_MES_FIN"));
        }
    }

    public void getConceptosNegativosMes() {
        validarCamposAnioMes();
        if (SysmanFunciones.validarVariableVacio(periodo2)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1736"));
        }

        // --Revision conceptos negativos mes
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(CTECOMPANIA, compania);
        reemplazar.put("ano2", ano2);
        reemplazar.put("mes2", mes2);
        reemplazar.put(CTEPERIODO2, periodo2);
        String strSql = Reporteador.resuelveConsulta(
                        "800030REVISION_CONCEPTOS_NEGATIVOS_MES",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        generarReporte(strSql);
    }

    public void getDiferenciasNetoPeriodo() {
        validarCamposAnioMes();
        if (SysmanFunciones.validarVariableVacio(proceso)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2548"));
        }

        // --Revision conceptos negativos mes
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(CTECOMPANIA, compania);
        reemplazar.put("ano2", ano2);
        reemplazar.put("mes2", mes2);
        reemplazar.put("proceso", proceso);
        String strSql = Reporteador
                        .resuelveConsulta("800031DiferenciasNetoPeriodo",
                                        Integer.parseInt(
                                                        SessionUtil.getModulo()),
                                        reemplazar);
        generarReporte(strSql);
    }

    public void getDiferenciasParafiscalesCV() {
        validarCamposAnioMes();
        // --Revision conceptos negativos mes
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(CTECOMPANIA, compania);
        reemplazar.put("ano2", ano2);
        reemplazar.put("mes2", mes2);
        String strSql = Reporteador.resuelveConsulta(
                        "800032Diferencias_parafiscales_CV",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        generarReporte(strSql);

    }

    public void getDiferenciaAporteDescuentosSalud() {
        validarCamposAnioMes();

        // --Revision conceptos negativos mes
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(CTECOMPANIA, compania);
        reemplazar.put("ano2", ano2);
        reemplazar.put("mes2", mes2);
        reemplazar.put(CTEPERIODO2, periodo2);
        String strSql = Reporteador.resuelveConsulta(
                        "800034DiferenciaAporteDescuentoSalud",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        generarReporte(strSql);
    }

    public void getDiferenciaDescuentosSaludPension() {
        validarCamposAnioMes();

        // --Revision conceptos negativos mes
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(CTECOMPANIA, compania);
        reemplazar.put("ano2", ano2);
        reemplazar.put("mes2", mes2);
        reemplazar.put(CTEPERIODO2, periodo2);
        String strSql = Reporteador.resuelveConsulta(
                        "800035DiferenciasDescuentoSaludPension",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        generarReporte(strSql);
    }

    public boolean comprobarContenido(String sql) throws SystemException {
        return service.getConteoConsulta(sql) > 0 ? true : false;
    }

    public void generarReporte(String strSql) {
        try {
            if (comprobarContenido(strSql)) {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL97);
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                                            e.getMessage()));
        }
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getAno2() {
        return ano2;
    }

    public void setAno2(String ano2) {
        this.ano2 = ano2;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getModulo() {
        return modulo;
    }

}
