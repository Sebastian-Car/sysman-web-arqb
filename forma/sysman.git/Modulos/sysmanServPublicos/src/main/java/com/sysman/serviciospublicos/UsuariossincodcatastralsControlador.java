package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.UsuariossincodcatastralsControladorEnum;
import com.sysman.serviciospublicos.enums.UsuariossincodcatastralsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 22/08/2016
 * @modifier amonroy
 * @version 2, 21/06/2017 Proceso de Refactoring para la carga del
 * formulario y la operacion de actualizacion
 */
@ManagedBean
@ViewScoped
public class UsuariossincodcatastralsControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private String ciclo;
    private String cicloTitulo;
    private StreamedContent archivoDescarga;
    private static final String CODIGOCATASTRAL = UsuariossincodcatastralsControladorEnum.CODIGOCATASTRAL
                    .getValue();

    /**
     * Creates a new instance of UsuariossincodcatastralsControlador
     */
    public UsuariossincodcatastralsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.USUARIOSSINCODCATASTRALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("CICLO").toString();
                cicloTitulo = "CICLO " + ciclo;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            UsuariossincodcatastralsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = UsuariossincodcatastralsControladorEnum.SP_USUARIO.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        UsuariossincodcatastralsControladorEnum.CICLOFILTRO
                                        .getValue(),
                        ciclo);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariossincodcatastralsControladorUrlEnum.URL001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariossincodcatastralsControladorUrlEnum.URL002
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        String reporte = "001043UsuariossinCodcatastral";

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", ciclo);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CICLO", ciclo);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        registro.getCampos()
                        .remove(UsuariossincodcatastralsControladorEnum.CODIGOINTERNO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());

        if (registro.getCampos().get(CODIGOCATASTRAL) != null
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CODIGOCATASTRAL)) {
            registro.getCampos().put(CODIGOCATASTRAL, registro.getCampos()
                            .get(CODIGOCATASTRAL).toString().replace("-", ""));
        }
        else {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1305")
                                            .replace("s$mascara$s", idioma
                                                            .getString("TB_TB3243"))
                                            .replace("s$campo$s", idioma
                                                            .getString("TG_CODIGO_CATASTRAL")));
            return false;
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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCicloTitulo() {
        return cicloTitulo;
    }

    public void setCicloTitulo(String cicloTitulo) {
        this.cicloTitulo = cicloTitulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
