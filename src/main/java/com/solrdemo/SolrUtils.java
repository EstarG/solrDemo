/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved/
 */
package com.solrdemo;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import java.net.MalformedURLException;

/**
 *
 * @author baoxing.gbx
 * @version $Id:SolrUtils.java, V 0.1 2015-11-21 14:37 baoxing.gbx Exp $$
 */
public class SolrUtils {


	private static final String DEFAULT_URL = "http://localhost:8080/solr/";


	private static  CommonsHttpSolrServer httpServer;

	static {
		try {
			httpServer = new CommonsHttpSolrServer(DEFAULT_URL);
		} catch (MalformedURLException e) {
			System.out.println("初始化报错");
		}
	}


	public static CommonsHttpSolrServer getInstance () {
			return  httpServer;
	}

}