/*-
 * InterfacenominaControlador.java
 *
 * 1.0
 *
 * 23/06/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.nomina.enums.InterfacenominaControladorEnum;
import com.sysman.nomina.enums.InterfacenominaControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador para generación de plano
 *
 * @version 1.0, 23/06/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class InterfacenominaControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
    
     */
    private int ano = Integer.parseInt(SessionUtil.getSessionVar("anioNomina").toString());
    /**
    
     */
    private int mes = Integer.parseInt(SessionUtil.getSessionVar("mesNomina").toString());
    /**
    
     */
    private int periodo = Integer.parseInt(SessionUtil.getSessionVar("periodoNomina").toString());
    private int proceso = Integer.parseInt(SessionUtil.getSessionVar("procesoNomina").toString());
    private boolean ckManejaCentros = true;
    private boolean ckUnicoComprobante = false;
    private boolean ckAuxTercero = false;
    private String tipo = "NOM";
    private int numeroComp;
    private int opcionPlano = 1;
    private Date fechaInterface = new Date();
    private double porcentajeContrib = 0;
    private String cuentaDbt;
    private String cuentaCrd;
    private boolean patronalEmpleado = false;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
    
     */
    private List<Registro> listaAno1;
    private RegistroDataModelImpl listaMes1;
    private RegistroDataModelImpl listaPeriodo1;
    private RegistroDataModelImpl listaProceso;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbNominaOchoRemote ejbNominaOcho;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InterfacenominaControlador
     */
    public InterfacenominaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INTERFACE_NOMINA.getCodigo();
            validarPermisos();
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
        // <CARGAR_LISTA>
        cargarListaAno1();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaProceso();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno1
     *
     *
     */
    public void cargarListaAno1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put("PROCESO", proceso);
        try
        {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InterfacenominaControladorUrlEnum.URL4440.getValue())
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
     * Carga la lista listaMes1
     *
     */
    public void cargarListaMes1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put("ANO", ano);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InterfacenominaControladorUrlEnum.URL4470
                                                        .getValue());
        listaMes1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NUMERO");
    }

    /**
     *
     * Carga la lista listaPeriodo1
     *
     *
     */
    public void cargarListaPeriodo1()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InterfacenominaControladorUrlEnum.URL4430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(InterfacenominaControladorEnum.PROCESO.getValue(), proceso);
        param.put("ANO", ano);
        param.put("MES", mes);

        listaPeriodo1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "PERIODO");
    }

    /**
     *
     * Carga la lista listaProceso
     *
     *
     */
    public void cargarListaProceso()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InterfacenominaControladorUrlEnum.URL4420
                                                        .getValue());
        listaProceso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        InterfacenominaControladorEnum.PROCESO.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     *
     */
    public void oprimirAceptar()
    {
        archivoDescarga = null;

        try
        {
            String nombrePlano = "";

            if (opcionPlano == 1)
            {
                nombrePlano = "CONTABILIZAR_SYSMAN_" + numeroComp + ".txt";
            }
            else if (opcionPlano == 3)
            {
                nombrePlano = "CONTABILIZAR_PROVISIONES_SYSMAN_" + numeroComp + ".txt";
            }
            else if (ckManejaCentros)
            {
                nombrePlano = "CONTABILIZAR_CONTABLE_SYSMAN_OTROS_" + numeroComp + ".txt";
            }
            else
            {
                nombrePlano = "CONTABILIZAR_CONTABLE_SYSMAN_TOTAL_" + numeroComp + ".txt";
            }
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(ejbNominaOcho.planoContabilizarNomina(compania, opcionPlano, proceso, ano, mes, periodo,
                                            fechaInterface, tipo, BigInteger.valueOf(numeroComp),
                                            ckManejaCentros, ckAuxTercero, BigDecimal.valueOf(porcentajeContrib), cuentaDbt,
                                            cuentaCrd, ckUnicoComprobante, patronalEmpleado, SessionUtil.getUser().getCodigo())),
                            nombrePlano);
        }
        catch (JRException | SystemException | IOException e)
        {
            Logger.getLogger(InterfacenominaControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void cambiarAno1()
    {
        cargarListaMes1();
    }

    public void cambiaropcion()
    {
        try
        {
            if (opcionPlano == 1)
            {

                tipo = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "TIPO COMPROBANTE INTERFASE NOMINA", modulo,
                                                new Date(), true), "NOM")
                                .toString();

            }
            else if (opcionPlano == 3)
            {
                tipo = "CDC";
            }
            else
            {
                tipo = "NOM";
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(InterfacenominaControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaMes1
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */

    public void seleccionarFilaMes1(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        mes = Integer.parseInt(SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "0").toString());
        cargarListaPeriodo1();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPeriodo1
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPeriodo1(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        periodo = Integer.parseInt(SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODO"), "1").toString());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaProceso
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProceso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        proceso = Integer.parseInt(SysmanFunciones
                        .nvl(registroAux.getCampos().get(InterfacenominaControladorEnum.PROCESO.getValue()), "1").toString());
        cargarListaAno1();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public int getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano)
    {
        this.ano = ano;
    }

    /**
     * Retorna la variable mes
     *
     * @return mes
     */
    public int getMes()
    {
        return mes;
    }

    /**
     * Asigna la variable mes
     *
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes)
    {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public int getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(int periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable proceso
     *
     * @return proceso
     */
    public int getProceso()
    {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     *
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(int proceso)
    {
        this.proceso = proceso;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno1
     *
     * @return listaAno1
     */
    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    /**
     * Asigna la lista listaAno1
     *
     * @param listaAno1
     * Variable a asignar en listaAno1
     */
    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaMes1
     *
     * @return listaMes1
     */
    public RegistroDataModelImpl getListaMes1()
    {
        return listaMes1;
    }

    /**
     * Asigna la lista listaMes1
     *
     * @param listaMes1
     * Variable a asignar en listaMes1
     */
    public void setListaMes1(RegistroDataModelImpl listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    /**
     * Retorna la lista listaPeriodo1
     *
     * @return listaPeriodo1
     */
    public RegistroDataModelImpl getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    /**
     * Asigna la lista listaPeriodo1
     *
     * @param listaPeriodo1
     * Variable a asignar en listaPeriodo1
     */
    public void setListaPeriodo1(RegistroDataModelImpl listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    /**
     * Retorna la lista listaProceso
     *
     * @return listaProceso
     */
    public RegistroDataModelImpl getListaProceso()
    {
        return listaProceso;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Asigna la lista listaProceso
     *
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(RegistroDataModelImpl listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    public Date getFechaInterface()
    {
        return fechaInterface;
    }

    public void setFechaInterface(Date fechaInterface)
    {
        this.fechaInterface = fechaInterface;
    }

    public double getPorcentajeContrib()
    {
        return porcentajeContrib;
    }

    public void setPorcentajeContrib(double porcentajeContrib)
    {
        this.porcentajeContrib = porcentajeContrib;
    }

    public String getCuentaDbt()
    {
        return cuentaDbt;
    }

    public void setCuentaDbt(String cuentaDbt)
    {
        this.cuentaDbt = cuentaDbt;
    }

    public String getCuentaCrd()
    {
        return cuentaCrd;
    }

    public void setCuentaCrd(String cuentaCrd)
    {
        this.cuentaCrd = cuentaCrd;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public int getNumeroComp()
    {
        return numeroComp;
    }

    public void setNumeroComp(int numeroComp)
    {
        this.numeroComp = numeroComp;
    }

    public int getOpcionPlano()
    {
        return opcionPlano;
    }

    public void setOpcionPlano(int opcionPlano)
    {
        this.opcionPlano = opcionPlano;
    }

    public boolean isCkManejaCentros()
    {
        return ckManejaCentros;
    }

    public void setCkManejaCentros(boolean ckManejaCentros)
    {
        this.ckManejaCentros = ckManejaCentros;
    }

    public boolean isCkUnicoComprobante()
    {
        return ckUnicoComprobante;
    }

    public boolean isCkAuxTercero()
    {
        return ckAuxTercero;
    }

    public void setCkAuxTercero(boolean ckAuxTercero)
    {
        this.ckAuxTercero = ckAuxTercero;
    }

    public void setCkUnicoComprobante(boolean ckUnicoComprobante)
    {
        this.ckUnicoComprobante = ckUnicoComprobante;
    }

    public boolean isPatronalEmpleado()
    {
        return patronalEmpleado;
    }

    public void setPatronalEmpleado(boolean patronalEmpleado)
    {
        this.patronalEmpleado = patronalEmpleado;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
