package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.enums.FcuentaalmacenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 08/04/2016
 * @version 2, 27/04/2017 Se modifico controlador segun especificaciones del SonarLint y se agregaron textos en bean para los mensajes. Fueron cambiadas las llamadas del Acciones por la invocacion de
 * EJB.
 * 
 * @author ybecerra
 * @version 3, 11/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class FcuentaalmacenControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el cual se ingresa en la aplicacion
     */
    private final String modulo;
    private final String parametroFechaInicial;
    private final String parametroFechaFinal;

    private final String strPrFechas;
    private final String strFormatoFecha;
    private final String strFechaInicial;
    private final String strFechaFinal;
    private final String excelplano;

    // <DECLARAR_ATRIBUTOS>

    private String opcion;
    private String codigoCompania;
    private String ordenado;
    private String presentado;
    private Date fechaInicial;
    private Date fechaFinal;
    private Integer grupo;
    private String nombreCom;
    private boolean mostrarBoton;
    /**
     * Define el valor del indicador <b>Sin consumo controlado en servicio</b>
     */
    private boolean sinConsumoCont;
    
    private boolean excelpl;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private String codigoCompaniaP;
    private String fechaInicialP;
    private String fechaFinalP;
    private String grupoP;
    private String opcionP;
    private String nombreComP;
    private String parUspec;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Object[] valores;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FcuentaalmacenControlador
     */
    public FcuentaalmacenControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        parametroFechaInicial = "#$fechaInicial#$";
        parametroFechaFinal = "#$fechaFinal#$";

        strPrFechas = "PR_FECHAS";
        strFormatoFecha = "dd/MMM/yyyy";
        strFechaInicial = "fechaInicial";
        strFechaFinal = "fechaFinal";
        excelplano = "excelpl";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FCUENTAALMACEN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                codigoCompaniaP = (String) parametrosEntrada
                                .get("codigoCompaniaP");
                fechaInicialP = (String) parametrosEntrada.get("fechaInicialP");
                fechaFinalP = (String) parametrosEntrada.get("fechaFinalP");
                grupoP = (String) parametrosEntrada.get("grupoP");
                opcionP = (String) parametrosEntrada.get("opcionP");
                nombreComP = (String) parametrosEntrada.get("nombreComP");
                codigoCompania = codigoCompaniaP;
                fechaInicial = SysmanFunciones.convertirAFecha(fechaInicialP);
                fechaFinal = SysmanFunciones.convertirAFecha(fechaFinalP);
                grupo = Integer.valueOf(grupoP);
                opcion = opcionP;
                nombreCom = nombreComP;
            }
            else
            {
                fechaInicial = new Date();
                fechaFinal = new Date();
                opcion = "2";
                mostrarBoton = true;
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    public void cambiarOpciones()
    {
        // <CODIGO_DESARROLLADO>
        if (opcion.equals("2") || opcion.equals("3") || opcion.equals("4")
                        || opcion.equals("15"))
        {
            mostrarBoton = true;
        }
        else
        {
            mostrarBoton = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pantalla en la vista
     *
     */
    public void oprimirPantalla()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (SysmanFunciones.ano(fechaInicial) != SysmanFunciones
                        .ano(fechaFinal))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3097"));
        }
        if (SysmanFunciones.ano(fechaInicial) == SysmanFunciones
                        .ano(fechaFinal))
        {
        	if(opcion.equals("16"))
        	{        		
        		String[] campos = {};	               
	            String[] valores = {};
	
	            SessionUtil.cargarModalDatosFlashCerrar(
	                                Integer.toString(
	                                                GeneralCodigoFormaEnum.LISTA_INVENTARIO_EXIS_CONTROLADOR
	                                                                .getCodigo()),
	                                SessionUtil.getModulo(), campos, valores);        		
        	}
        	else
        	{
        		generarInforme(FORMATOS.PDF);
        	}
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Salir en la vista
     *
     */
    public void oprimirSalir()
    {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionarMenu();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Metodo que se ejecuta al darle clic al boton Ejecutar del formulario
     * 
     * @param fomato
     */
    public void generarInforme(ReportesBean.FORMATOS fomato)
    {
        archivoDescarga = null;
        String parReporte = " ";
        try
        {
            // Parametro para el Almacenista
            String almacenista;
            almacenista = ejbSysmanUtil.consultarParametro(compania,
                            "ALMACENISTA", modulo, new Date(), false);
            // Parametro para el Nombre del Contador
            String contador = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE CONTADOR", modulo, new Date(), false);
            // Parametro Nombre Gerente
            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE GERENTE", modulo, new Date(),
                            false);
            // Parametro Nombre Responsable Consumo
            String responsableConsumo = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE RESPONSABLE CONSUMO", modulo,
                            new Date(), false);
            // Parametro Cargo del Gerente
            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO GERENTE", modulo, new Date(),
                            false);
            // Parametro Cargo Coordinador Almacen
            String cargoCoordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO COORDINADOR ALMACEN", modulo,
                            new Date(), false);
            // Parametro Contraloria Departamental
            String contraloriaDepartamental = ejbSysmanUtil.consultarParametro(
                            compania, "CONTRALORIA DEPARTAMENTAL",
                            modulo, new Date(), false);
            // Parametro fecha corte almacen
            String fechaC = ejbSysmanUtil.consultarParametro(compania,
                            "FECHA DE CORTE PARA INICIO DEL ALMACEN", modulo,
                            new Date(), false);
            // Parametro Coordinador Almacen
            String coordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania, "COORDINADOR ALMACEN", modulo,
                            new Date(), false);
            // Parametro Cargo Almacenista
            String cargoAlmacenista = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO ALMACENISTA", modulo,
                            new Date(), false);
            
            Date fechaCorte = SysmanFunciones.convertirAFecha(fechaC,
                            "dd/MM/yyyy");
            
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("grupo", grupo);
            reemplazar.put(strFechaInicial,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(strFechaFinal,
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("fechaCorte",
                            SysmanFunciones.formatearFecha(fechaCorte));
            reemplazar.put("sinConsumoControlado", sinConsumoCont ? -1 : 0);
            reemplazar.put("excelplano", excelpl ? -1 : 0);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ALMACENISTA", almacenista);
            parametros.put("PR_NOMBRE_CONTADOR", contador);
            parametros.put("PR_NOMBRECOMPANIA", nombreCom);
            parametros.put("PR_NOMBREGERENTE", nombreGerente);
            parametros.put("PR_NOMBRERESPONSABLECONSUMO", responsableConsumo);
            parametros.put("PR_CARGOCOORDINADORALMACEN", cargoCoordinadorAlmacen);
            parametros.put("PR_CARGOGERENTE", cargoGerente);
            parametros.put("PR_CONTRALORIADEPARTAMENTAL",
                            contraloriaDepartamental);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBRECIUDAD",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_DIRECCION", SessionUtil.getCompaniaIngreso()
                            .getDireccion() == null ? " "
                                            : SessionUtil.getCompaniaIngreso()
                                                            .getDireccion());
            parametros.put("PR_TELEFONO", SessionUtil.getCompaniaIngreso()
                            .getTelefono() == null ? " "
                                            : SessionUtil.getCompaniaIngreso()
                                                            .getTelefono());
            parametros.put("PR_COORDINADORALMACEN", coordinadorAlmacen);
            parametros.put("PR_CARGOALMACENISTA", cargoAlmacenista);
            
            if(parUspec.equals("SI") && opcion.equals("2")) {
            	parametros.put("PR_FORMATOS_USPEC", true);
            } else {
            	parametros.put("PR_FORMATOS_USPEC", false);
            }

            String[] campos = { strFechaInicial, strFechaFinal, "companiaSel",
                            "grupo", "excelplano" };
            String[] valores1 = { SysmanFunciones
                            .convertirAFechaCadena(fechaInicial),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal),
                            codigoCompania, grupo.toString(), String.valueOf(excelpl) };
            parReporte = evaluarOpcion(parametros, campos, valores1);
            
            
            if (!parReporte.isEmpty())
            {
                Reporteador.resuelveConsulta(parReporte,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                fomato);
            }
            
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", parReporte)
                                            + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            " " + e.getMessage()));
        }

    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInformeExcel(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>

    }

    public void generarInformeExcel(ReportesBean.FORMATOS formato)
    {
        String parReporte = " ";
        try
        {
            // Parametro para el Almacenista
            String almacenista;
            almacenista = ejbSysmanUtil.consultarParametro(compania,
                            "ALMACENISTA", modulo, new Date(), false);
            // Parametro para el Nombre del Contador
            String contador = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE CONTADOR", modulo, new Date(), false);
            // Parametro Nombre Gerente
            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE GERENTE", modulo, new Date(),
                            false);
            // Parametro Nombre Responsable Consumo
            String responsableConsumo = ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE RESPONSABLE CONSUMO", modulo,
                            new Date(), false);
            // Parametro Cargo del Gerente
            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO GERENTE", modulo, new Date(),
                            false);
            // Parametro Cargo Coordinador Almacen
            String cargoCoordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO COORDINADOR ALMACEN", modulo,
                            new Date(), false);
            // Parametro Contraloria Departamental
            String contraloriaDepartamental = ejbSysmanUtil.consultarParametro(
                            compania, "CONTRALORIA DEPARTAMENTAL",
                            modulo, new Date(), false);
            // Parametro fecha corte almacen
            String fechaC = ejbSysmanUtil.consultarParametro(compania,
                            "FECHA DE CORTE PARA INICIO DEL ALMACEN", modulo,
                            new Date(), false);
            // Parametro Coordinador Almacen
            String coordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania, "COORDINADOR ALMACEN", modulo,
                            new Date(), false);
            // Parametro Cargo Almacenista
            String cargoAlmacenista = ejbSysmanUtil.consultarParametro(
                            compania, "CARGO ALMACENISTA", modulo,
                            new Date(), false);
            
            boolean valoresEncero = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(
     				compania,"INCLUIR VALORES TOTALES A CERO EN INFORME DE CUENTA ALMACEN",
     				SessionUtil.getModulo(),new Date(),true ),"NO"));
           
            
            Date fechaCorte = SysmanFunciones.convertirAFecha(fechaC,
                    		"dd/MM/yyyy");
            
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("grupo", grupo);
            reemplazar.put(strFechaInicial,
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put(strFechaFinal,
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("fechaCorte",
                            SysmanFunciones.formatearFecha(fechaCorte));
            reemplazar.put("sinConsumoControlado", sinConsumoCont ? -1 : 0);
            reemplazar.put("excelplano", excelpl ? -1 : 0);
            

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ALMACENISTA", almacenista);
            parametros.put("PR_NOMBRE_CONTADOR", contador);
            parametros.put("PR_NOMBRECOMPANIA", nombreCom);
            parametros.put("PR_NOMBREGERENTE", nombreGerente);
            parametros.put("PR_NOMBRERESPONSABLECONSUMO", responsableConsumo);
            parametros.put("PR_CARGOCOORDINADORALMACEN", cargoCoordinadorAlmacen);
            parametros.put("PR_CARGOGERENTE", cargoGerente);
            parametros.put("PR_CONTRALORIADEPARTAMENTAL",
                            contraloriaDepartamental);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_NOMBRECIUDAD",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_DIRECCION", SessionUtil.getCompaniaIngreso()
                            .getDireccion() == null ? " "
                                            : SessionUtil.getCompaniaIngreso()
                                                            .getDireccion());
            parametros.put("PR_TELEFONO", SessionUtil.getCompaniaIngreso()
                            .getTelefono() == null ? " "
                                            : SessionUtil.getCompaniaIngreso()
                                                            .getTelefono());
            parametros.put("PR_COORDINADORALMACEN", coordinadorAlmacen);
            parametros.put("PR_CARGOALMACENISTA", cargoAlmacenista);


            
            if(parUspec.equals("SI") && opcion.equals("2")) {
            	parametros.put("PR_FORMATOS_USPEC", true);
            } else {
            	parametros.put("PR_FORMATOS_USPEC", false);
            }
            
            String[] campos = { strFechaInicial, strFechaFinal, "companiaSel",
                            "grupo","excelplano" };
            String[] valores1 = { SysmanFunciones
                            .convertirAFechaCadena(fechaInicial),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal),
                            codigoCompania, grupo.toString(),String.valueOf(excelpl)};
            parReporte = evaluarOpcion(parametros, campos, valores1);
            
            
            if(opcion.equals("2") && excelpl == true) 
            {
            	String grupoEtiq = parUspec.equals("SI") ? "CATEGORIA" : "GRUPO";
            	parReporte = "800378CuentaAlmacen";
            	generarTituloCtaAlmacen(parametros);
            	reemplazar.put("grupoEtiq", grupoEtiq);
        	
            
        	String datosExcel = Reporteador.resuelveConsulta("800378CuentaAlmacen", Integer.parseInt(modulo),
        			reemplazar);
        	
        	try {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.EXCEL,"800378CuentaAlmacen");
			} catch (SQLException | DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	
            }
            
            if(opcion.equals("3") && excelpl == true) 
            {
            	String TotalEncero = "";
            	if (valoresEncero) {
            		TotalEncero = "(VALORSALDOANTERIOR + VALORENTRADAS + VALORSALIDAS +  VALORAJUSTESDEBITO +  VALORAJUSTESCREDITO) NOT IN(0) OR \n" + 
            					  "((VALORSALDOANTERIOR + VALORENTRADAS + VALORSALIDAS +  VALORAJUSTESDEBITO +  VALORAJUSTESCREDITO) = 0 AND(CANTIDADSALDOANTERIOR +  CANTIDADENTRADAS - CANTIDADSALIDAS + CANTIDADAJUSTESDEBITO +  CANTIDADAJUSTESCREDITO) <> 0) ";
            		
            	}else {
            		TotalEncero = "(VALORSALDOANTERIOR + VALORENTRADAS + VALORSALIDAS +  VALORAJUSTESDEBITO +  VALORAJUSTESCREDITO) NOT IN(0)";
            		
            	}
            	reemplazar.put("Incluirvalorencero", TotalEncero);
            	parReporte = "800379CuentaAlmacenCantidades";
            	generarTituloCtaAlmacen(parametros);
        	
            
        	String datosExcel = Reporteador.resuelveConsulta("800379CuentaAlmacenCantidades", Integer.parseInt(modulo),
        			reemplazar);
        	
        	try {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.EXCEL,"800379CuentaAlmacenCantidades");
			} catch (SQLException | DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	
            }
            
            
            if(opcion.equals("4") && excelpl == true) 
            {
            	parReporte = "800380CuentaAlmacenContabilidad";
            	generarTituloCtaAlmacen(parametros);
        	
            
        	String datosExcel = Reporteador.resuelveConsulta("800380CuentaAlmacenContabilidad", Integer.parseInt(modulo),
        			reemplazar);
        	
        	try {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.EXCEL,"800380CuentaAlmacenContabilidad");
			} catch (SQLException | DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            }
            
            if(opcion.equals("15") && excelpl == true) 
            {
            	parReporte = "800381CuentaAlmacenContabilidadCuentaGrupo";
            	generarTituloCtaAlmacen(parametros);
        	
            
        	String datosExcel = Reporteador.resuelveConsulta("800381CuentaAlmacenContabilidadCuentaGrupo", Integer.parseInt(modulo),
        			reemplazar);
        	
        	try {
        		
        		boolean informeResumen = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA INFORME RESUMEN EN ALMACEN", modulo, new Date(), true),"NO"));
        		
        		if(informeResumen) {
            		
            		archivoDescarga = generarReporteConsolidadoResumen(parReporte, datosExcel, reemplazar, compania, modulo, fechaCorte);
                    
            	} else {
        		
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, ConectorPool.ESQUEMA_SYSMAN,
						FORMATOS.EXCEL,"800381CuentaAlmacenContabilidadCuentaGrupo");
            	}
        		
			} catch (SQLException | DRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        	
            }
            
            
            
        	else     { 
            if (!parReporte.isEmpty())
            {
                Reporteador.resuelveConsulta(parReporte,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(parReporte,
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            }
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", parReporte)
                                            + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            " " + e.getMessage()));
        }
    }
    
    private StreamedContent generarReporteConsolidadoResumen(String nombreReporte, String strSql, Map<String, Object> reemplazar, String compania, String modulo, Date fechaCorte) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Workbook workbook = new XSSFWorkbook(JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL).getStream());
            Sheet sheet = workbook.getSheetAt(0);

            CellStyle contabilidadStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            contabilidadStyle.setDataFormat(df.getFormat("#,##0.00"));

            Row r4 = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cellT = r4.createCell(4);
            cellT.setCellValue("TOTAL GENERAL");

            String[] columnas = { "F", "G", "H", "I", "J", "K"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = r4.createCell(i + 5);
                cell.setCellFormula("SUM(" + columnas[i] + "2:" + columnas[i] + (sheet.getLastRowNum()) + ")");
                cell.setCellStyle(contabilidadStyle);
            }
            
            String fechaInicialC = SysmanFunciones.convertirAFechaCadena(fechaInicial);
            String fechaFinalC = SysmanFunciones.convertirAFechaCadena(fechaFinal);
            String fechaCorteC = SysmanFunciones.convertirAFechaCadena(fechaCorte);
            Map<String, Object> param = new HashMap<>();
            param.put("COMPANIA", compania);
            param.put("FECHAINICIAL", fechaInicialC);
            param.put("FECHAFINAL", fechaFinalC);
            param.put("FECHACORTE", fechaCorteC);
            param.put("SINCONTROLADO", reemplazar.get("sinConsumoControlado"));

            List<Registro> listaReporte2 = RegistroConverter.toListRegistro(
                requestManager.getList(
                    UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FcuentaalmacenControladorUrlEnum.URL179007.getValue())
                        .getUrl(),
                    param));

            int filaTotalGeneral = sheet.getLastRowNum();
            int filaEncabezado = filaTotalGeneral + 2;
            Row headerRow = sheet.createRow(filaEncabezado);

            String[] encabezados = {
                "CUENTA", "NOMBRECUENTA", "ANTERIOR", "ENTRADAS", "SALIDAS",
                "AJUSTES_DEBITO", "AJUSTES_CREDITO", "SALDO_FINAL"};

            int col = 3;
            for (String encabezado : encabezados) {
                Cell cell = headerRow.createCell(col++);
                cell.setCellValue(encabezado);
            }

            int filaInicioDatos = filaEncabezado + 1;
            int filaDatos = filaInicioDatos;
            for (Registro reg : listaReporte2) {
                Row dataRow = sheet.createRow(filaDatos++);

                dataRow.createCell(3).setCellValue(String.valueOf(reg.getCampos().get("CUENTA")));
                dataRow.createCell(4).setCellValue(String.valueOf(reg.getCampos().get("NOMBRECUENTA")));

                Cell cell2 = dataRow.createCell(5);
                cell2.setCellValue(parseDouble(reg.getCampos().get("ANTERIOR")));
                cell2.setCellStyle(contabilidadStyle);

                Cell cell3 = dataRow.createCell(6);
                cell3.setCellValue(parseDouble(reg.getCampos().get("ENTRADAS")));
                cell3.setCellStyle(contabilidadStyle);

                Cell cell4 = dataRow.createCell(7);
                cell4.setCellValue(parseDouble(reg.getCampos().get("SALIDAS")));
                cell4.setCellStyle(contabilidadStyle);

                Cell cell5 = dataRow.createCell(8);
                cell5.setCellValue(parseDouble(reg.getCampos().get("AJUSTES_DEBITO")));
                cell5.setCellStyle(contabilidadStyle);
                
                Cell cell6 = dataRow.createCell(9);
                cell6.setCellValue(parseDouble(reg.getCampos().get("AJUSTES_CREDITO")));
                cell6.setCellStyle(contabilidadStyle);
                
                Cell cell7 = dataRow.createCell(10);
                cell7.setCellValue(parseDouble(reg.getCampos().get("SALDO_FINAL")));
                cell7.setCellStyle(contabilidadStyle);
            }
            
            Row rt = sheet.createRow(sheet.getLastRowNum() + 1);
            Cell cellT2 = rt.createCell(4);
            cellT2.setCellValue("TOTAL GENERAL");

            String[] columnasT = { "F", "G", "H", "I", "J", "K"};
            for (int i = 0; i < columnasT.length; i++) {
            	Cell cell = rt.createCell(i + 5);
                cell.setCellFormula("SUM(" + columnasT[i] + filaInicioDatos + ":" + columnasT[i] + sheet.getLastRowNum() + ")");
                cell.setCellStyle(contabilidadStyle);
            }

            workbook.write(out);
            workbook.close();
            
            return JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreReporte + ".xlsx");
        }
    }
    
    private double parseDouble(Object val) {
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Metodo que se ejecuta en generarInforme, valida segun la opcion seleccionada en el formulario, que informe se genera o que formulario carga
     * 
     * @param parametros
     * @param campos
     * @param valore
     * @return nombreReporte
     */
    private String evaluarOpcion(Map<String, Object> parametros,
                    String[] campos, String[] valore)
    {
        String parReporte = "";
        switch (opcion)
        {
        case "2":
            parReporte = "000605CuentaAlmacen";
            generarTituloCtaAlmacen(parametros);
            break;
        case "3":
            parReporte = "000609CuentaAlmacenCantidades";
            generarTituloCtaAlmacenCantidades(parametros);
            break;
        case "4":
            parReporte = "000612CuentaAlmacenContabilidad";
            generarTituloCtaAlmContabilidad(parametros);
            break;
        case "5":
            parReporte = "000611CuentaAlmacenDependenciaGrupo";
            generarTituloCtaAlmDependenciaGrupo(parametros);
            break;
        case "6":
            parReporte = "000617IInventarioSuministroFechas";
            generarTituloInvSuministroFechas(parametros);
            break;
        case "7":
            revisarInconsistencias();
            break;

        case "11":
            abrirFormRevisarCuentaAlmacen();
            break;

        case "13":
            parReporte = "000623ElementosCuentaContable";
            generarTituloElementosCtaContable(parametros);
            break;
        case "15":
            parReporte = "001961CuentaAlmacenContabilidadCuentaGrupo";
            generarTituloContabilidadCuentaGrupo(parametros);
            break;
        default:
            cargarFormularios(campos, valore);
            break;
        }
        return parReporte;
    }

    /**
     * Metodo que se llama en evaluarOpcion, valida segun la opcion 10,12 y 14 que formulario se va a cargar
     * 
     * @param campos
     * @param valore
     */
    private void cargarFormularios(String[] campos, String[] valore)
    {
        switch (opcion)
        {
        case "10":
            cargarCuentaAlmacenDetallado(campos, valore);
            break;
        case "12":
            cargarFormInventarioBodega(campos, valore);
            break;
        case "14":
            cargarFormCuentaAlmacenDeprec(campos, valore);
            break;
        default:
            break;
        }

    }

    /**
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (2) -> Imprimir informe almacen solo valores(Num Digitos)
     * 
     * @param parametros
     */
    private void generarTituloCtaAlmacen(Map<String, Object> parametros) {
    	String titulo = "";
    	
        try {
        	
        	if(parUspec.equals("SI") && opcion.equals("2")) {
        		titulo = "BOLETÍN DE ALMACÉN";
        	} else {
        		titulo = "Cuentas del almacén";
        	}
        	
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB1852")
                            			    .replace("#$titulo#$", titulo)
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (3) -> Informe almacén cantidades y valores(16 digitos)
     * 
     * @param parametros
     */
    private void generarTituloCtaAlmacenCantidades(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB3098")
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (4) -> Imprimir informe para contabilidad(Num digitos)
     * 
     * @param parametros
     */
    private void generarTituloCtaAlmContabilidad(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB3099")
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (5) -> Resumen movimientos por dependencia y agrupaciones
     * 
     * @param parametros
     */
    private void generarTituloCtaAlmDependenciaGrupo(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal,
                                            strFormatoFecha));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (6) -> Existencia de suministros a una fecha
     * 
     * @param parametros
     */
    private void generarTituloInvSuministroFechas(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB3100")
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (7) -> Revisar Inconsistencias
     */
    private void revisarInconsistencias()
    {
        String aux;
        try
        {
            aux = ejbAlmacenCuatro.verificarCuentaAlmacen(compania,
                            fechaInicial, fechaFinal);
            ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(aux);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "Inconsistencias.txt");
        }
        catch (SystemException | JRException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo llamado en cargarFormularios, se ejecuta si la opcion seleccionada es (10) -> Informe almacén cantidades y valores(Detallado)
     * 
     * @param campos
     * @param valore
     */
    private void cargarCuentaAlmacenDetallado(String[] campos,
                    String[] valore)
    {
        String numForm = String
                        .valueOf(GeneralCodigoFormaEnum.CUENTAALMACENDETALLADO_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numForm, modulo, campos,
                        valore);
    }

    /**
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (11) -> Revisar detalle de un codigo
     */
    private void abrirFormRevisarCuentaAlmacen()
    {
        try
        {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.REVISARCUENTAALMACENS_CONTROLADOR
                                            .getCodigo()));
            Map<String, Object> params = new HashMap<>();
            params.put(strFechaInicial, SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            params.put(strFechaFinal,
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            params.put("codigoCompaniaP", codigoCompania);
            params.put("grupoP", grupo.toString());
            params.put("nombreComP", nombreCom);
            params.put("opcionP", opcion);
            direccionador.setParametros(params);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo llamado en cargarFormularios, se ejecuta si la opcion seleccionada es (12) ->
     * 
     * @param campos
     * @param valore
     */
    private void cargarFormInventarioBodega(String[] campos, String[] valore)
    {
        String numForm = String.valueOf(
                        GeneralCodigoFormaEnum.I_DOSFINVENTARIOBODEGA_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numForm, modulo, campos,
                        valore);
    }

    /**
     * 
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (13) -> Informe de elementos por cuenta contable
     * 
     * @param campos
     * @param valore
     * @param parametros
     */
    private void generarTituloElementosCtaContable(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB3101")
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo llamado en cargarFormularios, se ejecuta si la opcion seleccionada es (14) -> Comparacion de relación de depreciaciones contable
     * 
     * @param campos
     * @param valore
     */
    private void cargarFormCuentaAlmacenDeprec(String[] campos,
                    String[] valore)
    {
        String numForm = String
                        .valueOf(GeneralCodigoFormaEnum.ICUENTAALMACENDEPREC_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlashCerrar(numForm, modulo, campos,
                        valore);
    }

    /**
     * 
     * Metodo llamado en evaluarOpcion, se ejecuta si la opcion seleccionada es (15) -> Imprimir informe para contabilidad por cuenta y grupo
     * 
     * @param campos
     * @param valore
     * @param parametros
     */
    private void generarTituloContabilidadCuentaGrupo(
                    Map<String, Object> parametros)
    {
        try
        {
            parametros.put(strPrFechas,
                            idioma.getString("TB_TB4257")
                                            .replace(parametroFechaInicial,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaInicial,
                                                                            strFormatoFecha))
                                            .replace(parametroFechaFinal,
                                                            SysmanFunciones.convertirAFechaCadena(
                                                                            fechaFinal,
                                                                            strFormatoFecha)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        codigoCompania = SessionUtil.getCompania();
        nombreCom = SessionUtil.getCompaniaIngreso().getNombre();
        try
        {
            grupo = Integer.valueOf(ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo,
                            new Date(), false));
            
            parUspec = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
        					"FORMATOS UNICOS USPEC", modulo, 
        					new Date(), false),"NO").toString();
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable opcion
     * 
     * @return opcion
     */
    public String getOpcion()
    {
        return opcion;
    }

    /**
     * Asigna la variable opcion
     * 
     * @param opcion
     * Variable a asignar en opcion
     */
    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    /**
     * Retorna la variable codigoCompania
     * 
     * @return codigoCompania
     */
    public String getCodigoCompania()
    {
        return codigoCompania;
    }

    /**
     * Asigna la variable codigoCompania
     * 
     * @param codigoCompania
     * Variable a asignar en codigoCompania
     */
    public void setCodigoCompania(String codigoCompania)
    {
        this.codigoCompania = codigoCompania;
    }

    /**
     * Retorna la variable ordenado
     * 
     * @return ordenado
     */
    public String getOrdenado()
    {
        return ordenado;
    }

    /**
     * Asigna la variable ordenado
     * 
     * @param ordenado
     * Variable a asignar en ordenado
     */
    public void setOrdenado(String ordenado)
    {
        this.ordenado = ordenado;
    }

    /**
     * Retorna la variable presentado
     * 
     * @return presentado
     */
    public String getPresentado()
    {
        return presentado;
    }

    /**
     * Asigna la variable presentado
     * 
     * @param presentado
     * Variable a asignar en presentado
     */
    public void setPresentado(String presentado)
    {
        this.presentado = presentado;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;

    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {

        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable grupo
     * 
     * @return grupo
     */
    public Integer getGrupo()
    {
        return grupo;
    }

    /**
     * Asigna la variable grupo
     * 
     * @param grupo
     * Variable a asignar en grupo
     */
    public void setGrupo(Integer grupo)
    {
        this.grupo = grupo;
    }

    /**
     * Retorna la variable nombreCom
     * 
     * @return nombreCom
     */
    public String getNombreCom()
    {
        return nombreCom;
    }

    /**
     * Asigna la variable nombreCom
     * 
     * @param nombreCom
     * Variable a asignar en nombreCom
     */
    public void setNombreCom(String nombreCom)
    {
        this.nombreCom = nombreCom;

    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    /**
     * Retorna la variable codigoCompaniaP
     * 
     * @return codigoCompaniaP
     */
    public String getCodigoCompaniaP()
    {
        return codigoCompaniaP;
    }

    /**
     * Asigna la variable codigoCompaniaP
     * 
     * @param codigoCompaniaP
     * Variable a asignar en codigoCompaniaP
     */
    public void setCodigoCompaniaP(String codigoCompaniaP)
    {
        this.codigoCompaniaP = codigoCompaniaP;
    }

    /**
     * Retorna la variable fechaInicialP
     * 
     * @return fechaInicialP
     */
    public String getFechaInicialP()
    {
        return fechaInicialP;
    }

    /**
     * Asigna la variable fechaInicialP
     * 
     * @param fechaInicialP
     * Variable a asignar en fechaInicialP
     */
    public void setFechaInicialP(String fechaInicialP)
    {
        this.fechaInicialP = fechaInicialP;
    }

    /**
     * Retorna la variable fechaFinalP
     * 
     * @return fechaFinalP
     */
    public String getFechaFinalP()
    {
        return fechaFinalP;
    }

    /**
     * Asigna la variable fechaFinalP
     * 
     * @param fechaFinalP
     * Variable a asignar en fechaFinalP
     */
    public void setFechaFinalP(String fechaFinalP)
    {
        this.fechaFinalP = fechaFinalP;
    }

    /**
     * Retorna la variable grupoP
     * 
     * @return grupoP
     */
    public String getGrupoP()
    {
        return grupoP;
    }

    /**
     * Asigna la variable grupoP
     * 
     * @param grupoP
     * Variable a asignar en grupoP
     */
    public void setGrupoP(String grupoP)
    {
        this.grupoP = grupoP;
    }

    /**
     * Retorna la variable nombreComP
     * 
     * @return nombreComP
     */
    public String getNombreComP()
    {
        return nombreComP;
    }

    /**
     * Asigna la variable nombreComP
     * 
     * @param nombreComP
     * Variable a asignar en nombreComP
     */
    public void setNombreComP(String nombreComP)
    {
        this.nombreComP = nombreComP;
    }

    /**
     * Retorna la variable opcionP
     * 
     * @return opcionP
     */
    public String getOpcionP()
    {
        return opcionP;
    }

    /**
     * Asigna la variable opcionP
     * 
     * @param opcionP
     * Variable a asignar en opcionP
     */
    public void setOpcionP(String opcionP)
    {
        this.opcionP = opcionP;
    }

    /**
     * Retorna la variable parUspec
     * 
     * @return parUspec
     */
    public String getParUspec()
    {
        return parUspec;
    }

    /**
     * Asigna la variable parUspec
     * 
     * @param parUspec
     * Variable a asignar en parUspec
     */
    public void setParUspec(String parUspec)
    {
    	this.parUspec = parUspec;
    }
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto valores
     * 
     * @return valores
     */
    public Object[] getValores()
    {
        return valores;
    }

    /**
     * Asigna el objeto valores
     * 
     * @param valores
     * Objeto a asignar en valores
     */
    public void setValores(Object[] valores)
    {
        this.valores = valores;
    }
    // </SET_GET_ADICIONALES>

    /**
     * @return the mostrarBoton
     */
    public boolean isMostrarBoton()
    {
        return mostrarBoton;
    }

    /**
     * @param mostrarBoton
     * the mostrarBoton to set
     */
    public void setMostrarBoton(boolean mostrarBoton)
    {
        this.mostrarBoton = mostrarBoton;
    }

    public boolean isSinConsumoCont()
    {
        return sinConsumoCont;
    }

    public void setSinConsumoCont(boolean sinConsumoCont)
    {
        this.sinConsumoCont = sinConsumoCont;
    }
    
    //
    
    public boolean isexcelpl()
    {
        return excelpl;
    }

    public void setexcelpl(boolean excelpl)
    {
        this.excelpl = excelpl;
    }

}
