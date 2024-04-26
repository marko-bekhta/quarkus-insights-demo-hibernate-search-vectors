package org.hibernate.demos.quarkus.insights.vectorsearch.service;

import java.net.URI;
import java.nio.file.Path;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.insights.vectorsearch")
public interface ApplicationConfiguration {

	Path imagesRootPath();

}
