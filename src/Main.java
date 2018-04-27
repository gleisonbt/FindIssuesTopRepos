import java.io.FileWriter;
import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.opencsv.CSVWriter;

public class Main {

	public static void main(String[] args) throws IOException {
		Github github = new RtGithub("gleisonbt", "Aleister93");
		
		Request request;
		JsonArray items;
		int page = 1;
		int cont = 1;
		
		//CSVWriter writer = new CSVWriter(new FileWriter("/home/gleison/monorepos/Java/topJava_5.csv"));
		
		
		do {
			request = github.entry().uri().path("/search/repositories")
					.queryParam("q", "stars:>100 archived:false")
					//.queryParam("q", "user:stoicflame repo:enunciate language:Java")
					.queryParam("sort", "stars")
					.queryParam("per_page", "100")
					.queryParam("page", "" + page)
					.queryParam("order", "desc")
					//.queryParam("stars", ">=4000")
					.back().method(Request.GET);
			
			//System.out.println(request.uri().toString());
			items = request.fetch().as(JsonResponse.class).json().readObject().getJsonArray("items");
			
			for (JsonValue item : items) {
				JsonObject repoData = (JsonObject) item;
				
				
				String line = repoData.getString("full_name") + ","
						+ repoData.getString("git_url") + ","
						+ repoData.getInt("stargazers_count");// + "," //stars
						
				//writer.writeNext(line.split(","));
				
				System.out.println(cont++ + "" + "\t" + line);
				
				JsonArray items2;
				int page2 = 1;
				do {
					Request request2 = github.entry().uri().path("/repos/" + repoData.getString("full_name") + "/issues")
							.queryParam("page", "" + page2)
							.queryParam("order", "desc")
							//.queryParam("stars", ">=4000")
							.back().method(Request.GET);
					
					items2 = request2.fetch().as(JsonResponse.class).json().readArray();
					int cont2 = 0;
					for (JsonValue item2 : items2) {
						JsonObject issueData = (JsonObject) item2;
						System.out.println(cont2++ + " - " + issueData.getInt("id"));
						System.out.println(issueData.getString("body"));
					}
					//System.out.println(request2.uri().toString());
					//JsonObject issueData = (JsonObject) items2;
					//System.out.println(issueData.getString("body"));
					
					page2++;
				} while (items2.size() == 30);
				
				
		
			}
			
			page++;
			
			if (page > 1) {
				break;
			}
			
		} while (items.size() == 100);

	}

}
