/*-
 * Reportesrips.java
 *
 * 1.0
 * 
 * 14/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 14/11/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class Reportesrips extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    //<DECLARAR_ATRIBUTOS>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean consultas;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean hospitalizacion;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean medicamentos;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean procedimientos;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean recienNacidos;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean transacciones;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean urgencias;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean usuarios;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean otrosServicios;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Boolean todos;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String terceroInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String terceroFinal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String consecutivoInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String consecutivoFinal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String facturaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String facturaFinal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String usuarioInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String usuarioFinal;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Date fechaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaterceroInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaterceroFinal;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de Reportesrips
     */
    public Reportesrips() {
        super();
        compania = SessionUtil.getCompania();
        todos=true;
        cambiartodos();
        try {
            //2128
            numFormulario = GeneralCodigoFormaEnum.FRM_REPORTES_RIPS.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas(){
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaterceroInicial();
        cargarListaterceroFinal();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub(){
        //<CARGAR_LISTAS_SUBFORM>
        //</CARGAR_LISTAS_SUBFORM>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
    }
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo(){
        //<CARGAR_LISTAS_SUBFORM_NULL>
        //</CARGAR_LISTAS_SUBFORM_NULL>
    }
    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar(){
        //      tabla="";
        //       buscarLlave();
        //       asignarOrigenDatos();


        iniciarListas();
        inicializarFiltros();
    }

    private void inicializarFiltros() {
        consecutivoInicial="0";
        consecutivoFinal="9999999999";
        facturaInicial=SysmanConstantes.DEFECTOINICIAL_STRING.toString();
        facturaFinal= "zzz";
        terceroInicial=SysmanConstantes.DEFECTOINICIAL_STRING.toString();
        terceroFinal= "zzz";
        usuarioInicial=SysmanConstantes.DEFECTOINICIAL_STRING.toString();
        usuarioFinal= "zzz";

        try {
            fechaInicial=SysmanFunciones.primeroDeMesFecha(new Date());
            fechaFinal= SysmanFunciones.ultimoDiaDate(new Date());
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
        
    }
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos="";	
    }


    //<METODOS_CARGAR_LISTA>	
    /**
     * 
     * Carga la lista listaterceroInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaterceroInicial(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4391
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaterceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
        // listaterceroInicial = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FR2128_nuevo:TBCB7155","SELECT NIT, NOMBRE, SUCURSAL FROM TERCERO WHERE COMPANIA = getCompany() ORDER BY NIT",true,"NIT");
    }
    /**
     * 
     * Carga la lista listaterceroFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaterceroFinal(){
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmImportarRipsControladorUrlEnum.URL4391
                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaterceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
        // listaterceroFinal = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FR2128_nuevo:TBCB7156","SELECT NIT, NOMBRE, SUCURSAL FROM TERCERO WHERE COMPANIA = getCompany() ORDER BY NIT",true,"NIT");
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_CAMBIAR>	
    /**
     * Metodo ejecutado al cambiar el control consecutivoInicial
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarconsecutivoInicial() {
        //<CODIGO_DESARROLLADO>
        if(consecutivoFinal.equals("9999999999"))
            consecutivoFinal= consecutivoInicial;
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control usuarioInicial
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarusuarioInicial() {
        //<CODIGO_DESARROLLADO>
        if(usuarioFinal.equals("zzz"))
            usuarioFinal= usuarioInicial;
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control fechaInicial
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarfechaInicial() {
        //<CODIGO_DESARROLLADO>
        if(fechaFinal==null)
            fechaFinal= fechaInicial;
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control facturaInicial
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarfacturaInicial() {
        if(facturaFinal.equals("zzz"))
            facturaFinal=facturaInicial;
    }

    public void cambiartodos() {
        consultas=false;
        hospitalizacion=false;
        medicamentos=false;
        procedimientos=false;
        recienNacidos=false;
        transacciones=false;
        urgencias=false;
        usuarios=false;
        otrosServicios=false;
    }

    public void cambiarconsultas() {
        todos=false;
    }

    public void cambiarhospitalizacion() {
        todos=false;
    }

    public void cambiarmedicamentos() {
        todos=false;
    }

    public void cambiarprocedimientos() {
        todos=false;
    }

    public void cambiarrecienNacidos() {
        todos=false;
    }

    public void cambiartransacciones() {
        todos=false;
    }

    public void cambiarurgencias() {
        todos=false;
    }

    public void cambiarusuarios(){
        todos=false;
    }

    public void cambiarotrosServicios() {
        todos=false;
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>	
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaterceroInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaterceroInicial(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();

        if(terceroFinal.equals("zzz"))
            terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
            .toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaterceroFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaterceroFinal(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>	
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirEXCEL() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null;

        ArrayList<String> informes=new ArrayList<String>();
        ArrayList<String> hojas=new ArrayList<String>();

        try {
            Map<String, Object> reemplazos = new TreeMap<>();
            reemplazos.put("compania", compania);
            reemplazos.put("conseucutivoInicial", consecutivoInicial);
            reemplazos.put("conseucutivoFinal", consecutivoFinal);
            reemplazos.put("terceroInicial", terceroInicial);
            reemplazos.put("terceroFinal", terceroFinal);
            reemplazos.put("facturaInicial", facturaInicial);
            reemplazos.put("facturaFinal", facturaFinal);
            reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena( fechaInicial));
            reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazos.put("numIdentInicial", usuarioInicial);
            reemplazos.put("numIdentFinal", usuarioFinal);

            if(transacciones || todos) {
                informes.add(Reporteador.resuelveConsulta("800360TransaccionesRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Transacciones");
            }
            if(consultas || todos) {
                informes.add(Reporteador.resuelveConsulta("800368ConsultasRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Consultas");
            }
            if(hospitalizacion || todos) {
                informes.add(Reporteador.resuelveConsulta("800361HospitalicacionRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Hospitalicación");
            }
            if(medicamentos || todos) {
                informes.add(Reporteador.resuelveConsulta("800362MedicamentosRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Medicamentos");
            }
            if(procedimientos || todos) {
                informes.add(Reporteador.resuelveConsulta("800363ProcedimientosRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Procedimientos");
            }
            if(recienNacidos || todos) {
                informes.add(Reporteador.resuelveConsulta("800364RecienNacidosRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Recien nacidos");
            }
            if(urgencias || todos) {
                informes.add(Reporteador.resuelveConsulta("800365UrgeObservacionRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Urgencias y observación");
            }
            if(otrosServicios || todos) {
                informes.add(Reporteador.resuelveConsulta("800366OtrosServiciosRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Otros servicios");
            }
            if(usuarios || todos) {
                informes.add(Reporteador.resuelveConsulta("800367UsuariosRIPS",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos));
                hojas.add("Usuarios");
            }

            if(informes.size()==0)
                JsfUtil.agregarMensajeError("Se debe seleccionar minimo un tipo de informe");

            String[] consultas = new String[informes.size()];
            String[] hojasExcel = new String[hojas.size()];

            for (int i = 0; i < informes.size(); i++) {
                consultas[i]=informes.get(i);
                hojasExcel[i]=hojas.get(i);
            }


            archivoDescarga= JsfUtil.exportarHojaDatosStreamed(consultas, ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL, hojasExcel);
        }
        catch (IOException | DRException
                        | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }


        //</CODIGO_DESARROLLADO>
    }


    //</METODOS_BOTONES>	
    //<METODOS_SUBFORM>	
    //</METODOS_SUBFORM>	
    //<METODOS_ADICIONALES>	
    //</METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>


        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
    public void cargarRegistro() {
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarAntes(){
        //<CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean insertarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean actualizarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @return TODO VARIABLE
     */
    @Override
    public boolean eliminarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable consultas
     * 
     * @return  consultas
     */
    public Boolean getConsultas() {
        return consultas;
    }
    /**
     * Asigna la variable  consultas
     * 
     * @param  consultas
     * Variable a asignar en  consultas
     */
    public void setConsultas(Boolean consultas) {
        this.consultas = consultas;
    }
    /**
     * Retorna la variable hospitalizacion
     * 
     * @return  hospitalizacion
     */
    public Boolean getHospitalizacion() {
        return hospitalizacion;
    }
    /**
     * Asigna la variable  hospitalizacion
     * 
     * @param  hospitalizacion
     * Variable a asignar en  hospitalizacion
     */
    public void setHospitalizacion(Boolean hospitalizacion) {
        this.hospitalizacion = hospitalizacion;
    }
    /**
     * Retorna la variable medicamentos
     * 
     * @return  medicamentos
     */
    public Boolean getMedicamentos() {
        return medicamentos;
    }
    /**
     * Asigna la variable  medicamentos
     * 
     * @param  medicamentos
     * Variable a asignar en  medicamentos
     */
    public void setMedicamentos(Boolean medicamentos) {
        this.medicamentos = medicamentos;
    }
    /**
     * Retorna la variable procedimientos
     * 
     * @return  procedimientos
     */
    public Boolean getProcedimientos() {
        return procedimientos;
    }
    /**
     * Asigna la variable  procedimientos
     * 
     * @param  procedimientos
     * Variable a asignar en  procedimientos
     */
    public void setProcedimientos(Boolean procedimientos) {
        this.procedimientos = procedimientos;
    }
    /**
     * Retorna la variable recienNacidos
     * 
     * @return  recienNacidos
     */
    public Boolean getRecienNacidos() {
        return recienNacidos;
    }
    /**
     * Asigna la variable  recienNacidos
     * 
     * @param  recienNacidos
     * Variable a asignar en  recienNacidos
     */
    public void setRecienNacidos(Boolean recienNacidos) {
        this.recienNacidos = recienNacidos;
    }
    /**
     * Retorna la variable transacciones
     * 
     * @return  transacciones
     */
    public Boolean getTransacciones() {
        return transacciones;
    }
    /**
     * Asigna la variable  transacciones
     * 
     * @param  transacciones
     * Variable a asignar en  transacciones
     */
    public void setTransacciones(Boolean transacciones) {
        this.transacciones = transacciones;
    }
    /**
     * Retorna la variable urgencias
     * 
     * @return  urgencias
     */
    public Boolean getUrgencias() {
        return urgencias;
    }
    /**
     * Asigna la variable  urgencias
     * 
     * @param  urgencias
     * Variable a asignar en  urgencias
     */
    public void setUrgencias(Boolean urgencias) {
        this.urgencias = urgencias;
    }
    /**
     * Retorna la variable usuarios
     * 
     * @return  usuarios
     */
    public Boolean getUsuarios() {
        return usuarios;
    }
    /**
     * Asigna la variable  usuarios
     * 
     * @param  usuarios
     * Variable a asignar en  usuarios
     */
    public void setUsuarios(Boolean usuarios) {
        this.usuarios = usuarios;
    }
    /**
     * Retorna la variable otrosServicios
     * 
     * @return  otrosServicios
     */
    public Boolean getOtrosServicios() {
        return otrosServicios;
    }
    /**
     * Asigna la variable  otrosServicios
     * 
     * @param  otrosServicios
     * Variable a asignar en  otrosServicios
     */
    public void setOtrosServicios(Boolean otrosServicios) {
        this.otrosServicios = otrosServicios;
    }
    /**
     * Retorna la variable TODOS
     * 
     * @return  TODOS
     */
    public Boolean getTodos() {
        return todos;
    }
    /**
     * Asigna la variable  TODOS
     * 
     * @param  TODOS
     * Variable a asignar en  TODOS
     */
    public void setTodos(Boolean todos) {
        this.todos = todos;
    }
    /**
     * Retorna la variable terceroInicial
     * 
     * @return  terceroInicial
     */
    public String getTerceroInicial() {
        return terceroInicial;
    }
    /**
     * Asigna la variable  terceroInicial
     * 
     * @param  terceroInicial
     * Variable a asignar en  terceroInicial
     */
    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }
    /**
     * Retorna la variable terceroFinal
     * 
     * @return  terceroFinal
     */
    public String getTerceroFinal() {
        return terceroFinal;
    }
    /**
     * Asigna la variable  terceroFinal
     * 
     * @param  terceroFinal
     * Variable a asignar en  terceroFinal
     */
    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }
    /**
     * Retorna la variable consecutivoInicial
     * 
     * @return  consecutivoInicial
     */
    public String getConsecutivoInicial() {
        return consecutivoInicial;
    }
    /**
     * Asigna la variable  consecutivoInicial
     * 
     * @param  consecutivoInicial
     * Variable a asignar en  consecutivoInicial
     */
    public void setConsecutivoInicial(String consecutivoInicial) {
        this.consecutivoInicial = consecutivoInicial;
    }
    /**
     * Retorna la variable consecutivoFinal
     * 
     * @return  consecutivoFinal
     */
    public String getConsecutivoFinal() {
        return consecutivoFinal;
    }
    /**
     * Asigna la variable  consecutivoFinal
     * 
     * @param  consecutivoFinal
     * Variable a asignar en  consecutivoFinal
     */
    public void setConsecutivoFinal(String consecutivoFinal) {
        this.consecutivoFinal = consecutivoFinal;
    }
    /**
     * Retorna la variable facturaInicial
     * 
     * @return  facturaInicial
     */
    public String getFacturaInicial() {
        return facturaInicial;
    }
    /**
     * Asigna la variable  facturaInicial
     * 
     * @param  facturaInicial
     * Variable a asignar en  facturaInicial
     */
    public void setFacturaInicial(String facturaInicial) {
        this.facturaInicial = facturaInicial;
    }
    /**
     * Retorna la variable facturaFinal
     * 
     * @return  facturaFinal
     */
    public String getFacturaFinal() {
        return facturaFinal;
    }
    /**
     * Asigna la variable  facturaFinal
     * 
     * @param  facturaFinal
     * Variable a asignar en  facturaFinal
     */
    public void setFacturaFinal(String facturaFinal) {
        this.facturaFinal = facturaFinal;
    }
    /**
     * Retorna la variable usuarioInicial
     * 
     * @return  usuarioInicial
     */
    public String getUsuarioInicial() {
        return usuarioInicial;
    }
    /**
     * Asigna la variable  usuarioInicial
     * 
     * @param  usuarioInicial
     * Variable a asignar en  usuarioInicial
     */
    public void setUsuarioInicial(String usuarioInicial) {
        this.usuarioInicial = usuarioInicial;
    }
    /**
     * Retorna la variable usuarioFinal
     * 
     * @return  usuarioFinal
     */
    public String getUsuarioFinal() {
        return usuarioFinal;
    }
    /**
     * Asigna la variable  usuarioFinal
     * 
     * @param  usuarioFinal
     * Variable a asignar en  usuarioFinal
     */
    public void setUsuarioFinal(String usuarioFinal) {
        this.usuarioFinal = usuarioFinal;
    }
    /**
     * Retorna la variable fechaInicial
     * 
     * @return  fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }
    /**
     * Asigna la variable  fechaInicial
     * 
     * @param  fechaInicial
     * Variable a asignar en  fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }
    /**
     * Retorna la variable fechaFinal
     * 
     * @return  fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }
    /**
     * Asigna la variable  fechaFinal
     * 
     * @param  fechaFinal
     * Variable a asignar en  fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaterceroInicial
     * 
     * @return listaterceroInicial
     */
    public RegistroDataModelImpl getListaterceroInicial() {
        return listaterceroInicial;
    }
    /**
     * Asigna la lista listaterceroInicial
     * 
     * @param listaterceroInicial
     * Variable a asignar en  listaterceroInicial
     */
    public void setListaterceroInicial(RegistroDataModelImpl listaterceroInicial) {
        this.listaterceroInicial = listaterceroInicial;
    }
    /**
     * Retorna la lista listaterceroFinal
     * 
     * @return listaterceroFinal
     */
    public RegistroDataModelImpl getListaterceroFinal() {
        return listaterceroFinal;
    }
    /**
     * Asigna la lista listaterceroFinal
     * 
     * @param listaterceroFinal
     * Variable a asignar en  listaterceroFinal
     */
    public void setListaterceroFinal(RegistroDataModelImpl listaterceroFinal) {
        this.listaterceroFinal = listaterceroFinal;
    }
    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES>	
    //</SET_GET_ADICIONALES>
}
