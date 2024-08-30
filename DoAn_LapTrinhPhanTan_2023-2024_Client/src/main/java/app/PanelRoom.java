package app;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import entity.*;
import service.RoomService;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.List;

public class PanelRoom extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private static final long serialVersionUID = 1L;
	private JLabel lblTenPhong, lblLoaiPhong, lblSucChua, lblGiaPhong, lblMoTa, lblTimMaPhong, lblTinhTrang, lblMaPhong;
	private JTextField txtRoomName, txtCapacity, txtPrice, txtTimPhong, txtRoomType, txtRoomID, txtStatus;
	private JTextArea txaDescription;
	private JButton btnThemMoi, btnCapNhat, btnXoa, btnLamMoi, btnTim, btnLuu;
	private JComboBox<String> cbRoomType, cbStatus;
	private JTable table;
	private DefaultTableModel tableModel;
	private List<Room> listRoom;
	private RoomService roomService;
	private DecimalFormat formatter = new DecimalFormat("###");

	public PanelRoom() throws RemoteException, MalformedURLException, NotBoundException {
		roomService = (RoomService) Naming.lookup(URL + "roomService");
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		createUI();
//		// add event button
		fetchAllRooms();
		cbStatus.addActionListener(e -> {
			try {
				filterRoomsByStatus();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		cbRoomType.addActionListener(e -> {
			try {
				filterRoomsByType();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnLamMoi.addActionListener(e -> {
			try {
				processRefesh(cbRoomType, cbStatus, txtRoomID, txtRoomName, txtRoomType, txtPrice, txtCapacity, txtStatus, txtCapacity);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnThemMoi.addActionListener(e -> {
			try {
				processAdd(txtRoomID, txtRoomName, txtRoomType, txtCapacity, txtPrice, txaDescription, tableModel);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnXoa.addActionListener(e -> {
			try {
				processDelete();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnCapNhat.addActionListener(e -> {
			try {
				processUpdate(table, txtRoomName, txtRoomType, txtCapacity, txtPrice, txaDescription);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnTim.addActionListener(e -> {
			try {
				processSearch();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		table.addMouseListener(this);

	}

	private void createUI() throws RemoteException {
		// TODO Auto-generated method stub
		Icon img_add = new ImageIcon("src/main/java/img/add16.png");
		Icon img_del = new ImageIcon("src/main/java/img/bin.png");
		Icon img_reset = new ImageIcon("src/main/java/img/refresh16.png");
		Icon img_edit = new ImageIcon("src/main/java/img/edit.png");
		Icon img_search = new ImageIcon("src/main/java/img/search.png");
		Border line = BorderFactory.createLineBorder(Color.BLACK);

		// Thông tin phòng
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new GridLayout(4, 2, 30, 0));
		pnlInput.add(createPanel(lblMaPhong = new JLabel("Mã phòng"), txtRoomID = new JTextField()));
		pnlInput.add(createPanel(lblTenPhong = new JLabel("Tên phòng"), txtRoomName = new JTextField()));
		pnlInput.add(createPanel(lblLoaiPhong = new JLabel("Loại phòng"), txtRoomType = new JTextField()));
		pnlInput.add(createPanel(lblMoTa = new JLabel("Mô tả"), txaDescription = new JTextArea()));
		pnlInput.add(createPanel(lblGiaPhong = new JLabel("Giá phòng"), txtPrice = new JTextField()));
		pnlInput.add(createPanel(lblTinhTrang = new JLabel("Tình trạng"), txtStatus = new JTextField()));
		pnlInput.add(createPanel(lblSucChua = new JLabel("Sức chứa"), txtCapacity = new JTextField()));

		// Nút chức năng
		JPanel pnlChucNang = new JPanel();
		pnlChucNang.setLayout(new GridLayout(3, 2, 10, 10));
		pnlChucNang.add(btnThemMoi = new ButtonGradient("Thêm mới", img_add));
//		pnlChucNang.add(btnLuu = new ButtonGradient("Lưu", img_add));
		pnlChucNang.add(btnCapNhat = new ButtonGradient("Cập nhật", img_edit));
		pnlChucNang.add(btnXoa = new ButtonGradient("Xóa", img_del));
		pnlChucNang.add(btnLamMoi = new ButtonGradient("Làm mới", img_reset));

		Box bThongTinPhong = Box.createHorizontalBox();
		bThongTinPhong.add(pnlInput);
		bThongTinPhong.add(Box.createHorizontalStrut(20));
		bThongTinPhong.add(pnlChucNang);
		JPanel pnlThongTinPhong = new JPanel();
		pnlThongTinPhong.setBorder(BorderFactory.createTitledBorder("Thông tin phòng"));
		pnlThongTinPhong.add(bThongTinPhong);

		Dimension dimension = new Dimension(240, 30);
		lblMaPhong.setPreferredSize(lblLoaiPhong.getPreferredSize());
		lblTenPhong.setPreferredSize(lblLoaiPhong.getPreferredSize());
		lblSucChua.setPreferredSize(lblLoaiPhong.getPreferredSize());
		lblGiaPhong.setPreferredSize(lblLoaiPhong.getPreferredSize());
		lblTinhTrang.setPreferredSize(lblLoaiPhong.getPreferredSize());
		lblMoTa.setPreferredSize(lblLoaiPhong.getPreferredSize());
		txtRoomName.setPreferredSize(dimension);
		txtRoomID.setPreferredSize(dimension);
		txtRoomType.setPreferredSize(dimension);
		txtCapacity.setPreferredSize(dimension);
		txtPrice.setPreferredSize(dimension);
		txtStatus.setPreferredSize(dimension);
		txaDescription.setPreferredSize(dimension);
		int preferredWidth = 400;
		Dimension preferredSize = new Dimension(preferredWidth, pnlChucNang.getPreferredSize().height);
		pnlChucNang.setPreferredSize(preferredSize);

		// Tìm
		String cbtt[] = { "Tất cả", "Còn trống", "Đã đặt trước", "Đang thuê" };
		String cblp[] = { "Tất cả", "Thường", "VIP" };
		Box bLoc1, bLoc2;
		JPanel pnlLoc = new JPanel(new GridLayout(1, 2, 0, 0));
		bLoc1 = Box.createHorizontalBox();
		bLoc1.add(Box.createHorizontalStrut(20));
		bLoc1.add(lblLoaiPhong = new JLabel("Tình trạng"));
		bLoc1.add(Box.createHorizontalStrut(10));
		bLoc1.add(cbStatus = new JComboBox<>(cbtt));
		bLoc2 = Box.createHorizontalBox();
		bLoc2.add(Box.createHorizontalStrut(20));
		bLoc2.add(lblLoaiPhong = new JLabel("Loại phòng"));
		bLoc2.add(Box.createHorizontalStrut(10));
		bLoc2.add(cbRoomType = new JComboBox<>(cblp));
		pnlLoc.add(bLoc1);
		pnlLoc.add(bLoc2);
		JPanel pnlTim = new JPanel();
		pnlTim.add(Box.createVerticalStrut(25));
		pnlTim.add(lblTimMaPhong = new JLabel("Tìm theo tên phòng"));
		pnlTim.add(txtTimPhong = new JTextField(20));
		txtTimPhong.setPreferredSize(dimension);
		pnlTim.add(btnTim = new ButtonGradient("Tìm", img_search));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLoc, pnlTim);
		splitPane.setDividerLocation(600);

		JPanel pnlTable = new JPanel();
		pnlTable.setBorder(BorderFactory.createTitledBorder(line, "Danh sách phòng"));
		table = new JTable();
		String[] headers = "Mã phòng;Tên phòng;Loại phòng;Sức chứa;Giá phòng;Tình trạng;Mô tả".split(";");
		tableModel = new DefaultTableModel(headers, 0);
		table.setModel(tableModel);
		table.setRowHeight(30);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(table);
		pnlTable.setLayout(new BorderLayout());
		pnlTable.add(splitPane, BorderLayout.NORTH);
		pnlTable.add(scroll, BorderLayout.CENTER);

//		// Set editable
		txtRoomID.setEnabled(false);
		txtStatus.setEnabled(false);

		// Add vào giao diện
		setLayout(new BorderLayout());
		add(pnlThongTinPhong, BorderLayout.NORTH);
		add(pnlTable, BorderLayout.CENTER);
		pnlLoc.setBackground(Color.decode("#D0BAFB"));
		pnlTim.setBackground(Color.decode("#D0BAFB"));
		pnlTable.setBackground(Color.decode("#D0BAFB"));
		pnlInput.setBackground(Color.decode("#D0BAFB"));
		pnlChucNang.setBackground(Color.decode("#D0BAFB"));
		pnlThongTinPhong.setBackground(Color.decode("#D0BAFB"));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int row = table.getSelectedRow();
		txtRoomID.setText(table.getValueAt(row, 0).toString());
		txtRoomName.setText(table.getValueAt(row, 1).toString());
		txtRoomType.setText(table.getValueAt(row, 2).toString());
		txtCapacity.setText(table.getValueAt(row, 3).toString());
		txtPrice.setText(table.getValueAt(row, 4).toString());
		txtStatus.setText(table.getValueAt(row, 5).toString());
		txaDescription.setText(table.getValueAt(row, 6).toString());

	}
	
	public void processAdd(JTextField txtRoomID, JTextField txtRoomName,
			JTextField txtRoomType, JTextField txtCapacity, JTextField txtPrice, JTextArea txaDescription, DefaultTableModel tableModel) throws RemoteException{		    
		if (!validateInput()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin phòng!");
			return;
		}
		txtRoomID.setText((roomService.getAllRooms().size() + 1) + "");
		String roomID = txtRoomID.getText();
		String roomName = txtRoomName.getText();
		String roomType = txtRoomType.getText();
		int capacity = Integer.parseInt(txtCapacity.getText());
		double price = Double.parseDouble(txtPrice.getText());
		String description = txaDescription.getText();
		RoomType typeRoom = new RoomType(roomType, capacity, price);
		Room room = new Room(Integer.parseInt(roomID), roomName, "Còn trống", description, typeRoom);

		int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn thêm mới phòng không ?", "Chú ý!",
				JOptionPane.YES_OPTION);
		if (confirmation == JOptionPane.YES_OPTION && roomService.addRoom(room)) {
			Object[] rowData = { roomID, roomName, roomType, capacity, price, "Còn trống", description };
			tableModel.addRow(rowData);
			JOptionPane.showMessageDialog(null, "Thêm mới phòng thành công!");
		}
	}

	// Xu ly cap nhat thong tin phong
	public void processUpdate(JTable table, JTextField txtRoomName, JTextField txtRoomType, JTextField txtCapacity, JTextField txtPrice, JTextArea txaDescription) throws RemoteException{
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần cập nhật!");
			return;
		}

		if (!validateInput()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin phòng!");
			return;
		}

		int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn cập nhật phòng?", "Chú ý!",
				JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION) {
			return;
		}

		String roomName = txtRoomName.getText();
		String roomType = txtRoomType.getText();
		int capacity = Integer.parseInt(txtCapacity.getText());
		double price = Double.parseDouble(txtPrice.getText());
		String description = txaDescription.getText();

		table.setValueAt(roomName, selectedRow, 1);
		table.setValueAt(roomType, selectedRow, 2);
		table.setValueAt(capacity, selectedRow, 3);
		table.setValueAt(formatter.format(price), selectedRow, 4);
		table.setValueAt(description, selectedRow, 6);

		String currentStatus = table.getValueAt(selectedRow, 5).toString();
		RoomType roomTypeObject = new RoomType(roomType, capacity, price);
		Room room = new Room(Integer.valueOf(table.getValueAt(selectedRow, 0).toString()), roomName, currentStatus,
				description, roomTypeObject);
		roomService.updateRoom(room);

		JOptionPane.showMessageDialog(null, "Cập nhật thông tin phòng thành công!");
	}

	// Xu ly lam moi
	public void processRefesh(JComboBox<String> cbRoomType, JComboBox<String> cbStatus, JTextField txtRoomID,
			JTextField txtRoomName, JTextField txtRoomType, JTextField txtPrice,JTextField txtCapacity, JTextField txtStatus, JTextField txaDescription) throws RemoteException{
		cbRoomType.setSelectedIndex(0);
		cbStatus.setSelectedIndex(0);
		txtRoomID.setText("");
		txtRoomName.setText("");
		txtRoomType.setText("");
		txtPrice.setText("");
		txtCapacity.setText("");
		txtStatus.setText("");
		txaDescription.setText("");
	}

	// Xu ly xoa phong
	public void processDelete() throws RemoteException{
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn phòng cần xóa!");
			return;
		}

		int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phòng không?", "Chú ý!",
				JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION) {
			return;
		}

		int roomID = Integer.parseInt(table.getValueAt(row, 0).toString());
		roomService.deleteRoom(roomID);
		tableModel.removeRow(row);
		JOptionPane.showMessageDialog(null, "Xóa phòng thành công!");
	}

	public boolean validateInput() throws RemoteException{
		String roomName = txtRoomName.getText();
		String roomType = txtRoomType.getText();
		String capacity = txtCapacity.getText();
		String price = txtPrice.getText();
		if (roomName.equals("") || roomType.equals("") || capacity.equals("") || price.equals("")) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin phòng!");
			return false;
		}
		return true;
	}

	// Xu ly tim kiem phong
	public boolean processSearch() throws RemoteException{
		String roomIDToSearch = txtTimPhong.getText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 1).equals(roomIDToSearch)) {
				table.setRowSelectionInterval(i, i);
				JOptionPane.showMessageDialog(null, "Phòng được tìm thấy!");
				return true;
			}
		}
		JOptionPane.showMessageDialog(null, "Phòng không tồn tại!");
		return false;
	} 

	// Xoa toan bo loai phong
	public void clearTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);
	}

	// Xu ly combobox loc theo tinh trang
	public void filterRoomsByStatus() throws RemoteException{
		clearTable();
		String selectedStatus = cbStatus.getSelectedItem().toString();
		List<Room> listRooms = roomService.getRoomsByStatus(selectedStatus);
		listRooms.forEach(room -> {
			RoomType roomType = room.getRoomType();
			Object[] rowData = new Object[] { 
					room.getId(), 
					room.getName(), 
					roomType.getTypeRoom(),
					roomType.getCapacity(), 
					formatter.format(roomType.getPrice()), 
					room.getRoomStatus(),
					room.getDescribe() 
			};
			tableModel.addRow(rowData);
		});
	}
 
	// Xu ly combobox loc theo loai phong
	public void filterRoomsByType() throws RemoteException{
	    clearTable();
	    String selectedRoomType = cbRoomType.getSelectedItem().toString();
	    List<Room> listRooms = roomService.getRoomsByType(selectedRoomType);
	    
	    for (Room room : listRooms) {
	        RoomType roomType = room.getRoomType();
	        Object[] rowData = new Object[] { 
	            room.getId(), 
	            room.getName(), 
	            roomType.getTypeRoom(),
	            roomType.getCapacity(),  
	            formatter.format(roomType.getPrice()),    
	            room.getRoomStatus(), 				    
	            room.getDescribe() 
	        };
	        tableModel.addRow(rowData);
	    }
	}
	
	public void fetchAllRooms() throws RemoteException{
		tableModel.setRowCount(0);
		listRoom = roomService.getAllRooms();
		listRoom.forEach(r -> {
			tableModel.addRow(new Object[] { 
				    r.getId(), 
				    r.getName(), 
				    r.getRoomType().getTypeRoom(),
				    r.getRoomType().getCapacity(),  
				    r.getRoomType().getPrice(),    				    			    
				    r.getRoomStatus(), 	
				    r.getDescribe(),
				   
				});
		});
	}
	
	public JPanel createPanel(JLabel label, JComponent component) throws RemoteException{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(label);
		panel.add(component);
		label.setFont(new Font("Sanserif", Font.BOLD, 13));
		panel.setBackground(Color.decode("#D0BAFB"));
		return panel;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
