(function() {
window.DS = Ember.Namespace.create({
  CURRENT_API_REVISION: 10
});

})();



(function() {
var get = Ember.get, set = Ember.set;

/**
  A record array is an array that contains records of a certain type. The record
  array materializes records as needed when they are retrieved for the first
  time. You should not create record arrays yourself. Instead, an instance of
  DS.RecordArray or its subclasses will be returned by your application's store
  in response to queries.
*/

DS.RecordArray = Ember.ArrayProxy.extend(Ember.Evented, {
  /**
    The model type contained by this record array.

    @type DS.Model
  */
  type: null,

  // The array of client ids backing the record array. When a
  // record is requested from the record array, the record
  // for the client id at the same index is materialized, if
  // necessary, by the store.
  content: null,

  isLoaded: false,
  isUpdating: false,

  // The store that created this record array.
  store: null,

  objectAtContent: function(index) {
    var content = get(this, 'content'),
        clientId = content.objectAt(index),
        store = get(this, 'store');

    if (clientId !== undefined) {
      return store.findByClientId(get(this, 'type'), clientId);
    }
  },

  materializedObjectAt: function(index) {
    var clientId = get(this, 'content').objectAt(index);
    if (!clientId) { return; }

    if (get(this, 'store').recordIsMaterialized(clientId)) {
      return this.objectAt(index);
    }
  },

  update: function() {
    if (get(this, 'isUpdating')) { return; }

    var store = get(this, 'store'),
        type = get(this, 'type');

    store.fetchAll(type, this);
  }
});

})();



(function() {
var get = Ember.get;

DS.FilteredRecordArray = DS.RecordArray.extend({
  filterFunction: null,
  isLoaded: true,

  replace: function() {
    var type = get(this, 'type').toString();
    throw new Error("The result of a client-side filter (on " + type + ") is immutable.");
  },

  updateFilter: Ember.observer(function() {
    var store = get(this, 'store');
    store.updateRecordArrayFilter(this, get(this, 'type'), get(this, 'filterFunction'));
  }, 'filterFunction')
});

})();



(function() {
var get = Ember.get, set = Ember.set;

DS.AdapterPopulatedRecordArray = DS.RecordArray.extend({
  query: null,

  replace: function() {
    var type = get(this, 'type').toString();
    throw new Error("The result of a server query (on " + type + ") is immutable.");
  },

  load: function(array) {
    var store = get(this, 'store'), type = get(this, 'type');

    var clientIds = store.loadMany(type, array).clientIds;

    this.beginPropertyChanges();
    set(this, 'content', Ember.A(clientIds));
    set(this, 'isLoaded', true);
    this.endPropertyChanges();

    this.trigger('didLoad');
  }
});

})();



(function() {
var get = Ember.get, set = Ember.set, guidFor = Ember.guidFor;

var Set = function() {
  this.hash = {};
  this.list = [];
};

Set.prototype = {
  add: function(item) {
    var hash = this.hash,
        guid = guidFor(item);

    if (hash.hasOwnProperty(guid)) { return; }

    hash[guid] = true;
    this.list.push(item);
  },

  remove: function(item) {
    var hash = this.hash,
        guid = guidFor(item);

    if (!hash.hasOwnProperty(guid)) { return; }

    delete hash[guid];
    var list = this.list,
        index = Ember.EnumerableUtils.indexOf(this, item);

    list.splice(index, 1);
  },

  isEmpty: function() {
    return this.list.length === 0;
  }
};

var LoadedState = Ember.State.extend({
  recordWasAdded: function(manager, record) {
    var dirty = manager.dirty, observer;
    dirty.add(record);

    observer = function() {
      if (!get(record, 'isDirty')) {
        record.removeObserver('isDirty', observer);
        manager.send('childWasSaved', record);
      }
    };

    record.addObserver('isDirty', observer);
  },

  recordWasRemoved: function(manager, record) {
    var dirty = manager.dirty, observer;
    dirty.add(record);

    observer = function() {
      record.removeObserver('isDirty', observer);
      if (!get(record, 'isDirty')) { manager.send('childWasSaved', record); }
    };

    record.addObserver('isDirty', observer);
  }
});

var states = {
  loading: Ember.State.create({
    isLoaded: false,
    isDirty: false,

    loadedRecords: function(manager, count) {
      manager.decrement(count);
    },

    becameLoaded: function(manager) {
      manager.transitionTo('clean');
    }
  }),

  clean: LoadedState.create({
    isLoaded: true,
    isDirty: false,

    recordWasAdded: function(manager, record) {
      this._super(manager, record);
      manager.goToState('dirty');
    },

    update: function(manager, clientIds) {
      var manyArray = manager.manyArray;
      set(manyArray, 'content', clientIds);
    }
  }),

  dirty: LoadedState.create({
    isLoaded: true,
    isDirty: true,

    childWasSaved: function(manager, child) {
      var dirty = manager.dirty;
      dirty.remove(child);

      if (dirty.isEmpty()) { manager.send('arrayBecameSaved'); }
    },

    arrayBecameSaved: function(manager) {
      manager.goToState('clean');
    }
  })
};

DS.ManyArrayStateManager = Ember.StateManager.extend({
  manyArray: null,
  initialState: 'loading',
  states: states,

  /**
   This number is used to keep track of the number of outstanding
   records that must be loaded before the array is considered
   loaded. As results stream in, this number is decremented until
   it becomes zero, at which case the `isLoaded` flag will be set
   to true
  */
  counter: 0,

  init: function() {
    this._super();
    this.dirty = new Set();
    this.counter = get(this, 'manyArray.length');
  },

  decrement: function(count) {
    var counter = this.counter = this.counter - count;

    Ember.assert("Somehow the ManyArray loaded counter went below 0. This is probably an ember-data bug. Please report it at https://github.com/emberjs/data/issues", counter >= 0);

    if (counter === 0) {
      this.send('becameLoaded');
    }
  }
});

})();



(function() {
var get = Ember.get, set = Ember.set;

/**
  A ManyArray is a RecordArray that represents the contents of a has-many
  association.

  The ManyArray is instantiated lazily the first time the association is
  requested.

  ### Inverses

  Often, the associations in Ember Data applications will have
  an inverse. For example, imagine the following models are
  defined:

      App.Post = DS.Model.extend({
        comments: DS.hasMany('App.Comment')
      });

      App.Comment = DS.Model.extend({
        post: DS.belongsTo('App.Post')
      });

  If you created a new instance of `App.Post` and added
  a `App.Comment` record to its `comments` has-many
  association, you would expect the comment's `post`
  property to be set to the post that contained
  the has-many.

  We call the record to which an association belongs the
  association's _owner_.
*/
DS.ManyArray = DS.RecordArray.extend({
  init: function() {
    this._super.apply(this, arguments);
    this._changesToSync = Ember.OrderedSet.create();
  },

  /**
    @private

    The record to which this association belongs.

    @property {DS.Model}
  */
  owner: null,

  // LOADING STATE

  isLoaded: false,

  loadingRecordsCount: function(count) {
    this.loadingRecordsCount = count;
  },

  loadedRecord: function() {
    this.loadingRecordsCount--;
    if (this.loadingRecordsCount === 0) {
      set(this, 'isLoaded', true);
      this.trigger('didLoad');
    }
  },

  fetch: function() {
    var clientIds = get(this, 'content'),
        store = get(this, 'store'),
        type = get(this, 'type');

    store.fetchUnloadedClientIds(type, clientIds);
  },

  // Overrides Ember.Array's replace method to implement
  replaceContent: function(index, removed, added) {
    // Map the array of record objects into an array of  client ids.
    added = added.map(function(record) {
      Ember.assert("You can only add records of " + (get(this, 'type') && get(this, 'type').toString()) + " to this association.", !get(this, 'type') || (get(this, 'type') === record.constructor));
      return record.get('clientId');
    }, this);

    this._super(index, removed, added);
  },

  arrangedContentDidChange: function() {
    this.fetch();
  },

  arrayContentWillChange: function(index, removed, added) {
    var owner = get(this, 'owner'),
        name = get(this, 'name');

    if (!owner._suspendedAssociations) {
      // This code is the first half of code that continues inside
      // of arrayContentDidChange. It gets or creates a change from
      // the child object, adds the current owner as the old
      // parent if this is the first time the object was removed
      // from a ManyArray, and sets `newParent` to null.
      //
      // Later, if the object is added to another ManyArray,
      // the `arrayContentDidChange` will set `newParent` on
      // the change.
      for (var i=index; i<index+removed; i++) {
        var clientId = get(this, 'content').objectAt(i);
        //var record = this.objectAt(i);
        //if (!record) { continue; }

        var change = DS.OneToManyChange.forChildAndParent(clientId, get(this, 'store'), {
          parentType: owner.constructor,
          hasManyName: name
        });
        change.hasManyName = name;

        if (change.oldParent === undefined) { change.oldParent = get(owner, 'clientId'); }
        change.newParent = null;
        this._changesToSync.add(change);
      }
    }

    return this._super.apply(this, arguments);
  },

  arrayContentDidChange: function(index, removed, added) {
    this._super.apply(this, arguments);

    var owner = get(this, 'owner'),
        name = get(this, 'name');

    if (!owner._suspendedAssociations) {
      // This code is the second half of code that started in
      // `arrayContentWillChange`. It gets or creates a change
      // from the child object, and adds the current owner as
      // the new parent.
      for (var i=index; i<index+added; i++) {
        var clientId = get(this, 'content').objectAt(i);

        var change = DS.OneToManyChange.forChildAndParent(clientId, get(this, 'store'), {
          parentType: owner.constructor,
          hasManyName: name
        });
        change.hasManyName = name;

        // The oldParent will be looked up in `sync` if it
        // was not set by `belongsToWillChange`.
        change.newParent = get(owner, 'clientId');
        this._changesToSync.add(change);
      }

      // We wait until the array has finished being
      // mutated before syncing the OneToManyChanges created
      // in arrayContentWillChange, so that the array
      // membership test in the sync() logic operates
      // on the final results.
      this._changesToSync.forEach(function(change) {
        change.sync();
      });
      this._changesToSync.clear();
    }
  },

  // Create a child record within the owner
  createRecord: function(hash, transaction) {
    var owner = get(this, 'owner'),
        store = get(owner, 'store'),
        type = get(this, 'type'),
        record;

    transaction = transaction || get(owner, 'transaction');

    record = store.createRecord.call(store, type, hash, transaction);
    this.pushObject(record);

    return record;
  },

  /**
    METHODS FOR USE BY INVERSE RELATIONSHIPS
    ========================================

    These methods exists so that belongsTo relationships can
    set their inverses without causing an infinite loop.

    This creates two APIs:

    * the normal enumerable API, which is used by clients
      of the `ManyArray` and triggers a change to inverse
      `belongsTo` relationships.
    * `removeFromContent` and `addToContent`, which are
      used by inverse relationships and do not trigger a
      change to `belongsTo` relationships.

    Unlike the normal `addObject` and `removeObject` APIs,
    these APIs manipulate the `content` array without
    triggering side-effects.
  */

  /** @private */
  removeFromContent: function(record) {
    var clientId = get(record, 'clientId');
    get(this, 'content').removeObject(clientId);
  },

  /** @private */
  addToContent: function(record) {
    var clientId = get(record, 'clientId');
    get(this, 'content').addObject(clientId);
  }
});

})();



(function() {

})();



(function() {
var get = Ember.get, set = Ember.set, fmt = Ember.String.fmt,
    removeObject = Ember.EnumerableUtils.removeObject, forEach = Ember.EnumerableUtils.forEach;

/**
  A transaction allows you to collect multiple records into a unit of work
  that can be committed or rolled back as a group.

  For example, if a record has local modifications that have not yet
  been saved, calling `commit()` on its transaction will cause those
  modifications to be sent to the adapter to be saved. Calling
  `rollback()` on its transaction would cause all of the modifications to
  be discarded and the record to return to the last known state before
  changes were made.

  If a newly created record's transaction is rolled back, it will
  immediately transition to the deleted state.

  If you do not explicitly create a transaction, a record is assigned to
  an implicit transaction called the default transaction. In these cases,
  you can treat your application's instance of `DS.Store` as a transaction
  and call the `commit()` and `rollback()` methods on the store itself.

  Once a record has been successfully committed or rolled back, it will
  be moved back to the implicit transaction. Because it will now be in
  a clean state, it can be moved to a new transaction if you wish.

  ### Creating a Transaction

  To create a new transaction, call the `transaction()` method of your
  application's `DS.Store` instance:

      var transaction = App.store.transaction();

  This will return a new instance of `DS.Transaction` with no records
  yet assigned to it.

  ### Adding Existing Records

  Add records to a transaction using the `add()` method:

      record = App.store.find(App.Person, 1);
      transaction.add(record);

  Note that only records whose `isDirty` flag is `false` may be added
  to a transaction. Once modifications to a record have been made
  (its `isDirty` flag is `true`), it is not longer able to be added to
  a transaction.

  ### Creating New Records

  Because newly created records are dirty from the time they are created,
  and because dirty records can not be added to a transaction, you must
  use the `createRecord()` method to assign new records to a transaction.

  For example, instead of this:

    var transaction = store.transaction();
    var person = App.Person.createRecord({ name: "Steve" });

    // won't work because person is dirty
    transaction.add(person);

  Call `createRecord()` on the transaction directly:

    var transaction = store.transaction();
    transaction.createRecord(App.Person, { name: "Steve" });

  ### Asynchronous Commits

  Typically, all of the records in a transaction will be committed
  together. However, new records that have a dependency on other new
  records need to wait for their parent record to be saved and assigned an
  ID. In that case, the child record will continue to live in the
  transaction until its parent is saved, at which time the transaction will
  attempt to commit again.

  For this reason, you should not re-use transactions once you have committed
  them. Always make a new transaction and move the desired records to it before
  calling commit.
*/

var arrayDefault = function() { return []; };

DS.Transaction = Ember.Object.extend({
  /**
    @private

    Creates the bucket data structure used to segregate records by
    type.
  */
  init: function() {
    set(this, 'buckets', {
      clean:    Ember.OrderedSet.create(),
      created:  Ember.OrderedSet.create(),
      updated:  Ember.OrderedSet.create(),
      deleted:  Ember.OrderedSet.create(),
      inflight: Ember.OrderedSet.create()
    });

    set(this, 'relationships', Ember.OrderedSet.create());
  },

  /**
    Creates a new record of the given type and assigns it to the transaction
    on which the method was called.

    This is useful as only clean records can be added to a transaction and
    new records created using other methods immediately become dirty.

    @param {DS.Model} type the model type to create
    @param {Object} hash the data hash to assign the new record
  */
  createRecord: function(type, hash) {
    var store = get(this, 'store');

    return store.createRecord(type, hash, this);
  },

  isEqualOrDefault: function(other) {
    if (this === other || other === get(this, 'store.defaultTransaction')) {
      return true;
    }
  },

  isDefault: Ember.computed(function() {
    return this === get(this, 'store.defaultTransaction');
  }),

  /**
    Adds an existing record to this transaction. Only records without
    modficiations (i.e., records whose `isDirty` property is `false`)
    can be added to a transaction.

    @param {DS.Model} record the record to add to the transaction
  */
  add: function(record) {
    Ember.assert("You must pass a record into transaction.add()", record instanceof DS.Model);

    var recordTransaction = get(record, 'transaction'),
        defaultTransaction = get(this, 'store.defaultTransaction');

    // Make `add` idempotent
    if (recordTransaction === this) { return; }

    // XXX it should be possible to move a dirty transaction from the default transaction

    // we could probably make this work if someone has a valid use case. Do you?
    Ember.assert("Once a record has changed, you cannot move it into a different transaction", !get(record, 'isDirty'));

    Ember.assert("Models cannot belong to more than one transaction at a time.", recordTransaction === defaultTransaction);

    this.adoptRecord(record);
  },

  relationshipBecameDirty: function(relationship) {
    get(this, 'relationships').add(relationship);
  },

  relationshipBecameClean: function(relationship) {
    get(this, 'relationships').remove(relationship);
  },

  /**
    Commits the transaction, which causes all of the modified records that
    belong to the transaction to be sent to the adapter to be saved.

    Once you call `commit()` on a transaction, you should not re-use it.

    When a record is saved, it will be removed from this transaction and
    moved back to the store's default transaction.
  */
  commit: function() {
    var store = get(this, 'store');
    var adapter = get(store, '_adapter');
    var defaultTransaction = get(store, 'defaultTransaction');

    var iterate = function(records) {
      var set = records.copy();
      set.forEach(function (record) {
        record.send('willCommit');
      });
      return set;
    };

    var relationships = get(this, 'relationships');

    var commitDetails = {
      created: iterate(this.bucketForType('created')),
      updated: iterate(this.bucketForType('updated')),
      deleted: iterate(this.bucketForType('deleted')),
      relationships: relationships
    };

    if (this === defaultTransaction) {
      set(store, 'defaultTransaction', store.transaction());
    }

    this.removeCleanRecords();

    if (!commitDetails.created.isEmpty() || !commitDetails.updated.isEmpty() || !commitDetails.deleted.isEmpty() || !relationships.isEmpty()) {
      if (adapter && adapter.commit) { adapter.commit(store, commitDetails); }
      else { throw fmt("Adapter is either null or does not implement `commit` method", this); }
    }

    // Once we've committed the transaction, there is no need to
    // keep the OneToManyChanges around. Destroy them so they
    // can be garbage collected.
    relationships.forEach(function(relationship) {
      relationship.destroy();
    });
  },

  /**
    Rolling back a transaction resets the records that belong to
    that transaction.

    Updated records have their properties reset to the last known
    value from the persistence layer. Deleted records are reverted
    to a clean, non-deleted state. Newly created records immediately
    become deleted, and are not sent to the adapter to be persisted.

    After the transaction is rolled back, any records that belong
    to it will return to the store's default transaction, and the
    current transaction should not be used again.
  */
  rollback: function() {
    // Loop through all of the records in each of the dirty states
    // and initiate a rollback on them. As a side effect of telling
    // the record to roll back, it should also move itself out of
    // the dirty bucket and into the clean bucket.
    ['created', 'updated', 'deleted', 'inflight'].forEach(function(bucketType) {
      var records = this.bucketForType(bucketType);
      forEach(records, function(record) {
        record.send('rollback');
      });
      records.clear();
    }, this);

    // Now that all records in the transaction are guaranteed to be
    // clean, migrate them all to the store's default transaction.
    this.removeCleanRecords();
  },

  /**
    @private

    Removes a record from this transaction and back to the store's
    default transaction.

    Note: This method is private for now, but should probably be exposed
    in the future once we have stricter error checking (for example, in the
    case of the record being dirty).

    @param {DS.Model} record
  */
  remove: function(record) {
    var defaultTransaction = get(this, 'store.defaultTransaction');
    defaultTransaction.adoptRecord(record);
  },

  /**
    @private

    Removes all of the records in the transaction's clean bucket.
  */
  removeCleanRecords: function() {
    var clean = this.bucketForType('clean');
    clean.forEach(function(record) {
      this.remove(record);
    }, this);
    clean.clear();
  },

  /**
    @private

    Returns the bucket for the given bucket type. For example, you might call
    `this.bucketForType('updated')` to get the `Ember.Map` that contains all
    of the records that have changes pending.

    @param {String} bucketType the type of bucket
    @returns Ember.Map
  */
  bucketForType: function(bucketType) {
    var buckets = get(this, 'buckets');

    return get(buckets, bucketType);
  },

  /**
    @private

    This method moves a record into a different transaction without the normal
    checks that ensure that the user is not doing something weird, like moving
    a dirty record into a new transaction.

    It is designed for internal use, such as when we are moving a clean record
    into a new transaction when the transaction is committed.

    This method must not be called unless the record is clean.

    @param {DS.Model} record
  */
  adoptRecord: function(record) {
    var oldTransaction = get(record, 'transaction');

    if (oldTransaction) {
      oldTransaction.removeFromBucket('clean', record);
    }

    this.addToBucket('clean', record);
    set(record, 'transaction', this);
  },

  /**
    @private

    Adds a record to the named bucket.

    @param {String} bucketType one of `clean`, `created`, `updated`, or `deleted`
  */
  addToBucket: function(bucketType, record) {
    this.bucketForType(bucketType).add(record);
  },

  /**
    @private

    Removes a record from the named bucket.

    @param {String} bucketType one of `clean`, `created`, `updated`, or `deleted`
  */
  removeFromBucket: function(bucketType, record) {
    this.bucketForType(bucketType).remove(record);
  },

  /**
    @private

    Called by a record's state manager to indicate that the record has entered
    a dirty state. The record will be moved from the `clean` bucket and into
    the appropriate dirty bucket.

    @param {String} bucketType one of `created`, `updated`, or `deleted`
  */
  recordBecameDirty: function(bucketType, record) {
    this.removeFromBucket('clean', record);
    this.addToBucket(bucketType, record);
  },

  /**
    @private

    Called by a record's state manager to indicate that the record has entered
    inflight state. The record will be moved from its current dirty bucket and into
    the `inflight` bucket.

    @param {String} bucketType one of `created`, `updated`, or `deleted`
  */
  recordBecameInFlight: function(kind, record) {
    this.removeFromBucket(kind, record);
    this.addToBucket('inflight', record);
  },

  recordIsMoving: function(kind, record) {
    this.removeFromBucket(kind, record);
    this.addToBucket('clean', record);
  },

  /**
    @private

    Called by a record's state manager to indicate that the record has entered
    a clean state. The record will be moved from its current dirty or inflight bucket and into
    the `clean` bucket.

    @param {String} bucketType one of `created`, `updated`, or `deleted`
  */
  recordBecameClean: function(kind, record) {
    this.removeFromBucket(kind, record);
    this.remove(record);
  }
});

})();



