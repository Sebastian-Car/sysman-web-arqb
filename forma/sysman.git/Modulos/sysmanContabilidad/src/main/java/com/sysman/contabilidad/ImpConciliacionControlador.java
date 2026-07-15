package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ImpConciliacionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
import javax.faces.event.AjaxBehaviorEvent;

/**
 * Formulario que permite realizar la impresion de la conciliacion, antes de verificar la fecha limite en la cual esta conciliando y registrar el valor del saldo final que reporta el extracto el cual
 * debe coincidir con el del sistema; de lo contrario esto indica que la conciliacion est� mal.
 *
 * @author jrodrigueza
 * @version 1, 16/03/2016
 * 
 * @author eamaya
 * @version 2, 10/04/2017 Proceso de Refactoring y Correciones SonarLint
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class ImpConciliacionControlador extends BeanBaseModal
{

	private final String compania;
	private final String cCodCuenta;
	private final String cCodigoCuenta;
	private final String cTotal;
	private final String cStrTipos;
	private final String cCondicion;
	private final String cFechaConciliacion;
	private final String cObservConcialiacion;

	private BigDecimal saldoExtracto;
	private String codCuenta;
	private String codEquivalente;
	private String cuenta;
	private String observaciones;
	private Date fechaInicial;
	private Date fechaFinal;
	private int ano;
	private int mes;
	private StreamedContent archivoDescarga;
	private boolean etiquetaGuardarObservaciones = false;
	private boolean cambiarckGuardarObservaciones = false;
	private boolean ckGuardarObservaciones;  

	@EJB
	private EjbSysmanUtilRemote ejbParametroUno;

	private Map<String, Object> parametro;
	private String manejaPartidasConci;
	private String manejaConciAnterior;

	/**
	 * Creates a new instance of ImpConciliacionControlador
	 */
	public ImpConciliacionControlador()
	{
		super();
		compania = SessionUtil.getCompania();
		cCodCuenta = "codCuenta";
		cCodigoCuenta = "CODCUENTA";
		cTotal = "TOTAL";
		cStrTipos = "strTipos";
		cCondicion = "condicion";
		cFechaConciliacion = "FECHA_CONCILIACION";
		cObservConcialiacion = "OBSERV_CONCILIACION";
		try
		{
			numFormulario = GeneralCodigoFormaEnum.IMP_CONCILIACION_CONTROLADOR
					.getCodigo();
			validarPermisos();
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null)
			{
				ano = Integer.parseInt((String) parametros.get("ano"));
				mes = Integer.parseInt((String) parametros.get("mes"));
				codCuenta = (String) parametros.get(cCodCuenta);
				cuenta = codCuenta + " " + parametros.get("nombreCuenta");
				codEquivalente = (String) parametros.get("codEquivalente");
				//observaciones = "AQUI LO LLENAS CON LA CONSULTA INICIAL";
			}
		}
		catch (SysmanException | NumberFormatException ex)
		{
			Logger.getLogger(ImpConciliacionControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		finally
		{
			SessionUtil.cleanFlash();
		}
	}

	@PostConstruct
	public void inicializar()
	{
		cargarIntervaloFechas();
		saldoExtracto = traerSaldoExtracto(mes, ano);
		// Se comenta pues guarda los datos de la �ltima consulta no
		// el saldo real despus de la conciliaci�n
		//getDatosConciliacion();
		parametro = new HashMap<>();
		abrirFormulario();
	}

	private Registro getObservacionesReg() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.CODIGO.getName(),codCuenta );

		Registro reg = null;
		try {
			reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ImpConciliacionControladorUrlEnum.URL1942001
									.getValue())
							.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return reg;
		
	}

	public void cambiarObservacion()
	{
		
	
	}
	/**
	 * Carga el primer y el ultimo dia del ano y mes seleccionados en el periodo de conciliacion.
	 */
	private void cargarIntervaloFechas()
	{
		Calendar fecha = Calendar.getInstance();
		fecha.set(ano, mes - 1, 1);
		fechaInicial = fecha.getTime();
		try
		{
			fechaFinal = SysmanFunciones.ultimoDiaDate(fechaInicial);
		}
		catch (ParseException ex)
		{
			Logger.getLogger(ImpConciliacionControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Trae el saldo segun el periodo de conciliacion.
	 *
	 * @param mes
	 * @param ano
	 * @return numero que representa el saldo extracto
	 */
	private BigDecimal traerSaldoExtracto(int mes, int ano)
	{
		BigDecimal saldo = BigDecimal.ZERO;
		try
		{
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put("MES", mes);
			param.put(cCodigoCuenta, codCuenta);

			Registro reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ImpConciliacionControladorUrlEnum.URL69696
									.getValue())
							.getUrl(), param));
			if (reg == null)
			{
				return saldo;
			}
			else
			{
				saldo = new BigDecimal(
						reg.getCampos().get("AJUSTE").toString());
			}
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return saldo;
	}

	/**
	 * Trae el valor del parametro ALMACENA RESULTADOS IMPRESION CONCILIACION.
	 *
	 * @return <code>true</code> si el valor del parametro es igual a SI
	 */
	private boolean almacenaImpresion()
	{
		String par = valorParametro(
				"ALMACENA RESULTADOS IMPRESION CONCILIACION");
		return "SI".equals(par);
	}

	/**
	 * Trae el valor del parametro MANEJA CONCILIACION ANTERIOR.
	 *
	 * @return <code>true</code> si el valor del parametro es igual a SI
	 */
	private boolean manejaConciliacionAnterior()
	{
		String par = valorParametro("MANEJA CONCILIACION ANTERIOR");
		return "SI".equals(par);
	}

	/**
	 * Trae el valor de un parametro.
	 *
	 * @param nombre
	 * Nombre del parametro.
	 * @return Valor del parametro ingresado por parametro.
	 */
	private String valorParametro(String nombre)
	{
		try
		{
			return ejbParametroUno.consultarParametro(compania, nombre,
					SessionUtil.getModulo(), new Date(), false);

		}
		catch (SystemException e)
		{
			Logger.getLogger(ImpConciliacionControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return null;
		}
	}

	/**
	 * Trae los datos de la conciliacion en caso de que se hayan almacenado en la tabla SALDO_AUX_CONTABLE.
	 */
	private void getDatosConciliacion()
	{
		try
		{
			// Se comenta pues el saldo se genera por el �ltimo mes
			// consultado no es real'
			if (almacenaImpresion())
			{
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ANO.getName(), ano);
				param.put(cCodigoCuenta, codCuenta);
				Registro reg = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ImpConciliacionControladorUrlEnum.URL13666
										.getValue())
								.getUrl(), param));

				if (reg != null)
				{
					asignarConciliacion(reg);

				}
			}
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void asignarConciliacion(Registro reg)
	{
		String valorSaldo = reg.getCampos().get("SALDO_CONCILIACION")
				.toString();
		BigDecimal saldoConciliacion = new BigDecimal(valorSaldo);
		if (saldoConciliacion.compareTo(BigDecimal.ZERO) != 0)
		{
			saldoExtracto = saldoConciliacion;
		}
		if (reg.getCampos().containsKey(cFechaConciliacion))
		{
			if (null != reg.getCampos().get(cFechaConciliacion))
			{
				fechaInicial = (Date) reg.getCampos()
						.get(cFechaConciliacion);
			}
			else
			{
				fechaInicial = fechaFinal;
			}
		}
		if (reg.getCampos().containsKey(cObservConcialiacion)
				&& null != reg.getCampos()
				.get(cObservConcialiacion))
		{

			observaciones = (String) reg.getCampos()
					.get(cObservConcialiacion);

		}

	}

	@Override
	public void abrirFormulario()
	{

		try
		{
			Registro registro = getObservacionesReg();
			if(registro != null) {
				observaciones = SysmanFunciones.nvlStr(SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName())),"");
			}
			fechaInicial = SysmanFunciones.ultimoDiaDate(SysmanFunciones
					.convertirAFecha("01/" + mes + "/" + ano + ""));
		}
		catch (ParseException e)
		{

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	/**
     * Trae las clases contables para conciliacion bancaria.
     *
     * @return cadena con las clases separadas por coma.
     */
	 private String getClasesContables()
	    {
	        String clase = "";
	        try
	        {
	            clase = SysmanFunciones.nvl(
	            		ejbParametroUno.consultarParametro(compania,
	                                            "CLASES CONTABLES EN CONCILIACION BANCARIA",
	                                            SessionUtil.getModulo(), new Date(),
	                                            false),
	                            "").toString();
	            
	             clase = SysmanFunciones.separarCaracteres(clase, ",");
	             clase = clase.replace("", "'");
	            // clase = "'" + clase + "'";
	        }
	        catch (SystemException e)
	        {
	            Logger.getLogger(SubConciliacionControlador.class.getName())
	                            .log(Level.SEVERE, null, e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	        return clase;
	    }
	 
	 
	 public void cambiarckGuardarObservaciones() {
		 if(ckGuardarObservaciones) {
			 ckGuardarObservaciones =  false; 
		 }else {
			 ckGuardarObservaciones =  true; 
		 }

		}

	
	 
	/**
	 * Definicin de reemplazos para la consulta y envio de parametros para la generacion del informe
	 */
	private void generarLogicaInforme()
	{

		String strTipos = "";
		String condicion = "";
		String fechaArmada = SysmanFunciones.formatearFecha(fechaFinal);
		int modulo = Integer.parseInt(SessionUtil.getModulo());
		String reporte = valorParametro("FORMATO CONCILIACIONES BANCARIAS");
		actualizarAjusteMes();
		guardarDatosImpresion();
		cargarParametros();
		
		String subSinCobrar = "";
		String subTransito = "";
		String subNotasBancarias = "";
		String subTransacPag = "";
		String subPartConciliatorias = "";
		

		switch (reporte) {
	    case "000710IConciliacionConPartidas":
	       subSinCobrar = "000711SubCheqSinCobrar";
	       subTransito = "000712SubConsEnTransito";
	       subNotasBancarias = "000713SubNotaBancarias";
	       subTransacPag = "000714SubTransacPagPost";
	       subPartConciliatorias = "000715SubPartidasConciliatorias";
	        break;
	     case "002829IConciliacionConPartidas_ACACIAS":
		       subSinCobrar = "002830SubCheqSinCobrar_ACACIAS";
		       subTransito = "002831SubConsEnTransito_ACACIAS";
		       subNotasBancarias = "002832SubNotaBancarias_ACACIAS";
		       subTransacPag = "002833SubTransacPagPost_ACACIAS";
		       subPartConciliatorias = "002834SubPartidasConciliatorias_ACACIAS";
		       break;    
	    case "002613ConciliacionMelgar":
	       subSinCobrar = "002614SubSinCobrarMelgar";
	       subTransito = "002615SubTransitoMelgar";
	       subNotasBancarias = "002616SubNotasBancMelgar";
	       subTransacPag = "002617SubTransacPagPostMelgar";
	       subPartConciliatorias = "002618SubPartConciliatoriasMelgar";
	       
			parametro.put("PR_NOMBRE_ELABORO1", valorParametro("NOMBRE ELABORO 1"));
			parametro.put("PR_NOMBRE_ELABORO2", valorParametro("NOMBRE ELABORO 2"));
			parametro.put("PR_CARGO_ELABORO1", valorParametro("CARGO ELABORO 1"));
			parametro.put("PR_CARGO_ELABORO2", valorParametro("CARGO ELABORO 2"));
			
	        break;
	    case "002853ConciliacionConPartidas": //JM CC 2711 (no estoy de acuerdo con esta practica pero no hay tiempo para arreglar todo esto) 
	    	   subSinCobrar = "002855SubCheqSinCobrar";
		       subTransito = "002856SubConsEnTransito";
		       subNotasBancarias = "002857SubNotaBancarias";
		       subTransacPag = "002858SubTransacPagPost";
		       subPartConciliatorias = "002858SubPartidasConciliatorias";
		     break;
	    default:
	        break;
	}
		
		Map<String, Object> reemplazos = new HashMap<>();
		reemplazos.put(cCodCuenta, codCuenta);

		// 000711SubCheqSinCobrar
		//strTipos = getTiposComprobantes("'E'");
		strTipos = "'E'";
		String subCheqSinCobrarVA = null;
		if (manejaConciAnterior.equals("SI"))
		{
			condicion = "AND     D.PAGADOBANCO = 0  \n" +
					"AND    NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		}
		else
		{
			condicion = "AND   FECHA <= " + fechaArmada + " \n" +
					"AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0) "
					+ "OR (PAGADOBANCO <> 0 AND FECHA_CONCILIA > " + fechaArmada + "))";
			// SubCheqSinCobrarVA
			if (codEquivalente != null)
			{
				Map<String, Object> hashMap = new HashMap<>();
				hashMap.put(cCodCuenta, codEquivalente);
				hashMap.put(cStrTipos, strTipos);
				hashMap.put(cCondicion, condicion);
				subCheqSinCobrarVA = Reporteador.resuelveConsulta(
						subSinCobrar, modulo, hashMap);
			}
		}

		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		String subCheqSinCobrar = Reporteador.resuelveConsulta(
				subSinCobrar, modulo, reemplazos);

		// 000712SubConsEnTransito

		//strTipos = getTiposComprobantes("'S'");
		strTipos = "'S'";
		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicionSubConsEnTransito(fechaArmada));
		String subConsEnTransito = Reporteador.resuelveConsulta(
				subTransito, modulo, reemplazos);

		// 000713SubNotaBancarias
		if (manejaConciAnterior.equals("SI"))
		{
			//strTipos = getTiposComprobantes("'B', 'G' ,'A','D'");
			strTipos = "'B', 'G' ,'A','D'";
			condicion = "AND     PAGADOBANCO = 0  \n" +
					"AND     NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		}
		else
		{
			//strTipos = getTiposComprobantes("'B','G','A','D','I'");
			strTipos = "'B','G','A','D','I'";
			condicion = "AND     FECHA <= " + fechaArmada + " \n" +
					"AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0)  \n"
					+
					"        OR (PAGADOBANCO <> 0  \n" +
					"        AND FECHA_CONCILIA > " + fechaArmada + "))";
		}

		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		String subNotaBancarias = Reporteador.resuelveConsulta(
				subNotasBancarias, modulo, reemplazos);

		// 000714SubTransacPagPost
		if (manejaConciAnterior.equals("SI"))
		{
			condicion = "AND     D.ANO = " + ano + " \n" +
					"AND     NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		}
		else
		{
			condicion = "AND  FECHA_CONCILIA IS NOT NULL \n" +
					"AND     FECHA_CONCILIA <= " + fechaArmada;
		}

		//strTipos = getTiposComprobantes("'E','S','B','G','A','D','I'");
		strTipos = "'E','S','B','G','A','D','I'";
		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put(cCondicion, condicion);
		reemplazos.put("fechaArmada", fechaArmada);
		String subTransacPagPost = Reporteador.resuelveConsulta(
				subTransacPag, modulo, reemplazos);

		// 000715SubPartidasConciliatorias
		reemplazos.clear();
		reemplazos.put("ano", ano);
		reemplazos.put("mes", mes);
		int mesAnterior = SysmanFunciones.mes(fechaInicial) - 1;

		reemplazos.put("mesAnterior", mesAnterior);
		reemplazos.put(cCodCuenta, codCuenta);
		String subPartidasConciliatorias = Reporteador.resuelveConsulta(
				subPartConciliatorias, modulo,
				reemplazos);

		// 000710IConciliacionConPartidas
		String sql = Reporteador.resuelveConsulta(
				reporte, modulo,
				reemplazos);
		
//			Reporteador.resuelveConsulta(
//					"000710IConciliacionConPartidas", modulo,
//					reemplazos, parametro);

		// Map<String, Object> parametros = new HashMap<>();
		parametro.put("PR_STRSQL", sql);
		parametro.put("PR_STRSQL_SUBCHEQSINCOBRAR", subCheqSinCobrar);
		parametro.put("PR_STRSQL_SUBCHEQSINCOBRAR_VA", subCheqSinCobrarVA);
		parametro.put("PR_STRSQL_SUBCONSENTRANSITO", subConsEnTransito);
		parametro.put("PR_STRSQL_SUBNOTABANCARIAS", subNotaBancarias);
		parametro.put("PR_STRSQL_SUBTRANSACPAGPOST", subTransacPagPost);
		parametro.put("PR_STRSQL_SUBPARTIDASCONCILIATORIAS",
				subPartidasConciliatorias);
		parametro.put("PR_CUENTA", codCuenta);
		parametro.put("PR_NOMBRECUENTA",
				cuenta.replace(codCuenta + " ", ""));

	      String sumaTotal = "SELECT  SUM(VALOR_DEBITO-VALOR_CREDITO) TOTAL FROM    (";
            Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                            sumaTotal + subTransacPagPost + ")");
            String total = (String) reg.getCampos().get(cTotal);
            double totalTransacPagPost = Double
                            .parseDouble(total != null ? total : "0");

            double totalPartidasConciliatorias = 0;
            double totalChequesVA = 0;
            if ("SI".equals(manejaPartidasConci))
            {
                sumaTotal = "SELECT  SUM(PARTIDA_DEBITO-PARTIDA_CREDITO) TOTAL FROM    (";
                reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                                sumaTotal + subPartidasConciliatorias + ")");
                total = (String) reg.getCampos().get(cTotal);
                totalPartidasConciliatorias = Double
                                .parseDouble(total != null ? total : "0");
            }
            
            
            if (codEquivalente != null)
            {
                sumaTotal = "SELECT  SUM(VALOR_CREDITO-VALOR_DEBITO) TOTAL FROM    (";
                reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                                sumaTotal + subCheqSinCobrarVA + ")");
                total = (String) reg.getCampos().get(cTotal);
                totalChequesVA = Double
                                .parseDouble(total != null ? total : "0");
            }
            

            sumaTotal = "SELECT  SUM(VALOR_CREDITO-VALOR_DEBITO) TOTAL FROM    (";
            reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                            sumaTotal + subCheqSinCobrar + ")");
            total = (String) reg.getCampos().get(cTotal);
            double totalCheques = Double
                            .parseDouble(total != null ? total : "0");

            reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                            sumaTotal + subNotaBancarias + ")");
            total = (String) reg.getCampos().get(cTotal);
            double totalNotas = Double.parseDouble(total != null ? total : "0");

            reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                            sumaTotal + subConsEnTransito + ")");
            total = (String) reg.getCampos().get(cTotal);
            double totalConsignacion = Double
                            .parseDouble(total != null ? total : "0");

            String saldoCon = " SELECT SALDO" + mes + " AS SALDOCONTABLE \n"
                            + " FROM    PLAN_CONTABLE \n"
                            + " WHERE   COMPANIA = '" + compania + "' \n"
                            + "   AND   ANO = " + ano + " \n"
                            + "   AND   CODIGO = '" + codCuenta + "'";
            reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, saldoCon);
            total = (String) reg.getCampos().get("SALDOCONTABLE");
            double saldoCont = Double.parseDouble(total != null ? total : "0");

            double saldoConciliado = saldoCont + totalCheques
                            + totalConsignacion
                            + totalNotas
                            + totalTransacPagPost
                            + totalPartidasConciliatorias + totalChequesVA;

            parametro.put("PR_SALDOCONCILIADO", saldoConciliado);
            parametro.put("PR_AVISO", Double.doubleToRawLongBits(
                            saldoConciliado - saldoExtracto.doubleValue()) == 0
                                            ? "CONCILIACI�N CORRECTA"
                                            : "POR CONCILIAR");

		//		}
		//		catch (SystemException ex)
		//		{
		//			JsfUtil.agregarMensajeError(
		//					idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
		//					+ " "
		//					+ ex.getMessage());
		//			Logger.getLogger(ImpConciliacionControlador.class.getName())
		//			.log(Level.SEVERE, null, ex);
		//		}
	}
	/**
	 * Definicin de reemplazos para la consulta y envio de parametros para la generacion del informe
	 */
	private void generarLogicaInformeIDCBIS() {

		String strTipos = getClasesContables();
		String fechaArmada = SysmanFunciones.formatearFecha(fechaFinal);
		int modulo = Integer.parseInt(SessionUtil.getModulo());
		String reporte = valorParametro("FORMATO CONCILIACIONES BANCARIAS");

		actualizarAjusteMes();
		guardarDatosImpresion();
		cargarParametros();

		Map<String, Object> reemplazos = new HashMap<>();
		reemplazos.put(cCodCuenta, codCuenta);

		// 000714SubTransacPagPost -- 002517PartidasExtrato_sub

		reemplazos.put(cStrTipos, strTipos);
		reemplazos.put("fechaArmada", fechaArmada);
		String subTransacPagPost = Reporteador.resuelveConsulta("002517PartidasExtrato_sub", modulo, reemplazos);

		// 000715SubPartidasConciliatorias --002518PartidasLibros_sub
		reemplazos.clear();
		reemplazos.put("ano", ano);
		reemplazos.put("mes", mes);
		int mesAnterior = SysmanFunciones.mes(fechaInicial) - 1;
		reemplazos.put("mesAnterior", mesAnterior);
		reemplazos.put(cCodCuenta, codCuenta);
		String subPartidasConciliatorias = Reporteador.resuelveConsulta("002518PartidasLibros_sub", modulo, reemplazos);

		// 000710IConciliacionConPartidas -- 002516ConciliacionPartidasIDCBIS
		String sql = Reporteador.resuelveConsulta(reporte, modulo, reemplazos);


		parametro.put("PR_STRSQL", sql);
		parametro.put("PR_STRSQL_SUBTRANSACPAGPOST", subTransacPagPost);
		parametro.put("PR_STRSQL_SUBPARTIDASCONCILIATORIAS", subPartidasConciliatorias);
		parametro.put("PR_CUENTA", codCuenta);
		parametro.put("PR_NOMBRECUENTA", cuenta.replace(codCuenta + " ", ""));
		parametro.put("PR_IMAGENLOGO", "/opt/sysman/data/imagenes/escudo.png");

		String sumaTotal = "SELECT  SUM(VALOR_CREDITO-VALOR_DEBITO) TOTAL FROM    (";

		Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subTransacPagPost + ")");
		String total = (String) reg.getCampos().get(cTotal);
		double totalTransacPagPost = Double.parseDouble(total != null ? total : "0");

		double totalPartidasConciliatorias = 0;
		if ("SI".equals(manejaPartidasConci)) {
			sumaTotal = "SELECT  SUM(PARTIDA_DEBITO-PARTIDA_CREDITO) TOTAL FROM    (";
			reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sumaTotal + subPartidasConciliatorias + ")");
			total = (String) reg.getCampos().get(cTotal);
			totalPartidasConciliatorias = Double.parseDouble(total != null ? total : "0");
		}

		String saldoCon = " SELECT SALDO" + mes + " AS SALDOCONTABLE \n" + " FROM    PLAN_CONTABLE \n"
				+ " WHERE   COMPANIA = '" + compania + "' \n" + "   AND   ANO = " + ano + " \n" + "   AND   CODIGO = '"
				+ codCuenta + "'";
		reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, saldoCon);
		total = (String) reg.getCampos().get("SALDOCONTABLE");
		double saldoCont = Double.parseDouble(total != null ? total : "0");

		double saldoConciliado = saldoCont + totalTransacPagPost + totalPartidasConciliatorias;

		parametro.put("PR_SALDOCONCILIADO", saldoConciliado);
		parametro.put("PR_AVISO",
				Double.doubleToRawLongBits(saldoConciliado - saldoExtracto.doubleValue()) == 0
						? "CONCILIACI�N CORRECTA"
						: "POR CONCILIAR");

	}
	
	/**
	 * Realiza el llamado al m�todo en el que se define la l�gica del informe y envia el formato PDF para la generacion del mismo
	 */
	public void oprimirPresentar() {
		try {
			archivoDescarga = null;
			String reporte = valorParametro("FORMATO CONCILIACIONES BANCARIAS");

			if (reporte.equals("002516ConciliacionPartidasIDCBIS")) {

				generarLogicaInformeIDCBIS();
			} else {
				generarLogicaInforme();
			}

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametro, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.PDF);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel en la vista
	 *
	 * Realiza el llamado al m�todo en el que se define la l�gica del informe y envia el formato EXCEL para la generacion del mismo
	 *
	 */
	public void oprimirBtnExcel() {
		// <CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;
			String reporte = valorParametro("FORMATO CONCILIACIONES BANCARIAS");

			if (reporte.equals("002516ConciliacionPartidasIDCBIS")) {

				generarLogicaInformeIDCBIS();
			} else {
				generarLogicaInforme();
			}
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametro, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.EXCEL);

		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	private Object condicionSubConsEnTransito(String fechaArmada)
	{
		String condicion;
		if (manejaConciAnterior.equals("SI"))
		{
			condicion = "AND  ANO = " + ano + " \n" +
					"AND     PAGADOBANCO = 0  \n" +
					"AND    NVL(FECHA_CONCILIA ,FECHA) <= " + fechaArmada;
		}
		else
		{
			condicion = "AND     FECHA <= " + fechaArmada + " \n" +
					"AND     ((FECHA_CONCILIA IS NULL AND PAGADOBANCO = 0) OR (FECHA_CONCILIA > "
					+ fechaArmada + "))";
		}
		return condicion;
	}

	/**
	 * Actualiza el ajuste para el mes
	 */
	private void actualizarAjusteMes()
	{
		try
		{
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(cCodigoCuenta, codCuenta);
			param.put("MES", mes);
			param.put("SALDOEXTRACTO", saldoExtracto);
			param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					new Date());
			Parameter parameter = new Parameter();

			parameter.setFields(param);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							ImpConciliacionControladorUrlEnum.URL26660
							.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					parameter);
		}
		catch (SystemException ex)
		{
			Logger.getLogger(ImpConciliacionControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(
					idioma.getString("MSM_TRANS_INTERRUMPIDA") + " "
							+ ex);
		}
	}

	/**
	 * Guarda los datos de la impresion de la conciliacion
	 */
	private void guardarDatosImpresion() {
			
		if (ckGuardarObservaciones) {

		        try {
		        	//Traemos el registro asociado de la tabla PLAN_CONTABLE_OBSERVACIONES
		        	Registro reg = getObservacionesReg();
		    		Map<String, Object> param = new TreeMap<>();

					
					if(reg == null) {
						
				         param.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
				         param.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
				         param.put(GeneralParameterEnum.OBSERVACIONES.getName(), observaciones);
				         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
						 param.put(GeneralParameterEnum.ANO.getName(), ano);
						 param.put(GeneralParameterEnum.MES.getName(), mes);
						 param.put(GeneralParameterEnum.CODIGO.getName(),codCuenta );

				            
				            Parameter parameter = new Parameter();
				            parameter.setFields(param);
				          
							
							   UrlBean urlCreate = UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(ImpConciliacionControladorUrlEnum.URL1942002.getValue());

							requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parameter);
								
						
					}  else {
						 param.remove(GeneralParameterEnum.COMPANIA.getName());
						 param.remove(GeneralParameterEnum.ANO.getName());
						 param.remove(GeneralParameterEnum.MES.getName());
						 param.remove(GeneralParameterEnum.CODIGO.getName());
						 param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
						 param.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
						 param.put(GeneralParameterEnum.KEY_MES.getName(), mes);
						 param.put(GeneralParameterEnum.KEY_CODIGO.getName(),codCuenta );
				         param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
				         param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
				         param.put(GeneralParameterEnum.OBSERVACIONES.getName(), observaciones);
				            
				            Parameter parameter = new Parameter();
				            parameter.setFields(param);
				          

								UrlBean urlUpdate = UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(ImpConciliacionControladorUrlEnum.URL1942003.getValue());
					 
								requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
						
					}
					
		        } catch (SystemException e) {
		            Logger.getLogger(ImpConciliacionControlador.class.getName()).log(Level.SEVERE, null, e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }
		    
		    
		    
		} 
		else {
		    System.out.println("ckGuardarObservaciones es false, invocando el m�todo.");
		    
  
		}
	    
		
		
	    if (almacenaImpresion()) {
	        try {
	            Map<String, Object> param = new TreeMap<>();
	            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	            param.put(GeneralParameterEnum.ANO.getName(), ano);
	            param.put(cCodigoCuenta, codCuenta);
	            param.put("SALDOEXTRACTO", saldoExtracto);
	            param.put("FECHAINICIAL", fechaInicial);
	            param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
	            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
	            param.put(GeneralParameterEnum.OBSERVACIONES.getName(), observaciones);
	            
	            Parameter parameter = new Parameter();
	            parameter.setFields(param);
	            
	            UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ImpConciliacionControladorUrlEnum.URL25244.getValue());
	            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
	        } catch (SystemException e) {
	            Logger.getLogger(ImpConciliacionControlador.class.getName()).log(Level.SEVERE, null, e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	    }
	}


	/**
	 * Codigos TIPO_COMPROBANTE segun la(s) clase(s) contable(s) ingresada por parametro.
	 *
	 * @param clasesContables
	 * Codigo de las clases contables, separadas por coma.
	 * @return Cadena con los tipos de comprobante separados por coma.
	 */
	private StringBuilder getTiposComprobantes(String clasesContables)
	{
		StringBuilder tipos = new StringBuilder();
		int tamanio = 0;
		try
		{

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("CLASECONTABLE", clasesContables);

			List<Registro> codigos = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ImpConciliacionControladorUrlEnum.URL27777
									.getValue())
							.getUrl(), param));

			for (Registro codigo : codigos)
			{
				tamanio = tipos.length();
				if (tamanio != 0)
				{
					tipos.append(",");
				}
				tipos.append("'" + codigo.getCampos().get("CODIGO")
						+ "'");
			}

		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return tipos;

	}

	/**
	 * Actualiza la fecha limite por el ultimo dia y el saldo extracto segun el mes y el ano de la fecha ingresada.
	 */
	public void cambiarFechaLimite()
	{
		if (fechaInicial != null)
		{
			try
			{
				fechaInicial = SysmanFunciones.ultimoDiaDate(fechaInicial);
			}
			catch (ParseException ex)
			{
				Logger.getLogger(ImpConciliacionControlador.class.getName())
				.log(Level.SEVERE, null, ex);
			}
			saldoExtracto = traerSaldoExtracto(
					SysmanFunciones.mes(fechaInicial),
					SysmanFunciones.ano(fechaInicial));
		}
	}
	
	public void cargarParametros() {
		String vistoBuenoConciliacion = valorParametro("VISTO BUENO EN FORMATO DE CONCILIACION");
		String elaboroConciliacion = valorParametro("ELABORO EN CONCILIACION");
		String revisoConcilicacion = valorParametro("REVISO EN CONCILIACION");
		String cargoEncargado = valorParametro("CARGO ENCARGADO DE TESORERIA");
		String nombreEncargado = valorParametro("NOMBRE ENCARGADO DE TESORERIA");
		String cargoJefeConta = valorParametro("CARGO DE JEFE DE CONTABILIDAD");
		String nombreJefeConta = valorParametro("NOMBRE DE JEFE DE CONTABILIDAD");
		String nombreJefeDiv = valorParametro("NOMBRE JEFE DIVISION ADMINISTRATIVA");
		String cargoJefeFinan = valorParametro("CARGO JEFE FINANCIERO");
		String chequesinCobrar = valorParametro("CONCILIACION BANCARIA, CHEQUE SIN COBRAR");
		String consignacionTransito = valorParametro("CONCILIACION BANCARIA, CONSIGNACION DE TRANSITO");
		manejaPartidasConci = valorParametro("MANEJA INGRESO DE PARTIDAS CONCILIATORIAS");
		String formatoEspConci = valorParametro("FORMATO ESPECIAL DE CONCILIACION");
		String tituloConciBancaria = valorParametro("TITULO Y ELABORO ESPECIAL EN CONCILIACION BANCARIA");
		String modConciBancaria = valorParametro("MODIFICACION DE PALABRAS CONCILIACION BANCARIA");
		String chequeSinCobrarTot = valorParametro("CONCILIACION BANCARIA, CHEQUE SIN COBRAR, TOTAL");
		String consignacionTransitoTot = valorParametro("CONCILIACION BANCARIA, CONSIGNACION DE TRANSITO, TOTAL");
		String mostrarConciliador = valorParametro("MOSTRAR CONCILIADOR EN CONCILIACION BANCARIA");
		manejaConciAnterior = valorParametro("MANEJA CONCILIACION ANTERIOR");
		boolean modificaFirmas = "SI".equals(SysmanFunciones
					.nvl(valorParametro("MODIFICA FIRMAS EN CONCILIACION BANCARIA"), "NO"));
		
		parametro.put("PR_VoBo_EN_CONCILIACION", vistoBuenoConciliacion);
		parametro.put("PR_ELABORO_EN_CONCILIACION", elaboroConciliacion);
		parametro.put("PR_REVISO_EN_CONCILIACION",	revisoConcilicacion);
		parametro.put("PR_OBSERVACIONES", observaciones);
		parametro.put("PR_CARGO_ENCARGADO_DE_TESORERIA", cargoEncargado);
		parametro.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", nombreEncargado);
		parametro.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", cargoJefeConta);
		parametro.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", nombreJefeConta);
		parametro.put("PR_NOMBRE_JEFE_DIVISION_ADMINISTRATIVA", nombreJefeDiv);
		parametro.put("PR_CARGO_JEFE_FINANCIERO", cargoJefeFinan);
		parametro.put("PR_SALDOEXTRACTO", saldoExtracto.doubleValue());
        parametro.put("PR_CONCILIACION_BANCARIA_CHEQUE_SIN_COBRAR", chequesinCobrar);
        parametro.put("PR_CONCILIACION_BANCARIA,_CONSIGNACION_DE_TRANSITO", consignacionTransito);
		parametro.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
		parametro.put("PR_CIUDADCOMPANIA", SessionUtil.getCompaniaIngreso().getCiudad());
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, mes - 1);
		calendar.set(Calendar.YEAR, ano);
		parametro.put("PR_FECHA", calendar.getTime());
		parametro.put("PR_MES", getNombreMes(mes));
		parametro.put("PR_ANO", ano);
		parametro.put("PR_MANEJA_INGRESO_PARTIDAS_CONCILIATORIAS", manejaPartidasConci);
	    parametro.put("PR_FORMATO_ESPECIAL_CONCILIACION",formatoEspConci);
		parametro.put("PR_TITULO_ELABORO_ESPECIAL_CONCILIACION_BANCARIA", tituloConciBancaria);
		parametro.put("PR_MODIFICACION_PALABRAS_CONCILIACION_BANCARIA", modConciBancaria);
		parametro.put("PR_TEXTO_TOTAL_SUBCHEQSINCOBRAR", chequeSinCobrarTot);
		parametro.put("PR_TEXTO_TOTAL_SUBCHEQSINCOBRAR_VA", "Total Cheques Sin Cobrar Vigencia anterior ");
		parametro.put("PR_TEXTO_TOTAL_SUBCONSENTRANSITO", consignacionTransitoTot);
		parametro.put("PR_MOSTRAR_CONCILIADOR", mostrarConciliador);
		parametro.put("PR_MODIFICA_FIRMAS", modificaFirmas);
		
		
	}
	

	/*
	 * Getters and Setters
	 */

	public String getCuenta()
	{
		return cuenta;
	}

	public BigDecimal getSaldoExtracto()
	{
		return saldoExtracto;
	}

	public void setSaldoExtracto(BigDecimal saldoExtracto)
	{
		this.saldoExtracto = saldoExtracto;
	}

	public void setCuenta(String cuenta)
	{
		this.cuenta = cuenta;
	}

	public Date getFechaInicial()
	{
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial)
	{
		this.fechaInicial = fechaInicial;
	}
	
	/**
	 * @return the etiquetaGuardarObservaciones
	 */
	public boolean isEtiquetaGuardarObservaciones() {
		return etiquetaGuardarObservaciones;
	}

	/**
	 * @param etiquetaGuardarObservaciones the etiquetaGuardarObservaciones to set
	 */
	public void setEtiquetaGuardarObservaciones(boolean etiquetaGuardarObservaciones) {
		this.etiquetaGuardarObservaciones = etiquetaGuardarObservaciones;
	}

	public Date getFechaFinal()
	{
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal)
	{
		this.fechaFinal = fechaFinal;
	}

	public String getObservaciones()
	{
		return observaciones;
	}

	public void setObservaciones(String observaciones)
	{
		this.observaciones = observaciones;
	}

	public StreamedContent getArchivoDescarga()
	{
		return archivoDescarga;
	}

	public String getCodEquivalente()
	{
		return codEquivalente;
	}

	public void setCodEquivalente(String codEquivalente)
	{
		this.codEquivalente = codEquivalente;
	}

	/**
	 * Trae el nombre del mes a partir de su numero.
	 *
	 * @param numero
	 * Numero de mes. Inicia en 1.
	 * @return Cadena con el nombre del mes.
	 */
	private String getNombreMes(int numero)
	{
		int aux = numero;
		String nombreMes = "";
		DateFormatSymbols dfs = new DateFormatSymbols(new Locale("es"));
		String[] meses = dfs.getMonths();
		aux--;
		if ((aux >= 0) && (aux <= 11))
		{
			nombreMes = SysmanFunciones.initCap(meses[aux]);
		}
		return nombreMes;
	}

	/**
	 * @return the cambiarckGuardarObservaciones
	 */
	public boolean isCambiarckGuardarObservaciones() {
		return cambiarckGuardarObservaciones;
	}

	/**
	 * @return the ckGuardarObservaciones
	 */
	public boolean isCkGuardarObservaciones() {
		return ckGuardarObservaciones;
	}

	/**
	 * @param ckGuardarObservaciones the ckGuardarObservaciones to set
	 */
	public void setCkGuardarObservaciones(boolean ckGuardarObservaciones) {
		this.ckGuardarObservaciones = ckGuardarObservaciones;
	}

	/**
	 * @param cambiarckGuardarObservaciones the cambiarckGuardarObservaciones to set
	 */
	public void setCambiarckGuardarObservaciones(boolean cambiarckGuardarObservaciones) {
		this.cambiarckGuardarObservaciones = cambiarckGuardarObservaciones;
	}

}
