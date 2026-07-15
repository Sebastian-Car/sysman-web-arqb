/*-
 * SolicitudDisponibilidadControlador.java
 *
 * 1.0
 *
 * 13/03/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.CentroscostosControlador;
import com.sysman.general.enums.SolicitudDisponibilidadControladorEnum;
import com.sysman.general.enums.SolicitudDisponibilidadControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesWorkflowEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario SolicitudDisponibilidad en
 * access "frm_SolicitudCDP" el cual es llamado desde:
 * 'PRESUPUESTO\MOVIMIENTOS\SOLICITUD DE DISPONIBILIDAD PRESUPUESTAL'
 *
 * @version 1.0, 13/03/2018
 * @author jmalaver
 * 
 * @version 2.0, 04/04/2018
 * @author mvenegas
 * 
 * @version 3, 2018/08/25, <strong>pespitia</strong>:
 *          <li>Se adiciona el boton Enviar A Workflow.
 * 
 * @version 3.1, 02/01/2019
 * @author jrojas
 *         <li>Se agrega parámetro que valida reporte al imprimir
 *         
 *        
 *  @version 4.1, 30/03/2023
 * @author mrosero
 *         <li>Se anexan combos clasificadores en el subformulario
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class SolicitudDisponibilidadControlador extends BeanBaseDatosAcmeImpl {

	/**
	 * Constante a nivel de clase que aloja el codigo del modulo desde el cual se
	 * accede al formulario.
	 */
	private String modulo = SessionUtil.getModulo();

	/**
	 * 
	 * 
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */

	// <DECLARAR_ATRIBUTOS>

	/**
	 * Indicador que establece si el parametro: <code>MANEJA WORKFLOW</code>, tiene
	 * el valor definido en SI.
	 */
	private boolean indManejaWF;

	private boolean especial;
	private final String id;
	private final String compania;
	private double valor;
	private int indicePresupuesto;

	/**
	 * Esta variable me almacena el texto de No Aprobado
	 */
	private String msjNoAprobado;
	private String msjTBTB4073;
	private String msjTBTB4090;
	private String msjTBTB4032;
	private String msjTBTB4434;
	private String msjTBTB4435;
	private String msjTBTB4436;
	private String msjTBTB4437;

	/**
	 * Esta variable permite bloquear toda la funcionalidad del formulario cuando
	 * una solicitud ya se encuenta en modo "IMPRESO"
	 */
	private boolean bloqueoGeneral;

	/**
	 * Esta variable controla si se bloquea o no el campo de aprobacion
	 */
	private boolean bloqueoAprobacion;
	/**
	 * Esta variable se encarga de bloquear el campo de solicitud afectada
	 */
	private boolean bloqueoAfectador;

	/**
	 * Atributo que bloquea el campo de Rubro para cuando el parametro MANEJA TIPO
	 * GASTO EN SOLICITUD DE DISPONIBILIDAD esta activado
	 */
	private boolean bloqueoRubro;

	/**
	 * Esta variable permite bloquear o desbloquear el combo de tercero y
	 * dependencia, esto aplica solo si la solicitud es de tipo adicion o reduccion
	 * dado que estas heredan los datos previamente mencionado de la solicitud
	 * afectada
	 */
	private boolean bloqueoSolicitante;


	private boolean visibleAfectado;

	/**
	 * Atributo que indica si se esta actualizando el tipo de gasto en la grilla de
	 * imputacio presupuestal
	 */
	private boolean actualizaTipoGasto;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * NUMERO
	 */
	private final String cNumero;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * AUXILIAR
	 */
	private final String cAuxiliar;
	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * VIGENCIA_INICIAL
	 */
	private final String cVigenciaInicial;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * NRO_SOLICITUD_AFECT
	 */
	private final String cNroSolicitudAfectada;

	/**
	 * Esta variable almacena el nombre del campo TIPO_SOLICITUD
	 */
	private String cTipoSolicitud;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * CENTRO_COSTO
	 */
	private final String cCentroCosto;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * cCodigo
	 */
	private final String cCodigo;
	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * cIdPlan
	 */
	private final String cIdPlan;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * cSolicitud
	 */
	private final String cSolicitud;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * COMPANIA
	 */
	private final String cCompania;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * COMPANIA
	 */
	private final String cAdicion;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * TERCERO
	 */
	private final String cTercero;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * SUCURSAL
	 */
	private final String cSucursal;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * RUBRO
	 */
	private final String cRubro;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * REFERENCIA
	 */
	private final String cReferencia;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * APROCACION
	 */
	private final String cAprobacion;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * cTipCompAfect
	 */
	private final String cTipCompAfect;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * NOMBRE_TERCERO
	 */
	private final String cNombreTercero;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * NOMBRE_DEPENDENCIA
	 */
	private final String cNombreDependencia;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * FUENTE
	 */
	private final String cFuente;

	/**
	 * Constante que almacena la cadena "numeroSolicitud"
	 */
	private final String numeroSolicitud;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * VALOR
	 */
	private final String cValor;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * IMPUESTO
	 */
	private final String cImpuesto;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * VALORCONIMPUESTO
	 */
	private final String cValorBase;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * DEPENDENCIA
	 */
	private final String cDependencia;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * CONSECUTIVO
	 */
	private final String cConsecutivo;

	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * DESCRIPCION_MODALIDAD
	 */
	private final String cDescripcionModalidad;
	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * "ID_PROYECTO"
	 */
	private final String cIdProyecto;
	/**
	 * Constante definida por el numero de veces que se hace el llamado al texto
	 * "NOMBRE_TIPO_SOL"
	 */
	private final String cNombreSol;
	/**
	 * constante para almacenar la palabra remplazos
	 */
	private final String cRemplazos;
	/**
	 * Constante definida para almacenar la etiqueta
	 */
	private String cSolicitudRelacionada;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Listado de registros para el combo Vigencia
	 */
	private List<Registro> listaVigencia;
	/**
	 * Listado de registros para el combo de Rubro
	 */
	private RegistroDataModelImpl listaRubro;
	/**
	 * Listado de registros para el combo de RubroE
	 */
	private RegistroDataModelImpl listaRubroE;

	/**
	 * variable que me almacena los valosres en la listaFuente
	 */
	private RegistroDataModelImpl listaFuente;
	/**
	 * variable que me almacena los valosres en la listaFuenteE
	 */
	private RegistroDataModelImpl listaFuenteE;
	/**
	 * variable que me almacena los valosres en la listacentroCosto
	 */
	private RegistroDataModelImpl listacentroCosto;
	/**
	 * variable que me almacena los valosres en la listacentroCostoE
	 */
	private RegistroDataModelImpl listacentroCostoE;
	/**
	 * variable que me almacena los valosres en la listareferencia
	 */
	private RegistroDataModelImpl listareferencia;
	/**
	 * variable que me almacena los valosres en la listareferenciaE
	 */
	private RegistroDataModelImpl listareferenciaE;
	/**
	 * variable que me almacena los valosres en la listaTercero
	 */
	private RegistroDataModelImpl listaTercero;

	/**
	 * variable que me almacena los valosres en la listacmbTipoGasto
	 */
	private RegistroDataModelImpl listacmbTipoGasto;

	/**
	 * variable que me almacena los valosres en la listacmbTipoGastoE
	 */
	private RegistroDataModelImpl listacmbTipoGastoE;
	/**
	 * Tvariable que me almacena los valosres en la listacmbTipoSolicitud
	 */
	private RegistroDataModelImpl listacmbTipoSolicitud;
	/**
	 * Esta variable que me almacena los valosres en la listacmbProyecto
	 */
	private RegistroDataModelImpl listacmbProyecto;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private RegistroDataModelImpl listacmbProyectoE;
	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliarRubro;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String tercero;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String fuente;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String centroCosto;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String referencia;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String sucursal;
	
	
	private boolean bloqueoResponsable;
	private final Map<String, Object> camposProtegidosAfectados = new HashMap<>();
	/**
	 * Listado de registros para el combo de Dependencia
	 */
	private RegistroDataModelImpl listaDependencia;

	/**
	 * Listado de registros para el combo listaCmbSolicitudAfectada
	 */
	private RegistroDataModelImpl listaCmbSolicitudAfectada;
	/**
	 * Listado de registros para el combo de ModalidadSeleccion
	 */
	private RegistroDataModelImpl listaModalidadSeleccion;

	private RegistroDataModelImpl listaResponsableAprobar;
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>

	/**
	 * Listado de registros para el combo de Presupuesto
	 */
	private List<Registro> listaPresupuesto;

	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaAuxiliarE;
	
	private RegistroDataModelImpl listaListaPlantillas;

	private boolean visiblePresentarPlantillas;
	
	private Boolean visibleListaPlantillas = false;
	
	private boolean bloqFuente;
	private boolean bloqCentro;
	private boolean bloqReferencia;
	private boolean bloqTercero;
	private boolean bloqAuxiliar;
	private boolean bloqMovimiento;
	/**
	 * Esta variable permite bloquear o desbloquear el campo de proyectos esto solo
	 * aplica si el destino es INVERSION
	 */
	private boolean bloqProyecto;
	/**
	 * variable que bloquea o desbloquea el tipo de gasto
	 */
	private boolean bloquearTipoGasto;
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>

	/**
	 * Atributo de referencia para el subformulario
	 */
	private Registro registroSub;

	/**
	 * Esta variable toma el valor del paramatro FORMATO SOLICITUD DISPONIBILIDAD
	 */
	private String paraFormatoSDis;

	/**
	 * Esta variable me captura el valor del parametro MANEJA BANCO DE PROYECTOS
	 */

	private String paraManBancoP;

	/**
	 * Esta variable me controla el calor del parametro MANEJA AUXILIAR POR FUENTE
	 * EN PRESUPUESTO
	 */

	private String paraManFuentRec;
	/**
	 * Esta variable me controla el calor del parametro MANEJA TIPO GASTO EN
	 * SOLICITUD DE DISPONIBILIDAD
	 */
	private String parManGastSolDis;
	/**
	 * Esta variable permite controlar el valor del parametro PERMITE VISUALIZAR LOS
	 * RUBROS COMPLETOS
	 */
	private String paraRubrosCompl;
	/**
	 * Atributo de referencia auxiliar para el subformulario
	 */
	private Registro registroAuxiliarSub;

	// </DECLARAR_ADICIONALES>

	/**
	 * Esta variable permite bloquear el campos de numero
	 */

	private boolean bloquearNumero;
	/**
	 * Estas 3 cosntantes me permiten compara el valor de los parametros con la
	 * palabra SI, NO o 1
	 */
	static final String OPCIONNO = "NO";
	static final String OPCIONSI = "SI";
	static final String OPCIONUNO = "1";

	/**
	 * Esta variable me permite ocultar el combo de modalidad de seleccion dado que
	 * la ANE informo que para ellos no aplica
	 */
	private boolean mostrarModalidad;

	/**
	 * Esta variable me determida segun la opcion que se seleccione en el combo de
	 * tipo de solicitud si se cargan o no los campos numero de solicitud afectada y
	 * el comprobante afectado, ademas determina si se muestra o no el campos
	 * DISPONIBILIDAD NUMERO:
	 */
	private boolean cargarAfectados;

	/**
	 * Esta variable almacena el nombre que seleccione el usuario en el conto de
	 * tipo de solicitud
	 */
	private String nombreTipoSolicitud;

	/**
	 * Esta variable almacena el ID que seleccione el usuario en el conto de tipo de
	 * solicitud
	 */
	private String codigoTipoSolicitud;

	/**
	 * Esta variable permite identificar que protyecto se selecciono si el destino
	 * es INVERSION
	 */
	private String proyectoSeleccionado;

	/**
	 * Esta variable determina si se debe o no agregar datos en el sub formulario
	 */
	private boolean insertarSubFormulario;
	/**
	 * Esta variable determina si se debe o no editar y eliminar datos en el sub
	 * formulario
	 */
	private boolean permitirSubFormulario;

	/**
	 * Variable para almacenar la de indicador PAA
	 */
	private String cIndicadorPaa;

	/**
	 * esta variable carga o no el campo de motivo de rechazo
	 */
	private boolean cargarMotivoRechazo;

	private boolean manejaTercero;
	
	private boolean bpin;
	
	private boolean cofinanciacion;
	
	private boolean funcionamiento;
	
	private boolean propios;
	
	private String cNombreReferencia;
	
	private String cNombreBeneficiario;
	
	private String terceroEtiqueta;
	
	
	private String rubro;
	
	private boolean mostrarTipoCompro;
//7725064_PRESUPUESTO 

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSector;
	private RegistroDataModelImpl listaSectorE;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPrograma;
	private RegistroDataModelImpl listaProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSupPrograma;
	private RegistroDataModelImpl listaSupProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoProducto;
	private RegistroDataModelImpl listaCodigoProductoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoBPIN;
	private RegistroDataModelImpl listaCodigoBPINE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPET;
	private RegistroDataModelImpl listaCodigoCCPETE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCPCDANE;
	private RegistroDataModelImpl listaCodigoCPCDANEE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoUnidEje;
	private RegistroDataModelImpl listaCodigoUnidEjeE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFuente;
	private RegistroDataModelImpl listaCodigoFuenteE;
	/**
	 * TODO DOCUMENTACION NECESAR
	 */
	private RegistroDataModelImpl listaCodigoCCPETRega;
	private RegistroDataModelImpl listaCodigoCCPETRegaE;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaPoliticaPublica;
	private RegistroDataModelImpl listaPoliticaPublicaE;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaDetalleSectorial;
	private RegistroDataModelImpl listaDetalleSectorialE;
	
	private RegistroDataModelImpl listaBeneficiario;
	
	private RegistroDataModelImpl listareferenciaGeneral;
	
	private RegistroDataModelImpl listatipoCompromiso;

	
	private String verCCPET;
	
	private  String naturalezaCuenta =  "";
	private String sector;
	private String programa;
	private String supPrograma;
	private String codigoProducto;
	private String codigoBPIN;
	private String codigoCCPET;
	private String codigoCPCDANE;
	private String codigoFuente;
	private String codigoUnidEje;
	private String politicaPublica;
	private String detalleSectorial;
	private String codigoCCPETRega;
	
	private boolean bloqPoliticaPublica;
	private boolean bloqDetalleSectorial;
	private boolean bloqSector;
	private boolean bloqPrograma;
	private boolean bloqSupPrograma;
	private boolean bloqCodigoProducto;
	private boolean bloqCodigoBPIN;
	private boolean bloqCodigoCCPET;
	private boolean bloqCodigoCPCDANE;
	private boolean bloqCodigoUnidEje;
	private boolean bloqCodigoFuente;
	private boolean bloqCodigoCCPETRega;
