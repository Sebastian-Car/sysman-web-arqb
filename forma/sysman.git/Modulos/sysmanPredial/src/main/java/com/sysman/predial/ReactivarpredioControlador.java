package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.ReactivarpredioControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author NGOMEZ
 * @version 1, 27/05/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el código: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 *
 * @author spina
 * @version 3, 14/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class ReactivarpredioControlador extends BeanBaseModal
{
    private final String compania;
    private String nOrden;
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
     * Creates a new instance of ReactivarpredioControlador
     */
    public ReactivarpredioControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        fechaResolucion = new Date();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.REACTIVARPREDIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ReactivarpredioControlador.class.getName())
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
        cargarListacodigo();
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
    public void cargarListacodigo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReactivarpredioControladorUrlEnum.URL4843
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAnular()
    {
        if (validarFechaActivacion())
        {
            // <CODIGO_DESARROLLADO>
            try
            {
                JsfUtil.agregarMensajeInformativo(
                                ejbPredialCuatro.getReactivarPredio(compania,
                                                SessionUtil.getNivelUsuario(
                                                                SessionUtil.getModulo()),
                                                codigo,
                                                numeroResolucion,
                                                fechaResolucion,
                                                elaboraResolucion,
                                                firmaResolucion,
                                                SessionUtil.getUser()
                                                                .getCodigo()));

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            inicializarValores();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Reinicia los valores del formulario una vez finalizado el proceso de reactivacion del predio
     */
    private void inicializarValores()
    {
        codigo = numeroResolucion = elaboraResolucion = firmaResolucion = null;
    }

    /**
     * Valida que la fecha de resolucion que se ingresa en el formulario sea mayor a la fecha en la que se anulo el predio
     *
     * @return si la fecha de activacion es mayor a la fecha de anulacion
     */
    private boolean validarFechaActivacion()
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        boolean rta = true;
        Registro rs;
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReactivarpredioControladorUrlEnum.URL4844
                                                                            .getValue())
                                            .getUrl(), param));

            if (!SysmanFunciones.validarCampoVacio(rs.getCampos(),
                            "FECHABORRADO"))
            {
                Date fechaBorrado = (Date) rs.getCampos().get("FECHABORRADO");
                if (!fechaBorrado.after(fechaResolucion))
                {
                    rta = true;
                }
                else
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2900"));
                    rta = false;
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                        .toString().length() > 15)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB143"));
        }
        else
        {
            codigo = String.valueOf(registroAux.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));
            nOrden = String.valueOf(
                            registroAux.getCampos().get("NUMERO_ORDEN"));
        }
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNumeroResolucion()
    {
        return numeroResolucion;
    }

    public void setNumeroResolucion(String numeroResolucion)
    {
        this.numeroResolucion = numeroResolucion;
    }

    public Date getFechaResolucion()
    {
        return fechaResolucion;
    }

    public void setFechaResolucion(Date fechaResolucion)
    {
        this.fechaResolucion = fechaResolucion;
    }

    public String getElaboraResolucion()
    {
        return elaboraResolucion;
    }

    public void setElaboraResolucion(String elaboraResolucion)
    {
        this.elaboraResolucion = elaboraResolucion;
    }

    public String getFirmaResolucion()
    {
        return firmaResolucion;
    }

    public void setFirmaResolucion(String firmaResolucion)
    {
        this.firmaResolucion = firmaResolucion;
    }

    public String getnOrden()
    {
        return nOrden;
    }

    public void setnOrden(String nOrden)
    {
        this.nOrden = nOrden;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigo()
    {
        return listacodigo;
    }

    public void setListacodigo(RegistroDataModelImpl listacodigo)
    {
        this.listacodigo = listacodigo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
