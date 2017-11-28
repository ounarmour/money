package review.louis.money;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Transaction {

  private Date date;
  private Double amount;
  private String description;
  private String account;
  private String bankName;
  private String accountNumber;

  public Transaction(Date date, Double amount, String description, String account) {
    this.date = date;
    this.amount = amount;
    this.description = description;
    this.account = account;  // wf_12340000001020

    String[] arr = account.split("\\_");
    this.accountNumber = arr[1]; // 12340000001020
    if ("wf".equals(arr[0])) {
      this.bankName = "Wells Fargo";
    } else if ("us".equals(arr[0])) {
      this.bankName = "USAA";
    } else {
      this.bankName = "Unknown";
    }

  }

  public Date getDate() {
    return date;
  }

  public Double getAmount() {
    return amount;
  }

  public String getDescription() {
    return description;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public String getBankName() {
    return bankName;
  }

  public String toString() {
    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    return account + " (" + amount + ") - " + df.format(date) + " - " + description;
  }

  public boolean equals(Object o) {
    if (o instanceof Transaction) {
      Transaction t = (Transaction) o;

      if (!this.date.equals(t.getDate())) {
        return false;
      } else if (!this.amount.equals(t.getAmount())) {
        return false;
      } else if (!this.description.equals(t.getDescription())) {
        return false;
      } else {
        return true;
      }

    } else {
      return false;
    }
  }

}
