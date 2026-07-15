/*-
 * FrmfechasuspensionsControlador.java
 *
 * 1.0
 *
 * 28/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.FrmfechasuspensionsControladorEnum;
import com.sysman.serviciospublicos.enums.FrmfechasuspensionsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para ejecutar el procedimiento de actualializacion a la tabla SP_USUARIO
 *
 * @version 1.0, 28/09/2016
 * @author ybecerra
 *
 * @version 2.0, 25/05/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejb
 */

@ManagedBean
@ViewScoped
public class FrmfechasuspensionsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante que contiene el texto compania utilizado para filtar en el origenDeDatos del formulario
     */
    private final String compania;

    /**
     * Constante que contiene el numero de aplicacion por el cual se ingresa al formulario, se llama al invocar a Acciones.getParametro
     */
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usuado para hacer visible la columna USUARIO_POSTERGA del formulario
     */
    private boolean visibleUsuario = false;

    /**
     * Atributo usado para haver visible la columna FECHA_POSTERGA del formulario
     */
    private boolean visibleFecha = false;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Alamcena el valor del parametro "ciclo" recibido del formulario PedirCiclo
     */
    private String ciclo;

    /**
     * Almacena el valor del parametro "parametro" recibido del formulario PedirCiclo
     */
    private String parametroPeriodoAtraso;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmfechasuspensionsControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public FrmfechasuspensionsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMFECHASUSPENSIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                ciclo = SysmanFunciones.nvl(parametrosEntrada.get("ciclo"), "")
                                .toString();
                parametroPeriodoAtraso = SysmanFunciones.nvl(parametrosEntrada
                                .get("parametro"), "").toString();

            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
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
        tabla = "SP_USUARIO";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(FrmfechasuspensionsControladorUrlEnum.URL3973
                                        .getValue());

        urlActualizacion = UrlServiceUtil
                        .getUrlBeanById(FrmfechasuspensionsControladorUrlEnum.URL3974
                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put("PERIODOATRASO", parametroPeriodoAtraso);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            String parPosterga = ejbSysmanUtil.consultarParametro(compania,
                            "GUARDA FECHA Y USUARIO QUE POSTERGA SUSPENSION",
                            modulo,
                            new Date(), true);

            if (parPosterga == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1668"));
                return;
            }
            else if ("SI".equals(parPosterga))
            {
                visibleUsuario = true;
                visibleFecha = true;
            }
            else
            {
                visibleFecha = false;
                visibleUsuario = false;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        try
        {
            List<Registro> listFechaLimite = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmfechasuspensionsControladorUrlEnum.URL4740
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if ((listFechaLimite != null) && !listFechaLimite.isEmpty())
            {
                Registro rsFechaLimite = listFechaLimite.get(0);

                /* Almacena el registro que viene de rsFechaLimite que es la fecha limite del ciclo */

                Date fechaLimite = (Date) rsFechaLimite.getCampos()
                                .get("FECHALIMITE2");

                /* Almacena el valor ingresado en el formulario */
                Date fechaSus = (Date) registro.getCampos()
                                .get("FECHAACTASUSPEN");

                if (fechaLimite == null || fechaLimite.before(fechaSus))
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1669"));
                    return false;
                }

                registro.getCampos().put("FECHA_POST_SUSP", new Date());
                registro.getCampos().put(
                                FrmfechasuspensionsControladorEnum.USUARIO_POST_SUSP
                                                .getValue(),
                                SessionUtil.getUser().getCodigo());

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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
        // Este metodo no ejecuta ningun evento
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Este metodo no ejecuta ningun evento
    }
    // <SET_GET_ATRIBUTOS>

    public boolean isVisibleUsuario()
    {
        return visibleUsuario;
    }

    public void setVisibleUsuario(boolean visibleUsuario)
    {
        this.visibleUsuario = visibleUsuario;
    }

    public boolean isVisibleFecha()
    {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha)
    {
        this.visibleFecha = visibleFecha;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
