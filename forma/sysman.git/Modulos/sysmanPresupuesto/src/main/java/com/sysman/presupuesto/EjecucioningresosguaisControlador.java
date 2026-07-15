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
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucioningresosguaisControladorEnum;
import com.sysman.presupuesto.enums.EjecucioningresosguaisControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 21/06/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author jreina
 * @version 4, 13/06/2017 Cambio código formulario y actualización de
 * ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class EjecucioningresosguaisControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "SYSDATE"
     */
    private final String sysdate;
    // <DECLARAR_ATRIBUTOS>
    private String enMiles;
    private String cuentaInicial;
    private String cuentaFinal;
    private String codigoInicial;
    private String codigoFinal;
    private int mesInicial;
    private int mesFinal;
    private String centroInicial;
    private int ano;
    private StreamedContent archivoDescarga;
    String nombreCentro;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;
    private List<Registro> listaano;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of EjecucioningresosguaisControlador
     */
    public EjecucioningresosguaisControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        sysdate = "SYSDATE";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.EJECUCIONINGRESOSGUAIS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(EjecucioningresosguaisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        // <CARGAR_LISTA>
        cargarListaano();
        ano = SysmanFunciones
                        .ano(new Date());
        abrirFormulario();
        cargarListaMesInicial();
        mesInicial = 1;
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMesInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucioningresosguaisControladorUrlEnum.URL4056
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
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NUMERO.getName(), mesInicial);
        try
        {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucioningresosguaisControladorUrlEnum.URL4601
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaano()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucioningresosguaisControladorUrlEnum.URL5085
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
                                        EjecucioningresosguaisControladorUrlEnum.URL5636
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucioningresosguaisControladorUrlEnum.URL6515
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecucioningresosguaisControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListacentrocostoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucioningresosguaisControladorUrlEnum.URL7723
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String condicion = codigoInicial.equals(codigoFinal)
            ? " AND V_PLAN_PRESUPUESTAL.CODIGO = CENTROCTOMAYOR.CODIGO " : "";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("centroCosto", "'" + centroInicial + "'");
            reemplazar.put("condicion", condicion);

            String nombreRepresentante = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "NOMBRE REPRESENTANTE LEGAL", modulo,
                            sysdate);
            String nombreSecretaria = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "NOMBRE DE SECRETARIA DE HACIENDA",
                            modulo, sysdate);
            String nombreJefe = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo,
                            sysdate);
            String nombreTesorero = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "NOMBRE TESORERO", modulo, sysdate);
            String cargoRepresentante = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO REPRESENTANTE LEGAL", modulo,
                            sysdate);
            String cargoSecretaria = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO DE SECRETARIA DE HACIENDA", modulo,
                            sysdate);
            String cargoJefe = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO PRESUPUESTO", modulo, sysdate);
            String cargoTesorero = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO TESORERO", modulo, sysdate);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NOMBREREPRESENTANTE", nombreRepresentante);
            parametros.put("PR_PERIODO",
                            "DE "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                                                .toUpperCase()
                                + " A "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                                                .toUpperCase()
                                + " DE " + ano);
            parametros.put("PR_CENTROCOSTO", "CENTRO DE COSTO: "
                + (nombreCentro == null ? " CONSOLIDADO " : nombreCentro));
            parametros.put("PR_NOMBRESECRETARIA", nombreSecretaria);
            parametros.put("PR_NOMBREJEFE", nombreJefe);
            parametros.put("PR_CARGOJEFE", cargoJefe);
            parametros.put("PR_CARGOSECRETARIA", cargoSecretaria);
            parametros.put("PR_CARGOREPRESENTANTE", cargoRepresentante);
            parametros.put("PR_NOMBRETESORERO", nombreTesorero);
            parametros.put("PR_CARGOTESORERO", cargoTesorero);
            parametros.put("PR_MESES",
                            "ENTRE "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]
                                                .toUpperCase()
                                + " Y "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]
                                                .toUpperCase());
            parametros.put("PR_ANO", ano);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            if (SysmanFunciones.validarVariableVacio(centroInicial))
            {
                Reporteador.resuelveConsulta("000930LisEjecucionIngresosGUAI",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
            }
            else
            {
                Reporteador.resuelveConsulta(
                                "000930LisEjecucionIngresosGUAIMayoriza",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
            }

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000930LisEjecucionIngresosGUAI", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | NamingException
                        | SQLException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>

    public void cambiarano()
    {

        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinal = null;
        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreCentro = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getEnMiles()
    {
        return enMiles;
    }

    public void setEnMiles(String enMiles)
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

    public int getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal)
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

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
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

    public List<Registro> getListaano()
    {
        return listaano;
    }

    public void setListaano(List<Registro> listaano)
    {
        this.listaano = listaano;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
