package com.kalman03.gateway.doc.domain;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
public class AliyunOssProperties {
	/**
	 * Oss bucket
	 */
	private String bucket;
	/**
	 * 文件上传在bucket下的目录
	 */
	private String bucketDir;
	/**
	 * OSS 访问域名(公网)
	 */
	private String endpoint;
	/**
	 * 地域或者数据中心
	 */
	private String regionId;
	/**
	 * 访问密钥
	 */
	private String accessKeyId;
	/**
	 * 访问密钥
	 */
	private String accessKeySecret;
	/**
	 * CDN的域名(若有专属cdn域名的话)，否则直接去endpoint对应的公网地址
	 */
	private String cdnBaseUrl;

	public String getHost() {
		if(isNotBlank(cdnBaseUrl)) {
			return cdnBaseUrl;
		}
		return "https://" + getBucket() + "." + getEndpoint();
	}

}
