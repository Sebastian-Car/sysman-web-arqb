package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisauxpptalgeneralControladorEnum;
import com.sysman.presupuesto.enums.LisauxpptalgeneralControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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
 * @author NGOMEZ
 * @version 1, 06/07/2016
 * 
 * @version 2, 19/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class LisauxpptalgeneralControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String asigInicial;
    private String asigFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String lugarInicial;
    private String lugarFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private int anio;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaAsiginicial;
    private RegistroDataModelImpl listaAsigfinal;
    private RegistroDataModelImpl listafuenteinicial;
    private RegistroDataModelImpl listafuentefinal;
    private RegistroDataModelImpl listalugarinicial;
    private RegistroDataModelImpl listalugarfinal;
    private static final String CODIGO = "CODIGO";
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LisauxpptalgeneralControlador
     */
    public LisauxpptalgeneralControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISAUXPPTALGENERAL_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(LisauxpptalgeneralControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);

        cargarListaTipoInicial();
        cargarListaTipoFinal();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        cargarListaAsiginicial();
        cargarListaAsigfinal();
        cargarListafuenteinicial();
        cargarListafuentefinal();
        cargarListalugarinicial();
        cargarListalugarfinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL4763.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListaTipoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL5254.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisauxpptalgeneralControladorEnum.PARAM0.getValue(), tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL5830.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL6441.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisauxpptalgeneralControladorEnum.PARAM1.getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListaTerceroInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL7142.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "NIT");

    }

    public void cargarListaTerceroFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL7597.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisauxpptalgeneralControladorEnum.PARAM2.getValue(), terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaAsiginicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL8125.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAsiginicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListaAsigfinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL8426.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisauxpptalgeneralControladorEnum.PARAM0.getValue(), asigInicial);

        listaAsigfinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListafuenteinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL9010.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listafuenteinicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListafuentefinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL3510.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LisauxpptalgeneralControladorEnum.PARAM0.getValue(), fuenteInicial);

        listafuentefinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListalugarinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL10192.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listalugarinicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    public void cargarListalugarfinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisauxpptalgeneralControladorUrlEnum.URL10352.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisauxpptalgeneralControladorEnum.PARAM0.getValue(), lugarInicial);

        listalugarfinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, CODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("asigInicial", asigInicial);
            reemplazar.put("asigFinal", asigFinal);
            reemplazar.put("lugarInicial", lugarInicial);
            reemplazar.put("lugarFinal", lugarFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LISAUXPPTALGENERAL_FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FORMS_LISAUXPPTALGENERAL_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            String reporte = "000980LisAuxPptalGeneral";
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(idioma.getString(MSM_TRANS_INTERRUMPIDA) + ex.getMessage());
            Logger.getLogger(LisauxpptalgeneralControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParseException e)
        {
            Logger.getLogger(LisauxpptalgeneralControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);

        cuentaInicial = null;
        cuentaFinal = null;
        fuenteInicial = null;
        fuenteFinal = null;

        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListafuenteinicial();
        cargarListafuentefinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ").toString();
        terceroFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ").toString();
    }

    public void seleccionarFilaAsiginicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        asigInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
        cargarListaAsigfinal();
    }

    public void seleccionarFilaAsigfinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        asigFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
    }

    public void seleccionarFilafuenteinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
        cargarListafuentefinal();
    }

    public void seleccionarFilafuentefinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
    }

    public void seleccionarFilalugarinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        lugarInicial = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
        cargarListalugarfinal();
    }

    public void seleccionarFilalugarfinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        lugarFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), " ").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoInicial()
    {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial)
    {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal()
    {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal)
    {
        this.tipoFinal = tipoFinal;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public String getTerceroInicial()
    {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial)
    {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal()
    {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal)
    {
        this.terceroFinal = terceroFinal;
    }

    public String getAsigInicial()
    {
        return asigInicial;
    }

    public void setAsigInicial(String asigInicial)
    {
        this.asigInicial = asigInicial;
    }

    public String getAsigFinal()
    {
        return asigFinal;
    }

    public void setAsigFinal(String asigFinal)
    {
        this.asigFinal = asigFinal;
    }

    public String getFuenteInicial()
    {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial)
    {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal()
    {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal)
    {
        this.fuenteFinal = fuenteFinal;
    }

    public String getLugarInicial()
    {
        return lugarInicial;
    }

    public void setLugarInicial(String lugarInicial)
    {
        this.lugarInicial = lugarInicial;
    }

    public String getLugarFinal()
    {
        return lugarFinal;
    }

    public void setLugarFinal(String lugarFinal)
    {
        this.lugarFinal = lugarFinal;
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

    public int getAnio()
    {
        return anio;
    }

    public void setAnio(int anio)
    {
        this.anio = anio;
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

    public RegistroDataModelImpl getListaTipoInicial()
    {
        return listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial()
    {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial)
    {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal()
    {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal)
    {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public RegistroDataModelImpl getListaAsiginicial()
    {
        return listaAsiginicial;
    }

    public void setListaAsiginicial(RegistroDataModelImpl listaAsiginicial)
    {
        this.listaAsiginicial = listaAsiginicial;
    }

    public RegistroDataModelImpl getListaAsigfinal()
    {
        return listaAsigfinal;
    }

    public void setListaAsigfinal(RegistroDataModelImpl listaAsigfinal)
    {
        this.listaAsigfinal = listaAsigfinal;
    }

    public RegistroDataModelImpl getListafuenteinicial()
    {
        return listafuenteinicial;
    }

    public void setListafuenteinicial(RegistroDataModelImpl listafuenteinicial)
    {
        this.listafuenteinicial = listafuenteinicial;
    }

    public RegistroDataModelImpl getListafuentefinal()
    {
        return listafuentefinal;
    }

    public void setListafuentefinal(RegistroDataModelImpl listafuentefinal)
    {
        this.listafuentefinal = listafuentefinal;
    }

    public RegistroDataModelImpl getListalugarinicial()
    {
        return listalugarinicial;
    }

    public void setListalugarinicial(RegistroDataModelImpl listalugarinicial)
    {
        this.listalugarinicial = listalugarinicial;
    }

    public RegistroDataModelImpl getListalugarfinal()
    {
        return listalugarfinal;
    }

    public void setListalugarfinal(RegistroDataModelImpl listalugarfinal)
    {
        this.listalugarfinal = listalugarfinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
