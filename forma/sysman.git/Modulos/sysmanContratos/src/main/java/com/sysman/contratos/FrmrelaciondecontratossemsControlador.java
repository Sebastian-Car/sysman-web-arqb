package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 15/12/2015
 * @version 2, 08/08/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties. Cambio el numero del formulario al
 * enumerado.
 */
@ManagedBean
@ViewScoped
public class FrmrelaciondecontratossemsControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String periodo;
    private String filtro;
    private String ano;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmrelaciondecontratossemsControlador
     */
    public FrmrelaciondecontratossemsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMRELACIONDECONTRATOSSEMS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        String parReporte = "000437rptRelaciondeContratosSEM";
        Map<String, Object> remplazar = new HashMap<>();

        String generarContrato;

        if ("T".equals(filtro)) {
            generarContrato = " ";
        }
        else if ("-1".equals(filtro)) {
            generarContrato = " AND TIPOORDENDECOMPRA.GENERACONTRATO NOT IN (0) ";
        }
        else {
            generarContrato = " AND TIPOORDENDECOMPRA.GENERACONTRATO IN (0) ";
        }

        remplazar.put("generarContrato", generarContrato);
        remplazar.put("ano", ano);
        remplazar.put("tipoSemestre", "S1".equals(periodo) ? "<=6" : ">=6");

        String strsql = Reporteador.resuelveConsulta(parReporte,
                        Integer.parseInt(modulo), remplazar);

        Map<String, Object> parametros = new HashMap<>();
        try {
            String firmaAsesor = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA DEL ASESOR JURIDICO", modulo, new Date(),
                            true);
            String filtros;

            if ("T".equals(filtro)) {
                filtros = " ";
            }
            else if ("-1".equals(filtro)) {
                filtros = " CON FORMALIDADES";
            }
            else {
                filtros = " SIN FORMALIDADES";
            }
            parametros.put("PR_STRSQL", strsql);
            parametros.put("PR_FIRMA_DEL_ASESOR_JURIDICO", firmaAsesor);
            parametros.put("PR_ENCABEZADO", SysmanFunciones
                            .concatenar("S1".equals(periodo) ? "Semestre I"
                                : "Semestre II", " DE ", ano));
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CONTRATOS", SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3369"), filtros));

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
}
