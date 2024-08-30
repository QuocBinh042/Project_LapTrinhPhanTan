
package app;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import entity.Employee;
import service.EmployeeService;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.List;

public class PanelEmployee extends JPanel implements MouseListener {
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private JLabel lblMaNhanVien, lblTenNhanVien, lblNamSinh, lblGioiTinh, lblSoDienThoai, lblCCCD, lblChucVu,
			lblMatKhau, lblTinhTrang;
	private JTextField txtEmployeeName, txtPhoneNumber, txtIdentityCard, txtPassword, txtTimNhanVien, txtTimSDT,
			txtEmployeeID, txtTinhTrang;
	private JButton btnThemMoi, btnCapNhat, btnXoa, btnLamMoiNV, btnThoat, btnTim, btnLamMoi, btnLuu;
	private JComboBox cbPosition, cbTinhTrang, cbGender, cbTimChucVu;
	private String[] headers = { "Mã nhân viên", "Tên nhân viên", "Năm sinh", "Giới tính", "Số điện thoại", "CCCD",
			"Chức vụ", "Mật khẩu", "Tình trạng" };
	private JDateChooser dateNamSinh;
	private JTable table;
	private DefaultTableModel tableModel;
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private EmployeeService employeeService;
	private List<Employee> listEmployee;
//	private MaTuDong maNhanVien = new MaTuDong();

