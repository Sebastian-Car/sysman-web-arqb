package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.VehiculoaccesoriosControladorEnum;
import com.sysman.mantenimientoactivos.enums.VehiculoaccesoriosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
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

/**
 *
 * @author ngomez
 * @version 1, 23/09/2015
 * @author jcrodriguez, Refactoring y depuracion
 * @version 2, 18/08/2017
 */
@ManagedBean
@ViewScoped
public class VehiculoaccesoriosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private String registroauxNombre;
    private String registroauxSerie;
    private String tituloPlaca;
    private String placa;
    private HashMap<String, Object> rid;
    private String opcion;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private String auxiliar;
    private int indice;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of VehiculoaccesoriosControlador
     */

    public VehiculoaccesoriosControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.VEHICULOACCESORIOS_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        try
        {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
                opcion = validarCadena(parametrosEntrada, VehiculoaccesoriosControladorEnum.OPCION.getValue().toLowerCase());
                placa = validarCadena(parametrosEntrada, GeneralParameterEnum.PLACA.getName());
            }
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(VehiculoaccesoriosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.VEHICULOACCESORIOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        registro.getCampos().put(GeneralParameterEnum.PLACA.getName(), placa);
        tituloPlaca = SysmanFunciones.concatenar(idioma.getString("TB_TB3470"), " ", placa);
        cargarListaElemento();
        cargarListaElementoE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.PLACA.getName(), placa);

    }

    public void cargarListaElemento()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VehiculoaccesoriosControladorUrlEnum.URL17912
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            VehiculoaccesoriosControladorEnum.HERRAMIENTAS.getValue()));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaElementoE()
    {

        listaElementoE = listaElemento;
    }

    private Registro serieAccesorioVehiculo(String serie)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PLACA.getName(), placa);
        param.put(GeneralParameterEnum.SERIE.getName(), serie);
        Registro regContar = null;
        try
        {
            regContar = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            VehiculoaccesoriosControladorUrlEnum.URL17925
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return regContar;
    }

    public void seleccionarFilaElemento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        if (!registroAux.getLlave().isEmpty())
        {

            Registro regContar = serieAccesorioVehiculo(registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()).toString());
            if ("0".equals(regContar.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()))
            {
                registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                                registroAux.getCampos().get(VehiculoaccesoriosControladorEnum.CODIGOELEMENTO.getValue()));
                registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                                registroAux.getCampos().get(VehiculoaccesoriosControladorEnum.NOMBRELEMENTO.getValue()));
                registro.getCampos().put(VehiculoaccesoriosControladorEnum.SERIE_ELEMENTO.getValue(),
                                registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2494"));
            }
        }

    }

    public void seleccionarFilaElementoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = validarCadena(registroAux.getCampos(), VehiculoaccesoriosControladorEnum.CODIGOELEMENTO.getValue());
        registroauxNombre = validarCadena(registroAux.getCampos(), VehiculoaccesoriosControladorEnum.NOMBRELEMENTO.getValue());
        registroauxSerie = validarCadena(registroAux.getCampos(), GeneralParameterEnum.SERIE.getName());
    }

    private boolean validarFechas()
    {
        Date fechaEntrega = (Date) registro.getCampos().get(VehiculoaccesoriosControladorEnum.FSERVICIO.getValue());
        Date fechaPerdida = (Date) registro.getCampos().get(VehiculoaccesoriosControladorEnum.FRESPONSABILIDAD.getValue());
        Date fechaReintegro = (Date) registro.getCampos().get(VehiculoaccesoriosControladorEnum.FREINTEGRO.getValue());

        if ((fechaEntrega != null && fechaPerdida != null) && (fechaEntrega.compareTo(fechaPerdida) > 0))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3474"));
            return true;
        }
        else if ((fechaEntrega != null && fechaReintegro != null) && (fechaEntrega.compareTo(fechaReintegro) > 0))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3475"));
            return true;
        }
        return false;
    }

    @Override
    public boolean insertarAntes()
    {
        if (validarFechas())
        {
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(GeneralParameterEnum.PLACA.getName(), placa);

        long aux = 0;
        StringBuilder criterio = new StringBuilder();
        criterio.append("VEHICULOACCESORIOS.COMPANIA =");
        criterio.append("''" + compania + "''");
        criterio.append(" AND VEHICULOACCESORIOS.PLACA =");
        criterio.append("''" + placa + "''");
        try
        {
            aux = ejbSysmanUtil.generarSiguienteConsecutivo(VehiculoaccesoriosControladorEnum.VEHICULOACCESORIOS.getValue(),
                            criterio.toString(),
                            GeneralParameterEnum.CODIGO.getName());
        }
        catch (SystemException ex)
        {
            Logger.getLogger(VehiculoaccesoriosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(idioma.getString("TB_TB2482"), ex.getMessage()));
        }

        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), aux);
        return true;
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", VehiculoaccesoriosControladorEnum.OPCION.getValue().toLowerCase(),
                            GeneralParameterEnum.PLACA.getName().toLowerCase() };
        Object[] valores = { rid, opcion, placa };
        SessionUtil.redireccionar(
                        "/inventarioparqueautomotor.sysman",
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFReintegroC(int rowNum)
    {
        listaInicial.getDatasource().get(indice).getCampos().put(VehiculoaccesoriosControladorEnum.FRESPONSABILIDAD.getValue(),
                        null);

    }

    public void cambiarFResponsabilidadC(int rowNum)
    {

        listaInicial.getDatasource().get(indice).getCampos().put(VehiculoaccesoriosControladorEnum.FREINTEGRO.getValue(), null);

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
    }

    @Override
    public void removerCombos()
    {
        // Metodo heredado
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        if (validarFechas())
        {
            return false;
        }
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

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }

    public String getPlaca()
    {
        return placa;
    }

    public void setPlaca(String placa)
    {
        this.placa = placa;
    }

    public RegistroDataModelImpl getListaElemento()
    {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento)
    {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE()
    {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE)
    {
        this.listaElementoE = listaElementoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getRegistroauxNombre()
    {
        return registroauxNombre;
    }

    public void setRegistroauxNombre(String registroauxNombre)
    {
        this.registroauxNombre = registroauxNombre;
    }

    public String getRegistroauxSerie()
    {
        return registroauxSerie;
    }

    public void setRegistroauxSerie(String registroauxSerie)
    {
        this.registroauxSerie = registroauxSerie;
    }

    public String getTituloPlaca()
    {
        return tituloPlaca;
    }

    public void setTituloPlaca(String tituloPlaca)
    {
        this.tituloPlaca = tituloPlaca;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

}
