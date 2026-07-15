package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.DnovedadcontratosControladorEnum;
import com.sysman.contratos.enums.DnovedadcontratosControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 02/12/2015
 * @version 2, 08/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 */
@ManagedBean
@ViewScoped
public class DnovedadcontratosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Constante que almacenara la cadena "CANTIDADAENTREGAR"
     */
    private final String cantidadEntregarC;

    /**
     * Constante que almacenara la cadena "CODIGOELEMENTO"
     */
    private final String codigoElementoC;

    /**
     * Constante que almacenara la cadena "SALDOPORENTREGAR"
     */
    private final String saldoPorEntregarC;

    /**
     * Constante que almacenara la cadena "VALORTOTAL"
     */
    private final String valorTotalC;

    /**
     * Constante que almacenara la cadena "VALORUNITARIO"
     */
    private final String valorUnitarioC;
    private String claseOrden;
    private String ordenDeCompra;
    private String novedad;
    private String codigo;
    private RegistroDataModelImpl listaCodigoElemento;
    private RegistroDataModelImpl listaCodigoElementoE;
    private String auxiliar;
    private String total;

    /**
     * Creates a new instance of DnovedadcontratosControlador
     */
    public DnovedadcontratosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cantidadEntregarC = "CANTIDADAENTREGAR";
        codigoElementoC = GeneralParameterEnum.CODIGOELEMENTO.getName();
        saldoPorEntregarC = "SALDOPORENTREGAR";
        valorTotalC = GeneralParameterEnum.VALORTOTAL.getName();
        valorUnitarioC = GeneralParameterEnum.VALORUNITARIO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DNOVEDADCONTRATOS_CONTROLADOR
                            .getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            claseOrden = parametrosEntrada.get("claseOrden").toString();
            ordenDeCompra = parametrosEntrada.get("ordenDeCompra").toString();
            novedad = parametrosEntrada.get("novedad").toString();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(DnovedadcontratosControlador.class.getName())
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
        enumBase = GenericUrlEnum.D_NOVEDADCONTRATO;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaCodigoElemento();
        cargarListaCodigoElementoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put(DnovedadcontratosControladorEnum.ORDENDECOMPRA
                        .getValue(), ordenDeCompra);
        parametrosListado.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

        buscarUrls();
    }

    public void calcularTotal()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param.put(DnovedadcontratosControladorEnum.ORDENDECOMPRA.getValue(), ordenDeCompra);
            param.put(GeneralParameterEnum.NOVEDAD.getName(), novedad);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DnovedadcontratosControladorUrlEnum.URL8453
                                                                            .getValue())
                                            .getUrl(), param));
            DecimalFormat dblDF = new DecimalFormat("##,###.00");
            total = dblDF.format(SysmanFunciones.nvl(rs.getCampos().get("VALORTOTAL"), 0.0));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getClaseOrden()
    {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden)
    {
        this.claseOrden = claseOrden;
    }

    public String getOrdenDeCompra()
    {
        return ordenDeCompra;
    }

    public void setOrdenDeCompra(String ordenDeCompra)
    {
        this.ordenDeCompra = ordenDeCompra;
    }

    public String getNovedad()
    {
        return novedad;
    }

    public void setNovedad(String novedad)
    {
        this.novedad = novedad;
    }

    public RegistroDataModelImpl getListaCodigoElemento()
    {
        return listaCodigoElemento;
    }

    public void setListaCodigoElemento(
        RegistroDataModelImpl listaCodigoElemento)
    {
        this.listaCodigoElemento = listaCodigoElemento;
    }

    public RegistroDataModelImpl getListaCodigoElementoE()
    {
        return listaCodigoElementoE;
    }

    public void setListaCodigoElementoE(
        RegistroDataModelImpl listaCodigoElementoE)
    {
        this.listaCodigoElementoE = listaCodigoElementoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public void cargarListaCodigoElemento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DnovedadcontratosControladorUrlEnum.URL5971
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCodigoElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoC);
    }

    public void cargarListaCodigoElementoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DnovedadcontratosControladorUrlEnum.URL5971
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCodigoElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoElementoC);
    }

    public void cambiarCantidad()
    {
        // <CODIGO_DESARROLLADO>
        Double saldoPorEntregar = registro.getCampos()
                        .get(saldoPorEntregarC) == null ? 0.0
                            : Double.parseDouble((String) registro.getCampos()
                                            .get(saldoPorEntregarC));
        Double valorUnitario = registro.getCampos().get(valorUnitarioC) == null
            ? 0.0
            : Double.parseDouble(
                            (String) registro.getCampos().get(valorUnitarioC));
        Double cantidadEntregar = registro.getCampos()
                        .get(cantidadEntregarC) == null ? 0.0
                            : Double.parseDouble((String) registro.getCampos()
                                            .get(cantidadEntregarC));

        if (cantidadEntregar > saldoPorEntregar)
        {
            registro.getCampos().remove(cantidadEntregarC);
        }
        else
        {

            registro.getCampos().put(valorTotalC,
                            cantidadEntregar * valorUnitario);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitario()
    {
        // <CODIGO_DESARROLLADO>
        Double valorUnitario = registro.getCampos().get(valorUnitarioC) == null
            ? 0.0
            : Double.parseDouble(
                            (String) registro.getCampos().get(valorUnitarioC));
        Double cantidadEntregar = registro.getCampos()
                        .get(cantidadEntregarC) == null ? 0.0
                            : Double.parseDouble((String) registro.getCampos()
                                            .get(cantidadEntregarC));

        registro.getCampos().put(valorTotalC,
                        cantidadEntregar * valorUnitario);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorTotal()
    {
        // <CODIGO_DESARROLLADO>
        Double saldoPorEntregar = registro.getCampos()
                        .get(saldoPorEntregarC) == null ? 0.0
                            : Double.parseDouble((String) registro.getCampos()
                                            .get(saldoPorEntregarC));
        Double valorTotal = registro.getCampos().get(valorTotalC) == null ? 0.0
            : Double.parseDouble(
                            (String) registro.getCampos().get(valorTotalC));

        if (valorTotal > saldoPorEntregar)
        {
            registro.getCampos().remove(valorTotalC);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        Double saldoPorEntregar = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(saldoPorEntregarC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(saldoPorEntregarC).toString());
        Double valorUnitario = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(valorUnitarioC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(valorUnitarioC).toString());
        Double cantidadEntregar = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cantidadEntregarC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(cantidadEntregarC).toString());

        if (cantidadEntregar > saldoPorEntregar)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .remove(cantidadEntregarC);
            JsfUtil.agregarMensajeInformativo("La cantidad a entregar debe ser menor que el saldo");
        }
        else
        {

            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            valorTotalC, cantidadEntregar * valorUnitario);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitarioC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        Double valorUnitario = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(valorUnitarioC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(valorUnitarioC).toString());
        Double cantidadEntregar = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cantidadEntregarC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(cantidadEntregarC).toString());

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(valorTotalC, cantidadEntregar * valorUnitario);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorTotalC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        Double saldoPorEntregar = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(saldoPorEntregarC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(saldoPorEntregarC).toString());
        Double valorTotal = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(valorTotalC) == null ? 0.0
                            : Double.parseDouble(listaInicial
                                            .getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(valorTotalC).toString());

        if (valorTotal > saldoPorEntregar)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .remove(valorTotalC);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoElemento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigoElementoC,
                        registroAux.getCampos().get(codigoElementoC));
    }

    public void seleccionarFilaCodigoElementoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoElementoC), "")
                        .toString();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        registro.getCampos().put(DnovedadcontratosControladorEnum.ORDENDECOMPRA
                        .getValue(), ordenDeCompra);
        registro.getCampos().put(GeneralParameterEnum.NOVEDAD.getName(),
                        novedad);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
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
        calcularTotal();
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
        calcularTotal();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        // Metodo que se hereda desde el bean base
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
        registro.getCampos()
                        .remove(DnovedadcontratosControladorEnum.ORDENDECOMPRA
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NOVEDAD.getName());
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo que se hereda desde el bean base

    }

    public String getTotal()
    {
        return total;
    }

    public void setTotal(String total)
    {
        this.total = total;
    }

}