(function() {
var classify = Ember.String.classify, get = Ember.get;

/**
@private

  The Mappable mixin is designed for classes that would like to
  behave as a map for configuration purposes.

  For example, the DS.Adapter class can behave like a map, with
  more semantic API, via the `map` API:

    DS.Adapter.map('App.Person', { firstName: { keyName: 'FIRST' } });

  Class configuration via a map-like API has a few common requirements
  that differentiate it from the standard Ember.Map implementation.

  First, values often are provided as strings that should be normalized
  into classes the first time the configuration options are used.

  Second, the values configured on parent classes should also be taken
  into account.

  Finally, setting the value of a key sometimes should merge with the
  previous value, rather than replacing it.

  This mixin provides a instance method, `createInstanceMapFor`, that
  will reify all of the configuration options set on an instance's
  constructor and provide it for the instance to use.

  Classes can implement certain hooks that allow them to customize
  the requirements listed above:

  * `resolveMapConflict` - called when a value is set for an existing
    value
  * `transformMapKey` - allows a key name (for example, a global path
    to a class) to be normalized
  * `transformMapValue` - allows a value (for example, a class that
    should be instantiated) to be normalized

  Classes that implement this mixin should also implement a class
  method built using the `generateMapFunctionFor` method:

    DS.Adapter.reopenClass({
      map: DS.Mappable.generateMapFunctionFor('attributes', function(key, newValue, map) {
        var existingValue = map.get(key);

        for (var prop in newValue) {
          if (!newValue.hasOwnProperty(prop)) { continue; }
          existingValue[prop] = newValue[prop];
        }
      })
    });

   The function passed to `generateMapFunctionFor` is invoked every time a
   new value is added to the map.
**/

var resolveMapConflict = function(oldValue, newValue, mappingsKey) {
  return oldValue;
};

var transformMapKey = function(key, value) {
  return key;
};

var transformMapValue = function(key, value) {
  return value;
};

DS._Mappable = Ember.Mixin.create({
  createInstanceMapFor: function(mapName) {
    var instanceMeta = Ember.metaPath(this, ['DS.Mappable'], true);

    instanceMeta.values = instanceMeta.values || {};

    if (instanceMeta.values[mapName]) { return instanceMeta.values[mapName]; }

    var instanceMap = instanceMeta.values[mapName] = new Ember.Map();

    var klass = this.constructor;

    while (klass && klass !== DS.Store) {
      this._copyMap(mapName, klass, instanceMap);
      klass = klass.superclass;
    }

    instanceMeta.values[mapName] = instanceMap;
    return instanceMap;
  },

  _copyMap: function(mapName, klass, instanceMap) {
    var classMeta = Ember.metaPath(klass, ['DS.Mappable'], true);

    var classMap = classMeta[mapName];
    if (classMap) {
      classMap.forEach(eachMap, this);
    }

    function eachMap(key, value) {
      var transformedKey = (klass.transformMapKey || transformMapKey)(key, value);
      var transformedValue = (klass.transformMapValue || transformMapValue)(key, value);

      var oldValue = instanceMap.get(transformedKey);
      var newValue = transformedValue;

      if (oldValue) {
        newValue = (this.constructor.resolveMapConflict || resolveMapConflict)(oldValue, newValue, mapName);
      }

      instanceMap.set(transformedKey, newValue);
    }
  },


});

DS._Mappable.generateMapFunctionFor = function(mapName, transform) {
  return function(key, value) {
    var meta = Ember.metaPath(this, ['DS.Mappable'], true);
    var map = meta[mapName] || Ember.MapWithDefault.create({
      defaultValue: function() { return {}; }
    });

    transform.call(this, key, value, map);

    meta[mapName] = map;
  };
};

})();



