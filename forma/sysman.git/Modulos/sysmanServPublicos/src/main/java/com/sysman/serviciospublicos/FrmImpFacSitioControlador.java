/*-
 * FrmImpFacSitioControlador.java
 *
 * 1.0
 * 
 * 24/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 24/01/2017
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class  FrmImpFacSitioControlador extends BeanBaseModal{
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
    private String ruta;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private String ciclo;
    
    private boolean terminalDolphin;
    private boolean facturacionPlanos;
    private String rutaArchivo;
    private String tituloPaginaEmpresaParametrizada;
    private String labelPagEmpresaParametrizada;
    private InputStream is;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    private List<Registro> listaCiclo;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModel listaruta;
    
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmImpFacSitioControlador
     */
    public FrmImpFacSitioControlador() {
        super();
        compania = SessionUtil.getCompania();
        
        //codigo que aplica para implementacion de marca blanca
        tituloPaginaEmpresaParametrizada = idioma.getString("EM_FR1267");
        tituloPaginaEmpresaParametrizada = tituloPaginaEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        
        labelPagEmpresaParametrizada = idioma.getString("TT_LB33808");
        labelPagEmpresaParametrizada = labelPagEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        try {
            numFormulario=1267;
            validarPermisos();
            //<INI_ADICIONAL>
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        cargarListaruta(); 
        cargarListaCiclo();
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        try {
            String parTerminalDolphin = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "MANEJA TERMINAL DOLPHIN 7600", SessionUtil.getModulo(),
                            "SYSDATE");
            terminalDolphin = ("SI").equals(parTerminalDolphin);
            
            
            String parFacturacionPlanos = Acciones.getParametro(
                            ConectorPool.ESQUEMA_SYSMAN, compania,
                            "FIMM - FACTURACION CON PLANOS", SessionUtil.getModulo(),
                            "SYSDATE");
            facturacionPlanos = ("SI").equals(parFacturacionPlanos);
            
            
            terminalDolphin=true;
        }
        catch (NamingException | SQLException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }  
        
        /*
FR1267-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
    formularioAbrir 74, Me.Name
    '29/04/2010 jp parametro para cargar los archivos planos a
    If ParFacturacion(Getcompany(), "MANEJA TERMINAL DOLPHIN 7600") = "SI" Then
        Me.E_Ruta.visible = True
        Me.Ruta.visible = True
    Else
        Me!Aceptar.Top = Me!Aceptar.Top - 280
        Me!Cancelar.Top = Me!Cancelar.Top - 280
        Me!txtMensaje.Top = Me!txtMensaje.Top - 280
        Me.InsideHeight = Me.InsideHeight - 280
    End If
End Sub
         */
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaruta
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaruta(){
        listaruta = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FR1267_nuevo:TBCB4183","SELECT DISTINCT "+
                        "    (SUBSTR(CODIGORUTA, 9, 3))  RUTA "+
                        " FROM "+
                        "     SP_USUARIO USUARIO "+
                        " WHERE "+
                        "   USUARIO.COMPANIA = '"+compania+"' "+
                        "   AND "+
                        "   USUARIO.CICLO = "+ciclo+
                        " ORDER BY "+
                        "    SUBSTR(CODIGORUTA, 9, 3)",true,"RUTA");
    }
    /**
     * 
     * Carga la lista listaCiclo
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaCiclo(){
        listaCiclo = service.getListado(ConectorPool.ESQUEMA_SYSMAN, "SELECT "+
                        "     CICLO.NUMERO "+
                        " FROM "+
                        "     SP_CICLO CICLO "+
                        " WHERE "+
                        "     CICLO.COMPANIA = '"+compania+"' "+
                        " ORDER BY "+
                        "     CICLO.NUMERO");
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirAceptar() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirCancelar() {
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    
    public void leerArchivo(){
        if(!terminalDolphin){
            if(facturacionPlanos){
                
            }
            
        }else{
            
        }
    }
    
    
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarCiclo() {
         //<CODIGO_DESARROLLADO>
        cargarListaruta();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Aceptar
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoAceptar(FileUploadEvent event){
        //<CODIGO_DESARROLLADO>
        ruta = event.getFile().getFileName();
        try {
            is = event.getFile().getInputstream();
        }
        catch (IOException e) {
            Logger.getLogger(FrmImpFacSitioControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaruta
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaruta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
                ruta= (String) registroAux.getCampos().get("RUTA");
        }
    
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ruta
     * 
     * @return  ruta
     */
    public String getRuta() {
        return ruta;
    }
    /**
     * Asigna la variable  ruta
     * 
     * @param  ruta
     * Variable a asignar en  ruta
     */
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    /**
     * Retorna la variable ciclo
     * 
     * @return  ciclo
     */
    public String getCiclo() {
        return ciclo;
    }
    /**
     * Asigna la variable  ciclo
     * 
     * @param  ciclo
     * Variable a asignar en  ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
    
    public boolean isTerminalDolphin() {
        return terminalDolphin;
    }
    public void setTerminalDolphin(boolean terminalDolphin) {
        this.terminalDolphin = terminalDolphin;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listaruta
     * 
     * @return listaruta
     */
    public RegistroDataModel getListaruta() {
        return listaruta;
    }
    /**
     * Asigna la lista listaruta
     * 
     * @param listaruta
     * Variable a asignar en  listaruta
     */
    public void setListaruta(RegistroDataModel listaruta) {
        this.listaruta = listaruta;
    }
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
	public String getTituloPaginaEmpresaParametrizada() {
		return tituloPaginaEmpresaParametrizada;
	}
	public void setTituloPaginaEmpresaParametrizada(String tituloPaginaEmpresaParametrizada) {
		this.tituloPaginaEmpresaParametrizada = tituloPaginaEmpresaParametrizada;
	}
	public String getLabelPagEmpresaParametrizada() {
		return labelPagEmpresaParametrizada;
	}
	public void setLabelPagEmpresaParametrizada(String labelPagEmpresaParametrizada) {
		this.labelPagEmpresaParametrizada = labelPagEmpresaParametrizada;
	}
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}
