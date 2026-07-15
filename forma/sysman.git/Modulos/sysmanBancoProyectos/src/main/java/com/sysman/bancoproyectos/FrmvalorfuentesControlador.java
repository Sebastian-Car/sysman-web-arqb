
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmvalorfuentesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmvalorfuentesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodrigueza
 * @version 1, 23/09/2015
 * 
 * @author asana
 * @version 2, 22, 29/09/2017 Se realiza refactoring de controlador
 */
@ManagedBean
@ViewScoped
public class FrmvalorfuentesControlador extends BeanBaseContinuoAcmeImpl
{

    private String compania;
    private final String codigoCons;
    private String idPlan;
    private String vigenciaPlan;
    private String vigenciaMeta;
    private String auxiliar;
    private String descripcion;
    private String nombreFuente;
    private RegistroDataModelImpl listaNOMBRE;
    private RegistroDataModelImpl listaNOMBREE;
    private int indice;

    /**
     * Creates a new instance of FrmvalorfuentesControlador
     */
    public FrmvalorfuentesControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMVALORFUENTES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";

        registro = new Registro(new HashMap<String, Object>());

        Map<String, Object> parametros = SessionUtil.getFlash();

        if (parametros != null)
        {
            idPlan = parametros.get("idPlan").toString();
            vigenciaPlan = parametros.get("vigenciaPlan").toString();
            vigenciaMeta = parametros.get("vigenciaMeta").toString();
        }

        registro = new Registro();

        if (idPlan != null && vigenciaPlan != null && vigenciaMeta != null)
        {
            cargarListaNOMBRE();
            cargarListaNOMBREE();
            cargarDescripcion();
        }
        try
        {
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(FrmvalorfuentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.BP_PLAN_INDICATIVO_FUENTES;
        buscarLlave();
        reasignarOrigen();
        abrirFormulario();
    }

    public void cargarListaNOMBRE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvalorfuentesControladorUrlEnum.URL3308
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmvalorfuentesControladorEnum.PARAM1.getValue(),
                        Integer.parseInt(vigenciaMeta));

        listaNOMBRE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaNOMBREE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmvalorfuentesControladorUrlEnum.URL3308
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmvalorfuentesControladorEnum.PARAM1.getValue(),
                        Integer.parseInt(vigenciaMeta));

        listaNOMBREE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void seleccionarFilaNOMBRE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE", SysmanFunciones.nvl(registroAux.getCampos().get(codigoCons), ""));
        registro.getCampos().put("NOMBRE", SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), ""));
    }

    public void seleccionarFilaNOMBREE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(codigoCons), "").toString();

        nombreFuente = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
    }

    @Override
    public void abrirFormulario()
    {
        // NO ESTA IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("ID_PLAN", SysmanFunciones.nvlStr(idPlan, ""));
        registro.getCampos().put("VIGENCIA_PLAN", SysmanFunciones.nvlStr(vigenciaPlan, ""));
        registro.getCampos().put("VIGENCIA_META", SysmanFunciones.nvlStr(vigenciaMeta, ""));
        registro.getCampos().remove("NOMBRE");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>

        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>

        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        auxiliar = SysmanFunciones.nvl(registro.getCampos().get("NOMBRE"), "").toString();
        nombreFuente = SysmanFunciones.nvl(registro.getCampos().get("NOMBRE"), "").toString();
    }

    /**
     * Trae la descripci�n de banco de proyectos asociado a la meta
     * de producto.
     */

    private void cargarDescripcion()
    {
        Registro valordescripcion;

        try
        {
            Map<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmvalorfuentesControladorEnum.PARAM4.getValue(), idPlan);
            param.put(FrmvalorfuentesControladorEnum.PARAM5.getValue(),
                            vigenciaPlan);

            valordescripcion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmvalorfuentesControladorUrlEnum.URL3819
                                                                            .getValue())
                                            .getUrl(), param));

            descripcion = SysmanFunciones.nvl(valordescripcion.getCampos().get("DESCRIPCION_TEXTO_BP"), "").toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getIdPlan()
    {
        return idPlan;
    }

    public void setIdPlan(String idPlan)
    {
        this.idPlan = idPlan;
    }

    public String getVigenciaPlan()
    {
        return vigenciaPlan;
    }

    public void setVigenciaPlan(String vigenciaPlan)
    {
        this.vigenciaPlan = vigenciaPlan;
    }

    public String getVigenciaMeta()
    {
        return vigenciaMeta;
    }

    public void setVigenciaMeta(String vigenciaMeta)
    {
        this.vigenciaMeta = vigenciaMeta;
    }

    public RegistroDataModelImpl getListaFUENTE()
    {
        return listaNOMBRE;
    }

    public RegistroDataModelImpl getListaNOMBRE()
    {
        return listaNOMBRE;
    }

    public void setListaNOMBRE(RegistroDataModelImpl listaNOMBRE)
    {
        this.listaNOMBRE = listaNOMBRE;
    }

    public RegistroDataModelImpl getListaNOMBREE()
    {
        return listaNOMBREE;
    }

    public void setListaNOMBREE(RegistroDataModelImpl listaNOMBREE)
    {
        this.listaNOMBREE = listaNOMBREE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public String getNombreFuente()
    {
        return nombreFuente;
    }

    public void setNombreFuente(String nombreFuente)
    {
        this.nombreFuente = nombreFuente;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos().remove("VIGENCIA_PLAN");
        registro.getCampos().remove("ID_PLAN");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("VIGENCIA_META");
        registro.getCampos().remove("NOMBREFUENTE");
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("FUENTE");

    }

    @Override
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(FrmvalorfuentesControladorEnum.PARAM4.getValue(), idPlan);
        parametrosListado.put(FrmvalorfuentesControladorEnum.PARAM5.getValue(), vigenciaPlan);
        parametrosListado.put(FrmvalorfuentesControladorEnum.PARAM0.getValue(), vigenciaMeta);
        buscarUrls();
    }

    @Override
    public void asignarValoresRegistro()
    {
        // NO ESTA IMPLEMENTADO
    }
}