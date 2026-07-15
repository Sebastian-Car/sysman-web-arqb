package com.sysman.almacen;


import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.ejb.EjbAlmacenDosRemote;
import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.enums.ListadoGeneralPlacasEnum;
import com.sysman.almacen.enums.MovimientosControladorEnum;
import com.sysman.almacen.enums.MovimientosControladorUrlEnum;
import com.sysman.beanbase.AbstractBeanBaseAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SolicitudDisponibilidadControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.enums.APIAutoServicioEnum;

import co.com.sysman.comun.excepcion.NegocioExcepcion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
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
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import sysman.util.consumo.AuditoriaProcesador2;
import sysman.util.consumo.IAuditoria;
import sysman.util.consumo.enums.EnumAccionAuditoria;
import sysman.util.consumo.enums.EnumAcciones;
import sysman.util.consumo.pojo.PojoAuditoria;

/**
 * Formulario para la administracion de los movimientos de
 * almacen.
 *
 * @author sdaza 
 * @version 1, 28/01/2016
 *
 * @author sdaza
 * @version 2, 29/03/2017 Se verifican alertas de sonar
 *
 * @author jrodrigueza
 * @version 3, 10/05/2017 Proceso de refactoring.
 *
 * @author lcortes
 * @version 3, 24,28/08/2017. Se agregan parametros al metodo
 * imprimirMovimiento para genear los reportes de los tipos de
 * movimiento TDR y CDC.
 * 
 * @author crodriguez
 * @version 17/02/2018. Se realizo el cambio al momento de seleccionar
 * el nuevo detalle para obtener la especificacion segun la tabla
 * devolutivo.
 * 
 * @author amonroy
 * @version 12/04/2018. Se adiciona el envio del indicador de registro
 * <code>IND_REG</code> en cero (0) en el metodo
 * editarRegSubSubdmovimiento()
 * 
 * @author asana
 * @version 22/08/2018, se cargan los campos VIDAUTILPLACANIIF y
 * VIDAUTILPLACA en el subdetalle
 * 
 * @author asana, 23/08/2018 1. Se cmbian combs VIDAUTILPLACANIIF y
 * VIDAUTILPLACA a campos diligenciandosen al momento de seleccionar
 * el elemento en el subdetalle se consulta los meses de vida util del
 * tipo_activo configurado en Archivo/Inventario (elemento) 2. Al
 * crear un subdetalle se carga el campos AplicaNIIF por defecto true.
 * 3. Al PCK_ALMACEN.FC_GrabaDevolutivos, se agregan campos
 * VIDAUTILPLACANIIF y VIDAUTILPLACA, NIIF_VALOR_BASE, NIIF_VALORBASE
 * al crear el registro en la tabla DEVOLUTIVO
 * 
 * @author asana, 24/10/2018 1. Se agrega variable verDocumentos para
 * controlador se muestre boton Documentos en caso de los movimientos
 * de Correcciones de valor.
 * 
 * @author gfigueredo
 * @version 3.1
 * Se ajusta la funci�n {@link #getValorUnitarioConsumo(Object)}, para que no se redondee el valor unitario a
 * menos que se configure por parametro
 * @see #getValorUnitarioConsumo(Object)
 * 
 */
@ManagedBean
@ViewScoped
public class MovimientosControlador extends BeanBaseDatosAcmeImpl implements IAuditoria{

    /**
     * constante en la cual se almacena el codigo de la entidad con lo
     * que se logea el usuario
     */
    private final String compania;
    /**
     * atributo en el cual se almacena el NIT de la entidad con la se
     * logea el usuario
     */
    private String companiaNit;
    /**
     * atributo en el cual se almacena el codigo del modulo al cual
     * ingresa el usuario
     */
    private String modulo;
    /**
     * atributo usado para almacenar el archivo a descargar de la
     * pagina (reporte, excel o texto)
     */
    private StreamedContent archivoDescarga;
    /**
     * atributo en el cual se almacena el nuevo numero que se
     * asiganara la movimiento de almacen
     */
    private String nuevoNumero;
    /**
     * registro con el cual se podra tener acceso a los datos de los
     * detalles del movimiento
     */
    private Registro registroSub;
    /**
     * registro auxiliar para diferentes operaciones dentro del
     * controlador
     */
    Registro registroAux;
    
    private List<Registro> listaEstado;
    private List<Registro> listaNumero;
    private List<Registro> listaTipoMovAsociado1;
    private List<Registro> listaDependenciaAnt;
    private List<Registro> listaCmbDependencia;
    private RegistroDataModelImpl listaTipoActivoNiif;
    private RegistroDataModelImpl listaTipoActivoNiifE;
    private RegistroDataModelImpl listavidaUtilPlacaNiif;
    private RegistroDataModelImpl listavidaUtilPlacaNiifE;
    private RegistroDataModelImpl listaVidaUtilPlaca;
    private RegistroDataModelImpl listaVidaUtilPlacaE;
    private RegistroDataModelImpl listaAuxiliarSub;
    private RegistroDataModelImpl listaSubdmovimiento;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private RegistroDataModelImpl listaElementoLote;   
	private RegistroDataModelImpl listaElementoLoteE;
    private RegistroDataModelImpl listaSerie;
    private RegistroDataModelImpl listaSerieE;

    private String auxiliar;
    private RegistroDataModelImpl listaMovAsociado;
    private RegistroDataModelImpl listaMovAsociadoComp;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaResponsableOrigen;
    private RegistroDataModelImpl listaResponsableDestino;
    private RegistroDataModelImpl listaBodegaOrigen;
    private RegistroDataModelImpl listaBodegaDestino;
    private RegistroDataModelImpl listaCentroDeCosto;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaCmbProveedor;
    private RegistroDataModelImpl listaRecibidoPor;
    private RegistroDataModelImpl listacomponente;
    private RegistroDataModelImpl listacomponenteE;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaFuenteRecurso;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaElementoIdi;
    private RegistroDataModelImpl listaElementoIdiE;
    private RegistroDataModelImpl listaUnidad;
    private RegistroDataModelImpl listaUnidadE;
    private RegistroDataModelImpl listaUbicacion;
    private RegistroDataModelImpl listaUbicacionOrigen;
	private RegistroDataModelImpl listaUbicacionDestino;
    private RegistroDataModelImpl listaFuenteRecursos;
    private RegistroDataModelImpl listaFuenteRecursosE;
    private RegistroDataModelImpl listaReferenciaCnt;
    private RegistroDataModelImpl listaReferenciaCntE;
    private RegistroDataModelImpl listaAuxiliarDet;
    private RegistroDataModelImpl listaAuxiliarDetE;
    private RegistroDataModelImpl listaProyectoDet;
    private RegistroDataModelImpl listaProyectoDetE;
    private RegistroDataModelImpl listaCentroCostoDet;
    private RegistroDataModelImpl listaCentroCostoDetE;
    private RegistroDataModelImpl listaElementoAux;
    private RegistroDataModelImpl listaElementoAuxE;
    private RegistroDataModelImpl listaPlantillaDocx;
    
    private ContenedorArchivo contArchivoInventarioVencimiento;

    /**
     * atributo en el cual se almacena el nombre del responsable
     * origen para visualizarlo en la pagina
     */
    private String nomRespOrigen;
    /**
     * atributo en el cual se almacena el nombre del tipo de
     * movimiento asociado para visualizarlo en la pagina
     */
    private String nomTipoAsociado;
    /**
     * atributo en el cual se almacena el nombre del centro de costo
     * para visualizarlo en la pagina
     */
    private String nomCentroCosto;
    /**
     * atributo en el cual se almacena el valor calculado con ajuste
     * al centavo
     */
    private String vlrAjusteCentavos;
    /**
     * atributo en el cual se almacena el nombre del responsable
     * destino para visualizarlo en la pagina
     */
    private String nomRespDestino;
    /**
     * atributo en el cual se almacena el nombre del tercero para
     * visualizarlo en la pagina
     */
    private String nomTercero;
    /**
     * atributo en el cual se almacena el nombre del elemento para
     * visualizarlo en la pagina
     */
    private String nomElemento;
    /**
     * atributo en el cual se almacena el valor total del movimiento
     * sin redondeo
     */
    private String txtTotalSinRedo;
    /**
     * atributo en el cual se almacena el vaor total con ajuste
     */
    private String totalConAjuste;
    /**
     * atributo en el cual se almacena el nombre largo
     */
    private String nombreLargo;
    /**
     * atributo en el cual se almacena el valor del sub total
     */
    private String subtotal;
    /**
     * atributo en el cual se almacena el valor ajustado con redondeo
     */
    private String vlrAjusteRedondeo;
    /**
     * atributo en el cual se almacena el valor del ajuste en centavos
     */
    private String ajusteCentavos;
    /**
     * atributo en el cual se almacena la fecha inicial del periodo
     */
    private Date primerFechaPer;
    /**
     * atributo en el cual se almacena la fecha final del periodo
     */
    private Date ultimoFechaPer;
    /**
     * atributo en el cual se almacena el titulo del formulario
     */
    private String tituloForm;
    /**
     * atributo en el cual se almacena el nombre de la bodega origen
     * para visualizarlo en la pagina
     */
    private String nomBodOrigen;
    /**
     * atributo en el cual se almacena el nombre de la bodega destino
     * para visualizarlo en la pagina
     */
    private String nomBodDestino;
    /**
     * atributo en el cual se almacena el nombre del proveedor para
     * visualizarlo en la pagina
     */
    private String nomProveedor;
    /**
     * atributo en el cual se almacena indicador para establecer si se
     * bloquea a no el campo para escoger la serie del elemento
     */
    private boolean bloqSerie;

    private boolean verUbicacion;

    private boolean verComponente;

    private boolean verDocumentos;

    private boolean bloqueadoPeriodo;

    /**
     * Almacena el nombre del tercero seleccionado en el combo del
     * campo Recibido Por.
     */
    private String nomRecibidoPor;
    /**
     * atributo en el cual se almacena el nombre del auxiliar
     */
    private String nomAuxiliar;
    /**
     * atributo en el cual se almacena el valor booleando para
     * permitir o no modificar datos del movimiento
     */
    private boolean permiteModificar = true;
    /**
     * atributo en el cual se almacena el valor configurado en
     * parametros para modificar movimientos anteriores
     */
    private String parPermiteMovAntAlm;
    /**
     * atributo en el cual se almacena la fecha del ultimo movimiento
     * para realizar validaciones
     */
    private Date ultFechaMov;
    /**
     * atributo en el cual se almacena el valor booleano que indica si
     * el movimiento tiene o no detalles
     */
    private boolean tieneDetalles = false;
    /**
     * atributo en el cual se almacena el tipo de documento
     */
    private String tipoDocumento;
    /**
     * atributo en el cual se almacena el valor booleano para bloquer
     * o no el tipo de movimiento
     */
    private boolean tipoDocumentoBloq;
    /**
     * atributo en el cual se almacena el valor para la etiqueta de
     * observaciones
     */
    private String etiquetaObs;
    /**
     * atributo en el cual se almacena el numero del movimiento
     */
    private long nroMovimiento;
    /**
     * atributo en el cual se almacena el valor booleano para indicar
     * si el campo cantidad debe o no estar bloqueado
     */
    private boolean cantBloq;
    /**
     * atributo en el cual se almacena el valor booleano para indicar
     * si el campo valor unitario debe o no estar bloqueado
     */
    private boolean vlrUnitBloq;
    /**
     * atributo en el cual se almacena el valor booleano para indicar
     * si el campo serie devolutivo debe o no estar bloqueado
     */
    private boolean serieDevBloq;
    /**
     * atributo en el cual se almacena el valor booleano para indicar
     * si el campo marca debe o no estar bloqueado
     */
    private boolean marcaBloq;
    /**
     * atributo en el cual se almacena el valor booleano para indicar
     * si el campo serie debe o no estar visible
     */
    private boolean ocultarSerie;

    private boolean validarEstado;

    private boolean camposNiifVisible;

    private boolean indiceSubdmovimiento;
    
    private boolean generaCausacion;
    
    private boolean identificadorUnico;
    /**
     * atributo en el cual se almacena el valor configurado para un
     * formato
     */
    private String formatoNombre;
    /**
     * atributo en el cual se almacena un indicador para establecer si
     * se esta insertando un detalle para realizar validaciones
     */
    private int insertandoDetalle;
    /**
     * Indicador para hacer visible los componentes graficos asociados
     * al campo Recibido por.
     */
    private boolean manejaRecibido;
    /**
     * Indica si se puede ingresar el valor unitario antes de IVA.
     */
    private boolean valorAntesIVA = false;
    /**
     * Permite mostrar el check y el campo para ingresar por valor
     * unitario antes de IVA.
     */
    private boolean mostrarValorAntesIVA;
    private String elemento;
    private int editarMovimiento;
    /**
     * Permite bloquear los botones que se deben inactivar al momento
     * de crear un registro.
     */
    private boolean bloqueado;    
    /**
     * Atributo en el cual se almacena el valor booleano que indica si
     * el movimiento maneja proyecto
     */
    private boolean proyAuxVisible;
    /**
     * Atributo en el cual se almacena el valor booleano que indica si
     * el movimiento maneja rueda y operaciones
     */
    private boolean ruedaOperVisible;
    // parametros de entrada
    
    private String anoMov;
    private String mesMov;
    private String tipoMov;
    private String cptoMov;
    private String claseMov;
    private String tipoElemento;
    private boolean manVtas;
    private String claseDocAsMov;
    private String nombreDocAsociado;
    private boolean generaPlaca;
    private String tipoPersona;
    private boolean pideCCto;
    private boolean invInicial;
    private String nroInicial;
    private String nomFuenteRecurso;
    private String nomReferencia;
    private String nombreConsulta;
    private String nomProyecto;
    private boolean visiblePlantilla;
    private boolean visibleUbicacion;
    private boolean visibleUbicacionTras;


	private String nomUbicacion;
	private String nomUbicacionOrigen;
	private String nomUbicacionDestino;
    private boolean manejaFuente;
    private boolean bloqueaFuente;
    /**
     * Clase de bodega Origen configurada para el tipo de movimiento.
     */
    private String claseBodOrig;
    /**
     * Clase de bodega Destino configurada para el tipo de movimiento.
     */
    private String claseBodDest;
    /**
     * Indica si es obligatiorio seleccionar un tercero para el
     * movimiento. Esto segun la configuracion de la tabla
     * TRANSACCIONES_VALIDAS.
     */
    private boolean obligaTercero;
    /**
     * Indica si es obligatiorio seleccionar una bodega origen para el
     * movimiento. Esto segun la configuracion de la tabla
     * TRANSACCIONES_VALIDAS.
     */
    private boolean obligaOrigen;
    /**
     * Indica si es obligatiorio seleccionar una bodega destino para
     * el movimiento. Esto segun la configuracion de la tabla
     * TRANSACCIONES_VALIDAS.
     */
    private boolean obligaDestino;
    /**
     * Indica si es obligatiorio seleccionar un proveedor para el
     * movimiento y documento asociado. Esto segun la configuracion de
     * la tabla TRANSACCIONES_VALIDAS.
     */
    private boolean obligaProveedor;
    private boolean igualarOrigDest;
    private boolean visibleAdicionales;
    /**
     * Atributo que almacena la existencia en inventario (cantidad) de
     * un elemento seleccionado
     */
    private double existenciaElemento;
    /**
     * Atributo que almacena el valor del campo <b>CANTIDAD</b> antes
     * de ser modificado en un registro del suformulario Detalle de
     * Movimiento
     */
    private double cantidadAnterior;

    private String componenteInicial;

    private String estadoAlmacen;

    private String estado;

    private String copiarSalidas;

    private boolean visibleCopiar;

    private boolean activoCopiar;

    private boolean ocultarEliminarLote;

    private boolean obligaCampos;

    private static final String CLASEBODEGASERIE = "('10','80')";
    private static final String CAMPO_CODIGO = "CODIGO";
    private static final String CAMPO_NUMERO = "NUMERO";
    private static final String CAMPO_DEPDESTINO = "DEPENDENCIA_DESTINO";
    private static final String CAMPO_PROVEEDORCA = "PROVEEDORCA";
    private static final String CAMPO_RESDESTINO = "RESPONSABLE_DESTINO";
    private static final String CAMPO_SUCRESDESTINO = "SUCURSAL_RESDESTINO";
    private static final String CAMPO_SUCRECIBIDO = "SUCURSAL_RECIBIDO";
    private static final String CAMPO_NITRECIBIDO = "NIT_RECIBIDO";
    private static final String CAMPO_CCTO = "CENTRODECOSTO";
    private static final String CAMPO_SERIE = "SERIE";
    private static final String CAMPO_ELEMENTO = "ELEMENTO";
    private static final String CAMPO_MOVIMIENTO = "MOVIMIENTO";
    private static final String TEXTO_SINNOMBRE = "SIN NOMBRE";
    private static final String CAMPO_MARCA = "MARCA";
    private static final String CAMPO_MODELO = "MODELO";
    private static final String CAMPO_SERIEDEV = "SERIEDEVOLUTIVO";
    private static final String CAMPO_TIPOMOV = "TIPOMOVIMIENTO";
    private static final String CAMPO_DEPORIGEN = "DEPENDENCIA_ORIGEN";
    private static final String CAMPO_RESORIGEN = "RESPONSABLE_ORIGEN";
    private static final String CAMPO_SUCRESORIGEN = "SUCURSAL_RESORIGEN";
    private static final String CAMPO_COMPANIA = "COMPANIA";
    private static final String CAMPO_AUXILIAR = "AUXILIAR";
    private static final String CAMPO_FECHA = "FECHA";
    private static final String CAMPO_NOMLARGO = "NOMBRELARGO";
    private static final String CAMPO_SUCURSAL = "SUCURSAL";
    private static final String CAMPO_SALDOCANT = "SALDOCANT";
    private static final String CAMPO_VALORUNITARIO = "VALORUNITARIO";
    private static final String CAMPO_PORCDESCUENTO = "PORCDESCUENTO";
    private static final String CAMPO_PORCIVA = "PORCIVA";
    private static final String CAMPO_PORCIMPCONSUMO = "PORC_IMPCONSUMO";
    private static final String CAMPO_VLRIMPCONSUMO = "VLRIMPCONSUMO";
    private static final String CAMPO_VALORTOTAL = "VALORTOTAL";
    private static final String CAMPO_SALDOELEMENTO = "SALDOELEMENTO";
    private static final String CAMPO_VALORSALDO = "VALORSALDO";
    private static final String CAMPO_VLRUNITARIOPROM = "VLRUNITARIOPROM";
    private static final String CAMPO_IND_REG = "IND_REG";
    private static final String CAMPO_VALORANTERIOR = "VALORANTERIOR";
    private static final String CAMPO_CANTANTERIOR = "CANTANTERIOR";
    private static final String CAMPO_VLRUNITANTERIOR = "VLRUNITANTERIOR";
    private static final String CAMPO_VLRAJUSTADO = "VLRAJUSTADO";
    private static final String CAMPO_SALDOKARDEX = "SALDOKARDEX";
    private static final String CAMPO_INCREMENAJUS = "INCREMENAJUS";
    private static final String CAMPO_PEDIDO = "PEDIDO";
    private static final String CAMPO_COSTOSALIDA = "COSTOSALIDA";
    private static final String CAMPO_COSTOSALIDAAJ = "COSTOSALIDAAJ";
    private static final String CAMPO_AJUSTECENTAVOS = "AJUSTECENTAVOS";
    private static final String CAMPO_AJUSTECENTAVOS1 = "AJUSTECENTAVOS1";
    private static final String CAMPO_CANTIDADDOCAS = "CANTIDADDOCAS";
    private static final String CAMPO_VALORDOCAS = "VALORDOCAS";
    private static final String CAMPO_INFINMUEBLE = "INFINMUEBLE";
    private static final String CAMPO_PLACAANTERIOR = "PLACAANTERIOR";
    private static final String CAMPO_REGISTRADOBI = "REGISTRADOBI";
    private static final String CAMPO_VALORBASE = "VALORBASE";
    private static final String CAMPO_VALORIVA = "VALORIVA";
    private static final String CAMPO_PORCIVAUNI = "PORCIVAUNI";
    private static final String CAMPO_VALORTOTALCONIVA = "VALORTOTALCONIVA";
    private static final String CAMPO_VLRUNITARIO_ANTESIVA = "VLRUNITARIO_ANTESIVA";
    private static final String CAMPO_REGISTRADO = "REGISTRADO";
    private static final String CAMPO_VLRUNITARIO_SINIVA = "VLRUNITARIO_SINIVA";
    private static final String CAMPO_SUCURSALCA = "SUCURSALCA";
    private static final String CAMPO_CLASE_BODDESTINO = "CLASE_BODEGA_DESTINO";
    private static final String CAMPO_CLASE_BODORIGEN = "CLASE_BODEGA_ORIGEN";
    private static final String CAMPO_BODEGAORIGEN = "BODEGA_ORIGEN";
    private static final String CAMPO_BODEGADESTINO = "BODEGA_DESTINO";
    private static final String CAMPO_CLASEBOD = "CLASE_BODEGA";
    private static final String CAMPO_TERCERO = "TERCERO";
    private static final String CAMPO_NOMBRE = "NOMBRE";
    private static final String CAMPO_NOMBRERES = "NOMRESPONSABLE";
    private static final String CAMPO_CANTIDAD = "CANTIDAD";
    private static final String CAMPO_CEDULA = "CEDULA";
    private static final String CAMPO_CODELEMENTO = "CODIGOELEMENTO";
    private static final String COD_ELEMENTO = "COD_ELEMENTO";
    private static final String CAMPO_RUEDA = "RUEDA";
    private static final String CAMPO_NOMBREPROYECTO = "NOMBREPROYECTO";
    private static final String CAMPO_OPERACION = "OPERACION";
    private static final String CAMPO_ESPECIFICACION = "DESCRIPCION";
    private static final String CONSECUTIVO_ORFEO = "ORFEO";
    private static final String CAMPO_VLR_COLGAAP = "VLR_COLGAAP";
    private static final String VALOR = "VALOR";
    private static final String PARAMETRO_TIPO_MOV = "claseOrden";
    private static final String PARAMETRO_NUMERO_MOV = "numeroOrden";



    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;
    @EJB
    private EjbAlmacenUnoRemote ejbAlmacenUno;
    @EJB
    private EjbAlmacenDosRemote ejbAlmacenDos;

    @EJB
    private EjbAlmacenCincoRemote almacenCincoRemote; 
    
    @EJB
    private EjbContabilizarCeroGeneralRemote ejbContabilizarCero;
    
    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCero; //JM 24/01/2025
    
    /**
     * Bloqueo de los combos que permiten la selecci&oacute;n de la
     * dependencia origen y destino.
     */
    private boolean bloqueaDependencia;
    /**
     * Texto que puede a tomar la etiqueta del campo tercero.
     */
    private String etiquetaTercero;
    private String manTraslados;
    private String predecesor;
    private String manPredecesor;
    private String consecutivoOrfeo;
    private boolean manOrfeo;
    private boolean verElemento;
    private String manElementoPepsIdi;
    private boolean verElementoIdi;
    private boolean verElementoLote;
    private String manFuenteIn;
	private String accionAuditoria;
    private int detalleMov;
    private boolean cargarPlacaNiif;
    private String colgaapNiif;
    private String entAplicaNiif;
    private String manNiifAlmacen;
    private String manAuditoriaAlm;
    private String manAuxiliares;
    private boolean verElementoAux;

    //Procesos Auditoria 
    private Map<String, Object> valAnterior = new HashMap<>();
    private Map<String, Object> valActual = new HashMap<>();
	private boolean pideVencimiento;
	private String manejaUbicacion;
	private boolean excluyeConsumo;
	private boolean excluyeDifConsumo;
	private boolean desagregrarElementos;
	private boolean cargaMovAsociado;
	private boolean cargaMovAsociadoComp;
	
	private double cantAnterior = 0;
	private String codigoAfect;
	private boolean verPorcValorResidual = true;
	private boolean bloqResidual = true;
	private int digPorcValorResidual;
	private boolean bloqFuenteMov;
	private boolean bloqRefMov;
	private boolean bloqAuxMov;
	private boolean bloqProyMov;
	private boolean bloqCCMov;
	private boolean cargaFuente;
	private boolean cargaReferCnt;
	private boolean cargaAuxDet;
	private boolean cargaProyDet;
	private boolean cargaCCDet;
	private boolean cargaLoteDet;
	private boolean bloqueaAux;
    private boolean bloqueaAuxCont;
    private boolean bloqInterfaz;
    private String tipoDocAsociado;
    private String nroDocAsociado;
    private String proyecto; 
    private String fuenteR;
    private String referencia;  
    private String auxiliarD;
    private String centroCosto;
    private String operacion;
    private String auxAlmacen;
    private String rueda;
    private String msg;
    private boolean manvlrdefcodproy = false; //JM 11/10/2024 
    private int redondeoVlr; //JM 19/11/2024
	private boolean manejaImpconsumo;
	/**
     * Indica si se puede ingresar el valor unitario antes de IVA.
     */
   // private boolean ValorAIU = false;


	/**
     * Permite mostrar el check y el campo para ingresar por valor
     * unitario antes de IVA.
     */
    private boolean visibleAIU= false;
    
	private boolean activoAIU= false; 
	
	private boolean manejaAIU;
	
	
	private boolean ckAIU;
	
	private static final String CAMPO_PORCAIU = "PORC_AIU";
	 private static final String CAMPO_VLRAIU = "VLR_AIU";
	
	private double  administracion;
	

	private double imprevistos;
	
	private double utilidades;
	

	private double porcentajeAIU;
	

	
	private Map<String, Object> parametroswf;
	private String topTotalIva;
	private String topTotalAjuste;
	private String topTotalImpConsumo;
	private String topIva;
	private String plantilla;
	private boolean visibleListaPlantillas;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	private boolean visiblePresentarPlantillas;
	private boolean bloqueaNiif;
	private boolean contratoManual;
	
   

