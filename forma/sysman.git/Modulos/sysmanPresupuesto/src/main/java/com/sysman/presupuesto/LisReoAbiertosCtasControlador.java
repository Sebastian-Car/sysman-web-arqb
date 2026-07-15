package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.presupuesto.enums.LisReoAbiertosCtasControladorEnum;
import com.sysman.presupuesto.enums.LisReoAbiertosCtasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 11/07/2016
 * @modified jguerrero
 * @version 2. 19/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 * @author jgomez
 * @version 4, 10/08/2018 Se ajusta para que el reporte por excel
 * salga plano
 * 
 * @author gfigueredo
 * @version 5, 31/05/2021, Se cambia el parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO por los parametros REPORTE REGISTROS OBLIGACION ABIERTOS POR CUENTA y REPORTE EXCEL REGISTROS OBLIGACION ABIERTOS POR CUENTA,
 *          debido a que el uso del parametro MANEJA AUXILIAR POR FUENTE EN
 *          PRESUPUESTO en este controlador, afecta otros procesos en la
 *          aplicaci�n.
 * @see #REPORTE_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA
 * @see #REPORTE_EXCEL_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA
 * @see #generaReporte(FORMATOS)
 * Integrar
 */
@ManagedBean
@ViewScoped

public class LisReoAbiertosCtasControlador extends BeanBaseModal {
	private static final String REPORTE_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA = "REPORTE REGISTROS OBLIGACION ABIERTOS POR CUENTA";
	private static final String REPORTE_EXCEL_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA = "REPORTE EXCEL REGISTROS OBLIGACION ABIERTOS POR CUENTA";
    private final String compania;
    private final String codigoCons;
    // <DECLARAR_ATRIBUTOS>

    private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    private String terceroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
    private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;

    private String tipoCuenta;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private String anio;
    private boolean ckNit;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisReoAbiertosCtasControlador
     */
    public LisReoAbiertosCtasControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoCons = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.LIS_REO_ABIERTOS_CTAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LisReoAbiertosCtasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        fechaInicial = fechaFinal = new Date();
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        tipoCuenta = "1";
        cargarListaCuentaInicial();
        cargarListaTerceroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR997-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 3, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisReoAbiertosCtasControladorUrlEnum.URL3766
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // 45018
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisReoAbiertosCtasControladorUrlEnum.URL4700
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisReoAbiertosCtasControladorEnum.PARAM2.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

        // CUENTAINICIAL 45020
    }

    public void cargarListaTerceroInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisReoAbiertosCtasControladorUrlEnum.URL5758
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

        // 14067
    }

    public void cargarListaTerceroFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisReoAbiertosCtasControladorUrlEnum.URL6460
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LisReoAbiertosCtasControladorEnum.PARAM6.getValue(),
                        String.valueOf(terceroInicial));
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

        // 14033 TERCEROINICIAL

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

    private boolean auxGeneraReporte() {
        boolean rta = false;
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB822"));
            rta = true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB823"));
            rta = true;
        }
        if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB824"));
            rta = true;
        }
        if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB825"));
            rta = true;
        }
        if (fechaInicial == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB826"));
            rta = true;
        }
        if (fechaFinal == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB827"));
            rta = true;
        }
        return rta;
    }

    private void generaReporte(FORMATOS formato) {

        if (!validarFechas()) {
            return;
        }
        try {

            if (auxGeneraReporte()) {
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = null;
            String excelSalida = null;

            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("tipoCuenta", tipoCuenta);
            
            if ("SI".equals(obtenerParametro(
            		"MANEJA REPORTES IDIPRON",
                    "NO")) && (ckNit) ) {
            		 reporte = "002158LISREOABIERTOSCUENTASNIT";
            	        excelSalida = "002158LISREOABIERTOSCUENTASNIT";     
            }
            else {

            	reporte = obtenerParametro(REPORTE_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA, "001818LisReoAbiertosCuentasF");
				excelSalida = obtenerParametro(REPORTE_EXCEL_REGISTROS_OBLIGACION_ABIERTOS_POR_CUENTA, "001818LisReoAbiertosCuentasF_Excel");
            }
            
          //INI 7716843  _PRESUPUESTO(14/10/2022 MROSERO)
            
			if ("SI".equals(obtenerParametro("MANEJA REFERENCIA Y DEPENDENCIA PARA INFORMES", "NO")))
			{
			reemplazar.put("referencia", "referencia,");
			reemplazar.put("dependencia", "dependencia,");
			reemplazar.put("nombre_dependencia", "nombre_dependencia,");
			} else {
				reemplazar.put("referencia", " ");
				reemplazar.put("dependencia", " ");
				reemplazar.put("nombre_dependencia", " ");
			}			
			//INI 7716843  _PRESUPUESTO(14/10/2022 MROSERO)


            parametros.put("PR_FECHAINICIAL", fechaIni);
            parametros.put("PR_FECHAFINAL", fechaFin);
            int modulo = Integer.parseInt(SessionUtil.getModulo());

            archivoDescarga = JsfUtil.exportarExcelPlano(reporte, excelSalida,
                            ConectorPool.ESQUEMA_SYSMAN, formato, reemplazar,
                            parametros, modulo);

        }
        catch (JRException | IOException | SysmanException
                        | ParseException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(
                            SessionUtil.getCompania(),
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    public void cambiarFechaInicial() {
        anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
        cargarListaCuentaInicial();
        cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    }

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

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
        cargarListaTerceroFinal();
        terceroFinal = null;
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NIT"), "").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

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

    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }

	public boolean isCkNit() {
		return ckNit;
	}

	public void setCkNit(boolean ckNit) {
		this.ckNit = ckNit;
	}

}