(function() {
/*globals Ember*/
var get = Ember.get, set = Ember.set, fmt = Ember.String.fmt;

// These values are used in the data cache when clientIds are
// needed but the underlying data has not yet been loaded by
// the server.
var UNLOADED = 'unloaded';
var LOADING = 'loading';
var MATERIALIZED = { materialized: true };
var CREATED = { created: true };

// Implementors Note:
//
//   The variables in this file are consistently named according to the following
//   scheme:
//
//   * +id+ means an identifier managed by an external source, provided inside
//     the data provided by that source.
//   * +clientId+ means a transient numerical identifier generated at runtime by
//     the data store. It is important primarily because newly created objects may
//     not yet have an externally generated id.
//   * +type+ means a subclass of DS.Model.

// Used by the store to normalize IDs entering the store.  Despite the fact
// that developers may provide IDs as numbers (e.g., `store.find(Person, 1)`),
// it is important that internally we use strings, since IDs may be serialized
// and lose type information.  For example, Ember's router may put a record's
// ID into the URL, and if we later try to deserialize that URL and find the
// corresponding record, we will not know if it is a string or a number.
var coerceId = function(id) {
  return id+'';
};

var map = Ember.EnumerableUtils.map;

/**
  The store contains all of the data for records loaded from the server.
  It is also responsible for creating instances of DS.Model that wraps
  the individual data for a record, so that they can be bound to in your
  Handlebars templates.

  Create a new store like this:

       MyApp.store = DS.Store.create();

  You can retrieve DS.Model instances from the store in several ways. To retrieve
  a record for a specific id, use the `find()` method:

       var record = MyApp.store.find(MyApp.Contact, 123);

   By default, the store will talk to your backend using a standard REST mechanism.
   You can customize how the store talks to your backend by specifying a custom adapter:

       MyApp.store = DS.Store.create({
         adapter: 'MyApp.CustomAdapter'
       });

    You can learn more about writing a custom adapter by reading the `DS.Adapter`
    documentation.
*/
DS.Store = Ember.Object.extend(DS._Mappable, {

  /**
    Many methods can be invoked without specifying which store should be used.
    In those cases, the first store created will be used as the default. If
    an application has multiple stores, it should specify which store to use
    when performing actions, such as finding records by id.

    The init method registers this store as the default if none is specified.
  */
  init: function() {
    // Enforce API revisioning. See BREAKING_CHANGES.md for more.
    var revision = get(this, 'revision');

    if (revision !== DS.CURRENT_API_REVISION && !Ember.ENV.TESTING) {
      throw new Error("Error: The Ember Data library has had breaking API changes since the last time you updated the library. Please review the list of breaking changes at https://github.com/emberjs/data/blob/master/BREAKING_CHANGES.md, then update your store's `revision` property to " + DS.CURRENT_API_REVISION);
    }

    if (!get(DS, 'defaultStore') || get(this, 'isDefaultStore')) {
      set(DS, 'defaultStore', this);
    }

    // internal bookkeeping; not observable
    this.typeMaps = {};
    this.recordCache = [];
    this.clientIdToId = {};
    this.clientIdToType = {};
    this.clientIdToData = {};
    this.recordArraysByClientId = {};
    this.relationshipChanges = {};

    // Internally, we maintain a map of all unloaded IDs requested by
    // a ManyArray. As the adapter loads data into the store, the
    // store notifies any interested ManyArrays. When the ManyArray's
    // total number of loading records drops to zero, it becomes
    // `isLoaded` and fires a `didLoad` event.
    this.loadingRecordArrays = {};

    set(this, 'defaultTransaction', this.transaction());
  },

  /**
    Returns a new transaction scoped to this store. This delegates
    responsibility for invoking the adapter's commit mechanism to
    a transaction.

    Transaction are responsible for tracking changes to records
    added to them, and supporting `commit` and `rollback`
    functionality. Committing a transaction invokes the store's
    adapter, while rolling back a transaction reverses all
    changes made to records added to the transaction.

    A store has an implicit (default) transaction, which tracks changes
    made to records not explicitly added to a transaction.

    @see {DS.Transaction}
    @returns DS.Transaction
  */
  transaction: function() {
    return DS.Transaction.create({ store: this });
  },

  /**
    @private

    Instructs the store to materialize the data for a given record.

    To materialize a record, the store first retrieves the opaque data that was
    passed to either `load()` or `loadMany()`. Then, the data and the record
    are passed to the adapter's `materialize()` method, which allows the adapter
    to translate arbitrary data structures from the adapter into the normalized
    form the record expects.

    The adapter's `materialize()` method will invoke `materializeAttribute()`,
    `materializeHasMany()` and `materializeBelongsTo()` on the record to
    populate it with normalized values.

    @param {DS.Model} record
  */
  materializeData: function(record) {
    var clientId = get(record, 'clientId'),
        cidToData = this.clientIdToData,
        adapter = this.adapterForType(record.constructor),
        data = cidToData[clientId];

    cidToData[clientId] = MATERIALIZED;

    // Ensures the record's data structures are setup
    // before being populated by the adapter.
    record.setupData();

    if (data !== CREATED) {
      // Instructs the adapter to extract information from the
      // opaque data and materialize the record's attributes and
      // relationships.
      adapter.materialize(record, data);
    }
  },

  /**
    @private

    Returns true if there is already a record for this clientId.

    This is used to determine whether cleanup is required, so that
    "changes" to unmaterialized records do not trigger mass
    materialization.

    For example, if a parent record in an association with a large
    number of children is deleted, we want to avoid materializing
    those children.

    @param {String|Number} clientId
    @return {Boolean}
  */
  recordIsMaterialized: function(clientId) {
    return !!this.recordCache[clientId];
  },

  /**
    The adapter to use to communicate to a backend server or other persistence layer.

    This can be specified as an instance, a class, or a property path that specifies
    where the adapter can be located.

    @property {DS.Adapter|String}
  */
  adapter: 'DS.Adapter',

  /**
    @private

    Returns a JSON representation of the record using the adapter's
    serialization strategy. This method exists primarily to enable
    a record, which has access to its store (but not the store's
    adapter) to provide a `serialize()` convenience.

    The available options are:

    * `includeId`: `true` if the record's ID should be included in
      the JSON representation

    @param {DS.Model} record the record to serialize
    @param {Object} options an options hash
  */
  serialize: function(record, options) {
    return this.adapterForType(record.constructor).serialize(record, options);
  },

  /**
    @private

    This property returns the adapter, after resolving a possible
    property path.

    If the supplied `adapter` was a class, or a String property
    path resolved to a class, this property will instantiate the
    class.

    This property is cacheable, so the same instance of a specified
    adapter class should be used for the lifetime of the store.

    @returns DS.Adapter
  */
  _adapter: Ember.computed(function() {
    var adapter = get(this, 'adapter');
    if (typeof adapter === 'string') {
      adapter = get(this, adapter, false) || get(Ember.lookup, adapter);
    }

    if (DS.Adapter.detect(adapter)) {
      adapter = adapter.create();
    }

    return adapter;
  }).property('adapter'),

  /**
    @private

    A monotonically increasing number to be used to uniquely identify
    data and records.

    It starts at 1 so other parts of the code can test for truthiness
    when provided a `clientId` instead of having to explicitly test
    for undefined.
  */
  clientIdCounter: 1,

  // .....................
  // . CREATE NEW RECORD .
  // .....................

  /**
    Create a new record in the current store. The properties passed
    to this method are set on the newly created record.

    Note: The third `transaction` property is for internal use only.
    If you want to create a record inside of a given transaction,
    use `transaction.createRecord()` instead of `store.createRecord()`.

    @param {subclass of DS.Model} type
    @param {Object} properties a hash of properties to set on the
      newly created record.
    @returns DS.Model
  */
  createRecord: function(type, properties, transaction) {
    properties = properties || {};

    // Create a new instance of the model `type` and put it
    // into the specified `transaction`. If no transaction is
    // specified, the default transaction will be used.
    var record = type._create({
      store: this
    });

    transaction = transaction || get(this, 'defaultTransaction');

    // adoptRecord is an internal API that allows records to move
    // into a transaction without assertions designed for app
    // code. It is used here to ensure that regardless of new
    // restrictions on the use of the public `transaction.add()`
    // API, we will always be able to insert new records into
    // their transaction.
    transaction.adoptRecord(record);

    // `id` is a special property that may not be a `DS.attr`
    var id = properties.id;

    // If the passed properties do not include a primary key,
    // give the adapter an opportunity to generate one. Typically,
    // client-side ID generators will use something like uuid.js
    // to avoid conflicts.
    var adapter;
    if (Ember.none(id)) {
      adapter = get(this, 'adapter');
      if (adapter && adapter.generateIdForRecord) {
        id = coerceId(adapter.generateIdForRecord(this, record));
        properties.id = id;
      }
    }

    id = coerceId(id);

    // Create a new `clientId` and associate it with the
    // specified (or generated) `id`. Since we don't have
    // any data for the server yet (by definition), store
    // the sentinel value CREATED as the data for this
    // clientId. If we see this value later, we will skip
    // materialization.
    var clientId = this.pushData(CREATED, id, type);

    // Now that we have a clientId, attach it to the record we
    // just created.
    set(record, 'clientId', clientId);

    // Move the record out of its initial `empty` state into
    // the `loaded` state.
    record.loadedData();

    // Store the record we just created in the record cache for
    // this clientId.
    this.recordCache[clientId] = record;

    // Set the properties specified on the record.
    record.setProperties(properties);

    return record;
  },

  // .................
  // . DELETE RECORD .
  // .................

  /**
    For symmetry, a record can be deleted via the store.

    @param {DS.Model} record
  */
  deleteRecord: function(record) {
    record.deleteRecord();
  },

  /**
    For symmetry, a record can be unloaded via the store.

    @param {DS.Model} record
  */
  unloadRecord: function(record) {
    record.unloadRecord();
  },

  // ................
  // . FIND RECORDS .
  // ................

  /**
    This is the main entry point into finding records. The first parameter to
    this method is always a subclass of `DS.Model`.

    You can use the `find` method on a subclass of `DS.Model` directly if your
    application only has one store. For example, instead of
    `store.find(App.Person, 1)`, you could say `App.Person.find(1)`.

    ---

    To find a record by ID, pass the `id` as the second parameter:

        store.find(App.Person, 1);
        App.Person.find(1);

    If the record with that `id` had not previously been loaded, the store will
    return an empty record immediately and ask the adapter to find the data by
    calling the adapter's `find` method.

    The `find` method will always return the same object for a given type and
    `id`. To check whether the adapter has populated a record, you can check
    its `isLoaded` property.

    ---

    To find all records for a type, call `find` with no additional parameters:

        store.find(App.Person);
        App.Person.find();

    This will return a `RecordArray` representing all known records for the
    given type and kick off a request to the adapter's `findAll` method to load
    any additional records for the type.

    The `RecordArray` returned by `find()` is live. If any more records for the
    type are added at a later time through any mechanism, it will automatically
    update to reflect the change.

    ---

    To find a record by a query, call `find` with a hash as the second
    parameter:

        store.find(App.Person, { page: 1 });
        App.Person.find({ page: 1 });

    This will return a `RecordArray` immediately, but it will always be an
    empty `RecordArray` at first. It will call the adapter's `findQuery`
    method, which will populate the `RecordArray` once the server has returned
    results.

    You can check whether a query results `RecordArray` has loaded by checking
    its `isLoaded` property.
  */
  find: function(type, id) {
    if (id === undefined) {
      return this.findAll(type);
    }

    // We are passed a query instead of an id.
    if (Ember.typeOf(id) === 'object') {
      return this.findQuery(type, id);
    }

    return this.findById(type, coerceId(id));
  },

  /**
    @private

    This method returns a record for a given type and id combination.

    If the store has never seen this combination of type and id before, it
    creates a new `clientId` with the LOADING sentinel and asks the adapter to
    load the data.

    If the store has seen the combination, this method delegates to
    `findByClientId`.
  */
  findById: function(type, id) {
    var clientId = this.typeMapFor(type).idToCid[id];

    if (clientId) {
      return this.findByClientId(type, clientId);
    }

    clientId = this.pushData(LOADING, id, type);

    // create a new instance of the model type in the
    // 'isLoading' state
    var record = this.materializeRecord(type, clientId, id);

    // let the adapter set the data, possibly async
    var adapter = this.adapterForType(type);
    if (adapter && adapter.find) { adapter.find(this, type, id); }
    else { throw "Adapter is either null or does not implement `find` method"; }

    return record;
  },

  /**
    @private

    This method returns a record for a given clientId.

    If there is no record object yet for the clientId, this method materializes
    a new record object. This allows adapters to eagerly load large amounts of
    data into the store, and avoid incurring the cost to create the objects
    until they are requested.

    Several parts of Ember Data call this method:

    * findById, if a clientId already exists for a given type and
      id combination
    * OneToManyChange, which is backed by clientIds, when getChild,
      getOldParent or getNewParent are called
    * RecordArray, which is backed by clientIds, when an object at
      a particular index is looked up

    In short, it's a convenient way to get a record for a known
    clientId, materializing it if necessary.

    @param {Class} type
    @param {Number|String} clientId
  */
  findByClientId: function(type, clientId) {
    var cidToData, record, id;

    record = this.recordCache[clientId];

    if (!record) {
      // create a new instance of the model type in the
      // 'isLoading' state
      id = this.clientIdToId[clientId];
      record = this.materializeRecord(type, clientId, id);

      cidToData = this.clientIdToData;

      if (typeof cidToData[clientId] === 'object') {
        record.loadedData();
      }
    }

    return record;
  },

  /**
    @private

    Given a type and array of `clientId`s, determines which of those
    `clientId`s has not yet been loaded.

    In preparation for loading, this method also marks any unloaded
    `clientId`s as loading.
  */
  neededClientIds: function(type, clientIds) {
    var neededClientIds = [],
        cidToData = this.clientIdToData,
        clientId;

    for (var i=0, l=clientIds.length; i<l; i++) {
      clientId = clientIds[i];
      if (cidToData[clientId] === UNLOADED) {
        neededClientIds.push(clientId);
        cidToData[clientId] = LOADING;
      }
    }

    return neededClientIds;
  },

  /**
    @private

    This method is the entry point that associations use to update
    themselves when their underlying data changes.

    First, it determines which of its `clientId`s are still unloaded,
    then converts the needed `clientId`s to IDs and invokes `findMany`
    on the adapter.
  */
  fetchUnloadedClientIds: function(type, clientIds) {
    var neededClientIds = this.neededClientIds(type, clientIds);
    this.fetchMany(type, neededClientIds);
  },

  /**
    @private

    This method takes a type and list of `clientId`s, converts the
    `clientId`s into IDs, and then invokes the adapter's `findMany`
    method.

    It is used both by a brand new association (via the `findMany`
    method) or when the data underlying an existing association
    changes (via the `fetchUnloadedClientIds` method).
  */
  fetchMany: function(type, clientIds) {
    var clientIdToId = this.clientIdToId;

    var neededIds = map(clientIds, function(clientId) {
      return clientIdToId[clientId];
    });

    if (!neededIds.length) { return; }

    var adapter = this.adapterForType(type);
    if (adapter && adapter.findMany) { adapter.findMany(this, type, neededIds); }
    else { throw "Adapter is either null or does not implement `findMany` method"; }
  },

  /**
    @private

    `findMany` is the entry point that associations use to generate a
    new `ManyArray` for the list of IDs specified by the server for
    the association.

    Its responsibilities are:

    * convert the IDs into clientIds
    * determine which of the clientIds still need to be loaded
    * create a new ManyArray whose content is *all* of the clientIds
    * notify the ManyArray of the number of its elements that are
      already loaded
    * insert the unloaded clientIds into the `loadingRecordArrays`
      bookkeeping structure, which will allow the `ManyArray` to know
      when all of its loading elements are loaded from the server.
    * ask the adapter to load the unloaded elements, by invoking
      findMany with the still-unloaded IDs.
  */
  findMany: function(type, ids, record, relationship) {
    // 1. Convert ids to client ids
    // 2. Determine which of the client ids need to be loaded
    // 3. Create a new ManyArray whose content is ALL of the clientIds
    // 4. Decrement the ManyArray's counter by the number of loaded clientIds
    // 5. Put the ManyArray into our bookkeeping data structure, keyed on
    //    the needed clientIds
    // 6. Ask the adapter to load the records for the unloaded clientIds (but
    //    convert them back to ids)

    if (!Ember.isArray(ids)) {
      var adapter = this.adapterForType(type);
      if (adapter && adapter.findAssociation) { adapter.findAssociation(this, record, relationship, ids); }
      else { throw fmt("Adapter is either null or does not implement `findMany` method", this); }

      return this.createManyArray(type, Ember.A());
    }

    ids = map(ids, function(id) { return coerceId(id); });
    var clientIds = this.clientIdsForIds(type, ids);

    var neededClientIds = this.neededClientIds(type, clientIds),
        manyArray = this.createManyArray(type, Ember.A(clientIds)),
        loadingRecordArrays = this.loadingRecordArrays,
        clientId, i, l;

    // Start the decrementing counter on the ManyArray at the number of
    // records we need to load from the adapter
    manyArray.loadingRecordsCount(neededClientIds.length);

    if (neededClientIds.length) {
      for (i=0, l=neededClientIds.length; i<l; i++) {
        clientId = neededClientIds[i];

        // keep track of the record arrays that a given loading record
        // is part of. This way, if the same record is in multiple
        // ManyArrays, all of their loading records counters will be
        // decremented when the adapter provides the data.
        if (loadingRecordArrays[clientId]) {
          loadingRecordArrays[clientId].push(manyArray);
        } else {
          this.loadingRecordArrays[clientId] = [ manyArray ];
        }
      }

      this.fetchMany(type, neededClientIds);
    } else {
      // all requested records are available
      manyArray.set('isLoaded', true);
    }

    return manyArray;
  },

  /**
    @private

    This method delegates a query to the adapter. This is the one place where
    adapter-level semantics are exposed to the application.

    Exposing queries this way seems preferable to creating an abstract query
    language for all server-side queries, and then require all adapters to
    implement them.

    @param {Class} type
    @param {Object} query an opaque query to be used by the adapter
    @return {DS.AdapterPopulatedRecordArray}
  */
  findQuery: function(type, query) {
    var array = DS.AdapterPopulatedRecordArray.create({ type: type, query: query, content: Ember.A([]), store: this });
    var adapter = this.adapterForType(type);
    if (adapter && adapter.findQuery) { adapter.findQuery(this, type, query, array); }
    else { throw "Adapter is either null or does not implement `findQuery` method"; }
    return array;
  },

  /**
    @private

    This method returns an array of all records adapter can find.
    It triggers the adapter's `findAll` method to give it an opportunity to populate
    the array with records of that type.

    @param {Class} type
    @return {DS.AdapterPopulatedRecordArray}
  */
  findAll: function(type) {
    var array = this.all(type);
    this.fetchAll(type, array);
    return array;
  },

  /**
    @private
  */
  fetchAll: function(type, array) {
    var sinceToken = this.typeMapFor(type).sinceToken,
        adapter = this.adapterForType(type);

    set(array, 'isUpdating', true);

    if (adapter && adapter.findAll) { adapter.findAll(this, type, sinceToken); }
    else { throw "Adapter is either null or does not implement `findAll` method"; }
  },

  /**
  */
  sinceForType: function(type, sinceToken) {
    this.typeMapFor(type).sinceToken = sinceToken;
  },

  /**
  */
  didUpdateAll: function(type) {
    var findAllCache = this.typeMapFor(type).findAllCache;
    set(findAllCache, 'isUpdating', false);
  },

  /**
    This method returns a filtered array that contains all of the known records
    for a given type.

    Note that because it's just a filter, it will have any locally
    created records of the type.

    Also note that multiple calls to `all` for a given type will always
    return the same RecordArray.

    @param {Class} type
    @return {DS.RecordArray}
  */
  all: function(type) {
    var typeMap = this.typeMapFor(type),
        findAllCache = typeMap.findAllCache;

    if (findAllCache) { return findAllCache; }

    var array = DS.RecordArray.create({ type: type, content: Ember.A([]), store: this, isLoaded: true });
    this.registerRecordArray(array, type);

    typeMap.findAllCache = array;
    return array;
  },

  /**
    Takes a type and filter function, and returns a live RecordArray that
    remains up to date as new records are loaded into the store or created
    locally.

    The callback function takes a materialized record, and returns true
    if the record should be included in the filter and false if it should
    not.

    The filter function is called once on all records for the type when
    it is created, and then once on each newly loaded or created record.

    If any of a record's properties change, or if it changes state, the
    filter function will be invoked again to determine whether it should
    still be in the array.

    Note that the existence of a filter on a type will trigger immediate
    materialization of all loaded data for a given type, so you might
    not want to use filters for a type if you are loading many records
    into the store, many of which are not active at any given time.

    In this scenario, you might want to consider filtering the raw
    data before loading it into the store.

    @param {Class} type
    @param {Function} filter

    @return {DS.FilteredRecordArray}
  */
  filter: function(type, query, filter) {
    // allow an optional server query
    if (arguments.length === 3) {
      this.findQuery(type, query);
    } else if (arguments.length === 2) {
      filter = query;
    }

    var array = DS.FilteredRecordArray.create({ type: type, content: Ember.A([]), store: this, filterFunction: filter });

    this.registerRecordArray(array, type, filter);

    return array;
  },

  /**
    TODO: What is this method trying to do?
  */
  recordIsLoaded: function(type, id) {
    return !Ember.none(this.typeMapFor(type).idToCid[id]);
  },

  // ............
  // . UPDATING .
  // ............

  /**
    @private

    If the adapter updates attributes or acknowledges creation
    or deletion, the record will notify the store to update its
    membership in any filters.

    To avoid thrashing, this method is invoked only once per
    run loop per record.

    @param {Class} type
    @param {Number|String} clientId
    @param {DS.Model} record
  */
  dataWasUpdated: function(type, clientId, record) {
    // Because data updates are invoked at the end of the run loop,
    // it is possible that a record might be deleted after its data
    // has been modified and this method was scheduled to be called.
    //
    // If that's the case, the record would have already been removed
    // from all record arrays; calling updateRecordArrays would just
    // add it back. If the record is deleted, just bail. It shouldn't
    // give us any more trouble after this.

    if (get(record, 'isDeleted')) { return; }

    var cidToData = this.clientIdToData,
        data = cidToData[clientId];

    if (typeof data === "object") {
      this.updateRecordArrays(type, clientId);
    }
  },

  // ..............
  // . PERSISTING .
  // ..............

  /**
    This method delegates committing to the store's implicit
    transaction.

    Calling this method is essentially a request to persist
    any changes to records that were not explicitly added to
    a transaction.
  */
  commit: function() {
    get(this, 'defaultTransaction').commit();
  },

  /**
    Adapters should call this method if they would like to acknowledge
    that all changes related to a record (other than relationship
    changes) have persisted.

    Because relationship changes affect multiple records, the adapter
    is responsible for acknowledging the change to the relationship
    directly (using `store.didUpdateRelationship`) when all aspects
    of the relationship change have persisted.

    It can be called for created, deleted or updated records.

    If the adapter supplies new data, that data will become the new
    canonical data for the record. That will result in blowing away
    all local changes and rematerializing the record with the new
    data (the "sledgehammer" approach).

    Alternatively, if the adapter does not supply new data, the record
    will collapse all local changes into its saved data. Subsequent
    rollbacks of the record will roll back to this point.

    If an adapter is acknowledging receipt of a newly created record
    that did not generate an id in the client, it *must* either
    provide data or explicitly invoke `store.didReceiveId` with
    the server-provided id.

    Note that an adapter may not supply new data when acknowledging
    a deleted record.

    @see DS.Store#didUpdateRelationship

    @param {DS.Model} record the in-flight record
    @param {Object} data optional data (see above)
  */
  didSaveRecord: function(record, data) {
    record.adapterDidCommit();

    if (data) {
      this.updateId(record, data);
      this.updateRecordData(record, data);
    } else {
      this.didUpdateAttributes(record);
    }
  },

  /**
    For convenience, if an adapter is performing a bulk commit, it can also
    acknowledge all of the records at once.

    If the adapter supplies an array of data, they must be in the same order as
    the array of records passed in as the first parameter.

    @param {#forEach} list a list of records whose changes the
      adapter is acknowledging. You can pass any object that
      has an ES5-like `forEach` method, including the
      `OrderedSet` objects passed into the adapter at commit
      time.
    @param {Array[Object]} dataList an Array of data. This
      parameter must be an integer-indexed Array-like.
  */
  didSaveRecords: function(list, dataList) {
    var i = 0;
    list.forEach(function(record) {
      this.didSaveRecord(record, dataList && dataList[i++]);
    }, this);
  },

  /**
    This method allows the adapter to specify that a record
    could not be saved because it had backend-supplied validation
    errors.

    The errors object must have keys that correspond to the
    attribute names. Once each of the specified attributes have
    changed, the record will automatically move out of the
    invalid state and be ready to commit again.

    TODO: We should probably automate the process of converting
    server names to attribute names using the existing serializer
    infrastructure.

    @param {DS.Model} record
    @param {Object} errors
  */
  recordWasInvalid: function(record, errors) {
    record.adapterDidInvalidate(errors);
  },

  /**
     This method allows the adapter to specify that a record
     could not be saved because the server returned an unhandled
     error.

     @param {DS.Model} record
  */
  recordWasError: function(record) {
    record.adapterDidError();
  },

  /**
    This is a lower-level API than `didSaveRecord` that allows an
    adapter to acknowledge the persistence of a single attribute.

    This is useful if an adapter needs to make multiple asynchronous
    calls to fully persist a record. The record will keep track of
    which attributes and relationships are still outstanding and
    automatically move into the `saved` state once the adapter has
    acknowledged everything.

    If a value is provided, it clobbers the locally specified value.
    Otherwise, the local value becomes the record's last known
    saved value (which is used when rolling back a record).

    Note that the specified attributeName is the normalized name
    specified in the definition of the `DS.Model`, not a key in
    the server-provided data.

    Also note that the adapter is responsible for performing any
    transformations on the value using the serializer API.

    @param {DS.Model} record
    @param {String} attributeName
    @param {Object} value
  */
  didUpdateAttribute: function(record, attributeName, value) {
    record.adapterDidUpdateAttribute(attributeName, value);
  },

  /**
    This method allows an adapter to acknowledge persistence
    of all attributes of a record but not relationships or
    other factors.

    It loops through the record's defined attributes and
    notifies the record that they are all acknowledged.

    This method does not take optional values, because
    the adapter is unlikely to have a hash of normalized
    keys and transformed values, and instead of building
    one up, it should just call `didUpdateAttribute` as
    needed.

    This method is intended as a middle-ground between
    `didSaveRecord`, which acknowledges all changes to
    a record, and `didUpdateAttribute`, which allows an
    adapter fine-grained control over updates.

    @param {DS.Model} record
  */
  didUpdateAttributes: function(record) {
    record.eachAttribute(function(attributeName) {
      this.didUpdateAttribute(record, attributeName);
    }, this);
  },

  /**
    This allows an adapter to acknowledge that it has saved all
    necessary aspects of a relationship change.

    This is separated from acknowledging the record itself
    (via `didSaveRecord`) because a relationship change can
    involve as many as three separate records. Records should
    only move out of the in-flight state once the server has
    acknowledged all of their relationships, and this differs
    based upon the adapter's semantics.

    There are three basic scenarios by which an adapter can
    save a relationship.

    ### Foreign Key

    An adapter can save all relationship changes by updating
    a foreign key on the child record. If it does this, it
    should acknowledge the changes when the child record is
    saved.

        record.eachAssociation(function(name, meta) {
          if (meta.kind === 'belongsTo') {
            store.didUpdateRelationship(record, name);
          }
        });

        store.didSaveRecord(record, data);

    ### Embedded in Parent

    An adapter can save one-to-many relationships by embedding
    IDs (or records) in the parent object. In this case, the
    relationship is not considered acknowledged until both the
    old parent and new parent have acknowledged the change.

    In this case, the adapter should keep track of the old
    parent and new parent, and acknowledge the relationship
    change once both have acknowledged. If one of the two
    sides does not exist (e.g. the new parent does not exist
    because of nulling out the belongs-to relationship),
    the adapter should acknowledge the relationship once
    the other side has acknowledged.

    ### Separate Entity

    An adapter can save relationships as separate entities
    on the server. In this case, they should acknowledge
    the relationship as saved once the server has
    acknowledged the entity.

    @see DS.Store#didSaveRecord

    @param {DS.Model} record
    @param {DS.Model} relationshipName
  */
  didUpdateRelationship: function(record, relationshipName) {
    var relationship = this.relationshipChangeFor(get(record, 'clientId'), relationshipName);
    if (relationship) { relationship.adapterDidUpdate(); }
  },

  materializeHasMany: function(record, name, ids) {
    record.materializeHasMany(name, ids);
    record.adapterDidUpdateHasMany(name);
  },

  /**
    This allows an adapter to acknowledge all relationship changes
    for a given record.

    Like `didUpdateAttributes`, this is intended as a middle ground
    between `didSaveRecord` and fine-grained control via the
    `didUpdateRelationship` API.
  */
  didUpdateRelationships: function(record) {
    var changes = this.relationshipChangesFor(get(record, 'clientId'));

    for (var name in changes) {
      if (!changes.hasOwnProperty(name)) { continue; }
      changes[name].adapterDidUpdate();
    }
  },

  /**
    When acknowledging the creation of a locally created record,
    adapters must supply an id (if they did not implement
    `generateIdForRecord` to generate an id locally).

    If an adapter does not use `didSaveRecord` and supply a hash
    (for example, if it needs to make multiple HTTP requests to
    create and then update the record), it will need to invoke
    `didReceiveId` with the backend-supplied id.

    When not using `didSaveRecord`, an adapter will need to
    invoke:

    * didReceiveId (unless the id was generated locally)
    * didCreateRecord
    * didUpdateAttribute(s)
    * didUpdateRelationship(s)

    @param {DS.Model} record
    @param {Number|String} id
  */
  didReceiveId: function(record, id) {
    var typeMap = this.typeMapFor(record.constructor),
        clientId = get(record, 'clientId'),
        oldId = get(record, 'id');

    Ember.assert("An adapter cannot assign a new id to a record that already has an id. " + record + " had id: " + oldId + " and you tried to update it with " + id + ". This likely happened because your server returned data in response to a find or update that had a different id than the one you sent.", oldId === undefined || id === oldId);

    typeMap.idToCid[id] = clientId;
    this.clientIdToId[clientId] = id;
  },

  /**
    @private

    This method re-indexes the data by its clientId in the store
    and then notifies the record that it should rematerialize
    itself.

    @param {DS.Model} record
    @param {Object} data
  */
  updateRecordData: function(record, data) {
    var clientId = get(record, 'clientId'),
        cidToData = this.clientIdToData;

    cidToData[clientId] = data;

    record.didChangeData();
  },

  /**
    @private

    If an adapter invokes `didSaveRecord` with data, this method
    extracts the id from the supplied data (using the adapter's
    `extractId()` method) and indexes the clientId with that id.

    @param {DS.Model} record
    @param {Object} data
  */
  updateId: function(record, data) {
    var typeMap = this.typeMapFor(record.constructor),
        clientId = get(record, 'clientId'),
        oldId = get(record, 'id'),
        type = record.constructor,
        id = this.preprocessData(type, data);

    Ember.assert("An adapter cannot assign a new id to a record that already has an id. " + record + " had id: " + oldId + " and you tried to update it with " + id + ". This likely happened because your server returned data in response to a find or update that had a different id than the one you sent.", oldId === undefined || id === oldId);

    typeMap.idToCid[id] = clientId;
    this.clientIdToId[clientId] = id;
  },

  /**
    @private

    This method receives opaque data provided by the adapter and
    preprocesses it, returning an ID.

    The actual preprocessing takes place in the adapter. If you would
    like to change the default behavior, you should override the
    appropriate hooks in `DS.Serializer`.

    @see {DS.Serializer}
    @return {String} id the id represented by the data
  */
  preprocessData: function(type, data) {
    this.adapterForType(type).extractEmbeddedData(this, type, data);
    return this.adapterForType(type).extractId(type, data);
  },

  // .................
  // . RECORD ARRAYS .
  // .................

  /**
    @private

    Register a RecordArray for a given type to be backed by
    a filter function. This will cause the array to update
    automatically when records of that type change attribute
    values or states.

    @param {DS.RecordArray} array
    @param {Class} type
    @param {Function} filter
  */
  registerRecordArray: function(array, type, filter) {
    var recordArrays = this.typeMapFor(type).recordArrays;

    recordArrays.push(array);

    this.updateRecordArrayFilter(array, type, filter);
  },

  /**
    @private

    Create a `DS.ManyArray` for a type and list of clientIds
    and index the `ManyArray` under each clientId. This allows
    us to efficiently remove records from `ManyArray`s when
    they are deleted.

    @param {Class} type
    @param {Array} clientIds

    @return {DS.ManyArray}
  */
  createManyArray: function(type, clientIds) {
    var array = DS.ManyArray.create({ type: type, content: clientIds, store: this });

    clientIds.forEach(function(clientId) {
      var recordArrays = this.recordArraysForClientId(clientId);
      recordArrays.add(array);
    }, this);

    return array;
  },

  /**
    @private

    This method is invoked if the `filterFunction` property is
    changed on a `DS.FilteredRecordArray`.

    It essentially re-runs the filter from scratch. This same
    method is invoked when the filter is created in th first place.
  */
  updateRecordArrayFilter: function(array, type, filter) {
    var typeMap = this.typeMapFor(type),
        cidToData = this.clientIdToData,
        clientIds = typeMap.clientIds,
        clientId, data, shouldFilter, record;

    for (var i=0, l=clientIds.length; i<l; i++) {
      clientId = clientIds[i];
      shouldFilter = false;

      data = cidToData[clientId];

      if (typeof data === 'object') {
        if (record = this.recordCache[clientId]) {
          if (!get(record, 'isDeleted')) { shouldFilter = true; }
        } else {
          shouldFilter = true;
        }

        if (shouldFilter) {
          this.updateRecordArray(array, filter, type, clientId);
        }
      }
    }
  },

  /**
    @private

    This method is invoked whenever data is loaded into the store
    by the adapter or updated by the adapter, or when an attribute
    changes on a record.

    It updates all filters that a record belongs to.

    To avoid thrashing, it only runs once per run loop per record.

    @param {Class} type
    @param {Number|String} clientId
  */
  updateRecordArrays: function(type, clientId) {
    var recordArrays = this.typeMapFor(type).recordArrays,
        filter;

    recordArrays.forEach(function(array) {
      filter = get(array, 'filterFunction');
      this.updateRecordArray(array, filter, type, clientId);
    }, this);

    // loop through all manyArrays containing an unloaded copy of this
    // clientId and notify them that the record was loaded.
    var manyArrays = this.loadingRecordArrays[clientId];

    if (manyArrays) {
      for (var i=0, l=manyArrays.length; i<l; i++) {
        manyArrays[i].loadedRecord();
      }

      this.loadingRecordArrays[clientId] = null;
    }
  },

  /**
    @private

    Update an individual filter.

    @param {DS.FilteredRecordArray} array
    @param {Function} filter
    @param {Class} type
    @param {Number|String} clientId
  */
  updateRecordArray: function(array, filter, type, clientId) {
    var shouldBeInArray, record;

    if (!filter) {
      shouldBeInArray = true;
    } else {
      record = this.findByClientId(type, clientId);
      shouldBeInArray = filter(record);
    }

    var content = get(array, 'content');
    var alreadyInArray = content.indexOf(clientId) !== -1;

    var recordArrays = this.recordArraysForClientId(clientId);

    if (shouldBeInArray && !alreadyInArray) {
      recordArrays.add(array);
      content.pushObject(clientId);
    } else if (!shouldBeInArray && alreadyInArray) {
      recordArrays.remove(array);
      content.removeObject(clientId);
    }
  },

  /**
    @private

    When a record is deleted, it is removed from all its
    record arrays.

    @param {DS.Model} record
  */
  removeFromRecordArrays: function(record) {
    var clientId = get(record, 'clientId');
    var recordArrays = this.recordArraysForClientId(clientId);

    recordArrays.forEach(function(array) {
      var content = get(array, 'content');
      content.removeObject(clientId);
    });
  },

  // ............
  // . INDEXING .
  // ............

  /**
    @private

    Return a list of all `DS.RecordArray`s a clientId is
    part of.

    @return {Object(clientId: Ember.OrderedSet)}
  */
  recordArraysForClientId: function(clientId) {
    var recordArrays = get(this, 'recordArraysByClientId');
    var ret = recordArrays[clientId];

    if (!ret) {
      ret = recordArrays[clientId] = Ember.OrderedSet.create();
    }

    return ret;
  },

  typeMapFor: function(type) {
    var typeMaps = get(this, 'typeMaps');
    var guidForType = Ember.guidFor(type);

    var typeMap = typeMaps[guidForType];

    if (typeMap) {
      return typeMap;
    } else {
      return (typeMaps[guidForType] =
        {
          idToCid: {},
          clientIds: [],
          recordArrays: []
      });
    }
  },

  /** @private

    For a given type and id combination, returns the client id used by the store.
    If no client id has been assigned yet, one will be created and returned.

    @param {DS.Model} type
    @param {String|Number} id
  */
  clientIdForId: function(type, id) {
    id = coerceId(id);

    var clientId = this.typeMapFor(type).idToCid[id];
    if (clientId !== undefined) { return clientId; }

    return this.pushData(UNLOADED, id, type);
  },

  /**
    @private

    This method works exactly like `clientIdForId`, but does not
    require looking up the `typeMap` for every `clientId` and
    invoking a method per `clientId`.
  */
  clientIdsForIds: function(type, ids) {
    var typeMap = this.typeMapFor(type),
        idToClientIdMap = typeMap.idToCid;

    return map(ids, function(id) {
      id = coerceId(id);

      var clientId = idToClientIdMap[id];
      if (clientId) { return clientId; }
      return this.pushData(UNLOADED, id, type);
    }, this);
  },

  typeForClientId: function(clientId) {
    return this.clientIdToType[clientId];
  },

  idForClientId: function(clientId) {
    return this.clientIdToId[clientId];
  },

  // ................
  // . LOADING DATA .
  // ................

  /**
    Load new data into the store for a given id and type combination.
    If data for that record had been loaded previously, the new information
    overwrites the old.

    If the record you are loading data for has outstanding changes that have not
    yet been saved, an exception will be thrown.

    @param {DS.Model} type
    @param {String|Number} id
    @param {Object} data the data to load
  */
  load: function(type, id, data) {
    if (data === undefined) {
      data = id;

      var adapter = this.adapterForType(type);
      id = this.preprocessData(type, data);
    }

    id = coerceId(id);

    var typeMap = this.typeMapFor(type),
        cidToData = this.clientIdToData,
        clientId = typeMap.idToCid[id];

    if (clientId !== undefined) {
      cidToData[clientId] = data;

      var record = this.recordCache[clientId];
      if (record) {
        record.loadedData();
      }
    } else {
      clientId = this.pushData(data, id, type);
    }

    this.updateRecordArrays(type, clientId);

    return { id: id, clientId: clientId };
  },

  loadMany: function(type, ids, dataList) {
    var clientIds = Ember.A([]);

    if (dataList === undefined) {
      dataList = ids;
      ids = [];

      var adapter = this.adapterForType(type);

      ids = map(dataList, function(data) {
        return this.preprocessData(type, data);
      }, this);
    }

    for (var i=0, l=get(ids, 'length'); i<l; i++) {
      var loaded = this.load(type, ids[i], dataList[i]);
      clientIds.pushObject(loaded.clientId);
    }

    return { clientIds: clientIds, ids: ids };
  },

  /** @private

    Stores data for the specified type and id combination and returns
    the client id.

    @param {Object} data
    @param {String|Number} id
    @param {DS.Model} type
    @returns {Number}
  */
  pushData: function(data, id, type) {
    var typeMap = this.typeMapFor(type);

    var idToClientIdMap = typeMap.idToCid,
        clientIdToIdMap = this.clientIdToId,
        clientIdToTypeMap = this.clientIdToType,
        clientIds = typeMap.clientIds,
        cidToData = this.clientIdToData;

    var clientId = ++this.clientIdCounter;

    cidToData[clientId] = data;
    clientIdToTypeMap[clientId] = type;

    // if we're creating an item, this process will be done
    // later, once the object has been persisted.
    if (id) {
      idToClientIdMap[id] = clientId;
      clientIdToIdMap[clientId] = id;
    }

    clientIds.push(clientId);

    return clientId;
  },

  // ..........................
  // . RECORD MATERIALIZATION .
  // ..........................

  materializeRecord: function(type, clientId, id) {
    var record;

    this.recordCache[clientId] = record = type._create({
      store: this,
      clientId: clientId,
    });

    set(record, 'id', id);

    get(this, 'defaultTransaction').adoptRecord(record);

    record.loadingData();
    return record;
  },

  dematerializeRecord: function(record) {
    var id = get(record, 'id'),
        clientId = get(record, 'clientId'),
        type = this.typeForClientId(clientId),
        typeMap = this.typeMapFor(type);

    record.updateRecordArrays();

    delete this.recordCache[clientId];
    delete this.clientIdToId[clientId];
    delete this.clientIdToType[clientId];
    delete this.clientIdToData[clientId];
    delete this.recordArraysByClientId[clientId];

    if (id) { delete typeMap.idToCid[id]; }
  },

  destroy: function() {
    if (get(DS, 'defaultStore') === this) {
      set(DS, 'defaultStore', null);
    }

    return this._super();
  },

  // ........................
  // . RELATIONSHIP CHANGES .
  // ........................

  addRelationshipChangeFor: function(clientId, key, change) {
    var changes = this.relationshipChanges;
    if (!(clientId in changes)) {
      changes[clientId] = {};
    }

    changes[clientId][key] = change;
  },

  removeRelationshipChangeFor: function(clientId, key) {
    var changes = this.relationshipChanges;
    if (!(clientId in changes)) {
      return;
    }

    delete changes[clientId][key];
  },

  relationshipChangeFor: function(clientId, key) {
    var changes = this.relationshipChanges;
    if (!(clientId in changes)) {
      return;
    }

    return changes[clientId][key];
  },

  relationshipChangesFor: function(clientId) {
    return this.relationshipChanges[clientId];
  },

  // ......................
  // . PER-TYPE ADAPTERS
  // ......................

  adapterForType: function(type) {
    this._adaptersMap = this.createInstanceMapFor('adapters');

    var adapter = this._adaptersMap.get(type);
    if (adapter) { return adapter; }

    return this.get('_adapter');
  },

  // ..............................
  // . RECORD CHANGE NOTIFICATION .
  // ..............................
  recordAttributeDidChange: function(record, attributeName, newValue, oldValue) {
    var dirtySet = new Ember.OrderedSet(),
        adapter = this.adapterForType(record.constructor);

    if (adapter.dirtyRecordsForAttributeChange) {
      adapter.dirtyRecordsForAttributeChange(dirtySet, record, attributeName, newValue, oldValue);
    }

    dirtySet.forEach(function(record) {
      record.adapterDidDirty();
    });
  },

  recordBelongsToDidChange: function(dirtySet, child, relationship) {
    var adapter = this.adapterForType(child.constructor);

    if (adapter.dirtyRecordsForBelongsToChange) {
      adapter.dirtyRecordsForBelongsToChange(dirtySet, child, relationship);
    }
  },

  recordHasManyDidChange: function(dirtySet, parent, relationship) {
    var adapter = this.adapterForType(parent.constructor);

    if (adapter.dirtyRecordsForHasManyChange) {
      adapter.dirtyRecordsForHasManyChange(dirtySet, parent, relationship);
    }
  }
});

DS.Store.reopenClass({
  registerAdapter: DS._Mappable.generateMapFunctionFor('adapters', function(type, adapter, map) {
    map.set(type, adapter);
  }),

  transformMapKey: function(key) {
    if (typeof key === 'string') {
      var transformedKey;
      transformedKey = get(Ember.lookup, key);
      Ember.assert("Could not find model at path " + key, transformedKey);
      return transformedKey;
    } else {
      return key;
    }
  },

  transformMapValue: function(key, value) {
    if (Ember.Object.detect(value)) {
      return value.create();
    }

    return value;
  }
});

})();



