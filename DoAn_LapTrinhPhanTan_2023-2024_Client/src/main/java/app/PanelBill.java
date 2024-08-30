package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import entity.Bill;
import service.BillService;

public class PanelBill extends JPanel {
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private JTable table;
	private DefaultTableModel tableModel;
	private String[] headers = { "Mã hoá đơn", "Ngày thanh toán", "Giờ thanh toán", "Tên nhân viên",
			"Tên khách hàng", "Số điện thoại khách", "Tổng hoá đơn" };
	private JLabel lblTenNV, lblSđtKH, lblNgayBatDau, lblNgayKetThuc, lblThoiGian, lblMaHD;
	private JTextField txtTimNV, txtTimKH, txtTimMaHD;
	private ButtonGradient btnTim, btnLamMoi, btnXemCT;
	private JDateChooser dateBDTim, dateKTTim;
	private BillService billService;
	private JComboBox<String> cbLuaChon;
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DecimalFormat formatter = new DecimalFormat("###,###,### VNĐ");
	private List<Bill> listBill;

	public PanelBill() throws RemoteException, MalformedURLException, NotBoundException {
		billService = (BillService) Naming.lookup(URL + "billService");
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
		// Load data
		listBill = billService.getAllBills();
		loadData(listBill);

		// Sự kiện
		btnTim.addActionListener(e -> {
			try {
				processSearch();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnLamMoi.addActionListener(e -> xuLyLamMoi());
		cbLuaChon.addActionListener(e -> {
			try {
				xuLyCBLuaChon();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		dateBDTim.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (dateKTTim.getDate() != null)
					try {
						loadData(billService.getBillsByTimeFrame(
								dateBDTim.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
								dateKTTim.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		dateKTTim.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (dateBDTim.getDate() != null) {
					try {
						loadData(billService.getBillsByTimeFrame(
								dateBDTim.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
								dateKTTim.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnXemCT.addActionListener(e -> xuLyXemCT());

	}

	public void createUI() {
		Icon img_search = new ImageIcon("src/main/java/img/search.png");
		Icon img_refresh = new ImageIcon("src/main/java/img/refresh16.png");
		Icon img_detail = new ImageIcon("src/main/java/img/detail16.png");
		Box bThoiGian, bNgayBD, bNgayKT, bMaHD, bTenNV, bSDT, bb;
		Border line = BorderFactory.createLineBorder(Color.BLACK);
		Dimension dimension = new Dimension(180, 30);

		// NORT
		bb = Box.createHorizontalBox();
		bThoiGian = Box.createHorizontalBox();
		bNgayBD = Box.createHorizontalBox();
		bNgayKT = Box.createHorizontalBox();
		bMaHD = Box.createHorizontalBox();
		bTenNV = Box.createHorizontalBox();
		bSDT = Box.createHorizontalBox();

		bThoiGian.add(lblThoiGian = new JLabel("Thời gian"));
		lblThoiGian.setFont(new Font("Sanserif", Font.BOLD, 13));
		bThoiGian.add(cbLuaChon = new JComboBox<>());
		cbLuaChon.addItem("Tất cả");
		cbLuaChon.addItem("Ngày hiện tại");
		cbLuaChon.addItem("Tháng hiện tại");
		cbLuaChon.addItem("Năm hiện tại");

		bNgayBD.add(lblNgayBatDau = new JLabel("Từ ngày"));
		lblNgayBatDau.setFont(new Font("Sanserif", Font.BOLD, 13));
		bNgayBD.add(dateBDTim = new JDateChooser());
		bNgayKT.add(lblNgayKetThuc = new JLabel("Đến ngày"));
		lblNgayKetThuc.setFont(new Font("Sanserif", Font.BOLD, 13));
		bNgayKT.add(Box.createHorizontalStrut(5));
		bNgayKT.add(dateKTTim = new JDateChooser());
		bMaHD.add(lblMaHD = new JLabel("Mã hoá đơn"));
		lblMaHD.setFont(new Font("Sanserif", Font.BOLD, 13));
		bMaHD.add(txtTimMaHD = new JTextField(10));
		bTenNV.add(lblTenNV = new JLabel("Tên nhân viên"));
		lblTenNV.setFont(new Font("Sanserif", Font.BOLD, 13));
		bTenNV.add(txtTimNV = new JTextField(10));
		bSDT.add(lblSđtKH = new JLabel("Số điện thoại khách"));
		lblSđtKH.setFont(new Font("Sanserif", Font.BOLD, 13));
		bSDT.add(Box.createHorizontalStrut(5));
		bSDT.add(txtTimKH = new JTextField(10));

		JPanel pnlChucNang = new JPanel(new GridLayout(3, 1));
		pnlChucNang.add(btnXemCT = new ButtonGradient("Xem chi tiết", img_detail));
		pnlChucNang.add(btnTim = new ButtonGradient("Tìm kiếm", img_search));
		pnlChucNang.add(btnLamMoi = new ButtonGradient("Làm mới", img_refresh));
		JPanel pnlNorth = new JPanel(new GridLayout(2, 4, 20, 10));
		pnlNorth.add(bThoiGian);
		pnlNorth.add(bNgayBD);
		pnlNorth.add(bNgayKT);
		pnlNorth.add(bMaHD);
		pnlNorth.add(bTenNV);
		pnlNorth.add(bSDT);
		pnlNorth.setBorder(BorderFactory.createTitledBorder("Tra cứu"));
		bb.add(pnlNorth);
		bb.add(pnlChucNang);
		dateBDTim.setBackground(Color.decode("#D0BAFB"));
		dateKTTim.setBackground(Color.decode("#D0BAFB"));
		pnlNorth.setBackground(Color.decode("#D0BAFB"));
		pnlChucNang.setBackground(Color.decode("#D0BAFB"));

		cbLuaChon.setPreferredSize(dimension);
		dateBDTim.setPreferredSize(dimension);
		dateKTTim.setPreferredSize(dimension);
		txtTimMaHD.setPreferredSize(dimension);
		txtTimNV.setPreferredSize(dimension);
		txtTimKH.setPreferredSize(dimension);
		lblMaHD.setPreferredSize(lblSđtKH.getPreferredSize());
		lblThoiGian.setPreferredSize(lblSđtKH.getPreferredSize());
		lblNgayBatDau.setPreferredSize(lblSđtKH.getPreferredSize());
		lblNgayKetThuc.setPreferredSize(lblSđtKH.getPreferredSize());
		lblTenNV.setPreferredSize(lblSđtKH.getPreferredSize());

		// CENTER
		tableModel = new DefaultTableModel(headers, 0);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(BorderFactory.createTitledBorder(line, "Danh sách hoá đơn"));
		scroll.setViewportView(table = new JTable(tableModel));
		table.setRowHeight(25);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// Add giao diện
		this.setLayout(new BorderLayout());
		this.add(bb, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);

	}

	public void loadData(List<Bill> listBills) throws RemoteException {
		// delete all
		clearTable();
		for (Bill bill : listBills) {
			if(bill.getTotal() != null) {
				tableModel.addRow(new Object[] { bill.getId(), dateFormat.format(bill.getPaymentDate()),
						bill.getPaymentTime().toString(), bill.getCustomer().getCustomerName(),
						bill.getEmployee().getName(), bill.getCustomer().getPhoneNumber(),
						formatter.format(bill.getTotal())});
			}
		}
	}

	private void clearTable() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.setRowCount(0);
	}

	private Object xuLyCBLuaChon() throws RemoteException {
		// TODO Auto-generated method stub
		if (cbLuaChon.getSelectedItem().equals("Tất cả"))
			loadData(billService.getAllBills());
		else if (cbLuaChon.getSelectedItem().equals("Ngày hiện tại"))
			loadData(billService.getCurrentDayBills());
		else if (cbLuaChon.getSelectedItem().equals("Tháng hiện tại"))
			loadData(billService.getCurrentMonthBills());
		else if (cbLuaChon.getSelectedItem().equals("Năm hiện tại"))
			loadData(billService.getCurrentYearBills());
		return null;
	}

	private void processSearch() throws RemoteException {
		// TODO Auto-generated method stub
		clearTable();
		List<Bill> ds = billService.searchBills(txtTimMaHD.getText(), txtTimNV.getText(), txtTimKH.getText());
		if (ds.size() > 0) {
			loadData(ds);
			JOptionPane.showMessageDialog(null, "Đã tìm thấy hoá đơn!");
		} else
			JOptionPane.showMessageDialog(null, "Không tìm thấy hoá đơn!");
	}

	private Object xuLyXemCT() {
		// TODO Auto-generated method stub

		return null;
	}

	private Object xuLyLamMoi() {
		// TODO Auto-generated method stub
		txtTimKH.setText("");
		txtTimNV.setText("");
		txtTimMaHD.setText("");
		((JTextField) dateBDTim.getDateEditor().getUiComponent()).setText("");
		((JTextField) dateKTTim.getDateEditor().getUiComponent()).setText("");
		return null;
	}
}