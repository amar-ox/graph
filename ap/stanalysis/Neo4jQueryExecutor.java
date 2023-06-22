package stanalysis;

import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Neo4jQueryExecutor {

    public static void main(String[] args) {
        String uri = "bolt://localhost:7687"; // Replace with your Neo4j server URI
        String username = "neo4j"; // Replace with your Neo4j username
        String password = "12345"; // Replace with your Neo4j password

        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))) {
        	// createGraph(driver);
        	
        Session session = driver.session(SessionConfig.forDatabase("forwarding2"));
        	long start = System.nanoTime();
        	Result result = session.run("MATCH (r:Router)-[:HAS_PORT]->(p:Port)-[:LINKS_TO]-()\r\n"
        			+ "WITH DISTINCT r,p\r\n"
        			+ "WITH collect(r.name+','+p.name) as ports\r\n"
        			+ "CALL trees.compute(ports, false) YIELD time\r\n"
        			+ "RETURN time");
        	long end = System.nanoTime();
        	System.out.println("Done after " + (end - start)/1000000.0 + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createGraph(Driver driver) {
    	String queriesFilePath = "network.cypher";

        // Read queries from file
        String queries = "";
		try {
			queries = readQueriesFromFile(queriesFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Split queries by semicolon
        String[] queryArray = queries.split(";");

        // Execute queries
        try (Session session = driver.session(SessionConfig.forDatabase("forwarding2"))) {
        	int i = 1;
            for (String query : queryArray) {
                query = query.trim();

                if (!query.isEmpty()) {
                    Result result = session.run(query);
                    if (result.consume().counters().nodesCreated() > 0 || result.consume().counters().relationshipsCreated() > 0) {
                        System.out.println("["+i+"/"+queryArray.length+"] executed: " + query);
                    } else {
                    	System.out.println("No change: " + query);
                    }
                    i++;
                    // Process the result as per your requirements
                    // You can retrieve the returned data or check for errors
                }
            }
        }
    }

    private static String readQueriesFromFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
