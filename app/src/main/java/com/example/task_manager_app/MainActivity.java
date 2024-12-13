package com.example.task_manager_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout calendarPanel;
    private TextView monthText;
    private GridLayout calendarGrid;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private TextView selectedDateText;
    private DatabaseHelper dbHelper;
    private ImageButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        selectedDateText = findViewById(R.id.selected_date_text);
        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DatabaseHelper(this);
        btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(v -> showAddTaskDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация компонентов
        calendarPanel = findViewById(R.id.calendar_panel);
        monthText = findViewById(R.id.month_text);
        calendarGrid = findViewById(R.id.calendar_grid);
        selectedDateText = findViewById(R.id.selected_date_text);

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

    private void showAddTaskDialog() {
        // Создание диалога для ввода данных
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final EditText taskName = dialogView.findViewById(R.id.task_name);
        final EditText taskDescription = dialogView.findViewById(R.id.task_description);
        final EditText taskDate = dialogView.findViewById(R.id.task_date);
        final EditText taskTime = dialogView.findViewById(R.id.task_time);
        final Spinner prioritySpinner = dialogView.findViewById(R.id.priority_spinner);
        final LinearLayout subtasksLayout = dialogView.findViewById(R.id.subtasks_layout);
        final Button addSubtaskBtn = dialogView.findViewById(R.id.add_subtask_button);
        final Button cancelBtn = dialogView.findViewById(R.id.cancel_button);
        final Button doneBtn = dialogView.findViewById(R.id.done_button);

        // Настройка Spinner для приоритета
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        // Календарь для выбора даты
        taskDate.setOnClickListener(v -> showDatePickerDialog(taskDate));

        // Время для выбора времени
        taskTime.setOnClickListener(v -> showTimePickerDialog(taskTime));

        // Кнопка для добавления подзадачи
        addSubtaskBtn.setOnClickListener(v -> addSubtaskRow(subtasksLayout));

        // Настройка кнопки "Готово"
        doneBtn.setOnClickListener(v -> {
            String taskNameStr = taskName.getText().toString();
            String taskDescriptionStr = taskDescription.getText().toString();
            String taskDateStr = taskDate.getText().toString();
            String taskTimeStr = taskTime.getText().toString();
            String taskPriority = prioritySpinner.getSelectedItem().toString();

            // Добавить задачу в базу данных
            if (validateFields(taskNameStr, taskDateStr, taskTimeStr)) {
                long taskId = dbHelper.addTask(taskNameStr, "Not Started", taskDateStr, taskTimeStr, taskDescriptionStr, taskPriority);
                // Добавить подзадачи, если есть
                addSubtasks(taskId, subtasksLayout);
                Toast.makeText(MainActivity.this, "Task added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });


        //кнопка "отмена"
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Показываем диалог
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()
                .show();
    }

    private boolean validateFields(String taskName, String taskDate, String taskTime) {
        return !taskName.isEmpty() && !taskDate.isEmpty() && !taskTime.isEmpty();
    }

    private void showDatePickerDialog(final EditText taskDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> taskDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText taskTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> taskTime.setText(hourOfDay + ":" + minute),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void addSubtaskRow(LinearLayout subtasksLayout) {
        // Создание нового поля подзадачи
        View subtaskView = getLayoutInflater().inflate(R.layout.subtask_row, null);
        EditText subtaskName = subtaskView.findViewById(R.id.subtask_name);
        CheckBox subtaskCompleted = subtaskView.findViewById(R.id.subtask_checkbox);

        // Добавление строки подзадачи в layout
        subtasksLayout.addView(subtaskView);
    }

    private void addSubtasks(long taskId, LinearLayout subtasksLayout) {
        for (int i = 0; i < subtasksLayout.getChildCount(); i++) {
            View subtaskView = subtasksLayout.getChildAt(i);
            EditText subtaskName = subtaskView.findViewById(R.id.subtask_name);
            if (!subtaskName.getText().toString().isEmpty()) {
                dbHelper.addSubtask((int) taskId, subtaskName.getText().toString(), "Not Started");
            }
        }
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
        String selectedDate = String.format(Locale.getDefault(), "%d %s", day, dateFormat.format(calendar.getTime()));
        selectedDateText.setText("Selected Date: " + selectedDate);  // Обновляем текст

        // Показываем Toast с выбранной датой
        String message = "You selected: " + selectedDate;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Закрыть календарь после выбора
        calendarPanel.setVisibility(View.GONE);
    }

}
