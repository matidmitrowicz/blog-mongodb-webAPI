package servicios;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class PageMongoService {

	private String uri;
	private static final String COLLECTION_NAME = "pages";
	private static final String DATABASE = "blogDB";

	public PageMongoService(String uri) {
		this.uri = uri;
	}

	public String findByID(String id) {
		String resultado = null;
		try (MongoClient mongoClient = MongoClients.create(uri)) {

			MongoDatabase database = mongoClient.getDatabase(DATABASE);

			MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

			FindIterable<Document> d = collection.find(Filters.eq("_id", new ObjectId(id)));

			resultado = StreamSupport.stream(d.spliterator(), false).map(Document::toJson)
					.collect(Collectors.joining(", ", "[", "]"));
		}
		return resultado;
	}

}
