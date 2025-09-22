package com.todo.dao;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import com.todo.util.*;


// import java.security.Timestamp;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;





public class TodoAppDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY id ASC";
    private static final String SELECT_COMPLETED_TODOS = "SELECT * FROM todos WHERE COMPLETED = true";
    private static final String SELECT_PENDING_TODOS = "SELECT * FROM todos WHERE COMPLETED = false";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";
    private static final String UPDATE_TODO = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_TODO = "DELETE FROM todos WHERE id = ?";


    // Create a New Todo
    public int createtodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TODO, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString (1, todo.getTitle());
            stmt.setString (2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
            stmt.setTimestamp(5, todo.getUpdated_at() != null ? Timestamp.valueOf(todo.getUpdated_at()) : null);

            int rows = stmt.executeUpdate();

            if (rows == 0) throw new SQLException("Creating todo failed, no rows affected.");

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }
    
    public boolean updateTodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO)
        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getUpdated_at()));
            stmt.setInt(5, todo.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTodo(int id) throws SQLException{
        try(
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_TODO)
        ){
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Todo getTodoRow(ResultSet res) throws SQLException {
        return new Todo(
                res.getInt("id"),
                res.getString("title"),
                res.getString("description"),
                res.getBoolean("completed"),
                res.getTimestamp("created_at").toLocalDateTime(),
                res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null
        );
    }

    public List<Todo> getAllTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
                ResultSet res = stmt.executeQuery()
        ) {
            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }
    public List<Todo> getCompletedTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_COMPLETED_TODOS);
                ResultSet res = stmt.executeQuery()
        ) {
            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }
    public List<Todo> getPendingTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_PENDING_TODOS);
                ResultSet res = stmt.executeQuery()
        ) {
            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }
    public Todo getTodoById(int id) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID)
        ) {
            stmt.setInt(1, id);
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    return getTodoRow(res);
                }
            }
        }
        return null;
    }
}









// public class TodoAppDAO {

//     private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
//     private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
//     private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";
//     private static final String UPDATE_TODO = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";
//     private static final String DELETE_TODO = "DELETE FROM todos WHERE id = ?";
    
//     public static String getSelectAllTodos() {
//         return SELECT_ALL_TODOS;
//     }

//     public static String getInsertTodo() {
//         return INSERT_TODO;
//     }
//     // Create a new todo 

//     public  int createTodo(Todo todo) throws SQLException {
        
//         DatabaseConnection db = new DatabaseConnection();
        
//         try (
//             Connection conn = db.getDBConnection();
//             PreparedStatement stmt = conn.prepareStatement(INSERT_TODO,Statement.RETURN_GENERATED_KEYS);
//         ) {
//             stmt.setString(1, todo.getTitle());
//             stmt.setString(2, todo.getDescription());
//             stmt.setBoolean(3, todo.isCompleted());
//             stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
//             stmt.setTimestamp(5, Timestamp.valueOf(todo.getUpdated_at()));
//             int rowsAffected = stmt.executeUpdate();
//             if(rowsAffected == 0){
//                 throw new SQLException("Failed to get generated key");
//             }
//             try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
//                 if(generatedKeys.next()){
//                     return generatedKeys.getInt(1);
//                 }
                
//             }
           
        
//         }
//         return -1;
//     }

//     public boolean updateTodo(Todo todo) throws SQLException {
//         DatabaseConnection db = new DatabaseConnection();
        
//         try (
//             Connection conn = db.getDBConnection();
//             PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO);
//         ) {
//             stmt.setString(1, todo.getTitle());
//             stmt.setString(2, todo.getDescription());
//             stmt.setBoolean(3, todo.isCompleted());
//             stmt.setTimestamp(4,Timestamp.valueOf(todo.getUpdated_at()));
//             stmt.setInt(5, todo.getId());
//             int rowsAffected = stmt.executeUpdate();
//             return rowsAffected > 0;
//         }
        
//     }
    
//     public boolean deleteTodo(int id) throws SQLException {
//             DatabaseConnection db = new DatabaseConnection();
            
//             try (
//                 Connection conn = db.getDBConnection();
//                 PreparedStatement stmt = conn.prepareStatement(DELETE_TODO);
//             ) {
//                 stmt.setInt(1, id);
//                 int rowsAffected = stmt.executeUpdate();
//                 return rowsAffected > 0;
//             }
            
//         }

//     private Todo getTodoRow(ResultSet r) throws SQLException {
//         int id = r.getInt("id");
//         String title = r.getString("title");
//         String description = r.getString("description");
//         boolean completed = r.getBoolean("completed");
//         LocalDateTime createdAt = r.getTimestamp("created_at") != null ? r.getTimestamp("created_at").toLocalDateTime() : null;
//         LocalDateTime updatedAt = r.getTimestamp("updated_at") != null ? r.getTimestamp("updated_at").toLocalDateTime() : null;
//         Todo todo = new Todo(id, title, description, createdAt, completed, updatedAt); // model object created
//         return todo;

//     }


   


//     public List<Todo> getAllTodos() throws SQLException{

//         List<Todo> todos = new ArrayList<>();
//         DatabaseConnection db = new DatabaseConnection(); // DB connection  and Query execution

//         try(
//             Connection conn = db.getDBConnection();
//             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
//             ResultSet rs = stmt.executeQuery();

//         ){
//             while (rs.next()) {
//                 // Todo todo = new Todo();
//                 // todo.setId(rs.getInt("ID"));
//                 // todo.setTitle(rs.getString("Title"));
//                 // todo.setDescription(rs.getString("Description"));
//                 // todo.setCompleted(rs.getBoolean("Completed"));
//                 // LocalDateTime createdAt = rs.getTimestamp("Created_at").toLocalDateTime();
//                 // LocalDateTime updatedAt = rs.getTimestamp("Updated_at").toLocalDateTime();
//                 // todo.setCreated_at(createdAt);
//                 // todo.setUpdated_at(updatedAt);
//                 // todos.add(todo);

          
//                 todos.add(getTodoRow(rs));

    
//             }
           
//         }
//         catch(SQLException e){
//             e.printStackTrace();
//         }
//          return todos;
        
//     }


//      public Todo getTodoById(int id) throws SQLException {
//         DatabaseConnection db = new DatabaseConnection();
//         try (
//                 Connection conn = db.getDBConnection();
//                 PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID)
//         ) {
//             stmt.setInt(1, id);
//             try(ResultSet res = stmt.executeQuery()){
//             if (res.next()) {
                
//                     return getTodoRow(res);
                
//             }
//         }
        
//     }
//     return null;
   
// }
// }
