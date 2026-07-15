package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmmodalidadprecontratosControladorEnum;
import com.sysman.precontractual.enums.FrmmodalidadprecontratosControladorUrlEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 16/02/2016
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 29/08/2017
 */
@ManagedBean
@ViewScoped
public class FrmmodalidadprecontratosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private HashMap<String, Object> rid;
    private boolean esCreador;
    private String vigencia;

    /**
     * Creates a new instance of FrmmodalidadprecontratosControlador
     */
    public FrmmodalidadprecontratosControlador()
    {
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMMODALIDADPRECONTRATOS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                esCreador = Boolean.parseBoolean(parametrosEntrada.get("esCreador").toString());
                vigencia = parametrosEntrada.get("vigenciaPeriodo").toString();
            }
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(FrmmodalidadprecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.ES_TIPOCONTRATO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override

    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(FrmmodalidadprecontratosControladorEnum.MODALIDAD.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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

    @Override
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodalidadprecontratosControladorUrlEnum.URL162323
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodalidadprecontratosControladorUrlEnum.URL162324
                                                        .getValue());
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put("ridEstPrevios", rid);
        parametros.put("vigenciaPeriodo", vigencia);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // HEREDADO DEL BEAN BASE
    }

    @Override
    public void asignarValoresRegistro()
    {
        // HEREDADO DEL BEAN BASE
    }

    public boolean isEsCreador()
    {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador)
    {
        this.esCreador = esCreador;
    }

}
