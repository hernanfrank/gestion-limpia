package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMovimientoCaja;
import com.burbujas.gestionlimpia.models.repositories.IMovimientoCajaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import static java.time.temporal.TemporalAdjusters.*;

import java.util.Date;
import java.util.List;

@Service("CajaServiceImpl")
public class CajaServiceImpl implements ICajaService{

    private final IMovimientoCajaRepository movimientoCajaRepository;
    @Value("${app.database.tmp_path}")
    private String tmpPath; // termina en /

    @Autowired
    public CajaServiceImpl(IMovimientoCajaRepository MovimientoCajaRepository) {
        this.movimientoCajaRepository = MovimientoCajaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCaja> findAllMovimientosCajaOrderByFechaDesc() {
        return this.movimientoCajaRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public List<MovimientoCaja> findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(Date fechaDesde, Date fechaHasta) {
        return this.movimientoCajaRepository.findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(fechaDesde, fechaHasta);
    }

    @Override
    public Double sumAllByMes(String fecha) {
        LocalDate fechaDesde = LocalDate.parse(fecha).withDayOfMonth(1);
        LocalDate fechaHasta = fechaDesde.with(lastDayOfMonth());
        Double val = this.movimientoCajaRepository.sumAllByFechaAfterAndFechaBefore(Timestamp.valueOf(fechaDesde.atStartOfDay()), Timestamp.valueOf(fechaHasta.atTime(23,59,59)));
        return val == null ? 0.0 : val;//si es null, no hay movimientos entre las fechas
    }

    @Override
    public List<Object[]> sumAllGroupByMes(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha).with(lastDayOfMonth());
        return this.movimientoCajaRepository.sumAllGroupByMes(Timestamp.valueOf(fechaHasta.atTime(23,59,59)));
    }

    @Override
    public List<Object[]> sumAllGroupByDia(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha);
        return this.movimientoCajaRepository.sumAllGroupByDia(Timestamp.valueOf(fechaHasta.atStartOfDay()));
    }

