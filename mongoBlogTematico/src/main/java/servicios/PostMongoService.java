package servicios;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

public class PostMongoService {

	private String uri;
	private static final String COLLECTION_NAME = "posts";
	private static final String DATABASE = "blogDB";

	public PostMongoService(String uri) {
		this.uri = uri;
	}

	public String findLatestPosts() {
		// https://www.mongodb.com/docs/drivers/java/sync/current/usage-examples/findOne/
		// https://www.mongodb.com/docs/manual/reference/method/db.collection.find/
		String resultado = null;
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			MongoDatabase database = mongoClient.getDatabase(DATABASE);
			MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

			Bson projectionFields = Projections.fields(Projections.include("_id", "title", "resume"));

			FindIterable<Document> d = collection.find().projection(projectionFields).limit(4)
					.sort(Sorts.descending("date"));

			resultado = StreamSupport.stream(d.spliterator(), false).map(Document::toJson)
					.collect(Collectors.joining(", ", "[", "]"));

		}
		return resultado;
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

	public String numberPostsByAuthor() {
		// https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/aggregation/
		// https://www.mongodb.com/docs/drivers/java/sync/v4.3/fundamentals/crud/read-operations/sort/
		String resultado = null;
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			MongoDatabase database = mongoClient.getDatabase(DATABASE);
			MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

			AggregateIterable<Document> d = collection
					.aggregate(Arrays.asList(Aggregates.group("$author", Accumulators.sum("count", 1)),
							Aggregates.sort(Sorts.descending("count"))));

			resultado = StreamSupport.stream(d.spliterator(), false).map(Document::toJson)
					.collect(Collectors.joining(", ", "[", "]"));
		}
		return resultado;
	}

	public String postsByAuthor(String autor) {
		String resultado = null;
		try (MongoClient mongoClient = MongoClients.create(uri)) {

			MongoDatabase database = mongoClient.getDatabase(DATABASE);

			MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

			FindIterable<Document> d = collection.find(Filters.eq("author", autor));

			resultado = StreamSupport.stream(d.spliterator(), false).map(Document::toJson)
					.collect(Collectors.joining(", ", "[", "]"));
		}
		return resultado;
	}

	public String search(String textSearch) {
		String resultado = null;
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			MongoDatabase database = mongoClient.getDatabase(DATABASE);
			MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

			Bson projectionFields = Projections.fields(Projections.include("_id", "title", "resume", "author", "date"));

			// collection.createIndex(Indexes.text("text"));
			FindIterable<Document> d = collection.find(Filters.text(textSearch)).projection(projectionFields);

			resultado = StreamSupport.stream(d.spliterator(), false).map(Document::toJson)
					.collect(Collectors.joining(", ", "[", "]"));
		}
		return resultado;
	}

}