	@SuppressWarnings("unchecked")
	public MovimientosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "10");
        	}
        	
            modulo = SessionUtil.getModulo();
        	companiaNit = SessionUtil.getCompaniaIngreso().getNit();
        	numFormulario = GeneralCodigoFormaEnum.MOVIMIENTOS_CONTROLADOR
        			.getCodigo();
        	validarPermisos();
        	registroSub = new Registro(new HashMap<String, Object>());
        	contArchivoInventarioVencimiento = new ContenedorArchivo();
        	validarEstado = false;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        if (SessionUtil.getFlash() == null) {
            SessionUtil.redireccionarMenu();
        }

    }

    @PostConstruct
    public void inicializar() {
        traerParametrosEntrada();
        parPermiteMovAntAlm = getParametro(
                        "PERMITE MOVIMIENTOS ANTERIORES ALMACEN", "NO");
        copiarSalidas = getParametro(
                        "MOSTRAR BOTON COPIAR DE EN SALIDAS DE CONSUMO", "NO");
        manTraslados = getParametro(
                        "MANEJA TRASLADOS ENTRE BODEGAS DE CONSUMO", "NO");
        manPredecesor = getParametro(
                        "MANEJA PEDECESOR DE DEPENDENCIAS EN MOVIMIENTOS DE ALMACEN",
                        "NO");
        manElementoPepsIdi = getParametro(
		        		"MANEJA PEPS CONSUMO DE ALMACEN IDIPRON",
		        		"NO");
        manFuenteIn  = getParametro(
        		"MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN",
        		"NO");
        colgaapNiif = getParametro("EJECUTA COLGAAP y NIIF","NO");
        entAplicaNiif = getParametro("ENTIDAD APLICA NIIF", "NO");
        manNiifAlmacen = getParametro("MANEJA NIIF EN ALMACEN", "NO");
        
        manAuditoriaAlm = getParametro(
        				"MANEJA AUDITORIA EN EL MODULO DE ALMACEN",
        				"NO");
        
        manejaFuente = getParametro(
        		"MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN",
        		"NO").equals("SI");
        
        verPorcValorResidual = getParametro(
        		"MANEJA VALOR RESIDUAL POR AGRUPACION EN ALMACEN",
        		"NO").equals("SI");
        
    	digPorcValorResidual = Integer.parseInt(
    								getParametro(
    										"DIGITOS DE AGRUPACION PARA VALOR RESIDUAL EN ALMACEN",
    										"0")
    								);
    	
    	Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),tipoMov);
		List<Registro> aux;
		try {
			aux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											MovimientosControladorUrlEnum.URL139033
											.getValue())
									.getUrl(),
									param));


			String aux2 = aux.get(0).getCampos().get(VALOR).toString();

			if( getParametro(
					"MANEJA VALOR RESIDUAL POR PORCENTAJE EN NIIF",
					"NO").equals("SI") && !("0").equals(aux2) ) {
				bloqResidual =  true;
			}else if (("N".equals(tipoElemento) || "D".equals(tipoElemento)
					|| "M".equals(tipoElemento) || "E".equals(tipoElemento))
					&& !generaPlaca
					&& !"CR".equals(cptoMov)) {
				bloqResidual =  true;
			} else {
				bloqResidual =  false;
			}
		} catch (SystemException e1) {
			e1.printStackTrace();
		}
    	manAuxiliares = getParametro(
        		"MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN",
        		"NO");
    	
    	redondeoVlr = Integer.parseInt(getParametro("REDONDEO VALOR", "2"));
    	
    	manejaImpconsumo = (getParametro(
                "APLICAR IMPOCONSUMO EN ENTRADAS DE ALMACEN", "NO").equals("SI"))?true:false;
    	
    	manejaAIU = (getParametro(
                "APLICAR AIU EN ENTRADAS DE ALMACEN", "NO").equals("SI"))?true:false; 
    	
    	activoAIU = false;
        enumBase = GenericUrlEnum.MOVIMIENTO;
        buscarLlave();
        asignarOrigenDatos();
        
        try 
        {
        	generaCausacion = "SI".equals(ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA",
    				"1", new Date(), true));
        	
        	manvlrdefcodproy = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "VALOR POR DEFECTO EN CODIGO DE PROYECTO PARA MOVIMIENTOS", SessionUtil.getModulo(), new Date(), true), "NO").equals("SI") ? true: false;
        	
        	if("C".equals(tipoElemento)) {
        		visibleAdicionales = false;
        		excluyeConsumo = false;
        	} else {
        		visibleAdicionales = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
 					   "MANEJA CAMPOS ADICIONALES EN BIENES INMUEBLES", SessionUtil.getModulo(), new Date(), true), "NO").equals("SI") ? true: false;
        		excluyeConsumo = true;
        	}
			
			visiblePlantilla = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					   "MANEJA INVENTARIO CON VENCIMIENTOS", SessionUtil.getModulo(), new Date(), true), "NO").equals("SI") ? true: false;
			
			identificadorUnico = "SI".equals(getParametro("MANEJA IDENTIFICADOR UNICO EN ALMACEN", "NO")) ? true: false;
		} 
        catch (SystemException e) 
        {
			e.printStackTrace();
		}
        
        if(manElementoPepsIdi.equals("SI") 
        		&& !("E".equals(claseMov)) 
        		&& "C".equals(tipoElemento))
        {
        	verElemento = false;
        	verElementoIdi = true;
        }
        else
        {
        	verElemento = true;
        	verElementoIdi = false;
        }
        if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas y  elementos de consumo 
     	    verElemento = false;
     	    verElementoIdi = false;
     	    verElementoLote = true;
         }else {
        	verElementoLote = false;
         }
        if (claseMov.equals("E") 
        		&& (tipoElemento.equals("D") 
        				|| tipoElemento.equals("N") 
        				|| tipoElemento.equals("M") 
        				|| tipoElemento.equals("E"))
        		&& colgaapNiif.equals("NO")
        		&& entAplicaNiif.equals("NO")
        		&& manNiifAlmacen.equals("NO")) {
        	cargarPlacaNiif = true;
        } else {
        	cargarPlacaNiif = false;
        }
        
        if (!("C".equals(tipoElemento))) {
    		excluyeDifConsumo = false;
    	} else {
    		excluyeDifConsumo = true;
    	}
        
        if (claseMov.equals("E") && "C".equals(cptoMov)) {
        	cargaMovAsociadoComp = true;
        	cargaMovAsociado = false;
        	if(manejaAIU) {
        		visibleAIU = true;
        	}
        } else {
        	cargaMovAsociadoComp = false;
        	cargaMovAsociado = true;
        }
        
        bloqueaAuxCont = manAuxiliares.equals("SI");
        
        if(manAuxiliares.equals("SI") && manFuenteIn.equals("NO") && ("S".equals(claseMov) || "TB".equals(cptoMov))) {
        	verElemento = false;
     	    verElementoIdi = false;
     	    verElementoLote = false;
     	    verElementoAux = true;
        } else {
        	verElementoAux = false;
        }
        
        if (manejaImpconsumo && visibleAIU) {
        	topTotalIva = "735px";
        	topTotalAjuste = "759px";
        	topTotalImpConsumo = "710px";
        	topIva = "685px";
        }else if (manejaImpconsumo) {
        	topTotalIva = "710px";
        	topTotalAjuste = "735px";
        	topTotalImpConsumo = "685px";
        	topIva = "661px";
        }
        else {
        	topTotalIva = "685px";
        	topTotalAjuste = "710px";
        	topIva = "661px";
        }
    }

    /**
     * Trae parametros enviados por flash.
     */
    private void traerParametrosEntrada() {
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            nombreDocAsociado = extraerString(
                            parametrosEntrada.get("nombreDocAsociado"));

            anoMov = extraerString(parametrosEntrada.get("anoMov"));
            mesMov = extraerString(parametrosEntrada.get("mesMov"));
            tipoMov = extraerString(parametrosEntrada.get("tipoMov"));
            tipoElemento = extraerString(
                            parametrosEntrada.get("tipoElementoMov"));
            claseMov = extraerString(parametrosEntrada.get("claseMov"));
            claseDocAsMov = String
                            .valueOf(parametrosEntrada.get("claseDocAsMov"));
            try {
                primerFechaPer = SysmanFunciones
                                .convertirAFecha("01/" + mesMov + "/" + anoMov);
                ultimoFechaPer = SysmanFunciones
                                .ultimoDiaDate(SysmanFunciones.convertirAFecha(
                                                "01/" + mesMov + "/" + anoMov));
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            tituloForm = extraerString(parametrosEntrada.get("tituloForm"));
            cptoMov = extraerString(parametrosEntrada.get("cptoMov"));
            tipoPersona = extraerString(parametrosEntrada.get("tipoPersona"));
            manVtas = (boolean) parametrosEntrada.get("manVtas");
            generaPlaca = (boolean) parametrosEntrada.get("generaPlaca");
            pideCCto = (boolean) parametrosEntrada.get("pideCCto");
            invInicial = (boolean) parametrosEntrada.get("inventarioInicial");
            nroInicial = extraerString(parametrosEntrada.get("nroInicial"));
            claseBodOrig = String
                            .valueOf(parametrosEntrada.get("claseBodOrig"));
            claseBodDest = String
                            .valueOf(parametrosEntrada.get("claseBodDest"));
            // parametro sin usar: claseBodDest
            obligaOrigen = (boolean) parametrosEntrada.get("obligaOrigen");
            obligaDestino = (boolean) parametrosEntrada.get("obligaDestino");
            obligaTercero = (boolean) parametrosEntrada.get("obligaTercero");
            obligaProveedor = (boolean) parametrosEntrada
                            .get("obligaProveedor");
            igualarOrigDest = (boolean) parametrosEntrada
                            .get("igualarOrigDest");

            obligaCampos = (boolean) parametrosEntrada
                            .get("obligaCampos");
            
            bloqueaAux = (boolean) parametrosEntrada
                          .get("bloqueaAux") ? false : true;
            
            contratoManual = (boolean) parametrosEntrada
                    .get("contratoManual");

            accion = extraerString(parametrosEntrada.get("accion"));
            rid = (Map<String, Object>) parametrosEntrada.get("rid");
        }
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("TIPO", tipoMov);
        parametrosListado.put("FECHA_INICIAL", primerFechaPer);
        parametrosListado.put("FECHA_FINAL", ultimoFechaPer);
    }

    public void cargarListaSubdmovimiento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get(CAMPO_TIPOMOV));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));

        try {
            String urlEnumId = GenericUrlEnum.D_MOVIMIENTO.getGridKey();
            UrlBean urlSelectSub = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);

            String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.D_MOVIMIENTO.getTable());
            listaSubdmovimiento = new RegistroDataModelImpl(
                            urlSelectSub.getUrl(),
                            urlSelectSub.getUrlConteo().getUrl(),
                            param, rowKey);

        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoMovAsociado1() {
        Map<String, Object> param = new TreeMap<>();
        try {
            String urlEnumId = MovimientosControladorUrlEnum.URL5909.getValue();
            listaTipoMovAsociado1 = RegistroConverter
                            .toListRegistro(requestManager
                                            .getList(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            urlEnumId)
                                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarlistaTipoActivoNiif() {

        String urlEnumId = MovimientosControladorUrlEnum.URL1216.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoActivoNiif = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO_TIPOACTIVO");
    }

    private void cargarlistaTipoActivoNiifE() {

        String urlEnumId = MovimientosControladorUrlEnum.URL1216.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoActivoNiifE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO_TIPOACTIVO");
    }

    public void cargarlistacomponenteE() {

        String urlEnumId = MovimientosControladorUrlEnum.URL1219.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacomponenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());
    }

    public void cargarListaDependenciaAnt() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM10.getValue(), claseBodOrig);
        try {
            String urlEnumId = MovimientosControladorUrlEnum.URL6089.getValue();
            listaDependenciaAnt = RegistroConverter
                            .toListRegistro(requestManager
                                            .getList(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            urlEnumId)
                                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbDependencia() {

        boolean esTraspaso = "T".equalsIgnoreCase("T")
            && "T".equalsIgnoreCase("T");

        if (manPredecesor.equals("SI") && esTraspaso) {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(MovimientosControladorEnum.PARAM10.getValue(),
                            claseBodDest);
            param.put(MovimientosControladorEnum.PREDECESOR.getValue(),
                            predecesor);

            try {
                String urlEnumId = MovimientosControladorUrlEnum.URL1417
                                .getValue();
                listaCmbDependencia = RegistroConverter
                                .toListRegistro(requestManager
                                                .getList(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                urlEnumId)
                                                                .getUrl(),
                                                                param));
                if (listaCmbDependencia.isEmpty()) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4345"));
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(MovimientosControladorEnum.PARAM10.getValue(),
                            claseBodDest);

            try {
                String urlEnumId = MovimientosControladorUrlEnum.URL6089
                                .getValue();
                listaCmbDependencia = RegistroConverter
                                .toListRegistro(requestManager
                                                .getList(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                urlEnumId)
                                                                .getUrl(),
                                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void cargarListaEstado() {
        Map<String, Object> param = new TreeMap<>();

        try {
            String urlEnumId = MovimientosControladorUrlEnum.URL1453.getValue();
            listaEstado = RegistroConverter
                            .toListRegistro(requestManager
                                            .getList(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            urlEnumId)
                                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAuxiliar() {
        String urlEnumId = MovimientosControladorUrlEnum.URL6299.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);
    }

    public void cargarListaAuxiliarSub() {
        String urlEnumId = MovimientosControladorUrlEnum.URL6299.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaAuxiliarSub = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);
    }

    public void cargarListaElemento() {

        String urlEnumId;

        if (manTraslados.equals("NO")) {

            urlEnumId = MovimientosControladorUrlEnum.URL6559.getValue();

        }
        else {

            urlEnumId = MovimientosControladorUrlEnum.URL1416.getValue();

        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        boolean esTraspaso = "T".equalsIgnoreCase(cptoMov)
            && "T".equalsIgnoreCase(claseMov);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        param.put(MovimientosControladorEnum.IND_TRASPASO.getValue(),
                        esTraspaso ? -1 : 0);
        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODELEMENTO);

    }
    
    public void cargarListaElementoLote() {

        String urlEnumId;

        urlEnumId = MovimientosControladorUrlEnum.URL112189.getValue();


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        boolean esTraspaso = "T".equalsIgnoreCase(cptoMov)
            && "T".equalsIgnoreCase(claseMov);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        param.put(MovimientosControladorEnum.IND_TRASPASO.getValue(),
                        esTraspaso ? -1 : 0);
        listaElementoLote = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ROWIDCON");

    }
    public void cargarListaElementoLoteE() {

        String urlEnumId;

        urlEnumId = MovimientosControladorUrlEnum.URL112189.getValue();
        
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        boolean esTraspaso = "T".equalsIgnoreCase(cptoMov)
            && "T".equalsIgnoreCase(claseMov);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        param.put(MovimientosControladorEnum.IND_TRASPASO.getValue(),
                        esTraspaso ? -1 : 0);
        listaElementoLoteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ROWIDCON");
    }

    public void cargarListaElementoE() {

        String urlEnumId;

        if (manTraslados.equals("NO")) {

            urlEnumId = MovimientosControladorUrlEnum.URL6559.getValue();

        }
        else {

            urlEnumId = MovimientosControladorUrlEnum.URL1416.getValue();

        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        boolean esTraspaso = "T".equalsIgnoreCase(cptoMov)
            && "T".equalsIgnoreCase(claseMov);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        param.put(MovimientosControladorEnum.IND_TRASPASO.getValue(),
                        esTraspaso ? -1 : 0);
        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODELEMENTO);
    }

    private RegistroDataModelImpl crearListaSerie() {
        if (registro == null) {
            return null;
        }
        String urlEnumId = "";
        Map<String, Object> param = new TreeMap<>();
  
        //JM INI CC 484 
        if(manAuxiliares.equals("SI")) {
        	 urlEnumId = MovimientosControladorUrlEnum.URL112195.getValue();
        	 
             param.put("CAMP_"+GeneralParameterEnum.COMPANIA.getName(), compania);
             param.put("CAMP_"+GeneralParameterEnum.RESPONSABLE.getName(),
                             registro.getCampos().get(CAMPO_RESORIGEN));
             param.put("CAMP_"+GeneralParameterEnum.SUCURSAL_RESPONSABLE.getName(),
                             registro.getCampos().get(CAMPO_SUCRESORIGEN));
             param.put("CAMP_"+GeneralParameterEnum.ELEMENTO.getName(), elemento);
             param.put("CAMP_"+MovimientosControladorEnum.PARAM1.getValue(), claseMov);
        	
    		param.put("CAMP_PROYECTO", registro.getCampos().get("CODIGOPROYECTO"));
    		param.put("CAMP_FUENTER", registro.getCampos().get("FUENTEDERECURSO"));
    		param.put("CAMP_REFERENCIA", registro.getCampos().get("REFERENCIA"));
    		param.put("CAMP_"+CAMPO_AUXILIAR, registro.getCampos().get(CAMPO_AUXILIAR));
    		param.put("CAMP_"+CAMPO_CCTO, registro.getCampos().get(CAMPO_CCTO));
            param.put("CAMP_"+MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
            param.put("CAMP_BODEGA", registro.getCampos().get(CAMPO_BODEGAORIGEN));
            
    		
        }else{
        	 urlEnumId = MovimientosControladorUrlEnum.URL10089.getValue();
        	 
             param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
             param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                             registro.getCampos().get(CAMPO_DEPORIGEN));
             param.put(GeneralParameterEnum.RESPONSABLE.getName(),
                             registro.getCampos().get(CAMPO_RESORIGEN));
             param.put(GeneralParameterEnum.SUCURSAL_RESPONSABLE.getName(),
                             registro.getCampos().get(CAMPO_SUCRESORIGEN));
             param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
             param.put(MovimientosControladorEnum.PARAM1.getValue(), claseMov);
             //MOD JM 1233
             param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
             /*String tipoInventario = "0";
             if ("E".contains(claseMov)) {
                 tipoInventario = tipoElemento;
             }
             else if (!Arrays.asList("S", "D", "T").contains(claseMov)) {
                 tipoInventario = "D";
             }
             param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoInventario); */
        }
        //JM FIN CC 484 
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        
        return new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_SERIE);
    }
    
    public void calcularValoresAIU(Registro reg) {

    	BigDecimal valorAntesIVA = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("VLRUNITARIO_ANTESIVA")),"0"));
    	BigDecimal porcAIU = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("PORC_AIU")),"0"));
    	BigDecimal valorAIU = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("VLR_AIU")),"0"));
    	BigDecimal porcIVA = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("PORCIVA")),"0"));;
    	BigDecimal porcImpConsumo = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("PORC_IMPCONSUMO")),"0"));
    	BigDecimal cantidad = new BigDecimal(SysmanFunciones.nvl(SysmanFunciones.toString(reg.getCampos().get("CANTIDAD")),"0"));
    	Boolean aiu = Boolean.parseBoolean(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("AIU"),false)));

    	if(aiu) {
    		RoundingMode redondeo = RoundingMode.HALF_UP;

    		BigDecimal primerParte = valorAntesIVA
    				.multiply(porcAIU)
    				.divide(BigDecimal.valueOf(100), redondeoVlr, redondeo);

    		BigDecimal segundaParte = primerParte
    				.multiply(porcIVA)
    				.divide(BigDecimal.valueOf(100), redondeoVlr, redondeo);
    		
    		BigDecimal terceraParte = primerParte
    				.multiply(porcImpConsumo)
    				.divide(BigDecimal.valueOf(100), redondeoVlr, redondeo);

    		BigDecimal vlrUnitario = valorAntesIVA
    				.add(primerParte)
    				.add(segundaParte)
    				.add(terceraParte)
    				.setScale(redondeoVlr, redondeo);

    		BigDecimal vlrIva = valorAIU.multiply(porcIVA).divide(BigDecimal.valueOf(100), redondeoVlr, redondeo);

    		BigDecimal vlrImpConsumo = valorAIU.multiply(porcImpConsumo);

    		BigDecimal vlrTotal = vlrUnitario.multiply(cantidad);

    		reg.getCampos().put("VALORUNITARIO", vlrUnitario);
    		reg.getCampos().put("VALORIVA", vlrIva);
    		reg.getCampos().put("VLRIMPCONSUMO", vlrImpConsumo);
    		reg.getCampos().put("VALORTOTAL", vlrTotal);
    		reg.getCampos().put("NIIF_VALOR_TOTAL", vlrTotal);

    	}

    }

    public void cargarListaSerie() {
        listaSerie = crearListaSerie();
    }

    public void cargarListaSerieE() {
        listaSerieE = crearListaSerie();
    }
    
    public void cargarListaUnidad() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientosControladorUrlEnum.URL10280
						.getValue());
		listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null, true,
				"UNIDAD");
	}
    
    public void cargarListaUnidadE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						MovimientosControladorUrlEnum.URL10280
						.getValue());
		listaUnidadE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), null, true,
				"UNIDAD");
	}
    
    public void cargarListaFuenteRecursos() {
    	
    	String urlEnum = MovimientosControladorUrlEnum.URL1223.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaFuenteRecursos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);     
	}
    
    public void cargarListaFuenteRecursosE() {
    	
    	String urlEnum = MovimientosControladorUrlEnum.URL1223.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaFuenteRecursosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);     
	}

    public void cargarListaReferenciaCnt() {  
    	String urlEnum = MovimientosControladorUrlEnum.URL1224.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaReferenciaCnt = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);
	}
    
    public void cargarListaReferenciaCntE() {   
    	String urlEnum = MovimientosControladorUrlEnum.URL1224.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaReferenciaCntE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);
	}
    
    public void cargarListaAuxiliarDet() {
    	 String urlEnumId = MovimientosControladorUrlEnum.URL6299.getValue();
         UrlBean urlBean = UrlServiceUtil.getInstance()
                         .getUrlServiceByUrlByEnumID(urlEnumId);
         Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         param.put(GeneralParameterEnum.ANO.getName(), anoMov);

         listaAuxiliarDet = new RegistroDataModelImpl(urlBean.getUrl(),
                         	urlBean.getUrlConteo().getUrl(), param, true,
                         	CAMPO_CODIGO);
	}
    
    public void cargarListaAuxiliarDetE() {
    	 String urlEnumId = MovimientosControladorUrlEnum.URL6299.getValue();
         UrlBean urlBean = UrlServiceUtil.getInstance()
                         .getUrlServiceByUrlByEnumID(urlEnumId);
         Map<String, Object> param = new TreeMap<>();
         param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         param.put(GeneralParameterEnum.ANO.getName(), anoMov);

         listaAuxiliarDetE = new RegistroDataModelImpl(urlBean.getUrl(),
                         	urlBean.getUrlConteo().getUrl(), param, true,
                         	CAMPO_CODIGO);
	}
    
    public void cargarListaProyectoDet() {
    	String urlEnum = MovimientosControladorUrlEnum.URL1418.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaProyectoDet = new RegistroDataModelImpl(urlBean.getUrl(),
                           urlBean.getUrlConteo().getUrl(), param, true,
                           CAMPO_CODIGO);
	}
   
    public void cargarListaProyectoDetE() {
    	String urlEnum = MovimientosControladorUrlEnum.URL1418.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaProyectoDetE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            CAMPO_CODIGO);
	}
    
    public void cargarListaCentroCostoDet() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);
        String urlEnumId = MovimientosControladorUrlEnum.URL84866.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        
        listaCentroCostoDet = new RegistroDataModelImpl(urlBean.getUrl(),
                        	  urlBean.getUrlConteo().getUrl(), param, true,
                        	  GeneralParameterEnum.CODIGO.getName());
	}
   
    public void cargarListaCentroCostoDetE() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);
        String urlEnumId = MovimientosControladorUrlEnum.URL84866.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        
        listaCentroCostoDetE = new RegistroDataModelImpl(urlBean.getUrl(),
                        	   urlBean.getUrlConteo().getUrl(), param, true,
                        	   GeneralParameterEnum.CODIGO.getName());
	}
    
    public void cargarListaMovAsociado() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String urlEnumId = "";
        if ("O".equals(claseDocAsMov) || ("R".equals(claseDocAsMov)
            && Arrays.asList("E", "S").contains(claseMov))) {
            urlEnumId = MovimientosControladorUrlEnum.URL71913.getValue();
        }
        else if (Arrays.asList("E", "S").contains(claseMov)) {
            parametrosPorClaseDocumento(param);
            urlEnumId = MovimientosControladorUrlEnum.URL72113.getValue();
            if(cptoMov.equals("D") && claseMov.equals("S") && desagregrarElementos) {
               param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseDocAsMov);
            }
        }
        else if (Arrays.asList("E", "S").contains(claseMov)
            && "M".equals(claseDocAsMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseDocAsMov);
            urlEnumId = MovimientosControladorUrlEnum.URL74313.getValue();
        }
        	 


        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        listaMovAsociado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());

    }  
    
    public void cargarListaMovAsociadoComp() {
        Map<String, Object> param = new TreeMap<>();
        String urlEnumId = "";
        
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosPorClaseDocumento(param);
        urlEnumId = MovimientosControladorUrlEnum.URL82124.getValue();

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

        listaMovAsociadoComp = new RegistroDataModelImpl(urlBean.getUrl(),
                           urlBean.getUrlConteo().getUrl(), param, true,
                           GeneralParameterEnum.NUMERO.getName());

    }
    
    
    public void cargarListaFuenteRecurso() {
        String urlEnum = MovimientosControladorUrlEnum.URL1223.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }
    
    public void cargarListaUbicacion() {
        String urlEnum = MovimientosControladorUrlEnum.URL1922.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), registro.getCampos().get("DEPENDENCIA_DESTINO"));

        listaUbicacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }
    
    public void cargarListaUbicacionOrigen() {
        String urlEnum = MovimientosControladorUrlEnum.URL1922.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), registro.getCampos().get("DEPENDENCIA_ORIGEN"));

        listaUbicacionOrigen = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }
    
    
    public void cargarListaUbicacionDestino() {
        String urlEnum = MovimientosControladorUrlEnum.URL1922.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), registro.getCampos().get("DEPENDENCIA_DESTINO"));

        listaUbicacionDestino = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }
    

    public void cargarListaReferencia() {
        String urlEnum = MovimientosControladorUrlEnum.URL1224.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }

    /**
     * Envio de parametro claseOrden para las clases de movimiento E y
     * S, para los que aplica la tabla <b>ORDENDECOMPRA</b>. Se
     * excluye la clase de documento M
     *
     * @param param
     * Ingresa la clase de orden en el Map.
     */
    private void parametrosPorClaseDocumento(Map<String, Object> param) {

        if (Arrays.asList("ODC", "C").contains(claseDocAsMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "ODC");
        }
        else if (Arrays.asList("CDS", "S").contains(claseDocAsMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CDS");
        }
        else if ("CDC".equals(claseDocAsMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CDC");
        }
        else if ("CAD".equals(claseDocAsMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CAD");
        }
        else if ("J".equals(claseDocAsMov) && "E".equals(claseMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), "CCM");
        }
        else if ("E".equals(claseMov)) {
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseDocAsMov);
        }
    }

    public void cargarListaTercero() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM3.getValue(), tipoPersona);
        param.put(MovimientosControladorEnum.PARAM4.getValue(), cptoMov);
        param.put(MovimientosControladorEnum.PARAM5.getValue(), claseMov);
        param.put(GeneralParameterEnum.NUMERO.getName(), companiaNit);
        String urlEnumId = MovimientosControladorUrlEnum.URL77358.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MovimientosControladorEnum.PARAM2.getValue());
    }

    /**
     * Crea una lista de combo grande con los responsables activos y
     * responsables de almacen por dependencia.
     *
     * @param dependencia
     * C&oacute;digo de la dependencia.
     * @return lista de responsables.
     */
    private RegistroDataModelImpl crearListaResponsables(Object dependencia) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
        String urlEnumId = MovimientosControladorUrlEnum.URL79165.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        return new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MovimientosControladorEnum.PARAM6.getValue());
    }

    public void cargarListaResponsableOrigen() {
        Object dependenciaOrigen = registro.getCampos().get(CAMPO_DEPORIGEN);
        listaResponsableOrigen = crearListaResponsables(dependenciaOrigen);
    }

    public void cargarListaresponsableDestino() {
        Object dependenciaDestino = registro.getCampos().get(CAMPO_DEPDESTINO);
        listaResponsableDestino = crearListaResponsables(dependenciaDestino);
    }

    public void cargarListaRecibidoPor() {
        Object dependenciaDestino = registro.getCampos().get(CAMPO_DEPDESTINO);
        listaRecibidoPor = crearListaResponsables(dependenciaDestino);
    }

    public void cargarListabodegaOrigen() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
        String urlEnumId = MovimientosControladorUrlEnum.URL82866.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaBodegaOrigen = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaComponente() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        String urlEnumId = MovimientosControladorUrlEnum.URL1219.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listacomponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CONSECUTIVO.getName());
    }

    public void cargarListabodegaDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
        String urlEnumId = MovimientosControladorUrlEnum.URL79166.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaBodegaDestino = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCentroDeCosto() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);
        String urlEnumId = MovimientosControladorUrlEnum.URL84866.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListacmbProveedor() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String urlEnumId = MovimientosControladorUrlEnum.URL85966.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        listaCmbProveedor = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        MovimientosControladorEnum.PARAM7.getValue());
    }
    public void cargarListaProyecto() {
        String urlEnum = MovimientosControladorUrlEnum.URL1418.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoMov);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);

    }
    
    public void cargarListaElementoIdi() 
    {
    	String urlEnumId;

        urlEnumId = MovimientosControladorUrlEnum.URL112176.getValue();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PROYECTO", registro.getCampos().get("CODIGOPROYECTO"));
        
        listaElementoIdi = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "RN");
    }
    
    public void cargarListaElementoIdiE() 
    {
    	listaElementoIdiE = listaElementoIdi;
    }
    
    public void cargarListaElementoAux() {
    	String urlEnumId;
    	Map<String, Object> param = new TreeMap<>();
    	
    	String codpro = SysmanFunciones.nvl(registro.getCampos().get("CODIGOPROYECTO"), "9999999999999999").toString();// JM 27/11/2024
    	
    	if (registro.getCampos().get("FUENTEDERECURSO").toString().equals(SysmanConstantes.CONS_FUENTE)
    			&& registro.getCampos().get("REFERENCIA").toString().equals(SysmanConstantes.CONS_FUENTE)
    			&& registro.getCampos().get(CAMPO_AUXILIAR).toString().equals(SysmanConstantes.CONS_AUXILIAR)
    			&& registro.getCampos().get(CAMPO_CCTO).toString().equals(SysmanConstantes.CONS_CENTRO)
    			&& codpro.equals("9999999999999999")) {  
    		urlEnumId = MovimientosControladorUrlEnum.URL112194.getValue();
    	} else {
    		urlEnumId = MovimientosControladorUrlEnum.URL112192.getValue();
    		
    		param.put("PROYECTO", registro.getCampos().get("CODIGOPROYECTO"));
    		param.put("FUENTER", registro.getCampos().get("FUENTEDERECURSO"));
    		param.put("REFERENCIA", registro.getCampos().get("REFERENCIA"));
    		param.put(CAMPO_AUXILIAR, registro.getCampos().get(CAMPO_AUXILIAR));
    		param.put(CAMPO_CCTO, registro.getCampos().get(CAMPO_CCTO));
    	}

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        param.put("BODEGA", registro.getCampos().get(CAMPO_BODEGAORIGEN));
        
        listaElementoAux = new RegistroDataModelImpl(urlBean.getUrl(),
                           urlBean.getUrlConteo().getUrl(), param, true,
                           "RN");
    }
    
    public void cargarListaElementoAuxE() {
    	listaElementoAuxE = listaElementoAux;
    }
        public void agregarRegistroSubSubdmovimiento() {
        try {
        	  try {
        		   reemplazarComillas(registroSub.getCampos(),"MARCA");
                   reemplazarComillas(registroSub.getCampos(),"ESPECIFICACION");
                   reemplazarComillas(registroSub.getCampos(),"REFERENCIA");
                   reemplazarComillas(registroSub.getCampos(),"LOTE");
                   reemplazarComillas(registroSub.getCampos(),"TEMPERATURA");
                   reemplazarComillas(registroSub.getCampos(),"REGISTRO_SANITARIO");
                   reemplazarComillas(registroSub.getCampos(),"REVELACIONES");
                   reemplazarComillas(registroSub.getCampos(),"MODELO");
                   reemplazarComillas(registroSub.getCampos(),"SERIEDEVOLUTIVO");
                   reemplazarComillas(registroSub.getCampos(),"DIRECCION");
                   reemplazarComillas(registroSub.getCampos(),"ESCRITURA");
                   reemplazarComillas(registroSub.getCampos(),"NUMEROCATASTRAL");
                   reemplazarComillas(registroSub.getCampos(),"MATINMOBILIARIA");
                   reemplazarComillas(registroSub.getCampos(),"AREA");
      		} catch (Exception e) {
      			e.printStackTrace();
      		}
              	
        	 if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas, elementos de consumo, fuente y lote 
        		
        		 String lote = (registroSub.getCampos().get("LOTE") != null) ? registroSub.getCampos().get("LOTE").toString() : "";
        		 
        		 BigDecimal existencia = ejbAlmacenCero.buscarExistencia(compania,
        				  registroSub.getCampos().get("ELEMENTO").toString(),
        				  lote,
        				  registroSub.getCampos().get("FUENTEDERECURSO").toString()
                       ); 
        		
        		String cantidadStr = registroSub.getCampos().get("CANTIDAD").toString();
        		String cleanedStr = cantidadStr.replaceAll(",", ""); 
        		BigDecimal cantidad = new BigDecimal(cleanedStr);
        		  
        		  if(existencia.compareTo(cantidad) < 0) {
        			  JsfUtil.agregarMensajeInformativo("La cantidad sobrepasa las cantidades en existencia, No existe saldo para realizar la salida del elemento a la fecha.");
        			  return;
        		  }
        		
            }//JM 24/01/2025 
        	 
        	 Object identificadorObj = registroSub.getCampos().get("IDENTIFICADOR");

        	 if (identificadorObj != null) {
        	     String identificador = identificadorObj.toString();
        	     if ("C".equals(identificador) && identificadorUnico) {
        	         JsfUtil.agregarMensajeError(idioma.getString("TB_TB4484"));
        	         return;
        	     }
        	 }
        	

            if (!validarObligaPlaca()) {
                return;
            }

            if (!validarVidaUtilNIIF()) {
                return;
            }

            if (!validarObligaCampos()) {
                return;
            }
            insertandoDetalle = -1;
            
            registroSub.getCampos().put(CAMPO_COMPANIA, compania);
            registroSub.getCampos().put(CAMPO_TIPOMOV,
                            registro.getCampos().get(CAMPO_TIPOMOV));
            registroSub.getCampos().put(CAMPO_MOVIMIENTO,
                            registro.getCampos().get(CAMPO_NUMERO));
            
            if (manAuxiliares.equals("NO")) {
            	registroSub.getCampos().put(CAMPO_CCTO,
	                        registro.getCampos().get(CAMPO_CCTO));
	            registroSub.getCampos().put(CAMPO_AUXILIAR,
	                        registro.getCampos().get(CAMPO_AUXILIAR));
	        }
            
            registroSub.getCampos().put(CAMPO_FECHA,
                            registro.getCampos().get(CAMPO_FECHA));
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());
            registroSub.getCampos().remove(CAMPO_NOMLARGO);
            registroSub.getCampos().put("UBICACION", registro.getCampos().get("UBICACION"));
            if(desagregrarElementos) {
            registroSub.getCampos().put("TIPOMOVASOCIADO", registro.getCampos().get("TIPOMOVASOCIADO"));
            registroSub.getCampos().put("MOVASOCIADO", registro.getCampos().get("MOVASOCIADO"));
            }
            
            if(manElementoPepsIdi.equals("SI") 
            		&& "E".equals(claseMov)) {
            	registroSub.getCampos().put("CODIGOPROYECTO",
                        registro.getCampos().get("CODIGOPROYECTO"));
            	registroSub.getCampos().put("TIPOMOVASOCIADO", 
                		registro.getCampos().get("TIPOMOVASOCIADO"));
                registroSub.getCampos().put("MOVASOCIADO", 
                		registro.getCampos().get("MOVASOCIADO"));
                registroSub.getCampos().put(CAMPO_OPERACION, 
                		registro.getCampos().get(CAMPO_OPERACION));
                registroSub.getCampos().put("AUX_ALMACEN", 
                		registro.getCampos().get("AUX_ALMACEN"));
                registroSub.getCampos().put(CAMPO_RUEDA, 
                		registro.getCampos().get(CAMPO_RUEDA));
            }

            Locale.setDefault(Locale.US);
            DecimalFormat conversion = new DecimalFormat("####.00");
            registroSub.getCampos().put(CAMPO_VALORTOTAL,
                            conversion.format(Double.parseDouble(registroSub
                                            .getCampos().get(CAMPO_VALORTOTAL)
                                            .toString())));
            registroSub.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA, conversion
                            .format(Double.parseDouble(registroSub.getCampos()
                                            .get(CAMPO_VLRUNITARIO_ANTESIVA)
                                            .toString())));
            
            if(colgaapNiif.equals("SI")) {
	            registroSub.getCampos().put(CAMPO_VLR_COLGAAP, conversion
			                    .format(Double.parseDouble(registroSub.getCampos()
			                                    .get(CAMPO_VALORUNITARIO)
			                                    .toString())));
            }

            long consecutivo;
            String condicion = " COMPANIA = ''" + compania + "'' "
                + "AND TIPOMOVIMIENTO = ''" + tipoMov + "'' "
                + "AND MOVIMIENTO = " + registro.getCampos().get(CAMPO_NUMERO);

            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.D_MOVIMIENTO.getTable(),
                            condicion, CAMPO_CODIGO, "1");
            registroSub.getCampos().put(CAMPO_CODIGO, consecutivo);
            
            if(manElementoPepsIdi.equals("SI") 
            		&& !("E".equals(claseMov)) 
            		&& "C".equals(tipoElemento))
            {
            	registroSub.getCampos().put(CAMPO_ELEMENTO, 
            			registroSub.getCampos().get("ELEMENTO_PEPS"));
            }
            /*if(manFuenteIn.equals("IS")){
            	registroSub.getCampos().put(CAMPO_ELEMENTO, 
            			registroSub.getCampos().get("ELEMENTO_LOTE"));
            }
            registroSub.getCampos().remove("ELEMENTO_LOTE");*/
            registroSub.getCampos().remove("ELEMENTO_PEPS");
            
            if (manAuxiliares.equals("SI")
            		&& registroSub.getCampos().get("LOTE") == null) {
            	registroSub.getCampos().put("LOTE","99999999999999999999");
	        }
            
            String urlEnumId = GenericUrlEnum.D_MOVIMIENTO.getCreateKey();

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            
            /**Proceso auditoria*/
            accionAuditoria = EnumAcciones.INSERT.getValue();
            detalleMov = -1;
            ejecutarAuditoria();
            
            if ("S".equals(claseMov)) {
            	actCantidadAfectada("INSERT");
            }
            
            saldosInventBodega("INSERT");

            if (estado.equals("40") || estado.equals("50")
                || estado.equals("60")) {

                UrlBean updateEstado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                MovimientosControladorUrlEnum.URL1411
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                fields.put(GeneralParameterEnum.ELEMENTO.getName(),
                                registroSub.getCampos().get("ELEMENTO"));
                fields.put(GeneralParameterEnum.ESTADO.getName(),
                                registroSub.getCampos().get("ESTADO"));
                fields.put(GeneralParameterEnum.SERIE.getName(),
                                registroSub.getCampos().get("SERIE"));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(updateEstado.getUrl(),
                                updateEstado.getMetodo(),
                                parameter);
                if (estado.equals("40") || estado.equals("50")) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4314"));
                }
            }
            insertarDespuesSub();
            cargarListaSubdmovimiento();
            insertandoDetalle = 0;
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarRegistro(css, AbstractBeanBaseAcme.ACCION_MODIFICAR);
            registroSub = new Registro(new HashMap<String, Object>());
            registroSub.getCampos().put(CAMPO_SALDOCANT, "0");
            registroSub.getCampos().put(CAMPO_VALORUNITARIO, "0");
            registroSub.getCampos().put(CAMPO_PORCDESCUENTO, "0");
            registroSub.getCampos().put(CAMPO_PORCIVA, "0");
            registroSub.getCampos().put(CAMPO_VALORTOTAL, "0");
            registroSub.getCampos().put(CAMPO_SALDOELEMENTO, "0");
            registroSub.getCampos().put(CAMPO_VALORSALDO, "0");
            registroSub.getCampos().put(CAMPO_VLRUNITARIOPROM, "0");
            registroSub.getCampos().put(CAMPO_IND_REG, "0");
            registroSub.getCampos().put(CAMPO_VALORANTERIOR, "0");
            registroSub.getCampos().put(CAMPO_CANTANTERIOR, "0");
            registroSub.getCampos().put(CAMPO_CCTO, 
            		registro.getCampos().get(CAMPO_CCTO));
            registroSub.getCampos().put(CAMPO_VLRUNITANTERIOR, "0");
            registroSub.getCampos().put(CAMPO_VLRAJUSTADO, "0");
            registroSub.getCampos().put(CAMPO_SALDOKARDEX, "0");
            registroSub.getCampos().put(CAMPO_INCREMENAJUS, "0");
            registroSub.getCampos().put(CAMPO_PEDIDO, "0");
            registroSub.getCampos().put("ITEM", "0");
            registroSub.getCampos().put(CAMPO_COSTOSALIDA, "0");
            registroSub.getCampos().put(CAMPO_COSTOSALIDAAJ, "0");
            registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS, "0");
            registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS1, "0");
            registroSub.getCampos().put(CAMPO_CANTIDADDOCAS, "0");
            registroSub.getCampos().put(CAMPO_VALORDOCAS, "0");
            registroSub.getCampos().put(CAMPO_INFINMUEBLE, "0");
            registroSub.getCampos().put(CAMPO_PLACAANTERIOR, "0");
            registroSub.getCampos().put(CAMPO_REGISTRADOBI, "0");
            registroSub.getCampos().put(CAMPO_VALORBASE, "0");
            registroSub.getCampos().put(CAMPO_VALORIVA, "0");
            registroSub.getCampos().put(CAMPO_PORCIVAUNI, "0");
            registroSub.getCampos().put(CAMPO_VALORTOTALCONIVA, "0");
            registroSub.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA, "0");
            registroSub.getCampos().put("CANTIDADAFECTADA", "0");
            registroSub.getCampos().put("SALDO_PEPS", "0");
            registroSub.getCampos().put("NIIF_VALOR_BASE", "0");
            registroSub.getCampos().put("DETERIORO", "0");
            registroSub.getCampos().put("NIIF_VALOR_TOTAL", "0");
            registroSub.getCampos().put("INSTALACION", "0");
            registroSub.getCampos().put("PORC_VALOR_RESIDUAL", "0");
            registroSub.getCampos().put("SALVAMENTO", "0");
            registroSub.getCampos().put("PREPARACION", "0");
            registroSub.getCampos().put("ENTREGA", "0");
            registroSub.getCampos().put("COMPROBACION", "0");
            registroSub.getCampos().put("APLICANIIF", true);
            registroSub.getCampos().put("FUENTEDERECURSO", 
            		registro.getCampos().get("FUENTEDERECURSO"));
            registroSub.getCampos().put("REFERENCIA_CNT", 
            		registro.getCampos().get("REFERENCIA"));
            registroSub.getCampos().put(CAMPO_AUXILIAR, 
            		registro.getCampos().get(CAMPO_AUXILIAR));
            registroSub.getCampos().put("CODIGOPROYECTO", 
            		registro.getCampos().get("CODIGOPROYECTO"));

            if ("CR".equals(cptoMov)) {
                cantBloq = true;
                registroSub.getCampos().put(CAMPO_CANTIDAD, "0");
            }
            else if ("C".equals(tipoElemento)) {
                cantBloq = false;
                registroSub.getCampos().put(CAMPO_CANTIDAD, "1");
            }
            else {
                cantBloq = true;
                registroSub.getCampos().put(CAMPO_CANTIDAD, "1");
            }

            if (obligaCampos) {
                registroSub.getCampos().put(
                                GeneralParameterEnum.ESPECIFICACION.getName(),
                                "N.A.");
                registroSub.getCampos().put(
                                GeneralParameterEnum.MARCA.getName(), "N.A.");
                registroSub.getCampos().put(
                                GeneralParameterEnum.MODELO.getName(), "N.A.");
                registroSub.getCampos().put(CAMPO_SERIEDEV, "N.A.");
            }
            if(desagregrarElementos) {
            	if(!"S".equals(claseMov)) { //JM 07/10/2024 7752214 
            		tomarFuenteRecursos(); 
                }
	        }
            


        }

    }

    /**
     * 
     * Carga la lista listaPlantillaDocx
     *
     */
    public void cargarListaPlantillaDocx(){

    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.MODULO.getName(), modulo);
    	param.put(GeneralParameterEnum.TIPO.getName(), tipoMov);

    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL104080.getValue());


    	listaPlantillaDocx = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
    			GeneralParameterEnum.CODIGO.getName());
    	visibleListaPlantillas = listaPlantillaDocx==null?false:true;
    }

    private boolean validarObligaCampos() {
        if (obligaCampos && !("C".equals(tipoElemento)) && (SysmanFunciones
                        .validarVariableVacio(SysmanFunciones.nvl(
                                        registroSub.getCampos()
                                                        .get(GeneralParameterEnum.ESPECIFICACION
                                                                        .getName()),
                                        "").toString())
            || SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(
                            registroSub.getCampos()
                                            .get(GeneralParameterEnum.MARCA
                                                            .getName()),
                            "").toString())
            || SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(
                            registroSub.getCampos()
                                            .get(GeneralParameterEnum.MODELO
                                                            .getName()),
                            "").toString())
            || SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(
                            registroSub.getCampos()
                                            .get(CAMPO_SERIEDEV),
                            "").toString()))) {

            JsfUtil.agregarMensajeError(idioma.getString(
                            "TB_TB4337"));

            return false;
        }
        return true;

    }

    private boolean validarVidaUtilNIIF() {
        if ("E".equals(claseMov)
            && ("D".equals(tipoElemento)
                || "M".equals(tipoElemento)
                || "N".equals(tipoElemento))
            && SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(
                            registroSub.getCampos()
                                            .get("VIDAUTILPLACANIIF"),
                            "").toString())
            && (!"R".equals(cptoMov) && !"CR".equals(cptoMov))) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4317"));

            return false;
        }
        return true;
    }

    public void editarRegSubSubdmovimiento(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
        	registroSub = reg;
            detalleMov = -1;
            
            try {
            	
                reemplazarComillas(registroSub.getCampos(),"MARCA");
                reemplazarComillas(registroSub.getCampos(),"ESPECIFICACION");
                reemplazarComillas(registroSub.getCampos(),"REFERENCIA");
                reemplazarComillas(registroSub.getCampos(),"LOTE");
                reemplazarComillas(registroSub.getCampos(),"TEMPERATURA");
                reemplazarComillas(registroSub.getCampos(),"REGISTRO_SANITARIO");
                reemplazarComillas(registroSub.getCampos(),"REVELACIONES");
                reemplazarComillas(registroSub.getCampos(),"MODELO");
                reemplazarComillas(registroSub.getCampos(),"SERIEDEVOLUTIVO");
                reemplazarComillas(registroSub.getCampos(),"DIRECCION");
                reemplazarComillas(registroSub.getCampos(),"ESCRITURA");
                reemplazarComillas(registroSub.getCampos(),"NUMEROCATASTRAL");
                reemplazarComillas(registroSub.getCampos(),"MATINMOBILIARIA");
                reemplazarComillas(registroSub.getCampos(),"AREA");
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            	
        	obtenerValoresAnteriores();
        	
        	cantAnterior();
            
	       	 if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas, elementos de consumo, fuente y lote 
	     		
	       		BigDecimal existencia = ejbAlmacenCero.buscarExistencia(compania,
	       				  registroSub.getCampos().get("ELEMENTO").toString(),
	       				  registroSub.getCampos().get("LOTE").toString(),
	       				  registroSub.getCampos().get("FUENTEDERECURSO").toString()
	                      ); 
	       		
	       		String cantidadStr = registroSub.getCampos().get("CANTIDAD").toString();
	       		String cleanedStr = cantidadStr.replaceAll(",", ""); 
	       		BigDecimal cantidad = new BigDecimal(cleanedStr);
	       		BigDecimal cantAnteriorBD = new BigDecimal(cantAnterior);
	       		existencia = existencia.add(cantAnteriorBD);
	       		  if(existencia.compareTo(cantidad) < 0) {
	       			  JsfUtil.agregarMensajeInformativo("La cantidad sobrepasa las cantidades en existencia, No existe saldo para editar la salida del elemento a la fecha.");
	       			  return;
	       		  }
	       		  
	           }//JM 24/01/2025 
       	
            
            

        	
            reg.getCampos().put("MODIFIED_BY",
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put("DATE_MODIFIED", new Date());
            reg.getCampos().remove(CAMPO_NOMLARGO);
            reg.getCampos().remove("RNUM");
            reg.getCampos().remove("PIDE_VENCIMIENTO");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(
                            GeneralParameterEnum.TIPOMOVIMIENTO.getName());
            reg.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            /**
             * Se adiciona este indicador en cero (0) para que al
             * realizar una actualizacion se ejecute el TRIGGER
             * <code>AIUD_D_MOVIMIENTO</code> y realice la validacion
             * de la cantidad con el saldo disponible en inventario
             */
            reg.getCampos().put(CAMPO_IND_REG, 0);
            reg.getCampos().remove("ELEMENTO_PEPS");
            
            codigoAfect = SysmanFunciones.nvl(reg.getCampos().get("CODIGO_AFECT"), "0").toString();
            
            //reg.getCampos().remove("CODIGO_AFECT");
            
            if(colgaapNiif.equals("SI")) {
	            reg.getCampos().put(CAMPO_VLR_COLGAAP, Double.parseDouble(registroSub.getCampos()
	            		.get(CAMPO_VALORUNITARIO)
			            .toString()));
            }
        	
            String urlEnumId = GenericUrlEnum.D_MOVIMIENTO.getUpdateKey();
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            
            /**Proceso auitoria*/
            accionAuditoria = EnumAcciones.UPDATE.getValue();
        	ejecutarAuditoria();
        	
        	if ("S".equals(claseMov)) {
        		actCantidadAfectada("UPDATE");
        	}
        	
            saldosInventBodega("UPDATE");

            if (estado.equals("40") || estado.equals("50")
                || estado.equals("60")) {

                UrlBean updateEstado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                MovimientosControladorUrlEnum.URL1411
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                fields.put(GeneralParameterEnum.ELEMENTO.getName(),
                                reg.getCampos().get("ELEMENTO"));
                fields.put(GeneralParameterEnum.ESTADO.getName(),
                                reg.getCampos().get("ESTADO"));
                fields.put(GeneralParameterEnum.SERIE.getName(),
                                reg.getCampos().get("SERIE"));
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                Parameter parameter = new Parameter();
                parameter.setFields(fields);
                requestManager.update(updateEstado.getUrl(),
                                updateEstado.getMetodo(),
                                parameter);
            }
            UrlBean updateDevolutivo = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            MovimientosControladorUrlEnum.URL1414
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.ELEMENTO.getName(),
                            reg.getCampos().get("ELEMENTO"));

            fields.put(GeneralParameterEnum.SERIE.getName(),
                            reg.getCampos().get("SERIE"));

            fields.put(GeneralParameterEnum.MARCA.getName(),
                            reg.getCampos().get(GeneralParameterEnum.MARCA
                                            .getName()));
            
            fields.put(GeneralParameterEnum.MODELO.getName(),
                    reg.getCampos().get(GeneralParameterEnum.MODELO
                                    .getName()));

            fields.put(GeneralParameterEnum.DESCRIPCION.getName(),
                            reg.getCampos().get(
                                            GeneralParameterEnum.ESPECIFICACION
                                                            .getName()));

            fields.put(CAMPO_SERIEDEV,
                            reg.getCampos().get(CAMPO_SERIEDEV));
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.update(updateDevolutivo.getUrl(),
                            updateDevolutivo.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
            cargarRegistro(css, AbstractBeanBaseAcme.ACCION_MODIFICAR);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubdmovimiento();
            cargarlistacomponenteE();
        }
    }

    public void eliminarRegSubSubdmovimiento(Registro reg) {
        try {
            registroSub = reg;
            detalleMov = -1;
            obtenerValoresAnteriores();
        	
            codigoAfect = SysmanFunciones.nvl(reg.getCampos().get("CODIGO_AFECT"), "0").toString();
            
            saldosInventBodega("DELETE");
            
            String urlEnumId = GenericUrlEnum.D_MOVIMIENTO.getDeleteKey();
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);
            reversar(reg.getLlave().get("KEY_TIPOMOVIMIENTO").toString(),
                            reg.getLlave().get("KEY_MOVIMIENTO").toString(),
                            reg.getLlave().get("KEY_CODIGO").toString());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            
            /**Proceso auitoria*/
            accionAuditoria = EnumAcciones.DELETE.getValue();
            ejecutarAuditoria();
            
            if ("S".equals(claseMov)) {
            	actCantidadAfectada("DELETE");
            }

            if (estado.equals("40") || estado.equals("50")
                || estado.equals("60")) {

                Map<String, Object> paramBodega = new HashMap<>();
                paramBodega.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramBodega.put(GeneralParameterEnum.ELEMENTO.getName(),
                                reg.getCampos().get(
                                                GeneralParameterEnum.ELEMENTO
                                                                .getName()));
                paramBodega.put(GeneralParameterEnum.SERIE.getName(),
                                reg.getCampos().get(GeneralParameterEnum.SERIE
                                                .getName()));

                Registro rsBodega = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                MovimientosControladorUrlEnum.URL1412
                                                                                .getValue())
                                                .getUrl(),
                                                paramBodega));

                if (rsBodega != null) {

                    String bodega = SysmanFunciones
                                    .nvl(rsBodega.getCampos()
                                                    .get("BODEGA"),
                                                    "")
                                    .toString();

                    if (bodega.equals("40") || bodega.equals("50")
                        || bodega.equals("60")) {

                        UrlBean updateEstado = UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        MovimientosControladorUrlEnum.URL1411
                                                                        .getValue());
                        Map<String, Object> fields = new TreeMap<>();
                        fields.put(GeneralParameterEnum.COMPANIA.getName(),
                                        compania);
                        fields.put(GeneralParameterEnum.ELEMENTO.getName(),
                                        reg.getCampos().get("ELEMENTO"));
                        fields.put(GeneralParameterEnum.ESTADO.getName(),
                                        "B");
                        fields.put(GeneralParameterEnum.SERIE.getName(),
                                        reg.getCampos().get("SERIE"));
                        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                        SessionUtil.getUser().getCodigo());
                        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                        new Date());
                        Parameter parameter = new Parameter();
                        parameter.setFields(fields);
                        requestManager.update(updateEstado.getUrl(),
                                        updateEstado.getMetodo(),
                                        parameter);

                    }

                }
            }
            cargarRegistro(css, AbstractBeanBaseAcme.ACCION_MODIFICAR);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubdmovimiento();
            eliminarDespuesSub();
            registro.getCampos();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private void actCantidadAfectada(String accion) {
    	try {
			almacenCincoRemote.actCantidadAfectada(compania,tipoMov,
					new BigInteger(registro.getCampos().get(CAMPO_NUMERO).toString()),
					Double.parseDouble(registroSub.getCampos().get(CAMPO_CANTIDAD).toString()),
					registroSub.getCampos().get(CAMPO_ELEMENTO).toString(),
					Long.parseLong(SysmanFunciones.nvl(registroSub.getCampos().get(CAMPO_SERIE),"0").toString()),
					codigoAfect,cantAnterior,accion);
		} catch (NumberFormatException | SystemException e) {
			e.printStackTrace();
		}
    }

    private void cantAnterior() {
    	Registro reg = null;
    	cantAnterior = 0;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOMOV", tipoMov);
        param.put(GeneralParameterEnum.MOVIMIENTO.getName(),
        		registro.getCampos().get(CAMPO_NUMERO));
        param.put(GeneralParameterEnum.CODIGO.getName(),
        		registroSub.getCampos().get(CAMPO_CODIGO));

        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosControladorUrlEnum.URL119032
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null) {
            	cantAnterior = Double.parseDouble(reg.getCampos().get(CAMPO_CANTIDAD).toString());
            }
        } catch (SystemException e) {
        	logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private void saldosInventBodega(String accion) {
    	if (manAuxiliares.equals("SI")) {
	    	try {
				almacenCincoRemote.saldosInventBodega(compania, tipoMov,
						Long.parseLong(registro.getCampos().get(CAMPO_NUMERO).toString()),
						registroSub.getCampos().get(CAMPO_ELEMENTO).toString(), 
						registro.getCampos().get(CAMPO_BODEGAORIGEN).toString(),
						registro.getCampos().get(CAMPO_BODEGADESTINO).toString(),
						registroSub.getCampos().get("FUENTEDERECURSO").toString(), 
						registroSub.getCampos().get("REFERENCIA_CNT").toString(), 
						registroSub.getCampos().get(CAMPO_AUXILIAR).toString(), 
						registroSub.getCampos().get("CODIGOPROYECTO").toString(), 
						registroSub.getCampos().get(CAMPO_CCTO).toString(), 
						SysmanFunciones.nvl(registroSub.getCampos().get("LOTE"),"99999999999999999999").toString(), 
						Double.parseDouble(registroSub.getCampos().get(CAMPO_CANTIDAD).toString()), cantAnterior, 
						accion, SessionUtil.getUser().getCodigo());
			} catch (NumberFormatException | SystemException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subdmovimiento.
     */
    public void cancelarEdicionSubdmovimiento() {
        cargarListaSubdmovimiento();
    }

    public void cambiarDependenciaAnt() {
        nomRespOrigen = "";
        registro.getCampos().put(CAMPO_RESORIGEN, null);
        registro.getCampos().put(CAMPO_SUCRESORIGEN, null);
        cargarListaResponsableOrigen();
        if (listaResponsableOrigen != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put("JEFEUNIDADT", true);
            map.put(MovimientosControladorEnum.PARAM8.getValue(), 1);
            Registro regResp;
            try {
                regResp = listaResponsableOrigen.getRegistroUnico(map);
                if (regResp != null) {
                    registro.getCampos().put(CAMPO_RESORIGEN,
                                    regResp.getCampos().get(CAMPO_CEDULA));
                    registro.getCampos().put(CAMPO_SUCRESORIGEN,
                                    regResp.getCampos().get(CAMPO_SUCURSAL));
                    nomRespOrigen = extraerString(
                                    regResp.getCampos().get(CAMPO_NOMBRERES));
                    cargarListaSerie();
                    cargarListaSerieE();
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            cargarListaResponsableOrigen();
            predecesor = service.buscarEnLista(
                            registro.getCampos().get("DEPENDENCIA_ORIGEN")
                                            .toString(),
                            "CODIGO", "PREDECESOR", listaDependenciaAnt);
            cargarListacmbDependencia();
        
            cargarListaUbicacionOrigen();
            registro.getCampos().put("NOMBRE_UBICACIONORIGEN", null);
            registro.getCampos().put("UBICACION_ORIGEN", null);
        }

    }

    public void cambiarValorResidual() {
        // <CODIGO DESARROLLADO>
        // </CODIGO DESARROLLADO>
    }

    public void cambiarCmbDependencia() {
        nomRespDestino = "";
        nomRecibidoPor = "";
        registro.getCampos().put(CAMPO_RESDESTINO, null);
        registro.getCampos().put(CAMPO_SUCRESDESTINO, null);
        registro.getCampos().put(CAMPO_SUCRECIBIDO, null);
        registro.getCampos().put(CAMPO_NITRECIBIDO, null);
        cargarListaresponsableDestino();
        cargarListaRecibidoPor();
        if (listaResponsableDestino != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put("JEFEUNIDADT", true);
            map.put(MovimientosControladorEnum.PARAM8.getValue(), 1);
            Registro regResp;
            try {
                regResp = listaResponsableDestino.getRegistroUnico(map);
                if (regResp != null) {
                    registro.getCampos().put(CAMPO_RESDESTINO,
                                    regResp.getCampos().get(CAMPO_CEDULA));
                    registro.getCampos().put(CAMPO_SUCRESDESTINO,
                                    regResp.getCampos().get(CAMPO_SUCURSAL));
                    nomRespDestino = extraerString(
                                    regResp.getCampos().get(CAMPO_NOMBRERES));
                    manejaRecibido = esTraspaso();
                    cargarNombreRecibido();
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
        }
        cargarListaUbicacion();       
        registro.getCampos().put("UBICACION", null);
        registro.getCampos().put("NOMBRE_UBICACION", null);
        
        cargarListaUbicacionDestino();
        registro.getCampos().put("NOMBRE_UBICACIONDESTINO", null);
        registro.getCampos().put("UBICACION_DESTINO", null);
    }
    public void cambiarValorAIU() {
    	
    	boolean ind = (Boolean) registro.getCampos().get("AIU");
    	if(ind){
    		registro.getCampos().put("VLRUNITARIO_SINIVA", true);
    				activoAIU = true;
    		
    	}
    	else {
    		registro.getCampos().put("VLRUNITARIO_SINIVA", false);
    		activoAIU = false;
    		
    	}

        
    }

    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        imprimirMovimiento(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
    	 Map<String, Object> param = new HashMap<>();
	        param.put("s$compania$s", compania);
	        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());
	        String[] campos = new String[3];

	        String[] valores = new String[3];
	        campos[0] = "codigoPlantilla";
	        campos[1] = "fechaPlantilla";
	        campos[2] = "nombreDocDescarga";

	        valores[0] = plantilla;
	        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
	        valores[2] = nombrePlantilla;

	        HashMap<String, String> variablesConsultaW = new HashMap<>();
	        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
	        variablesConsultaW.put("s$tipoMovimiento$s",  tipoMov);
	        variablesConsultaW.put("s$movimientoInicial$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        variablesConsultaW.put("s$movimientoFinal$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

	        SessionUtil.cargarModalDatosFlash(
	                        Integer.toString(
	                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
	                                                        .getCodigo()),
	                        SessionUtil.getModulo(),
	                        campos,
	                        valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmbModNo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerArchivo en la vista
     *
     */
    public void oprimirVerArchivo() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { PARAMETRO_TIPO_MOV, PARAMETRO_NUMERO_MOV };
        String[] valores = { tipoMov, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirInterface() {
        
    	try {
    	
    	String usuario = SessionUtil.getUser().getCodigo();
    	long numero = Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO)));
    	BigDecimal numeroComprobante = BigDecimal.valueOf(numero);    	
    	
    	if(generaCausacion && validarTipoMov())
    	{    		
    		String cadenaClob = ejbContabilizarCero.contabilizarHNivelesCC(
                    compania,
                    Integer.parseInt(anoMov),
                    Integer.parseInt(mesMov),
                    (Date)registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
                    tipoMov,
                    numeroComprobante,
                    false,
                    SessionUtil.getUser()
                                    .getCodigo());
    		
    		archivoDescarga = JsfUtil.getArchivoDescarga(
                    JsfUtil.serializarPlano(cadenaClob),
                    SessionUtil.getCompaniaIngreso().getNombre()
                                    + " Contabilizar.txt");
    	}
    	
    	String datos = almacenCincoRemote.generarCausacion(compania, tipoMov, numero, usuario);
    	String[] param = datos.split(",");
    	
    	Map<String, Object> parametros = new HashMap<>();
        parametros.put("anoMov", anoMov);
        parametros.put("mesMov", mesMov);
        parametros.put("tipoMov", tipoMov);
        parametros.put("claseDocAsoc", claseDocAsMov);
        parametros.put("inventarioInicial", invInicial);
        parametros.put("claseMov", claseMov);
        parametros.put("tipoElementoMov", tipoElemento);
        parametros.put("tituloForm", tituloForm);
        parametros.put("cptoMov", cptoMov);
        parametros.put("manVtas", manVtas);
        parametros.put("claseDocAsMov", claseDocAsMov);
        parametros.put("generaPlaca", generaPlaca);
        parametros.put("tipoPersona", tipoPersona);
        parametros.put("pideCCto", pideCCto);
        parametros.put("nroInicial", nroInicial);
        parametros.put("claseBodOrig", claseBodOrig);
        parametros.put("claseBodDest", claseBodDest);
        parametros.put("obligaOrigen", obligaOrigen);
        parametros.put("obligaDestino", obligaDestino);
        parametros.put("obligaTercero", obligaTercero);
        parametros.put("obligaProveedor", obligaProveedor);
        parametros.put("igualarOrigDest", igualarOrigDest);
        parametros.put("obligaCampos", obligaCampos);
        parametros.put("bloqueaAux", bloqueaAux);
        parametros.put("MovContable", param[0]);
        parametros.put("retorno", true);
        parametros.put("rid", css);
        SessionUtil.setSessionVarContainer("parametrosAlm",parametros);
    	
    	Map<String, Object> rid = new HashMap<>();
    	rid.put(GeneralParameterEnum.KEY_TIPO.getName(), param[0]);
    	rid.put(GeneralParameterEnum.KEY_NUMERO.getName(), param[1]);
    	rid.put(GeneralParameterEnum.KEY_ANO.getName(), param[2]);
    	rid.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
    	
    	String[] campos = { "rid", "opcionMenu", "ridContable", "almacen"};
    	Object[] valores = { css, 10306, rid, true};
		
		SessionUtil.redireccionarPorFormulario("1",
				Integer.toString(GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR.getCodigo()), campos, valores,
				true);
    	} catch (SystemException | NamingException | JRException | IOException e) {
    		logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
    	}

	// </CODIGO_DESARROLLADO>
    }

    public void oprimircmdRegistrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioCopiar(SelectEvent event) {
        if (event.getObject() != null) {
            SessionUtil.redireccionar((Direccionador) event.getObject());
        }
    }

    public void oprimirCopiar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "tipo", "numero", "fecha", "tercero", "sucursal",
                            "auxiliar" };
        Object[] valores = { registro.getCampos()
                        .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName())
                        .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.NUMERO
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.FECHA
                                                             .getName()),

                             registro.getCampos()
                                             .get(GeneralParameterEnum.TERCERO
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.SUCURSAL
                                                             .getName())
                                             .toString(),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.AUXILIAR
                                                             .getName())
                                             .toString() };

        SessionUtil.cargarModalDatosFlash(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCOPIARDE_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirDetalle() {
        // <CODIGO_DESARROLLADO>

        try {
            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("tipo", registro.getCampos().get(CAMPO_TIPOMOV));
            reemplazos.put("numero", registro.getCampos().get(CAMPO_NUMERO));

            String strSql = Reporteador.resuelveConsulta(
                            "800305DetallesMovimiento",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);
            if (!rs.isEmpty()) {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                FORMATOS.EXCEL);
            }
            else {
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEliminarLote() {
        // <CODIGO_DESARROLLADO>
        ocultarEliminarLote = true;
        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirDescargarPlantilla() {
    	try {
    		//llamado a la informacion con los parametros de compania, tipocomprobante y numero 
    		String tipoMov = SysmanFunciones.toString(registro.getCampos().get(CAMPO_TIPOMOV));
    		long numero = Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO)));
    		String datos;

    		datos = almacenCincoRemote.plantillaExcelIDCBIS(compania, tipoMov, numero);


    		if(datos.isEmpty()) {
    			JsfUtil.agregarMensajeError(
    					idioma.getString(
    							"TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
    			return;
    		}

    		asignarInformacionVencimiento(datos);
    	} catch (SystemException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    public void aceptarEliminarLote() {
        List<Registro> detallesMovimiento = null;
        if (detallesMovimiento != null) {
            tieneDetalles = !detallesMovimiento.isEmpty();
        }
        else {
            tieneDetalles = false;
        }

        if (tieneDetalles) {
            String mensaje = idioma.getString("TB_TB4313");
            mensaje = mensaje.replace("s$tipo$s", registro.getCampos()
                            .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName())
                            .toString());
            mensaje = mensaje.replace("s$comprobante$s", registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName())
                            .toString());
            JsfUtil.agregarMensajeAlerta(mensaje);
        }
        else {

            try {

                almacenCincoRemote.eliminarLoteAlmacen(compania, registro
                                .getCampos()
                                .get(GeneralParameterEnum.TIPOMOVIMIENTO
                                                .getName())
                                .toString(),
                                Long.parseLong(registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName())
                                                .toString()));

                cargarRegistro(css, AbstractBeanBaseAcme.ACCION_MODIFICAR);
                registroSub = new Registro(new HashMap<String, Object>());
                registroSub.getCampos().put(CAMPO_PORCIVA, "0");
                registroSub.getCampos().put(CAMPO_VALORTOTAL, "0");
                registroSub.getCampos().put(CAMPO_VALORANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_CANTANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_VLRUNITANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_VLRAJUSTADO, "0");
                registroSub.getCampos().put(CAMPO_SALDOKARDEX, "0");
                registroSub.getCampos().put(CAMPO_INCREMENAJUS, "0");
                registroSub.getCampos().put("ITEM", "0");
                registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS, "0");
                registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS1, "0");
                registroSub.getCampos().put(CAMPO_CANTIDADDOCAS, "0");
                registroSub.getCampos().put(CAMPO_VALORDOCAS, "0");
                registroSub.getCampos().put(CAMPO_VALORBASE, "0");
                registroSub.getCampos().put(CAMPO_VALORIVA, "0");
                registroSub.getCampos().put(CAMPO_PORCIVAUNI, "0");
                registroSub.getCampos().put(CAMPO_VALORTOTALCONIVA, "0");
                registroSub.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA, "0");
                registroSub.getCampos().put(CAMPO_CANTIDAD, "1");
                registroSub.getCampos().put("CANTIDADAFECTADA", "0");
                registroSub.getCampos().put("SALDO_PEPS", "0");
                registroSub.getCampos().put("NIIF_VALOR_BASE", "0");
                registroSub.getCampos().put("DETERIORO", "0");
                registroSub.getCampos().put("NIIF_VALOR_TOTAL", "0");
                registroSub.getCampos().put("INSTALACION", "0");
                registroSub.getCampos().put("PORC_VALOR_RESIDUAL", "0");
                registroSub.getCampos().put("SALVAMENTO", "0");
                registroSub.getCampos().put("PREPARACION", "0");
                registroSub.getCampos().put("ENTREGA", "0");
                registroSub.getCampos().put("COMPROBACION", "0");
                registroSub.getCampos().put("APLICANIIF", true);
                    registroSub.getCampos().put(CAMPO_CCTO, 
                        registro.getCampos().get(CAMPO_CCTO));
                registroSub.getCampos().put("FUENTEDERECURSO", 
                        registro.getCampos().get("FUENTEDERECURSO"));
                registroSub.getCampos().put("REFERENCIA_CNT", 
                        registro.getCampos().get("REFERENCIA"));
                registroSub.getCampos().put(CAMPO_AUXILIAR, 
                        registro.getCampos().get(CAMPO_AUXILIAR));
                registroSub.getCampos().put("CODIGOPROYECTO", 
                        registro.getCampos().get("CODIGOPROYECTO"));

                ocultarEliminarLote = false;

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_PROCESO_EJECUTADO"));
            }
            catch (SystemException | NumberFormatException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public void cancelarEliminarLote() {
        ocultarEliminarLote = false;
    }

    public void oprimirCmdEditar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "tipoMovp", "movp" };
        String[] valores = { tipoMov, extraerString(
                        registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatosFlash("501", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSeleccionar() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { "tipoMovp", "movp", "dependencia", "responsable",
                            "sucursal", "fecha" };
        Object[] valores = { tipoMov,
                             extraerString(registro.getCampos()
                                             .get(CAMPO_NUMERO)),
                             extraerString(registro.getCampos()
                                             .get(CAMPO_DEPORIGEN)),
                             extraerString(registro.getCampos()
                                             .get(CAMPO_RESORIGEN)),
                             extraerString(registro.getCampos()
                                             .get(CAMPO_SUCRESORIGEN)),
                             registro.getCampos()
                                             .get("FECHA") };
        SessionUtil.cargarModalDatosFlash("466", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirdocumentos() {

        // <CODIGO_DESARROLLADO>

        String[] campos = { "tipo", "movimiento" };

        Object[] valores = { registro.getCampos()
                        .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName())
                        .toString(),
                             String.valueOf(registro.getCampos()
                                             .get(GeneralParameterEnum.NUMERO
                                                             .getName())
                                             .toString()) };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.DOCUMENTOSCORRECCIONVALOR_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirubicacions(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "tipoMovimiento", "movimiento", "codigo",
                            "elemento", "serie", "bloqueo" };

        Object[] valores = { reg.getCampos()
                        .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName()
                                        .toString()),
                             String.valueOf(reg.getCampos().get(
                                             GeneralParameterEnum.MOVIMIENTO
                                                             .getName()
                                                             .toString())),
                             String.valueOf(reg.getCampos()
                                             .get(GeneralParameterEnum.CODIGO
                                                             .getName()
                                                             .toString())),
                             String.valueOf(reg.getCampos()
                                             .get(GeneralParameterEnum.ELEMENTO
                                                             .getName()
                                                             .toString())),
                             String.valueOf(reg.getCampos()
                                             .get(GeneralParameterEnum.SERIE
                                                             .getName()
                                                             .toString())),
                             SysmanFunciones.validarCampoVacio(reg.getCampos(),
                                             "COMPONENTE") ? false : true };

        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.UBICACIONELEMENTO_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdTrasladar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void asignarInformacionVencimiento(String datos) {
    	FileInputStream file = null;
    	Workbook workbook = null;
    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
    		String archivo = String.valueOf(contArchivoInventarioVencimiento.getArchivo());
    		String extension = archivo.substring(archivo.indexOf('.'), archivo.length()).toLowerCase();
    		if (validarArchivo(extension)) {
    			String rutaArchivo = contArchivoInventarioVencimiento.getArchivo().getPath();
    			file = new FileInputStream(new File(rutaArchivo));

    			if (".xls".equals(extension)) {
    				workbook = new HSSFWorkbook(file);
    			} else {
    				workbook = new XSSFWorkbook(file);
    			}
    			Sheet sheet = workbook.getSheetAt(0);

    			String proveedor = registro.getCampos().get("TERCERO").toString();


    			String[] registro = datos.split(SysmanConstantes.SEPARADOR_REG);
    			String[] colum;

    			if (registro.length == 0) {
    				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2514"));
    				return;
    			}

    			int registrosPorHoja = 20;
    			int hojaActual = 0;
    			int row = 12;
    			int[] columnas = {0, 2, 4, 8,10, 14, 16, 27, 33};
    			for (int i = 0; i < registro.length; i++) {
    				row = row + 1;
    				int hoja = 1 + i;
    				colum = registro[i].split(SysmanConstantes.SEPARADOR_COL);

    				if (i % registrosPorHoja == 0 && i > 0) {
    					// Cambiar a una nueva hoja (suponiendo que ya existe una hoja)
    					hojaActual++;
    					row = 13;
    				}

    				if (hojaActual < workbook.getNumberOfSheets()) {
    					sheet = workbook.getSheetAt(hojaActual);


    					Cell cellProveedor = sheet.getRow(5).getCell(14);
    					Cell cellNit = sheet.getRow(5).getCell(26);
    					cellProveedor.setCellValue(nomTercero);
    					cellNit.setCellValue(proveedor);


    					Cell cell0 = sheet.getRow(row).getCell(0);
    					for (int j = 0; j < colum.length; j++) {
    						if (j < columnas.length) {

    							int columna = columnas[j];

    							Cell cell = sheet.getRow(row).getCell(columna);
    							cell.setCellValue(colum[j]);


    						}

    					}
    				}else {

    					JsfUtil.agregarMensajeAlerta("No tiene creadas las hojas requeridas para insertar todos los items. Por favor revise la plantilla.");
    					archivoDescarga = null;
    					return;

    				}
    			}
    			workbook.write(out);
    			out.close();
    			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
    					contArchivoInventarioVencimiento.getArchivo().getName());


    		}
    	} catch (IOException | NumberFormatException | JRException e) {
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	} finally {
    		try {
    			if (file != null) {
    				file.close();
    			}
    			if (workbook != null) {
    				workbook.close();
    			}
    		} catch (IOException e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		}
    	}
    }
    
    
    public boolean validarArchivo(String extension) {
		if (contArchivoInventarioVencimiento.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			if ((".xlsx".equals(extension)) || (".xls".equals(extension))) {
				return true;
			} else {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4002"));
				return false;
			}
		}
	}

    public void seleccionarFilaElemento(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_ELEMENTO,
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO,
                        registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put(CAMPO_MARCA, "");
        registroSub.getCampos().put(CAMPO_MODELO, "");
        registroSub.getCampos().put(CAMPO_SERIEDEV, "");
        registroSub.getCampos().put("UNIDAD",
                registroAux.getCampos().get("UNIDAD"));
        registroSub.getCampos().put("REFERENCIA",
                registroAux.getCampos().get("REFERENCIA"));
                
        if(manAuxiliares.equals("NO")) {
	    	if(manFuenteIn.equals("SI")) {
	    		registroSub.getCampos().put("FUENTEDERECURSO",
	                registro.getCampos().get("FUENTEDERECURSO"));
	    	}else {
	    		registroSub.getCampos().put("FUENTEDERECURSO",SysmanConstantes.CONS_FUENTE);
	    	}
        }
   
        pideVencimiento = !(boolean) SysmanFunciones.nvl(registroAux.getCampos().get("PIDE_VENCIMIENTO"), false);

        if ((!"C".equals(tipoElemento)) && (!"E".contains(claseMov))) {
            cambiarEspecificacion();
            registroSub.getCampos().put(CAMPO_SERIE, "");
        }
        cambiarElemento();
        cambiarCantidad();
        cargaMesesVidaUtil(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString());
        //EFCM
        if (verPorcValorResidual) {
        	cargaPorcValorResidual(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                    .toString());	
        }
        cambiarValor();
        //efcm fin
        validarObligaPlaca();
        
        //(TICKET: 7746175; FECHA: 22/05/2024; AUTOR: JEG)
        if(desagregrarElementos && ("E".contains(claseMov) && ("C".equals(cptoMov) || "CM".equals(cptoMov))) && !(registro.getCampos().get("MOVASOCIADO").equals("0"))) {
        	cargarDetalleOrden();

        }

    }
    
    public void seleccionarFilaElementoLote(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_ELEMENTO,
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO,
                        registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put(CAMPO_MARCA, "");
        registroSub.getCampos().put(CAMPO_MODELO, "");
        registroSub.getCampos().put(CAMPO_SERIEDEV, "");
        registroSub.getCampos().put("UNIDAD",
                registroAux.getCampos().get("UNIDAD"));
        registroSub.getCampos().put("REFERENCIA",
                registroAux.getCampos().get("REFERENCIA"));
        
        registroSub.getCampos().put("LOTE",
                registroAux.getCampos().get("LOTE"));
        
        registroSub.getCampos().put("FUENTEDERECURSO",
                registroAux.getCampos().get("FUENTEDERECURSO"));        
        
        
        registroSub.getCampos().put("UNIDAD",
                registroAux.getCampos().get("UNIDAD"));
        
        registroSub.getCampos().put("FECHA_VENCIMIENTO",
                registroAux.getCampos().get("FECHAVENCIMIENTO"));
        
        
        
        
        pideVencimiento = !(boolean) SysmanFunciones.nvl(registroAux.getCampos().get("PIDE_VENCIMIENTO"), false);

        if ((!"C".equals(tipoElemento)) && (!"E".contains(claseMov))) {
            cambiarEspecificacion();
            registroSub.getCampos().put(CAMPO_SERIE, "");
        }
        cambiarElemento();
        cambiarCantidad();
        cambiarValor();
        cargaMesesVidaUtil(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString());
        validarObligaPlaca();

    }
    
    public void seleccionarFilaProyecto(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOPROYECTO",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomProyecto = extraerString(
                        registroAux.getCampos().get(CAMPO_NOMBREPROYECTO));
    }

    public void seleccionarFilaElementoIdi(SelectEvent event) {
    	registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("ELEMENTO_PEPS",
        		registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO, 
        		registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put("IDENTIFICADOR", 
        		registroAux.getCampos().get("IDENTIFICADOR"));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put(CAMPO_VALORTOTAL, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put("TIPOMOVIMIENTO_AFECT", 
        		registroAux.getCampos().get(CAMPO_TIPOMOV));
        registroSub.getCampos().put("MOVIMIENTO_AFECT", 
        		registroAux.getCampos().get(CAMPO_NUMERO));
        registroSub.getCampos().put("CODIGOPROYECTO", 
        		registroAux.getCampos().get("PROYECTO"));
        registroSub.getCampos().put("TIPOMOVASOCIADO", 
        		registroAux.getCampos().get("TIPOMOVASOCIADO"));
        registroSub.getCampos().put("MOVASOCIADO", 
        		registroAux.getCampos().get("MOVASOCIADO"));
        registroSub.getCampos().put(CAMPO_OPERACION, 
        		registroAux.getCampos().get(CAMPO_OPERACION));
        registroSub.getCampos().put("AUX_ALMACEN", 
        		registroAux.getCampos().get("AUX_ALMACEN"));
        registroSub.getCampos().put(CAMPO_RUEDA, 
        		registroAux.getCampos().get(CAMPO_RUEDA));
        
        existenciaElemento = extraerDouble(
        		registroAux.getCampos().get("EXISTENCIA").toString());
        
        
    	if(manFuenteIn.equals("SI")) {
    		registroSub.getCampos().put("FUENTEDERECURSO",
                registro.getCampos().get("FUENTEDERECURSO"));
    	}else {
    		registroSub.getCampos().put("FUENTEDERECURSO",SysmanConstantes.CONS_FUENTE);
    	}
        
        
        
        actualizarSaldoCantidad(registroSub, existenciaElemento);
        cambiarCantidad();
        cambiarValor();
    }
    
    public void seleccionarFilaElementoIdiE(SelectEvent event) {
    	registroAux = (Registro) event.getObject();
        auxiliar = extraerString(
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO, 
        		registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put("IDENTIFICADOR", 
        		registroAux.getCampos().get("IDENTIFICADOR"));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put(CAMPO_VALORTOTAL, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put("TIPOMOVIMIENTO_AFECT", 
        		registroAux.getCampos().get(CAMPO_TIPOMOV));
        registroSub.getCampos().put("MOVIMIENTO_AFECT", 
        		registroAux.getCampos().get(CAMPO_NUMERO));
        registroSub.getCampos().put("CODIGOPROYECTO", 
        		registroAux.getCampos().get("PROYECTO"));
        registroSub.getCampos().put("TIPOMOVASOCIADO", 
        		registroAux.getCampos().get("TIPOMOVASOCIADO"));
        registroSub.getCampos().put("MOVASOCIADO", 
        		registroAux.getCampos().get("MOVASOCIADO"));
        registroSub.getCampos().put(CAMPO_OPERACION, 
        		registroAux.getCampos().get(CAMPO_OPERACION));
        registroSub.getCampos().put("AUX_ALMACEN", 
        		registroAux.getCampos().get("AUX_ALMACEN"));
        registroSub.getCampos().put(CAMPO_RUEDA, 
        		registroAux.getCampos().get(CAMPO_RUEDA));
    }
    
    public void seleccionarFilaElementoAux(SelectEvent event) {
    	registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_ELEMENTO,
        		registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO, 
        		registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put("IDENTIFICADOR", 
        		registroAux.getCampos().get("IDENTIFICADOR"));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put(CAMPO_VALORTOTAL, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put("CODIGOPROYECTO", 
        		registroAux.getCampos().get("PROYECTO"));
        registroSub.getCampos().put("FUENTEDERECURSO", 
        		registroAux.getCampos().get("FUENTEDERECURSO"));
        registroSub.getCampos().put("REFERENCIA_CNT", 
        		registroAux.getCampos().get("REFERENCIA"));
        registroSub.getCampos().put(CAMPO_AUXILIAR, 
        		registroAux.getCampos().get(CAMPO_AUXILIAR));
        registroSub.getCampos().put(CAMPO_CCTO, 
        		registroAux.getCampos().get(CAMPO_CCTO));
        registroSub.getCampos().put("LOTE", 
        		registroAux.getCampos().get("LOTE"));
        
        elemento = extraerString(registroAux.getCampos().get(CAMPO_CODELEMENTO));
        
        existenciaElemento = extraerDouble(
        		registroAux.getCampos().get("EXISTENCIA").toString());
        
        actualizarSaldoCantidad(registroSub, existenciaElemento);
        cambiarCantidad();
        cambiarValor();
    }
    
    public void seleccionarFilaElementoAuxE(SelectEvent event) {
    	registroAux = (Registro) event.getObject();
        auxiliar = extraerString(
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO, 
        		registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put("IDENTIFICADOR", 
        		registroAux.getCampos().get("IDENTIFICADOR"));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put(CAMPO_VALORTOTAL, 
        		registroAux.getCampos().get(CAMPO_VALORUNITARIO));
        registroSub.getCampos().put("CODIGOPROYECTO", 
        		registroAux.getCampos().get("PROYECTO"));
        registroSub.getCampos().put("FUENTEDERECURSO", 
        		registroAux.getCampos().get("FUENTEDERECURSO"));
        registroSub.getCampos().put("REFERENCIA_CNT", 
        		registroAux.getCampos().get("REFERENCIA"));
        registroSub.getCampos().put(CAMPO_AUXILIAR, 
        		registroAux.getCampos().get(CAMPO_AUXILIAR));
        registroSub.getCampos().put(CAMPO_CCTO, 
        		registroAux.getCampos().get(CAMPO_CCTO));
        registroSub.getCampos().put("LOTE", 
        		registroAux.getCampos().get("LOTE"));
    }
    
    
    private void cambiarEspecificacion() {
        String codElemento = registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString();

        Map<String, Object> param = new TreeMap<>();

        param.put(COD_ELEMENTO, codElemento);

        try {
            Registro registro = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            MovimientosControladorUrlEnum.URL1214
                                                                            .getValue())
                                            .getUrl(),
                            param));
            String descripcion = registro != null
                ? registro.getCampos()
                                .get(GeneralParameterEnum.DESCRIPCION.getName())
                                .toString()
                : " ";

            registroSub.getCampos().put(
                            GeneralParameterEnum.ESPECIFICACION.getName(),
                            descripcion);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarElemento() {
        alCambiarElemento(registroSub, registroAux);
        cargarListaSerie();
        cargaMesesVidaUtil(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString());
    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        System.out.println(registroAux);
        
        auxiliar = extraerString(
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_ELEMENTO,
                        registroAux.getCampos().get(CAMPO_CODELEMENTO));
        registroSub.getCampos().put(CAMPO_NOMLARGO,
                        registroAux.getCampos().get(CAMPO_NOMLARGO));
        cargaMesesVidaUtil(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString());
        if ("S".equals(claseMov) && "C".equals(tipoElemento)
            && "N".equals(cptoMov)) {
            registroSub.getCampos().put(CAMPO_VALORUNITARIO,
                            registroAux.getCampos().get(CAMPO_VLRUNITARIOPROM));

        }
        cargaMesesVidaUtil(registroAux.getCampos().get(CAMPO_CODELEMENTO)
                        .toString());
        
//       if(bloqResidual|| verPorcValorResidual) {
//    	   cargaPorcValorResidual(registroAux.getCampos().get(CAMPO_CODELEMENTO)
//                   .toString());
//    	   
//       }
       
    }
    
    public void seleccionarFilaUnidad(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("UNIDAD",
				registroAux.getCampos().get("UNIDAD"));
	}
    
    public void seleccionarFilaUnidadE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get("UNIDAD"));
	}
    
    public void seleccionarFilaFuenteRecursos(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("FUENTEDERECURSO",
				registroAux.getCampos().get("CODIGO"));
	}
    
    public void seleccionarFilaFuenteRecursosE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get("CODIGO"));
	}
    
    public void seleccionarFilaReferenciaCnt(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("REFERENCIA_CNT",
        		registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaReferenciaCntE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaAuxiliarDet(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_AUXILIAR,
        		registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaAuxiliarDetE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaProyectoDet(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CODIGOPROYECTO",
                        registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaProyectoDetE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaCentroCostoDet(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_CCTO,
                        registroAux.getCampos().get(CAMPO_CODIGO));
	}
    
    public void seleccionarFilaCentroCostoDetE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(
				registroAux.getCampos().get(CAMPO_CODIGO));
	}

    public void cambiarElementoC(int rowNum) {
        alCambiarElemento(listaSubdmovimiento.getDatasource().get(rowNum % 10),
                        registroAux);
        cargarListaSerieE();
        cambiarCantidadC(rowNum);
        cambiarValorC(rowNum);
    }
    public void cambiarElementoLoteC(int rowNum) {
        alCambiarElemento(listaSubdmovimiento.getDatasource().get(rowNum % 10),
                        registroAux);
        cargarListaSerieE();
        cambiarCantidadC(rowNum);
        cambiarValorC(rowNum);
    }


    public void cambiarcomponenteC(int rowNum) {
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
                        "COMPONENTE",
                        registroAux.getCampos().get("COMPONENTE"));
    }

    public void cambiarTipoActivoNiifC(int rowNum) {
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
                        "NIIF_TIPO_ACTIVO",
                        registroAux.getCampos().get("NIIF_TIPO_ACTIVO"));
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
                        "VIDAUTILPLACA",
                        registroAux.getCampos().get("MESESVIDAUTIL"));

    }

    public void cambiarTipoActivoNiif() {
        //
        //
    }
    
    public void cambiarElementoIdiC(int rowNum) {}
    /**
     * Evento que se activa al seleccionar la serie en el modal "Nuevo
     * Registro".
     *
     * @param event
     */
    public void seleccionarFilaSerie(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_SERIE,
                        registroAux.getCampos().get(CAMPO_SERIE));
        registroSub.getCampos().put(CAMPO_ELEMENTO,
                        registroAux.getCampos().get(CAMPO_ELEMENTO));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO,
                        registroAux.getCampos().get("VALOR"));
        cambiarValor();
        registroSub.getCampos().put(CAMPO_NOMLARGO,
                        registroAux.getCampos().get(CAMPO_NOMLARGO));
        registroSub.getCampos().put(CAMPO_MARCA,
                        registroAux.getCampos().get(CAMPO_MARCA));
        registroSub.getCampos().put(CAMPO_MODELO,
        		registroAux.getCampos().get(CAMPO_MODELO));        
        registroSub.getCampos().put(CAMPO_SERIEDEV,
                        registroAux.getCampos().get(CAMPO_SERIEDEV));
        
        registroSub.getCampos().put("ESPECIFICACION",
				registroAux.getCampos().get(CAMPO_ESPECIFICACION));
        
        if (bloqResidual|| verPorcValorResidual) {
        	cargaPorcValorResidual(registroSub.getCampos().get("ELEMENTO")
        			.toString());	
        	
        	 if ( verPorcValorResidual ) {
            	 
                 double porVR = Double
                         .parseDouble(SysmanFunciones
                                         .nvl(registroSub.getCampos().get("PORC_VALOR_RESIDUAL"), "0.0")
                                         .toString());
                 double vlrTot = Double 
                 			.parseDouble(SysmanFunciones
                         .nvl(registroSub.getCampos().get(CAMPO_VALORTOTAL), "0.0")
                         .toString());
                 
                 double vlrVR = porVR * vlrTot / 100;
                 
                 if (porVR != 0) {
                	 registroSub.getCampos().put("SALVAMENTO",
                     								new BigDecimal(SysmanFunciones.redondear(vlrVR, redondeoVlr)));	
                 }
                 
             }
        	
        }
         
    }

    /**
     * Evento que se activa al seleccionar la serie en el formulario
     * continuo.
     *
     * @param event
     */
    public void seleccionarFilaSerieE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        System.out.println("----------here here");
        System.out.println(registroAux);
        auxiliar = ((BigDecimal) registroAux.getCampos().get(CAMPO_SERIE))
                        .toString();
        registroSub.getCampos().put(CAMPO_ELEMENTO,
                        registroAux.getCampos().get(CAMPO_ELEMENTO));
        registroSub.getCampos().put(CAMPO_VALORUNITARIO,
                        registroAux.getCampos().get("VALOR"));
        registroSub.getCampos().put(CAMPO_MARCA,
                        registroAux.getCampos().get(CAMPO_MARCA));
        registroSub.getCampos().put(CAMPO_MODELO,
                registroAux.getCampos().get(CAMPO_MODELO));        
        registroSub.getCampos().put("ESPECIFICACION",
        				registroAux.getCampos().get(CAMPO_ESPECIFICACION));
        registroSub.getCampos().put(CAMPO_SERIEDEV,
                        registroAux.getCampos().get(CAMPO_SERIEDEV));
        
        if (bloqResidual|| verPorcValorResidual) {
        	cargaPorcValorResidual(registroSub.getCampos().get("ELEMENTO")
        			.toString());	
        	
        	 if ( verPorcValorResidual ) {
            	 
                 double porVR = Double
                         .parseDouble(SysmanFunciones
                                         .nvl(registroSub.getCampos().get("PORC_VALOR_RESIDUAL"), "0.0")
                                         .toString());
                 double vlrTot = Double 
                 			.parseDouble(SysmanFunciones
                         .nvl(registroSub.getCampos().get(CAMPO_VALORTOTAL), "0.0")
                         .toString());
                 
                 double vlrVR = porVR * vlrTot / 100;
                 
                 if (porVR != 0) {
                	 registroSub.getCampos().put("SALVAMENTO",
                     								new BigDecimal(SysmanFunciones.redondear(vlrVR, redondeoVlr)));	
                 }
                 
             }
        	
        }
        
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantillaDocx
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantillaDocx(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla =  SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visiblePresentarPlantillas = plantilla != null;
    }

    public void cambiarSerieC(int rowNum) {
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
                        CAMPO_MARCA,
                        registroAux.getCampos().get(CAMPO_MARCA));
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
                        CAMPO_SERIEDEV,
                        registroAux.getCampos().get(CAMPO_SERIEDEV));
        listaSubdmovimiento.getDatasource().get(rowNum % 10).getCampos().put(
        		CAMPO_MODELO,
                registroAux.getCampos().get(CAMPO_MODELO));
        cambiarCantidadC(rowNum);
        cambiarValorC(rowNum);
    }

    /**
     * Metodo ejecutado al cambiar el control lblTitulo.
     *
     */
    public void cambiarlblTitulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public void validaAuxiliares() {
    	bloqFuenteMov = false;
    	bloqRefMov = false;
    	bloqAuxMov = false;
    	bloqProyMov = false;
    	bloqCCMov = false;
    	cargaFuente = manejaFuente;
    	cargaReferCnt = false;
    	cargaAuxDet = false;
    	cargaProyDet = false;
    	cargaCCDet = true;
    	cargaLoteDet = true;
    	String bodega = "";
    	
    	if (getParametro("MANEJA SALDO POR BODEGA Y AUXILIARES EN ALMACEN",
        		"NO").equals("SI")) {
	    	if (claseMov.equals("E") || claseMov.equals("T")) {
	    		bodega = registro.getCampos().get(CAMPO_BODEGADESTINO).toString();
	    	} else if (claseMov.equals("S")) {
	    	    bodega = registro.getCampos().get(CAMPO_BODEGAORIGEN).toString();
	    	}
	    	 
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put("BODEGA", bodega);
	         
	        try {
	            Registro rs = RegistroConverter.toRegistro(
	                            requestManager.get(UrlServiceUtil.getInstance()
	                                             .getUrlServiceByUrlByEnumID(
	                                                            MovimientosControladorUrlEnum.URL135013
	                                                                            .getValue())
	                                            .getUrl(), param));
	
	            if (rs != null) {
		            if (rs.getCampos().get("FUENTERECURSO").equals(false)) {
		            	bloqFuenteMov = true;
		            } else {
		            	cargaFuente = true;
		            }
		            
		            if (rs.getCampos().get("REFERENCIA").equals(false)) {
		            	bloqRefMov = true;
		            } else {
		            	cargaReferCnt = true;
		            }
		            
		            if (rs.getCampos().get(CAMPO_AUXILIAR).equals(false)) {
		            	bloqAuxMov = true;
		            } else {
		            	cargaAuxDet = true;
		            }
		            
		            if (rs.getCampos().get("PROYECTO").equals(false)) {
		            	bloqProyMov = true;
		            } else {
		            	proyAuxVisible = true;
		            	cargaProyDet = true;
		            }
		            
		            if (rs.getCampos().get("CENTROCOSTO").equals(false)) {
		            	bloqCCMov = true;
		            } else {
		            	cargaCCDet = true;
		            }
		            
		            if (rs.getCampos().get("LOTE").equals(false)) {
		            	cargaLoteDet = false;
		            } 
	            }
	        } catch (SystemException e) {
	            Logger.getLogger(MovimientosControlador.class
	                            .getName()).log(Level.SEVERE, null, e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
    	}
    }
    /* 7751352_mrosero */
   public void heredarDetallesContrato()
    {
	   msg = null;
	   tipoDocAsociado = SysmanFunciones.toString(registro.getCampos().get("TIPOMOVASOCIADO")); 
	   nroDocAsociado = SysmanFunciones.toString(registro.getCampos().get("MOVASOCIADO")); 
	   proyecto = SysmanFunciones.toString(registro.getCampos().get("CODIGOPROYECTO")); 
	   fuenteR = SysmanFunciones.toString(registro.getCampos().get("FUENTEDERECURSO")); 
	   referencia = SysmanFunciones.toString(registro.getCampos().get("REFERENCIA")); 
	   auxiliarD = SysmanFunciones.toString(registro.getCampos().get("AUXILIAR")); 
	   centroCosto = SysmanFunciones.toString(registro.getCampos().get("CENTRODECOSTO")); 
	   operacion= SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("OPERACION"), " "));
	   auxAlmacen=SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("AUX_ALMACEN"), " "));
	   rueda=SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("RUEDA"), " "));
	   
	         // <CODIGO_DESARROLLADO>
        if (tipoDocAsociado == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1936"));
            return;
        }
        
        try
        {
            msg = ejbAlmacenUno.cargarMovimientoDocAsociado(compania,
                            Integer.parseInt(modulo), claseDocAsMov,
                            claseDocAsMov, Long.parseLong(nroDocAsociado),
                            tipoMov, BigInteger.valueOf(nroMovimiento), 0,
                            proyecto, fuenteR, referencia, auxiliarD, 
                            centroCosto, claseBodOrig);        
        
            /*msg = ejbAlmacenUno.afectarMovimientoDocAsociado(compania,
                            Integer.parseInt(modulo), claseDocAsMov,
                            claseDocAsMov, Long.parseLong(nroDocAsociado),
                            tipoMov, nroMovimiento,
                            SessionUtil.getUser().getCodigo(),
                            proyecto, operacion, auxAlmacen,
                            rueda, fuenteR, referencia,
                            auxiliar, centroCosto); */ //comentado por JM el 1/10/2024 por solicitud de la HU: 7752555 
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        finally {
        	//cargarListaSubdmovimiento(); //comentado por JM el 1/10/2024 por solicitud de la HU: 7752555 
        }

    }
    
    public void seleccionarFilaMovAsociado(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        if(desagregrarElementos) {
        registro.getCampos().put("MOVASOCIADO",
                        registroAux.getCampos().get(CAMPO_NUMERO));
        }
        
        heredarDetallesContrato();
        JsfUtil.agregarMensajeInformativo(msg);
    }
    
    public void seleccionarFilaMovAsociadoComp(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        if(desagregrarElementos) {
        	registro.getCampos().put("MOVASOCIADO",
                        	registroAux.getCampos().get(CAMPO_NUMERO));
        }
        heredarDetallesContrato();
        JsfUtil.agregarMensajeInformativo(msg);
    }

    public void seleccionarFilaTercero(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_TERCERO,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(CAMPO_SUCURSAL,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        nomTercero = extraerString(registroAux.getCampos().get(CAMPO_NOMBRE));
    }

    public void seleccionarFilaResponsableOrigen(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_RESORIGEN,
                        registroAux.getCampos().get(CAMPO_CEDULA));
        registro.getCampos().put(CAMPO_SUCRESORIGEN,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        nomRespOrigen = String
                        .valueOf(registroAux.getCampos().get(CAMPO_NOMBRERES));
        cargarListaSerie();
        cargarListaSerieE();
    }

    public void seleccionarFilaResponsableDestino(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_RESDESTINO,
                        registroAux.getCampos().get(CAMPO_CEDULA));
        registro.getCampos().put(CAMPO_SUCRESDESTINO,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        nomRespDestino = extraerString(
                        registroAux.getCampos().get(CAMPO_NOMBRERES));
        // Inicializa los datos del responsable final
        String nitRecibido = extraerString(
                        registro.getCampos().get(CAMPO_NITRECIBIDO));
        if ((nitRecibido == null) || nitRecibido.isEmpty()) {
            registro.getCampos().put(CAMPO_SUCRECIBIDO,
                            registroAux.getCampos().get(CAMPO_SUCURSAL));
            registro.getCampos().put(CAMPO_NITRECIBIDO,
                            registroAux.getCampos().get(CAMPO_CEDULA));
            nomRecibidoPor = nomRespDestino;
        }
        manejaRecibido = esTraspaso();
    }

    public void seleccionarFilaBodegaOrigen(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_BODEGAORIGEN,
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomBodOrigen = String
                        .valueOf(registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put(CAMPO_CLASE_BODORIGEN,
                        registroAux.getCampos().get(CAMPO_CLASEBOD));
        manejaRecibido = esTraspaso();
        validaAuxiliares();
    }

    public void seleccionarFilaBodegaDestino(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_BODEGADESTINO,
                        registroAux.getCampos().get(CAMPO_CODIGO));

        nomBodDestino = String
                        .valueOf(registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put(CAMPO_CLASE_BODDESTINO,
                        registroAux.getCampos().get(CAMPO_CLASEBOD));
        manejaRecibido = esTraspaso();

        ///
        claseBodDest = registroAux.getCampos().get(CAMPO_CLASEBOD).toString();
        cargarListacmbDependencia();
        validaAuxiliares();
    }

    public void seleccionarFilaCentroDeCosto(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_CCTO,
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomCentroCosto = String
                        .valueOf(registroAux.getCampos().get(CAMPO_NOMBRE));
    }

    public void seleccionarFilaAuxiliar(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_AUXILIAR,
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomAuxiliar = extraerString(registroAux.getCampos().get(CAMPO_NOMBRE));
    }

    public void seleccionarFilaCmbProveedor(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_PROVEEDORCA,
                        registroAux.getCampos().get("NIT"));
        nomProveedor = String
                        .valueOf(registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put(CAMPO_SUCURSALCA,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
    }

    public void seleccionarFilaTipoActivoNiif(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("NIIF_TIPO_ACTIVO",
                        registroAux.getCampos().get("CODIGO_TIPOACTIVO"));
    }

    public void seleccionarFilaTipoActivoNiifE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos().get("CONSECUTIVO"));
    }

    public void seleccionarFilacomponente(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("COMPONENTE",
                        registroAux.getCampos().get("CONSECUTIVO"));

    }

    public void seleccionarFilacomponenteE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = extraerString(registroAux.getCampos().get("CONSECUTIVO"));
        /*
         * registroSub.getCampos().put("COMPONENTE",
         * registroAux.getCampos().get("CONSECUTIVO"));
         */
    }

    public void seleccionarFilaRecibidoPor(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_SUCRECIBIDO,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        registro.getCampos().put(CAMPO_NITRECIBIDO,
                        registroAux.getCampos().get(CAMPO_CEDULA));
        nomRecibidoPor = extraerString(
                        registroAux.getCampos().get(CAMPO_NOMBRERES));
    }

    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTEDERECURSO",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomFuenteRecurso = extraerString(
                        registroAux.getCampos().get(CAMPO_NOMBRE));
    }

    public void seleccionarFilaReferencia(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        nomReferencia = extraerString(
                        registroAux.getCampos().get(CAMPO_NOMBRE));
    }
    
    public void seleccionarFilaUbicacion(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("UBICACION",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        
        registro.getCampos().put("NOMBRE_UBICACION",
                registroAux.getCampos().get(CAMPO_NOMBRE));
        
    }
    
    public void seleccionarFilaUbicacionOrigen(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("UBICACION_ORIGEN",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        
        registro.getCampos().put("NOMBRE_UBICACIONORIGEN",
                registroAux.getCampos().get(CAMPO_NOMBRE));
        
    }
    
    public void seleccionarFilaUbicacionDestino(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put("UBICACION_DESTINO",
                        registroAux.getCampos().get(CAMPO_CODIGO));
        
        registro.getCampos().put("NOMBRE_UBICACIONDESTINO",
                registroAux.getCampos().get(CAMPO_NOMBRE));
        
    }

    /*
     * public void seleccionarFilavidaUtilPlacaNiif(SelectEvent event)
     * { registroAux = (Registro) event.getObject();
     * registroSub.getCampos().put("VIDAUTILPLACANIIF",
     * registroAux.getCampos().get("MESESVIDAUTIL")); }
     * 
     * public void seleccionarFilavidaUtilPlacaNiifE(SelectEvent
     * event) {
     * 
     * registroAux = (Registro) event.getObject(); auxiliar =
     * extraerString( registroAux.getCampos().get("MESESVIDAUTIL"));
     * registroSub.getCampos().put("VIDAUTILPLACANIIF",
     * registroAux.getCampos().get("MESESVIDAUTIL")); }
     * 
     * public void seleccionarFilaVidaUtilPlaca(SelectEvent event) {
     * 
     * registroAux = (Registro) event.getObject();
     * registroSub.getCampos().put("VIDAUTILPLACA",
     * registroAux.getCampos().get("MESESVIDAUTIL")); } public void
     * seleccionarFilaVidaUtilPlacaE(SelectEvent event) { registroAux
     * = (Registro) event.getObject(); auxiliar = extraerString(
     * registroAux.getCampos().get("MESESVIDAUTIL"));
     * registroSub.getCampos().put("VIDAUTILPLACA",
     * registroAux.getCampos().get("MESESVIDAUTIL")); }
     */
    public void onRowSelectTipoMovimiento(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_TIPOMOV,
                        registroAux.getCampos().get(CAMPO_CODIGO));
    }

    public void cambiarValorC(int rowNum) {
        calcularVlrTotal(listaSubdmovimiento.getDatasource().get(rowNum % 10));
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
        calcularVlrImpconsumo(listaSubdmovimiento.getDatasource().get(rowNum % 10));

    }

    public void cambiarPorcentajeIVAC(int rowNum) {
        calcularVlrTotal(listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiarEntrega() {
        calcularNiifValorTotal(registroSub);
    }

    public void cambiarEntregaC(int rowNum) {
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiarComprobacionC(int rowNum) {
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiarPreparacionC(int rowNum) {
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiarInstalacion() {
        calcularNiifValorTotal(registroSub);
    }

    public void cambiarPreparacion() {
        calcularNiifValorTotal(registroSub);
    }

    public void cambiarComprobacion() {
        calcularNiifValorTotal(registroSub);
    }

    public void cambiarInstalacionC(int rowNum) {
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiaraplicaNiifC(int rowNum) {
        calcularNiifValorTotal(
                        listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    public void cambiarTipoMovAsociado1() {
        claseDocAsMov = extraerString(registro.getCampos()
                        .get(MovimientosControladorEnum.PARAM9.getValue()));
        cargarListaMovAsociado();
        cargarListaMovAsociadoComp();
    }

    public void cambiarValor() {
        calcularVlrTotal(registroSub);
        calcularNiifValorTotal(registroSub);
        calcularVlrImpconsumo(registroSub);
    }

    private void cargaMesesVidaUtil(String elemento) {

        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

            Registro registroMesesEle = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MovimientosControladorUrlEnum.URL1218
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametros));

            registroSub.getCampos().put("VIDAUTILPLACA",
                            registroMesesEle.getCampos().get("MESESVIDAUTIL"));
            registroSub.getCampos().put("VIDAUTILPLACANIIF",
                            registroMesesEle.getCampos().get("MESESVIDAUTIL"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private void cargaPorcValorResidual(String elemento) {

        try {

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
            parametros.put("DIGITOS",digPorcValorResidual);

            Registro registroMesesEle = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID( MovimientosControladorUrlEnum.URL112191
                                                                                    .getValue())                                                                            
                                                                            .getUrl(),
                                                            parametros));

            registroSub.getCampos().put("PORC_VALOR_RESIDUAL",
                            registroMesesEle.getCampos().get("PORC_VALOR_RESIDUAL"));
         }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarPorcentajeIVA() {
           calcularVlrTotal(registroSub);
           calcularValoresAIU(registroSub);
    }
    
    public void cambiarporcentajeImpConsumo() {
    	calcularVlrTotal(registroSub);
    	calcularVlrImpconsumo(registroSub);
    	calcularValoresAIU(registroSub);
    }
    
    public void cambiarporcentajeImpConsumoC(int rowNum) {
    	calcularVlrImpconsumo(listaSubdmovimiento.getDatasource().get(rowNum % 10));
    	calcularValoresAIU(listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            estadoAlmacen = ejbSysmanUtil.verificarEstadoPeriodoMensual(
                            compania, Integer.parseInt(anoMov),
                            Integer.parseInt(mesMov), Integer.parseInt(modulo),
                            1);
            bloqueadoPeriodo = "A".equals(estadoAlmacen);
            permiteModificar = bloqueadoPeriodo;
            if (!bloqueadoPeriodo) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2008"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
     		try {
     			manejaUbicacion = consultarParametro(
     					"MANEJA DEPENDENCIA-UBICACION", false);

     			visibleUbicacion = (manejaUbicacion.equals("SI") && (claseMov.equals("E") || claseMov.equals("S"))) ?true:false;
     			
     			visibleUbicacionTras = (manejaUbicacion.equals("SI") && (claseMov.equals("T") && "T".equals(cptoMov))) ?true:false;
                if(visibleUbicacionTras== true ) {
                	visibleUbicacion = false;
                }
                	
     		} catch (SystemException e) {
     			logger.error(e.getMessage(), e);
     			JsfUtil.agregarMensajeError(e.getMessage());
     		}
     		
     		
     		desagregrarElementos = SysmanFunciones.nvlStr(
    				obtenerParametro("DESAGREGAR ELEMENTOS EN ENTRADA SEGUN LOTE", true), "NO").equals("SI")?true:false;;
     		// </CODIGO_DESARROLLADO>
     		     		     		
     	}

    

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        registroSub = new Registro(new HashMap<String, Object>());
        precargarRegistro();
        evaluarDetalle();
        cargarListaUbicacion(); 
        cargarListaUbicacionOrigen(); 
        cargarListaUbicacionDestino(); 
        
        if (manejaFuente) {
        	validarFuenteRecursoDet();
        }

        if ("S".equals(claseMov) && "C".equals(tipoElemento)
            && "SI".equals(copiarSalidas)) {
            visibleCopiar = true;

            if (registro.getCampos().get("REGISTRADO") != null
                && (boolean) registro.getCampos().get("REGISTRADO")) {
                activoCopiar = true;
            }
            else {
                activoCopiar = false;
            }
        }
//validar aqui mrosero
        if (claseDocAsMov != null) {
            registro.getCampos().put(
                            MovimientosControladorEnum.PARAM9.getValue(),
                            claseDocAsMov);
        }
        validarControlesGraficos();
        if (registro.getCampos().get(CAMPO_VLRUNITARIO_SINIVA) == null) {
            valorAntesIVA = false;
        }
        else {
            valorAntesIVA = !"0".equals(
                            registro.getCampos().get(CAMPO_VLRUNITARIO_SINIVA));
        }
        mostrarValorAntesIVA = esEntradaPorCompra();
        valorAntesIVA = extraerBoolean(
                        registro.getCampos().get(CAMPO_VLRUNITARIO_SINIVA));
        if (ACCION_INSERTAR.equals(accion)) {
            verDocumentos = false;
            registro.getCampos().put(CAMPO_REGISTRADO, "0");
            tieneDetalles = false;
            nomTercero = "";
            nomProveedor = "";
            nomRecibidoPor = "";
            nomBodOrigen = "";
            nomBodDestino = "";
            nomRespDestino = "";
            nomRespOrigen = "";
            nomCentroCosto = "";
            ajusteCentavos = "0";
            totalConAjuste = "0";
            subtotal = "0";
            registro.getCampos().put(CAMPO_AJUSTECENTAVOS, "0");
            registro.getCampos().put("AJUSTE_DEC_MANUAL", "0");
            registro.getCampos().put(CAMPO_VALORIVA, "0");
            registro.getCampos().put(CAMPO_VALORTOTALCONIVA, "0");
            registro.getCampos().put("MOVASOCIADO", "0");
            registro.getCampos().put("VALORDOCASOCIADO", "0");
            registro.getCampos().put(CAMPO_PROVEEDORCA,
                            SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put(CAMPO_SUCURSALCA,
                            SysmanConstantes.CONS_SUCURSAL);
            registro.getCampos().put(CAMPO_AUXILIAR,
                            SysmanConstantes.CONS_AUXILIAR);
            registro.getCampos().put(CAMPO_REGISTRADO, "0");
            registro.getCampos().put("FUENTEDERECURSO",
                            SysmanConstantes.CONS_FUENTE);
            registro.getCampos().put("REFERENCIA",
                            SysmanConstantes.CONS_FUENTE);
            if (manvlrdefcodproy) {
            	registro.getCampos().put("CODIGOPROYECTO", 
						"9999999999999999");
            }
            
            try {
                if (listaBodegaOrigen != null) {
                    Map<String, Object> map = new TreeMap<>();
                    map.put(MovimientosControladorEnum.PARAM8.getValue(), 1);
                    Registro regBodega;
                    regBodega = listaBodegaOrigen.getRegistroUnico(map);

                    registro.getCampos().put(CAMPO_BODEGAORIGEN,
                                    regBodega.getCampos().get(CAMPO_CODIGO));
                    nomBodOrigen = extraerString(
                                    regBodega.getCampos().get(CAMPO_NOMBRE));
                    registro.getCampos().put(CAMPO_CLASE_BODORIGEN,
                                    regBodega.getCampos().get(CAMPO_CLASEBOD));
                }
                if (listaBodegaDestino != null) {
                    Map<String, Object> map = new TreeMap<>();
                    map.put(MovimientosControladorEnum.PARAM8.getValue(), 1);
                    Registro regBodega = listaBodegaDestino
                                    .getRegistroUnico(map);
                    registro.getCampos().put(CAMPO_BODEGADESTINO,
                                    regBodega.getCampos().get(CAMPO_CODIGO));
                    nomBodDestino = extraerString(
                                    regBodega.getCampos().get(CAMPO_NOMBRE));
                    registro.getCampos().put(CAMPO_CLASE_BODDESTINO,
                                    regBodega.getCampos().get(CAMPO_CLASEBOD));
                }
                if (listaAuxiliar != null) {
                    cargarNombreAuxiliar();
                }
                if (listaFuenteRecurso != null) {
                    cargarNombreFuenteRecurso();
                }
                if (listaReferencia != null) {
                    cargarNombreReferencia();
                }
                if (listaProyecto != null) {
                    cargarNombreProyecto();
                }
            }
            catch (SystemException e) {
                Logger.getLogger(MovimientosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CAMPO_NUMERO)) {
                nroMovimiento = Long.parseLong(SysmanFunciones.toString(
                                registro.getCampos().get(CAMPO_NUMERO)));
                registroSub.getCampos().put(CAMPO_SALDOCANT, "0");
                registroSub.getCampos().put(CAMPO_VALORUNITARIO, "0");
                registroSub.getCampos().put(CAMPO_PORCDESCUENTO, "0");
                registroSub.getCampos().put(CAMPO_PORCIVA, "0");
                registroSub.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA, "0");
                registroSub.getCampos().put(CAMPO_VALORTOTAL, "0");
                registroSub.getCampos().put(CAMPO_SALDOELEMENTO, "0");
                registroSub.getCampos().put(CAMPO_VALORSALDO, "0");
                registroSub.getCampos().put(CAMPO_VLRUNITARIOPROM, "0");
                registroSub.getCampos().put(CAMPO_IND_REG, "0");
                registroSub.getCampos().put(CAMPO_VALORANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_CANTANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_VLRUNITANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_VLRAJUSTADO, "0");
                registroSub.getCampos().put(CAMPO_SALDOKARDEX, "0");
                registroSub.getCampos().put(CAMPO_INCREMENAJUS, "0");
                registroSub.getCampos().put(CAMPO_PEDIDO, "0");
                registroSub.getCampos().put("ITEM", "0");
                registroSub.getCampos().put(CAMPO_COSTOSALIDA, "0");
                registroSub.getCampos().put(CAMPO_COSTOSALIDAAJ, "0");
                registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS, "0");
                registroSub.getCampos().put(CAMPO_AJUSTECENTAVOS1, "0");
                registroSub.getCampos().put(CAMPO_CANTIDADDOCAS, "0");
                registroSub.getCampos().put(CAMPO_VALORDOCAS, "0");
                registroSub.getCampos().put(CAMPO_INFINMUEBLE, "0");
                registroSub.getCampos().put(CAMPO_PLACAANTERIOR, "0");
                registroSub.getCampos().put(CAMPO_REGISTRADOBI, "0");
                registroSub.getCampos().put(CAMPO_VALORBASE, "0");
                registroSub.getCampos().put(CAMPO_VALORIVA, "0");
                registroSub.getCampos().put(CAMPO_PORCIVAUNI, "0");
                registroSub.getCampos().put(CAMPO_VALORTOTALCONIVA, "0");
                registroSub.getCampos().put("CANTIDADAFECTADA", "0");
                registroSub.getCampos().put("SALDO_PEPS", "0");
                registroSub.getCampos().put("NIIF_VALOR_BASE", "0");
                registroSub.getCampos().put("DETERIORO", "0");
                registroSub.getCampos().put("NIIF_VALOR_TOTAL", "0");
                registroSub.getCampos().put("INSTALACION", "0");
                registroSub.getCampos().put("PORC_VALOR_RESIDUAL", "0");
                registroSub.getCampos().put("SALVAMENTO", "0");
                registroSub.getCampos().put("PREPARACION", "0");
                registroSub.getCampos().put("ENTREGA", "0");
                registroSub.getCampos().put("COMPROBACION", "0");
                registroSub.getCampos().put("APLICANIIF", true);
                registroSub.getCampos().put("ESTADO", "B");
                registroSub.getCampos().put("FUENTEDERECURSO", 
                		registro.getCampos().get("FUENTEDERECURSO"));
                registroSub.getCampos().put("REFERENCIA_CNT", 
                		registro.getCampos().get("REFERENCIA"));
                registroSub.getCampos().put(CAMPO_AUXILIAR, 
                		registro.getCampos().get(CAMPO_AUXILIAR));
                registroSub.getCampos().put("CODIGOPROYECTO", 
                		registro.getCampos().get("CODIGOPROYECTO"));
                registroSub.getCampos().put(CAMPO_CCTO, 
                		registro.getCampos().get(CAMPO_CCTO));

                if (obligaCampos) {
                    registroSub.getCampos()
                                    .put(GeneralParameterEnum.ESPECIFICACION
                                                    .getName(), "N.A.");
                    registroSub.getCampos().put(
                                    GeneralParameterEnum.MARCA.getName(),
                                    "N.A.");
                    registroSub.getCampos().put(
                                    GeneralParameterEnum.MODELO.getName(),
                                    "N.A.");
                    registroSub.getCampos().put(CAMPO_SERIEDEV, "N.A.");
                }
                
            }
            
            nomProyecto = SysmanFunciones.nvl(registro.getCampos().get(CAMPO_NOMBREPROYECTO), "")
    				.toString();
        }
        //
        estado = registro.getCampos().get(CAMPO_BODEGADESTINO)
                        .toString();
        
        if (estado.equals("40") || estado.equals("50") || estado.equals("60")) {

            validarEstado = true;
            registroSub.getCampos().put("ESTADO", "M");

        }
        else {
            validarEstado = false;
        }
        consecutivoOrfeo = getParametro("MANEJA CONSECUTIVO ORFEO EN ALMACEN",
                        "NO");
        manOrfeo = consecutivoOrfeo.equals("SI") ? true : false;
        
        ckAIU = (Boolean) SysmanFunciones.nvl(registro.getCampos().get("AIU"), false);
		if(ckAIU && manejaAIU ){
		    		
		    		calcularPorAIU();		
		    		activoAIU = true;
		    		
		    	}
			else {
		   activoAIU = false;
				}
    	
      
        validaAuxiliares();
        // estadoVisible();
        cargarNombreRecibido();
        cargarListaResponsableOrigen();
        cargarListaresponsableDestino();
        cargarListaElementoAux();
        cargarListaPlantillaDocx();
        manejaRecibido = esTraspaso();
        setBloqueado(ACCION_INSERTAR.equals(accion));
        setObligaCampos(obligaCampos);
        elemento = null;
        // </CODIGO_DESARROLLADO>
        
        System.out.println(registroSub.getCampos());
    }

    public void cambiarFecha() {
        int vigenciaFecha = SysmanFunciones
                        .ano((Date) registro.getCampos().get(CAMPO_FECHA));
        int mesFecha = SysmanFunciones
                        .mes((Date) registro.getCampos().get(CAMPO_FECHA));
        boolean fechaValida = false;
        if ((vigenciaFecha == Integer.parseInt(anoMov))
            && (mesFecha == Integer.parseInt(mesMov))) {
            fechaValida = true;
        }

        if (!fechaValida) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2012"));
            registro.getCampos().put(CAMPO_FECHA, null);
            return;
        }

        if (SysmanFunciones.comparaFechas(
                        (Date) registro.getCampos().get(CAMPO_FECHA),
                        ultFechaMov)
            && (cptoMov.compareTo("II") != 0)
            && (parPermiteMovAntAlm.compareTo("SI") != 0)) {
            try {
                String mensajeFecha = idioma.getString("TB_TB2013");
                mensajeFecha = mensajeFecha.replace("s$fecha$s", SysmanFunciones
                                .convertirAFechaCadena(ultFechaMov));
                JsfUtil.agregarMensajeAlerta(mensajeFecha);
                registro.getCampos().put(CAMPO_FECHA, null);
            }
            catch (ParseException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2014"));
            }
        }

    }

    public void cambiarCantidad() {
        calcularVlrTotal(registroSub);
        calcularVlrImpconsumo(registroSub);
        actualizarSaldoCantidad(registroSub, existenciaElemento);
        calcularVlrAIU(registroSub);
        calcularValoresAIU(registroSub);
    }

    /**
     * Acciones ejecutadas al cambiar el campo <b>CANTIDAD</b> en el
     * subformulario <i>Detalle Movimiento</i>
     * 
     * @param rowNum
     */
    public void cambiarCantidadC(int rowNum) {
        calcularVlrTotal(listaSubdmovimiento.getDatasource().get(rowNum % 10));

        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(CAMPO_CODELEMENTO,
                            listaSubdmovimiento.getDatasource().get(rowNum % 10)
                                            .getCampos().get(CAMPO_ELEMENTO));

            existenciaElemento = Double
                            .parseDouble(new Registro(listaElementoE
                                            .getRegistroUnico(parametros)
                                            .getCampos()).getCampos()
                                                            .get("EXISTENCIA")
                                                            .toString());

            actualizarSaldoCantidadC(listaSubdmovimiento.getDatasource()
                            .get(rowNum % 10), existenciaElemento);
            calcularVlrAIU(listaSubdmovimiento.getDatasource().get(rowNum % 10));
            calcularValoresAIU(listaSubdmovimiento.getDatasource().get(rowNum % 10));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void calcularNiifValorTotal(Registro auxilRegistro) {
        double valorUnit = Double
                        .parseDouble(SysmanFunciones.nvl(
                                        auxilRegistro.getCampos().get(
                                                        CAMPO_VALORUNITARIO),
                                        "0.0").toString());
        double preparacion = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxilRegistro.getCampos().get(
                                                        "PREPARACION"), "0.0")
                                        .toString());
        double entrega = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxilRegistro.getCampos()
                                                        .get("ENTREGA"), "0.0")
                                        .toString());
        double instalacion = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxilRegistro.getCampos().get(
                                                        "INSTALACION"), "0.0")
                                        .toString());
        double comprobacion = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxilRegistro.getCampos().get(
                                                        "COMPROBACION"), "0.0")
                                        .toString());
        auxilRegistro.getCampos().put("NIIF_VALOR_TOTAL", new BigDecimal(
                        SysmanFunciones.redondear(valorUnit + preparacion
                            + entrega + instalacion + comprobacion, redondeoVlr)));
    }

    /**
     * Calculo del valor unitario antes y despues de IVA, y el
     * valor total, para su visualizacion en el formulario.
     *
     * @param auxRegistro
     * registro actual
     */
    public void calcularVlrTotal(Registro auxRegistro) {
        double varCantidad = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxRegistro.getCampos().get(
                                                        CAMPO_CANTIDAD), "0.0")
                                        .toString());
        double porcentajeIva = Double
                        .parseDouble(SysmanFunciones
                                        .nvl(auxRegistro.getCampos().get(
                                                        CAMPO_PORCIVA), "0.0")
                                        .toString());
        
        double porcentajeImpConsumo = Double
                .parseDouble(SysmanFunciones
                                .nvl(auxRegistro.getCampos().get(
                                                CAMPO_PORCIMPCONSUMO), "0.0")
                                .toString());
        
        double unitarioDespuesIVA;
        double unitarioAntesIVA;
        double varVlrUnitario;
        
        if (!validarCamposCalculo(auxRegistro)) {
            return;
        }
        
        if (mostrarValorAntesIVA) {
            if (valorAntesIVA) {
                unitarioAntesIVA = SysmanFunciones.redondear(
                                Double.parseDouble(auxRegistro.getCampos()
                                                .get(CAMPO_VLRUNITARIO_ANTESIVA)
                                                .toString()),
                                redondeoVlr); //JM AQUI 
                unitarioDespuesIVA = unitarioAntesIVA
                		 		* (1 + (porcentajeIva / 100) + (porcentajeImpConsumo / 100));
                
                auxRegistro.getCampos().put(CAMPO_VALORUNITARIO,
                                new BigDecimal(unitarioDespuesIVA));
            }
            else {
                unitarioDespuesIVA = SysmanFunciones
                                .redondear(Double.parseDouble(auxRegistro
                                                .getCampos()
                                                .get(CAMPO_VALORUNITARIO)
                                                .toString()), redondeoVlr);
                unitarioAntesIVA = SysmanFunciones.redondear(unitarioDespuesIVA
                    / (1 + (porcentajeIva / 100)), redondeoVlr);
                auxRegistro.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA,
                                new BigDecimal(unitarioAntesIVA));
            }
            auxRegistro.getCampos().put(CAMPO_VALORTOTAL,
                            new BigDecimal(SysmanFunciones.redondear(
                                            varCantidad * unitarioDespuesIVA,
                                            redondeoVlr)));
            auxRegistro.getCampos().put("NIIF_VALOR_TOTAL",
                            new BigDecimal(varCantidad * unitarioDespuesIVA));
        }
        else {
            unitarioDespuesIVA = SysmanFunciones
                            .redondear(Double.parseDouble(auxRegistro
                                            .getCampos()
                                            .get(CAMPO_VALORUNITARIO)
                                            .toString()), redondeoVlr);
            /*
             * Calculo del valor unitario sin IVA para impedir que
             * quede en ceros y afecte calculos posteriores
             */
            unitarioAntesIVA = unitarioDespuesIVA / (1 + (porcentajeIva / 100));
            auxRegistro.getCampos().put(CAMPO_VLRUNITARIO_ANTESIVA,
                            new BigDecimal(unitarioAntesIVA));
            varVlrUnitario = Double.parseDouble(auxRegistro.getCampos()
                            .get(CAMPO_VALORUNITARIO).toString());
            auxRegistro.getCampos().put(CAMPO_VALORTOTAL,
                            new BigDecimal(SysmanFunciones.redondear(
                                            varCantidad * varVlrUnitario, redondeoVlr)));
        }
        /*
         * Evalua si el movimiento es una correccion de valor porque
         * en este caso el valor total es igual al valor unitario asi
         * la cantidad sea cero.
         */
        if ("CR".equals(cptoMov)) {
            auxRegistro.getCampos().put(CAMPO_VALORTOTAL,
                            new BigDecimal(SysmanFunciones
                                            .redondear(Double.parseDouble(
                                                            auxRegistro.getCampos()
                                                                            .get(CAMPO_VALORUNITARIO)
                                                                            .toString()),
                                            		redondeoVlr)));
        }
        
        if ( verPorcValorResidual ) {
        	 
            double porVR = Double
                    .parseDouble(SysmanFunciones
                                    .nvl(auxRegistro.getCampos().get("PORC_VALOR_RESIDUAL"), "0.0")
                                    .toString());
            double vlrTot = Double 
            			.parseDouble(SysmanFunciones
                    .nvl(auxRegistro.getCampos().get(CAMPO_VALORTOTAL), "0.0")
                    .toString());
            
            double vlrVR = porVR * vlrTot / 100;
            
            if (porVR != 0) {
                auxRegistro.getCampos().put("SALVAMENTO",
                								new BigDecimal(SysmanFunciones.redondear(vlrVR, redondeoVlr)));	
            }
            
        }
       
        		//vlrVR.toString());
