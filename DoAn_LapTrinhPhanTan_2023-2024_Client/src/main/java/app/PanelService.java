package app;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import entity.Service;
import service.ServiceService;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.List;
public class PanelService extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private static final long serialVersionUID = 1L;
	private JLabel lblTenDichVu, lblDonGia, lblDonVi, lblSoLuong, lblTinhTrang, lblLocTinhTrang, lblTimDV, lblMaDV;
	private JTextField txtServiceName, txtPrice, txtUnit, txtQuantity, txtFindService, txtServiceID, txtStatus;
	private JButton btnThemMoi, btnCapNhat, btnXoa, btnLamMoi, btnTim, btnLuu;
	private JComboBox cbTinhTrang;
	private JTable table;
	private DefaultTableModel tableModel;
	private Box bLeft, bRight;
	private List<Service> listService;
	private ServiceService serviceService;
	private DecimalFormat formatter = new DecimalFormat("###");

	public PanelService() throws RemoteException, MalformedURLException, NotBoundException{
		createUI();
		serviceService = (ServiceService) Naming.lookup(URL + "serviceService");
		// add event button
		fetchAllService(); 
		table.addMouseListener(this);
		btnThemMoi.addActionListener(e -> {
			try {
				processAdd();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnLamMoi.addActionListener(e -> refesh());
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
				processUpdate();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnTim.addActionListener(e -> search());
		cbTinhTrang.addActionListener(e -> {
			try {
				filterServicesByStatus();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private void createUI() throws RemoteException{
		Icon img_add = new ImageIcon("src/main/java/img/add16.png");
		Icon img_del = new ImageIcon("src/main/java/img/bin.png");
		Icon img_reset = new ImageIcon("src/main/java/img/refresh16.png");
		Icon img_edit = new ImageIcon("src/main/java/img/edit16.png");
		Icon img_search = new ImageIcon("src/main/java/img/search.png");
		Border line = BorderFactory.createLineBorder(Color.BLACK);

		// Thông tin khuyến mãi
		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new GridLayout(3, 2, 40, 0));
		pnlInput.add(createPanel(lblMaDV = new JLabel("Mã dịch vụ"), txtServiceID = new JTextField()));
		pnlInput.add(createPanel(lblTenDichVu = new JLabel("Tên dịch vụ"), txtServiceName = new JTextField()));
		pnlInput.add(createPanel(lblDonVi = new JLabel("Đơn vị"), txtUnit = new JTextField()));
		pnlInput.add(createPanel(lblDonGia = new JLabel("Đơn giá"), txtPrice = new JTextField()));
		pnlInput.add(createPanel(lblSoLuong = new JLabel("Số lượng"), txtQuantity = new JTextField()));
		pnlInput.add(createPanel(lblTinhTrang = new JLabel("Tình trạng"), txtStatus = new JTextField()));
 
		// Nút chức năng
		JPanel pnlChucNang = new JPanel();
		pnlChucNang.setLayout(new GridLayout(3, 2, 0, 10));
		pnlChucNang.add(btnThemMoi = new ButtonGradient("Thêm mới", img_add));
		pnlChucNang.add(btnCapNhat = new ButtonGradient("Cập nhật", img_edit));
		pnlChucNang.add(btnXoa = new ButtonGradient("Xóa", img_del));
		pnlChucNang.add(btnLamMoi = new ButtonGradient("Làm mới", img_reset));

		Box bThongTinKM = Box.createHorizontalBox();
		bThongTinKM.add(pnlInput);
		bThongTinKM.add(Box.createHorizontalStrut(50));
		bThongTinKM.add(pnlChucNang);
		JPanel pnlThongTinKM = new JPanel();
		pnlThongTinKM.setBorder(BorderFactory.createTitledBorder("Thông tin dịch vụ"));
		pnlThongTinKM.add(bThongTinKM);

		// Set kích thước
		Dimension dimension = new Dimension(250, 30);
		txtServiceName.setPreferredSize(dimension);
		txtPrice.setPreferredSize(dimension);
		txtQuantity.setPreferredSize(dimension);
		txtUnit.setPreferredSize(dimension);
		txtServiceID.setPreferredSize(dimension);
		txtStatus.setPreferredSize(dimension);
		lblMaDV.setPreferredSize(lblTenDichVu.getPreferredSize());
		lblTinhTrang.setPreferredSize(lblTenDichVu.getPreferredSize());
		lblDonGia.setPreferredSize(lblTenDichVu.getPreferredSize());
		lblDonVi.setPreferredSize(lblTenDichVu.getPreferredSize());
		lblSoLuong.setPreferredSize(lblTenDichVu.getPreferredSize());
		int preferredWidth = 300;
		Dimension preferredSize = new Dimension(preferredWidth, pnlChucNang.getPreferredSize().height);
		pnlChucNang.setPreferredSize(preferredSize);

		// Tìm
		JPanel pnlLoc = new JPanel();
		String[] cbTT = { "Tất cả", "Còn hàng", "Hết hàng", "Sắp hết hàng" };
		pnlLoc.add(lblTinhTrang = new JLabel("Tình trạng"));
		pnlLoc.add(Box.createHorizontalStrut(30));
		pnlLoc.add(cbTinhTrang = new JComboBox<>(cbTT));
		cbTinhTrang.setPreferredSize(dimension);
		JPanel pnlTim = new JPanel();
		pnlTim.add(lblTenDichVu = new JLabel("Tên dịch vụ"));
		pnlTim.add(txtFindService = new JTextField(20));
		pnlTim.add(btnTim = new ButtonGradient("Tìm", img_search));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLoc, pnlTim);
		splitPane.setDividerLocation(600);

		// Table
		JPanel pnlTable = new JPanel();
		pnlTable.setBorder(BorderFactory.createTitledBorder(line, "Danh sách dịch vụ"));
		table = new JTable();
		String[] headers = "Mã dịch vụ;Tên dịch vụ;Đơn giá;Đơn vị;Số lượng;Tình trạng".split(";");
		tableModel = new DefaultTableModel(headers, 0);
		table.setModel(tableModel);
		table.setRowHeight(25);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(table);
		pnlTable.setLayout(new BorderLayout());
		pnlTable.add(splitPane, BorderLayout.NORTH);
		pnlTable.add(scroll, BorderLayout.CENTER);

		// Set editable
		txtServiceID.setEnabled(false);
		txtStatus.setEnabled(false);

		// Add vào giao diện
		setLayout(new BorderLayout());
		add(pnlThongTinKM, BorderLayout.NORTH);
		add(pnlTable, BorderLayout.CENTER);
		pnlLoc.setBackground(Color.decode("#D0BAFB"));
		pnlTim.setBackground(Color.decode("#D0BAFB"));
		pnlTable.setBackground(Color.decode("#D0BAFB"));
		pnlInput.setBackground(Color.decode("#D0BAFB"));
		pnlChucNang.setBackground(Color.decode("#D0BAFB"));
		pnlThongTinKM.setBackground(Color.decode("#D0BAFB"));
	}

	private JPanel createPanel(JLabel label, JComponent component) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(label);
		panel.add(component);
		label.setFont(new Font("Sanserif", Font.BOLD, 13));
		panel.setBackground(Color.decode("#D0BAFB"));
		return panel;
	}

	// Xu ly kiem tra thong tin day du
	private boolean validateInput() {
	    String serviceName = txtServiceName.getText();
	    String unit = txtUnit.getText();
	    String price = txtPrice.getText();
	    String quantity = txtQuantity.getText();

	    if (serviceName.trim().isEmpty() || unit.trim().isEmpty() || price.trim().isEmpty() || quantity.trim().isEmpty()) {
	        return false;
	    }
	    Pattern serviceNamePattern = Pattern.compile("[a-zA-Z_0-9]");
	    if (!serviceNamePattern.matcher(serviceName).find()) {
	        JOptionPane.showMessageDialog(null, "Tên dịch vụ không hợp lệ!");
	        return false;
	    }

	    Pattern unitPattern = Pattern.compile("[a-zA-Z]+$");
	    if (!unitPattern.matcher(unit).find()) {
	        JOptionPane.showMessageDialog(null, "Đơn vị chỉ bao gồm chữ cái!");
	        return false;
	    }

	    Pattern pricePattern = Pattern.compile("^\\d+$");
	    if (!pricePattern.matcher(price).find()) {
	        JOptionPane.showMessageDialog(null, "Đơn giá nhập không hợp lệ!");
	        return false;
	    }

	    Pattern quantityPattern = Pattern.compile("^\\d+$");
	    if (!(quantity.length() > 0 && quantityPattern.matcher(quantity).find())) {
	        JOptionPane.showMessageDialog(null, "Số lượng không hợp lệ!");
	        return false;
	    }

	    return true;
	}

	// Xu ly them moi
	private void processAdd() throws RemoteException {		    
	    if (!validateInput()) {
	        JOptionPane.showMessageDialog(null, "Kiểm tra lại thông tin dịch vụ!");
	        return;
	    }
	    txtServiceID.setText((serviceService.getAllServices().size()+1)+"");
	    String serviceID = txtServiceID.getText();
	    String serviceName = txtServiceName.getText();
	    String unit = txtUnit.getText();
	    double price = Double.parseDouble(txtPrice.getText());
	    int quantity = Integer.parseInt(txtQuantity.getText());
	    String status = "";
	    if (quantity == 0) {
	        status = "Hết hàng";
	    } else if (quantity > 0 && quantity <= 10) {
	        status = "Sắp hết hàng";
	    } else if (quantity > 10) {
	        status = "Còn hàng";
	    }
	    Service service = new Service(Integer.parseInt(serviceID), serviceName, price, unit, quantity, status);

	    int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn thêm mới dịch vụ không ?", "Chú ý!",
	            JOptionPane.YES_OPTION);
	    if (confirmation == JOptionPane.YES_OPTION && serviceService.addService(service)) {
	        Object[] rowData = { serviceID, serviceName, price, unit, quantity, status };
	        tableModel.addRow(rowData);
	        JOptionPane.showMessageDialog(null, "Thêm mới dịch vụ thành công!");
	    }
	}


	// Xu ly cap nhat 
	private void processUpdate() throws RemoteException {
	    int selectedRow = table.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(null, "Vui lòng chọn dịch vụ cần cập nhật!");
	        return;
	    }

	    if (!validateInput()) {
	        JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin dịch vụ!");
	        return;
	    }

	    int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn cập nhật dịch vụ không ?", "Chú ý!", JOptionPane.YES_NO_OPTION);
	    if (confirmation != JOptionPane.YES_OPTION) {
	        return;
	    }
	    int serviceID = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
	    String serviceName = txtServiceName.getText();
	    String unit = txtUnit.getText();
	    double price = Double.parseDouble(txtPrice.getText());
	    int quantity = Integer.parseInt(txtQuantity.getText());

	    table.setValueAt(serviceName, selectedRow, 1);
	    table.setValueAt(price, selectedRow, 2);
	    table.setValueAt(unit, selectedRow, 3);
	    table.setValueAt(quantity, selectedRow, 4);

	    String currentStatus = table.getValueAt(selectedRow, 5).toString();
	    Service service = new Service(serviceID, serviceName, price, unit, quantity, currentStatus);
	    serviceService.updateService(service);

	    if (quantity == 0) {
	        table.setValueAt("Hết hàng", selectedRow, 5);
	    } else if (quantity > 10) {
	        table.setValueAt("Còn hàng", selectedRow, 5);
	    } else if (quantity > 0 && quantity <= 10) {
	        table.setValueAt("Sắp hết hàng", selectedRow, 5);
	    }
	    table.setValueAt(formatter.format(price), selectedRow, 2);

	    JOptionPane.showMessageDialog(null, "Cập nhật thông tin dịch vụ thành công!");
	}


	// Xu ly lam moi
	private void refesh() {
		txtServiceID.setText("");
		txtServiceName.setText("");
		txtUnit.setText("");
		txtPrice.setText("");
		txtQuantity.setText("");
		txtFindService.setText("");
		txtStatus.setText("");
		cbTinhTrang.setSelectedIndex(0);
	}

	// Xu ly xoa
	private void processDelete() throws RemoteException {
	    int row = table.getSelectedRow();
	    if (row == -1) {
	        JOptionPane.showMessageDialog(null, "Vui lòng chọn dịch vụ cần xóa!");
	        return;
	    }

	    int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa dịch vụ này không?", "Chú ý!", JOptionPane.YES_NO_OPTION);
	    if (confirmation != JOptionPane.YES_OPTION) {
	        return;
	    }

	    int serviceID = Integer.parseInt(table.getValueAt(row, 0).toString());
	    if (serviceService.deleteService(serviceID)) {
	        tableModel.removeRow(row);
	        JOptionPane.showMessageDialog(null, "Xóa dịch vụ thành công!");
	    } else {
	        JOptionPane.showMessageDialog(null, "Xóa dịch vụ không thành công!");
	    }
	}


	// Xu ly tim kiem
	private boolean search() {
	    String serviceName = txtFindService.getText();
	    for (int i = 0; i < table.getRowCount(); i++) {
	        if (table.getValueAt(i, 1).equals(serviceName)) {
	            table.setRowSelectionInterval(i, i);
	            JOptionPane.showMessageDialog(null, "Dịch vụ được tìm thấy!");
	            return true;
	        }
	    }
	    JOptionPane.showMessageDialog(null, "Dịch vụ không tồn tại!");
	    return false;
	}


	// Lay toan bo dich vu
	private void fetchAllService() throws RemoteException  {
		clearTable();
	    listService = serviceService.getAllServices();
	    for (entity.Service dv : listService) {
	        Object[] rowData = new Object[] { 
	            dv.getId(), 
	            dv.getName(), 
	            formatter.format(dv.getPrice()), 
	            dv.getUnit(), 
	            dv.getInventoryNumber(), 
	            dv.getStatus() 
	        };
	        tableModel.addRow(rowData);
	    }
	}

	// Xoa toan bo dich vu
	private void clearTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);
	}

	// Xu ly combo tinh trang
	private void filterServicesByStatus() throws RemoteException {
	    clearTable(); 
	    String selectedStatus = cbTinhTrang.getSelectedItem().toString();
	    listService = serviceService.getActiveServices(selectedStatus);
	    
	    for (Service s : listService) {
	        Object[] rowData = new Object[] { 
	            s.getId(), 
	            s.getName(), 
	            formatter.format(s.getPrice()), 
	            s.getUnit(), 
	            s.getInventoryNumber(), 
	            s.getStatus() 
	        };
	        tableModel.addRow(rowData);
	    }
	}



	// Xu ly mouseclick
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int row = table.getSelectedRow();
		txtServiceID.setText(table.getValueAt(row, 0).toString());
		txtServiceName.setText(table.getValueAt(row, 1).toString());
		txtPrice.setText(table.getValueAt(row, 2).toString());
		txtUnit.setText(table.getValueAt(row, 3).toString());
		txtQuantity.setText(table.getValueAt(row, 4).toString());
		txtStatus.setText(table.getValueAt(row, 5).toString());
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
}
