package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.ListadoResIGACControladorEnum;
import com.sysman.predial.enums.ListadoResIGACControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
 * @version 2, 09/06/2016 11:19:16 -- Modificado por jrodriguezr
 *
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambio el llamado del codigo del formulario y actualizacion de ConnectorPool
 *
 * @author spina
 * @version 4, 07/07/2017 - se refactoriza dss, depuracion sonar y ejbs
 *
 */
@ManagedBean
@ViewScoped
public class ListadoResIGACControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String resolucionInicial;
    private String resolucionFinal;
    private String opcion;
    private String predioInicial;
    private String predioFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private boolean fechaVisible;
    private boolean resolucionVisible;
    private boolean predioVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNumResIni;
    private RegistroDataModelImpl listaNumResfin;
    private RegistroDataModelImpl listaPredioInicial;
    private RegistroDataModelImpl listaPredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String nroOrdenConstante;
    private String fechaIniRes;
    private String fechaFinRes;

    /**
     * Creates a new instance of ListadoResIGACControlador
     */
    public ListadoResIGACControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_RES_IGACCONTROLADOR
                            .getCodigo();
            nroOrdenConstante = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ListadoResIGACControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        fechaInicial = fechaFinal = new Date();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        opcion = "3";
        cargarListaNumResIni();
        resolucionVisible = true;
        fechaVisible = false;
        predioVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaNumResIni()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoResIGACControladorUrlEnum.URL5808
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumResIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESOLUCION.getName());

    }

    public void cargarListaNumResfin()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoResIGACControladorUrlEnum.URL5240
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHA.getName(), fechaIniRes);

        listaNumResfin = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESOLUCION.getName());
    }

    public void cargarListaPredioInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoResIGACControladorUrlEnum.URL5242
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoResIGACControladorEnum.NUMERO_ORDEN_PREDIAL.getValue(),
                        nroOrdenConstante);

        listaPredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaPredioFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListadoResIGACControladorUrlEnum.URL5243
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoResIGACControladorEnum.NUMERO_ORDEN_PREDIAL.getValue(),
                        nroOrdenConstante);
        param.put(ListadoResIGACControladorEnum.CODIGO_INICIAL.getValue(),
                        predioInicial);

        listaPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = getReporte(reemplazar, parametros);

            if (SysmanFunciones.validarVariableVacio(opcion)
                || SysmanFunciones.validarVariableVacio(reporte))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB165"));
                return;
            }
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String getReporte(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros)
    {

        String fechaIni = "";
        String fechaFin = "";
        String reporte = "";

        try
        {
            fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
            fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);
        }
        catch (ParseException e)
        {
            Logger.getLogger(ListadoResIGACControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        StringBuilder titulo = new StringBuilder("");
        StringBuilder tituloFecha = new StringBuilder("");

        cargarTitulos(titulo, tituloFecha, fechaIni, fechaFin);

        parametros.put("PR_TITULO", titulo.toString());
        parametros.put("PR_TITULOFECHA", tituloFecha.toString());

        if ("3".equals(opcion))
        {
            reporte = gestionarOpcion3(reemplazar);
        }
        else if ("1".equals(opcion) || "2".equals(opcion))
        {
            reporte = gestionarOpcion1y2(reemplazar, fechaIni, fechaFin);
        }
        else if ("4".equals(opcion))
        {
            if (agregarMensaje(predioInicial, idioma.getString("TB_TB170"))
                || agregarMensaje(predioFinal, idioma.getString("TB_TB171")))
            {
                return null;
            }

            reemplazar.put("predioInicial", predioInicial);
            reemplazar.put("predioFinal", predioFinal);
            reporte = "000836PREDIALLISIGACRESOLUCIONESP";
        }

        return reporte;
    }

    private String gestionarOpcion1y2(HashMap<String, Object> reemplazar,
        String fechaIni, String fechaFin)
    {

        if (fechaInicial == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB168"));
            return null;
        }
        if (fechaFinal == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB169"));
            return null;
        }

        reemplazar.put("fechaInicial", fechaIni);
        reemplazar.put("fechaFinal", fechaFin);
        reemplazar.put("filtroFecha", "1".equals(opcion)
            ? ListadoResIGACControladorEnum.FECHAINGRESOSISTEMA.getValue()
            : "DATE_CREATED");
        return "000835PREDIALLISIGACRESOLUCIONES";

    }

    private String gestionarOpcion3(HashMap<String, Object> reemplazar)
    {

        if (agregarMensaje(resolucionInicial, idioma.getString("TB_TB166"))
            || agregarMensaje(resolucionFinal, idioma.getString("TB_TB167")))
        {
            return null;
        }

        reemplazar.put("fechaInicial", fechaIniRes);
        reemplazar.put("fechaFinal", fechaFinRes);
        reemplazar.put("resolucionInicial", resolucionInicial);
        reemplazar.put("resolucionFinal", resolucionFinal);

        return "000830PREDIALLISICACRESOLUCIONES2";
    }

    public boolean agregarMensaje(String campo, String mensaje)
    {
        if (SysmanFunciones.validarVariableVacio(campo))
        {
            JsfUtil.agregarMensajeAlerta(mensaje);
            return true;
        }
        return false;
    }

    private void cargarTitulos(StringBuilder titulo, StringBuilder tituloFecha,
        String fechaIni, String fechaFin)
    {

        switch (opcion)
        {
        case "1":
            titulo.append(idioma.getString("TB_TB3288"));
            tituloFecha.append(" ENTRE " + fechaIni + "  Y  " + fechaFin);
            break;
        case "2":
            titulo.append(idioma.getString("TB_TB3289"));
            tituloFecha.append(" ENTRE " + fechaIni + "  Y  " + fechaFin);
            break;
        case "3":
            cargarTituloFechas(titulo, tituloFecha);
            break;
        case "4":
            titulo.append(idioma.getString("TB_TB3291"));
            tituloFecha.append(" DEL PREDIO No " + predioInicial
                + "  AL PREDIO No "
                + predioFinal);
            break;
        default:
            break;

        }
    }

    private void cargarTituloFechas(StringBuilder titulo,
        StringBuilder tituloFecha)
    {
        titulo.append(idioma.getString("TB_TB3290"));
        tituloFecha.append(idioma.getString("TB_TB3286")
                        .replace("s$resolucionInicial$s", resolucionInicial)
                        .replace("s$fechaIniRes$s", fechaIniRes)
                        .replace("s$resolucionFinal$s", resolucionFinal)
                        .replace("s$fechaFinRes$s", fechaFinRes));
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarcmbopcion()
    {
        // <CODIGO_DESARROLLADO>
        if ("3".equals(opcion))
        {
            resolucionVisible = true;
            fechaVisible = false;
            predioVisible = false;
            cargarListaNumResIni();
        }
        else if ("1".equals(opcion) || "2".equals(opcion))
        {
            resolucionVisible = false;
            fechaVisible = true;
            predioVisible = false;
        }
        else
        {
            resolucionVisible = false;
            fechaVisible = false;
            predioVisible = true;
            cargarListaPredioInicial();
            cargarListaPredioFinal();
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaNumResIni(SelectEvent event)
    {
        resolucionFinal = null;
        Registro registroAux = (Registro) event.getObject();
        resolucionInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.RESOLUCION.getName())
                        .toString();
        fechaIniRes = registroAux.getCampos()
                        .get(ListadoResIGACControladorEnum.FECHAINGRESOSISTEMA
                                        .getValue())
                        .toString();
        cargarListaNumResfin();

    }

    public void seleccionarFilaNumResfin(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        resolucionFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESOLUCION
                                                        .getName()),
                                        "")
                        .toString();
        fechaFinRes = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(ListadoResIGACControladorEnum.FECHAINGRESOSISTEMA
                                                        .getValue()),
                        "")
                        .toString();
    }

    public void seleccionarFilaPredioInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        predioFinal = null;
        cargarListaPredioFinal();
    }

    public void seleccionarFilaPredioFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getResolucionInicial()
    {
        return resolucionInicial;
    }

    public void setResolucionInicial(String resolucionInicial)
    {
        this.resolucionInicial = resolucionInicial;
    }

    public String getResolucionFinal()
    {
        return resolucionFinal;
    }

    public void setResolucionFinal(String resolucionFinal)
    {
        this.resolucionFinal = resolucionFinal;
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
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

    public String getNroOrdenConstante()
    {
        return nroOrdenConstante;
    }

    public void setNroOrdenConstante(String nroOrdenConstante)
    {
        this.nroOrdenConstante = nroOrdenConstante;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isFechaVisible()
    {
        return fechaVisible;
    }

    public void setFechaVisible(boolean fechaVisible)
    {
        this.fechaVisible = fechaVisible;
    }

    public boolean isResolucionVisible()
    {
        return resolucionVisible;
    }

    public void setResolucionVisible(boolean resolucionVisible)
    {
        this.resolucionVisible = resolucionVisible;
    }

    public boolean isPredioVisible()
    {
        return predioVisible;
    }

    public void setPredioVisible(boolean predioVisible)
    {
        this.predioVisible = predioVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaNumResIni()
    {
        return listaNumResIni;
    }

    public void setListaNumResIni(RegistroDataModelImpl listaNumResIni)
    {
        this.listaNumResIni = listaNumResIni;
    }

    public RegistroDataModelImpl getListaNumResfin()
    {
        return listaNumResfin;
    }

    public void setListaNumResfin(RegistroDataModelImpl listaNumResfin)
    {
        this.listaNumResfin = listaNumResfin;
    }

    public RegistroDataModelImpl getListaPredioInicial()
    {
        return listaPredioInicial;
    }

    public void setListaPredioInicial(RegistroDataModelImpl listaPredioInicial)
    {
        this.listaPredioInicial = listaPredioInicial;
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
