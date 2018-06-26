package controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import model.Account;
import model.Customer;
import service.BranchLocal;

/**
 * Servlet implementation class BankController
 */
@WebServlet("/BankController")
public class BankController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB private BranchLocal branch;
	@Resource UserTransaction utx;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BankController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String action = request.getParameter("operation");
		int accountNum;
		double amount;
		int secondAccountNum;
		String firstName, lastName, cf;
		String message = "";
		switch(action) {
		case "deposit":
			try {
				accountNum = Integer.parseInt(request.getParameter("account"));
				Account a = branch.getAccount(accountNum);
				if(a==null) throw new AccountNotFoundException("L'account con id inserito [" + accountNum + "] non esiste!");
				amount = Double.parseDouble(request.getParameter("amount"));
				if(amount<0) throw new NegativeAmountException("Hai inserito un importo negativo!");
				branch.deposit(accountNum, amount);
				message = "L'importo [" + amount + "] e' stato accredidato!";
			}catch(AccountNotFoundException anf) {
				message="Problema: " + anf.getMessage();
			}
			catch(NegativeAmountException nae) {
				message="Problema: " + nae.getMessage();
			}
			catch(NumberFormatException e) {
				message = "L'importo inserito non e' del formato corretto!";
			}
			catch(Exception e){
				message = "L'importo non puo' essere accreditato!";
			}
			break;
		case "withdraw":
			try {
				accountNum = Integer.parseInt(request.getParameter("account"));
				Account a = branch.getAccount(accountNum);
				if(a==null) throw new AccountNotFoundException("L'account con id inserito [" + accountNum + "] non esiste!");
				amount = Double.parseDouble(request.getParameter("amount"));
				if(amount<0) throw new NegativeAmountException("Hai inserito un importo negativo!");
				if(a.getBalance()<amount) throw new IllegalTransferException("Il saldo totale del conto [" + a.getAccountId() + "] e' inferiore all'importo da prelevare!");
				branch.withdraw(accountNum, amount);
				message = "L'importo [" + amount + "] e' stato addebitato!";
			}catch(AccountNotFoundException anf) {
				message="Problema: " + anf.getMessage();
			}
			catch(NegativeAmountException nae) {
				message="Problema: " + nae.getMessage();
			}
			catch(NumberFormatException e) {
				message = "Problema: L'importo inserito non e' del formato corretto!";
			}
			catch(IllegalTransferException it) {
				message="Problema: " + it.getMessage();
			}
			catch(Exception e){
				message = "Problema: L'importo non puo' essere addebitato!";
			}
			break;
		case "createAccount":
			try {
				cf = request.getParameter("cf");
				Customer cust = branch.getCustomer(cf);
				if(cust==null) throw new CustomerNotFoundException("Il codice fiscale inserito [" + cf + "] non e' presente nella banca dati!");
				amount = Double.parseDouble(request.getParameter("amount"));
				if(amount<0) throw new NegativeAmountException("Hai inserito un importo negativo!");
				int num = branch.createAccount(cf, amount);
				message = "Il conto e' stato creato ed ha il seguente numero: [" + num + "].";
			}catch(CustomerNotFoundException cnf) {
				message="Problema: " + cnf.getMessage();
			}
			catch(NegativeAmountException nae) {
				message="Problema: " + nae.getMessage();
			}
			catch(NumberFormatException e) {
				message = "Problema: L'importo inserito non e' del formato corretto!";
			}
			catch(Exception e) {
				message = "Problema: Non e' possibile creare il conto!";
			}
			break;
		case "createCust":
			try {
				cf = request.getParameter("cf");
				Customer cust = branch.getCustomer(cf);
				if(cust!=null) throw new CustomerAlreadyExistException("L'utente con codice fiscale [" + cf + "] e' gia' registrato!");
				firstName = request.getParameter("firstName");
				System.out.println(firstName);
				lastName = request.getParameter("lastName");
				branch.createCustomer(cf, firstName, lastName);
				message = "L'utente con codice fiscale [" + cf + "] e' stato registrato nel sistema!";
			}catch(CustomerAlreadyExistException cae) {
				message = "Problema: " + cae.getMessage();
			}
			catch(Exception e) {
				message = "Problema: L'utente non puo' essere registrato";
			}
			break;
		case "allCustomerAccounts":
			try {
				cf = request.getParameter("cf");
				Customer cust = branch.getCustomer(cf);
				if(cust==null) throw new CustomerNotFoundException("Il codice fiscale inserito [" + cf + "] non e' presente nella banca dati!");
				List<Account> accounts = branch.getAccounts(cf);
				if(accounts != null)
					for(Account item: accounts) {
						Account a = item;
						message+="Conto n. [" + a.getAccountId() + "] saldo [" + a.getBalance() + "]\n";
					}
				else throw new AccountNotFoundException("Non e' disponibile nessun conto!");
			}catch(CustomerNotFoundException cnf) {
				message = "Problema: " + cnf.getMessage();
			}
			catch(AccountNotFoundException anf) {
				message = "Problema: " + anf.getMessage();
			}
			break;
		case "transfer":
			try {
				accountNum = Integer.parseInt(request.getParameter("account1"));
				Account a = branch.getAccount(accountNum);
				if(a==null) throw new AccountNotFoundException("L'account con id inserito [" + accountNum + "] non esiste!");
				secondAccountNum = Integer.parseInt(request.getParameter("account2"));
				Account b = branch.getAccount(secondAccountNum);
				if(b==null) throw new AccountNotFoundException("L'account con id inserito [" + secondAccountNum + "] non esiste!");
				if(accountNum==secondAccountNum) throw new IllegalTransferException("I due conti hanno lo stesso id!");
				amount = Double.parseDouble(request.getParameter("amount"));
				if(amount<0) throw new NegativeAmountException("Hai inserito un importo negativo!");
				if(a.getBalance()<amount) throw new IllegalTransferException("Il saldo totale del conto [" + a.getAccountId() + "] e' inferiore all'importo da trasferire!");
				utx.begin();
				branch.withdraw(accountNum, amount);
				branch.deposit(secondAccountNum, amount);
				utx.commit();
				message = "Trasferimento di [" + amount + "] dollari dal conto [" + accountNum + "] al conto [" + secondAccountNum + "] effettuato!";
			}catch(AccountNotFoundException anf) {
				message="Problema: " + anf.getMessage();
			}
			catch(IllegalTransferException it) {
				message="Problema: " + it.getMessage();
			}
			catch(NegativeAmountException nae) {
				message="Problema: " + nae.getMessage();
			}
			catch(NumberFormatException e) {
				message = "Problema: L'importo inserito non e' del formato corretto!";
			}
			catch(Exception e) {
				try {
					utx.rollback();
					message = "Problema: Trasferimento non possibile!";
				}catch(Exception ee) {}
			}
			break;
		default:
			message = "Problema: Operazione non supportata!";
			break;
		}
		response.getWriter().println(message);
	}
}
