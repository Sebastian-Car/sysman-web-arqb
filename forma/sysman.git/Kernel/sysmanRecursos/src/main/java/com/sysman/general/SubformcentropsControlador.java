package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.SubformcentropsControladorEnum;
import com.sysman.general.enums.SubformcentropsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author cmanrique *
 * @modified jguerrero
 * @version 2. 04/04/2017 Se realizo el refactory del formulario.
 * Además, se ajustaron los errores del sonar
 * 
 * @author asana 
 * @version 3, 12/06/2017
 * Redireccion de formulario.
 */
@ManagedBean
@ViewScoped

public class SubformcentropsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    private Map<String, Object> rid;
    private String anoQr;
    private String mesInicialQr;
    private String mesFinalQr;
    private String centroCosto;
    private String nombreCentro;
    private String codAuxiliar;
    private String nombreAux;
    private String formulario;
    private String codigo;
    private String nombre;
    private String condicion;
    private String titulo;
    private String etiquetaNombre;
    private String etiquetaCodigo;
    private String lbTitulo;
    private String codTercero;
    private String nombreTercero;
    private String sucursalTercero;
    private String codReferencia;
    private String nombreReferencia;
    private final String centroCostoCons;
    private final String terceroCons;
    private final String auxiliarCons;
    private final String referenciaCons;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of SubformcentropsControlador
     */
    public SubformcentropsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        centroCostoCons = "centroCosto";
        terceroCons = "tercero";
        auxiliarCons = "auxiliar";
        referenciaCons = "referencia";
        try {
            Map<String, Object> parametros = SessionUtil.getFlash();

            anoQr = parametros.get("anoQr").toString();
            mesInicialQr = parametros.get("mesInicialQr").toString();
            mesFinalQr = parametros.get("mesFinalQr").toString();
            formulario = parametros.get("formulario").toString();
            switch (formulario) {
            case "tercero":
                codTercero = parametros.get("codTercero").toString();
                nombreTercero = parametros.get("nombreTercero").toString();
                sucursalTercero = parametros.get("sucursalTercero").toString();
                break;
            case "auxiliar":
                codAuxiliar = parametros.get("codAuxiliar").toString();
                nombreAux = parametros.get("nombreAux").toString();
                break;
            case "referencia":
                codReferencia = parametros.get("codReferencia").toString();
                nombreReferencia = parametros.get("nombreReferencia")
                                .toString();
                break;
            case "centroCosto":
                centroCosto = parametros.get(centroCostoCons).toString();
                nombreCentro = parametros.get("nombreCentro").toString();
                break;
            default:
                break;
            }

            numFormulario = GeneralCodigoFormaEnum.SUBFORMCENTROPS_CONTROLADOR.getCodigo();

            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {

            SessionUtil.cleanFlash();

        }

    }

    @PostConstruct
    public void inicializar() {
        if (auxiliarCons.equals(formulario)) {
            etiquetaCodigo = idioma.getString("TG_AUXILIAR2") + " ";
            codigo = codAuxiliar;
            etiquetaNombre = idioma.getString("TB_TB416") + " ";
            nombre = nombreAux;
            lbTitulo = idioma.getString("TB_TB3063");
            titulo = idioma.getString("TB_TB3064");
        }
        else if (centroCostoCons.equals(formulario)) {
            etiquetaCodigo = idioma.getString("TG_CENTRO_DE_COSTO4") + " ";
            codigo = centroCosto;
            etiquetaNombre = idioma.getString("TB_TB420") + " ";
            nombre = nombreCentro;
            lbTitulo = idioma.getString("TB_TB3065");
            titulo = idioma.getString("TB_TB3066");
        }
        else if (referenciaCons.equals(formulario)) {
            etiquetaCodigo = idioma.getString("TG_REFERENCIA2") + " ";
            codigo = codReferencia;
            etiquetaNombre = "Nombre Referencia: ";
            nombre = nombreReferencia;
            lbTitulo = idioma.getString("TB_TB3068");
            titulo = idioma.getString("TB_TB3069");
        }
        else if (terceroCons.equals(formulario)) {

            etiquetaCodigo = idioma.getString("TG_TERCERO3") + " ";
            codigo = codTercero;
            etiquetaNombre = idioma.getString("TT_LB31847") + " ";
            nombre = nombreTercero;
            lbTitulo = idioma.getString("TB_TB3070");
            titulo = idioma.getString("TB_TB3071");
        }
        tabla = SubformcentropsControladorEnum.PARAM2.getValue();
        buscarLlave();
        reasignarOrigen();

        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void abrirFormulario() {

        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ANOQR", anoQr);
        parametrosListado.put("MESINICIALQR",
                        mesInicialQr);
        parametrosListado.put("MESFINALQR",
                        mesFinalQr);

        if (auxiliarCons.equals(formulario)) {
            parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
                            codAuxiliar);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentropsControladorUrlEnum.URL0001
                                                            .getValue());
        }
        if (centroCostoCons.equals(formulario)) {
            parametrosListado.put(GeneralParameterEnum.CENTRODECOSTO.getName(),
                            centroCosto);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentropsControladorUrlEnum.URL0002
                                                            .getValue());
        }

        if (referenciaCons.equals(formulario)) {
            parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(),
                            codReferencia);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentropsControladorUrlEnum.URL0003
                                                            .getValue());

        }
        if (terceroCons.equals(formulario)) {
            parametrosListado.put(GeneralParameterEnum.TERCERO.getName(),
                            codTercero);
            parametrosListado.put("SUCURSALTERCERO",
                            sucursalTercero);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentropsControladorUrlEnum.URL0004
                                                            .getValue());
        }

    }
    private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos
		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte; 
		// Codigo del reporte
	
				reporte = "001944LisMovTercerosP";

			

		// <REEMPLAZAR VARIABLES EN CONSULTA>
         reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania); 
	reemplazar.put("ano", anoQr);
		reemplazar.put("fechaInicial", mesInicialQr); 
		reemplazar.put("fechaFinal", mesFinalQr);
		reemplazar.put("terceroNit", codigo); 
		reemplazar.put("terceroSucursal", sucursalTercero);

		//</REEMPLAZAR VARIABLES EN CONSULTA
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
					.getNombre().toUpperCase());
			// </ENVIAR PARAMETROS AL REPORTE>
			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(modulo),
					reemplazar, parametros);
			/*-aqui reporte hace referencia al nombre del reporte*/
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,	ConectorPool.ESQUEMA_SYSMAN, formato);
		}

		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
		} 
    
    public void oprimirimprimirPdf() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
       //</CODIGO_DESARROLLADO>
   }
    public void oprimirimprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga = null; 
        generarReporte(ReportesBean.FORMATOS.EXCEL);
       //</CODIGO_DESARROLLADO>
   }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado de la clase BeanBase
    }
    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getAnoQr() {
        return anoQr;
    }

    public void setAnoQr(String anoQr) {
        this.anoQr = anoQr;
    }

    public String getMesInicialQr() {
        return mesInicialQr;
    }

    public void setMesInicialQr(String mesInicialQr) {
        this.mesInicialQr = mesInicialQr;
    }

    public String getMesFinalQr() {
        return mesFinalQr;
    }

    public void setMesFinalQr(String mesFinalQr) {
        this.mesFinalQr = mesFinalQr;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public String getCodAuxiliar() {
        return codAuxiliar;
    }

    public void setCodAuxiliar(String codAuxiliar) {
        this.codAuxiliar = codAuxiliar;
    }

    public String getNombreAux() {
        return nombreAux;
    }

    public void setNombreAux(String nombreAux) {
        this.nombreAux = nombreAux;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

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

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getEtiquetaNombre() {
        return etiquetaNombre;
    }

    public void setEtiquetaNombre(String etiquetaNombre) {
        this.etiquetaNombre = etiquetaNombre;
    }

    public String getEtiquetaCodigo() {
        return etiquetaCodigo;
    }

    public void setEtiquetaCodigo(String etiquetaCodigo) {
        this.etiquetaCodigo = etiquetaCodigo;
    }

    public String getLbTitulo() {
        return lbTitulo;
    }

    public void setLbTitulo(String lbTitulo) {
        this.lbTitulo = lbTitulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCodTercero() {
        return codTercero;
    }

    public void setCodTercero(String codTercero) {
        this.codTercero = codTercero;
    }

    public String getNombreTercero() {
        return nombreTercero;
    }

    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    public String getSucursalTercero() {
        return sucursalTercero;
    }

    public void setSucursalTercero(String sucursalTercero) {
        this.sucursalTercero = sucursalTercero;
    }

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