//	FIN 7725064_PRESUPUESTO
	
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbPresupuestoTresGeneralRemote ejbPresupuestoTres;
	@EJB
	private EjbGeneralesRemote ejbGeneralesRemote;
	@EJB
	private EjbSysmanUtil ejbSysmanUtils;

	/**
	 * Variable que almacena la palabra MANEJA TIPO GASTO EN SOLICITUD DE
	 * DISPONIBILIDAD
	 */
	private String cManejaTipoGasto;

	/**
	 * Cosntante que almacena la palabra No Aplica
	 */
	private String cNoAplica;
	/**
	 * constante que almacena el texto JavaScript
	 */
	private String cJavaScript;

	/**
	 * Cedula del usuario que inicia en la aplicacion
	 */
	private String cedulaUsusuario;

	private String modificaConsecutivo;

	private String cNombreAprobar;

	private String cResponsableAprobar;

	private String cSucursalAprobar;

	private boolean visibleAprobar;

	private boolean verImpuesto;
	
	private boolean calculaGravamen;

	private double valorGMF = 0.0;
	
	private String ano;
	
	private double valorBase = 0.0;
	
	private double impuesto = 0.0;

	private String cBeneficiario;
	
	private String cRecurso;

	private String reporteSinchi;
	
	private String cadenaRecursos;
	
	private Boolean obligaCCPET;

	private String campos;

	private boolean obligaCPCDANE;

	private String lbResponsable;
	private Map<String,Object> parametroswf;

	private String  plantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;

	private String anio;
	private String orden;
	private List<Registro> listaanio;
	/**
	 * Crea una nueva instancia de SolicitudDisponibilidadControlador
	 */

	public SolicitudDisponibilidadControlador() {
		super();
		compania = SessionUtil.getCompania();
		cNumero = GeneralParameterEnum.NUMERO.getName();
		cAuxiliar = GeneralParameterEnum.AUXILIAR.getName();
		cCodigo = GeneralParameterEnum.CODIGO.getName();
		cCentroCosto = GeneralParameterEnum.CENTRO_COSTO.getName();
		cCompania = GeneralParameterEnum.COMPANIA.getName();
		cValor = GeneralParameterEnum.VALOR.getName();
		cImpuesto = "IMPUESTO";
		cValorBase = "VALOR_BASE";
		cTercero = GeneralParameterEnum.TERCERO.getName();
		cSucursal = GeneralParameterEnum.SUCURSAL.getName();
		cRubro = GeneralParameterEnum.RUBRO.getName();
		cReferencia = GeneralParameterEnum.REFERENCIA.getName();
		cDependencia = GeneralParameterEnum.DEPENDENCIA.getName();
		cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
		// -tipoTCons = FrmSolicitudCdpControladorEnum.TIPOT.getValue();
		// claseTCons = FrmSolicitudCdpControladorEnum.CLASET.getValue();
		cedulaUsusuario = SessionUtil.getUser().getCedula();
		ano = String.valueOf(SysmanFunciones.ano(new Date()));
		anio = ano;
		orden = "E";
		cRecurso = "RECURSO";
		cBeneficiario = "BENEFICIARIO";
		cNombreAprobar = "NOMBRE_APROBAR";
		cResponsableAprobar = "RESPONSABLE_APROBAR";
		cSucursalAprobar = "SUCURSAL_APROBAR";
		obligaCCPET = false;
		cNombreTercero = "NOMBRE_TERCERO";
		cNombreDependencia = "NOMBRE_DEPENDENCIA";
		cFuente = "FUENTE";
		cDescripcionModalidad = "DESCRIPCION_MODALIDAD";
		nombreTipoSolicitud = "";
		cSolicitud = "SOLICITUD";
		cAdicion = "ADICIÓN A SOLICITUD";
		cTipCompAfect = "TIPO_CPTE_AFECT";
		cIdPlan = "ID_PLAN";
		cIdProyecto = "ID_PLAN_INDICATIVO";
		cNombreSol = "NOMBRE_TIPO_SOL";
		cTipoSolicitud = "TIPO_SOLICITUD";
		cAprobacion = "APROBACION";
		cNroSolicitudAfectada = "NRO_SOLICITUD_AFECT";
		id = "ID";
		cVigenciaInicial = "VIGENCIA_INICIAL";
		cSolicitudRelacionada = "DISPONIBILIDAD_RELACIONADA";
		bloqProyecto = true;
		visibleAfectado = true;
		mostrarModalidad = false;
		mostrarTipoCompro = false;
		cargarAfectados = false;
		insertarSubFormulario = true;
		permitirSubFormulario = true;
		bloqueoSolicitante = false;
		bloqueoResponsable =  false;
		bloqueoAprobacion = false;
		bloqueoAfectador = false;
		bloqueoRubro = false;
		cargarMotivoRechazo = false;
		actualizaTipoGasto = false;
		bpin = false;
		cofinanciacion = false;
		funcionamiento = false;
		propios = false;
		msjNoAprobado = "N";
		msjTBTB4073 = "TB_TB4073";
		msjTBTB4090 = "TB_TB4090";
		msjTBTB4032 = "TB_TB4032";
		msjTBTB4434 = "TB_TB4434";
		msjTBTB4435 = "TB_TB4435";
		msjTBTB4436 = "TB_TB4436";
		msjTBTB4437 = "TB_TB4437";
		valor = 0;
		cIndicadorPaa = "INDICADOR_PAA";
		cNombreReferencia = "NOMBRE_REFERENCIA";
		cNombreBeneficiario = "NOMBRE_BENEFICIARIO";
		cRemplazos = "remplazos";
		numeroSolicitud = "numeroSolicitud";
		bloquearTipoGasto = true;
		cManejaTipoGasto = "MANEJA TIPO GASTO EN SOLICITUD DE DISPONIBILIDAD";
		cNoAplica = "No Aplica";
		cJavaScript = "PF('dlg194').show(); PF('alert').renderMessage({'summary':'Error','detail':'El campo Tipo Gasto es obligatorio.','severity':'error'});";
		naturalezaCuenta=GeneralParameterEnum.NATURALEZA.getName();
		try {
			numFormulario = GeneralCodigoFormaEnum.SOLICITUD_DISPONIBILIDAD_CONTROLADOR.getCodigo();
			parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "3");
				modulo = SessionUtil.getModulo();
			}
			validarPermisos();
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			registro = new Registro();
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }

	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>

		cargarListaDependencia();
		cargarListaModalidadSeleccion();
		cargarListacmbTipoSolicitud();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaVigencia();
		cargarListaanio();

		if (parManGastSolDis.equals("SI")) {
			cargarListacmbTipoGasto();
		}
		
		if (manejaTercero) {
			cargarListaBeneficiario();
			}
		
		cargarListaListaPlantillas();
		cargarListatipoCompromiso();

		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		cargarListaPresupuesto();
		if (parManGastSolDis.equals("SI")) {
			cargarListacmbTipoGastoE();
		}
		cargarListaAuxiliar();
		cargarListaCentroCosto();
		cargarListaFuente();
		cargarListaReferencia();
		// cargarListaAuxiliarE();
		// cargarListaCentroCostoE();
		cargarListaFuenteE();
		cargarListaReferenciaE();
		cargarListaTercero();
        cargarListaResponsableAprobar();
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>

	//	7725064
		cargarListaSector();
		cargarListaSectorE();
		cargarListaPrograma();
		cargarListaProgramaE();
		cargarListaSupPrograma();
		cargarListaSupProgramaE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
		cargarListaCodigoBPIN();
		cargarListaCodigoBPINE();
		cargarListaCodigoCCPET();
		cargarListaCodigoCCPETE();
		cargarListaCodigoUnidEje();
		cargarListaCodigoUnidEjeE();
		cargarListaCodigoFuente();
		cargarListaCodigoFuenteE();
		cargarListaCodigoCCPETRega();
		cargarListaCodigoCCPETRegaE();
		cargarListaPoliticaPublica();
		cargarListaPoliticaPublicaE();
		cargarListaDetalleSectorial();
		cargarListaDetalleSectorialE();
		if (manejaTercero) {
			cargarListaBeneficiario();
			}
		cargarListareferenciaGeneral();
		//	7725064		
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaPresupuesto = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}
	
	

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {

		enumBase = GenericUrlEnum.SOLICITUDDISPONIBILIDAD;
		buscarLlave();
		asignarOrigenDatos();

		paraFormatoSDis = obtenerParametro("FORMATO SOLICITUD DISPONIBILIDAD", "");

		paraManBancoP = obtenerParametro("MANEJA BANCO DE PROYECTOS", "");

		paraManFuentRec = obtenerParametro("MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO", "");

		parManGastSolDis = obtenerParametro("MANEJA TIPO GASTO EN SOLICITUD DE DISPONIBILIDAD", "");

		visibleAprobar = obtenerParametro("MANEJA APROBACION EN SOLICITUD", "NO").equals("SI") ? true : false;

		paraRubrosCompl = obtenerParametro("PERMITE VISUALIZAR LOS RUBROS COMPLETOS", "");

		calculaGravamen = obtenerParametro("PERMITE CALCULO DEL IMPUESTO A LOS MOVIMIENTOS FINANCIEROS", "NO").equals("SI") ? true : false;

		valorGMF = (Double.parseDouble(obtenerParametro("GRAVAMEN A LOS MOVIMIENTOS FINANCIEROS", "0.0")));
		
		mostrarTipoCompro = obtenerParametro("MANEJA TIPO COMPROMISO EN SOLICITUD DE DISPONIBILIDAD", "NO").equals("SI") ? true : false;
		
		try {
			manejaTercero = "SI".equals(SysmanFunciones
			        .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA TERCERO EN SOLICITUD DE DISPONIBILIDAD FUNCIONAMIENTO",
			                "-1", new Date(), true), "NO"));
			
			terceroEtiqueta = SysmanFunciones.toString(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "NOMBRE ETIQUETA RESPONSABLE EN SOLICITUD",modulo, new Date(), false), "Responsable:"));
			
			lbResponsable = SysmanFunciones.toString(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "NOMBRE ETIQUETA RESPONSABLE DE APROBAR EN SOLICITUD", modulo, new Date(), false), "Responsable de Aprobar:"));
						
			cargarListaVigencia();
			cargarListaanio();
			
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 *
	 *
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
		parametrosListado.put(GeneralParameterEnum.ORDEN.getName(), orden);

		boolean esOrdanador = false;
		try {
			esOrdanador = ejbPresupuestoTres.esOrdenador(compania,
					SysmanFunciones.toString(SessionUtil.getUser().getCedula()));
		} catch (SystemException e) {
			Logger.getLogger(SolicitudDisponibilidadControlador.class.getName()).log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());

		}
		/**
		 * Si el grupo es administrador debe mostrar todas las solicitudes, perso si no
		 * es administrador, solo debe mostrar las solicitudes que ese usuario creo.
		 */
		if (!SessionUtil.getGrupo(modulo).isEsAdministrador()) {

			parametrosListado.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().toString());

			urlListado = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL023.getValue());

		}
		/**
		 * Si el usuario que ingreso es ordenador del gasto, debe mostrar solo las
		 * solicitudes que esta en REVISION en la grilla
		 */

		if (esOrdanador) {

			urlListado = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL024.getValue());

		}
	}

	public void cargarListaListaPlantillas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.MODULO.getName(), modulo);
		param.put(GeneralParameterEnum.TIPO.getName(), "SCD");
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL104080.getValue());
		
		
		listaListaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
		visibleListaPlantillas = listaListaPlantillas==null?false:true;
	}
	
	/**
	 * 
	 * Carga la lista listatipoCompromiso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListatipoCompromiso(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL2001001.getValue());
		
		listatipoCompromiso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
	 * 
	 * Carga la lista listacmbTipoGasto
	 *
	 */
	public void cargarListacmbTipoGasto() {

		String servicio;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cRubro, registroSub.getCampos().get(cRubro));

		if (bloqProyecto) {

			servicio = SolicitudDisponibilidadControladorUrlEnum.URL010.getValue();
		} else {

			servicio = SolicitudDisponibilidadControladorUrlEnum.URL014.getValue();
			param.put("IDPROYECTO", proyectoSeleccionado);

		}

		if (parManGastSolDis.equals("SI")) {
			servicio = SolicitudDisponibilidadControladorUrlEnum.URL026.getValue();

			param.put(GeneralParameterEnum.RESPONSABLE.getName(), cedulaUsusuario);

		}

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(servicio);

		try {
			listacmbTipoGasto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, CacheUtil.getLlaveServicio(urlConexionCache, "BP_PLAN_ADQUISICIONES"));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacmbTipoGastoE
	 *
	 */
	public void cargarListacmbTipoGastoE() {
		String servicio;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cRubro, registroSub.getCampos().get(cRubro));

		if (bloqProyecto) {

			servicio = SolicitudDisponibilidadControladorUrlEnum.URL010.getValue();
		} else {

			servicio = SolicitudDisponibilidadControladorUrlEnum.URL014.getValue();
			param.put("IDPROYECTO", proyectoSeleccionado);

		}
		if (parManGastSolDis.equals("SI")) {
			servicio = SolicitudDisponibilidadControladorUrlEnum.URL026.getValue();

			param.put(GeneralParameterEnum.RESPONSABLE.getName(), cedulaUsusuario);

		}

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(servicio);

		try {
			listacmbTipoGastoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, CacheUtil.getLlaveServicio(urlConexionCache, "BP_PLAN_ADQUISICIONES"));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listacmbTipoSolicitud
	 *
	 */
	public void cargarListacmbTipoSolicitud() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL011.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("CATEGORIA", OPCIONUNO);

		listacmbTipoSolicitud = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cCodigo);
	}

	/**
	 *
	 * Carga la lista listaPresupuesto
	 *
	 */
	public void cargarListaPresupuesto() {
		if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {
			bloquearTipoGasto = false;
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(cNumero));
		param.put("TIPOSOLICITUD", registro.getCampos().get(cTipoSolicitud));

		try {
			listaPresupuesto = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													GenericUrlEnum.D_SOLICITUDDISPONIBILIDAD.getGridKey())
											.getUrl(),
									param),
							CacheUtil.getLlaveServicio(urlConexionCache, "D_SOLICITUDDISPONIBILIDAD"));
		} catch (SystemException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 *
	 * Carga la lista listaVigencia
	 *
	 */
	public void cargarListaVigencia() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaVigencia = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SolicitudDisponibilidadControladorUrlEnum.URL3794.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	
	public void cargarListaanio() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SolicitudDisponibilidadControladorUrlEnum.URL3794.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		};
	}

	/**
	 * 
	 * Carga la lista listaCmbSolicitudAfectada
	 *
	 */
	public void cargarListaCmbSolicitudAfectada() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOSOLICITUD", 1);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL012.getValue());

		listaCmbSolicitudAfectada = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, cNumero);
	}

	/**
	 *
	 * Carga la lista listaRubro
	 *
	 */
	public void cargarListaRubro() {

		/**
		 * En esta seccion se valida que si el cliente es GOBERNACION DE CAQUETA y el
		 * parametro MANEJA BANCO DE PROYECTOS esta en SI no se deben mostrar los rubros
		 * de tipo inversion.
		 */
		String servicioRubro;

		if ("SI".equals(obtenerParametro("PERMITE VISUALIZAR LOS RUBROS COMPLETOS", "NO"))) {
			servicioRubro = SolicitudDisponibilidadControladorUrlEnum.URL030.getValue();
		}

		else {
			servicioRubro = SolicitudDisponibilidadControladorUrlEnum.URL004.getValue();
		}

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(servicioRubro);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(cNumero));

		listaRubro = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, id);
	}

	/**
	 *
	 * Carga la lista listaRubro
	 *
	 */
	public void cargarListaRubroE() {

		/**
		 * En esta seccion se valida que si el cliente es GOBERNACION DE CAQUETA y el
		 * parametro MANEJA BANCO DE PROYECTOS esta en SI no se deben mostrar los rubros
		 * de tipo inversion.
		 */
		String servicioRubro;

		if ("SI".equals(obtenerParametro("PERMITE VISUALIZAR LOS RUBROS COMPLETOS", "NO"))) {
			servicioRubro = SolicitudDisponibilidadControladorUrlEnum.URL030.getValue();
		} else {
			servicioRubro = SolicitudDisponibilidadControladorUrlEnum.URL004.getValue();
		}

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(servicioRubro);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));
		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(cNumero));

		listaRubroE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, id);

		bloqAuxiliar = bloqCentro = bloqFuente = bloqReferencia = bloqTercero = true;

	}

	/**
	 *
	 * Carga la lista listaTercero
	 *
	 */
	public void cargarListaTercero() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL5114.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cDependencia, registro.getCampos().get(cDependencia));

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "NIT");
	}

	/**
	 * 
	 * Carga la lista listaResponsableAprobar
	 *
	 */
	public void cargarListaResponsableAprobar() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL5114.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cDependencia, registro.getCampos().get(cDependencia));

		listaResponsableAprobar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}

	public void cargarListaFuente() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL007.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaFuenteE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL007.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

		listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaReferencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL008.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

		listareferencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	
	public void cargarListareferenciaGeneral() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL13001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		
		listareferenciaGeneral = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaReferenciaE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL008.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

		listareferenciaE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroCosto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL009.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		param.put(SolicitudDisponibilidadControladorEnum.CODEXCLUIDO.getValue(), 0);

		listacentroCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 *
	 * Carga la lista listaDependencia
	 *
	 */
	public void cargarListaDependencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL5537.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaAuxiliar() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL029.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANIO", registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 *
	 * Carga la lista listamodalidad_seleccion
	 *
	 */
	public void cargarListaModalidadSeleccion() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL005.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaModalidadSeleccion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listacmbProyecto
	 *
	 */
	public void cargarListacmbProyecto() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		String cantidadDigitos = obtenerParametro("NUMERO DE DIGITOS PROYECTO", "0");
		param.put("CANTIDADDIGITOS", cantidadDigitos);

		listacmbProyecto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");

	}

	/**
	 * 
	 * Carga la lista listacmbProyecto
	 *
	 */
	public void cargarListacmbProyectoE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(SolicitudDisponibilidadControladorUrlEnum.URL013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		String cantidadDigitos = obtenerParametro("NUMERO DE DIGITOS PROYECTO", "0");
		param.put("CANTIDADDIGITOS", cantidadDigitos);

		listacmbProyectoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID");
	}
	
	//7725064_PRESUPUESTO
	
	
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo,int aplicacion, String naturaleza)
	{
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";
		naturaleza=GeneralParameterEnum.NATURALEZA.getName();
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

		if(naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals(""))
		{			
			
			param.put(GeneralParameterEnum.CLASE.getName(), codigo);

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SolicitudDisponibilidadControladorUrlEnum.URL13432
								.getValue());

			listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());


		}		 
		return listaTipo;
	}
	/**
	 * 
	 * Carga la lista listaSector
	 *
	 */
	public void cargarListaSector(){
		listaSector =   cargarListaTipoClasificador("001",2,naturalezaCuenta);
	}
	/**
	 * 
	 * Carga la lista listaSectorE
	 *
	 */
	public void cargarListaSectorE(){
		listaSectorE =   cargarListaTipoClasificador("001",2,naturalezaCuenta);
	}
	
	/**
	 * Carga la lista listaPrograma
	 */
	public void cargarListaPrograma(){
		listaPrograma  =  cargarListaTipoClasificador("002",2,naturalezaCuenta);
  }
	
	/**
	 * Carga la lista listaProgramaE
	 */
	public void cargarListaProgramaE(){
		listaProgramaE = listaPrograma;
   }
	
	/**
	 * Carga la lista listaSupPrograma
	 */
	public void cargarListaSupPrograma(){
		listaSupPrograma =   cargarListaTipoClasificador("003",2,naturalezaCuenta);
	}
	
	public void cargarListaSupProgramaE(){
		listaSupProgramaE =  listaSupPrograma;
	}
	/**
	 * Carga la lista listaCodigoProducto
	 */
	public void cargarListaCodigoProducto(){
		listaCodigoProducto  =  cargarListaTipoClasificador("004",2,naturalezaCuenta);
	
	}
	public void cargarListaCodigoProductoE(){
		listaCodigoProductoE =  listaCodigoProducto;
		
		
	}
	
	/**
	 * Carga la lista listaCodigoBPIN
	 */
	public void cargarListaCodigoBPIN(){
		listaCodigoBPIN =  cargarListaTipoClasificador("005",2,naturalezaCuenta);
	}
	
	/**
	 * 
	 */
	public void cargarListaCodigoBPINE(){
		listaCodigoBPINE  =  listaCodigoBPIN;
	}
	
	/**
	 * Carga la lista listaCodigoCCPET
	 */
	public void cargarListaCodigoCCPET(){
		listaCodigoCCPET =  cargarListaTipoClasificador("006",2,naturalezaCuenta);
	}

	public void cargarListaCodigoCCPETE(){
		listaCodigoCCPETE =  listaCodigoCCPET;
		
	}
	
	/**
	 * Carga la lista listaCodigoCPCDANE
	 */
	public void cargarListaCodigoCPCDANE(){
		listaCodigoCPCDANE = null;
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		param.put("IDPADRE", "006"+registroSub.getCampos().get("CODIGOCCPET"));

		urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SolicitudDisponibilidadControladorUrlEnum.URL13433
								.getValue());

		listaCodigoCPCDANE = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
	
	}
	
	/**
	 * 
	 */
	public void cargarListaCodigoCPCDANEE(){		
		listaCodigoCPCDANEE = null;		
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
		param.put("IDPADRE", "006"+codigoCCPET);

		urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SolicitudDisponibilidadControladorUrlEnum.URL13433
								.getValue());

		listaCodigoCPCDANEE = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
		
	}
	
	/**
	 * Carga la lista listaCodigoUnidEje
	 */
	public void cargarListaCodigoUnidEje(){
		listaCodigoUnidEje =  cargarListaTipoClasificador("008",2,naturalezaCuenta);
	
	}
	
	/**
	 * 
	 */
	public void cargarListaCodigoUnidEjeE(){
		listaCodigoUnidEjeE =  listaCodigoUnidEje;
	}

	/**
	 * 
	 * Carga la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoFuente() {
		listaCodigoFuente =  cargarListaTipoClasificador("009",2,naturalezaCuenta);
	}

	/**
	 * 
	 */
	public void cargarListaCodigoFuenteE() {
		listaCodigoFuenteE = listaCodigoFuente;
	}
	

	/**
	 * Carga la lista listaCodigoCCPETRega
	 */
	public void cargarListaCodigoCCPETRega(){
		listaCodigoCCPETRega =  cargarListaTipoClasificador("010",2,naturalezaCuenta );
	}
	
	/**
	 * 
	 */
	public void cargarListaCodigoCCPETRegaE(){
		listaCodigoCCPETRegaE =  listaCodigoCCPETRega;
	}
	
	/**
	 * Carga la lista listaPoliticaPublica
	 */
	public void cargarListaPoliticaPublica(){
		listaPoliticaPublica =  cargarListaTipoClasificador("011",2,naturalezaCuenta );
	}
	
	/**
	 * 
	 */
	public void cargarListaPoliticaPublicaE(){
		listaPoliticaPublicaE = listaPoliticaPublica;
	}
	
	/**
	 * 
	 */
	public void cargarListaDetalleSectorial(){

          listaDetalleSectorial = cargarListaTipoClasificador("012",2,naturalezaCuenta );		
		
	}
	/**
	 * 
	 */
	public void cargarListaDetalleSectorialE(){

            listaDetalleSectorialE = listaDetalleSectorial;
		
	}
	
	public void cargarListaBeneficiario() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SolicitudDisponibilidadControladorUrlEnum.URL14001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaBeneficiario = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, "NIT");
	}
	

	//7725064_PRESUPUESTO

	/**
	 * 
	 * @param formato  -> Formato en el que se genera el reporte: PDF o EXCEL.
	 * @param workflow -> Indica si se debe generar el reporte para el modulo de
	 *                 workflow.
	 */
	private void generarReporte(ReportesBean.FORMATOS formato, String nombreInforme) {
		try {

			if(manejaTercero && nombreInforme.equals("002565FORMATODIS_SINCHI")) {
				String numeroSolicitud = SysmanFunciones.nvl(registro.getCampos().get(cNumero), "").toString();
				HashMap<String, Object> reemplazos = new HashMap<>();
				reemplazos.put("compania", compania);
				reemplazos.put("ano", registro.getCampos().get(GeneralParameterEnum.ANO.getName())); //MOD JM 10/01/2025 CC626
				reemplazos.put("tipoSolicitud", registro.getCampos().get(cTipoSolicitud));
				reemplazos.put("numeroSolicitud", numeroSolicitud);

				Map<String, Object> parametros = new HashMap<>();
				parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
				parametros.put("PR_IMAGENES", SessionUtil.getCompaniaIngreso().getRutaSticker());
				parametros.put("PR_NOMBRECIUDAD", SessionUtil.getCompaniaIngreso().getCiudad());

				Reporteador.resuelveConsulta(nombreInforme, Integer.parseInt(modulo), reemplazos, parametros);

				archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN,
						formato);
			}else {
			if(nombreInforme.equals("002467SolicitudPresupuestal")) {

				HashMap<String, Object> reemplazar = new HashMap<>();
				reemplazar.put("compania", compania);
				reemplazar.put("anio", registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
				reemplazar.put("dependencia", SysmanFunciones.toString(registro.getCampos().get(cDependencia)));
				reemplazar.put("codigo", registro.getCampos().get("NUMERO"));

				String strSql = Reporteador.resuelveConsulta(nombreInforme, Integer.parseInt(modulo), reemplazar);

				HashMap<String,Object> parametros = new HashMap<>();			
				parametros.put("PR_STRSQL", strSql);

				archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			}else {
				if (nombreInforme.equals("002286SOLCERTIFDISPPTALIDIPRON")) {
					String numeroDeSolicitud = SysmanFunciones.nvl(registro.getCampos().get(cNumero), "").toString();
					HashMap<String, Object> reempla = new HashMap<>();
					reempla.put("compania", compania);
					reempla.put("tipot", "SCD");
					reempla.put("consecutivo", numeroDeSolicitud);

					Map<String, Object> paramet = new HashMap<>();
					paramet.put("PR_CREADOR", SessionUtil.getUser().getCodigo());
					paramet.put("PR_IMAGENES", SessionUtil.getCompaniaIngreso().getRutaSticker());

					Reporteador.resuelveConsulta(nombreInforme, Integer.parseInt(modulo), reempla, paramet);

					archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, paramet, ConectorPool.ESQUEMA_SYSMAN,
							formato);

				} else {

					if ("i".equals(accion)) {
						JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2122"));
						return;
					}
					String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
					Map<String, Object> reemplazos = new HashMap<>();

					// reemplazos para informe 001716SCDCAQUETA
					reemplazos.put("tipoT", "SCD");
					reemplazos.put("numDisponibilidad", registro.getCampos().get(cNumero));

					// reemplazos para informe 000304SDP
					reemplazos.put("numSolicitud",
							registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());

					Map<String, Object> parametros = new HashMap<>();
					parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
					parametros.put("PR_NOMBRECIUDAD", SessionUtil.getCompaniaIngreso().getCiudad());
					// CONSULTAR PARAMETRO: NOMBRE DE JEFE DE PRESUPUESTO
					parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
							obtenerParametro("NOMBRE DE JEFE DE PRESUPUESTO", ""));
					// CONSULTAR PARAMETRO: CARGO JEFE DE PRESUPUESTO TUNJA
					parametros.put("PR_CARGO_JEFE_DE_PRESUPUESTO_TUNJA",
							obtenerParametro("CARGO JEFE DE PRESUPUESTO TUNJA", ""));
					// CONSULTAR PARAMETRO: JEFE BANCO PROYECTOS
					parametros.put("PR_JEFE_BANCO_PROYECTOS", obtenerParametro("JEFE BANCO PROYECTOS", ""));
					// CONSULTAR PARAMETRO: CARGO JEFE DE BANCO PROYECTOS
					parametros.put("PR_CARGO_JEFE_DE_BANCO_PROYECTOS",
							obtenerParametro("CARGO JEFE DE BANCO PROYECTOS", ""));

					parametros.put("PR_SLOGAN", ejbSysmanUtil.consultarParametro(compania,
							"IMAGEN SLOGAN ENCABEZADO CAQUETA", SessionUtil.getModulo(), new Date(), false));
					parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
					parametros.put("PR_CREADOR", SessionUtil.getUser().getCodigo());
					// AGRAGO MVENEGAS
					parametros.put("PR_CIUDADCOMPANIA",
							SessionUtil.getCompaniaIngreso().getCiudad() + ", " + SysmanFunciones.convertirAFechaCadena(
									(Date) registro.getCampos().get("FECHA"), "dd ' de ' MMMM ' de ' yyyy"));

					parametros.put("PR_NOMBRE_JEFE_DIVISION_ADMINISTRATIVA",
							obtenerParametro("NOMBRE JEFE DIVISION ADMINISTRATIVA", ""));

					parametros.put("PR_CARGO_DIVISION_FINANCIERA_Y_DE_PRESUPUESTO",
							obtenerParametro("CARGO DIVISION FINANCIERA Y DE PRESUPUESTO", ""));

					parametros.put("PR_PIE_PAGINA_IMAGEN", SessionUtil.getCompaniaIngreso().getRutaSticker());
					// PARAMETROS USADOS PARA REPORTE DE IDCBIS
					// FIRMA 1
					parametros.put("PR_FIRMA_1_NOMBRE",
							obtenerParametro("FIRMA 1 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// FRIMA 2
					parametros.put("PR_FIRMA_2_NOMBRE",
							obtenerParametro("FIRMA 2 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// FIRMA 3
					parametros.put("PR_FIRMA_3_NOMBRE",
							obtenerParametro("FIRMA 3 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// CARGO 1
					parametros.put("PR_FIRMA_1_CARGO",
							obtenerParametro("CARGO 1 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// CARGO 2
					parametros.put("PR_FIRMA_2_CARGO",
							obtenerParametro("CARGO 2 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// CARGO 3
					parametros.put("PR_FIRMA_3_CARGO",
							obtenerParametro("CARGO 3 REPORTE DISPONIBILIDADES IDCBIS", ""));
					// FIN AGREGADO MVENEGAS
					/*
					 * if (paraManBancoP.equals(OPCIONSI) // modificacion por TAR 1000098575 &&
					 * paraManFuentRec.equals(OPCIONSI)) { nombreInforme = "001716SCDCAQUETA"; }
					 * else {
					 */
					String numeroDeSolicitud = SysmanFunciones.nvl(registro.getCampos().get(cNumero), "").toString();

					reemplazos.put("numeroSolicitud", numeroDeSolicitud);
					reemplazos.put("tipoSolicitud", registro.getCampos().get(cTipoSolicitud));
					
					int tipo = Integer.parseInt(registro.getCampos().get(cTipoSolicitud).toString());
					switch(tipo) {
					  case 1:
						  reemplazos.replace("tipoT", "SCD");
						  reemplazos.put("claseT", "B");
					    break;
					  case 2:
						  reemplazos.replace("tipoT", "SAD");
						  reemplazos.put("claseT", "P");
					    break;
					  case 3:
						  reemplazos.replace("tipoT", "SAD");
							reemplazos.put("claseT", "P");
						    break;
					  default:
						 reemplazos.put("claseT", "B");
					}


					Reporteador.resuelveConsulta(nombreInforme, Integer.parseInt(SessionUtil.getModulo()), reemplazos,
							parametros);

					archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN,
							formato);
				}
			}
		  }
		} catch (SysmanException | JRException | IOException | ParseException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	public void mensajesInicioModal() {

	}

	public void ejecutaractualizarImpreso() {
		registro.getCampos().put(SolicitudDisponibilidadControladorEnum.IMPRESO.getValue(), true);
		agregarRegistroNuevo(false);
		cambiarImpreso();
	}

	/**
	 * Cuando se modifica el check de impreso
	 */
	public void cambiarImpreso() {
		if ((boolean) registro.getCampos().get(SolicitudDisponibilidadControladorEnum.IMPRESO.getValue())) {
			bloqueoGeneral = true;
			insertarSubFormulario = false;
			permitirSubFormulario = false;
		} else {
			bloqueoGeneral = false;
			insertarSubFormulario = true;
			permitirSubFormulario = true;
		}
	}

	public void cambiaranio() {
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
		asignarOrigenDatos();
	}
	public void cambiarordenarpor() {
		parametrosListado.put(GeneralParameterEnum.ORDEN.getName(), orden);
		asignarOrigenDatos();
	}

	/**
	 * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
	 *
	 * @param nombreParametro Nombre del parametro a consultar en la base de datos.
	 * @param valorDefault    Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, nombreParametro, "3", new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control Rubro en la fila seleccionada dentro
	 * de la grilla
	 *
	 *
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarRubroC(int rowNum) {
		valor = Double.parseDouble(listaPresupuesto.get(rowNum).getCampos().get(cValor).toString());
		listaPresupuesto.get(rowNum).getCampos().put(cRubro, auxiliar);

		if (valor > 0) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4033"));
			cargarListaPresupuesto();
			listaPresupuesto.get(rowNum).getCampos().put(cRubro, registroAuxiliarSub.getCampos().get(cRubro));
		} else {
			listaPresupuesto.get(rowNum).getCampos().put(cTercero, tercero);
			listaPresupuesto.get(rowNum).getCampos().put(cAuxiliar, auxiliar);
			listaPresupuesto.get(rowNum).getCampos().put(cFuente, fuente);
			listaPresupuesto.get(rowNum).getCampos().put(cCentroCosto, centroCosto);
			listaPresupuesto.get(rowNum).getCampos().put(cReferencia, referencia);
			listaPresupuesto.get(rowNum).getCampos().put(cSucursal, sucursal);
			listaPresupuesto.get(rowNum).getCampos().put(cAuxiliar, auxiliarRubro);
		}
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Tipo Gasto en la fila seleccionada
	 * dentro de la grilla
	 *
	 *
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarcmbTipoGastoC(int rowNum) {

		actualizaTipoGasto = true;

		if ("SI".equals(parManGastSolDis)) {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(cIdPlan, auxiliar);

			try {
				Registro rsRubro = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL027.getValue())
										.getUrl(),
								param));

				listaPresupuesto.get(rowNum).getCampos().put(cRubro, rsRubro.getCampos().get(cRubro));

				param.put(cIdPlan, rsRubro.getCampos().get(cRubro));

				Registro rsRubroInfo = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL028.getValue())
										.getUrl(),
								param));

				if (rsRubroInfo != null) {

					listaPresupuesto.get(rowNum).getCampos().put(cFuente,
							SysmanFunciones.nvl(rsRubroInfo.getCampos().get(cFuente), SysmanConstantes.CONS_FUENTE));
					listaPresupuesto.get(rowNum).getCampos().put(cCentroCosto, SysmanFunciones
							.nvl(rsRubroInfo.getCampos().get(cCentroCosto), SysmanConstantes.CONS_CENTRO));
					listaPresupuesto.get(rowNum).getCampos().put(cReferencia, SysmanFunciones
							.nvl(rsRubroInfo.getCampos().get(cReferencia), SysmanConstantes.CONS_REFERENCIA));

					listaPresupuesto.get(rowNum).getCampos().put(cAuxiliar, SysmanFunciones
							.nvl(rsRubroInfo.getCampos().get(cAuxiliar), SysmanConstantes.CONS_AUXILIAR));

				} else {
					listaPresupuesto.get(rowNum).getCampos().put(cFuente, SysmanConstantes.CONS_FUENTE);
					listaPresupuesto.get(rowNum).getCampos().put(cCentroCosto, SysmanConstantes.CONS_CENTRO);
					listaPresupuesto.get(rowNum).getCampos().put(cReferencia, SysmanConstantes.CONS_REFERENCIA);
					listaPresupuesto.get(rowNum).getCampos().put(cAuxiliar, SysmanConstantes.CONS_AUXILIAR);
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
	}

	/**
	 * Metodo ejecutado al cambiar el control Vigencia
	 *
	 *
	 */
	public void cambiarVigencia() {
		// <CODIGO_DESARROLLADO>
		Object valorSeleccionado = registro.getCampos().get("ANO");

	    if (valorSeleccionado != null) {
	        ano = valorSeleccionado.toString();
	    }
		// </CODIGO_DESARROLLADO>
	}

	/**
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCmbSolicitudAfectada(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cNroSolicitudAfectada, registroAux.getCampos().get(cNumero));
		registro.getCampos().put(cTercero, registroAux.getCampos().get(cTercero));
		registro.getCampos().put(cDependencia, registroAux.getCampos().get(cDependencia));
		registro.getCampos().put(cNombreTercero, registroAux.getCampos().get(cNombreTercero));
		registro.getCampos().put(cNombreDependencia, registroAux.getCampos().get(cNombreDependencia));
		registro.getCampos().put(cSucursal, registroAux.getCampos().get(cSucursal));
		registro.getCampos().put("OBJETO", registroAux.getCampos().get("OBJETO"));
		registro.getCampos().put(cSolicitudRelacionada, null);
		cargarListaTercero();
	    cargarListaResponsableAprobar();

	}
	
	
	public void seleccionarFilaListaPlantillas(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla =  SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visiblePresentarPlantillas = plantilla==null?false:true;
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipoCompromiso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipoCompromiso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(SolicitudDisponibilidadControladorEnum.TIPO_COMPROMISO.getValue(), 
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(SolicitudDisponibilidadControladorEnum.NOMBRE_COMPROMISO.getValue(), 
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	/**
	 * Metodo ejecutado al cambiar el control Fecha
	 *
	 */
	public void cambiarFecha() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control cmbAprobacion
	 * 
	 * 
	 */
	public void cambiarcmbAprobacion() {
		// <CODIGO_DESARROLLADO>
		if ("Aprobado".equals(registro.getCampos().get(cAprobacion))) {
			registro.getCampos().put("FECHA_APROBACION", new Date());

		} else {
			registro.getCampos().put("FECHA_APROBACION", null);
		}

		// </CODIGO_DESARROLLADO>
	}
	
	/**
	 * Metodo ejecutado al cambiar el control cmbAprobacion
	 * 
	 * 
	 */
	public void cambiarCodigoCCPETC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaPresupuesto.get(rowNum).getCampos().put("CODIGOCPCDANE", "");
		cargarListaCodigoCPCDANE();
		cargarListaCodigoCPCDANEE();

		// </CODIGO_DESARROLLADO>
	}

	private long generarConsecutivoDetalle() {
		long consecutivoDetalle = 1;
		try {
			consecutivoDetalle = ejbSysmanUtil.generarConsecutivoConValorInicial("D_SOLICITUDDISPONIBILIDAD",
					SysmanFunciones.concatenar("COMPANIA = ''", compania, "'' AND ANO=",
							registro.getCampos().get("ANO").toString(), " AND NUMERO=",
							registro.getCampos().get(cNumero).toString()),
					cConsecutivo, OPCIONUNO);

		} catch (SystemException ex) {
			Logger.getLogger(SolicitudDisponibilidadControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		return consecutivoDetalle;
	}

	/**
	 * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
	 * registro que tambien ha sido ingresado por parametro
	 *
	 * @param reg   Registro en el que se desea evaluar el campo
	 * @param campo Campo que se desea consultar
	 * @return Cadena vacia o el valor del campo
	 */
	private String retornarString(Registro reg, String campo) {
		return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cFuente, registroAux.getCampos().get(cCodigo));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbProyecto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbProyecto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cIdProyecto, registroAux.getCampos().get("ID"));

		proyectoSeleccionado = SysmanFunciones
				.toString(registroSub.getCampos().put(cIdProyecto, registroAux.getCampos().get(id)));
		cargarListacmbTipoGasto();
		registroSub.getCampos().put(cIdPlan, null);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbProyecto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbProyectoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("ID");

		proyectoSeleccionado = SysmanFunciones
				.toString(registroSub.getCampos().put(cIdProyecto, registroAux.getCampos().get(id)));

		cargarListacmbTipoGastoE();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTipoGasto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTipoGasto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cIdPlan, registroAux.getCampos().get(cIdPlan));

		registroSub.getCampos().put(cCodigo, registroAux.getCampos().get(cCodigo));

		registroSub.getCampos().put(cVigenciaInicial, registroAux.getCampos().get(cVigenciaInicial));

		if ("SI".equals(parManGastSolDis)) {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(cIdPlan, registroAux.getCampos().get(cIdPlan).toString());

			try {
				Registro rsRubro = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL027.getValue())
										.getUrl(),
								param));

				registroSub.getCampos().put(cRubro, rsRubro.getCampos().get(cRubro));

				param.put(cIdPlan, rsRubro.getCampos().get(cRubro));

				Registro rsRubroInfo = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL028.getValue())
										.getUrl(),
								param));

				if (rsRubroInfo != null) {

					registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),

							GeneralParameterEnum.ANO.getName());
					registroSub.getCampos().put(cFuente,
							SysmanFunciones.nvl(rsRubroInfo.getCampos().get(cFuente), SysmanConstantes.CONS_FUENTE));
					registroSub.getCampos().put(cCentroCosto, SysmanFunciones
							.nvl(rsRubroInfo.getCampos().get(cCentroCosto), SysmanConstantes.CONS_CENTRO));
					registroSub.getCampos().put(cReferencia, SysmanFunciones
							.nvl(rsRubroInfo.getCampos().get(cReferencia), SysmanConstantes.CONS_REFERENCIA));

					registroSub.getCampos().put(cAuxiliar, SysmanFunciones.nvl(rsRubroInfo.getCampos().get(cAuxiliar),
							SysmanConstantes.CONS_AUXILIAR));

				} else {
					registroSub.getCampos().put(cFuente, SysmanConstantes.CONS_FUENTE);
					registroSub.getCampos().put(cCentroCosto, SysmanConstantes.CONS_CENTRO);
					registroSub.getCampos().put(cReferencia, SysmanConstantes.CONS_REFERENCIA);
					registroSub.getCampos().put(cAuxiliar, SysmanConstantes.CONS_AUXILIAR);
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTipoGasto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTipoGastoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get(cIdPlan).toString();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacmbTipoSolicitud
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbTipoSolicitud(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cTipoSolicitud, registroAux.getCampos().get(cCodigo));

		nombreTipoSolicitud = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));

		codigoTipoSolicitud = SysmanFunciones.toString(registroAux.getCampos().get(id));
		
		//INI MPEREZ TICKET7731639
		if(calculaGravamen && codigoTipoSolicitud.equals(OPCIONUNO))
		{
			verImpuesto = true;
		}
		else
		{
			verImpuesto = false;
		}
		//FIN MPEREZ TICKET7731639
		
		if (!nombreTipoSolicitud.equals(cSolicitud)) {
			cargarAfectados = true;
			cargarListaCmbSolicitudAfectada();
			if (nombreTipoSolicitud.equals(cAdicion)) {
				registro.getCampos().put(cTipCompAfect, "SAD");
			} else {
				registro.getCampos().put(cTipCompAfect, "SRE");
			}

			bloqueoSolicitante = true;

		} else {
			cargarAfectados = false;
			bloqueoSolicitante = false;
			registro.getCampos().put(cTipCompAfect, "SCD");

		}

		registro.getCampos().put(cNombreSol, nombreTipoSolicitud);
		if (modificaConsecutivo.equals("NO")) {
			registro.getCampos().put(cNumero, consecutivoSIIF());
		} else {
			registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), consecutivoSolicitud());
		}
	}

	private long consecutivoSolicitud() {
		long consecutivo = 0;
		try {			
			consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial("SOLICITUDDISPONIBILIDAD",
					SysmanFunciones.concatenar("COMPANIA = ''" + compania, "'' AND TIPO_SOLICITUD = ''",
							SysmanFunciones.nvl(codigoTipoSolicitud, "").toString() + "''", " AND ANO = " + ano),
					GeneralParameterEnum.NUMERO.getName(), ano+codigoTipoSolicitud+"00001");
	
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return consecutivo;
	}

	/**
	 * Este metodo me genera un consecutivo dependiendo si la entidad maneja
	 * consecutivo SIIF o no.
	 */

	public String consecutivoSIIF() {

		int consecutivoFinal = 0;
		String consecutivoSalida = "";
		try {
			long consecutivoGenerado = ejbSysmanUtil.generarConsecutivoConValorInicial("SOLICITUDDISPONIBILIDAD",
					SysmanFunciones.concatenar("COMPANIA = ''" + compania, "'' AND TIPO_SOLICITUD = ''",
							SysmanFunciones.nvl(codigoTipoSolicitud, "").toString() + "''", " AND ANO = " + ano),
					GeneralParameterEnum.NUMERO.getName(), 
					"1".equals(codigoTipoSolicitud) ? ano+"000001" : ano+codigoTipoSolicitud+"00001");

			String manejaConsecutivoSiif = obtenerParametro("MANEJA CONSECUTIVO SIIF", "");
			String sufijoEntidad = obtenerParametro("SUFIJO CONSECUTIVO SIIF", "");

			if (manejaConsecutivoSiif.equals(OPCIONSI)) {
				if (consecutivoGenerado == 1) {

					consecutivoSalida = String.valueOf(consecutivoGenerado) + sufijoEntidad;
				} else {
					int tamanoSugijo = sufijoEntidad.length();
					int tamanoConsecutivo = String.valueOf(consecutivoGenerado - 1).length();
					consecutivoFinal = Integer.parseInt(
							String.valueOf(consecutivoGenerado - 1).substring(0, tamanoConsecutivo - tamanoSugijo));
					consecutivoSalida = String.valueOf(consecutivoFinal + 1) + sufijoEntidad;
				}

			} else {
				consecutivoSalida = String.valueOf(consecutivoGenerado);
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return consecutivoSalida;
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuente
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(cCodigo);
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaRubro
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaRubro(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		if (!"0.0".equals(registroAux.getCampos().get("MOVIMIENTO") == null ? ""
				: registroAux.getCampos().get("MOVIMIENTO").toString())) {
			bloqAuxiliar = bloqCentro = bloqFuente = bloqReferencia = bloqTercero = true;
		} else {
			bloqAuxiliar = "0.0".equals(registroAux.getCampos().get("MAN_AUX_GEN") == null ? ""
					: registroAux.getCampos().get("MAN_AUX_GEN").toString());

			bloqCentro = "0.0".equals(registroAux.getCampos().get("MAN_CEN_CTO") == null ? ""
					: registroAux.getCampos().get("MAN_CEN_CTO").toString());

			bloqFuente = "0.0".equals(registroAux.getCampos().get("MAN_AUX_FUE") == null ? ""
					: registroAux.getCampos().get("MAN_AUX_FUE").toString());
			bloqReferencia = "0.0".equals(registroAux.getCampos().get("MAN_AUX_REF") == null ? ""
					: registroAux.getCampos().get("MAN_AUX_REF").toString());

			bloqTercero = "0.0".equals(registroAux.getCampos().get("MAN_AUX_TER") == null ? ""
					: registroAux.getCampos().get("MAN_AUX_TER").toString());
		}

		registroSub.getCampos().put(cRubro, registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put(cTercero, registroAux.getCampos().get(cTercero));
		registroSub.getCampos().put(cAuxiliar, registroAux.getCampos().get(cAuxiliar));
		registroSub.getCampos().put(cFuente, registroAux.getCampos().get(cFuente));
		registroSub.getCampos().put(cCentroCosto, registroAux.getCampos().get(cCentroCosto));
		registroSub.getCampos().put(cReferencia, registroAux.getCampos().get(cReferencia));
		registroSub.getCampos().put(cSucursal, registroAux.getCampos().get(cSucursal));
		registroSub.getCampos().put(sector, registroAux.getCampos().get(sector));
		registroSub.getCampos().put(programa, registroAux.getCampos().get(programa));
		registroSub.getCampos().put(supPrograma, registroAux.getCampos().get(supPrograma));
		registroSub.getCampos().put(codigoProducto, registroAux.getCampos().get(codigoProducto));
		registroSub.getCampos().put(codigoBPIN, registroAux.getCampos().get(codigoBPIN));
		registroSub.getCampos().put(codigoCCPET, registroAux.getCampos().get(codigoCCPET));
		registroSub.getCampos().put(codigoCPCDANE, registroAux.getCampos().get(codigoCPCDANE));
		registroSub.getCampos().put(codigoUnidEje, registroAux.getCampos().get(codigoUnidEje));
		registroSub.getCampos().put(codigoFuente, registroAux.getCampos().get(codigoFuente));
		registroSub.getCampos().put(codigoCCPETRega, registroAux.getCampos().get(codigoCCPETRega));
		registroSub.getCampos().put(politicaPublica, registroAux.getCampos().get(politicaPublica));
		registroSub.getCampos().put(detalleSectorial, registroAux.getCampos().get(detalleSectorial));

		String tipoDestino = SysmanFunciones.nvl(registroAux.getCampos().get("DESTINO"), "").toString();

		if ("I".equals(tipoDestino)) {
			bloqProyecto = false;
			cargarListacmbProyecto();
			listacmbTipoGasto = null;

		} else {
			bloqProyecto = true;
			listacmbProyecto = null;
			cargarListacmbTipoGasto();
		}
		registroSub.getCampos().put(cIdProyecto, null);
		registroSub.getCampos().put(cIdPlan, null);
	}
	
	public void validarObligaCCPET(String rubro) {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put("RUBRO", rubro);

		Registro rs;
		try {
			rs = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
							SolicitudDisponibilidadControladorUrlEnum.URL45116.getValue()).getUrl(), param));

			if(rs!=null) {
				obligaCCPET =rs.getCampos().get("OBLIGACCPET").equals(true)?true:false;	
			}

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}	
	}



	public void validarObligaCPCDANE(String rubro) {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CODIGO.getName(), "006" + rubro);

		Registro rs;
		try {
			rs = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
							SolicitudDisponibilidadControladorUrlEnum.URL1889012.getValue()).getUrl(), param));

			if(rs!=null) {
				obligaCPCDANE =Integer.parseInt(rs.getCampos().get("TOTAL").toString())>0?true:false;	
			}

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}	
	}


	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaRubro
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaRubroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = retornarString(registroAux, id);
		auxiliarRubro = retornarString(registroAux, cAuxiliar);
		tercero = retornarString(registroAux, cTercero);
		centroCosto = retornarString(registroAux, cCentroCosto);
		sucursal = retornarString(registroAux, cSucursal);
		fuente = retornarString(registroAux, cFuente);
		referencia = retornarString(registroAux, cReferencia);
		sector = retornarString(registroAux, sector);
		programa = retornarString(registroAux, programa);
		supPrograma = retornarString(registroAux, supPrograma);
		codigoProducto = retornarString(registroAux, codigoProducto);
		codigoBPIN = retornarString(registroAux, codigoBPIN);
		codigoCCPET = retornarString(registroAux, codigoCCPET);
		codigoCPCDANE = retornarString(registroAux, codigoCPCDANE);
		codigoUnidEje = retornarString(registroAux, codigoUnidEje);
		codigoFuente = retornarString(registroAux, codigoFuente);
		codigoCCPETRega = retornarString(registroAux, codigoCCPETRega);
		politicaPublica = retornarString(registroAux, politicaPublica);
		detalleSectorial = retornarString(registroAux, detalleSectorial);
		
				String tipoDestino = registroAux.getCampos().get("DESTINO").toString();
		if ("I".equals(tipoDestino)) {
			bloqProyecto = false;
			cargarListacmbProyectoE();
		} else {
			bloqProyecto = true;
			cargarListacmbTipoGastoE();
		}

	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listacentroCosto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cCentroCosto, registroAux.getCampos().get(cCodigo));
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listacentroCosto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroCostoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = retornarString(registroAux, cCodigo);
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listareferencia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilareferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(cReferencia, registroAux.getCampos().get(cCodigo));
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listareferencia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilareferenciaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = retornarString(registroAux, cCodigo);
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaTercero
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cTercero, registroAux.getCampos().get("NIT"));
		registro.getCampos().put(cNombreTercero, registroAux.getCampos().get(cNombreTercero));
		registro.getCampos().put(cSucursal, registroAux.getCampos().get(cSucursal));
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaTercero
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = retornarString(registroAux, "NIT");
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cDependencia, registroAux.getCampos().get(cCodigo));
		registro.getCampos().put(cNombreDependencia, registroAux.getCampos().get(cNombreDependencia));

		registro.getCampos().put(cNombreTercero, null);

		registro.getCampos().put(cTercero, null);
		cargarListaTercero();
		cargarListaResponsableAprobar();
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listamodalidad_seleccion
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaModalidadSeleccion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("MODALIDAD_SELECCION", registroAux.getCampos().get(cCodigo));
		registro.getCampos().put(cDescripcionModalidad,
				registroAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("AUXILIAR", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	/**
	 *
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaResponsableAprobar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsableAprobar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cResponsableAprobar, registroAux.getCampos().get("NIT"));
		registro.getCampos().put(cNombreAprobar, registroAux.getCampos().get(cNombreTercero));
		registro.getCampos().put(cSucursalAprobar, registroAux.getCampos().get(cSucursal));
	}


//7725064	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSector
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSector(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("PROGRAMA", "");
		registroSub.getCampos().put("CODIGOPRODUCTO", "");
		sector =  extraerString( registroAux.getCampos().get("CODIGO"));
		cargarListaPrograma();
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSector
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		sector =  auxiliar;
		registroSub.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("PROGRAMA", "");
		registroSub.getCampos().put("CODIGOPRODUCTO", "");
		cargarListaProgramaE();
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPrograma
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPrograma(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		programa =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registroSub.getCampos().put("PROGRAMA", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("CODIGOPRODUCTO", "");
		cargarListaCodigoProducto();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPrograma
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		programa =  auxiliar;
		cargarListaCodigoProductoE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSupPrograma
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupPrograma(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("SUBPROGRAMA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSupPrograma
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoProducto
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProducto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registroSub.getCampos().put("CODIGOPRODUCTO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoProducto
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProductoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CODIGOPRODUCTO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoBPIN
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoBPIN(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CODIGOBPIN", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoBPIN
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoBPINE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPET
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPET(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCCPET =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registroSub.getCampos().put("CODIGOCCPET", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("CODIGOCPCDANE", "");
		cargarListaCodigoCPCDANE();	
		//validarObligaCPCDANE(registroSub.getCampos().get("RUBRO").toString());
	}
	/**
	 * Evento que se activa al seleccionar el combo Cdigo CCPET del formulario
	 *
	 * @param event
	 */
	public void seleccionarFilaCodigoCCPETE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		codigoCCPET =  auxiliar;	
		registroSub.getCampos().put("CODIGOCPCDANE", "");
		registro.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaCodigoCPCDANEE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCPCDANE
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CODIGOCPCDANE", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCPCDANE
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANEE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoUnidEje
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEje(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject(); 
		codigoUnidEje =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        registroSub.getCampos().put("CODIGOUNIDADEJE", registroAux.getCampos().get("CODIGO"));
		registroSub.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaDetalleSectorial();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoUnidEje
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEjeE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		codigoUnidEje = auxiliar;
		registro.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaDetalleSectorialE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFuente
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CODIGOFUENTE", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFuente
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPETRega
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRega(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("CODIGOCCPETREGA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPETRega
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRegaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaPublica
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublica(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("POLITICA_PUBLICA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaPublica
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublicaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put("DETALLE_SECTORIAL", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorialE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}
	
	
	public void seleccionarFilaBeneficiario(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cBeneficiario, registroAux.getCampos().get("NIT"));
		registro.getCampos().put(cNombreBeneficiario, registroAux.getCampos().get("NOMBRE"));
	}
	
	public void seleccionarFilareferenciaGeneral(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(cReferencia, registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put(cNombreReferencia, registroAux.getCampos().get("NOMBRE"));
		
	}
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	
//7725064	
	/**
	 * metodo que llama al oprimir el boton pdf
	 */
	public void oprimirPresentarPlantillas() {
		// <CODIGO_DESARROLLADO>
		if (plantilla == null) {
			oprimirBtnPdf();
		}else {
			generarPdfdesdeWord();
		}
		// </CODIGO_DESARROLLADO>
	}
	
	private void generarPdfdesdeWord() {
//		 TODO Auto-generated method stub
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
	        variablesConsultaW.put("s$ano$s", ano);
	        variablesConsultaW.put("s$tipo$s",  "SCD");
	        variablesConsultaW.put("s$numeroIni$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        variablesConsultaW.put("s$numeroFin$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

	        SessionUtil.cargarModalDatosFlash(
	                        Integer.toString(
	                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
	                                                        .getCodigo()),
	                        SessionUtil.getModulo(),
	                        campos,
	                        valores);

	}
	
	/**
	 *
	 * Metodo ejecutado al oprimir el boton imprimir en la vista
	 *
	 *
	 */
	public void oprimirBtnPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;

		String nombreInforme;

		if ("9".equals(SessionUtil.getModulo())) {
			if (isEspecial()) {
				nombreInforme = "000305FDP";
			} else {
				nombreInforme = "000304SDP";
			}
		} else {
			nombreInforme = parametroReporte("FORMATO SOLICITUD DISPONIBILIDAD PRESUPUESTAL");
			generarReporte(FORMATOS.PDF, nombreInforme);
		}

	}

	/**
	 *
	 * Metodo ejecutado al oprimir el boton VISTAPREVIA en la vista
	 *
	 *
	 */
	public void oprimirBtnExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String nombreInforme;

		if ("9".equals(SessionUtil.getModulo())) {
			if (isEspecial()) {
				nombreInforme = "000305FDP";
			} else {
				nombreInforme = "000304SDP";
			}
		}else{
				nombreInforme = parametroReporte("FORMATO SOLICITUD DISPONIBILIDAD PRESUPUESTAL");
				generarReporte(FORMATOS.EXCEL, nombreInforme);
			}      
		}
		// </CODIGO_DESARROLLADO>
	

	/**
	 *
	 * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
	 *
	 *
	 */
	public void oprimircmdPantalla() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al presionar el boton Workflow (BT3268) en la vista.
	 */
	public void oprimirBtWorkflow() {
		archivoDescarga = null;
		String nombreInforme;

		if ("9".equals(SessionUtil.getModulo())) {
			if (isEspecial()) {
				nombreInforme = "000305FDP";
			} else {
				nombreInforme = "000304SDP";
			}
		} else {
			nombreInforme = parametroReporte("FORMATO SOLICITUD DISPONIBILIDAD PRESUPUESTAL");
			generarReporte(FORMATOS.PDF, nombreInforme);
		}

		if (archivoDescarga != null) {
			String[] claves = { ConstantesWorkflowEnum.PR_ARCHIVODESCARGA.getValue() };

			Object[] valores = { archivoDescarga };

			SessionUtil.cargarModalDatosFlashCerrar(
					Integer.toString(GeneralCodigoFormaEnum.FRM_SELECCIONAR_TRAMITES_CONTROLADOR.getCodigo()), modulo,
					claves, valores);
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	/**
	 * Metodo de insercion del formulario Presupuesto
	 *
	 */
	public void agregarRegistroSubPresupuesto() {
		String solicitusAfect = (String) SysmanFunciones.nvl(registro.getCampos().get(cNroSolicitudAfectada), "0");
		solicitusAfect = solicitusAfect.isEmpty() || "".equals(solicitusAfect) ? "0" : solicitusAfect;
		try {
			validarObligaCCPET(registroSub.getCampos().get("RUBRO").toString());
			validarObligaCPCDANE(SysmanFunciones.nvlStr(SysmanFunciones.toString(registroSub.getCampos().get("CODIGOCCPET")),""));

			if(obligaCCPET) {
				campos = "";
				String valorcodigoCCPET = SysmanFunciones.nvlStr(SysmanFunciones.toString(registroSub.getCampos().get("CODIGOCCPET")),"");
				String valorcodigoCPCDANE = SysmanFunciones.nvlStr(SysmanFunciones.toString(registroSub.getCampos().get("CODIGOCPCDANE")),"");
				
				String alerta = "  Por favor ingrese los campos obligatorios: ";
				campos = valorcodigoCCPET.equals("")?campos + "\n Codigo CCPET":"";
				if(obligaCPCDANE) {
					campos = valorcodigoCPCDANE.equals("")?campos + "  Codigo CPCDANE":"";								
				}
				

				if(valorcodigoCCPET.equals("") || (obligaCPCDANE && valorcodigoCPCDANE.equals(""))) {
					JsfUtil.agregarMensajeErrorDialogo(alerta + campos);
					return;
				}

			}
				
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));

			registroSub.getCampos().put(cCompania, compania);

			registroSub.getCampos().put("ANO", registro.getCampos().get("ANO"));

			registroSub.getCampos().put(cNumero, registro.getCampos().get(cNumero));
			registroSub.getCampos().put(cConsecutivo, generarConsecutivoDetalle());
			registroSub.getCampos().put(cTipoSolicitud, registro.getCampos().get(cTipoSolicitud));
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());

			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.D_SOLICITUDDISPONIBILIDAD.getCreateKey());

			/**
			 * En esta seccion se valida que si el parametro esta en SI e intenta dejar al
			 * campo ID_PLAN en nulo, le diga que ese campo es obligatorio, dado que caqueta
			 * debe permitir el campo anterioremente mencionado como opcional,sin embargo
			 * entidades como la ANE no
			 */
			if ((registroSub.getCampos().get(cIdPlan) == null
					|| registroSub.getCampos().get(cIdPlan).toString().isEmpty())
					&& obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

				JsfUtil.ejecutarJavaScript(cJavaScript);
				return;
			} else {

				int respuestaSaldoItem;
				if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania,
							SysmanFunciones.toString(registroSub.getCampos().get(cIdPlan)),
							Integer.parseInt(SysmanFunciones.toString(registroSub.getCampos().get(cVigenciaInicial))),
							BigDecimal.valueOf(Double.parseDouble(registroSub.getCampos().get(cValor).toString())),
							Integer.parseInt(SysmanFunciones.toString(registroSub.getCampos().get(cCodigo))),
							Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud))), "I",
							BigDecimal.valueOf(0), SysmanFunciones.toString(registroSub.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(registroSub.getCampos().get(cRubro)),
									SysmanFunciones.toString(registroSub.getCampos().get("ANO")),
									registroSub.getCampos().get(cFuente) == null ? " "
											: registroSub.getCampos().get(cFuente).toString(),
									registroSub.getCampos().get("CENTRO_COSTO").toString(),
									registroSub.getCampos().get("REFERENCIA").toString()),
							registroSub.getCampos().get(cFuente).toString(),
							registroSub.getCampos().get("CENTRO_COSTO").toString(),
							registroSub.getCampos().get("REFERENCIA").toString(), Integer.parseInt(solicitusAfect),
							Integer.parseInt(registroSub.getCampos().get(cNumero).toString()));
				} else {

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania, "0", 0,
							BigDecimal.valueOf(Double.parseDouble(registroSub.getCampos().get(cValor).toString())), 0,
							Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud))), "I",
							BigDecimal.valueOf(0), SysmanFunciones.toString(registroSub.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(registroSub.getCampos().get(cRubro)),
									SysmanFunciones.toString(registroSub.getCampos().get("ANO")),
									registroSub.getCampos().get(cFuente) == null ? " "
											: registroSub.getCampos().get(cFuente).toString(),
									registroSub.getCampos().get("CENTRO_COSTO").toString(),
									registroSub.getCampos().get("REFERENCIA").toString()),
							registroSub.getCampos().get(cFuente).toString(),
							registroSub.getCampos().get("CENTRO_COSTO").toString(),
							registroSub.getCampos().get("REFERENCIA").toString(),
							Integer.parseInt(SysmanFunciones.toString(solicitusAfect)),
							Integer.parseInt(registroSub.getCampos().get(cNumero).toString()));

				}

				if (respuestaSaldoItem == -1  || respuestaSaldoItem == 2) {//NINGUNO SE PASA

					requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), registroSub.getCampos());
					if(!verImpuesto)
					{
						cargarRegistro(registro.getLlave(), accion, registro.getIndice());
					}
					cargarListaPresupuesto();
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
				} else {

					String tipoPAA;
					if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

						Map<String, Object> parametrosIndicador = new TreeMap<>();

						parametrosIndicador.put(GeneralParameterEnum.COMPANIA.getName(),
								registroSub.getCampos().get(cCompania));
						parametrosIndicador.put(cVigenciaInicial, registroSub.getCampos().get(cVigenciaInicial));

						parametrosIndicador.put(cIdPlan, registroSub.getCampos().get(cIdPlan));

						parametrosIndicador.put(GeneralParameterEnum.CODIGO.getName(),
								registroSub.getCampos().get(cCodigo));

						Registro rsValorAcum = RegistroConverter
								.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL018.getValue())
										.getUrl(), parametrosIndicador));
						tipoPAA = SysmanFunciones.toString(rsValorAcum.getCampos().get(cIndicadorPaa));

					} else {
						/**
						 * Se inserta no aplica dado que no maneja plan de accion
						 */
						tipoPAA = cNoAplica;
					}
				}
					
					if (respuestaSaldoItem == 0 || respuestaSaldoItem == 3  )// RUBRO SE PASA
					{
						JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4032));

					} else if (respuestaSaldoItem == 4) {
						JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(msjTBTB4090));
					}

					// FIN DEL ELSE DE VALIDAD CAMPO ID_PLAN
					if ( verImpuesto) {
						cargarRegistro(registro.getLlave(), accion, registro.getIndice());				
					}
				}
		}catch (	SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}

	cargarListaPresupuesto();
	registroSub.setCampos(new HashMap<String, Object>());
	}

	/**
	 * metodo que obtiene el saldo disponible de un rubro especifico
	 * 
	 * @param rubro
	 * @param ano
	 * @return
	 */
	public BigDecimal obtenerSaldoRubro(String rubro, String ano, String fuente, String centroCosto,
			String referencia) {
		BigDecimal saldoRubro = new BigDecimal("0");
		Map<String, Object> parametrosSaldo = new TreeMap<>();
		parametrosSaldo.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		parametrosSaldo.put(GeneralParameterEnum.CODIGO.getName(), rubro);

		parametrosSaldo.put(GeneralParameterEnum.ANO.getName(), ano);

		parametrosSaldo.put("MESINICIAL", 1);

		parametrosSaldo.put("MESFINAL", 12);

		Registro saldoActual;

		try {
			/**
			 * Se valida que si se ingresa por CAQUETA, filtre el rubro por los respectivos
			 * auxiliares
			 */
			String servicio = "";

			// if ("800091594-4".equals(SessionUtil.getCompaniaIngreso().getNit())
			// || "800.091.594-4".equals(
			// SessionUtil.getCompaniaIngreso().getNit())) {

			parametrosSaldo.put(cFuente, fuente);
			parametrosSaldo.put("CENTRO_COSTO", centroCosto);
			parametrosSaldo.put("REFERENCIA", referencia);
			servicio = SolicitudDisponibilidadControladorUrlEnum.URL022.getValue();
			// }
			// else {
			// servicio = SolicitudDisponibilidadControladorUrlEnum.URL017
			// .getValue();
			// }
			saldoActual = RegistroConverter.toRegistro(requestManager
					.get(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(servicio).getUrl(), parametrosSaldo));

			if (saldoActual != null) {

				saldoRubro = SysmanFunciones.toString(saldoActual.getCampos().get("SALDODISPONIBLE")) == null
						? new BigDecimal("0")
						: new BigDecimal(SysmanFunciones.toString(saldoActual.getCampos().get("SALDODISPONIBLE")));
			} else {
				saldoRubro = new BigDecimal("0");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return saldoRubro;
	}

	/**
	 * Metodo de edicion del formulario Presupuesto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void  editarRegSubPresupuesto(RowEditEvent event) {
		String solicitusAfect = SysmanFunciones.nvl(registro.getCampos().get(cNroSolicitudAfectada), "0").toString();
		solicitusAfect = solicitusAfect.isEmpty() || "".equals(solicitusAfect) ? "0" : solicitusAfect;
		try {
			Registro reg = (Registro) event.getObject();
			validarObligaCCPET(registroAuxiliarSub.getCampos().get("RUBRO").toString());
			validarObligaCPCDANE(SysmanFunciones.nvlStr(SysmanFunciones.toString(reg.getCampos().get("CODIGOCCPET")),""));
			if(obligaCCPET) {
				campos = "";
				String valorcodigoCCPET = SysmanFunciones.nvlStr(SysmanFunciones.toString(reg.getCampos().get("CODIGOCCPET")),"");
				String valorcodigoCPCDANE = SysmanFunciones.nvlStr(SysmanFunciones.toString(reg.getCampos().get("CODIGOCPCDANE")),"");
				
				String alerta = "  Por favor ingrese los campos obligatorios: ";
				campos = valorcodigoCCPET.equals("")?campos + "\n Codigo CCPET":"";
				if(obligaCPCDANE) {
					campos = valorcodigoCPCDANE.equals("")?campos + "  Codigo CPCDANE":"";								
				}
			
				if(valorcodigoCCPET.equals("") || (obligaCPCDANE && valorcodigoCPCDANE.equals(""))) {
					JsfUtil.agregarMensajeErrorDialogo(alerta + campos);
					obligaCPCDANE = false;
					return;
				}
			}
			
			/**
			 * En esta seccion se valida que si se esta modificando un detalle se calcule el
			 * valor actual del detalle antes de ser modificado
			 */
			Map<String, Object> parametros = new TreeMap<>();

			parametros.put(GeneralParameterEnum.COMPANIA.getName(),
					reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()));
			parametros.put(GeneralParameterEnum.ANO.getName(), reg.getCampos().get(GeneralParameterEnum.ANO.getName()));
			parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
					reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
			parametros.put(GeneralParameterEnum.NUMERO.getName(),
					reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			parametros.put(GeneralParameterEnum.RUBRO.getName(),
					reg.getCampos().get(GeneralParameterEnum.RUBRO.getName()));
			parametros.put(cTipoSolicitud, reg.getCampos().get(cTipoSolicitud));

			Registro rsValor;
			BigDecimal valorDetalle;

			if (!actualizaTipoGasto) {

				rsValor = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												SolicitudDisponibilidadControladorUrlEnum.URL016.getValue())
										.getUrl(),
								parametros));

				valorDetalle = BigDecimal.valueOf(Double.parseDouble(
						SysmanFunciones.toString(rsValor.getCampos().get(GeneralParameterEnum.VALOR.getName()))));
			} else {
				valorDetalle = BigDecimal.valueOf(
						Double.parseDouble(reg.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString()));
			}
			int respuestaSaldoItem;
			String tipoPAA;
			/**
			 * En esta seccion se valida que si el parametro esta en SI e intenta dejar al
			 * campo ID_PLAN en nulo, le diga que ese campo es obligatorio, dado que caqueta
			 * debe permitir el campo anterioremente mencionado como opcional,sin embargo
			 * entidades como la ANE no
			 */
			if ((reg.getCampos().get(cIdPlan) == null || reg.getCampos().get(cIdPlan).toString().isEmpty())
					&& obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

				JsfUtil.ejecutarJavaScript(cJavaScript);
				return;
			} else {

				if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania,
							SysmanFunciones.toString(reg.getCampos().get(cIdPlan)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cVigenciaInicial))),

							BigDecimal
									.valueOf(Double.parseDouble(SysmanFunciones.toString(reg.getCampos().get(cValor)))),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cCodigo))),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cTipoSolicitud))), "M",
							valorDetalle, SysmanFunciones.toString(reg.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(reg.getCampos().get(cRubro)),
									SysmanFunciones.toString(reg.getCampos().get("ANO")),
									reg.getCampos().get(cFuente) == null ? " "
											: reg.getCampos().get(cFuente).toString(),
									reg.getCampos().get("CENTRO_COSTO").toString(),
									reg.getCampos().get("REFERENCIA").toString()),
							reg.getCampos().get(cFuente).toString(), reg.getCampos().get("CENTRO_COSTO").toString(),
							reg.getCampos().get("REFERENCIA").toString(), Integer.parseInt(solicitusAfect),
							Integer.parseInt(reg.getCampos().get(cNumero).toString()));

					Map<String, Object> parametrosIndicador = new TreeMap<>();

					parametrosIndicador.put(GeneralParameterEnum.COMPANIA.getName(), reg.getCampos().get(cCompania));
					parametrosIndicador.put(cVigenciaInicial, reg.getCampos().get(cVigenciaInicial));

					parametrosIndicador.put(cIdPlan, reg.getCampos().get(cIdPlan));

					parametrosIndicador.put(GeneralParameterEnum.CODIGO.getName(), reg.getCampos().get(cCodigo));

					Registro rsValorAcum;

					rsValorAcum = RegistroConverter.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("1034008").getUrl(),
							parametrosIndicador));
					tipoPAA = SysmanFunciones.toString(rsValorAcum.getCampos().get(cIndicadorPaa));

				} else {{

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania, "0", 0,

							BigDecimal
									.valueOf(Double.parseDouble(SysmanFunciones.toString(reg.getCampos().get(cValor)))),
							0, Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cTipoSolicitud))), "M",
							valorDetalle, SysmanFunciones.toString(reg.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(reg.getCampos().get(cRubro)),
									SysmanFunciones.toString(reg.getCampos().get("ANO")),
									reg.getCampos().get(cFuente) == null ? " "
											: reg.getCampos().get(cFuente).toString(),
									reg.getCampos().get("CENTRO_COSTO").toString(),
									reg.getCampos().get("REFERENCIA").toString()),
							reg.getCampos().get(cFuente).toString(), reg.getCampos().get("CENTRO_COSTO").toString(),
							reg.getCampos().get("REFERENCIA").toString(), Integer.parseInt(solicitusAfect),
							Integer.parseInt(reg.getCampos().get(cNumero).toString()));

					tipoPAA = cNoAplica;
				}
				
				reg.getCampos().remove(cCompania);
				reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
				reg.getCampos().remove(cConsecutivo);
				reg.getCampos().remove(cNumero);
				reg.getCampos().remove("NOMBRE_CENTRO");
				reg.getCampos().remove("NOMBRE_REFERENCIA");
				reg.getCampos().remove("NOMBRE_FUENTE");
				reg.getCampos().remove("NOMBRE_AUXILIAR");
				reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
				reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GenericUrlEnum.D_SOLICITUDDISPONIBILIDAD.getUpdateKey());
				
				switch(respuestaSaldoItem) {
				case 0:
				case 3:
					JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4032));
					break;
				case 2:
				case -1:
				case -2:
					requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
					break;
				case 4:
					JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(msjTBTB4090));
					break;
				case 5:
					JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(msjTBTB4435));
					break;
				case 6:
					JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4434));
					break;
				case 7:
					JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4436));
					break;	
				default:
					JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4437));
					}
				}}} catch (SystemException  ex) {
			Logger.getLogger(CentroscostosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaPresupuesto();			
			cargarRegistro(registro.getLlave(), accion, registro.getIndice());			
			cargarListaRubro();
		}
	}

	public void ejecutarformularioEditar() {
		/**
		 * Envio de mensaje cuando el rubro sobrepasa el saldo
		 */
	}

	/**
	 * Metodo de eliminacion del formulario Presupuesto
	 *
	 *
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubPresupuesto(Registro reg) {
		String solicitusAfect = (String) SysmanFunciones.nvl(registro.getCampos().get(cNroSolicitudAfectada), "0");
		solicitusAfect = solicitusAfect.isEmpty() || "".equals(solicitusAfect) ? "0" : solicitusAfect;
		try {

			/**
			 * En esta seccion se valida que si el parametro esta en SI e intenta dejar al
			 * campo ID_PLAN en nulo, le diga que ese campo es obligatorio, dado que caqueta
			 * debe permitir el campo anterioremente mencionado como opcional,sin embargo
			 * entidades como la ANE no
			 */
			int respuestaSaldoItem;
			if ((reg.getCampos().get(cIdPlan) == null || reg.getCampos().get(cIdPlan).toString().isEmpty())
					&& obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

				JsfUtil.ejecutarJavaScript(cJavaScript);
				return;
			} else {

				if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania,
							SysmanFunciones.toString(reg.getCampos().get(cIdPlan)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cVigenciaInicial))),

							BigDecimal.valueOf(Double.parseDouble(reg.getCampos().get(cValor).toString())),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cCodigo))),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cTipoSolicitud))), "E",
							BigDecimal.valueOf(0), SysmanFunciones.toString(reg.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(reg.getCampos().get(cRubro)),
									SysmanFunciones.toString(reg.getCampos().get("ANO")),
									reg.getCampos().get(cFuente) == null ? " "
											: reg.getCampos().get(cFuente).toString(),
											reg.getCampos().get("CENTRO_COSTO").toString(),
											reg.getCampos().get("REFERENCIA").toString()),
							reg.getCampos().get(cFuente).toString(), reg.getCampos().get("CENTRO_COSTO").toString(),
							reg.getCampos().get("REFERENCIA").toString(), Integer.parseInt(solicitusAfect),
							Integer.parseInt(reg.getCampos().get(cNumero).toString()));

				} else {

					respuestaSaldoItem = ejbPresupuestoTres.actualizarValorSolicitado(compania, "0", 0,

							BigDecimal
							.valueOf(Double.parseDouble(SysmanFunciones.toString(reg.getCampos().get(cValor)))),
							0, Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get(cTipoSolicitud))), "E",
							BigDecimal.valueOf(0), SysmanFunciones.toString(reg.getCampos().get(cRubro)),
							Integer.parseInt(SysmanFunciones.toString(reg.getCampos().get("ANO"))),
							obtenerSaldoRubro(SysmanFunciones.toString(reg.getCampos().get(cRubro)),
									SysmanFunciones.toString(reg.getCampos().get("ANO")),
									reg.getCampos().get(cFuente) == null ? " "
											: reg.getCampos().get(cFuente).toString(),
											reg.getCampos().get("CENTRO_COSTO").toString(),
											reg.getCampos().get("REFERENCIA").toString()),
							reg.getCampos().get(cFuente).toString(), reg.getCampos().get("CENTRO_COSTO").toString(),
							reg.getCampos().get("REFERENCIA").toString(), Integer.parseInt(solicitusAfect),
							Integer.parseInt(reg.getCampos().get(cNumero).toString()));

				}
			}

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.D_SOLICITUDDISPONIBILIDAD.getDeleteKey());
			
			if (respuestaSaldoItem == -1 || respuestaSaldoItem == 2) {
				requestManager.delete(urlDelete.getUrl(), reg.getLlave());
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			} else {
				
				String tipoPAA;
				if (obtenerParametro(cManejaTipoGasto, "SI").equals("SI")) {
					Map<String, Object> parametrosIndicador = new TreeMap<>();

					parametrosIndicador.put(GeneralParameterEnum.COMPANIA.getName(), reg.getCampos().get(cCompania));
					parametrosIndicador.put(cVigenciaInicial, reg.getCampos().get(cVigenciaInicial));

					parametrosIndicador.put(cIdPlan, reg.getCampos().get(cIdPlan));

					parametrosIndicador.put(GeneralParameterEnum.CODIGO.getName(), reg.getCampos().get(cCodigo));

					Registro rsValorAcum;
					rsValorAcum = RegistroConverter.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID("1034008").getUrl(),
							parametrosIndicador));
					tipoPAA = SysmanFunciones.toString(rsValorAcum.getCampos().get(cIndicadorPaa));
					
				} else {
					tipoPAA = cNoAplica;
				}
			}
			switch (respuestaSaldoItem){
			case -1:
			case 2:
				// cierre de validación, saldos correctos.				
				break;
			case 3:
			case 0:// RUBRO SE PASA
				
				JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4032));
				break;
			case 4:
				JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(msjTBTB4090));
				break;
			case 5:
				JsfUtil.agregarMensajeAlertaDialogo(idioma.getString(msjTBTB4435));
				break;
			case 6:
				JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4434));
				break;	
			case 7:
				JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4436));
				break;	
			default:
				JsfUtil.agregarMensajeErrorDialogo(idioma.getString(msjTBTB4437));
				}
				cargarRegistro(registro.getLlave(), accion, registro.getIndice());
			cargarListaPresupuesto();

		
			} catch (SystemException  ex) {
			Logger.getLogger(CentroscostosControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			if(!verImpuesto)
			{
				cargarRegistro(registro.getLlave(), accion, registro.getIndice());
			}
			cargarListaRubro();
		}
	}


	/**
	 * Metodo ejecutado al cambiar el control ValorSolicitado
	 * 
	 * 
	 */

	public void cambiarValor(){
//
	}
	public void cambiarValorBase() {
		if (verImpuesto) {
			double impuesto = (Double.parseDouble(registro.getCampos().get("VALOR_BASE").toString()) * valorGMF) / 1000;
			impuesto = SysmanFunciones.redondear(impuesto, 2);
			double valorConImpuesto = Double.parseDouble(registro.getCampos().get("VALOR_BASE").toString()) + impuesto;
			valorConImpuesto = SysmanFunciones.redondear(valorConImpuesto, 2);
			registro.getCampos().put("IMPUESTO", impuesto);
			registro.getCampos().put("VALOR", valorConImpuesto);
		}
	}

	public void cambiarValorBaseDetalle(){
		if (verImpuesto) {
			double valorDetalle = Double.parseDouble(registroSub.getCampos().get("VALOR_BASE").toString());
			double impuestoDetalle = (valorDetalle * valorGMF) / 1000;
			double valorTotal = valorDetalle + impuestoDetalle;
			impuestoDetalle = SysmanFunciones.redondear(impuestoDetalle, 2);
			valorTotal = SysmanFunciones.redondear(valorTotal, 2);
			registroSub.getCampos().put("IMPUESTO", impuestoDetalle);
			registroSub.getCampos().put("VALOR", valorTotal);

		}
	}


	public void cambiarValorBaseDetalleC(int rowNum) {
		if (verImpuesto) {
			double valorBase = Double.parseDouble(listaPresupuesto.get(rowNum).getCampos().get("VALOR_BASE").toString());
			double impuestoDetalle = (valorBase * valorGMF) / 1000;
			double valorTotal = valorBase + impuestoDetalle;
			impuestoDetalle = SysmanFunciones.redondear(impuestoDetalle, 2);
			valorTotal = SysmanFunciones.redondear(valorTotal, 2);
			listaPresupuesto.get(rowNum).getCampos().put("IMPUESTO", impuestoDetalle); 
			listaPresupuesto.get(rowNum).getCampos().put("VALOR", valorTotal); 
		}
	}

	

	/**
	 * Metodo ejecutado cuando ...
	 *
	 */
	public void activarEdicionPresupuesto(Registro registro) {
		indicePresupuesto = listaPresupuesto.indexOf(registro);
		registroAuxiliarSub = new Registro(new HashMap<>(registro.getCampos()));
		naturalezaCuenta =  (String) registro.getCampos().get("NATURALEZA");
		cargarListaSector();
		cargarListaSectorE();
		cargarListaPrograma();
		cargarListaProgramaE();
		cargarListaSupPrograma();
		cargarListaSupProgramaE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
		cargarListaCodigoBPIN();
		cargarListaCodigoBPINE();
		cargarListaCodigoCCPET();
		cargarListaCodigoCCPETE();
		cargarListaCodigoUnidEje();
		cargarListaCodigoUnidEjeE();
		cargarListaCodigoFuente();
		cargarListaCodigoFuenteE();
		cargarListaCodigoCCPETRega();
		cargarListaCodigoCCPETRegaE();
		cargarListaPoliticaPublica();
		cargarListaPoliticaPublicaE();
		cargarListaDetalleSectorial();
		cargarListaDetalleSectorialE();
	}
	

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Presupuesto
	 *
	 */
	public void cancelarEdicionPresupuesto() {
		cargarListaPresupuesto();
	}

	// </METODOS_SUBFORM>

	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		if ("9".equals(SessionUtil.getModulo())) {
			if (isVisibleAfectado()) {
				visibleAfectado = false;
			} else {
				visibleAfectado = true;
			}
		}
		

		// indManejaWF = validarValorParSI("MANEJA WORKFLOW");

		// </CODIGO_DESARROLLADO>

	}


	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		camposProtegidosAfectados.clear();

		bloqueoRubro = false;
		
		if (accion.equals(ACCION_INSERTAR)) {
			registro.getCampos().put(cValor, 0);
			registro.getCampos().put("ANO", SysmanFunciones.ano(new Date()));
			registro.getCampos().put("FECHA", new Date());
			ano = String.valueOf(SysmanFunciones.ano(new Date()));
			bloqueoGeneral = false;
			//bloqueoAprobacion = true;
			cargarMotivoRechazo = false;
			if (calculaGravamen)
			{
				verImpuesto = true;
			}
			else
			{
				verImpuesto = false;
			}

		} else if (accion.equals(ACCION_MODIFICAR)) {

			cargarListaRubro();
			cargarListaRubroE();
			if (calculaGravamen && SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO))
			{
				verImpuesto = true;
			}
			else
			{
				verImpuesto = false;
			}

			if ("SI".equals(parManGastSolDis)) {
				bloqueoRubro = true;
			}

			if (SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO)) {
				cargarAfectados = false;
				insertarSubFormulario = true;
				bloqueoSolicitante = false;
			} else {
				cargarAfectados = true;
				insertarSubFormulario = false;
				bloqueoSolicitante = true;
				cargarListaCmbSolicitudAfectada();
			}

			if ((boolean) registro.getCampos().get(SolicitudDisponibilidadControladorEnum.IMPRESO.getValue())) {
				bloqueoGeneral = true;
				insertarSubFormulario = false;
				permitirSubFormulario = false;
				bloqueoAfectador = true;
			} else {
				bloqueoGeneral = false;
				bloqueoAfectador = false;
				if (SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO)) {
					insertarSubFormulario = true;					
				} else {
					insertarSubFormulario = false;					
				}

				permitirSubFormulario = true;
			}

			/**
			 * Validacion para que solo el ordenador del gasto pueda ver el campo de
			 * aprobacion
			 */
			/*try {
				boolean esOrdanador = ejbPresupuestoTres.esOrdenador(compania,
						SysmanFunciones.toString(SessionUtil.getUser().getCedula()));
				if (esOrdanador) {
					bloqueoAprobacion = false;
				} else {
					bloqueoAprobacion = true;
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}*/

			/**
			 * Validacion para saber si la solicitud que se esta abriendo se encuentra en en
			 * modo no aprobada osea en tipo 0
			 */
			if (SysmanFunciones.toString(registro.getCampos().get(cAprobacion)).equals(msjNoAprobado)) {
				bloqueoGeneral = true;
				insertarSubFormulario = false;
				permitirSubFormulario = false;
				//bloqueoAprobacion = true;
				bloqueoAfectador = true;
				cargarMotivoRechazo = true;
			} else {
				cargarMotivoRechazo = false;
			}

			guardarCamposProtegidosAfectados();

		} else if (accion.equals(ACCION_VER)) {

			if (SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO)) {
				cargarAfectados = false;
			} else {
				cargarAfectados = true;
			}

			if (SysmanFunciones.toString(registro.getCampos().get(cAprobacion)).equals(msjNoAprobado)) {
				cargarMotivoRechazo = true;
			}
		}

		try {

			modificaConsecutivo = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"PERMITE MODIFICAR CONSECUTIVOS SOLICITUD CDP PPTAL", modulo, new Date(), true).toString(), "NO")
					.toString();

			bloquearNumero = modificaConsecutivo.equals("NO");
			
			cadenaRecursos = "";
			cadenaRecursos = SysmanFunciones.nvl(registro.getCampos().get(cRecurso),"").toString();
			
			if (cadenaRecursos.contains("BPIN")) {
				bpin = true;
			}else {
				bpin = false;
			}
			if (cadenaRecursos.contains("COFINANCIACION")) {
				cofinanciacion = true;
			}else {
				cofinanciacion = false;
			}
			if (cadenaRecursos.contains("FUNCIONAMIENTO")) {
				funcionamiento = true;
			}else {
				funcionamiento = false;
			}
			if (cadenaRecursos.contains("PROPIOS")) {
				propios = true;
			}else {
				propios = false;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean insertarAntes() {

		String solicitudAfectada = SysmanFunciones.toString(registro.getCampos().get(cNroSolicitudAfectada));
		String comprobanteAfectado = SysmanFunciones.toString(registro.getCampos().get("TIPO_CPTE_AFECT"));
		String disponibilidadRelacionada = registro.getCampos().get(cSolicitudRelacionada).toString();
		if ((!nombreTipoSolicitud.equals(cSolicitud)) && (solicitudAfectada == null || comprobanteAfectado == null)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4074"));
			return false;
		}

		if ((!nombreTipoSolicitud.equals(cSolicitud))
				&& (disponibilidadRelacionada == null || disponibilidadRelacionada.equals(""))) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4155"));
			return false;
		}

		validarRecursos();		
		registro.getCampos().put(cRecurso, cadenaRecursos);
		cadenaRecursos = "";
		
		// <CODIGO_DESARROLLADO>
		if (accion.equals(ACCION_INSERTAR)) {
			//registro.getCampos().put(cValor, 0);
			registro.getCampos().put(cCompania, compania);
			registro.getCampos().put(cTipoSolicitud, codigoTipoSolicitud);
			registro.getCampos().remove(cNombreDependencia);
			registro.getCampos().remove(cNombreTercero);
			registro.getCampos().remove(cDescripcionModalidad);
			registro.getCampos().remove(cNombreSol);
			registro.getCampos().remove(cNombreAprobar);
			registro.getCampos().put(cAprobacion, "2");
			registro.getCampos().remove(cNombreBeneficiario);
			registro.getCampos().remove(cNombreReferencia);
			registro.getCampos().remove(SolicitudDisponibilidadControladorEnum.NOMBRE_COMPROMISO.getValue());
			if (!SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals("1")) {
				registro.getCampos().put("TIPO_AFECTACION", "1");
			}
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel en la vista
	 *
	 *
	 */
	public void oprimirbtnEnviar() {

		/**
		 * Se define si es ordenador del gasto o no
		 */
		boolean esOrdanador = false;
		try {
			esOrdanador = ejbPresupuestoTres.esOrdenador(compania,
					SysmanFunciones.toString(SessionUtil.getUser().getCedula()));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


		/**
		 * Validaciones para el ordenador del gasto
		 */
		// SECCION PARA INGRESAR EL SI ESTA APROBADO O NO
		if (registro.getCampos().get(cAprobacion) != null && esOrdanador) {

			registro.getCampos().put(cAprobacion, calcularAprobacion(2));
		
		}
	}



	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean insertarDespues() {
		/*
		 * SECCION PARA VALIDAR SI LO QUE VA A ACTUALIZAR ES TIPO ADICION
		 */
		if (accion.equals(ACCION_INSERTAR)
				&& (!SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO))) {

			try {
				ejbPresupuestoTres.afectarSolicitud(compania,
						Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cNroSolicitudAfectada))),
						Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cNumero))),
						Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud))));
			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (!validarEdicionCamposAfectados()) {
			return false;
		}

		if (accion.equals(ACCION_MODIFICAR)) {

			registro.getCampos().put("VALOR", Double.parseDouble(registro.getCampos().get("VALOR").toString()));

			String disponibilidadRelacionada = registro.getCampos().get(cSolicitudRelacionada).toString();

			if ((!nombreTipoSolicitud.equals(cSolicitud))
					&& (disponibilidadRelacionada == null || disponibilidadRelacionada.equals(""))) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4155"));
				return false;
			}

			/**
			 * Este llamado se hace para que cada vez que se actualice una solicitud,
			 * automaticamente se actualien las adiciones y reducciones que dependen de ella
			 * tanto en detalles como en el tipo de aprobacion que esta tenga
			 */
			if (SysmanFunciones.toString(registro.getCampos().get(cTipoSolicitud)).equals(OPCIONUNO)) {

				try {
					ejbPresupuestoTres.actualizarTerDeoSolicitud(compania,
							Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cNumero))),
							SysmanFunciones.toString(registro.getCampos().get(cTercero)),
							SysmanFunciones.toString(registro.getCampos().get(cDependencia)));

					if (ejbPresupuestoTres.actualizarSolicitudesNoAprobadas(compania,
							Long.parseLong(SysmanFunciones.toString(registro.getCampos().get(cNumero))),
							calcularAprobacion(1))) {
						bloqueoAfectador = true;
					}

				} catch (NumberFormatException | SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}

				registro.getCampos().put(cSolicitudRelacionada, " ");

			}
			if (registro.getCampos().get(cAprobacion) != null) {

				calcularAprobacion(2);
			}

			validarRecursos();			
			registro.getCampos().put(cRecurso, cadenaRecursos);
			cadenaRecursos = "";
			
			
			// SESSION AGREGA Y ELIMINA CAMPOS
			registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			registro.getCampos().remove(cNombreDependencia);
			registro.getCampos().remove(cNombreTercero);
			registro.getCampos().remove(cDescripcionModalidad);
			registro.getCampos().remove(cNombreSol);
			registro.getCampos().remove(cNombreSol);
			registro.getCampos().remove(cNumero);
			registro.getCampos().remove(cNombreAprobar);
			registro.getCampos().remove(cNombreBeneficiario);
			registro.getCampos().remove(cNombreReferencia);
			registro.getCampos().remove(SolicitudDisponibilidadControladorEnum.NOMBRE_COMPROMISO.getValue());

		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	private void guardarCamposProtegidosAfectados() {
		guardarCampoProtegido(cBeneficiario);
		guardarCampoProtegido(GeneralParameterEnum.FECHA.getName());
		guardarCampoProtegido("OBJETO");
		guardarCampoProtegido(cResponsableAprobar);
		guardarCampoProtegido(cReferencia);
		guardarCampoProtegido(cValor);
	}

	private void guardarCampoProtegido(String nombreCampo) {
		camposProtegidosAfectados.put(nombreCampo, registro.getCampos().get(nombreCampo));
	}

	private boolean validarEdicionCamposAfectados() {
		if (!isBloquearEdicionCamposAfectados()) {
			return true;
		}

		if (campoProtegidoModificado(cBeneficiario)
				|| campoProtegidoModificado(GeneralParameterEnum.FECHA.getName())
				|| campoProtegidoModificado("OBJETO")
				|| campoProtegidoModificado(cResponsableAprobar)
				|| campoProtegidoModificado(cReferencia)
				|| campoProtegidoModificado(cValor)) {
			JsfUtil.agregarMensajeAlerta(construirMensajeEdicionAfectada());
			return false;
		}

		return true;
	}

	private boolean campoProtegidoModificado(String nombreCampo) {
		Object valorOriginal = camposProtegidosAfectados.get(nombreCampo);
		Object valorActual = registro.getCampos().get(nombreCampo);

		if (GeneralParameterEnum.FECHA.getName().equals(nombreCampo)) {
			return !normalizarFecha(valorOriginal).equals(normalizarFecha(valorActual));
		}

		if (cValor.equals(nombreCampo)) {
			return normalizarNumero(valorOriginal).compareTo(normalizarNumero(valorActual)) != 0;
		}

		return !normalizarTexto(valorOriginal).equals(normalizarTexto(valorActual));
	}

	private String normalizarTexto(Object valor) {
		return SysmanFunciones.nvl(valor, "").toString().trim();
	}

	private String normalizarFecha(Object valor) {
		if (valor == null) {
			return "";
		}

		if (valor instanceof Date) {
			return new SimpleDateFormat("dd/MM/yyyy").format((Date) valor);
		}

		String fecha = valor.toString().trim();
		return fecha.length() >= 10 ? fecha.substring(0, 10) : fecha;
	}

	private BigDecimal normalizarNumero(Object valor) {
		String numero = SysmanFunciones.nvl(valor, "0").toString().trim().replace(",", "");

		if (numero.isEmpty()) {
			numero = "0";
		}

		return new BigDecimal(numero);
	}

	private String construirMensajeEdicionAfectada() {
		String disponibilidad = normalizarTexto(registro.getCampos().get("DISPONIBILIDADNUMERO"));

		if (disponibilidad.isEmpty()) {
			disponibilidad = normalizarTexto(registro.getCampos().get(cNroSolicitudAfectada));
		}

		if (disponibilidad.isEmpty()) {
			return "No es posible editar el registro, ya tiene afectacion presupuestal.";
		}

		return "No es posible editar el registro, ya tiene afectacion presupuestal con el CDP "
				+ disponibilidad + ".";
	}

	public String getMensajeEdicionAfectada() {
		return construirMensajeEdicionAfectada();
	}

	private void validarRecursos() {

		cadenaRecursos = "";
		if (bpin) {
			cadenaRecursos = cadenaRecursos + "," + SolicitudDisponibilidadControladorEnum.BPIN.getValue();
		}
		if (cofinanciacion) {
			cadenaRecursos = cadenaRecursos + "," + SolicitudDisponibilidadControladorEnum.COFINANCIACION.getValue();
		}
		if (funcionamiento) {
			cadenaRecursos = cadenaRecursos + "," + SolicitudDisponibilidadControladorEnum.FUNCIONAMIENTO.getValue();
		}
		if (propios) {
			cadenaRecursos = cadenaRecursos + "," + SolicitudDisponibilidadControladorEnum.PROPIOS.getValue();
		}
		if (cadenaRecursos.startsWith(",")) {
			cadenaRecursos = cadenaRecursos.substring(1);
		}

	}

	/**
	 * Metodo que realiza la conversion del tipo de aprobacion 1-no almacena
	 * aprobacion 2-almacena aprobacion
	 * 
	 * @return
	 */
	public String calcularAprobacion(int opcion) {

		String valorAprobacion = SysmanFunciones.toString(registro.getCampos().get(cAprobacion));

		String salida;

		if ("A".equals(SysmanFunciones.toString(valorAprobacion))) {
			salida = "-1";
		} else if ("E".equals(SysmanFunciones.toString(valorAprobacion))) {
			salida = "2";

		} else if ("AN".equals(SysmanFunciones.toString(valorAprobacion))) {
			salida = "3";

		} else {
			salida = "0";
		}

		if (opcion == 2) {
			registro.getCampos().put(cAprobacion, salida);
		}

		return salida;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 *
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean actualizarDespues() {
		if (!registro.getLlave().isEmpty()) {
		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		
		}
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 *
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean eliminarAntes() {
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 *
	 * @return valor booleano true
	 */
	@Override
	public boolean eliminarDespues() {
		return true;
	}

	/**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     */
	public void ejecutarrcCerrar() {
		if (parametroswf != null) {
			Map<String,Object> parametros = new TreeMap<>();
			parametros.put("PR_ROWKEY",parametroswf.get("PR_ROWKEY"));
	
			Direccionador direccionador = new Direccionador();
			direccionador.setNumForm(Integer.toString(
					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo()));
	
			direccionador.setParametros(parametros);
			SessionUtil.redireccionarForma(direccionador,"35");
		} else {
			SessionUtil.redireccionar("/menu.sysman");
		}
	}
	/**
	 * Verifica que el valor del parametro sea <code>SI</code>
	 * 
	 * @return <code>true</code>: El parametro tiene asignado el valor:
	 *         <code>SI</code>
	 */
	private boolean validarValorParSI(String nombrePar) {
		String miValor = null;

		try {
			miValor = ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo, new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return validarParametro(nombrePar, miValor) && "SI".equals(miValor);
	}

	/**
	 * Agregado por jrojas, se crea metodo para llamar el EJB de parametros para
	 * trae el valor del parámetro
	 * 
	 * @param FORMATO SOLICITUD DISPONIBILIDAD PRESUPUESTAL
	 * @return retorna el valor del parametro, que es el nombre del reporte para
	 *         cuando la entidad no maneja Banco de Proyectos
	 */
	private String parametroReporte(String parametro) {

		String miValor = null;

		try {
			miValor = ejbSysmanUtils.consultarParametro(compania, parametro, modulo, new Date(), false);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return miValor;
	}

	/**
	 * Util para verificar que el parametro {@code nomPar} existe en la base de
	 * datos. De lo contrario muestra un mensaje informativo.
	 * 
	 * @param nomPar Nombre del parametro.
	 * @param valor  Valor asignado al parametro en la base de datos.
	 * @return {@code true}: si el parametro existe y tiene valor diferente a nulo.
	 */
	private boolean validarParametro(String nomPar, String valor) {
		if (SysmanFunciones.validarVariableVacio(valor)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2908").replace("#PAR#", nomPar));
			return false;
		}

		return true;
	}

	// <SET_GET_ATRIBUTOS>
	public boolean isIndManejaWF() {
		return indManejaWF;
	}

	public void setIndManejaWF(boolean indManejaWF) {
		this.indManejaWF = indManejaWF;
	}
	// </SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable especial
	 *
	 * @return especial
	 */
	public boolean isEspecial() {
		return especial;
	}

	/**
	 * @return the indiceSubPresupuesto
	 */
	public int getIndicePresupuesto() {
		return indicePresupuesto;
	}

	/**
	 * @param indiceSubPresupuesto the indiceSubPresupuesto to set
	 */
	public void setIndicePresupuesto(int indicePresupuesto) {
		this.indicePresupuesto = indicePresupuesto;
	}

	/**
	 * Asigna la variable especial
	 *
	 * @param especial Variable a asignar en especial
	 */
	public void setEspecial(boolean especial) {
		this.especial = especial;
	}

	/**
	 * @return the visibleAfectado
	 */
	public boolean isVisibleAfectado() {
		return visibleAfectado;
	}

	/**
	 * @param visibleAfectado the visibleAfectado to set
	 */
	public void setVisibleAfectado(boolean visibleAfectado) {
		this.visibleAfectado = visibleAfectado;
	}

	/**
	 * @return the bloqueoGeneral
	 */
	public boolean isBloqueoGeneral() {
		return bloqueoGeneral;
	}

	/**
	 * @param bloqueoGeneral the bloqueoGeneral to set
	 */
	public void setBloqueoGeneral(boolean bloqueoGeneral) {
		this.bloqueoGeneral = bloqueoGeneral;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna la lista listaVigencia
	 *
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}

	/**
	 * Asigna la lista listaVigencia
	 *
	 * @param listaVigencia Variable a asignar en listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}

	/**
	 * Retorna la lista listaCmbSolicitudAfectada
	 * 
	 * @return listaCmbSolicitudAfectada
	 */
	public RegistroDataModelImpl getListaCmbSolicitudAfectada() {
		return listaCmbSolicitudAfectada;
	}

	/**
	 * Asigna la lista listaCmbSolicitudAfectada
	 * 
	 * @param listaCmbSolicitudAfectada Variable a asignar en
	 *                                  listaCmbSolicitudAfectada
	 */
	public void setListaCmbSolicitudAfectada(RegistroDataModelImpl listaCmbSolicitudAfectada) {
		this.listaCmbSolicitudAfectada = listaCmbSolicitudAfectada;
	}

	/**
	 * Retorna la lista listaRubro
	 *
	 * @return listaRubro
	 */
	public RegistroDataModelImpl getListaRubro() {
		return listaRubro;
	}

	/**
	 * Asigna la lista listaRubro
	 *
	 * @param listaRubro Variable a asignar en listaRubro
	 */
	public void setListaRubro(RegistroDataModelImpl listaRubro) {
		this.listaRubro = listaRubro;
	}

	/**
	 * Retorna la lista listaRubro
	 *
	 * @return listaRubro
	 */
	public RegistroDataModelImpl getListaRubroE() {
		return listaRubroE;
	}

	/**
	 * Asigna la lista listaRubro
	 *
	 * @param listaRubro Variable a asignar en listaRubro
	 */
	public void setListaRubroE(RegistroDataModelImpl listaRubroE) {
		this.listaRubroE = listaRubroE;
	}

	/**
	 * Retorna la variable auxiliar
	 *
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * Asigna la variable auxiliar
	 *
	 * @param auxiliar Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * Retorna la lista listaTercero
	 *
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	/**
	 * Asigna la lista listaTercero
	 *
	 * @param listaTercero Variable a asignar en listaTercero
	 */
	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	/**
	 * Retorna la lista listaDependencia
	 *
	 * @return listaDependencia
	 */
	public RegistroDataModelImpl getListaDependencia() {
		return listaDependencia;
	}

	/**
	 * Asigna la lista listaDependencia
	 *
	 * @param listaDependencia Variable a asignar en listaDependencia
	 */
	public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
		this.listaDependencia = listaDependencia;
	}

	/**
	 * Retorna la lista listaModalidadSeleccion
	 *
	 * @return listaModalidadSeleccion
	 */
	public RegistroDataModelImpl getListaModalidadSeleccion() {
		return listaModalidadSeleccion;
	}

	/**
	 * Asigna la lista listamodalidad_seleccion
	 *
	 * @param listaModalidadSeleccion Variable a asignar en listaModalidadSeleccion
	 */
	public void setListaModalidadSeleccion(RegistroDataModelImpl listaModalidadSeleccion) {
		this.listaModalidadSeleccion = listaModalidadSeleccion;
	}

	/**
	 * Retorna la lista listaPresupuesto
	 *
	 * @return listaPresupuesto
	 */
	public List<Registro> getListaPresupuesto() {
		return listaPresupuesto;
	}

	/**
	 * Asigna la lista listaPresupuesto
	 *
	 * @param listaPresupuesto Variable a asignar en listaPresupuesto
	 */
	public void setListaPresupuesto(List<Registro> listaPresupuesto) {
		this.listaPresupuesto = listaPresupuesto;
	}

	/**
	 * @return the listaFuente
	 */
	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}

	/**
	 * @param listaFuente the listaFuente to set
	 */
	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
	}

	/**
	 * @return the listaFuenteE
	 */
	public RegistroDataModelImpl getListaFuenteE() {
		return listaFuenteE;
	}

	/**
	 * @param listaFuenteE the listaFuenteE to set
	 */
	public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
		this.listaFuenteE = listaFuenteE;
	}

	/**
	 * @return the listacentroCosto
	 */
	public RegistroDataModelImpl getListacentroCosto() {
		return listacentroCosto;
	}

	/**
	 * @param listacentroCosto the listacentroCosto to set
	 */
	public void setListacentroCosto(RegistroDataModelImpl listacentroCosto) {
		this.listacentroCosto = listacentroCosto;
	}

	/**
	 * @return the listacentroCostoE
	 */
	public RegistroDataModelImpl getListacentroCostoE() {
		return listacentroCostoE;
	}

	/**
	 * @param listacentroCostoE the listacentroCostoE to set
	 */
	public void setListacentroCostoE(RegistroDataModelImpl listacentroCostoE) {
		this.listacentroCostoE = listacentroCostoE;
	}

	/**
	 * @return the listareferencia
	 */
	public RegistroDataModelImpl getListareferencia() {
		return listareferencia;
	}

	/**
	 * @param listareferencia the listareferencia to set
	 */
	public void setListareferencia(RegistroDataModelImpl listareferencia) {
		this.listareferencia = listareferencia;
	}

	/**
	 * @return the listareferenciaE
	 */
	public RegistroDataModelImpl getListareferenciaE() {
		return listareferenciaE;
	}

	/**
	 * @param listareferenciaE the listareferenciaE to set
	 */
	public void setListareferenciaE(RegistroDataModelImpl listareferenciaE) {
		this.listareferenciaE = listareferenciaE;
	}

	/**
	 * Retorna el objeto registroSub
	 *
	 * @return registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}

	/**
	 * Asigna el objeto registroSub
	 *
	 * @param registroSub Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	public boolean isBloqFuente() {
		return bloqFuente;
	}

	public void setBloqFuente(boolean bloqFuente) {
		this.bloqFuente = bloqFuente;
	}
	///
	public boolean isBloqSector() {
		return bloqSector;
	}

	public void setBloqSector(boolean bloqSector) {
		this.bloqSector = bloqSector;
	}

	public boolean isBloqCentro() {
		return bloqCentro;
	}

	public void setBloqCentro(boolean bloqCentro) {
		this.bloqCentro = bloqCentro;
	}

	public boolean isBloqReferencia() {
		return bloqReferencia;
	}

	public void setBloqReferencia(boolean bloqReferencia) {
		this.bloqReferencia = bloqReferencia;
	}

	public boolean isBloqTercero() {
		return bloqTercero;
	}

	public void setBloqTercero(boolean bloqTercero) {
		this.bloqTercero = bloqTercero;
	}

	public boolean isBloqAuxiliar() {
		return bloqAuxiliar;
	}

	public void setBloqAuxiliar(boolean bloqAuxiliar) {
		this.bloqAuxiliar = bloqAuxiliar;
	}

	public boolean isBloqMovimiento() {
		return bloqMovimiento;
	}

	public void setBloqMovimiento(boolean bloqMovimiento) {
		this.bloqMovimiento = bloqMovimiento;
	}

	public boolean isBloquearNumero() {
		return bloquearNumero;
	}

	public void setBloquearNumero(boolean bloquearNumero) {
		this.bloquearNumero = bloquearNumero;
	}

	public String getParaFormatoSDis() {
		return paraFormatoSDis;
	}

	public void setParaFormatoSDis(String paraFormatoSDis) {
		this.paraFormatoSDis = paraFormatoSDis;
	}

	public String getParaManBancoP() {
		return paraManBancoP;
	}

	public void setParaManBancoP(String paraManBancoP) {
		this.paraManBancoP = paraManBancoP;
	}

	public String getParaManFuentRec() {
		return paraManFuentRec;
	}

	public void setParaManFuentRec(String paraManFuentRec) {
		this.paraManFuentRec = paraManFuentRec;
	}

	public String getParManGAstSolDis() {
		return parManGastSolDis;
	}

	public void setParManGAstSolDis(String parManGAstSolDis) {
		this.parManGastSolDis = parManGAstSolDis;
	}

	/**
	 * Retorna la lista listacmbTipoGasto
	 * 
	 * @return listacmbTipoGasto
	 */
	public RegistroDataModelImpl getListacmbTipoGasto() {
		return listacmbTipoGasto;
	}

	/**
	 * Asigna la lista listacmbTipoGasto
	 * 
	 * @param listacmbTipoGasto Variable a asignar en listacmbTipoGasto
	 */
	public void setListacmbTipoGasto(RegistroDataModelImpl listacmbTipoGasto) {
		this.listacmbTipoGasto = listacmbTipoGasto;
	}

	public boolean isMostrarModalidad() {
		return mostrarModalidad;
	}

	public void setMostrarModalidad(boolean mostrarModalidad) {
		this.mostrarModalidad = mostrarModalidad;
	}
	
	public boolean isMostrarTipoCompro() {
        return mostrarTipoCompro;
    }

    public void setMostrarTipoCompro(boolean mostrarTipoCompro) {
        this.mostrarTipoCompro = mostrarTipoCompro;
    }

	/**
	 * Retorna la lista listacmbTipoSolicitud
	 * 
	 * @return listacmbTipoSolicitud
	 */
	public RegistroDataModelImpl getListacmbTipoSolicitud() {
		return listacmbTipoSolicitud;
	}

	/**
	 * Asigna la lista listacmbTipoSolicitud
	 * 
	 * @param listacmbTipoSolicitud Variable a asignar en listacmbTipoSolicitud
	 */
	public void setListacmbTipoSolicitud(RegistroDataModelImpl listacmbTipoSolicitud) {
		this.listacmbTipoSolicitud = listacmbTipoSolicitud;
	}

	public boolean isCargarAfectados() {
		return cargarAfectados;
	}

	public void setCargarAfectados(boolean cargarAfectados) {
		this.cargarAfectados = cargarAfectados;
	}

	public String getCodigoTipoSolicitud() {
		return codigoTipoSolicitud;
	}

	public void setCodigoTipoSolicitud(String codigoTipoSolicitud) {
		this.codigoTipoSolicitud = codigoTipoSolicitud;
	}

	/**
	 * Retorna la lista listacmbTipoGasto
	 * 
	 * @return listacmbTipoGasto
	 */
	public RegistroDataModelImpl getListacmbTipoGastoE() {
		return listacmbTipoGastoE;
	}

	/**
	 * Asigna la lista listacmbTipoGasto
	 * 
	 * @param listacmbTipoGasto Variable a asignar en listacmbTipoGasto
	 */
	public void setListacmbTipoGastoE(RegistroDataModelImpl listacmbTipoGastoE) {
		this.listacmbTipoGastoE = listacmbTipoGastoE;
	}

	/**
	 * Retorna la lista listacmbProyecto
	 * 
	 * @return listacmbProyecto
	 */
	public RegistroDataModelImpl getListacmbProyecto() {
		return listacmbProyecto;
	}

	/**
	 * Asigna la lista listacmbProyecto
	 * 
	 * @param listacmbProyecto Variable a asignar en listacmbProyecto
	 */
	public void setListacmbProyecto(RegistroDataModelImpl listacmbProyecto) {
		this.listacmbProyecto = listacmbProyecto;
	}

	/**
	 * Retorna la lista listacmbProyecto
	 * 
	 * @return listacmbProyecto
	 */
	public RegistroDataModelImpl getListacmbProyectoE() {
		return listacmbProyectoE;
	}

	/**
	 * Asigna la lista listacmbProyecto
	 * 
	 * @param listacmbProyecto Variable a asignar en listacmbProyecto
	 */
	public void setListacmbProyectoE(RegistroDataModelImpl listacmbProyectoE) {
		this.listacmbProyectoE = listacmbProyectoE;
	}

	public boolean isBloqProyecto() {
		return bloqProyecto;
	}

	public void setBloqProyecto(boolean bloqProyecto) {
		this.bloqProyecto = bloqProyecto;
	}

	public String getProyectoSeleccionado() {
		return proyectoSeleccionado;
	}

	public void setProyectoSeleccionado(String proyectoSeleccionado) {
		this.proyectoSeleccionado = proyectoSeleccionado;
	}

	public String getcIdProyecto() {
		return cIdProyecto;
	}

	public String getcNombreSol() {
		return cNombreSol;
	}

	public boolean isInsertarSubFormulario() {
		return insertarSubFormulario;
	}

	public void setInsertarSubFormulario(boolean insertarSubFormulario) {
		this.insertarSubFormulario = insertarSubFormulario;
	}

	public boolean isPermitirSubFormulario() {
		return permitirSubFormulario;
	}

	public void setPermitirSubFormulario(boolean permitirSubFormulario) {
		this.permitirSubFormulario = permitirSubFormulario;
	}

	public boolean isBloqueoSolicitante() {
		return bloqueoSolicitante;
	}

	public void setBloqueoSolicitante(boolean bloqueoSolicitante) {
		this.bloqueoSolicitante = bloqueoSolicitante;
	}

	public boolean isBloqueoAprobacion() {
		return bloqueoAprobacion;
	}

	public void setBloqueoAprobacion(boolean bloqueoAprobacion) {
		this.bloqueoAprobacion = bloqueoAprobacion;
	}

	public boolean isBloqueoAfectador() {
		return bloqueoAfectador;
	}

	public void setBloqueoAfectador(boolean bloqueoAfectador) {
		this.bloqueoAfectador = bloqueoAfectador;
	}

	public boolean isBloquearEdicionCamposAfectados() {
		Object afectado = registro == null ? null : registro.getCampos().get("AFECTADO");
		return ACCION_MODIFICAR.equals(accion)
				&& (Boolean.TRUE.equals(afectado) || "true".equalsIgnoreCase(String.valueOf(afectado)));
	}

	public boolean isCargarMotivoRechazo() {
		return cargarMotivoRechazo;
	}

	public void setCargarMotivoRechazo(boolean cargarMotivoRechazo) {
		this.cargarMotivoRechazo = cargarMotivoRechazo;
	}

	public boolean isBloquearTipoGasto() {
		return bloquearTipoGasto;
	}

	public void setBloquearTipoGasto(boolean bloquearTipoGasto) {
		this.bloquearTipoGasto = bloquearTipoGasto;
	}

	public boolean isBloqueoRubro() {
		return bloqueoRubro;
	}

	public void setBloqueoRubro(boolean bloqueoRubro) {
		this.bloqueoRubro = bloqueoRubro;
	}

	public String getModificaConsecutivo() {
		return modificaConsecutivo;
	}

	public void setModificaConsecutivo(String modificaConsecutivo) {
		this.modificaConsecutivo = modificaConsecutivo;
	}

	public RegistroDataModelImpl getListaResponsableAprobar() {
		return listaResponsableAprobar;
	}

	public void setListaResponsableAprobar(RegistroDataModelImpl listaResponsableAprobar) {
		this.listaResponsableAprobar = listaResponsableAprobar;
	}

	public boolean isVisibleAprobar() {
		return visibleAprobar;
	}

	public void setVisibleAprobar(boolean visibleAprobar) {
		this.visibleAprobar = visibleAprobar;
	}

	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	public RegistroDataModelImpl getListaAuxiliarE() {
		return listaAuxiliarE;
	}

	public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
		this.listaAuxiliarE = listaAuxiliarE;
	}

	/**
	 * @return the paraRubrosCompl
	 */
	public String getParaRubrosCompl() {
		return paraRubrosCompl;
	}

	/**
	 * @param paraRubrosCompl the paraRubrosCompl to set
	 */
	public void setParaRubrosCompl(String paraRubrosCompl) {
		this.paraRubrosCompl = paraRubrosCompl;
	}

	/**
	 * @return the verImpuesto
	 */
	public boolean isVerImpuesto() {
		return verImpuesto;
	}

	/**
	 * @param verImpuesto the verImpuesto to set
	 */
	public void setVerImpuesto(boolean verImpuesto) {
		this.verImpuesto = verImpuesto;
	}

	/**
	 * @return the valorGMF
	 */
	public double getValorGMF() {
		return valorGMF;
	}

	/**
	 * @param valorGMF the valorGMF to set
	 */
	public void setValorGMF(double valorGMF) {
		this.valorGMF = valorGMF;
	}
	
	public boolean isBloqueoResponsable() {
		return bloqueoResponsable;
	}

	public void setBloqueoResponsable(boolean bloqueoResponsable) {
		this.bloqueoResponsable = bloqueoResponsable;
	}

	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	public void setbloqSector(boolean bloqSector)
	{
		this.bloqSector = bloqSector;
	}

	public boolean getbloqSector()
	{
		return bloqSector;
	}

	public void setbloqPrograma(boolean bloqPrograma)
	{
		this.bloqPrograma = bloqPrograma;
	}
	public boolean getbloqPrograma()
	{
		return bloqPrograma;
	}

	public void setbloqSupPrograma(boolean bloqSupPrograma)
	{
		this.bloqSupPrograma = bloqSupPrograma;
	}
	public boolean getbloqSupPrograma()
	{
		return bloqSupPrograma;
	}

	public void setbloqCodigoProducto(boolean bloqCodigoProducto)
	{
		this.bloqCodigoProducto = bloqCodigoProducto;
	}
	public boolean getbloqCodigoProducto()
	{
		return bloqCodigoProducto;
	}


	public void setbloqCodigoBPIN(boolean bloqCodigoBPIN)
	{
		this.bloqCodigoBPIN = bloqCodigoBPIN;
	}
	public boolean getbloqCodigoBPIN()
	{
		return bloqCodigoBPIN;
	}

	public void setbloqCodigoCCPET(boolean bloqCodigoCCPET)
	{
		this.bloqCodigoCCPET = bloqCodigoCCPET;
	}
	public boolean getbloqCodigoCCPET()
	{
		return bloqCodigoCCPET;
	}

	public void setbloqCodigoCPCDANE(boolean bloqCodigoCPCDANE)
	{
		this.bloqCodigoCPCDANE = bloqCodigoCPCDANE;
	}
	public boolean getbloqCodigoCPCDANE()
	{
		return bloqCodigoCPCDANE;
	}


	public void setbloqCodigoUnidEje(boolean bloqCodigoUnidEje)
	{
		this.bloqCodigoUnidEje = bloqCodigoUnidEje;
	}
	public boolean getbloqCodigoUnidEje()
	{
		return bloqCodigoUnidEje;
	}

	public void setbloqCodigoFuente(boolean bloqCodigoFuente)
	{
		this.bloqCodigoFuente = bloqCodigoFuente;
	}
	public boolean getbloqCodigoFuente()
	{
		return bloqCodigoFuente;
	}

	public void setbloqCodigoCCPETRega(boolean bloqCodigoCCPETRega)
	{
		this.bloqCodigoCCPETRega = bloqCodigoCCPETRega;
	}
	public boolean getbloqCodigoCCPETRega()
	{
		return bloqCodigoCCPETRega;
	}
	
	/**
	 * Retorna la variable sector
	 * 
	 * @return  sector
	 */
	public String getSector() {
		return sector;
	}
	
	/**
	 * Asigna la variable  sector
	 * 
	 * @param  sector
	 * Variable a asignar en  sector
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}

	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSector() {
		return listaSector;
	}
	
	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en  listaSector
	 */
	public void setListaSector(RegistroDataModelImpl listaSector) {
		this.listaSector = listaSector;
	}

	/**
	 * Retorna la lista listaPrograma
	 * 
	 * @return listaPrograma
	 */
	public RegistroDataModelImpl getListaPrograma() {
		return listaPrograma;
	}
	
	/**
	 * Asigna la lista listaPrograma
	 * 
	 * @param listaPrograma
	 * Variable a asignar en  listaPrograma
	 */
	public void setListaPrograma(RegistroDataModelImpl listaPrograma) {
		this.listaPrograma = listaPrograma;
	}
	
	/**
	 * Retorna la lista listaSupPrograma
	 * 
	 * @return listaSupPrograma
	 */
	public RegistroDataModelImpl getListaSupPrograma() {
		return listaSupPrograma;
	}
	
	/**
	 * Asigna la lista listaSupPrograma
	 * 
	 * @param listaSupPrograma
	 * Variable a asignar en  listaSupPrograma
	 */
	public void setListaSupPrograma(RegistroDataModelImpl listaSupPrograma) {
		this.listaSupPrograma = listaSupPrograma;
	}
	
	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProducto() {
		return listaCodigoProducto;
	}
	
	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en  listaCodigoProducto
	 */
	public void setListaCodigoProducto(RegistroDataModelImpl listaCodigoProducto) {
		this.listaCodigoProducto = listaCodigoProducto;
	}
	
	/**
	 * Retorna la lista listaCodigoBPIN
	 * 
	 * @return listaCodigoBPIN
	 */
	public RegistroDataModelImpl getListaCodigoBPIN() {
		return listaCodigoBPIN;
	}
	
	/**
	 * Asigna la lista listaCodigoBPIN
	 * 
	 * @param listaCodigoBPIN
	 * Variable a asignar en  listaCodigoBPIN
	 */
	public void setListaCodigoBPIN(RegistroDataModelImpl listaCodigoBPIN) {
		this.listaCodigoBPIN = listaCodigoBPIN;
	}
	
	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigoCCPET() {
		return listaCodigoCCPET;
	}
	
	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigoCCPET(RegistroDataModelImpl listaCodigoCCPET) {
		this.listaCodigoCCPET = listaCodigoCCPET;
	}

	/**
	 * Retorna la lista listaCodigoCPCDANE
	 * 
	 * @return listaCodigoCPCDANE
	 */
	public RegistroDataModelImpl getListaCodigoCPCDANE() {
		return listaCodigoCPCDANE;
	}
	/**
	 * Asigna la lista listaCodigoCPCDANE
	 * 
	 * @param listaCodigoCPCDANE
	 * Variable a asignar en  listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANE(RegistroDataModelImpl listaCodigoCPCDANE) {
		this.listaCodigoCPCDANE = listaCodigoCPCDANE;
	}
	/**
	 * Retorna la lista listaCodigoUnidEje
	 * 
	 * @return listaCodigoUnidEje
	 */
	public RegistroDataModelImpl getListaCodigoUnidEje() {
		return listaCodigoUnidEje;
	}
	/**
	 * Asigna la lista listaCodigoUnidEje
	 * 
	 * @param listaCodigoUnidEje
	 * Variable a asignar en  listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEje(RegistroDataModelImpl listaCodigoUnidEje) {
		this.listaCodigoUnidEje = listaCodigoUnidEje;
	}
	/**
	 * Retorna la lista listaCodigoFuente
	 * 
	 * @return listaCodigoFuente
	 */
	public RegistroDataModelImpl getListaCodigoFuente() {
		return listaCodigoFuente;
	}
	/**
	 * Asigna la lista listaCodigoFuente
	 * 
	 * @param listaCodigoFuente
	 * Variable a asignar en  listaCodigoFuente
	 */
	public void setListaCodigoFuente(RegistroDataModelImpl listaCodigoFuente) {
		this.listaCodigoFuente = listaCodigoFuente;
	}
	/**
	 * Retorna la lista listaCodigoCCPETRega
	 * 
	 * @return listaCodigoCCPETRega
	 */
	public RegistroDataModelImpl getListaCodigoCCPETRega() {
		return listaCodigoCCPETRega;
	}
	/**
	 * Asigna la lista listaCodigoCCPETRega
	 * 
	 * @param listaCodigoCCPETRega
	 * Variable a asignar en  listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRega(RegistroDataModelImpl listaCodigoCCPETRega) {
		this.listaCodigoCCPETRega = listaCodigoCCPETRega;
	}
	
	public boolean isBloqPoliticaPublica() {
		return bloqPoliticaPublica;
	}

	public void setBloqPoliticaPublica(boolean bloqPoliticaPublica) {
		this.bloqPoliticaPublica = bloqPoliticaPublica;
	}

	public boolean isBloqDetalleSectorial() {
		return bloqDetalleSectorial;
	}

	public void setBloqDetalleSectorial(boolean bloqDetalleSectorial) {
		this.bloqDetalleSectorial = bloqDetalleSectorial;
	}
	public RegistroDataModelImpl getListaPoliticaPublica() {
		return listaPoliticaPublica;
	}

	public void setListaPoliticaPublica(RegistroDataModelImpl listaPoliticaPublica) {
		this.listaPoliticaPublica = listaPoliticaPublica;
	}

	public RegistroDataModelImpl getListaPoliticaPublicaE() {
		return listaPoliticaPublicaE;
	}

	public void setListaPoliticaPublicaE(RegistroDataModelImpl listaPoliticaPublicaE) {
		this.listaPoliticaPublicaE = listaPoliticaPublicaE;
	}

	public RegistroDataModelImpl getListaDetalleSectorial() {
		return listaDetalleSectorial;
	}

	public void setListaDetalleSectorial(RegistroDataModelImpl listaDetalleSectorial) {
		this.listaDetalleSectorial = listaDetalleSectorial;
	}

	public RegistroDataModelImpl getListaDetalleSectorialE() {
		return listaDetalleSectorialE;
	}

	public void setListaDetalleSectorialE(RegistroDataModelImpl listaDetalleSectorialE) {
		this.listaDetalleSectorialE = listaDetalleSectorialE;
	}
	/**
	 * Retorna la lista listaProductoCUIPO
	 * 
	 * @return listaProductoCUIPO
	 */
	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSectorE() {
		return listaSectorE;
	}
	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en  listaSector
	 */
	public void setListaSectorE(RegistroDataModelImpl listaSectorE) {
		this.listaSectorE = listaSectorE;
	}

	/**
	 * Retorna la lista listaPrograma
	 * 
	 * @return listaPrograma
	 */
	public RegistroDataModelImpl getListaProgramaE() {
		return listaProgramaE;
	}
	/**
	 * Asigna la lista listaPrograma
	 * 
	 * @param listaPrograma
	 * Variable a asignar en  listaPrograma
	 */
	public void setListaProgramaE(RegistroDataModelImpl listaProgramaE) {
		this.listaProgramaE = listaProgramaE;
	}

	/**
	 * Retorna la lista listaSupPrograma
	 * 
	 * @return listaSupPrograma
	 */
	public RegistroDataModelImpl getListaSupProgramaE() {
		return listaSupProgramaE;
	}
	/**
	 * Asigna la lista listaSupPrograma
	 * 
	 * @param listaSupPrograma
	 * Variable a asignar en  listaSupPrograma
	 */
	public void setListaSupProgramaE(RegistroDataModelImpl listaSupProgramaE) {
		this.listaSupProgramaE = listaSupProgramaE;
	}

	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProductoE() {
		return listaCodigoProductoE;
	}
	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en  listaCodigoProducto
	 */
	public void setListaCodigoProductoE(RegistroDataModelImpl listaCodigoProductoE) {
		this.listaCodigoProductoE = listaCodigoProductoE;
	}

	/**
	 * Retorna la lista listaCodigoBPIN
	 * 
	 * @return listaCodigoBPIN
	 */
	public RegistroDataModelImpl getListaCodigoBPINE() {
		return listaCodigoBPINE;
	}
	/**
	 * Asigna la lista listaCodigoBPIN
	 * 
	 * @param listaCodigoBPIN
	 * Variable a asignar en  listaCodigoBPIN
	 */
	public void setListaCodigoBPINE(RegistroDataModelImpl listaCodigoBPINE) {
		this.listaCodigoBPINE = listaCodigoBPINE;
	}

	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigoCCPETE() {
		return listaCodigoCCPETE;
	}

	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en listaCodigoCCPET

	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigoCCPETE(RegistroDataModelImpl listaCodigoCCPETE) {
		this.listaCodigoCCPETE = listaCodigoCCPETE;
	}

	/**
	 * Retorna la lista listaCodigoCPCDANE
	 * 
	 * @return listaCodigoCPCDANE
	 */
	public RegistroDataModelImpl getListaCodigoCPCDANEE() {
		return listaCodigoCPCDANEE;
	}
	/**
	 * Asigna la lista listaCodigoCPCDANE
	 * 
	 * @param listaCodigoCPCDANE
	 * Variable a asignar en  listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANEE(RegistroDataModelImpl listaCodigoCPCDANEE) {
		this.listaCodigoCPCDANEE = listaCodigoCPCDANEE;
	}

	/**
	 * Retorna la lista listaCodigoUnidEje
	 * 
	 * @return listaCodigoUnidEje
	 */
	public RegistroDataModelImpl getListaCodigoUnidEjeE() {
		return listaCodigoUnidEjeE;
	}
	/**
	 * Asigna la lista listaCodigoUnidEje
	 * 
	 * @param listaCodigoUnidEje
	 * Variable a asignar en  listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEjeE(RegistroDataModelImpl listaCodigoUnidEjeE) {
		this.listaCodigoUnidEjeE = listaCodigoUnidEjeE;
	}

	/**
	 * Retorna la lista listaCodigoFuente
	 * 
	 * @return listaCodigoFuente
	 */
	public RegistroDataModelImpl getListaCodigoFuenteE() {
		return listaCodigoFuenteE;
	}
	/**
	 * Asigna la lista listaCodigoFuente
	 * 
	 * @param listaCodigoFuente
	 * Variable a asignar en  listaCodigoFuente
	 */
	public void setListaCodigoFuenteE(RegistroDataModelImpl listaCodigoFuenteE) {
		this.listaCodigoFuenteE = listaCodigoFuenteE;
	}

	/**
	 * Retorna la lista listaCodigoCCPETRega
	 * 
	 * @return listaCodigoCCPETRega
	 */
	public RegistroDataModelImpl getListaCodigoCCPETRegaE() {
		return listaCodigoCCPETRegaE;
	}
	/**
	 * Asigna la lista listaCodigoCCPETRega
	 * 
	 * @param listaCodigoCCPETRega
	 * Variable a asignar en  listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRegaE(RegistroDataModelImpl listaCodigoCCPETRegaE) {
		this.listaCodigoCCPETRegaE = listaCodigoCCPETRegaE;
	}
	// </SET_GET_ADICIONALES>

	/**
	 * @return the listaBeneficiario
	 */
	public RegistroDataModelImpl getListaBeneficiario() {
		return listaBeneficiario;
	}

	/**
	 * @param listaBeneficiario the listaBeneficiario to set
	 */
	public void setListaBeneficiario(RegistroDataModelImpl listaBeneficiario) {
		this.listaBeneficiario = listaBeneficiario;
	}

	/**
	 * @return the calculaGravamen
	 */
	public boolean isCalculaGravamen() {
		return calculaGravamen;
	}

	/**
	 * @param calculaGravamen the calculaGravamen to set
	 */
	public void setCalculaGravamen(boolean calculaGravamen) {
		this.calculaGravamen = calculaGravamen;
	}
	/**
	 * @return the valorBase
	 */


	public Double getvalorBase() {
		return valorBase;
	}

	/**

	 * @param valorBase the valorBase to set
	 */


	public void setvalorBase(Double valorBase) {
		this.valorBase = valorBase;
	}

	/**
	 * @return the impuesto
	 */
	public Double getImpuesto() {
		return impuesto;
	}

	/**
	 * @param impuesto the impuesto to set
	 */
	public void setImpuesto(Double impuesto) {
		this.impuesto = impuesto;
	}

	/**
	 * @return the manejaTercero
	 */
	public boolean isManejaTercero() {
		return manejaTercero;
	}

	/**
	 * @param manejaTercero the manejaTercero to set
	 */
	public void setManejaTercero(boolean manejaTercero) {
		this.manejaTercero = manejaTercero;
	}

	/**
	 * @return the listareferenciaGeneral
	 */
	public RegistroDataModelImpl getListareferenciaGeneral() {
		return listareferenciaGeneral;
	}

	/**
	 * @param listareferenciaGeneral the listareferenciaGeneral to set
	 */
	public void setListareferenciaGeneral(RegistroDataModelImpl listareferenciaGeneral) {
		this.listareferenciaGeneral = listareferenciaGeneral;
	}

	/**
	 * @return the cNombreReferencia
	 */
	public String getcNombreReferencia() {
		return cNombreReferencia;
	}

	/**
	 * @param cNombreReferencia the cNombreReferencia to set
	 */
	public void setcNombreReferencia(String cNombreReferencia) {
		this.cNombreReferencia = cNombreReferencia;
	}

	/**
	 * @return the cNombreBeneficiario
	 */
	public String getcNombreBeneficiario() {
		return cNombreBeneficiario;
	}

	/**
	 * @param cNombreBeneficiario the cNombreBeneficiario to set
	 */
	public void setcNombreBeneficiario(String cNombreBeneficiario) {
		this.cNombreBeneficiario = cNombreBeneficiario;
	}

	/**
	 * @return the bpin
	 */
	public boolean isBpin() {
		return bpin;
	}

	/**
	 * @param bpin the bpin to set
	 */
	public void setBpin(boolean bpin) {
		this.bpin = bpin;
	}

	/**
	 * @return the cofinanciacion
	 */
	public boolean isCofinanciacion() {
		return cofinanciacion;
	}

	/**
	 * @param cofinanciacion the cofinanciacion to set
	 */
	public void setCofinanciacion(boolean cofinanciacion) {
		this.cofinanciacion = cofinanciacion;
	}

	/**
	 * @return the funcionamiento
	 */
	public boolean isFuncionamiento() {
		return funcionamiento;
	}

	/**
	 * @param funcionamiento the funcionamiento to set
	 */
	public void setFuncionamiento(boolean funcionamiento) {
		this.funcionamiento = funcionamiento;
	}

	/**
	 * @return the propios
	 */
	public boolean isPropios() {
		return propios;
	}

	/**
	 * @param propios the propios to set
	 */
	public void setPropios(boolean propios) {
		this.propios = propios;
	}

	/**
	 * @return the terceroEtiqueta
	 */
	public String getTerceroEtiqueta() {
		return terceroEtiqueta;
	}

	/**
	 * @param terceroEtiqueta the terceroEtiqueta to set
	 */
	public void setTerceroEtiqueta(String terceroEtiqueta) {
		this.terceroEtiqueta = terceroEtiqueta;
	}

	/**
	 * @return the lbResponsable
	 */
	public String getLbResponsable() {
		return lbResponsable;
	}

	/**
	 * @param lbResponsable the lbResponsable to set
	 */
	public void setLbResponsable(String lbResponsable) {
		this.lbResponsable = lbResponsable;
	}
	/**
	 * @return the listaListaPlantillas
	 */
	public RegistroDataModelImpl getListaListaPlantillas() {
		return listaListaPlantillas;
	}

	/**
	 * @param listaListaPlantillas the listaListaPlantillas to set
	 */
	public void setListaListaPlantillas(RegistroDataModelImpl listaListaPlantillas) {
		this.listaListaPlantillas = listaListaPlantillas;
	}
	
	/**
     * Retorna la lista listatipoCompromiso
     * 
     * @return listatipoCompromiso
     */
    public RegistroDataModelImpl getListatipoCompromiso() {
        return listatipoCompromiso;
    }
    /**
     * Asigna la lista listatipoCompromiso
     * 
     * @param listatipoCompromiso
     * Variable a asignar en  listatipoCompromiso
     */
    public void setListatipoCompromiso(RegistroDataModelImpl listatipoCompromiso) {
        this.listatipoCompromiso = listatipoCompromiso;
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
	 * @return the visibleListaPlantillas
	 */
	public Boolean getVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(Boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public List<Registro> getListaanio() {
		return listaanio;
	}

	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}
	
	
}
