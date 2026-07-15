/*-
 * FrmunidadejecutorasControlador.java
 *
 * 1.0
 * 
 * 13/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;

import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.frmunidadejecutorasControladorEnum;
import com.sysman.presupuesto.enums.unidadejecutorasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 13/01/2026
 * @author ncardenas
 */
@ManagedBean
@ViewScoped
public class  FrmunidadejecutorasControlador  extends BeanBaseContinuoAcmeImpl{
 
	private final String compania;
	private int ano;
	

	private boolean anioBaseVisible;
    private String anioBase;
   	private String anioDestino;
	private HashMap<String, Object> rid;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
private List<Registro> listaANO;
private List<Registro> listaanioDestino;

private List<Registro> listaanioBase;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
@EJB
private EjbPrepararAnoRemote ejbPrepararAno;
//</DECLARAR_LISTAS_COMBO_GRANDE>
 public FrmunidadejecutorasControlador() 
 {
	super();
	compania = SessionUtil.getCompania();
	try {
		numFormulario=GeneralCodigoFormaEnum.UNIDADEJECUTORA_CONTROLADOR.getCodigo();
		validarPermisos();
		//<INI_ADICIONAL>
	
		//</INI_ADICIONAL>
	} 
	catch (Exception ex) 
	{
		Logger.getLogger(FrmunidadejecutorasControlador.class.getName())
        .log(Level.SEVERE, null, ex);
        SessionUtil.redireccionarMenuPermisos();
    } 
 }
 

  @PostConstruct
public void inicializar(){

	 enumBase=GenericUrlEnum.UNI_EJECUTORA;
	 
	buscarLlave();
	reasignarOrigen();
	registro =new Registro(new HashMap<String, Object>());
	
	
//<CARGAR_LISTA>
	cargarListaANO();
	cargarListaanioBase();
    cargarListaanioDestino();
	
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
	abrirFormulario();
    
  }
    @Override
    public void reasignarOrigen(){
    	parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
    	if (ano > 0) {
		parametrosListado.put(
				frmunidadejecutorasControladorEnum.ANO.getValue(),
				String.valueOf(ano));
    	}
    	buscarUrls();
        
        
    }
//<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaANO
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListaANO(){
	 Map<String, Object> param = new TreeMap<>();
     param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

     try
     {
    	 listaANO = RegistroConverter.toListRegistro(
                         requestManager.getList(UrlServiceUtil.getInstance()
                                         .getUrlServiceByUrlByEnumID(
                                        		 unidadejecutorasControladorUrlEnum.URL4002
                                                                         .getValue())
                                         .getUrl(), param));
     }
     catch (SystemException e)
     {
         Logger.getLogger(FrmunidadejecutorasControlador.class.getName())
                         .log(Level.SEVERE, null, e);
         JsfUtil.agregarMensajeError(e.getMessage());
     }
}

public void cargarListaanioBase()
{
    listaanioBase = listaANO;
}

public void cargarListaanioDestino()
{
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ANO.getName(), anioBase);

    try
    {
        listaanioDestino = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                        		unidadejecutorasControladorUrlEnum.URL4016
                                                        .getValue())
                                        .getUrl(), param));
    }
    catch (SystemException e)
    {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
    }

}
public void oprimirIniciar() {
         //<CODIGO_DESARROLLADO>
	anioBaseVisible = true;
        //</CODIGO_DESARROLLADO>
    }

public void cancelaranioBase() {
	// <CODIGO_DESARROLLADO>
	anioBaseVisible = false;
	// </CODIGO_DESARROLLADO>
}


private void prepararAnoSiguiente()
{
    try
    {

        ejbPrepararAno.copiarUnidadEjecutora(compania,
                        Integer.parseInt(anioDestino),
                        Integer.parseInt(anioBase), compania);
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1637"));
        anioBase = null;
        anioDestino = null;

    }
    catch (SystemException e)
    {

        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
    }
    finally
    {
        anioBaseVisible = false;
    }

}

public void cambiaranioBase()
{
    // <CODIGO_DESARROLLADO>
    anioDestino = null;
    cargarListaanioDestino();

    // </CODIGO_DESARROLLADO>
}
//<METODOS_CAMBIAR>

public void cambiarANO() {
	// <CODIGO_DESARROLLADO>

	if (ano == 0) {
		JsfUtil.agregarMensajeAlerta(
				idioma.getString("TB_TB2680").replace("#ANIO#",
						Integer.toString(ano)));
	}
	reasignarOrigen();
	
	// </CODIGO_DESARROLLADO>
}

public void aceptaranioBase() {

    if ("".equals(anioBase) || (anioBase == null))
    {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB4494"));
        return;
    }
    if ("".equals(anioDestino) || (anioDestino == null))
    {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB4495"));
        return;
    }
    

    prepararAnoSiguiente();
    }
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
  
@Override
	public void abrirFormulario(){
	ano = SysmanFunciones
			.ano(new Date());
	anioBase = String.valueOf(SysmanFunciones
			.ano(new Date()));
	
	}

    @Override
 public void cancelarEdicion(RowEditEvent event) {
  	 getListaInicial().load();
      }
   
    @Override
    public boolean insertarAntes(){
    	 // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(
                        GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(
                        GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());
        return true;
        // </CODIGO_DESARROLLADO>
    }
  
	@Override
    public boolean insertarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
   
    @Override
    public boolean actualizarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
         return true;
    }
  
    @Override   
    public boolean actualizarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
 
    @Override    
    public boolean eliminarAntes(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
      return true;
    }

    @Override   
    public boolean eliminarDespues(){
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
       return true;
    }
    
    @Override
    public void removerCombos() {
    	
//    	   registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
//           registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
//           registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
    }
    
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
//        String[] campos = { "rid" };
//        Object[] valores = { rid };
        SessionUtil.redireccionarMenuPermisos();
        
        // </CODIGO_DESARROLLADO>
    }
   @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }
//<SET_GET_ATRIBUTOS>
 

  
    public List<Registro> getListaANO() {
	return listaANO;
}

public void setListaANO(List<Registro> listaANO) {
	this.listaANO = listaANO;
}


//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
public boolean isAnioBaseVisible() {
	return anioBaseVisible;
}

public void setAnioBaseVisible(boolean anioBaseVisible) {
	this.anioBaseVisible = anioBaseVisible;
}

public List<Registro> getListaanioDestino() {
return listaanioDestino;
}

public void setListaanioDestino(List<Registro> listaanioDestino) {
this.listaanioDestino = listaanioDestino;
}

public List<Registro> getListaanioBase() {
	return listaanioBase;
}

public void setListaanioBase(List<Registro> listaanioBase) {
	this.listaanioBase = listaanioBase;
}

public String getAnioBase() {
	return anioBase;
}

public void setAnioBase(String anioBase) {
	this.anioBase = anioBase;
}
public String getAnioDestino() {
	return anioDestino;
}

public void setAnioDestino(String anioDestino) {
	this.anioDestino = anioDestino;
}


public int getAno() {
	return ano;
}

public void setAno(int ano) {
	this.ano = ano;
}
   
    public HashMap<String, Object> getRid() {
		return rid;
	}

	public void setRid(HashMap<String, Object> rid) {
		this.rid = rid;
	}
	


//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
