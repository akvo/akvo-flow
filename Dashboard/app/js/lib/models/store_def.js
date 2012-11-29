var host = "http://" + window.location.hostname;
FLOW.store = DS.Store.create({
	revision: 8,
	adapter:DS.FLOWRESTAdapter.create({bulkCommit:false, namespace:"rest", url:host})
	//adapter: DS.fixtureAdapter
});