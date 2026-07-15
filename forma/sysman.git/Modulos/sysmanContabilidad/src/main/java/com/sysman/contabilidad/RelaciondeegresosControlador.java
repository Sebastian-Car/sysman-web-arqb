package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelaciondeegresosControladorEnum;
import com.sysman.contabilidad.enums.RelaciondeegresosControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
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
 * @version 1, 20/05/2016
 * 
 * @author jlramirez
 * @version 2, 10/04/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de SONARLINT
 * @version 3, 20/04/2017, cambio de sysdate por new Date()
 */
@ManagedBean
@ViewScoped
public class RelaciondeegresosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean cuentaInicialVisible;
    private boolean rubroPresupuestal;
    private boolean compromiso;
    private boolean rubro;
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private int anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RelaciondeegresosControlador
     */
    public RelaciondeegresosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELACIONDEEGRESOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(RelaciondeegresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        abrirFormulario();
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        rubro = true;
        cuentaInicialVisible = true;
        rubroPresupuestal = !compromiso;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeegresosControladorUrlEnum.URL3743
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelaciondeegresosControladorEnum.ANOINICIAL.getValue(),
                        anio);
        param.put(RelaciondeegresosControladorEnum.ANOFINAL.getValue(),
                        anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, RelaciondeegresosControladorEnum.ID.getValue());
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelaciondeegresosControladorUrlEnum.URL4993
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RelaciondeegresosControladorEnum.ANOINICIAL.getValue(),
                        anio);
        param.put(RelaciondeegresosControladorEnum.ANOFINAL.getValue(),
                        anio);

        param.put(RelaciondeegresosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, RelaciondeegresosControladorEnum.ID.getValue());
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

    // </METODOS_BOTONES>

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anoInicial", SysmanFunciones.ano(fechaInicial));
            reemplazar.put("anoFinal", SysmanFunciones.ano(fechaFinal));
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FECHAS", "Periodo del "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " al "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_CONRUBRO", rubroPresupuestal ? true : false);
            String consultaResuelve;
            String Reporte;
            if (rubro)
            {
                if (SysmanFunciones.validarVariableVacio(cuentaInicial)
                    || SysmanFunciones.validarVariableVacio(cuentaFinal))
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB3524"));
                    return;
                }

                consultaResuelve = "000803RelacionDeEgresos";
                Reporte = "000803RelacionDeEgresos" ;
            }
            else
            {
                consultaResuelve = "000803RelacionDeEgresosValor";
                Reporte = "000803RelacionDeEgresosValor";
                
            }

            Reporteador.resuelveConsulta(consultaResuelve,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
            				Reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());

            logger.error(e.getMessage(), e);

        }

    }

    // <METODOS_CAMBIAR>

    public void cambiarVerificacion50()
    {
        if (rubroPresupuestal)
        {
            compromiso = false;
        }
        else
        {
            compromiso = true;
        }
    }

    public void cambiarVerificacion53()
    {
        if (compromiso)
        {
            rubroPresupuestal = false;
        }
        else
        {
            rubroPresupuestal = true;
        }
    }

    public void cambiarporubro()
    {
        if (rubro)
        {
            cuentaInicialVisible = true;
        }
        else
        {
            cuentaInicialVisible = false;
        }

        cuentaInicial = null;
        cuentaFinal = null;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();
    }
    
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        
        anio = SysmanFunciones.getParteFecha(fechaInicial, Calendar.YEAR);
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaCuentaInicial();
        cargarListaCuentaFinal();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal () {
     // </CODIGO_DESARROLLADO>
     // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public boolean getRubroPresupuestal()
    {
        return rubroPresupuestal;
    }

    public void setRubroPresupuestal(boolean rubroPresupuestal)
    {
        this.rubroPresupuestal = rubroPresupuestal;
    }

    public boolean getCompromiso()
    {
        return compromiso;
    }

    public void setCompromiso(boolean compromiso)
    {
        this.compromiso = compromiso;
    }

    public boolean getRubro()
    {
        return rubro;
    }

    public void setRubro(boolean rubro)
    {
        this.rubro = rubro;
    }

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

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isCuentaInicialVisible()
    {
        return cuentaInicialVisible;
    }

    public void setCuentaInicialVisible(boolean cuentaInicialVisible)
    {
        this.cuentaInicialVisible = cuentaInicialVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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
