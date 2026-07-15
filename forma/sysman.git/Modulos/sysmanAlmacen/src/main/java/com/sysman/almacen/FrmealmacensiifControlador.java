/*-
 * FrmealmacensiifControlador.java
 *
 * 1.0
 * 
 * 21/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 21/10/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class FrmealmacensiifControlador extends BeanBaseDatosAcme{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Almacena la fecha contable
     */
    private Date fechaContable;
    /**
     * Almacena el tipod de documento
     */
    private String tipoDocumento;
    /**
     * Almacena el consecutivo
     */
    private String consecutivo;
    /**
     * Almacena el numero de documento
     */
    private String numeroDocumento;
    /**
     * Almacena la descripcion
     */
    private String descripcion;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmealmacensiifControlador
     */
    public FrmealmacensiifControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            //2113
            numFormulario = GeneralCodigoFormaEnum.FRM_E_ALMACEN_SIIF_CONTROLADOR
                            .getCodigo();;
            validarPermisos();
            inicializarCampos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
    /**
     * Retorna la variable varVolver
     * 
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }
    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas(){
        //<CARGAR_LISTA_COMBO_GRANDE>
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
        tabla="";
      //  buscarLlave();
      //  asignarOrigenDatos();
      //  reasignarOrigenGrilla();
    }
    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos="";	
    }
    /**
     * Se realiza la asignacion de la variable origenGrilla por la
     * consulta correspondiente de la grilla del formulario, se hace
     * la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     * 
     */
    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla="";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenGrilla);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenGrilla);
        }
    }
    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     */
    public void ejecutarrcVolver(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>	
    //</METODOS_CARGAR_LISTA>
    //<METODOS_CAMBIAR>	
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>	
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>	
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>	
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnDepreciaciones
     * en la vista
     *
     */
    public void oprimirBtnDepreciaciones() {
        //<CODIGO_DESARROLLADO>
        
        String[] campos = { "consecutivo",
                            "fechaContable",
                            "tipoDocumento",
                            "numeroDocumento",
                            "descripcion" };
        
        Object[] valores = { consecutivo,
                             fechaContable,
                             tipoDocumento,
                             numeroDocumento,
                             descripcion};

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_E_ALMACEN_CONTABILIDAD_M_SIIF_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnInterfazDiaria
     * en la vista
     *
     * 
     */
    public void oprimirBtnInterfazDiaria() {
        //<CODIGO_DESARROLLADO>
        String[] campos = { "consecutivo",
                            "fechaContable",
                            "tipoDocumento",
                            "numeroDocumento",
                            "descripcion" };
        
        Object[] valores = { consecutivo,
                             fechaContable,
                             tipoDocumento,
                             numeroDocumento,
                             descripcion};

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRM_E_ALMACEN_CONTABILIDAD_T_SIIF_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
        //</CODIGO_DESARROLLADO>
    }
    
    /**
     * Se de asignar valores por defecto a los atributos
     */
    private void inicializarCampos() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            fechaContable = SysmanFunciones.ultimoDiaDate(calendar.getTime());
            tipoDocumento= "32";
            consecutivo="1";
            
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
        
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
     */
    @Override
    public void cargarRegistro() {
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
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
     * Retorna la variable fechaContable
     * 
     * @return  fechaContable
     */
    public Date getFechaContable() {
        return fechaContable;
    }
    /**
     * Asigna la variable  fechaContable
     * 
     * @param  fechaContable
     * Variable a asignar en  fechaContable
     */
    public void setFechaContable(Date fechaContable) {
        this.fechaContable = fechaContable;
    }
    /**
     * Retorna la variable tipoDocumento
     * 
     * @return  tipoDocumento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    /**
     * Asigna la variable  tipoDocumento
     * 
     * @param  tipoDocumento
     * Variable a asignar en  tipoDocumento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    /**
     * Retorna la variable consecutivo
     * 
     * @return  consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }
    /**
     * Asigna la variable  consecutivo
     * 
     * @param  consecutivo
     * Variable a asignar en  consecutivo
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }
    /**
     * Retorna la variable numeroDocumento
     * 
     * @return  numeroDocumento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    /**
     * Asigna la variable  numeroDocumento
     * 
     * @param  numeroDocumento
     * Variable a asignar en  numeroDocumento
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
    /**
     * Retorna la variable descripcion
     * 
     * @return  descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }
    /**
     * Asigna la variable  descripcion
     * 
     * @param  descripcion
     * Variable a asignar en  descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES>	
    //</SET_GET_ADICIONALES>
}
