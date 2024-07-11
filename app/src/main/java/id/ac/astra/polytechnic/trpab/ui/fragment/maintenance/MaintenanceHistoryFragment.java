package id.ac.astra.polytechnic.trpab.ui.fragment.maintenance;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
                } catch (FileNotFoundException e) {
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

    private void createPdf() throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Reporting_TRPAB.pdf");
        OutputStream outputStream = new FileOutputStream(file);

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
        trpabImage.setHeight(50);
        trpabImage.setWidth(50);
        trpabImage.setFixedPosition(PageSize.A4.rotate().getWidth() - 60, PageSize.A4.rotate().getHeight() - 60);

        // Get the Astratech drawable
        Drawable astratechDrawable = ContextCompat.getDrawable(getContext(), R.drawable.astratech_long);
        Bitmap astratechBitmap = ((BitmapDrawable) astratechDrawable).getBitmap();
        ByteArrayOutputStream astratechStream = new ByteArrayOutputStream();
        astratechBitmap.compress(Bitmap.CompressFormat.PNG, 100, astratechStream);
        byte[] astratechBitmapData = astratechStream.toByteArray();
        ImageData astratechImageData = ImageDataFactory.create(astratechBitmapData);
        Image astratechImage = new Image(astratechImageData);
        astratechImage.setHeight(40.3f); // Height in points
        astratechImage.setWidth(177.2f); // Width in points
        astratechImage.setFixedPosition(10, PageSize.A4.rotate().getHeight() - 60); // Position at left top

        // Add images to document
        document.add(astratechImage);
        document.add(trpabImage);

        // Add some text
        Paragraph paragraph = new Paragraph("Hello Affiyan").setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(paragraph);

        document.close();

        Toast.makeText(getContext(), "PDF successfully created", Toast.LENGTH_LONG).show();
    }


}
