package web;

import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;

import org.junit.Test;

public class MainVerticleTest extends AbstractIntegrationTest {

	@Test
	public void getMessages(TestContext context) {
		vertx.createHttpClient().getNow(8080, "localhost", "/api/messages", response -> {
			response.bodyHandler(body -> {
				context.assertTrue(body.toString().contains("foo"), body.toString());
				context.async().complete();
			});
		});
	}

	@Test
	public void postMessage(TestContext context) {
		final String json = Json.encodePrettily(new Message(2L, "quuux"));
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(8080, "localhost", "/api/messages").putHeader("content-type", "application/json")
				.putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 201);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						final Message message = Json.decodeValue(body.toString(), Message.class);
						context.assertEquals(message.getId(), 23L);
						context.assertEquals(message.getContent(), "quuux");
						context.async().complete();
					});
				}).write(json).end();
	}

}
