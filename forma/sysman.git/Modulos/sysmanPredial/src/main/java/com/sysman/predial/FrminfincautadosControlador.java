package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrminfincautadosControladorEnum;
import com.sysman.predial.enums.FrminfincautadosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
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
 * @author ybecerra
 * @version 1, 27/05/2016
 *
 * -- Modificado por lcortes 02/03/2017 04:54 pm. Se ajustan los filtros de las listas codigo de predio inicial y final para que cargue los predios activos.
 *
 * @author spina
 * @version 2, 04/07/2017 - se refactoriza dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrminfincautadosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;

    /**
     * Creates a new instance of FrminfincautadosControlador
     */
    public FrminfincautadosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMINFINCAUTADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrminfincautadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaCodInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfincautadosControladorUrlEnum.URL3613
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminfincautadosControladorUrlEnum.URL4743
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrminfincautadosControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
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
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String reporte = "000829INFINCAUTADOS";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");

            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CODIGOS", idioma.getString("TB_TB3276")
                            .replace("s$codigoInicial$s", codigoInicial)
                            .replace("s$codigoFinal$s", codigoFinal));
            parametros.put("PR_FECHAS", "Y ENTRE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | IOException | JRException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrminfincautadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilaCodInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        codigoFinal = null;
        cargarListaCodFinal();
    }

    public void seleccionarFilaCodFinal(SelectEvent event)
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

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCodInicial()
    {
        return listaCodInicial;
    }

    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial)
    {
        this.listaCodInicial = listaCodInicial;
    }

    public RegistroDataModelImpl getListaCodFinal()
    {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal)
    {
        this.listaCodFinal = listaCodFinal;
    }
}
