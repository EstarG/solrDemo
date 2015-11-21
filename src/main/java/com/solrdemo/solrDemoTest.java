/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2015 All Rights Reserved/
 */
package com.solrdemo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author baoxing.gbx
 * @version $Id:solrDemoTest.java, V 0.1 2015-11-21 14:37 baoxing.gbx Exp $$
 */
public class solrDemoTest {

    /**
     * 测试添加索引   可以基于注解将某个javaBean作为文档信息
     */
    @Test
    public void testAddIndex() {

        try {

            //创建doc文档
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", 1);
            doc.addField("test_name", "我的solr测试");
            doc.addField("test_content", "一定要成功啊");

            //创建doc文档
            SolrInputDocument doc2 = new SolrInputDocument();
            doc2.addField("id", 2);
            doc2.addField("test_name", "测试走起");
            doc2.addField("test_content", "已经成功了");

            List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
            solrInputDocuments.add(doc);
            solrInputDocuments.add(doc2);

            CommonsHttpSolrServer server = SolrUtils.getInstance();
            //添加一个doc文档
            UpdateResponse response = server.add(solrInputDocuments);

            server.commit();

            query("成功");

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }  finally {
        }


    }

    /**
     * 删除索引
     */
    @Test
    public void testDeleteIndex() {
        try {

            System.out.println("fefe");

            CommonsHttpSolrServer server = SolrUtils.getInstance();

            server.deleteById(Arrays.asList(new String[] { "1", "2" }));

            //创建doc文档
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", 1);
            doc.addField("test_name", "我的solr测试, 被修改的");
            doc.addField("test_content", "一定要成功啊");
            server.add(doc);
            server.commit();

            query("成功");

        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    /**
     * 更新索引
     *
     */
    @Test
    public void testUpdateIndex() {
        try {

            System.out.println("fefe");

            CommonsHttpSolrServer server = SolrUtils.getInstance();


            server.deleteById(Arrays.asList(new String[] { "1" }));

            server.commit();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }
    }

    /**
     * 查询结果 高亮+分页
     *
     * 改查询几月solr/home 修改了schema。xml
     *
     *
     * 配置文件如下：
     * 1. 加入mmseg中文分词器 jar 和配置
     * <!--自定义中文分词 -->
         <fieldType name="textComplex" class="solr.TextField" >
         <analyzer>
         <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="complex" dicPath="dic"/>
         </analyzer>
         </fieldType>
         <fieldType name="textMaxWord" class="solr.TextField" >
         <analyzer>
         <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="max-word" dicPath="dic"/>
         </analyzer>
         </fieldType>
         <fieldType name="textSimple" class="solr.TextField" >
         <analyzer>
         <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="simple" dicPath="dic"/>
         </analyzer>
         </fieldType>
       2. 分别配置fied如下：
         <!--自定义filed -->
         <field name="test_name" type="textComplex" indexed="true" stored="true"  />
         <field name="test_content" type="textComplex" indexed="true" stored="true" />
         <field name="test_default" type="textComplex" indexed="true" stored="true" multiValued="true"/>
       3. 配置默认查询字段
        <defaultSearchField>test_default</defaultSearchField>
        <copyField source="test_name" dest="test_default"/>
        <copyField source="test_content" dest="test_default"/>
     *
     * @param keyword
     */
    public void query(String keyword) {

        try {

            SolrQuery query = new SolrQuery(keyword);

            // 高亮 + 分页
            query.setHighlight(true).setHighlightSimplePre("<b color='red'>")
                .setHighlightSimplePost("</b>").setStart(0).setRows(5);
            //
            query.setParam("hl.fl", "test_name,test_content");
            QueryResponse response = SolrUtils.getInstance().query(query);

            SolrDocumentList list = response.getResults();

            for (SolrDocument solrDocument : list) {

                System.out.println("原始查询结果  ： name = " + solrDocument.get("test_name")
                                   + "content = " + solrDocument.get("test_content"));

                String id = (String) solrDocument.getFieldValue("id");

                String name = (String) solrDocument.get("test_name");
                String content = (String) solrDocument.get("test_content");

//                System.out.println(response.getHighlighting().get(id));

                if (response.getHighlighting().get(id).get("test_name") != null) {
                    name = (String) response.getHighlighting().get(id).get("test_name").get(0);

                }

                if (response.getHighlighting().get(id).get("test_content") != null) {
                    content = (String) response.getHighlighting().get(id).get("test_content")
                        .get(0);
                }

                System.out.println("高亮结果  ： name = " + name + "content = " + content);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}