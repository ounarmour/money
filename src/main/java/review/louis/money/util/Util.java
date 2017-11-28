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
			// each line: "10/31/2017","-6.55","*","","PANDA EXPRESS #2412 FORT COLLINS CO"
			String line = s.nextLine();
			ln++;
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
			Transaction tx = new Transaction(date, amount, description, file.getName().replace(".csv", ""));
			list.add(tx);
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
			txDoc.append("account", tx.getAccount());
			txDocList.add(txDoc);
		}
		// Store it in MongoDB
		collTransactions.insertMany(txDocList);
  }





}
