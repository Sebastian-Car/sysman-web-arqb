package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionGralTrasladosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 31/05/2016 08:57:29 -- Modificado por jrodriguezr
 * @modifier amonroy
 * @version 2, 10/04/2017 Proceso de Refactoring y Revision de buenas practicas sugeridas por la herramienta SonarLint
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class RelacionGralTrasladosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String mes;
    private String anio;
    private String nombreMes;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listames;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RelacionGralTrasladosControlador
     */
    public RelacionGralTrasladosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELACION_GRAL_TRASLADOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(RelacionGralTrasladosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListames();
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mes)];
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
    public void cargarListames()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacionGralTrasladosControladorUrlEnum.URL3653
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacionGralTrasladosControladorUrlEnum.URL4094
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
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

    private void generaReporte(FORMATOS formato)
    {
        if (SysmanFunciones.validarVariableVacio(anio))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB830"));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mes))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB831"));
            return;
        }

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "000814INFRELGRALTRASLADOS";
            String cargoTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO TESORERO", modulo, new Date(), true);
            String nombreTesorero = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE TESORERO", modulo, new Date(), true);
            String cargoAuxiliar = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO AUXILIAR TESORERO", modulo, new Date(),
                            true);
            String nombreAuxiliar = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE AUXILIAR TESORERO", modulo, new Date(),
                            true);
            String tipoCompTraslados = ejbSysmanUtil.consultarParametro(
                            compania,
                            "TIPOS DE COMPROBANTE PARA TRASLADOS CONTABLES",
                            modulo, new Date(), true);

            tipoCompTraslados = tipoCompTraslados == null ? ""
                : SysmanFunciones.colocarComillas(tipoCompTraslados);

            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("tipoCompTraslados", tipoCompTraslados);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            parametros.put("PR_ANO", anio);
            parametros.put("PR_MES", nombreMes.toUpperCase());
            parametros.put("PR_NOMBRE_TESORERO", nombreTesorero);
            parametros.put("PR_CARGO_TESORERO", cargoTesorero);
            parametros.put("PR_NOMBRE_AUXILIAR_TESORERO", nombreAuxiliar);
            parametros.put("PR_CARGO_AUXILIAR_TESORERO", cargoAuxiliar);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDepartamento()
                                            .toUpperCase());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SystemException | SysmanException ex)
        {
            Logger.getLogger(RelacionGralTrasladosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarmes()
    {
        // <CODIGO_DESARROLLADO>
        nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mes)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        mes = nombreMes = null;
        cargarListames();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getNombreMes()
    {
        return nombreMes;
    }

    public void setNombreMes(String nombreMes)
    {
        this.nombreMes = nombreMes;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListames()
    {
        return listames;
    }

    public void setListames(List<Registro> listames)
    {
        this.listames = listames;
    }

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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
