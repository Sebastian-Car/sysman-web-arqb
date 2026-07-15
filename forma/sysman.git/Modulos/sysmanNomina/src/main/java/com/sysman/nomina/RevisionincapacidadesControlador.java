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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.RevisionincapacidadesControladorEnum;
import com.sysman.nomina.enums.RevisionincapacidadesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
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
 * @author acaceres
 * @version 1, 27/11/2015
 * 
 * @author eamaya
 * @version 2.0,27/10/2017, Proceso de Refactoring DSS y cambio de
 * numero de formulario por enum
 */
@ManagedBean
@ViewScoped
public class RevisionincapacidadesControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String opcion;
    private String ano1;
    private String mes1;
    private String periodo1;
    private String proceso;
    private String periodo;
    private String ano2;
    private String mes2;
    private String periodo2;
    private String fechaInicial;
    private String fechaFinal;
    private String entre;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaProceso;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    private StreamedContent archivoDescarga;

    private final String fechaInicialCons;
    private final String fechaFinalCons;
    private final String periodoCons;
    private String nombregerente;
    private String cargogerente;
    private String nombretesorero;
    private String cargotesorero;
    private String autorizanomina;
    private String cargoautoriza;

    private boolean verBtnPdf;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of RevisionincapacidadesControlador
     */
    public RevisionincapacidadesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        fechaInicialCons = "fechaInicial";
        fechaFinalCons = "fechaFinal";
        periodoCons = " Periodo ";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.REVISIONINCAPACIDADES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            opcion = "1";
        }
        catch (Exception ex)
        {
            Logger.getLogger(RevisionincapacidadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        ano1 = (String) SessionUtil.getSessionVar("anioNomina");
        ano2 = (String) SessionUtil.getSessionVar("anioNomina");
        mes1 = (String) SessionUtil.getSessionVar("mesNomina");
        mes2 = (String) SessionUtil.getSessionVar("mesNomina");
        periodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
        periodo2 = (String) SessionUtil.getSessionVar("periodoNomina");
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaProceso();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();

    }

    public void cargarListaProceso()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL4237
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL4973
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);
        param.put(RevisionincapacidadesControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL5429
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(RevisionincapacidadesControladorEnum.PROCESO.getValue(),
                        proceso);

        param.put(GeneralParameterEnum.ANO.getName(), ano1);

        param.put(GeneralParameterEnum.MES.getName(), mes1);

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL5454
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL6850
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano2);
        param.put(RevisionincapacidadesControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try
        {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL7340
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(RevisionincapacidadesControladorEnum.PROCESO.getValue(),
                        proceso);

        param.put(GeneralParameterEnum.ANO.getName(), ano2);

        param.put(GeneralParameterEnum.MES.getName(), mes2);

        try
        {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RevisionincapacidadesControladorUrlEnum.URL6868
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarFechas()
    {
        fechaInicial = ano1 + (mes1.length() == 1 ? "0" + mes1 : mes1)
            + (periodo1.length() == 1 ? "0" + periodo1 : periodo1);
        fechaFinal = ano2 + (mes2.length() == 1 ? "0" + mes2 : mes2)
            + (periodo2.length() == 1 ? "0" + periodo2 : periodo2);

    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(opcion))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));

            return;
        }

        validarFechas();
        archivoDescarga = null;
        if (fechaInicial.compareTo(fechaFinal) > 0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB49"));
            return;
        }

        switch (opcion)
        {
        case "1":
            getAportesIncapacidadesEPS(FORMATOS.PDF);
            break;
        case "2":
            getAportesIncapacidadesEPSINCAP(FORMATOS.PDF);
            break;
        case "3":
            getConsultaApIncapacidadesEPS(FORMATOS.PDF);
            break;
        case "4":
            getTotalIncapacidades(FORMATOS.PDF);
            break;
        default:

        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(opcion))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));

            return;
        }
        validarFechas();
        archivoDescarga = null;
        if (fechaInicial.compareTo(fechaFinal) > 0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB49"));
            return;
        }

        switch (opcion)
        {
        case "1":
            getAportesIncapacidadesEPS(FORMATOS.EXCEL97);
            break;
        case "2":
            getAportesIncapacidadesEPSINCAP(FORMATOS.EXCEL97);
            break;
        case "3":
            getConsultaApIncapacidadesEPS(FORMATOS.EXCEL97);
            break;
        case "4":
            getTotalIncapacidades(FORMATOS.EXCEL97);
            break;
        default:
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2()
    {
        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2()
    {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso()
    {
        // <CODIGO_DESARROLLADO>
        ano1 = null;
        ano2 = null;
        mes1 = null;
        mes2 = null;
        periodo1 = null;
        periodo2 = null;
        cargarListaAno1();
        cargarListaAno2();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control controlOpcion
     * 
     * 
     */
    public void cambiarcontrolOpcion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void getAportesIncapacidadesEPS(FORMATOS formatos)
    {

        archivoDescarga = null;
        try
        {

            Map<String, Object> reemplazos = new TreeMap<>();
            reemplazos.put("proceso", proceso);
            reemplazos.put("ano1", ano1);
            reemplazos.put("ano2", ano2);
            reemplazos.put("mes1", mes1);
            reemplazos.put("mes2", mes2);
            reemplazos.put("periodo1", periodo2);
            reemplazos.put("periodo2", periodo2);
            // reemplazos.put(fechaInicialCons, fechaInicial);
            // reemplazos.put(fechaFinalCons, fechaFinal);
            Map<String, Object> parametros = new TreeMap<>();

            entre = SysmanFunciones.concatenar("Entre: ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, periodoCons,
                            periodo1.length() == 1 ? "0" + periodo1
                                : periodo1,
                            " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, periodoCons, periodo2.length() == 1
                                ? "0" + periodo2
                                : periodo2);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENTRE", entre);

            Reporteador.resuelveConsulta("000400AportesIncapacidadesEPS",
                            Integer.parseInt(modulo), reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000400AportesIncapacidadesEPS", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void getTotalIncapacidades(FORMATOS formatos)
    {
        String prEntre = "PR_ENTRE";
        String prNombreEmpresa = "PR_NOMBREEMPRESA";
        archivoDescarga = null;
        try
        {

            Map<String, Object> reemplazos = new TreeMap<>();
            reemplazos.put("proceso", proceso);
            reemplazos.put(fechaInicialCons, fechaInicial);
            reemplazos.put(fechaFinalCons, fechaFinal);

            Map<String, Object> parametros = new TreeMap<>();

            entre = SysmanFunciones.concatenar(
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, periodoCons, periodo2.length() == 1
                                ? "0" + periodo2
                                : periodo2);

            try
            {
                nombregerente = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE DEL GERENTE", modulo, new Date(), true);
                cargogerente = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO DEL GERENTE", modulo, new Date(), true);
                nombretesorero = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE DEL CARGO TESORERO PAGADOR", modulo,
                                new Date(), true);
                cargotesorero = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO DEL TESORERO PAGADOR", modulo,
                                new Date(), true);
                autorizanomina = ejbSysmanUtil.consultarParametro(compania,
                                "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
                                new Date(), true);
                cargoautoriza = ejbSysmanUtil.consultarParametro(compania,
                                "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                                new Date(), true);
                
              //7750292 - ljdiaz - cuarta firma
                String mostrarCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "ACTIVAR CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        		if("SI".equals(mostrarCuartaFirma)) {
        			String nombreCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "NOMBRE CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        			String cargoCuartaFirma = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "CARGO CUARTA FIRMA", modulo,
        					new Date(), false), "NO");
        			
        			parametros.put("PR_MOSTRAR_CUARTA_FIRMA", mostrarCuartaFirma);
        			parametros.put("PR_NOMBRE_CUARTA_FIRMA", nombreCuartaFirma);
        			parametros.put("PR_CARGO_CUARTA_FIRMA", cargoCuartaFirma);
        		}

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
            parametros.put(prNombreEmpresa,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(prEntre, entre);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombregerente);
            parametros.put("PR_CARGO_DEL_GERENTE", cargogerente);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombretesorero);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargotesorero);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            autorizanomina);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoautoriza);

            Reporteador.resuelveConsulta("001700INFORMETOTALINCAPCIDADES",
                            Integer.parseInt(modulo), reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001700INFORMETOTALINCAPCIDADES", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void getAportesIncapacidadesEPSINCAP(FORMATOS formatos)
    {
        archivoDescarga = null;
        try
        {

            Map<String, Object> reemplazos = new TreeMap<>();
            reemplazos.put(fechaInicialCons, fechaInicial);
            reemplazos.put(fechaFinalCons, fechaFinal);
            Map<String, Object> parametros = new TreeMap<>();

            entre = SysmanFunciones.concatenar("Entre: ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, periodoCons,
                            periodo1.length() == 1 ? "0" + periodo1
                                : periodo1,
                            " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, periodoCons, periodo2.length() == 1
                                ? "0" + periodo2
                                : periodo2);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENTRE", entre);

            Reporteador.resuelveConsulta("000401AportesIncapacidadesEPSINCAP",
                            Integer.parseInt(modulo), reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000401AportesIncapacidadesEPSINCAP", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void getConsultaApIncapacidadesEPS(FORMATOS formatos)
    {
        // --Revision conceptos negativos mes
        archivoDescarga = null;
        try
        {
            Map<String, Object> reemplazos = new TreeMap<>();

            reemplazos.put("compania", compania);
            reemplazos.put("proceso", proceso);
            reemplazos.put(fechaInicialCons, fechaInicial);
            reemplazos.put(fechaFinalCons, fechaFinal);

            String strSql = Reporteador
                            .resuelveConsulta("800033Ap_Incapacidades_EPS",
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getEntre()
    {
        return entre;
    }

    public void setEntre(String entre)
    {
        this.entre = entre;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    /**
     * @return the verBtnPdf
     */
    public boolean isVerBtnPdf()
    {
        return verBtnPdf;
    }

    /**
     * @param verBtnPdf
     * the verBtnPdf to set
     */
    public void setVerBtnPdf(boolean verBtnPdf)
    {
        this.verBtnPdf = verBtnPdf;
    }

    public String getAno1()
    {
        return ano1;
    }

    public void setAno1(String ano1)
    {
        this.ano1 = ano1;
    }

    public String getMes1()
    {
        return mes1;
    }

    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    public String getPeriodo1()
    {
        return periodo1;
    }

    public void setPeriodo1(String periodo1)
    {
        this.periodo1 = periodo1;
    }

    public String getProceso()
    {
        return proceso;
    }

    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    public String getAno2()
    {
        return ano2;
    }

    public void setAno2(String ano2)
    {
        this.ano2 = ano2;
    }

    public String getMes2()
    {
        return mes2;
    }

    public void setMes2(String mes2)
    {
        this.mes2 = mes2;
    }

    public String getPeriodo2()
    {
        return periodo2;
    }

    public void setPeriodo2(String periodo2)
    {
        this.periodo2 = periodo2;
    }

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaProceso()
    {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaAno2()
    {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2)
    {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2()
    {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2)
    {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2()
    {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2)
    {
        this.listaPeriodo2 = listaPeriodo2;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
