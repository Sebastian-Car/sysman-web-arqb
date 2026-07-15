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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InformecompensacionfresumensControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 13/08/2015
 *
 *
 * --Modificado por lcortes 16/03/2017 16:40 --> Ajustes de buenas
 * practicas SonarLint. -- Modificado por lcortes 23/03/2017 12:00 -->
 * En le metodo getInforme se reemplaza la consulta "quemada" por un
 * reemplazar consulta para exportar la hoja datos.
 * 
 * 
 * @modified jguerrero
 * @version 3. 09/10/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 * 
 */
@ManagedBean
@ViewScoped

public class InformecompensacionfresumensControlador extends BeanBaseModal {

    private final String nombreCompania;

    private final String compania;

    private String total;
    private String ano1;
    private String mes1;
    private String periodo1;
    private String proceso;
    private String afiliacion;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaProceso;

    private final String moduloNomina;
    private String[] arrayNombresJasper;
    private Map<String, Object>[] arrayParametros;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * InformecompensacionfresumensControlador
     */
    @SuppressWarnings("unchecked")
    public InformecompensacionfresumensControlador() {
        super();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        numFormulario = GeneralCodigoFormaEnum.INFORMECOMPENSACIONFRESUMENS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        moduloNomina = SessionUtil.getModulo();
        ano1 = (String) SessionUtil.getSessionVar("anioNomina");
        mes1 = (String) SessionUtil.getSessionVar("mesNomina");
        periodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        total = "2";
        afiliacion = "63691";
        arrayNombresJasper = new String[2];
        arrayParametros = new HashMap[2];

        try {
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(InformecompensacionfresumensControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaProceso();
        abrirFormulario();
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformecompensacionfresumensControladorUrlEnum.URL4438
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PROCESO", proceso);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformecompensacionfresumensControladorUrlEnum.URL5196
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 7030 ANO PROCESO
    }

    public void cargarListaPeriodo1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        param.put("PROCESO", proceso);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformecompensacionfresumensControladorUrlEnum.URL5197
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 471029
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformecompensacionfresumensControladorUrlEnum.URL7704
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 537007
    }

    public void oprimirPreliminarBancos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        definirInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        definirInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void definirInforme(FORMATOS formato) {
        String stDocName;
        String valorParametro;

        try {
            if (("2").equals(total)) {

                valorParametro = ejbSysmanUtil.consultarParametro(compania,
                                "FORMATO RESUMEN DE CAJAS DE COMPENSACION",
                                SessionUtil.getModulo(),
                                new Date(), false);

                stDocName = SysmanFunciones.validarVariableVacio(valorParametro)
                    ? "000167PagoCajasCompensacionmensual"
                    : valorParametro;

            }
            else {
                valorParametro = ejbSysmanUtil.consultarParametro(compania,
                                "FORMATO RESUMEN DE CAJAS DE COMPENSACION MENSUAL",
                                SessionUtil.getModulo(),
                                new Date(), false);

                stDocName = SysmanFunciones.validarVariableVacio(valorParametro)
                    ? "000167PagoCajasCompensacionmensual"
                    : valorParametro;

                getInformeESAP();
            }
            getInforme(formato, stDocName);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void getInforme(FORMATOS formato, String stDocName) {
        try {

            String cond;
            String campoParafiscal2;
            String campoTransporte;
            String condPeriodo;

            if (("2").equals(total)) {
                cond = idioma.getString("TB_TB3701");
                campoParafiscal2 = idioma.getString("TB_TB3702");
                campoTransporte = idioma.getString("TB_TB3703");
                condPeriodo = SysmanFunciones.concatenar(
                                "AND Historicos.Periodo=", periodo1, "  ");

            }
            else {
                cond = idioma.getString("TB_TB3704");
                campoParafiscal2 = idioma.getString("TB_TB3705");
                campoTransporte = idioma.getString("TB_TB3706");
                condPeriodo = "";
            }

            Map<String, Object> reemplazar = new TreeMap<>();

            reemplazar.put("proceso", proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put("periodo1", periodo1);
            reemplazar.put("COND", cond);
            reemplazar.put("CAMPOPARAFISCAL2", campoParafiscal2);
            reemplazar.put("CAMPOTRANSPORTE", campoTransporte);
            reemplazar.put("CONDPERIODO", condPeriodo);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String codigoPresupuestal = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CODIGO PRESUPUESTAL PARA INFORMES DE NOMINA",
                            SessionUtil.getModulo(),
                            new Date(), true);

            codigoPresupuestal = codigoPresupuestal == null ? ""
                : codigoPresupuestal;

            parametros.put("PR_CODIGO_PRESUPUESTAL", codigoPresupuestal);
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)]);
            parametros.put("PR_PERIODO1", periodo1);
            parametros.put("PR_MNOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)].toUpperCase());
            parametros.put("PR_ANO1", ano1);

            Reporteador.resuelveConsulta(stDocName,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            arrayParametros[1] = parametros;
            arrayNombresJasper[1] = stDocName;

            if (!("2").equals(total)) {

                archivoDescarga = JsfUtil
                                .exportarComprimidoReportesStreamed(
                                                arrayNombresJasper,
                                                arrayParametros,
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                formato);

            }
            else {

                archivoDescarga = JsfUtil.exportarStreamed(stDocName,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);

            }

        }
        catch (JRException | IOException | SysmanException | SystemException
                        | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void getInformeESAP() {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);

            Map<String, Object> parametros = new HashMap<>();
            String nombreJefeRh = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE JEFE RECURSOS HUMANOS",
                            SessionUtil.getModulo(),
                            new Date(), true);
            String cedulaRLegal = ejbSysmanUtil.consultarParametro(compania,
                            "CEDULA DEL REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(),
                            new Date(), true);
            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", SessionUtil.getModulo(),
                            new Date(), true);
            String cargoJefeRh = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS",
                            SessionUtil.getModulo(),
                            new Date(), true);

            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRh);
            parametros.put("PR_CEDULA_DEL_REPRESENTANTE_LEGAL", cedulaRLegal);
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
                            nombreJefeRh);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
            Reporteador.resuelveConsulta("000170PagocajasESAP",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            arrayParametros[0] = parametros;
            arrayNombresJasper[0] = "000170PagocajasESAP";
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimircomfaboy() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme();
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme() {
        try {

            HashMap<String, Object> reemplazos = new HashMap<>();

            reemplazos.put("afiliacion", afiliacion);
            reemplazos.put("idProceso", "1");
            reemplazos.put("anio", ano1);
            reemplazos.put("mes", mes1);
            reemplazos.put("periodo", periodo1);
            reemplazos.put("clase", "7");
            reemplazos.put("claseConc", "3");

            String sql = Reporteador.resuelveConsulta(
                            "900008ConsultaFactoresComfaboy",
                            Integer.valueOf(moduloNomina),
                            reemplazos);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo1() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(String afiliacion) {
        this.afiliacion = afiliacion;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public String[] getArrayNombresJasper() {
        return arrayNombresJasper;
    }

    public void setArrayNombresJasper(String[] arrayNombresJasper) {
        this.arrayNombresJasper = arrayNombresJasper;
    }

    public Map<String, Object>[] getArrayParametros() {
        return arrayParametros;
    }

    public void setArrayParametros(HashMap<String, Object>[] arrayParametros) {
        this.arrayParametros = arrayParametros;
    }

    public void cambiarProceso() {
        ano1 = mes1 = periodo1 = null;
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

}
