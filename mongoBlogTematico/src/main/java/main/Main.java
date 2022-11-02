package main;

import servicios.PageMongoService;
import servicios.PostMongoService;
import web.WebAPI;

public class Main {

	public static void main(String[] args) {

		String mongoURI = "mongodb://localhost:27017";

		PageMongoService pages = new PageMongoService(mongoURI);
		PostMongoService posts = new PostMongoService(mongoURI);

		WebAPI webApi = new WebAPI(pages, posts, 4567);

		webApi.start();

	}

}
