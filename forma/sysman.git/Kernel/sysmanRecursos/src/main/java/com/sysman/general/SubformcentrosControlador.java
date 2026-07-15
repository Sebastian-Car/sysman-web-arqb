package com.sysman.general;



import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.SubformcentrosControladorEnum;
import com.sysman.general.enums.SubformcentrosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author cmanrique
 *
 * @Modifier amonroy
 * @version 2, 05/04/2017 Proceso de Refactoring y Revision de buenas practicas sugeridas por SonarLint
 * @version 3, 12/06/2017 Refactoring conexiones y número frm.
 */
@ManagedBean
@ViewScoped
public class SubformcentrosControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el llamado de la palabra "centroCosto" en el formulario, almacena el texto centroCosto
     */
    private final String cCentroCosto;
    /**
     * Constante definida por el numero de veces que se realiza el llamado de la palabra "auxiliar" en el formulario, almacena el texto auxiliar
     */
    private final String cAuxiliar;
    /**
     * Constante definida por el numero de veces que se realiza el llamado de la palabra "referencia" en el formulario, almacena el texto referencia
     */
    private final String cReferencia;
    /**
     * Constante definida por el numero de veces que se realiza el llamado de la palabra "tercero" en el formulario, almacena el texto tercero
     */
    private final String cTercero;

    private String anoQr;
    private String mesFinalQr;
    private String mesInicialQr;
    private String centroCosto;
    private String nombreCentro;
    private String codAuxiliar;
    private String nombreAux;
    private String formulario;
    private String codigo;
    private String modulo;
    private String nombre;
    private String etiquetaNombre;
    private String etiquetaCodigo;
    private String lbTitulo;
    private String tituloGrilla;
    private String codTercero;
    private String nombreTercero;
    private String sucursalTercero;
    private String codReferencia;
    private String nombreReferencia;
    private StreamedContent archivoDescarga;
    


    /**
     * Creates a new instance of SubformcentrosControlador
     */
    public SubformcentrosControlador()
    {
        super();
        modulo = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        cCentroCosto = "centroCosto";
        cAuxiliar = "auxiliar";
        cReferencia = "referencia";
        cTercero = "tercero";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBFORMCENTROS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(SubformcentrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init()
    {
        tabla = SubformcentrosControladorEnum.PARAM2.getValue();
        buscarLlave();
        Map<String, Object> parametros = SessionUtil.getFlash();
        anoQr = parametros.get("anoQr").toString();
        mesInicialQr = parametros.get("mesInicialQr").toString();
        mesFinalQr = parametros.get("mesFinalQr").toString();
        formulario = parametros.get("formulario").toString();
        switch (formulario)
        {
        case "centroCosto":
            centroCosto = parametros.get(cCentroCosto).toString();
            nombreCentro = parametros.get("nombreCentro").toString();
            break;

        case "auxiliar":
            codAuxiliar = parametros.get("codAuxiliar").toString();
            nombreAux = parametros.get("nombreAux").toString();
            break;
        case "referencia":
            codReferencia = parametros.get("codReferencia").toString();
            nombreReferencia = parametros.get("nombreReferencia").toString();
            break;
        case "tercero":
            codTercero = parametros.get("codTercero").toString();
            nombreTercero = parametros.get("nombreTercero").toString();
            sucursalTercero = parametros.get("sucursalTercero").toString();
            break;
        default:
            break;

        }
        SessionUtil.cleanFlash();

        if (cAuxiliar.equals(formulario))
        {
            etiquetaCodigo = idioma.getString("TG_AUXILIAR2");
            codigo = codAuxiliar;
            etiquetaNombre = idioma.getString("TB_TB416");
            nombre = nombreAux;
            lbTitulo = idioma.getString("TB_TB3054");
            tituloGrilla = idioma.getString("TB_TB3055");
        }
        else if (cCentroCosto.equals(formulario))
        {
            etiquetaCodigo = idioma.getString("TG_CENTRO_DE_COSTO6");
            codigo = centroCosto;
            etiquetaNombre = idioma.getString("TB_TB420");
            nombre = nombreCentro;
            lbTitulo = idioma.getString("TB_TB3056");
            tituloGrilla = idioma.getString("TB_TB3057");
        }
        else if (cReferencia.equals(formulario))
        {
            etiquetaCodigo = idioma.getString("TG_REFERENCIA2");
            codigo = codReferencia;
            etiquetaNombre = idioma.getString("TB_TB3058");
            nombre = nombreReferencia;
            lbTitulo = idioma.getString("TB_TB3059");
            tituloGrilla = idioma.getString("TB_TB3060");
        }
        else if (cTercero.equals(formulario))
        {
            etiquetaCodigo = idioma.getString("TG_TERCERO3");
            codigo = codTercero;
            etiquetaNombre = idioma.getString("TB_TB424");
            nombre = nombreTercero;
            lbTitulo = idioma.getString("TB_TB3061");
            tituloGrilla = idioma.getString("TB_TB3062");
        }

        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();

    }

    public String getAnoQr()
    {
        return anoQr;
    }

    public void setAnoQr(String anoQr)
    {
        this.anoQr = anoQr;
    }

    public String getMesInicialQr()
    {
        return mesInicialQr;
    }

    public void setMesInicialQr(String mesInicialQr)
    {
        this.mesInicialQr = mesInicialQr;
    }

    public String getMesFinalQr()
    {
        return mesFinalQr;
    }

    public void setMesFinalQr(String mesFinalQr)
    {
        this.mesFinalQr = mesFinalQr;
    }

    public String getCentroCosto()
    {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    public String getNombreCentro()
    {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro)
    {
        this.nombreCentro = nombreCentro;
    }

    public String getCodAuxiliar()
    {
        return codAuxiliar;
    }

    public void setCodAuxiliar(String codAuxiliar)
    {
        this.codAuxiliar = codAuxiliar;
    }

    public String getNombreAux()
    {
        return nombreAux;
    }

    public void setNombreAux(String nombreAux)
    {
        this.nombreAux = nombreAux;
    }

    public String getFormulario()
    {
        return formulario;
    }

    public void setFormulario(String formulario)
    {
        this.formulario = formulario;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getEtiquetaNombre()
    {
        return etiquetaNombre;
    }

    public void setEtiquetaNombre(String etiquetaNombre)
    {
        this.etiquetaNombre = etiquetaNombre;
    }

    public String getEtiquetaCodigo()
    {
        return etiquetaCodigo;
    }

    public void setEtiquetaCodigo(String etiquetaCodigo)
    {
        this.etiquetaCodigo = etiquetaCodigo;
    }

    public String getLbTitulo()
    {
        return lbTitulo;
    }

    public void setLbTitulo(String lbTitulo)
    {
        this.lbTitulo = lbTitulo;
    }

    public String getTituloGrilla()
    {
        return tituloGrilla;
    }

    public void setTituloGrilla(String tituloGrilla)
    {
        this.tituloGrilla = tituloGrilla;
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    

    @Override
    public void reasignarOrigen()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(SubformcentrosControladorEnum.PARAM3.getValue(),
                        anoQr);
        parametrosListado.put(SubformcentrosControladorEnum.PARAM0.getValue(),
                        mesInicialQr);
        parametrosListado.put(SubformcentrosControladorEnum.PARAM1.getValue(),
                        mesFinalQr);

        if (cAuxiliar.equals(formulario))
        {
            parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
                            codAuxiliar);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentrosControladorUrlEnum.URL0001
                                                            .getValue());
        }
        else if (cCentroCosto.equals(formulario))
        {
            parametrosListado.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                            centroCosto);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentrosControladorUrlEnum.URL0002
                                                            .getValue());
        }
        else if (cReferencia.equals(formulario))
        {
            parametrosListado.put(GeneralParameterEnum.REFERENCIA.getName(),
                            codReferencia);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentrosControladorUrlEnum.URL0003
                                                            .getValue());
        }
        else if (cTercero.equals(formulario))
        {
            parametrosListado.put(GeneralParameterEnum.TERCERO.getName(),
                            codTercero);
            parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursalTercero);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentrosControladorUrlEnum.URL0004
                                                            .getValue());
        }

    }
    private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos
		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte; 
		// Codigo del reporte
	
				reporte = "001935LisMovTerceros";

			

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
        archivoDescarga=null;
        generarReporte(ReportesBean.FORMATOS.PDF);
       //</CODIGO_DESARROLLADO>
   }
   /**
    * 
    * Metodo ejecutado al oprimir el boton imprimirExcel
    * en la vista
    *
    * TODO DOCUMENTACION ADICIONAL
    *
    */
public void oprimirimprimirExcel() {
        //<CODIGO_DESARROLLADO>
        archivoDescarga=null; 
        generarReporte(ReportesBean.FORMATOS.EXCEL);
       //</CODIGO_DESARROLLADO>
   }
    
  

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getCodTercero()
    {
        return codTercero;
    }

    public void setCodTercero(String codTercero)
    {
        this.codTercero = codTercero;
    }

    public String getNombreTercero()
    {
        return nombreTercero;
    }

    public void setNombreTercero(String nombreTercero)
    {
        this.nombreTercero = nombreTercero;
    }

    public String getSucursalTercero()
    {
        return sucursalTercero;
    }

    public void setSucursalTercero(String sucursalTercero)
    {
        this.sucursalTercero = sucursalTercero;
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

}
