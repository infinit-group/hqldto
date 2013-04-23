package com.infinit.hqldto
import com.infinit.hqldto.dtos.HqlDto
import com.infinit.hqldto.exceptions.InvalidHqlDtoQueryException
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.SessionFactory

class HqlDtoServiceTests extends GroovyTestCase {

	// injections into test
	SessionFactory sessionFactory
	GrailsApplication grailsApplication

	// service to be tested
	HqlDtoService hqlDtoService

	void setUp() {
		super.setUp()
		hqlDtoService = new HqlDtoService()
		hqlDtoService.sessionFactory = sessionFactory
		hqlDtoService.grailsApplication = grailsApplication

		// arrange some test domain
		new TestDomain(name: "name1", duration: 1).save(flush: true, failOnError: true)
		new TestDomain(name: "name2", duration: 2).save(flush: true, failOnError: true)
	}

	void testExecuteDtoQueryWithoutDomainAliasAndWithoutPropertyAliases() {
		def hqlDtos = hqlDtoService.executeDtoQuery("select name, duration from TestDomain order by name")
		assertHqlDtoFields(hqlDtos, "name", ["name1", "name2"])
		assertHqlDtoFields(hqlDtos, "duration", [1, 2])
	}

	void testExecuteDtoQueryWithoutDomainAliasAndWithPropertyAliases() {
		def hqlDtos = hqlDtoService.executeDtoQuery("select name as name_alias, duration as duration_alias from TestDomain order by name_alias")
		assertHqlDtoFields(hqlDtos, "name_alias", ["name1", "name2"])
		assertHqlDtoFields(hqlDtos, "duration_alias", [1, 2])
	}

	void testExecuteDtoQueryWithDomainAliasAndWithoutPropertyAliases() {
		def hqlDtos = hqlDtoService.executeDtoQuery("select t.name, t.duration from TestDomain t order by name")
		assertHqlDtoFields(hqlDtos, "name", ["name1", "name2"])
		assertHqlDtoFields(hqlDtos, "duration", [1, 2])
	}

	void testExecuteDtoQueryWithDomainAliasAndWithPropertyAliases() {
		def hqlDtos = hqlDtoService.executeDtoQuery("select t.name as name_alias, t.duration as duration_alias from TestDomain t order by t.name")
		assertHqlDtoFields(hqlDtos, "name_alias", ["name1", "name2"])
		assertHqlDtoFields(hqlDtos, "duration_alias", [1, 2])
	}

	void testExecuteDtoQueryWithDuplicatePropertyNames() {
		String failureMessage = shouldFail(InvalidHqlDtoQueryException) {
			hqlDtoService.executeDtoQuery("select name, name from TestDomain order by name")
		}
		assert failureMessage
		assert failureMessage.startsWith("Duplicate HQL property name or alias found in select clause")
	}

	void testExecuteDtoQueryWithNonScalarSelectClause() {
		String failureMessage = shouldFail(InvalidHqlDtoQueryException) {
			hqlDtoService.executeDtoQuery("select t from TestDomain t")
		}
		assert failureMessage
		assert failureMessage.startsWith("The select clause of the HQL query may only consist of scalar properties")
	}

	private void assertHqlDtoFields(List<HqlDto> hqlDtos, String fieldName, List<String> values) {
		assert values == hqlDtos.collect { it.get(fieldName) }
	}

}
