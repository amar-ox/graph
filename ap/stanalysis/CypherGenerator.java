package stanalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class CypherGenerator {
	
	public static void writeFile(String name, String line, boolean append) {
		try {
            FileWriter writer = new FileWriter(name, append);
            writer.write(line);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
	}

    public static void main(String[] args) {}
    
    public static void generatePPM(File inputFile, HashSet<Integer> universe) throws IOException {
		Scanner OneLine = null;
		try {
			OneLine = new Scanner (inputFile);
			OneLine.useDelimiter("\n");
		} catch (FileNotFoundException e) {
			System.out.println ("File not found!"); // for debugging
			System.exit (0); // Stop program if no file found
		}
		
		for(int ap : universe) {
			CypherGenerator.writeFile("network.cypher", String.format("CREATE (ec:EC {v: %s});\n", ap), true);
		}
		
		// CypherGenerator.writeFile("ppm.cypher", "", false);
		while(OneLine.hasNext()) {
			String linestr = OneLine.next();
			String[] tokens = linestr.split("\t");
			
			String ecs = tokens[3];
			String comp = " SET x.complement = false";
			if (ecs.endsWith("complement")) {
	            // Remove "complement" along with the preceding space
	            ecs = ecs.substring(0, ecs.length() - 11);
	            comp = " SET x.complement = true";
	        } 

			if(tokens[0].equals( "fw")) {
				CypherGenerator.writeFile("network.cypher", String.format(
						"MATCH (r:Router {name: '%s'})-[:HAS_PORT|HAS_VLAN]->(i {name: '%s'}) "
					  + "CREATE (i)-[:FWD]->(x:Fwd)" +comp+ " WITH x "
					  + "MATCH (ec:EC) WHERE ec.v IN %s "
					  + "CREATE (x)-[:HAS_EC]->(ec);\n", tokens[1], tokens[2], ecs), true);
				//	  + "CREATE (i)-[:FWD]->(x:Fwd {ecs: %s})" +comp+ ";\n", tokens[1], tokens[2], ecs), true);
			} else if(tokens[0].equals("inacl")) {
				CypherGenerator.writeFile("network.cypher", String.format(
						"MATCH (r:Router {name: '%s'})-[:HAS_PORT|HAS_VLAN]->(i {name: '%s'}) "
					  + "CREATE (i)-[:INACL]->(x:InAcl)" +comp+ " WITH x "
					  + "MATCH (ec:EC) WHERE ec.v IN %s "
					  + "CREATE (x)-[:HAS_EC]->(ec)" +comp+ ";\n", tokens[1], tokens[2], ecs), true);
				//	  + "CREATE (i)-[:INACL]->(x:InAcl {ecs: %s})" +comp+ ";\n", tokens[1], tokens[2], ecs), true);
			} else if(tokens[0].equals("outacl")) {
				CypherGenerator.writeFile("network.cypher", String.format(
						"MATCH (r:Router {name: '%s'})-[:HAS_PORT|HAS_VLAN]->(i {name: '%s'}) "
				      + "CREATE (i)-[:OUTACL]->(x:OutAcl)" +comp+ " WITH x "
					  + "MATCH (ec:EC) WHERE ec.v IN %s "
					  + "CREATE (x)-[:HAS_EC]->(ec)" +comp+ ";\n", tokens[1], tokens[2], ecs), true);
				//	  + "CREATE (i)-[:OUTACL]->(x:OutAcl {ecs: %s})" +comp+ ";\n", tokens[1], tokens[2], ecs), true);
			}
		}
	}
}
