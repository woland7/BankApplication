package service;

import java.util.List;
import javax.ejb.Local;
import model.Account;
import model.Customer;

@Local
public interface BranchLocal {
public int createAccount(String cf, double a) throws Exception;
public Account getAccount(int num);
public List<Account> getAccounts(String cf);
public void deposit(int num, double a) throws Exception;
public void withdraw(int num, double a) throws Exception;
public double totalAmount();
public void createCustomer(String cf, String fn, String ln) throws Exception;
public Customer getCustomer(String cf);
}
