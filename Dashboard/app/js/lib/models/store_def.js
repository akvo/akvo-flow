FLOW.store = DS.Store.create({
	revision: 8,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:"http://33.33.33.6"})
	// adapter: DS.fixtureAdapter
});
