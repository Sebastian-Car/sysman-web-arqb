package com.sysman.almacen;

import com.sysman.almacen.enums.VidaUtilPlacasControladorEnum;
import com.sysman.almacen.enums.VidaUtilPlacasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 04/02/2016
 * @version 2, 09/05/2017 jrodriguezr Se refactoriza el codigo SQL de las listas para utilizar dss. Tambien los llamados a funciones, procedimientos y metodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 *
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 *
 * @author asana, se envía el parámetro compania en la lista cargarListaMiCompania para que se muestre en combo compañía solo desde la compañía desde la que se inicia sesión
 * @version 4, 22/08/2018
 */
@ManagedBean
@ViewScoped
public class VidaUtilPlacasControlador extends BeanBaseContinuoAcmeImpl
{

    private String compania;
    /**
     * Constante definida para almacenar la cadena "DEVOLUTIVO"
     */
    /**
     * variable de tipo cadena auxiliar
     */
    private String auxiliar;

    private final String tablaDevolutivo;
    /**
     * Constante definida para almacenar la cadena "MSM_TRANS_INTERRUMPIDA"
     */
    private String miCompania;
    private StreamedContent archivoDescarga;
    private Date fechaCorte;
    private List<Registro> listaMiCompania;
    private List<Registro> listaNiifTipoActivo;
    private RegistroDataModelImpl listafuenteDeterioroExtE;
    private RegistroDataModelImpl listafuenteDeterioroIntE;

    private String manejaNIIFenAlmacen;
    private String fechaCorteNiif;
    private boolean calcularvlrbaseVisible;
    private boolean excelniifVisible;
    private boolean niifValorTotalVisible;
    private boolean salvamentoVisible;
    private boolean deterioroVisible;
    private boolean niifValorBaseVisible;
    private boolean niifVidaUtilVisible;
    private boolean niifFechafuncionamientoVisible;
    private boolean niifTipoActivoVisible;
    private boolean aplicaniifVisible;
    private boolean residualVisible;
    private boolean valorNiifVisible;
    private boolean fechacorteVisible;
    private boolean consultarVisible;
    private boolean lblfechacorteVisible;
    private String condicionReporte;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of VidaUtilPlacasControlador
     */
    public VidaUtilPlacasControlador()
    {
        super();
        condicionReporte = "";
        compania = SessionUtil.getCompania();
        tablaDevolutivo = "DEVOLUTIVO";
        try
        {
            fechaCorte = new Date();
            numFormulario = GeneralCodigoFormaEnum.VIDA_UTIL_PLACAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException e)
        {
            SessionUtil.redireccionarMenuPermisos();
            logger.error(e.getMessage(), e);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        try
        {
            fechaCorteNiif = ejbSysmanUtil.consultarParametro(
                            compania,
                            "FECHA DE CORTE PARA INICIO EN NIIF DEL ALMACEN",
                            SessionUtil.getModulo(),
                            new Date(), true);
            manejaNIIFenAlmacen = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA NIIF EN ALMACEN", SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        fechaCorte = new Date();
        reasignarOrigen();
        tabla = tablaDevolutivo;
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaMiCompania();
        cargarListaNiifTipoActivo();
        cargarListaFDeterioroExt();
        cargarListaFDeterioroInt();
        abrirFormulario();
    }

    public List<Registro> getListaMiCompania()
    {
        return listaMiCompania;
    }

    public void setListaMiCompania(List<Registro> listaMiCompania)
    {
        this.listaMiCompania = listaMiCompania;
    }

    public void seleccionarFilafuenteDeterioroExtE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        // registro.getCampos().put("FDETERIOROEXTERNA", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

    }

    public void seleccionarFilafuenteDeterioroIntE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        // registro.getCampos().put("FDETERIOROINTERNA", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

    }

    /**
     * Retorna la lista listaNiifTipoActivo
     *
     * @return listaNiifTipoActivo
     */
    public List<Registro> getListaNiifTipoActivo()
    {
        return listaNiifTipoActivo;
    }

    /**
     * Asigna la lista listaNiifTipoActivo
     *
     * @param listaNiifTipoActivo
     * Variable a asignar en listaNiifTipoActivo
     */
    public void setListaNiifTipoActivo(List<Registro> listaNiifTipoActivo)
    {
        this.listaNiifTipoActivo = listaNiifTipoActivo;
    }

    public String getMiCompania()
    {
        return miCompania;
    }

