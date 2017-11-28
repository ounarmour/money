package review.louis.money;

import review.louis.money.util.Util;
import review.louis.money.util.Record;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Base64;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {

		// Parse arguments
		ArgumentParser parser = ArgumentParsers.newArgumentParser("Money")
			.defaultHelp(true)
			.version("0.1")
			.description("Personal financial analysis");
		parser.addArgument("-f", "--filePath")
			.setDefault("tx-source.csv")
			.help("Path to csv file of transaction data.");
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(-1);
		}
		String filePath = ns.getString("filePath");
		File txFile = new File(filePath);


		// Open MongoDB and set database and collection
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("money");
		MongoCollection<Document> collTransactions = db.getCollection("transactions");
		MongoCollection<Document> collAccounts = db.getCollection("accounts");

		ArrayList<Transaction> txList = Util.loadTransactions(txFile);
		Util.storeTransactionsToMongoDB(txList, collTransactions);

	}



}
