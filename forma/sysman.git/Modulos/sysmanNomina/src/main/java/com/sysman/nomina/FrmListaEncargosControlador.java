/*-
 * FrmListaEncargosControlador.java
 *
 * 1.0
 * 
 * 19/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.FrmListaEncargosControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/02/2020
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  FrmListaEncargosControlador  extends BeanBaseContinuoAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
	private final String compania;
	private int indice;
	@EJB
	private EjbNominaDosRemote ejbNominaDos;
//<DECLARAR_ATRIBUTOS>
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmListaEncargosControlador
     */
	private Map<String, Object> parametrosEntrada;
    private String periodoNomina;
    private String procesoNomina;
    private String mesNomina;
    private String anioNomina;
    private String titulomodal;
    private String fechaPeriodo;
    private boolean activo;
    
    
    
    
   
	public FrmListaEncargosControlador() {
	super();
	
	compania = SessionUtil.getCompania();
	parametrosEntrada = SessionUtil.getFlash();

    try {
    	
        periodoNomina = validarCadena(
                        SessionUtil.getSessionVar("periodoNomina"));
        procesoNomina = validarCadena(
                        SessionUtil.getSessionVar("procesoNomina"));
        mesNomina = validarCadena(SessionUtil.getSessionVar("mesNomina"));
        anioNomina = validarCadena(SessionUtil.getSessionVar("anioNomina"));
        titulomodal="Encargos Vigentes de el mes de "+ SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesNomina)] +" del año " + anioNomina;
        fechaPeriodo ="01"+"/"+ mesNomina + "/" + anioNomina; 
	numFormulario=2161;
	 validarPermisos();
	 activo = Boolean
             .parseBoolean(SysmanFunciones.nvl(
                             SessionUtil.getSessionVar(
                                             "periodoActivo"),
                             "false").toString());
//<INI_ADICIONAL>
//</INI_ADICIONAL>
} catch (Exception ex) {
logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
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
	tabla="ENCARGOS";
	reasignarOrigen();		    
 buscarLlave();
 abrirFormulario();
    }
    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen(){
    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                 compania);
 parametrosListado.put("ANIO", anioNomina);
 parametrosListado.put(GeneralParameterEnum.MES.getName(), mesNomina);
 parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodoNomina);
 parametrosListado.put("PROCESO", procesoNomina);

 urlListado = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(
                		 FrmListaEncargosControladorUrlEnum.URL0001
                                                 .getValue());
 urlActualizacion = UrlServiceUtil.getInstance()
         .getUrlServiceByUrlByEnumID(
        		 FrmListaEncargosControladorUrlEnum.URL0002
                                         .getValue());

    }
//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
@Override
	public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
	if (!activo) {
        SessionUtil.agregarMensajeErrorMenu(
                        idioma.getString("TB_TB2550"));

        SessionUtil.redireccionarMenu();

        return;
    }
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * TODO DOCUMENTACION ADICIONAL
     */
    @Override
 public void cancelarEdicion(RowEditEvent event) {
  	 getListaInicial().load();
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
       	activarEdicion(registro);
    	registro.getCampos().remove("ANO");
    	registro.getCampos().remove("ACTO");
    	registro.getCampos().remove("FECHAINICIO");
    	registro.getCampos().remove("ID_DE_CARGO");
    	registro.getCampos().remove("ID_DE_CATEGORIA");
    	registro.getCampos().remove("ESCALAFON");
    	registro.getCampos().remove("ID_DE_EMPLEADO");
    	registro.getCampos().remove("FECHAPAGO");
    	registro.getCampos().remove("SUELDOMENSUAL");
    	registro.getCampos().remove("NIVEL");
    	registro.getCampos().remove("COMPANIA");
    	registro.getCampos().remove("CARGO");
    	registro.getCampos().remove("ID_DE_PROCESO");
    	registro.getCampos().remove("NOMBRE");

    	
        //</CODIGO_DESARROLLADO>
         return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * 
     * @return 
     */
    @Override   
    public boolean actualizarDespues(){
         //<CODIGO_DESARROLLADO>
    	JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4355"));
    	
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
    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
    }
    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores
     * al registro despues de dichas acciones
     */
   @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }
   /**
    * Metodo ejecutado cuando se activa la edicion de un registro del
    * formulario
    * 
    * TODO DOCUMENTACION ADICIONAL
    *
    * @param registro
    * registro del cual se activo la edicion
    */
   public void activarEdicion(Registro registro) {
	   /*indice = listaInicial.getRowIndex();*/
	   
	   try {
		   String fechaini=(registro.getCampos().get("FECHAINICIO").toString());
		   String fechafin=(registro.getCampos().get("FECHAFINAL").toString());
		   int rta= ejbNominaDos.difereirEncMod(
				   compania, 
				   procesoNomina, 
				   registro.getCampos().get("ID_DE_EMPLEADO").toString(), 
				   null,
				   ((Date) registro.getCampos().get("FECHAINICIO")),
				   ((Date) registro.getCampos().get("FECHAFINAL")),
				   Double.parseDouble("0"), 
				   Double.parseDouble(registro.getCampos().get("SUELDOMENSUAL").toString()), 
				   SessionUtil.getUser().getCodigo(), 
				   periodoNomina);
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
   }
   
   private String validarCadena(Object var) {
       return SysmanFunciones.validarVariableVacio(var.toString()) ? ""
           : var.toString();
   }
   public String getTitulomodal() {
		return titulomodal;
	}
	public void setTitulomodal(String titulomodal) {
		this.titulomodal = titulomodal;
	}
	public String getFechaPeriodo() {
		return fechaPeriodo;
	}
	public void setFechaPeriodo(String fechaPeriodo) {
		this.fechaPeriodo = fechaPeriodo;
	}
	public int getIndice() {
		return indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}
}
