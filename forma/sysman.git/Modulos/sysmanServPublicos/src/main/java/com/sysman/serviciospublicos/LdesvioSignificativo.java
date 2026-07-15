/*-
 * LdesvioSignificativo.java
 *
 * 1.0
 *
 * 16/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman. Paipa, Boyac�. All rights reserved.
 *
 * Formulario para la generacion del informe "Usuarios con desvio significativo"
 *
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LdesvioSignificativoUrlEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario para la generacion del informe
 * "Usuarios con desvio significativo" para un a�o, periodo y ciclo
 * seleccionados.
 *
 * @author jlozano
 * @version 2, 22/09/2016 15:03:19 -- Modificado por jlozano
 * 
 * @version 3, 05/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author ybecerra
 * @version 4, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */

@ManagedBean
@ViewScoped
public class LdesvioSignificativo extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Ciclo seleccionado para el reporte
     */
    private String ciclo;

    /**
     * Periodo seleccionado para el reporte
     */
    private String periodo;

    /**
     * Anio seleccionado para el reporte
     */
    private String ano;

    /**
     * Archivo generado que contiene el reporte
     */
    private StreamedContent archivoDescarga; //
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private List<Registro> listaCiclo;
    private List<Registro> listaPeriodo;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LdesvioSignificativo
     */
    public LdesvioSignificativo()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.LDESVIO_SIGNIFICATIVO
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(LdesvioSignificativo.class.getName())
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
        cargarListaCiclo();
        cargarListaPeriodo();
        cargarListaAno();
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
    /**
     * Metodo que carga la lista de los ciclos disponibles para
     * seleccionar
     */
    public void cargarListaCiclo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LdesvioSignificativoUrlEnum.URL4447
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que carga la lista de los periodos disponibles para
     * seleccionar
     */
    public void cargarListaPeriodo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LdesvioSignificativoUrlEnum.URL4910
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que carga la lista de los anios disponibles para
     * seleccionar
     */
    public void cargarListaAno()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LdesvioSignificativoUrlEnum.URL5626
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

    /**
     * Metodo que se ejecuta al presionar el boton Impresora. Invoca
     * el metodo generarReporte y envia como parametro de formato
     * ReportesBean.FORMATOS.PDF
     */
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al presionar el boton Excel. Invoca el
     * metodo generarReporte y envia como parametro de formato
     * ReportesBean.FORMATOS.EXCEL
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera el reporte
     * "Usuarios con desvio significativo" para el anio,periodo y
     * ciclo seleccionado
     *
     * @param formato
     * Formato en el que se genera el reporte
     */
    public void generarReporte(ReportesBean.FORMATOS formato)
    {
        String reporte = "001085LDesvioSignificativo";
        archivoDescarga = null;
        HashMap<String, Object> reemplazos = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazos.put("ano", ano);
        reemplazos.put("ciclo", ciclo);
        reemplazos.put("periodo", periodo);

        parametros.put("PR_CICLO", ciclo);
        parametros.put("PR_PERIODO", periodo);

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()), reemplazos,
                        parametros);

        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
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

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        periodo = "";
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
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
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
