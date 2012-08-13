var get = Ember.get, set = Ember.set;

DS.FLOWRESTAdapter = DS.Adapter.extend({
  bulkCommit: false,

////////////// Create Record //////////////
  createRecord: function(store, type, record) {
   // var root = this.rootForType(type);

    var data = "7\|0\|7\|http://akvoflowsandbox.appspot.com/org.waterforpeople.mapping.portal.portal/\|A40BA8A568CA4A2E9CBDC22A57BBDF58\|org.waterforpeople.mapping.app.gwt.client.survey.SurveyService\|saveSurveyGroup\|org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto/2955417315\|Mark\|mark westra\|1\|2\|3\|4\|1\|5\|5\|6\|0\|7\|0\|0\|0\|0\|";
  //  data[root] = record.toJSON();
	
	console.log("creating record");
	
    this.ajaxPOST("http://flow-dashboard.dev/REST/org.waterforpeople.mapping.portal.portal/surveyrpcservice", "POST", {
      data: data,
      context: this,
      success: function(json) {
        this.didCreateRecord(store, type, record, json);
      }
    });
  },

  didCreateRecord: function(store, type, record, json) {
    var root = this.rootForType(type);

    this.sideload(store, type, json, root);
    store.didCreateRecord(record, json[root]);
  },

//
//createRecord: function(store, type, record) {
//    var root = this.rootForType(type);
//
//    var data = {};
//    data[root] = record.toJSON();
//
//    this.ajax(this.buildURL(root), "POST", {
//      data: data,
//      context: this,
//      success: function(json) {
//        this.didCreateRecord(store, type, record, json);
//      }
//    });
//  },
//
//  didCreateRecord: function(store, type, record, json) {
//    var root = this.rootForType(type);
//
//    this.sideload(store, type, json, root);
//    store.didCreateRecord(record, json[root]);
//  },


////////////// Create Records //////////////
  createRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root);

    var data = {};
    data[plural] = records.map(function(record) {
      return record.toJSON();
    });

    this.ajax(this.buildURL(root), "POST", {
      data: data,
      context: this,
      success: function(json) {
        this.didCreateRecords(store, type, records, json);
      }
    });
  },

  didCreateRecords: function(store, type, records, json) {
    var root = this.pluralize(this.rootForType(type));

    this.sideload(store, type, json, root);
    store.didCreateRecords(type, records, json[root]);
  },

////////////// Update Record //////////////
  updateRecord: function(store, type, record) {
    var id = get(record, 'id');
    var root = this.rootForType(type);

    var data = {};
    data[root] = record.toJSON();

    this.ajax(this.buildURL(root, id), "PUT", {
      data: data,
      context: this,
      success: function(json) {
        this.didUpdateRecord(store, type, record, json);
      }
    });
  },

  didUpdateRecord: function(store, type, record, json) {
    var root = this.rootForType(type);

    this.sideload(store, type, json, root);
    store.didUpdateRecord(record, json && json[root]);
  },

////////////// Update Records //////////////
  updateRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root);

    var data = {};
    data[plural] = records.map(function(record) {
      return record.toJSON();
    });

    this.ajax(this.buildURL(root, "bulk"), "PUT", {
      data: data,
      context: this,
      success: function(json) {
        this.didUpdateRecords(store, type, records, json);
      }
    });
  },

  didUpdateRecords: function(store, type, records, json) {
    var root = this.pluralize(this.rootForType(type));

    this.sideload(store, type, json, root);
    store.didUpdateRecords(records, json[root]);
  },

////////////// Delete Record //////////////
  deleteRecord: function(store, type, record) {
    var id = get(record, 'id');
    var root = this.rootForType(type);

    this.ajax(this.buildURL(root, id), "DELETE", {
      context: this,
      success: function(json) {
        this.didDeleteRecord(store, type, record, json);
      }
    });
  },

  didDeleteRecord: function(store, type, record, json) {
    if (json) { this.sideload(store, type, json); }
    store.didDeleteRecord(record);
  },

////////////// Delete Records //////////////
  deleteRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root);

    var data = {};
    data[plural] = records.map(function(record) {
      return get(record, 'id');
    });

    this.ajax(this.buildURL(root, 'bulk'), "DELETE", {
      data: data,
      context: this,
      success: function(json) {
        this.didDeleteRecords(store, type, records, json);
      }
    });
  },

  didDeleteRecords: function(store, type, records, json) {
    if (json) { this.sideload(store, type, json); }
    store.didDeleteRecords(records);
  },

////////////// find //////////////
  find: function(store, type, id) {
    var root = this.rootForType(type);

    this.ajax(this.buildURL(root, id), "GET", {
      success: function(json) {
        this.sideload(store, type, json, root);
        store.load(type, json[root]);
      }
    });
  },

