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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.FmractualizadesviosignificativosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmactualizachapetasControladorEnum;
import com.sysman.serviciospublicos.enums.FrmactualizachapetasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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
 * @author acaceres
 * @version 1, 24/08/2016
 * 
 * @author eamaya
 * @version 2, 23/05/2017 Proceso de Refactoring, Manejo de EJBs y
 * Creacion del procedimiento PR_ACTUALIZACHAPETA
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class FrmactualizachapetasControlador extends BeanBaseModal
{
    private final String compania;
    private String ciclo;
    private String codigoInicial;
    private String codigoFinal;
    private boolean dialogoVisible;
    private RegistroDataModelImpl listaCiclo;

    private RegistroDataModelImpl listaCodigoInicial;

    private RegistroDataModelImpl listaCodigoFinal;
    @EJB
    private EjbServiciosPublicosCeroRemote ejbSPCero;

    /**
     * Creates a new instance of FrmactualizachapetasControlador
     */
    public FrmactualizachapetasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMACTUALIZACHAPETAS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmactualizachapetasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaCiclo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizachapetasControladorUrlEnum.URL2900
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     */
    public void cargarListaCodigoInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizachapetasControladorUrlEnum.URL145
                                                        .getValue());

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     */
    public void cargarListaCodigoFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(FmractualizadesviosignificativosControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmactualizachapetasControladorUrlEnum.URL178
                                                        .getValue());

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar()
    {
        dialogoVisible = true;
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // <METODOS_CAMBIAR>
    public void cambiarActualizaProceso()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void aceptarActualizaProceso()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (!validarVacios())
            {
                return;
            }

            int num = ejbSPCero.actualizarChapetas(compania, Integer.parseInt(ciclo),
                            codigoInicial,
                            codigoFinal, SessionUtil.getUser().getCodigo());
            if (num > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1354").replace("s$numero$s", String.valueOf(num)));
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3326"));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios()
    {
        boolean condicionCiclo = ciclo == null || ciclo.isEmpty();
        boolean condicionCodigoInicial = codigoInicial == null
            || codigoInicial.isEmpty();
        boolean condicionCodigoFinal = codigoFinal == null
            || codigoFinal.isEmpty();
        if (condicionCiclo || condicionCodigoInicial
            || condicionCodigoFinal)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1335"));
            return false;
        }
        return true;
    }

    public void seleccionarFilaCiclo(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
        codigoInicial = null;
        codigoFinal = null;
        codigoInicial = registroAux.getCampos().get(FrmactualizachapetasControladorEnum.CODIGOINICIAL.getValue()).toString();
        codigoFinal = registroAux.getCampos().get(FrmactualizachapetasControladorEnum.CODIGOFINAL.getValue()).toString();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();

    }

    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        FrmactualizachapetasControladorEnum.CODIGORUTA.getValue()) ? ""
                            : registroAux.getCampos().get(FrmactualizachapetasControladorEnum.CODIGORUTA.getValue()).toString();
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        FrmactualizachapetasControladorEnum.CODIGORUTA.getValue()) ? ""
                            : registroAux.getCampos().get(FrmactualizachapetasControladorEnum.CODIGORUTA.getValue()).toString();
    }
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
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

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
}
