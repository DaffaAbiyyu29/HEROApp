package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import static com.itextpdf.kernel.colors.ColorConstants.BLACK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.R;
import id.ac.astra.polytechnic.trpab.data.adapter.RiwayatAdapter;
import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.model.ActionForMaintananceReport;
import id.ac.astra.polytechnic.trpab.data.model.MaintenanceReport;
import id.ac.astra.polytechnic.trpab.data.model.RiwayatMaintenance;
import id.ac.astra.polytechnic.trpab.data.viewmodel.MaintananceReportViewModel;
import id.ac.astra.polytechnic.trpab.databinding.FragmentRiwayatMaintananceBinding;
import id.ac.astra.polytechnic.trpab.ui.activity.MainActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintananceDetailHistoryFragment extends Fragment {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private MaintananceReportViewModel mReportViewModel;
    private RecyclerView recyclerView;
    private RiwayatAdapter mRiwayatAdapter;
    private List<RiwayatMaintenance> riwayatMaintenanceList;
    private String untid;
    private FragmentRiwayatMaintananceBinding binding;
    private static final String TAG = "MaintananceDetailHistory";

    String id, nama, role, npk, nim;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRiwayatMaintananceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.recycler_riwayat_maintenance_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getString("ID_USER");
            nama = bundle.getString("NAMA_USER");
            role = bundle.getString("ROLE_USER");
            npk = bundle.getString("NPK");
            nim = bundle.getString("NIM");
        }

        TextView titleTextView = binding.perbaikan;

        Bundle args = getArguments();
        if (args != null) {
            String schaduleId = args.getString("title", "Default Title");
            untid = args.getString("id");

            titleTextView.setText(schaduleId);
        }

        mRiwayatAdapter = new RiwayatAdapter(new ArrayList<>(), (position, untId, pbkId) -> {
//            Toast.makeText(getContext(), "Print PDF clicked for item unt_id: " + untId + " pbk_id: " + pbkId, Toast.LENGTH_SHORT).show();
            fetchMaintenanceReportData(untId, pbkId);
        });
        recyclerView.setAdapter(mRiwayatAdapter);

        fetchRiwayatMaintenanceData();

        ((MainActivity) getActivity()).showBackButton();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchRiwayatMaintenanceData() {
        String json = "{\"unt_id\": \"" + untid + "\"}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getRiwayatMaintanance(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");

                        Type maintenanceListType = new TypeToken<List<RiwayatMaintenance>>() {}.getType();
                        List<RiwayatMaintenance> maintenanceList = new Gson().fromJson(dataArray, maintenanceListType);

                        if (maintenanceList == null || maintenanceList.isEmpty()) {
                            Log.d(TAG, "No maintenance data found");
                            mRiwayatAdapter.updateData(new ArrayList<>());
                        } else {
                            mRiwayatAdapter.updateData(maintenanceList);
                        }

                        recyclerView.setAdapter(mRiwayatAdapter);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response body: " + e.getMessage());
                        Toast.makeText(getContext(), "Error reading response body", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to load data: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMaintenanceReportData(String untId, String pbkId) {
        mReportViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                return (T) new MaintananceReportViewModel(apiService);
            }
        }).get(MaintananceReportViewModel.class);

        mReportViewModel.fetchDataForReport(untId, pbkId, new MaintananceReportViewModel.ReportCallback() {
            @Override
            public void onSuccess(List<MaintenanceReport> result) {
                if (result != null && !result.isEmpty()) {
                    try {
                        createPdf(result.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No data available for the selected report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Failed to fetch data for report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching data for report", t);
            }
        });
    }

    private void createPdf(MaintenanceReport report) throws IOException {
        List<ActionForMaintananceReport> actions = report.getActions();

        String unitName = report.getUnt_nama();
        String Hoursmeter = report.getPbk_hours_meter();
        String BeginDate = report.getPbk_tanggal_awal();
        String BeginTime = report.getPbk_jam_awal();
        String EndDate = report.getPbk_tanggal_akhir();
        String EndTime = report.getPbk_jam_akhir();

        String technicianName = report.getNama_pelaksana();
        String asesorName = "Asesor Fulan";
        String pengawasName = "Pengawas Fulan";
        String schedulereport = report.getSch_nama();


        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Reporting_TRPAB_Try.pdf");
        FileOutputStream outputStream = new FileOutputStream(file);

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

        // Get the checkmark
        Drawable checkmarkDrawable = ContextCompat.getDrawable(getContext(), R.drawable.checklist_green);
        Bitmap checkmarkBitmap = ((BitmapDrawable) checkmarkDrawable).getBitmap();
        ByteArrayOutputStream checkmarkStream = new ByteArrayOutputStream();
        checkmarkBitmap.compress(Bitmap.CompressFormat.PNG, 100, checkmarkStream);
        byte[] checkmarkBitmapData = checkmarkStream.toByteArray();
        ImageData checkmarkImageData = ImageDataFactory.create(checkmarkBitmapData);
        Image checkmarkImage = new Image(checkmarkImageData);
        checkmarkImage.setHeight(12f);
        checkmarkImage.setWidth(12f);

        Drawable uncheckDrawable = ContextCompat.getDrawable(getContext(), R.drawable.uncheck);
        Bitmap uncheckBitmap = ((BitmapDrawable) uncheckDrawable).getBitmap();
        ByteArrayOutputStream uncheckStream = new ByteArrayOutputStream();
        uncheckBitmap.compress(Bitmap.CompressFormat.PNG, 100, uncheckStream);
        byte[] uncheckBitmapData = uncheckStream.toByteArray();
        ImageData uncheckImageData = ImageDataFactory.create(uncheckBitmapData);
        Image uncheckImage = new Image(uncheckImageData);
        uncheckImage.setHeight(12f);
        uncheckImage.setWidth(12f);

        float yPosition = 750; // Starting Y position
        float margin = 20; // Margin from the bottom of the page

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
        table.addCell(new Cell(1, 10).add(new Paragraph(new Text(schedulereport)).setFont(boldFont).setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER)));

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

        document.add(astratechImage);
        document.add(trpabImage);
        document.add(teknikParagraph);
        document.add(periodicParagraph);
        document.add(table);

        // Nama Unit
        Text namaUnitText = new Text(unitName).setFont(font).setFontSize(12);
        Paragraph namaUnitParagraph = new Paragraph(namaUnitText).setFixedPosition(211, 497, 100);

        // Lokasi
        Text lokasiText = new Text("AstraTech").setFont(font).setFontSize(12);
        Paragraph lokasiParagraph = new Paragraph(lokasiText).setFixedPosition(211, 475, 100);

        // Hours Meter
        Text hoursMeterText = new Text(Hoursmeter).setFont(font).setFontSize(12);
        Paragraph hoursMeterParagraph = new Paragraph(hoursMeterText).setFixedPosition(211, 454, 100);

        // Begin Date
        Text begindateText = new Text(BeginDate).setFont(font).setFontSize(12);
        Paragraph begindateParagraph = new Paragraph(begindateText).setFixedPosition(394, 457, 100);

        // Begin Time
        Text begintimeText = new Text(BeginTime).setFont(font).setFontSize(12);
        Paragraph begintimeParagraph = new Paragraph(begintimeText).setFixedPosition(473, 457, 100);

        // End Date
        Text enddateText = new Text(EndDate).setFont(font).setFontSize(12);
        Paragraph enddateParagraph = new Paragraph(enddateText).setFixedPosition(394, 432, 100);

        // End Time
        Text endtimeText = new Text(EndTime).setFont(font).setFontSize(12);
        Paragraph endtimeParagraph = new Paragraph(endtimeText).setFixedPosition(473, 432, 100);

        // Pelaksana
        Text pelaksanaText = new Text(technicianName).setFont(font).setFontSize(12);
        Paragraph pelaksanaParagraph = new Paragraph(pelaksanaText).setFixedPosition(625, 497, 150);

        // Pemeriksa
        Text checkerText = new Text(asesorName).setFont(font).setFontSize(12);
        Paragraph checkerParagraph = new Paragraph(checkerText).setFixedPosition(625, 475, 150);
//
        // Diketahui
        Text asesorText = new Text(pengawasName).setFont(font).setFontSize(12);
        Paragraph asesorParagraph = new Paragraph(asesorText).setFixedPosition(625, 454, 150);

        // Add paragraphs to the document
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

        // Track the index of the first element on a new page
        int actionsPerPage = 3;

        for (int i = 0; i < actions.size(); i++) {
            if (i > 0 && i % actionsPerPage == 0) {
                // Start a new page after every 3 actions
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

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
            }

            ActionForMaintananceReport action = actions.get(i);

            // Get the work drawable
            Drawable workDrawable = ContextCompat.getDrawable(getContext(), R.drawable.gambarkerja);
            Bitmap workBitmap = ((BitmapDrawable) workDrawable).getBitmap();
            ByteArrayOutputStream workStream = new ByteArrayOutputStream();
            workBitmap.compress(Bitmap.CompressFormat.PNG, 100, workStream);
            byte[] workBitmapData = workStream.toByteArray();
            ImageData workImageData = ImageDataFactory.create(workBitmapData);
            Image workImage = new Image(workImageData);
            workImage.setHeight(79.2f);
            workImage.setWidth(160.7f);

            // Action description and condition
            Text doworkText = new Text((i + 1) + ". " + action.getAct_nama()).setFont(font).setFontSize(10);
            Paragraph doworkParagraph = new Paragraph(doworkText);
            Text conditionworkText = new Text(action.getAct_keterangan()).setFont(font).setFontSize(10);
            Paragraph conditionworkParagraph = new Paragraph(conditionworkText);

            Image resultImage;
            if ("check".equals(action.getResult_check())) {
                resultImage = checkmarkImage;
            } else {
                resultImage = uncheckImage;
            }

            switch (i % actionsPerPage) {
                case 0:
                    workImage.setFixedPosition(35, 285);
                    doworkParagraph.setFixedPosition(212, 345, 120);
                    conditionworkParagraph.setFixedPosition(390, 365, 290);
                    if ("uncheck".equals(action.getResult_check())) {
                        resultImage.setFixedPosition(780, 315);
                    } else {
                        resultImage.setFixedPosition(707, 315);
                    }
                    break;
                case 1:
                    workImage.setFixedPosition(35, 158);
                    doworkParagraph.setFixedPosition(212, 220, 120);
                    conditionworkParagraph.setFixedPosition(390, 240, 290);
                    if ("uncheck".equals(action.getResult_check())) {
                        resultImage.setFixedPosition(780, 195);
                    } else {
                        resultImage.setFixedPosition(707, 195);
                    }
                    break;
                case 2:
                    workImage.setFixedPosition(35, 40);
                    doworkParagraph.setFixedPosition(212, 93, 120);
                    conditionworkParagraph.setFixedPosition(390, 113, 290);
                    if ("uncheck".equals(action.getResult_check())) {
                        resultImage.setFixedPosition(780, 75);
                    } else {
                        resultImage.setFixedPosition(707, 75);
                    }
                    break;
            }

            document.add(workImage);
            document.add(doworkParagraph);
            document.add(conditionworkParagraph);
            document.add(resultImage);
        }

        document.close();
        Toast.makeText(getContext(), "PDF successfully created", Toast.LENGTH_LONG).show();

    }
}
