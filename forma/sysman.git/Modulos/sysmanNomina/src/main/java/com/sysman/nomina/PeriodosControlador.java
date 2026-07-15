package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Usuario;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.PeriodosControladorEnum;
import com.sysman.nomina.enums.PeriodosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Se aplicaron las recomendaciones de SonarLint en el controlador.
 * 
 * @author pespitia
 * @version 0.2, 17/03/2017
 * 
 * Se paso texto quemado en el metodo evaluarPeriodo() a texto en
 * bean.
 * @author jlramirez
 * @version 0.3, 22/03/2017
 * 
 * @author jcrodriguez,Refacotring y depuracion
 * @version 0.4, 18/10/2017
 */
@ManagedBean
@ViewScoped

public class PeriodosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    
    private final String procesoSesion;
    private final String anioSesion;
    private final String usuarioValido = "PRUEBAS_SS";
    private int indice;
    private boolean permiteEditar = false;
    private Usuario usuario;
    /**
     * Atributo a nivel de clase que establece si se deben utilizar
     * los indicadores en el registro
     */
    private boolean indicadores;
    private String codigoProceso;
    private String nombreProceso;
    private String anoPreparar;
    private List<Registro> listaMes;
    private List<Registro> listaAno;
    private List<Registro> listaano1;
    private boolean estado;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;
    @EJB
    private EjbNominaDosRemote ejbNominaDos;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PeriodosControlador
     *
     */
    public PeriodosControlador() {
        numFormulario = GeneralCodigoFormaEnum.PERIODOS_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        procesoSesion = validarParamtroSession(
                        SessionUtil.getSessionVar("procesoNomina"));
        anioSesion = validarParamtroSession(
                        SessionUtil.getSessionVar("anioNomina"));
        codigoProceso = procesoSesion;
        
        usuario = SessionUtil.getUser();

        try {
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String validarParamtroSession(Object var) {
        return SysmanFunciones.validarVariableVacio(var.toString()) ? ""
            : var.toString();
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PERIODOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaMes();
        cargarListaAno();
        cargarListaano1();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(PeriodosControladorEnum.PROCESO_SESION.getValue(),
                        procesoSesion);

    }

    public void cargarListaMes() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioSesion);

        try {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosControladorUrlEnum.URL4964
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaAno() {
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosControladorUrlEnum.URL4925
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaano1() {

        try {
            listaano1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosControladorUrlEnum.URL4925
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirRetefuente() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Registro registroAno = null;
        String cadenaAno = "";
        if (SessionUtil.getNivelUsuario(SessionUtil.getModulo()) >= 9) {
            try {
                registroAno = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PeriodosControladorUrlEnum.URL4929
                                                                                .getValue())
                                                .getUrl(), param));
                cadenaAno = "".equals(
                                validarParametroCadena(registroAno.getCampos(),
                                                GeneralParameterEnum.NUMERO
                                                                .getName()))
                                                                    ? "9999"
                                                                    : validarParametroCadena(
                                                                                    registroAno.getCampos(),
                                                                                    GeneralParameterEnum.NUMERO
                                                                                                    .getName());
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if (SysmanFunciones.validarVariableVacio(anoPreparar)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2652"));
                return;
            }

            if (!cadenaAno.isEmpty()
                && Integer.valueOf(cadenaAno) >= Integer.valueOf(anoPreparar)
                    - 1) {

                try {

                    ejbNominaDos.retefuente(compania,
                                    Integer.parseInt(anoPreparar),
                                    Integer.parseInt(anioSesion),
                                    SessionUtil.getUser().getCodigo());

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2654")
                                                    .replace("#$anoPreparar#$",
                                                                    anoPreparar));

                }
                catch (NumberFormatException | SystemException ex) {
                    Logger.getLogger(PeriodosControlador.class.getName())
                                    .log(Level.SEVERE, null, ex);
                    JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                                    idioma.getString("TB_TB2655"), anoPreparar,
                                    ex.getMessage()));
                }
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2656"));
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3920").replace(
                                            "s$usuario$s",
                                            SessionUtil.getUser()
                                                            .getCodigo()));
        }

    }

    public void oprimirCredito(Registro reg, int indice) {
        // heredado del bean base
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        reemplazar.put("nombreEmpresa",
                        SessionUtil.getCompaniaIngreso().getNombre());
        reemplazar.put("compania", compania);
        reemplazar.put("iddeproceso", reg.getCampos()
                        .get(PeriodosControladorEnum.ID_DE_PROCESO.getValue()));
        reemplazar.put("anos", reg.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        reemplazar.put("mes", reg.getCampos()
                        .get(GeneralParameterEnum.MES.getName()));
        reemplazar.put("periodo", reg.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()));

        Reporteador.resuelveConsulta(
                        PeriodosControladorEnum.REPORTE001475.getValue(),
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar,
                        parametros);
        parametros.put("PR_NOMBREEMPRESA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            PeriodosControladorEnum.REPORTE001475.getValue(),
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            PeriodosControladorEnum.REPORTE001475.getValue()));
            Logger.getLogger(PeriodosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public int dateDiff(String tipo, Date dateInicio, Date dateFin) {

        Calendar fechaInicio = Calendar.getInstance();
        fechaInicio.setTime(dateInicio);

        Calendar fechaFin = Calendar.getInstance();
        fechaFin.setTime(dateFin);

        if ("D".equals(tipo)) {
            long diferenciaMiliSeg = fechaFin.getTimeInMillis()
                - fechaInicio.getTimeInMillis();

            long dias = diferenciaMiliSeg / (1000 * 60 * 60 * 24);

            return (int) dias;
        }
        return 0;
    }

    public Date ultimoDia(int dia, String mes, String anio)
                    throws ParseException {
        int auxMes = Integer.parseInt(mes);
        int auxAnio = Integer.parseInt(anio);

        auxMes++;

        if (auxMes > 12) {
            auxAnio++;
            auxMes = 1;
        }
        String fecha = SysmanFunciones.concatenar(String.valueOf(dia), "/",
                        String.valueOf(auxMes), "/", String.valueOf(auxAnio));

        return SysmanFunciones
                        .sumarRestarDiasFecha(
                                        SysmanFunciones.convertirAFecha(fecha,
                                                        PeriodosControladorEnum.FORMATO
                                                                        .getValue()),
                                        -1);
    }

    public void cambiarPeriodo() {
        calcularCampos();
    }

    public void cambiarAno() {
        calcularCampos();
    }

    public void cambiarMes() {
        calcularCampos();
    }

    private String validarParametroCadena(Map<String, Object> campos,
        String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    /**
     * Evalua el valor del campo 'PERIODO' y determina que proceso
     * ejecutar
     * 
     * @throws ParseException
     */
    private void evaluarPeriodo(Registro registro)
                    throws ParseException {
        String ano = validarParametroCadena(registro.getCampos(),
                        GeneralParameterEnum.ANO.getName());
        String mes = validarParametroCadena(registro.getCampos(),
                        GeneralParameterEnum.MES.getName());

        if ("1".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2983"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("15",
                                                            "/", mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            true, true, true);
        }
        else if ("2".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2984"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("16",
                                                            "/", mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            true, true, true);
        }
        else if ("3".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2985"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            true, true, true);
        }
        else if ("4".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2986"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("5".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, recuperarNomPeriodo(),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("6".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2987"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("7".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2988"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("8".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2989"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("9".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2990"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else if ("10".equals(registro.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()))) {
            asignarValoresPeriodo(registro, idioma.getString("TB_TB2991"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }
        else {

            asignarValoresPeriodo(registro, idioma.getString("TB_TB2992"),
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones.concatenar("1", "/",
                                                            mes, "/", ano),
                                            PeriodosControladorEnum.FORMATO
                                                            .getValue()),
                            ultimoDia(1, mes, ano),
                            false, true, true);
        }

    }

    /**
     * Recupera el nombre del periodo de la opcion 31 en la tabla
     * PARAMETROS_DE_ENTRADA.
     * 
     * @return
     */
    private String recuperarNomPeriodo() {
        String par31 = "";

        try {

            par31 = ejbNominaUno.getParametroNomina(compania, 31);
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return "890.481.123-1".equals(par31)
            ? PeriodosControladorEnum.VACACIONES.getValue()
            : PeriodosControladorEnum.RETROACTIVO.getValue();
    }

    /**
     * Asigna a los campos del resgitro los valores ingresados por
     * parametro.
     * 
     * @param registro
     * -> Referencia del registro que contiene el objeto clave-valor.
     * @param nombre
     * -> Valor con el nombre del periodo.
     * @param fInicial
     * -> Valor con la fecha de inicio.
     * @param fFinal
     * -> Valor con la fecha final.
     * @param diferidos
     * -> Indicador de diferidos.
     * @param acumulado
     * -> Indicador de acumulado.
     * @param estado
     * -> Indicador de estado.
     */
    private void asignarValoresPeriodo(Registro registro, String nombre,
        Date fInicial,
        Date fFinal, boolean diferidos, boolean acumulado, boolean estado) {
        registro.getCampos().put(PeriodosControladorEnum.NOM_PERIODO.getValue(),
                        nombre);
        registro.getCampos().put(PeriodosControladorEnum.FECHAINICIO.getValue(),
                        fInicial);
        registro.getCampos().put(PeriodosControladorEnum.FECHAFINAL.getValue(),
                        fFinal);

        if (indicadores) {
            registro.getCampos().put(
                            PeriodosControladorEnum.DIFERIDOS.getValue(),
                            diferidos);
            registro.getCampos().put(
                            PeriodosControladorEnum.ACUMULADO.getValue(),
                            acumulado);
            registro.getCampos().put(PeriodosControladorEnum.ESTADO.getValue(),
                            estado);
        }
    }

    public void calcularCampos() {

        boolean key = !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.ANO.getName())
            && registro.getCampos().get(GeneralParameterEnum.ANO.getName())
                            .toString().length() == 4
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            GeneralParameterEnum.MES.getName());

        try {
            if (key && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            GeneralParameterEnum.PERIODO.getName())) {

                /* Asignar indicadores */
                indicadores = true;
                evaluarPeriodo(registro);

                Date dateInicio = (Date) registro.getCampos()
                                .get(PeriodosControladorEnum.FECHAINICIO
                                                .getValue());

                Date dateFinal = (Date) registro.getCampos().get(
                                PeriodosControladorEnum.FECHAFINAL.getValue());

                registro.getCampos().put(
                                PeriodosControladorEnum.DIAS.getValue(),
                                SysmanFunciones
                                                .calcularDiferenciaDias(
                                                                dateInicio,
                                                                dateFinal)
                                    + 1);

                if ((int) registro.getCampos().get(PeriodosControladorEnum.DIAS
                                .getValue()) == 16) {
                    registro.getCampos().put(
                                    PeriodosControladorEnum.DIAS.getValue(),
                                    15);
                }
                else if ((int) registro.getCampos()
                                .get(PeriodosControladorEnum.DIAS
                                                .getValue()) == 31) {
                    registro.getCampos().put(
                                    PeriodosControladorEnum.DIAS.getValue(),
                                    30);
                }
            }
            else {
                registro.getCampos().put(
                                PeriodosControladorEnum.DIAS.getValue(), null);
                registro.getCampos().put(
                                PeriodosControladorEnum.FECHAINICIO.getValue(),
                                null);
                registro.getCampos().put(
                                PeriodosControladorEnum.FECHAFINAL.getValue(),
                                null);
                registro.getCampos().put(
                                PeriodosControladorEnum.NOM_PERIODO.getValue(),
                                null);

            }
            
            if ("SI".equals(getParametro(
                    "MANEJA NOMINA HIBRIDA",
                    true)) && !usuario.getCodigo().equalsIgnoreCase(usuarioValido)) {
            	if(SysmanFunciones.nvl(Integer.parseInt(registro.getCampos().get("PERIODO").toString()),0) >= 1 
                        &&  SysmanFunciones.nvl(Integer.parseInt(registro.getCampos().get("PERIODO").toString()),0) <= 3 ){
            		permiteEditar = true;
            	}else {
            		permiteEditar = false;
            	}
            }
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarMesC(int rowNum) throws ParseException {
        calcularCamposContinuo(rowNum);

    }

    public void cambiarAnoC(int rowNum) throws ParseException {
        calcularCamposContinuo(rowNum);

    }

    public void cambiarPeriodoC(int rowNum) throws ParseException {
        calcularCamposContinuo(rowNum);

    }

    public void cambiarEstado() {
        // heredado del bean base
    }

    public void cambiarEstadoC(int rowNum) {
        // heredado del bean base
    }

    public void calcularCamposContinuo(int rowNum) throws ParseException {
        Map<String, Object> campos = listaInicial.getDatasource()
                        .get(rowNum % 10)
                        .getCampos();

        boolean key = !SysmanFunciones.validarCampoVacio(campos,
                        GeneralParameterEnum.ANO.getName())
            && campos.get(GeneralParameterEnum.ANO.getName()).toString()
                            .length() == 4
            && !SysmanFunciones.validarCampoVacio(campos,
                            GeneralParameterEnum.MES.getName());

        if (key && !SysmanFunciones.validarCampoVacio(campos,
                        GeneralParameterEnum.PERIODO.getName())) {

            /* No asignar en los indicadores */
            indicadores = false;
            evaluarPeriodo(listaInicial.getDatasource().get(rowNum % 10));

            Date dateInicio = (Date) listaInicial.getDatasource()
                            .get(rowNum % 10).getCampos()
                            .get(PeriodosControladorEnum.FECHAINICIO
                                            .getValue());
            Date dateFinal = (Date) listaInicial.getDatasource()
                            .get(rowNum % 10).getCampos()
                            .get(PeriodosControladorEnum.FECHAFINAL.getValue());

            listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .put(PeriodosControladorEnum.DIAS.getValue(),
                                            SysmanFunciones.calcularDiferenciaDias(
                                                            dateInicio,
                                                            dateFinal)
                                                + 1);

            if ((int) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get(PeriodosControladorEnum.DIAS
                                            .getValue()) == 16) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(PeriodosControladorEnum.DIAS.getValue(),
                                                15);
            }
            else if ((int) listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get(PeriodosControladorEnum.DIAS
                                            .getValue()) == 31) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(PeriodosControladorEnum.DIAS.getValue(),
                                                30);
            }

        }
        else {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(PeriodosControladorEnum.DIAS.getValue(), null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(PeriodosControladorEnum.FECHAINICIO.getValue(),
                                            null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(PeriodosControladorEnum.FECHAFINAL.getValue(),
                                            null);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(PeriodosControladorEnum.NOM_PERIODO.getValue(),
                                            null);

        }
        

    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        
        
        if ("SI".equals(getParametro(
                "MANEJA NOMINA HIBRIDA",
                true)) && !usuario.getCodigo().equalsIgnoreCase(usuarioValido)) {
        	int periodoaux = SysmanFunciones.nvl(Integer.parseInt(listaInicial.getDatasource().get(indice).getCampos().get("PERIODO").toString()),0);
        	if(periodoaux >= 1 &&  periodoaux <= 3 ){
        		permiteEditar = true;
        		JsfUtil.ejecutarJavaScript("setTimeout(function() {$('.ui-inputtext').filter('input[type=\"text\"]').each(function() {if($(this).val() == 'Mensual' || $(this).val() == 'Primera Quincena' || $(this).val() == 'Segunda Quincena'){$(this).prop('disabled', true); $(this).addClass('ui-state-disabled'); } }); }, 50);");
        	}else{
        		permiteEditar = false;

        	}
        }
        
    }
    

    @Override
    public void removerCombos() {
        // heredado del bean base
    }
    
    /**
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            "6", new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    @Override
    public void abrirFormulario() {
        anoPreparar = String.valueOf(SysmanFunciones.ano(new Date()));
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PeriodosControladorEnum.ID_DE_PROCESO.getValue(),
                        codigoProceso);
        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PeriodosControladorUrlEnum.URL4931
                                                                            .getValue())
                                            .getUrl(), param));

            nombreProceso = validarParametroCadena(reg.getCampos(),
                            PeriodosControladorEnum.NOMBRE_PROCESO.getValue());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // heredado del bean base
        listaInicial.getDatasource().get(indice % 10).getCampos().put(
                        PeriodosControladorEnum.ESTADO.getValue(),
                        estado ? "TRUE" : "FALSE");
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        PeriodosControladorEnum.ID_DE_PROCESO.getValue(),
                        procesoSesion);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    private boolean validacionActInsertar() {
        estado = !Boolean
                        .parseBoolean(registro.getCampos()
                                        .get(PeriodosControladorEnum.ESTADO
                                                        .getValue())
                                        .toString());
        if (estado) {
            registro.getCampos().put(PeriodosControladorEnum.ESTADO.getValue(),
                            true);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2657"));
            return true;
        }
        else {
            if (SessionUtil.getNivelUsuario(SessionUtil.getModulo()) >= 9) {
                registro.getCampos().put(
                                PeriodosControladorEnum.ESTADO.getValue(),
                                true);
                try {
                    String agregarMensaje = idioma.getString("TB_TB3880")
                                    .replace(
                                                    "s$id_proceso$s",
                                                    codigoProceso)
                                    .replace("s$ano$s",
                                                    registro.getCampos()
                                                                    .get(GeneralParameterEnum.ANO
                                                                                    .getName())
                                                                    .toString())
                                    .replace("s$mes$s", ejbSysmanUtil
                                                    .mostrarNombreDeMes(Integer
                                                                    .parseInt(registro
                                                                                    .getCampos()
                                                                                    .get(GeneralParameterEnum.MES
                                                                                                    .getName())
                                                                                    .toString())))
                                    .replace("s$periodo$s",
                                                    registro.getCampos()
                                                                    .get(GeneralParameterEnum.PERIODO
                                                                                    .getName())
                                                                    .toString());
                    JsfUtil.agregarMensajeInformativo(agregarMensaje);
                }
                catch (SystemException | NumberFormatException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            else {
                registro.getCampos().put(
                                PeriodosControladorEnum.ESTADO.getValue(),
                                false);
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB3919").replace(
                                                "s$usuario$s",
                                                SessionUtil.getUser()
                                                                .getCodigo()));
                return true;

            }
        }
        return false;

    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

        if (validacionActInsertar()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        if (SessionUtil.getNivelUsuario(SessionUtil.getModulo()) < 9) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3915").replace(
                                            "s$usuario$s",
                                            SessionUtil.getUser()
                                                            .getCodigo()));
            return false;
        }
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // heredado del bean base
    }

    public String getNombreProceso() {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }

    public String getCodigoProceso() {
        return codigoProceso;
    }

    public void setCodigoProceso(String codigoProceso) {
        this.codigoProceso = codigoProceso;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public String getAnoPreparar() {
        return anoPreparar;
    }

    public void setAnoPreparar(String anoPreparar) {
        this.anoPreparar = anoPreparar;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaano1() {
        return listaano1;
    }

    public void setListaano1(List<Registro> listaano1) {
        this.listaano1 = listaano1;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }
    
    public boolean isPermiteEditar() {
        return permiteEditar;
    }
    public void setPermiteEditar(boolean permiteEditar) {
        this.permiteEditar = permiteEditar;
    }

}
