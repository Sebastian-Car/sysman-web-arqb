package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.InversionrvsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 18/05/2016
 * @version 2, 12/04/2017, Refactorizaci�n MZANGUNA
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class InversionrvsControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String nombreR;
    private boolean bloqueadoCodigo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listatipoinversion;
    private List<Registro> listaintermediaria;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaOrigenRecursos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public InversionrvsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INVERSIONRVS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                SessionUtil.removeSessionVar("rid");

            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(InversionrvsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaOrigenRecursos();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatipoinversion();
        cargarListaintermediaria();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.INVERSIONESRV;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatipoinversion()
    {
        try
        {
            listatipoinversion = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            InversionrvsControladorUrlEnum.URL3588
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e)
        {

            Logger.getLogger(InversionrvsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaintermediaria()
    {
        try
        {
            listaintermediaria = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            InversionrvsControladorUrlEnum.URL3790
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));
        }
        catch (SystemException e)
        {
            Logger.getLogger(InversionrvsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaOrigenRecursos()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InversionrvsControladorUrlEnum.URL4186
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaOrigenRecursos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaOrigenRecursos(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ORIGENRECURSOS",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        (int) registroAux.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
        nombreR = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirbtnorigenRecursos()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid" };
        Object[] valores = { css };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FUENTERECURSOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
        precargarRegistro();
        // <CODIGO_DESARROLLADO>
        if (css != null)
        {
            Map<String, Object> parametrosUnico = new HashMap<>();
            parametrosUnico.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get("ORIGENRECURSOS"));

            try
            {
                nombreR = listaOrigenRecursos.getRegistroUnico(parametrosUnico)
                                .getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();

            }
            catch (SystemException e)
            {

                Logger.getLogger(InversionrvsControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }
            bloqueadoCodigo = true;

        }
        else
        {

            nombreR = " ";
            registro.getCampos().put("VI_VALORUNITARIO", "0");
            registro.getCampos().put("VI_VALORNOMGLOBAL", "0");
            registro.getCampos().put("VI_NUMERO", "0");
            registro.getCampos().put("RF_NUMERO", "0");
            registro.getCampos().put("RF_VALORNOMGLOBAL", "0");
            registro.getCampos().put("RF_VALORUNITARIO", "0");
            bloqueadoCodigo = false;

        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if ("i".equals(accion))
        {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put("MONEDA", "COP");

        }
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
        if ("m".equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().remove("KEY_CODIGO");
            registro.getCampos().remove("KEY_COMPANIA");
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

    // <SET_GET_ATRIBUTOS>
    public String getNombreR()
    {
        return nombreR;
    }

    public void setNombreR(String nombreR)
    {
        this.nombreR = nombreR;
    }

    public boolean isBloqueadoCodigo()
    {
        return bloqueadoCodigo;
    }

    public void setBloqueadoCodigo(boolean bloqueadoCodigo)
    {
        this.bloqueadoCodigo = bloqueadoCodigo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListatipoinversion()
    {
        return listatipoinversion;
    }

    public void setListatipoinversion(List<Registro> listatipoinversion)
    {
        this.listatipoinversion = listatipoinversion;
    }

    public List<Registro> getListaintermediaria()
    {
        return listaintermediaria;
    }

    public void setListaintermediaria(List<Registro> listaintermediaria)
    {
        this.listaintermediaria = listaintermediaria;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaOrigenRecursos()
    {
        return listaOrigenRecursos;
    }

    public void setListaOrigenRecursos(
        RegistroDataModelImpl listaOrigenRecursos)
    {
        this.listaOrigenRecursos = listaOrigenRecursos;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
