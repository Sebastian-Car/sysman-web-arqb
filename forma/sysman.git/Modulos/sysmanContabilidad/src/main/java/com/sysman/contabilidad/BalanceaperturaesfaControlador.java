package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceaperturaesfaControladorEnum;
import com.sysman.contabilidad.enums.BalanceaperturaesfaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 26/04/2016
 * 
 * @author jrodrigueza
 * @version 2, 11/04/2017 Proceso de Refactoring y ajustes segun
 * recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class BalanceaperturaesfaControlador extends BeanBaseModal {
    private final String compania;
    private String codigoInicial;
    private String codigoFinal;
    private String anio;
    private String mes;
    private String nombreCodInicial;
    private String nombreCodFinal;
    private String digitos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BalanceaperturaesfaControlador
     */
    public BalanceaperturaesfaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.BALANCEAPERTURAESFA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAnoTrabajo();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = "13";
        cargarListaMesTrabajo();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceaperturaesfaControladorUrlEnum.URL3137
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(BalanceaperturaesfaControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(BalanceaperturaesfaControladorEnum.PARAM1.getValue(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalanceaperturaesfaControladorUrlEnum.URL3137
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(BalanceaperturaesfaControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(BalanceaperturaesfaControladorEnum.PARAM1.getValue(), anio);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(BalanceaperturaesfaControladorEnum.PARAM0.getValue(),
                        compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceaperturaesfaControladorUrlEnum.URL4380
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(BalanceaperturaesfaControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(BalanceaperturaesfaControladorEnum.PARAM1.getValue(), anio);
        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalanceaperturaesfaControladorUrlEnum.URL4873
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirXBRL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (datosInvalidos()) {
            return;
        }
        generarArchivo("application/xml", ".xbrl");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (datosInvalidos()) {
            return;
        }
        generarArchivo("application/vnd.ms-excel", ".csv");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCodigoInicial();
        codigoInicial = null;
        nombreCodInicial = null;
        codigoFinal = null;
        nombreCodFinal = null;
        listaCodigoFinal = null;
        cargarListaMesTrabajo();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = extraerString(registroAux.getCampos().get("CODIGO"));
        nombreCodInicial = extraerString(registroAux.getCampos().get("NOMBRE"));
        codigoFinal = "";
        nombreCodFinal = "";
        listaCodigoFinal = null;
        cargarListaCodigoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = extraerString(registroAux.getCampos().get("CODIGO"));
        nombreCodFinal = extraerString(registroAux.getCampos().get("NOMBRE"));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo para generar el archivo segun el contenido y extension.
     * 
     * @param content
     * @param extension
     */
    public void generarArchivo(String content, String extension) {
        String sql = resolverConsulta();
        try {
            List<Registro> listado = service
                            .getListado(ConectorPool.ESQUEMA_SYSMAN, sql);
            if (listado == null || listado.isEmpty()) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB108"));
                return;
            }
            String nombreArchivo = "";
            StringBuilder textoArchivo = new StringBuilder("");
            String nombreCompania = SessionUtil.getCompaniaIngreso()
                            .getNombre();
            if (".csv".equals(extension)) {
                nombreArchivo = idioma.getString("TB_TB499") + extension;
                String fechaCadena = SysmanFunciones
                                .convertirAFechaCadena(new Date(),
                                                "MMMMM 'de' YYYY")
                                .toUpperCase();
                String[] titulos = { idioma.getString("TG_ENTIDAD2")
                    + nombreCompania + "\n" + idioma.getString("TB_TB501")
                    + "\n" + idioma.getString("TB_TB502") + fechaCadena + "\n"
                    + "\n" + idioma.getString("TG_CUENTA"),
                                     idioma.getString("TB_TB503"),
                                     idioma.getString("TB_TB504") + "\r\n" };
                textoArchivo = generarFilasTitulo(titulos);
                for (Registro registro : listado) {
                    textoArchivo.append(SysmanFunciones
                                    .nvl(registro.getCampos().get("CUENTA"), "")
                        + ",");
                    textoArchivo.append(SysmanFunciones
                                    .nvl(registro.getCampos()
                                                    .get("SALDO_AUXILIAR"), 0)
                                    .toString().replace(",", "")
                        + ",");
                    textoArchivo.append(SysmanFunciones
                                    .nvl(registro.getCampos()
                                                    .get("SALDO_MAYOR"), 0)
                                    .toString().replace(",", "")
                        + "\n");
                }
            }
            else if (".xbrl".equals(extension)) {
                nombreArchivo = idioma.getString("TB_TB505") + extension;
                String[] titulos = {
                                     "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <!-- Copyright 2014 SYSMAN,Paipa,Boyaca-Colombia. --> "
                                         + "\n"
                                         + "<xbrli:xbrl xmlns:xbrli=\"http://www.xbrl.org/2003/instance\" xmlns:link=\"http://www.xbrl.org/2003/linkbase\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:iso4217=\"http://www.xbrl.org/2003/iso4217\" xmlns:dt=\"http://xbrl.c-ebs.org/dt\" xmlns:xbrldi=\"http://xbrl.org/2005/xbrldi\" xmlns:d-hh=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-hh-2006-07-01\" xmlns:d-ty=\"http://www.c-ebs.org/eu/fr/esrs/corep/2006-07-01/d-ty-2006-07-01\" xmlns:ref=\"http://www.xbrl.org/2004/ref\" xmlns:xbrldt=\"http://xbrl.org/2005/xbrldt\" xmlns:ref-corep=\"http://www.c-ebs.org/eu/fr/esrs/corep/2005-09-30/ref-corep-2005-09-30\"> "
                                         + "\n"
                                         + "<xbrli:unit id=\"COP\"> <xbrli:measure>iso4217:COP</xbrli:measure> </xbrli:unit> <xbrli:unit id=\"Pure\"> <xbrli:measure>xbrli:pure</xbrli:measure> </xbrli:unit>"
                                         + "\n" };
                textoArchivo = generarFilasTitulo(titulos);
                for (Registro registro : listado) {
                    textoArchivo.append("\t" + "<Cuenta> "
                        + SysmanFunciones.nvl(
                                        registro.getCampos().get("CUENTA"),
                                        "ND")
                        + " </Cuenta>" + "\n");
                    textoArchivo.append("\t\t " + "<SaldoAuxiliar> "
                        + SysmanFunciones.nvl(registro.getCampos()
                                        .get("SALDO_AUXILIAR"), "ND")
                        + " </SaldoAuxiliar>" + "\n");
                    textoArchivo.append("\t\t " + "<SaldoMayor> "
                        + SysmanFunciones.nvl(
                                        registro.getCampos().get("SALDO_MAYOR"),
                                        "ND")
                        + " </SaldoMayor>" + "\n");
                }
                textoArchivo.append("</xbrli:xbrl>");
            }
            ByteArrayInputStream archivo = JsfUtil
                            .serializarPlano(textoArchivo.toString());
            archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                            nombreArchivo, content);
        }
        catch (ParseException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private StringBuilder generarFilasTitulo(String[] titulos) {
        StringBuilder textoArchivo = new StringBuilder("");
        for (String celda : titulos) {
            textoArchivo.append(celda + ",");
        }
        return new StringBuilder(
                        textoArchivo.substring(0, textoArchivo.length() - 1)
                            + "\n");
    }

    private String resolverConsulta() {
        int mesTrabajo = Integer.parseInt(mes) - 1;
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("anio", anio);
        reemplazos.put("mes", mes);
        reemplazos.put("mesTrabajo", mesTrabajo);
        reemplazos.put("codigoInicial", codigoInicial);
        reemplazos.put("codigoFinal", codigoFinal);
        reemplazos.put("digitos", digitos);
        reemplazos.put("parAenValor", getParametro(
                        "TIPO DE COMPROBANTE AJUSTES POR ERRORES", " "));
        reemplazos.put("parAcnValor", getParametro(
                        "TIPO DE COMPROBANTE AJUSTES POR CONVERGENCIA", " "));
        reemplazos.put("parReValor", getParametro(
                        "TIPO DE COMPROBANTE RECLASIFICACIONES NIIF", " "));
        int modulo = Integer.parseInt(SessionUtil.getModulo());
        return Reporteador.resuelveConsulta("800043SituacionFinancieraESFA",
                        modulo, reemplazos);
    }

    /**
     * Valida que los datos ingresados en el formulario sean validos.
     * 
     * @return Verdadero si faltan campos obligatorios o el mes es
     * inválido.
     */
    private boolean datosInvalidos() {
        if (faltanCamposObligatorios()) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB105"));
            return true;
        }
        if (Integer.parseInt(mes) > 13) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB106"));
            mes = "13";
            return true;
        }
        return false;
    }

    /**
     * Validacion de campos obligatorios segun el tipo.
     *
     * @return Verdadero si hay camos nulos o vacios.
     */
    private boolean faltanCamposObligatorios() {
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mes)
            || SysmanFunciones.validarVariableVacio(codigoInicial)) {
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(codigoFinal)
            || SysmanFunciones.validarVariableVacio(digitos)) {
            return true;
        }
        return false;
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getNombreCodInicial() {
        return nombreCodInicial;
    }

    public void setNombreCodInicial(String nombreCodInicial) {
        this.nombreCodInicial = nombreCodInicial;
    }

    public String getNombreCodFinal() {
        return nombreCodFinal;
    }

    public void setNombreCodFinal(String nombreCodFinal) {
        this.nombreCodFinal = nombreCodFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
}
