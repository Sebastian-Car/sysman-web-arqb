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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenPorDependenciasControladorEnum;
import com.sysman.nomina.enums.ResumenPorDependenciasControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 25/08/2015
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2, 26/10/2017
 */
@ManagedBean
@ViewScoped

public class ResumenPorDependenciasControlador extends BeanBaseModal
{

    private final String compania;
    private String opcion;
    private String interfase;
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String dependencia;
    private String encabezado;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaDependencia;
    private String anioSession;
    private String mesSession;
    private String periodoSession;
    private String procesoSesion;
    private String moduloNomina;
    private String reporte;
    private String tesoreroPagador;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private StreamedContent archivoDescarga;
    private String cargoJefe;
    private String nombreJefe;
    private String nombreGerente;
    private String cargoGerente;
    private String nombreTesorero;
    //JM CC 1614 01/07/2025
    private String cargoAlcalde;
    private String nombreAlcalde;

    /**
     * Creates a new instance of ResumenPorDependencias
     */
    public ResumenPorDependenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        encabezado = SessionUtil.getCompaniaIngreso().getNombre();
        try
        {

            numFormulario = GeneralCodigoFormaEnum.RESUMEN_POR_DEPENDENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            anioSession = SessionUtil
                            .getSessionVar("anioNomina").toString();
            mesSession = SessionUtil
                            .getSessionVar("mesNomina").toString();
            periodoSession = SessionUtil
                            .getSessionVar("periodoNomina").toString();
            procesoSesion = SessionUtil
                            .getSessionVar("procesoNomina").toString();
            moduloNomina = SessionUtil.getModulo();
            ano1 = ano2 = anioSession;
            mes1 = mes2 = mesSession;
            periodo1 = periodo2 = periodoSession;
            proceso = procesoSesion;
            opcion = "1";
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumenPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaDependencia();
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
                                                            ResumenPorDependenciasControladorUrlEnum.URL3367
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
        listaAno2 = listaAno1;
    }