////////////// find Many //////////////
  findMany: function(store, type, ids) {
    var root = this.rootForType(type), plural = this.pluralize(root);

    this.ajax(this.buildURL(root), "GET", {
      data: { ids: ids },
      success: function(json) {
        this.sideload(store, type, json, plural);
        store.loadMany(type, json[plural]);
      }
    });
  },

////////////// find All //////////////
  findAll: function(store, type) {
    //var root = this.rootForType(type), plural = this.pluralize(root);

    this.ajax(this.buildURL(""), "GET", {
      success: function(json) {
      
      // we don't need sideload now
      //  this.sideload(store, type, json, plural);
      
      
        store.loadMany(type, json["dtoList"]);
      }
    });
  },

////////////// find Query //////////////
  findQuery: function(store, type, query, recordArray) {
    var root = this.rootForType(type), plural = this.pluralize(root);

    this.ajax(this.buildURL(root), "GET", {
      data: query,
      success: function(json) {
        this.sideload(store, type, json, plural);
        recordArray.load(json[plural]);
      }
    });
  },

  // HELPERS

  plurals: {},

  // define a plurals hash in your subclass to define
  // special-case pluralization
  pluralize: function(name) {
    return this.plurals[name] || name + "s";
  },

  rootForType: function(type) {
    if (type.url) { return type.url; }

    // use the last part of the name as the URL
    var parts = type.toString().split(".");
    var name = parts[parts.length - 1];
    return name.replace(/([A-Z])/g, '_$1').toLowerCase().slice(1);
  },

  ajax: function(url, type, hash) {
    hash.url = url;
    hash.type = type;
    hash.dataType = 'json';
    hash.contentType = 'application/json; charset=utf-8';
    hash.context = this;

    if (hash.data && type !== 'GET') {
     // hash.data = JSON.stringify(hash.data);
    }

    jQuery.ajax(hash);
  },
  
  ajaxPOST: function(url, type, hash) {
    hash.url = url;
    hash.type = type;
    hash.dataType = 'json';
    hash.contentType = "text/x-gwt-rpc; charset=utf-8";
    hash.context = this;
    hash.headers = {"X-GWT-Permutation":"5CEEFB2FFADF2FDF2DCA6D2DF2D13328"};
  

    jQuery.ajax(hash);
  },

  sideload: function(store, type, json, root) {
    var sideloadedType, mappings, loaded = {};

    loaded[root] = true;

    for (var prop in json) {
      if (!json.hasOwnProperty(prop)) { continue; }
      if (prop === root) { continue; }

      sideloadedType = type.typeForAssociation(prop);

      if (!sideloadedType) {
        mappings = get(this, 'mappings');
        Ember.assert("Your server returned a hash with the key " + prop + " but you have no mappings", !!mappings);

        sideloadedType = get(mappings, prop);

        if (typeof sideloadedType === 'string') {
          sideloadedType = get(window, sideloadedType);
        }

        Ember.assert("Your server returned a hash with the key " + prop + " but you have no mapping for it", !!sideloadedType);
      }

      this.sideloadAssociations(store, sideloadedType, json, prop, loaded);
    }
  },

  sideloadAssociations: function(store, type, json, prop, loaded) {
    loaded[prop] = true;

    get(type, 'associationsByName').forEach(function(key, meta) {
      key = meta.key || key;
      if (meta.kind === 'belongsTo') {
        key = this.pluralize(key);
      }
      if (json[key] && !loaded[key]) {
        this.sideloadAssociations(store, meta.type, json, key, loaded);
      }
    }, this);

    this.loadValue(store, type, json[prop]);
  },

  loadValue: function(store, type, value) {
    if (value instanceof Array) {
      store.loadMany(type, value);
    } else {
      store.load(type, value);
    }
  },

  buildURL: function(record, suffix) {
    var url = ["/REST/surveyrestapi"];

    //Ember.assert("Namespace URL (" + this.namespace + ") must not start with slash", !this.namespace || this.namespace.toString().charAt(0) !== "/");
    //Ember.assert("Record URL (" + record + ") must not start with slash", !record || record.toString().charAt(0) !== "/");
    //Ember.assert("URL suffix (" + suffix + ") must not start with slash", !suffix || suffix.toString().charAt(0) !== "/");

   // if (this.namespace !== undefined) {
   //   url.push(this.namespace);
   // }

   // url.push(this.pluralize(record));
   // if (suffix !== undefined) {
   //   url.push(suffix);
   // }
	url.push("?action=listSurveyGroups");

    return url.join("");
  }
});
