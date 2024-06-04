import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import javax.swing.*;

public class quiz extends JPanel implements ActionListener{
    Timer timer;
    static Connection con1;
    static PreparedStatement insert;
    static String connectURL = "jdbc:mysql://127.0.0.1:3306/sqlquiz";
    static String user = "root";
    static String Password = "maplestory";
    static String[] questions = new String[2];
    static String[] answers = new String[2];
    static int currquestion = 0;
    static JTextField answer;
    static int score = 0;
    static JFrame questionFrame  = new JFrame();
    static JPanel panel = new JPanel();
    static JFrame scoreBoard = new JFrame();
    static JLabel label = new JLabel("");
    static String difficulty;
    static String name;
    
    @Override
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        if(currquestion < answers.length){
            drawQuestion(graphics);
        }else{
            drawEndScreen(graphics);
        }
    }
    private String selectDifficulty() {
        String[] options = {"easy", "medium", "hard"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select Difficulty: ",
                "Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
		// Return String of difficulty chosen
        if (choice >= 0 && choice < options.length) {
            return options[choice];
        } else {
            return "medium"; // Default to MEDIUM if somehow no option is selected
        }
    }
    private String nameString() {
        JTextPane textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(100, 50));
        
        int choice = JOptionPane.showOptionDialog(
                null,
                scrollPane,
                "Whats your name?",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
        
        if (choice == JOptionPane.OK_OPTION) {
            return textPane.getText();
        } else {
            return "Unknown"; // Default to empty string if the dialog is cancelled
        }
    }
    public static void drawEndScreen(Graphics graphics){
        // try{
        //     ResultSet allBoardEntries = sql.executeQuery("Select * from scores order by score desc;");
        
        //     graphics.setColor(Color.black);
        //     graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 20));
        //     System.out.println("\nLEADERBOARD:");
        //     while(allBoardEntries.next()) {
        //         String name = allBoardEntries.getString("name");
        //         int score = allBoardEntries.getInt("score");
        //         System.out.printf("%-15s%-5d\n", name, score);
        //     }

        // }catch(SQLException ex){

        // }
        
    }
    public quiz(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con1 = DriverManager.getConnection(connectURL, user,Password );
            
        }catch(ClassNotFoundException|SQLException ex){}
        name = nameString();
        System.out.println(con1);
        this.selectDifficulty();

        this.setPreferredSize(new Dimension(500,500));
        this.setBackground(Color.gray);
        this.setFocusable(true);
        
        createQuestions();
        timer = new Timer(100,this);
        timer.start();
    }
    static void createQuestions(){
        questions[0] = "Select all columns from the employees table.";
        answers[0] = "select * from employees;";

        questions[1] = "Select the first_name and last_name columns from the employees table.";
        answers[1] = "select first_name, last_name from empolyees;";
    }
    static void drawQuestion(Graphics graphics){
        graphics.setColor(Color.black);
        graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 20));
        graphics.drawString(name, graphics.getFont().getSize(), graphics.getFont().getSize());
        graphics.drawString("Score:"+score, graphics.getFont().getSize(), graphics.getFont().getSize()+20);
        graphics.drawString(questions[currquestion], graphics.getFont().getSize(), graphics.getFont().getSize()+40);
        
    }   
    static void updateSQL(){

    }
    static JTextField createTextField(){
        JTextField textField = new JTextField(10);
        textField.addKeyListener(new MyKeyAdapter());
        return textField;
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

        repaint();
    }
    private static class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            //Handle key pressed events for hte cursor
            switch (e.getKeyCode()) {

                case KeyEvent.VK_ENTER:
                    String response = answer.getText();
                    System.out.println(response);
                    answer.setText("");
                    answer.requestFocusInWindow();
                    
                    if (response.equals(answers[currquestion])){
                        score+=1;
                            
                    }
                    currquestion+=1;
                    System.out.println(currquestion);
                    if(currquestion >= answers.length){
                        
                        try{
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            con1 = DriverManager.getConnection(connectURL, user,Password );
                            insert = con1.prepareStatement("INSERT INTO scores(studentName,mobile,course)values('"+1+"','"+name+"','"+score+"')");
                            insert.execute();

                        }catch(ClassNotFoundException|SQLException ex){

                        }
                        

                    }
                    
                    break;
            }
            //test
            // System.out.println(currCol+currRow);
        }
    }
    public static void main(String[] args) {
        
        quiz quizPanel = new quiz();
    
        // Set up the frame
        questionFrame.setTitle("SQL questions");
        questionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        questionFrame.setResizable(false);
        questionFrame.setLayout(new BorderLayout());  // Use BorderLayout
        questionFrame.setSize(new Dimension(500, 300)); // Set preferred size

        // Set up the question panel
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        answer = createTextField();
        //answer.addKeyListener(new MyKeyAdapter());
        panel.add(answer);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        // Add components to the frame
        questionFrame.add(quizPanel, BorderLayout.CENTER);  // Add quiz panel to center
        questionFrame.add(panel, BorderLayout.SOUTH);  // Add input panel to south

        // Make the frame visible
        questionFrame.setLocationRelativeTo(null);
        questionFrame.setVisible(true);

        answer.requestFocusInWindow();
            

    }
}
