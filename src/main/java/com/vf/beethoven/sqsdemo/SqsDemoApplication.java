package com.vf.beethoven.sqsdemo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSns;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Log4j2
@EnableSns
@RestController
@SpringBootApplication
public class SqsDemoApplication {

	@Autowired
	ObjectMapper mapper;

	@Value("${sns.topic.arn}")
	private String snsTopicARN;

	@Value("${aws.accessKey}")
	private String awsAccessKey;

	@Value("${aws.secretKey}")
	private String awsSecretKey;

	@Value("${aws.region}")
	private String awsRegion;

	@PostConstruct
	private void postConstructor() {
		log.info("SQS URL: " + snsTopicARN);
		AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(awsAccessKey, awsSecretKey)
		);
		this.amazonSNS = AmazonSNSClientBuilder.standard()
				//.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsEndpointUrl, awsRegion))
				.withRegion(awsRegion)
				.withCredentials(awsCredentialsProvider)
				.build();
	}

	private AmazonSNS amazonSNS;

	public static void main(String[] args) {
		SpringApplication.run(SqsDemoApplication.class, args);
	}

	@PostMapping("/products")
	public ResponseEntity<String> create(@RequestBody Product product) throws JsonProcessingException {
		publishSNSMessage(mapper.writeValueAsString(product));
		return ResponseEntity.ok("success");
	}

	public void publishSNSMessage(String message) {
		log.info("Publishing SNS message: " + message);
		PublishRequest request = new PublishRequest();
		request.setMessage(message);
		//request.setTargetArn(targetARN);
		request.setTopicArn(snsTopicARN);
		PublishResult result = this.amazonSNS.publish(request);
		log.info("SNS Message ID: " + result.getMessageId());
	}


}
