FLOW.store = DS.Store.create({
	revision: 8,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"restlocal", url:"http://localhost"})
	//adapter: DS.fixtureAdapter
});