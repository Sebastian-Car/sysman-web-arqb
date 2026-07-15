package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.SubconceptosControladorEnum;
import com.sysman.predial.enums.SubconceptosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author sdaza
 * @version 1, 02/06/2016 18:07:22 -- Modificado por sdaza
 * @author jcrodriguez=>Refactoring y depuracion del controlador
 * @version 2, 18/07/2017
 */
@ManagedBean
@ViewScoped

public class SubconceptosControlador extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private String modulo;
    private String codigoPredio;
    private String nomPropietario;
    private String numeroOrden;
    private String direccionPredio;
    private String nomC1;
    private String nomC2;
    private String nomC3;
    private String nomC4;
    private String nomC13;
    private String nomC14;
    private String nomC15;
    private String nomC16;
    private String nomC17;
    private String nomC18;
    private String nomC19;
    private String nomC20;
    private String accion;
    private boolean indReserva;
    private boolean indBloq;
    private boolean indBloqCopias;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;
    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of SubconceptosControlador
     */
    public SubconceptosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBCONCEPTOS_CONTROLADOR
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
            else
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(SubconceptosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void init()
    {
        enumBase = GenericUrlEnum.IP_FACTURADOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(SubconceptosControladorEnum.CODIGOPREDIO.getValue(), codigoPredio);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), numeroOrden);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubconceptosControladorUrlEnum.URL6511
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubconceptosControladorUrlEnum.URL7257
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getUrlBeanById(
                        enumBase.getDeleteKey());

    }

    @Override
    public void abrirFormulario()
    {

        String permiteModificarFact;
        indBloq = true;
        permiteModificarFact = getParametro(SubconceptosControladorEnum.PERMITE_MANIPULAR_FACTURADOS.getValue(), true);
        permiteModificarFact = permiteModificarFact == null ? "NO"
            : permiteModificarFact;
        if ("SI".equals(permiteModificarFact))
        {
            indBloq = false;
        }
        else
        {
            indBloq = true;
        }
        String permiteAdmon;
        indBloqCopias = true;
        permiteAdmon = getParametro(SubconceptosControladorEnum.ADMINISTRADOR_INCREMENTOS_LEY.getValue(), true);
        permiteAdmon = permiteAdmon == null ? "NO" : permiteAdmon;
        if ("SI".equals(permiteAdmon))
        {
            indBloqCopias = false;
        }
        else
        {
            indBloqCopias = true;
        }

        try
        {
            nomC1 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 1);
            nomC2 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 2);
            nomC3 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 3);
            nomC4 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 4);
            nomC13 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 13);
            nomC14 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 14);
            nomC15 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 15);
            nomC16 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 16);
            nomC17 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 17);
            nomC18 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 18);
            nomC19 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 19);
            nomC20 = ejbPredialCero.consultarEncabezadoDeColumna(compania, 20);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String valueMap(Map<String, Object> mapa, String nombre)
    {

        return SysmanFunciones.validarCampoVacio(mapa, nombre) ? "" : mapa.get(nombre).toString();
    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
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
        // </CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.TOTAL.getName());
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private double convertirConcepto(String var)
    {
        return Double.parseDouble(
                        SysmanFunciones.validarCampoVacio(registro.getCampos(), var) ? "0" : registro.getCampos().get(var).toString());
    }

    private double calcularTotal()
    {
        return convertirConcepto(SubconceptosControladorEnum.C1.getValue()) + convertirConcepto(SubconceptosControladorEnum.C2.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C3.getValue()) + convertirConcepto(SubconceptosControladorEnum.C4.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C12.getValue()) + convertirConcepto(SubconceptosControladorEnum.C13.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C14.getValue()) + convertirConcepto(SubconceptosControladorEnum.C15.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C16.getValue()) + convertirConcepto(SubconceptosControladorEnum.C17.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C18.getValue()) + convertirConcepto(SubconceptosControladorEnum.C19.getValue())
            + convertirConcepto(SubconceptosControladorEnum.C20.getValue());
    }

    @Override
    public boolean actualizarAntes()
    {
        // Heredado del bean base
        registro.getCampos().put(GeneralParameterEnum.TOTAL.getName(), calcularTotal());
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // heredado del bean padre
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // heredado del bean padre
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // heredado del bean padre
        return true;
    }

    @Override
    public void removerCombos()
    {
        // Heredado del bean
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Heredado del bean
    }

    /**
     * metodos get y set
     * 
     * @return
     */

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

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

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

    public boolean isIndBloq()
    {
        return indBloq;
    }

    public void setIndBloq(boolean indBloq)
    {
        this.indBloq = indBloq;
    }

    public boolean isIndBloqCopias()
    {
        return indBloqCopias;
    }

    public void setIndBloqCopias(boolean indBloqCopias)
    {
        this.indBloqCopias = indBloqCopias;
    }

    public String getNomC1()
    {
        return nomC1;
    }

    public void setNomC1(String nomC1)
    {
        this.nomC1 = nomC1;
    }

    public String getNomC2()
    {
        return nomC2;
    }

    public void setNomC2(String nomC2)
    {
        this.nomC2 = nomC2;
    }

    public String getNomC3()
    {
        return nomC3;
    }

    public void setNomC3(String nomC3)
    {
        this.nomC3 = nomC3;
    }

    public String getNomC4()
    {
        return nomC4;
    }

    public void setNomC4(String nomC4)
    {
        this.nomC4 = nomC4;
    }

    public String getNomC13()
    {
        return nomC13;
    }

    public void setNomC13(String nomC13)
    {
        this.nomC13 = nomC13;
    }

    public String getNomC14()
    {
        return nomC14;
    }

    public void setNomC14(String nomC14)
    {
        this.nomC14 = nomC14;
    }

    public String getNomC15()
    {
        return nomC15;
    }

    public void setNomC15(String nomC15)
    {
        this.nomC15 = nomC15;
    }

    public String getNomC16()
    {
        return nomC16;
    }

    public void setNomC16(String nomC16)
    {
        this.nomC16 = nomC16;
    }

    public String getNomC17()
    {
        return nomC17;
    }

    public void setNomC17(String nomC17)
    {
        this.nomC17 = nomC17;
    }

    public String getNomC18()
    {
        return nomC18;
    }

    public void setNomC18(String nomC18)
    {
        this.nomC18 = nomC18;
    }

    public String getNomC19()
    {
        return nomC19;
    }

    public void setNomC19(String nomC19)
    {
        this.nomC19 = nomC19;
    }

    public String getNomC20()
    {
        return nomC20;
    }

    public void setNomC20(String nomC20)
    {
        this.nomC20 = nomC20;
    }
}
