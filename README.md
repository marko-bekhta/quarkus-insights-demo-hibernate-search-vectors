# hibernate-search-vectors

This demo application provides fulltext search capabilities to search over the book database 
and allows to find similar books using vector search.

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the demo 

Before starting the application make sure that the AI service that provides image cover embeddings is up and running.
1. Build the [`bentoml.Dockerfile`](image-service%2Fbentoml.Dockerfile)
```bash
docker build -t bento-ml -f image-service/bentoml.Dockerfile image-service
```
2. Run the service with cache directory mounted (to prevent re-downloads between runs)
```bash
docker run --rm -t -i -v /path/to/your/local/cache/dir/to/use:/home/dev/.cache -u $UID:$GID -p 3000:3000 bento-ml:latest
```
3. Start the Quarkus application, e.g. in running the application in dev mode from a command line:
```bash
./mvnw compile quarkus:dev
```
4. Once started and this is an initial run call the import endpoint. 
It will take books listed in [books.txt](data%2Fbooks.txt) and scrape the data into the DB 
while populating the search index at the same time.

```http request
GET localhost:8080/api/import
```
5. Open the application UI by going to the http://localhost:8080

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- Hibernate Search + Elasticsearch ([guide](https://quarkus.io/guides/hibernate-search-orm-elasticsearch)): Automatically index your Hibernate entities in Elasticsearch
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- Hibernate Search 7.1.1.Final: [Reference Documentation](https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/)
