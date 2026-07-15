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
import com.sysman.nomina.enums.ResumPorDependenciasControladorEnum;
import com.sysman.nomina.enums.ResumPorDependenciasControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 12/08/2015 controlador y se paso a la bd
 * 
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2, 27/10/2017
 */
@ManagedBean
@ViewScoped

public class ResumPorDependenciasControlador extends BeanBaseModal
{

    private final String compania;
    private String opcion;
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String empleado;

    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private List<Registro> listaEmpleado;
    private StreamedContent archivoDescarga;
    private final String modulo;

    /**
     * Creates a new instance of ResumPorDependenciasControlador
     */
    public ResumPorDependenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
        ano1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
        ano2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
        mes1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
        mes2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
        periodo1 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
        periodo2 = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RESUM_POR_DEPENDENCIAS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaProceso();
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaEmpleado();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        opcion = "1";
    }

    public void cargarListaAno1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ResumPorDependenciasControladorUrlEnum.URL3812.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno2()
    {
        listaAno2 = listaAno1;
    }

    public void cargarListaMes1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ResumPorDependenciasControladorUrlEnum.URL4125.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes2()
    {
        listaMes2 = listaMes1;
    }

    public void cargarListaPeriodo1()
    {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        param.put(ResumPorDependenciasControladorEnum.ID_DE_PROCESO.getValue(), SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("procesoNomina"), "").toString());

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ResumPorDependenciasControladorUrlEnum.URL4127.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2()
    {
        listaPeriodo2 = listaPeriodo1;
    }

    public void cargarListaProceso()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaProceso = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ResumPorDependenciasControladorUrlEnum.URL7329.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEmpleado()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaEmpleado = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ResumPorDependenciasControladorUrlEnum.URL4246.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        getReporte(FORMATOS.EXCEL97);
    }

    private boolean validaciones()
    {
        boolean rta = true;

        if (SysmanFunciones.validarVariableVacio(ano1))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2672"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mes1))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2673"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(periodo1))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2674"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(ano2))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2675"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mes2))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2676"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(periodo2))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2677"));
            rta = false;
        }

        return rta;
    }

    public void getReporte(FORMATOS formato)
    {

        if (!validaciones())
        {
            return;
        }

        if (!validacionPeriodo())
        {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(), compania);
        reemplazar.put("codigoInicial", SysmanFunciones.concatenar(
                        SysmanFunciones.padl(proceso, 2, "0"), "", SysmanFunciones.padl(ano1, 4, "0"), "",
                        SysmanFunciones.padl(mes1, 2, "0"), "", SysmanFunciones.padl(periodo1, 2, "0")));
        reemplazar.put("codigoFinal",
                        SysmanFunciones.concatenar(SysmanFunciones.padl(proceso, 2, "0"), "", SysmanFunciones.padl(ano2, 4, "0"), "",
                                        SysmanFunciones.padl(mes2, 2, "0"), "", SysmanFunciones.padl(periodo2, 2, "0")));

        if ("2".equals(opcion))
        {
            if (SysmanFunciones.validarVariableVacio(empleado))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2678"));
                return;
            }
            reemplazar.put(ResumPorDependenciasControladorEnum.FILTRO.getValue().toLowerCase(),
                            SysmanFunciones.concatenar("AND V_ACUMULADOS.DEPENDENCIA = '", empleado, "'"));
        }
        else
        {
            reemplazar.put(ResumPorDependenciasControladorEnum.FILTRO.getValue().toLowerCase(), "");
        }
        Map<String, Object> parametros = new HashMap<>();
        Reporteador.resuelveConsulta(ResumPorDependenciasControladorEnum.REPORTE000156.getValue(),
                        Integer.valueOf(modulo),
                        reemplazar, parametros);
        try
        {

            String parametroEntreAux = idioma.getString("TB_TB3746")
                            .replace("s$mes$s", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes1)])
                            .replace("s$ano1$s", ano1).replace("s$periodo1$s", periodo1)
                            .replace("s$mes2s$", SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)])
                            .replace("s$ano2$s", ano2).replace("s$periodo2$s", periodo2);

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENTRE", parametroEntreAux);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            archivoDescarga = JsfUtil.exportarStreamed(
                            ResumPorDependenciasControladorEnum.REPORTE000156.getValue(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            ResumPorDependenciasControladorEnum.REPORTE000156.getValue()));
            Logger.getLogger(ResumPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            Logger.getLogger(ResumPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), ex.getMessage()));
        }

    }

    private boolean validacionPeriodo()
    {
        boolean rta = true;

        String periodoInicial = SysmanFunciones.concatenar(ano1, SysmanFunciones.padl(mes1, 2, "0"),
                        SysmanFunciones.padl(periodo1, 2, "0"));
        String periodoFinal = SysmanFunciones.concatenar(ano2, SysmanFunciones.padl(mes2, 2, "0"), SysmanFunciones.padl(periodo2, 2, "0"));

        if (periodoInicial.compareTo(periodoFinal) > 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB574"));

            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(proceso))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2548"));

            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(opcion))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));

            rta = false;
        }

        return rta;
    }

    public void cambiarOpcion()
    {
        // heredado del bean base
    }

    public void cambiarAno1()
    {

        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();

    }

    public void cambiarAno2()
    {
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
    }

    public void cambiarMes1()
    {
        periodo1 = null;
        cargarListaPeriodo1();
    }

    public void cambiarMes2()
    {
        periodo2 = null;
        cargarListaPeriodo2();
    }

    public void cambiarProceso()
    {
        ano1 = null;
        ano2 = null;
        mes1 = null;
        mes2 = null;
        periodo1 = null;
        periodo2 = null;
        cargarListaAno1();
        cargarListaAno2();
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getAno1()
    {
        return ano1;
    }

    public void setAno1(String ano1)
    {
        this.ano1 = ano1;
    }

    public String getAno2()
    {
        return ano2;
    }

    public void setAno2(String ano2)
    {
        this.ano2 = ano2;
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

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaAno2()
    {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2)
    {
        this.listaAno2 = listaAno2;
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

    public List<Registro> getListaEmpleado()
    {
        return listaEmpleado;
    }

    public void setListaEmpleado(List<Registro> listaEmpleado)
    {
        this.listaEmpleado = listaEmpleado;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

}
