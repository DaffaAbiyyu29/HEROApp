package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;


import static com.itextpdf.kernel.colors.ColorConstants.BLACK;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
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
import com.google.gson.Gson;
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

import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.model.MaintenanceReport;
import id.ac.astra.polytechnic.trpab.data.viewmodel.MaintananceReportViewModel;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.HeavyEngineAdapter;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.viewmodel.HeavyEngineViewModel;

public class MaintenanceHistoryFragment extends Fragment {

    private HeavyEngineViewModel mViewModel;
    private MaintananceReportViewModel mReportViewModel;
    private RecyclerView recyclerView;
    private HeavyEngineAdapter mHeavyEngineAdapter;
    private List<HeavyEngine> dashboardItemList;
    private List<MaintenanceReport> maintenanceReports;
    String id, nama, role, npk, nim;

    public static MaintenanceHistoryFragment newInstance() {
        return new MaintenanceHistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintenance_history, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("ID_USER");
            nama = bundle.getString("NAMA_USER");
            role = bundle.getString("ROLE_USER");
            npk = bundle.getString("NPK");
            nim = bundle.getString("NIM");
        }
        Log.d("oooo99", nama);

        recyclerView = view.findViewById(R.id.recycler_view_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel = new ViewModelProvider(this).get(HeavyEngineViewModel.class);
        mReportViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                return (T) new MaintananceReportViewModel(apiService);
            }
        }).get(MaintananceReportViewModel.class);
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
                mReportViewModel.fetchDataForReport("3", "2", new MaintananceReportViewModel.ReportCallback() {
                    @Override
                    public void onSuccess(List<MaintenanceReport> result) {
                        maintenanceReports = result;
                        // Display the result
                        Gson gson = new Gson();
                        String jsonResponse = gson.toJson(result);
                        Log.d("MaintenanceForReport Data", jsonResponse);
                        Toast.makeText(getContext(), "Data fetched successfully!", Toast.LENGTH_SHORT).show();
                        try {
                            createPdf();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getContext(), "Failed to fetch data for report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MaintenanceHistoryFragment", "Error fetching data for report", t);
                    }
                });
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
        MaintenanceReport report = maintenanceReports.get(0);
        String unitName = report.getUnt_nama();
        String Hoursmeter = report.getPbk_hours_meter();
        String BeginDate = report.getPbk_tanggal_awal();
        String BeginTime = report.getPbk_jam_awal();
        String EndDate = report.getPbk_tanggal_akhir();
        String EndTime = report.getPbk_jam_akhir();

        String technicianName = report.getNama_pelaksana();

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Reporting_TRPAB.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdfDocument);

        // Set the default font for the document
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        document.setFont(font);

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

        Text teknikText = new Text("TEKNIK ALAT BERAT").setFont(boldFont).setFontSize(18).setBold();
        Paragraph teknikParagraph = new Paragraph(teknikText);
        teknikParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Text periodicText = new Text("PERIODIC SERVICE").setFont(font).setFontSize(14);
        Paragraph periodicParagraph = new Paragraph(periodicText);
        periodicParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // nama unit
        Text namaUnitText = new Text(unitName).setFont(font).setFontSize(12);
        Paragraph namaUnitParagraph = new Paragraph(namaUnitText);

        // lokasi
        Text lokasiText = new Text("AstraTech").setFont(font).setFontSize(12);
        Paragraph lokasiParagraph = new Paragraph(lokasiText);

        // hours meter
        Text hoursMeterText = new Text(Hoursmeter).setFont(font).setFontSize(12);
        Paragraph hoursMeterParagraph = new Paragraph(hoursMeterText);

        // tanggal start
        Text begindateText = new Text(BeginDate).setFont(font).setFontSize(12);
        Paragraph begindateParagraph = new Paragraph(begindateText);

        // jam start
        Text begintimeText = new Text(BeginTime).setFont(font).setFontSize(12);
        Paragraph begintimeParagraph = new Paragraph(begintimeText);

        // tanggal finish
        Text enddateText = new Text(EndDate).setFont(font).setFontSize(12);
        Paragraph enddateParagraph = new Paragraph(enddateText);

        // jam finish
        Text endtimeText = new Text(EndTime).setFont(font).setFontSize(12);
        Paragraph endtimeParagraph = new Paragraph(endtimeText);

        // pelaksana
        Text pelaksanaText = new Text(technicianName).setFont(font).setFontSize(12);
        Paragraph pelaksanaParagraph = new Paragraph(pelaksanaText);

        // diperiksa
        Text checkerText = new Text(nama).setFont(font).setFontSize(12);
        Paragraph checkerParagraph = new Paragraph(checkerText);

        // diketahui
        Text asesorText = new Text(nama).setFont(font).setFontSize(12);
        Paragraph asesorParagraph = new Paragraph(asesorText);

        // Get the checkmark
        Drawable checkmarkDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checklist_green);  // assuming checkmark.png is the image of the check mark
        Bitmap checkmarkBitmap = ((BitmapDrawable) checkmarkDrawable).getBitmap();
        ByteArrayOutputStream checkmarkStream = new ByteArrayOutputStream();
        checkmarkBitmap.compress(Bitmap.CompressFormat.PNG, 100, checkmarkStream);
        byte[] checkmarkBitmapData = checkmarkStream.toByteArray();
        ImageData checkmarkImageData = ImageDataFactory.create(checkmarkBitmapData);

        // Get the work1 drawable
        Drawable work1Drawable = ContextCompat.getDrawable(getContext(), R.drawable.astratech_long);
        Bitmap work1Bitmap = ((BitmapDrawable) work1Drawable).getBitmap();
        ByteArrayOutputStream work1Stream = new ByteArrayOutputStream();
        work1Bitmap.compress(Bitmap.CompressFormat.PNG, 100, work1Stream);
        byte[] work1BitmapData = work1Stream.toByteArray();
        ImageData work1ImageData = ImageDataFactory.create(work1BitmapData);
        Image work1Image = new Image(work1ImageData);
        work1Image.setHeight(79.2f);
        work1Image.setWidth(160.7f);

        Text dowork1Text = new Text("1.Mengganti oli samping").setFont(font).setFontSize(12);
        Paragraph dowork1Paragraph = new Paragraph(dowork1Text);
        Text conditionwork1Text = new Text("harus tidak ada kebocoran oli").setFont(font).setFontSize(12);
        Paragraph conditionwork1Paragraph = new Paragraph(conditionwork1Text);
        Image checkmarkImage1 = new Image(checkmarkImageData);
        checkmarkImage1.setHeight(12f);
        checkmarkImage1.setWidth(12f);

        // Get the work2 drawable
        Drawable work2Drawable = ContextCompat.getDrawable(getContext(), R.drawable.astratech_long);
        Bitmap work2Bitmap = ((BitmapDrawable) work2Drawable).getBitmap();
        ByteArrayOutputStream work2Stream = new ByteArrayOutputStream();
        work2Bitmap.compress(Bitmap.CompressFormat.PNG, 100, work2Stream);
        byte[] work2BitmapData = work2Stream.toByteArray();
        ImageData work2ImageData = ImageDataFactory.create(work1BitmapData);
        Image work2Image = new Image(work1ImageData);
        work2Image.setHeight(79.2f);
        work2Image.setWidth(160.7f);

        Text dowork2Text = new Text("2.Mengganti Handle Rem").setFont(font).setFontSize(12);
        Paragraph dowork2Paragraph = new Paragraph(dowork2Text);
        Text conditionwork2Text = new Text("pastikan melepas pengunci terlebih dulu").setFont(font).setFontSize(12);
        Paragraph conditionwork2Paragraph = new Paragraph(conditionwork2Text);
        Image checkmarkImage2 = new Image(checkmarkImageData);
        checkmarkImage2.setHeight(12f);
        checkmarkImage2.setWidth(12f);


        // Get the work3 drawable
        Drawable work3Drawable = ContextCompat.getDrawable(getContext(), R.drawable.astratech_long);
        Bitmap work3Bitmap = ((BitmapDrawable) work3Drawable).getBitmap();
        ByteArrayOutputStream work3Stream = new ByteArrayOutputStream();
        work3Bitmap.compress(Bitmap.CompressFormat.PNG, 100, work3Stream);
        byte[] work3BitmapData = work3Stream.toByteArray();
        ImageData work3ImageData = ImageDataFactory.create(work3BitmapData);
        Image work3Image = new Image(work3ImageData);
        work3Image.setHeight(79.2f);
        work3Image.setWidth(160.7f);

        Text dowork3Text = new Text("3.Mengganti Paddle Gas").setFont(font).setFontSize(12);
        Paragraph dowork3Paragraph = new Paragraph(dowork3Text);
        Text conditionwork3Text = new Text("perbaiki posisi paddle gas").setFont(font).setFontSize(12);
        Paragraph conditionwork3Paragraph = new Paragraph(conditionwork3Text);
        Image checkmarkImage3 = new Image(checkmarkImageData);
        checkmarkImage3.setHeight(12f);
        checkmarkImage3.setWidth(12f);

        // Define column widths for the table
        float[] columnWidths = {130f, 100f, 160f, 90f, 90f, 90f, 100f, 100f, 100f, 100f}; // tambahkan lebar kolom
        Table table = new Table(columnWidths);
        // Baris 1
        table.addCell(new Cell(4, 1).add(new Paragraph("LEMBAR KERJA CHECK SHEET SERVIS")).setFont(boldFont).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
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
        table.addCell(new Cell(1, 1).add(new Paragraph("TANGGAL")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("JAM")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("DIPERIKSA")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 3
        table.addCell(new Cell(1, 1).add(new Paragraph("H/M")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("START")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("")));
        table.addCell(new Cell(1, 1).add(new Paragraph("DIKETAHUI")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph(":")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER).setBorderRight(new SolidBorder(BLACK, 1)));
        // Baris 4
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER)).setBorderBottom(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER)).setBorderBottom(new SolidBorder(BLACK, 1));
        table.addCell(new Cell(1, 1).add(new Paragraph("FINISH")).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER));
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
        table.addCell(new Cell(1, 10).add(new Paragraph(new Text("INSPECTION AND MAINTENANCE")).setFont(boldFont).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER)));

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
        periodicParagraph.setFixedPosition(427, 522, 200);
        table.setFixedPosition(25,10,795);

        namaUnitParagraph.setFixedPosition(211, 497, 100);
        lokasiParagraph.setFixedPosition(211, 475, 100);
        hoursMeterParagraph.setFixedPosition(211, 454, 100);

        begindateParagraph.setFixedPosition(394, 457, 100);
        begintimeParagraph.setFixedPosition(473, 457, 100);
        enddateParagraph.setFixedPosition(394, 432, 100);
        endtimeParagraph.setFixedPosition(473, 432, 100);

        pelaksanaParagraph.setFixedPosition(625, 497, 150);
        checkerParagraph.setFixedPosition(625, 475, 150);
        asesorParagraph.setFixedPosition(625, 454, 150);

        work1Image.setFixedPosition(35, 285);
        dowork1Paragraph.setFixedPosition(212, 365, 150);
        conditionwork1Paragraph.setFixedPosition(390, 365, 300);
        checkmarkImage1.setFixedPosition(1, 707,315);

        work2Image.setFixedPosition(35, 158);
        dowork2Paragraph.setFixedPosition(212, 240, 150);
        conditionwork2Paragraph.setFixedPosition(390, 240, 300);
        checkmarkImage2.setFixedPosition(1, 707, 195);

        work3Image.setFixedPosition(35, 40);
        dowork3Paragraph.setFixedPosition(212, 113, 150);
        conditionwork3Paragraph.setFixedPosition(390, 113, 300);
        checkmarkImage3.setFixedPosition(1, 707, 75);

        document.add(astratechImage);
        document.add(trpabImage);
        document.add(teknikParagraph);
        document.add(periodicParagraph);
        document.add(table);

        document.add(namaUnitParagraph);
        document.add(lokasiParagraph);
        document.add(hoursMeterParagraph);
        document.add(begindateParagraph);
        document.add(begintimeParagraph);
        document.add(enddateParagraph);
        document.add(endtimeParagraph);
        document.add(pelaksanaParagraph);
        document.add(checkerParagraph);
        document.add(asesorParagraph);

        document.add(work1Image);
        document.add(dowork1Paragraph);
        document.add(conditionwork1Paragraph);
        document.add(checkmarkImage1);

        document.add(work2Image);
        document.add(dowork2Paragraph);
        document.add(conditionwork2Paragraph);
        document.add(checkmarkImage2);

        document.add(work3Image);
        document.add(dowork3Paragraph);
        document.add(conditionwork3Paragraph);
        document.add(checkmarkImage3);

        document.close();
        Toast.makeText(getContext(), "PDF successfully created", Toast.LENGTH_LONG).show();

    }
}
