package review.louis.money.util;

import review.louis.money.Transaction;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;


public class Util {

  public static ArrayList<Transaction> loadTransactions(File file) {
		Scanner s = null;
		try {
			s = new Scanner(file);
		} catch (Exception e) {
			System.out.println("ERROR: Failed to create scanner from file.");
			System.exit(-1);
		}

		ArrayList<Transaction> list = new ArrayList<>();
		int ln = 0;
		while (s.hasNextLine()) {
			String line = s.nextLine();  ln++;
      String fileName = file.getName().replace(".csv", "");
      String bankName = fileName.substring(0,2); // "wf" or "us"

      if ("wf".equals(bankName)) {
        // WELLS FARGO
        // line: "10/31/2017|-6.55|*||PANDA EXPRESS #2412 FORT COLLINS CO"
        if ("".equals(line)) {
          // we've hit the end of the data.  There may be some blank lines.
          break;
        }
        line = line.replace("\",\"", "\"|\"");
        line = line.replace("\"", "");
        // TODO (future) - validate line format before splitting into String[]

  			String[] vals = line.split("\\|");
  			// vals[0] = "10/31/2017"
  			// vals[1] = "-6.55"
  			// vals[4] = "PANDA EXPRESS #2412 FORT COLLINS CO"
  			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
  			Date date = null;
        try{
  				date = df.parse(vals[0]);
  			} catch (Exception e) {
  				System.out.println("ERROR: Failed to parse date at " + file.getName() + ":" + ln + " [\"" + vals[0] + "\"]");
  				System.exit(-1);
  			}
  			Double amount = Double.parseDouble(vals[1]);
  			String description = vals[4];
  			Transaction tx = new Transaction(date, amount, description, fileName);
  			list.add(tx);
      } else if ("us".equals(bankName)) {
        // USAA
        // line: "posted,,09/22/2017,,COMCAST          CABLE      ***********0270,Cable/Satellite Services,-60.74"
        if ("".equals(line)) {
          // we've hit the end of the data.  There may be some blank lines.
          break;
        }
        String[] vals = line.split(",");
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try{
  				date = df.parse(vals[2]);
  			} catch (Exception e) {
  				System.out.println("ERROR: Failed to parse date at " + file.getName() + ":" + ln + " [\"" + vals[0] + "\"]");
  				System.exit(-1);
  			}

        // If amount is positive X then USAA exports this as --X so we need to check if there are two "-" chars and remove them if there are
        if ("--".equals(vals[6].substring(0,2))) {
          vals[6] = vals[6].replace("--","");
        }
        Double amount = Double.parseDouble(vals[6]);
        String description = vals[4];
        Transaction tx = new Transaction(date, amount, description, fileName);
        list.add(tx);
      }

		}
		return list;
	}

  public static void storeTransactionsToMongoDB(ArrayList<Transaction> txList, MongoCollection collTransactions) {
    ArrayList<Document> txDocList = new ArrayList<>();

		for (Transaction tx : txList) {
			Document txDoc = new Document();
			txDoc.append("date", (new SimpleDateFormat("MM/dd/yyyy")).format(tx.getDate()));
			txDoc.append("amount", tx.getAmount());
			txDoc.append("description", tx.getDescription());
			txDoc.append("accountNumber", tx.getAccountNumber());
      txDoc.append("bankName", tx.getBankName());
			txDocList.add(txDoc);
		}
		// Store it in MongoDB
		collTransactions.insertMany(txDocList);
  }





}
