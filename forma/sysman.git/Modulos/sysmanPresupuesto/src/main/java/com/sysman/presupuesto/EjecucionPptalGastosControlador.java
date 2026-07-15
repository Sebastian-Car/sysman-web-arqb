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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionPptalGastosControladorEnum;
import com.sysman.presupuesto.enums.EjecucionPptalGastosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
 * @author jrodriguezr
 * @version 1, 20/06/2016
 * @author lcortes
 * @version 2, 07/12/2016 04:58:38 -- Modificado por lcortes --
 * Modificado por lcortes 18/04/2017 09:11. Ajustes Refactoring.
 */
@ManagedBean
@ViewScoped
public class EjecucionPptalGastosControlador extends BeanBaseModal
{
    private final String compania;
    private final String cod;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial;
    private String centroFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String observaciones;
    private String nmes1;
    private String nmes2;
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
    /**
     * variable que almacena el valor de la casilla de verificacion
     * indFuenteCuipo del formulario
     */
    private boolean indFuenteCuipo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

   

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EjecucionPptalGastosControlador
     */
    public EjecucionPptalGastosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cod = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_PPTAL_GASTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(EjecucionPptalGastosControlador.class.getName())
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
        InicializarCombos();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    public void InicializarCombos()
    {
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR925-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
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
                                                            EjecucionPptalGastosControladorUrlEnum.URL4686
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(EjecucionPptalGastosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
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
                                                            EjecucionPptalGastosControladorUrlEnum.URL5111
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(EjecucionPptalGastosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
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
                                                            EjecucionPptalGastosControladorUrlEnum.URL5600
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(EjecucionPptalGastosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL5941
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL6882
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(EjecucionPptalGastosControladorEnum.PARAM0.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListacentrocostoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL7993
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListacentrocostoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL8700
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaFuenteInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL9413
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
    }

    public void cargarListaFuenteFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionPptalGastosControladorUrlEnum.URL10036
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecucionPptalGastosControladorEnum.PARAM1.getValue(),
                        String.valueOf(fuenteInicial));
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cod);
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

    public void ejecutarlimpiarObservaciones()
    {
        // <CODIGO_DESARROLLADO>
        observaciones = "";
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarMesVacio()
    {
        if (SysmanFunciones.validarVariableVacio(mesInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB241"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(mesFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB242"));
            return false;
        }
        return true;
    }

    public boolean validarFuenteCentro()
    {
        if (SysmanFunciones.validarVariableVacio(fuenteInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB209"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(fuenteFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB210"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(centroInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB202"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(centroFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB205"));
            return false;
        }
        return true;
    }

    public boolean validarVacios()
    {
        if (SysmanFunciones.validarVariableVacio(anio))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB212"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB239"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB240"));
            return false;
        }

        return true;
    }

    private void generaReporte(FORMATOS formato)
    {
        if (!validarVacios() || !validarMesVacio()
            || !validarFuenteCentro())
        {
            return;
        }
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte;
            String salida ;
            
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesInicial-1", Integer.parseInt(mesInicial) - 1);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("anio", anio);

            String manejaAuxiliarPresupuesto = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                            SessionUtil.getModulo(), new Date(), true);
            if ((manejaAuxiliarPresupuesto != null)
                && "SI".equals(manejaAuxiliarPresupuesto))
            {
            	if(indFuenteCuipo) {
            		reporte = "002724FCrptEjePptalGtosAGRFuente";
            	}
            	else {
            		reporte = "000921FCrptEjePptalGtosAGRFuente";
            	}
               
            }
            else
            {
                reporte = "000925FCrptEjePptalGtosAGR";
            }
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            String nombreRpteLegal = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), true);
            String firmaRpteLegal = ejbSysmanUtil.consultarParametro(
                            compania, "FIRMA REPRESENTANTE LEGAL",
                            SessionUtil.getModulo(), new Date(), true);
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES2", nmes2.toUpperCase());
            parametros.put("PR_NMES1", nmes1.toUpperCase());
            parametros.put("PR_MESINICIAL", mesInicial);
            parametros.put("PR_MESFINAL", mesFinal);
            parametros.put("PR_NOMBRE_REPRESENTANTE_LEGAL", nombreRpteLegal);
            parametros.put("PR_FIRMA_REPRESENTANTE_LEGAL", firmaRpteLegal);
            parametros.put("PR_OBSERVACIONES", observaciones);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            if (formato.equals(ReportesBean.FORMATOS.EXCEL) || formato.equals(ReportesBean.FORMATOS.EXCEL97))
            {
                reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                                reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar));
                if(indFuenteCuipo) {
                	salida =  Reporteador.resuelveConsulta(
                            "800697FCrptEjePptalGtosAGRExcel",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
                	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida, ConectorPool.ESQUEMA_SYSMAN, formato,
                            "800697FCrptEjePptalGtosAGRExcel");
            	}
            	else {
            		salida = Reporteador.resuelveConsulta(
                            "000925FCrptEjePptalGtosAGRExcel",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(salida, ConectorPool.ESQUEMA_SYSMAN, formato,
                            "000925FCrptEjePptalGtosAGRExcel");
            	}

                
            }
            else
            {

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (JRException | IOException | SysmanException
                        | SystemException | SQLException | DRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        cargarListaMesFinal();
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
        cargarListaMesInicial();
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        InicializarCombos();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos().get(cod).toString();
        cargarListacentrocostoFinal();
        centroFinal = null;
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos().get(cod).toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos().get(cod).toString();
        cargarListaFuenteFinal();
        fuenteFinal = null;
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos().get(cod).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
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

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
    public boolean getIndFuenteCuipo() {
		return indFuenteCuipo;
	}

	public void setIndFuenteCuipo(boolean indFuenteCuipo) {
		this.indFuenteCuipo = indFuenteCuipo;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