(function() {
var get = Ember.get, set = Ember.set, guidFor = Ember.guidFor;

/**
  This file encapsulates the various states that a record can transition
  through during its lifecycle.

  ### State Manager

  A record's state manager explicitly tracks what state a record is in
  at any given time. For instance, if a record is newly created and has
  not yet been sent to the adapter to be saved, it would be in the
  `created.uncommitted` state.  If a record has had local modifications
  made to it that are in the process of being saved, the record would be
  in the `updated.inFlight` state. (These state paths will be explained
  in more detail below.)

  Events are sent by the record or its store to the record's state manager.
  How the state manager reacts to these events is dependent on which state
  it is in. In some states, certain events will be invalid and will cause
  an exception to be raised.

  States are hierarchical. For example, a record can be in the
  `deleted.start` state, then transition into the `deleted.inFlight` state.
  If a child state does not implement an event handler, the state manager
  will attempt to invoke the event on all parent states until the root state is
  reached. The state hierarchy of a record is described in terms of a path
  string. You can determine a record's current state by getting its manager's
  current state path:

      record.get('stateManager.currentPath');
      //=> "created.uncommitted"

  The `DS.Model` states are themselves stateless. What we mean is that,
  though each instance of a record also has a unique instance of a
  `DS.StateManager`, the hierarchical states that each of *those* points
  to is a shared data structure. For performance reasons, instead of each
  record getting its own copy of the hierarchy of states, each state
  manager points to this global, immutable shared instance. How does a
  state know which record it should be acting on?  We pass a reference to
  the current state manager as the first parameter to every method invoked
  on a state.

  The state manager passed as the first parameter is where you should stash
  state about the record if needed; you should never store data on the state
  object itself. If you need access to the record being acted on, you can
  retrieve the state manager's `record` property. For example, if you had
  an event handler `myEvent`:

      myEvent: function(manager) {
        var record = manager.get('record');
        record.doSomething();
      }

  For more information about state managers in general, see the Ember.js
  documentation on `Ember.StateManager`.

  ### Events, Flags, and Transitions

  A state may implement zero or more events, flags, or transitions.

  #### Events

  Events are named functions that are invoked when sent to a record. The
  state manager will first look for a method with the given name on the
  current state. If no method is found, it will search the current state's
  parent, and then its grandparent, and so on until reaching the top of
  the hierarchy. If the root is reached without an event handler being found,
  an exception will be raised. This can be very helpful when debugging new
  features.

  Here's an example implementation of a state with a `myEvent` event handler:

      aState: DS.State.create({
        myEvent: function(manager, param) {
          console.log("Received myEvent with "+param);
        }
      })

  To trigger this event:

      record.send('myEvent', 'foo');
      //=> "Received myEvent with foo"

  Note that an optional parameter can be sent to a record's `send()` method,
  which will be passed as the second parameter to the event handler.

  Events should transition to a different state if appropriate. This can be
  done by calling the state manager's `transitionTo()` method with a path to the
  desired state. The state manager will attempt to resolve the state path
  relative to the current state. If no state is found at that path, it will
  attempt to resolve it relative to the current state's parent, and then its
  parent, and so on until the root is reached. For example, imagine a hierarchy
  like this:

      * created
        * start <-- currentState
        * inFlight
      * updated
        * inFlight

  If we are currently in the `start` state, calling
  `transitionTo('inFlight')` would transition to the `created.inFlight` state,
  while calling `transitionTo('updated.inFlight')` would transition to
  the `updated.inFlight` state.

  Remember that *only events* should ever cause a state transition. You should
  never call `transitionTo()` from outside a state's event handler. If you are
  tempted to do so, create a new event and send that to the state manager.

  #### Flags

  Flags are Boolean values that can be used to introspect a record's current
  state in a more user-friendly way than examining its state path. For example,
  instead of doing this:

      var statePath = record.get('stateManager.currentPath');
      if (statePath === 'created.inFlight') {
        doSomething();
      }

  You can say:

      if (record.get('isNew') && record.get('isSaving')) {
        doSomething();
      }

  If your state does not set a value for a given flag, the value will
  be inherited from its parent (or the first place in the state hierarchy
  where it is defined).

  The current set of flags are defined below. If you want to add a new flag,
  in addition to the area below, you will also need to declare it in the
  `DS.Model` class.

  #### Transitions

  Transitions are like event handlers but are called automatically upon
  entering or exiting a state. To implement a transition, just call a method
  either `enter` or `exit`:

      myState: DS.State.create({
        // Gets called automatically when entering
        // this state.
        enter: function(manager) {
          console.log("Entered myState");
        }
      })

   Note that enter and exit events are called once per transition. If the
   current state changes, but changes to another child state of the parent,
   the transition event on the parent will not be triggered.
*/

var stateProperty = Ember.computed(function(key) {
  var parent = get(this, 'parentState');
  if (parent) {
    return get(parent, key);
  }
}).property();

var isEmptyObject = function(object) {
  for (var name in object) {
    if (object.hasOwnProperty(name)) { return false; }
  }

  return true;
};

var hasDefinedProperties = function(object) {
  for (var name in object) {
    if (object.hasOwnProperty(name) && object[name]) { return true; }
  }

  return false;
};

var didChangeData = function(manager) {
  var record = get(manager, 'record');
  record.materializeData();
};

var setProperty = function(manager, context) {
  var record = get(manager, 'record'),
      store = get(record, 'store'),
      key = context.key,
      oldValue = context.oldValue,
      newValue = context.value;

  store.recordAttributeDidChange(record, key, newValue, oldValue);
};

// Whenever a property is set, recompute all dependent filters
var updateRecordArrays = function(manager) {
  var record = manager.get('record');
  record.updateRecordArraysLater();
};

DS.State = Ember.State.extend({
  isLoaded: stateProperty,
  isDirty: stateProperty,
  isSaving: stateProperty,
  isDeleted: stateProperty,
  isError: stateProperty,
  isNew: stateProperty,
  isValid: stateProperty,

  // For states that are substates of a
  // DirtyState (updated or created), it is
  // useful to be able to determine which
  // type of dirty state it is.
  dirtyType: stateProperty
});

// Implementation notes:
//
// Each state has a boolean value for all of the following flags:
//
// * isLoaded: The record has a populated `data` property. When a
//   record is loaded via `store.find`, `isLoaded` is false
//   until the adapter sets it. When a record is created locally,
//   its `isLoaded` property is always true.
// * isDirty: The record has local changes that have not yet been
//   saved by the adapter. This includes records that have been
//   created (but not yet saved) or deleted.
// * isSaving: The record's transaction has been committed, but
//   the adapter has not yet acknowledged that the changes have
//   been persisted to the backend.
// * isDeleted: The record was marked for deletion. When `isDeleted`
//   is true and `isDirty` is true, the record is deleted locally
//   but the deletion was not yet persisted. When `isSaving` is
//   true, the change is in-flight. When both `isDirty` and
//   `isSaving` are false, the change has persisted.
// * isError: The adapter reported that it was unable to save
//   local changes to the backend. This may also result in the
//   record having its `isValid` property become false if the
//   adapter reported that server-side validations failed.
// * isNew: The record was created on the client and the adapter
//   did not yet report that it was successfully saved.
// * isValid: No client-side validations have failed and the
//   adapter did not report any server-side validation failures.

// The dirty state is a abstract state whose functionality is
// shared between the `created` and `updated` states.
//
// The deleted state shares the `isDirty` flag with the
// subclasses of `DirtyState`, but with a very different
// implementation.
//
// Dirty states have three child states:
//
// `uncommitted`: the store has not yet handed off the record
//   to be saved.
// `inFlight`: the store has handed off the record to be saved,
//   but the adapter has not yet acknowledged success.
// `invalid`: the record has invalid information and cannot be
//   send to the adapter yet.
var DirtyState = DS.State.extend({
  initialState: 'uncommitted',

  // FLAGS
  isDirty: true,

  // SUBSTATES

  // When a record first becomes dirty, it is `uncommitted`.
  // This means that there are local pending changes, but they
  // have not yet begun to be saved, and are not invalid.
  uncommitted: DS.State.extend({
    // TRANSITIONS
    enter: function(manager) {
      var dirtyType = get(this, 'dirtyType'),
          record = get(manager, 'record');

      record.withTransaction(function (t) {
        t.recordBecameDirty(dirtyType, record);
      });
    },

    // EVENTS
    setProperty: setProperty,

    becomeDirty: Ember.K,

    willCommit: function(manager) {
      manager.transitionTo('inFlight');
    },

    becameClean: function(manager) {
      var record = get(manager, 'record'),
          dirtyType = get(this, 'dirtyType');

      record.withTransaction(function(t) {
        t.recordBecameClean(dirtyType, record);
      });

      manager.transitionTo('loaded.saved');
    },

    becameInvalid: function(manager) {
      var dirtyType = get(this, 'dirtyType'),
          record = get(manager, 'record');

      record.withTransaction(function (t) {
        t.recordBecameInFlight(dirtyType, record);
      });

      manager.transitionTo('invalid');
    },

    rollback: function(manager) {
      get(manager, 'record').rollback();
    }
  }),

  // Once a record has been handed off to the adapter to be
  // saved, it is in the 'in flight' state. Changes to the
  // record cannot be made during this window.
  inFlight: DS.State.extend({
    // FLAGS
    isSaving: true,

    // TRANSITIONS
    enter: function(manager) {
      var dirtyType = get(this, 'dirtyType'),
          record = get(manager, 'record');

      record.becameInFlight();

      record.withTransaction(function (t) {
        t.recordBecameInFlight(dirtyType, record);
      });
    },

    // EVENTS
    didCommit: function(manager) {
      var dirtyType = get(this, 'dirtyType'),
          record = get(manager, 'record');

      record.withTransaction(function(t) {
        t.recordBecameClean('inflight', record);
      });

      manager.transitionTo('saved');
      manager.send('invokeLifecycleCallbacks', dirtyType);
    },

    becameInvalid: function(manager, errors) {
      var record = get(manager, 'record');

      set(record, 'errors', errors);

      manager.transitionTo('invalid');
      manager.send('invokeLifecycleCallbacks');
    },

    becameError: function(manager) {
      manager.transitionTo('error');
      manager.send('invokeLifecycleCallbacks');
    }
  }),

  // A record is in the `invalid` state when its client-side
  // invalidations have failed, or if the adapter has indicated
  // the the record failed server-side invalidations.
  invalid: DS.State.extend({
    // FLAGS
    isValid: false,

    exit: function(manager) {
      var record = get(manager, 'record');

      record.withTransaction(function (t) {
        t.recordBecameClean('inflight', record);
      });
    },

    // EVENTS
    deleteRecord: function(manager) {
      manager.transitionTo('deleted');
      get(manager, 'record').clearRelationships();
    },

    setProperty: function(manager, context) {
      var record = get(manager, 'record'),
          errors = get(record, 'errors'),
          key = context.key;

      set(errors, key, null);

      if (!hasDefinedProperties(errors)) {
        manager.send('becameValid');
      }

      setProperty(manager, context);
    },

    becomeDirty: Ember.K,

    rollback: function(manager) {
      manager.send('becameValid');
      manager.send('rollback');
    },

    becameValid: function(manager) {
      manager.transitionTo('uncommitted');
    },

    invokeLifecycleCallbacks: function(manager) {
      var record = get(manager, 'record');
      record.trigger('becameInvalid', record);
    }
  })
});

// The created and updated states are created outside the state
// chart so we can reopen their substates and add mixins as
// necessary.

var createdState = DirtyState.create({
  dirtyType: 'created',

  // FLAGS
  isNew: true
});

var updatedState = DirtyState.create({
  dirtyType: 'updated'
});

createdState.states.uncommitted.reopen({
  deleteRecord: function(manager) {
    var record = get(manager, 'record');

    record.withTransaction(function(t) {
      t.recordIsMoving('created', record);
    });

    manager.transitionTo('deleted.saved');
    record.clearRelationships();
  }
});

createdState.states.uncommitted.reopen({
  rollback: function(manager) {
    this._super(manager);
    manager.transitionTo('deleted.saved');
  }
});

updatedState.states.uncommitted.reopen({
  deleteRecord: function(manager) {
    var record = get(manager, 'record');

    record.withTransaction(function(t) {
      t.recordIsMoving('updated', record);
    });

    manager.transitionTo('deleted');
    get(manager, 'record').clearRelationships();
  }
});

var states = {
  rootState: Ember.State.create({
    // FLAGS
    isLoaded: false,
    isDirty: false,
    isSaving: false,
    isDeleted: false,
    isError: false,
    isNew: false,
    isValid: true,

    // SUBSTATES

    // A record begins its lifecycle in the `empty` state.
    // If its data will come from the adapter, it will
    // transition into the `loading` state. Otherwise, if
    // the record is being created on the client, it will
    // transition into the `created` state.
    empty: DS.State.create({
      // EVENTS
      loadingData: function(manager) {
        manager.transitionTo('loading');
      },

      loadedData: function(manager) {
        manager.transitionTo('loaded.created');
      }
    }),

    // A record enters this state when the store askes
    // the adapter for its data. It remains in this state
    // until the adapter provides the requested data.
    //
    // Usually, this process is asynchronous, using an
    // XHR to retrieve the data.
    loading: DS.State.create({
      // TRANSITIONS
      exit: function(manager) {
        var record = get(manager, 'record');
        record.trigger('didLoad');
      },

      // EVENTS
      loadedData: function(manager) {
        didChangeData(manager);
        manager.transitionTo('loaded');
      }
    }),

    // A record enters this state when its data is populated.
    // Most of a record's lifecycle is spent inside substates
    // of the `loaded` state.
    loaded: DS.State.create({
      initialState: 'saved',

      // FLAGS
      isLoaded: true,

      // SUBSTATES

      // If there are no local changes to a record, it remains
      // in the `saved` state.
      saved: DS.State.create({

        // EVENTS
        setProperty: setProperty,
        didChangeData: didChangeData,
        loadedData: didChangeData,

        becomeDirty: function(manager) {
          manager.transitionTo('updated');
        },

        deleteRecord: function(manager) {
          manager.transitionTo('deleted');
          get(manager, 'record').clearRelationships();
        },

        unloadRecord: function(manager) {
          manager.transitionTo('deleted.saved');
          get(manager, 'record').clearRelationships();
        },

        willCommit: function(manager) {
          manager.transitionTo('relationshipsInFlight');
        },

        invokeLifecycleCallbacks: function(manager, dirtyType) {
          var record = get(manager, 'record');
          if (dirtyType === 'created') {
            record.trigger('didCreate', record);
          } else {
            record.trigger('didUpdate', record);
          }
        }
      }),

      relationshipsInFlight: Ember.State.create({
        // TRANSITIONS
        enter: function(manager) {
          var record = get(manager, 'record');

          record.withTransaction(function (t) {
            t.recordBecameInFlight('clean', record);
          });
        },

        // EVENTS
        didCommit: function(manager) {
          var record = get(manager, 'record');

          record.withTransaction(function(t) {
            t.recordBecameClean('inflight', record);
          });

          manager.transitionTo('saved');

          manager.send('invokeLifecycleCallbacks');
        }
      }),

      // A record is in this state after it has been locally
      // created but before the adapter has indicated that
      // it has been saved.
      created: createdState,

      // A record is in this state if it has already been
      // saved to the server, but there are new local changes
      // that have not yet been saved.
      updated: updatedState
    }),

    // A record is in this state if it was deleted from the store.
    deleted: DS.State.create({
      initialState: 'uncommitted',
      dirtyType: 'deleted',

      // FLAGS
      isDeleted: true,
      isLoaded: true,
      isDirty: true,

      // TRANSITIONS
      setup: function(manager) {
        var record = get(manager, 'record'),
            store = get(record, 'store');

        store.removeFromRecordArrays(record);
      },

      // SUBSTATES

      // When a record is deleted, it enters the `start`
      // state. It will exit this state when the record's
      // transaction starts to commit.
      uncommitted: DS.State.create({
        // TRANSITIONS
        enter: function(manager) {
          var record = get(manager, 'record');

          record.withTransaction(function(t) {
            t.recordBecameDirty('deleted', record);
          });
        },

        // EVENTS
        willCommit: function(manager) {
          manager.transitionTo('inFlight');
        },

        rollback: function(manager) {
          get(manager, 'record').rollback();
        },

        becomeDirty: Ember.K,

        becameClean: function(manager) {
          var record = get(manager, 'record');

          record.withTransaction(function(t) {
            t.recordBecameClean('deleted', record);
          });

          manager.transitionTo('loaded.saved');
        }
      }),

      // After a record's transaction is committing, but
      // before the adapter indicates that the deletion
      // has saved to the server, a record is in the
      // `inFlight` substate of `deleted`.
      inFlight: DS.State.create({
        // FLAGS
        isSaving: true,

        // TRANSITIONS
        enter: function(manager) {
          var record = get(manager, 'record');

          record.becameInFlight();

          record.withTransaction(function (t) {
            t.recordBecameInFlight('deleted', record);
          });
        },

        // EVENTS
        didCommit: function(manager) {
          var record = get(manager, 'record');

          record.withTransaction(function(t) {
            t.recordBecameClean('inflight', record);
          });

          manager.transitionTo('saved');

          manager.send('invokeLifecycleCallbacks');
        }
      }),

      // Once the adapter indicates that the deletion has
      // been saved, the record enters the `saved` substate
      // of `deleted`.
      saved: DS.State.create({
        // FLAGS
        isDirty: false,

        setup: function(manager) {
          var record = get(manager, 'record'),
              store = get(record, 'store');

          store.dematerializeRecord(record);
        },

        invokeLifecycleCallbacks: function(manager) {
          var record = get(manager, 'record');
          record.trigger('didDelete', record);
        }
      })
    }),

    // If the adapter indicates that there was an unknown
    // error saving a record, the record enters the `error`
    // state.
    error: DS.State.create({
      isError: true,

      // EVENTS

      invokeLifecycleCallbacks: function(manager) {
        var record = get(manager, 'record');
        record.trigger('becameError', record);
      }
    })
  })
};

DS.StateManager = Ember.StateManager.extend({
  record: null,
  initialState: 'rootState',
  states: states
});

})();



