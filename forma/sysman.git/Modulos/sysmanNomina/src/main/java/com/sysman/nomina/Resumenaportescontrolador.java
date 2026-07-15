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
import com.sysman.nomina.enums.ResumenaportescontroladorEnum;
import com.sysman.nomina.enums.ResumenaportescontroladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 22/08/2015
 * @author jcrodriguez,Refactoring y depuracion de dss
 * @version 2, 25/10/2017
 */
@ManagedBean
@ViewScoped

public class Resumenaportescontrolador extends BeanBaseModal {

    private final String compania;

    private String ano1;
    private String mes1;
    private String periodo1;
    private String proceso;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private String anioSession;
    private String mesSession;
    private String periodoSession;
    private String consulta1;
    private StreamedContent archivoDescarga;
    private boolean pagoTerceros;
    private boolean cooperativasFuenteRecurso;
    private boolean agrupadoTercero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of Resumenaportescontrolador
     */
    public Resumenaportescontrolador() {
        super();
        compania = SessionUtil.getCompania();
        numFormulario = GeneralCodigoFormaEnum.RESUMENAPORTESCONTROLADOR
                        .getCodigo();
        try {

            validarPermisos();
            anioSession = SessionUtil
                            .getSessionVar("anioNomina").toString();
            mesSession = SessionUtil
                            .getSessionVar("mesNomina").toString();
            periodoSession = SessionUtil
                            .getSessionVar("periodoNomina").toString();
            ano1 = anioSession;
            mes1 = mesSession;
            periodo1 = periodoSession;
            proceso = SysmanFunciones
                            .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                            .toString();
        }
        catch (Exception ex) {
            Logger.getLogger(Resumenaportescontrolador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    public void cargarListaAno1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenaportescontroladorUrlEnum.URL3367
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

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenaportescontroladorUrlEnum.URL4125
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo1() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        param.put(ResumenaportescontroladorEnum.ID_DE_PROCESO.getValue(),
                        SessionUtil
                                        .getSessionVar("procesoNomina")
                                        .toString());

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenaportescontroladorUrlEnum.URL4127
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPreliminarBancos() {
        generarReporte(FORMATOS.PDF);
    }

    public void oprimirComando10() {
        generarReporte(FORMATOS.EXCEL);
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        try {
            archivoDescarga = null;
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("proceso", proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put("periodo1", periodo1);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_PERIODO", periodo1);
            parametros.put("PR_ANO", ano1);
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)].toUpperCase());
            parametros.put("PR_NOMBREPERIODO",
                            SessionUtil.getSessionVar("nombrePeriodoNomina"));
            parametros.put("PR_NOMBRE_GERENTE",
                            obtenerParametro("NOMBRE DEL GERENTE",
                                            ""));
            parametros.put("PR_CARGO_GERENTE",
                            obtenerParametro("CARGO DEL GERENTE",
                                            ""));
            parametros.put("PR_NOMBRE_CARGO_TESORERO_PAGADOR",
                            obtenerParametro(
                                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                                            ""));
            parametros.put("PR_CARGO_TESORERO_PAGADOR",
                            obtenerParametro("CARGO DEL TESORERO PAGADOR",
                                            ""));
            parametros.put("PR_NOMBRE_AUTORIZA_NOMINA",
                            obtenerParametro("NOMBRE DE QUIEN AUTORIZA NOMINA",
                                            ""));
            parametros.put("PR_CARGO_AUTORIZA_NOMINA",
                            obtenerParametro("CARGO DE QUIEN AUTORIZA NOMINA",
                                            ""));
            String formatoResumen = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO APORTES COOPERATIVAS",
                            SessionUtil.getModulo(),
                            new Date(), false);
            String nombreInforme;
            String nombreConsulta;

            if (isPagoTerceros()) {
                nombreInforme = "001762PagosATerceros";
                nombreConsulta = nombreInforme;
            }
            else if (isCooperativasFuenteRecurso()) {
                nombreInforme = "000174APORTECOPERATIVASFUENTERECURSO";
                nombreConsulta = nombreInforme;
            }
            else if (isAgrupadoTercero()) {
                nombreInforme = "000013DescuentosCooperativas";
                nombreConsulta = "002015DescuentosCooperativas";
            }
            else if ("000173AportesCooperativas".equals(formatoResumen)) {
                nombreInforme = "000173AportesCooperativas";
                nombreConsulta = nombreInforme;
            }
            else if ("002632AportesCooperativasCuatroFirmas".equals(formatoResumen)) {
//            	// 7750292 - En caso tal de ser el informe de la condicion agrega la cuarta firma si el parametro es si
				String nombreCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE CUARTA FIRMA", SessionUtil.getModulo(),
						new Date(), false), "NO");
				String cargoCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "CARGO CUARTA FIRMA", SessionUtil.getModulo(),
						new Date(), false), "NO");
				
				parametros.put("PR_NOMBRE_CUARTA_FIRMA", nombreCuartaFirma);
				parametros.put("PR_CARGO_CUARTA_FIRMA", cargoCuartaFirma);

                nombreInforme = "002632AportesCooperativasCuatroFirmas";
                nombreConsulta = nombreInforme;
            }
            else {
                nombreInforme = "001750Listadoconceptosanticipados";
                nombreConsulta = nombreInforme;
            }

            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreInforme,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (SysmanException | JRException
                        | IOException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    public void cambiarpagoTerceros() {
        cooperativasFuenteRecurso = false;
        agrupadoTercero = false;
    }

    public void cambiarcooperativasPorFuenteRecurso() {
        pagoTerceros = false;
        agrupadoTercero = false;
    }

    public void cambiarAgrupadoTercero() {
        // <CODIGO_DESARROLLADO>
        pagoTerceros = false;
        cooperativasFuenteRecurso = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1() {
        mes1 = "";
        periodo1 = "";
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public void cambiarMes1() {
        periodo1 = "";
        cargarListaPeriodo1();
    }

    public void cambiarPeriodo1() {
        // heredado del bean base
    }

    /**
     * @return the proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * @param proceso
     * the proceso to set
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
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

    /**
     * @return the pagoTerceros
     */
    public boolean isPagoTerceros() {
        return pagoTerceros;
    }

    /**
     * @param pagoTerceros
     * the pagoTerceros to set
     */
    public void setPagoTerceros(boolean pagoTerceros) {
        this.pagoTerceros = pagoTerceros;
    }

    /**
     * @return the cooperativasFuenteRecurso
     */
    public boolean isCooperativasFuenteRecurso() {
        return cooperativasFuenteRecurso;
    }

    /**
     * @param cooperativasFuenteRecurso
     * the cooperativasFuenteRecurso to set
     */
    public void setCooperativasFuenteRecurso(
        boolean cooperativasFuenteRecurso) {
        this.cooperativasFuenteRecurso = cooperativasFuenteRecurso;
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

    public String getConsulta1() {
        return consulta1;
    }

    public void setConsulta1(String consulta1) {
        this.consulta1 = consulta1;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the agrupadoTercero
     */
    public boolean isAgrupadoTercero() {
        return agrupadoTercero;
    }

    /**
     * @param agrupadoTercero
     * the agrupadoTercero to set
     */
    public void setAgrupadoTercero(boolean agrupadoTercero) {
        this.agrupadoTercero = agrupadoTercero;
    }

}
