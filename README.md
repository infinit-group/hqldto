hqldto
======

Grails Plugin that allows creation of DTOs from HQL queries and converting them into JSON

Installation
------------
The plugin is currently not an official Grails plugin.
You have to download the sourcecode using the command

	git clone git@github.com:infinit-group/hqldto.git

In you grails project just add the following line at the bottom of your BuildConfig.groovy:

	grails.plugin.location."hqldto" = "../hqldto"

Note that the line above assumes that the hqldto plugin is located in the same directory as your grails project.

Usage
-----
In your controller or services just inject the HqlDtoService that is provided by the plugin and use it to execute queries:

	class YourService {
		...
		HqlDtoService hqlDtoService
		...
		List<HqlDto> getSomeDtos() {
			...
			return hqlDtoService.executeDtoQuery(hqlQueryString)
		}
	...
	}
 
The HqlDtoService.executeDtoQuery supports the same method signatures as the conventional Grails HQL queries:
This means the query works the same way as <SomeGrailsDomainClass>.executeQuery(...).

Examples
--------
A minimalistic Grails project that uses the hqlDto plugin can be found here:
https://github.com/infinit-group/hqldto

To clone this project type

	git clone git@github.com:infinit-group/hqldto-demo.git
