/*-
 * PersonalcesantiasControlador.java
 *
 * 1.0
 * 
 * 04/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PersonalcesantiasControladorEnum;
import com.sysman.nomina.enums.PersonalcesantiasControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * @version 1.0, 04/01/2018
 * @author jcrodriguez
 */
@ManagedBean
@ViewScoped
public class PersonalcesantiasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * variable cadena que almacen el empleado
     */
    private String empleado;
    /**
     * variable cadena que almacena el nombre del empleado
     */
    private String nombre;
    /**
     * variable lista que carga la lista de empleados
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * variable lista que carga la lista de empleados
     */
    private RegistroDataModelImpl listaEmpleadoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String anticipoCesantias;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String interes;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String baseCesantias;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String entregasRetiradas;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String dias;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String diaslicencias;
    /**
     * variable cadena que almacena la sumatoria de la grilla
     */
    private String parciales;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * variable booleana almacena el estado de los botones
     */
    private boolean estado;
    /**
     * variable boolean que controlado si el dialogo aparece o
     * desaparece
     */
    private boolean dialogo;

    /**
     * variable cadena que almacena la fecha incial
     */
    private Date fechaInicial;
    /**
     * variable cadena que almacena la fecha final
     */
    private Date fechaFinal;

    /**
     * Crea una nueva instancia de PersonalcesantiasControlador
     */
    public PersonalcesantiasControlador() {
        super();
        compania = SessionUtil.getCompania();
        estado = true;
        try {
            numFormulario = GeneralCodigoFormaEnum.PERSONALCESANTIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CESANTIAS;
        registro = new Registro();
        reasignarOrigen();
        buscarLlave();
        cargarListaEmpleado();
        cargarListaEmpleadoE();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(PersonalcesantiasControladorEnum.ID_DE_EMPLEADO_AUX
                                        .getValue(),
                                        registro.getCampos()
                                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                        .getName()));

    }

    /**
     * 
     * Carga la lista listaEmpleado
     */
    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalcesantiasControladorUrlEnum.URL001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RESUMEN en la vista
     *
     */
    public void oprimirRESUMEN() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF,
                        PersonalcesantiasControladorEnum.REPORTE001620
                                        .getValue(),
                        PersonalcesantiasControladorEnum.REPORTE001620
                                        .getValue());
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton listadocesempleado en la
     * vista
     * 
     */
    public void oprimirlistadocesempleado() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF,
                        PersonalcesantiasControladorEnum.REPORTE001621
                                        .getValue(),
                        PersonalcesantiasControladorEnum.REPORTE001621
                                        .getValue());
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton FECHAS en la vista
     *
     */
    public void oprimirFECHAS() {

        archivoDescarga = null;
        generarInforme(FORMATOS.PDF,
                        PersonalcesantiasControladorEnum.REPORTE001622
                                        .getValue(),
                        PersonalcesantiasControladorEnum.REPORTE001621
                                        .getValue());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cesantiasentrefechas en la
     * vista
     *
     */
    public void oprimircesantiasentrefechas() {
        dialogo = true;
    }

	public void oprimirMIGRAR() {
		Direccionador direccionador = new Direccionador();
		direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.MIGRAR_CESANTIAS_CONTROLADOR.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

    private void generarInforme(FORMATOS formato, String nombre,
        String cosulta) {
        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazos = new HashMap<>();

        reemplazos.put(GeneralParameterEnum.COMPANIA.getName().toLowerCase(),
                        compania);
        if(fechaInicial != null  && fechaFinal != null){
        	reemplazos.put(PersonalcesantiasControladorEnum.FECHAINICIAL.getValue(),
        			SysmanFunciones.formatearFecha(fechaInicial));
        	reemplazos.put(PersonalcesantiasControladorEnum.FECHAFINAL.getValue(),
        			SysmanFunciones.formatearFecha(fechaFinal));   
        }
        parametros.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        try {
            Reporteador.resuelveConsulta(cosulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombre,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleadoE() {
        listaEmpleadoE = listaEmpleado;
    }

    private String agregarFormato(Object obj) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        String variable = SysmanFunciones.nvl(obj,"0").toString();
        return new DecimalFormat("#,##0.00", dfs)
                        .format(Double.parseDouble(
                                        SysmanFunciones.nvl(variable, "0")
                                                        .toString()));
    }

    private void sumatoriaGrilla() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO.getName()));
        try {
            Registro registroSumatoria = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PersonalcesantiasControladorUrlEnum.URL002
                                                                            .getValue())
                                            .getUrl(), param));
            if (registroSumatoria != null) {
                anticipoCesantias = agregarFormato(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.CESANTIA
                                                .getValue()));
                interes = agregarFormato(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.INTERES
                                                .getValue()));

                baseCesantias = agregarFormato(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.PROMVARIABLES
                                                .getValue()));

                entregasRetiradas = agregarFormato(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.RETIRADAS
                                                .getValue()));

                dias = SysmanFunciones.nvl(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.DIAS
                                                .getValue()),
                                "0").toString();

                diaslicencias = SysmanFunciones
                                .nvl(registroSumatoria.getCampos()
                                                .get(PersonalcesantiasControladorEnum.DIAS_LICENCIAS
                                                                .getValue()),
                                                "0")
                                .toString();

                parciales = agregarFormato(registroSumatoria.getCampos()
                                .get(PersonalcesantiasControladorEnum.PARCIALES
                                                .getValue()));
            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                                        .getName()),
                                                        "")
                                        .toString());

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        SysmanFunciones
                                        .nvl(registroAux.getCampos()
                                                        .get(PersonalcesantiasControladorEnum.NOM
                                                                        .getValue()),
                                                        "")
                                        .toString());
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName())) {
            estado = false;
        }else{
        	estado= true;
        }

        reasignarOrigen();
        sumatoriaGrilla();
        cargarForma();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * entreFechas en la vista
     * 
     */
    public void aceptarentreFechas() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF,
                        PersonalcesantiasControladorEnum.REPORTE001625
                                        .getValue(),
                        PersonalcesantiasControladorEnum.REPORTE001625
                                        .getValue());
        dialogo=false;
        fechaInicial=fechaFinal=null;
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // heredado del bean base
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName())) {
            sumatoriaGrilla();
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // heredado del bean base
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // heredado del bean base
        sumatoriaGrilla();
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // heredado del bean base

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // heredado del bean base
        sumatoriaGrilla();
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // heredado del bean base
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // heredado del bean base
        sumatoriaGrilla();
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // heredado del bean base
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public RegistroDataModelImpl getListaEmpleadoE() {
        return listaEmpleadoE;
    }

    public void setListaEmpleadoE(RegistroDataModelImpl listaEmpleadoE) {
        this.listaEmpleadoE = listaEmpleadoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAnticipoCesantias() {
        return anticipoCesantias;
    }

    public void setAnticipoCesantias(String anticipoCesantias) {
        this.anticipoCesantias = anticipoCesantias;
    }

    public String getInteres() {
        return interes;
    }

    public void setInteres(String interes) {
        this.interes = interes;
    }

    public String getBaseCesantias() {
        return baseCesantias;
    }

    public void setBaseCesantias(String baseCesantias) {
        this.baseCesantias = baseCesantias;
    }

    public String getEntregasRetiradas() {
        return entregasRetiradas;
    }

    public void setEntregasRetiradas(String entregasRetiradas) {
        this.entregasRetiradas = entregasRetiradas;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getDiaslicencias() {
        return diaslicencias;
    }

    public void setDiaslicencias(String diaslicencias) {
        this.diaslicencias = diaslicencias;
    }

    public String getParciales() {
        return parciales;
    }

    public void setParciales(String parciales) {
        this.parciales = parciales;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public boolean isDialogo() {
        return dialogo;
    }

    public void setDialogo(boolean dialogo) {
        this.dialogo = dialogo;
    }

}
