package com.kalman03.gateway.doc.domain;

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
	 * OSS 访问域名
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
	 * 角色ARN全局唯一，用来指定具体的RAM角色 ARN遵循阿里云的命名规范，格式为：acs:ram::$accountID:role/$roleName
	 */
	private String stsRoleArn;
	/**
	 * CDN的域名(若有专属cdn域名的话)，否则直接去endpoint对应的公网地址
	 */
	private String cdnBaseUrl;

	public String getHost() {
		return "https://" + getBucket() + "." + getEndpoint();
	}

}
