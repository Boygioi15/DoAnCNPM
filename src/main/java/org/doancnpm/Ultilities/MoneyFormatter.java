package org.doancnpm.Ultilities;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;
import java.text.ParseException;

import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class MoneyFormatter {
    static DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
    static {
        decimalFormat.applyPattern("#,###");
    }

    // Tạo StringConverter để chuyển đổi giữa Number và String
    static StringConverter<Number> converter = new NumberStringConverter() {
        @Override
        public String toString(Number object) {
            if (object == null) {
                return "";
            }
            return decimalFormat.format(object);
        }

        @Override
        public Number fromString(String string) {
            try {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return decimalFormat.parse(string);
            } catch (ParseException e) {
                //ue.printStackTrace();
                return null;
            }
        }
    };

    // Tạo một pattern để chỉ cho phép các ký tự số và dấu phẩy
    static Pattern validEditingState = Pattern.compile("[\\d,]*");

    // Tạo UnaryOperator để lọc các ký tự đầu vào
    static UnaryOperator<TextFormatter.Change> filter = change -> {
        String newText = change.getControlNewText();
        if (validEditingState.matcher(newText).matches()) {
            return change;
        }
        return null;
    };
    // Tạo TextFormatter với StringConverter và UnaryOperator

    static public long getLongValueFromTextField(TextField textField) {
        TextFormatter<Number> formatter = (TextFormatter<Number>) textField.getTextFormatter();
        Number value = formatter != null ? formatter.getValue() : null;
        return value != null ? value.longValue() : 0L; // Trả về 0 nếu giá trị null
    }

    static public String convertLongToString(Long value) {
        if (value == null) {
            return "";
        }

        // Create a DecimalFormat instance with a pattern for thousand separators
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.getDefault());
        decimalFormat.applyPattern("#,###");

        // Format the long value and return the result as a string
        return decimalFormat.format(value);
    }
    static public void MoneyFormatTextField(TextField textField){
        TextFormatter<Number> textFormatter = new TextFormatter<>(converter, null, filter);
        // Áp dụng TextFormatter cho TextField
        textField.setTextFormatter(textFormatter);

        // Thêm ChangeListener để cập nhật giá trị trong thời gian thực
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d,]*")) {
                textField.setText(oldValue);
            } else {
                try {
                    String plainNumber = newValue.replace(",", "");
                    if (!plainNumber.isEmpty()) {
                        Number parsedNumber = decimalFormat.parse(plainNumber);
                        String formattedValue = decimalFormat.format(parsedNumber);

                        if (!newValue.equals(formattedValue)) {
                            // RunLater to ensure the TextField is not being updated while it is being processed
                            javafx.application.Platform.runLater(() -> textField.setText(formattedValue));
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // Phương thức để lấy giá trị số từ TextField
    public Number getNumericValueFromTextField(TextField textField) {
        TextFormatter<?> formatter = textField.getTextFormatter();
        if (formatter != null) {
            return (Number) formatter.getValue();
        }
        return null;
    }


}
