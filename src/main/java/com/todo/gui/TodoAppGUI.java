package com.todo.gui;

import com.todo.model.Todo;
import com.todo.dao.TodoAppDAO;
import com.todo.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.*;

import java.util.List;
import java.util.*;
import java.util.Date;

import java.sql.SQLException;
import java.sql.*;

public class TodoAppGUI extends JFrame {
    private TodoAppDAO todoAppDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton, deleteButton, editButton, refreshButton;
    private JComboBox<String> categoryComboBox; // generic class

    public TodoAppGUI() {
        todoAppDAO = new TodoAppDAO();
        todoTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
        setupEventListeners();
        loadTodos();
    }

    private void initializeComponents() {
        setTitle("Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        String[] columnNames = { "ID", "Title", "Description", "Completed", "Created At", "Updated At" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inputs
        titleField = new JTextField(25);

        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Completed");

        // Buttons
        addButton = new JButton("Add todo");
        deleteButton = new JButton("Delete todo");
        editButton = new JButton("Update");
        refreshButton = new JButton("Refresh");
        
        // Filter dropdown
        String[] categoryOptions = { "All", "Completed", "Pending" };
        categoryComboBox = new JComboBox<>(categoryOptions);
    }

    private void setupComponents() {
        setLayout(new BorderLayout());

        // Input panel for title, description, completed checkbox
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title : "), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Description : "), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        // Button panel for Add, Update, Delete, Refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        

        // Filter panel for filter label and combo box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(categoryComboBox);

        // North panel to combine filter, input, and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(todoTable), BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a todo to edit or delete:"));
        add(statusPanel, BorderLayout.SOUTH);
    }
    private void clearTodo() {
        titleField.setText("");
        descriptionArea.setText("");
        completedCheckBox.setSelected(false);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addTodo());
        editButton.addActionListener(e -> updateTodo());
        deleteButton.addActionListener(e -> deleteTodo());
        refreshButton.addActionListener(e -> refreshTodo());
        categoryComboBox.addActionListener(e -> filterTodo());
        