//                new BigDecimal(SysmanFunciones
//                                .redondear(Double.parseDouble(
//                                                auxRegistro.getCampos()
//                                                                .get(CAMPO_VALORUNITARIO)
//                                                                .toString()),
//                                                2)));
        

        
        double porcIva = Double
                .parseDouble(SysmanFunciones
                        .nvl(auxRegistro.getCampos().get(
                       		 CAMPO_PORCIVA), "0.0")
                        .toString());
   	 
        double valorIva = SysmanFunciones.redondear((unitarioAntesIVA * (porcIva / 100)),redondeoVlr) * varCantidad;  
        
        auxRegistro.getCampos().put("VALORIVA",
        		new BigDecimal(SysmanFunciones.redondear(valorIva, redondeoVlr)));
        
    }
    
    private void calcularVlrImpconsumo(Registro auxRegistro) {
    	double varCantidad = Double
                .parseDouble(SysmanFunciones
                                .nvl(auxRegistro.getCampos().get(
                                                CAMPO_CANTIDAD), "0.0")
                                .toString());

    	 double porcentajeImpConsumo = Double
                 .parseDouble(SysmanFunciones
                                 .nvl(auxRegistro.getCampos().get(
                                                 CAMPO_PORCIMPCONSUMO), "0.0")
                                 .toString());
    	 double unitarioAntesIVA = SysmanFunciones.redondear(
                 Double.parseDouble(auxRegistro.getCampos()
                                 .get(CAMPO_VLRUNITARIO_ANTESIVA)
                                 .toString()),
                 redondeoVlr);
    	 
    	 double valorImpConsumo = unitarioAntesIVA
                 * (porcentajeImpConsumo / 100)* varCantidad;
    	 
             auxRegistro.getCampos().put(CAMPO_VLRIMPCONSUMO,
                             new BigDecimal(valorImpConsumo));
    	   	 
	}
    
    private void calcularVlrAIU(Registro auxRegistro) {
    	double varCantidad = Double
                .parseDouble(SysmanFunciones
                                .nvl(auxRegistro.getCampos().get(
                                                CAMPO_CANTIDAD), "0.0")
                                .toString());

    	 porcentajeAIU = Double
                 .parseDouble(SysmanFunciones
                                 .nvl(auxRegistro.getCampos().get(
                                		 CAMPO_PORCAIU), "0.0")
                                 .toString());
    	 double unitarioAntesIVA = SysmanFunciones.redondear(
                 Double.parseDouble(auxRegistro.getCampos()
                                 .get(CAMPO_VLRUNITARIO_ANTESIVA)
                                 .toString()),
                 redondeoVlr);
    	 
    	 double valorAIU = unitarioAntesIVA
                 * (porcentajeAIU / 100)* varCantidad;
    	 
            auxRegistro.getCampos().put(CAMPO_VLRAIU,
            		new BigDecimal(valorAIU));
         
             
    	   	 
	}
    
    private boolean calcularPorAIU() {
    	administracion = new Double(SysmanFunciones.nvl(SysmanFunciones.toString(registro.getCampos().get("ADMINISTRACION")),"0"));
    	imprevistos = new Double(SysmanFunciones.nvl(SysmanFunciones.toString(registro.getCampos().get("IMPREVISTOS")),"0"));
    	utilidades = new Double(SysmanFunciones.nvl(SysmanFunciones.toString(registro.getCampos().get("UTILIDADES")),"0"));
    	porcentajeAIU =  administracion + imprevistos + utilidades;
    	
    	if(porcentajeAIU > 100) {
    		JsfUtil.agregarMensajeAlerta("La suma de porcentajes AIU no debe superar 100%.");
    		return true;
    	}else {
    		registroSub.getCampos().put(CAMPO_PORCAIU, SysmanFunciones.redondear(porcentajeAIU,2));
    	}
    	return false;
    }

    public void alCambiarElemento(Registro reg, Registro auxReg) {
        elemento = extraerString(auxReg.getCampos().get(CAMPO_CODELEMENTO));
        if (SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
                        CAMPO_CODELEMENTO)) {
            return;
        }
        reg.getCampos().put(CAMPO_NOMLARGO,
                        auxReg.getCampos().get(CAMPO_NOMLARGO));
        reg.getCampos().put("IDENTIFICADOR",
                        auxReg.getCampos().get("IDENTIFICADOR"));
        reg.getCampos().put(CAMPO_SERIE, auxReg.getCampos().get(CAMPO_SERIE));
        Object codigoElemento = auxReg.getCampos().get(CAMPO_CODELEMENTO);
        if ("C".equals(tipoElemento)) {
            reg.getCampos().put(CAMPO_VALORUNITARIO,
                            getValorUnitarioConsumo(codigoElemento));
            existenciaElemento = extraerDouble(
                            auxReg.getCampos().get("EXISTENCIA").toString());
            actualizarSaldoCantidad(reg, existenciaElemento);
        } else if ("E".equals(claseMov) && !("T".equals(cptoMov) || "TG".equals(cptoMov) || "R".equals(cptoMov) || "CR".equals(cptoMov))) {

            reg.getCampos().put(CAMPO_VALORUNITARIO,(auxReg.getCampos().get("VLRUNITARIOPROM")));
            reg.getCampos().put("VLRUNITARIOPROM",(auxReg.getCampos().get("VLRUNITARIOPROM")));
            reg.getCampos().put("NIIF_VALOR_TOTAL",(auxReg.getCampos().get("VLRUNITARIOPROM")));
        }else {
        	reg.getCampos().put(CAMPO_VALORUNITARIO,0);
            reg.getCampos().put("VLRUNITARIOPROM",0);
        }
        if (Arrays.asList("D", "N").contains(auxReg.getCampos().get("TIPO"))) {
            reg.getCampos().put("FECHAADEPRECIAR", new Date());
        }
        else {
            reg.getCampos().put("FECHAADEPRECIAR", "");
        }
        int numeroAnio = SysmanFunciones
                        .ano((Date) registro.getCampos().get(CAMPO_FECHA));
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), codigoElemento);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoMov);
        param.put(GeneralParameterEnum.ANO.getName(), numeroAnio);
        Registro regCuentas = null;
        String urlEnumId = MovimientosControladorUrlEnum.URL167968.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            Parameter parameter = requestManager.get(url, param);
            regCuentas = RegistroConverter.toRegistro(parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (regCuentas != null) {
            reg.getCampos().put("CUENTACREDITO",
                            regCuentas.getCampos().get("CUENTACREDITO"));
            reg.getCampos().put("CUENTADEBITO",
                            regCuentas.getCampos().get("CUENTADEBITO"));
        }
    }

    /**
     * Trae el valor unitario para elementos de consumo.
     *
     * @param codigoElemento
     * Codigo del elemento.
     * @return Valor unitario.
     */
    private Object getValorUnitarioConsumo(Object codigoElemento) {
        BigDecimal valorUnitario;
        BigDecimal valorUnitarioProm = new BigDecimal(String
                        .valueOf(getValorUnitarioPromedio(codigoElemento)));
        String redondeoVlrUnitEC = getParametro(
                        "REDONDEAR VALOR UNITARIO ENTRADA CONSUMO", "NO");
        int digitosRedondeo = Integer.parseInt(getParametro(
                        "DIGITOS REDONDEO VALOR UNITARIO ENTRADA CONSUMO",
                        "0"));
        if ("S".equals(claseMov)) { // Si es un Salida
            Registro regPEPS = getPeps(codigoElemento);
            String manejasPeps = getParametro("MANEJA PEPS EN CONSUMO ALMACEN",
                            "NO");
            if ("SI".equals(manejasPeps)) {
                valorUnitario = regPEPS != null
                    ? new BigDecimal(
                                    extraerString(regPEPS.getCampos().get(
                                                    GeneralParameterEnum.VALORUNITARIO
                                                                    .getName())))
                    : BigDecimal.ZERO;
            }
            else {
                valorUnitario = valorUnitarioProm;
            }
            if ("SI".equals(redondeoVlrUnitEC)) {
                valorUnitario = SysmanFunciones.redondear(valorUnitario,
                                digitosRedondeo);
            }
			/*
			 * else { valorUnitario = SysmanFunciones.redondear(valorUnitario, 0); }
			 */
        }
        else {
            if ("SI".equals(redondeoVlrUnitEC)) {
                valorUnitario = SysmanFunciones.redondear(valorUnitarioProm,
                                digitosRedondeo);
            }
            else {
                valorUnitario = valorUnitarioProm;
            }
        }
        return valorUnitario;
    }

    /**
     * Obtiene los datos necesarios para la salida, cuando maneja
     * PEPS.
     *
     * @param codigoElemento
     * Codigo del elemento de consumo.
     * @return registro
     */
    private Registro getPeps(Object codigoElemento) {
        Registro regPEPS = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(MovimientosControladorEnum.PARAM11.getValue(),
                            tipoElemento);
            param.put(GeneralParameterEnum.ELEMENTO.getName(), codigoElemento);
            String urlEnumId = MovimientosControladorUrlEnum.URL16771
                            .getValue();
            String url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
            Parameter parameter = requestManager.get(url, param);
            regPEPS = RegistroConverter.toRegistro(parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return regPEPS;
    }
    
    public void validarFuenteRecursoDet() {
    	bloqueaFuente = false;
    	if("E".equals(claseMov) || "S".equals(claseMov)) {
    		bloqueaFuente = true;
    		if(ACCION_MODIFICAR.equals(accion)){
    		registroSub.getCampos().put("FUENTEDERECURSO", registro.getCampos().get("FUENTEDERECURSO"));
    		}
    	}
    }

    /**
     * Trae el valor unitario promedio de un elemento del inventario.
     *
     * @param codigoElemento
     * Codigo del elemento.
     * @return valor unitario promedio
     */
    private Object getValorUnitarioPromedio(Object codigoElemento) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(),
                        codigoElemento);
        param.put(MovimientosControladorEnum.PARAM0.getValue(), tipoElemento);
        Registro regInventario = null;
        String urlEnumId = MovimientosControladorUrlEnum.URL165875.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            Parameter parameter = requestManager.get(url, param);
            regInventario = RegistroConverter.toRegistro(parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (regInventario != null) {
            return regInventario.getCampos().get("VLRUNITARIOPROM");
        }
        else {
            return BigDecimal.ZERO;
        }
    }
    
    public void actualizarUbicacion(){
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
    	param.put(GeneralParameterEnum.NUMERO.getName(),
                registro.getCampos().get(
                                GeneralParameterEnum.NUMERO.getName()));   	
    	
    	param.put(GeneralParameterEnum.CODIGO.getName(),
                registro.getCampos().get(
                                GeneralParameterEnum.UBICACION.getName()));
    	param.put("UBICACION_ORIGEN",
                registro.getCampos().get(
                                "UBICACION_ORIGEN"));
    	
    	param.put("UBICACION_DESTINO",
                registro.getCampos().get(
                                "UBICACION_DESTINO"));
    	/**
         * Se adiciona este indicador en cero (0) para que al
         * realizar una actualizacion se ejecute el TRIGGER
         * <code>AIUD_D_MOVIMIENTO</code> y realice la validacion
         * de la cantidad con el saldo disponible en inventario
         */
    	//param.put(CAMPO_IND_REG, 0);
    	
    	Parameter parameter = new Parameter();

        parameter.setFields(param);

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosControladorUrlEnum.URL119030
                                                        .getValue());
        
        
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
           
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        
        if(visibleUbicacionTras == true ) {
        	Map<String, Object> paramUbicaod = new TreeMap<>();
        	paramUbicaod.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        	paramUbicaod.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
        	paramUbicaod.put(GeneralParameterEnum.NUMERO.getName(),
                    registro.getCampos().get(
                                    GeneralParameterEnum.NUMERO.getName()));
    		Parameter parameterDevolutivo = new Parameter();
    		parameterDevolutivo.setFields(paramUbicaod);
    		UrlBean urlUpdateDevolutivoUbi = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						MovimientosControladorUrlEnum.URL119031
    						.getValue());
    		try {
    			requestManager.update(urlUpdateDevolutivoUbi.getUrl(), urlUpdateDevolutivoUbi.getMetodo(),
    					parameterDevolutivo);
    		}
    		catch (SystemException e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		}   
    	}
        
        if(claseMov.equals("E")) {         	        
  
            	Map<String, Object> params = new TreeMap<>();
            	params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            	params.put(GeneralParameterEnum.TIPOMOVIMIENTOI.getName(), tipoMov);
            	params.put(GeneralParameterEnum.MOVIMIENTOI.getName(), registro.getCampos().get(
                        GeneralParameterEnum.NUMERO.getName()));
            	

            	params.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.UBICACION.getName()));
            
            	
            	Parameter parameters = new Parameter();

                parameters.setFields(params);

                UrlBean urlUpd = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                MovimientosControladorUrlEnum.URL141147
                                                                .getValue());
                try {
                    requestManager.update(urlUpd.getUrl(), urlUpd.getMetodo(),
                                    parameters);
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }  
        }
        	        	
        }
    
    public void cargarDetalleOrden() {
    	
    	try {
    		
    		Object tipo = registro.getCampos().get("TIPOMOVASOCIADO");
    		Object numero = registro.getCampos().get("MOVASOCIADO");
    		Object elemento = registroSub.getCampos().get("ELEMENTO");
   		    Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipo);
            param.put(GeneralParameterEnum.NUMERO.getName(), numero);
            param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

            Registro rsOrden;

   			rsOrden = RegistroConverter
   			                 .toRegistro(requestManager.get(
   			                                 UrlServiceUtil.getInstance()
   			                                                 .getUrlServiceByUrlByEnumID(
   			                                                		 MovimientosControladorUrlEnum.URL113041.getValue())
   			                                                 .getUrl(),
   			                                                 param));

            if (rsOrden != null)
            {

            	registroSub.getCampos().put("CANTIDAD", rsOrden.getCampos().get("CANTIDAD"));
                registroSub.getCampos().put("VALORUNITARIO", rsOrden.getCampos().get("VALORUNITARIODI"));
                registroSub.getCampos().put("VALORTOTAL", rsOrden.getCampos().get("VLRTOTAL"));
                registroSub.getCampos().put("UNIDAD", rsOrden.getCampos().get("UNIDAD"));
                
                if (rsOrden.getCampos().get("REFERENCIA") != null) {
                    registroSub.getCampos().put("REFERENCIA", rsOrden.getCampos().get("REFERENCIA"));
                }
                registroSub.getCampos().put("VLRUNITARIOPROM", rsOrden.getCampos().get("VALORUNITARIODI"));
                registroSub.getCampos().put("VALORDOCAS", rsOrden.getCampos().get("VALORUNITARIODI"));
                registroSub.getCampos().put("NIIF_VALOR_TOTAL", rsOrden.getCampos().get("VALORUNITARIODI"));
                
                
                if(rsOrden.getCampos().get("PORCIVA") != null) {
                	registroSub.getCampos().put("PORCIVA", rsOrden.getCampos().get("PORCIVA"));
                	registroSub.getCampos().put("VLRUNITARIO_ANTESIVA", rsOrden.getCampos().get("VALORUNITARIO"));
                	registroSub.getCampos().put("VALORBASE", rsOrden.getCampos().get("VALORUNITARIO"));
                }else {
                	registroSub.getCampos().put("VLRUNITARIO_ANTESIVA", rsOrden.getCampos().get("VALORUNITARIODI"));
                	registroSub.getCampos().put("VALORBASE", rsOrden.getCampos().get("VALORUNITARIODI"));
                }

            }else {
            	
            	registroSub.getCampos().put(CAMPO_ELEMENTO, 0);
                registroSub.getCampos().put(CAMPO_NOMLARGO, "");
                registroSub.getCampos().put("CANTIDAD", 0);
                registroSub.getCampos().put("VALORUNITARIO", "");
                registroSub.getCampos().put("VALORTOTAL", "");
                registroSub.getCampos().put("UNIDAD", "");
                registroSub.getCampos().put("REFERENCIA", "");
            	
            	String msg;
            	msg = idioma.getString("TB_TB4449");
            	msg = msg.replace("s$tipo$s", tipo.toString());
            	msg = msg.replace("s$numero$s", numero.toString());
            	JsfUtil.agregarMensajeError(msg);
            }
   			                                
   	                                
   		} catch (SystemException e) {
   			 logger.error(e.getMessage(), e);
   	         JsfUtil.agregarMensajeError(e.getMessage());
   		}
    	
    }
    	
    	
    

    @Override
    public boolean insertarAntes() {
        boolean salida = true;
        // <CODIGO_DESARROLLADO>
        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(CAMPO_COMPANIA, compania);
            registro.getCampos().put(CAMPO_TIPOMOV, tipoMov);
            registro.getCampos().remove("NOMBRE_UBICACION");
            registro.getCampos().remove("NOMBRE_UBICACIONORIGEN");
            registro.getCampos().remove("NOMBRE_UBICACIONDESTINO");
            // registro.getCampos().put(CAMPO_AUXILIAR,
            // SysmanConstantes.CONS_AUXILIAR);
            if (manvlrdefcodproy) {
            	registro.getCampos().put("CODIGOPROYECTO", "9999999999999999");
            } //JM 24/10/2024 7752522
            
            if(generaCausacion && validarTipoMov())
        	{ 
            	String nitCompania = SessionUtil.getCompaniaIngreso().getNit();
            	if (nitCompania.contains("-"))
                {
                    int fin = nitCompania.indexOf("-");
                    nitCompania = nitCompania.substring(0, fin);
                }
            	registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), nitCompania);
            	registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), "001");
        	}
        }
        if (faltanCamposObligatorios()) {
            salida = false;
        }
        try {
            if (validarFechaMovimiento()) {
                generarNumeroMovimiento();
                nroMovimiento = Long.parseLong(SysmanFunciones.toString(
                                registro.getCampos().get(CAMPO_NUMERO)));
                salida = true;
            }
            else {
                salida = false;
            }
        }
        catch (Exception e) {
            JsfUtil.agregarMensajeAlerta(
                            "Por favor configure el consecutivo inicial.");
        }
        
        if(manejaAIU && calcularPorAIU()) {
        	salida = false;
        }

        return salida;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica si falta diligenciar campos obligatorios.
     *
     * @return Verdadero si faltan campos obligatorios.
     */
    private boolean faltanCamposObligatorios() {
        boolean camposValidos = validarObligaTercero() && validarObligaOrigen()
            && validarObligaDestino() && validarConsecutivoOrfeo() && validarObligaProyecto() && validarObligaFactura();
        if (!camposValidos) {
            return true;
        }
        else if (pideCCto && SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), CAMPO_CCTO)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2021"));
            return true;
        }
        else {
            return false;
        }
    }

    private boolean validarConsecutivoOrfeo() {
    	 // Validaciones de transaccion
    	boolean retorno = true;
        if (manOrfeo) {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CONSECUTIVO_ORFEO))
               {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4364"));
                retorno = false;
            }
           
        }
       
        return retorno;
	}

	public boolean validarFechaMovimiento() {
        boolean retorno = true;
        if (SysmanFunciones.comparaFechas(
                        (Date) registro.getCampos().get(CAMPO_FECHA),
                        ultFechaMov)
            && (cptoMov.compareTo("II") != 0)
            && (parPermiteMovAntAlm.compareTo("SI") != 0)) {
            try {
                String mensaje = idioma.getString("TB_TB2013");
                mensaje = mensaje.replace("s$fecha$s", SysmanFunciones
                                .convertirAFechaCadena(ultFechaMov));
                JsfUtil.agregarMensajeAlerta(mensaje);
            }
            catch (ParseException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2014"));
            }
            retorno = false;
        }
        return retorno;
    }
    private String obtenerParametro(String parametro, boolean mayMe) {
		try {
			return ejbSysmanUtil.consultarParametro(compania, parametro,
					modulo, new Date(), mayMe);
		}
		catch (SystemException e) {

			Logger.getLogger(MovimientosControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return null;
	}
	


	public void validarEntradaOperacion() {
    	String manejaRueda;
    	
    	manejaRueda = SysmanFunciones.nvlStr(
				obtenerParametro("MANEJA RUEDA Y OPERACION", true), "NO");
    	
		if("SI".equals(manejaRueda)) {
			ruedaOperVisible = true;
		} else {
			ruedaOperVisible = false;
		}
		
		if("SI".equals(manElementoPepsIdi)) {
			proyAuxVisible = true;
		} else {
			proyAuxVisible = false;
		}
    }
	
	
	public void tomarFuenteRecursos() {
		try {
		 Map<String, Object> paramDisminuido = new HashMap<>();
         paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(), compania);
         paramDisminuido.put(GeneralParameterEnum.CLASEORDEN.getName(), registro.getCampos().get("TIPOMOVASOCIADO"));
         paramDisminuido.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("MOVASOCIADO"));

         Registro rsOrden;

			rsOrden = RegistroConverter
			                 .toRegistro(requestManager.get(
			                                 UrlServiceUtil.getInstance()
			                                                 .getUrlServiceByUrlByEnumID(
			                                                		 MovimientosControladorUrlEnum.URL82123.getValue())
			                                                 .getUrl(),
			                                                 paramDisminuido));

         if (rsOrden != null)
         {

             registro.getCampos().put("FUENTEDERECURSO", rsOrden.getCampos().get("FUENTEDERECURSO"));
             registroSub.getCampos().put("FUENTEDERECURSO", rsOrden.getCampos().get("FUENTEDERECURSO"));
         }else {
        	 //MOD JM 10/01/2025 CC625
        	 registro.getCampos().put("FUENTEDERECURSO", SysmanFunciones.nvl(registro.getCampos().get("FUENTEDERECURSO"), SysmanConstantes.CONS_FUENTE).toString());
         }
			                                
	                                
		} catch (SystemException e) {
			 logger.error(e.getMessage(), e);
	         JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}
    
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        if (((boolean) registro.getCampos().get(CAMPO_REGISTRADO))
            && (ACCION_MODIFICAR.equals(accion))) {
            accion = ACCION_VER;
        }
        
        /**Proceso auitoria*/
        accionAuditoria = EnumAcciones.INSERT.getValue();
        detalleMov = 0;
        ejecutarAuditoria();
   
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.TIPOMOVIMIENTO.getName());
            registro.getCampos().remove(CAMPO_NOMBREPROYECTO);
            registro.getCampos().remove("NOMBRE_UBICACION");
            registro.getCampos().remove("NOMBRE_UBICACIONORIGEN");
            registro.getCampos().remove("NOMBRE_UBICACIONDESTINO");
            // registro.getCampos().put("MOVASOCIADO", 7);
            
           
            if (SysmanFunciones.esBdSqlServer()) {

                registro.getCampos().put(CAMPO_VALORTOTAL,
                                new BigDecimal(registro.getCampos()
                                                .get("VALORTOTAL").toString()));
                registro.getCampos().put("VALORDOCASOCIADO",
                                new BigDecimal((Double) registro.getCampos()
                                                .get("VALORDOCASOCIADO")));
                registro.getCampos().put("TOTALCONAJUSTE",
                                new BigDecimal((Double) registro.getCampos()
                                                .get("TOTALCONAJUSTE")));
                registro.getCampos().put(CAMPO_VALORTOTALCONIVA,
                                new BigDecimal((Double) registro.getCampos()
                                                .get("VALORTOTALCONIVA")));
            }

        }
        
        detalleMov = 0;
        obtenerValoresAnteriores();
        if(desagregrarElementos) {
        	if(!"S".equals(claseMov)) { //JM 07/10/2024 7752214 
        		tomarFuenteRecursos(); 
            }
        }
        
     // mrosero CC_782 
        evaluarDetalle();
        
        try {
            reemplazarComillas(registro.getCampos(),"DESCRIPCION");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        if (manejaAIU) {
        	if (calcularPorAIU()) {
        		return false;
        	}
        	registro.getCampos().put("AJUSTE_DEC_MANUAL", "-1");
        }

        
        return validarObligaTercero() && validarObligaOrigen()
            && validarObligaDestino() && validarConsecutivoOrfeo()
            && validarObligaProyecto() && validarObligaFactura();

        // </CODIGO_DESARROLLADO>
    }

    private void reemplazarComillas(Map<String, Object> campos, String nombreCampo) {
        String valor = SysmanFunciones.nvlStr(SysmanFunciones.toString(campos.get(nombreCampo)), "");
        if (valor.contains("'")) {
            valor = valor.replace("'", "|");
            campos.put(nombreCampo, valor);
        }
    }
    
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if (((boolean) registro.getCampos().get(CAMPO_REGISTRADO))
            && (ACCION_MODIFICAR.equals(accion))) {
            accion = "v";
            cargarRegistro(css, accion, registro.getIndice());
        }
        if (ACCION_MODIFICAR.equals(accion) && (insertandoDetalle == 0)) {

            actualizarFechaActaInstalacionDetalle();
            cargarRegistro(css, accion, registro.getIndice());
        }
        if(manejaUbicacion.equals("SI")) {
        	actualizarUbicacion();
        }
        
        /**Proceso auitoria*/
        if (!valAnterior.isEmpty()) {
        	accionAuditoria = EnumAcciones.UPDATE.getValue();
        	ejecutarAuditoria();
        }
        
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private void actualizarFechaActaInstalacionDetalle() {

        Map<String, Object> param = new TreeMap<>();

        param.put("FECHA_INSTALACION",
                        registro.getCampos().get("FECHA_INSTALACION"));
        param.put("ACTA_INSTALACION",
                        registro.getCampos().get("ACTA_INSTALACION"));

        param.put(GeneralParameterEnum.COMPANIA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.COMPANIA.getName()));
        param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.TIPOMOVIMIENTO.getName()));
        param.put(GeneralParameterEnum.MOVIMIENTO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));

        Parameter parameter = new Parameter();

        parameter.setFields(param);

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovimientosControladorUrlEnum.URL1415
                                                        .getValue());
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que verifica la configuracion de la transaccion para
     * determinar si es obligatorio seleccionar un tercero / proveedor
     */
    public boolean validarObligaTercero() {
        boolean retorno = true;
        // Validaciones de transaccion
        boolean terceroVacio = SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), CAMPO_TERCERO);
        if (obligaTercero && terceroVacio) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2022"));
            retorno = false;
        }
        else if (obligaProveedor && "P".equals(tipoPersona) && terceroVacio) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2025"));
            retorno = false;
        }
        return retorno;
    }
    
    /**
     * Metodo que verifica la configuracion del tipo de movimiento
     */
    public boolean validarTipoMov() 
    {
    	boolean retorno = false;
    	
    	if(tipoElemento.equals("D") || tipoElemento.equals("M"))
    	{
    		if(claseBodDest.equals("60") && claseMov.equals("D"))
    		{
    			if((claseBodOrig.equals("20") && cptoMov.equals("CM")) || 
    					(claseBodOrig.equals("40") && cptoMov.equals("DT")) ||
    					(claseBodOrig.equals("50") && cptoMov.equals("L")))
    			{
    				retorno = true;
    			} 
    		}
    		else if(claseBodOrig.equals("20") && claseBodDest.equals("90") && cptoMov.equals("CM") && claseMov.equals("S"))
    		{
    			retorno = true;
    		} 
    	}
    	return retorno;
    }
    

    /**
     * Metodo que verifica la configuracion de la transaccion para
     * determinar si es obligatorio seleccionar los datos de bodega,
     * dependencia y responsable origen
     */
    public boolean validarObligaOrigen() {
        boolean retorno = true;
        // Validaciones de transaccion
        if (obligaOrigen) {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CAMPO_BODEGAORIGEN)
                || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                CAMPO_DEPORIGEN)
                || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                CAMPO_RESORIGEN)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2023"));
                retorno = false;
            }
            if (igualarOrigDest) {
                registro.getCampos().put(CAMPO_BODEGADESTINO,
                                registro.getCampos().get(CAMPO_BODEGAORIGEN));
            }
        }
        else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CAMPO_BODEGAORIGEN)) {
            String mensaje = idioma.getString("TB_TB3215");
            mensaje = mensaje.replace("s$tipoBodega$s", "Origen");
            mensaje = mensaje.replace("s$tipoMov$s", tipoMov);
            JsfUtil.agregarMensajeError(mensaje);
            retorno = false;
        }
        return retorno;
    }

    /**
     * Metodo que verifica la configuracion de la transaccion para
     * determinar si es obligatorio seleccionar los datos de bodega,
     * dependencia y responsable destino
     */
    public boolean validarObligaDestino() {
        boolean retorno = true;
        // Validaciones de transaccion
        if (obligaDestino) {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CAMPO_BODEGADESTINO)
                || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                CAMPO_DEPDESTINO)
                || SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                CAMPO_RESDESTINO)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2024"));
                retorno = false;
            }
            if (igualarOrigDest) {
                registro.getCampos().put(CAMPO_BODEGAORIGEN,
                                registro.getCampos().get(CAMPO_BODEGADESTINO));
            }
        }
        else if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CAMPO_BODEGADESTINO)) {
            String mensaje = idioma.getString("TB_TB3215");
            mensaje = mensaje.replace("s$tipoBodega$s", "Destino");
            mensaje = mensaje.replace("s$tipoMov$s", tipoMov);
            JsfUtil.agregarMensajeError(mensaje);
            retorno = false;
        }
        return retorno;
    }

    /**
     * Verifica si el tipo de movimiento en el que se esta trabajando
     * obliga a seleccionar una placa antes de guardar el detalle del
     * movimiento
     * 
     * @return false cuando no se ha seleccionado una placa y es
     * neceesario seleccionarla
     */
    private boolean validarObligaPlaca() {
        boolean retorno = true;
        boolean obligaPlaca = (("T".equals(claseMov) && "T".equals(cptoMov))
            || ("T".equals(claseMov) && "DS".equals(cptoMov))
            || ("T".equals(claseMov) && "L".equals(cptoMov))
            || ("E".equals(claseMov) && "R".equals(cptoMov))
            || ("S".equals(claseMov) && "N".equals(cptoMov))
            || ("D".equals(claseMov) && "DT".equals(cptoMov)))
            && !"C".equals(tipoElemento) && !"S".equals(tipoElemento);

        boolean bodega = estado.equals("40") || estado.equals("50")
            || estado.equals("60");

        if (obligaPlaca && Integer.parseInt(SysmanFunciones
                        .nvl(registroSub.getCampos().get(CAMPO_SERIE), 0)
                        .toString()) == 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4219"));
            retorno = false;
        }

        if (bodega && registroSub.getCampos().get("ESTADO") == null) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4219"));
            retorno = false;
        }

        return retorno;
    }
    /**
     * Metodo que verifica la configuracion de la transaccion para
     * determinar si es obligatorio seleccionar el codigo del proyecto
     */
    public boolean validarObligaProyecto() {
        boolean retorno = true;
        boolean proyectoVacio = SysmanFunciones.validarCampoVacio(registro.getCampos(), "CODIGOPROYECTO");
        
        if (proyAuxVisible && proyectoVacio) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4421"));
            retorno = false;
        }

        return retorno;
    }
    
    /**
     * Metodo que verifica la configuracion de la transaccion para
     * determinar si es obligatorio digitar el numero de factura.
     */
    public boolean validarObligaFactura() {
        boolean retorno = true;
        boolean facturaVacia = SysmanFunciones.validarCampoVacio(registro.getCampos(), "FACTURANO");
        
        //(TICKET: 7752152; FECHA: 20/09/2024; AUTOR: JEG) Se agrega el control �nicamente para conceto compras o comodato.
        if (manFuenteIn.equals("SI") && facturaVacia && claseMov.equals("E") && ("C".equals(cptoMov) || "CM".equals(cptoMov)) && (tipoElemento.equals("C") || tipoElemento.equals("D"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4450"));
            retorno = false;
        }

        return retorno;
    }
    
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        //registro.getCampos();
        evaluarDetalle();
        if (tieneDetalles) {
            return false;
        }

        detalleMov = 0;
        obtenerValoresAnteriores();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    	/**Proceso auitoria*/
        accionAuditoria = EnumAcciones.DELETE.getValue();
        ejecutarAuditoria();
        if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas y  elementos de consumo
            cargarListaElementoLote();
            cargarListaElementoLoteE();
        }
        return true;
    }

    public void eliminarDespuesSub() {
        // <CODIGO_DESARROLLADO>
        // verificar si el movimiento tiene items
        evaluarDetalle();
        if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas y  elementos de consumo
            cargarListaElementoLote();
            cargarListaElementoLoteE();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void reversar(String tipo, String movimiento, String codigoDetalle) {
        try {

            Registro reg;
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("KEY_COMPANIA", compania);
            parametros.put("KEY_TIPOMOVIMIENTO", tipo);
            parametros.put("KEY_MOVIMIENTO", movimiento);
            parametros.put("KEY_CODIGO", codigoDetalle);

            reg = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            MovimientosControladorUrlEnum.URL1217
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametros));

            ejbAlmacenDos.reversarDocumento(compania,
                            registro.getCampos().get("TIPOMOVASOCIADO")
                                            .toString(),
                            Long.parseLong(registro.getCampos()
                                            .get("MOVASOCIADO").toString()),
                            reg.getCampos().get(CAMPO_ELEMENTO).toString(),
                            new BigDecimal(reg.getCampos().get(CAMPO_CANTIDAD)
                                            .toString()));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void insertarDespuesSub() {
    	if(visibleUbicacionTras == true ) {
    		actualizarUbicacion();
    	}
        evaluarDetalle();     
        if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas y  elementos de consumo
            cargarListaElementoLote();
            cargarListaElementoLoteE();
        }
    }

    /**
     * Verificar si el movimiento tiene items
     */
    public void evaluarDetalle() {
        Object numeroMovimiento = registro.getCampos().get(CAMPO_NUMERO);
        if (numeroMovimiento == null) {
            return;
        }
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
        params.put(GeneralParameterEnum.MOVIMIENTO.getName(), numeroMovimiento);
        List<Registro> detallesMovimiento = null;
        String urlEnumId = MovimientosControladorUrlEnum.URL195067.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            List<Parameter> parameters = requestManager.getList(url, params);
            detallesMovimiento = RegistroConverter.toListRegistro(parameters);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (detallesMovimiento != null) {
            if (!detallesMovimiento.isEmpty() &&  detallesMovimiento.get(0).getCampos().get("TIPOMOVASOCIADO")!=null) {
                registro.getCampos().put("TIPOMOVASOCIADO", 
                	    SysmanFunciones.toString(detallesMovimiento.get(0).getCampos().get("TIPOMOVASOCIADO")));
            }else {
            	 registro.getCampos().put("TIPOMOVASOCIADO",claseDocAsMov);
            	
            }
           
            tieneDetalles = !detallesMovimiento.isEmpty();
            if("S".equals(claseMov) && manAuxiliares.equals("NO")) { //JM 07/10/2024 7752214 
            	tieneDetalles = false; 
            }
        }
        else {
        	
            tieneDetalles = false;
        }

    }

    public void retornarFormularioSeleccionar(SelectEvent event) {
        cargarListaSubdmovimiento();
        evaluarDetalle();
        if(manejaUbicacion.equals("SI")) {
        	actualizarUbicacion();
        }
        if(manFuenteIn.equals("SI") && "S".equals(claseMov) && "C".equals(tipoElemento) ){//salidas y  elementos de consumo
            cargarListaElementoLote();
            cargarListaElementoLoteE();
        }     
    }

    public void retornarFormularioCmdEditar(SelectEvent event) {
        cargarListaSubdmovimiento();
    }

    public void aceptarCambiarNumero() {
        // <CODIGO_DESARROLLADO>
        String usuario = SessionUtil.getUser().getCodigo();
        long numeroMovimiento = Long.parseLong(
                        extraerString(registro.getCampos().get(CAMPO_NUMERO)));
        long numeroNuevo = Long.parseLong(nuevoNumero);
        try {
            ejbAlmacenCuatro.cambiarNumeroMovimiento(compania, usuario, tipoMov,
                            numeroMovimiento, numeroNuevo);

            Map<String, Object> nuevaLlave = css;
            nuevaLlave.put("KEY_NUMERO", numeroNuevo);
            cargarRegistro(nuevaLlave, accion);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2028") + "<br>"
                + e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica si el movimiento tiene movimientos sin registrar, si
     * los tiene retorno true
     */
    public boolean verificarDetallesSinRegistrar() {
        boolean retorno = false;
        try {
            long numeroMovimiento = nroMovimiento;
            retorno = ejbAlmacenCuatro.tieneMovimientosSinRegistrar(compania,
                            tipoMov, numeroMovimiento);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return retorno;
    }

    public boolean validarAntesImprimir() {
        boolean puedeImprimir = true;
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CAMPO_NUMERO)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2030"));
            puedeImprimir = false;
        }

        if (!puedeImprimir && verificarDetallesSinRegistrar()) {
            puedeImprimir = false;
        }
        try {
        	if(contratoManual) {
        		formatoNombre = "002965MOIDICASTILLA";
        	}
        	else {
        		formatoNombre = ejbAlmacenCuatro.traerTipoFormato(compania,
                        tipoMov);
        	}
        	
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!puedeImprimir
            && SysmanFunciones.validarVariableVacio(formatoNombre)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2032"));
            puedeImprimir = false;
        }
        return puedeImprimir;
    }

    public void imprimirMovimiento(ReportesBean.FORMATOS formato) {
        if (!validarAntesImprimir()) {
            return;
        }
        archivoDescarga = null;
        nombreConsulta = null;
        try {
        	String parUspec = getParametro("FORMATOS UNICOS USPEC", "NO");
        	
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoMovimiento", tipoMov);
            reemplazar.put("movimientoInicial", nroMovimiento);
            reemplazar.put("movimientoFinal", nroMovimiento);
            String parametro = getParametro("DIGITOS AGRUPACION INVENTARIO",
                            "0");
            reemplazar.put("nivelGrupo", Integer.parseInt(parametro));
            if (formatoNombre.equals("001986MOIDIUPC")) {

                nombreConsulta = "000528MOIDITUN";

            }
            else {

                nombreConsulta = formatoNombre;

            }
            
            if(parUspec.equals("SI")) {
            	switch (claseMov) {
                case "E":
            		nombreConsulta = "002461FormEntradasUspec";
            		formatoNombre = "002461FormEntradasUspec";
            		break;
                case "S":
                	nombreConsulta = "002472FormSalidasUspec";
            		formatoNombre = "002472FormSalidasUspec";
            		break;
                case "T":
                	nombreConsulta = "002486FormTraspasosUspec";
            		formatoNombre = "002486FormTraspasosUspec";
            		break;
            	default:
            		break;
            	}
            } else if (parUspec.equals("NO") && formatoNombre.contains("Uspec")) {
            	return;
            }
            
            Reporteador.resuelveConsulta(nombreConsulta,
                            Integer.parseInt(modulo), reemplazar, parametros);

            parametros.put("PR_APLICA_AIU", manejaAIU);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CIUDADCOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getCiudad());
            parametros.put("PR_NOMBRE_JEFE_DIVISION_ADMINISTRATIVA",
                            getParametro("NOMBRE JEFE DIVISION ADMINISTRATIVA",
                                            null));
            parametros.put("PR_CARGO_JEFE_DIVISION_ADMINISTRATIVA",
                            getParametro("CARGO JEFE DIV"
                            		+ "ISION ADMINISTRATIVA",
                                            null));

            parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
                            getParametro("CARGO COORDINADOR ALMACEN", null));
            parametros.put("PR_JEFE_DE_AREA",
                            getParametro("JEFE DE AREA", null));

            String vFormatoCalidad = getParametro(
                            "VERSION FORMATO CALIDAD TUNJA", null);

            parametros.put("PR_VERSION_FORMATO_CALIDAD_TUNJA", vFormatoCalidad);
            String codFormatoCalidad = getParametro(
                            "CODIGO FORMATO CALIDAD TUNJA", null);
            parametros.put("PR_CODIGO_FORMATO_CALIDAD_TUNJA",
                            codFormatoCalidad);
            String fecFormatoCalidad = getParametro(
                            "FECHA FORMATO CALIDAD TUNJA", null);
            parametros.put("PR_FECHA_FORMATO_CALIDAD_TUNJA", fecFormatoCalidad);
            String pieFormatoCalidad = getParametro(
                            "PIE DE PAGINA FORMATO CALIDAD TRASLADOS TUNJA",
                            null);
            parametros.put("PR_PIE_DE_PAGINA_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                            pieFormatoCalidad);
            
         // INICIO AGREGO CAMILA ROSERO
         			String vFormatoCalidad1 = getParametro(
         					"VERSION FORMATO CALIDAD FUNZA", null);        

         			parametros.put("PR_VERSION_FORMATO_CALIDAD_FUNZA", vFormatoCalidad1);         


         			String codFormatoCalidad1 = getParametro(
         					"CODIGO FORMATO CALIDAD FUNZA", null);


         			parametros.put("PR_CODIGO_FORMATO_CALIDAD_FUNZA",
         					codFormatoCalidad1);

         			String fecFormatoCalidad1 = getParametro(
         					"FECHA FORMATO CALIDAD FUNZA", null);            


         			parametros.put("PR_FECHA_FORMATO_CALIDAD_FUNZA", fecFormatoCalidad1);


         			String pieFormatoCalidad1 = getParametro(
         					"PIE DE PAGINA FORMATO CALIDAD TRASLADOS FUNZA",
         					null);

         			parametros.put("PR_PIE_DE_PAGINA_FORMATO_CALIDAD_TRASLADOS_FUNZA",
         					pieFormatoCalidad1);

         			//FIN AGREGO CAMILA ROSERO

            String nomAlmacenista = getParametro("ALMACENISTA", null);
            parametros.put("PR_ALMACENISTA", nomAlmacenista);
            String cargoAlmacenista = getParametro("CARGO ALMACENISTA", null);
            parametros.put("PR_CARGO_ALMACENISTA", cargoAlmacenista);
            String seriePlacaCsc = getParametro(
                            "NOMBRE SERIE-PLACA/CONSECUTIVO INFORME MOI_DI",
                            null);
            parametros.put("PR_NOMBRE_SERIE-PLACA/CONSECUTIVO_INFORME_MOI_DI",
                            seriePlacaCsc);
            String coordAlmacen = getParametro("CARGO COORDINADOR ALMACEN",
                            null);
            parametros.put("PR_CARGO_COORDINADOR_ALMACEN", coordAlmacen);
            String firmaOrdAlmacen = getParametro(
                            "FIRMA CARGO ORDENADOR ALMACEN", null);
            parametros.put("PR_FIRMA_CARGO_ORDENADOR_ALMACEN", firmaOrdAlmacen);

            String CoordAlmacen = getParametro("COORDINADOR ALMACEN", null);
            parametros.put("PR_COORDINADOR_ALMACEN", CoordAlmacen);
            
            String nomJefeDivAdmin = getParametro("NOMBRE JEFE DIVISION ADMINISTRATIVA",
                    null);
		    parametros.put("PR_NOMBRE_JEFE_DIVISION_ADMINISTRATIVA", nomJefeDivAdmin);
		    
		
		    String cargoJefeDivAdmin = getParametro("CARGO JEFE DIVISION ADMINISTRATIVA",
		                    null);
		    parametros.put("PR_CARGO_JEFE_DIVISION_ADMINISTRATIVA", cargoJefeDivAdmin);

            // laurab

            String ordenadorAlmacen = getParametro("ORDENADOR ALMACEN", null);
            parametros.put("PR_ORDENADOR_ALMACEN", ordenadorAlmacen);

            String cargoAlmacen = getParametro("CARGO ORDENADOR ALMACEN", null);
            parametros.put("PR_CARGO_ORDENADOR_ALMACEN", cargoAlmacen);

            String nombreAlmacen = getParametro("NOMBRE AUXILIAR ALMACEN",
                            null);
            parametros.put("PR_NOMBRE_AUXILIAR_ALMACEN", nombreAlmacen);

            String firma1Acta = getParametro("FIRMA 1 EN ACTA DE TRASPASO",
                            null);
            parametros.put("PR_FIRMA_1_EN_ACTA_DE_TRASPASO", firma1Acta);

            String cargo1Acta = getParametro("CARGO 1 EN ACTA DE TRASPASO",
                            null);
            parametros.put("PR_CARGO_1_EN_ACTA_DE_TRASPASO", cargo1Acta);
            
            String adminFinanciero = getParametro("NOMBRE SUBDIRECTOR ADMINISTRATIVO Y FINANCIERO",
                    null);
            parametros.put("PR_NOMBRE_SUBDIRECTOR", adminFinanciero);
            
            String cargoAdminFinanciero = getParametro("CARGO SUBDIRECTOR ADM. Y FIANCIERA",
                    null);
            parametros.put("PR_CARGO_SUBDIRECTOR", cargoAdminFinanciero);
            
            String nombreProfesional = getParametro("NOMBRE DEL PROFESIONAL UNIVERSITARIO",
                    null);
            parametros.put("PR_NOMBRE_PROFESIONAL", nombreProfesional);
            
            String cargoProfesional = getParametro("CARGO DEL PROFESIONAL UNIVERSITARIO",
                    null);
            parametros.put("PR_CARGO_PROFESIONAL", cargoProfesional);
            
            String tecnicoRecursos = getParametro("TECNICO DE RECURSOS FISICOS",
                    null);
            parametros.put("PR_TECNICO_RECURSOS_FISICOS", tecnicoRecursos);
            

            //
            /* AGREGO POR MIGUEL VENEGAS */

            String revisorAlmacen = getParametro("REVISOR ALMACEN", null);
            parametros.put("PR_REVISOR_ALMACEN", revisorAlmacen);

            String cargoRevisorAlmacen = getParametro("CARGO REVISOR ALMACEN",
                            null);
            parametros.put("PR_CARGO_REVISOR_ALMACEN", cargoRevisorAlmacen);
            String reportediferente = "001467TDR";
            if (!formatoNombre.equals(reportediferente)) {
                String requisicion = ejbAlmacenCuatro
                                .generarNumeroRequisiciones(compania, tipoMov,
                                                nroMovimiento,
                                                nroMovimiento);
                parametros.put("REQUISICION", requisicion);
            }

            parametros.put("PR_DIRECCIONCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDireccion());

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            parametros.put("PR_EXAMINADOR_DE_DOCUMENTOS",
                            getParametro("EXAMINADOR DE DOCUMENTOS", null));

            parametros.put("PR_CARGO_EXAMINADOR_DOCUMENTOS",
                            getParametro("CARGO EXAMINADOR DOCUMENTOS", null));

            parametros.put("PR_SUB_ADMINISTRATIVO",
                            getParametro("SUB ADMINISTRATIVO", null));

            parametros.put("PR_CARGO_SUB_ADMINISTRATIVO",
                            getParametro("SUB_ADMINISTRATIVO", null));

            /* FIN AGREGO POR MIGUEL VENEGAS */
            
            /*INICIO AGREGO POR DIANA CASTIBLANCO*/
            String aprobo = getParametro("APROBADO", null);
            parametros.put("PR_APROBADO", aprobo);
            /*FIN AGREGO POR DIANA CASTIBLANCO*/
            
            /*INICIO (TICKET: 7722052; FECHA: 28/10/2022; AUTOR: JEG) */
            String cargoSecAlm = getParametro("CARGO SECRETARIA ALMACEN", null);
            parametros.put("PR_CARGO_SECRETARIA_ALMACEN", cargoSecAlm);
            String usuario = SessionUtil.getUser().getNombre1() + ' ' + SessionUtil.getUser().getNombre2() +
            				' ' + SessionUtil.getUser().getApellido1() + ' ' + SessionUtil.getUser().getApellido2();            
            parametros.put("PR_USUARIO", usuario);
            
            /* FIN */
            
            /*INICIO AGREGO POR DANIEL RAMIREZ*/
            String txtPersonalizado = getParametro("TEXTO PERSONALIZADO FUNZA", null);
            parametros.put("PR_TXT_PERSONALIZADO", txtPersonalizado);
            String firmaAlmacenista = getParametro("FIRMA ALMACENISTA GENERAL", null);
            parametros.put("PR_FIRMA_ALMACENISTA_GENERAL", firmaAlmacenista);
            /*FIN AGREGO POR DANIEL RAMIREZ*/

            if ("000536SDFTUNJA".equals(formatoNombre) || "002486FormTraspasosUspec".equals(formatoNombre)) {
                parametros.put("PR_CODIGO_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                                getParametro("CODIGO FORMATO CALIDAD TRASLADOS TUNJA",
                                                " "));
                parametros.put("PR_FECHA_FORMATO_CALIDAD_TRASLADOS_TUNJA",
                                getParametro("FECHA FORMATO CALIDAD TRASLADOS TUNJA",
                                                " "));
                           // INICIO AGREGO CAMILA ROSERO
             			} else if ("002331AlmacenConsumoFunza".equals(formatoNombre)) {
             				parametros.put("PR_CODIGO_FORMATO_CALIDAD_TRASLADOS_FUNZA",
             						getParametro("CODIGO FORMATO CALIDAD TRASLADOS FUNZA",
             								" "));
             				parametros.put("PR_FECHA_FORMATO_CALIDAD_TRASLADOS_FUNZA",
             						getParametro("FECHA FORMATO CALIDAD TRASLADOS FUNZA",
             								" "));
             				}
             				// FIN AGREGO CAMILA ROSERO
            
            archivoDescarga = JsfUtil.exportarStreamed(formatoNombre,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " "
                                + e.getMessage() + " " + formatoNombre);
            Logger.getLogger(MovimientosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        catch (JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch (SysmanException | NumberFormatException
                        | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirdocAsociado() {
        if (tieneDetalles) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2034"));
            return;
        }
        if (registro.getCampos().get(
                        MovimientosControladorEnum.PARAM9.getValue()) != null) {
            String ruta = "/frmdocasociadomovimiento.sysman";
            Object tipoMovAsociado = registro.getCampos()
                            .get(MovimientosControladorEnum.PARAM9.getValue());
            // traer el nombre del tipo de documento asociado
            try {
                String codDocumento = extraerString(tipoMovAsociado);
                nombreDocAsociado = ejbAlmacenCuatro
                                .traerNombreDocumentoAsociado(compania,
                                                codDocumento);
                // Eliminar los registros de la tabla temporal
                // relacionadas con este movimiento
                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(),
                                tipoMov);
                params.put(GeneralParameterEnum.MOVIMIENTO.getName(),
                                nroMovimiento);

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                MovimientosControladorUrlEnum.URL219970
                                                                .getValue());
                requestManager.delete(urlDelete.getUrl(), params);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            Map<String, Object> parametros = SessionUtil.getFlash();

            if (rid == null) {
                parametros.put("rid", css);
            }
            else {
                parametros.put("rid", rid);
            }
            parametros.put("accion", accion);
            parametros.put("nombreDocAsociado", nombreDocAsociado);
            parametros.put("nroMov", nroMovimiento);
            parametros.put("tipoElementoMov", tipoElemento);
            parametros.put("proyecto", registro.getCampos().get("CODIGOPROYECTO"));
            parametros.put("operacion", registro.getCampos().get(CAMPO_OPERACION));
            parametros.put("auxAlmacen", registro.getCampos().get("AUX_ALMACEN"));
            parametros.put("rueda", registro.getCampos().get(CAMPO_RUEDA));
            parametros.put("fuenteR", registro.getCampos().get("FUENTEDERECURSO"));
            parametros.put("referencia", registro.getCampos().get("REFERENCIA"));
            parametros.put("auxiliar", registro.getCampos().get(CAMPO_AUXILIAR));
            parametros.put("centroCosto", registro.getCampos().get(CAMPO_CCTO));
            parametros.put("bodega", registro.getCampos().get(CAMPO_BODEGAORIGEN));
            parametros.put("cptoMov", cptoMov);
            Direccionador direccionador = new Direccionador();
            direccionador.setRuta(ruta);
            direccionador.setParametros(parametros);
            // se agrega variable que afirma que el formulario se
            // redirecciono para solucionar problema de la lista LMS
            SessionUtil.setSessionVar("retornoFormulario", "retorna");
            SessionUtil.redireccionar(direccionador);
        }
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubdmovimiento = null;
    }

    @Override
    public void iniciarListasSub() {

        cargarListaSubdmovimiento();
        cargarListaResponsableOrigen();
        cargarListaresponsableDestino();
        cargarListaRecibidoPor();
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaElementoLote();
        cargarListaElementoLoteE();
        cargarListaSerie();
        cargarListaSerieE();
        cargarNombreTercero();
        cargarNombreResponsableOrigen();
        cargarNombreResponsableDestino();
        cargarNombreBodegaOrigen();
        cargarNombreBodegaDestino();
        cargarNombreCentroCosto();
        cargarNombreAuxiliar();
        cargarNombreProveedor();
        cargarNombreFuenteRecurso();
        cargarNombreReferencia();
        cargarNombreProyecto();
        cargarListaComponente();
        cargarlistacomponenteE();
        cargarListaElementoIdi();
        cargarListaElementoIdiE();
        cargarListaUnidad();
        cargarListaUnidadE();
        cargarListaFuenteRecursos();
        cargarListaFuenteRecursosE();
        cargarListaReferenciaCnt();
        cargarListaReferenciaCntE();
        cargarListaAuxiliarDet();
        cargarListaAuxiliarDetE();
        cargarListaProyectoDet();
        cargarListaProyectoDetE();
        cargarListaCentroCostoDet();
        cargarListaCentroCostoDetE();
        cargarListaElementoAux();
        cargarListaElementoAuxE();
        cargarListaPlantillaDocx();

        if (!"M".equals(claseDocAsMov)) {
            registro.getCampos().put(
                            MovimientosControladorEnum.PARAM9.getValue(),
                            claseDocAsMov == null ? 'O' : claseDocAsMov);

        }

        if (!"O".equals(claseDocAsMov)
            && Arrays.asList("E", "S").contains(claseMov)) {
            cargarListaMovAsociado();
            cargarListaMovAsociadoComp();
        }
    }

    @Override
    public void iniciarListas() {
    	validarEntradaOperacion();
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaElementoLote();
        cargarListaElementoLoteE();
        cargarListaSerie();
        cargarListaSerieE();
        cargarListaTercero();
        cargarListaDependenciaAnt();
        cargarListacmbDependencia();
        cargarListabodegaOrigen();
        cargarListabodegaDestino();
        cargarListaCentroDeCosto();
        cargarListacmbProveedor();
        cargarListaTipoMovAsociado1();
        cargarListaAuxiliar();
        cargarListaAuxiliarSub();
        cargarlistaTipoActivoNiif();
        cargarlistaTipoActivoNiifE();
        cargarListaComponente();
        cargarlistacomponenteE();
        cargarListaFuenteRecurso();
        cargarListaReferencia();
        cargarListaEstado();
        cargarListaProyecto();
        cargarListaFuenteRecursos();
        cargarListaFuenteRecursosE();
        cargarListaReferenciaCnt();
        cargarListaReferenciaCntE();
        cargarListaAuxiliarDet();
        cargarListaAuxiliarDetE();
        cargarListaProyectoDet();
        cargarListaProyectoDetE();
        cargarListaCentroCostoDet();
        cargarListaCentroCostoDetE();
    }

    private void estadoVisible() {

    }

    public void activarEdicionSubdmovimiento(Registro registro) {
        cargarListaSerieE();
        cantidadAnterior = extraerDouble(
                        registro.getCampos().get(CAMPO_CANTIDAD));

    }

    /**
     * Identifica si la bodega origen es de clase <i>bodega</i> o
     * <i>proveedor</i> y la bodega destino es de clase <i>servicio</>
     *
     * @return <code>true</code> si el movimiento es una entrada a
     * servicio o un traslado.
     */
    private boolean esTraspaso() {
        boolean rta = false;
        if (registro != null) {
            Map<String, Object> map = registro.getCampos();
            String claseBodegaOrigen = String
                            .valueOf(map.get(CAMPO_CLASE_BODORIGEN));
            String claseBodegaDestino = String
                            .valueOf(map.get(CAMPO_CLASE_BODDESTINO));
            if ("30".equals(claseBodegaDestino)
                && ("20".equals(claseBodegaOrigen)
                    || "30".equals(claseBodegaOrigen))) {
                rta = true;
            }
        }
        return rta;
    }

    /**
     * Identifica si la bodega origen es de clase <i>proveedores</i> y
     * la bodega destino es de clase <i>bodega</i>.
     *
     * @return <code>true</code> si el movimiento es una entrada por
     * compra.
     */
    private boolean esEntradaPorCompra() {
        boolean rta = false;
        if (registro != null) {
            Map<String, Object> map = registro.getCampos();
            String claseBodegaOrigen = String
                            .valueOf(map.get(CAMPO_CLASE_BODORIGEN));
            String claseBodegaDestino = String
                            .valueOf(map.get(CAMPO_CLASE_BODDESTINO));
            rta = "10".equals(claseBodegaOrigen)
                && "20".equals(claseBodegaDestino);
        }
        return rta;
    }

    /**
     * Trae el nombre del tercero seleccionado en la lista
     * <b>listaRecibidoPor</b>. Si el nombre est vacio asigna el texo
     * SIN NOMBRE.
     */
    private void cargarNombreRecibido() {
        if (registro.getCampos().get(CAMPO_NITRECIBIDO) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(CAMPO_CEDULA, registro.getCampos().get(CAMPO_NITRECIBIDO));
            Registro registro;
            try {
                registro = listaRecibidoPor.getRegistroUnico(map);
                if (registro != null) {
                    nomRecibidoPor = extraerString(
                                    registro.getCampos().get(CAMPO_NOMBRERES));
                }
                else {
                    nomRecibidoPor = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre del tercero seleccionado en la lista
     * <b>listaTercero</b>. Si el nombre esta vacio asigna el texo SIN
     * NOMBRE.
     */
    private void cargarNombreTercero() {
        if (registro.getCampos().get(CAMPO_TERCERO) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put("NIT", registro.getCampos().get(CAMPO_TERCERO));
            Registro regTercero;
            try {
                regTercero = listaTercero.getRegistroUnico(map);
                if (regTercero != null) {
                    nomTercero = extraerString(
                                    regTercero.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomTercero = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre del responsable origen seleccionado en la lista
     * <b>listaResponsableOrigen</b>. Si el nombre esta vacio asigna
     * el texo SIN NOMBRE.
     */
    private void cargarNombreResponsableOrigen() {
        if (registro.getCampos().get(CAMPO_RESORIGEN) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(CAMPO_CEDULA, registro.getCampos().get(CAMPO_RESORIGEN));
            Registro regRespOrigen;
            try {
                regRespOrigen = listaResponsableOrigen.getRegistroUnico(map);
                if (regRespOrigen != null) {
                    nomRespOrigen = extraerString(regRespOrigen.getCampos()
                                    .get(CAMPO_NOMBRERES));
                }
                else {
                    nomRespOrigen = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Trae el nombre del responsable destino seleccionado en la lista
     * <b>listaresponsableDestino</b>. Si el nombre esta vacio asigna
     * el texo SIN NOMBRE.
     */
    private void cargarNombreResponsableDestino() {
        if (registro.getCampos().get(CAMPO_RESDESTINO) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(CAMPO_CEDULA, registro.getCampos().get(CAMPO_RESDESTINO));
            Registro regRespDestino;
            try {
                regRespDestino = listaResponsableDestino.getRegistroUnico(map);
                if (regRespDestino != null) {
                    nomRespDestino = extraerString(regRespDestino.getCampos()
                                    .get(CAMPO_NOMBRERES));
                }
                else {
                    nomRespDestino = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre de la bodega origen seleccionada en la lista
     * <b>listabodegaOrigen</b>. Si el nombre esta vacio asigna el
     * texo SIN NOMBRE.
     */
    private void cargarNombreBodegaOrigen() {
        if (registro.getCampos().get(CAMPO_BODEGAORIGEN) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(CAMPO_BODEGAORIGEN));
            Registro regBodOrig;
            try {
                regBodOrig = listaBodegaOrigen.getRegistroUnico(map);
                if (regBodOrig != null) {
                    nomBodOrigen = extraerString(
                                    regBodOrig.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomBodOrigen = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre de la bodega destino seleccionada en la lista
     * <b>listabodegaDestino</b>. Si el nombre esta vacio asigna el
     * texo SIN NOMBRE.
     */
    private void cargarNombreBodegaDestino() {
        if (registro.getCampos().get(CAMPO_BODEGADESTINO) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(CAMPO_BODEGADESTINO));
            Registro regBodDest;
            try {
                regBodDest = listaBodegaDestino.getRegistroUnico(map);
                if (regBodDest != null) {
                    nomBodDestino = extraerString(
                                    regBodDest.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomBodDestino = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre del centro de costo seleccionado en la lista
     * <b>listaCentroDeCosto</b>. Si el nombre esta vacio asigna el
     * texo SIN NOMBRE.
     */
    private void cargarNombreCentroCosto() {
        if (registro.getCampos().get(CAMPO_CCTO) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(CAMPO_CCTO));
            Registro regCcto;
            try {
                regCcto = listaCentroDeCosto.getRegistroUnico(map);
                if (regCcto != null) {
                    nomCentroCosto = extraerString(
                                    regCcto.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomCentroCosto = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    private void cargarNombreFuenteRecurso() {
        if (registro.getCampos().get("FUENTEDERECURSO") != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get("FUENTEDERECURSO"));
            Registro regFtrec;
            try {
                regFtrec = listaFuenteRecurso.getRegistroUnico(map);
                if (regFtrec != null) {
                    nomFuenteRecurso = extraerString(
                                    regFtrec.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomFuenteRecurso = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    private void cargarNombreReferencia() {
        if (registro.getCampos().get("REFERENCIA") != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get("REFERENCIA"));
            Registro regRef;
            try {
                regRef = listaReferencia.getRegistroUnico(map);
                if (regRef != null) {
                    nomReferencia = extraerString(
                                    regRef.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomReferencia = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Trae el nombre del auxiliar seleccionado en la lista
     * <b>listaAuxiliar</b>. Si el nombre esta vacio asigna el texo
     * SIN NOMBRE.
     */
    private void cargarNombreAuxiliar() {
        if (registro.getCampos().get(CAMPO_AUXILIAR) != null) {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(CAMPO_AUXILIAR));
            Registro regAux;
            try {
                regAux = listaAuxiliar.getRegistroUnico(params);
                if (regAux != null) {
                    nomAuxiliar = extraerString(
                                    regAux.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomAuxiliar = TEXTO_SINNOMBRE;
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    private void cargarNombreProyecto() {
    	if (registro.getCampos().get("CODIGOPROYECTO") != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get("CODIGOPROYECTO"));
            Registro regRef;
            try {
                regRef = listaProyecto.getRegistroUnico(map);
                if (regRef != null) {
                    nomProyecto = extraerString(
                                    regRef.getCampos().get(CAMPO_NOMBREPROYECTO));
                } else {
                    nomProyecto = TEXTO_SINNOMBRE;
                }
            } catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }
    
    /**
     * Trae el nombre del proveedor seleccionado en la lista
     * <b>listacmbProveedor</b>. Si el nombre esta vacio asigna el
     * texo SIN NOMBRE.
     */
    private void cargarNombreProveedor() {
        if (registro.getCampos().get(CAMPO_PROVEEDORCA) != null) {
            Map<String, Object> map = new TreeMap<>();
            map.put("NIT", registro.getCampos().get(CAMPO_PROVEEDORCA));
            Registro regProv;
            try {
                regProv = listaCmbProveedor.getRegistroUnico(map);
                if (regProv != null) {
                    nomProveedor = extraerString(
                                    regProv.getCampos().get(CAMPO_NOMBRE));
                }
                else {
                    nomProveedor = "";
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void generarNumeroMovimiento() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CAMPO_NUMERO)) {
            try {
            	
                long numeroInicial = Long.parseLong(nroInicial);
                long consecutivo = 0;
                
                
            	consecutivo = ejbAlmacenUno.generarConsecutivoAlmacen( 
                            compania, tipoMov, claseMov, invInicial,
                            numeroInicial, Integer.parseInt(modulo),tipoElemento);
        	
                if (consecutivo > 0) {
                    registro.getCampos().put(CAMPO_NUMERO, consecutivo);
                }
            }
            catch (NumberFormatException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2020")
                    + "<br>" + e.getMessage());
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Dependiendo de ciertas condiciones del tipo de movimiento
     * seleccionado habilita o no controles graficos.
     */
    public void validarControlesGraficos() {

        verDocumentos = false;
        registroSub.getCampos().put("APLICANIIF", "-1");
        if (obligaProveedor && "P".equals(tipoPersona)) {
            setEtiquetaTercero(idioma.getString("TG_PROVEEDOR3") + " :");
        }
        else {
            setEtiquetaTercero(idioma.getString("TG_TERCERO2") + " :");
        }
        if ("S".equals(claseMov)) {
            etiquetaObs = idioma.getString("TB_TB3133");
        }
        else {
            etiquetaObs = idioma.getString("TG_OBSERVACIONES5");
        }

        String manejaRubroAlmacen = getParametro("MANEJA RUBRO EN ALMACEN",
                        "NO");
        if ("SI".equals(manejaRubroAlmacen)) {
            etiquetaObs = idioma.getString("TB_TB3134");
        }
        /*
         * Se debe validar el concepto del movimiento o tipo de
         * elemento para determinar si se activa o no la cantidad y el
         * valor por omision
         */
        if ("CR".equals(cptoMov)) {
            verDocumentos = true;
            cantBloq = true;
            registroSub.getCampos().put(CAMPO_CANTIDAD, "0");
            /*
             * Configuracion de dependencia para movimientos con
             * concepto "Correccion de Valor".
             */
            setBloqueaDependencia(true);
            String bodegaAlmacen = getParametro("BODEGA ALMACEN",
                            "000000000000");
            registro.getCampos().put(CAMPO_DEPORIGEN, bodegaAlmacen);
            registro.getCampos().put(CAMPO_DEPDESTINO, bodegaAlmacen);
        }

        else if ("C".equals(tipoElemento)) {
            cantBloq = false;
            registroSub.getCampos().put(CAMPO_CANTIDAD, "1");
        }
        else {
            cantBloq = true;
            registroSub.getCampos().put(CAMPO_CANTIDAD, "1");
        }
        /*
         * inicializar el valor que tendra los controles de marca y
         * serie devolutivo
         */
        serieDevBloq = true;
        marcaBloq = true;
        
        String permiteEspecificacion = getParametro("PERMITE MODIFICAR ESPECIFICACION EN MOVIMIENTOS DE ALMACEN",
                "NO");
        /*
         * validar la clase de movimiento para habilitar o no el
         * control de valor unitario
         */
        if ("E".equals(claseMov)) {
            vlrUnitBloq = false;
            /*
             * Si la clase bodega es 10 (PROVEEDORES) para habilitar o
             * no el control de marca y serie devolutivo.
             */
            if ("10".equals(claseBodOrig) || "80".equals(claseBodOrig)
                || "90".equals(claseBodOrig)) {
                serieDevBloq = false;
                marcaBloq = false;
            }

            /*
             * Si la clase de bodega destino es 20 y concepto de
             * movimiento es reintegro, se bloquea el valor unitario
             * del detalle
             */

            if ("20".equals(claseBodDest) && "R".equals(cptoMov)) {
                vlrUnitBloq = false;
            }
        }
        else {
        	
            
            if("SI".equals(permiteEspecificacion)) {
            	 vlrUnitBloq = false;
            	
            }else {
            	 vlrUnitBloq = true;
              
            }
        }

        /*
         * validar el tipo de elemento y bodega origen para visualizar
         * y determinar el comportamiento del combo serie.
         */
        if ("C".equals(tipoElemento)
            || CLASEBODEGASERIE.contains(claseBodOrig)) {
            ocultarSerie = false;
            registroSub.getCampos().put(CAMPO_SERIE, "0");
            bloqSerie = true;
        }
        else {
            ocultarSerie = true;
            bloqSerie = false;
        }
        ultFechaMov = traerUltimaFechaMovimiento();

        camposNiifVisible = "SI".equals(manNiifAlmacen) ? true: false;
        bloqueaNiif = camposNiifVisible;
        if (("S".equals(claseMov)) && "D".equals(tipoElemento)) {
            // || "T".equals(claseMov)
            verComponente = true;
            verUbicacion = true;

        }
        else {
            verComponente = false;
            verUbicacion = false;
        }
        
        try {
        boolean causacion = SysmanFunciones.nvl((ejbSysmanUtil.consultarParametro(compania,
				   "MANEJA CAUSACION AUTOMATICA", "1",new Date(), false)),"NO").equals("SI");
        boolean registrado = (boolean) SysmanFunciones.nvl(registro.getCampos().get("REGISTRADO"),false);
        if (causacion && registrado) {
        	bloqInterfaz = false;
        } else {
        	bloqInterfaz = true;
        }
        } catch (SystemException ex) {
            Logger.getLogger(TipomovimientosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        
    }

    /**
     * Trae la ultima fecha de movimiento en Almacen.
     *
     * @return fecha
     */
    private Date traerUltimaFechaMovimiento() {
        Date ultimaFechaMovimiento = new Date();
        ultimaFechaMovimiento = SysmanFunciones
                        .truncarFecha(ultimaFechaMovimiento);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        Registro regUltFecha = null;
        String urlEnumId = MovimientosControladorUrlEnum.URL263267.getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try {
            Parameter parameter = requestManager.get(url, param);
            regUltFecha = RegistroConverter.toRegistro(parameter);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (regUltFecha != null
            && regUltFecha.getCampos().get("ULTFECHAMOV") != null) {
            ultimaFechaMovimiento = (Date) regUltFecha.getCampos()
                            .get("ULTFECHAMOV");
        }
        return ultimaFechaMovimiento;
    }

    /**
     * Acciones despues de cambiar el valor en el campo "Ajuste
     * Manual".
     */
    public void cambiarDecManual() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control Registrado
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     */
    public void cambiarRegistrado() {

    	if (claseMov.equals("S") && tipoElemento.equals("C")) {

    		try {

    			Map<String, Object> param = new HashMap<>();
    			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    			param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
    					registro.getCampos().get(CAMPO_DEPDESTINO).toString());
    			List<Registro> aux;

    			aux = RegistroConverter
    					.toListRegistro(
    							requestManager.getList(
    									UrlServiceUtil.getInstance()
    									.getUrlServiceByUrlByEnumID(
    											MovimientosControladorUrlEnum.URL62113
    											.getValue())
    									.getUrl(),
    									param));

    			boolean manCompaniaDes = (boolean) aux.get(0).getCampos().get("MAN_COMPANIADES");

    			if(manCompaniaDes) {

    				int anio = Integer.parseInt(anoMov);
    				String respuesta = almacenCincoRemote.crearMovimientoCompania(compania,tipoMov, 
    						Long.parseLong(registro.getCampos().get(CAMPO_NUMERO).toString()),
    						anio,
    						SessionUtil.getUser().getCodigo());

    				if (respuesta.startsWith("OK,")) {
    					JsfUtil.agregarMensajeInformativoDialogo(respuesta.substring(3));
    					agregarRegistroNuevo(false);
    				} else if (respuesta.startsWith("ERROR,")) {
    					JsfUtil.agregarMensajeErrorDialogo(respuesta.substring(6));
    					registro.getCampos().put(CAMPO_REGISTRADO, false);
    				}

    			}

    		} catch (NumberFormatException | SystemException e) {
    			e.printStackTrace();
    		}

    	}

    }

    /**
     * Metodo que valida si los campos requeridos para realizar el
     * calculo del valor total por elemento tiene valores validos o
     * no. Si es correcto retornara true
     */
    public boolean validarCamposCalculo(Registro auxRegistro) {
        boolean retorno = true;

        if (SysmanFunciones.validarCampoVacio(auxRegistro.getCampos(),
                        CAMPO_CANTIDAD)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2016"));
            retorno = false;
        }

        if (SysmanFunciones.validarCampoVacio(auxRegistro.getCampos(),
                        CAMPO_VALORUNITARIO)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2017"));
            retorno = false;
        }
        if (SysmanFunciones.validarCampoVacio(auxRegistro.getCampos(),
                        CAMPO_PORCIVA)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2018"));
            retorno = false;
        }

        return retorno;
    }

    public void cambiarvalorAntesIVA() {
        valorAntesIVA = extraerBoolean(
                        registro.getCampos().get(CAMPO_VLRUNITARIO_SINIVA));
        agregarRegistroNuevo(false);
    }

    public void cambiarVlrUnitarioSinIVA() {
        calcularVlrTotal(registroSub);
        calcularVlrImpconsumo(registroSub);
        calcularVlrAIU(registroSub);
        calcularValoresAIU(registroSub);
    }

    public void cambiarVlrUnitarioSinIVAC(int rowNum) {
        calcularVlrTotal(listaSubdmovimiento.getDatasource().get(rowNum % 10));
        calcularVlrImpconsumo(listaSubdmovimiento.getDatasource().get(rowNum % 10));
        calcularVlrAIU(listaSubdmovimiento.getDatasource().get(rowNum % 10));
        calcularValoresAIU(listaSubdmovimiento.getDatasource().get(rowNum % 10));
    }
    
    public void cambiarporcAdministracion() {
    	calcularPorAIU();
//    	calcularVlrAIU(registroSub);
    	
    }
    
    public void cambiarporcImprevistos() {
    	calcularPorAIU();
//    	calcularVlrAIU(registroSub);
    	
    }
    
    public void cambiarporcUtilidades() {
    	calcularPorAIU();
//    	calcularVlrAIU(registroSub);
    	
    }

    /**
     * Ajusta el valor del campo <b>SALDOCANT</b> evaluando el tipo de
     * movimiento
     * 
     * @param reg
     * Registro del subformulario del Detalle del Movimiento
     * @param existenciaAux
     * Existencia actual del elemento que se esta trabajando
     */
    public void actualizarSaldoCantidad(Registro reg, double existenciaAux) {
        if ("C".equals(tipoElemento)) {
            double cantidadAux = extraerInteger(
                            reg.getCampos().get(CAMPO_CANTIDAD));
            double saldoCantidadAux = "E".equals(claseMov)
                ? existenciaAux + cantidadAux
                : existenciaAux - cantidadAux;

            reg.getCampos().put(CAMPO_SALDOCANT, saldoCantidadAux);
            String paramCantMax = getParametro("CONTROLA CANTIDADES MAXIMAS DE INVENTARIO",
					" ").toUpperCase();
			String paramCantMin = getParametro("CONTROLA CANTIDADES MINIMAS DE INVENTARIO",
					" ").toUpperCase();

			if(cantidadAux != 0 && paramCantMax.equals("SI") && claseMov.equals("E")) {



				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(), elemento);



				try {
					Registro aux2 = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											MovimientosControladorUrlEnum.URL1419
											.getValue())
									.getUrl(),
									param));

					double cantMax = extraerDouble(((Registro) aux2).getCampos().get("CANTIDADMAXIMA"));
					
					if(saldoCantidadAux > cantMax) {
						int rta = (int) (saldoCantidadAux - cantMax);
						JsfUtil.agregarMensajeInformativo("LA CANTIDAD SOBREPASA LAS CANTIDADES MAXIMAS EN ALMACEN EN " + rta);
					}
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}

			}else if(cantidadAux != 0 && paramCantMin.equals("SI") && claseMov.equals("S")) {


				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(), 
						SysmanFunciones.nvl(elemento, registroSub.getCampos().get("ELEMENTO_PEPS")));



				try {
					Registro aux2 = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											MovimientosControladorUrlEnum.URL1419
											.getValue())
									.getUrl(),
									param));

					double cantMin = extraerDouble(((Registro) aux2).getCampos().get("CANTIDADMINIMA"));
					if(saldoCantidadAux < cantMin) {
						//int rta = (int) (cantMin - saldoCantidadAux);
						JsfUtil.agregarMensajeInformativo("QUEDAN MENOS DE " + cantMin + " CANTIDADES EN EL INVENTARIO");
					}
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			}
		}
	}
    /**
     * Ajusta el valor del campo <b>SALDOCANT</b> cuando se esta
     * editando un registro del subformulario <i>Detalle
     * Movimiento</i> evaluando el tipo de movimiento
     * 
     * @param reg
     * Registro del subformulario Detalle Movimiento en el que se esta
     * trabajando
     * @param existenciaAux
     * Existencia actual del elemento que se esta trabajando
     */
    public void actualizarSaldoCantidadC(Registro reg, double existenciaAux) {
        double cantidadAux;
        if ("C".equals(tipoElemento)) {
            cantidadAux = -cantidadAnterior
                + extraerDouble(reg.getCampos().get(CAMPO_CANTIDAD));

            double saldoCantidadAux = "E".equals(claseMov)
                ? existenciaAux + cantidadAux
                : existenciaAux - cantidadAux;
            reg.getCampos().put(CAMPO_SALDOCANT, saldoCantidadAux);

        }
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
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

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Extrae el boolean que representa el objeto.
     *
     * @param object
     * @return
     */
    private boolean extraerBoolean(Object object) {
        boolean valor;
        if (object == null) {
            valor = Boolean.FALSE;
        }
        else if (object instanceof String) {
            valor = Boolean.parseBoolean((String) object);
        }
        else {
            valor = (boolean) object;
        }
        return valor;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return int que representa al objeto
     */
    private int extraerInteger(Object object) {
        return object != null ? Integer.parseInt(object.toString()) : 0;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return double que representa al objeto
     */
    private double extraerDouble(Object object) {
        return object != null ? Double.parseDouble(object.toString()) : 0.0;
    }

    /*
     * Getters y Setters.
     */
    public boolean isIndiceSubdmovimiento() {
        return indiceSubdmovimiento;
    }

    public void setIndiceSubdmovimiento(boolean indiceSubdmovimiento) {
        this.indiceSubdmovimiento = indiceSubdmovimiento;
    }

    public String getNuevoNumero() {
        return nuevoNumero;
    }

    public void setNuevoNumero(String nuevoNumero) {
        this.nuevoNumero = nuevoNumero;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public List<Registro> getListaNumero() {
        return listaNumero;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setListaNumero(List<Registro> listaNumero) {
        this.listaNumero = listaNumero;
    }

    public List<Registro> getListaTipoMovAsociado1() {
        return listaTipoMovAsociado1;
    }

    public void setListaTipoMovAsociado1(List<Registro> listaTipoMovAsociado1) {
        this.listaTipoMovAsociado1 = listaTipoMovAsociado1;
    }

    public List<Registro> getListaDependenciaAnt() {
        return listaDependenciaAnt;
    }

    public void setListaDependenciaAnt(List<Registro> listaDependenciaAnt) {
        this.listaDependenciaAnt = listaDependenciaAnt;
    }

    public RegistroDataModelImpl getListaAuxiliarSub() {
        return listaAuxiliarSub;
    }

    public void setListaAuxiliarSub(RegistroDataModelImpl listaAuxiliarSub) {
        this.listaAuxiliarSub = listaAuxiliarSub;
    }

    public RegistroDataModelImpl getListaSubdmovimiento() {
        return listaSubdmovimiento;
    }

    public void setListaSubdmovimiento(
        RegistroDataModelImpl listaSubdmovimiento) {
        this.listaSubdmovimiento = listaSubdmovimiento;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListaSerie() {
        return listaSerie;
    }

    public void setListaSerie(RegistroDataModelImpl listaSerie) {
        this.listaSerie = listaSerie;
    }

    public RegistroDataModelImpl getListaSerieE() {
        return listaSerieE;
    }

    public void setListaSerieE(RegistroDataModelImpl listaSerieE) {
        this.listaSerieE = listaSerieE;
    }

    public String getCptoMov() {
        return cptoMov;
    }

    public void setCptoMov(String cptoMov) {
        this.cptoMov = cptoMov;
    }

    public String getClaseMov() {
        return claseMov;
    }

    public void setClaseMov(String claseMov) {
        this.claseMov = claseMov;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public boolean getManVtas() {
        return manVtas;
    }

    public void setManVtas(boolean manVtas) {
        this.manVtas = manVtas;
    }

    public String getEtiquetaObs() {
        return etiquetaObs;
    }

    public void setEtiquetaObs(String etiquetaObs) {
        this.etiquetaObs = etiquetaObs;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public boolean isTieneDetalles() {
        return tieneDetalles;
    }

    public void setTieneDetalles(boolean tieneDetalles) {
        this.tieneDetalles = tieneDetalles;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaMovAsociado() {
        return listaMovAsociado;
    }

    public void setListaMovAsociado(RegistroDataModelImpl listaMovAsociado) {
        this.listaMovAsociado = listaMovAsociado;
    }
    
    public RegistroDataModelImpl getListaMovAsociadoComp() {
        return listaMovAsociadoComp;
    }

    public void setListaMovAsociadoComp(RegistroDataModelImpl listaMovAsociadoComp) {
        this.listaMovAsociadoComp = listaMovAsociadoComp;
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaResponsableOrigen() {
        return listaResponsableOrigen;
    }

    public void setListaResponsableOrigen(
        RegistroDataModelImpl listaResponsableOrigen) {
        this.listaResponsableOrigen = listaResponsableOrigen;
    }

    public String getNomProveedor() {
        return nomProveedor;
    }

    public void setNomProveedor(String nomProveedor) {
        this.nomProveedor = nomProveedor;
    }

    public String getNomAuxiliar() {
        return nomAuxiliar;
    }

    public void setNomAuxiliar(String nomAuxiliar) {
        this.nomAuxiliar = nomAuxiliar;
    }

    public List<Registro> getListaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListaCmbDependencia(List<Registro> listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    public RegistroDataModelImpl getListaTipoActivoNiif() {
        return listaTipoActivoNiif;
    }

    public void setListaTipoActivoNiif(
        RegistroDataModelImpl listaTipoActivoNiif) {
        this.listaTipoActivoNiif = listaTipoActivoNiif;
    }

    public RegistroDataModelImpl getListaTipoActivoNiifE() {
        return listaTipoActivoNiifE;
    }

    public void setListaTipoActivoNiifE(
        RegistroDataModelImpl listaTipoActivoNiifE) {
        this.listaTipoActivoNiifE = listaTipoActivoNiifE;
    }

    /**
     * Retorna el nombre del responsable final a cargo del bien.
     *
     * @return nombre del responsable final.
     */
    public String getNomRecibidoPor() {
        return nomRecibidoPor;
    }

    /**
     * Asigna el valor del campo de texto Recibido Por.
     *
     * @param nomRecibidoPor
     * Nombre del tercero.
     */
    public void setNomRecibidoPor(String nomRecibidoPor) {
        this.nomRecibidoPor = nomRecibidoPor;
    }

    public RegistroDataModelImpl getListaCentroDeCosto() {
        return listaCentroDeCosto;
    }

    public void setListaCentroDeCosto(
        RegistroDataModelImpl listaCentroDeCosto) {
        this.listaCentroDeCosto = listaCentroDeCosto;
    }

    public RegistroDataModelImpl getListaRecibidoPor() {
        return listaRecibidoPor;
    }

    public void setListaRecibidoPor(RegistroDataModelImpl listaRecibidoPor) {
        this.listaRecibidoPor = listaRecibidoPor;
    }

    public RegistroDataModelImpl getListaResponsableDestino() {
        return listaResponsableDestino;
    }

    public void setListaResponsableDestino(
        RegistroDataModelImpl listaResponsableDestino) {
        this.listaResponsableDestino = listaResponsableDestino;
    }

    public RegistroDataModelImpl getListaCmbProveedor() {
        return listaCmbProveedor;
    }

    public RegistroDataModelImpl getListaBodegaOrigen() {
        return listaBodegaOrigen;
    }

    public void setListaBodegaOrigen(RegistroDataModelImpl listaBodegaOrigen) {
        this.listaBodegaOrigen = listaBodegaOrigen;
    }

    public RegistroDataModelImpl getListaBodegaDestino() {
        return listaBodegaDestino;
    }

    public void setListaBodegaDestino(
        RegistroDataModelImpl listabodegaDestino) {
        this.listaBodegaDestino = listabodegaDestino;
    }

    public void setListaCmbProveedor(RegistroDataModelImpl listaCmbProveedor) {
        this.listaCmbProveedor = listaCmbProveedor;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getNomRespOrigen() {
        return nomRespOrigen;
    }

    public void setNomRespOrigen(String nomRespOrigen) {
        this.nomRespOrigen = nomRespOrigen;
    }

    public String getNomTipoAsociado() {
        return nomTipoAsociado;
    }

    public void setNomTipoAsociado(String nomTipoAsociado) {
        this.nomTipoAsociado = nomTipoAsociado;
    }

    public String getNomCentroCosto() {
        return nomCentroCosto;
    }

    public void setNomCentroCosto(String nomCentroCosto) {
        this.nomCentroCosto = nomCentroCosto;
    }

    public String getVlrAjusteCentavos() {
        return vlrAjusteCentavos;
    }

    public void setVlrAjusteCentavos(String vlrAjusteCentavos) {
        this.vlrAjusteCentavos = vlrAjusteCentavos;
    }

    public String getNomRespDestino() {
        return nomRespDestino;
    }

    public void setNomRespDestino(String nomRespDestino) {
        this.nomRespDestino = nomRespDestino;
    }

    public String getNomTercero() {
        return nomTercero;
    }

    public void setNomTercero(String nomTercero) {
        this.nomTercero = nomTercero;
    }

    public boolean isPideCCto() {
        return pideCCto;
    }

    public void setPideCCto(boolean pideCCto) {
        this.pideCCto = pideCCto;
    }

    public long getNroMovimiento() {
        return nroMovimiento;
    }

    public void setNroMovimiento(long nroMovimiento) {
        this.nroMovimiento = nroMovimiento;
    }

    public String getNomElemento() {
        return nomElemento;
    }

    public void setNomElemento(String nomElemento) {
        this.nomElemento = nomElemento;
    }

    public String getNomBodOrigen() {
        return nomBodOrigen;
    }

    public void setNomBodOrigen(String nomBodOrigen) {
        this.nomBodOrigen = nomBodOrigen;
    }

    public String getNomBodDestino() {
        return nomBodDestino;
    }

    public void setNomBodDestino(String nomBodDestino) {
        this.nomBodDestino = nomBodDestino;
    }

    public boolean isTipoDocumentoBloq() {
        return tipoDocumentoBloq;
    }

    public void setTipoDocumentoBloq(boolean tipoDocumentoBloq) {
        this.tipoDocumentoBloq = tipoDocumentoBloq;
    }

    public String getTxtTotalSinRedo() {
        return txtTotalSinRedo;
    }

    public void setTxtTotalSinRedo(String txtTotalSinRedo) {
        this.txtTotalSinRedo = txtTotalSinRedo;
    }

    public String getTotalConAjuste() {
        return totalConAjuste;
    }

    public void setTotalConAjuste(String totalConAjuste) {
        this.totalConAjuste = totalConAjuste;
    }

    public String getNombreLargo() {
        return nombreLargo;
    }

    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getVlrAjusteRedondeo() {
        return vlrAjusteRedondeo;
    }

    public void setVlrAjusteRedondeo(String vlrAjusteRedondeo) {
        this.vlrAjusteRedondeo = vlrAjusteRedondeo;
    }

    public String getAjusteCentavos() {
        return ajusteCentavos;
    }

    public void setAjusteCentavos(String ajusteCentavos) {
        this.ajusteCentavos = ajusteCentavos;
    }

    public String getClaseDocAsMov() {
        return claseDocAsMov;
    }

    public void setClaseDocAsMov(String claseDocAsMov) {
        this.claseDocAsMov = claseDocAsMov;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public boolean isGeneraPlaca() {
        return generaPlaca;
    }

    public void setGeneraPlaca(boolean generaPlaca) {
        this.generaPlaca = generaPlaca;
    }

    public boolean isPermiteModificar() {
        return permiteModificar;
    }

    public void setPermiteModificar(boolean permiteModificar) {
        this.permiteModificar = permiteModificar;
    }

    public boolean isObligaTercero() {
        return obligaTercero;
    }

    public void setObligaTercero(boolean obligaTercero) {
        this.obligaTercero = obligaTercero;
    }

    public boolean isObligaOrigen() {
        return obligaOrigen;
    }

    public void setObligaOrigen(boolean obligaOrigen) {
        this.obligaOrigen = obligaOrigen;
    }

    public boolean isObligaDestino() {
        return obligaDestino;
    }

    public void setObligaDestino(boolean obligaDestino) {
        this.obligaDestino = obligaDestino;
    }

    public boolean isObligaProveedor() {
        return obligaProveedor;
    }

    public void setObligaProveedor(boolean obligaProveedor) {
        this.obligaProveedor = obligaProveedor;
    }

    public boolean isCantBloq() {
        return cantBloq;
    }

    public void setCantBloq(boolean cantBloq) {
        this.cantBloq = cantBloq;
    }

    public boolean isOcultarSerie() {
        return ocultarSerie;
    }

    public void setOcultarSerie(boolean ocultarSerie) {
        this.ocultarSerie = ocultarSerie;
    }

    public boolean isCamposNiifVisible() {
        return camposNiifVisible;
    }

    public void setCamposNiifVisible(boolean camposNiifVisible) {
        this.camposNiifVisible = camposNiifVisible;
    }

    public boolean isBloqSerie() {
        return bloqSerie;
    }

    public void setBloqSerie(boolean bloqSerie) {
        this.bloqSerie = bloqSerie;
    }

    public int getEditarMovimiento() {
        return editarMovimiento;
    }

    public boolean isSerieDevBloq() {
        return serieDevBloq;
    }

    public void setSerieDevBloq(boolean serieDevBloq) {
        this.serieDevBloq = serieDevBloq;
    }

    public boolean isMarcaBloq() {
        return marcaBloq;
    }

    public void setMarcaBloq(boolean marcaBloq) {
        this.marcaBloq = marcaBloq;
    }

    public boolean isVlrUnitBloq() {
        return vlrUnitBloq;
    }

    public void setVlrUnitBloq(boolean vlrUnitBloq) {
        this.vlrUnitBloq = vlrUnitBloq;
    }

    public boolean isValorAntesIVA() {
        return valorAntesIVA;
    }

    public void setValorAntesIVA(boolean valorAntesIVA) {
        this.valorAntesIVA = valorAntesIVA;
    }

    public boolean isMostrarValorAntesIVA() {
        return mostrarValorAntesIVA;
    }

    public void setMostrarValorAntesIVA(boolean mostrarValorAntesIVA) {
        this.mostrarValorAntesIVA = mostrarValorAntesIVA;
    }

    /**
     * Retorna el valor del indicador maneja recibido.
     *
     * @return Verdadero si se permite mostrar el campo recibido por.
     */
    public boolean isManejaRecibido() {
        return manejaRecibido;
    }

    /**
     * Asigna el valor al indicador maneja recibido.
     *
     * @param manejaRecibido
     */
    public void setManejaRecibido(boolean manejaRecibido) {
        this.manejaRecibido = manejaRecibido;
    }

    /**
     * Retorna el valor de la variable bloqueado.
     *
     * @return Verdadero si se bloquea los botones en la forma.
     */
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Asigna el valor a la variable bloqueado.
     *
     * @param bloqueado
     */
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    /**
     * Retorna el valor de la variable bloqueaDependencia.
     *
     * @return Verdadero si se bloquea el combo de dependencias.
     */
    public boolean isBloqueaDependencia() {
        return bloqueaDependencia;
    }

    /**
     * Asigna el valor a la variable bloqueaDependencia.
     *
     * @param bloqueaDependencia
     */
    public void setBloqueaDependencia(boolean bloqueaDependencia) {
        this.bloqueaDependencia = bloqueaDependencia;
    }

    /**
     * Retorna el valor de la variable etiquetaTercero.
     *
     * @return the etiquetaTercero
     */
    public String getEtiquetaTercero() {
        return etiquetaTercero;
    }

    /**
     * Asigna el valor a la variable etiquetaTercero.
     *
     * @param etiquetaTercero
     * the etiquetaTercero to set
     */
    public void setEtiquetaTercero(String etiquetaTercero) {
        this.etiquetaTercero = etiquetaTercero;
    }

    public double getExistenciaElemento() {
        return existenciaElemento;
    }

    public void setExistenciaElemento(double existenciaElemento) {
        this.existenciaElemento = existenciaElemento;
    }

    public RegistroDataModelImpl getListaVidaUtilPlaca() {
        return listaVidaUtilPlaca;
    }

    public void setListaVidaUtilPlaca(
        RegistroDataModelImpl listaVidaUtilPlaca) {
        this.listaVidaUtilPlaca = listaVidaUtilPlaca;
    }

    public RegistroDataModelImpl getListaVidaUtilPlacaE() {
        return listaVidaUtilPlacaE;
    }

    public void setListaVidaUtilPlacaE(
        RegistroDataModelImpl listaVidaUtilPlacaE) {
        this.listaVidaUtilPlacaE = listaVidaUtilPlacaE;
    }

    public RegistroDataModelImpl getListavidaUtilPlacaNiif() {
        return listavidaUtilPlacaNiif;
    }

    public void setListavidaUtilPlacaNiif(
        RegistroDataModelImpl listavidaUtilPlacaNiif) {
        this.listavidaUtilPlacaNiif = listavidaUtilPlacaNiif;
    }

    public RegistroDataModelImpl getListavidaUtilPlacaNiifE() {
        return listavidaUtilPlacaNiifE;
    }

    public void setListavidaUtilPlacaNiifE(
        RegistroDataModelImpl listavidaUtilPlacaNiifE) {
        this.listavidaUtilPlacaNiifE = listavidaUtilPlacaNiifE;
    }

    public RegistroDataModelImpl getListacomponente() {
        return listacomponente;
    }

    public void setListacomponente(RegistroDataModelImpl listacomponente) {
        this.listacomponente = listacomponente;
    }

    public RegistroDataModelImpl getListacomponenteE() {
        return listacomponenteE;
    }

    public void setListacomponenteE(RegistroDataModelImpl listacomponenteE) {
        this.listacomponenteE = listacomponenteE;
    }

    public boolean isVerUbicacion() {
        return verUbicacion;
    }

    public void setVerUbicacion(boolean verUbicacion) {
        this.verUbicacion = verUbicacion;
    }

    public boolean isVerComponente() {
        return verComponente;
    }

    public void setVerComponente(boolean verComponente) {
        this.verComponente = verComponente;
    }

    public boolean isVerDocumentos() {
        return verDocumentos;
    }

    public void setVerDocumentos(boolean verDocumentos) {
        this.verDocumentos = verDocumentos;
    }

    /**
     * @return the listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    /**
     * @param listaReferencia
     * the listaReferencia to set
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    /**
     * @return the listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }

    /**
     * @param listaFuenteRecurso
     * the listaFuenteRecurso to set
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    /**
     * @return the nomFuenteRecurso
     */
    public String getNomFuenteRecurso() {
        return nomFuenteRecurso;
    }

    /**
     * @param nomFuenteRecurso
     * the nomFuenteRecurso to set
     */
    public void setNomFuenteRecurso(String nomFuenteRecurso) {
        this.nomFuenteRecurso = nomFuenteRecurso;
    }

    /**
     * @return the nomReferencia
     */
    public String getNomReferencia() {
        return nomReferencia;
    }

    /**
     * @param nomReferencia
     * the nomReferencia to set
     */
    public void setNomReferencia(String nomReferencia) {
        this.nomReferencia = nomReferencia;
    }

    public boolean isBloqueadoPeriodo() {
        return bloqueadoPeriodo;
    }

    public void setBloqueadoPeriodo(boolean bloqueadoPeriodo) {
        this.bloqueadoPeriodo = bloqueadoPeriodo;
    }

    public boolean isVisibleCopiar() {
        return visibleCopiar;
    }

    public void setVisibleCopiar(boolean visibleCopiar) {
        this.visibleCopiar = visibleCopiar;
    }

    public boolean isActivoCopiar() {
        return activoCopiar;
    }

    public void setActivoCopiar(boolean activoCopiar) {
        this.activoCopiar = activoCopiar;
    }

    /**
     * @return the listaEstado
     */
    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    /**
     * @param listaEstado
     * the listaEstado to set
     */
    public void setListaEstado(List<Registro> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * @return the validarEstado
     */
    public boolean isValidarEstado() {
        return validarEstado;
    }

    /**
     * @param validarEstado
     * the validarEstado to set
     */
    public void setValidarEstado(boolean validarEstado) {
        this.validarEstado = validarEstado;
    }

    public boolean isOcultarEliminarLote() {
        return ocultarEliminarLote;
    }

    public void setOcultarEliminarLote(boolean ocultarEliminarLote) {
        this.ocultarEliminarLote = ocultarEliminarLote;
    }

    public boolean isObligaCampos() {
        return obligaCampos;
    }

    public void setObligaCampos(boolean obligaCampos) {
        this.obligaCampos = obligaCampos;
    }

    public boolean isManOrfeo() {
        return manOrfeo;
    }

    public void setManOrfeo(boolean manOrfeo) {
        this.manOrfeo = manOrfeo;
    }

    public boolean isVerElemento() {
        return verElemento;
    }

    public void setVerElemento(boolean verElemento) {
        this.verElemento = verElemento;
    }
    
    public boolean isVerElementoIdi() {
        return verElementoIdi;
    }

    public void setVerElementoIdi(boolean verElementoIdi) {
        this.verElementoIdi = verElementoIdi;
    }
    
    public boolean isCargarPlacaNiif() {
        return cargarPlacaNiif;
    }

    public void setCargarPlacaNiif(boolean cargarPlacaNiif) {
        this.cargarPlacaNiif = cargarPlacaNiif;
    }
     
    public boolean isVisibleAdicionales() {
		return visibleAdicionales;
	}

	public void setVisibleAdicionales(boolean visibleAdicionales) {
		this.visibleAdicionales = visibleAdicionales;
	}

	public boolean isProyAuxVisible() {
		return proyAuxVisible;
	}

	public void setProyAuxVisible(boolean proyAuxVisible) {
		this.proyAuxVisible = proyAuxVisible;
	}

	public boolean isRuedaOperVisible() {
		return ruedaOperVisible;
	}

	public void setRuedaOperVisible(boolean ruedaOperVisible) {
		this.ruedaOperVisible = ruedaOperVisible;
	}

	public String getNomProyecto() {
		return nomProyecto;
	}

	public void setNomProyecto(String nomProyecto) {
		this.nomProyecto = nomProyecto;
	}
	
	public String getManElementoPepsIdi()
    {
        return manElementoPepsIdi;
    }

    public void setManElementoPepsIdi(String manElementoPepsIdi)
    {
    	this.manElementoPepsIdi = manElementoPepsIdi;
    }
    
    public String getManAuxiliares()
    {
        return manAuxiliares;
    }

    public void setManAuxiliares(String manAuxiliares)
    {
    	this.manAuxiliares = manAuxiliares;
    }
    
    public boolean isBloqueaAux() {
        return bloqueaAux;
    }

    public void setBloqueaAux(boolean bloqueaAux) {
        this.bloqueaAux = bloqueaAux;
    }
    
    public boolean isBloqueaAuxCont() {
        return bloqueaAuxCont;
    }

    public void setBloqueaAuxCont(boolean bloqueaAuxCont) {
        this.bloqueaAuxCont = bloqueaAuxCont;
    }

	public RegistroDataModelImpl getListaProyecto() {
		return listaProyecto;
	}

	public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
		this.listaProyecto = listaProyecto;
	} 
    
	public RegistroDataModelImpl getListaElementoIdi() {
        return listaElementoIdi;
    }

    public void setListaElementoIdi(RegistroDataModelImpl listaElementoIdi) {
        this.listaElementoIdi = listaElementoIdi;
    }

    public RegistroDataModelImpl getListaElementoIdiE() {
        return listaElementoIdiE;
    }

    public void setListaElementoIdiE(RegistroDataModelImpl listaElementoIdiE) {
        this.listaElementoIdiE = listaElementoIdiE;
    }
    
    public RegistroDataModelImpl getListaElementoAux() {
        return listaElementoAux;
    }

    public void setListaElementoAux(RegistroDataModelImpl listaElementoAux) {
        this.listaElementoAux = listaElementoAux;
    }

    public RegistroDataModelImpl getListaElementoAuxE() {
        return listaElementoAuxE;
    }

    public void setListaElementoAuxE(RegistroDataModelImpl listaElementoAuxE) {
        this.listaElementoAuxE = listaElementoAuxE;
    }
    
    
    /**
	 * @return the listaUnidad
	 */
	public RegistroDataModelImpl getListaUnidad() {
		return listaUnidad;
	}

	/**
	 * @param listaUnidad the listaUnidad to set
	 */
	public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
		this.listaUnidad = listaUnidad;
	}

	/**
	 * @return the listaUnidadE
	 */
	public RegistroDataModelImpl getListaUnidadE() {
		return listaUnidadE;
	}

	/**
	 * @param listaUnidadE the listaUnidadE to set
	 */
	public void setListaUnidadE(RegistroDataModelImpl listaUnidadE) {
		this.listaUnidadE = listaUnidadE;
	}
	
	/**
	 * @return the pideVencimiento
	 */
	public boolean isPideVencimiento() {
		return pideVencimiento;
	}

	/**
	 * @param pideVencimiento the pideVencimiento to set
	 */
	public void setPideVencimiento(boolean pideVencimiento) {
		this.pideVencimiento = pideVencimiento;
	}
	
	
	public boolean isVerElementoLote() {
		return verElementoLote;
	}
	
	public boolean isExcluyeConsumo() {
		return excluyeConsumo;
	}

	public void setExcluyeConsumo(boolean excluyeConsumo) {
		this.excluyeConsumo = excluyeConsumo;
	}
	
	public boolean isExcluyeDifConsumo() {
		return excluyeDifConsumo;
	}

	public void setExcluyeDifConsumo(boolean excluyeDifConsumo) {
		this.excluyeDifConsumo = excluyeDifConsumo;
	}

	public void setVerElementoLote(boolean verElementoLote) {
		this.verElementoLote = verElementoLote;
	}
	
	public RegistroDataModelImpl getListaElementoLote() {
		return listaElementoLote;
	}

	public void setListaElementoLote(RegistroDataModelImpl listaElementoLote) {
		this.listaElementoLote = listaElementoLote;
	}

	public RegistroDataModelImpl getListaElementoLoteE() {
		return listaElementoLoteE;
	}

	public void setListaElementoLoteE(RegistroDataModelImpl listaElementoLoteE) {
		this.listaElementoLoteE = listaElementoLoteE;
	}

	/**
	 * @return the contArchivoInventarioVencimiento
	 */
	public ContenedorArchivo getContArchivoInventarioVencimiento() {
		return contArchivoInventarioVencimiento;
	}

	/**
	 * @param contArchivoInventarioVencimiento the contArchivoInventarioVencimiento to set
	 */
	public void setContArchivoInventarioVencimiento(ContenedorArchivo contArchivoInventarioVencimiento) {
		this.contArchivoInventarioVencimiento = contArchivoInventarioVencimiento;
	}

	/**
	 * @return the visiblePlantilla
	 */
	public boolean isVisiblePlantilla() {
		return visiblePlantilla;
	}

	/**
	 * @param visiblePlantilla the visiblePlantilla to set
	 */
	public void setVisiblePlantilla(boolean visiblePlantilla) {
		this.visiblePlantilla = visiblePlantilla;
	}
	
	public boolean isCargaMovAsociado() {
        return cargaMovAsociado;
    }

    public void setCargaMovAsociado(boolean cargaMovAsociado) {
        this.cargaMovAsociado = cargaMovAsociado;
    }
    
    public boolean isCargaMovAsociadoComp() {
        return cargaMovAsociadoComp;
    }

    public void setCargaMovAsociadoComp(boolean cargaMovAsociadoComp) {
        this.cargaMovAsociadoComp = cargaMovAsociadoComp;
    }

    public boolean isBloqFuenteMov() {
        return bloqFuenteMov;
    }

    public void setBloqFuenteMov(boolean bloqFuenteMov) {
        this.bloqFuenteMov = bloqFuenteMov;
    }
    
    public boolean isBloqRefMov() {
        return bloqRefMov;
    }

    public void setBloqRefMov(boolean bloqRefMov) {
        this.bloqRefMov = bloqRefMov;
    }
    
    public boolean isBloqAuxMov() {
        return bloqAuxMov;
    }

    public void setBloqAuxMov(boolean bloqAuxMov) {
        this.bloqAuxMov = bloqAuxMov;
    }
    
    public boolean isBloqProyMov() {
        return bloqProyMov;
    }

    public void setBloqProyMov(boolean bloqProyMov) {
        this.bloqProyMov = bloqProyMov;
    }
    
    public boolean isBloqCCMov() {
        return bloqCCMov;
    }

    public void setBloqCCMov(boolean bloqCCMov) {
        this.bloqCCMov = bloqCCMov;
    }
    
    public boolean isCargaFuente() {
        return cargaFuente;
    }

    public void setCargaFuente(boolean cargaFuente) {
        this.cargaFuente = cargaFuente;
    }
    
    public boolean isCargaReferCnt() {
        return cargaReferCnt;
    }

    public void setCargaReferCnt(boolean cargaReferCnt) {
        this.cargaReferCnt = cargaReferCnt;
    }
    
    public boolean isCargaAuxDet() {
        return cargaAuxDet;
    }

    public void setCargaAuxDet(boolean cargaAuxDet) {
        this.cargaAuxDet = cargaAuxDet;
    }
    
    public boolean isCargaProyDet() {
        return cargaProyDet;
    }

    public void setCargaProyDet(boolean cargaProyDet) {
        this.cargaProyDet = cargaProyDet;
    }
    
    public boolean isCargaCCDet() {
        return cargaCCDet;
    }

    public void setCargaCCDet(boolean cargaCCDet) {
        this.cargaCCDet = cargaCCDet;
    }
    
    public boolean isCargaLoteDet() {
        return cargaLoteDet;
    }

    public void setCargaLoteDet(boolean cargaLoteDet) {
        this.cargaLoteDet = cargaLoteDet;
    }
    
	/**
	 * @return the listaFuenteRecursos
	 */
	public RegistroDataModelImpl getListaFuenteRecursos() {
		return listaFuenteRecursos;
	}

	/**
	 * @param listaFuenteRecursos the listaFuenteRecursos to set
	 */
	public void setListaFuenteRecursos(RegistroDataModelImpl listaFuenteRecursos) {
		this.listaFuenteRecursos = listaFuenteRecursos;
	}

	/**
	 * @return the listaFuenteRecursosE
	 */
	public RegistroDataModelImpl getListaFuenteRecursosE() {
		return listaFuenteRecursosE;
	}

	/**
	 * @param listaFuenteRecursosE the listaFuenteRecursosE to set
	 */
	public void setListaFuenteRecursosE(RegistroDataModelImpl listaFuenteRecursosE) {
		this.listaFuenteRecursosE = listaFuenteRecursosE;
	}
	
	public RegistroDataModelImpl getListaReferenciaCnt() {
		return listaReferenciaCnt;
	}

	public void setListaReferenciaCnt(RegistroDataModelImpl listaReferenciaCnt) {
		this.listaReferenciaCnt = listaReferenciaCnt;
	}

	public RegistroDataModelImpl getListaReferenciaCntE() {
		return listaReferenciaCntE;
	}

	public void setListaReferenciaCntE(RegistroDataModelImpl listaReferenciaCntE) {
		this.listaReferenciaCntE = listaReferenciaCntE;
	}
	
	public RegistroDataModelImpl getListaAuxiliarDet() {
		return listaAuxiliarDet;
	}

	public void setListaAuxiliarDet(RegistroDataModelImpl listaAuxiliarDet) {
		this.listaAuxiliarDet = listaAuxiliarDet;
	}

	public RegistroDataModelImpl getListaAuxiliarDetE() {
		return listaAuxiliarDetE;
	}

	public void setListaAuxiliarDetE(RegistroDataModelImpl listaAuxiliarDetE) {
		this.listaAuxiliarDetE = listaAuxiliarDetE;
	}
	
	public RegistroDataModelImpl getListaProyectoDet() {
		return listaProyectoDet;
	}

	public void setListaProyectoDet(RegistroDataModelImpl listaProyectoDet) {
		this.listaProyectoDet = listaProyectoDet;
	}

	public RegistroDataModelImpl getListaProyectoDetE() {
		return listaProyectoDetE;
	}

	public void setListaProyectoDetE(RegistroDataModelImpl listaProyectoDetE) {
		this.listaProyectoDetE = listaProyectoDetE;
	}
	
	public RegistroDataModelImpl getListaCentroCostoDet() {
		return listaCentroCostoDet;
	}

	public void setListaCentroCostoDet(RegistroDataModelImpl listaCentroCostoDet) {
		this.listaCentroCostoDet = listaCentroCostoDet;
	}

	public RegistroDataModelImpl getListaCentroCostoDetE() {
		return listaCentroCostoDetE;
	}

	public void setListaCentroCostoDetE(RegistroDataModelImpl listaCentroCostoDetE) {
		this.listaCentroCostoDetE = listaCentroCostoDetE;
	}

	/**
	 * @return the manejaFuente
	 */
	public boolean isManejaFuente() {
		return manejaFuente;
	}

	/**
	 * @param manejaFuente the manejaFuente to set
	 */
	public void setManejaFuente(boolean manejaFuente) {
		this.manejaFuente = manejaFuente;
	}

	/**
	 * @return the bloqueaFuente
	 */
	public boolean isBloqueaFuente() {
		return bloqueaFuente;
	}

	/**
	 * @param bloqueaFuente the bloqueaFuente to set
	 */
	public void setBloqueaFuente(boolean bloqueaFuente) {
		this.bloqueaFuente = bloqueaFuente;
	}	
	/**
	 * @return the listaUbicacion
	 */
	public RegistroDataModelImpl getListaUbicacion() {
		return listaUbicacion;
	}

	/**
	 * @param listaUbicacion the listaUbicacion to set
	 */
	public void setListaUbicacion(RegistroDataModelImpl listaUbicacion) {
		this.listaUbicacion = listaUbicacion;
	}

	/**
     * Arma la referencia del registro
     **/
	public String armarReferencia() {
		String referencia = "";
		String fecha = "";
		
		try {
			if (accionAuditoria.equals(EnumAcciones.DELETE.getValue()) && detalleMov == 0) {
				fecha = registro.getCampos().get("FECHAMOV").toString();
			} else {
				fecha = SysmanFunciones.convertirAFechaCadena((Date)registro.getCampos().get(CAMPO_FECHA),"dd/MM/yyyy");
			}
			
			referencia = detalleMov == -1? "DETALLE: ":"";
			referencia = referencia + "Mov.: " + registro.getCampos().get(CAMPO_NUMERO) +
					     " Tipo Mov.: " + tituloForm +
					     " Fecha Mov.: " + fecha;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return referencia;
	}
	/**
     * Ejecuta los procesos de auditoria
     **/
    @Override
	public void ejecutarAuditoria() {
    	if(manAuditoriaAlm.equals("SI")) {
	    	try {
				PojoAuditoria pojoAudit = new PojoAuditoria();
				AuditoriaProcesador2 auditoria = new AuditoriaProcesador2();
				
				pojoAudit.setCodproceso(auditoria.obtenerCodProceso(claseMov,cptoMov,tipoElemento));
				pojoAudit.setUsuario(SessionUtil.getUser().getCodigo());
				pojoAudit.setCodCompania(compania);
				pojoAudit.setCodEntidad(auditoria.obtenerCodEntidad(compania));
				pojoAudit.setEquipo(InetAddress.getLocalHost().getHostName());
				pojoAudit.setIp(InetAddress.getLocalHost().getHostAddress());
				pojoAudit.setReferencia(armarReferencia());
	
				if (accionAuditoria.equals(EnumAcciones.INSERT.getValue())) {	
					pojoAudit.setAccionAuditar(EnumAccionAuditoria.CREAR.getValue());
					obtenerValorActual();
					pojoAudit.setValActual(valActual);
				} else if (accionAuditoria.equals(EnumAcciones.UPDATE.getValue())) {
					pojoAudit.setAccionAuditar(EnumAccionAuditoria.ACTUALIZAR.getValue());
					pojoAudit.setValAnterior(valAnterior);  
					obtenerValorActual();
					pojoAudit.setValActual(valActual);
				} else if (accionAuditoria.equals(EnumAcciones.DELETE.getValue())) {
					pojoAudit.setAccionAuditar(EnumAccionAuditoria.BORRAR.getValue());
					pojoAudit.setValAnterior(valAnterior);
				}
				auditoria.validarDatos(pojoAudit);
			} catch (NegocioExcepcion | UnknownHostException e) {
				logger.error("Error ejecutar auditoria Motivo {}  ", e );
				e.printStackTrace();
			}
    	}
	}
    /**Campos auditables, quedan registrados como valActual del mov.
	 */
	@Override
	public void obtenerValorActual(Object... datos) {
		try {	
			valActual = new HashMap<>(); 
			valActual.put(CAMPO_COMPANIA, compania);
			
			if (detalleMov == -1) {
				obtenerValorActualDetalle();
			} else {
				valActual.put(CAMPO_TIPOMOV, tipoMov);
				valActual.put(CAMPO_NUMERO, registro.getCampos().get(CAMPO_NUMERO));
				valActual.put(CAMPO_TERCERO, registro.getCampos().get(CAMPO_TERCERO).toString());
				valActual.put(CAMPO_RESORIGEN, registro.getCampos().get(CAMPO_RESORIGEN).toString());
				valActual.put(CAMPO_RESDESTINO, registro.getCampos().get(CAMPO_RESDESTINO).toString());
				valActual.put("TIPOMOVASOCIADO", registro.getCampos().get("TIPOMOVASOCIADO"));
				valActual.put("MOVASOCIADO", registro.getCampos().get("MOVASOCIADO"));
				valActual.put(GeneralParameterEnum.CREATED_BY.getName(), registro.getCampos()
						.get(GeneralParameterEnum.CREATED_BY.getName()));
				valActual.put(GeneralParameterEnum.MODIFIED_BY.getName(), registro.getCampos()
						.get(GeneralParameterEnum.MODIFIED_BY.getName()));
				valActual.put(CAMPO_DEPORIGEN, registro.getCampos().get(CAMPO_DEPORIGEN));
				valActual.put(CAMPO_DEPDESTINO, registro.getCampos().get(CAMPO_DEPDESTINO));
				valActual.put(GeneralParameterEnum.DATE_CREATED.getName(), registro.getCampos()
						.get(GeneralParameterEnum.DATE_CREATED.getName()));
				valActual.put(GeneralParameterEnum.DATE_MODIFIED.getName(), registro.getCampos()
						.get(GeneralParameterEnum.DATE_MODIFIED.getName()));
				valActual.put("FECHAMOVASOCIADO", SysmanFunciones.convertirAFechaCadena(
						(Date)registro.getCampos().get("FECHAMOVASOCIADO"), "dd/MM/yyyy"));
				valActual.put(CAMPO_FECHA, SysmanFunciones.convertirAFechaCadena(
						(Date)registro.getCampos().get(CAMPO_FECHA), "dd/MM/yyyy"));
				valActual.put("FFINAL_CONTRATO", SysmanFunciones.convertirAFechaCadena(
						(Date)registro.getCampos().get("FFINAL_CONTRATO"), "dd/MM/yyyy"));
				valActual.put("HORA", registro.getCampos().get("HORA"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**Campos auditables, quedan registrados como valActual del detalle
	 */
	public void obtenerValorActualDetalle() {
		try {
			valActual.put(CAMPO_ELEMENTO, registroSub.getCampos().get(CAMPO_ELEMENTO));
			valActual.put(CAMPO_SERIE, registroSub.getCampos().get(CAMPO_SERIE));
			valActual.put(CAMPO_CANTIDAD, registroSub.getCampos().get(CAMPO_CANTIDAD));
			valActual.put(CAMPO_VALORUNITARIO, registroSub.getCampos().get(CAMPO_VALORUNITARIO));
			valActual.put(CAMPO_PORCIVA, registroSub.getCampos().get(CAMPO_PORCIVA));
			valActual.put(CAMPO_VALORTOTAL, registroSub.getCampos().get(CAMPO_VALORTOTAL));
			valActual.put(GeneralParameterEnum.CREATED_BY.getName(), registroSub.getCampos()
					.get(GeneralParameterEnum.CREATED_BY.getName()));
			valActual.put(GeneralParameterEnum.MODIFIED_BY.getName(), registroSub.getCampos()
					.get(GeneralParameterEnum.MODIFIED_BY.getName()));
			valActual.put("NIIF_VALOR_BASE", registroSub.getCampos().get("NIIF_VALOR_BASE"));
			valActual.put("REVELACIONES", registroSub.getCampos().get("REVELACIONES"));
			valActual.put("DETERIORO", registroSub.getCampos().get("DETERIORO"));
			valActual.put("NIIF_VALOR_TOTAL", registroSub.getCampos().get("NIIF_VALOR_TOTAL"));
			valActual.put("SALVAMENTO", registroSub.getCampos().get("SALVAMENTO"));
			valActual.put(GeneralParameterEnum.DATE_CREATED.getName(), registroSub.getCampos()
					.get(GeneralParameterEnum.DATE_CREATED.getName()));
			valActual.put(GeneralParameterEnum.DATE_MODIFIED.getName(), registroSub.getCampos()
					.get(GeneralParameterEnum.DATE_MODIFIED.getName()));
			valActual.put(CAMPO_FECHA, SysmanFunciones.convertirAFechaCadena(
					(Date)registroSub.getCampos().get(CAMPO_FECHA), "dd/MM/yyyy"));
			valActual.put("VLRIMPCONSUMO", registroSub.getCampos().get("VLRIMPCONSUMO"));
			valActual.put("APLICANIIF", registroSub.getCampos().get("APLICANIIF"));
			valActual.put("NIIF_VIDA_UTIL", registroSub.getCampos().get("NIIF_VIDA_UTIL"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**Consulta los valores que se encuentran en la base antes de actualizar 
	 * para el registro que tiene como referencia
	 */
	@Override
	public void obtenerValoresAnteriores(Object... datos) {
		try {
			valAnterior = new HashMap<>();
			Registro rs = null;
			Map<String, Object> param = new TreeMap<>();
			
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
			param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(CAMPO_NUMERO));
	
			if (detalleMov == -1) {
				/**valAnterior del detalle*/
				param.put(GeneralParameterEnum.CODIGO.getName(), registroSub.getCampos().get(CAMPO_CODIGO));
				
				rs = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
														MovimientosControladorUrlEnum.URL1422
																.getValue())
										.getUrl(),param));
			} else {
				/**valAnterior del mov.*/
				rs = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
														MovimientosControladorUrlEnum.URL1421
																.getValue())
										.getUrl(),param));
			}
			
			if (rs == null) {
				valAnterior = Collections.emptyMap();
			} else {
				valAnterior = rs.getCampos();
				
				if(detalleMov == 0) {
					valAnterior.put(CAMPO_TERCERO, rs.getCampos().get(CAMPO_TERCERO).toString());
					valAnterior.put(CAMPO_RESORIGEN, rs.getCampos().get(CAMPO_RESORIGEN).toString());
					valAnterior.put(CAMPO_RESDESTINO, rs.getCampos().get(CAMPO_RESDESTINO).toString());
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}

	public boolean isVisibleUbicacion() {
		return visibleUbicacion;
	}

	public void setVisibleUbicacion(boolean visibleUbicacion) {
		this.visibleUbicacion = visibleUbicacion;
	}
	
	private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
				new Date(), mayus);
	}
	
    public boolean isVisibleUbicacionTras() {
		return visibleUbicacionTras;
	}

	public void setVisibleUbicacionTras(boolean visibleUbicacionTras) {
		this.visibleUbicacionTras = visibleUbicacionTras;
	}
	
	public RegistroDataModelImpl getListaUbicacionOrigen() {
		return listaUbicacionOrigen;
	}

	public void setListaUbicacionOrigen(RegistroDataModelImpl listaUbicacionOrigen) {
		this.listaUbicacionOrigen = listaUbicacionOrigen;
	}

	public RegistroDataModelImpl getListaUbicacionDestino() {
		return listaUbicacionDestino;
	}

	public void setListaUbicacionDestino(RegistroDataModelImpl listaUbicacionDestino) {
		this.listaUbicacionDestino = listaUbicacionDestino;
	}

	public boolean isVerPorcValorResidual() {
		return verPorcValorResidual;
	}

	public void setVerPorcValorResidual(boolean verPorcValorResidual) {
		this.verPorcValorResidual = verPorcValorResidual;
	}

	public int getDigPorcValorResidual() {
		return digPorcValorResidual;
	}

	public void setDigPorcValorResidual(int digPorcValorResidual) {
		this.digPorcValorResidual = digPorcValorResidual;
	}
	
	public boolean isVerElementoAux() {
        return verElementoAux;
    }

    public void setVerElementoAux(boolean verElementoAux) {
        this.verElementoAux = verElementoAux;
    }
    
    public boolean isBloqInterfaz() {
        return bloqInterfaz;
    }

    public void setBloqInterfaz(boolean bloqInterfaz) {
        this.bloqInterfaz = bloqInterfaz;
    }

	public boolean isGeneraCausacion() {
		return generaCausacion;
	}

	public void setGeneraCausacion(boolean generaCausacion) {
		this.generaCausacion = generaCausacion;
	}
	public boolean isBloqResidual() {
		return bloqResidual;
	}

	public void setBloqResidual(boolean bloqResidual) {
		this.bloqResidual = bloqResidual;
	}
	/**
	 * @return the manejaImpconsumo
	 */
	public boolean getManejaImpconsumo() {
		return manejaImpconsumo;
	}

	/**
	 * @param manejaImpconsumo the manejaImpconsumo to set
	 */
	public void setManejaImpconsumo(boolean manejaImpconsumo) {
		this.manejaImpconsumo = manejaImpconsumo;
	}
	
	public boolean isVisibleAIU() {
		return visibleAIU;
	}

	public void setVisibleAIU(boolean visibleAIU) {
		this.visibleAIU = visibleAIU;
	}

	public boolean isActivoAIU() {
		return activoAIU;
	}

	public void setActivoAIU(boolean activoAIU) {
		this.activoAIU = activoAIU;
	}
	
	public boolean isManejaAIU() {
		return manejaAIU;
	}

	public void setManejaAIU(boolean manejaAIU) {
		this.manejaAIU = manejaAIU;
	}
	
	public boolean isCkAIU() {
		return ckAIU;
	}

	public void setCkAIU(boolean ckAIU) {
		this.ckAIU = ckAIU;
	}
	
	public double getPorcentajeAIU() {
		return porcentajeAIU;
	}

	public void setPorcentajeAIU(double porcentajeAIU) {
		this.porcentajeAIU = porcentajeAIU;
	}
	
	public double getAdministracion() {
		return administracion;
	}

	public void setAdministracion(double administracion) {
		this.administracion = administracion;
	}
	
	public double getImprevistos() {
		return imprevistos;
	}

	public void setImprevistos(double imprevistos) {
		this.imprevistos = imprevistos;
	}

	public double getUtilidades() {
		return utilidades;
	}

	public void setUtilidades(double utilidades) {
		this.utilidades = utilidades;
	}
	
	public String getTopTotalIva()
    {
        return topTotalIva;
    }

    public void setTopTotalIva(String topTotalIva)
    {
        this.topTotalIva = topTotalIva;
    }

    public String getTopTotalAjuste()
    {
        return topTotalAjuste;
    }

    public void setTopTotalAjuste(String topTotalAjuste)
    {
        this.topTotalAjuste = topTotalAjuste;
    }
    
    /**
	 * @return the topTotalImpConsumo
	 */
	public String getTopTotalImpConsumo() {
		return topTotalImpConsumo;
	}

	/**
	 * @param topTotalImpConsumo the topTotalImpConsumo to set
	 */
	public void setTopTotalImpConsumo(String topTotalImpConsumo) {
		this.topTotalImpConsumo = topTotalImpConsumo;
	}

	/**
	 * @return the topIva
	 */
	public String getTopIva() {
		return topIva;
	}

	/**
	 * @param topIva the topIva to set
	 */
	public void setTopIva(String topIva) {
		this.topIva = topIva;
	}

	/**
	 * @return the listaPlantillaDocx
	 */
	public RegistroDataModelImpl getListaPlantillaDocx() {
		return listaPlantillaDocx;
	}

	/**
	 * @param listaPlantillaDocx the listaPlantillaDocx to set
	 */
	public void setListaPlantillaDocx(RegistroDataModelImpl listaPlantillaDocx) {
		this.listaPlantillaDocx = listaPlantillaDocx;
	}

	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}

	/**
	 * @param plantilla the plantilla to set
	 */
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * @return the visibleListaPlantillas
	 */
	public boolean isVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}

	/**
	 * @return the visiblePresentarPlantillas
	 */
	public boolean isVisiblePresentarPlantillas() {
		return visiblePresentarPlantillas;
	}

	/**
	 * @param visiblePresentarPlantillas the visiblePresentarPlantillas to set
	 */
	public void setVisiblePresentarPlantillas(boolean visiblePresentarPlantillas) {
		this.visiblePresentarPlantillas = visiblePresentarPlantillas;
	}

	/**
	 * @return the bloqueaNiif
	 */
	public boolean isBloqueaNiif() {
		return bloqueaNiif;
	}

	/**
	 * @param bloqueaNiif the bloqueaNiif to set
	 */
	public void setBloqueaNiif(boolean bloqueaNiif) {
		this.bloqueaNiif = bloqueaNiif;
	}
	
	public boolean isContratoManual() {
			return contratoManual;
	}
	
	public void setContratoManual(boolean contratoManual) {
			this.contratoManual = contratoManual;
	}
	
}
