package com.example.pay.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.pay.domain.MemberEntity;
import com.example.pay.domain.SignUpStats;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SignUpStatsJobConfig {

	private final JobRepository jobRepository;
	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;

	@Bean(name = "signUpStatsJob")
	public Job signUpStatsJob() {
		return new JobBuilder("signUpStatsJob", jobRepository)
			.start(signUpStatsStep())
			.build();
	}

	@Bean
	public Step signUpStatsStep() {
		return new StepBuilder("signUpStatsStep", jobRepository)
			.<MemberEntity, SignUpStats>chunk(100, transactionManager)
			.reader(memberReader())
			.processor(statsProcessor())
			.writer(statsWriter())
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<MemberEntity> memberReader() {
		return new JpaPagingItemReaderBuilder<MemberEntity>()
			.name("memberReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)  // 한 번에 100개씩 조회
			.queryString("SELECT m FROM MemberEntity m")
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<MemberEntity, SignUpStats> statsProcessor() {
		return member -> SignUpStats.builder()
			.signupHour(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
			.signupCount(1)
			.signupDate(LocalDate.now())
			.build();
	}

	@Bean
	@StepScope
	public JpaItemWriter<SignUpStats> statsWriter() {
		JpaItemWriter<SignUpStats> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		return writer;
	}

}

