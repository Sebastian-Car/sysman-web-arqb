/*-
 * CertificadoContratosControlador.java
 *
 * 1.0
 * 
 * 22/12/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.CertificadoContratosControladorEnum;
import com.sysman.contratos.enums.CertificadoContratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/12/2021
 * @author jhenao
 */
@ManagedBean
@ViewScoped
public class CertificadoContratosControlador extends BeanBaseModal {
	private final String compania;
    private final String modulo;
    private final String usuario;
    private final String cCodigo;
    private final String cNombre;
    private final String cNit;
    private boolean resumen;
    private String terceroInicial;
    private String nombreInicial;
    private String terceroFinal;
    private String nombreFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String contratoInicial;
    private String contratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaTipoContratoInicial;
    private RegistroDataModelImpl listaTipoContratoFinal;
    private RegistroDataModelImpl listaplantillaCertificados;
    private String  plantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	private Boolean visibleListaPlantillas = false;
    private StreamedContent archivoDescarga;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;  
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CertificadoContratosControlador
	 */
	public CertificadoContratosControlador() {
		 super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cNit = CertificadoContratosControladorEnum.NIT.getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.CERTIFICADO_CONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(RelacioncontratoszipaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
		cargarListaTerceroInicial();
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
         cargarListaPlantillaCertificados();
        fechaInicial = fechaFinal = new Date();
        abrirFormulario();
        
	}

	/**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
  @Override
	public void abrirFormulario(){

  }
    public void cargarListaTipoContratoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CertificadoContratosControladorUrlEnum.URL5270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTipoContratoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CertificadoContratosControladorUrlEnum.URL6095
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadoContratosControladorEnum.CODIGOINI.getValue(),
                        tipoContratoInicial);

        listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CertificadoContratosControladorUrlEnum.URL3642
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cNit);
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CertificadoContratosControladorUrlEnum.URL4313
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadoContratosControladorEnum.TECEROINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cNit);
    }

	public void cargarListaPlantillaCertificados() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.MODULO.getName(), modulo);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(CertificadoContratosControladorUrlEnum.URL104078.getValue());
		
		
		listaplantillaCertificados = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
			
	}
    

    
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimircmdPantalla() {
        archivoDescarga = null;
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            return;
        }
        generarInforme(ReportesBean.FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
        archivoDescarga = null;
        if (fechaInicial.after(fechaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB574"));
            return;
        }
        generarInforme(FORMATOS.EXCEL);
	}
	public void generarInforme(ReportesBean.FORMATOS formato) {
        try {

        	  String reporte =   SysmanFunciones.nvlStr(
  					ejbSysmanUtil.consultarParametro(
          														compania,
          														"FORMATO CERTIFICADO DE CONTRATOS",
          														modulo,
          														new Date(), 
          														false), "002329CertificadoContrato");
          													     													
            // MANEJO DE REEMPLAZOS DEL REPORTE
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoContratoInicial", SysmanFunciones
                            .concatenar("'", tipoContratoInicial, "'"));
            reemplazar.put("tipoContratoFinal", SysmanFunciones.concatenar("'",
                            tipoContratoFinal, "'"));
            reemplazar.put("terceroInicial", SysmanFunciones.concatenar("'",
                            terceroInicial, "' "));
            reemplazar.put("terceroFinal",
                            SysmanFunciones.concatenar("'", terceroFinal, "'"));
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL",
            			Reporteador.resuelveConsulta(reporte,
                    		Integer.parseInt(SessionUtil.getModulo()), reemplazar));
            parametros.put("PR_USUARIO",usuario);        
            
	
            String asesorJuridico = SysmanFunciones
                    .nvlStr(ejbSysmanUtil.consultarParametro(
                                    compania,
                                    "ASESOR JURÍDICO",
                                    modulo, new Date(), true), "");
            
            
            parametros.put("PR_ASESOR_JURIDICO", asesorJuridico);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoContratoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = validarVacio(registroAux.getCampos().get(cNit));
        nombreInicial = validarVacio(registroAux.getCampos().get(cNombre).toString());
        terceroFinal = null;
        nombreFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = validarVacio(registroAux.getCampos().get(cNit));
        nombreFinal = validarVacio(registroAux.getCampos().get(cNombre).toString());
    }

    public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoInicial = validarVacio(registroAux.getCampos().get(cCodigo));
        contratoInicial = validarVacio(registroAux.getCampos().get(cNombre).toString());
        tipoContratoFinal = null;
        contratoFinal = null;
        cargarListaTipoContratoFinal();
    }

    public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoContratoFinal = validarVacio(registroAux.getCampos().get(cCodigo));
        contratoFinal = validarVacio(registroAux.getCampos().get(cNombre).toString());
    }
    
    public void seleccionarFilaplantillaCertificados(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantilla = SysmanFunciones.toString(registroAux.getCampos().get(cCodigo));
        nombrePlantilla = SysmanFunciones.toString(registroAux.getCampos().get(cNombre));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visibleListaPlantillas = plantilla != null;
    }
    
    
    
	private void generarPdfdesdeWord() {
//		 TODO Auto-generated method stub
		 Map<String, Object> param = new HashMap<>();
	        param.put("s$compania$s", compania);
	        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());
	        String[] campos = new String[3];

	        String[] valores = new String[3];
	        campos[0] = "codigoPlantilla";
	        campos[1] = "fechaPlantilla";
	        campos[2] = "nombreDocDescarga";

	        valores[0] = plantilla;
	        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
	        valores[2] = nombrePlantilla;

	        HashMap<String, String> variablesConsultaW = new HashMap<>();
	        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
	        variablesConsultaW.put("s$tipoContratoInicial$s", tipoContratoInicial);
	        variablesConsultaW.put("s$tipoContratoFinal$s", tipoContratoFinal);
	        variablesConsultaW.put("s$terceroInicial$s", "'" + terceroInicial + "'");
	        variablesConsultaW.put("s$terceroFinal$s",   "'" + terceroFinal + "'");
	        variablesConsultaW.put("s$fechaInicial$s", SysmanFunciones.formatearFechaCadena(fechaInicial,"DD/MM/YYYY"));
	        variablesConsultaW.put("s$fechaFinal$s", SysmanFunciones.formatearFechaCadena(fechaFinal,"DD/MM/YYYY"));
		       
	       
	      
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

	        SessionUtil.cargarModalDatosFlash(
	                        Integer.toString(
	                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
	                                                        .getCodigo()),
	                        SessionUtil.getModulo(),
	                        campos,
	                        valores);

	}

	/**
	 * metodo que llama al oprimir el boton pdf
	 */
	public void oprimirPdfCertificadoPlantilla() {

			generarPdfdesdeWord();
	}
    

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
    private String validarVacio(Object campo) {
        return SysmanFunciones.nvl(campo, " ").toString();
    }	
	/**
	 * Retorna la variable resumen
	 * 
	 * @return resumen
	 */
    public boolean getResumen() {
        return resumen;
    }

    public void setResumen(boolean resumen) {
        this.resumen = resumen;
    }

	/**
	 * Retorna la variable tipoContratoInicial
	 * 
	 * @return tipoContratoInicial
	 */
	public String getTipoContratoInicial() {
		return tipoContratoInicial;
	}

	/**
	 * Asigna la variable tipoContratoInicial
	 * 
	 * @param tipoContratoInicial Variable a asignar en tipoContratoInicial
	 */
	public void setTipoContratoInicial(String tipoContratoInicial) {
		this.tipoContratoInicial = tipoContratoInicial;
	}

	/**
	 * Retorna la variable tipoContratoFinal
	 * 
	 * @return tipoContratoFinal
	 */
	public String getTipoContratoFinal() {
		return tipoContratoFinal;
	}

	/**
	 * Asigna la variable tipoContratoFinal
	 * 
	 * @param tipoContratoFinal Variable a asignar en tipoContratoFinal
	 */
	public void setTipoContratoFinal(String tipoContratoFinal) {
		this.tipoContratoFinal = tipoContratoFinal;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable contratoInicial
	 * 
	 * @return contratoInicial
	 */
	public String getContratoInicial() {
		return contratoInicial;
	}

	/**
	 * Asigna la variable contratoInicial
	 * 
	 * @param contratoInicial Variable a asignar en contratoInicial
	 */
	public void setContratoInicial(String contratoInicial) {
		this.contratoInicial = contratoInicial;
	}

	/**
	 * Retorna la variable contratoFinal
	 * 
	 * @return contratoFinal
	 */
	public String getContratoFinal() {
		return contratoFinal;
	}

	/**
	 * Asigna la variable contratoFinal
	 * 
	 * @param contratoFinal Variable a asignar en contratoFinal
	 */
	public void setContratoFinal(String contratoFinal) {
		this.contratoFinal = contratoFinal;
	}

	/**
	 * Retorna la variable nombreInicial
	 * 
	 * @return nombreInicial
	 */
	public String getNombreInicial() {
		return nombreInicial;
	}

	/**
	 * Asigna la variable nombreInicial
	 * 
	 * @param nombreInicial Variable a asignar en nombreInicial
	 */
	public void setNombreInicial(String nombreInicial) {
		this.nombreInicial = nombreInicial;
	}

	/**
	 * Retorna la variable nombreFinal
	 * 
	 * @return nombreFinal
	 */
	public String getNombreFinal() {
		return nombreFinal;
	}

	/**
	 * Asigna la variable nombreFinal
	 * 
	 * @param nombreFinal Variable a asignar en nombreFinal
	 */
	public void setNombreFinal(String nombreFinal) {
		this.nombreFinal = nombreFinal;
	}

	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable fechaInicial
	 * 
	 * @param fechaInicial Variable a asignar en fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * Asigna la variable fechaFinal
	 * 
	 * @param fechaFinal Variable a asignar en fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoContratoInicial
	 * 
	 * @return listaTipoContratoInicial
	 */
	public RegistroDataModelImpl getListaTipoContratoInicial() {
		return listaTipoContratoInicial;
	}

	/**
	 * Asigna la lista listaTipoContratoInicial
	 * 
	 * @param listaTipoContratoInicial Variable a asignar en
	 *                                 listaTipoContratoInicial
	 */
	public void setListaTipoContratoInicial(RegistroDataModelImpl listaTipoContratoInicial) {
		this.listaTipoContratoInicial = listaTipoContratoInicial;
	}

	/**
	 * Retorna la lista listaTipoContratoFinal
	 * 
	 * @return listaTipoContratoFinal
	 */
	public RegistroDataModelImpl getListaTipoContratoFinal() {
		return listaTipoContratoFinal;
	}

	/**
	 * Asigna la lista listaTipoContratoFinal
	 * 
	 * @param listaTipoContratoFinal Variable a asignar en listaTipoContratoFinal
	 */
	public void setListaTipoContratoFinal(RegistroDataModelImpl listaTipoContratoFinal) {
		this.listaTipoContratoFinal = listaTipoContratoFinal;
	}

	/**
	 * Retorna la lista listaTerceroInicial
	 * 
	 * @return listaTerceroInicial
	 */
	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}

	/**
	 * Asigna la lista listaTerceroInicial
	 * 
	 * @param listaTerceroInicial Variable a asignar en listaTerceroInicial
	 */
	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}

	/**
	 * Retorna la lista listaTerceroFinal
	 * 
	 * @return listaTerceroFinal
	 */
	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}

	/**
	 * Asigna la lista listaTerceroFinal
	 * 
	 * @param listaTerceroFinal Variable a asignar en listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}

	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}

	/**
	 * @return the listaplantillaCertificados
	 */
	public RegistroDataModelImpl getListaplantillaCertificados() {
		return listaplantillaCertificados;
	}

	/**
	 * @param listaplantillaCertificados the listaplantillaCertificados to set
	 */
	public void setListaplantillaCertificados(RegistroDataModelImpl listaplantillaCertificados) {
		this.listaplantillaCertificados = listaplantillaCertificados;
	}

	/**
	 * @param plantilla the plantilla to set
	 */
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	/**
	 * @return the visibleListaPlantillas
	 */
	public Boolean getVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(Boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}
	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
