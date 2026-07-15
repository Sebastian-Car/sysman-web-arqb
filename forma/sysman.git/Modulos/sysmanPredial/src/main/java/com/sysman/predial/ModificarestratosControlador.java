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
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.predial.enums.ModificarestratosControladorEnum;
import com.sysman.predial.enums.ModificarestratosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author lcortes
 * @version 1, 25/05/2016
 * @author jcrodriguez Refactoring y depuracion del controlador
 * @version 2, 07/07/2017
 */
@ManagedBean
@ViewScoped

public class ModificarestratosControlador extends BeanBaseModal
{
    private final String compania;
    private String codigo;
    private String nuevoEstrato;
    private String nuevoFormato;
    private String nombre;
    private String nit;
    private String estrato;
    private String formato;
    private List<Registro> listaTNuevoEstrato;
    private List<Registro> listaTNuevoFormato;
    private RegistroDataModelImpl listaTCodigo;
    @EJB
    private EjbPredialUnoRemote ejbPredialUno;

    /**
     * Creates a new instance of ModificarestratosControlador
     */
    public ModificarestratosControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.MODIFICARESTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ModificarestratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTNuevoEstrato();
        cargarListaTNuevoFormato();
        cargarListaTCodigo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
    }

    public void cargarListaTNuevoEstrato()
    {
        try
        {
            listaTNuevoEstrato = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ModificarestratosControladorUrlEnum.URL3348.getValue()).getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTNuevoFormato()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTNuevoFormato = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ModificarestratosControladorUrlEnum.URL3849.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTCodigo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ModificarestratosControladorUrlEnum.URL4284.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaTCodigo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirCmdModificar()
    {
        if (SysmanFunciones.validarVariableVacio(codigo))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB428"));
            return;
        }

        if (SysmanFunciones.validarVariableVacio(nuevoEstrato)
            || SysmanFunciones.validarVariableVacio(nuevoFormato))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB998"));
            return;
        }

        try
        {

            int res = ejbPredialUno.consultarEncabezadoDeColumna(codigo,
                            estrato,
                            nuevoEstrato,
                            formato,
                            nuevoFormato,
                            ModificarestratosControladorEnum.IP_USUARIOS_PREDIAL.getValue(),
                            ModificarestratosControladorEnum.IP_AUDITORIA.getValue(),
                            compania,
                            SessionUtil.getUser().getCodigo(),
                            idioma.getString("TB_TB2762"));

            if (res > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1004"));
                codigo = "";
                nombre = "";
                nit = "";
                estrato = "";
                formato = "";
                nuevoEstrato = "";
                nuevoFormato = "";

            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(ModificarestratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void retornarFormularioCmdModificar()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTCodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigo = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombre = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        nit = registroAux.getCampos().get(ModificarestratosControladorEnum.NIT.getValue()).toString();
        estrato = registroAux.getCampos()
                        .get(ModificarestratosControladorEnum.ESTRATO_SOCIOECONOMICO.getValue()).toString();
        formato = registroAux.getCampos().get(ModificarestratosControladorEnum.FORMATO_ESTRATO.getValue()).toString();
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNuevoEstrato()
    {
        return nuevoEstrato;
    }

    public void setNuevoEstrato(String nuevoEstrato)
    {
        this.nuevoEstrato = nuevoEstrato;
    }

    public String getNuevoFormato()
    {
        return nuevoFormato;
    }

    public void setNuevoFormato(String nuevoFormato)
    {
        this.nuevoFormato = nuevoFormato;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getNit()
    {
        return nit;
    }

    public void setNit(String nit)
    {
        this.nit = nit;
    }

    public String getEstrato()
    {
        return estrato;
    }

    public void setEstrato(String estrato)
    {
        this.estrato = estrato;
    }

    public String getFormato()
    {
        return formato;
    }

    public void setFormato(String formato)
    {
        this.formato = formato;
    }

    public List<Registro> getListaTNuevoEstrato()
    {
        return listaTNuevoEstrato;
    }

    public void setListaTNuevoEstrato(List<Registro> listaTNuevoEstrato)
    {
        this.listaTNuevoEstrato = listaTNuevoEstrato;
    }

    public List<Registro> getListaTNuevoFormato()
    {
        return listaTNuevoFormato;
    }

    public void setListaTNuevoFormato(List<Registro> listaTNuevoFormato)
    {
        this.listaTNuevoFormato = listaTNuevoFormato;
    }

    public RegistroDataModelImpl getListaTCodigo()
    {
        return listaTCodigo;
    }

    public void setListaTCodigo(RegistroDataModelImpl listaTCodigo)
    {
        this.listaTCodigo = listaTCodigo;
    }

}
