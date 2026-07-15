package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.SubformcentroasControladorEnum;
import com.sysman.general.enums.SubformcentroasControladorUrlEnum;
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
 * @author cmanrique
 * @version 2 jrodriguezr Se refactoriza el codigo SQL de las listas para utilizar dss.
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017 
 */
@ManagedBean
@ViewScoped

public class SubformcentroasControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;
    private String modulo;
    private final String cteAuxiliar;
    private final String cteCentroCosto;
    private final String cteTercero;
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
    private String etiquetaNombre;
    private String etiquetaCodigo;
    private String lbTitulo;
    private String titulo;
    private String codTercero;
    private String nombreTercero;
    private String sucursalTercero;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of SubformcentroasControlador
     */
    public SubformcentroasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cteAuxiliar = "auxiliar";
        cteCentroCosto = "centroCosto";
        cteTercero = "tercero";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SUBFORMCENTROAS_CONTROLADOR.getCodigo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                anoQr = parametrosEntrada.get("anoQr").toString();
                mesInicialQr = parametrosEntrada.get("mesInicialQr").toString();
                mesFinalQr = parametrosEntrada.get("mesFinalQr").toString();
                formulario = parametrosEntrada.get("formulario").toString();
                if (cteAuxiliar.equals(formulario))
                {

                    codAuxiliar = parametrosEntrada.get("codAuxiliar")
                                    .toString();
                    nombreAux = parametrosEntrada.get("nombreAux").toString();
                }
                else if (cteCentroCosto.equals(formulario))
                {
                    centroCosto = parametrosEntrada.get(cteCentroCosto)
                                    .toString();
                    nombreCentro = parametrosEntrada.get("nombreCentro")
                                    .toString();
                }
                else if (cteTercero.equals(formulario))
                {
                    codTercero = parametrosEntrada.get("codTercero").toString();
                    nombreTercero = parametrosEntrada.get("nombreTercero")
                                    .toString();
                    sucursalTercero = parametrosEntrada.get("sucursalTercero")
                                    .toString();
                }

                SessionUtil.cleanFlash();
            }
            validarPermisos();
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        tabla = SubformcentroasControladorEnum.PARAM2.getValue();
        buscarLlave();
        if (formulario.equals(cteAuxiliar))
        {
            condicion = "M.AUXILIAR = '" + codAuxiliar + "'";
            etiquetaCodigo = idioma.getString("TG_AUXILIAR2");
            codigo = codAuxiliar;
            etiquetaNombre = idioma.getString("TB_TB416");
            nombre = nombreAux;
            lbTitulo = idioma.getString("TB_TB417");
            titulo = idioma.getString("TB_TB418");
        }
        else if (formulario.equals(cteCentroCosto))
        {
            condicion = "DM.CENTRODECOSTO = '" + centroCosto + "' ";
            etiquetaCodigo = idioma.getString("TG_CENTRO_DE_COSTO6");
            codigo = centroCosto;
            etiquetaNombre = idioma.getString("TB_TB420");
            nombre = nombreCentro;
            lbTitulo = idioma.getString("TB_TB421");
            titulo = idioma.getString("TB_TB422");
        }
        else if (formulario.equals(cteTercero))
        {
            condicion = "DM.TERCERO = '" + codTercero + "'  AND DM.SUCURSAL = '"
                + sucursalTercero + "' ";
            etiquetaCodigo = idioma.getString("TG_TERCERO3");
            codigo = codTercero;
            etiquetaNombre = idioma.getString("TB_TB424");
            nombre = nombreTercero;
            lbTitulo = idioma.getString("TB_TB425");
            titulo = idioma.getString("TB_TB426");
        }
        reasignarOrigen();
        abrirFormulario();

        registro = new Registro(new HashMap<String, Object>());

    }
    private void generarReporte(ReportesBean.FORMATOS formato) {
		// Creacion arreglos
		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte; 
		// Codigo del reporte
	
				reporte = "001941LisMovTercerosA";

			

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
        archivoDescarga = null; 
        generarReporte(ReportesBean.FORMATOS.EXCEL);
       //</CODIGO_DESARROLLADO>
   }

   

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        actualizarAntes();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen()
    {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoQr);
        parametrosListado.put(SubformcentroasControladorEnum.PARAM0.getValue(),
                        mesInicialQr);
        parametrosListado.put(SubformcentroasControladorEnum.PARAM1.getValue(),
                        mesFinalQr);

        if (formulario.equals(cteAuxiliar))
        {
            parametrosListado.put(GeneralParameterEnum.AUXILIAR.getName(),
                            codAuxiliar);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentroasControladorUrlEnum.URL0001
                                                            .getValue());
        }
        else if (formulario.equals(cteCentroCosto))
        {
            parametrosListado.put(GeneralParameterEnum.CENTRODECOSTO.getName(),
                            centroCosto);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentroasControladorUrlEnum.URL0002
                                                            .getValue());
        }
        else if (formulario.equals(cteTercero))
        {
            parametrosListado.put(GeneralParameterEnum.TERCERO.getName(),
                            codTercero);
            parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursalTercero);
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubformcentroasControladorUrlEnum.URL0003
                                                            .getValue());
        }

    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

    public String getCondicion()
    {
        return condicion;
    }

    public void setCondicion(String condicion)
    {
        this.condicion = condicion;
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

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
