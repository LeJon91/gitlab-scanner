package main.java;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import analysis.GitlabAnalysis;
import gitlabScanner.TeamStructure;

@RestController
public class GitlabController {

	@Value("${private_token}")
	String privateToken;
	
	@RequestMapping(method = RequestMethod.PUT, path = "/team")
	public ResponseEntity<String> writeTeamStructureToNEO4J(@RequestParam(required = true) String repoURI) {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		TeamStructure structure = getTeamStructureFromEncodedURI(repoURI);
		structure.serializeToNEO4J();
		return response;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/team")
	public ResponseEntity<TeamStructure> getTeamStructure(@RequestParam(required = true) String repoURI) {
		ResponseEntity<TeamStructure> response = null;
		TeamStructure structure = getTeamStructureFromEncodedURI(repoURI);
		if(structure == null)
			response = new ResponseEntity<TeamStructure>(HttpStatus.INTERNAL_SERVER_ERROR);
		else
			response = new ResponseEntity<TeamStructure>(structure, HttpStatus.OK);
		return response;
	}
	
	private TeamStructure getTeamStructureFromEncodedURI(String repoURI) {
		String baseURI = "";
		String decodedRepoURI = "";
		
		try {
			decodedRepoURI = new String(Base64.decodeBase64(URLDecoder.decode(repoURI, "UTF-8").getBytes()));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		
		Pattern pattern = Pattern.compile("(http|https)://.[^/]*/");
		Matcher matcher = pattern.matcher(decodedRepoURI);
		if(matcher.find()) {
			baseURI = decodedRepoURI.substring(matcher.start(),matcher.end());
		}

		GitlabAnalysis analysis = new GitlabAnalysis(privateToken, baseURI);
		return analysis.getTeamStructure(decodedRepoURI);
	}
}
