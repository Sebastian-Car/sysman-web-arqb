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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.predial.enums.FrmanoceromantControladorEnum;
import com.sysman.predial.enums.FrmanoceromantControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 08/06/2016
 * 
 * @author ybecerra
 * @version 2, 29/06/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class FrmanoceromantControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String codigo;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private String vigencia;
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
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    @EJB
    private EjbPredialUnoRemote ejbPredialUno;

    /**
     * Creates a new instance of FrmanoceromantControlador
     */
    public FrmanoceromantControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        codigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMANOCEROMANT_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmanoceromantControlador.class.getName())
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
        cargarListaVigencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodInicial();
        cargarListaCodFinal();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     */
    public void cargarListaVigencia()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmanoceromantControladorUrlEnum.URL3517
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
     * Carga la lista listaCodInicial
     */
    public void cargarListaCodInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmanoceromantControladorUrlEnum.URL4035
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /**
     * 
     * Carga la lista listaCodFinal
     */
    public void cargarListaCodFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmanoceromantControladorUrlEnum.URL5018
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmanoceromantControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAceptar en la vista
     *
     */
    public void oprimirCmdAceptar()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            String aux;
            aux = ejbPredialUno.verificarPrediosFacturados(compania,
                            codigoInicial, codigoFinal);
            ejbPredialUno.actualizarUltimaVigenciaPaga(compania, codigoInicial,
                            codigoFinal, Integer.parseInt(vigencia),
                            SessionUtil.getUser().getCodigo());

            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(aux);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "Inconsistencias.txt");
            ejecutarMensajeArchivo();

        }
        catch (NumberFormatException | SystemException | IOException
                        | JRException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EXCEL en la vista
     *
     */
    public void oprimirEXCEL()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF en la vista
     *
     */
    public void oprimirPDF()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    public void ejecutarMensajeArchivo()
    {
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        return;
    }

    /**
     * Metodo ejecutado al oprimir el boton de pdf o excel
     * 
     * @param formato
     */
    public void getInforme(FORMATOS formato)
    {
        HashMap<String, Object> reemplazar = new HashMap<>();
        try
        {
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000881INFPREDIOSANOCERO";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigo).toString();
        codigoFinal = null;
        cargarListaCodFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia()
    {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia)
    {
        this.vigencia = vigencia;
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
     * Retorna la lista listaCodInicial
     * 
     * @return listaCodInicial
     */
    public RegistroDataModelImpl getListaCodInicial()
    {
        return listaCodInicial;
    }

    /**
     * Asigna la lista listaCodInicial
     * 
     * @param listaCodInicial
     * Variable a asignar en listaCodInicial
     */
    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial)
    {
        this.listaCodInicial = listaCodInicial;
    }

    /**
     * Retorna la lista listaCodFinal
     * 
     * @return listaCodFinal
     */
    public RegistroDataModelImpl getListaCodFinal()
    {
        return listaCodFinal;
    }

    /**
     * Asigna la lista listaCodFinal
     * 
     * @param listaCodFinal
     * Variable a asignar en listaCodFinal
     */
    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal)
    {
        this.listaCodFinal = listaCodFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
