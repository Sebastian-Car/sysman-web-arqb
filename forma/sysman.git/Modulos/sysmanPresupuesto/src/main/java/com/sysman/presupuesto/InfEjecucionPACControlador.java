package com.sysman.presupuesto;

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
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.InfEjecucionPACControladorEnum;
import com.sysman.presupuesto.enums.InfEjecucionPACControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 16/06/2016
 *
 *
 * @author ybecerra
 * @version 2, 19/04/2017 Revision Sonar y Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class InfEjecucionPACControlador extends BeanBaseModal
{
    private final String compania;
    /**
     * Constante que identifica el nombre del campo CODIGO
     */
    private final String campoCodigo;
    // <DECLARAR_ATRIBUTOS>
    private boolean enMiles;
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial;
    private String centroFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String nmes1;
    private String nmes2;
    private String observaciones;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of InfEjecucionPACControlador
     */
    public InfEjecucionPACControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        campoCodigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INF_EJECUCION_PACCONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(InfEjecucionPACControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListaMesFinal();
        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)
            + 1];
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfEjecucionPACControladorUrlEnum.URL4539
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMesFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);

        try
        {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfEjecucionPACControladorUrlEnum.URL4961
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfEjecucionPACControladorUrlEnum.URL5436
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL5780
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL6756
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfEjecucionPACControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);

    }

    public void cargarListacentrocostoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL7829
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);

    }

    public void cargarListacentrocostoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL8510
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(InfEjecucionPACControladorEnum.CENTROINICIAL.getValue(),
                        centroInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);

    }

    public void cargarListaFuenteInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL9229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);

    }

    public void cargarListaFuenteFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfEjecucionPACControladorUrlEnum.URL9811
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(InfEjecucionPACControladorEnum.AUXILIARINICIAL.getValue(),
                        fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoCodigo);

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
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generaReporte(FORMATOS formato)
    {
        String reporte = "000915FCAGREJECUCIONPACC";
        try
        {

            if ((cuentaInicial == null) || cuentaInicial.isEmpty())
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB699"));
                return;
            }
            if (!validarVacios())
            {
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametro = new HashMap<>();

            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            reemplazar.put("centroCostoCond", evaluarCondicion("centroCosto"));
            reemplazar.put("fuenteCond", evaluarCondicion("auxiliar"));
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anio);
            reemplazar.put("miles", enMiles ? "-1" : "0");

            parametro = asignarValParamReporte();

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametro);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametro,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + ex.getMessage() + " " + reporte);
            Logger.getLogger(InfEjecucionPACControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex)
        {
            Logger.getLogger(InfEjecucionPACControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String evaluarCondicion(String valor)
    {
        String cadena = "";
        if (("centroCosto").equals(valor))
        {
            cadena = !(SysmanFunciones.validarVariableVacio(centroInicial)
                && SysmanFunciones.validarVariableVacio(centroFinal))
                    ? "  AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                        + centroInicial + "' AND '" + centroFinal + "' "
                    : " ";
        }
        if ("auxiliar".equals(valor))
        {
            cadena = !(SysmanFunciones.validarVariableVacio(fuenteInicial)
                && SysmanFunciones.validarVariableVacio(fuenteFinal))
                    ? "  AND PLAN_PRESUPUESTAL.AUXILIAR     BETWEEN '"
                        + fuenteInicial + "' AND '" + fuenteFinal + "' "
                    : " ";
        }
        return cadena;
    }

    private Map<String, Object> asignarValParamReporte()
    {
        Map<String, Object> parametros = new HashMap<>();
        String nombreRpteLegal = "";
        String firmaRpteLegal = "";
        try
        {
            nombreRpteLegal = Acciones.getParametro(ConectorPool.ESQUEMA_SYSMAN,
                            compania, "NOMBRE REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), "SYSDATE");
            firmaRpteLegal = Acciones.getParametro(ConectorPool.ESQUEMA_SYSMAN,
                            compania, "FIRMA REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), "SYSDATE");

            parametros.put("PR_FORMATO",
                            enMiles ? "#,#00;(#,#00)" : "#,#00.00;(#,#00.00)");
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES2", nmes2.toUpperCase());
            parametros.put("PR_NMES1", nmes1.toUpperCase());
            parametros.put("PR_MESINICIAL", mesInicial);
            parametros.put("PR_MESFINAL", mesFinal);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", nombreRpteLegal);
            parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL", firmaRpteLegal);
            parametros.put("PR_OBSERVACIONES", observaciones);
            parametros.put("PR_MILES",
                            enMiles ? "VALOR EN MILES DE PESOS" : "");
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
        }
        catch (NamingException | SQLException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametros;
    }

    private boolean validarVacios()
    {
        if (SysmanFunciones.validarVariableVacio(cuentaFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB700"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB703"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(mesFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB704"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(anio))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB705"));
            return false;
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        cargarListaMesFinal();
        mesFinal = nmes2 = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesFinal()
    {
        // <CODIGO_DESARROLLADO>
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesFinal)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = null;
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListacentrocostoInicial();
        cargarListacentrocostoFinal();
        cargarListaFuenteInicial();
        cargarListaFuenteFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
        cargarListacentrocostoFinal();
        centroFinal = null;
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
        cargarListaFuenteFinal();
        fuenteFinal = null;
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos().get(campoCodigo) == null ? ""
            : registroAux.getCampos().get(campoCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getEnMiles()
    {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles)
    {
        this.enMiles = enMiles;
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

    public String getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    public String getCentroInicial()
    {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial)
    {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal()
    {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal)
    {
        this.centroFinal = centroFinal;
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

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getNmes1()
    {
        return nmes1;
    }

    public void setNmes1(String nmes1)
    {
        this.nmes1 = nmes1;
    }

    public String getNmes2()
    {
        return nmes2;
    }

    public void setNmes2(String nmes2)
    {
        this.nmes2 = nmes2;
    }

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
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

    public RegistroDataModelImpl getListacentrocostoInicial()
    {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial)
    {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal()
    {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal)
    {
        this.listacentrocostoFinal = listacentrocostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial()
    {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial)
    {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal()
    {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal)
    {
        this.listaFuenteFinal = listaFuenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
