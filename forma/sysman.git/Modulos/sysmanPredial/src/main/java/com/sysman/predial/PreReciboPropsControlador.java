package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.PreReciboPropsControladorEnum;
import com.sysman.predial.enums.PreReciboPropsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author sdaza
 * @version 1, 26/07/2016
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author ybecerra
 * @version 3, 15/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class PreReciboPropsControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String codigoPredio;
    private String nitPropietario;
    private String nomPropietario;
    private boolean indMil;
    private boolean indMilCien;
    private String anoFin;
    private String fechaExpedicion;
    private String fechaLimPago;
    private String nitConsulta;
    private String nomConsulta;
    private boolean indVerDg;
    private String totalFacturar;
    private boolean verMil;
    private boolean verMilCien;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaanofin;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listatxtNit;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaPrerecibopredprop;
    private List<Registro> listaPrerecibodetprop;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private String trabajaLeyMil;
    private String trabajaLeyMilCien;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSubpreReciboPredProp;
    private Registro registroSubpreReciboDetProp;
    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public PreReciboPropsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PRE_RECIBO_PROPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubpreReciboPredProp = new Registro(
                            new HashMap<String, Object>());
            registroSubpreReciboDetProp = new Registro(
                            new HashMap<String, Object>());
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
                nitPropietario = (String) parametrosEntrada
                                .get("nitPropietario");
                nomPropietario = (String) parametrosEntrada
                                .get("nomPropietario");
                nitConsulta = nitPropietario;
                nomConsulta = nomPropietario;
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PreReciboPropsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatxtNit();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaanofin();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaPrerecibopredprop();
        cargarListaPrerecibodetprop();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaPrerecibopredprop = null;
        listaPrerecibodetprop = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = "";
        indVerDg = false;
        totalFacturar = "0.0";
        asignarOrigenDatos();
        iniciarListas();
        iniciarListasSubNulo();
        iniciarListasSub();

        actualizarTotales();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    public void cargarListaPrerecibopredprop()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PreReciboPropsControladorEnum.PARAM0.getValue(),
                            nitConsulta);
            param.put(PreReciboPropsControladorEnum.PARAM1.getValue(),
                            nomConsulta);

            listaPrerecibopredprop = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PreReciboPropsControladorUrlEnum.URL6331
                                                                            .getValue())
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.IP_USUARIOS_PREDIAL
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cargarListaPrerecibodetprop()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaPrerecibodetprop = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PreReciboPropsControladorUrlEnum.URL8156
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.IP_USUARIOS_PREDIAL
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanofin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        try
        {
            listaanofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PreReciboPropsControladorUrlEnum.URL9270
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void cargarListatxtNit()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PreReciboPropsControladorUrlEnum.URL10648
                                                        .getValue());
        listatxtNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilatxtNit(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        nitPropietario = ((BigInteger) registroAux.getCampos().get("NIT"))
                        .toString();
        nomPropietario = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        nitConsulta = nitPropietario;
        nomConsulta = nomPropietario;
        cargarListaPrerecibopredprop();
        actualizarTotales();
        indVerDg = true;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentacion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdCalcular()
    {
        // necesario en la vista
    }

    public void oprimirCmdVerDetalle(Registro reg, int indice)
    { // indice
      // llamado
      // en
      // la
      // vista
      // <CODIGO_DESARROLLADO>
        String[] campos = { "codPredio", "codigoPredio", "nitPropietario",
                            "nomPropietario", "rid" };
        Object[] valores = { (String) reg.getCampos().get("CODIGO"),
                             codigoPredio, nitPropietario, nomPropietario,
                             rid };

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.FRMDETDEUDAPROPS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubPrerecibopredprop()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void editarRegSubPrerecibopredprop(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            if (predioConDeuda(reg.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString()))
            {
                reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
                reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
                reg.getCampos().remove(
                                GeneralParameterEnum.NUMERO_ORDEN.getName());
                reg.getCampos().remove(
                                GeneralParameterEnum.DIRECCION.getName());
                reg.getCampos().remove("AVALUO_ANO");
                reg.getCampos().remove("TRPCOD");
                reg.getCampos().remove("PAGO_ANO");
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PreReciboPropsControladorUrlEnum.URL19016
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1025"));
            }

        }
        catch (SystemException ex)
        {
            Logger.getLogger(PreReciboPropsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaPrerecibopredprop();
            cargarListaPrerecibodetprop();
            actualizarTotales();
            cargarListaanofin();
        }
    }

    public void eliminarRegSubPrerecibopredprop(Registro reg)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEdicionPrerecibopredprop()
    {
        cargarListaPrerecibopredprop();
        cargarListaPrerecibodetprop();
    }

    public void agregarRegistroSubPrerecibodetprop()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void editarRegSubPrerecibodetprop(RowEditEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void eliminarRegSubPrerecibodetprop(Registro reg)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEdicionPrerecibodetprop()
    {
        cargarListaPrerecibodetprop();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String ruta = "/usuariospredial.sysman";
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>

    }

    public void aceptarValidar()
    {
        // usado en la vista
    }

    public void cancelarValidar()
    {

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PreReciboPropsControladorUrlEnum.URL15659
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        Parameter parameter = new Parameter();
        parameter.setFields(fields);
        try
        {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));

        cargarListaPrerecibodetprop();
    }

    public void actualizarTotales()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        Registro regAux;
        try
        {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PreReciboPropsControladorUrlEnum.URL19692
                                                                            .getValue())
                                            .getUrl(), param));

            if (regAux != null)
            {
                totalFacturar = regAux.getCampos().get("TFACTURAR") == null
                    ? "0.0"
                    : regAux.getCampos().get("TFACTURAR").toString();
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public boolean predioConDeuda(String predio)
    {
        boolean rta = true;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        predio);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        Registro regDeuda;
        try
        {
            regDeuda = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PreReciboPropsControladorUrlEnum.URL16480
                                                                            .getValue())
                                            .getUrl(), param));

            if (regDeuda != null)
            {
                if (Integer.parseInt(regDeuda.getCampos().get("ANOSFACT")
                                .toString()) > 0)
                {
                    rta = true;
                }
                else
                {
                    rta = false;
                }
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return rta;
    }

    public void cargarParametros()
    {
        try
        {
            tomarFechas();
            trabajaLeyMil = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TRABAJA LEY 1066/2006", modulo,
                                            new Date(), true), "NO");
            String indicadorMil = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "VALOR PREDETERMINADO 3RA OPCION LEY 1066/2006",
                                            modulo, new Date(), true), "0");

            if ("0".equals(indicadorMil))
            {
                indMil = false;
            }
            else
            {
                indMil = true;
            }

            trabajaLeyMilCien = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TRABAJA LEY 1175/2007", modulo,
                                            new Date(), true), "NO");

            String indicadorMilCien = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "VALOR PREDETERMINADO INDICADOR LEY 1175/2007",
                                            modulo, new Date(), true), "0");

            if ("0".equals(indicadorMilCien))
            {
                indMilCien = false;
            }
            else
            {
                indMilCien = true;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void tomarFechas()

    {
        try
        {
            fechaExpedicion = SysmanFunciones
                            .convertirAFechaCadena(new Date());
            fechaLimPago = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FECHA LIMITE DE PAGO", modulo,
                                            new Date(), true),
                            SysmanFunciones.convertirAFechaCadena(new Date()));
        }
        catch (ParseException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        cargarParametros();
        // <CODIGO_DESARROLLADO>
        /*
           */
        if ("SI".equals(trabajaLeyMil))
        {
            verMil = true;
        }
        else
        {
            verMil = false;
        }
        if ("SI".equals(trabajaLeyMilCien))
        {
            verMilCien = true;
        }
        else
        {
            verMilCien = false;
        }

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

    // <SET_GET_ATRIBUTOS>
    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public String getNitPropietario()
    {
        return nitPropietario;
    }

    public void setNitPropietario(String nitPropietario)
    {
        this.nitPropietario = nitPropietario;
    }

    public String getNomPropietario()
    {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario)
    {
        this.nomPropietario = nomPropietario;
    }

    public boolean isIndMil()
    {
        return indMil;
    }

    public void setIndMil(boolean indMil)
    {
        this.indMil = indMil;
    }

    public boolean isIndMilCien()
    {
        return indMilCien;
    }

    public void setIndMilCien(boolean indMilCien)
    {
        this.indMilCien = indMilCien;
    }

    public String getAnoFin()
    {
        return anoFin;
    }

    public void setAnoFin(String anoFin)
    {
        this.anoFin = anoFin;
    }

    public String getFechaExpedicion()
    {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(String fechaExpedicion)
    {
        this.fechaExpedicion = fechaExpedicion;
    }

    public String getFechaLimPago()
    {
        return fechaLimPago;
    }

    public void setFechaLimPago(String fechaLimPago)
    {
        this.fechaLimPago = fechaLimPago;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public boolean isIndVerDg()
    {
        return indVerDg;
    }

    public void setIndVerDg(boolean indVerDg)
    {
        this.indVerDg = indVerDg;
    }

    public String getTotalFacturar()
    {
        return totalFacturar;
    }

    public void setTotalFacturar(String totalFacturar)
    {
        this.totalFacturar = totalFacturar;
    }

    public boolean isVerMil()
    {
        return verMil;
    }

    public void setVerMil(boolean verMil)
    {
        this.verMil = verMil;
    }

    public boolean isVerMilCien()
    {
        return verMilCien;
    }

    public void setVerMilCien(boolean verMilCien)
    {
        this.verMilCien = verMilCien;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanofin()
    {
        return listaanofin;
    }

    public void setListaanofin(List<Registro> listaanofin)
    {
        this.listaanofin = listaanofin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListatxtNit()
    {
        return listatxtNit;
    }

    public void setListatxtNit(RegistroDataModelImpl listatxtNit)
    {
        this.listatxtNit = listatxtNit;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaPrerecibopredprop()
    {
        return listaPrerecibopredprop;
    }

    public void setListaPrerecibopredprop(
        List<Registro> listaPrerecibopredprop)
    {
        this.listaPrerecibopredprop = listaPrerecibopredprop;
    }

    public List<Registro> getListaPrerecibodetprop()
    {
        return listaPrerecibodetprop;
    }

    public void setListaPrerecibodetprop(List<Registro> listaPrerecibodetprop)
    {
        this.listaPrerecibodetprop = listaPrerecibodetprop;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSubpreReciboPredProp()
    {
        return registroSubpreReciboPredProp;
    }

    public void setRegistroSubpreReciboPredProp(
        Registro registroSubpreReciboPredProp)
    {
        this.registroSubpreReciboPredProp = registroSubpreReciboPredProp;
    }

    public Registro getRegistroSubpreReciboDetProp()
    {
        return registroSubpreReciboDetProp;
    }

    public void setRegistroSubpreReciboDetProp(
        Registro registroSubpreReciboDetProp)
    {
        this.registroSubpreReciboDetProp = registroSubpreReciboDetProp;
    }
    // </SET_GET_ADICIONALES>
}
