import javax.swing.JFrame;

public class test extends JFrame{
    
    public test(){
        quiz panel = new quiz();
        this.add(panel);
		this.setTitle("SQL quiz");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        new test();
    }
}
