package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TercerosAportantesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 19/11/2015
 * @modified jsforero
 * @version 2. 06/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numero de formulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped

public class TercerosAportantesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String numeroOrden;
    private String claseOrden;
    private RegistroDataModelImpl listaNIT;
    private RegistroDataModelImpl listaNITE;
    private String auxiliar;
    private List<Registro> listaclaseAportes;
    private String sucursal;
	private Map<String, Object> parametroswf;
	private String modulo;
	

    private static final String CTESUCURSAL = "SUCURSAL";

    /**
     * Creates a new instance of TercerosAportantesControlador
     */
    public TercerosAportantesControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.TERCEROS_APORTANTES_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "10");
        	}
            modulo = SessionUtil.getModulo();
            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(TercerosAportantesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TERCEROS_APORTANTES;
        numeroOrden = JsfUtil.getParametros().get("numeroOrden");
        claseOrden = JsfUtil.getParametros().get("claseOrden");
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaclaseAportes();
        cargarListaNIT();
        cargarListaNITE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.name(),
                        claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.name(),
                        numeroOrden);

    }

    public List<Registro> getListaClaseAportes() {
        return listaclaseAportes;
    }

    public void setListaClaseAportes(List<Registro> listaclaseAportes) {
        this.listaclaseAportes = listaclaseAportes;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public RegistroDataModelImpl getListaNIT() {
        return listaNIT;
    }

    public void setListaNIT(RegistroDataModelImpl listaNIT) {
        this.listaNIT = listaNIT;
    }

    public RegistroDataModelImpl getListaNITE() {
        return listaNITE;
    }

    public void setListaNITE(RegistroDataModelImpl listaNITE) {
        this.listaNITE = listaNITE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public void cargarListaclaseAportes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        UrlBean urlLista = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosAportantesControladorUrlEnum.URL4017
                                                        .getValue());
        try {
            listaclaseAportes = RegistroConverter.toListRegistro(
                            requestManager.getList(urlLista.getUrl(), param));
        }
        catch (SystemException ex) {
            Logger.getLogger(TercerosAportantesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());

        }

    }

    public void cargarListaNIT() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosAportantesControladorUrlEnum.URL4435
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNITE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosAportantesControladorUrlEnum.URL4435
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaNITE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void seleccionarFilaNIT(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NIT", registroAux.getCampos().get("NIT"));
        registro.getCampos().put(CTESUCURSAL,
                        registroAux.getCampos().get(CTESUCURSAL));
    }

    public void seleccionarFilaNITE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT").toString();
        setSucursal(registroAux.getCampos().get(CTESUCURSAL).toString());
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.name(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.name(),
                        claseOrden);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.name(),
                        numeroOrden);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.name());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.name());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR335-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * Me.Requery If Estacargado("PCONTRATO") Then
         * Forms!PContrato!Txtaportest = Me!APORTE Else
         * Forms!NovedadContrato!SubNovedadContrato.Form!Txtaportest =
         * Me!APORTE End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.name());
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.name());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.name());
    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

}
