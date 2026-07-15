package com.sysman.almacen;

import com.sysman.almacen.enums.FrmdevolutivopartesControladorEnum;
import com.sysman.almacen.enums.FrmdevolutivopartesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author apineda
 * @version 1, 04/02/2016
 * 
 * @version 2, 27/04/2017, pespitia :<br>
 * Refactoring.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class FrmdevolutivopartesControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.COMPANIA</code>
     */
    private final String cCompania;
    /**
     * Constante que identifica el nombre del campo SERIE
     */
    private final String campoSerie;
    private String seleccionado;
    private String elemento;
    private String nombreDevolutivo;
    private String condicion;
    private boolean comboDatos;

    private RegistroDataModelImpl listaDevolutivo;
    private RegistroDataModelImpl listaDevolutivoE;
    private String auxiliar;

    /**
     * Creates a new instance of FrmdevolutivopartesControlador
     */
    public FrmdevolutivopartesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        campoSerie = GeneralParameterEnum.SERIE.getName();
        comboDatos = false;

        cCompania = GeneralParameterEnum.COMPANIA.getName();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMDEVOLUTIVOPARTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmdevolutivopartesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = GenericUrlEnum.DEVOLUTIVO.getTable();
        elemento = seleccionado = "";

        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaDevolutivo();
        cargarListaDevolutivoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        cargado = comboDatos ? cargado : comboDatos;

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdevolutivopartesControladorUrlEnum.URL0001
                                                        .getValue());

        parametrosListado.put(cCompania, compania);

        parametrosListado.put(FrmdevolutivopartesControladorEnum.ELEMENTO_PADRE
                        .getValue(),
                        elemento);

        parametrosListado.put(FrmdevolutivopartesControladorEnum.SERIE_PADRE
                        .getValue(),
                        seleccionado);
    }

    public RegistroDataModelImpl getListaDevolutivo()
    {
        return listaDevolutivo;
    }

    public void setListaDevolutivo(RegistroDataModelImpl listaDevolutivo)
    {
        this.listaDevolutivo = listaDevolutivo;
    }

    public RegistroDataModelImpl getListaDevolutivoE()
    {
        return listaDevolutivoE;
    }

    public void setListaDevolutivoE(RegistroDataModelImpl listaDevolutivoE)
    {
        this.listaDevolutivoE = listaDevolutivoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getSeleccionado()
    {
        return seleccionado;
    }

    public void setSeleccionado(String seleccionado)
    {
        this.seleccionado = seleccionado;
    }

    public String getElemento()
    {
        return elemento;
    }

    public void setElemento(String elemento)
    {
        this.elemento = elemento;
    }

    public String getNombreDevolutivo()
    {
        return nombreDevolutivo;
    }

    public void setNombreDevolutivo(String nombreDevolutivo)
    {
        this.nombreDevolutivo = nombreDevolutivo;
    }

    public void cargarListaDevolutivo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdevolutivopartesControladorUrlEnum.URL4817
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaDevolutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoSerie);
    }

    public void cargarListaDevolutivoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdevolutivopartesControladorUrlEnum.URL4817
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        listaDevolutivoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, campoSerie);
    }

    public void seleccionarFilaDevolutivo(SelectEvent event)
    {
        comboDatos = true;
        Registro registroAux = (Registro) event.getObject();

        seleccionado = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoSerie), "")
                        .toString();

        elemento = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreDevolutivo = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();

        cargado = false;
        reasignarOrigen();
    }

    public void onRowSelectDevolutivoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(campoSerie), "")
                        .toString();

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
        registro.getCampos().put(cCompania, compania);
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    public String getCondicion()
    {
        return condicion;
    }

    public void setCondicion(String condicion)
    {
        this.condicion = condicion;
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}
