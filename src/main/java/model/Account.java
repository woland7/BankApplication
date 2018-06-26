package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Account
 *
 */
@Entity
@NamedQueries({@NamedQuery(name = "Account.findAllPositiveAmounts",query = "SELECT a from Account a WHERE a.balance>0"),
		@NamedQuery(name = "Account.findAllCustomerAccounts",query = "SELECT a from Account a WHERE a.customer.CF = :CF")})

public class Account implements Serializable {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int accountId;
	private double balance;
	@ManyToOne
	@JoinColumn
	private Customer customer;
	
	private static final long serialVersionUID = 1L;

	public Account() {
		super();
	}   
	
	public Account (Customer c, double amount) {
		customer = c;
		balance = amount;
	}
	
	public Account(double bal){
		balance = bal;
	}
	
	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}   
	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public void deposit(double amount) {
		balance = balance + amount;
		
	}

	public void withdraw(double amount) {
		balance = balance - amount;
		
	}
   
}
