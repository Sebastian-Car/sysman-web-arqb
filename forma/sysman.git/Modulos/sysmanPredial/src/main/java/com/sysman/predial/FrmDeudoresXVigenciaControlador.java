package com.sysman.predial;

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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmDeudoresXVigenciaControladorEnum;
import com.sysman.predial.enums.FrmDeudoresXVigenciaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda-modificado por jcrodriguez
 * @version 1, 23/06/2016
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario, se ajusta conexion.
 * @author spina
 * @version 3, 30/06/2017 - se refactoriza dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmDeudoresXVigenciaControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;

    private StreamedContent archivoDescarga;
    /**
     * variable que almacena el codigo final del predio
     */
    private String codigoFinal;
    /**
     * variable que almacena el a�o inicial
     */
    private int anoIni;
    /**
     * variable que almacena el a�o final
     */
    private int anoFin;
    /**
     * variable que almacena el codigo inicial del predio
     */
    private String codigoInicial;
    /**
     * variable que almacena el valor inicial
     */
    private String valorInicial;
    /**
     * variable que almacena el valor final
     */
    private String valorFinal;
    /**
     * variable que almacena el area const mayor a
     */
    private String areaConst;
    /**
     * variable que almacena las posibilidades de que un predio este activo, bloqueado y activos sin bloqueo
     */
    private String incluye;
    /**
     * lista que muestra los a�os
     */
    private List<Registro> listaAnoInicial;
    /**
     * lista que muestra los a�os
     */
    private List<Registro> listaAnofinal;
    /**
     * listado de predios y nit
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * listado de predios y nit
     */
    private RegistroDataModelImpl listaCodigoInicial;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmDeudoresXVigenciaControlador
     */
    public FrmDeudoresXVigenciaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anoIni = SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR);
        anoFin = SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR);
        areaConst = "0";
        incluye = "Activos";
        valorInicial = "0";
        valorFinal = "99999999999";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_DEUDORES_XVIGENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmDeudoresXVigenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAnoInicial();
        cargarListaAnofinal();
        cargarListaCodigoInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // HEREDADO DEL BEAN BASE
    }

    /**
     * metodo que carga una lista de a�os
     */
    public void cargarListaAnoInicial()
    {
        try
        {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDeudoresXVigenciaControladorUrlEnum.URL3958
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga una lista de a�os
     */
    public void cargarListaAnofinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), anoIni);

        try
        {
            listaAnofinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmDeudoresXVigenciaControladorUrlEnum.URL3959
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga una lista de codigos del predio y nit
     */
    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDeudoresXVigenciaControladorUrlEnum.URL3961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmDeudoresXVigenciaControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que carga una lista de codigos del predio y nit
     */
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmDeudoresXVigenciaControladorUrlEnum.URL3960
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo invocado cuando se oprime el boton pdf
     */
    public void oprimirImprimirPdf()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        if (validarVacios() && validarFAincluye())
        {
            generaInforme(ReportesBean.FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo invocado cuando se oprime el boton excel
     */
    public void oprimirImprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (validarVacios() && validarFAincluye())
        {
            generaInforme(ReportesBean.FORMATOS.EXCEL);
        }
    }

    /**
     * metodo que valida si los siguentes campos estan vacios: anoIni anoFin incluye areaConst
     *
     * @return
     */
    private boolean validarFAincluye()
    {
        if (anoIni != 0 || anoFin != 0
            || !SysmanFunciones.validarVariableVacio(incluye)
            || !SysmanFunciones.validarVariableVacio(areaConst))
        {
            return true;
        }
        return false;
    }

    /**
     * metodo que valida si los siguentes campos estan vacios: codigoInicial CodigoFinal valorInicial valorFinal
     *
     * @return
     */
    private boolean validarVacios()
    {
        if (!SysmanFunciones.validarVariableVacio(codigoInicial)
            || !SysmanFunciones.validarVariableVacio(codigoFinal)
            || !SysmanFunciones.validarVariableVacio(valorInicial)
            || !SysmanFunciones.validarVariableVacio(valorFinal))
        {
            return true;

        }
        return false;
    }

    /**
     * este metodo se invoca cuanso se va a generar el reporte y esta seleecionado la opcion incluye predios se adiciona una linea a la consulta del reporte
     *
     * @param strIncluye
     */
    private void agregarCondicion(StringBuilder strIncluye)
    {
        switch (incluye)
        {
        case "Activos":
            strIncluye.append(
                            " AND NVL(U.INDBORRADO,0) IN(0) AND NVL(U.CODIGO_NO_ACTIVO,0) IN(0) ");
            break;
        case "Bloqueados":
            strIncluye.append(
                            " AND NVL(U.INDBORRADO,0) IN(0) AND NVL(U.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(U.BLOQUEADO,0) NOT IN(0) ");
            break;
        case "Activos sin bloqueo":
            strIncluye.append(
                            " AND NVL(U.INDBORRADO,0) IN(0) AND NVL(U.CODIGO_NO_ACTIVO,0) IN(0) AND NVL(U.BLOQUEADO,0) IN(0) ");
            break;
        default:
            break;
        }
    }

    /**
     * metodo invocado cuando se genera un reporte en formato pdf excel
     *
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        String informe = null;
        StringBuilder strIncluye = new StringBuilder("");

        try
        {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            // Informe 000935RptDeudoresXVigencia
            informe = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO DEUDORES POR VIGENCIA", modulo, new Date(),
                            true);
            if (informe == null)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB429"));
                return;
            }
            agregarCondicion(strIncluye);

            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("valorInicial", valorInicial);
            reemplazar.put("valorFinal", valorFinal);
            reemplazar.put("anoIni", anoIni);
            reemplazar.put("anoFin", anoFin);
            reemplazar.put("strIncluye", strIncluye.toString());
            reemplazar.put("areaConst", areaConst);

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
            parametros.put("PR_CODIGOINICIAL", codigoInicial);
            parametros.put("PR_CODIGOFINAL", codigoFinal);
            parametros.put("PR_VRINICIAL", valorInicial);
            parametros.put("PR_VRFINAL", valorFinal);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que busca el concepto
     *
     * @param codigo
     * @return
     */
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
                                                            FrmDeudoresXVigenciaControladorUrlEnum.URL3962
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg == null ? " " : reg.getCampos().get("NOMBRE").toString();
    }

    public void cambiarAnoInicial()
    {
        cargarListaAnofinal();
    }

    /**
     * metodo para seleccionar un codigo final
     *
     * @param event
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo para seleccionar un codigo inicial
     *
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        codigoFinal = "";
        cargarListaCodigoFinal();
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public int getAnoIni()
    {
        return anoIni;
    }

    public void setAnoIni(int anoIni)
    {
        this.anoIni = anoIni;
    }

    public int getAnoFin()
    {
        return anoFin;
    }

    public void setAnoFin(int anoFin)
    {
        this.anoFin = anoFin;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getValorInicial()
    {
        return valorInicial;
    }

    public void setValorInicial(String valorInicial)
    {
        this.valorInicial = valorInicial;
    }

    public String getValorFinal()
    {
        return valorFinal;
    }

    public void setValorFinal(String valorFinal)
    {
        this.valorFinal = valorFinal;
    }

    public List<Registro> getListaAnoInicial()
    {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial)
    {
        this.listaAnoInicial = listaAnoInicial;
    }

    public List<Registro> getListaAnofinal()
    {
        return listaAnofinal;
    }

    public void setListaAnofinal(List<Registro> listaAnofinal)
    {
        this.listaAnofinal = listaAnofinal;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public String getAreaConst()
    {
        return areaConst;
    }

    public void setAreaConst(String areaConst)
    {
        this.areaConst = areaConst;
    }

    public String getCompania()
    {
        return compania;
    }

    public String getIncluye()
    {
        return incluye;
    }

    public void setIncluye(String incluye)
    {
        this.incluye = incluye;
    }
}