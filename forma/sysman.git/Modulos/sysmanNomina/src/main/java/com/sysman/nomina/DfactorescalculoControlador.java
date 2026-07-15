/*-
 * DfactorescalculoControlador.java
 *
 * 1.0
 * 
 * 26/10/2023
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;
import com.sysman.nomina.enums.NovedadesControladorEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 26/10/2023
 * @author ecabrera
 */
@ManagedBean
@ViewScoped
public class  DfactorescalculoControlador  extends BeanBaseContinuoAcmeImpl{
	private final String compania;
	private final String moduloNomina;
	
	private String codigo;
    private String nombre;
    private String valor_parametro;
    private String usuario;
    
    private RegistroDataModelImpl listaidCodigo;
    private RegistroDataModelImpl listaidCodigoE;
    private String auxiliar;
    private Map<String, Object> parametrosEntrada;

    public DfactorescalculoControlador() {
    	super();
    	compania = SessionUtil.getCompania();
    	moduloNomina = SessionUtil.getModulo();
    	try {
    		numFormulario = GeneralCodigoFormaEnum.DFACTORES_CALCULO.getCodigo();
    		validarPermisos();

    		//[INSERTAR,BORRAR,MODIFICAR,CONSULTAR,EXPORTAR]/
    		if ( SessionUtil.getUser().getCodigo().equals("PRUEBAS_SS") ) {
    			permisos[0] = true;
    			permisos[1] = true; 
    			permisos[2] = true; 
    			permisos[4] = true;
    		}
    		else {
    			permisos[0] = false;
    			permisos[1] = false; 
    			permisos[2] = false; 
    			permisos[4] = true;    			
    		}
    		
    		parametrosEntrada = SessionUtil.getFlash();
    	} catch (Exception ex) {
    			logger.error(ex.getMessage(),ex);
    			SessionUtil.redireccionarMenuPermisos();
        } finally {
        }
    }
    
    private String validarCampoCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }
    
    @PostConstruct
    public void inicializar(){
    	enumBase = GenericUrlEnum.D_FACTORES_CALCULO;
    	buscarLlave();
    	reasignarOrigen();
    	registro = new Registro(new HashMap<String, Object>());
    	cargarListaidCodigo();
    	cargarListaidCodigoE();
    	abrirFormulario();
    	//recibirParametros();
    }
    
    private void recibirParametros() {
        if (parametrosEntrada != null) {
        	codigo = validarCampoCadena(parametrosEntrada, "CODIGO");
            reasignarOrigen();
            cargarForma();
            parametrosEntrada.clear();
        }

    }
    
    @Override
    public void reasignarOrigen(){
    	buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        parametrosListado.put("CODIGO",codigo);
    }
    
    public void cargarListaidCodigo(){
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID("191800G");
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	listaidCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                 urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }
  
	public void  cargarListaidCodigoE(){
    	listaidCodigoE = listaidCodigo;
    }

	public void oprimirfactores() {
		Map<String, Object> param = new HashMap<>();
        param.put("CODIGO", codigo);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.FACTORES_CALCULO.getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);
    }

	public void seleccionarFilaidCodigo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		setCodigo(registroAux.getCampos().get("CODIGO").toString());
		setNombre(registroAux.getCampos().get("NOMBRE").toString());
		setValor_parametro(registroAux.getCampos().get("VALOR_PARAMETRO").toString());
		reasignarOrigen();
	}
    
	public void seleccionarFilaidCodigoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
	}

	@Override
	public void abrirFormulario(){
    }
    
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
    }
    
	@Override
    public boolean insertarAntes(){
    	if (SysmanFunciones.validarVariableVacio(codigo)) {
    		JsfUtil.agregarMensajeError(idioma.getString("TI_MS_ERROR_VALIDACION"));
    		return false;
    	}
    	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	registro.getCampos().put("CODIGO", codigo);
    	registro.getCampos().put("VALOR_PARAMETRO", valor_parametro);
        return true;
    }
  
	@Override
    public boolean insertarDespues(){
        return true;
    }
    
	@Override
    public boolean actualizarAntes(){
        return true;
    }
   
   @Override   
    public boolean actualizarDespues(){
        return true;
    }

    @Override    
    public boolean eliminarAntes(){
		return true;
    }
    
	@Override   
    public boolean eliminarDespues(){
       return true;
    }
    
	@Override
    public void removerCombos() {
    }
    
	@Override
    public void asignarValoresRegistro()
    {
    }
   
   //<SET_GET_ATRIBUTOS>
   public String getCodigo() {
        return codigo;
   }
   
	public void setCodigo(String codigo) {
        this.codigo = codigo;
	}
   
	public String getNombre() {
        return nombre;
    }
   
	public void setNombre(String nombre) {
        this.nombre = nombre;
	}
   
	public String getValor_parametro() {
        return valor_parametro;
    }
   
	public void setValor_parametro(String valor_parametro) {
        this.valor_parametro = valor_parametro;
    }

    public RegistroDataModelImpl getListaidCodigo() {
        return listaidCodigo;
    }
    
	public void setListaidCodigo(RegistroDataModelImpl listaidCodigo) {
        this.listaidCodigo = listaidCodigo;
    }
    
	public RegistroDataModelImpl getListaidCodigoE() {
        return listaidCodigoE;
    }
    
	public void setListaidCodigoE(RegistroDataModelImpl listaidCodigoE) {
        this.listaidCodigoE = listaidCodigoE;
    }
    
	public String getAuxiliar() {
        return auxiliar;
    }
    
	public void setAuxiliar(String auxiliar) {
        this.auxiliar= auxiliar;
    }
}
