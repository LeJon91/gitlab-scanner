package main.java;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GitlabScannerApplication implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(GitlabScannerApplication.class, args);
	}
	
	@Override
	public void run(String... arg0) throws Exception {
	}
}
