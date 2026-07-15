package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.FileNotFoundException;
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
 * @author dsuesca
 * @version 1, 29/07/2015
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 16/03/2017
 * 
 * Se paso texto quemado en el metodo setTitulos() a texto en bean. Se
 * modific� para que muestre cual informe se debe generar en caso de
 * que no exista.
 * @author jlramirez
 * @version 3, 22/03/2017
 * 
 * 
 * @version 2, 10/10/2017
 * @author jreina se realizaron los cambios de refactoring.
 * 
 */

@ManagedBean
@ViewScoped
public class ListadoPersonalPorFondosControlador extends BeanBaseModal {

    private final String nombreCompania;
    private String opcion;
    private String titulo;
    private String idConsulta;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ListadoPersonalPorFondosControlador
     */
    public ListadoPersonalPorFondosControlador() {
        super();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTADO_PERSONAL_POR_FONDOS_CONTROLADOR.getCodigo();
            opcion = "0";
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ListadoPersonalPorFondosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        archivoDescarga = null;
        String jasperCompilado = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            jasperCompilado = "1".equals(opcion)
                ? "000125ListadoPersonalFondosSalud"
                : "000124ListadoPersonalFondos";

            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_EQUIPO", "#EQUIPO#");

            setTitulos();

            parametros.put("PR_TITULOFONDOS", titulo.toUpperCase());
            setTitulos();

            Reporteador.resuelveConsulta(idConsulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(jasperCompilado,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException ex) {
            String msj = idioma.getString("MSM_INFORME_VAR_NO_EXISTE");
            msj = msj.replace("s$reporte$s", jasperCompilado);
            JsfUtil.agregarMensajeError(ex.getMessage() + msj);
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        // </CODIGO_DESARROLLADO>
        
    }

    // </CODIGO_DESARROLLADO>
    public void setTitulos() {

        switch (opcion) {
        case "0":
            titulo = idioma.getString("TB_TB2975");
            idConsulta = "000124ListadoPersonalFondos";
            break;
        case "1":
            titulo = idioma.getString("TB_TB2976");
            idConsulta = "000125ListadoPersonalFondosSalud";
            break;
        case "2":
            titulo = idioma.getString("TB_TB2977");
            idConsulta = "000124ListadoPersonalFondos2";
            break;
        case "3":
            titulo = idioma.getString("TB_TB2978");
            idConsulta = "000124ListadoPersonalFondos3";
            break;
        case "4":
            titulo = idioma.getString("TB_TB2979");
            idConsulta = "000124ListadoPersonalFondos4";
            break;
        case "5":
            titulo = idioma.getString("TB_TB2980");
            idConsulta = "000124ListadoPersonalFondos5";
            break;
        case "6":
            titulo = idioma.getString("TB_TB2981");
            idConsulta = "000124ListadoPersonalFondos6";
            break;
        default:
            break;
        }

    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void oprimirExcel() {
        getInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    public void oprimirPreliminar() {
        // <CODIGO_DESARROLLADO>
        getInforme(ReportesBean.FORMATOS.PDF);

    }

    public String getJasper() {
        return idConsulta;
    }

    public void setJasper(String jasper) {
        this.idConsulta = jasper;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
