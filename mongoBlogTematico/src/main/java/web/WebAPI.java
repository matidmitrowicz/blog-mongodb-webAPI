package web;

import java.util.Map;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import servicios.PageMongoService;
import servicios.PostMongoService;

public class WebAPI {

	private PageMongoService pages;
	private PostMongoService posts;
	private int webPort;

	public WebAPI(PageMongoService pages, PostMongoService posts, int webPort) {
		this.pages = pages;
		this.posts = posts;
		this.webPort = webPort;
	}

	public void start() {
		Javalin app = Javalin.create(config -> {
			config.enableCorsForAllOrigins();
		}).start(this.webPort);

		app.get("/pages/{id}", findPage());
		app.get("/posts/latest", findLatestPosts());
		app.get("/posts/{id}", findPost());
		app.get("/byauthor", numberPostsByAuthor());
		app.get("/posts/author/{author}", postsByAuthor());
		app.get("/search/{text}", searchPostByText());

		app.exception(Exception.class, (e, ctx) -> {
			ctx.json(Map.of("result", "error", "message", "Ups... algo se rompi�.: " + e.getMessage()));
		});

	}

	private Handler findPage() {
		return ctx -> {
			String page = this.pages.findByID(ctx.pathParam("id"));

			ctx.json(page);
		};
	}

	private Handler findLatestPosts() {
		return ctx -> {
			String latestPosts = this.posts.findLatestPosts();

			ctx.json(latestPosts);
		};
	}

	private Handler findPost() {
		return ctx -> {
			String post = this.posts.findByID(ctx.pathParam("id"));

			ctx.json(post);
		};
	}

	private Handler numberPostsByAuthor() {
		return ctx -> {
			String byAuthor = this.posts.numberPostsByAuthor();

			ctx.json(byAuthor);
		};
	}

	private Handler postsByAuthor() {
		return ctx -> {
			String postsByAuthor = this.posts.postsByAuthor(ctx.pathParam("author"));

			ctx.json(postsByAuthor);
		};
	}

	private Handler searchPostByText() {
		return ctx -> {
			String postsByText = this.posts.search(ctx.pathParam("text"));

			ctx.json(postsByText);
		};
	}

}
