package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.SubavaluosControladorEnum;
import com.sysman.predial.enums.SubavaluosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @author sdaza
 * @version 4, 31/05/2016 12:23:33 -- Modificado por sdaza
 * @author jcrodriguez=>Depuracion del controlador y refactoring
 * @version 5, 18/07/2017
 */
@ManagedBean
@ViewScoped

public class SubavaluosControlador extends BeanBaseContinuoAcmeImpl
{
    private int indice;
    private final String compania;
    private String codigoPredio;
    private String numeroOrden;
    private String nomPropietario;
    private String direccionPredio;
    private String anoTarifa;
    private boolean indReserva;
    private String accion;
    private RegistroDataModelImpl listatrpcod;
    private RegistroDataModelImpl listatrpcodE;
    private String auxiliar;
    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of SubavaluosControlador
     */
    public SubavaluosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBAVALUOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                codigoPredio = valueMap(parametrosEntrada, "codigoPredio");
                numeroOrden = valueMap(parametrosEntrada, "nroOrden");
                nomPropietario = valueMap(parametrosEntrada, "nomPropietario");
                direccionPredio = valueMap(parametrosEntrada, "direccionPredio");
                indReserva = Boolean.parseBoolean(valueMap(parametrosEntrada, "indReserva"));
                accion = valueMap(parametrosEntrada, "accion");
            }

        }
        catch (Exception ex)
        {
            Logger.getLogger(SubavaluosControlador.class.getName())
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
        enumBase = GenericUrlEnum.IP_FACTURADOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        anoTarifa = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListatrpcod();
        cargarListatrpcodE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(SubavaluosControladorEnum.CODIGOPREDIO.getValue(), codigoPredio);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
    }

    public void cambiarAvaluoC(int rowNum)
    {
        // necesario en la vista
    }

    public void cambiartrpcodC(int rowNum)
    {
        // necesario en la vista
    }

    private String valueMap(Map<String, Object> mapa, String campo)
    {
        return SysmanFunciones.validarCampoVacio(mapa, campo) ? "" : mapa.get(campo).toString();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatrpcod()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(SubavaluosControladorUrlEnum.URL6511.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubavaluosControladorEnum.ANOTARIFA.getValue(), anoTarifa);

        listatrpcod = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, SubavaluosControladorEnum.TRPCOD.getValue());

    }

    public void cargarListatrpcodE()
    {
        listatrpcodE = listatrpcod;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPrediosReserva()
    {
        // <CODIGO_DESARROLLADO>
        String ruta = "/subavaluosreserva.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.setSessionVar("retornoFormulario", "retorna");
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarPreano()
    {
        registro.getCampos().put(SubavaluosControladorEnum.TRPCOD.getValue(), "");
        registro.getCampos().put(SubavaluosControladorEnum.TRPPOR.getValue(), "");
        registro.getCampos().put(SubavaluosControladorEnum.TRPRAN.getValue(), "");
        anoTarifa = registro.getCampos().get(SubavaluosControladorEnum.PREANO.getValue()).toString();
        cargarListatrpcod();
        cargarListatrpcodE();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilatrpcod(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SubavaluosControladorEnum.TRPCOD.getValue(),
                        registroAux.getCampos().get(SubavaluosControladorEnum.TRPCOD.getValue()));
        registro.getCampos().put(SubavaluosControladorEnum.TRPPOR.getValue(),
                        registroAux.getCampos().get(SubavaluosControladorEnum.TRPPOR.getValue()));
        registro.getCampos().put(SubavaluosControladorEnum.TRPRAN.getValue(),
                        registroAux.getCampos().get(SubavaluosControladorEnum.TRPRAN.getValue()));
    }

    public void seleccionarFilatrpcodE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(SubavaluosControladorEnum.TRPCOD.getValue());
        registro.getCampos().put(SubavaluosControladorEnum.TRPPOR.getValue(),
                        registroAux.getCampos().get(SubavaluosControladorEnum.TRPPOR.getValue()));
        registro.getCampos().put(SubavaluosControladorEnum.TRPRAN.getValue(),
                        registroAux.getCampos().get(SubavaluosControladorEnum.TRPRAN.getValue()));
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar()
    {
        String ruta = "/usuariospredial.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
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
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        registro.getCampos().put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);
        registro.getCampos().put(SubavaluosControladorEnum.PREIND.getValue(), "M");
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
        registro.getCampos().remove(SubavaluosControladorEnum.PREINDT.getValue());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove(SubavaluosControladorEnum.INDHIP.getValue());
        registro.getCampos().remove(SubavaluosControladorEnum.PREFEC.getValue());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_ORDEN.getName());

    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }

    // <SET_GET_ATRIBUTOS>
    public String getNomPropietario()
    {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario)
    {
        this.nomPropietario = nomPropietario;
    }

    public String getDireccionPredio()
    {
        return direccionPredio;
    }

    public void setDireccionPredio(String direccionPredio)
    {
        this.direccionPredio = direccionPredio;
    }

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public boolean isIndReserva()
    {
        return indReserva;
    }

    public void setIndReserva(boolean indReserva)
    {
        this.indReserva = indReserva;
    }

    public String getAccion()
    {
        return accion;
    }

    public void setAccion(String accion)
    {
        this.accion = accion;
    }

    public RegistroDataModelImpl getListatrpcod()
    {
        return listatrpcod;
    }

    public void setListatrpcod(RegistroDataModelImpl listatrpcod)
    {
        this.listatrpcod = listatrpcod;
    }

    public RegistroDataModelImpl getListatrpcodE()
    {
        return listatrpcodE;
    }

    public void setListatrpcodE(RegistroDataModelImpl listatrpcodE)
    {
        this.listatrpcodE = listatrpcodE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }
}
