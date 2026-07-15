package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.RelacioncausacionControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 31/05/2016
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario y se ajusta
 * Conexion.
 * @author lcortes
 * @version 3, 15/08/2017 Refactorizacion de la consulta de la lista
 * ano para usar dss y reemplazo de llamados a la clase Acciones por
 * el ejb respectivo.
 * @author lcortes
 * @version 3, 01/09/2017. Se cambia el formato en que se envian las
 * fechas al reporte.
 */
@ManagedBean
@ViewScoped
public class RelacioncausacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo definido para almacenar el numero del ano seleccionado
     * en el combo
     */
    private int ano;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    /**
     * Atributo a nivel clase que almacena la lista del combo ano
     */
    private List<Registro> listaano;

    /**
     * Creates a new instance of RelacioncausacionControlador
     */
    public RelacioncausacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RELACIONCAUSACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            fechaInicial = new Date();
            fechaFinal = new Date();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RelacioncausacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaano();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(new Date());
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaano
     *
     */
    public void cargarListaano() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RelacioncausacionControladorUrlEnum.URL3989
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "000842RELACIONCAUSACIONSG";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("ano", ano);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_RELACIONCAUSACION_TXTFECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_RELACIONCAUSACION_TXTFECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            "MSM_INFORME_NO_EXISTE"),
                                            " ", ex.getMessage(), " ",
                                            reporte));
            Logger.getLogger(RelacioncausacionControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo get y set
     *
     * @return
     */

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la lista listaano
     *
     * @return listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaano
     *
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }
}
