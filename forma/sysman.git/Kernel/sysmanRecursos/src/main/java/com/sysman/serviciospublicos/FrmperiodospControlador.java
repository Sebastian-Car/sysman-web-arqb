/*-
 * FrmperiodospControlador.java
 *
 * 1.0
 *
 * 12/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.FrmperiodospControladorEnum;
import com.sysman.serviciospublicos.enums.FrmperiodospControladorUrlEnum;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Controlador para pedir el periodo de las tarifas a ingresar
 *
 * @version 1.0, 12/01/2017
 * @author mzanguna
 * @version 2.0, 11/05/2017 modificado por jcrodriguez Descripcion:*depuracion del controlador *Creacion de Dss *Revision de sonar
 */
@ManagedBean
@ViewScoped
public class FrmperiodospControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Año que se va a consultar
     */
    private Integer ano;
    /**
     * Periodo que se va a consultar en la tarifa
     */
    private String periodo;

    /**
     * año actual para validar que sea en el unico para ingresar o modificar las tarifas
     */
    private Integer ultimoAno;
    /**
     * Periodo actual para validar que sea en el unico en el cual se puedan ingresar o modificar las tarifas
     */
    private String ultimoPeriodo;

    /**
     * variable que almacena una lista de peridos
     */
    private List<Registro> listaMes;
    /**
     * variable que almacena una lista de años
     */
    private List<Registro> listaAno;

    /**
     * Crea una nueva instancia de FrmperiodospControlador
     */
    public FrmperiodospControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPERIODOSP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            ano = new GregorianCalendar().get(Calendar.YEAR);

        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        cargarListaAno();

        cargarListaMes();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmperiodospControladorUrlEnum.URL6125.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));

            if (rs != null)
            {
                String per = rs.getCampos()
                                .get(FrmperiodospControladorEnum.PER.getValue())
                                .toString();

                if (per.length() == 6)
                {
                    String anio = per.substring(0, 4);
                    ano = Integer.valueOf(anio);
                    ultimoAno = Integer.valueOf(anio);
                    periodo = per.substring(4, 6);
                    ultimoPeriodo = per.substring(4, 6);

                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaMes
     */
    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmperiodospControladorUrlEnum.URL5556
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaAno
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmperiodospControladorUrlEnum.URL6161
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     * Boton para abrir la pagina de tarifas
     *
     */
    public void oprimirAceptar()
    {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        periodo);
        parametros.put(FrmperiodospControladorEnum.ANIO.getValue(), ano);
        parametros.put(FrmperiodospControladorEnum.ULTIMOPERIODOS.getValue(),
                        ultimoPeriodo);
        parametros.put(FrmperiodospControladorEnum.ULTIMOANIO.getValue(),
                        ultimoAno);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        FrmperiodospControladorEnum.FORMULARIO1257.getValue());
        direccionador.setParametros(parametros);
        direccionador.getRuta();
        RequestContext.getCurrentInstance()
                        .closeDialog(direccionador);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     *
     * Boton para cerrar el modal
     *
     */
    public void oprimirCancelar()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado al cambiar el control Ano
     *
     */
    public void cambiarAno()
    {
        periodo = "";
        cargarListaMes();
    }

    /**
     * metodos get y set
     */
    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public Integer getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(Integer ano)
    {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public Integer getUltimoAno()
    {
        return ultimoAno;
    }

    public String getUltimoPeriodo()
    {
        return ultimoPeriodo;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

}
