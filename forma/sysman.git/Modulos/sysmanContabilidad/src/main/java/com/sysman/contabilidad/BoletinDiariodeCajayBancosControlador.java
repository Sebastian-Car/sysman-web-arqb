package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BoletinDiariodeCajayBancosControladorEnum;
import com.sysman.contabilidad.enums.BoletinDiariodeCajayBancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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
 *
 * @author dmaldonado
 * @version 2, 16/05/2016 15:47:44 -- Modificado por dmaldonado
 * @modified jsforero
 * @version 2. 06/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @version 3. 20/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 * 
 * @author jreina
 * @version 4, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class BoletinDiariodeCajayBancosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String codigoConst;

    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private Date fecha;
    private StreamedContent archivoDescarga;
    
    private Boolean indFecha = false;
    private Boolean actFecha = false;
    private Date fechaFinal;
    private Boolean totaliza = false;
    private String numeroDigitos;

    
	// </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
 
   

	/**
     * Creates a new instance of BoletinDiariodeCajayBancosControlador
     */
    public BoletinDiariodeCajayBancosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoConst = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.BOLETIN_DIARIODE_CAJAY_BANCOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(BoletinDiariodeCajayBancosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fecha = new Date();
        fechaFinal = new Date();
        actFecha = false;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
        numeroDigitos = "4";
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR705-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BoletinDiariodeCajayBancosControladorUrlEnum.URL3784
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), SysmanFunciones.ano(fecha));
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BoletinDiariodeCajayBancosControladorUrlEnum.URL4820
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), SysmanFunciones.ano(fecha));
        param.put(BoletinDiariodeCajayBancosControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoConst);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFecha() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarckFecha() {
    	if(indFecha) {
    		fechaFinal = new Date();
    		actFecha = true;
    	}
    	else {
    		actFecha = false;
    	}
    	
    }

    public void cambiarTotaliza() {
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoConst).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoConst).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    public void generaInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        
        try {
        	String reporte;
        	if(actFecha) {
        		reporte = "002837BoletinDiarioDeCajaYBancosPorFecha";
        	}else {
        		reporte = SysmanFunciones.nvl(
						ejbSysmanUtil.consultarParametro(
								compania, 
								"FORMATO BOLETIN DIARIO DE CAJA Y BANCOS", 
								modulo, new Date(), false),
						"000774BoletinDiarioDeCajaYBancos").toString();        		
        	}
        	
        	
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            Date fechaAct = new Date();
            reemplazar.put("id_codigo", "ID");
            reemplazar.put("mesAnterior", SysmanFunciones.getParteFecha(fecha,
                            Calendar.MONTH));
            reemplazar.put("id_cuenta", "ID");
            reemplazar.put("anio", SysmanFunciones.getParteFecha(fecha,
                            Calendar.YEAR));
            reemplazar.put("tipoInicial", "AAA");
            reemplazar.put("tipoFinal", "ZZZ");
            reemplazar.put("cuentaInicial", codigoInicial);
            reemplazar.put("cuentaFinal", codigoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.convertirAFechaCadena(fecha));
            if(actFecha) {
            	reemplazar.put("fechaFinal",
                        SysmanFunciones.convertirAFechaCadena(fechaFinal));
            	
            }else {
            	reemplazar.put("fechaFinal",
                        SysmanFunciones.convertirAFechaCadena(fecha));
            }
            reemplazar.put("es_id", 1);
            reemplazar.put("filtrosTercero", "");
            reemplazar.put("filtrosCentro", "");
            reemplazar.put("condicionReferencias", "");
            parametros.put("PR_ENTREFECHAS",
                            "DESDE EL "
                                + SysmanFunciones.convertirAFechaCadena(fecha)
                                + " Y "
                                + SysmanFunciones.convertirAFechaCadena(fecha));

            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_EL_BOLETIN_DIARIO",
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CARGO DE QUIEN FIRMA EL BOLETIN DIARIO",
                                            SessionUtil.getModulo(),
                                            fechaAct, true));
            parametros.put("PR_FIRMA_DEL_TESORERO_BOLETIN_DIARIO",
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "FIRMA DEL TESORERO BOLETIN DIARIO",
                                            SessionUtil.getModulo(),
                                            fechaAct, true));
            parametros.put("PR_NOMBRE_DE_QUIEN_ELABORO_EL_BOLETIN_DIARIO",
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "NOMBRE DE QUIEN ELABORO EL BOLETIN DIARIO",
                                            SessionUtil.getModulo(),
                                            fechaAct, true));
            parametros.put("PR_CARGO_DE_QUIEN_ELABORO_EL_BOLETIN_DIARIO",
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CARGO DE QUIEN ELABORO EL BOLETIN DIARIO",
                                            SessionUtil.getModulo(),
                                            fechaAct, true));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FECHAREPORTE", fecha);
            String strsqlBase = Reporteador.resuelveConsulta(
                            "800044BaseAuxiliares", Integer.valueOf(modulo),
                            reemplazar);
            reemplazar = new HashMap<>();
            reemplazar.put("baseAux", strsqlBase);
            System.out.println(strsqlBase);
            reemplazar.put("fecha",
                            SysmanFunciones.convertirAFechaCadena(fecha));
            reemplazar.put("mesAnterior", SysmanFunciones.getParteFecha(fecha,
                    Calendar.MONTH));
		    reemplazar.put("anio", SysmanFunciones.getParteFecha(fecha,
		                    Calendar.YEAR));
		    reemplazar.put("cuentaInicial", codigoInicial);
		    reemplazar.put("cuentaFinal", codigoFinal);
		    reemplazar.put("fechaInicial",
		                    SysmanFunciones.convertirAFechaCadena(fecha));
		    if(actFecha) {
            	reemplazar.put("fechaFinal",
                        SysmanFunciones.convertirAFechaCadena(fechaFinal));
            	  parametros.put("PR_ENTREFECHA",
                          "DESDE EL "
                              + SysmanFunciones.convertirAFechaCadena(fecha)
                              + " Y "
                              + SysmanFunciones.convertirAFechaCadena(fechaFinal));
            	
            }else {
	            reemplazar.put("fechaFinal",
	                SysmanFunciones.convertirAFechaCadena(fecha));
	            parametros.put("PR_ENTREFECHA",
	                "DESDE EL " + SysmanFunciones.convertirAFechaCadena(fecha));
            }
		   
			if (totaliza && numeroDigitos != null) {
			    parametros.put("NUMERODIGITOS", numeroDigitos);
			} else {
			    parametros.put("NUMERODIGITOS", null);  
			}

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
            				reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);     
        }
        catch (SystemException | ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    
    
    public Boolean getIndFecha() {
		return indFecha;
	}

	public void setIndFecha(Boolean indFecha) {
		this.indFecha = indFecha;
	}
	
	public Boolean getActFecha() {
		return actFecha;
	}

	public void setActFecha(Boolean actFecha) {
		this.actFecha = actFecha;
	}
	
	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the totaliza
	 */
	public Boolean getTotaliza() {
		return totaliza;
	}

	/**
	 * @param totaliza the totaliza to set
	 */
	public void setTotaliza(Boolean totaliza) {
		this.totaliza = totaliza;
	}

	/**
	 * @return the numeroDigitos
	 */
	public String getNumeroDigitos() {
		return numeroDigitos;
	}

	/**
	 * @param numeroDigitos the numeroDigitos to set
	 */
	public void setNumeroDigitos(String numeroDigitos) {
		this.numeroDigitos = numeroDigitos;
	}

	
  
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