(function() {
var get = Ember.get, set = Ember.set, none = Ember.none;

var retrieveFromCurrentState = Ember.computed(function(key) {
  return get(get(this, 'stateManager.currentState'), key);
}).property('stateManager.currentState');

DS.Model = Ember.Object.extend(Ember.Evented, {
  isLoaded: retrieveFromCurrentState,
  isDirty: retrieveFromCurrentState,
  isSaving: retrieveFromCurrentState,
  isDeleted: retrieveFromCurrentState,
  isError: retrieveFromCurrentState,
  isNew: retrieveFromCurrentState,
  isValid: retrieveFromCurrentState,

  clientId: null,
  transaction: null,
  stateManager: null,
  errors: null,

  /**
    Create a JSON representation of the record, using the serialization
    strategy of the store's adapter.

    Available options:

    * `includeId`: `true` if the record's ID should be included in the
      JSON representation.

    @param {Object} options
    @returns {Object} an object whose values are primitive JSON values only
  */
  serialize: function(options) {
    var store = get(this, 'store');
    return store.serialize(this, options);
  },

  didLoad: Ember.K,
  didUpdate: Ember.K,
  didCreate: Ember.K,
  didDelete: Ember.K,
  becameInvalid: Ember.K,
  becameError: Ember.K,

  data: Ember.computed(function() {
    if (!this._data) {
      this.materializeData();
    }

    return this._data;
  }).property(),

  materializeData: function() {
    this.setupData();
    get(this, 'store').materializeData(this);

    this.suspendAssociationObservers(function() {
      this.notifyPropertyChange('data');
    });
  },

  _data: null,

  init: function() {
    var stateManager = DS.StateManager.create({ record: this });
    set(this, 'stateManager', stateManager);

    this.setup();

    stateManager.goToState('empty');
  },

  setup: function() {
    this._relationshipChanges = {};
  },

  send: function(name, context) {
    return get(this, 'stateManager').send(name, context);
  },

  withTransaction: function(fn) {
    var transaction = get(this, 'transaction');
    if (transaction) { fn(transaction); }
  },

  loadingData: function() {
    this.send('loadingData');
  },

  loadedData: function() {
    this.send('loadedData');
  },

  didChangeData: function() {
    this.send('didChangeData');
  },

  setProperty: function(key, value, oldValue) {
    this.send('setProperty', { key: key, value: value, oldValue: oldValue });
  },

  deleteRecord: function() {
    this.send('deleteRecord');
  },

  unloadRecord: function() {
    Ember.assert("You can only unload a loaded, non-dirty record.", !get(this, 'isDirty'));

    this.send('unloadRecord');
  },

  clearRelationships: function() {
    this.eachAssociation(function(name, relationship) {
      if (relationship.kind === 'belongsTo') {
        set(this, name, null);
      } else if (relationship.kind === 'hasMany') {
        get(this, name).clear();
      }
    }, this);
  },

  updateRecordArrays: function() {
    var store = get(this, 'store');
    if (store) {
      store.dataWasUpdated(this.constructor, get(this, 'clientId'), this);
    }
  },

  /**
    If the adapter did not return a hash in response to a commit,
    merge the changed attributes and associations into the existing
    saved data.
  */
  adapterDidCommit: function() {
    var attributes = get(this, 'data').attributes;

    get(this.constructor, 'attributes').forEach(function(name, meta) {
      attributes[name] = get(this, name);
    }, this);

    this.send('didCommit');
    this.updateRecordArraysLater();
  },

  adapterDidDirty: function() {
    this.send('becomeDirty');
    this.updateRecordArraysLater();
  },

  dataDidChange: Ember.observer(function() {
    var associations = get(this.constructor, 'associationsByName'),
        hasMany = get(this, 'data').hasMany, store = get(this, 'store'),
        idToClientId = store.idToClientId,
        cachedValue;

    this.updateRecordArraysLater();

    associations.forEach(function(name, association) {
      if (association.kind === 'hasMany') {
        cachedValue = this.cacheFor(name);

        if (cachedValue) {
          var key = name,
              ids = hasMany[key] || [];

          var clientIds;

          clientIds = Ember.EnumerableUtils.map(ids, function(id) {
            return store.clientIdForId(association.type, id);
          });

          set(cachedValue, 'content', Ember.A(clientIds));
        }
      }
    }, this);
  }, 'data'),

  updateRecordArraysLater: function() {
    Ember.run.once(this, this.updateRecordArrays);
  },

  setupData: function() {
    this._data = {
      attributes: {},
      belongsTo: {},
      hasMany: {},
      id: null
    };
  },

  materializeId: function(id) {
    set(this, 'id', id);
  },

  materializeAttributes: function(attributes) {
    Ember.assert("Must pass a hash of attributes to materializeAttributes", !!attributes);
    this._data.attributes = attributes;
  },

  materializeAttribute: function(name, value) {
    this._data.attributes[name] = value;
  },

  materializeHasMany: function(name, ids) {
    this._data.hasMany[name] = ids;
  },

  materializeBelongsTo: function(name, id) {
    this._data.belongsTo[name] = id;
  },

  rollback: function() {
    this.setup();
    this.send('becameClean');

    this.suspendAssociationObservers(function() {
      this.notifyPropertyChange('data');
    });
  },

  /**
    @private

    The goal of this method is to temporarily disable specific observers
    that take action in response to application changes.

    This allows the system to make changes (such as materialization and
    rollback) that should not trigger secondary behavior (such as setting an
    inverse relationship or marking records as dirty).

    The specific implementation will likely change as Ember proper provides
    better infrastructure for suspending groups of observers, and if Array
    observation becomes more unified with regular observers.
  */
  suspendAssociationObservers: function(callback, binding) {
    var observers = get(this.constructor, 'associationNames').belongsTo;
    var self = this;

    try {
      this._suspendedAssociations = true;
      Ember._suspendObservers(self, observers, null, 'belongsToDidChange', function() {
        Ember._suspendBeforeObservers(self, observers, null, 'belongsToWillChange', function() {
          callback.call(binding || self);
        });
      });
    } finally {
      this._suspendedAssociations = false;
    }
  },

  becameInFlight: function() {
  },

  // FOR USE DURING COMMIT PROCESS

  adapterDidUpdateAttribute: function(attributeName, value) {

    // If a value is passed in, update the internal attributes and clear
    // the attribute cache so it picks up the new value. Otherwise,
    // collapse the current value into the internal attributes because
    // the adapter has acknowledged it.
    if (value !== undefined) {
      get(this, 'data.attributes')[attributeName] = value;
      this.notifyPropertyChange(attributeName);
    } else {
      value = get(this, attributeName);
      get(this, 'data.attributes')[attributeName] = value;
    }

    this.updateRecordArraysLater();
  },

  adapterDidUpdateHasMany: function(name) {
    var cachedValue = this.cacheFor(name),
        hasMany = get(this, 'data').hasMany,
        store = get(this, 'store');

    var associations = get(this.constructor, 'associationsByName'),
        association = associations.get(name),
        idToClientId = store.idToClientId;

    if (cachedValue) {
      var key = name,
          ids = hasMany[key] || [];

      var clientIds;

      clientIds = Ember.EnumerableUtils.map(ids, function(id) {
        return store.clientIdForId(association.type, id);
      });

      set(cachedValue, 'content', Ember.A(clientIds));
      set(cachedValue, 'isLoaded', true);
    }

    this.updateRecordArraysLater();
  },

  adapterDidInvalidate: function(errors) {
    this.send('becameInvalid', errors);
  },

  adapterDidError: function() {
    this.send('becameError');
  },

  /**
    @private

    Override the default event firing from Ember.Evented to
    also call methods with the given name.
  */
  trigger: function(name) {
    Ember.tryInvoke(this, name, [].slice.call(arguments, 1));
    this._super.apply(this, arguments);
  }
});

// Helper function to generate store aliases.
// This returns a function that invokes the named alias
// on the default store, but injects the class as the
// first parameter.
var storeAlias = function(methodName) {
  return function() {
    var store = get(DS, 'defaultStore'),
        args = [].slice.call(arguments);

    args.unshift(this);
    return store[methodName].apply(store, args);
  };
};

DS.Model.reopenClass({
  isLoaded: storeAlias('recordIsLoaded'),
  find: storeAlias('find'),
  all: storeAlias('all'),
  filter: storeAlias('filter'),

  _create: DS.Model.create,

  create: function() {
    throw new Ember.Error("You should not call `create` on a model. Instead, call `createRecord` with the attributes you would like to set.");
  },

  createRecord: storeAlias('createRecord')
});

})();



(function() {
var get = Ember.get;
DS.Model.reopenClass({
  attributes: Ember.computed(function() {
    var map = Ember.Map.create();

    this.eachComputedProperty(function(name, meta) {
      if (meta.isAttribute) {
        Ember.assert("You may not set `id` as an attribute on your model. Please remove any lines that look like: `id: DS.attr('<type>')` from " + this.toString(), name !== 'id');

        meta.name = name;
        map.set(name, meta);
      }
    });

    return map;
  })
});

DS.Model.reopen({
  eachAttribute: function(callback, binding) {
    get(this.constructor, 'attributes').forEach(function(name, meta) {
      callback.call(binding, name, meta);
    }, binding);
  }
});

function getAttr(record, options, key) {
  var attributes = get(record, 'data').attributes;
  var value = attributes[key];

  if (value === undefined) {
    value = options.defaultValue;
  }

  return value;
}

DS.attr = function(type, options) {
  options = options || {};

  var meta = {
    type: type,
    isAttribute: true,
    options: options
  };

  return Ember.computed(function(key, value, oldValue) {
    var data;

    if (arguments.length > 1) {
      // TODO: If there is a cached oldValue, use it [tomhuda]
      oldValue = get(this, 'data.attributes')[key];
      Ember.assert("You may not set `id` as an attribute on your model. Please remove any lines that look like: `id: DS.attr('<type>')` from " + this.toString(), key !== 'id');
      this.setProperty(key, value, oldValue);
    } else {
      value = getAttr(this, options, key);
    }

    return value;
  // `data` is never set directly. However, it may be
  // invalidated from the state manager's setData
  // event.
  }).property('data').meta(meta);
};


})();



(function() {

})();



(function() {
var get = Ember.get, set = Ember.set,
    none = Ember.none;

DS.belongsTo = function(type, options) {
  Ember.assert("The first argument DS.belongsTo must be a model type or string, like DS.belongsTo(App.Person)", !!type && (typeof type === 'string' || DS.Model.detect(type)));

  options = options || {};

  var meta = { type: type, isAssociation: true, options: options, kind: 'belongsTo' };

  return Ember.computed(function(key, value) {
    if (arguments.length === 2) {
      return value === undefined ? null : value;
    }

    var data = get(this, 'data').belongsTo,
        store = get(this, 'store'), id;

    if (typeof type === 'string') {
      type = get(this, type, false) || get(Ember.lookup, type);
    }

    id = data[key];
    return id ? store.find(type, id) : null;
  }).property('data').meta(meta);
};

/**
  These observers observe all `belongsTo` relationships on the record. See
  `associations/ext` to see how these observers get their dependencies.

  The observers use `removeFromContent` and `addToContent` to avoid
  going through the public Enumerable API that would try to set the
  inverse (again) and trigger an infinite loop.
*/

DS.Model.reopen({
  /** @private */
  belongsToWillChange: Ember.beforeObserver(function(record, key) {
    if (get(record, 'isLoaded')) {
      var oldParent = get(record, key);

      var childId = get(record, 'clientId'),
          store = get(record, 'store');

      var change = DS.OneToManyChange.forChildAndParent(childId, store, { belongsToName: key });

      if (change.oldParent === undefined) {
        change.oldParent = oldParent ? get(oldParent, 'clientId') : null;
      }
    }
  }),

  /** @private */
  belongsToDidChange: Ember.immediateObserver(function(record, key) {
    if (get(record, 'isLoaded')) {
      var change = get(record, 'store').relationshipChangeFor(get(record, 'clientId'), key),
          newParent = get(record, key);

      change.newParent = newParent ? get(newParent, 'clientId') : null;
      change.sync();
    }
  })
});

})();



(function() {
var get = Ember.get, set = Ember.set;
var hasAssociation = function(type, options) {
  options = options || {};

  var meta = { type: type, isAssociation: true, options: options, kind: 'hasMany' };

  return Ember.computed(function(key, value) {
    var data = get(this, 'data').hasMany,
        store = get(this, 'store'),
        ids, association;

    if (typeof type === 'string') {
      type = get(this, type, false) || get(Ember.lookup, type);
    }

    ids = data[key];
    association = store.findMany(type, ids || [], this, meta);
    set(association, 'owner', this);
    set(association, 'name', key);

    return association;
  }).property().meta(meta);
};

DS.hasMany = function(type, options) {
  Ember.assert("The type passed to DS.hasMany must be defined", !!type);
  return hasAssociation(type, options);
};

})();



