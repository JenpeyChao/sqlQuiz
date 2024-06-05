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
    static Statement statement;
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
    static JPanel quizPanel = new JPanel();
    static JLabel label = new JLabel("");
    static String difficulty;
    static String name;
    
    @Override
    public void paintComponent(Graphics graphics){
        //paints the question and when it hits the end of the questions it draws the leaderboard
        super.paintComponent(graphics);
        if(currquestion < answers.length){
            drawQuestion(graphics);
        }else{
            drawEndScreen(graphics);
        }
    }
    //asks for the difficulty
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
    //gets your name
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
        //draws the leaderboard
        //gets the data from the mysql database
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con1 = DriverManager.getConnection(connectURL, user,Password );
            statement = con1.createStatement();
            ResultSet result = statement.executeQuery("Select * from scores order by score desc;");
        
            graphics.setColor(Color.black);
            graphics.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 20));
            graphics.drawString("\nLEADERBOARD:",graphics.getFont().getSize(),graphics.getFont().getSize());
            int index = 1;
            //prints out the top 5 people
            while(result.next()){
                if(index <= 5) {
                    graphics.drawString("Name: " + result.getString("name") + " Score:" + result.getInt("score"), graphics.getFont().getSize(), graphics.getFont().getSize() + (20 * index));
                    index += 1;
                }
            }

        }catch(SQLException ex){

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public quiz(){
        //init
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
        main();
        createQuestions();
        timer = new Timer(100,this);
        timer.start();
       
    }
    static void createQuestions(){
        //creates the questions
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
    
    static JTextField createTextField(){
        //makes the answer textfield
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
        //when it hits enter
        //we check the answer that was given to the actual answer
        //if its right then we add one to the score
        //once we hit the end of the questions we add the score to the database
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
                    
                    if(currquestion == 2){
                        
                        try{
                            
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            con1 = DriverManager.getConnection(connectURL, user,Password );
                            insert = con1.prepareStatement("INSERT INTO scores(name,score)values(?,?)");
                            con1.setAutoCommit(false);
                            insert.setString(1, name);
                            insert.setInt(2, score);
                            
                            insert.executeUpdate();
                            con1.commit();

                            System.out.println(currquestion);
                        }catch(ClassNotFoundException|SQLException ex){
                            ex.printStackTrace();
                        }
                        

                    }
                    break;
            }
        }
    }
    
    public void main(){
    
        // Set up the frame
        
        this.setLayout(new BorderLayout());  // Use BorderLayout
        this.setSize(new Dimension(500, 300)); // Set preferred size

        // Set up the question panel
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        answer = createTextField();
        //answer.addKeyListener(new MyKeyAdapter());
        panel.add(answer);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);

        // Add components to the frame
        this.add(panel, BorderLayout.SOUTH);  // Add input panel to south

        // Make the frame visible
        
        this.setVisible(true);

        this.requestFocusInWindow();
            

    }
}
