package entity;

import jakarta.persistence.*;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String address;

    public Address() {}

    public Address(Customer customer, String address) {
        this.customer = customer;
        this.address = address;
    }
}