(function() {
var get = Ember.get, set = Ember.set;

/**
  @private

  This file defines several extensions to the base `DS.Model` class that
  add support for one-to-many relationships.
*/

DS.Model.reopen({
  // This Ember.js hook allows an object to be notified when a property
  // is defined.
  //
  // In this case, we use it to be notified when an Ember Data user defines a
  // belongs-to relationship. In that case, we need to set up observers for
  // each one, allowing us to track relationship changes and automatically
  // reflect changes in the inverse has-many array.
  //
  // This hook passes the class being set up, as well as the key and value
  // being defined. So, for example, when the user does this:
  //
  //   DS.Model.extend({
  //     parent: DS.belongsTo(App.User)
  //   });
  //
  // This hook would be called with "parent" as the key and the computed
  // property returned by `DS.belongsTo` as the value.
  didDefineProperty: function(proto, key, value) {
    // Check if the value being set is a computed property.
    if (value instanceof Ember.Descriptor) {

      // If it is, get the metadata for the association. This is
      // populated by the `DS.belongsTo` helper when it is creating
      // the computed property.
      var meta = value.meta();

      if (meta.isAssociation && meta.kind === 'belongsTo') {
        Ember.addObserver(proto, key, null, 'belongsToDidChange');
        Ember.addBeforeObserver(proto, key, null, 'belongsToWillChange');
      }
    }
  }
});

/**
  These DS.Model extensions add class methods that provide relationship
  introspection abilities about relationships.

  A note about the computed properties contained here:

  **These properties are effectively sealed once called for the first time.**
  To avoid repeatedly doing expensive iteration over a model's fields, these
  values are computed once and then cached for the remainder of the runtime of
  your application.

  If your application needs to modify a class after its initial definition
  (for example, using `reopen()` to add additional attributes), make sure you
  do it before using your model with the store, which uses these properties
  extensively.
*/

DS.Model.reopenClass({
  /**
    For a given relationship name, returns the model type of the relationship.

    For example, if you define a model like this:

        App.Post = DS.Model.extend({
          comments: DS.hasMany(App.Comment)
        });

    Calling `App.Post.typeForAssociation('comments')` will return `App.Comment`.

    @param {String} name the name of the association
    @return {subclass of DS.Model} the type of the association, or undefined
  */
  typeForAssociation: function(name) {
    var association = get(this, 'associationsByName').get(name);
    return association && association.type;
  },

  /**
    The model's associations as a map, keyed on the type of the
    association. The value of each entry is an array containing a descriptor
    for each association with that type, describing the name of the association
    as well as the type.

    For example, given the following model definition:

        App.Blog = DS.Model.extend({
          users: DS.hasMany(App.User),
          owner: DS.belongsTo(App.User),

          posts: DS.hasMany(App.Post)
        });

    This computed property would return a map describing these
    associations, like this:

        var associations = Ember.get(App.Blog, 'associations');
        associatons.get(App.User);
        //=> [ { name: 'users', kind: 'hasMany' },
        //     { name: 'owner', kind: 'belongsTo' } ]
        associations.get(App.Post);
        //=> [ { name: 'posts', kind: 'hasMany' } ]

    @type Ember.Map
    @readOnly
  */
  associations: Ember.computed(function() {
    var map = new Ember.MapWithDefault({
      defaultValue: function() { return []; }
    });

    // Loop through each computed property on the class
    this.eachComputedProperty(function(name, meta) {

      // If the computed property is an association, add
      // it to the map.
      if (meta.isAssociation) {
        if (typeof meta.type === 'string') {
          meta.type = Ember.get(Ember.lookup, meta.type);
        }

        var associationsForType = map.get(meta.type);

        associationsForType.push({ name: name, kind: meta.kind });
      }
    });

    return map;
  }),

  /**
    A hash containing lists of the model's associations, grouped
    by the association kind. For example, given a model with this
    definition:

        App.Blog = DS.Model.extend({
          users: DS.hasMany(App.User),
          owner: DS.belongsTo(App.User),

          posts: DS.hasMany(App.Post)
        });

    This property would contain the following:

       var associationNames = Ember.get(App.Blog, 'associationNames');
       associationNames.hasMany;
       //=> ['users', 'posts']
       associationNames.belongsTo;
       //=> ['owner']

    @type Object
    @readOnly
  */
  associationNames: Ember.computed(function() {
    var names = { hasMany: [], belongsTo: [] };

    this.eachComputedProperty(function(name, meta) {
      if (meta.isAssociation) {
        names[meta.kind].push(name);
      }
    });

    return names;
  }),

  /**
    A map whose keys are the associations of a model and whose values are
    association descriptors.

    For example, given a model with this
    definition:

        App.Blog = DS.Model.extend({
          users: DS.hasMany(App.User),
          owner: DS.belongsTo(App.User),

          posts: DS.hasMany(App.Post)
        });

    This property would contain the following:

       var associationsByName = Ember.get(App.Blog, 'associationsByName');
       associationsByName.get('users');
       //=> { key: 'users', kind: 'hasMany', type: App.User }
       associationsByName.get('owner');
       //=> { key: 'owner', kind: 'belongsTo', type: App.User }

    @type Ember.Map
    @readOnly
  */
  associationsByName: Ember.computed(function() {
    var map = Ember.Map.create(), type;

    this.eachComputedProperty(function(name, meta) {
      if (meta.isAssociation) {
        meta.key = name;
        type = meta.type;

        if (typeof type === 'string') {
          type = get(this, type, false) || get(Ember.lookup, type);
          meta.type = type;
        }

        map.set(name, meta);
      }
    });

    return map;
  }),

  /**
    A map whose keys are the fields of the model and whose values are strings
    describing the kind of the field. A model's fields are the union of all of its
    attributes and relationships.

    For example:

        App.Blog = DS.Model.extend({
          users: DS.hasMany(App.User),
          owner: DS.belongsTo(App.User),

          posts: DS.hasMany(App.Post),

          title: DS.attr('string')
        });

        var fields = Ember.get(App.Blog, 'fields');
        fields.forEach(function(field, kind) {
          console.log(field, kind);
        });

        // prints:
        // users, hasMany
        // owner, belongsTo
        // posts, hasMany
        // title, attribute

    @type Ember.Map
    @readOnly
  */
  fields: Ember.computed(function() {
    var map = Ember.Map.create(), type;

    this.eachComputedProperty(function(name, meta) {
      if (meta.isAssociation) {
        map.set(name, meta.kind);
      } else if (meta.isAttribute) {
        map.set(name, 'attribute');
      }
    });

    return map;
  }),

  /**
    Given a callback, iterates over each of the associations in the model,
    invoking the callback with the name of each association and its association
    descriptor.

    @param {Function} callback the callback to invoke
    @param {any} binding the value to which the callback's `this` should be bound
  */
  eachAssociation: function(callback, binding) {
    get(this, 'associationsByName').forEach(function(name, association) {
      callback.call(binding, name, association);
    });
  }
});

DS.Model.reopen({
  /**
    Given a callback, iterates over each of the associations in the model,
    invoking the callback with the name of each association and its association
    descriptor.

    @param {Function} callback the callback to invoke
    @param {any} binding the value to which the callback's `this` should be bound
  */
  eachAssociation: function(callback, binding) {
    this.constructor.eachAssociation(callback, binding);
  }
});

/**
  @private

  Helper method to look up the name of the inverse of an association.

  In a has-many relationship, there are always two sides: the `belongsTo` side
  and the `hasMany` side. When one side changes, the other side should be updated
  automatically.

  Given a model, the model of the inverse, and the kind of the association, this
  helper returns the name of the association on the inverse.

  For example, imagine the following two associated models:

      App.Post = DS.Model.extend({
        comments: DS.hasMany('App.Comment')
      });

      App.Comment = DS.Model.extend({
        post: DS.belongsTo('App.Post')
      });

  If the `post` property of a `Comment` was modified, Ember Data would invoke
  this helper like this:

      DS._inverseNameFor(App.Comment, App.Post, 'hasMany');
      //=> 'comments'

  Ember Data uses the name of the association returned to reflect the changed
  relationship on the other side.
*/
DS._inverseNameFor = function(modelType, inverseModelType, inverseAssociationKind) {
  var associationMap = get(modelType, 'associations'),
      possibleAssociations = associationMap.get(inverseModelType),
      possible, actual, oldValue;

  if (!possibleAssociations) { return; }

  for (var i = 0, l = possibleAssociations.length; i < l; i++) {
    possible = possibleAssociations[i];

    if (possible.kind === inverseAssociationKind) {
      actual = possible;
      break;
    }
  }

  if (actual) { return actual.name; }
};

/**
  @private

  Given a model and an association name, returns the model type of
  the named association.

      App.Post = DS.Model.extend({
        comments: DS.hasMany('App.Comment')
      });

      DS._inverseTypeFor(App.Post, 'comments');
      //=> App.Comment
  @param {DS.Model class} modelType
  @param {String} associationName
  @return {DS.Model class}
*/
DS._inverseTypeFor = function(modelType, associationName) {
  var associations = get(modelType, 'associationsByName'),
      association = associations.get(associationName);

  if (association) { return association.type; }
};

})();



(function() {
var get = Ember.get, set = Ember.set;

DS.OneToManyChange = function(options) {
  this.oldParent = options.oldParent;
  this.child = options.child;
  this.belongsToName = options.belongsToName;
  this.store = options.store;
  this.committed = {};
  this.awaiting = 0;
};

/** @private */
DS.OneToManyChange.create = function(options) {
  return new DS.OneToManyChange(options);
};

/** @private */
DS.OneToManyChange.forChildAndParent = function(childClientId, store, options) {
  // Get the type of the child based on the child's client ID
  var childType = store.typeForClientId(childClientId), key;

  // If the name of the belongsTo side of the relationship is specified,
  // use that
  // If the type of the parent is specified, look it up on the child's type
  // definition.
  if (options.parentType) {
    key = inverseBelongsToName(options.parentType, childType, options.hasManyName);
  } else if (options.belongsToName) {
    key = options.belongsToName;
  } else {
    Ember.assert("You must pass either a parentType or belongsToName option to OneToManyChange.forChildAndParent", false);
  }

  var change = store.relationshipChangeFor(childClientId, key);

  if (!change) {
    change = DS.OneToManyChange.create({
      child: childClientId,
      store: store
    });

    store.addRelationshipChangeFor(childClientId, key, change);
  }

  change.belongsToName = key;

  return change;
};

DS.OneToManyChange.prototype = {
  /**
    Get the child type and ID, if available.

    @returns {Array} an array of type and ID
  */
  getChildTypeAndId: function() {
    return this.getTypeAndIdFor(this.child);
  },

  /**
    Get the old parent type and ID, if available.

    @returns {Array} an array of type and ID
  */
  getOldParentTypeAndId: function() {
    return this.getTypeAndIdFor(this.oldParent);
  },

  /**
    Get the new parent type and ID, if available.

    @returns {Array} an array of type and ID
  */
  getNewParentTypeAndId: function() {
    return this.getTypeAndIdFor(this.newParent);
  },

  /**
    Get the name of the relationship on the hasMany side.

    @returns {String}
  */
  getHasManyName: function() {
    var name = this.hasManyName, store = this.store, parent;

    if (!name) {
      parent = this.oldParent || this.newParent;
      if (!parent) { return; }

      var childType = store.typeForClientId(this.child);
      var inverseType = DS._inverseTypeFor(childType, this.belongsToName);
      name = inverseHasManyName(inverseType, childType, this.belongsToName);
      this.hasManyName = name;
    }

    return name;
  },

  /**
    Get the name of the relationship on the belongsTo side.

    @returns {String}
  */
  getBelongsToName: function() {
    var name = this.belongsToName, store = this.store, parent;

    if (!name) {
      parent = this.oldParent || this.newParent;
      if (!parent) { return; }

      var childType = store.typeForClientId(this.child);
      var parentType = store.typeForClientId(parent);
      name = DS._inverseNameFor(childType, parentType, 'belongsTo', this.hasManyName);

      this.belongsToName = name;
    }

    return name;
  },

  /** @private */
  getTypeAndIdFor: function(clientId) {
    if (clientId) {
      var store = this.store;

      return [
        store.typeForClientId(clientId),
        store.idForClientId(clientId)
      ];
    }
  },

  /** @private */
  destroy: function() {
    var childClientId = this.child,
        belongsToName = this.getBelongsToName(),
        hasManyName = this.getHasManyName(),
        store = this.store,
        child, oldParent, newParent, lastParent, transaction;

    store.removeRelationshipChangeFor(childClientId, belongsToName);

    if (transaction = this.transaction) {
      transaction.relationshipBecameClean(this);
    }
  },

  /** @private */
  getByClientId: function(clientId) {
    var store = this.store;

    // return null or undefined if the original clientId was null or undefined
    if (!clientId) { return clientId; }

    if (store.recordIsMaterialized(clientId)) {
      return store.findByClientId(null, clientId);
    }
  },

  /** @private */
  getChild: function() {
    return this.getByClientId(this.child);
  },

  /** @private */
  getOldParent: function() {
    return this.getByClientId(this.oldParent);
  },

  /** @private */
  getNewParent: function() {
    return this.getByClientId(this.newParent);
  },

  /** @private */
  getLastParent: function() {
    return this.getByClientId(this.lastParent);
  },

  /**
    @private

    Make sure that all three parts of the relationship change are part of
    the same transaction. If any of the three records is clean and in the
    default transaction, and the rest are in a different transaction, move
    them all into that transaction.
  */
  ensureSameTransaction: function(child, oldParent, newParent, hasManyName, belongsToName) {
    var transactions = Ember.A();

    if (child)     { transactions.pushObject(get(child, 'transaction')); }
    if (oldParent) { transactions.pushObject(get(oldParent, 'transaction')); }
    if (newParent) { transactions.pushObject(get(newParent, 'transaction')); }

    var transaction = transactions.reduce(function(prev, t) {
      if (!get(t, 'isDefault')) {
        if (prev === null) { return t; }
        Ember.assert("All records in a changed relationship must be in the same transaction. You tried to change the relationship between records when one is in " + t + " and the other is in " + prev, t === prev);
      }

      return prev;
    }, null);

    if (transaction) {
      transaction.add(child);
      if (oldParent) { transaction.add(oldParent); }
      if (newParent) { transaction.add(newParent); }
    } else {
      transaction = transactions.objectAt(0);
    }

    this.transaction = transaction;
    return transaction;
  },

  /** @private */
  sync: function() {
    var oldParentClientId = this.oldParent,
        newParentClientId = this.newParent,
        hasManyName = this.getHasManyName(),
        belongsToName = this.getBelongsToName(),
        child = this.getChild(),
        oldParent, newParent;

    //Ember.assert("You specified a hasMany (" + hasManyName + ") on " + (!belongsToName && (newParent || oldParent || this.lastParent).constructor) + " but did not specify an inverse belongsTo on " + child.constructor, belongsToName);

    // This code path is reached if a child record was added to a new ManyArray
    // without being removed from its old ManyArray. Below, this method will
    // ensure (via `removeObject`) that the record is no longer in the old
    // ManyArray.
    if (oldParentClientId === undefined) {
      // Since the child was added to a ManyArray, we know it was materialized.
      oldParent = get(child, belongsToName);

      if (oldParent) {
        this.oldParent = get(oldParent, 'clientId');
      } else {
        this.oldParent = null;
      }
    } else {
      oldParent = this.getOldParent();
    }

    // Coalesce changes from A to B and back to A.
    if (oldParentClientId === newParentClientId) {
      // If we have gone from oldParent to newParent and back to oldParent,
      // there must be a materialized child.

      // If our lastParent clientId is not null, there will always be a
      // materialized lastParent.
      var lastParent = this.getLastParent();
      if (lastParent) {
        lastParent.suspendAssociationObservers(function() {
          get(lastParent, hasManyName).removeObject(child);
        });
      }

      // Don't do anything if the belongsTo is going from null back to null
      if (oldParent) {
        get(oldParent, hasManyName).addObject(child);
      }

      set(child, belongsToName, oldParent);

      this.destroy();
      return;
    }

    //Ember.assert("You specified a belongsTo (" + belongsToName + ") on " + child.constructor + " but did not specify an inverse hasMany on " + (!hasManyName && (newParent || oldParent || this.lastParentRecord).constructor), hasManyName);

    newParent = this.getNewParent();
    var transaction = this.ensureSameTransaction(child, oldParent, newParent, hasManyName, belongsToName);

    transaction.relationshipBecameDirty(this);

    // Next, make sure that all three side of the association reflect the
    // state of the OneToManyChange, while making sure to avoid an
    // infinite loop.


    var dirtySet = new Ember.OrderedSet();

    // If there is an `oldParent` and the `oldParent` is different to
    // the `newParent`, use the idempotent `removeObject` to ensure
    // that the record is no longer in its ManyArray. The `removeObject`
    // method only has an effect if:
    //
    // 1. The change happened from the belongsTo side
    // 2. The record was moved to a new parent without explicitly
    //    removing it from the old parent first.
    if (oldParent && oldParent !== newParent) {
      get(oldParent, hasManyName).removeObject(child);

      // TODO: This implementation causes a race condition in key-value
      // stores. The fix involves buffering changes that happen while
      // a record is loading. A similar fix is required for other parts
      // of ember-data, and should be done as new infrastructure, not
      // a one-off hack. [tomhuda]
      if (get(oldParent, 'isLoaded')) {
        this.store.recordHasManyDidChange(dirtySet, oldParent, this);
      }
    }

    // If there is a `newParent`, use the idempotent `addObject`
    // to ensure that the record is in its ManyArray. The `addObject`
    // method only has an effect if the change happened from the
    // belongsTo side.
    if (newParent) {
      get(newParent, hasManyName).addObject(child);

      if (get(newParent, 'isLoaded')) {
        this.store.recordHasManyDidChange(dirtySet, newParent, this);
      }
    }

    if (child) {
      // Only set the belongsTo on the child if it is not already the
      // newParent. This happens if the change happened from the
      // ManyArray side.
      if (belongsToName && (get(child, belongsToName) !== newParent)) {
        set(child, belongsToName, newParent);
      }

      this.store.recordBelongsToDidChange(dirtySet, child, this);
    }

    dirtySet.forEach(function(record) {
      record.adapterDidDirty();
    });

    // If this change is later reversed (A->B followed by B->A),
    // we will need to remove the child from this parent. Save
    // it off as `lastParent` so we can do that.
    this.lastParent = newParentClientId;
  },

  /** @private */
  adapterDidUpdate: function() {
    if (this.awaiting > 0) { return; }
    var belongsToName = this.getBelongsToName();
    var hasManyName = this.getHasManyName();
    var oldParent, newParent, child;

    this.destroy();
  },

  wait: function() {
    this.awaiting++;
  },

  done: function() {
    this.awaiting--;

    if (this.awaiting === 0) {
      this.adapterDidUpdate();
    }
  }
};

function inverseBelongsToName(parentType, childType, hasManyName) {
  // Get the options passed to the parent's DS.hasMany()
  var options = parentType.metaForProperty(hasManyName).options;
  var belongsToName;

  if (belongsToName = options.inverse) {
    return belongsToName;
  }

  return DS._inverseNameFor(childType, parentType, 'belongsTo');
}

function inverseHasManyName(parentType, childType, belongsToName) {
  var options = childType.metaForProperty(belongsToName).options;
  var hasManyName;

  if (hasManyName = options.inverse) {
    return hasManyName;
  }

  return DS._inverseNameFor(parentType, childType, 'hasMany');
}

})();



(function() {

})();



(function() {
var set = Ember.set;

/**
  This code registers an injection for Ember.Application.

  If an Ember.js developer defines a subclass of DS.Store on their application,
  this code will automatically instantiate it and make it available on the
  router.

  Additionally, after an application's controllers have been injected, they will
  each have the store made available to them.

  For example, imagine an Ember.js application with the following classes:

  App.Store = DS.Store.extend({
    adapter: 'App.MyCustomAdapter'
  });

  App.PostsController = Ember.ArrayController.extend({
    // ...
  });

  When the application is initialized, `App.Store` will automatically be
  instantiated, and the instance of `App.PostsController` will have its `store`
  property set to that instance.

  Note that this code will only be run if the `ember-application` package is
  loaded. If Ember Data is being used in an environment other than a
  typical application (e.g., node.js where only `ember-runtime` is available),
  this code will be ignored.
*/

Ember.onLoad('Ember.Application', function(Application) {
  Application.registerInjection({
    name: "store",
    before: "controllers",

    // If a store subclass is defined, like App.Store,
    // instantiate it and inject it into the router.
    injection: function(app, stateManager, property) {
      if (!stateManager) { return; }
      if (property === 'Store') {
        set(stateManager, 'store', app[property].create());
      }
    }
  });

  Application.registerInjection({
    name: "giveStoreToControllers",
    after: ['store','controllers'],

    // For each controller, set its `store` property
    // to the DS.Store instance we created above.
    injection: function(app, stateManager, property) {
      if (!stateManager) { return; }
      if (/^[A-Z].*Controller$/.test(property)) {
        var controllerName = property.charAt(0).toLowerCase() + property.substr(1);
        var store = stateManager.get('store');
        var controller = stateManager.get(controllerName);
        if(!controller) { return; }

        controller.set('store', store);
      }
    }
  });
});

})();



