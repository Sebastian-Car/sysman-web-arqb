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
import com.sysman.presupuesto.enums.FrmobligacionesypagosControladorEnum;
import com.sysman.presupuesto.enums.FrmobligacionesypagosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 21/06/2016
 * 
 * @version 2, 19/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class FrmobligacionesypagosControlador extends BeanBaseModal {
	private static final String FORMATO_OBLIGACIONES_Y_PAGOS = "FORMATO OBLIGACIONES Y PAGOS";
    private final String compania;
    private final String modulo;

    private final String strCodigo;

    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private String nomCuentaInicial;
    private String nomCuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private boolean conId;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
	private EjbSysmanUtilRemote sysmanUtil;
    /**
     * Creates a new instance of FrmobligacionesypagosControlador
     */
    public FrmobligacionesypagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        strCodigo = "CODIGO";

        try {
            // 936
            numFormulario = GeneralCodigoFormaEnum.FRMOBLIGACIONESYPAGOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            ano = SysmanFunciones
                            .ano(new Date());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmobligacionesypagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmobligacionesypagosControladorUrlEnum.URL3752
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmobligacionesypagosControladorUrlEnum.URL4177
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaCuentaFinal() {
		String cuentaFinal;
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmobligacionesypagosControladorUrlEnum.URL5293
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmobligacionesypagosControladorEnum.PARAM0.getValue(),
                        cuentaInicial);
		// MROSERO CC1705 Validación: si cuentaInicial inicia con letra, usar "ZZZZZZZZZZ", si no, "9999999999999999"

		if (cuentaInicial != null && !cuentaInicial.isEmpty() && Character.isLetter(cuentaInicial.charAt(0))) {
			cuentaFinal = "ZZZZZZZZZZZZZZZZ"; 
		} else {
			cuentaFinal = "9999999999999999"; 
		}
        param.put(FrmobligacionesypagosControladorEnum.PARAM1.getValue(),cuentaFinal);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }
    
	private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(SessionUtil.getCompania(), nombreParametro,
					SessionUtil.getModulo(), new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}
	
	
    public void obtenerReporte(FORMATOS formatos) {
        HashMap<String, Object> reemplazar = new HashMap<>();
    	String reporte = null;		

        try {
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("ano", ano);
            reemplazar.put("conId", conId ? "S" : "N");
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put("PR_CUENTAFINAL", cuentaFinal);

            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            parametros.put("PR_CUENTAINICIAL", cuentaInicial);

            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            
            
            //CORPOBOYACA 800544OBLIGACIONESYPAGOS
            reporte = obtenerParametro(FORMATO_OBLIGACIONES_Y_PAGOS, "000931ObligacionesyPagos");

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);
            
            if ("800544OBLIGACIONESYPAGOS".equals(reporte)){
            	
			archivoDescarga = JsfUtil.exportarExcelPlano(reporte, reporte, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
					reemplazar, parametros,Integer.valueOf(modulo));
			
            }else             	
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formatos);
			
        }
        catch (JRException | IOException | SysmanException | ParseException | NumberFormatException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.ano(fechaInicial) != Integer.valueOf(ano)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB958"));
            return;
        }
        if (SysmanFunciones.ano(fechaFinal) != Integer.valueOf(ano)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB959"));
            return;
        }
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.ano(fechaInicial) != Integer.valueOf(ano)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB958"));
            return;
        }
        if (SysmanFunciones.ano(fechaFinal) != Integer.valueOf(ano)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB959"));
            return;
        }
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), " ")
                        .toString();
        nomCuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
        cuentaFinal = null;
        nomCuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigo), " ")
                        .toString();
        nomCuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), " ")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getNomCuentaInicial() {
        return nomCuentaInicial;
    }

    public void setNomCuentaInicial(String nomCuentaInicial) {
        this.nomCuentaInicial = nomCuentaInicial;
    }

    public String getNomCuentaFinal() {
        return nomCuentaFinal;
    }

    public void setNomCuentaFinal(String nomCuentaFinal) {
        this.nomCuentaFinal = nomCuentaFinal;
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

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }
    
    public boolean isConId() {
        return conId;
    }

    public void setConId(boolean conId) {
        this.conId = conId;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}