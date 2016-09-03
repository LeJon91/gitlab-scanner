package analysis;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabGroupMember;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;

import gitlabScanner.TeamStructure;

public class GitlabAnalysis {

	private GitlabAPI api;
	
	public GitlabAnalysis(String privateToken, String hostURL) {
		this.api = GitlabAPI.connect(hostURL, privateToken);
	}
	
	public GitlabProject getGitlabProject(String projectName) {
		GitlabProject searchedProject = null;
		List<GitlabProject> projects;
		try {
			projects = api.getProjects();
		} catch (IOException e) {
			e.printStackTrace();
			return searchedProject;
		}
		
		for(GitlabProject project:projects) {
			if(project.getName().equals(projectName)) {
				searchedProject = project;
				break;
			}
		}
		
		return searchedProject;
	}
	
	public GitlabGroup getGroup(String groupName) {
		GitlabGroup searchedGroup = null;
		List<GitlabGroup> groups = null;
		
		try {
			groups = api.getGroups();
		} catch (IOException e) {
			e.printStackTrace();
			return searchedGroup;
		}
		
		for(GitlabGroup group:groups) {
			if(group.getName().equals(groupName)) {
				searchedGroup = group;
				break;
			}
		}
		
		return searchedGroup;
	}
	
	public GitlabUser getUser(String userName) {
		GitlabUser searchedUser = null;
		List<GitlabUser> users = null;
		
		try {
			users = api.getUsers();
		} catch (IOException e) {
			e.printStackTrace();
			return searchedUser;
		}
		
		for(GitlabUser user:users) {
			if(user.getUsername().equals(userName)) {
				searchedUser = user;
				break;
			}
		}
		
		return searchedUser;
	}
	
	public List<GitlabGroupMember> getGroupMembers(GitlabGroup group) {
		List<GitlabGroupMember> membersToReturn = null;
		try {
			membersToReturn = api.getGroupMembers(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return membersToReturn;
	}
	
	public TeamStructure getTeamStructure(String repoURI) {
		TeamStructure structure = new TeamStructure(repoURI);
		String reversedURI = reverseString(repoURI);
		Pattern pattern = Pattern.compile("/.[^/]*/");
		Matcher matcher = pattern.matcher(reversedURI);
		String teamName = "";
		
		//to find the last group in the string
		if(matcher.find()) {
			teamName = reverseString(reversedURI.substring(matcher.start()+1,matcher.end()-1));
		}
		GitlabGroup teamGroup = this.getGroup(teamName);
		if(teamGroup != null) {
			structure.setTeamName(teamName);
			
			List<GitlabGroupMember> members = null;
			try {
				members = api.getGroupMembers(teamGroup);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(members!=null) {
				for(GitlabGroupMember member:members) {
					structure.addTeamMemberName(member.getName());
				}
			}
		} else {
			GitlabUser user = this.getUser(teamName);
			if(user != null) {
				structure.addTeamMemberName(user.getName());
			}
		}
		return structure;
	}
	
	private String reverseString(String stringToReverse) {
		char[] reversedURICharArr = new char[stringToReverse.length()];
		for(int i = 0;i<stringToReverse.length();i++) {
			reversedURICharArr[i] = stringToReverse.charAt(stringToReverse.length()-1-i);
		}
		
		return new String(reversedURICharArr);
	}
}
