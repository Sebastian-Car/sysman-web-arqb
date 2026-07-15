package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;

/**
 *
 * @author dmaldonado
 * @version 1, 17/11/2015
 */
@ManagedBean
@ViewScoped
public class NovedadserviciosControlador extends BeanBaseDatosAcme {

    private final String compania;
    private List<Registro> listaNovedad;
    private String titulo;
    private StreamedContent archivoDescarga;
    private String prTipoContrato;
    private String prTNumero;
    private String prTNovedad;
    private boolean cargaParametros;

    public NovedadserviciosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 344;
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                prTipoContrato = (String) parametrosEntrada
                                .get("prTipoContrato");
                prTNumero = (String) parametrosEntrada.get("prTNumero");
                prTNovedad = (String) parametrosEntrada.get("prTNovedad");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(NovedadserviciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "NOVEDADCONTRATO";
        buscarLlave();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "  SELECT "
            + "     COMPANIA,"
            + "     CLASEORDEN,"
            + "     ORDENDECOMPRA,"
            + "     NOVEDAD,"
            + "     CLASET,"
            + "     TIPOT,"
            + "     FECHA,"
            + "     FECHAINICIAL,"
            + "     FECHAFINAL,"
            + "     FECHAVENCIMIENTO,"
            + "     ANO,"
            + "     NUMERO,"
            + "     VALORTOTAL,"
            + "     OBSERVACIONES,"
            + "     DIAS_CONTRATO,"
            + "     VLR_ALQ_VEHICULO,"
            + "     POREJECUCION,"
            + "     VISITAIN,"
            + "     RESULTADOS,"
            + "     INDADICION,"
            + "     VALOR_LIBERADO,"
            + "     INDICADOR_CUMPLIMIENTO,"
            + "     VOBO,"
            + "     INDICADOR_NOMINA,"
            + "     TIPO_RETENCION,"
            + "     APORTESTE,"
            + "     CONSIDERANDOS,"
            + "     ORDENADOR,"
            + "     SUCURSAL,"
            + "     IMPRESO,"
            + "     CREATED_BY,"
            + "     DATE_CREATED,"
            + "     MODIFIED_BY,"
            + "     DATE_MODIFIED"
            + "         FROM "
            + "     NOVEDADCONTRATO";
    }

    @Override
    public void reasignarOrigenGrilla() {
        origenGrilla = "SELECT "
            + "     COMPANIA, "
            + "     NOVEDAD,"
            + "     CLASEORDEN,"
            + "     ORDENDECOMPRA,"
            + "     CLASET,"
            + "     TIPOT"
            + " FROM "
            + "     NOVEDADCONTRATO "
            + " WHERE NOVEDADCONTRATO.COMPANIA = '" + compania + "' "
            + "     AND NOVEDADCONTRATO.CLASEORDEN = '" + prTipoContrato + "'"
            + "     AND NOVEDADCONTRATO.ORDENDECOMPRA = " + prTNumero
            + "     AND NOVEDADCONTRATO.CLASET = 'N' "
            + "     AND NOVEDADCONTRATO.TIPOT = '" + prTNovedad + "'";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
        if (listaInicialF != null) {
            listaInicialF.setOrigen(origenDatos);
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaNovedad();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaNovedad();
    }

    @Override
    public void iniciarListasSubNulo() {
        // Metodo heredado
    }

    public void cargarListaNovedad() {
        listaNovedad = service.getListado(ConectorPool.ESQUEMA_SYSMAN, "SELECT "
            + "     NOVEDAD "
            + " FROM "
            + "     NOVEDADCONTRATO "
            + " WHERE COMPANIA = '" + compania + "' "
            + "     AND CLASEORDEN = " + prTipoContrato + ""
            + "     AND ORDENDECOMPRA = " + prTNumero + ""
            + " ORDER BY "
            + "     NOVEDAD");
    }

    public void oprimircmdPantalla(ActionEvent ac) {
        // Metodo heredado
    }

    public void oprimircmdImpresora(ActionEvent ac) {
        // Metodo heredado
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public List<Registro> getListaNovedad() {
        return listaNovedad;
    }

    public void setListaNovedad(List<Registro> listaNovedad) {
        this.listaNovedad = listaNovedad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCargaParametros() {
        return cargaParametros;
    }

    public void setCargaParametros(boolean cargaParametros) {
        this.cargaParametros = cargaParametros;
    }
}
