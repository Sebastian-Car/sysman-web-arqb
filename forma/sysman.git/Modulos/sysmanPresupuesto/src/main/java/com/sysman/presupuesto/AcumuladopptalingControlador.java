package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.AcumuladopptalingControladorEnum;
import com.sysman.presupuesto.enums.AcumuladopptalingControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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
 * @author NGOMEZ
 * @version 1, 22/06/2016
 * @modified jguerrero
 * @version 2. 17/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class AcumuladopptalingControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    private String cusacionTotal;
    private String recaudoTotal;
    private String recaudoConTotal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> rid;
    private String anio;
    private String codigo;
    private String nombre;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of AcumuladopptalingControlador
     */
    @SuppressWarnings("unchecked")
    public AcumuladopptalingControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACUMULADOPPTALING_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = SysmanFunciones.nvl(parametrosEntrada.get("anio"), "")
                                .toString();
                codigo = SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()
                                                                .toLowerCase()),
                                                "")
                                .toString();
                nombre = SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()
                                                                .toLowerCase()),
                                                "")
                                .toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(AcumuladopptalingControlador.class.getName())
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
        tabla = AcumuladopptalingControladorEnum.PARAM2.getValue();

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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcumuladopptalingControladorUrlEnum.URL9759
                                                        .getValue());

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
        titulo = "ACUMULADO DE LA CUENTA: " + " " + nombre.toUpperCase();
        actualizarTotales();
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { rid, anio };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getCusacionTotal()
    {
        return cusacionTotal;
    }

    public void setCusacionTotal(String cusacionTotal)
    {
        this.cusacionTotal = cusacionTotal;
    }

    public String getRecaudoTotal()
    {
        return recaudoTotal;
    }

    public void setRecaudoTotal(String recaudoTotal)
    {
        this.recaudoTotal = recaudoTotal;
    }

    public String getRecaudoConTotal()
    {
        return recaudoConTotal;
    }

    public void setRecaudoConTotal(String recaudoConTotal)
    {
        this.recaudoConTotal = recaudoConTotal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public String getAnio()
    {
        return anio;
    }

    public Map<String, Object> getRid()
    {
        return rid;
    }

    public void setRid(Map<String, Object> rid)
    {
        this.rid = rid;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void actualizarTotales()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcumuladopptalingControladorUrlEnum.URL9287
                                                                            .getValue())
                                            .getUrl(), param));

            cusacionTotal = SysmanFunciones
                            .nvl(regAux.getCampos().get("CAUSACION"), "")
                            .toString();
            recaudoTotal = regAux.getCampos().get("RECAUDOPPTAL")
                            .toString();
            recaudoConTotal = regAux.getCampos().get("RECAUDOCONTABLE")
                            .toString();

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
}
