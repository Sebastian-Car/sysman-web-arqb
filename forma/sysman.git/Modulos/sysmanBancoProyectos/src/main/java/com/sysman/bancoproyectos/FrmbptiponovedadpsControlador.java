package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmbptiponovedadpsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmbptiponovedadpsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author dmaldonado
 * @version 1, 14/08/2015
 * 
 * @version 2, 14/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 */

@ManagedBean
@ViewScoped
public class FrmbptiponovedadpsControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private boolean bloqueado;

    // variable para almacenar el valor del check aplica modificacion
    private boolean aplicaModif;

    private List<Registro> listaClaseNovedad;

    public FrmbptiponovedadpsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMBPTIPONOVEDADPS_CONTROLADOR
                            .getCodigo();
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(FrmbptiponovedadpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.BPTIPONOVEDAD;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * Metodo ejecutado al cambiar el control ActualizaPlan
     * 
     * 
     */
    public void cambiarActualizaPlan()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaClaseNovedad()
    {
        return listaClaseNovedad;
    }

    public void setListaClaseNovedad(List<Registro> listaClaseNovedad)
    {
        this.listaClaseNovedad = listaClaseNovedad;
    }

    @Override
    public void iniciarListas()
    {
        cargarListaClaseNovedad();
    }

    @Override
    public void iniciarListasSub()
    {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // METODO_NO_IMPLEMENTADO
    }

    public void cargarListaClaseNovedad()
    {
        try
        {
            listaClaseNovedad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbptiponovedadpsControladorUrlEnum.URL2810
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR))
        {
            bloqueado = true;
        }
        else
        {
            bloqueado = false;
        }
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

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
        StringBuilder aux = verificarPlanIndicativo();
        if ((boolean) registro.getCampos().get("ACT_PLAN_INDI")
            && (!"VACIO".equals(aux.toString())))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2301")
                            .replace("#$mensaje$#", aux.toString()));
            registro.getCampos().put("ACT_PLAN_INDI", false);
            return false;
        }

        // guarda el valor del check aplica modificacion
        aplicaModif = (boolean) registro.getCampos()
                        .get(FrmbptiponovedadpsControladorEnum.APLICA_MODIFICACION
                                        .getValue());
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

    public StringBuilder verificarPlanIndicativo()
    {
        StringBuilder aux = new StringBuilder("");
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            List<Registro> listaPI = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmbptiponovedadpsControladorUrlEnum.URL5368
                                                                            .getValue())
                                            .getUrl(), param));
            if (listaPI.isEmpty())
            {
                aux.append("VACIO");
            }
            else
            {
                for (int i = 0; i < listaPI.size(); i++)
                {
                    aux.append(listaPI.get(i).getCampos().get("TIPOT")
                                    .toString()).append(" ")
                                    .append(listaPI.get(i).getCampos()
                                                    .get("NOMBRE").toString())
                                    .append("\n");
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return aux;
    }

    public boolean isBloqueado()
    {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado)
    {
        this.bloqueado = bloqueado;
    }

    public boolean isAplicaModif()
    {
        return aplicaModif;
    }

    public void setAplicaModif(boolean aplicaModif)
    {
        this.aplicaModif = aplicaModif;
    }

}