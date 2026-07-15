package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.QuienafectoadocumentoControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 20/06/2016
 *
 * @author eamaya
 * @version 2.0, 08/02/2018, Proceso de Refactoring DSS ,cambio de
 * numero de formulario por enum y creacion del metodo
 * generarReporte()
 *
 */
@ManagedBean
@ViewScoped

public class QuienafectoadocumentoControlador extends BeanBaseModal {
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
    /**
     * Constante que almacena el codigo de la compania
     */
    private final String compania;
    /**
     * Constante que almacena el modulo de la compania
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del indicador entre fechas
     */
    private Boolean indicador;
    /**
     * Atributo que almacena el tipo de comrobante seleccionado en la
     * forma
     */
    private String tipo;

    /**
     * Atributo que almacena el nombre del tipo de comprobante
     * seleccionado en la vista
     */
    private String nombreTipoComprobante;
    /**
     * Atributo que almacena el numero de comprobante seleccionado en
     * la forma
     */
    private String numero;

    /**
     * Atributo que almacena la fecha del numero de comprobante
     */
    private Date fechaComprobante;
    /**
     * Atributo que almacena el valor del numero de comprobante
     */
    private Double valorComprobante;

    /**
     * Atributo que almacena la fecha inicial de la forma
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final de la forma
     */
    private Date fechaFinal;

    /**
     * Atributo que gestiona el bloqueo de la fecha inicial
     */
    private Boolean bloqueaFechaInicial;
    /**
     * Atributo que gestiona el bloqueo de la fecha final
     */
    private Boolean bloqueaFechaFinal;

    /**
     * Atributo que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo para cargar la lista año
     */
    private List<Registro> listaAnio;
    /**
     * Atributo para la variable anio
     */
    private String anio;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los numero de comprobante dependiendo del tipo
     * de comprobantes
     */
    private RegistroDataModelImpl listaNumero;
    /**
     * Lista que carga los tipos de comprobante
     */
    private RegistroDataModelImpl listaTipo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of QuienafectoadocumentoControlador
     */
    public QuienafectoadocumentoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.QUIENAFECTOADOCUMENTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            indicador = false;
            bloqueaFechaInicial = true;
            bloqueaFechaFinal = true;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(QuienafectoadocumentoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	cargarListaAnio();
    	anio = Integer.toString(SysmanFunciones.ano(new Date()));
    	cambiarAnio();
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		QuienafectoadocumentoControladorUrlEnum.URL5960
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
       // cargarListaMes();
    	cargarListaNumero();
    	tipo = null;
        numero = null;
    	
    	cargarListaTipo();
        // </CODIGO_DESARROLLADO>
    }
    
    public void cargarListaTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuienafectoadocumentoControladorUrlEnum.URL2972
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

    }

    /**
     *
     * Carga la lista listaNumero
     *
     */
    public void cargarListaNumero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        QuienafectoadocumentoControladorUrlEnum.URL5959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        param.put("TIPO", tipo);

        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "COMPROBANTE");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {

//            String reporte = "000920Afectacionesdocu";
        	String reporte = null;
			try {
				reporte = ejbSysmanUtil.consultarParametro(compania,
				         "FORMATO AFECTACIONES",
				         modulo, new Date(),false);
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            
            reemplazar.put("tipoComprobante", tipo);
            reemplazar.put("numeroComprobante", numero);
            reemplazar.put("anio", anio);

            if (indicador) {
                reemplazar.put("condicion", "1");
                reemplazar.put("fechaInicial", SysmanFunciones
                                .convertirAFechaCadena(fechaInicial));
                reemplazar.put("fechaFinal", SysmanFunciones
                                .convertirAFechaCadena(fechaFinal));
            }
            else {
                reemplazar.put("condicion", "0");
            }

            parametros.put("PR_NOMBRECOMPROBANTE",
                            nombreTipoComprobante);
            parametros.put("PR_NUMEROCOMPROBANTE",
                            numero);
            parametros.put("PR_FECHACOMPROBANTE",
                            SysmanFunciones.convertirAFechaCadena(fechaComprobante));

            parametros.put("PR_VALORCOMPROBANTE",
                            valorComprobante);

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarIndicador() {
        // <CODIGO_DESARROLLADO>
        if (indicador) {
            bloqueaFechaInicial = false;
            bloqueaFechaFinal = false;
        }
        else {
            bloqueaFechaInicial = true;
            bloqueaFechaFinal = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();

        nombreTipoComprobante = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        " ")
                        .toString();

        numero = null;

        cargarListaNumero();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = SysmanFunciones
                        .nvl(registroAux.getCampos().get("COMPROBANTE"), "")
                        .toString();

        fechaComprobante = (Date) registroAux.getCampos().get(
                        GeneralParameterEnum.FECHA.getName());

        valorComprobante = Double.parseDouble(SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.VALOR.getName()),
                                        "0")
                        .toString());

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getTipo() {
        return tipo;
    }

    public Boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(Boolean indicador) {
        this.indicador = indicador;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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

    public Boolean getBloqueaFechaInicial() {
        return bloqueaFechaInicial;
    }

    public void setBloqueaFechaInicial(Boolean bloqueaFechaInicial) {
        this.bloqueaFechaInicial = bloqueaFechaInicial;
    }

    public Boolean getBloqueaFechaFinal() {
        return bloqueaFechaFinal;
    }

    public void setBloqueaFechaFinal(Boolean bloqueaFechaFinal) {
        this.bloqueaFechaFinal = bloqueaFechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaNumero
     *
     * @return listaNumero
     */
    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    /**
     * Asigna la lista listaNumero
     *
     * @param listaNumero
     * Variable a asignar en listaNumero
     */
    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public String getNombreTipoComprobante() {
        return nombreTipoComprobante;
    }

    public void setNombreTipoComprobante(String nombreTipoComprobante) {
        this.nombreTipoComprobante = nombreTipoComprobante;
    }

    public Date getFechaComprobante() {
        return fechaComprobante;
    }

    public void setFechaComprobante(Date fechaComprobante) {
        this.fechaComprobante = fechaComprobante;
    }

    public Double getValorComprobante() {
        return valorComprobante;
    }

    public void setValorComprobante(Double valorComprobante) {
        this.valorComprobante = valorComprobante;
    }

	/**
	 * @return the listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * @param listaAnio the listaAnio to set
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * @param anio the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
