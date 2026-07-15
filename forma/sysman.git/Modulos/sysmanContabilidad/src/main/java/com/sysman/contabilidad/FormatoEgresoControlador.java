package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author dmaldonado
 * @version 1, 28/04/2016
 * @version 2, 11/04/2017 modificado por jcrodriguez descripcion:--depuracion del controlador --creacion de servicio para generacion del reporte
 * @version 3, 25/03/2018 comentar la linea en la que se concatena comillas sencillas a la compania
 */
@ManagedBean
@ViewScoped
public class FormatoEgresoControlador extends BeanBaseModal {
    /**
     * variable que almacena la compania
     */
    private final String compania;

    /**
     * Atributo a nivel de clase que almacena el nombre de la compania desde la cual el usuario inicio sesion.
     */
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();

    /**
     * variable que almacena el formato de descarga
     */
    private String formato;
    /**
     * varible que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que alamcena el a�o
     */
    private String anio;
    /**
     * variable que almacena el tipo de comprobante
     */
    private String tipoComp;
    /**
     * variable que almacena el numero de comprobante
     */
    private String numeroComp;
    
    private boolean manejaDiferentesFormatos;

    private ComprobantesContPresReporteador comprobantesContPresReporteador;

    /**
     * variable Ejb
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtilRemote;

	private String fecha;
    /**
     * constantes de clase
     */
    public static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    public static final String MSM_INFORME_NO_EXISTE = "MSM_INFORME_NO_EXISTE";
    public static final String FORMAT = "FORMATO";
    public static final String SYSDATE = "SYSDATE";
    public static final String PR_COMPANIA = "PR_COMPANIA";
    public static final String PR_ANO = "PR_ANO";
    public static final String PR_TIPO = "PR_TIPO";
    public static final String PR_COMPROBANTE = "PR_COMPROBANTE";
    public static final String COMPANIAINFORME = "companiaInforme";
    public static final String ANO = "ano";
    public static final String TIPOCPTE = "tipoCpte";
    public static final String NUMEROCPTE = "numeroCpte";
    public static final String MODULO = "modulo";
    public static final String NUMERO = "15023";
    public static final String NO = "NO";
    public static final String PR_NITCOMPANIA = "PR_NITCOMPANIA";
    public static final String PR_NOMBRECOMPANIA = "PR_NOMBRECOMPANIA";
    public static final String PR_CARGO_FINANCIERO = "PR_CARGO_FINANCIERO";    
    public static final String CARGO_FINANCIERO = "CARGO FINANCIERO";
    public static final String PR_CARGO_TESORERO = "PR_CARGO_TESORERO";    
    public static final String TIPOCOMPS = "tipoComp";
    public static final String NUMEROCOMPS = "numeroComp";
    public static final String NUMEROCOMP_INI = "numeroPptoInicial";
    public static final String NUMEROCOMP_FIN = "numeroPptoFinal";
    public static final String ANIOS = "anio";
    
  //--INI_7709352 _(22/04/2022 mrosero)_CONTABILIDAD
    public static final String PR_NOMBRE_TESORERO = "PR_NOMBRE_TESORERO";
    public static final String PR_NOMBRE_FINANCIERO = "PR_NOMBRE_FINANCIERO";
    public static final String PR_RESOLUCION_EN_FORMATO_DE_EGRESO = "PR_RESOLUCION_EN_FORMATO_DE_EGRESO";
  //--FIN_7709352 _(22/04/2022 mrosero)_CONTABILIDAD
    
    public static final String PR_NOMBRE_CONTADOR = "PR_NOMBRE_CONTADOR";
    public static final String PR_NOMBRE_ORDENADOR_DEL_GASTO = "PR_NOMBRE_ORDENADOR_DEL_GASTO";
    
