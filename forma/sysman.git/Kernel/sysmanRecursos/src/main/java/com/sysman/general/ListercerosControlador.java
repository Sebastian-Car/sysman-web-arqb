package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.ListercerosControladorEnum;
import com.sysman.general.enums.ListercerosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 12/12/2015
 * 
 * @author eamaya
 * @version 2, 04/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class ListercerosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String opcion;
    private String terceroInicial;
    private String terceroFinal;
    private String nombreInicial;
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;

    /**
     * Creates a new instance of ListercerosControlador
     */
    public ListercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        opcion = "1";
        try {
            numFormulario = GeneralCodigoFormaEnum.LISTERCEROS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ListercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListaTerceroInicial();
    }

    public void cargarListaTerceroInicial() {

        if ("2".equals(opcion)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ListercerosControladorUrlEnum.URL2417
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "NIT");

        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ListercerosControladorUrlEnum.URL3333
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "NIT");
        }
    }

    public void cargarListaTerceroFinal() {

        if ("2".equals(opcion)) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ListercerosControladorUrlEnum.URL4344
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(ListercerosControladorEnum.PARAM3.getValue(),
                            terceroInicial);

            listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "NIT");
        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ListercerosControladorUrlEnum.URL5352
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(ListercerosControladorEnum.PARAM5.getValue(),
                            nombreInicial);

            listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, "NIT");
        }
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {
            StringBuilder orderBy = new StringBuilder();
            if ("2".equals(opcion)) {
                orderBy.append("WHERE TERCERO.COMPANIA = '").append(compania).append("' \n")
                    .append("        AND TERCERO.NIT BETWEEN '").append(terceroInicial)
                    .append("' AND '").append(terceroFinal).append("'")
                    .append("      ORDER BY  TERCERO.COMPANIA, \n")
                    .append("                TERCERO.NIT, \n")
                    .append("                TERCERO.SUCURSAL");
            }
            else {
                orderBy.append("WHERE TERCERO.COMPANIA = '").append(compania).append("' \n")
                    .append("       AND TERCERO.NOMBRE BETWEEN '").append(nombreInicial)
                    .append("' AND '").append(nombreFinal).append("'")
                    .append("      ORDER BY  TERCERO.COMPANIA, \n")
                    .append("                TERCERO.NOMBRE");
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("strOrderBy", orderBy.toString());
            String strSql = Reporteador.resuelveConsulta("000355LisTerceros",
                            Integer.parseInt(modulo), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            if ("2".equals(opcion)) {
                parametros.put("PR_TERCERO", idioma.getString("TB_TB873")
                    + terceroInicial + " y " + terceroFinal);
            }
            else {
                parametros.put("PR_TERCERO", idioma.getString("TB_TB873")
                    + nombreInicial + " y " + nombreFinal);
            }
            try {
                archivoDescarga = JsfUtil.exportarStreamed("000355LisTerceros",
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        
       

    }

    public void cambiarOpcion() {
        terceroInicial = null;
        terceroFinal = null;
        nombreInicial = null;
        nombreFinal = null;
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();

    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NIT") == null ? " "
            : registroAux.getCampos().get("NIT").toString();
        nombreInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
        terceroFinal = null;
        nombreFinal = null;
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NIT") == null ? " "
            : registroAux.getCampos().get("NIT").toString();
        nombreFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getNombreInicial() {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal() {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR316-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * 'formularioAbrir 10, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

}
