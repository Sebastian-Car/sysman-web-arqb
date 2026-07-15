package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.enums.ComprobanteDiferidoControladorEnum;
import com.sysman.contabilidad.enums.ComprobanteDiferidoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @version 1, 20/04/2016
 * @modified jsforero
 * @version 2. 06/04/2017 Se realizo el refactory. Ademas se hicieron
 * las respectivas Correcciones del sonar.
 * @version 3. 20/04/2017 Se adaptan llamados a EJBs
 * @author cmanrique
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class ComprobanteDiferidoControlador extends BeanBaseModal {
    private final String compania;
    private final String usuario;
    private int mes;
    private int trimestre;
    private int semestre;
    private String terceroInicial;
    private String terceroFinal;
    private String companiaSeleccionada;
    private int anio;
    private String nomTerceroInicial;
    private String nomTerceroFinal;
    private List<Registro> listaCompania;
    private List<Registro> listaAno;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private String modulo;
    private Registro reg;
    private Date fechacorte;
    private String descripcion;
    private StreamedContent archivoDescarga;
    private boolean mostrarDialogo;
    private boolean mostrarDialogoDeterioro;
    private boolean verficado;
    private String mesesVencidos;
    private String respuesta;
    private String textoDialogo;
    private String periodicidad;
    private String funcion;   
   
	/**
     * Atributo que identifica la casilla de verificacion que indica
     * si se muestra la periodicidad por meses
     */
    private boolean porMes;
    
    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si se muestra la periodicidad por trimestre
     */
    private boolean porTrimestre;
    
    /**
     * Atributo que identifica la casilla de verificacion que indica
     * si se muestra la periodicidad por semestre
     */
    private boolean porSemestre;
    
    /**
     * Atributo que identifica si el deterioro de cartera maneja periodicidad
     */
    private boolean manejaPeriodicidad;    
    

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

    /**
     * Creates a new instance of ComprobanteDiferidoControlador
     */
    public ComprobanteDiferidoControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        verficado = false;
        mes = SysmanFunciones.getParteFecha(new Date(),
                        Calendar.MONTH)
            + 1;
        anio = SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR);
        terceroInicial = "0";
        terceroFinal = SysmanConstantes.CONS_TERCERO;

        mostrarDialogoDeterioro = false;        
        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTE_DIFERIDO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ComprobanteDiferidoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        cargarListaCompania();
        cargarListaAno();
        cargarListaTerceroInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
    	companiaSeleccionada = compania;
    	periodicidad = "Mes:";
        porMes = true;
        porTrimestre = false;
        porSemestre = false;
        trimestre = 1;
        semestre = 1;
    	try {
			manejaPeriodicidad = "SI".equals(SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA PERIODICIDAD EN DETERIORO CARTERA",
							SessionUtil.getModulo(), new Date(), true), "NO"));
			
			funcion = ejbSysmanUtil.consultarParametro(
                    compania,
                    "FUNCION CALCULO DE DETERIORO",
                    SessionUtil.getModulo(),
                    new Date(), true);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
    
    /**
     * Metodo ejecutado al cambiar el control PorMes
     *
     */
    public void cambiarckMensual() 
    {        
    	periodicidad = "Mes:";
    	porTrimestre = false;
    	porSemestre = false;    	
    }
    
    /**
     * Metodo ejecutado al cambiar el control PorTrimestre
     *
     */
    public void cambiarckTrimestral() 
    {    
    	periodicidad = "Trimestre:";
    	porMes = false;
    	porSemestre = false;
    }
    
    /**
     * Metodo ejecutado al cambiar el control PorSemestre
     *
     */
    public void cambiarckSemestral() 
    {    
    	periodicidad = "Semestre:";
    	porMes = false;
    	porTrimestre = false;
    } 

    public void cargarListaCompania() {
        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteDiferidoControladorUrlEnum.URL3778
                                                            .getValue());
            listaCompania = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), null));
        }
        catch (SystemException ex) {
            Logger.getLogger(ComprobanteDiferidoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            ComprobanteDiferidoControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));

        }

    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ComprobanteDiferidoControladorUrlEnum.URL4162
                                                            .getValue());
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param));
        }
        catch (SystemException ex) {
            Logger.getLogger(ComprobanteDiferidoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            ComprobanteDiferidoControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }

    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobanteDiferidoControladorUrlEnum.URL4465
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");

    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobanteDiferidoControladorUrlEnum.URL5040
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(ComprobanteDiferidoControladorEnum.TERCEROINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");

    }

    public void oprimirAceptar() {
        respuesta = null;
        archivoDescarga = null;
        Date fechaCorte;
        
        verificarPeriodicidad();
        
        if (!verficado) {
            mostrarDialogo = true;
        }        
        else {
            try {            	
                fechaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                .convertirAFecha("01/" + mes + "/"
                                    + anio));

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.FECHA.getName(), fechaCorte);

                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ComprobanteDiferidoControladorUrlEnum.URL88001
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (rs == null) {
                    JsfUtil.agregarMensajeAlerta(
                                    "No se encontró tasas de interés configuradas, se debe verificar para continuar con el proceso");
                }
                else {

                    Map<String, Object> param2 = new TreeMap<>();

                    param2.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param2.put(GeneralParameterEnum.ANO.getName(), anio);
                    param2.put(GeneralParameterEnum.MES.getName(), mes);

                    rs = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ComprobanteDiferidoControladorUrlEnum.URL1865001
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param2));

                    if (rs != null) {

                        mostrarDialogoDeterioro = true;

                        textoDialogo = idioma.getString("TB_TB4363").replace(
                                        "#PERIODO#",
                                        anio + " - " + ejbSysmanUtil
                                                        .mostrarNombreDeMes(
                                                                        mes));

                    }
                    else {

						ejecturaDeterioroCuentaH(fechaCorte);
                    }

                }
            }
            catch (ParseException | SystemException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    private void ejecturaDeterioroCuentaH(Date fechaCorte) {

        String cadena = "";
        archivoDescarga = null;
        try {
            cadena = ejbContabilidadCuatro.deterioroCuentaH(compania, anio, mes,
                            fechaCorte,
                            usuario, funcion);

            Map<String, Object> reemplazar = new TreeMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);

            String strSql = Reporteador.resuelveConsulta(
                            "800431ComprobantesDeterioro",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            if (!cadena.isEmpty()) {

            	ByteArrayInputStream salidaNombrePlano = null;
            	ByteArrayInputStream salidaNombreExcel = null;
            	ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
                String[] nombresArchivos = new String[2];

                salidaNombrePlano = JsfUtil.serializarPlano(cadena);
                
                try {
                	salidaNombreExcel = JsfUtil.serializarHojaDatos(strSql,
	                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
                }
                catch (SysmanException | JRException | IOException | DRException
                                | SQLException e1) {
                    logger.error(e1.getMessage(), e1);
                    JsfUtil.agregarMensajeError(e1.getMessage());
                }
                
                int cantidad = 0;
                if (salidaNombrePlano != null) {
                    salidas[cantidad] = salidaNombrePlano;
                    nombresArchivos[cantidad]  = "CONFIGURACION TASA.txt";
                    cantidad++;
                }
                
                if (salidaNombreExcel != null) {
                    salidas[cantidad] = salidaNombreExcel;
                    nombresArchivos[cantidad]  = "ComprobantesDeterioro.xlsx";
                    cantidad++;
                }
                
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                        salidas, nombresArchivos);
            }
            else {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                                "ComprobantesDeterioro");

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }

        }
        catch (SystemException | IOException | JRException | SQLException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerificarConfiguracion en
     * la vista
     *
     *
     */
    public void oprimirVerificarConfiguracion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Date fechaCorte;
        try {
        	fechaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + anio));

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.FECHA.getName(), fechaCorte);

            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ComprobanteDiferidoControladorUrlEnum.URL88001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null) {

                verficado = true;

                Map<String, Object> reemplazar = new TreeMap<>();

                reemplazar.put("fecha", SysmanFunciones
                                .convertirAFechaCadena(fechaCorte));
                reemplazar.put("compania", compania);
                reemplazar.put("anio", anio);

                String strSql = Reporteador.resuelveConsulta(
                                "800430VerificacionTasas",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                                "Verificacion");

            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                "No se encontró tasas de interés configuradas, se debe verificar para continuar con el proceso");
            }
        }
        catch (ParseException | SystemException | JRException | IOException
                        | SQLException | DRException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    
    private void verificarPeriodicidad()
    {
    	if(manejaPeriodicidad & porTrimestre)
    	{
        	switch (trimestre) {
            case 1:
                mes = 3;
                break;
            case 2:
            	mes = 6;
                break;
            case 3:
            	mes = 9;
                break;
            case 4:
            	mes = 12;
                break;
            default:
                break;
            }
    	}
    	else if(manejaPeriodicidad & porSemestre)
    	{
        	switch (semestre) {
            case 1:
                mes = 6;
                break;
            case 2:
            	mes = 12;
                break;
            default:
                break;
            }
    	}
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        // Verifica si la entidad aplica NIIF

        String informe = "000644ComprobanteDiferidoNIIF";
        try {
            if ("SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "ENTIDAD APLICA NIIF",
                            SessionUtil.getModulo(),
                            new Date(), true))) {
                if (param()) {
                    return;
                }

                fechacorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                .convertirAFecha(SysmanFunciones.concatenar(
                                                "01/", String.valueOf(mes), "/",
                                                String.valueOf(anio))));

                mesesVencidos = ejbSysmanUtil.consultarParametro(
                                compania,
                                "NUMERO DE MESES VENCIDOS QUE APLICAN DETERIORO NIIF",
                                SessionUtil.getModulo(),
                                new Date(), true);

                respuesta = ejbContabilidadCuatro.revisarDeterioroDeCartera(
                                companiaSeleccionada, fechacorte, anio,
                                terceroInicial, terceroFinal,
                                Integer.parseInt(mesesVencidos),
                                SessionUtil.getUser().getCodigo());

                if (!"OK".equals(respuesta)) {
                    JsfUtil.agregarMensajeInformativo(
                                    SysmanFunciones.concatenar(
                                                    idioma.getString(
                                                                    "TB_TB851"),
                                                    SysmanFunciones.convertirAFechaCadena(
                                                                    fechacorte,
                                                                    "dd/MM/yyyy")));
                    return;
                }
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB852"));
                mostrarDialogo = false;
                return;
            }

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            reemplazar.put("fechacorte",
                            SysmanFunciones.convertirAFechaCadena(fechacorte));

            reemplazar.put("mesesVencidos", mesesVencidos);
            reemplazar.put("companiaSeleccionada", companiaSeleccionada);

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_NOMBRECOMPANIA",
                            service.buscarEnLista(companiaSeleccionada,
                                            GeneralParameterEnum.CODIGO
                                                            .getName(),
                                            GeneralParameterEnum.NOMBRE
                                                            .getName(),
                                            listaCompania));

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ", informe));
            Logger.getLogger(EstadodetesoreriaaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException
                        | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            if ("OK".equals(respuesta)) {
                mostrarDialogo = true;
            }
        }

    }

    public boolean param() {
        try {
            // Verificaciï¿½n de periodo activo o cerrado

            String estadoperiodo = ejbSysmanUtil.verificarEstadoPeriodoMensual(
                            compania, anio, mes, Integer.parseInt(modulo), 1);

            if ("E".equals(estadoperiodo)) {
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString(
                                                ComprobanteDiferidoControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                                .getValue()),
                                idioma.getString("TB_TB848")));
                return true;
            }
            else if ("C".equals(estadoperiodo)) {
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString(
                                                ComprobanteDiferidoControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                                .getValue()),
                                idioma.getString("TB_TB849")));
                return true;
            }
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(ComprobanteDiferidoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(
                                            ComprobanteDiferidoControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            idioma.getString("TB_TB850")));
        }
        return false;
    }

    public void oprimirCancelar() {
        archivoDescarga = null;
        String cadena;

        Date fechaCorte;
        try {

            fechaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + anio));

            cadena = ejbContabilidadCuatro.contabilizarDeterioro(compania, anio,
                            mes,
                            fechaCorte,
                            usuario);

            if (!cadena.isEmpty()) {

                ArchivosBean.generarPlano("CONFIGURACION CUENTAS.txt", cadena);
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }

        }
        catch (SystemException | IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirbotonOculto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarseguirProceso() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcontinuar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarcontinuar() {
        // <CODIGO_DESARROLLADO>
        mostrarDialogo = false;
        Date fechaCorte;

        try {
            fechaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + anio));
            
            SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.FECHA.getName(), formatFecha.format(fechaCorte));

            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ComprobanteDiferidoControladorUrlEnum.URL88001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs == null) {
                JsfUtil.agregarMensajeAlerta(
                                "No se encontró tasas de interés configuradas, se debe verificar para continuar con el proceso");
            }
            else {

                Map<String, Object> param2 = new TreeMap<>();

                param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param2.put(GeneralParameterEnum.ANO.getName(), anio);
                param2.put(GeneralParameterEnum.MES.getName(), mes);

                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ComprobanteDiferidoControladorUrlEnum.URL1865001
                                                                                                .getValue())
                                                                .getUrl(),
                                                param2));

                if (rs != null) {

                    mostrarDialogoDeterioro = true;

                    textoDialogo = idioma.getString("TB_TB4363").replace(
                                    "#PERIODO#", anio + " - " + ejbSysmanUtil
                                                    .mostrarNombreDeMes(mes));

                }
                else {
                    ejecturaDeterioroCuentaH(fechaCorte);
                }

            }

        }
        catch (ParseException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarcontinuar() {
        // <CODIGO_DESARROLLADO>
        mostrarDialogo = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * EliminarDeterioroCartera en la vista
     *
     */
    public void aceptarEliminarDeterioroCartera() {
        // <CODIGO_DESARROLLADO>
        mostrarDialogoDeterioro = false;
        archivoDescarga = null;
        borrarDatosDeterioroCartera();

        Date fechaCorte;

        try {
            fechaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/"
                                + anio));

            ejecturaDeterioroCuentaH(fechaCorte);

        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private void borrarDatosDeterioroCartera() {

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), anio);
        params.put(GeneralParameterEnum.MES.getName(), mes);

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobanteDiferidoControladorUrlEnum.URL1865002
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * EliminarDeterioroCartera en la vista
     *
     */
    public void cancelarEliminarDeterioroCartera() {
        mostrarDialogoDeterioro = false;
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT").toString();
        nomTerceroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        terceroFinal = "";
        nomTerceroFinal = "";
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT").toString();
        nomTerceroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getCompaniaSeleccionada() {
        return companiaSeleccionada;
    }

    public void setCompaniaSeleccionada(String companiaSeleccionada) {
        this.companiaSeleccionada = companiaSeleccionada;
    }

    public String getNomTerceroInicial() {
        return nomTerceroInicial;
    }

    public void setNomTerceroInicial(String nomTerceroInicial) {
        this.nomTerceroInicial = nomTerceroInicial;
    }

    public String getNomTerceroFinal() {
        return nomTerceroFinal;
    }

    public void setNomTerceroFinal(String nomTerceroFinal) {
        this.nomTerceroFinal = nomTerceroFinal;
    }

    public List<Registro> getListaCompania() {
        return listaCompania;
    }

    public void setListaCompania(List<Registro> listaCompania) {
        this.listaCompania = listaCompania;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public Registro getReg() {
        return reg;
    }

    public void setReg(Registro reg) {
        this.reg = reg;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMostrarDialogo() {
        return mostrarDialogo;
    }

    public void setMostrarDialogo(boolean mostrarDialogo) {
        this.mostrarDialogo = mostrarDialogo;
    }

    public String getMesesVencidos() {
        return mesesVencidos;
    }

    public void setMesesVencidos(String mesesVencidos) {
        this.mesesVencidos = mesesVencidos;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public boolean isMostrarDialogoDeterioro() {
        return mostrarDialogoDeterioro;
    }

    public void setMostrarDialogoDeterioro(boolean mostrarDialogoDeterioro) {
        this.mostrarDialogoDeterioro = mostrarDialogoDeterioro;
    }

    public String getTextoDialogo() {
        return textoDialogo;
    }

    public void setTextoDialogo(String textoDialogo) {
        this.textoDialogo = textoDialogo;
    }

	/**
	 * @return the porMes
	 */
	public boolean isPorMes() {
		return porMes;
	}

	/**
	 * @param porMes the porMes to set
	 */
	public void setPorMes(boolean porMes) {
		this.porMes = porMes;
	}

	/**
	 * @return the porTrimestre
	 */
	public boolean isPorTrimestre() {
		return porTrimestre;
	}

	/**
	 * @param porTrimestre the porTrimestre to set
	 */
	public void setPorTrimestre(boolean porTrimestre) {
		this.porTrimestre = porTrimestre;
	}

	/**
	 * @return the porSemestre
	 */
	public boolean isPorSemestre() {
		return porSemestre;
	}

	/**
	 * @param porSemestre the porSemestre to set
	 */
	public void setPorSemestre(boolean porSemestre) {
		this.porSemestre = porSemestre;
	}

	/**
	 * @return the manejaPeriodicidad
	 */
	public boolean isManejaPeriodicidad() {
		return manejaPeriodicidad;
	}

	/**
	 * @param manejaPeriodicidad the manejaPeriodicidad to set
	 */
	public void setManejaPeriodicidad(boolean manejaPeriodicidad) {
		this.manejaPeriodicidad = manejaPeriodicidad;
	}

	/**
	 * @return the periodicidad
	 */
	public String getPeriodicidad() {
		return periodicidad;
	}

	/**
	 * @param periodicidad the periodicidad to set
	 */
	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
	}

	/**
	 * @return the trimestre
	 */
	public int getTrimestre() {
		return trimestre;
	}

	/**
	 * @param trimestre the trimestre to set
	 */
	public void setTrimestre(int trimestre) {
		this.trimestre = trimestre;
	}

	/**
	 * @return the semestre
	 */
	public int getSemestre() {
		return semestre;
	}

	/**
	 * @param semestre the semestre to set
	 */
	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}
	
	 public String getFuncion() {
			return funcion;
		}

		public void setFuncion(String funcion) {
			this.funcion = funcion;
		}

}
