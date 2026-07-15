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
import com.sysman.predial.enums.TarifasControladorEnum;
import com.sysman.predial.enums.TarifasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * @version 1, 24/05/2016
 *
 * @author spina
 * @version 2, 19/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class TarifasControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private final String modulo;
    private String anio;
    private String estado;
    private String mesAdministia;
    private boolean permiteAcciones;
    private boolean porcDescuentoVisible;
    private List<Registro> listaAno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of TarifasControlador
     */
    public TarifasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TARIFAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(TarifasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.IP_TARIFAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        porcDescuentoVisible = false;
        cargarListaAno();
        abrirFormulario();
        cambiarAno();

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
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
                                                            TarifasControladorUrlEnum.URL2264
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

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        mesAdministia = service.buscarEnLista(anio,
                        GeneralParameterEnum.NUMERO.getName(),
                        TarifasControladorEnum.MESESAMNISTIA_PREDIAL.getValue(),
                        listaAno);
        reasignarOrigen();
        String aux = String.valueOf(SysmanFunciones.ano(new Date()));
        permiteAcciones = anio.equals(aux);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        try
        {
            String parametroPredial = ejbSysmanUtil.consultarParametro(compania,
                            "TOMA PORCENTAJE DESCUENTO ESPECIAL TARIFAS",
                            modulo, new Date(), true);

            if (parametroPredial == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB983"));
                return;
            }

            porcDescuentoVisible = ("SI").equals(parametroPredial);
        }
        catch (SystemException e)
        {
            Logger.getLogger(TarifasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        reasignarOrigen();
        registro.getCampos().put(TarifasControladorEnum.TRPRAN1.getValue(),
                        "0");
        registro.getCampos().put(TarifasControladorEnum.TRPRAN2.getValue(),
                        "99999999999");
        registro.getCampos().put(TarifasControladorEnum.TRPPOR.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.PORCBOMB.getValue(),
                        "0");
        registro.getCampos().put(TarifasControladorEnum.TRPALUMBRADO.getValue(),
                        "0");
        registro.getCampos().put(TarifasControladorEnum.TRPINC.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.TRPINT.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.TRPCAR.getValue(), "0");
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
        registro.getCampos().put(TarifasControladorEnum.TRPANO.getValue(),
                        anio);
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro()
    {
        registro.getCampos().put(TarifasControladorEnum.TRPRAN1.getValue(),
                        "0");
        registro.getCampos().put(TarifasControladorEnum.TRPRAN2.getValue(),
                        "99999999999");
        registro.getCampos().put(TarifasControladorEnum.TRPPOR.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.PORCBOMB.getValue(),
                        "0");
        registro.getCampos().put(TarifasControladorEnum.TRPINC.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.TRPINT.getValue(), "0");
        registro.getCampos().put(TarifasControladorEnum.TRPCAR.getValue(), "0");
    }

    // <SET_GET_ATRIBUTOS>
    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public String getMesAdministia()
    {
        return mesAdministia;
    }

    public void setMesAdministia(String mesAdministia)
    {
        this.mesAdministia = mesAdministia;
    }

    public boolean isPorcDescuentoVisible()
    {
        return porcDescuentoVisible;
    }

    public void setPorcDescuentoVisible(boolean porcDescuentoVisible)
    {
        this.porcDescuentoVisible = porcDescuentoVisible;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public boolean isPermiteAcciones()
    {
        return permiteAcciones;
    }

    public void setPermiteAcciones(boolean permiteAcciones)
    {
        this.permiteAcciones = permiteAcciones;
    }

}
