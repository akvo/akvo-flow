FLOW.store = DS.Store.create({
	revision: 8,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:"http://akvo-flow.dev"})
	// adapter: DS.fixtureAdapter
});
