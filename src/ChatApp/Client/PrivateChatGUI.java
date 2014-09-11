package ChatApp.Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ChatApp.Common.Peer;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class PrivateChatGUI{

	private JFrame frame;
	private ClientGUI parent;
	private JTextField textField;
	private String outbound;
	private JTextArea textArea;
	private JPanel privChatPanel;
	private JPanel textButtonPanel;
	private JButton btnSend;
	private JLabel lblPrivChat;
	private Peer peer;
	private LinkedList<String> msgQueue; 
	private JScrollPane scrollPane;
	
	
	/**
	 * Create the application.
	 */
	public PrivateChatGUI(ClientGUI clientGUI, Peer p, LinkedList<String> msgQ) {
		parent = clientGUI;
		peer = p;
		msgQueue = msgQ;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("ChatApp-"+parent.getSelf().getName());
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				parent.closePrivateGUI(peer.getIp());
				close();
			}
		});
		frame.setBounds(100, 100, 400, 300);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		privChatPanel = new JPanel();
		frame.getContentPane().add(privChatPanel, BorderLayout.CENTER);
		privChatPanel.setLayout(new BorderLayout(0, 0));
		
		lblPrivChat = new JLabel("Private conversation with "+peer.getName());
		privChatPanel.add(lblPrivChat, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		privChatPanel.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		
		textArea.setBackground(new Color(204, 255, 204));
		textArea.setForeground(new Color(0, 0, 0));
		textArea.setEditable(false);
		textArea.setColumns(40);
		scrollPane.setViewportView(textArea);
		
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    }); 
		
		textButtonPanel = new JPanel();
		privChatPanel.add(textButtonPanel, BorderLayout.SOUTH);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				outbound="@"+peer.getIp()+" "+textField.getText();
				synchronized(msgQueue){
					msgQueue.add(outbound);
				}
				textField.setText(null);
			}
		});
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					
					outbound="@"+peer.getIp()+" "+textField.getText();
					synchronized(msgQueue){
						msgQueue.add(outbound);
					}
					textField.setText(null);
				}
			}
		});
		textButtonPanel.setLayout(new BorderLayout(0, 0));
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		textField.setColumns(45);
		textButtonPanel.add(textField, BorderLayout.CENTER);
		textButtonPanel.add(btnSend, BorderLayout.EAST);
		frame.setVisible(true);
	}
	
	
	public void setInboundText(String s){
		textArea.append("\n"+s);
	}
	
	public void close(){
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void disableGUI(){
		setInboundText("<Server> "+peer.getName()+" has left ChatApp");
		textField.setEnabled(false);
		btnSend.setEnabled(false);
	}
	
		
	
}
