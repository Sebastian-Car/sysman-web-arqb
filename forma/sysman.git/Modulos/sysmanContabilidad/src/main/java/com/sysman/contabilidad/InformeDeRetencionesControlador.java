package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.InformeDeRetencionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.contabilidad.enums.InformeDeRetencionesControladorEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 19/05/2016 14:56:03 -- Modificado por jrodriguezr
 * 
 * @version 2, 10/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 * @author asana
 * @version 3, 12/06/2017, se implementa enum en formulario y se modifica conexion.
 */
@ManagedBean
@ViewScoped
public class InformeDeRetencionesControlador extends BeanBaseModal {
    private final String compania;
    private final String consCodigo;
    // <DECLARAR_ATRIBUTOS>
    private boolean especial;
    private String cuentaInicial;
    private String cuentaFinal;
    private String anio;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    
    private boolean ckEspecial;
    private String referenciaInicial;
    private String referenciaFin;
    private String bancoIni;
    private String bancoFin;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaReferenciaIni;
    private RegistroDataModelImpl listaReferenciaFin;
    private RegistroDataModelImpl listaBancoIni;
    private RegistroDataModelImpl listaBancoFin;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private String terceroInicial;
   	private String terceroFinal ;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of InformeDeRetencionesControlador
     */
    public InformeDeRetencionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigo="CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_DE_RETENCIONES_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(InformeDeRetencionesControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        fechaInicial = fechaFinal = new Date();
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaReferenciaIni();  
        cargarListaBancoIni(); 
        cargarListaTerceroInicial();
        
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    	referenciaFin = SysmanConstantes.DEFECTOFINAL_STRING;
    	
    	bancoIni = SysmanConstantes.DEFECTOINICIAL_STRING;
    	bancoFin = SysmanConstantes.DEFECTOFINAL_STRING;
    	
