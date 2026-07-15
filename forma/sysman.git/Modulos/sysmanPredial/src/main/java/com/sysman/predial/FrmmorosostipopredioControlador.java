package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmmorosostipopredioControladorEnum;
import com.sysman.predial.enums.FrmmorosostipopredioControladorUrlEnum;
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
 * @author lcortes
 * @version 1, 02/06/2016
 *
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del formulario y actualización de ConnectorPool
 *
 * @author spina
 * @version 3, 05/07/2017 - refactorizacion dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmmorosostipopredioControlador extends BeanBaseModal
{
    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;

    private String nombreTipoInicial;
    private String nombreTipoFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbTipoInicial;
    private RegistroDataModelImpl listaCmbTipoFinal;
    private static final String CODIGO = "CODIGO";
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmmorosostipopredioControlador
     */
    public FrmmorosostipopredioControlador()
    {
        super();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMMOROSOSTIPOPREDIO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmmorosostipopredioControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        cargarListaCmbTipoInicial();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
    public void cargarListaCmbTipoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmorosostipopredioControladorUrlEnum.URL4409
                                                        .getValue());

        listaCmbTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCmbTipoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmorosostipopredioControladorUrlEnum.URL5371
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmmorosostipopredioControladorEnum.TIPOINICIAL.getValue(),
                        tipoInicial);

        listaCmbTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdgenerar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(tipoInicial)
            || SysmanFunciones.validarVariableVacio(tipoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB186"));
            return;
        }
        // </CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirCmdExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(tipoInicial)
            || SysmanFunciones.validarVariableVacio(tipoFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB186"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try
        {
            // Reemplazos valores consulta reporte
            reemplazos.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazos.put("tipoInicial", tipoInicial);
            reemplazos.put("tipoFinal", tipoFinal);

            // Inicio Parametros Reporte
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta("000853INFDEUDATIPOPREDIO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000853INFDEUDATIPOPREDIO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarChkConAcuerdos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nombreTipoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        tipoFinal = " ";
        nombreTipoFinal = " ";
        cargarListaCmbTipoFinal();

    }

    public void seleccionarFilaCmbTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nombreTipoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
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

    public String getNombreTipoInicial()
    {
        return nombreTipoInicial;
    }

    public void setNombreTipoInicial(String nombreTipoInicial)
    {
        this.nombreTipoInicial = nombreTipoInicial;
    }

    public String getNombreTipoFinal()
    {
        return nombreTipoFinal;
    }

    public void setNombreTipoFinal(String nombreTipoFinal)
    {
        this.nombreTipoFinal = nombreTipoFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbTipoInicial()
    {
        return listaCmbTipoInicial;
    }

    public void setListaCmbTipoInicial(
        RegistroDataModelImpl listaCmbTipoInicial)
    {
        this.listaCmbTipoInicial = listaCmbTipoInicial;
    }

    public RegistroDataModelImpl getListaCmbTipoFinal()
    {
        return listaCmbTipoFinal;
    }

    public void setListaCmbTipoFinal(RegistroDataModelImpl listaCmbTipoFinal)
    {
        this.listaCmbTipoFinal = listaCmbTipoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
