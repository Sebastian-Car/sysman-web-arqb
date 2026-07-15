/*-
 * FrmInformesSireciReporteador.java
 *
 * 1.0
 * 
 * 28/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.reportes;

import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanConstantes;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase utilizada como auxiliar del controlador
 * FrmInformesSireciControlador, encargada de crear una hoja de datos
 * personalizada.
 * 
 * @see {@link com.sysman.bancoproyectos.FrmInformesSireciControlador}
 * 
 * @version 1.0, 28/03/2018
 * @author pespitia
 *
 */
public class FrmInformesSireciReporteador {

    private Log logger;

    /**
     * Metodo utilizado para personalizar la plantilla de excel de la
     * opcion de menu: 5203060101.
     * 
     * @param workbook
     * -> Objeto que guarda la configuracion de la plantilla.
     * @param datosExcel
     * -> Datos a ingresar en la plantilla.
     * @return
     */
    public ByteArrayInputStream exportarHojaDatosProyInv(
        XSSFWorkbook workbook, String datosExcel) {
        ByteArrayInputStream input = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String[] datos = datosExcel
                            .split(SysmanConstantes.SEPARADOR_REG);

            XSSFColor mycolor = new XSSFColor(new Color(102, 102, 153));

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setBorderBottom((short) 1);
            style.setBorderLeft((short) 1);
            style.setBorderTop((short) 1);
            style.setBorderRight((short) 1);
            ((XSSFCellStyle) style).setFillForegroundColor(mycolor);

            Font headerFont = workbook.createFont();
            headerFont.setColor(Font.BOLDWEIGHT_BOLD);
            style.setFont(headerFont);
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.index);

            style.setFillPattern(CellStyle.SOLID_FOREGROUND);

            String[] colum;
            Row row = null;
            XSSFSheet sheet = workbook.getSheetAt(0);
            int tipo = 0;

            for (int i = 0; i < datos.length; i++) {
                colum = datos[i].split(SysmanConstantes.SEPARADOR_COL);

                row = sheet.createRow(i + 10);

                int k = 0;

                for (int j = 0; j < colum.length; j++) {
                    Cell nCell;

                    nCell = row.createCell(k);
                    nCell.setCellValue(colum[j]);

                    if (nCell.getColumnIndex() == 0) {
                        nCell.setCellStyle(style);
                    }

                    if (tipo == 1 && j == 1) {
                        k = 3;
                    }

                    k++;
                }
            }

            workbook.write(out);
            out.close();
            workbook.close();

            input = new ByteArrayInputStream(out.toByteArray());
        }
        catch (NumberFormatException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return input;
    }

    /**
     * Metodo utilizado para personalizar la plantilla de excel de la
     * opcion de menu: 5203060102.
     * 
     * @param workbook
     * -> Objeto que guarda la configuracion de la plantilla.
     * @param datosExcel
     * -> Datos a ingresar en la plantilla.
     * @return
     */
    public ByteArrayInputStream exportarHojaDatosInfEficacia(
        XSSFWorkbook workbook, String datosExcel) {
        ByteArrayInputStream input = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String[] datos = datosExcel
                            .split(SysmanConstantes.SEPARADOR_REG);

            String[] colum;
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < datos.length; i++) {
                colum = datos[i].split(SysmanConstantes.SEPARADOR_COL);

                for (int j = 0; j < colum.length; j++) {
                    Cell nCell = sheet.getRow(i + (i == datos.length ? 0 : 1))
                                    .getCell(j);

                    nCell.setCellValue(colum[j]);
                }
            }

            workbook.write(out);
            out.close();
            workbook.close();

            input = new ByteArrayInputStream(out.toByteArray());
        }
        catch (NumberFormatException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return input;
    }

    /**
     * Metodo utilizado para personalizar la plantilla de excel de la
     * opcion de menu: 5203060103.
     * 
     * @param workbook
     * -> Objeto que guarda la configuracion de la plantilla.
     * @param datosExcel
     * -> Datos a ingresar en la plantilla.
     * @return
     */
    public ByteArrayInputStream exportarHojaDatosProyOrientados(
        XSSFWorkbook workbook, String datosExcel) {
        ByteArrayInputStream input = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String[] datos = datosExcel.split(SysmanConstantes.SEPARADOR_REG);

            // code

            workbook.write(out);
            out.close();
            workbook.close();

            input = new ByteArrayInputStream(out.toByteArray());
        }
        catch (NumberFormatException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return input;
    }

    public ByteArrayInputStream exportarHojaDatosSaneAgua(XSSFWorkbook workbook,
        String datosExcel) {
        ByteArrayInputStream input = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String[] datos = datosExcel
                            .split(SysmanConstantes.SEPARADOR_REG);

            String[] colum;
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < datos.length; i++) {
                colum = datos[i].split(SysmanConstantes.SEPARADOR_COL);

                for (int j = 0; j < colum.length; j++) {
                    Cell nCell = sheet.getRow(i + 10)
                                    .getCell(j);

                    nCell.setCellValue(colum[j]);
                }
            }

            workbook.write(out);
            out.close();
            workbook.close();

            input = new ByteArrayInputStream(out.toByteArray());
        }
        catch (NumberFormatException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return input;
    }

    /**
     * @return the logger
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * @param logger
     * the logger to set
     */
    public void setLogger(Log logger) {
        this.logger = logger;
    }

}
