package service;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Account;
import model.Customer;

@Stateless
public class Branch implements BranchLocal {

	@PersistenceContext
	EntityManager em;
	
	public Branch() {}

	public double totalAmount() {
		List<Account> l = em.createNamedQuery("Account.findAllPositiveAmounts", Account.class).getResultList();
		double total = 0;
		for(int i=0;i<l.size();i++) {
			total+=l.get(i).getBalance();
		}
		return total;
	}

	public int createAccount(String cf, double am) throws Exception {
		Customer cust = em.find(Customer.class,  cf);
		if(cust != null) {
			Account account = new Account(cust, am);
			em.persist(account);
			return account.getAccountId();
		}
		throw new Exception();
	}

	public Account getAccount(int num) {
		return em.find(Account.class, num);
	}
	
	public List<Account> getAccounts(String cf){
		Customer c = getCustomer(cf);
		return c.getAccounts();
	}
	

	public void deposit(int accountId, double amount) throws Exception {
		Account a = em.find(Account.class,  accountId);
		if(a != null) a.deposit(amount);
		else throw new Exception();
		
	}
	

	public void withdraw(int accountId, double amount) throws Exception {
		Account a = em.find(Account.class,  accountId);
		if(a != null) a.withdraw(amount);
		else throw new Exception();
		
	}
	
	public void createCustomer(String cf, String fn, String ln)throws Exception {
		Customer c = new Customer(cf,fn,ln);
		em.persist(c);
	}

	@Override
	public Customer getCustomer(String cf) {
		return em.find(Customer.class, cf);
	}
	

}
