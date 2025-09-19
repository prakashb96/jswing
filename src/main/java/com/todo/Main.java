package com.todo;

import com.todo.util.DatabaseConnection;
import java.sql.Connection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.todo.gui.TodoAppGUI;
import com.todo.dao.TodoAppDAO;
import com.todo.model.Todo;

public class Main {
    public static void main(String[] args) {
        try {
            Connection cn = DatabaseConnection.getDBConnection();
            System.out.println("Connected to database successfullyyyyyyyyyyyyyyyyy");
            cn.close(); 
        } catch (Exception e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace(); 
        }
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            System.err.println("Could not set look and feel " + e.getMessage());
        }
        //event dispatch thread
        SwingUtilities.invokeLater(()->{
            try{
                TodoAppGUI app = new TodoAppGUI();
                app.initializeComponents();
                app.setVisible(true);
            }
            catch(Exception e){
                System.err.println("Error Starting teh application " + e.getMessage());
            }
           
        });
    }
}
