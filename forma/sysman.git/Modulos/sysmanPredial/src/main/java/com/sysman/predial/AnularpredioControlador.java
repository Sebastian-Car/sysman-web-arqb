package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.AnularpredioControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 20/05/2016
 * 
 * @author asana
 * @version 2, 13/06/2017 se modifica enum en formulario
 * 
 * @author ybecerra
 * @version 3, 22/06/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class AnularpredioControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    private String codigo;
    private String numeroResolucion;
    private Date fechaResolucion;
    private String elaboraResolucion;
    private String firmaResolucion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;

    /**
     * Creates a new instance of AnularpredioControlador
     */
    public AnularpredioControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        fechaResolucion = new Date();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ANULARPREDIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
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
        cargarListacodigo();
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
     * Carga la lista listacodigo
     *
     */
    public void cargarListacodigo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnularpredioControladorUrlEnum.URL2892
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAnular en la vista
     *
     *
     */
    public void oprimirCmdAnular()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            JsfUtil.agregarMensajeInformativo(
                            ejbPredialCuatro.getAnularPredio(compania,
                                            SessionUtil.getNivelUsuario(
                                                            SessionUtil.getModulo()),
                                            codigo, numeroResolucion,
                                            fechaResolucion, elaboraResolucion,
                                            firmaResolucion,
                                            SessionUtil.getUser().getCodigo()));

        }
        catch (SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().get(cCodigo).toString().length() > 15)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB143"));
        }
        else
        {
            codigo = (String) registroAux.getCampos().get(cCodigo);
        }
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigo
     * 
     * @return codigo
     */
    public String getCodigo()
    {
        return codigo;
    }

    /**
     * Asigna la variable codigo
     * 
     * @param codigo
     * Variable a asignar en codigo
     */
    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    /**
     * Retorna la variable numeroResolucion
     * 
     * @return numeroResolucion
     */
    public String getNumeroResolucion()
    {
        return numeroResolucion;
    }

    /**
     * Asigna la variable numeroResolucion
     * 
     * @param numeroResolucion
     * Variable a asignar en numeroResolucion
     */
    public void setNumeroResolucion(String numeroResolucion)
    {
        this.numeroResolucion = numeroResolucion;
    }

    /**
     * Retorna la variable fechaResolucion
     * 
     * @return fechaResolucion
     */
    public Date getFechaResolucion()
    {
        return fechaResolucion;
    }

    /**
     * Asigna la variable fechaResolucion
     * 
     * @param fechaResolucion
     * Variable a asignar en fechaResolucion
     */
    public void setFechaResolucion(Date fechaResolucion)
    {
        this.fechaResolucion = fechaResolucion;
    }

    /**
     * Retorna la variable elaboraResolucion
     * 
     * @return elaboraResolucion
     */
    public String getElaboraResolucion()
    {
        return elaboraResolucion;
    }

    /**
     * Asigna la variable elaboraResolucion
     * 
     * @param elaboraResolucion
     * Variable a asignar en elaboraResolucion
     */
    public void setElaboraResolucion(String elaboraResolucion)
    {
        this.elaboraResolucion = elaboraResolucion;
    }

    /**
     * Retorna la variable firmaResolucion
     * 
     * @return firmaResolucion
     */
    public String getFirmaResolucion()
    {
        return firmaResolucion;
    }

    /**
     * Asigna la variable firmaResolucion
     * 
     * @param firmaResolucion
     * Variable a asignar en firmaResolucion
     */
    public void setFirmaResolucion(String firmaResolucion)
    {
        this.firmaResolucion = firmaResolucion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigo()
    {
        return listacodigo;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigo(RegistroDataModelImpl listacodigo)
    {
        this.listacodigo = listacodigo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
