class akvo-flow {

  file { "/etc/nginx/sites-enabled/flow":
    ensure  => present,
    content => template("akvo-flow/nginx_flow.erb")
  }
  
  file { "/akvo-flow/GAE/war/WEB-INF/appengine-web.xml":
    ensure  => present,
    content => template("akvo-flow/appengine-web.xml.erb")
  }

  file { "/akvo-flow/GAE/build.properties":
    ensure  => present,
    content => template("akvo-flow/build.properties.erb")
  }

  file { "/akvo-flow/GAE/war/WEB-INF/appengine-generated/":
    ensure => "directory",
  }

  file { '/akvo-flow/GAE/war/WEB-INF/appengine-generated/local_db.bin':
    ensure => 'link',
    target => '/akvo-flow-env/local_db_org.bin',
    require => File["/akvo-flow/GAE/war/WEB-INF/appengine-generated/"]
  }

  file { '/akvo-flow/GAE/src/org/waterforpeople/mapping/app/gwt/client/util/UploadConstants.properties':
    ensure => 'link',
    target => '/akvo-flow-env/UploadConstants.properties',
  }
}