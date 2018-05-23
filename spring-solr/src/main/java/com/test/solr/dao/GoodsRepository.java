package com.test.solr.dao;

import java.util.List;

import com.test.solr.pojo.Goods;
import org.springframework.data.solr.repository.SolrCrudRepository;


public interface GoodsRepository extends SolrCrudRepository<Goods, String> {

	public List<Goods> findByTitle(String title);
	
}
