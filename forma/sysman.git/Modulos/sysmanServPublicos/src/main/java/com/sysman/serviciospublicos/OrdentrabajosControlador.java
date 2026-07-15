package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCuatroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.OrdentrabajosControladorEnum;
import com.sysman.serviciospublicos.enums.OrdentrabajosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 15/09/2016
 *
 * @author ybecerra
 * @version 2, 21/06/2017, proceso de Refactoring y modificaciones
 * segun especificaciones de sonar
 */
@ManagedBean
@ViewScoped
public class OrdentrabajosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante que especifica el codigo de la compania
     */
    private final String compania;
    /**
     * Constante que especifica el codigo del modulo
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que especifica el nombre del Aforador en la Orden de
     * Trabajo
     */
    private String nombreAforador;
    /**
     * Atributo que especifica el anio segun la fecha de Solicitud en
     * Orden de Trabajo
     */
    private String anio;
    /**
     * Atributo que especifica el periodo segun la fecha de Solicitud
     * en Orden de Trabajo
     */
    private String periodo;
    /**
     * Atributo que especifica el mes segun la fecha de Solicitud en
     * Orden de Trabajo
     */
    private String mes;
    /**
     * Atributo que especifica el codigo interno del usuario
     * relacionado a la Orden de trabajo
     */
    private String codigoInterno;
    /**
     * Atributo que especifica el formato en que se va a generar la
     * plantilla WORD
     */
    private String formato;
    /**
     * Atributo que especifica la clase de Orden de trabajo. Viene
     * como parametro segun la opcion de menu en que se abra el
     * formulario
     */
    private String claseDoc;
    /**
     * Atributo que especifica el titulo del formulario. Varia segun
     * la opcion de menu en que se abra el formulario
     */
    private String titulo;
    /**
     * Atributo que especifica el titulo de la grilla del formulario.
     * Varia segun la opcion de menu en que se abra el formulario
     */
    private String tituloGrilla;
    /**
     * StreamedContent que se usa para la descarga de reportes
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que especifica el registro que se va a editar en el
     * subformulario Subdordenservicio
     */
    private int indiceSubdordenservicio;
    /**
     * Atributo que especifica la fecha de la plantilla WORD
     * seleccionada
     */
    private String fechaFormato;
    /**
     * Atributo que especifica el nombre de la plantilla WORD
     * seleccionada
     */
    private String nombreWord;
    /**
     * Atributo que especifica la plantilla WORD seleccionada
     */
    private String plantillaWord;
    /**
     * Variable donde se almacena la direccion tecnica del usuario al
     * seleccionar un codigoRuta
     */
    private String dirTecnica;
    /**
     * Variable donde se almacena el telefono del usuario al
     * seleccionar un codigoRuta
     */
    private String telefono;
    /**
     * Variable donde se almacena el primer apellido del usuario al
     * seleccionar un codigoRuta
     */
    private String primerApellido;
    /**
     * Variable donde se almacena el segundo apellido del usuario al
     * seleccionar un codigoRuta
     */
    private String segundoApellido;
    /**
     * Variable donde se almacenan los nombres del usuario al
     * seleccionar un codigoRuta
     */
    private String nombres;
    /**
     * Atributo donde se especifica el ultimo aforador de la Orden de
     * Trabajo. Se toma como atributo porque no esta en la tabla
     * SP_ORDENTRABAJO
     */
    private String ultimoAforador;
    /**
     * Variable donde se almacenan el ultimo aforador del usuario al
     * seleccionar un codigoRuta
     */
    private String ultimoAforadorAux;
    /**
     * Variable donde se almacenan los nombres del usuario al
     * seleccionar un codigoRuta
     */
    private String codigoInternoAux;
    /**
     * Variable donde se almacenan la factura del usuario al
     * seleccionar un codigoRuta
     */
    private String factura;
    /**
     * Variable donde se almacenan el valor total actual de la factura
     * del usuario al seleccionar un codigoRuta
     */
    private String totFacturaPerActual;
    /**
     * Variable donde se almacenan el periodo del usuario al
     * seleccionar un codigoRuta
     */
    private String periodoUsuario;
    /**
     * Variable donde se almacenan el anio del usuario al seleccionar
     * un codigoRuta
     */
    private String anoUsuario;
    /**
     * Variable donde se almacenan el codigoRuta del usuario al
     * seleccionar un codigoRuta
     */
    private String codigoRuta;
    /**
     * Atributo donde se especifica el titulo que debe tomar la
     * etuiqueta para el campo orden
     */
    private String ordenEtiqueta;
    /**
     * Atributo donde se especifica si tiene acueducto o no en el
     * dialogo que se abre al oprimir el boton de servicion no
     * reclamados
     */
    private String acueducto;
    /**
     * Atributo donde se especifica si tiene alcantarillado o no en el
     * dialogo que se abre al oprimir el boton de servicion no
     * reclamados
     */
    private String alcantarillado;
    /**
     * Atributo donde se especifica si tiene aseo o no en el dialogo
     * que se abre al oprimir el boton de servicion no reclamados
     */
    private String aseo;
    /**
     * Variable donde se almacenan la clase Problema al seleccionar un
     * claseProblema
     */
    private String claseProblemaAux;
    /**
     * Variable donde se almacenan valor anterior al seleccionar un
     * concepto
     */
    private String valorAnteriorAux;
    /**
     * Variable donde se almacena el nombre del documento al
     * seleccionar documento
     */
    private String nombreDocumentoAux;
    /**
     * Variable donde se almacena el nombre de la clase de problema al
     * seleccionar una clase de problema
     */
    private String nombreClaseProblemaAux;
    /**
     * Variable donde se almacena el nombre del problema al
     * seleccionar un problema
     */
    private String nombreProblemaAux;
    /**
     * Variable donde se almacena la descripcion del problema al
     * seleccionar un problema
     */
    private String txtSolucionAux;
    /**
     * Variable donde se almacena el indFavor del problema al
     * seleccionar un problema
     */
    private String indFavoroAux;
    /**
     * Atributo que especifica si el boton abrirOrdenTrabajo esta no
     * disponible
     */
    private boolean abrirOrdenTrabajoDisable;
    /**
     * Atributo que especifica si el dialogo de confirmacion al
     * seleccionar usuario esta visible
     */
    private boolean cuadroUsuarioVisible;
    /**
     * Atributo que especifica si el dialogo que abre el boton
     * ServicionNoReclamados esta visible
     */
    private boolean cuadroServiciosVisible;
    /**
     * Atributo que especifica si el campo ciclo esta bloqueado
     */
    private boolean codigoCicloBloqueado;
    /**
     * Atributo que especifica si el boton generar esta no disponible
     */
    private boolean cmdGenerarDisable;
    /**
     * Atributo que especifica si el campo terreno esta visible
     */
    private boolean terrenoVisible;
    /**
     * Atributo que especifica si el combo cmbNoRec esta visible
     */
    private boolean cmbNoRecVisible;
    /**
     * Atributo que especifica la visibilidad de campos cuando la
     * clase es PQR
     */
    private boolean abrirVisibles;
    /**
     * Atributo que especifica la visibilidad de campos cuando la
     * clase no es PQR
     */
    private boolean abrirNoVisibles;
    /**
     * Atributo que especifica la visibilidad del boton Actualiza PQR
     */
    private boolean pqrVisible;
    /**
     * Atributo que especifica la visibilidad del campo fechaSitio
     */
    private boolean fechaSitioVisible;
    /**
     * Atributo que especifica si el campo txtSolucion esta bloqueado
     */
    private boolean txtSolucionBloqueado;
    /**
     * Variable auxiliar que se asigna al seleccionar clase problema
     * para validar el problema
     */
    private boolean suiUnoAux;
    /**
     * Variable que identifica si la orden de trabajo se esta abriendo
     * desde una PQR
     */
    private boolean varVolver;

    /**
     * Constante que representa la cadena TRUE
     */
    private final String trueCons;
    /**
     * Constante que representa la cadena FALSE
     */
    private final String falseCons;
    /**
     * Constante que representa la cadena AFORADOR para referenciar el
     * campo del registro
     */
    private final String aforadorCons;
    /**
     * Constante que representa la cadena CICLO para referenciar el
     * campo del registro
     */
    private final String cicloCons;
    /**
     * Constante que representa la cadena CODIGORUTA para referenciar
     * el campo del registro
     */
    private final String codigoRutaCons;
    /**
     * Constante que representa la cadena ANOUSUARIO para referenciar
     * el campo del registro
     */
    private final String anoUsuarioCons;
    /**
     * Constante que representa la cadena CLASEPROBLEMA para
     * referenciar el campo del registro
     */
    private final String claseProblemaCons;
    /**
     * Constante que representa la cadena CLASEDOC para referenciar el
     * campo del registro
     */
    private final String claseDocCons;
    /**
     * Constante que representa la cadena CODIGOINTERNO para
     * referenciar el campo del registro
     */
    private final String codigoInternoCons;
    /**
     * Constante que representa la cadena FECHASOLICITUD para
     * referenciar el campo del registro
     */
    private final String fechaSolucitudCons;
    /**
     * Constante que representa la cadena TIPOREQUERIMIENTO para
     * referenciar el campo del registro
     */
    private final String tipoRequerimientoCons;
    /**
     * Constante que representa la cadena CODIGO para referenciar el
     * campo del registro
     */
    private final String codigoCons;
    /**
     * Constante que representa la cadena DOCUMENTONOMBRE para
     * referenciar el campo del registro
     */
    private final String documentoNombreCons;
    /**
     * Constante que representa la cadena NOMBRE para referenciar el
     * campo del registro
     */
    private final String nombreCons;
    /**
     * Constante que representa la cadena NUMORDEN para referenciar el
     * campo del registro
     */
    private final String numOrdenCons;
    /**
     * Constante que representa la cadena NUMORDENT para referenciar
     * el campo del registro
     */
    private final String numOrdenTCons;
    /**
     * Constante que representa la cadena ORDENTRABAJO para
     * referenciar el campo del registro
     */
    private final String ordenTrabajoCons;
    /**
     * Constante que representa la cadena PERIODOUSUARIO para
     * referenciar el campo del registro
     */
    private final String periodoUsuarioCons;
    /**
     * Constante que representa la cadena PROBLEMA para referenciar el
     * campo del registro
     */
    private final String problemaCons;
    /**
     * Constante que representa la cadena PROBLEMANOMBRE para
     * referenciar el campo del registro
     */
    private final String problemaNombreCons;
    /**
     * Constante que representa la cadena SERVNORECLAMADO para
     * referenciar el campo del registro
     */
    private final String servNoReclamadoCons;
    /**
     * Constante que representa la cadena NUMERO para referenciar el
     * campo del registro
     */
    private final String numeroCons;
    /**
     * Constante que representa la cadena CLASENOMBRE para referenciar
     * el campo del registro
     */
    private final String claseNombreCons;
    /**
     * Constante que representa la cadena FECHANOTIFICACION para
     * referenciar el campo del registro
     */
    private final String fechaNotificacionCons;
    /**
     * Constante que representa la cadena FECHASOLUCION para
     * referenciar el campo del registro
     */
    private final String fechaSolucionCons;
    /**
     * Constante que representa la cadena TIPONOTIFICACION para
     * referenciar el campo del registro
     */
    private final String tipoNotificacionCons;
    /**
     * Constante que representa la cadena FECHATRASLADO_SSPD para
     * referenciar el campo del registro
     */
    private final String fechatrasladosspCons;
    /**
     * Constante que representa la cadena SOLUCION para referenciar el
     * campo del registro
     */
    private final String solucionCons;
    /**
     * Constante que representa la cadena INDFAVOREMPRESA para
     * referenciar el campo del registro
     */
    private final String indFavorEmpresaCons;
    /**
     * Constante que representa el nombre del parametro PQR DESPUES DE
     * RES 20101300048765 DEL 14-12-2010
     */
    private final String parPQRDespues;
    /**
     * Constante que representa la cadena PERIODO para referenciar el
     * campo del registro
     */
    private final String periodoCons;
    /**
     * Constante definida para almacenar la cadena KEY_CLASEDOC
     */
    private final String keyClaseDocCons;
    /**
     * Constante definida para almacenar la cadena "CARTERA"
     */
    private final String carteraCons;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * ciclo
     */
    private List<Registro> listaCiclo;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * tipoRespuesta
     */
    private List<Registro> listaTipoRespuesta;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Formateado
     */
    private RegistroDataModelImpl listaFormateado;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * claseProblema al crear un nuevo registro
     */
    private RegistroDataModelImpl listaClaseProblema;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * claseProblema al editar un registro
     */
    private RegistroDataModelImpl listaClaseProblemaE;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Problema al crear un nuevo registro
     */
    private RegistroDataModelImpl listaProblema;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Problema al editar un registro
     */
    private RegistroDataModelImpl listaProblemaE;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Concepto al crear un nuevo registro
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Concepto al editar un registro
     */
    private RegistroDataModelImpl listaConceptoE;
    /**
     * Atributo que se usa para almacenar valores al seleccionar en un
     * combo grande
     */
    private String auxiliar;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Documento al crear un nuevo registro
     */
    private RegistroDataModelImpl listaDocumento;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Documento al editar un registro
     */
    private RegistroDataModelImpl listaDocumentoE;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * ProblemasSe al crear un nuevo registro
     */
    private RegistroDataModelImpl listaProblemasSe;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * ProblemasSe al editar un registro
     */
    private RegistroDataModelImpl listaProblemasSeE;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * CodigoRuta
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Aforador
     */
    private RegistroDataModelImpl listaAforador;
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * DependenciaEnv
     */
    private RegistroDataModelImpl listaDependenciaEnv;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Atributo que especifica la lista que se va a cargar en el
     * subformulario Subdordenservicio
     */
    private List<Registro> listaSubdordenservicio;
    /**
     * Atributo que especifica la lista que se va a cargar en el
     * subformulario Subordendocpresentado
     */
    private List<Registro> listaSubordendocpresentado;
    /**
     * Atributo que especifica la lista que se va a cargar en el
     * subformulario Seguimientosub
     */
    private List<Registro> listaSeguimientosub;
    /**
     * Atributo que especifica la lista que se va a cargar en el
     * subformulario Ordentrabajonovedades
     */
    private List<Registro> listaOrdentrabajonovedades;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo que especifica el registro para el subformulario
     * SubDOrdenServicio
     */
    private Registro registroSubSubDOrdenServicio;
    /**
     * Atributo que especifica el registro para el subformulario
     * SubOrdenDocPresentado
     */
    private Registro registroSubSubOrdenDocPresentado;
    /**
     * Atributo que especifica el registro para el subformulario
     * SeguimientoSub
     */
    private Registro registroSubSeguimientoSub;
    /**
     * Atributo que especifica el registro para el subformulario
     * OrdenTrabajoNovedades
     */
    private Registro registroSubOrdenTrabajoNovedades;

    /**
     * Almacena la llave de la Orden de Trabajo, se envia al
     * formulario factura , para cuando se le de clic en el boton
     * volver del formulario retorne al registro que abrio el
     * formulario
     */
    private Map<String, Object> ridOrdenTrabajo;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    @EJB
    private EjbServiciosPublicosCuatroRemote ejbServiciosPublicosCuatro;
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServPublicosOcho;

    // </DECLARAR_ADICIONALES>

    /**
     * Constructor de la clase OrdentrabajosControlador, en este se
     * inicializan las constantes y se verifica si se abre una PQR o
     * ORD
     */
    @SuppressWarnings("unchecked")
    public OrdentrabajosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        carteraCons = OrdentrabajosControladorEnum.PARAM6.getValue();
        trueCons = OrdentrabajosControladorEnum.PARAM26.getValue();
        falseCons = OrdentrabajosControladorEnum.PARAM25.getValue();
        aforadorCons = OrdentrabajosControladorEnum.PARAM24.getValue();
        cicloCons = GeneralParameterEnum.CICLO.getName();
        codigoRutaCons = GeneralParameterEnum.CODIGORUTA.getName();
        anoUsuarioCons = OrdentrabajosControladorEnum.PARAM23.getValue();
        claseProblemaCons = OrdentrabajosControladorEnum.PARAM4.getValue();
        claseDocCons = OrdentrabajosControladorEnum.PARAM0.getValue();
        keyClaseDocCons = OrdentrabajosControladorEnum.PARAM5.getValue();
        codigoInternoCons = OrdentrabajosControladorEnum.PARAM7.getValue();
        fechaSolucitudCons = OrdentrabajosControladorEnum.PARAM8.getValue();
        tipoRequerimientoCons = OrdentrabajosControladorEnum.PARAM3.getValue();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        documentoNombreCons = OrdentrabajosControladorEnum.PARAM9.getValue();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        numOrdenCons = OrdentrabajosControladorEnum.PARAM2.getValue();
        numOrdenTCons = OrdentrabajosControladorEnum.PARAM10.getValue();
        ordenTrabajoCons = OrdentrabajosControladorEnum.PARAM1.getValue();
        periodoUsuarioCons = OrdentrabajosControladorEnum.PARAM11.getValue();
        problemaCons = OrdentrabajosControladorEnum.PARAM12.getValue();
        problemaNombreCons = OrdentrabajosControladorEnum.PARAM13.getValue();
        servNoReclamadoCons = OrdentrabajosControladorEnum.PARAM14.getValue();
        numeroCons = GeneralParameterEnum.NUMERO.getName();
        claseNombreCons = OrdentrabajosControladorEnum.PARAM15.getValue();
        fechaNotificacionCons = OrdentrabajosControladorEnum.PARAM16.getValue();
        fechaSolucionCons = OrdentrabajosControladorEnum.PARAM17.getValue();
        tipoNotificacionCons = OrdentrabajosControladorEnum.PARAM18.getValue();
        fechatrasladosspCons = OrdentrabajosControladorEnum.PARAM19.getValue();
        solucionCons = OrdentrabajosControladorEnum.PARAM20.getValue();
        indFavorEmpresaCons = OrdentrabajosControladorEnum.PARAM21.getValue();
        parPQRDespues = OrdentrabajosControladorEnum.PARAM22.getValue();
        indiceSubdordenservicio = -1;
        periodoCons = GeneralParameterEnum.PERIODO.getName();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.ORDENTRABAJOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSubDOrdenServicio = new Registro(
                            new HashMap<String, Object>());
            registroSubSubOrdenDocPresentado = new Registro(
                            new HashMap<String, Object>());
            registroSubSeguimientoSub = new Registro(
                            new HashMap<String, Object>());
            registroSubOrdenTrabajoNovedades = new Registro(
                            new HashMap<String, Object>());

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                ridOrdenTrabajo = (Map<String, Object>) parametros
                                .get("ridOrdenTrabajo");
            }
            if ((parametros != null) && (parametros.get("rid") != null))
            {
                rid = extracted(parametros);
                if ("ORD".equals(rid.get(keyClaseDocCons)))
                {
                    titulo = idioma.getString("TB_TB1564");
                    tituloGrilla = idioma.getString("TB_TB1653");
                    claseDoc = "ORD";
                    varVolver = true;
                }
                else
                {
                    titulo = idioma.getString("TB_TB1563");
                    tituloGrilla = idioma.getString("TB_TB1652");
                    claseDoc = "PQR";
                    varVolver = false;
                }
            }
            else
            {
                varVolver = false;
                titulo = "";
                claseDoc = "PQR"; /*- Si se necesita que abra con otro tipo de clase desde otra opción de menú, condicionar luego de esta asignación*/

                if ("740301".equals(SessionUtil.getMenuActual()))
                {
                    titulo = idioma.getString("TB_TB1563");
                    tituloGrilla = idioma.getString("TB_TB1652");
                    claseDoc = "PQR";
                }
                else if ("740302".equals(SessionUtil.getMenuActual()))
                {
                    titulo = idioma.getString("TB_TB1564");
                    tituloGrilla = idioma.getString("TB_TB1653");
                    claseDoc = "ORD";
                }
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(OrdentrabajosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Metodo para extraer la llave de los parametros que vienen de
     * una PQR
     *
     * @param parametros
     * Recibe la llave cuando se esta abriendo una ORD que viene de
     * una PQR
     * @return La llave de la ORD que se va abrir
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> extracted(Map<String, Object> parametros)
    {
        return (Map<String, Object>) parametros.get("rid");
    }

    /**
     * Inicializa las listas de los combos que se pueden cargar sin
     * necesidad de tener el registro guardado
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaClaseProblema();
        cargarListaClaseProblemaE();

        cargarListaDocumento();
        cargarListaDocumentoE();
        cargarListaAforador();
        cargarListaDependenciaEnv();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaFormateado();
        // </CARGAR_LISTA>
    }

    /**
     * Inicializa las listas de los combos en los subformularios que
     * se pueden cargar sin necesidad de tener el registro guardado
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubdordenservicio();
        cargarListaSubordendocpresentado();
        cargarListaSeguimientosub();
        cargarListaOrdentrabajonovedades();
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * Metodo para poner nulas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubdordenservicio = null;
        listaSubordendocpresentado = null;
        listaSeguimientosub = null;
        listaOrdentrabajonovedades = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Metodo para init de la clase
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.SP_ORDENTRABAJO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Carga la consulta principal del formulario (Tabla:
     * SP_ORDENTRABAJO)
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(claseDocCons, claseDoc);

    }

    /**
     * Metodo para recargar la grilla del subformulario
     * Subdordenservicio
     */
    public void cargarListaSubdordenservicio()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(claseDocCons, registro.getCampos().get(claseDocCons));
            param.put(ordenTrabajoCons, registro.getCampos().get(numOrdenCons));

            listaSubdordenservicio = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                                                            .getGridKey())
                                                            .getUrl(),
                                            param), CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Metodo para recargar la grilla del subformulario
     * Subordendocpresentado
     */
    public void cargarListaSubordendocpresentado()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(claseDocCons, registro.getCampos().get(claseDocCons));
            param.put(ordenTrabajoCons, registro.getCampos().get(numOrdenCons));

            listaSubordendocpresentado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_ORDENDOCPRESENTADO
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SP_ORDENDOCPRESENTADO
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Metodo para recargar la grilla del subformulario Seguimientosub
     */
    public void cargarListaSeguimientosub()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(claseDocCons, registro.getCampos().get(claseDocCons));
            param.put(ordenTrabajoCons, registro.getCampos().get(numOrdenCons));

            listaSeguimientosub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL15253
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo para recargar la grilla del subformulario
     * Ordentrabajonovedades
     */
    public void cargarListaOrdentrabajonovedades()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(claseDocCons, registro.getCampos().get(claseDocCons));
            param.put(numOrdenCons, registro.getCampos().get(numOrdenCons));

            listaOrdentrabajonovedades = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e)
        {
            Logger.getLogger(OrdentrabajosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista del combo ciclo
     */
    public void cargarListaCiclo()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL22085
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (Exception e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista del combo TipoRespuesta
     */
    public void cargarListaTipoRespuesta()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(tipoRequerimientoCons,
                        registro.getCampos().get(tipoRequerimientoCons));

        try
        {
            listaTipoRespuesta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL22521
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Carga la lista del combo Formateado
     */
    public void cargarListaFormateado()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put("TIPO", "33");
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL34656
                                                        .getValue());
        listaFormateado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * Carga la lista del combo ClaseProblema cuando se esta
     * insertando
     */
    public void cargarListaClaseProblema()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL23014
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaClaseProblema = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, claseProblemaCons);

    }

    /**
     * Carga la lista del combo ClaseProblema cuando se esta editando
     */
    public void cargarListaClaseProblemaE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL23014
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaClaseProblemaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, claseProblemaCons);

    }

    /**
     * Carga la lista del combo Problema cuando se esta insertando
     */
    public void cargarListaProblema()
    {
        UrlBean urlBean = getRowSourceProblema();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        SysmanFunciones.nvl(registroSubSubDOrdenServicio
                                        .getCampos().get(claseProblemaCons), "")
                                        .toString());
        listaProblema = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * Carga la lista del combo Problema cuando se esta editando
     */
    public void cargarListaProblemaE()
    {
        UrlBean urlBean = getRowSourceProblema();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        SysmanFunciones.nvl(claseProblemaAux, "").toString());
        listaProblemaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * Carga la lista del combo Concepto cuando se esta insertando
     */
    public void cargarListaConcepto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL26368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cicloCons, registro.getCampos().get(cicloCons));
        param.put(codigoRutaCons, registro.getCampos().get(codigoRutaCons));
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(anoUsuarioCons));
        param.put(periodoCons, registro.getCampos().get(periodoUsuarioCons));

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * Carga la lista del combo Concepto cuando se esta editando
     */
    public void cargarListaConceptoE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL26368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cicloCons, registro.getCampos().get(cicloCons));
        param.put(codigoRutaCons, registro.getCampos().get(codigoRutaCons));
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(anoUsuarioCons));
        param.put(periodoCons, registro.getCampos().get(periodoUsuarioCons));

        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * Carga la lista del combo Documento cuando se esta insertando
     */
    public void cargarListaDocumento()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL28770
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * Carga la lista del combo Documento cuando se esta editando
     */
    public void cargarListaDocumentoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL28770
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDocumentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * Carga la lista del combo ProblemaSe cuando se esta insertando
     */
    public void cargarListaProblemasSe()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL30278
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        registroSubSeguimientoSub.getCampos()
                                        .get(claseProblemaCons));

        listaProblemasSe = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    /**
     * Carga la lista del combo ProblemaSe cuando se esta editando
     */
    public void cargarListaProblemasSeE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL30278
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        registroSubSeguimientoSub.getCampos()
                                        .get(claseProblemaCons));

        listaProblemasSeE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * Carga la lista del combo CodigoRuta
     */
    public void cargarListaCodigoRuta()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL31486
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cicloCons, registro.getCampos().get(cicloCons));

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaCons);

    }

    /**
     * Carga la lista del combo Aforador
     */
    public void cargarListaAforador()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL32897
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAforador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    /**
     * Carga la lista del combo DependenciaEnv
     */
    public void cargarListaDependenciaEnv()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        OrdentrabajosControladorUrlEnum.URL34050
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaEnv = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo que se ejecuta al cambiar la fecha de traslado sspdc
     * cuando se esta insertando en el subformulario
     *
     */
    public void cambiarFechaTrasladoSSPD()
    {
        // <CODIGO_DESARROLLADO>
        if ((registroSubSubDOrdenServicio.getCampos()
                        .get(fechatrasladosspCons) != null)
            && (registroSubSubDOrdenServicio
                            .getCampos()
                            .get(fechaSolucionCons) != null)
            && ((Date) registroSubSubDOrdenServicio.getCampos()
                            .get(fechatrasladosspCons)).before(
                                            (Date) registroSubSubDOrdenServicio
                                                            .getCampos()
                                                            .get(fechaSolucionCons)))
        {
            registroSubSubDOrdenServicio.getCampos().put(
                            fechatrasladosspCons,
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1642"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la fecha de traslado sspdc
     * cuando se esta editando en el subformulario
     *
     */
    public void cambiarFechaTrasladoSSPDC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (((Date) listaSubdordenservicio.get(rowNum).getCampos()
                        .get(fechatrasladosspCons)).before(
                                        (Date) listaSubdordenservicio
                                                        .get(rowNum).getCampos()
                                                        .get(fechaSolucionCons)))
        {
            listaSubdordenservicio.get(rowNum).getCampos().put(
                            fechatrasladosspCons,
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1642"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que evalua si un tipo de respuesta es 9 o 10
     *
     * @param tipoRespuesta
     * @return Verdadero si es 9 o 10
     */
    public boolean tipoRespuestaOk(String tipoRespuesta)
    {
        return "9".equals(tipoRespuesta) || "10".equals(tipoRespuesta);
    }

    /**
     * Metodo que se ejecuta al cambiar el tipo de respuesta cuando se
     * esta insertando en el subformulario
     *
     */
    public void cambiarTipoRespuesta()
    {
        // <CODIGO_DESARROLLADO>
        if (!"R".equals(registro.getCampos().get(tipoRequerimientoCons))
            && !"REA".equals(registro.getCampos().get(tipoRequerimientoCons))
            && tipoRespuestaOk(registroSubSubDOrdenServicio.getCampos()
                            .get("TIPORESPUESTA").toString()))
        {

            registroSubSubDOrdenServicio.getCampos().put(
                            fechaNotificacionCons, null);
            registroSubSubDOrdenServicio.getCampos().put(
                            tipoNotificacionCons, "3");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar el tipo de respuesta cuando se
     * esta editando en el subformulario
     *
     */
    public void cambiarTipoRespuestaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (!"R".equals(registro.getCampos().get(tipoRequerimientoCons))
            && !"REA".equals(registro.getCampos().get(tipoRequerimientoCons))
            && tipoRespuestaOk(listaSubdordenservicio.get(rowNum).getCampos()
                            .get("TIPORESPUESTA").toString()))
        {

            listaSubdordenservicio.get(rowNum).getCampos().put(
                            fechaNotificacionCons, null);
            listaSubdordenservicio.get(rowNum).getCampos().put(
                            tipoNotificacionCons, "3");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la fecha de notificacion
     * cuando se esta insertando en el subformulario
     *
     */
    public void cambiarFechaNotificacion()
    {
        // <CODIGO_DESARROLLADO>
        if ((registroSubSubDOrdenServicio.getCampos()
                        .get(fechaNotificacionCons) != null)
            && (registroSubSubDOrdenServicio
                            .getCampos().get(
                                            fechaSolucionCons) != null)
            && ((Date) registroSubSubDOrdenServicio.getCampos()
                            .get(fechaNotificacionCons)).before(
                                            (Date) registroSubSubDOrdenServicio
                                                            .getCampos().get(
                                                                            fechaSolucionCons)))
        {
            registroSubSubDOrdenServicio.getCampos().put(fechaNotificacionCons,
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1640"));
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la fecha de notificacion
     * cuando se esta editando en el subformulario
     *
     */
    public void cambiarFechaNotificacionC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (((Date) listaSubdordenservicio.get(rowNum).getCampos()
                        .get(fechaNotificacionCons)).before(
                                        (Date) listaSubdordenservicio
                                                        .get(rowNum).getCampos()
                                                        .get(fechaSolucionCons)))
        {
            listaSubdordenservicio.get(rowNum).getCampos().put(
                            fechaNotificacionCons,
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1640"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la FechaSolicitudSub cuando se
     * esta insertando en el subformulario
     *
     */
    public void cambiarFechaSolicitudSub()
    {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        if ((registroSubSubDOrdenServicio
                        .getCampos()
                        .get(fechaSolucionCons) != null)
            && ((Date) registro.getCampos()
                            .get(fechaSolucitudCons)).after(
                                            (Date) registroSubSubDOrdenServicio
                                                            .getCampos()
                                                            .get(fechaSolucionCons)))
        {
            registroSubSubDOrdenServicio.getCampos().put(
                            fechaSolucionCons, null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1641"));
            return;
        }

        if (!"R".equals(registro.getCampos().get(tipoRequerimientoCons))
            || !"REA".equals(registro.getCampos().get(tipoRequerimientoCons)))
        {
            if ("PQR".equals(claseDoc) && (registroSubSubDOrdenServicio
                            .getCampos().get(fechaSolucionCons) != null))
            {
                registroSubSubDOrdenServicio.getCampos().put(
                                fechaNotificacionCons,
                                registroSubSubDOrdenServicio
                                                .getCampos()
                                                .get(fechaSolucionCons));
                registroSubSubDOrdenServicio.getCampos().put(
                                tipoNotificacionCons, "1");
            }
            else if ("PQR".equals(claseDoc) && (registroSubSubDOrdenServicio
                            .getCampos().get(fechaSolucionCons) == null))
            {
                registroSubSubDOrdenServicio.getCampos().put(
                                fechaNotificacionCons, null);
                registroSubSubDOrdenServicio.getCampos().put(
                                tipoNotificacionCons, null);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la FechaSolicitudSub cuando se
     * esta editando en el subformulario
     *
     */
    public void cambiarFechaSolicitudSubC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (((Date) registro.getCampos()
                        .get(fechaSolucitudCons)).after(
                                        (Date) listaSubdordenservicio
                                                        .get(rowNum).getCampos()
                                                        .get(fechaSolucionCons)))
        {
            listaSubdordenservicio.get(rowNum).getCampos().put(
                            fechaSolucionCons, null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1641"));
            return;
        }

        if ("R".equals(registro.getCampos().get(tipoRequerimientoCons))
            || "REA".equals(registro.getCampos().get(tipoRequerimientoCons)))
        {
            if ("PQR".equals(claseDoc) && (listaSubdordenservicio.get(rowNum)
                            .getCampos().get(fechaSolucionCons) != null))
            {
                listaSubdordenservicio.get(rowNum).getCampos().put(
                                fechaNotificacionCons,
                                listaSubdordenservicio.get(rowNum)
                                                .getCampos()
                                                .get(fechaSolucionCons));
                listaSubdordenservicio.get(rowNum).getCampos().put(
                                tipoNotificacionCons, "1");
            }
            else if ("PQR".equals(claseDoc)
                && (listaSubdordenservicio.get(rowNum)
                                .getCampos().get(fechaSolucionCons) == null))
            {
                listaSubdordenservicio.get(rowNum).getCampos().put(
                                fechaNotificacionCons, null);
                listaSubdordenservicio.get(rowNum).getCampos().put(
                                tipoNotificacionCons, null);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se esta editando la clase de
     * problema en el subformulario Subdordenservicio
     *
     */
    public void cambiarClaseProblemaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        if (indiceSubdordenservicio == -1)
        {
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(claseNombreCons, nombreClaseProblemaAux);
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(problemaCons, null);
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(problemaNombreCons, null);
        }

        claseProblemaAux = listaSubdordenservicio.get(rowNum).getCampos()
                        .get(claseProblemaCons).toString();
        cargarListaProblemaE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarConceptoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaSubdordenservicio.get(rowNum).getCampos()
                        .put("VRFACTURADOANT", valorAnteriorAux);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se esta editando el Documento en
     * el subformulario Subordendocpresentado
     *
     */
    public void cambiarDocumentoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaSubordendocpresentado.get(rowNum).getCampos()
                        .put(documentoNombreCons, nombreDocumentoAux);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se esta editando el problema en el
     * subformulario Subdordenservicio
     *
     */
    public void cambiarProblemaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (suiUnoAux)
        {
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(problemaNombreCons, nombreProblemaAux);
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(solucionCons, txtSolucionAux);
            listaSubdordenservicio.get(rowNum).getCampos()
                            .put(indFavorEmpresaCons, indFavoroAux);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la fecha de solicitud. De esta
     * fecha se obtiene el mes anio y periodo
     *
     */
    public void cambiarTipoRequerimiento()
    {

        if ("m".equals(accion))
        {
            try
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(claseDocCons, claseDoc);
                param.put(ordenTrabajoCons,
                                registro.getCampos().get(numOrdenCons));

                Registro regAux;

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                OrdentrabajosControladorUrlEnum.URL60183
                                                                                .getValue())
                                                .getUrl(), param));

                if (!"0".equals(regAux.getCampos()
                                .get(GeneralParameterEnum.CUENTA.getName())))
                {
                    registro.getCampos().put(tipoRequerimientoCons,
                                    registroIni.get(tipoRequerimientoCons));
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1596"));
                    return;
                }

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1597"));
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar la fecha de solicitud. De esta
     * fecha se obtiene el mes anio y periodo
     *
     */
    public void cambiarFechaSolicitud()
    {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(fechaSolucitudCons) != null)
        {
            anio = String.valueOf(SysmanFunciones.ano(
                            (Date) registro.getCampos()
                                            .get(fechaSolucitudCons)));
            mes = String.valueOf(SysmanFunciones.mes(
                            (Date) registro.getCampos()
                                            .get(fechaSolucitudCons)));
        }
        else
        {
            anio = null;
            mes = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar de ciclo
     *
     */
    public void cambiarCiclo()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(codigoRutaCons, null);
        cargarListaCodigoRuta();

        try
        {
            if ("SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtl.consultarParametro(compania,
                                            idioma.getString("TB_TB3244"),
                                            modulo,
                                            new Date(), true),
                            "NO"))
                && (registro.getCampos().get(cicloCons) != null)
                && !"".equals(registro.getCampos().get(cicloCons)))
            {
                String aux = service.buscarEnLista(
                                registro.getCampos().get(cicloCons).toString(),
                                numeroCons, "PREFACTURANDO", listaCiclo);
                if (trueCons.equals(aux))
                {
                    registro.getCampos().put(cicloCons, null);
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1581"));
                    registro.getCampos().put(codigoRutaCons, null);
                    cargarListaCodigoRuta();
                }
            }

            if (registro.getCampos().get(cicloCons) != null)
            {
                periodo = service.buscarEnLista(
                                registro.getCampos().get(cicloCons).toString(),
                                numeroCons, periodoCons, listaCiclo);
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * ClaseProblema al insertar
     *
     * @param event
     */
    public void seleccionarFilaClaseProblema(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubDOrdenServicio.getCampos().put(claseProblemaCons,
                        registroAux.getCampos().get(claseProblemaCons));
        registroSubSubDOrdenServicio.getCampos().put(claseNombreCons,
                        registroAux.getCampos().get(nombreCons));
        registroSubSubDOrdenServicio.getCampos().put(problemaCons,
                        null);
        registroSubSubDOrdenServicio.getCampos().put(problemaNombreCons,
                        null);
        cargarListaProblema();
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * ClaseProblema al editar
     *
     * @param event
     */
    public void seleccionarFilaClaseProblemaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(claseProblemaCons).toString();
        nombreClaseProblemaAux = registroAux.getCampos()
                        .get(nombreCons).toString();
        claseProblemaAux = auxiliar;
        indiceSubdordenservicio = -1;
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Problema al insertar
     *
     * @param event
     */
    public void seleccionarFilaProblema(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        try
        {
            if ("SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtl.consultarParametro(compania,
                                            parPQRDespues, modulo, new Date(),
                                            true),
                            "NO"))
                && ("1".equals(registroAux.getCampos()
                                .get("COD_EQUIVALENTE_SUI").toString()))
                && ("P".equals(registro.getCampos().get(tipoRequerimientoCons))
                    || "C".equals(registro.getCampos()
                                    .get(tipoRequerimientoCons))))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1643"));
                return;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registroSubSubDOrdenServicio.getCampos().put(problemaCons,
                        registroAux.getCampos().get(codigoCons));
        registroSubSubDOrdenServicio.getCampos().put(problemaNombreCons,
                        registroAux.getCampos().get(nombreCons));
        registroSubSubDOrdenServicio.getCampos().put(solucionCons,
                        registroAux.getCampos().get(solucionCons));
        registroSubSubDOrdenServicio.getCampos().put(indFavorEmpresaCons,
                        (boolean) registroAux.getCampos()
                                        .get(indFavorEmpresaCons)
                                            ? trueCons : falseCons);
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Problema al editar
     *
     * @param event
     */
    public void seleccionarFilaProblemaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        try
        {
            if ("SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtl.consultarParametro(compania,
                                            parPQRDespues, modulo, new Date(),
                                            true),
                            "NO"))
                && ("1".equals(registroAux.getCampos()
                                .get("COD_EQUIVALENTE_SUI")))
                && ("P".equals(registro.getCampos().get(tipoRequerimientoCons))
                    || "C".equals(registro.getCampos()
                                    .get(tipoRequerimientoCons))))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1643"));
                suiUnoAux = false;
                return;
            }
            else
            {
                suiUnoAux = true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        auxiliar = registroAux.getCampos().get(codigoCons).toString();
        nombreProblemaAux = registroAux.getCampos().get(nombreCons).toString();
        txtSolucionAux = registroAux.getCampos().get(solucionCons).toString();
        indFavoroAux = (boolean) registroAux.getCampos()
                        .get(indFavorEmpresaCons)
                            ? trueCons : falseCons;
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Concepto al insertar
     *
     * @param event
     */
    public void seleccionarFilaConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubDOrdenServicio.getCampos().put(
                        GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(codigoCons));
        registroSubSubDOrdenServicio.getCampos().put("VRFACTURADOANT",
                        registroAux.getCampos().get("VALOR_FACTURADO"));
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Concepto al editar
     *
     * @param event
     */
    public void seleccionarFilaConceptoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoCons).toString();
        valorAnteriorAux = registroAux.getCampos()
                        .get("VALOR_FACTURADO").toString();
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Documento al insertar
     *
     * @param event
     */
    public void seleccionarFilaDocumento(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubOrdenDocPresentado.getCampos().put("DOCUMENTO",
                        registroAux.getCampos().get(codigoCons));
        registroSubSubOrdenDocPresentado.getCampos().put(documentoNombreCons,
                        registroAux.getCampos().get(nombreCons));
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Documento al editar
     *
     * @param event
     */
    public void seleccionarFilaDocumentoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoCons).toString();
        nombreDocumentoAux = registroAux.getCampos().get(nombreCons).toString();
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * ProblemasSe al insertar
     *
     * @param event
     */
    public void seleccionarFilaProblemasSe(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSeguimientoSub.getCampos().put(problemaCons,
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * ProblemasSe al editar
     *
     * @param event
     */
    public void seleccionarFilaProblemasSeE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoCons).toString();
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * CodigoRuta
     *
     * @param event
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        codigoRuta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoRutaCons), "")
                        .toString();
        periodoUsuario = SysmanFunciones
                        .nvl(registroAux.getCampos().get(periodoCons), "")
                        .toString();
        anoUsuario = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()), "")
                        .toString();
        dirTecnica = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DIRTECNICA"), "")
                        .toString();
        telefono = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.TELEFONO
                                                        .getName()),
                                        "")
                        .toString();
        primerApellido = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PRIMERAPELLIDO"), "")
                        .toString();
        segundoApellido = SysmanFunciones
                        .nvl(registroAux.getCampos().get("SEGUNDOAPELLIDO"), "")
                        .toString();
        nombres = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRES"), "")
                        .toString();
        ultimoAforadorAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(aforadorCons), "")
                        .toString();
        factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.FACTURA.getName()),
                                        "")
                        .toString();
        totFacturaPerActual = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TOTFACTURAPERACTUAL"),
                                        "")
                        .toString();
        codigoInternoAux = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoInternoCons), "")
                        .toString();

        if ((registroAux.getCampos().get("BANCOPERPROCESO") != null)
            && !"".equals(registroAux.getCampos().get("BANCOPERPROCESO"))
            && "PQR".equals(claseDoc))
        {
            cuadroUsuarioVisible = true;
        }
        else
        {
            seleccionarUsuario();
        }
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * FilaAforador
     *
     * @param event
     */
    public void seleccionarFilaAforador(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(aforadorCons,
                        registroAux.getCampos().get(codigoCons));
        nombreAforador = registroAux.getCampos().get(nombreCons).toString();
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * DependenciaEnv
     *
     * @param event
     */
    public void seleccionarFilaDependenciaEnv(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIAENV",
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * Metodo que se ejecuta al seleccionar un registro en el combo
     * Formateado
     *
     * @param event
     */
    public void seleccionarFilaFormateado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        formato = (String) registroAux.getCampos().get(codigoCons);
        fechaFormato = SysmanFunciones.formatearFecha(
                        (Date) registroAux.getCampos().get("FECHAAUX"));
        nombreWord = (String) registroAux.getCampos().get(nombreCons);
        plantillaWord = (String) registroAux.getCampos().get("PLANTILLA");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>

    /**
     * Metodo que se ejecuta al oprimir el boton Generar orden de
     * trabajo Genera la orden de trabajo correspondiente al PQR en
     * que se encuentre
     */
    public void oprimirCmdGenerar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {

            String[] criterio = { " COMPANIA=''" + compania
                + "'' AND CLASEDOC=''ORD''" };

            Long consecutivo = ejbSysmanUtl.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SP_ORDENTRABAJO.getTable(),
                            SysmanFunciones.concatenar(criterio), numOrdenCons,
                            "1");

            ejbServPublicosOcho.generarOrdenDeTrabajo(compania,
                            BigInteger.valueOf(consecutivo),
                            new BigInteger(registro.getCampos()
                                            .get(numOrdenCons).toString()),
                            SessionUtil.getUser().getCodigo());

            registro.getCampos().put("ORDENGENERADA", "-1");
            registro.getCampos().put(numOrdenTCons, consecutivo);
            cmdGenerarDisable = true;
            abrirOrdenTrabajoDisable = false;
            agregarRegistroNuevo(false);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1599") + " " + consecutivo);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton ver orden trabajo.
     * Estando en un PQR cambia de registro a su ORD correspondiente
     */
    public void oprimirabrirOrdenTrabajo()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> llave = new HashMap<>();
        llave.put("KEY_COMPANIA", compania);
        llave.put(keyClaseDocCons,
                        "ORD");
        llave.put("KEY_NUMORDEN",
                        registro.getCampos().get(numOrdenTCons));

        String[] campos = { "rid" };
        Object[] valores = { llave };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.ORDENTRABAJOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton CmbSerNoreclamado.
     * Abre el dialogo para seleccionar los servicios
     */
    public void oprimirCmbSerNoreclamado()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            agregarRegistroNuevo(false);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            registro.getCampos().get(cicloCons));
            param.put(codigoRutaCons,
                            registro.getCampos().get(codigoRutaCons));

            Registro regAux;

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL29522
                                                                            .getValue())
                                            .getUrl(), param));

            if ((regAux != null)
                && (regAux.getCampos().get(servNoReclamadoCons) != null))
            {

                if (regAux.getCampos().get(servNoReclamadoCons).toString()
                                .indexOf("01") != -1)
                {
                    acueducto = trueCons;
                }
                else
                {
                    acueducto = falseCons;
                }

                if (regAux.getCampos().get(servNoReclamadoCons).toString()
                                .indexOf("02") != -1)
                {
                    alcantarillado = trueCons;
                }
                else
                {
                    alcantarillado = falseCons;
                }

                if (regAux.getCampos().get(servNoReclamadoCons).toString()
                                .indexOf("03") != -1)
                {
                    aseo = trueCons;
                }
                else
                {
                    aseo = falseCons;
                }

            }
            else
            {
                acueducto = falseCons;
                alcantarillado = falseCons;
                aseo = falseCons;
            }

            cuadroServiciosVisible = true;
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton verFactura. Abre el
     * formulario FACTURA
     */
    public void oprimirVerFactura()
    {
        // <CODIGO_DESARROLLADO>

        cargarConsultaFacturacion();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo en el que se actualiza el PQR, dicho proceso se ejeccuta
     * mediante el MERGE especificado
     */
    public void oprimircmdPQR()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdentrabajosControladorUrlEnum.URL65978
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.USUARIO.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(numOrdenCons,
                            registro.getCampos().get(numOrdenCons));
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1630"));

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton WORD. Descarga la
     * plantilla WORD con las variables reemplazadas
     */
    public void oprimirWord()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (css != null)
        {

            if ((formato == null) || "".equals(formato))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1662"));
            }
            else
            {

                String[] campos = { "codigoPlantilla", "fechaPlantilla",
                                    "nombreDocDescarga" };
                String[] valores = { formato, fechaFormato,
                                     nombreWord };

                HashMap<String, String> variablesConsultaW = new HashMap<>();

                variablesConsultaW.put("s$compania$s", "'" + compania + "'");
                variablesConsultaW.put("s$claseDoc$s", "'" + claseDoc + "'");
                variablesConsultaW.put("s$numOrden$s", registro.getCampos()
                                .get(numOrdenCons).toString());
                variablesConsultaW.put("s$codigoRuta$s",
                                "'" + registro.getCampos()
                                                .get(codigoRutaCons).toString()
                                    + "'");
                variablesConsultaW.put("s$modulo$s", modulo);
                variablesConsultaW.put("s$user$s", "'" +
                    SessionUtil.getUser().getCodigo() + "'");
                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);

                SessionUtil.cargarModalDatosFlash(Integer
                                .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos,
                                valores);
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1656"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que descarga el informe 001096PQRYOP en formato PDF
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (css != null)
        {
            genInforme(FORMATOS.PDF, "001096PQRYOP");
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1656"));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    /**
     * Inserta un nuevo registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario SubSubdordenservicio. Previamente
     * genera el consecutivo y quita los campos PROBLEMANOMBRE y
     * CLASENOMBRE ya que no son de la tabla
     */
    public void agregarRegistroSubSubdordenservicio()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(cicloCons, registro.getCampos().get(cicloCons));
            param.put(codigoRutaCons,
                            registro.getCampos().get(codigoRutaCons));

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL25472
                                                                            .getValue())
                                            .getUrl(), param));

            registroSubSubDOrdenServicio.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubDOrdenServicio.getCampos().put(claseDocCons,
                            registro.getCampos().get(claseDocCons));
            registroSubSubDOrdenServicio.getCampos().put(ordenTrabajoCons,
                            registro.getCampos().get(numOrdenCons));
            String[] criterio = { " COMPANIA = ''" + compania
                + "'' AND CLASEDOC= ''"
                + claseDoc + "'' AND ORDENTRABAJO="
                + registroSubSubDOrdenServicio.getCampos()
                                .get(ordenTrabajoCons) };
            Long consecutivo = ejbSysmanUtl.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SP_D_ORDENTRABAJO.getTable(),
                            SysmanFunciones.concatenar(criterio), numeroCons,
                            "1");
            registroSubSubDOrdenServicio.getCampos().put(numeroCons,
                            consecutivo);

            registroSubSubDOrdenServicio.getCampos().put("TOTALFACTURAANT",
                            regAux.getCampos().get("TOTFACTURAPERACTUAL"));
            registroSubSubDOrdenServicio.getCampos().remove(problemaNombreCons);
            registroSubSubDOrdenServicio.getCampos().remove(claseNombreCons);

            registroSubSubDOrdenServicio.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubDOrdenServicio.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubDOrdenServicio.getCampos());

            cargarListaSubdordenservicio();
            cargarListaSeguimientosub();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));

            boolean carteraAux = registroSubSubDOrdenServicio.getCampos()
                            .get(fechaSolucionCons) == null;
            param.put(carteraCons, carteraAux ? 1 : 0);

            UrlBean urlUpdateCartera = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdentrabajosControladorUrlEnum.URL24555
                                                            .getValue());
            Parameter parameter = new Parameter();
            parameter.setFields(param);
            requestManager.update(urlUpdateCartera.getUrl(),
                            urlUpdateCartera.getMetodo(),
                            parameter);

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        finally
        {
            registroSubSubDOrdenServicio = new Registro(
                            new HashMap<String, Object>());
            cargarListaProblema();
        }
    }

    /**
     * Actualiza un registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario Subdordenservicio. Previamente
     * quita los campos PROBLEMANOMBRE y CLASENOMBRE ya que no son de
     * la tabla
     */
    public void editarRegSubSubdordenservicio(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(claseDocCons);
            reg.getCampos().remove(ordenTrabajoCons);
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(problemaNombreCons);
            reg.getCampos().remove(claseNombreCons);
            reg.getCampos().remove("FECHASOLUCIONSITIO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            Constantes.MSM_REGISTRO_MODIFICADO));

            boolean carteraAux = reg.getCampos()
                            .get(fechaSolucionCons) == null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(cicloCons, registro.getCampos().get(cicloCons));
            param.put(codigoRutaCons,
                            registro.getCampos().get(codigoRutaCons));
            param.put(carteraCons, carteraAux ? 1 : 0);

            UrlBean urlUpdateCartera = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdentrabajosControladorUrlEnum.URL24555
                                                            .getValue());
            Parameter parameter = new Parameter();
            parameter.setFields(param);
            requestManager.update(urlUpdateCartera.getUrl(),
                            urlUpdateCartera.getMetodo(),
                            parameter);
            claseProblemaAux = null;
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            cargarListaSubdordenservicio();
            cargarListaSeguimientosub();
        }
    }

    /**
     * Elimina un registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario SubSubdordenservicio.
     */
    public void eliminarRegSubSubdordenservicio(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_D_ORDENTRABAJO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaSubdordenservicio();
            cargarListaSeguimientosub();
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Recarga las listas cuando se cancela la edicion en el
     * subformulario Subdordenservicio
     */
    public void cancelarEdicionSubdordenservicio()
    {
        cargarListaSubdordenservicio();
        cargarListaSubordendocpresentado();
        cargarListaSeguimientosub();
        cargarListaOrdentrabajonovedades();
    }

    /**
     * Inserta un nuevo registro en la tabla SP_ORDENDOCPRESENTADO que
     * corresponde al subformulario Subordendocpresentado. Previamente
     * quita el campo DOCUMENTONOMBRE ya que no es de la tabla
     */
    public void agregarRegistroSubSubordendocpresentado()
    {
        try
        {
            registroSubSubOrdenDocPresentado.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubOrdenDocPresentado.getCampos().put(claseDocCons,
                            registro.getCampos().get(claseDocCons));
            registroSubSubOrdenDocPresentado.getCampos().put(ordenTrabajoCons,
                            registro.getCampos().get(numOrdenCons));

            registroSubSubOrdenDocPresentado.getCampos()
                            .remove(documentoNombreCons);

            registroSubSubOrdenDocPresentado.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubOrdenDocPresentado.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENDOCPRESENTADO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubOrdenDocPresentado.getCampos());
            cargarListaSubordendocpresentado();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            registroSubSubOrdenDocPresentado = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Actualiza un registro en la tabla SP_ORDENDOCPRESENTADO que
     * corresponde al subformulario Subordendocpresentado. Previamente
     * quita el campo DOCUMENTONOMBRE ya que no es de la tabla
     */
    public void editarRegSubSubordendocpresentado(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(claseDocCons);
            reg.getCampos().remove(ordenTrabajoCons);
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos()
                            .remove(documentoNombreCons);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENDOCPRESENTADO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            cargarListaSubordendocpresentado();
        }
    }

    /**
     * Elimina un registro en la tabla SP_ORDENDOCPRESENTADO que
     * corresponde al subformulario Subordendocpresentado.
     */
    public void eliminarRegSubSubordendocpresentado(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENDOCPRESENTADO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaSubordendocpresentado();
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Recarga las listas cuando se cancela la edicion en el
     * subformulario Subordendocpresentado
     */
    public void cancelarEdicionSubordendocpresentado()
    {
        cargarListaSubordendocpresentado();
        cargarListaSeguimientosub();
        cargarListaOrdentrabajonovedades();
    }

    /**
     * Inserta un nuevo registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario Seguimientosub.
     */
    public void agregarRegistroSubSeguimientosub()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Actualiza un registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario Seguimientosub.
     */
    public void editarRegSubSeguimientosub(RowEditEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Elimina un registro en la tabla SP_D_ORDENTRABAJO que
     * corresponde al subformulario Seguimientosub.
     */
    public void eliminarRegSubSeguimientosub(Registro reg)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Recarga las listas cuando se cancela la edicion en el
     * subformulario Seguimientosub
     */
    public void cancelarEdicionSeguimientosub()
    {
        cargarListaSeguimientosub();
        cargarListaOrdentrabajonovedades();
    }

    /**
     * Inserta un nuevo registro en la tabla SP_ORDENTRABAJONOVEDADES
     * que corresponde al subformulario Ordentrabajonovedades.
     * Previamente genera el consecutivo para el campo CONSECUTIVO
     */
    public void agregarRegistroSubOrdentrabajonovedades()
    {
        try
        {
            registroSubOrdenTrabajoNovedades.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubOrdenTrabajoNovedades.getCampos().put(claseDocCons,
                            registro.getCampos().get(claseDocCons));
            registroSubOrdenTrabajoNovedades.getCampos().put(numOrdenCons,
                            registro.getCampos().get(numOrdenCons));

            String[] criterio = { " COMPANIA=''" + compania
                + "'' AND CLASEDOC=''"
                + claseDoc + "'' AND NUMORDEN="
                + registro.getCampos().get(numOrdenCons)
            };
            Long consecutivo = ejbSysmanUtl.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES.getTable(),
                            SysmanFunciones.concatenar(criterio),
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");
            registroSubOrdenTrabajoNovedades.getCampos().put(
                            GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
            registroSubOrdenTrabajoNovedades.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubOrdenTrabajoNovedades.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubOrdenTrabajoNovedades.getCampos());

            cargarListaOrdentrabajonovedades();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_INGRESADO));

        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            registroSubOrdenTrabajoNovedades = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Actualiza un registro en la tabla SP_ORDENTRABAJONOVEDADES que
     * corresponde al subformulario Ordentrabajonovedades.
     */
    public void editarRegSubOrdentrabajonovedades(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos()
                            .remove(GeneralParameterEnum.CONSECUTIVO.getName());
            reg.getCampos().remove(numOrdenCons);
            reg.getCampos().remove(claseDocCons);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_MODIFICADO));

        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        finally
        {
            cargarListaOrdentrabajonovedades();
        }
    }

    /**
     * Elimina un registro en la tabla SP_ORDENTRABAJONOVEDADES que
     * corresponde al subformulario Ordentrabajonovedades.
     */
    public void eliminarRegSubOrdentrabajonovedades(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_ORDENTRABAJONOVEDADES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            Constantes.MSM_REGISTRO_ELIMINADO));

            cargarListaOrdentrabajonovedades();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(OrdentrabajosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Recarga las listas cuando se cancela la edicion en el
     * subformulario Ordentrabajonovedades
     */
    public void cancelarEdicionOrdentrabajonovedades()
    {
        cargarListaOrdentrabajonovedades();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que se ejecuta cuando se da clic al boton Consultar
     * Facturacion
     */
    public void cargarConsultaFacturacion()
    {

        try
        {
            boolean bloqueado = ejbServiciosPublicosCuatro.estarBloqueado(
                            compania,
                            Integer.parseInt(registro.getCampos().get(cicloCons)
                                            .toString()));
            if (bloqueado)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3156"));
            }
            else
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(),
                                registro.getCampos().get(cicloCons));
                param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                registro.getCampos().get(codigoRutaCons));

                Registro registroUsuario = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                OrdentrabajosControladorUrlEnum.URL66184
                                                                                .getValue())
                                                .getUrl(), param));

                String[] campos = { "ridUsuario", "ciclo", "ano", "periodo",
                                    "ridOrdenTrabajo" };
                Object[] valores = { registroUsuario.getCampos(), registro
                                .getCampos().get(cicloCons).toString(),
                                     anio,
                                     periodo,
                                     registro.getLlave() };

                SessionUtil.redireccionarPorFormulario(modulo,
                                Integer.toString(
                                                GeneralCodigoFormaEnum.FACTURA_CONTROLADOR
                                                                .getCodigo()),
                                campos,
                                valores, true);

            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo en el cual se asignan las variables del usuario que se
     * selecciono en el campo codigo ruta al registro de la tabla
     * SP_ORDENTRABAJO
     */
    public void seleccionarUsuario()
    {
        try
        {
            registro.getCampos().put(codigoRutaCons, codigoRuta);
            registro.getCampos().put(periodoUsuarioCons, periodoUsuario);
            registro.getCampos().put(anoUsuarioCons, anoUsuario);
            registro.getCampos().put("DIRTECNICA", dirTecnica);
            registro.getCampos().put(GeneralParameterEnum.TELEFONO.getName(),
                            telefono);
            registro.getCampos().put("PRIMERAPELLIDO", primerApellido);
            registro.getCampos().put("SEGUNDOAPELLIDO", segundoApellido);
            registro.getCampos().put("NOMBRES", nombres);
            registro.getCampos().put(aforadorCons, ultimoAforador);
            ultimoAforador = ultimoAforadorAux;
            codigoInterno = codigoInternoAux;

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(aforadorCons, ultimoAforador);

            Registro regAux;

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            OrdentrabajosControladorUrlEnum.URL61732
                                                                            .getValue())
                                            .getUrl(), param));

            if (regAux != null)
            {
                nombreAforador = SysmanFunciones
                                .nvl(regAux.getCampos().get(nombreCons), "")
                                .toString();
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "VALIDA NUMERO DE FACTURA EN PQR",
                                            modulo, new Date(), true),
                                            "NO"))
                && !"0".equals(totFacturaPerActual))
            {
                if (!"0".equals(factura))
                {
                    registro.getCampos().put(
                                    GeneralParameterEnum.FACTURA.getName(),
                                    factura);
                }
                else
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1585"));
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo equivalente a mid en Access
     *
     * @param valor
     * Cadena a realizar mid
     * @param ini
     * Desde
     * @param len
     * Longitud
     * @return Retorna la consulta con la que se debe cargar el combo
     * problema en el subformulario Subdordenservicio
     */
    public String mid(String valor, int ini, int len)
    {
        return valor.substring(ini - 1, (ini - 1) + len);
    }

    /**
     * Metodo que retorna la consulta con la que se debe cargar el
     * combo problema en el subformulario Subdordenservicio
     *
     * @return Retorna la consulta con la que se debe cargar el combo
     * problema en el subformulario Subdordenservicio
     */

    public UrlBean getRowSourceProblema()
    {

        UrlBean rta = null;
        boolean tipoReque = "P"
                        .equals(registro.getCampos().get(tipoRequerimientoCons))
            || "C".equals(registro.getCampos().get(tipoRequerimientoCons));
        try
        {

            if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtl.consultarParametro(
                            compania, parPQRDespues, modulo, new Date(), true),
                            "NO")))
            {
                if (tipoReque)
                {
                    rta = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    OrdentrabajosControladorUrlEnum.URL23800
                                                                    .getValue());
                }
                else
                {
                    rta = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    OrdentrabajosControladorUrlEnum.URL24576
                                                                    .getValue());
                }
            }
            else
            {
                if (tipoReque)
                {
                    rta = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    OrdentrabajosControladorUrlEnum.URL19041
                                                                    .getValue());
                }
                else
                {
                    rta = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    OrdentrabajosControladorUrlEnum.URL30883
                                                                    .getValue());
                }
            }
            return rta;
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return rta;
    }

    /**
     * Metodo que se ejecuta al no aceptar el dialogo CuadroServicios
     */
    public void cancelarCuadroServicios()
    {
        // <CODIGO_DESARROLLADO>
        cuadroServiciosVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al activar la edición de la grilla
     * Subdordenservicio
     *
     * @param reg
     * Registro en que se activa la edición
     */
    public void activarEdicionSubdordenservicio(Registro reg)
    {
        indiceSubdordenservicio = listaSubdordenservicio.indexOf(reg);
    }

    /**
     * Proceso en que se genera el reporte
     *
     * @param formato
     * Formato en que se desea generar el reporte
     * @param reporte
     * Nombre del reporte que se va a descargar
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte)
    {
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("claseDoc", claseDoc);
            reemplazar.put("numOrden", registro.getCampos().get(numOrdenCons));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_MENSAJEPQR", SysmanFunciones.nvl(
                            ejbSysmanUtl.consultarParametro(compania,
                                            "MENSAJE PQR", modulo, new Date(),
                                            true),
                            "(Parámetro:MENSAJE PQR)"));
            parametros.put("PR_NOMBRE AUXILIAR_PQRS", SysmanFunciones.nvl(
                            ejbSysmanUtl.consultarParametro(compania,
                                            "NOMBRE AUXILIAR PQRS", modulo,
                                            new Date(), true),
                            ""));
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_TITULO", "PQR".equals(claseDoc)
                ? idioma.getString("TB_TB1654")
                : idioma.getString("TB_TB1655"));
            parametros.put("PR_FECHA",
                            new Date());
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo que se ejecuta al oprimir el boton volver del
     * formulario. En este caso si viene de una PQR se devuelve a esta
     * misma
     */
    public void ejecutarrcVolver()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> llave = new HashMap<>();
        llave.put("KEY_COMPANIA", compania);
        llave.put(keyClaseDocCons,
                        "PQR");
        llave.put("KEY_NUMORDEN",
                        registro.getCampos().get(numOrdenTCons));

        String[] campos = { "rid" };
        Object[] valores = { llave };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.ORDENTRABAJOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al aceptar el dialogo CuadroServicios
     */
    public void aceptarCuadroServicios()
    {

        // <CODIGO_DESARROLLADO>
        if (falseCons.equals(acueducto) && falseCons.equals(alcantarillado)
            && falseCons.equals(aseo))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1629"));
            cuadroServiciosVisible = false;
            return;
        }
        else
        {
            String servicios = "";

            if (trueCons.equals(acueducto))
            {
                servicios = "01,";
            }

            if (trueCons.equals(alcantarillado))
            {
                servicios = servicios + "02,";
            }

            if (trueCons.equals(aseo))
            {
                servicios = servicios + "03,";
            }
            try
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(),
                                registro.getCampos().get(cicloCons));
                param.put(codigoRutaCons,
                                registro.getCampos().get(codigoRutaCons));
                param.put("SERVICIO",
                                mid(servicios, 1, servicios.length() - 1));
                param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                OrdentrabajosControladorUrlEnum.URL55991
                                                                .getValue());

                Parameter parameter = new Parameter();
                parameter.setFields(param);
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

            cuadroServiciosVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al aceptar el dialogo CuadroUsuario
     */
    public void aceptarCuadroUsuario()
    {
        // <CODIGO_DESARROLLADO>
        seleccionarUsuario();
        cuadroUsuarioVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al no aceptar el dialogo CuadroUsuario
     */
    public void cancelarCuadroUsuario()
    {
        // <CODIGO_DESARROLLADO>
        cuadroUsuarioVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_ADICIONALES>
    /**
     * Metodo que se ejecuta al abrir el formulario. En este se
     * verifican caracteristicas como visibilidad y bloqueos de
     * componentees segun la clase de orden de trabajo o segun
     * parametros
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        try
        {

            if ("PQR".equals(claseDoc))
            {
                abrirVisibles = true;
                abrirNoVisibles = false;
                pqrVisible = false;
                ordenEtiqueta = "ORD ";
            }
            else if ("ORD".equals(claseDoc))
            {
                abrirVisibles = false;
                abrirNoVisibles = true;
                pqrVisible = "SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtl.consultarParametro(compania,
                                                "ACTUALIZA PQR DESDE ORDEN DE TRABAJO",
                                                modulo, new Date(), true),
                                                "NO"));
                ordenEtiqueta = "PQR ";
            }

            cmbNoRecVisible = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "FACTURA SERVICIOS NO RECLAMADOS",
                                            modulo, new Date(), true), "NO"));

            terrenoVisible = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "MANEJA ORDENES DE TRABAJO WEB",
                                            modulo, new Date(), true), "NO"));

            fechaSitioVisible = terrenoVisible;

            txtSolucionBloqueado = "NO".equals(SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "MODIFICAR DESCRIPCION EN PQR",
                                            modulo, new Date(), true), "NO"));
            if ((ridOrdenTrabajo != null) && !ridOrdenTrabajo.isEmpty())
            {
                cargarRegistro(ridOrdenTrabajo, ACCION_MODIFICAR);
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cargar o crear un nuevo registro. En
     * este se verifican caracteristicas como visibilidad y bloqueos
     * de componentes
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        varVolver = "ORD".equals(claseDoc) && (!"i".equals(accion))
            && varVolver;

        anio = null;
        periodo = null;
        mes = null;
        nombreAforador = null;
        codigoInterno = null;
        ultimoAforador = null;

        cargarListaConcepto();
        cargarListaConceptoE();

        if (css == null)
        {
            codigoCicloBloqueado = false;
            cmdGenerarDisable = true;
            abrirOrdenTrabajoDisable = true;
            Date fechaActual = new Date();
            registro.getCampos().put("FECHASOLICITUD",
                            fechaActual);
            registro.getCampos().put("HORASOLICITUD",
                            fechaActual);
            anio = String.valueOf(SysmanFunciones.ano(
                            fechaActual));
            mes = String.valueOf(SysmanFunciones.mes(
                            fechaActual));
            registro.getCampos().put("OPERADOR",
                            SessionUtil.getUser().getCodigo());
        }
        else
        {
            cargarListaCodigoRuta();
            cargarListaTipoRespuesta();
            codigoCicloBloqueado = true;
            if (registro.getCampos().get(fechaSolucitudCons) != null)
            {
                anio = String.valueOf(SysmanFunciones.ano((Date) registro
                                .getCampos().get(fechaSolucitudCons)));
                mes = String.valueOf(SysmanFunciones.mes((Date) registro
                                .getCampos().get(fechaSolucitudCons)));
            }

            periodo = service.buscarEnLista(
                            registro.getCampos().get(cicloCons).toString(),
                            numeroCons, periodoCons, listaCiclo);
            nombreAforador = SysmanFunciones
                            .nvl(registro.getCampos().get("NOMBREAFORADOR"), "")
                            .toString();
            codigoInterno = SysmanFunciones
                            .nvl(registro.getCampos().get(codigoInternoCons),
                                            "")
                            .toString();
            ultimoAforador = SysmanFunciones
                            .nvl(registro.getCampos().get("ULTIMOAFORADOR"), "")
                            .toString();

            if ("0".equals(registro.getCampos().get(numOrdenTCons)
                            .toString()))
            {
                abrirOrdenTrabajoDisable = true;
            }
            else
            {
                abrirOrdenTrabajoDisable = false;
            }

            if ((boolean) registro.getCampos().get("ORDENGENERADA"))
            {
                cmdGenerarDisable = true;
            }
            else
            {
                cmdGenerarDisable = false;
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Proceso que se ejecuta antes de insertar un registro en el
     * formulario principal. En este se genera el consecutivo para el
     * campo NUMORDEN
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            int consPQR = Integer.parseInt(
                            SysmanFunciones.nvl(ejbSysmanUtl.consultarParametro(
                                            compania, "CONSECUTIVO PQR", modulo,
                                            new Date(), true), "1")
                                            .toString());

            String[] criterio = { "COMPANIA =''" + compania
                + "'' AND CLASEDOC=''"
                + claseDoc + "'' "
            };
            Long numOrd = ejbSysmanUtl.generarConsecutivoConValorInicial(
                            GenericUrlEnum.SP_ORDENTRABAJO.getTable(),
                            SysmanFunciones.concatenar(criterio), numOrdenCons,
                            "1");

            if ((consPQR > 0) && (consPQR > numOrd))
            {
                registro.getCampos().put(numOrdenCons, consPQR);
            }
            else
            {
                registro.getCampos().put(numOrdenCons, numOrd);
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Proceso que se ejecuta despues de insertar un registro en el
     * formulario principal.
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Proceso que se ejecuta antes de insertar y editar un registro
     * en el formulario principal. En este se agrega al registro la
     * clase y se le quitan los campos informativos que no pertenecen
     * a la tabla SP_ORDENTRABAJO
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>

        if ("m".equals(accion))
        {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(claseDocCons);
            registro.getCampos().remove(numOrdenCons);
        }
        else
        {
            registro.getCampos().put(claseDocCons, claseDoc);

        }
        registro.getCampos().remove("NOMBREAFORADOR");
        registro.getCampos().remove(codigoInternoCons);
        registro.getCampos().remove(periodoUsuarioCons);
        registro.getCampos().remove(anoUsuarioCons);
        registro.getCampos().remove("ULTIMOAFORADOR");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Despues de editar un registro en el formulario principal se
     * hace una actualizacion a SP_USUARIO en el campo EXCLUIRCARTERA
     * segun este activa o no la orden de trabajo
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            registro.getCampos().get(cicloCons));
            param.put(codigoRutaCons,
                            registro.getCampos().get(codigoRutaCons));
            param.put(carteraCons, (boolean) registro.getCampos().get("ACTIVO")
                ? "1" : "0");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            OrdentrabajosControladorUrlEnum.URL24555
                                                            .getValue());
            Parameter parameter = new Parameter();
            parameter.setFields(param);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Proceso que se ejecuta antes de eliminar un registro en el
     * formulario principal.
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Proceso que se despues de eliminar un registro en el formulario
     * principal.
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
     * Retorna la variable nombreAforador
     *
     * @return nombreAforador
     */
    public String getNombreAforador()
    {
        return nombreAforador;
    }

    /**
     * Asigna la variable nombreAforador
     *
     * @param nombreAforador
     * Variable a asignar en nombreAforador
     */
    public void setNombreAforador(String nombreAforador)
    {
        this.nombreAforador = nombreAforador;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable mes
     *
     * @return mes
     */
    public String getMes()
    {
        return mes;
    }

    /**
     * Asigna la variable mes
     *
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes)
    {
        this.mes = mes;
    }

    /**
     * Retorna la variable codigoInterno
     *
     * @return codigoInterno
     */
    public String getCodigoInterno()
    {
        return codigoInterno;
    }

    /**
     * Asigna la variable codigoInterno
     *
     * @param codigoInterno
     * Variable a asignar en codigoInterno
     */
    public void setCodigoInterno(String codigoInterno)
    {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Retorna la variable formato
     *
     * @return formato
     */
    public String getFormato()
    {
        return formato;
    }

    /**
     * Asigna la variable formato
     *
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato)
    {
        this.formato = formato;
    }

    /**
     * Retorna la variable archivoDescarga
     *
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Retorna la variable claseDoc
     *
     * @return claseDoc
     */
    public String getClaseDoc()
    {
        return claseDoc;
    }

    /**
     * Asigna la variable claseDoc
     *
     * @param claseDoc
     * Variable a asignar en claseDoc
     */
    public void setClaseDoc(String claseDoc)
    {
        this.claseDoc = claseDoc;
    }

    /**
     * Retorna la variable titulo
     *
     * @return titulo
     */
    public String getTitulo()
    {
        return titulo;
    }

    /**
     * Asigna la variable titulo
     *
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    /**
     * Retorna la variable dirTecnica
     *
     * @return dirTecnica
     */
    public String getDirTecnica()
    {
        return dirTecnica;
    }

    /**
     * Asigna la variable dirTecnica
     *
     * @param dirTecnica
     * Variable a asignar en dirTecnica
     */
    public void setDirTecnica(String dirTecnica)
    {
        this.dirTecnica = dirTecnica;
    }

    /**
     * Retorna la variable telefono
     *
     * @return telefono
     */
    public String getTelefono()
    {
        return telefono;
    }

    /**
     * Asigna la variable telefono
     *
     * @param telefono
     * Variable a asignar en telefono
     */
    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    /**
     * Retorna la variable primerApellido
     *
     * @return primerApellido
     */
    public String getPrimerApellido()
    {
        return primerApellido;
    }

    /**
     * Asigna la variable primerApellido
     *
     * @param primerApellido
     * Variable a asignar en primerApellido
     */
    public void setPrimerApellido(String primerApellido)
    {
        this.primerApellido = primerApellido;
    }

    /**
     * Retorna la variable segundoApellido
     *
     * @return segundoApellido
     */
    public String getSegundoApellido()
    {
        return segundoApellido;
    }

    /**
     * Asigna la variable segundoApellido
     *
     * @param segundoApellido
     * Variable a asignar en segundoApellido
     */
    public void setSegundoApellido(String segundoApellido)
    {
        this.segundoApellido = segundoApellido;
    }

    /**
     * Retorna la variable nombres
     *
     * @return nombres
     */
    public String getNombres()
    {
        return nombres;
    }

    /**
     * Asigna la variable nombres
     *
     * @param nombres
     * Variable a asignar en nombres
     */
    public void setNombres(String nombres)
    {
        this.nombres = nombres;
    }

    /**
     * Retorna la variable ultimoAforador
     *
     * @return ultimoAforador
     */
    public String getUltimoAforador()
    {
        return ultimoAforador;
    }

    /**
     * Asigna la variable ultimoAforador
     *
     * @param ultimoAforador
     * Variable a asignar en ultimoAforador
     */
    public void setUltimoAforador(String ultimoAforador)
    {
        this.ultimoAforador = ultimoAforador;
    }

    /**
     * Retorna la variable factura
     *
     * @return factura
     */
    public String getFactura()
    {
        return factura;
    }

    /**
     * Asigna la variable factura
     *
     * @param factura
     * Variable a asignar en factura
     */
    public void setFactura(String factura)
    {
        this.factura = factura;
    }

    /**
     * Retorna la variable cuadroUsuarioVisible
     *
     * @return cuadroUsuarioVisible
     */
    public boolean isCuadroUsuarioVisible()
    {
        return cuadroUsuarioVisible;
    }

    /**
     * Asigna la variable cuadroUsuarioVisible
     *
     * @param cuadroUsuarioVisible
     * Variable a asignar en cuadroUsuarioVisible
     */
    public void setCuadroUsuarioVisible(boolean cuadroUsuarioVisible)
    {
        this.cuadroUsuarioVisible = cuadroUsuarioVisible;
    }

    /**
     * Retorna la variable totFacturaPerActual
     *
     * @return totFacturaPerActual
     */
    public String getTotFacturaPerActual()
    {
        return totFacturaPerActual;
    }

    /**
     * Asigna la variable totFacturaPerActual
     *
     * @param totFacturaPerActual
     * Variable a asignar en totFacturaPerActual
     */
    public void setTotFacturaPerActual(String totFacturaPerActual)
    {
        this.totFacturaPerActual = totFacturaPerActual;
    }

    /**
     * Retorna la variable ultimoAforadorAux
     *
     * @return ultimoAforadorAux
     */
    public String getUltimoAforadorAux()
    {
        return ultimoAforadorAux;
    }

    /**
     * Asigna la variable ultimoAforadorAux
     *
     * @param ultimoAforadorAux
     * Variable a asignar en ultimoAforadorAux
     */
    public void setUltimoAforadorAux(String ultimoAforadorAux)
    {
        this.ultimoAforadorAux = ultimoAforadorAux;
    }

    /**
     * Retorna la variable codigoInternoAux
     *
     * @return codigoInternoAux
     */
    public String getCodigoInternoAux()
    {
        return codigoInternoAux;
    }

    /**
     * Asigna la variable codigoInternoAux
     *
     * @param codigoInternoAux
     * Variable a asignar en codigoInternoAux
     */
    public void setCodigoInternoAux(String codigoInternoAux)
    {
        this.codigoInternoAux = codigoInternoAux;
    }

    /**
     * Retorna la variable periodoUsuario
     *
     * @return periodoUsuario
     */
    public String getPeriodoUsuario()
    {
        return periodoUsuario;
    }

    /**
     * Asigna la variable periodoUsuario
     *
     * @param periodoUsuario
     * Variable a asignar en periodoUsuario
     */
    public void setPeriodoUsuario(String periodoUsuario)
    {
        this.periodoUsuario = periodoUsuario;
    }

    /**
     * Retorna la variable anoUsuario
     *
     * @return anoUsuario
     */
    public String getAnoUsuario()
    {
        return anoUsuario;
    }

    /**
     * Asigna la variable anoUsuario
     *
     * @param anoUsuario
     * Variable a asignar en anoUsuario
     */
    public void setAnoUsuario(String anoUsuario)
    {
        this.anoUsuario = anoUsuario;
    }

    /**
     * Retorna la variable codigoRuta
     *
     * @return codigoRuta
     */
    public String getCodigoRuta()
    {
        return codigoRuta;
    }

    /**
     * Asigna la variable codigoRuta
     *
     * @param codigoRuta
     * Variable a asignar en codigoRuta
     */
    public void setCodigoRuta(String codigoRuta)
    {
        this.codigoRuta = codigoRuta;
    }

    /**
     * Retorna la variable abrirOrdenTrabajoDisable
     *
     * @return abrirOrdenTrabajoDisable
     */
    public boolean isAbrirOrdenTrabajoDisable()
    {
        return abrirOrdenTrabajoDisable;
    }

    /**
     * Asigna la variable abrirOrdenTrabajoDisable
     *
     * @param abrirOrdenTrabajoDisable
     * Variable a asignar en abrirOrdenTrabajoDisable
     */
    public void setAbrirOrdenTrabajoDisable(boolean abrirOrdenTrabajoDisable)
    {
        this.abrirOrdenTrabajoDisable = abrirOrdenTrabajoDisable;
    }

    /**
     * Retorna la variable codigoCicloBloqueado
     *
     * @return codigoCicloBloqueado
     */
    public boolean isCodigoCicloBloqueado()
    {
        return codigoCicloBloqueado;
    }

    /**
     * Asigna la variable codigoCicloBloqueado
     *
     * @param codigoCicloBloqueado
     * Variable a asignar en codigoCicloBloqueado
     */
    public void setCodigoCicloBloqueado(boolean codigoCicloBloqueado)
    {
        this.codigoCicloBloqueado = codigoCicloBloqueado;
    }

    /**
     * Retorna la variable cmdGenerarDisable
     *
     * @return cmdGenerarDisable
     */
    public boolean isCmdGenerarDisable()
    {
        return cmdGenerarDisable;
    }

    /**
     * Asigna la variable cmdGenerarDisable
     *
     * @param cmdGenerarDisable
     * Variable a asignar en cmdGenerarDisable
     */
    public void setCmdGenerarDisable(boolean cmdGenerarDisable)
    {
        this.cmdGenerarDisable = cmdGenerarDisable;
    }

    /**
     * Retorna la variable terrenoVisible
     *
     * @return terrenoVisible
     */
    public boolean isTerrenoVisible()
    {
        return terrenoVisible;
    }

    /**
     * Asigna la variable terrenoVisible
     *
     * @param terrenoVisible
     * Variable a asignar en terrenoVisible
     */
    public void setTerrenoVisible(boolean terrenoVisible)
    {
        this.terrenoVisible = terrenoVisible;
    }

    /**
     * Retorna la variable cmbNoRecVisible
     *
     * @return cmbNoRecVisible
     */
    public boolean isCmbNoRecVisible()
    {
        return cmbNoRecVisible;
    }

    /**
     * Asigna la variable cmbNoRecVisible
     *
     * @param cmbNoRecVisible
     * Variable a asignar en cmbNoRecVisible
     */
    public void setCmbNoRecVisible(boolean cmbNoRecVisible)
    {
        this.cmbNoRecVisible = cmbNoRecVisible;
    }

    /**
     * Retorna la variable abrirVisibles
     *
     * @return abrirVisibles
     */
    public boolean isAbrirVisibles()
    {
        return abrirVisibles;
    }

    /**
     * Asigna la variable abrirVisibles
     *
     * @param abrirVisibles
     * Variable a asignar en abrirVisibles
     */
    public void setAbrirVisibles(boolean abrirVisibles)
    {
        this.abrirVisibles = abrirVisibles;
    }

    /**
     * Retorna la variable abrirNoVisibles
     *
     * @return abrirNoVisibles
     */
    public boolean isAbrirNoVisibles()
    {
        return abrirNoVisibles;
    }

    /**
     * Asigna la variable abrirNoVisibles
     *
     * @param abrirNoVisibles
     * Variable a asignar en abrirNoVisibles
     */
    public void setAbrirNoVisibles(boolean abrirNoVisibles)
    {
        this.abrirNoVisibles = abrirNoVisibles;
    }

    /**
     * Retorna la variable pqrVisible
     *
     * @return pqrVisible
     */
    public boolean isPqrVisible()
    {
        return pqrVisible;
    }

    /**
     * Asigna la variable pqrVisible
     *
     * @param pqrVisible
     * Variable a asignar en pqrVisible
     */
    public void setPqrVisible(boolean pqrVisible)
    {
        this.pqrVisible = pqrVisible;
    }

    /**
     * Retorna la variable ordenEtiqueta
     *
     * @return ordenEtiqueta
     */
    public String getOrdenEtiqueta()
    {
        return ordenEtiqueta;
    }

    /**
     * Asigna la variable ordenEtiqueta
     *
     * @param ordenEtiqueta
     * Variable a asignar en ordenEtiqueta
     */
    public void setOrdenEtiqueta(String ordenEtiqueta)
    {
        this.ordenEtiqueta = ordenEtiqueta;
    }

    /**
     * Retorna la variable cuadroServiciosVisible
     *
     * @return cuadroServiciosVisible
     */
    public boolean isCuadroServiciosVisible()
    {
        return cuadroServiciosVisible;
    }

    /**
     * Asigna la variable cuadroServiciosVisible
     *
     * @param cuadroServiciosVisible
     * Variable a asignar en cuadroServiciosVisible
     */
    public void setCuadroServiciosVisible(boolean cuadroServiciosVisible)
    {
        this.cuadroServiciosVisible = cuadroServiciosVisible;
    }

    /**
     * Retorna la variable acueducto
     *
     * @return acueducto
     */
    public String getAcueducto()
    {
        return acueducto;
    }

    /**
     * Asigna la variable acueducto
     *
     * @param acueducto
     * Variable a asignar en acueducto
     */
    public void setAcueducto(String acueducto)
    {
        this.acueducto = acueducto;
    }

    /**
     * Retorna la variable alcantarillado
     *
     * @return alcantarillado
     */
    public String getAlcantarillado()
    {
        return alcantarillado;
    }

    /**
     * Asigna la variable alcantarillado
     *
     * @param alcantarillado
     * Variable a asignar en alcantarillado
     */
    public void setAlcantarillado(String alcantarillado)
    {
        this.alcantarillado = alcantarillado;
    }

    /**
     * Retorna la variable aseo
     *
     * @return aseo
     */
    public String getAseo()
    {
        return aseo;
    }

    /**
     * Asigna la variable aseo
     *
     * @param aseo
     * Variable a asignar en aseo
     */
    public void setAseo(String aseo)
    {
        this.aseo = aseo;
    }

    /**
     * Retorna la variable nombreDocumentoAux
     *
     * @return nombreDocumentoAux
     */
    public String getNombreDocumentoAux()
    {
        return nombreDocumentoAux;
    }

    /**
     * Asigna la variable nombreDocumentoAux
     *
     * @param nombreDocumentoAux
     * Variable a asignar en nombreDocumentoAux
     */
    public void setNombreDocumentoAux(String nombreDocumentoAux)
    {
        this.nombreDocumentoAux = nombreDocumentoAux;
    }

    /**
     * Retorna la variable valorAnteriorAux
     *
     * @return valorAnteriorAux
     */
    public String getValorAnteriorAux()
    {
        return valorAnteriorAux;
    }

    /**
     * Asigna la variable valorAnteriorAux
     *
     * @param valorAnteriorAux
     * Variable a asignar en valorAnteriorAux
     */
    public void setValorAnteriorAux(String valorAnteriorAux)
    {
        this.valorAnteriorAux = valorAnteriorAux;
    }

    /**
     * Retorna la variable claseProblemaAux
     *
     * @return claseProblemaAux
     */
    public String getClaseProblemaAux()
    {
        return claseProblemaAux;
    }

    /**
     * Asigna la variable claseProblemaAux
     *
     * @param claseProblemaAux
     * Variable a asignar en claseProblemaAux
     */
    public void setClaseProblemaAux(String claseProblemaAux)
    {
        this.claseProblemaAux = claseProblemaAux;
    }

    /**
     * Retorna la variable nombreClaseProblemaAux
     *
     * @return nombreClaseProblemaAux
     */
    public String getNombreClaseProblemaAux()
    {
        return nombreClaseProblemaAux;
    }

    /**
     * Asigna la variable nombreClaseProblemaAux
     *
     * @param nombreClaseProblemaAux
     * Variable a asignar en nombreClaseProblemaAux
     */
    public void setNombreClaseProblemaAux(String nombreClaseProblemaAux)
    {
        this.nombreClaseProblemaAux = nombreClaseProblemaAux;
    }

    /**
     * Retorna la variable nombreProblemaAux
     *
     * @return nombreProblemaAux
     */
    public String getNombreProblemaAux()
    {
        return nombreProblemaAux;
    }

    /**
     * Asigna la variable nombreProblemaAux
     *
     * @param nombreProblemaAux
     * Variable a asignar en nombreProblemaAux
     */
    public void setNombreProblemaAux(String nombreProblemaAux)
    {
        this.nombreProblemaAux = nombreProblemaAux;
    }

    /**
     * Retorna la variable fechaSitioVisible
     *
     * @return fechaSitioVisible
     */
    public boolean isFechaSitioVisible()
    {
        return fechaSitioVisible;
    }

    /**
     * Asigna la variable fechaSitioVisible
     *
     * @param fechaSitioVisible
     * Variable a asignar en fechaSitioVisible
     */
    public void setFechaSitioVisible(boolean fechaSitioVisible)
    {
        this.fechaSitioVisible = fechaSitioVisible;
    }

    /**
     * Retorna la variable txtSolucionBloqueado
     *
     * @return txtSolucionBloqueado
     */
    public boolean isTxtSolucionBloqueado()
    {
        return txtSolucionBloqueado;
    }

    /**
     * Asigna la variable txtSolucionBloqueado
     *
     * @param txtSolucionBloqueado
     * Variable a asignar en txtSolucionBloqueado
     */
    public void setTxtSolucionBloqueado(boolean txtSolucionBloqueado)
    {
        this.txtSolucionBloqueado = txtSolucionBloqueado;
    }

    /**
     * Retorna la variable txtSolucionAux
     *
     * @return txtSolucionAux
     */
    public String getTxtSolucionAux()
    {
        return txtSolucionAux;
    }

    /**
     * Asigna la variable txtSolucionAux
     *
     * @param txtSolucionAux
     * Variable a asignar en txtSolucionAux
     */
    public void setTxtSolucionAux(String txtSolucionAux)
    {
        this.txtSolucionAux = txtSolucionAux;
    }

    /**
     * Retorna la variable indFavoroAux
     *
     * @return indFavoroAux
     */
    public String getIndFavoroAux()
    {
        return indFavoroAux;
    }

    /**
     * Asigna la variable indFavoroAux
     *
     * @param indFavoroAux
     * Variable a asignar en indFavoroAux
     */
    public void setIndFavoroAux(String indFavoroAux)
    {
        this.indFavoroAux = indFavoroAux;
    }

    /**
     * Retorna la variable suiUnoAux
     *
     * @return suiUnoAux
     */
    public boolean isSuiUnoAux()
    {
        return suiUnoAux;
    }

    /**
     * Asigna la variable suiUnoAux
     *
     * @param suiUnoAux
     * Variable a asignar en suiUnoAux
     */
    public void setSuiUnoAux(boolean suiUnoAux)
    {
        this.suiUnoAux = suiUnoAux;
    }

    /**
     * Retorna la variable indiceSubdordenservicio
     *
     * @return indiceSubdordenservicio
     */
    public int getIndiceSubdordenservicio()
    {
        return indiceSubdordenservicio;
    }

    /**
     * Asigna la variable indiceSubdordenservicio
     *
     * @param indiceSubdordenservicio
     * Variable a asignar en indiceSubdordenservicio
     */
    public void setIndiceSubdordenservicio(int indiceSubdordenservicio)
    {
        this.indiceSubdordenservicio = indiceSubdordenservicio;
    }

    /**
     * Retorna la variable tituloGrilla
     *
     * @return tituloGrilla
     */
    public String getTituloGrilla()
    {
        return tituloGrilla;
    }

    /**
     * Asigna la variable tituloGrilla
     *
     * @param tituloGrilla
     * Variable a asignar en tituloGrilla
     */
    public void setTituloGrilla(String tituloGrilla)
    {
        this.tituloGrilla = tituloGrilla;
    }

    /**
     * Retorna la variable fechaFormato
     *
     * @return fechaFormato
     */
    public String getFechaFormato()
    {
        return fechaFormato;
    }

    /**
     * Asigna la variable fechaFormato
     *
     * @param fechaFormato
     * Variable a asignar en fechaFormato
     */
    public void setFechaFormato(String fechaFormato)
    {
        this.fechaFormato = fechaFormato;
    }

    /**
     * Retorna la variable nombreWord
     *
     * @return nombreWord
     */
    public String getNombreWord()
    {
        return nombreWord;
    }

    /**
     * Asigna la variable nombreWord
     *
     * @param nombreWord
     * Variable a asignar en nombreWord
     */
    public void setNombreWord(String nombreWord)
    {
        this.nombreWord = nombreWord;
    }

    /**
     * Retorna la variable plantillaWord
     *
     * @return plantillaWord
     */
    public String getPlantillaWord()
    {
        return plantillaWord;
    }

    /**
     * Asigna la variable plantillaWord
     *
     * @param plantillaWord
     * Variable a asignar en plantillaWord
     */
    public void setPlantillaWord(String plantillaWord)
    {
        this.plantillaWord = plantillaWord;
    }

    /**
     * Retorna la variable varVolver
     *
     * @return varVolver
     */
    public boolean isVarVolver()
    {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     *
     * @param varVolver
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver)
    {
        this.varVolver = varVolver;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    /**
     * Retorna la lista listaTipoRespuesta
     *
     * @return listaTipoRespuesta
     */
    public List<Registro> getListaTipoRespuesta()
    {
        return listaTipoRespuesta;
    }

    /**
     * Asigna la lista listaTipoRespuesta
     *
     * @param listaTipoRespuesta
     * Variable a asignar en listaTipoRespuesta
     */
    public void setListaTipoRespuesta(List<Registro> listaTipoRespuesta)
    {
        this.listaTipoRespuesta = listaTipoRespuesta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFormateado
     *
     * @return listaFormateado
     */
    public RegistroDataModelImpl getListaFormateado()
    {
        return listaFormateado;
    }

    /**
     * Asigna la lista listaFormateado
     *
     * @param listaFormateado
     * Variable a asignar en listaFormateado
     */
    public void setListaFormateado(RegistroDataModelImpl listaFormateado)
    {
        this.listaFormateado = listaFormateado;
    }

    /**
     * Retorna la lista listaClaseProblema
     *
     * @return listaClaseProblema
     */
    public RegistroDataModelImpl getListaClaseProblema()
    {
        return listaClaseProblema;
    }

    /**
     * Asigna la lista listaClaseProblema
     *
     * @param listaClaseProblema
     * Variable a asignar en listaClaseProblema
     */
    public void setListaClaseProblema(RegistroDataModelImpl listaClaseProblema)
    {
        this.listaClaseProblema = listaClaseProblema;
    }

    /**
     * Retorna la lista listaProblema
     *
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaClaseProblemaE()
    {
        return listaClaseProblemaE;
    }

    /**
     * Asigna la lista listaProblema
     *
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaClaseProblemaE(
        RegistroDataModelImpl listaClaseProblemaE)
    {
        this.listaClaseProblemaE = listaClaseProblemaE;
    }

    /**
     * Retorna la lista listaProblema
     *
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblema()
    {
        return listaProblema;
    }

    /**
     * Asigna la lista listaProblema
     *
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblema(RegistroDataModelImpl listaProblema)
    {
        this.listaProblema = listaProblema;
    }

    /**
     * Retorna la lista listaProblema
     *
     * @return listaProblema
     */
    public RegistroDataModelImpl getListaProblemaE()
    {
        return listaProblemaE;
    }

    /**
     * Asigna la lista listaProblema
     *
     * @param listaProblema
     * Variable a asignar en listaProblema
     */
    public void setListaProblemaE(RegistroDataModelImpl listaProblemaE)
    {
        this.listaProblemaE = listaProblemaE;
    }

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto()
    {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto)
    {
        this.listaConcepto = listaConcepto;
    }

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConceptoE()
    {
        return listaConceptoE;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE)
    {
        this.listaConceptoE = listaConceptoE;
    }

    /**
     * Retorna la lista listaDocumento
     *
     * @return listaDocumento
     */
    public RegistroDataModelImpl getListaDocumento()
    {
        return listaDocumento;
    }

    /**
     * Asigna la lista listaDocumento
     *
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumento(RegistroDataModelImpl listaDocumento)
    {
        this.listaDocumento = listaDocumento;
    }

    /**
     * Retorna la lista listaDocumento
     *
     * @return listaDocumento
     */
    public RegistroDataModelImpl getListaDocumentoE()
    {
        return listaDocumentoE;
    }

    /**
     * Asigna la lista listaDocumento
     *
     * @param listaDocumento
     * Variable a asignar en listaDocumento
     */
    public void setListaDocumentoE(RegistroDataModelImpl listaDocumentoE)
    {
        this.listaDocumentoE = listaDocumentoE;
    }

    /**
     * Retorna la lista listaProblemasSe
     *
     * @return listaProblemasSe
     */
    public RegistroDataModelImpl getListaProblemasSe()
    {
        return listaProblemasSe;
    }

    /**
     * Asigna la lista listaProblemasSe
     *
     * @param listaProblemasSe
     * Variable a asignar en listaProblemasSe
     */
    public void setListaProblemasSe(RegistroDataModelImpl listaProblemasSe)
    {
        this.listaProblemasSe = listaProblemasSe;
    }

    /**
     * Retorna la lista listaProblemasSe
     *
     * @return listaProblemasSe
     */
    public RegistroDataModelImpl getListaProblemasSeE()
    {
        return listaProblemasSeE;
    }

    /**
     * Asigna la lista listaProblemasSe
     *
     * @param listaProblemasSe
     * Variable a asignar en listaProblemasSe
     */
    public void setListaProblemasSeE(RegistroDataModelImpl listaProblemasSeE)
    {
        this.listaProblemasSeE = listaProblemasSeE;
    }

    /**
     * Retorna la lista listaCodigoRuta
     *
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta()
    {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     *
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta)
    {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    /**
     * Retorna la lista listaAforador
     *
     * @return listaAforador
     */
    public RegistroDataModelImpl getListaAforador()
    {
        return listaAforador;
    }

    /**
     * Asigna la lista listaAforador
     *
     * @param listaAforador
     * Variable a asignar en listaAforador
     */
    public void setListaAforador(RegistroDataModelImpl listaAforador)
    {
        this.listaAforador = listaAforador;
    }

    /**
     * Retorna la lista listaDependenciaEnv
     *
     * @return listaDependenciaEnv
     */
    public RegistroDataModelImpl getListaDependenciaEnv()
    {
        return listaDependenciaEnv;
    }

    /**
     * Asigna la lista listaDependenciaEnv
     *
     * @param listaDependenciaEnv
     * Variable a asignar en listaDependenciaEnv
     */
    public void setListaDependenciaEnv(
        RegistroDataModelImpl listaDependenciaEnv)
    {
        this.listaDependenciaEnv = listaDependenciaEnv;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubdordenservicio
     *
     * @return listaSubdordenservicio
     */
    public List<Registro> getListaSubdordenservicio()
    {
        return listaSubdordenservicio;
    }

    /**
     * Asigna la lista listaSubdordenservicio
     *
     * @param listaSubdordenservicio
     * Variable a asignar en listaSubdordenservicio
     */
    public void setListaSubdordenservicio(
        List<Registro> listaSubdordenservicio)
    {
        this.listaSubdordenservicio = listaSubdordenservicio;
    }

    /**
     * Retorna la lista listaSubordendocpresentado
     *
     * @return listaSubordendocpresentado
     */
    public List<Registro> getListaSubordendocpresentado()
    {
        return listaSubordendocpresentado;
    }

    /**
     * Asigna la lista listaSubordendocpresentado
     *
     * @param listaSubordendocpresentado
     * Variable a asignar en listaSubordendocpresentado
     */
    public void setListaSubordendocpresentado(
        List<Registro> listaSubordendocpresentado)
    {
        this.listaSubordendocpresentado = listaSubordendocpresentado;
    }

    /**
     * Retorna la lista listaSeguimientosub
     *
     * @return listaSeguimientosub
     */
    public List<Registro> getListaSeguimientosub()
    {
        return listaSeguimientosub;
    }

    /**
     * Asigna la lista listaSeguimientosub
     *
     * @param listaSeguimientosub
     * Variable a asignar en listaSeguimientosub
     */
    public void setListaSeguimientosub(List<Registro> listaSeguimientosub)
    {
        this.listaSeguimientosub = listaSeguimientosub;
    }

    /**
     * Retorna la lista listaOrdentrabajonovedades
     *
     * @return listaOrdentrabajonovedades
     */
    public List<Registro> getListaOrdentrabajonovedades()
    {
        return listaOrdentrabajonovedades;
    }

    /**
     * Asigna la lista listaOrdentrabajonovedades
     *
     * @param listaOrdentrabajonovedades
     * Variable a asignar en listaOrdentrabajonovedades
     */
    public void setListaOrdentrabajonovedades(
        List<Registro> listaOrdentrabajonovedades)
    {
        this.listaOrdentrabajonovedades = listaOrdentrabajonovedades;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSubDOrdenServicio
     *
     * @return registroSubSubDOrdenServicio
     */
    public Registro getRegistroSubSubDOrdenServicio()
    {
        return registroSubSubDOrdenServicio;
    }

    /**
     * Asigna el objeto registroSubSubDOrdenServicio
     *
     * @param registroSubSubDOrdenServicio
     * Variable a asignar en registroSubSubDOrdenServicio
     */
    public void setRegistroSubSubDOrdenServicio(
        Registro registroSubSubDOrdenServicio)
    {
        this.registroSubSubDOrdenServicio = registroSubSubDOrdenServicio;
    }

    /**
     * Retorna el objeto registroSubSubOrdenDocPresentado
     *
     * @return registroSubSubOrdenDocPresentado
     */
    public Registro getRegistroSubSubOrdenDocPresentado()
    {
        return registroSubSubOrdenDocPresentado;
    }

    /**
     * Asigna el objeto registroSubSubOrdenDocPresentado
     *
     * @param registroSubSubOrdenDocPresentado
     * Variable a asignar en registroSubSubOrdenDocPresentado
     */
    public void setRegistroSubSubOrdenDocPresentado(
        Registro registroSubSubOrdenDocPresentado)
    {
        this.registroSubSubOrdenDocPresentado = registroSubSubOrdenDocPresentado;
    }

    /**
     * Retorna el objeto registroSubSeguimientoSub
     *
     * @return registroSubSeguimientoSub
     */
    public Registro getRegistroSubSeguimientoSub()
    {
        return registroSubSeguimientoSub;
    }

    /**
     * Asigna el objeto registroSubSeguimientoSub
     *
     * @param registroSubSeguimientoSub
     * Variable a asignar en registroSubSeguimientoSub
     */
    public void setRegistroSubSeguimientoSub(
        Registro registroSubSeguimientoSub)
    {
        this.registroSubSeguimientoSub = registroSubSeguimientoSub;
    }

    /**
     * Retorna el objeto registroSubOrdenTrabajoNovedades
     *
     * @return registroSubOrdenTrabajoNovedades
     */
    public Registro getRegistroSubOrdenTrabajoNovedades()
    {
        return registroSubOrdenTrabajoNovedades;
    }

    /**
     * Asigna el objeto registroSubOrdenTrabajoNovedades
     *
     * @param registroSubOrdenTrabajoNovedades
     * Variable a asignar en registroSubOrdenTrabajoNovedades
     */
    public void setRegistroSubOrdenTrabajoNovedades(
        Registro registroSubOrdenTrabajoNovedades)
    {
        this.registroSubOrdenTrabajoNovedades = registroSubOrdenTrabajoNovedades;
    }

    // </SET_GET_ADICIONALES>

}
