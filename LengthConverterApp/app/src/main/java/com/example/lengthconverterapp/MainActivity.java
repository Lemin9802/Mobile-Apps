package com.example.lengthconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Array of units for conversion
    private final String[] units = {"Millimetre", "Metre", "Foot", "Mile"};

    // Conversion rates between units (with respect to millimetres)
    private final Map<String, Double> conversionRates = new HashMap<String, Double>() {{
        put("Millimetre", 1.0);
        put("Metre", 1000.0);
        put("Foot", 304.8);
        put("Mile", 1_609_344.0);
    }};

    // List to store conversion history, limited to the 6 most recent entries
    private List<String> historyList = new ArrayList<>();
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to views
        EditText inputValue = findViewById(R.id.inputValue);
        Spinner inputUnitSpinner = findViewById(R.id.inputUnitSpinner);
        Spinner outputUnitSpinner = findViewById(R.id.outputUnitSpinner);
        Button convertButton = findViewById(R.id.convertButton);
        TextView resultTextView = findViewById(R.id.resultTextView);
        RecyclerView historyRecyclerView = findViewById(R.id.historyRecyclerView);

        // Setting up RecyclerView for conversion history
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(historyList);
        historyRecyclerView.setAdapter(historyAdapter);

        // Set up ArrayAdapter for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        inputUnitSpinner.setAdapter(adapter);
        outputUnitSpinner.setAdapter(adapter);

        // Set up click listener for conversion button
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input value
                String input = inputValue.getText().toString();

                // Check if input is empty
                if (input.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse the input value
                    double value = Double.parseDouble(input);
                    String inputUnit = inputUnitSpinner.getSelectedItem().toString();
                    String outputUnit = outputUnitSpinner.getSelectedItem().toString();

                    // Convert the value directly
                    double result = convertUnits(value, inputUnit, outputUnit);

                    // Format the result based on specific unit conversion cases
                    String formattedResult = formatResult(result, inputUnit, outputUnit);

                    // Display the result
                    resultTextView.setText("Result: " + formattedResult + " " + outputUnit);

                    // Add to conversion history at the top of the list
                    String historyEntry = value + " " + inputUnit + " = " + formattedResult + " " + outputUnit;
                    historyList.add(0, historyEntry);
                    historyAdapter.notifyDataSetChanged(); // Update RecyclerView

                } catch (NumberFormatException e) {
                    // Show error if input is not valid
                    Toast.makeText(MainActivity.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Function to convert between units
    private double convertUnits(double value, String fromUnit, String toUnit) {
        // Get the conversion rate of the input unit to millimetres
        double fromRate = conversionRates.get(fromUnit);
        // Get the conversion rate of the output unit to millimetres
        double toRate = conversionRates.get(toUnit);
        // Convert value from the input unit to millimetres, then to the output unit
        return (value * fromRate) / toRate;
    }

    // Function to format the result based on conversion requirements
    private String formatResult(double result, String fromUnit, String toUnit) {
        // Check specific cases and set the number of decimal places accordingly
        if (fromUnit.equals("Millimetre") && toUnit.equals("Metre")) {
            return String.format("%.3f", result); // 3 decimal places
        } else if (fromUnit.equals("Millimetre") && toUnit.equals("Foot")) {
            return String.format("%.19f", result); // 19 decimal places
        } else if (fromUnit.equals("Millimetre") && toUnit.equals("Mile")) {
            return String.format("%.21f", result); // 21 decimal places
        } else if (fromUnit.equals("Foot") && toUnit.equals("Millimetre")) {
            return String.format("%.1f", result); // 1 decimal place
        } else {
            return String.format("%.0f", result);
        }
    }
}
