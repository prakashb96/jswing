package com.todo.dao;
import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import com.todo.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;





public class TodoAppDAO {

    // private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    // private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";





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
