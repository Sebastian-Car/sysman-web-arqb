package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisauxpptalcuentasControladorEnum;
import com.sysman.presupuesto.enums.LisauxpptalcuentasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
 * @author eamaya
 * @version 2, 18/04/2017 Proceso de Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class LisauxpptalcuentasControlador extends BeanBaseModal
{
    private final String compania;
    private final String falseCons;
    private final String codigoCons;

    // <DECLARAR_ATRIBUTOS>

    private String tipoCuenta;
    private String especial;
    private String discriminado;
    private String afectaciones;
    private String clasecpte = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String textoInicio = "Inicio";
    private String textoFinal = "Final";
    private Date fechaInicial;
    private Date fechaFinal;
    private int anio;
    private String terceroInicialNom;
    private String terceroFinalNom;
    private String centroInicialNom;
    private String centroFinalNom;
    private String fuenteInicialNom;
    private String fuenteFinalNom;
    private String urlEnum;
    private boolean idPredVisible;
    private boolean formatoEspecial;
    private boolean nombrePredVisible;
    private boolean especialExcel;
    private boolean clasecpteview;

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
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listaCentrocostofinal;
    private RegistroDataModelImpl listafuenteInicial;
    private RegistroDataModelImpl listafuenteFinal;
    private RegistroDataModelImpl listaClasecptecb;

    private RegistroDataModelImpl listaReferenciaInicial;

    private RegistroDataModelImpl listaReferenciaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of LisauxpptalcuentasControlador
     */
    public LisauxpptalcuentasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        falseCons = "false";
        codigoCons = "CODIGO";
        idPredVisible = true;
        nombrePredVisible = true;

        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISAUXPPTALCUENTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        tipoCuenta = "1";

        cargarListaClasecptecb();

        cargarListaTipoInicial();
        // cargarListaTipoFinal()
        cargarListaCuentaInicial();
        // cargarListaCuentaFinal()
        cargarListaTerceroInicial();
        // cargarListaTerceroFinal()
        cargarListacentrocostoInicial();
        // cargarListaCentrocostofinal()
        cargarListafuenteInicial();
        cargarListafuenteFinal();
        cargarListaReferenciaInicial();
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
    public void cargarListaClasecptecb()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL5451
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaClasecptecb = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaTipoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if (clasecpteview)
        {

            param.put(GeneralParameterEnum.CLASE.getName(),
                            clasecpte);

            urlEnum = LisauxpptalcuentasControladorUrlEnum.URL5452
                            .getValue();

        }
        else
        {

            urlEnum = LisauxpptalcuentasControladorUrlEnum.URL4857
                            .getValue();

        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
        cargarListaTipoFinal();
    }

    public void cargarListaTipoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LisauxpptalcuentasControladorEnum.PARAM2.getValue(),
                        tipoInicial);

        if (clasecpteview)
        {

            param.put(GeneralParameterEnum.CLASE.getName(),
                            clasecpte);

            urlEnum = LisauxpptalcuentasControladorUrlEnum.URL5453
                            .getValue();

        }
        else
        {

            urlEnum = LisauxpptalcuentasControladorUrlEnum.URL5448
                            .getValue();

        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL6157
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL6874
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(LisauxpptalcuentasControladorEnum.PARAM7.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaTerceroInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL7713
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL8232
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(LisauxpptalcuentasControladorEnum.PARAM10.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListacentrocostoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL8835
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCentrocostofinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL9520
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(LisauxpptalcuentasControladorEnum.PARAM15.getValue(),
                        centroInicial);

        listaCentrocostofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListafuenteInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL5449
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listafuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * Carga la lista listafuenteFinal
     */
    public void cargarListafuenteFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL5450
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LisauxpptalcuentasControladorEnum.PARAM2.getValue(),
                        String.valueOf(fuenteInicial));
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listafuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaReferenciaInicial
     *
     */
    public void cargarListaReferenciaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL5584
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaReferenciaFinal
     *
     */
    public void cargarListaReferenciaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisauxpptalcuentasControladorUrlEnum.URL5585
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("REFERENCIAINICIAL", referenciaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
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

    private String definir(String texto, boolean esInicio)
    {

        if (esInicio)
        {
            return SysmanConstantes.DEFECTOINICIAL_STRING.equals(texto)
                || texto == null
                    ? textoInicio
                    : texto;
        }
        else
        {
            return SysmanConstantes.DEFECTOFINAL_STRING.equals(texto)
                || texto == null
                    ? textoFinal
                    : texto;
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            boolean tipoNumero = "SI"
                            .equals(SysmanFunciones.nvl(
                                            ejbParametro.consultarParametro(
                                                            compania,
                                                            "MANEJA TIPO Y NRO DE DOCUMENTO EN AUXILIAR",
                                                            SessionUtil.getModulo(),
                                                            new Date(), false),
                                            "NO"));
            boolean manejaAuxiliar = "SI".equals(SysmanFunciones
                            .nvl(ejbParametro.consultarParametro(compania,
                                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO"));

            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoCuenta", tipoCuenta);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);

            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);

            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);

            reemplazar.put("anio", anio);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_CUENTAINICIAL",
                            definir(cuentaInicial, true));
            parametros.put("PR_CUENTAFINAL",
                            definir(cuentaFinal, false));
            parametros.put("PR_TERCEROINICIAL",
                            definir(terceroInicialNom, true));
            parametros.put("PR_TERCEROFINAL",
                            definir(terceroFinalNom, false));
            parametros.put("PR_CENTROCOSTOINICIAL",
                            definir(centroInicialNom, true));
            parametros.put("PR_CENTROCOSTOFINAL",
                            definir(centroFinalNom, false));
            parametros.put("PR_FUENTEINICIAL",
                            definir(fuenteInicialNom, true));
            parametros.put("PR_FUENTEFINAL",
                            definir(fuenteFinalNom, false));

            parametros.put("PR_TIPO_NUMERO", tipoNumero);

            parametros.put("PR_VERFUENTE", manejaAuxiliar);

            if ("true".equals(especial))
            {

                parametros.put("PR_IDPREDVISIBLE", false);

            }
            else
            {
                parametros.put("PR_IDPREDVISIBLE", true);
            }

            String reporteTabla = null;

            if (formatoEspecial)
            {

                reporteTabla = "002036LisAuxPptalCuentasFZAGZEspecial";

            }
            else if (especialExcel)
            {

                // INI 7716847_PRESUPUESTO(12/10/2022 MROSERO)
                if ("SI".equals(SysmanFunciones
                                .nvl(ejbParametro.consultarParametro(compania,
                                                "MANEJA REFERENCIA Y DEPENDENCIA PARA INFORMES",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "NO")))
                {

                    reporteTabla = "002397EspecialLisAuxPptalCuentasFZAGZ";

                }
                else
                {

                    reporteTabla = "000984EspecialLisAuxPptalCuentasFZAGZv1";
                }

            }
            else
            {

                reporteTabla = "000984LisAuxPptalCuentasFZAGZ";

            }

            String strSqlSub = Reporteador.resuelveConsulta(reporteTabla,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);
            parametros.put("PR_STRSQLDETALLE", strSqlSub);
            String reporte = "800172Compania";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporteTabla, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);

        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroInicialNom = definir(null, true);
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroFinalNom = definir(null, false);
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListacentrocostoInicial();
        cargarListaCentrocostofinal();
        cargarListafuenteInicial();
        cargarListafuenteFinal();
        cargarListaReferenciaInicial();
        cargarListaReferenciaFinal();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event)
    {
        tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();

        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        terceroInicialNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), " ")
                        .toString();
        terceroFinalNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
        centroInicialNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
        centroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        cargarListaCentrocostofinal();
    }

    public void seleccionarFilaCentrocostofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
        centroFinalNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listafuenteInicial objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListafuenteFinal();
        fuenteInicialNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listafuenteFinalobjeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafuenteFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        fuenteFinalNom = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        " ")
                        .toString();

    }

    public void seleccionarFilaClasecptecb(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        clasecpte = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), " ")
                        .toString();
        cargarListaTipoInicial();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();

        cargarListaReferenciaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaReferenciaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    public void cambiarFormatoEspecial()
    {

        if (formatoEspecial)
        {
            especialExcel = false;

        }
    }

    public void cambiarEspecialExcel()
    {

        if (especialExcel)
        {
            formatoEspecial = false;

        }
    }

    public void cambiarPorclasecpte()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaClasecptecb();
        cargarListaTipoInicial();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoCuenta()
    {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta)
    {
        this.tipoCuenta = tipoCuenta;
    }

    public String getEspecial()
    {
        return especial;
    }

    public void setEspecial(String especial)
    {
        this.especial = especial;
    }

    public String getDiscriminado()
    {
        return discriminado;
    }

    public void setDiscriminado(String discriminado)
    {
        this.discriminado = discriminado;
    }

    public String getAfectaciones()
    {
        return afectaciones;
    }

    public void setAfectaciones(String afectaciones)
    {
        this.afectaciones = afectaciones;
    }

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

    public String getTerceroInicialNom()
    {
        return terceroInicialNom;
    }

    public void setTerceroInicialNom(String terceroInicialNom)
    {
        this.terceroInicialNom = terceroInicialNom;
    }

    public String getTerceroFinalNom()
    {
        return terceroFinalNom;
    }

    public void setTerceroFinalNom(String terceroFinalNom)
    {
        this.terceroFinalNom = terceroFinalNom;
    }

    public String getCentroInicialNom()
    {
        return centroInicialNom;
    }

    public void setCentroInicialNom(String centroInicialNom)
    {
        this.centroInicialNom = centroInicialNom;
    }

    public String getCentroFinalNom()
    {
        return centroFinalNom;
    }

    public void setCentroFinalNom(String centroFinalNom)
    {
        this.centroFinalNom = centroFinalNom;
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

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
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

    public RegistroDataModelImpl getListaTerceroInicial()
    {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial)
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

    public RegistroDataModelImpl getListacentrocostoInicial()
    {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial)
    {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListaCentrocostofinal()
    {
        return listaCentrocostofinal;
    }

    public void setListaCentrocostofinal(
        RegistroDataModelImpl listaCentrocostofinal)
    {
        this.listaCentrocostofinal = listaCentrocostofinal;
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

    public RegistroDataModelImpl getListafuenteInicial()
    {
        return listafuenteInicial;
    }

    public void setListafuenteInicial(
        RegistroDataModelImpl listafuenteInicial)
    {
        this.listafuenteInicial = listafuenteInicial;
    }

    public RegistroDataModelImpl getListafuenteFinal()
    {
        return listafuenteFinal;
    }

    public void setListafuenteFinal(RegistroDataModelImpl listafuenteFinal)
    {
        this.listafuenteFinal = listafuenteFinal;
    }

    public boolean isNombrePredVisible()
    {
        return nombrePredVisible;
    }

    public void setNombrePredVisible(boolean nombrePredVisible)
    {
        this.nombrePredVisible = nombrePredVisible;
    }

    public boolean isIdPredVisible()
    {
        return idPredVisible;
    }

    public void setIdPredVisible(boolean idPredVisible)
    {
        this.idPredVisible = idPredVisible;
    }

    public boolean isFormatoEspecial()
    {
        return formatoEspecial;
    }

    public void setFormatoEspecial(boolean formatoEspecial)
    {
        this.formatoEspecial = formatoEspecial;
    }

    public boolean isEspecialExcel()
    {
        return especialExcel;
    }

    public void setEspecialExcel(boolean especialExcel)
    {
        this.especialExcel = especialExcel;
    }

    public RegistroDataModelImpl getListaClasecptecb()
    {
        return listaClasecptecb;
    }

    public void setListaClasecptecb(RegistroDataModelImpl listaClasecptecb)
    {
        this.listaClasecptecb = listaClasecptecb;
    }

    /**
     * Retorna la lista listaReferenciaInicial
     *
     * @return listaReferenciaInicial
     */
    public RegistroDataModelImpl getListaReferenciaInicial()
    {
        return listaReferenciaInicial;
    }

    /**
     * Asigna la lista listaReferenciaInicial
     *
     * @param listaReferenciaInicial
     * Variable a asignar en listaReferenciaInicial
     */
    public void setListaReferenciaInicial(
        RegistroDataModelImpl listaReferenciaInicial)
    {
        this.listaReferenciaInicial = listaReferenciaInicial;
    }

    /**
     * Retorna la lista listaReferenciaFinal
     *
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal()
    {
        return listaReferenciaFinal;
    }

    /**
     * Asigna la lista listaReferenciaFinal
     *
     * @param listaReferenciaFinal
     * Variable a asignar en listaReferenciaFinal
     */
    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal)
    {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    public String getClasecpte()
    {
        return clasecpte;
    }

    public void setClasecpte(String clasecpte)
    {
        this.clasecpte = clasecpte;
    }

    /**
     * Retorna la variable referenciaInicial
     *
     * @return referenciaInicial
     */
    public String getReferenciaInicial()
    {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     *
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial)
    {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     *
     * @return referenciaFinal
     */
    public String getReferenciaFinal()
    {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     *
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal)
    {
        this.referenciaFinal = referenciaFinal;
    }

    public boolean isClasecpteview()
    {
        return clasecpteview;
    }

    public void setClasecpteview(boolean clasecpteview)
    {
        this.clasecpteview = clasecpteview;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
