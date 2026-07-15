package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.ProyectosaportesvolsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 23/05/2016 10:13:09 -- Modificado por dmaldonado
 *
 * -- Modificado por lcortes 24/03/2017, se agrega la clausula order by a la consulta del origen de datos.
 *
 * @author spina
 * @version 2, 14/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class ProyectosaportesvolsControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private String ano;

    /**
     * Creates a new instance of ProyectosaportesvolsControlador
     */
    public ProyectosaportesvolsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PROYECTOSAPORTESVOLS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();

            if (parametros != null)
            {
                ano = (String) parametros.get("ano");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(ProyectosaportesvolsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.IP_PROYECTOS_APORTESVOL;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(),
                        ano);
    }

    public void ejecutarrcCerrar()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
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
        getListaInicial().load();
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
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        SysmanConstantes.CONS_FUENTE);

        try
        {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyectosaportesvolsControladorUrlEnum.URL4843
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg == null)
            {
                JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB739"),
                                SysmanConstantes.CONS_FUENTE,
                                idioma.getString("TB_TB740"), ano, "."));
                return false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
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
    public void removerCombos()
    {
        // Metodo heredado
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }
    // <SET_GET_ATRIBUTOS>

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

}