(function() {
var get = Ember.get, set = Ember.set;

/**
  The serializer is responsible for converting the data returned from the
  adapter into the semantics expected by records in Ember Data. It is also
  responsible for converting a record into the form expected by the adapter
  when saving changes that have been made locally.

  Typically, your application's `DS.Adapter` is responsible for both creating
  a serializer as well as calling the appropriate methods when it needs to
  materialize data or serialize a record.

  ## Serialization

  These methods are responsible for taking a record and
  producing a JSON object.

  These methods are designed in layers, like a delicious 7-layer
  cake (but with fewer layers).

  The main entry point for serialization is the `serialize`
  method, which takes the record and options.

  The `serialize` method is responsible for:

  * turning the record's attributes (`DS.attr`) into
    attributes on the JSON object.
  * optionally adding the record's ID onto the hash
  * adding relationships (`DS.hasMany` and `DS.belongsTo`)
    to the JSON object.

  Depending on the backend, the serializer can choose
  whether to include the `hasMany` or `belongsTo`
  relationships on the JSON hash.

  For very custom serialization, you can implement your
  own `serialize` method. In general, however, you will want
  to override the hooks described below.

  ### Adding the ID

  The default `serialize` will optionally call your serializer's
  `addId` method with the JSON hash it is creating, the
  record's type, and the record's ID. The `serialize` method
  will not call `addId` if the record's ID is undefined.

  Your adapter must specifically request ID inclusion by
  passing `{ includeId: true }` as an option to `serialize`.

  NOTE: You may not want to include the ID when updating an
  existing record, because your server will likely disallow
  changing an ID after it is created, and the PUT request
  itself will include the record's identification.

  By default, `addId` will:

  1. Get the primary key name for the record by calling
     the serializer's `primaryKey` with the record's type.
     Unless you override the `primaryKey` method, this
     will be `'id'`.
  2. Assign the record's ID to the primary key in the
     JSON hash being built.

  If your backend expects a JSON object with the primary
  key at the root, you can just override the `primaryKey`
  method on your serializer subclass.

  Otherwise, you can override the `addId` method for
  more specialized handling.

  ### Adding Attributes

  By default, the serializer's `serialize` method will call
  `addAttributes` with the JSON object it is creating
  and the record to serialize.

  The `addAttributes` method will then call `addAttribute`
  in turn, with the JSON object, the record to serialize,
  the attribute's name and its type.

  Finally, the `addAttribute` method will serialize the
  attribute:

  1. It will call `keyForAttributeName` to determine
     the key to use in the JSON hash.
  2. It will get the value from the record.
  3. It will call `serializeValue` with the attribute's
     value and attribute type to convert it into a
     JSON-compatible value. For example, it will convert a
     Date into a String.

  If your backend expects a JSON object with attributes as
  keys at the root, you can just override the `serializeValue`
  and `keyForAttributeName` methods in your serializer
  subclass and let the base class do the heavy lifting.

  If you need something more specialized, you can probably
  override `addAttribute` and let the default `addAttributes`
  handle the nitty gritty.

  ### Adding Relationships

  By default, `serialize` will call your serializer's
  `addRelationships` method with the JSON object that is
  being built and the record being serialized. The default
  implementation of this method is to loop over all of the
  relationships defined on your record type and:

  * If the relationship is a `DS.hasMany` relationship,
    call `addHasMany` with the JSON object, the record
    and a description of the relationship.
  * If the relationship is a `DS.belongsTo` relationship,
    call `addBelongsTo` with the JSON object, the record
    and a description of the relationship.

  The relationship description has the following keys:

  * `type`: the class of the associated information (the
    first parameter to `DS.hasMany` or `DS.belongsTo`)
  * `kind`: either `hasMany` or `belongsTo`

  The relationship description may get additional
  information in the future if more capabilities or
  relationship types are added. However, it will
  remain backwards-compatible, so the mere existence
  of new features should not break existing adapters.
*/
DS.Serializer = Ember.Object.extend({
  init: function() {
    this.mappings = Ember.Map.create();
  },

  //.......................
  //. SERIALIZATION HOOKS
  //.......................

  /**
    The main entry point for serializing a record. While you can consider this
    a hook that can be overridden in your serializer, you will have to manually
    handle serialization. For most cases, there are more granular hooks that you
    can override.

    If overriding this method, these are the responsibilities that you will need
    to implement yourself:

    * If the option hash contains `includeId`, add the record's ID to the serialized form.
      By default, `serialize` calls `addId` if appropriate.
    * Add the record's attributes to the serialized form. By default, `serialize` calls
      `addAttributes`.
    * Add the record's relationships to the serialized form. By default, `serialize` calls
      `addRelationships`.

    @param {DS.Model} record the record to serialize
    @param {Object} [options] a hash of options
    @returns {any} the serialized form of the record
  */
  serialize: function(record, options) {
    options = options || {};

    var serialized = this.createSerializedForm(), id;

    if (options.includeId) {
      if (id = get(record, 'id')) {
        this._addId(serialized, record.constructor, id);
      }
    }

    this.addAttributes(serialized, record);
    this.addRelationships(serialized, record);

    return serialized;
  },

  /**
    @private

    Given an attribute type and value, convert the value into the
    serialized form using the transform registered for that type.

    @param {any} value the value to convert to the serialized form
    @param {String} attributeType the registered type (e.g. `string`
      or `boolean`)
    @returns {any} the serialized form of the value
  */
  serializeValue: function(value, attributeType) {
    var transform = this.transforms ? this.transforms[attributeType] : null;

    Ember.assert("You tried to use an attribute type (" + attributeType + ") that has not been registered", transform);
    return transform.serialize(value);
  },

  /**
    A hook you can use to normalize IDs before adding them to the
    serialized representation.

    Because the store coerces all IDs to strings for consistency,
    this is the opportunity for the serializer to, for example,
    convert numerical IDs back into number form.

    @param {String} id the id from the record
    @returns {any} the serialized representation of the id
  */
  serializeId: function(id) {
    if (isNaN(id)) { return id; }
    return +id;
  },

  /**
    A hook you can use to change how attributes are added to the serialized
    representation of a record.

    By default, `addAttributes` simply loops over all of the attributes of the
    passed record, maps the attribute name to the key for the serialized form,
    and invokes any registered transforms on the value. It then invokes the
    more granular `addAttribute` with the key and transformed value.

    Since you can override `keyForAttributeName`, `addAttribute`, and register
    custom tranforms, you should rarely need to override this hook.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
  */
  addAttributes: function(data, record) {
    record.eachAttribute(function(name, attribute) {
      this._addAttribute(data, record, name, attribute.type);
    }, this);
  },

  /**
    A hook you can use to customize how the key/value pair is added to
    the serialized data.

    @param {any} serialized the serialized form being built
    @param {String} key the key to add to the serialized data
    @param {any} value the value to add to the serialized data
  */
  addAttribute: Ember.K,

  /**
    A hook you can use to customize how the record's id is added to
    the serialized data.

    The `addId` hook is called with:

    * the serialized representation being built
    * the resolved primary key (taking configurations and the
      `primaryKey` hook into consideration)
    * the serialized id (after calling the `serializeId` hook)

    @param {any} data the serialized representation that is being built
    @param {String} key the resolved primary key
    @param {id} id the serialized id
  */
  addId: Ember.K,

  /**
    A hook you can use to change how relationships are added to the serialized
    representation of a record.

    By default, `addAttributes` loops over all of the relationships of the
    passed record, maps the relationship names to the key for the serialized form,
    and then invokes the public `addBelongsTo` and `addHasMany` hooks.

    Since you can override `keyForBelongsTo`, `keyForHasMany`, `addBelongsTo`,
    `addHasMany`, and register mappings, you should rarely need to override this
    hook.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
  */
  addRelationships: function(data, record) {
    record.eachAssociation(function(name, relationship) {
      if (relationship.kind === 'belongsTo') {
        this._addBelongsTo(data, record, name, relationship);
      } else if (relationship.kind === 'hasMany') {
        this._addHasMany(data, record, name, relationship);
      }
    }, this);
  },

  /**
    A hook you can use to add a `belongsTo` relationship to the
    serialized representation.

    The specifics of this hook are very adapter-specific, so there
    is no default implementation. You can see `DS.RESTSerializer`
    for an example of an implementation of the `addBelongsTo` hook.

    The `belongsTo` relationship object has the following properties:

    * **type** a subclass of DS.Model that is the type of the
      relationship. This is the first parameter to DS.belongsTo
    * **options** the options passed to the call to DS.belongsTo
    * **kind** always `belongsTo`

    Additional properties may be added in the future.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
    @param {String} key the key for the serialized object
    @param {Object} relationship an object representing the relationship
  */
  addBelongsTo: Ember.K,

  /**
    A hook you can use to add a `hasMany` relationship to the
    serialized representation.

    The specifics of this hook are very adapter-specific, so there
    is no default implementation. You may not need to implement this,
    for example, if your backend only expects associations on the
    child of a one to many relationship.

    The `hasMany` relationship object has the following properties:

    * **type** a subclass of DS.Model that is the type of the
      relationship. This is the first parameter to DS.hasMany
    * **options** the options passed to the call to DS.hasMany
    * **kind** always `hasMany`

    Additional properties may be added in the future.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
    @param {String} key the key for the serialized object
    @param {Object} relationship an object representing the relationship
  */
  addHasMany: Ember.K,

  /**
    NAMING CONVENTIONS

    The most commonly overridden APIs of the serializer are
    the naming convention methods:

    * `keyForAttributeName`: converts a camelized attribute name
      into a key in the adapter-provided data hash. For example,
      if the model's attribute name was `firstName`, and the
      server used underscored names, you would return `first_name`.
    * `primaryKey`: returns the key that should be used to
      extract the id from the adapter-provided data hash. It is
      also used when serializing a record.
  */

  /**
    A hook you can use in your serializer subclass to customize
    how an unmapped attribute name is converted into a key.

    By default, this method returns the `name` parameter.

    For example, if the attribute names in your JSON are underscored,
    you will want to convert them into JavaScript conventional
    camelcase:

    ```javascript
    App.MySerializer = DS.Serializer.extend({
      // ...

      keyForAttributeName: function(type, name) {
        return name.camelize();
      }
    });
    ```

    @param {DS.Model subclass} type the type of the record with
      the attribute name `name`
    @param {String} name the attribute name to convert into a key

    @returns {String} the key
  */
  keyForAttributeName: function(type, name) {
    return name;
  },

  /**
    A hook you can use in your serializer to specify a conventional
    primary key.

    By default, this method will return the string `id`.

    In general, you should not override this hook to specify a special
    primary key for an individual type; use `configure` instead.

    For example, if your primary key is always `__id__`:

    ```javascript
    App.MySerializer = DS.Serializer.extend({
      // ...
      primaryKey: function(type) {
        return '__id__';
      }
    });
    ```

    In another example, if the primary key always includes the
    underscored version of the type before the string `id`:

    ```javascript
    App.MySerializer = DS.Serializer.extend({
      // ...
      primaryKey: function(type) {
        // If the type is `BlogPost`, this will return
        // `blog_post_id`.
        var typeString = type.toString.split(".")[1].underscore();
        return typeString + "_id";
      }
    });
    ```

    @param {DS.Model subclass} type
    @returns {String} the primary key for the type
  */
  primaryKey: function(type) {
    return "id";
  },

  /**
    A hook you can use in your serializer subclass to customize
    how an unmapped `belongsTo` association is converted into
    a key.

    By default, this method calls `keyForAttributeName`, so if
    your naming convention is uniform across attributes and
    associations, you can use the default here and override
    just `keyForAttributeName` as needed.

    For example, if the `belongsTo` names in your JSON always
    begin with `BT_` (e.g. `BT_posts`), you can strip out the
    `BT_` prefix:"

    ```javascript
    App.MySerializer = DS.Serializer.extend({
      // ...
      keyForBelongsTo: function(type, name) {
        return name.match(/^BT_(.*)$/)[1].camelize();
      }
    });
    ```

    @param {DS.Model subclass} type the type of the record with
      the `belongsTo` association.
    @param {String} name the association name to convert into a key

    @returns {String} the key
  */
  keyForBelongsTo: function(type, name) {
    return this.keyForAttributeName(type, name);
  },

  /**
    A hook you can use in your serializer subclass to customize
    how an unmapped `hasMany` association is converted into
    a key.

    By default, this method calls `keyForAttributeName`, so if
    your naming convention is uniform across attributes and
    associations, you can use the default here and override
    just `keyForAttributeName` as needed.

    For example, if the `hasMany` names in your JSON always
    begin with the "table name" for the current type (e.g.
    `post_comments`), you can strip out the prefix:"

    ```javascript
    App.MySerializer = DS.Serializer.extend({
      // ...
      keyForHasMany: function(type, name) {
        // if your App.BlogPost has many App.BlogComment, the key from
        // the server would look like: `blog_post_blog_comments`
        //
        // 1. Convert the type into a string and underscore the
        //    second part (App.BlogPost -> blog_post)
        // 2. Extract the part after `blog_post_` (`blog_comments`)
        // 3. Underscore it, to become `blogComments`
        var typeString = type.toString().split(".")[1].underscore();
        return name.match(new RegExp("^" + typeString + "_(.*)$"))[1].camelize();
      }
    });
    ```

    @param {DS.Model subclass} type the type of the record with
      the `belongsTo` association.
    @param {String} name the association name to convert into a key

    @returns {String} the key
  */
  keyForHasMany: function(type, name) {
    return this.keyForAttributeName(type, name);
  },
  //.........................
  //. MATERIALIZATION HOOKS
  //.........................

  materialize: function(record, serialized) {
    if (Ember.none(get(record, 'id'))) {
      record.materializeId(this.extractId(record.constructor, serialized));
    }

    this.materializeAttributes(record, serialized);
    this.materializeRelationships(record, serialized);
  },

  deserializeValue: function(value, attributeType) {
    var transform = this.transforms ? this.transforms[attributeType] : null;

    Ember.assert("You tried to use a attribute type (" + attributeType + ") that has not been registered", transform);
    return transform.deserialize(value);
  },

  materializeAttributes: function(record, serialized) {
    record.eachAttribute(function(name, attribute) {
      this.materializeAttribute(record, serialized, name, attribute.type);
    }, this);
  },

  materializeAttribute: function(record, serialized, attributeName, attributeType) {
    var value = this.extractAttribute(record.constructor, serialized, attributeName);
    value = this.deserializeValue(value, attributeType);

    record.materializeAttribute(attributeName, value);
  },

  materializeRelationships: function(record, hash) {
    record.eachAssociation(function(name, relationship) {
      if (relationship.kind === 'hasMany') {
        this.materializeHasMany(name, record, hash, relationship);
      } else if (relationship.kind === 'belongsTo') {
        this.materializeBelongsTo(name, record, hash, relationship);
      }
    }, this);
  },

  materializeHasMany: function(name, record, hash, relationship) {
    var key = this._keyForHasMany(record.constructor, relationship.key);
    record.materializeHasMany(name, this.extractHasMany(record.constructor, hash, key));
  },

  materializeBelongsTo: function(name, record, hash, relationship) {
    var key = this._keyForBelongsTo(record.constructor, relationship.key);
    record.materializeBelongsTo(name, this.extractBelongsTo(record.constructor, hash, key));
  },

  _extractEmbeddedRelationship: function(type, hash, name, relationshipType) {
    var key = this['_keyFor' + relationshipType](type, name),
        mappings = this.mappingForType(type),
        mapping = mappings && mappings[name];

    if (mapping && mapping.embedded === 'load') {
      return this['extractEmbedded' + relationshipType](type, hash, key);
    }
  },

  _extractEmbeddedBelongsTo: function(type, hash, name) {
    return this._extractEmbeddedRelationship(type, hash, name, 'BelongsTo');
  },

  _extractEmbeddedHasMany: function(type, hash, name) {
    return this._extractEmbeddedRelationship(type, hash, name, 'HasMany');
  },

  extractEmbeddedBelongsTo: function(type, hash, key) {
    return this.extractBelongsTo(type, hash, key);
  },

  extractEmbeddedHasMany: function(type, hash, key) {
    return this.extractHasMany(type, hash, key);
  },

  /**
    @private

    This method is called to get the primary key for a given
    type.

    If a primary key configuration exists for this type, this
    method will return the configured value. Otherwise, it will
    call the public `primaryKey` hook.

    @param {DS.Model subclass} type
    @returns {String} the primary key for the type
  */
  _primaryKey: function(type) {
    var mapping = this.mappingForType(type),
        primaryKey = mapping && mapping.primaryKey;

    if (primaryKey) {
      return primaryKey;
    } else {
      return this.primaryKey(type);
    }
  },

  /**
    @private

    This method looks up the key for the attribute name and transforms the
    attribute's value using registered transforms.

    Specifically:

    1. Look up the key for the attribute name. If available, this will use
       any registered mappings. Otherwise, it will invoke the public
       `keyForAttributeName` hook.
    2. Get the value from the record using the `attributeName`.
    3. Transform the value using registered transforms for the `attributeType`.
    4. Invoke the public `addAttribute` hook with the hash, key, and
       transformed value.

    @param {any} data the serialized representation being built
    @param {DS.Model} record the record to serialize
    @param {String} attributeName the name of the attribute on the record
    @param {String} attributeType the type of the attribute (e.g. `string`
      or `boolean`)
  */
  _addAttribute: function(data, record, attributeName, attributeType) {
    var key = this._keyForAttributeName(record.constructor, attributeName);
    var value = get(record, attributeName);

    this.addAttribute(data, key, this.serializeValue(value, attributeType));
  },

  /**
    @private

    This method looks up the primary key for the `type` and invokes
    `serializeId` on the `id`.

    It then invokes the public `addId` hook with the primary key and
    the serialized id.

    @param {any} data the serialized representation that is being built
    @param {Ember.Model subclass} type
    @param {any} id the materialized id from the record
  */
  _addId: function(hash, type, id) {
    var primaryKey = this._primaryKey(type);

    this.addId(hash, primaryKey, this.serializeId(id));
  },

  /**
    @private

    This method is called to get a key used in the data from
    an attribute name. It first checks for any mappings before
    calling the public hook `keyForAttributeName`.

    @param {DS.Model subclass} type the type of the record with
      the attribute name `name`
    @param {String} name the attribute name to convert into a key

    @returns {String} the key
  */
  _keyForAttributeName: function(type, name) {
    return this._keyFromMappingOrHook('keyForAttributeName', type, name);
  },

  /**
    @private

    This method is called to get a key used in the data from
    a belongsTo association. It first checks for any mappings before
    calling the public hook `keyForBelongsTo`.

    @param {DS.Model subclass} type the type of the record with
      the `belongsTo` association.
    @param {String} name the association name to convert into a key

    @returns {String} the key
  */
  _keyForBelongsTo: function(type, name) {
    return this._keyFromMappingOrHook('keyForBelongsTo', type, name);
  },

  /**
    @private

    This method is called to get a key used in the data from
    a hasMany association. It first checks for any mappings before
    calling the public hook `keyForHasMany`.

    @param {DS.Model subclass} type the type of the record with
      the `hasMany` association.
    @param {String} name the association name to convert into a key

    @returns {String} the key
  */
  _keyForHasMany: function(type, name) {
    return this._keyFromMappingOrHook('keyForHasMany', type, name);
  },
  /**
    @private

    This method converts the relationship name to a key for serialization,
    and then invokes the public `addBelongsTo` hook.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
    @param {String} name the relationship name
    @param {Object} relationship an object representing the relationship
  */
  _addBelongsTo: function(data, record, name, relationship) {
    var key = this._keyForBelongsTo(record.constructor, name);
    this.addBelongsTo(data, record, key, relationship);
  },

  /**
    @private

    This method converts the relationship name to a key for serialization,
    and then invokes the public `addHasMany` hook.

    @param {any} data the serialized representation that is being built
    @param {DS.Model} record the record to serialize
    @param {String} name the relationship name
    @param {Object} relationship an object representing the relationship
  */
  _addHasMany: function(data, record, name, relationship) {
    var key = this._keyForHasMany(record.constructor, name);
    this.addHasMany(data, record, key, relationship);
  },

  /**
    @private

    An internal method that handles checking whether a mapping
    exists for a particular attribute or association name before
    calling the public hooks.

    If a mapping is found, and the mapping has a key defined,
    use that instead of invoking the hook.

    @param {String} publicMethod the public hook to invoke if
      a mapping is not found (e.g. `keyForAttributeName`)
    @param {DS.Model subclass} type the type of the record with
      the attribute or association name.
    @param {String} name the attribute or association name to
      convert into a key
  */
  _keyFromMappingOrHook: function(publicMethod, type, name) {
    var mapping = this.mappingForType(type),
        mappingOptions = mapping && mapping[name],
        key = mappingOptions && mappingOptions.key;

    if (key) {
      return key;
    } else {
      return this[publicMethod](type, name);
    }
  },

  /**
    TRANSFORMS
  */

  registerTransform: function(type, transform) {
    this.transforms[type] = transform;
  },

  /**
    MAPPING CONVENIENCE
  */

  map: function(type, mappings) {
    this.mappings.set(type, mappings);
  },

  mappingForType: function(type) {
    this._reifyMappings();
    return this.mappings.get(type);
  },

  _reifyMappings: function() {
    if (this._didReifyMappings) { return; }

    var mappings = this.mappings,
        reifiedMappings = Ember.Map.create();

    mappings.forEach(function(key, mapping) {
      if (typeof key === 'string') {
        var type = Ember.get(Ember.lookup, key);
        Ember.assert("Could not find model at path" + key, type);

        reifiedMappings.set(type, mapping);
      } else {
        reifiedMappings.set(key, mapping);
      }
    });

    this.mappings = reifiedMappings;

    this._didReifyMappings = true;
  }
});


})();



(function() {
/**
  DS.Transforms is a hash of transforms used by DS.Serializer.
*/
DS.JSONTransforms = {
  string: {
    deserialize: function(serialized) {
      return Ember.none(serialized) ? null : String(serialized);
    },

    serialize: function(deserialized) {
      return Ember.none(deserialized) ? null : String(deserialized);
    }
  },

  number: {
    deserialize: function(serialized) {
      return Ember.none(serialized) ? null : Number(serialized);
    },

    serialize: function(deserialized) {
      return Ember.none(deserialized) ? null : Number(deserialized);
    }
  },

  // Handles the following boolean inputs:
  // "TrUe", "t", "f", "FALSE", 0, (non-zero), or boolean true/false
  'boolean': {
    deserialize: function(serialized) {
      var type = typeof serialized;

      if (type === "boolean") {
        return serialized;
      } else if (type === "string") {
        return serialized.match(/^true$|^t$|^1$/i) !== null;
      } else if (type === "number") {
        return serialized === 1;
      } else {
        return false;
      }
    },

    serialize: function(deserialized) {
      return Boolean(deserialized);
    }
  },

  date: {
    deserialize: function(serialized) {
      var type = typeof serialized;

      if (type === "string" || type === "number") {
        // this is a fix for Safari 5.1.5 on Mac which does not accept timestamps as yyyy-mm-dd
        if (type === "string" && serialized.search(/^\d{4}-\d{2}-\d{2}$/) !== -1){
          serialized += "T00:00:00Z";
        }

        return new Date(serialized);
      } else if (serialized === null || serialized === undefined) {
        // if the value is not present in the data,
        // return undefined, not null.
        return serialized;
      } else {
        return null;
      }
    },

    serialize: function(date) {
      if (date instanceof Date) {
        var days = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        var pad = function(num) {
          return num < 10 ? "0"+num : ""+num;
        };

        var utcYear = date.getUTCFullYear(),
            utcMonth = date.getUTCMonth(),
            utcDayOfMonth = date.getUTCDate(),
            utcDay = date.getUTCDay(),
            utcHours = date.getUTCHours(),
            utcMinutes = date.getUTCMinutes(),
            utcSeconds = date.getUTCSeconds();


        var dayOfWeek = days[utcDay];
        var dayOfMonth = pad(utcDayOfMonth);
        var month = months[utcMonth];

        return dayOfWeek + ", " + dayOfMonth + " " + month + " " + utcYear + " " +
               pad(utcHours) + ":" + pad(utcMinutes) + ":" + pad(utcSeconds) + " GMT";
      } else if (date === undefined) {
        return undefined;
      } else {
        return null;
      }
    }
  }
};

})();



