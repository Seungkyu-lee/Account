package com.example.pay.config;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.pay.exception.BatchJobException;
import com.example.pay.type.ErrorCode;

@Configuration
@EnableScheduling
public class SchedulingConfig {

	private final JobLauncher jobLauncher;
	private final Job signUpStatsJob;

	public SchedulingConfig(
		JobLauncher jobLauncher,
		@Qualifier("signUpStatsJob") Job signUpStatsJob) {
		this.jobLauncher = jobLauncher;
		this.signUpStatsJob = signUpStatsJob;
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void runJob() {
		try {
			JobParameters parameters = new JobParametersBuilder()
				.addString("datetime", LocalDateTime.now().toString())
				.toJobParameters();

			jobLauncher.run(signUpStatsJob, parameters);
		} catch (Exception e) {
			throw new BatchJobException(
				ErrorCode.JOB_EXECUTION_ERROR,
				"Failed to execute sign up stats job: " + e.getMessage()
			);
		}
	}
}




