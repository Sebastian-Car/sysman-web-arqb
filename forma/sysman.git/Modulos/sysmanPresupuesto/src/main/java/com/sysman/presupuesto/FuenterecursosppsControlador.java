package com.sysman.presupuesto;

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
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.FuenterecursosppsControladorEnum;
import com.sysman.presupuesto.enums.FuenterecursosppsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author acaceres
 * @version 1, 16/06/2016
 *
 * @author ybecerra
 * @version 2, 18/04/2017 Revision Sonar y Refactoring
 * 
 * @author asana
 * @version 3, 13/06/2017 Se implementa enum en formulario 
 */
@ManagedBean
@ViewScoped

public class FuenterecursosppsControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;

    private int ano;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTipo;
    private List<Registro> listaANO;
    private List<Registro> listaanioBase;
    /**
     */
    private List<Registro> listaanioDestino;
    private String anioBase;
    /**
     */
    private String anioDestino;
    /**
     */

    private boolean anioBaseVisible;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbPrepararAnoRemote ejbPrepararAno;
    @EJB
    private EjbPresupuestoCeroRemote ejbPresupuestoCero;

    /**
     * Creates a new instance of FuenterecursosppsControlador
     */
    public FuenterecursosppsControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FUENTERECURSOSPPS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FuenterecursosppsControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        tabla = GenericUrlEnum.FUENTE_RECURSOS.getTable();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaANO();
        cargarListaTipo();
        cargarListaanioBase();
        cargarListaanioDestino();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    @Override
    public void reasignarOrigen()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        FuenterecursosppsControladorEnum.PARAM0.getValue(),
                        String.valueOf(ano));
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FuenterecursosppsControladorUrlEnum.URL128
                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FuenterecursosppsControladorUrlEnum.URL119
                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FuenterecursosppsControladorUrlEnum.URL122
                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FuenterecursosppsControladorUrlEnum.URL126
                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaTipo()
    {
        try
        {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FuenterecursosppsControladorUrlEnum.URL4660
                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaANO()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaANO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FuenterecursosppsControladorUrlEnum.URL4989
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaanioBase()
    {
        listaanioBase = listaANO;
    }

    public void cargarListaanioDestino()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioBase);

        try
        {
            listaanioDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FuenterecursosppsControladorUrlEnum.URL5987
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
    public void oprimirIniciar()
    {
        // <CODIGO_DESARROLLADO>
        anioBaseVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando18()
    {
        // <CODIGO_DESARROLLADO>
        try
        {

            String usuario = SessionUtil.getUser().getCodigo();

            ejbPresupuestoCero.insertarAuxiliarenPresupuesto(compania,
                            Integer.valueOf(ano), usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarANO()
    {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control anioBase
     *
     *
     */
    public void cambiaranioBase()
    {
        // <CODIGO_DESARROLLADO>
        anioDestino = null;
        cargarListaanioDestino();

        // </CODIGO_DESARROLLADO>
    }

    public void aceptaranioBase()
    {

        // <CODIGO_DESARROLLADO>

        if ("".equals(anioBase) || (anioBase == null))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2606"));
            return;
        }
        if ("".equals(anioDestino) || (anioDestino == null))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2607"));
            return;
        }

        prepararAnoSiguiente();

        // </CODIGO_DESARROLLADO>
    }

    private void prepararAnoSiguiente()
    {
        try
        {

            ejbPrepararAno.copiarFuenteRecurso(compania,
                            Integer.parseInt(anioDestino),
                            Integer.parseInt(anioBase), compania);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
            anioBase = null;
            anioDestino = null;

        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {
            anioBaseVisible = false;
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * AnioDestino en la vista
     *
     *
     */
    public void aceptarAnioDestino()
    {

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones
                        .ano(new Date());
        anioBase = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        reasignarOrigen();
        /*
         * FR906-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name DoCmd.Restore Me.Requery End Sub
         */
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
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().remove("NOMBRETIPO");
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove("NOMBRETIPO");

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaANO()
    {
        return listaANO;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public List<Registro> getListaTipo()
    {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    public void setListaANO(List<Registro> listaANO)
    {
        this.listaANO = listaANO;
    }

    public String getAnioBase()
    {
        return anioBase;
    }

    public void setAnioBase(String anioBase)
    {
        this.anioBase = anioBase;
    }

    public String getAnioDestino()
    {
        return anioDestino;
    }

    public void setAnioDestino(String anioDestino)
    {
        this.anioDestino = anioDestino;
    }

    public boolean isAnioBaseVisible()
    {
        return anioBaseVisible;
    }

    public void setAnioBaseVisible(boolean anioBaseVisible)
    {
        this.anioBaseVisible = anioBaseVisible;
    }

    public List<Registro> getListaanioBase()
    {
        return listaanioBase;
    }

    public void setListaanioBase(List<Registro> listaanioBase)
    {
        this.listaanioBase = listaanioBase;
    }

    public List<Registro> getListaanioDestino()
    {
        return listaanioDestino;
    }

    public void setListaanioDestino(List<Registro> listaanioDestino)
    {
        this.listaanioDestino = listaanioDestino;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
