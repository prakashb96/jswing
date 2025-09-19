package com.todo.dao;
import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import com.todo.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;





public class TodoAppDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";


    public  int createTodo(Todo todo) throws SQLException {
        
        DatabaseConnection db = new DatabaseConnection();
        int rowsAffected = 0;
        try (
            Connection conn = db.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_TODO,Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setObject(4, todo.getCreated_at());
            stmt.setObject(5, todo.getUpdated_at());
            rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0){
                throw new SQLException("Failed to get generated key");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if(generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
                else{
                    throw new SQLException("Failed to get generated key");
                }
            }
        
        }
        // return rowsAffected;
    }
    


    private Todo getTodoRow(ResultSet r) throws SQLException {
        int id = r.getInt("id");
        String title = r.getString("title");
        String description = r.getString("description");
        boolean completed = r.getBoolean("completed");
        LocalDateTime createdAt = r.getTimestamp("created_at").toLocalDateTime(); 
        LocalDateTime updatedAt = r.getTimestamp("updated_at").toLocalDateTime(); 
        Todo todo = new Todo(id, title, description, createdAt, completed, updatedAt); // model object created
        return todo;

    }





    public List<Todo> getAllTodos() throws SQLException{

        List<Todo> todos = new ArrayList<>();
        DatabaseConnection db = new DatabaseConnection(); // DB connection  and Query execution

        try(
            Connection conn = db.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos ORDER BY created_at DESC");
            ResultSet rs = stmt.executeQuery();

        ){
            while (rs.next()) {
                // Todo todo = new Todo();
                // todo.setId(rs.getInt("ID"));
                // todo.setTitle(rs.getString("Title"));
                // todo.setDescription(rs.getString("Description"));
                // todo.setCompleted(rs.getBoolean("Completed"));
                // LocalDateTime createdAt = rs.getTimestamp("Created_at").toLocalDateTime();
                // LocalDateTime updatedAt = rs.getTimestamp("Updated_at").toLocalDateTime();
                // todo.setCreated_at(createdAt);
                // todo.setUpdated_at(updatedAt);
                // todos.add(todo);

          
                todos.add(getTodoRow(rs));

    
            }
           
        }
         return todos;
        
    }
   
}
