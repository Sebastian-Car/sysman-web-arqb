package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
 * @author NGOMEZ
 * @version 1, 02/06/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author ybecerra
 * @version 3, 07/07/2017, proceso de Refactoring
 * 
 * @author eamaya
 * @version 3.1, 14/07/2017, Cambio de textos quemados por Texto en
 * Bean
 * 
 */
@ManagedBean
@ViewScoped
public class LiscertpysfechasvalorizacionControlador extends BeanBaseModal
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean anulados;
    private Date fechaInicial;
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Crea una nueva instancia de
     * LiscertpysfechasvalorizacionControlador
     */
    public LiscertpysfechasvalorizacionControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISCERTPYSFECHASVALORIZACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(LiscertpysfechasvalorizacionControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
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

        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        String reporte = "000862LisPazYsalvos";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("conAnulados", anulados ? " "
                : "AND ANULADO IN (0)");
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LISCERTPYS_FECHAS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_LISCERTPYS_FECHAS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_TITULO",
                            anulados
                                ? idioma.getString("TB_TB3307")
                                : idioma.getString("TB_TB3308"));
            parametros.put("PR_VALOR_VISIBLE", "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtl
                                            .consultarParametro(compania,
                                                            "MOSTRAR VALORES EN INFORME PAZ Y SALVOS",
                                                            modulo, new Date(),
                                                            true),
                                            "SI")));
            Reporteador.resuelveConsulta("000862LisPazYsalvosValorizacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            String[] cadena = { idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                            .replace("s$reporte$s", reporte), e.getMessage() };
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(cadena));
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException | SystemException | SysmanException
                        | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anulados
     * 
     * @return anulados
     */
    public boolean getAnulados()
    {
        return anulados;
    }

    /**
     * Asigna la variable anulados
     * 
     * @param anulados
     * Variable a asignar en anulados
     */
    public void setAnulados(boolean anulados)
    {
        this.anulados = anulados;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
