package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.JuzgadosControladorEnum;
import com.sysman.general.enums.JuzgadosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ngomez
 * @version 1, 22/07/2015
 * @modified spina 04/04/2017 - se refactoriza para DSS y se depura
 * sonar
 * @author asana
 * @version 2, 12/06/2017 Redireccion de formulario.
 */
@ManagedBean
@ViewScoped
public class JuzgadosControlador extends BeanBaseContinuoAcmeImpl
{

    private String compania;
    private String pais;
    private String departamento;
    private Map<String, Object> rid;
    private int indice;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;

    /**
     * Creates a new instance of JuzgadosControlador
     */
    public JuzgadosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {

            numFormulario = GeneralCodigoFormaEnum.JUZGADOS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(JuzgadosControlador.class.getName())
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
        enumBase = GenericUrlEnum.JUZGADOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaPais();
        cargarListaDepartamento();
        cargarListaCiudad();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public List<Registro> getListaPais()
    {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais)
    {
        this.listaPais = listaPais;
    }

    public String getPais()
    {
        return pais;
    }

    public void setPais(String pais)
    {
        this.pais = pais;
    }

    public String getCiudad()
    {
        return departamento;
    }

    public void setCiudad(String ciudad)
    {
        this.departamento = ciudad;
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

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public void cargarListaPais()
    {
        try
        {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            JuzgadosControladorUrlEnum.URL3839
                                                                            .getValue())
                                            .getUrl(), null));
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
        param.put(JuzgadosControladorEnum.PAIS.getValue(), pais);

        try
        {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            JuzgadosControladorUrlEnum.URL4185
                                                                            .getValue())
                                            .getUrl(), param));
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
        param.put(JuzgadosControladorEnum.PAIS.getValue(), pais);
        param.put(JuzgadosControladorEnum.DEPARTAMENTO.getValue(),
                        departamento);

        try
        {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            JuzgadosControladorUrlEnum.URL4845
                                                                            .getValue())
                                            .getUrl(), param));
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
        pais = registro.getCampos()
                        .get(JuzgadosControladorEnum.PAIS.getValue()) == null
                            ? ""
                            : registro.getCampos()
                                            .get(JuzgadosControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamento();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamento()
    {
        // <CODIGO_DESARROLLADO>
        departamento = registro.getCampos()
                        .get(JuzgadosControladorEnum.DEPARTAMENTO
                                        .getValue()) == null ? ""
                                            : registro.getCampos()
                                                            .get(JuzgadosControladorEnum.DEPARTAMENTO
                                                                            .getValue())
                                                            .toString();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPaisC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        pais = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(JuzgadosControladorEnum.PAIS.getValue()) == null
                            ? "" : listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(JuzgadosControladorEnum.PAIS
                                                            .getValue())
                                            .toString();
        cargarListaDepartamento();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDepartamentoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        departamento = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(JuzgadosControladorEnum.DEPARTAMENTO
                                        .getValue()) == null ? ""
                                            : listaInicial.getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(JuzgadosControladorEnum.DEPARTAMENTO
                                                                            .getValue())
                                                            .toString();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion()
    {
        indice = listaInicial.getRowIndex();
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
        registro.getCampos()
                        .remove(JuzgadosControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBREDEPARTAMENTO.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBRECIUDAD.getValue());

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(JuzgadosControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBREDEPARTAMENTO.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBRECIUDAD.getValue());

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(JuzgadosControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBREDEPARTAMENTO.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBRECIUDAD.getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(JuzgadosControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBREDEPARTAMENTO.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBRECIUDAD.getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .remove(JuzgadosControladorEnum.NOMBREPAIS.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBREDEPARTAMENTO.getValue());
        registro.getCampos().remove(
                        JuzgadosControladorEnum.NOMBRECIUDAD.getValue());
        return true;
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String menuActual = SessionUtil.getMenuActual();
        if ("6020302".equals(menuActual))
        {
            Direccionador direccionador = new Direccionador();
            HashMap<String, Object> param = new HashMap<>();
            direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.EMBARGOS_CONTROLADOR.getCodigo()));
            param.put("rid", rid);
            direccionador.setParametros(param);
            SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        }
        else
        {

            SessionUtil.redireccionarMenuPermisos();
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // Metodo heredado
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro()
    {
        // Metodo heredado
    }
}
