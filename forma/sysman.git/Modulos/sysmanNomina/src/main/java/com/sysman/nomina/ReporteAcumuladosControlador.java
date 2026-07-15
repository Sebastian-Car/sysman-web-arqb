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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.ReporteAcumuladosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
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

import org.apache.xalan.xsltc.compiler.sym;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 15/07/2015
 * 
 * @version 2, 26/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @version 2.5,22/03/2018, Se adiciono el indicador Reporte
 * Diferencias para generar el reporte
 * 001697REPORTEDIFERENCIAS2PERIODOSXEMPLEADO y se modifico el metodo
 * abrirInforme()
 * @author eamaya
 * 
 */

@ManagedBean
@ViewScoped
public class ReporteAcumuladosControlador extends BeanBaseModal {

    private String compania;
    private String nombreCompania;
    private String modulo;
    private String procesoSesion;
    private String anioSesion;
    private String mesSesion;
    private String periodoSesion;
    private String extras;
    private String opcion;
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String empleado;
    private String cedula;
    private String idEmpleado;
    private String condEmleado;
    private String condClase;
    private boolean acumuladoMes;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaEmpleado;
    private StreamedContent archivoDescarga;

    private final String nombreInformeCons;
    private final String prStrSqlCons;
    private final String prNombreEmpresaCons;
    private final String prEntreCons;

