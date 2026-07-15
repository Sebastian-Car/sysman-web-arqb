package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InformeNominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 07/07/2015
 *
 * -- Modificado por lcortes 17/03/2017 09:44. --> Ajustes de buenas
 * practicas SonarLint.
 *
 * @author eamaya
 * @version 2.0,09/10/2017, Proceso de Refactoring DSS,manejo de EJBs,
 * cambio de numero de formulario por enum y cambio de textos quemados
 * por texto en bean
 * 
 * @author obarragan
 * @version 3, 10/06/2019 - Se agrego opcion de imprimir header con
 * imagenes adicionales.
 */
@ManagedBean
@ViewScoped

public class InformeNominaControlador extends BeanBaseModal {

    private final String moduloNomina = SessionUtil.getModulo();
    private final String anio = (String) SessionUtil
                    .getSessionVar("anioNomina");
    private final String proceso = (String) SessionUtil
                    .getSessionVar("procesoNomina");
    private final String mes = (String) SessionUtil.getSessionVar("mesNomina");
    private final String periodo = (String) SessionUtil
                    .getSessionVar("periodoNomina");
    private final String nombreProceso = (String) SessionUtil
                    .getSessionVar("nombreProcesoNomina");
    private final String nombrePeriodo = (String) SessionUtil
                    .getSessionVar("nombrePeriodoNomina");
    private final String nombreCompania = SessionUtil.getCompaniaIngreso()
                    .getNombre();
    private final String compania;

    private String opcion;
    private String idEmpleado;
    private String idCentroCosto;
    private String nombreCompleto;
    private String controDeCosto;
    private String titulo;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaIdDeEmpleado;
    private RegistroDataModelImpl listaIdCentroDeCosto;
    private StreamedContent archivoDescarga;

