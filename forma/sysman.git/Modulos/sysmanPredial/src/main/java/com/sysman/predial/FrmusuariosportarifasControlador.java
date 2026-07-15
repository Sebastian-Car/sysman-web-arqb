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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmusuariosportarifasControladorEnum;
import com.sysman.predial.enums.FrmusuariosportarifasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @version 1, 02/06/2016
 *
 * -- Modificado por lcortes 20/02/2017
 * 
 * @author ybecerra
 * @version 3, 06/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class FrmusuariosportarifasControlador extends BeanBaseModal
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

    /**
     * Constante que almacenara la cadena "TRPCOD"
     */
    private final String trpCodC;
    // <DECLARAR_ATRIBUTOS>
    private String tarifaInicial;
    private String tarifaFinal;
    private int vigencia;
    private String ubicacion;
    private boolean resumen;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaInicial;
    private RegistroDataModelImpl listafinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de FrmusuariosportarifasControlador
     */
    public FrmusuariosportarifasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        trpCodC = FrmusuariosportarifasControladorEnum.PARAM1.getValue();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMUSUARIOSPORTARIFAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmusuariosportarifasControlador.class.getName())
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
        cargarListaVigencia();
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

    /**
     * 
     * Carga la lista listaVigencia
     */
    // <METODOS_CARGAR_LISTA>
    public void cargarListaVigencia()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmusuariosportarifasControladorUrlEnum.URL3441
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
     * Carga la lista listaInicial
     */
    public void cargarListaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmusuariosportarifasControladorUrlEnum.URL4038
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        listaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, trpCodC);

    }

    /**
     * 
     * Carga la lista listafinal
     */
    public void cargarListafinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmusuariosportarifasControladorUrlEnum.URL4707
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmusuariosportarifasControladorEnum.PARAM0.getValue(),
                        tarifaInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigencia);

        listafinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, trpCodC);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pantalla en la vista
     *
     */
    public void oprimirPantalla()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
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
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        String reporte = "000864RptUsuariosporTarifa";
        try
        {
            String trpCod;
            String avaluoAno;
            String like = "";
            int visible;
            Integer ano = SysmanFunciones.ano(
                            new Date());
            if (vigencia == ano)
            {

                trpCod = "  IP_TARIFAS.TRPCOD ";
                avaluoAno = " IP_USUARIOS_PREDIAL.AVALUO_ANO ";

            }
            else
            {
                trpCod = "  IP_FACTURADOS.TRPCOD   ";
                avaluoAno = " IP_FACTURADOS.AVALUO AS AVALUO_ANO   ";

            }

            switch (ubicacion)
            {
            case "1":
                like = " AND IP_USUARIOS_PREDIAL.CODIGO LIKE '01%' ";
                break;
            case "2":
                like = " AND IP_USUARIOS_PREDIAL.CODIGO NOT LIKE '01%'";
                break;
            case "3":
                like = "";
                break;
            default:
                break;
            }

            if (resumen)
            {
                visible = 0;
            }
            else
            {

                visible = 1;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("trpCod", trpCod);
            reemplazar.put("avaluoAno", avaluoAno);
            reemplazar.put("like", like);
            reemplazar.put("ano", vigencia);
            reemplazar.put("tarifaInicial", "'" + tarifaInicial + "'");
            reemplazar.put("tarifaFinal", "'" + tarifaFinal + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_VISIBLE", visible);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Vigencia
     * 
     */
    public void cambiarVigencia()
    {
        tarifaInicial = null;
        tarifaFinal = null;
        cargarListaInicial();
        cargarListafinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tarifaInicial = registroAux.getCampos().get(trpCodC).toString();
        tarifaFinal = null;
        cargarListafinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listafinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilafinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tarifaFinal = registroAux.getCampos().get(trpCodC).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tarifaInicial
     * 
     * @return tarifaInicial
     */
    public String getTarifaInicial()
    {
        return tarifaInicial;
    }

    /**
     * Asigna la variable tarifaInicial
     * 
     * @param tarifaInicial
     * Variable a asignar en tarifaInicial
     */
    public void setTarifaInicial(String tarifaInicial)
    {
        this.tarifaInicial = tarifaInicial;
    }

    /**
     * Retorna la variable tarifaFinal
     * 
     * @return tarifaFinal
     */
    public String getTarifaFinal()
    {
        return tarifaFinal;
    }

    /**
     * Asigna la variable tarifaFinal
     * 
     * @param tarifaFinal
     * Variable a asignar en tarifaFinal
     */
    public void setTarifaFinal(String tarifaFinal)
    {
        this.tarifaFinal = tarifaFinal;
    }

    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public int getVigencia()
    {
        return vigencia;
    }

    public void setVigencia(int vigencia)
    {
        this.vigencia = vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public String getUbicacion()
    {
        return ubicacion;
    }

    /**
     * Asigna la variable ubicacion
     * 
     * @param ubicacion
     * Variable a asignar en ubicacion
     */
    public void setUbicacion(String ubicacion)
    {
        this.ubicacion = ubicacion;
    }

    /**
     * Retorna la variable resumen
     * 
     * @return resumen
     */
    public boolean isResumen()
    {
        return resumen;
    }

    /**
     * Asigna la variable resumen
     * 
     * @param resumen
     * Variable a asignar en resumen
     */
    public void setResumen(boolean resumen)
    {
        this.resumen = resumen;
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
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia()
    {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia)
    {
        this.listaVigencia = listaVigencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaInicial
     * 
     * @return listaInicial
     */
    public RegistroDataModelImpl getListaInicial()
    {
        return listaInicial;
    }

    /**
     * Asigna la lista listaInicial
     * 
     * @param listaInicial
     * Variable a asignar en listaInicial
     */
    public void setListaInicial(RegistroDataModelImpl listaInicial)
    {
        this.listaInicial = listaInicial;
    }

    /**
     * Retorna la lista listafinal
     * 
     * @return listafinal
     */
    public RegistroDataModelImpl getListafinal()
    {
        return listafinal;
    }

    /**
     * Asigna la lista listafinal
     * 
     * @param listafinal
     * Variable a asignar en listafinal
     */
    public void setListafinal(RegistroDataModelImpl listafinal)
    {
        this.listafinal = listafinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
