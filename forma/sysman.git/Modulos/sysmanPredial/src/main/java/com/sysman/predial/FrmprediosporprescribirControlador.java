package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmprediosporprescribirControladorEnum;
import com.sysman.predial.enums.FrmprediosporprescribirControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
 * @author acaceres
 * @version 1, 02/06/2016
 * @author jcrodriguez=>Depuracion y Refactoring
 * @version 2, 05/07/2017
 */
@ManagedBean
@ViewScoped

public class FrmprediosporprescribirControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private String tipoInforme;
    private String nombreCodigoInicial;
    private String nombreCodigoFinal;
    private Boolean deudaTotal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmprediosporprescribirControlador
     */
    public FrmprediosporprescribirControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPREDIOSPORPRESCRIBIR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmprediosporprescribirControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmprediosporprescribirControladorUrlEnum.URL3178.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosporprescribirControladorEnum.NUMERO_ORDEN_PREDIAL.getValue(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmprediosporprescribirControladorUrlEnum.URL4243.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosporprescribirControladorEnum.NUMERO_ORDEN_PREDIAL.getValue(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmprediosporprescribirControladorEnum.CODIGO_INICIAL.getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void obtenerReporte(FORMATOS formatos)
    {
        try
        {
            String anoPrescripcion = ejbSysmanUtil.consultarParametro(compania, parametros.getString("PR_ANO_MX_PRESCRIPCION"), modulo,
                            new Date(), true);

            int visible;

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            if (deudaTotal)
            {
                reemplazar.put("condicionAno", "");
            }
            else
            {
                reemplazar.put("condicionAno", "AND IP_FACTURADOS.PREANO <= "
                    + anoPrescripcion + "");
            }

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = FrmprediosporprescribirControladorEnum.REPORTE000855.getValue();
            // MANEJO DE PARAMETROS DEL REPORTE
            if (tipoInforme.equals(idioma.getString("OD_CB2764_1")))
            {
                visible = 0;
            }
            else
            {
                visible = 1;
            }
            parametros.put(FrmprediosporprescribirControladorEnum.PR_NITCOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put(FrmprediosporprescribirControladorEnum.PR_NOMBRECOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(FrmprediosporprescribirControladorEnum.PR_VISIBLE.getValue(), visible);

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoFinal = registroAux.getCampos().get("NOMBRE").toString();
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

    public String getTipoInforme()
    {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme)
    {
        this.tipoInforme = tipoInforme;
    }

    public String getNombreCodigoInicial()
    {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial)
    {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public String getNombreCodigoFinal()
    {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal)
    {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public Boolean getDeudaTotal()
    {
        return deudaTotal;
    }

    public void setDeudaTotal(Boolean deudaTotal)
    {
        this.deudaTotal = deudaTotal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