    private String headerEspecial;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Creates a new instance of InformeNominaControlador
     */
    public InformeNominaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_NOMINA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            titulo = idioma.getString("TB_TB3710")
                            .replace("#$nombreProceso#$", nombreProceso)
                            .replace("#$nombreMes#$",
                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                            .parseInt(mes)].toUpperCase())
                            .replace("#$anio#$", anio)
                            .replace("#$nombrePeriodo#$",
                                            nombrePeriodo.toUpperCase());

            opcion = "2";
        }
        catch (Exception ex) {
            Logger.getLogger(InformeNominaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaIdDeEmpleado();
        cargarListaIdCentroDeCosto();
        abrirFormulario();
    }

    public void cargarListaIdDeEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeNominaControladorUrlEnum.URL4514
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIdDeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    public void cargarListaIdCentroDeCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeNominaControladorUrlEnum.URL5391
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaIdCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaIdDeEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idEmpleado = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("ID_DE_EMPLEADO"), "").toString();

        nombreCompleto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

    }

    public void seleccionarFilaIdCentroDeCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idCentroCosto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        controDeCosto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        getInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        archivoDescarga = null;

        if (!validarVacios()) {
            return;
        }

        String condAdicional = "";
        if (("1").equals(opcion)) {
            condAdicional = " AND V_VOLANTES.ID_DE_EMPLEADO=" + idEmpleado
                + " ";
        }
        else if (("2").equals(opcion)) {
            condAdicional = "";
        }
        else if (("3").equals(opcion)) {
            condAdicional = " AND V_VOLANTES.ID_CENTRO_DE_COSTO="
                + idCentroCosto + " ";
        }

        Map<String, Object> parametros = new HashMap<>();
        try {
            String reporte = SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "FORMATO NOMINA", moduloNomina,
                                            new Date(), false),
                                            "000089NominaSTR");

            String ccAgrupado = "002024ResumensinEncabezado";

            String nombreVb1 = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DE QUIEN FIRMA NOMINA VB1", moduloNomina,
                            new Date(), false);

            String cargoVb1 = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DE QUIEN FIRMA NOMINA VB1", moduloNomina,
                            new Date(), false);

            String nombreVb2 = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DE QUIEN FIRMA NOMINA VB2", moduloNomina,
                            new Date(), false);

            String cargoVb2 = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DE QUIEN FIRMA NOMINA VB2", moduloNomina,
                            new Date(), false);

            String nombreAutoriza = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DE QUIEN AUTORIZA NOMINA", moduloNomina,
                            new Date(), false);

            String cargoAutoriza = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DE QUIEN AUTORIZA NOMINA", moduloNomina,
                            new Date(), false);
            String nombreGerente = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DEL GERENTE", moduloNomina, new Date(),
                            false);

            String cargoGerente = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DEL GERENTE", moduloNomina, new Date(),
                            false);

            String nombreTP = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DEL CARGO TESORERO PAGADOR", moduloNomina,
                            new Date(), false);

            String cargoTP = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DEL TESORERO PAGADOR", moduloNomina,
                            new Date(), false);

            // inicio dcastiblanco

            String cargoJP = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DEL JEFE DE PRESUPUESTO", moduloNomina,
                            new Date(), false);

            String nombreRevisaNomina = ejbSysmanUtl.consultarParametro(
                            compania,
                            "NOMBRE DE QUIEN REVISA NOMINA", moduloNomina,
                            new Date(), false);

            String cargoRevisaNomina = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO DE QUIEN REVISA NOMINA", moduloNomina,
                            new Date(), false);

            String nombreJefeRecursos = ejbSysmanUtl.consultarParametro(
                            compania,
                            "NOMBRE JEFE RECURSOS HUMANOS", moduloNomina,
                            new Date(), false);

            String cargoJefeRecursos = ejbSysmanUtl.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS", moduloNomina,
                            new Date(), false);

            String elaborado = ejbSysmanUtl.consultarParametro(compania,
                            "ELABORADO POR", moduloNomina,
                            new Date(), false);

            String nombreJP = ejbSysmanUtl.consultarParametro(compania,
                            "NOMBRE DE JEFE DE PRESUPUESTO", moduloNomina,
                            new Date(), false);

            // final dcastiblanco

            String validarFirmas = ejbSysmanUtl.consultarParametro(compania,
                            "MOSTRAR FIRMAS FND", moduloNomina,
                            new Date(),
                            true);

            headerEspecial = ejbSysmanUtl.consultarParametro(compania,
                            "FORMATOS ESPECIALES BUCARAMANGA", moduloNomina,
                            new Date(),
                            true);

            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("proceso", proceso);
            reemplazar.put("anio", anio);
            reemplazar.put("mes", mes);
            reemplazar.put("periodo", periodo);
            reemplazar.put("ano", anio);
            reemplazar.put("CONDADICIONAL", condAdicional);

            parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_NOMINA_VB1", nombreVb1);
            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_NOMINA_VB1", cargoVb1);
            parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_NOMINA_VB2", nombreVb2);
            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_NOMINA_VB2", cargoVb2);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nombreAutoriza);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA", cargoAutoriza);
            parametros.put("PR_NOMBREEMPRESA", nombreCompania);
            parametros.put("PR_NOMBRE_DEL_GERENTE",
                            nombreGerente);

            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombreJP);
            parametros.put("PR_CARGO_DEL_GERENTE",
                            cargoGerente);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nombreTP);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            cargoTP);
            parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
                            cargoJP);

            parametros.put("PR_VALIDAR_FIRMAS",
                            validarFirmas.equals("SI") ? true : false);

            // inicio dcastiblanco
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            nombreRevisaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            cargoRevisaNomina);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                            nombreJefeRecursos);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                            cargoJefeRecursos);
            parametros.put("PR_ELABORADO_POR",
                            elaborado);
            boolean bugCompania = SessionUtil.getCompaniaIngreso().getNit()
                            .equals("890201222") ? true : false;
            parametros.put("PR_COMPANIA_BUG", bugCompania);
            // final dcastiblanco

            parametros.put("PR_HEADER_ESPECIAL",
                            headerEspecial.equals("SI") ? true : false);
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);

            String informe = opcion.equals("4") ? ccAgrupado : reporte;
            
            String consultaReporte = "";
            
            if("001804NOMINABUC".equalsIgnoreCase(reporte)) {
            	consultaReporte = "001804NOMINABUC";
            }else {
            	consultaReporte = "000089NominaSTR";
            }

            Reporteador.resuelveConsulta(consultaReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarVacios() {
        if (("1").equals(opcion)
            && SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            return false;
        }
        else if (("3").equals(opcion)
            && SysmanFunciones.validarVariableVacio(idCentroCosto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2774"));
            return false;

        }
        return true;

    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        getInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaIdDeEmpleado() {
        return listaIdDeEmpleado;
    }

    public void setListaIdDeEmpleado(RegistroDataModelImpl listaIdDeEmpleado) {
        this.listaIdDeEmpleado = listaIdDeEmpleado;
    }

    public RegistroDataModelImpl getListaIdCentroDeCosto() {
        return listaIdCentroDeCosto;
    }

    public void setListaIdCentroDeCosto(
        RegistroDataModelImpl listaIdCentroDeCosto) {
        this.listaIdCentroDeCosto = listaIdCentroDeCosto;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {
        this.service = service;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getIdCentroCosto() {
        return idCentroCosto;
    }

    public void setIdCentroCosto(String idCentroCosto) {
        this.idCentroCosto = idCentroCosto;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getControDeCosto() {
        return controDeCosto;
    }

    public void setControDeCosto(String controDeCosto) {
        this.controDeCosto = controDeCosto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public String getHeaderEspecial() {
        return headerEspecial;
    }

    public void setHeaderEspecial(String headerEspecial) {
        this.headerEspecial = headerEspecial;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}
