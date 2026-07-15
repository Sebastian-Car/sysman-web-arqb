package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.logica.Formulario;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 10/11/2015
 * 
 * @author eamaya
 * @version 2.0, 08/08/2017 Cambio de numero del formulario por enum ,
 * cambio de Sysdate por New Date()
 */
@ManagedBean
@ViewScoped
public class EjecucionDeContratosControlador extends BeanBaseModal {

    private Date hasta;
    private Date desde;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of EjecucionDeContratosControlador
     */
    public EjecucionDeContratosControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.EJECUCION_DE_CONTRATOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EjecucionDeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenu();
        }

    }

    @PostConstruct
    public void inicializar() {
        if (permisos == null) {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            permisos = form.getPermisos();
            if (permisos == null || !permisos[3]) {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
        }
        abrirFormulario();

        desde = new Date();
        hasta = new Date();
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        genInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public String ejecutadoMes(Integer mes, Integer ano) {
        if (mes == 1) {
            return "";
        }
        else {
            return SysmanFunciones.concatenar("EJECUTADO ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[1]
                                            .toUpperCase(),
                            " - ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes - 1]
                                            .toUpperCase(),
                            " DE ", Integer.toString(ano));
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String desdeAux = SysmanFunciones.convertirAFechaCadena(desde);
            String hastaAux = SysmanFunciones.convertirAFechaCadena(hasta);
            reemplazar.put("desde", desdeAux);
            reemplazar.put("hasta", hastaAux);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador
                            .resuelveConsulta("000367EjecucionDeContratos",
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_EJECUCIONDECONTRATOS_HASTA", hastaAux);
            parametros.put("PR_FORMS_EJECUCIONDECONTRATOS_DESDE", desdeAux);
            parametros.put("PR_ANIO",
                            String.valueOf(SysmanFunciones.ano(hasta) - 1));
            parametros.put("PR_EJECUTADOMES",
                            ejecutadoMes(SysmanFunciones.mes(hasta),
                                            SysmanFunciones.ano(hasta)));
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                            .mes(desde)].toUpperCase());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000367EjecucionDeContratos", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(EjecucionDeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | SysmanException | JRException
                        | IOException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
            Logger.getLogger(EjecucionDeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}
