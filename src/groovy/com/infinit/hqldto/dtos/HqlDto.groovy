package com.infinit.hqldto.dtos

/**
 * Holds the result of one row of a HQL query
 */
class HqlDto extends HashMap<String, Object> {

	HqlDto() {
		super()
	}

	HqlDto(Map map) {
		super(map)
	}

}
