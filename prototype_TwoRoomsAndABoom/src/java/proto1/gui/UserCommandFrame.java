package proto1.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import proto1.game.kb.Actions;

public class UserCommandFrame /*extends JFrame implements ActionListener*/ {
	/*
	private String result = "";
	
	private JComboBox combo;
	private Object obj;
	
	public UserCommandFrame(Object obj){
		this.obj = obj;
		this.setBounds(100, 100, 400, 300);
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		String[] cmds = { Actions.OK_I_AM_DONE };
		this.combo = new JComboBox(cmds);
		panel.add(combo);
		panel.add(new JButton("DO!!!"));
		this.setContentPane(panel);
		this.setVisible(true);
	}
	
	public String getResult(){
		return this.result;
	}
	
	public static void main(String[] args){
		String str = AskCommandToUser("Hi how are you?");
		System.out.println("===> "+ str);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj instanceof JButton){
			JComboBox combo = (JComboBox)obj;
			result = combo.getSelectedItem().toString();
			obj.notify();
			this.dispose();
		}
	}
	*/
	
	public static String AskCommandToUser(String str){
		return JOptionPane.showInputDialog(str + ". Insert a command: ");
	}
	
}
