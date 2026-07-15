/*-
 * ImpuestopredialdnpsControlador.java
 *
 * 1.0
 *
 * 14/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.ImpuestopredialdnpsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que controla la vista del formulario Informe de Recuado
 * Predial PREDIAL\Informes\Informes DNP\Informe de Recaudo Predial
 *
 * @version 1.0, 14/02/2017
 * @author ybecerra
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @author ybecerra
 * @version 3, 06/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class ImpuestopredialdnpsControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo de modulo por
     * la cual ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano inicial
     */
    private String anoInicial;
    /**
     * Atributo que almacena el numero de ano seleccionado en el combo
     * ano Final
     */
    private String anoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo de la clase que almacena la lista de registros del
     * combo ano inicial
     */
    private List<Registro> listaanoini;
    /**
     * Atributo de la clase que almacena la lista de registros del
     * combo ano Final
     */
    private List<Registro> listaanofin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSymanUtl;

    /**
     * Crea una nueva instancia de ImpuestopredialdnpsControlador
     */
    public ImpuestopredialdnpsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.IMPUESTOPREDIALDNPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaanoini();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaanoini
     *
     */
    public void cargarListaanoini()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaanoini = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpuestopredialdnpsControladorUrlEnum.URL5240
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaanofin
     *
     */
    public void cargarListaanofin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoInicial);

        try
        {
            listaanofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpuestopredialdnpsControladorUrlEnum.URL5808
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton informe en la vista
     *
     *
     */
    public void oprimirinforme()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton excel en la vista
     *
     *
     */
    public void oprimirexcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    private String codigoRural(String codRurales)
    {
        String mensaje = "OK";

        if (codRurales == null)
        {
            mensaje = "TB_TB2824";
        }
        else if (Integer.parseInt(codRurales.substring(0, 1)) != 0)
        {
            mensaje = "TB_TB2825";
        }
        return mensaje;
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String reporte = "001403INFRECAUDOPREDIALDNP";
        try
        {
            String concep;
            String codRurales = ejbSymanUtl.consultarParametro(compania,
                            "CODIGOS PREDIOS RURALES", modulo, new Date(),
                            true);
            String conceptos = ejbSymanUtl.consultarParametro(compania,
                            "CONCEPTOS PARA EXCEDENTES Y SALDO", modulo,
                            new Date(), true);
            String tituloUno = ejbSymanUtl.consultarParametro(compania,
                            "TITULO UNO OFICIOS", modulo, new Date(), true);
            String tituloDos = ejbSymanUtl.consultarParametro(compania,
                            "TITULO DOS OFICIOS", modulo, new Date(), true);
            String tituloTres = ejbSymanUtl.consultarParametro(compania,
                            "TITULO TRES OFICIOS", modulo, new Date(), true);
            if (mensajeReporteNulo()
                || valorParametro("CONCEPTOS PARA EXCEDENTES Y SALDO",
                                "TB_TB2826"))
            {
                return;
            }
            if ("OK" != codigoRural(codRurales))
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString(codigoRural(codRurales)));
                return;
            }

            if ("NA".equals(conceptos))
            {
                concep = "";
            }
            else
            {
                concep = "+ C" + conceptos;
                concep = concep.replace(", ", "+ C");
                concep = concep.replace(",", "+ C");
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigosRurales", codRurales);
            reemplazar.put("conceptos", concep);
            reemplazar.put("anoInicial", anoInicial);
            reemplazar.put("anoFinal", anoFinal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULOUNO", tituloUno);
            parametros.put("PR_TITULODOS", tituloDos);
            parametros.put("PR_TITULOTRES", tituloTres);
            parametros.put("PR_NIT", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ANOINICIAL", anoInicial);
            parametros.put("PR_ANOFINAL", anoFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo que llamado en generarInforme
     *
     * @return true o false
     */
    private boolean mensajeReporteNulo()
    {
        return valorParametro("TITULO UNO OFICIOS", "TB_TB2827")
            || valorParametro("TITULO DOS OFICIOS", "TB_TB2828")
            || valorParametro("TITULO TRES OFICIOS", "TB_TB2829");

    }

    /**
     * Metodo que retorna verdadero si el parametro esta nulo
     *
     * @param nombreParametro
     * @param mensaje
     * @return true o false
     */
    private boolean valorParametro(String nombreParametro, String mensaje)
    {
        try
        {
            String parametro = ejbSymanUtl.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);

            if (parametro == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString(mensaje));
                return true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return false;
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoIni
     * 
     */
    public void cambiaranoini()
    {
        anoFinal = null;
        cargarListaanofin();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial()
    {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial)
    {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal()
    {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal)
    {
        this.anoFinal = anoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaanoini
     *
     * @return listaanoini
     */
    public List<Registro> getListaanoini()
    {
        return listaanoini;
    }

    /**
     * Asigna la lista listaanoini
     *
     * @param listaanoini
     * Variable a asignar en listaanoini
     */
    public void setListaanoini(List<Registro> listaanoini)
    {
        this.listaanoini = listaanoini;
    }

    /**
     * Retorna la lista listaanofin
     *
     * @return listaanofin
     */
    public List<Registro> getListaanofin()
    {
        return listaanofin;
    }

    /**
     * Asigna la lista listaanofin
     *
     * @param listaanofin
     * Variable a asignar en listaanofin
     */
    public void setListaanofin(List<Registro> listaanofin)
    {
        this.listaanofin = listaanofin;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
