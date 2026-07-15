package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmprescripcionesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * @author NGOMEZ
 * @version 1, 02/06/2016
 *
 * --Modificado por lcortes 16/02/2017
 *
 * @author spina
 * @version 2, 05/07/2017 - refactorizo dss, depuracion sonar y ejbs
 *
 */
@ManagedBean
@ViewScoped
public class FrmprescripcionesControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String resumen;
    private String agrupado;
    private String predioInicial;
    private String predioFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listapredioinicial;
    private RegistroDataModelImpl listaPredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmprescripcionesControlador
     */
    public FrmprescripcionesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPRESCRIPCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmprescripcionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListapredioinicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListapredioinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprescripcionesControladorUrlEnum.URL4409
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listapredioinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaPredioFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprescripcionesControladorUrlEnum.URL5371
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(GeneralParameterEnum.PREDIO.getName(),
                        predioInicial);

        listaPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        String nombre = "000866PREDIALRELPRESCRIPCIONES";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("predioInicial", predioInicial);
            reemplazar.put("predioFinal", predioFinal);
            reemplazar.put("nOrden", SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "true".equals(agrupado)
                ? "000866PREDIALRELPRESCRIPCIONESAGRUPADO" : nombre;
            String strSql = "true".equals(agrupado)
                ? Reporteador.resuelveConsulta(nombre,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar)
                : "";
            reemplazar.put("consulta", strSql);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            List<String> nombreCon = new ArrayList<>();
            nombreCon.add(nombreConcepto("13"));
            nombreCon.add(nombreConcepto("14"));
            nombreCon.add(nombreConcepto("15"));
            nombreCon.add(nombreConcepto("16"));
            nombreCon.add(nombreConcepto("17"));
            nombreCon.add(nombreConcepto("18"));
            nombreCon.add(nombreConcepto("19"));
            nombreCon.add(nombreConcepto("20"));
            parametros.put("PR_NOMBRE_CONCEPTOS", nombreCon);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_FORMS_FRMPRESCRIPCIONES_FECHAINI",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_FRMPRESCRIPCIONES_FECHAFIN",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reporte = nombre;
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            "<br>", e.getMessage()));
        }
    }

    public String nombreConcepto(String codigo)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.getParteFecha(new Date(),
                                        Calendar.YEAR));
        param.put(GeneralParameterEnum.NUMERO.getName(), codigo);

        Registro reg = null;
        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprescripcionesControladorUrlEnum.URL5372
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return reg == null ? " "
            : reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName())
                            .toString().toUpperCase();
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilapredioinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "0")
                        .toString();
        predioFinal = null;
        cargarListaPredioFinal();
    }

    public void seleccionarFilaPredioFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "0")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getResumen()
    {
        return resumen;
    }

    public void setResumen(String resumen)
    {
        this.resumen = resumen;
    }

    public String getAgrupado()
    {
        return agrupado;
    }

    public void setAgrupado(String agrupado)
    {
        this.agrupado = agrupado;
    }

    public String getPredioInicial()
    {
        return predioInicial;
    }

    public void setPredioInicial(String predioInicial)
    {
        this.predioInicial = predioInicial;
    }

    public String getPredioFinal()
    {
        return predioFinal;
    }

    public void setPredioFinal(String predioFinal)
    {
        this.predioFinal = predioFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListapredioinicial()
    {
        return listapredioinicial;
    }

    public void setListapredioinicial(RegistroDataModelImpl listapredioinicial)
    {
        this.listapredioinicial = listapredioinicial;
    }

    public RegistroDataModelImpl getListaPredioFinal()
    {
        return listaPredioFinal;
    }

    public void setListaPredioFinal(RegistroDataModelImpl listaPredioFinal)
    {
        this.listaPredioFinal = listaPredioFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
