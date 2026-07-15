package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.PrepararEmbargosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ngomez
 * @version 1, 23/07/2015
 *
 * @author spina
 * @version 2, 19/10/2017 - se refactoriza para dss, depuracion y ejbs
 */
@ManagedBean
@ViewScoped
public class PrepararEmbargosControlador extends BeanBaseModal
{

    private final String compania;
    private String mesUno;
    private String anioUno;
    private String mesDos;
    private String anioDos;
    private String periodoUno;
    private String periodoDos;
    private List<Registro> listaMes;
    private List<Registro> listaAno;
    private List<Registro> listaMes2;
    private List<Registro> listaAno2;
    private List<Registro> listaPeriodo;
    private List<Registro> listaPeriodo2;
    private final String proceso;
    private final String anio;
    private final String mes;
    private final String periodo;

    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    /**
     * Creates a new instance of PrepararEmbargosControlador
     */
    public PrepararEmbargosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        proceso = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("procesoNomina"), "")
                        .toString();
        anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(SessionUtil.getSessionVar("periodoNomina"), "")
                        .toString();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREPARAR_EMBARGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PrepararEmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        anioUno = anio;
        mesUno = mes;
        periodoUno = periodo;
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        abrirFormulario();
    }

    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioUno);
        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararEmbargosControladorUrlEnum.URL28520
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
                                                            PrepararEmbargosControladorUrlEnum.URL28521
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioDos);
        try
        {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararEmbargosControladorUrlEnum.URL28520
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2()
    {
        listaAno2 = listaAno;
    }

    public void cargarListaPeriodo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioUno);
        param.put(GeneralParameterEnum.MES.getName(), mesUno);
        try
        {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararEmbargosControladorUrlEnum.URL28522
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioDos);
        param.put(GeneralParameterEnum.MES.getName(), mesDos);
        try
        {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararEmbargosControladorUrlEnum.URL28522
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCerrar()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalContinuoModulo();");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPreparar()
    {

        if (Integer.parseInt(anioUno) > Integer.parseInt(anioDos))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3876"));
            return;
        }

        if (Integer.parseInt(mesUno) > Integer.parseInt(mesDos)
            && Integer.parseInt(anioUno) == Integer.parseInt(anioDos))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3877"));
            return;
        }

        try
        {

            ejbNominaDos.prepararEmbargos(compania, Integer.parseInt(anioUno),
                            Integer.parseInt(mesUno),
                            Integer.parseInt(periodoUno),
                            Integer.parseInt(anioDos), Integer.parseInt(mesDos),
                            Integer.parseInt(periodoDos),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2653")
                            .replace("#$periodoDos$#", periodoDos)
                            .replace("#$mesDos$#", mesDos)
                            .replace("#$anioDos$#", anioDos));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PrepararEmbargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString("TB_TB2651"),
                                            ex.getMessage()));
        }
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        mesUno = null;
        periodoUno = null;
        anioDos = null;
        mesDos = null;
        periodoDos = null;

        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes()
    {

        // <CODIGO_DESARROLLADO>
        periodoUno = null;
        anioDos = null;
        mesDos = null;
        periodoDos = null;
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo()
    {
        // <CODIGO_DESARROLLADO>
        anioDos = null;
        mesDos = null;
        periodoDos = null;
        perpre(anioUno, mesUno, periodoUno);
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarAno2()
    {
        // <CODIGO_DESARROLLADO>
        mesDos = null;
        periodoDos = null;
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2()
    {
        // <CODIGO_DESARROLLADO>
        periodoDos = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void perpre(String anioP, String mesP, String periodoP)
    {
        try
        {

            String resultadoAux = ejbNominaDos.siguientePeriodo(compania,
                            Integer.parseInt(anioP), Integer.parseInt(periodoP),
                            Integer.parseInt(mesP), Integer.parseInt(proceso),
                            "");
            if (resultadoAux.length() < 8)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2610"));
                anioDos = null;
                mesDos = null;
                periodoDos = null;
            }
            else
            {
                actualizarCombos(resultadoAux);
            }
        }
        catch (NumberFormatException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(PrepararFinanciablesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void actualizarCombos(String resultado)
    {
        anioDos = resultado.substring(0, 4);
        cargarListaMes2();
        mesDos = String.valueOf(Integer.parseInt(resultado.substring(4, 6)));
        cargarListaPeriodo2();
        periodoDos = String
                        .valueOf(Integer.parseInt(resultado.substring(6, 8)));
    }

    public String getMesUno()
    {
        return mesUno;
    }

    public void setMesUno(String mesUno)
    {
        this.mesUno = mesUno;
    }

    public String getAnioUno()
    {
        return anioUno;
    }

    public void setAnioUno(String anioUno)
    {
        this.anioUno = anioUno;
    }

    public String getMesDos()
    {
        return mesDos;
    }

    public void setMesDos(String mesDos)
    {
        this.mesDos = mesDos;
    }

    public String getAnioDos()
    {
        return anioDos;
    }

    public void setAnioDos(String anioDos)
    {
        this.anioDos = anioDos;
    }

    public String getPeriodoUno()
    {
        return periodoUno;
    }

    public void setPeriodoUno(String periodoUno)
    {
        this.periodoUno = periodoUno;
    }

    public String getPeriodoDos()
    {
        return periodoDos;
    }

    public void setPeriodoDos(String periodoDos)
    {
        this.periodoDos = periodoDos;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes2()
    {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2)
    {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaAno2()
    {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2)
    {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaPeriodo2()
    {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2)
    {
        this.listaPeriodo2 = listaPeriodo2;
    }

    @Override
    public void abrirFormulario()
    {
        perpre(anio, mes, periodo);
    }

}