    @Override
    public List<Object[]> sumAllGroupByTipoCaja(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha);
        return this.movimientoCajaRepository.sumAllGroupByTipoCaja(Timestamp.valueOf(fechaHasta.atStartOfDay()));
    }

    @Override
    public List<Object[]> sumAllGroupByTipoPedido(String fecha) {
        LocalDate fechaHasta = LocalDate.parse(fecha);
        return this.movimientoCajaRepository.sumAllGroupByTipoPedido(Timestamp.valueOf(fechaHasta.atStartOfDay()));
    }

    @Override
    public MovimientoCaja findMovimientoCajaById(Long id) {
        return this.movimientoCajaRepository.findById(id).orElse(null);
    }

    @Override
    public List<MovimientoCaja> findAllMovimientosByTipoCaja(TipoCaja tipoCaja) {
        return this.movimientoCajaRepository.findAllByTipoCaja(tipoCaja);
    }

    @Override
    public List<MovimientoCaja> findAllMovimientosCajaEliminados() {
        return this.movimientoCajaRepository.findAllMovimientosCajaEliminados();
    }

    @Override
    public void saveMovimientoCaja(MovimientoCaja movimientoCaja) {
        this.movimientoCajaRepository.save(movimientoCaja);
    }

    @Override
    public void deleteMovimientoCaja(MovimientoCaja movimientoCaja) {
        if(movimientoCaja.getPedido() != null){
            throw new IllegalArgumentException("No se puede eliminar un movimiento de caja generado por el cobro pedido.");
        }else{
            this.movimientoCajaRepository.delete(movimientoCaja);
        }
    }

    @Override
    public void restaurarMovimientoCaja(Long id){
        this.movimientoCajaRepository.restaurarMovimientoCaja(id);
    }

    @Override
    public MovimientoCaja findMovimientoCajaByReabastecimiento(Reabastecimiento reabastecimiento) {
        return this.movimientoCajaRepository.findMovimientoCajaByReabastecimiento(reabastecimiento);
    }

    @Override
    public MovimientoCaja generarMovimientoPorCobranzaPedido(Pedido pedido, TipoCaja tipoCaja){
        try{
            MovimientoCaja movimientoCobranza = new MovimientoCaja();
            Date ahora = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            movimientoCobranza.setDescripcion("Generado por cobranza de pedido N°"+pedido.getId()+" del cliente "+pedido.getCliente().getNombreApellido()+" en la fecha "+formatter.format(ahora)+".");
            movimientoCobranza.setFecha(ahora);
            movimientoCobranza.setTipoMovimientoCaja(TipoMovimientoCaja.INGRESO);
            movimientoCobranza.setTipoCaja(tipoCaja);
            movimientoCobranza.setMonto(pedido.getPrecio());
            movimientoCobranza.setPedido(pedido);
            this.movimientoCajaRepository.save(movimientoCobranza);
            return movimientoCobranza;
        }catch (Exception e){
            System.out.println("Excepción capturada al cobrar un pedido: "+e);
            throw e;
        }
    }

    @Override
    public File exportarMovimientosCaja(Date fechaDesde, Date fechaHasta, String nombreArchivo) throws Exception{
        List<MovimientoCaja> movimientos = this.movimientoCajaRepository.findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(fechaDesde, fechaHasta);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Movimientos Caja");

        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setFontHeightInPoints((short) 10);
        headerStyle.setFont(boldFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CreationHelper creationHelper = workbook.getCreationHelper();

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(creationHelper.createDataFormat().getFormat("\"$\"#,##0.00"));

        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFont(boldFont);

        // Encabezados
        Row header = sheet.createRow(0);
        String[] columnas = {"ID", "Fecha", "Descripción", "Caja", "Tipo", "Monto"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        double totalEfectivo = 0;
        double totalBanco = 0;
        for (MovimientoCaja m : movimientos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getId());

            Cell fechaCell = row.createCell(1);
            fechaCell.setCellValue(DateFormat.getDateInstance(DateFormat.SHORT).format(m.getFecha()));
            fechaCell.setCellStyle(dateStyle);

            row.createCell(2).setCellValue(m.getDescripcion());

            row.createCell(3).setCellValue(m.getTipoCaja().getDisplayValue());
            row.createCell(4).setCellValue(m.getTipoMovimientoCaja().getDisplayValue());

            Cell montoCell = row.createCell(5);
            double monto = m.getMonto();
            if(m.getTipoMovimientoCaja().equals(TipoMovimientoCaja.EGRESO) ||
               m.getTipoMovimientoCaja().equals(TipoMovimientoCaja.NOTACREDITO)){
                monto *= -1; // cambio signo signo
            }
            montoCell.setCellValue(monto);
            montoCell.setCellStyle(moneyStyle);

            if(m.getTipoCaja().equals(TipoCaja.EFECTIVO)) {
                totalEfectivo += monto;
            }else{
                totalBanco += monto;
            }
        }

        // Total caja efectivo
        Row totalEfectivoRow = sheet.createRow(rowNum);
        Cell totalEfectivoLabel = totalEfectivoRow.createCell(4);
        totalEfectivoLabel.setCellValue("TOTAL EFECTIVO:");
        totalEfectivoLabel.setCellStyle(totalStyle);

        Cell totalEfectivoValue = totalEfectivoRow.createCell(5);
        totalEfectivoValue.setCellValue(totalEfectivo);
        totalEfectivoValue.setCellStyle(moneyStyle);
        totalEfectivoValue.getCellStyle().setFont(boldFont);

        // Total caja banco
        Row totalBancoRow = sheet.createRow(++rowNum);
        Cell totalBancoLabel = totalBancoRow.createCell(4);
        totalBancoLabel.setCellValue("TOTAL BANCO:");
        totalBancoLabel.setCellStyle(totalStyle);

        Cell totalBancoValue = totalBancoRow.createCell(5);
        totalBancoValue.setCellValue(totalBanco);
        totalBancoValue.setCellStyle(moneyStyle);
        totalBancoValue.getCellStyle().setFont(boldFont);

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar en disco
        File archivo = new File(this.tmpPath + nombreArchivo);
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            workbook.write(fos);
            workbook.close();
        }

        return archivo;
    }

}
