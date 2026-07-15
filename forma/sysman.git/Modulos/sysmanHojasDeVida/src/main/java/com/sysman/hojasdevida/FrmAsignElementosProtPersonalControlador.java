/*-
 * FrmAsignElementosProtPersonalControlador.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmAsignElementosProtPersonalControladorEnum;
import com.sysman.hojasdevida.enums.FrmAsignElementosProtPersonalControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que administra la asignacion de elementos de proteccion y dotacion al personal
 *
 * @version 1.0, 28/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmAsignElementosProtPersonalControlador
                extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la dependencia del empleado
     */
    private String dependencia;

    /**
     * Atributo que almacena el codigo del cargo del empleado
     */
    private String idCargoEmpleado;

    /**
     * Atributo que almacena el codigo del empleado
     */
    private String idDeEmpleado;

    /**
     * Atributo que almacena el nombre del empleado
     */
    private String nombreEmpleado;

    private String nombreElemento;

    /**
     * Atributo que almacena el salario base del empleado
     */
    private int salarioBase = 0;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que almacena la sucursal base del empleado
     */
    private String sucursal;

    private String cedula = "";

    private Date fechaIni = new Date();
    private Date fechaFin = new Date();
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena los registros de los elementos
     */
    private RegistroDataModelImpl listaElemento;
    /**
     * Lista que almacena los registros de los elementos en la grilla
     */
    private RegistroDataModelImpl listaElementoE;
    /**
     * Lista que almacena los registros de los empleados
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * Lista que almacena los registros de los empleados en la grilla
     */
    private RegistroDataModelImpl listaEmpleadoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmAsignElementosProtPersonalControlador
     */
    public FrmAsignElementosProtPersonalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_ASIGN_ELEMENTOS_PROT_PERSONAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
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
        enumBase = GenericUrlEnum.ELEMENTO_PROTECCION_PERSONAL;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaEmpleado();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(FrmAsignElementosProtPersonalControladorEnum.CEDULA.getValue(), cedula);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaElemento
     *
     */
    public void cargarListaElemento()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsignElementosProtPersonalControladorUrlEnum.URL6214
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    /**
     *
     * Carga la lista listaElemento
     *
     */
    public void cargarListaElementoE()
    {
        listaElementoE = listaElemento;
    }

    /**
     *
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsignElementosProtPersonalControladorUrlEnum.URL7716
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     *
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleadoE()
    {
        listaEmpleadoE = listaEmpleado;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Empleado en la fila seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEmpleadoC(int rowNum)
    {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.NOMBRE_EMPLEADO
                                        .getValue(),
                                        registro
                                                        .getCampos()
                                                        .get(FrmAsignElementosProtPersonalControladorEnum.NOMBRE_EMPLEADO
                                                                        .getValue()));

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.SALARIO
                                        .getValue(),
                                        registro
                                                        .getCampos()
                                                        .get(FrmAsignElementosProtPersonalControladorEnum.SALARIO
                                                                        .getValue()));

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtInforme en la vista
     *
     *
     */
    public void oprimirBtInforme()
    {
        String reporte = "001972FOREntregaEpp";
        archivoDescarga = null;
        if (cedula.isEmpty())
        {
            JsfUtil.agregarMensajeError(idioma
                            .getString("TI_MS_ERROR_VALIDACION"));

        }
        else
        {
            try
            {
                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(GeneralParameterEnum.COMPANIA.getName()
                                .toLowerCase(), compania);
                reemplazar.put("cedulaIni", cedula);
                reemplazar.put("cedulaFin", cedula);
                reemplazar.put("fechaIni", SysmanFunciones.formatearFecha(fechaIni));
                reemplazar.put("fechaFin", SysmanFunciones.formatearFecha(fechaFin));

                Map<String, Object> parametros = new HashMap<>();
                parametros.put("PR_GETUSER", SessionUtil.getUser());
                parametros.put("PR_CARGOENC",
                                ejbSysmanUtil.consultarParametro(compania, FrmAsignElementosProtPersonalControladorEnum.PARCARGO.getValue(),
                                                SessionUtil.getModulo(), new Date(), false));

                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

            }
            catch (FileNotFoundException ex)
            {
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString(
                                                "MSM_INFORME_NO_EXISTE"),
                                " ", ex.getMessage(), " ",
                                reporte));
                logger.error(ex.getMessage(), ex);
            }
            catch (JRException | IOException | SysmanException | SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }

    }

    public void cambiarElementoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.NOMBREELEMENTO
                                        .getValue(),
                                        nombreElemento);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaElemento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElemento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ELEMENTO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()));

        registro.getCampos().put(FrmAsignElementosProtPersonalControladorEnum.NOMBREELEMENTO.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

        registro.getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.SALARIO
                                        .getValue(),
                                        salarioBase);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaElemento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaElementoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElemento = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName())
                        .toString();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        idDeEmpleado = registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                        .getName())
                        .toString();

        nombreEmpleado = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE
                                        .getName())
                        .toString();

        salarioBase = Integer.parseInt(registroAux.getCampos()
                        .get("SALARIO_BASE").toString());

        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL
                                        .getName())
                        .toString();
        cedula = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO
                                        .getName())
                        .toString();

        dependencia = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()),
                        SysmanConstantes.CONS_DEPENDENCIA).toString();

        idCargoEmpleado = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get("ID_DE_CARGO"),
                        SysmanConstantes.CONS_SUCURSAL).toString();

        reasignarOrigen();
        cargarForma();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleadoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.NOMBRE_EMPLEADO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()));

        registro.getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.SALARIO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get("SALARIO_BASE"));

        registro.getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.SUCURSAL
                                                                        .getName()));

        registro.getCampos()
                        .put("CEDULA",
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                                        .getName()));

        dependencia = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()),
                        SysmanConstantes.CONS_DEPENDENCIA).toString();

        idCargoEmpleado = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get("ID_DE_CARGO"),
                        SysmanConstantes.CONS_SUCURSAL).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        if (!validarEmpleado())
        {
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        registro.getCampos().put("ID_CARGO",
                        idCargoEmpleado);

        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        generarConsecutivo());

        registro.getCampos().remove(FrmAsignElementosProtPersonalControladorEnum.NOMBREELEMENTO.getValue());

        return true;
    }

    private Object generarConsecutivo()
    {
        long consecutivo = 0;
        try
        {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "ELEMENTO_PROTECCION_PERSONAL",
                            "COMPANIA=" + compania,
                            GeneralParameterEnum.CONSECUTIVO.getName());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    private boolean validarEmpleado()
    {
        if (SysmanFunciones.validarVariableVacio(idDeEmpleado))
        {
            JsfUtil.agregarMensajeError(idioma
                            .getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarEmpleado())
        {
            return false;
        }
        // mzanguna
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idDeEmpleado);

        registro.getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.NOMBRE_EMPLEADO
                                        .getValue(),
                                        nombreEmpleado);

        registro.getCampos()
                        .put(FrmAsignElementosProtPersonalControladorEnum.SALARIO
                                        .getValue(),
                                        salarioBase);

        registro.getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        sucursal);

        registro.getCampos()
                        .put("CEDULA",
                                        cedula);

        return true;
        // </CODIGO_DESARROLLADO>

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        return validarEmpleado();

    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(FrmAsignElementosProtPersonalControladorEnum.NOMBREELEMENTO.getValue());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public String getDependencia()
    {
        return dependencia;
    }

    public void setDependencia(String dependencia)
    {
        this.dependencia = dependencia;
    }

    public String getIdCargoEmpleado()
    {
        return idCargoEmpleado;
    }

    public void setIdCargoEmpleado(String idCargoEmpleado)
    {
        this.idCargoEmpleado = idCargoEmpleado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaElemento
     *
     * @return listaElemento
     */
    public RegistroDataModelImpl getListaElemento()
    {
        return listaElemento;
    }

    /**
     * Asigna la lista listaElemento
     *
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElemento(RegistroDataModelImpl listaElemento)
    {
        this.listaElemento = listaElemento;
    }

    /**
     * Retorna la lista listaElemento
     *
     * @return listaElemento
     */
    public RegistroDataModelImpl getListaElementoE()
    {
        return listaElementoE;
    }

    /**
     * Asigna la lista listaElemento
     *
     * @param listaElemento
     * Variable a asignar en listaElemento
     */
    public void setListaElementoE(RegistroDataModelImpl listaElementoE)
    {
        this.listaElementoE = listaElementoE;
    }

    /**
     * Retorna la lista listaEmpleado
     *
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado()
    {
        return listaEmpleado;
    }

    /**
     * Asigna la lista listaEmpleado
     *
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado)
    {
        this.listaEmpleado = listaEmpleado;
    }

    /**
     * Retorna la lista listaEmpleado
     *
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleadoE()
    {
        return listaEmpleadoE;
    }

    /**
     * Asigna la lista listaEmpleado
     *
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleadoE(RegistroDataModelImpl listaEmpleadoE)
    {
        this.listaEmpleadoE = listaEmpleadoE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getIdDeEmpleado()
    {
        return idDeEmpleado;
    }

    public void setIdDeEmpleado(String idDeEmpleado)
    {
        this.idDeEmpleado = idDeEmpleado;
    }

    public String getNombreEmpleado()
    {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado)
    {
        this.nombreEmpleado = nombreEmpleado;
    }

    public double getSalarioBase()
    {
        return salarioBase;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setSalarioBase(int salarioBase)
    {
        this.salarioBase = salarioBase;
    }

    public String getCedula()
    {
        return cedula;
    }

    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public Date getFechaIni()
    {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni)
    {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin()
    {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin)
    {
        this.fechaFin = fechaFin;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
