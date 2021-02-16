package com.money.api.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Tag;
import com.money.api.config.property.MoneyApiProperty;
import com.money.api.service.exception.S3Exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class S3 {

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private MoneyApiProperty property;

	public String salvarTemporariamente(MultipartFile arquivo) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());

		String nomeunico = gerarNomeUnico(arquivo.getOriginalFilename());

		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(property.getS3().getBucket(), nomeunico,
					arquivo.getInputStream(), objectMetadata).withAccessControlList(acl);
			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(new Tag("expirar", "true"))));

			amazonS3.putObject(putObjectRequest);

			if (log.isDebugEnabled()) {
				log.debug("Arquivo {} enviado com sucesso para o S3.", arquivo.getOriginalFilename());
			}

			return nomeunico;
		} catch (IOException e) {
			throw new S3Exception("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}

	public String configurarUrl(String objeto) {
		return "\\\\" + property.getS3().getBucket() + ".s3.amazonaws.com/" + objeto;
	}

	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}

}
