import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import java.io.*;

public class SudokuGUI {

	private JFrame frame;
	private JTable table;
	private JButton solveButton;
	private JTextField inputProblem;
	private Solver solver;
	private String problem;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SudokuGUI window = new SudokuGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SudokuGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		solver = new Solver();
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 511, 393);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Sudoku Solver");
		frame.setLocationRelativeTo(null);
		
		table = new JTable(9,9);
		table.setEnabled(false);
		table.setBounds(20, 20, 270, 270);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
        for (int i = 0; i < 9; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(30);
            table.setRowHeight(i, 30);
            //put context in cell center
        	table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        	
        }
        
        for(int i = 0; i < 9; i++){
        	for(int j = 0; j < 9; j++){
        		getTableCellBackground(table, i, j);
        	}
        }
        
    
        
        table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		frame.getContentPane().add(table);
		
		JButton openfileButton = new JButton("open file");
		openfileButton.addActionListener(new ActionListener() {
			private BufferedReader br;

			public void actionPerformed(ActionEvent e) {
				 JFileChooser fc = new JFileChooser();

			        int returnVal = fc.showOpenDialog(frame);

			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile(); 
			            try {
							br = new BufferedReader(new FileReader(file));
							String s;
							String st = null;
							while((s = br.readLine())!=null){
					            st += s;
					        }
							Grid grid = solver.displayGrid(st);
							setGrid(grid);
							if (grid.isValid())
								problem = st;
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}   
			        }
			}
		});
		openfileButton.setBounds(348, 55, 117, 29);
		frame.getContentPane().add(openfileButton);
		
		solveButton = new JButton("Solve");
		solveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Grid grid = solver.giveSolution(problem);
				setGrid(grid);
				if(grid.isValid()){
					JOptionPane.showMessageDialog(frame, "Solved!");
				}
			}
		});
		solveButton.setBounds(348, 144, 117, 29);
		frame.getContentPane().add(solveButton);
		
		inputProblem = new JTextField();
		inputProblem.setBounds(21, 314, 178, 26);
		frame.getContentPane().add(inputProblem);
		inputProblem.setColumns(10);
		
		JButton btnOk = new JButton("ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String p = inputProblem.getText();
				Grid grid = solver.displayGrid(p);
				setGrid(grid);
				if (grid.isValid())
					problem = p;
			}
		});
		btnOk.setBounds(204, 314, 117, 29);
		frame.getContentPane().add(btnOk);
		
	//	problem = "....7..2.8.......6.1.2.5...9.54....8.........3....85.1...3.2.8.4.......9.7..6....";
	}
	
	public Component getTableCellBackground(JTable table, int row, int col) {
		TableCellRenderer renderer = table.getCellRenderer(row, col);
	    Component component = table.prepareRenderer(renderer, row, col);
	    component.setBackground(Color.LIGHT_GRAY);
		
		return component;
	}

	
	public void setGrid(Grid grid){
		if(!grid.isValid()){
			JOptionPane.showMessageDialog(frame, "Error!");
			return;
		}
		for(int i = 0; i <  Solver.square.size(); i++){
				String s = Solver.square.get(i);
				int row = i/9;
				int col = i%9;
				String value = grid.getValues().get(s);
				if(value.length() > 1){
					value = "";
				}
				table.setValueAt(value, row, col);
			}
		}


}
