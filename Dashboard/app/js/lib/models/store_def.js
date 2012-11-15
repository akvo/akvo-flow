FLOW.store = DS.Store.create({
	revision: 7,
	//adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:"http://localhost"})
	adapter: DS.fixtureAdapter
});