(function() {
var get = Ember.get, set = Ember.set;

DS.JSONSerializer = DS.Serializer.extend({
  init: function() {
    this._super();

    if (!get(this, 'transforms')) {
      this.set('transforms', DS.JSONTransforms);
    }
  },

  addId: function(data, key, id) {
    data[key] = id;
  },

  /**
    A hook you can use to customize how the key/value pair is added to
    the serialized data.

    @param {any} hash the JSON hash being built
    @param {String} key the key to add to the serialized data
    @param {any} value the value to add to the serialized data
  */
  addAttribute: function(hash, key, value) {
    hash[key] = value;
  },

  /**
    @private

    Creates an empty hash that will be filled in by the hooks called from the
    `serialize()` method.

    @return {Object}
  */
  createSerializedForm: function() {
    return {};
  },

  extractAttribute: function(type, hash, attributeName) {
    var key = this._keyForAttributeName(type, attributeName);
    return hash[key];
  },

  extractId: function(type, hash) {
    var primaryKey = this._primaryKey(type);

    // Ensure that we coerce IDs to strings so that record
    // IDs remain consistent between application runs; especially
    // if the ID is serialized and later deserialized from the URL,
    // when type information will have been lost.
    return hash[primaryKey]+'';
  },

  extractHasMany: function(type, hash, key) {
    return hash[key];
  },

  extractBelongsTo: function(type, hash, key) {
    return hash[key];
  },

  replaceEmbeddedBelongsTo: function(type, hash, name, id) {
    hash[this._keyForBelongsTo(type, name)] = id;
  },

  replaceEmbeddedHasMany: function(type, hash, name, ids) {
    hash[this._keyForHasMany(type, name)] = ids;
  }
});

})();



(function() {
/**
  An adapter is an object that receives requests from a store and
  translates them into the appropriate action to take against your
  persistence layer. The persistence layer is usually an HTTP API, but may
  be anything, such as the browser's local storage.

  ### Creating an Adapter

  First, create a new subclass of `DS.Adapter`:

      App.MyAdapter = DS.Adapter.extend({
        // ...your code here
      });

  To tell your store which adapter to use, set its `adapter` property:

      App.store = DS.Store.create({
        revision: 3,
        adapter: App.MyAdapter.create()
      });

  `DS.Adapter` is an abstract base class that you should override in your
  application to customize it for your backend. The minimum set of methods
  that you should implement is:

    * `find()`
    * `createRecord()`
    * `updateRecord()`
    * `deleteRecord()`

   To improve the network performance of your application, you can optimize
   your adapter by overriding these lower-level methods:

    * `findMany()`
    * `createRecords()`
    * `updateRecords()`
    * `deleteRecords()`
    * `commit()`

   For more information about the adapter API, please see `README.md`.
*/

var get = Ember.get, set = Ember.set;

DS.Adapter = Ember.Object.extend(DS._Mappable, {

  init: function() {
    var serializer = get(this, 'serializer');

    if (Ember.Object.detect(serializer)) {
      serializer = serializer.create();
      set(this, 'serializer', serializer);
    }

    this._attributesMap = this.createInstanceMapFor('attributes');

    this._outstandingOperations = new Ember.MapWithDefault({
      defaultValue: function() { return 0; }
    });

    this._dependencies = new Ember.MapWithDefault({
      defaultValue: function() { return new Ember.OrderedSet(); }
    });

    this.registerSerializerTransforms(this.constructor, serializer, {});
    this.registerSerializerMappings(serializer);
  },

  dirtyRecordsForAttributeChange: function(dirtySet, record, attributeName, newValue, oldValue) {
    // TODO: Custom equality checking [tomhuda]
    if (newValue !== oldValue) {
      dirtySet.add(record);
    }
  },

  dirtyRecordsForBelongsToChange: function(dirtySet, child) {
    dirtySet.add(child);
  },

  dirtyRecordsForHasManyChange: function(dirtySet, parent) {
    dirtySet.add(parent);
  },

  /**
    @private

    This method recursively climbs the superclass hierarchy and
    registers any class-registered transforms on the adapter's
    serializer.

    Once it registers a transform for a given type, it ignores
    subsequent transforms for the same attribute type.

    @param {Class} klass the DS.Adapter subclass to extract the
      transforms from
    @param {DS.Serializer} serializer the serializer to register
      the transforms onto
    @param {Object} seen a hash of attributes already seen
  */
  registerSerializerTransforms: function(klass, serializer, seen) {
    var transforms = klass._registeredTransforms, superclass, prop;

    for (prop in transforms) {
      if (!transforms.hasOwnProperty(prop) || prop in seen) { continue; }
      seen[prop] = true;

      serializer.registerTransform(prop, transforms[prop]);
    }

    if (superclass = klass.superclass) {
      this.registerSerializerTransforms(superclass, serializer, seen);
    }
  },

  /**
    @private

    This method recursively climbs the superclass hierarchy and
    registers any class-registered mappings on the adapter's
    serializer.

    @param {Class} klass the DS.Adapter subclass to extract the
      transforms from
    @param {DS.Serializer} serializer the serializer to register the
      mappings onto
  */
  registerSerializerMappings: function(serializer) {
    var mappings = this._attributesMap;

    mappings.forEach(function(type, mapping) {
      serializer.map(type, mapping);
    }, this);
  },

  /**
    The `find()` method is invoked when the store is asked for a record that
    has not previously been loaded. In response to `find()` being called, you
    should query your persistence layer for a record with the given ID. Once
    found, you can asynchronously call the store's `load()` method to load
    the record.

    Here is an example `find` implementation:

      find: function(store, type, id) {
        var url = type.url;
        url = url.fmt(id);

        jQuery.getJSON(url, function(data) {
            // data is a hash of key/value pairs. If your server returns a
            // root, simply do something like:
            // store.load(type, id, data.person)
            store.load(type, id, data);
        });
      }
  */
  find: null,

  serializer: DS.JSONSerializer,

  registerTransform: function(attributeType, transform) {
    get(this, 'serializer').registerTransform(attributeType, transform);
  },

  /**
    If the globally unique IDs for your records should be generated on the client,
    implement the `generateIdForRecord()` method. This method will be invoked
    each time you create a new record, and the value returned from it will be
    assigned to the record's `primaryKey`.

    Most traditional REST-like HTTP APIs will not use this method. Instead, the ID
    of the record will be set by the server, and your adapter will update the store
    with the new ID when it calls `didCreateRecord()`. Only implement this method if
    you intend to generate record IDs on the client-side.

    The `generateIdForRecord()` method will be invoked with the requesting store as
    the first parameter and the newly created record as the second parameter:

        generateIdForRecord: function(store, record) {
          var uuid = App.generateUUIDWithStatisticallyLowOddsOfCollision();
          return uuid;
        }
  */
  generateIdForRecord: null,

  materialize: function(record, data) {
    get(this, 'serializer').materialize(record, data);
  },

  serialize: function(record, options) {
    return get(this, 'serializer').serialize(record, options);
  },

  extractId: function(type, data) {
    return get(this, 'serializer').extractId(type, data);
  },

  extractEmbeddedData: function(store, type, data) {
    var serializer = get(this, 'serializer');

    type.eachAssociation(function(name, association) {
      var dataListToLoad, dataToLoad, typeToLoad;

      if (association.kind === 'hasMany') {
        this._extractEmbeddedHasMany(store, serializer, type, data, association);
      } else if (association.kind === 'belongsTo') {
        this._extractEmbeddedBelongsTo(store, serializer, type, data, association);
      }
    }, this);
  },

  _extractEmbeddedHasMany: function(store, serializer, type, data, association) {
    var dataListToLoad = serializer._extractEmbeddedHasMany(type, data, association.key),
        typeToLoad = association.type;

    if (dataListToLoad) {
      var ids = [];

      for (var i=0, l=dataListToLoad.length; i<l; i++) {
        var dataToLoad = dataListToLoad[i];
        ids.push(store.adapterForType(typeToLoad).extractId(typeToLoad, dataToLoad));
      }
      serializer.replaceEmbeddedHasMany(type, data, association.key, ids);
      store.loadMany(association.type, dataListToLoad);
    }
  },

  _extractEmbeddedBelongsTo: function(store, serializer, type, data, association) {
    var dataToLoad = serializer._extractEmbeddedBelongsTo(type, data, association.key),
        typeToLoad = association.type;

    if (dataToLoad) {
      var id = store.adapterForType(typeToLoad).extractId(typeToLoad, dataToLoad);
      serializer.replaceEmbeddedBelongsTo(type, data, association.key, id);
      store.load(association.type, dataToLoad);
    }
  },

  groupByType: function(enumerable) {
    var map = Ember.MapWithDefault.create({
      defaultValue: function() { return Ember.OrderedSet.create(); }
    });

    enumerable.forEach(function(item) {
      map.get(item.constructor).add(item);
    });

    return map;
  },

  processRelationship: function(relationship) {
    // TODO: Track changes to relationships made after a
    // materialization request but before the adapter
    // responds. [tomhuda]
  },

  commit: function(store, commitDetails) {
    this.save(store, commitDetails);
  },

  save: function(store, commitDetails) {
    this.groupByType(commitDetails.created).forEach(function(type, set) {
      this.createRecords(store, type, set.copy());
    }, this);

    this.groupByType(commitDetails.updated).forEach(function(type, set) {
      this.updateRecords(store, type, set.copy());
    }, this);

    this.groupByType(commitDetails.deleted).forEach(function(type, set) {
      this.deleteRecords(store, type, set.copy());
    }, this);
  },

  createRecords: function(store, type, records) {
    records.forEach(function(record) {
      this.createRecord(store, type, record);
    }, this);
  },

  updateRecords: function(store, type, records) {
    records.forEach(function(record) {
      this.updateRecord(store, type, record);
    }, this);
  },

  deleteRecords: function(store, type, records) {
    records.forEach(function(record) {
      this.deleteRecord(store, type, record);
    }, this);
  },

  findMany: function(store, type, ids) {
    ids.forEach(function(id) {
      this.find(store, type, id);
    }, this);
  }
});

DS.Adapter.reopenClass({
  registerTransform: function(attributeType, transform) {
    var registeredTransforms = this._registeredTransforms || {};

    registeredTransforms[attributeType] = transform;

    this._registeredTransforms = registeredTransforms;
  },

  map: DS._Mappable.generateMapFunctionFor('attributes', function(key, newValue, map) {
    var existingValue = map.get(key);

    for (var prop in newValue) {
      if (!newValue.hasOwnProperty(prop)) { continue; }
      existingValue[prop] = newValue[prop];
    }
  }),

  resolveMapConflict: function(oldValue, newValue, mappingsKey) {
    for (var prop in oldValue) {
      if (!oldValue.hasOwnProperty(prop)) { continue; }
      newValue[prop] = oldValue[prop];
    }

    return newValue;
  }
});

})();



(function() {
var get = Ember.get;

DS.FixtureAdapter = DS.Adapter.extend({

  simulateRemoteResponse: true,

  latency: 50,

  /*
    Implement this method in order to provide data associated with a type
  */
  fixturesForType: function(type) {
    return type.FIXTURES ? Ember.A(type.FIXTURES) : null;
  },

  /*
    Implement this method in order to query fixtures data
  */
  queryFixtures: function(fixtures, query) {
    return fixtures;
  },

  /*
    Implement this method in order to provide provide json for CRUD methods
  */
  mockJSON: function(type, record) {
    return this.serialize(record, { includeId: true });
  },

  /*
    Adapter methods
  */
  generateIdForRecord: function(store, record) {
    return Ember.guidFor(record);
  },

  find: function(store, type, id) {
    var fixtures = this.fixturesForType(type);

    Ember.assert("Unable to find fixtures for model type "+type.toString(), !!fixtures);

    if (fixtures) {
      fixtures = fixtures.findProperty('id', id);
    }

    if (fixtures) {
      this.simulateRemoteCall(function() {
        store.load(type, fixtures);
      }, store, type);
    }
  },

  findMany: function(store, type, ids) {
    var fixtures = this.fixturesForType(type);

    Ember.assert("Unable to find fixtures for model type "+type.toString(), !!fixtures);

    if (fixtures) {
      fixtures = fixtures.filter(function(item) {
        return ids.indexOf(item.id) !== -1;
      });
    }

    if (fixtures) {
      this.simulateRemoteCall(function() {
        store.loadMany(type, fixtures);
      }, store, type);
    }
  },

  findAll: function(store, type) {
    var fixtures = this.fixturesForType(type);

    Ember.assert("Unable to find fixtures for model type "+type.toString(), !!fixtures);

    this.simulateRemoteCall(function() {
      store.loadMany(type, fixtures);
      store.didUpdateAll(type);
    }, store, type);
  },

  findQuery: function(store, type, query, array) {
    var fixtures = this.fixturesForType(type);

    Ember.assert("Unable to find fixtures for model type "+type.toString(), !!fixtures);

    fixtures = this.queryFixtures(fixtures, query);

    if (fixtures) {
      this.simulateRemoteCall(function() {
        array.load(fixtures);
      }, store, type);
    }
  },

  createRecord: function(store, type, record) {
    var fixture = this.mockJSON(type, record);

    fixture.id = this.generateIdForRecord(store, record);

    this.simulateRemoteCall(function() {
      store.didSaveRecord(record, fixture);
    }, store, type, record);
  },

  updateRecord: function(store, type, record) {
    var fixture = this.mockJSON(type, record);

    this.simulateRemoteCall(function() {
      store.didSaveRecord(record, fixture);
    }, store, type, record);
  },

  deleteRecord: function(store, type, record) {
    this.simulateRemoteCall(function() {
      store.didSaveRecord(record);
    }, store, type, record);
  },

  /*
    @private
  */
  simulateRemoteCall: function(callback, store, type, record) {
    if (get(this, 'simulateRemoteResponse')) {
      setTimeout(callback, get(this, 'latency'));
    } else {
      callback();
    }
  }
});

DS.fixtureAdapter = DS.FixtureAdapter.create();

})();



(function() {
var get = Ember.get;

DS.RESTSerializer = DS.JSONSerializer.extend({
  keyForBelongsTo: function(type, name) {
    return this.keyForAttributeName(type, name) + "_id";
  },

  keyForAttributeName: function(type, name) {
    return Ember.String.decamelize(name);
  },

  addBelongsTo: function(hash, record, key, relationship) {
    var id = get(record, relationship.key+'.id');

    if (!Ember.none(id)) { hash[key] = id; }
  }
});

})();



(function() {
/*global jQuery*/

var get = Ember.get, set = Ember.set;

DS.RESTAdapter = DS.Adapter.extend({
  bulkCommit: false,

  serializer: DS.RESTSerializer,

  createRecord: function(store, type, record) {
    var root = this.rootForType(type);

    var data = {};
    data[root] = this.serialize(record, { includeId: true });

    this.ajax(this.buildURL(root), "POST", {
      data: data,
      context: this,
      success: function(json) {
        this.didCreateRecord(store, type, record, json);
      },
      error: function(xhr) {
        this.didError(store, type, record, xhr);
      }
    });
  },

  dirtyRecordsForHasManyChange: Ember.K,

  didSaveRecord: function(store, record, hash) {
    record.eachAssociation(function(name, meta) {
      if (meta.kind === 'belongsTo') {
        store.didUpdateRelationship(record, name);
      }
    });

    store.didSaveRecord(record, hash);
  },

  didSaveRecords: function(store, records, array) {
    var i = 0;

    records.forEach(function(record) {
      this.didSaveRecord(store, record, array && array[i++]);
    }, this);
  },

  didCreateRecord: function(store, type, record, json) {
    var root = this.rootForType(type);

    this.sideload(store, type, json, root);
    this.didSaveRecord(store, record, json[root]);
  },

  createRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root);

    var data = {};
    data[plural] = [];
    records.forEach(function(record) {
      data[plural].push(this.serialize(record, { includeId: true }));
    }, this);

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
    this.didSaveRecords(store, records, json[root]);
  },

  updateRecord: function(store, type, record) {
    var id = get(record, 'id');
    var root = this.rootForType(type);

    var data = {};
    data[root] = this.serialize(record);

    this.ajax(this.buildURL(root, id), "PUT", {
      data: data,
      context: this,
      success: function(json) {
        this.didUpdateRecord(store, type, record, json);
      },
      error: function(xhr) {
        this.didError(store, type, record, xhr);
      }
    });
  },

  didUpdateRecord: function(store, type, record, json) {
    var root = this.rootForType(type);

    this.sideload(store, type, json, root);
    this.didSaveRecord(store, record, json && json[root]);
  },

  updateRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root);

    var data = {};
    data[plural] = [];
    records.forEach(function(record) {
      data[plural].push(this.serialize(record, { includeId: true }));
    }, this);

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
    this.didSaveRecords(store, records, json[root]);
  },

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
    this.didSaveRecord(store, record);
  },

  deleteRecords: function(store, type, records) {
    if (get(this, 'bulkCommit') === false) {
      return this._super(store, type, records);
    }

    var root = this.rootForType(type),
        plural = this.pluralize(root),
        serializer = get(this, 'serializer');

    var data = {};
    data[plural] = [];
    records.forEach(function(record) {
      data[plural].push(serializer.serializeId( get(record, 'id') ));
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
    this.didSaveRecords(store, records);
  },

  find: function(store, type, id) {
    var root = this.rootForType(type);

    this.ajax(this.buildURL(root, id), "GET", {
      success: function(json) {
        this.didFindRecord(store, type, json, id);
      }
    });
  },

  didFindRecord: function(store, type, json, id) {
    var root = this.rootForType(type);

    this.sideload(store, type, json, root);
    store.load(type, id, json[root]);
  },

  findAll: function(store, type, since) {
    var root = this.rootForType(type);

    this.ajax(this.buildURL(root), "GET", {
      data: this.sinceQuery(since),
      success: function(json) {
        this.didFindAll(store, type, json);
      }
    });
  },

  didFindAll: function(store, type, json) {
    var root = this.pluralize(this.rootForType(type)),
        since = this.extractSince(json);

    this.sideload(store, type, json, root);
    store.loadMany(type, json[root]);

    // this registers the id with the store, so it will be passed
    // into the next call to `findAll`
    if (since) { store.sinceForType(type, since); }

    store.didUpdateAll(type);
  },

  findQuery: function(store, type, query, recordArray) {
    var root = this.rootForType(type);

    this.ajax(this.buildURL(root), "GET", {
      data: query,
      success: function(json) {
        this.didFindQuery(store, type, json, recordArray);
      }
    });
  },

  didFindQuery: function(store, type, json, recordArray) {
    var root = this.pluralize(this.rootForType(type));

    this.sideload(store, type, json, root);
    recordArray.load(json[root]);
  },

  findMany: function(store, type, ids) {
    var root = this.rootForType(type);
    ids = this.serializeIds(ids);

    this.ajax(this.buildURL(root), "GET", {
      data: {ids: ids},
      success: function(json) {
        this.didFindMany(store, type, json);
      }
    });
  },

  /**
    @private

    This method serializes a list of IDs using `serializeId`

    @returns {Array} an array of serialized IDs
  */
  serializeIds: function(ids) {
    var serializer = get(this, 'serializer');

    return Ember.EnumerableUtils.map(ids, function(id) {
      return serializer.serializeId(id);
    });
  },

  didFindMany: function(store, type, json) {
    var root = this.pluralize(this.rootForType(type));

    this.sideload(store, type, json, root);
    store.loadMany(type, json[root]);
  },

  didError: function(store, type, record, xhr) {
    if (xhr.status === 422) {
      var data = JSON.parse(xhr.responseText);
      store.recordWasInvalid(record, data['errors']);
    } else {
      store.recordWasError(record);
    }
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
      hash.data = JSON.stringify(hash.data);
    }

    jQuery.ajax(hash);
  },

  sideload: function(store, type, json, root) {
    var sideloadedType, mappings, loaded = {};

    loaded[root] = true;

    for (var prop in json) {
      if (!json.hasOwnProperty(prop)) { continue; }
      if (prop === root) { continue; }
      if (prop === get(this, 'meta')) { continue; }

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

  url: "",

  buildURL: function(record, suffix) {
    var url = [this.url];

    Ember.assert("Namespace URL (" + this.namespace + ") must not start with slash", !this.namespace || this.namespace.toString().charAt(0) !== "/");
    Ember.assert("Record URL (" + record + ") must not start with slash", !record || record.toString().charAt(0) !== "/");
    Ember.assert("URL suffix (" + suffix + ") must not start with slash", !suffix || suffix.toString().charAt(0) !== "/");

    if (this.namespace !== undefined) {
      url.push(this.namespace);
    }

    url.push(this.pluralize(record));
    if (suffix !== undefined) {
      url.push(suffix);
    }

    return url.join("/");
  },

  meta: 'meta',
  since: 'since',

  sinceQuery: function(since) {
    var query = {};
    query[get(this, 'since')] = since;
    return since ? query : null;
  },

  extractSince: function(json) {
    var meta = this.extractMeta(json);
    return meta[get(this, 'since')] || null;
  },

  extractMeta: function(json) {
    return json[get(this, 'meta')] || {};
  }
});


})();



(function() {

})();



(function() {
//Copyright (C) 2011 by Living Social, Inc.

//Permission is hereby granted, free of charge, to any person obtaining a copy of
//this software and associated documentation files (the "Software"), to deal in
//the Software without restriction, including without limitation the rights to
//use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
//of the Software, and to permit persons to whom the Software is furnished to do
//so, subject to the following conditions:

//The above copyright notice and this permission notice shall be included in all
//copies or substantial portions of the Software.

//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//SOFTWARE.

})();

