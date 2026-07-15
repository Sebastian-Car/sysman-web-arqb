package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmrptfuentesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmrptfuentesControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author ybecerra
 * @version 1, 16/10/2015
 * 
 * @author asana
 * @version 2, 21/09/2017 Se realiza proceso de refactoring
 */
@ManagedBean
@ViewScoped
public class FrmrptfuentesControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private Boolean subFuente;
    private String fuenteInicial;
    private String fuenteFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCmbFuente;
    private RegistroDataModelImpl listaCmbFuenteF;
    private String codigo;
    private String nombreInicial;
    private String nombreFinal;

    /**
     * Creates a new instance of FrmrptfuentesControlador
     */
    public FrmrptfuentesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        fuenteInicial = "00000000";
        fuenteFinal = "99999999";
        codigo = "CODIGOFUENTE";
        nombreInicial = "";
        nombreFinal = "";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMRPTFUENTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmrptfuentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbFuente();
        cargarListacmbFuenteF();
        abrirFormulario();
    }

    public void cargarListacmbFuente()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrptfuentesControladorUrlEnum.URL2379.getValue());

        listaCmbFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, codigo);

    }

    public void cargarListacmbFuenteF()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmrptfuentesControladorEnum.PARAM1.getValue(),
                        fuenteInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmrptfuentesControladorUrlEnum.URL3088.getValue());

        listaCmbFuenteF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, codigo);

    }

    public void cambiarCmbFuente()
    {
        // <CODIGO_DESARROLLADO>
        cargarListacmbFuenteF();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>

        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        String nombreReporte = "";
        try
        {

            if (subFuente)
            {
                nombreReporte = "000308rptfuentessubfuentes";

            }
            else
            {
                nombreReporte = "000309rptfuentes";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);
            Map<String, Object> parametros = new HashMap<>();

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmrptfuentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | SysmanException | IOException ex)
        {
            Logger.getLogger(FrmrptfuentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    // </CODIGO_DESARROLLADO>
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>

        generarInforme(ReportesBean.FORMATOS.EXCEL97);
    }
    // </CODIGO_DESARROLLADO>

    public void seleccionarFilaCmbFuente(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        fuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();

        fuenteFinal = "";
        nombreInicial = registroAux.getCampos().get("NOMBRE").toString();

        cargarListacmbFuenteF();
    }

    public void seleccionarFilaCmbFuenteF(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();

        fuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        nombreFinal = registroAux.getCampos().get("NOMBRE").toString();

    }

    public Boolean getSubFuente()
    {
        return subFuente;
    }

    public void setSubFuente(Boolean subFuente)
    {
        this.subFuente = subFuente;
    }

    public String getFuenteInicial()
    {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial)
    {
        this.fuenteInicial = fuenteInicial;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getFuenteFinal()
    {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal)
    {
        this.fuenteFinal = fuenteFinal;
    }

    public RegistroDataModelImpl getListaCmbFuente()
    {
        return listaCmbFuente;
    }

    public void setListaCmbFuente(RegistroDataModelImpl listaCmbFuente)
    {
        this.listaCmbFuente = listaCmbFuente;
    }

    public RegistroDataModelImpl getListaCmbFuenteF()
    {
        return listaCmbFuenteF;
    }

    public void setListaCmbFuenteF(RegistroDataModelImpl listaCmbFuenteF)
    {
        this.listaCmbFuenteF = listaCmbFuenteF;
    }

    public String getNombreInicial()
    {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial)
    {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal()
    {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal)
    {
        this.nombreFinal = nombreFinal;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
    }
}