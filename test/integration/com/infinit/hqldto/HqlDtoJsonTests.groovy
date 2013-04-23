package com.infinit.hqldto

import com.infinit.hqldto.dtos.HqlDto
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Test that JSON is rendered properly
 */
class HqlDtoJsonTests extends GroovyTestCase {

	HqlDtoService hqlDtoService

	/**
	 * Test plain conversion of HqlDto to JSON
	 */
	void testRenderHqlDtoAsJson() {
		// arrange
		HqlDto hqlDto = new HqlDto(name: "name1", duration: 1)

		// act
		JSON json = hqlDto as JSON

		// assert
		String jsonString = json.toString(true)
		JSONElement jsonElement = JSON.parse(jsonString)
		assert jsonElement instanceof JSONObject
		assert "name1" == jsonElement.name
		assert 1 == jsonElement.duration
	}

	/**
	 * Test rendering a list of HqlDtos in Controller
	 */
	void testRenderHqlDtoAsJsonResponseInController() {
		// arrange
		new TestDomain(name: "name1", duration: 1).save(flush: true, failOnError: true)
		new TestDomain(name: "name2", duration: 2).save(flush: true, failOnError: true)
		TestController testController = new TestController()
		testController.hqlDtoService = hqlDtoService

		// act
		testController.getTestDomainsAsJson()

		// assert
		String jsonString = testController.response.contentAsString
		assert jsonString
		JSONElement json = JSON.parse(jsonString)
		assert json instanceof JSONArray
		assert 2 == json.size()
		assert "name1" == json[0].name
		assert 1 == json[0].duration
		assert "name2" == json[1].name
		assert 2 == json[1].duration
	}

}
