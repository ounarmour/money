package review.louis.money.util;

import com.google.gson.*;
import java.util.*;
import java.lang.reflect.Type;
import review.louis.money.Transaction;
import java.text.SimpleDateFormat;

public class Record {

  private Gson gson;

  public Record() {
    GsonBuilder gsb = new GsonBuilder();
    gsb.registerTypeAdapter(Transaction.class, new TransactionSerializer());
    gsb.registerTypeAdapter(Transaction.class, new TransactionDeserializer());
    gson = gsb.create();
  }

  public String jsonify(Transaction jt) {
      return gson.toJson(jt);
  }

  public Transaction deJsonify(String jsont) {
      return gson.fromJson(jsont, Transaction.class);
  }

  class TransactionSerializer implements JsonSerializer<Transaction> {
    @Override
    public JsonElement serialize(Transaction tx, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonObject obj = new JsonObject();
      obj.addProperty("date", (new SimpleDateFormat("MM/dd/yyyy")).format(tx.getDate()));
      obj.addProperty("amount", tx.getAmount());
      obj.addProperty("description", tx.getDescription());
      obj.addProperty("account", tx.getAccount());
      return obj;
    }
  }

  class TransactionDeserializer implements JsonDeserializer<Transaction> {
    @Override
    public Transaction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject job = jsonElement.getAsJsonObject();

      Date date = null;
			try{
				date = (new SimpleDateFormat("MM/dd/yyyy")).parse(job.get("date").getAsString());
			} catch (Exception e) {
				System.out.println("ERROR: Failed to parse date during deserialization. [JsonObject: " + job.toString() + "]");
				System.exit(-1);
			}
      Transaction tx = new Transaction(date, job.get("amount").getAsDouble(), job.get("description").getAsString(), job.get("account").getAsString());
      return tx;
    }
  }



}
