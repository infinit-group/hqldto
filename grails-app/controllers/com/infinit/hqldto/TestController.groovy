package com.infinit.hqldto

import com.infinit.hqldto.dtos.HqlDto
import grails.converters.JSON

class TestController {

	HqlDtoService hqlDtoService

    def getTestDomainsAsJson() {
	    List<HqlDto> hqlDtos = hqlDtoService.executeDtoQuery("select name, duration from TestDomain order by name")
	    render hqlDtos as JSON
    }
}
