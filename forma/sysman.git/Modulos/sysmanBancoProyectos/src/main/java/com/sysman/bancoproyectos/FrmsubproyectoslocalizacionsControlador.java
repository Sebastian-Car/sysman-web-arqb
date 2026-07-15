package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmsubproyectoslocalizacionsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmsubproyectoslocalizacionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 28/08/2015
 * 
 * @author jcrodriguez,Refactoring, depuracion y creacion de dss
 * @version 2, 22/09/2017
 *
 */
@ManagedBean
@ViewScoped

public class FrmsubproyectoslocalizacionsControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    private int indice;
    private String codigoProy;
    private String accion;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaBarrioUbicacion;
    private String pais;
    private String departamento;
    private String ciudad;
    private boolean muestraRegistro;
    private String menuActual;
    private Map<String, Object> parametrosEntrada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * FrmsubproyectoslocalizacionsControlador
     */
    public FrmsubproyectoslocalizacionsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();

        switch (menuActual)
        {
        case "52020402":
        case "52020102":
            muestraRegistro = false;
            break;
        case "52020101":
            muestraRegistro = true;
            break;
        default:
            SessionUtil.redireccionarMenu();
            break;
        }

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMSUBPROYECTOSLOCALIZACIONS_CONTROLADOR.getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            codigoProy = validarParametroCadena(parametrosEntrada, "codigoProy");
            accion = validarParametroCadena(parametrosEntrada, "accion");
        }
        catch (SysmanException ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(FrmsubproyectoslocalizacionsControladorEnum.CODIGO_PROY.getValue(), codigoProy);

    }

    @Override
    public void asignarValoresRegistro()
    {
        // heredado del bean base
        listaInicial.load();
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.PROYECTOLOCALIZACION;
        buscarLlave();

        if (ACCION_VER.equals(accion))
        {
            muestraRegistro = false;
        }
        reasignarOrigen();
        registro = new Registro();
        cargarListaPais();
        cargarListaDepartamento();
        cargarListaCiudad();
        cargarListaBarrioUbicacion();
        abrirFormulario();
    }

    private String validarParametroCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
    }

    public void cargarListaPais()
    {
        try
        {
            listaPais = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                                            FrmsubproyectoslocalizacionsControladorUrlEnum.URL3679.getValue()).getUrl(),
                                            null));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue(), SysmanFunciones.validarVariableVacio(pais) ? "" : pais);

        try
        {
            listaDepartamento = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                                            FrmsubproyectoslocalizacionsControladorUrlEnum.URL4174.getValue()).getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCiudad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue(), SysmanFunciones.validarVariableVacio(pais) ? "" : pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), SysmanFunciones.validarVariableVacio(departamento) ? "" : departamento);
        try
        {
            listaCiudad = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                                            FrmsubproyectoslocalizacionsControladorUrlEnum.URL4829.getValue()).getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaBarrioUbicacion()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue(), SysmanFunciones.validarVariableVacio(pais) ? "" : pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), SysmanFunciones.validarVariableVacio(departamento) ? "" : departamento);
        param.put(GeneralParameterEnum.CIUDAD.getName(), SysmanFunciones.validarVariableVacio(ciudad) ? "" : ciudad);

        try
        {
            listaBarrioUbicacion = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                                            FrmsubproyectoslocalizacionsControladorUrlEnum.URL5340.getValue()).getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarPais()
    {
        // <CODIGO_DESARROLLADO>
        departamento = null;
        registro.getCampos().put(GeneralParameterEnum.DEPARTAMENTO.getName(), null);
        ciudad = null;
        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);
        pais = validarParametroCadena(registro.getCampos(), FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue());
        cargarListaDepartamento();
        cargarListaCiudad();
        cargarListaBarrioUbicacion();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento()
    {
        ciudad = null;
        registro.getCampos().put(GeneralParameterEnum.CIUDAD.getName(), null);
        departamento = validarParametroCadena(registro.getCampos(), GeneralParameterEnum.DEPARTAMENTO.getName());
        cargarListaCiudad();
        cargarListaBarrioUbicacion();
    }

    public void cambiarCiudad()
    {
        ciudad = validarParametroCadena(registro.getCampos(), GeneralParameterEnum.CIUDAD.getName());
        cargarListaBarrioUbicacion();
    }

    public void cambiarBarrioUbicacionC(int rowNum)
    {
        // heredado del bean base
    }

    public void cambiarPaisC(int rowNum)
    {

        pais = validarParametroCadena(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos(), FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue());
        cargarListaDepartamento();
        cargarListaCiudad();
        cargarListaBarrioUbicacion();
    }

    public void cambiarDepartamentoC(int rowNum)
    {

        departamento = validarParametroCadena(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos(), GeneralParameterEnum.DEPARTAMENTO.getName());
        cargarListaCiudad();
        cargarListaBarrioUbicacion();
    }

    public void cambiarCiudadC(int rowNum)
    {

        ciudad = validarParametroCadena(listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos(), GeneralParameterEnum.CIUDAD.getName());
        cargarListaBarrioUbicacion();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(FrmsubproyectoslocalizacionsControladorEnum.CODIGOPROYECTO.getValue(), codigoProy);

        StringBuilder condicion = new StringBuilder();
        condicion.append("COMPANIA = ''");
        condicion.append(compania);
        condicion.append("'' ");
        condicion.append(" AND CODIGOPROYECTO = ''");
        condicion.append(codigoProy);
        condicion.append("'' ");
        condicion.append("  AND PAIS = ''");
        condicion.append(registro.getCampos().get(FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue()));
        condicion.append("'' ");
        condicion.append(" AND DEPARTAMENTO = ''");
        condicion.append(registro.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()));
        condicion.append("'' ");
        condicion.append(" AND CIUDAD = ''");
        condicion.append(registro.getCampos().get(GeneralParameterEnum.CIUDAD.getName()));
        condicion.append("'' ");
        condicion.append(" AND BARRIO = ''");
        condicion.append(registro.getCampos().get(GeneralParameterEnum.BARRIO.getName()));
        condicion.append("'' ");
        Long consecutivo;
        try
        {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(GenericUrlEnum.PROYECTOLOCALIZACION.getTable(), condicion.toString(),
                            GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), consecutivo);
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // heredado del bean base
        return true;
    }

    private boolean validar()
    {
        return SysmanFunciones.validarCampoVacio(registro.getCampos(), FrmsubproyectoslocalizacionsControladorEnum.PAIS.getValue())
            || SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.DEPARTAMENTO.getName())
            || SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.CIUDAD.getName())
            || SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.BARRIO.getName());

    }

    @Override
    public boolean actualizarAntes()
    {

        if (validar())
        {
            JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(FrmsubproyectoslocalizacionsControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(FrmsubproyectoslocalizacionsControladorEnum.NOMBREDEP.getValue());
        registro.getCampos().remove(FrmsubproyectoslocalizacionsControladorEnum.NOMBRECIU.getValue());
        registro.getCampos().remove(FrmsubproyectoslocalizacionsControladorEnum.NOMBREBAR.getValue());

        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // heredado del bean base
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // heredado del bean base
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // heredado del bean base
        return true;
    }

    @Override
    public void removerCombos()
    {
        // heredado del bean base
    }

    public String getPais()
    {
        return pais;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    public String getDepartamento()
    {
        return departamento;
    }

    public void setDepartamento(String departamento)
    {
        this.departamento = departamento;
    }

    public String getCiudad()
    {
        return ciudad;
    }

    public void setCiudad(String ciudad)
    {
        this.ciudad = ciudad;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public List<Registro> getListaPais()
    {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais)
    {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento()
    {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento)
    {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad()
    {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad)
    {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaBarrioUbicacion()
    {
        return listaBarrioUbicacion;
    }

    public void setListaBarrioUbicacion(List<Registro> listaBarrioUbicacion)
    {
        this.listaBarrioUbicacion = listaBarrioUbicacion;
    }

    public String getCodigoProy()
    {
        return codigoProy;
    }

    public void setCodigoProy(String codigoProy)
    {
        this.codigoProy = codigoProy;
    }

    public boolean isMuestraRegistro()
    {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro)
    {
        this.muestraRegistro = muestraRegistro;
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public String getAccion()
    {
        return accion;
    }

    public void setAccion(String accion)
    {
        this.accion = accion;
    }

}