    public void setMiCompania(String miCompania)
    {
        this.miCompania = miCompania;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public Date getFechaCorte()
    {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte)
    {
        this.fechaCorte = fechaCorte;
    }

    public String getFechaCorteNiif()
    {
        return fechaCorteNiif;
    }

    public void setFechaCorteNiif(String fechaCorteNiif)
    {
        this.fechaCorteNiif = fechaCorteNiif;
    }

    public boolean isCalcularvlrbaseVisible()
    {
        return calcularvlrbaseVisible;
    }

    public void setCalcularvlrbaseVisible(boolean calcularvlrbaseVisible)
    {
        this.calcularvlrbaseVisible = calcularvlrbaseVisible;
    }

    public boolean isExcelniifVisible()
    {
        return excelniifVisible;
    }

    public void setExcelniifVisible(boolean excelniifVisible)
    {
        this.excelniifVisible = excelniifVisible;
    }

    public boolean isNiifValorTotalVisible()
    {
        return niifValorTotalVisible;
    }

    public void setNiifValorTotalVisible(boolean niifValorTotalVisible)
    {
        this.niifValorTotalVisible = niifValorTotalVisible;
    }

    public boolean isSalvamentoVisible()
    {
        return salvamentoVisible;
    }

    public void setSalvamentoVisible(boolean salvamentoVisible)
    {
        this.salvamentoVisible = salvamentoVisible;
    }

    public boolean isDeterioroVisible()
    {
        return deterioroVisible;
    }

    public void setDeterioroVisible(boolean deterioroVisible)
    {
        this.deterioroVisible = deterioroVisible;
    }

    public boolean isNiifValorBaseVisible()
    {
        return niifValorBaseVisible;
    }

    public void setNiifValorBaseVisible(boolean niifValorBaseVisible)
    {
        this.niifValorBaseVisible = niifValorBaseVisible;
    }

    public boolean isNiifVidaUtilVisible()
    {
        return niifVidaUtilVisible;
    }

    public void setNiifVidaUtilVisible(boolean niifVidaUtilVisible)
    {
        this.niifVidaUtilVisible = niifVidaUtilVisible;
    }

    public boolean isNiifFechafuncionamientoVisible()
    {
        return niifFechafuncionamientoVisible;
    }

    public void setNiifFechafuncionamientoVisible(
        boolean niifFechafuncionamientoVisible)
    {
        this.niifFechafuncionamientoVisible = niifFechafuncionamientoVisible;
    }

    public boolean isNiifTipoActivoVisible()
    {
        return niifTipoActivoVisible;
    }

    public void setNiifTipoActivoVisible(boolean niifTipoActivoVisible)
    {
        this.niifTipoActivoVisible = niifTipoActivoVisible;
    }

    public boolean isAplicaniifVisible()
    {
        return aplicaniifVisible;
    }

    public void setAplicaniifVisible(boolean aplicaniifVisible)
    {
        this.aplicaniifVisible = aplicaniifVisible;
    }

    public boolean isResidualVisible()
    {
        return residualVisible;
    }

    public void setResidualVisible(boolean residualVisible)
    {
        this.residualVisible = residualVisible;
    }

    public boolean isValorNiifVisible()
    {
        return valorNiifVisible;
    }

    public void setValorNiifVisible(boolean valorNiifVisible)
    {
        this.valorNiifVisible = valorNiifVisible;
    }

    public boolean isFechacorteVisible()
    {
        return fechacorteVisible;
    }

    public void setFechacorteVisible(boolean fechacorteVisible)
    {
        this.fechacorteVisible = fechacorteVisible;
    }

    public boolean isConsultarVisible()
    {
        return consultarVisible;
    }

    public void setConsultarVisible(boolean consultarVisible)
    {
        this.consultarVisible = consultarVisible;
    }

    public boolean isLblfechacorteVisible()
    {
        return lblfechacorteVisible;
    }

    public void setLblfechacorteVisible(boolean lblfechacorteVisible)
    {
        this.lblfechacorteVisible = lblfechacorteVisible;
    }

    public void cargarListaMiCompania()
    {
        try
        {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaMiCompania = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            VidaUtilPlacasControladorUrlEnum.URL9526
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNiifTipoActivo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaNiifTipoActivo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            VidaUtilPlacasControladorUrlEnum.URL10009
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFDeterioroExt()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), "E");

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(VidaUtilPlacasControladorUrlEnum.URL11325.getValue());
        listafuenteDeterioroExtE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFDeterioroInt()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), "I");

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(VidaUtilPlacasControladorUrlEnum.URL11325.getValue());

        listafuenteDeterioroIntE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirConsultar()
    {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMiCompania()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(miCompania))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
            miCompania = compania;
            return;
        }
        compania = miCompania;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioExcelNIIF(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcelNIIF()
    {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModalDatos("505", SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCalcularVlrBase()
    {
        // <CODIGO_DESARROLLADO>
        int num = 0;
        try
        {
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(VidaUtilPlacasControladorEnum.FECHACORTE.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaCorte,
                                            "dd/MM/yyyy HH:mm:ss"));
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            VidaUtilPlacasControladorUrlEnum.URL27810
                                                            .getValue());
            num = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("TB_TB1899").replace("#num#",
                                        String.valueOf(num)));
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1898"));
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirreexpresar(Registro reg, int indice)
    {
        String[] campos = { "elemento", "serie" };
        Object[] valores = { reg.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()).toString(),
                             String.valueOf(reg.getCampos().get(GeneralParameterEnum.SERIE.getName())) };
        SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(GeneralCodigoFormaEnum.FRM_REEXPRESARVIDAUTIL_CONTROLADOR.getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

    }

    public String listaInicialEle()
    {
        String condicion = "";
        if (SysmanFunciones.esBdSqlServer())
        {
            if (listaInicial.getFilters()
                            .get("campos['ELEMENTO']") != null)
            {
                condicion = " AND DEVOLUTIVO.ELEMENTO LIKE ('"
                    + listaInicial.getFilters().get("campos['ELEMENTO']")
                    + "%') ";
            }
        }
        else
        {

            if (listaInicial.getFilters()
                            .get("campos['ELEMENTO']") != null)
            {
                condicion = " AND TO_CHAR(DEVOLUTIVO.ELEMENTO) LIKE ('"
                    + listaInicial.getFilters().get("campos['ELEMENTO']")
                    + "%') ";
            }
        }
        return condicion;
    }

    public String listaInicialSer()
    {
        String condicion = "";
        if (listaInicial.getFilters().get("campos['SERIE']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.SERIE) LIKE ('"
                + listaInicial.getFilters().get("campos['SERIE']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialVal()
    {
        String condicion = "";
        if (listaInicial.getFilters().get("campos['VALOR']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.VALOR) LIKE ('"
                + listaInicial.getFilters().get("campos['VALOR']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialFechaA()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['FECHAADQUISICION']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.FECHAADQUISICION,'DD/MM/YYYY') LIKE ('%"
                + listaInicial.getFilters().get(
                                "campos['FECHAADQUISICION']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialFechaS()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['FECHASALIDASERVICIO']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.FECHASALIDASERVICIO,'DD/MM/YYYY') LIKE ('%"
                + listaInicial.getFilters().get(
                                "campos['FECHASALIDASERVICIO']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialTipoM()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['TIPOMOVIMIENTOI']") != null)
        {
            condicion = " AND UPPER(DEVOLUTIVO.TIPOMOVIMIENTOI) LIKE UPPER('"
                + listaInicial.getFilters().get(
                                "campos['TIPOMOVIMIENTOI']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialMovimiento()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['MOVIMIENTOI']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.MOVIMIENTOI) LIKE ('"
                + listaInicial.getFilters().get("campos['MOVIMIENTOI']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialMesesVidaUtil()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['MESESVIDAUTILPLACA']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.MESESVIDAUTILPLACA) LIKE ('"
                + listaInicial.getFilters().get(
                                "campos['MESESVIDAUTILPLACA']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialNiifValorBase()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['NIIF_VALOR_BASE']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.NIIF_VALOR_BASE) LIKE ('"
                + listaInicial.getFilters()
                                .get("campos['NIIF_VALOR_BASE']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialSalvamento()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['SALVAMENTO']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.SALVAMENTO) LIKE ('"
                + listaInicial.getFilters().get("campos['SALVAMENTO']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialDeterioro()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['DETERIORO']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.DETERIORO) LIKE ('"
                + listaInicial.getFilters().get("campos['DETERIORO']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialNiifVidaUtil()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['NIIF_VIDA_UTIL']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.NIIF_VIDA_UTIL) LIKE ('"
                + listaInicial.getFilters()
                                .get("campos['NIIF_VIDA_UTIL']")
                + "%') ";
        }
        return condicion;
    }

    public String listaInicialNiifTipo()
    {
        String condicion = "";
        if (listaInicial.getFilters()
                        .get("campos['NIIF_TIPO_ACTIVO']") != null)
        {
            condicion = " AND TO_CHAR(DEVOLUTIVO.NIIF_TIPO_ACTIVO) LIKE ('"
                + listaInicial.getFilters().get(
                                "campos['NIIF_TIPO_ACTIVO']")
                + "%') ";
        }
        return condicion;
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        String strReporte;
        archivoDescarga = null;
        try
        {
            if (!listaInicial.getFilters().isEmpty())
            {
                condicionReporte += listaInicialEle();
                condicionReporte += listaInicialSer();
                condicionReporte += listaInicialVal();
                condicionReporte += listaInicialFechaA();
                condicionReporte += listaInicialFechaS();
                condicionReporte += listaInicialTipoM();
                condicionReporte += listaInicialMovimiento();
                condicionReporte += listaInicialMesesVidaUtil();
                condicionReporte += listaInicialNiifValorBase();
                condicionReporte += listaInicialSalvamento();
                condicionReporte += listaInicialDeterioro();
                condicionReporte += listaInicialNiifVidaUtil();
                condicionReporte += listaInicialNiifTipo();
                if (listaInicial.getFilters()
                                .get("campos['NIIF_VALOR_TOTAL']") != null)
                {
                    condicionReporte += " AND TO_CHAR(DEVOLUTIVO.NIIF_VALOR_TOTAL) LIKE ('"
                        + listaInicial.getFilters().get(
                                        "campos['NIIF_VALOR_TOTAL']")
                        + "%') ";
                }
                if (listaInicial.getFilters().get(
                                "campos['NIIF_FECHAFUNCIONAMIENTO']") != null)
                {
                    condicionReporte += " AND TO_CHAR(DEVOLUTIVO.NIIF_FECHAFUNCIONAMIENTO,'DD/MM/YYYY') LIKE ('%"
                        + listaInicial.getFilters()
                                        .get("campos['NIIF_FECHAFUNCIONAMIENTO']")
                        + "%') ";
                }
                if (listaInicial.getFilters()
                                .get("campos['APLICA_NIIF']") != null)
                {
                    condicionReporte += " AND DEVOLUTIVO.APLICA_NIIF = "
                        + ("TRUE".equalsIgnoreCase(listaInicial.getFilters()
                                        .get("campos['APLICA_NIIF']")
                                        .toString()) ? "-1" : "0")
                        + " ";
                }
                if (listaInicial.getFilters()
                                .get("campos['DEPACUMULADA']") != null)
                {
                    condicionReporte += " AND TO_CHAR(DEVOLUTIVO.DEPACUMULADA) LIKE ('"
                        + listaInicial.getFilters()
                                        .get("campos['DEPACUMULADA']")
                        + "%') ";
                }
                if (listaInicial.getFilters()
                                .get("campos['VLRLIBROS']") != null)
                {
                    condicionReporte += " AND TO_CHAR(DEVOLUTIVO.VLRLIBROS) LIKE ('"
                        + listaInicial.getFilters().get("campos['VLRLIBROS']")
                        + "%') ";
                }
                if (listaInicial.getFilters()
                                .get("campos['NOMBRELARGO']") != null)
                {
                    condicionReporte += " AND UPPER(INVENTARIO.NOMBRELARGO) LIKE UPPER('%"
                        + listaInicial.getFilters().get("campos['NOMBRELARGO']")
                        + "%') ";
                }
            }
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("fechaCorte",
                            SysmanFunciones.formatearFecha(fechaCorte));
            reemplazos.put("fechaCorteNiif", fechaCorteNiif);
            reemplazos.put("condicionReporte", condicionReporte);
            condicionReporte = "";
            strReporte = Reporteador
                            .resuelveConsulta("800039DevolutivoVidaUtil",
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos);
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strReporte,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (JRException | IOException | DRException | SysmanException
                        | SQLException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarMesesVidaUtilPlacaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (!listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get("MESESVIDAUTILPLACA").toString().isEmpty()
            && "0".equals(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .get("MESESVIDAUTILPLACA")))
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1900"));

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarSALVAMENTOC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        String valorTotalNiif = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("NIIF_VALOR_TOTAL").toString();
        double vlrTotalNiif = valorTotalNiif == null ? 0
            : Double.parseDouble(valorTotalNiif);
        String deterioro = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("DETERIORO").toString();
        double vlrDeterioro = deterioro == null ? 0
            : Double.parseDouble(deterioro);
        String salvamento = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("SALVAMENTO").toString();
        double vlrSalvamento = salvamento == null ? 0
            : Double.parseDouble(salvamento);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "NIIF_VALOR_BASE",
                        vlrTotalNiif - vlrDeterioro - vlrSalvamento);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDETERIOROC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        String valorTotalNiif = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("NIIF_VALOR_TOTAL").toString();
        double vlrTotalNiif = valorTotalNiif == null ? 0
            : Double.parseDouble(valorTotalNiif);
        String deterioro = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("DETERIORO").toString();
        double vlrDeterioro = deterioro == null ? 0
            : Double.parseDouble(deterioro);
        String salvamento = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("SALVAMENTO").toString();
        double vlrSalvamento = salvamento == null ? 0
            : Double.parseDouble(salvamento);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "NIIF_VALOR_BASE",
                        vlrTotalNiif - vlrDeterioro - vlrSalvamento);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("SERIE");
        registro.getCampos().remove("ELEMENTO");
        registro.getCampos().remove("TIPOMOVIMIENTOI");
        registro.getCampos().remove("MOVIMIENTOI");
        registro.getCampos().remove("NOMBRELARGO");
        registro.getCampos().remove("FECHASALIDASERVICIO");
        registro.getCampos().remove("VALOR");
        registro.getCampos().remove("FECHAADQUISICION");
        registro.getCampos().remove("VLRVALORIZACION");
        registro.getCampos().remove("MESESVIDAUTREST");
        registro.getCampos().remove("NDETERIOROINTERNA");
        registro.getCampos().remove("NDETERIOROEXTERNA");
        registro.getCampos().remove("DEPACUMULADA");
        registro.getCampos().remove("VLRLIBROS");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen()
    {
        try
        {
            miCompania = compania;
            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            miCompania);
            parametrosListado.put(
                            VidaUtilPlacasControladorEnum.FECHACORTE
                                            .getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaCorte,
                                            "dd/MM/yyyy"));

            if ("SI".equals(manejaNIIFenAlmacen))
            {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                VidaUtilPlacasControladorUrlEnum.URL11324
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                fields.put(VidaUtilPlacasControladorEnum.FECHACORTE.getValue(),
                                SysmanFunciones.convertirAFecha(fechaCorteNiif,
                                                "dd/MM/yyyy"));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);

                parametrosListado.put("FECHACORTENIIF",
                                SysmanFunciones.convertirAFechaCadena(
                                                SysmanFunciones.convertirAFecha(
                                                                fechaCorteNiif),
                                                "dd/MM/yyyy"));

                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                VidaUtilPlacasControladorUrlEnum.URL0001
                                                                .getValue());
                urlActualizacion = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                VidaUtilPlacasControladorUrlEnum.URL0003
                                                                .getValue());
                calcularvlrbaseVisible = true;
                excelniifVisible = true;
                niifValorTotalVisible = true;
                salvamentoVisible = true;
                deterioroVisible = true;
                niifValorBaseVisible = true;
                niifVidaUtilVisible = true;
                niifFechafuncionamientoVisible = true;
                niifTipoActivoVisible = true;
                aplicaniifVisible = true;
                valorNiifVisible = true;
                fechacorteVisible = true;
                consultarVisible = true;
                lblfechacorteVisible = true;
            }
            else
            {
                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                VidaUtilPlacasControladorUrlEnum.URL0002
                                                                .getValue());
            }
        }
        catch (ParseException | SystemException e)
        {
            e.printStackTrace();
        }
    }

    public RegistroDataModelImpl getListafuenteDeterioroExtE()
    {
        return listafuenteDeterioroExtE;
    }

    public void setListafuenteDeterioroExtE(
        RegistroDataModelImpl listafuenteDeterioroExtE)
    {
        this.listafuenteDeterioroExtE = listafuenteDeterioroExtE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListafuenteDeterioroIntE()
    {
        return listafuenteDeterioroIntE;
    }

    public void setListafuenteDeterioroIntE(
        RegistroDataModelImpl listafuenteDeterioroIntE)
    {
        this.listafuenteDeterioroIntE = listafuenteDeterioroIntE;
    }

}
