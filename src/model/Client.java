package model;

import java.util.ArrayList;
import java.util.List;

public class Client extends User{
    private String deliveryAddress;
    private String phoneNumber;

    private List<Order> orderHistory;

    public Client(){
        super();
        this.orderHistory=new ArrayList<>();
    }

    public Client(int id, String name, String email, String password, String deliveryAddress, String phoneNumber) {
        super(id, name, email, password);
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.orderHistory = new ArrayList<>();
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
       if(deliveryAddress!=null && deliveryAddress.length()>3){
           this.deliveryAddress = deliveryAddress;
       }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber!=null && phoneNumber.matches("\\d{10,}")){
            this.phoneNumber = phoneNumber;
        }
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public void addOrder(Order order){
        if(order!=null){
            this.orderHistory.add(order);
        }
    }

    @Override
    public String toString() {
        return "Client{" +super.toString() +
                "deliveryAddress='" + deliveryAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", orderHistory=" + orderHistory +
                "} ";
    }
}
