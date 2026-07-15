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
import com.sysman.predial.enums.TasasinterespredialsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * @version 1, 19/05/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 19/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class TasasinterespredialsControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private Map<String, Object> rid;
    private List<Registro> listaMes;
    private int anio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of TasasinterespredialsControlador
     */
    @SuppressWarnings("unchecked")
    public TasasinterespredialsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TASASINTERESPREDIALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                setRid((HashMap<String, Object>) parametros.get("ridF"));
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(TasasinterespredialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.IP_TASASINTERES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>

        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TasasinterespredialsControladorUrlEnum.URL2264
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TasasinterespredialsControladorUrlEnum.URL2265
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        anio = Integer.valueOf(registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), null);
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        anio = Integer.valueOf(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString());
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.MES.getName(),
                        null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TASADIARIA", 0);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TASAMENSUAL", 0);
        cargarListaMes();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdCompuesto()
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "rid" };
        String[] valores = { (String) registro.getCampos().get("RID") };
        SessionUtil.redireccionar("/tasainteresfechas.sysman", campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put("TASADIARIA", 0);
        registro.getCampos().put("TASAMENSUAL", 0);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                        SysmanFunciones.mes(new Date()));
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
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo no implementado
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public int getAnio()
    {
        return anio;
    }

    public void setAnio(int anio)
    {
        this.anio = anio;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

}
