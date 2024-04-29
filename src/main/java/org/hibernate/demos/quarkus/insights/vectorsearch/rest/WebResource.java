package org.hibernate.demos.quarkus.insights.vectorsearch.rest;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.enterprise.event.Observes;

public class WebResource {
	public void init(@Observes Router router) {
//		router.route()
//				.path("/*")
//				.handler( StaticHandler.create("web/"));
	}
}
