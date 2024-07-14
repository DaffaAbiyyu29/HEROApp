package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;


import static com.itextpdf.kernel.colors.ColorConstants.BLACK;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;

public class MaintenanceHistoryFragment extends Fragment {

    private HeavyEngineViewModel mViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;

    public static MaintenanceHistoryFragment newInstance() {
        return new MaintenanceHistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_history, container, false);

        Log.d("MaintenanceFragment", "onCreateView: Fragment created successfully");

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);

        mViewModel.getHeavyEngineList().observe(getViewLifecycleOwner(), new Observer<List<HeavyEngine>>() {
            @Override
            public void onChanged(List<HeavyEngine> heavyEngineList) {
                if (heavyEngineList != null) {
                    mHeavyEngineAdapter = new HeavyEngineAdapter(heavyEngineList, true);
                    recyclerView.setAdapter(mHeavyEngineAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextInputEditText search_view_dashboard = view.findViewById(R.id.search_view_dashboard);
        search_view_dashboard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu melakukan apa pun sebelum teks diubah
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Panggil searchUnitByName dari ViewModel
                mViewModel.fetchDataUnitByName(s.toString(), null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu melakukan apa pun setelah teks diubah
            }
        });

        MaterialButton delBtn = view.findViewById(R.id.btn_search_delete);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view_dashboard.setText("");
            }
        });

        ((MainActivity) getActivity()).showBackButton();

        MaterialButton printpdfBtn = view.findViewById(R.id.btn_print_pdf);
        printpdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private List<HeavyEngine> filterAvailableItems(List<HeavyEngine> items) {
        return items.stream()
                .filter(item -> "Sedang Dalam Perawatan".equals(item.getStatus()))
                .collect(Collectors.toList());
    }

    private void createPdf() throws IOException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Reporting_TRPAB.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDocument);

        // Get the TRPAB drawable
        Drawable trpabDrawable = ContextCompat.getDrawable(getContext(), R.drawable.trpab);
        Bitmap trpabBitmap = ((BitmapDrawable) trpabDrawable).getBitmap();
        ByteArrayOutputStream trpabStream = new ByteArrayOutputStream();
        trpabBitmap.compress(Bitmap.CompressFormat.PNG, 100, trpabStream);
        byte[] trpabBitmapData = trpabStream.toByteArray();
        ImageData trpabImageData = ImageDataFactory.create(trpabBitmapData);
        Image trpabImage = new Image(trpabImageData);
        trpabImage.setHeight(50.f);
        trpabImage.setWidth(50.f);

        // Get the Astratech drawable
        Drawable astratechDrawable = ContextCompat.getDrawable(getContext(), R.drawable.astratech_long);
        Bitmap astratechBitmap = ((BitmapDrawable) astratechDrawable).getBitmap();
        ByteArrayOutputStream astratechStream = new ByteArrayOutputStream();
        astratechBitmap.compress(Bitmap.CompressFormat.PNG, 100, astratechStream);
        byte[] astratechBitmapData = astratechStream.toByteArray();
        ImageData astratechImageData = ImageDataFactory.create(astratechBitmapData);
        Image astratechImage = new Image(astratechImageData);
        astratechImage.setHeight(40.3f);
        astratechImage.setWidth(177.2f);

        Text teknikText = new Text("TEKNIK ALAT BERAT").setFontSize(18).setBold();
        Paragraph teknikParagraph = new Paragraph(teknikText);
        teknikParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Text periodicText = new Text("PERIODIC SERVICE").setFontSize(14);
        Paragraph periodicParagraph = new Paragraph(periodicText);
        periodicParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Define column widths for the table
        float[] columnWidths = {130f, 100f, 160f, 90f, 90f, 90f, 100f, 100f, 100f, 100f}; // tambahkan lebar kolom
        Table table = new Table(columnWidths);
        // Baris 1
        table.addCell(new Cell(4, 1).add(new Paragraph("LEMBAR KERJA CHECK SHEET SERVIS")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("UNIT")).setBorder(Border.NO_BORDER)).setBorderTop(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER)).setBorderTop(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 3).add(new Paragraph("WAKTU PROSES")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("PELAKSANA")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER)).setBorderTop(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER)).setBorderTop(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 2
        table.addCell(new Cell(1, 1).add(new Paragraph("LOKASI")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("TANGGAL")));
        table.addCell(new Cell(1, 1).add(new Paragraph("JAM")));
        table.addCell(new Cell(1, 1).add(new Paragraph("DIPERIKSA")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 3
        table.addCell(new Cell(1, 1).add(new Paragraph("H/M")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("START")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("DIKETAHUI")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 4
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER)).setBorderBottom(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER)).setBorderBottom(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("FINISH")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 5
        table.addCell(new Cell(1, 2).add(new Paragraph("GAMBAR KERJA")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 2).add(new Paragraph("PEKERJAAN")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 4).add(new Paragraph("KONDISI")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("OK")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("NOK")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        // Baris 6
        table.addCell(new Cell(1, 10).add(new Paragraph(new Text("INSPECTION AND MAINTENANCE").setBold())).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));

        // Baris 7 large
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));

        // Baris 8 large
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));

        // Baris 9 large
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)).setBorderTop(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 2).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 4).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));
        table.addCell(new Cell(4, 1).add(new Paragraph(" ")).setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(BLACK, 1)).setBorderRight(new SolidBorder(BLACK, 1)));

        astratechImage.setFixedPosition(30, 530);
        trpabImage.setFixedPosition(770, 520);
        teknikParagraph.setFixedPosition(390, 540, 200);
        periodicParagraph.setFixedPosition(415, 525, 200);
        table.setFixedPosition(25,10,795);

        document.add(astratechImage);
        document.add(trpabImage);
        document.add(teknikParagraph);
        document.add(periodicParagraph);
        document.add(table);

        document.close();
        Toast.makeText(getContext(), "PDF successfully created", Toast.LENGTH_LONG).show();
    }
}
