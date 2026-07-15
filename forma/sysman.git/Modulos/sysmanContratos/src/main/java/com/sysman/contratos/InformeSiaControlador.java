
package com.sysman.contratos;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.InformeSiaControladorEnum;
import com.sysman.contratos.enums.InformeSiaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 21/04/2016
 * @modified jguerrero
 * @version 2. 10/08/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 */
@ManagedBean
@ViewScoped

public class InformeSiaControlador extends BeanBaseModal {
    private final String compania;
    private final String formatoFechaCons;
    private final String formatoCons;
    private final String appExcelCons;
    private final String msgNoExisteInfo;
    private String opcionInforme;
    private String bimestre;
    private String vigencia;
    private boolean filtraPorFechaDeDiligenciamiento;
    private boolean manejaAuxiliarPorFuenteEnPresupuesto;
    private StreamedContent archivoDescarga;
    private List<Registro> listavigencia;
    private int modulo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of InformeSiaControlador
     */
    public InformeSiaControlador() {
        super();
        modulo = Integer.parseInt(SessionUtil.getModulo());
        compania = SessionUtil.getCompania();
        formatoFechaCons = "yyyy/MM/dd";
        formatoCons = "formato_";
        appExcelCons = "application/vnd.ms-excel";
        msgNoExisteInfo = "TB_TB108";
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_SIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InformeSiaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        abrirFormulario();
        initParametros();
        cargarListavigencia();
        opcionInforme = "1";
        vigencia = String.valueOf(SysmanFunciones
                        .ano(new Date()));
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void initParametros() {
        try {
            String valorPar = ejbSysmanUtil.consultarParametro(compania,
                            InformeSiaControladorEnum.PARAM0.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

            filtraPorFechaDeDiligenciamiento = valorPar == null ? false
                : ("SI").equalsIgnoreCase(valorPar);
            valorPar = ejbSysmanUtil.consultarParametro(compania,
                            InformeSiaControladorEnum.PARAM1.getValue(),
                            SessionUtil.getModulo(),
                            new Date(), true);

            manejaAuxiliarPorFuenteEnPresupuesto = valorPar == null ? false
                : ("SI").equalsIgnoreCase(valorPar);

        }
        catch (SystemException e) {

            Logger.getLogger(InformeSiaControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListavigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listavigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeSiaControladorUrlEnum.URL4660
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar()

    {
        archivoDescarga = null;

        int mesFinal = Integer.parseInt(bimestre) * 2;
        int mesInicial = mesFinal - 1;

        switch (opcionInforme) {
        case "1":
            generarF20A1(mesInicial, mesFinal);
            break;
        case "2":
            generarF20B(mesInicial, mesFinal);
            break;
        case "3":
            generarF20C1();
            break;
        default:
            break;
        }

        // </CODIGO_DESARROLLADO>
    }

    /*
     * Metodo encargado de la generacion de la consulta que sera
     * enviada para exportar la hoja de datos.
     * 
     */

    public String obtenerCondicion(int mesInicial, int mesFinal) {
        String condicion;
        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put(InformeSiaControladorEnum.PARAM57.getValue(), vigencia);

        if (filtraPorFechaDeDiligenciamiento) {

            condicion = Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM54.getValue(),
                            modulo,
                            reemplazar);

        }
        else {
            condicion = Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM55.getValue(),
                            modulo,
                            reemplazar);

        }
        return condicion;
    }

    public String generarTextoSalida(String[] titulos, StringBuilder sql,
        Map<String, Object> reemplazar) {

        if (!manejaAuxiliarPorFuenteEnPresupuesto) {
            sql.append(Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM49.getValue(),
                            modulo,
                            reemplazar));
        }
        else {
            sql.append(Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM50.getValue(),
                            modulo,
                            reemplazar));
        }

        StringBuilder textoSalida1 = new StringBuilder();

        for (String celda : titulos) {
            textoSalida1.append(celda).append(",");
        }

        String textoSalidaAux;
        textoSalidaAux = SysmanFunciones.concatenar(
                        textoSalida1.toString().substring(0,
                                        textoSalida1.toString().length() - 1),
                        "\n");
        return textoSalidaAux;
    }

    public void generarF20A1(int mesInicial, int mesFinal) {

        try {
            String nombreAarchivo = SysmanFunciones.concatenar(formatoCons,
                            vigencia, SysmanFunciones.padl(bimestre, 2, "0"),
                            "-", SysmanFunciones.padl(
                                            String.valueOf(Integer.parseInt(
                                                            bimestre)
                                                + 1),
                                            2, "0"),
                            InformeSiaControladorEnum.PARAM48.getValue());
            String[] titulos = { idioma.getString("TB_TB3387"),
                                 idioma.getString("TB_TB3388"),
                                 idioma.getString("TB_TB3389"),
                                 idioma.getString("TB_TB3390"),
                                 idioma.getString("TB_TB3391"),
                                 idioma.getString("TB_TB3392"),
                                 idioma.getString("TB_TB3393"),
                                 idioma.getString("TB_TB3394"),
                                 idioma.getString("TB_TB3395"),
                                 idioma.getString("TB_TB3396"),
                                 idioma.getString("TB_TB3397"),
                                 idioma.getString("TB_TB3398"),
                                 idioma.getString("TB_TB3399"),
                                 idioma.getString("TB_TB3400"),
                                 idioma.getString("TB_TB3401"),
                                 idioma.getString("TB_TB3402"),
                                 idioma.getString("TB_TB3403"),
                                 idioma.getString("TB_TB3404"),
                                 idioma.getString("TB_TB3405"),
                                 idioma.getString("TB_TB3406"),
                                 idioma.getString("TB_TB3407"),
                                 idioma.getString("TB_TB3408"),
                                 idioma.getString("TB_TB3409"),
                                 idioma.getString("TB_TB3410"),
                                 idioma.getString("TB_TB3411"),
                                 idioma.getString("TB_TB3412"),
                                 idioma.getString("TB_TB3413"),
                                 idioma.getString("TB_TB3414"),
                                 idioma.getString("TB_TB3415"),
                                 idioma.getString("TB_TB3416"),
                                 idioma.getString("TB_TB3417"),
                                 idioma.getString("TB_TB3418"),
                                 idioma.getString("TB_TB3419"),
                                 idioma.getString("TB_TB3420"),
                                 idioma.getString("TB_TB3421"),
                                 idioma.getString("TB_TB3422"),
                                 idioma.getString("TB_TB3423"),
                                 idioma.getString("TB_TB3424"),
                                 idioma.getString("TB_TB3425"),
                                 idioma.getString("TB_TB3426"),
                                 idioma.getString("TB_TB3427"),
                                 idioma.getString("TB_TB3428"),
                                 idioma.getString("TB_TB3429") };

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("condicion", obtenerCondicion(mesInicial, mesFinal));

            StringBuilder sql = new StringBuilder();
            String aux = generarTextoSalida(titulos, sql, reemplazar);

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            sql.toString());
            StringBuilder publicacion = new StringBuilder();
            StringBuilder textoSalida = new StringBuilder();
            textoSalida.append(aux);
            for (Registro registro : rs) {
                textoSalida.append(SessionUtil.getCompaniaIngreso().getNit())
                                .append(",");
                textoSalida.append(SessionUtil.getCompaniaIngreso().getNombre())
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM3
                                                                .getValue()),
                                                "ND"))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM4
                                                                .getValue()),
                                                0))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM5.getValue()),
                                "ND")).append(",");
                textoSalida.append(SessionUtil.getCompaniaIngreso()
                                .getDepartamento()).append(",");
                textoSalida.append(SessionUtil.getCompaniaIngreso().getCiudad())
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM6
                                                                .getValue()),
                                                "ND"))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM7
                                                                .getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM8
                                                                .getValue()),
                                                "ND").toString().replace(",",
                                                                ""))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM9
                                                                .getValue()),
                                "ND")
                                                .toString().replace(",", ""))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM10
                                                                .getValue()),
                                                "ND")
                                                                .toString()
                                                                .replace(",", ""))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM11
                                                                .getValue()),
                                "ND")
                                                .toString().replace(",", ""))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM12
                                                                .getValue()),
                                                "ND").toString().replace(",",
                                                                ""))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM13
                                                                .getValue()),
                                                0)
                                                                .toString()
                                                                .replace(",", "."))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM14
                                                                .getValue()),
                                                0)
                                                                .toString()
                                                                .replace(",", ""))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM15
                                                                .getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM16.getValue()),
                                "ND").toString().replace(",", "")).append(",");

                textoSalida.append((registro.getCampos()
                                .get(InformeSiaControladorEnum.PARAM17
                                                .getValue()) == null) ? "ND,"
                                                    : SysmanFunciones
                                                                    .convertirAFechaCadena(
                                                                                    (Date) registro.getCampos()
                                                                                                    .get(InformeSiaControladorEnum.PARAM17
                                                                                                                    .getValue()),
                                                                                    formatoFechaCons))
                                .append(",");

                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM18
                                                                .getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM19.getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM20
                                                                .getValue()),
                                "ND")).append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM21
                                                                .getValue()),
                                                "ND"))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM22
                                                                .getValue()),
                                                0))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM23
                                                                .getValue()),
                                                "ND"))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM24.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append(",");
                textoSalida.append("ND").append(",");
                textoSalida.append(
                                (registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM25
                                                                .getValue()) == null)
                                                                    ? "ND,"
                                                                    : SysmanFunciones
                                                                                    .convertirAFechaCadena(
                                                                                                    (Date) registro.getCampos()
                                                                                                                    .get(InformeSiaControladorEnum.PARAM25
                                                                                                                                    .getValue()),
                                                                                                    formatoFechaCons))
                                .append(",");
                textoSalida.append((registro.getCampos()
                                .get(InformeSiaControladorEnum.PARAM26
                                                .getValue()) == null) ? "ND,"
                                                    : SysmanFunciones
                                                                    .convertirAFechaCadena(
                                                                                    (Date) registro.getCampos()
                                                                                                    .get(InformeSiaControladorEnum.PARAM26
                                                                                                                    .getValue()),
                                                                                    formatoFechaCons))
                                .append(",");
                if (registro.getCampos().get(InformeSiaControladorEnum.PARAM27
                                .getValue()) != null) {
                    publicacion.append("SI");
                }
                textoSalida.append(publicacion).append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM28.getValue()),
                                "ND").toString().replace(",", ".")).append(",");
                textoSalida.append(
                                (registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM29
                                                                .getValue()) == null)
                                                                    ? "ND,"
                                                                    : SysmanFunciones
                                                                                    .convertirAFechaCadena(
                                                                                                    (Date) registro.getCampos()
                                                                                                                    .get(InformeSiaControladorEnum.PARAM29
                                                                                                                                    .getValue()),
                                                                                                    formatoFechaCons))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM30.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM31.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM32.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM33
                                                                .getValue()),
                                0).toString().replace(",", ".")).append(",");
                textoSalida.append((registro.getCampos()
                                .get(InformeSiaControladorEnum.PARAM34
                                                .getValue()) == null) ? "ND,"
                                                    : SysmanFunciones
                                                                    .convertirAFechaCadena(
                                                                                    (Date) registro.getCampos()
                                                                                                    .get(InformeSiaControladorEnum.PARAM34
                                                                                                                    .getValue()),
                                                                                    formatoFechaCons))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM35.getValue()),
                                "ND")).append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM36.getValue()),
                                "ND")).append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM37
                                                                .getValue()),
                                                0)
                                                                .toString()
                                                                .replace(",", "."))
                                .append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM38
                                                                .getValue()),
                                                0)
                                                                .toString()
                                                                .replace(",", "."))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM39.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM40.getValue()),
                                0)
                                                .toString().replace(",", "."))
                                .append("\n");
            }
            generarArchivoDescarga(rs, textoSalida, nombreAarchivo);
        }
        catch (ParseException e) {
            Logger.getLogger(InformeSiaControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void generarArchivoDescarga(List<Registro> rs,
        StringBuilder textoSalida, String nombreAarchivo) {
        if ((rs == null) || rs.isEmpty()) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgNoExisteInfo));
        }
        else {
            ByteArrayInputStream archivo;
            try {
                archivo = JsfUtil.serializarPlano(textoSalida.toString());
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                nombreAarchivo, appExcelCons);
            }
            catch (JRException | IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void generarF20B(int mesInicial, int mesFinal) {
        archivoDescarga = null;

        try {
            String nombreAarchivo = SysmanFunciones.concatenar(formatoCons,
                            vigencia, SysmanFunciones.padl(bimestre, 2, "0"),
                            InformeSiaControladorEnum.PARAM51.getValue());

            String[] titulos = { idioma.getString("TB_TB3430"),
                                 idioma.getString("TB_TB3388"),
                                 idioma.getString("TB_TB3394"),
                                 idioma.getString("TB_TB3431"),
                                 idioma.getString("TB_TB3432"),
                                 idioma.getString("TB_TB3433"),
                                 idioma.getString("TB_TB3434") };

            String sql;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("vigencia", vigencia);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);

            sql = Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM56.getValue(),
                            modulo,
                            reemplazar);
            StringBuilder textoSalida1 = new StringBuilder();
            for (String celda : titulos) {
                textoSalida1.append(celda).append(",");
            }
            String textoSalidaAux = SysmanFunciones
                            .concatenar(textoSalida1.toString().substring(0,
                                            textoSalida1.toString().length()
                                                - 1),
                                            "\n");

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            sql);

            String nit = SessionUtil.getCompaniaIngreso().getNit();
            StringBuilder textoSalida = new StringBuilder();
            textoSalida.append(textoSalidaAux);
            for (Registro registro : rs) {
                textoSalida.append((nit.contains("-") ? nit.substring(0,
                                nit.indexOf('-'))
                    : nit.substring(0, 1)).replace(",", "") + ",");
                textoSalida.append(SessionUtil.getCompaniaIngreso().getNombre()
                                .replace(",", "")).append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()),
                                "ND")).append(",");
                textoSalida.append(
                                nvl(registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM44
                                                                .getValue()),
                                                "ND"))
                                .append(",");
                textoSalida.append(nvl(registro.getCampos().get(
                                InformeSiaControladorEnum.PARAM45.getValue()),
                                "ND").toString().replace(",", ".")).append(",");
                textoSalida.append((registro.getCampos()
                                .get(InformeSiaControladorEnum.PARAM46
                                                .getValue()) == null) ? "ND,"
                                                    : registro.getCampos()
                                                                    .get(InformeSiaControladorEnum.PARAM46
                                                                                    .getValue()))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM47
                                                                .getValue()),
                                "ND")).append(" \n");
            }

            if ((rs == null) || rs.isEmpty()) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgNoExisteInfo));
            }
            else {
                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(textoSalida.toString());
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                nombreAarchivo, appExcelCons);
            }
        }
        catch (JRException | IOException e) {

            Logger.getLogger(InformeSiaControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void generarF20C1() {
        archivoDescarga = null;

        try {
            String nombreAarchivo = SysmanFunciones.concatenar(formatoCons,
                            vigencia, bimestre,
                            InformeSiaControladorEnum.PARAM52.getValue());
            String[] titulos = { idioma.getString("TB_TB3435"),
                                 idioma.getString("TB_TB3436"),
                                 idioma.getString("TB_TB3437"),
                                 idioma.getString("TB_TB3438"),
                                 idioma.getString("TB_TB3439"),
                                 idioma.getString("TB_TB3440") };

            String sql;
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("vigencia", vigencia);
            sql = Reporteador.resuelveConsulta(
                            InformeSiaControladorEnum.PARAM53.getValue(),
                            modulo,
                            reemplazar);
            StringBuilder textoSalida1 = new StringBuilder();
            for (String celda : titulos) {
                textoSalida1.append(celda + ",");
            }

            String textoSalidaAux = SysmanFunciones
                            .concatenar(textoSalida1.toString().substring(0,
                                            textoSalida1.toString().length()
                                                - 1),
                                            "\n");

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            sql);

            String nit = SessionUtil.getCompaniaIngreso().getNit();
            StringBuilder textoSalida = new StringBuilder();

            textoSalida.append(textoSalidaAux);

            for (Registro registro : rs) {

                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM41
                                                                .getValue()),
                                "ND")
                                                .toString().replace(",", ""))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()),
                                "ND")
                                                .toString().replace(",", ""))
                                .append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM42
                                                                .getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(InformeSiaControladorEnum.PARAM43
                                                                .getValue()),
                                "ND").toString().replace(",", "")).append(",");
                textoSalida.append(nit).append(",");
                textoSalida.append(nvl(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()),
                                "ND")).append(" \n");
            }

            if ((rs == null) || rs.isEmpty()) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgNoExisteInfo));
            }
            else {

                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(textoSalida.toString());
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                nombreAarchivo, appExcelCons);
            }
        }
        catch (JRException | IOException e) {

            Logger.getLogger(InformeSiaControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void oprimirCancelar(ActionEvent ac) {
        // NO ESTA IMPLEMENTADO
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public boolean isFiltraPorFechaDeDiligenciamiento() {
        return filtraPorFechaDeDiligenciamiento;
    }

    public void setFiltraPorFechaDeDiligenciamiento(
        boolean filtraPorFechaDeDiligenciamiento) {
        this.filtraPorFechaDeDiligenciamiento = filtraPorFechaDeDiligenciamiento;
    }

    public boolean isManejaAuxiliarPorFuenteEnPresupuesto() {
        return manejaAuxiliarPorFuenteEnPresupuesto;
    }

    public void setManejaAuxiliarPorFuenteEnPresupuesto(
        boolean manejaAuxiliarPorFuenteEnPresupuesto) {
        this.manejaAuxiliarPorFuenteEnPresupuesto = manejaAuxiliarPorFuenteEnPresupuesto;
    }

    public List<Registro> getListavigencia() {
        return listavigencia;
    }

    public void setListavigencia(List<Registro> listavigencia) {
        this.listavigencia = listavigencia;
    }

    public int getModulo() {
        return modulo;
    }

    public void setModulo(int modulo) {
        this.modulo = modulo;
    }

    public String getOpcionInforme() {
        return opcionInforme;
    }

    public void setOpcionInforme(String opcionInforme) {
        this.opcionInforme = opcionInforme;
    }

    public String getBimestre() {
        return bimestre;
    }

    public void setBimestre(String bimestre) {
        this.bimestre = bimestre;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}