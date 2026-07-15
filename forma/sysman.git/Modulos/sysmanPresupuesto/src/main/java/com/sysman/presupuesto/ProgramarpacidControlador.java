package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.ProgramarpacidControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 26/07/2016
 * @modified jguerrero
 * @version 2. 19/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class ProgramarpacidControlador extends BeanBaseModal {
    private final String compania;

    private final String strCodigo;
    private final String strNaturaleza;

    // <DECLARAR_ATRIBUTOS>
    private String id;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private Map<String, Object> rid;
    private String anio;
    private String codigo;
    private String nombre;
    private String naturaleza;
    private String formulario;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaId;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ProgramarpacidControlador
     */
    @SuppressWarnings("unchecked")
    public ProgramarpacidControlador() {
        super();
        compania = SessionUtil.getCompania();

        strCodigo = "codigo";
        strNaturaleza = "naturaleza";

        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMARPACID_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = SysmanFunciones.nvl(parametrosEntrada.get("anio"), "")
                                .toString();
                codigo = SysmanFunciones
                                .nvl(parametrosEntrada.get(strCodigo), "")
                                .toString();
                nombre = SysmanFunciones
                                .nvl(parametrosEntrada.get("nombre"), "")
                                .toString();
                naturaleza = SysmanFunciones
                                .nvl(parametrosEntrada.get(strNaturaleza), "")
                                .toString();
                formulario = SysmanFunciones
                                .nvl(parametrosEntrada.get("formulario"), "")
                                .toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ProgramarpacidControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaId();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaId() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        UrlBean urlBean;
        if (Integer.toString(GeneralCodigoFormaEnum.PROGRAMARPACS_CONTROLADOR
                        .getCodigo()).equals(formulario)) {
            // not in 0
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramarpacidControladorUrlEnum.URL4178
                                                            .getValue());

        }
        else {
            // sin el not in 0
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramarpacidControladorUrlEnum.URL4140
                                                            .getValue());

        }

        listaId = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (formulario.contains("grafica")) {
            archivoDescarga = null;
            try {
                String reporte = "grafica1".equals(formulario)
                    ? "000939GraficaAPlanPPTAL" : "000941GraficaBPlanPPTAL";

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put(strCodigo, id);
                reemplazar.put("anio", anio);
                reemplazar.put(strNaturaleza, naturaleza);
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("PR_TITULO", id + " " + nombre);
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", rid);
            parametros.put("anio", anio);
            parametros.put(strCodigo, id);
            parametros.put("nombre", nombre);
            parametros.put(strNaturaleza, naturaleza);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(formulario);
            direccionador.setParametros(parametros);
            if (Integer.toString(
                            GeneralCodigoFormaEnum.RESUMENPPTOINGS_CONTROLADOR
                                            .getCodigo())
                            .equals(formulario)) {
                direccionador.setRuta("/resumenpptoing.sysman");
            }
            if (Integer.toString(
                            GeneralCodigoFormaEnum.RESUMENPPTOS_CONTROLADOR
                                            .getCodigo())
                            .equals(formulario)) {
                direccionador.setRuta("/resumenppto.sysman");
            }
            RequestContext.getCurrentInstance().closeDialog(direccionador);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaId(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        id = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
        nombre = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public String getFormulario() {
        return formulario;
    }

    public void setFormulario(String formulario) {
        this.formulario = formulario;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaId() {
        return listaId;
    }

    public void setListaId(RegistroDataModelImpl listaId) {
        this.listaId = listaId;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
