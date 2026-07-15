package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.TiposretencionesControladorEnum;
import com.sysman.nomina.enums.TiposretencionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author cmanrique
 *
 *
 * @author jcrodriguez, Refactoring y Depuracion
 * @version 2,30/10/2017
 */
@ManagedBean
@ViewScoped

public class TiposretencionesControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private RegistroDataModelImpl listaCUENTACONTABLE;
    private RegistroDataModelImpl listaCUENTACONTABLEE;

    private RegistroDataModelImpl listaCuentaSiif;
    private RegistroDataModelImpl listaCuentaSiifE;
    private String auxiliar;

    /**
     * Creates a new instance of TiposretencionesControlador
     */
    public TiposretencionesControlador()
    {
        numFormulario = GeneralCodigoFormaEnum.TIPOSRETENCIONES_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();

        try
        {
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(TiposretencionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCUENTACONTABLE
     *
     */
    public void cargarListaCUENTACONTABLE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TiposretencionesControladorUrlEnum.URL3322
                                                        .getValue());
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.nvl(SessionUtil
                        .getSessionVar("anioNomina"), "").toString());
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCUENTACONTABLE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCuentaSiif()
    {
        listaCuentaSiif = listaCUENTACONTABLE;
    }

    public void cargarListaCuentaSiifE()
    {
        listaCuentaSiifE = listaCuentaSiif;
    }

    public void seleccionarFilaCUENTACONTABLE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(TiposretencionesControladorEnum.CUENTACONTABLE.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCUENTACONTABLEE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
    }

    public void seleccionarFilaCuentaSiif(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACONTABLESIIF", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCuentaSiifE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
    }

    /**
     *
     * Carga la lista listaCUENTACONTABLE
     */
    public void cargarListaCUENTACONTABLEE()
    {
        listaCUENTACONTABLEE = listaCUENTACONTABLE;
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.TIPO_RETENCIONES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCUENTACONTABLE();
        cargarListaCUENTACONTABLEE();

        cargarListaCuentaSiif();
        cargarListaCuentaSiifE();

        abrirFormulario();

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        // heredado del bean base
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    public RegistroDataModelImpl getListaCUENTACONTABLE()
    {
        return listaCUENTACONTABLE;
    }

    public void setListaCUENTACONTABLE(RegistroDataModelImpl listaCUENTACONTABLE)
    {
        this.listaCUENTACONTABLE = listaCUENTACONTABLE;
    }

    public RegistroDataModelImpl getListaCUENTACONTABLEE()
    {
        return listaCUENTACONTABLEE;
    }

    public void setListaCUENTACONTABLEE(RegistroDataModelImpl listaCUENTACONTABLEE)
    {
        this.listaCUENTACONTABLEE = listaCUENTACONTABLEE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaCuentaSiif()
    {
        return listaCuentaSiif;
    }

    public void setListaCuentaSiif(RegistroDataModelImpl listaCuentaSiif)
    {
        this.listaCuentaSiif = listaCuentaSiif;
    }

    public RegistroDataModelImpl getListaCuentaSiifE()
    {
        return listaCuentaSiifE;
    }

    public void setListaCuentaSiifE(RegistroDataModelImpl listaCuentaSiifE)
    {
        this.listaCuentaSiifE = listaCuentaSiifE;
    }

}