        // ✅ Add this listener
        todoTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedTodo();
            }
        });
    }

    private void addTodo() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Todo todo = new Todo(title, description, completed);
            todoAppDAO.createtodo(todo);

            JOptionPane.showMessageDialog(this, "Todo added succesfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearTodo();
            loadTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding todo", "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTodo() {
        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to update", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Todo todo = todoAppDAO.getTodoById(id);
            if (todo != null) {
                todo.setTitle(title);
                todo.setDescription(description);
                todo.setCompleted(completed);
                todo.setUpdated_at(java.time.LocalDateTime.now());

                if (todoAppDAO.updateTodo(todo)) {
                    JOptionPane.showMessageDialog(this, "Todo updated successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearTodo();
                    loadTodos();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTodo() {

        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            todoAppDAO.deleteTodo(id);
            JOptionPane.showMessageDialog(this, "Todo deleted successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        clearTodo();
        loadTodos();
    }

    private void filterTodo() {
        if (categoryComboBox.getSelectedItem().equals("All")) {
            loadTodos();
        } else if (categoryComboBox.getSelectedItem().equals("Completed")) {
            loadCompletedTodos();
        } else {
            loadPendingTodos();
        }
    }

    private void refreshTodo() {
        clearTodo();
        categoryComboBox.setSelectedIndex(0);
        loadTodos();
    }

    private void loadCompletedTodos() {
        try {
            List<Todo> todos = todoAppDAO.getCompletedTodos();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadPendingTodos() {
        try {
            List<Todo> todos = todoAppDAO.getPendingTodos();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTodos() {
        try {
            List<Todo> todos = todoAppDAO.getAllTodos();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTable(List<Todo> todos) {
        tableModel.setRowCount(0);
        for (Todo todo : todos) {
            Object[] row = { todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(),
                    todo.getCreated_at(), todo.getUpdated_at() };
            tableModel.addRow(row);
        }
    }

    private void loadSelectedTodo() {
        int row = todoTable.getSelectedRow();
        if (row >= 0) {
            String title = tableModel.getValueAt(row, 1).toString();
            String description = tableModel.getValueAt(row, 2).toString();
            boolean completed = Boolean.parseBoolean(tableModel.getValueAt(row, 3).toString());
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);
        }
    }
}

// public class TodoAppGUI extends JFrame {
// private TodoAppDAO todoDAO;
// private JTable todoTable;
// private DefaultTableModel tableModel;
// private JTextField titleField;
// private JTextArea descriptionArea;
// private JCheckBox completedCheckBox;
// private JButton addButton;
// private JButton updateButton;
// private JButton deleteButton;
// private JButton refreshButton;

// private JComboBox<String> filterComboBox;

// public TodoAppGUI() {
// this.todoDAO = new TodoAppDAO(); //instance of DAO
// initializeComponents();
// setupLayout();
// setupEventListeners();
// loadTodos(); // loads data from db\
// loadSelectedTodo();
// }

// public void initializeComponents(){
// setTitle("Todo Application");
// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// setSize(800, 600);
// setLocationRelativeTo(null);

// String[] columnNames = {"ID", "Title", "Description", "Completed", "Created
// At", "Updated At"};
// tableModel = new DefaultTableModel(columnNames, 0){
// @Override
// public boolean isCellEditable(int row, int column) {
// return false;
// }
// };

// todoTable = new JTable (tableModel);
// todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
// todoTable.getSelectionModel().addListSelectionListener(
// (e)->{
// if(!e.getValueIsAdjusting()){
// loadSelectedTodo();
// }
// }
// );

// titleField = new JTextField(20);

// descriptionArea = new JTextArea(3, 20);
// descriptionArea.setLineWrap(true);
// descriptionArea.setWrapStyleWord(true);

// completedCheckBox = new JCheckBox("Completed");
// addButton = new JButton("Add Todo");
// updateButton = new JButton("Update Todo");
// deleteButton = new JButton("Delete Todo");
// refreshButton = new JButton("Refresh Todo");

// String[] filterOptions = {"All", "Completed", "Pending"};

// filterComboBox = new JComboBox<>(filterOptions);
// filterComboBox.addActionListener((e)->{
// String op= (String) filterComboBox.getSelectedItem();
// filterTodos();
// });
// }

// private void setupLayout(){
// setLayout(new BorderLayout());

// // I/P panel for title, description , completed

// JPanel inputPanel = new JPanel(new GridBagLayout());
// GridBagConstraints gbc = new GridBagConstraints();
// gbc.insets = new Insets(5, 5, 5, 5);
// gbc.anchor = GridBagConstraints.WEST;
// gbc.gridx=0;
// gbc.gridy=0;

// inputPanel.add(new JLabel("Title:"), gbc);

// gbc.gridx=1;
// gbc.fill = GridBagConstraints.HORIZONTAL;
// inputPanel.add(titleField, gbc);

// gbc.gridx=0;
// gbc.gridy=1;
// gbc.fill = GridBagConstraints.HORIZONTAL;
// inputPanel.add(new JLabel("Description:"), gbc);

// gbc.gridx=1;

// inputPanel.add(new JScrollPane(descriptionArea), gbc);

// gbc.gridx=1;
// gbc.gridy=2;
// inputPanel.add(completedCheckBox, gbc);

// // Button panel for Add, Update, Delete, Refresh
// JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// buttonPanel.add(addButton);
// buttonPanel.add(updateButton);
// buttonPanel.add(deleteButton);
// buttonPanel.add(refreshButton);

// // Filter panel for filter label and combo box
// JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// filterPanel.add(new JLabel("Filter:"));
// filterPanel.add(filterComboBox);

// // North panel to combine filter, input, and button panels
// JPanel northPanel = new JPanel(new BorderLayout());
// northPanel.add(filterPanel, BorderLayout.NORTH);
// northPanel.add(inputPanel, BorderLayout.CENTER);
// northPanel.add(buttonPanel, BorderLayout.SOUTH);

// add(northPanel, BorderLayout.NORTH);
// add(new JScrollPane(todoTable), BorderLayout.CENTER);

// JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
// statusPanel.add(new JLabel("Select a todo to Edit or Delete : "));
// add(statusPanel, BorderLayout.SOUTH);
// }

// private void setupEventListeners() {
// addButton.addActionListener ( (e)-> {addTodo();} );
// updateButton.addActionListener( (e)-> {updateTodo();} );
// deleteButton.addActionListener( (e)-> {deleteTodo();} );
// refreshButton.addActionListener( (e)-> {refreshTodo();} );
// completedCheckBox.addActionListener( (e)-> {} );

// }
// private void addTodo(){
// String title = titleField.getText().trim();
// String description = descriptionArea.getText().trim();
// boolean completed = completedCheckBox.isSelected();

// if (title.isEmpty() ) {
// JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Input Error",
// JOptionPane.ERROR_MESSAGE);
// return;
// }
// Todo todo = new Todo();
// todo.setTitle(title);
// todo.setDescription(description);
// todo.setCompleted(completed);

// try {
// int result = todoDAO.createTodo(todo);
// if(result > 0){
// JOptionPane.showMessageDialog(this, "Todo added successfully", "Success",
// JOptionPane.INFORMATION_MESSAGE);
// titleField.setText("");
// descriptionArea.setText("");
// completedCheckBox.setSelected(false);
// loadTodos();
// } else {
// JOptionPane.showMessageDialog(this, "Failed to add todo", "Error",
// JOptionPane.ERROR_MESSAGE);
// }
// } catch(Exception e){
// JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(),
// "Database Error", JOptionPane.ERROR_MESSAGE);
// e.printStackTrace();
// }
// }
// private void updateTodo() {
// tableModel.getRowCount();
// int row = todoTable.getSelectedRow();
// if(row==-1){
// JOptionPane.showMessageDialog(this, "Please select a todo to update.", "No
// Selection", JOptionPane.WARNING_MESSAGE);
// return;
// }

// int id = (int) tableModel.getValueAt(row, 0);
// String title = titleField.getText().trim();
// String description = descriptionArea.getText().trim();
// boolean completed = completedCheckBox.isSelected();

// if(title.isEmpty()){
// JOptionPane.showMessageDialog(this, "Title cannot be empty!", "Error",
// JOptionPane.WARNING_MESSAGE);
// return;
// }
// try{
// Todo todo = todoDAO.getTodoById(id);
// if (todo != null) {
// todo.setTitle(title);
// todo.setDescription(description);
// todo.setCompleted(completed);
// todo.setUpdated_at(java.time.LocalDateTime.now());
// boolean result = todoDAO.updateTodo(todo);

// if (result) {
// JOptionPane.showMessageDialog(this, "Todo updated successfully", "Success",
// JOptionPane.INFORMATION_MESSAGE);
// loadTodos();
// } else {
// JOptionPane.showMessageDialog(this, "Update failed", "Error",
// JOptionPane.ERROR_MESSAGE);
// }
// }
// }
// catch (SQLException e) {
// JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
// "Error", JOptionPane.ERROR_MESSAGE);
// }

// }

// private void deleteTodo() {
// int row = todoTable.getSelectedRow();
// if(row==-1){
// JOptionPane.showMessageDialog(this, "Please select a todo to delete.", "No
// Selection", JOptionPane.WARNING_MESSAGE);
// return;
// }

// int id = (int) todoTable.getValueAt(row, 0);

// try{
// boolean success = todoDAO.deleteTodo(id);
// if(success){
// JOptionPane.showMessageDialog(this, "Todo deleted successfully.", "Success",
// JOptionPane.INFORMATION_MESSAGE);
// loadTodos();
// } else {
// JOptionPane.showMessageDialog(this, "Todo not found or could not be
// deleted.", "Error", JOptionPane.ERROR_MESSAGE);
// }
// } catch(SQLException e){
// JOptionPane.showMessageDialog(this, "Error deleting todo: " + e.getMessage(),
// "Database Error", JOptionPane.ERROR_MESSAGE);
// e.printStackTrace();
// }

// }

// private void refreshTodo() {
// loadTodos();
// }

// private void loadTodos() {
// try {
// List<Todo> todos = todoDAO.getAllTodos(); // GUI requests data from DAO
// updateTable(todos);
// }
// catch(SQLException e){
// JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
// "Database Error", JOptionPane.ERROR_MESSAGE);
// e.printStackTrace();
// }
// }

// private void loadSelectedTodo() {
// int r = todoTable.getSelectedRow();
// if(r >=0){
// String title = (String) tableModel.getValueAt(r, 1);
// String description = (String) tableModel.getValueAt(r, 2);
// boolean completed = (boolean) tableModel.getValueAt(r, 3);
// titleField.setText(title);
// descriptionArea.setText(description);
// completedCheckBox.setSelected(completed);

// }
// }

// private void filterTodos() {
// String option = (String) filterComboBox.getSelectedItem();
// if("All".equals(option)) {
// loadTodos();
// }
// else if("Completed".equals(option)) {
// //filterTodosByCompletion(true);

// }
// else if("Pending".equals(option)) {
// //filterTodosByCompletion(false);
// }
// }

// private void updateTable(List<Todo> todos) {
// tableModel.setRowCount(0); // clears existing table datas
// for (Todo t : todos) {
// Object[] row = {
// t.getId(),
// t.getTitle(),
// t.getDescription(),
// t.isCompleted(),
// t.getCreated_at(),
// t.getUpdated_at()
// };
// tableModel.addRow(row);
// }

// }
// }
