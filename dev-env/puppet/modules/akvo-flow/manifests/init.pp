class akvo-flow {
  file { "/akvo-flow/Dashboard/app/js/lib/models/store_def.js":
    ensure  => present,
    content => template("akvo-flow/store_def.js_rest.erb")
  }
}