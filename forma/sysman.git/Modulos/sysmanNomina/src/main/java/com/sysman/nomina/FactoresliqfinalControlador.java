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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.enums.FactoresliqfinalControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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
 * @author sdaza
 * @version 1, 27/08/2015
 *
 * @author spina
 * @version 2, 06/10/2017 - se refactoriza para dss, depuracion y ejbs
 */
@ManagedBean
@ViewScoped
public class FactoresliqfinalControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el proceso de la nomina
     */
    private final String procesoNomina;
    /**
     * variable que almacena el a�o de la nomina
     */
    private final String anoNomina;
    /**
     * variable que almacena el mes de la nomina
     */
    private final String mesNomina;
    /**
     * variable que almacena el periodo de la nomina
     */
    private final String periodoNomina;
    /**
     * variable que almacena el reporte
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que almacena el a�o
     */
    private String ano;
    /**
     * variable que almacena el mes
     */
    private String mes;
    /**
     * variable que almacena el periodo
     */
    private String periodo;
    /**
     * varible que almacena el empleado
     */
    private String empleado;
    /**
     * variable que almacena la observaciones
     */
    private String observacion;
    /**
     * variable que almacena la lista de a�os
     */
    private List<Registro> listaAno1;
    /**
     * variable que almacena la lista de mes
     */
    private List<Registro> listaMes1;
    /**
     * variable que almacena la lista de periodos
     */
    private List<Registro> listaPeriodo1;
    /**
     * lista los empleados
     */
    private RegistroDataModelImpl listaEmpleado;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    /**
     * Creates a new instance of FactoresliqfinalControlador
     */
    public FactoresliqfinalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        periodoNomina = SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("periodoNomina"), "").toString();
        procesoNomina = SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("procesoNomina"), "").toString();
        anoNomina = SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("anioNomina"), "").toString();
        mesNomina = SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("mesNomina"), "").toString();
        numFormulario = GeneralCodigoFormaEnum.FACTORESLIQFINAL_CONTROLADOR
                        .getCodigo();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FactoresliqfinalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        cargarListaAno1();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        ano = anoNomina;
        mes = mesNomina;
        periodo = periodoNomina;
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaEmpleado();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactoresliqfinalControladorUrlEnum.URL28520
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactoresliqfinalControladorUrlEnum.URL28521
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
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), procesoNomina);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FactoresliqfinalControladorUrlEnum.URL28522
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEmpleado()
    {
        try
        {
            Date fechaPeriodo = ejbNominaCero.getFechaPeriodoIniFin(
                            compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false, true);

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FactoresliqfinalControladorUrlEnum.URL28523
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ESTADO.getName(), 1);
            param.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones
                            .convertirAFechaCadena(fechaPeriodo));

            listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        }
        catch (NumberFormatException | SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPreliminarBancos()
    {
        archivoDescarga = null;
        genInformeLiquidacion(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcelBancos()
    {
        archivoDescarga = null;
        genInformeLiquidacion(ReportesBean.FORMATOS.EXCEL);
    }

    private boolean mensajeInformeLiquidacion()
    {
        if (SysmanFunciones.validarVariableVacio(ano))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2543"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(mes))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2544"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(periodo))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2545"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(empleado))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2546"));
            return true;
        }
        return false;
    }

    private void genInformeLiquidacion(ReportesBean.FORMATOS formato)
    {
        if (mensajeInformeLiquidacion())
        {
            return;
        }
        try
        {
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha(SysmanFunciones.concatenar("01/",
                                            mes, "/", ano)));

            String formatoFactoresLiquidacion = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "FORMATO FACTORES LIQUIDACION FINAL",
                                            modulo, fechaParametros, true);

            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplaza = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            reemplaza.put("idProceso", procesoNomina);
            reemplaza.put("ano", ano);
            reemplaza.put("mes", mes);
            reemplaza.put("periodo", periodo);
            reemplaza.put("idEmpleado", empleado);
            String sql = Reporteador.resuelveConsulta(
                            formatoFactoresLiquidacion,
                            Integer.parseInt(modulo), reemplaza);
            parametros.put("PR_STRSQL", sql);

            String miNit = SysmanFunciones.nvl(
                            ejbSysmanUtil.formatearNitEntidad(compania, 311),
                            "").toString();

            parametros.put("PR_MINIT", miNit);
            parametros.put("PR_OBSERVACION", observacion);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE", modulo, fechaParametros, true);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);

            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", modulo, fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);

            String nombreLiquidador = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN LIQUIDA NOMINA", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
                            nombreLiquidador);

            String cargoLiquidador = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN LIQUIDA NOMINA", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA", cargoLiquidador);

            String nombreRevisa = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN REVISA NOMINA", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA", nombreRevisa);

            String cargoRevisa = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN REVISA NOMINA", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA", cargoRevisa);

            String apruebaLiquidacion = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DE QUIEN APRUEBA LIQUIDACION DEFINITIVA",
                            modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DE_QUIEN_APRUEBA_LIQUIDACION_DEFINITIVA",
                            apruebaLiquidacion);

            String apruebaCargoLiquidacion = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CARGO DE QUIEN APRUEBA LIQUIDACION DEFINITIVA",
                            modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CARGO_DE_QUIEN_APRUEBA_LIQUIDACION_DEFINITIVA",
                            apruebaCargoLiquidacion);

            String ciudadExpide = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CIUDAD DONDE SE EXPIDE CERTIFICACION LABORAL",
                            modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CIUDAD_DONDE_SE_EXPIDE_CERTIFICACION_LABORAL",
                            ciudadExpide);

            String sqlSubReporte = Reporteador.resuelveConsulta(
                            "000184VLIQDEFINITIVA", Integer.parseInt(modulo),
                            null);
            parametros.put("PR_STRSQL_RUBRO_PRESUPUESTAL", sqlSubReporte);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(
                            formatoFactoresLiquidacion, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | SystemException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2547"));
        }
        catch (ParseException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPlantillaLiquidacion()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInformePlanilla(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcelPl()
    {
        // <CODIGO_DESARROLLADO>
        if (!mensajeInformePlantilla())
        {
            archivoDescarga = null;
            genInformePlanilla(ReportesBean.FORMATOS.EXCEL);
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean mensajeInformePlantilla()
    {
        if (SysmanFunciones.validarVariableVacio(ano))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2543"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(mes))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2544"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(periodo))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2545"));
            return true;
        }
        return false;
    }

    private void genInformePlanilla(ReportesBean.FORMATOS formato)
    {

        try
        {
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplaza = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha(SysmanFunciones.concatenar("01/",
                                            mes, "/", ano)));

            reemplaza.put("idProceso", procesoNomina);
            reemplaza.put("ano", ano);
            reemplaza.put("mes", mes);
            reemplaza.put("periodo", periodo);
            String sql = Reporteador.resuelveConsulta(
                            "000191PLANILLAFACTORESLIQUIDACIONFINAL",
                            Integer.parseInt(modulo), reemplaza);
            parametros.put("PR_STRSQL", sql);
            Date fechaPer = SysmanFunciones.convertirAFecha(
                            SysmanFunciones.concatenar("01/01/", ano));
            parametros.put("PR_FECHAPERIODO", fechaPer);
            parametros.put("PR_ANO", ano);

            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", modulo, fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE", modulo, fechaParametros,
                            true);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);

            String nombreTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", modulo,
                            fechaParametros,
                            true);

            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombreTesorero);

            String cargoTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL TESORERO PAGADOR", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR", cargoTesorero);

            String nombreJefeRH = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE JEFE RECURSOS HUMANOS", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS", nombreJefeRH);

            String cargoJefeRH = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoJefeRH);

            String nombreAutoriza = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo,
                            fechaParametros,
                            true);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreAutoriza);

            String cargoAutoriza = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE QUIEN AUTORIZA NOMINA", modulo,
                            fechaParametros,
                            true);

            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoAutoriza);
            parametros.put("PR_OBSERVACION", observacion);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000191PLANILLAFACTORESLIQUIDACIONFINAL",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | JRException | IOException
                        | ParseException | SysmanException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes1();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO.getName())
                        .toString();
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getEmpleado()
    {
        return empleado;
    }

    public void setEmpleado(String empleado)
    {
        this.empleado = empleado;
    }

    public String getObservacion()
    {
        return observacion;
    }

    public void setObservacion(String observacion)
    {
        this.observacion = observacion;
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

    public RegistroDataModelImpl getListaEmpleado()
    {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado)
    {
        this.listaEmpleado = listaEmpleado;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

}
