package com.infinit.hqldto

import antlr.collections.AST
import com.infinit.hqldto.dtos.HqlDto
import com.infinit.hqldto.exceptions.InvalidHqlDtoQueryException
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.orm.hibernate.metaclass.ExecuteQueryPersistentMethod
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.engine.query.HQLQueryPlan
import org.hibernate.hql.QueryTranslator
import org.hibernate.hql.antlr.SqlTokenTypes
import org.hibernate.hql.ast.QueryTranslatorImpl
import org.hibernate.hql.ast.tree.DotNode
import org.hibernate.hql.ast.tree.QueryNode
import org.hibernate.hql.ast.tree.SelectClause
import org.hibernate.hql.ast.util.NodeTraverser
import org.hibernate.impl.SessionFactoryImpl
import org.hibernate.impl.SessionImpl

class HqlDtoService {

	SessionFactory sessionFactory
	GrailsApplication grailsApplication

	List<HqlDto> executeDtoQuery(String query) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	List<HqlDto> executeDtoQuery(String query, arg) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query, arg] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	List<HqlDto> executeDtoQuery(String query, Map args) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query, args] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	List<HqlDto> executeDtoQuery(String query, Map params, Map args) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query, params, args] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	List<HqlDto> executeDtoQuery(String query, Collection params) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query, params] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	List<HqlDto> executeDtoQuery(String query, Collection params, Map args) {
		List<Object[]> rows = (List<Object[]>) executeQueryMethod.invoke(HqlDtoService, "executeQuery", [query, params, args] as Object[])
		return convertRowsToHqlDtos(rows, query)
	}

	private ExecuteQueryPersistentMethod getExecuteQueryMethod() {
		return new ExecuteQueryPersistentMethod(sessionFactory, grailsApplication.classLoader, grailsApplication)
	}

	private List<HqlDto> convertRowsToHqlDtos(List<Object[]> rows, String query) {
		List<String> fields = extractDtoProperties(query)
		return rows.collect { def row ->
			HqlDto hqlDto = new HqlDto()
			row.eachWithIndex { def cell, Integer index ->
				hqlDto.put(fields[index], cell)
			}
			return hqlDto
		}
	}

	private List<String> extractDtoProperties(String query) {
		List<String> result = []
		Session session = sessionFactory.getCurrentSession()
		HQLQueryPlan hqlPlan = ((SessionFactoryImpl) sessionFactory).getQueryPlanCache().getHQLQueryPlan(query, false, ((SessionImpl) session).getEnabledFilters())
		QueryTranslator[] translators = hqlPlan.getTranslators()
		QueryNode queryNode = (QueryNode) ((QueryTranslatorImpl) translators[0]).getSqlAST()
		SelectClause selectClause = queryNode.selectClause
		if (!selectClause.isScalarSelect()) {
			throw new InvalidHqlDtoQueryException("The select clause of the HQL query may only consist of scalar properties")
		}
		NodeTraverser.VisitationStrategy visitationStrategy = new NodeTraverser.VisitationStrategy() {
			public void visit(AST node) {
				if (node.getType() == SqlTokenTypes.DOT) {
					DotNode dotNode = (DotNode) node
					String propertyName = dotNode.getPropertyPath()
					String alias = dotNode.getAlias()
					if (alias) {
						addToPropertyNameList(result, alias)
					} else {
						addToPropertyNameList(result, propertyName)
					}
				}
			}
		}
		NodeTraverser nodeTraverser = new NodeTraverser(visitationStrategy)
		nodeTraverser.traverseDepthFirst(selectClause)
		return result
	}

	void addToPropertyNameList(List<String> propertyNameList, String propertyName) {
		if (propertyNameList.contains(propertyName)) {
			throw new InvalidHqlDtoQueryException("Duplicate HQL property name or alias found in select clause: " + propertyName)
		}
		propertyNameList << propertyName
	}

}
