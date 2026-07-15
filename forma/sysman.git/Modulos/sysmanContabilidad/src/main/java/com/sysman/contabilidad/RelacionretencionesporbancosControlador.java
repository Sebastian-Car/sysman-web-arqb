package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.enums.RelacionretencionesporbancosControladorEnum;
import com.sysman.contabilidad.enums.RelacionretencionesporbancosControladorUrlEnum;
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

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 19/05/2016
 * @modified jsforero
 * @version 2. 10/04/2017 Se realizo el refactory. Ademas se hicieron las respectivas Correcciones del sonar.
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio cï¿½digo formulario y actualizaciï¿½n de ConnectorPool
 * 
 * * @author jgomezp
 * @version 4, 04/09/2018 Se agraga reporte N° 001895 de retenciones detalladas.
 */
@ManagedBean
@ViewScoped
public class RelacionretencionesporbancosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private final String codigoConst;
    private String bancoInicial;
    private String bancoFinal;
    private String tipo;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;

    private RegistroDataModelImpl listaBancoInicial;
    private RegistroDataModelImpl listaBancoFinal;

    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

    /**
     * Creates a new instance of RelacionretencionesporbancosControlador
     */
    public RelacionretencionesporbancosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoConst = "CODIGO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELACIONRETENCIONESPORBANCOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        cargarListaBancoInicial();
    }

    @Override
    public void abrirFormulario()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        tipo = "1";
    }

    public void cargarListaBancoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionretencionesporbancosControladorUrlEnum.URL3938
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));
        listaBancoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaBancoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionretencionesporbancosControladorUrlEnum.URL5009
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(RelacionretencionesporbancosControladorEnum.BANCOINICIAL
                        .getValue(), bancoInicial);
        listaBancoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            String nombreReporte = "000792RelacionRetencionesPorBancoAcum";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("resumido", ("1".equals(tipo)) ? "-1" : "0");
            reemplazar.put("resumido", tipo);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAS", "Periodo del "
                            + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " al "
                            + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException
                        | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
    }

    public void cambiarFechaInicial()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaBancoInicial();
    }

    public void seleccionarFilaBancoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux == null ? ""
                        : registroAux.getCampos().get(codigoConst).toString();
        bancoFinal = null;
        cargarListaBancoFinal();
    }

    public void seleccionarFilaBancoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux == null ? ""
                        : registroAux.getCampos().get(codigoConst).toString();
    }

    public String getBancoInicial()
    {
        return bancoInicial;
    }

    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    public String getBancoFinal()
    {
        return bancoFinal;
    }

    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaBancoInicial()
    {
        return listaBancoInicial;
    }

    public void setListaBancoInicial(RegistroDataModelImpl listaBancoInicial)
    {
        this.listaBancoInicial = listaBancoInicial;
    }

    public RegistroDataModelImpl getListaBancoFinal()
    {
        return listaBancoFinal;
    }

    public void setListaBancoFinal(RegistroDataModelImpl listaBancoFinal)
    {
        this.listaBancoFinal = listaBancoFinal;
    }

}
