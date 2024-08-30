package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import service.RoomService;

public class Client {
	private static final String URL = "rmi://DESKTOP-K2I7FKM:9571/";
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		RoomService roomService = (RoomService) Naming.lookup(URL + "roomService");
		roomService.getAllRooms().forEach(e -> System.out.println(e));
	}
}
