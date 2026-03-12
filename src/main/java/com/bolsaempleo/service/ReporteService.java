package com.bolsaempleo.service;

import com.bolsaempleo.model.Puesto;
import com.bolsaempleo.repository.PuestoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteService {

    private final PuestoRepository puestoRepo;

    public ReporteService(PuestoRepository puestoRepo) {
        this.puestoRepo = puestoRepo;
    }

    public byte[] reportePuestosPorMes(int mes, int anio) throws DocumentException {
        List<Puesto> puestos = puestoRepo.findByMesYAnio(mes, anio);

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 10);

        String[] meses = {"","Enero","Febrero","Marzo","Abril","Mayo","Junio",
                          "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

        doc.add(new Paragraph("Bolsa de Empleo", fuenteTitulo));
        doc.add(new Paragraph("Reporte de puestos — " + meses[mes] + " " + anio, fuenteHeader));
        doc.add(new Paragraph("Total: " + puestos.size(), fuenteNormal));
        doc.add(Chunk.NEWLINE);

        if (puestos.isEmpty()) {
            doc.add(new Paragraph("No hay puestos en este período.", fuenteNormal));
        } else {
            PdfPTable tabla = new PdfPTable(new float[]{1f, 4f, 3f, 2f, 1.5f, 1.5f});
            tabla.setWidthPercentage(100);

            for (String col : new String[]{"ID","Descripción","Empresa","Salario","Tipo","Activo"}) {
                PdfPCell celda = new PdfPCell(new Phrase(col, fuenteHeader));
                celda.setBackgroundColor(new BaseColor(41, 128, 185));
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setPadding(5);
                tabla.addCell(celda);
            }

            for (Puesto p : puestos) {
                tabla.addCell(new Phrase(p.getId().toString(), fuenteNormal));
                tabla.addCell(new Phrase(p.getDescripcion(), fuenteNormal));
                tabla.addCell(new Phrase(p.getEmpresa().getNombre(), fuenteNormal));
                tabla.addCell(new Phrase(p.getSalario() != null ? "₡" + p.getSalario() : "N/A", fuenteNormal));
                tabla.addCell(new Phrase(p.getTipo().name(), fuenteNormal));
                tabla.addCell(new Phrase(p.isActivo() ? "Sí" : "No", fuenteNormal));
            }
            doc.add(tabla);
        }

        doc.close();
        return baos.toByteArray();
    }
}