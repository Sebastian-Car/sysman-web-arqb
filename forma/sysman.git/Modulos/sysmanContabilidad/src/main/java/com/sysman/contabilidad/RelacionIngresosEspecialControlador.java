package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelacionIngresosEspecialControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 23/05/2016
 * 
 * @modified jguerrero
 * @version 2. 07/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author asana
 * @version 3, 12/06/2017 Se implementa enum en formulario y se modifica Conexi�n.
 */
@ManagedBean
@ViewScoped
public class RelacionIngresosEspecialControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String anio;
    private StreamedContent archivoDescarga;
    /**
     * Variable para indicar si se visualizan los combos cuenta inicial y final
     * **/
    private boolean verCuenta;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    private final String codigoCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RelacionIngresosEspecialControlador
     */
    public RelacionIngresosEspecialControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";

        try {
            numFormulario = GeneralCodigoFormaEnum.RELACION_INGRESOS_ESPECIAL_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            RelacionIngresosEspecialControlador.class.getName())
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
        cuentaInicial = "1";

        cargarListaCuentaFinal();
        cuentaFinal = "999999999999999999";
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
    	
    	try {
			verCuenta = "SI".equals(SysmanFunciones.nvl(
			        ejbSysmanUtil.consultarParametro(compania,
			                        "GENERAR RELACION DE INGRESOS POR CUENTA",
			                        SessionUtil.getModulo(), new Date(),
			                        true),
			        "NO"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RelacionIngresosEspecialControladorUrlEnum.URL3448
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    public void cargarListaCuentaFinal() {

        if (cuentaInicial != null) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RelacionIngresosEspecialControladorUrlEnum.URL4379
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);

            param.put("CUENTAINICIAL", cuentaInicial);

            listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoCons);
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validaciones() {
        boolean rta = true;

        if(verCuenta)
        {
	        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
	            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB137"));
	            rta = false;
	        }
	        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
	            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB138"));
	            rta = false;
	        }
        }
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB124"));
            rta = false;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB126"));
            rta = false;
        }
        return rta;
    }

    private void generaReporte(FORMATOS formato) {

        if (!validarFechas()) {
            return;
        }

        if (!validaciones()) {
            return;
        }
       
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String reporte = "000808RelacionDeIngresosEsp";
            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);
            String nombre1 = "";
            String nombre2 = "";
            String cargo1 = "";
            String cargo2 = "";

            nombre1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA 1 EN RELACION DE INGRESOS ESPECIAL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nombre2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA 2 EN RELACION DE INGRESOS ESPECIAL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            cargo1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO 1 EN RELACION DE INGRESOS ESPECIAL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            cargo2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO 2 EN RELACION DE INGRESOS ESPECIAL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            if(!verCuenta)
            {
            	cuentaInicial = "0";
            	cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
            }
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);

            // MANEJO DE PARAMETROS DEL REPORTE
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            parametros.put("PR_CARGO_2_EN_RELACION_DE_INGRESOS_ESPECIAL",
                            cargo2);
            parametros.put("PR_CARGO_1_EN_RELACION_DE_INGRESOS_ESPECIAL",
                            cargo1);
            parametros.put("PR_FIRMA_1_EN_RELACION_DE_INGRESOS_ESPECIAL",
                            nombre1);
            parametros.put("PR_FIRMA_2_EN_RELACION_DE_INGRESOS_ESPECIAL",
                            nombre2);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_FECHAINICIAL", fechaIni);
            parametros.put("PR_FECHAFINAL", fechaFin);
            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);

            
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
  
            
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        cuentaInicial = null;
        cargarListaCuentaFinal();
        cuentaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
                        .toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoCons), "")
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
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

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }

	public boolean isVerCuenta() {
		return verCuenta;
	}

	public void setVerCuenta(boolean verCuenta) {
		this.verCuenta = verCuenta;
	}

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
