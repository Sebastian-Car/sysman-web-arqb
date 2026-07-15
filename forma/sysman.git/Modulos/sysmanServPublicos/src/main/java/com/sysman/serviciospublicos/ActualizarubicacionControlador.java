package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ActualizarubicacionControladorEnum;
import com.sysman.serviciospublicos.enums.ActualizarubicacionControladorUrlEnum;

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

/**
 *
 * @author jguerrero
 * @version 1, 23/08/2016
 * @version 2, 15/05/2017 modificado por jcrodriguez Descripcion:*Depuracion del controlador *Refactoring y creacion de dss
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class ActualizarubicacionControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;

    /**
     * variable que almacena el estado
     */
    private boolean dialogoVisible;
    /**
     * variable que almacen el ciclo
     */
    private String ciclo;
    /**
     * variable que almacena el codigo inicial
     */
    private String codigoInicial;
    /**
     * variable que almacena el codigo final
     */
    private String codigoFinal;
    /**
     * variable que almacena la ubicacion
     */
    private String ubicacion;
    /**
     * variable que almacena la lista de ciclos
     */
    private List<Registro> listaCiclo;
    /**
     * variable que almacena la lista de codigos inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * variable que almacena la lista de codigo final
     */
    private RegistroDataModelImpl listaCodigofinal;

    /**
     * Creates a new instance of ActualizarubicacionControlador
     */
    public ActualizarubicacionControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARUBICACION_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ActualizarubicacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que se llama para cargar las listas y abrir el formulairo
     */
    @PostConstruct
    public void inicializar()
    {
        cargarListaCiclo();
        cargarListaCodigoInicial();
        cargarListaCodigofinal();
        abrirFormulario();
    }

    /**
     * meotod que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        ubicacion = "U";
        ciclo = "1";
        cambiarCiclo();
    }

    /**
     * metodo que se llama para cargar la lista de ciclos
     */
    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(ActualizarubicacionControladorUrlEnum.URL3315.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar la lista de codigo inicias
     */
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ActualizarubicacionControladorUrlEnum.URL3757.getValue());
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * metodo que se llama para cargar la lista de codigo final
     */
    public void cargarListaCodigofinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ActualizarubicacionControladorUrlEnum.URL4087.getValue());
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(ActualizarubicacionControladorEnum.CODIGOINICIAL.getValue(), codigoInicial);
        listaCodigofinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());

    }

    /**
     * metodo que se llama al oprimir el boton aceptar
     */
    public void oprimirAceptar()
    {
        dialogoVisible = true;
    }

    /**
     * metodo que se llama al cambiar o seleccionar de la lista ciclo una fila
     */
    public void cambiarCiclo()
    {
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigofinal();
    }

    /**
     * metodo que contiene la logica para actualizar la ubicacion
     */
    public void aceptarconfirmacionActualizarUbicacion()
    {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ActualizarubicacionControladorUrlEnum.URL3327.getValue());

        try
        {
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.UBICACION.getName(), ubicacion);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(ActualizarubicacionControladorEnum.CODIGOINICIAL.getValue(), codigoInicial);
            param.put(ActualizarubicacionControladorEnum.CODIGOFINAL.getValue(), codigoFinal);
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            Parameter parameter = new Parameter();
            parameter.setFields(param);
            int miRpta = requestManager.update(url.getUrl(), url.getMetodo(), parameter);

            dialogoVisible = false;
            ciclo = null;
            codigoInicial = null;
            codigoFinal = null;
            ubicacion = null;

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(ActualizarubicacionControladorEnum.TB_TB3157.getValue())
                                            .replace("s$cantidad$s", String.valueOf(miRpta)));
            abrirFormulario();

        }
        catch (SystemException e)
        {
            Logger.getLogger(ActualizarubicacionControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama para seleccionar un registro de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()).toString();
        cargarListaCodigofinal();
    }

    /**
     * metodo que se llama para seleccionar un registro de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCodigofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()).toString();
    }

    /**
     * metodos get y set
     * 
     * @return
     */
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

    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigofinal()
    {
        return listaCodigofinal;
    }

    public void setListaCodigofinal(RegistroDataModelImpl listaCodigofinal)
    {
        this.listaCodigofinal = listaCodigofinal;
    }

    public String getUbicacion()
    {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion)
    {
        this.ubicacion = ubicacion;
    }

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

}