    /**
     * Creates a new instance of FormatoEgresoControlador
     */
    public FormatoEgresoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anio = cadenaVaciaH(parametrosEntrada, ANIOS);
                tipoComp = cadenaVaciaH(parametrosEntrada, TIPOCOMPS);
                numeroComp = cadenaVaciaH(parametrosEntrada, NUMEROCOMPS);
                fecha = cadenaVaciaH(parametrosEntrada, "fecha");
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
            }
            numFormulario = GeneralCodigoFormaEnum.FORMATO_EGRESO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * metodo que valida el casteo a toString
     *
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVaciaH(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
                        : campos.get(var).toString();
    }

    /**
     * metodo para inicialilzar el formulario
     */
    @PostConstruct
    public void inicializar() {
        formato = "1";
        abrirFormulario();
        comprobantesContPresReporteador = new ComprobantesContPresReporteador(
                        ejbSysmanUtilRemote);
    }

    /**
     * metodo que se utilza al abrir el formulario
     */
    @Override
    public void abrirFormulario() {    	
        try {
			manejaDiferentesFormatos = ("SI").equals(
			                SysmanFunciones.nvlStr(ejbSysmanUtilRemote
			                                .consultarParametro(
			                                                compania,
			                                                "MANEJA DIFERENTES FORMATOS DE IMPRESION EN EGRESOS",
			                                                SessionUtil.getModulo(),
			                                                new Date(),
			                                                false),
			                                "NO"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // heredado del bean base
    }

    /**
     * metodo que se llama al oprimir cancelar
     */
    public void oprimirCancelar() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * metodo que3 se llama al oprimir aceptar
     *
     */
    public void oprimirAceptar() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /**
     * metodo que contiene la logia para generar el reporte en formato pdf
     *
     * @param format
     */
    public void generarInforme(FORMATOS format) {
        String modulo = SessionUtil.getModulo();
        String reporte = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            // reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put(ANO, anio);
            reemplazar.put(TIPOCPTE, tipoComp);
            reemplazar.put(NUMEROCOMP_INI, numeroComp);
            reemplazar.put(NUMEROCOMP_FIN, numeroComp);
            reemplazar.put(MODULO, modulo);
            reemplazar.put("nombreCompania", SysmanFunciones.concatenar("'",
                            nombreCompania, "'"));
            reemplazar.put("fechaInicial", fecha);
    		reemplazar.put("fechaFinal", fecha); 

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), tipoComp);

            Map<String, Object> regTipoComprobante;
            regTipoComprobante = requestManager
                            .get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(NUMERO)
                                            .getUrl(), param)
                            .getFields();
            reporte = regTipoComprobante.get(FORMAT)
                    .toString();
            if(manejaDiferentesFormatos)
            {
            	if(formato.equals("1"))
            	{
            		reporte = ejbSysmanUtilRemote
                            .consultarParametro(
                                    compania,
                                    "FORMATO IMPRESION EGRESO NORMAL",
                                    SessionUtil.getModulo(),
                                    new Date(),
                                    false);
            	}
            	else if(formato.equals("3"))
            	{
            		reporte = ejbSysmanUtilRemote
                            .consultarParametro(
                                    compania,
                                    "FORMATO IMPRESION EGRESO POR TRANSFERENCIA",
                                    SessionUtil.getModulo(),
                                    new Date(),
                                    false);
            	}   
            }
            
            parametros.put(PR_NITCOMPANIA,
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put(PR_NOMBRECOMPANIA,
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(PR_CARGO_FINANCIERO,
                            SysmanFunciones.nvl(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            CARGO_FINANCIERO,
                                                            modulo, new Date(),
                                                            true),
                                            NO));
            parametros.put(PR_CARGO_TESORERO,
                            SysmanFunciones.nvl(ejbSysmanUtilRemote
                                            .consultarParametro(compania,
                                                            "CARGO TESORERO",
                                                            modulo, new Date(),
                                                            true),
                                            NO));
            
          //--INI_7709352 _(22/04/2022 mrosero)_CONTABILIDAD        
            parametros.put(PR_NOMBRE_TESORERO,
                    SysmanFunciones.nvl(ejbSysmanUtilRemote
                                    .consultarParametro(compania,
                                                    "NOMBRE TESORERO",
                                                    modulo, new Date(),
                                                    true),
                                    NO));
            parametros.put(PR_NOMBRE_FINANCIERO,
                    SysmanFunciones.nvl(ejbSysmanUtilRemote
                                    .consultarParametro(compania,
                                                    "NOMBRE FINANCIERO",
                                                    modulo, new Date(),
                                                    true),
                                    NO));
            
                  parametros.put(PR_RESOLUCION_EN_FORMATO_DE_EGRESO,
            		 SysmanFunciones.nvl(ejbSysmanUtilRemote
                             .consultarParametro(compania,
                                             "RESOLUCION EN FORMATO DE EGRESO",
                                             modulo, new Date(),
                                             true),
                             NO));
            //--FIN_7709352 _(22/04/2022 mrosero)_CONTABILIDAD
                  parametros.put(PR_NOMBRE_CONTADOR,
                          SysmanFunciones.nvl(ejbSysmanUtilRemote
                                          .consultarParametro(compania,
                                                          "NOMBRE CONTADOR",
                                                          modulo, new Date(),
                                                          true),
                                          NO));
                  parametros.put(PR_NOMBRE_ORDENADOR_DEL_GASTO,
                          SysmanFunciones.nvl(ejbSysmanUtilRemote
                                          .consultarParametro(compania,
                                                          "NOMBRE ORDENADOR DEL GASTO",
                                                          modulo, new Date(),
                                                          true),
                                          NO));
                  

            parametros.put(PR_COMPANIA, compania);
            parametros.put(PR_ANO, anio);
            parametros.put(PR_TIPO, tipoComp);
            parametros.put(PR_COMPROBANTE, numeroComp);
            

            parametros.put("PR_FIRMA_EN_EGRESO", SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FIRMA EN EGRESO",
                                            modulo, new Date(),
                                            true),
                            "").toString());
            
            parametros.put("PR_CARGO_FINANCIERO_PARA_PASTO", SysmanFunciones.nvl(
                    		ejbSysmanUtilRemote.consultarParametro(compania,
                                    		"CARGO FINANCIERO PARA PASTO",
                                    		modulo, new Date(),
                                    		true),
                    		"").toString());
            
            parametros.put("PR_CARGO_TESORERO_PARA_PASTO", SysmanFunciones.nvl(
		            		ejbSysmanUtilRemote.consultarParametro(compania,
		                            		"CARGO Tesorero PARA PASTO",
		                            		modulo, new Date(),
		                            		true),
		            		"").toString());

            if ("001495EGRSOCNT".equals(reporte)

                            || "001499EGRUPCCNT".equals(reporte)) {
                parametros.put("PR_ACCION_EN_EGRESO", SysmanFunciones.nvl(
                                ejbSysmanUtilRemote.consultarParametro(compania,
                                                "ACCION EN EGRESO",
                                                modulo, new Date(),
                                                true),
                                "").toString());

                parametros.put("PR_FIRMA_EN_EGRESO", SysmanFunciones.nvl(
                                ejbSysmanUtilRemote.consultarParametro(compania,
                                                "FIRMA EN EGRESO",
                                                modulo, new Date(),
                                                true),
                                "").toString());

                // Par�metros Informe 001495EGRSOCNT contabilidad

                String firmasEgr = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "FIRMAS EN EGR",
                                                modulo, new Date(), true),
                                                "NO");

                String firmasEpeciales = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "MANEJA FIRMAS ESPECIALES EN EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");
                String verNota = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "VER NOTA AL PIE EN FORMATO EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");

                String fechaRecibido = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "MANEJA FECHA RECIBIDO EN EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");
                String eliminarSuperior = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "ELIMINAR ELABORO SUPERIOR EN EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");
                String manejaPago = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "MANEJA PAGO ELECTRONICO EN EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");
                String colocarTitulos = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "COLOCAR TITULOS EN FORMATO COM_SM",
                                                modulo, new Date(), true),
                                                "NO");
                String elimCreadorEgrSo = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "ELIMINAR CREADOR EN EGR_SO",
                                                modulo, new Date(), true),
                                                "NO");
                String codigoFormatoEgreso = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "CODIGO FORMATO EGRESO",
                                                modulo, new Date(), true), "");
                String textoNota = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "TEXTO EN NOTA AL PIE EN FORMATO EGR_SO",
                                                modulo, new Date(), true), "");
                String elaboroEgreso = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "ELABORO EN EGRESO",
                                                modulo, new Date(), true), "");
                String revisoEgreso = SysmanFunciones
                                .nvlStr(ejbSysmanUtilRemote.consultarParametro(
                                                compania,
                                                "REVISO EN EGRESO",
                                                modulo, new Date(), true), "");

                parametros.put("PR_CODIGO_FORMATO_EGRESO", codigoFormatoEgreso);
                parametros.put("PR_FIRMASENEGR", firmasEgr);
                parametros.put("PR_MANEJAFIRMASESPECIALES", firmasEpeciales);
                parametros.put("PR_VERNOTA", verNota);
                parametros.put("PR_FECHARECIBIDO", fechaRecibido);
                parametros.put("PR_ELIMINARELABORO", eliminarSuperior);
                parametros.put("PR_MANEJAPAGO", manejaPago);
                parametros.put("PR_COLOCARTITULOS", colocarTitulos);
                parametros.put("PR_ELIMINARCREADOR", elimCreadorEgrSo);
                parametros.put("PR_FORMATOEGRSOCNT", textoNota);
                parametros.put("PR_ELABORO_EN_EGRESO", elaboroEgreso);
                parametros.put("PR_REVISO_EN_EGRESO", revisoEgreso);

                Reporteador.resuelveConsulta("001495EGRSOCNT",
                                Integer.valueOf(modulo),
                                reemplazar, parametros);
            }
            else {
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(modulo),
                                reemplazar, parametros);

            }
            Map<String, Object> valores = new HashMap<>();
            valores.put("informe", reporte);
            valores.put("formato", format);
            valores.put("nombreCompania", nombreCompania);
            valores.put("nitCompania", SessionUtil.getCompaniaIngreso().getNit());
            valores.put("lote", false);

            archivoDescarga = comprobantesContPresReporteador
                            .generarInforme(valores, parametros, reemplazar);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(idioma.getString(MSM_TRANS_INTERRUMPIDA)
                            + " " + e.getMessage());
        }
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
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

    public String getTipoComp() {
        return tipoComp;
    }

    public void setTipoComp(String tipoComp) {
        this.tipoComp = tipoComp;
    }

    public String getNumeroComp() {
        return numeroComp;
    }

    public void setNumeroComp(String numeroComp) {
        this.numeroComp = numeroComp;
    }

	/**
	 * @return the manejaDiferentesFormatos
	 */
	public boolean isManejaDiferentesFormatos() {
		return manejaDiferentesFormatos;
	}

	/**
	 * @param manejaDiferentesFormatos the manejaDiferentesFormatos to set
	 */
	public void setManejaDiferentesFormatos(boolean manejaDiferentesFormatos) {
		this.manejaDiferentesFormatos = manejaDiferentesFormatos;
	}

}