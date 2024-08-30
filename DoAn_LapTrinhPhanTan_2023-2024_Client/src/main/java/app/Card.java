package app;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import entity.Employee;
import service.EmployeeService;

public class Card extends JFrame {
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	private JButton btnTrangChu, btnPhongHat, btnPhong, btnDichVu, btnKhachHang, btnNhanVien, btnHoaDon, btnThongKe,
			btnTroGiup, btnDangXuat;
	private JLabel lbTenNV;
	private JPanel pnl = new JPanel(new CardLayout());
	private PanelHome tc = new PanelHome();
	private PanelBooking dp;
	private PanelRoom phong = new PanelRoom();
	private PanelService dv = new PanelService();
	private PanelEmployee nv = new PanelEmployee();
	private PanelCustomer kh = new PanelCustomer();
	private PanelBill hd = new PanelBill();
	private PanelStatistic tk = new PanelStatistic();
	private JPanel pnlButton = new JPanel();
	private int employeeID;
	private EmployeeService eDAO;

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public Card(int employeeID) throws RemoteException, MalformedURLException, NotBoundException {
		eDAO = (EmployeeService) Naming.lookup(URL + "employeeService");
		this.employeeID = employeeID;
		dp = new PanelBooking("1234");
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
		addEventListeners();
	}

	private void createUI() throws RemoteException {
		// Set frame properties
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1400, 720);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		// Create components
		btnTrangChu = createButton(" TRANG CHỦ ", "src/main/java/img/home.png");
		btnPhongHat = createButton(" PHÒNG HÁT ", "src/main/java/img/karaoke.png");
		btnPhong = createButton(" PHÒNG          ", "src/main/java/img/room.png");
		btnDichVu = createButton(" DỊCH VỤ         ", "src/main/java/img/service.png");
		btnKhachHang = createButton(" KHÁCH HÀNG", "src/main/java/img/client.png");
		btnNhanVien = createButton(" NHÂN VIÊN ", "src/main/java/img/staff.png");
		btnHoaDon = createButton(" HÓA ĐƠN   ", "src/main/java/img/invoice.png");
		btnThongKe = createButton(" THỐNG KÊ  ", "src/main/java/img/statistical.png");
		btnTroGiup = createButton(" TRỢ GIÚP  ", "src/main/java/img/help.png");
		btnDangXuat = createButton(" ĐĂNG XUẤT ", "src/main/java/img/out.png");
		createPanelLayout();

		// Add components to the frame
		add(createLeftPanel(), BorderLayout.WEST);
		add(pnl, BorderLayout.CENTER);
//		add(createNVPanel(), BorderLayout.SOUTH);
	}

	private JButton createButton(String text, String iconPath) {
		ImageIcon icon = new ImageIcon(iconPath);
		JButton button = new JButton(text, icon);
		button.setFont(new Font("Arial", Font.BOLD, 12));
		button.setBackground(Color.decode("#e6dbd1"));
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(180, 40));
		pnlButton.add(button);
		return button;
	}

	private void createPanelLayout() {
		pnl.add(tc, "tc");
		pnl.add(dp, "dp");
		pnl.add(dv, "dv");
		pnl.add(phong, "phong");
		pnl.add(nv, "nv");
		pnl.add(kh, "kh");
		pnl.add(hd, "hd");
		pnl.add(tk, "tk");
	}

	private JPanel createLeftPanel() throws RemoteException {
		JPanel pnlLeft = new JPanel(new BorderLayout());
		pnlLeft.add(createLogoPanel(), BorderLayout.NORTH);
		pnlLeft.add(pnlButton, BorderLayout.CENTER);
		pnlLeft.add(createNVPanel(), BorderLayout.SOUTH);
//		pnlLeft.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlButton.setPreferredSize(new Dimension(200, 700));
		return pnlLeft;
	}

	private JPanel createLogoPanel() {
		JPanel pnlLogo = new JPanel();
		ImageIcon icon = new ImageIcon("src/main/java/img/Logo.png");
		JLabel label = new JLabel(icon);
		pnlLogo.add(label);
		pnlLogo.setBackground(Color.decode("#990447"));
		pnlButton.setBackground(Color.decode("#990447"));
		return pnlLogo;
	}

	private JPanel createNVPanel() throws RemoteException {
		Employee employee = eDAO.getEmployeeByID(employeeID);
		JPanel pnlNV = new JPanel();
		lbTenNV = new JLabel();
		lbTenNV.setText(employee.getName().trim());
		JPanel pnlMain = new JPanel();
		ImageIcon icon = new ImageIcon("src/main/java/img/logo2.png");
		JLabel label = new JLabel(icon);
		lbTenNV.setFont(new Font("Sanserif", Font.BOLD, 14));
		lbTenNV.setForeground(Color.white);
		pnlNV.setLayout(new BorderLayout());
		pnlNV.add(label, BorderLayout.WEST);
		pnlNV.add(lbTenNV, BorderLayout.CENTER);
		pnlNV.setBackground(Color.decode("#990447"));
		pnlMain.add(pnlNV);
		pnlMain.setBackground(Color.decode("#990447"));
		return pnlMain;
	}

	private void addEventListeners() {
		List<JButton> buttons = Arrays.asList(btnTrangChu, btnPhongHat, btnPhong, btnDichVu, btnKhachHang, btnNhanVien,
				btnHoaDon, btnThongKe, btnTroGiup, btnDangXuat);
		buttons.forEach(button -> button.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(Color.decode("#e6dbd1"));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(Color.decode("#B5B5B5"));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		}));
		btnPhong.addActionListener(e -> xuLyPhong());
		btnPhongHat.addActionListener(e -> xuLyPhongHat());
		btnKhachHang.addActionListener(e -> xuLyKhachHang());
		btnNhanVien.addActionListener(e -> xuLyNhanVien());
		btnHoaDon.addActionListener(e -> xuLyHoaDon());
		btnDichVu.addActionListener(e -> xuLyDichVu());
		btnThongKe.addActionListener(e -> xuLyThongKe());
		btnTroGiup.addActionListener(e -> xuLyTroGiup());
		btnTrangChu.addActionListener(e -> xuLyTrangChu());
		btnDangXuat.addActionListener(e -> {
			try {
				try {
					xuLyDangXuat();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private void xuLyPhong() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "phong");
	}

	private void xuLyPhongHat() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "dp");
	}

	private void xuLyDichVu() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "dv");
	}

	private void xuLyNhanVien() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "nv");
	}

	private void xuLyKhachHang() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "kh");
	}

	private void xuLyHoaDon() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "hd");
	}

	private void xuLyThongKe() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "tk");
	}

	private void xuLyTroGiup() {
		int result = JOptionPane.showConfirmDialog(null, "Bạn muốn xem hướng dãn sử dụng không?", "Chú ý",
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			try {
				Desktop.getDesktop().open(new File("document\\UserManual.pdf"));
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void xuLyTrangChu() {
		CardLayout card = (CardLayout) pnl.getLayout();
		card.show(pnl, "tc");
	}

	private void xuLyDangXuat() throws RemoteException, MalformedURLException, NotBoundException {
		int result = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?",
				"Chú ý", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			dispose();
			new FrameLogin().setVisible(true);
		}
	}
}
