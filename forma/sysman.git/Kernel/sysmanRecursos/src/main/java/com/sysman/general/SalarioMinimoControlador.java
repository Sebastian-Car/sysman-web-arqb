package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SalarioMinimoControladorEnum;
import com.sysman.general.enums.SalarioMinimoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 19/09/2015
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 04/04/2017
 */
@ManagedBean
@ViewScoped

public class SalarioMinimoControlador extends BeanBaseModal
{

    private final String compania;
    /**
     * Constante definida para almacenar el valor de
     * GeneralParameterEnum.NUMERO.getName();
     */
    private String estado;
    private final String cNumero;
    private String nuevoAnio;
    private String anio;
    private String salariominimo;
    private String cuantiaminima;
    private String cuantiaminimaDepre;
    private boolean cuantiaMini;
    private boolean cuantiaMiniDepre;
    private List<Registro> listaNumero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of SalarioMinimoControlador
     */
    public SalarioMinimoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SALARIO_MINIMO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(SalarioMinimoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        cargarListaNumero();
        abrirFormulario();

    }

    public void cargarListaNumero()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        try
        {
            listaNumero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SalarioMinimoControladorUrlEnum.URL2170
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimircmdActualizar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {

            estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania, Integer.parseInt(anio), Integer.parseInt(SessionUtil.getModulo()),
                            1);
            if ("A".equals(estado))
            {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SalarioMinimoControladorUrlEnum.URL4731
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(SalarioMinimoControladorEnum.PARAM0.getValue(),
                                salariominimo);
                fields.put(SalarioMinimoControladorEnum.PARAM1.getValue(),
                                cuantiaminima);
                fields.put(SalarioMinimoControladorEnum.PARAM2.getValue(),
                                cuantiaminimaDepre);
                fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                fields.put(GeneralParameterEnum.ANO.getName(), anio);
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                int actualizadas = requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
                if (actualizadas > 0)
                {
                    cargarListaNumero();
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3044"));
                }
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3045"));
                anio = salariominimo = cuantiaminima = cuantiaminimaDepre = null;
            }

        }
        catch (SystemException ex)
        {
            Logger.getLogger(SalarioMinimoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirAnio(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNumero()
    {
        // <CODIGO_DESARROLLADO>
        salariominimo = service.buscarEnLista(anio, cNumero, "SALARIOMINIMO",
                        listaNumero);
        cuantiaminima = service.buscarEnLista(anio, cNumero, "CUANTIAMINIMA",
                        listaNumero);
        cuantiaminimaDepre = service.buscarEnLista(anio, cNumero,
                        "CUANTIAMINIMADEPRE", listaNumero);
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioAnio(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarNuevoAnio()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            // <CODIGO_DESARROLLADO>
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SalarioMinimoControladorUrlEnum.URL5567
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(GeneralParameterEnum.NUMERO.getName(), nuevoAnio);
            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

            cargarListaNumero();
            anio = nuevoAnio;
            salariominimo = "0.00";
            cuantiaminima = "0.00";
            cuantiaminimaDepre = "0.00";
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(SalarioMinimoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        if ("90118".equals(SessionUtil.getMenuActual()))
        {

            cuantiaMini = true;

        }
        else
        {

            cuantiaMiniDepre = true;
        }
    }

    public String getNuevoAnio()
    {
        return nuevoAnio;
    }

    public void setNuevoAnio(String nuevoAnio)
    {
        this.nuevoAnio = nuevoAnio;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getSalariominimo()
    {
        return salariominimo;
    }

    public void setSalariominimo(String salariominimo)
    {
        this.salariominimo = salariominimo;
    }

    public String getCuantiaminima()
    {
        return cuantiaminima;
    }

    public void setCuantiaminima(String cuantiaminima)
    {
        this.cuantiaminima = cuantiaminima;
    }

    public List<Registro> getListaNumero()
    {
        return listaNumero;
    }

    public void setListaNumero(List<Registro> listaNumero)
    {
        this.listaNumero = listaNumero;
    }

    public String getCuantiaminimaDepre()
    {
        return cuantiaminimaDepre;
    }

    public void setCuantiaminimaDepre(String cuantiaminimaDepre)
    {
        this.cuantiaminimaDepre = cuantiaminimaDepre;
    }

    public boolean isCuantiaMini()
    {
        return cuantiaMini;
    }

    public void setCuantiaMini(boolean cuantiaMini)
    {
        this.cuantiaMini = cuantiaMini;
    }

    public boolean isCuantiaMiniDepre()
    {
        return cuantiaMiniDepre;
    }

    public void setCuantiaMiniDepre(boolean cuantiaMiniDepre)
    {
        this.cuantiaMiniDepre = cuantiaMiniDepre;
    }

}
