package com.example.certicaralt4;

import android.content.Intent;
import android.content.res.AssetManager;;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.graphics.Bitmap;

import java.io.*;

public class CreateReportActivity extends AppCompatActivity {

    private static final String TAG = "CreateReportActivity";
    private Spinner spinnerSheet, spinnerCategory, spinnerColor;
    private EditText etPoints, etCorrectionFactor, etObservation;
    private Button btnSubmit, btnSaveReport;

    private Workbook workbook;
    private Sheet sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        spinnerSheet = findViewById(R.id.spinnerSheet);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerColor = findViewById(R.id.spinnerColor);
        etPoints = findViewById(R.id.etPoints);
        etCorrectionFactor = findViewById(R.id.etCorrectionFactor);
        etObservation = findViewById(R.id.etObservation);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSaveReport = findViewById(R.id.btnSaveReport);

        restoreOriginalExcelFile();

        loadSheetSpinner();
        loadColorSpinner();
        loadExcelFile();

        spinnerSheet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCategorySpinner(position);
                sheet = workbook.getSheetAt(position); // Switch to the selected sheet
                Log.d(TAG, "Switched to sheet: " + sheet.getSheetName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        btnSubmit.setOnClickListener(v -> {
            try {
                updateExcelFile();
                //saveExcelForDebugging(); Save the file to verify data

                // Clear all fields
                etPoints.setText("");
                etCorrectionFactor.setText("");
                etObservation.setText("");
                spinnerColor.setSelection(0);
                spinnerCategory.setSelection(0);

                // Show toast message
                Toast.makeText(CreateReportActivity.this, "Form submitted successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Excel Debug", "Error updating Excel file", e);
            }
        });;


        btnSaveReport.setOnClickListener(v -> {
            try {

                // Protect the Excel file
                File protectedExcelFile = protectExcelFile();

                // Share the protected file
                shareExcelFile(protectedExcelFile);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CreateReportActivity.this, "Error saving report", Toast.LENGTH_SHORT).show();
            }
        });




    }

    // Loaders
    private void loadSheetSpinner() {
        ArrayAdapter<CharSequence> sheetAdapter = ArrayAdapter.createFromResource(this,
                R.array.sheet_options, android.R.layout.simple_spinner_item);
        sheetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSheet.setAdapter(sheetAdapter);
    }

    private void loadColorSpinner() {
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.color_options, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);
    }

    private void updateCategorySpinner(int sheetIndex) {
        int categoryArrayId;
        switch (sheetIndex) {
            case 0: categoryArrayId = R.array.categories_initial; break;
            case 1: categoryArrayId = R.array.categories_plan1; break;
            case 2: categoryArrayId = R.array.categories_plan2; break;
            case 3: categoryArrayId = R.array.categories_plan3; break;
            default: categoryArrayId = R.array.categories_initial;
        }

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                categoryArrayId, android.R.layout.simple_spinner_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void loadExcelFile() {
        try {
            File file = new File(getFilesDir(), "CertiCar_Verificacion_Temporary.xlsx");

            // Check if the file already exists in internal storage
            if (!file.exists()) {
                // Copy from assets if it doesn't exist
                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("CertiCar Verificacion.xlsx");
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
                Log.d(TAG, "Excel file copied to internal storage.");
            }

            // Load the workbook from the internal storage file
            FileInputStream fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0); // Default to the first sheet
            fis.close();

            Log.d(TAG, "Excel file loaded successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load Excel file", e);
        }
    }


    // Excel Update
    /* void updateExcelFile() {
        String category = spinnerCategory.getSelectedItem().toString();
        String color = spinnerColor.getSelectedItem().toString();
        String points = etPoints.getText().toString();
        String correctionFactor = etCorrectionFactor.getText().toString();
        String observation = etObservation.getText().toString();

        int rowIndex = spinnerCategory.getSelectedItemPosition() + 4; // Adjust offset if needed
        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        // Write data to the row
        row.createCell(4).setCellValue(color);
        row.createCell(6).setCellValue(points);
        row.createCell(8).setCellValue(correctionFactor);
        row.createCell(10).setCellValue(observation);

        // Add logs to verify
        Log.d("Excel Update", "Sheet: " + sheet.getSheetName());
        Log.d("Excel Update", "Row: " + rowIndex);
        Log.d("Excel Update", "Data Written - Color: " + color + ", Points: " + points
                + ", Correction Factor: " + correctionFactor + ", Observation: " + observation);

        // Persist changes to the internal storage file
        try {
            File file = new File(getFilesDir(), "CertiCar_Verificacion_Temporary.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            Log.d(TAG, "Excel file updated and saved.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to save updated Excel file", e);
        }

        Toast.makeText(this, "Data updated for " + category, Toast.LENGTH_SHORT).show();
    }*/


    void updateExcelFile() {
        String category = spinnerCategory.getSelectedItem().toString();
        String color = spinnerColor.getSelectedItem().toString();
        String points = etPoints.getText().toString();
        String correctionFactor = etCorrectionFactor.getText().toString();
        String observation = etObservation.getText().toString();

        int rowIndex = spinnerCategory.getSelectedItemPosition() + 4; // Adjust offset if needed
        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        // Create a cell style for Arial 10 font and center alignment
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Determine the column index and background color based on the selected color
        int colorColumnIndex;
        IndexedColors cellColor;
        switch (color.toLowerCase()) {
            case "verde":
                colorColumnIndex = 3;
                cellColor = IndexedColors.LIGHT_GREEN;
                break;
            case "amarillo":
                colorColumnIndex = 4;
                cellColor = IndexedColors.LIGHT_YELLOW;
                break;
            case "rojo":
                colorColumnIndex = 5;
                cellColor = IndexedColors.RED;
                break;
            default:
                Toast.makeText(this, "Invalid color selected", Toast.LENGTH_SHORT).show();
                return;
        }

        // Create a style for the cell background color
        CellStyle colorStyle = workbook.createCellStyle();
        colorStyle.cloneStyleFrom(style);
        colorStyle.setFillForegroundColor(cellColor.getIndex());
        colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Write data to the row with the created style
        createStyledCell(row, colorColumnIndex, color, colorStyle);
        createStyledCell(row, 6, points, style);
        createStyledCell(row, 8, correctionFactor, style);
        createStyledCell(row, 10, observation, style);

        // Add logs to verify
        Log.d("Excel Update", "Sheet: " + sheet.getSheetName());
        Log.d("Excel Update", "Row: " + rowIndex);
        Log.d("Excel Update", "Data Written - Color: " + color + ", Points: " + points
                + ", Correction Factor: " + correctionFactor + ", Observation: " + observation);

        // Persist changes to the internal storage file
        try {
            File file = new File(getFilesDir(), "CertiCar_Verificacion_Temporary.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            Log.d(TAG, "Excel file updated and saved.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to save updated Excel file", e);
        }

        Toast.makeText(this, "Data updated for " + category, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to create a cell with the specified style.
     */
    private void createStyledCell(Row row, int columnIndex, String value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }










    // Generate QR Code
    private Bitmap generateQRCode(String filePath) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(filePath, BarcodeFormat.QR_CODE, 400, 400);
        return new BarcodeEncoder().createBitmap(bitMatrix);
    }


    private void shareExcelFile(File excelFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", excelFile);

        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
    }

    private File protectExcelFile() throws IOException {
        File tempFile = new File(getFilesDir(), "CertiCar_Verificacion_Temporary.xlsx");
        FileInputStream fis = new FileInputStream(tempFile);
        Workbook workbook = new XSSFWorkbook(fis);

        // Protect each sheet in the workbook
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheet.protectSheet("YourPassword123"); // Replace with your desired password
        }

        // Save the protected workbook to a new file
        File protectedFile = new File(getFilesDir(), "CertiCar_Verificacion_Protected.xlsx");
        FileOutputStream fos = new FileOutputStream(protectedFile);
        workbook.write(fos);
        fos.close();
        fis.close();

        Toast.makeText(this, "Protected Excel file saved!", Toast.LENGTH_SHORT).show();
        return protectedFile;
    }
    private void restoreOriginalExcelFile() {
        try {
            File file = new File(getFilesDir(), "CertiCar_Verificacion_Temporary.xlsx");

            // Copy the original file from assets to internal storage
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("CertiCar Verificacion.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
            Log.d(TAG, "Original Excel file restored.");
        } catch (IOException e) {
            Log.e(TAG, "Failed to restore original Excel file", e);
        }
    }

// Call this method when you need to revert the changes


}

