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
import com.sysman.nomina.enums.ResumPorCentroCostoControladorEnum;
import com.sysman.nomina.enums.ResumPorCentroCostoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 11/08/2015
 * @modified spina 23/03/2017 Las consultas para los reportes se
 * pasaron a la base de datos
 * 
 * @author ybecerra
 * @version 2, 27/10/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class ResumPorCentroCostoControlador extends BeanBaseModal
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    private final String periodoCons;
    private static final String CTEFILTRO = "filtro";
    // <DECLARAR_ATRIBUTOS>
    /**
     * 
     */
    private String anoInicial;
    /**
     * 
     */
    private String anoFinal;
    /**
     * 
     */
    private String mesInicial;
    /**
     * 
     */
    private String mesFinal;
    /**
     * 
     */
    private String periodoInicial;
    /**
     * 
     */
    private String periodoFinal;
    /**
     * 
     */
    private String proceso;
    /**
     * 
     */
    private String codigoCentro;
    /**
     * 
     */
    private String centrocosto;
    /**
     * 
     */
    private String opcion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAnoInicial;
    private List<Registro> listaAnoFinal;
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    private List<Registro> listaPeriodoInicial;
    private List<Registro> listaPeriodoFinal;
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoCentro;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de ResumPorCentroCostoControlador
     */
    public ResumPorCentroCostoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getCompania();
        periodoCons = ResumPorCentroCostoControladorEnum.PERIODO.getValue();

        // 109
        numFormulario = GeneralCodigoFormaEnum.RESUM_POR_CENTRO_COSTO_CONTROLADOR
                        .getCodigo();
        try
        {
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumPorCentroCostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        anoInicial = anoFinal = (String) SessionUtil
                        .getSessionVar("anioNomina");
        mesInicial = mesFinal = (String) SessionUtil.getSessionVar("mesNomina");
        periodoInicial = periodoFinal = (String) SessionUtil
                        .getSessionVar("periodoNomina");

        // <CARGAR_LISTA>
        cargarListaProceso();
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        cargarListaMesInicial();
        cargarListaMesFinal();
        cargarListaPeriodoInicial();
        cargarListaPeriodoFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoCentro();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        opcion = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoFinal
     */
    public void cargarListaAnoInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL6086
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaAnoFinal
     */
    public void cargarListaAnoFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL6086
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMesInicial
     */
    public void cargarListaMesInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try
        {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL7104
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMesFinal
     */
    public void cargarListaMesFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoFinal);

        try
        {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL7104
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPeriodoInicial
     */
    public void cargarListaPeriodoInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoFinal);
        param.put(GeneralParameterEnum.MES.getName(), mesFinal);

        try
        {
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL6594
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPeriodoFinal
     */
    public void cargarListaPeriodoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoFinal);
        param.put(GeneralParameterEnum.MES.getName(), mesFinal);

        try
        {
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL6594
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaProceso
     */
    public void cargarListaProceso()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumPorCentroCostoControladorUrlEnum.URL10526
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaCodigoCentro
     */
    public void cargarListaCodigoCentro()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumPorCentroCostoControladorUrlEnum.URL11399
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigoCentro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     * 
     */
    public void cambiarAnoInicial()
    {
        // <CODIGO_DESARROLLADO>
        mesInicial = null;
        periodoInicial = null;
        cargarListaMesInicial();
        cargarListaPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     * 
     */
    public void cambiarAnoFinal()
    {
        // <CODIGO_DESARROLLADO>
        mesFinal = null;
        periodoFinal = null;
        cargarListaMesFinal();
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cambiarMesInicial
     * 
     */
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        periodoInicial = null;
        cargarListaPeriodoInicial();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesFinal
     * 
     */
    public void cambiarMesFinal()
    {
        // <CODIGO_DESARROLLADO>
        periodoFinal = null;
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Proceso
     * 
     */
    public void cambiarProceso()
    {
        // <CODIGO_DESARROLLADO>
        anoInicial = null;
        anoFinal = null;
        mesInicial = null;
        mesFinal = null;
        periodoInicial = null;
        periodoFinal = null;
        cargarListaAnoInicial();
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Opcion
     * 
     */
    public void cambiarOpcion()
    {
        // <CODIGO_DESARROLLADO>
        codigoCentro = null;
        centrocosto = null;
        // <CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoCentro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoCentro(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoCentro = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        centrocosto = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * @return
     */
    private boolean validarCampos()
    {
        boolean rta = true;
        if (SysmanFunciones.validarVariableVacio(anoInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2324"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB703"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(periodoInicial))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2618"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(anoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2326"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mesFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB704"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(periodoFinal))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2621"));
            rta = false;
        }
        return rta;
    }

    /**
     * @return
     */
    private boolean validarPeriodo()
    {
        boolean rta = true;
        String perInicial = SysmanFunciones.concatenar(anoInicial,
                        SysmanFunciones.padl(mesInicial, 2, "0"),
                        SysmanFunciones.padl(periodoInicial, 2, "0"));
        String perFinal = SysmanFunciones.concatenar(anoFinal,
                        SysmanFunciones.padl(mesFinal, 2, "0"),
                        SysmanFunciones.padl(periodoFinal, 2, "0"));

        if (perInicial.compareTo(perFinal) > 0)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB574"));

            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(opcion))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1855"));

            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(proceso))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2548"));

            rta = false;
        }
        return rta;
    }

    /**
     * @return
     */
    private boolean validarCamposPeriodo()
    {
        if (!validarCampos())
        {
            return true;
        }

        if (!validarPeriodo())
        {
            return true;
        }
        return false;
    }

    /**
     * @param formato
     */
    public void generarInforme(FORMATOS formato)
    {
        // <CODIGO_DESARROLLADO>
        if (validarCamposPeriodo())
        {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);

        String codIni = SysmanFunciones
                        .concatenar(SysmanFunciones.padl(proceso, 2, "0"), "",
                                        SysmanFunciones.padl(anoInicial, 4,
                                                        "0"),
                                        "",
                                        SysmanFunciones.padl(mesInicial, 2,
                                                        "0"),
                                        "", SysmanFunciones.padl(periodoInicial,
                                                        2, "0"));
        String codFin = SysmanFunciones
                        .concatenar(SysmanFunciones.padl(proceso, 2, "0"), "",
                                        SysmanFunciones.padl(anoFinal, 4, "0"),
                                        "", SysmanFunciones.padl(mesFinal, 2,
                                                        "0"),
                                        "", SysmanFunciones.padl(periodoFinal,
                                                        2, "0"));

        reemplazar.put("codigoInicial",
                        codIni);
        reemplazar.put("codigoFinal", codFin);

        HashMap<String, Object> reemplazar2 = new HashMap<>();
        reemplazar2.put("compania", compania);
        reemplazar2.put("codigoInicial",
                        codIni);
        reemplazar2.put("codigoFinal", codFin);

        if ("2".equals(opcion))
        {
            if (SysmanFunciones.validarVariableVacio(codigoCentro))
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2658"));
                return;
            }

            reemplazar.put(CTEFILTRO,
                            SysmanFunciones.concatenar(
                                            "AND PERSONAL_HISTORICO.ID_CENTRO_DE_COSTO = '",
                                            codigoCentro, "'"));
            reemplazar2.put(CTEFILTRO,
                            SysmanFunciones.concatenar(
                                            "AND V_ACUMULADOS.ID_CENTRO_DE_COSTO = '",
                                            codigoCentro, "'"));
        }
        else
        {
            reemplazar.put(CTEFILTRO, "");
            reemplazar2.put(CTEFILTRO, "");
        }

        String rCentroCosto = Reporteador.resuelveConsulta(
                        "000155ResumPorCentroCosto",
                        Integer.valueOf(modulo), reemplazar);

        String rotro = Reporteador.resuelveConsulta(
                        "000154ResumPorCentroCostoOtro",
                        Integer.valueOf(modulo), reemplazar2);

        Map<String, Object> parametros = new HashMap<>();
        String parametroEntre = SysmanFunciones.concatenar("Entre: ",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesInicial)],
                        " de ", anoInicial," ", periodoCons, " ",
                        periodoInicial,
                        "  y  ",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesFinal)],
                        " de ", anoFinal, " ", periodoCons, " ", periodoFinal);
        parametros.put("PR_STRSQL", rCentroCosto);
        parametros.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_ENTRE", parametroEntre);
        Map<String, Object> parametros2 = new HashMap<>();
       
        parametros2.put("PR_STRSQL", rotro);
        parametros2.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros2.put("PR_ENTRE", parametroEntre);
        String[] nombres = new String[2];
        nombres[0] = "000155ResumPorCentroCosto";
        nombres[1] = "000154ResumPorCentroCostoOtro";

        Map<String, Object>[] listaParametros = new HashMap[2];

        listaParametros[0] = parametros;
        listaParametros[1] = parametros2;
        try
        {

            archivoDescarga = JsfUtil.exportarComprimidoReportesStreamed(
                            nombres, listaParametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion()
    {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable anoInicial
     * 
     * @return anoInicial
     */
    public String getAnoInicial()
    {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     * 
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable anoFinal
     * 
     * @return anoFinal
     */
    public String getAnoFinal()
    {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     * 
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal)
    {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public String getMesInicial()
    {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public String getMesFinal()
    {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable periodoInicial
     * 
     * @return periodoInicial
     */
    public String getPeriodoInicial()
    {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     * 
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial)
    {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable periodoFinal
     * 
     * @return periodoFinal
     */
    public String getPeriodoFinal()
    {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     * 
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal)
    {
        this.periodoFinal = periodoFinal;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso()
    {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable codigoCentro
     * 
     * @return codigoCentro
     */
    public String getCodigoCentro()
    {
        return codigoCentro;
    }

    /**
     * Asigna la variable codigoCentro
     * 
     * @param codigoCentro
     * Variable a asignar en codigoCentro
     */
    public void setCodigoCentro(String codigoCentro)
    {
        this.codigoCentro = codigoCentro;
    }

    /**
     * Retorna la variable centrocosto
     * 
     * @return centrocosto
     */
    public String getCentrocosto()
    {
        return centrocosto;
    }

    /**
     * Asigna la variable centrocosto
     * 
     * @param centrocosto
     * Variable a asignar en centrocosto
     */
    public void setCentrocosto(String centrocosto)
    {
        this.centrocosto = centrocosto;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoInicial
     * 
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     * 
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     * 
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal()
    {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     * 
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal)
    {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     * 
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial()
    {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     * 
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial)
    {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     * 
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal()
    {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     * 
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal)
    {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso()
    {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoCentro
     * 
     * @return listaCodigoCentro
     */
    public RegistroDataModelImpl getListaCodigoCentro()
    {
        return listaCodigoCentro;
    }

    /**
     * Asigna la lista listaCodigoCentro
     * 
     * @param listaCodigoCentro
     * Variable a asignar en listaCodigoCentro
     */
    public void setListaCodigoCentro(RegistroDataModelImpl listaCodigoCentro)
    {
        this.listaCodigoCentro = listaCodigoCentro;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
