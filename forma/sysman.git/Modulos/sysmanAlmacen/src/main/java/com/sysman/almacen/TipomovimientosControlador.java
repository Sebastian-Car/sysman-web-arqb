package com.sysman.almacen;

import com.sysman.almacen.enums.TipomovimientosControladorEnum;
import com.sysman.almacen.enums.TipomovimientosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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

/**
 *
 * @author ngomez
 * @version 1, 20/10/2015
 *
 * @author eamaya
 * @version 2, 10/05/2017 Procesos de Refactoring, Correcciones
 * SonarLint y Manejo de EJBs
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class TipomovimientosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String cClase;
    private final String cTipoElemento;
    private final String cCodigo;
    private final String cConcepto;
    private List<Registro> listaConcepto;
    private List<Registro> listaClaseDocAsociado;
    private List<Registro> listaClasebodegaorigen;
    private List<Registro> listaCuadroCombinado58;
    private List<Registro> listacomRelacionado;
    private boolean inventarioVisible;
    private boolean ventasVisible;
    private boolean placaVisible;
    private boolean niifVisible = true;
    private String titulo;
    private String condicion;
    private String claseContable;
    private boolean transpasoVisible;
    private boolean cargaCambAux = false;
    private boolean permiteCausacion = false;
    private boolean manCausacionAuto = false;
    private boolean causacion;
    private boolean parContratoManual;
	private boolean ContratoManual = false;



	@EJB
    private EjbSysmanUtilRemote ejbParametro;

    public TipomovimientosControlador() {
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cClase = "CLASE";
        cTipoElemento = "TIPOELEMENTO";
        cCodigo = "CODIGO";
        cConcepto = "CONCEPTO";

        try {
            registro = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.TIPOMOVIMIENTOS_CONTROLADOR
                            .getCodigo();
            if ("100116".equals(SessionUtil.getMenuActual())) {
                titulo = idioma.getString("TB_TB3142");
                condicion = "";
                transpasoVisible = false;
            }
            else {
                titulo = idioma.getString("TB_TB3143");
                condicion = "T";
                transpasoVisible = true;
            }
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(TipomovimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TIPO_MOVIMIENTO;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void abrirFormulario() {
        niifVisible = true;
        try {
            Object aux = ejbParametro.consultarParametro(compania,
                            "MANEJA NIIF EN ALMACEN", modulo, new Date(),
                            false);
            cargaCambAux = SysmanFunciones.nvl((ejbParametro.consultarParametro(compania,
    					   "MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN", modulo, 
    					   new Date(), false)),"NO").equals("SI");
            
            causacion = SysmanFunciones.nvl((ejbParametro.consultarParametro(compania,
					   "MANEJA CAUSACION AUTOMATICA", "1", 
					   new Date(), false)),"NO").equals("SI");
            
            parContratoManual = SysmanFunciones.nvl((ejbParametro.consultarParametro(compania,
					   "CONTRATO MANUAL EN ENTRADAS DE ALMACEN", modulo, 
					   new Date(), false)),"NO").equals("SI");
            
            if (aux != null) {
                if ("NO".equals(aux)) {
                    niifVisible = false;
                }
            }
            else {
                niifVisible = false;
            }
            
          
        }
        catch (SystemException ex) {
            Logger.getLogger(TipomovimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        if ("i".equals(accion)) {
            registro.getCampos().put("CLASEMOVIMIENTO", "M");
            registro.getCampos().put(cConcepto, "N");
            registro.getCampos().put("COSTEA", true);
        }
        validarCausacionAutomatica();
        precargarRegistro();
        if ("E".equals(registro.getCampos().get(cClase).toString()) && parContratoManual) {
        	ContratoManual = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("CONDICION", condicion);

    }

    @Override
    public void iniciarListas() {
        cargarListaConcepto();
        cargarListaClaseDocAsociado();
        cargarListaClasebodegaorigen();
        cargarListaCuadroCombinado58();
    }

    @Override
    public void iniciarListasSub() {
        actualizarChecks();
    }

    public void cargarListaConcepto() {

        try {

            listaConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipomovimientosControladorUrlEnum.URL3847
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaClaseDocAsociado() {
        try {
            listaClaseDocAsociado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipomovimientosControladorUrlEnum.URL4078
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaClasebodegaorigen() {
        try {

            listaClasebodegaorigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipomovimientosControladorUrlEnum.URL4391
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuadroCombinado58() {
        listaCuadroCombinado58 = listaClasebodegaorigen;

    }
    
    public void cargarListacomRelacionado(){
    	try {
    		
    		Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASECONTABLE.getName(), claseContable);
            
    		listacomRelacionado = RegistroConverter.toListRegistro(
    	                    requestManager.getList(UrlServiceUtil.getInstance()
    	                                    .getUrlServiceByUrlByEnumID(
    	                                                    TipomovimientosControladorUrlEnum.URL15048
    	                                                                    .getValue())
    	                                    .getUrl(), param));
    	}
    	catch (SystemException e) {
    	    logger.error(e.getMessage(), e);
    	    JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	
    	 
    	}

    public void actualizarChecks() {
        if (registro.getCampos().get(cClase) != null) {
            if ("E".equals(registro.getCampos().get(cClase).toString())) {
                inventarioVisible = true;
                ventasVisible = true;
                if (registro.getCampos().get(cTipoElemento) != null) {
                    placaVisible = "D".equals(registro.getCampos()
                                    .get(cTipoElemento).toString())
                        || "N".equals(registro.getCampos().get(cTipoElemento)
                                        .toString())
                        || "E".equals(registro.getCampos().get(cTipoElemento)
                                        .toString())
                        || "M".equals(registro.getCampos().get(cTipoElemento)
                                        .toString());
                }
            }
            else {
                if (registro.getCampos().get(cConcepto) != null) {
                    inventarioVisible = "II".equals(registro.getCampos()
                                    .get(cConcepto).toString());
                    ventasVisible = false;
                    placaVisible = false;
                }
            }
        }
    }

    public void cambiarClase() {
        // <CODIGO_DESARROLLADO>
        actualizarChecks();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(cTipoElemento) != null) {
            placaVisible = "D".equals(registro.getCampos()
                            .get(cTipoElemento).toString())
                || "N".equals(registro.getCampos().get(cTipoElemento)
                                .toString())
                || "E".equals(registro.getCampos().get(cTipoElemento)
                                .toString())
                || "M".equals(registro.getCampos().get(cTipoElemento)
                                .toString());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarConcepto() {
        // <CODIGO_DESARROLLADO>
        actualizarChecks();
        registro.getCampos().put("TIPOELEMENTO", "");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNumeroInicial() {
        try { // <CODIGO_DESARROLLADO>
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(TipomovimientosControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(cCodigo));

            List<Registro> aux;

            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipomovimientosControladorUrlEnum.URL7338
                                                                            .getValue())
                                            .getUrl(), param));

            String aux2 = (aux.get(0).getCampos().get("CUENTA"))
                            .toString();
            if (!"0".equals(aux2)) {
                registro.getCampos().put("NUMEROINICIAL",
                                registroIni.get("NUMEROINICIAL"));
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1999"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoCampo() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cCodigo, registro.getCampos().get(cCodigo)
                        .toString().toUpperCase());
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarmanCausacion() {
        //<CODIGO_DESARROLLADO>
    	permiteCausacion = (boolean) registro.getCampos().get("MAN_CAUSACION");
    	validarCausacionAutomatica();
       //</CODIGO_DESARROLLADO>
   }

    public void cambiarNombreCorto() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("NOMBRECORTO", registro.getCampos()
                        .get("NOMBRECORTO").toString().toUpperCase());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        try { // <CODIGO_DESARROLLADO>
            if (!"100116".equals(SessionUtil.getMenuActual())) {
                registro.getCampos().put(cClase, "T");
                registro.getCampos().put("CLASEMOVIMIENTO", "T");
                registro.getCampos().put(cTipoElemento, "D");
                registro.getCampos().put("TIPOPESONA", "R");
            }

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TipomovimientosControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get("CONCEPTO"));

            param.put(TipomovimientosControladorEnum.PARAM2.getValue(),
                            registro.getCampos().get("CLASE"));

            param.put(TipomovimientosControladorEnum.PARAM3.getValue(),
                            registro.getCampos().get("CLASE_BODEGA_ORIGEN"));

            param.put(TipomovimientosControladorEnum.PARAM4.getValue(),
                            registro.getCampos().get("CLASE_BODEGA_DESTINO"));

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipomovimientosControladorUrlEnum.URL4882
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs == null) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2768"));
                return false;

            }

            if ("m".equals(accion)) {
                registro.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    private void validarCausacionAutomatica() {
        
    	claseContable = "C";
    	if (css != null) {
    		permiteCausacion = (boolean) registro.getCampos().get("MAN_CAUSACION");
    		String concepto = registro.getCampos().get(cConcepto).toString();
            if (causacion && (concepto.equals("C") || concepto.equals("CM") 
            		|| concepto.equals("DT") || concepto.equals("L"))) {
            	manCausacionAuto = true;
            } else {
            	manCausacionAuto = false;
            }
            
            if (registro.getCampos().get(cConcepto).toString().equals("C") && registro.getCampos().get(cClase).toString().equals("E")) {
            	claseContable = "P";
            }
            cargarListacomRelacionado();
    	}    	
    }

    public List<Registro> getListaConcepto() {
        return listaConcepto;
    }

    public void setListaConcepto(List<Registro> listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    public List<Registro> getListaClaseDocAsociado() {
        return listaClaseDocAsociado;
    }

    public void setListaClaseDocAsociado(List<Registro> listaClaseDocAsociado) {
        this.listaClaseDocAsociado = listaClaseDocAsociado;
    }

    public List<Registro> getListaClasebodegaorigen() {
        return listaClasebodegaorigen;
    }

    public void setListaClasebodegaorigen(
        List<Registro> listaClasebodegaorigen) {
        this.listaClasebodegaorigen = listaClasebodegaorigen;
    }

    public List<Registro> getListaCuadroCombinado58() {
        return listaCuadroCombinado58;
    }

    public void setListaCuadroCombinado58(
        List<Registro> listaCuadroCombinado58) {
        this.listaCuadroCombinado58 = listaCuadroCombinado58;
    }
    
    public List<Registro> getListacomRelacionado() {
    	return listacomRelacionado;
    }
    
    public void setListacomRelacionado(List<Registro> listacomRelacionado) {
    	this.listacomRelacionado = listacomRelacionado;
    }

    public boolean isInventarioVisible() {
        return inventarioVisible;
    }

    public void setInventarioVisible(boolean inventarioVisible) {
        this.inventarioVisible = inventarioVisible;
    }

    public boolean isVentasVisible() {
        return ventasVisible;
    }

    public void setVentasVisible(boolean ventasVisible) {
        this.ventasVisible = ventasVisible;
    }

    public boolean isPlacaVisible() {
        return placaVisible;
    }

    public void setPlacaVisible(boolean placaVisible) {
        this.placaVisible = placaVisible;
    }

    public boolean isNiifVisible() {
        return niifVisible;
    }

    public void setNiifVisible(boolean niifVisible) {
        this.niifVisible = niifVisible;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isTranspasoVisible() {
        return transpasoVisible;
    }

    public void setTranspasoVisible(boolean transpasoVisible) {
        this.transpasoVisible = transpasoVisible;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }
    
    public boolean isCargaCambAux() {
        return cargaCambAux;
    }

    public void setCargaCambAux(boolean cargaCambAux) {
        this.cargaCambAux = cargaCambAux;
    }
    
    public boolean isPermiteCausacion() {
        return permiteCausacion;
    }

    public void setPermiteCausacion(boolean permiteCausacion) {
        this.permiteCausacion = permiteCausacion;
    }
    
    public boolean isManCausacionAuto() {
        return manCausacionAuto;
    }

    public void setManCausacionAuto(boolean manCausacionAuto) {
        this.manCausacionAuto = manCausacionAuto;
    }
    
    public boolean isContratoManual() {
		return ContratoManual;
	}

	public void setContratoManual(boolean contratoManual) {
		ContratoManual = contratoManual;
	}
	
    public boolean isParContratoManual() {
		return parContratoManual;
	}

	public void setParContratoManual(boolean parContratoManual) {
		this.parContratoManual = parContratoManual;
	}

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}