    private final String desdeCons;
    private final String hastaCons;
    private final String condEmpleadoCons;
    private final String condClaseCons;
    private final String periodoCons;
    private final String msjErrorCons;
    private String desde;
    private String hasta;
    private String prmEntre;
    private String observaciones;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    /**
     * Atributo que almacena el valor del indicador de Reporte
     * Diferencias
     */
    private boolean diferencias;
    private boolean conDiferencias;

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ReporteAcumuladosControlador
     */
    public ReporteAcumuladosControlador() {
        super();
        nombreInformeCons = "000049Acumuladostodoslosprocesos";
        prNombreEmpresaCons = "PR_NOMBREEMPRESA";
        prEntreCons = "PR_ENTRE";
        desdeCons = "DESDE";
        hastaCons = "HASTA";
        condEmpleadoCons = "CONDEMPLEADO";
        condClaseCons = "CONDCLASE";
        prStrSqlCons = "PR_STRSQL";
        periodoCons = " Periodo ";
        msjErrorCons = "MSM_TRANS_INTERRUMPIDA";
        try {
            compania = SessionUtil.getCompania();
            nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
            procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
            anioSesion = (String) SessionUtil.getSessionVar("anioNomina");
            mesSesion = (String) SessionUtil.getSessionVar("mesNomina");
            periodoSesion = (String) SessionUtil.getSessionVar("periodoNomina");
            modulo = SessionUtil.getModulo();
            numFormulario = GeneralCodigoFormaEnum.REPORTE_ACUMULADOS_CONTROLADOR
                            .getCodigo();
            ano1 = ano2 = anioSesion;
            mes1 = mes2 = mesSesion;
            periodo1 = periodo2 = periodoSesion;
            proceso = procesoSesion;
            opcion = "1";
            extras = "false";
            diferencias = false;
            conDiferencias = false;

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ReporteAcumuladosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaEmpleado();
        abrirFormulario();
    }

    public void cargarListaAno1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL5200
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
            param.put(GeneralParameterEnum.ANO.getName(), ano1);

            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL5624
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(), ano1);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL6048
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
                                                            ReporteAcumuladosControladorUrlEnum.URL6048
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano1);
            param.put(GeneralParameterEnum.MES.getName(), mes1);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL6761
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
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL6761
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteAcumuladosControladorUrlEnum.URL9007
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEmpleado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteAcumuladosControladorUrlEnum.URL9975
                                                        .getValue());
        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");

    }

    public void oprimirPresentar() {
        if (diferencias || conDiferencias) {
            if (validarRango() && validarFecha()) {
                abrirInforme(ReportesBean.FORMATOS.PDF);
            }
            else {
                ejecutarMensaje();

            }
        }
        else {
            if (validarRango()) {
                abrirInforme(ReportesBean.FORMATOS.PDF);
            }
        }

    }

    public void oprimirImprimir() {
        if (diferencias || conDiferencias) {
            if (validarRango() && validarFecha()) {
                abrirInforme(ReportesBean.FORMATOS.EXCEL);
            }
            else {
                ejecutarMensaje();

            }
        }
        else {
            if (validarRango()) {
                abrirInforme(ReportesBean.FORMATOS.EXCEL);
            }
        }
    }

    private String fecInicial() {
        String fecInicial;
        fecInicial = SysmanFunciones.concatenar(ano1,
                        mes1.length() == 1 ? "0" + mes1 : mes1,
                        periodo1.length() == 1 ? "0" + periodo1 : periodo1);
        return fecInicial;
    }

    private String fecFinal() {
        String fecFinal;
        fecFinal = SysmanFunciones.concatenar(ano2,
                        mes2.length() == 1 ? "0" + mes2 : mes2,
                        periodo2.length() == 1 ? "0" + periodo2 : periodo2);
        return fecFinal;
    }

    private boolean validarFecha() {
        if (fecInicial().equals(fecFinal())) {
            return false;
        }
        return true;
    }

    public void abrirInforme(FORMATOS formato) {
        boolean extrasInforme = "true".equals(extras);
        String strReporte = nombreInformeCons;
        String psql = getSql(false);
        archivoDescarga = null;
        try {
            Map<String, Object> parametros = new HashMap<>();

            asignarRangoTitulo();

            parametros.put(prStrSqlCons, psql);
            parametros.put(prNombreEmpresaCons, nombreCompania);
            parametros.put(prEntreCons, prmEntre);

            if (diferencias) {

                Map<String, Object> parametrosDiferencias = new TreeMap<>();
                Map<String, Object> reemplazosDiferencias = new TreeMap<>();

                parametrosDiferencias.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso().getNombre());

                parametrosDiferencias.put("PR_USUARIO",
                                SessionUtil.getUser().getCodigo());
                parametrosDiferencias.put("PR_MES1", ejbSysmanUtil
                                .mostrarNombreDeMes(Integer.parseInt(mes1))
                                .toUpperCase());
                parametrosDiferencias.put("PR_ANO1", ano1);
                parametrosDiferencias.put("PR_PERIODO1", periodo1);
                parametrosDiferencias.put("PR_MES2", ejbSysmanUtil
                                .mostrarNombreDeMes(Integer.parseInt(mes2))
                                .toUpperCase());
                parametrosDiferencias.put("PR_ANO2", ano2);
                parametrosDiferencias.put("PR_PERIODO2", periodo2);

                parametrosDiferencias.put("PR_NOMBRE_DEL_GERENTE",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DEL GERENTE", modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DEL_GERENTE",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DEL GERENTE", modulo,
                                                new Date(), false));

                parametrosDiferencias.put(
                                "PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DEL CARGO TESORERO PAGADOR",
                                                modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DEL TESORERO PAGADOR",
                                                modulo,
                                                new Date(), false));

                parametrosDiferencias.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DE QUIEN AUTORIZA NOMINA",
                                                modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DE QUIEN AUTORIZA NOMINA",
                                                modulo,
                                                new Date(), false));
                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                parametrosDiferencias.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                // FIN IMPLEMENTACION MARCA_BLANCA
                reemplazosDiferencias.put("mes", mes1);
                reemplazosDiferencias.put("ano", ano1);
                reemplazosDiferencias.put("periodo", periodo2);
                reemplazosDiferencias.put("mesDos", mes2);
                reemplazosDiferencias.put("anoDos", ano2);
                reemplazosDiferencias.put("periodoDos", periodo2);
                reemplazosDiferencias.put("varConDiferncia", 0);

                reemplazosDiferencias.put(condEmpleadoCons,
                                condicionEmpleado());

                String[] informe = new String[2];
                informe[0] = "001697REPORTEDIFERENCIAS2PERIODOSXEMPLEADO";
                informe[1] = "001759ReporteDiferenciasSinJustificacion";
                Reporteador.resuelveConsulta(informe[0],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosDiferencias, parametrosDiferencias);

                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                salidas[0] = JsfUtil.serializarReporte(informe[0],
                                parametrosDiferencias,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

                Reporteador.resuelveConsulta(informe[0],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosDiferencias, parametrosDiferencias);

                salidas[1] = JsfUtil.serializarReporte(informe[1],
                                parametrosDiferencias,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                String[] nombresArchivos = new String[2];

                if (FORMATOS.PDF.equals(formato)) {
                    nombresArchivos[0] = "001697ReporteDiferencias.pdf";
                    nombresArchivos[1] = "001759ReporteDiferenciasSinJustificacion.pdf";
                }
                else {
                    nombresArchivos[0] = "001697ReporteDiferencias.xlsx";
                    nombresArchivos[1] = "001759ReporteDiferenciasSinJustificacion.xlsx";
                }

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);

            }
            else

            if (extrasInforme) {
                String exSql = getSql(true);
                Map<String, Object> parametrosExtra = new HashMap<>();
                parametrosExtra.put(prNombreEmpresaCons, nombreCompania);
                parametrosExtra.put(prEntreCons, prmEntre);
                parametrosExtra.put(prStrSqlCons, exSql);

                ByteArrayInputStream normal = JsfUtil.serializarReporte(
                                nombreInformeCons, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                ByteArrayInputStream extra = JsfUtil.serializarReporte(
                                nombreInformeCons,
                                parametrosExtra, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
                ByteArrayInputStream[] reportesSerializados = { normal, extra };

                String[] nombres = new String[2];
                if (formato == ReportesBean.FORMATOS.PDF) {
                    nombres[0] = "Acumulados.pdf";
                    nombres[1] = "AcumuladosExtra.pdf";

                }
                else {
                    nombres[0] = "Acumulados.xls";
                    nombres[1] = "AcumuladosExtra.xls";

                }

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                reportesSerializados, nombres);

            }
            else

            if (conDiferencias) {

                Map<String, Object> parametrosDiferencias = new TreeMap<>();
                Map<String, Object> reemplazosDiferencias = new TreeMap<>();

                parametrosDiferencias.put("PR_NOMBREEMPRESA",
                                SessionUtil.getCompaniaIngreso().getNombre());

                parametrosDiferencias.put("PR_USUARIO",
                                SessionUtil.getUser().getCodigo());
                parametrosDiferencias.put("PR_MES1", ejbSysmanUtil
                                .mostrarNombreDeMes(Integer.parseInt(mes1))
                                .toUpperCase());
                parametrosDiferencias.put("PR_ANO1", ano1);
                parametrosDiferencias.put("PR_PERIODO1", periodo1);
                parametrosDiferencias.put("PR_MES2", ejbSysmanUtil
                                .mostrarNombreDeMes(Integer.parseInt(mes2))
                                .toUpperCase());
                parametrosDiferencias.put("PR_ANO2", ano2);
                parametrosDiferencias.put("PR_PERIODO2", periodo2);

                parametrosDiferencias.put("PR_NOMBRE_DEL_GERENTE",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DEL GERENTE", modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DEL_GERENTE",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DEL GERENTE", modulo,
                                                new Date(), false));

                parametrosDiferencias.put(
                                "PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DEL CARGO TESORERO PAGADOR",
                                                modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DEL TESORERO PAGADOR",
                                                modulo,
                                                new Date(), false));

                parametrosDiferencias.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "NOMBRE DE QUIEN AUTORIZA NOMINA",
                                                modulo,
                                                new Date(), false));
                parametrosDiferencias.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                                ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO DE QUIEN AUTORIZA NOMINA",
                                                modulo,
                                                new Date(), false));
                
                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                parametrosDiferencias.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                // FIN IMPLEMENTACION MARCA_BLANCA

                reemplazosDiferencias.put("mes", mes1);
                reemplazosDiferencias.put("ano", ano1);
                reemplazosDiferencias.put("periodo", periodo2);
                reemplazosDiferencias.put("mesDos", mes2);
                reemplazosDiferencias.put("anoDos", ano2);
                reemplazosDiferencias.put("periodoDos", periodo2);

                reemplazosDiferencias.put("varConDiferncia", 1);

                reemplazosDiferencias.put(condEmpleadoCons,
                                condicionEmpleado());

                String[] informe = new String[2];
                informe[0] = "001697REPORTEDIFERENCIAS2PERIODOSXEMPLEADO";
                informe[1] = "001759ReporteDiferenciasSinJustificacion";
                Reporteador.resuelveConsulta(informe[0],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosDiferencias, parametrosDiferencias);

                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

                informe[0] = "001697ReporteConDiferencias";
                informe[1] = "001759ReporteConDiferenciasSinJustificacion";

                salidas[0] = JsfUtil.serializarReporte(informe[0],
                                parametrosDiferencias,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

                Reporteador.resuelveConsulta(informe[0],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazosDiferencias, parametrosDiferencias);

                salidas[1] = JsfUtil.serializarReporte(informe[1],
                                parametrosDiferencias,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                String[] nombresArchivos = new String[2];

                if (formato == ReportesBean.FORMATOS.PDF) {
                    nombresArchivos[0] = "001697ReporteConDiferencias.pdf";
                    nombresArchivos[1] = "001759ReporteConDiferenciasSinJustificacion.pdf";

                }
                else {
                    nombresArchivos[0] = "001697ReporteConDiferencias.xls";
                    nombresArchivos[1] = "001759ReporteConDiferenciasSinJustificacion.xls";
                }

                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);

            }
            else {

                archivoDescarga = JsfUtil.exportarStreamed(strReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (SQLException | JRException | IOException | DRException ex) {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(msjErrorCons),
                                            ex.getMessage()));
            logger.error(ex.getMessage(), ex);
        }
        catch (SysmanException | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private Object condicionEmpleado() {
        if ("3".equals(opcion)) {
            condEmleado = "AND HISTORICOS.ID_DE_EMPLEADO =" + idEmpleado + "";
        }
        else if ("1".equals(opcion)) {
            condEmleado = "";
        }
        return condEmleado;
    }

    public String getSql(boolean extrasInforme) {
        String strReporte = nombreInformeCons;
        asignarRango();

        if ("3".equals(opcion)) {
            condEmleado = "and V_ACUMULADOS.NUMERO_DCTO='" + cedula + "'";
        }
        else if ("1".equals(opcion)) {
            condEmleado = "";
        }

        if (!extrasInforme) {
            if ("0".equals(proceso)) {
                condClase = " AND V_ACUMULADOS.Clase in (3,5) ";
            }
            else {
                condClase = " AND V_ACUMULADOS.Clase in (3,5,8,99) ";
            }
        }
        else {
            condClase = SysmanFunciones.concatenar(" AND V_ACUMULADOS.CLASE=3 ",
                            "AND (V_ACUMULADOS.ID_DE_CONCEPTO Between '048' And '060' ",
                            "OR V_ACUMULADOS.ID_DE_CONCEPTO IN (507,508,511,518,519,520,521,522,523,528,546))");

        }
        Map<String, Object> reemplazar = new HashMap<>();
        // MANEJO DE PARAMETROS DE REEMPLAZO
        reemplazar.put(desdeCons, desde);
        reemplazar.put(hastaCons, hasta);
        reemplazar.put(condEmpleadoCons, condEmleado);
        reemplazar.put(condClaseCons, condClase);

        return Reporteador.resuelveConsulta(strReporte,
                        Integer.parseInt(modulo), reemplazar);
    }

    public void abrirInformeKardex(boolean detallado) {
        archivoDescarga = null;
        asignarRango();
        String condEmpleado = "";
        String joinResumido;
        String nombreConceptoAs;
        String nombreConcepto;
        String condNull;
        String condIdConcepto;
        String gCondConcepto;
        String strReporte = "000077KARDEXNOMINANUEVODETALLADO";

        if ("3".equals(opcion)) {
            condEmpleado = "and V_ACUMULADOS.ID_DE_EMPLEADO=" + idEmpleado
                + " ";
        }
        else if ("1".equals(opcion)) {
            condEmpleado = "";
        }

        if (!detallado) {
            joinResumido = "LEFT JOIN CONCEPTOS_AGR \n"
                + "ON V_ACUMULADOS.TIPOAGRCONCEPTO = CONCEPTOS_AGR.CODAGR ";
            nombreConceptoAs = " CONCEPTOS_AGR.NombreConceptoAGR AS NOMBRECONCEPTO, ";
            nombreConcepto = " CONCEPTOS_AGR.NombreConceptoAGR";
            condNull = " AND V_ACUMULADOS.TIPOAGRCONCEPTO IS NOT NULL";
            condIdConcepto = " V_ACUMULADOS.TIPOAGRCONCEPTO AS TIPOAGRCONCEPTO,";
            gCondConcepto = " V_ACUMULADOS.TIPOAGRCONCEPTO,";
        }
        else {

            joinResumido = "";
            nombreConceptoAs = " V_ACUMULADOS.Nombre_Concepto AS NOMBRECONCEPTO, ";
            nombreConcepto = " V_ACUMULADOS.Nombre_Concepto";
            condNull = " AND V_ACUMULADOS.ID_DE_CONCEPTO IS NOT NULL "
                + " AND V_ACUMULADOS.CLASE NOT IN (8) ";
            condIdConcepto = " V_ACUMULADOS.ID_de_Concepto AS TIPOAGRCONCEPTO,";
            gCondConcepto = " V_ACUMULADOS.ID_DE_CONCEPTO,";
        }
        try {

            Map<String, Object> parametros = new HashMap<>();
            asignarRangoTitulo();
            String titulo;
            if (detallado) {
                titulo = "TARJETA DE  KARDEX CONCEPTOS HISTORICOS EMPLEADO DETALLADO";
            }
            else {
                titulo = "TARJETA DE  KARDEX CONCEPTOS HISTORICOS EMPLEADO RESUMIDO";
            }

            Map<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put(desdeCons, desde);
            reemplazar.put(hastaCons, hasta);
            reemplazar.put(condEmpleadoCons, condEmpleado);
            reemplazar.put("JOINRESUMIDO", joinResumido);
            reemplazar.put("NOMBRECONCEPTOAS", nombreConceptoAs);
            reemplazar.put("NOMBRECONCEPTO", nombreConcepto);
            reemplazar.put("CONDNULL", condNull);
            reemplazar.put("CONDIDCONCEPTO", condIdConcepto);
            reemplazar.put("GCONDIDCONCEPTO", gCondConcepto);

            String sql = Reporteador.resuelveConsulta(strReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            parametros.put(prStrSqlCons, sql);
            parametros.put(prNombreEmpresaCons, nombreCompania);
            parametros.put(prEntreCons, prmEntre);
            parametros.put("PR_OBSERVACIONES",observaciones);
            parametros.put("PR_TITULO", titulo);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            archivoDescarga = JsfUtil.exportarStreamed(strReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(msjErrorCons),
                                            ex.getMessage()));
            logger.error(ex.getMessage(), ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirKardex() {
        archivoDescarga = null;
        asignarRango();
        try {
            if (validarRango()) {
                String encabezadoEmpleado = (empleado).replace(" ", "_");
                if (encabezadoEmpleado.length() > 30) {
                    encabezadoEmpleado = encabezadoEmpleado.substring(0, 30);
                }

                String condicionPivot = ejbNominaUno.getKardexAcumNomina(
                                compania,
                                SysmanFunciones.concatenar(ano1,
                                                String.format("%02d",
                                                                Integer.parseInt(
                                                                                mes1)),
                                                String.format("%02d",
                                                                Integer.parseInt(
                                                                                periodo1))),
                                SysmanFunciones.concatenar(ano2,
                                                String.format("%02d",
                                                                Integer.parseInt(
                                                                                mes2)),
                                                String.format("%02d",
                                                                Integer.parseInt(
                                                                                periodo2))),
                                Integer.parseInt(idEmpleado));

                if (condicionPivot != null) {
                    Map<String, Object> reemplazar = new HashMap<>();
                    reemplazar.put(desdeCons, desde);
                    reemplazar.put(hastaCons, hasta);
                    reemplazar.put("ENCABEZADOEMPLEADO",
                                    "\"" + encabezadoEmpleado + "\"");
                    reemplazar.put("CONDICIONPIVOT", condicionPivot);
                    reemplazar.put("IDEMPLEADO", idEmpleado);

                    String sql = Reporteador.resuelveConsulta(
                                    "800037KardexAcum",
                                    Integer.parseInt(modulo), reemplazar);

                    archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.EXCEL97);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(
                                    "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                }
            }

        }
        catch (JRException | IOException
                        | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton KardexDevengos en la vista
     *
     */
    public void oprimirKardexDevengos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> reemplazos = new HashMap<>();
        String sql;
        try {
            String condicionPivot = ejbNominaUno.getPivotKardexNomina(compania,
                            Integer.parseInt(ano1),
                            1, 9999,
                            Integer.parseInt(mes1), new Long("1"),
                            true);
            reemplazos.put("condicionPivot", condicionPivot);
            reemplazos.put("mesInicial", mes1);
            reemplazos.put("mesFinal", mes2);
            reemplazos.put("anioInicial", ano1);
            reemplazos.put("anioFinal", ano2);

            sql = Reporteador.resuelveConsulta("800331KARDEXDEDEVENGOS",
                            Integer.parseInt(modulo),
                            reemplazos);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97,
                            "KARDEX_DE_DEVENGOS");

        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton AcumuladosPeriodo en la
     * vista
     *
     *
     */
    public void oprimirAcumuladosPeriodo() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> reemplazos = new HashMap<>();
            String reporte;
            String nombreReporte;
            String condicionPivot = ejbNominaUno.getPrepararPivotDevengosAnio(
                            compania,
                            Integer.parseInt(ano1));

            reemplazos.put("condicionPivot", "'" + condicionPivot);
            reemplazos.put("limiteInicial" , ano1 + SysmanFunciones.padl(mes1, 2, "0") + SysmanFunciones.padl(periodo1, 2, "0"));
            reemplazos.put("limiteFinal"   , ano2 + SysmanFunciones.padl(mes2, 2, "0") + SysmanFunciones.padl(periodo2, 2, "0"));
            reemplazos.put(condEmpleadoCons, condicionEmpleado());

            reporte = acumuladoMes ? "800333ACUMENTREPERIODOSPORMESES"
                : "800332ACUMULADOSENTREPERIODOS";
            
            nombreReporte = acumuladoMes ? "ACUM_ENTRE_PERIODOS_POR_MESES"
                : "ACUMULADOS_ENTRE_PERIODOS";

            String sql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo),
                            reemplazos);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL,
                            nombreReporte);
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </CODIGO_DESARROLLADO>

    public void asignarRango() {
        desde = SysmanFunciones.concatenar(ano1,
                        SysmanFunciones.padl(mes1, 2, "0"),
                        SysmanFunciones.padl(periodo1, 2, "0"));
        hasta = SysmanFunciones.concatenar(ano2,
                        SysmanFunciones.padl(mes2, 2, "0"),
                        SysmanFunciones.padl(periodo2, 2, "0"));
    }

    public void asignarRangoTitulo() {
        prmEntre = SysmanFunciones.concatenar("Entre: ",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes1)],
                        " de ", ano1, periodoCons, periodo1, "  y  ",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes2)],
                        " de ", ano2, periodoCons, periodo2);
    }

    public boolean validarRango() {
        boolean estado = true;
        String perInicial = SysmanFunciones.concatenar(ano1,
                        SysmanFunciones.padl(mes1, 2, "0"),
                        SysmanFunciones.padl(periodo1, 2, "0"));
        String perFinal = SysmanFunciones.concatenar(ano2,
                        SysmanFunciones.padl(mes2, 2, "0"),
                        SysmanFunciones.padl(periodo2, 2, "0"));
        if (perInicial.compareTo(perFinal) > 0) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2509"));
            estado = false;
        }
        return estado;
    }

    public void oprimirinformeresumido() {
        if (validarRango()) {
            abrirInformeKardex(false);
        }
    }

    public void ejecutarMensaje() {

        JsfUtil.agregarMensajeError(idioma.getString("TB_TB4057"));

    }

    public void oprimirinformedetallado() {
        if (validarRango()) {
            abrirInformeKardex(true);
        }
    }

    public void cambiarAno1() {
        cargarListaPeriodo1();
        cargarListaMes1();
        cargarListaAno2();
        cargarListaPeriodo2();
        cargarListaMes2();
        mes1 = periodo1 = ano2 = mes2 = periodo2 = null;
    }

    public void cambiarAno2() {
        cargarListaPeriodo2();
        cargarListaMes2();
        periodo2 = mes2 = null;
    }

    public void cambiarMes1() {
        cargarListaPeriodo1();
        periodo1 = null;
    }

    public void cambiarMes2() {
        cargarListaPeriodo2();
        periodo2 = null;
    }

    public void cambiarOpcion() {
        cargarListaEmpleado();

    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos().get("NOMBRECOMPLETO").toString();
        cedula = registroAux.getCampos().get("NUMERO_DCTO").toString();
        idEmpleado = registroAux.getCampos().get("ID_DE_EMPLEADO").toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getAno2() {
        return ano2;
    }

    public void setAno2(String ano2) {
        this.ano2 = ano2;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public String getCompania() {
        return compania;
    }

    public String getProcesoSesion() {
        return procesoSesion;
    }

    public String getAnioSesion() {
        return anioSesion;
    }

    public String getMesSesion() {
        return mesSesion;
    }

    public String getPeriodoSesion() {
        return periodoSesion;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreCompania() {
        return nombreCompania;
    }

    public void cambiarextras() {
        if ("true".equals(extras)) {
            diferencias = false;
            conDiferencias = false;
        }
    }

    public void cambiarDiferencias() {
        if (diferencias) {
            extras = "false";
            conDiferencias = false;
        }
    }

    /**
     * Método ejecutado al cambiar el control conDiferencias.
     * 
     */

    public void cambiarconDiferencias() {
        // <CODIGO_DESARROLLADO>
        if (conDiferencias) {
            extras = "false";
            diferencias = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        ano1 = ano2 = mes1 = mes2 = periodo1 = periodo2 = null;
    }

    public void cambiarPeriodo2() {
        // Metodo ejecutado al cambiar el periodo1
    }

    public void cambiarPeriodo1() {
        // Metodo ejecutado al cambiar el periodo1
    }

    @Override
    public void abrirFormulario() {
        // Metodo ejecutado al cambiar el periodo1
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCONDEMPLEADO() {
        return condEmleado;
    }

    public String getCondEmleado() {
        return condEmleado;
    }

    public void setCondEmleado(String condEmleado) {
        this.condEmleado = condEmleado;
    }

    public String getCondClase() {
        return condClase;
    }

    public void setCondClase(String condClase) {
        this.condClase = condClase;
    }

    public boolean isDiferencias() {
        return diferencias;
    }

    public void setDiferencias(boolean diferencias) {
        this.diferencias = diferencias;
    }

    public boolean isConDiferencias() {
        return conDiferencias;
    }

    public void setConDiferencias(boolean conDiferencias) {
        this.conDiferencias = conDiferencias;
    }

    /**
     * @return the acumuladoMes
     */
    public boolean isAcumuladoMes() {
        return acumuladoMes;
    }

    /**
     * @param acumuladoMes
     * the acumuladoMes to set
     */
    public void setAcumuladoMes(boolean acumuladoMes) {
        this.acumuladoMes = acumuladoMes;
    }
    public String getObservaciones() {
        return observaciones;
    }
    /**
     * Asigna la variable  observaciones
     * 
     * @param  observaciones
     * Variable a asignar en  observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

}
