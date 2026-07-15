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
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionMensualGastosControladorEnum;
import com.sysman.presupuesto.enums.EjecucionMensualGastosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 01/07/2016 10:09:45 -- Modificado por jrodriguezr
 * 
 * @author eamaya
 * @version 2, 18/04/2017 Proceso de Refactoring
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */
@ManagedBean
@ViewScoped

public class EjecucionMensualGastosControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String anio;
    private String nmes1;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMesInicial;
    private List<Registro> listaano;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of EjecucionMensualGastosControlador
     */
    public EjecucionMensualGastosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_MENSUAL_GASTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(EjecucionMensualGastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaano();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        cargarListaMesInicial();
        mesInicial = Integer.toString(SysmanFunciones.mes(new Date()));
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
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
    public void cargarListaMesInicial()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionMensualGastosControladorUrlEnum.URL3843
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
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EjecucionMensualGastosControladorUrlEnum.URL4261
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
                                        EjecucionMensualGastosControladorUrlEnum.URL4600
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(EjecucionMensualGastosControladorEnum.PARAM1.getValue(),
                        "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EjecucionMensualGastosControladorUrlEnum.URL5546
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(EjecucionMensualGastosControladorEnum.PARAM3.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(EjecucionMensualGastosControladorEnum.PARAM1.getValue(),
                        "D");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

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

    public boolean validarVacios()
    {
        if (SysmanFunciones.validarVariableVacio(anio))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB729"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB730"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB195"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB196"));
            return false;
        }
        return true;
    }

    private void generaReporte(FORMATOS formato)
    {
        String reporte = "000962LisEjecPptalGastosCTRECE";
        try
        {
            if (!validarVacios())
            {
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("anio", anio);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            String cargoPresupuesto;
            String nombreJefePresupuesto;

            cargoPresupuesto = ejbParametro.consultarParametro(compania,
                            "CARGO PRESUPUESTO", SessionUtil.getModulo(),
                            new Date(), false);

            nombreJefePresupuesto = ejbParametro.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO",
                            SessionUtil.getModulo(), new Date(), false);

            parametros.put("PR_CARGO_PRESUPUESTO", cargoPresupuesto);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombreJefePresupuesto);
            parametros.put("PR_MES", mesInicial);
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES", nmes1);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarano()
    {
        // <CODIGO_DESARROLLADO>
        mesInicial = nmes1 = null;
        cargarListaMesInicial();
        cargarListaCuentaInicial();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("ID"), " ").toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;

    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("ID"), " ").toString();
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
