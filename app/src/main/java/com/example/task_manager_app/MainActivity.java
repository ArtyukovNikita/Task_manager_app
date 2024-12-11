package com.example.task_manager_app;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout calendarPanel;
    private TextView monthText;
    private GridLayout calendarGrid;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация компонентов
        calendarPanel = findViewById(R.id.calendar_panel);
        monthText = findViewById(R.id.month_text);
        calendarGrid = findViewById(R.id.calendar_grid);

        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton prevMonthButton = findViewById(R.id.prev_month_button);
        ImageButton nextMonthButton = findViewById(R.id.next_month_button);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        // Настройка кнопки открытия календаря
        calendarButton.setOnClickListener(v -> toggleCalendarVisibility());

        // Настройка кнопок переключения месяцев
        prevMonthButton.setOnClickListener(v -> changeMonth(-1));
        nextMonthButton.setOnClickListener(v -> changeMonth(1));

        updateCalendar();
    }

    private void toggleCalendarVisibility() {
        if (calendarPanel.getVisibility() == View.GONE) {
            calendarPanel.setVisibility(View.VISIBLE);
        } else {
            calendarPanel.setVisibility(View.GONE);
        }
    }

    private void updateCalendar() {
        monthText.setText(dateFormat.format(calendar.getTime()));

        // Очищаем старые кнопки (если они есть)
        calendarGrid.removeAllViews();

        // Определяем первый день месяца и количество дней в месяце
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Уменьшаем ширину ячейки на 10% для уменьшения ширины кнопок
        int gridCellSize = getResources().getDisplayMetrics().widthPixels / 7;
        gridCellSize = (int) (gridCellSize * 0.9); // Уменьшаем на 10%

        // Добавляем пустые кнопки для дней перед первым числом
        for (int i = 1; i < firstDayOfMonth; i++) {
            Button emptyButton = new Button(this);
            emptyButton.setVisibility(View.INVISIBLE);
            // Задаем размер пустых кнопок, чтобы они были того же размера, что и другие
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = gridCellSize;
            params.height = gridCellSize;
            emptyButton.setLayoutParams(params);
            calendarGrid.addView(emptyButton);
        }

        // Добавляем кнопки для дней месяца
        for (int i = 1; i <= daysInMonth; i++) {
            final int day = i;  // делаем переменную final
            Button dayButton = new Button(this);
            dayButton.setText(String.valueOf(i));
            dayButton.setOnClickListener(v -> onDaySelected(day));  // используем final переменную

            // Настройка размеров кнопки, чтобы она была квадратной
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = gridCellSize;
            params.height = gridCellSize;
            dayButton.setLayoutParams(params);

            // Выравнивание кнопки по центру ячейки
            dayButton.setGravity(Gravity.CENTER);

            // Добавляем отступы между кнопками
            int margin = 4; // отступ между кнопками в пикселях
            params.setMargins(1, 1, 1, 1);

            calendarGrid.addView(dayButton);
        }

        // Добавляем дополнительные строки в сетку, если необходимо
        int rows = (int) Math.ceil((double) (firstDayOfMonth + daysInMonth - 1) / 7);
        while (calendarGrid.getRowCount() < rows) {
            GridLayout.LayoutParams rowParams = new GridLayout.LayoutParams();
            rowParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            calendarGrid.setRowCount(rows);  // Устанавливаем количество строк
        }
    }



    // Метод для переключения месяца
    private void changeMonth(int direction) {
        calendar.add(Calendar.MONTH, direction); // Изменяем месяц на основе переданного направления
        updateCalendar(); // Обновляем календарь
    }

    // Метод для обработки выбора дня
    private void onDaySelected(int day) {
        String message = "Вы выбрали день: " + day + " " + dateFormat.format(calendar.getTime());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        // Закрыть календарь после выбора
        calendarPanel.setVisibility(View.GONE);
    }
}