	public PanelEmployee() throws RemoteException, MalformedURLException, NotBoundException {
		
		employeeService = (EmployeeService) Naming.lookup(URL + "employeeService");
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

		getAllEmployee();
		// sự kiện
		btnLamMoi.addActionListener(e -> refesh());
		btnThemMoi.addActionListener(e -> {
			try {
				processAdd();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnXoa.addActionListener(e -> processDelete());
		btnCapNhat.addActionListener(e -> {
			try {
				processUpdate();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnTim.addActionListener(e -> processSearch());
		cbTinhTrang.addActionListener(e -> {
			try {
				filterEmployeeByStatus();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		cbTimChucVu.addActionListener(e -> {
			try {
				filterEmployeeByPosition();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		table.addMouseListener(this);

	}

	private void createUI() {
		Icon img_add = new ImageIcon("src/main/java/img/add16.png");
		Icon img_del = new ImageIcon("src/main/java/img/bin.png");
		Icon img_reset = new ImageIcon("src/main/java/img/refresh16.png");
		Icon img_edit = new ImageIcon("src/main/java/img/edit16.png");
		Icon img_search = new ImageIcon("src/main/java/img/search.png");
		Border line = BorderFactory.createLineBorder(Color.BLACK);

		JPanel pnlInput = new JPanel();
		pnlInput.setLayout(new GridLayout(4, 2, 40, 0));
		pnlInput.add(createPanel(lblMaNhanVien = new JLabel("Mã nhân viên"), txtEmployeeID = new JTextField()));
		pnlInput.add(createPanel(lblTenNhanVien = new JLabel("Tên nhân viên"), txtEmployeeName = new JTextField()));
		pnlInput.add(createPanel(lblNamSinh = new JLabel("Ngày sinh"), dateNamSinh = new JDateChooser()));
		pnlInput.add(createPanel(lblGioiTinh = new JLabel("Giới tính"), cbGender = new JComboBox<>()));
		cbGender.addItem("Nam");
		cbGender.addItem("Nữ");
		pnlInput.add(createPanel(lblSoDienThoai = new JLabel("Số điện thoại"), txtPhoneNumber = new JTextField()));
		pnlInput.add(createPanel(lblCCCD = new JLabel("CCCD"), txtIdentityCard = new JTextField()));
		pnlInput.add(createPanel(lblChucVu = new JLabel("Chức vụ"), cbPosition = new JComboBox<>()));
		cbPosition.addItem("Lễ tân");
		cbPosition.addItem("Nhân viên quản lý");
		cbPosition.addItem("Phục vụ");
		pnlInput.add(createPanel(lblMatKhau = new JLabel("Mật khẩu"), txtPassword = new JTextField()));

		// Nút chức năng
		JPanel pnlChucNang = new JPanel();
		pnlChucNang.setLayout(new GridLayout(3, 2));
		pnlChucNang.add(btnThemMoi = new ButtonGradient("Thêm mới", img_add));
		pnlChucNang.add(btnCapNhat = new ButtonGradient("Cập nhật", img_edit));
		pnlChucNang.add(btnXoa = new ButtonGradient("Xóa", img_del));
		pnlChucNang.add(btnLamMoi = new ButtonGradient("Làm mới", img_reset));

		Box bThongTinKM = Box.createHorizontalBox();
		bThongTinKM.add(pnlInput);
		bThongTinKM.add(Box.createHorizontalStrut(50));
		bThongTinKM.add(pnlChucNang);
		JPanel pnlThongTinNV = new JPanel();
		pnlThongTinNV.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
		pnlThongTinNV.add(bThongTinKM);

		// Set kích thước
		Dimension dimension = new Dimension(250, 30);
		txtEmployeeID.setPreferredSize(dimension);
		txtEmployeeName.setPreferredSize(dimension);
		dateNamSinh.setPreferredSize(dimension);
		cbGender.setPreferredSize(dimension);
		txtPhoneNumber.setPreferredSize(dimension);
		txtIdentityCard.setPreferredSize(dimension);
		cbPosition.setPreferredSize(dimension);
		txtPassword.setPreferredSize(dimension);
		lblMaNhanVien.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblNamSinh.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblGioiTinh.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblSoDienThoai.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblCCCD.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblChucVu.setPreferredSize(lblTenNhanVien.getPreferredSize());
		lblMatKhau.setPreferredSize(lblTenNhanVien.getPreferredSize());
		int preferredWidth = 300;
		Dimension preferredSize = new Dimension(preferredWidth, pnlChucNang.getPreferredSize().height);
		pnlChucNang.setPreferredSize(preferredSize);

		// Tìm
		Box bLoc1, bLoc2;
		JPanel pnlTim = new JPanel();
		JPanel pnlLoc = new JPanel(new GridLayout(1, 2, 20, 0));
		bLoc1 = Box.createHorizontalBox();
		bLoc1.add(Box.createHorizontalStrut(10));
		bLoc1.add(lblChucVu = new JLabel("Chức vụ:"));
		bLoc1.add(Box.createHorizontalStrut(10));
		bLoc1.add(cbTimChucVu = new JComboBox<>());
		cbTimChucVu.addItem("Tất cả");
		cbTimChucVu.addItem("Lễ tân");
		cbTimChucVu.addItem("Nhân viên quản lý");
		cbTimChucVu.addItem("Phục vụ");
		bLoc2 = Box.createHorizontalBox();
		bLoc2.add(lblTinhTrang = new JLabel("Tình trạng:"));
		bLoc2.add(Box.createHorizontalStrut(10));
		bLoc2.add(cbTinhTrang = new JComboBox<>());
		cbTinhTrang.addItem("Tất cả");
		cbTinhTrang.addItem("Đang làm");
		cbTinhTrang.addItem("Nghỉ việc");
		pnlLoc.add(bLoc1);
		pnlLoc.add(bLoc2);
//		pnlLblTenNV.add(lblTenNhanVien = new JLabel("Tên nhân viên:"));
//		pnlTxtTenNV.add(txtTimNhanVien = new JTextField(10));
		pnlTim.add(lblSoDienThoai = new JLabel("Số điện thoại:"));
		pnlTim.add(txtTimNhanVien = new JTextField(15));
		pnlTim.add(btnTim = new ButtonGradient("Tìm kiếm", img_search));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlLoc, pnlTim);
		splitPane.setDividerLocation(700);

		// Table
		JPanel pnlTable = new JPanel();
		pnlTable.setBorder(BorderFactory.createTitledBorder(line, "Danh sách nhân viên"));
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
		pnlTable.add(splitPane, BorderLayout.NORTH);
		pnlTable.add(scroll, BorderLayout.CENTER);

		// Set editable
		txtEmployeeID.setEnabled(false);

		// Add vào giao diện
		setLayout(new BorderLayout());
		add(pnlThongTinNV, BorderLayout.NORTH);
		add(pnlTable, BorderLayout.CENTER);
		dateNamSinh.setBackground(Color.decode("#D0BAFB"));
		pnlLoc.setBackground(Color.decode("#D0BAFB"));
		pnlTim.setBackground(Color.decode("#D0BAFB"));
		pnlTable.setBackground(Color.decode("#D0BAFB"));
		pnlInput.setBackground(Color.decode("#D0BAFB"));
		pnlChucNang.setBackground(Color.decode("#D0BAFB"));
		pnlThongTinNV.setBackground(Color.decode("#D0BAFB"));
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

	private void processAdd() throws RemoteException {
		if (!validateInput()) {
			JOptionPane.showMessageDialog(null, "Vui lòng kiểm tra thông tin nhân viên!");
			return;
		}
		txtEmployeeID.setText((employeeService.getAllEmployees().size() + 2) + "");
		String employeeID = txtEmployeeID.getText();
		String employeeName = txtEmployeeName.getText();
		LocalDate dateOfBirth = dateNamSinh.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String gender = cbGender.getSelectedItem().toString();
		String phoneNumber = txtPhoneNumber.getText();
		String identityCard = txtIdentityCard.getText();
		String position = cbPosition.getSelectedItem().toString();
		String password = txtPassword.getText();

		// Tạo đối tượng Employee mới
		Employee employee = new Employee(Integer.parseInt(employeeID), employeeName, dateOfBirth,
				gender.equalsIgnoreCase("Nam"), phoneNumber, identityCard, position, password, true);

		try {
			if (employeeService.addEmployee(employee)) {
				String ngaySinh = dateFormat.format(dateOfBirth);
				String tinhTrang = "Đang làm";
				String[] rowData = { String.valueOf(employee.getId()), employeeName, ngaySinh, gender, phoneNumber,
						identityCard, position, password, tinhTrang };
				tableModel.addRow(rowData);
				JOptionPane.showMessageDialog(null, "Thêm mới nhân viên thành công!");
			} else {
				JOptionPane.showMessageDialog(null, "Mã nhân viên đã tồn tại!");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhân viên vào cơ sở dữ liệu!");
		}
	}

	private boolean validateInput() {
		String tenNV = txtEmployeeName.getText();
		String sdt = txtPhoneNumber.getText();
		String cccd = txtIdentityCard.getText();
		String matKhau = txtPassword.getText();

		Pattern p = Pattern.compile("[a-zA-Z]+");
		if (!(p.matcher(tenNV).find())) {
			JOptionPane.showMessageDialog(null, "Tên nhân viên không hợp lệ!");
			return false;
		}

		Pattern p1 = Pattern.compile("[0-9]{10}");
		if (!(p1.matcher(sdt).find())) {
			JOptionPane.showMessageDialog(null, "Số điện thoại chỉ được nhập chữ số!");
			return false;
		}

		Pattern p2 = Pattern.compile("[0-9]{12}");
		if (!(p2.matcher(cccd).find())) {
			JOptionPane.showMessageDialog(null, "Căn cước công dân chỉ được nhập chữ số!");
			return false;
		}

		Pattern p3 = Pattern.compile("(.)+");
		if (!(p3.matcher(matKhau).find())) {
			JOptionPane.showMessageDialog(null, "Mật khẩu không được để trống!");
			return false;
		}
		return true;
	}

	private void processDelete() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
			return;
		}

		int confirmation = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa thông tin nhân viên?",
				"Chú ý!", JOptionPane.YES_NO_OPTION);
		if (confirmation == JOptionPane.YES_OPTION) {
			tableModel.removeRow(selectedRow);
			JOptionPane.showMessageDialog(null, "Xóa thông tin nhân viên thành công!");
		}
	}

	private void processUpdate() throws RemoteException {
		int confirmation = JOptionPane.showConfirmDialog(null,
				"Bạn có chắc chắn muốn cập nhật thông tin nhân viên không?", "Chú ý", JOptionPane.YES_NO_OPTION);
		if (confirmation != JOptionPane.YES_OPTION) {
			return;
		}

		if (!validateInput()) {
			JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin nhân viên!");
			return;
		}

		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần cập nhật!");
			return;
		}

		String employeeID = table.getValueAt(table.getSelectedRow(), 0).toString();
		String employeeName = txtEmployeeName.getText();
		LocalDate dateOfBirth = dateNamSinh.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String gender = cbGender.getSelectedItem().toString();
		String phoneNumber = txtPhoneNumber.getText();
		String identityCard = txtIdentityCard.getText();
		String position = cbPosition.getSelectedItem().toString();
		String password = txtPassword.getText();
		String status = table.getValueAt(table.getSelectedRow(), 8).toString();

		boolean isActive = status.equalsIgnoreCase("Đang làm");
		String formattedDateOfBirth = dateFormat.format(dateOfBirth);

		String[] rowData = { employeeID, employeeName, formattedDateOfBirth, gender, phoneNumber, identityCard,
				position, password, status };

		try {
			employeeService.updateEmployee(new Employee(Integer.parseInt(employeeID), employeeName, null, isActive,
					phoneNumber, status, position, password, isActive));

			tableModel.removeRow(selectedRow); // Remove the row after successful update
			tableModel.insertRow(selectedRow, rowData); // Insert the updated row
			JOptionPane.showMessageDialog(null, "Cập nhật thông tin nhân viên thành công!");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private Object refesh() {
		// TODO Auto-generated method stub
		txtEmployeeID.setText("");
		txtEmployeeName.setText("");
		((JTextField) dateNamSinh.getDateEditor().getUiComponent()).setText("");
		txtPhoneNumber.setText("");
		txtIdentityCard.setText("");
		txtPassword.setText("");
		cbPosition.setSelectedIndex(-1);
		cbGender.setSelectedIndex(-1);
		return null;
	}

	private boolean processSearch() {
		String employeeNameToSearch = txtTimNhanVien.getText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 4).toString().equalsIgnoreCase(employeeNameToSearch)) {
				table.setRowSelectionInterval(i, i);
				JOptionPane.showMessageDialog(null, "Nhân viên được tìm thấy!");
				return true;
			}
		}
		JOptionPane.showMessageDialog(null, "Tên nhân viên không tồn tại!");
		return false;
	}

	// Xu ly combo tinh trang
	private void filterEmployeeByStatus() throws RemoteException {
		clearTable();
		String selectedStatus = cbTinhTrang.getSelectedItem().toString();
		boolean status;
		if ("Tất cả".equals(selectedStatus)) {
			listEmployee = employeeService.getAllEmployees();
		} else {
			status = "Đang làm".equals(selectedStatus) ? true : false;
			listEmployee = employeeService.getEmployeesByStatus(status);
		}
		for (Employee employee : listEmployee) {
			String ngaySinh = dateFormat.format(employee.getBirth());
			String gioiTinh = (employee.isGender()) ? "Nam" : "Nữ";
			String tinhTrang = employee.isEmployeeStatus() ? "Đang làm" : "Nghỉ việc";
			tableModel.addRow(
					new Object[] { employee.getId(), employee.getName(), ngaySinh, gioiTinh, employee.getPhoneNumber(),
							employee.getCI(), employee.getPosition(), employee.getPassword(), tinhTrang });
		}
	}

	private void filterEmployeeByPosition() throws RemoteException {
		clearTable();
		String selectedPosition = cbTimChucVu.getSelectedItem().toString();
		listEmployee = employeeService.getEmployeesByPosition(selectedPosition);
		for (Employee employee : listEmployee) {
			String ngaySinh = dateFormat.format(employee.getBirth());
			String gioiTinh = (employee.isGender()) ? "Nam" : "Nữ";
			String tinhTrang = employee.isEmployeeStatus() ? "Đang làm" : "Nghỉ việc";
			tableModel.addRow(
					new Object[] { employee.getId(), employee.getName(), ngaySinh, gioiTinh, employee.getPhoneNumber(),
							employee.getCI(), employee.getPosition(), employee.getPassword(), tinhTrang });
		}
	}

	// Lay toan bo nhan vien
	private void getAllEmployee() throws RemoteException {
		clearTable();
		listEmployee = employeeService.getAllEmployees();
		for (Employee employee : listEmployee) {
			String ngaySinh = dateFormat.format(employee.getBirth());
			String gioiTinh = (employee.isGender()) ? "Nam" : "Nữ";
			String tinhTrang = employee.isEmployeeStatus() ? "Đang làm" : "Nghỉ việc";
			tableModel.addRow(
					new Object[] { employee.getId(), employee.getName(), ngaySinh, gioiTinh, employee.getPhoneNumber(),
							employee.getCI(), employee.getPosition(), employee.getPassword(), tinhTrang });

		}
	}

	// Xoa toan bo dich vu
	private void clearTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);
	}

	// Xu ly mouseclick
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int row = table.getSelectedRow();
		txtEmployeeID.setText(table.getValueAt(row, 0).toString());
		txtEmployeeName.setText(table.getValueAt(row, 1).toString());
		LocalDate date;
		try {
			date = LocalDate.parse(table.getValueAt(row, 2).toString(), dateFormat);
			dateNamSinh.setDate(java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} catch (DateTimeParseException e1) {
			e1.printStackTrace();
		}
		cbGender.setSelectedItem(table.getValueAt(row, 3).toString());
		txtPhoneNumber.setText(table.getValueAt(row, 4).toString());
		txtIdentityCard.setText(table.getValueAt(row, 5).toString());
		cbPosition.setSelectedItem(table.getValueAt(row, 6).toString());
		txtPassword.setText(table.getValueAt(row, 7).toString());
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
