package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.InformeEstratificacionControladorEnum;
import com.sysman.predial.enums.InformeEstratificacionControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @author jrodriguezr
 * @version 1, 10/06/2016
 *
 * @author spina
 * @version 2, 07/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class InformeEstratificacionControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private String estado;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listapredioInicial;
    private RegistroDataModelImpl listapredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private static final String CONDESTADO = "condEstado";

    /**
     * Creates a new instance of InformeEstratificacionControlador
     */
    public InformeEstratificacionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORME_ESTRATIFICACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(InformeEstratificacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListapredioInicial();

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
    public void cargarListapredioInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeEstratificacionControladorUrlEnum.URL5808
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listapredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListapredioFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeEstratificacionControladorUrlEnum.URL5240
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(InformeEstratificacionControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        listapredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
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

    private boolean mensaje()
    {
        if (SysmanFunciones.validarVariableVacio(estado))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB458"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(codigoInicial))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB459"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(codigoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB460"));
            return true;
        }
        return false;
    }

    private void generaReporte(FORMATOS formato)
    {
        if (mensaje())
        {
            return;
        }
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000899INFESTRATIFICACION";

            if ("I".equals(estado))
            {
                reemplazar.put(CONDESTADO,
                                " AND (USUARIOS_PREDIAL.PERMITE_EXP_CERT IN (0) "
                                    + "OR USUARIOS_PREDIAL.PERMITE_EXP_CERT IS NULL)");
            }
            else if ("A".equals(estado))
            {
                reemplazar.put(CONDESTADO,
                                " AND USUARIOS_PREDIAL.PERMITE_EXP_CERT NOT IN (0) ");
            }
            else
            {
                reemplazar.put(CONDESTADO, "");
            }
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), "<br>",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilapredioInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListapredioFinal();
        codigoFinal = null;
    }

    public void seleccionarFilapredioFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
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
    public RegistroDataModelImpl getListapredioInicial()
    {
        return listapredioInicial;
    }

    public void setListapredioInicial(RegistroDataModelImpl listapredioInicial)
    {
        this.listapredioInicial = listapredioInicial;
    }

    public RegistroDataModelImpl getListapredioFinal()
    {
        return listapredioFinal;
    }

    public void setListapredioFinal(RegistroDataModelImpl listapredioFinal)
    {
        this.listapredioFinal = listapredioFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
