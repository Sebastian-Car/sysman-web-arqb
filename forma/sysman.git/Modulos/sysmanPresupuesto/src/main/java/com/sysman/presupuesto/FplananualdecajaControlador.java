package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FplananualdecajaControladorEnum;
import com.sysman.presupuesto.enums.FplananualdecajaControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 06/07/2016
 *
 * -- Modificado por lcortes 18/04/2017 14:25. Ajustes refactorizacion
 * de codigo y SonarLint.
 * 
 * @author ybecerra
 * @version 2, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class FplananualdecajaControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    /**
     * Constante para identificar el texto MSM_TRANS_INTERRUMPIDA
     */
    private final String msmTransInterrumpida;
    /**
     * Constante que permite identificar el texto SYSDATE
     */
    private final String cSysdate;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private String nombreCuentaFin;
    private String nombreCuentaIni;
    private StreamedContent archivoDescarga;

    private String cargoUnoPresupuesto;
    private String cargoDosPresupuesto;
    private String firmaUnoPresupuesto;
    private String firmaDosPresupuesto;
    private String teniendoModificaciones;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FplananualdecajaControlador
     */
    public FplananualdecajaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        msmTransInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        cSysdate = "SYSDATE";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FPLANANUALDECAJA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FplananualdecajaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
    	abrirFormulario();
        cargarListaAno();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
              
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN));
        

        /*
         * FR971-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FplananualdecajaControladorUrlEnum.URL3596
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(FplananualdecajaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FplananualdecajaControladorUrlEnum.URL4086
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FplananualdecajaControladorUrlEnum.URL4943
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FplananualdecajaControladorEnum.PARAM3.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    private boolean validarParametros()
    {
        try
        {
            cargoUnoPresupuesto = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO 1 EN PRESUPUESTO", modulo,
                            cSysdate);
            if (cargoUnoPresupuesto == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB182"));
                return false;
            }
            cargoDosPresupuesto = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "CARGO 2 EN PRESUPUESTO", modulo,
                            cSysdate);
            if (cargoDosPresupuesto == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB183"));
                return false;
            }
            firmaUnoPresupuesto = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "FIRMA 1 EN PRESUPUESTO", modulo,
                            cSysdate);
            if (firmaUnoPresupuesto == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB184"));
                return false;
            }
            firmaDosPresupuesto = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "FIRMA 2 EN PRESUPUESTO", modulo,
                            cSysdate);
            if (firmaDosPresupuesto == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB185"));
                return false;
            }

        }
        catch (NamingException | SQLException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(msmTransInterrumpida)
                                + ex.getMessage());
            Logger.getLogger(FplananualdecajaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public void obtenerReporte(FORMATOS formatos)
    {
        String reporte = "000968FPlanAnualDeCaja";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            parametros.put("PR_CARGO_1_EN_PRESUPUESTO", cargoUnoPresupuesto);
            parametros.put("PR_CARGO_2_EN_PRESUPUESTO", cargoDosPresupuesto);
            parametros.put("PR_FIRMA_1_EN_PRESUPUESTO", firmaUnoPresupuesto);
            parametros.put("PR_FIRMA_2_EN_PRESUPUESTO", firmaDosPresupuesto);
            parametros.put("PR_PARAMETRO",
                            "NO".equals(teniendoModificaciones) ? 0 : 1);

            parametros.put("PR_ANO", "PLAN ANUAL DE CAJA " + ano + "");
            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarParametros())
        {
            return;
        }
        try
        {
            teniendoModificaciones = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "DISTRIBUIR PAC TENIENDO EN CUENTA MODIFICACIONES",
                            modulo, cSysdate);
        }
        catch (NamingException | SQLException e)
        {
            Logger.getLogger(FplananualdecajaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (teniendoModificaciones == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2696"));
            return;
        }
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarParametros())
        {
            return;
        }
        try
        {
            teniendoModificaciones = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "DISTRIBUIR PAC TENIENDO EN CUENTA MODIFICACIONES",
                            modulo, cSysdate);
        }
        catch (NamingException | SQLException e)
        {
            Logger.getLogger(FplananualdecajaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (teniendoModificaciones == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2696"));
            return;
        }
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaIni = "";
        nombreCuentaFin = "";

        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        nombreCuentaIni = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
        nombreCuentaFin = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
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

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNombreCuentaFin()
    {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin)
    {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    public String getNombreCuentaIni()
    {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni)
    {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

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
