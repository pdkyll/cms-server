package nl.menninga.menno.cms.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.MustJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;

import nl.menninga.menno.cms.entity.CmsObject;

@Repository
@Transactional
public class CmsObjectSearchRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public List<CmsObject> search(boolean isAdmin, String text) {

		// get the full text entity manager
		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
				.getFullTextEntityManager(entityManager);

		// create the query using Hibernate Search query DSL
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder()
				.forEntity(CmsObject.class).get();

		Query publishedQuery = queryBuilder.keyword().onField("published").matching(true).createQuery();
		
		Query searchStringQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(2)
				.withPrefixLength(1).onFields("searchString").matching(text).createQuery();
		
		MustJunction mustJunction = queryBuilder.bool().must( searchStringQuery );
		if(!isAdmin) {
			mustJunction = mustJunction.must( publishedQuery );
		}
		Query conditionalQuery = mustJunction.createQuery();

		// wrap Lucene query in an Hibernate Query object
		FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(conditionalQuery,
				CmsObject.class);

		// execute search and return results (sorted by relevance as default)
		@SuppressWarnings("unchecked")
		List<CmsObject> results = jpaQuery.getResultList();

		return results;
	}
}
