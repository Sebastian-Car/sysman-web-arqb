package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.ejb.EjbContratosUnoRemote;
import com.sysman.contratos.enums.NovedadcontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 16/10/2015
 * 
 * @author eamaya
 * @version 2.0, 10/08/2017 Proceso de refactoring DSS, Manejo de
 * EJBs, Cambio de numero de formulario por enum y ajuste de
 * redireccionamientos
 * 
 * @author asana
 * @version 3.0 28/09/2017 Se modifican parametros a enviar a
 * formulario subnovedad.
 */
@ManagedBean
@ViewScoped

public class NovedadcontratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private String modulo;
    /**
     * Constante definida para almacenar la cadena "titulo"
     */
    private final String cTitulo;
    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String cNumero;
    /**
     * Constante definida para almacenar la cadena "claseOrden"
     */
    private final String cClaseOrden;

    /**
     * Constante definida para almacenar la cadena
     * "MSM_TRANS_INTERRUMPIDA"
     */
    private final String cTransaccionInterrumpida;
    /**
     * Constante definida para almacenar la cadena "PLAZODEENTREGA"
     */
    private final String cPlazoEntrega;
    /**
     * Constante definida para almacenar la cadena "VALORFINAL"
     */
    private final String cValorFinal;

    /**
     * Constante definida para almacenar la cadena "-"
     */
    private final String cGuion;
    private Registro registroSub;

    private String auxiliar;
    private String anio;
    private String tipoContrato;

    private String ttValorTotal;
    private String valorAPagar;
    private String novedad;
    private String claseNov;
    private String titulo;
    private boolean allowEdits;
    private boolean manejaNominaDeContratistas;

    private String opcionFormato;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbContratosUnoRemote ejbContratosUno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private Map<String,Object> parametroswf;
	private boolean verCerrar = true;

    @SuppressWarnings("unchecked")
    public NovedadcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cTitulo = "titulo";
        cNumero = "NUMERO";
        cClaseOrden = "claseOrden";
        cTransaccionInterrumpida = "MSM_TRANS_INTERRUMPIDA";
        cPlazoEntrega = "PLAZODEENTREGA";
        cValorFinal = "VALORFINAL";
        cGuion = "-";
        try {

            numFormulario = GeneralCodigoFormaEnum.NOVEDADCONTRATOS_CONTROLADOR
                            .getCodigo();

            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
            opcionFormato = "2";

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
    		if(parametroswf != null) {
    			SessionUtil.setSessionVar("modulo", "9");
    		}
    		modulo = SessionUtil.getModulo();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("ridR");
                anio = parametrosEntrada.get("anio").toString();
                tipoContrato = parametrosEntrada.get("tipoContrato").toString();
                if (parametrosEntrada.get(cTitulo).toString() == null) {
                    // PENDIENTE PARA RECIBIR DESDE UNA OPCION DE MENU

                }
                claseNov = parametrosEntrada.get("claseNov").toString();
                titulo = parametrosEntrada.get(cTitulo).toString();
                SessionUtil.cleanFlash();
            }

            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    private void init() {
    	
        enumBase = GenericUrlEnum.ORDENDECOMPRA;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void abrirFormulario() {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarOrigenDatos() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContrato);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadcontratosControladorUrlEnum.URL8418
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadcontratosControladorUrlEnum.URL9249
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NovedadcontratosControladorUrlEnum.URL001
                                                        .getValue());
        
        urlEliminacion = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                NovedadcontratosControladorUrlEnum.URL002
                                                .getValue());

    }

    @Override
    public void iniciarListas() {
        // METODO_NO_IMPLEMENTADO
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarCamposCalculados() {
        try {
            ttValorTotal = SysmanFunciones
                            .nvl(ejbContratosUno.getTotalValorNovedad(compania,
                                            registro.getCampos().get(cClaseOrden
                                                            .toUpperCase())
                                                            .toString(),
                                            Long.parseLong(registro.getCampos()
                                                            .get(cNumero)
                                                            .toString()),
                                            claseNov), "0")
                            .toString();

            valorAPagar = String.valueOf(Double
                            .parseDouble(registro.getCampos()
                                            .get("VALORTOTAL").toString())
                - Double.parseDouble(ttValorTotal));
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if ("2".equals(opcionFormato)) {
            generarReporte(ReportesBean.FORMATOS.PDF);
        }
        else {
            generarReporteADS1(ReportesBean.FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>

    }

    public String firmaO() {
        String firmaOrdendes = "";

        try {
            firmaOrdendes = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA ORDENES DE SERVICIO", modulo, new Date(),
                            false);

        }
        catch (NullPointerException ex) {
            firmaOrdendes = "";
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(cTransaccionInterrumpida)
                                + ex.getMessage());
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return firmaOrdendes;
    }

    public void generarReporteADS1(ReportesBean.FORMATOS formato) {
        try {
            String firmaOrdendes;

            firmaOrdendes = firmaO();

            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put(cClaseOrden,
                            registro.getCampos()
                                            .get(cClaseOrden.toUpperCase()));
            reemplazar.put(cNumero.toLowerCase(),
                            registro.getCampos().get(cNumero));

            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000362INOVEDADCONTRATOADS1";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            strSql = strSql.replace("NovedadContrato.FECHA",
                            "TO_CHAR(NovedadContrato.FECHA,'DD/MM/YYYY')");

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_NOVEDADCONTRATO_VALORPAGAR", valorAPagar);
            parametros.put("PR_FORMS_NOVEDADCONTRATO_TXTVALORTOTAL",
                            ttValorTotal);
            parametros.put("PR_ADICIONES", getAdiciones());
            parametros.put("PR_NOMBRECONTRATO",
                            titulo.substring(titulo.indexOf(cGuion) + 1));
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_FIRMA_ORDENES_DE_SERVICIO", firmaOrdendes);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(cTransaccionInterrumpida)
                                + ex.getMessage());
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public String getAdiciones() {
        String retorno = null;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(cNumero));

        Registro r;
        try {
            r = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NovedadcontratosControladorUrlEnum.URL7878
                                                                            .getValue())
                                            .getUrl(), param));

            if (r == null) {
                return "0.0";
            }
            else {
                retorno = SysmanFunciones
                                .nvl(r.getCampos().get("TOTAL_ADICIONES"),
                                                "0.0")
                                .toString();
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return retorno;
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(cClaseOrden,
                            registro.getCampos()
                                            .get(cClaseOrden.toUpperCase()));
            reemplazar.put("claseNovedad", claseNov);
            reemplazar.put(cNumero.toLowerCase(),
                            registro.getCampos().get(cNumero));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000357NovedadesPorContrato";
            // MANEJO DE PARAMETROS DEL REPORTE

            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            strSql = strSql.replace("ORDENDECOMPRA.FECHA",
                            "TO_CHAR(ORDENDECOMPRA.FECHA,'DD/MM/YYYY')");
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_NOVEDADCONTRATO_CLASEORDEN",
                            registro.getCampos()
                                            .get(cClaseOrden.toUpperCase()));
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FORMS_NOVEDADCONTRATO_NUMERO",
                            registro.getCampos().get(cNumero));
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(cTransaccionInterrumpida)
                                + ex.getMessage());
            Logger.getLogger(NovedadcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public void oprimirTercerosAportantes() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { cClaseOrden, "numeroOrden" };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(cNumero)) };

        SessionUtil.cargarModalDatos(Integer
                        .toString(GeneralCodigoFormaEnum.TERCEROS_APORTANTES_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirSub(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdMetasProducto(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirvisitain(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirResultados(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirnovedades() {
        Date fechaFirma;
        try {
            fechaFirma = registro.getCampos().get("FECHAFIRMA") == null ? null
                : SysmanFunciones.convertirAFecha(
                                registro.getCampos().get("FECHAFIRMA")
                                                .toString());

            String txtFecha = registro.getCampos().get("FECHA") == null ? null
                : registro.getCampos().get("FECHA").toString();

            Double valorFinal = registro.getCampos().get(cValorFinal) == null
                ? null
                : Double.parseDouble(registro.getCampos().get(cValorFinal)
                                .toString());

            String plazoEntrega = registro.getCampos()
                            .get(cPlazoEntrega) == null
                                ? null
                                : registro.getCampos()
                                                .get(cPlazoEntrega)
                                                .toString();
            String dependecia = SysmanFunciones
                            .nvl(registro.getCampos().get("DEPENDENCIA"), "")
                            .toString();
            
            Double valorTotalCont = Double.parseDouble(registro.getCampos().get("VALORTOTAL")
            					.toString());
            
           
            String aplicasActasIni = SysmanFunciones
                    .nvl(registro.getCampos().get("APLICA_ACTAS_INICIO"), "")
                    .toString();


            Map<String, Object> param = new TreeMap<>();
            param.put("tipoContrato", tipoContrato);
            param.put(cNumero.toLowerCase(),
                            registro.getCampos().get(cNumero).toString());
            param.put("claseNov", claseNov);
            param.put(cClaseOrden, registro.getCampos()
                            .get(cClaseOrden.toUpperCase())
                            .toString());
            param.put("fechaFirma", fechaFirma);
            param.put("fechaFinalizacion",
                            registro.getCampos().get("FECHAFINALIZACION"));
            param.put("txtFecha", txtFecha);
            param.put("fechaPolizas", registro.getCampos().get("FECHAPOLIZAS"));
            param.put("valorFinal", valorFinal);
            param.put("plazoEntrega", plazoEntrega);
            param.put("dependencia", dependecia);
            param.put(cTitulo, titulo);
            param.put("anio", anio);
            param.put("ridR", css);
            param.put("valorTotalNovedad", ttValorTotal);
            param.put("valorAPagar", valorAPagar);
            param.put("valorTotalCont", valorTotalCont);           
            param.put("aplicasActasIni", aplicasActasIni);
            param.put("parametroswf",parametroswf);

            Direccionador direccionador = new Direccionador();
            direccionador.setParametros(param);
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                                            .getCodigo()));

            SessionUtil.redireccionarForma(direccionador, modulo);

        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void ejecutarrcCerrar() {
    	try {
    		if (parametroswf != null) {
    			Map<String,Object> parametros = new TreeMap<>();
    			parametros.put("PR_ROWKEY",parametroswf.get("PR_ROWKEY"));

    			SessionUtil.removeSessionVarContainer("parametroswf");

    			Direccionador direccionador = new Direccionador();
    			direccionador.setNumForm(Integer.toString(
    					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo()));

    			direccionador.setParametros(parametros);
    			SessionUtil.redireccionarForma(direccionador,"35");
    		} else {
    			SessionUtil.redireccionar("/menu.sysman");
    		}
    	} catch (NamingException e) {
    		logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    public void cambiarClaseT() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTIPOT() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtValorTotal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtValorContrato() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarCamposCalculados();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove("VALORANTICIPOAMORTIZAR");
        registro.getCampos().remove("APLICA_ACTAS_INICIO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if ("m".equals(accion)) {
            registro.getCampos().remove(GeneralParameterEnum.FECHA.getName());
            registro.getCampos().remove("FECHAFIRMA");
            registro.getCampos()
                            .remove(GeneralParameterEnum.FECHAINICIO.getName());
            registro.getCampos().remove("AFAVORDE");
            registro.getCampos().remove("PLAZODEENTREGA");
            registro.getCampos().remove("OBJETOCONTRATO");
            registro.getCampos().remove("FECHAFINALIZACION");
            registro.getCampos().remove("TERCERO");
            registro.getCampos().remove("VALORFINAL");
            registro.getCampos().remove("FECHAPOLIZAS");
            registro.getCampos().remove("FECHA_NACTO");
            registro.getCampos().remove("CLASEDISPONIBILIDAD");
            registro.getCampos().remove("VALORANTICIPOAMORTIZAR");
            registro.getCampos().remove("APLICA_ACTAS_INICIO");

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String getClaseNov() {
        return claseNov;
    }

    public void setClaseNov(String claseNov) {
        this.claseNov = claseNov;
    }

    public boolean getManejaNominaDeContratistas() {
        return manejaNominaDeContratistas;
    }

    public void setManejaNominaDeContratistas(
        boolean novedadcontratosControlador) {
        this.manejaNominaDeContratistas = novedadcontratosControlador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnticipo
     * 
     * 
     */
    public void cambiarValorAnticipo() {

        actualizarAnticipoPorAmortizar();

    }

    /**
     * Metodo ejecutado al cambiar el control AnticpadoAmortizado
     * 
     */
    public void cambiarAnticpadoAmortizado() {

        actualizarAnticipoPorAmortizar();

    }

    private void actualizarAnticipoPorAmortizar() {
        double valorAnticipo = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("VALOR_ANTICIPO"), "0")
                        .toString());

        double valorAnticipoAmortizado = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("ANTCAMORTIZADO"), "0")
                        .toString());

        double vlrAnticipoPorAmortizar = valorAnticipo
            - valorAnticipoAmortizado;

        registro.getCampos().put("VALORANTICIPOAMORTIZAR",
                        vlrAnticipoPorAmortizar);

    }

    public void retornarFormularionovedades(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean isAllowEdits() {
        return allowEdits;
    }

    public void setAllowEdits(boolean allowEdits) {
        this.allowEdits = allowEdits;
    }

    public String getOpcionFormato() {
        return opcionFormato;
    }

    public void setOpcionFormato(String opcionFormato) {
        this.opcionFormato = opcionFormato;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTtValorTotal() {
        return ttValorTotal;
    }

    public void setTtValorTotal(String ttValorTotal) {
        this.ttValorTotal = ttValorTotal;
    }

    public String getValorAPagar() {
        return valorAPagar;
    }

    public void setValorAPagar(String valorAPagar) {
        this.valorAPagar = valorAPagar;
    }

    public String getNovedad() {
        return novedad;
    }

    public void setNovedad(String novedad) {
        this.novedad = novedad;
    }

    public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}
}
