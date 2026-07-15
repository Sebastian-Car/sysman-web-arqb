package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.ejb.EjbPlaneacionUnoRemote;
import com.sysman.planeacion.enums.FrmcertplancomprasControladorEnum;
import com.sysman.planeacion.enums.FrmcertplancomprasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;
import java.io.IOException;
import java.math.BigInteger;
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
import javax.faces.event.ActionEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 22/12/2015
 *
 * @author spina
 * @version 2, 07/09/2017 - Se refactoriza para dss, depuracion sonar
 * y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmcertplancomprasControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private boolean impreso;
    private Registro registroSub;
    private RegistroDataModelImpl listaCmbDependencia;
    private RegistroDataModelImpl listaCmbResponsable;
    private List<Registro> listaFrmsubplancompras;
    private List<Registro> listaCmbAnio;
    private List<Registro> listanumero;
    private StreamedContent archivoDescarga;
    private String mes;
    private String cmbDependencia;
    private String cmbAno;
    private String cmbResponsable;
    private String txtConsecutivo;
    private String textoFirma;
    private String txtObservacion;
    private String nombreDependencia;
    private String nombreResponsable;
    private int numero;
    private int nuevoNumero;
    private int indiceFrmsubplancompras;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPlaneacionUnoRemote ejbPlaneacionUno;

    public FrmcertplancomprasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
            numFormulario = GeneralCodigoFormaEnum.FRMCERTPLANCOMPRAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmcertplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.CERTIFICADO_PLAN_COMPRAS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaFrmsubplancompras = null;
    }

    @Override
    public void iniciarListasSub() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        try {
            Registro regNombreDependencia = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertplancomprasControladorUrlEnum.URL3320
                                                                            .getValue())
                                            .getUrl(), param));

            nombreDependencia = (String) regNombreDependencia.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName());

            Registro regNombreResponsable = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertplancomprasControladorUrlEnum.URL3321
                                                                            .getValue())
                                            .getUrl(), param));

            nombreResponsable = (String) regNombreResponsable.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListaFrmsubplancompras();
    }

    @Override
    public void iniciarListas() {
        cargarListaCmbDependencia();
        cargarListaCmbResponsable();
        cargarListaCmbAnio();
        cargarListanumero();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    public void activarEdicionFrmsubplancompras(Registro r) {
        registroSub = r;
    }

    public void cargarListaFrmsubplancompras() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.MES.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.MES.getName()));
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get("CONSECUTIVO_PC"));
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));
        try {
            listaFrmsubplancompras = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertplancomprasControladorUrlEnum.URL3322
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmcertplancomprasControladorEnum.INVENTARIO
                                                            .getValue()));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcertplancomprasControladorUrlEnum.URL3324
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCmbResponsable() {
        cmbDependencia = SysmanFunciones.nvl(
                        registro.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()),
                        "").toString();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcertplancomprasControladorUrlEnum.URL3325
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), cmbDependencia);

        listaCmbResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESPONSABLE.getName());
    }

    public void cargarListaCmbAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCmbAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertplancomprasControladorUrlEnum.URL3326
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanumero() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listanumero = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcertplancomprasControladorUrlEnum.URL3327
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubFrmsubplancompras() {
        // METODO_NO_IMPLEMENTADO

    }

    public void editarRegSubFrmsubplancompras(RowEditEvent event) {
    	try 
    	{
	    	UrlBean urlUpdate = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(FrmcertplancomprasControladorUrlEnum.URL114010.getValue());
	    	
			Map<String, Object> reg = new TreeMap<>();
			
			reg.put("ENVIAR_CERT", registroSub.getCampos().get("ENVIAR_CERT"));
			reg.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			reg.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.put("KEY_COMPANIA", compania);
			reg.put("KEY_ANO", registro.getCampos().get("ANO"));
			reg.put("KEY_RUBRO", registroSub.getCampos().get("RUBRO"));
			reg.put("KEY_CODIGO", registroSub.getCampos().get("CODIGOELEMENTO"));
			reg.put("KEY_DEPENDENCIA", registro.getCampos().get("DEPENDENCIA"));
			reg.put("KEY_MES", registroSub.getCampos().get("MES"));
			
			Parameter parameter = new Parameter();
			parameter.setFields(reg);
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			
			JsfUtil.agregarMensajeInformativo(
                    idioma.getString("MSM_REGISTRO_MODIFICADO"));
			
			cargarListaFrmsubplancompras();
		} 
    	catch (SystemException e) 
    	{
			e.printStackTrace();
		}
    }

    public void eliminarRegSubFrmsubplancompras(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    public void cancelarEdicionFrmsubplancompras() {
        cargarListaFrmsubplancompras();
    }

    public void oprimirPresentar(ActionEvent ac)
                    throws IOException, JRException {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel(ActionEvent ac) throws IOException, JRException {
        // </CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporte(FORMATOS formato)
                    throws IOException, JRException {
        // </CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            agregarRegistroNuevo(false);
            imprimirReporte();
            obtenerCertPlanCompras(formato);
            cargarListaFrmsubplancompras();
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3526"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbDependencia(SelectEvent event) {
        Registro registroAux = ((Registro) event.getObject());
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreDependencia = (String) registroAux.getCampos().get("NOMBRE");
        cmbResponsable = null;
        listaFrmsubplancompras = null;
        cargarListaCmbResponsable();
        cargarListaFrmsubplancompras();
    }

    public void seleccionarFilaCmbResponsable(SelectEvent event) {
        Registro registroAux = ((Registro) event.getObject());
        registro.getCampos().put(GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));
        nombreResponsable = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName());
    }

    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>
        cargarListaCmbResponsable();
        cargarListaFrmsubplancompras();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbAnio() {
        // <CODIGO_DESARROLLADO>
        try {
            long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "CERTIFICADO_PLAN_COMPRAS",
                            "COMPANIA = ''" + compania + "'' " + " AND ANO = "
                                + registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                + " ",
                            GeneralParameterEnum.NUMERO.getName());

            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmcertplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarnumero() {
        // <CODIGO_DESARROLLADO>
        cargarListanumero();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacion4() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void imprimirReporte() {

        impreso = (boolean) registro.getCampos().get("IMPRESO");

        cmbAno = (String) registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName());
        cmbDependencia = (String) registro.getCampos()
                        .get(GeneralParameterEnum.DEPENDENCIA.getName());
        cmbResponsable = (String) registro.getCampos()
                        .get(GeneralParameterEnum.RESPONSABLE.getName());
        txtConsecutivo = (String) registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName());
        mes = (String) registro.getCampos()
                        .get(GeneralParameterEnum.MES.getName());

        if (!impreso
            && (cmbAno == null || cmbAno.equals("")
                || cmbDependencia == null
                || cmbDependencia.equals("") || cmbResponsable == null
                || cmbResponsable.equals(""))) {
            JsfUtil.agregarMensajeAlerta(
                            "Faltan datos para generar el reporte. Por favor verifique.");
            return;

        }

        try {
            ejbPlaneacionUno.registrarCertPlanCompras(compania,
                            Integer.parseInt(cmbAno),
                            new BigInteger(txtConsecutivo), cmbDependencia,
                            cmbResponsable,
                            Integer.parseInt(mes));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void obtenerCertPlanCompras(FORMATOS formatos)
                    throws IOException, JRException {
        txtObservacion = (String) registro.getCampos().get("OBSERVACIONES");
        String usuario = SessionUtil.getUser().getCodigo();
        String numeroInforme = (String) (registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()) == null ? 0
                            : String.valueOf(registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())));
        String reporte = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("consecutivo", registro.getCampos()
                            .get(GeneralParameterEnum.NUMERO.getName()));
            reemplazar.put("usuario", usuario);
            reemplazar.put("ano", cmbAno);
            reemplazar.put("mes", registro.getCampos().get("MES"));
            reemplazar.put("numero", numeroInforme);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            HashMap<String, Object> parametros = new HashMap<>();
            if ("899999428-8".equals(
                            SessionUtil.getCompaniaIngreso().getNit())) {
                reporte = "002091FormatoPlanAdquisiciones";
            }
            else {

                reporte = "000447ICertPlanCompra";
            }
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            String textoFijo = ejbSysmanUtil.consultarParametro(compania,
                            "TEXTO FIJO CERTIFICADO PLAN DE COMPRAS",
                            modulo,
                            new Date(), true);

            String coordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania,
                            "COORDINADOR ALMACEN",
                            modulo,
                            new Date(), true);

            String cargoCoordinadorAlmacen = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CARGO COORDINADOR ALMACEN",
                            modulo,
                            new Date(), true);

            textoFirma = ejbSysmanUtil.consultarParametro(
                            compania,
                            "TEXTO FIRMA CERTIFICADO PLAN DE COMPRAS",
                            modulo,
                            new Date(), true);

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_FORMS_FRM_CERT_PLAN_COMPRAS_CMBANIO", cmbAno);
            parametros.put("PR_FORMS_FRM_CERT_PLAN_COMPRAS_TEXTOFIRMA",
                            textoFirma);
            parametros.put("PR_TEXTO_FIJO_CERTIFICADO_PLAN_DE_COMPRAS",
                            textoFijo);
            parametros.put("PR_COORDINADOR_ALMACEN", coordinadorAlmacen);
            parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
                            cargoCoordinadorAlmacen);
            parametros.put("PR_COMPANIA", compania);

            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDepartamento());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (SystemException | SysmanException | JRException
                        | IOException ex) {
            Logger.getLogger(FrmcertplancomprasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            textoFirma = ejbSysmanUtil.consultarParametro(
                            compania,
                            "TEXTO FIRMA CERTIFICADO PLAN DE COMPRAS",
                            modulo,
                            new Date(), true);
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmcertplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        cargarListaCmbResponsable();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css != null) {
            cargarListaFrmsubplancompras();
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "CERTIFICADO_PLAN_COMPRAS",
                            "COMPANIA = ''" + compania + "'' "
                                + " AND ANO = "
                                + registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                + " ",
                            GeneralParameterEnum.NUMERO
                                            .getName());
            registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmcertplancomprasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

            return false;
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
        cmbAno = (String) registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName());

        String strSql;
        List<Registro> rs;
        int numeroConsecutivo = 0;

        strSql = "SELECT DISTINCT ('x')\n"
            + "FROM INVENTARIO\n"
            + "INNER JOIN DETALLE_PLAN_COMPRAS \n"
            + "ON INVENTARIO.COMPANIA = DETALLE_PLAN_COMPRAS.COMPANIA \n"
            + "AND INVENTARIO.CODIGOELEMENTO = DETALLE_PLAN_COMPRAS.CODIGO\n"
            + "WHERE DETALLE_PLAN_COMPRAS.MES =  " + mes
            + "                      \n"
            + "AND DETALLE_PLAN_COMPRAS.DEPENDENCIA = '" + cmbDependencia
            + "'               \n"
            + "AND DETALLE_PLAN_COMPRAS.ANO =  " + cmbAno + "";

        rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);

        if (!rs.isEmpty()) {
            nuevoNumero = numeroConsecutivo;
        }
        else {
            JsfUtil.agregarMensajeAlerta(
                            "No existen items para seleccionar y generar el certificado.");
        }
        String sucursal = (String) service
                        .getRegistro(ConectorPool.ESQUEMA_SYSMAN,
                                        "SELECT SUCURSAL\n"
                                            + "FROM DEPENDENCIA_RESPONSABLE\n"
                                            + "WHERE COMPANIA = '" + compania
                                            + "'\n"
                                            + "AND DEPENDENCIA = '"
                                            + registro.getCampos()
                                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                                            .getName())
                                            + "'\n"
                                            + "AND RESPONSABLE = '"
                                            + registro.getCampos()
                                                            .get(GeneralParameterEnum.RESPONSABLE
                                                                            .getName())
                                            + "'")
                        .getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if (css != null) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
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
        String strSql = "SELECT COUNT(*) CONTEOCONSE\n"
            + "        FROM CERTIFICADO_PLAN_COMPRAS\n"
            + "          INNER JOIN DETALLE_PLAN_COMPRAS\n"
            + "  ON  CERTIFICADO_PLAN_COMPRAS.COMPANIA = DETALLE_PLAN_COMPRAS.COMPANIA\n"
            + " AND CERTIFICADO_PLAN_COMPRAS.ANO = DETALLE_PLAN_COMPRAS.ANO\n"
            + " AND CERTIFICADO_PLAN_COMPRAS.NUMERO = DETALLE_PLAN_COMPRAS.CONSECUTIVO_PC\n"
            + "WHERE CERTIFICADO_PLAN_COMPRAS.COMPANIA = '"
            + registro.getCampos().get(GeneralParameterEnum.COMPANIA.getName())
            + "'"
            + " AND CERTIFICADO_PLAN_COMPRAS.ANO = "
            + registro.getCampos().get(GeneralParameterEnum.ANO.getName())
            + " AND CERTIFICADO_PLAN_COMPRAS.NUMERO = "
            + registro.getCampos().get(GeneralParameterEnum.NUMERO.getName());

        Registro rs = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, strSql);
        if (!rs.getCampos().get("CONTEOCONSE").equals("0")) {
            JsfUtil.agregarMensajeError(
                            "El certificado no puede ser eliminado. Existen datos en el detalle.");
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public RegistroDataModelImpl getListaCmbDependencia() {
        return listaCmbDependencia;
    }

    public void setListaCmbDependencia(
        RegistroDataModelImpl listaCmbDependencia) {
        this.listaCmbDependencia = listaCmbDependencia;
    }

    public RegistroDataModelImpl getListaCmbResponsable() {
        return listaCmbResponsable;
    }

    public void setListaCmbResponsable(
        RegistroDataModelImpl listaCmbResponsable) {
        this.listaCmbResponsable = listaCmbResponsable;
    }

    public List<Registro> getListaCmbAnio() {
        return listaCmbAnio;
    }

    public void setListaCmbAnio(List<Registro> listaCmbAnio) {
        this.listaCmbAnio = listaCmbAnio;
    }

    public List<Registro> getListanumero() {
        return listanumero;
    }

    public void setListanumero(List<Registro> listanumero) {
        this.listanumero = listanumero;
    }

    public List<Registro> getListaFrmsubplancompras() {
        return listaFrmsubplancompras;
    }

    public void setListaFrmsubplancompras(
        List<Registro> listaFrmsubplancompras) {
        this.listaFrmsubplancompras = listaFrmsubplancompras;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isImpreso() {
        return impreso;
    }

    public void setImpreso(boolean impreso) {
        this.impreso = impreso;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getCmbDependencia() {
        return cmbDependencia;
    }

    public void setCmbDependencia(String cmbDependencia) {
        this.cmbDependencia = cmbDependencia;
    }

    public String getCmbAno() {
        return cmbAno;
    }

    public void setCmbAno(String cmbAno) {
        this.cmbAno = cmbAno;
    }

    public String getCmbResponsable() {
        return cmbResponsable;
    }

    public void setCmbResponsable(String cmbResponsable) {
        this.cmbResponsable = cmbResponsable;
    }

    public String getTxtConsecutivo() {
        return txtConsecutivo;
    }

    public void setTxtConsecutivo(String txtConsecutivo) {
        this.txtConsecutivo = txtConsecutivo;
    }

    public String getTextoFirma() {
        return textoFirma;
    }

    public void setTextoFirma(String textoFirma) {
        this.textoFirma = textoFirma;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public int getIndiceFrmsubplancompras() {
        return indiceFrmsubplancompras;
    }

    public void setIndiceFrmsubplancompras(int indiceFrmsubplancompras) {
        this.indiceFrmsubplancompras = indiceFrmsubplancompras;
    }
}
