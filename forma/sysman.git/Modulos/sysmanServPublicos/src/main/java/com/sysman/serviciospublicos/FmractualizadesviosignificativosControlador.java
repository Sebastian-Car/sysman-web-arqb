package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.FmractualizadesviosignificativosControladorEnum;
import com.sysman.serviciospublicos.enums.FmractualizadesviosignificativosControladorUrlEnum;

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

/**
 *
 * @author ybecerra
 * @version 1, 12/09/2016
 * @author spina
 * @version 2, 23/05/2017 - se refactoriza para dss, depuracion y ejb
 * 
 * @author ybecerra
 * @version 3, 18/07/2017, se cambiaron campos de codigo inicial y
 * final por combos
 */
@ManagedBean
@ViewScoped
public class FmractualizadesviosignificativosControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String ciclo;
    private String codigoInicial;
    private String codigoFinal;
    private boolean dialogoVisible;
    private String periodo;
    private int anio;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;

    private RegistroDataModelImpl listacodigoInicial;

    private RegistroDataModelImpl listacodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    @EJB
    private EjbServiciosPublicosCincoRemote ejbServiciosPublicosCinco;

    /**
     * Creates a new instance of
     * FmractualizadesviosignificativosControlador
     */
    public FmractualizadesviosignificativosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FMRACTUALIZADESVIOSIGNIFICATIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FmractualizadesviosignificativosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
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
        cargarListaCiclo();
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
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FmractualizadesviosignificativosControladorUrlEnum.URL10228
                                                        .getValue());

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FmractualizadesviosignificativosControladorEnum.NUMERO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listacodigoInicial
     *
     */
    public void cargarListacodigoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FmractualizadesviosignificativosControladorUrlEnum.URL145
                                                        .getValue());

        listacodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGORUTA.getName());

    }

    /**
     * 
     * Carga la lista listacodigoFinal
     */
    public void cargarListacodigoFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(FmractualizadesviosignificativosControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FmractualizadesviosignificativosControladorUrlEnum.URL178
                                                        .getValue());

        listacodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGORUTA.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            boolean desviacion = ejbServiciosPublicosDos.autorizarDesviacion(
                            compania,
                            SessionUtil.getCompaniaIngreso().getNit());
            if (desviacion)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1509"));
                return;
            }

            dialogoVisible = true;
        }
        catch (SystemException ex)

        {
            Logger.getLogger(FmractualizadesviosignificativosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void aceptarDgRta()
    {
        if (periodo.length() <= 1)
        {
            periodo = "0" + periodo;
        }

        try
        {
            ejbServiciosPublicosCinco.actualizacionDesvioSignificativo(compania,
                            Integer.parseInt(ciclo), codigoInicial, codigoFinal,
                            SessionUtil.getUser().getCodigo(), anio, periodo,
                            SessionUtil.getCompaniaIngreso().getNit());
            dialogoVisible = false;
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarDgRta()
    {

        dialogoVisible = false;

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCiclo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos()
                        .get(FmractualizadesviosignificativosControladorEnum.NUMERO
                                        .getValue())
                        .toString();
        codigoInicial = registroAux.getCampos()
                        .get(FmractualizadesviosignificativosControladorEnum.CODIGOINICIAL
                                        .getValue())
                        .toString();
        codigoFinal = registroAux.getCampos()
                        .get(FmractualizadesviosignificativosControladorEnum.CODIGOFINAL
                                        .getValue())
                        .toString();
        periodo = registroAux.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
        anio = Integer.parseInt(registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        cargarListacodigoInicial();
        cargarListacodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        codigoFinal = null;
        cargarListacodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
    }
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
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
    public RegistroDataModelImpl getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListacodigoInicial()
    {
        return listacodigoInicial;
    }

    public void setListacodigoInicial(RegistroDataModelImpl listacodigoInicial)
    {
        this.listacodigoInicial = listacodigoInicial;
    }

    public RegistroDataModelImpl getListacodigoFinal()
    {
        return listacodigoFinal;
    }

    public void setListacodigoFinal(RegistroDataModelImpl listacodigoFinal)
    {
        this.listacodigoFinal = listacodigoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
