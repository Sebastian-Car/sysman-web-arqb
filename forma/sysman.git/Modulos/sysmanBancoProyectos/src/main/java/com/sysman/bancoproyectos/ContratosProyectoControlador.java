package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.ContratosProyectoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.FileNotFoundException;
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
 * @author esarmiento
 * @version 1, 23/11/2015
 * 
 * @author ybecerra
 * @version 2, 12/09/2017, proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class ContratosProyectoControlador extends BeanBaseModal
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
    private String codigoProyecto;
    private String pantalla;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaProyecto;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de ContratosProyectoControlador
     */
    public ContratosProyectoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            // 133
            numFormulario = GeneralCodigoFormaEnum.CONTRATOS_PROYECTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ContratosProyectoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaproyecto();
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
     * Carga la lista listaProyecto
     */
    public void cargarListaproyecto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ContratosProyectoControladorUrlEnum.URL2762
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        if (codigoProyecto == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2283"));
            return;
        }
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
        if (codigoProyecto == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2283"));
            return;
        }
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilaProyecto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoProyecto = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        pantalla = registroAux.getCampos().get("NOMBREPROYECTO").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    private void generarInforme(FORMATOS formato)
    {
        String nombreReporte = "";
        try
        {
            archivoDescarga = null;

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String strsql;

            nombreReporte = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO INFORME CONTRATOS POR PROYECTO", modulo,
                            new Date(), false);

            if (nombreReporte == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2287"));
                return;
            }

            reemplazar.put("proyecto", codigoProyecto);
            strsql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strsql);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(idioma
                            .getString("MSM_INFORME_VAR_NO_EXISTE")
                            .replace("s$reporte$s", nombreReporte)
                + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (SystemException | JRException | IOException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoProyecto
     * 
     * @return codigoProyecto
     */
    public String getCodigoProyecto()
    {
        return codigoProyecto;
    }

    /**
     * Asigna la variable codigoProyecto
     * 
     * @param codigoProyecto
     * Variable a asignar en codigoProyecto
     */
    public void setCodigoProyecto(String codigoProyecto)
    {
        this.codigoProyecto = codigoProyecto;
    }

    /**
     * Retorna la variable pantalla
     * 
     * @return pantalla
     */
    public String getPantalla()
    {
        return pantalla;
    }

    /**
     * Asigna la variable pantalla
     * 
     * @param pantalla
     * Variable a asignar en pantalla
     */
    public void setPantalla(String pantalla)
    {
        this.pantalla = pantalla;
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
    /**
     * Retorna la lista listaProyecto
     * 
     * @return listaProyecto
     */
    public RegistroDataModelImpl getListaProyecto()
    {
        return listaProyecto;
    }

    /**
     * Asigna la lista listaProyecto
     * 
     * @param listaProyecto
     * Variable a asignar en listaProyecto
     */
    public void setListaProyecto(RegistroDataModelImpl listaProyecto)
    {
        this.listaProyecto = listaProyecto;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}