package config

couchdbUrl = 'http://127.0.0.1:5984/'
couchdbDatabaseName = 'data-generation'

environments {
    test {
    }
    localhost {
        couchdbUrl = 'http://127.0.0.1:5984/'
        couchdbDatabaseName = 'data-generation'
    }
}