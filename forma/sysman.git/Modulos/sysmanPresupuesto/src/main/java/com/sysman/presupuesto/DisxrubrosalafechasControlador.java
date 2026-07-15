package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.DisxrubrosalafechasControladorEnum;
import com.sysman.presupuesto.enums.DisxrubrosalafechasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
 * @author ybecerra
 * @version 1, 24/06/2016
 * @version 2, 17/04/2017 jrodriguezr Se refactoriza el codigo SQL de las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo ajustando los llamados a funciones, procedimientos y metodos de la clase Acciones a llamados a EJB.
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class DisxrubrosalafechasControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacuentaInicial;
    private RegistroDataModelImpl listacuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of DisxrubrosalafechasControlador
     */
    public DisxrubrosalafechasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DISXRUBROSALAFECHAS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(DisxrubrosalafechasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        abrirFormulario();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacuentaInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>

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
    public void cargarListacuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DisxrubrosalafechasControladorUrlEnum.URL3492
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DisxrubrosalafechasControladorEnum.ANOINICIAL.getValue(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(DisxrubrosalafechasControladorEnum.ANOFINAL.getValue(),
                        SysmanFunciones.ano(fechaFinal));

        listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    public void cargarListacuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DisxrubrosalafechasControladorUrlEnum.URL4750
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DisxrubrosalafechasControladorEnum.ANOINICIAL.getValue(),
                        SysmanFunciones.ano(fechaInicial));
        param.put(DisxrubrosalafechasControladorEnum.ANOFINAL.getValue(),
                        SysmanFunciones.ano(fechaFinal));
        param.put(DisxrubrosalafechasControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listacuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
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
        if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
                        .ano(fechaFinal))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB44"));
            return;
        }
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");

            String nombreJefe = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", modulo,
                            new Date(), true);
            String cargoJefe = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO PRESUPUESTO", modulo, new Date(), true);
            int mes = SysmanFunciones.mes(
                            new Date());

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NOMBREJEFE", nombreJefe);
            parametros.put("PR_CARGOJEFE", cargoJefe);
            parametros.put("PR_EXPEDIDA",
                            "Expedido en "
                                + SessionUtil.getCompaniaIngreso().getCiudad()
                                + " a los "
                                + SysmanFunciones.dia(new Date())
                                + " " + idioma.getString("TB_TB2684") + " "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                + " " + idioma.getString("TB_TB2685") + " "
                                + SysmanFunciones.ano(new Date()));
            parametros.put("PR_ENCABEZADO",
                            "Que en el presupuesto General de Rentas y Gastos de "
                                + SessionUtil.getCompaniaIngreso().getNombre()
                                + " " +
                                idioma.getString("TB_TB2683") + " "
                                + SysmanFunciones.ano(fechaInicial)
                                + ", " + idioma.getString("TB_TB2682"));

            Reporteador.resuelveConsulta("000946Disponibilidadesalafecha",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000946Disponibilidadesalafecha", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | OutOfMemoryError | JRException
                        | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial()
    {
        fechaFinal = null;

    }

    public void cambiarFechaFinal()
    {
        if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
                        .ano(fechaFinal))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB44"));
            fechaFinal = null;
            return;
        }
        else
        {
            cargarListacuentaInicial();
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinal = null;
        cargarListacuentaFinal();
    }

    public void seleccionarFilacuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacuentaInicial()
    {
        return listacuentaInicial;
    }

    public void setListacuentaInicial(
        RegistroDataModelImpl listacuentaInicial)
    {
        this.listacuentaInicial = listacuentaInicial;
    }

    public RegistroDataModelImpl getListacuentaFinal()
    {
        return listacuentaFinal;
    }

    public void setListacuentaFinal(RegistroDataModelImpl listacuentaFinal)
    {
        this.listacuentaFinal = listacuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
