package com.infinit.hqldto

class TestDomain {

	String name
	Integer duration

    static constraints = {
	    name nullable: false, blank: false
	    duration nullable: false
    }
}
