package ChatApp.Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedList;


import ChatApp.Common.Message;
import ChatApp.Common.Peer;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

public class ClientGUI {

	

	private JFrame frmChatapp;
	private JPanel panel;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmExit;
	private JTextArea textArea;
	private JTextField textField;
	private String outbound;
	private JPanel peerTitle;
	private JPanel peerPanel;
	private JLabel lblNewLabel;
	private HashMap<String, Peer> peerMap;
	private HashMap<String, PrivateChatGUI> convMap;
	private JScrollPane scrollPane;
	private JPanel sendTextPanel;
	private JButton btnLoginSend;
	private JPanel userPanel;
	private JPanel textPanel;
	private JPanel peerList;
	private JLabel userName;
	private JLabel[] peerArray;
	private LinkedList<String> msgQueue; 
	private HashMap<String, String> nameToIp;
	private Peer self;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frmChatapp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
		new Thread(new ChatAppClient(this, peerMap, msgQueue)).start();
		//client.init(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		peerMap = new HashMap<String, Peer>();
		convMap =  new HashMap<String, PrivateChatGUI>();
		msgQueue = new LinkedList<String>(); 
		nameToIp = new HashMap<String, String>();
		frmChatapp = new JFrame();
		frmChatapp.setResizable(false);
		frmChatapp.setTitle("ChatApp");
		frmChatapp.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				setOutboundText("/quit");
				
			}
		});
		frmChatapp.setBounds(100, 100, 500, 400);
		frmChatapp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		panel = new JPanel();
		frmChatapp.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		textPanel = new JPanel();
		panel.add(textPanel, BorderLayout.CENTER);
		textPanel.setLayout(new BorderLayout(1, 1));
		
		userPanel = new JPanel();
		textPanel.add(userPanel, BorderLayout.NORTH);
		userPanel.setLayout(new BorderLayout(0, 0));
		
		userName = new JLabel("Logged in as:");
		userPanel.add(userName);
		
		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    }); 
		textPanel.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setBackground(new Color(204, 255, 204));
		textArea.setEditable(false);
		textArea.setForeground(Color.BLACK);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		scrollPane.setViewportView(textArea);
		
		sendTextPanel = new JPanel();
		panel.add(sendTextPanel, BorderLayout.SOUTH);
		sendTextPanel.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		textField.setText("Enter username here");
		textField.setForeground(Color.BLACK);
		sendTextPanel.add(textField, BorderLayout.CENTER);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					outbound=textField.getText();
					synchronized(msgQueue){
						msgQueue.add(outbound);
					}
					
					textField.setText(null);
					
				}
			}
		});
		textField.setColumns(1);
		
		btnLoginSend = new JButton("Login");
		btnLoginSend.setBackground(UIManager.getColor("Button.background"));
		btnLoginSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				outbound=textField.getText();
				setOutboundText(outbound);				
				textField.setText(null);
				
			}
		});
		sendTextPanel.add(btnLoginSend, BorderLayout.EAST);
		
		peerPanel = new JPanel();
		panel.add(peerPanel, BorderLayout.EAST);
		peerPanel.setLayout(new BorderLayout(0, 0));
		
		peerTitle = new JPanel();
		FlowLayout flowLayout = (FlowLayout) peerTitle.getLayout();
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setHgap(1);
		flowLayout.setVgap(1);
		peerPanel.add(peerTitle, BorderLayout.NORTH);
		
		lblNewLabel = new JLabel("****Peer List****");
		lblNewLabel.setBackground(new Color(255, 255, 204));
		lblNewLabel.setForeground(Color.BLUE);
		peerTitle.add(lblNewLabel);
		
		peerList = new JPanel();
		peerList.setBackground(UIManager.getColor("Panel.background"));
		peerPanel.add(peerList, BorderLayout.CENTER);
		peerList.setLayout(new GridLayout(10, 1, 0, 0));
		
		peerArray = new JLabel[10];
		
		for(int i=0; i<10; i++){
			peerArray[i] = new JLabel("");
			peerArray[i].addMouseListener(new PeerMouseListener(i, this));
			peerList.add(peerArray[i]);
		}

		
		menuBar = new JMenuBar();
		frmChatapp.setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				outbound = "/quit";
				setOutboundText(outbound);
			}
		});
		mnFile.add(mntmExit);
		
	}
	
	
	public void setOutboundText(String s) {
		synchronized(msgQueue){
			msgQueue.add(s);
		}
	}
	
	public void setInboundText(String s){
		textArea.append("\n"+s);
	}
	
	public void refreshPeerList(){
		for(int i=0; i<10; i++){
			peerArray[i].setText("");
		}
		peerList.updateUI();
		int j = 0;
		nameToIp.clear();
		for(Peer p: peerMap.values()){
			
			if(p.getName().length() > 15){
				
				peerArray[j++].setText(p.getName().substring(0, 15)+"...");
				System.out.println(p);
			}
			else{
				peerArray[j++].setText(p.getName());
				System.out.println(p);
				
			}
			
			nameToIp.put(p.getName(), p.getIp());
			
		}
		peerList.updateUI();

	}
	
	public void setSelf(Peer me){
		self = me;
		userName.setText("Logged in as: "+self.getName());
		userName.validate();
		btnLoginSend.setText("Send");
		btnLoginSend.validate();
	}
	
	public Peer getPeer(String name){
		return peerMap.get(name);
	}
	
	public void setPrivateMsg(Message msg){
		//if sender is self put msg on private window of receiver
		if(msg.getSender().equals(self.getIp())){
			if(convMap.containsKey(msg.getReceiver())){
				convMap.get(msg.getReceiver()).setInboundText("<"+self.getName()+"> "+ msg.getBody());
			}
		}
		//if sender is peer put msg on private window of sender
		else{
			if(convMap.containsKey(msg.getSender())){
				convMap.get(msg.getSender()).setInboundText("<"+peerMap.get(msg.getSender()).getName()+"> "+ msg.getBody());
			}
			//if new conversation open a new window
			else{
				PrivateChatGUI pcg = new PrivateChatGUI(this, peerMap.get(msg.getSender()), msgQueue);
				
				convMap.put(msg.getSender(), pcg);
				pcg.setInboundText("<"+peerMap.get(msg.getSender()).getName()+"> "+msg.getBody());
			}
			
		}
		
		
	}
	public void closePrivateGUI(String peerIp){
		convMap.remove(peerIp);
	}
	
	public void closeAllPrivateGUI(){
		for(PrivateChatGUI pcg: convMap.values()){
			pcg.close();
		}
	}
	
	public void notifyPrivateGUI(String ip){
		if(convMap.containsKey(ip)){
			PrivateChatGUI pcg = convMap.get(ip);
			pcg.disableGUI();
		}
	}
	
	public Peer getSelf(){
		return self;
	}
	public void close(){
		closeAllPrivateGUI();
		frmChatapp.setVisible(false);
		frmChatapp.dispose();
	}

	class PeerMouseListener extends MouseAdapter{
		
		private int index;
		private ClientGUI parent;
		private PrivateChatGUI pcg;
	
		private PeerMouseListener(int i, ClientGUI clientGUI){
			index = i;
			parent = clientGUI;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			//double click on peer and peer name is not empty and no on going peer-to-peer conversation
			if(e.getClickCount() == 2 && !peerArray[index].getText().isEmpty() && !convMap.containsKey(nameToIp.get(peerArray[index].getText()))){
				//open up another GUI for private conversation
				//add peer to convMap
				pcg = new PrivateChatGUI(parent, peerMap.get(nameToIp.get(peerArray[index].getText())), msgQueue);
				
				convMap.put(nameToIp.get(peerArray[index].getText()), pcg);
				//new Thread(pcg).start();
				System.out.println("private conv with "+ peerArray[index].getText());
				
			}
		}
		
	}

}

