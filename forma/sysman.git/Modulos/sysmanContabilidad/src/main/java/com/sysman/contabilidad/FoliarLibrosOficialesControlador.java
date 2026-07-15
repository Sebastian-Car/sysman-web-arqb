package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @version 1, 11/05/2016
 * @modified spina 10/04/2017 - se refactoriza para DSS y depuracion
 * sonar se elimina el boton para generar reporte en excel ya que no
 * es necesario para este proceso
 */
@ManagedBean
@ViewScoped
public class FoliarLibrosOficialesControlador extends BeanBaseModal {
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean mostrarLogo;
    private boolean datoCompania;
    private String tipoLibro;
    private String tamano;
    private String orientacion;
    private int numInicial;
    private int numFinal;
    private String codLibro;
    private StreamedContent archivoDescarga;
    private String informe;
    private boolean mostrarCodigo;
    private String nombreLibro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FoliarLibrosOficialesControlador
     */
    public FoliarLibrosOficialesControlador() {
        super();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FOLIAR_LIBROS_OFICIALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FoliarLibrosOficialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        if (validarCampos()) {
            return;
        }
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            if ("carta".equals(tamano)) {
                if ("vertical".equals(orientacion)) {
                    informe = "000759FoliarLibrosOficialesCV";
                }
                else {
                    informe = "000758FoliarLibrosOficialesCH";
                }
            }
            else {
                if ("vertical".equals(orientacion)) {
                    informe = "000761FoliarLibrosOficialesOV";
                }
                else {
                    informe = "000760FoliarLibrosOficialesOH";
                }
            }

            mostrarCodigo = SysmanFunciones.validarVariableVacio(codLibro)
                ? false
                : true;

            reemplazar.put("numFinal", numFinal);
            reemplazar.put("numInicial", numInicial);

            parametros.put("PR_MOSTRARLOGO", mostrarLogo);
            parametros.put("PR_MOSTRARCODIGO", mostrarCodigo);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_MOSTRARCOMPANIA", datoCompania);
            parametros.put("PR_PAGINICIAL", numInicial);
            parametros.put("PR_NOMBRELIBRO", nombreLibro);
            parametros.put("PR_CODIGOLIBRO", codLibro);
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCampos() {
        if (numInicial > numFinal) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB523"));
            return true;
        }
        if ((numInicial == 0) || (numFinal == 0)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB524"));
            return true;
        }
        return false;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartipoLibro() {
        // <CODIGO_DESARROLLADO>
        nombreLibro = tipoLibro;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getTipoLibro() {
        return tipoLibro;
    }

    public void setTipoLibro(String tipoLibro) {
        this.tipoLibro = tipoLibro;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(String orientacion) {
        this.orientacion = orientacion;
    }

    public String getCodLibro() {
        return codLibro;
    }

    public void setCodLibro(String codLibro) {
        this.codLibro = codLibro;
    }

    public String getNombreLibro() {
        return nombreLibro;
    }

    public void setNombreLibro(String nombreLibro) {
        this.nombreLibro = nombreLibro;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public int getNumInicial() {
        return numInicial;
    }

    public void setNumInicial(int numInicial) {
        this.numInicial = numInicial;
    }

    public int getNumFinal() {
        return numFinal;
    }

    public void setNumFinal(int numFinal) {
        this.numFinal = numFinal;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public boolean isMostrarCodigo() {
        return mostrarCodigo;
    }

    public void setMostrarCodigo(boolean mostrarCodigo) {
        this.mostrarCodigo = mostrarCodigo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMostrarLogo() {
        return mostrarLogo;
    }

    public void setMostrarLogo(boolean mostrarLogo) {
        this.mostrarLogo = mostrarLogo;
    }

    public boolean isDatoCompania() {
        return datoCompania;
    }

    public void setDatoCompania(boolean datoCompania) {
        this.datoCompania = datoCompania;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
