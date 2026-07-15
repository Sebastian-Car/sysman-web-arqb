package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.precontractual.enums.AnexosestudiospreviosControladorEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 22/06/2016
 * @author jcrodriguez, Refactoring y depuracion
 * @version 2, 22/08/2017
 */
@ManagedBean
@ViewScoped
public class AnexosestudiospreviosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String codigo;
    private String condicion;
    private StreamedContent archivoDescarga;
    private String titulo;
    private String vigencia;
    private String tipoContrato;
    private String transaccion;
    private String consecutivo;
    private boolean esCreador;
    private String ruta;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of AnexosestudiospreviosControlador
     */
    public AnexosestudiospreviosControlador() {
        super();
        compania = SessionUtil.getCompania();

        Map<String, Object> parametros = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.ANEXOSESTUDIOSPREVIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            codigo = validarCadena(parametros, GeneralParameterEnum.CODIGO
                            .getName().toLowerCase());
            vigencia = validarCadena(parametros, GeneralParameterEnum.VIGENCIA
                            .getName().toLowerCase());
            titulo = validarCadena(parametros,
                            AnexosestudiospreviosControladorEnum.TITULO
                                            .getValue().toLowerCase());
            esCreador = Boolean.parseBoolean(
                            validarCadena(parametros, "esCreador"));
            tipoContrato = validarCadena(parametros, "tipoContrato");
            transaccion = validarCadena(parametros,
                            AnexosestudiospreviosControladorEnum.TRANSACCION
                                            .getValue().toLowerCase());
            consecutivo = validarCadena(parametros,
                            GeneralParameterEnum.CONSECUTIVO.getName()
                                            .toLowerCase());

        }
        catch (Exception ex) {
            Logger.getLogger(AnexosestudiospreviosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String validarCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    @PostConstruct
    public void inicializar() {
        if (AnexosestudiospreviosControladorEnum.MENU190207.getValue()
                        .equals(SessionUtil.getMenuActual())) {
            enumBase = GenericUrlEnum.ARCHIVOS_ES_ESTPREVIO;
            ruta = getParametro(idioma.getString("TB_TB3483"), false);
        }
        else {
            enumBase = GenericUrlEnum.ARCHIVOS_D_TRANSACCION;
            ruta = getParametro(idioma.getString("TB_TB3486"), true);
        }
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if (AnexosestudiospreviosControladorEnum.MENU190207.getValue()
                        .equals(SessionUtil.getMenuActual())) {
            parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                            codigo);
            parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                            vigencia);
        }
        else {
            parametrosListado
                            .put(AnexosestudiospreviosControladorEnum.TIPOCONTRATO
                                            .getValue(), tipoContrato);
            parametrosListado
                            .put(AnexosestudiospreviosControladorEnum.TRANSACCION
                                            .getValue(), transaccion);
            parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
        }
    }

    public String getCodigoEstudio() {
        return codigo;
    }

    public void setCodigoEstudio(String codigoEstudio) {
        this.codigo = codigoEstudio;
    }

    public void oprimirdescargar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        ContenedorArchivo contenedorArchivo = getContenedorArchivo(reg);
        try (FileInputStream inputStream = new FileInputStream(
                        contenedorArchivo.getArchivo())) {
            byte[] vec = new byte[(int) contenedorArchivo.getArchivo()
                            .length()];
            inputStream.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec),
                            validarCadena(reg.getCampos(),
                                            AnexosestudiospreviosControladorEnum.ARCHIVO
                                                            .getValue()));
        }
        catch (IOException | JRException ex) {
            Logger.getLogger(AnexosestudiospreviosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            SysmanFunciones.concatenar(
                                            idioma.getString("TB_TB1959"), " ",
                                            contenedorArchivo.getArchivo()
                                                            .getAbsolutePath()));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarArchivolector(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            String nombreArch = event.getFile().getFileName();
            nombreArch = nombreArch.contains(File.separator)
                ? nombreArch.substring(
                                nombreArch.lastIndexOf(File.separator) + 1,
                                nombreArch.length())
                : nombreArch;
            String rutaAux = generarRuta();
            if (rutaAux != null) {
                JsfUtil.upload(event.getFile().getInputstream(), nombreArch,
                                rutaAux);
                registro.getCampos()
                                .put(AnexosestudiospreviosControladorEnum.ARCHIVO
                                                .getValue(), nombreArch);
                agregarRegistroNuevo(null);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2188")
                                .replace("$#parametrouta$#",
                                                idioma.getString("TB_TB3483")));
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (IOException ex) {
            Logger.getLogger(AnexosestudiospreviosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // heredado del bean base
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        if (AnexosestudiospreviosControladorEnum.MENU190207.getValue()
                        .equals(SessionUtil.getMenuActual())) {
            registro.getCampos()
                            .put(AnexosestudiospreviosControladorEnum.COD_ESTUDIO
                                            .getValue(), codigo);
            registro.getCampos()
                            .remove(AnexosestudiospreviosControladorEnum.CONSECUTIVODETALLE
                                            .getValue());
            registro.getCampos()
                            .remove(AnexosestudiospreviosControladorEnum.TIPOCONTRATO
                                            .getValue());
            registro.getCampos()
                            .remove(AnexosestudiospreviosControladorEnum.TRANSACCION
                                            .getValue());
        }
        else {
            registro.getCampos()
                            .remove(AnexosestudiospreviosControladorEnum.COD_ESTUDIO
                                            .getValue());
            registro.getCampos()
                            .put(AnexosestudiospreviosControladorEnum.TIPOCONTRATO
                                            .getValue(), tipoContrato);
            registro.getCampos()
                            .put(AnexosestudiospreviosControladorEnum.TRANSACCION
                                            .getValue(), transaccion);
            registro.getCampos()
                            .put(AnexosestudiospreviosControladorEnum.CONSECUTIVODETALLE
                                            .getValue(), consecutivo);
        }

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
        ContenedorArchivo contenedorArchivo = getContenedorArchivo(registro);
        return contenedorArchivo.getArchivo().delete() ? true : false;

    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre,
                            SessionUtil.getModulo(), new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    public String generarRuta() {
        if (ruta != null) {
            String auxRuta = SysmanFunciones.concatenar(ruta, File.separator,
                            codigo.replace(" ", ""), File.separator);
            File file = new File(auxRuta);
            file.mkdirs();

            return auxRuta;

        }
        else {
            return "";
        }
    }

    public ContenedorArchivo getContenedorArchivo(Registro reg) {
        ContenedorArchivo contenedorArchivo = new ContenedorArchivo();
        contenedorArchivo.setArchivo(new File(
                        generarRuta() + validarCadena(reg.getCampos(),
                                        AnexosestudiospreviosControladorEnum.ARCHIVO
                                                        .getValue())));
        return contenedorArchivo;
    }

    public String getVigencia() {
        return condicion;
    }

    public void setVigencia(String vigencia) {
        this.condicion = vigencia;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(String transaccion) {
        this.transaccion = transaccion;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

}