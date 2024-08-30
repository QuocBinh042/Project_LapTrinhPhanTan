package app;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.regex.Pattern;
import java.util.List;
import entity.Customer;
import service.CustomerService;

public class PanelCustomer extends JPanel implements MouseListener{
	/**
	 * 
	 */

	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private static final long serialVersionUID = 1L;
	private JLabel lblTenKhachHang, lblSoDienThoai, lblGhiChu, lblMaKH;
	private JTextField txtCustomerName, txtPhoneNumber, txtEmail, txtTimSDT, txtTimTenKH, txtMaKH;
	private JTextArea txaNote;
	private JButton btnThemMoi, btnCapNhat, btnLamMoi, btnTim, btnLamMoiKH, btnLuu;
	private String[] headers = { "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Ghi chú" };
	private JTable table;
	private DefaultTableModel tableModel;
	private CustomerService customerService;
	private List<Customer> listCustomer;

	public PanelCustomer() throws RemoteException, MalformedURLException, NotBoundException {
		customerService = (CustomerService) Naming.lookup(URL + "customerService");
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
		getAllCustomers(table, tableModel);
		// sự kiện
		btnLamMoi.addActionListener(e -> refesh(txtMaKH, txtCustomerName, txtPhoneNumber, txaNote));
		btnThemMoi.addActionListener(e -> {
			try {
				processAddCustomer(txtCustomerName, txtPhoneNumber, txtCustomerName, tableModel);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnCapNhat.addActionListener(e -> {
			try {
				processUpdate(table, txtCustomerName, txtPhoneNumber, txaNote);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnTim.addActionListener(e -> processSearch(txtTimSDT, table));
		table.addMouseListener(this);

	}
	
	public JPanel createPanel(JLabel label, JComponent component) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(label);
		panel.add(component);
		label.setFont(new Font("Sanserif", Font.BOLD, 13));
		panel.setBackground(Color.decode("#D0BAFB"));
		return panel;
	}
	
	public void processAddCustomer(JTextField txtCustomerName, JTextField txtPhoneNumber, JTextField txaNote, DefaultTableModel tableModel) throws RemoteException {
		if (!validateInput(txtCustomerName, txtPhoneNumber)) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin khách hàng!");
			return;
		}

		String customerID = Integer.toString(customerService.getAllCustomers().size() + 1);
		String customerName = txtCustomerName.getText();
		String phoneNumber = txtPhoneNumber.getText();
		String note = txaNote.getText();

		Customer customer = new Customer(Integer.parseInt(customerID), customerName, phoneNumber, note);

		int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn thêm mới khách hàng không ?",
				"Chú ý!", JOptionPane.YES_OPTION);
		if (confirmation == JOptionPane.YES_OPTION && customerService.addCustomer(customer)) {
			Object[] rowData = { customerID, customerName, phoneNumber, note };
			tableModel.addRow(rowData);
			JOptionPane.showMessageDialog(null, "Thêm mới khách hàng thành công!");
		} else {
			JOptionPane.showMessageDialog(null, "Thêm mới khách hàng không thành công!");
		}
	}	

	// Xoa toan bo khach hang
	public void clearTable(JTable table) {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);
	}

	// Lay toan bo khach hang
	public void getAllCustomers(JTable table, DefaultTableModel tableModel) throws RemoteException {
		clearTable(table);
		listCustomer = customerService.getAllCustomers();
		listCustomer.forEach(c -> {
			tableModel.addRow(new Object[] { 
					c.getId(), 
					c.getCustomerName(), 
					c.getPhoneNumber(), 
					c.getNote() 
				});

		});
	}

	// Kiem tra rang buoc
	public boolean validateInput(JTextField txtCustomerName, JTextField txtPhoneNumber) throws RemoteException{
		String tenKH = txtCustomerName.getText();
		String sdt = txtPhoneNumber.getText();
		Pattern p = Pattern.compile("[a-zA-Z]+");
		if (!(p.matcher(tenKH).find())) {
			JOptionPane.showMessageDialog(null, "Tên khách hàng không hợp lệ!");
			return false;
		}

		Pattern p1 = Pattern.compile("[0-9]{10}");
		if (!(p1.matcher(sdt).find())) {
			JOptionPane.showMessageDialog(null, "Số điện thoại chỉ được nhập chữ số!");
			return false;
		}

		return true;
	}

	public void processUpdate(JTable table, JTextField txtCustomerName, JTextField txtPhoneNumber, JTextArea txaNote) throws RemoteException {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng cần cập nhật!");
			return;
		}

		if (!validateInput(txtCustomerName, txtPhoneNumber)) {
			JOptionPane.showMessageDialog(null, "Vui lòng kiểm tra lại thông tin đã nhập!");
			return;
		}

		int confirmation = JOptionPane.showConfirmDialog(null, "Có chắc chắn muốn cập nhật thông tin khách hàng không?",
				"Chú ý", JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION) {
			return;
		}

		String customerName = txtCustomerName.getText();
		String phoneNumber = txtPhoneNumber.getText();
		String note = txaNote.getText();

		table.setValueAt(customerName, selectedRow, 1);
		table.setValueAt(phoneNumber, selectedRow, 2);
		table.setValueAt(note, selectedRow, 3);

		String customerId = table.getValueAt(selectedRow, 0).toString();
		Customer customer = new Customer(Integer.parseInt(customerId), customerName, phoneNumber, note);
		customerService.updateCustomer(customer);

		JOptionPane.showMessageDialog(null, "Cập nhật thông tin khách hàng thành công!");
	}

	public void refesh(JTextField txtMaKH, JTextField txtCustomerName, JTextField txtPhoneNumber, JTextArea txaNote) {
		// TODO Auto-generated method stub
		txtMaKH.setText("");
		txtCustomerName.setText("");
		txtPhoneNumber.setText("");
		txaNote.setText("");
	}

	public boolean processSearch(JTextField txtTimSDT, JTable table) {
		String phoneNumber = txtTimSDT.getText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 2).equals(phoneNumber)) {
				table.setRowSelectionInterval(i, i);
				JOptionPane.showMessageDialog(null, "Khách hàng được tìm thấy!");
				return true;
			}
		}
		JOptionPane.showMessageDialog(null, "Không tìm thấy số điện thoại!");
		return false;
	}

	public void createUI() {
		Icon img_add = new ImageIcon("src/main/java/img/add16.png");
		Icon img_del = new ImageIcon("src/main/java/img/bin.png");
		Icon img_reset = new ImageIcon("src/main/java/img/refresh16.png");
		Icon img_edit = new ImageIcon("src/main/java/img/edit16.png");
		Icon img_out = new ImageIcon("src/main/java/img/out.png");
		Icon img_search = new ImageIcon("src/main/java/img/search.png");
		Icon img_refresh = new ImageIcon("src/main/java/img/refresh16.png");
		Border line = BorderFactory.createLineBorder(Color.BLACK);

		// Thông tin kháchh hàng
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new GridLayout(3, 2, 40, 0));
		pnlInput.add(createPanel(lblMaKH = new JLabel("Mã khách hàng"), txtMaKH = new JTextField()));
		pnlInput.add(createPanel(lblTenKhachHang = new JLabel("Tên khách hàng"), txtCustomerName = new JTextField()));
		pnlInput.add(createPanel(lblSoDienThoai = new JLabel("Số điện thoại"), txtPhoneNumber = new JTextField()));
		pnlInput.add(createPanel(lblGhiChu = new JLabel("Ghi chú"), txaNote = new JTextArea()));

		// Nút chức năng
		JPanel pnlChucNang = new JPanel();
		pnlChucNang.setBackground(Color.decode("#cccccc"));
		pnlChucNang.setLayout(new GridLayout(3, 2));
		pnlChucNang.add(btnThemMoi = new ButtonGradient("Thêm mới", img_add));
		pnlChucNang.add(btnCapNhat = new ButtonGradient("Cập nhật", img_edit));
		pnlChucNang.add(btnLamMoi = new ButtonGradient("Làm mới", img_reset));

		Box bThongTinKH = Box.createHorizontalBox();
		bThongTinKH.add(pnlInput);
		bThongTinKH.add(Box.createHorizontalStrut(50));
		bThongTinKH.add(pnlChucNang);
		JPanel pnlThongTinKH = new JPanel();
		pnlThongTinKH.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
		pnlThongTinKH.add(bThongTinKH);

		// Set kích thước
		Dimension dimension = new Dimension(250, 30);
		txtMaKH.setPreferredSize(dimension);
		txtCustomerName.setPreferredSize(dimension);
		txtPhoneNumber.setPreferredSize(dimension);
		txaNote.setPreferredSize(dimension);
		lblTenKhachHang.setPreferredSize(lblTenKhachHang.getPreferredSize());
		lblSoDienThoai.setPreferredSize(lblTenKhachHang.getPreferredSize());
		lblGhiChu.setPreferredSize(lblTenKhachHang.getPreferredSize());
		int preferredWidth = 300;
		Dimension preferredSize = new Dimension(preferredWidth, pnlChucNang.getPreferredSize().height);
		pnlChucNang.setPreferredSize(preferredSize);

		// Tìm
		JPanel pnlTim = new JPanel();
		pnlTim.add(lblSoDienThoai = new JLabel("Số điện thoại"));
		pnlTim.add(txtTimSDT = new JTextField(15));
		pnlTim.add(btnTim = new ButtonGradient("Tìm kiếm", img_search));

		// Table
		JPanel pnlTable = new JPanel();
		pnlTable.setBorder(BorderFactory.createTitledBorder(line, "Danh sách khách hàng "));
		table = new JTable();
		tableModel = new DefaultTableModel(headers, 0);
		table.setModel(tableModel);
		table.setRowHeight(25);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(table);
		pnlTable.setLayout(new BorderLayout());
		pnlTable.add(pnlTim, BorderLayout.NORTH);
		pnlTable.add(scroll, BorderLayout.CENTER);

		// Set editable
		txtMaKH.setEnabled(false);

		// Add vào giao diện
		setLayout(new BorderLayout());
		add(pnlThongTinKH, BorderLayout.NORTH);
		add(pnlTable, BorderLayout.CENTER);
		pnlTim.setBackground(Color.decode("#D0BAFB"));
		pnlTable.setBackground(Color.decode("#D0BAFB"));
		pnlInput.setBackground(Color.decode("#D0BAFB"));
		pnlChucNang.setBackground(Color.decode("#D0BAFB"));
		pnlThongTinKH.setBackground(Color.decode("#D0BAFB"));
	}


	// Xoa toan bo khach hang

	// Kiem tra rang buoc

	@Override
	public void mousePressed(MouseEvent e) {
		int row = table.getSelectedRow();
		txtMaKH.setText(table.getValueAt(row, 0).toString());
		txtCustomerName.setText(table.getValueAt(row, 1).toString());
		txtPhoneNumber.setText(table.getValueAt(row, 2).toString());
		txaNote.setText(table.getValueAt(row, 3).toString());
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
