package com.kalman03.gateway.doc.service;

import java.io.IOException;

import com.kalman03.gateway.doc.domain.DocumentObject;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public interface DocumentService {

	DocumentObject getDocumentObject()throws IOException;
}
