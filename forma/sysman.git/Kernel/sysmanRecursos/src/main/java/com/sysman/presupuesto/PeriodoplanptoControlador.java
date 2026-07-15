package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.PeriodoplanptoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author NGOMEZ
 * @version 1, 16/06/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 17/04/2017
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class PeriodoplanptoControlador extends BeanBaseModal
{
    private final String compania;
    /**
     * Atributo que almacena el codigo del modilo por
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String anio;
    /**
     * Atributo que contiene el estado del anio.
     */
    String estado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Creates a new instance of PeriodoplanptoControlador
     */
    public PeriodoplanptoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PERIODOPLANPTO_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PeriodoplanptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.YEAR));
        // </CODIGO_DESARROLLADO>
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
                                                            PeriodoplanptoControladorUrlEnum.URL2656
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
    // <METODOS_BOTONES>
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        verificarEstado();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("anio", anio);
        parametros.put("estado", estado);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        if ("C".equals(verificarEstado()))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2591").replace("#ANIO#",
                                            anio));

        }
    }

    // </METODOS_CAMBIAR>
    /**
     * Verifica que el anio tenga estado activo.
     *
     * @return retorna A si esta Activo o C si esta Cerrado
     */
    public String verificarEstado()
    {
        try
        {

            estado = ejbSysmanUtl.verificarEstadoPeriodoAnual(compania, Integer.parseInt(anio), Integer.parseInt(modulo), 1);

        }
        catch (NumberFormatException | SystemException ex)
        {

            Logger.getLogger(PeriodoplanptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        return estado;
    }
    // <METODOS_COMBOS_GRANDES>

    // </METODOS_COMBOS_GRANDES>g

    // <SET_GET_ATRIBUTOS>

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
