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
import com.sysman.nomina.enums.ReporteCuotasPartesControladorEnum;
import com.sysman.nomina.enums.ReporteCuotasPartesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 08/09/2015
 *
 * @author spina
 * @version 2, 25/10/2017 - se refactoriza para dss, depuracion y ejbs
 *
 */
@ManagedBean
@ViewScoped
public class ReporteCuotasPartesControlador extends BeanBaseModal
{

    private final String compania;
    private String opcion;
    private String anio1;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String empleado;
    private String anio2;
    private String cedulaEmpleado;
    private String nombre;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private List<Registro> listaAno2;
    private RegistroDataModelImpl listaEmpleado;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ReporteCuotasPartesControlador
     */
    public ReporteCuotasPartesControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.REPORTE_CUOTAS_PARTES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ReporteCuotasPartesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        proceso = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                        .toString();
        anio1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        anio2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        mes2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        periodo1 = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        periodo2 = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        cargarListaAno1();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaAno2();
        cargarListaEmpleado();
        abrirFormulario();
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
                                                            ReporteCuotasPartesControladorUrlEnum.URL7540
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
        param.put(GeneralParameterEnum.ANO.getName(), anio1);
        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7541
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio2);
        try
        {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7541
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
        param.put(GeneralParameterEnum.ANO.getName(), anio1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7542
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
        param.put(GeneralParameterEnum.ANO.getName(), anio2);
        param.put(GeneralParameterEnum.MES.getName(), mes2);
        try
        {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7542
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7543
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteCuotasPartesControladorUrlEnum.URL7540
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
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteCuotasPartesControladorUrlEnum.URL7544
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ReporteCuotasPartesControladorEnum.NOMBRE.getValue());
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>

    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacio()
    {
        if ((anio1 == null) || (anio2 == null)
            || (mes1 == null) || (mes2 == null))
        {

            return true;

        }
        if ((periodo1 == null) || (periodo2 == null)
            || (proceso == null))
        {

            return true;
        }
        return false;
    }

    private void generaInforme(ReportesBean.FORMATOS formato)
    {
        if (validarVacio())
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2523"));
            return;
        }
        String perInicial = SysmanFunciones.concatenar(proceso, anio1,
                        SysmanFunciones.padl(mes1, 2, "0"),
                        SysmanFunciones.padl(periodo1, 2, "0"));
        String perFinal = SysmanFunciones.concatenar(proceso, anio2,
                        SysmanFunciones.padl(mes2, 2, "0"),
                        SysmanFunciones.padl(periodo2, 2, "0"));

        if (perInicial.compareTo(perFinal) > 0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2509"));
            return;
        }
        String strWhere = "";
        if ("2".equals(opcion))
        {
            if (cedulaEmpleado == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2506"));
                return;
            }
            else
            {
                strWhere = SysmanFunciones.concatenar(
                                " AND PERSONAL.NUMERO_DCTO=",
                                cedulaEmpleado);
            }
        }
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            reemplazar.put("strWhere", strWhere);
            reemplazar.put("perInicial", perInicial);
            reemplazar.put("perFinal", perFinal);

            String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
            String encabezado = idioma.getString("TB_TB3741")
                            .replace("s$mes1$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes1)])
                            .replace("s$anio1$s", anio1)
                            .replace("s$periodo1$s", SysmanFunciones
                                            .padl(periodo1, 2, "0"))
                            .replace("s$mes2$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes2)])
                            .replace("s$anio2$s", anio2)
                            .replace("s$periodo2$s", SysmanFunciones
                                            .padl(periodo2, 2, "0"));

            Reporteador.resuelveConsulta("000151AcumuladosCuotasPartes",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
            parametros.put("PR_ENCABEZADO", encabezado);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000151AcumuladosCuotasPartes", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (IOException | JRException | SysmanException ex)
        {
            Logger.getLogger(ReporteCuotasPartesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cambiarOpcion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cedulaEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_DCTO"), "")
                        .toString();
        empleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2()
    {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2()
    {
        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        opcion = "1";

        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getAnio1()
    {
        return anio1;
    }

    public void setAnio1(String anio1)
    {
        this.anio1 = anio1;
    }

    public String getMes1()
    {
        return mes1;
    }

    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    public String getMes2()
    {
        return mes2;
    }

    public void setMes2(String mes2)
    {
        this.mes2 = mes2;
    }

    public String getPeriodo1()
    {
        return periodo1;
    }

    public void setPeriodo1(String periodo1)
    {
        this.periodo1 = periodo1;
    }

    public String getPeriodo2()
    {
        return periodo2;
    }

    public void setPeriodo2(String periodo2)
    {
        this.periodo2 = periodo2;
    }

    public String getProceso()
    {
        return proceso;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    public String getEmpleado()
    {
        return empleado;
    }

    public void setEmpleado(String empleado)
    {
        this.empleado = empleado;
    }

    public String getAnio2()
    {
        return anio2;
    }

    public void setAnio2(String anio2)
    {
        this.anio2 = anio2;
    }

    public String getCedulaEmpleado()
    {
        return cedulaEmpleado;
    }

    public void setCedulaEmpleado(String cedulaEmpleado)
    {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
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

    public List<Registro> getListaMes2()
    {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2)
    {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaPeriodo2()
    {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2)
    {
        this.listaPeriodo2 = listaPeriodo2;
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

    public RegistroDataModelImpl getListaEmpleado()
    {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado)
    {
        this.listaEmpleado = listaEmpleado;
    }
}