    	terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    	terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    	
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(InformeDeRetencionesControladorUrlEnum.URL3314
                                        .getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, consCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(InformeDeRetencionesControladorUrlEnum.URL4368.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        param.put(GeneralParameterEnum.CUENTA.getName(),cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, consCodigo);
    }

    /**
     * 
     * Carga la lista listaReferenciaIni
     *
     */
    public void cargarListaReferenciaIni(){
    	//listaReferenciaIni = 
    	 UrlBean urlBean = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(
                		 InformeDeRetencionesControladorUrlEnum.URL13028
                                                 .getValue());
	 Map<String, Object> param = new TreeMap<>();
	 param.put(GeneralParameterEnum.COMPANIA.name(), compania);
	
	 listaReferenciaIni = new RegistroDataModelImpl(urlBean.getUrl(),
	                 urlBean.getUrlConteo().getUrl(), param,
	                 true, "CODIGO");
    }
    /**
     * 
     * Carga la lista listaReferenciaFin
     *
     */
    public void cargarListaReferenciaFin(){
    	//listaReferenciaFin =
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					InformeDeRetencionesControladorUrlEnum.URL13030
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.name(), compania);
    	param.put("REFERENCIAINICIAL", referenciaInicial);

    	listaReferenciaFin = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param,
    			true, "CODIGO");
    }
    /**
     * 
     * Carga la lista listaBancoIni
     *
     */
    public void cargarListaBancoIni(){

        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        InformeDeRetencionesControladorUrlEnum.URL29045
                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),anio);
        
        listaBancoIni = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }
    /**
     * 
     * Carga la lista listaBancoFin
     *
     */
    public void cargarListaBancoFin(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    	        .getUrlServiceByUrlByEnumID(
    	                        InformeDeRetencionesControladorUrlEnum.URL29047
    	                        .getValue());
    	        Map<String, Object> param = new TreeMap<>();
    	        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
    	        param.put(GeneralParameterEnum.ANO.getName(),anio);
    	        param.put("BANCOINICIAL", bancoIni);
    	        
    	        
    	        listaBancoFin = new RegistroDataModelImpl(urlBean.getUrl(),
    	                        urlBean.getUrlConteo().getUrl(), param,
    	                        true, "CODIGO");
    }
    
    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InformeDeRetencionesControladorUrlEnum.URL14001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }
    
    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		InformeDeRetencionesControladorUrlEnum.URL14026
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformeDeRetencionesControladorEnum.NITINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validar(){
        boolean estado=true;
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB168"));
            estado=false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB169"));
            estado=false;
        }
        if ((cuentaInicial == null) || "".equals(cuentaInicial)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB835"));
            estado=false;
        }
        if ((cuentaFinal == null) || "".equals(cuentaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB836"));
            estado=false;
        }
        
        if ((terceroInicial == null) || "".equals(terceroInicial.trim())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4485"));
            estado=false;
        }
             
  
        return estado;
    }

    private void generaReporte(FORMATOS formato) {
        if(validar()){
            try {
                HashMap<String, Object> reemplazar = new HashMap<>();
                String reporte = especial ? "000784RelRetencionesXTerceroSog"
                    : "000785InformeDeRetencionesLis";
                reporte = ckEspecial ? "002573InformeDeRetencionesLis_SINCHI" : reporte;
                String fechaIni = SysmanFunciones
                                .convertirAFechaCadena(fechaInicial);
                String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

                reemplazar.put("fechaInicial",
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.formatearFecha(fechaFinal));
                reemplazar.put("cuentaInicial", cuentaInicial);
                reemplazar.put("cuentaFinal", cuentaFinal);
                
                reemplazar.put("referenciaInicial", referenciaInicial);
                reemplazar.put("referenciaFin", referenciaFin);
                
                reemplazar.put("bancoIni", bancoIni);
                reemplazar.put("bancoFin", bancoFin);
                
                reemplazar.put("terceroInicial", terceroInicial);
                reemplazar.put("terceroFinal", terceroFinal);
                
                
                

                // MANEJO DE PARAMETROS DEL REPORTE
                Map<String, Object> parametros = new HashMap<>();
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar,
                                parametros);
                System.out.println();
                parametros.put("PR_NOMBRECOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNombre());
                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_DIRECCIONCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_TELEFONOCOMPANIA", "");
                parametros.put("PR_FECHAINICIAL", fechaIni);
                parametros.put("PR_FECHAFINAL", fechaFin);
                parametros.put("PR_CUENTAINICIAL", cuentaInicial);
                parametros.put("PR_CUENTAFINAL", cuentaFinal);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException | ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }



    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        cuentaInicial = null;
        cuentaFinal = null;
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control ckEspecial
     * 
     * 
     */
    public void cambiarckEspecial() {
    	//<CODIGO_DESARROLLADO>
    	if(ckEspecial) {
    		especial = false;
    		JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB4465"));
    	}else {
    		ckEspecial = false;
    	}
    	//</CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Especial
     * 
     * 
     */
    public void cambiarEspecial() {
    	//<CODIGO_DESARROLLADO>
    	if(especial) {
    		ckEspecial = false;
    	}else {
    		especial = true;
    	}
    	//</CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(consCodigo).toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(consCodigo).toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaIni
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaIni(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	referenciaInicial= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
    	cargarListaReferenciaFin();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFin(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	referenciaFin= registroAux.getCampos().get("CODIGO").toString();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoIni
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoIni(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	bancoIni= registroAux.getCampos().get("CODIGO").toString();
    	cargarListaBancoFin();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoFin
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoFin(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	bancoFin= registroAux.getCampos().get("CODIGO").toString();
    }
    
    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = asignarValorCampo(registroAux, "NIT");
        terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        cargarListaTerceroFinal();
    }
    
    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = asignarValorCampo(registroAux, "NIT");
    }
    
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    
	 public String getTerceroInicial() {
		 return terceroInicial;
	}

	public void setTerceroInicial(String terceroInicial) {
		 this.terceroInicial = terceroInicial;
	}

   public String getTerceroFinal() {
		return terceroFinal;
	}

	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	/**
	 * @return the ckEspecial
	 */
	public boolean isCkEspecial() {
		return ckEspecial;
	}

	/**
	 * @param ckEspecial the ckEspecial to set
	 */
	public void setCkEspecial(boolean ckEspecial) {
		this.ckEspecial = ckEspecial;
	}

	/**
	 * @return the referenciaInicial
	 */
	public String getReferenciaInicial() {
		return referenciaInicial;
	}

	/**
	 * @param referenciaInicial the referenciaInicial to set
	 */
	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}

	/**
	 * @return the referenciaFin
	 */
	public String getReferenciaFin() {
		return referenciaFin;
	}

	/**
	 * @param referenciaFin the referenciaFin to set
	 */
	public void setReferenciaFin(String referenciaFin) {
		this.referenciaFin = referenciaFin;
	}

	/**
	 * @return the bancoIni
	 */
	public String getBancoIni() {
		return bancoIni;
	}

	/**
	 * @param bancoIni the bancoIni to set
	 */
	public void setBancoIni(String bancoIni) {
		this.bancoIni = bancoIni;
	}

	/**
	 * @return the bancoFin
	 */
	public String getBancoFin() {
		return bancoFin;
	}

	/**
	 * @param bancoFin the bancoFin to set
	 */
	public void setBancoFin(String bancoFin) {
		this.bancoFin = bancoFin;
	}

	/**
	 * @return the listaReferenciaIni
	 */
	public RegistroDataModelImpl getListaReferenciaIni() {
		return listaReferenciaIni;
	}

	/**
	 * @param listaReferenciaIni the listaReferenciaIni to set
	 */
	public void setListaReferenciaIni(RegistroDataModelImpl listaReferenciaIni) {
		this.listaReferenciaIni = listaReferenciaIni;
	}

	/**
	 * @return the listaReferenciaFin
	 */
	public RegistroDataModelImpl getListaReferenciaFin() {
		return listaReferenciaFin;
	}

	/**
	 * @param listaReferenciaFin the listaReferenciaFin to set
	 */
	public void setListaReferenciaFin(RegistroDataModelImpl listaReferenciaFin) {
		this.listaReferenciaFin = listaReferenciaFin;
	}

	/**
	 * @return the listaBancoIni
	 */
	public RegistroDataModelImpl getListaBancoIni() {
		return listaBancoIni;
	}

	/**
	 * @param listaBancoIni the listaBancoIni to set
	 */
	public void setListaBancoIni(RegistroDataModelImpl listaBancoIni) {
		this.listaBancoIni = listaBancoIni;
	}

	/**
	 * @return the listaBancoFin
	 */
	public RegistroDataModelImpl getListaBancoFin() {
		return listaBancoFin;
	}

	/**
	 * @param listaBancoFin the listaBancoFin to set
	 */
	public void setListaBancoFin(RegistroDataModelImpl listaBancoFin) {
		this.listaBancoFin = listaBancoFin;
	}
    
	public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }
	

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
