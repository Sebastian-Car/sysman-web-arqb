/*-
 * FrmadjuntosestudiospreviosControlador.java
 *
 * 1.0
 * 
 * 13/02/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmadjuntosestudiospreviosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import org.primefaces.model.UploadedFile;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 13/02/2025
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmadjuntosestudiospreviosControlador extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
	 * de un registro. Toma el valor del indice dentro de la grilla del registro
	 * seleccionado para editar
	 */
	private int indice;
	/**
	 * Constante a nivel de clase que aloja el codigo del usuario que inicio sesion.
	 */
	private final String usuario = SessionUtil.getUser().getCodigo();
//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * BuscarAdjunto y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaBuscarAdjunto;

	private String rutaPpal;

	/*
	 * Parametros que se reciben desde el formulario principal
	 */
	private HashMap<String, Object> ridP;
	private String codEstudio;
	private String vigencia;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmadjuntosestudiospreviosControlador
	 */
	public FrmadjuntosestudiospreviosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// 2506
			numFormulario = GeneralCodigoFormaEnum.FRM_ADJUNTOS_ESTUDIOS_PREVIOS_CONTROLADOR.getCodigo();
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				ridP = (HashMap<String, Object>) parametrosEntrada.get("rid");
				codEstudio = validarCadena(parametrosEntrada, "codEstudio");
				vigencia = parametrosEntrada.get("vigenciaPeriodo").toString();
			}
			validarPermisos();

		} catch (Exception ex) {
			Logger.getLogger(FrmadjuntosestudiospreviosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.ES_ADJUNTOS_ESTPREVIO;
		buscarLlave();
        reasignarOrigen();
		registro = new Registro();

		abrirFormulario();
	}	

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton BtAdjuntar
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg    registro en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 * @param indice indice en el cual esta ubicado el boton oprimido dentro de la
	 *               grilla
	 */
	public void oprimirBtAdjuntar(Registro reg, int indice) {
		String archivo = archivoCargaBuscarAdjunto.getFileName();
		String miExtension = FilenameUtils.getExtension(archivo);
		Map<String, Object> keyAdjunto = new LinkedHashMap<>();
		keyAdjunto.put(GeneralParameterEnum.COD_ESTUDIO.getName(),codEstudio);
		keyAdjunto.put(GeneralParameterEnum.SECTOR.getName(), reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		keyAdjunto.put(GeneralParameterEnum.CONSECUTIVO.getName(), reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
		
		try {
			if (SysmanFunciones.validarVariableVacio(archivo)) 
			{
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4079"));
				return;
			}
		
			if(miExtension.equals("jpg") || miExtension.equals("png"))
			{		
				String ruta =  generarNombreArchivo(rutaPpal, keyAdjunto,
						codEstudio, archivo);
				
				JsfUtil.upload(archivoCargaBuscarAdjunto.getInputstream(), ruta);
				String rutaAdjunto = rutaPpal + codEstudio + "/"+ FilenameUtils.getName(ruta);
				
				Registro regAux = new Registro();
				regAux.getCampos().put("ADJUNTO", rutaAdjunto);
				regAux.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
				regAux.getLlave().put(GeneralParameterEnum.COMPANIA.getName(), compania);
				regAux.getLlave().put(GeneralParameterEnum.COD_ESTUDIO.getName(), codEstudio);
				regAux.getLlave().put(GeneralParameterEnum.SECTOR.getName(), reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
				regAux.getLlave().put(GeneralParameterEnum.CONSECUTIVO.getName(), reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
				UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		FrmadjuntosestudiospreviosControladorUrlEnum.URL1956001.getValue());
				int conteo = requestManager.update(urlUpdate.getUrl(),
                        urlUpdate.getMetodo(), regAux.getCampos(),
                        regAux.getLlave());

                if (conteo > 0)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_MODIFICADO"));
                }
			}
			else
			{
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4123"));
				return;
			}
		
		} catch (IOException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//metodo para arma el nombre y ruta del archivo
		public static String generarNombreArchivo(String rutaIni,
				Map<String, Object> llave,
				String carpetas, String archivo) {

			StringBuilder nombre = new StringBuilder();
			
			for (Object valor : llave.values()) {
				nombre.append(valor);
				nombre.append("_");
			}
			
			nombre.append(archivo);

			StringBuilder ruta = new StringBuilder();

			ruta.append(rutaIni);
			ruta.append(carpetas);

			File verificar = new File(ruta.toString());
			if (!verificar.isDirectory()) {
				verificar.mkdirs();
			}
			
			ruta.append("/");
			ruta.append(nombre);

			return ruta.toString();
		}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		cargarParametros();
	}
	
	private void cargarParametros() {
		try {
			rutaPpal = ejbSysmanUtil.consultarParametro(compania,
					"RUTA IMAGENES ESTUDIOS PREVIOS",
					SessionUtil.getModulo(), new Date(), false);
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo ejecutado cuando se activa la edicion de un registro del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param registro registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		setIndice(listaInicial.getRowIndex());
	}

	/**
	 * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar() {
		
		Map<String, Object> parametros = new TreeMap<>();

        parametros.put("ridEstPrevios", ridP);
        parametros.put("vigenciaPeriodo", vigencia);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	private String validarCadena(Map<String, Object> campos, String var) {
		return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivoBuscarAdjunto
	 * 
	 * @return contArchivoBuscarAdjunto
	 */
	public UploadedFile getArchivoCargaBuscarAdjunto() {
		return archivoCargaBuscarAdjunto;
	}

	/**
	 * Asigna el objeto contArchivoBuscarAdjunto
	 * 
	 * @param contArchivoBuscarAdjunto Variable a asignar en
	 *                                 contArchivoBuscarAdjunto
	 */
	public void setArchivoCargaBuscarAdjunto(UploadedFile archivoCargaBuscarAdjunto) {
		this.archivoCargaBuscarAdjunto = archivoCargaBuscarAdjunto;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
	@Override
	public void asignarValoresRegistro() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removerCombos() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reasignarOrigen() {
		 buscarUrls();
	     parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
	     parametrosListado.put(GeneralParameterEnum.COD_ESTUDIO.getName(),
	                        codEstudio);

	}

	@Override
	public boolean insertarAntes() {
		try {
			String sector = registro.getCampos().get(GeneralParameterEnum.SECTOR.getName()).toString();
			long aux = ejbSysmanUtil.generarConsecutivoConValorInicial(
			        "ES_ADJUNTOS_ESTPREVIO",
			        "ES_ADJUNTOS_ESTPREVIO.COMPANIA =''"
			            + compania + "''"
			            + " AND ES_ADJUNTOS_ESTPREVIO.COD_ESTUDIO =" + codEstudio
			            + " AND ES_ADJUNTOS_ESTPREVIO.SECTOR="
			            + sector,
			        "CONSECUTIVO", "1");
			String codSector = "";
			
			switch (sector)
			{
            	case "1":
            	{
            		codSector = "IMG_ASP_GEN";           		
	                break;
	            }
            	case "2":
            	{
            		codSector = "IMG_ASP_ECO";           		
	                break;
	            }
            	case "3":
            	{
            		codSector = "IMG_ASP_TEC";        		
	                break;
	            }
            	case "4":
            	{
            		codSector = "IMG_ASP_REG";           		
	                break;
	            }
            	case "5":
            	{
            		codSector = "IMG_ASP_OFE";           		
	                break;
	            }
            	case "6":
            	{
            		codSector = "IMG_ASP_DEM";           		
	                break;
	            }
            	case "7":
            	{
            		codSector = "IMG_ASP_EST";           		
	                break;
	            }
            	case "8":
            	{
            		codSector = "IMG_ASP_LEG";           		
	                break;
	            }
            	case "9":
            	{
            		codSector = "IMG_ASP_CON";           		
	                break;
	            }
            	default: 
            	{
            		codSector = ""; 
            	}
			}	
			
			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(), codEstudio);
			registro.getCampos().put(GeneralParameterEnum.SECTOR.getName(), sector);
			registro.getCampos().put(GeneralParameterEnum.COD_SECTOR.getName(), codSector);
			registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(), aux);
			
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean insertarDespues() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean actualizarDespues() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		registro.getLlave().put(GeneralParameterEnum.KEY_SECTOR.getName(), 
				registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}
}
