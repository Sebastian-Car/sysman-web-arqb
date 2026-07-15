package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.DatosbasicospqrsControladorEnum;
import com.sysman.serviciospublicos.enums.DatosbasicospqrsControladorUrlEnum;

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
 * @author jrodriguezr
 * @version 1, 26/08/2016 15:38:01 -- Modificado por jrodriguezr
 * 
 * @version 2, 18/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos, en el origen de datos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class DatosbasicospqrsControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCiudad;
    private List<Registro> listaDepartamento;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public DatosbasicospqrsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DATOSBASICOSPQRS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(DatosbasicospqrsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

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
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DatosbasicospqrsControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));

            if (("0").equals(rs.getCampos().get("NUM").toString()))
            {
                param.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                DatosbasicospqrsControladorUrlEnum.URL3655
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), param);

            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(DatosbasicospqrsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        tabla = DatosbasicospqrsControladorEnum.TABLA.getValue();
        buscarLlave();
        asignarOrigenDatos();
        Map<String, Object> key = new HashMap<>();
        key.put("COMPANIA", compania);
        cargarRegistro(key, BeanBaseDatosAcme.ACCION_INSERTAR);
        cargarListaCiudad();
        cargarListaDepartamento();
    }

    @Override
    public void asignarOrigenDatos()
    {
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        DatosbasicospqrsControladorUrlEnum.URL15084.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosbasicospqrsControladorUrlEnum.URL9050
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiudad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(DatosbasicospqrsControladorEnum.PARAM0.getValue(),
                        SessionUtil.getCompaniaIngreso().getCodigoPais());
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),
                        registro.getCampos().get("DEPARTAMENTO"));

        try
        {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DatosbasicospqrsControladorUrlEnum.URL5111
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(DatosbasicospqrsControladorEnum.PARAM0.getValue(),
                        String.valueOf(registro.getCampos().get("PAIS")));

        try
        {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DatosbasicospqrsControladorUrlEnum.URL5575
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarDepartamento()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaCiudad();
        registro.getCampos().put("CIUDAD", "");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
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
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
        registro.getCampos().put("KEY_COMPANIA", compania);
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

    public void cerrarFormulario()
    {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiudad()
    {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad)
    {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaDepartamento()
    {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento)
    {
        this.listaDepartamento = listaDepartamento;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
