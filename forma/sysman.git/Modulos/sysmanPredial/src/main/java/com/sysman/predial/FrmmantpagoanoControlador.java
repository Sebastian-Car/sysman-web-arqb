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
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.FrmmantpagoanoControladorEnum;
import com.sysman.predial.enums.FrmmantpagoanoControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

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
 * @author dsuesca
 * @version 1, 08/06/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 05/07/2017 - refactoring dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmmantpagoanoControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodInicial;
    private RegistroDataModelImpl listaCodFinal;

    @EJB
    private EjbPredialDosRemote ejbPredialDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmmantpagoanoControlador
     */
    public FrmmantpagoanoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMMANTPAGOANO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmmantpagoanoControlador.class.getName())
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
        cargarListaCodInicial();
        cargarListaCodFinal();
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
    public void cargarListaCodInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmantpagoanoControladorUrlEnum.URL4409
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmantpagoanoControladorUrlEnum.URL5371
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmmantpagoanoControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdAceptar()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            String rta = ejbPredialDos.actualizarUltimaVigenciaCancelada(
                            compania,
                            codigoInicial, codigoFinal,
                            idioma.getString("TB_TB1204")
                                            .replace("#CODIGOINICIAL#",
                                                            codigoInicial)
                                            .replace("#CODIGOFINAL#",
                                                            codigoFinal),
                            SessionUtil.getUser().getCodigo(),
                            idioma.getString("TB_TB1205"));

            if ("MSM_PROCESO_EJECUTADO".equals(rta))
            {
                JsfUtil.agregarMensajeInformativoDialogo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            else
            {
                JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(rta));
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(FrmmantpagoanoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        codigoFinal = null;
        cargarListaCodFinal();
    }

    public void seleccionarFilaCodFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodInicial()
    {
        return listaCodInicial;
    }

    public void setListaCodInicial(RegistroDataModelImpl listaCodInicial)
    {
        this.listaCodInicial = listaCodInicial;
    }

    public RegistroDataModelImpl getListaCodFinal()
    {
        return listaCodFinal;
    }

    public void setListaCodFinal(RegistroDataModelImpl listaCodFinal)
    {
        this.listaCodFinal = listaCodFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