    public void cargarListaMes1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenPorDependenciasControladorUrlEnum.URL4125
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
        listaMes2 = listaMes1;
    }

    public void cargarListaPeriodo1()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        param.put(ResumenPorDependenciasControladorEnum.ID_DE_PROCESO
                        .getValue(),
                        SessionUtil
                                        .getSessionVar("procesoNomina")
                                        .toString());

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenPorDependenciasControladorUrlEnum.URL4127
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
        listaPeriodo2 = listaPeriodo1;
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
                                                            ResumenPorDependenciasControladorUrlEnum.URL11204
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDependencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorDependenciasControladorUrlEnum.URL4123
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * validacion de ano
     * 
     * @return
     */

    private boolean validarAno()
    {

        return Integer.parseInt(ano1) > Integer.parseInt(ano2);

    }

    private boolean validarPeriodo()
    {
        return Integer.parseInt(periodo1) > Integer.parseInt(periodo2);
    }

    /**
     * validacion de mes
     * 
     * @return
     */
    private boolean validarMes()
    {
        return Integer.parseInt(mes1) > Integer.parseInt(mes2);
    }

    public void getInforme(FORMATOS formato)
    {
        try
        {
            if (validarAno())
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB723"));
                return;
            }
            if (validarMes())
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB40"));
                return;
            }
            if (validarPeriodo())
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1734"));
                return;
            }
            archivoDescarga = null;
            String[] arrayNombresJasper = new String[2];
            Map<String, Object>[] listaParametros = new HashMap[2];
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String entre = idioma.getString("TB_TB3744")
                            .replace("s$nombre$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes1)])
                            .replace("s$ano$s", ano1)
                            .replace("s$periodo$s", periodo1)
                            .replace("s$mes$s",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes2)])
                            .replace("s$ano2$s", ano2)
                            .replace("s$periodo2$s", periodo2);

            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_JEFE_PAGADOR", tesoreroPagador);

            reemplazar.put("CONDDEPENDENCIA", "3".equals(opcion)
                            ? SysmanFunciones.concatenar("AND V_ACUMULADOS.DEPENDENCIA='",
                                            dependencia, "' ")
                            : "");
            reemplazar.put("nitCompania",
                            SessionUtil.getCompaniaIngreso().getNit());
            reemplazar.put("proceso", proceso);
            reemplazar.put("ano1", ano1);
            reemplazar.put("ano2", ano2);
            reemplazar.put("mes1", mes1);
            reemplazar.put("mes2", mes2);
            reemplazar.put("periodo1", periodo1);
            reemplazar.put("periodo2", periodo2);

            tesoreroPagador = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            nombreJefe = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE JEFE DE NOMINA",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            cargoJefe = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL JEFE DE NOMINA",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            nombreGerente = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE GERENTE DE NOMINA",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            cargoGerente = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "CARGO GERENTE DE NOMINA",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            nombreTesorero = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE TESORERO",
                            SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            
            //JM INI CC 1614 01/07/2025
            
            nombreAlcalde = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE DEL ALCALDE",
                    SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            cargoAlcalde  = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                    "CARGO DEL ALCALDE",
                    SessionUtil.getModulo(), new Date(), true).toString(), " ").toString();
            
            parametros.put("PR_CARGO_ALCALDE", cargoAlcalde);
            parametros.put("PR_NOMBRE_ALCALDE", nombreAlcalde);
            //JM FIN CC 1614 01/07/2025 
            parametros.put("PR_CARGO_TESORERO", tesoreroPagador);
            parametros.put("PR_NOMBRE_TESORERO", nombreTesorero);
            parametros.put("PR_NOMBRE_JEFE", nombreJefe);
            parametros.put("PR_CARGO_JEFE", cargoJefe);
            parametros.put("PR_NOMBRE_GERENTE", nombreGerente);
            parametros.put("PR_CARGO_GERENTE", cargoGerente);

            if ("false".equalsIgnoreCase(interfase))
            {
                reporte = obtenerValorReporte(
                                "FORMATO RESUMEN NOMINA POR DEPENDENCIAS",
                                "000185ResumenPorDependencias");
                if ("RESUMENPORDEPENDENCIAS_HOS"
                                .equalsIgnoreCase(reporte))
                {
                    /*
                     * *** ESPACIO PARA GENERAR EL INFORME "RESUMENPORDEPENDENCIAS_HOS"
                     * 
                     * EN EL MOMENTO NO EXISTE
                     * 
                     * ***************
                     */
                }
                else
                {
                    Map<String, Object> parametros2 = new HashMap<>();
                    Reporteador.resuelveConsulta(reporte,
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);
                    int conteo = (int) service.getConteoConsulta(
                                    parametros.get("PR_STRSQL").toString());
                    if (conteo > 0)
                    {
                        arrayNombresJasper[0] = reporte;
                        listaParametros[0] = parametros;
                        reporte = obtenerValorReporte(
                                        "FORMATO RESUMEN NOMINA POR GRUPO CONTABLE Y DEPENDENCIAS",
                                        "000192RESUMENPORGRUPODEPENDENCIAS");
                        Reporteador.resuelveConsulta(reporte,
                                        Integer.parseInt(SessionUtil
                                                        .getModulo()),
                                        reemplazar, parametros2);
                        parametros2.put("PR_ENTRE", entre);
                        parametros2.put("PR_NOMBREEMPRESA", SessionUtil
                                        .getCompaniaIngreso().getNombre());
                        
                        // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz mu�oz)
                        parametros2.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                        // FIN IMPLEMENTACION MARCA_BLANCA
                        
                        arrayNombresJasper[1] = reporte;
                        listaParametros[1] = parametros2;
                        archivoDescarga = JsfUtil
                                        .exportarComprimidoReportesStreamed(
                                                        arrayNombresJasper,
                                                        listaParametros,
                                                        ConectorPool.ESQUEMA_SYSMAN,
                                                        formato);
                    }
                    else
                    {
                        JsfUtil.agregarMensajeAlerta(idioma.getString(
                                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
                    }
                }
            }
            else
            {
                reporte = ResumenPorDependenciasControladorEnum.REPORTE000194
                                .getValue();
                Reporteador.resuelveConsulta(
                                ResumenPorDependenciasControladorEnum.REPORTE000194
                                                .getValue(),
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(
                                ResumenPorDependenciasControladorEnum.REPORTE000194
                                                .getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
        }

        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ", reporte));
            Logger.getLogger(ResumenPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SQLException | JRException | IOException
                        | DRException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));

            Logger.getLogger(ResumenPorDependenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String obtenerValorReporte(String textoFormato,
                    String nombreReporte)
    {
        try
        {
            String valorParametro = ejbSysmanUtil.consultarParametro(compania,
                            textoFormato, moduloNomina, new Date(), false);
            return valorParametro.isEmpty()
                            ? nombreReporte
                            : valorParametro;
        }
        catch (NullPointerException | SystemException ex)
        {
            Logger.getLogger(ResumenPorDependenciasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            return nombreReporte;
        }

    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>
        if (validarAno())
        {
            ano2 = null;
            mes2 = null;
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB723"));

        }
        else
        {
            cargarListaMes1();
            cargarListaPeriodo1();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2()
    {
        if (validarAno())
        {
            ano2 = null;
            mes2 = null;
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB723"));

        }
        else
        {
            cargarListaMes2();
            cargarListaPeriodo2();
        }
    }

    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        if (validarMes())
        {
            mes2 = null;
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB40"));

        }
        else
        {
            cargarListaPeriodo1();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2()
    {
        if (validarMes())
        {
            mes2 = null;
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB40"));

        }
        else
        {
            cargarListaPeriodo2();
        }
    }

    public void cambiarPeriodo1()
    {
        if (validarPeriodo())
        {
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1734"));
        }
    }

    public void cambiarPeriodo2()
    {
        if (validarPeriodo())
        {
            periodo2 = null;
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1734"));
        }
    }

    public void cambiarOpcion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        dependencia = validarParametroCadena(registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName());
    }

    private String validarParametroCadena(Map<String, Object> campos,
                    String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
                        : campos.get(var).toString();
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getInterfase()
    {
        return interfase;
    }

    public void setInterfase(String interfase)
    {
        this.interfase = interfase;
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

    public String getDependencia()
    {
        return dependencia;
    }

    public void setDependencia(String dependencia)
    {
        this.dependencia = dependencia;
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

    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public String getEncabezado()
    {
        return encabezado;
    }

    public void setEncabezado(String encabezado)
    {
        this.encabezado = encabezado;
    }

    public void cambiarProceso()
    {
        ano1 = ano2 = mes1 = mes2 = periodo1 = periodo2 = null;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getReporte()
    {
        return reporte;
    }

    public void setReporte(String reporte)
    {
        this.reporte = reporte;
    }

    public String getTesoreroPagador()
    {
        return tesoreroPagador;
    }

    public void setTesoreroPagador(String tesoreroPagador)
    {
        this.tesoreroPagador = tesoreroPagador;
    }

}
